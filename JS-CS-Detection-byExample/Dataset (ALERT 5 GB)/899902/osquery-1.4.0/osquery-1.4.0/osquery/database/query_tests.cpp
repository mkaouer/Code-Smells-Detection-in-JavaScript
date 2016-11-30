/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant 
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <algorithm>
#include <ctime>
#include <deque>

#include <boost/filesystem/operations.hpp>

#include <gtest/gtest.h>

#include <osquery/database/query.h>

#include "osquery/core/test_util.h"

const std::string kTestingQueryDBPath = "/tmp/rocksdb-osquery-querytests";

namespace osquery {

class QueryTests : public testing::Test {
 public:
  void SetUp() { db = DBHandle::getInstanceAtPath(kTestingQueryDBPath); }

 public:
  std::shared_ptr<DBHandle> db;
};

TEST_F(QueryTests, test_get_column_family_name) {
  auto query = getOsqueryScheduledQuery();
  auto cf = Query(query);
  EXPECT_EQ(cf.getQueryName(), query.name);
}

TEST_F(QueryTests, test_get_query) {
  auto query = getOsqueryScheduledQuery();
  auto cf = Query(query);
  EXPECT_EQ(cf.getQuery(), query.query);
}

TEST_F(QueryTests, test_get_interval) {
  auto query = getOsqueryScheduledQuery();
  auto cf = Query(query);
  EXPECT_EQ(cf.getInterval(), query.interval);
}

TEST_F(QueryTests, test_private_members) {
  auto query = getOsqueryScheduledQuery();
  auto cf = Query(query);
  EXPECT_EQ(cf.query_, query);
}

TEST_F(QueryTests, test_add_and_get_current_results) {
  auto query = getOsqueryScheduledQuery();
  auto cf = Query(query);
  auto s = cf.addNewResults(getTestDBExpectedResults(), std::time(0), db);
  EXPECT_TRUE(s.ok());
  EXPECT_EQ(s.toString(), "OK");
  for (auto result : getTestDBResultStream()) {
    DiffResults dr;
    HistoricalQueryResults hQR;
    auto hqr_status = cf.getHistoricalQueryResults(hQR, db);
    EXPECT_TRUE(hqr_status.ok());
    EXPECT_EQ(hqr_status.toString(), "OK");
    auto s = cf.addNewResults(result.second, dr, true, std::time(0), db);
    EXPECT_TRUE(s.ok());
    DiffResults expected = diff(hQR.mostRecentResults.second, result.second);
    EXPECT_EQ(dr, expected);
    QueryData qd;
    cf.getCurrentResults(qd, db);
    EXPECT_EQ(qd, result.second);
  }
}

TEST_F(QueryTests, test_get_historical_query_results) {
  auto hQR = getSerializedHistoricalQueryResultsJSON();
  auto query = getOsqueryScheduledQuery();
  auto put_status = db->Put(kQueries, query.name, hQR.first);
  EXPECT_TRUE(put_status.ok());
  EXPECT_EQ(put_status.toString(), "OK");
  auto cf = Query(query);
  HistoricalQueryResults from_db;
  auto query_status = cf.getHistoricalQueryResults(from_db, db);
  EXPECT_TRUE(query_status.ok());
  EXPECT_EQ(query_status.toString(), "OK");
  EXPECT_EQ(from_db, hQR.second);
}

TEST_F(QueryTests, test_query_name_not_found_in_db) {
  HistoricalQueryResults from_db;
  auto query = getOsqueryScheduledQuery();
  query.name = "not_a_real_query";
  auto cf = Query(query);
  auto query_status = cf.getHistoricalQueryResults(from_db, db);
  EXPECT_FALSE(query_status.ok());
  EXPECT_EQ(query_status.toString(), "query name not found in database");
}

TEST_F(QueryTests, test_is_query_name_in_database) {
  auto query = getOsqueryScheduledQuery();
  auto cf = Query(query);
  auto hQR = getSerializedHistoricalQueryResultsJSON();
  auto put_status = db->Put(kQueries, query.name, hQR.first);
  EXPECT_TRUE(put_status.ok());
  EXPECT_EQ(put_status.toString(), "OK");
  EXPECT_TRUE(cf.isQueryNameInDatabase(db));
}

TEST_F(QueryTests, test_get_stored_query_names) {
  auto query = getOsqueryScheduledQuery();
  auto cf = Query(query);
  auto hQR = getSerializedHistoricalQueryResultsJSON();
  auto put_status = db->Put(kQueries, query.name, hQR.first);
  EXPECT_TRUE(put_status.ok());
  EXPECT_EQ(put_status.toString(), "OK");
  auto names = cf.getStoredQueryNames(db);
  auto in_vector = std::find(names.begin(), names.end(), query.name);
  EXPECT_NE(in_vector, names.end());
}

TEST_F(QueryTests, test_get_current_results) {
  auto hQR = getSerializedHistoricalQueryResultsJSON();
  auto query = getOsqueryScheduledQuery();
  auto put_status = db->Put(kQueries, query.name, hQR.first);
  EXPECT_TRUE(put_status.ok());
  EXPECT_EQ(put_status.toString(), "OK");
  auto cf = Query(query);
  QueryData qd;
  auto query_status = cf.getCurrentResults(qd, db);
  EXPECT_TRUE(query_status.ok());
  EXPECT_EQ(query_status.toString(), "OK");
  EXPECT_EQ(qd, hQR.second.mostRecentResults.second);
}
}

int main(int argc, char* argv[]) {
  testing::InitGoogleTest(&argc, argv);
  int status = RUN_ALL_TESTS();
  boost::filesystem::remove_all(kTestingQueryDBPath);
  return status;
}
