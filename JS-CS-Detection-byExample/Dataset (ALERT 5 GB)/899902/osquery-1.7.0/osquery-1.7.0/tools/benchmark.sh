#!/usr/bin/env bash

#  Copyright (c) 2014, Facebook, Inc.
#  All rights reserved.
#
#  This source code is licensed under the BSD-style license found in the
#  LICENSE file in the root directory of this source tree. An additional grant
#  of patent rights can be found in the PATENTS file in the same directory.

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
source $SCRIPT_DIR/lib.sh

# Run the build function without the tests
build false

# NODE_LABELS is defined in the Jenkins environment, and provides a wasy for
# us to detect what type of box we are running on.  (ie. osx10, centos6).
OUTDIR="$SCRIPT_DIR/../build/benchmarks"
NODE=$(echo $NODE_LABELS | awk '{print $NF}')
mkdir -p $OUTDIR

REPETITIONS=5

export BENCHMARK_TO_FILE="--benchmark_format=csv \
  --benchmark_repetitions=$REPETITIONS :>$OUTDIR/$NODE-benchmark.csv"
make run-benchmark/fast

export BENCHMARK_TO_FILE="--benchmark_format=csv \
  --benchmark_repetitions=$REPETITIONS :>$OUTDIR/$NODE-kernel-benchmark.csv"
make run-kernel-benchmark/fast

strip $(find $SCRIPT_DIR/../build -name "osqueryi" | xargs)
strip $(find $SCRIPT_DIR/../build -name "osqueryd" | xargs)
wc -c $(find $SCRIPT_DIR/../build -name "osqueryi" | xargs) \
  | head -n 1 \
  | awk '{print "\"EXECUTABLE_osqueryi_size\","$1",,,,,\""$2"\""}' \
    >>$OUTDIR/$NODE-benchmark.csv
wc -c $(find $SCRIPT_DIR/../build -name "osqueryd" | xargs) \
  | head -n 1 \
  | awk '{print "\"EXECUTABLE_osqueryd_size\","$1",,,,,\""$2"\""}' \
    >>$OUTDIR/$NODE-benchmark.csv

exit 0
