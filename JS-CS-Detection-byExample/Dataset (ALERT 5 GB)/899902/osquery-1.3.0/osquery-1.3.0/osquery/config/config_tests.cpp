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
#include <osquery/config.h>
#include <osquery/config/plugin.h>
#include <osquery/flags.h>
#include <osquery/registry.h>

#include "osquery/core/test_util.h"

namespace osquery {

// The config_path flag is defined in the filesystem config plugin.
DECLARE_string(config_path);

class ConfigTests : public testing::Test {
 public:
  ConfigTests() {
    FLAGS_config_retriever = "filesystem";
    FLAGS_config_path = core::kTestDataPath + "test.config";

    osquery::InitRegistry::get().run();
    auto c = Config::getInstance();
    c->load();
  }
};

TEST_F(ConfigTests, test_queries_execute) {
  auto c = Config::getInstance();
  auto queries = c->getScheduledQueries();

  EXPECT_EQ(queries.size(), 1);
  for (const auto& i : queries) {
    int err;
    auto r = query(i.query, err);
    EXPECT_EQ(err, 0);
  }
}

class TestConfigPlugin : public ConfigPlugin {
 public:
  TestConfigPlugin() {}

  std::pair<Status, std::string> genConfig() {
    return std::make_pair(Status(0, "OK"), "foobar");
  }

  virtual ~TestConfigPlugin() {}
};

REGISTER_CONFIG_PLUGIN("test", std::make_shared<osquery::TestConfigPlugin>());

TEST_F(ConfigTests, test_plugin) {
  auto p = REGISTERED_CONFIG_PLUGINS.at("test")->genConfig();
  EXPECT_EQ(p.first.ok(), true);
  EXPECT_EQ(p.first.toString(), "OK");
  EXPECT_EQ(p.second, "foobar");
}
}

int main(int argc, char* argv[]) {
  testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
