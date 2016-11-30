 /* BonkEnc version 0.4
  * Copyright (C) 2001 Robert Kausch <robert.kausch@gmx.net>
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the "GNU General Public License".
  *
  * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. */

#ifndef _H_MAIN_
#define _H_MAIN_

#include <recarray.h>
#include <smoothx.h>

const int	 ENCODER_BONKENC	= 0;
const int	 ENCODER_BLADEENC	= 1;

typedef struct
{
	int		 encoder;
	bool		 enable_blade;

	int		 bonk_quantization;
	int		 bonk_predictor;
	int		 bonk_downsampling;
	bool		 bonk_jstereo;
	SMOOTHString	 bonk_outdir;

	int		 blade_bitrate;
	bool		 blade_crc;
	bool		 blade_copyright;
	bool		 blade_original;
	SMOOTHString	 blade_outdir;
}
bonkEncConfig;

class bonkEnc : public SMOOTHApplication
{
	private:
		SMOOTHPopupMenu		*menu_file;
		SMOOTHPopupMenu		*menu_options;

		SMOOTHMenubar		*mainWnd_menubar;
		SMOOTHMenubar		*mainWnd_iconbar;
		SMOOTHWindow		*mainWnd;
		SMOOTHTitlebar		*mainWnd_titlebar;
		SMOOTHStatusbar		*mainWnd_statusbar;
		SMOOTHLayer		*mainWnd_layer;

		SMOOTHListBox		*joblist;
		SMOOTHText		*txt_joblist;
		SMOOTHHyperlink		*hyperlink;

		SMOOTHText		*enc_filename;
		SMOOTHText		*enc_time;
		SMOOTHText		*enc_percent;
		SMOOTHText		*enc_encoder;
		SMOOTHText		*enc_progress;
		SMOOTHText		*enc_outdir;

		SMOOTHEditBox		*edb_filename;
		SMOOTHEditBox		*edb_time;
		SMOOTHEditBox		*edb_percent;
		SMOOTHEditBox		*edb_encoder;
		SMOOTHEditBox		*edb_outdir;

		SMOOTHProgressbar	*progress;

		RecArray<SMOOTHString>	 ra_joblist;
		bool			 encoding;
		SMOOTHThread		*encoder_thread;

		bonkEncConfig		*currentConfig;

		HINSTANCE		 bladedll;

		FARPROC			 beInitStream;
		FARPROC			 beEncodeChunk;
		FARPROC			 beDeinitStream;
		FARPROC			 beCloseStream;

		bool			 LoadBladeDLL();
		void			 FreeBladeDLL();

		void			 About();
		void			 AddFile();
		void			 RemoveFile();
		void			 ClearList();
		void			 Exit();
		void			 SelectEncoder();
		void			 ConfigureEncoder();
		void			 Encode();
		void			 Encoder(SMOOTHThread *);
		void			 StopEncoding();
		bool			 KillProc();
	public:
					 bonkEnc();
					~bonkEnc();
};

#endif
