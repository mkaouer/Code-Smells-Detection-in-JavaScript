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

#include <osquery/core.h>
#include <osquery/registry.h>
#include <osquery/sql.h>
#include <osquery/tables.h>

namespace osquery {

extern void escapeNonPrintableBytes(std::string& data);

class SQLTests : public testing::Test {};

TEST_F(SQLTests, test_raw_access) {
  // Access to the table plugins (no SQL parsing required) works in both
  // extensions and core, though with limitations on available tables.
  auto results = SQL::selectAllFrom("time");
  EXPECT_EQ(results.size(), 1U);
}

class TestTablePlugin : public TablePlugin {
 private:
  TableColumns columns() const {
    return {{"test_int", INTEGER_TYPE}, {"test_text", TEXT_TYPE}};
  }

  QueryData generate(QueryContext& ctx) {
    QueryData results;
    if (ctx.constraints["test_int"].existsAndMatches("1")) {
      results.push_back({{"test_int", "1"}, {"test_text", "0"}});
    } else {
      results.push_back({{"test_int", "0"}, {"test_text", "1"}});
    }

    auto ints = ctx.constraints["test_int"].getAll<int>(EQUALS);
    for (const auto& int_match : ints) {
      results.push_back({{"test_int", INTEGER(int_match)}});
    }

    return results;
  }
};

TEST_F(SQLTests, test_raw_access_context) {
  Registry::add<TestTablePlugin>("table", "test");
  auto results = SQL::selectAllFrom("test");

  EXPECT_EQ(results.size(), 1U);
  EXPECT_EQ(results[0]["test_text"], "1");

  results = SQL::selectAllFrom("test", "test_int", EQUALS, "1");
  EXPECT_EQ(results.size(), 2U);

  results = SQL::selectAllFrom("test", "test_int", EQUALS, "2");
  EXPECT_EQ(results.size(), 2U);
  EXPECT_EQ(results[0]["test_int"], "0");
}

TEST_F(SQLTests, test_sql_escape) {
  std::string input = "しかたがない";
  escapeNonPrintableBytes(input);
  EXPECT_EQ(input,
            "\\xE3\\x81\\x97\\xE3\\x81\\x8B\\xE3\\x81\\x9F\\xE3\\x81\\x8C\\xE3"
            "\\x81\\xAA\\xE3\\x81\\x84");

  input = "悪因悪果";
  escapeNonPrintableBytes(input);
  EXPECT_EQ(input,
            "\\xE6\\x82\\xAA\\xE5\\x9B\\xA0\\xE6\\x82\\xAA\\xE6\\x9E\\x9C");

  input = "モンスターハンター";
  escapeNonPrintableBytes(input);
  EXPECT_EQ(input,
            "\\xE3\\x83\\xA2\\xE3\\x83\\xB3\\xE3\\x82\\xB9\\xE3\\x82\\xBF\\xE3"
            "\\x83\\xBC\\xE3\\x83\\x8F\\xE3\\x83\\xB3\\xE3\\x82\\xBF\\xE3\\x83"
            "\\xBC");

  input = "съешь же ещё этих мягких французских булок, да выпей чаю";
  escapeNonPrintableBytes(input);
  EXPECT_EQ(
      input,
      "\\xD1\\x81\\xD1\\x8A\\xD0\\xB5\\xD1\\x88\\xD1\\x8C \\xD0\\xB6\\xD0\\xB5 "
      "\\xD0\\xB5\\xD1\\x89\\xD1\\x91 \\xD1\\x8D\\xD1\\x82\\xD0\\xB8\\xD1\\x85 "
      "\\xD0\\xBC\\xD1\\x8F\\xD0\\xB3\\xD0\\xBA\\xD0\\xB8\\xD1\\x85 "
      "\\xD1\\x84\\xD1\\x80\\xD0\\xB0\\xD0\\xBD\\xD1\\x86\\xD1\\x83\\xD0\\xB7\\"
      "xD1\\x81\\xD0\\xBA\\xD0\\xB8\\xD1\\x85 "
      "\\xD0\\xB1\\xD1\\x83\\xD0\\xBB\\xD0\\xBE\\xD0\\xBA, "
      "\\xD0\\xB4\\xD0\\xB0 \\xD0\\xB2\\xD1\\x8B\\xD0\\xBF\\xD0\\xB5\\xD0\\xB9 "
      "\\xD1\\x87\\xD0\\xB0\\xD1\\x8E");

  input = "The quick brown fox jumps over the lazy dog.";
  escapeNonPrintableBytes(input);
  EXPECT_EQ(input, "The quick brown fox jumps over the lazy dog.");
}
}
