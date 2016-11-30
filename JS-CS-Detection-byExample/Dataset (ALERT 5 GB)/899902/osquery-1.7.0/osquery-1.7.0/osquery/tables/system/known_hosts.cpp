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
#include <vector>

#include <osquery/core.h>
#include <osquery/tables.h>
#include <osquery/filesystem.h>

#include "osquery/tables/system/system_utils.h"

namespace osquery {
namespace tables {

const std::vector<std::string> kSSHKnownHostskeys = {".ssh/known_hosts"};

void genSSHkeysForHosts(const std::string& uid,
                        const std::string& directory,
                        QueryData& results) {
  for (const auto& kfile : kSSHKnownHostskeys) {
    boost::filesystem::path keys_file = directory;
    keys_file /= kfile;

    std::string keys_content;
    if (!forensicReadFile(keys_file, keys_content).ok()) {
      // Cannot read a specific keys file.
      continue;
    }

    for (const auto& line : split(keys_content, "\n")) {
      if (!line.empty() && line[0] != '#') {
        Row r;
        r["uid"] = uid;
        r["key"] = line;
        r["key_file"] = keys_file.string();
        results.push_back(r);
      }
    }
  }
}

QueryData getKnownHostsKeys(QueryContext& context) {
  QueryData results;

  // Iterate over each user
  auto users = usersFromContext(context);
  for (const auto& row : users) {
    if (row.count("uid") > 0 && row.count("directory") > 0) {
      genSSHkeysForHosts(row.at("uid"), row.at("directory"), results);
    }
  }

  return results;
}
}
}
