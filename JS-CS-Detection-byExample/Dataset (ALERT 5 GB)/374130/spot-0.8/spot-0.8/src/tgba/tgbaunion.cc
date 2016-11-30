// Copyright (C) 2009, 2011 Laboratoire de Recherche et Développement
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

#include "tgbaunion.hh"
#include <string>
#include <cassert>
#include "misc/hashfunc.hh"

namespace spot
{

  ////////////////////////////////////////////////////////////
  // state_union

  state_union::state_union(const state_union& o)
    : state(),
      left_(o.left()->clone()),
      right_(o.right()->clone())
  {
  }

  state_union::~state_union()
  {
    left_->destroy();
    right_->destroy();
  }

  int
  state_union::compare(const state* other) const
  {
    const state_union* o = down_cast<const state_union*>(other);
    assert(o);
    // Initial state
    if (!o->left() && !o->right())
      return left_ || right_;
    // States from different automata.
    if (left_ && !o->left())
      return -1;
    // States from different automata.
    if (right_ && !o->right())
      return 1;
    // States from left automata.
    if (left_)
      return left_->compare(o->left());
    // States from right automata.
    if (right_)
      return right_->compare(o->right());
    // 0 should never be returned.
    return 0;
  }

  size_t
  state_union::hash() const
  {
    // We assume that size_t is 32-bit wide.
    if (left_)
      return wang32_hash(left_->hash());
    if (right_)
      return wang32_hash(right_->hash());
    return 0;
  }

  state_union*
  state_union::clone() const
  {
    return new state_union(*this);
  }

   ////////////////////////////////////////////////////////////
  // tgba_succ_iterator_union

  tgba_succ_iterator_union::tgba_succ_iterator_union
  (tgba_succ_iterator* left, tgba_succ_iterator* right,
   bdd left_missing, bdd right_missing, bdd left_neg, bdd right_neg)
    : left_(left), right_(right),
      left_missing_(left_missing),
      right_missing_(right_missing),
      left_neg_(left_neg), right_neg_(right_neg)
  {
  }

  tgba_succ_iterator_union::~tgba_succ_iterator_union()
  {
    delete left_;
    delete right_;
  }

  void
  tgba_succ_iterator_union::next()
  {
    // Is it the initial state ?
    if (left_ && right_)
    {
      // Yes, first iterate on the successors of the initial state of the
      // left automaton.
      if (!left_->done())
      {
	left_->next();
	if (!left_->done())
	  current_cond_ = left_->current_condition();
	else
	  current_cond_ = right_->current_condition();
      }
      // Now iterate with the successors of the initial state of the right
      // automaton.
      else
      {
	right_->next();
	if (!right_->done())
	  current_cond_ = right_->current_condition();
      }
    }
    else
    {
      // No, iterate either on the left or the right automaton.
      if (left_)
      {
	left_->next();
	if (!left_->done())
	  current_cond_ = left_->current_condition();
      }
      else
      {
	right_->next();
	if (!right_->done())
	  current_cond_ = right_->current_condition();
      }
    }
  }

  void
  tgba_succ_iterator_union::first()
  {
    if (right_)
    {
      right_->first();
      current_cond_ = right_->current_condition();
    }
    if (left_)
    {
      left_->first();
      current_cond_ = left_->current_condition();
    }
  }

  bool
  tgba_succ_iterator_union::done() const
  {
    if (right_)
      return right_->done();
    else
      return left_->done();
  }

  state_union*
  tgba_succ_iterator_union::current_state() const
  {
    if (left_ && !left_->done())
      return new state_union(left_->current_state(), 0);
    else
      return new state_union(0, right_->current_state());
  }

  bdd
  tgba_succ_iterator_union::current_condition() const
  {
    return current_cond_;
  }

  bdd tgba_succ_iterator_union::current_acceptance_conditions() const
  {
    if (left_ && right_)
    {
      if (left_->done())
	return right_->current_acceptance_conditions();
      else
	return left_->current_acceptance_conditions();
    }
    // If we are either on the left or the right automaton, we need to modify
    // the acceptance conditions of the transition.
    if (left_)
    {
      bdd cur = (left_->current_acceptance_conditions() & left_neg_)
	| left_missing_;
      return cur;
    }
    else
    {
      bdd cur = (right_->current_acceptance_conditions() & right_neg_)
	| right_missing_;
      return cur;
    }
  }

  ////////////////////////////////////////////////////////////
  // tgba_union

  tgba_union::tgba_union(const tgba* left, const tgba* right)
    : dict_(left->get_dict()), left_(left), right_(right)
  {
    assert(dict_ == right->get_dict());

    // Conjunction of negated acceptance conditions, e.g. !a & !b & !c
    bdd lna = left_->neg_acceptance_conditions();
    bdd rna = right_->neg_acceptance_conditions();

    left_acc_complement_ = lna;
    right_acc_complement_ = rna;

    // Disjunctive Normal Form for all acceptance conditions
    // e.g. (a & !b & !c) | (b & !a & !c) | (c & !a & !b)
    bdd lac = left_->all_acceptance_conditions();
    bdd rac = right_->all_acceptance_conditions();

    // Remove all occurences in rna of the variables in the set lna.
    // The result is a set with the missing variables in the left automaton,
    // i.e. the variables that are in the right automaton but not in the left
    // one.
    bdd lmac = bdd_exist(rna, lna);
    left_var_missing_ = lmac;
    // Remove all occurences in lna of the variables in the set rna.
    // The result is a set with the missing variables in the right automaton,
    // i.e. the variables that are in the left automaton but not in the right
    // one.
    bdd rmac = bdd_exist(lna, rna);
    right_var_missing_ = rmac;

    // The new Disjunctive Normal Form for our union.
    all_acceptance_conditions_ = ((lac & lmac) | (rac & rmac));
    // Simply the conjunction of left and right negated acceptance conditions.
    neg_acceptance_conditions_ = lna & rna;

    // "-" is a NAND between the two sets.
    // The result is the list of acceptance conditions to add to each
    // transition.
    // For example if a transition on the left automaton has (a & !b)
    // and the right automaton has a new acceptance condition c, the resulting
    // acceptance condition will be (a & !b & !c) | (c & !a & !b)
    left_acc_missing_ = all_acceptance_conditions_ - (lac & lmac);
    right_acc_missing_ = all_acceptance_conditions_ - (rac & rmac);

    dict_->register_all_variables_of(&left_, this);
    dict_->register_all_variables_of(&right_, this);
  }

  tgba_union::~tgba_union()
  {
    dict_->unregister_all_my_variables(this);
  }

  state*
  tgba_union::get_init_state() const
  {
    return new state_union(0, 0);
  }

  tgba_succ_iterator_union*
  tgba_union::succ_iter(const state* local_state,
			const state* global_state,
			const tgba* global_automaton) const
  {
    (void) global_state;
    (void) global_automaton;
    const state_union* s = down_cast<const state_union*>(local_state);
    assert(s);
    tgba_succ_iterator_union* res = 0;
    // Is it the initial state ?
    if (!s->left() && !s->right())
    {
      // Yes, create an iterator with both initial states.
      state* left_init = left_->get_init_state();
      state* right_init = right_->get_init_state();
      tgba_succ_iterator* li = left_->succ_iter(left_init);
      tgba_succ_iterator* ri = right_->succ_iter(right_init);
      res = new tgba_succ_iterator_union(li, ri, left_acc_missing_,
					 right_acc_missing_,
					 left_var_missing_,
					 right_var_missing_);
      left_init->destroy();
      right_init->destroy();
    }
    else
    {
      // No, create an iterator based on the corresponding state
      // in the left or in the right automaton.
      if (s->left())
      {
        tgba_succ_iterator* li = left_->succ_iter(s->left());
	res = new tgba_succ_iterator_union(li, 0, left_acc_missing_,
					   right_acc_missing_,
					   left_var_missing_,
					   right_var_missing_);
      }
      else
      {
	tgba_succ_iterator* ri = right_->succ_iter(s->right());
	res = new tgba_succ_iterator_union(0, ri, left_acc_missing_,
					   right_acc_missing_,
					   left_var_missing_,
					   right_var_missing_);
      }
    }
    return res;
  }

  bdd
  tgba_union::compute_support_conditions(const state* in) const
  {
    const state_union* s = down_cast<const state_union*>(in);
    assert(s);
    if (!s->left() && !s->right())
      return (left_->support_conditions(left_->get_init_state())
	      & right_->support_conditions(right_->get_init_state()));
    if (s->left())
	return left_->support_conditions(s->left());
    else
      return right_->support_conditions(s->right());
  }

  bdd
  tgba_union::compute_support_variables(const state* in) const
  {
    const state_union* s = down_cast<const state_union*>(in);
    assert(s);
    if (!s->left() && !s->right())
      return (left_->support_variables(left_->get_init_state())
	      & right_->support_variables(right_->get_init_state()));
    if (s->left())
      return left_->support_variables(s->left());
    else
      return right_->support_variables(s->right());
  }

  bdd_dict*
  tgba_union::get_dict() const
  {
    return dict_;
  }

  std::string
  tgba_union::format_state(const state* target_state) const
  {
    const state_union* s = down_cast<const state_union*>(target_state);
    assert(s);
    if (!s->left() && !s->right())
    {
      return "initial";
    }
    else
    {
      if (s->left())
	return (left_->format_state(s->left()));
      else
	return (right_->format_state(s->right()));
    }
  }

  state*
  tgba_union::project_state(const state* s, const tgba* t) const
  {
    const state_union* s2 = down_cast<const state_union*>(s);
    assert(s2);
    // We can't project the initial state of our union.
    if (!s2->left() && !s2->right())
      return 0;
    if (t == this)
      return s2->clone();
    if (s2->left())
      return left_->project_state(s2->left(), t);
    else
      return right_->project_state(s2->right(), t);
  }

  bdd
  tgba_union::all_acceptance_conditions() const
  {
    return all_acceptance_conditions_;
  }

  bdd
  tgba_union::neg_acceptance_conditions() const
  {
    return neg_acceptance_conditions_;
  }
}
