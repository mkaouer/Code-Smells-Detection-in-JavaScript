// Copyright (C) 2009, 2010, 2011 Laboratoire de Recherche et Développement
// de l'Epita (LRDE)
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

#include <map>
#include <algorithm>
#include <iterator>
#include <iostream>
#include "tgba/formula2bdd.hh"
#include "ltlvisit/tostring.hh"
#include "ltlvisit/clone.hh"
#include "misc/bddop.hh"
#include "taatgba.hh"

namespace spot
{
  /*--------.
  | taa_tgba |
  `--------*/

  taa_tgba::taa_tgba(bdd_dict* dict)
    : dict_(dict),
      all_acceptance_conditions_(bddfalse),
      all_acceptance_conditions_computed_(false),
      neg_acceptance_conditions_(bddtrue),
      init_(0), state_set_vec_()
  {
  }

  taa_tgba::~taa_tgba()
  {
    ss_vec::iterator j;
    for (j = state_set_vec_.begin(); j != state_set_vec_.end(); ++j)
      delete *j;
    dict_->unregister_all_my_variables(this);
  }

  void
  taa_tgba::add_condition(transition* t, const ltl::formula* f)
  {
    t->condition &= formula_to_bdd(f, dict_, this);
    f->destroy();
  }

  state*
  taa_tgba::get_init_state() const
  {
    assert(init_);
    return new spot::state_set(init_);
  }

  tgba_succ_iterator*
  taa_tgba::succ_iter(const spot::state* state,
                     const spot::state* global_state,
                     const tgba* global_automaton) const
  {
    const spot::state_set* s = down_cast<const spot::state_set*>(state);
    assert(s);
    (void) global_state;
    (void) global_automaton;
    return new taa_succ_iterator(s->get_state(), all_acceptance_conditions());
  }

  bdd_dict*
  taa_tgba::get_dict() const
  {
    return dict_;
  }

  bdd
  taa_tgba::all_acceptance_conditions() const
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
  taa_tgba::neg_acceptance_conditions() const
  {
    return neg_acceptance_conditions_;
  }

  bdd
  taa_tgba::compute_support_conditions(const spot::state* s) const
  {
    const spot::state_set* se = down_cast<const spot::state_set*>(s);
    assert(se);
    const state_set* ss = se->get_state();

    bdd res = bddtrue;
    taa_tgba::state_set::const_iterator i;
    taa_tgba::state::const_iterator j;
    for (i = ss->begin(); i != ss->end(); ++i)
      for (j = (*i)->begin(); j != (*i)->end(); ++j)
	res |= (*j)->condition;
    return res;
  }

  bdd
  taa_tgba::compute_support_variables(const spot::state* s) const
  {
    const spot::state_set* se = down_cast<const spot::state_set*>(s);
    assert(se);
    const state_set* ss = se->get_state();

    bdd res = bddtrue;
    taa_tgba::state_set::const_iterator i;
    taa_tgba::state::const_iterator j;
    for (i = ss->begin(); i != ss->end(); ++i)
      for (j = (*i)->begin(); j != (*i)->end(); ++j)
	res &= bdd_support((*j)->condition);
    return res;
  }

  /*----------.
  | state_set |
  `----------*/

  const taa_tgba::state_set*
  state_set::get_state() const
  {
    return s_;
  }

  int
  state_set::compare(const spot::state* other) const
  {
    const state_set* o = down_cast<const state_set*>(other);
    assert(o);

    const taa_tgba::state_set* s1 = get_state();
    const taa_tgba::state_set* s2 = o->get_state();

    if (s1->size() != s2->size())
      return s1->size() - s2->size();

    taa_tgba::state_set::const_iterator it1 = s1->begin();
    taa_tgba::state_set::const_iterator it2 = s2->begin();
    while (it2 != s2->end())
    {
      int i = *it1++ - *it2++;
      if (i != 0)
	return i;
    }
    return 0;
  }

  size_t
  state_set::hash() const
  {
    size_t res = 0;
    taa_tgba::state_set::const_iterator it = s_->begin();
    while (it != s_->end())
    {
      res ^= reinterpret_cast<const char*>(*it++) - static_cast<char*>(0);
      res = wang32_hash(res);
    }
    return res;
  }

  state_set*
  state_set::clone() const
  {
    if (delete_me_ && s_)
      return new spot::state_set(new taa_tgba::state_set(*s_), true);
    else
      return new spot::state_set(s_, false);
  }

  /*--------------.
  | taa_succ_iter |
  `--------------*/

  taa_succ_iterator::taa_succ_iterator(const taa_tgba::state_set* s,
                                       bdd all_acc)
    : all_acceptance_conditions_(all_acc), seen_()
  {
    if (s->empty())
    {
      taa_tgba::transition* t = new taa_tgba::transition;
      t->condition = bddtrue;
      t->acceptance_conditions = bddfalse;
      t->dst = new taa_tgba::state_set;
      succ_.push_back(t);
      return;
    }

    bounds_t bounds;
    for (taa_tgba::state_set::const_iterator i = s->begin(); i != s->end(); ++i)
      bounds.push_back(std::make_pair((*i)->begin(), (*i)->end()));

    /// Sorting might make the cartesian product faster by not
    /// exploring all possibilities.
    std::sort(bounds.begin(), bounds.end(), distance_sort());

    std::vector<iterator> pos;
    pos.reserve(bounds.size());
    for (bounds_t::const_iterator i = bounds.begin(); i != bounds.end(); ++i)
      pos.push_back(i->first);

    while (pos[0] != bounds[0].second)
    {
      taa_tgba::transition* t = new taa_tgba::transition;
      t->condition = bddtrue;
      t->acceptance_conditions = bddfalse;
      taa_tgba::state_set* ss = new taa_tgba::state_set;

      unsigned p;
      for (p = 0; p < pos.size() && t->condition != bddfalse; ++p)
      {
	taa_tgba::state_set::const_iterator j;
	for (j = (*pos[p])->dst->begin(); j != (*pos[p])->dst->end(); ++j)
	  if ((*j)->size() > 0) // Remove sink states.
	    ss->insert(*j);

	// Fill the new transition.
	t->condition &= (*pos[p])->condition;
	t->acceptance_conditions |= (*pos[p])->acceptance_conditions;
      } // If p != pos.size() we have found a contradiction
      assert(p > 0);
      t->dst = ss;
      // Boxing to be able to insert ss in the map directly.
      spot::state_set* b = new spot::state_set(ss);

      // If no contradiction, then look for another transition to
      // merge with the new one.
      seen_map::iterator i = seen_.end(); // Initialize to silent a g++ warning.
      std::vector<taa_tgba::transition*>::iterator j;
      if (t->condition != bddfalse)
      {
	i = seen_.find(b);
	if (i != seen_.end())
	  for (j = i->second.begin(); j != i->second.end(); ++j)
	  {
	    taa_tgba::transition* current = *j;
	    if (*current->dst == *t->dst
		&& current->condition == t->condition)
	    {
	      current->acceptance_conditions &= t->acceptance_conditions;
	      break;
	    }
	    if (*current->dst == *t->dst
		&& current->acceptance_conditions == t->acceptance_conditions)
	    {
	      current->condition |= t->condition;
	      break;
	    }
	}
      }
      // Mark the new transition as seen and keep it if we have not
      // found any contradiction and no other transition to merge
      // with, or delete it otherwise.
      if (t->condition != bddfalse
	  && (i == seen_.end() || j == i->second.end()))
      {
	seen_[b].push_back(t);
	succ_.push_back(t);
      }
      else
      {
	delete t->dst;
	delete t;
	delete b;
      }

      for (int i = pos.size() - 1; i >= 0; --i)
      {
	if ((i < int(p))
	    && (std::distance(pos[i], bounds[i].second) > 1
		|| (i == 0 && std::distance(pos[i], bounds[i].second) == 1)))
	{
	  ++pos[i];
	  break;
	}
	else
	  pos[i] = bounds[i].first;
      }
    }
  }

  taa_succ_iterator::~taa_succ_iterator()
  {
    for (seen_map::iterator i = seen_.begin(); i != seen_.end();)
    {
      // Advance the iterator before deleting the state set.
      const spot::state_set* s = i->first;
      ++i;
      delete s;
    }
    for (unsigned i = 0; i < succ_.size(); ++i)
    {
      delete succ_[i]->dst;
      delete succ_[i];
    }
  }

  void
  taa_succ_iterator::first()
  {
    i_ = succ_.begin();
  }

  void
  taa_succ_iterator::next()
  {
    ++i_;
  }

  bool
  taa_succ_iterator::done() const
  {
    return i_ == succ_.end();
  }

  spot::state_set*
  taa_succ_iterator::current_state() const
  {
    assert(!done());
    return new spot::state_set(new taa_tgba::state_set(*(*i_)->dst), true);
  }

  bdd
  taa_succ_iterator::current_condition() const
  {
    assert(!done());
    return (*i_)->condition;
  }

  bdd
  taa_succ_iterator::current_acceptance_conditions() const
  {
    assert(!done());
    return all_acceptance_conditions_ -
      ((*i_)->acceptance_conditions & all_acceptance_conditions_);
  }

  /*----------------.
  | taa_tgba_string |
  `----------------*/

  taa_tgba_string::~taa_tgba_string()
  {
    ns_map::iterator i;
    for (i = name_state_map_.begin(); i != name_state_map_.end(); ++i)
    {
      taa_tgba::state::iterator i2;
      for (i2 = i->second->begin(); i2 != i->second->end(); ++i2)
	delete *i2;
      delete i->second;
    }
  }

  std::string
  taa_tgba_string::label_to_string(const label_t& label) const
  {
    return label;
  }

  std::string
  taa_tgba_string::clone_if(const label_t& label) const
  {
    return label;
  }

  /*-----------------.
  | taa_tgba_formula |
  `-----------------*/

  taa_tgba_formula::~taa_tgba_formula()
  {
    ns_map::iterator i;
    for (i = name_state_map_.begin(); i != name_state_map_.end();)
    {
      taa_tgba::state::iterator i2;
      for (i2 = i->second->begin(); i2 != i->second->end(); ++i2)
	delete *i2;
      // Advance the iterator before destroying the formula.
      const ltl::formula* s = i->first;
      delete i->second;
      ++i;
      s->destroy();
    }
  }

  std::string
  taa_tgba_formula::label_to_string(const label_t& label) const
  {
    return ltl::to_string(label);
  }

  ltl::formula*
  taa_tgba_formula::clone_if(const label_t& label) const
  {
    return label->clone();
  }
}
