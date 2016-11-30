#!/usr/bin/env python

import argparse
import json
import logging
import os

logging_format = '[%(levelname)s] %(message)s'
logging.basicConfig(level=logging.INFO, format=logging_format)

SCRIPT_DIR = os.path.dirname(os.path.realpath(__file__))
REPO_ROOT_DIR = os.path.realpath(os.path.join(SCRIPT_DIR, "../.."))


def get_files_to_compile(json_data):
    files_to_compile = []
    for element in json_data:
        filename = element["file"]
        if not filename.endswith("tests.cpp") and \
                not filename.endswith("benchmarks.cpp") and \
                "third-party" not in filename and \
                "example" not in filename and \
                "generated/gen" not in filename and \
                "test_util" not in filename:
            base = filename.rfind("osquery/")
            filename = filename[base + len("osquery/"):]
            base_generated = filename.rfind("generated/")
            if base_generated >= 0:
                filename = filename[base_generated:]
            files_to_compile.append(filename)
    return files_to_compile

TARGETS_PREAMBLE = """
# DO NOT EDIT
# Automatically generated: make sync

thrift_library(
  name="if",
  languages=[
    "cpp",
    "py",
  ],
  thrift_srcs={
    "extensions/osquery.thrift": ["Extension", "ExtensionManager"],
  },
)

cpp_library(
  name="osquery_sdk",
  srcs=["""

TARGETS_POSTSCRIPT = """  ],
  deps=[
    "@/thrift/lib/cpp/concurrency:concurrency",
    ":if-cpp",
  ],
  external_deps=[
    "boost",
    "glog",
    "gflags",
    "gtest",
    "rocksdb",
    "libuuid",
  ],
  compiler_flags=[
    "-Wno-unused-function",
    "-Wno-non-virtual-dtor",
    "-Wno-address",
    "-Wno-overloaded-virtual",
    "-DOSQUERY_BUILD_VERSION=%s",
    "-DOSQUERY_BUILD_SDK_VERSION=%s",
    "-DOSQUERY_THRIFT_LIB=thrift/lib/cpp",
    "-DOSQUERY_THRIFT_SERVER_LIB=thrift/lib/cpp/server/example",
    "-DOSQUERY_THRIFT_POINTER=std",
    "-DOSQUERY_THRIFT=osquery/gen-cpp/",
  ],
)
"""

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=(
        "Generate a TARGETS files from CMake metadata"
    ))
    parser.add_argument("--input", "-i", required=True)
    parser.add_argument("--version", "-v", required=True)
    parser.add_argument("--sdk", required=True)
    args = parser.parse_args()

    try:
        with open(args.input, "r") as f:
            try:
                json_data = json.loads(f.read())
            except ValueError:
                logging.critical("Error: %s is not valid JSON" % args.input)

            source_files = get_files_to_compile(json_data)
            print(TARGETS_PREAMBLE)
            for source_file in source_files:
                print("    \"%s\"," % source_file)
            print(TARGETS_POSTSCRIPT % (args.version, args.sdk))

    except IOError:
        logging.critical("Error: %s doesn't exist" % args.input)
