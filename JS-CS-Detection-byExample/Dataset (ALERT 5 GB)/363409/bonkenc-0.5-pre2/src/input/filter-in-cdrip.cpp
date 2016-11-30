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
#include <input/filter-in-cdrip.h>
#include <cdrip/cdrip.h>
#include <main.h>
#include <memory.h>

#define PARANOIA_MODE_FULL        0xff
#define PARANOIA_MODE_DISABLE     0

#define PARANOIA_MODE_VERIFY      1
#define PARANOIA_MODE_FRAGMENT    2
#define PARANOIA_MODE_OVERLAP     4
#define PARANOIA_MODE_SCRATCH     8
#define PARANOIA_MODE_REPAIR      16
#define PARANOIA_MODE_NEVERSKIP   32

FilterCDRip::FilterCDRip(bonkEncConfig *config) : InputFilter(config)
{
	packageSize = 0;
	trackNumber = -1;
	buffer = NIL;
}

FilterCDRip::~FilterCDRip()
{
	if (buffer != NIL)
	{
		delete [] buffer;

		CR_CloseRipper();

		if (currentConfig->cdrip_locktray) CR_LockCD(false);
	}
}

bool FilterCDRip::EncodeData(unsigned char **data, int size, int *outsize)
{
	*outsize = size;

	return true;
}

bool FilterCDRip::DecodeData(unsigned char **data, int size, int *outsize)
{
	*outsize = size;

	if (trackNumber == -1) return true;

	if (byteCount >= trackSize)
	{
		if (buffer != NIL)
		{
			delete [] buffer;

			buffer = NIL;

			CR_CloseRipper();

			if (currentConfig->cdrip_locktray) CR_LockCD(false);
		}

		trackNumber = -1;

		return true;
	}

	LONG	 nBytes;
	BOOL	 abort = false;

	CR_RipChunk(buffer, &nBytes, abort);

	byteCount += nBytes;

	*outsize = nBytes;

	delete [] *data;

	*data = new unsigned char [*outsize];

	memcpy((void *) *data, (void *) buffer, *outsize);


	return true;
}

bool FilterCDRip::SetTrack(int newTrack)
{
	trackNumber = newTrack;

	int		 numTocEntries;
	TOCENTRY	 entry;
	SMOOTHString	 file = SMOOTH::StartDirectory;

	file.Append("bonkenc.ini");

	CR_SetTransportLayer(currentConfig->cdrip_ntscsi);

	CR_Init(file);

	CR_SetActiveCDROM(currentConfig->cdrip_activedrive);

	CR_ReadToc();

	numTocEntries = CR_GetNumTocEntries();

	entry.btTrackNumber = 0;

	for (int i = 0; i < numTocEntries; i++)
	{
		entry = CR_GetTocEntry(i);

		if (!(entry.btFlag & CDROMDATAFLAG) && (entry.btTrackNumber == trackNumber)) break;
		else entry.btTrackNumber = 0;
	}

	if (entry.btTrackNumber == 0)
	{
		trackNumber = -1;

		return false;
	}

	int		 startSector = entry.dwStartSector;
	int		 endSector = 0;
	TOCENTRY	 entry2 = CR_GetTocEntry(0);

	for (int j = 1; j <= numTocEntries; j++)
	{
		if (entry2.btTrackNumber == entry.btTrackNumber || entry2.btTrackNumber == 0xAA)

		{
			if ((j > 1) && (entry2.btFlag != CR_GetTocEntry(j).btFlag) && (CR_GetTocEntry(j).btTrackNumber != 0xAA))
			{
				endSector = CR_GetTocEntry(j).dwStartSector - 11250 - 1;
			}
			else
			{
				endSector = CR_GetTocEntry(j).dwStartSector - 1;
			}

			break;
		}

		entry2 = CR_GetTocEntry(j);
	}

	trackSize = (endSector - startSector + 1) * 2352;
	byteCount = 0;

	LONG		 bufferSize = 0;
	CDROMPARAMS	 params;
	int		 nParanoiaMode = PARANOIA_MODE_FULL ^ PARANOIA_MODE_NEVERSKIP;

	switch (currentConfig->cdrip_paranoia_mode)
	{
		case 0:
			nParanoiaMode = PARANOIA_MODE_OVERLAP;
			break;
		case 1:
			nParanoiaMode &= ~PARANOIA_MODE_VERIFY;
			break;
		case 2:
			nParanoiaMode &= ~(PARANOIA_MODE_SCRATCH | PARANOIA_MODE_REPAIR);
			break;
	}	
 

	CR_GetCDROMParameters(&params);

	params.nRippingMode = currentConfig->cdrip_paranoia;
	params.nParanoiaMode = nParanoiaMode;
	params.bSwapLefRightChannel = currentConfig->cdrip_swapchannels;
	params.bJitterCorrection = currentConfig->cdrip_jitter;

	CR_SetCDROMParameters(&params);

	if (currentConfig->cdrip_locktray) CR_LockCD(true);

	CR_OpenRipper(&bufferSize, startSector, endSector);

	buffer = new unsigned char [bufferSize];

	return true;
}

int FilterCDRip::GetTrackSize()
{
	if (trackNumber == -1) return 0;

	return trackSize;
}

bonkFormatInfo FilterCDRip::GetAudioFormat()
{
	bonkFormatInfo	 format;

	format.channels = 2;
	format.rate = 44100;
	format.bits = 16;
	format.length = GetTrackSize() / (format.bits / 8);
	format.order = BYTE_INTEL;

	return format;
}
