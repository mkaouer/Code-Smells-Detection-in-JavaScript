// Copyright (C) 2003, 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include "nenoform.hh"
#include "ltlast/allnodes.hh"
#include <cassert>

namespace spot
{
  namespace ltl
  {
    namespace
    {
      class negative_normal_form_visitor: public visitor
      {
      public:
	negative_normal_form_visitor(bool negated)
	  : negated_(negated)
	{
	}

	virtual
	~negative_normal_form_visitor()
	{
	}

	formula* result() const
	{
	  return result_;
	}

	void
	visit(atomic_prop* ap)
	{
	  formula* f = ap->ref();
	  if (negated_)
	    result_ = unop::instance(unop::Not, f);
	  else
	    result_ = f;
	}

	void
	visit(constant* c)
	{
	  if (!negated_)
	    {
	      result_ = c;
	      return;
	    }

	  switch (c->val())
	    {
	    case constant::True:
	      result_ = constant::false_instance();
	      return;
	    case constant::False:
	      result_ = constant::true_instance();
	      return;
	    }
	  /* Unreachable code.  */
	  assert(0);
	}

	void
	visit(unop* uo)
	{
	  formula* f = uo->child();
	  switch (uo->op())
	    {
	    case unop::Not:
	      result_ = recurse_(f, negated_ ^ true);
	      return;
	    case unop::X:
	      /* !Xa == X!a */
	      result_ = unop::instance(unop::X, recurse(f));
	      return;
	    case unop::F:
	      /* !Fa == G!a */
	      result_ = unop::instance(negated_ ? unop::G : unop::F,
				       recurse(f));
	      return;
	    case unop::G:
	      /* !Ga == F!a */
	      result_ = unop::instance(negated_ ? unop::F : unop::G,
				       recurse(f));
	      return;
	    }
	  /* Unreachable code.  */
	  assert(0);
	}

	void
	visit(binop* bo)
	{
	  formula* f1 = bo->first();
	  formula* f2 = bo->second();
	  switch (bo->op())
	    {
	    case binop::Xor:
	      /* !(a ^ b) == a <=> b */
	      result_ = binop::instance(negated_ ? binop::Equiv : binop::Xor,
					recurse_(f1, false),
					recurse_(f2, false));
	      return;
	    case binop::Equiv:
	      /* !(a <=> b) == a ^ b */
	      result_ = binop::instance(negated_ ? binop::Xor : binop::Equiv,
					recurse_(f1, false),
					recurse_(f2, false));
	      return;
	    case binop::Implies:
	      if (negated_)
		/* !(a => b) == a & !b */
		result_ = multop::instance(multop::And,
					   recurse_(f1, false),
					   recurse_(f2, true));
	      else
		result_ = binop::instance(binop::Implies,
					  recurse(f1), recurse(f2));
	      return;
	    case binop::U:
	      /* !(a U b) == !a R !b */
	      result_ = binop::instance(negated_ ? binop::R : binop::U,
					recurse(f1), recurse(f2));
	      return;
	    case binop::R:
	      /* !(a R b) == !a U !b */
	      result_ = binop::instance(negated_ ? binop::U : binop::R,
					recurse(f1), recurse(f2));
	      return;
	    }
	  /* Unreachable code.  */
	  assert(0);
	}

	void
	visit(multop* mo)
	{
	  /* !(a & b & c) == !a | !b | !c  */
	  /* !(a | b | c) == !a & !b & !c  */
	  multop::type op = mo->op();
	  if (negated_)
	    switch (op)
	      {
	      case multop::And:
		op = multop::Or;
		break;
	      case multop::Or:
		op = multop::And;
		break;
	      }
	  multop::vec* res = new multop::vec;
	  unsigned mos = mo->size();
	  for (unsigned i = 0; i < mos; ++i)
	    res->push_back(recurse(mo->nth(i)));
	  result_ = multop::instance(op, res);
	}

	formula*
	recurse_(formula* f, bool negated)
	{
	  return negative_normal_form(f, negated);
	}

	formula*
	recurse(formula* f)
	{
	  return recurse_(f, negated_);
	}

      protected:
	formula* result_;
	bool negated_;
      };
    }

    formula*
    negative_normal_form(const formula* f, bool negated)
    {
      negative_normal_form_visitor v(negated);
      const_cast<formula*>(f)->accept(v);
      return v.result();
    }

  }
}
