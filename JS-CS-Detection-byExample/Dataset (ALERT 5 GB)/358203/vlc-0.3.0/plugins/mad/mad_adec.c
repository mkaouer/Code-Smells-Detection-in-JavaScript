/***************************************************************************
              mad_adec.c  -  description
                -------------------
    Plugin Module definition for using libmad audio decoder in vlc. The
    libmad codec uses integer arithmic only. This makes it suitable for using
    it on architectures without a hardware FPU unit, such as the StrongArm
    CPU.

    begin                : Mon Nov 5 2001
    copyright            : (C) 2001 by Jean-Paul Saman
    email                : jpsaman@wxs.nl
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

/*****************************************************************************
 * Preamble
 *****************************************************************************/
#include <stdlib.h>                                      /* malloc(), free() */
#include <string.h>                                              /* strdup() */

#include <videolan/vlc.h>

#include "audio_output.h"

#include "stream_control.h"
#include "input_ext-dec.h"

#include "debug.h"

/*****************************************************************************
 * Libmad include files                                                      *
 *****************************************************************************/
#include <mad.h>
#include "mad_adec.h"
#include "mad_libmad.h"

/*****************************************************************************
 * Local prototypes
 *****************************************************************************/
static int  decoder_Probe  ( u8 * );
static int  decoder_Run    ( decoder_config_t * );
static int  InitThread     ( mad_adec_thread_t * p_mad_adec );
static void EndThread      ( mad_adec_thread_t * p_mad_adec );

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
    SET_DESCRIPTION( "Libmad MPEG 1/2/3 audio decoder library" )
    ADD_CAPABILITY( DECODER, 50 )
    ADD_SHORTCUT( "mad" )
MODULE_INIT_STOP

MODULE_ACTIVATE_START
    _M( adec_getfunctions )( &p_module->p_functions->dec );
MODULE_ACTIVATE_STOP

MODULE_DEACTIVATE_START
MODULE_DEACTIVATE_STOP

/*****************************************************************************
 * decoder_Probe: probe the decoder and return score
 *****************************************************************************
 * Tries to launch a decoder and return score so that the interface is able
 * to chose.
 *****************************************************************************/
static int decoder_Probe( u8 *pi_type )
{
    if( *pi_type == MPEG1_AUDIO_ES || *pi_type == MPEG2_AUDIO_ES )
    {
        return 0;
    }

    return -1;
}

/*****************************************************************************
 * decoder_Run: this function is called just after the thread is created
 *****************************************************************************/
static int decoder_Run ( decoder_config_t * p_config )
{
    mad_adec_thread_t *   p_mad_adec;

    intf_ErrMsg( "mad_adec debug: mad_adec thread launched, initializing" );

    /* Allocate the memory needed to store the thread's structure */
    p_mad_adec = (mad_adec_thread_t *) malloc(sizeof(mad_adec_thread_t));

    if (p_mad_adec == NULL)
    {
        intf_ErrMsg ( "mad_adec error: not enough memory "
                      "for decoder_Run() to allocate p_mad_adec" );
        DecoderError( p_config->p_decoder_fifo );
        return( -1 );
    }

    /*
     * Initialize the thread properties
     */
    p_mad_adec->p_config = p_config;
    p_mad_adec->p_fifo = p_mad_adec->p_config->p_decoder_fifo;
    if( InitThread( p_mad_adec ) )
    {
        intf_ErrMsg( "mad_adec error: could not initialize thread" );
        DecoderError( p_config->p_decoder_fifo );
        free( p_mad_adec );
        return( -1 );
    }

    /* mad decoder thread's main loop */
    while ((!p_mad_adec->p_fifo->b_die) && (!p_mad_adec->p_fifo->b_error))
    {
        intf_ErrMsg( "mad_adec: starting libmad decoder" );
        if (mad_decoder_run(p_mad_adec->libmad_decoder, MAD_DECODER_MODE_SYNC)==-1)
        {
            intf_ErrMsg( "mad_adec error: libmad decoder returns abnormally");
            DecoderError( p_mad_adec->p_fifo );
            EndThread(p_mad_adec);
            return( -1 );
        }
    }

    /* If b_error is set, the mad decoder thread enters the error loop */
    if (p_mad_adec->p_fifo->b_error)
    {
        DecoderError( p_mad_adec->p_fifo );
    }

    /* End of the mad decoder thread */
    EndThread (p_mad_adec);

    return( 0 );
}

/*****************************************************************************
 * InitThread: initialize data before entering main loop
 *****************************************************************************/
static int InitThread( mad_adec_thread_t * p_mad_adec )
{
    decoder_fifo_t * p_fifo = p_mad_adec->p_fifo;

    /*
     * Properties of audio for libmad
     */
        
    /* Initialize the libmad decoder structures */
    p_mad_adec->libmad_decoder = (struct mad_decoder*) malloc(sizeof(struct mad_decoder));
    if (p_mad_adec->libmad_decoder == NULL)
    {
        intf_ErrMsg ( "mad_adec error: not enough memory "
                      "for decoder_InitThread() to allocate p_mad_adec->libmad_decder" );
        return -1;
    }
    p_mad_adec->i_current_pts = p_mad_adec->i_next_pts = 0;

    mad_decoder_init( p_mad_adec->libmad_decoder,
                          p_mad_adec,         /* vlc's thread structure and p_fifo playbuffer */
                      libmad_input,          /* input_func */
                      0,                /* header_func */
                      0,                /* filter */
                      libmad_output,         /* output_func */
                      0,                  /* error */
                      0);                    /* message */

    mad_decoder_options(p_mad_adec->libmad_decoder, MAD_OPTION_IGNORECRC);
    mad_timer_reset(&p_mad_adec->libmad_timer);

    /*
     * Initialize the output properties
     */
    p_mad_adec->p_aout_fifo = NULL;

    /*
     * Initialize the input properties
     */
    /* Get the first data packet. */
    vlc_mutex_lock( &p_fifo->data_lock );
    while ( p_fifo->p_first == NULL )
    {
        if ( p_fifo->b_die )
        {
            vlc_mutex_unlock( &p_fifo->data_lock );
            return( -1 );
        }
        vlc_cond_wait( &p_fifo->data_wait, &p_fifo->data_lock );
    }
    vlc_mutex_unlock( &p_fifo->data_lock );
    p_mad_adec->p_data = p_fifo->p_first->p_first;

    intf_ErrMsg("mad_adec debug: mad decoder thread %p initialized", p_mad_adec);

    return( 0 );
}

/*****************************************************************************
 * EndThread : libmad decoder thread destruction
 *****************************************************************************/
static void EndThread (mad_adec_thread_t * p_mad_adec)
{
    intf_ErrMsg ("mad_adec debug: destroying mad decoder thread %p", p_mad_adec);

    /* If the audio output fifo was created, we destroy it */
    if (p_mad_adec->p_aout_fifo != NULL)
    {
        aout_DestroyFifo (p_mad_adec->p_aout_fifo);

        /* Make sure the output thread leaves the NextFrame() function */
        vlc_mutex_lock (&(p_mad_adec->p_aout_fifo->data_lock));
        vlc_cond_signal (&(p_mad_adec->p_aout_fifo->data_wait));
        vlc_mutex_unlock (&(p_mad_adec->p_aout_fifo->data_lock));
    }

    /* mad_decoder_finish releases the memory allocated inside the struct */
    mad_decoder_finish( p_mad_adec->libmad_decoder );

    /* Unlock the modules, p_mad_adec->p_config is released by the decoder subsystem  */
    free( p_mad_adec->libmad_decoder );
    free( p_mad_adec );

    intf_ErrMsg ("mad_adec debug: mad decoder thread %p destroyed", p_mad_adec);
}

