/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#pragma once

#include "osquery/tables/system/smbios_utils.h"

namespace osquery {
namespace tables {

/**
 * @brief A flexible SMBIOS parser for Darwin.
 *
 * The parsing work is within SMBIOSParser and is shared between platforms.
 * Each OS should implement a discover and set method that implements the
 * OS-specific SMBIOS facilities.
 *
 * On Darwin the SMBIOS data is kept in the DeviceTree IOKit registry.
 */
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
}
}
