/*****************************************************************************
 * variables.h: variables handling
 *****************************************************************************
 * Copyright (C) 2002-2004 the VideoLAN team
 * $Id: aa1c6b466be8740ff2c701ae3658b6ca1776bf54 $
 *
 * Authors: Samuel Hocevar <sam@zoy.org>
 *          Gildas Bazin <gbazin@netcourrier.com>
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

#ifndef VLC_VARIABLES_H
#define VLC_VARIABLES_H 1

/**
 * \file
 * This file defines functions and structures for dynamic variables in vlc
 */

/**
 * \defgroup variables Variables
 *
 * Functions for using the object variables in vlc.
 *
 * Vlc have a very powerful "object variable" infrastructure useful
 * for many things.
 *
 * @{
 */

/*****************************************************************************
 * Variable types - probably very incomplete
 *****************************************************************************/
#define VLC_VAR_TYPE      0x00ff
#define VLC_VAR_FLAGS     0xff00

/** \defgroup var_flags Additive flags
 * These flags are added to the type field of the variable. Most as a result of
 * a __var_Change() call, but some may be added at creation time
 * @{
 */
#define VLC_VAR_HASCHOICE 0x0100
#define VLC_VAR_HASMIN    0x0200
#define VLC_VAR_HASMAX    0x0400
#define VLC_VAR_HASSTEP   0x0800

#define VLC_VAR_ISCOMMAND 0x2000

/** Creation flag */
#define VLC_VAR_DOINHERIT 0x8000
/**@}*/

/**
 * \defgroup var_action Variable actions
 * These are the different actions that can be used with __var_Change().
 * The parameters given are the meaning of the two last parameters of
 * __var_Change() when this action is being used.
 * @{
 */

/**
 * Set the minimum value of this variable
 * \param p_val The new minimum value
 * \param p_val2 Unused
 */
#define VLC_VAR_SETMIN              0x0010
/**
 * Set the maximum value of this variable
 * \param p_val The new maximum value
 * \param p_val2 Unused
 */
#define VLC_VAR_SETMAX              0x0011
#define VLC_VAR_SETSTEP             0x0012

/**
 * Set the value of this variable without triggering any callbacks
 * \param p_val The new value
 * \param p_val2 Unused
 */
#define VLC_VAR_SETVALUE            0x0013

#define VLC_VAR_SETTEXT             0x0014
#define VLC_VAR_GETTEXT             0x0015

#define VLC_VAR_GETMIN              0x0016
#define VLC_VAR_GETMAX              0x0017
#define VLC_VAR_GETSTEP             0x0018

#define VLC_VAR_ADDCHOICE           0x0020
#define VLC_VAR_DELCHOICE           0x0021
#define VLC_VAR_CLEARCHOICES        0x0022
#define VLC_VAR_SETDEFAULT          0x0023
#define VLC_VAR_GETCHOICES          0x0024
#define VLC_VAR_FREECHOICES         0x0025
#define VLC_VAR_GETLIST             0x0026
#define VLC_VAR_FREELIST            0x0027
#define VLC_VAR_CHOICESCOUNT        0x0028

#define VLC_VAR_INHERITVALUE        0x0030
#define VLC_VAR_TRIGGER_CALLBACKS   0x0035

#define VLC_VAR_SETISCOMMAND        0x0040
/**@}*/

/*****************************************************************************
 * Prototypes
 *****************************************************************************/
VLC_EXPORT( int, __var_Create, ( vlc_object_t *, const char *, int ) );
VLC_EXPORT( int, __var_Destroy, ( vlc_object_t *, const char * ) );

VLC_EXPORT( int, __var_Change, ( vlc_object_t *, const char *, int, vlc_value_t *, vlc_value_t * ) );

VLC_EXPORT( int, __var_Type, ( vlc_object_t *, const char * ) );
VLC_EXPORT( int, __var_Set, ( vlc_object_t *, const char *, vlc_value_t ) );
VLC_EXPORT( int, __var_Get, ( vlc_object_t *, const char *, vlc_value_t * ) );

#define var_Command(a,b,c,d,e) __var_Command( VLC_OBJECT( a ), b, c, d, e )
VLC_EXPORT( int, __var_Command, ( vlc_object_t *, const char *, const char *, const char *, char ** ) );

VLC_EXPORT( vlc_mutex_t *, var_AcquireMutex, ( const char * ) );

/**
 * __var_Create() with automatic casting.
 */
#define var_Create(a,b,c) __var_Create( VLC_OBJECT(a), b, c )
/**
 * __var_Destroy() with automatic casting
 */
#define var_Destroy(a,b) __var_Destroy( VLC_OBJECT(a), b )

/**
 * __var_Change() with automatic casting
 */
#define var_Change(a,b,c,d,e) __var_Change( VLC_OBJECT(a), b, c, d, e )

/**
 * __var_Type() with automatic casting
 */
#define var_Type(a,b) __var_Type( VLC_OBJECT(a), b )
/**
 * __var_Set() with automatic casting
 */
#define var_Set(a,b,c) __var_Set( VLC_OBJECT(a), b, c )
/**
 * __var_Get() with automatic casting
 */
#define var_Get(a,b,c) __var_Get( VLC_OBJECT(a), b, c )

/*****************************************************************************
 * Variable callbacks
 *****************************************************************************
 * int MyCallback( vlc_object_t *p_this,
 *                 char const *psz_variable,
 *                 vlc_value_t oldvalue,
 *                 vlc_value_t newvalue,
 *                 void *p_data);
 *****************************************************************************/
VLC_EXPORT( int, __var_AddCallback, ( vlc_object_t *, const char *, vlc_callback_t, void * ) );
VLC_EXPORT( int, __var_DelCallback, ( vlc_object_t *, const char *, vlc_callback_t, void * ) );
VLC_EXPORT( int, __var_TriggerCallback, ( vlc_object_t *, const char * ) );

/**
 * __var_AddCallback() with automatic casting
 */
#define var_AddCallback(a,b,c,d) __var_AddCallback( VLC_OBJECT(a), b, c, d )

/**
 * __var_DelCallback() with automatic casting
 */
#define var_DelCallback(a,b,c,d) __var_DelCallback( VLC_OBJECT(a), b, c, d )

/**
 * __var_TriggerCallback() with automatic casting
 */
#define var_TriggerCallback(a,b) __var_TriggerCallback( VLC_OBJECT(a), b )

/*****************************************************************************
 * helpers functions
 *****************************************************************************/

/**
 * Set the value of an integer variable
 *
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 * \param i The new integer value of this variable
 */
static inline int __var_SetInteger( vlc_object_t *p_obj, const char *psz_name, int i )
{
    vlc_value_t val;
    val.i_int = i;
    return __var_Set( p_obj, psz_name, val );
}
#define var_SetInteger(a,b,c)   __var_SetInteger( VLC_OBJECT(a),b,c)
/**
 * Set the value of an boolean variable
 *
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 * \param b The new boolean value of this variable
 */
static inline int __var_SetBool( vlc_object_t *p_obj, const char *psz_name, bool b )
{
    vlc_value_t val;
    val.b_bool = b;
    return __var_Set( p_obj, psz_name, val );
}

/**
 * Set the value of a time variable
 *
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 * \param i The new time value of this variable
 */
static inline int __var_SetTime( vlc_object_t *p_obj, const char *psz_name, int64_t i )
{
    vlc_value_t val;
    val.i_time = i;
    return __var_Set( p_obj, psz_name, val );
}

/**
 * Set the value of a float variable
 *
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 * \param f The new float value of this variable
 */
static inline int __var_SetFloat( vlc_object_t *p_obj, const char *psz_name, float f )
{
    vlc_value_t val;
    val.f_float = f;
    return __var_Set( p_obj, psz_name, val );
}

/**
 * Set the value of a string variable
 *
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 * \param psz_string The new string value of this variable
 */
static inline int __var_SetString( vlc_object_t *p_obj, const char *psz_name, const char *psz_string )
{
    vlc_value_t val;
    val.psz_string = (char *)psz_string;
    return __var_Set( p_obj, psz_name, val );
}

/**
 * Trigger the callbacks on a void variable
 *
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 */
static inline int __var_SetVoid( vlc_object_t *p_obj, const char *psz_name )
{
    vlc_value_t val;
    val.b_bool = true;
    return __var_Set( p_obj, psz_name, val );
}
#define var_SetVoid(a,b)        __var_SetVoid( VLC_OBJECT(a),b)

/**
 * __var_SetBool() with automatic casting
 */
#define var_SetBool(a,b,c)   __var_SetBool( VLC_OBJECT(a),b,c)

/**
 * __var_SetTime() with automatic casting
 */
#define var_SetTime(a,b,c)      __var_SetTime( VLC_OBJECT(a),b,c)
/**
 * __var_SetFloat() with automatic casting
 */
#define var_SetFloat(a,b,c)     __var_SetFloat( VLC_OBJECT(a),b,c)
/**
 * __var_SetString() with automatic casting
 */
#define var_SetString(a,b,c)     __var_SetString( VLC_OBJECT(a),b,c)

/**
 * Get an integer value
*
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 */
static inline int __var_GetInteger( vlc_object_t *p_obj, const char *psz_name )
{
    vlc_value_t val;val.i_int = 0;
    if( !__var_Get( p_obj, psz_name, &val ) )
        return val.i_int;
    else
        return 0;
}

/**
 * Get a boolean value
 *
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 */
static inline int __var_GetBool( vlc_object_t *p_obj, const char *psz_name )
{
    vlc_value_t val; val.b_bool = false;
    if( !__var_Get( p_obj, psz_name, &val ) )
        return val.b_bool;
    else
        return false;
}

/**
 * Get a time value
 *
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 */
static inline int64_t __var_GetTime( vlc_object_t *p_obj, const char *psz_name )
{
    vlc_value_t val; val.i_time = 0L;
    if( !__var_Get( p_obj, psz_name, &val ) )
        return val.i_time;
    else
        return 0;
}

/**
 * Get a float value
 *
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 */
static inline float __var_GetFloat( vlc_object_t *p_obj, const char *psz_name )
{
    vlc_value_t val; val.f_float = 0.0;
    if( !__var_Get( p_obj, psz_name, &val ) )
        return val.f_float;
    else
        return 0.0;
}

/**
 * Get a string value
 *
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 */
static inline char *__var_GetString( vlc_object_t *p_obj, const char *psz_name )
{
    vlc_value_t val; val.psz_string = NULL;
    if( __var_Get( p_obj, psz_name, &val ) )
        return NULL;
    else
        return val.psz_string;
}

static inline char *__var_GetNonEmptyString( vlc_object_t *obj, const char *name )
{
    vlc_value_t val;
    if( __var_Get( obj, name, &val ) )
        return NULL;
    if( *val.psz_string )
        return val.psz_string;
    free( val.psz_string );
    return NULL;
}


/**
 * __var_GetInteger() with automatic casting
 */
#define var_GetInteger(a,b)   __var_GetInteger( VLC_OBJECT(a),b)
/**
 * __var_GetBool() with automatic casting
 */
#define var_GetBool(a,b)   __var_GetBool( VLC_OBJECT(a),b)
/**
 * __var_GetTime() with automatic casting
 */
#define var_GetTime(a,b)   __var_GetTime( VLC_OBJECT(a),b)
/**
 * __var_GetFloat() with automatic casting
 */
#define var_GetFloat(a,b)   __var_GetFloat( VLC_OBJECT(a),b)
/**
 * __var_GetString() with automatic casting
 */
#define var_GetString(a,b)   __var_GetString( VLC_OBJECT(a),b)
#define var_GetNonEmptyString(a,b)   __var_GetNonEmptyString( VLC_OBJECT(a),b)



/**
 * Increment an integer variable
 * \param p_obj the object that holds the variable
 * \param psz_name the name of the variable
 */
static inline void __var_IncInteger( vlc_object_t *p_obj, const char *psz_name )
{
    int i_val = __var_GetInteger( p_obj, psz_name );
    __var_SetInteger( p_obj, psz_name, ++i_val );
}
#define var_IncInteger(a,b) __var_IncInteger( VLC_OBJECT(a), b )

/**
 * Decrement an integer variable
 * \param p_obj the object that holds the variable
 * \param psz_name the name of the variable
 */
static inline void __var_DecInteger( vlc_object_t *p_obj, const char *psz_name )
{
    int i_val = __var_GetInteger( p_obj, psz_name );
    __var_SetInteger( p_obj, psz_name, --i_val );
}
#define var_DecInteger(a,b) __var_DecInteger( VLC_OBJECT(a), b )

/**
 * Create a integer variable with inherit and get its value.
 *
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 */
static inline int __var_CreateGetInteger( vlc_object_t *p_obj, const char *psz_name )
{
    __var_Create( p_obj, psz_name, VLC_VAR_INTEGER | VLC_VAR_DOINHERIT );
    return __var_GetInteger( p_obj, psz_name );
}

/**
 * Create a boolean variable with inherit and get its value.
 *
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 */
static inline int __var_CreateGetBool( vlc_object_t *p_obj, const char *psz_name )
{
    __var_Create( p_obj, psz_name, VLC_VAR_BOOL | VLC_VAR_DOINHERIT );
    return __var_GetBool( p_obj, psz_name );
}

/**
 * Create a time variable with inherit and get its value.
 *
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 */
static inline int64_t __var_CreateGetTime( vlc_object_t *p_obj, const char *psz_name )
{
    __var_Create( p_obj, psz_name, VLC_VAR_TIME | VLC_VAR_DOINHERIT );
    return __var_GetTime( p_obj, psz_name );
}

/**
 * Create a float variable with inherit and get its value.
 *
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 */
static inline float __var_CreateGetFloat( vlc_object_t *p_obj, const char *psz_name )
{
    __var_Create( p_obj, psz_name, VLC_VAR_FLOAT | VLC_VAR_DOINHERIT );
    return __var_GetFloat( p_obj, psz_name );
}

/**
 * Create a string variable with inherit and get its value.
 *
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 */
static inline char *__var_CreateGetString( vlc_object_t *p_obj,
                                           const char *psz_name )
{
    __var_Create( p_obj, psz_name, VLC_VAR_STRING | VLC_VAR_DOINHERIT );
    return __var_GetString( p_obj, psz_name );
}

static inline char *__var_CreateGetNonEmptyString( vlc_object_t *p_obj,
                                                   const char *psz_name )
{
    __var_Create( p_obj, psz_name, VLC_VAR_STRING | VLC_VAR_DOINHERIT );
    return __var_GetNonEmptyString( p_obj, psz_name );
}

/**
 * __var_CreateGetInteger() with automatic casting
 */
#define var_CreateGetInteger(a,b)   __var_CreateGetInteger( VLC_OBJECT(a),b)
/**
 * __var_CreateGetBool() with automatic casting
 */
#define var_CreateGetBool(a,b)   __var_CreateGetBool( VLC_OBJECT(a),b)
/**
 * __var_CreateGetTime() with automatic casting
 */
#define var_CreateGetTime(a,b)   __var_CreateGetTime( VLC_OBJECT(a),b)
/**
 * __var_CreateGetFloat() with automatic casting
 */
#define var_CreateGetFloat(a,b)   __var_CreateGetFloat( VLC_OBJECT(a),b)
/**
 * __var_CreateGetString() with automatic casting
 */
#define var_CreateGetString(a,b)   __var_CreateGetString( VLC_OBJECT(a),b)
#define var_CreateGetNonEmptyString(a,b)   __var_CreateGetNonEmptyString( VLC_OBJECT(a),b)

/**
 * Create a integer command variable with inherit and get its value.
 *
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 */
static inline int __var_CreateGetIntegerCommand( vlc_object_t *p_obj, const char *psz_name )
{
    __var_Create( p_obj, psz_name, VLC_VAR_INTEGER | VLC_VAR_DOINHERIT
                                   | VLC_VAR_ISCOMMAND );
    return __var_GetInteger( p_obj, psz_name );
}

/**
 * Create a boolean command variable with inherit and get its value.
 *
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 */
static inline int __var_CreateGetBoolCommand( vlc_object_t *p_obj, const char *psz_name )
{
    __var_Create( p_obj, psz_name, VLC_VAR_BOOL | VLC_VAR_DOINHERIT
                                   | VLC_VAR_ISCOMMAND );
    return __var_GetBool( p_obj, psz_name );
}

/**
 * Create a time command variable with inherit and get its value.
 *
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 */
static inline int64_t __var_CreateGetTimeCommand( vlc_object_t *p_obj, const char *psz_name )
{
    __var_Create( p_obj, psz_name, VLC_VAR_TIME | VLC_VAR_DOINHERIT
                                   | VLC_VAR_ISCOMMAND );
    return __var_GetTime( p_obj, psz_name );
}

/**
 * Create a float command variable with inherit and get its value.
 *
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 */
static inline float __var_CreateGetFloatCommand( vlc_object_t *p_obj, const char *psz_name )
{
    __var_Create( p_obj, psz_name, VLC_VAR_FLOAT | VLC_VAR_DOINHERIT
                                   | VLC_VAR_ISCOMMAND );
    return __var_GetFloat( p_obj, psz_name );
}

/**
 * Create a string command variable with inherit and get its value.
 *
 * \param p_obj The object that holds the variable
 * \param psz_name The name of the variable
 */
static inline char *__var_CreateGetStringCommand( vlc_object_t *p_obj,
                                           const char *psz_name )
{
    __var_Create( p_obj, psz_name, VLC_VAR_STRING | VLC_VAR_DOINHERIT
                                   | VLC_VAR_ISCOMMAND );
    return __var_GetString( p_obj, psz_name );
}

static inline char *__var_CreateGetNonEmptyStringCommand( vlc_object_t *p_obj,
                                                   const char *psz_name )
{
    __var_Create( p_obj, psz_name, VLC_VAR_STRING | VLC_VAR_DOINHERIT
                                   | VLC_VAR_ISCOMMAND );
    return __var_GetNonEmptyString( p_obj, psz_name );
}

/**
 * __var_CreateGetInteger() with automatic casting
 */
#define var_CreateGetIntegerCommand(a,b)   __var_CreateGetIntegerCommand( VLC_OBJECT(a),b)
/**
 * __var_CreateGetBoolCommand() with automatic casting
 */
#define var_CreateGetBoolCommand(a,b)   __var_CreateGetBoolCommand( VLC_OBJECT(a),b)
/**
 * __var_CreateGetTimeCommand() with automatic casting
 */
#define var_CreateGetTimeCommand(a,b)   __var_CreateGetTimeCommand( VLC_OBJECT(a),b)
/**
 * __var_CreateGetFloat() with automatic casting
 */
#define var_CreateGetFloatCommand(a,b)   __var_CreateGetFloatCommand( VLC_OBJECT(a),b)
/**
 * __var_CreateGetStringCommand() with automatic casting
 */
#define var_CreateGetStringCommand(a,b)   __var_CreateGetStringCommand( VLC_OBJECT(a),b)
#define var_CreateGetNonEmptyStringCommand(a,b)   __var_CreateGetNonEmptyStringCommand( VLC_OBJECT(a),b)
/**
 * @}
 */
#endif /*  _VLC_VARIABLES_H */
