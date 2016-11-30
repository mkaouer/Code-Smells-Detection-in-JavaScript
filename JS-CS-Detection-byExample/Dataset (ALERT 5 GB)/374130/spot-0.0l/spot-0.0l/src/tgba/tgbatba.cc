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
#include "tgbatba.hh"
#include "bddprint.hh"
#include "ltlast/constant.hh"

namespace spot
{

  /// \brief A state for spot::tgba_tba_proxy.
  ///
  /// This state is in fact a pair of state: the state from the tgba
  /// automaton, and the "counter" (we use the acceptance set
  /// BDD variable instead of an integer counter).
  class state_tba_proxy : public state
  {
  public:
    state_tba_proxy(state* s, bdd acc)
      :	s_(s), acc_(acc)
    {
    }

    /// Copy constructor
    state_tba_proxy(const state_tba_proxy& o)
      : state(),
	s_(o.real_state()->clone()),
	acc_(o.acceptance_cond())
    {
    }

    virtual
    ~state_tba_proxy()
    {
      delete s_;
    }

    state*
    real_state() const
    {
      return s_;
    }

    bdd
    acceptance_cond() const
    {
      return acc_;
    }

    virtual int
    compare(const state* other) const
    {
      const state_tba_proxy* o = dynamic_cast<const state_tba_proxy*>(other);
      assert(o);
      int res = s_->compare(o->real_state());
      if (res != 0)
	return res;
      return acc_.id() - o->acceptance_cond().id();
    }

    virtual size_t
    hash() const
    {
      // We expect to have many more state than acceptance conditions.
      // Hence we keep only 8 bits for acceptance conditions.
      return (s_->hash() << 8) + (acc_.id() & 0xFF);
    }

    virtual
    state_tba_proxy* clone() const
    {
      return new state_tba_proxy(*this);
    }

  private:
    state* s_;
    bdd acc_;
  };


  /// \brief Iterate over the successors of tgba_tba_proxy computed on the fly.
  class tgba_tba_proxy_succ_iterator: public tgba_succ_iterator
  {
  public:
    tgba_tba_proxy_succ_iterator(tgba_succ_iterator* it,
				 bdd acc, bdd next_acc,
				 bdd the_acceptance_cond)
      : it_(it), acc_(acc), next_acc_(next_acc),
	the_acceptance_cond_(the_acceptance_cond)
    {
    }

    virtual
    ~tgba_tba_proxy_succ_iterator()
    {
      delete it_;
    }

    // iteration

    void
    first()
    {
      it_->first();
    }

    void
    next()
    {
      it_->next();
    }

    bool
    done() const
    {
      return it_->done();
    }

    // inspection

    state_tba_proxy*
    current_state() const
    {
      state* s = it_->current_state();
      bdd acc;
      // Transition in the ACC_ acceptance set should be directed
      // to the NEXT_ACC_ acceptance set.
      if (acc_ == bddtrue
	  || (acc_ & it_->current_acceptance_conditions()) == acc_)
	acc = next_acc_;
      else
	acc = acc_;
      return new state_tba_proxy(s, acc);
    }

    bdd
    current_condition() const
    {
      return it_->current_condition();
    }

    bdd
    current_acceptance_conditions() const
    {
      return the_acceptance_cond_;
    }

  protected:
    tgba_succ_iterator* it_;
    bdd acc_;
    bdd next_acc_;
    bdd the_acceptance_cond_;
  };


  tgba_tba_proxy::tgba_tba_proxy(const tgba* a)
    : a_(a)
  {
    bdd all = a_->all_acceptance_conditions();

    // We will use one acceptance condition for this automata.
    // Let's call it Acc[True].
    int v = get_dict()
      ->register_acceptance_variable(ltl::constant::true_instance(), this);
    the_acceptance_cond_ = bdd_ithvar(v);

    // Now build the "cycle" of acceptance conditions.

    bdd last = bdd_satone(all);
    all -= last;

    acc_cycle_[bddtrue] = last;

    while (all != bddfalse)
      {
	bdd next = bdd_satone(all);
	all -= next;
	acc_cycle_[last] = next;
	last = next;
      }

    acc_cycle_[last] = bddtrue;
  }

  tgba_tba_proxy::~tgba_tba_proxy()
  {
    get_dict()->unregister_all_my_variables(this);
  }

  state*
  tgba_tba_proxy::get_init_state() const
  {
    cycle_map::const_iterator i = acc_cycle_.find(bddtrue);
    assert(i != acc_cycle_.end());
    return new state_tba_proxy(a_->get_init_state(), i->second);
  }

  tgba_succ_iterator*
  tgba_tba_proxy::succ_iter(const state* local_state,
			    const state* global_state,
			    const tgba* global_automaton) const
  {
    const state_tba_proxy* s =
      dynamic_cast<const state_tba_proxy*>(local_state);
    assert(s);

    tgba_succ_iterator* it = a_->succ_iter(s->real_state(),
					   global_state, global_automaton);
    bdd acc = s->acceptance_cond();
    cycle_map::const_iterator i = acc_cycle_.find(acc);
    assert(i != acc_cycle_.end());
    return
      new tgba_tba_proxy_succ_iterator(it, acc, i->second,
				       (acc == bddtrue)
				       ? the_acceptance_cond_ : bddfalse);
  }

  bdd_dict*
  tgba_tba_proxy::get_dict() const
  {
    return a_->get_dict();
  }

  std::string
  tgba_tba_proxy::format_state(const state* state) const
  {
    const state_tba_proxy* s = dynamic_cast<const state_tba_proxy*>(state);
    assert(s);
    return a_->format_state(s->real_state()) + "("
      + bdd_format_set(get_dict(), s->acceptance_cond()) + ")";
  }

  state*
  tgba_tba_proxy::project_state(const state* s, const tgba* t) const
  {
    const state_tba_proxy* s2 = dynamic_cast<const state_tba_proxy*>(s);
    assert(s2);
    if (t == this)
      return s2->clone();
    return a_->project_state(s2->real_state(), t);
  }


  bdd
  tgba_tba_proxy::all_acceptance_conditions() const
  {
    return the_acceptance_cond_;
  }

  bdd
  tgba_tba_proxy::neg_acceptance_conditions() const
  {
    return !the_acceptance_cond_;
  }

  bool
  tgba_tba_proxy::state_is_accepting(const state* state) const
  {
    const state_tba_proxy* s =
      dynamic_cast<const state_tba_proxy*>(state);
    assert(s);
    return bddtrue == s->acceptance_cond();
  }

  bdd
  tgba_tba_proxy::compute_support_conditions(const state* state) const
  {
    const state_tba_proxy* s =
      dynamic_cast<const state_tba_proxy*>(state);
    assert(s);
    return a_->support_conditions(s->real_state());
  }

  bdd
  tgba_tba_proxy::compute_support_variables(const state* state) const
  {
    const state_tba_proxy* s =
      dynamic_cast<const state_tba_proxy*>(state);
    assert(s);
    return a_->support_variables(s->real_state());
  }

}
