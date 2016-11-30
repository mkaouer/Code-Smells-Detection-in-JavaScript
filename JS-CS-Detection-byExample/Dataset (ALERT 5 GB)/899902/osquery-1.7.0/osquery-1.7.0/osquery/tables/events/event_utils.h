/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <string>

#include <osquery/tables.h>

namespace osquery {

/**
 * @brief A helper function for each platform's implementation of file_events.
 *
 * Given an action and path, this Row decorator assures a common implementation
 * of hashing and common columns from the `file` table.
 *
 * @param path The target path from the file event.
 * @param hash Should the target path be read and hashed.
 * @param r The output parameter row structure.
 */
void decorateFileEvent(const std::string& path, bool hash, Row& r);
}
