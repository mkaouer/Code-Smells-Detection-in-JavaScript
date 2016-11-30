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

#include <blkid/blkid.h>
#include <libudev.h>
#include <unistd.h>

#include <osquery/core.h>
#include <osquery/filesystem.h>
#include <osquery/logger.h>
#include <osquery/tables.h>

namespace osquery {
namespace tables {

static void getBlockDevice(struct udev_device *dev, QueryData &results) {
  Row r;
  const char *name = udev_device_get_devnode(dev);
  if (name == nullptr) {
    // Cannot get devnode information from UDEV.
    return;
  }

  // The device name may be blank but will have a string value.
  r["name"] = name;

  struct udev_device *subdev =
      udev_device_get_parent_with_subsystem_devtype(dev, "block", nullptr);
  if (subdev != nullptr) {
    r["parent"] = udev_device_get_devnode(subdev);
  }

  const char *size = udev_device_get_sysattr_value(dev, "size");
  if (size != nullptr) {
    r["size"] = size;
  }

  subdev = udev_device_get_parent_with_subsystem_devtype(dev, "scsi", nullptr);
  if (subdev != nullptr) {
    const char *model = udev_device_get_sysattr_value(subdev, "model");
    std::string model_string = std::string(model);
    boost::algorithm::trim(model_string);
    r["model"] = model_string;

    model = udev_device_get_sysattr_value(subdev, "vendor");
    model_string = std::string(model);
    boost::algorithm::trim(model_string);
    r["vendor"] = model_string;
  }

  blkid_probe pr = blkid_new_probe_from_filename(name);
  if (pr != nullptr) {
    blkid_probe_enable_superblocks(pr, 1);
    blkid_probe_set_superblocks_flags(
        pr, BLKID_SUBLKS_LABEL | BLKID_SUBLKS_UUID | BLKID_SUBLKS_TYPE);

    if (!blkid_do_safeprobe(pr)) {
      const char *blk_value = nullptr;
      if (!blkid_probe_lookup_value(pr, "TYPE", &blk_value, nullptr)) {
        r["type"] = blk_value;
      }
      if (!blkid_probe_lookup_value(pr, "UUID", &blk_value, nullptr)) {
        r["uuid"] = blk_value;
      }
      if (!blkid_probe_lookup_value(pr, "LABEL", &blk_value, nullptr)) {
        r["label"] = blk_value;
      }
    }
    blkid_free_probe(pr);
  }

  results.push_back(r);
}

QueryData genBlockDevs(QueryContext &context) {
  if (getuid() || geteuid()) {
    VLOG(1) << "Not running as root, some column data not available";
  }

  QueryData results;

  struct udev *udev = udev_new();
  if (udev == nullptr) {
    return {};
  }

  struct udev_enumerate *enumerate = udev_enumerate_new(udev);
  udev_enumerate_add_match_subsystem(enumerate, "block");
  udev_enumerate_scan_devices(enumerate);

  struct udev_list_entry *devices, *dev_list_entry;
  devices = udev_enumerate_get_list_entry(enumerate);
  udev_list_entry_foreach(dev_list_entry, devices) {
    const char *path = udev_list_entry_get_name(dev_list_entry);
    struct udev_device *dev = udev_device_new_from_syspath(udev, path);
    if (path != nullptr && dev != nullptr) {
      getBlockDevice(dev, results);
    }
    udev_device_unref(dev);
  }

  udev_enumerate_unref(enumerate);
  udev_unref(udev);

  return results;
}
}
}
