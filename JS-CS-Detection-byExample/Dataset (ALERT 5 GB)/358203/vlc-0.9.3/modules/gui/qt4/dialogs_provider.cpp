/*****************************************************************************
 * dialogs_provider.cpp : Dialog Provider
 *****************************************************************************
 * Copyright (C) 2006-2008 the VideoLAN team
 * $Id: 671511630cea175cbfa2da06dfbc28c5aa6d430e $
 *
 * Authors: Clément Stenac <zorglub@videolan.org>
 *          Jean-Baptiste Kempf <jb@videolan.org>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/
#ifdef HAVE_CONFIG_H
# include "config.h"
#endif

#include <QEvent>
#include <QApplication>
#include <QSignalMapper>
#include <QFileDialog>

#include <vlc_common.h>
#include "qt4.hpp"
#include "dialogs_provider.hpp"
#include "main_interface.hpp"
#include "menus.hpp"
#include <vlc_intf_strings.h>
#include "input_manager.hpp"

/* The dialogs */
#include "dialogs/playlist.hpp"
#include "dialogs/bookmarks.hpp"
#include "dialogs/preferences.hpp"
#include "dialogs/mediainfo.hpp"
#include "dialogs/messages.hpp"
#include "dialogs/extended.hpp"
#include "dialogs/vlm.hpp"
#include "dialogs/sout.hpp"
#include "dialogs/open.hpp"
#include "dialogs/help.hpp"
#include "dialogs/gototime.hpp"
#include "dialogs/podcast_configuration.hpp"

DialogsProvider* DialogsProvider::instance = NULL;

DialogsProvider::DialogsProvider( intf_thread_t *_p_intf ) :
                                  QObject( NULL ), p_intf( _p_intf )
{
    b_isDying = false;
    fixed_timer = new QTimer( this );
    fixed_timer->start( 150 /* milliseconds */ );

    menusMapper = new QSignalMapper();
    CONNECT( menusMapper, mapped(QObject *), this, menuAction( QObject *) );

    menusUpdateMapper = new QSignalMapper();
    CONNECT( menusUpdateMapper, mapped(QObject *),
             this, menuUpdateAction( QObject *) );

    SDMapper = new QSignalMapper();
    CONNECT( SDMapper, mapped (QString), this, SDMenuAction( QString ) );
}

DialogsProvider::~DialogsProvider()
{
    msg_Dbg( p_intf, "Destroying the Dialog Provider" );
    PlaylistDialog::killInstance();
    MediaInfoDialog::killInstance();
    MessagesDialog::killInstance();
    ExtendedDialog::killInstance();
    BookmarksDialog::killInstance();
    HelpDialog::killInstance();
#ifdef UPDATE_CHECK
    UpdateDialog::killInstance();
#endif

    fixed_timer->stop();
    delete menusMapper;
    delete menusUpdateMapper;
    delete SDMapper;
}

void DialogsProvider::quit()
{
    /* Stop the playlist */
    playlist_Stop( THEPL );
    b_isDying = true;
    vlc_object_kill( p_intf->p_libvlc );
    QApplication::closeAllWindows();
    QApplication::quit();
}

void DialogsProvider::customEvent( QEvent *event )
{
    if( event->type() == DialogEvent_Type )
    {
        DialogEvent *de = static_cast<DialogEvent*>(event);
        switch( de->i_dialog )
        {
        case INTF_DIALOG_FILE_SIMPLE:
        case INTF_DIALOG_FILE:
            openDialog(); break;
        case INTF_DIALOG_FILE_GENERIC:
            openFileGenericDialog( de->p_arg ); break;
        case INTF_DIALOG_DISC:
            openDiscDialog(); break;
        case INTF_DIALOG_NET:
            openNetDialog(); break;
        case INTF_DIALOG_SAT:
        case INTF_DIALOG_CAPTURE:
            openCaptureDialog(); break;
        case INTF_DIALOG_DIRECTORY:
            PLAppendDir(); break;
        case INTF_DIALOG_PLAYLIST:
            playlistDialog(); break;
        case INTF_DIALOG_MESSAGES:
            messagesDialog(); break;
        case INTF_DIALOG_FILEINFO:
           mediaInfoDialog(); break;
        case INTF_DIALOG_PREFS:
           prefsDialog(); break;
        case INTF_DIALOG_BOOKMARKS:
           bookmarksDialog(); break;
        case INTF_DIALOG_EXTENDED:
           extendedDialog(); break;
#ifdef ENABLE_VLM
        case INTF_DIALOG_VLM:
           vlmDialog(); break;
#endif
        case INTF_DIALOG_INTERACTION:
           doInteraction( de->p_arg ); break;
        case INTF_DIALOG_POPUPMENU:
           QVLCMenu::PopupMenu( p_intf, (de->i_arg != 0) ); break;
        case INTF_DIALOG_AUDIOPOPUPMENU:
           QVLCMenu::AudioPopupMenu( p_intf ); break;
        case INTF_DIALOG_VIDEOPOPUPMENU:
           QVLCMenu::VideoPopupMenu( p_intf ); break;
        case INTF_DIALOG_MISCPOPUPMENU:
           QVLCMenu::MiscPopupMenu( p_intf ); break;
        case INTF_DIALOG_WIZARD:
        case INTF_DIALOG_STREAMWIZARD:
            openThenStreamingDialogs(); break;
#ifdef UPDATE_CHECK
        case INTF_DIALOG_UPDATEVLC:
            updateDialog(); break;
#endif
        case INTF_DIALOG_EXIT:
            quit(); break;
        default:
           msg_Warn( p_intf, "unimplemented dialog" );
        }
    }
}

/****************************************************************************
 * Individual simple dialogs
 ****************************************************************************/
void DialogsProvider::playlistDialog()
{
    PlaylistDialog::getInstance( p_intf )->toggleVisible();
}

void DialogsProvider::prefsDialog()
{
    PrefsDialog::getInstance( p_intf )->toggleVisible();
}

void DialogsProvider::extendedDialog()
{
    ExtendedDialog::getInstance( p_intf )->toggleVisible();
}

void DialogsProvider::messagesDialog()
{
    MessagesDialog::getInstance( p_intf )->toggleVisible();
}

void DialogsProvider::gotoTimeDialog()
{
    GotoTimeDialog::getInstance( p_intf )->toggleVisible();
}

#ifdef ENABLE_VLM
void DialogsProvider::vlmDialog()
{
    VLMDialog::getInstance( p_intf )->toggleVisible();
}
#endif

void DialogsProvider::helpDialog()
{
    HelpDialog::getInstance( p_intf )->toggleVisible();
}

#ifdef UPDATE_CHECK
void DialogsProvider::updateDialog()
{
    UpdateDialog::getInstance( p_intf )->toggleVisible();
}
#endif

void DialogsProvider::aboutDialog()
{
    AboutDialog::getInstance( p_intf )->toggleVisible();
}

void DialogsProvider::mediaInfoDialog()
{
    MediaInfoDialog::getInstance( p_intf )->toggleVisible();
}

void DialogsProvider::mediaCodecDialog()
{
    MediaInfoDialog::getInstance( p_intf )->showTab( 2 );
}

void DialogsProvider::bookmarksDialog()
{
    BookmarksDialog::getInstance( p_intf )->toggleVisible();
}

void DialogsProvider::podcastConfigureDialog()
{
    PodcastConfigDialog::getInstance( p_intf )->toggleVisible();
}


/****************************************************************************
 * All the open/add stuff
 * Open Dialog first - Simple Open then
 ****************************************************************************/

void DialogsProvider::openDialog( int i_tab )
{
    OpenDialog::getInstance( p_intf->p_sys->p_mi , p_intf )->showTab( i_tab );
}
void DialogsProvider::openDialog()
{
    openDialog( OPEN_FILE_TAB );
}
void DialogsProvider::openFileGenericDialog( intf_dialog_args_t *p_arg )
{
    if( p_arg == NULL )
    {
        msg_Warn( p_intf, "openFileGenericDialog() called with NULL arg" );
        return;
    }

    /* Replace the extensions to a Qt format */
    int i = 0;
    QString extensions = qfu( p_arg->psz_extensions );
    while ( ( i = extensions.indexOf( "|", i ) ) != -1 )
    {
        if( ( extensions.count( "|" ) % 2 ) == 0 )
            extensions.replace( i, 1, ");;" );
        else
            extensions.replace( i, 1, "(" );
    }
    extensions.replace(QString(";*"), QString(" *"));
    extensions.append( ")" );

    /* Save */
    if( p_arg->b_save )
    {
        QString file = QFileDialog::getSaveFileName( NULL, p_arg->psz_title,
                            qfu( p_intf->p_sys->psz_filepath ), extensions );
        if( !file.isEmpty() )
        {
            p_arg->i_results = 1;
            p_arg->psz_results = (char **)malloc( p_arg->i_results * sizeof( char * ) );
            p_arg->psz_results[0] = strdup( qtu( toNativeSepNoSlash( file ) ) );
        }
        else
            p_arg->i_results = 0;
    }
    else /* non-save mode */
    {
        QStringList files = QFileDialog::getOpenFileNames( NULL,
                p_arg->psz_title, qfu( p_intf->p_sys->psz_filepath ),
                extensions );
        p_arg->i_results = files.count();
        p_arg->psz_results = (char **)malloc( p_arg->i_results * sizeof( char * ) );
        i = 0;
        foreach( QString file, files )
            p_arg->psz_results[i++] = strdup( qtu( toNativeSepNoSlash( file ) ) );
    }

    /* Callback */
    if( p_arg->pf_callback )
        p_arg->pf_callback( p_arg );

    /* Clean afterwards */
    if( p_arg->psz_results )
    {
        for( i = 0; i < p_arg->i_results; i++ )
            free( p_arg->psz_results[i] );
        free( p_arg->psz_results );
    }
    free( p_arg->psz_title );
    free( p_arg->psz_extensions );
    free( p_arg );
}

void DialogsProvider::openFileDialog()
{
    openDialog( OPEN_FILE_TAB );
}
void DialogsProvider::openDiscDialog()
{
    openDialog( OPEN_DISC_TAB );
}
void DialogsProvider::openNetDialog()
{
    openDialog( OPEN_NETWORK_TAB );
}
void DialogsProvider::openCaptureDialog()
{
    openDialog( OPEN_CAPTURE_TAB );
}

/* Same as the open one, but force the enqueue */
void DialogsProvider::PLAppendDialog()
{
    OpenDialog::getInstance( p_intf->p_sys->p_mi, p_intf, false, OPEN_AND_ENQUEUE)
                            ->showTab( OPEN_FILE_TAB );
}

void DialogsProvider::MLAppendDialog()
{
    OpenDialog::getInstance( p_intf->p_sys->p_mi, p_intf, false,
                            OPEN_AND_ENQUEUE, false, false )
                                    ->showTab( OPEN_FILE_TAB );
}

/**
 * Simple open
 ***/
QStringList DialogsProvider::showSimpleOpen( QString help,
                                             int filters,
                                             QString path )
{
    QString fileTypes = "";
    if( filters & EXT_FILTER_MEDIA ) {
        ADD_FILTER_MEDIA( fileTypes );
    }
    if( filters & EXT_FILTER_VIDEO ) {
        ADD_FILTER_VIDEO( fileTypes );
    }
    if( filters & EXT_FILTER_AUDIO ) {
        ADD_FILTER_AUDIO( fileTypes );
    }
    if( filters & EXT_FILTER_PLAYLIST ) {
        ADD_FILTER_PLAYLIST( fileTypes );
    }
    if( filters & EXT_FILTER_SUBTITLE ) {
        ADD_FILTER_SUBTITLE( fileTypes );
    }
    ADD_FILTER_ALL( fileTypes );
    fileTypes.replace(QString(";*"), QString(" *"));

    return QFileDialog::getOpenFileNames( NULL,
        help.isEmpty() ? qfu(I_OP_SEL_FILES ) : help,
        path.isEmpty() ? qfu( p_intf->p_sys->psz_filepath ) : path,
        fileTypes );
}

/**
 * Open a file,
 * pl helps you to choose from playlist or media library,
 * go to start or enqueue
 **/
void DialogsProvider::addFromSimple( bool pl, bool go)
{
    QStringList files = DialogsProvider::showSimpleOpen();
    int i = 0;
    foreach( QString file, files )
    {
        const char * psz_utf8 = qtu( toNativeSeparators( file ) );
        playlist_Add( THEPL, psz_utf8, NULL,
                      go ? ( PLAYLIST_APPEND | ( i ? 0 : PLAYLIST_GO ) |
                                               ( i ? PLAYLIST_PREPARSE : 0 ) )
                         : ( PLAYLIST_APPEND | PLAYLIST_PREPARSE ),
                      PLAYLIST_END,
                      pl ? true : false, false );
        i++;
    }
}

void DialogsProvider::simpleOpenDialog()
{
    addFromSimple( true, true ); /* Playlist and Go */
}

void DialogsProvider::simplePLAppendDialog()
{
    addFromSimple( true, false );
}

void DialogsProvider::simpleMLAppendDialog()
{
    addFromSimple( false, false );
}

/* Directory */
/**
 * Open a directory,
 * pl helps you to choose from playlist or media library,
 * go to start or enqueue
 **/
static void openDirectory( intf_thread_t *p_intf, bool pl, bool go )
{
    QString dir = QFileDialog::getExistingDirectory( NULL, qtr("Open Directory") );

    if (!dir.isEmpty() )
    {
        input_item_t *p_input = input_item_NewExt( THEPL,
                              qtu( "directory://" + toNativeSeparators(dir) ),
                              NULL, 0, NULL, -1 );

        /* FIXME: playlist_AddInput() can fail */
        playlist_AddInput( THEPL, p_input,
                       go ? ( PLAYLIST_APPEND | PLAYLIST_GO ) : PLAYLIST_APPEND,
                       PLAYLIST_END, pl, pl_Unlocked );
        if( !go )
            input_Read( THEPL, p_input, true );
        vlc_gc_decref( p_input );
    }
}

void DialogsProvider::PLOpenDir()
{
    openDirectory( p_intf, true, true );
}

void DialogsProvider::PLAppendDir()
{
    openDirectory( p_intf, true, false );
}

void DialogsProvider::MLAppendDir()
{
    openDirectory( p_intf, false , false );
}

/****************
 * Playlist     *
 ****************/
void DialogsProvider::openAPlaylist()
{
    QStringList files = showSimpleOpen( qtr( "Open playlist file" ),
                                        EXT_FILTER_PLAYLIST );
    foreach( QString file, files )
    {
        playlist_Import( THEPL, qtu( toNativeSeparators( file ) ) );
    }
}

void DialogsProvider::saveAPlaylist()
{
    QFileDialog *qfd = new QFileDialog( NULL,
                                   qtr( "Choose a filename to save playlist" ),
                                   qfu( p_intf->p_sys->psz_filepath ),
                                   qtr( "XSPF playlist (*.xspf);; " ) +
                                   qtr( "M3U playlist (*.m3u);; Any (*.*) " ) );
    qfd->setFileMode( QFileDialog::AnyFile );
    qfd->setAcceptMode( QFileDialog::AcceptSave );
    qfd->setConfirmOverwrite( true );

    if( qfd->exec() == QDialog::Accepted )
    {
        if( qfd->selectedFiles().count() > 0 )
        {
            static const char psz_xspf[] = "export-xspf",
                              psz_m3u[] = "export-m3u";
            const char *psz_module;

            QString file = qfd->selectedFiles().first();
            QString filter = qfd->selectedFilter();

            if( file.contains( ".xsp" ) ||
                ( filter.contains( ".xspf" ) && !file.contains( ".m3u" ) ) )
            {
                psz_module = psz_xspf;
                if( !file.contains( ".xsp" ) )
                    file.append( ".xspf" );
            }
            else
            {
                psz_module = psz_m3u;
                if( !file.contains( ".m3u" ) )
                    file.append( ".m3u" );
            }

            playlist_Export( THEPL, qtu( toNativeSeparators( file ) ),
                        THEPL->p_local_category, psz_module);
        }
    }
    delete qfd;
}


/****************************************************************************
 * Sout emulation
 ****************************************************************************/

void DialogsProvider::streamingDialog( QWidget *parent, QString mrl,
                                       bool b_transcode_only )
{
    SoutDialog *s = SoutDialog::getInstance( parent, p_intf, b_transcode_only );

    if( s->exec() == QDialog::Accepted )
    {
        msg_Dbg( p_intf, "Sout mrl %s", qta( s->getMrl() ) );
        /* Just do it */
        int i_len = strlen( qtu( s->getMrl() ) ) + 10;
        char *psz_option = (char*)malloc( i_len );
        snprintf( psz_option, i_len - 1, "%s", qtu( s->getMrl() ) );

        playlist_AddExt( THEPL, qtu( mrl ), "Streaming",
                         PLAYLIST_APPEND | PLAYLIST_GO, PLAYLIST_END,
                        -1, &psz_option, 1, true, pl_Unlocked );
    }
}

void DialogsProvider::openThenStreamingDialogs()
{
    OpenDialog::getInstance( p_intf->p_sys->p_mi, p_intf, false, OPEN_AND_STREAM )
                                ->showTab( OPEN_FILE_TAB );
}

void DialogsProvider::openThenTranscodingDialogs()
{
    OpenDialog::getInstance( p_intf->p_sys->p_mi , p_intf, false, OPEN_AND_SAVE )
                                ->showTab( OPEN_FILE_TAB );
}

/****************************************************************************
 * Menus / Interaction
 ****************************************************************************/

void DialogsProvider::menuAction( QObject *data )
{
    QVLCMenu::DoAction( p_intf, data );
}

void DialogsProvider::menuUpdateAction( QObject *data )
{
    MenuFunc * f = qobject_cast<MenuFunc *>(data);
    f->doFunc( p_intf );
}

void DialogsProvider::SDMenuAction( QString data )
{
    char *psz_sd = strdup( qtu( data ) );
    if( !playlist_IsServicesDiscoveryLoaded( THEPL, psz_sd ) )
        playlist_ServicesDiscoveryAdd( THEPL, psz_sd );
    else
        playlist_ServicesDiscoveryRemove( THEPL, psz_sd );
    free( psz_sd );
}

void DialogsProvider::doInteraction( intf_dialog_args_t *p_arg )
{
    InteractionDialog *qdialog;
    interaction_dialog_t *p_dialog = p_arg->p_dialog;
    switch( p_dialog->i_action )
    {
    case INTERACT_NEW:
        qdialog = new InteractionDialog( p_intf, p_dialog );
        p_dialog->p_private = (void*)qdialog;
        if( !(p_dialog->i_status == ANSWERED_DIALOG) )
            qdialog->show();
        break;
    case INTERACT_UPDATE:
        qdialog = (InteractionDialog*)(p_dialog->p_private);
        if( qdialog )
            qdialog->update();
        else
        {
            /* The INTERACT_NEW message was forgotten
               so we must create the dialog and update it*/
            qdialog = new InteractionDialog( p_intf, p_dialog );
            p_dialog->p_private = (void*)qdialog;
            if( !(p_dialog->i_status == ANSWERED_DIALOG) )
                qdialog->show();
            if( qdialog )
                qdialog->update();
        }
        break;
    case INTERACT_HIDE:
        msg_Dbg( p_intf, "Hide the Interaction Dialog" );
        qdialog = (InteractionDialog*)(p_dialog->p_private);
        if( qdialog )
            qdialog->hide();
        p_dialog->i_status = HIDDEN_DIALOG;
        break;
    case INTERACT_DESTROY:
        msg_Dbg( p_intf, "Destroy the Interaction Dialog" );
        qdialog = (InteractionDialog*)(p_dialog->p_private);
        if( !p_dialog->i_flags & DIALOG_NONBLOCKING_ERROR )
            delete qdialog;
        p_dialog->i_status = DESTROYED_DIALOG;
        break;
    }
}

void DialogsProvider::loadSubtitlesFile()
{
    input_thread_t *p_input = THEMIM->getInput();
    if( !p_input )
        return;
    input_item_t *p_item = input_GetItem( p_input );
    if( !p_item )
        return;
    char *path = input_item_GetURI( p_item );
    if( !path )
        path = strdup( "" );
    char *sep = strrchr( path, DIR_SEP_CHAR );
    if( sep )
        *sep = '\0';
    QStringList qsl = showSimpleOpen( qtr( "Open subtitles file" ),
                                      EXT_FILTER_SUBTITLE,
                                      path );
    free( path );
    QString qsFile;
    foreach( qsFile, qsl )
    {
        if( !input_AddSubtitles( p_input, qtu( toNativeSeparators( qsFile ) ),
                    true ) )
            msg_Warn( p_intf, "unable to load subtitles from '%s'",
                      qtu( qsFile ) );
    }
}
