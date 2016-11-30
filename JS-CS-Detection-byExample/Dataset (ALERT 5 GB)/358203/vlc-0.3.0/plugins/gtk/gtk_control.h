/*****************************************************************************
 * gtk_control.h: prototypes for control functions
 *****************************************************************************
 * Copyright (C) 1999, 2000 VideoLAN
 * $Id: gtk_control.h,v 1.1 2001/05/15 01:01:44 stef Exp $
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

gboolean GtkControlBack ( GtkWidget *, GdkEventButton *, gpointer );
gboolean GtkControlStop ( GtkWidget *, GdkEventButton *, gpointer );
gboolean GtkControlPlay ( GtkWidget *, GdkEventButton *, gpointer );
gboolean GtkControlPause( GtkWidget *, GdkEventButton *, gpointer );
gboolean GtkControlSlow ( GtkWidget *, GdkEventButton *, gpointer );
gboolean GtkControlFast ( GtkWidget *, GdkEventButton *, gpointer );
void GtkPlayActivate    ( GtkMenuItem *, gpointer );
void GtkPauseActivate   ( GtkMenuItem *, gpointer );
void GtKStopActivate    ( GtkMenuItem *, gpointer );
void GtkBackActivate    ( GtkMenuItem *, gpointer );
void GtkSlowActivate    ( GtkMenuItem *, gpointer );
void GtkFastActivate    ( GtkMenuItem *, gpointer );

