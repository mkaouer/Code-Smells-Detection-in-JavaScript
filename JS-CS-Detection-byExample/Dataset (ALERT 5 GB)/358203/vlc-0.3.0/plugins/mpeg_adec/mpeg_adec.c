/*****************************************************************************
 * mpeg_adec.c: MPEG audio decoder thread
 *****************************************************************************
 * Copyright (C) 1999-2001 VideoLAN
 * $Id: mpeg_adec.c,v 1.21 2002/02/24 22:06:50 sam Exp $
 *
 * Authors: Michel Kaempf <maxx@via.ecp.fr>
 *          Michel Lespinasse <walken@via.ecp.fr>
 *          Samuel Hocevar <sam@via.ecp.fr>
 *          Cyril Deguet <asmax@via.ecp.fr>
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
#include <stdlib.h>                                      /* malloc(), free() */
#include <string.h>

#include <videolan/vlc.h>

#include "audio_output.h"               /* aout_fifo_t (for audio_decoder.h) */

#include "stream_control.h"
#include "input_ext-dec.h"

#include "mpeg_adec_generic.h"
#include "mpeg_adec.h"

#define ADEC_FRAME_SIZE (2*1152)

/*****************************************************************************
 * Local Prototypes
 *****************************************************************************/
static int   decoder_Probe ( u8 * );
static int   decoder_Run   ( decoder_config_t * );
static void  EndThread     ( adec_thread_t * );
static void  DecodeThread  ( adec_thread_t * );

/*****************************************************************************
 * Capabilities
 *****************************************************************************/
void _M( adec_getfunctions )( function_list_t * p_function_list )
{
    p_function_list->functions.dec.pf_probe = decoder_Probe;
    p_function_list->functions.dec.pf_run   = decoder_Run;
}

/*****************************************************************************
 * Build configuration tree.
 *****************************************************************************/
MODULE_CONFIG_START
MODULE_CONFIG_STOP

MODULE_INIT_START
    SET_DESCRIPTION( "Mpeg I layer 1/2 audio decoder" )
    ADD_CAPABILITY( DECODER, 100 )
    ADD_REQUIREMENT( FPU )
    ADD_SHORTCUT( "builtin" )
MODULE_INIT_STOP

MODULE_ACTIVATE_START
    _M( adec_getfunctions )( &p_module->p_functions->dec );
MODULE_ACTIVATE_STOP

MODULE_DEACTIVATE_START
MODULE_DEACTIVATE_STOP

/*****************************************************************************
 * decoder_Probe: probe the decoder and return score
 *****************************************************************************/
static int decoder_Probe( u8 *pi_type )
{
    return( ( *pi_type == MPEG1_AUDIO_ES
               || *pi_type == MPEG2_AUDIO_ES ) ? 0 : -1 );
}

/*****************************************************************************
 * decoder_Run: initialize, go inside main loop, detroy
 *****************************************************************************/
static int decoder_Run ( decoder_config_t * p_config )
{
    adec_thread_t   * p_adec;
    
    /* Allocate the memory needed to store the thread's structure */
    if ( (p_adec = (adec_thread_t *)malloc (sizeof(adec_thread_t))) == NULL ) 
    {
        intf_ErrMsg ( "adec error: not enough memory for"
                      " adec_CreateThread() to create the new thread" );
        DecoderError( p_config->p_decoder_fifo );
        return 0;
    }
    
    /*
     * Initialize the thread properties
     */
    p_adec->p_config = p_config;
    p_adec->p_fifo = p_config->p_decoder_fifo;

    /* 
     * Initilize the banks
     */
    p_adec->bank_0.actual = p_adec->bank_0.v1;
    p_adec->bank_0.pos = 0;
    p_adec->bank_1.actual = p_adec->bank_1.v1;
    p_adec->bank_1.pos = 0;
    
    /*
     * Initialize bit stream 
     */
    InitBitstream( &p_adec->bit_stream, p_adec->p_config->p_decoder_fifo,
                   NULL, NULL );

    /* We do not create the audio output fifo now, but
       it will be created when the first frame is received */
    p_adec->p_aout_fifo = NULL;

    p_adec->i_sync = 0;

    /* Audio decoder thread's main loop */
    while( (!p_adec->p_fifo->b_die) && (!p_adec->p_fifo->b_error) )
    {
        DecodeThread( p_adec );
    }
    
    /* If b_error is set, the audio decoder thread enters the error loop */
    if( p_adec->p_fifo->b_error ) 
    {
        DecoderError( p_adec->p_fifo );
    }

    /* End of the audio decoder thread */
    EndThread( p_adec );

    return( 0 );
}

/*
 * Following functions are local to this module
 */

/*****************************************************************************
 * DecodeThread: decodes a mpeg frame
 *****************************************************************************/
static void DecodeThread( adec_thread_t * p_adec )
{
    s16 *p_buffer;
    adec_sync_info_t sync_info;

    if( ! adec_SyncFrame (p_adec, &sync_info) )
    {
        
        /* TODO: check if audio type has changed */
        
        /* Create the output fifo if it doesn't exist yet */
        if( p_adec->p_aout_fifo == NULL )
        {
            int i_channels;
            
            if( p_main->b_stereo )
            {
                intf_WarnMsg( 4, "adec info: setting stereo output" );
                i_channels = 2;
            }
            else if( sync_info.b_stereo )
            {
                i_channels = 2;
            }
            else
            {
                i_channels = 1;
            }
            p_adec->p_aout_fifo = aout_CreateFifo( AOUT_FIFO_PCM, i_channels,
                    sync_info.sample_rate, ADEC_FRAME_SIZE, NULL );
            if( p_adec->p_aout_fifo == NULL)
            {
                intf_ErrMsg( "adec error: failed to create aout fifo" );
                p_adec->p_fifo->b_error = 1;
                return;
            }
        }

        p_adec->i_sync = 1;

        p_buffer = ((s16 *)p_adec->p_aout_fifo->buffer)
                    + (p_adec->p_aout_fifo->i_end_frame * ADEC_FRAME_SIZE);

        CurrentPTS( &p_adec->bit_stream,
            &p_adec->p_aout_fifo->date[p_adec->p_aout_fifo->i_end_frame],
            NULL );
        if( !p_adec->p_aout_fifo->date[p_adec->p_aout_fifo->i_end_frame] )
        {
            p_adec->p_aout_fifo->date[p_adec->p_aout_fifo->i_end_frame] =
                LAST_MDATE;
        }

        if( adec_DecodeFrame (p_adec, p_buffer) )
        {
            /* Ouch, failed decoding... We'll have to resync */
            p_adec->i_sync = 0;
        }
        else
        {
            vlc_mutex_lock (&p_adec->p_aout_fifo->data_lock);
            p_adec->p_aout_fifo->i_end_frame =
                (p_adec->p_aout_fifo->i_end_frame + 1) & AOUT_FIFO_SIZE;
            vlc_cond_signal (&p_adec->p_aout_fifo->data_wait);
            vlc_mutex_unlock (&p_adec->p_aout_fifo->data_lock);
        }
    }
}

/*****************************************************************************
 * EndThread : audio decoder thread destruction
 *****************************************************************************
 * This function is called when the thread ends after a sucessful
 * initialization.
 *****************************************************************************/
static void EndThread ( adec_thread_t *p_adec )
{
    /* If the audio output fifo was created, we destroy it */
    if ( p_adec->p_aout_fifo != NULL ) 
    {
        aout_DestroyFifo ( p_adec->p_aout_fifo );

        /* Make sure the output thread leaves the NextFrame() function */
        vlc_mutex_lock (&(p_adec->p_aout_fifo->data_lock));
        vlc_cond_signal (&(p_adec->p_aout_fifo->data_wait));
        vlc_mutex_unlock (&(p_adec->p_aout_fifo->data_lock));
    }
    /* Destroy descriptor */
    free( p_adec );
}

