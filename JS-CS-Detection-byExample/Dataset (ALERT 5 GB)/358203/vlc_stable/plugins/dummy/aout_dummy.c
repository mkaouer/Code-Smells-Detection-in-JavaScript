/*****************************************************************************
 * aout_dummy.c : dummy audio output plugin
 *****************************************************************************
 * Copyright (C) 2000 VideoLAN
 *
 * Authors:
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111, USA.
 *****************************************************************************/

/*****************************************************************************
 * Preamble
 *****************************************************************************/
#include "defs.h"

#include "config.h"
#include "common.h"                                     /* boolean_t, byte_t */
#include "threads.h"
#include "mtime.h"
#include "plugins.h"

#include "audio_output.h"                                   /* aout_thread_t */

#include "main.h"

/*****************************************************************************
 * vout_dummy_t: dummy video output method descriptor
 *****************************************************************************
 * This structure is part of the video output thread descriptor.
 * It describes the dummy specific properties of an output thread.
 *****************************************************************************/
typedef struct aout_sys_s
{


} aout_sys_t;

/*****************************************************************************
 * aout_DummyOpen: opens a dummy audio device
 *****************************************************************************/
int aout_DummyOpen( aout_thread_t *p_aout )
{
    /* Initialize some variables */
    p_aout->i_format = AOUT_FORMAT_DEFAULT;
    p_aout->i_channels = 1 + main_GetIntVariable( AOUT_STEREO_VAR, AOUT_STEREO_DEFAULT );
    p_aout->l_rate     = main_GetIntVariable( AOUT_RATE_VAR, AOUT_RATE_DEFAULT );

    return( 0 );
}

/*****************************************************************************
 * aout_DummyReset: fake reset
 *****************************************************************************/
int aout_DummyReset( aout_thread_t *p_aout )
{
    return( 0 );
}

/*****************************************************************************
 * aout_DummySetFormat: pretends to set the dsp output format
 *****************************************************************************/
int aout_DummySetFormat( aout_thread_t *p_aout )
{
    return( 0 );
}

/*****************************************************************************
 * aout_DummySetChannels: pretends to set stereo or mono mode
 *****************************************************************************/
int aout_DummySetChannels( aout_thread_t *p_aout )
{
    return( 0 );
}

/*****************************************************************************
 * aout_DummySetRate: pretends to set audio output rate
 *****************************************************************************/
int aout_DummySetRate( aout_thread_t *p_aout )
{
    return( 0 );
}

/*****************************************************************************
 * aout_DummyGetBufInfo: returns available bytes in buffer
 *****************************************************************************/
long aout_DummyGetBufInfo( aout_thread_t *p_aout, long l_buffer_limit )
{
    return( sizeof(s16) * l_buffer_limit + 1 ); /* value big enough to sleep */
}

/*****************************************************************************
 * aout_DummyPlaySamples: pretends to play a sound
 *****************************************************************************/
void aout_DummyPlaySamples( aout_thread_t *p_aout, byte_t *buffer, int i_size )
{
}

/*****************************************************************************
 * aout_DummyClose: closes the dummy audio device
 *****************************************************************************/
void aout_DummyClose( aout_thread_t *p_aout )
{
    ;
}

