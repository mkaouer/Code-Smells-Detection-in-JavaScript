#!/usr/bin/env bash

#  Copyright (c) 2014, Facebook, Inc.
#  All rights reserved.
#
#  This source code is licensed under the BSD-style license found in the
#  LICENSE file in the root directory of this source tree. An additional grant
#  of patent rights can be found in the PATENTS file in the same directory.

function main_centos() {
  sudo yum update -y

  package texinfo
  package wget
  package git-all
  package unzip
  package xz
  package xz-devel
  package epel-release
  package python-pip
  package python-devel
  package rpm-build
  package ruby-devel
  package rubygems

  if [[ $DISTRO = "centos6" ]]; then
    pushd /etc/yum.repos.d
    if [[ ! -f /etc/yum.repos.d/devtools-2.repo ]]; then
      sudo wget http://people.centos.org/tru/devtools-2/devtools-2.repo
    fi

    package devtoolset-2-gcc
    package devtoolset-2-binutils
    package devtoolset-2-gcc-c++

    if [[ ! -e /usr/bin/gcc ]]; then
      sudo ln -s /opt/rh/devtoolset-2/root/usr/bin/gcc /usr/bin/gcc
    fi
    if [[ ! -e /usr/bin/g++ ]]; then
      sudo ln -s /opt/rh/devtoolset-2/root/usr/bin/gcc /usr/bin/g++
    fi

    source /opt/rh/devtoolset-2/enable
    if [[ ! -d /usr/lib/gcc ]]; then
      sudo ln -s /opt/rh/devtoolset-2/root/usr/lib/gcc /usr/lib/
    fi
    popd

  elif [[ $DISTRO = "centos7" ]]; then
    package gcc
    package binutils
    package gcc-c++
  fi

  package clang
  package clang-devel

  package bzip2
  package bzip2-devel
  package openssl-devel
  package readline-devel
  package rpm-devel
  package rpm-build
  package libblkid-devel

  install_cmake

  set_cc clang
  set_cxx clang++

  install_boost

  if [[ $DISTRO = "centos6" ]]; then
    package libudev-devel
    package cryptsetup-luks-devel
  elif [[ $DISTRO = "centos7" ]]; then
    package systemd-devel
    package cryptsetup-devel
  fi

  install_gflags

  package doxygen
  package byacc
  package flex
  package bison

  remove_package libunwind-devel

  if [[ $DISTRO = "centos6" ]]; then
    install_autoconf
    install_automake
    install_libtool
  elif [[ $DISTRO = "centos7" ]]; then
    package autoconf
    package automake
    package libtool
  fi

  install_snappy
  install_rocksdb
  install_thrift
  install_yara

  gem_install fpm
}
