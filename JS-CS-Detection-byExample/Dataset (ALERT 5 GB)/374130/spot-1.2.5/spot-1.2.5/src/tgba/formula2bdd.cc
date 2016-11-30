// Copyright (C) 2009, 2010, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2004 Laboratoire d'Informatique de Paris
// 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
//
// This file is part of Spot, a model checking library.
//
// Spot is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or
// (at your option) any later version.
//
// Spot is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

#include <cassert>
#include "formula2bdd.hh"
#include "ltlast/allnodes.hh"
#include "ltlast/visitor.hh"
#include "misc/minato.hh"

namespace spot
{
  using namespace ltl;

  namespace
  {
    class formula_to_bdd_visitor: public ltl::visitor
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
	  case constant::EmptyWord:
	    assert(!"unsupported operator");
	  }
	/* Unreachable code.  */
	assert(0);
      }

      virtual void
      visit(const bunop*)
      {
	assert(!"unsupported operator");
	return;
      }

      virtual void
      visit(const unop* node)
      {
	switch (node->op())
	  {
	  case unop::Finish:
	  case unop::F:
	  case unop::G:
	  case unop::X:
	  case unop::Closure:
	  case unop::NegClosure:
	  case unop::NegClosureMarked:
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
	  case binop::W:
	  case binop::M:
	  case binop::UConcat:
	  case binop::EConcat:
	  case binop::EConcatMarked:
	    assert(!"unsupported operator");
	  }
	/* Unreachable code.  */
	assert(0);
      }

      virtual void
      visit(const automatop*)
      {
	assert(!"unsupported operator");
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
	  case multop::Concat:
	  case multop::Fusion:
	  case multop::AndNLM:
	  case multop::OrRat:
	  case multop::AndRat:
	    assert(!"unsupported operator");
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

    // Convert a BDD which is known to be a conjonction into a formula.
    static const ltl::formula*
    conj_to_formula(bdd b, const bdd_dict* d)
    {
      if (b == bddfalse)
	return constant::false_instance();
      multop::vec* v = new multop::vec;
      while (b != bddtrue)
	{
	  int var = bdd_var(b);
	  const bdd_dict::bdd_info& i = d->bdd_map[var];
	  assert(i.type == bdd_dict::var);
	  const formula* res = i.f->clone();

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

  } // anonymous

  bdd
  formula_to_bdd(const formula* f, bdd_dict* d, void* for_me)
  {
    formula_to_bdd_visitor v(d, for_me);
    f->accept(v);
    return v.result();
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
