/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant 
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <osquery/core.h>
#include <osquery/logger.h>
#include <osquery/tables.h>

#include "osquery/events/linux/udev.h"

namespace osquery {
namespace tables {

const std::string kUSBKeyVendorID = "ID_VENDOR_ID";
const std::string kUSBKeyVendor = "ID_VENDOR_FROM_DATABASE";
const std::string kUSBKeyModelID = "ID_MODEL_ID";
const std::string kUSBKeyModel = "ID_MODEL_FROM_DATABASE";
const std::string kUSBKeyDriver = "ID_USB_DRIVER";
const std::string kUSBKeySubsystem = "SUBSYSTEM";
const std::string kUSBKeySerial = "ID_SERIAL_SHORT";
const std::string kUSBKeyAddress = "BUSNUM";
const std::string kUSBKeyPort = "DEVNUM";

QueryData genUSBDevices(QueryContext &context) {
  QueryData results;

  auto udev_handle = udev_new();
  if (udev_handle == nullptr) {
    VLOG(1) << "Could not get udev handle.";
    return results;
  }

  // Perform enumeration/search.
  auto enumerate = udev_enumerate_new(udev_handle);
  udev_enumerate_add_match_subsystem(enumerate, "usb");
  udev_enumerate_scan_devices(enumerate);

  // Get list entries and iterate over entries.
  struct udev_list_entry *device_entries, *entry;
  device_entries = udev_enumerate_get_list_entry(enumerate);

  udev_list_entry_foreach(entry, device_entries) {
    const char *path = udev_list_entry_get_name(entry);
    auto device = udev_device_new_from_syspath(udev_handle, path);

    Row r;
    // r["driver"] = UdevEventPublisher::getValue(device, kUSBKeyDriver);
    r["vendor"] = UdevEventPublisher::getValue(device, kUSBKeyVendor);
    r["model"] = UdevEventPublisher::getValue(device, kUSBKeyModel);

    // USB-specific vendor/model ID properties.
    r["model_id"] = UdevEventPublisher::getValue(device, kUSBKeyModelID);
    r["vendor_id"] = UdevEventPublisher::getValue(device, kUSBKeyVendorID);
    r["serial"] = UdevEventPublisher::getValue(device, kUSBKeySerial);

    // Address/port accessors.
    r["usb_address"] = UdevEventPublisher::getValue(device, kUSBKeyAddress);
    r["usb_port"] = UdevEventPublisher::getValue(device, kUSBKeyPort);

    // Removable detection.
    auto removable = UdevEventPublisher::getAttr(device, "removable");
    if (removable == "unknown") {
      r["removable"] = "-1";
    } else {
      r["removable"] = "1";
    }

    if (r["usb_address"].size() > 0 && r["usb_port"].size() > 0) {
      results.push_back(r);
    }
    udev_device_unref(device);
  }

  // Drop references to udev structs.
  udev_enumerate_unref(enumerate);
  udev_unref(udev_handle);

  return results;
}
}
}
