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
#include <osquery/flags.h>
#include <osquery/logger.h>

namespace osquery {

DECLARE_string(test_string_flag);

class FlagsTests : public testing::Test {
 public:
  FlagsTests() {}

  void SetUp() {}
};

FLAG(string, test_string_flag, "TEST STRING", "TEST DESCRIPTION");

TEST_F(FlagsTests, test_set_get) {
  // Test the core gflags functionality.
  EXPECT_EQ(FLAGS_test_string_flag, "TEST STRING");

  // Check that the gflags flag name was recorded in the osquery flag tracker.
  auto all_flags = Flag::flags();
  EXPECT_EQ(all_flags.count("test_string_flag"), 1);

  // Update the value of the flag, and access through the osquery wrapper.
  FLAGS_test_string_flag = "NEW TEST STRING";
  EXPECT_EQ(Flag::getValue("test_string_flag"), "NEW TEST STRING");
}

TEST_F(FlagsTests, test_defaults) {
  // Make sure the flag value was not reset.
  EXPECT_EQ(FLAGS_test_string_flag, "NEW TEST STRING");

  // Now test that the default value is tracked.
  EXPECT_FALSE(Flag::isDefault("test_string_flag"));

  // Check the default value accessor.
  std::string default_value;
  auto status = Flag::getDefaultValue("test_mistake", default_value);
  EXPECT_FALSE(status.ok());
  status = Flag::getDefaultValue("test_string_flag", default_value);
  EXPECT_TRUE(status.ok());
  EXPECT_EQ(default_value, "TEST STRING");
}

TEST_F(FlagsTests, test_details) {
  // Make sure flag details are tracked correctly.
  auto all_flags = Flag::flags();
  auto flag_info = all_flags["test_string_flag"];

  EXPECT_EQ(flag_info.type, "string");
  EXPECT_EQ(flag_info.description, "TEST DESCRIPTION");
  EXPECT_EQ(flag_info.default_value, "TEST STRING");
  EXPECT_EQ(flag_info.value, "NEW TEST STRING");
  EXPECT_EQ(flag_info.detail.shell, false);
  EXPECT_EQ(flag_info.detail.external, false);
}

SHELL_FLAG(bool, shell_only, true, "TEST SHELL DESCRIPTION");
EXTENSION_FLAG(bool, extension_only, true, "TEST EXTENSION DESCRIPTION");

TEST_F(FlagsTests, test_flag_detail_types) {
  EXPECT_TRUE(FLAGS_shell_only);
  EXPECT_TRUE(FLAGS_extension_only);

  auto all_flags = Flag::flags();
  EXPECT_TRUE(all_flags["shell_only"].detail.shell);
  EXPECT_TRUE(all_flags["extension_only"].detail.external);
}

FLAG_ALIAS(bool, shell_only_alias, shell_only);

TEST_F(FlagsTests, test_aliases) {
  EXPECT_TRUE(FLAGS_shell_only_alias);
  FLAGS_shell_only = false;
  EXPECT_FALSE(FLAGS_shell_only);
  EXPECT_FALSE(FLAGS_shell_only_alias);
}

FLAG(int32, test_int32, 1, "none");
FLAG_ALIAS(google::int32, test_int32_alias, test_int32);

FLAG(int64, test_int64, (int64_t)1 << 34, "none");
FLAG_ALIAS(google::int64, test_int64_alias, test_int64);

FLAG(double, test_double, 4.2, "none");
FLAG_ALIAS(double, test_double_alias, test_double);

FLAG(string, test_string, "test", "none");
FLAG_ALIAS(std::string, test_string_alias, test_string);

TEST_F(FlagsTests, test_alias_types) {
  // Test int32 lexical casting both ways.
  EXPECT_EQ(FLAGS_test_int32_alias, 1);
  FLAGS_test_int32_alias = 2;
  EXPECT_EQ(FLAGS_test_int32, 2);
  FLAGS_test_int32 = 3;
  EXPECT_EQ(FLAGS_test_int32_alias, 3);
  EXPECT_TRUE(FLAGS_test_int32_alias > 0);

  EXPECT_EQ(FLAGS_test_int64_alias, (int64_t)1 << 34);
  FLAGS_test_int64_alias = (int64_t)1 << 35;
  EXPECT_EQ(FLAGS_test_int64, (int64_t)1 << 35);
  FLAGS_test_int64 = (int64_t)1 << 36;
  EXPECT_EQ(FLAGS_test_int64_alias, (int64_t)1 << 36);
  EXPECT_TRUE(FLAGS_test_int64_alias > 0);

  EXPECT_EQ(FLAGS_test_double_alias, 4.2);
  FLAGS_test_double_alias = 2.4;
  EXPECT_EQ(FLAGS_test_double, 2.4);
  FLAGS_test_double = 22.44;
  EXPECT_EQ(FLAGS_test_double_alias, 22.44);
  EXPECT_TRUE(FLAGS_test_double_alias > 0);

  // Compile-time type checking will not compare typename T to const char*
  std::string value = FLAGS_test_string_alias;
  EXPECT_EQ(value, "test");
  FLAGS_test_string_alias = "test2";
  EXPECT_EQ(FLAGS_test_string, "test2");
  FLAGS_test_string = "test3";

  // Test both the copy and assignment constructor aliases.
  value = FLAGS_test_string_alias;
  auto value2 = (std::string)FLAGS_test_string_alias;
  EXPECT_EQ(value, "test3");
}
}
