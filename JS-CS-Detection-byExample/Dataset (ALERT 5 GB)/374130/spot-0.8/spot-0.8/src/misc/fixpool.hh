// Copyright (C) 2011 Laboratoire de Recherche et Developpement de
// l'Epita (LRDE)
//
// This file is part of Spot, a model checking library.
//
// Spot is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// Spot is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Spot; see the file COPYING.  If not, write to the Free
// Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
// 02111-1307, USA.

#ifndef SPOT_MISC_FIXPOOL_HH
#  define SPOT_MISC_FIXPOOL_HH

#include <new>
#include <cstddef>
#include <cstdlib>
#include <cassert>

namespace spot
{

  /// A fixed-size memory pool implementation.
  class fixed_size_pool
  {
  public:
    /// Create a pool allocating objects of \a size bytes.
    fixed_size_pool(size_t size)
      : freelist_(0), free_start_(0), free_end_(0), chunklist_(0)
    {
      const size_t alignement = 2 * sizeof(size_t);
      size_ = ((size >= sizeof(block_) ? size : sizeof(block_))
	       + alignement - 1) & ~(alignement - 1);
    }

    /// Free any memory allocated by this pool.
    ~fixed_size_pool()
    {
      while (chunklist_)
	{
	  chunk_* prev = chunklist_->prev;
	  free(chunklist_);
	  chunklist_ = prev;
	}
    }

    /// Allocate \a size bytes of memory.
    void*
    allocate()
    {
      block_* f = freelist_;
      // If we have free blocks available, return the first one.
      if (f)
	{
	  freelist_ = f->next;
	  return f;
	}

      // Else, create a block out of the last chunk of allocated
      // memory.

      // If all the last chunk has been used, allocate one more.
      if (free_start_ + size_ > free_end_)
	{
	  const size_t requested = (size_ > 128 ? size_ : 128) * 8192 - 64;
	  chunk_* c = reinterpret_cast<chunk_*>(malloc(requested));
	  if (!c)
	    throw std::bad_alloc();
	  c->prev = chunklist_;
	  chunklist_ = c;

	  free_start_ = c->data_ + size_;
	  free_end_ = c->data_ + requested;
	}

      void* res = free_start_;
      free_start_ += size_;
      return res;
    }

    /// \brief Recycle \a size bytes of memory.
    ///
    /// Despite the name, the memory is not really deallocated in the
    /// "delete" sense: it is still owned by the pool and will be
    /// reused by allocate as soon as possible.  The memory is only
    /// freed when the pool is destroyed.
    void
    deallocate (const void* ptr)
    {
      assert(ptr);
      block_* b = reinterpret_cast<block_*>(const_cast<void*>(ptr));
      b->next = freelist_;
      freelist_ = b;
    }

  private:
    size_t size_;
    struct block_ { block_* next; }* freelist_;
    char* free_start_;
    char* free_end_;
    // chunk = several agglomerated blocks
    union chunk_ { chunk_* prev; char data_[1]; }* chunklist_;
  };

}

#endif // SPOT_MISC_FIXPOOL_HH
