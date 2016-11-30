/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <osquery/tables.h>

#if defined(__APPLE__)
  #include <time.h>
  #include <errno.h>
  #include <sys/sysctl.h>
#elif defined(__linux__)
  #include <sys/sysinfo.h>
#endif

namespace osquery {
namespace tables {

long getUptime() {
  #if defined(DARWIN)
    struct timeval boot_time;
    size_t len = sizeof(boot_time);
    int mib[2] = {
        CTL_KERN,
        KERN_BOOTTIME
    };

    if (sysctl(mib, 2, &boot_time, &len, NULL, 0) < 0) {
        return -1;
    }

    time_t seconds_since_boot = boot_time.tv_sec;
    time_t current_seconds = time(NULL);

    return long(difftime(current_seconds, seconds_since_boot));
  #elif defined(__linux__)
    struct sysinfo sys_info;

    if (sysinfo(&sys_info) != 0) {
      return -1;
    }

    return sys_info.uptime;
  #endif

  return -1;
}

QueryData genUptime(QueryContext& context) {
  Row r;
  QueryData results;
  long uptime_in_seconds = getUptime();

  if (uptime_in_seconds >= 0) {
    r["days"] = INTEGER(uptime_in_seconds / 60 / 60 / 24);
    r["hours"] = INTEGER((uptime_in_seconds / 60 / 60) % 24);
    r["minutes"] = INTEGER((uptime_in_seconds / 60) % 60);
    r["seconds"] = INTEGER(uptime_in_seconds % 60);
    r["total_seconds"] = BIGINT(uptime_in_seconds);
    results.push_back(r);
  }

  return results;
}
}
}
