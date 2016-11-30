// Copyright (C) 2011 Laboratoire de Recherche et Développement de
// l'Epita.
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

#include "wdbacomp.hh"
#include "ltlast/constant.hh"

namespace spot
{
  namespace
  {
    class state_wdba_comp_proxy : public state
    {
    public:
      state_wdba_comp_proxy(state* s) : s_(s)
      {
      }

      /// Copy constructor
      state_wdba_comp_proxy(const state_wdba_comp_proxy& o)
	: state(),
	  s_(o.real_state())
      {
	if (s_)
	  s_ = s_->clone();
      }

      virtual
      ~state_wdba_comp_proxy()
      {
	if (s_)
	  s_->destroy();
      }

      state*
      real_state() const
      {
	return s_;
      }

      virtual int
      compare(const state* other) const
      {
	const state_wdba_comp_proxy* o =
	  down_cast<const state_wdba_comp_proxy*>(other);
	assert(o);
	const state* oo = o->real_state();
	if (s_ == 0)
	  return oo ? 1 : 0;
	if (oo == 0)
	  return -1;

	return s_->compare(oo);
      }

      virtual size_t
      hash() const
      {
	if (s_)
	  return s_->hash();
	return 0;
      }

      virtual
      state_wdba_comp_proxy* clone() const
      {
	return new state_wdba_comp_proxy(*this);
      }

    private:
      state* s_; // 0 if sink-state.
    };

    class tgba_wdba_comp_proxy_succ_iterator: public tgba_succ_iterator
    {
    public:
      tgba_wdba_comp_proxy_succ_iterator(tgba_succ_iterator* it,
					 bdd the_acceptance_cond)
	: it_(it), the_acceptance_cond_(the_acceptance_cond), left_(bddtrue)
      {
      }

      virtual
      ~tgba_wdba_comp_proxy_succ_iterator()
      {
	delete it_;
      }

      // iteration

      void
      first()
      {
	if (it_)
	  it_->first();
	left_ = bddtrue;
      }

      void
      next()
      {
	left_ -= current_condition();
	if (it_)
	  it_->next();
      }

      bool
      done() const
      {
	return left_ == bddfalse;
      }

      // inspection

      state_wdba_comp_proxy*
      current_state() const
      {
	if (!it_ || it_->done())
	  return new state_wdba_comp_proxy(0);
	return new state_wdba_comp_proxy(it_->current_state());
      }

      bdd
      current_condition() const
      {
	if (!it_ || it_->done())
	  return left_;
	return it_->current_condition();
      }

      bdd
      current_acceptance_conditions() const
      {
	if (!it_ || it_->done())
	  // The sink state is accepting in the negated automaton.
	  return the_acceptance_cond_;

	if (it_->current_acceptance_conditions() == bddfalse)
	  return the_acceptance_cond_;
	else
	  return bddfalse;
      }

    protected:
      tgba_succ_iterator* it_;
      const bdd the_acceptance_cond_;
      bdd left_;
    };

    class tgba_wdba_comp_proxy: public tgba
    {
    public:
      tgba_wdba_comp_proxy(const tgba* a)
	: a_(a)
      {
	the_acceptance_cond_ = a->all_acceptance_conditions();
	if (the_acceptance_cond_ == bddfalse)
	  {
	    int v = get_dict()
	      ->register_acceptance_variable(ltl::constant::true_instance(),
					     this);
	    the_acceptance_cond_ = bdd_ithvar(v);
	  }
      }

      virtual ~tgba_wdba_comp_proxy()
      {
	get_dict()->unregister_all_my_variables(this);
      }

      virtual state* get_init_state() const
      {
	return new state_wdba_comp_proxy(a_->get_init_state());
      }

      virtual tgba_succ_iterator*
      succ_iter(const state* local_state,
		const state* global_state = 0,
		const tgba* global_automaton = 0) const
      {
	const state_wdba_comp_proxy* s =
	  down_cast<const state_wdba_comp_proxy*>(local_state);
	assert(s);

	const state* o = s->real_state();
	tgba_succ_iterator* it = 0;
	if (o)
	   it = a_->succ_iter(s->real_state(), global_state, global_automaton);
	return new tgba_wdba_comp_proxy_succ_iterator(it,
						      the_acceptance_cond_);
      }

      virtual bdd_dict*
      get_dict() const
      {
	return a_->get_dict();
      }

      virtual std::string
      format_state(const state* ostate) const
      {
	const state_wdba_comp_proxy* s =
	  down_cast<const state_wdba_comp_proxy*>(ostate);
	assert(s);
	const state* rs = s->real_state();
	if (rs)
	  return a_->format_state(s->real_state());
	else
	  return "(*)"; // sink state
      }

      virtual state*
      project_state(const state* s, const tgba* t) const
      {
	const state_wdba_comp_proxy* s2 =
	  down_cast<const state_wdba_comp_proxy*>(s);
	assert(s2);
	if (t == this)
	  return s2->clone();
	return a_->project_state(s2->real_state(), t);
      }

      virtual bdd
      all_acceptance_conditions() const
      {
	return the_acceptance_cond_;
      }

      virtual bdd
      neg_acceptance_conditions() const
      {
	return !the_acceptance_cond_;
      }

  protected:
    virtual bdd
    compute_support_conditions(const state* ostate) const
      {
	const state_wdba_comp_proxy* s =
	  down_cast<const state_wdba_comp_proxy*>(ostate);
	assert(s);
	const state* rs = s->real_state();
	if (rs)
	  return a_->support_conditions(rs);
	else
	  return bddtrue;
      }

      virtual bdd compute_support_variables(const state* ostate) const
      {
	const state_wdba_comp_proxy* s =
	  down_cast<const state_wdba_comp_proxy*>(ostate);
	assert(s);
	const state* rs = s->real_state();
	if (rs)
	  return a_->support_variables(rs);
	else
	  return bddtrue;
      }

      const tgba* a_;
    private:
      bdd the_acceptance_cond_;

      // Disallow copy.
      tgba_wdba_comp_proxy(const tgba_wdba_comp_proxy&);
      tgba_wdba_comp_proxy& operator=(const tgba_wdba_comp_proxy&);
    };

  }

  tgba*
  wdba_complement(const tgba* aut)
  {
    return new tgba_wdba_comp_proxy(aut);
  }
}
