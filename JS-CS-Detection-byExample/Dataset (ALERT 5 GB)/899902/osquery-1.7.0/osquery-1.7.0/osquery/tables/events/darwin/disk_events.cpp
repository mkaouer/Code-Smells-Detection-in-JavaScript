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
#include <osquery/logger.h>
#include <osquery/tables.h>
#include <osquery/events.h>

#include "osquery/events/darwin/diskarbitration.h"

namespace osquery {

class DiskEventSubscriber
    : public EventSubscriber<DiskArbitrationEventPublisher> {
 public:
  Status init() override;

  Status Callback(const ECRef& ec, const SCRef& sc);
};

REGISTER(DiskEventSubscriber, "event_subscriber", "disk_events");

Status DiskEventSubscriber::init() {
  auto subscription = createSubscriptionContext();
  // Don't want physical disk events
  subscription->physical_disks = false;

  subscribe(&DiskEventSubscriber::Callback, subscription);
  return Status(0, "OK");
}

Status DiskEventSubscriber::Callback(const ECRef& ec, const SCRef& sc) {
  Row r;
  r["action"] = ec->action;
  r["path"] = ec->path;
  r["name"] = ec->name;
  r["bsd_name"] = "/dev/" + ec->bsd_name;
  r["uuid"] = ec->uuid;
  r["size"] = ec->size;
  r["ejectable"] = ec->ejectable;
  r["mountable"] = ec->mountable;
  r["writable"] = ec->writable;
  r["content"] = ec->content;
  r["media_name"] = ec->media_name;
  r["vendor"] = ec->vendor;
  r["filesystem"] = ec->filesystem;
  r["checksum"] = ec->checksum;

  EventTime time = ec->time;
  if (ec->action == "add") {
    boost::conversion::try_lexical_convert(ec->disk_appearance_time, time);
  }

  add(r, ec->time);
  return Status(0, "OK");
}
}
