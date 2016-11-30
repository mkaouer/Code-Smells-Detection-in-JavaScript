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

#include <functional>
#include <string>
#include <unordered_map>
#include <utility>

#include <glog/logging.h>

#include "osquery/registry/init_registry.h"
#include "osquery/registry/singleton.h"

namespace osquery {

/**
 * @brief A simple registry system for making values available by key across
 * components.
 *
 * To use this registry, make a header like so:
 *
 * @code{.cpp}
 *   #include <osquery/registry.h>
 *
 *   DECLARE_REGISTRY(MathFuncs, int, std::function<double(double)>)
 *   #define REGISTERED_MATH_FUNCS REGISTRY(MathFuncs)
 *   #define REGISTER_MATH_FUNC(id, func) \
 *           REGISTER(MathFuncs, id, func)
 * @endcode
 *
 * Client code may then advertise an entry from a .cpp like so:
 *
 * @code{.cpp}
 *    #include "my/registry/header.h"
 *    REGISTER_MATH_FUNC(1, sqrt);
 * @endcode
 *
 * Server code may then access the set of registered values by using
 * `REGISTERED_MATH_FUNCS` as a map, which will be populated after
 * `osquery::InitRegistry::get().run()` has been called.
 */
template <class Key, class Value>
class Registry : public std::unordered_map<Key, Value> {
 public:
  /**
   * @brief Register a value in the global registry
   *
   * This is used internally by the `DECLARE_REGISTRY` registration workflow.
   * If you're calling this method directly, you're probably doing something
   * incorrectly.
   */
  void registerValue(const Key& key,
                     const Value& value,
                     const char* displayName = "registry") {
    if (this->insert(std::make_pair(key, value)).second) {
      VLOG(1) << displayName << "[" << key << "]"
              << " registered";
    } else {
      LOG(ERROR) << displayName << "[" << key << "]"
                 << " already registered";
    }
  }
};
}

#define DECLARE_REGISTRY(registryName, KeyType, ObjectType)     \
  namespace osquery {                                           \
  namespace registries {                                        \
  class registryName : public Registry<KeyType, ObjectType> {}; \
  }                                                             \
  }

#define REGISTRY(registryName) \
  (osquery::Singleton<osquery::registries::registryName>::get())

#ifndef UNIQUE_VAR
#define UNIQUE_VAR_CONCAT(_name_, _line_) _name_##_line_
#define UNIQUE_VAR_LINENAME(_name_, _line_) UNIQUE_VAR_CONCAT(_name_, _line_)
#define UNIQUE_VAR(_name_) UNIQUE_VAR_LINENAME(_name_, __LINE__)
#endif

#define REGISTER(registryName, key, value)                               \
  namespace {/* require global scope, don't pollute static namespace */  \
  static osquery::RegisterInitFunc UNIQUE_VAR(registryName)([] {         \
    REGISTRY(registryName).registerValue((key), (value), #registryName); \
  });                                                                    \
  }
