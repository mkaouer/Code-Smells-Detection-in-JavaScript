 /* BonkEnc version 0.4
  * Copyright (C) 2001 Robert Kausch <robert.kausch@gmx.net>
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the "GNU General Public License".
  *
  * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. */

#ifndef _H_BLADECONFIG_
#define _H_BLADECONFIG_

#include <smoothx.h>
#include <main.h>

class configureBladeEnc : public SMOOTHApplication
{
	private:
		SMOOTHGroupBox		*group_bit;
		SMOOTHSlider		*slider_bit;
		SMOOTHText		*text_bit;

		SMOOTHGroupBox		*group_dir;
		SMOOTHEditBox		*edit_dir;
		SMOOTHButton		*button_dir_browse;

		SMOOTHGroupBox		*group_crc;
		SMOOTHCheckBox		*check_crc;

		SMOOTHGroupBox		*group_copyright;
		SMOOTHCheckBox		*check_copyright;

		SMOOTHGroupBox		*group_original;
		SMOOTHCheckBox		*check_original;

		SMOOTHDivisionbar	*divbar;

		SMOOTHWindow		*mainWnd;
		SMOOTHTitlebar		*mainWnd_titlebar;
		SMOOTHLayer		*mainWnd_layer;

		SMOOTHButton		*btn_cancel;
		SMOOTHButton		*btn_ok;

		int			 bitrate;
		bool			 crc;
		bool			 copyright;
		bool			 original;

		bonkEncConfig		*currentConfig;

		void			 OK();
		void			 Cancel();
		void			 SelectDir();
		void			 SetBitrate();
		int			 GetBitrate();
		int			 GetSliderValue();
	public:
					 configureBladeEnc(bonkEncConfig *);
					~configureBladeEnc();
		int			 ShowDialog();
};

#endif
