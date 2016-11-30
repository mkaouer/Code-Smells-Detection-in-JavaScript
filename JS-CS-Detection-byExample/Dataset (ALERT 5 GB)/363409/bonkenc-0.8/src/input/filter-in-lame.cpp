 /* BonkEnc version 0.8
  * Copyright (C) 2001-2002 Robert Kausch <robert.kausch@gmx.net>
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the "GNU General Public License".
  *
  * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. */

#include <iolib-cxx.h>
#include <input/filter-in-lame.h>
#include <dllinterfaces.h>

FilterInLAME::FilterInLAME(bonkEncConfig *config) : InputFilter(config)
{
	packageSize = 0;
}

FilterInLAME::~FilterInLAME()
{
}

bool FilterInLAME::Activate()
{
	ex_lame_decode_init();

	int	 size = 4096;

	do
	{
		unsigned char	*data = new unsigned char [size];

		driver->ReadData(data, size);

		short	*pcm_l = new short [size * 32];
		short	*pcm_r = new short [size * 32];

		mp3data_struct	 mp3data;

		ex_lame_decode_headers(data, size, pcm_l, pcm_r, &mp3data);

		format.order = BYTE_INTEL;
		format.channels = mp3data.stereo;
		format.rate = mp3data.samplerate;
		format.bits = 16;
		format.length = -1;

		delete [] data;

		if (mp3data.stereo <= 2) break;
	}
	while (driver->GetPos() < driver->GetSize());

	driver->Seek(0);

	return true;
}

bool FilterInLAME::Deactivate()
{
	return true;
}

int FilterInLAME::ReadData(unsigned char **data, int size)
{
	if (size <= 0) return -1;

	inBytes += size;

	*data = new unsigned char [size];

	driver->ReadData(*data, size);

	short	*pcm_l = new short [size * 32];
	short	*pcm_r = new short [size * 32];

	mp3data_struct	 mp3data;
	int		 nsamples = ex_lame_decode_headers(*data, size, pcm_l, pcm_r, &mp3data);

	delete [] *data;

	*data = new unsigned char [nsamples * 4];

	for (int i = 0; i < nsamples; i++)
	{
		((short *) *data)[2 * i]	= pcm_l[i];
		((short *) *data)[2 * i + 1]	= pcm_r[i];
	}

	delete [] pcm_l;
	delete [] pcm_r;

	return nsamples * 4;
}

bonkFormatInfo FilterInLAME::GetAudioFormat()
{
	return format;
}
