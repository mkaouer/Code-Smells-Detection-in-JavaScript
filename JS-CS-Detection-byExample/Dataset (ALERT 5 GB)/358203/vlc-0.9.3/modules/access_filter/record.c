/*****************************************************************************
 * record.c
 *****************************************************************************
 * Copyright (C) 2005-2006 the VideoLAN team
 * $Id: 67dba9d280a221898da2188881f4ac723b6a6ddf $
 *
 * Author: Laurent Aimar <fenrir@via.ecp.fr>
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

/*****************************************************************************
 * Preamble
 *****************************************************************************/

#ifdef HAVE_CONFIG_H
# include "config.h"
#endif

#include <vlc_common.h>
#include <vlc_plugin.h>

#include <vlc_input.h>
#include <vlc_access.h>

#include "vlc_keys.h"
#include <vlc_osd.h>
#include <vlc_charset.h>
#include <errno.h>
#include <time.h>

/*****************************************************************************
 * Module descriptor
 *****************************************************************************/

#define RECORD_PATH_TXT N_("Record directory")
#define RECORD_PATH_LONGTXT N_( \
    "Directory where the record will be stored." )

static int  Open ( vlc_object_t * );
static void Close( vlc_object_t * );

vlc_module_begin();
    set_shortname( N_("Record") );
    set_description( N_("Record") );
    set_category( CAT_INPUT );
    set_subcategory( SUBCAT_INPUT_ACCESS_FILTER );
    set_capability( "access_filter", 0 );
    add_shortcut( "record" );

    add_directory( "record-path", NULL, NULL,
                   RECORD_PATH_TXT, RECORD_PATH_LONGTXT, true );
        change_unsafe();

    set_callbacks( Open, Close );

vlc_module_end();

/*****************************************************************************
 * Local prototypes
 *****************************************************************************/

static block_t *Block  ( access_t * );
static ssize_t  Read   ( access_t *, uint8_t *, size_t );
static int      Control( access_t *, int i_query, va_list args );
static int      Seek   ( access_t *, int64_t );

static void Dump( access_t *, uint8_t *, int );

static int EventKey( vlc_object_t *, char const *,
                     vlc_value_t, vlc_value_t, void * );
static int ToggleRecord( vlc_object_t *, char const *,
                         vlc_value_t, vlc_value_t, void * );

struct access_sys_t
{
    bool b_dump;

    char *psz_path;
    const char *psz_ext;
    char *psz_file;
    int64_t i_size;
    FILE *f;

    vout_thread_t *p_vout;
    int            i_vout_chan;

    int i_update_sav;
};

static inline void PreUpdateFlags( access_t *p_access )
{
    access_t *p_src = p_access->p_source;
    /* backport flags turned off 0 */
    p_src->info.i_update &= p_access->p_sys->i_update_sav ^ (~p_access->info.i_update);
}

static inline void PostUpdateFlags( access_t *p_access )
{
    access_t *p_src = p_access->p_source;

    p_access->info = p_src->info;
    p_access->p_sys->i_update_sav = p_access->info.i_update;
}


/*****************************************************************************
 * Open:
 *****************************************************************************/
static int Open( vlc_object_t *p_this )
{
    access_t *p_access = (access_t*)p_this;
    access_t *p_src = p_access->p_source;
    access_sys_t *p_sys;
    char *psz;

    p_access->pf_read  = p_src->pf_read  ? Read : NULL;
    p_access->pf_block = p_src->pf_block ? Block : NULL;
    p_access->pf_seek  = p_src->pf_seek  ? Seek : NULL;
    p_access->pf_control = Control;

    p_access->info = p_src->info;

    p_access->p_sys = p_sys = malloc( sizeof( access_t ) );
    if( !p_sys ) return VLC_ENOMEM;

    /* */
    p_sys->f = NULL;
    p_sys->i_size = 0;
    p_sys->psz_file = NULL;
    p_sys->psz_ext = "dat";
    p_sys->b_dump = false;
    p_sys->p_vout = NULL;
    p_sys->i_vout_chan = -1;
    p_sys->i_update_sav = p_access->info.i_update;

    if( !strncasecmp( p_src->psz_access, "dvb", 3 ) ||
        !strncasecmp( p_src->psz_access, "udp", 3 ) )
        p_sys->psz_ext = "ts";

    psz = var_CreateGetString( p_access, "record-path" );
    if( *psz == '\0' )
    {
        free( psz );
        psz = strdup( config_GetHomeDir() );
    }
    p_sys->psz_path = psz;
    msg_Dbg( p_access, "Record access filter path %s", psz );

    input_thread_t *p_input = ( input_thread_t * )
            vlc_object_find( p_access, VLC_OBJECT_INPUT, FIND_PARENT );
    if( p_input )
    {
        var_Create( p_input, "record-toggle", VLC_VAR_VOID );
        var_AddCallback( p_input, "record-toggle", ToggleRecord, p_access );
        vlc_object_release( p_input );
    }

    /* catch all key event */
    var_AddCallback( p_access->p_libvlc, "key-action", EventKey, p_access );

    return VLC_SUCCESS;
}

/*****************************************************************************
 * Close:
 *****************************************************************************/
static void Close( vlc_object_t *p_this )
{
    access_t     *p_access = (access_t*)p_this;
    access_sys_t *p_sys = p_access->p_sys;

    var_DelCallback( p_access->p_libvlc, "key-action", EventKey, p_access );
    input_thread_t *p_input = ( input_thread_t * )
            vlc_object_find( p_access, VLC_OBJECT_INPUT, FIND_PARENT );
    if( p_input )
    {
        var_Destroy( p_input, "record-toggle" );
        vlc_object_release( p_input );
    }

    if( p_sys->f )
    {
        fclose( p_sys->f );
        free( p_sys->psz_file );
    }

    free( p_sys->psz_path );
    free( p_sys );
}

/*****************************************************************************
 *
 *****************************************************************************/
static block_t *Block( access_t *p_access )
{
    access_t     *p_src = p_access->p_source;
    block_t      *p_block;

    PreUpdateFlags( p_access );

    p_block = p_src->pf_block( p_src );
    if( p_block && p_block->i_buffer )
        Dump( p_access, p_block->p_buffer, p_block->i_buffer );

    PostUpdateFlags( p_access );

    return p_block;
}

/*****************************************************************************
 *
 *****************************************************************************/
static ssize_t Read( access_t *p_access, uint8_t *p_buffer, size_t i_len )
{
    access_t     *p_src = p_access->p_source;
    int i_ret;

    PreUpdateFlags( p_access );

    i_ret = p_src->pf_read( p_src, p_buffer, i_len );
    if( i_ret > 0 )
        Dump( p_access, p_buffer, i_ret );

    PostUpdateFlags( p_access );

    return i_ret;
}

/*****************************************************************************
 *
 *****************************************************************************/
static int Control( access_t *p_access, int i_query, va_list args )
{
    access_t     *p_src = p_access->p_source;
    int i_ret;

    PreUpdateFlags( p_access );

    i_ret = p_src->pf_control( p_src, i_query, args );

    PostUpdateFlags( p_access );

    return i_ret;
}

/*****************************************************************************
 *
 *****************************************************************************/
static int Seek( access_t *p_access, int64_t i_pos )
{
    access_t     *p_src = p_access->p_source;
    int i_ret;

    PreUpdateFlags( p_access );

    i_ret = p_src->pf_seek( p_src, i_pos );

    PostUpdateFlags( p_access );

    return i_ret;
}

/*****************************************************************************
 *
 *****************************************************************************/
static int EventKey( vlc_object_t *p_this, char const *psz_var,
                     vlc_value_t oldval, vlc_value_t newval, void *p_data )
{
    access_t     *p_access = p_data;
    access_sys_t *p_sys = p_access->p_sys;

    (void)p_this;
    (void)psz_var;
    (void)oldval;

    if( newval.i_int == ACTIONID_RECORD )
    {
        if( p_sys->b_dump )
            p_sys->b_dump = false;
        else
            p_sys->b_dump = true;
    }

    return VLC_SUCCESS;
}

static int ToggleRecord( vlc_object_t *p_this, char const *psz_var,
                          vlc_value_t oldval, vlc_value_t newval,
                          void *p_data )
{
    access_t     *p_access = p_data;
    access_sys_t *p_sys = p_access->p_sys;

    (void)p_this;
    (void)psz_var;
    (void)oldval;
    (void)newval;

    p_sys->b_dump = !p_sys->b_dump;

    return VLC_SUCCESS;
}

/*****************************************************************************
 *
 *****************************************************************************/
static void Notify( access_t *p_access, bool b_dump )
{
    access_sys_t *p_sys = p_access->p_sys;
    vout_thread_t *p_vout;

    p_vout = vlc_object_find( p_access, VLC_OBJECT_VOUT, FIND_ANYWHERE );
    if( !p_vout ) return;

    if( p_vout != p_sys->p_vout )
    {
        p_sys->p_vout = p_vout;
        if( spu_Control( p_vout->p_spu, SPU_CHANNEL_REGISTER,
                         &p_sys->i_vout_chan  ) )
            p_sys->i_vout_chan = -1;
    }

    if( p_sys->i_vout_chan != -1 )
    {
        if( b_dump )
            vout_OSDMessage( p_vout, p_sys->i_vout_chan, _("Recording") );
        else
            vout_OSDMessage( p_vout, p_sys->i_vout_chan, _("Recording done") );
    }
    vlc_object_release( p_vout );
}

/*****************************************************************************
 *
 *****************************************************************************/
static void Dump( access_t *p_access, uint8_t *p_buffer, int i_buffer )
{
    access_sys_t *p_sys = p_access->p_sys;
    int i_write;

    if( !p_sys->b_dump )
    {
        if( p_sys->f )
        {
            msg_Dbg( p_access, "dumped %"PRId64" kb (%s)",
                     p_sys->i_size/1024, p_sys->psz_file );

            Notify( p_access, false );

            fclose( p_sys->f );
            p_sys->f = NULL;

            free( p_sys->psz_file );
            p_sys->psz_file = NULL;

            p_sys->i_size = 0;
        }
        return;
    }

    if( !p_sys->f )
    {
        input_thread_t *p_input;
        char *psz_name = NULL, *psz;
        time_t t = time(NULL);
        struct tm l;

        if( !localtime_r( &t, &l ) ) memset( &l, 0, sizeof(l) );

        p_input = vlc_object_find( p_access, VLC_OBJECT_INPUT, FIND_PARENT );
        if( p_input )
        {
            input_item_t * p_item = input_GetItem( p_input );
            vlc_mutex_lock( &p_item->lock );
            if( p_item->psz_name )
            {
                char *p = strrchr( p_item->psz_name, '/' );
                if( p == NULL )
                    p = strrchr( p_item->psz_name, '\\' );

                if( p == NULL )
                    psz_name = strdup( p_item->psz_name );
                else if( p[1] != '\0' )
                    psz_name = strdup( &p[1] );
            }
            vlc_mutex_unlock( &p_item->lock );

            vlc_object_release( p_input );
        }

        if( asprintf( &p_sys->psz_file, "%s %d-%d-%d %.2dh%.2dm%.2ds.%s",
                      ( psz_name != NULL ) ? psz_name : "Unknown",
                      l.tm_mday, l.tm_mon+1, l.tm_year+1900,
                      l.tm_hour, l.tm_min, l.tm_sec,
                      p_sys->psz_ext ) == -1 )
            p_sys->psz_file = NULL;

        free( psz_name );
        if( p_sys->psz_file == NULL )
        {
            p_sys->b_dump = false;
            return;
        }

        /* Remove all forbidden characters (except (back)slashes) */
        for( psz = p_sys->psz_file; *psz; psz++ )
        {
            unsigned char c = (unsigned char)*psz;

            /* Even if many OS accept non printable characters, we remove
             * them to avoid confusing users */
            if( ( c < 32 ) || ( c == 127 ) )
                *psz = '_';
#if defined (WIN32) || defined (UNDER_CE)
            /* Windows has a lot of forbidden characters, even if it has
             * fewer than DOS. */
            if( strchr( "\"*:<>?|", c ) != NULL )
                *psz = '_';
#endif
        }

        psz_name = p_sys->psz_file;

#if defined (WIN32) || defined (UNDER_CE)
#define DIR_SEP "\\"
#else
#define DIR_SEP "/"
#endif
        if( asprintf( &p_sys->psz_file, "%s" DIR_SEP "%s",
                      p_sys->psz_path, psz_name ) == -1 )
            p_sys->psz_file = NULL;
        free( psz_name );
        if( p_sys->psz_file == NULL )
        {
            p_sys->b_dump = false;
            return;
        }

        msg_Dbg( p_access, "dump in file '%s'", p_sys->psz_file );

        p_sys->f = utf8_fopen( p_sys->psz_file, "wb" );
        if( p_sys->f == NULL )
        {
            msg_Err( p_access, "cannot open file '%s' (%m)",
                     p_sys->psz_file );
            free( p_sys->psz_file );
            p_sys->psz_file = NULL;
            p_sys->b_dump = false;
            return;
        }

        Notify( p_access, true );

        p_sys->i_size = 0;
    }

    if( ( i_write = fwrite( p_buffer, 1, i_buffer, p_sys->f ) ) > 0 )
        p_sys->i_size += i_write;
}

