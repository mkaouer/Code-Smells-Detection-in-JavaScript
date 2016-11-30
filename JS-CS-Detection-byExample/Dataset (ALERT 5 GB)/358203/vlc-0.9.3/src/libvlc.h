/*****************************************************************************
 * libvlc.h: Internal libvlc generic/misc declaration
 *****************************************************************************
 * Copyright (C) 1999, 2000, 2001, 2002 the VideoLAN team
 * Copyright © 2006-2007 Rémi Denis-Courmont
 * $Id: 4738306db10acfb8856a042cc162d54b234a018c $
 *
 * Authors: Vincent Seguin <seguin@via.ecp.fr>
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

#ifndef LIBVLC_LIBVLC_H
# define LIBVLC_LIBVLC_H 1

extern const char vlc_usage[];

/* Hotkey stuff */
extern const struct hotkey libvlc_hotkeys[];
extern const size_t libvlc_hotkeys_size;
extern int vlc_key_to_action (vlc_object_t *, const char *,
                              vlc_value_t, vlc_value_t, void *);

/*
 * OS-specific initialization
 */
void system_Init      ( libvlc_int_t *, int *, const char *[] );
void system_Configure ( libvlc_int_t *, int *, const char *[] );
void system_End       ( libvlc_int_t * );

/*
 * Threads subsystem
 */
int vlc_threads_init( void );
void vlc_threads_end( void );
vlc_object_t *vlc_threadobj (void);
#ifdef LIBVLC_REFCHECK
void vlc_refcheck (vlc_object_t *obj);
#else
# define vlc_refcheck( obj ) (void)0
#endif

/*
 * CPU capabilities
 */
extern uint32_t cpu_flags;
uint32_t CPUCapabilities( void );

/*
 * Message/logging stuff
 */

typedef struct msg_queue_t
{
    /** Message queue lock */
    vlc_mutex_t             lock;
    bool              b_overflow;

    /* Message queue */
    msg_item_t              msg[VLC_MSG_QSIZE];           /**< message queue */
    int i_start;
    int i_stop;

    /* Subscribers */
    int i_sub;
    msg_subscription_t **pp_sub;

    /* Logfile for WinCE */
#ifdef UNDER_CE
    FILE *logfile;
#endif
} msg_queue_t;

/**
 * Store all data required by messages interfaces.
 */
typedef struct msg_bank_t
{
    vlc_mutex_t             lock;
    msg_queue_t             queue;
} msg_bank_t;

void msg_Create  (libvlc_int_t *);
void msg_Flush   (libvlc_int_t *);
void msg_Destroy (libvlc_int_t *);

/** Internal message stack context */
typedef struct
{
    int i_code;
    char * psz_message;
} msg_context_t;

void msg_StackSet ( int, const char*, ... );
void msg_StackAdd ( const char*, ... );
const char* msg_StackMsg ( void );
/** The global thread var for msg stack context
 *  We store this as a static global variable so we don't need a vlc_object_t
 *  everywhere.
 *  This key is created in vlc_threads_init and is therefore ready to use at
 *  the very beginning of the universe */
extern vlc_threadvar_t msg_context_global_key;
void msg_StackDestroy (void *);

/*
 * Unicode stuff
 */
char *vlc_fix_readdir (const char *);

/*
 * LibVLC objects stuff
 */

/**
 * Creates a VLC object.
 *
 * Note that because the object name pointer must remain valid, potentially
 * even after the destruction of the object (through the message queues), this
 * function CANNOT be exported to plugins as is. In this case, the old
 * vlc_object_create() must be used instead.
 *
 * @param p_this an existing VLC object
 * @param i_size byte size of the object structure
 * @param i_type object type, usually VLC_OBJECT_CUSTOM
 * @param psz_type object type name
 * @return the created object, or NULL.
 */
extern void *
__vlc_custom_create (vlc_object_t *p_this, size_t i_size, int i_type,
                     const char *psz_type);
#define vlc_custom_create(o, s, t, n) \
        __vlc_custom_create(VLC_OBJECT(o), s, t, n)

/**
 * libvlc_global_data_t (global variable)
 *
 * This structure has an unique instance, statically allocated in libvlc and
 * never accessed from the outside. It stores process-wide VLC variables,
 * mostly process-wide locks, and (currently) the module bank and objects tree.
 */
typedef struct libvlc_global_data_t
{
    VLC_COMMON_MEMBERS

    module_bank_t *        p_module_bank; ///< The module bank

    char *                 psz_vlcpath;
} libvlc_global_data_t;


libvlc_global_data_t *vlc_global (void);

/**
 * Private LibVLC data for each object.
 */
struct vlc_object_internals_t
{
    /* Object variables */
    variable_t *    p_vars;
    vlc_mutex_t     var_lock;
    int             i_vars;

    /* Thread properties, if any */
    vlc_thread_t    thread_id;
    bool            b_thread;

    /* Objects thread synchronization */
    vlc_mutex_t     lock;
    vlc_cond_t      wait;
    int             pipes[2];
    vlc_spinlock_t  spin;

    /* Objects management */
    vlc_spinlock_t   ref_spin;
    unsigned         i_refcount;
    vlc_destructor_t pf_destructor;
#ifndef LIBVLC_REFCHECK
    vlc_thread_t     creator_id;
#endif

    /* Objects tree structure */
    vlc_object_t    *prev, *next;
    vlc_object_t   **pp_children;
    int              i_children;
};

#define ZOOM_SECTION N_("Zoom")
#define ZOOM_QUARTER_KEY_TEXT N_("1:4 Quarter")
#define ZOOM_HALF_KEY_TEXT N_("1:2 Half")
#define ZOOM_ORIGINAL_KEY_TEXT N_("1:1 Original")
#define ZOOM_DOUBLE_KEY_TEXT N_("2:1 Double")

#define vlc_internals( obj ) (((vlc_object_internals_t*)(VLC_OBJECT(obj)))-1)

/**
 * Private LibVLC instance data.
 */
typedef struct libvlc_priv_t
{
    libvlc_int_t       public_data;

    /* Configuration */
    vlc_mutex_t        config_lock; ///< config file lock
    char *             psz_configfile;   ///< location of config file

    /* There is no real reason to keep a list of items, but not to break
     * everything, let's keep it */
    input_item_array_t input_items; ///< Array of all created input items
    int                i_last_input_id ; ///< Last id of input item

    /* Messages */
    msg_bank_t         msg_bank;    ///< The message bank
    int                i_verbose;   ///< info messages
    bool               b_color;     ///< color messages?

    /* Timer stats */
    vlc_mutex_t        timer_lock;  ///< Lock to protect timers
    counter_t        **pp_timers;   ///< Array of all timers
    int                i_timers;    ///< Number of timers
    bool               b_stats;     ///< Whether to collect stats

    void              *p_stats_computer;  ///< Input thread computing stats
                                          /// (needs cleanup)

    /* Singleton objects */
    module_t          *p_memcpy_module;  ///< Fast memcpy plugin used
    playlist_t        *p_playlist; //< the playlist singleton
    vlm_t             *p_vlm;  ///< the VLM singleton (or NULL)
    interaction_t     *p_interaction;    ///< interface interaction object
    httpd_t           *p_httpd; ///< HTTP daemon (src/network/httpd.c)

    /* Private playlist data (FIXME - playlist_t is too public...) */
    sout_instance_t   *p_sout; ///< kept sout instance (for playlist)
} libvlc_priv_t;

static inline libvlc_priv_t *libvlc_priv (libvlc_int_t *libvlc)
{
    return (libvlc_priv_t *)libvlc;
}

void playlist_ServicesDiscoveryKillAll( playlist_t *p_playlist );

#define libvlc_stats( o ) (libvlc_priv((VLC_OBJECT(o))->p_libvlc)->b_stats)

/**
 * LibVLC "main module" configuration settings array.
 */
extern module_config_t libvlc_config[];
extern const size_t libvlc_config_count;

/*
 * Variables stuff
 */
void var_OptionParse (vlc_object_t *, const char *, bool trusted);

/*
 * Replacement functions
 */
# ifndef HAVE_DIRENT_H
typedef void DIR;
#  ifndef FILENAME_MAX
#      define FILENAME_MAX (260)
#  endif
struct dirent
{
    long            d_ino;          /* Always zero. */
    unsigned short  d_reclen;       /* Always zero. */
    unsigned short  d_namlen;       /* Length of name in d_name. */
    char            d_name[FILENAME_MAX]; /* File name. */
};
#  define opendir vlc_opendir
#  define readdir vlc_readdir
#  define closedir vlc_closedir
#  define rewinddir vlc_rewindir
void *vlc_opendir (const char *);
void *vlc_readdir (void *);
int   vlc_closedir(void *);
void  vlc_rewinddir(void *);
# endif

#if defined (WIN32)
#   include <dirent.h>
void *vlc_wopendir (const wchar_t *);
/* void *vlc_wclosedir (void *); in vlc's exported symbols */
struct _wdirent *vlc_wreaddir (void *);
void vlc_rewinddir (void *);
#   define _wopendir vlc_wopendir
#   define _wreaddir vlc_wreaddir
#   define _wclosedir vlc_wclosedir
#   define rewinddir vlc_rewinddir
#endif

#endif
