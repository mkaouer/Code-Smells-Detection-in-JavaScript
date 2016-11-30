/*
 *  Copyright (c) 2014, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

#include <string>
#include <cstring>

#include <zlib.h>

namespace osquery {

#define MOD_GZIP_ZLIB_WINDOWSIZE 15
#define MOD_GZIP_ZLIB_CFACTOR 9

void compress(std::string& data) {
  z_stream zs;
  memset(&zs, 0, sizeof(zs));

  if (deflateInit2(&zs,
                   Z_BEST_COMPRESSION,
                   Z_DEFLATED,
                   MOD_GZIP_ZLIB_WINDOWSIZE + 16,
                   MOD_GZIP_ZLIB_CFACTOR,
                   Z_DEFAULT_STRATEGY) != Z_OK) {
    return;
  }

  zs.next_in = (Bytef*)data.data();
  zs.avail_in = data.size();

  int ret = Z_OK;
  std::string output;

  {
    char buffer[16384] = {0};
    while (ret == Z_OK) {
      zs.next_out = reinterpret_cast<Bytef*>(buffer);
      zs.avail_out = sizeof(output);

      ret = deflate(&zs, Z_FINISH);
      if (output.size() < zs.total_out) {
        output.append(buffer, zs.total_out - output.size());
      }
    }
  }

  deflateEnd(&zs);
  if (ret != Z_STREAM_END) {
    return;
  }

  data = std::move(output);
}
}
