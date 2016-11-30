/*****************************************************************************
 * win32.h : Win32 interface plugin for vlc
 *****************************************************************************
 * Copyright (C) 2002 VideoLAN
 *
 * Authors: Olivier Teuli�re <ipkiss@via.ecp.fr> 
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
 * Exported interface functions.
 *****************************************************************************/
extern "C" __declspec(dllexport)
    int __VLC_SYMBOL( InitModule ) ( module_t *p_module );
extern "C" __declspec(dllexport)
    int __VLC_SYMBOL( ActivateModule ) ( module_t *p_module );
extern "C" __declspec(dllexport)
    int __VLC_SYMBOL( DeactivateModule ) ( module_t *p_module );
