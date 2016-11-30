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
#include <bonkconfig.h>
#include <resources.h>
#include <shlobjmini.h>

configureBonkEnc::configureBonkEnc(bonkEncConfig *config)
{
	POINT	 pos;
	SIZE	 size;

	currentConfig = config;

	quant = currentConfig->bonk_quantization;
	predictor = currentConfig->bonk_predictor;
	downsampling = currentConfig->bonk_downsampling;
	jstereo = currentConfig->bonk_jstereo;

	mainWnd			= new SMOOTHWindow("BonkEnc configuration");
	mainWnd_titlebar	= new SMOOTHTitlebar(false, false, true);
	divbar			= new SMOOTHDivisionbar(42, OR_HORZ | OR_BOTTOM);
	mainWnd_layer		= new SMOOTHLayer();

	pos.x = 175;
	pos.y = 29;
	size.cx = 0;
	size.cy = 0;

	btn_cancel		= new SMOOTHButton("Cancel", pos, size, SMOOTHProc(configureBonkEnc, this, Cancel));
	btn_cancel->SetOrientation(OR_LOWERRIGHT);

	pos.x -= 88;

	btn_ok			= new SMOOTHButton("OK", pos, size, SMOOTHProc(configureBonkEnc, this, OK));
	btn_ok->SetOrientation(OR_LOWERRIGHT);

	pos.x = 7;
	pos.y = 11;
	size.cx = 168;
	size.cy = 43;

	group_quant			= new SMOOTHGroupBox("Quantization:", pos, size);

	pos.x += 176;

	group_predictor			= new SMOOTHGroupBox("Predictor size:", pos, size);

	pos.x -= 176;
	pos.y += 55;

	group_downsampling		= new SMOOTHGroupBox("Downsampling ratio:", pos, size);

	pos.x += 176;

	group_stereo			= new SMOOTHGroupBox("Channels:", pos, size);

	pos.x -= 176;
	pos.y += 55;
	size.cx += 176;

	group_dir			= new SMOOTHGroupBox("Output directory:", pos, size);

	pos.x = 17;
	pos.y = 24;
	size.cx = 120;
	size.cy = 0;

	slider_quant			= new SMOOTHSlider(pos, size, OR_HORZ, &quant, 0, 40, SMOOTHProc(configureBonkEnc, this, SetQuantization));

	pos.x += 127;
	pos.y += 2;

	text_quant			= new SMOOTHText("", pos);
	SetQuantization();

	pos.x += 49;
	pos.y -= 2;

	slider_predictor		= new SMOOTHSlider(pos, size, OR_HORZ, &predictor, 0, 512, SMOOTHProc(configureBonkEnc, this, SetPredictorSize));

	pos.x += 127;
	pos.y += 2;

	text_predictor			= new SMOOTHText("", pos);
	SetPredictorSize();

	pos.x = 17;
	pos.y += 53;
	size.cx = 120;
	size.cy = 0;

	slider_downsampling		= new SMOOTHSlider(pos, size, OR_HORZ, &downsampling, 1, 10, SMOOTHProc(configureBonkEnc, this, SetDownsamplingRatio));

	pos.x += 127;
	pos.y += 2;

	text_downsampling		= new SMOOTHText("", pos);
	SetDownsamplingRatio();

	pos.x += 49;
	pos.y -= 2;
	size.cx += 27;

	check_joint			= new SMOOTHCheckBox("Enable \'Joint Stereo\'", pos, size, &jstereo, NULLPROC);

	pos.x = 17;
	pos.y += 54;
	size.cx = 236;

	edit_dir			= new SMOOTHEditBox(currentConfig->bonk_outdir, pos, size, EDB_ALPHANUMERIC, 0, NULLPROC);

	pos.x += 244;
	pos.y -= 1;
	size.cx = 0;

	button_dir_browse		= new SMOOTHButton("Browse", pos, size, SMOOTHProc(configureBonkEnc, this, SelectDir));

	RegisterObject(mainWnd);

	mainWnd_layer->RegisterObject(btn_ok);
	mainWnd_layer->RegisterObject(btn_cancel);
	mainWnd_layer->RegisterObject(group_quant);
	mainWnd_layer->RegisterObject(slider_quant);
	mainWnd_layer->RegisterObject(text_quant);
	mainWnd_layer->RegisterObject(group_dir);
	mainWnd_layer->RegisterObject(edit_dir);
	mainWnd_layer->RegisterObject(button_dir_browse);
	mainWnd_layer->RegisterObject(group_stereo);
	mainWnd_layer->RegisterObject(check_joint);
	mainWnd_layer->RegisterObject(group_downsampling);
	mainWnd_layer->RegisterObject(slider_downsampling);
	mainWnd_layer->RegisterObject(text_downsampling);
	mainWnd_layer->RegisterObject(group_predictor);
	mainWnd_layer->RegisterObject(slider_predictor);
	mainWnd_layer->RegisterObject(text_predictor);

	mainWnd->RegisterObject(mainWnd_titlebar);
	mainWnd->RegisterObject(mainWnd_layer);
	mainWnd->RegisterObject(divbar);

	mainWnd->SetExStyle(WS_EX_TOOLWINDOW);
	mainWnd->SetIcon(SMOOTH::LoadImage("bonkenc.pci", 0, NULL));
	mainWnd->SetApplicationIcon(IDI_ICON);
	mainWnd->SetMetrics(120, 120, 364, 242);
}

configureBonkEnc::~configureBonkEnc()
{
	mainWnd->UnregisterObject(mainWnd_titlebar);
	mainWnd->UnregisterObject(mainWnd_layer);
	mainWnd->UnregisterObject(divbar);

	mainWnd_layer->UnregisterObject(btn_ok);
	mainWnd_layer->UnregisterObject(btn_cancel);
	mainWnd_layer->UnregisterObject(group_quant);
	mainWnd_layer->UnregisterObject(slider_quant);
	mainWnd_layer->UnregisterObject(text_quant);
	mainWnd_layer->UnregisterObject(group_dir);
	mainWnd_layer->UnregisterObject(edit_dir);
	mainWnd_layer->UnregisterObject(button_dir_browse);
	mainWnd_layer->UnregisterObject(group_stereo);
	mainWnd_layer->UnregisterObject(check_joint);
	mainWnd_layer->UnregisterObject(group_downsampling);
	mainWnd_layer->UnregisterObject(slider_downsampling);
	mainWnd_layer->UnregisterObject(text_downsampling);
	mainWnd_layer->UnregisterObject(group_predictor);
	mainWnd_layer->UnregisterObject(slider_predictor);
	mainWnd_layer->UnregisterObject(text_predictor);

	UnregisterObject(mainWnd);

	delete mainWnd_titlebar;
	delete mainWnd_layer;
	delete mainWnd;
	delete divbar;
	delete group_quant;
	delete slider_quant;
	delete text_quant;
	delete group_dir;
	delete edit_dir;
	delete button_dir_browse;
	delete btn_ok;
	delete btn_cancel;
	delete group_stereo;
	delete check_joint;
	delete group_downsampling;
	delete slider_downsampling;
	delete text_downsampling;
	delete group_predictor;
	delete slider_predictor;
	delete text_predictor;
}

int configureBonkEnc::ShowDialog()
{
	mainWnd->Stay();

	return mainWnd->value;
}

void configureBonkEnc::OK()
{
	currentConfig->bonk_quantization = quant;
	currentConfig->bonk_predictor = predictor;
	currentConfig->bonk_downsampling = downsampling;
	currentConfig->bonk_jstereo = jstereo;
	currentConfig->bonk_outdir = edit_dir->GetText();

	int	 len = currentConfig->bonk_outdir.Length() - 1;

	if (currentConfig->bonk_outdir[len] != '\\') currentConfig->bonk_outdir[++len] = '\\';

	SMOOTH::CloseWindow(mainWnd);
}

void configureBonkEnc::Cancel()
{
	SMOOTH::CloseWindow(mainWnd);
}

void configureBonkEnc::SetQuantization()
{
	SMOOTHString	 val = SMOOTHString::DoubleToString(0.05 * (double) quant);

	switch (val.Length())
	{
		case 1:
			val.Append(".00");
			break;
		case 3:
			val.Append("0");
			break;
	}

	text_quant->SetText(val);
}

void configureBonkEnc::SetPredictorSize()
{
	text_predictor->SetText(SMOOTHString::IntToString(predictor));
}

void configureBonkEnc::SetDownsamplingRatio()
{
	text_downsampling->SetText(SMOOTHString::IntToString(downsampling).Append(":1"));
}

void configureBonkEnc::SelectDir()
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
