// Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_EMPTINESS_CHECK_HH
# define SPOT_EMPTINESS_CHECK_HH

#include "tgba/tgba.hh"
#include "misc/hash.hh"
#include <stack>
#include <list>
#include <utility>
#include <iostream>

namespace spot
{

  /// \brief Check whether the language of an automate is empty.
  ///
  /// This is based on the following paper.
  /// \verbatim
  /// @InProceedings{couvreur.99.fm,
  ///   author    = {Jean-Michel Couvreur},
  ///   title     = {On-the-fly Verification of Temporal Logic},
  ///   pages     = {253--271},
  ///   editor    = {Jeannette M. Wing and Jim Woodcock and Jim Davies},
  ///   booktitle = {Proceedings of the World Congress on Formal Methods in
  ///                the Development of Computing Systems (FM'99)},
  ///   publisher = {Springer-Verlag},
  ///   series    = {Lecture Notes in Computer Science},
  ///   volume    = {1708},
  ///   year      = {1999},
  ///   address   = {Toulouse, France},
  ///   month     = {September},
  ///   isbn      = {3-540-66587-0}
  /// }
  /// \endverbatim
  class emptiness_check
  {
    typedef std::list<const state*> state_sequence;
    typedef std::pair<const state*, bdd> state_proposition;
    typedef std::list<state_proposition> cycle_path;
  public:
    emptiness_check(const tgba* a);
    ~emptiness_check();

    /// This function returns true if the automata's language is empty,
    /// and builds a stack of SCC.
    bool check();

    /// Compute a counter example if tgba_emptiness_check() returned false.
    void counter_example();

    std::ostream& print_result(std::ostream& os,
			       const tgba* restrict = 0) const;

  private:

    struct connected_component
    {
      // During the Depth path we keep the connected component that we met.
    public:
      connected_component(int index = -1);

      int index;
      /// The bdd condition is the union of all acceptance conditions of
      /// transitions which connect the states of the connected component.
      bdd condition;
    };

    struct connected_component_set: public connected_component
    {
      typedef Sgi::hash_set<const state*,
			    state_ptr_hash, state_ptr_equal> set_type;
      /// for the counter example we need to know all the states of the
      /// component
      set_type states;

      /// Check if the SCC contains states \a s.
      bool has_state(const state* s) const;
    };

    const tgba* aut_;
    std::stack<connected_component> root;
    state_sequence suffix;
    cycle_path period;

    typedef Sgi::hash_map<const state*, int,
			  state_ptr_hash, state_ptr_equal> hash_type;
    hash_type h;		///< Map of visited states.

    /// \brief Return a state which is equal to \a s, but is in \c h,
    /// and free \a s if it is different.  Doing so simplify memory
    /// management, because we don't have to track which state need
    /// to be kept or deallocated: all key in \c h should last for
    /// the whole life of the emptiness_check.
    const state* h_filt(const state* s) const;

    /// \brief Remove a strongly component from the hash.
    ///
    /// This function remove all accessible state from a given
    /// state. In other words, it removes the strongly connected
    /// component that contains this state.
    void remove_component(const state* start_delete);

    /// Called by counter_example to find a path which traverses all
    /// acceptance conditions in the accepted SCC.
    void accepting_path (const connected_component_set& scc,
			 const state* start, bdd acc_to_traverse);

    /// Complete a cycle that caraterise the period of the counter
    /// example.  Append a sequence to the path given by accepting_path.
    void complete_cycle(const connected_component_set& scc,
			const state* from, const state* to);
  };
}
#endif // SPOT_EMPTINESS_CHECK_HH
