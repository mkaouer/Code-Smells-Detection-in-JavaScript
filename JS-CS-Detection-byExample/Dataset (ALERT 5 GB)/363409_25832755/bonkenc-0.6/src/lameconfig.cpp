 /* BonkEnc version 0.6
  * Copyright (C) 2001-2002 Robert Kausch <robert.kausch@gmx.net>
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the "GNU General Public License".
  *
  * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. */

#include <smoothx.h>
#include <lameconfig.h>
#include <resources.h>

#include <lame/lame.h>

configureLameEnc::configureLameEnc(bonkEncConfig *config)
{
	SMOOTHPoint	 pos;
	SMOOTHSize	 size;

	currentConfig = config;

	set_bitrate = currentConfig->lame_set_bitrate;
	bitrate = GetSliderValue();
	ratio = currentConfig->lame_ratio;
	set_quality = currentConfig->lame_set_quality;
	quality = 9 - currentConfig->lame_quality;
	stereomode = currentConfig->lame_stereomode;
	forcejs = currentConfig->lame_forcejs;
	vbrmode = currentConfig->lame_vbrmode;
	vbrquality = 9 - currentConfig->lame_vbrquality;
	abrbitrate = currentConfig->lame_abrbitrate;
	set_min_vbr_brate = currentConfig->lame_set_min_vbr_bitrate;
	min_vbr_brate = GetMinVBRSliderValue();
	set_max_vbr_brate = currentConfig->lame_set_max_vbr_bitrate;
	max_vbr_brate = GetMaxVBRSliderValue();
	set_original = currentConfig->lame_original;
	set_copyright = currentConfig->lame_copyright;
	set_private = currentConfig->lame_private;
	set_crc = currentConfig->lame_crc;
	set_iso = currentConfig->lame_strict_iso;
	disable_filtering = currentConfig->lame_disable_filtering;
	set_lowpass = currentConfig->lame_set_lowpass;
	set_lowpass_width = currentConfig->lame_set_lowpass_width;
	set_highpass = currentConfig->lame_set_highpass;
	set_highpass_width = currentConfig->lame_set_highpass_width;

	mainWnd			= new SMOOTHWindow("LAME MP3 encoder configuration");
	mainWnd_titlebar	= new SMOOTHTitlebar(false, false, true);
	mainWnd_layer		= new SMOOTHLayer();

	register_layer_basic	= new SMOOTHLayer("Basic");
	register_layer_vbr	= new SMOOTHLayer("VBR");
	register_layer_misc	= new SMOOTHLayer("Misc");
	register_layer_expert	= new SMOOTHLayer("Expert");
	register_layer_filtering= new SMOOTHLayer("Audio processing");

	pos.x = 175;
	pos.y = 29;
	size.cx = 0;
	size.cy = 0;

	btn_cancel		= new SMOOTHButton("Cancel", NIL, pos, size, SMOOTHProc(configureLameEnc, this, Cancel));
	btn_cancel->SetOrientation(OR_LOWERRIGHT);

	pos.x -= 88;

	btn_ok			= new SMOOTHButton("OK", NIL, pos, size, SMOOTHProc(configureLameEnc, this, OK));
	btn_ok->SetOrientation(OR_LOWERRIGHT);

	pos.x = 7;
	pos.y = 7;
	size.cx = 384;
	size.cy = 221;

	reg_register		= new SMOOTHTabRegister(pos, size);

	pos.x = 7;
	pos.y = 11;
	size.cx = 232;
	size.cy = 63;

	basic_bitrate		= new SMOOTHGroupBox("Bitrate", pos, size);
	if (vbrmode != vbr_off) basic_bitrate->Deactivate();

	pos.x += 10;
	pos.y += 11;
	size.cx = 76;
	size.cy = 0;

	basic_option_set_bitrate= new SMOOTHOptionBox("Set bitrate:", pos, size, &set_bitrate, 1, SMOOTHProc(configureLameEnc, this, SetBitrateOption));
	if (vbrmode != vbr_off) basic_option_set_bitrate->Deactivate();

	pos.y += 25;

	basic_option_set_ratio	= new SMOOTHOptionBox("Set ratio:", pos, size, &set_bitrate, 0, SMOOTHProc(configureLameEnc, this, SetBitrateOption));
	if (vbrmode != vbr_off) basic_option_set_ratio->Deactivate();

	pos.y -= 25;
	pos.x += 85;

	basic_slider_bitrate	= new SMOOTHSlider(pos, size, OR_HORZ, &bitrate, 0, 17, SMOOTHProc(configureLameEnc, this, SetBitrate));
	if (!set_bitrate) basic_slider_bitrate->Deactivate();
	if (vbrmode != vbr_off) basic_slider_bitrate->Deactivate();

	pos.x += 83;
	pos.y += 2;

	basic_text_bitrate	= new SMOOTHText("", pos);
	SetBitrate();
	if (!set_bitrate) basic_text_bitrate->Deactivate();
	if (vbrmode != vbr_off) basic_text_bitrate->Deactivate();

	pos.x -= 83;
	pos.y += 22;
	size.cx = 34;

	basic_edit_ratio	= new SMOOTHEditBox(SMOOTHString::DoubleToString(((double) ratio) / 100), pos, size, EDB_NUMERIC, 5, NULLPROC);
	if (set_bitrate) basic_edit_ratio->Deactivate();
	if (vbrmode != vbr_off) basic_edit_ratio->Deactivate();

	pos.x = 7;
	pos.y = 86;
	size.cx = 232;
	size.cy = 51;

	basic_quality		= new SMOOTHGroupBox("Quality", pos, size);

	pos.x += 10;
	pos.y += 11;
	size.cx = 76;
	size.cy = 0;

	basic_check_set_quality	= new SMOOTHCheckBox("Set quality:", pos, size, &set_quality, SMOOTHProc(configureLameEnc, this, SetQualityOption));

	pos.x += 85;
	size.cx += 38;

	basic_slider_quality	= new SMOOTHSlider(pos, size, OR_HORZ, &quality, 0, 9, SMOOTHProc(configureLameEnc, this, SetQuality));
	if (!set_quality) basic_slider_quality->Deactivate();

	pos.x += 121;
	pos.y += 2;

	basic_text_quality	= new SMOOTHText("", pos);
	SetQuality();
	if (!set_quality) basic_text_quality->Deactivate();

	pos.x -= 132;
	pos.y += 17;

	basic_text_quality_worse= new SMOOTHText("worse", pos);
	if (!set_quality) basic_text_quality_worse->Deactivate();

	pos.x += 107;

	basic_text_quality_better= new SMOOTHText("better", pos);
	if (!set_quality) basic_text_quality_better->Deactivate();

	pos.x = 247;
	pos.y = 11;
	size.cx = 127;
	size.cy = 126;

	basic_stereomode	= new SMOOTHGroupBox("Stereo mode", pos, size);

	pos.x += 10;
	pos.y += 11;
	size.cx = 106;
	size.cy = 0;

	basic_option_autostereo	= new SMOOTHOptionBox("Auto", pos, size, &stereomode, 0, SMOOTHProc(configureLameEnc, this, SetStereoMode));

	pos.y += 25;

	basic_option_stereo	= new SMOOTHOptionBox("Stereo", pos, size, &stereomode, 1, SMOOTHProc(configureLameEnc, this, SetStereoMode));

	pos.y += 25;

	basic_option_jstereo	= new SMOOTHOptionBox("Joint Stereo", pos, size, &stereomode, 2, SMOOTHProc(configureLameEnc, this, SetStereoMode));

	pos.y += 31;

	basic_check_forcejs	= new SMOOTHCheckBox("Force Joint Stereo", pos, size, &forcejs, NULLPROC);
	if (stereomode != 2) basic_check_forcejs->Deactivate();

	pos.x = 7;
	pos.y = 11;
	size.cx = 127;
	size.cy = 106;

	vbr_vbrmode		= new SMOOTHGroupBox("VBR mode", pos, size);

	pos.x += 10;
	pos.y += 11;
	size.cx = 107;
	size.cy = 0;

	vbr_option_cbr		= new SMOOTHOptionBox("CBR (no VBR)", pos, size, &vbrmode, vbr_off, SMOOTHProc(configureLameEnc, this, SetVBRMode));

	pos.y += 23;

	vbr_option_abr		= new SMOOTHOptionBox("ABR", pos, size, &vbrmode, vbr_abr, SMOOTHProc(configureLameEnc, this, SetVBRMode));

	pos.y += 23;

	vbr_option_vbrrh	= new SMOOTHOptionBox("VBR rh", pos, size, &vbrmode, vbr_rh, SMOOTHProc(configureLameEnc, this, SetVBRMode));

	pos.y += 23;

	vbr_option_vbrmtrh	= new SMOOTHOptionBox("VBR mtrh", pos, size, &vbrmode, vbr_mtrh, SMOOTHProc(configureLameEnc, this, SetVBRMode));

	pos.x = 142;
	pos.y = 11;
	size.cx = 232;
	size.cy = 51;

	vbr_quality		= new SMOOTHGroupBox("VBR quality", pos, size);
	if (vbrmode != vbr_rh && vbrmode != vbr_mtrh) vbr_quality->Deactivate();

	pos.x += 11;
	pos.y += 13;

	vbr_text_setquality	= new SMOOTHText("Quality:", pos);
	if (vbrmode != vbr_rh && vbrmode != vbr_mtrh) vbr_text_setquality->Deactivate();

	pos.x += 42;
	pos.y -= 2;
	size.cx = 157;
	size.cy = 0;

	vbr_slider_quality	= new SMOOTHSlider(pos, size, OR_HORZ, &vbrquality, 0, 9, SMOOTHProc(configureLameEnc, this, SetVBRQuality));
	if (vbrmode != vbr_rh && vbrmode != vbr_mtrh) vbr_slider_quality->Deactivate();

	pos.x += 164;
	pos.y += 2;

	vbr_text_quality	= new SMOOTHText("", pos);
	SetVBRQuality();
	if (vbrmode != vbr_rh && vbrmode != vbr_mtrh) vbr_text_quality->Deactivate();

	pos.x -= 175;
	pos.y += 17;

	vbr_text_quality_worse= new SMOOTHText("worse", pos);
	if (vbrmode != vbr_rh && vbrmode != vbr_mtrh) vbr_text_quality_worse->Deactivate();

	pos.x += 150;

	vbr_text_quality_better= new SMOOTHText("better", pos);
	if (vbrmode != vbr_rh && vbrmode != vbr_mtrh) vbr_text_quality_better->Deactivate();

	pos.x = 142;
	pos.y = 74;
	size.cx = 232;
	size.cy = 43;

	vbr_abrbitrate		= new SMOOTHGroupBox("ABR target bitrate", pos, size);
	if (vbrmode != vbr_abr) vbr_abrbitrate->Deactivate();

	pos.x += 10;
	pos.y += 13;
	size.cx = 146;
	size.cy = 0;

	vbr_slider_abrbitrate	= new SMOOTHSlider(pos, size, OR_HORZ, &abrbitrate, 8, 310, SMOOTHProc(configureLameEnc, this, SetABRBitrate));
	if (vbrmode != vbr_abr) vbr_slider_abrbitrate->Deactivate();

	pos.x += 154;
	pos.y -= 1;
	size.cx = 25;

	vbr_edit_abrbitrate	= new SMOOTHEditBox("", pos, size, EDB_NUMERIC, 3, SMOOTHProc(configureLameEnc, this, SetABRBitrateByEditBox));
	if (vbrmode != vbr_abr) vbr_edit_abrbitrate->Deactivate();
	SetABRBitrate();

	pos.x += 32;
	pos.y += 3;

	vbr_text_abrbitrate_kbps= new SMOOTHText("kbps", pos);
	if (vbrmode != vbr_abr) vbr_text_abrbitrate_kbps->Deactivate();

	pos.x = 7;
	pos.y = 129;
	size.cx = 367;
	size.cy = 63;

	vbr_bitrate		= new SMOOTHGroupBox("VBR bitrate range", pos, size);
	if (vbrmode == vbr_off) vbr_bitrate->Deactivate();

	pos.x += 10;
	pos.y += 11;
	size.cx = 146;
	size.cy = 0;

	vbr_check_set_min_brate	= new SMOOTHCheckBox("Set minimum VBR bitrate:", pos, size, &set_min_vbr_brate, SMOOTHProc(configureLameEnc, this, SetMinVBRBitrateOption));
	if (vbrmode == vbr_off) vbr_check_set_min_brate->Deactivate();

	pos.x += 155;
	size.cx = 138;

	vbr_slider_min_brate	= new SMOOTHSlider(pos, size, OR_HORZ, &min_vbr_brate, 0, 17, SMOOTHProc(configureLameEnc, this, SetMinVBRBitrate));
	if (vbrmode == vbr_off) vbr_slider_min_brate->Deactivate();
	if (!set_min_vbr_brate) vbr_slider_min_brate->Deactivate();

	pos.x += 145;
	pos.y += 2;

	vbr_text_min_brate_kbps	= new SMOOTHText("kbps", pos);
	SetMinVBRBitrate();
	if (vbrmode == vbr_off) vbr_text_min_brate_kbps->Deactivate();
	if (!set_min_vbr_brate) vbr_text_min_brate_kbps->Deactivate();

	pos.x -= 300;
	pos.y += 23;
	size.cx = 146;

	vbr_check_set_max_brate	= new SMOOTHCheckBox("Set maximum VBR bitrate:", pos, size, &set_max_vbr_brate, SMOOTHProc(configureLameEnc, this, SetMaxVBRBitrateOption));
	if (vbrmode == vbr_off) vbr_check_set_max_brate->Deactivate();

	pos.x += 155;
	size.cx = 138;

	vbr_slider_max_brate	= new SMOOTHSlider(pos, size, OR_HORZ, &max_vbr_brate, 0, 17, SMOOTHProc(configureLameEnc, this, SetMaxVBRBitrate));
	if (vbrmode == vbr_off) vbr_slider_max_brate->Deactivate();
	if (!set_max_vbr_brate) vbr_slider_max_brate->Deactivate();

	pos.x += 145;
	pos.y += 2;

	vbr_text_max_brate_kbps	= new SMOOTHText("", pos);
	SetMaxVBRBitrate();
	if (vbrmode == vbr_off) vbr_text_max_brate_kbps->Deactivate();
	if (!set_max_vbr_brate) vbr_text_max_brate_kbps->Deactivate();

	pos.x = 7;
	pos.y = 11;
	size.cx = 138;
	size.cy = 89;

	misc_bits		= new SMOOTHGroupBox("Control bits", pos, size);

	pos.x += 10;
	pos.y += 11;
	size.cx = 117;
	size.cy = 0;

	misc_check_copyright	= new SMOOTHCheckBox("Set Copyright bit", pos, size, &set_copyright, NULLPROC);

	pos.y += 25;

	misc_check_original	= new SMOOTHCheckBox("Set Original bit", pos, size, &set_original, NULLPROC);

	pos.y += 25;

	misc_check_private	= new SMOOTHCheckBox("Set Private bit", pos, size, &set_private, NULLPROC);

	pos.x = 7;
	pos.y = 112;
	size.cx = 138;
	size.cy = 39;

	misc_crc		= new SMOOTHGroupBox("CRC", pos, size);

	pos.x += 10;
	pos.y += 11;
	size.cx = 117;
	size.cy = 0;

	misc_check_crc		= new SMOOTHCheckBox("Enable CRC", pos, size, &set_crc, NULLPROC);

	pos.x = 153;
	pos.y = 11;
	size.cx = 221;
	size.cy = 39;

	misc_format		= new SMOOTHGroupBox("Stream format", pos, size);

	pos.x += 10;
	pos.y += 11;
	size.cx = 200;
	size.cy = 0;

	misc_check_iso		= new SMOOTHCheckBox("Enforce strict ISO compliance", pos, size, &set_iso, NULLPROC);

	pos.x = 153;
	pos.y = 61;
	size.cx = 221;
	size.cy = 39;

	misc_padding		= new SMOOTHGroupBox("Padding", pos, size);

	pos.x += 9;
	pos.y += 13;

	misc_text_padding	= new SMOOTHText("Set padding type:", pos);

	pos.x += 90;
	pos.y -= 3;
	size.cx = 112;
	size.cy = 0;

	misc_combo_padding	= new SMOOTHComboBox(pos, size, NULLPROC);
	misc_combo_padding->AddEntry("pad no frames", NULLPROC);
	misc_combo_padding->AddEntry("pad all frames", NULLPROC);
	misc_combo_padding->AddEntry("adjust padding", NULLPROC);
	misc_combo_padding->SelectEntry(currentConfig->lame_padding_type);

	pos.x = 7;
	pos.y = 11;
	size.cx = 138;
	size.cy = 39;

	filtering_resample	= new SMOOTHGroupBox("Output sampling rate", pos, size);

	pos.x += 10;
	pos.y += 10;
	size.cx = 118;
	size.cy = 0;

	filtering_combo_resample= new SMOOTHComboBox(pos, size, NULLPROC);
	filtering_combo_resample->AddEntry("no resampling", NULLPROC);
	filtering_combo_resample->AddEntry("8 kHz", NULLPROC);
	filtering_combo_resample->AddEntry("11.025 kHz", NULLPROC);
	filtering_combo_resample->AddEntry("12 kHz", NULLPROC);
	filtering_combo_resample->AddEntry("16 kHz", NULLPROC);
	filtering_combo_resample->AddEntry("22.05 kHz", NULLPROC);
	filtering_combo_resample->AddEntry("24 kHz", NULLPROC);
	filtering_combo_resample->AddEntry("32 kHz", NULLPROC);
	filtering_combo_resample->AddEntry("44.1 kHz", NULLPROC);
	filtering_combo_resample->AddEntry("48 kHz", NULLPROC);

	if (currentConfig->lame_resample == 8000)	filtering_combo_resample->SelectEntry(1);
	else if (currentConfig->lame_resample == 11025)	filtering_combo_resample->SelectEntry(2);
	else if (currentConfig->lame_resample == 12000)	filtering_combo_resample->SelectEntry(3);
	else if (currentConfig->lame_resample == 16000)	filtering_combo_resample->SelectEntry(4);
	else if (currentConfig->lame_resample == 22050)	filtering_combo_resample->SelectEntry(5);
	else if (currentConfig->lame_resample == 24000)	filtering_combo_resample->SelectEntry(6);
	else if (currentConfig->lame_resample == 32000)	filtering_combo_resample->SelectEntry(7);
	else if (currentConfig->lame_resample == 44100)	filtering_combo_resample->SelectEntry(8);
	else if (currentConfig->lame_resample == 48000)	filtering_combo_resample->SelectEntry(9);

	pos.x = 153;
	pos.y = 11;
	size.cx = 221;
	size.cy = 64;

	filtering_highpass	= new SMOOTHGroupBox("Highpass filter", pos, size);
	if (disable_filtering) filtering_highpass->Deactivate();

	pos.x += 10;
	pos.y += 11;
	size.cx = 155;
	size.cy = 0;

	filtering_set_highpass	= new SMOOTHCheckBox("Set Highpass frequency (Hz):", pos, size, &set_highpass, SMOOTHProc(configureLameEnc, this, SetHighpass));
	if (disable_filtering) filtering_set_highpass->Deactivate();

	pos.x += 164;
	pos.y -= 1;
	size.cx = 37;

	filtering_edit_highpass	= new SMOOTHEditBox(SMOOTHString::IntToString(currentConfig->lame_highpass), pos, size, EDB_NUMERIC, 5, NULLPROC);
	if (!set_highpass || disable_filtering) filtering_edit_highpass->Deactivate();

	pos.x -= 164;
	pos.y += 26;
	size.cx = 155;

	filtering_set_highpass_width= new SMOOTHCheckBox("Set Highpass width (Hz):", pos, size, &set_highpass_width, SMOOTHProc(configureLameEnc, this, SetHighpassWidth));
	if (!set_highpass || disable_filtering) filtering_set_highpass_width->Deactivate();

	pos.x += 164;
	pos.y -= 1;
	size.cx = 37;

	filtering_edit_highpass_width= new SMOOTHEditBox(SMOOTHString::IntToString(currentConfig->lame_highpass_width), pos, size, EDB_NUMERIC, 5, NULLPROC);
	if (!set_highpass_width || !set_highpass || disable_filtering) filtering_edit_highpass_width->Deactivate();

	pos.x = 153;
	pos.y = 87;
	size.cx = 221;
	size.cy = 64;

	filtering_lowpass	= new SMOOTHGroupBox("Lowpass filter", pos, size);
	if (disable_filtering) filtering_lowpass->Deactivate();

	pos.x += 10;
	pos.y += 11;
	size.cx = 155;
	size.cy = 0;

	filtering_set_lowpass	= new SMOOTHCheckBox("Set Lowpass frequency (Hz):", pos, size, &set_lowpass, SMOOTHProc(configureLameEnc, this, SetLowpass));
	if (disable_filtering) filtering_set_lowpass->Deactivate();

	pos.x += 164;
	pos.y -= 1;
	size.cx = 37;

	filtering_edit_lowpass	= new SMOOTHEditBox(SMOOTHString::IntToString(currentConfig->lame_lowpass), pos, size, EDB_NUMERIC, 5, NULLPROC);
	if (!set_lowpass || disable_filtering) filtering_edit_lowpass->Deactivate();

	pos.x -= 164;
	pos.y += 26;
	size.cx = 155;

	filtering_set_lowpass_width= new SMOOTHCheckBox("Set Lowpass width (Hz):", pos, size, &set_lowpass_width, SMOOTHProc(configureLameEnc, this, SetLowpassWidth));
	if (!set_lowpass || disable_filtering) filtering_set_lowpass_width->Deactivate();

	pos.x += 164;
	pos.y -= 1;
	size.cx = 37;

	filtering_edit_lowpass_width= new SMOOTHEditBox(SMOOTHString::IntToString(currentConfig->lame_lowpass_width), pos, size, EDB_NUMERIC, 5, NULLPROC);
	if (!set_lowpass_width || !set_lowpass || disable_filtering) filtering_edit_lowpass_width->Deactivate();

	pos.x = 7;
	pos.y = 62;
	size.cx = 138;
	size.cy = 39;

	filtering_misc		= new SMOOTHGroupBox("Misc settings", pos, size);

	pos.x += 10;
	pos.y += 11;
	size.cx = 117;
	size.cy = 0;

	filtering_check_disable_all= new SMOOTHCheckBox("Disable all filtering", pos, size, &disable_filtering, SMOOTHProc(configureLameEnc, this, SetDisableFiltering));

	RegisterObject(mainWnd);

	mainWnd->RegisterObject(mainWnd_titlebar);
	mainWnd->RegisterObject(mainWnd_layer);

	mainWnd_layer->RegisterObject(btn_ok);
	mainWnd_layer->RegisterObject(btn_cancel);
	mainWnd_layer->RegisterObject(reg_register);

	reg_register->RegisterObject(register_layer_basic);
	reg_register->RegisterObject(register_layer_vbr);
	reg_register->RegisterObject(register_layer_misc);
//	reg_register->RegisterObject(register_layer_expert);
	reg_register->RegisterObject(register_layer_filtering);

	register_layer_basic->RegisterObject(basic_bitrate);
	register_layer_basic->RegisterObject(basic_option_set_bitrate);
	register_layer_basic->RegisterObject(basic_option_set_ratio);
	register_layer_basic->RegisterObject(basic_slider_bitrate);
	register_layer_basic->RegisterObject(basic_text_bitrate);
	register_layer_basic->RegisterObject(basic_edit_ratio);

	register_layer_basic->RegisterObject(basic_quality);
	register_layer_basic->RegisterObject(basic_check_set_quality);
	register_layer_basic->RegisterObject(basic_slider_quality);
	register_layer_basic->RegisterObject(basic_text_quality);
	register_layer_basic->RegisterObject(basic_text_quality_worse);
	register_layer_basic->RegisterObject(basic_text_quality_better);

	register_layer_basic->RegisterObject(basic_stereomode);
	register_layer_basic->RegisterObject(basic_option_autostereo);
	register_layer_basic->RegisterObject(basic_option_stereo);
	register_layer_basic->RegisterObject(basic_option_jstereo);
	register_layer_basic->RegisterObject(basic_check_forcejs);

	register_layer_vbr->RegisterObject(vbr_vbrmode);
	register_layer_vbr->RegisterObject(vbr_option_cbr);
	register_layer_vbr->RegisterObject(vbr_option_abr);
	register_layer_vbr->RegisterObject(vbr_option_vbrrh);
	register_layer_vbr->RegisterObject(vbr_option_vbrmtrh);

	register_layer_vbr->RegisterObject(vbr_quality);
	register_layer_vbr->RegisterObject(vbr_text_setquality);
	register_layer_vbr->RegisterObject(vbr_slider_quality);
	register_layer_vbr->RegisterObject(vbr_text_quality);
	register_layer_vbr->RegisterObject(vbr_text_quality_worse);
	register_layer_vbr->RegisterObject(vbr_text_quality_better);

	register_layer_vbr->RegisterObject(vbr_abrbitrate);
	register_layer_vbr->RegisterObject(vbr_slider_abrbitrate);
	register_layer_vbr->RegisterObject(vbr_edit_abrbitrate);
	register_layer_vbr->RegisterObject(vbr_text_abrbitrate_kbps);

	register_layer_vbr->RegisterObject(vbr_bitrate);
	register_layer_vbr->RegisterObject(vbr_check_set_min_brate);
	register_layer_vbr->RegisterObject(vbr_check_set_max_brate);
	register_layer_vbr->RegisterObject(vbr_slider_min_brate);
	register_layer_vbr->RegisterObject(vbr_slider_max_brate);
	register_layer_vbr->RegisterObject(vbr_text_min_brate_kbps);
	register_layer_vbr->RegisterObject(vbr_text_max_brate_kbps);

	register_layer_misc->RegisterObject(misc_bits);
	register_layer_misc->RegisterObject(misc_check_original);
	register_layer_misc->RegisterObject(misc_check_copyright);
	register_layer_misc->RegisterObject(misc_check_private);

	register_layer_misc->RegisterObject(misc_crc);
	register_layer_misc->RegisterObject(misc_check_crc);

	register_layer_misc->RegisterObject(misc_format);
	register_layer_misc->RegisterObject(misc_check_iso);

	register_layer_misc->RegisterObject(misc_padding);
	register_layer_misc->RegisterObject(misc_text_padding);
	register_layer_misc->RegisterObject(misc_combo_padding);

	register_layer_filtering->RegisterObject(filtering_resample);
	register_layer_filtering->RegisterObject(filtering_combo_resample);

	register_layer_filtering->RegisterObject(filtering_lowpass);
	register_layer_filtering->RegisterObject(filtering_set_lowpass);
	register_layer_filtering->RegisterObject(filtering_edit_lowpass);
	register_layer_filtering->RegisterObject(filtering_set_lowpass_width);
	register_layer_filtering->RegisterObject(filtering_edit_lowpass_width);

	register_layer_filtering->RegisterObject(filtering_highpass);
	register_layer_filtering->RegisterObject(filtering_set_highpass);
	register_layer_filtering->RegisterObject(filtering_edit_highpass);
	register_layer_filtering->RegisterObject(filtering_set_highpass_width);
	register_layer_filtering->RegisterObject(filtering_edit_highpass_width);

	register_layer_filtering->RegisterObject(filtering_misc);
	register_layer_filtering->RegisterObject(filtering_check_disable_all);

	mainWnd->SetExStyle(WS_EX_TOOLWINDOW);
	mainWnd->SetIcon(SMOOTH::LoadImage("bonkenc.pci", 0, NIL));
	mainWnd->SetApplicationIcon(IDI_ICON);
	mainWnd->SetMetrics(SMOOTHPoint(120, 120), SMOOTHSize(405, 297));
}

configureLameEnc::~configureLameEnc()
{
	register_layer_basic->UnregisterObject(basic_bitrate);
	register_layer_basic->UnregisterObject(basic_option_set_bitrate);
	register_layer_basic->UnregisterObject(basic_option_set_ratio);
	register_layer_basic->UnregisterObject(basic_slider_bitrate);
	register_layer_basic->UnregisterObject(basic_text_bitrate);
	register_layer_basic->UnregisterObject(basic_edit_ratio);

	register_layer_basic->UnregisterObject(basic_quality);
	register_layer_basic->UnregisterObject(basic_check_set_quality);
	register_layer_basic->UnregisterObject(basic_slider_quality);
	register_layer_basic->UnregisterObject(basic_text_quality);
	register_layer_basic->UnregisterObject(basic_text_quality_worse);
	register_layer_basic->UnregisterObject(basic_text_quality_better);

	register_layer_basic->UnregisterObject(basic_stereomode);
	register_layer_basic->UnregisterObject(basic_option_autostereo);
	register_layer_basic->UnregisterObject(basic_option_stereo);
	register_layer_basic->UnregisterObject(basic_option_jstereo);
	register_layer_basic->UnregisterObject(basic_check_forcejs);

	register_layer_vbr->UnregisterObject(vbr_vbrmode);
	register_layer_vbr->UnregisterObject(vbr_option_cbr);
	register_layer_vbr->UnregisterObject(vbr_option_abr);
	register_layer_vbr->UnregisterObject(vbr_option_vbrrh);
	register_layer_vbr->UnregisterObject(vbr_option_vbrmtrh);

	register_layer_vbr->UnregisterObject(vbr_quality);
	register_layer_vbr->UnregisterObject(vbr_text_setquality);
	register_layer_vbr->UnregisterObject(vbr_slider_quality);
	register_layer_vbr->UnregisterObject(vbr_text_quality);
	register_layer_vbr->UnregisterObject(vbr_text_quality_worse);
	register_layer_vbr->UnregisterObject(vbr_text_quality_better);

	register_layer_vbr->UnregisterObject(vbr_abrbitrate);
	register_layer_vbr->UnregisterObject(vbr_slider_abrbitrate);
	register_layer_vbr->UnregisterObject(vbr_edit_abrbitrate);
	register_layer_vbr->UnregisterObject(vbr_text_abrbitrate_kbps);

	register_layer_vbr->UnregisterObject(vbr_bitrate);
	register_layer_vbr->UnregisterObject(vbr_check_set_min_brate);
	register_layer_vbr->UnregisterObject(vbr_check_set_max_brate);
	register_layer_vbr->UnregisterObject(vbr_slider_min_brate);
	register_layer_vbr->UnregisterObject(vbr_slider_max_brate);
	register_layer_vbr->UnregisterObject(vbr_text_min_brate_kbps);
	register_layer_vbr->UnregisterObject(vbr_text_max_brate_kbps);

	register_layer_misc->UnregisterObject(misc_bits);
	register_layer_misc->UnregisterObject(misc_check_original);
	register_layer_misc->UnregisterObject(misc_check_copyright);
	register_layer_misc->UnregisterObject(misc_check_private);

	register_layer_misc->UnregisterObject(misc_crc);
	register_layer_misc->UnregisterObject(misc_check_crc);

	register_layer_misc->UnregisterObject(misc_format);
	register_layer_misc->UnregisterObject(misc_check_iso);

	register_layer_misc->UnregisterObject(misc_padding);
	register_layer_misc->UnregisterObject(misc_text_padding);
	register_layer_misc->UnregisterObject(misc_combo_padding);

	register_layer_filtering->UnregisterObject(filtering_resample);
	register_layer_filtering->UnregisterObject(filtering_combo_resample);

	register_layer_filtering->UnregisterObject(filtering_lowpass);
	register_layer_filtering->UnregisterObject(filtering_set_lowpass);
	register_layer_filtering->UnregisterObject(filtering_edit_lowpass);
	register_layer_filtering->UnregisterObject(filtering_set_lowpass_width);
	register_layer_filtering->UnregisterObject(filtering_edit_lowpass_width);

	register_layer_filtering->UnregisterObject(filtering_highpass);
	register_layer_filtering->UnregisterObject(filtering_set_highpass);
	register_layer_filtering->UnregisterObject(filtering_edit_highpass);
	register_layer_filtering->UnregisterObject(filtering_set_highpass_width);
	register_layer_filtering->UnregisterObject(filtering_edit_highpass_width);

	register_layer_filtering->UnregisterObject(filtering_misc);
	register_layer_filtering->UnregisterObject(filtering_check_disable_all);

	reg_register->UnregisterObject(register_layer_basic);
	reg_register->UnregisterObject(register_layer_vbr);
	reg_register->UnregisterObject(register_layer_misc);
//	reg_register->UnregisterObject(register_layer_expert);
	reg_register->UnregisterObject(register_layer_filtering);

	mainWnd_layer->UnregisterObject(btn_ok);
	mainWnd_layer->UnregisterObject(btn_cancel);
	mainWnd_layer->UnregisterObject(reg_register);

	mainWnd->UnregisterObject(mainWnd_titlebar);
	mainWnd->UnregisterObject(mainWnd_layer);

	UnregisterObject(mainWnd);

	SMOOTH::DeleteObject(mainWnd_titlebar);
	SMOOTH::DeleteObject(mainWnd_layer);
	SMOOTH::DeleteObject(mainWnd);
	SMOOTH::DeleteObject(btn_ok);
	SMOOTH::DeleteObject(btn_cancel);
	SMOOTH::DeleteObject(reg_register);
	SMOOTH::DeleteObject(register_layer_basic);
	SMOOTH::DeleteObject(register_layer_vbr);
	SMOOTH::DeleteObject(register_layer_misc);
	SMOOTH::DeleteObject(register_layer_expert);
	SMOOTH::DeleteObject(register_layer_filtering);
	SMOOTH::DeleteObject(basic_bitrate);
	SMOOTH::DeleteObject(basic_option_set_bitrate);
	SMOOTH::DeleteObject(basic_option_set_ratio);
	SMOOTH::DeleteObject(basic_slider_bitrate);
	SMOOTH::DeleteObject(basic_text_bitrate);
	SMOOTH::DeleteObject(basic_edit_ratio);
	SMOOTH::DeleteObject(basic_quality);
	SMOOTH::DeleteObject(basic_check_set_quality);
	SMOOTH::DeleteObject(basic_slider_quality);
	SMOOTH::DeleteObject(basic_text_quality);
	SMOOTH::DeleteObject(basic_text_quality_worse);
	SMOOTH::DeleteObject(basic_text_quality_better);
	SMOOTH::DeleteObject(basic_stereomode);
	SMOOTH::DeleteObject(basic_option_autostereo);
	SMOOTH::DeleteObject(basic_option_stereo);
	SMOOTH::DeleteObject(basic_option_jstereo);
	SMOOTH::DeleteObject(basic_check_forcejs);
	SMOOTH::DeleteObject(vbr_vbrmode);
	SMOOTH::DeleteObject(vbr_option_cbr);
	SMOOTH::DeleteObject(vbr_option_abr);
	SMOOTH::DeleteObject(vbr_option_vbrrh);
	SMOOTH::DeleteObject(vbr_option_vbrmtrh);
	SMOOTH::DeleteObject(vbr_quality);
	SMOOTH::DeleteObject(vbr_text_setquality);
	SMOOTH::DeleteObject(vbr_slider_quality);
	SMOOTH::DeleteObject(vbr_text_quality);
	SMOOTH::DeleteObject(vbr_text_quality_worse);
	SMOOTH::DeleteObject(vbr_text_quality_better);
	SMOOTH::DeleteObject(vbr_abrbitrate);
	SMOOTH::DeleteObject(vbr_slider_abrbitrate);
	SMOOTH::DeleteObject(vbr_edit_abrbitrate);
	SMOOTH::DeleteObject(vbr_text_abrbitrate_kbps);
	SMOOTH::DeleteObject(vbr_bitrate);
	SMOOTH::DeleteObject(vbr_check_set_min_brate);
	SMOOTH::DeleteObject(vbr_check_set_max_brate);
	SMOOTH::DeleteObject(vbr_slider_min_brate);
	SMOOTH::DeleteObject(vbr_slider_max_brate);
	SMOOTH::DeleteObject(vbr_text_min_brate_kbps);
	SMOOTH::DeleteObject(vbr_text_max_brate_kbps);
	SMOOTH::DeleteObject(misc_bits);
	SMOOTH::DeleteObject(misc_check_original);
	SMOOTH::DeleteObject(misc_check_copyright);
	SMOOTH::DeleteObject(misc_check_private);
	SMOOTH::DeleteObject(misc_crc);
	SMOOTH::DeleteObject(misc_check_crc);
	SMOOTH::DeleteObject(misc_format);
	SMOOTH::DeleteObject(misc_check_iso);
	SMOOTH::DeleteObject(misc_padding);
	SMOOTH::DeleteObject(misc_text_padding);
	SMOOTH::DeleteObject(misc_combo_padding);
	SMOOTH::DeleteObject(filtering_resample);
	SMOOTH::DeleteObject(filtering_combo_resample);
	SMOOTH::DeleteObject(filtering_lowpass);
	SMOOTH::DeleteObject(filtering_set_lowpass);
	SMOOTH::DeleteObject(filtering_edit_lowpass);
	SMOOTH::DeleteObject(filtering_set_lowpass_width);
	SMOOTH::DeleteObject(filtering_edit_lowpass_width);
	SMOOTH::DeleteObject(filtering_highpass);
	SMOOTH::DeleteObject(filtering_set_highpass);
	SMOOTH::DeleteObject(filtering_edit_highpass);
	SMOOTH::DeleteObject(filtering_set_highpass_width);
	SMOOTH::DeleteObject(filtering_edit_highpass_width);
	SMOOTH::DeleteObject(filtering_misc);
	SMOOTH::DeleteObject(filtering_check_disable_all);
}

SMOOTHInt configureLameEnc::ShowDialog()
{
	mainWnd->Stay();

	return mainWnd->value;
}

SMOOTHVoid configureLameEnc::OK()
{
	if (abrbitrate < 8)	abrbitrate = 8;
	if (abrbitrate > 310)	abrbitrate = 310;

	if (set_lowpass && filtering_edit_lowpass->GetText().Length() == 0)
	{
		SMOOTH::MessageBox("Please enter a frequency for the Lowpass filter!", "Error", MB_OK, IDI_HAND);

		return;
	}

	if (set_lowpass && set_lowpass_width && filtering_edit_lowpass_width->GetText().Length() == 0)
	{
		SMOOTH::MessageBox("Please enter a frequency for the Lowpass filter width!", "Error", MB_OK, IDI_HAND);

		return;
	}

	if (set_highpass && filtering_edit_highpass->GetText().Length() == 0)
	{
		SMOOTH::MessageBox("Please enter a frequency for the Highpass filter!", "Error", MB_OK, IDI_HAND);

		return;
	}

	if (set_highpass && set_highpass_width && filtering_edit_highpass_width->GetText().Length() == 0)
	{
		SMOOTH::MessageBox("Please enter a frequency for the Highpass filter width!", "Error", MB_OK, IDI_HAND);

		return;
	}

	if (set_highpass && set_lowpass && filtering_edit_lowpass->GetText().ToInt() != 0 && filtering_edit_highpass->GetText().ToInt() != 0 && (filtering_edit_lowpass->GetText().ToInt() < filtering_edit_highpass->GetText().ToInt()))
	{
		SMOOTH::MessageBox("Lowpass frequency is lower than Highpass frequency!", "Error", MB_OK, IDI_HAND);

		return;
	}

	currentConfig->lame_set_bitrate = set_bitrate;
	currentConfig->lame_bitrate = GetBitrate();
	currentConfig->lame_ratio = (int) (basic_edit_ratio->GetText().ToDouble() * 100);
	currentConfig->lame_set_quality = set_quality;
	currentConfig->lame_quality = 9 - quality;
	currentConfig->lame_stereomode = stereomode;
	currentConfig->lame_forcejs = forcejs;
	currentConfig->lame_vbrmode = vbrmode;
	currentConfig->lame_vbrquality = 9 - vbrquality;
	currentConfig->lame_abrbitrate = abrbitrate;
	currentConfig->lame_set_min_vbr_bitrate = set_min_vbr_brate;
	currentConfig->lame_min_vbr_bitrate = GetMinVBRBitrate();
	currentConfig->lame_set_max_vbr_bitrate = set_max_vbr_brate;
	currentConfig->lame_max_vbr_bitrate = GetMaxVBRBitrate();
	currentConfig->lame_copyright = set_copyright;
	currentConfig->lame_original = set_original;
	currentConfig->lame_private = set_private;
	currentConfig->lame_crc = set_crc;
	currentConfig->lame_strict_iso = set_iso;
	currentConfig->lame_padding_type = misc_combo_padding->GetSelectedEntry();
	currentConfig->lame_disable_filtering = disable_filtering;
	currentConfig->lame_set_lowpass = set_lowpass;
	currentConfig->lame_set_lowpass_width = set_lowpass_width;
	currentConfig->lame_set_highpass = set_highpass;
	currentConfig->lame_set_highpass_width = set_highpass_width;
	currentConfig->lame_lowpass = filtering_edit_lowpass->GetText().ToInt();
	currentConfig->lame_lowpass_width = filtering_edit_lowpass_width->GetText().ToInt();
	currentConfig->lame_highpass = filtering_edit_highpass->GetText().ToInt();
	currentConfig->lame_highpass_width = filtering_edit_highpass_width->GetText().ToInt();

	switch (filtering_combo_resample->GetSelectedEntry())
	{
		case 0:
			currentConfig->lame_resample = 0;
			break;
		case 1:
			currentConfig->lame_resample = 8000;
			break;
		case 2:
			currentConfig->lame_resample = 11025;
			break;
		case 3:
			currentConfig->lame_resample = 12000;
			break;
		case 4:
			currentConfig->lame_resample = 16000;
			break;
		case 5:
			currentConfig->lame_resample = 22050;
			break;
		case 6:
			currentConfig->lame_resample = 24000;
			break;
		case 7:
			currentConfig->lame_resample = 32000;
			break;
		case 8:
			currentConfig->lame_resample = 44100;
			break;
		case 9:
			currentConfig->lame_resample = 48000;
			break;
	}

	SMOOTH::CloseWindow(mainWnd);
}

SMOOTHVoid configureLameEnc::Cancel()
{
	SMOOTH::CloseWindow(mainWnd);
}

SMOOTHVoid configureLameEnc::SetBitrateOption()
{
	if (set_bitrate)
	{
		basic_slider_bitrate->Activate();
		basic_text_bitrate->Activate();
		basic_edit_ratio->Deactivate();
	}
	else
	{
		basic_edit_ratio->Activate();
		basic_slider_bitrate->Deactivate();
		basic_text_bitrate->Deactivate();
	}
}

SMOOTHVoid configureLameEnc::SetBitrate()
{
	basic_text_bitrate->SetText(SMOOTHString::IntToString(GetBitrate()).Append(" kbps"));
}

SMOOTHVoid configureLameEnc::SetQualityOption()
{
	if (set_quality)
	{
		basic_slider_quality->Activate();
		basic_text_quality->Activate();
		basic_text_quality_worse->Activate();
		basic_text_quality_better->Activate();
	}
	else
	{
		basic_slider_quality->Deactivate();
		basic_text_quality->Deactivate();
		basic_text_quality_worse->Deactivate();
		basic_text_quality_better->Deactivate();
	}
}

SMOOTHVoid configureLameEnc::SetQuality()
{
	basic_text_quality->SetText(SMOOTHString::IntToString(9 - quality));
}

SMOOTHVoid configureLameEnc::SetStereoMode()
{
	if (stereomode == 2)	basic_check_forcejs->Activate();
	else			basic_check_forcejs->Deactivate();
}

SMOOTHVoid configureLameEnc::SetVBRQuality()
{
	vbr_text_quality->SetText(SMOOTHString::IntToString(9 - vbrquality));
}

SMOOTHVoid configureLameEnc::SetVBRMode()
{
	switch (vbrmode)
	{
		default:
			vbr_quality->Deactivate();
			vbr_text_setquality->Deactivate();
			vbr_slider_quality->Deactivate();
			vbr_text_quality->Deactivate();
			vbr_text_quality_worse->Deactivate();
			vbr_text_quality_better->Deactivate();

			vbr_abrbitrate->Deactivate();
			vbr_slider_abrbitrate->Deactivate();
			vbr_edit_abrbitrate->Deactivate();
			vbr_text_abrbitrate_kbps->Deactivate();

			vbr_bitrate->Deactivate();
			vbr_check_set_min_brate->Deactivate();
			vbr_slider_min_brate->Deactivate();
			vbr_text_min_brate_kbps->Deactivate();
			vbr_check_set_max_brate->Deactivate();
			vbr_slider_max_brate->Deactivate();
			vbr_text_max_brate_kbps->Deactivate();

			basic_bitrate->Activate();
			basic_option_set_bitrate->Activate();
			basic_option_set_ratio->Activate();

			if (set_bitrate == 1)
			{
				basic_slider_bitrate->Activate();
				basic_text_bitrate->Activate();
			}
			else
			{
				basic_edit_ratio->Activate();
			}

			break;
		case vbr_abr:
			vbr_quality->Deactivate();
			vbr_text_setquality->Deactivate();
			vbr_slider_quality->Deactivate();
			vbr_text_quality->Deactivate();
			vbr_text_quality_worse->Deactivate();
			vbr_text_quality_better->Deactivate();

			vbr_abrbitrate->Activate();
			vbr_slider_abrbitrate->Activate();
			vbr_edit_abrbitrate->Activate();
			vbr_text_abrbitrate_kbps->Activate();

			vbr_bitrate->Activate();
			vbr_check_set_min_brate->Activate();

			if (set_min_vbr_brate)
			{
				vbr_slider_min_brate->Activate();
				vbr_text_min_brate_kbps->Activate();
			}
			else
			{
				vbr_slider_min_brate->Deactivate();
				vbr_text_min_brate_kbps->Deactivate();
			}

			vbr_check_set_max_brate->Activate();

			if (set_max_vbr_brate)
			{
				vbr_slider_max_brate->Activate();
				vbr_text_max_brate_kbps->Activate();
			}
			else
			{
				vbr_slider_max_brate->Deactivate();
				vbr_text_max_brate_kbps->Deactivate();
			}

			basic_bitrate->Deactivate();
			basic_option_set_bitrate->Deactivate();
			basic_option_set_ratio->Deactivate();
			basic_slider_bitrate->Deactivate();
			basic_text_bitrate->Deactivate();
			basic_edit_ratio->Deactivate();

			break;
		case vbr_rh:
		case vbr_mtrh:
			vbr_quality->Activate();
			vbr_text_setquality->Activate();
			vbr_slider_quality->Activate();
			vbr_text_quality->Activate();
			vbr_text_quality_worse->Activate();
			vbr_text_quality_better->Activate();

			vbr_abrbitrate->Deactivate();
			vbr_slider_abrbitrate->Deactivate();
			vbr_edit_abrbitrate->Deactivate();
			vbr_text_abrbitrate_kbps->Deactivate();

			vbr_bitrate->Activate();
			vbr_check_set_min_brate->Activate();

			if (set_min_vbr_brate)
			{
				vbr_slider_min_brate->Activate();
				vbr_text_min_brate_kbps->Activate();
			}
			else
			{
				vbr_slider_min_brate->Deactivate();
				vbr_text_min_brate_kbps->Deactivate();
			}

			vbr_check_set_max_brate->Activate();

			if (set_max_vbr_brate)
			{
				vbr_slider_max_brate->Activate();
				vbr_text_max_brate_kbps->Activate();
			}
			else
			{
				vbr_slider_max_brate->Deactivate();
				vbr_text_max_brate_kbps->Deactivate();
			}

			basic_bitrate->Deactivate();
			basic_option_set_bitrate->Deactivate();
			basic_option_set_ratio->Deactivate();
			basic_slider_bitrate->Deactivate();
			basic_text_bitrate->Deactivate();
			basic_edit_ratio->Deactivate();

			break;
	}
}

SMOOTHVoid configureLameEnc::SetABRBitrate()
{
	vbr_edit_abrbitrate->SetText(SMOOTHString::IntToString(abrbitrate));
}

SMOOTHVoid configureLameEnc::SetABRBitrateByEditBox()
{
	vbr_slider_abrbitrate->SetValue(vbr_edit_abrbitrate->GetText().ToInt());
}

SMOOTHVoid configureLameEnc::SetMinVBRBitrateOption()
{
	if (set_min_vbr_brate)
	{
		vbr_slider_min_brate->Activate();
		vbr_text_min_brate_kbps->Activate();
	}
	else
	{
		vbr_slider_min_brate->Deactivate();
		vbr_text_min_brate_kbps->Deactivate();
	}
}

SMOOTHVoid configureLameEnc::SetMaxVBRBitrateOption()
{
	if (set_max_vbr_brate)
	{
		vbr_slider_max_brate->Activate();
		vbr_text_max_brate_kbps->Activate();
	}
	else
	{
		vbr_slider_max_brate->Deactivate();
		vbr_text_max_brate_kbps->Deactivate();
	}
}

SMOOTHVoid configureLameEnc::SetMinVBRBitrate()
{
	vbr_text_min_brate_kbps->SetText(SMOOTHString::IntToString(GetMinVBRBitrate()).Append(" kbps"));

	if (min_vbr_brate > max_vbr_brate)
	{
		vbr_slider_max_brate->SetValue(min_vbr_brate);
	}
}

SMOOTHVoid configureLameEnc::SetMaxVBRBitrate()
{
	vbr_text_max_brate_kbps->SetText(SMOOTHString::IntToString(GetMaxVBRBitrate()).Append(" kbps"));

	if (max_vbr_brate < min_vbr_brate)
	{
		vbr_slider_min_brate->SetValue(max_vbr_brate);
	}
}

SMOOTHVoid configureLameEnc::SetHighpass()
{
	if (set_highpass)
	{
		filtering_edit_highpass->Activate();
		filtering_set_highpass_width->Activate();

		SetHighpassWidth();
	}
	else
	{
		filtering_edit_highpass->Deactivate();
		filtering_set_highpass_width->Deactivate();
		filtering_edit_highpass_width->Deactivate();
	}
}

SMOOTHVoid configureLameEnc::SetHighpassWidth()
{
	if (set_highpass_width)	filtering_edit_highpass_width->Activate();
	else			filtering_edit_highpass_width->Deactivate();
}

SMOOTHVoid configureLameEnc::SetLowpass()
{
	if (set_lowpass)
	{
		filtering_edit_lowpass->Activate();
		filtering_set_lowpass_width->Activate();

		SetLowpassWidth();
	}
	else
	{
		filtering_edit_lowpass->Deactivate();
		filtering_set_lowpass_width->Deactivate();
		filtering_edit_lowpass_width->Deactivate();
	}
}

SMOOTHVoid configureLameEnc::SetLowpassWidth()
{
	if (set_lowpass_width)	filtering_edit_lowpass_width->Activate();
	else			filtering_edit_lowpass_width->Deactivate();
}

SMOOTHVoid configureLameEnc::SetDisableFiltering()
{
	if (disable_filtering)
	{
		filtering_lowpass->Deactivate();
		filtering_highpass->Deactivate();
		filtering_set_lowpass->Deactivate();
		filtering_edit_lowpass->Deactivate();
		filtering_set_lowpass_width->Deactivate();
		filtering_edit_lowpass_width->Deactivate();
		filtering_set_highpass->Deactivate();
		filtering_edit_highpass->Deactivate();
		filtering_set_highpass_width->Deactivate();
		filtering_edit_highpass_width->Deactivate();
	}
	else
	{
		filtering_lowpass->Activate();
		filtering_highpass->Activate();
		filtering_set_lowpass->Activate();
		filtering_set_highpass->Activate();

		SetLowpass();
		SetHighpass();
	}
}

SMOOTHInt configureLameEnc::GetBitrate()
{
	return SliderValueToBitrate(bitrate);
}

SMOOTHInt configureLameEnc::GetSliderValue()
{
	return BitrateToSliderValue(currentConfig->lame_bitrate);
}

SMOOTHInt configureLameEnc::GetMinVBRBitrate()
{
	return SliderValueToBitrate(min_vbr_brate);
}

SMOOTHInt configureLameEnc::GetMinVBRSliderValue()
{
	return BitrateToSliderValue(currentConfig->lame_min_vbr_bitrate);
}

SMOOTHInt configureLameEnc::GetMaxVBRBitrate()
{
	return SliderValueToBitrate(max_vbr_brate);
}

SMOOTHInt configureLameEnc::GetMaxVBRSliderValue()
{
	return BitrateToSliderValue(currentConfig->lame_max_vbr_bitrate);
}

SMOOTHInt configureLameEnc::SliderValueToBitrate(SMOOTHInt value)
{
	switch (value)
	{
		case 0:
			return 8;
		case 1:
			return 16;
		case 2:
			return 24;
		case 3:
			return 32;
		case 4:
			return 40;
		case 5:
			return 48;
		case 6:
			return 56;
		case 7:
			return 64;
		case 8:
			return 80;
		case 9:
			return 96;
		case 10:
			return 112;
		case 11:
			return 128;
		case 12:
			return 144;
		case 13:
			return 160;
		case 14:
			return 192;
		case 15:
			return 224;
		case 16:
			return 256;
		case 17:
			return 320;
		default:
			return 128;
	}
}

SMOOTHInt configureLameEnc::BitrateToSliderValue(SMOOTHInt value)
{
	switch (value)
	{
		case 8:
			return 0;
		case 16:
			return 1;
		case 24:
			return 2;
		case 32:
			return 3;
		case 40:
			return 4;
		case 48:
			return 5;
		case 56:
			return 6;
		case 64:
			return 7;
		case 80:
			return 8;
		case 96:
			return 9;
		case 112:
			return 10;
		case 128:
			return 11;
		case 144:
			return 12;
		case 160:
			return 13;
		case 192:
			return 14;
		case 224:
			return 15;
		case 256:
			return 16;
		case 320:
			return 17;
		default:
			return 11;
	}
}
