 /* BonkEnc version 0.5
  * Copyright (C) 2001-2002 Robert Kausch <robert.kausch@gmx.net>
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the "GNU General Public License".
  *
  * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. */

#ifndef _H_FILTER_CDRIP_
#define _H_FILTER_CDRIP_

#include "inputfilter.h"

class FilterCDRip : public InputFilter
{
	private:
		int		 trackNumber;
		int		 trackSize;
		int		 byteCount;
		unsigned char	*buffer;
	public:
				 FilterCDRip(bonkEncConfig *);
				~FilterCDRip();

		bool		 EncodeData(unsigned char **, int, int *);
		bool		 DecodeData(unsigned char **, int, int *);

		bool		 SetTrack(int);
		int		 GetTrackSize();

		bonkFormatInfo	 GetAudioFormat();
};

#endif
