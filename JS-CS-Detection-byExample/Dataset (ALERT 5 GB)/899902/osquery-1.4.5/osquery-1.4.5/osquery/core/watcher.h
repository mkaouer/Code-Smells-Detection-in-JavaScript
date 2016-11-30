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

#include <unistd.h>

#include <boost/noncopyable.hpp>
#include <boost/thread/mutex.hpp>

#include <osquery/flags.h>

#include "osquery/dispatcher/dispatcher.h"

namespace osquery {

DECLARE_bool(disable_watchdog);

/**
 * @brief Categories of process performance limitations.
 *
 * Performance limits are applied by a watcher thread on autoloaded extensions
 * and optional a daemon worker process. The performance types are identified
 * here, and organized into levels. Such that a caller may enforce rigor or
 * relax the performance expectations of a osquery daemon.
 */
enum WatchdogLimitType {
  MEMORY_LIMIT,
  UTILIZATION_LIMIT,
  RESPAWN_LIMIT,
  RESPAWN_DELAY,
  LATENCY_LIMIT,
  INTERVAL,
};

/**
 * @brief A performance state structure for an autoloaded extension or worker.
 *
 * A watcher thread will continue to check the performance state, and keep a
 * last-checked snapshot for each autoloaded extension and worker process.
 */
struct PerformanceState {
  /// A counter of how many intervals the process exceeded performance limits.
  size_t sustained_latency;
  /// The last checked user CPU time.
  size_t user_time;
  /// The last checked system CPU time.
  size_t system_time;
  /// A timestamp when the process/worker was last created.
  size_t last_respawn_time;

  /// The initial (or as close as possible) process image footprint.
  size_t initial_footprint;

  PerformanceState() {
    sustained_latency = 0;
    user_time = 0;
    system_time = 0;
    last_respawn_time = 0;
    initial_footprint = 0;
  }
};

/**
 * @brief Thread-safe watched child process state manager.
 *
 * The Watcher instance is separated from the WatcherRunner thread to allow
 * signals and osquery-introspection to monitor the autoloaded extensions
 * and optional worker stats. A child-process change signal may indicate an
 * autoloaded extension ended. Tables may also report on the historic worker
 * or extension utilizations.
 *
 * Though not critical, it is preferred to remove the extension's broadcasted
 * routes quickly. Locking access to the extensions list between signals and
 * the WatcherRunner thread allows osquery to tearDown registry changes before
 * attempting to respawn an extension process.
 */
class Watcher : private boost::noncopyable {
 public:
  /// Instance accessor
  static Watcher& instance() {
    static Watcher instance;
    return instance;
  }

  /// Reset counters after a worker exits.
  static void resetWorkerCounters(size_t respawn_time);

  /// Reset counters for an extension path.
  static void resetExtensionCounters(const std::string& extension,
                                     size_t respawn_time);

  /// Lock access to extensions.
  static void lock() { instance().lock_.lock(); }

  /// Unlock access to extensions.
  static void unlock() { instance().lock_.unlock(); }

  /// Accessor for autoloadable extension paths.
  static const std::map<std::string, pid_t>& extensions() {
    return instance().extensions_;
  }

  /// Lookup extension path from pid.
  static std::string getExtensionPath(pid_t child);

  /// Remove an autoloadable extension path.
  static void removeExtensionPath(const std::string& extension);

  /// Add extensions autoloadable paths.
  static void addExtensionPath(const std::string& path);

  /// Get state information for a worker or extension child.
  static PerformanceState& getState(pid_t child);
  static PerformanceState& getState(const std::string& extension);

  /// Accessor for the worker process.
  static pid_t getWorker() { return instance().worker_; }

  /// Setter for worker process.
  static void setWorker(pid_t child) { instance().worker_ = child; }

  /// Setter for an extension process.
  static void setExtension(const std::string& extension, pid_t child);

  /// Reset pid and performance counters for a worker or extension process.
  static void reset(pid_t child);

  /**
   * @brief Return the state of autoloadable extensions.
   *
   * Some initialization decisions are made based on waiting for plugins to
   * broadcast from potentially-loaded extensions. If no extensions are loaded
   * and an active (selected at command line) plugin is missing, fail quickly.
   */
  static bool hasManagedExtensions();

 private:
  /// Do not request the lock until extensions are used.
  Watcher() : worker_(-1), lock_(mutex_, boost::defer_lock) {}
  Watcher(Watcher const&);
  void operator=(Watcher const&);
  virtual ~Watcher() {}

 private:
  /// Performance state for the worker process.
  PerformanceState state_;
  /// Performance states for each autoloadable extension binary.
  std::map<std::string, PerformanceState> extension_states_;

 private:
  /// Keep the single worker process/thread ID for inspection.
  pid_t worker_;
  /// Keep a list of resolved extension paths and their managed pids.
  std::map<std::string, pid_t> extensions_;
  /// Paths to autoload extensions.
  std::vector<std::string> extensions_paths_;

 private:
  /// Mutex and lock around extensions access.
  boost::mutex mutex_;
  /// Mutex and lock around extensions access.
  boost::unique_lock<boost::mutex> lock_;
};

/**
 * @brief A scoped locker for iterating over watcher extensions.
 *
 * A lock must be used if any part of osquery wants to enumerate the autoloaded
 * extensions or autoloadable extension paths a Watcher may be monitoring.
 * A signal or WatcherRunner thread may stop or start extensions.
 */
class WatcherLocker {
 public:
  /// Construct and gain watcher lock.
  WatcherLocker() { Watcher::lock(); }
  /// Destruct and release watcher lock.
  ~WatcherLocker() { Watcher::unlock(); }
};

/**
 * @brief The watchdog thread responsible for spawning/monitoring children.
 *
 * The WatcherRunner thread will spawn any autoloaded extensions or optional
 * osquery daemon worker processes. It will then poll for their performance
 * state and kill/respawn osquery child processes if they violate limits.
 */
class WatcherRunner : public InternalRunnable {
 public:
  /**
   * @brief Construct a watcher thread.
   *
   * @param argc The osquery process argc.
   * @param argv The osquery process argv.
   * @param use_worker True if the process should spawn and monitor a worker.
   */
  explicit WatcherRunner(int argc, char** argv, bool use_worker)
      : argc_(argc), argv_(argv), use_worker_(use_worker) {
    (void)argc_;
  }

 private:
  /// Dispatcher (this service thread's) entry point.
  void enter();
  /// Boilerplate function to sleep for some configured latency
  bool ok();
  /// Begin the worker-watcher process.
  bool watch(pid_t child);
  /// Inspect into the memory, CPU, and other worker/extension process states.
  bool isChildSane(pid_t child);

 private:
  /// Fork and execute a worker process.
  void createWorker();
  /// Fork an extension process.
  bool createExtension(const std::string& extension);
  /// If a worker/extension has otherwise gone insane, stop it.
  void stopChild(pid_t child);

 private:
  /// Keep the invocation daemon's argc to iterate through argv.
  int argc_;
  /// When a worker child is spawned the argv will be scrubbed.
  char** argv_;
  /// Spawn/monitor a worker process.
  bool use_worker_;
};

/// The WatcherWatcher is spawned within the worker and watches the watcher.
class WatcherWatcherRunner : public InternalRunnable {
 public:
  explicit WatcherWatcherRunner(pid_t watcher) : watcher_(watcher) {}
  void enter();

 private:
  pid_t watcher_;
};

/// Get a performance limit by name and optional level.
size_t getWorkerLimit(WatchdogLimitType limit, int level = -1);
}
