/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <gtest/gtest.h>

#include "osquery/core/test_util.h"
#include "osquery/sql/sqlite_util.h"
#include "osquery/dispatcher/scheduler.h"

namespace osquery {

extern SQL monitor(const std::string& name, const ScheduledQuery& query);

class SchedulerTests : public testing::Test {};

TEST_F(SchedulerTests, test_monitor) {
  std::string name = "pack_test_test_query";

  // This query has never run so it will not have a timestamp.
  std::string timestamp;
  getDatabaseValue(kPersistentSettings, "timestamp." + name, timestamp);
  ASSERT_TRUE(timestamp.empty());

  // Fill in a scheduled query and execute it via the query monitor wrapper.
  ScheduledQuery query;
  query.interval = 10;
  query.splayed_interval = 11;
  query.query = "select * from time";
  auto results = monitor(name, query);
  EXPECT_EQ(results.rows().size(), 1U);

  // Ask the config instance for the monitored performance.
  QueryPerformance perf;
  Config::getInstance().getPerformanceStats(
      name, ([&perf](const QueryPerformance& r) { perf = r; }));
  // Make sure it was recorded query ran.
  // There is no pack for this query within the config, that is fine as these
  // performance stats are tracked independently.
  EXPECT_EQ(perf.executions, 1U);
  EXPECT_GT(perf.output_size, 0U);

  // A bit more testing, potentially redundant, check the database results.
  // Since we are only monitoring, no 'actual' results are stored.
  std::string content;
  getDatabaseValue(kQueries, name, content);
  EXPECT_TRUE(content.empty());

  // Finally, make sure there is a recorded timestamp for the execution.
  // We are not concerned with the APPROX value, only that it was recorded.
  getDatabaseValue(kPersistentSettings, "timestamp." + name, timestamp);
  EXPECT_FALSE(timestamp.empty());
}

TEST_F(SchedulerTests, test_config_results_purge) {
  // Set a query time for now (time is only important relative to a week ago).
  auto query_time = osquery::getUnixTime();
  setDatabaseValue(
      kPersistentSettings, "timestamp.test_query", std::to_string(query_time));
  // Store a meaningless saved query interval splay.
  setDatabaseValue(kPersistentSettings, "interval.test_query", "11");
  // Store meaningless query differential results.
  setDatabaseValue(kQueries, "test_query", "{}");

  // We do not need "THE" config instance.
  // We only need to trigger a 'purge' event, this occurs when configuration
  // content is updated by a plugin or on load.
  Config::getInstance().purge();

  // Nothing should have been purged.
  {
    std::string content;
    getDatabaseValue(kPersistentSettings, "timestamp.test_query", content);
    EXPECT_FALSE(content.empty());
  }

  {
    std::string content;
    getDatabaseValue(kPersistentSettings, "interval.test_query", content);
    EXPECT_FALSE(content.empty());
  }

  {
    std::string content;
    getDatabaseValue(kQueries, "test_query", content);
    EXPECT_FALSE(content.empty());
  }

  // Update the timestamp to have run a week and a day ago.
  query_time -= (84600 * (7 + 1));
  setDatabaseValue(
      kPersistentSettings, "timestamp.test_query", std::to_string(query_time));

  // Trigger another purge.
  Config::getInstance().purge();
  // Now ALL 'test_query' related storage will have been purged.
  {
    std::string content;
    getDatabaseValue(kPersistentSettings, "timestamp.test_query", content);
    EXPECT_TRUE(content.empty());
  }

  {
    std::string content;
    getDatabaseValue(kPersistentSettings, "interval.test_query", content);
    EXPECT_TRUE(content.empty());
  }

  {
    std::string content;
    getDatabaseValue(kQueries, "test_query", content);
    EXPECT_TRUE(content.empty());
  }
}
}
