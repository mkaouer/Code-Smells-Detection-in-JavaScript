// Copyright (C) 2004, 2006  Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
// et Marie Curie.
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

#ifndef SPOT_TGBAALGOS_GTEC_NSHEAP_HH
# define SPOT_TGBAALGOS_GTEC_NSHEAP_HH

#include "tgba/state.hh"
#include "misc/hash.hh"

namespace spot
{
  /// Iterator on numbered_state_heap objects.
  class numbered_state_heap_const_iterator
  {
  public:
    virtual ~numbered_state_heap_const_iterator() {}

    //@{
    /// Iteration
    virtual void first() = 0;
    virtual void next() = 0;
    virtual bool done() const = 0;
    //@}

    //@{
    /// Inspection
    virtual const state* get_state() const = 0;
    virtual int get_index() const = 0;
    //@}
  };

  /// Keep track of a large quantity of indexed states.
  class numbered_state_heap
  {
  public:
    typedef std::pair<const state*, int*> state_index_p;
    typedef std::pair<const state*, int> state_index;

    virtual ~numbered_state_heap() {}
    //@{
    /// \brief Is state in the heap?
    ///
    /// Returns a pair (0,0) if \a s is not in the heap.
    /// or a pair (p, i) if there is a clone \a p of \a s \a i
    /// in the heap with index.  If \a s is in the heap and is different
    /// from \a p it will be freed.
    ///
    /// These functions are called by the algorithm to check whether a
    /// successor is a new state to explore or an already visited
    /// state.
    ///
    /// These functions can be redefined to search for more
    /// than an equal match.  For example we could redefine
    /// it to check state inclusion.
    virtual state_index find(const state* s) const = 0;
    virtual state_index_p find(const state* s) = 0;
    //@}

    //@{
    /// \brief Return the index of an existing state.
    ///
    /// This is mostly similar to find(), except it will
    /// be called for state which we know are already in
    /// the heap, or for state which may not be in the
    /// heap but for which it is always OK to do equality
    /// checks.
    virtual state_index index(const state* s) const = 0;
    virtual state_index_p index(const state* s) = 0;
    //@}

    /// Add a new state \a s with index \a index
    virtual void insert(const state* s, int index) = 0;

    /// The number of stored states.
    virtual int size() const = 0;

    /// Return an iterator on the states/indexes pairs.
    virtual numbered_state_heap_const_iterator* iterator() const = 0;
  };

  /// Abstract factory for numbered_state_heap
  class numbered_state_heap_factory
  {
  public:
    virtual ~numbered_state_heap_factory() {}
    virtual numbered_state_heap* build() const = 0;
  };

  /// A straightforward implementation of numbered_state_heap with a hash map.
  class numbered_state_heap_hash_map : public numbered_state_heap
  {
  public:
    virtual ~numbered_state_heap_hash_map();

    virtual state_index find(const state* s) const;
    virtual state_index_p find(const state* s);
    virtual state_index index(const state* s) const;
    virtual state_index_p index(const state* s);

    virtual void insert(const state* s, int index);
    virtual int size() const;

    virtual numbered_state_heap_const_iterator* iterator() const;

    typedef Sgi::hash_map<const state*, int,
			  state_ptr_hash, state_ptr_equal> hash_type;
  protected:
    hash_type h;		///< Map of visited states.
  };

  /// \brief Factory for numbered_state_heap_hash_map.
  ///
  /// This class is a singleton.  Retrieve the instance using instance().
  class numbered_state_heap_hash_map_factory:
    public numbered_state_heap_factory
  {
  public:
    virtual numbered_state_heap_hash_map* build() const;

    /// Get the unique instance of this class.
    static const numbered_state_heap_hash_map_factory* instance();
  protected:
    virtual ~numbered_state_heap_hash_map_factory() {}
    numbered_state_heap_hash_map_factory();
  };

}

#endif // SPOT_TGBAALGOS_GTEC_NSHEAP_HH
