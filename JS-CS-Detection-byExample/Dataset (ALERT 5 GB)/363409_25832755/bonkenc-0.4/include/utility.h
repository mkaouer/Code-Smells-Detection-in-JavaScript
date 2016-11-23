#ifndef UTILITY_H
#define UTILITY_H

#include <stdlib.h>
#include <vector>
#include <iolib-cxx.h>

typedef unsigned long  uint32;
typedef unsigned short uint16;
typedef unsigned char  uint8;
typedef short          int16;
typedef unsigned int   uint;

// Endian-safe int readers/writers:

// Throwable error structure:

struct error { 
  char *message; 
  error(char *_message) : message(_message) { } 
};

// Number of bits required to store a value:

int bits_to_store(uint32 i);

// Bitstream writer:

struct bitstream_out {
  OutStream *f_out;
  int byte;
  int bytes_written;
  int bit_no;

  void setup(OutStream *_f_out) {
    f_out = _f_out;
    byte = 0;
    bytes_written = 0;
    bit_no = 0;
  }

  void write(int bit) {
    if (bit)
      byte = (byte | (1<<bit_no));

    bit_no++;

    if (bit_no == 8) {
      f_out->OutputNumber(byte, 1);
      bytes_written++;
      byte = 0;
      bit_no = 0;
    }
  }

  void write_uint(uint value,int bits) {
    for(int i=0;i<bits;i++)
      write(value & (1<<i));
  }

  void write_uint_max(uint value,int max) {
    if (!max) return;
    int bits = bits_to_store(max);
    for(int i=0;i<bits-1;i++)
      write(value & (1<<i));
   
    if ( (value | (1<<(bits-1))) <= (uint) max )
      write(value & (1<<(bits-1)));
  }

  void flush() {
    while(bit_no != 0) 
      write(0);
  }
};

// Coder/decoder for lists of small signed ints:

const int adapt_level = 8;

void write_list(const vector<int> &list,bool base_2_part, bitstream_out &out);

#endif UTILITY_H