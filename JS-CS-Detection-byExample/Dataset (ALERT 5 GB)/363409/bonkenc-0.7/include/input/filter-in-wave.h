 /* BonkEnc version 0.7
  * Copyright (C) 2001-2002 Robert Kausch <robert.kausch@gmx.net>
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the "GNU General Public License".
  *
  * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. */

#ifndef _H_FILTER_IN_WAVE_
#define _H_FILTER_IN_WAVE_

#include "inputfilter.h"

class FilterInWAVE : public InputFilter
{
	private:
		bool		 setup;
		bonkFormatInfo	 format;
	public:
				 FilterInWAVE(bonkEncConfig *);
				~FilterInWAVE();

		bool		 EncodeData(unsigned char **, int, int *);
		bool		 DecodeData(unsigned char **, int, int *);

		bonkFormatInfo	 GetAudioFormat();
};

#endif
