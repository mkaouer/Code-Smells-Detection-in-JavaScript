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

import json
import os
import subprocess
import unittest

# osquery-specific testing utils
import test_base
from utils import platform


def allowed_platform(qp):
    if qp in ["all", "any"]:
        return True
    if len(qp) == 0:
        return True
    return qp.find(platform()) >= 0


class ReleaseTests(test_base.QueryTester):
    def test_pack_queries(self):
        packs = {}
        PACKS_DIR = SOURCE_DIR + "/packs"
        for root, dirs, files in os.walk(PACKS_DIR):
            for name in files:
                with open(os.path.join(PACKS_DIR, name), 'r') as fh:
                    packs[name] = json.loads(fh.read())
        for name, pack in packs.items():
            if "queries" not in pack:
                continue
            if "platform" in pack and not allowed_platform(pack["platform"]):
                continue
            queries = []
            for query_name, query in pack["queries"].items():
                qp = query["platform"] if "platform" in query else ""
                if allowed_platform(qp):
                    queries.append(query["query"])
            self._execute_set(queries)

    def test_no_avx_instructions(self):
        if platform() == "darwin":
            tool = "otool -tV"
        else:
            tool = "objdump -d"
        proc = subprocess.call(
            "%s %s | grep vxorps" % (tool, self.binary), shell=True)
        # Require no AVX instructions
        self.assertEqual(proc, 1)

    def test_no_local_link(self):
        if platform() == "darwin":
            tool = "otool -L"
        else:
            tool = "ldd"
        proc = subprocess.call(
            "%s %s | grep /usr/local/" % (tool, self.binary), shell=True)
        # Require no local dynamic dependent links.
        self.assertEqual(proc, 1)

if __name__ == '__main__':
    SCRIPT_DIR = os.path.dirname(os.path.realpath(__file__))
    SOURCE_DIR = os.path.abspath(SCRIPT_DIR + "/../../")

    module = test_base.Tester()
    # Find and import the thrift-generated python interface
    test_base.loadThriftFromBuild(test_base.ARGS.build)
    module.run()
