/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <boost/property_tree/json_parser.hpp>

#include <gtest/gtest.h>

#include <osquery/core.h>
#include <osquery/filesystem.h>
#include <osquery/flags.h>
#include <osquery/packs.h>

#include "osquery/core/test_util.h"

namespace osquery {

extern size_t getMachineShard(const std::string& hostname = "",
                              bool force = false);

class PacksTests : public testing::Test {};

TEST_F(PacksTests, test_parse) {
  auto tree = getExamplePacksConfig();
  EXPECT_EQ(tree.count("packs"), 1U);
}

TEST_F(PacksTests, test_should_pack_execute) {
  auto kpack = Pack("unrestricted_pack", getUnrestrictedPack());
  EXPECT_TRUE(kpack.shouldPackExecute());

  auto fpack = Pack("discovery_pack", getPackWithDiscovery());
  EXPECT_FALSE(fpack.shouldPackExecute());
}

TEST_F(PacksTests, test_get_discovery_queries) {
  std::vector<std::string> expected;

  auto kpack = Pack("unrestricted_pack", getUnrestrictedPack());
  EXPECT_EQ(kpack.getDiscoveryQueries(), expected);

  expected = {"select pid from processes where name = 'foobar';"};
  auto fpack = Pack("discovery_pack", getPackWithDiscovery());
  EXPECT_EQ(fpack.getDiscoveryQueries(), expected);
}

TEST_F(PacksTests, test_platform) {
  auto fpack = Pack("discovery_pack", getPackWithDiscovery());
  EXPECT_EQ(fpack.getPlatform(), "all");
}

TEST_F(PacksTests, test_version) {
  auto fpack = Pack("discovery_pack", getPackWithDiscovery());
  EXPECT_EQ(fpack.getVersion(), "1.5.0");
}

TEST_F(PacksTests, test_name) {
  auto fpack = Pack("discovery_pack", getPackWithDiscovery());
  fpack.setName("also_discovery_pack");
  EXPECT_EQ(fpack.getName(), "also_discovery_pack");
}

TEST_F(PacksTests, test_sharding) {
  auto shard1 = getMachineShard("localhost.localdomain");
  auto shard2 = getMachineShard("not.localhost.localdomain");
  // Expect some static caching.
  EXPECT_EQ(shard1, shard2);

  // Bypass the caching.
  shard2 = getMachineShard("not.localhost.localdomain", true);
  EXPECT_NE(shard1, shard2);
}

TEST_F(PacksTests, test_check_platform) {
  auto fpack = Pack("discovery_pack", getPackWithDiscovery());
  EXPECT_TRUE(fpack.checkPlatform());

  // Depending on the current build platform, this check will be true or false.
  fpack.platform_ = kSDKPlatform;
  EXPECT_TRUE(fpack.checkPlatform());

  fpack.platform_ = (kSDKPlatform == "darwin") ? "linux" : "darwin";
  EXPECT_FALSE(fpack.checkPlatform());

  fpack.platform_ = "null";
  EXPECT_TRUE(fpack.checkPlatform());

  fpack.platform_ = "";
  EXPECT_TRUE(fpack.checkPlatform());

  fpack.platform_ = "bad_value";
  EXPECT_FALSE(fpack.checkPlatform());
}

TEST_F(PacksTests, test_check_version) {
  auto zpack = Pack("fake_version_pack", getPackWithFakeVersion());
  EXPECT_FALSE(zpack.checkVersion());

  auto fpack = Pack("discovery_pack", getPackWithDiscovery());
  EXPECT_TRUE(fpack.checkVersion());
}

TEST_F(PacksTests, test_restriction_population) {
  // Require that all potential restrictions are populated before being checked.
  auto tree = getExamplePacksConfig();
  auto packs = tree.get_child("packs");
  auto fpack = Pack("fake_pack", packs.get_child("restricted_pack"));

  ASSERT_FALSE(fpack.getPlatform().empty());
  ASSERT_FALSE(fpack.getVersion().empty());
  ASSERT_EQ(fpack.getShard(), 1U);
}

TEST_F(PacksTests, test_schedule) {
  auto fpack = Pack("discovery_pack", getPackWithDiscovery());
  // Expect a single query in the schedule since one query has an explicit
  // invalid/fake platform requirement.
  EXPECT_EQ(fpack.getSchedule().size(), 1U);
}

TEST_F(PacksTests, test_discovery_cache) {
  Config c;
  // This pack and discovery query are valid, expect the SQL to execute.
  c.addPack("valid_discovery_pack", "", getPackWithValidDiscovery());
  size_t query_count = 0U;
  size_t query_attemts = 5U;
  for (size_t i = 0; i < query_attemts; i++) {
    c.scheduledQueries(
        ([&query_count](const std::string& name,
                        const ScheduledQuery& query) { query_count++; }));
  }
  EXPECT_EQ(query_count, query_attemts);

  size_t pack_count = 0U;
  c.packs(([&pack_count, query_attemts](Pack& p) {
    pack_count++;
    EXPECT_EQ(p.getStats().total, query_attemts);
    EXPECT_EQ(p.getStats().hits, query_attemts - 1);
    EXPECT_EQ(p.getStats().misses, 1U);
  }));

  EXPECT_EQ(pack_count, 1U);
}

TEST_F(PacksTests, test_discovery_zero_state) {
  auto pack = Pack("discovery_pack", getPackWithDiscovery());
  auto stats = pack.getStats();
  EXPECT_EQ(stats.total, 0U);
  EXPECT_EQ(stats.hits, 0U);
  EXPECT_EQ(stats.misses, 0U);
}

TEST_F(PacksTests, test_splay) {
  auto val1 = splayValue(100, 10);
  EXPECT_GE(val1, 90U);
  EXPECT_LE(val1, 110U);

  auto val2 = splayValue(100, 10);
  EXPECT_GE(val2, 90U);
  EXPECT_LE(val2, 110U);

  auto val3 = splayValue(10, 0);
  EXPECT_EQ(val3, 10U);

  auto val4 = splayValue(100, 1);
  EXPECT_GE(val4, 99U);
  EXPECT_LE(val4, 101U);

  auto val5 = splayValue(1, 10);
  EXPECT_EQ(val5, 1U);
}

TEST_F(PacksTests, test_restore_splay) {
  auto splay = restoreSplayedValue("pack_test_query_name", 3600);
  EXPECT_GE(splay, 3600U - 360);
  EXPECT_LE(splay, 3600U + 360);

  // If we restore, the splay should always be equal.
  for (size_t i = 0; i < 10; i++) {
    auto splay2 = restoreSplayedValue("pack_test_query_name", 3600);
    EXPECT_EQ(splay, splay2);
  }

  // If we modify the input interval the splay will change.
  auto splay3 = restoreSplayedValue("pack_test_query_name", 3600 * 10);
  EXPECT_GE(splay3, 3600U * 10 - (360 * 10));
  EXPECT_LE(splay3, 3600U * 10 + (360 * 10));
  EXPECT_NE(splay, splay3);
}
}
