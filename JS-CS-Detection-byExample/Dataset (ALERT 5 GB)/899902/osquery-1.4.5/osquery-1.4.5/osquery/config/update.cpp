/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <osquery/config.h>

namespace osquery {

/**
 * @brief A special config plugin that updates an osquery core's config.
 *
 * Config plugins may asynchronously change config data for the core osquery
 * process. This is a rare instance where a plugin acts to change core state.
 * Plugins normally act on behalf of a registry or extension call.
 * To acheive plugin-initiated calls, Config plugins chain calls to plugins
 * using the UpdateConfigPlugin named 'update'.
 *
 * Plugins do not need to implement call-chaining explicitly. If an extension
 * plugin implements an asynchronous feature it should call `Config::update`
 * directly. The osquery config will check if the registry is external, meaning
 * the config instance is running as an extension. If external, config will
 * route the update request and the registry will send missing (in this case
 * "config/update" is missing) requests to core.
 */
class UpdateConfigPlugin : public ConfigPlugin {
 public:
  Status genConfig(std::map<std::string, std::string>& config) {
    return Status(0, "Unused");
  }
};

REGISTER(UpdateConfigPlugin, "config", "update");
}
