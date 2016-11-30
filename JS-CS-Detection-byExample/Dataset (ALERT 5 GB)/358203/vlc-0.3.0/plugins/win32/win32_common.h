/*****************************************************************************
 * win32_common.h: private win32 interface description
 *****************************************************************************
 * Copyright (C) 2002 VideoLAN
 *
 * Authors: Olivier Teuliere <ipkiss@via.ecp.fr>
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

#include "about.h"
#include "disc.h"
#include "mainframe.h"
#include "menu.h"
#include "messages.h"
#include "network.h"
#include "playlist.h"
#include "preferences.h"

typedef struct es_descriptor_s es_descriptor_t;

/*****************************************************************************
 * The TrackBar is graduated from 0 to SLIDER_MAX_VALUE.
 * SLIDER_MAX_VALUE is set to the higher acceptable value (2^31 - 1), in order
 * to obtain the best precision in date calculation
 *****************************************************************************/
#define SLIDER_MAX_VALUE 2147483647

/*****************************************************************************
 * intf_sys_t: description and status of Win32 interface
 *****************************************************************************/
typedef struct intf_sys_s
{
    /* special actions */
    boolean_t           b_playing;
    boolean_t           b_popup_changed;             /* display popup menu ? */
    boolean_t           b_slider_free;                      /* slider status */

    /* menus handlers */
    boolean_t           b_program_update;   /* do we need to update programs 
                                                                        menu */
    boolean_t           b_title_update;  /* do we need to update title menus */
    boolean_t           b_chapter_update;    /* do we need to update chapter
                                                                       menus */
    boolean_t           b_audio_update;  /* do we need to update audio menus */
    boolean_t           b_spu_update;      /* do we need to update spu menus */

    /* windows and widgets */
    TMainFrameDlg     * p_window;                             /* main window */
    TPlaylistDlg      * p_playlist;                              /* playlist */
    TPopupMenu        * p_popup;                               /* popup menu */
    TAboutDlg         * p_about;                             /* about window */
    TDiscDlg          * p_disc;                     /* disc selection window */
    TNetworkDlg       * p_network;                  /* network stream window */
    TPreferencesDlg   * p_preferences;                 /* preferences window */

    /* The slider */
    off_t               OldValue;                          /* previous value */

    /* The messages window */
    TMessagesDlg      * p_messages;                       /* messages window */
    intf_subscription_t*p_sub;                     /* interface subscription */

    /* Playlist management */
    int                 i_playing;                 /* playlist selected item */

    /* The window labels for DVD mode */
    TLabel            * p_label_title;
    TLabel            * p_label_chapter;
    int                 i_part;                           /* current chapter */

    /* Language information */
    es_descriptor_t   * p_audio_es_old;
    es_descriptor_t   * p_spu_es_old;

} intf_sys_t;

