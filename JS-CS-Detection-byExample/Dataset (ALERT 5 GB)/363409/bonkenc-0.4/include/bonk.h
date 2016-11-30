 /* BonkEnc version 0.4
  * Copyright (C) 2001 Robert Kausch <robert.kausch@gmx.net>
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the "GNU General Public License".
  *
  * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. */

#ifndef __BONK__
#define __BONK__

#include <utility.h>
#include <vector>
#include <iolib-cxx.h>

class __declspec (dllexport) BONKencoder
{
	private:
		OutStream	*f_out;
		bitstream_out	 bit_out;
		int		 channels, rate;
		bool		 lossless;
		bool		 mid_side;
		int		 n_taps;
		int		 down_sampling, samples_per_packet;
		double		 quant_level;
		int		 sample_count; 
		vector<int>	 tail;

		vector<vector<int> >	 output_samples;
	public:
		int		 samples_size;
		void		 begin(OutStream *, const char *, uint32, uint32, int, bool, bool, int, int, int, double);
		void		 finish();
		void		 store_packet(vector<int> &);
};

#endif
