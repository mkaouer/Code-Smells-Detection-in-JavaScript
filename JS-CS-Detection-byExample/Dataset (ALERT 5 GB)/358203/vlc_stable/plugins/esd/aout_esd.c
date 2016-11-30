/*****************************************************************************
 * aout_esd.c : Esound functions library
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

/* TODO:
 *
 * - use the libesd function to get latency when it's not buggy anymore
 *
 */

/*****************************************************************************
 * Preamble
 *****************************************************************************/
#include "defs.h"

#include <errno.h>                                                 /* ENOMEM */
#include <fcntl.h>                                       /* open(), O_WRONLY */
#include <string.h>                                            /* strerror() */
#include <unistd.h>                                      /* write(), close() */
#include <stdio.h>                                           /* "intf_msg.h" */
#include <stdlib.h>                            /* calloc(), malloc(), free() */

#include <esd.h>

#include "config.h"
#include "common.h"                                     /* boolean_t, byte_t */
#include "threads.h"
#include "mtime.h"
#include "plugins.h"

#include "audio_output.h"                                   /* aout_thread_t */

#include "intf_msg.h"                        /* intf_DbgMsg(), intf_ErrMsg() */
#include "main.h"

/*****************************************************************************
 * aout_sys_t: esd audio output method descriptor
 *****************************************************************************
 * This structure is part of the audio output thread descriptor.
 * It describes some esd specific variables.
 *****************************************************************************/
typedef struct aout_sys_s
{
    esd_format_t esd_format;

} aout_sys_t;

/*****************************************************************************
 * aout_EsdOpen: opens an esd socket
 *****************************************************************************/
int aout_EsdOpen( aout_thread_t *p_aout )
{
    /* mpg123 does it this way */
    int i_bits = ESD_BITS16;
    int i_mode = ESD_STREAM;
    int i_func = ESD_PLAY;

    /* Allocate structure */
    p_aout->p_sys = malloc( sizeof( aout_sys_t ) );
    if( p_aout->p_sys == NULL )
    {
        intf_ErrMsg("error: %s\n", strerror(ENOMEM) );
        return( 1 );
    }

    /* Initialize some variables */
    p_aout->i_format = AOUT_FORMAT_DEFAULT;
    p_aout->i_channels = 1 + main_GetIntVariable( AOUT_STEREO_VAR, AOUT_STEREO_DEFAULT );
    p_aout->l_rate     = main_GetIntVariable( AOUT_RATE_VAR, AOUT_RATE_DEFAULT );

    i_bits = ESD_BITS16;
    i_mode = ESD_STREAM;
    i_func = ESD_PLAY;
    p_aout->p_sys->esd_format = (i_bits | i_mode | i_func) & (~ESD_MASK_CHAN);

    if( p_aout->i_channels == 1 )
        p_aout->p_sys->esd_format |= ESD_MONO;
    else
        p_aout->p_sys->esd_format |= ESD_STEREO;

    /* open a socket for playing a stream
     * and try to open /dev/dsp if there's no EsounD */
    if ( (p_aout->i_fd
            = esd_play_stream_fallback(p_aout->p_sys->esd_format,
                p_aout->l_rate, NULL, "vlc")) < 0 )
    {
        intf_ErrMsg( "aout error: can't open esound socket"
                     " (format 0x%08x at %ld Hz)\n",
                     p_aout->p_sys->esd_format, p_aout->l_rate );
        return( -1 );
    }

    return( 0 );
}

/*****************************************************************************
 * aout_EsdReset: resets the dsp
 *****************************************************************************/
int aout_EsdReset( aout_thread_t *p_aout )
{
    return( 0 );
}

/*****************************************************************************
 * aout_EsdSetFormat: sets the dsp output format
 *****************************************************************************/
int aout_EsdSetFormat( aout_thread_t *p_aout )
{
    return( 0 );
}

/*****************************************************************************
 * aout_EsdSetChannels: sets the dsp's stereo or mono mode
 *****************************************************************************/
int aout_EsdSetChannels( aout_thread_t *p_aout )
{
    return( 0 );
}

/*****************************************************************************
 * aout_EsdSetRate: sets the dsp's audio output rate
 *****************************************************************************/
int aout_EsdSetRate( aout_thread_t *p_aout )
{
    return( 0 );
}

/*****************************************************************************
 * aout_EsdGetBufInfo: buffer status query
 *****************************************************************************/
long aout_EsdGetBufInfo( aout_thread_t *p_aout, long l_buffer_limit )
{
    /* arbitrary value that should be changed */
    return( l_buffer_limit );
}

/*****************************************************************************
 * aout_EsdPlaySamples: plays a sound samples buffer
 *****************************************************************************
 * This function writes a buffer of i_length bytes in the dsp
 *****************************************************************************/
void aout_EsdPlaySamples( aout_thread_t *p_aout, byte_t *buffer, int i_size )
{
    int amount;

    if (p_aout->p_sys->esd_format & ESD_STEREO)
    {
        if (p_aout->p_sys->esd_format & ESD_BITS16)
            amount = (44100 * (ESD_BUF_SIZE + 64)) / p_aout->l_rate;
        else
            amount = (44100 * (ESD_BUF_SIZE + 128)) / p_aout->l_rate;
    }
    else
    {
        if (p_aout->p_sys->esd_format & ESD_BITS16)
            amount = (2 * 44100 * (ESD_BUF_SIZE + 128)) / p_aout->l_rate;
        else
            amount = (2 * 44100 * (ESD_BUF_SIZE + 256)) / p_aout->l_rate;
    }

    intf_DbgMsg( "aout: latency is %i\n", amount );

    write( p_aout->i_fd, buffer, i_size );
}

/*****************************************************************************
 * aout_EsdClose: closes the dsp audio device
 *****************************************************************************/
void aout_EsdClose( aout_thread_t *p_aout )
{
    close( p_aout->i_fd );
}

