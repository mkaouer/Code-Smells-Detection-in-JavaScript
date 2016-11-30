/*****************************************************************************
 * timeshift.c: access filter implementing timeshifting capabilities
 *****************************************************************************
 * Copyright (C) 2005 the VideoLAN team
 * $Id: 6567e22c57cde4579c3030dd69f4dc7999c3a22d $
 *
 * Authors: Laurent Aimar <fenrir@via.ecp.fr>
 *          Gildas Bazin <gbazin@videolan.org>
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

#include <errno.h>

#include <vlc_access.h>
#include <vlc_charset.h>
#include <vlc_input.h>

#include <unistd.h>

#ifdef WIN32
#  include <direct.h>                                        /* _wgetcwd  */
#endif

/*****************************************************************************
 * Module descriptor
 *****************************************************************************/
static int  Open ( vlc_object_t * );
static void Close( vlc_object_t * );

#define GRANULARITY_TEXT N_("Timeshift granularity")
/// \bug [String] typo
#define GRANULARITY_LONGTEXT N_( "This is the size of the temporary files " \
  "that will be used to store the timeshifted streams." )
#define DIR_TEXT N_("Timeshift directory")
#define DIR_LONGTEXT N_( "Directory used to store the timeshift temporary " \
  "files." )
#define FORCE_TEXT N_("Force use of the timeshift module")
#define FORCE_LONGTEXT N_("Force use of the timeshift module even if the " \
  "access declares that it can control pace or pause." )

vlc_module_begin();
    set_shortname( N_("Timeshift") );
    set_description( N_("Timeshift") );
    set_category( CAT_INPUT );
    set_subcategory( SUBCAT_INPUT_ACCESS_FILTER );
    set_capability( "access_filter", 0 );
    add_shortcut( "timeshift" );
    set_callbacks( Open, Close );

    add_integer( "timeshift-granularity", 50, NULL, GRANULARITY_TEXT,
                 GRANULARITY_LONGTEXT, true );
    add_directory( "timeshift-dir", 0, 0, DIR_TEXT, DIR_LONGTEXT, false );
        change_unsafe();
    add_bool( "timeshift-force", false, NULL, FORCE_TEXT, FORCE_LONGTEXT,
              false );
vlc_module_end();

/*****************************************************************************
 * Local prototypes
 *****************************************************************************/

static int      Seek( access_t *, int64_t );
static block_t *Block  ( access_t *p_access );
static int      Control( access_t *, int i_query, va_list args );
static void*    Thread ( vlc_object_t *p_this );
static int      WriteBlockToFile( access_t *p_access, block_t *p_block );
static block_t *ReadBlockFromFile( access_t *p_access );
static char    *GetTmpFilePath( access_t *p_access );

#define TIMESHIFT_FIFO_MAX (10*1024*1024)
#define TIMESHIFT_FIFO_MIN (TIMESHIFT_FIFO_MAX/4)
#define TMP_FILE_MAX 256

typedef struct ts_entry_t
{
    FILE *file;
    struct ts_entry_t *p_next;

} ts_entry_t;

struct access_sys_t
{
    block_fifo_t *p_fifo;

    unsigned  i_files;
    unsigned  i_file_size;
    unsigned  i_write_size;

    ts_entry_t *p_read_list;
    ts_entry_t **pp_read_last;
    ts_entry_t *p_write_list;
    ts_entry_t **pp_write_last;

    char *psz_filename_base;
    char *psz_filename;

    int64_t i_data;
};

/*****************************************************************************
 * Open:
 *****************************************************************************/
static int Open( vlc_object_t *p_this )
{
    access_t *p_access = (access_t*)p_this;
    access_t *p_src = p_access->p_source;
    access_sys_t *p_sys;
    bool b_bool;

    var_Create( p_access, "timeshift-force", VLC_VAR_BOOL|VLC_VAR_DOINHERIT );
    if( var_GetBool( p_access, "timeshift-force" ) )
    {
        msg_Dbg( p_access, "Forcing use of timeshift even if access can control pace or pause" );
    }
    else
    {
        /* Only work with not pace controled access */
        if( access_Control( p_src, ACCESS_CAN_CONTROL_PACE, &b_bool ) ||
            b_bool )
        {
            msg_Dbg( p_src, "ACCESS_CAN_CONTROL_PACE: timeshift useless" );
            return VLC_EGENERIC;
        }
        /* Refuse access that can be paused */
        if( access_Control( p_src, ACCESS_CAN_PAUSE, &b_bool ) || b_bool )
        {
            msg_Dbg( p_src, "ACCESS_CAN_PAUSE: timeshift useless" );
            return VLC_EGENERIC;
        }
    }

    /* */
    p_access->pf_read = NULL;
    p_access->pf_block = Block;
    p_access->pf_seek = Seek;
    p_access->pf_control = Control;
    p_access->info = p_src->info;

    p_access->p_sys = p_sys = malloc( sizeof( access_sys_t ) );
    if( !p_sys )
        return VLC_ENOMEM;

    /* */
    p_sys->p_fifo = block_FifoNew();
    p_sys->i_write_size = 0;
    p_sys->i_files = 0;
    p_sys->i_data = 0;

    p_sys->p_read_list = NULL;
    p_sys->pp_read_last = &p_sys->p_read_list;
    p_sys->p_write_list = NULL;
    p_sys->pp_write_last = &p_sys->p_write_list;

    var_Create( p_access, "timeshift-dir",
                VLC_VAR_DIRECTORY | VLC_VAR_DOINHERIT );
    var_Create( p_access, "timeshift-granularity",
                VLC_VAR_INTEGER | VLC_VAR_DOINHERIT );
    p_sys->i_file_size = var_GetInteger( p_access, "timeshift-granularity" );
    if( p_sys->i_file_size < 1 ) p_sys->i_file_size = 1;
    p_sys->i_file_size *= 1024 * 1024; /* In MBytes */

    p_sys->psz_filename_base = GetTmpFilePath( p_access );
    p_sys->psz_filename = malloc( strlen( p_sys->psz_filename_base ) + 1000 );

    if( vlc_thread_create( p_access, "timeshift thread", Thread,
                           VLC_THREAD_PRIORITY_LOW, false ) )
    {
        Close( p_this );
        msg_Err( p_access, "cannot spawn timeshift access thread" );
        return VLC_EGENERIC;
    }

    return VLC_SUCCESS;
}

/*****************************************************************************
 * Close:
 *****************************************************************************/
static void Close( vlc_object_t *p_this )
{
    access_t     *p_access = (access_t*)p_this;
    access_sys_t *p_sys = p_access->p_sys;
    ts_entry_t *p_entry;
    unsigned i;

    msg_Dbg( p_access, "timeshift close called" );
    vlc_thread_join( p_access );

    for( p_entry = p_sys->p_write_list; p_entry; )
    {
        ts_entry_t *p_next = p_entry->p_next;
        fclose( p_entry->file );
        free( p_entry );
        p_entry = p_next;
    }
    for( p_entry = p_sys->p_read_list; p_entry; )
    {
        ts_entry_t *p_next = p_entry->p_next;
        fclose( p_entry->file );
        free( p_entry );
        p_entry = p_next;
    }
    for( i = 0; i < p_sys->i_files; i++ )
    {
        sprintf( p_sys->psz_filename, "%s%i.dat",
                 p_sys->psz_filename_base, i );
        unlink( p_sys->psz_filename );
    }

    free( p_sys->psz_filename );
    free( p_sys->psz_filename_base );
    block_FifoRelease( p_sys->p_fifo );
    free( p_sys );
}

/*****************************************************************************
 *
 *****************************************************************************/
static block_t *Block( access_t *p_access )
{
    access_sys_t *p_sys = p_access->p_sys;
    access_t *p_src = p_access->p_source;
    block_t *p_block = NULL;

    /* Update info (we probably ought to be time caching that as well) */
    if( p_src->info.i_update & INPUT_UPDATE_META )
    {
        p_src->info.i_update &= ~INPUT_UPDATE_META;
        p_access->info.i_update |= INPUT_UPDATE_META;
    }

    /* Get data from timeshift fifo */
    if( !p_access->info.b_eof )
        p_block = block_FifoGet( p_sys->p_fifo );

    if( p_block && !p_block->i_buffer ) /* Used to signal EOF */
    { block_Release( p_block ); p_block = 0; }

    if( p_block )
    {
        p_sys->i_data -= p_block->i_buffer;
        return p_block;
    }

    p_access->info.b_eof = p_src->info.b_eof;
    return NULL;
}

/*****************************************************************************
 *
 *****************************************************************************/
static void* Thread( vlc_object_t* p_this )
{
    access_t *p_access = (access_t*)p_this;
    access_sys_t *p_sys = p_access->p_sys;
    access_t     *p_src = p_access->p_source;
    block_t      *p_block;

    while( vlc_object_alive (p_access) )
    {
        /* Get a new block from the source */
        if( p_src->pf_block )
        {
            p_block = p_src->pf_block( p_src );
        }
        else
        {
            int i_read;

            if( ( p_block = block_New( p_access, 2048 ) ) == NULL ) break;

            i_read = p_src->pf_read( p_src, p_block->p_buffer, 2048 );
            if( i_read <= 0 )
            {
              block_Release( p_block );
              p_block = NULL;
            }
            p_block->i_buffer = i_read;
        }

        if( p_block == NULL )
        {
          if( p_src->info.b_eof ) break;
          msleep( 10000 );
          continue;
        }

        p_sys->i_data += p_block->i_buffer;

        /* Write block */
        if( !p_sys->p_write_list && !p_sys->p_read_list &&
            block_FifoSize( p_sys->p_fifo ) < TIMESHIFT_FIFO_MAX )
        {
            /* If there isn't too much timeshifted data,
             * write directly to FIFO */
            block_FifoPut( p_sys->p_fifo, p_block );
            continue;
        }

        WriteBlockToFile( p_access, p_block );
        block_Release( p_block );

        /* Read from file to fill up the fifo */
        while( block_FifoSize( p_sys->p_fifo ) < TIMESHIFT_FIFO_MIN &&
               vlc_object_alive (p_access) )
        {
            p_block = ReadBlockFromFile( p_access );
            if( !p_block ) break;

            block_FifoPut( p_sys->p_fifo, p_block );
        }
    }

    msg_Dbg( p_access, "timeshift: no more input data" );

    while( vlc_object_alive (p_access) &&
           (p_sys->p_read_list || block_FifoSize( p_sys->p_fifo ) ) )
    {
        /* Read from file to fill up the fifo */
        while( block_FifoSize( p_sys->p_fifo ) < TIMESHIFT_FIFO_MIN &&
               vlc_object_alive (p_access) && p_sys->p_read_list )
        {
            p_block = ReadBlockFromFile( p_access );
            if( !p_block ) break;

            block_FifoPut( p_sys->p_fifo, p_block );
        }

        msleep( 100000 );
    }

    msg_Dbg( p_access, "timeshift: EOF" );
    p_src->info.b_eof = true;

    /* Send dummy packet to avoid deadlock in Block() */
    block_FifoPut( p_sys->p_fifo, block_New( p_access, 0 ) );
    return NULL;
}

/*****************************************************************************
 * NextFileWrite:
 *****************************************************************************/
static void NextFileWrite( access_t *p_access )
{
    access_sys_t *p_sys = p_access->p_sys;
    ts_entry_t   *p_next;

    if( !p_sys->p_write_list )
    {
        p_sys->i_write_size = 0;
        return;
    }

    p_next = p_sys->p_write_list->p_next;

    /* Put written file in read list */
    if( p_sys->i_write_size < p_sys->i_file_size )
        ftruncate( fileno( p_sys->p_write_list->file ), p_sys->i_write_size );

    fseek( p_sys->p_write_list->file, 0, SEEK_SET );
    *p_sys->pp_read_last = p_sys->p_write_list;
    p_sys->pp_read_last = &p_sys->p_write_list->p_next;
    p_sys->p_write_list->p_next = 0;

    /* Switch to next file to write */
    p_sys->p_write_list = p_next;
    if( !p_sys->p_write_list ) p_sys->pp_write_last = &p_sys->p_write_list;

    p_sys->i_write_size = 0;
}

/*****************************************************************************
 * NextFileRead:
 *****************************************************************************/
static void NextFileRead( access_t *p_access )
{
    access_sys_t *p_sys = p_access->p_sys;
    ts_entry_t   *p_next;

    if( !p_sys->p_read_list ) return;

    p_next = p_sys->p_read_list->p_next;

    /* Put read file in write list */
    fseek( p_sys->p_read_list->file, 0, SEEK_SET );
    *p_sys->pp_write_last = p_sys->p_read_list;
    p_sys->pp_write_last = &p_sys->p_read_list->p_next;
    p_sys->p_read_list->p_next = 0;

    /* Switch to next file to read */
    p_sys->p_read_list = p_next;
    if( !p_sys->p_read_list ) p_sys->pp_read_last = &p_sys->p_read_list;
}

/*****************************************************************************
 * WriteBlockToFile:
 *****************************************************************************/
static int WriteBlockToFile( access_t *p_access, block_t *p_block )
{
    access_sys_t *p_sys = p_access->p_sys;
    int i_write, i_buffer;

    if( p_sys->i_write_size == p_sys->i_file_size ) NextFileWrite( p_access );

    /* Open new file if necessary */
    if( !p_sys->p_write_list )
    {
        FILE *file;

        sprintf( p_sys->psz_filename, "%s%u.dat",
                 p_sys->psz_filename_base, p_sys->i_files );
        file = utf8_fopen( p_sys->psz_filename, "w+b" );

        if( !file && p_sys->i_files < 2 )
        {
            /* We just can't work with less than 2 buffer files */
            msg_Err( p_access, "cannot open temporary file '%s' (%m)",
                     p_sys->psz_filename );
            return VLC_EGENERIC;
        }
        else if( !file ) return VLC_EGENERIC;

        p_sys->p_write_list = malloc( sizeof(ts_entry_t) );
        p_sys->p_write_list->p_next = 0;
        p_sys->p_write_list->file = file;
        p_sys->pp_write_last = &p_sys->p_write_list->p_next;

        p_sys->i_files++;
    }

    /* Write to file */
    i_buffer = __MIN( p_block->i_buffer,
                      p_sys->i_file_size - p_sys->i_write_size );

    i_write = fwrite( p_block->p_buffer, 1, i_buffer,
                      p_sys->p_write_list->file );

    if( i_write > 0 ) p_sys->i_write_size += i_write;

    //p_access->info.i_size += i_write;
    //p_access->info.i_update |= INPUT_UPDATE_SIZE;

    if( i_write < i_buffer )
    {
        /* Looks like we're short of space */

        if( !p_sys->p_write_list->p_next )
        {
            msg_Warn( p_access, "no more space, overwritting old data" );
            NextFileRead( p_access );
            NextFileRead( p_access );
        }

        /* Make sure we switch to next file in write list */
        p_sys->i_write_size = p_sys->i_file_size;
    }

    p_block->p_buffer += i_write;
    p_block->i_buffer -= i_write;

    /* Check if we have some data left */
    if( p_block->i_buffer ) return WriteBlockToFile( p_access, p_block );

    return VLC_SUCCESS;
}

/*****************************************************************************
 * ReadBlockFromFile:
 *****************************************************************************/
static block_t *ReadBlockFromFile( access_t *p_access )
{
    access_sys_t *p_sys = p_access->p_sys;
    block_t *p_block;

    if( !p_sys->p_read_list && p_sys->p_write_list )
    {
        /* Force switching to next write file, that should
         * give us something to read */
        NextFileWrite( p_access );
    }

    if( !p_sys->p_read_list ) return 0;

    p_block = block_New( p_access, 4096 );
    p_block->i_buffer = fread( p_block->p_buffer, 1, 4096,
                               p_sys->p_read_list->file );

    if( p_block->i_buffer == 0 ) NextFileRead( p_access );

    //p_access->info.i_size -= p_block->i_buffer;
    //p_access->info.i_update |= INPUT_UPDATE_SIZE;

    return p_block;
}

/*****************************************************************************
 * Seek: seek to a specific location in a file
 *****************************************************************************/
static int Seek( access_t *p_access, int64_t i_pos )
{
    //access_sys_t *p_sys = p_access->p_sys;
    (void)p_access;
    (void)i_pos;
    return VLC_SUCCESS;
}

/*****************************************************************************
 *
 *****************************************************************************/
static int Control( access_t *p_access, int i_query, va_list args )
{
    bool   *pb_bool;
    int          *pi_int;

    switch( i_query )
    {
    case ACCESS_CAN_SEEK:
    case ACCESS_CAN_FASTSEEK:
        pb_bool = (bool*)va_arg( args, bool* );
        *pb_bool = true;
        break;

    case ACCESS_CAN_CONTROL_PACE:   /* Not really true */
    case ACCESS_CAN_PAUSE:
        pb_bool = (bool*)va_arg( args, bool* );
        *pb_bool = true;
        break;

    case ACCESS_GET_MTU:
        pi_int = (int*)va_arg( args, int * );
        *pi_int = 0;
        break;

    case ACCESS_SET_PAUSE_STATE:
        break;

    /* Forward everything else to the source access */
    default:
        return access_vaControl( p_access->p_source, i_query, args );
    }
    return VLC_SUCCESS;
}

/*****************************************************************************
 * GetTmpFilePath:
 *****************************************************************************/
#ifdef WIN32
#define getpid() (int)GetCurrentProcessId()
#endif
static char *GetTmpFilePath( access_t *p_access )
{
    char *psz_dir = var_GetNonEmptyString( p_access, "timeshift-dir" );
    char *psz_filename_base;

    if( psz_dir == NULL )
    {
#ifdef WIN32
        DWORD ret = GetTempPathW (0, NULL);
        wchar_t wdir[ret + 3]; // can at least old "C:" + nul
        const wchar_t *pwdir = wdir;
        wchar_t *pwdir_free = NULL;

        if (GetTempPathW (ret + 1, wdir) == 0)
        {
            pwdir_free = pwdir = _wgetcwd (NULL, 0);
            if (pwdir == NULL)
                pwdir = L"C:";
        }

        psz_dir = FromWide (pwdir);
        if (pwdir_free != NULL)
            free (pwdir_free);

        /* remove trailing antislash if any */
        if (psz_dir[strlen (psz_dir) - 1] == '\\')
            psz_dir[strlen (psz_dir) - 1] = '\0';
#else
        psz_dir = strdup( "/tmp" );
#endif
    }

    if( asprintf( &psz_filename_base, "%s/vlc-timeshift-%d-%d-",
              psz_dir, getpid(), p_access->i_object_id ) == -1 )
        psz_filename_base = NULL;
    free( psz_dir );

    return psz_filename_base;
}
