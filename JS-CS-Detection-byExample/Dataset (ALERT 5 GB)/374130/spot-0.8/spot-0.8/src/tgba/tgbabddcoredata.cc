// Copyright (C) 2009, 2011 Laboratoire de Recherche et Développement de
// l'Epita (LRDE).
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

#include <cassert>
#include "tgbabddcoredata.hh"

namespace spot
{
  tgba_bdd_core_data::tgba_bdd_core_data(bdd_dict* dict)
    : relation(bddtrue),
      acceptance_conditions(bddfalse),
      acceptance_conditions_support(bddtrue),
      all_acceptance_conditions(bddfalse),
      now_set(bddtrue),
      next_set(bddtrue),
      nownext_set(bddtrue),
      notnow_set(bddtrue),
      notnext_set(bddtrue),
      var_set(bddtrue),
      notvar_set(bddtrue),
      varandnext_set(bddtrue),
      acc_set(bddtrue),
      notacc_set(bddtrue),
      negacc_set(bddtrue),
      dict(dict)
  {
  }

  tgba_bdd_core_data::tgba_bdd_core_data(const tgba_bdd_core_data& copy)
    : relation(copy.relation),
      acceptance_conditions(copy.acceptance_conditions),
      acceptance_conditions_support(copy.acceptance_conditions_support),
      all_acceptance_conditions(copy.all_acceptance_conditions),
      now_set(copy.now_set),
      next_set(copy.next_set),
      nownext_set(copy.nownext_set),
      notnow_set(copy.notnow_set),
      notnext_set(copy.notnext_set),
      var_set(copy.var_set),
      notvar_set(copy.notvar_set),
      varandnext_set(copy.varandnext_set),
      acc_set(copy.acc_set),
      notacc_set(copy.notacc_set),
      negacc_set(copy.negacc_set),
      dict(copy.dict)
  {
  }

  // Merge two core_data.
  tgba_bdd_core_data::tgba_bdd_core_data(const tgba_bdd_core_data& left,
					   const tgba_bdd_core_data& right)
    : relation(left.relation & right.relation),
      acceptance_conditions(left.acceptance_conditions
			   | right.acceptance_conditions),
      acceptance_conditions_support(left.acceptance_conditions_support
				    & right.acceptance_conditions_support),
      all_acceptance_conditions(left.all_acceptance_conditions
			       | right.all_acceptance_conditions),
      now_set(left.now_set & right.now_set),
      next_set(left.next_set & right.next_set),
      nownext_set(left.nownext_set & right.nownext_set),
      notnow_set(left.notnow_set & right.notnow_set),
      notnext_set(left.notnext_set & right.notnext_set),
      var_set(left.var_set & right.var_set),
      notvar_set(left.notvar_set & right.notvar_set),
      varandnext_set(left.varandnext_set & right.varandnext_set),
      acc_set(left.acc_set & right.acc_set),
      notacc_set(left.notacc_set & right.notacc_set),
      negacc_set(left.negacc_set & right.negacc_set),
      dict(left.dict)
  {
    assert(dict == right.dict);
  }

  const tgba_bdd_core_data&
  tgba_bdd_core_data::operator= (const tgba_bdd_core_data& copy)
  {
    if (this != &copy)
      {
	this->~tgba_bdd_core_data();
	new (this) tgba_bdd_core_data(copy);
      }
    return *this;
  }

  void
  tgba_bdd_core_data::declare_now_next(bdd now, bdd next)
  {
    now_set &= now;
    next_set &= next;
    notnext_set &= now;
    notnow_set &= next;
    bdd both = now & next;
    nownext_set &= both;
    notvar_set &= both;
    notacc_set &= both;
    varandnext_set &= next;
  }

  void
  tgba_bdd_core_data::declare_atomic_prop(bdd var)
  {
    notnow_set &= var;
    notnext_set &= var;
    notacc_set &= var;
    var_set &= var;
    varandnext_set &= var;
  }

  void
  tgba_bdd_core_data::declare_acceptance_condition(bdd acc)
  {
    notnow_set &= acc;
    notnext_set &= acc;
    notvar_set &= acc;
    acc_set &= acc;
    negacc_set &= !acc;
  }

  void
  tgba_bdd_core_data::delete_unaccepting_scc(bdd init)
  {
    bdd er = bdd_exist(relation, var_set); /// existsRelation
    bdd s0 = bddfalse;
    bdd s1 = bdd_exist(bdd_exist(init & relation, var_set), now_set);
    s1 = bdd_replace(s1, dict->next_to_now);

    /// Find all reachable states
    while (s0 != s1)
    {
      s0 = s1;
      /// Compute s1 = succ(s0) | s
      s1 = bdd_replace(bdd_exist(s0 & er, now_set), dict->next_to_now) | s0;
    }

    /// Find states which can be visited infinitely often while seeing
    /// all acceptance conditions
    s0 = bddfalse;
    while (s0 != s1)
    {
      s0 = s1;
      bdd all = all_acceptance_conditions;
      while (all != bddfalse)
      {
	bdd next = bdd_satone(all);
	all -= next;
	s1 = infinitely_often(s1, next, er);
      }
    }

    relation = (relation & bdd_replace(s0, dict->now_to_next));
  }

  bdd
  tgba_bdd_core_data::infinitely_often(bdd s, bdd acc, bdd er)
  {
    bdd ar = acc & (relation & acceptance_conditions); /// accRelation
    bdd s0 = bddfalse;
    bdd s1 = s;

    while (s0 != s1)
    {
      s0 = s1;
      bdd as = bdd_replace(s0, dict->now_to_next) & ar;
      as = bdd_exist(bdd_exist(as, next_set), var_set) & s0;

      /// Do predStar
      bdd s0_ = bddfalse;
      bdd s1_ = bdd_exist(as, acc_set);
      while (s0_ != s1_)
      {
	s0_ = s1_;
	/// Compute s1_ = pred(s0_) | s0_
	s1_ = bdd_exist(er & bdd_replace(s0_, dict->now_to_next), next_set);
	s1_ = (s1_ & s0) | s0_;
      }
      s1 = s0_;
    }

    return s0;
  }
}
