/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant 
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <boost/algorithm/string/join.hpp>
#include <boost/filesystem/operations.hpp>
#include <boost/filesystem/path.hpp>

#include <osquery/core.h>
#include <osquery/filesystem.h>
#include <osquery/logger.h>
#include <osquery/tables.h>
#include <osquery/sql.h>

namespace pt = boost::property_tree;

namespace osquery {
namespace tables {

const std::map<std::string, std::string> kAppsInfoPlistTopLevelStringKeys = {
    {"CFBundleExecutable", "bundle_executable"},
    {"CFBundleIdentifier", "bundle_identifier"},
    {"CFBundleName", "bundle_name"},
    {"CFBundleShortVersionString", "bundle_short_version"},
    {"CFBundleVersion", "bundle_version"},
    {"CFBundlePackageType", "bundle_package_type"},
    {"LSEnvironment", "environment"},
    {"LSUIElement", "element"},
    {"CFBundleDevelopmentRegion", "development_region"},
    {"CFBundleDisplayName", "display_name"},
    {"CFBundleGetInfoString", "info_string"},
    {"DTCompiler", "compiler"},
    {"LSMinimumSystemVersion", "minimum_system_version"},
    {"LSApplicationCategoryType", "category"},
    {"NSAppleScriptEnabled", "applescript_enabled"},
    {"NSHumanReadableCopyright", "copyright"},
};

const std::vector<std::string> kHomeDirSearchPaths = {
    "Applications", "Desktop", "Downloads",
};

std::vector<std::string> getSystemApplications() {
  std::vector<std::string> results;
  std::vector<std::string> sys_apps;
  auto status = osquery::listDirectoriesInDirectory("/Applications", sys_apps);

  if (status.ok()) {
    for (const auto& app_path : sys_apps) {
      std::string plist_path = app_path + "/Contents/Info.plist";
      if (boost::filesystem::exists(plist_path)) {
        results.push_back(plist_path);
      }
    }
  } else {
    VLOG(1) << "Error listing /Applications: " << status.toString();
  }
  return results;
}

std::vector<std::string> getUserApplications(const std::string& home_dir) {
  std::vector<std::string> results;
  for (const auto& dir_to_check : kHomeDirSearchPaths) {
    boost::filesystem::path apps_path = home_dir;
    apps_path /= dir_to_check;

    std::vector<std::string> user_apps;
    auto status = osquery::listDirectoriesInDirectory(apps_path, user_apps);
    if (status.ok()) {
      for (const auto& user_app : user_apps) {
        std::string plist_path = user_app + "/Contents/Info.plist";
        if (boost::filesystem::exists(plist_path)) {
          results.push_back(plist_path);
        }
      }
    } else {
      VLOG(1) << "Error listing " << apps_path << ": " << status.toString();
    }
  }
  return results;
}

std::string getNameFromInfoPlistPath(const std::string& path) {
  boost::filesystem::path full = path;
  return full.parent_path().parent_path().filename().string();
}

std::string getPathFromInfoPlistPath(const std::string& path) {
  boost::filesystem::path full = path;
  return full.parent_path().parent_path().string();
}

Row parseInfoPlist(const std::string& path, const pt::ptree& tree) {
  Row r;

  boost::filesystem::path full = path;
  r["name"] = getNameFromInfoPlistPath(path);
  r["path"] = getPathFromInfoPlistPath(path);
  for (const auto& it : kAppsInfoPlistTopLevelStringKeys) {
    try {
      r[it.second] = tree.get<std::string>(it.first);
      // Change boolean values into integer 1, 0.
      if (r[it.second] == "true" || r[it.second] == "YES" ||
          r[it.second] == "Yes") {
        r[it.second] = INTEGER(1);
      } else if (r[it.second] == "false" || r[it.second] == "NO" ||
                 r[it.second] == "No") {
        r[it.second] = INTEGER(0);
      }
    } catch (const pt::ptree_error& e) {
      // Expect that most of the selected keys are missing.
      r[it.second] = "";
    }
  }
  return r;
}

QueryData genApps(QueryContext& context) {
  QueryData results;
  pt::ptree tree;

  // Enumerate and parse applications in / (system applications).
  for (const auto& path : getSystemApplications()) {
    if (osquery::parsePlist(path, tree).ok()) {
      results.push_back(parseInfoPlist(path, tree));
    } else {
      VLOG(1) << "Error parsing system applications: " << path;
    }
  }

  auto users = SQL::selectAllFrom("users");

  // Enumerate apps for each user (several paths).
  for (const auto& user : users) {
    for (const auto& path : getUserApplications(user.at("directory"))) {
      if (osquery::parsePlist(path, tree).ok()) {
        Row r = parseInfoPlist(path, tree);
        results.push_back(r);
      } else {
        VLOG(1) << "Error parsing user applications: " << path;
      }
    }
  }

  return results;
}
}
}
