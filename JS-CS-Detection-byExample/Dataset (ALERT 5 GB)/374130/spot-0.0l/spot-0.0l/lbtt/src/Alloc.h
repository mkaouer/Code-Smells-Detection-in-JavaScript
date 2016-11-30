/*
 *  Copyright (C) 1999, 2000, 2001, 2002, 2003
 *  Heikki Tauriainen <Heikki.Tauriainen@hut.fi>
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

#ifndef ALLOC_H
#define ALLOC_H

#include <config.h>

#ifdef HAVE_SINGLE_CLIENT_ALLOC
#define ALLOC(typename) single_client_alloc
#else
#define ALLOC(typename) allocator<typename>
#endif /* HAVE_SINGLE_CLIENT_ALLOC */

#ifdef HAVE_OBSTACK_H

/* GNU libc 2.3's copy of obstack.h uses a definition of __INT_TO_PTR
   which does not compile in C++.  Fortunately it will not override
   an existing definition.  */
#if __GLIBC__ == 2 && __GLIBC_MINOR__ == 3
# define __INT_TO_PTR(P) ((P) + (char *) 0)
#endif

#include <obstack.h>
#include <cstdlib>
#include <new>

/******************************************************************************
 *
 * A wrapper class for allocating memory through an obstack.
 *
 *****************************************************************************/

class ObstackAllocator
{
public:
  ObstackAllocator();                               /* Constructor. */

  ~ObstackAllocator();                              /* Destructor. */

  void* alloc(int size);                            /* Allocates memory. */

  void free(void* obj);                             /* Deallocates memory. */

  static void failure();                            /* Callback function for
						     * reporting a memory
						     * allocation failure.
						     */
private:
  ObstackAllocator(const ObstackAllocator&);        /* Prevent copying and */
  ObstackAllocator& operator=                       /* assignment of       */
    (const ObstackAllocator&);                      /* ObstackAllocator
						     * objects.
						     */

  struct obstack store;                             /* The obstack. */
};

#define obstack_chunk_alloc std::malloc
#define obstack_chunk_free std::free



/******************************************************************************
 *
 * Inline function definitions for class ObstackAllocator.
 *
 *****************************************************************************/

/* ========================================================================= */
inline ObstackAllocator::ObstackAllocator()
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class ObstackAllocator.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  obstack_init(&store);
}

/* ========================================================================= */
inline ObstackAllocator::~ObstackAllocator()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class ObstackAllocator.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  obstack_free(&store, NULL);
}

/* ========================================================================= */
inline void* ObstackAllocator::alloc(int size)
/* ----------------------------------------------------------------------------
 *
 * Description:   Interface to the memory allocator.
 *
 * Argument:      size  --  Number of bytes to allocate.
 *
 * Returns:       A pointer to the beginning of the newly allocated memory.
 *
 * ------------------------------------------------------------------------- */
{
   return obstack_alloc(&store, size);
}

/* ========================================================================= */
inline void ObstackAllocator::free(void* obj)
/* ----------------------------------------------------------------------------
 *
 * Description:   Interface to the memory deallocation function.
 *
 * Argument:      obj  --  A pointer to the object to deallocate. (Because the
 *                         underlying memory allocator is an obstack, freeing
 *                         an object also releases all objects allocated after
 *                         the given object.)
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  obstack_free(&store, obj);
}

/* ========================================================================= */
inline void ObstackAllocator::failure()
/* ----------------------------------------------------------------------------
 *
 * Description:   Callback function for reporting memory allocation failures.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  throw std::bad_alloc();
}

#endif /* HAVE_OBSTACK_H */

#endif /* !ALLOC_H */
