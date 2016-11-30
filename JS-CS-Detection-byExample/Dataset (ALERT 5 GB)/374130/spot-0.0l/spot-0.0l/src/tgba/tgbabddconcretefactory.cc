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

#include "ltlvisit/clone.hh"
#include "ltlvisit/destroy.hh"
#include "tgbabddconcretefactory.hh"
namespace spot
{
  tgba_bdd_concrete_factory::tgba_bdd_concrete_factory(bdd_dict* dict)
    : data_(dict)
  {
  }

  tgba_bdd_concrete_factory::~tgba_bdd_concrete_factory()
  {
    acc_map_::iterator ai;
    for (ai = acc_.begin(); ai != acc_.end(); ++ai)
      destroy(ai->first);
    get_dict()->unregister_all_my_variables(this);
  }

  int
  tgba_bdd_concrete_factory::create_state(const ltl::formula* f)
  {
    int num = get_dict()->register_state(f, this);
    // Keep track of all "Now" variables for easy
    // existential quantification.
    data_.declare_now_next (bdd_ithvar(num), bdd_ithvar(num + 1));
    return num;
  }

  int
  tgba_bdd_concrete_factory::create_atomic_prop(const ltl::formula* f)
  {
    int num = get_dict()->register_proposition(f, this);
    // Keep track of all atomic proposition for easy
    // existential quantification.
    data_.declare_atomic_prop(bdd_ithvar(num));
    return num;
  }

  void
  tgba_bdd_concrete_factory::declare_acceptance_condition(bdd b,
							 const ltl::formula* a)
  {
    // Maintain a conjunction of BDDs associated to A.  We will latter
    // (in tgba_bdd_concrete_factory::finish()) associate this
    // conjunction to A.
    acc_map_::iterator ai = acc_.find(a);
    if (ai == acc_.end())
      {
	a = clone(a);
	acc_[a] = b;
      }
    else
      {
	ai->second &= b;
      }
  }

  void
  tgba_bdd_concrete_factory::finish()
  {
    acc_map_::iterator ai;
    for (ai = acc_.begin(); ai != acc_.end(); ++ai)
      {
	// Register a BDD variable for this acceptance condition.
	int num = get_dict()->register_acceptance_variable(ai->first, this);
	// Keep track of all acceptance conditions for easy
	// existential quantification.
	data_.declare_acceptance_condition(bdd_ithvar(num));
      }
    for (ai = acc_.begin(); ai != acc_.end(); ++ai)
      {
	bdd acc = bdd_ithvar(get_dict()->acc_map[ai->first]);

	// Complete acc with all the other acceptance conditions negated.
	acc &= bdd_exist(data_.negacc_set, acc);

	// Any state matching the BDD formulae registered is part
	// of this acceptance set.
	data_.acceptance_conditions |= ai->second & acc;

	// Keep track of all acceptance conditions, so that we can
	// easily check whether a transition satisfies all acceptance
	// conditions.
	data_.all_acceptance_conditions |= acc;
      }

    // Any constraint between Now variables also exist between Next
    // variables.  Doing this limits the quantity of useless
    // successors we will have to explore.  (By "useless successors"
    // I mean a combination of Next variables that represent a cul de sac
    // state: the combination exists but won't allow further exploration
    // because it fails the constraints.)
    data_.relation &= bdd_replace(bdd_exist(data_.relation, data_.notnow_set),
				  get_dict()->now_to_next);
  }

  const tgba_bdd_core_data&
  tgba_bdd_concrete_factory::get_core_data() const
  {
    return data_;
  }

  bdd_dict*
  tgba_bdd_concrete_factory::get_dict() const
  {
    return data_.dict;
  }

  void
  tgba_bdd_concrete_factory::constrain_relation(bdd new_rel)
  {
    data_.relation &= new_rel;
  }
}
