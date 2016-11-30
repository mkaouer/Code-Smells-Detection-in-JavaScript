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

namespace pt = boost::property_tree;

namespace osquery {

/**
 * @brief A simple ConfigParserPlugin for an "events" dictionary key.
 */
class EventsConfigParserPlugin : public ConfigParserPlugin {
 public:
  std::vector<std::string> keys() { return {"events"}; }

  EventsConfigParserPlugin() { data_.put_child("events", pt::ptree()); }

  Status update(const std::map<std::string, pt::ptree>& config) {
    if (config.count("events") > 0) {
      data_ = pt::ptree();
      data_.put_child("events", config.at("events"));
    }
    return Status(0, "OK");
  }
};

REGISTER_INTERNAL(EventsConfigParserPlugin, "config_parser", "events");
}
