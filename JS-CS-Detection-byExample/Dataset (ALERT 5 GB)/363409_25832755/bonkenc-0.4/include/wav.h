#ifndef WAV_H
#define WAV_H

#include <iolib-cxx.h>

void read_wav_header(InStream *file, int &n_channel, int &sampling_rate, int &length);

#endif /* WAV_H */
