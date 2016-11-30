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

#include "osquery/tables/utils/yara_utils.h"

namespace osquery {
namespace tables {

/**
 * The callback used when there are compilation problems in the rules.
 */
void YARACompilerCallback(int error_level,
                          const char* file_name,
                          int line_number,
                          const char* message,
                          void* user_data) {
  if (error_level == YARA_ERROR_LEVEL_ERROR) {
    VLOG(1) << file_name << "(" << line_number << "): error: " << message;
  }
  else {
    VLOG(1) << file_name << "(" << line_number << "): warning: " << message;
  }
}

/**
 * Compile a single rule file and load it into rule pointer.
 */
Status compileSingleFile(const std::string& file, YR_RULES** rules) {
  YR_COMPILER *compiler = nullptr;
  int result = yr_compiler_create(&compiler);
  if (result != ERROR_SUCCESS) {
    VLOG(1) << "Could not create compiler: " + std::to_string(result);
    return Status(1, "Could not create compiler: " + std::to_string(result));
  }

  yr_compiler_set_callback(compiler, YARACompilerCallback, NULL);

  bool compiled = false;
  YR_RULES *tmp_rules;
  VLOG(1) << "Loading " << file;

  // First attempt to load the file, in case it is saved (pre-compiled)
  // rules.
  //
  // If you want to use saved rule files you must have them all in a single
  // file. This is easy to accomplish with yarac(1).
  result = yr_rules_load(file.c_str(), &tmp_rules);
  if (result != ERROR_SUCCESS && result != ERROR_INVALID_FILE) {
    yr_compiler_destroy(compiler);
    return Status(1, "Error loading YARA rules: " + std::to_string(result));
  } else if (result == ERROR_SUCCESS) {
    *rules = tmp_rules;
  } else {
    compiled = true;
    // Try to compile the rules.
    FILE *rule_file = fopen(file.c_str(), "r");

    if (rule_file == nullptr) {
      yr_compiler_destroy(compiler);
      return Status(1, "Could not open file: " + file);
    }

    int errors = yr_compiler_add_file(compiler,
                                      rule_file,
                                      NULL,
                                      file.c_str());

    fclose(rule_file);
    rule_file = nullptr;

    if (errors > 0) {
      yr_compiler_destroy(compiler);
      // Errors printed via callback.
      return Status(1, "Compilation errors");
    }
  }

  if (compiled) {
    // All the rules for this category have been compiled, save them in the map.
    result = yr_compiler_get_rules(compiler, *(&rules));

    if (result != ERROR_SUCCESS) {
      yr_compiler_destroy(compiler);
      return Status(1, "Insufficient memory to get YARA rules");
    }
  }

  if (compiler != nullptr) {
    yr_compiler_destroy(compiler);
    compiler = nullptr;
  }

  return Status(0, "OK");
}


/**
 * Given a vector of strings, attempt to compile them and store the result
 * in the map under the given category.
 */
Status handleRuleFiles(const std::string& category,
                       const pt::ptree& rule_files,
                       std::map<std::string, YR_RULES*>* rules) {
  YR_COMPILER *compiler = nullptr;
  int result = yr_compiler_create(&compiler);
  if (result != ERROR_SUCCESS) {
    VLOG(1) << "Could not create compiler: " + std::to_string(result);
    return Status(1, "Could not create compiler: " + std::to_string(result));
  }

  yr_compiler_set_callback(compiler, YARACompilerCallback, NULL);

  bool compiled = false;
  for (const auto& item : rule_files) {
    YR_RULES *tmp_rules;
    const auto rule = item.second.get("", "");
    VLOG(1) << "Loading " << rule;

    std::string full_path;
    if (rule[0] != '/') {
      full_path = std::string("/etc/osquery/yara/") + rule;
    } else {
      full_path = rule;
    }

    // First attempt to load the file, in case it is saved (pre-compiled)
    // rules. Sadly there is no way to load multiple compiled rules in
    // succession. This means that:
    //
    // saved1, saved2
    // results in saved2 being the only file used.
    //
    // Also, mixing source and saved rules results in the saved rules being
    // overridden by the combination of the source rules once compiled, e.g.:
    //
    // file1, saved1
    // result in file1 being the only file used.
    //
    // If you want to use saved rule files you must have them all in a single
    // file. This is easy to accomplish with yarac(1).
    result = yr_rules_load(full_path.c_str(), &tmp_rules);
    if (result != ERROR_SUCCESS && result != ERROR_INVALID_FILE) {
      yr_compiler_destroy(compiler);
      return Status(1, "Error loading YARA rules: " + std::to_string(result));
    } else if (result == ERROR_SUCCESS) {
      // If there are already rules there, destroy them and put new ones in.
      if (rules->count(category) > 0) {
        yr_rules_destroy((*rules)[category]);
      }
      (*rules)[category] = tmp_rules;
    } else {
      compiled = true;
      // Try to compile the rules.
      FILE *rule_file = fopen(full_path.c_str(), "r");

      if (rule_file == nullptr) {
        yr_compiler_destroy(compiler);
        return Status(1, "Could not open file: " + full_path);
      }

      int errors = yr_compiler_add_file(compiler,
                                        rule_file,
                                        NULL,
                                        full_path.c_str());

      fclose(rule_file);
      rule_file = nullptr;

      if (errors > 0) {
        yr_compiler_destroy(compiler);
        // Errors printed via callback.
        return Status(1, "Compilation errors");
      }
    }
  }

  if (compiled) {
    // All the rules for this category have been compiled, save them in the map.
    result = yr_compiler_get_rules(compiler, &((*rules)[category]));

    if (result != ERROR_SUCCESS) {
      yr_compiler_destroy(compiler);
      return Status(1, "Insufficient memory to get YARA rules");
    }
  }

  if (compiler != nullptr) {
    yr_compiler_destroy(compiler);
    compiler = nullptr;
  }

  return Status(0, "OK");
}

/**
 * This is the YARA callback. Used to store matching rules in the row which is
 * passed in as user_data.
 */
int YARACallback(int message, void *message_data, void *user_data) {
  if (message == CALLBACK_MSG_RULE_MATCHING) {
    Row *r = (Row *) user_data;
    YR_RULE *rule = (YR_RULE *) message_data;
    if ((*r)["matches"].length() > 0) {
      (*r)["matches"] += "," + std::string(rule->identifier);
    } else {
      (*r)["matches"] = std::string(rule->identifier);
    }
    (*r)["count"] = INTEGER(std::stoi((*r)["count"]) + 1);
  }

  return CALLBACK_CONTINUE;
}

Status YARAConfigParserPlugin::setUp() {
  int result = yr_initialize();
  if (result != ERROR_SUCCESS) {
    LOG(WARNING) << "Unable to initialize YARA (" << result << ")";
    return Status(1, "Unable to initialize YARA");
  }

  return Status(0, "OK");
}

Status YARAConfigParserPlugin::update(const std::map<std::string, ConfigTree>& config) {
  Status status;
  const auto& yara_config = config.at("yara");
  if (yara_config.count("signatures") > 0) {
    const auto& signatures = yara_config.get_child("signatures");
    data_.add_child("signatures", signatures);
    for (const auto& element : signatures) {
      VLOG(1) << "Compiling YARA signature group: " << element.first;
      status = handleRuleFiles(element.first, element.second, &rules_);
      if (!status.ok()) {
        VLOG(1) << "YARA rule compile error: " << status.getMessage();
        return status;
      }
    }
  }
  if (yara_config.count("file_paths") > 0) {
    const auto& file_paths = yara_config.get_child("file_paths");
    data_.add_child("file_paths", file_paths);
  }
  return Status(0, "OK");
}

/// Call the simple YARA ConfigParserPlugin "yara".
REGISTER(YARAConfigParserPlugin, "config_parser", "yara");

}
}
