/*****************************************************************************
 * vlc_objects.h: vlc_object_t definition and manipulation methods
 *****************************************************************************
 * Copyright (C) 2002-2008 the VideoLAN team
 * $Id: 5346a3b9d4a76d250c7bc3c45d304900eca2fda9 $
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

/**
 * \file
 * This file defines the vlc_object_t structure and object types.
 */

/**
 * \defgroup vlc_object Objects
 * @{
 */

/* Object types */
#define VLC_OBJECT_LIBVLC      (-2)
#define VLC_OBJECT_MODULE      (-3)
#define VLC_OBJECT_INTF        (-4)
#define VLC_OBJECT_PLAYLIST    (-5)
#define VLC_OBJECT_INPUT       (-7)
#define VLC_OBJECT_DECODER     (-8)
#define VLC_OBJECT_VOUT        (-9)
#define VLC_OBJECT_AOUT        (-10)
#define VLC_OBJECT_PACKETIZER  (-13)
#define VLC_OBJECT_ENCODER     (-14)
#define VLC_OBJECT_ANNOUNCE    (-17)
#define VLC_OBJECT_OPENGL      (-21)
#define VLC_OBJECT_OSDMENU     (-28)
/* Please add new object types below -34 */
/* Please do not add new object types anyway */
#define VLC_OBJECT_GENERIC     (-666)

/* Object search mode */
#define FIND_PARENT         0x0001
#define FIND_CHILD          0x0002
#define FIND_ANYWHERE       0x0003

#define FIND_STRICT         0x0010

/* Object flags */
#define OBJECT_FLAGS_NODBG       0x0001
#define OBJECT_FLAGS_QUIET       0x0002
#define OBJECT_FLAGS_NOINTERACT  0x0004

/* Types */
typedef void (*vlc_destructor_t)(struct vlc_object_t *);

/*****************************************************************************
 * The vlc_object_t type. Yes, it's that simple :-)
 *****************************************************************************/
/** The main vlc_object_t structure */
struct vlc_object_t
{
    VLC_COMMON_MEMBERS
};

/*****************************************************************************
 * Prototypes
 *****************************************************************************/
VLC_EXPORT( void *, __vlc_object_create, ( vlc_object_t *, int ) );
VLC_EXPORT( void, __vlc_object_set_destructor, ( vlc_object_t *, vlc_destructor_t ) );
VLC_EXPORT( void, __vlc_object_attach, ( vlc_object_t *, vlc_object_t * ) );
VLC_EXPORT( void, __vlc_object_detach, ( vlc_object_t * ) );
VLC_EXPORT( void *, vlc_object_get, ( int ) );
VLC_EXPORT( void *, __vlc_object_find, ( vlc_object_t *, int, int ) );
VLC_EXPORT( void *, __vlc_object_find_name, ( vlc_object_t *, const char *, int ) );
VLC_EXPORT( void, __vlc_object_yield, ( vlc_object_t * ) );
VLC_EXPORT( void, __vlc_object_release, ( vlc_object_t * ) );
VLC_EXPORT( vlc_list_t *, __vlc_list_find, ( vlc_object_t *, int, int ) );
VLC_EXPORT( vlc_list_t *, __vlc_list_children, ( vlc_object_t * ) );
VLC_EXPORT( void, vlc_list_release, ( vlc_list_t * ) );

/* __vlc_object_dump */
VLC_EXPORT( void, __vlc_object_dump, ( vlc_object_t *p_this ) );

/*}@*/

#define vlc_object_create(a,b) \
    __vlc_object_create( VLC_OBJECT(a), b )

#define vlc_object_set_destructor(a,b) \
    __vlc_object_set_destructor( VLC_OBJECT(a), b )

#define vlc_object_detach(a) \
    __vlc_object_detach( VLC_OBJECT(a) )

#define vlc_object_attach(a,b) \
    __vlc_object_attach( VLC_OBJECT(a), VLC_OBJECT(b) )

#define vlc_object_find(a,b,c) \
    __vlc_object_find( VLC_OBJECT(a),b,c)

#define vlc_object_find_name(a,b,c) \
    __vlc_object_find_name( VLC_OBJECT(a),b,c)

#define vlc_object_yield(a) \
    __vlc_object_yield( VLC_OBJECT(a) )

#define vlc_object_release(a) \
    __vlc_object_release( VLC_OBJECT(a) )

#define vlc_list_find(a,b,c) \
    __vlc_list_find( VLC_OBJECT(a),b,c)

#define vlc_list_children(a) \
    __vlc_list_children( VLC_OBJECT(a) )

#define vlc_object_dump(a) \
    __vlc_object_dump( VLC_OBJECT(a))


/* Objects and threading */
VLC_EXPORT( void, __vlc_object_lock, ( vlc_object_t * ) );
#define vlc_object_lock( obj ) \
    __vlc_object_lock( VLC_OBJECT( obj ) )

VLC_EXPORT( void, __vlc_object_unlock, ( vlc_object_t * ) );
#define vlc_object_unlock( obj ) \
    __vlc_object_unlock( VLC_OBJECT( obj ) )

VLC_EXPORT( void, __vlc_object_wait, ( vlc_object_t * ) );
#define vlc_object_wait( obj ) \
    __vlc_object_wait( VLC_OBJECT( obj ) )

VLC_EXPORT( int, __vlc_object_timedwait, ( vlc_object_t *, mtime_t ) );
#define vlc_object_timedwait( obj, d ) \
    __vlc_object_timedwait( VLC_OBJECT( obj ), d )

VLC_EXPORT( void, __vlc_object_signal_unlocked, ( vlc_object_t * ) );
#define vlc_object_signal_unlocked( obj ) \
    __vlc_object_signal_unlocked( VLC_OBJECT( obj ) )

static inline void __vlc_object_signal( vlc_object_t *obj )
{
    vlc_object_lock( obj );
    vlc_object_signal_unlocked( obj );
    vlc_object_unlock( obj );
}
#define vlc_object_signal( obj ) \
    __vlc_object_signal( VLC_OBJECT( obj ) )

VLC_EXPORT( void, __vlc_object_kill, ( vlc_object_t * ) );
#define vlc_object_kill(a) \
    __vlc_object_kill( VLC_OBJECT(a) )

static inline bool __vlc_object_alive (const vlc_object_t *obj)
{
    barrier ();
    return !obj->b_die;
}

#define vlc_object_alive(a) \
    __vlc_object_alive( VLC_OBJECT(a) )

VLC_EXPORT( int, __vlc_object_waitpipe, ( vlc_object_t *obj ));
#define vlc_object_waitpipe(a) \
    __vlc_object_waitpipe( VLC_OBJECT(a) )
