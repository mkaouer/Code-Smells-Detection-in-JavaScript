/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <sys/shm.h>
#include <pwd.h>

#include <osquery/core.h>
#include <osquery/filesystem.h>
#include <osquery/logger.h>
#include <osquery/tables.h>

namespace osquery {
namespace tables {

struct shm_info {
  int used_ids;
  unsigned long shm_tot;
  unsigned long shm_rss;
  unsigned long shm_swp;
  unsigned long swap_attempts;
  unsigned long swap_successes;
} __attribute__((unused));

QueryData genSharedMemory(QueryContext &context) {
  QueryData results;

  // Use shared memory control (shmctl) to get the max SHMID.
  struct shm_info shm_info;
  int maxid = shmctl(0, SHM_INFO, (struct shmid_ds *)(void *)&shm_info);
  if (maxid < 0) {
    VLOG(1) << "Linux kernel not configured for shared memory";
    return {};
  }

  // Use a static pointer to access IPC permissions structure.
  struct shmid_ds shmseg;
  struct ipc_perm *ipcp = &shmseg.shm_perm;

  // Then iterate each shared memory ID up to the max.
  for (int id = 0; id <= maxid; id++) {
    int shmid = shmctl(id, SHM_STAT, &shmseg);
    if (shmid < 0) {
      continue;
    }

    Row r;
    r["shmid"] = INTEGER(shmid);

    struct passwd *pw = getpwuid(shmseg.shm_perm.uid);
    if (pw != nullptr) {
      r["owner_uid"] = BIGINT(pw->pw_uid);
    }

    pw = getpwuid(shmseg.shm_perm.cuid);
    if (pw != nullptr) {
      r["creator_uid"] = BIGINT(pw->pw_uid);
    }

    // Accessor, creator pids.
    r["pid"] = BIGINT(shmseg.shm_lpid);
    r["creator_pid"] = BIGINT(shmseg.shm_cpid);

    // Access, detached, creator times
    r["atime"] = BIGINT(shmseg.shm_atime);
    r["dtime"] = BIGINT(shmseg.shm_dtime);
    r["ctime"] = BIGINT(shmseg.shm_ctime);

    r["permissions"] = lsperms(ipcp->mode);
    r["size"] = BIGINT(shmseg.shm_segsz);
    r["attached"] = INTEGER(shmseg.shm_nattch);
    r["status"] = (ipcp->mode & SHM_DEST) ? "dest" : "";
    r["locked"] = (ipcp->mode & SHM_LOCKED) ? "1" : "0";

    results.push_back(r);
  }

  return results;
}
}
}