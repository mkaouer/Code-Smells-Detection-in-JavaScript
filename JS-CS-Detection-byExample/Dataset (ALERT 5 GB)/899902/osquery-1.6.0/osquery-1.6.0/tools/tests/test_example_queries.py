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

import json
import os
import shutil
import time
import unittest
import sys

# osquery-specific testing utils
import test_base
import utils

class ExampleQueryTests(test_base.QueryTester):
    def test_cross_platform_queries(self):
        self._execute_set(PLATFORM_EXAMPLES["specs"])

    def test_platform_specific_queries(self):
        self._execute_set(PLATFORM_EXAMPLES[utils.platform()])

    def test_utility_queries(self):
        self._execute_set(PLATFORM_EXAMPLES["utility"])

if __name__ == '__main__':
    # Import the API generation code for example query introspection.
    SCRIPT_DIR = os.path.dirname(os.path.realpath(__file__))
    SOURCE_DIR = os.path.abspath(SCRIPT_DIR + "/../../")
    sys.path.append(SOURCE_DIR + "/tools/codegen")
    from genapi import gen_api
    API = gen_api(SOURCE_DIR + "/specs")

    # Organize example queries by platform
    PLATFORM_EXAMPLES = {}
    for category in API:
        PLATFORM_EXAMPLES[category["key"]] = []
        for table in category["tables"]:
            PLATFORM_EXAMPLES[category["key"]] += table["examples"]

    module = test_base.Tester()

    # Find and import the thrift-generated python interface
    test_base.loadThriftFromBuild(test_base.ARGS.build)

    module.run()
