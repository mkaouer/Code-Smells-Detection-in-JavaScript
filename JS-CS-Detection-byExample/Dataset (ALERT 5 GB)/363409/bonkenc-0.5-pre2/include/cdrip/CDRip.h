/*
** Copyright (C) 1999 Albert L. Faber
**  
** This program is free software; you can redistribute it and/or modify
** it under the terms of the GNU General Public License as published by
** the Free Software Foundation; either version 2 of the License, or
** (at your option) any later version.
** 
** This program is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
** GNU General Public License for more details.
** 
** You should have received a copy of the GNU General Public License
** along with this program; if not, write to the Free Software 
** Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
*/

#ifndef CDRIP_INCLUDED
#define CDRIP_INCLUDED

#pragma pack(push,4)

#ifdef _WIN32
  #define CCONV _stdcall
#endif

#undef DLLEXPORT

#define DLLEXPORT 
#ifdef CDEX_DLL
//	#define DLLEXPORT __declspec(dllexport)
#else
//	#define DLLEXPORT __declspec(import)
#endif

#define		CDEX_ERR							LONG
#define		CDEX_OK								0x00000000
#define		CDEX_ERROR							0x00000001
#define		CDEX_FILEOPEN_ERROR					0x00000002
#define		CDEX_JITTER_ERROR					0x00000003
#define		CDEX_RIPPING_DONE					0x00000004
#define		CDEX_RIPPING_INPROGRESS				0x00000005


#define		TRANSPLAYER_ASPI						(0)
#define		TRANSPLAYER_NTSCSI						(1)

#define CDROMDATAFLAG 0x04
#define AUDIOTRKFLAG 0x10


#define CR_RIPPING_MODE_NORMAL						( 0 )
#define CR_RIPPING_MODE_PARANOIA					( 1 )


// forward class declaration
class CIni;


typedef struct SENSKEY_TAG
{
	BYTE	SK;
	BYTE	ASC;
	BYTE	ASCQ;
} SENSEKEY;


enum DRIVETYPE
{
	GENERIC=0,
	TOSHIBA,
	TOSHIBANEW,
	IBM,
	NEC,
	DEC,
	IMS,
	KODAK,
	RICOH,
	HP,
	PHILIPS,
	PLASMON,
	GRUNDIGCDR100IPW,
	MITSUMICDR,
	PLEXTOR,
	SONY,
	YAMAHA,
	NRC,
	IMSCDD5,
	CUSTOMDRIVE,
	NUMDRIVETYPES
};

enum READMETHOD
{
	READMMC=0,
	READ10,
	READNEC,
	READSONY,
	READMMC2,
	READMMC3,
	READC1,
	READC2,
	READC3,
	READMMC4,
	NUMREADMETHODS
};

enum SETSPEED
{
	SPEEDNONE=0,
	SPEEDMMC,
	SPEEDSONY,
	SPEEDYAMAHA,
	SPEEDTOSHIBA,
	SPEEDPHILIPS,
	SPEEDNEC,
	NUMSPEEDMETHODS
};

enum ENDIAN
{
	BIGENDIAN=0,
	LITTLEENDIAN,
	NUMENDIAN
};

enum ENABLEMODE
{
	ENABLENONE=0,
	ENABLESTD,
	NUMENABLEMODES
};


typedef struct DRIVETABLE_TAG
{
	DRIVETYPE	DriveType;
	READMETHOD	ReadMethod;
	SETSPEED	SetSpeed;
	ENDIAN		Endian;
	ENABLEMODE	EnableMode;
	LONG		nDensity;
	BOOL		bAtapi;
} DRIVETABLE;

enum OUTPUTFORMAT
{
	STEREO44100=0,
	MONO44100,
	STEREO22050,
	MONO22050,
	STEREO11025,
	MONO11025,
	NUMOUTPUTFORMATS
};


struct CDROMPARAMS
{
	char 		lpszCDROMID[255];		// CD-ROM ID, must be unique to index settings in INI file
	LONG		nNumReadSectors;		// Number of sector to read per burst
	LONG		nNumOverlapSectors;		// Number of overlap sectors for jitter correction
	LONG		nNumCompareSectors;		// Number of sector to compare for jitter correction
	LONG		nOffsetStart;			// Fudge factor at start of ripping in sectors
	LONG		nOffsetEnd;				// Fudge factor at the end of ripping in sectors
	LONG		nSpeed;					// CD-ROM speed factor 0 .. 32 x
	LONG		nSpinUpTime;			// CD-ROM spin up time in seconds
	BOOL		bJitterCorrection;		// Boolean indicates whether to use Jitter Correction
	BOOL		bSwapLefRightChannel;	// Swap left and right channel ? 
	DRIVETABLE	DriveTable;				// Drive specific parameters
		
	BYTE		btTargetID;				// SCSI target ID
	BYTE		btAdapterID;			// SCSI Adapter ID
	BYTE		btLunID;				// SCSI LUN ID

	BOOL		bAspiPosting;			// When set ASPI posting is used, otherwhiese ASPI polling is used
	INT			nAspiRetries;
	INT			nAspiTimeOut;

	BOOL        bEnableMultiRead;       // Enables Multiple Read Verify Feature
	BOOL        bMultiReadFirstOnly;    // Only do the multiple reads on the first block
	INT         nMultiReadCount;        // Number of times to reread and compare

	BOOL		bLockDuringRead;        // Number of times to reread and compare

	INT			nRippingMode;
	INT			nParanoiaMode;


};


// Table of contents structure
struct TOCENTRY
{
	DWORD	dwStartSector;		// Start sector of the track
	BYTE	btFlag;				// Track flags (i.e. data or audio track)
	BYTE	btTrackNumber;		// Track number
};
/*
// Call init before anything else
DLLEXPORT CDEX_ERR CCONV CR_Init( LPCSTR strIniFname );

// Get the DLL version number
DLLEXPORT LONG CCONV CR_GetCDRipVersion();

// Get the number of detected CD-ROM drives
DLLEXPORT LONG CCONV CR_GetNumCDROM();

// Get the active CDROM drive index (0..GetNumCDROM()-1 )
DLLEXPORT LONG CCONV CR_GetActiveCDROM();

// Get the active CDROM drive (0..GetNumCDROM()-1 )
//DLLFUNCTION void CR_SetActiveCDROM(LONG nActiveDrive);
DLLEXPORT void CCONV CR_SetActiveCDROM(LONG nActiveDrive);

// Setlect the DRIVETYPE of the active drive
DLLEXPORT CDEX_ERR CCONV CR_SelectCDROMType( DRIVETYPE cdType );

// Get the Selected CDROM type
DLLEXPORT DRIVETYPE CCONV CR_GetCDROMType();

// Get the CDROM parameters of the active drive
DLLEXPORT CDEX_ERR CCONV CR_GetCDROMParameters( CDROMPARAMS* pParam);

// Set the CDROM parameters of the active drive
DLLEXPORT CDEX_ERR CCONV CR_SetCDROMParameters( CDROMPARAMS* pParam);

// Start ripping section, output is fetched to WriteBufferFunc
// Data is extracted from dwStartSector to dwEndSector
DLLEXPORT CDEX_ERR CCONV CR_OpenRipper(	LONG* plBufferSize,LONG dwStartSector,LONG dwEndSector);


// Close the ripper, has to be called when the ripping process is completed (i.e 100%)
// Or it can be called to abort the current ripping section
DLLEXPORT CDEX_ERR CCONV CR_CloseRipper();

// Indicates how far the ripping process is right now
// Returns 100% when the ripping is completed
DLLEXPORT LONG CCONV	CR_GetPercentCompleted();

// Returns the peak value of the ripped section (0..2^15)
DLLEXPORT LONG CCONV	CR_GetPeakValue();

// Get number of Jitter Errors that have occured during the ripping
// This function must be called before CloseRipper is called !
DLLEXPORT LONG CCONV	CR_GetNumberOfJitterErrors();

// Get the jitter position of the extracted track
DLLEXPORT LONG CCONV	CR_GetJitterPosition();

// Rip a chunk from the CD, pbtStream contains the ripped data, pNumBytes the
// number of bytes that have been ripped and corrected for jitter (if enabled)
DLLEXPORT CDEX_ERR CCONV CR_RipChunk( BYTE* pbtStream,LONG* pNumBytes, BOOL& bAbort );

// Load the CD-ROM settings from the file
DLLEXPORT CDEX_ERR CCONV CR_LoadSettings();

// Save the settings to a INI file
DLLEXPORT CDEX_ERR CCONV CR_SaveSettings();

// Normalize the stream (i.e. multiply by dScaleFactor)
DLLEXPORT void CCONV CR_NormalizeChunk(SHORT* pbsStream,LONG nNumSamples,double dScaleFactor);

// Read the table of contents
DLLEXPORT CDEX_ERR CCONV CR_ReadToc();

// Read CD Text entry
DLLEXPORT CDEX_ERR CCONV CR_ReadCDText(BYTE* pbtBuffer,int nBufferSize,LPINT pnCDTextSize);

// Get the number of TOC entries, including the lead out
DLLEXPORT LONG CCONV CR_GetNumTocEntries();

// Get the TOC entry
DLLEXPORT TOCENTRY CCONV CR_GetTocEntry(LONG nTocEntry);

// Checks if the unit is ready (i.e. is the CD media present)
DLLEXPORT BOOL CCONV CR_IsUnitReady();

// Eject the CD, bEject=TRUE=> the CD will be ejected, bEject=FALSE=> the CD will be loaded
DLLEXPORT BOOL CCONV CR_EjectCD(BOOL bEject);

// Check if the CD is playing
DLLEXPORT BOOL CCONV CR_IsAudioPlaying();

// Play track
DLLEXPORT CDEX_ERR CCONV CR_PlayTrack(int nTrack);

// Stop Play track
DLLEXPORT CDEX_ERR CCONV CR_StopPlayTrack();

// Pause Play track
DLLEXPORT CDEX_ERR CCONV CR_PauseCD(BOOL bPause);

// Get debug information
DLLEXPORT SENSEKEY CCONV CR_GetSenseKey();

// Lock/unlock the CD Tray
DLLEXPORT void CCONV CR_LockCD( BOOL bLock );

DLLEXPORT void CCONV  CR_GetSubChannelTrackInfo(int&	nReadIndex,
												int&	nReadTrack,
												DWORD&	dwReadPos );



// Get status of audio playing
DLLEXPORT CDEX_ERR CCONV CR_GetPlayPosition(DWORD& dwRelPos,DWORD& dwAbsPos);

// Set the audio play position
DLLEXPORT CDEX_ERR CCONV CR_SetPlayPosition(DWORD dwAbsPos);

DLLEXPORT CDEX_ERR CCONV CR_PlaySection(LONG lStartSector,LONG lEndSector);

DLLEXPORT void CCONV CR_GetLastJitterErrorPosition(DWORD& dwStartSector,DWORD& dwEndSector);


// Change transport layer, DLL has to be re-initialzed when changing the transport layer!
// 0 = ASPI drivers
// 1 = Native NT scsi drivers

DLLEXPORT VOID CCONV CR_SetTransportLayer( int nTransportLayer );
DLLEXPORT INT CCONV CR_GetTransportLayer(  );
*/
DLLEXPORT void CCONV CR_ScanForC2Errors(	
	LONG	lStartSector,
	LONG	lEndSector,
	INT&	nErrors,
	INT*	pnErrorSectors,
	INT		nMaxErrors,
	BOOL&	bAbort	);

typedef CDEX_ERR	(CCONV *CR_INIT)		(LPCSTR);
typedef CDEX_ERR	(CCONV *CR_READTOC)		();
typedef LONG		(CCONV *CR_GETNUMTOCENTRIES)	();
typedef TOCENTRY	(CCONV *CR_GETTOCENTRY)		(LONG);
typedef CDEX_ERR	(CCONV *CR_OPENRIPPER)		(LONG *, LONG, LONG);
typedef CDEX_ERR	(CCONV *CR_CLOSERIPPER)		();
typedef CDEX_ERR	(CCONV *CR_RIPCHUNK)		(BYTE *, LONG *, BOOL &);
typedef LONG		(CCONV *CR_GETNUMCDROM)		();
typedef LONG		(CCONV *CR_GETACTIVECDROM)	();
typedef void		(CCONV *CR_SETACTIVECDROM)	(LONG);
typedef CDEX_ERR	(CCONV *CR_GETCDROMPARAMETERS)	(CDROMPARAMS *);
typedef CDEX_ERR	(CCONV *CR_SETCDROMPARAMETERS)	(CDROMPARAMS *);
typedef VOID		(CCONV *CR_SETTRANSPORTLAYER)	(int);
typedef void		(CCONV *CR_LOCKCD)		(BOOL);

#pragma pack(pop)

#endif