 /* BonkEnc version 0.5
  * Copyright (C) 2001-2002 Robert Kausch <robert.kausch@gmx.net>
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the "GNU General Public License".
  *
  * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. */

#include <iolib-cxx.h>
#include <output/filter-out-lame.h>
#include <main.h>
#include <memory.h>

FilterLAME::FilterLAME(bonkEncConfig *config, bonkFormatInfo *format) : OutputFilter(config, format)
{
	packageSize = 0;
}

FilterLAME::~FilterLAME()
{
}

bool FilterLAME::EncodeData(unsigned char **data, int size, int *outsize)
{
	*outsize = size;

	return true;
}

bool FilterLAME::DecodeData(unsigned char **data, int size, int *outsize)
{
	*outsize = size;

	return true;
}
