#!/usr/bin/env bash

#  Copyright (c) 2014, Facebook, Inc.
#  All rights reserved.
#
#  This source code is licensed under the BSD-style license found in the
#  LICENSE file in the root directory of this source tree. An additional grant
#  of patent rights can be found in the PATENTS file in the same directory.

function main_darwin() {
  type brew >/dev/null 2>&1 || {
    fatal "could not find homebrew. please install it from http://brew.sh/";
  }

  type pip >/dev/null 2>&1 || {
    fatal "could not find pip. please install it using 'sudo easy_install pip'";
  }

  brew update

  package wget
  package cmake
  package makedepend
  package doxygen
  package boost
  package gflags
  package lz4
  package snappy
  package rocksdb
  package thrift
  package yara
  package libressl
  package cpp-netlib
  package google-benchmark
  package libmagic
  package sleuthkit
}
