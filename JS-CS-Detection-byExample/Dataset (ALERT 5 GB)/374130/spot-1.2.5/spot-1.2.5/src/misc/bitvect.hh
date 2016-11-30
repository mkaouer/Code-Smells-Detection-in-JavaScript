// -*- coding: utf-8 -*-
// Copyright (C) 2013, 2014 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
//
// This file is part of Spot, a model checking library.
//
// Spot is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or
// (at your option) any later version.
//
// Spot is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

#ifndef SPOT_MISC_BITVECT_HH
#  define SPOT_MISC_BITVECT_HH

#  include "common.hh"
#  include <cstddef>
#  include <cstdlib>
#  include <cassert>
#  include <iosfwd>
#  include <iostream>
#  include <algorithm>

namespace spot
{
  /// \addtogroup misc_tools
  /// @{

  class bitvect;
  class bitvect_array;

  ///\brief Allocate a bit-vector of \a bitcount bits.
  ///
  /// The resulting object should be released with <code>delete</code>.
  SPOT_API bitvect* make_bitvect(size_t bitcount);

  /// \brief Allocate \a vectcount bit-vectors of \a bitcount bits.
  ///
  /// The resulting bitvect_array should be released with <code>delete</code>.
  SPOT_API bitvect_array* make_bitvect_array(size_t bitcount,
					     size_t vectcount);

  /// \brief A bit vector
  class SPOT_API bitvect
  {
  private:
    // Used by make_bitvect to construct a large bitvect in place.
    bitvect(size_t size, size_t block_count);
    bitvect(size_t size, size_t block_count, bool);

  public:
    typedef unsigned long block_t;

    bitvect():
      size_(0),
      block_count_(1),
      storage_(&local_storage_),
      local_storage_(0)
    {
    }

    bitvect(const bitvect& other):
      size_(other.size_),
      block_count_(1),
      storage_(&local_storage_)
    {
      *this = other;
    }

    bitvect* clone() const;

    void make_empty()
    {
      size_ = 0;
    }

    bitvect& operator=(const bitvect& other)
    {
      reserve_blocks(other.block_count_);
      size_ = other.size();
      for (size_t i = 0; i < block_count_; ++i)
	storage_[i] = other.storage_[i];
      return *this;
    }

    ~bitvect()
    {
      if (storage_ != &local_storage_)
	free(storage_);
    }

    /// Grow the bitvector to \a new_block_count blocks.
    ///
    /// This only changes the capacity of the bitvector, not its size.
    void reserve_blocks(size_t new_block_count)
    {
      if (new_block_count < block_count_)
	return;
      if (storage_ == &local_storage_)
	{
	  block_t* new_storage_ = static_cast<block_t*>
	    (malloc(new_block_count * sizeof(block_t)));
	  for (size_t i = 0; i < block_count_; ++i)
	    new_storage_[i] = storage_[i];
	  storage_ = new_storage_;
	}
      else
	{
	  storage_ = static_cast<block_t*>
	    (realloc(storage_, new_block_count * sizeof(block_t)));
	}
      block_count_ = new_block_count;
    }

  private:
    void grow()
    {
      size_t new_block_count_ = (block_count_ + 1) * 7 / 5;
      reserve_blocks(new_block_count_);
    }

  public:

    size_t used_blocks() const
    {
      const size_t bpb = 8 * sizeof(block_t);
      return (size_ + bpb - 1) / bpb;
    }

    /// Append one bit.
    void push_back(bool val)
    {
      if (size() == capacity())
	grow();
      size_t pos = size_++;
      if (val)
	set(pos);
      else
	clear(pos);
    }

    /// \brief Append the lowest \a count bits of \a data.
    void push_back(block_t data, unsigned count)
    {
      if (size() + count > capacity())
	grow();
      const size_t bpb = 8 * sizeof(block_t);

      // Clear the higher bits.
      if (count != bpb)
	data &= (1UL << count) - 1;

      size_t n = size() % bpb;
      size_t i = size_ / bpb;
      size_ += count;
      if (n == 0)		// Aligned on block_t boundary
	{
	  storage_[i] = data;
	}
      else			// Only (bpb-n) bits free in this block.
	{
	  // Take the lower bpb-n bits of data...
	  block_t mask = (1UL << (bpb - n)) - 1;
	  block_t data1 = (data & mask) << n;
	  mask <<= n;
	  // ... write them on the higher bpb-n bits of last block.
	  storage_[i] = (storage_[i] & ~mask) | data1;
	  // Write the remaining bits in the next block.
	  if (bpb - n < count)
	    storage_[i + 1] = data >> (bpb - n);
	}
    }

    size_t size() const
    {
      return size_;
    }

    size_t capacity() const
    {
      return 8 * block_count_ * sizeof(block_t);
    }

    size_t hash() const;

    bool get(size_t pos) const
    {
      assert(pos < size_);
      const size_t bpb = 8 * sizeof(block_t);
      return storage_[pos / bpb] & (1UL << (pos % bpb));
    }

    void clear_all()
    {
      for (size_t i = 0; i < block_count_; ++i)
	storage_[i] = 0;
    }

    bool is_fully_clear() const
    {
      size_t i;
      for (i = 0; i < block_count_ - 1; ++i)
	if (storage_[i] != 0)
	  return false;
      // The last block might not be fully used, compare only the
      // relevant portion.
      const size_t bpb = 8 * sizeof(bitvect::block_t);
      block_t mask = (1UL << (size() % bpb)) - 1;
      return (storage_[i] & mask) == 0;
    }

    bool is_fully_set() const
    {
      size_t i;
      for (i = 0; i < block_count_ - 1; ++i)
	if (storage_[i] != -1UL)
	  return false;
      // The last block might not be fully used, compare only the
      // relevant portion.
      const size_t bpb = 8 * sizeof(bitvect::block_t);
      block_t mask = (1UL << (size() % bpb)) - 1;
      return ((~storage_[i]) & mask) == 0;
    }

    void set_all()
    {
      for (size_t i = 0; i < block_count_; ++i)
	storage_[i] = -1UL;
    }

    void flip_all()
    {
      for (size_t i = 0; i < block_count_; ++i)
	storage_[i] = ~storage_[i];
    }

    void set(size_t pos)
    {
      assert(pos < size_);
      const size_t bpb = 8 * sizeof(block_t);
      storage_[pos / bpb] |= 1UL << (pos % bpb);
    }

    void clear(size_t pos)
    {
      assert(pos < size_);
      const size_t bpb = 8 * sizeof(block_t);
      storage_[pos / bpb] &= ~(1UL << (pos % bpb));
    }

    void flip(size_t pos)
    {
      assert(pos < size_);
      const size_t bpb = 8 * sizeof(block_t);
      storage_[pos / bpb] ^= (1UL << (pos % bpb));
    }


    bitvect& operator|=(const bitvect& other)
    {
      assert(other.size_ <= size_);
      unsigned m = std::min(other.block_count_, block_count_);
      for (size_t i = 0; i < m; ++i)
	storage_[i] |= other.storage_[i];
      return *this;
    }

    bitvect& operator&=(const bitvect& other)
    {
      assert(other.size_ <= size_);
      unsigned m = std::min(other.block_count_, block_count_);
      for (size_t i = 0; i < m; ++i)
	storage_[i] &= other.storage_[i];
      return *this;
    }

    bitvect& operator^=(const bitvect& other)
    {
      assert(other.size_ <= size_);
      unsigned m = std::min(other.block_count_, block_count_);
      for (size_t i = 0; i < m; ++i)
	storage_[i] ^= other.storage_[i];
      return *this;
    }

    bitvect& operator-=(const bitvect& other)
    {
      assert(other.block_count_ <= block_count_);
      for (size_t i = 0; i < other.block_count_; ++i)
	storage_[i] &= ~other.storage_[i];
      return *this;
    }

    bool operator==(const bitvect& other) const
    {
      if (other.size_ != size_)
	return false;
      if (size_ == 0)
	return true;
      size_t i;
      size_t m = other.used_blocks();
      for (i = 0; i < m - 1; ++i)
	if (storage_[i] != other.storage_[i])
	  return false;
      // The last block might not be fully used, compare only the
      // relevant portion.
      const size_t bpb = 8 * sizeof(bitvect::block_t);
      block_t mask = (1UL << (size() % bpb)) - 1;
      return (storage_[i] & mask) == (other.storage_[i] & mask);
    }

    bool operator!=(const bitvect& other) const
    {
      return !(*this == other);
    }

    bool operator<(const bitvect& other) const
    {
      if (size_ != other.size_)
	return size_ < other.size_;
      if (size_ == 0)
	return false;
      size_t i;
      size_t m = other.used_blocks();
      for (i = 0; i < m - 1; ++i)
	if (storage_[i] > other.storage_[i])
	  return false;
      // The last block might not be fully used, compare only the
      // relevant portion.
      const size_t bpb = 8 * sizeof(bitvect::block_t);
      block_t mask = (1UL << (size() % bpb)) - 1;
      return (storage_[i] & mask) < (other.storage_[i] & mask);
    }

    bool operator>=(const bitvect& other) const
    {
      return !(*this < other);
    }

    bool operator>(const bitvect& other) const
    {
      return other < *this;
    }

    bool operator<=(const bitvect& other) const
    {
      return !(other < *this);
    }

    // \brief Extract a range of bits.
    //
    // Build a new bit-vector using the bits from \a begin (included)
    // to \a end (excluded).
    bitvect* extract_range(size_t begin, size_t end)
    {
      assert(begin <= end);
      assert(end <= size());
      size_t count = end - begin;
      bitvect* res = make_bitvect(count);
      res->make_empty();

      if (end == begin)
	return res;

      const size_t bpb = 8 * sizeof(bitvect::block_t);

      size_t indexb = begin / bpb;
      unsigned bitb = begin % bpb;
      size_t indexe = (end - 1) / bpb;

      if (indexb == indexe)
	{
	  block_t data = storage_[indexb];
	  data >>= bitb;
	  res->push_back(data, count);
	}
      else
	{
	  block_t data = storage_[indexb];
	  data >>= bitb;
	  res->push_back(data, bpb - bitb);
	  count -= bpb - bitb;
	  while (count >= bpb)
	    {
	      ++indexb;
	      res->push_back(storage_[indexb], bpb);
	      count -= bpb;
	      assert(indexb != indexe || count == 0);
	    }
	  if (count > 0)
	    {
	      ++indexb;
	      assert(indexb == indexe);
	      assert(count == end % bpb);
	      res->push_back(storage_[indexb], count);
	    }
	}
      return res;
    }

    friend SPOT_API bitvect*
      ::spot::make_bitvect(size_t bitcount);

    /// Print a bitvect.
    friend SPOT_API std::ostream& operator<<(std::ostream&,
					     const bitvect&);

  private:
    friend SPOT_API bitvect_array*
      ::spot::make_bitvect_array(size_t bitcount,
				 size_t vectcount);

    size_t size_;
    size_t block_count_;
    // storage_ points to local_storage_ as long as size_ <= block_count_ * 8.
    block_t* storage_;
    // Keep this at the end of the structure: when make_bitvect is used,
    // it may allocate more block_t at the end of this structure.
    block_t local_storage_;
  };

  class SPOT_API bitvect_array
  {
  private:
    /// Private constructor used by make_bitvect_array().
    bitvect_array(size_t size, size_t bvsize):
      size_(size),
      bvsize_(bvsize)
    {
    }

    /// Not implemented.
    SPOT_LOCAL bitvect_array(const bitvect_array&);
    /// Not implemented.
    SPOT_LOCAL void operator=(const bitvect_array&);


  public:
    ~bitvect_array()
    {
      for (size_t i = 0; i < size_; ++i)
	at(i).~bitvect();
    }

    /// The number of bitvect in the array.
    size_t size() const
    {
      return size_;
    }

    /// Return the bit-vector at \a index.
    bitvect& at(const size_t index)
    {
      assert(index < size_);
      return *reinterpret_cast<bitvect*>(storage_ + index * bvsize_);
    }

    /// Return the bit-vector at \a index.
    const bitvect& at(const size_t index) const
    {
      assert(index < size_);
      return *reinterpret_cast<const bitvect*>(storage_ + index * bvsize_);
    }

    friend SPOT_API bitvect_array*
      ::spot::make_bitvect_array(size_t bitcount,
				 size_t vectcount);


    /// Print a bitvect_array.
    friend SPOT_API std::ostream& operator<<(std::ostream&,
					     const bitvect_array&);

  private:
    size_t size_;
    size_t bvsize_;
    char storage_[0];
  };

  /// @}
}


#endif // SPOT_MISC_BITVECT_HH
