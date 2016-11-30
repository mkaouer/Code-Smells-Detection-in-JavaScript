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

#include <deque>
#include <map>
#include <string>
#include <vector>

#include <boost/property_tree/ptree.hpp>

#include <osquery/status.h>

namespace osquery {

/////////////////////////////////////////////////////////////////////////////
// Row
/////////////////////////////////////////////////////////////////////////////

/**
 * @brief A variant type for the SQLite type affinities.
 */
typedef std::string RowData;

/**
 * @brief A single row from a database query
 *
 * Row is a simple map where individual column names are keys, which map to
 * the Row's respective value
 */
typedef std::map<std::string, RowData> Row;

/**
 * @brief Serialize a Row into a property tree
 *
 * @param r the Row to serialize
 * @param tree a reference to a property tree which, if all operations are
 * completed successfully, the contents of Row will be serialized into
 *
 * @return an instance of osquery::Status, indicating the success or failure
 * of the operation
 */
Status serializeRow(const Row& r, boost::property_tree::ptree& tree);

/**
 * @brief Serialize a Row object into a JSON string
 *
 * @param r the Row to serialize
 * @param json a reference to a string which, if all operations are completed
 * successfully, the contents of Row will be serialized into
 *
 * @return an instance of osquery::Status, indicating the success or failure
 * of the operation
 */
Status serializeRowJSON(const Row& r, std::string& json);

Status deserializeRow(const boost::property_tree::ptree& tree, Row& r);
Status deserializeRowJSON(const std::string& json, Row& r);

/////////////////////////////////////////////////////////////////////////////
// QueryData
/////////////////////////////////////////////////////////////////////////////

/**
 * @brief The result set returned from a osquery SQL query
 *
 * QueryData is the canonical way to represent the results of SQL queries in
 * osquery. It's just a vector of Row's.
 */
typedef std::vector<Row> QueryData;

/**
 * @brief Serialize a QueryData object into a property tree
 *
 * @param q the QueryData to serialize
 * @param tree a reference to a property tree which, if all operations are
 * completed successfully, the contents of QueryData will be serialized into
 *
 * @return an instance of osquery::Status, indicating the success or failure
 * of the operation
 */
Status serializeQueryData(const QueryData& q,
                          boost::property_tree::ptree& tree);

/////////////////////////////////////////////////////////////////////////////
// DiffResults
/////////////////////////////////////////////////////////////////////////////

/**
 * @brief Data structure representing the difference between the results of
 * two queries
 *
 * The representation of two diffed QueryData result sets. Given and old and
 * new QueryData, DiffResults indicates the "added" subset of rows and the
 * "removed" subset of rows.
 */
struct DiffResults {
  /// vector of added rows
  QueryData added;

  /// vector of removed rows
  QueryData removed;

  /// equals operator
  bool operator==(const DiffResults& comp) const {
    return (comp.added == added) && (comp.removed == removed);
  }

  /// not equals operator
  bool operator!=(const DiffResults& comp) const { return !(*this == comp); }
};

/**
 * @brief Serialize a DiffResults object into a property tree
 *
 * @param d the DiffResults to serialize
 * @param tree a reference to a property tree which, if all operations are
 * completed successfully, the contents of DiffResults will be serialized into
 *
 * @return an instance of osquery::Status, indicating the success or failure
 * of the operation
 */
Status serializeDiffResults(const DiffResults& d,
                            boost::property_tree::ptree& tree);

/**
 * @brief Serialize a DiffResults object into a JSON string
 *
 * @param d the DiffResults to serialize
 * @param json a reference to a string which, if all operations are completed
 * successfully, the contents of DiffResults will be serialized into
 *
 * @return an instance of osquery::Status, indicating the success or failure
 * of the operation
 */
Status serializeDiffResultsJSON(const DiffResults& d, std::string& json);

/**
 * @brief Diff two QueryData objects and create a DiffResults object
 *
 * @param old_ the "old" set of results
 * @param new_ the "new" set of results
 *
 * @return a DiffResults object which indicates the change from old_ to new_
 *
 * @see DiffResults
 */
DiffResults diff(const QueryData& old_, const QueryData& new_);

/////////////////////////////////////////////////////////////////////////////
// HistoricalQueryResults
/////////////////////////////////////////////////////////////////////////////

/**
 * @brief A representation of scheduled query's historical results on disk
 *
 * In practice, a HistoricalQueryResults object is generated after inspecting
 * the persistent data storage.
 */
struct HistoricalQueryResults {
  /**
   * @brief the most recent results in the database
   *
   * mostRecentResults->first is the timestamp of the most recent results and
   * mostRecentResults->second is the query result data of the most recent
   */
  std::pair<int, QueryData> mostRecentResults;

  /// equals operator
  bool operator==(const HistoricalQueryResults& comp) const {
    return (comp.mostRecentResults == mostRecentResults);
  }

  /// not equals operator
  bool operator!=(const HistoricalQueryResults& comp) const {
    return !(*this == comp);
  }
};

/**
 * @brief Serialize a HistoricalQueryResults object into a property tree
 *
 * @param r the HistoricalQueryResults to serialize
 * @param tree a reference to a property tree which, if all operations are
 * completed successfully, the contents of HistoricalQueryResults will be
 * serialized into
 *
 * @return an instance of osquery::Status, indicating the success or failure
 * of the operation
 */
Status serializeHistoricalQueryResults(const HistoricalQueryResults& r,
                                       boost::property_tree::ptree& tree);

/**
 * @brief Serialize a HistoricalQueryResults object into a JSON string
 *
 * @param r the HistoricalQueryResults to serialize
 * @param json a reference to a string which, if all operations are completed
 * successfully, the contents of HistoricalQueryResults will be serialized
 * into
 *
 * @return an instance of osquery::Status, indicating the success or failure
 * of the operation
 */
Status serializeHistoricalQueryResultsJSON(const HistoricalQueryResults& r,
                                           std::string& json);

/**
 * @brief Deserialize a property tree into a HistoricalQueryResults object
 *
 * @param tree a property tree which contains a serialized
 * HistoricalQueryResults
 * @param r a reference to a HistoricalQueryResults object which, if all
 * operations are completed successfully, the contents of tree will be
 * serialized into
 *
 * @return an instance of osquery::Status, indicating the success or failure
 * of the operation
 */
Status deserializeHistoricalQueryResults(
    const boost::property_tree::ptree& tree, HistoricalQueryResults& r);

/**
 * @brief Deserialize JSON into a HistoricalQueryResults object
 *
 * @param json a string which contains a serialized HistoricalQueryResults
 * @param r a reference to a HistoricalQueryResults object which, if all
 * operations are completed successfully, the contents of json will be
 * serialized into
 *
 * @return an instance of osquery::Status, indicating the success or failure
 * of the operation
 */
Status deserializeHistoricalQueryResultsJSON(const std::string& json,
                                             HistoricalQueryResults& r);

/**
 * @brief Add a Row to a QueryData if the Row hasn't appeared in the QueryData
 * already
 *
 * Note that this function will iterate through the QueryData list until a
 * given Row is found (or not found). This shouldn't be that significant of an
 * overhead for most use-cases, but it's worth keeping in mind before you use
 * this in it's current state.
 *
 * @param q the QueryData list to append to
 * @param r the Row to add to q
 *
 * @return true if the Row was added to the QueryData, false if it wasn't
 */
bool addUniqueRowToQueryData(QueryData& q, const Row& r);

/**
 * @brief Construct a new QueryData from an existing one, replacing all
 * non-ascii characters with their \u encoding.
 *
 * This function is intended as a workaround for
 * https://svn.boost.org/trac/boost/ticket/8883,
 * and will allow rows containing data with non-ascii characters to be stored in
 * the database and parsed back into a property tree.
 *
 * @param oldData the old QueryData to copy
 * @param newData the new escaped QueryData object
 */
void escapeQueryData(const osquery::QueryData &oldData, osquery::QueryData &newData);
}
