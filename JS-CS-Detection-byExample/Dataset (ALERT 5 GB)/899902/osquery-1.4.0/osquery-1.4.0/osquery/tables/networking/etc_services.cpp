/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant 
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <vector>
#include <string>

#include <boost/algorithm/string/join.hpp>
#include <boost/algorithm/string/predicate.hpp>

#include <osquery/core.h>
#include <osquery/logger.h>
#include <osquery/tables.h>
#include <osquery/filesystem.h>

namespace osquery {
namespace tables {

QueryData parseEtcServicesContent(const std::string& content) {
  QueryData results;

  for (const auto& line : split(content, "\n")) {
    // Empty line or comment.
    if (line.size() == 0 || boost::starts_with(line, "#")) {
      continue;
    }

    // [0]: name port/protocol [aliases]
    // [1]: [comment part1]
    // [2]: [comment part2]
    // [n]: [comment partn]
    auto service_info_comment = split(line, "#");

    // [0]: name
    // [1]: port/protocol
    // [2]: [aliases0]
    // [3]: [aliases1]
    // [n]: [aliasesn]
    auto service_info = split(service_info_comment[0]);
    if (service_info.size() < 2) {
      LOG(WARNING) << "Line of /etc/services wasn't properly formatted. "
                   << "Expected at least 2, got " << service_info.size();
      continue;
    }

    // [0]: port [1]: protocol
    auto service_port_protocol = split(service_info[1], "/");
    if (service_port_protocol.size() != 2) {
      LOG(WARNING) << "Line of /etc/services wasn't properly formatted. "
                   << "Expected 2, got " << service_port_protocol.size();
      continue;
    }

    Row r;
    r["name"] = TEXT(service_info[0]);
    r["port"] = INTEGER(service_port_protocol[0]);
    r["protocol"] = TEXT(service_port_protocol[1]);

    // Removes the name and the port/protcol elements.
    service_info.erase(service_info.begin(), service_info.begin() + 2);
    r["aliases"] = TEXT(boost::algorithm::join(service_info, " "));

    // If there is a comment for the service.
    if (service_info_comment.size() > 1) {
      // Removes everything except the comment (parts of the comment).
      service_info_comment.erase(service_info_comment.begin(), service_info_comment.begin() + 1);
      r["comment"] = TEXT(boost::algorithm::join(service_info_comment, " # "));
    }
    results.push_back(r);
  }
  return results;
}

QueryData genEtcServices(QueryContext& context) {
  std::string content;
  auto s = osquery::readFile("/etc/services", content);
  if (s.ok()) {
    return parseEtcServicesContent(content);
  } else {
    LOG(ERROR) << "Error reading /etc/services: " << s.toString();
    return {};
  }
}
}
}
