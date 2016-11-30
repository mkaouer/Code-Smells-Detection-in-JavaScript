/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant 
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <pwd.h>
#include <grp.h>
#include <sys/stat.h>

#include <boost/filesystem.hpp>

#include <osquery/filesystem.h>
#include <osquery/logger.h>
#include <osquery/tables.h>

namespace fs = boost::filesystem;

namespace osquery {
namespace tables {

std::vector<std::string> kBinarySearchPaths = {
  "/bin",
  "/sbin",
  "/usr/bin",
  "/usr/sbin",
  "/usr/local/bin",
  "/usr/local/sbin",
  "/tmp",
};

Status genBin(const fs::path& path, int perms, QueryData& results) {
  struct stat info;
  // store user and group
  if (stat(path.c_str(), &info) != 0) {
    return Status(1, "stat failed");
  }

  // store path
  Row r;
  r["path"] = path.string();
  struct passwd *pw = getpwuid(info.st_uid);
  struct group *gr = getgrgid(info.st_gid);

  // get user name + group
  std::string user;
  if (pw != nullptr) {
    user = std::string(pw->pw_name);
  } else {
    user = boost::lexical_cast<std::string>(info.st_uid);
  }

  std::string group;
  if (gr != nullptr) {
    group = std::string(gr->gr_name);
  } else {
    group = boost::lexical_cast<std::string>(info.st_gid);
  }

  r["username"] = user;
  r["groupname"] = group;

  r["permissions"] = "";
  if ((perms & 04000) == 04000) {
    r["permissions"] += "S";
  }

  if ((perms & 02000) == 02000) {
    r["permissions"] += "G";
  }

  results.push_back(r);
  return Status(0, "OK");
}

bool isSuidBin(const fs::path& path, int perms) {
  if (!fs::is_regular_file(path)) {
    return false;
  }

  if ((perms & 04000) == 04000 || (perms & 02000) == 02000) {
    return true;
  }
  return false;
}

void genSuidBinsFromPath(const std::string& path, QueryData& results) {
  if (!pathExists(path).ok()) {
    // Creating an iterator on a missing path will except.
    return;
  }

  auto it = fs::recursive_directory_iterator(fs::path(path));
  fs::recursive_directory_iterator end;
  while (it != end) {
    fs::path path = *it;
    try {
      // Do not traverse symlinked directories.
      if (fs::is_directory(path) && fs::is_symlink(path)) {
        it.no_push();
      }

      int perms = it.status().permissions();
      if (isSuidBin(path, perms)) {
        // Only emit suid bins.
        genBin(path, perms, results);
      }

      ++it;
    } catch (fs::filesystem_error& e) {
      VLOG(1) << "Cannot read binary from " << path;
      it.no_push();
      // Try to recover, otherwise break.
      try { ++it; } catch(fs::filesystem_error& e) { break; }
    }
  }
}

QueryData genSuidBin(QueryContext& context) {
  QueryData results;

  // Todo: add hidden column to select on that triggers non-std path searches.
  for (const auto& path : kBinarySearchPaths) {
    genSuidBinsFromPath(path, results);
  }

  return results;
}
}
}
