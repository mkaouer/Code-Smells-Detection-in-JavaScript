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

import argparse
import json
import os
import signal
import ssl
import sys

# Create a simple TLS/HTTP server.
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
from urlparse import parse_qs

EXAMPLE_CONFIG = {
    "schedule": {
        "tls_proc": {"query": "select * from processes", "interval": 0},
    },
    "node_invalid": False,
}

EXAMPLE_DISTRIBUTED = {
    "queries": {
        "info": "select * from osquery_info",
        "flags": "select * from osquery_flags",
    }
}

TEST_RESPONSE = {
    "foo": "bar",
}

NODE_KEYS = [
    "this_is_a_node_secret",
    "this_is_also_a_node_secret",
]

FAILED_ENROLL_RESPONSE = {
    "node_invalid": True
}

ENROLL_RESPONSE = {
    "node_key": "this_is_a_node_secret"
}


def debug(response):
    print("-- [DEBUG] %s" % str(response))


ENROLL_RESET = {
    "count": 1,
    "max": 3,
}

class RealSimpleHandler(BaseHTTPRequestHandler):
    def _set_headers(self):
        self.send_response(200)
        self.send_header('Content-type', 'application/json')
        self.end_headers()

    def do_GET(self):
        debug("RealSimpleHandler::get %s" % self.path)
        self._set_headers()
        self._reply(TEST_RESPONSE)

    def do_HEAD(self):
        debug("RealSimpleHandler::head %s" % self.path)
        self._set_headers()

    def do_POST(self):
        debug("RealSimpleHandler::post %s" % self.path)
        self._set_headers()
        content_len = int(self.headers.getheader('content-length', 0))
        request = json.loads(self.rfile.read(content_len))
        debug("Request: %s" % str(request))

        if self.path == '/enroll':
            self.enroll(request)
        elif self.path == '/config':
            self.config(request)
        elif self.path == '/log':
            self.log(request)
        elif self.path == '/distributed_read':
            self.distributed_read(request)
        elif self.path == '/distributed_write':
            self.distributed_write(request)
        else:
            self._reply(TEST_RESPONSE)

    def enroll(self, request):
        '''A basic enrollment endpoint'''

        # This endpoint expects an "enroll_secret" POST body variable.
        # Over TLS, this string may be a shared secret value installed on every
        # managed host in an enterprise.

        # Alternatively, each client could authenticate with a TLS client cert.
        # Then, access to the enrollment endpoint implies the required auth.
        # A generated node_key is still supplied for identification.
        if ARGS.use_enroll_secret and ENROLL_SECRET != request["enroll_secret"]:
            self._reply(FAILED_ENROLL_RESPONSE)
            return
        self._reply(ENROLL_RESPONSE)

    def config(self, request):
        '''A basic config endpoint'''

        # This endpoint responds with a JSON body that is the entire config
        # content. There is no special key or status.

        # Authorization is simple authentication (the ability to download the
        # config data) using a "valid" node_key. Validity means the node_key is
        # known to this server. This toy server delivers a shared node_key,
        # imagine generating a unique node_key per enroll request, tracking the
        # generated keys, and asserting a match.

        # The osquery TLS config plugin calls the TLS enroll plugin to retrieve
        # a node_key, then submits that key alongside config/logger requests.
        if "node_key" not in request or request["node_key"] not in NODE_KEYS:
            self._reply(FAILED_ENROLL_RESPONSE)
            return

        # This endpoint will also invalidate the node secret key (node_key)
        # after several attempts to test re-enrollment.
        ENROLL_RESET["count"] += 1
        if ENROLL_RESET["count"] % ENROLL_RESET["max"] == 0:
            ENROLL_RESET["first"] = 0
            self._reply(FAILED_ENROLL_RESPONSE)
            return
        self._reply(EXAMPLE_CONFIG)

    def distributed_read(self, request):
        '''A basic distributed read endpoint'''
        if "node_key" not in request or request["node_key"] not in NODE_KEYS:
            self._reply(FAILED_ENROLL_RESPONSE)
            return
        self._reply(EXAMPLE_DISTRIBUTED)

    def distributed_write(self, request):
        '''A basic distributed write endpoint'''
        self._reply({})

    def log(self, request):
        self._reply({})

    def _reply(self, response):
        debug("Replying: %s" % (str(response)))
        self.wfile.write(json.dumps(response))


def handler(signum, frame):
    print("[DEBUG] Shutting down HTTP server via timeout (%d) seconds."
          % (ARGS.timeout))
    sys.exit(0)

if __name__ == '__main__':
    SCRIPT_DIR = os.path.dirname(os.path.realpath(__file__))
    parser = argparse.ArgumentParser(description=(
        "osquery python https server for client TLS testing."
    ))
    parser.add_argument(
        "--tls", default=False, action="store_true",
        help="Wrap the HTTP server socket in TLS."
    )

    parser.add_argument(
        "--persist", default=False, action="store_true",
        help="Wrap the HTTP server socket in TLS."
    )
    parser.add_argument(
        "--timeout", default=10, type=int,
        help="If not persisting, exit after a number of seconds"
    )

    parser.add_argument(
        "--cert", metavar="CERT_FILE",
        default=SCRIPT_DIR + "/test_server.pem",
        help="TLS server cert."
    )
    parser.add_argument(
        "--key", metavar="PRIVATE_KEY_FILE",
        default=SCRIPT_DIR + "/test_server.key",
        help="TLS server cert private key."
    )
    parser.add_argument(
        "--ca", metavar="CA_FILE",
        default=SCRIPT_DIR + "/test_server_ca.pem",
        help="TLS server CA list for client-auth."
    )

    parser.add_argument(
        "--use_enroll_secret", action="store_true",
        default=True,
        help="Require an enrollment secret for node enrollment"
    )
    parser.add_argument(
        "--enroll_secret", metavar="SECRET_FILE",
        default=SCRIPT_DIR + "/test_enroll_secret.txt",
        help="File containing enrollment secret"
    )

    parser.add_argument(
        "port", metavar="PORT", type=int,
        help="Bind to which local TCP port."
    )

    ARGS = parser.parse_args()

    ENROLL_SECRET = ""
    if ARGS.use_enroll_secret:
        try:
            with open(ARGS.enroll_secret, "r") as fh:
                ENROLL_SECRET = fh.read().strip()
        except IOError as e:
            print("Cannot read --enroll_secret: %s" % str(e))
            exit(1)

    if not ARGS.persist:
        signal.signal(signal.SIGALRM, handler)
        signal.alarm(ARGS.timeout)

    httpd = HTTPServer(('localhost', ARGS.port), RealSimpleHandler)
    if ARGS.tls:
        if 'SSLContext' in vars(ssl):
            ctx = ssl.SSLContext(ssl.PROTOCOL_SSLv23)
            ctx.load_cert_chain(ARGS.cert, keyfile=ARGS.key)
            ctx.load_verify_locations(capath=ARGS.ca)
            ctx.options ^= ssl.OP_NO_SSLv2 | ssl.OP_NO_SSLv3
            httpd.socket = ctx.wrap_socket(httpd.socket, server_side=True)
        else:
            httpd.socket = ssl.wrap_socket(httpd.socket,
                                           ca_certs=ARGS.ca,
                                           ssl_version=ssl.PROTOCOL_SSLv23,
                                           certfile=ARGS.cert,
                                           keyfile=ARGS.key,
                                           server_side=True)
        debug("Starting TLS/HTTPS server on TCP port: %d" % ARGS.port)
    else:
        debug("Starting HTTP server on TCP port: %d" % ARGS.port)
    httpd.serve_forever()
