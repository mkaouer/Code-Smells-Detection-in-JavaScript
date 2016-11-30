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

#include <string>

#include <osquery/flags.h>
#include <osquery/registry.h>

namespace osquery {

/// Allow users to disable enrollment features.
DECLARE_bool(disable_enrollment);

/**
 * @brief Superclass for enroll plugins.
 *
 * Config and Logger plugins may use some remote API. In most cases an
 * authentication and authorization step is needed. Enroll plugins are an
 * easy wrapper-type facility that other osquery plugin types can choose to
 * implement.
 *
 * An enrollment is useful when a "backend" config or logger facility requires
 * a node or shared secret. The plugins that support this "backend" will
 * request authentication secrets through their well-known enrollment plugin.
 *
 * Enrollment plugins and authentication models are complicated. It is best
 * to use a "suite" of plugins that implement an enroll, config, and log flow.
 * Please see the osquery wiki for more details on Enrollment.
 */
class EnrollPlugin : public Plugin {
 public:
  /// The EnrollPlugin PluginRequest action router.
  Status call(const PluginRequest& request, PluginResponse& response);

 protected:
  /**
   * @brief Perform enrollment on the request of a config/logger.
   *
   * The single 'enroll' plugin request action will call EnrollPlugin::enroll,
   * the requester may include an optional "force" key to ask the enroll plugin
   * to perhaps re-request an enrollment rather than doing a cache lookup.
   * This force functionality is optional and implemented/described by each
   * enroll plugin.
   *
   * @param force An optionally-supported/used request to force re-enrollment.
   * @return An enrollment secret or key material or identifier.
   */
  virtual std::string enroll() = 0;
};

/**
 * @brief Get a node key from the osquery RocksDB cache or perform node
 * enrollment.
 *
 * Enrollment allows a new node to announce to an enrollment endpoint via an
 * enroll plugin. While the details of authentication/authorization are up to
 * the plugin implementation, the endpoint may return a "node secret".
 *
 * If a node_key is requested from an enroll plugin because no current key
 * exists in the backing store, the result will be cached.
 *
 * @param enroll_plugin Name of the enroll plugin to use if no node_key set.
 * @param force Optionally bypass cache and force call the enroll plugin.
 * @return node_key A unique, often private, node secret key.
 */
std::string getNodeKey(const std::string& enroll_plugin);

/**
 * @brief Delete the existing node key from the persistent storage
 *
 * @return a Status indicating the success or failure of the operation
 */
Status clearNodeKey();

/**
 * @brief Read the enrollment secret from disk.
 *
 * We suspect multiple enrollment types may require an apriori, and enterprise
 * shared, secret. Use of this enroll or deployment secret is an optional choice
 * made by the enroll plugin type.
 *
 * @return enroll_secret The trimmed content read from FLAGS_enroll_secret_path.
 */
const std::string getEnrollSecret();

/**
 * @brief Enroll plugin registry.
 *
 * This creates an osquery registry for "enroll" which may implement
 * EnrollPlugin. Only strings are logged in practice, and EnrollPlugin
 * provides a helper member for transforming PluginRequests to strings.
 */
CREATE_LAZY_REGISTRY(EnrollPlugin, "enroll");
}
