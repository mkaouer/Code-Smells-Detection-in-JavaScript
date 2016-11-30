/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <cstring>

#include <sys/wait.h>
#include <signal.h>

#include <boost/filesystem.hpp>

#include <osquery/events.h>
#include <osquery/filesystem.h>
#include <osquery/logger.h>
#include <osquery/sql.h>

#include "osquery/core/watcher.h"
#include "osquery/dispatcher/dispatcher.h"

extern char** environ;

namespace fs = boost::filesystem;

namespace osquery {

const std::map<WatchdogLimitType, std::vector<size_t> > kWatchdogLimits = {
    // Maximum MB worker can privately allocate.
    {MEMORY_LIMIT, {50, 30, 10, 1000}},
    // Percent of user or system CPU worker can utilize for LATENCY_LIMIT
    // seconds.
    {UTILIZATION_LIMIT, {90, 80, 60, 1000}},
    // Number of seconds the worker should run, else consider the exit fatal.
    {RESPAWN_LIMIT, {20, 20, 20, 5}},
    // If the worker respawns too quickly, backoff on creating additional.
    {RESPAWN_DELAY, {5, 5, 5, 1}},
    // Seconds of tolerable UTILIZATION_LIMIT sustained latency.
    {LATENCY_LIMIT, {12, 6, 3, 1}},
    // How often to poll for performance limit violations.
    {INTERVAL, {3, 3, 3, 1}},
};

const std::string kExtensionExtension = ".ext";

CLI_FLAG(int32,
         watchdog_level,
         1,
         "Performance limit level (0=loose, 1=normal, 2=restrictive, 3=debug)");

CLI_FLAG(bool, disable_watchdog, false, "Disable userland watchdog process");

/// If the worker exits the watcher will inspect the return code.
void childHandler(int signum) {
  siginfo_t info;
  // Make sure WNOWAIT is used to the wait information is not removed.
  // Watcher::watch implements a thread to poll for this information.
  waitid(P_ALL, 0, &info, WEXITED | WSTOPPED | WNOHANG | WNOWAIT);
  if (info.si_code == CLD_EXITED && info.si_status == EXIT_CATASTROPHIC) {
    // A child process had a catastrophic error, abort the watcher.
    ::exit(EXIT_FAILURE);
  }
}

void Watcher::resetWorkerCounters(size_t respawn_time) {
  // Reset the monitoring counters for the watcher.
  auto& state = instance().state_;
  state.sustained_latency = 0;
  state.user_time = 0;
  state.system_time = 0;
  state.last_respawn_time = respawn_time;
}

void Watcher::resetExtensionCounters(const std::string& extension,
                                     size_t respawn_time) {
  WatcherLocker locker;
  auto& state = instance().extension_states_[extension];
  state.sustained_latency = 0;
  state.user_time = 0;
  state.system_time = 0;
  state.last_respawn_time = respawn_time;
}

std::string Watcher::getExtensionPath(pid_t child) {
  for (const auto& extension : extensions()) {
    if (extension.second == child) {
      return extension.first;
    }
  }
  return "";
}

void Watcher::removeExtensionPath(const std::string& extension) {
  WatcherLocker locker;
  instance().extensions_.erase(extension);
  instance().extension_states_.erase(extension);
}

PerformanceState& Watcher::getState(pid_t child) {
  if (child == instance().worker_) {
    return instance().state_;
  } else {
    return instance().extension_states_[getExtensionPath(child)];
  }
}

PerformanceState& Watcher::getState(const std::string& extension) {
  return instance().extension_states_[extension];
}

void Watcher::setExtension(const std::string& extension, pid_t child) {
  WatcherLocker locker;
  instance().extensions_[extension] = child;
}

void Watcher::reset(pid_t child) {
  if (child == instance().worker_) {
    instance().worker_ = 0;
    resetWorkerCounters(0);
    return;
  }

  // If it was not the worker pid then find the extension name to reset.
  for (const auto& extension : extensions()) {
    if (extension.second == child) {
      setExtension(extension.first, 0);
      resetExtensionCounters(extension.first, 0);
    }
  }
}

void Watcher::addExtensionPath(const std::string& path) {
  // Resolve acceptable extension binaries from autoload paths.
  if (isDirectory(path).ok()) {
    VLOG(1) << "Cannot autoload extension from directory: " << path;
    return;
  }

  // Only autoload extensions which were safe at the time of discovery.
  // If the extension binary later becomes unsafe (permissions change) then
  // it will fail to reload if a reload is ever needed.
  fs::path extension(path);
  if (safePermissions(extension.parent_path().string(), path, true)) {
    if (extension.extension().string() == kExtensionExtension) {
      setExtension(extension.string(), 0);
      resetExtensionCounters(extension.string(), 0);
      VLOG(1) << "Found autoloadable extension: " << extension.string();
    }
  }
}

bool Watcher::hasManagedExtensions() {
  if (instance().extensions_.size() > 0) {
    return true;
  }

  // A watchdog process may hint to a worker the number of managed extensions.
  // Setting this counter to 0 will prevent the worker from waiting for missing
  // dependent config plugins. Otherwise, its existance, will cause a worker to
  // wait for missing plugins to broadcast from managed extensions.
  return (getenv("OSQUERY_EXTENSIONS") != nullptr);
}

bool WatcherRunner::ok() {
  interruptableSleep(getWorkerLimit(INTERVAL) * 1000);
  // Watcher is OK to run if a worker or at least one extension exists.
  return (Watcher::getWorker() >= 0 || Watcher::hasManagedExtensions());
}

void WatcherRunner::enter() {
  // Set worker performance counters to an initial state.
  Watcher::resetWorkerCounters(0);
  signal(SIGCHLD, childHandler);

  // Enter the watch loop.
  do {
    if (use_worker_ && !watch(Watcher::getWorker())) {
      // The watcher failed, create a worker.
      createWorker();
    }

    // Loop over every managed extension and check sanity.
    std::vector<std::string> failing_extensions;
    for (const auto& extension : Watcher::extensions()) {
      if (!watch(extension.second)) {
        if (!createExtension(extension.first)) {
          failing_extensions.push_back(extension.first);
        }
      }
    }
    // If any extension creations failed, stop managing them.
    for (const auto& failed_extension : failing_extensions) {
      Watcher::removeExtensionPath(failed_extension);
    }
  } while (ok());
}

bool WatcherRunner::watch(pid_t child) {
  int status;
  pid_t result = waitpid(child, &status, WNOHANG);
  if (child == 0 || result == child) {
    // Worker does not exist or never existed.
    return false;
  } else if (result == 0) {
    // If the inspect finds problems it will stop/restart the worker.
    if (!isChildSane(child)) {
      stopChild(child);
      return false;
    }
  }
  return true;
}

void WatcherRunner::stopChild(pid_t child) {
  kill(child, SIGKILL);

  // Clean up the defunct (zombie) process.
  waitpid(-1, 0, WNOHANG);
}

bool WatcherRunner::isChildSane(pid_t child) {
  auto rows =
      SQL::selectAllFrom("processes", "pid", tables::EQUALS, INTEGER(child));
  if (rows.size() == 0) {
    // Could not find worker process?
    return false;
  }

  // Get the performance state for the worker or extension.
  size_t sustained_latency = 0;
  // Compare CPU utilization since last check.
  BIGINT_LITERAL footprint, user_time, system_time, parent;
  // IV is the check interval in seconds, and utilization is set per-second.
  auto iv = std::max(getWorkerLimit(INTERVAL), (size_t)1);

  {
    WatcherLocker locker;
    auto state = Watcher::getState(child);
    try {
      parent = AS_LITERAL(BIGINT_LITERAL, rows[0].at("parent"));
      user_time = AS_LITERAL(BIGINT_LITERAL, rows[0].at("user_time")) / iv;
      system_time = AS_LITERAL(BIGINT_LITERAL, rows[0].at("system_time")) / iv;
      footprint = AS_LITERAL(BIGINT_LITERAL, rows[0].at("resident_size"));
    } catch (const std::exception& e) {
      state.sustained_latency = 0;
    }

    // Check the different of CPU time used since last check.
    if (state.user_time + getWorkerLimit(UTILIZATION_LIMIT) < user_time ||
        state.system_time + getWorkerLimit(UTILIZATION_LIMIT) < system_time) {
      state.sustained_latency++;
    } else {
      state.sustained_latency = 0;
    }
    // Update the current CPU time.
    state.user_time = user_time;
    state.system_time = system_time;

    // Check if the sustained difference exceeded the acceptable latency limit.
    sustained_latency = state.sustained_latency;

    // Set the memory footprint as the amount of resident bytes allocated
    // since the process image was created (estimate).
    // A more-meaningful check would limit this to writable regions.
    if (state.initial_footprint == 0) {
      state.initial_footprint = footprint;
    }

    // Set the measured/limit-applied footprint to the post-launch allocations.
    if (footprint < state.initial_footprint) {
      footprint = 0;
    } else {
      footprint = footprint - state.initial_footprint;
    }
  }

  // Only make a decision about the child sanity if it is still the watcher's
  // child. It's possible for the child to die, and its pid reused.
  if (parent != getpid()) {
    // The child's parent is not the watcher.
    Watcher::reset(child);
    // Do not stop or call the child insane, since it is not our child.
    return true;
  }

  if (sustained_latency > 0 &&
      sustained_latency * iv >= getWorkerLimit(LATENCY_LIMIT)) {
    LOG(WARNING) << "osqueryd worker system performance limits exceeded";
    return false;
  }

  // Check if the private memory exceeds a memory limit.
  if (footprint > 0 && footprint > getWorkerLimit(MEMORY_LIMIT) * 1024 * 1024) {
    LOG(WARNING) << "osqueryd worker memory limits exceeded: " << footprint;
    return false;
  }

  // The worker is sane, no action needed.
  return true;
}

void WatcherRunner::createWorker() {
  {
    WatcherLocker locker;
    if (Watcher::getState(Watcher::getWorker()).last_respawn_time >
        getUnixTime() - getWorkerLimit(RESPAWN_LIMIT)) {
      LOG(WARNING) << "osqueryd worker respawning too quickly";
      interruptableSleep(getWorkerLimit(RESPAWN_DELAY) * 1000);
    }
  }

  // Get the path of the current process.
  auto qd = SQL::selectAllFrom("processes", "pid", tables::EQUALS,
    INTEGER(getpid()));
  if (qd.size() != 1 || qd[0].count("path") == 0 || qd[0]["path"].size() == 0) {
    LOG(ERROR) << "osquery watcher cannot determine process path";
    ::exit(EXIT_FAILURE);
  }

  // Set an environment signaling to potential plugin-dependent workers to wait
  // for extensions to broadcast.
  if (Watcher::hasManagedExtensions()) {
    setenv("OSQUERY_EXTENSIONS", "true", 1);
  }

  auto worker_pid = fork();
  if (worker_pid < 0) {
    // Unrecoverable error, cannot create a worker process.
    LOG(ERROR) << "osqueryd could not create a worker process";
    ::exit(EXIT_FAILURE);
  } else if (worker_pid == 0) {
    // This is the new worker process, no watching needed.
    setenv("OSQUERY_WORKER", std::to_string(getpid()).c_str(), 1);
    // Get the complete path of the osquery process binary.
    auto exec_path = fs::system_complete(fs::path(qd[0]["path"]));
    execve(exec_path.string().c_str(), argv_, environ);
    // Code should never reach this point.
    LOG(ERROR) << "osqueryd could not start worker process";
    ::exit(EXIT_CATASTROPHIC);
  }

  Watcher::setWorker(worker_pid);
  Watcher::resetWorkerCounters(getUnixTime());
  VLOG(1) << "osqueryd watcher (" << getpid() << ") executing worker ("
          << worker_pid << ")";
}

bool WatcherRunner::createExtension(const std::string& extension) {
  {
    WatcherLocker locker;
    if (Watcher::getState(extension).last_respawn_time >
        getUnixTime() - getWorkerLimit(RESPAWN_LIMIT)) {
      LOG(WARNING) << "Extension respawning too quickly: " << extension;
      // Unlike a worker, if an extension respawns to quickly we give up.
      return false;
    }
  }

  // Check the path to the previously-discovered extension binary.
  auto exec_path = fs::system_complete(fs::path(extension));
  if (!safePermissions(
          exec_path.parent_path().string(), exec_path.string(), true)) {
    // Extension binary has become unsafe.
    LOG(WARNING) << "Extension binary has unsafe permissions: " << extension;
    return false;
  }

  auto ext_pid = fork();
  if (ext_pid < 0) {
    // Unrecoverable error, cannot create an extension process.
    LOG(ERROR) << "Cannot create extension process: " << extension;
    ::exit(EXIT_FAILURE);
  } else if (ext_pid == 0) {
    // Pass the current extension socket and a set timeout to the extension.
    setenv("OSQUERY_EXTENSION", std::to_string(getpid()).c_str(), 1);
    // Execute extension with very specific arguments.
    execle(exec_path.string().c_str(),
           ("osquery extension: " + extension).c_str(),
           "--socket",
           Flag::getValue("extensions_socket").c_str(),
           "--timeout",
           Flag::getValue("extensions_timeout").c_str(),
           "--interval",
           Flag::getValue("extensions_interval").c_str(),
           (Flag::getValue("verbose") == "true") ? "--verbose" : (char*)nullptr,
           (char*)nullptr,
           environ);
    // Code should never reach this point.
    VLOG(1) << "Could not start extension process: " << extension;
    ::exit(EXIT_FAILURE);
  }

  Watcher::setExtension(extension, ext_pid);
  Watcher::resetExtensionCounters(extension, getUnixTime());
  VLOG(1) << "Created and monitoring extension child (" << ext_pid << "): "
          << extension;
  return true;
}

void WatcherWatcherRunner::enter() {
  while (true) {
    if (getppid() != watcher_) {
      // Watcher died, the worker must follow.
      VLOG(1) << "osqueryd worker (" << getpid()
              << ") detected killed watcher (" << watcher_ << ")";
      Dispatcher::removeServices();
      Dispatcher::joinServices();
      ::exit(EXIT_SUCCESS);
    }
    interruptableSleep(getWorkerLimit(INTERVAL) * 1000);
  }
}

size_t getWorkerLimit(WatchdogLimitType name, int level) {
  if (kWatchdogLimits.count(name) == 0) {
    return 0;
  }

  // If no level was provided then use the default (config/switch).
  if (level == -1) {
    level = FLAGS_watchdog_level;
  }
  if (level > 3) {
    return kWatchdogLimits.at(name).back();
  }
  return kWatchdogLimits.at(name).at(level);
}
}
