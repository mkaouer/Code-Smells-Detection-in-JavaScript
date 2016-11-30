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
}
