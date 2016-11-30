/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <sstream>

#include <osquery/core.h>
#include <osquery/logger.h>
#include <osquery/sql.h>
#include <osquery/tables.h>
#include <osquery/registry.h>

namespace osquery {

FLAG(int32, value_max, 512, "Maximum returned row value size");

const std::map<ConstraintOperator, std::string> kSQLOperatorRepr = {
    {EQUALS, "="},
    {GREATER_THAN, ">"},
    {LESS_THAN_OR_EQUALS, "<="},
    {LESS_THAN, "<"},
    {GREATER_THAN_OR_EQUALS, ">="},
};

typedef unsigned char byte;

SQL::SQL(const std::string& q) { status_ = query(q, results_); }

const QueryData& SQL::rows() const { return results_; }

bool SQL::ok() { return status_.ok(); }

const Status& SQL::getStatus() const { return status_; }

std::string SQL::getMessageString() { return status_.toString(); }

void escapeNonPrintableBytes(std::string& data) {
  std::string escaped;
  // clang-format off
  char const hex_chars[16] = {
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      'A',
      'B',
      'C',
      'D',
      'E',
      'F',
  };
  // clang-format on

  bool needs_replacement = false;
  for (size_t i = 0; i < data.length(); i++) {
    if (((byte)data[i]) < 0x20 || ((byte)data[i]) >= 0x80) {
      needs_replacement = true;
      escaped += "\\x";
      escaped += hex_chars[(((byte)data[i])) >> 4];
      escaped += hex_chars[((byte)data[i] & 0x0F) >> 0];
    } else {
      escaped += data[i];
    }
  }

  // Only replace if any escapes were made.
  if (needs_replacement) {
    data = escaped;
  }
}

void SQL::escapeResults() {
  for (auto& row : results_) {
    for (auto& column : row) {
      escapeNonPrintableBytes(column.second);
    }
  }
}

QueryData SQL::selectAllFrom(const std::string& table) {
  PluginResponse response;
  PluginRequest request = {{"action", "generate"}};
  Registry::call("table", table, request, response);
  return response;
}

QueryData SQL::selectAllFrom(const std::string& table,
                             const std::string& column,
                             ConstraintOperator op,
                             const std::string& expr) {
  PluginResponse response;
  PluginRequest request = {{"action", "generate"}};
  QueryContext ctx;
  ctx.constraints[column].add(Constraint(op, expr));

  TablePlugin::setRequestFromContext(ctx, request);
  Registry::call("table", table, request, response);
  return response;
}

Status SQLPlugin::call(const PluginRequest& request, PluginResponse& response) {
  response.clear();
  if (request.count("action") == 0) {
    return Status(1, "SQL plugin must include a request action");
  }

  if (request.at("action") == "query") {
    return this->query(request.at("query"), response);
  } else if (request.at("action") == "columns") {
    TableColumns columns;
    auto status = this->getQueryColumns(request.at("query"), columns);
    // Convert columns to response
    for (const auto& column : columns) {
      response.push_back(
          {{"n", column.first}, {"t", columnTypeName(column.second)}});
    }
    return status;
  } else if (request.at("action") == "attach") {
    // Attach a virtual table name using an optional included definition.
    return this->attach(request.at("table"));
  } else if (request.at("action") == "detach") {
    this->detach(request.at("table"));
    return Status(0, "OK");
  }
  return Status(1, "Unknown action");
}

Status query(const std::string& q, QueryData& results) {
  return Registry::call(
      "sql", "sql", {{"action", "query"}, {"query", q}}, results);
}

Status getQueryColumns(const std::string& q, TableColumns& columns) {
  PluginResponse response;
  auto status = Registry::call(
      "sql", "sql", {{"action", "columns"}, {"query", q}}, response);

  // Convert response to columns
  for (const auto& item : response) {
    columns.push_back(make_pair(item.at("n"), columnTypeName(item.at("t"))));
  }
  return status;
}
}
