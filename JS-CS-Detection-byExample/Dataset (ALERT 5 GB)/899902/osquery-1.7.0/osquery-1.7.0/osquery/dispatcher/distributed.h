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

#include "osquery/dispatcher/dispatcher.h"

namespace osquery {

/// A Dispatcher service thread that implements the distributed query service
class DistributedRunner : public InternalRunnable {
 public:
  virtual ~DistributedRunner() {}
  DistributedRunner() {}

 public:
  /// The Dispatcher thread entry point.
  void start();
};

Status startDistributed();
}
