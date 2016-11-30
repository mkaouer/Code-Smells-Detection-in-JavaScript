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
#include <string>
#include <vector>

#include <boost/property_tree/ptree.hpp>

#include <osquery/registry.h>
#include <osquery/status.h>

namespace osquery {

/**
 * @brief A backing storage domain name, used for key/value based storage.
 *
 * There are certain "cached" variables such as a node-unique UUID or negotiated
 * 'node_key' following enrollment. If a value or setting must persist between
 * osqueryi or osqueryd runs it should be stored using the kPersistentSetting%s
 * domain.
 */
extern const std::string kPersistentSettings;

/// The "domain" where the results of scheduled queries are stored.
extern const std::string kQueries;

/// The "domain" where event results are stored, queued for querytime retrieval.
extern const std::string kEvents;

/**
 * @brief The "domain" where buffered log results are stored.
 *
 * Logger plugins may shuttle logs to a remote endpoint or API call
 * asynchronously. The backing store can be used to buffer results and status
 * logs until the logger plugin-specific thread decided to flush.
 */
extern const std::string kLogs;

/// An ordered list of column type names.
extern const std::vector<std::string> kDomains;

/////////////////////////////////////////////////////////////////////////////
// Row
/////////////////////////////////////////////////////////////////////////////

/**
 * @brief A variant type for the SQLite type affinities.
 */
using RowData = std::string;

/**
 * @brief A single row from a database query
 *
 * Row is a simple map where individual column names are keys, which map to
 * the Row's respective value
 */
using Row = std::map<std::string, RowData>;

/**
 * @brief Serialize a Row into a property tree
 *
 * @param r the Row to serialize
 * @param tree the output property tree
 *
 * @return Status indicating the success or failure of the operation
 */
Status serializeRow(const Row& r, boost::property_tree::ptree& tree);

/**
 * @brief Serialize a Row object into a JSON string
 *
 * @param r the Row to serialize
 * @param json the output JSON string
 *
 * @return Status indicating the success or failure of the operation
 */
Status serializeRowJSON(const Row& r, std::string& json);

/**
 * @brief Deserialize a Row object from a property tree
 *
 * @param tree the input property tree
 * @param r the output Row structure
 *
 * @return Status indicating the success or failure of the operation
 */
Status deserializeRow(const boost::property_tree::ptree& tree, Row& r);

/**
 * @brief Deserialize a Row object from a JSON string
 *
 * @param json the input JSON string
 * @param r the output Row structure
 *
 * @return Status indicating the success or failure of the operation
 */
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
using QueryData = std::vector<Row>;

/**
 * @brief Serialize a QueryData object into a property tree
 *
 * @param q the QueryData to serialize
 * @param tree the output property tree
 *
 * @return Status indicating the success or failure of the operation
 */
Status serializeQueryData(const QueryData& q,
                          boost::property_tree::ptree& tree);

/**
 * @brief Serialize a QueryData object into a JSON string
 *
 * @param q the QueryData to serialize
 * @param json the output JSON string
 *
 * @return Status indicating the success or failure of the operation
 */
Status serializeQueryDataJSON(const QueryData& q, std::string& json);

/// Inverse of serializeQueryData, convert property tree to QueryData.
Status deserializeQueryData(const boost::property_tree::ptree& tree,
                            QueryData& qd);

/// Inverse of serializeQueryDataJSON, convert a JSON string to QueryData.
Status deserializeQueryDataJSON(const std::string& json, QueryData& qd);

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
 * @param tree the output property tree
 *
 * @return Status indicating the success or failure of the operation
 */
Status serializeDiffResults(const DiffResults& d,
                            boost::property_tree::ptree& tree);

/**
 * @brief Serialize a DiffResults object into a JSON string
 *
 * @param d the DiffResults to serialize
 * @param json the output JSON string
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
 * @return true if the Row was added to the QueryData, false if it was not
 */
bool addUniqueRowToQueryData(QueryData& q, const Row& r);

/**
 * @brief Construct a new QueryData from an existing one, replacing all
 * non-ASCII characters with their \u encoding.
 *
 * This function is intended as a workaround for
 * https://svn.boost.org/trac/boost/ticket/8883,
 * and will allow rows containing data with non-ASCII characters to be stored in
 * the database and parsed back into a property tree.
 *
 * @param oldData the old QueryData to copy
 * @param newData the new escaped QueryData object
 */
void escapeQueryData(const QueryData& oldData, QueryData& newData);

/**
 * @brief performance statistics about a query
 */
struct QueryPerformance {
  /// Number of executions.
  size_t executions;

  /// Last UNIX time in seconds the query was executed successfully.
  size_t last_executed;

  /// Total wall time taken
  unsigned long long int wall_time;

  /// Total user time (cycles)
  unsigned long long int user_time;

  /// Total system time (cycles)
  unsigned long long int system_time;

  /// Average memory differentials. This should be near 0.
  unsigned long long int average_memory;

  /// Total characters, bytes, generated by query.
  unsigned long long int output_size;

  QueryPerformance()
      : executions(0),
        last_executed(0),
        wall_time(0),
        user_time(0),
        system_time(0),
        average_memory(0),
        output_size(0) {}
};

/**
 * @brief represents the relevant parameters of a scheduled query.
 *
 * Within the context of osqueryd, a scheduled query may have many relevant
 * attributes. Those attributes are represented in this data structure.
 */
struct ScheduledQuery {
  /// The SQL query.
  std::string query;

  /// How often the query should be executed, in second.
  size_t interval;

  /// A temporary splayed internal.
  size_t splayed_interval;

  /// Set of query options.
  std::map<std::string, bool> options;

  ScheduledQuery() : interval(0), splayed_interval(0) {}

  /// equals operator
  bool operator==(const ScheduledQuery& comp) const {
    return (comp.query == query) && (comp.interval == interval);
  }

  /// not equals operator
  bool operator!=(const ScheduledQuery& comp) const { return !(*this == comp); }
};

/////////////////////////////////////////////////////////////////////////////
// QueryLogItem
/////////////////////////////////////////////////////////////////////////////

/**
 * @brief Query results from a schedule, snapshot, or ad-hoc execution.
 *
 * When a scheduled query yields new results, we need to log that information
 * to our upstream logging receiver. A QueryLogItem contains metadata and
 * results in potentially-differential form for a logger.
 */
struct QueryLogItem {
  /// Differential results from the query.
  DiffResults results;

  /// Optional snapshot results, no differential applied.
  QueryData snapshot_results;

  /// The name of the scheduled query.
  std::string name;

  /// The identifier (hostname, or uuid) of the host.
  std::string identifier;

  /// The time that the query was executed, seconds as UNIX time.
  int time;

  /// The time that the query was executed, an ASCII string.
  std::string calendar_time;

  /// equals operator
  bool operator==(const QueryLogItem& comp) const {
    return (comp.results == results) && (comp.name == name);
  }

  /// not equals operator
  bool operator!=(const QueryLogItem& comp) const { return !(*this == comp); }
};

/**
 * @brief Serialize a QueryLogItem object into a property tree
 *
 * @param item the QueryLogItem to serialize
 * @param tree the output property tree
 *
 * @return Status indicating the success or failure of the operation
 */
Status serializeQueryLogItem(const QueryLogItem& item,
                             boost::property_tree::ptree& tree);

/**
 * @brief Serialize a QueryLogItem object into a JSON string
 *
 * @param item the QueryLogItem to serialize
 * @param json the output JSON string
 *
 * @return Status indicating the success or failure of the operation
 */
Status serializeQueryLogItemJSON(const QueryLogItem& item, std::string& json);

/// Inverse of serializeQueryLogItem, convert property tree to QueryLogItem.
Status deserializeQueryLogItem(const boost::property_tree::ptree& tree,
                               QueryLogItem& item);

/// Inverse of serializeQueryLogItem, convert a JSON string to QueryLogItem.
Status deserializeQueryLogItemJSON(const std::string& json, QueryLogItem& item);

/**
 * @brief Serialize a QueryLogItem object into a property tree
 * of events, a list of actions.
 *
 * @param item the QueryLogItem to serialize
 * @param tree the output property tree
 *
 * @return Status indicating the success or failure of the operation
 */
Status serializeQueryLogItemAsEvents(const QueryLogItem& item,
                                     boost::property_tree::ptree& tree);

/**
 * @brief Serialize a QueryLogItem object into a JSON string of events,
 * a list of actions.
 *
 * @param i the QueryLogItem to serialize
 * @param json the output JSON string
 *
 * @return Status indicating the success or failure of the operation
 */
Status serializeQueryLogItemAsEventsJSON(const QueryLogItem& i,
                                         std::vector<std::string>& items);

/////////////////////////////////////////////////////////////////////////////
// DistributedQueryRequest
/////////////////////////////////////////////////////////////////////////////

/**
 * @brief Small struct containing the query and ID information for a
 * distributed query
 */
struct DistributedQueryRequest {
 public:
  explicit DistributedQueryRequest() {}
  explicit DistributedQueryRequest(const std::string& q, const std::string& i)
      : query(q), id(i) {}

  /// equals operator
  bool operator==(const DistributedQueryRequest& comp) const {
    return (comp.query == query) && (comp.id == id);
  }

  std::string query;
  std::string id;
};

/**
 * @brief Serialize a DistributedQueryRequest into a property tree
 *
 * @param r the DistributedQueryRequest to serialize
 * @param tree the output property tree
 *
 * @return Status indicating the success or failure of the operation
 */
Status serializeDistributedQueryRequest(const DistributedQueryRequest& r,
                                        boost::property_tree::ptree& tree);

/**
 * @brief Serialize a DistributedQueryRequest object into a JSON string
 *
 * @param r the DistributedQueryRequest to serialize
 * @param json the output JSON string
 *
 * @return Status indicating the success or failure of the operation
 */
Status serializeDistributedQueryRequestJSON(const DistributedQueryRequest& r,
                                            std::string& json);

/**
 * @brief Deserialize a DistributedQueryRequest object from a property tree
 *
 * @param tree the input property tree
 * @param r the output DistributedQueryRequest structure
 *
 * @return Status indicating the success or failure of the operation
 */
Status deserializeDistributedQueryRequest(
    const boost::property_tree::ptree& tree, DistributedQueryRequest& r);

/**
 * @brief Deserialize a DistributedQueryRequest object from a JSON string
 *
 * @param json the input JSON string
 * @param r the output DistributedQueryRequest structure
 *
 * @return Status indicating the success or failure of the operation
 */
Status deserializeDistributedQueryRequestJSON(const std::string& json,
                                              DistributedQueryRequest& r);

/////////////////////////////////////////////////////////////////////////////
// DistributedQueryResult
/////////////////////////////////////////////////////////////////////////////

/**
 * @brief Small struct containing the results of a distributed query
 */
struct DistributedQueryResult {
 public:
  explicit DistributedQueryResult() {}
  explicit DistributedQueryResult(const DistributedQueryRequest& req,
                                  const QueryData& res)
      : request(req), results(res) {}

  /// equals operator
  bool operator==(const DistributedQueryResult& comp) const {
    return (comp.request == request) && (comp.results == results);
  }

  DistributedQueryRequest request;
  QueryData results;
};

/**
 * @brief Serialize a DistributedQueryResult into a property tree
 *
 * @param r the DistributedQueryResult to serialize
 * @param tree the output property tree
 *
 * @return Status indicating the success or failure of the operation
 */
Status serializeDistributedQueryResult(const DistributedQueryResult& r,
                                       boost::property_tree::ptree& tree);

/**
 * @brief Serialize a DistributedQueryResult object into a JSON string
 *
 * @param r the DistributedQueryResult to serialize
 * @param json the output JSON string
 *
 * @return Status indicating the success or failure of the operation
 */
Status serializeDistributedQueryResultJSON(const DistributedQueryResult& r,
                                           std::string& json);

/**
 * @brief Deserialize a DistributedQueryResult object from a property tree
 *
 * @param tree the input property tree
 * @param r the output DistributedQueryResult structure
 *
 * @return Status indicating the success or failure of the operation
 */
Status deserializeDistributedQueryResult(
    const boost::property_tree::ptree& tree, DistributedQueryResult& r);

/**
 * @brief Deserialize a DistributedQueryResult object from a JSON string
 *
 * @param json the input JSON string
 * @param r the output DistributedQueryResult structure
 *
 * @return Status indicating the success or failure of the operation
 */
Status deserializeDistributedQueryResultJSON(const std::string& json,
                                             DistributedQueryResult& r);

/////////////////////////////////////////////////////////////////////////////

/**
 * @brief An osquery backing storage (database) type that persists executions.
 *
 * The osquery tools need a high-performance storage and indexing mechanism for
 * storing intermediate results from EventPublisher%s, persisting one-time
 * generated values, and performing non-memory backed differentials.
 *
 * Practically, osquery is built around RocksDB's performance guarantees and
 * all of the internal APIs expect RocksDB's indexing and read performance.
 * However, access to this representation of a backing-store is still abstracted
 * to removing RocksDB as a dependency for the osquery SDK.
 */
class DatabasePlugin : public Plugin {
 protected:
  /**
   * @brief Perform a domain and key lookup from the backing store.
   *
   * Database value access indexing is abstracted into domains and keys.
   * Both are string values but exist separately for simple indexing without
   * API-enforcing tokenization. In some cases we do add a component-specific
   * tokeninzation to keys.
   *
   * @param domain A string value representing abstract storage indexing.
   * @param key A string value representing the lookup/retrieval key.
   * @param value The output parameter, left empty if the key does not exist.
   * @return Failure if the data could not be accessed. It is up to the plugin
   * to determine if a missing key means a non-success status.
   */
  virtual Status get(const std::string& domain,
                     const std::string& key,
                     std::string& value) const = 0;

  /**
   * @brief Store a string-represented value using a domain and key index.
   *
   * See DatabasePlugin::get for discussion around domain and key use.
   *
   * @param domain A string value representing abstract storage indexing.
   * @param key A string value representing the lookup/retrieval key.
   * @param value A string value representing the data.
   * @return Failure if the data could not be stored. It is up to the plugin
   * to determine if a conflict/overwrite should return different status text.
   */
  virtual Status put(const std::string& domain,
                     const std::string& key,
                     const std::string& value) = 0;

  /// Data removal method.
  virtual Status remove(const std::string& domain, const std::string& k) = 0;

  /// Key/index lookup method.
  virtual Status scan(const std::string& domain,
                      std::vector<std::string>& results,
                      size_t max = 0) const {
    return Status(0, "Not used");
  }

 public:
  Status call(const PluginRequest& request, PluginResponse& response);
};

/**
 * @brief Lookup a value from the active osquery DatabasePlugin storage.
 *
 * See DatabasePlugin::get for discussion around domain and key use.
 * Extensions, components, plugins, and core code should use getDatabaseValue
 * as a wrapper around the current tool's choice of a backing storage plugin.
 *
 * @param domain A string value representing abstract storage indexing.
 * @param key A string value representing the lookup/retrieval key.
 * @param value The output parameter, left empty if the key does not exist.
 * @return Storage operation status.
 */
Status getDatabaseValue(const std::string& domain,
                        const std::string& key,
                        std::string& value);

/**
 * @brief Set or put a value into the active osquery DatabasePlugin storage.
 *
 * See DatabasePlugin::get for discussion around domain and key use.
 * Extensions, components, plugins, and core code should use setDatabaseValue
 * as a wrapper around the current tool's choice of a backing storage plugin.
 *
 * @param domain A string value representing abstract storage indexing.
 * @param key A string value representing the lookup/retrieval key.
 * @param value A string value representing the data.
 * @return Storage operation status.
 */
Status setDatabaseValue(const std::string& domain,
                        const std::string& key,
                        const std::string& value);

/// Remove a domain/key identified value from backing-store.
Status deleteDatabaseValue(const std::string& domain, const std::string& key);

/// Get a list of keys for a given domain.
Status scanDatabaseKeys(const std::string& domain,
                        std::vector<std::string>& keys,
                        size_t max = 0);

/// Allow callers to scan each column family and print each value.
void dumpDatabase();

/// Generate a specific-use registry for database access abstraction.
CREATE_REGISTRY(DatabasePlugin, "database");
}
