#!/usr/bin/env python

#  Copyright (c) 2014, Facebook, Inc.
#  All rights reserved.
#
#  This source code is licensed under the BSD-style license found in the
#  LICENSE file in the root directory of this source tree. An additional grant
#  of patent rights can be found in the PATENTS file in the same directory.

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
# pyexpect.replwrap will not work with unicode_literals
# from __future__ import unicode_literals

import copy
import os
import psutil
import random
import re
import shutil
import signal
import subprocess
import sys
import time
import threading
import unittest
import utils
import pexpect

try:
    from pexpect.replwrap import REPLWrapper
except ImportError as e:
    print("Could not import pexpect.replwrap: %s" % (str(e)))
    print("  Need pexpect version 3.3, installed version: %s" % (
        str(pexpect.__version__)))
    print("  pexpect location: %s" % (str(pexpect.__file__)))
    exit(1)

try:
    import argparse
except ImportError:
    print("Cannot import argparse: pip install argparse?")
    exit(1)

try:
    from thrift import Thrift
    from thrift.transport import TSocket
    from thrift.transport import TTransport
    from thrift.protocol import TBinaryProtocol
except ImportError:
    print("Cannot import thrift: pip install thrift?")
    exit(1)

'''Defaults that should be used in integration tests.'''
SCRIPT_DIR = os.path.dirname(os.path.realpath(__file__))
CONFIG_DIR = "/tmp/osquery-tests/"
CONFIG_NAME = CONFIG_DIR + "tests"
DEFAULT_CONFIG = {
    "options": {
        "database_path": "%s.db" % CONFIG_NAME,
        "pidfile": "%s.pid" % CONFIG_NAME,
        "config_path": "%s.conf" % CONFIG_NAME,
        "extensions_socket": "%s.em" % CONFIG_NAME,
        "extensions_interval": "1",
        "extensions_timeout": "0",
        "watchdog_level": "3",
        "disable_logging": "true",
        "disable_events": "true",
        "force": "true",
    },
    "schedule": {},
}

'''Expect CONFIG to be set during Tester.main() to a python dict.'''
CONFIG = None
'''Expect ARGS to contain the argparsed namespace.'''
ARGS = None


class OsqueryUnknownException(Exception):
    '''Exception thrown for unknown output from the shell'''
    pass


class OsqueryException(Exception):
    '''Exception thrown when the shell returns an error'''
    pass


class OsqueryWrapper(REPLWrapper):
    '''A pexpect wrapper intended for interacting with the osqueryi REPL'''
    PROMPT = u'osquery> '
    CONTINUATION_PROMPT = u'    ...> '
    ERROR_PREFIX = 'Error:'

    def __init__(self, command='../osqueryi', args={}, env={}):
        global CONFIG_NAME, CONFIG
        options = copy.deepcopy(CONFIG)["options"]
        for option in args.keys():
            options[option] = args[option]
        options["database_path"] += str(random.randint(1000, 9999))
        command = command + " " + " ".join(["--%s=%s" % (k, v) for
                                            k, v in options.iteritems()])
        proc = pexpect.spawn(command, env=env)
        super(OsqueryWrapper, self).__init__(
            proc,
            self.PROMPT,
            None,
            continuation_prompt=self.CONTINUATION_PROMPT)

    def run_query(self, query):
        '''Run a query, returning the results as a list of dictionaries

        When unknown output is encountered, OsqueryUnknownException is thrown.
        When osqueryi returns an error, OsqueryException is thrown.
        '''
        query = query + ';'  # Extra semicolon causes no harm
        result = self.run_command(query)
        # On Mac, the query appears first in the string. Remove it if so.
        result = re.sub(re.escape(query), '', result).strip()
        result_lines = result.splitlines()

        if len(result_lines) < 1:
            raise OsqueryUnknownException(
                'Unexpected output:\n %s' % result_lines)
        if result_lines[0].startswith(self.ERROR_PREFIX):
            raise OsqueryException(result_lines[0])

        try:
            header = result_lines[1]
            columns = re.findall('[^ |]+', header)
            rows = []
            for line in result_lines[3:-1]:
                values = re.findall('[^ |]+', line)
                rows.append(
                    dict((col, val) for col, val in zip(columns, values)))
            return rows
        except:
            raise OsqueryUnknownException(
                'Unexpected output:\n %s' % result_lines)


class ProcRunner(object):
    '''A helper class to open a subprocess and perform testing actions.

    The subprocess is opened in a new thread and state is tracked using
    this class wrapper.
    '''

    def __init__(self, name, path, _args=[], interval=0.02, silent=False):
        self.started = False
        self.proc = None
        self.name = name
        self.path = path
        self.args = _args
        self.interval = interval
        self.silent = silent
        self.retcode = -1
        thread = threading.Thread(target=self.run, args=())
        thread.daemon = True
        thread.start()

    def run(self):
        pid = 0
        try:
            if self.silent:
                self.proc = subprocess.Popen([self.path] + self.args,
                                             stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            else:
                self.proc = subprocess.Popen([self.path] + self.args)
            pid = self.proc.pid
            self.started = True
        except Exception as e:
            print(utils.red("Process start failed:") + " %s" % self.name)
            print(str(e))
            sys.exit(1)
        try:
            while self.proc.poll() is None:
                self.started = True
                time.sleep(self.interval)
            self.started = True
            self.retcode = -1 if self.proc is None else self.proc.poll()
            self.proc = None
        except Exception as e:
            return

    def requireStarted(self, timeout=2):
        delay = 0
        while delay < timeout:
            if self.started is True:
                break
            time.sleep(self.interval * 10)
            delay += self.interval * 10

    def getChildren(self, timeout=1):
        '''Get the child pids.'''
        self.requireStarted()
        if not self.proc:
            return []
        try:
            proc = psutil.Process(pid=self.proc.pid)
            delay = 0
            while len(proc.children()) == 0:
                if delay > timeout:
                    return []
                time.sleep(self.interval)
                delay += self.interval
            return [p.pid for p in proc.children()]
        except:
            pass
        return []

    @property
    def code(self):
        self.requireStarted()
        return self.retcode

    @property
    def pid(self):
        self.requireStarted()
        return self.proc.pid if self.proc is not None else None

    def kill(self, children=False):
        self.requireStarted()
        if children:
            for child in self.getChildren():
                try:
                    os.kill(child, 9)
                except:
                    pass
        if self.proc:
            try:
                os.kill(self.pid, 9)
            except:
                pass
        self.proc = None

    def isAlive(self, timeout=3):
        self.requireStarted()
        '''Check if the process is alive.'''
        delay = 0
        while self.proc is None:
            if delay > timeout:
                break
            time.sleep(self.interval)
            delay += self.interval
        if self.proc is None:
            return False
        return self.proc.poll() is None

    def isDead(self, pid, timeout=5):
        self.requireStarted()
        '''Check if the process was killed.

        This is different than `isAlive` in that the timeout is an expectation
        that the process will die before the timeout, `isAlive`'s timeout is
        an expectation that the process will be scheduled before the timeout.
        '''
        try:
            proc = psutil.Process(pid=pid)
        except psutil.NoSuchProcess as e:
            return True
        delay = 0
        while delay < timeout:
            if not proc.is_running():
                return True
            time.sleep(self.interval)
            delay += self.interval
        return False


class ProcessGenerator(object):
    '''Helper methods to patch into a unittest'''
    generators = []

    def setUp(self):
        shutil.rmtree(CONFIG_DIR)
        os.makedirs(CONFIG_DIR)

    def _run_daemon(self, options={}, silent=False, options_only={},
                    overwrite={}):
        '''Spawn an osquery daemon process'''
        global ARGS, CONFIG_NAME, CONFIG
        config = copy.deepcopy(CONFIG)
        config["options"]["database_path"] += str(random.randint(1000, 9999))
        config["options"][
            "extensions_socket"] += str(random.randint(1000, 9999))
        for option in options.keys():
            config["options"][option] = options[option]
        flags = ["--%s=%s" % (k, v) for k, v in config["options"].items()]
        for option in options_only.keys():
            config["options"][option] = options_only[option]
        for key in overwrite:
            config[key] = overwrite[key]
        utils.write_config(config)
        binary = os.path.join(ARGS.build, "osquery", "osqueryd")

        daemon = ProcRunner("daemon", binary, flags, silent=silent)
        daemon.options = config["options"]
        self.generators.append(daemon)
        return daemon

    def _run_extension(self, timeout=0, path=None, silent=False):
        '''Spawn an osquery extension (example_extension)'''
        global ARGS, CONFIG
        config = copy.deepcopy(CONFIG)
        config["options"][
            "extensions_socket"] += str(random.randint(1000, 9999))
        binary = os.path.join(ARGS.build, "osquery", "example_extension.ext")
        if path is not None:
            config["options"]["extensions_socket"] = path
        extension = ProcRunner("extension",
                               binary,
                               [
                                   "--socket=%s" % config["options"][
                                       "extensions_socket"],
                                   "--verbose" if not silent else "",
                                   "--timeout=%d" % timeout,
                                   "--interval=%d" % 0,
                               ],
                               silent=silent)
        self.generators.append(extension)
        extension.options = config["options"]
        return extension

    def tearDown(self):
        '''When the unit test stops, clean up child-generated processes.

        Iterate through the generated daemons and extensions, and kill -9 them.
        Unittest should stop processes they generate, but on failure the
        tearDown method will cleanup.
        '''
        for generator in self.generators:
            if generator.pid is not None:
                try:
                    os.kill(generator.pid, signal.SIGKILL)
                except Exception as e:
                    pass


class EXClient(object):
    '''An osquery Thrift/extensions python client generator.'''
    transport = None
    '''The instance transport object.'''
    _manager = None
    '''The client class's reference to run-time discovered manager.'''
    _client = None
    '''The client class's reference to run-time discovered client.'''

    def __init__(self, path=None, uuid=None):
        global CONFIG
        '''Create a extensions client to a UNIX path and optional UUID.'''
        if path is None:
            path = CONFIG["options"]["extensions_socket"]
        self.path = path
        if uuid:
            self.path += ".%s" % str(uuid)
        transport = TSocket.TSocket(unix_socket=self.path)
        transport = TTransport.TBufferedTransport(transport)
        self.protocol = TBinaryProtocol.TBinaryProtocol(transport)
        self.transport = transport

    @classmethod
    def setUp(cls, manager, client):
        '''Set the manager and client modules to generate clients from.'''
        cls._manager = manager
        cls._client = client

    def close(self):
        if self.transport:
            self.transport.close()

    def open(self, timeout=0.1, interval=0.01):
        '''Attempt to open the UNIX domain socket.'''
        delay = 0
        while delay < timeout:
            try:
                self.transport.open()
                return True
            except Exception as e:
                pass
            delay += interval
            time.sleep(interval)
        return False

    def getEM(self):
        '''Return an extension manager (osquery core) client.'''
        if self._manager is None:
            raise(Exception, "The EXClient must be 'setUp' with a manager")
        return self._manager.Client(self.protocol)

    def getEX(self):
        '''Return an extension (osquery extension) client.'''
        if self._client is None:
            raise(Exception, "The EXClient must be 'setUp' with a client")
        return self._client.Client(self.protocol)


class Autoloader(object):
    '''Helper class to write a module or extension autoload file.'''

    def __init__(self, autoloads=[]):
        global CONFIG_DIR
        self.path = CONFIG_DIR + "ext.load" + str(random.randint(1000, 9999))
        with open(self.path, "w") as fh:
            fh.write("\n".join(autoloads))

    def __del__(self):
        try:
            os.unlink(self.path)
        except:
            pass


class TimeoutRunner(object):

    def __init__(self, cmd=[], timeout_sec=1):
        self.stdout = None
        self.stderr = None
        self.proc = subprocess.Popen(" ".join(cmd),
                                     shell=True,
                                     stdout=subprocess.PIPE,
                                     stderr=subprocess.PIPE)
        kill_proc = lambda p: p.kill()
        timer = threading.Timer(timeout_sec, kill_proc, [self.proc])
        timer.start()
        self.stdout, self.stderr = self.proc.communicate()
        timer.cancel()

def flaky(gen):
    exceptions = []
    def attempt(this):
        try:
            worked = gen(this)
            return True
        except Exception as e:
            exc_type, exc_obj, exc_tb = sys.exc_info()
            fname = os.path.split(exc_tb.tb_frame.f_code.co_filename)[1]
            exceptions.append((e, fname, exc_tb.tb_lineno))
        return False
    def wrapper(this):
        for i in range(3):
            if attempt(this):
                return True
        i = 1
        for exc in exceptions:
            print("Test (attempt %d) %s::%s failed: %s (%s:%d)" % (
                i,
                this.__class__.__name__,
                gen.__name__,  str(exc[0]), exc[1], exc[2]))
            i += 1
        raise exceptions[0][0]
    return wrapper

class Tester(object):

    def __init__(self):
        global ARGS, CONFIG, CONFIG_DIR
        parser = argparse.ArgumentParser(description=(
            "osquery python integration testing."
        ))
        parser.add_argument(
            "--config", metavar="FILE", default=None,
            help="Use special options from a config."
        )
        parser.add_argument(
            "--verbose", default=False, action="store_true",
            help="Run daemons and extensions with --verbose"
        )

        # Directory structure options
        parser.add_argument(
            "--build", metavar="PATH", default=".",
            help="Path to osquery build (./build/<sys>/)."
        )
        ARGS = parser.parse_args()

        if not os.path.exists(ARGS.build):
            print("Cannot find --build: %s" % ARGS.build)
            print("You must first run: make")
            exit(1)

        # Write config
        random.seed(time.time())

        try:
            shutil.rmtree(CONFIG_DIR)
        except:
            # Allow the tester to fail
            pass
        os.makedirs(CONFIG_DIR)
        CONFIG = read_config(ARGS.config) if ARGS.config else DEFAULT_CONFIG

    def run(self):
        os.setpgrp()
        unittest_args = [sys.argv[0]]
        if ARGS.verbose:
            unittest_args += ["-v"]
        unittest.main(argv=unittest_args)


def expect(functional, expected, interval=0.01, timeout=4):
    """Helper function to run a function with expected latency"""
    delay = 0
    result = None
    while result is None or len(result) != expected:
        try:
            result = functional()
            if len(result) == expected:
                break
        except Exception as e:
            print("Expect exception (%s): %s not %s" % (
                str(e), str(functional), expected))
            return None
        if delay >= timeout:
            return None
        time.sleep(interval)
        delay += interval
    return result


class QueryTester(ProcessGenerator, unittest.TestCase):

    def setUp(self):
        self.binary = os.path.join(ARGS.build, "osquery", "osqueryi")
        self.daemon = self._run_daemon({
            # The set of queries will hammer the daemon process.
            "disable_watchdog": True,
            # Enable the 'hidden' flag "registry_exceptions" to prevent
            # catching.
            "registry_exceptions": True,
        })
        self.assertTrue(self.daemon.isAlive())

        # The sets of example tests will use the extensions APIs.
        self.client = EXClient(self.daemon.options["extensions_socket"])
        expectTrue(self.client.open)
        self.assertTrue(self.client.open())
        self.em = self.client.getEM()

    def tearDown(self):
        self.client.close()
        self.daemon.kill()

    def _execute(self, query):
        try:
            result = self.em.query(query)
            self.assertEqual(result.status.code, 0)
            return result.response
        except Exception as e:
            print("General exception executing query: %s" % (
                utils.lightred(query)))
            raise e

    def _execute_set(self, queries):
        for example in queries:
            start_time = time.time()
            result = self._execute(example)
            end_time = time.time()
            duration_ms = int((end_time - start_time) * 1000)
            if duration_ms > 2000:
                # Query took longer than 2 seconds.
                duration_ms = utils.lightred(duration_ms)
            print("Query (%sms): %s, rows: %d" % (
                duration_ms, example, len(result)))


def expectTrue(functional, interval=0.01, timeout=8):
    """Helper function to run a function with expected latency"""
    delay = 0
    while delay < timeout:
        if functional():
            return True
        time.sleep(interval)
        delay += interval
    return False


def assertPermissions():
    stat_info = os.stat('.')
    if stat_info.st_uid != os.getuid():
        print(utils.lightred("Will not load modules/extensions in tests."))
        print(utils.lightred("Repository owner (%d) executer (%d) mismatch" % (
            stat_info.st_uid, os.getuid())))
        exit(1)


def loadThriftFromBuild(build_dir):
    '''Find and import the thrift-generated python interface.'''
    thrift_path = build_dir + "/generated/gen-py"
    try:
        sys.path = [thrift_path, thrift_path + "/osquery"] + sys.path
        from osquery import ExtensionManager, Extension
        EXClient.setUp(ExtensionManager, Extension)
    except ImportError as e:
        print("Cannot import osquery thrift API from %s" % (thrift_path))
        print("Exception: %s" % (str(e)))
        print("You must first run: make")
        exit(1)
