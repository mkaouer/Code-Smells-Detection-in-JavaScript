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
from __future__ import unicode_literals

import flaky
import os
import signal
import shutil
import time
import unittest

# osquery-specific testing utils
import test_base

class DaemonTests(test_base.ProcessGenerator, unittest.TestCase):
    def test_1_daemon_without_watchdog(self):
        daemon = self._run_daemon({
            "disable_watchdog": True,
            "disable_extensions": True,
        })
        self.assertTrue(daemon.isAlive())
        daemon.kill()

    def test_2_daemon_with_option(self):
        logger_path = os.path.join(test_base.CONFIG_DIR, "logger-tests")
        os.makedirs(logger_path)
        daemon = self._run_daemon({
            "disable_watchdog": True,
            "disable_extensions": True,
            "disable_logging": False,
        },
        options_only={
            "logger_path": logger_path,
            "verbose": True,
        })
        info_path = os.path.join(logger_path, "osqueryd.INFO")
        self.assertTrue(daemon.isAlive())

        def info_exists():
            return os.path.exists(info_path)
        # Wait for the daemon to flush to GLOG.
        test_base.expectTrue(info_exists)
        self.assertTrue(os.path.exists(info_path))
        daemon.kill()
    
    @flaky.flaky(max_runs=3)
    def test_3_daemon_with_watchdog(self):
        daemon = self._run_daemon({
            "disable_watchdog": False,
        })
        self.assertTrue(daemon.isAlive())

        # Check that the daemon spawned a child process
        children = daemon.getChildren()
        self.assertTrue(len(children) > 0)
        daemon.kill()

        # This will take a few moments to make sure the client process
        # dies when the watcher goes away
        self.assertTrue(daemon.isDead(children[0]))

    def test_4_daemon_sighup(self):
        # A hangup signal should not do anything to the daemon.
        daemon = self._run_daemon({
            "disable_watchdog": True,
        })
        self.assertTrue(daemon.isAlive())

        # Send a SIGHUP
        os.kill(daemon.proc.pid, signal.SIGHUP)
        self.assertTrue(daemon.isAlive())

    def test_5_daemon_sigint(self):
        # An interrupt signal will cause the daemon to stop.
        daemon = self._run_daemon({
            "disable_watchdog": True,
        })
        self.assertTrue(daemon.isAlive())

        # Send a SIGINT
        os.kill(daemon.pid, signal.SIGINT)
        self.assertTrue(daemon.isDead(daemon.pid, 10))

        acceptable_retcodes = [-1, -2, -1 * signal.SIGINT]
        self.assertTrue(daemon.retcode in acceptable_retcodes)

if __name__ == '__main__':
    test_base.Tester().run()
