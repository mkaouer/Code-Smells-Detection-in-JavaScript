// Copyright (C) 2009, 2010, 2011 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include <sstream>
#include "ltlast/atomic_prop.hh"
#include "ltlast/constant.hh"
#include "tgbaexplicit.hh"
#include "tgba/formula2bdd.hh"
#include "misc/bddop.hh"
#include <cassert>
#include "ltlvisit/tostring.hh"

namespace spot
{

  ////////////////////////////////////////
  // tgba_explicit_succ_iterator

  tgba_explicit_succ_iterator::tgba_explicit_succ_iterator
  (const state_explicit::transitions_t* s, bdd all_acc)
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
    return const_cast<state_explicit*>(i_->dest);
  }

  bdd
  tgba_explicit_succ_iterator::current_condition() const
  {
    assert(!done());
    return i_->condition;
  }

  bdd
  tgba_explicit_succ_iterator::current_acceptance_conditions() const
  {
    assert(!done());
    return i_->acceptance_conditions & all_acceptance_conditions_;
  }


  ////////////////////////////////////////
  // state_explicit

  int
  state_explicit::compare(const spot::state* other) const
  {
    const state_explicit* o = down_cast<const state_explicit*>(other);
    assert(o);
    // Do not simply return "o - this", it might not fit in an int.
    if (o < this)
      return -1;
    if (o > this)
      return 1;
    return 0;
  }

  size_t
  state_explicit::hash() const
  {
    return
      reinterpret_cast<const char*>(this) - static_cast<const char*>(0);
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
    dict_->unregister_all_my_variables(this);
  }

  tgba_explicit::transition*
  tgba_explicit::create_transition(state* source, const state* dest)
  {
    transition t;
    t.dest = dest;
    t.condition = bddtrue;
    t.acceptance_conditions = bddfalse;
    state_explicit::transitions_t::iterator i =
      source->successors.insert(source->successors.end(), t);
    return &*i;
  }
  void
  tgba_explicit::add_condition(transition* t, const ltl::formula* f)
  {
    t->condition &= formula_to_bdd(f, dict_, this);
    f->destroy();
  }

  void
  tgba_explicit::add_conditions(transition* t, bdd f)
  {
    dict_->register_propositions(f, this);
    t->condition &= f;
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
  tgba_explicit::set_acceptance_conditions(bdd acc)
  {
    assert(neg_acceptance_conditions_ == bddtrue);
    assert(all_acceptance_conditions_computed_ == false);
    dict_->register_acceptance_variables(bdd_support(acc), this);
    neg_acceptance_conditions_ = compute_neg_acceptance_conditions(acc);
    all_acceptance_conditions_computed_ = true;
    all_acceptance_conditions_ = acc;
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
    f->destroy();
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
      const_cast<tgba_explicit*>(this)->add_default_init();
    return init_;
  }

  tgba_succ_iterator*
  tgba_explicit::succ_iter(const spot::state* state,
			   const spot::state* global_state,
			   const tgba* global_automaton) const
  {
    const state_explicit* s = down_cast<const state_explicit*>(state);
    assert(s);
    (void) global_state;
    (void) global_automaton;
    return new tgba_explicit_succ_iterator(&s->successors,
					   all_acceptance_conditions());
  }

  bdd
  tgba_explicit::compute_support_conditions(const spot::state* in) const
  {
    const state_explicit* s = down_cast<const state_explicit*>(in);
    assert(s);
    const state_explicit::transitions_t& st = s->successors;

    bdd res = bddfalse;
    state_explicit::transitions_t::const_iterator i;
    for (i = st.begin(); i != st.end(); ++i)
      res |= i->condition;
    return res;
  }

  bdd
  tgba_explicit::compute_support_variables(const spot::state* in) const
  {
    const state_explicit* s = down_cast<const state_explicit*>(in);
    assert(s);
    const state_explicit::transitions_t& st = s->successors;

    bdd res = bddtrue;
    state_explicit::transitions_t::const_iterator i;
    for (i = st.begin(); i != st.end(); ++i)
      res &= bdd_support(i->condition);
    return res;
  }

  bdd_dict*
  tgba_explicit::get_dict() const
  {
    return dict_;
  }

  bdd
  tgba_explicit::all_acceptance_conditions() const
  {
    if (!all_acceptance_conditions_computed_)
      {
	all_acceptance_conditions_ =
	  compute_all_acceptance_conditions(neg_acceptance_conditions_);
	all_acceptance_conditions_computed_ = true;
      }
    return all_acceptance_conditions_;
  }

  bdd
  tgba_explicit::neg_acceptance_conditions() const
  {
    return neg_acceptance_conditions_;
  }

  tgba_explicit_string::~tgba_explicit_string()
  {
    ns_map::iterator i;
    for (i = name_state_map_.begin(); i != name_state_map_.end(); ++i)
    {
      // Do not erase the same state twice.  (Because of possible aliases.)
      if (state_name_map_.erase(i->second))
	{
	  delete i->second;
	}
    }
  }

  tgba_explicit::state*
  tgba_explicit_string::add_default_init()
  {
    return add_state("empty");
  }

  std::string
  tgba_explicit_string::format_state(const spot::state* s) const
  {
    const state_explicit* se = down_cast<const state_explicit*>(s);
    assert(se);
    sn_map::const_iterator i = state_name_map_.find(se);
    assert(i != state_name_map_.end());
    return i->second;
  }

  tgba_explicit_formula::~tgba_explicit_formula()
  {
    ns_map::iterator i = name_state_map_.begin();
    while (i != name_state_map_.end())
    {
      // Advance the iterator before deleting the formula.
      const ltl::formula* s = i->first;
      delete i->second;
      ++i;
      s->destroy();
    }
  }

  tgba_explicit::state* tgba_explicit_formula::add_default_init()
  {
    return add_state(ltl::constant::true_instance());
  }

  std::string
  tgba_explicit_formula::format_state(const spot::state* s) const
  {
    const state_explicit* se = down_cast<const state_explicit*>(s);
    assert(se);
    sn_map::const_iterator i = state_name_map_.find(se);
    assert(i != state_name_map_.end());
    return ltl::to_string(i->second);
  }

  tgba_explicit_number::~tgba_explicit_number()
  {
    ns_map::iterator i = name_state_map_.begin();
    while (i != name_state_map_.end())
      {
	delete i->second;
	++i;
      }
  }

  tgba_explicit::state* tgba_explicit_number::add_default_init()
  {
    return add_state(0);
  }

  std::string
  tgba_explicit_number::format_state(const spot::state* s) const
  {
    const state_explicit* se = down_cast<const state_explicit*>(s);
    assert(se);
    sn_map::const_iterator i = state_name_map_.find(se);
    assert(i != state_name_map_.end());
    std::stringstream ss;
    ss << i->second;
    return ss.str();
  }
}
