/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <arpa/inet.h>

#include <boost/algorithm/string/split.hpp>

#include <osquery/core.h>
#include <osquery/filesystem.h>
#include <osquery/logger.h>
#include <osquery/tables.h>

namespace osquery {
namespace tables {

// Linux proc protocol define to net stats file name.
const std::map<int, std::string> kLinuxProtocolNames = {
    {IPPROTO_ICMP, "icmp"},
    {IPPROTO_TCP, "tcp"},
    {IPPROTO_UDP, "udp"},
    {IPPROTO_UDPLITE, "udplite"},
    {IPPROTO_RAW, "raw"},
};

std::string addressFromHex(const std::string &encoded_address, int family) {
  char addr_buffer[INET6_ADDRSTRLEN] = {0};
  if (family == AF_INET) {
    struct in_addr decoded;
    if (encoded_address.length() == 8) {
      sscanf(encoded_address.c_str(), "%X", &(decoded.s_addr));
      inet_ntop(AF_INET, &decoded, addr_buffer, INET_ADDRSTRLEN);
    }
  } else if (family == AF_INET6) {
    struct in6_addr decoded;
    if (encoded_address.length() == 32) {
      sscanf(encoded_address.c_str(),
             "%8x%8x%8x%8x",
             (unsigned int *)&(decoded.s6_addr[0]),
             (unsigned int *)&(decoded.s6_addr[4]),
             (unsigned int *)&(decoded.s6_addr[8]),
             (unsigned int *)&(decoded.s6_addr[12]));
      inet_ntop(AF_INET6, &decoded, addr_buffer, INET6_ADDRSTRLEN);
    }
  }

  return TEXT(addr_buffer);
}

unsigned short portFromHex(const std::string &encoded_port) {
  unsigned short decoded = 0;
  if (encoded_port.length() == 4) {
    sscanf(encoded_port.c_str(), "%hX", &decoded);
  }
  return decoded;
}

void genSocketsFromProc(const std::map<std::string, std::string> &socket_inodes,
                        int protocol,
                        int family,
                        QueryData &results) {
  std::string path = "/proc/net/";
  path += kLinuxProtocolNames.at(protocol);
  path += (family == AF_INET6) ? "6" : "";

  std::string content;
  if (!osquery::readFile(path, content).ok()) {
    // Could not open socket information from /proc.
    return;
  }

  // The system's socket information is tokenized by line.
  size_t index = 0;
  for (const auto &line : osquery::split(content, "\n")) {
    index += 1;
    if (index == 1) {
      // The first line is a textual header and will be ignored.
      if (line.find("sl") != 0) {
        // Header fields are unknown, stop parsing.
        break;
      }
      continue;
    }

    // The socket information is tokenized by spaces, each a field.
    auto fields = osquery::split(line, " ");
    if (fields.size() < 10) {
      // Unknown/malformed socket information.
      continue;
    }

    // Two of the fields are the local/remote address/port pairs.
    auto locals = osquery::split(fields[1], ":");
    auto remotes = osquery::split(fields[2], ":");
    if (locals.size() != 2 || remotes.size() != 2) {
      // Unknown/malformed socket information.
      continue;
    }

    Row r;
    r["socket"] = fields[9];
    r["family"] = INTEGER(family);
    r["protocol"] = INTEGER(protocol);
    r["local_address"] = addressFromHex(locals[0], family);
    r["local_port"] = INTEGER(portFromHex(locals[1]));
    r["remote_address"] = addressFromHex(remotes[0], family);
    r["remote_port"] = INTEGER(portFromHex(remotes[1]));

    if (socket_inodes.count(r["socket"]) > 0) {
      r["pid"] = socket_inodes.at(r["socket"]);
    } else {
      r["pid"] = "-1";
    }

    results.push_back(r);
  }
}

QueryData genOpenSockets(QueryContext &context) {
  QueryData results;

  // If a pid is given then set that as the only item in processes.
  std::set<std::string> pids;
  if (context.constraints["pid"].exists()) {
    pids = context.constraints["pid"].getAll(EQUALS);
  } else {
    osquery::procProcesses(pids);
  }

  // Generate a map of socket inode to process tid.
  std::map<std::string, std::string> socket_inodes;
  for (const auto &process : pids) {
    std::map<std::string, std::string> descriptors;
    if (osquery::procDescriptors(process, descriptors).ok()) {
      for (const auto& fd : descriptors) {
        if (fd.second.find("socket:") != std::string::npos) {
          // See #792: std::regex is incomplete until GCC 4.9
          auto inode = fd.second.substr(fd.second.find("socket:") + 8);
          socket_inodes[inode.substr(0, inode.size() - 1)] = process;
        }
      }
    }
  }

  // This used to use netlink (Ref: #1094) to request socket information.
  // Use proc messages to query socket information.
  for (const auto &protocol : kLinuxProtocolNames) {
    genSocketsFromProc(socket_inodes, protocol.first, AF_INET, results);
    genSocketsFromProc(socket_inodes, protocol.first, AF_INET6, results);
  }

  return results;
}
}
}
