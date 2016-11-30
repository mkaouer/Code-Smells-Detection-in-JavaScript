// Copyright (C) 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include "syntimpl.hh"
#include "ltlast/allnodes.hh"
#include <cassert>

#include "lunabbrev.hh"
#include "simpfg.hh"
#include "nenoform.hh"
#include "ltlvisit/destroy.hh"

namespace spot
{
  namespace ltl
  {
    namespace
    {

      class eventual_universal_visitor: public const_visitor
      {
      public:

	eventual_universal_visitor()
	  : eventual(false), universal(false)
	{
	}

	virtual
	~eventual_universal_visitor()
	{
	}

	bool
	is_eventual() const
	{
	  return eventual;
	}

	bool
	is_universal() const
	{
	  return universal;
	}

	void
	visit(const atomic_prop*)
	{
	}

	void
	visit(const constant*)
	{
	}

	void
	visit(const unop* uo)
	{
	  const formula* f1 = uo->child();
	  if (uo->op() == unop::F)
	    {
	      eventual = true;
	      universal = recurse_un(f1);
	      return;
	    }
	  if (uo->op() == unop::G)
	    {
	      universal = true;
	      eventual = recurse_ev(f1);
	    }
	}

	void
	visit(const binop* bo)
	{
	  const formula* f1 = bo->first();
	  const formula* f2 = bo->second();
	  switch (bo->op())
	    {
	    case binop::Xor:
	    case binop::Equiv:
	    case binop::Implies:
	      universal = recurse_un(f1) && recurse_un(f2);
	      eventual = recurse_ev(f1) && recurse_ev(f2);
	      return;
	    case binop::U:
	      universal = recurse_un(f1) && recurse_un(f2);
	      if ((f1 == constant::true_instance())
		  // Both operand must be purely eventual, unlike in
		  // the proceedings of Concur'00.  (The revision of
		  // the paper available at
		  // http://www1.bell-labs.com/project/TMP/ is fixed.)
		  || (recurse_ev(f1) && recurse_ev(f2)))
		eventual = true;
	      return;
	    case binop::R:
	      eventual = recurse_ev(f1) && recurse_ev(f2);
	      if ((f1 == constant::false_instance())
		  || (recurse_un(f1) && recurse_un(f2)))
		universal = true;
	      return;
	    }
	  /* Unreachable code.  */
	  assert(0);
	}

	void
	visit(const multop* mo)
	{
	  unsigned mos = mo->size();

	  eventual = true;
	  universal = true;
	  for (unsigned i = 0; i < mos; ++i)
	    if (!recurse_ev(mo->nth(i)))
	      {
		eventual = false;
		break;
	      }
	  for (unsigned i = 0; i < mos; ++i)
	    if (!recurse_un(mo->nth(i)))
	      {
		universal = false;
		break;
	      }
	}

	bool
	recurse_ev(const formula* f)
	{
	  eventual_universal_visitor v;
	  const_cast<formula*>(f)->accept(v);
	  return v.is_eventual();
	}

	bool
	recurse_un(const formula* f)
	{
	  eventual_universal_visitor v;
	  const_cast<formula*>(f)->accept(v);
	  return v.is_universal();
	}

      protected:
	bool eventual;
	bool universal;
      };


      /////////////////////////////////////////////////////////////////////////


      class inf_right_recurse_visitor: public const_visitor
      {
      public:

	inf_right_recurse_visitor(const formula *f)
	  : result_(false), f(f)
	{
	}

	virtual
	~inf_right_recurse_visitor()
	{
	}

	int
	result() const
	{
	  return result_;
	}

	void
	visit(const atomic_prop* ap)
	{
	  if (dynamic_cast<const atomic_prop*>(f) == ap)
	    result_ = true;
	}

	void
	visit(const constant* c)
	{
	  switch (c->val())
	    {
	    case constant::True:
	      result_ = true;
	      return;
	    case constant::False:
	      result_ = false;
	      return;
	    }
	}

	void
	visit(const unop* uo)
	{
	  const formula* f1 = uo->child();
	  switch (uo->op())
	    {
	    case unop::Not:
	      if (uo == f)
		result_ = true;
	      return;
	    case unop::X:
	      {
		const unop* op = dynamic_cast<const unop*>(f);
		if (op && op->op() == unop::X)
		  result_ = syntactic_implication(op->child(), f1);
	      }
	      return;
	    case unop::F:
	      /* F(a) = true U a */
	      result_ = syntactic_implication(f, f1);
	      return;
	    case unop::G:
	      /* G(a) = false R a */
	      if (syntactic_implication(f, constant::false_instance()))
		result_ = true;
	      return;
	    }
	  /* Unreachable code.  */
	  assert(0);
	}

	void
	visit(const binop* bo)
	{
	  const formula* f1 = bo->first();
	  const formula* f2 = bo->second();
	  const binop* fb = dynamic_cast<const binop*>(f);
	  const unop* fu = dynamic_cast<const unop*>(f);
	  switch (bo->op())
	    {
	    case binop::Xor:
	    case binop::Equiv:
	    case binop::Implies:
	      return;
	    case binop::U:
	      if (syntactic_implication(f, f2))
		result_ = true;
	      return;
	    case binop::R:
	      if (fb && fb->op() == binop::R)
		if (syntactic_implication(fb->first(), f1) &&
		    syntactic_implication(fb->second(), f2))
		  {
		    result_ = true;
		    return;
		  }
	      if (fu && fu->op() == unop::G)
		if (f1 == constant::false_instance() &&
		    syntactic_implication(fu->child(), f2))
		  {
		    result_ = true;
		    return;
		  }
	      if (syntactic_implication(f, f1)
		  && syntactic_implication(f, f2))
		result_ = true;
	      return;
	    }
	  /* Unreachable code.  */
	  assert(0);
	}

	void
	visit(const multop* mo)
	{
	  multop::type op = mo->op();
	  unsigned mos = mo->size();
	  switch (op)
	    {
	    case multop::And:
	      for (unsigned i = 0; i < mos; ++i)
		if (!syntactic_implication(f, mo->nth(i)))
		  return;
	      result_ = true;
	      break;
	    case multop::Or:
	      for (unsigned i = 0; i < mos && !result_; ++i)
		if (syntactic_implication(f, mo->nth(i)))
		  result_ = true;
	      break;
	    }
	}

	bool
	recurse(const formula* f1, const formula* f2)
	{
	  if (f1 == f2)
	    return true;
	  inf_right_recurse_visitor v(f2);
	  const_cast<formula*>(f1)->accept(v);
	  return v.result();
	}

      protected:
	bool result_; /* true if f < f1, false otherwise. */
	const formula* f;
      };

      /////////////////////////////////////////////////////////////////////////

      class inf_left_recurse_visitor: public const_visitor
      {
      public:

	inf_left_recurse_visitor(const formula *f)
	  : result_(false), f(f)
	{
	}

	virtual
	~inf_left_recurse_visitor()
	{
	}

	bool
	special_case(const formula* f2)
	{
	  const binop* fb = dynamic_cast<const binop*>(f);
	  const binop* f2b = dynamic_cast<const binop*>(f2);
	  if (fb && f2b && fb->op() == f2b->op()
	      && syntactic_implication(f2b->first(), fb->first())
	      && syntactic_implication(f2b->second(), fb->second()))
	    return true;
	  return false;
	}

	int
	result() const
	{
	  return result_;
	}

	void
	visit(const atomic_prop* ap)
	{
	  inf_right_recurse_visitor v(ap);
	  const_cast<formula*>(f)->accept(v);
	  result_ = v.result();
	}

	void
	visit(const constant* c)
	{
	  inf_right_recurse_visitor v(c);
	  switch (c->val())
	    {
	    case constant::True:
	      const_cast<formula*>(f)->accept(v);
	      result_ = v.result();
	      return;
	    case constant::False:
	      result_ = true;
	      return;
	    }
	  /* Unreachable code.  */
	  assert(0);
	}

	void
	visit(const unop* uo)
	{
	  const formula* f1 = uo->child();
	  inf_right_recurse_visitor v(uo);
	  switch (uo->op())
	    {
	    case unop::Not:
	      if (uo == f)
		result_ = true;
	      return;
	    case unop::X:
	      {
		const unop* op = dynamic_cast<const unop*>(f);
		if (op && op->op() == unop::X)
		  result_ = syntactic_implication(f1, op->child());
	      }
	      return;
	    case unop::F:
	      {
		/* F(a) = true U a */
		const formula* tmp = binop::instance(binop::U,
						     constant::true_instance(),
						     clone(f1));
		if (special_case(tmp))
		  {
		    result_ = true;
		    destroy(tmp);
		    return;
		  }
		if (syntactic_implication(tmp, f))
		  result_ = true;
		destroy(tmp);
		return;
	      }
	    case unop::G:
	      {
		/* G(a) = false R a */
		const formula* tmp = binop::instance(binop::R,
						     constant::false_instance(),
						     clone(f1));
		if (special_case(tmp))
		  {
		    result_ = true;
		    destroy(tmp);
		    return;
		  }
		if (syntactic_implication(tmp, f))
		  result_ = true;
		destroy(tmp);
		return;
	      }
	    }
	  /* Unreachable code.  */
	  assert(0);
	}

	void
	visit(const binop* bo)
	{
	  if (special_case(bo))
	    {
	      result_ = true;
	      return;
	    }

	  const formula* f1 = bo->first();
	  const formula* f2 = bo->second();
	  const binop* fb = dynamic_cast<const binop*>(f);
	  const unop* fu = dynamic_cast<const unop*>(f);
	  switch (bo->op())
	    {
	    case binop::Xor:
	    case binop::Equiv:
	    case binop::Implies:
	      return;
	    case binop::U:
	      /* (a < c) && (c < d) => a U b < c U d */
	      if (fb && fb->op() == binop::U)
		if (syntactic_implication(f1, fb->first()) &&
		    syntactic_implication(f2, fb->second()))
		  {
		    result_ = true;
		    return;
		  }
	      if (fu && fu->op() == unop::F)
		if (f1 == constant::true_instance() &&
		    syntactic_implication(f2, fu->child()))
		  {
		    result_ = true;
		    return;
		  }
	      if (syntactic_implication(f1, f)
		  && syntactic_implication(f2, f))
		result_ = true;
	      return;
	    case binop::R:
	      if (fu && fu->op() == unop::G)
		if (f1 == constant::false_instance() &&
		    syntactic_implication(f2, fu->child()))
		  {
		    result_ = true;
		    return;
		  }
	      if (syntactic_implication(f2, f))
		result_ = true;
	      return;
	    }
	  /* Unreachable code.  */
	  assert(0);
	}

	void
	visit(const multop* mo)
	{
	  multop::type op = mo->op();
	  unsigned mos = mo->size();
	  switch (op)
	    {
	    case multop::And:
	      for (unsigned i = 0; (i < mos) && !result_; ++i)
		if (syntactic_implication(mo->nth(i), f))
		  result_ = true;
	      break;
	    case multop::Or:
	      for (unsigned i = 0; i < mos; ++i)
		if (!syntactic_implication(mo->nth(i), f))
		  return;
	      result_ = true;
	      break;
	    }
	}

      protected:
	bool result_; /* true if f1 < f, 1 otherwise. */
	const formula* f;
      };

    } // anonymous

    bool
    is_eventual(const formula* f)
    {
      eventual_universal_visitor v;
      const_cast<formula*>(f)->accept(v);
      return v.is_eventual();
    }

    bool
    is_universal(const formula* f)
    {
      eventual_universal_visitor v;
      const_cast<formula*>(f)->accept(v);
      return v.is_universal();
    }

    // This is called by syntactic_implication() after the
    // formulae have been normalized.
    bool
    syntactic_implication(const formula* f1, const formula* f2)
    {
      if (f1 == f2)
	return true;
      inf_left_recurse_visitor v1(f2);
      inf_right_recurse_visitor v2(f1);

      if (f2 == constant::true_instance()
	  || f1 == constant::false_instance())
	return true;

      const_cast<formula*>(f1)->accept(v1);
      if (v1.result())
	return true;

      const_cast<formula*>(f2)->accept(v2);
      if (v2.result())
	return true;

      return false;
    }

    bool
    syntactic_implication_neg(const formula* f1, const formula* f2, bool right)
    {
      formula* l = clone(f1);
      formula* r = clone(f2);
      if (right)
	r = unop::instance(unop::Not, r);
      else
	l = unop::instance(unop::Not, l);

      formula* tmp = unabbreviate_logic(l);
      destroy(l);
      l = simplify_f_g(tmp);
      destroy(tmp);
      tmp = negative_normal_form(l);
      destroy(l);
      l = tmp;

      tmp = unabbreviate_logic(r);
      destroy(r);
      r = simplify_f_g(tmp);
      destroy(tmp);
      tmp = negative_normal_form(r);
      destroy(r);
      r = tmp;

      bool result = syntactic_implication(l, r);
      destroy(l);
      destroy(r);
      return result;
    }
  }
}
