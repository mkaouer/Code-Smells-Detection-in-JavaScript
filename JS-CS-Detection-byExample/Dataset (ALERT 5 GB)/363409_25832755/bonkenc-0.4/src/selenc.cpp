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
#include <selenc.h>
#include <resources.h>
#include <main.h>

selectEncoder::selectEncoder(bonkEncConfig *config)
{
	POINT	 pos;
	SIZE	 size;

	encoder = config->encoder;
	oldenc = config->encoder;

	mainWnd			= new SMOOTHWindow("Select encoder");
	mainWnd_titlebar	= new SMOOTHTitlebar(false, false, true);
	divbar			= new SMOOTHDivisionbar(42, OR_HORZ | OR_BOTTOM);
	mainWnd_layer		= new SMOOTHLayer();

	pos.x = 175;
	pos.y = 29;
	size.cx = 0;
	size.cy = 0;

	btn_cancel		= new SMOOTHButton("Cancel", pos, size, SMOOTHProc(selectEncoder, this, Cancel));
	btn_cancel->SetOrientation(OR_LOWERRIGHT);

	pos.x -= 88;

	btn_ok			= new SMOOTHButton("OK", pos, size, SMOOTHProc(selectEncoder, this, OK));
	btn_ok->SetOrientation(OR_LOWERRIGHT);

	pos.x = 7;
	pos.y = 11;
	size.cx = 168;
	size.cy = 65;

	group			= new SMOOTHGroupBox("Encoder", pos, size);

	pos.x = 17;
	pos.y = 24;
	size.cx = 147;
	size.cy = 0;

	opt_bonk		= new SMOOTHOptionBox("BonkEnc v0.4", pos, size, &encoder, ENCODER_BONKENC, NULLPROC);

	pos.y += 25;

	opt_blade		= new SMOOTHOptionBox("BladeEnc v0.94", pos, size, &encoder, ENCODER_BLADEENC, NULLPROC);
	if (!config->enable_blade) opt_blade->Deactivate();

	RegisterObject(mainWnd);

	mainWnd_layer->RegisterObject(group);
	mainWnd_layer->RegisterObject(btn_ok);
	mainWnd_layer->RegisterObject(btn_cancel);
	mainWnd_layer->RegisterObject(opt_bonk);
	mainWnd_layer->RegisterObject(opt_blade);

	mainWnd->RegisterObject(mainWnd_titlebar);
	mainWnd->RegisterObject(mainWnd_layer);
	mainWnd->RegisterObject(divbar);

	mainWnd->SetExStyle(WS_EX_TOOLWINDOW);
	mainWnd->SetIcon(SMOOTH::LoadImage("bonkenc.pci", 0, NULL));
	mainWnd->SetApplicationIcon(IDI_ICON);
	mainWnd->SetMetrics(200, 200, 188, 154);
}

selectEncoder::~selectEncoder()
{
	mainWnd->UnregisterObject(mainWnd_titlebar);
	mainWnd->UnregisterObject(mainWnd_layer);
	mainWnd->UnregisterObject(divbar);

	mainWnd_layer->UnregisterObject(group);
	mainWnd_layer->UnregisterObject(btn_ok);
	mainWnd_layer->UnregisterObject(btn_cancel);
	mainWnd_layer->UnregisterObject(opt_bonk);
	mainWnd_layer->UnregisterObject(opt_blade);

	UnregisterObject(mainWnd);

	delete mainWnd_titlebar;
	delete mainWnd_layer;
	delete mainWnd;
	delete divbar;
	delete group;
	delete btn_ok;
	delete btn_cancel;
	delete opt_bonk;
	delete opt_blade;
}

int selectEncoder::ShowDialog()
{
	mainWnd->Stay();

	return encoder;
}

void selectEncoder::OK()
{
	SMOOTH::CloseWindow(mainWnd);
}

void selectEncoder::Cancel()
{
	encoder = oldenc;

	SMOOTH::CloseWindow(mainWnd);
}
