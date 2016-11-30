/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#pragma once

#include <vector>

#include <osquery/events.h>
#include <osquery/status.h>

#include "osquery/events/kernel/circular_queue_user.h"

namespace osquery {

/**
 * @brief Name of the kernel communication device node.
 *
 * The kernel component creates an ioctl API for synchronizing kernel-based
 * subscriptions and userland access to regions of shared memory.
 */
extern const std::string kKernelDevice;

/**
 * @brief Load kernel extension if applicable.
 */
void loadKernelExtension();

/**
 * @brief Subscription details for KernelEventPublisher events.
 */
struct KernelSubscriptionContext : public SubscriptionContext {
  /// The kernel event subscription type.
  osquery_event_t event_type;

  /// Optional category passed to the callback.
  std::string category;
};

/**
 * @brief Event details for a KernelEventPubliser events.
 */
struct KernelEventContext : public EventContext {
  /// The event type.
  osquery_event_t event_type;

  /// The observed uptime of the system at event time.
  uint32_t uptime{0};
};

template <typename EventType>
struct TypedKernelEventContext : public KernelEventContext {
  EventType event;

  // The flexible data must remain as the last member.
  std::vector<char> flexible_data;
};

using KernelSubscriptionContextRef = std::shared_ptr<KernelSubscriptionContext>;
using KernelEventContextRef = std::shared_ptr<KernelEventContext>;

template <typename EventType>
using TypedKernelEventContextRef =
    std::shared_ptr<TypedKernelEventContext<EventType> >;

class KernelEventPublisher
    : public EventPublisher<KernelSubscriptionContext, KernelEventContext> {
  DECLARE_PUBLISHER("kernel");

 public:
  KernelEventPublisher() : EventPublisher(), queue_(nullptr){};

  Status setUp() override;

  void configure() override;

  void tearDown() override;

  Status run() override;

 private:
  CQueue *queue_{nullptr};

  /// Check whether the subscription matches the event.
  bool shouldFire(const KernelSubscriptionContextRef &sc,
                  const KernelEventContextRef &ec) const override;

  template <typename EventType>
  KernelEventContextRef createEventContextFrom(osquery_event_t event_type,
                                               CQueue::event *event) const;
};

} // namespace osquery
