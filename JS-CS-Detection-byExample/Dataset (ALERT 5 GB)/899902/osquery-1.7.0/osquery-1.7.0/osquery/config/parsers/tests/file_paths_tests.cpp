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

#include <osquery/config.h>
#include <osquery/registry.h>

#include "osquery/core/test_util.h"

namespace osquery {

class FilePathsConfigParserPluginTests : public testing::Test {
 public:
  void SetUp() override {
    // Read config content manually.
    readFile(kTestDataPath + "test_parse_items.conf", content_);

    // Construct a config map, the typical output from `Config::genConfig`.
    config_data_["awesome"] = content_;
  }

  size_t numFiles() {
    size_t count = 0;
    Config::getInstance().files(([&count](
        const std::string&, const std::vector<std::string>&) { count++; }));
    return count;
  }

 protected:
  std::string content_;
  std::map<std::string, std::string> config_data_;
};

TEST_F(FilePathsConfigParserPluginTests, test_get_files) {
  std::vector<std::string> expected_categories = {
      "config_files", "logs", "logs"};
  std::vector<std::string> categories;
  std::vector<std::string> expected_values = {
      "/dev", "/dev/zero", "/dev/null", "/dev/random", "/dev/urandom"};
  std::vector<std::string> values;

  Config::getInstance().update(config_data_);
  Config::getInstance().files(([&categories, &values](
      const std::string& category, const std::vector<std::string>& files) {
    categories.push_back(category);
    for (const auto& file : files) {
      values.push_back(file);
    }
  }));

  EXPECT_EQ(categories, expected_categories);
  EXPECT_EQ(values, expected_values);
}

TEST_F(FilePathsConfigParserPluginTests, test_get_file_accesses) {
  // Assume config data has been added.
  auto parser = Config::getParser("file_paths");
  auto& accesses = parser->getData().get_child("file_accesses");
  EXPECT_EQ(accesses.size(), 2U);
}

TEST_F(FilePathsConfigParserPluginTests, test_remove_source) {
  EXPECT_GT(numFiles(), 0U);
  Config::getInstance().removeFiles("awesome");
  // Expect the pack's set to persist.
  // Do not call removeFiles, instead only update the pack/config content.
  EXPECT_EQ(numFiles(), 1U);

  // This will clear all source data for 'awesome'.
  config_data_["awesome"] = "";
  Config::getInstance().update(config_data_);
  EXPECT_EQ(numFiles(), 0U);
}
}
