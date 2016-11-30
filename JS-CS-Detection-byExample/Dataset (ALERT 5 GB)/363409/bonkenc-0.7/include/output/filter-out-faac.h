 /* BonkEnc version 0.7
  * Copyright (C) 2001-2002 Robert Kausch <robert.kausch@gmx.net>
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the "GNU General Public License".
  *
  * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. */

#ifndef _H_FILTER_OUT_FAAC_
#define _H_FILTER_OUT_FAAC_

#include "outputfilter.h"
#include <faac/faac.h>

class FilterOutFAAC : public OutputFilter
{
	private:
		faacEncHandle		 handle;
		faacEncConfigurationPtr	 fConfig;

		unsigned long		 samples_size;
		unsigned long		 buffersize;
	public:
					 FilterOutFAAC(bonkEncConfig *, bonkFormatInfo *);
					~FilterOutFAAC();

		bool			 EncodeData(unsigned char **, int, int *);
		bool			 DecodeData(unsigned char **, int, int *);
};

#endif
