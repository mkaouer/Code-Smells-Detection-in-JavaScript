// Copyright 2004-present Facebook. All Rights Reserved.

#include <vector>
#include <string>

#include <boost/algorithm/string/join.hpp>
#include <boost/algorithm/string/predicate.hpp>

#include <osquery/core.h>
#include <osquery/filesystem.h>
#include <osquery/logger.h>
#include <osquery/tables.h>

namespace osquery {
namespace tables {

void genNFSShare(const std::string& share_line, QueryData& results) {
  auto line = split(share_line);
  if (line.size() == 0 || boost::starts_with(line[0], "#")) {
    return;
  }

  std::vector<std::string> line_exports;
  unsigned int readonly = 0;
  int options_index = -1;

  for (const auto& iter : line) {
    options_index++;
    if (iter[0] == '/') {
      line_exports.push_back(iter);
    } else {
      break;
    }
  }

  // Start looping through starting at the first options
  // (so skip the exports)
  for (auto iter = line.begin() + options_index; iter != line.end(); ++iter) {
    if (iter->compare("-ro") == 0 || iter->compare("-o") == 0) {
      readonly = 1;
    }
  }

  for (const auto& iter : line_exports) {
    Row r;
    r["share"] = iter;
    r["readonly"] = (readonly) ? "1" : "0";

    std::ostringstream oss;
    std::copy(line.begin() + options_index,
              line.end(),
              std::ostream_iterator<std::string>(oss, " "));
    r["options"] = oss.str();
    results.push_back(r);
  }
}

QueryData genNFSShares(QueryContext& context) {
  QueryData results;

  std::string content;
  auto status = osquery::forensicReadFile("/etc/exports", content);
  if (!status.ok()) {
    VLOG(1) << "Error reading /etc/exports: " << status.toString();
    return {};
  }

  for (const auto& share_line : split(content, "\n")) {
    genNFSShare(share_line, results);
  }
  return results;
}
}
}
