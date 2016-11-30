/*****************************************************************************
 * dbus.c : D-Bus control interface
 *****************************************************************************
 * Copyright © 2006-2008 Rafaël Carré
 * Copyright © 2007-2008 Mirsal Ennaime
 * $Id: 7c8a1c9431258f3661aa1f08d356be6934eaefa7 $
 *
 * Authors:    Rafaël Carré <funman at videolanorg>
 *             Mirsal Ennaime <mirsal dot ennaime at gmail dot com>
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

/*
 * D-Bus Specification:
 *      http://dbus.freedesktop.org/doc/dbus-specification.html
 * D-Bus low-level C API (libdbus)
 *      http://dbus.freedesktop.org/doc/dbus/api/html/index.html
 *  extract:
 *   "If you use this low-level API directly, you're signing up for some pain."
 *
 * MPRIS Specification (still drafting on Jan, 23 of 2008):
 *      http://wiki.xmms2.xmms.se/index.php/MPRIS
 */

/*****************************************************************************
 * Preamble
 *****************************************************************************/

#include <dbus/dbus.h>

#include "dbus.h"

#ifdef HAVE_CONFIG_H
# include "config.h"
#endif

#include <vlc_common.h>
#include <vlc_plugin.h>
#include <vlc_aout.h>
#include <vlc_interface.h>
#include <vlc_meta.h>
#include <vlc_input.h>
#include <vlc_playlist.h>

#include <math.h>

#include <assert.h>

/*****************************************************************************
 * Local prototypes.
 *****************************************************************************/

static int  Open    ( vlc_object_t * );
static void Close   ( vlc_object_t * );
static void Run     ( intf_thread_t * );

static int StateChange( vlc_object_t *, const char *, vlc_value_t,
                        vlc_value_t, void * );

static int TrackChange( vlc_object_t *, const char *, vlc_value_t,
                        vlc_value_t, void * );

static int StatusChangeEmit( vlc_object_t *, const char *, vlc_value_t,
                        vlc_value_t, void * );

static int TrackListChangeEmit( vlc_object_t *, const char *, vlc_value_t,
                        vlc_value_t, void * );

static int GetInputMeta ( input_item_t *, DBusMessageIter * );
static int MarshalStatus ( intf_thread_t *, DBusMessageIter *, bool );
static int UpdateCaps( intf_thread_t*, bool );

/* GetCaps() capabilities */
enum
{
     CAPS_NONE                  = 0,
     CAPS_CAN_GO_NEXT           = 1 << 0,
     CAPS_CAN_GO_PREV           = 1 << 1,
     CAPS_CAN_PAUSE             = 1 << 2,
     CAPS_CAN_PLAY              = 1 << 3,
     CAPS_CAN_SEEK              = 1 << 4,
     CAPS_CAN_PROVIDE_METADATA  = 1 << 5,
     CAPS_CAN_HAS_TRACKLIST     = 1 << 6
};

struct intf_sys_t
{
    DBusConnection *p_conn;
    bool      b_meta_read;
    dbus_int32_t    i_caps;
};

/*****************************************************************************
 * Module descriptor
 *****************************************************************************/

vlc_module_begin();
    set_shortname( N_("dbus"));
    set_category( CAT_INTERFACE );
    set_subcategory( SUBCAT_INTERFACE_CONTROL );
    set_description( N_("D-Bus control interface") );
    set_capability( "interface", 0 );
    set_callbacks( Open, Close );
vlc_module_end();

/*****************************************************************************
 * Methods
 *****************************************************************************/

/* Player */

DBUS_METHOD( Quit )
{ /* exits vlc */
    REPLY_INIT;
    playlist_t *p_playlist = pl_Yield( (vlc_object_t*) p_this );
    playlist_Stop( p_playlist );
    pl_Release( ((vlc_object_t*) p_this) );
    vlc_object_kill(((vlc_object_t*)p_this)->p_libvlc);
    REPLY_SEND;
}

DBUS_METHOD( MprisVersion )
{ /*implemented version of the mpris spec */
    REPLY_INIT;
    OUT_ARGUMENTS;
    VLC_UNUSED( p_this );
    dbus_uint16_t i_major = VLC_MPRIS_VERSION_MAJOR;
    dbus_uint16_t i_minor = VLC_MPRIS_VERSION_MINOR;
    DBusMessageIter version;

    if( !dbus_message_iter_open_container( &args, DBUS_TYPE_STRUCT, NULL,
            &version ) )
        return DBUS_HANDLER_RESULT_NEED_MEMORY;

    if( !dbus_message_iter_append_basic( &version, DBUS_TYPE_UINT16,
            &i_major ) )
        return DBUS_HANDLER_RESULT_NEED_MEMORY;

    if( !dbus_message_iter_append_basic( &version, DBUS_TYPE_UINT16,
            &i_minor ) )
        return DBUS_HANDLER_RESULT_NEED_MEMORY;

    if( !dbus_message_iter_close_container( &args, &version ) )
        return DBUS_HANDLER_RESULT_NEED_MEMORY;
    REPLY_SEND;
}

DBUS_METHOD( PositionGet )
{ /* returns position in milliseconds */
    REPLY_INIT;
    OUT_ARGUMENTS;
    vlc_value_t position;
    dbus_int32_t i_pos;

    playlist_t *p_playlist = pl_Yield( ((vlc_object_t*) p_this) );
    PL_LOCK;
    input_thread_t *p_input = p_playlist->p_input;

    if( !p_input )
        i_pos = 0;
    else
    {
        var_Get( p_input, "time", &position );
        i_pos = position.i_time / 1000;
    }
    PL_UNLOCK;
    pl_Release( ((vlc_object_t*) p_this) );
    ADD_INT32( &i_pos );
    REPLY_SEND;
}

DBUS_METHOD( PositionSet )
{ /* set position in milliseconds */

    REPLY_INIT;
    vlc_value_t position;
    playlist_t* p_playlist = NULL;
    dbus_int32_t i_pos;

    DBusError error;
    dbus_error_init( &error );

    dbus_message_get_args( p_from, &error,
            DBUS_TYPE_INT32, &i_pos,
            DBUS_TYPE_INVALID );

    if( dbus_error_is_set( &error ) )
    {
        msg_Err( (vlc_object_t*) p_this, "D-Bus message reading : %s\n",
                error.message );
        dbus_error_free( &error );
        return DBUS_HANDLER_RESULT_NOT_YET_HANDLED;
    }
    p_playlist = pl_Yield( ((vlc_object_t*) p_this) );
    PL_LOCK;
    input_thread_t *p_input = p_playlist->p_input;

    if( p_input )
    {
        position.i_time = i_pos * 1000;
        var_Set( p_input, "time", position );
    }
    PL_UNLOCK;
    pl_Release( ((vlc_object_t*) p_this) );
    REPLY_SEND;
}

DBUS_METHOD( VolumeGet )
{ /* returns volume in percentage */
    REPLY_INIT;
    OUT_ARGUMENTS;
    dbus_int32_t i_dbus_vol;
    audio_volume_t i_vol;
    /* 2nd argument of aout_VolumeGet is int32 */
    aout_VolumeGet( (vlc_object_t*) p_this, &i_vol );
    double f_vol = 100. * i_vol / AOUT_VOLUME_MAX;
    i_dbus_vol = round( f_vol );
    ADD_INT32( &i_dbus_vol );
    REPLY_SEND;
}

DBUS_METHOD( VolumeSet )
{ /* set volume in percentage */
    REPLY_INIT;

    DBusError error;
    dbus_error_init( &error );

    dbus_int32_t i_dbus_vol;
    audio_volume_t i_vol;

    dbus_message_get_args( p_from, &error,
            DBUS_TYPE_INT32, &i_dbus_vol,
            DBUS_TYPE_INVALID );

    if( dbus_error_is_set( &error ) )
    {
        msg_Err( (vlc_object_t*) p_this, "D-Bus message reading : %s\n",
                error.message );
        dbus_error_free( &error );
        return DBUS_HANDLER_RESULT_NOT_YET_HANDLED;
    }

    double f_vol = AOUT_VOLUME_MAX * i_dbus_vol / 100.;
    i_vol = round( f_vol );
    aout_VolumeSet( (vlc_object_t*) p_this, i_vol );

    REPLY_SEND;
}

DBUS_METHOD( Next )
{ /* next playlist item */
    REPLY_INIT;
    playlist_t *p_playlist = pl_Yield( ((vlc_object_t*) p_this) );
    playlist_Next( p_playlist );
    pl_Release( ((vlc_object_t*) p_this) );
    REPLY_SEND;
}

DBUS_METHOD( Prev )
{ /* previous playlist item */
    REPLY_INIT;
    playlist_t *p_playlist = pl_Yield( ((vlc_object_t*) p_this) );
    playlist_Prev( p_playlist );
    pl_Release( ((vlc_object_t*) p_this) );
    REPLY_SEND;
}

DBUS_METHOD( Stop )
{ /* stop playing */
    REPLY_INIT;
    playlist_t *p_playlist = pl_Yield( ((vlc_object_t*) p_this) );
    playlist_Stop( p_playlist );
    pl_Release( ((vlc_object_t*) p_this) );
    REPLY_SEND;
}

DBUS_METHOD( GetStatus )
{ /* returns the current status as a struct of 4 ints */
/*
    First   0 = Playing, 1 = Paused, 2 = Stopped.
    Second  0 = Playing linearly , 1 = Playing randomly.
    Third   0 = Go to the next element once the current has finished playing , 1 = Repeat the current element
    Fourth  0 = Stop playing once the last element has been played, 1 = Never give up playing *
 */
    REPLY_INIT;
    OUT_ARGUMENTS;

    MarshalStatus( p_this, &args, true );

    REPLY_SEND;
}

DBUS_METHOD( Pause )
{
    REPLY_INIT;
    playlist_t *p_playlist = pl_Yield( (vlc_object_t*) p_this );
    playlist_Pause( p_playlist );
    pl_Release( (vlc_object_t*) p_this );
    REPLY_SEND;
}

DBUS_METHOD( Play )
{
    REPLY_INIT;
    playlist_t *p_playlist = pl_Yield( (vlc_object_t*) p_this );

    PL_LOCK;
    input_thread_t *p_input = p_playlist->p_input;
    if( p_input )
        vlc_object_yield( p_input );
    PL_UNLOCK;

    if( p_input )
    {
        double i_pos = 0;
        input_Control( p_input, INPUT_SET_POSITION, i_pos );
        vlc_object_release( p_input );
    }
    else
        playlist_Play( p_playlist );

    pl_Release( (vlc_object_t*) p_this );
    REPLY_SEND;
}

DBUS_METHOD( GetCurrentMetadata )
{
    REPLY_INIT;
    OUT_ARGUMENTS;
    playlist_t* p_playlist = pl_Yield( (vlc_object_t*) p_this );
    PL_LOCK;
    if( p_playlist->status.p_item )
        GetInputMeta( p_playlist->status.p_item->p_input, &args );
    PL_UNLOCK;
    pl_Release( (vlc_object_t*) p_this );
    REPLY_SEND;
}

DBUS_METHOD( GetCaps )
{
    REPLY_INIT;
    OUT_ARGUMENTS;

    ADD_INT32( &((intf_thread_t*)p_this)->p_sys->i_caps );

    REPLY_SEND;
}

/* Media Player information */

DBUS_METHOD( Identity )
{
    VLC_UNUSED(p_this);
    REPLY_INIT;
    OUT_ARGUMENTS;
    char *psz_identity;
    if( asprintf( &psz_identity, "%s %s", PACKAGE, VERSION ) != -1 )
    {
        ADD_STRING( &psz_identity );
        free( psz_identity );
    }
    else
        return DBUS_HANDLER_RESULT_NEED_MEMORY;

    REPLY_SEND;
}

/* TrackList */

DBUS_METHOD( AddTrack )
{ /* add the string to the playlist, and play it if the boolean is true */
    REPLY_INIT;
    OUT_ARGUMENTS;

    DBusError error;
    dbus_error_init( &error );
    playlist_t* p_playlist = NULL;

    char *psz_mrl;
    dbus_bool_t b_play;

    dbus_message_get_args( p_from, &error,
            DBUS_TYPE_STRING, &psz_mrl,
            DBUS_TYPE_BOOLEAN, &b_play,
            DBUS_TYPE_INVALID );

    if( dbus_error_is_set( &error ) )
    {
        msg_Err( (vlc_object_t*) p_this, "D-Bus message reading : %s\n",
                error.message );
        dbus_error_free( &error );
        return DBUS_HANDLER_RESULT_NOT_YET_HANDLED;
    }

    p_playlist = pl_Yield( (vlc_object_t*) p_this );
    playlist_Add( p_playlist, psz_mrl, NULL, PLAYLIST_APPEND |
            ( ( b_play == TRUE ) ? PLAYLIST_GO : 0 ) ,
            PLAYLIST_END, true, false );
    pl_Release( (vlc_object_t*) p_this );

    dbus_int32_t i_success = 0;
    ADD_INT32( &i_success );

    REPLY_SEND;
}

DBUS_METHOD( GetCurrentTrack )
{
    REPLY_INIT;
    OUT_ARGUMENTS;

    playlist_t *p_playlist = pl_Yield( (vlc_object_t*) p_this );
    dbus_int32_t i_position = p_playlist->i_current_index;
    pl_Release( (vlc_object_t*) p_this );

    ADD_INT32( &i_position );
    REPLY_SEND;
}

DBUS_METHOD( GetMetadata )
{
    REPLY_INIT;
    OUT_ARGUMENTS;
    DBusError error;
    dbus_error_init( &error );

    dbus_int32_t i_position;

    playlist_t *p_playlist = pl_Yield( (vlc_object_t*) p_this );
    PL_LOCK;

    dbus_message_get_args( p_from, &error,
           DBUS_TYPE_INT32, &i_position,
           DBUS_TYPE_INVALID );

    if( dbus_error_is_set( &error ) )
    {
        PL_UNLOCK;
        pl_Release( (vlc_object_t*) p_this );
        msg_Err( (vlc_object_t*) p_this, "D-Bus message reading : %s\n",
                error.message );
        dbus_error_free( &error );
        return DBUS_HANDLER_RESULT_NOT_YET_HANDLED;
    }

    if( i_position < p_playlist->current.i_size )
    {
        GetInputMeta( p_playlist->current.p_elems[i_position]->p_input, &args );
    }

    PL_UNLOCK;
    pl_Release( (vlc_object_t*) p_this );
    REPLY_SEND;
}

DBUS_METHOD( GetLength )
{
    REPLY_INIT;
    OUT_ARGUMENTS;

    playlist_t *p_playlist = pl_Yield( (vlc_object_t*) p_this );
    dbus_int32_t i_elements = p_playlist->current.i_size;
    pl_Release( (vlc_object_t*) p_this );

    ADD_INT32( &i_elements );
    REPLY_SEND;
}

DBUS_METHOD( DelTrack )
{
    REPLY_INIT;

    DBusError error;
    dbus_error_init( &error );

    dbus_int32_t i_position;
    playlist_t *p_playlist = pl_Yield( (vlc_object_t*) p_this );

    dbus_message_get_args( p_from, &error,
            DBUS_TYPE_INT32, &i_position,
            DBUS_TYPE_INVALID );

    if( dbus_error_is_set( &error ) )
    {
        msg_Err( (vlc_object_t*) p_this, "D-Bus message reading : %s\n",
                error.message );
        dbus_error_free( &error );
        return DBUS_HANDLER_RESULT_NOT_YET_HANDLED;
    }

    PL_LOCK;
    if( i_position < p_playlist->current.i_size )
    {
        playlist_DeleteFromInput( p_playlist,
            p_playlist->current.p_elems[i_position]->p_input->i_id,
            pl_Locked );
    }
    PL_UNLOCK;

    pl_Release( (vlc_object_t*) p_this );

    REPLY_SEND;
}

DBUS_METHOD( SetLoop )
{
    REPLY_INIT;
    OUT_ARGUMENTS;

    DBusError error;
    dbus_bool_t b_loop;
    vlc_value_t val;
    playlist_t* p_playlist = NULL;

    dbus_error_init( &error );
    dbus_message_get_args( p_from, &error,
            DBUS_TYPE_BOOLEAN, &b_loop,
            DBUS_TYPE_INVALID );

    if( dbus_error_is_set( &error ) )
    {
        msg_Err( (vlc_object_t*) p_this, "D-Bus message reading : %s\n",
                error.message );
        dbus_error_free( &error );
        return DBUS_HANDLER_RESULT_NOT_YET_HANDLED;
    }

    val.b_bool = ( b_loop == TRUE ) ? true : false ;
    p_playlist = pl_Yield( (vlc_object_t*) p_this );
    var_Set ( p_playlist, "loop", val );
    pl_Release( ((vlc_object_t*) p_this) );

    REPLY_SEND;
}

DBUS_METHOD( Repeat )
{
    REPLY_INIT;
    OUT_ARGUMENTS;

    DBusError error;
    dbus_bool_t b_repeat;
    vlc_value_t val;
    playlist_t* p_playlist = NULL;

    dbus_error_init( &error );
    dbus_message_get_args( p_from, &error,
            DBUS_TYPE_BOOLEAN, &b_repeat,
            DBUS_TYPE_INVALID );

    if( dbus_error_is_set( &error ) )
    {
        msg_Err( (vlc_object_t*) p_this, "D-Bus message reading : %s\n",
                error.message );
        dbus_error_free( &error );
        return DBUS_HANDLER_RESULT_NOT_YET_HANDLED;
    }

    val.b_bool = ( b_repeat == TRUE ) ? true : false ;

    p_playlist = pl_Yield( (vlc_object_t*) p_this );
    var_Set ( p_playlist, "repeat", val );
    pl_Release( ((vlc_object_t*) p_this) );

    REPLY_SEND;
}

DBUS_METHOD( SetRandom )
{
    REPLY_INIT;
    OUT_ARGUMENTS;

    DBusError error;
    dbus_bool_t b_random;
    vlc_value_t val;
    playlist_t* p_playlist = NULL;

    dbus_error_init( &error );
    dbus_message_get_args( p_from, &error,
            DBUS_TYPE_BOOLEAN, &b_random,
            DBUS_TYPE_INVALID );

    if( dbus_error_is_set( &error ) )
    {
        msg_Err( (vlc_object_t*) p_this, "D-Bus message reading : %s\n",
                error.message );
        dbus_error_free( &error );
        return DBUS_HANDLER_RESULT_NOT_YET_HANDLED;
    }

    val.b_bool = ( b_random == TRUE ) ? true : false ;

    p_playlist = pl_Yield( (vlc_object_t*) p_this );
    var_Set ( p_playlist, "random", val );
    pl_Release( ((vlc_object_t*) p_this) );

    REPLY_SEND;
}
/*****************************************************************************
 * Introspection method
 *****************************************************************************/

DBUS_METHOD( handle_introspect_root )
{ /* handles introspection of root object */
    VLC_UNUSED(p_this);
    REPLY_INIT;
    OUT_ARGUMENTS;
    ADD_STRING( &psz_introspection_xml_data_root );
    REPLY_SEND;
}

DBUS_METHOD( handle_introspect_player )
{
    VLC_UNUSED(p_this);
    REPLY_INIT;
    OUT_ARGUMENTS;
    ADD_STRING( &psz_introspection_xml_data_player );
    REPLY_SEND;
}

DBUS_METHOD( handle_introspect_tracklist )
{
    VLC_UNUSED(p_this);
    REPLY_INIT;
    OUT_ARGUMENTS;
    ADD_STRING( &psz_introspection_xml_data_tracklist );
    REPLY_SEND;
}

/*****************************************************************************
 * handle_*: answer to incoming messages
 *****************************************************************************/

#define METHOD_FUNC( method, function ) \
    else if( dbus_message_is_method_call( p_from, MPRIS_DBUS_INTERFACE, method ) )\
        return function( p_conn, p_from, p_this )

DBUS_METHOD( handle_root )
{

    if( dbus_message_is_method_call( p_from,
                DBUS_INTERFACE_INTROSPECTABLE, "Introspect" ) )
        return handle_introspect_root( p_conn, p_from, p_this );

    /* here D-Bus method's names are associated to an handler */

    METHOD_FUNC( "Identity",                Identity );
    METHOD_FUNC( "MprisVersion",            MprisVersion );
    METHOD_FUNC( "Quit",                    Quit );

    return DBUS_HANDLER_RESULT_NOT_YET_HANDLED;
}


DBUS_METHOD( handle_player )
{
    if( dbus_message_is_method_call( p_from,
                DBUS_INTERFACE_INTROSPECTABLE, "Introspect" ) )
        return handle_introspect_player( p_conn, p_from, p_this );

    /* here D-Bus method's names are associated to an handler */

    METHOD_FUNC( "Prev",                    Prev );
    METHOD_FUNC( "Next",                    Next );
    METHOD_FUNC( "Stop",                    Stop );
    METHOD_FUNC( "Play",                    Play );
    METHOD_FUNC( "Pause",                   Pause );
    METHOD_FUNC( "Repeat",                  Repeat );
    METHOD_FUNC( "VolumeSet",               VolumeSet );
    METHOD_FUNC( "VolumeGet",               VolumeGet );
    METHOD_FUNC( "PositionSet",             PositionSet );
    METHOD_FUNC( "PositionGet",             PositionGet );
    METHOD_FUNC( "GetStatus",               GetStatus );
    METHOD_FUNC( "GetMetadata",             GetCurrentMetadata );
    METHOD_FUNC( "GetCaps",                 GetCaps );

    return DBUS_HANDLER_RESULT_NOT_YET_HANDLED;
}

DBUS_METHOD( handle_tracklist )
{
    if( dbus_message_is_method_call( p_from,
                DBUS_INTERFACE_INTROSPECTABLE, "Introspect" ) )
    return handle_introspect_tracklist( p_conn, p_from, p_this );

    /* here D-Bus method's names are associated to an handler */

    METHOD_FUNC( "GetMetadata",             GetMetadata );
    METHOD_FUNC( "GetCurrentTrack",         GetCurrentTrack );
    METHOD_FUNC( "GetLength",               GetLength );
    METHOD_FUNC( "AddTrack",                AddTrack );
    METHOD_FUNC( "DelTrack",                DelTrack );
    METHOD_FUNC( "SetLoop",                 SetLoop );
    METHOD_FUNC( "SetRandom",               SetRandom );

    return DBUS_HANDLER_RESULT_NOT_YET_HANDLED;
}

/*****************************************************************************
 * Open: initialize interface
 *****************************************************************************/

static int Open( vlc_object_t *p_this )
{ /* initialisation of the connection */
    intf_thread_t   *p_intf = (intf_thread_t*)p_this;
    intf_sys_t      *p_sys  = malloc( sizeof( intf_sys_t ) );
    playlist_t      *p_playlist;
    DBusConnection  *p_conn;
    DBusError       error;

    if( !p_sys )
        return VLC_ENOMEM;

    p_sys->b_meta_read = false;
    p_sys->i_caps = CAPS_NONE;

    dbus_error_init( &error );

    /* connect to the session bus */
    p_conn = dbus_bus_get( DBUS_BUS_SESSION, &error );
    if( !p_conn )
    {
        msg_Err( p_this, "Failed to connect to the D-Bus session daemon: %s",
                error.message );
        dbus_error_free( &error );
        free( p_sys );
        return VLC_EGENERIC;
    }

    /* register a well-known name on the bus */
    dbus_bus_request_name( p_conn, VLC_MPRIS_DBUS_SERVICE, 0, &error );
    if( dbus_error_is_set( &error ) )
    {
        msg_Err( p_this, "Error requesting service " VLC_MPRIS_DBUS_SERVICE
                 ": %s", error.message );
        dbus_error_free( &error );
        free( p_sys );
        return VLC_EGENERIC;
    }

    /* we register the objects */
    dbus_connection_register_object_path( p_conn, MPRIS_DBUS_ROOT_PATH,
            &vlc_dbus_root_vtable, p_this );
    dbus_connection_register_object_path( p_conn, MPRIS_DBUS_PLAYER_PATH,
            &vlc_dbus_player_vtable, p_this );
    dbus_connection_register_object_path( p_conn, MPRIS_DBUS_TRACKLIST_PATH,
            &vlc_dbus_tracklist_vtable, p_this );

    dbus_connection_flush( p_conn );

    p_playlist = pl_Yield( p_intf );
    PL_LOCK;
    var_AddCallback( p_playlist, "playlist-current", TrackChange, p_intf );
    var_AddCallback( p_playlist, "intf-change", TrackListChangeEmit, p_intf );
    var_AddCallback( p_playlist, "item-append", TrackListChangeEmit, p_intf );
    var_AddCallback( p_playlist, "item-deleted", TrackListChangeEmit, p_intf );
    var_AddCallback( p_playlist, "random", StatusChangeEmit, p_intf );
    var_AddCallback( p_playlist, "repeat", StatusChangeEmit, p_intf );
    var_AddCallback( p_playlist, "loop", StatusChangeEmit, p_intf );
    PL_UNLOCK;
    pl_Release( p_intf );

    p_intf->pf_run = Run;
    p_intf->p_sys = p_sys;
    p_sys->p_conn = p_conn;

    UpdateCaps( p_intf, false );

    return VLC_SUCCESS;
}

/*****************************************************************************
 * Close: destroy interface
 *****************************************************************************/

static void Close   ( vlc_object_t *p_this )
{
    intf_thread_t   *p_intf     = (intf_thread_t*) p_this;
    playlist_t      *p_playlist = pl_Yield( p_intf );;
    input_thread_t  *p_input;

    p_this->b_dead = true;

    PL_LOCK;
    var_DelCallback( p_playlist, "playlist-current", TrackChange, p_intf );
    var_DelCallback( p_playlist, "intf-change", TrackListChangeEmit, p_intf );
    var_DelCallback( p_playlist, "item-append", TrackListChangeEmit, p_intf );
    var_DelCallback( p_playlist, "item-deleted", TrackListChangeEmit, p_intf );
    var_DelCallback( p_playlist, "random", StatusChangeEmit, p_intf );
    var_DelCallback( p_playlist, "repeat", StatusChangeEmit, p_intf );
    var_DelCallback( p_playlist, "loop", StatusChangeEmit, p_intf );

    p_input = p_playlist->p_input;
    if ( p_input )
    {
        vlc_object_yield( p_input );
        var_DelCallback( p_input, "state", StateChange, p_intf );
        vlc_object_release( p_input );
    }

    PL_UNLOCK;
    pl_Release( p_intf );

    dbus_connection_unref( p_intf->p_sys->p_conn );

    free( p_intf->p_sys );
}

/*****************************************************************************
 * Run: main loop
 *****************************************************************************/

static void Run          ( intf_thread_t *p_intf )
{
    while( !intf_ShouldDie( p_intf ) )
    {
        msleep( INTF_IDLE_SLEEP );
        dbus_connection_read_write_dispatch( p_intf->p_sys->p_conn, 0 );
    }
}

/******************************************************************************
 * CapsChange: player capabilities change signal
 *****************************************************************************/
DBUS_SIGNAL( CapsChangeSignal )
{
    SIGNAL_INIT( "CapsChange" );
    OUT_ARGUMENTS;

    ADD_INT32( &((intf_thread_t*)p_data)->p_sys->i_caps );
    SIGNAL_SEND;
}

/******************************************************************************
 * TrackListChange: tracklist order / length change signal 
 *****************************************************************************/
DBUS_SIGNAL( TrackListChangeSignal )
{ /* emit the new tracklist lengh */
    SIGNAL_INIT("TrackListChange");
    OUT_ARGUMENTS;

    playlist_t *p_playlist = pl_Yield( (vlc_object_t*) p_data );
    dbus_int32_t i_elements = p_playlist->current.i_size;
    pl_Release( (vlc_object_t*) p_data );

    ADD_INT32( &i_elements );
    SIGNAL_SEND;
}

/*****************************************************************************
 * TrackListChangeEmit: Emits the TrackListChange signal
 *****************************************************************************/
/* FIXME: It is not called on tracklist reordering */
static int TrackListChangeEmit( vlc_object_t *p_this, const char *psz_var,
            vlc_value_t oldval, vlc_value_t newval, void *p_data )
{
    VLC_UNUSED(oldval);
    intf_thread_t *p_intf = p_data;

    if( !strcmp( psz_var, "item-append" ) || !strcmp( psz_var, "item-remove" ) )
    {
        /* don't signal when items are added/removed in p_category */
        playlist_t *p_playlist = (playlist_t*)p_this;
        playlist_add_t *p_add = newval.p_address;
        playlist_item_t *p_item;
        p_item = playlist_ItemGetById( p_playlist, p_add->i_node, pl_Locked );
        assert( p_item );
        while( p_item->p_parent )
            p_item = p_item->p_parent;
        if( p_item == p_playlist->p_root_category )
            return VLC_SUCCESS;
    }

    if( p_intf->b_dead )
        return VLC_SUCCESS;

    UpdateCaps( p_intf, true );
    TrackListChangeSignal( p_intf->p_sys->p_conn, p_data );
    return VLC_SUCCESS;
}
/*****************************************************************************
 * TrackChange: Playlist item change callback
 *****************************************************************************/

DBUS_SIGNAL( TrackChangeSignal )
{ /* emit the metadata of the new item */
    SIGNAL_INIT( "TrackChange" );
    OUT_ARGUMENTS;

    input_item_t *p_item = (input_item_t*) p_data;
    GetInputMeta ( p_item, &args );

    SIGNAL_SEND;
}

/*****************************************************************************
 * StatusChange: Player status change signal
 *****************************************************************************/

DBUS_SIGNAL( StatusChangeSignal )
{ /* send the updated status info on the bus */
    SIGNAL_INIT( "StatusChange" );
    OUT_ARGUMENTS;

    /* we're called from a callback of input_thread_t, so it can not be
     * destroyed before we return */
    MarshalStatus( (intf_thread_t*) p_data, &args, false );

    SIGNAL_SEND;
}

/*****************************************************************************
 * StateChange: callback on input "state"
 *****************************************************************************/
static int StateChange( vlc_object_t *p_this, const char* psz_var,
            vlc_value_t oldval, vlc_value_t newval, void *p_data )
{
    VLC_UNUSED(psz_var); VLC_UNUSED(oldval);
    intf_thread_t       *p_intf     = ( intf_thread_t* ) p_data;
    intf_sys_t          *p_sys      = p_intf->p_sys;

    if( p_intf->b_dead )
        return VLC_SUCCESS;

    UpdateCaps( p_intf, true );

    if( !p_sys->b_meta_read && newval.i_int == PLAYING_S )
    {
        input_item_t *p_item = input_GetItem( (input_thread_t*)p_this );
        if( p_item )
        {
            p_sys->b_meta_read = true;
            TrackChangeSignal( p_sys->p_conn, p_item );
        }
    }

    if( newval.i_int == PLAYING_S || newval.i_int == PAUSE_S ||
        newval.i_int == STOP_S ||  newval.i_int == END_S )
    {
        StatusChangeSignal( p_sys->p_conn, (void*) p_intf );
    }

    return VLC_SUCCESS;
}

/*****************************************************************************
 * StatusChangeEmit: Emits the StatusChange signal
 *****************************************************************************/
static int StatusChangeEmit( vlc_object_t *p_this, const char *psz_var,
            vlc_value_t oldval, vlc_value_t newval, void *p_data )
{
    VLC_UNUSED(p_this); VLC_UNUSED(psz_var);
    VLC_UNUSED(oldval); VLC_UNUSED(newval);
    intf_thread_t *p_intf = p_data;

    if( p_intf->b_dead )
        return VLC_SUCCESS;

    UpdateCaps( p_intf, false );
    StatusChangeSignal( p_intf->p_sys->p_conn, p_data );
    return VLC_SUCCESS;
}

/*****************************************************************************
 * TrackChange: callback on playlist "playlist-current"
 *****************************************************************************/
static int TrackChange( vlc_object_t *p_this, const char *psz_var,
            vlc_value_t oldval, vlc_value_t newval, void *p_data )
{
    intf_thread_t       *p_intf     = ( intf_thread_t* ) p_data;
    intf_sys_t          *p_sys      = p_intf->p_sys;
    playlist_t          *p_playlist;
    input_thread_t      *p_input    = NULL;
    input_item_t        *p_item     = NULL;
    VLC_UNUSED( p_this ); VLC_UNUSED( psz_var );
    VLC_UNUSED( oldval ); VLC_UNUSED( newval );

    if( p_intf->b_dead )
        return VLC_SUCCESS;

    p_sys->b_meta_read = false;

    p_playlist = pl_Yield( p_intf );
    p_input = p_playlist->p_input;

    if( !p_input )
    {
        PL_UNLOCK;
        pl_Release( p_intf );
        return VLC_SUCCESS;
    }

    vlc_object_yield( p_input );
    pl_Release( p_intf );

    p_item = input_GetItem( p_input );
    if( !p_item )
    {
        vlc_object_release( p_input );
        return VLC_EGENERIC;
    }

    if( input_item_IsPreparsed( p_item ) )
    {
        p_sys->b_meta_read = true;
        TrackChangeSignal( p_sys->p_conn, p_item );
    }

    var_AddCallback( p_input, "state", StateChange, p_intf );

    vlc_object_release( p_input );
    return VLC_SUCCESS;
}

/*****************************************************************************
 * UpdateCaps: update p_sys->i_caps
 ****************************************************************************/
static int UpdateCaps( intf_thread_t* p_intf, bool b_playlist_locked )
{
    intf_sys_t* p_sys = p_intf->p_sys;
    dbus_int32_t i_caps = CAPS_CAN_HAS_TRACKLIST;
    playlist_t* p_playlist = pl_Yield( p_intf );
    if( !b_playlist_locked ) PL_LOCK;
    
    if( p_playlist->current.i_size > 0 )
        i_caps |= CAPS_CAN_PLAY | CAPS_CAN_GO_PREV | CAPS_CAN_GO_NEXT;

    if( p_playlist->p_input )
    {
        /* XXX: if UpdateCaps() is called too early, these are
         * unconditionnaly true */
        if( var_GetBool( p_playlist->p_input, "can-pause" ) )
            i_caps |= CAPS_CAN_PAUSE;
        if( var_GetBool( p_playlist->p_input, "seekable" ) )
            i_caps |= CAPS_CAN_SEEK;
    }

    if( !b_playlist_locked ) PL_UNLOCK;
    pl_Release( p_intf );

    if( p_sys->b_meta_read )
        i_caps |= CAPS_CAN_PROVIDE_METADATA;

    if( i_caps != p_intf->p_sys->i_caps )
    {
        p_sys->i_caps = i_caps;
        CapsChangeSignal( p_intf->p_sys->p_conn, (vlc_object_t*)p_intf );
    }

    return VLC_SUCCESS;
}

/*****************************************************************************
 * GetInputMeta: Fill a DBusMessage with the given input item metadata
 *****************************************************************************/

#define ADD_META( entry, type, data ) \
    if( data ) { \
        dbus_message_iter_open_container( &dict, DBUS_TYPE_DICT_ENTRY, \
                NULL, &dict_entry ); \
        dbus_message_iter_append_basic( &dict_entry, DBUS_TYPE_STRING, \
                &ppsz_meta_items[entry] ); \
        dbus_message_iter_open_container( &dict_entry, DBUS_TYPE_VARIANT, \
                type##_AS_STRING, &variant ); \
        dbus_message_iter_append_basic( &variant, \
                type, \
                & data ); \
        dbus_message_iter_close_container( &dict_entry, &variant ); \
        dbus_message_iter_close_container( &dict, &dict_entry ); }

#define ADD_VLC_META_STRING( entry, item ) \
    { \
        char * psz = input_item_Get##item( p_input );\
        ADD_META( entry, DBUS_TYPE_STRING, \
                  psz ); \
        free( psz ); \
    }

static int GetInputMeta( input_item_t* p_input,
                        DBusMessageIter *args )
{
    DBusMessageIter dict, dict_entry, variant;
    /* We need the track length to be expressed in milli-seconds
     * instead of µ-seconds */
    dbus_int64_t i_length = ( input_item_GetDuration( p_input ) / 1000 );

    const char* ppsz_meta_items[] =
    {
    "title", "artist", "genre", "copyright", "album", "tracknum",
    "description", "rating", "date", "setting", "url", "language",
    "nowplaying", "publisher", "encodedby", "arturl", "trackid",
    "status", "location", "length", "video-codec", "audio-codec",
    "video-bitrate", "audio-bitrate", "audio-samplerate"
    };

    dbus_message_iter_open_container( args, DBUS_TYPE_ARRAY, "{sv}", &dict );

    ADD_VLC_META_STRING( 0,  Title );
    ADD_VLC_META_STRING( 1,  Artist );
    ADD_VLC_META_STRING( 2,  Genre );
    ADD_VLC_META_STRING( 3,  Copyright );
    ADD_VLC_META_STRING( 4,  Album );
    ADD_VLC_META_STRING( 5,  TrackNum );
    ADD_VLC_META_STRING( 6,  Description );
    ADD_VLC_META_STRING( 7,  Rating );
    ADD_VLC_META_STRING( 8,  Date );
    ADD_VLC_META_STRING( 9,  Setting );
    ADD_VLC_META_STRING( 10, URL );
    ADD_VLC_META_STRING( 11, Language );
    ADD_VLC_META_STRING( 12, NowPlaying );
    ADD_VLC_META_STRING( 13, Publisher );
    ADD_VLC_META_STRING( 14, EncodedBy );
    ADD_VLC_META_STRING( 15, ArtURL );
    ADD_VLC_META_STRING( 16, TrackID );

    vlc_mutex_lock( &p_input->lock );
    if( p_input->p_meta )
        ADD_META( 17, DBUS_TYPE_INT32, p_input->p_meta->i_status );
    vlc_mutex_unlock( &p_input->lock );

    ADD_VLC_META_STRING( 18, URI );
    ADD_META( 19, DBUS_TYPE_INT64, i_length );

    dbus_message_iter_close_container( args, &dict );
    return VLC_SUCCESS;
}

#undef ADD_META
#undef ADD_VLC_META_STRING

/*****************************************************************************
 * MarshalStatus: Fill a DBusMessage with the current player status
 *****************************************************************************/

static int MarshalStatus( intf_thread_t* p_intf, DBusMessageIter* args,
                          bool lock )
{ /* This is NOT the right way to do that, it would be better to sore
     the status information in p_sys and update it on change, thus
     avoiding a long lock */

    DBusMessageIter status;
    dbus_int32_t i_state, i_random, i_repeat, i_loop;
    vlc_value_t val;
    playlist_t* p_playlist = NULL;
    input_thread_t* p_input = NULL;

    p_playlist = pl_Yield( p_intf );
    if( lock )
        PL_LOCK;

    i_state = 2;

    p_input = p_playlist->p_input;
    if( p_input )
    {
        var_Get( p_input, "state", &val );
        if( val.i_int >= END_S )
            i_state = 2;
        else if( val.i_int == PAUSE_S )
            i_state = 1;
        else if( val.i_int <= PLAYING_S )
            i_state = 0;
    }

    i_random = var_CreateGetBool( p_playlist, "random" );

    i_repeat = var_CreateGetBool( p_playlist, "repeat" );

    i_loop = var_CreateGetBool( p_playlist, "loop" );

    if( lock )
        PL_UNLOCK;
    pl_Release( p_intf );

    dbus_message_iter_open_container( args, DBUS_TYPE_STRUCT, NULL, &status );
    dbus_message_iter_append_basic( &status, DBUS_TYPE_INT32, &i_state );
    dbus_message_iter_append_basic( &status, DBUS_TYPE_INT32, &i_random );
    dbus_message_iter_append_basic( &status, DBUS_TYPE_INT32, &i_repeat );
    dbus_message_iter_append_basic( &status, DBUS_TYPE_INT32, &i_loop );
    dbus_message_iter_close_container( args, &status );

    return VLC_SUCCESS;
}
