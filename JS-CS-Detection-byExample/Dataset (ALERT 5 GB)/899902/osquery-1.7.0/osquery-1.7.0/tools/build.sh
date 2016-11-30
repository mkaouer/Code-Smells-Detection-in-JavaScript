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

# To request that tests run 'make deps' before building.
# Define or uncomment the following control variable: RUN_BUILD_DEPS
# $ export RUN_BUILD_DEPS=1

# To request that tests include additional 'release' or 'package' units.
# Define or uncomment the following control variable: RUN_RELEASE_TESTS
# $ export RUN_RELEASE_TESTS=1

# Run the build function and the tests
build true

exit 0
