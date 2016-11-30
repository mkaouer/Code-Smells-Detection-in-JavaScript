// Copyright (C) 2003, 2004, 2006  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_TGBA_TGBAEXPLICIT_HH
# define SPOT_TGBA_TGBAEXPLICIT_HH

#include "misc/hash.hh"
#include <list>
#include "tgba.hh"
#include "ltlast/formula.hh"

namespace spot
{
  // Forward declarations.  See below.
  class state_explicit;
  class tgba_explicit_succ_iterator;

  /// Explicit representation of a spot::tgba.
  /// \ingroup tgba_representation
  class tgba_explicit: public tgba
  {
  public:
    tgba_explicit(bdd_dict* dict);

    struct transition;
    typedef std::list<transition*> state;

    /// Explicit transitions (used by spot::tgba_explicit).
    struct transition
    {
      bdd condition;
      bdd acceptance_conditions;
      const state* dest;
    };

    state* set_init_state(const std::string& state);

    transition*
    create_transition(const std::string& source, const std::string& dest);
    transition*
    create_transition(state* source, const state* dest);

    void add_condition(transition* t, const ltl::formula* f);
    /// This assumes that all variables in \a f are known from dict.
    void add_conditions(transition* t, bdd f);
    void declare_acceptance_condition(const ltl::formula* f);

    /// \brief Copy the acceptance conditions of a tgba.
    ///
    /// If used, this function should be called before creating any
    /// transition.
    void copy_acceptance_conditions_of(const tgba *a);

    bool has_acceptance_condition(const ltl::formula* f) const;
    void add_acceptance_condition(transition* t, const ltl::formula* f);
    /// This assumes that all acceptance conditions in \a f are known from dict.
    void add_acceptance_conditions(transition* t, bdd f);
    void complement_all_acceptance_conditions();
    void merge_transitions();

    /// Return the tgba_explicit::state for \a name, creating the state if
    /// it does not exist.
    state* add_state(const std::string& name);

    // tgba interface
    virtual ~tgba_explicit();
    virtual spot::state* get_init_state() const;
    virtual tgba_succ_iterator*
    succ_iter(const spot::state* local_state,
	      const spot::state* global_state = 0,
	      const tgba* global_automaton = 0) const;
    virtual bdd_dict* get_dict() const;
    virtual std::string format_state(const spot::state* state) const;

    virtual bdd all_acceptance_conditions() const;
    virtual bdd neg_acceptance_conditions() const;

  protected:
    virtual bdd compute_support_conditions(const spot::state* state) const;
    virtual bdd compute_support_variables(const spot::state* state) const;

    bdd get_acceptance_condition(const ltl::formula* f);

    typedef Sgi::hash_map<const std::string, tgba_explicit::state*,
			  string_hash> ns_map;
    typedef Sgi::hash_map<const tgba_explicit::state*, std::string,
			  ptr_hash<tgba_explicit::state> > sn_map;
    ns_map name_state_map_;
    sn_map state_name_map_;
    bdd_dict* dict_;
    tgba_explicit::state* init_;
    mutable bdd all_acceptance_conditions_;
    bdd neg_acceptance_conditions_;
    mutable bool all_acceptance_conditions_computed_;

  private:
    // Disallow copy.
    tgba_explicit(const tgba_explicit& other);
    tgba_explicit& operator=(const tgba_explicit& other);
  };


  /// States used by spot::tgba_explicit.
  /// \ingroup tgba_representation
  class state_explicit : public spot::state
  {
  public:
    state_explicit(const tgba_explicit::state* s)
      : state_(s)
    {
    }

    virtual int compare(const spot::state* other) const;
    virtual size_t hash() const;
    virtual state_explicit* clone() const;

    virtual ~state_explicit()
    {
    }

    const tgba_explicit::state* get_state() const;
  private:
    const tgba_explicit::state* state_;
  };


  /// Successor iterators used by spot::tgba_explicit.
  /// \ingroup tgba_representation
  class tgba_explicit_succ_iterator: public tgba_succ_iterator
  {
  public:
    tgba_explicit_succ_iterator(const tgba_explicit::state* s, bdd all_acc);

    virtual
    ~tgba_explicit_succ_iterator()
    {
    }

    virtual void first();
    virtual void next();
    virtual bool done() const;

    virtual state_explicit* current_state() const;
    virtual bdd current_condition() const;
    virtual bdd current_acceptance_conditions() const;

  private:
    const tgba_explicit::state* s_;
    tgba_explicit::state::const_iterator i_;
    bdd all_acceptance_conditions_;
  };

}

#endif // SPOT_TGBA_TGBAEXPLICIT_HH
