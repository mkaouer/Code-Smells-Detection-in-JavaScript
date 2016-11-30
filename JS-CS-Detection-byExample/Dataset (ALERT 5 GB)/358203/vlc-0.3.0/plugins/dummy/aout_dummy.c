/*****************************************************************************
 * aout_dummy.c : dummy audio output plugin
 *****************************************************************************
 * Copyright (C) 2000, 2001 VideoLAN
 * $Id: aout_dummy.c,v 1.20 2002/02/24 22:06:50 sam Exp $
 *
 * Authors: Samuel Hocevar <sam@zoy.org>
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
#include <string.h>

#include <videolan/vlc.h>

#include "audio_output.h"                                   /* aout_thread_t */

/*****************************************************************************
 * vout_dummy_t: dummy video output method descriptor
 *****************************************************************************
 * This structure is part of the video output thread descriptor.
 * It describes the dummy specific properties of an output thread.
 *****************************************************************************/
typedef struct aout_sys_s
{
    /* Prevent malloc(0) */
    int i_dummy;

} aout_sys_t;

/*****************************************************************************
 * Local prototypes.
 *****************************************************************************/
static int     aout_Open        ( aout_thread_t *p_aout );
static int     aout_SetFormat   ( aout_thread_t *p_aout );
static int     aout_GetBufInfo  ( aout_thread_t *p_aout, int i_buffer_info );
static void    aout_Play        ( aout_thread_t *p_aout,
                                  byte_t *buffer, int i_size );
static void    aout_Close       ( aout_thread_t *p_aout );

/*****************************************************************************
 * Functions exported as capabilities. They are declared as static so that
 * we don't pollute the namespace too much.
 *****************************************************************************/
void _M( aout_getfunctions )( function_list_t * p_function_list )
{
    p_function_list->functions.aout.pf_open = aout_Open;
    p_function_list->functions.aout.pf_setformat = aout_SetFormat;
    p_function_list->functions.aout.pf_getbufinfo = aout_GetBufInfo;
    p_function_list->functions.aout.pf_play = aout_Play;
    p_function_list->functions.aout.pf_close = aout_Close;
}

/*****************************************************************************
 * aout_Open: opens a dummy audio device
 *****************************************************************************/
static int aout_Open( aout_thread_t *p_aout )
{
    return( 0 );
}

/*****************************************************************************
 * aout_SetFormat: pretends to set the dsp output format
 *****************************************************************************/
static int aout_SetFormat( aout_thread_t *p_aout )
{
    return( 0 );
}

/*****************************************************************************
 * aout_GetBufInfo: returns available bytes in buffer
 *****************************************************************************/
static int aout_GetBufInfo( aout_thread_t *p_aout, int i_buffer_limit )
{
    return( sizeof(s16) * i_buffer_limit + 1 ); /* value big enough to sleep */
}

/*****************************************************************************
 * aout_Play: pretends to play a sound
 *****************************************************************************/
static void aout_Play( aout_thread_t *p_aout, byte_t *buffer, int i_size )
{
    ;
}

/*****************************************************************************
 * aout_Close: closes the dummy audio device
 *****************************************************************************/
static void aout_Close( aout_thread_t *p_aout )
{
    ;
}

