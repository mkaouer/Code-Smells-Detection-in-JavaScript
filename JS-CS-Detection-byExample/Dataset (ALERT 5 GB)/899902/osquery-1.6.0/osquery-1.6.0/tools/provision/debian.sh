#!/usr/bin/env bash

# g++ stuff in wheezy has a bug which has been fixed in gcc 4.7.3 which conveniently is exactly 
# the version that is not shipped in wheezy
# See https://gcc.gnu.org/viewcvs/gcc/branches/gcc-4_7-branch/libstdc%2B%2B-v3/include/std/condition_variable?view=patch&r1=189276&r2=193528&pathrev=193528

function patch_condition_variable() {
  TARGET_DIR=/usr/include/c++/4.7
  pushd $TARGET_DIR
  
  if [[ ! -f condition_variable.orig ]]; then   
    sudo cp condition_variable condition_variable.orig
    sudo patch -p5 < $FILES_DIR/debian/condition_variable.patch

    log "patched $TARGET_DIR/condition_variable which prevents rocksdb from building"
  else
    log "$TARGET_DIR/condition_variable already patched: found a .orig"
  fi
  
  popd
}

# As long as https://github.com/google/benchmark/issues/134 isn't resolved, we need this: 
function install_google_benchmark_with_patch() {
  SOURCE=benchmark-0.1.0
  TARBALL=$SOURCE.tar.gz
  URL=$DEPS_URL/$TARBALL

  if provision benchmark /usr/local/lib/libbenchmark.a; then
    pushd $SOURCE
    # function deviates from install_google_benchmark starting here 
    patch -p0 < $FILES_DIR/debian/benchmark_traits_fix.patch
    # end change 
    mkdir -p build
    pushd build
    cmake -DCMAKE_CXX_FLAGS="$CFLAGS" ..
    CC="$CC" CXX="$CXX" make -j $THREADS
    sudo make install
    popd
    popd
  fi
}
  
function main_debian() {
  sudo apt-get update -y
  sudo apt-get upgrade -y

  package git-core
  package wget
  
  # Add LLVM to the APT sources 
  
  if [[ $DISTRO == "wheezy" ]]; then 
    set_cc clang
    set_cxx clang++
  
    if [[ ! -f /usr/bin/clang ]]; then      
      wget -O - http://llvm.org/apt/llvm-snapshot.gpg.key|sudo apt-key add -
      sudo sh -c "echo 'deb http://llvm.org/apt/wheezy/ llvm-toolchain-wheezy-3.4-binaries main' > /etc/apt/sources.list.d/llvm.list"
  
      sudo apt-get update -y
    
      package clang-3.4
      package lldb-3.4
    fi
  fi
  
  package unzip
  package build-essential
  package libtool
  package autoconf
  package pkg-config
  
  package bison
  package flex
  package devscripts
  package debhelper
  package python-pip
  package python-dev

  package ruby-dev
  package gcc
  package doxygen

  package autopoint
  package libssl-dev
  package liblzma-dev
  package uuid-dev
  package libpopt-dev
  package libdpkg-dev
  package libudev-dev
  package libblkid-dev

  package libbz2-dev
  package libreadline-dev

  package g++-multilib
  package iptables-dev

  package libsnappy-dev
  package libgflags-dev

  package libaudit-dev
  package libmagic-dev
  
  if [[ $DISTRO == "wheezy" ]]; then 
    patch_condition_variable
    
    install_cmake
    install_boost
    install_google_benchmark_with_patch   
  elif [[ $DISTRO == "jessie" ]]; then 
    package cmake
    package libboost-all-dev
    install_google_benchmark
  fi 
  

  install_thrift
  install_rocksdb 
  install_yara
  install_cppnetlib
  install_gflags
  
  # Need headers and PC macros
  package libgcrypt-dev
  package libdevmapper-dev

  package libcryptsetup-dev
}
