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

import os
import shutil
import time
import unittest

# osquery-specific testing utils
import test_base
import utils


class AdditionalFeatureTests(test_base.ProcessGenerator, unittest.TestCase):
    @test_base.flaky
    def test_query_packs(self):
        query_pack_path = test_base.CONFIG_DIR + "/test_pack.conf"
        utils.write_config({
            "queries": {
                "simple_test": {
                    "query": "select * from time",
                    "interval": 60,
                },
                "simple_test2": {
                    "query": "select * from time",
                    "interval": 60,
                    "platform": "does_not_exist",
                }
            }
        }, path=query_pack_path)

        # Get a daemon process, loaded with the default test configuration.
        # We'll add a config override (overwrite) for the "packs" key.
        # THis will point a single pack at the config written above.
        daemon = self._run_daemon({
            "disable_watchdog": True,
            },
            overwrite={
            "packs": {
                "test_pack": query_pack_path
            },
        })
        self.assertTrue(daemon.isAlive())

        # Introspect into the daemon's query packs.
        client = test_base.EXClient(daemon.options["extensions_socket"])
        test_base.expectTrue(client.open)
        self.assertTrue(client.open())
        em = client.getEM()

        # Every query from the pack(s) is added to the packs table.
        def get_packs():
            result = em.query("select * from osquery_packs")
            return len(result.response) == 2
        # Allow the daemon some lag to parse the pack content.
        test_base.expectTrue(get_packs)
        result = em.query("select * from osquery_packs")
        self.assertEqual(len(result.response), 2)

        # Only the applicable queries are added to the schedule.
        # There will be len(pack_queries) - 1 since "simple_test2" is bound
        # to an unknown/non-existing platform.
        result = em.query("select * from osquery_schedule")
        self.assertEqual(len(result.response), 1)
        daemon.kill()

if __name__ == '__main__':
    module = test_base.Tester()

    # Find and import the thrift-generated python interface
    test_base.loadThriftFromBuild(test_base.ARGS.build)

    module.run()
