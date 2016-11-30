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

#include <boost/algorithm/string/trim.hpp>

#include <osquery/core.h>
#include <osquery/logger.h>
#include <osquery/tables.h>

extern "C" {
extern CFDictionaryRef OSKextCopyLoadedKextInfo(CFArrayRef, CFArrayRef);
}

namespace osquery {
namespace tables {

int getKextInt(const void *value, const CFStringRef key) {
  int result;
  auto num = (CFNumberRef)CFDictionaryGetValue((CFDictionaryRef)value, key);
  CFNumberGetValue(num, kCFNumberSInt32Type, (void *)&result);
  return result;
}

long long int getKextBigInt(const void *value, const CFStringRef key) {
  long long int result;
  auto num = (CFNumberRef)CFDictionaryGetValue((CFDictionaryRef)value, key);
  CFNumberGetValue(num, kCFNumberSInt64Type, (void *)&result);
  return result;
}

std::string getKextString(const void *value, const CFStringRef key) {
  std::string result;
  auto string = (CFStringRef)CFDictionaryGetValue((CFDictionaryRef)value, key);
  CFIndex length = CFStringGetLength(string) + 1;
  char *buffer = (char *)malloc(length);

  if (CFStringGetCString(string, buffer, length, kCFStringEncodingUTF8)) {
    result = std::string(buffer);
    boost::algorithm::trim(result);
  }

  if (buffer != nullptr) {
    free(buffer);
  }
  return result;
}

std::string getKextLinked(const void *value, const CFStringRef key) {
  std::string result;
  auto links = (CFArrayRef)CFDictionaryGetValue((CFDictionaryRef)value, key);
  if (links == nullptr) {
    // Very core.
    return result;
  }

  CFIndex count = CFArrayGetCount(links);
  if (count == 0) {
    // Odd error case, there was a linked value, but an empty list.
    return result;
  }

  auto link_indexes = CFArrayCreateMutableCopy(NULL, count, links);
  CFArraySortValues(link_indexes,
                    CFRangeMake(0, count),
                    (CFComparatorFunction)CFNumberCompare,
                    NULL);

  for (int i = 0; i < count; i++) {
    int link;
    CFNumberGetValue((CFNumberRef)CFArrayGetValueAtIndex(link_indexes, i),
                     kCFNumberSInt32Type,
                     (void *)&link);

    if (i > 0) {
      result += " ";
    }

    result += TEXT(link);
  }

  CFRelease(link_indexes);
  // Return in kextstat format for linked extensions.
  return "<" + result + ">";
}

QueryData genKernelExtensions(QueryContext &context) {
  QueryData results;

  // Populate dict of kernel extensions.
  CFDictionaryRef dict = OSKextCopyLoadedKextInfo(NULL, NULL);
  CFIndex count = CFDictionaryGetCount(dict);

  // Allocate memory for each extension parse.
  auto values = (void **)malloc(sizeof(void *) * count);
  CFDictionaryGetKeysAndValues(dict, nullptr, (const void **)values);
  for (CFIndex j = 0; j < count; j++) {
    // name
    auto name = getKextString(values[j], CFSTR("CFBundleIdentifier"));
    auto kextTag = getKextInt(values[j], CFSTR("OSBundleLoadTag"));

    // Possibly limit expensive lookups.
    if (!context.constraints["name"].matches(name)) {
      continue;
    }

    if (!context.constraints["idx"].matches<int>(kextTag)) {
      continue;
    }

    auto references = getKextInt(values[j], CFSTR("OSBundleRetainCount"));

    // size
    auto load_size = getKextBigInt(values[j], CFSTR("OSBundleLoadSize"));
    auto wired_size = getKextBigInt(values[j], CFSTR("OSBundleWiredSize"));
    auto version = getKextString(values[j], CFSTR("CFBundleVersion"));

    // linked_against
    auto linked = getKextLinked(values[j], CFSTR("OSBundleDependencies"));

    Row r;
    r["idx"] = INTEGER(kextTag);
    r["refs"] = INTEGER(references);
    r["size"] = BIGINT(load_size);
    r["wired"] = BIGINT(wired_size);
    r["name"] = name;
    r["version"] = version;
    r["linked_against"] = linked;
    results.push_back(r);
  }

  CFRelease(dict);
  free(values);
  return results;
}
}
}
