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
#include <vector>

#include <boost/filesystem.hpp>

#include <sqlite3.h>

#include <osquery/database/results.h>

#ifndef STR
#define STR_OF(x) #x
#define STR(x) STR_OF(x)
#endif

namespace osquery {

/**
 * @brief The version of osquery
 */
extern const std::string kVersion;

/// Use a macro for the version literal, set the kVersion symbol in the library.
#define OSQUERY_VERSION STR(OSQUERY_BUILD_VERSION)

/**
 * @brief A helpful tool type to report when logging, print help, or debugging.
 */
enum osqueryTool {
  OSQUERY_TOOL_SHELL,
  OSQUERY_TOOL_DAEMON,
  OSQUERY_TOOL_TEST,
};

/**
 * @brief Execute a query
 *
 * This is a lower-level version of osquery::SQL. Prefer to use osquery::SQL.
 *
 * @code{.cpp}
 *   std::string q = "SELECT * FROM time;";
 *   int i = 0;
 *   auto qd = query(q, i);
 *   if (i == 0) {
 *     for (const auto& each : qd) {
 *       for (const auto& it : each) {
 *         LOG(INFO) << it.first << ": " << it.second;
 *       }
 *     }
 *   } else {
 *     LOG(ERROR) << "Error: " << i;
 *   }
 * @endcode
 *
 * @param q the query to execute
 * @param error_return an int indicating the success or failure of the query
 *
 * @return the results of the query
 */
osquery::QueryData query(const std::string& q, int& error_return);

/**
 * @brief Execute a query on a specific database
 *
 * If you need to use a different database, other than the osquery default,
 * use this method and pass along a pointer to a SQLite3 database. This is
 * useful for testing.
 *
 * @param q the query to execute
 * @param error_return an int indicating the success or failure of the query
 * @param db the SQLite3 database the execute query q against
 *
 * @return the results of the query
 */
osquery::QueryData query(const std::string& q, int& error_return, sqlite3* db);

/**
 * @brief Return a fully configured sqlite3 database object
 *
 * An osquery database is basically just a SQLite3 database with several
 * virtual tables attached. This method is the main abstraction for creating
 * SQLite3 databases within osquery.
 *
 * @return a SQLite3 database with all virtual tables attached
 */
sqlite3* createDB();

/**
 * @brief Sets up various aspects of osquery execution state.
 *
 * osquery needs a few things to happen as soon as the executable begins
 * executing. initOsquery takes care of setting up the relevant parameters.
 * initOsquery should be called in an executable's `main()` function.
 *
 * @param argc the number of elements in argv
 * @param argv the command-line arguments passed to `main()`
 */
void initOsquery(int argc, char* argv[], int tool = OSQUERY_TOOL_TEST);

/**
 * @brief Split a given string based on an optional deliminator.
 *
 * If no deliminator is supplied, the string will be split based on whitespace.
 *
 * @param s the string that you'd like to split
 * @param delim the delimiter which you'd like to split the string by
 *
 * @return a vector of strings which represent the split string that you
 * passed as the s parameter.
 */
std::vector<std::string> split(const std::string& s,
                               const std::string& delim = "\t ");

/**
 * @brief Getter for a host's current hostname
 *
 * @return a string representing the host's current hostname
 */
std::string getHostname();

/**
 * @brief generate a uuid to uniquely identify this machine
 *
 * @return uuid string to identify this machine
 */
std::string generateHostUuid();

/**
 * @brief Getter for the current time, in a human-readable format.
 *
 * @return the current date/time in the format: "Wed Sep 21 10:27:52 2011"
 */
std::string getAsciiTime();

/**
 * @brief Getter for the current unix time.
 *
 * @return an int representing the amount of seconds since the unix epoch
 */
int getUnixTime();

/**
 * @brief Return a vector of all home directories on the system
 *
 * @return a vector of strings representing the path of all home directories
 */
std::vector<boost::filesystem::path> getHomeDirectories();

/**
 * @brief Inline helper function for use with utf8StringSize
 */
template <typename _Iterator1, typename _Iterator2>
inline size_t incUtf8StringIterator(_Iterator1& it, const _Iterator2& last) {
  if (it == last)
    return 0;
  unsigned char c;
  size_t res = 1;
  for (++it; last != it; ++it, ++res) {
    c = *it;
    if (!(c & 0x80) || ((c & 0xC0) == 0xC0))
      break;
  }

  return res;
}

/**
 * @brief Get the length of a UTF-8 string
 *
 * @param str The UTF-8 string
 *
 * @return the length of the string
 */
inline size_t utf8StringSize(const std::string& str) {
  size_t res = 0;
  std::string::const_iterator it = str.begin();
  for (; it != str.end(); incUtf8StringIterator(it, str.end()))
    res++;

  return res;
}

/**
 * @brief Create a pid file
 *
 * @return A status object indicating the success or failure of the operation
 */
Status createPidFile();
}
