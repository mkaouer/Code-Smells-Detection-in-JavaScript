/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <memory>
#include <vector>

#include <boost/property_tree/json_parser.hpp>

#include <gtest/gtest.h>

#include <osquery/config.h>
#include <osquery/core.h>
#include <osquery/flags.h>
#include <osquery/packs.h>
#include <osquery/registry.h>
#include <osquery/sql.h>

#include "osquery/core/test_util.h"

namespace pt = boost::property_tree;

namespace osquery {

// Blacklist testing methods, internal to config implementations.
extern void restoreScheduleBlacklist(std::map<std::string, size_t>& blacklist);
extern void saveScheduleBlacklist(
    const std::map<std::string, size_t>& blacklist);
extern void stripConfigComments(std::string& json);

class ConfigTests : public testing::Test {
 protected:
  void SetUp() { createMockFileStructure(); }

  void TearDown() { tearDownMockFileStructure(); }
};

class TestConfigPlugin : public ConfigPlugin {
 public:
  TestConfigPlugin() {
    genConfigCount = 0;
    genPackCount = 0;
  }

  Status genConfig(std::map<std::string, std::string>& config) override {
    genConfigCount++;
    std::string content;
    auto s = readFile(kTestDataPath + "test_noninline_packs.conf", content);
    config["data"] = content;
    return s;
  }

  Status genPack(const std::string& name,
                 const std::string& value,
                 std::string& pack) override {
    genPackCount++;
    std::stringstream ss;
    pt::write_json(ss, getUnrestrictedPack(), false);
    pack = ss.str();
    return Status(0, "OK");
  }

  int genConfigCount{0};
  int genPackCount{0};
};

TEST_F(ConfigTests, test_plugin) {
  Registry::add<TestConfigPlugin>("config", "test");

  // Change the active config plugin.
  EXPECT_TRUE(Registry::setActive("config", "test").ok());

  PluginResponse response;
  auto status = Registry::call("config", {{"action", "genConfig"}}, response);

  EXPECT_EQ(status.ok(), true);
  EXPECT_EQ(status.toString(), "OK");
}

TEST_F(ConfigTests, test_bad_config_update) {
  std::string bad_json = "{\"options\": {},}";
  ASSERT_NO_THROW(Config::getInstance().update({{"bad_source", bad_json}}));
}

class PlaceboConfigParserPlugin : public ConfigParserPlugin {
 public:
  std::vector<std::string> keys() const override { return {}; }
  Status update(const std::string&, const ParserConfig&) override {
    return Status(0);
  }

  /// Make sure configure is called.
  void configure() override { configures++; }

  size_t configures{0};
};

TEST_F(ConfigTests, test_plugin_reconfigure) {
  // Add a configuration plugin (could be any plugin) that will react to
  // config updates.
  Registry::add<PlaceboConfigParserPlugin>("config_parser", "placebo");

  // Create a config that has been loaded.
  Config c;
  c.loaded_ = true;
  c.update({{"data", "{}"}});
  // Get the placebo.
  auto placebo = std::static_pointer_cast<PlaceboConfigParserPlugin>(
      Registry::get("config_parser", "placebo"));
  EXPECT_EQ(placebo->configures, 1U);
}

TEST_F(ConfigTests, test_strip_comments) {
  std::string json_comments =
      "// Comment\n // Comment //\n  # Comment\n# Comment\n{\"options\":{}}";

  // Test support for stripping C++ and hash style comments from config JSON.
  auto actual = json_comments;
  stripConfigComments(actual);
  std::string expected = "{\"options\":{}}\n";
  EXPECT_EQ(actual, expected);

  // Make sure the config update source logic applies the stripping.
  EXPECT_TRUE(Config::getInstance().update({{"data", json_comments}}));
}

class TestConfigParserPlugin : public ConfigParserPlugin {
 public:
  std::vector<std::string> keys() const override {
    // This config parser requests the follow top-level-config keys.
    return {"dictionary", "dictionary2", "list"};
  }

  Status update(const std::string& source,
                const ParserConfig& config) override {
    // Set a simple boolean indicating the update callin occurred.
    update_called = true;
    // Copy all expected keys into the parser's data.
    for (const auto& key : config) {
      data_.put_child(key.first, key.second);
    }

    // Set parser-rendered additional data.
    data_.put("dictionary3.key2", "value2");
    return Status(0, "OK");
  }

  // Flag tracking that the update method was called.
  static bool update_called;

 private:
  FRIEND_TEST(ConfigTests, test_config_parser);
};

// An intermediate boolean to check parser updates.
bool TestConfigParserPlugin::update_called = false;

TEST_F(ConfigTests, test_parse) {
  Config c;
  auto tree = getExamplePacksConfig();
  auto packs = tree.get_child("packs");
  for (const auto& pack : packs) {
    c.addPack(pack.first, "", pack.second);
  }

  std::map<std::string, bool> results = {
      {"unrestricted_pack", true},
      {"discovery_pack", false},
      {"fake_version_pack", true},
      // Although this is a valid discovery query, there is no SQL plugin in
      // the core tests.
      {"valid_discovery_pack", false},
      {"restricted_pack", true},
  };

  c.packs(([&results](Pack& pack) {
    if (results[pack.getName()]) {
      EXPECT_TRUE(pack.shouldPackExecute());
    } else {
      EXPECT_FALSE(pack.shouldPackExecute());
    }
  }));
}

TEST_F(ConfigTests, test_remove) {
  Config c;
  c.addPack("unrestricted_pack", "", getUnrestrictedPack());
  c.removePack("unrestricted_pack");

  c.packs(([](Pack& pack) { EXPECT_NE("unrestricted_pack", pack.getName()); }));
}

TEST_F(ConfigTests, test_add_remove_pack) {
  Config c;

  size_t pack_count = 0;
  c.packs(([&pack_count](Pack& pack) { pack_count++; }));
  EXPECT_EQ(pack_count, 0U);

  pack_count = 0;
  c.addPack("unrestricted_pack", "", getUnrestrictedPack());
  c.packs(([&pack_count](Pack& pack) { pack_count++; }));
  EXPECT_EQ(pack_count, 1U);

  pack_count = 0;
  c.removePack("unrestricted_pack");
  c.packs(([&pack_count](Pack& pack) { pack_count++; }));
  EXPECT_EQ(pack_count, 0U);
}

TEST_F(ConfigTests, test_update_clear) {
  // Read config content manually.
  std::string content;
  readFile(kTestDataPath + "test_parse_items.conf", content);

  // Create the output of a `genConfig`.
  std::map<std::string, std::string> config_data;
  config_data["awesome"] = content;

  // Update, then clear, packs should have been cleared.
  Config c;
  c.update(config_data);
  size_t count = 0;
  auto packCounter = [&count](Pack& pack) { count++; };
  c.packs(packCounter);
  EXPECT_GT(count, 0U);

  // Now clear.
  config_data["awesome"] = "";
  c.update(config_data);
  count = 0;
  c.packs(packCounter);
  EXPECT_EQ(count, 0U);
}

TEST_F(ConfigTests, test_get_scheduled_queries) {
  Config c;
  std::vector<ScheduledQuery> queries;
  c.addPack("unrestricted_pack", "", getUnrestrictedPack());
  c.scheduledQueries(
      ([&queries](const std::string&,
                  const ScheduledQuery& query) { queries.push_back(query); }));
  EXPECT_EQ(queries.size(), getUnrestrictedPack().get_child("queries").size());
}

TEST_F(ConfigTests, test_get_parser) {
  Registry::add<TestConfigParserPlugin>("config_parser", "test");
  EXPECT_TRUE(Registry::setActive("config_parser", "test").ok());

  Config c;
  auto s = c.update(getTestConfigMap());
  EXPECT_TRUE(s.ok());
  EXPECT_EQ(s.toString(), "OK");

  auto plugin = Config::getInstance().getParser("test");
  EXPECT_TRUE(plugin != nullptr);
  EXPECT_TRUE(plugin.get() != nullptr);

  const auto& parser =
      std::dynamic_pointer_cast<TestConfigParserPlugin>(plugin);
  const auto& data = parser->getData();

  EXPECT_EQ(data.count("list"), 1U);
  EXPECT_EQ(data.count("dictionary"), 1U);
}

TEST_F(ConfigTests, test_noninline_pack) {
  Registry::add<TestConfigPlugin>("config", "test");

  // Change the active config plugin.
  EXPECT_TRUE(Registry::setActive("config", "test").ok());

  const auto& plugin = std::dynamic_pointer_cast<TestConfigPlugin>(
      Registry::get("config", "test"));

  Config c;
  c.load();
  EXPECT_EQ(plugin->genPackCount, 1);

  int total_packs = 0;
  c.packs([&total_packs](const Pack& pack) { total_packs++; });
  EXPECT_EQ(total_packs, 2);
}

TEST_F(ConfigTests, test_blacklist) {
  auto current_time = getUnixTime();
  std::map<std::string, size_t> blacklist;
  saveScheduleBlacklist(blacklist);
  restoreScheduleBlacklist(blacklist);
  EXPECT_EQ(blacklist.size(), 0U);

  // Create some entries.
  blacklist["test_1"] = current_time * 2;
  blacklist["test_2"] = current_time * 3;
  saveScheduleBlacklist(blacklist);
  blacklist.clear();
  restoreScheduleBlacklist(blacklist);
  ASSERT_EQ(blacklist.count("test_1"), 1U);
  ASSERT_EQ(blacklist.count("test_2"), 1U);
  EXPECT_EQ(blacklist.at("test_1"), current_time * 2);
  EXPECT_EQ(blacklist.at("test_2"), current_time * 3);

  // Now save an expired query.
  blacklist["test_1"] = 1;
  saveScheduleBlacklist(blacklist);
  blacklist.clear();

  // When restoring, the values below the current time will not be included.
  restoreScheduleBlacklist(blacklist);
  EXPECT_EQ(blacklist.size(), 1U);
}
}
