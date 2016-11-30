/*****************************************************************************
 * video_yuv.c: MMX YUV transformation functions
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
 * vout_InitYUV: allocate and initialize translations tables
 *****************************************************************************
 * This function will allocate memory to store translation tables, depending
 * of the screen depth.
 *****************************************************************************/
int yuv_MMXInit( vout_thread_t *p_vout )
{
    size_t      tables_size;                        /* tables size, in bytes */

    /* Computes tables size for 8bbp only */
    if( p_vout->i_bytes_per_pixel == 1 )
    {
        tables_size = sizeof( u8 )
                * (p_vout->b_grayscale ? GRAY_TABLE_SIZE : PALETTE_TABLE_SIZE);

        /* Allocate memory */
        p_vout->yuv.p_base = malloc( tables_size );
        if( p_vout->yuv.p_base == NULL )
        {
            intf_ErrMsg("error: %s\n", strerror(ENOMEM));
            return( 1 );
        }
    }
    else
    {
        p_vout->yuv.p_base = NULL;
    }

    /* Allocate memory for conversion buffer and offset array */
    p_vout->yuv.p_buffer = malloc( VOUT_MAX_WIDTH * p_vout->i_bytes_per_pixel );
    if( p_vout->yuv.p_buffer == NULL )
    {
        intf_ErrMsg("error: %s\n", strerror(ENOMEM));
        free( p_vout->yuv.p_base );
        return( 1 );
    }
    p_vout->yuv.p_offset = malloc( p_vout->i_width * sizeof( int ) );
    if( p_vout->yuv.p_offset == NULL )
    {
        intf_ErrMsg("error: %s\n", strerror(ENOMEM));
        free( p_vout->yuv.p_base );
        free( p_vout->yuv.p_buffer );
        return( 1 );
    }

    /* Initialize tables */
    SetYUV( p_vout );
    return( 0 );
}

/*****************************************************************************
 * yuv_MMXEnd: destroy translations tables
 *****************************************************************************
 * Free memory allocated by yuv_MMXCreate.
 *****************************************************************************/
void yuv_MMXEnd( vout_thread_t *p_vout )
{
    if( p_vout->i_bytes_per_pixel == 1 )
    {
        free( p_vout->yuv.p_base );
    }

    free( p_vout->yuv.p_buffer );
    free( p_vout->yuv.p_offset );
}

/*****************************************************************************
 * yuv_MMXReset: re-initialize translations tables
 *****************************************************************************
 * This function will initialize the tables allocated by vout_CreateTables and
 * set functions pointers.
 *****************************************************************************/
int yuv_MMXReset( vout_thread_t *p_vout )
{
    yuv_MMXEnd( p_vout );
    return( yuv_MMXInit( p_vout ) );
}

/* following functions are local */

/*****************************************************************************
 * SetYUV: compute tables and set function pointers
+ *****************************************************************************/
void SetYUV( vout_thread_t *p_vout )
{
    int         i_index;                                  /* index in tables */

    /*
     * Set pointers and build YUV tables
     */
    if( p_vout->b_grayscale )
    {
        /* Grayscale: build gray table */
        if( p_vout->i_bytes_per_pixel == 1 )
        {
            u16 bright[256], transp[256];

            for( i_index = 0; i_index < 256; i_index++)
            {
                bright[ i_index ] = i_index << 8;
                transp[ i_index ] = 0;
            }
            /* the colors have been allocated, we can set the palette */
            p_vout->p_set_palette( p_vout, bright, bright, bright, transp );
            p_vout->i_white_pixel = 0xff;
            p_vout->i_black_pixel = 0x00;
            p_vout->i_gray_pixel = 0x44;
            p_vout->i_blue_pixel = 0x3b;
        }
    }
    else
    {
        /* Color: build red, green and blue tables */
        if( p_vout->i_bytes_per_pixel == 1 )
        {
            #define RGB_MIN 0
            #define RGB_MAX 255
            #define CLIP( x ) ( ((x < 0) ? 0 : (x > 255) ? 255 : x) << 8 )
            #define SHIFT 20
            #define U_GREEN_COEF    ((int)(-0.391 * (1<<SHIFT) / 1.164))
            #define U_BLUE_COEF     ((int)(2.018 * (1<<SHIFT) / 1.164))
            #define V_RED_COEF      ((int)(1.596 * (1<<SHIFT) / 1.164))
            #define V_GREEN_COEF    ((int)(-0.813 * (1<<SHIFT) / 1.164))

            int y,u,v;
            int r,g,b;
            int uvr, uvg, uvb;
            int i = 0, j = 0;
            u16 red[256], green[256], blue[256], transp[256];
            unsigned char lookup[PALETTE_TABLE_SIZE];

            p_vout->yuv.yuv.p_rgb8 = (u8 *)p_vout->yuv.p_base;

            /* this loop calculates the intersection of an YUV box
             * and the RGB cube. */
            for ( y = 0; y <= 256; y += 16 )
            {
                for ( u = 0; u <= 256; u += 32 )
                for ( v = 0; v <= 256; v += 32 )
                {
                    uvr = (V_RED_COEF*(v-128)) >> SHIFT;
                    uvg = (U_GREEN_COEF*(u-128) + V_GREEN_COEF*(v-128)) >> SHIFT;
                    uvb = (U_BLUE_COEF*(u-128)) >> SHIFT;
                    r = y + uvr;
                    g = y + uvg;
                    b = y + uvb;

                    if( r >= RGB_MIN && g >= RGB_MIN && b >= RGB_MIN
                            && r <= RGB_MAX && g <= RGB_MAX && b <= RGB_MAX )
                    {
                        /* this one should never happen unless someone fscked up my code */
                        if(j == 256) { intf_ErrMsg( "vout error: no colors left to build palette\n" ); break; }

                        /* clip the colors */
                        red[j] = CLIP( r );
                        green[j] = CLIP( g );
                        blue[j] = CLIP( b );
                        transp[j] = 0;

                        /* allocate color */
                        lookup[i] = 1;
                        p_vout->yuv.yuv.p_rgb8[i++] = j;
                        j++;
                    }
                    else
                    {
                        lookup[i] = 0;
                        p_vout->yuv.yuv.p_rgb8[i++] = 0;
                    }
                }
                i += 128-81;
            }

            /* the colors have been allocated, we can set the palette */
            /* there will eventually be a way to know which colors
             * couldn't be allocated and try to find a replacement */
            p_vout->p_set_palette( p_vout, red, green, blue, transp );

            p_vout->i_white_pixel = 0xff;
            p_vout->i_black_pixel = 0x00;
            p_vout->i_gray_pixel = 0x44;
            p_vout->i_blue_pixel = 0x3b;

            i = 0;
            /* this loop allocates colors that got outside
             * the RGB cube */
            for ( y = 0; y <= 256; y += 16 )
            {
                for ( u = 0; u <= 256; u += 32 )
                {
                    for ( v = 0; v <= 256; v += 32 )
                    {
                        int u2, v2;
                        int dist, mindist = 100000000;

                        if( lookup[i] || y==0)
                        {
                            i++;
                            continue;
                        }

                        /* heavy. yeah. */
                        for( u2 = 0; u2 <= 256; u2 += 32 )
                        for( v2 = 0; v2 <= 256; v2 += 32 )
                        {
                            j = ((y>>4)<<7) + (u2>>5)*9 + (v2>>5);
                            dist = (u-u2)*(u-u2) + (v-v2)*(v-v2);
                            if( lookup[j] )
                            /* find the nearest color */
                            if( dist < mindist )
                            {
                                p_vout->yuv.yuv.p_rgb8[i] = p_vout->yuv.yuv.p_rgb8[j];
                                mindist = dist;
                            }
                            j -= 128;
                            if( lookup[j] )
                            /* find the nearest color */
                            if( dist + 128 < mindist )
                            {
                                p_vout->yuv.yuv.p_rgb8[i] = p_vout->yuv.yuv.p_rgb8[j];
                                mindist = dist + 128;
                            }
                        }
                        i++;
                    }
                }
                i += 128-81;
            }
        }
    }

    /*
     * Set functions pointers
     */
    if( p_vout->b_grayscale )
    {
        /* Grayscale */
        switch( p_vout->i_bytes_per_pixel )
        {
        case 1:
            p_vout->yuv.p_Convert420 = (vout_yuv_convert_t *) ConvertY4Gray8;
            p_vout->yuv.p_Convert422 = (vout_yuv_convert_t *) ConvertY4Gray8;
            p_vout->yuv.p_Convert444 = (vout_yuv_convert_t *) ConvertY4Gray8;
            break;
        case 2:
            p_vout->yuv.p_Convert420 = (vout_yuv_convert_t *) ConvertY4Gray16;
            p_vout->yuv.p_Convert422 = (vout_yuv_convert_t *) ConvertY4Gray16;
            p_vout->yuv.p_Convert444 = (vout_yuv_convert_t *) ConvertY4Gray16;
            break;
        case 3:
            p_vout->yuv.p_Convert420 = (vout_yuv_convert_t *) ConvertYUV420RGB24;
            p_vout->yuv.p_Convert422 = (vout_yuv_convert_t *) ConvertY4Gray24;
            p_vout->yuv.p_Convert444 = (vout_yuv_convert_t *) ConvertY4Gray24;
            break;
        case 4:
            p_vout->yuv.p_Convert420 = (vout_yuv_convert_t *) ConvertYUV420RGB32;
            p_vout->yuv.p_Convert422 = (vout_yuv_convert_t *) ConvertY4Gray32;
            p_vout->yuv.p_Convert444 = (vout_yuv_convert_t *) ConvertY4Gray32;
            break;
        }
    }
    else
    {
        /* Color */
        switch( p_vout->i_bytes_per_pixel )
        {
        case 1:
            p_vout->yuv.p_Convert420 = (vout_yuv_convert_t *) ConvertYUV420RGB8;
            p_vout->yuv.p_Convert422 = (vout_yuv_convert_t *) ConvertYUV422RGB8;
            p_vout->yuv.p_Convert444 = (vout_yuv_convert_t *) ConvertYUV444RGB8;
            break;
        case 2:
            p_vout->yuv.p_Convert420 =   (vout_yuv_convert_t *) ConvertYUV420RGB16;
            p_vout->yuv.p_Convert422 =   (vout_yuv_convert_t *) ConvertYUV422RGB16;
            p_vout->yuv.p_Convert444 =   (vout_yuv_convert_t *) ConvertYUV444RGB16;
            break;
        case 3:
            p_vout->yuv.p_Convert420 =   (vout_yuv_convert_t *) ConvertYUV420RGB24;
            p_vout->yuv.p_Convert422 =   (vout_yuv_convert_t *) ConvertYUV422RGB24;
            p_vout->yuv.p_Convert444 =   (vout_yuv_convert_t *) ConvertYUV444RGB24;
            break;
        case 4:
            p_vout->yuv.p_Convert420 =   (vout_yuv_convert_t *) ConvertYUV420RGB32;
            p_vout->yuv.p_Convert422 =   (vout_yuv_convert_t *) ConvertYUV422RGB32;
            p_vout->yuv.p_Convert444 =   (vout_yuv_convert_t *) ConvertYUV444RGB32;
            break;
        }
    }
}

/*****************************************************************************
 * SetOffset: build offset array for conversion functions
 *****************************************************************************
 * This function will build an offset array used in later conversion functions.
 * It will also set horizontal and vertical scaling indicators.
 *****************************************************************************/
void SetOffset( int i_width, int i_height, int i_pic_width, int i_pic_height,
                boolean_t *pb_h_scaling, int *pi_v_scaling, int *p_offset,
                boolean_t b_double )
{
    int i_x;                                    /* x position in destination */
    int i_scale_count;                                     /* modulo counter */

    /*
     * Prepare horizontal offset array
     */
    if( i_pic_width - i_width == 0 )
    {
        /* No horizontal scaling: YUV conversion is done directly to picture */
        *pb_h_scaling = 0;
    }
    else if( i_pic_width - i_width > 0 )
    {
        /* Prepare scaling array for horizontal extension */
        *pb_h_scaling =  1;
        i_scale_count =  i_pic_width;
        for( i_x = i_width; i_x--; )
        {
            while( (i_scale_count -= i_width) > 0 )
            {
                *p_offset++ = 0;
            }
            *p_offset++ = 1;
            i_scale_count += i_pic_width;
        }
    }
    else /* if( i_pic_width - i_width < 0 ) */
    {
        /* Prepare scaling array for horizontal reduction */
        *pb_h_scaling =  1;
        i_scale_count =  i_width;
        for( i_x = i_pic_width; i_x--; )
        {
            *p_offset = 1;
            while( (i_scale_count -= i_pic_width) > 0 )
            {
                *p_offset += 1;
            }
            p_offset++;
            i_scale_count += i_width;
        }
    }

    /*
     * Set vertical scaling indicator
     */
    if( i_pic_height - i_height == 0 )
    {
        *pi_v_scaling = 0;
    }
    else if( i_pic_height - i_height > 0 )
    {
        *pi_v_scaling = 1;
    }
    else /* if( i_pic_height - i_height < 0 ) */
    {
        *pi_v_scaling = -1;
    }
}

