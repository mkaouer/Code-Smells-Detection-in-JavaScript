/*****************************************************************************
 * dummy.c : dummy plugin for vlc
 *****************************************************************************
 * Copyright (C) 2000, 2001 VideoLAN
 * $Id: dummy.c,v 1.17 2002/03/01 00:33:18 massiot Exp $
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
 * Preamble
 *****************************************************************************/
#include <stdlib.h>                                      /* malloc(), free() */
#include <string.h>

#include <videolan/vlc.h>

/*****************************************************************************
 * Capabilities defined in the other files.
 *****************************************************************************/
void _M( access_getfunctions ) ( function_list_t * p_function_list );
void _M( demux_getfunctions ) ( function_list_t * p_function_list );
void _M( aout_getfunctions )  ( function_list_t * p_function_list );
void _M( vout_getfunctions )  ( function_list_t * p_function_list );
void _M( intf_getfunctions )  ( function_list_t * p_function_list );

/*****************************************************************************
 * Build configuration tree.
 *****************************************************************************/
MODULE_CONFIG_START
MODULE_CONFIG_STOP


MODULE_INIT_START
    SET_DESCRIPTION( "dummy functions module" )
    /* Capability score set to 0 because we don't want to be spawned
     * unless explicitly requested to */
    ADD_CAPABILITY( AOUT, 0 )
    ADD_CAPABILITY( VOUT, 0 )
    ADD_CAPABILITY( INTF, 0 )
    ADD_CAPABILITY( ACCESS, 0 )
    ADD_CAPABILITY( DEMUX, 0 )
    ADD_SHORTCUT( "dummy" )
    ADD_SHORTCUT( "vlc" )
MODULE_INIT_STOP


MODULE_ACTIVATE_START
    _M( access_getfunctions )( &p_module->p_functions->access );
    _M( demux_getfunctions )( &p_module->p_functions->demux );
    _M( aout_getfunctions )( &p_module->p_functions->aout );
    _M( vout_getfunctions )( &p_module->p_functions->vout );
    _M( intf_getfunctions )( &p_module->p_functions->intf );
MODULE_ACTIVATE_STOP


MODULE_DEACTIVATE_START
MODULE_DEACTIVATE_STOP

