/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <iostream>

#include <gtest/gtest.h>

#include <osquery/core.h>
#include <osquery/sql.h>

#include "osquery/core/test_util.h"
#include "osquery/sql/sqlite_util.h"

namespace osquery {

class SQLiteUtilTests : public testing::Test {};

SQLiteDBInstance getTestDBC() {
  SQLiteDBInstance dbc = SQLiteDBManager::getUnique();
  char* err = nullptr;
  std::vector<std::string> queries = {
      "CREATE TABLE test_table (username varchar(30) primary key, age int)",
      "INSERT INTO test_table VALUES (\"mike\", 23)",
      "INSERT INTO test_table VALUES (\"matt\", 24)"};

  for (auto q : queries) {
    sqlite3_exec(dbc.db(), q.c_str(), nullptr, nullptr, &err);
    if (err != nullptr) {
      throw std::domain_error(std::string("Cannot create testing DBC's db: ") +
                              err);
    }
  }

  return dbc;
}

TEST_F(SQLiteUtilTests, test_simple_query_execution) {
  // Access to the internal SQL implementation is only available in core.
  auto sql = SQL("SELECT * FROM time");
  EXPECT_TRUE(sql.ok());
  EXPECT_EQ(sql.rows().size(), 1);
}

TEST_F(SQLiteUtilTests, test_get_tables) {
  // Access to the internal SQL implementation is only available in core.
  auto tables = SQL::getTableNames();
  EXPECT_TRUE(tables.size() > 0);
}

TEST_F(SQLiteUtilTests, test_sqlite_instance_manager) {
  auto dbc1 = SQLiteDBManager::get();
  auto dbc2 = SQLiteDBManager::get();
  EXPECT_NE(dbc1.db(), dbc2.db());
  EXPECT_EQ(dbc1.db(), dbc1.db());
}

TEST_F(SQLiteUtilTests, test_sqlite_instance) {
  // Don't do this at home kids.
  // Keep a copy of the internal DB and let the SQLiteDBInstance go oos.
  auto internal_db = SQLiteDBManager::get().db();
  // Compare the internal DB to another request with no SQLiteDBInstances
  // in scope, meaning the primary will be returned.
  EXPECT_EQ(internal_db, SQLiteDBManager::get().db());
}

TEST_F(SQLiteUtilTests, test_direct_query_execution) {
  auto dbc = getTestDBC();
  QueryData results;
  auto status = queryInternal(kTestQuery, results, dbc.db());
  EXPECT_TRUE(status.ok());
  EXPECT_EQ(results, getTestDBExpectedResults());
}

TEST_F(SQLiteUtilTests, test_passing_callback_no_data_param) {
  char* err = nullptr;
  auto dbc = getTestDBC();
  sqlite3_exec(dbc.db(), kTestQuery.c_str(), queryDataCallback, nullptr, &err);
  EXPECT_TRUE(err != nullptr);
  if (err != nullptr) {
    sqlite3_free(err);
  }
}

TEST_F(SQLiteUtilTests, test_aggregate_query) {
  auto dbc = getTestDBC();
  QueryData results;
  auto status = queryInternal(kTestQuery, results, dbc.db());
  EXPECT_TRUE(status.ok());
  EXPECT_EQ(results, getTestDBExpectedResults());
}

TEST_F(SQLiteUtilTests, test_get_test_db_result_stream) {
  auto dbc = getTestDBC();
  auto results = getTestDBResultStream();
  for (auto r : results) {
    char* err_char = nullptr;
    sqlite3_exec(dbc.db(), (r.first).c_str(), nullptr, nullptr, &err_char);
    EXPECT_TRUE(err_char == nullptr);
    if (err_char != nullptr) {
      sqlite3_free(err_char);
      ASSERT_TRUE(false);
    }

    QueryData expected;
    auto status = queryInternal(kTestQuery, expected, dbc.db());
    EXPECT_EQ(expected, r.second);
  }
}

TEST_F(SQLiteUtilTests, test_get_query_columns) {
  auto dbc = getTestDBC();
  tables::TableColumns results;

  std::string query = "SELECT seconds, version FROM time JOIN osquery_info";
  auto status = getQueryColumnsInternal(query, results, dbc.db());
  ASSERT_TRUE(status.ok());
  ASSERT_EQ(2, results.size());
  EXPECT_EQ(std::make_pair(std::string("seconds"), std::string("INTEGER")),
            results[0]);
  EXPECT_EQ(std::make_pair(std::string("version"), std::string("TEXT")),
            results[1]);

  query = "SELECT hour + 1 AS hour1, minutes + 1 FROM time";
  status = getQueryColumnsInternal(query, results, dbc.db());
  ASSERT_TRUE(status.ok());
  ASSERT_EQ(2, results.size());
  EXPECT_EQ(std::make_pair(std::string("hour1"), std::string("UNKNOWN")),
            results[0]);
  EXPECT_EQ(std::make_pair(std::string("minutes + 1"), std::string("UNKNOWN")),
            results[1]);

  query = "SELECT * FROM foo";
  status = getQueryColumnsInternal(query, results, dbc.db());
  ASSERT_FALSE(status.ok());
}
}
