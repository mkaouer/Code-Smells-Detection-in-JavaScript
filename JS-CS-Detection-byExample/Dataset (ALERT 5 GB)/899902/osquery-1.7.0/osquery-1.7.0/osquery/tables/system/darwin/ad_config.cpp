/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <osquery/filesystem.h>
#include <osquery/hash.h>
#include <osquery/sql.h>
#include <osquery/tables.h>

namespace osquery {
namespace tables {

const std::string kADConfigPath =
    "/Library/Preferences/OpenDirectory/"
    "Configurations/Active Directory/";

void genADConfig(const std::string& path, QueryData& results) {
  auto config = SQL::selectAllFrom("preferences", "path", EQUALS, path);
  if (config.size() == 0) {
    // Fail if the file could not be plist-parsed.
    return;
  }

  // Walk through module options quickly to find the trust domain.
  // The file name and domain will be included in every row.
  auto name = config[0].at("domain");
  std::string domain;
  for (const auto& row : config) {
    if (row.at("subkey") == "ActiveDirectory/trust domain") {
      domain = row.at("value");
      break;
    }
  }

  // Iterate again with the domain known, searching for options.
  for (const auto& row : config) {
    Row r;
    r["domain"] = domain;
    r["name"] = name;

    // Get references to common columns.
    const auto& key = row.at("key");
    const auto& subkey = row.at("subkey");
    if (key == "trustoptions" ||
        key == "trustkerberosprincipal" ||
        key == "trustaccount" ||
        key == "trusttype") {
      r["option"] = key;
      r["value"] = row.at("value");
      results.push_back(r);
    } else if (key == "options") {
      // The options key has a single subkey with the option name.
      r["option"] = subkey;
      r["value"] = row.at("value");
      results.push_back(r);
    } else if (key == "module options") {
      // Module options may contain 'managed client template', skip those.
      if (subkey.find("managed client template") != std::string::npos) {
        continue;
      }

      // Skip the "ActiveDirectory/" preamble.
      r["option"] = subkey.substr(16);
      r["value"] = row.at("value");
      results.push_back(r);
    }
  }
}

QueryData genADConfig(QueryContext& context) {
  QueryData results;

  // Not common to have multiple domains configured, but iterate over any file
  // within the known-path for AD plists.
  std::vector<std::string> configs;
  if (listFilesInDirectory(kADConfigPath, configs).ok()) {
    for (const auto& path : configs) {
      genADConfig(path, results);
    }
  }

  return results;
}
}
}
