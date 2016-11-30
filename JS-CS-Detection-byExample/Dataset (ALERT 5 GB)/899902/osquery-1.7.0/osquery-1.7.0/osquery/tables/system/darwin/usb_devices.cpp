/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <IOKit/usb/IOUSBLib.h>

#include <osquery/tables.h>

#include "osquery/tables/system/darwin/iokit_utils.h"

namespace osquery {
namespace tables {

void genUSBDevice(const io_service_t& device, QueryData& results) {
  Row r;

  // Get the device details
  CFMutableDictionaryRef details;
  IORegistryEntryCreateCFProperties(
      device, &details, kCFAllocatorDefault, kNilOptions);

  r["usb_address"] = getIOKitProperty(details, "USB Address");
  r["usb_port"] = getIOKitProperty(details, "PortNum");

  r["model"] = getIOKitProperty(details, "USB Product Name");
  if (r.at("model").size() == 0) {
    // Could not find the model name from IOKit, use the label.
    io_name_t name;
    if (IORegistryEntryGetName(device, name) == KERN_SUCCESS) {
      r["model"] = std::string(name);
    }
  }

  r["model_id"] = getIOKitProperty(details, "idProduct");
  r["vendor"] = getIOKitProperty(details, "USB Vendor Name");
  r["vendor_id"] = getIOKitProperty(details, "idVendor");

  r["serial"] = getIOKitProperty(details, "USB Serial Number");
  if (r.at("serial").size() == 0) {
    r["serial"] = getIOKitProperty(details, "iSerialNumber");
  }

  auto non_removable = getIOKitProperty(details, "non-removable");
  r["removable"] = (non_removable == "yes") ? "0" : "1";

  if (r.at("vendor_id").size() > 0 && r.at("model_id").size() > 0) {
    // Only add the USB device on OS X if it contains a Vendor and Model ID.
    // On OS X 10.11 the simulation hubs are PCI devices within IOKit and
    // lack the useful USB metadata.
    idToHex(r["vendor_id"]);
    idToHex(r["model_id"]);
    results.push_back(r);
  }
  CFRelease(details);
}

QueryData genUSBDevices(QueryContext& context) {
  QueryData results;

  auto matching = IOServiceMatching(kIOUSBDeviceClassName);
  if (matching == nullptr) {
    // No devices matched USB, very odd.
    return results;
  }

  io_iterator_t it;
  auto kr = IOServiceGetMatchingServices(kIOMasterPortDefault, matching, &it);
  if (kr != KERN_SUCCESS) {
    return results;
  }

  io_service_t device;
  while ((device = IOIteratorNext(it))) {
    genUSBDevice(device, results);
    IOObjectRelease(device);
  }

  IOObjectRelease(it);
  return results;
}
}
}
