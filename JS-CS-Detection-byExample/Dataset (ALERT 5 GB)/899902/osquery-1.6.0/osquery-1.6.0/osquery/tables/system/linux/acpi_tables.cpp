/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <boost/filesystem.hpp>

#include <osquery/core.h>
#include <osquery/filesystem.h>
#include <osquery/hash.h>
#include <osquery/tables.h>

namespace fs = boost::filesystem;

namespace osquery {
namespace tables {

const std::string kLinuxACPIPath = "/sys/firmware/acpi/tables";

void genACPITable(const std::string& table, QueryData& results) {
  fs::path table_path = table;

  // There may be "categories" of tables in the form of directories.
  Status status;
  if (!fs::is_regular_file(table_path)) {
    std::vector<std::string> child_tables;
    status = osquery::listFilesInDirectory(table_path, child_tables);
    if (status.ok()) {
      for (const auto& child_table : child_tables) {
        genACPITable(child_table, results);
      }
    }

    return;
  }

  Row r;
  r["name"] = table_path.filename().string();

  std::string table_content;
  status = osquery::readFile(table_path, table_content);
  if (!status.ok()) {
    r["size"] = INTEGER(-1);
  } else {
    r["size"] = INTEGER(table_content.size());
    r["md5"] = osquery::hashFromBuffer(
        HASH_TYPE_MD5, table_content.c_str(), table_content.length());
  }

  results.push_back(r);
}

QueryData genACPITables(QueryContext& context) {
  QueryData results;

  // In Linux, hopefully the ACPI tables are parsed and exposed as nodes.
  std::vector<std::string> tables;
  auto status = osquery::listFilesInDirectory(kLinuxACPIPath, tables);
  if (!status.ok()) {
    // We could not read the tables path or the nodes are not exposed.
    return {};
  }

  for (const auto& table : tables) {
    genACPITable(table, results);
  }

  return results;
}
}
}
