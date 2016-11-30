/*****************************************************************************
 * charset.h: Unicode UTF-8 wrappers function
 *****************************************************************************
 * Copyright (C) 2003-2005 the VideoLAN team
 * Copyright © 2005-2006 Rémi Denis-Courmont
 * $Id: 5bd3faa429b145fe0128c04f1b639668b03997da $
 *
 * Author: Rémi Denis-Courmont <rem # videolan,org>
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

#ifndef VLC_CHARSET_H
#define VLC_CHARSET_H 1

/**
 * \file
 * This files handles locale conversions in vlc
 */

#include <stdarg.h>
#include <sys/types.h>
#include <dirent.h>

VLC_EXPORT( void, LocaleFree, ( const char * ) );
VLC_EXPORT( char *, FromLocale, ( const char * ) );
VLC_EXPORT( char *, FromLocaleDup, ( const char * ) );
VLC_EXPORT( char *, ToLocale, ( const char * ) );
VLC_EXPORT( char *, ToLocaleDup, ( const char * ) );

/* TODO: move all of this to "vlc_fs.h" or something like that */
VLC_EXPORT( int, utf8_open, ( const char *filename, int flags, mode_t mode ) );
VLC_EXPORT( FILE *, utf8_fopen, ( const char *filename, const char *mode ) );
VLC_EXPORT( DIR *, utf8_opendir, ( const char *dirname ) );
VLC_EXPORT( char *, utf8_readdir, ( DIR *dir ) );
VLC_EXPORT( int, utf8_loaddir, ( DIR *dir, char ***namelist, int (*select)( const char * ), int (*compar)( const char **, const char ** ) ) );
VLC_EXPORT( int, utf8_scandir, ( const char *dirname, char ***namelist, int (*select)( const char * ), int (*compar)( const char **, const char ** ) ) );
VLC_EXPORT( int, utf8_mkdir, ( const char *filename, mode_t mode ) );
VLC_EXPORT( int, utf8_unlink, ( const char *filename ) );

#ifdef WIN32
# define stat _stati64
#endif

VLC_EXPORT( int, utf8_stat, ( const char *filename, struct stat *buf ) );
VLC_EXPORT( int, utf8_lstat, ( const char *filename, struct stat *buf ) );

VLC_EXPORT( int, utf8_vfprintf, ( FILE *stream, const char *fmt, va_list ap ) );
VLC_EXPORT( int, utf8_fprintf, ( FILE *, const char *, ... ) LIBVLC_FORMAT( 2, 3 ) );

VLC_EXPORT( char *, EnsureUTF8, ( char * ) );
VLC_EXPORT( const char *, IsUTF8, ( const char * ) );

#ifdef WIN32
static inline char *FromWide (const wchar_t *wide)
{
    size_t len = WideCharToMultiByte (CP_UTF8, 0, wide, -1, NULL, 0, NULL, NULL);
    if (len == 0)
        return NULL;

    char *out = (char *)malloc (len);

    if (out)
        WideCharToMultiByte (CP_UTF8, 0, wide, -1, out, len, NULL, NULL);
    return out;
}
#endif

VLC_EXPORT( const char *, GetFallbackEncoding, ( void ) );

VLC_EXPORT( double, us_strtod, ( const char *, char ** ) );
VLC_EXPORT( double, us_atof, ( const char * ) );

#endif
