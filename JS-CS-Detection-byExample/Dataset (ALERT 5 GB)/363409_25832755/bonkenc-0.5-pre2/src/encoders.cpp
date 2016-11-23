 /* BonkEnc version 0.5
  * Copyright (C) 2001-2002 Robert Kausch <robert.kausch@gmx.net>
  *
  * This program is free software; you can redistribute it and/or
  * modify it under the terms of the "GNU General Public License".
  *
  * THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR
  * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
  * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE. */

#define __THROW_BAD_ALLOC exit(1)

#include <smoothx.h>
#include <main.h>
#include <string>
#include <time.h>

#include <bonk/bonk.h>
#include <bladedll/bladedll.h>
#include <lame/lame.h>
#include <vorbis/vorbisenc.h>
#include <faac/faac.h>

SMOOTHInt bonkEnc::EncodeBONK(SMOOTHInStream *f_in, SMOOTHOutStream *f_out, bonkFormatInfo *format)
{
#ifndef _MSC_VER
	int	 startticks;
	int	 ticks;
	int	 lastticks = 0;

	int	 down_sampling = currentConfig->bonk_downsampling;
	double	 quantization = 0.05 * (double) currentConfig->bonk_quantization;
	int	 tap_count = currentConfig->bonk_predictor;
	bool	 lossless = false;
	bool	 mid_side = currentConfig->bonk_jstereo;
	int	 packet_size = int(1024.0 * down_sampling * format->rate / 44100);
	int	 samples_size = format->channels * packet_size;

	if (format->channels != 1 && format->channels != 2)
	{
		SMOOTH::MessageBox("BonkEnc does not support more than 2 channels!", "Error", MB_OK, IDI_HAND);

		return SMOOTH::Error;
	}

	if (format->bits != 16)
	{
		SMOOTH::MessageBox("Input files must be 16 bit for BONK encoding!", "Error", MB_OK, IDI_HAND);

		return SMOOTH::Error;
	}

	BONKencoder *encoder = bonk_create_encoder(f_out, NIL,
		format->length, format->rate, format->channels, lossless, mid_side,
		tap_count, down_sampling,
		packet_size / down_sampling,
		quantization);

	vector<int> samples(samples_size); 

	int	 position = 0;
	int	 n_loops = (format->length + samples_size - 1) / samples_size;
	int	 lastpercent = 100;

	startticks = clock();

	for(int loop = 0; loop < n_loops; loop++)
	{
		int	 step = samples.size();

		if (position + step > format->length)
			step = format->length-position;

		if (format->order == BYTE_INTEL)
			for(int i = 0; i < step; i++)
				samples[i] = int16(f_in->InputNumberIntel(2));
		else if (format->order == BYTE_RAW)
			for(int i = 0; i < step; i++)
				samples[i] = int16(f_in->InputNumberRaw(2));

		for(int i = step; i < (int) samples.size(); i++)
			samples[i] = 0;

		position += step;

		bonk_encode_packet(encoder, samples);

		progress->SetValue((int) ((position * 100.0 / format->length) * 10.0));

		if ((int) (position * 100.0 / format->length) != lastpercent)
		{
			lastpercent = (int) (position * 100.0 / format->length);

			edb_percent->SetText(SMOOTHString::IntToString(lastpercent).Append("%"));
		}

		ticks = clock() - startticks;

		ticks = (int) (ticks * ((1000.0 - ((position * 100.0 / format->length) * 10.0)) / ((position * 100.0 / format->length) * 10.0))) / 1000 + 1;

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

	bonk_close_encoder(encoder);
#endif

	return SMOOTH::Success;
}

SMOOTHInt bonkEnc::EncodeBLADE(SMOOTHInStream *f_in, SMOOTHOutStream *f_out, bonkFormatInfo *format)
{
	int		 startticks;
	int		 ticks;
	int		 lastticks = 0;

	unsigned long		 samples_size;
	unsigned long		 buffersize;
	unsigned long		 handle;
	BE_CONFIG	 config;

	if (format->bits != 16)
	{
		SMOOTH::MessageBox("Input files must be 16 bit for BladeEnc MP3 encoding!", "Error", MB_OK, IDI_HAND);

		return SMOOTH::Error;
	}

	if (format->rate != 32000 && format->rate != 44100 && format->rate != 48000)
	{
		SMOOTH::MessageBox("Bad sampling rate! BladeEnc supports only 32, 44.1 or 48kHz.", "Error", MB_OK, IDI_HAND);

		return SMOOTH::Error;
	}

	config.dwConfig			= BE_CONFIG_MP3;
	config.format.mp3.dwSampleRate	= format->rate;

	if (format->channels == 2)
	{
		if (currentConfig->blade_dualchannel)	config.format.mp3.byMode = BE_MP3_MODE_DUALCHANNEL;
		else					config.format.mp3.byMode = BE_MP3_MODE_STEREO;
	}
	else if (format->channels == 1)
	{
		config.format.mp3.byMode = BE_MP3_MODE_MONO;
	}
	else
	{
		SMOOTH::MessageBox("BonkEnc does not support more than 2 channels!", "Error", MB_OK, IDI_HAND);

		return SMOOTH::Error;
	}

	config.format.mp3.wBitrate	= currentConfig->blade_bitrate;
	config.format.mp3.bCopyright	= currentConfig->blade_copyright;
	config.format.mp3.bCRC		= currentConfig->blade_crc;
	config.format.mp3.bOriginal	= currentConfig->blade_original;
	config.format.mp3.bPrivate	= currentConfig->blade_private;

	beInitStream(&config, &samples_size, &buffersize, &handle);

	unsigned char	*outbuffer = new unsigned char [buffersize];
	signed short	*samples = new signed short [samples_size]; 

	int	 position = 0;
	int	 n_loops = (format->length + samples_size - 1) / samples_size;
	int	 lastpercent = 100;

	startticks = clock();

	for(int loop = 0; loop < n_loops; loop++)
	{
		int	 step = samples_size;

		if (position + step > format->length)
			step = format->length - position;

		if (format->order == BYTE_INTEL)
			for(int i = 0; i < step; i++)
				samples[i] = int16(f_in->InputNumberIntel(2));
		else if (format->order == BYTE_RAW)
			for(int i = 0; i < step; i++)
				samples[i] = int16(f_in->InputNumberRaw(2));

		for(unsigned int i = step; i < samples_size; i++)
			samples[i] = 0;

		position += step;

		beEncodeChunk(handle, step, samples, outbuffer, &buffersize);

		f_out->OutputData((void *) outbuffer, buffersize);

		progress->SetValue((int) ((position * 100.0 / format->length) * 10.0));

		if ((int) (position * 100.0 / format->length) != lastpercent)
		{
			lastpercent = (int) (position * 100.0 / format->length);

			edb_percent->SetText(SMOOTHString::IntToString(lastpercent).Append("%"));
		}

		ticks = clock() - startticks;

		ticks = (int) (ticks * ((1000.0 - ((position * 100.0 / format->length) * 10.0)) / ((position * 100.0 / format->length) * 10.0))) / 1000 + 1;

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

	f_out->OutputData((void *) outbuffer, buffersize);

	delete [] outbuffer;
	delete [] samples;

	beCloseStream(handle);

	return SMOOTH::Success;
}

SMOOTHInt bonkEnc::EncodeLAME(SMOOTHInStream *f_in, SMOOTHOutStream *f_out, bonkFormatInfo *format)
{
#ifndef _MSC_VER
	int			 startticks;
	int			 ticks;
	int			 lastticks = 0;

	long			 samples_size = 1024;
	long			 buffersize = 0;
	long			 n_bytes = 0;
	lame_global_flags	*lameFlags;

	if (format->bits != 16)
	{
		SMOOTH::MessageBox("Input files must be 16 bit for LAME MP3 encoding!", "Error", MB_OK, IDI_HAND);

		return SMOOTH::Error;
	}

	int	 effrate;

	if (currentConfig->lame_resample)	effrate = currentConfig->lame_resample;
	else					effrate = format->rate;

	switch (effrate)
	{
		case 8000:
		case 11025:
		case 12000:
		case 16000:
		case 22050:
		case 24000:
			if (currentConfig->lame_set_bitrate && currentConfig->lame_vbrmode == vbr_off && (currentConfig->lame_bitrate == 192 || currentConfig->lame_bitrate == 224 || currentConfig->lame_bitrate == 256 || currentConfig->lame_bitrate == 320))
			{
				SMOOTH::MessageBox("Bad bitrate! The selected bitrate is not supported for this sampling rate.", "Error", MB_OK, IDI_HAND);

				return SMOOTH::Error;
			}

			if (currentConfig->lame_set_min_vbr_bitrate && currentConfig->lame_vbrmode != vbr_off && (currentConfig->lame_min_vbr_bitrate == 192 || currentConfig->lame_min_vbr_bitrate == 224 || currentConfig->lame_min_vbr_bitrate == 256 || currentConfig->lame_min_vbr_bitrate == 320))
			{
				SMOOTH::MessageBox("Bad minimum VBR bitrate! The selected minimum VBR bitrate is not supported for this sampling rate.", "Error", MB_OK, IDI_HAND);

				return SMOOTH::Error;
			}

			if (currentConfig->lame_set_max_vbr_bitrate && currentConfig->lame_vbrmode != vbr_off && (currentConfig->lame_max_vbr_bitrate == 192 || currentConfig->lame_max_vbr_bitrate == 224 || currentConfig->lame_max_vbr_bitrate == 256 || currentConfig->lame_max_vbr_bitrate == 320))
			{
				SMOOTH::MessageBox("Bad maximum VBR bitrate! The selected maximum VBR bitrate is not supported for this sampling rate.", "Error", MB_OK, IDI_HAND);

				return SMOOTH::Error;
			}
			break;
		case 32000:
		case 44100:
		case 48000:
			if (currentConfig->lame_set_bitrate && currentConfig->lame_vbrmode == vbr_off && (currentConfig->lame_bitrate == 8 || currentConfig->lame_bitrate == 16 || currentConfig->lame_bitrate == 24 || currentConfig->lame_bitrate == 144))
			{
				SMOOTH::MessageBox("Bad bitrate! The selected bitrate is not supported for this sampling rate.", "Error", MB_OK, IDI_HAND);

				return SMOOTH::Error;
			}

			if (currentConfig->lame_set_min_vbr_bitrate && currentConfig->lame_vbrmode != vbr_off && (currentConfig->lame_min_vbr_bitrate == 8 || currentConfig->lame_min_vbr_bitrate == 16 || currentConfig->lame_min_vbr_bitrate == 24 || currentConfig->lame_min_vbr_bitrate == 144))
			{
				SMOOTH::MessageBox("Bad minimum VBR bitrate! The selected minimum VBR bitrate is not supported for this sampling rate.", "Error", MB_OK, IDI_HAND);

				return SMOOTH::Error;
			}

			if (currentConfig->lame_set_max_vbr_bitrate && currentConfig->lame_vbrmode != vbr_off && (currentConfig->lame_max_vbr_bitrate == 8 || currentConfig->lame_max_vbr_bitrate == 16 || currentConfig->lame_max_vbr_bitrate == 24 || currentConfig->lame_max_vbr_bitrate == 144))
			{
				SMOOTH::MessageBox("Bad maximum VBR bitrate! The selected maximum VBR bitrate is not supported for this sampling rate.", "Error", MB_OK, IDI_HAND);

				return SMOOTH::Error;
			}
			break;
		default:
			SMOOTH::MessageBox("Bad sampling rate! The selected sampling rate is not supported.", "Error", MB_OK, IDI_HAND);

			return SMOOTH::Error;
	}

	if (format->rate != 8000 && format->rate != 11025 && format->rate != 12000 && format->rate != 16000 && format->rate != 22050 && format->rate != 24000 && format->rate != 32000 && format->rate != 44100 && format->rate != 48000)
	{
		SMOOTH::MessageBox("Bad sampling rate! The selected sampling rate is not supported.", "Error", MB_OK, IDI_HAND);

		return SMOOTH::Error;
	}

	lameFlags = lame_init();

	lame_set_in_samplerate(lameFlags, format->rate);
	lame_set_num_channels(lameFlags, format->channels);

	lame_set_copyright(lameFlags, currentConfig->lame_copyright);
	lame_set_original(lameFlags, currentConfig->lame_original);
	lame_set_extension(lameFlags, currentConfig->lame_private);
	lame_set_error_protection(lameFlags, currentConfig->lame_crc);
	lame_set_strict_ISO(lameFlags, currentConfig->lame_strict_iso);
	lame_set_padding_type(lameFlags, (Padding_type) currentConfig->lame_padding_type);

	if (currentConfig->lame_resample) lame_set_out_samplerate(lameFlags, currentConfig->lame_resample);

	if (currentConfig->lame_vbrmode == vbr_off)
	{
		if (currentConfig->lame_set_bitrate)	lame_set_brate(lameFlags, currentConfig->lame_bitrate);
		else					lame_set_compression_ratio(lameFlags, ((double)currentConfig->lame_ratio) / 100);
	}

	if (currentConfig->lame_set_quality)	lame_set_quality(lameFlags, currentConfig->lame_quality);
	else					lame_set_quality(lameFlags, -1);

	if (currentConfig->lame_disable_filtering)
	{
		lame_set_lowpassfreq(lameFlags, -1);
		lame_set_highpassfreq(lameFlags, -1);
	}
	else
	{
		if (currentConfig->lame_set_lowpass) lame_set_lowpassfreq(lameFlags, currentConfig->lame_lowpass);
		if (currentConfig->lame_set_highpass) lame_set_highpassfreq(lameFlags, currentConfig->lame_highpass);

		if (currentConfig->lame_set_lowpass && currentConfig->lame_set_lowpass_width) lame_set_lowpasswidth(lameFlags, currentConfig->lame_lowpass_width);
		if (currentConfig->lame_set_highpass && currentConfig->lame_set_highpass_width) lame_set_highpasswidth(lameFlags, currentConfig->lame_highpass_width);
	}

	if (format->channels == 2)
	{
		if (currentConfig->lame_stereomode == 1)	lame_set_mode(lameFlags, STEREO);
		else if (currentConfig->lame_stereomode == 2)	lame_set_mode(lameFlags, JOINT_STEREO);
		else						lame_set_mode(lameFlags, NOT_SET);

		if (currentConfig->lame_stereomode == 2)
		{
			if (currentConfig->lame_forcejs)	lame_set_force_ms(lameFlags, 1);
			else					lame_set_force_ms(lameFlags, 0);
		}
	}
	else if (format->channels == 1)
	{
		lame_set_mode(lameFlags, MONO);
	}
	else
	{
		lame_close(lameFlags);

		SMOOTH::MessageBox("BonkEnc does not support more than 2 channels!", "Error", MB_OK, IDI_HAND);

		return SMOOTH::Error;
	}

	switch (currentConfig->lame_vbrmode)
	{
		default:
		case vbr_off:
			break;
		case vbr_abr:
			lame_set_VBR(lameFlags, vbr_abr);
			lame_set_VBR_mean_bitrate_kbps(lameFlags, currentConfig->lame_abrbitrate);
			break;
		case vbr_rh:
			lame_set_VBR(lameFlags, vbr_rh);
			lame_set_VBR_q(lameFlags, currentConfig->lame_vbrquality);
			break;
		case vbr_mtrh:
			lame_set_VBR(lameFlags, vbr_mtrh);
			lame_set_VBR_q(lameFlags, currentConfig->lame_vbrquality);
			break;
	}

	if (currentConfig->lame_vbrmode != vbr_off && currentConfig->lame_set_min_vbr_bitrate) lame_set_VBR_min_bitrate_kbps(lameFlags, currentConfig->lame_min_vbr_bitrate);
	if (currentConfig->lame_vbrmode != vbr_off && currentConfig->lame_set_max_vbr_bitrate) lame_set_VBR_max_bitrate_kbps(lameFlags, currentConfig->lame_max_vbr_bitrate);

	lame_init_params(lameFlags);

	buffersize = samples_size * 2 + 7800;

	unsigned char	*outbuffer = new unsigned char [buffersize];
	signed short	*samples = new signed short [samples_size]; 

	int	 position = 0;
	int	 n_loops = (format->length + samples_size - 1) / samples_size;
	int	 lastpercent = 100;

	startticks = clock();

	for(int loop = 0; loop < n_loops; loop++)
	{
		int	 step = samples_size;

		if (position + step > format->length)
			step = format->length - position;

		if (format->order == BYTE_INTEL)
			for(int i = 0; i < step; i++)
				samples[i] = int16(f_in->InputNumberIntel(2));
		else if (format->order == BYTE_RAW)
			for(int i = 0; i < step; i++)
				samples[i] = int16(f_in->InputNumberRaw(2));

		for(int i = step; i < samples_size; i++)
			samples[i] = 0;

		position += step;

		if (format->channels == 2)	n_bytes = lame_encode_buffer_interleaved(lameFlags, samples, step / format->channels, outbuffer, buffersize);
		else				n_bytes = lame_encode_buffer(lameFlags, samples, samples, step, outbuffer, buffersize);

		f_out->OutputData((void *) outbuffer, n_bytes);

		progress->SetValue((int) ((position * 100.0 / format->length) * 10.0));

		if ((int) (position * 100.0 / format->length) != lastpercent)
		{
			lastpercent = (int) (position * 100.0 / format->length);

			edb_percent->SetText(SMOOTHString::IntToString(lastpercent).Append("%"));
		}

		ticks = clock() - startticks;

		ticks = (int) (ticks * ((1000.0 - ((position * 100.0 / format->length) * 10.0)) / ((position * 100.0 / format->length) * 10.0))) / 1000 + 1;

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

	n_bytes = lame_encode_flush(lameFlags, outbuffer, buffersize);

	f_out->OutputData((void *) outbuffer, n_bytes);

	delete [] outbuffer;
	delete [] samples;

	lame_close(lameFlags);
#endif

	return SMOOTH::Success;
}

SMOOTHInt bonkEnc::EncodeVORBIS(SMOOTHInStream *f_in, SMOOTHOutStream *f_out, bonkFormatInfo *format)
{
#ifndef _MSC_VER
	int	 startticks;
	int	 ticks;
	int	 lastticks = 0;

	long	 samples_size = 1024;

	if (format->channels != 2)
	{
		SMOOTH::MessageBox("Input files must be stereo for OggVorbis encoding!", "Error", MB_OK, IDI_HAND);

		return SMOOTH::Error;
	}

	if (format->bits != 16)
	{
		SMOOTH::MessageBox("Input files must be 16 bit for OggVorbis encoding!", "Error", MB_OK, IDI_HAND);

		return SMOOTH::Error;
	}

	ogg_stream_state	 os;
	ogg_page		 og;
	ogg_packet		 op;

	vorbis_info		 vi;
	vorbis_comment		 vc;
	vorbis_dsp_state	 vd;
	vorbis_block		 vb;

	srand(clock());

	vorbis_info_init(&vi);

	switch (currentConfig->vorbis_mode)
	{
		case 0:
			vorbis_encode_init_vbr(&vi, 2, format->rate, ((double) currentConfig->vorbis_quality) / 100);
			break;
		case 1:
			vorbis_encode_init(&vi, 2, format->rate, -1, currentConfig->vorbis_bitrate * 1000, -1);
			break;
	}

	vorbis_comment_init(&vc);
	vorbis_comment_add_tag(&vc, "COMMENT", "BonkEnc v0.5 <http://bonkenc.sf.net>");
	vorbis_analysis_init(&vd, &vi);
	vorbis_block_init(&vd, &vb);

	ogg_stream_init(&os, rand());

	{

		ogg_packet	 header;
		ogg_packet	 header_comm;
		ogg_packet	 header_code;


		vorbis_analysis_headerout(&vd, &vc, &header, &header_comm, &header_code);

		ogg_stream_packetin(&os, &header); /* automatically placed in its own page */

		ogg_stream_packetin(&os, &header_comm);
		ogg_stream_packetin(&os, &header_code);

		do
		{
			int result = ogg_stream_flush(&os, &og);

			if (result == 0) break;

			f_out->OutputData(og.header, og.header_len);
			f_out->OutputData(og.body, og.body_len);
		}
		while (true);
	}

	signed char	*samples = new signed char [samples_size * 2];

	int	 position = 0;
	int	 n_loops = (format->length + samples_size - 1) / samples_size;
	int	 lastpercent = 100;

	startticks = clock();

	for(int loop = 0; loop < n_loops; loop++)
	{
		int	 step = samples_size;

		if (position + step > format->length)
			step = format->length - position;

		if (format->order == BYTE_INTEL)
		{
			for(int i = 0; i < step; i++)
			{
				samples[2 * i] = f_in->InputNumber(1);
				samples[2 * i + 1] = f_in->InputNumber(1);
			}
		}
		else if (format->order == BYTE_RAW)
		{
			for(int i = 0; i < step; i++)
			{
				samples[2 * i + 1] = f_in->InputNumber(1);
				samples[2 * i] = f_in->InputNumber(1);
			}
		}

		for(int i = step; i < samples_size; i++)
		{
			samples[2 * i] = 0;
			samples[2 * i + 1] = 0;
		}

		position += step;

		float	**buffer = vorbis_analysis_buffer(&vd, samples_size / 2);

		for (int j = 0; j < samples_size / 2; j++)
		{
			buffer[0][j] = ((samples[j*4+1]<<8)|(0x00ff&(int)samples[j*4]))/32768.f;
			buffer[1][j] = ((samples[j*4+3]<<8)|(0x00ff&(int)samples[j*4+2]))/32768.f;
		}

		vorbis_analysis_wrote(&vd, step / 2);

		while (vorbis_analysis_blockout(&vd, &vb) == 1)
		{
			vorbis_analysis(&vb, NULL);
			vorbis_bitrate_addblock(&vb);

			while(vorbis_bitrate_flushpacket(&vd, &op))
			{
				ogg_stream_packetin(&os, &op);

				do
				{
					int	 result = ogg_stream_pageout(&os, &og);

					if (result == 0) break;

					f_out->OutputData(og.header, og.header_len);
					f_out->OutputData(og.body, og.body_len);

					if (ogg_page_eos(&og)) break;
				}
				while (true);
			}
		}

		progress->SetValue((int) ((position * 100.0 / format->length) * 10.0));

		if ((int) (position * 100.0 / format->length) != lastpercent)
		{
			lastpercent = (int) (position * 100.0 / format->length);

			edb_percent->SetText(SMOOTHString::IntToString(lastpercent).Append("%"));
		}

		ticks = clock() - startticks;

		ticks = (int) (ticks * ((1000.0 - ((position * 100.0 / format->length) * 10.0)) / ((position * 100.0 / format->length) * 10.0))) / 1000 + 1;

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

	vorbis_analysis_wrote(&vd, 0);

	while (vorbis_analysis_blockout(&vd, &vb) == 1)
	{
		vorbis_analysis(&vb, NULL);
		vorbis_bitrate_addblock(&vb);

		while(vorbis_bitrate_flushpacket(&vd, &op))
		{
			ogg_stream_packetin(&os, &op);

			do
			{
				int	 result = ogg_stream_pageout(&os, &og);

				if (result == 0) break;

				f_out->OutputData(og.header, og.header_len);
				f_out->OutputData(og.body, og.body_len);

				if (ogg_page_eos(&og)) break;
			}
			while (true);
		}
	}

	delete [] samples;

	ogg_stream_clear(&os);
	vorbis_block_clear(&vb);
	vorbis_dsp_clear(&vd);
	vorbis_comment_clear(&vc);
	vorbis_info_clear(&vi);
#endif

	return SMOOTH::Success;
}

SMOOTHInt bonkEnc::EncodeFAAC(SMOOTHInStream *f_in, SMOOTHOutStream *f_out, bonkFormatInfo *format)
{
	int			 startticks;
	int			 ticks;
	int			 lastticks = 0;

	unsigned long		 samples_size;
	unsigned long		 buffersize;
	long			 n_bytes = 0;
	faacEncHandle		 handle;
	faacEncConfigurationPtr	 config;

	if (format->bits != 16)
	{
		SMOOTH::MessageBox("Input files must be 16 bit for FAAC encoding!", "Error", MB_OK, IDI_HAND);

		return SMOOTH::Error;
	}

	if (format->channels > 2)
	{
		SMOOTH::MessageBox("BonkEnc does not support more than 2 channels!", "Error", MB_OK, IDI_HAND);

		return SMOOTH::Error;
	}

	handle = (faacEncHandle) faacEncOpen(format->rate, format->channels, &samples_size, &buffersize);

	config = (faacEncConfigurationPtr) faacEncGetCurrentConfiguration(handle);

	config->mpegVersion	= currentConfig->faac_mpegversion;
	config->aacObjectType	= currentConfig->faac_type;
	config->allowMidside	= currentConfig->faac_allowjs;
	config->useTns		= currentConfig->faac_usetns;
	config->bandWidth	= currentConfig->faac_bandwidth;
	config->bitRate		= currentConfig->faac_bitrate * 1000;

	faacEncSetConfiguration(handle, config);

	char		*outbuffer = new char [buffersize];
	signed short	*samples = new signed short [samples_size]; 

	int	 position = 0;
	int	 n_loops = (format->length + samples_size - 1) / samples_size;
	int	 lastpercent = 100;

	startticks = clock();

	for(int loop = 0; loop < n_loops; loop++)
	{
		int	 step = samples_size;

		if (position + step > format->length)
			step = format->length - position;

		if (format->order == BYTE_INTEL)
			for(int i = 0; i < step; i++)
				samples[i] = int16(f_in->InputNumberIntel(2));
		else if (format->order == BYTE_RAW)
			for(int i = 0; i < step; i++)
				samples[i] = int16(f_in->InputNumberRaw(2));

		for(unsigned int i = step; i < samples_size; i++)
			samples[i] = 0;

		position += step;

		n_bytes = faacEncEncode(handle, samples, step, outbuffer, buffersize);

		f_out->OutputData((void *) outbuffer, n_bytes);

		progress->SetValue((int) ((position * 100.0 / format->length) * 10.0));

		if ((int) (position * 100.0 / format->length) != lastpercent)
		{
			lastpercent = (int) (position * 100.0 / format->length);

			edb_percent->SetText(SMOOTHString::IntToString(lastpercent).Append("%"));
		}

		ticks = clock() - startticks;

		ticks = (int) (ticks * ((1000.0 - ((position * 100.0 / format->length) * 10.0)) / ((position * 100.0 / format->length) * 10.0))) / 1000 + 1;

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

	n_bytes = faacEncEncode(handle, NULL, 0, outbuffer, buffersize);

	f_out->OutputData((void *) outbuffer, n_bytes);

	delete [] outbuffer;
	delete [] samples;

	faacEncClose(handle);

	return SMOOTH::Success;
}
