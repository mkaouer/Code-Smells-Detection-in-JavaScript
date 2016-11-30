 /* BonkEnc version 0.8
  * Copyright (C) 2001-2002 Robert Kausch <robert.kausch@gmx.net>
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the "GNU General Public License".
  *
  * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. */

#ifndef _H_INPUTFILTER_
#define _H_INPUTFILTER_

#include <iolib-cxx.h>
#include "../main.h"

class InputFilter : public IOLibFilter
{
	protected:
		int			 inBytes;

		unsigned int		 fileSize;
		bonkEncConfig		*currentConfig;
	public:
		int			 error;

					 InputFilter(bonkEncConfig *);
					~InputFilter();

		virtual int		 ReadData(unsigned char **, int) = 0;

		virtual bonkFormatInfo	 GetAudioFormat() = 0;

		virtual bool		 SetFileSize(unsigned int);

		int			 GetInBytes();
};

#endif
