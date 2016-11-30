/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <string>
#include <fstream>
#include <map>

#include <stdlib.h>
#include <unistd.h>
#include <proc/readproc.h>

#include <boost/algorithm/string/trim.hpp>

#include <osquery/core.h>
#include <osquery/tables.h>
#include <osquery/filesystem.h>

namespace osquery {
namespace tables {

#ifdef PROC_EDITCMDLCVT
/// EDITCMDLCVT is available in libprocps3-dev
#define PROC_SELECTS                                                 \
  PROC_FILLCOM | PROC_EDITCMDLCVT | PROC_FILLMEM | PROC_FILLSTATUS | \
      PROC_FILLSTAT
#else
#define PROC_SELECTS \
  PROC_FILLCOM | PROC_FILLMEM | PROC_FILLSTATUS | PROC_FILLSTAT
#endif

std::string proc_name(const proc_t* proc_info) {
  return std::string(proc_info->cmd);
}

std::string proc_attr(const std::string& attr, const proc_t* proc_info) {
  std::stringstream filename;

  filename << "/proc/" << proc_info->tid << "/" << attr;
  return filename.str();
}

std::string proc_cmdline(const proc_t* proc_info) {
  std::string attr;
  std::string result;

  attr = proc_attr("cmdline", proc_info);
  std::ifstream fd(attr, std::ios::in | std::ios::binary);
  if (fd) {
    result = std::string(std::istreambuf_iterator<char>(fd),
                         std::istreambuf_iterator<char>());
    std::replace_if(
      result.begin(),
      result.end(),
      [](const char& c) { return c == 0; },
      ' ');
  }

  return result;
}

std::string proc_link(const proc_t* proc_info) {
  std::string attr;
  std::string result;
  char* link_path;
  long path_max;
  int bytes;

  // The exe is a symlink to the binary on-disk.
  attr = proc_attr("exe", proc_info);
  path_max = pathconf(attr.c_str(), _PC_PATH_MAX);
  link_path = (char*)malloc(path_max);

  memset(link_path, 0, path_max);
  bytes = readlink(attr.c_str(), link_path, path_max);
  if (bytes >= 0) {
    result = std::string(link_path);
  }

  free(link_path);
  return result;
}

std::map<std::string, std::string> proc_env(const proc_t* proc_info) {
  std::map<std::string, std::string> env;
  std::string attr = proc_attr("environ", proc_info);
  std::string buf;

  std::ifstream fd(attr, std::ios::in | std::ios::binary);

  while (!(fd.fail() || fd.eof())) {
    std::getline(fd, buf, '\0');
    size_t idx = buf.find_first_of("=");

    std::string key = buf.substr(0, idx);
    std::string value = buf.substr(idx + 1);

    env[key] = value;
  }
  return env;
}

/**
 * @brief deallocate the space allocated by readproc if the passed rbuf was NULL
 *
 * @param p The rbuf to free
 */
void standard_freeproc(proc_t* p) {
  if (!p) { // in case p is NULL
    return;
  }

#ifdef PROC_EDITCMDLCVT
  freeproc(p);
  return;
#endif

  // ptrs are after strings to avoid copying memory when building them.
  // so free is called on the address of the address of strvec[0].
  if (p->cmdline) {
    free((void*)*p->cmdline);
  }
  if (p->environ) {
    free((void*)*p->environ);
  }
  free(p);
}

QueryData genProcesses(QueryContext& context) {
  QueryData results;

  proc_t* proc_info;
  PROCTAB* proc = openproc(PROC_SELECTS);

  // Populate proc struc for each process.
  while ((proc_info = readproc(proc, NULL))) {
    Row r;

    r["pid"] = INTEGER(proc_info->tid);
    r["uid"] = BIGINT((unsigned int)proc_info->ruid);
    r["gid"] = BIGINT((unsigned int)proc_info->rgid);
    r["euid"] = BIGINT((unsigned int)proc_info->euid);
    r["egid"] = BIGINT((unsigned int)proc_info->egid);
    r["name"] = proc_name(proc_info);
    std::string cmdline = proc_cmdline(proc_info);
    boost::algorithm::trim(cmdline);
    r["cmdline"] = cmdline;
    r["path"] = proc_link(proc_info);
    r["on_disk"] = osquery::pathExists(r["path"]).toString();

    r["resident_size"] = INTEGER(proc_info->vm_rss);
    r["phys_footprint"] = INTEGER(proc_info->vm_size);
    r["user_time"] = INTEGER(proc_info->utime);
    r["system_time"] = INTEGER(proc_info->stime);
    r["start_time"] = INTEGER(proc_info->start_time);
    r["parent"] = INTEGER(proc_info->ppid);

    results.push_back(r);
    standard_freeproc(proc_info);
  }

  closeproc(proc);

  return results;
}

QueryData genProcessEnvs(QueryContext& context) {
  QueryData results;

  proc_t* proc_info;
  PROCTAB* proc = openproc(PROC_SELECTS);

  // Populate proc struc for each process.

  while ((proc_info = readproc(proc, NULL))) {
    auto env = proc_env(proc_info);
    for (auto itr = env.begin(); itr != env.end(); ++itr) {
      Row r;
      r["pid"] = INTEGER(proc_info->tid);
      r["name"] = proc_name(proc_info);
      r["path"] = proc_link(proc_info);
      r["key"] = itr->first;
      r["value"] = itr->second;
      results.push_back(r);
    }

    standard_freeproc(proc_info);
  }

  closeproc(proc);

  return results;
}
}
}
