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

#ifndef OSQUERY_BUILD_SDK
#define OSQUERY_BUILD_SDK
#endif

#include <osquery/config.h>
#include <osquery/core.h>
#include <osquery/database.h>
#include <osquery/events.h>
#include <osquery/extensions.h>
#include <osquery/filesystem.h>
#include <osquery/flags.h>
#include <osquery/hash.h>
#include <osquery/logger.h>
#include <osquery/registry.h>
#include <osquery/sql.h>
#include <osquery/status.h>
#include <osquery/tables.h>

namespace osquery {
/**
 * @brief Create the external SQLite implementation wrapper.
 *
 * Anything built with only libosquery and not the 'additional' library will
 * not include a native SQL implementation. This applies to extensions and
 * separate applications built with the osquery SDK.
 *
 * The ExternalSQLPlugin is a wrapper around the SQLite API, which forwards
 * calls to an osquery extension manager (core).
 */
REGISTER_INTERNAL(ExternalSQLPlugin, "sql", "sql");

/**
 * @brief Mimic the REGISTER macro, extensions should use this helper.
 *
 * The SDK does not provide a REGISTER macro for modules or extensions.
 * Tools built with the osquery SDK should use REGISTER_EXTERNAL to add to
 * their own 'external' registry. This registry will broadcast to the osquery
 * extension manager (core) in an extension.
 *
 * osquery 'modules' should not construct their plugin registrations in
 * global scope (global construction time). Instead they should use the
 * module call-in well defined symbol, declare their SDK constraints, then
 * use the REGISTER_MODULE call within `initModule`.
 */
#define REGISTER_EXTERNAL(type, registry, name)                            \
  __attribute__((constructor)) static void type##ExtensionRegistryItem() { \
    Registry::add<type>(registry, name);                                   \
  }

/// Helper macro to write the `initModule` symbol without rewrites.
#define DECLARE_MODULE(name)        \
  extern "C" void initModule(void); \
  __attribute__((constructor)) static void declareModule()

/**
 * @brief Create an osquery extension 'module'.
 *
 * This helper macro creates a constructor to declare an osquery module is
 * loading. The osquery registry is set up when modules (shared objects) are
 * discovered via search paths and opened. At that phase the registry is locked
 * meaning no additional plugins can be registered. To unlock the registry
 * for modifications a module must call Registry::declareModule. This declares
 * and any plugins added will use the metadata in the declare to determine:
 *  - The name of the module adding the plugin
 *  - The SDK version the module was built with, to determine compatibility
 *  - The minimum SDK the module requires from osquery core
 *
 * The registry is again locked when the module load is complete and a well
 * known module-exported symbol is called.
 */
#define CREATE_MODULE(name, version, min_sdk_version)         \
  DECLARE_MODULE(name) {                                      \
    Registry::declareModule(                                  \
        name, version, min_sdk_version, OSQUERY_SDK_VERSION); \
  }

/**
 * @brief Create an osquery extension 'module', if an expression is true.
 *
 * This is a helper testing wrapper around CREATE_MODULE and DECLARE_MODULE.
 * It allows unit and integration tests to generate global construction code
 * that depends on data/variables available during global construction.
 *
 * And example use includes checking if a process environment variable is
 * defined. If defined the module is declared.
 */
#define CREATE_MODULE_IF(expr, name, version, min_sdk_version)  \
  DECLARE_MODULE(name) {                                        \
    if ((expr)) {                                               \
      Registry::declareModule(                                  \
          name, version, min_sdk_version, OSQUERY_SDK_VERSION); \
    }                                                           \
  }

/// Helper replacement for REGISTER, used within extension modules.
#define REGISTER_MODULE(type, registry, name) \
  auto type##ModuleRegistryItem = Registry::add<type>(registry, name)

// Remove registry-helper macros from the SDK.
#undef REGISTER
#define REGISTER "Do not REGISTER in the osquery SDK"
#undef REGISTER_INTERNAL
#define REGISTER_INTERNAL "Do not REGISTER_INTERNAL in the osquery SDK"
#undef CREATE_REGISTRY
#define CREATE_REGISTRY "Do not CREATE_REGISTRY in the osquery SDK"
#undef CREATE_LAZY_REGISTRY
#define CREATE_LAZY_REGISTRY "Do not CREATE_LAZY_REGISTRY in the osquery SDK"
}
