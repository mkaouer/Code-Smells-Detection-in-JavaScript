/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <osquery/enroll.h>
#include <osquery/filesystem.h>

#include "osquery/remote/requests.h"
#include "osquery/remote/transports/tls.h"
#include "osquery/remote/serializers/json.h"

#define ENROLL_TLS_MAX_ATTEMPTS 3

namespace osquery {

DECLARE_string(enroll_secret_path);
DECLARE_bool(disable_enrollment);

/// Enrollment TLS endpoint (path) using TLS hostname.
CLI_FLAG(string,
         enroll_tls_endpoint,
         "",
         "TLS/HTTPS endpoint for client enrollment");

/// Undocumented feature for TLS access token passing.
HIDDEN_FLAG(bool,
            tls_secret_always,
            false,
            "Include TLS enroll secret in every request");

/// Undocumented feature to override TLS enrollment key name.
HIDDEN_FLAG(string,
            tls_enroll_override,
            "enroll_secret",
            "Override the TLS enroll secret key name");

class TLSEnrollPlugin : public EnrollPlugin {
 private:
  /// Enroll called, return cached key or if no key cached, call requestKey.
  std::string enroll(bool force);

 private:
  /// Request an enrollment key response from the TLS endpoint.
  Status requestKey(const std::string& uri);

 private:
  /// The cached enrollment key.
  std::string node_secret_key_;
};

REGISTER(TLSEnrollPlugin, "enroll", "tls");

std::string TLSEnrollPlugin::enroll(bool force) {
  // If no node secret has been negotiated, try a TLS request.
  auto uri = "https://" + FLAGS_tls_hostname + FLAGS_enroll_tls_endpoint;
  if (FLAGS_tls_secret_always) {
    uri += ((uri.find("?") != std::string::npos) ? "&" : "?") +
           FLAGS_tls_enroll_override + "=" + getEnrollSecret();
  }

  if (node_secret_key_.size() == 0 || force) {
    VLOG(1) << "TLSEnrollPlugin requesting a node enroll key from: " << uri;
    for (size_t i = 1; i <= ENROLL_TLS_MAX_ATTEMPTS; i++) {
      auto status = requestKey(uri);
      if (status.ok() || i == ENROLL_TLS_MAX_ATTEMPTS) {
        break;
      }

      LOG(WARNING) << "Failed enrollment request to " << uri << " ("
                   << status.what() << ") retrying...";
      ::sleep(i * i);
    }
  }

  return node_secret_key_;
}

Status TLSEnrollPlugin::requestKey(const std::string& uri) {
  // Read the optional enrollment secret data (sent with an enrollment request).
  boost::property_tree::ptree params;
  params.put<std::string>(FLAGS_tls_enroll_override, getEnrollSecret());
  params.put<std::string>("host_identifier", getHostIdentifier());

  auto request = Request<TLSTransport, JSONSerializer>(uri);
  auto status = request.call(params);
  if (!status.ok()) {
    return status;
  }

  // The call succeeded, store the node secret key (the enrollment response).
  boost::property_tree::ptree recv;
  status = request.getResponse(recv);
  if (!status.ok()) {
    return status;
  }

  // Support multiple response keys as a node key (identifier).
  if (recv.count("node_key") > 0) {
    node_secret_key_ = recv.get("node_key", "");
  } else if (recv.count("id") > 0) {
    node_secret_key_ = recv.get("id", "");
  }

  if (node_secret_key_.size() == 0) {
    return Status(1, "No enrollment key returned from TLS enroll plugin");
  }
  return Status(0, "OK");
}
}
