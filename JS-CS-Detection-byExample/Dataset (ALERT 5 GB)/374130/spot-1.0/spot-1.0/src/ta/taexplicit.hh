// Copyright (C) 2010, 2011, 2012 Laboratoire de Recherche et
// Developpement de l Epita_explicit (LRDE).
//
// This file is part of Spot, a model checking library.
//
// Spot is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or
// (at your option) any later version.
//
// Spot is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANta_explicitBILITY
// or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more deta_explicitils.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

#ifndef SPOT_TA_TAEXPLICIT_HH
# define SPOT_TA_TAEXPLICIT_HH

#include "misc/hash.hh"
#include <list>
#include "tgba/tgba.hh"
#include <set>
#include "ltlast/formula.hh"
#include <cassert>
#include "misc/bddlt.hh"
#include "ta.hh"

namespace spot
{
  // Forward declarations.  See below.
  class state_ta_explicit;
  class ta_explicit_succ_iterator;
  class ta_explicit;

  /// Explicit representation of a spot::ta.
  /// \ingroup ta_representation
  class ta_explicit : public ta
  {
  public:
    ta_explicit(const tgba* tgba, bdd all_acceptance_conditions,
		state_ta_explicit* artificial_initial_state = 0,
		bool own_tgba = false);

    const tgba*
    get_tgba() const;

    state_ta_explicit*
    add_state(state_ta_explicit* s);

    void
    add_to_initial_states_set(state* s, bdd condition = bddfalse);

    void
    create_transition(state_ta_explicit* source, bdd condition,
        bdd acceptance_conditions, state_ta_explicit* dest,
        bool add_at_beginning = false);

    void
    delete_stuttering_transitions();
    // ta interface
    virtual
    ~ta_explicit();
    virtual const states_set_t
    get_initial_states_set() const;

    virtual ta_succ_iterator*
    succ_iter(const spot::state* s) const;

    virtual ta_succ_iterator*
    succ_iter(const spot::state* s, bdd condition) const;

    virtual bdd_dict*
    get_dict() const;

    virtual std::string
    format_state(const spot::state* s) const;

    virtual bool
    is_accepting_state(const spot::state* s) const;

    virtual bool
    is_livelock_accepting_state(const spot::state* s) const;

    virtual bool
    is_initial_state(const spot::state* s) const;

    virtual bdd
    get_state_condition(const spot::state* s) const;

    virtual void
    free_state(const spot::state* s) const;

    spot::state*
    get_artificial_initial_state() const
    {
      return (spot::state*) artificial_initial_state_;
    }

    void
    set_artificial_initial_state(state_ta_explicit* s)
    {
      artificial_initial_state_ = s;

    }

    virtual void
    delete_stuttering_and_hole_successors(spot::state* s);

    ta::states_set_t
    get_states_set()
    {
      return states_set_;
    }

    /// \brief Return the set of all acceptance conditions used
    /// by this automaton.
    ///
    /// The goal of the emptiness check is to ensure that
    /// a strongly connected component walks through each
    /// of these acceptiong conditions.  I.e., the union
    /// of the acceptiong conditions of all transition in
    /// the SCC should be equal to the result of this function.
    bdd
    all_acceptance_conditions() const
    {
      return all_acceptance_conditions_;
    }

  private:
    // Disallow copy.
    ta_explicit(const ta_explicit& other);
    ta_explicit&
    operator=(const ta_explicit& other);

    const tgba* tgba_;
    bdd all_acceptance_conditions_;
    state_ta_explicit* artificial_initial_state_;
    ta::states_set_t states_set_;
    ta::states_set_t initial_states_set_;
    bool own_tgba_;
  };

  /// states used by spot::ta_explicit.
  /// \ingroup ta_representation
  class state_ta_explicit : public spot::state
  {
#ifndef SWIG
  public:

    /// Explicit transitions.
    struct transition
    {
      bdd condition;
      bdd acceptance_conditions;
      state_ta_explicit* dest;
    };

    typedef std::list<transition*> transitions;

    state_ta_explicit(const state* tgba_state, const bdd tgba_condition,
        bool is_initial_state = false, bool is_accepting_state = false,
        bool is_livelock_accepting_state = false, transitions* trans = 0) :
      tgba_state_(tgba_state), tgba_condition_(tgba_condition),
          is_initial_state_(is_initial_state), is_accepting_state_(
              is_accepting_state), is_livelock_accepting_state_(
              is_livelock_accepting_state), transitions_(trans)
    {
    }

    virtual int
    compare(const spot::state* other) const;
    virtual size_t
    hash() const;
    virtual state_ta_explicit*
    clone() const;

    virtual void
    destroy() const
    {
    }

    virtual
    ~state_ta_explicit()
    {
    }

    transitions*
    get_transitions() const;

    // return transitions filtred by condition
    transitions*
    get_transitions(bdd condition) const;

    void
    add_transition(transition* t, bool add_at_beginning = false);

    const state*
    get_tgba_state() const;
    const bdd
    get_tgba_condition() const;
    bool
    is_accepting_state() const;
    void
    set_accepting_state(bool is_accepting_state);
    bool
    is_livelock_accepting_state() const;
    void
    set_livelock_accepting_state(bool is_livelock_accepting_state);

    bool
    is_initial_state() const;
    void
    set_initial_state(bool is_initial_state);

    /// \brief Return true if the state has no successors
    bool
    is_hole_state() const;

    /// \brief Remove stuttering transitions
    /// and transitions leading to states having no successors
    void
    delete_stuttering_and_hole_successors();

    void
    free_transitions();

    state_ta_explicit* stuttering_reachable_livelock;
  private:
    const state* tgba_state_;
    const bdd tgba_condition_;
    bool is_initial_state_;
    bool is_accepting_state_;
    bool is_livelock_accepting_state_;
    transitions* transitions_;
    Sgi::hash_map<int, transitions*, Sgi::hash<int> > transitions_by_condition;
#endif // !SWIG
  };

  /// Successor iterators used by spot::ta_explicit.
  class ta_explicit_succ_iterator : public ta_succ_iterator
  {
  public:
    ta_explicit_succ_iterator(const state_ta_explicit* s);

    ta_explicit_succ_iterator(const state_ta_explicit* s, bdd condition);

    virtual void
    first();
    virtual void
    next();
    virtual bool
    done() const;

    virtual state*
    current_state() const;
    virtual bdd
    current_condition() const;

    virtual bdd
    current_acceptance_conditions() const;

  private:
    state_ta_explicit::transitions* transitions_;
    state_ta_explicit::transitions::const_iterator i_;
    const state_ta_explicit* source_;
  };

}

#endif // SPOT_TA_TAEXPLICIT_HH
