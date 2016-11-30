/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <syslog.h>

#include <osquery/config.h>
#include <osquery/core.h>
#include <osquery/events.h>
#include <osquery/flags.h>
#include <osquery/filesystem.h>
#include <osquery/logger.h>
#include <osquery/registry.h>

namespace osquery {

const std::string kDescription =
    "your operating system as a high-performance "
    "relational database";
const std::string kEpilog = "osquery project page <http://osquery.io>.";

DEFINE_osquery_flag(bool, debug, false, "Enable debug messages");

DEFINE_osquery_flag(bool,
                    verbose_debug,
                    false,
                    "Enable verbose debug messages");

DEFINE_osquery_flag(bool, disable_logging, false, "Disable ERROR/INFO logging");

DEFINE_osquery_flag(string,
                    osquery_log_dir,
                    "/var/log/osquery/",
                    "Directory for ERROR/INFO and results logging");

DEFINE_osquery_flag(bool,
                    config_check,
                    false,
                    "Check the format of an osquery config");

#ifndef __APPLE__
namespace osquery {
DEFINE_osquery_flag(bool, daemonize, false, "Run as daemon (osqueryd only)");
}
#endif

namespace fs = boost::filesystem;

void printUsage(const std::string& binary, int tool) {
  // Parse help options before gflags. Only display osquery-related options.
  fprintf(stdout, "osquery " OSQUERY_VERSION ", %s\n", kDescription.c_str());
  if (tool == OSQUERY_TOOL_SHELL) {
    // The shell allows a caller to run a single SQL statement and exit.
    fprintf(
        stdout, "Usage: %s [OPTION]... [SQL STATEMENT]\n\n", binary.c_str());
  } else {
    fprintf(stdout, "Usage: %s [OPTION]...\n\n", binary.c_str());
  }
  fprintf(stdout,
          "The following options control the osquery "
          "daemon and shell.\n\n");

  Flag::printFlags(Flag::get().flags());

  if (tool == OSQUERY_TOOL_SHELL) {
    // Print shell flags.
    fprintf(stdout, "\nThe following options control the osquery shell.\n\n");
    Flag::printFlags(Flag::get().shellFlags());
  }

  fprintf(stdout, "\n%s\n", kEpilog.c_str());
}

void announce() {
  syslog(LOG_NOTICE, "osqueryd started [version=" OSQUERY_VERSION "]");
}

void initOsquery(int argc, char* argv[], int tool) {
  std::string binary(fs::path(std::string(argv[0])).filename().string());
  std::string first_arg = (argc > 1) ? std::string(argv[1]) : "";

  // osquery implements a custom help/usage output.
  if ((first_arg == "--help" || first_arg == "-h" || first_arg == "-help") &&
      tool != OSQUERY_TOOL_TEST) {
    printUsage(binary, tool);
    ::exit(0);
  }

  FLAGS_alsologtostderr = true;
  FLAGS_logbufsecs = 0; // flush the log buffer immediately
  FLAGS_stop_logging_if_full_disk = true;
  FLAGS_max_log_size = 10; // max size for individual log file is 10MB
  
  // if you'd like to change the default logging plugin, compile osquery with
  // -DOSQUERY_DEFAULT_CONFIG_PLUGIN=<new_default_plugin>
#ifdef OSQUERY_DEFAULT_CONFIG_PLUGIN
  FLAGS_config_plugin = STR(OSQUERY_DEFAULT_CONFIG_PLUGIN);
#endif

  // Set version string from CMake build
  __GFLAGS_NAMESPACE::SetVersionString(OSQUERY_VERSION);

  // Let gflags parse the non-help options/flags.
  __GFLAGS_NAMESPACE::ParseCommandLineFlags(&argc, &argv, false);

  // The log dir is used for glogging and the filesystem results logs.
  if (isWritable(FLAGS_osquery_log_dir.c_str()).ok()) {
    FLAGS_log_dir = FLAGS_osquery_log_dir;
  }

  if (FLAGS_verbose_debug) {
    // Turn verbosity up to 1.
    // Do log DEBUG, INFO, WARNING, ERROR to their log files.
    // Do log the above and verbose=1 to stderr.
    FLAGS_debug = true;
    FLAGS_v = 1;
  }

  if (!FLAGS_debug) {
    // Do NOT log INFO, WARNING, ERROR to stderr.
    // Do log to their log files.
    FLAGS_minloglevel = 0; // INFO
    FLAGS_alsologtostderr = false;
  }

  if (FLAGS_disable_logging) {
    // Do log ERROR to stderr.
    // Do NOT log INFO, WARNING, ERROR to their log files.
    FLAGS_logtostderr = true;
    FLAGS_minloglevel = 2; // ERROR
  }

  // Start the logging, and announce the daemon is starting.
  google::InitGoogleLogging(argv[0]);
  VLOG(1) << "osquery initializing [version=" OSQUERY_VERSION "]";

  // Run the setup for all non-lazy registries.
  Registry::setUp();
  // And finally load the config.
  auto config = Config::getInstance();
  config->load();

  if (FLAGS_config_check) {
    auto s = Config::checkConfig();
    if (!s.ok()) {
      std::cerr << "Error reading config: " << s.toString() << "\n";
    }
    ::exit(s.getCode());
  }
}

void initOsqueryDaemon() {
#ifndef __APPLE__
  // OSX uses launchd to daemonize.
  if (osquery::FLAGS_daemonize) {
    if (daemon(0, 0) == -1) {
      ::exit(EXIT_FAILURE);
    }
  }
#endif

  // Print the version to SYSLOG.
  announce();

  // Create a process mutex around the daemon.
  auto pid_status = createPidFile();
  if (!pid_status.ok()) {
    LOG(ERROR) << "osqueryd initialize failed: " << pid_status.toString();
    ::exit(EXIT_FAILURE);
  }

  // Check the backing store by allocating and exitting on error.
  if (!DBHandle::checkDB()) {
    LOG(ERROR) << "osqueryd initialize failed: Could not create DB handle";
    ::exit(EXIT_FAILURE);
  }
}

void shutdownOsquery() {
  // End any event type run loops.
  EventFactory::end();

  // Hopefully release memory used by global string constructors in gflags.
  __GFLAGS_NAMESPACE::ShutDownCommandLineFlags();
}
}
