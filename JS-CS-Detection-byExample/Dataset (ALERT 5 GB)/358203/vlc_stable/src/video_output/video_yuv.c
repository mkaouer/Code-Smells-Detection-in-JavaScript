/*****************************************************************************
 * video_yuv.c: YUV transformation functions
 * These functions set up YUV tables for colorspace conversion
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

#include "main.h"

/*****************************************************************************
 * vout_InitYUV: allocate and initialize translation tables
 *****************************************************************************
 * This function will allocate memory to store translation tables, depending
 * on the screen depth.
 *****************************************************************************/
int vout_InitYUV( vout_thread_t *p_vout )
{
    typedef void ( yuv_getplugin_t ) ( vout_thread_t * p_vout );
    
    int          i_index;
    int          i_best_index = 0, i_best_score = 0;

    /* Get a suitable YUV plugin */
    for( i_index = 0 ; i_index < p_main->p_bank->i_plugin_count ; i_index++ )
    {
        /* If there's a plugin in p_info ... */
        if( p_main->p_bank->p_info[ i_index ] != NULL )
        {
            /* ... and if this plugin provides the functions we want ... */
            if( p_main->p_bank->p_info[ i_index ]->yuv_GetPlugin != NULL )
            {
                /* ... and if this plugin has a good score ... */
                if(  p_main->p_bank->p_info[ i_index ]->i_score > i_best_score )
                {
                    /* ... then take it */
                    i_best_score = p_main->p_bank->p_info[ i_index ]->i_score;
                    i_best_index = i_index;
                }
            }
        }
    }

    if( i_best_score == 0 )
    {
        /* this should NEVER happen ! */
        free( p_vout );
        return( 12 ); 
    }

    /* Get the plugin functions */
    ( ( yuv_getplugin_t * ) p_main->p_bank->p_info[ i_best_index ]->yuv_GetPlugin)( p_vout );
 
    
    return p_vout->p_yuv_init( p_vout );
}

/*****************************************************************************
 * vout_ResetYUV: re-initialize translation tables
 *****************************************************************************
 * This function will initialize the tables allocated by vout_InitYUV and
 * set functions pointers.
 *****************************************************************************/
int vout_ResetYUV( vout_thread_t *p_vout )
{
    p_vout->p_yuv_end( p_vout );
    return( p_vout->p_yuv_init( p_vout ) );
}

/*****************************************************************************
 * vout_EndYUV: destroy translation tables
 *****************************************************************************
 * Free memory allocated by vout_InitYUV
 *****************************************************************************/
void vout_EndYUV( vout_thread_t *p_vout )
{
    p_vout->p_yuv_end( p_vout );
}

