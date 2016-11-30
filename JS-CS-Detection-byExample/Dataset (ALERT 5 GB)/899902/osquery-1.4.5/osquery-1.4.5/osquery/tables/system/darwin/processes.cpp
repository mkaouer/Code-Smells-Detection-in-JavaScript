/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <map>
#include <set>

#include <libproc.h>
#include <mach/mach.h>
#include <sys/sysctl.h>

#include <mach-o/dyld_images.h>

#include <boost/algorithm/string.hpp>
#include <boost/algorithm/string/trim.hpp>

#include <osquery/core.h>
#include <osquery/filesystem.h>
#include <osquery/logger.h>
#include <osquery/tables.h>

namespace osquery {
namespace tables {

// The maximum number of expected memory regions per process.
#define MAX_MEMORY_MAPS 512

std::set<int> getProcList(const QueryContext &context) {
  std::set<int> pidlist;
  if (context.constraints.count("pid") > 0 &&
      context.constraints.at("pid").exists()) {
    pidlist = context.constraints.at("pid").getAll<int>(EQUALS);
  }

  // No equality matches, get all pids.
  if (!pidlist.empty()) {
    return pidlist;
  }

  int bufsize = proc_listpids(PROC_ALL_PIDS, 0, nullptr, 0);
  if (bufsize <= 0) {
    VLOG(1) << "An error occurred retrieving the process list";
    return pidlist;
  }

  // arbitrarily create a list with 2x capacity in case more processes have
  // been loaded since the last proc_listpids was executed
  pid_t pids[2 * bufsize / sizeof(pid_t)];

  // now that we've allocated "pids", let's overwrite num_pids with the actual
  // amount of data that was returned for proc_listpids when we populate the
  // pids data structure
  bufsize = proc_listpids(PROC_ALL_PIDS, 0, pids, sizeof(pids));
  if (bufsize <= 0) {
    VLOG(1) << "An error occurred retrieving the process list";
    return pidlist;
  }

  int num_pids = bufsize / sizeof(pid_t);
  for (int i = 0; i < num_pids; ++i) {
    // if the pid is negative or 0, it doesn't represent a real process so
    // continue the iterations so that we don't add it to the results set
    if (pids[i] <= 0) {
      continue;
    }
    pidlist.insert(pids[i]);
  }

  return pidlist;
}

std::map<int, int> getParentMap(std::set<int> &pidlist) {
  std::map<int, int> pidmap;

  struct kinfo_proc proc;
  size_t size = sizeof(proc);

  for (const auto &pid : pidlist) {
    int name[] = {CTL_KERN, KERN_PROC, KERN_PROC_PID, pid};
    if (sysctl((int *)name, 4, &proc, &size, nullptr, 0) == -1) {
      break;
    }

    if (size > 0) {
      pidmap[pid] = (int)proc.kp_eproc.e_ppid;
    }
  }

  return pidmap;
}

inline std::string getProcPath(int pid) {
  char path[PROC_PIDPATHINFO_MAXSIZE] = "\0";
  int bufsize = proc_pidpath(pid, path, sizeof(path));
  if (bufsize <= 0) {
    path[0] = '\0';
  }

  return std::string(path);
}

struct proc_cred {
  struct {
    uid_t uid;
    gid_t gid;
  } real, effective;
};

inline bool getProcCred(int pid, proc_cred &cred) {
  struct proc_bsdshortinfo bsdinfo;

  if (proc_pidinfo(pid, PROC_PIDT_SHORTBSDINFO, 0, &bsdinfo, sizeof(bsdinfo)) ==
      sizeof(bsdinfo)) {
    cred.real.uid = bsdinfo.pbsi_ruid;
    cred.real.gid = bsdinfo.pbsi_ruid;
    cred.effective.uid = bsdinfo.pbsi_uid;
    cred.effective.gid = bsdinfo.pbsi_gid;
    return true;
  }
  return false;
}

// Get the max args space
static int genMaxArgs() {
  int mib[2] = {CTL_KERN, KERN_ARGMAX};

  int argmax = 0;
  size_t size = sizeof(argmax);
  if (sysctl(mib, 2, &argmax, &size, nullptr, 0) == -1) {
    VLOG(1) << "An error occurred retrieving the max arg size";
    return 0;
  }

  return argmax;
}

void genProcRootAndCWD(int pid, Row &r) {
  r["cwd"] = "";
  r["root"] = "";

  struct proc_vnodepathinfo pathinfo;
  if (proc_pidinfo(
          pid, PROC_PIDVNODEPATHINFO, 0, &pathinfo, sizeof(pathinfo)) ==
      sizeof(pathinfo)) {
    if (pathinfo.pvi_cdir.vip_vi.vi_stat.vst_dev != 0) {
      r["cwd"] = std::string(pathinfo.pvi_cdir.vip_path);
    }

    if (pathinfo.pvi_rdir.vip_vi.vi_stat.vst_dev != 0) {
      r["root"] = std::string(pathinfo.pvi_rdir.vip_path);
    }
  }
}

std::vector<std::string> getProcRawArgs(int pid, size_t argmax) {
  std::vector<std::string> args;
  uid_t euid = geteuid();

  char procargs[argmax];
  const char *cp = procargs;
  int mib[3] = {CTL_KERN, KERN_PROCARGS2, pid};

  if (sysctl(mib, 3, &procargs, &argmax, nullptr, 0) == -1) {
    if (euid == 0) {
      VLOG(1) << "An error occurred retrieving the env for pid: " << pid;
    }

    return args;
  }

  // Here we make the assertion that we are interested in all non-empty strings
  // in the proc args+env
  do {
    std::string s = std::string(cp);
    if (s.length() > 0) {
      args.push_back(s);
    }
    cp += args.back().size() + 1;
  } while (cp < procargs + argmax);
  return args;
}

std::map<std::string, std::string> getProcEnv(int pid, size_t argmax) {
  std::map<std::string, std::string> env;
  auto args = getProcRawArgs(pid, argmax);

  // Since we know that all envs will have an = sign and are at the end of the
  // list, we iterate from the end forward until we stop seeing = signs.
  // According to the // ps source, there is no programmatic way to know where
  // args stop and env begins, so args at the end of a command string which
  // contain "=" may erroneously appear as env vars.
  for (auto itr = args.rbegin(); itr < args.rend(); ++itr) {
    size_t idx = itr->find_first_of("=");
    if (idx == std::string::npos) {
      break;
    }
    std::string key = itr->substr(0, idx);
    std::string value = itr->substr(idx + 1);
    env[key] = value;
  }

  return env;
}

std::vector<std::string> getProcArgs(int pid, size_t argmax) {
  auto raw_args = getProcRawArgs(pid, argmax);
  std::vector<std::string> args;
  bool collect = false;

  // Iterate from the back until we stop seeing environment vars
  // Then start pushing args (in reverse order) onto a vector.
  // We trim the args of leading/trailing whitespace to make
  // analysis easier.
  for (auto itr = raw_args.rbegin(); itr < raw_args.rend(); ++itr) {
    if (collect) {
      std::string arg = *itr;
      boost::algorithm::trim(arg);
      args.push_back(arg);
    } else {
      size_t idx = itr->find_first_of("=");
      if (idx == std::string::npos) {
        collect = true;
      }
    }
  }

  // We pushed them on backwards, so we need to fix that.
  std::reverse(args.begin(), args.end());

  return args;
}

QueryData genProcesses(QueryContext &context) {
  QueryData results;

  auto pidlist = getProcList(context);
  auto parent_pid = getParentMap(pidlist);
  int argmax = genMaxArgs();

  for (auto &pid : pidlist) {
    Row r;
    r["pid"] = INTEGER(pid);
    r["path"] = getProcPath(pid);
    // OS X proc_name only returns 16 bytes, use the basename of the path.
    r["name"] = boost::filesystem::path(r["path"]).filename().string();

    // The command line invocation including arguments.
    std::string cmdline = boost::algorithm::join(getProcArgs(pid, argmax), " ");
    boost::algorithm::trim(cmdline);
    r["cmdline"] = cmdline;
    genProcRootAndCWD(pid, r);

    proc_cred cred;
    if (getProcCred(pid, cred)) {
      r["uid"] = BIGINT(cred.real.uid);
      r["gid"] = BIGINT(cred.real.gid);
      r["euid"] = BIGINT(cred.effective.uid);
      r["egid"] = BIGINT(cred.effective.gid);
    } else {
      r["uid"] = "-1";
      r["gid"] = "-1";
      r["euid"] = "-1";
      r["egid"] = "-1";
    }

    // Find the parent process.
    const auto parent_it = parent_pid.find(pid);
    if (parent_it != parent_pid.end()) {
      r["parent"] = INTEGER(parent_it->second);
    } else {
      r["parent"] = "-1";
    }

    // If the path of the executable that started the process is available and
    // the path exists on disk, set on_disk to 1. If the path is not
    // available, set on_disk to -1. If, and only if, the path of the
    // executable is available and the file does NOT exist on disk, set on_disk
    // to 0.
    r["on_disk"] = osquery::pathExists(r["path"]).toString();

    // systems usage and time information
    struct rusage_info_v2 rusage_info_data;
    int rusage_status = proc_pid_rusage(
        pid, RUSAGE_INFO_V2, (rusage_info_t *)&rusage_info_data);
    // proc_pid_rusage returns -1 if it was unable to gather information
    if (rusage_status == 0) {
      // size/memory information
      r["wired_size"] = TEXT(rusage_info_data.ri_wired_size);
      r["resident_size"] = TEXT(rusage_info_data.ri_resident_size);
      r["phys_footprint"] = TEXT(rusage_info_data.ri_phys_footprint);

      // time information
      r["user_time"] = TEXT(rusage_info_data.ri_user_time / 1000000);
      r["system_time"] = TEXT(rusage_info_data.ri_system_time / 1000000);
      r["start_time"] = TEXT(rusage_info_data.ri_proc_start_abstime);
    } else {
      r["wired_size"] = "-1";
      r["resident_size"] = "-1";
      r["phys_footprint"] = "-1";
      r["user_time"] = "-1";
      r["system_time"] = "-1";
      r["start_time"] = "-1";
    }

    results.push_back(r);
  }

  return results;
}

QueryData genProcessEnvs(QueryContext &context) {
  QueryData results;

  auto pidlist = getProcList(context);
  int argmax = genMaxArgs();
  for (const auto &pid : pidlist) {
    auto env = getProcEnv(pid, argmax);
    for (auto env_itr = env.begin(); env_itr != env.end(); ++env_itr) {
      Row r;

      r["pid"] = INTEGER(pid);
      r["key"] = env_itr->first;
      r["value"] = env_itr->second;

      results.push_back(r);
    }
  }

  return results;
}

void genMemoryRegion(int pid,
                     const vm_address_t &address,
                     const vm_size_t &size,
                     struct vm_region_submap_info_64 &info,
                     const std::map<vm_address_t, std::string> &libraries,
                     QueryData &results) {
  Row r;
  r["pid"] = INTEGER(pid);

  char addr_str[17] = {0};
  sprintf(addr_str, "%016lx", address);
  r["start"] = "0x" + std::string(addr_str);
  sprintf(addr_str, "%016lx", address + size);
  r["end"] = "0x" + std::string(addr_str);

  char perms[5] = {0};
  sprintf(perms,
          "%c%c%c",
          (info.protection & VM_PROT_READ) ? 'r' : '-',
          (info.protection & VM_PROT_WRITE) ? 'w' : '-',
          (info.protection & VM_PROT_EXECUTE) ? 'x' : '-');
  // Mimic Linux permissions reporting.
  r["permissions"] = std::string(perms) + 'p';

  char filename[PATH_MAX] = {0};
  // Eventually we'll arrive at dynamic memory COW regions.
  // OS X will return a dyld_shared_cache[...] substitute alias.
  int bytes = proc_regionfilename(pid, address, filename, sizeof(filename));

  if (info.share_mode == SM_COW && info.ref_count == 1) {
    // (psutil) Treat single reference SM_COW as SM_PRIVATE
    info.share_mode = SM_PRIVATE;
  }

  if (bytes == 0 || filename[0] == 0) {
    switch (info.share_mode) {
    case SM_COW:
      r["path"] = "[cow]";
      break;
    case SM_PRIVATE:
      r["path"] = "[private]";
      break;
    case SM_EMPTY:
      r["path"] = "[null]";
      break;
    case SM_SHARED:
    case SM_TRUESHARED:
      r["path"] = "[shared]";
      break;
    case SM_PRIVATE_ALIASED:
      r["path"] = "[private_aliased]";
      break;
    case SM_SHARED_ALIASED:
      r["path"] = "[shared_aliased]";
      break;
    default:
      r["path"] = "[unknown]";
    }
    // Labeling all non-path regions pseudo is not 100% appropriate.
    // Practically, pivoting on non-meta (actual) paths is helpful.
    r["pseudo"] = "1";
  } else {
    // The share mode is not a mutex for having a filled-in path.
    r["path"] = std::string(filename);
    r["pseudo"] = "0";
  }

  r["offset"] = INTEGER(info.offset);
  r["device"] = INTEGER(info.object_id);

  // Fields not applicable to OS X maps.
  r["inode"] = "0";

  // Increment the address/region request offset.
  results.push_back(r);

  // Submaps or offsets into regions may contain libraries mapped from the
  // dyld cache.
  for (const auto &library : libraries) {
    if (library.first > address && library.first < (address + size)) {
      r["offset"] = INTEGER(info.offset + (library.first - address));
      r["path"] = library.second;
      r["pseudo"] = "0";
      results.push_back(r);
    }
  }
}

static bool readProcessMemory(const mach_port_t &task,
                              const vm_address_t &from,
                              const vm_size_t &size,
                              vm_address_t to) {
  vm_size_t bytes;
  auto status = vm_read_overwrite(task, from, size, to, &bytes);
  if (status != KERN_SUCCESS) {
    return false;
  }

  if (bytes != size) {
    return false;
  }
  return true;
}

void genProcessLibraries(const mach_port_t &task,
                         std::map<vm_address_t, std::string> &libraries) {
  struct task_dyld_info dyld_info;
  mach_msg_type_number_t count = TASK_DYLD_INFO_COUNT;
  auto status =
      task_info(task, TASK_DYLD_INFO, (task_info_t)&dyld_info, &count);
  if (status != KERN_SUCCESS) {
    // Cannot request dyld information for pid (permissions, invalid).
    return;
  }

  // The info struct is a pointer to another process's virtual space.
  auto all_info = (struct dyld_all_image_infos *)dyld_info.all_image_info_addr;
  uint64_t image_offset = (uint64_t)all_info;
  if (dyld_info.all_image_info_format != TASK_DYLD_ALL_IMAGE_INFO_64) {
    // Only support 64bit process images.
    return;
  }

  // Skip the 32-bit integer version field.
  image_offset += sizeof(uint32_t);
  uint32_t info_array_count = 0;
  // Read the process's 32-bit integer infoArrayCount (number of libraries).
  if (!readProcessMemory(task,
                         image_offset,
                         sizeof(uint32_t),
                         (vm_address_t)&info_array_count)) {
    return;
  }

  image_offset += sizeof(uint32_t);
  vm_address_t info_array = 0;
  // Read the process's infoArray address field.
  if (!readProcessMemory(task,
                         image_offset,
                         sizeof(vm_address_t),
                         (vm_address_t)&info_array)) {
    return;
  }

  // Loop over the array of dyld_image_info structures.
  // Read the process-mapped address and pointer to the library path.
  for (uint32_t i = 0; i < info_array_count; i++) {
    dyld_image_info image;
    if (!readProcessMemory(task,
                           info_array + (i * sizeof(struct dyld_image_info)),
                           sizeof(dyld_image_info),
                           (vm_address_t)&image)) {
      return;
    }

    // It's possible to optimize for smaller reads by chucking the memory reads.
    char path[PATH_MAX] = {0};
    if (!readProcessMemory(task,
                           (vm_address_t)image.imageFilePath,
                           PATH_MAX,
                           (vm_address_t)&path)) {
      continue;
    }

    // Keep the process-mapped address as the library index.
    libraries[(vm_address_t)image.imageLoadAddress] = path;
  }
}

void genProcessMemoryMap(int pid, QueryData &results) {
  mach_port_t task = MACH_PORT_NULL;
  kern_return_t status = task_for_pid(mach_task_self(), pid, &task);
  if (status != KERN_SUCCESS) {
    // Cannot request memory map for pid (permissions, invalid).
    return;
  }

  // Create a map of library paths from the dyld cache.
  std::map<vm_address_t, std::string> libraries;
  genProcessLibraries(task, libraries);

  // Use address offset (starting at 0) to count memory maps.
  vm_address_t address = 0;
  size_t map_count = 0;
  uint32_t depth = 0;

  while (map_count++ < MAX_MEMORY_MAPS) {
    struct vm_region_submap_info_64 info;
    mach_msg_type_number_t count = VM_REGION_SUBMAP_INFO_COUNT_64;

    vm_size_t size = 0;
    status = vm_region_recurse_64(
        task, &address, &size, &depth, (vm_region_info_64_t)&info, &count);

    if (status == KERN_INVALID_ADDRESS) {
      // Reached the end of the memory map.
      break;
    }

    if (info.is_submap) {
      // A submap increments the depth search to vm_region_recurse.
      // Use the same address to continue a recursive search within the region.
      depth++;
      continue;
    }

    genMemoryRegion(pid, address, size, info, libraries, results);
    address += size;
  }

  if (task != MACH_PORT_NULL) {
    mach_port_deallocate(mach_task_self(), task);
  }
}

QueryData genProcessMemoryMap(QueryContext& context) {
  QueryData results;

  auto pidlist = getProcList(context);
  for (const auto &pid : pidlist) {
    genProcessMemoryMap(pid, results);
  }

  return results;
}
}
}
