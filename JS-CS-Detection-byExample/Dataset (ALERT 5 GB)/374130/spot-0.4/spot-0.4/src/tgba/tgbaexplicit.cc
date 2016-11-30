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

#include "ltlast/atomic_prop.hh"
#include "ltlast/constant.hh"
#include "ltlvisit/destroy.hh"
#include "tgbaexplicit.hh"
#include "tgba/formula2bdd.hh"
#include <cassert>

namespace spot
{

  ////////////////////////////////////////
  // tgba_explicit_succ_iterator

  tgba_explicit_succ_iterator::tgba_explicit_succ_iterator
  (const tgba_explicit::state* s, bdd all_acc)
    : s_(s), all_acceptance_conditions_(all_acc)
  {
  }

  void
  tgba_explicit_succ_iterator::first()
  {
    i_ = s_->begin();
  }

  void
  tgba_explicit_succ_iterator::next()
  {
    ++i_;
  }

  bool
  tgba_explicit_succ_iterator::done() const
  {
    return i_ == s_->end();
  }

  state_explicit*
  tgba_explicit_succ_iterator::current_state() const
  {
    assert(!done());
    return new state_explicit((*i_)->dest);
  }

  bdd
  tgba_explicit_succ_iterator::current_condition() const
  {
    assert(!done());
    return (*i_)->condition;
  }

  bdd
  tgba_explicit_succ_iterator::current_acceptance_conditions() const
  {
    assert(!done());
    return (*i_)->acceptance_conditions & all_acceptance_conditions_;
  }


  ////////////////////////////////////////
  // state_explicit

  const tgba_explicit::state*
  state_explicit::get_state() const
  {
    return state_;
  }

  int
  state_explicit::compare(const spot::state* other) const
  {
    const state_explicit* o = dynamic_cast<const state_explicit*>(other);
    assert(o);
    return o->get_state() - get_state();
  }

  size_t
  state_explicit::hash() const
  {
    return
      reinterpret_cast<const char*>(get_state()) - static_cast<const char*>(0);
  }

  state_explicit*
  state_explicit::clone() const
  {
    return new state_explicit(*this);
  }

  ////////////////////////////////////////
  // tgba_explicit


  tgba_explicit::tgba_explicit(bdd_dict* dict)
    : dict_(dict), init_(0), all_acceptance_conditions_(bddfalse),
      neg_acceptance_conditions_(bddtrue),
      all_acceptance_conditions_computed_(false)
  {
  }

  tgba_explicit::~tgba_explicit()
  {
    ns_map::iterator i;
    for (i = name_state_map_.begin(); i != name_state_map_.end(); ++i)
      {
	tgba_explicit::state::iterator i2;
	for (i2 = i->second->begin(); i2 != i->second->end(); ++i2)
	  delete *i2;
	delete i->second;
      }
    dict_->unregister_all_my_variables(this);
  }

  tgba_explicit::state*
  tgba_explicit::add_state(const std::string& name)
  {
    ns_map::iterator i = name_state_map_.find(name);
    if (i == name_state_map_.end())
      {
	tgba_explicit::state* s = new tgba_explicit::state;
	name_state_map_[name] = s;
	state_name_map_[s] = name;

	// The first state we add is the inititial state.
	// It can also be overridden with set_init_state().
	if (!init_)
	  init_ = s;

	return s;
      }
    return i->second;
  }

  tgba_explicit::state*
  tgba_explicit::set_init_state(const std::string& state)
  {
    tgba_explicit::state* s = add_state(state);
    init_ = s;
    return s;
  }

  tgba_explicit::transition*
  tgba_explicit::create_transition(state* source, const state* dest)
  {
    transition* t = new transition;
    t->dest = dest;
    t->condition = bddtrue;
    t->acceptance_conditions = bddfalse;
    source->push_back(t);
    return t;
  }

  tgba_explicit::transition*
  tgba_explicit::create_transition(const std::string& source,
				   const std::string& dest)
  {
    // It's important that the source be created before the
    // destination, so the first encountered source becomes the
    // default initial state.
    state* s = add_state(source);
    return create_transition(s, add_state(dest));
  }

  void
  tgba_explicit::add_condition(transition* t, const ltl::formula* f)
  {
    t->condition &= formula_to_bdd(f, dict_, this);
    ltl::destroy(f);
  }

  void
  tgba_explicit::add_conditions(transition* t, bdd f)
  {
    dict_->register_propositions(f, this);
    t->condition &= f;
  }

  void
  tgba_explicit::declare_acceptance_condition(const ltl::formula* f)
  {
    int v = dict_->register_acceptance_variable(f, this);
    ltl::destroy(f);
    bdd neg = bdd_nithvar(v);
    neg_acceptance_conditions_ &= neg;

    // Append neg to all acceptance conditions.
    ns_map::iterator i;
    for (i = name_state_map_.begin(); i != name_state_map_.end(); ++i)
      {
	tgba_explicit::state::iterator i2;
	for (i2 = i->second->begin(); i2 != i->second->end(); ++i2)
	  (*i2)->acceptance_conditions &= neg;
      }

    all_acceptance_conditions_computed_ = false;
  }

  void
  tgba_explicit::copy_acceptance_conditions_of(const tgba *a)
  {
    assert(neg_acceptance_conditions_ == bddtrue);
    assert(all_acceptance_conditions_computed_ == false);
    bdd f = a->neg_acceptance_conditions();
    dict_->register_acceptance_variables(f, this);
    neg_acceptance_conditions_ = f;
  }

  void
  tgba_explicit::complement_all_acceptance_conditions()
  {
    bdd all = all_acceptance_conditions();
    ns_map::iterator i;
    for (i = name_state_map_.begin(); i != name_state_map_.end(); ++i)
      {
	tgba_explicit::state::iterator i2;
	for (i2 = i->second->begin(); i2 != i->second->end(); ++i2)
	  {
	    (*i2)->acceptance_conditions = all - (*i2)->acceptance_conditions;
	  }
      }
  }

  void
  tgba_explicit::merge_transitions()
  {
    ns_map::iterator i;
    for (i = name_state_map_.begin(); i != name_state_map_.end(); ++i)
      {
	state::iterator t1;
	for (t1 = i->second->begin(); t1 != i->second->end(); ++t1)
	  {
	    bdd acc = (*t1)->acceptance_conditions;
	    const state* dest = (*t1)->dest;

	    // Find another transition with the same destination and
	    // acceptance conditions.
	    state::iterator t2 = t1;
	    ++t2;
	    while (t2 != i->second->end())
	      {
		state::iterator t2copy = t2++;
		if ((*t2copy)->acceptance_conditions == acc
		    && (*t2copy)->dest == dest)
		  {
		    (*t1)->condition |= (*t2copy)->condition;
		    delete *t2copy;
		    i->second->erase(t2copy);
		  }
	      }
	  }
      }
  }

  bool
  tgba_explicit::has_acceptance_condition(const ltl::formula* f) const
  {
    return dict_->is_registered_acceptance_variable(f, this);
  }

  bdd
  tgba_explicit::get_acceptance_condition(const ltl::formula* f)
  {
    bdd_dict::fv_map::iterator i = dict_->acc_map.find(f);
    assert(has_acceptance_condition(f));
    /* If this second assert fails and the first doesn't,
       things are badly broken.  This has already happened. */
    assert(i != dict_->acc_map.end());
    ltl::destroy(f);
    bdd v = bdd_ithvar(i->second);
    v &= bdd_exist(neg_acceptance_conditions_, v);
    return v;
  }

  void
  tgba_explicit::add_acceptance_condition(transition* t, const ltl::formula* f)
  {
    bdd c = get_acceptance_condition(f);
    t->acceptance_conditions |= c;
  }

  void
  tgba_explicit::add_acceptance_conditions(transition* t, bdd f)
  {
    bdd sup = bdd_support(f);
    dict_->register_acceptance_variables(sup, this);
    while (sup != bddtrue)
      {
	neg_acceptance_conditions_ &= bdd_nithvar(bdd_var(sup));
	sup = bdd_high(sup);
      }
    t->acceptance_conditions |= f;
  }

  state*
  tgba_explicit::get_init_state() const
  {
    // Fix empty automata by adding a lone initial state.
    if (!init_)
      const_cast<tgba_explicit*>(this)->add_state("empty");
    return new state_explicit(init_);
  }

  tgba_succ_iterator*
  tgba_explicit::succ_iter(const spot::state* state,
			   const spot::state* global_state,
			   const tgba* global_automaton) const
  {
    const state_explicit* s = dynamic_cast<const state_explicit*>(state);
    assert(s);
    (void) global_state;
    (void) global_automaton;
    return new tgba_explicit_succ_iterator(s->get_state(),
					   all_acceptance_conditions());
  }

  bdd
  tgba_explicit::compute_support_conditions(const spot::state* in) const
  {
    const state_explicit* s = dynamic_cast<const state_explicit*>(in);
    assert(s);
    const state* st = s->get_state();

    bdd res = bddtrue;
    tgba_explicit::state::const_iterator i;
    for (i = st->begin(); i != st->end(); ++i)
      res |= (*i)->condition;
    return res;
  }

  bdd
  tgba_explicit::compute_support_variables(const spot::state* in) const
  {
    const state_explicit* s = dynamic_cast<const state_explicit*>(in);
    assert(s);
    const state* st = s->get_state();

    bdd res = bddtrue;
    tgba_explicit::state::const_iterator i;
    for (i = st->begin(); i != st->end(); ++i)
      res &= bdd_support((*i)->condition);
    return res;
  }

  bdd_dict*
  tgba_explicit::get_dict() const
  {
    return dict_;
  }

  std::string
  tgba_explicit::format_state(const spot::state* s) const
  {
    const state_explicit* se = dynamic_cast<const state_explicit*>(s);
    assert(se);
    sn_map::const_iterator i = state_name_map_.find(se->get_state());
    assert(i != state_name_map_.end());
    return i->second;
  }

  bdd
  tgba_explicit::all_acceptance_conditions() const
  {
    if (!all_acceptance_conditions_computed_)
      {
	bdd all = bddfalse;

	// Build all_acceptance_conditions_ from neg_acceptance_conditions_
	// I.e., transform !A & !B & !C into
	//        A & !B & !C
	//     + !A &  B & !C
	//     + !A & !B &  C
	bdd cur = neg_acceptance_conditions_;
	while (cur != bddtrue)
	  {
	    assert(cur != bddfalse);

	    bdd v = bdd_ithvar(bdd_var(cur));
	    all |= v & bdd_exist(neg_acceptance_conditions_, v);

	    assert(bdd_high(cur) != bddtrue);
	    cur = bdd_low(cur);
	  }
	all_acceptance_conditions_ = all;
	all_acceptance_conditions_computed_ = true;
      }
    return all_acceptance_conditions_;
  }

  bdd
  tgba_explicit::neg_acceptance_conditions() const
  {
    return neg_acceptance_conditions_;
  }

}
