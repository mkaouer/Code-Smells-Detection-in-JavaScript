/*
 *  Copyright (c) 2015, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <CoreFoundation/CoreFoundation.h>
#include <Foundation/Foundation.h>
#include <gtest/gtest.h>
#include <boost/filesystem/operations.hpp>
#include <boost/make_unique.hpp>
#include <mach-o/dyld.h>

#include "osquery/core/test_util.h"

namespace fs = boost::filesystem;

namespace osquery {
namespace tables {

void genSignatureForFile(const std::string& path, QueryData& results);

// Gets the full path to the current executable (only works on Darwin)
std::string getExecutablePath() {
  uint32_t size = 1024;

  while (true) {
    auto buf = boost::make_unique<char[]>(size);

    if (_NSGetExecutablePath(buf.get(), &size) == 0) {
      return std::string(buf.get());
    }

    // If we get here, the buffer wasn't large enough, and we need to
    // reallocate.  We just continue the loop and will reallocate above.
  }
}

// Get the full, real path to the current executable (only works on Darwin).
std::string getRealExecutablePath() {
  auto path = getExecutablePath();
  return fs::canonical(path).string();
}

class SignatureTest : public testing::Test {
 protected:
  void SetUp() {
    tempFile = kTestWorkingDirectory + "darwin-signature";
  }

  void TearDown() {
    // End the event loops, and join on the threads.
    fs::remove_all(tempFile);
  }

 protected:
  std::string tempFile;
};

/*
 * Ensures that the signature for a signed binary is correct.
 *
 * We use `/bin/ls` as the test binary, since it should be present "everywhere".
 */
TEST_F(SignatureTest, test_get_valid_signature) {
  std::string path = "/bin/ls";

  QueryData results;
  genSignatureForFile(path, results);

  Row expected = {
      {"path", path},
      {"signed", "1"},
      {"identifier", "com.apple.ls"},
  };

  for (const auto& column : expected) {
    EXPECT_EQ(results.front()[column.first], column.second);
  }
}

/*
 * Ensures that the results for an unsigned binary are correct.
 *
 * We use the currently-running binary as the 'unsigned binary', rather than
 * relying on a particular binary to be present.
 */
TEST_F(SignatureTest, test_get_unsigned) {
  std::string path = getRealExecutablePath();

  QueryData results;
  genSignatureForFile(path, results);

  Row expected = {
      {"path", path},
      {"signed", "0"},
      {"identifier", ""},
  };

  for (const auto& column : expected) {
    EXPECT_EQ(results.front()[column.first], column.second);
  }
}

/*
 * Ensures that the results for a signed but invalid binary are correct.
 *
 * This test is a bit of a hack - we copy an existing signed binary (/bin/ls,
 * like above), and then modify one byte in the middle of the file by XORing it
 * with 0xBA.  This should ensure that it differs from whatever the original
 * byte was, and should thus invalidate the signature.
 */
TEST_F(SignatureTest, test_get_invalid_signature) {
  std::string originalPath = "/bin/ls";
  std::string newPath = tempFile;

  // Create a buffer to hold the entire file.
  std::vector<uint8_t> binary;
  binary.resize(fs::file_size(originalPath));
  ASSERT_TRUE(binary.size() > 0);

  // Open existing file
  FILE* f = fopen(originalPath.c_str(), "rb");
  ASSERT_TRUE(f != nullptr);

  // Read it to memory
  auto nread = fread(&binary[0], sizeof(uint8_t), binary.size(), f);
  fclose(f);
  ASSERT_EQ(nread, binary.size());

  // Actually modify a byte.
  size_t offset = binary.size() / 2;
  binary[offset] = binary[offset] ^ 0xBA;

  // Write it back to a file.
  f = fopen(newPath.c_str(), "wb");
  ASSERT_TRUE(f != nullptr);
  fwrite(&binary[0], sizeof(uint8_t), binary.size(), f);
  fclose(f);

  // Get the signature of this new file.
  QueryData results;
  genSignatureForFile(newPath, results);

  Row expected = {
      {"path", newPath},
      {"signed", "0"},
      {"identifier", ""},
  };

  for (const auto& column : expected) {
    EXPECT_EQ(results.front()[column.first], column.second);
  }
}

}
}

