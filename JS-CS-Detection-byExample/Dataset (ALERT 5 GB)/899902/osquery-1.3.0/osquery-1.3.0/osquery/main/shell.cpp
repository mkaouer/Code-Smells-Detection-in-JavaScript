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
#include <osquery/database.h>
#include <osquery/devtools.h>
#include <osquery/events.h>

int main(int argc, char *argv[]) {
  osquery::FLAGS_db_path = "/tmp/rocksdb-osquery-shell";
  osquery::initOsquery(argc, argv, osquery::OSQUERY_TOOL_SHELL);

  // Start a thread for each appropriate event type
  osquery::registries::faucet(REGISTERED_EVENTPUBLISHERS,
                              REGISTERED_EVENTSUBSCRIBERS);
  osquery::EventFactory::delay();

  int retcode = osquery::launchIntoShell(argc, argv);

  // End any event type threads.
  osquery::EventFactory::end();
  return retcode;
}
