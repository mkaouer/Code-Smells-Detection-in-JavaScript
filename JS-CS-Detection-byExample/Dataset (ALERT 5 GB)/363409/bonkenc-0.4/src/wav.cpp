#include <iolib-cxx.h>

typedef unsigned long  uint32;
typedef unsigned short uint16;
typedef unsigned char  uint8;

void read_wav_header(InStream *file, int &n_channel, int &sampling_rate, int &length)
{
  //Add more checking to this!
    
  //Read RIFF chunk
  for(int i=0;i<12;i++)
    file->InputNumber(1);
    
  //Read FMT chunk
  for(int i=0;i<10;i++)
    file->InputNumber(1);

  n_channel = uint16(file->InputNumber(2));
  sampling_rate = uint32(file->InputNumber(4));

  for(int i=0;i<8;i++)
    file->InputNumber(1);
    
  //Read DATA chunk
  for(int i=0;i<4;i++)
    file->InputNumber(1);

  length = uint32(file->InputNumber(4)) / 2;
}
