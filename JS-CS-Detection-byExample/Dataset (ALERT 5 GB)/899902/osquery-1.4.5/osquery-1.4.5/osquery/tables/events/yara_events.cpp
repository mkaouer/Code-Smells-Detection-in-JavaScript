/*
 *  Copyright (c) 2015, Wesley Shields
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <map>
#include <string>

#include <osquery/config.h>
#include <osquery/logger.h>

/// The file change event publishers are slightly different in OS X and Linux.
#ifdef __APPLE__
#include "osquery/events/darwin/fsevents.h"
#else
#include "osquery/events/linux/inotify.h"
#endif

#include "osquery/tables/utils/yara_utils.h"

#ifdef CONCAT
#undef CONCAT
#endif
#include <yara.h>

namespace osquery {
namespace tables {

/// The file change event publishers are slightly different in OS X and Linux.
#ifdef __APPLE__
typedef EventSubscriber<FSEventsEventPublisher> FileEventSubscriber;
typedef FSEventsEventContextRef FileEventContextRef;
#define FILE_CHANGE_MASK \
  kFSEventStreamEventFlagItemCreated | kFSEventStreamEventFlagItemModified
#else
typedef EventSubscriber<INotifyEventPublisher> FileEventSubscriber;
typedef INotifyEventContextRef FileEventContextRef;
#define FILE_CHANGE_MASK \
  IN_CREATE | IN_CLOSE_WRITE | IN_MODIFY
#endif

/**
 * @brief Track YARA matches to files.
 */
class YARAEventSubscriber : public FileEventSubscriber {
 public:
  Status init();

 private:
  /**
   * @brief This exports a single Callback for FSEventsEventPublisher events.
   *
   * @param ec The Callback type receives an EventContextRef substruct
   * for the FSEventsEventPublisher declared in this EventSubscriber subclass.
   *
   * @return Status
   */
  Status Callback(const FileEventContextRef& ec, const void* user_data);
};

/**
 * @brief Each EventSubscriber must register itself so the init method is
 * called.
 *
 * This registers YARAEventSubscriber into the osquery EventSubscriber
 * pseudo-plugin registry.
 */
REGISTER(YARAEventSubscriber, "event_subscriber", "yara_events");

Status YARAEventSubscriber::init() {
  Status status;

  ConfigDataInstance config;
  const auto& yara_config = config.getParsedData("yara");
  if (yara_config.count("file_paths") == 0)
    return Status(0, "OK");
  const auto& yara_paths = yara_config.get_child("file_paths");
  const auto& file_map = config.files();
  for (const auto& yara_path_element : yara_paths) {
    // Subscribe to each file for the given key (category).
    if (file_map.count(yara_path_element.first) == 0) {
      LOG(WARNING) << "Key in yara.file_paths not found in file_paths: " <<
        yara_path_element.first;
      continue;
    }

    for (const auto& file : file_map.at(yara_path_element.first)) {
      VLOG(1) << "Added YARA listener to: " << file;
      auto mc = createSubscriptionContext();
      mc->path = file;
      mc->mask = FILE_CHANGE_MASK;
      mc->recursive = true;
      subscribe(&YARAEventSubscriber::Callback,
                mc,
                (void*)(&yara_path_element.first));
    }
  }

  return Status(0, "OK");
}

Status YARAEventSubscriber::Callback(const FileEventContextRef& ec,
                                     const void* user_data) {
  if (user_data == nullptr) {
    return Status(1, "No YARA category string provided");
  }

  Row r;
  r["action"] = ec->action;
  r["time"] = ec->time_string;
  r["target_path"] = ec->path;
  r["category"] = *(std::string*)user_data;

  // Only FSEvents transactions updates (inotify is a no-op).
  r["transaction_id"] = INTEGER(ec->transaction_id);

  // These are default values, to be updated in YARACallback.
  r["count"] = INTEGER(0);
  r["matches"] = std::string("");

  ConfigDataInstance config;
  const auto& parser = config.getParser("yara");
  if (parser == nullptr)
    return Status(1, "ConfigParser unknown.");
  const auto& yaraParser = std::static_pointer_cast<YARAConfigParserPlugin>(parser);
  auto rules = yaraParser->rules();

  // Use the category as a lookup into the yara file_paths. The value will be
  // a list of signature groups to scan with.
  auto category = r.at("category");
  const auto& yara_config = config.getParsedData("yara");
  const auto& yara_paths = yara_config.get_child("file_paths");
  const auto& sig_groups = yara_paths.find(category);
  for (const auto& rule : sig_groups->second) {
    const std::string group = rule.second.data();
    int result = yr_rules_scan_file(rules[group],
                                    ec->path.c_str(),
                                    SCAN_FLAGS_FAST_MODE,
                                    YARACallback,
                                    (void*)&r,
                                    0);

    if (result != ERROR_SUCCESS) {
      return Status(1, "YARA error: " + std::to_string(result));
    }
  }

  if (ec->action != "") {
    add(r, ec->time);
  }

  return Status(0, "OK");
}
}
}
