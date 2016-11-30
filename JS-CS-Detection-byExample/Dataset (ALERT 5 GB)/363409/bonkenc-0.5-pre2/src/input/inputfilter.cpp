 /* BonkEnc version 0.5
  * Copyright (C) 2001-2002 Robert Kausch <robert.kausch@gmx.net>
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the "GNU General Public License".
  *
  * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. */

#include <input/inputfilter.h>

InputFilter::InputFilter(bonkEncConfig *config)
{
	fileSize = 0;
	currentConfig = config;
}

InputFilter::~InputFilter()
{
}

bool InputFilter::SetFileSize(unsigned int newFileSize)
{
	fileSize = newFileSize;

	return true;
}
