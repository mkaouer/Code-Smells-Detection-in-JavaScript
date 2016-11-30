// Copyright (C) 2009, 2011 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2006 Laboratoire d'Informatique de
// Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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

#include "tgbaproduct.hh"
#include <string>
#include <cassert>
#include "misc/hashfunc.hh"
#include "kripke/kripke.hh"

namespace spot
{

  ////////////////////////////////////////////////////////////
  // state_product

  state_product::~state_product()
  {
    left_->destroy();
    right_->destroy();
  }

  void
  state_product::destroy() const
  {
    if (--count_)
      return;
    fixed_size_pool* p = pool_;
    this->~state_product();
    p->deallocate(this);
  }

  int
  state_product::compare(const state* other) const
  {
    const state_product* o = down_cast<const state_product*>(other);
    assert(o);
    int res = left_->compare(o->left());
    if (res != 0)
      return res;
    return right_->compare(o->right());
  }

  size_t
  state_product::hash() const
  {
    // We assume that size_t is 32-bit wide.
    return wang32_hash(left_->hash()) ^ wang32_hash(right_->hash());
  }

  state_product*
  state_product::clone() const
  {
    ++count_;
    return const_cast<state_product*>(this);
  }

  ////////////////////////////////////////////////////////////
  // tgba_succ_iterator_product

  namespace
  {

    class tgba_succ_iterator_product_common: public tgba_succ_iterator
    {
    public:
      tgba_succ_iterator_product_common(tgba_succ_iterator* left,
					tgba_succ_iterator* right,
					fixed_size_pool* pool)
	: left_(left), right_(right), pool_(pool)
      {
      }

      virtual ~tgba_succ_iterator_product_common()
      {
	delete left_;
	delete right_;
      }

      virtual void next_non_false_() = 0;

    void first()
      {
	if (!right_)
	  return;

	left_->first();
	right_->first();
	// If one of the two successor sets is empty initially, we
	// reset right_, so that done() can detect this situation
	// easily.  (We choose to reset right_ because this variable
	// is already used by done().)
	if (left_->done() || right_->done())
	  {
	    delete right_;
	    right_ = 0;
	    return;
	  }
	next_non_false_();
      }

      bool done() const
      {
	return !right_ || right_->done();
      }

      state_product* current_state() const
      {
	return new(pool_->allocate()) state_product(left_->current_state(),
						    right_->current_state(),
						    pool_);
      }

    protected:
      tgba_succ_iterator* left_;
      tgba_succ_iterator* right_;
      fixed_size_pool* pool_;
      friend class spot::tgba_product;
    };


    /// \brief Iterate over the successors of a product computed on the fly.
    class tgba_succ_iterator_product: public tgba_succ_iterator_product_common
    {
    public:
      tgba_succ_iterator_product(tgba_succ_iterator* left,
				 tgba_succ_iterator* right,
				 bdd left_neg, bdd right_neg,
				 bddPair* right_common_acc,
				 fixed_size_pool* pool)
	: tgba_succ_iterator_product_common(left, right, pool),
	  left_neg_(left_neg),
	  right_neg_(right_neg),
	  right_common_acc_(right_common_acc)
      {
      }

      virtual ~tgba_succ_iterator_product()
      {
      }

      void step_()
      {
	left_->next();
	if (left_->done())
	  {
	    left_->first();
	    right_->next();
	  }
      }

      void next_non_false_()
      {
	while (!done())
	  {
	    bdd l = left_->current_condition();
	    bdd r = right_->current_condition();
	    bdd current_cond = l & r;

	    if (current_cond != bddfalse)
	      {
		current_cond_ = current_cond;
		return;
	      }
	    step_();
	  }
      }

      void next()
      {
	step_();
	next_non_false_();
      }

      bdd current_condition() const
      {
	return current_cond_;
      }

      bdd current_acceptance_conditions() const
      {
	return ((left_->current_acceptance_conditions() & right_neg_)
		| (bdd_replace(right_->current_acceptance_conditions(),
			       right_common_acc_) & left_neg_));
      }

    protected:
      bdd current_cond_;
      bdd left_neg_;
      bdd right_neg_;
      bddPair* right_common_acc_;
    };

    /// Iterate over the successors of a product computed on the fly.
    /// This one assumes that LEFT is an iterator over a Kripke structure
    class tgba_succ_iterator_product_kripke:
      public tgba_succ_iterator_product_common
    {
    public:
      tgba_succ_iterator_product_kripke(tgba_succ_iterator* left,
					tgba_succ_iterator* right,
					fixed_size_pool* pool)
	: tgba_succ_iterator_product_common(left, right, pool)
      {
      }

      virtual ~tgba_succ_iterator_product_kripke()
      {
      }

      void next_non_false_()
      {
	// All the transitions of left_ iterator have the
	// same label, because it is a Kripke structure.
	bdd l = left_->current_condition();
	while (!right_->done())
	  {
	    bdd r = right_->current_condition();
	    bdd current_cond = l & r;

	    if (current_cond != bddfalse)
	      {
		current_cond_ = current_cond;
		return;
	      }
	    right_->next();
	  }
      }

      void next()
      {
	left_->next();
	if (left_->done())
	  {
	    left_->first();
	    right_->next();
	    next_non_false_();
	  }
      }

      bdd current_condition() const
      {
	return current_cond_;
      }

      bdd current_acceptance_conditions() const
      {
	return right_->current_acceptance_conditions();
      }

    protected:
      bdd current_cond_;
    };

  } // anonymous

  ////////////////////////////////////////////////////////////
  // tgba_product

  tgba_product::tgba_product(const tgba* left, const tgba* right)
    : dict_(left->get_dict()), left_(left), right_(right),
      pool_(sizeof(state_product))
  {
    assert(dict_ == right_->get_dict());

    // If one of the side is a Kripke structure, it is easier to deal
    // with (we don't have to fix the acceptance conditions, and
    // computing the successors can be improved a bit).
    if (dynamic_cast<const kripke*>(left_))
      {
	left_kripke_ = true;
      }
    else if (dynamic_cast<const kripke*>(right_))
      {
	std::swap(left_, right_);
	left_kripke_ = true;
      }
    else
      {
	left_kripke_ = false;
      }

    dict_->register_all_variables_of(&left_, this);
    dict_->register_all_variables_of(&right_, this);

    if (left_kripke_)
      {
	all_acceptance_conditions_ = right_->all_acceptance_conditions();
	neg_acceptance_conditions_ = right_->neg_acceptance_conditions();
	return;
      }

    bdd lna = left_->neg_acceptance_conditions();
    bdd rna = right_->neg_acceptance_conditions();

    right_common_acc_ = bdd_newpair();

    bdd tmp = lna;
    while (tmp != bddtrue)
      {
	assert(bdd_high(tmp) == bddfalse);
	int var = bdd_var(tmp);
	if ((bdd_nithvar(var) & rna) == rna)
	  {
	    int varclone = dict_->register_clone_acc(var, this);
	    bdd_setpair(right_common_acc_, var, varclone);
	  }
	tmp = bdd_low(tmp);
      }

    bdd lac = left_->all_acceptance_conditions();
    bdd rac = right_->all_acceptance_conditions();

    rna = bdd_replace(rna, right_common_acc_);
    rac = bdd_replace(rac, right_common_acc_);

    left_acc_complement_ = lna;
    assert(bdd_exist(lna, rna) == lna);
    right_acc_complement_ = rna;
    assert(bdd_exist(rna, lna) == rna);

    all_acceptance_conditions_ = ((lac & right_acc_complement_)
				  | (rac & left_acc_complement_));
    neg_acceptance_conditions_ = lna & rna;
  }

  tgba_product::~tgba_product()
  {
    if (!left_kripke_)
      bdd_freepair(right_common_acc_);
    dict_->unregister_all_my_variables(this);
    // Prevent these states from being destroyed by ~tgba(): they
    // will be destroyed before when the pool is destructed.
    if (last_support_conditions_input_)
      {
	last_support_conditions_input_->destroy();
	last_support_conditions_input_ = 0;
      }
    if (last_support_variables_input_)
      {
	last_support_variables_input_->destroy();
	last_support_variables_input_ = 0;
      }
  }

  state*
  tgba_product::get_init_state() const
  {
    fixed_size_pool* p = const_cast<fixed_size_pool*>(&pool_);
    return new(p->allocate()) state_product(left_->get_init_state(),
					    right_->get_init_state(), p);
  }

  tgba_succ_iterator*
  tgba_product::succ_iter(const state* local_state,
			  const state* global_state,
			  const tgba* global_automaton) const
  {
    const state_product* s =
      down_cast<const state_product*>(local_state);
    assert(s);

    // If global_automaton is not specified, THIS is the root of a
    // product tree.
    if (!global_automaton)
      {
	global_automaton = this;
	global_state = local_state;
      }

    tgba_succ_iterator* li = left_->succ_iter(s->left(),
					      global_state, global_automaton);
    tgba_succ_iterator* ri = right_->succ_iter(s->right(),
					       global_state, global_automaton);

    fixed_size_pool* p = const_cast<fixed_size_pool*>(&pool_);
    if (left_kripke_)
      return new tgba_succ_iterator_product_kripke(li, ri, p);
    else
      return new tgba_succ_iterator_product(li, ri,
					    left_acc_complement_,
					    right_acc_complement_,
					    right_common_acc_,
					    p);
  }

  bdd
  tgba_product::compute_support_conditions(const state* in) const
  {
    const state_product* s = down_cast<const state_product*>(in);
    assert(s);
    bdd lsc = left_->support_conditions(s->left());
    bdd rsc = right_->support_conditions(s->right());
    return lsc & rsc;
  }

  bdd
  tgba_product::compute_support_variables(const state* in) const
  {
    const state_product* s = down_cast<const state_product*>(in);
    assert(s);
    bdd lsc = left_->support_variables(s->left());
    bdd rsc = right_->support_variables(s->right());
    return lsc & rsc;
  }

  bdd_dict*
  tgba_product::get_dict() const
  {
    return dict_;
  }

  std::string
  tgba_product::format_state(const state* state) const
  {
    const state_product* s = down_cast<const state_product*>(state);
    assert(s);
    return (left_->format_state(s->left())
	    + " * "
	    + right_->format_state(s->right()));
  }

  state*
  tgba_product::project_state(const state* s, const tgba* t) const
  {
    const state_product* s2 = down_cast<const state_product*>(s);
    assert(s2);
    if (t == this)
      return s2->clone();
    state* res = left_->project_state(s2->left(), t);
    if (res)
      return res;
    return right_->project_state(s2->right(), t);
  }

  bdd
  tgba_product::all_acceptance_conditions() const
  {
    return all_acceptance_conditions_;
  }

  bdd
  tgba_product::neg_acceptance_conditions() const
  {
    return neg_acceptance_conditions_;
  }

  std::string
  tgba_product::transition_annotation(const tgba_succ_iterator* t) const
  {
    const tgba_succ_iterator_product* i =
      down_cast<const tgba_succ_iterator_product*>(t);
    assert(i);
    std::string left = left_->transition_annotation(i->left_);
    std::string right = right_->transition_annotation(i->right_);
    if (left == "")
      return right;
    if (right == "")
      return left;
    return "<" + left + ", " + right + ">";
  }

  //////////////////////////////////////////////////////////////////////
  // tgba_product_init

  tgba_product_init::tgba_product_init(const tgba* left, const tgba* right,
				       const state* left_init,
				       const state* right_init)
    : tgba_product(left, right),
      left_init_(left_init), right_init_(right_init)
  {
    if (left_ != left)
      std::swap(left_init_, right_init_);
  }

  state*
  tgba_product_init::get_init_state() const
  {
    fixed_size_pool* p = const_cast<fixed_size_pool*>(&pool_);
    return new(p->allocate()) state_product(left_init_->clone(),
					    right_init_->clone(), p);
  }

}
