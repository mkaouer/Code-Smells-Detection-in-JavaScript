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

#include <osquery/core.h>
#include <osquery/filesystem.h>
#include <osquery/logger.h>
#include <osquery/tables.h>

#include "osquery/core/conversions.h"

namespace fs = boost::filesystem;
namespace pt = boost::property_tree;

namespace osquery {
namespace tables {

const size_t kPreferenceDepthLimit = 20;

struct TRowResults {
  const Row* const base;
  QueryData* const results;
  size_t depth;

  TRowResults(const Row& r, QueryData& q, size_t d)
      : base(&r), results(&q), depth(d) {}
};

// Walk the supported preference value structures.
void genOSXPrefValues(const CFTypeRef& value,
                      const Row& base,
                      QueryData& results,
                      size_t depth);

void genOSXHashPref(const void* key, const void* value, void* tref) {
  if (key == nullptr || value == nullptr || tref == nullptr) {
    // Paranoia: don't expect the callback application to yield nullptrs.
    return;
  }

  const TRowResults* trow = (const TRowResults*)tref;
  Row r = *(trow->base);
  if (CFGetTypeID((CFTypeRef)key) == CFStringGetTypeID()) {
    if (r["subkey"].size() > 0) {
      r["subkey"] += "/";
    }
    // The subkey is the hash map key.
    r["subkey"] += stringFromCFString((CFStringRef)key);
  }
  genOSXPrefValues((CFTypeRef)value, r, *(trow->results), trow->depth);
}

void genOSXListPref(const CFArrayRef& list,
                    const Row& base,
                    QueryData& results,
                    size_t depth) {
  // Iterate over a preference value that contains a list structure.
  for (CFIndex j = 0; j < CFArrayGetCount(list); ++j) {
    Row r = base;
    CFTypeRef value = (CFTypeRef)CFArrayGetValueAtIndex(list, j);
    if (r["subkey"].size() > 0) {
      r["subkey"] += "/";
    }
    // The subkey is the index into the list.
    r["subkey"] += std::to_string(j);
    genOSXPrefValues(value, r, results, depth);
  }
}

void genOSXPrefValues(const CFTypeRef& value,
                      const Row& base,
                      QueryData& results,
                      size_t depth) {
  if (value == nullptr) {
    return;
  }

  // Since we recurse when parsing Arrays/Dicts, monitor stack limits.
  if (++depth > kPreferenceDepthLimit) {
    TLOG << "OS X Preference: " << base.at("domain")
         << " exceeded subkey depth limit: " << kPreferenceDepthLimit;
    return;
  }

  // Emit a string representation for each preference type.
  Row r = base;
  if (CFGetTypeID(value) == CFNumberGetTypeID()) {
    r["value"] = stringFromCFNumber((CFDataRef)value);
  } else if (CFGetTypeID(value) == CFStringGetTypeID()) {
    r["value"] = stringFromCFString((CFStringRef)value);
  } else if (CFGetTypeID(value) == CFBooleanGetTypeID()) {
    r["value"] =
        (CFBooleanGetValue((CFBooleanRef)value) == TRUE) ? "true" : "false";
  } else if (CFGetTypeID(value) == CFDataGetTypeID()) {
    // Do not include data preferences.
  } else if (CFGetTypeID(value) == CFArrayGetTypeID()) {
    genOSXListPref((CFArrayRef)value, base, results, depth);
    return;
  } else if (CFGetTypeID(value) == CFDictionaryGetTypeID()) {
    // Generate a row for each hash key.
    TRowResults trow(base, results, depth);
    CFDictionaryApplyFunction((CFDictionaryRef)value, &genOSXHashPref, &trow);
    return;
  }

  results.push_back(std::move(r));
}

void genOSXDomainPrefs(const CFStringRef& domain, QueryData& results) {
  CFArrayRef keys = CFPreferencesCopyKeyList(
      domain, kCFPreferencesCurrentUser, kCFPreferencesAnyHost);
  if (keys == nullptr) {
    return;
  }

  CFDictionaryRef values = CFPreferencesCopyMultiple(
      keys, domain, kCFPreferencesCurrentUser, kCFPreferencesAnyHost);
  if (values == nullptr) {
    CFRelease(keys);
    return;
  }

  // Iterate over each preference domain's preference name.
  for (CFIndex j = 0; j < CFArrayGetCount(keys); ++j) {
    Row r;
    r["domain"] = stringFromCFString(domain);
    CFStringRef key = (CFStringRef)CFArrayGetValueAtIndex(keys, j);
    if (CFStringGetTypeID() != CFGetTypeID(key)) {
      continue;
    }

    // Interesting results/behavior from Microsoft products.
    r["key"] = stringFromCFString(key);
    if (r.at("key").find(">") != std::string::npos ||
        r.at("key").find("<") != std::string::npos || r.at("key").size() == 0) {
      continue;
    }

    // Check if the preference key is managed by a profile.
    r["forced"] = (CFPreferencesAppValueIsForced(key, domain)) ? "1" : "0";

    // Check the key and key type (which may be any CF type).
    CFTypeRef value = (CFTypeRef)CFDictionaryGetValue(values, key);
    genOSXPrefValues(value, r, results, 0);
  }

  CFRelease(values);
  CFRelease(keys);
}

void genOSXDefaultPreferences(QueryContext& context, QueryData& results) {
  CFArrayRef app_map = nullptr;

  if (context.constraints["domain"].exists(EQUALS)) {
    // If a specific domain is requested, speed up the set of type conversions.
    auto domains = context.constraints["domain"].getAll(EQUALS);
    app_map = (CFArrayRef)CFArrayCreateMutable(
        kCFAllocatorDefault, domains.size(), &kCFTypeArrayCallBacks);
    for (const auto& domain : domains) {
      auto cf_domain = CFStringCreateWithCString(kCFAllocatorDefault,
                                                 domain.c_str(),
                                                 kCFStringEncodingASCII);
      CFArrayAppendValue((CFMutableArrayRef)app_map, cf_domain);
      CFRelease(cf_domain);
    }
  } else {
    // Listing ALL application preferences is deprecated.
    OSQUERY_USE_DEPRECATED(
        app_map = CFPreferencesCopyApplicationList(kCFPreferencesCurrentUser,
                                                   kCFPreferencesAnyHost););
  }

  if (app_map == nullptr) {
    return;
  }

  // Iterate over each preference domain (applicationID).
  for (CFIndex i = 0; i < CFArrayGetCount(app_map); ++i) {
    CFStringRef domain = (CFStringRef)CFArrayGetValueAtIndex(app_map, i);
    genOSXDomainPrefs(domain, results);
  }

  CFRelease(app_map);
}

void genOSXPlistPrefValue(const pt::ptree& tree,
                          const Row& base,
                          QueryData& results) {
  if (tree.empty()) {
    Row r = base;
    r["value"] = tree.data();
    results.push_back(std::move(r));
    // No more levels to parse.
    return;
  }

  for (const auto& item : tree) {
    Row r = base;
    if (r["subkey"].size() > 0) {
      r["subkey"] += "/";
    }
    r["subkey"] += item.first;
    genOSXPlistPrefValue(item.second, r, results);
  }
}

void genOSXPlistPreferences(const std::string& path, QueryData& results) {
  if (!pathExists(path).ok() || !isReadable(path).ok()) {
    VLOG(1) << "Cannot find/read defaults plist from path: " + path;
    return;
  }

  pt::ptree tree;
  if (!osquery::parsePlist(path, tree).ok()) {
    VLOG(1) << "Could not parse plist: " + path;
    return;
  }

  std::string filename = fs::path(path).filename().string();
  for (const auto& item : tree) {
    Row r;
    if (filename.substr(filename.size() - 6) == ".plist") {
      r["domain"] = filename.substr(0, filename.size() - 6);
    } else {
      r["domain"] = filename;
    }

    r["path"] = path;
    r["key"] = item.first;
    r["forced"] = "0";
    r["subkey"] = "";
    genOSXPlistPrefValue(item.second, r, results);
  }
}

QueryData genOSXPreferences(QueryContext& context) {
  QueryData results;

  if (context.constraints["path"].exists(EQUALS)) {
    // Read preferences from a plist at path.
    auto paths = context.constraints["path"].getAll(EQUALS);
    for (const auto& path : paths) {
      genOSXPlistPreferences(path, results);
    }
  } else {
    genOSXDefaultPreferences(context, results);
  }

  return std::move(results);
}
}
}
