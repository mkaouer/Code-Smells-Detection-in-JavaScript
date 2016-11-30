// Copyright (C) 2011 Laboratoire de Recherche et Développement de
// l'Epita (LRDE).
// Copyright (C) 2003 Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include "tgbabddconcrete.hh"
#include "bddprint.hh"
#include <cassert>

namespace spot
{
  tgba_bdd_concrete::tgba_bdd_concrete(const tgba_bdd_factory& fact)
    : data_(fact.get_core_data())
  {
    get_dict()->register_all_variables_of(&fact, this);
  }

  tgba_bdd_concrete::tgba_bdd_concrete(const tgba_bdd_factory& fact, bdd init)
    : data_(fact.get_core_data())
  {
    get_dict()->register_all_variables_of(&fact, this);
    set_init_state(init);
  }

  tgba_bdd_concrete::~tgba_bdd_concrete()
  {
    get_dict()->unregister_all_my_variables(this);
  }

  void
  tgba_bdd_concrete::set_init_state(bdd s)
  {
    // Usually, the ltl2tgba translator will return an
    // initial state which does not include all true Now variables,
    // even though the truth of some Now variables is garanteed.
    //
    // For instance, when building the automata for the formula GFa,
    // the translator will define the following two equivalences
    //    Now[Fa] <=> a | (Prom[a] & Next[Fa])
    //    Now[GFa] <=> Now[Fa] & Next[GFa]
    // and return Now[GFa] as initial state.
    //
    // Starting for state Now[GFa], we could then build
    // the following automaton:
    //    In state Now[GFa]:
    //        if `a', go to state Now[GFa] & Now[Fa]
    //        if `!a', go to state Now[GFa] & Now[Fa] with Prom[a]
    //    In state Now[GFa] & Now[Fa]:
    //        if `a', go to state Now[GFa] & Now[Fa]
    //        if `!a', go to state Now[GFa] & Now[Fa] with Prom[a]
    //
    // As we can see, states Now[GFa] and Now[GFa] & Now[Fa] share
    // the same actions.  This is no surprise, because
    // Now[GFa] <=> Now[GFa] & Now[Fa] according to the equivalences
    // defined by the translator.
    //
    // This happens because we haven't completed the initial
    // state with the value of other Now variables.  We can
    // complete this state with the other equivalant Now variables
    // here, but we can't do anything about the remaining unknown
    // variables.
    s &= bdd_relprod(s, data_.relation, data_.notnow_set);
    init_ = s;
  }

  state_bdd*
  tgba_bdd_concrete::get_init_state() const
  {
    return new state_bdd(init_);
  }

  bdd
  tgba_bdd_concrete::get_init_bdd() const
  {
    return init_;
  }

  tgba_succ_iterator_concrete*
  tgba_bdd_concrete::succ_iter(const state* local_state,
			       const state* global_state,
			       const tgba* global_automaton) const
  {
    const state_bdd* s = down_cast<const state_bdd*>(local_state);
    assert(s);
    bdd succ_set = data_.relation & s->as_bdd();
    // If we are in a product, inject the local conditions of
    // all other automata to limit the number of successors.
    if (global_automaton)
      {
	bdd varused = bdd_support(succ_set);
	bdd global_conds = global_automaton->support_conditions(global_state);
	succ_set = bdd_appexcomp(succ_set, global_conds, bddop_and, varused);
      }
    return new tgba_succ_iterator_concrete(data_, succ_set);
  }

  bdd
  tgba_bdd_concrete::compute_support_conditions(const state* st) const
  {
    const state_bdd* s = down_cast<const state_bdd*>(st);
    assert(s);
    return bdd_relprod(s->as_bdd(), data_.relation, data_.notvar_set);
  }

  bdd
  tgba_bdd_concrete::compute_support_variables(const state* st) const
  {
    const state_bdd* s = down_cast<const state_bdd*>(st);
    assert(s);
    bdd succ_set = data_.relation & s->as_bdd();
    // bdd_support must be called BEFORE bdd_exist
    // because bdd_exist(bdd_support((a&Next[f])|(!a&Next[g])),Next[*])
    // is obviously not the same as bdd_support(a|!a).
    // In other words: we cannot reuse compute_support_conditions() for
    // this computation.
    //
    // Also we need to inject the support of acceptance conditions, because a
    // "Next[f]" that looks like one transition might in fact be two
    // transitions if the acceptance condition distinguish between
    // letters, e.g. "Next[f] & ((a & Acc[1]) | (!a))"
    return bdd_exist(bdd_support(succ_set)
		     & data_.acceptance_conditions_support,
		     data_.notvar_set);
  }

  std::string
  tgba_bdd_concrete::format_state(const state* state) const
  {
    const state_bdd* s = down_cast<const state_bdd*>(state);
    assert(s);
    return bdd_format_set(get_dict(), s->as_bdd());
  }

  bdd_dict*
  tgba_bdd_concrete::get_dict() const
  {
    return data_.dict;
  }

  bdd
  tgba_bdd_concrete::all_acceptance_conditions() const
  {
    return data_.all_acceptance_conditions;
  }

  bdd
  tgba_bdd_concrete::neg_acceptance_conditions() const
  {
    return data_.negacc_set;
  }

  const tgba_bdd_core_data&
  tgba_bdd_concrete::get_core_data() const
  {
    return data_;
  }

  void
  tgba_bdd_concrete::delete_unaccepting_scc()
  {
    data_.delete_unaccepting_scc(init_);
    set_init_state(init_);
  }

}
