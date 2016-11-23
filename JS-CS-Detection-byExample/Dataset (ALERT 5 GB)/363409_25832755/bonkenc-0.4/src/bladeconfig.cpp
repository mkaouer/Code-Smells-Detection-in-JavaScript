 /* BonkEnc version 0.4
  * Copyright (C) 2001 Robert Kausch <robert.kausch@gmx.net>
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the "GNU General Public License".
  *
  * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. */

#include <smoothx.h>
#include <bladeconfig.h>
#include <resources.h>
#include <shlobjmini.h>

configureBladeEnc::configureBladeEnc(bonkEncConfig *config)
{
	POINT	 pos;
	SIZE	 size;

	currentConfig = config;

	bitrate = GetSliderValue();
	crc = currentConfig->blade_crc;
	copyright = currentConfig->blade_copyright;
	original = currentConfig->blade_original;

	mainWnd			= new SMOOTHWindow("BladeEnc configuration");
	mainWnd_titlebar	= new SMOOTHTitlebar(false, false, true);
	divbar			= new SMOOTHDivisionbar(42, OR_HORZ | OR_BOTTOM);
	mainWnd_layer		= new SMOOTHLayer();

	pos.x = 175;
	pos.y = 29;
	size.cx = 0;
	size.cy = 0;

	btn_cancel		= new SMOOTHButton("Cancel", pos, size, SMOOTHProc(configureBladeEnc, this, Cancel));
	btn_cancel->SetOrientation(OR_LOWERRIGHT);

	pos.x -= 88;

	btn_ok			= new SMOOTHButton("OK", pos, size, SMOOTHProc(configureBladeEnc, this, OK));
	btn_ok->SetOrientation(OR_LOWERRIGHT);

	pos.x = 7;
	pos.y = 11;
	size.cx = 168;
	size.cy = 43;

	group_bit			= new SMOOTHGroupBox("Bitrate:", pos, size);

	pos.x += 176;

	group_copyright			= new SMOOTHGroupBox("Copyright bit:", pos, size);

	pos.x -= 176;
	pos.y += 55;

	group_crc			= new SMOOTHGroupBox("CRC:", pos, size);

	pos.x += 176;

	group_original			= new SMOOTHGroupBox("Original bit:", pos, size);

	pos.x -= 176;
	pos.y += 55;
	size.cx += 176;

	group_dir			= new SMOOTHGroupBox("Output directory:", pos, size);

	pos.x = 17;
	pos.y = 24;
	size.cx = 103;
	size.cy = 0;

	slider_bit			= new SMOOTHSlider(pos, size, OR_HORZ, &bitrate, 0, 13, SMOOTHProc(configureBladeEnc, this, SetBitrate));

	pos.x += 110;
	pos.y += 2;

	text_bit			= new SMOOTHText("", pos);
	SetBitrate();

	pos.x += 66;
	pos.y -= 2;
	size.cx += 44;

	check_copyright			= new SMOOTHCheckBox("Set Copyright bit", pos, size, &copyright, NULLPROC);

	pos.x = 17;
	pos.y += 55;
	size.cx = 147;
	size.cy = 0;

	check_crc			= new SMOOTHCheckBox("Enable CRC", pos, size, &crc, NULLPROC);

	pos.x += 176;

	check_original			= new SMOOTHCheckBox("Set Original bit", pos, size, &original, NULLPROC);

	pos.x = 17;
	pos.y += 54;
	size.cx = 236;

	edit_dir			= new SMOOTHEditBox(currentConfig->blade_outdir, pos, size, EDB_ALPHANUMERIC, 0, NULLPROC);

	pos.x += 244;
	pos.y -= 1;
	size.cx = 0;

	button_dir_browse		= new SMOOTHButton("Browse", pos, size, SMOOTHProc(configureBladeEnc, this, SelectDir));

	RegisterObject(mainWnd);

	mainWnd_layer->RegisterObject(btn_ok);
	mainWnd_layer->RegisterObject(btn_cancel);
	mainWnd_layer->RegisterObject(group_bit);
	mainWnd_layer->RegisterObject(slider_bit);
	mainWnd_layer->RegisterObject(text_bit);
	mainWnd_layer->RegisterObject(group_dir);
	mainWnd_layer->RegisterObject(edit_dir);
	mainWnd_layer->RegisterObject(button_dir_browse);
	mainWnd_layer->RegisterObject(group_crc);
	mainWnd_layer->RegisterObject(check_crc);
	mainWnd_layer->RegisterObject(group_copyright);
	mainWnd_layer->RegisterObject(check_copyright);
	mainWnd_layer->RegisterObject(group_original);
	mainWnd_layer->RegisterObject(check_original);

	mainWnd->RegisterObject(mainWnd_titlebar);
	mainWnd->RegisterObject(mainWnd_layer);
	mainWnd->RegisterObject(divbar);

	mainWnd->SetExStyle(WS_EX_TOOLWINDOW);
	mainWnd->SetIcon(SMOOTH::LoadImage("bonkenc.pci", 0, NULL));
	mainWnd->SetApplicationIcon(IDI_ICON);
	mainWnd->SetMetrics(120, 120, 364, 242);
}

configureBladeEnc::~configureBladeEnc()
{
	mainWnd->UnregisterObject(mainWnd_titlebar);
	mainWnd->UnregisterObject(mainWnd_layer);
	mainWnd->UnregisterObject(divbar);

	mainWnd_layer->UnregisterObject(btn_ok);
	mainWnd_layer->UnregisterObject(btn_cancel);
	mainWnd_layer->UnregisterObject(group_bit);
	mainWnd_layer->UnregisterObject(slider_bit);
	mainWnd_layer->UnregisterObject(text_bit);
	mainWnd_layer->UnregisterObject(group_dir);
	mainWnd_layer->UnregisterObject(edit_dir);
	mainWnd_layer->UnregisterObject(button_dir_browse);
	mainWnd_layer->UnregisterObject(group_crc);
	mainWnd_layer->UnregisterObject(check_crc);
	mainWnd_layer->UnregisterObject(group_copyright);
	mainWnd_layer->UnregisterObject(check_copyright);
	mainWnd_layer->UnregisterObject(group_original);
	mainWnd_layer->UnregisterObject(check_original);

	UnregisterObject(mainWnd);

	delete mainWnd_titlebar;
	delete mainWnd_layer;
	delete mainWnd;
	delete divbar;
	delete group_bit;
	delete slider_bit;
	delete text_bit;
	delete group_dir;
	delete edit_dir;
	delete button_dir_browse;
	delete btn_ok;
	delete btn_cancel;
	delete group_crc;
	delete check_crc;
	delete group_copyright;
	delete check_copyright;
	delete group_original;
	delete check_original;
}

int configureBladeEnc::ShowDialog()
{
	mainWnd->Stay();

	return mainWnd->value;
}

void configureBladeEnc::OK()
{
	currentConfig->blade_bitrate = GetBitrate();
	currentConfig->blade_crc = crc;
	currentConfig->blade_copyright = copyright;
	currentConfig->blade_original = original;
	currentConfig->blade_outdir = edit_dir->GetText();

	int	 len = currentConfig->blade_outdir.Length() - 1;

	if (currentConfig->blade_outdir[len] != '\\') currentConfig->blade_outdir[++len] = '\\';

	SMOOTH::CloseWindow(mainWnd);
}

void configureBladeEnc::Cancel()
{
	SMOOTH::CloseWindow(mainWnd);
}

void configureBladeEnc::SetBitrate()
{
	text_bit->SetText(SMOOTHString::IntToString(GetBitrate()).Append(" kbit"));
}

int configureBladeEnc::GetBitrate()
{
	switch (bitrate)
	{
		case 0:
			return 32;
		case 1:
			return 40;
		case 2:
			return 48;
		case 3:
			return 56;
		case 4:
			return 64;
		case 5:
			return 80;
		case 6:
			return 96;
		case 7:
			return 112;
		case 8:
			return 128;
		case 9:
			return 160;
		case 10:
			return 192;
		case 11:
			return 224;
		case 12:
			return 256;
		case 13:
			return 320;
		default:
			return 128;
	}
}

int configureBladeEnc::GetSliderValue()
{
	switch (currentConfig->blade_bitrate)
	{
		case 32:
			return 0;
		case 40:
			return 1;
		case 48:
			return 2;
		case 56:
			return 3;
		case 64:
			return 4;
		case 80:
			return 5;
		case 96:
			return 6;
		case 112:
			return 7;
		case 128:
			return 8;
		case 160:
			return 9;
		case 196:
			return 10;
		case 224:
			return 11;
		case 256:
			return 12;
		case 320:
			return 13;
		default:
			return 8;
	}
}

void configureBladeEnc::SelectDir()
{
	BROWSEINFO	 info;
	char		*buffer = new char [32768];
	SMOOTHString	 dir;
	int		 len;

	info.hwndOwner = mainWnd->hwnd;
	info.pidlRoot = NULL;
	info.pszDisplayName = buffer;
	info.lpszTitle = "\nSelect the folder in which the encoded files will be placed:";
	info.ulFlags = BIF_RETURNONLYFSDIRS;
	info.lpfn = NULL;
	info.lParam = 0;
	info.iImage = 0;

	SHGetPathFromIDList(SHBrowseForFolder(&info), buffer);

	if (buffer != NULL)
	{
		dir = buffer;
		len = dir.Length() - 1;

		if (dir[len] != '\\') dir[++len] = '\\';

		edit_dir->SetText(dir);
	}

	delete [] buffer;
}
