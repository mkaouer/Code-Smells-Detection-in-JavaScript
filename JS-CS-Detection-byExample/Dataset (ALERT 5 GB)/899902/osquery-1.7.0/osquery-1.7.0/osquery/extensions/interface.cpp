/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <osquery/filesystem.h>
#include <osquery/logger.h>

#include "osquery/extensions/interface.h"

using namespace osquery::extensions;

namespace osquery {
namespace extensions {

void ExtensionHandler::ping(ExtensionStatus& _return) {
  _return.code = ExtensionCode::EXT_SUCCESS;
  _return.message = "pong";
  _return.uuid = uuid_;
}

void ExtensionHandler::call(ExtensionResponse& _return,
                            const std::string& registry,
                            const std::string& item,
                            const ExtensionPluginRequest& request) {
  // Call will receive an extension or core's request to call the other's
  // internal registry call. It is the ONLY actor that resolves registry
  // item aliases.
  auto local_item = Registry::getAlias(registry, item);

  PluginResponse response;
  PluginRequest plugin_request;
  for (const auto& request_item : request) {
    // Create a PluginRequest from an ExtensionPluginRequest.
    plugin_request[request_item.first] = request_item.second;
  }

  auto status = Registry::call(registry, local_item, plugin_request, response);
  _return.status.code = status.getCode();
  _return.status.message = status.getMessage();
  _return.status.uuid = uuid_;

  if (status.ok()) {
    for (const auto& response_item : response) {
      // Translate a PluginResponse to an ExtensionPluginResponse.
      _return.response.push_back(response_item);
    }
  }
}

void ExtensionManagerHandler::extensions(InternalExtensionList& _return) {
  refresh();
  _return = extensions_;
}

void ExtensionManagerHandler::options(InternalOptionList& _return) {
  auto flags = Flag::flags();
  for (const auto& flag : flags) {
    _return[flag.first].value = flag.second.value;
    _return[flag.first].default_value = flag.second.default_value;
    _return[flag.first].type = flag.second.type;
  }
}

void ExtensionManagerHandler::registerExtension(
    ExtensionStatus& _return,
    const InternalExtensionInfo& info,
    const ExtensionRegistry& registry) {
  if (exists(info.name)) {
    LOG(WARNING) << "Refusing to register duplicate extension " << info.name;
    _return.code = ExtensionCode::EXT_FAILED;
    _return.message = "Duplicate extension registered";
    return;
  }

  // Every call to registerExtension is assigned a new RouteUUID.
  RouteUUID uuid = rand();
  LOG(INFO) << "Registering extension (" << info.name << ", " << uuid
            << ", version=" << info.version << ", sdk=" << info.sdk_version
            << ")";

  if (!Registry::addBroadcast(uuid, registry).ok()) {
    LOG(WARNING) << "Could not add extension (" << info.name << ", " << uuid
                 << ") broadcast to registry";
    _return.code = ExtensionCode::EXT_FAILED;
    _return.message = "Failed adding registry broadcast";
    return;
  }

  extensions_[uuid] = info;
  _return.code = ExtensionCode::EXT_SUCCESS;
  _return.message = "OK";
  _return.uuid = uuid;
}

void ExtensionManagerHandler::deregisterExtension(
    ExtensionStatus& _return, const ExtensionRouteUUID uuid) {
  if (extensions_.count(uuid) == 0) {
    _return.code = ExtensionCode::EXT_FAILED;
    _return.message = "No extension UUID registered";
    _return.uuid = 0;
    return;
  }

  // On success return the uuid of the now de-registered extension.
  Registry::removeBroadcast(uuid);
  extensions_.erase(uuid);
  _return.code = ExtensionCode::EXT_SUCCESS;
  _return.uuid = uuid;
}

void ExtensionManagerHandler::query(ExtensionResponse& _return,
                                    const std::string& sql) {
  QueryData results;
  auto status = osquery::query(sql, results);
  _return.status.code = status.getCode();
  _return.status.message = status.getMessage();
  _return.status.uuid = uuid_;

  if (status.ok()) {
    for (const auto& row : results) {
      _return.response.push_back(row);
    }
  }
}

void ExtensionManagerHandler::getQueryColumns(ExtensionResponse& _return,
                                              const std::string& sql) {
  TableColumns columns;
  auto status = osquery::getQueryColumns(sql, columns);
  _return.status.code = status.getCode();
  _return.status.message = status.getMessage();
  _return.status.uuid = uuid_;

  if (status.ok()) {
    for (const auto& col : columns) {
      _return.response.push_back({{col.first, columnTypeName(col.second)}});
    }
  }
}

void ExtensionManagerHandler::refresh() {
  std::vector<RouteUUID> removed_routes;
  const auto uuids = Registry::routeUUIDs();
  for (const auto& ext : extensions_) {
    // Find extension UUIDs that have gone away.
    if (std::find(uuids.begin(), uuids.end(), ext.first) == uuids.end()) {
      removed_routes.push_back(ext.first);
    }
  }

  // Remove each from the manager's list of extension metadata.
  for (const auto& uuid : removed_routes) {
    extensions_.erase(uuid);
  }
}

bool ExtensionManagerHandler::exists(const std::string& name) {
  refresh();

  // Search the remaining extension list for duplicates.
  for (const auto& extension : extensions_) {
    if (extension.second.name == name) {
      return true;
    }
  }
  return false;
}
}

ExtensionRunnerCore::~ExtensionRunnerCore() { remove(path_); }

void ExtensionRunnerCore::stop() {
  if (server_ != nullptr) {
    server_->stop();
  }
}

inline void removeStalePaths(const std::string& manager) {
  std::vector<std::string> paths;
  // Attempt to remove all stale extension sockets.
  resolveFilePattern(manager + ".*", paths);
  for (const auto& path : paths) {
    remove(path);
  }
}

void ExtensionRunnerCore::startServer(TProcessorRef processor) {
  auto transport = TServerTransportRef(new TServerSocket(path_));
  // Before starting and after stopping the manager, remove stale sockets.
  removeStalePaths(path_);

  auto transport_fac = TTransportFactoryRef(new TBufferedTransportFactory());
  auto protocol_fac = TProtocolFactoryRef(new TBinaryProtocolFactory());

  // The minimum number of worker threads is 1.
  size_t threads = (FLAGS_worker_threads > 0) ? FLAGS_worker_threads : 1;
  manager_ = ThreadManager::newSimpleThreadManager(threads, 0);
  auto thread_fac = ThriftThreadFactory(new PosixThreadFactory());
  manager_->threadFactory(thread_fac);
  manager_->start();

  // Start the Thrift server's run loop.
  server_ = TThreadPoolServerRef(new TThreadPoolServer(
      processor, transport, transport_fac, protocol_fac, manager_));
  server_->serve();
}

void ExtensionRunner::start() {
  // Create the thrift instances.
  auto handler = ExtensionHandlerRef(new ExtensionHandler(uuid_));
  auto processor = TProcessorRef(new ExtensionProcessor(handler));

  VLOG(1) << "Extension service starting: " << path_;
  try {
    startServer(processor);
  } catch (const std::exception& e) {
    LOG(ERROR) << "Cannot start extension handler: " << path_ << " ("
               << e.what() << ")";
  }
}

ExtensionManagerRunner::~ExtensionManagerRunner() {
  if (server_ != nullptr) {
    // Eventually this extension manager should be stopped.
    // This involves a lock around assuring the thread context for destruction
    // matches and the server has begun serving (potentially opaque to our 
    // our use of ThreadPollServer API).
    // In newer (forks) version of thrift this server implementation has been
    // deprecated.
    // server_->stop();
    removeStalePaths(path_);
  }
}

void ExtensionManagerRunner::start() {
  // Create the thrift instances.
  auto handler = ExtensionManagerHandlerRef(new ExtensionManagerHandler());
  auto processor = TProcessorRef(new ExtensionManagerProcessor(handler));

  VLOG(1) << "Extension manager service starting: " << path_;
  try {
    startServer(processor);
  } catch (const std::exception& e) {
    LOG(WARNING) << "Extensions disabled: cannot start extension manager ("
                 << path_ << ") (" << e.what() << ")";
  }
}
}
