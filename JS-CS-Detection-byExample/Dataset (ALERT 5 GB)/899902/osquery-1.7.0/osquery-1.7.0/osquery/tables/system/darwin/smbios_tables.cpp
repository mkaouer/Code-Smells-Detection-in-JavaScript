/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <iomanip>
#include <sstream>

#include <CoreFoundation/CoreFoundation.h>
#include <IOKit/IOKitLib.h>

#include <boost/algorithm/string.hpp>

#include <osquery/tables.h>

#include "osquery/tables/system/smbios_utils.h"
#include "osquery/tables/system/darwin/iokit_utils.h"

namespace osquery {
namespace tables {

#define kIOSMBIOSClassName_ "AppleSMBIOS"
#define kIOSMBIOSPropertyName_ "SMBIOS"
#define kIOSMBIOSEPSPropertyName_ "SMBIOS-EPS"

class DarwinSMBIOSParser : public SMBIOSParser {
 public:
  void setData(uint8_t* tables, size_t length) {
    table_data_ = tables;
    table_size_ = length;
  }

  bool discover();

  ~DarwinSMBIOSParser() {
    if (smbios_data_ != nullptr) {
      free(smbios_data_);
    }
  }

 private:
  uint8_t* smbios_data_{nullptr};
};

bool DarwinSMBIOSParser::discover() {
  auto matching = IOServiceMatching(kIOSMBIOSClassName_);
  if (matching == nullptr) {
    // No ACPI platform expert service found.
    return false;
  }

  auto service = IOServiceGetMatchingService(kIOMasterPortDefault, matching);
  if (service == 0) {
    return false;
  }

  // Unlike ACPI the SMBIOS property will return several structures
  // followed by a table of structured entries (also called tables).
  // http://dmtf.org/sites/default/files/standards/documents/DSP0134_2.8.0.pdf
  CFTypeRef smbios = IORegistryEntryCreateCFProperty(
      service, CFSTR(kIOSMBIOSPropertyName_), kCFAllocatorDefault, 0);
  if (smbios == nullptr) {
    IOObjectRelease(service);
    return false;
  }

  // Check the first few SMBIOS structures before iterating through tables.
  const uint8_t* smbios_data = CFDataGetBytePtr((CFDataRef)smbios);
  size_t length = CFDataGetLength((CFDataRef)smbios);

  if (smbios_data == nullptr || length == 0) {
    // Problem creating SMBIOS property.
    CFRelease(smbios);
    IOObjectRelease(service);
    return false;
  }

  smbios_data_ = (uint8_t*)malloc(length);
  if (smbios_data_ != nullptr) {
    memcpy(smbios_data_, smbios_data, length);
  }
  IOObjectRelease(service);
  CFRelease(smbios);

  // The property and service exist.
  setData(const_cast<uint8_t*>(smbios_data_), length);
  return (smbios_data_ != nullptr);
}

QueryData genSMBIOSTables(QueryContext& context) {
  QueryData results;

  // Parse structures.
  DarwinSMBIOSParser parser;
  if (parser.discover()) {
    parser.tables(([&results](size_t index,
                              const SMBStructHeader* hdr,
                              uint8_t* address,
                              size_t size) {
      genSMBIOSTable(index, hdr, address, size, results);
    }));
  }
  return results;
}

QueryData genPlatformInfo(QueryContext& context) {
  auto entry =
      IORegistryEntryFromPath(kIOMasterPortDefault, "IODeviceTree:/rom@0");
  if (entry == MACH_PORT_NULL) {
    return {};
  }

  // Get the device details
  CFMutableDictionaryRef details = nullptr;
  IORegistryEntryCreateCFProperties(
      entry, &details, kCFAllocatorDefault, kNilOptions);

  QueryData results;
  if (details != nullptr) {
    Row r;
    r["vendor"] = getIOKitProperty(details, "vendor");
    r["volume_size"] = getIOKitProperty(details, "fv-main-size");
    r["size"] = getIOKitProperty(details, "rom-size");
    r["date"] = getIOKitProperty(details, "release-date");
    r["version"] = getIOKitProperty(details, "version");

    {
      auto address = getIOKitProperty(details, "fv-main-address");
      auto value = boost::lexical_cast<size_t>(address);

      std::stringstream hex_id;
      hex_id << std::hex << std::setw(8) << std::setfill('0') << value;
      r["address"] = "0x" + hex_id.str();
    }

    {
      std::vector<std::string> extra_items;
      auto info = getIOKitProperty(details, "apple-rom-info");
      std::vector<std::string> info_lines;
      iter_split(info_lines, info, boost::algorithm::first_finder("%0a"));
      for (const auto& line : info_lines) {
        std::vector<std::string> details;
        iter_split(details, line, boost::algorithm::first_finder(": "));
        if (details.size() > 1) {
          boost::trim(details[1]);
          if (details[0].find("Revision") != std::string::npos) {
            r["revision"] = details[1];
          }
          extra_items.push_back(details[1]);
        }
      }
      r["extra"] = osquery::join(extra_items, "; ");
    }

    results.push_back(r);
    CFRelease(details);
  }

  IOObjectRelease(entry);
  return results;
}
}
}
