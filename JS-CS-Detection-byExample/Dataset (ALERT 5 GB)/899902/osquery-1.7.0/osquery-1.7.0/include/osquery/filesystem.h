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

#include <map>
#include <set>
#include <string>
#include <vector>

#include <boost/filesystem/path.hpp>
#include <boost/property_tree/ptree.hpp>

#include <osquery/status.h>

namespace osquery {

/// Globbing directory traversal function recursive limit.
typedef unsigned short GlobLimits;

enum {
  GLOB_FILES = 0x1,
  GLOB_FOLDERS = 0x2,
  GLOB_ALL = GLOB_FILES | GLOB_FOLDERS,
};

/// Globbing wildcard character.
const std::string kSQLGlobWildcard = "%";
/// Globbing wildcard recursive character (double wildcard).
const std::string kSQLGlobRecursive = kSQLGlobWildcard + kSQLGlobWildcard;

/**
 * @brief Read a file from disk.
 *
 * @param path the path of the file that you would like to read.
 * @param content a reference to a string which will be populated with the
 * contents of the path indicated by the path parameter.
 * @param dry_run do not actually read the file content.
 *
 * @return an instance of Status, indicating success or failure.
 */
Status readFile(const boost::filesystem::path& path,
                std::string& content,
                size_t size = 0,
                bool dry_run = false,
                bool preserve_time = false);

/// Read a file and preserve the atime and mtime.
Status forensicReadFile(const boost::filesystem::path& path,
                        std::string& content);

/**
 * @brief Return the status of an attempted file read.
 *
 * @param path the path of the file that you would like to read.
 *
 * @return success iff the file would have been read. On success the status
 * message is the complete/absolute path.
 */
Status readFile(const boost::filesystem::path& path);

/// Internal representation for predicate-based chunk reading.
Status readFile(
    const boost::filesystem::path& path,
    size_t size,
    size_t block_size,
    bool dry_run,
    bool preserve_time,
    std::function<void(std::string& buffer, size_t size)> predicate);

/**
 * @brief Write text to disk.
 *
 * @param path the path of the file that you would like to write.
 * @param content the text that should be written exactly to disk.
 * @param permissions the filesystem permissions to request when opening.
 * @param force_permissions always `chmod` the path after opening.
 *
 * @return an instance of Status, indicating success or failure.
 */
Status writeTextFile(const boost::filesystem::path& path,
                     const std::string& content,
                     int permissions = 0660,
                     bool force_permissions = false);

/// Check if a path is writable.
Status isWritable(const boost::filesystem::path& path);

/// Check if a path is readable.
Status isReadable(const boost::filesystem::path& path);

/**
 * @brief A helper to check if a path exists on disk or not.
 *
 * @param path Target path.
 *
 * @return The code of the Status instance will be -1 if no input was supplied,
 * assuming the caller is not aware of how to check path-getter results.
 * The code will be 0 if the path does not exist on disk and 1 if the path
 * does exist on disk.
 */
Status pathExists(const boost::filesystem::path& path);

/**
 * @brief List all of the files in a specific directory.
 *
 * @param path the path which you would like to list.
 * @param results a non-const reference to a vector which will be populated
 * with the directory listing of the path param, assuming that all operations
 * completed successfully.
 * @param recursive should the listing descend recursively into the directory.
 *
 * @return an instance of Status, indicating success or failure.
 */
Status listFilesInDirectory(const boost::filesystem::path& path,
                            std::vector<std::string>& results,
                            bool recursive = false);

/**
 * @brief List all of the directories in a specific directory, non-recursively.
 *
 * @param path the path which you would like to list
 * @param results a non-const reference to a vector which will be populated
 * with the directory listing of the path param, assuming that all operations
 * completed successfully.
 * @param recursive should the listing descend recursively into the directory.
 *
 * @return an instance of Status, indicating success or failure.
 */
Status listDirectoriesInDirectory(const boost::filesystem::path& path,
                                  std::vector<std::string>& results,
                                  bool recursive = false);

/**
 * @brief Given a filesystem globbing patten, resolve all matching paths.
 *
 * @code{.cpp}
 *   std::vector<std::string> results;
 *   auto s = resolveFilePattern("/Users/marpaia/Downloads/%", results);
 *   if (s.ok()) {
 *     for (const auto& result : results) {
 *       LOG(INFO) << result;
 *     }
 *   }
 * @endcode
 *
 * @param pattern filesystem globbing pattern.
 * @param results output vector of matching paths.
 *
 * @return an instance of Status, indicating success or failure.
 */
Status resolveFilePattern(const boost::filesystem::path& pattern,
                          std::vector<std::string>& results);

/**
 * @brief Given a filesystem globbing patten, resolve all matching paths.
 *
 * See resolveFilePattern, but supply a limitation to request only directories
 * or files that match the path.
 *
 * @param pattern filesystem globbing pattern.
 * @param results output vector of matching paths.
 * @param setting a bit list of match types, e.g., files, folders.
 *
 * @return an instance of Status, indicating success or failure.
 */
Status resolveFilePattern(const boost::filesystem::path& pattern,
                          std::vector<std::string>& results,
                          GlobLimits setting);

/**
 * @brief Transform a path with SQL wildcards to globbing wildcard.
 *
 * SQL uses '%' as a wildcard matching token, and filesystem globbing uses '*'.
 * In osquery-internal methods the filesystem character is used. This helper
 * method will perform the correct preg/escape and replace.
 *
 * This has a side effect of canonicalizing paths up to the first wildcard.
 * For example: /tmp/% becomes /private/tmp/% on OS X systems. And /tmp/%.
 *
 * @param pattern the input and output filesystem glob pattern.
 */
void replaceGlobWildcards(std::string& pattern);

/// Attempt to remove a directory path.
Status remove(const boost::filesystem::path& path);

/**
 * @brief Check if an input path is a directory.
 *
 * @param path input path, either a filename or directory.
 *
 * @return If the input path was a directory.
 */
Status isDirectory(const boost::filesystem::path& path);

/**
 * @brief Return a vector of all home directories on the system.
 *
 * @return a vector of string paths containing all home directories.
 */
std::set<boost::filesystem::path> getHomeDirectories();

/**
 * @brief Check the permissions of a file and its directory.
 *
 * 'Safe' implies the directory is not a /tmp-like directory in that users
 * cannot control super-user-owner files. The file should be owned by the
 * process's UID or the file should be owned by root.
 *
 * @param dir the directory to check `/tmp` mode.
 * @param path a path to a file to check.
 * @param executable true if the file must also be executable.
 *
 * @return true if the file is 'safe' else false.
 */
bool safePermissions(const std::string& dir,
                     const std::string& path,
                     bool executable = false);

/**
 * @brief osquery may use local storage in a user-protected "home".
 *
 * Return a standard path to an "osquery" home directory. This path may store
 * a protected extensions socket, backing storage database, and debug logs.
 */
const std::string& osqueryHomeDirectory();

/// Return bit-mask-style permissions.
std::string lsperms(int mode);

/**
 * @brief Parse a JSON file on disk into a property tree.
 *
 * @param path the path of the JSON file.
 * @param tree output property tree.
 *
 * @return an instance of Status, indicating success or failure if malformed.
 */
Status parseJSON(const boost::filesystem::path& path,
                 boost::property_tree::ptree& tree);

/**
 * @brief Parse JSON content into a property tree.
 *
 * @param path JSON string data.
 * @param tree output property tree.
 *
 * @return an instance of Status, indicating success or failure if malformed.
 */
Status parseJSONContent(const std::string& content,
                        boost::property_tree::ptree& tree);

#ifdef __APPLE__
/**
 * @brief Parse a property list on disk into a property tree.
 *
 * @param path the input path to a property list.
 * @param tree the output property tree.
 *
 * @return an instance of Status, indicating success or failure if malformed.
 */
Status parsePlist(const boost::filesystem::path& path,
                  boost::property_tree::ptree& tree);

/**
 * @brief Parse property list content into a property tree.
 *
 * @param content the input string-content of a property list.
 * @param tree the output property tree.
 *
 * @return an instance of Status, indicating success or failure if malformed.
 */
Status parsePlistContent(const std::string& content,
                         boost::property_tree::ptree& tree);
#endif

#ifdef __linux__
/**
 * @brief Iterate over `/proc` process, returns a list of pids.
 *
 * @param processes output list of process pids as strings (int paths in proc).
 *
 * @return an instance of Status, indicating success or failure.
 */
Status procProcesses(std::set<std::string>& processes);

/**
 * @brief Iterate over a `/proc` process's descriptors, return a list of fds.
 *
 * @param process a string pid from proc.
 * @param descriptors output list of descriptor numbers as strings.
 *
 * @return status of iteration, failure if the process path did not exist.
 */
Status procDescriptors(const std::string& process,
                       std::map<std::string, std::string>& descriptors);

/**
 * @brief Read a descriptor's virtual path.
 *
 * @param process a string pid from proc.
 * @param descriptor a string descriptor number for a proc.
 * @param result output variable with value of link.
 *
 * @return status of read, failure on permission error or filesystem error.
 */
Status procReadDescriptor(const std::string& process,
                          const std::string& descriptor,
                          std::string& result);

/**
 * @brief Read bytes from Linux's raw memory.
 *
 * Most Linux kernels include a device node /dev/mem that allows privileged
 * users to map or seek/read pages of physical memory.
 * osquery discourages the use of physical memory reads for security and
 * performance reasons and must first try safer methods for data parsing
 * such as /sys and /proc.
 *
 * A platform user may disable physical memory reads:
 *   --disable_memory=true
 * This flag/option will cause readRawMemory to forcefully fail.
 *
 * @param base The absolute memory address to read from. This does not need
 * to be page aligned, readRawMem will take care of alignment and only
 * return the requested start address and size.
 * @param length The length of the buffer with a max of 0x10000.
 * @param buffer The output buffer, caller is responsible for resources if
 * readRawMem returns success.
 * @return status The status of the read.
 */
Status readRawMem(size_t base, size_t length, void** buffer);

#endif
}
