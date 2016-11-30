 /* BonkEnc version 0.4
  * Copyright (C) 2001 Robert Kausch <robert.kausch@gmx.net>
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the "GNU General Public License".
  *
  * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. */

#ifndef _H_BONKCONFIG_
#define _H_BONKCONFIG_

#include <smoothx.h>
#include <main.h>

class configureBonkEnc : public SMOOTHApplication
{
	private:
		SMOOTHGroupBox		*group_quant;
		SMOOTHSlider		*slider_quant;
		SMOOTHText		*text_quant;

		SMOOTHGroupBox		*group_dir;
		SMOOTHEditBox		*edit_dir;
		SMOOTHButton		*button_dir_browse;

		SMOOTHGroupBox		*group_stereo;
		SMOOTHCheckBox		*check_joint;

		SMOOTHGroupBox		*group_downsampling;
		SMOOTHSlider		*slider_downsampling;
		SMOOTHText		*text_downsampling;

		SMOOTHGroupBox		*group_predictor;
		SMOOTHSlider		*slider_predictor;
		SMOOTHText		*text_predictor;

		SMOOTHDivisionbar	*divbar;

		SMOOTHWindow		*mainWnd;
		SMOOTHTitlebar		*mainWnd_titlebar;
		SMOOTHLayer		*mainWnd_layer;

		SMOOTHButton		*btn_cancel;
		SMOOTHButton		*btn_ok;

		int			 quant;
		int			 predictor;
		int			 downsampling;
		bool			 jstereo;

		bonkEncConfig		*currentConfig;

		void			 OK();
		void			 Cancel();
		void			 SelectDir();
		void			 SetQuantization();
		void			 SetPredictorSize();
		void			 SetDownsamplingRatio();
	public:
					 configureBonkEnc(bonkEncConfig *);
					~configureBonkEnc();
		int			 ShowDialog();
};

#endif
