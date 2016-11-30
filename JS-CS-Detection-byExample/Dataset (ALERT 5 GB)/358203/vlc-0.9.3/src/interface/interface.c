/*****************************************************************************
 * interface.c: interface access for other threads
 * This library provides basic functions for threads to interact with user
 * interface, such as command line.
 *****************************************************************************
 * Copyright (C) 1998-2007 the VideoLAN team
 * $Id: c4b031e5fa284eca5f4fc53bab48ef4b24f64559 $
 *
 * Authors: Vincent Seguin <seguin@via.ecp.fr>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

/**
 *   \file
 *   This file contains functions related to interface management
 */


/*****************************************************************************
 * Preamble
 *****************************************************************************/

#ifdef HAVE_CONFIG_H
# include "config.h"
#endif

#include <vlc_common.h>

#include <vlc_aout.h>
#include <vlc_vout.h>

#include "vlc_interface.h"
#include "modules/modules.h" // Gruik!
#include "libvlc.h"

/*****************************************************************************
 * Local prototypes
 *****************************************************************************/
static void* RunInterface( vlc_object_t *p_this );
#if defined( __APPLE__ ) || defined( WIN32 )
static void * MonitorLibVLCDeath( vlc_object_t *p_this );
#endif
static int AddIntfCallback( vlc_object_t *, char const *,
                            vlc_value_t , vlc_value_t , void * );

/**
 * \brief Destroy the interface after the main loop endeed.
 *
 * \param p_intf the interface thread
 * \return nothing
 */
static void intf_Destroy( vlc_object_t *obj )
{
    intf_thread_t *p_intf = (intf_thread_t *)obj;

    /* Unlock module if present (a switch may have failed) */
    if( p_intf->p_module )
        module_Unneed( p_intf, p_intf->p_module );

    free( p_intf->psz_intf );
    vlc_mutex_destroy( &p_intf->change_lock );
}

/*****************************************************************************
 * intf_Create: prepare interface before main loop
 *****************************************************************************
 * This function opens output devices and creates specific interfaces. It sends
 * its own error messages.
 *****************************************************************************/
/**
 * Create the interface, and prepare it for main loop.
 *
 * \param p_this the calling vlc_object_t
 * \param psz_module a preferred interface module
 * \return a pointer to the created interface thread, NULL on error
 */
intf_thread_t* __intf_Create( vlc_object_t *p_this, const char *psz_module )
{
    intf_thread_t * p_intf;

    /* Allocate structure */
    p_intf = vlc_object_create( p_this, VLC_OBJECT_INTF );
    if( !p_intf )
        return NULL;
    p_intf->b_interaction = false;
#if defined( __APPLE__ ) || defined( WIN32 )
    p_intf->b_should_run_on_first_thread = false;
#endif

    /* Choose the best module */
    p_intf->psz_intf = strdup( psz_module );
    p_intf->p_module = module_Need( p_intf, "interface", psz_module, false );

    if( p_intf->p_module == NULL )
    {
        msg_Err( p_intf, "no suitable interface module" );
        free( p_intf->psz_intf );
        vlc_object_release( p_intf );
        return NULL;
    }

    /* Initialize structure */
    p_intf->b_menu        = false;
    p_intf->b_menu_change = false;

    /* Initialize mutexes */
    vlc_mutex_init( &p_intf->change_lock );

    /* Attach interface to its parent object */
    vlc_object_attach( p_intf, p_this );
    vlc_object_set_destructor( p_intf, intf_Destroy );

    return p_intf;
}

/*****************************************************************************
 * intf_RunThread: launch the interface thread
 *****************************************************************************
 * This function either creates a new thread and runs the interface in it.
 *****************************************************************************/
/**
 * Starts and runs the interface thread.
 *
 * \param p_intf the interface thread
 * \return VLC_SUCCESS on success, an error number else
 */
int intf_RunThread( intf_thread_t *p_intf )
{
#if defined( __APPLE__ ) || defined( WIN32 )
    /* Hack to get Mac OS X Cocoa runtime running
     * (it needs access to the main thread) */
    if( p_intf->b_should_run_on_first_thread )
    {
        if( vlc_thread_create( p_intf, "interface", MonitorLibVLCDeath,
                               VLC_THREAD_PRIORITY_LOW, false ) )
        {
            msg_Err( p_intf, "cannot spawn libvlc death monitoring thread" );
            return VLC_EGENERIC;
        }
        RunInterface( VLC_OBJECT(p_intf) );

        /* Make sure our MonitorLibVLCDeath thread exit */
        vlc_object_kill( p_intf );
        /* It is monitoring libvlc, not the p_intf */
        vlc_object_signal( p_intf->p_libvlc );
        vlc_thread_join( p_intf );

        vlc_object_detach( p_intf );
        vlc_object_release( p_intf );
        return VLC_SUCCESS;
    }
#endif
    /* Run the interface in a separate thread */
    if( vlc_thread_create( p_intf, "interface", RunInterface,
                           VLC_THREAD_PRIORITY_LOW, false ) )
    {
        msg_Err( p_intf, "cannot spawn interface thread" );
        return VLC_EGENERIC;
    }

    return VLC_SUCCESS;
}

/**
 * Stops the interface thread
 *
 * This function asks the interface thread to stop
 * \param p_intf the interface thread
 * \return nothing
 */
void intf_StopThread( intf_thread_t *p_intf )
{
    /* Tell the interface to die */
    vlc_object_kill( p_intf );
    vlc_thread_join( p_intf );
}

/* Following functions are local */

/*****************************************************************************
 * RunInterface: setups necessary data and give control to the interface
 *****************************************************************************/
static void* RunInterface( vlc_object_t *p_this )
{
    intf_thread_t *p_intf = (intf_thread_t *)p_this;
    vlc_value_t val, text;
    char *psz_intf;

    /* Variable used for interface spawning */
    var_Create( p_intf, "intf-add", VLC_VAR_STRING |
                VLC_VAR_HASCHOICE | VLC_VAR_ISCOMMAND );
    text.psz_string = _("Add Interface");
    var_Change( p_intf, "intf-add", VLC_VAR_SETTEXT, &text, NULL );

    val.psz_string = (char *)"rc";
    text.psz_string = (char *)_("Console");
    var_Change( p_intf, "intf-add", VLC_VAR_ADDCHOICE, &val, &text );
    val.psz_string = (char *)"telnet";
    text.psz_string = (char *)_("Telnet Interface");
    var_Change( p_intf, "intf-add", VLC_VAR_ADDCHOICE, &val, &text );
    val.psz_string = (char *)"http";
    text.psz_string = (char *)_("Web Interface");
    var_Change( p_intf, "intf-add", VLC_VAR_ADDCHOICE, &val, &text );
    val.psz_string = (char *)"logger";
    text.psz_string = (char *)_("Debug logging");
    var_Change( p_intf, "intf-add", VLC_VAR_ADDCHOICE, &val, &text );
    val.psz_string = (char *)"gestures";
    text.psz_string = (char *)_("Mouse Gestures");
    var_Change( p_intf, "intf-add", VLC_VAR_ADDCHOICE, &val, &text );

    var_AddCallback( p_intf, "intf-add", AddIntfCallback, NULL );

    do
    {
        /* Give control to the interface */
        if( p_intf->pf_run )
            p_intf->pf_run( p_intf );
        else
        {
            vlc_object_lock( p_intf );
            while( vlc_object_alive( p_intf ) )
                vlc_object_wait( p_intf );
            vlc_object_unlock( p_intf );
        }

        if( !p_intf->psz_switch_intf )
        {
            break;
        }

        /* Make sure the old interface is completely uninitialized */
        module_Unneed( p_intf, p_intf->p_module );

        /* Provide ability to switch the main interface on the fly */
        psz_intf = p_intf->psz_switch_intf;
        p_intf->psz_switch_intf = NULL;

        vlc_object_lock( p_intf );
        p_intf->b_die = false; /* FIXME */
        p_intf->b_dead = false;

        vlc_object_unlock( p_intf );

        p_intf->psz_intf = psz_intf;
        p_intf->p_module = module_Need( p_intf, "interface", psz_intf, 0 );
    }
    while( p_intf->p_module );
    return NULL;
}

#if defined( __APPLE__ ) || defined( WIN32 )
/*****************************************************************************
 * MonitorLibVLCDeath: Used when b_should_run_on_first_thread is set.
 *****************************************************************************/
static void * MonitorLibVLCDeath( vlc_object_t * p_this )
{
    intf_thread_t *p_intf = (intf_thread_t *)p_this;
    libvlc_int_t * p_libvlc = p_intf->p_libvlc;
    vlc_object_lock( p_libvlc );
    while(vlc_object_alive( p_libvlc ) )
    {
        if(p_intf->b_die)
        {
            vlc_object_unlock( p_libvlc );
            return NULL;
        }
        vlc_object_wait( p_libvlc );
    }
    vlc_object_unlock( p_libvlc );

    /* Someone killed libvlc */

    /* Make sure we kill all interface objects, especially
     * those that are blocking libvlc (running on main thread) */
    vlc_list_t * p_list = vlc_list_find( p_libvlc, VLC_OBJECT_INTF, FIND_CHILD );
    for( int i = 0; i < p_list->i_count; i++ )
    {
        vlc_object_t * p_intf = p_list->p_values[i].p_object;
        vlc_object_kill( p_intf );
    }
    vlc_list_release( p_list );
    return NULL;
}
#endif

static int AddIntfCallback( vlc_object_t *p_this, char const *psz_cmd,
                         vlc_value_t oldval, vlc_value_t newval, void *p_data )
{
    intf_thread_t *p_intf;
    char *psz_intf = malloc( strlen(newval.psz_string) + sizeof(",none") );

    (void)psz_cmd; (void)oldval; (void)p_data;

    /* Try to create the interface */
    sprintf( psz_intf, "%s,none", newval.psz_string );
    p_intf = intf_Create( p_this->p_libvlc, psz_intf );
    free( psz_intf );
    if( p_intf == NULL )
    {
        msg_Err( p_this, "interface \"%s\" initialization failed",
                 newval.psz_string );
        return VLC_EGENERIC;
    }

    /* Try to run the interface */
    if( intf_RunThread( p_intf ) != VLC_SUCCESS )
    {
        vlc_object_detach( p_intf );
        vlc_object_release( p_intf );
        return VLC_EGENERIC;
    }

    return VLC_SUCCESS;
}

