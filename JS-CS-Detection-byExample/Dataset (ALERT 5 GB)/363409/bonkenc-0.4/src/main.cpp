 /* BonkEnc version 0.4
  * Copyright (C) 2001 Robert Kausch <robert.kausch@gmx.net>
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the "GNU General Public License".
  *
  * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. */

#define __THROW_BAD_ALLOC exit(1)

#include <smooth.h>
#include <main.h>
#include <resources.h>
#include <selenc.h>
#include <bonkconfig.h>
#include <bladeconfig.h>
#include <bonk.h>
#include <wav.h>
#include <stdlib.h>
#include <string>
#include <vector>
#include <parseini.h>
#include <bladedll.h>
#include <time.h>

void SMOOTH::Main()
{
	bonkEnc	*app = new bonkEnc();

	SMOOTH::Loop();

	delete app;
}

bonkEnc::bonkEnc()
{
	encoding = false;
	encoder_thread = NULL;

	POINT	 pos;
	SIZE	 size;

	currentConfig = new bonkEncConfig;

	currentConfig->encoder = getINIValue("Settings", "Encoder", "0").ToInt();

	currentConfig->bonk_quantization = getINIValue("bonkEnc", "Quantization", "17").ToInt();
	currentConfig->bonk_predictor = getINIValue("bonkEnc", "Predictor", "128").ToInt();
	currentConfig->bonk_downsampling = getINIValue("bonkEnc", "Downsampling", "2").ToInt();
	currentConfig->bonk_jstereo = getINIValue("bonkEnc", "JointStereo", "1").ToInt();
	currentConfig->bonk_outdir = getINIValue("bonkEnc", "Outdir", "C:\\");

	currentConfig->blade_bitrate = getINIValue("bladeEnc", "Bitrate", "128").ToInt();
	currentConfig->blade_crc = getINIValue("bladeEnc", "CRC", "0").ToInt();
	currentConfig->blade_copyright = getINIValue("bladeEnc", "Copyright", "0").ToInt();
	currentConfig->blade_original = getINIValue("bladeEnc", "Original", "1").ToInt();
	currentConfig->blade_outdir = getINIValue("bladeEnc", "Outdir", "C:\\");;

	if (LoadBladeDLL() == false)	currentConfig->enable_blade = false;
	else				currentConfig->enable_blade = true;

	if ((currentConfig->enable_blade == false) && (currentConfig->encoder == ENCODER_BLADEENC)) currentConfig->encoder = ENCODER_BONKENC;

	int	 len = currentConfig->bonk_outdir.Length() - 1;
	if (currentConfig->bonk_outdir[len] != '\\') currentConfig->bonk_outdir[++len] = '\\';

	len = currentConfig->blade_outdir.Length() - 1;
	if (currentConfig->blade_outdir[len] != '\\') currentConfig->blade_outdir[++len] = '\\';

	mainWnd_menubar		= new SMOOTHMenubar();
	mainWnd_iconbar		= new SMOOTHMenubar();
	mainWnd			= new SMOOTHWindow("BonkEnc v0.4");
	mainWnd_titlebar	= new SMOOTHTitlebar(true, false, true);
	mainWnd_statusbar	= new SMOOTHStatusbar("BonkEnc v0.4 - Copyright (C) 2001 Robert Kausch");
	mainWnd_layer		= new SMOOTHLayer();
	menu_file		= new SMOOTHPopupMenu();
	menu_options		= new SMOOTHPopupMenu();

	pos.x = 291;
	pos.y = -22;

	hyperlink		= new SMOOTHHyperlink("bonkenc.sourceforge.net", "http://bonkenc.sourceforge.net", pos);

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

	switch (currentConfig->encoder)
	{
		case ENCODER_BONKENC:
			edb_encoder = new SMOOTHEditBox("BonkEnc", pos, size, EDB_ALPHANUMERIC, 4, NULLPROC);
			break;
		case ENCODER_BLADEENC:
			edb_encoder = new SMOOTHEditBox("BladeEnc", pos, size, EDB_ALPHANUMERIC, 4, NULLPROC);
			break;
	}

	edb_encoder->Deactivate();

	pos.x = 78;
	pos.y += 48;
	size.cx = 329;

	switch (currentConfig->encoder)
	{
		case ENCODER_BONKENC:
			edb_outdir = new SMOOTHEditBox(currentConfig->bonk_outdir, pos, size, EDB_ALPHANUMERIC, 1024, NULLPROC);
			break;
		case ENCODER_BLADEENC:
			edb_outdir = new SMOOTHEditBox(currentConfig->blade_outdir, pos, size, EDB_ALPHANUMERIC, 1024, NULLPROC);
			break;
	}

	edb_outdir->Deactivate();

	pos.x = 78;
	pos.y = 230;
	size.cx = 328;
	size.cy = 18;

	progress		= new SMOOTHProgressbar(pos, size, OR_HORZ, PB_NOTEXT, 0, 1000, 0);
	progress->Deactivate();

	menu_file->AddEntry("Add...", SMOOTHProc(bonkEnc, this, AddFile));
	menu_file->AddEntry("Remove", SMOOTHProc(bonkEnc, this, RemoveFile));
	menu_file->AddEntry();
	menu_file->AddEntry("Clear joblist", SMOOTHProc(bonkEnc, this, ClearList));
	menu_file->AddEntry();
	menu_file->AddEntry("Exit", SMOOTHProc(bonkEnc, this, Exit));

	menu_options->AddEntry("Select encoder...", SMOOTHProc(bonkEnc, this, SelectEncoder));
	menu_options->AddEntry("Configure selected encoder...", SMOOTHProc(bonkEnc, this, ConfigureEncoder));

	mainWnd_menubar->AddEntry("File", menu_file);
	mainWnd_menubar->AddEntry("Options", menu_options);
	mainWnd_menubar->AddHelpEntry();
	mainWnd_menubar->AddHelpEntry(SMOOTH::LoadImage("bonkenc.pci", 6, NULL), SMOOTHProc(bonkEnc, this, About));

	mainWnd_iconbar->AddEntry(SMOOTH::LoadImage("bonkenc.pci", 1, NULL), SMOOTHProc(bonkEnc, this, AddFile));
	mainWnd_iconbar->AddEntry(SMOOTH::LoadImage("bonkenc.pci", 2, NULL), SMOOTHProc(bonkEnc, this, RemoveFile));
	mainWnd_iconbar->AddEntry(SMOOTH::LoadImage("bonkenc.pci", 3, NULL), SMOOTHProc(bonkEnc, this, ClearList));
	mainWnd_iconbar->AddEntry();
	mainWnd_iconbar->AddEntry(SMOOTH::LoadImage("bonkenc.pci", 4, NULL), SMOOTHProc(bonkEnc, this, SelectEncoder));
	mainWnd_iconbar->AddEntry(SMOOTH::LoadImage("bonkenc.pci", 5, NULL), SMOOTHProc(bonkEnc, this, ConfigureEncoder));
	mainWnd_iconbar->AddEntry();
	mainWnd_iconbar->AddEntry(SMOOTH::LoadImage("bonkenc.pci", 7, NULL), SMOOTHProc(bonkEnc, this, Encode));
	mainWnd_iconbar->AddEntry(SMOOTH::LoadImage("bonkenc.pci", 8, NULL), SMOOTHProc(bonkEnc, this, StopEncoding));

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

	mainWnd->SetIcon(SMOOTH::LoadImage("bonkenc.pci", 0, NULL));
	mainWnd->SetApplicationIcon(IDI_ICON);
	mainWnd->SetMetrics(100, 100, 420, 371);
	mainWnd->SetKillProc(SMOOTHKillProc(bonkEnc, this, KillProc));
}

bonkEnc::~bonkEnc()
{
	if (currentConfig->enable_blade) FreeBladeDLL();

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
	delete hyperlink;
}

void bonkEnc::AddFile()
{
	if (encoding)
	{
		SMOOTH::MessageBox("Cannot modify the joblist while encoding!", "Error", MB_OK, IDI_HAND);

		return;
	}

	static OPENFILENAME	 ofn;
	char			*buffer = new char [32768];
	char			*buffer2 = new char [1024];
	int			 pos = 0;
	SMOOTHString		 dir;
	SMOOTHString		 file;

	for (int i = 0; i < 32768; i++) buffer[i] = 0;

	ofn.lStructSize		= sizeof(OPENFILENAME);
	ofn.hwndOwner		= mainWnd->hwnd;
	ofn.lpstrFilter		= "Wave Files (*.wav)\0*.wav\0All Files (*.*)\0*.*\0";
	ofn.nFilterIndex	= 1;
	ofn.lpstrFile		= buffer;
	ofn.nMaxFile		= 32786;
	ofn.lpstrFileTitle	= NULL;
	ofn.lpstrInitialDir	= NULL;
	ofn.lpstrTitle		= NULL;
	ofn.Flags		= OFN_ALLOWMULTISELECT | OFN_FILEMUSTEXIST | OFN_PATHMUSTEXIST | OFN_HIDEREADONLY | OFN_EXPLORER;

	if (GetOpenFileName(&ofn))
	{
		int	 i;

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

				ra_joblist.AddEntry(file, joblist->AddEntry(file, NULLPROC));

				pos = 0;

				if (buffer[i + 1] == 0) break;
			}
		}
	}

	delete [] buffer;
	delete [] buffer2;

	txt_joblist->SetText(SMOOTHString::IntToString(joblist->GetNOfEntries()).Append(" file(s) in joblist:"));
}

void bonkEnc::RemoveFile()
{
	if (encoding)
	{
		SMOOTH::MessageBox("Cannot modify the joblist while encoding!", "Error", MB_OK, IDI_HAND);

		return;
	}

	if (joblist->GetSelectedEntry() != SMOOTH::Error)
	{
		ra_joblist.DeleteEntry(joblist->GetSelectedEntry());
		joblist->RemoveEntry(joblist->GetSelectedEntry());

		txt_joblist->SetText(SMOOTHString::IntToString(joblist->GetNOfEntries()).Append(" file(s) in joblist:"));
	}
	else
	{
		SMOOTH::MessageBox("You have not selected a file!", "Error!", MB_OK, IDI_HAND);
	}
}

void bonkEnc::ClearList()
{
	if (encoding)
	{
		SMOOTH::MessageBox("Cannot modify the joblist while encoding!", "Error", MB_OK, IDI_HAND);

		return;
	}

	ra_joblist.DeleteAll();
	joblist->Cleanup();

	txt_joblist->SetText("0 file(s) in joblist:");
}

bool bonkEnc::KillProc()
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

		str = "Outdir=";
		str.Append(currentConfig->bonk_outdir);
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

		str = "Outdir=";
		str.Append(currentConfig->blade_outdir);
		out->OutputLine(str);
	}

	delete out;

	return true;
}

void bonkEnc::Exit()
{
	SMOOTH::CloseWindow(mainWnd);
}

void bonkEnc::About()
{
	SMOOTH::MessageBox("BonkEnc version 0.4\nCopyright (C) 2001 Robert Kausch\n\nThis program is being distributed under the\nterms of the GNU General Public License (GPL).\n\nFor more information on BonkEnc visit the\nwebsite at 'http://bonkenc.sourceforge.net' or\ncontact me at robert.kausch@gmx.net.", "About BonkEnc v0.4", MB_OK, MAKEINTRESOURCE(IDI_ICON));
}

void bonkEnc::SelectEncoder()
{
	if (encoding)
	{
		SMOOTH::MessageBox("Cannot select encoder while encoding!", "Error", MB_OK, IDI_HAND);

		return;
	}

	selectEncoder	*dlg = new selectEncoder(currentConfig);

	currentConfig->encoder = dlg->ShowDialog();

	switch (currentConfig->encoder)
	{
		case ENCODER_BONKENC:
			edb_encoder->SetText("BonkEnc");
			edb_outdir->SetText(currentConfig->bonk_outdir);
			break;
		case ENCODER_BLADEENC:
			edb_encoder->SetText("BladeEnc");
			edb_outdir->SetText(currentConfig->blade_outdir);
			break;
	}

	delete dlg;
}

void bonkEnc::ConfigureEncoder()
{
	if (encoding)
	{
		SMOOTH::MessageBox("Cannot configure encoder while encoding!", "Error", MB_OK, IDI_HAND);

		return;
	}

	switch (currentConfig->encoder)
	{
		case ENCODER_BONKENC:
			{
				configureBonkEnc	*dlg = new configureBonkEnc(currentConfig);

				dlg->ShowDialog();

				delete dlg;

				edb_outdir->SetText(currentConfig->bonk_outdir);
			}
			break;
		case ENCODER_BLADEENC:
			{
				configureBladeEnc	*dlg = new configureBladeEnc(currentConfig);

				dlg->ShowDialog();

				delete dlg;

				edb_outdir->SetText(currentConfig->blade_outdir);
			}
			break;
	}
}

void bonkEnc::Encode()
{
	if (encoding) return;

	if (encoder_thread != NULL)
	{
		UnregisterObject(encoder_thread);

		delete encoder_thread;

		encoder_thread = NULL;
	}

	encoder_thread = new SMOOTHThread(SMOOTHThreadProc(bonkEnc, this, Encoder));

	encoding = true;

	RegisterObject(encoder_thread);

	encoder_thread->Start();
}

void bonkEnc::Encoder(SMOOTHThread *thread)
{
	switch (currentConfig->encoder)
	{
		case ENCODER_BONKENC:
			{
				SMOOTHString	 file;
				SMOOTHString	 out_name;
				int		 startticks;
				int		 ticks;
				int		 lastticks = 0;

				int		 down_sampling = currentConfig->bonk_downsampling;
				double		 quantization = 0.05 * (double) currentConfig->bonk_quantization;
				int		 tap_count = currentConfig->bonk_predictor;
				bool		 lossless = false;
				bool		 mid_side = currentConfig->bonk_jstereo;
				char		*comment = 0, *artist = 0, *title = 0;

				for (int i = 0; i < ra_joblist.GetNOfEntries(); i++)
				{
					if (i == 0)	file = ra_joblist.GetFirstEntry();
					else		file = ra_joblist.GetNextEntry();

					edb_filename->SetText(file);
					progress->SetValue(0);
					edb_time->SetText("00:00");

					string description = "";

					if (artist)
					{
						description += "Artist: ";
						description += artist;
						description += "\n";
					}

					if (title)
					{
						description += "Title: ";
						description += title;
						description += "\n";
					}
  
					if (comment)
					{
						description += comment;
						description += "\n";
					}

					out_name.Copy(edb_outdir->GetText());

					int	 len = file.Length();
					int	 len2 = out_name.Length();
					int	 lastbs = 0;

					for (int i = 0; i < len; i++)
					{
						if (file[i] == '\\') lastbs = i;
					}

					for (int i = len2; i < (len + len2 - lastbs - 5); i++)
					{
						out_name[i] = file[(i - len2) + lastbs + 1];
					}

					out_name.Append(".bonk");

					InStream *f_in = new InStream(STREAM_FILE, file);

					int channels,rate,length;
					read_wav_header(f_in,channels,rate,length);

					OutStream *f_out = new OutStream(STREAM_FILE, out_name, OS_OVERWRITE);

					int packet_size = int(2048.0 *rate/44100);

					BONKencoder enco;
					enco.begin(f_out,
						description.c_str(),
						length, rate, channels, lossless, mid_side,
						tap_count, down_sampling,
						packet_size/down_sampling,
						quantization);

					vector<int> samples(enco.samples_size); 

					int position = 0;
					int n_loops = (length+enco.samples_size-1)/enco.samples_size;
					int lastpercent = 100;

					startticks = clock();

					for(int loop=0;loop<n_loops;loop++)
					{
						int step = samples.size();

						if (position+step > length)
							step = length-position;

						for(int i=0;i<step;i++)
							samples[i] = int16(f_in->InputNumber(2));

						for(int i=step;i<(int)samples.size();i++)
							samples[i] = 0;

						position += step;

						enco.store_packet(samples);

						progress->SetValue((int) ((position * 100.0 / length) * 10.0));

						if ((int)(position*100.0/length) != lastpercent)
						{
							lastpercent = (int)(position*100.0/length);

							edb_percent->SetText(SMOOTHString::IntToString(lastpercent).Append("%"));
						}

						ticks = clock() - startticks;

						ticks = (int) (ticks * ((1000.0 - ((position * 100.0 / length) * 10.0)) / ((position * 100.0 / length) * 10.0))) / 1000 + 1;

						if (ticks != lastticks)
						{
							lastticks = ticks;

							SMOOTHString	 buf = SMOOTHString::IntToString(ticks / 60);
							SMOOTHString	 txt = "0";

							if (buf.Length() == 1)	txt.Append(buf);
							else			txt.Copy(buf);

							txt.Append(":");

							buf = SMOOTHString::IntToString(ticks % 60);

							if (buf.Length() == 1)	txt.Append(SMOOTHString("0").Append(buf));
							else			txt.Append(buf);

							edb_time->SetText(txt);
						}
					}

					enco.finish();

					delete f_in;
					delete f_out;
				}

				encoding = false;

				ClearList();

				edb_filename->SetText("none");
				edb_percent->SetText("0%");
				progress->SetValue(0);
				edb_time->SetText("00:00");
			}
			break;
		case ENCODER_BLADEENC:
			{
				SMOOTHString	 file;
				SMOOTHString	 out_name;
				SMOOTHOutStream	*out;
				int		 startticks;
				int		 ticks;
				int		 lastticks = 0;

				long		 samples_size;
				long		 buffersize;
				int		 handle;
				BE_CONFIG	 config;

				for (int i = 0; i < ra_joblist.GetNOfEntries(); i++)
				{
					if (i == 0)	file = ra_joblist.GetFirstEntry();
					else		file = ra_joblist.GetNextEntry();

					edb_filename->SetText(file);
					progress->SetValue(0);
					edb_time->SetText("00:00");

					out_name.Copy(edb_outdir->GetText());

					int	 len = file.Length();
					int	 len2 = out_name.Length();
					int	 lastbs = 0;

					for (int i = 0; i < len; i++)
					{
						if (file[i] == '\\') lastbs = i;
					}

					for (int i = len2; i < (len + len2 - lastbs - 5); i++)
					{
						out_name[i] = file[(i - len2) + lastbs + 1];
					}

					out_name.Append(".mp3");

					InStream *f_in = new InStream(STREAM_FILE, file);

					int channels,rate,length;
					read_wav_header(f_in,channels,rate,length);

					config.dwConfig			= BE_CONFIG_MP3;
					config.format.mp3.dwSampleRate	= rate;
					config.format.mp3.byMode	= BE_MP3_MODE_STEREO;
					config.format.mp3.wBitrate	= currentConfig->blade_bitrate;
					config.format.mp3.bCopyright	= currentConfig->blade_copyright;
					config.format.mp3.bCRC		= currentConfig->blade_crc;
					config.format.mp3.bOriginal	= currentConfig->blade_original;
					config.format.mp3.bPrivate	= false;

					beInitStream(&config, &samples_size, &buffersize, &handle);

					char		*outbuffer = new char [buffersize];
					signed short	*samples = new signed short [samples_size]; 

					int position = 0;
					int n_loops = (length+samples_size-1)/samples_size;
					int lastpercent = 100;

					out = new SMOOTHOutStream(STREAM_FILE, out_name, OS_OVERWRITE);

					startticks = clock();

					for(int loop=0;loop<n_loops;loop++)
					{
						int step = samples_size;

						if (position+step > length)
							step = length-position;

						for(int i=0;i<step;i++)
							samples[i] = int16(f_in->InputNumber(2));

						for(int i=step;i<samples_size;i++)
							samples[i] = 0;

						position += step;

						beEncodeChunk(handle, step, samples, outbuffer, &buffersize);

						out->OutputData((void *) outbuffer, buffersize);

						progress->SetValue((int) ((position * 100.0 / length) * 10.0));

						if ((int)(position*100.0/length) != lastpercent)
						{
							lastpercent = (int)(position*100.0/length);

							edb_percent->SetText(SMOOTHString::IntToString(lastpercent).Append("%"));						}

						ticks = clock() - startticks;

						ticks = (int) (ticks * ((1000.0 - ((position * 100.0 / length) * 10.0)) / ((position * 100.0 / length) * 10.0))) / 1000 + 1;

						if (ticks != lastticks)
						{
							lastticks = ticks;

							SMOOTHString	 buf = SMOOTHString::IntToString(ticks / 60);
							SMOOTHString	 txt = "0";

							if (buf.Length() == 1)	txt.Append(buf);
							else			txt.Copy(buf);

							txt.Append(":");

							buf = SMOOTHString::IntToString(ticks % 60);

							if (buf.Length() == 1)	txt.Append(SMOOTHString("0").Append(buf));
							else			txt.Append(buf);

							edb_time->SetText(txt);
						}
					}

					beDeinitStream(handle, outbuffer, &buffersize);

					out->OutputData((void *) outbuffer, buffersize);

					delete [] outbuffer;
					delete [] samples;

					beCloseStream(handle);

					delete f_in;

					delete out;
				}

				encoding = false;

				ClearList();

				edb_filename->SetText("none");
				edb_percent->SetText("0%");
				progress->SetValue(0);
				edb_time->SetText("00:00");
			}
			break;
	}
}

void bonkEnc::StopEncoding()
{
	if (!encoding) return;

	encoder_thread->Stop();

	UnregisterObject(encoder_thread);

	delete encoder_thread;

	encoder_thread = NULL;
	encoding = false;

	edb_filename->SetText("none");
	edb_percent->SetText("0%");
	progress->SetValue(0);
	edb_time->SetText("00:00");
}

bool bonkEnc::LoadBladeDLL()
{
	SMOOTHString	 file = SMOOTH::StartDirectory;

	file.Append("bladeenc.dll");

	bladedll = LoadLibrary(file);

	if (bladedll == NULL) return false;

	beInitStream	= GetProcAddress(bladedll, "beInitStream");
	beEncodeChunk	= GetProcAddress(bladedll, "beEncodeChunk");
	beDeinitStream	= GetProcAddress(bladedll, "beDeinitStream");
	beCloseStream	= GetProcAddress(bladedll, "beCloseStream");

	return true;
}

void bonkEnc::FreeBladeDLL()
{
	FreeLibrary(bladedll);	
}
