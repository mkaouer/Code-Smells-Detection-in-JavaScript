// Copyright (C) 2003, 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_TGBAALGOS_REACHITER_HH
# define SPOT_TGBAALGOS_REACHITER_HH

#include "misc/hash.hh"
#include "tgba/tgba.hh"
#include <stack>
#include <deque>

namespace spot
{
  /// \brief Iterate over all reachable states of a spot::tgba.
  /// \ingroup tgba_generic
  class tgba_reachable_iterator
  {
  public:
    tgba_reachable_iterator(const tgba* a);
    virtual ~tgba_reachable_iterator();

    /// \brief Iterate over all reachable states of a spot::tgba.
    ///
    /// This is a template method that will call add_state(), next_state(),
    /// start(), end(), process_state(), and process_link(), while it
    /// iterate over state.
    void run();

    /// \name Todo list management.
    ///
    /// spot::tgba_reachable_iterator_depth_first and
    /// spot::tgba_reachable_iterator_breadth_first offer two precanned
    /// implementations for these functions.
    /// \{
    /// \brief Called by run() to register newly discovered states.
    virtual void add_state(const state* s) = 0;
    /// \brief Called by run() to obtain the
    virtual const state* next_state() = 0;
    /// \}

    /// Called by run() before starting its iteration.
    virtual void start();
    /// Called by run() once all states have been explored.
    virtual void end();

    /// Called by run() to process a state.
    ///
    /// \param s The current state.
    /// \param n A unique number assigned to \a s.
    /// \param si The spot::tgba_succ_iterator for \a s.
    virtual void process_state(const state* s, int n, tgba_succ_iterator* si);
    /// Called by run() to process a transition.
    ///
    /// \param in_s The source state
    /// \param in The source state number.
    /// \param out_s The destination state
    /// \param out The destination state number.
    /// \param si The spot::tgba_succ_iterator positionned on the current
    ///             transition.
    ///
    /// The in_s and out_s states are owned by the
    /// spot::tgba_reachable_iterator instance and destroyed when the
    /// instance is destroyed.
    virtual void process_link(const state* in_s, int in,
			      const state* out_s, int out,
			      const tgba_succ_iterator* si);

  protected:
    const tgba* automata_;	///< The spot::tgba to explore.

    typedef Sgi::hash_map<const state*, int,
			  state_ptr_hash, state_ptr_equal> seen_map;
    seen_map seen;		///< States already seen.
  };

  /// \brief An implementation of spot::tgba_reachable_iterator that browses
  /// states depth first.
  /// \ingroup tgba_generic
  class tgba_reachable_iterator_depth_first : public tgba_reachable_iterator
  {
  public:
    tgba_reachable_iterator_depth_first(const tgba* a);

    virtual void add_state(const state* s);
    virtual const state* next_state();

  protected:
    std::stack<const state*> todo; ///< A stack of states yet to explore.
  };

  /// \brief An implementation of spot::tgba_reachable_iterator that browses
  /// states breadth first.
  /// \ingroup tgba_generic
  class tgba_reachable_iterator_breadth_first : public tgba_reachable_iterator
  {
  public:
    tgba_reachable_iterator_breadth_first(const tgba* a);

    virtual void add_state(const state* s);
    virtual const state* next_state();

  protected:
    std::deque<const state*> todo; ///< A queue of states yet to explore.
  };


}


#endif // SPOT_TGBAALGOS_REACHITER_HH
