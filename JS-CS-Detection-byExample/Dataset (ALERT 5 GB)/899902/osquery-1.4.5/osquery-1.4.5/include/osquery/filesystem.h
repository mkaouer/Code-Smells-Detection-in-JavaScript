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
const unsigned int kMaxDirectoryTraversalDepth = 40;
typedef unsigned int ReturnSetting;

enum {
  /// Return only files
  REC_LIST_FILES = 0x1,
  /// Return only folders
  REC_LIST_FOLDERS = 0x2,
  /// Enable optimizations for file event resolutions
  REC_EVENT_OPT = 0x4,
  REC_LIST_ALL = REC_LIST_FILES | REC_LIST_FOLDERS
};

/// Globbing wildcard character.
const std::string kWildcardCharacter = "%";
/// Globbing wildcard recursive character (double wildcard).
const std::string kWildcardCharacterRecursive =
    kWildcardCharacter + kWildcardCharacter;

/**
 * @brief Read a file from disk.
 *
 * @param path the path of the file that you would like to read
 * @param content a reference to a string which will be populated with the
 * contents of the path indicated by the path parameter
 *
 * @return an instance of Status, indicating the success or failure
 * of the operation.
 */
Status readFile(const boost::filesystem::path& path, std::string& content);

/**
 * @brief Write text to disk.
 *
 * @param path the path of the file that you would like to write
 * @param content the text that should be written exactly to disk
 * @param permissions the filesystem permissions to request when opening
 * @param force_permissions always chmod the path after opening
 *
 * @return an instance of Status, indicating the success or failure
 * of the operation.
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
 * @param path the path on disk which you would like to check the existence of
 *
 * @return an instance of Status, indicating the success or failure
 * of the operation. Specifically, the code of the Status instance
 * will be -1 if no input was supplied, assuming the caller is not aware of how
 * to check path-getter results. The code will be 0 if the path does not exist
 * on disk and 1 if the path does exist on disk.
 */
Status pathExists(const boost::filesystem::path& path);

/**
 * @brief List all of the files in a specific directory, non-recursively.
 *
 * @param path the path which you would like to list.
 * @param results a non-const reference to a vector which will be populated
 * with the directory listing of the path param, assuming that all operations
 * completed successfully.
 *
 * @return an instance of Status, indicating the success or failure
 * of the operation.
 */
Status listFilesInDirectory(const boost::filesystem::path& path,
                            std::vector<std::string>& results,
                            bool ignore_error = 1);

/**
 * @brief List all of the directories in a specific directory, non-recursively.
 *
 * @param path the path which you would like to list.
 * @param results a non-const reference to a vector which will be populated
 * with the directory listing of the path param, assuming that all operations
 * completed successfully.
 *
 * @return an instance of Status, indicating the success or failure
 * of the operation.
 */
Status listDirectoriesInDirectory(const boost::filesystem::path& path,
                                  std::vector<std::string>& results,
                                  bool ignore_error = 1);

/**
 * @brief Given a wildcard filesystem patten, resolve all possible paths
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
 * @param fs_path The filesystem pattern
 * @param results The vector in which all results will be returned
 *
 * @return An instance of osquery::Status which indicates the success or
 * failure of the operation
 */
Status resolveFilePattern(const boost::filesystem::path& fs_path,
                          std::vector<std::string>& results);

/**
 * @brief Given a wildcard filesystem patten, resolve all possible paths
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
 * @param fs_path The filesystem pattern
 * @param results The vector in which all results will be returned
 * @param setting Do you want files returned, folders or both?
 *
 * @return An instance of osquery::Status which indicates the success or
 * failure of the operation
 */
Status resolveFilePattern(const boost::filesystem::path& fs_path,
                          std::vector<std::string>& results,
                          ReturnSetting setting);

/**
 * @brief Get directory portion of a path.
 *
 * @param path The input path, either a filename or directory.
 * @param dirpath a non-const reference to a resultant directory portion.
 *
 * @return If the input path was a directory this will indicate failure. One
 * should use `isDirectory` before.
 */
Status getDirectory(const boost::filesystem::path& path,
                    boost::filesystem::path& dirpath);

Status remove(const boost::filesystem::path& path);

/**
 * @brief Check if an input path is a directory.
 *
 * @param path The input path, either a filename or directory.
 *
 * @return If the input path was a directory.
 */
Status isDirectory(const boost::filesystem::path& path);

/**
 * @brief Return a vector of all home directories on the system
 *
 * @return a vector of strings representing the path of all home directories
 */
std::set<boost::filesystem::path> getHomeDirectories();

/**
 * @brief Check the permissions of a file and it's directory.
 *
 * 'Safe' implies the directory is not a /tmp-like directory in that users
 * cannot control super-user-owner files. The file should be owned by the
 * process's UID or the file should be owned by root.
 *
 * @param dir the directory to check /tmp mode
 * @param path a path to a file to check
 * @param executable the file must also be executable
 *
 * @return true if the file is 'safe' else false
 */
bool safePermissions(const std::string& dir,
                     const std::string& path,
                     bool executable = false);

/// The shell tooling may store local resources in an "osquery" home.
const std::string& osqueryHomeDirectory();

/// Return bit-mask-style permissions.
std::string lsperms(int mode);

/**
 * @brief Parse a JSON file on disk into a property tree.
 *
 * @param path the path of the JSON file
 * @param tree output property tree
 *
 * @return an instance of Status, indicating the success or failure
 */
Status parseJSON(const boost::filesystem::path& path,
                 boost::property_tree::ptree& tree);

/**
 * @brief Parse JSON content into a property tree.
 *
 * @param path JSON string data
 * @param tree output property tree
 *
 * @return an instance of Status, indicating the success or failure
 */
Status parseJSONContent(const std::string& content,
                        boost::property_tree::ptree& tree);

#ifdef __APPLE__
/**
 * @brief Parse a property list on disk into a property tree.
 *
 * @param path the path of the propery list which you'd like to read
 * @param tree a non-const reference to a Boost property tree, which will be
 * populated with the results of the property list
 *
 * @return an instance of Status, indicating the success or failure
 * of the operation.
 */
Status parsePlist(const boost::filesystem::path& path,
                  boost::property_tree::ptree& tree);

/**
 * @brief Parse property list content into a property tree.
 *
 * @param content a string reference to the content of a plist
 * @param tree a non-const reference to a Boost property tree, which will be
 * populated with the results of the property list
 *
 * @return an instance of Status, indicating the success or failure
 * of the operation.
 */
Status parsePlistContent(const std::string& content,
                         boost::property_tree::ptree& tree);
#endif

#ifdef __linux__
/**
 * @brief Iterate over proc process, returns a list of pids.
 *
 * @param processes output list of process pids as strings (int paths in proc).
 *
 * @return status of iteration.
 */
Status procProcesses(std::set<std::string>& processes);

/**
 * @brief Iterate over a proc process's descriptors, return a list of fds.
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
