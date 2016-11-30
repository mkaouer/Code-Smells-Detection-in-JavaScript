// Copyright (C) 2009, 2010 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
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

#ifndef SPOT_SABAALGOS_SABAREACHITER_HH
# define SPOT_SABAALGOS_SABAREACHITER_HH

#include "misc/hash.hh"
#include "saba/saba.hh"
#include <stack>
#include <deque>

namespace spot
{
  /// \brief Iterate over all reachable states of a spot::saba.
  /// \ingroup saba_generic
  class saba_reachable_iterator
  {
  public:
    saba_reachable_iterator(const saba* a);
    virtual ~saba_reachable_iterator();

    /// \brief Iterate over all reachable states of a spot::saba.
    ///
    /// This is a template method that will call add_state(), next_state(),
    /// start(), end(), process_state(), process_state_conjunction() and
    /// process_link(), while it iterates over states.
    void run();

    /// \name Todo list management.
    ///
    /// spot::saba_reachable_iterator_depth_first and
    /// spot::saba_reachable_iterator_breadth_first offer two precanned
    /// implementations for these functions.
    /// \{
    /// \brief Called by run() to register newly discovered states.
    virtual void add_state(const saba_state* s) = 0;
    /// \brief Called by run() to obtain the next state to process.
    virtual const saba_state* next_state() = 0;
    /// \}

    /// Called by add_state or next_states implementations to filter
    /// states.  Default implementation always return true.
    virtual bool want_state(const saba_state* s) const;

    /// Called by run() before starting its iteration.
    virtual void start();
    /// Called by run() once all states have been explored.
    virtual void end();

    /// Called by run() to process a state.
    ///
    /// \param s The current state.
    /// \param n A unique number assigned to \a s.
    virtual void process_state(const saba_state* s, int n);

    /// Called by run() to process a conjunction of states.
    ///
    /// \param in_s The current state.
    /// \param in An unique number assigned to \a in_s.
    /// \param sc The spot::saba_state_conjunction positionned on the current
    ///           conjunction.
    /// \param sc_id An unique number for the this transition assigned to \a sc.
    /// \param si The spot::saba_succ_iterator positionned on the current
    ///             transition.
    virtual void
    process_state_conjunction(const saba_state* in_s, int in,
                              const saba_state_conjunction* sc,
                              int sc_id,
                              const saba_succ_iterator* si);
    /// Called by run() to process a transition.
    ///
    /// \param in_s The source state
    /// \param in The source state number.
    /// \param out_s The destination state
    /// \param out The destination state number.
    /// \param sc The spot::saba_state_conjunction positionned on the current
    ///           conjunction.
    /// \param sc_id An unique number for the this transition assigned to \a sc.
    /// \param si The spot::saba_succ_iterator positionned on the current
    ///             transition.
    ///
    /// The in_s and out_s states are owned by the
    /// spot::saba_reachable_iterator instance and destroyed when the
    /// instance is destroyed.
    virtual void
    process_link(const saba_state* in_s, int in,
                 const saba_state* out_s, int out,
                 const saba_state_conjunction* sc,
                 int sc_id,
                 const saba_succ_iterator* si);

  protected:
    const saba* automata_;	///< The spot::saba to explore.

    typedef Sgi::hash_map<const saba_state*, int,
			  saba_state_ptr_hash, saba_state_ptr_equal> seen_map;
    seen_map seen;		///< States already seen.
  };

  /// \brief An implementation of spot::saba_reachable_iterator that browses
  /// states depth first.
  /// \ingroup saba_generic
  class saba_reachable_iterator_depth_first : public saba_reachable_iterator
  {
  public:
    saba_reachable_iterator_depth_first(const saba* a);

    virtual void add_state(const saba_state* s);
    virtual const saba_state* next_state();

  protected:
    std::stack<const saba_state*> todo; ///< A stack of states yet to explore.
  };

  /// \brief An implementation of spot::saba_reachable_iterator that browses
  /// states breadth first.
  /// \ingroup saba_generic
  class saba_reachable_iterator_breadth_first : public saba_reachable_iterator
  {
  public:
    saba_reachable_iterator_breadth_first(const saba* a);

    virtual void add_state(const saba_state* s);
    virtual const saba_state* next_state();

  protected:
    std::deque<const saba_state*> todo; ///< A queue of states yet to explore.
  };


}


#endif // SPOT_SABAALGOS_SABAREACHITER_HH
