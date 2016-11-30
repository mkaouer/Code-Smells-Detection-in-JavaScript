/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <vector>
#include <string>

#include <osquery/core.h>
#include <osquery/logger.h>
#include <osquery/tables.h>

#include "osquery/events/linux/inotify.h"

namespace osquery {

/**
 * @brief Track time, action changes to /etc/passwd
 *
 * This is mostly an example EventSubscriber implementation.
 */
class PasswdChangesEventSubscriber
    : public EventSubscriber<INotifyEventPublisher> {
 public:
  Status init();

  /**
   * @brief This exports a single Callback for INotifyEventPublisher events.
   *
   * @param ec The EventCallback type receives an EventContextRef substruct
   * for the INotifyEventPublisher declared in this EventSubscriber subclass.
   *
   * @return Was the callback successful.
   */
  Status Callback(const INotifyEventContextRef& ec, const void* user_data);
};

/**
 * @brief Each EventSubscriber must register itself so the init method is
 *called.
 *
 * This registers PasswdChangesEventSubscriber into the osquery EventSubscriber
 * pseudo-plugin registry.
 */
REGISTER(PasswdChangesEventSubscriber, "event_subscriber", "passwd_changes");

Status PasswdChangesEventSubscriber::init() {
  auto mc = createSubscriptionContext();
  mc->path = "/etc/passwd";
  mc->mask = IN_ATTRIB | IN_MODIFY | IN_DELETE | IN_CREATE;
  subscribe(&PasswdChangesEventSubscriber::Callback, mc, nullptr);
  return Status(0, "OK");
}

Status PasswdChangesEventSubscriber::Callback(const INotifyEventContextRef& ec,
                                              const void* user_data) {
  Row r;
  r["action"] = ec->action;
  r["target_path"] = ec->path;
  r["transaction_id"] = INTEGER(ec->event->cookie);
  if (ec->action != "" && ec->action != "OPENED") {
    // A callback is somewhat useless unless it changes the EventSubscriber
    // state or calls `add` to store a marked up event.
    add(r, ec->time);
  }
  return Status(0, "OK");
}
}
