// Copyright (C) 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_TGBAALGOS_GTEC_EXPLSCC_HH
# define SPOT_TGBAALGOS_GTEC_EXPLSCC_HH

#include "misc/hash.hh"
#include "tgba/state.hh"
#include "sccstack.hh"

namespace spot
{
  /// An SCC storing all its states explicitly.
  class explicit_connected_component: public scc_stack::connected_component
  {
  public:
    virtual ~explicit_connected_component() {}
    /// \brief Check if the SCC contains states \a s.
    ///
    /// Return the representative of \a s in the SCC, and delete \a
    /// s if it is different (acting like
    /// numbered_state_heap::filter), or 0 otherwise.
    virtual const state* has_state(const state* s) const = 0;

    /// Insert a new state in the SCC.
    virtual void insert(const state* s) = 0;
  };

  /// A straightforward implementation of explicit_connected_component
  /// using a hash.
  class connected_component_hash_set: public explicit_connected_component
  {
  public:
    virtual ~connected_component_hash_set() {}
    virtual const state* has_state(const state* s) const;
    virtual void insert(const state* s);
  protected:
    typedef Sgi::hash_set<const state*,
			  state_ptr_hash, state_ptr_equal> set_type;
    set_type states;
  };

  /// Abstract factory for explicit_connected_component.
  class explicit_connected_component_factory
  {
  public:
    virtual ~explicit_connected_component_factory() {}
    /// Create an explicit_connected_component.
    virtual explicit_connected_component* build() const = 0;
  };

  /// \brief Factory for connected_component_hash_set.
  ///
  /// This class is a singleton.  Retrieve the instance using instance().
  class connected_component_hash_set_factory :
    public explicit_connected_component_factory
  {
  public:
    virtual connected_component_hash_set* build() const;

    /// Get the unique instance of this class.
    static const connected_component_hash_set_factory* instance();

  protected:
    virtual ~connected_component_hash_set_factory() {}
    /// Construction is forbiden.
    connected_component_hash_set_factory();
  };
}

#endif // SPOT_TGBAALGOS_GTEC_EXPLSCC_HH
