/*****************************************************************************
 * video_yuv8.c: MMX YUV transformation functions for 8bpp
 * Provides functions to perform the YUV conversion. The functions provided here
 * are a complete and portable C implementation, and may be replaced in certain
 * case by optimized functions.
 *****************************************************************************
 * Copyright (C) 1999, 2000 VideoLAN
 *
 * Authors:
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 *****************************************************************************/

/*****************************************************************************
 * Preamble
 *****************************************************************************/
#include "defs.h"

#include <math.h>                                            /* exp(), pow() */
#include <errno.h>                                                 /* ENOMEM */
#include <stdlib.h>                                                /* free() */
#include <string.h>                                            /* strerror() */

#include "config.h"
#include "common.h"
#include "threads.h"
#include "mtime.h"
#include "plugins.h"
#include "video.h"
#include "video_output.h"
#include "video_yuv.h"

#include "intf_msg.h"

/*****************************************************************************
 * ConvertY4Gray8: grayscale YUV 4:x:x to RGB 8 bpp
 *****************************************************************************/
void ConvertY4Gray8( YUV_ARGS_8BPP )
{
    intf_ErrMsg( "yuvmmx error: unhandled function, grayscale, bpp = 8\n" );
}

/*****************************************************************************
 * ConvertYUV420RGB8: color YUV 4:2:0 to RGB 8 bpp
 *****************************************************************************/
void ConvertYUV420RGB8( YUV_ARGS_8BPP )
{
    intf_ErrMsg( "yuvmmx error: unhandled function, chroma = 420, bpp = 8\n" );
}

/*****************************************************************************
 * ConvertYUV422RGB8: color YUV 4:2:2 to RGB 8 bpp
 *****************************************************************************/
void ConvertYUV422RGB8( YUV_ARGS_8BPP )
{
    intf_ErrMsg( "yuvmmx error: unhandled function, chroma = 422, bpp = 8\n" );
}

/*****************************************************************************
 * ConvertYUV444RGB8: color YUV 4:4:4 to RGB 8 bpp
 *****************************************************************************/
void ConvertYUV444RGB8( YUV_ARGS_8BPP )
{
    intf_ErrMsg( "yuvmmx error: unhandled function, chroma = 444, bpp = 8\n" );
}

