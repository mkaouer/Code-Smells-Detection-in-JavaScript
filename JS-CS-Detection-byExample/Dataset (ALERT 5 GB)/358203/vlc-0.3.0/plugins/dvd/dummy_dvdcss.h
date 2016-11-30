/*****************************************************************************
 * dummy_dvdcss.h: Dummy libdvdcss header.
 *****************************************************************************
 * Copyright (C) 2001 VideoLAN
 * $Id: dummy_dvdcss.h,v 1.7 2002/01/23 03:15:31 stef Exp $
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
 * The libdvdcss structure
 *****************************************************************************/
typedef struct dvdcss_s* dvdcss_handle;

/*****************************************************************************
 * Defines and flags
 *****************************************************************************/
#define DVDCSS_BLOCK_SIZE      2048

#define DVDCSS_NOFLAGS         0
#define DVDCSS_READ_DECRYPT    (1 << 0)
#define DVDCSS_SEEK_MPEG       (1 << 0)
#define DVDCSS_SEEK_KEY        (1 << 1)

/*****************************************************************************
 * Exported prototypes
 *****************************************************************************/
dvdcss_handle dummy_dvdcss_open  ( char * );
int           dummy_dvdcss_close ( dvdcss_handle );
int           dummy_dvdcss_title ( dvdcss_handle, int );
int           dummy_dvdcss_seek  ( dvdcss_handle, int, int );
int           dummy_dvdcss_read  ( dvdcss_handle, void *, int, int );
int           dummy_dvdcss_readv ( dvdcss_handle, void *, int, int );
char *        dummy_dvdcss_error ( dvdcss_handle );

/*****************************************************************************
 * Pointers which will be filled either with dummy_dvdcss functions or
 * with the dlopen()ed ones.
 *****************************************************************************/
#define ____dvdcss_open  dvdcss_open
#define ____dvdcss_close dvdcss_close
#define ____dvdcss_title dvdcss_title
#define ____dvdcss_seek  dvdcss_seek
#define ____dvdcss_read  dvdcss_read
#define ____dvdcss_readv dvdcss_readv
#define ____dvdcss_error dvdcss_error

dvdcss_handle (* ____dvdcss_open )   ( char * );
int           (* ____dvdcss_close )  ( dvdcss_handle );
int           (* ____dvdcss_title )  ( dvdcss_handle, int );
int           (* ____dvdcss_seek )   ( dvdcss_handle, int, int );
int           (* ____dvdcss_read )   ( dvdcss_handle, void *, int, int );
int           (* ____dvdcss_readv )  ( dvdcss_handle, void *, int, int );
char *        (* ____dvdcss_error )  ( dvdcss_handle );


