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

#include <cassert>
#include "tgbasgba.hh"
#include "bddprint.hh"
#include "ltlast/constant.hh"
#include "misc/hashfunc.hh"

namespace spot
{
  namespace
  {
    /// \brief A state for spot::tgba_sgba_proxy.
    class state_sgba_proxy: public state
    {
    public:
      state_sgba_proxy(state* s, bdd acc)
	:	s_(s), acc_(acc)
      {
      }

      /// Copy constructor
      state_sgba_proxy(const state_sgba_proxy& o)
	: state(),
	  s_(o.real_state()->clone()),
	  acc_(o.acc_)
      {
      }

      virtual
      ~state_sgba_proxy()
      {
	s_->destroy();
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
	const state_sgba_proxy* o =
          down_cast<const state_sgba_proxy*>(other);
	assert(o);
	int res = s_->compare(o->real_state());
	if (res != 0)
	  return res;
	return acc_.id() - o->acc_.id();
      }

      virtual size_t
      hash() const
      {
	return wang32_hash(s_->hash()) ^ wang32_hash(acc_.id());
      }

      virtual
      state_sgba_proxy* clone() const
      {
	return new state_sgba_proxy(*this);
      }

    private:
      state* s_;
      bdd acc_;
    };

    /// \brief Iterate over the successors of tgba_sgba_proxy computed
    /// on the fly.
    class tgba_sgba_proxy_succ_iterator: public tgba_succ_iterator
    {
    public:
      tgba_sgba_proxy_succ_iterator(tgba_succ_iterator* it)
	: it_(it), emulate_acc_cond_(false)
      {
      }

      tgba_sgba_proxy_succ_iterator(tgba_succ_iterator* it, bdd acc)
	: it_(it), emulate_acc_cond_(true)
      {
        acceptance_condition_ = acc;
      }

      virtual
      ~tgba_sgba_proxy_succ_iterator()
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

      state_sgba_proxy*
      current_state() const
      {
	return new state_sgba_proxy(it_->current_state(),
                                    it_->current_acceptance_conditions());
      }

      bdd
      current_condition() const
      {
	return it_->current_condition();
      }

      bdd
      current_acceptance_conditions() const
      {
        if (emulate_acc_cond_)
          return acceptance_condition_;
	return it_->current_acceptance_conditions();
      }

    protected:
      tgba_succ_iterator* it_;
      // If the automaton has no acceptance condition,
      // every state is accepting.
      bool emulate_acc_cond_;
      bdd acceptance_condition_;
    };

  } // anonymous

  tgba_sgba_proxy::tgba_sgba_proxy(const tgba* a, bool no_zero_acc)
    : a_(a), emulate_acc_cond_(false)
  {
    if (no_zero_acc && a_->number_of_acceptance_conditions() == 0)
    {
      emulate_acc_cond_ = true;
      int v = get_dict()
        ->register_acceptance_variable(ltl::constant::true_instance(), this);
      acceptance_condition_ = bdd_ithvar(v);
    }

    get_dict()->register_all_variables_of(a, this);
  }

  tgba_sgba_proxy::~tgba_sgba_proxy()
  {
    get_dict()->unregister_all_my_variables(this);
  }

  state*
  tgba_sgba_proxy::get_init_state() const
  {
    return new state_sgba_proxy(a_->get_init_state(), bddfalse);
  }

  tgba_succ_iterator*
  tgba_sgba_proxy::succ_iter(const state* local_state,
			    const state* global_state,
			    const tgba* global_automaton) const
  {
    const state_sgba_proxy* s =
      down_cast<const state_sgba_proxy*>(local_state);
    assert(s);

    tgba_succ_iterator* it = a_->succ_iter(s->real_state(),
					   global_state, global_automaton);

    return new tgba_sgba_proxy_succ_iterator(it);
  }

  bdd_dict*
  tgba_sgba_proxy::get_dict() const
  {
    return a_->get_dict();
  }

  std::string
  tgba_sgba_proxy::format_state(const state* state) const
  {
    const state_sgba_proxy* s = down_cast<const state_sgba_proxy*>(state);
    assert(s);
    std::string a;
    if (!emulate_acc_cond_)
      a = bdd_format_accset(get_dict(), s->acceptance_cond());
    else
      a = bdd_format_accset(get_dict(), acceptance_condition_);
    if (a != "")
      a = " " + a;
    return a_->format_state(s->real_state()) + a;
  }

  bdd
  tgba_sgba_proxy::all_acceptance_conditions() const
  {
    if (emulate_acc_cond_)
      return acceptance_condition_;
    return a_->all_acceptance_conditions();
  }

  bdd
  tgba_sgba_proxy::neg_acceptance_conditions() const
  {
    if (emulate_acc_cond_)
      return bdd_nithvar(bdd_var(acceptance_condition_));
    return a_->neg_acceptance_conditions();
  }

  bdd
  tgba_sgba_proxy::state_acceptance_conditions(const state* state) const
  {
    const state_sgba_proxy* s =
      down_cast<const state_sgba_proxy*>(state);
    assert(s);
    if (emulate_acc_cond_)
      return acceptance_condition_;
    return s->acceptance_cond();
  }

  bdd
  tgba_sgba_proxy::compute_support_conditions(const state* state) const
  {
    const state_sgba_proxy* s =
      down_cast<const state_sgba_proxy*>(state);
    assert(s);

    if (emulate_acc_cond_)
      return acceptance_condition_;
    return a_->support_conditions(s->real_state());
  }

  bdd
  tgba_sgba_proxy::compute_support_variables(const state* state) const
  {
    const state_sgba_proxy* s =
      down_cast<const state_sgba_proxy*>(state);
    assert(s);

    if (emulate_acc_cond_)
      return bdd_support(acceptance_condition_);
    return a_->support_variables(s->real_state());
  }

}
