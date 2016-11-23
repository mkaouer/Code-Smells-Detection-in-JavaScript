 /* BonkEnc version 0.8
  * Copyright (C) 2001-2002 Robert Kausch <robert.kausch@gmx.net>
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the "GNU General Public License".
  *
  * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. */

#ifndef _H_FILTER_IN_AIFF_
#define _H_FILTER_IN_AIFF_

#include "inputfilter.h"

class FilterInAIFF : public InputFilter
{
	private:
		bonkFormatInfo	 format;
		bool		 setup;
	public:
				 FilterInAIFF(bonkEncConfig *);
				~FilterInAIFF();

		int		 ReadData(unsigned char **, int);

		bonkFormatInfo	 GetAudioFormat();
};

#endif
