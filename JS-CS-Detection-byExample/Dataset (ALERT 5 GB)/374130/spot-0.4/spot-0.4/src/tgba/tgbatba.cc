// Copyright (C) 2003, 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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
#include "misc/hashfunc.hh"

namespace spot
{
  namespace
  {
    /// \brief A state for spot::tgba_tba_proxy.
    ///
    /// This state is in fact a pair of states: the state from the tgba
    /// automaton, and a state of the "counter" (we use a pointer
    /// to the position in the cycle_acc_ list).
    class state_tba_proxy: public state
    {
      typedef tgba_tba_proxy::cycle_list::const_iterator iterator;
    public:
      state_tba_proxy(state* s, iterator acc)
	:	s_(s), acc_(acc)
      {
      }

      /// Copy constructor
      state_tba_proxy(const state_tba_proxy& o)
	: state(),
	  s_(o.real_state()->clone()),
	  acc_(o.acceptance_iterator())
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
	return *acc_;
      }

      iterator
      acceptance_iterator() const
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
	return acc_->id() - o->acceptance_cond().id();
      }

      virtual size_t
      hash() const
      {
	return wang32_hash(s_->hash()) ^ wang32_hash(acc_->id());
      }

      virtual
      state_tba_proxy* clone() const
      {
	return new state_tba_proxy(*this);
      }

    private:
      state* s_;
      iterator acc_;
    };


    /// \brief Iterate over the successors of tgba_tba_proxy computed
    /// on the fly.
    class tgba_tba_proxy_succ_iterator: public tgba_succ_iterator
    {
      typedef tgba_tba_proxy::cycle_list list;
      typedef tgba_tba_proxy::cycle_list::const_iterator iterator;
    public:
      tgba_tba_proxy_succ_iterator(tgba_succ_iterator* it,
				   iterator expected,
				   const list& cycle,
				   bdd the_acceptance_cond)
	: it_(it), expected_(expected), cycle_(cycle),
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
	sync_();
      }

      void
      next()
      {
	it_->next();
	sync_();
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
	return new state_tba_proxy(it_->current_state(), next_);
      }

      bdd
      current_condition() const
      {
	return it_->current_condition();
      }

      bdd
      current_acceptance_conditions() const
      {
	return accepting_ ? the_acceptance_cond_ : bddfalse;
      }

    protected:

      void
      sync_()
      {
	if (done())
	  return;

	bdd acc = it_->current_acceptance_conditions();

	// bddtrue is a special condition used for tgba_sba_proxy
	// to denote the (N+1)th copy of the state, after all acceptance
	// conditions have been traversed.  Such state is always accepting,
	// so do not check acc for this.
	// bddtrue is also used by tgba_tba_proxy if the automata do not
	// use acceptance conditions.  In that cases, all state are accepting.
	if (*expected_ != bddtrue)
	  {
	    // A transition in the *EXPECTED acceptance set should be
	    // directed to the next acceptance set.  If the current
	    // transition is also in the next acceptance set, then go
	    // the one after, etc.
	    //
	    // See Denis Oddoux's PhD thesis for a nice explanation (in French).
	    // @PhDThesis{	  oddoux.03.phd,
	    //   author	= {Denis Oddoux},
	    //   title	= {Utilisation des automates alternants pour un
	    // 		  model-checking efficace des logiques temporelles
	    // 		  lin{\'e}aires.},
	    //   school	= {Universit{\'e}e Paris 7},
	    //   year	= {2003},
	    //   address= {Paris, France},
	    //   month	= {December}
	    // }

	    next_ = expected_;
	    while (next_ != cycle_.end() && (acc & *next_) == *next_)
	      ++next_;
	    if (next_ != cycle_.end())
	      {
		accepting_ = false;
		return;
	      }
	  }
	// The transition is accepting.
	accepting_ = true;
	// Skip as much acceptance conditions as we can on our cycle.
	next_ = cycle_.begin();
	while (next_ != expected_ && (acc & *next_) == *next_)
	  ++next_;
      }

      tgba_succ_iterator* it_;
      const iterator expected_;
      iterator next_;
      bool accepting_;
      const list& cycle_;
      const bdd the_acceptance_cond_;
      friend class ::spot::tgba_tba_proxy;
    };

  } // anonymous

  tgba_tba_proxy::tgba_tba_proxy(const tgba* a)
    : a_(a)
  {
    // We will use one acceptance condition for this automata.
    // Let's call it Acc[True].
    int v = get_dict()
      ->register_acceptance_variable(ltl::constant::true_instance(), this);
    the_acceptance_cond_ = bdd_ithvar(v);

    if (a->number_of_acceptance_conditions() == 0)
      {
	acc_cycle_.push_front(bddtrue);
      }
    else
      {
	// Build a cycle of expected acceptance conditions.
	bdd all = a_->all_acceptance_conditions();
	while (all != bddfalse)
	  {
	    bdd next = bdd_satone(all);
	    all -= next;
	    acc_cycle_.push_front(next);
	  }
      }
  }

  tgba_tba_proxy::~tgba_tba_proxy()
  {
    get_dict()->unregister_all_my_variables(this);
  }

  state*
  tgba_tba_proxy::get_init_state() const
  {
    return new state_tba_proxy(a_->get_init_state(), acc_cycle_.begin());
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

    return new tgba_tba_proxy_succ_iterator(it, s->acceptance_iterator(),
					    acc_cycle_, the_acceptance_cond_);
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
    std::string a = bdd_format_accset(get_dict(), s->acceptance_cond());
    if (a != "")
      a = " " + a;
    return a_->format_state(s->real_state()) + a;
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

  std::string
  tgba_tba_proxy::transition_annotation(const tgba_succ_iterator* t) const
  {
    const tgba_tba_proxy_succ_iterator* i =
      dynamic_cast<const tgba_tba_proxy_succ_iterator*>(t);
    assert(i);
    return a_->transition_annotation(i->it_);
  }

  ////////////////////////////////////////////////////////////////////////
  // tgba_sba_proxy

  tgba_sba_proxy::tgba_sba_proxy(const tgba* a)
    : tgba_tba_proxy(a)
  {
    if (a->number_of_acceptance_conditions() > 0)
      acc_cycle_.push_back(bddtrue);
  }

  bool
  tgba_sba_proxy::state_is_accepting(const state* state) const
  {
    const state_tba_proxy* s =
      dynamic_cast<const state_tba_proxy*>(state);
    assert(s);
    return bddtrue == s->acceptance_cond();
  }

}
