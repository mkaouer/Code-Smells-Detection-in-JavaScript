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

#ifdef HAVE_CONFIG_H
#  include "config.h"
#endif
#include "bitvect.hh"
#include <new>
#include <iostream>
#include <cmath>
#include <hashfunc.hh>
#include <cstring>
#include <climits>

namespace spot
{
  namespace
  {

    // How many block_t do we need to store BITCOUNT bits?
    size_t block_needed(size_t bitcount)
    {
      const size_t bpb = 8 * sizeof(bitvect::block_t);
      size_t n = (bitcount + bpb - 1) / bpb;
      if (n < 1)
	return 1;
      return n;
    }

    // Fowler-Noll-Vo hash parameters.
    // Add specializations as needed.
    template<int numbytes>
    struct fnv
    {
    };

    // Do not define the following if ULONG_MAX cannot
    // hold a 64-bit value, otherwise the parser will
    // choke when parsing the constants.
#if ULONG_MAX >> 31 >> 31 >> 1 > 0
    // Fowler-Noll-Vo hash parameters for 64bits
    template<>
    struct fnv<8>
    {
      static unsigned long init()
      {
	return 14695981039346656037UL;
      }

      static unsigned long prime()
      {
	return 1099511628211UL;
      }
    };
#endif

    // Fowler-Noll-Vo hash parameters for 32bits
    template<>
    struct fnv<4>
    {
      static unsigned long init()
      {
	return 2166136261UL;
      }

      static unsigned long prime()
      {
	return 16777619UL;
      }
    };

  }

  bitvect::bitvect(size_t size, size_t block_count):
    size_(size),
    block_count_(block_count),
    storage_(&local_storage_)
  {
    clear_all();
  }

  bitvect::bitvect(size_t size, size_t block_count, bool):
    size_(size),
    block_count_(block_count),
    storage_(&local_storage_)
  {
  }

  bitvect* bitvect::clone() const
  {
    size_t n = block_needed(size_);
    // Allocate some memory for the bitvect.  The instance
    // already contains one int of local_storage_, but
    // we allocate n-1 more so that we store the table.
    void* mem = operator new(sizeof(bitvect)
			     + (n - 1) * sizeof(bitvect::block_t));
    bitvect* res = new(mem) bitvect(size_, n, true);
    memcpy(res->storage_, storage_, res->block_count_ * sizeof(block_t));
    return res;
  }

  size_t bitvect::hash() const
  {

    block_t res = fnv<sizeof(block_t)>::init();
    size_t i;
    size_t m = used_blocks();
    if (m == 0)
      return res;
    for (i = 0; i < m - 1; ++i)
      {
	res ^= storage_[i];
	res *= fnv<sizeof(block_t)>::prime();
      }
    // Deal with the last block, that might not be fully used.
    // Compute the number n of bits used in the last block.
    const size_t bpb = 8 * sizeof(bitvect::block_t);
    size_t n = size() % bpb;
    // Use only the least n bits from storage_[i].
    res ^= storage_[i] & ((1UL << n) - 1);
    return res;
  }

  bitvect* make_bitvect(size_t bitcount)
  {
    size_t n = block_needed(bitcount);
    // Allocate some memory for the bitvect.  The instance
    // already contains one int of local_storage_, but
    // we allocate n-1 more so that we store the table.
    void* mem = operator new(sizeof(bitvect)
			     + (n - 1) * sizeof(bitvect::block_t));
    return new(mem) bitvect(bitcount, n);
  }


  bitvect_array* make_bitvect_array(size_t bitcount, size_t vectcount)
  {
     size_t n = block_needed(bitcount);
     // Size of one bitvect.
     size_t bvsize = sizeof(bitvect) + (n - 1) * sizeof(bitvect::block_t);
     // Allocate the bitvect_array with enough space at the end
     // to store all bitvect instances.
     void* mem = operator new(sizeof(bitvect_array) + bvsize * vectcount);
     bitvect_array* bva = new(mem) bitvect_array(vectcount, bvsize);
     // Initialize all the bitvect instances.
     for (size_t i = 0; i < vectcount; ++i)
       new(bva->storage_ + i * bvsize) bitvect(bitcount, n);
     return bva;
  }

  std::ostream& operator<<(std::ostream& os , const bitvect& v)
  {
    for (size_t i = 0, end = v.size(); i != end; ++i)
      os << (v.get(i) ? '1' : '0');
    return os;
  }

  std::ostream& operator<<(std::ostream& os , const bitvect_array& a)
  {
    size_t end = a.size();
    if (end == 0)
      {
	os << "empty\n";
	return os;
      }
    int w = floor(log10(end - 1)) + 1;
    for (size_t i = 0; i != end; ++i)
      {
	os.width(w);
	os << i;
	os.width(1);
	os << ": " << a.at(i) << "\n";
      }
    return os;
  }
}
