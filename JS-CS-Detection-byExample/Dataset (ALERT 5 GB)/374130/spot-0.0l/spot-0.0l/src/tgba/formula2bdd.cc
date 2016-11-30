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
#include "formula2bdd.hh"
#include "ltlast/allnodes.hh"
#include "ltlast/visitor.hh"
#include "misc/minato.hh"
#include "ltlvisit/clone.hh"

namespace spot
{
  using namespace ltl;

  class formula_to_bdd_visitor : public ltl::const_visitor
  {
  public:
    formula_to_bdd_visitor(bdd_dict* d, void* owner)
      : d_(d), owner_(owner)
    {
    }

    virtual
    ~formula_to_bdd_visitor()
    {
    }

    virtual void
    visit(const atomic_prop* node)
    {
      res_ = bdd_ithvar(d_->register_proposition(node, owner_));
    }

    virtual void
    visit(const constant* node)
    {
      switch (node->val())
	{
	case constant::True:
	  res_ = bddtrue;
	  return;
	case constant::False:
	  res_ = bddfalse;
	  return;
	}
      /* Unreachable code.  */
      assert(0);
    }

    virtual void
    visit(const unop* node)
    {
      switch (node->op())
	{
	case unop::F:
	case unop::G:
	case unop::X:
	  assert(!"unsupported operator");
	case unop::Not:
	  {
	    res_ = bdd_not(recurse(node->child()));
	    return;
	  }
	}
      /* Unreachable code.  */
      assert(0);
    }

    virtual void
    visit(const binop* node)
    {
      bdd f1 = recurse(node->first());
      bdd f2 = recurse(node->second());

      switch (node->op())
	{
	case binop::Xor:
	  res_ = bdd_apply(f1, f2, bddop_xor);
	  return;
	case binop::Implies:
	  res_ = bdd_apply(f1, f2, bddop_imp);
	  return;
	case binop::Equiv:
	  res_ = bdd_apply(f1, f2, bddop_biimp);
	  return;
	case binop::U:
	case binop::R:
	  assert(!"unsupported operator");
	}
      /* Unreachable code.  */
      assert(0);
    }

    virtual void
    visit(const multop* node)
    {
      int op = -1;
      switch (node->op())
	{
	case multop::And:
	  op = bddop_and;
	  res_ = bddtrue;
	  break;
	case multop::Or:
	  op = bddop_or;
	  res_ = bddfalse;
	  break;
	}
      assert(op != -1);
      unsigned s = node->size();
      for (unsigned n = 0; n < s; ++n)
	{
	  res_ = bdd_apply(res_, recurse(node->nth(n)), op);
	}
    }

    bdd
    result() const
    {
      return res_;
    }

    bdd
    recurse(const formula* f) const
    {
      return formula_to_bdd(f, d_, owner_);
    }

  private:
    bdd_dict* d_;
    void* owner_;
    bdd res_;
  };

  bdd
  formula_to_bdd(const formula* f, bdd_dict* d, void* for_me)
  {
    formula_to_bdd_visitor v(d, for_me);
    f->accept(v);
    return v.result();
  }

  // Convert a BDD which is known to be a conjonction into a formula.
  static ltl::formula*
  conj_to_formula(bdd b, const bdd_dict* d)
  {
    if (b == bddfalse)
      return constant::false_instance();
    multop::vec* v = new multop::vec;
    while (b != bddtrue)
      {
	int var = bdd_var(b);
	bdd_dict::vf_map::const_iterator isi = d->var_formula_map.find(var);
	assert(isi != d->var_formula_map.end());
	formula* res = clone(isi->second);

	bdd high = bdd_high(b);
	if (high == bddfalse)
	  {
	    res = unop::instance(unop::Not, res);
	    b = bdd_low(b);
	  }
	else
	  {
	    // If bdd_low is not false, then b was not a conjunction.
	    assert(bdd_low(b) == bddfalse);
	    b = high;
	  }
	assert(b != bddfalse);
	v->push_back(res);
      }
    return multop::instance(multop::And, v);
  }

  const formula*
  bdd_to_formula(bdd f, const bdd_dict* d)
  {
    if (f == bddfalse)
      return constant::false_instance();

    multop::vec* v = new multop::vec;

    minato_isop isop(f);
    bdd cube;
    while ((cube = isop.next()) != bddfalse)
      v->push_back(conj_to_formula(cube, d));

    return multop::instance(multop::Or, v);
  }
}
