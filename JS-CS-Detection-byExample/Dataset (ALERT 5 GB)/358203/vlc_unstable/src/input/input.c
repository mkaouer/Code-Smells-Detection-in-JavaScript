/*****************************************************************************
 * input.c: input thread
 * Read an MPEG2 stream, demultiplex and parse it before sending it to
 * decoders.
 *****************************************************************************
 * Copyright (C) 1998, 1999, 2000 VideoLAN
 * $Id: input.c,v 1.73 2001/01/24 19:05:55 massiot Exp $
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

#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>

#ifdef STATS
#   include <sys/times.h>
#endif

#include "config.h"
#include "common.h"
#include "threads.h"
#include "mtime.h"

#include "intf_msg.h"

#include "stream_control.h"
#include "input_ext-intf.h"
#include "input_ext-dec.h"

#include "input.h"

/*****************************************************************************
 * Local prototypes
 *****************************************************************************/
static void RunThread   ( input_thread_t *p_input );
static void InitThread  ( input_thread_t *p_input );
static void ErrorThread ( input_thread_t *p_input );
static void EndThread   ( input_thread_t *p_input );
static void NetworkOpen ( input_thread_t *p_input );
static void FileOpen    ( input_thread_t *p_input );
static void DvdOpen     ( input_thread_t *p_input );

/*****************************************************************************
 * input_CreateThread: creates a new input thread
 *****************************************************************************
 * This function creates a new input, and returns a pointer
 * to its description. On error, it returns NULL.
 * If pi_status is NULL, then the function will block until the thread is ready.
 * If not, it will be updated using one of the THREAD_* constants.
 *****************************************************************************/
input_thread_t *input_CreateThread ( input_config_t * p_config, int *pi_status )
{
    input_thread_t *    p_input;                        /* thread descriptor */
    int                 i_status;                           /* thread status */

    /* Allocate descriptor */
    p_input = (input_thread_t *)malloc( sizeof(input_thread_t) );
    if( p_input == NULL )
    {
        intf_ErrMsg( "input error: can't allocate input thread (%s)",
                     strerror(errno) );
        free( p_config );
        return( NULL );
    }

    /* Initialize thread properties */
    p_input->b_die              = 0;
    p_input->b_error            = 0;
    /* I have never understood that stuff --Meuuh */
    p_input->pi_status          = (pi_status != NULL) ? pi_status : &i_status;
    *p_input->pi_status         = THREAD_CREATE;
    p_input->p_config = p_config;

    /* Initialize stream description */
    p_input->stream.i_es_number = 0;
    p_input->stream.i_selected_es_number = 0;
    p_input->stream.i_pgrm_number = 0;

    /* Initialize stream control properties. */
    p_input->stream.control.i_status = PLAYING_S;
    p_input->stream.control.i_rate = DEFAULT_RATE;
    p_input->stream.control.b_mute = 0;
    p_input->stream.control.b_bw = 0;

    /* Create thread and set locks. */
    vlc_mutex_init( &p_input->stream.stream_lock );
    vlc_mutex_init( &p_input->stream.control.control_lock );
    if( vlc_thread_create( &p_input->thread_id, "input", (void *) RunThread,
                           (void *) p_input ) )
    {
        intf_ErrMsg( "input error: can't create input thread (%s)",
                     strerror(errno) );
        free( p_input );
        free( p_config );
        return( NULL );
    }

    /* If status is NULL, wait until the thread is created */
    if( pi_status == NULL )
    {
        do
        {
            msleep( THREAD_SLEEP );
        } while( (i_status != THREAD_READY) && (i_status != THREAD_ERROR)
                && (i_status != THREAD_FATAL) );
        if( i_status != THREAD_READY )
        {
            return( NULL );
        }
    }
    return( p_input );
}

/*****************************************************************************
 * input_DestroyThread: mark an input thread as zombie
 *****************************************************************************
 * This function should not return until the thread is effectively cancelled.
 *****************************************************************************/
void input_DestroyThread( input_thread_t *p_input, int *pi_status )
{
    int         i_status;                                   /* thread status */

    /* Set status */
    p_input->pi_status = (pi_status != NULL) ? pi_status : &i_status;
    *p_input->pi_status = THREAD_DESTROY;

    /* Request thread destruction */
    p_input->b_die = 1;

    /* If status is NULL, wait until thread has been destroyed */
    if( pi_status == NULL )
    {
        do
        {
            msleep( THREAD_SLEEP );
        } while ( (i_status != THREAD_OVER) && (i_status != THREAD_ERROR)
                  && (i_status != THREAD_FATAL) );
    }
}

/*****************************************************************************
 * RunThread: main thread loop
 *****************************************************************************
 * Thread in charge of processing the network packets and demultiplexing.
 *****************************************************************************/
static void RunThread( input_thread_t *p_input )
{
    data_packet_t *         pp_packets[INPUT_READ_ONCE];
    int                     i_error, i;

    InitThread( p_input );

    while( !p_input->b_die && !p_input->b_error )
    {
#ifdef STATS
        p_input->c_loops++;
#endif

        vlc_mutex_lock( &p_input->stream.control.control_lock );
        if( p_input->stream.control.i_status == BACKWARD_S
             && p_input->p_plugin->pf_rewind != NULL )
        {
            p_input->p_plugin->pf_rewind( p_input );
            /* FIXME: probably don't do it every loop, but when ? */
        }
        vlc_mutex_unlock( &p_input->stream.control.control_lock );

        i_error = p_input->p_plugin->pf_read( p_input, pp_packets );

        /* Demultiplex read packets. */
        for( i = 0; i < INPUT_READ_ONCE && pp_packets[i] != NULL; i++ )
        {
            p_input->p_plugin->pf_demux( p_input, pp_packets[i] );
        }

        if( i_error )
        {
            if( i_error == 1 )
            {
                /* End of file */
                intf_WarnMsg( 1, "End of file reached" );
                /* FIXME: don't treat that as an error */
            }
            p_input->b_error = 1;
        }
    }

    if( p_input->b_error )
    {
        ErrorThread( p_input );
    }

    EndThread( p_input );
    intf_DbgMsg("Thread end");
}

/*****************************************************************************
 * InitThread: init the input thread
 *****************************************************************************/
input_capabilities_t * PSKludge( void );
input_capabilities_t * DVDKludge( void );
static void InitThread( input_thread_t * p_input )
{
    /* Initialize default settings for spawned decoders */
    p_input->p_default_aout     = p_input->p_config->p_default_aout;
    p_input->p_default_vout     = p_input->p_config->p_default_vout;

#ifdef STATS
    /* Initialize statistics */
    p_input->c_loops                    = 0;
    p_input->c_bytes                    = 0;
    p_input->c_payload_bytes            = 0;
    p_input->c_packets_read             = 0;
    p_input->c_packets_trashed          = 0;
#endif

    /* Use the appropriate input method */
    switch( p_input->p_config->i_method )
    {
    case INPUT_METHOD_FILE:                                  /* file methods */
        FileOpen( p_input );
        /* Probe plugin (FIXME: load plugins before & write this) */
        p_input->p_plugin = PSKludge();
       break;
    case INPUT_METHOD_DVD:                                     /* DVD method */
        DvdOpen( p_input );
        /* DVD plugin */
        p_input->p_plugin = DVDKludge();
        break;
    case INPUT_METHOD_VLAN_BCAST:                     /* vlan network method */
/*        if( !p_main->b_vlans )
        {
            intf_ErrMsg("input error: vlans are not activated");
            free( p_input );
            return( NULL );
        } */ /* la-lala */
        /* ... pass through */
    case INPUT_METHOD_UCAST:                              /* network methods */
    case INPUT_METHOD_MCAST:
    case INPUT_METHOD_BCAST:
        NetworkOpen( p_input );
        break;
#ifdef DEBUG
    default:
        intf_ErrMsg( "input error: unknow method 0x%.4x",
                     p_input->p_config->i_method );
        free( p_input->p_config );
        p_input->b_error = 1;
        break;
#endif
    }

    free( p_input->p_config );

    if( !p_input->b_error )
    {
        p_input->p_plugin->pf_init( p_input );
    }

    *p_input->pi_status = THREAD_READY;
}

/*****************************************************************************
 * ErrorThread: RunThread() error loop
 *****************************************************************************
 * This function is called when an error occured during thread main's loop.
 *****************************************************************************/
static void ErrorThread( input_thread_t *p_input )
{
    while( !p_input->b_die )
    {
        /* Sleep a while */
        msleep( INPUT_IDLE_SLEEP );
    }
}

/*****************************************************************************
 * EndThread: end the input thread
 *****************************************************************************/
static void EndThread( input_thread_t * p_input )
{
    int *       pi_status;                                  /* thread status */

    /* Store status */
    pi_status = p_input->pi_status;
    *pi_status = THREAD_END;

#ifdef STATS
    {
        struct tms cpu_usage;
        times( &cpu_usage );

        intf_Msg("input stats: cpu usage (user: %d, system: %d)",
                 cpu_usage.tms_utime, cpu_usage.tms_stime);
    }
#endif

    /* Free all ES and destroy all decoder threads */
    input_EndStream( p_input );

    /* Free demultiplexer's data */
    p_input->p_plugin->pf_end( p_input );
    free( p_input->p_plugin );

    /* Destroy Mutex locks */
    vlc_mutex_destroy( &p_input->stream.control.control_lock );
    vlc_mutex_destroy( &p_input->stream.stream_lock );
    
    /* Free input structure */
    free( p_input );

    /* Update status */
    *pi_status = THREAD_OVER;
}

/*****************************************************************************
 * NetworkOpen : open a network socket descriptor
 *****************************************************************************/
static void NetworkOpen( input_thread_t * p_input )
{
    /* straight copy & paste of input_network.c of input-I */

    /* We cannot rewind nor lseek() */
    p_input->stream.b_seekable = 0;
    /* We cannot control the pace */
    p_input->stream.b_pace_control = 0;
}

/*****************************************************************************
 * FileOpen : open a file descriptor
 *****************************************************************************/
static void FileOpen( input_thread_t * p_input )
{
    struct stat         stat_info;

#define p_config    p_input->p_config

    if( stat( p_config->p_source, &stat_info ) == (-1) )
    {
        intf_ErrMsg( "input error: cannot stat() file `%s' (%s)",
                     p_config->p_source, strerror(errno));
        p_input->b_error = 1;
        return;
    }

    vlc_mutex_lock( &p_input->stream.stream_lock );

    /* If we are here we can control the pace... */
    p_input->stream.b_pace_control = 1;

    if( S_ISREG(stat_info.st_mode) || S_ISCHR(stat_info.st_mode)
         || S_ISBLK(stat_info.st_mode) )
    {
        p_input->stream.b_seekable = 1;
        p_input->stream.i_size = stat_info.st_size;
    }
    else if( S_ISFIFO(stat_info.st_mode) || S_ISSOCK(stat_info.st_mode) )
    {
        p_input->stream.b_seekable = 0;
        p_input->stream.i_size = 0;
    }
    else
    {
        vlc_mutex_unlock( &p_input->stream.stream_lock );
        intf_ErrMsg( "input error: unknown file type for `%s'",
                     p_config->p_source );
        p_input->b_error = 1;
        return;
    }

    p_input->stream.i_tell = 0;
    vlc_mutex_unlock( &p_input->stream.stream_lock );

    intf_Msg( "input: opening file %s", p_config->p_source );
    if( (p_input->i_handle = open( p_config->p_source,
                                   /*O_NONBLOCK | O_LARGEFILE*/0 )) == (-1) )
    {
        intf_ErrMsg( "input error: cannot open file (%s)", strerror(errno) );
        p_input->b_error = 1;
        return;
    }

#undef p_config
}

/*****************************************************************************
 * DvdOpen : open the dvd device
 *****************************************************************************/
static void DvdOpen( input_thread_t * p_input )
{
    intf_Msg( "input: opening DVD %s", p_input->p_config->p_source );
    if( (p_input->i_handle = open( p_input->p_config->p_source,
                                O_RDONLY| O_NONBLOCK |O_LARGEFILE )) == (-1) )
    {
        intf_ErrMsg( "input error: cannot open device (%s)", strerror(errno) );
        p_input->b_error = 1;
        return;
    }

    vlc_mutex_lock( &p_input->stream.stream_lock );

    p_input->stream.b_pace_control = 1;
    p_input->stream.b_seekable = 1;
    p_input->stream.i_size = 0;
    p_input->stream.i_tell = 0;

    vlc_mutex_unlock( &p_input->stream.stream_lock );
}
