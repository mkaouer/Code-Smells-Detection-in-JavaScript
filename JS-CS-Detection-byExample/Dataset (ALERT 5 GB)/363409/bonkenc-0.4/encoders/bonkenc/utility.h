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

int bits_to_store(uint32 i) {
  int result = 0;
  while(i) {
    result++;
    i >>= 1;
  }
  return result;
}

// Coder/decoder for lists of small signed ints:

void write_list(const vector<int> &list,bool base_2_part, bitstream_out &out) {
  //Store a list of integers concisely

  //If large magnitude, store low order bits in base 2

  int low_bits = 0;

  if (base_2_part) {
    int energy = 0;
    for(int i=0;i<list.size();i++)
      energy += abs(list[i]);
  
    low_bits = bits_to_store(energy/(list.size()*2));
    if (low_bits > 15) low_bits = 15;

    out.write_uint(low_bits, 4);
  }

  vector<int> copy(list.size());
  for(int i=0;i<list.size();i++) {
    out.write_uint(abs(list[i]),low_bits);
    copy[i] = abs(list[i]) >> low_bits;
  }

  //Convert list into bitstream:
  
  vector<uint8> bits;

  int max = 0;
  for(int i=0;i<copy.size();i++)
    if (copy[i] > max)
      max = abs(copy[i]);
  
  for(int i=0;i<=max;i++) 
    for(int j=0;j<copy.size();j++)
      if (copy[j] >= i)
        bits.push_back(copy[j] > i);
  
  // Store bitstream:
  
  int step = 256;
  int pos = 0;
  bool dominant = false;
  while(pos < bits.size()) {
    int steplet = step>>8;
    if (pos+steplet > bits.size())
      steplet = bits.size()-pos;

    bool any = false;;
    for(int i=0;i<steplet;i++)
      if (bits[i+pos] != dominant)
        any = true;

    out.write(any);

    if (!any) {
      pos  += steplet;
      step += step / adapt_level;
    } else {
      int interloper=0;
      while(bits[pos+interloper] == dominant) interloper++;

      //Note change.
      out.write_uint_max(interloper,(step>>8)-1);
      pos += interloper+1;
      step -= step / adapt_level;
    }

    if (step < 256) {
      step = 65536 / step;
      dominant = !dominant;
    }
  }

  // Store signs:

  for(int i=0;i<list.size();i++)
    if (list[i])
      out.write(list[i]<0);
}

#endif UTILITY_H
