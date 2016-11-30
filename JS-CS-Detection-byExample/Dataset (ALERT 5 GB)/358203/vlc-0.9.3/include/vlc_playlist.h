/*****************************************************************************
 * vlc_playlist.h : Playlist functions
 *****************************************************************************
 * Copyright (C) 1999-2004 the VideoLAN team
 * $Id: c422e79c5a7eb123d8edf181f84055ce1b7afed1 $
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

#ifndef VLC_PLAYLIST_H_
#define VLC_PLAYLIST_H_

# ifdef __cplusplus
extern "C" {
# endif

#include <vlc_input.h>
#include <vlc_events.h>
#include <vlc_services_discovery.h>
#include <stdio.h>
#include <stdlib.h>

TYPEDEF_ARRAY(playlist_item_t*, playlist_item_array_t);

/**
 * \file
 * This file contain structures and function prototypes related
 * to the playlist in vlc
 *
 * \defgroup vlc_playlist Playlist
 *
 * The VLC playlist system has a tree structure. This allows advanced
 * categorization, like for SAP streams (which are grouped by "sap groups").
 *
 * The base structure for all playlist operations is the input_item_t. This
 * contains all information needed to play a stream and get info, ie, mostly,
 * mrl and metadata. This structure contains a unique i_id field. ids are
 * not recycled when an item is destroyed.
 *
 * Input items are not used directly, but through playlist items.
 * The playlist items are themselves in a tree structure. They only contain
 * a link to the input item, a unique id and a few flags. the playlist
 * item id is NOT the same as the input item id.
 * Several playlist items can be attached to a single input item. The input
 * item is refcounted and is automatically destroyed when it is not used
 * anymore.
 *
 * In the playlist itself, there are two trees, that should always be kept
 * in sync. The "category" tree contains the whole tree structure with
 * several levels, while the onelevel tree contains only one level :), ie
 * it only contains "real" items, not nodes
 * For example, if you open a directory, you will have
 *\verbatim
 * Category tree:               Onelevel tree:
 * Playlist                     Playlist
 *  - Dir                         - item1
 *    - Subdir                    - item2
 *      - item1
 *      - item2
 *\endverbatim
 * The top-level items of both tree are the same, and they are reproduced
 * in the left-part of the playlist GUIs, they are the "sources" from the
 * source selectors. Top-level items include: playlist, media library, SAP,
 * Shoutcast, devices, ...
 *
 * It is envisioned that a third tree will appear: VLM, but it's not done yet
 *
 * The playlist also stores, for utility purposes, an array of all input
 * items, an array of all playlist items and an array of all playlist items
 * and nodes (both are represented by the same structure).
 *
 * So, here is an example:
 * \verbatim
 * Inputs array
 *  - input 1 -> name = foo 1 uri = ...
 *  - input 2 -> name = foo 2 uri = ...
 *
 * Category tree                        Onelevel tree
 * - playlist (id 1)                    - playlist (id 3)
 *    - category 1 (id 2)                - foo 2 (id 8 - input 2)
 *      - foo 2 (id 6 - input 2)       - media library (id 4)
 * - media library (id 2)                - foo 1 (id6 - input 1)
 *    - foo 1 (id 5 - input 1)
 * \endverbatim
 * Sometimes, an item must be transformed to a node. This happens for the
 * directory access for example. In that case, the item is removed from
 * the onelevel tree, as it is not a real item anymore.
 *
 * For "standard" item addition, you can use playlist_Add, playlist_AddExt
 * (more options) or playlist_AddInput if you already created your input
 * item. This will add the item at the root of "Playlist" or of "Media library"
 * in each of the two trees.
 *
 * If you want more control (like, adding the item as the child of a given
 * node in the category tree, use playlist_BothAddInput. You'll have to provide
 * the node in the category tree. The item will be added as a child of
 * this node in the category tree, and as a child of the matching top-level
 * node in the onelevel tree. (Nodes are created with playlist_NodeCreate)
 *
 * Generally speaking, playlist_NodeAddInput should not be used in newer code, it
 * will maybe become useful again when we merge VLM;
 *
 * To delete an item, use playlist_DeleteFromInput( input_id ) which will
 * remove all occurrences of the input in both trees
 *
 * @{
 */

/** Helper structure to export to file part of the playlist */
struct playlist_export_t
{
    char *psz_filename;
    FILE *p_file;
    playlist_item_t *p_root;
};

/** playlist item / node */
struct playlist_item_t
{
    input_item_t           *p_input;    /**< Linked input item */
    /** Number of children, -1 if not a node */
    int                    i_children;
    playlist_item_t      **pp_children; /**< Children nodes/items */
    playlist_item_t       *p_parent;    /**< Item parent */

    int                    i_id;        /**< Playlist item specific id */
    uint8_t                i_flags;     /**< Flags */
    playlist_t            *p_playlist;  /**< Parent playlist */
};

#define PLAYLIST_SAVE_FLAG      0x0001    /**< Must it be saved */
#define PLAYLIST_SKIP_FLAG      0x0002    /**< Must playlist skip after it ? */
#define PLAYLIST_DBL_FLAG       0x0004    /**< Is it disabled ? */
#define PLAYLIST_RO_FLAG        0x0008    /**< Write-enabled ? */
#define PLAYLIST_REMOVE_FLAG    0x0010    /**< Remove this item at the end */
#define PLAYLIST_EXPANDED_FLAG  0x0020    /**< Expanded node */

/** Playlist status */
typedef enum
{ PLAYLIST_STOPPED,PLAYLIST_RUNNING,PLAYLIST_PAUSED } playlist_status_t;

/** Structure containing information about the playlist */
struct playlist_t
{
    VLC_COMMON_MEMBERS

    struct playlist_services_discovery_support_t {
        /* the playlist items for category and onelevel */
        playlist_item_t*    p_cat;
        playlist_item_t*    p_one;
        services_discovery_t * p_sd; /**< Loaded service discovery modules */
    } ** pp_sds;
    int                   i_sds;   /**< Number of service discovery modules */

    playlist_item_array_t items; /**< Arrays of items */
    playlist_item_array_t all_items; /**< Array of items and nodes */
    playlist_item_array_t items_to_delete; /**< Array of items and nodes to
            delete... At the very end. This sucks. */

    playlist_item_array_t current; /**< Items currently being played */
    int                   i_current_index; /**< Index in current array */
    /** Reset current item array */
    bool            b_reset_currently_playing;
    mtime_t               last_rebuild_date;

    int                   i_last_playlist_id; /**< Last id to an item */

    /* Predefined items */
    playlist_item_t *     p_root_category; /**< Root of category tree */
    playlist_item_t *     p_root_onelevel; /**< Root of onelevel tree */
    playlist_item_t *     p_local_category; /** < "Playlist" in CATEGORY view */
    playlist_item_t *     p_ml_category; /** < "Library" in CATEGORY view */
    playlist_item_t *     p_local_onelevel; /** < "Playlist" in ONELEVEL view */
    playlist_item_t *     p_ml_onelevel; /** < "Library" in ONELEVEL view */

    bool                  b_tree; /**< Display as a tree */

    bool            b_doing_ml; /**< Doing media library stuff,
                                       * get quicker */
    bool            b_auto_preparse;

    /* Runtime */
    input_thread_t *      p_input;  /**< the input thread associated
                                     * with the current item */
    int                   i_sort; /**< Last sorting applied to the playlist */
    int                   i_order; /**< Last ordering applied to the playlist */
    mtime_t               gc_date;
    bool            b_cant_sleep;
    playlist_preparse_t  *p_preparse; /**< Preparser object */
    playlist_fetcher_t   *p_fetcher;/**< Meta and art fetcher object */

    struct {
        /* Current status. These fields are readonly, only the playlist
         * main loop can touch it*/
        playlist_status_t   i_status;  /**< Current status of playlist */
        playlist_item_t *   p_item; /**< Currently playing/active item */
        playlist_item_t *   p_node; /**< Current node to play from */
    } status;

    struct {
        /* Request. Use this to give orders to the playlist main loop  */
        playlist_status_t   i_status; /**< requested playlist status */
        playlist_item_t *   p_node;   /**< requested node to play from */
        playlist_item_t *   p_item;   /**< requested item to play in the node */

        int                 i_skip;   /**< Number of items to skip */

        bool          b_request;/**< Set to true by the requester
                                           The playlist sets it back to false
                                           when processing the request */
        vlc_mutex_t         lock;     /**< Lock to protect request */
    } request;
};

/** Helper to add an item */
struct playlist_add_t
{
    int i_node;
    int i_item;
    int i_position;
};

#define SORT_ID 0
#define SORT_TITLE 1
#define SORT_TITLE_NODES_FIRST 2
#define SORT_ARTIST 3
#define SORT_GENRE 4
#define SORT_RANDOM 5
#define SORT_DURATION 6
#define SORT_TITLE_NUMERIC 7
#define SORT_ALBUM 8
#define SORT_TRACK_NUMBER 9
#define SORT_DESCRIPTION 10
#define SORT_RATING 11
#define SORT_URI 12

#define ORDER_NORMAL 0
#define ORDER_REVERSE 1

/* Used by playlist_Import */
#define PLAYLIST_INSERT          0x0001
#define PLAYLIST_APPEND          0x0002
#define PLAYLIST_GO              0x0004
#define PLAYLIST_PREPARSE        0x0008
#define PLAYLIST_SPREPARSE       0x0010
#define PLAYLIST_NO_REBUILD      0x0020

#define PLAYLIST_END           -666

enum pl_locked_state
{
    pl_Locked = true,
    pl_Unlocked = false
};

/*****************************************************************************
 * Prototypes
 *****************************************************************************/

/* Helpers */
#define PL_LOCK vlc_object_lock( p_playlist )
#define PL_UNLOCK vlc_object_unlock( p_playlist )

VLC_EXPORT( playlist_t *, __pl_Yield, ( vlc_object_t * ) );
#define pl_Yield( a ) __pl_Yield( VLC_OBJECT(a) )

VLC_EXPORT( void, __pl_Release, ( vlc_object_t * ) );
#define pl_Release(a) __pl_Release( VLC_OBJECT(a) )

/* Playlist control */
#define playlist_Play(p) playlist_Control(p,PLAYLIST_PLAY, pl_Unlocked )
#define playlist_Pause(p) playlist_Control(p,PLAYLIST_PAUSE, pl_Unlocked )
#define playlist_Stop(p) playlist_Control(p,PLAYLIST_STOP, pl_Unlocked )
#define playlist_Next(p) playlist_Control(p,PLAYLIST_SKIP, pl_Unlocked, 1)
#define playlist_Prev(p) playlist_Control(p,PLAYLIST_SKIP, pl_Unlocked, -1)
#define playlist_Skip(p,i) playlist_Control(p,PLAYLIST_SKIP, pl_Unlocked,  i)

/**
 * Do a playlist action.
 * If there is something in the playlist then you can do playlist actions.
 * Possible queries are listed in vlc_common.h
 * \param p_playlist the playlist to do the command on
 * \param i_query the command to do
 * \param b_locked TRUE if playlist is locked when entering this function
 * \param variable number of arguments
 * \return VLC_SUCCESS or an error
 */
VLC_EXPORT( int, playlist_Control, ( playlist_t *p_playlist, int i_query, bool b_locked, ...  ) );

/** Get current playing input. The object is retained.
 */
VLC_EXPORT( input_thread_t *, playlist_CurrentInput, ( playlist_t *p_playlist ) );

/** Clear the playlist
 * \param b_locked TRUE if playlist is locked when entering this function
 */
VLC_EXPORT( void,  playlist_Clear, ( playlist_t *, bool ) );

/** Enqueue an input item for preparsing */
VLC_EXPORT( int, playlist_PreparseEnqueue, (playlist_t *, input_item_t *) );

/** Enqueue a playlist item and all of its children if any for preparsing */
VLC_EXPORT( int, playlist_PreparseEnqueueItem, (playlist_t *, playlist_item_t *) );
/** Request the art for an input item to be fetched */
VLC_EXPORT( int, playlist_AskForArtEnqueue, (playlist_t *, input_item_t *) );

/********************** Services discovery ***********************/

/** Add a list of comma-separated service discovery modules */
VLC_EXPORT( int, playlist_ServicesDiscoveryAdd, (playlist_t *, const char *));
/** Remove a services discovery module by name */
VLC_EXPORT( int, playlist_ServicesDiscoveryRemove, (playlist_t *, const char *));
/** Check whether a given SD is loaded */
VLC_EXPORT( bool, playlist_IsServicesDiscoveryLoaded, ( playlist_t *,const char *));

/* Playlist sorting */
VLC_EXPORT( int,  playlist_TreeMove, ( playlist_t *, playlist_item_t *, playlist_item_t *, int ) );
VLC_EXPORT( int,  playlist_RecursiveNodeSort, ( playlist_t *, playlist_item_t *,int, int ) );

/**
 * Export a node of the playlist to a certain type of playlistfile
 * \param p_playlist the playlist to export
 * \param psz_filename the location where the exported file will be saved
 * \param p_export_root the root node to export
 * \param psz_type the type of playlist file to create (m3u, pls, ..)
 * \return VLC_SUCCESS on success
 */
VLC_EXPORT( int,  playlist_Export, ( playlist_t *p_playlist, const char *psz_name, playlist_item_t *p_export_root, const char *psz_type ) );

/********************************************************
 * Item management
 ********************************************************/

/*************************** Item creation **************************/

VLC_EXPORT( playlist_item_t* , playlist_ItemNewWithType, ( playlist_t *,const char *,const char *, int , const char *const *, int, int) );

/** Create a new item, without adding it to the playlist
 * \param p_obj a vlc object (anyone will do)
 * \param psz_uri the mrl of the item
 * \param psz_name a text giving a name or description of the item
 * \return the new item or NULL on failure
 */
#define playlist_ItemNew( a , b, c ) \
    playlist_ItemNewWithType( VLC_OBJECT(a) , b , c, 0, NULL, -1, 0 )


/*************************** Item deletion **************************/
VLC_EXPORT( int,  playlist_DeleteFromInput, ( playlist_t *, int, bool ) );

/*************************** Item fields accessors **************************/
VLC_EXPORT( int, playlist_ItemSetName, (playlist_item_t *, const char * ) );

/******************** Item addition ********************/
VLC_EXPORT( int,  playlist_Add,    ( playlist_t *, const char *, const char *, int, int, bool, bool ) );
VLC_EXPORT( int,  playlist_AddExt, ( playlist_t *, const char *, const char *, int, int, mtime_t, const char *const *,int, bool, bool ) );
VLC_EXPORT( int, playlist_AddInput, ( playlist_t *, input_item_t *, int, int, bool, bool ) );
VLC_EXPORT( int, playlist_BothAddInput, ( playlist_t *, input_item_t *,playlist_item_t *,int , int, int*, int*, bool ) );

/********************** Misc item operations **********************/
VLC_EXPORT( playlist_item_t*, playlist_ItemToNode, (playlist_t *,playlist_item_t *, bool) );

/********************************** Item search *************************/
VLC_EXPORT( playlist_item_t *, playlist_ItemGetById, (playlist_t *, int, bool ) );
VLC_EXPORT( playlist_item_t *, playlist_ItemGetByInput, (playlist_t *,input_item_t *, bool ) );
VLC_EXPORT( playlist_item_t *, playlist_ItemGetByInputId, (playlist_t *, int, playlist_item_t *) );

VLC_EXPORT( int, playlist_LiveSearchUpdate, (playlist_t *, playlist_item_t *, const char *) );

/********************************************************
 * Tree management
 ********************************************************/
VLC_EXPORT( int, playlist_NodeChildrenCount, (playlist_t *,playlist_item_t* ) );

/* Node management */
VLC_EXPORT( playlist_item_t *, playlist_NodeCreate, ( playlist_t *, const char *, playlist_item_t * p_parent, int i_flags, input_item_t * ) );
VLC_EXPORT( int, playlist_NodeAppend, (playlist_t *,playlist_item_t*,playlist_item_t *) );
VLC_EXPORT( int, playlist_NodeInsert, (playlist_t *,playlist_item_t*,playlist_item_t *, int) );
VLC_EXPORT( int, playlist_NodeRemoveItem, (playlist_t *,playlist_item_t*,playlist_item_t *) );
VLC_EXPORT( playlist_item_t *, playlist_ChildSearchName, (playlist_item_t*, const char* ) );
VLC_EXPORT( int, playlist_NodeDelete, ( playlist_t *, playlist_item_t *, bool , bool ) );
VLC_EXPORT( int, playlist_NodeEmpty, ( playlist_t *, playlist_item_t *, bool ) );
VLC_EXPORT( void, playlist_NodesPairCreate, (playlist_t *, const char *, playlist_item_t **, playlist_item_t **, bool ) );
VLC_EXPORT( playlist_item_t *, playlist_GetPreferredNode, ( playlist_t *p_playlist, playlist_item_t *p_node ) );
VLC_EXPORT( playlist_item_t *, playlist_GetNextLeaf, ( playlist_t *p_playlist, playlist_item_t *p_root, playlist_item_t *p_item, bool b_ena, bool b_unplayed ) );
VLC_EXPORT( playlist_item_t *, playlist_GetPrevLeaf, ( playlist_t *p_playlist, playlist_item_t *p_root, playlist_item_t *p_item, bool b_ena, bool b_unplayed ) );
VLC_EXPORT( playlist_item_t *, playlist_GetLastLeaf, ( playlist_t *p_playlist, playlist_item_t *p_root ) );

/***********************************************************************
 * Inline functions
 ***********************************************************************/
/** Open a playlist file, add its content to the current playlist */
static inline int playlist_Import( playlist_t *p_playlist, const char *psz_file)
{
    char psz_uri[256+10];
    input_item_t *p_input;
    snprintf( psz_uri, 256+9, "file/://%s", psz_file );
    const char *const psz_option = "meta-file";
    p_input = input_item_NewExt( p_playlist, psz_uri, psz_file,
                                1, &psz_option, -1 );
    playlist_AddInput( p_playlist, p_input, PLAYLIST_APPEND, PLAYLIST_END,
                       true, false );
    input_Read( p_playlist, p_input, true );
    return VLC_SUCCESS;
}

/** Small helper tp get current playing input or NULL. Release the input after use. */
#define pl_CurrentInput(a) __pl_CurrentInput( VLC_OBJECT(a) )
static  inline input_thread_t * __pl_CurrentInput( vlc_object_t * p_this )
{
    playlist_t * p_playlist = pl_Yield( p_this );
    if( !p_playlist ) return NULL;
    input_thread_t * p_input = playlist_CurrentInput( p_playlist );
    pl_Release( p_this );
    return p_input;
}

/** Tell if the playlist is currently running */
#define playlist_IsPlaying( pl ) ( pl->status.i_status == PLAYLIST_RUNNING && \
            !(pl->request.b_request && pl->request.i_status == PLAYLIST_STOPPED) )

#define playlist_IsStopped( pl ) ( pl->status.i_status == PLAYLIST_STOPPED || \
            (pl->request.b_request && pl->request.i_status == PLAYLIST_STOPPED) )

/** Tell if the playlist is empty */
#define playlist_IsEmpty( pl ) ( pl->items.i_size == 0 )

/** Tell the number of items in the current playing context */
#define playlist_CurrentSize( pl ) pl->current.i_size

/** Tell the current item id in current  playing context */
#define playlist_CurrentId( pl ) pl->status.p_item->i_id

/** Ask the playlist to do some work */
#define playlist_Signal( p_playlist ) vlc_object_signal( p_playlist )

/** @} */
# ifdef __cplusplus
}
# endif

#endif
