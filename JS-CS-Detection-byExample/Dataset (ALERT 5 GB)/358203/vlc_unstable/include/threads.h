/*****************************************************************************
 * threads.h : threads implementation for the VideoLAN client
 * This header provides a portable threads implementation.
 *****************************************************************************
 * Copyright (C) 1999, 2000 VideoLAN
 *
 * Authors: Jean-Marc Dressler <polux@via.ecp.fr>
 *          Samuel Hocevar <sam@via.ecp.fr>
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

#include <stdio.h>

#if defined(HAVE_PTHREAD_H)            /* pthreads (Linux & BSD for example) */
#include <pthread.h>

#elif defined(HAVE_CTHREADS_H)                                    /* GNUMach */
#include <cthreads.h>

#elif defined(HAVE_KERNEL_SCHEDULER_H) && defined(HAVE_KERNEL_OS_H)   /* BeOS */
#undef MAX
#undef MIN
#include <kernel/OS.h>
#include <kernel/scheduler.h>
#include <byteorder.h>
#else
#error no threads available on your system !
#endif

/*****************************************************************************
 * Constants
 *****************************************************************************
 * These constants are used by all threads in *_CreateThread() and
 * *_DestroyThreads() functions. Since those calls are non-blocking, an integer
 * value is used as a shared flag to represent the status of the thread.
 *****************************************************************************/

/* Void status - this value can be used to make sure no operation is currently
 * in progress on the concerned thread in an array of recorded threads */
#define THREAD_NOP          0                            /* nothing happened */

/* Creation status */
#define THREAD_CREATE       10                     /* thread is initializing */
#define THREAD_START        11                          /* thread has forked */
#define THREAD_READY        19                            /* thread is ready */

/* Destructions status */
#define THREAD_DESTROY      20            /* destruction order has been sent */
#define THREAD_END          21        /* destruction order has been received */
#define THREAD_OVER         29             /* thread does not exist any more */

/* Error status */
#define THREAD_ERROR        30                           /* an error occured */
#define THREAD_FATAL        31  /* an fatal error occured - program must end */

/*****************************************************************************
 * Types definition
 *****************************************************************************/

#if defined(HAVE_PTHREAD_H)

typedef pthread_t        vlc_thread_t;
typedef pthread_mutex_t  vlc_mutex_t;
typedef pthread_cond_t   vlc_cond_t;

#elif defined(HAVE_CTHREADS_H)

typedef cthread_t        vlc_thread_t;

/* those structs are the ones defined in /include/cthreads.h but we need
 * to handle (*foo) where foo is a (mutex_t) while they handle (foo) where
 * foo is a (mutex_t*) */
typedef struct s_mutex {
    spin_lock_t held;
    spin_lock_t lock;
    char *name;
    struct cthread_queue queue;
} vlc_mutex_t;

typedef struct s_condition {
    spin_lock_t lock;
    struct cthread_queue queue;
    char *name;
    struct cond_imp *implications;
} vlc_cond_t;

#elif defined(HAVE_KERNEL_SCHEDULER_H) && defined(HAVE_KERNEL_OS_H)

/* This is the BeOS implementation of the vlc thread, note that the mutex is
 * not a real mutex and the cond_var is not like a pthread cond_var but it is
 * enough for what wee need */

typedef thread_id vlc_thread_t;

typedef struct
{
    int32           init;
    sem_id          lock;
} vlc_mutex_t;

typedef struct
{
    int32           init;
    thread_id       thread;
} vlc_cond_t;

#endif

typedef void *(*vlc_thread_func_t)(void *p_data);

/*****************************************************************************
 * Prototypes
 *****************************************************************************/

static __inline__ int  vlc_thread_create( vlc_thread_t *p_thread, char *psz_name,
                                          vlc_thread_func_t func, void *p_data );
static __inline__ void vlc_thread_exit  ( void );
static __inline__ void vlc_thread_join  ( vlc_thread_t thread );

static __inline__ int  vlc_mutex_init    ( vlc_mutex_t *p_mutex );
static __inline__ int  vlc_mutex_lock    ( vlc_mutex_t *p_mutex );
static __inline__ int  vlc_mutex_unlock  ( vlc_mutex_t *p_mutex );
static __inline__ int  vlc_mutex_destroy ( vlc_mutex_t *p_mutex );

static __inline__ int  vlc_cond_init    ( vlc_cond_t *p_condvar );
static __inline__ int  vlc_cond_signal  ( vlc_cond_t *p_condvar );
static __inline__ int  vlc_cond_wait    ( vlc_cond_t *p_condvar, vlc_mutex_t *p_mutex );
static __inline__ int vlc_cond_destroy  ( vlc_cond_t *p_condvar );

#if 0
static _inline__ int    vlc_cond_timedwait   ( vlc_cond_t * condvar, vlc_mutex_t * mutex,
                              mtime_t absoute_timeout_time );
#endif

/*****************************************************************************
 * vlc_thread_create: create a thread
 *****************************************************************************/
static __inline__ int vlc_thread_create( vlc_thread_t *p_thread,
                                         char *psz_name, vlc_thread_func_t func,
                                         void *p_data)
{
#if defined(HAVE_PTHREAD_H)
    return pthread_create( p_thread, NULL, func, p_data );

#elif defined(HAVE_CTHREADS_H)
    *p_thread = cthread_fork( (cthread_fn_t)func, (any_t)p_data );
    return 0;

#elif defined(HAVE_KERNEL_SCHEDULER_H) && defined(HAVE_KERNEL_OS_H)
    *p_thread = spawn_thread( (thread_func)func, psz_name,
                              B_NORMAL_PRIORITY, p_data );
    return resume_thread( *p_thread );

#endif
}

/*****************************************************************************
 * vlc_thread_exit: terminate a thread
 *****************************************************************************/
static __inline__ void vlc_thread_exit( void )
{
#if defined(HAVE_PTHREAD_H)
    pthread_exit( 0 );

#elif defined(HAVE_CTHREADS_H)
    int result;
    cthread_exit( &result );

#elif defined(HAVE_KERNEL_SCHEDULER_H) && defined(HAVE_KERNEL_OS_H)
    exit_thread( 0 );

#endif
}

/*****************************************************************************
 * vlc_thread_join: wait until a thread exits
 *****************************************************************************/
static __inline__ void vlc_thread_join( vlc_thread_t thread )
{
#if defined(HAVE_PTHREAD_H)
    pthread_join( thread, NULL );

#elif defined(HAVE_CTHREADS_H)
    cthread_join( thread );

#elif defined(HAVE_KERNEL_SCHEDULER_H) && defined(HAVE_KERNEL_OS_H)
    int32 exit_value;
    wait_for_thread( thread, &exit_value );

#endif
}

/*****************************************************************************
 * vlc_mutex_init: initialize a mutex
 *****************************************************************************/
static __inline__ int vlc_mutex_init( vlc_mutex_t *p_mutex )
{
#if defined(HAVE_PTHREAD_H)
    return pthread_mutex_init( p_mutex, NULL );

#elif defined(HAVE_CTHREADS_H)
    mutex_init( p_mutex );
    return 0;

#elif defined(HAVE_KERNEL_SCHEDULER_H) && defined(HAVE_KERNEL_OS_H)

    /* check the arguments and whether it's already been initialized */
    if( p_mutex == NULL ) return B_BAD_VALUE;
    if( p_mutex->init == 9999 )
    {
        return EALREADY;
    }

    p_mutex->lock = create_sem( 1, "BeMutex" );
    if( p_mutex->lock < B_NO_ERROR )
        return( -1 );
    p_mutex->init = 9999;
    return B_OK;

#endif
}

/*****************************************************************************
 * vlc_mutex_lock: lock a mutex
 *****************************************************************************/
static __inline__ int vlc_mutex_lock( vlc_mutex_t *p_mutex )
{
#if defined(HAVE_PTHREAD_H)
    return pthread_mutex_lock( p_mutex );

#elif defined(HAVE_CTHREADS_H)
    mutex_lock( p_mutex );
    return 0;

#elif defined(HAVE_KERNEL_SCHEDULER_H) && defined(HAVE_KERNEL_OS_H)
    status_t err;

    if( !p_mutex ) return B_BAD_VALUE;
    if( p_mutex->init < 2000 ) return B_NO_INIT;

    err = acquire_sem( p_mutex->lock );
    return err;

#endif
}

/*****************************************************************************
 * vlc_mutex_unlock: unlock a mutex
 *****************************************************************************/
static __inline__ int vlc_mutex_unlock( vlc_mutex_t *p_mutex )
{
#if defined(HAVE_PTHREAD_H)
    return pthread_mutex_unlock( p_mutex );

#elif defined(HAVE_CTHREADS_H)
    mutex_unlock( p_mutex );
    return 0;

#elif defined(HAVE_KERNEL_SCHEDULER_H) && defined(HAVE_KERNEL_OS_H)

    if(! p_mutex) return B_BAD_VALUE;
    if( p_mutex->init < 2000 ) return B_NO_INIT;

    release_sem( p_mutex->lock );
    return B_OK;

#endif
}

/*****************************************************************************
 * vlc_mutex_destroy: destroy a mutex
 *****************************************************************************/
static __inline__ int vlc_mutex_destroy( vlc_mutex_t *p_mutex )
{
#if defined(HAVE_PTHREAD_H)    
    return pthread_mutex_destroy( p_mutex );
#endif    
}

/*****************************************************************************
 * vlc_cond_init: initialize a condition
 *****************************************************************************/
static __inline__ int vlc_cond_init( vlc_cond_t *p_condvar )
{
#if defined(HAVE_PTHREAD_H)
    return pthread_cond_init( p_condvar, NULL );

#elif defined(HAVE_CTHREADS_H)
    /* condition_init() */
    spin_lock_init( &p_condvar->lock );
    cthread_queue_init( &p_condvar->queue );
    p_condvar->name = 0;
    p_condvar->implications = 0;

    return 0;

#elif defined(HAVE_KERNEL_SCHEDULER_H) && defined(HAVE_KERNEL_OS_H)
    if( !p_condvar )
        return B_BAD_VALUE;

    if( p_condvar->init == 9999 )
        return EALREADY;

    p_condvar->thread = -1;
    p_condvar->init = 9999;
    return 0;

#endif
}

/*****************************************************************************
 * vlc_cond_signal: start a thread on condition completion
 *****************************************************************************/
static __inline__ int vlc_cond_signal( vlc_cond_t *p_condvar )
{
#if defined(HAVE_PTHREAD_H)
    return pthread_cond_signal( p_condvar );

#elif defined(HAVE_CTHREADS_H)
    /* condition_signal() */
    if ( p_condvar->queue.head || p_condvar->implications )
    {
        cond_signal( (condition_t)p_condvar );
    }
    return 0;

#elif defined(HAVE_KERNEL_SCHEDULER_H) && defined(HAVE_KERNEL_OS_H)
    if( !p_condvar )
        return B_BAD_VALUE;

    if( p_condvar->init < 2000 )
        return B_NO_INIT;

    while( p_condvar->thread != -1 )
    {
        thread_info info;
        if( get_thread_info(p_condvar->thread, &info) == B_BAD_VALUE )
            return 0;

        // is the thread sleeping ?
        if( info.state != B_THREAD_SUSPENDED )
        {
            // wait a little
            snooze( 10000 );
        }
        else
        {
            // ok, we have to wake up that thread
            resume_thread( p_condvar->thread );
            return 0;
        }
    }
    
    return 0;

#endif
}

/*****************************************************************************
 * vlc_cond_wait: wait until condition completion
 *****************************************************************************/
static __inline__ int vlc_cond_wait( vlc_cond_t *p_condvar, vlc_mutex_t *p_mutex )
{
#if defined(HAVE_PTHREAD_H)
    return pthread_cond_wait( p_condvar, p_mutex );

#elif defined(HAVE_CTHREADS_H)
    condition_wait( (condition_t)p_condvar, (mutex_t)p_mutex );
    return 0;

#elif defined(HAVE_KERNEL_SCHEDULER_H) && defined(HAVE_KERNEL_OS_H)
    if( !p_condvar )
        return B_BAD_VALUE;

    if( !p_mutex )
        return B_BAD_VALUE;

    if( p_condvar->init < 2000 )
        return B_NO_INIT;

    p_condvar->thread = find_thread( NULL );
    vlc_mutex_unlock( p_mutex );
    suspend_thread( p_condvar->thread );
    p_condvar->thread = -1;

    vlc_mutex_lock( p_mutex );
    return 0;

#endif
}

/*****************************************************************************
 * vlc_cond_destroy: destroy a condition
 *****************************************************************************/
static __inline__ int vlc_cond_destroy( vlc_cond_t *p_condvar )
{
#if defined(HAVE_PTHREAD_H)
    return pthread_cond_destroy( p_condvar );
#endif    
}
