#ifndef __BONK__
#define __BONK__

#include <vector>
#include <iolib-cxx.h>
#include "utility.h"

class BONKencoder;

__declspec (dllexport) BONKencoder	*bonk_create_encoder(OutStream *, const char *, uint32, uint32, int, bool, bool, int, int, int, double);
__declspec (dllexport) bool		 bonk_close_encoder(BONKencoder *);

__declspec (dllexport) bool		 bonk_encode_packet(BONKencoder *, vector<int> &);

__declspec (dllexport) const char	*bonk_get_version_string();

#endif
