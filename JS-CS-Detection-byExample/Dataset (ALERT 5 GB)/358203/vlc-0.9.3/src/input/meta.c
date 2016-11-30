/*****************************************************************************
 * meta.c : Metadata handling
 *****************************************************************************
 * Copyright (C) 1998-2004 the VideoLAN team
 * $Id: 402c9919ea47577b26d663e6678db48be32524d6 $
 *
 * Authors: Antoine Cellerier <dionoea@videolan.org>
 *          Clément Stenac <zorglub@videolan.org
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

#ifdef HAVE_CONFIG_H
# include "config.h"
#endif

#include <vlc_common.h>
#include <vlc_input.h>
#include <vlc_stream.h>
#include <vlc_meta.h>
#include <vlc_playlist.h>
#include <vlc_charset.h>
#include <vlc_strings.h>
#include "../playlist/playlist_internal.h"
#include <errno.h>
#include <limits.h>                                             /* PATH_MAX */
#include <assert.h>

#ifdef HAVE_SYS_STAT_H
#   include <sys/stat.h>
#endif

#include "../libvlc.h"

const char *
input_MetaTypeToLocalizedString( vlc_meta_type_t meta_type )
{
    switch( meta_type )
    {
    case vlc_meta_Title:        return _("Title");
    case vlc_meta_Artist:       return _("Artist");
    case vlc_meta_Genre:        return _("Genre");
    case vlc_meta_Copyright:    return _("Copyright");
    case vlc_meta_Album:        return _("Album");
    case vlc_meta_TrackNumber:  return _("Track number");
    case vlc_meta_Description:  return _("Description");
    case vlc_meta_Rating:       return _("Rating");
    case vlc_meta_Date:         return _("Date");
    case vlc_meta_Setting:      return _("Setting");
    case vlc_meta_URL:          return _("URL");
    case vlc_meta_Language:     return _("Language");
    case vlc_meta_NowPlaying:   return _("Now Playing");
    case vlc_meta_Publisher:    return _("Publisher");
    case vlc_meta_EncodedBy:    return _("Encoded by");
    case vlc_meta_ArtworkURL:   return _("Artwork URL");
    case vlc_meta_TrackID:      return _("Track ID");

    default: abort();
    }
};

#define input_FindArtInCache(a,b) __input_FindArtInCache(VLC_OBJECT(a),b)
static int __input_FindArtInCache( vlc_object_t *, input_item_t *p_item );

/* Return codes:
 *   0 : Art is in cache or is a local file
 *   1 : Art found, need to download
 *  -X : Error/not found
 */
int input_ArtFind( playlist_t *p_playlist, input_item_t *p_item )
{
    int i_ret = VLC_EGENERIC;
    module_t *p_module;
    char *psz_title, *psz_artist, *psz_album;

    psz_artist = input_item_GetArtist( p_item );
    psz_album = input_item_GetAlbum( p_item );
    psz_title = input_item_GetTitle( p_item );
    if(!psz_title)
        psz_title = input_item_GetName( p_item );

    if( !psz_title && !psz_artist && !psz_album )
        return VLC_EGENERIC;

    free( psz_title );

    /* If we already checked this album in this session, skip */
    if( psz_artist && psz_album )
    {
        FOREACH_ARRAY( playlist_album_t album, p_playlist->p_fetcher->albums )
            if( !strcmp( album.psz_artist, psz_artist ) &&
                !strcmp( album.psz_album, psz_album ) )
            {
                msg_Dbg( p_playlist, " %s - %s has already been searched",
                         psz_artist, psz_album );
        /* TODO-fenrir if we cache art filename too, we can go faster */
                free( psz_artist );
                free( psz_album );
                if( album.b_found )
                {
                    if( !strncmp( album.psz_arturl, "file://", 7 ) )
                        input_item_SetArtURL( p_item, album.psz_arturl );
                    else /* Actually get URL from cache */
                        input_FindArtInCache( p_playlist, p_item );
                    return 0;
                }
                else
                {
                    return VLC_EGENERIC;
                }
            }
        FOREACH_END();
    }
    free( psz_artist );
    free( psz_album );

    input_FindArtInCache( p_playlist, p_item );

    char *psz_arturl = input_item_GetArtURL( p_item );
    if( psz_arturl )
    {
        /* We already have an URL */
        if( !strncmp( psz_arturl, "file://", strlen( "file://" ) ) )
        {
            free( psz_arturl );
            return 0; /* Art is in cache, no need to go further */
        }

        free( psz_arturl );
        
        /* Art need to be put in cache */
        return 1;
    }

    PL_LOCK;
    p_playlist->p_private = p_item;
    psz_album = input_item_GetAlbum( p_item );
    psz_artist = input_item_GetArtist( p_item );
    psz_title = input_item_GetTitle( p_item );
    if( !psz_title )
        psz_title = input_item_GetName( p_item );

    if( psz_album && psz_artist )
    {
        msg_Dbg( p_playlist, "searching art for %s - %s",
             psz_artist, psz_album );
    }
    else
    {
        msg_Dbg( p_playlist, "searching art for %s",
             psz_title );
    }
    free( psz_title );

    p_module = module_Need( p_playlist, "art finder", 0, false );

    if( p_module )
        i_ret = 1;
    else
        msg_Dbg( p_playlist, "unable to find art" );

    /* Record this album */
    if( psz_artist && psz_album )
    {
        playlist_album_t a;
        a.psz_artist = psz_artist;
        a.psz_album = psz_album;
        a.psz_arturl = input_item_GetArtURL( p_item );
        a.b_found = (i_ret == VLC_EGENERIC ? false : true );
        ARRAY_APPEND( p_playlist->p_fetcher->albums, a );
    }
    else
    {
        free( psz_artist );
        free( psz_album );
    }

    if( p_module )
        module_Unneed( p_playlist, p_module );
    p_playlist->p_private = NULL;
    PL_UNLOCK;

    return i_ret;
}

static void ArtCacheCreateDir( const char *psz_dir )
{
    char newdir[strlen( psz_dir ) + 1];
    strcpy( newdir, psz_dir );
    char * psz_newdir = newdir;
    char * psz = psz_newdir;

    while( *psz )
    {
        while( *psz && *psz != DIR_SEP_CHAR) psz++;
        if( !*psz ) break;
        *psz = 0;
        if( !EMPTY_STR( psz_newdir ) )
            utf8_mkdir( psz_newdir, 0700 );
        *psz = DIR_SEP_CHAR;
        psz++;
    }
    utf8_mkdir( psz_dir, 0700 );
}

static char * ArtCacheGetSanitizedFileName( const char *psz )
{
    char *dup = strdup(psz);
    int i;

    filename_sanitize( dup );

    /* Doesn't create a filename with invalid characters
     * TODO: several filesystems forbid several characters: list them all
     */
    for( i = 0; dup[i] != '\0'; i++ )
    {
        if( dup[i] == DIR_SEP_CHAR )
            dup[i] = ' ';
    }
    return dup;
}

#define ArtCacheGetDirPath(a,b,c,d,e) __ArtCacheGetDirPath(VLC_OBJECT(a),b,c,d,e)
static void __ArtCacheGetDirPath( vlc_object_t *p_obj,
                                  char *psz_dir,
                                  const char *psz_title,
                                  const char *psz_artist, const char *psz_album )
{
    (void)p_obj;
    char *psz_cachedir = config_GetCacheDir();

    if( !EMPTY_STR(psz_artist) && !EMPTY_STR(psz_album) )
    {
        char * psz_album_sanitized = ArtCacheGetSanitizedFileName( psz_album );
        char * psz_artist_sanitized = ArtCacheGetSanitizedFileName( psz_artist );
        snprintf( psz_dir, PATH_MAX, "%s" DIR_SEP
                  "art" DIR_SEP "artistalbum" DIR_SEP "%s" DIR_SEP "%s",
                  psz_cachedir, psz_artist_sanitized, psz_album_sanitized );
        free( psz_album_sanitized );
        free( psz_artist_sanitized );
    }
    else
    {
        char * psz_title_sanitized = ArtCacheGetSanitizedFileName( psz_title );
        snprintf( psz_dir, PATH_MAX, "%s" DIR_SEP
                  "art" DIR_SEP "title" DIR_SEP "%s",
                  psz_cachedir, psz_title_sanitized );
        free( psz_title_sanitized );
    }
    free( psz_cachedir );
}



#define ArtCacheGetFilePath(a,b,c,d,e,f) __ArtCacheGetFilePath(VLC_OBJECT(a),b,c,d,e,f)
static void __ArtCacheGetFilePath( vlc_object_t *p_obj,
                                   char * psz_filename,
                                   const char *psz_title,
                                   const char *psz_artist, const char *psz_album,
                                   const char *psz_extension )
{
    char psz_dir[PATH_MAX+1];
    char * psz_ext;
    ArtCacheGetDirPath( p_obj, psz_dir, psz_title, psz_artist, psz_album );

    if( psz_extension )
    {
        psz_ext = strndup( psz_extension, 6 );
        filename_sanitize( psz_ext );
    }
    else psz_ext = strdup( "" );

    snprintf( psz_filename, PATH_MAX, "file://%s" DIR_SEP "art%s",
              psz_dir, psz_ext );

    free( psz_ext );
}

static int __input_FindArtInCache( vlc_object_t *p_obj, input_item_t *p_item )
{
    char *psz_artist;
    char *psz_album;
    char *psz_title;
    char psz_dirpath[PATH_MAX+1];
    char psz_filepath[PATH_MAX+1];
    char * psz_filename;
    DIR * p_dir;

    psz_artist = input_item_GetArtist( p_item );
    psz_album = input_item_GetAlbum( p_item );
    psz_title = input_item_GetTitle( p_item );
    if( !psz_title ) psz_title = input_item_GetName( p_item );

    if( !psz_title && ( !psz_album || !psz_artist ) )
    {
        free( psz_artist );
        free( psz_album );
        free( psz_title );
        return VLC_EGENERIC;
    }

    ArtCacheGetDirPath( p_obj, psz_dirpath, psz_title,
                           psz_artist, psz_album );

    free( psz_artist );
    free( psz_album );
    free( psz_title );

    /* Check if file exists */
    p_dir = utf8_opendir( psz_dirpath );
    if( !p_dir )
        return VLC_EGENERIC;

    while( (psz_filename = utf8_readdir( p_dir )) )
    {
        if( !strncmp( psz_filename, "art", 3 ) )
        {
            snprintf( psz_filepath, PATH_MAX, "file://%s" DIR_SEP "%s",
                      psz_dirpath, psz_filename );
            input_item_SetArtURL( p_item, psz_filepath );
            free( psz_filename );
            closedir( p_dir );
            return VLC_SUCCESS;
        }
        free( psz_filename );
    }

    /* Not found */
    closedir( p_dir );
    return VLC_EGENERIC;
}

/**
 * Download the art using the URL or an art downloaded
 * This function should be called only if data is not already in cache
 */
int input_DownloadAndCacheArt( playlist_t *p_playlist, input_item_t *p_item )
{
    int i_status = VLC_EGENERIC;
    stream_t *p_stream;
    char psz_filename[PATH_MAX+1];
    char *psz_artist = NULL;
    char *psz_album = NULL;
    char *psz_title = NULL;
    char *psz_arturl;
    char *psz_type;

    psz_artist = input_item_GetArtist( p_item );
    psz_album = input_item_GetAlbum( p_item );
    psz_title = input_item_GetTitle( p_item );
    if( !psz_title )
        psz_title = input_item_GetName( p_item );

    if( !psz_title && (!psz_artist || !psz_album) )
    {
        free( psz_title );
        free( psz_album );
        free( psz_artist );
        return VLC_EGENERIC;
    }

    psz_arturl = input_item_GetArtURL( p_item );
    assert( !EMPTY_STR( psz_arturl ) );

    if( !strncmp( psz_arturl , "file://", 7 ) )
    {
        msg_Dbg( p_playlist, "Album art is local file, no need to cache" );
        free( psz_arturl );
        return VLC_SUCCESS;
    }
    else if( !strncmp( psz_arturl , "APIC", 4 ) )
    {
        msg_Warn( p_playlist, "APIC fetch not supported yet" );
        free( psz_arturl );
        return VLC_EGENERIC;
    }

    psz_type = strrchr( psz_arturl, '.' );
    if( psz_type && strlen( psz_type ) > 5 )
        psz_type = NULL; /* remove extension if it's > to 4 characters */

    /* Warning: psz_title, psz_artist, psz_album may change in ArtCache*() */

    ArtCacheGetDirPath( p_playlist, psz_filename, psz_title, psz_artist,
                        psz_album );
    ArtCacheCreateDir( psz_filename );
    ArtCacheGetFilePath( p_playlist, psz_filename, psz_title, psz_artist,
                         psz_album, psz_type );

    free( psz_artist );
    free( psz_album );
    free( psz_title );

    p_stream = stream_UrlNew( p_playlist, psz_arturl );
    if( p_stream )
    {
        uint8_t p_buffer[65536];
        long int l_read;
        FILE *p_file = utf8_fopen( psz_filename+7, "w" );
        if( p_file == NULL ) {
            msg_Err( p_playlist, "Unable write album art in %s",
                     psz_filename + 7 );
            free( psz_arturl );
            return VLC_EGENERIC;
        }
        int err = 0;
        while( ( l_read = stream_Read( p_stream, p_buffer, sizeof (p_buffer) ) ) )
        {
            if( fwrite( p_buffer, l_read, 1, p_file ) != 1 )
            {
                err = errno;
                break;
            }
        }
        if( fclose( p_file ) && !err )
            err = errno;
        stream_Delete( p_stream );

        if( err )
        {
            errno = err;
            msg_Err( p_playlist, "%s: %m", psz_filename );
        }
        else
            msg_Dbg( p_playlist, "album art saved to %s\n", psz_filename );

        input_item_SetArtURL( p_item, psz_filename );
        i_status = VLC_SUCCESS;
    }
    free( psz_arturl );
    return i_status;
}

void input_ExtractAttachmentAndCacheArt( input_thread_t *p_input )
{
    input_item_t *p_item = p_input->p->input.p_item;
    const char *psz_arturl;
    const char *psz_artist = NULL;
    const char *psz_album = NULL;
    const char *psz_title = NULL;
    char *psz_type = NULL;
    char psz_filename[PATH_MAX+1];
    FILE *f;
    input_attachment_t *p_attachment;
    struct stat s;
    int i_idx;

    /* TODO-fenrir merge input_ArtFind with download and make it set the flags FETCH
     * and then set it here to to be faster */

    psz_arturl = vlc_meta_Get( p_item->p_meta, vlc_meta_ArtworkURL );

    if( !psz_arturl || strncmp( psz_arturl, "attachment://", strlen("attachment://") ) )
    {
        msg_Err( p_input, "internal input error with input_ExtractAttachmentAndCacheArt" );
        return;
    }

    if( input_item_IsArtFetched( p_item ) )
    {
        /* XXX Weird, we should not have end up with attachment:// art url unless there is a race
         * condition */
        msg_Warn( p_input, "internal input error with input_ExtractAttachmentAndCacheArt" );
        input_FindArtInCache( p_input, p_item );
        return;
    }

    /* */
    for( i_idx = 0, p_attachment = NULL; i_idx < p_input->p->i_attachment; i_idx++ )
    {
        if( !strcmp( p_input->p->attachment[i_idx]->psz_name,
                     &psz_arturl[strlen("attachment://")] ) )
        {
            p_attachment = p_input->p->attachment[i_idx];
            break;
        }
    }
    if( !p_attachment || p_attachment->i_data <= 0 )
    {
        msg_Warn( p_input, "internal input error with input_ExtractAttachmentAndCacheArt" );
        return;
    }

    psz_artist = vlc_meta_Get( p_item->p_meta, vlc_meta_Artist );
    psz_album = vlc_meta_Get( p_item->p_meta, vlc_meta_Album );
    psz_title = vlc_meta_Get( p_item->p_meta, vlc_meta_Title );
    if( !strcmp( p_attachment->psz_mime, "image/jpeg" ) )
        psz_type = strdup( ".jpg" );
    else if( !strcmp( p_attachment->psz_mime, "image/png" ) )
        psz_type = strdup( ".png" );

    if( !psz_title )
        psz_title = p_item->psz_name;

    if( (!psz_artist || !psz_album ) && !psz_title )
        return;

    ArtCacheGetDirPath( p_input, psz_filename, psz_title, psz_artist, psz_album );
    ArtCacheCreateDir( psz_filename );
    ArtCacheGetFilePath( p_input, psz_filename, psz_title, psz_artist, psz_album, psz_type );
    free( psz_type );

    /* Check if we already dumped it */
    if( !utf8_stat( psz_filename+7, &s ) )
    {
        vlc_meta_Set( p_item->p_meta, vlc_meta_ArtworkURL, psz_filename );
        return;
    }

    f = utf8_fopen( psz_filename+7, "w" );
    if( f )
    {
        if( fwrite( p_attachment->p_data, p_attachment->i_data, 1, f ) != 1 )
            msg_Err( p_input, "%s: %m", psz_filename );
        else
        {
            msg_Dbg( p_input, "album art saved to %s\n", psz_filename );
            vlc_meta_Set( p_item->p_meta, vlc_meta_ArtworkURL, psz_filename );
        }
        fclose( f );
    }
}
