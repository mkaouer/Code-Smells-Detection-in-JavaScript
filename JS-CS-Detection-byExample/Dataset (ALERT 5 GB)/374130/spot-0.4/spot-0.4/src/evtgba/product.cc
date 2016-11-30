// Copyright (C) 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include "product.hh"
#include "misc/hashfunc.hh"
#include "misc/modgray.hh"
#include <cstdlib>
#include <set>

namespace spot
{
  namespace
  {
    class evtgba_product_state: public state
    {
    public:
      evtgba_product_state(state const* const* s, int n)
	: s_(s), n_(n)
      {
      }

      ~evtgba_product_state()
      {
	for (int j = 0; j < n_; ++j)
	  delete s_[j];
	delete[] s_;
      }

      state const*
      nth(int n) const
      {
	assert(n < n_);
	return s_[n];
      }

      state const* const*
      all() const
      {
	return s_;
      }

      int
      compare(const state* other) const
      {
	const evtgba_product_state* s =
	  dynamic_cast<const evtgba_product_state*>(other);
	assert(s);
	assert(s->n_ == n_);
	for (int i = 0; i < n_; ++i)
	  {
	    int res = s_[i]->compare(s->nth(i));
	    if (res)
	      return res;
	  }
	return 0;
      }

      size_t
      hash() const
      {
	size_t res = 0;
	for (int i = 0; i != n_; ++i)
	  res ^= wang32_hash(s_[i]->hash());
	return res;
      }

      evtgba_product_state*
      clone() const
      {
	state const** s = new state const*[n_];
	memcpy(s, s_, n_ * sizeof(*s_));
	return new evtgba_product_state(s, n_);
      }


    private:
      state const* const* s_;
      int n_;
    };


    class evtgba_product_init_iterator:
      public evtgba_iterator, private loopless_modular_mixed_radix_gray_code
    {
    public:
      evtgba_product_init_iterator(evtgba_iterator* const* op, int n)
	: loopless_modular_mixed_radix_gray_code(n), op_(op), n_(n), done_(0)
      {
	for (int j = 0; j < n_; ++j)
	  {
	    op_[j]->first();
	    if (op_[j]->done())
	      ++done_;
	  }
      }

      ~evtgba_product_init_iterator()
      {
	for (int j = 0; j < n_; ++j)
	  delete op_[j];
	delete[] op_;
      }

      virtual void
      first()
      {
	loopless_modular_mixed_radix_gray_code::first();
	step_();
      }

      virtual void
      next()
      {
	loopless_modular_mixed_radix_gray_code::next();
	step_();
      }

      virtual bool
      done() const
      {
	return loopless_modular_mixed_radix_gray_code::done();
      }

      virtual const state*
      current_state() const
      {
	state const** s = new state const*[n_];
	for (int j = 0; j < n_; ++j)
	  s[j] = op_[j]->current_state();
	return new evtgba_product_state(s, n_);
      }

      virtual const symbol*
      current_label() const
      {
	assert(0);
	return 0;
      }

      virtual symbol_set
      current_acceptance_conditions() const
      {
	assert(0);
	symbol_set s;
	return s;
      }

    private:

      void
      pre_update(int j)
      {
	if (op_[j]->done())
	  --done_;
      }

      void
      post_update(int j)
      {
	if (op_[j]->done())
	  ++done_;
      }

      virtual void
      a_first(int j)
      {
	pre_update(j);
	op_[j]->first();
	post_update(j);
      }

      virtual void
      a_next(int j)
      {
	pre_update(j);
	op_[j]->next();
	post_update(j);
      }

      virtual bool
      a_last(int j) const
      {
	return op_[j]->done();
      }

      void
      step_()
      {
	while (done_ && !loopless_modular_mixed_radix_gray_code::done())
	  loopless_modular_mixed_radix_gray_code::next();
      }

      evtgba_iterator* const* op_;
      const int n_;
      int done_; // number of iterator for which done() is true.
    };

    class evtgba_product_iterator:
      public evtgba_iterator, private loopless_modular_mixed_radix_gray_code
    {
    public:
      evtgba_product_iterator(evtgba_iterator* const* op, int n,
			      state const* const* from,
			      const evtgba_product::common_symbol_table& cst)
	: loopless_modular_mixed_radix_gray_code(n), op_(op), n_(n),
	  from_(from), cst_(cst)
      {
	count_pointer_ = new int*[n];
	for (int j = 0; j < n; ++j)
	  count_pointer_[j] = 0;
      }

      virtual
      ~evtgba_product_iterator()
      {
	delete[] count_pointer_;
	for (int j = 0; j < n_; ++j)
	  delete op_[j];
	delete[] op_;
      }

      virtual void
      first()
      {
	loopless_modular_mixed_radix_gray_code::first();
	step_();
      }

      virtual void
      next()
      {
	loopless_modular_mixed_radix_gray_code::next();
	step_();
      }

      virtual bool
      done() const
      {
	return loopless_modular_mixed_radix_gray_code::done();
      }

      virtual const state*
      current_state() const
      {
	state const** s = new state const*[n_];
	for (int j = 0; j < n_; ++j)
	  {
	    if (op_[j]->done())
	      s[j] = from_[j]->clone();
	    else
	      s[j] = op_[j]->current_state();
	  }
	return new evtgba_product_state(s, n_);
      }

      virtual const symbol*
      current_label() const
      {
	return lcm_.begin()->first;
      }

      virtual symbol_set
      current_acceptance_conditions() const
      {
	symbol_set s;
	for (int j = 0; j < n_; ++j)
	  if (!op_[j]->done())
	    {
	      symbol_set t = op_[j]->current_acceptance_conditions();
	      s.insert(t.begin(), t.end());
	    }
	return s;
      }

    private:

      void
      pre_update(int j)
      {
	if (!--*count_pointer_[j])
	  lcm_.erase(op_[j]->current_label());
	count_pointer_[j] = 0;
      }

      void
      post_update(int j)
      {
	if (!op_[j]->done())
	  {
	    count_pointer_[j] = &lcm_[op_[j]->current_label()];
	    ++*count_pointer_[j];
	  }
      }

      virtual void
      a_first(int j)
      {
	if (count_pointer_[j])
	  pre_update(j);
	op_[j]->first();
	post_update(j);
      }

      virtual void
      a_next(int j)
      {
	pre_update(j);
	op_[j]->next();
	post_update(j);
      }

      virtual bool
      a_last(int j) const
      {
	return op_[j]->done();
      }

      void
      step_()
      {
	while (!loopless_modular_mixed_radix_gray_code::done())
	  {
	    if (lcm_.size() == 1)
	      {
		const symbol* l = lcm_.begin()->first;
		const std::set<int>& s = cst_.find(l)->second;
		std::set<int>::const_iterator i;
		for (i = s.begin(); i != s.end(); ++i)
		  if (op_[*i]->done())
		    break;
		if (i == s.end())
		  return;
	      }
	    loopless_modular_mixed_radix_gray_code::next();
	  }
      }

      evtgba_iterator* const* op_;
      int n_;
      state const* const* from_;
      const evtgba_product::common_symbol_table& cst_;
      typedef std::map<const symbol*, int> label_count_map;
      label_count_map lcm_;
      int** count_pointer_;
    };

  } // anonymous


  evtgba_product::evtgba_product(const evtgba_product_operands& op)
    : op_(op)
  {
    int n = 0;
    for (evtgba_product_operands::const_iterator i = op.begin();
	 i != op.end(); ++i, ++n)
      {
	const symbol_set& al = (*i)->alphabet();
	alphabet_.insert(al.begin(), al.end());
	const symbol_set& ac = (*i)->all_acceptance_conditions();
	all_acc_.insert(ac.begin(), ac.end());

	for (symbol_set::const_iterator j = al.begin(); j != al.end(); ++j)
	  common_symbols_[*j].insert(n);
      }
  }

  evtgba_product::~evtgba_product()
  {
  }

  evtgba_iterator*
  evtgba_product::init_iter() const
  {
    int n = op_.size();
    evtgba_iterator** it = new evtgba_iterator *[n];
    for (int i = 0; i < n; ++i)
      it[i] = op_[i]->init_iter();

    return new evtgba_product_init_iterator(it, n);
  }

  evtgba_iterator*
  evtgba_product::succ_iter(const state* st) const
  {
    const evtgba_product_state* s =
      dynamic_cast<const evtgba_product_state*>(st);
    assert(s);

    int n = op_.size();

    evtgba_iterator** it = new evtgba_iterator *[n];
    for (int i = 0; i < n; ++i)
      it[i] = op_[i]->succ_iter(s->nth(i));

    return new evtgba_product_iterator(it, n, s->all(), common_symbols_);
  }

  evtgba_iterator*
  evtgba_product::pred_iter(const state* st) const
  {
    const evtgba_product_state* s =
      dynamic_cast<const evtgba_product_state*>(st);
    assert(s);

    int n = op_.size();

    evtgba_iterator** it = new evtgba_iterator *[n];
    for (int i = 0; i < n; ++i)
      it[i] = op_[i]->pred_iter(s->nth(i));

    return new evtgba_product_iterator(it, n, s->all(), common_symbols_);
  }

  std::string
  evtgba_product::format_state(const state* st) const
  {
    const evtgba_product_state* s =
      dynamic_cast<const evtgba_product_state*>(st);
    int n = op_.size();
    std::string res = "<" + op_[0]->format_state(s->nth(0));

    for (int i = 1; i < n; ++i)
      res = res + ", " + op_[i]->format_state(s->nth(i));

    return res + ">";
  }

  const symbol_set&
  evtgba_product::all_acceptance_conditions() const
  {
    return all_acc_;
  }

  const symbol_set&
  evtgba_product::alphabet() const
  {
    return alphabet_;
  }

}
