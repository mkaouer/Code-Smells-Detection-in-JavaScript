 /* BonkEnc version 0.4
  * Copyright (C) 2001 Robert Kausch <robert.kausch@gmx.net>
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the "GNU General Public License".
  *
  * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. */

#ifndef _H_SELECTENCODER_
#define _H_SELECTENCODER_

#include <smoothx.h>
#include <main.h>

class selectEncoder : public SMOOTHApplication
{
	private:
		SMOOTHGroupBox		*group;
		SMOOTHDivisionbar	*divbar;

		SMOOTHWindow		*mainWnd;
		SMOOTHTitlebar		*mainWnd_titlebar;
		SMOOTHLayer		*mainWnd_layer;

		SMOOTHButton		*btn_cancel;
		SMOOTHButton		*btn_ok;

		SMOOTHOptionBox		*opt_bonk;
		SMOOTHOptionBox		*opt_blade;

		int			 oldenc;
		int			 encoder;

		void			 OK();
		void			 Cancel();
	public:
					 selectEncoder(bonkEncConfig *);
					~selectEncoder();
		int			 ShowDialog();
};

#endif
