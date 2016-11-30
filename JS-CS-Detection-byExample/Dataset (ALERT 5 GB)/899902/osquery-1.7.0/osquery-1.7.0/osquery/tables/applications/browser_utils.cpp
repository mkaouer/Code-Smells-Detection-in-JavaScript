/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <osquery/logger.h>
#include <osquery/tables/applications/browser_utils.h>

#include "osquery/tables/system/system_utils.h"

namespace osquery {
namespace tables {

#define kManifestFile "/manifest.json"

const std::map<std::string, std::string> kExtensionKeys = {
    {"version", "version"},
    {"name", "name"},
    {"description", "description"},
    {"default_locale", "locale"},
    {"update_url", "update_url"},
    {"author", "author"},
    {"background.persistent", "persistent"}};

void genExtension(const std::string& uid,
                  const std::string& path,
                  QueryData& results) {
  std::string json_data;
  if (!forensicReadFile(path + kManifestFile, json_data).ok()) {
    VLOG(1) << "Could not read file: " << path + kManifestFile;
    return;
  }

  // Read the extensions data into a JSON blob, then property tree.
  pt::ptree tree;
  try {
    std::stringstream json_stream;
    json_stream << json_data;
    pt::read_json(json_stream, tree);
  } catch (const pt::json_parser::json_parser_error& e) {
    VLOG(1) << "Could not parse JSON from: " << path + kManifestFile;
    return;
  }

  Row r;
  r["uid"] = uid;
  // Most of the keys are in the top-level JSON dictionary.
  for (const auto& it : kExtensionKeys) {
    r[it.second] = tree.get<std::string>(it.first, "");

    // Convert JSON bool-types to an integer.
    if (r[it.second] == "true") {
      r[it.second] = INTEGER(1);
    } else if (r[it.second] == "false") {
      r[it.second] = INTEGER(0);
    }
  }

  // Set the default persistence setting to false
  if (r.at("persistent") == "") {
    r["persistent"] = INTEGER(0);
  }

  r["identifier"] = fs::path(path).parent_path().parent_path().leaf().string();
  r["path"] = path;
  results.push_back(r);
}

QueryData genChromeBasedExtensions(QueryContext& context,
                                   const fs::path& sub_dir) {
  QueryData results;

  auto users = usersFromContext(context);
  for (const auto& row : users) {
    if (row.count("uid") > 0 && row.count("directory") > 0) {
      // For each user, enumerate all of their chrome profiles.
      std::vector<std::string> profiles;
      fs::path extension_path = row.at("directory") / sub_dir;
      if (!resolveFilePattern(extension_path, profiles, GLOB_FOLDERS).ok()) {
        continue;
      }

      // For each profile list each extension in the Extensions directory.
      std::vector<std::string> extensions;
      for (const auto& profile : profiles) {
        listDirectoriesInDirectory(profile, extensions);
      }

      // Generate an addons list from their extensions JSON.
      std::vector<std::string> versions;
      for (const auto& extension : extensions) {
        listDirectoriesInDirectory(extension, versions);
      }

      // Extensions use /<EXTENSION>/<VERSION>/manifest.json.
      for (const auto& version : versions) {
        genExtension(row.at("uid"), version, results);
      }
    }
  }

  return results;
}
}
}
