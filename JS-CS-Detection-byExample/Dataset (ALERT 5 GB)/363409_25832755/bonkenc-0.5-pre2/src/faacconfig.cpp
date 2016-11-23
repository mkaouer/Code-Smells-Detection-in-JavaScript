 /* BonkEnc version 0.5
  * Copyright (C) 2001-2002 Robert Kausch <robert.kausch@gmx.net>
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the "GNU General Public License".
  *
  * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. */

#include <smoothx.h>
#include <faacconfig.h>
#include <resources.h>

configureFAAC::configureFAAC(bonkEncConfig *config)
{
	SMOOTHPoint	 pos;
	SMOOTHSize	 size;

	currentConfig = config;

	mpegVersion = currentConfig->faac_mpegversion;
	aacType = currentConfig->faac_type;
	bitrate = currentConfig->faac_bitrate;
	allowjs = currentConfig->faac_allowjs;
	usetns = currentConfig->faac_usetns;

	mainWnd			= new SMOOTHWindow("FAAC encoder configuration");
	mainWnd_titlebar	= new SMOOTHTitlebar(false, false, true);
	divbar			= new SMOOTHDivisionbar(42, OR_HORZ | OR_BOTTOM);
	mainWnd_layer		= new SMOOTHLayer();

	pos.x = 175;
	pos.y = 29;
	size.cx = 0;
	size.cy = 0;

	btn_cancel		= new SMOOTHButton("Cancel", NIL, pos, size, SMOOTHProc(configureFAAC, this, Cancel));
	btn_cancel->SetOrientation(OR_LOWERRIGHT);

	pos.x -= 88;

	btn_ok			= new SMOOTHButton("OK", NIL, pos, size, SMOOTHProc(configureFAAC, this, OK));
	btn_ok->SetOrientation(OR_LOWERRIGHT);

	pos.x = 7;
	pos.y = 11;
	size.cx = 120;
	size.cy = 65;

	group_version			= new SMOOTHGroupBox("MPEG version", pos, size);

	pos.x += 10;
	pos.y += 13;
	size.cx = 99;
	size.cy = 0;

	option_version_mpeg2		= new SMOOTHOptionBox("MPEG 2", pos, size, &mpegVersion, 1, SMOOTHProc(configureFAAC, this, SetMPEGVersion));

	pos.y += 25;

	option_version_mpeg4		= new SMOOTHOptionBox("MPEG 4", pos, size, &mpegVersion, 0, SMOOTHProc(configureFAAC, this, SetMPEGVersion));

	pos.x = 7;
	pos.y = 88;
	size.cx = 120;
	size.cy = 90;

	group_aactype			= new SMOOTHGroupBox("AAC object type", pos, size);

	pos.x += 10;
	pos.y += 13;
	size.cx = 99;
	size.cy = 0;

	option_aactype_main		= new SMOOTHOptionBox("MAIN", pos, size, &aacType, 0, NULLPROC);

	pos.y += 25;

	option_aactype_low		= new SMOOTHOptionBox("LOW", pos, size, &aacType, 1, NULLPROC);

	pos.y += 25;

	option_aactype_ltp		= new SMOOTHOptionBox("LTP", pos, size, &aacType, 3, NULLPROC);
	if (mpegVersion == 1) option_aactype_ltp->Deactivate();

	pos.x = 135;
	pos.y = 11;
	size.cx = 320;
	size.cy = 43;

	group_bitrate			= new SMOOTHGroupBox("Bitrate", pos, size);

	pos.x += 11;
	pos.y += 15;

	text_bitrate			= new SMOOTHText("Bitrate per channel:", pos);

	pos.x += 100;
	pos.y -= 2;
	size.cx = 133;
	size.cy = 0;

	slider_bitrate			= new SMOOTHSlider(pos, size, OR_HORZ, &bitrate, 8, 256, SMOOTHProc(configureFAAC, this, SetBitrate));

	pos.x += 141;
	pos.y -= 1;
	size.cx = 25;

	edit_bitrate			= new SMOOTHEditBox("", pos, size, EDB_NUMERIC, 3, SMOOTHProc(configureFAAC, this, SetBitrateByEditBox));

	pos.x += 32;
	pos.y += 3;

	text_bitrate_kbps		= new SMOOTHText("kbps", pos);

	pos.x = 135;
	pos.y = 66;
	size.cx = 129;
	size.cy = 43;

	group_js			= new SMOOTHGroupBox("Stereo mode", pos, size);

	pos.x += 10;
	pos.y += 13;
	size.cx = 108;
	size.cy = 0;

	check_js			= new SMOOTHCheckBox("Allow Joint Stereo", pos, size, &allowjs, NULLPROC);

	pos.x = 272;
	pos.y = 66;
	size.cx = 183;
	size.cy = 43;

	group_tns			= new SMOOTHGroupBox("Temporal Noise Shaping", pos, size);

	pos.x += 10;
	pos.y += 13;
	size.cx = 162;
	size.cy = 0;

	check_tns			= new SMOOTHCheckBox("Use Temporal Noise Shaping", pos, size, &usetns, NULLPROC);

	pos.x = 135;
	pos.y = 121;
	size.cx = 320;
	size.cy = 43;

	group_bandwidth			= new SMOOTHGroupBox("Maximum bandwidth", pos, size);

	pos.x += 11;
	pos.y += 15;

	text_bandwidth			= new SMOOTHText("Maximum AAC frequency bandwidth to use (Hz):", pos);

	pos.x += 236;
	pos.y -= 3;
	size.cx = 63;
	size.cy = 0;

	edit_bandwidth			= new SMOOTHEditBox(SMOOTHString::IntToString(currentConfig->faac_bandwidth), pos, size, EDB_NUMERIC, 5, NULLPROC);

	SetBitrate();

	RegisterObject(mainWnd);

	mainWnd_layer->RegisterObject(btn_ok);
	mainWnd_layer->RegisterObject(btn_cancel);
	mainWnd_layer->RegisterObject(group_version);
	mainWnd_layer->RegisterObject(option_version_mpeg2);
	mainWnd_layer->RegisterObject(option_version_mpeg4);
	mainWnd_layer->RegisterObject(group_aactype);
	mainWnd_layer->RegisterObject(option_aactype_main);
	mainWnd_layer->RegisterObject(option_aactype_low);
	mainWnd_layer->RegisterObject(option_aactype_ltp);
	mainWnd_layer->RegisterObject(group_bitrate);
	mainWnd_layer->RegisterObject(text_bitrate);
	mainWnd_layer->RegisterObject(slider_bitrate);
	mainWnd_layer->RegisterObject(edit_bitrate);
	mainWnd_layer->RegisterObject(text_bitrate_kbps);
	mainWnd_layer->RegisterObject(group_js);
	mainWnd_layer->RegisterObject(check_js);
	mainWnd_layer->RegisterObject(group_tns);
	mainWnd_layer->RegisterObject(check_tns);
	mainWnd_layer->RegisterObject(group_bandwidth);
	mainWnd_layer->RegisterObject(text_bandwidth);
	mainWnd_layer->RegisterObject(edit_bandwidth);

	mainWnd->RegisterObject(mainWnd_titlebar);
	mainWnd->RegisterObject(mainWnd_layer);
	mainWnd->RegisterObject(divbar);

	mainWnd->SetExStyle(WS_EX_TOOLWINDOW);
	mainWnd->SetIcon(SMOOTH::LoadImage("bonkenc.pci", 0, NIL));
	mainWnd->SetApplicationIcon(IDI_ICON);
	mainWnd->SetMetrics(SMOOTHPoint(120, 120), SMOOTHSize(468, 256));
}

configureFAAC::~configureFAAC()
{
	mainWnd->UnregisterObject(mainWnd_titlebar);
	mainWnd->UnregisterObject(mainWnd_layer);
	mainWnd->UnregisterObject(divbar);

	mainWnd_layer->UnregisterObject(btn_ok);
	mainWnd_layer->UnregisterObject(btn_cancel);
	mainWnd_layer->UnregisterObject(group_version);
	mainWnd_layer->UnregisterObject(option_version_mpeg2);
	mainWnd_layer->UnregisterObject(option_version_mpeg4);
	mainWnd_layer->UnregisterObject(group_aactype);
	mainWnd_layer->UnregisterObject(option_aactype_main);
	mainWnd_layer->UnregisterObject(option_aactype_low);
	mainWnd_layer->UnregisterObject(option_aactype_ltp);
	mainWnd_layer->UnregisterObject(group_bitrate);
	mainWnd_layer->UnregisterObject(text_bitrate);
	mainWnd_layer->UnregisterObject(slider_bitrate);
	mainWnd_layer->UnregisterObject(edit_bitrate);
	mainWnd_layer->UnregisterObject(text_bitrate_kbps);
	mainWnd_layer->UnregisterObject(group_js);
	mainWnd_layer->UnregisterObject(check_js);
	mainWnd_layer->UnregisterObject(group_tns);
	mainWnd_layer->UnregisterObject(check_tns);
	mainWnd_layer->UnregisterObject(group_bandwidth);
	mainWnd_layer->UnregisterObject(text_bandwidth);
	mainWnd_layer->UnregisterObject(edit_bandwidth);

	UnregisterObject(mainWnd);

	delete mainWnd_titlebar;
	delete mainWnd_layer;
	delete mainWnd;
	delete divbar;
	delete btn_ok;
	delete btn_cancel;
	delete group_version;
	delete option_version_mpeg2;
	delete option_version_mpeg4;
	delete group_aactype;
	delete option_aactype_main;
	delete option_aactype_low;
	delete option_aactype_ltp;
	delete group_bitrate;
	delete text_bitrate;
	delete slider_bitrate;
	delete edit_bitrate;
	delete text_bitrate_kbps;
	delete group_js;
	delete check_js;
	delete group_tns;
	delete check_tns;
	delete group_bandwidth;
	delete text_bandwidth;
	delete edit_bandwidth;
}

SMOOTHInt configureFAAC::ShowDialog()
{
	mainWnd->Stay();

	return mainWnd->value;
}

SMOOTHVoid configureFAAC::OK()
{
	if (bitrate < 8)	bitrate = 8;
	if (bitrate > 256)	bitrate = 256;

	currentConfig->faac_mpegversion = mpegVersion;
	currentConfig->faac_type = aacType;
	currentConfig->faac_bitrate = bitrate;
	currentConfig->faac_allowjs = allowjs;
	currentConfig->faac_usetns = usetns;
	currentConfig->faac_bandwidth = edit_bandwidth->GetText().ToInt();

	SMOOTH::CloseWindow(mainWnd);
}

SMOOTHVoid configureFAAC::Cancel()
{
	SMOOTH::CloseWindow(mainWnd);
}

SMOOTHVoid configureFAAC::SetMPEGVersion()
{
	if (mpegVersion == 0) // MPEG4;
	{
		option_aactype_ltp->Activate();
	}
	else if (mpegVersion == 1) // MPEG2;
	{
		if (aacType == 3) // LTP
		{
			aacType = 0;

			option_aactype_main->Process(SM_CHECKOPTIONBOXES, 0, 0);
			option_aactype_low->Process(SM_CHECKOPTIONBOXES, 0, 0);
			option_aactype_ltp->Process(SM_CHECKOPTIONBOXES, 0, 0);
		}

		option_aactype_ltp->Deactivate();
	}
}

SMOOTHVoid configureFAAC::SetBitrate()
{
	edit_bitrate->SetText(SMOOTHString::IntToString(bitrate));
}

SMOOTHVoid configureFAAC::SetBitrateByEditBox()
{
	slider_bitrate->SetValue(edit_bitrate->GetText().ToInt());
}
