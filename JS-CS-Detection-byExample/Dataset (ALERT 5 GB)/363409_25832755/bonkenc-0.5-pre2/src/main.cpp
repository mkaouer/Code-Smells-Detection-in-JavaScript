 /* BonkEnc version 0.5
  * Copyright (C) 2001-2002 Robert Kausch <robert.kausch@gmx.net>
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the "GNU General Public License".
  *
  * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. */

#define __THROW_BAD_ALLOC exit(1)
#define MAKEUNICODESTR(x) L##x

#include <smooth.h>
#include <main.h>
#include <resources.h>
#include <stdlib.h>
#include <string>
#include <vector>
#include <parseini.h>
#include <time.h>

#include <genconfig.h>
#include <bonkconfig.h>
#include <bladeconfig.h>
#include <lameconfig.h>
#include <vorbisconfig.h>
#include <faacconfig.h>

#include <bonk/bonk.h>
#include <bladedll/bladedll.h>
#include <lame/lame.h>
#include <vorbis/vorbisenc.h>
#include <faac/faac.h>

#include <cdrip/cdrip.h>
#include <input/filter-in-cdrip.h>
#include <input/filter-in-wav.h>
#include <input/filter-in-voc.h>
#include <input/filter-in-aiff.h>
#include <input/filter-in-au.h>

SMOOTHInt	 ENCODER_BONKENC;
SMOOTHInt	 ENCODER_BLADEENC;
SMOOTHInt	 ENCODER_LAMEENC;
SMOOTHInt	 ENCODER_VORBISENC;
SMOOTHInt	 ENCODER_FAAC;

CR_INIT				 CR_Init;
CR_READTOC			 CR_ReadToc;
CR_GETNUMTOCENTRIES		 CR_GetNumTocEntries;
CR_GETTOCENTRY			 CR_GetTocEntry;
CR_OPENRIPPER			 CR_OpenRipper;
CR_CLOSERIPPER			 CR_CloseRipper;
CR_RIPCHUNK			 CR_RipChunk;
CR_GETNUMCDROM			 CR_GetNumCDROM;
CR_GETACTIVECDROM		 CR_GetActiveCDROM;
CR_SETACTIVECDROM		 CR_SetActiveCDROM;
CR_GETCDROMPARAMETERS		 CR_GetCDROMParameters;
CR_SETCDROMPARAMETERS		 CR_SetCDROMParameters;
CR_SETTRANSPORTLAYER		 CR_SetTransportLayer;
CR_LOCKCD			 CR_LockCD;

BEINITSTREAM			 beInitStream;
BEENCODECHUNK			 beEncodeChunk;
BEDEINITSTREAM			 beDeinitStream;
BECLOSESTREAM			 beCloseStream;
BEVERSION			 beVersion;

FAACENCOPEN			 faacEncOpen;
FAACENCGETCURRENTCONFIGURATION	 faacEncGetCurrentConfiguration;
FAACENCSETCONFIGURATION		 faacEncSetConfiguration;
FAACENCENCODE			 faacEncEncode;
FAACENCCLOSE			 faacEncClose;

SMOOTHVoid SMOOTH::Main()
{
	bonkEnc	*app = new bonkEnc();

	SMOOTH::Loop();

	delete app;
}

bonkEnc::bonkEnc()
{
	encoding = false;
	encoder_thread = NIL;

	SMOOTHPoint	 pos;
	SMOOTHSize	 size;

	currentConfig = new bonkEncConfig;

	currentConfig->encoder = getINIValue("Settings", "Encoder", "0").ToInt();
	currentConfig->enc_outdir = getINIValue("Settings", "EncoderOutdir", "C:\\");

	currentConfig->bonk_quantization = getINIValue("bonkEnc", "Quantization", "8").ToInt();
	currentConfig->bonk_predictor = getINIValue("bonkEnc", "Predictor", "32").ToInt();
	currentConfig->bonk_downsampling = getINIValue("bonkEnc", "Downsampling", "2").ToInt();
	currentConfig->bonk_jstereo = getINIValue("bonkEnc", "JointStereo", "0").ToInt();

	currentConfig->blade_bitrate = getINIValue("bladeEnc", "Bitrate", "192").ToInt();
	currentConfig->blade_crc = getINIValue("bladeEnc", "CRC", "0").ToInt();
	currentConfig->blade_copyright = getINIValue("bladeEnc", "Copyright", "0").ToInt();
	currentConfig->blade_original = getINIValue("bladeEnc", "Original", "1").ToInt();
	currentConfig->blade_private = getINIValue("bladeEnc", "Private", "0").ToInt();
	currentConfig->blade_dualchannel = getINIValue("bladeEnc", "DualChannel", "0").ToInt();

	currentConfig->lame_set_bitrate = getINIValue("lameMP3", "SetBitrate", "1").ToInt();
	currentConfig->lame_bitrate = getINIValue("lameMP3", "Bitrate", "192").ToInt();
	currentConfig->lame_ratio = getINIValue("lameMP3", "Ratio", "1100").ToInt();
	currentConfig->lame_set_quality = getINIValue("lameMP3", "SetQuality", "0").ToInt();
	currentConfig->lame_quality = getINIValue("lameMP3", "Quality", "4").ToInt();
	currentConfig->lame_stereomode = getINIValue("lameMP3", "StereoMode", "0").ToInt();
	currentConfig->lame_forcejs = getINIValue("lameMP3", "ForceJS", "0").ToInt();
	currentConfig->lame_vbrmode = getINIValue("lameMP3", "VBRMode", "0").ToInt();
	currentConfig->lame_vbrquality = getINIValue("lameMP3", "VBRQuality", "4").ToInt();
	currentConfig->lame_abrbitrate = getINIValue("lameMP3", "ABRBitrate", "192").ToInt();
	currentConfig->lame_set_min_vbr_bitrate = getINIValue("lameMP3", "SetMinVBRBitrate", "0").ToInt();
	currentConfig->lame_min_vbr_bitrate = getINIValue("lameMP3", "MinVBRBitrate", "128").ToInt();
	currentConfig->lame_set_max_vbr_bitrate = getINIValue("lameMP3", "SetMaxVBRBitrate", "0").ToInt();
	currentConfig->lame_max_vbr_bitrate = getINIValue("lameMP3", "MaxVBRBitrate", "256").ToInt();
	currentConfig->lame_crc = getINIValue("lameMP3", "CRC", "0").ToInt();
	currentConfig->lame_copyright = getINIValue("lameMP3", "Copyright", "0").ToInt();
	currentConfig->lame_original = getINIValue("lameMP3", "Original", "1").ToInt();
	currentConfig->lame_private = getINIValue("lameMP3", "Private", "0").ToInt();
	currentConfig->lame_strict_iso = getINIValue("lameMP3", "StrictISO", "0").ToInt();
	currentConfig->lame_padding_type = getINIValue("lameMP3", "PaddingType", "2").ToInt();
	currentConfig->lame_resample = getINIValue("lameMP3", "Resample", "0").ToInt();
	currentConfig->lame_disable_filtering = getINIValue("lameMP3", "DisableFiltering", "0").ToInt();
	currentConfig->lame_set_lowpass = getINIValue("lameMP3", "SetLowpass", "0").ToInt();
	currentConfig->lame_lowpass = getINIValue("lameMP3", "Lowpass", "0").ToInt();
	currentConfig->lame_set_lowpass_width = getINIValue("lameMP3", "SetLowpassWidth", "0").ToInt();
	currentConfig->lame_lowpass_width = getINIValue("lameMP3", "LowpassWidth", "0").ToInt();
	currentConfig->lame_set_highpass = getINIValue("lameMP3", "SetHighpass", "0").ToInt();
	currentConfig->lame_highpass = getINIValue("lameMP3", "Highpass", "0").ToInt();
	currentConfig->lame_set_highpass_width = getINIValue("lameMP3", "SetHighpassWidth", "0").ToInt();
	currentConfig->lame_highpass_width = getINIValue("lameMP3", "HighpassWidth", "0").ToInt();

	currentConfig->vorbis_mode = getINIValue("oggVorbis", "Mode", "0").ToInt();
	currentConfig->vorbis_quality = getINIValue("oggVorbis", "Quality", "60").ToInt();
	currentConfig->vorbis_bitrate = getINIValue("oggVorbis", "Bitrate", "192").ToInt();

	currentConfig->faac_mpegversion = getINIValue("FAAC", "MPEGVersion", "0").ToInt();
	currentConfig->faac_type = getINIValue("FAAC", "AACType", "0").ToInt();
	currentConfig->faac_bitrate = getINIValue("FAAC", "Bitrate", "96").ToInt();
	currentConfig->faac_bandwidth = getINIValue("FAAC", "BandWidth", "22000").ToInt();
	currentConfig->faac_allowjs = getINIValue("FAAC", "AllowJS", "1").ToInt();
	currentConfig->faac_usetns = getINIValue("FAAC", "UseTNS", "0").ToInt();

	currentConfig->cdrip_debuglevel = getINIValue("CDRip", "DebugCDRip", "0").ToInt();
	currentConfig->cdrip_paranoia = getINIValue("CDRip", "CDParanoia", "0").ToInt();
	currentConfig->cdrip_paranoia_mode = getINIValue("CDRip", "CDParanoiaMode", "3").ToInt();
	currentConfig->cdrip_jitter = getINIValue("CDRip", "Jitter", "0").ToInt();
	currentConfig->cdrip_activedrive = getINIValue("CDRip", "ActiveCDROM", "0").ToInt();
	currentConfig->cdrip_swapchannels = getINIValue("CDRip", "SwapChannels", "0").ToInt();
	currentConfig->cdrip_locktray = getINIValue("CDRip", "LockTray", "1").ToInt();
	currentConfig->cdrip_ntscsi = getINIValue("CDRip", "UseNTSCSI", "0").ToInt();

	if (LoadBladeDLL() == false)	currentConfig->enable_blade = false;
	else				currentConfig->enable_blade = true;

	if (LoadFAACDLL() == false)	currentConfig->enable_faac = false;
	else				currentConfig->enable_faac = true;

	if (LoadCDRipDLL() == false)	currentConfig->enable_cdrip = false;
	else				currentConfig->enable_cdrip = true;

	int	 nextEC = 0;

#ifndef _MSC_VER
	ENCODER_BONKENC = nextEC++;
#endif

	if (currentConfig->enable_blade) ENCODER_BLADEENC = nextEC++;

#ifndef _MSC_VER
	ENCODER_LAMEENC = nextEC++;
	ENCODER_VORBISENC = nextEC++;
#endif

	if (currentConfig->enable_faac) ENCODER_FAAC = nextEC++;

	if (currentConfig->encoder >= nextEC) currentConfig->encoder = ENCODER_BONKENC;

	if (currentConfig->enable_cdrip)
	{
		currentConfig->cdrip_numdrives = CR_GetNumCDROM();

		if (currentConfig->cdrip_numdrives <= currentConfig->cdrip_activedrive) currentConfig->cdrip_activedrive = 0;
	}

	int	 len = currentConfig->enc_outdir.Length() - 1;
	if (currentConfig->enc_outdir[len] != '\\') currentConfig->enc_outdir[++len] = '\\';

	mainWnd_menubar		= new SMOOTHMenubar();
	mainWnd_iconbar		= new SMOOTHMenubar();
	mainWnd			= new SMOOTHWindow("BonkEnc v0.5");
	mainWnd_titlebar	= new SMOOTHTitlebar(true, false, true);
	mainWnd_statusbar	= new SMOOTHStatusbar("BonkEnc v0.5 - Copyright (C) 2001-2002 Robert Kausch");
	mainWnd_layer		= new SMOOTHLayer();
	menu_file		= new SMOOTHPopupMenu();
	menu_options		= new SMOOTHPopupMenu();
	menu_addsubmenu		= new SMOOTHPopupMenu();
	menu_encode		= new SMOOTHPopupMenu();

	pos.x = 291;
	pos.y = -22;

	hyperlink		= new SMOOTHHyperlink("bonkenc.sourceforge.net", NULL, "http://bonkenc.sourceforge.net", pos);

	pos.x = 7;
	pos.y = 5;

	txt_joblist		= new SMOOTHText("0 file(s) in joblist:", pos);

	pos.x = 7;
	pos.y += 19;
	size.cx = 400;
	size.cy = 150;

	joblist			= new SMOOTHListBox(pos, size, NULLPROC);

	pos.y += 161;

	enc_filename		= new SMOOTHText("Encoding file:", pos);

	pos.y += 24;
	pos.x += 21;

	enc_time		= new SMOOTHText("Time left:", pos);

	pos.x += 91;

	enc_percent		= new SMOOTHText("Percent done:", pos);

	pos.x += 114;

	enc_encoder		= new SMOOTHText("Selected encoder:", pos);

	pos.x = 9;
	pos.y += 24;

	enc_progress		= new SMOOTHText("File progress:", pos);

	pos.y += 24;
	pos.x += 10;

	enc_outdir		= new SMOOTHText("Output dir.:", pos);

	pos.y = 182;
	pos.x = 78;
	size.cx = 329;
	size.cy = 0;

	edb_filename		= new SMOOTHEditBox("none", pos, size, EDB_ALPHANUMERIC, 1024, NULLPROC);
	edb_filename->Deactivate();

	pos.y += 24;
	size.cx = 34;

	edb_time		= new SMOOTHEditBox("00:00", pos, size, EDB_ALPHANUMERIC, 5, NULLPROC);
	edb_time->Deactivate();

	pos.x += 115;
	size.cx = 33;

	edb_percent		= new SMOOTHEditBox("0%", pos, size, EDB_ALPHANUMERIC, 4, NULLPROC);
	edb_percent->Deactivate();

	pos.x += 134;
	size.cx = 80;

	if (currentConfig->encoder == ENCODER_BONKENC)		edb_encoder = new SMOOTHEditBox("BonkEnc", pos, size, EDB_ALPHANUMERIC, 4, NULLPROC);
	else if (currentConfig->encoder == ENCODER_BLADEENC)	edb_encoder = new SMOOTHEditBox("BladeEnc", pos, size, EDB_ALPHANUMERIC, 4, NULLPROC);
	else if (currentConfig->encoder == ENCODER_LAMEENC)	edb_encoder = new SMOOTHEditBox("LAME", pos, size, EDB_ALPHANUMERIC, 4, NULLPROC);
	else if (currentConfig->encoder == ENCODER_VORBISENC)	edb_encoder = new SMOOTHEditBox("OggVorbis", pos, size, EDB_ALPHANUMERIC, 4, NULLPROC);
	else if (currentConfig->encoder == ENCODER_FAAC)	edb_encoder = new SMOOTHEditBox("FAAC", pos, size, EDB_ALPHANUMERIC, 4, NULLPROC);

	edb_encoder->Deactivate();

	pos.x = 78;
	pos.y += 48;
	size.cx = 329;

	edb_outdir		= new SMOOTHEditBox(currentConfig->enc_outdir, pos, size, EDB_ALPHANUMERIC, 1024, NULLPROC);
	edb_outdir->Deactivate();

	pos.x = 78;
	pos.y = 230;
	size.cx = 328;
	size.cy = 18;

	progress		= new SMOOTHProgressbar(pos, size, OR_HORZ, PB_NOTEXT, 0, 1000, 0);
	progress->Deactivate();

	menu_file->AddEntry("Add", NIL, NULLPROC, menu_addsubmenu);
	menu_file->AddEntry("Remove", NIL, SMOOTHProc(bonkEnc, this, RemoveFile));
	menu_file->AddEntry();
	menu_file->AddEntry("Clear joblist", NIL, SMOOTHProc(bonkEnc, this, ClearList));
	menu_file->AddEntry();
	menu_file->AddEntry("Exit", NIL, SMOOTHProc(bonkEnc, this, Exit));

	menu_options->AddEntry("General settings...", NIL, SMOOTHProc(bonkEnc, this, ConfigureGeneral));
	menu_options->AddEntry("Configure selected encoder...", NIL, SMOOTHProc(bonkEnc, this, ConfigureEncoder));

	menu_addsubmenu->AddEntry("Audio file...", NIL, SMOOTHProc(bonkEnc, this, AddFile));

	if (currentConfig->enable_cdrip)
	{
		menu_addsubmenu->AddEntry("Audio CD contents", NIL, SMOOTHProc(bonkEnc, this, ReadCD));
	}

	menu_encode->AddEntry("Start encoding", NIL, SMOOTHProc(bonkEnc, this, Encode));
	menu_encode->AddEntry("Stop encoding", NIL, SMOOTHProc(bonkEnc, this, StopEncoding));

	mainWnd_menubar->AddEntry("File", NIL, NULLPROC, menu_file);
	mainWnd_menubar->AddEntry("Options", NIL, NULLPROC, menu_options);
	mainWnd_menubar->AddEntry("Encode", NIL, NULLPROC, menu_encode);
	mainWnd_menubar->AddEntry()->SetOrientation(OR_RIGHT);
	mainWnd_menubar->AddEntry(NIL, SMOOTH::LoadImage("bonkenc.pci", 6, NIL), SMOOTHProc(bonkEnc, this, About), NIL, NIL, NIL, 0, OR_RIGHT)->SetStatusText("Display information about BonkEnc");

	mainWnd_iconbar->AddEntry(NIL, SMOOTH::LoadImage("bonkenc.pci", 1, NIL), SMOOTHProc(bonkEnc, this, AddFile))->SetStatusText("Add audio file(s) to the joblist");

	if (currentConfig->enable_cdrip && currentConfig->cdrip_numdrives >= 1)
	{
		mainWnd_iconbar->AddEntry(NIL, SMOOTH::LoadImage("bonkenc.pci", 9, NIL), SMOOTHProc(bonkEnc, this, ReadCD))->SetStatusText("Add audio CD contents to the joblist");
	}

	mainWnd_iconbar->AddEntry(NIL, SMOOTH::LoadImage("bonkenc.pci", 2, NIL), SMOOTHProc(bonkEnc, this, RemoveFile))->SetStatusText("Remove the selected entry from the joblist");
	mainWnd_iconbar->AddEntry(NIL, SMOOTH::LoadImage("bonkenc.pci", 3, NIL), SMOOTHProc(bonkEnc, this, ClearList))->SetStatusText("Clear the entire joblist");
	mainWnd_iconbar->AddEntry();
	mainWnd_iconbar->AddEntry(NIL, SMOOTH::LoadImage("bonkenc.pci", 4, NIL), SMOOTHProc(bonkEnc, this, ConfigureGeneral))->SetStatusText("Configure general settings");
	mainWnd_iconbar->AddEntry(NIL, SMOOTH::LoadImage("bonkenc.pci", 5, NIL), SMOOTHProc(bonkEnc, this, ConfigureEncoder))->SetStatusText("Configure the selected audio encoder");
	mainWnd_iconbar->AddEntry();
	mainWnd_iconbar->AddEntry(NIL, SMOOTH::LoadImage("bonkenc.pci", 7, NIL), SMOOTHProc(bonkEnc, this, Encode))->SetStatusText("Start the encoding process");
	mainWnd_iconbar->AddEntry(NIL, SMOOTH::LoadImage("bonkenc.pci", 8, NIL), SMOOTHProc(bonkEnc, this, StopEncoding))->SetStatusText("Stop encoding");

	RegisterObject(mainWnd);

	mainWnd_layer->RegisterObject(joblist);
	mainWnd_layer->RegisterObject(txt_joblist);
	mainWnd_layer->RegisterObject(enc_filename);
	mainWnd_layer->RegisterObject(enc_time);
	mainWnd_layer->RegisterObject(enc_percent);
	mainWnd_layer->RegisterObject(enc_encoder);
	mainWnd_layer->RegisterObject(enc_progress);
	mainWnd_layer->RegisterObject(enc_outdir);
	mainWnd_layer->RegisterObject(edb_filename);
	mainWnd_layer->RegisterObject(edb_time);
	mainWnd_layer->RegisterObject(edb_percent);
	mainWnd_layer->RegisterObject(edb_encoder);
	mainWnd_layer->RegisterObject(edb_outdir);
	mainWnd_layer->RegisterObject(progress);
	mainWnd_layer->RegisterObject(hyperlink);

	mainWnd->RegisterObject(mainWnd_titlebar);
	mainWnd->RegisterObject(mainWnd_statusbar);
	mainWnd->RegisterObject(mainWnd_layer);
	mainWnd->RegisterObject(mainWnd_menubar);
	mainWnd->RegisterObject(mainWnd_iconbar);

	mainWnd->SetIcon(SMOOTH::LoadImage("bonkenc.pci", 0, NIL));
	mainWnd->SetApplicationIcon(IDI_ICON);
	mainWnd->SetMetrics(SMOOTHPoint(100, 100), SMOOTHSize(420, 371));
	mainWnd->SetKillProc(SMOOTHKillProc(bonkEnc, this, KillProc));
}

bonkEnc::~bonkEnc()
{
	if (currentConfig->enable_blade) FreeBladeDLL();
	if (currentConfig->enable_faac) FreeFAACDLL();
	if (currentConfig->enable_cdrip) FreeCDRipDLL();

	mainWnd->UnregisterObject(mainWnd_menubar);
	mainWnd->UnregisterObject(mainWnd_iconbar);
	mainWnd->UnregisterObject(mainWnd_titlebar);
	mainWnd->UnregisterObject(mainWnd_statusbar);
	mainWnd->UnregisterObject(mainWnd_layer);

	mainWnd_layer->UnregisterObject(joblist);
	mainWnd_layer->UnregisterObject(txt_joblist);
	mainWnd_layer->UnregisterObject(enc_filename);
	mainWnd_layer->UnregisterObject(enc_time);
	mainWnd_layer->UnregisterObject(enc_percent);
	mainWnd_layer->UnregisterObject(enc_encoder);
	mainWnd_layer->UnregisterObject(enc_progress);
	mainWnd_layer->UnregisterObject(enc_outdir);
	mainWnd_layer->UnregisterObject(edb_filename);
	mainWnd_layer->UnregisterObject(edb_time);
	mainWnd_layer->UnregisterObject(edb_percent);
	mainWnd_layer->UnregisterObject(edb_encoder);
	mainWnd_layer->UnregisterObject(edb_outdir);
	mainWnd_layer->UnregisterObject(progress);
	mainWnd_layer->UnregisterObject(hyperlink);

	UnregisterObject(mainWnd);

	delete currentConfig;

	delete mainWnd_menubar;
	delete mainWnd_iconbar;
	delete mainWnd_titlebar;
	delete mainWnd_statusbar;
	delete mainWnd_layer;
	delete mainWnd;
	delete joblist;
	delete txt_joblist;
	delete enc_filename;
	delete enc_time;
	delete enc_percent;
	delete enc_encoder;
	delete enc_progress;
	delete enc_outdir;
	delete edb_filename;
	delete edb_time;
	delete edb_percent;
	delete edb_encoder;
	delete edb_outdir;
	delete progress;
	delete menu_file;
	delete menu_options;
	delete menu_addsubmenu;
	delete menu_encode;
	delete hyperlink;
}

SMOOTHVoid bonkEnc::AddFile()
{
	if (encoding)
	{
		SMOOTH::MessageBox("Cannot modify the joblist while encoding!", "Error", MB_OK, IDI_HAND);

		return;
	}

	if (SMOOTH::Setup::enableUnicode)	AddFileW();
	else					AddFileA();

	txt_joblist->SetText(SMOOTHString::IntToString(joblist->GetNOfEntries()).Append(" file(s) in joblist:"));
}

SMOOTHVoid bonkEnc::AddFileW()
{
	static OPENFILENAMEW	 ofn;
	wchar_t			*buffer = new wchar_t [32768];
	wchar_t			*buffer2 = new wchar_t [1024];
	SMOOTHInt		 pos = 0;
	SMOOTHString		 dir;
	SMOOTHString		 file;

	for (SMOOTHInt i = 0; i < 32768; i++) buffer[i] = 0;

	ofn.lStructSize		= sizeof(OPENFILENAMEW);
	ofn.hwndOwner		= mainWnd->hwnd;

	if (currentConfig->enable_cdrip)	ofn.lpstrFilter = MAKEUNICODESTR("Audio Files\0*.aif; *.aiff; *.au; *.voc; *.wav; *.cda\0Apple Audio Files (*.aif; *.aiff)\0*.aif; *.aiff\0Creative Voice Files (*.voc)\0*.voc\0Sun Audio Files (*.au)\0*.au\0Wave Files (*.wav)\0*.wav\0Windows CD Audio Track (*.cda)\0*.cda\0All Files\0*.*\0");
	else					ofn.lpstrFilter = MAKEUNICODESTR("Audio Files\0*.aif; *.aiff; *.au; *.voc; *.wav\0Apple Audio Files (*.aif; *.aiff)\0*.aif; *.aiff\0Creative Voice Files (*.voc)\0*.voc\0Sun Audio Files (*.au)\0*.au\0Wave Files (*.wav)\0*.wav\0All Files\0*.*\0");

	ofn.nFilterIndex	= 1;
	ofn.lpstrFile		= buffer;
	ofn.nMaxFile		= 32786;
	ofn.lpstrFileTitle	= NIL;
	ofn.lpstrInitialDir	= NIL;
	ofn.lpstrTitle		= NIL;
	ofn.Flags		= OFN_ALLOWMULTISELECT | OFN_FILEMUSTEXIST | OFN_PATHMUSTEXIST | OFN_HIDEREADONLY | OFN_EXPLORER;

	if (GetOpenFileNameW(&ofn))
	{
		SMOOTHInt	 i;

		for (i = 0; i < 32768; i++)
		{
			buffer2[pos++] = buffer[i];

			if (buffer[i] == 0)
			{
				dir.Copy(buffer2);

				break;
			}
		}

		i++;
		pos = 0;

		for (; i < 32768; i++)
		{
			buffer2[pos++] = buffer[i];

			if (buffer[i] == 0)
			{
				file = file.Copy(dir).Append("\\").Append(buffer2);

				if (file[file.Length() - 1] == '\\') file[file.Length() - 1] = 0;

				if (file[file.Length() - 3] == 'c' &&
				    file[file.Length() - 2] == 'd' &&
				    file[file.Length() - 1] == 'a' &&
				    currentConfig->enable_cdrip)
				{
					HFILE		 hfile = _lopen(file, OF_READ);
					InStream	*in = new InStream(STREAM_WINDOWS, hfile);
					SMOOTHInt	 trackNumber;
					SMOOTHInt	 trackLength;

					in->Seek(22);

					trackNumber = in->InputNumber(2);

					in->Seek(32);

					trackLength = in->InputNumber(4);

					delete in;

					_lclose(hfile);

					SMOOTHInt	 audiodrive = 0;
					SMOOTHBool	 done = false;

					CR_SetTransportLayer(currentConfig->cdrip_ntscsi);

					CR_Init(file);

					for (audiodrive = 0; audiodrive < currentConfig->cdrip_numdrives; audiodrive++)
					{
						CR_SetActiveCDROM(audiodrive);

						CR_ReadToc();

						SMOOTHInt	 numTocEntries = CR_GetNumTocEntries();

						for (int i = 0; i < numTocEntries; i++)
						{
							TOCENTRY	 entry = CR_GetTocEntry(i);
							TOCENTRY	 nextentry = CR_GetTocEntry(i + 1);
							SMOOTHInt	 length = nextentry.dwStartSector - entry.dwStartSector;

							if (!(entry.btFlag & CDROMDATAFLAG) && entry.btTrackNumber == trackNumber && length == trackLength)
							{
								done = true;
								break;
							}
						}

						if (done) break;
					}

					sa_joblist.AddEntry(SMOOTHString("Audio CD ").Append(SMOOTHString::IntToString(audiodrive)).Append(" track ").Append(SMOOTHString::IntToString(trackNumber)), joblist->AddEntry(SMOOTHString("Audio CD ").Append(SMOOTHString::IntToString(audiodrive)).Append(" track ").Append(SMOOTHString::IntToString(trackNumber)), NULLPROC));
				}
				else
				{
					sa_joblist.AddEntry(file, joblist->AddEntry(file, NULLPROC));
				}

				pos = 0;

				if (buffer[i + 1] == 0) break;
			}
		}
	}

	delete [] buffer;
	delete [] buffer2;
}

SMOOTHVoid bonkEnc::AddFileA()
{
	static OPENFILENAMEA	 ofn;
	char			*buffer = new char [32768];
	char			*buffer2 = new char [1024];
	SMOOTHInt		 pos = 0;
	SMOOTHString		 dir;
	SMOOTHString		 file;

	for (SMOOTHInt i = 0; i < 32768; i++) buffer[i] = 0;

	ofn.lStructSize		= sizeof(OPENFILENAMEA);
	ofn.hwndOwner		= mainWnd->hwnd;

	if (currentConfig->enable_cdrip)	ofn.lpstrFilter = "Audio Files\0*.aif; *.aiff; *.au; *.voc; *.wav; *.cda\0Apple Audio Files (*.aif; *.aiff)\0*.aif; *.aiff\0Creative Voice Files (*.voc)\0*.voc\0Sun Audio Files (*.au)\0*.au\0Wave Files (*.wav)\0*.wav\0Windows CD Audio Track (*.cda)\0*.cda\0All Files\0*.*\0";
	else					ofn.lpstrFilter = "Audio Files\0*.aif; *.aiff; *.au; *.voc; *.wav\0Apple Audio Files (*.aif; *.aiff)\0*.aif; *.aiff\0Creative Voice Files (*.voc)\0*.voc\0Sun Audio Files (*.au)\0*.au\0Wave Files (*.wav)\0*.wav\0All Files\0*.*\0";

	ofn.nFilterIndex	= 1;
	ofn.lpstrFile		= buffer;
	ofn.nMaxFile		= 32786;
	ofn.lpstrFileTitle	= NIL;
	ofn.lpstrInitialDir	= NIL;
	ofn.lpstrTitle		= NIL;
	ofn.Flags		= OFN_ALLOWMULTISELECT | OFN_FILEMUSTEXIST | OFN_PATHMUSTEXIST | OFN_HIDEREADONLY | OFN_EXPLORER;

	if (GetOpenFileNameA(&ofn))
	{
		SMOOTHInt	 i;

		for (i = 0; i < 32768; i++)
		{
			buffer2[pos++] = buffer[i];

			if (buffer[i] == 0)
			{
				dir.Copy(buffer2);

				break;
			}
		}

		i++;
		pos = 0;

		for (; i < 32768; i++)
		{
			buffer2[pos++] = buffer[i];

			if (buffer[i] == 0)
			{
				file = file.Copy(dir).Append("\\").Append(buffer2);

				if (file[file.Length() - 1] == '\\') file[file.Length() - 1] = 0;

				if (file[file.Length() - 3] == 'c' &&
				    file[file.Length() - 2] == 'd' &&
				    file[file.Length() - 1] == 'a' &&
				    currentConfig->enable_cdrip)
				{
					HFILE		 hfile = _lopen(file, OF_READ);
					InStream	*in = new InStream(STREAM_WINDOWS, hfile);
					SMOOTHInt	 trackNumber;
					SMOOTHInt	 trackLength;

					in->Seek(22);

					trackNumber = in->InputNumber(2);

					in->Seek(32);

					trackLength = in->InputNumber(4);

					delete in;

					_lclose(hfile);

					SMOOTHInt	 audiodrive = 0;
					SMOOTHBool	 done = false;

					CR_SetTransportLayer(currentConfig->cdrip_ntscsi);

					CR_Init(file);

					for (audiodrive = 0; audiodrive < currentConfig->cdrip_numdrives; audiodrive++)
					{
						CR_SetActiveCDROM(audiodrive);

						CR_ReadToc();

						SMOOTHInt	 numTocEntries = CR_GetNumTocEntries();

						for (int i = 0; i < numTocEntries; i++)
						{
							TOCENTRY	 entry = CR_GetTocEntry(i);
							TOCENTRY	 nextentry = CR_GetTocEntry(i + 1);
							SMOOTHInt	 length = nextentry.dwStartSector - entry.dwStartSector;

							if (!(entry.btFlag & CDROMDATAFLAG) && entry.btTrackNumber == trackNumber && length == trackLength)
							{
								done = true;
								break;
							}
						}

						if (done) break;
					}

					sa_joblist.AddEntry(SMOOTHString("Audio CD ").Append(SMOOTHString::IntToString(audiodrive)).Append(" track ").Append(SMOOTHString::IntToString(trackNumber)), joblist->AddEntry(SMOOTHString("Audio CD ").Append(SMOOTHString::IntToString(audiodrive)).Append(" track ").Append(SMOOTHString::IntToString(trackNumber)), NULLPROC));
				}
				else
				{
					sa_joblist.AddEntry(file, joblist->AddEntry(file, NULLPROC));
				}

				pos = 0;

				if (buffer[i + 1] == 0) break;
			}
		}
	}

	delete [] buffer;
	delete [] buffer2;
}

SMOOTHVoid bonkEnc::RemoveFile()
{
	if (encoding)
	{
		SMOOTH::MessageBox("Cannot modify the joblist while encoding!", "Error", MB_OK, IDI_HAND);

		return;
	}

	if (joblist->GetSelectedEntry() != -1)
	{
		sa_joblist.DeleteEntry(joblist->GetSelectedEntry());
		joblist->RemoveEntry(joblist->GetSelectedEntry());

		txt_joblist->SetText(SMOOTHString::IntToString(joblist->GetNOfEntries()).Append(" file(s) in joblist:"));
	}
	else
	{
		SMOOTH::MessageBox("You have not selected a file!", "Error!", MB_OK, IDI_HAND);
	}
}

SMOOTHVoid bonkEnc::ClearList()
{
	if (encoding)
	{
		SMOOTH::MessageBox("Cannot modify the joblist while encoding!", "Error", MB_OK, IDI_HAND);

		return;
	}

	sa_joblist.DeleteAll();
	joblist->Cleanup();

	txt_joblist->SetText("0 file(s) in joblist:");
}

SMOOTHBool bonkEnc::KillProc()
{
	if (encoding)
	{
		if (IDNO == SMOOTH::MessageBox("The encoding thread is still running!\nDo you really want to quit?", "Currently encoding", MB_YESNO, IDI_QUESTION)) return false;

		StopEncoding();
	}

	SMOOTHString	 file = SMOOTH::StartDirectory;

	file.Append("bonkenc.ini");

	SMOOTHOutStream	*out = new SMOOTHOutStream(STREAM_FILE, file, OS_OVERWRITE);
	SMOOTHString	 str;

	if (out->GetStreamType() == STREAM_FILE)
	{
		out->OutputLine("[Settings]");

		str = "Encoder=";
		str.Append(SMOOTHString::IntToString(currentConfig->encoder));
		out->OutputLine(str);

		str = "EncoderOutdir=";
		str.Append(currentConfig->enc_outdir);
		out->OutputLine(str);

		out->OutputLine("");
		out->OutputLine("[CDRip]");

		str = "ActiveCDROM=";
		str.Append(SMOOTHString::IntToString(currentConfig->cdrip_activedrive));
		out->OutputLine(str);

		str = "DebugCDRip=";
		str.Append(SMOOTHString::IntToString(currentConfig->cdrip_debuglevel));
		out->OutputLine(str);

		str = "CDParanoia=";
		str.Append(SMOOTHString::IntToString(currentConfig->cdrip_paranoia));
		out->OutputLine(str);

		str = "CDParanoiaMode=";
		str.Append(SMOOTHString::IntToString(currentConfig->cdrip_paranoia_mode));
		out->OutputLine(str);

		str = "Jitter=";
		str.Append(SMOOTHString::IntToString(currentConfig->cdrip_jitter));
		out->OutputLine(str);

		str = "SwapChannels=";
		str.Append(SMOOTHString::IntToString(currentConfig->cdrip_swapchannels));
		out->OutputLine(str);

		str = "LockTray=";
		str.Append(SMOOTHString::IntToString(currentConfig->cdrip_locktray));
		out->OutputLine(str);

		str = "UseNTSCSI=";
		str.Append(SMOOTHString::IntToString(currentConfig->cdrip_ntscsi));
		out->OutputLine(str);

		out->OutputLine("");
		out->OutputLine("[bonkEnc]");

		str = "Quantization=";
		str.Append(SMOOTHString::IntToString(currentConfig->bonk_quantization));
		out->OutputLine(str);

		str = "Predictor=";
		str.Append(SMOOTHString::IntToString(currentConfig->bonk_predictor));
		out->OutputLine(str);

		str = "Downsampling=";
		str.Append(SMOOTHString::IntToString(currentConfig->bonk_downsampling));
		out->OutputLine(str);

		str = "JointStereo=";
		str.Append(SMOOTHString::IntToString(currentConfig->bonk_jstereo));
		out->OutputLine(str);

		out->OutputLine("");
		out->OutputLine("[bladeEnc]");

		str = "Bitrate=";
		str.Append(SMOOTHString::IntToString(currentConfig->blade_bitrate));
		out->OutputLine(str);

		str = "CRC=";
		str.Append(SMOOTHString::IntToString(currentConfig->blade_crc));
		out->OutputLine(str);

		str = "Copyright=";
		str.Append(SMOOTHString::IntToString(currentConfig->blade_copyright));
		out->OutputLine(str);

		str = "Original=";
		str.Append(SMOOTHString::IntToString(currentConfig->blade_original));
		out->OutputLine(str);

		str = "Private=";
		str.Append(SMOOTHString::IntToString(currentConfig->blade_private));
		out->OutputLine(str);

		str = "DualChannel=";
		str.Append(SMOOTHString::IntToString(currentConfig->blade_dualchannel));
		out->OutputLine(str);

		out->OutputLine("");
		out->OutputLine("[lameMP3]");

		str = "SetBitrate=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_set_bitrate));
		out->OutputLine(str);

		str = "Bitrate=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_bitrate));
		out->OutputLine(str);

		str = "Ratio=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_ratio));
		out->OutputLine(str);

		str = "SetQuality=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_set_quality));
		out->OutputLine(str);

		str = "Quality=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_quality));
		out->OutputLine(str);

		str = "StereoMode=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_stereomode));
		out->OutputLine(str);

		str = "ForceJS=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_forcejs));
		out->OutputLine(str);

		str = "VBRMode=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_vbrmode));
		out->OutputLine(str);

		str = "VBRQuality=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_vbrquality));
		out->OutputLine(str);

		str = "ABRBitrate=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_abrbitrate));
		out->OutputLine(str);

		str = "SetMinVBRBitrate=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_set_min_vbr_bitrate));
		out->OutputLine(str);

		str = "MinVBRBitrate=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_min_vbr_bitrate));
		out->OutputLine(str);

		str = "SetMaxVBRBitrate=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_set_max_vbr_bitrate));
		out->OutputLine(str);

		str = "MaxVBRBitrate=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_max_vbr_bitrate));
		out->OutputLine(str);

		str = "CRC=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_crc));
		out->OutputLine(str);

		str = "Copyright=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_copyright));
		out->OutputLine(str);

		str = "Original=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_original));
		out->OutputLine(str);

		str = "Private=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_private));
		out->OutputLine(str);

		str = "StrictISO=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_strict_iso));
		out->OutputLine(str);

		str = "PaddingType=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_padding_type));
		out->OutputLine(str);

		str = "Resample=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_resample));
		out->OutputLine(str);

		str = "DisableFiltering=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_disable_filtering));
		out->OutputLine(str);

		str = "SetLowpass=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_set_lowpass));
		out->OutputLine(str);

		str = "Lowpass=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_lowpass));
		out->OutputLine(str);

		str = "SetLowpassWidth=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_set_lowpass_width));
		out->OutputLine(str);

		str = "LowpassWidth=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_lowpass_width));
		out->OutputLine(str);

		str = "SetHighpass=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_set_highpass));
		out->OutputLine(str);

		str = "Highpass=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_highpass));
		out->OutputLine(str);

		str = "SetHighpassWidth=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_set_highpass_width));
		out->OutputLine(str);

		str = "HighpassWidth=";
		str.Append(SMOOTHString::IntToString(currentConfig->lame_highpass_width));
		out->OutputLine(str);

		out->OutputLine("");
		out->OutputLine("[oggVorbis]");

		str = "Mode=";
		str.Append(SMOOTHString::IntToString(currentConfig->vorbis_mode));
		out->OutputLine(str);

		str = "Quality=";
		str.Append(SMOOTHString::IntToString(currentConfig->vorbis_quality));
		out->OutputLine(str);

		str = "Bitrate=";
		str.Append(SMOOTHString::IntToString(currentConfig->vorbis_bitrate));
		out->OutputLine(str);

		out->OutputLine("");
		out->OutputLine("[FAAC]");

		str = "MPEGVersion=";
		str.Append(SMOOTHString::IntToString(currentConfig->faac_mpegversion));
		out->OutputLine(str);

		str = "AACType=";
		str.Append(SMOOTHString::IntToString(currentConfig->faac_type));
		out->OutputLine(str);

		str = "Bitrate=";
		str.Append(SMOOTHString::IntToString(currentConfig->faac_bitrate));
		out->OutputLine(str);

		str = "BandWidth=";
		str.Append(SMOOTHString::IntToString(currentConfig->faac_bandwidth));
		out->OutputLine(str);

		str = "AllowJS=";
		str.Append(SMOOTHString::IntToString(currentConfig->faac_allowjs));
		out->OutputLine(str);

		str = "UseTNS=";
		str.Append(SMOOTHString::IntToString(currentConfig->faac_usetns));
		out->OutputLine(str);
	}

	delete out;

	return true;
}

SMOOTHVoid bonkEnc::Exit()
{
	SMOOTH::CloseWindow(mainWnd);
}

SMOOTHVoid bonkEnc::About()
{
	SMOOTH::MessageBox("BonkEnc version 0.5\nCopyright (C) 2001-2002 Robert Kausch\n\nThis program is being distributed under the\nterms of the GNU General Public License (GPL).\n\nFor more information on BonkEnc visit the\nwebsite at 'http://bonkenc.sourceforge.net' or\ncontact me at robert.kausch@gmx.net.", "About BonkEnc v0.5", MB_OK, MAKEINTRESOURCE(IDI_ICON));
}

SMOOTHVoid bonkEnc::ConfigureEncoder()
{
	if (encoding)
	{
		SMOOTH::MessageBox("Cannot configure encoder while encoding!", "Error", MB_OK, IDI_HAND);

		return;
	}

	if (currentConfig->encoder == ENCODER_BONKENC)
	{
		configureBonkEnc	*dlg = new configureBonkEnc(currentConfig);

		dlg->ShowDialog();

		delete dlg;
	}
	else if (currentConfig->encoder == ENCODER_BLADEENC)
	{
		configureBladeEnc	*dlg = new configureBladeEnc(currentConfig);

		dlg->ShowDialog();

		delete dlg;
	}
	else if (currentConfig->encoder == ENCODER_LAMEENC)
	{
		configureLameEnc	*dlg = new configureLameEnc(currentConfig);

		dlg->ShowDialog();

		delete dlg;
	}
	else if (currentConfig->encoder == ENCODER_VORBISENC)
	{
		configureVorbisEnc	*dlg = new configureVorbisEnc(currentConfig);

		dlg->ShowDialog();

		delete dlg;
	}
	else if (currentConfig->encoder == ENCODER_FAAC)
	{
		configureFAAC	*dlg = new configureFAAC(currentConfig);

		dlg->ShowDialog();

		delete dlg;
	}
}

SMOOTHVoid bonkEnc::ConfigureGeneral()
{
	if (encoding)
	{
		SMOOTH::MessageBox("Cannot change settings while encoding!", "Error", MB_OK, IDI_HAND);

		return;
	}

	configureGeneralSettings	*dlg = new configureGeneralSettings(currentConfig);

	dlg->ShowDialog();

	delete dlg;

	if (currentConfig->encoder == ENCODER_BONKENC)		edb_encoder->SetText("BonkEnc");
	else if (currentConfig->encoder == ENCODER_BLADEENC)	edb_encoder->SetText("BladeEnc");
	else if (currentConfig->encoder == ENCODER_LAMEENC)	edb_encoder->SetText("LAME");
	else if (currentConfig->encoder == ENCODER_VORBISENC)	edb_encoder->SetText("OggVorbis");
	else if (currentConfig->encoder == ENCODER_FAAC)	edb_encoder->SetText("FAAC");

	edb_outdir->SetText(currentConfig->enc_outdir);
}

SMOOTHVoid bonkEnc::Encode()
{
	if (encoding) return;

	if (encoder_thread != NIL)
	{
		UnregisterObject(encoder_thread);

		delete encoder_thread;

		encoder_thread = NIL;
	}

	encoder_thread = new SMOOTHThread(SMOOTHThreadProc(bonkEnc, this, Encoder));

	encoding = true;

	RegisterObject(encoder_thread);

	encoder_thread->Start();
}

SMOOTHVoid bonkEnc::Encoder(SMOOTHThread *thread)
{
	SMOOTHString	 in_filename;
	SMOOTHString	 out_filename;
	SMOOTHInt	 activedrive = currentConfig->cdrip_activedrive;

	for (int i = 0; i < sa_joblist.GetNOfEntries(); i++)
	{
		if (i == 0)	in_filename = sa_joblist.GetFirstEntry();
		else		in_filename = sa_joblist.GetNextEntry();

		edb_filename->SetText(in_filename);
		progress->SetValue(0);
		edb_time->SetText("00:00");

		SMOOTHString	 compString;
		SMOOTHInt	 trackNumber = -1;
		SMOOTHInt	 audiodrive = -1;
		SMOOTHBool	 cdTrack = SMOOTH::False;

		out_filename.Copy(edb_outdir->GetText());

		if (compString.CopyN(in_filename, 9).Compare("Audio CD ") == 0)
		{
			audiodrive = in_filename[9] - 48;
			trackNumber = in_filename[17] - 48;

			out_filename.Append("cd");
			out_filename.Append(SMOOTHString::IntToString(audiodrive));
			out_filename.Append("track");

			if (in_filename.Length() == 18)
			{
				out_filename.Append("0").Append(SMOOTHString::IntToString(trackNumber));
			}
			else
			{
				trackNumber = 10 * trackNumber + (in_filename[18] - 48);

				out_filename.Append(SMOOTHString::IntToString(trackNumber));
			}

			cdTrack = SMOOTH::True;
		}
		else
		{
			int	 in_len = in_filename.Length();
			int	 out_len = out_filename.Length();
			int	 lastbs = 0;
			int	 firstdot = 0;

			for (int i = 0; i < in_len; i++)
			{
				if (in_filename[i] == '\\') lastbs = i;
			}

			for (int j = in_len - 1; j >= 0; j--)
			{
				if (in_filename[j] == '.') { firstdot = in_len - j; break; }
				if (in_filename[j] == '\\') break;
			}

			for (int k = out_len; k < (in_len + out_len - lastbs - firstdot - 1); k++)
			{
				out_filename[k] = in_filename[(k - out_len) + lastbs + 1];
			}
		}

		if (currentConfig->encoder == ENCODER_BONKENC)		out_filename.Append(".bonk");
		else if (currentConfig->encoder == ENCODER_BLADEENC)	out_filename.Append(".mp3");
		else if (currentConfig->encoder == ENCODER_LAMEENC)	out_filename.Append(".mp3");
		else if (currentConfig->encoder == ENCODER_VORBISENC)	out_filename.Append(".ogg");
		else if (currentConfig->encoder == ENCODER_FAAC)	out_filename.Append(".aac");

		SMOOTHInStream	*f_in;
		InputFilter	*filter = NIL;
		bonkFormatInfo	 format;

		if (cdTrack)
		{
			currentConfig->cdrip_activedrive = audiodrive;

			f_in = new SMOOTHInStream(STREAM_ZERO);
			filter = new FilterCDRip(currentConfig);

			((FilterCDRip *) filter)->SetTrack(trackNumber);

			f_in->SetFilter(filter);
		}
		else
		{
			f_in = new SMOOTHInStream(STREAM_FILE, in_filename);

			int magic = f_in->InputNumber(4);

			f_in->Seek(0);

			switch (magic)
			{
				case 1297239878:
					filter = new FilterAIFF(currentConfig);
					break;
				case 1684960046:
					filter = new FilterAU(currentConfig);
					break;
				case 1634038339:
					filter = new FilterVOC(currentConfig);
					break;
				case 1179011410:
					filter = new FilterWAV(currentConfig);
					break;
			}

			filter->SetFileSize(f_in->Size());

			f_in->SetFilter(filter);
		}

		format = filter->GetAudioFormat();

		SMOOTHOutStream	*f_out	= new SMOOTHOutStream(STREAM_FILE, out_filename, OS_OVERWRITE);

		if (currentConfig->encoder == ENCODER_BONKENC)		EncodeBONK(f_in, f_out, &format);
		else if (currentConfig->encoder == ENCODER_BLADEENC)	EncodeBLADE(f_in, f_out, &format);
		else if (currentConfig->encoder == ENCODER_LAMEENC)	EncodeLAME(f_in, f_out, &format);
		else if (currentConfig->encoder == ENCODER_VORBISENC)	EncodeVORBIS(f_in, f_out, &format);
		else if (currentConfig->encoder == ENCODER_FAAC)	EncodeFAAC(f_in, f_out, &format);

		if (cdTrack) delete filter;

		delete f_in;

		if (f_out->Size() == 0)
		{
			delete f_out;

			remove(out_filename);
		}
		else
		{
			delete f_out;
		}
	}

	currentConfig->cdrip_activedrive = activedrive;

	encoding = false;

	ClearList();

	edb_filename->SetText("none");
	edb_percent->SetText("0%");
	progress->SetValue(0);
	edb_time->SetText("00:00");
}

SMOOTHVoid bonkEnc::StopEncoding()
{
	if (!encoding) return;

	encoder_thread->Stop();

	UnregisterObject(encoder_thread);

	delete encoder_thread;

	encoder_thread = NIL;
	encoding = false;

	edb_filename->SetText("none");
	edb_percent->SetText("0%");
	progress->SetValue(0);
	edb_time->SetText("00:00");
}

SMOOTHVoid bonkEnc::ReadCD()
{
	if (encoding)
	{
		SMOOTH::MessageBox("Cannot modify the joblist while encoding!", "Error", MB_OK, IDI_HAND);

		return;
	}

	SMOOTHInt	 numTocEntries;
	SMOOTHString	 file = SMOOTH::StartDirectory;

	file.Append("bonkenc.ini");

	CR_SetTransportLayer(currentConfig->cdrip_ntscsi);

	CR_Init(file);

	CR_SetActiveCDROM(currentConfig->cdrip_activedrive);

	CR_ReadToc();

	numTocEntries = CR_GetNumTocEntries();

	for (int i = 0; i < numTocEntries; i++)
	{
		TOCENTRY	 entry = CR_GetTocEntry(i);

		if (!(entry.btFlag & CDROMDATAFLAG))
		{
			sa_joblist.AddEntry(SMOOTHString("Audio CD ").Append(SMOOTHString::IntToString(currentConfig->cdrip_activedrive)).Append(" track ").Append(SMOOTHString::IntToString(entry.btTrackNumber)), joblist->AddEntry(SMOOTHString("Audio CD ").Append(SMOOTHString::IntToString(currentConfig->cdrip_activedrive)).Append(" track ").Append(SMOOTHString::IntToString(entry.btTrackNumber)), NULLPROC));

			txt_joblist->SetText(SMOOTHString::IntToString(joblist->GetNOfEntries()).Append(" file(s) in joblist:"));
		}
	}
}

SMOOTHBool bonkEnc::LoadBladeDLL()
{
	SMOOTHString	 file = SMOOTH::StartDirectory;

	file.Append("bladeenc.dll");

	bladedll = LoadLibraryA(file);

	if (bladedll == NIL) return false;

	beInitStream	= (BEINITSTREAM) GetProcAddress(bladedll, "beInitStream");
	beEncodeChunk	= (BEENCODECHUNK) GetProcAddress(bladedll, "beEncodeChunk");
	beDeinitStream	= (BEDEINITSTREAM) GetProcAddress(bladedll, "beDeinitStream");
	beCloseStream	= (BECLOSESTREAM) GetProcAddress(bladedll, "beCloseStream");
	beVersion	= (BEVERSION) GetProcAddress(bladedll, "beVersion");

	return true;
}

SMOOTHVoid bonkEnc::FreeBladeDLL()
{
	FreeLibrary(bladedll);	
}

SMOOTHBool bonkEnc::LoadFAACDLL()
{
	SMOOTHString	 file = SMOOTH::StartDirectory;

	file.Append("faac.dll");

	faacdll = LoadLibraryA(file);

	if (faacdll == NIL) return false;

	faacEncOpen			= (FAACENCOPEN) GetProcAddress(faacdll, "faacEncOpen@16");
	faacEncGetCurrentConfiguration	= (FAACENCGETCURRENTCONFIGURATION) GetProcAddress(faacdll, "faacEncGetCurrentConfiguration@4");
	faacEncSetConfiguration		= (FAACENCSETCONFIGURATION) GetProcAddress(faacdll, "faacEncSetConfiguration@8");
	faacEncEncode			= (FAACENCENCODE) GetProcAddress(faacdll, "faacEncEncode@20");
	faacEncClose			= (FAACENCCLOSE) GetProcAddress(faacdll, "faacEncClose@4");

	return true;
}

SMOOTHVoid bonkEnc::FreeFAACDLL()
{
	FreeLibrary(faacdll);	
}

SMOOTHBool bonkEnc::LoadCDRipDLL()
{
	SMOOTHString	 file = SMOOTH::StartDirectory;

	file.Append("cdrip.dll");

	cdripdll = LoadLibraryA(file);

	if (cdripdll == NIL) return false;

	CR_Init			= (CR_INIT) GetProcAddress(cdripdll, "CR_Init");
	CR_ReadToc		= (CR_READTOC) GetProcAddress(cdripdll, "CR_ReadToc");
	CR_GetNumTocEntries	= (CR_GETNUMTOCENTRIES) GetProcAddress(cdripdll, "CR_GetNumTocEntries");
	CR_GetTocEntry		= (CR_GETTOCENTRY) GetProcAddress(cdripdll, "CR_GetTocEntry");
	CR_OpenRipper		= (CR_OPENRIPPER) GetProcAddress(cdripdll, "CR_OpenRipper");
	CR_CloseRipper		= (CR_CLOSERIPPER) GetProcAddress(cdripdll, "CR_CloseRipper");
	CR_RipChunk		= (CR_RIPCHUNK) GetProcAddress(cdripdll, "CR_RipChunk");
	CR_GetNumCDROM		= (CR_GETNUMCDROM) GetProcAddress(cdripdll, "CR_GetNumCDROM");
	CR_GetActiveCDROM	= (CR_GETACTIVECDROM) GetProcAddress(cdripdll, "CR_GetActiveCDROM");
	CR_SetActiveCDROM	= (CR_SETACTIVECDROM) GetProcAddress(cdripdll, "CR_SetActiveCDROM");
	CR_GetCDROMParameters	= (CR_GETCDROMPARAMETERS) GetProcAddress(cdripdll, "CR_GetCDROMParameters");
	CR_SetCDROMParameters	= (CR_SETCDROMPARAMETERS) GetProcAddress(cdripdll, "CR_SetCDROMParameters");
	CR_SetTransportLayer	= (CR_SETTRANSPORTLAYER) GetProcAddress(cdripdll, "CR_SetTransportLayer");
	CR_LockCD		= (CR_LOCKCD) GetProcAddress(cdripdll, "CR_LockCD");

	file = SMOOTH::StartDirectory;

	file.Append("bonkenc.ini");

	CR_Init(file);

	return true;
}

SMOOTHVoid bonkEnc::FreeCDRipDLL()
{
	FreeLibrary(cdripdll);	
}
