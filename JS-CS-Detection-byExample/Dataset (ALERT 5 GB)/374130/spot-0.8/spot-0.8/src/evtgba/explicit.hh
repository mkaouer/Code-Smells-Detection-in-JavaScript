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

#ifndef SPOT_EVTGBA_EXPLICIT_HH
# define SPOT_EVTGBA_EXPLICIT_HH

#include "evtgba.hh"
#include <list>
#include "misc/hash.hh"

namespace spot
{
  // FIXME: doc me
  class evtgba_explicit: public evtgba
  {
  public:
    struct transition;
    typedef std::list<transition*> transition_list;
    struct state
    {
      transition_list in, out;
    };

    /// Explicit transitions (used by spot::evtgba_explicit).
    struct transition
    {
      const symbol* label;
      symbol_set acceptance_conditions;
      state* in;
      state* out;
    };

    evtgba_explicit();
    virtual ~evtgba_explicit();

    // evtgba interface
    virtual evtgba_iterator* init_iter() const;
    virtual evtgba_iterator* succ_iter(const spot::state* s) const;
    virtual evtgba_iterator* pred_iter(const spot::state* s) const;
    virtual std::string format_state(const spot::state* state) const;
    virtual const symbol_set& all_acceptance_conditions() const;
    virtual const symbol_set& alphabet() const;

    transition* add_transition(const std::string& source,
			       const rsymbol& label,
			       rsymbol_set acc,
			       const std::string& dest);
    /// \brief Designate \a name as initial state.
    ///
    /// Can be called multiple times in case there is several initial states.
    void set_init_state(const std::string& name);
    void declare_acceptance_condition(const rsymbol& acc);
  protected:

    state* declare_state(const std::string& name);

    typedef Sgi::hash_map<const std::string, evtgba_explicit::state*,
			  string_hash> ns_map;
    typedef Sgi::hash_map<const evtgba_explicit::state*, std::string,
			  ptr_hash<evtgba_explicit::state> > sn_map;

    ns_map name_state_map_;
    sn_map state_name_map_;

    symbol_set acc_set_;
    symbol_set alphabet_;
    transition_list init_states_;
  };

  /// States used by spot::tgba_evtgba_explicit.
  class state_evtgba_explicit : public spot::state
  {
  public:
    state_evtgba_explicit(const evtgba_explicit::state* s)
      : state_(s)
    {
    }

    virtual int compare(const spot::state* other) const;
    virtual size_t hash() const;
    virtual state_evtgba_explicit* clone() const;

    virtual ~state_evtgba_explicit()
    {
    }

    const evtgba_explicit::state* get_state() const;
  private:
    const evtgba_explicit::state* state_;
  };

}

#endif // SPOT_EVTGBA_EXPLICIT_HH
