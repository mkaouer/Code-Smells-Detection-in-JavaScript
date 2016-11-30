/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <CoreFoundation/CoreFoundation.h>
#include <IOKit/IOKitLib.h>

#include <osquery/core.h>
#include <osquery/logger.h>
#include <osquery/tables.h>

#include "osquery/core/conversions.h"

namespace osquery {
namespace tables {

#define kIODTOptionsPath_ "IODeviceTree:/options"

void genVariable(const void *key, const void *value, void *results) {
  Row nvram_row;
  std::string value_string;

  // OF variable canonical type casting.
  CFTypeID type_id;
  CFStringRef type_description;

  // Variable name is the dictionary key.
  nvram_row["name"] = stringFromCFString((CFStringRef)key);

  // Variable type will be defined by the CF type.
  type_id = CFGetTypeID(value);
  type_description = CFCopyTypeIDDescription(type_id);
  nvram_row["type"] = stringFromCFString(type_description);
  CFRelease(type_description);

  // Based on the type, get a texual representation of the variable.
  if (type_id == CFBooleanGetTypeID()) {
    value_string = (CFBooleanGetValue((CFBooleanRef)value)) ? "true" : "false";
  } else if (type_id == CFNumberGetTypeID()) {
    value_string = stringFromCFNumber((CFDataRef)value);
  } else if (type_id == CFStringGetTypeID()) {
    value_string = stringFromCFString((CFStringRef)value);
  } else if (type_id == CFDataGetTypeID()) {
    value_string = stringFromCFData((CFDataRef)value);
  } else {
    // Unknown result type, do not attempt to decode/format.
    value_string = "<INVALID>";
  }

  // Finally, add the variable value to the row.
  nvram_row["value"] = value_string;
  ((QueryData *)results)->push_back(nvram_row);
}

QueryData genNVRAM(QueryContext &context) {
  QueryData results;

  kern_return_t status;
  mach_port_t master_port;
  io_registry_entry_t options_ref;

  auto kr = IOMasterPort(bootstrap_port, &master_port);
  if (kr != KERN_SUCCESS) {
    VLOG(1) << "Could not get the IOMaster port";
    return {};
  }

  // NVRAM registry entry is :/options.
  auto options = IORegistryEntryFromPath(master_port, kIODTOptionsPath_);
  if (options == 0) {
    VLOG(1) << "NVRAM is not supported on this system";
    return {};
  }

  CFMutableDictionaryRef options_dict;
  kr = IORegistryEntryCreateCFProperties(options, &options_dict, 0, 0);
  if (kr != KERN_SUCCESS) {
    VLOG(1) << "Could not get NVRAM properties";
  } else {
    CFDictionaryApplyFunction(options_dict, &genVariable, &results);
  }

  // Cleanup (registry entry context).
  CFRelease(options_dict);
  IOObjectRelease(options);
  return results;
}
}
}
