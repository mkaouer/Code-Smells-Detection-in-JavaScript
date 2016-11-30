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

#include <CoreFoundation/CoreFoundation.h>
#include <Foundation/Foundation.h>

#include <osquery/filesystem.h>
#include <osquery/logger.h>
#include <osquery/sql.h>
#include <osquery/tables.h>

#include "osquery/core/conversions.h"

namespace osquery {
namespace tables {

static const std::string kAirPortPreferencesPath =
    "/Library/Preferences/SystemConfiguration/"
    "com.apple.airport.preferences.plist";

const std::map<std::string, std::string> kKnownWifiNetworkKeys = {
    {"ssid", "SSID"},
    {"network_name", "SSIDString"},
    {"security_type", "SecurityType"},
    {"roaming_profile", "RoamingProfileType"},
    {"auto_login", "AutoLogin"},
    {"last_connected", "LastConnected"},
    {"captive_portal", "Captive"},
    {"roaming", "SPRoaming"},
    {"passpoint", "Passpoint"},
    {"possibly_hidden", "PossiblyHiddenNetwork"},
    {"disabled", "Disabled"},
    {"temporarily_disabled", "TemporarilyDisabled"}};

// Check if we are running on OS X 10.9, where the key in plist is different
Status getKnownNetworksKey(std::string& key) {
  auto qd = SQL::selectAllFrom("os_version");
  if (qd.size() != 1) {
    return Status(-1, "Couldn't determine OS X version");
  }

  key = (qd.front().at("major") == "10" && qd.front().at("minor") == "9")
            ? "RememberedNetworks"
            : "KnownNetworks";
  return Status(0, "ok");
}

// SSIDs have no character set associated with them
// mirror Apple's representation of them
std::string extractSsid(const CFDataRef& data) {
  std::stringstream ss;
  auto bytes = CFDataGetBytePtr(data);
  auto length = CFDataGetLength(data);
  for (CFIndex i = 0; i < length; i++) {
    if (i > 0 && i % 4 == 0) {
      ss << " ";
    }
    ss << std::setfill('0') << std::setw(2) << std::hex
       << (unsigned int)bytes[i];
  }
  return ss.str();
}

std::string extractNetworkProperties(const CFTypeRef& property) {
  if (CFGetTypeID(property) == CFDataGetTypeID()) {
    return extractSsid((CFDataRef)property);
  } else if (CFGetTypeID(property) == CFDateGetTypeID()) {
    auto unix_time = CFDateGetAbsoluteTime((CFDateRef)property) +
                     kCFAbsoluteTimeIntervalSince1970;
    return INTEGER(std::llround(unix_time));
  } else if (CFGetTypeID(property) == CFBooleanGetTypeID()) {
    return (CFBooleanGetValue((CFBooleanRef)property)) ? INTEGER(1)
                                                       : INTEGER(0);
  } else if (CFGetTypeID(property) == CFStringGetTypeID()) {
    return stringFromCFString((CFStringRef)property);
  } else {
    return ""; // Cannot determine CFTypeRef
  }
}

void parseNetworks(const CFDictionaryRef& network, QueryData& results) {
  if (network == nullptr || CFGetTypeID(network) != CFDictionaryGetTypeID() ||
      CFDictionaryGetCount(network) == 0) {
    return;
  }

  Row r;
  for (const auto& kv : kKnownWifiNetworkKeys) {
    auto key = CFStringCreateWithCString(kCFAllocatorDefault, kv.second.c_str(),
                                         kCFStringEncodingUTF8);
    CFTypeRef value = nullptr;
    if (key != nullptr) {
      if (CFDictionaryGetValueIfPresent(network, key, &value)) {
        r[kv.first] = extractNetworkProperties(value);
      }
      CFRelease(key);
    }
  }
  results.push_back(r);
}

QueryData genKnownWifiNetworks(QueryContext& context) {
  std::string key;
  auto status = getKnownNetworksKey(key);
  if (!status.ok()) {
    VLOG(1) << status.getMessage();
    return {};
  }

  boost::filesystem::path path = kAirPortPreferencesPath;
  if (!pathExists(path).ok()) {
    VLOG(1) << "Airport preferences file not found: "
            << kAirPortPreferencesPath;
    return {};
  }

  // drop privileges if needed
  auto dropper = DropPrivileges::get();
  dropper->dropToParent(path);

  if (!readFile(path)) {
    VLOG(1) << "Unable to read file: " << kAirPortPreferencesPath;
    return {};
  }

  auto plist = (__bridge CFDictionaryRef)[NSDictionary
      dictionaryWithContentsOfFile:@(kAirPortPreferencesPath.c_str())];
  if (plist == nullptr || CFDictionaryGetCount(plist) == 0) {
    return {};
  }
  auto cfkey = CFStringCreateWithCString(kCFAllocatorDefault, key.c_str(),
                                         kCFStringEncodingUTF8);
  CFTypeRef networks = CFDictionaryGetValue(plist, cfkey);
  CFRelease(cfkey);
  if (networks == nullptr) {
    VLOG(1) << "Key not found : " << key;
    return {};
  }

  QueryData results;
  if (CFGetTypeID(networks) == CFArrayGetTypeID()) {
    auto count = CFArrayGetCount((CFArrayRef)networks);
    for (CFIndex i = 0; i < count; i++) {
      parseNetworks(
          (CFDictionaryRef)CFArrayGetValueAtIndex((CFArrayRef)networks, i),
          results);
    }
  } else if (CFGetTypeID(networks) == CFDictionaryGetTypeID()) {
    auto count = CFDictionaryGetCount((CFDictionaryRef)networks);
    const void* keys[count];
    const void* values[count];
    CFDictionaryGetKeysAndValues((CFDictionaryRef)networks, keys, values);

    for (CFIndex i = 0; i < count; i++) {
      parseNetworks((CFDictionaryRef)values[i], results);
    }
  }
  return results;
}
}
}
