/*****************************************************************************
 * vout_ggi.c: GGI video output display method
 *****************************************************************************
 * Copyright (C) 1998, 1999, 2000 VideoLAN
 *
 * Authors:
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
 * Preamble
 *****************************************************************************/
#include "defs.h"

#include <errno.h>                                                 /* ENOMEM */
#include <stdlib.h>                                                /* free() */
#include <string.h>                                            /* strerror() */

#include <ggi/ggi.h>

#include "config.h"
#include "common.h"
#include "threads.h"
#include "mtime.h"
#include "plugins.h"

#include "video.h"
#include "video_output.h"

#include "intf_msg.h"

/*****************************************************************************
 * vout_sys_t: video output GGI method descriptor
 *****************************************************************************
 * This structure is part of the video output thread descriptor.
 * It describes the GGI specific properties of an output thread.
 *****************************************************************************/
typedef struct vout_ggi_sys_s
{
    /* GGI system informations */
    ggi_visual_t        p_display;                         /* display device */

    /* Buffers informations */
    ggi_directbuffer *  p_buffer[2];                              /* buffers */
    boolean_t           b_must_acquire;   /* must be acquired before writing */
} vout_ggi_sys_t;

#define P_SYS ((vout_ggi_sys_t *)p_vout->p_sys)

/*****************************************************************************
 * Local prototypes
 *****************************************************************************/
static int     GGIOpenDisplay   ( vout_thread_t *p_vout, char *psz_display );
static void    GGICloseDisplay  ( vout_thread_t *p_vout );

/*****************************************************************************
 * vout_GGISysCreate: allocate GGI video thread output method
 *****************************************************************************
 * This function allocate and initialize a GGI vout method. It uses some of the
 * vout properties to choose the correct mode, and change them according to the
 * mode actually used.
 *****************************************************************************/
int vout_GGISysCreate( vout_thread_t *p_vout, char *psz_display, int i_root_window )
{
    /* Allocate structure */
    p_vout->p_sys = malloc( sizeof( vout_ggi_sys_t ) );
    if( p_vout->p_sys == NULL )
    {
        intf_ErrMsg("error: %s\n", strerror(ENOMEM) );
        return( 1 );
    }

    /* Open and initialize device */
    if( GGIOpenDisplay( p_vout, psz_display ) )
    {
        intf_ErrMsg("error: can't initialize GGI display\n");
        free( p_vout->p_sys );
        return( 1 );
    }
    return( 0 );
}

/*****************************************************************************
 * vout_GGISysInit: initialize GGI video thread output method
 *****************************************************************************
 * This function initialize the GGI display device.
 *****************************************************************************/
int vout_GGISysInit( vout_thread_t *p_vout )
{
    /* Acquire first buffer */
    if( P_SYS->b_must_acquire )
    {
        ggiResourceAcquire( P_SYS->p_buffer[ p_vout->i_buffer_index ]->resource, GGI_ACTYPE_WRITE );
    }

    return( 0 );
}

/*****************************************************************************
 * vout_GGISysEnd: terminate Sys video thread output method
 *****************************************************************************
 * Terminate an output method created by vout_GGISysCreate
 *****************************************************************************/
void vout_GGISysEnd( vout_thread_t *p_vout )
{
    /* Release buffer */
    if( P_SYS->b_must_acquire )
    {
        ggiResourceRelease( P_SYS->p_buffer[ p_vout->i_buffer_index ]->resource );
    }
}

/*****************************************************************************
 * vout_GGISysDestroy: destroy Sys video thread output method
 *****************************************************************************
 * Terminate an output method created by vout_GGISysCreate
 *****************************************************************************/
void vout_GGISysDestroy( vout_thread_t *p_vout )
{
    GGICloseDisplay( p_vout );
    free( p_vout->p_sys );
}

/*****************************************************************************
 * vout_GGISysManage: handle Sys events
 *****************************************************************************
 * This function should be called regularly by video output thread. It returns
 * a non null value if an error occured.
 *****************************************************************************/
int vout_GGISysManage( vout_thread_t *p_vout )
{
    /* FIXME: 8bpp: change palette ?? */
    return( 0 );
}

/*****************************************************************************
 * vout_GGISysDisplay: displays previously rendered output
 *****************************************************************************
 * This function send the currently rendered image to the display, wait until
 * it is displayed and switch the two rendering buffer, preparing next frame.
 *****************************************************************************/
void vout_GGISysDisplay( vout_thread_t *p_vout )
{
    /* Change display frame */
    if( P_SYS->b_must_acquire )
    {
        ggiResourceRelease( P_SYS->p_buffer[ p_vout->i_buffer_index ]->resource );
    }
    ggiFlush( P_SYS->p_display ); /* XXX?? */
    ggiSetDisplayFrame( P_SYS->p_display,
                        P_SYS->p_buffer[ p_vout->i_buffer_index ]->frame );

    /* Swap buffers and change write frame */
    if( P_SYS->b_must_acquire )
    {
        ggiResourceAcquire( P_SYS->p_buffer[ (p_vout->i_buffer_index + 1) & 1]->resource,
                            GGI_ACTYPE_WRITE );
    }
    ggiSetWriteFrame( P_SYS->p_display,
                      P_SYS->p_buffer[ (p_vout->i_buffer_index + 1) & 1]->frame );
}

/*****************************************************************************
 * vout_GGISysGetVisual: send visual to interface driver
 *****************************************************************************
 * This function is not part of the regular vout_GGISys* API, but is used by GGI
 * interface to get back visual display pointer once the output thread has
 * been spawned. This visual is used to keep track of keyboard events.
 *****************************************************************************/
ggi_visual_t    vout_GGISysGetVisual( vout_thread_t *p_vout )
{
    return( P_SYS->p_display );
}

/* following functions are local */

/*****************************************************************************
 * GGIOpenDisplay: open and initialize GGI device
 *****************************************************************************
 * Open and initialize display according to preferences specified in the vout
 * thread fields.
 *****************************************************************************/
static int GGIOpenDisplay( vout_thread_t *p_vout, char *psz_display )
{
    ggi_mode    mode;                                     /* mode descriptor */
    ggi_color   col_fg;                                  /* foreground color */
    ggi_color   col_bg;                                  /* background color */
    int         i_index;                               /* all purposes index */

    /* Initialize library */
    if( ggiInit() )
    {
        intf_ErrMsg("error: can't initialize GGI library\n");
        return( 1 );
    }

    /* Open display */
    P_SYS->p_display = ggiOpen( psz_display, NULL );
    if( P_SYS->p_display == NULL )
    {
        intf_ErrMsg("error: can't open GGI default display\n");
        ggiExit();
        return( 1 );
    }

    /* Find most appropriate mode */
    mode.frames =       2;                                      /* 2 buffers */
    mode.visible.x =    p_vout->i_width;                    /* minimum width */
    mode.visible.y =    p_vout->i_height;                  /* minimum height */
    mode.virt.x =       GGI_AUTO;
    mode.virt.y =       GGI_AUTO;
    mode.size.x =       GGI_AUTO;
    mode.size.y =       GGI_AUTO;
    mode.graphtype =    GT_15BIT;             /* minimum usable screen depth */
    mode.dpp.x =        GGI_AUTO;
    mode.dpp.y =        GGI_AUTO;
    ggiCheckMode( P_SYS->p_display, &mode );

    /* Check that returned mode has some minimum properties */
    /* XXX?? */

    /* Set mode */
    if( ggiSetMode( P_SYS->p_display, &mode ) )
    {
        intf_ErrMsg("error: can't set GGI mode\n");
        ggiClose( P_SYS->p_display );
        ggiExit();
        return( 1 );
    }

    /* Check buffers properties */
    P_SYS->b_must_acquire = 0;
    for( i_index = 0; i_index < 2; i_index++ )
    {
        /* Get buffer address */
        P_SYS->p_buffer[ i_index ] =
            ggiDBGetBuffer( P_SYS->p_display, i_index );
        if( P_SYS->p_buffer[ i_index ] == NULL )
        {
            intf_ErrMsg("error: double buffering is not possible\n");
            ggiClose( P_SYS->p_display );
            ggiExit();
            return( 1 );
        }

        /* Check buffer properties */
        if( ! (P_SYS->p_buffer[ i_index ]->type & GGI_DB_SIMPLE_PLB) ||
            (P_SYS->p_buffer[ i_index ]->page_size != 0) ||
            (P_SYS->p_buffer[ i_index ]->write == NULL ) ||
            (P_SYS->p_buffer[ i_index ]->noaccess != 0) ||
            (P_SYS->p_buffer[ i_index ]->align != 0) )
        {
            intf_ErrMsg("error: incorrect video memory type\n");
            ggiClose( P_SYS->p_display );
            ggiExit();
            return( 1 );
        }

        /* Check if buffer needs to be acquired before write */
        if( ggiResourceMustAcquire( P_SYS->p_buffer[ i_index ]->resource ) )
        {
            P_SYS->b_must_acquire = 1;
        }
    }
#ifdef DEBUG
    if( P_SYS->b_must_acquire )
    {
        intf_DbgMsg("buffers must be acquired\n");
    }
#endif

    /* Set graphic context colors */
    col_fg.r = col_fg.g = col_fg.b = -1;
    col_bg.r = col_bg.g = col_bg.b = 0;
    if( ggiSetGCForeground(P_SYS->p_display,
                           ggiMapColor(P_SYS->p_display,&col_fg)) ||
        ggiSetGCBackground(P_SYS->p_display,
                           ggiMapColor(P_SYS->p_display,&col_bg)) )
    {
        intf_ErrMsg("error: can't set colors\n");
        ggiClose( P_SYS->p_display );
        ggiExit();
        return( 1 );
    }

    /* Set clipping for text */
    if( ggiSetGCClipping(P_SYS->p_display, 0, 0,
                         mode.visible.x, mode.visible.y ) )
    {
        intf_ErrMsg("error: can't set clipping\n");
        ggiClose( P_SYS->p_display );
        ggiExit();
        return( 1 );
    }

    /* Set thread information */
    p_vout->i_width =           mode.visible.x;
    p_vout->i_height =          mode.visible.y;
    p_vout->i_bytes_per_line =  P_SYS->p_buffer[ 0 ]->buffer.plb.stride;
    p_vout->i_screen_depth =    P_SYS->p_buffer[ 0 ]->buffer.plb->pixelformat.depth;
    p_vout->i_bytes_per_pixel = P_SYS->p_buffer[ 0 ]->buffer.plb->pixelformat.size / 8;
    p_vout->i_red_mask =        P_SYS->p_buffer[ 0 ]->buffer.plb->pixelformat.red_mask;
    p_vout->i_green_mask =      P_SYS->p_buffer[ 0 ]->buffer.plb->pixelformat.green_mask;
    p_vout->i_blue_mask =       P_SYS->p_buffer[ 0 ]->buffer.plb->pixelformat.blue_mask;
    /* FIXME: palette in 8bpp ?? */

    /* Set and initialize buffers */
    vout_SetBuffers( p_vout, P_SYS->p_buffer[ 0 ]->write, P_SYS->p_buffer[ 1 ]->write );

    return( 0 );
}

/*****************************************************************************
 * GGICloseDisplay: close and reset GGI device
 *****************************************************************************
 * This function returns all resources allocated by GGIOpenDisplay and restore
 * the original state of the device.
 *****************************************************************************/
static void GGICloseDisplay( vout_thread_t *p_vout )
{
    /* Restore original mode and close display */
    ggiClose( P_SYS->p_display );

    /* Exit library */
    ggiExit();
}

