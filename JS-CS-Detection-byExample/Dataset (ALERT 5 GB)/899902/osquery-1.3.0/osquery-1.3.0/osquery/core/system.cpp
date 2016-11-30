/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <sstream>

#include <sys/types.h>
#include <signal.h>

#if !defined(__FreeBSD__)
#include <uuid/uuid.h>
#endif

#include <boost/algorithm/string/trim.hpp>
#include <boost/filesystem.hpp>
#include <boost/lexical_cast.hpp>
#include <boost/uuid/uuid.hpp>
#include <boost/uuid/uuid_generators.hpp>
#include <boost/uuid/uuid_io.hpp>

#include <glog/logging.h>

#include <osquery/core.h>
#include <osquery/database/db_handle.h>
#include <osquery/filesystem.h>
#include <osquery/sql.h>

namespace fs = boost::filesystem;

namespace osquery {

/// The path to the pidfile for osqueryd
DEFINE_osquery_flag(string,
                    pidfile,
                    "/var/osquery/osqueryd.pidfile",
                    "The path to the pidfile for osqueryd.");

std::string getHostname() {
  char hostname[256]; // Linux max should be 64.
  memset(hostname, 0, sizeof(hostname));
  gethostname(hostname, sizeof(hostname) - 1);
  std::string hostname_string = std::string(hostname);
  boost::algorithm::trim(hostname_string);
  return hostname_string;
}

std::string generateNewUuid() {
  boost::uuids::uuid uuid = boost::uuids::random_generator()();
  return boost::uuids::to_string(uuid);
}

std::string generateHostUuid() {
#ifdef __APPLE__
  // Use the hardware uuid available on OSX to identify this machine
  char uuid[128];
  memset(uuid, 0, sizeof(uuid));
  uuid_t id;
  // wait at most 5 seconds for gethostuuid to return
  const timespec wait = {5, 0};
  int result = gethostuuid(id, &wait);
  if (result == 0) {
    char out[128];
    uuid_unparse(id, out);
    std::string uuid_string = std::string(out);
    boost::algorithm::trim(uuid_string);
    return uuid_string;
  } else {
    // unable to get the hardware uuid, just return a new uuid
    return generateNewUuid();
  }
#else
  return generateNewUuid();
#endif
}

std::string getAsciiTime() {
  std::time_t result = std::time(NULL);
  std::string time_str = std::string(std::asctime(std::localtime(&result)));
  boost::algorithm::trim(time_str);
  return time_str;
}

int getUnixTime() {
  std::time_t result = std::time(NULL);
  return result;
}

std::vector<fs::path> getHomeDirectories() {
  auto sql = SQL(
      "SELECT DISTINCT directory FROM users WHERE directory != '/var/empty';");
  std::vector<fs::path> results;
  if (sql.ok()) {
    for (const auto& row : sql.rows()) {
      results.push_back(row.at("directory"));
    }
  } else {
    LOG(ERROR)
        << "Error executing query to return users: " << sql.getMessageString();
  }
  return results;
}

Status checkStalePid(const std::string& pidfile_content) {
  int pid;
  try {
    pid = stoi(pidfile_content);
  } catch (const std::invalid_argument& e) {
    return Status(1, std::string("Could not parse pidfile: ") + e.what());
  }

  int status = kill(pid, 0);
  if (status != ESRCH) {
    // The pid is running, check if it is an osqueryd process by name.
    std::stringstream query_text;
    query_text << "SELECT name FROM processes WHERE pid = " << pid << ";";
    auto q = SQL(query_text.str());
    if (!q.ok()) {
      return Status(1, "Error querying processes: " + q.getMessageString());
    }

    if (q.rows().size() >= 1 && q.rows().front()["name"] == "osqueryd") {
      // If the process really is osqueryd, return an "error" status.
      return Status(1,
                    std::string("osqueryd (") + pidfile_content +
                        ") is already running");
    } else {
      LOG(INFO) << "Found stale process for osqueryd (" << pidfile_content
                << ") removing pidfile.";
    }
  }

  // Now the pidfile is either the wrong pid or the pid is not running.
  try {
    boost::filesystem::remove(FLAGS_pidfile);
  } catch (boost::filesystem::filesystem_error& e) {
    // Unable to remove old pidfile.
    LOG(WARNING) << "Unable to remove the osqueryd pidfile.";
  }

  return Status(0, "OK");
}

Status createPidFile() {
  // check if pidfile exists
  auto exists = pathExists(FLAGS_pidfile);
  if (exists.ok()) {
    // if it exists, check if that pid is running.
    std::string content;
    auto read_status = readFile(FLAGS_pidfile, content);
    if (!read_status.ok()) {
      return Status(1, "Could not read pidfile: " + read_status.toString());
    }

    auto stale_status = checkStalePid(content);
    if (!stale_status.ok()) {
      return stale_status;
    }
  }

  // If no pidfile exists or the existing pid was stale, write, log, and run.
  auto pid = boost::lexical_cast<std::string>(getpid());
  LOG(INFO) << "Writing osqueryd pid (" << pid << ") to " << FLAGS_pidfile;
  auto status = writeTextFile(FLAGS_pidfile, pid, 0644);
  return status;
}
}
