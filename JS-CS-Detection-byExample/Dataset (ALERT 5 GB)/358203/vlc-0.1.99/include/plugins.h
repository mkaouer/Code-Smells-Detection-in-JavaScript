/*****************************************************************************
 * plugins.h : Dynamic plugin management functions
 *****************************************************************************
 * Copyright (C) 1999, 2000 VideoLAN
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

#ifdef SYS_BEOS
typedef int plugin_id_t;
#else
typedef void* plugin_id_t;
#endif

int    RequestPlugin     ( plugin_id_t * p_plugin, char * psz_mask, char * psz_name );
void   TrashPlugin       ( plugin_id_t p_plugin );
void * GetPluginFunction ( plugin_id_t plugin, char *name );

