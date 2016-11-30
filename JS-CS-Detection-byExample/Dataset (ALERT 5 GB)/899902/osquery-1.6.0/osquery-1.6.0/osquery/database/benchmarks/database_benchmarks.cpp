/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <benchmark/benchmark.h>

#include <osquery/filesystem.h>
#include <osquery/database.h>

#include "osquery/core/test_util.h"
#include "osquery/database/db_handle.h"
#include "osquery/database/query.h"

namespace osquery {

QueryData getExampleQueryData(size_t x, size_t y) {
  QueryData qd;
  Row r;

  // Fill in a row with x;
  for (size_t i = 0; i < x; i++) {
    r["key" + std::to_string(i)] = std::to_string(i) + "content";
  }
  // Fill in the vector with y;
  for (size_t i = 0; i < y; i++) {
    qd.push_back(r);
  }
  return qd;
}

static void DATABASE_serialize(benchmark::State& state) {
  auto qd = getExampleQueryData(state.range_x(), state.range_y());
  while (state.KeepRunning()) {
    boost::property_tree::ptree tree;
    serializeQueryData(qd, tree);
  }
}

BENCHMARK(DATABASE_serialize)->ArgPair(1, 1)->ArgPair(10, 10)->ArgPair(10, 100);

static void DATABASE_serialize_json(benchmark::State& state) {
  auto qd = getExampleQueryData(state.range_x(), state.range_y());
  while (state.KeepRunning()) {
    std::string content;
    serializeQueryDataJSON(qd, content);
  }
}

BENCHMARK(DATABASE_serialize_json)
    ->ArgPair(1, 1)
    ->ArgPair(10, 10)
    ->ArgPair(10, 100);

static void DATABASE_diff(benchmark::State& state) {
  auto qd = getExampleQueryData(state.range_x(), state.range_y());
  while (state.KeepRunning()) {
    auto d = diff(qd, qd);
  }
}

BENCHMARK(DATABASE_diff)->ArgPair(1, 1)->ArgPair(10, 10)->ArgPair(10, 100);

static void DATABASE_query_results(benchmark::State& state) {
  auto qd = getExampleQueryData(state.range_x(), state.range_y());
  auto query = getOsqueryScheduledQuery();
  while (state.KeepRunning()) {
    DiffResults diff_results;
    auto dbq = Query("default", query);
    dbq.addNewResults(qd, diff_results);
  }
}

BENCHMARK(DATABASE_query_results)->ArgPair(1, 1)->ArgPair(10, 10)->ArgPair(10,
                                                                           100);

static void DATABASE_store(benchmark::State& state) {
  while (state.KeepRunning()) {
    setDatabaseValue(kPersistentSettings, "benchmark", "1");
  }
  // All benchmarks will share a single database handle.
  deleteDatabaseValue(kPersistentSettings, "benchmark");
}

BENCHMARK(DATABASE_store);

static void DATABASE_store_large(benchmark::State& state) {
  // Serialize the example result set into a string.
  std::string content;
  auto qd = getExampleQueryData(20, 100);
  serializeQueryDataJSON(qd, content);

  while (state.KeepRunning()) {
    setDatabaseValue(kPersistentSettings, "benchmark", content);
  }
  // All benchmarks will share a single database handle.
  deleteDatabaseValue(kPersistentSettings, "benchmark");
}

BENCHMARK(DATABASE_store_large);

static void DATABASE_store_append(benchmark::State& state) {
  // Serialize the example result set into a string.
  std::string content;
  auto qd = getExampleQueryData(20, 100);
  serializeQueryDataJSON(qd, content);

  size_t k = 0;
  while (state.KeepRunning()) {
    setDatabaseValue(kPersistentSettings, "key" + std::to_string(k++), content);
  }

  // All benchmarks will share a single database handle.
  for (size_t i = 0; i < k; ++i) {
    deleteDatabaseValue(kPersistentSettings, "key" + std::to_string(i));
  }
}

BENCHMARK(DATABASE_store_append);
}
