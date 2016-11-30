/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <boost/algorithm/string.hpp>

#include <osquery/filesystem.h>
#include <osquery/tables.h>
#include <osquery/sql.h>

#include "osquery/tables/system/linux/smbios_utils.h"

namespace osquery {
namespace tables {

QueryData genSystemInfo(QueryContext& context) {
  Row r;
  r["hostname"] = osquery::getHostname();
  r["computer_name"] = r["hostname"];

  std::string uuid;
  r["uuid"] = (osquery::getHostUUID(uuid)) ? uuid : "";

  auto qd = SQL::selectAllFrom("cpuid");
  for (const auto& row : qd) {
    if (row.at("feature") == "product_name") {
      r["cpu_brand"] = row.at("value");
      boost::trim(r["cpu_brand"]);
    }
  }

  // Can parse /proc/cpuinfo or /proc/meminfo for this data.
  static long cores = sysconf(_SC_NPROCESSORS_CONF);
  if (cores > 0) {
    r["cpu_logical_cores"] = INTEGER(cores);
    r["cpu_physical_cores"] = INTEGER(cores);
  } else {
    r["cpu_logical_cores"] = "-1";
    r["cpu_physical_cores"] = "-1";
  }

  static long pages = sysconf(_SC_PHYS_PAGES);
  static long pagesize = sysconf(_SC_PAGESIZE);

  if (pages > 0 && pagesize > 0) {
    r["physical_memory"] = BIGINT((long long)pages * (long long)pagesize);
  } else {
    r["physical_memory"] = "-1";
  }

  r["cpu_type"] = "0";
  r["cpu_subtype"] = "0";

  // Read the types from CPU info within proc.
  std::string content;
  if (readFile("/proc/cpuinfo", content)) {
    for (const auto& line : osquery::split(content, "\n")) {
      // Iterate each line and look for labels (there is also a model type).
      if (line.find("cpu family") == 0 || line.find("model\t") == 0) {
        auto details = osquery::split(line, ":");
        if (details.size() == 2) {
          // Not the most elegant but prevents us from splitting each line.
          r[(line[0] == 'c') ? "cpu_type" : "cpu_subtype"] = details[1];
        }
      }
    }
  }

  {
    LinuxSMBIOSParser parser;
    if (!parser.discover()) {
      r["hardware_model"] = "";
    } else {
      parser.tables(([&r](size_t index,
                          const SMBStructHeader* hdr,
                          uint8_t* address,
                          size_t size) {
        if (hdr->type != kSMBIOSTypeSystem || size < 0x12) {
          return;
        }

        uint8_t* data = address + hdr->length;
        r["hardware_vendor"] = dmiString(data, address, 0x04);
        r["hardware_model"] = dmiString(data, address, 0x05);
        r["hardware_version"] = dmiString(data, address, 0x06);
        r["hardware_serial"] = dmiString(data, address, 0x07);
      }));
    }
  }

  return {r};
}
}
}
