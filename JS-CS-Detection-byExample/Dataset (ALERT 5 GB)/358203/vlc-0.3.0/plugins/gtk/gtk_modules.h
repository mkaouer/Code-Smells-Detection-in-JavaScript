/*****************************************************************************
 * gtk_modules.h: prototypes for modules functions
 *****************************************************************************
 * Copyright (C) 1999, 2000 VideoLAN
 * $Id: gtk_modules.h,v 1.2 2001/05/15 14:49:48 stef Exp $
 *
 * Authors: Samuel Hocevar <sam@zoy.org>
 *          St�phane Borel <stef@via.ecp.fr>
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

gboolean GtkModulesShow    ( GtkWidget *, GdkEventButton *, gpointer );
void     GtkModulesCancel  ( GtkButton * button, gpointer );
void     GtkModulesActivate( GtkMenuItem * menuitem, gpointer );
