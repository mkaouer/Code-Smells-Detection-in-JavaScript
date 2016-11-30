// Copyright (C) 2008, 2009, 2010, 2011 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2004, 2006, 2007 Laboratoire d'Informatique de
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

#include "reduce.hh"
#include "basicreduce.hh"
#include "syntimpl.hh"
#include "ltlast/allnodes.hh"
#include <cassert>

#include "lunabbrev.hh"
#include "simpfg.hh"
#include "nenoform.hh"
#include "contain.hh"

namespace spot
{
  namespace ltl
  {
    namespace
    {
      typedef union
      {
	unsigned v;
	struct is_struct
	{
	  bool eventual:1;
	  bool universal:1;
	} is;
      } eu_info;

      static unsigned recurse_eu(const formula* f);

      class eventual_universal_visitor: public const_visitor
      {
      public:

	eventual_universal_visitor()
	{
	}

	virtual
	~eventual_universal_visitor()
	{
	}

	bool
	is_eventual() const
	{
	  return ret_.is.eventual;
	}

	bool
	is_universal() const
	{
	  return ret_.is.universal;
	}

	unsigned
	eu() const
	{
	  return ret_.v;
	}

	void
	visit(const atomic_prop*)
	{
	  ret_.v = 0;
	}

	void
	visit(const constant*)
	{
	  ret_.v = 0;
	}

	void
	visit(const unop* uo)
	{
	  const formula* f1 = uo->child();
	  if (uo->op() == unop::F)
	    {
	      ret_.v = recurse_eu(f1);
	      ret_.is.eventual = true;
	      return;
	    }
	  if (uo->op() == unop::G)
	    {
	      ret_.v = recurse_eu(f1);
	      ret_.is.universal = true;
	      return;
	    }
	  ret_.v = 0;
	  return;
	}

	void
	visit(const binop* bo)
	{
	  const formula* f1 = bo->first();
	  const formula* f2 = bo->second();

	  // Beware: (f U g) is purely eventual if both operands
	  // are purely eventual, unlike in the proceedings of
	  // Concur'00.  (The revision of the paper available at
	  // http://www.bell-labs.com/project/TMP/ is fixed.)  See
	  // also http://arxiv.org/abs/1011.4214 for a discussion
	  // about this problem.  (Which we fixed in 2005 thanks
	  // to LBTT.)

	  // This means that we can use the following case to handle
	  // all cases of (f U g), (f R g), (f W g), (f M g) for
	  // universality and eventuality.
	  ret_.v = recurse_eu(f1) & recurse_eu(f2);

	  // we are left with the case where U, R, W, or M are actually
	  // used to represent F or G.
	  switch (bo->op())
	    {
	    case binop::Xor:
	    case binop::Equiv:
	    case binop::Implies:
	      return;
	    case binop::U:
	      if (f1 == constant::true_instance())
		ret_.is.eventual = true;
	      return;
	    case binop::W:
	      if (f2 == constant::true_instance())
		ret_.is.eventual = true;
	      return;
	    case binop::R:
	      if (f1 == constant::false_instance())
		ret_.is.universal = true;
	      return;
	    case binop::M:
	      if (f2 == constant::false_instance())
		ret_.is.universal = true;
	      return;
	    }
	  /* Unreachable code.  */
	  assert(0);
	}

	void
	visit(const automatop*)
	{
	  assert(0);
	}

	void
	visit(const multop* mo)
	{
	  unsigned mos = mo->size();
	  assert(mos != 0);
	  ret_.v = recurse_eu(mo->nth(0));
	  for (unsigned i = 1; i < mos && ret_.v != 0; ++i)
	    ret_.v &= recurse_eu(mo->nth(i));
	}

      private:
	eu_info ret_;
      };

      static unsigned
      recurse_eu(const formula* f)
      {
	eventual_universal_visitor v;
	const_cast<formula*>(f)->accept(v);
	return v.eu();
      }


      /////////////////////////////////////////////////////////////////////////

      class reduce_visitor: public visitor
      {
      public:

	reduce_visitor(int opt)
	  : opt_(opt)
	{
	}

	virtual ~reduce_visitor()
	{
	}

	formula*
	result() const
	{
	  return result_;
	}

	void
	visit(atomic_prop* ap)
	{
	  formula* f = ap->clone();
	  result_ = f;
	}

	void
	visit(constant* c)
	{
	  result_ = c;
	}

	void
	visit(unop* uo)
	{
	  result_ = recurse(uo->child());

	  switch (uo->op())
	    {
	    case unop::Not:
	      result_ = unop::instance(unop::Not, result_);
	      return;

	    case unop::X:
	      result_ = unop::instance(unop::X, result_);
	      return;

	    case unop::F:
	      /* If f is a pure eventuality formula then F(f)=f.  */
	      if (!(opt_ & Reduce_Eventuality_And_Universality)
		  || !is_eventual(result_))
		result_ = unop::instance(unop::F, result_);
	      return;

	    case unop::G:
	      /* If f is a pure universality formula then G(f)=f.  */
	      if (!(opt_ & Reduce_Eventuality_And_Universality)
		  || !is_universal(result_))
		result_ = unop::instance(unop::G, result_);
	      return;

	    case unop::Finish:
	      result_ = unop::instance(unop::Finish, result_);
	      return;
	    }
	  /* Unreachable code.  */
	  assert(0);
	}

	void
	visit(binop* bo)
	{
	  binop::type op = bo->op();

	  formula* f2 = recurse(bo->second());
	  eu_info f2i = { recurse_eu(f2) };

	  if (opt_ & Reduce_Eventuality_And_Universality)
	    {
	      /* If b is a pure eventuality formula then a U b = b.
		 If b is a pure universality formula a R b = b. */
	      if ((f2i.is.eventual && (op == binop::U))
		  || (f2i.is.universal && (op == binop::R)))
		{
		  result_ = f2;
		  return;
		}
	    }

	  formula* f1 = recurse(bo->first());
	  eu_info f1i = { recurse_eu(f1) };
	  if (opt_ & Reduce_Eventuality_And_Universality)
	    {
	      /* If a is a pure eventuality formula then a M b = a & b.
		 If a is a pure universality formula a W b = a|b. */
	      if (f1i.is.eventual && (op == binop::M))
		{
		  result_ = multop::instance(multop::And, f1, f2);
		  return;
		}
	      if (f1i.is.universal && (op == binop::W))
		{
		  result_ = multop::instance(multop::Or, f1, f2);
		  return;
		}
	    }


	  /* case of implies */
	  if (opt_ & Reduce_Syntactic_Implications)
	    {
	      switch (op)
		{
		case binop::Xor:
		case binop::Equiv:
		case binop::Implies:
		  break;

		case binop::U:
		  /* a < b => a U b = b */
		  if (syntactic_implication(f1, f2))
		    {
		      result_ = f2;
		      f1->destroy();
		      return;
		    }
		  /* !b < a => a U b = Fb */
		  if (syntactic_implication_neg(f2, f1, false))
		    {
		      result_ = unop::instance(unop::F, f2);
		      f1->destroy();
		      return;
		    }
		  /* a < b => a U (b U c) = (b U c) */
		  /* a < b => a U (b W c) = (b W c) */
		  {
		    binop* bo = dynamic_cast<binop*>(f2);
		    if (bo && (bo->op() == binop::U || bo->op() == binop::W)
			&& syntactic_implication(f1, bo->first()))
		      {
			result_ = f2;
			f1->destroy();
			return;
		      }
		  }
		  break;

		case binop::R:
		  /* b < a => a R b = b */
		  if (syntactic_implication(f2, f1))
		    {
		      result_ = f2;
		      f1->destroy();
		      return;
		    }
		  /* b < !a => a R b = Gb */
		  if (syntactic_implication_neg(f2, f1, true))
		    {
		      result_ = unop::instance(unop::G, f2);
		      f1->destroy();
		      return;
		    }
		  /* b < a => a R (b R c) = b R c */
		  /* b < a => a R (b M c) = b M c */
		  {
		    binop* bo = dynamic_cast<binop*>(f2);
		    if (bo && (bo->op() == binop::R || bo->op() == binop::M)
			&& syntactic_implication(bo->first(), f1))
		      {
			result_ = f2;
			f1->destroy();
			return;
		      }
		  }
		  /* a < b => a R (b R c) = a R c */
		  {
		    binop* bo = dynamic_cast<binop*>(f2);
		    if (bo && bo->op() == binop::R
			&& syntactic_implication(f1, bo->first()))
		      {
			result_ = binop::instance(binop::R, f1,
						  bo->second()->clone());
			f2->destroy();
			return;
		      }
		  }
		  break;

		case binop::W:
		  /* a < b => a W b = b */
		  if (syntactic_implication(f1, f2))
		    {
		      result_ = f2;
		      f1->destroy();
		      return;
		    }
		  /* !b < a => a W b = 1 */
		  if (syntactic_implication_neg(f2, f1, false))
		    {
		      result_ = constant::true_instance();
		      f1->destroy();
		      f2->destroy();
		      return;
		    }
		  /* a < b => a W (b W c) = (b W c) */
		  {
		    binop* bo = dynamic_cast<binop*>(f2);
		    if (bo && bo->op() == binop::W
			&& syntactic_implication(f1, bo->first()))
		      {
			result_ = f2;
			f1->destroy();
			return;
		      }
		  }
		  break;

		case binop::M:
		  /* b < a => a M b = b */
		  if (syntactic_implication(f2, f1))
		    {
		      result_ = f2;
		      f1->destroy();
		      return;
		    }
		  /* b < !a => a M b = 0 */
		  if (syntactic_implication_neg(f2, f1, true))
		    {
		      result_ = constant::false_instance();
		      f1->destroy();
		      f2->destroy();
		      return;
		    }
		  /* b < a => a M (b M c) = b M c */
		  {
		    binop* bo = dynamic_cast<binop*>(f2);
		    if (bo && bo->op() == binop::M
			&& syntactic_implication(bo->first(), f1))
		      {
			result_ = f2;
			f1->destroy();
			return;
		      }
		  }
		  /* a < b => a M (b M c) = a M c */
		  /* a < b => a M (b R c) = a M c */
		  {
		    binop* bo = dynamic_cast<binop*>(f2);
		    if (bo && (bo->op() == binop::M || bo->op() == binop::R)
			&& syntactic_implication(f1, bo->first()))
		      {
			result_ = binop::instance(binop::M, f1,
						  bo->second()->clone());
			f2->destroy();
			return;
		      }
		  }
		  break;
		}
	    }
	  result_ = binop::instance(op, f1, f2);
	}

	void
	visit(automatop*)
	{
	  assert(0);
	}

	void
	visit(multop* mo)
	{
	  unsigned mos = mo->size();
	  multop::vec* res = new multop::vec;

	  for (unsigned i = 0; i < mos; ++i)
	    res->push_back(recurse(mo->nth(i)));

	  if (opt_ & Reduce_Syntactic_Implications)
	    {

	      bool removed = true;
	      multop::vec::iterator f1;
	      multop::vec::iterator f2;

	      while (removed)
		{
		  removed = false;
		  f2 = f1 = res->begin();
		  ++f1;
		  while (f1 != res->end())
		    {
		      assert(f1 != f2);
		      // a < b => a + b = b
		      // a < b => a & b = a
		      if ((syntactic_implication(*f1, *f2) && // f1 < f2
			   (mo->op() == multop::Or)) ||
			  ((syntactic_implication(*f2, *f1)) && // f2 < f1
			   (mo->op() == multop::And)))
			{
			  // We keep f2
			  (*f1)->destroy();
			  res->erase(f1);
			  removed = true;
			  break;
			}
		      else if ((syntactic_implication(*f2, *f1) && // f2 < f1
				(mo->op() == multop::Or)) ||
			       ((syntactic_implication(*f1, *f2)) && // f1 < f2
				(mo->op() == multop::And)))
			{
			  // We keep f1
			  (*f2)->destroy();
			  res->erase(f2);
			  removed = true;
			  break;
			}
		      else
			++f1;
		    }
		}

	      /* f1 < !f2 => f1 & f2 = false
		 !f1 < f2 => f1 | f2 = true */
	      for (f1 = res->begin(); f1 != res->end(); f1++)
		for (f2 = res->begin(); f2 != res->end(); f2++)
		  if (f1 != f2 &&
		      syntactic_implication_neg(*f1, *f2,
						mo->op() !=  multop::Or))
		    {
		      for (multop::vec::iterator j = res->begin();
			   j != res->end(); j++)
			(*j)->destroy();
		      res->clear();
		      delete res;
		      if (mo->op() == multop::Or)
			result_ = constant::true_instance();
		      else
			result_ = constant::false_instance();
		      return;
		    }

	    }

	  if (!res->empty())
	    {
	      result_ = multop::instance(mo->op(), res);
	      return;
	    }
	  assert(0);
	}

	formula*
	recurse(formula* f)
	{
	  return reduce(f, opt_);
	}

      protected:
	formula* result_;
	int opt_;
      };

    } // anonymous

    formula*
    reduce(const formula* f, int opt)
    {
      formula* f1;
      formula* f2;
      formula* prev = 0;

      int n = 0;

      while (f != prev)
	{
	  ++n;
	  assert(n < 100);
	  if (prev)
	    {
	      prev->destroy();
	      prev = const_cast<formula*>(f);
	    }
	  else
	    {
	      prev = f->clone();
	    }
	  f1 = unabbreviate_logic(f);
	  f2 = simplify_f_g(f1);
	  f1->destroy();
	  f1 = negative_normal_form(f2);
	  f2->destroy();
	  f2 = f1;

	  if (opt & Reduce_Basics)
	    {
	      f1 = basic_reduce(f2);
	      f2->destroy();
	      f2 = f1;
	    }

	  if (opt & (Reduce_Syntactic_Implications
		     | Reduce_Eventuality_And_Universality))
	    {
	      reduce_visitor v(opt);
	      f2->accept(v);
	      f1 = v.result();
	      f2->destroy();
	      f2 = f1;
	    }


	  if (opt & (Reduce_Containment_Checks
		     | Reduce_Containment_Checks_Stronger))
	    {
	      formula* f1 =
		reduce_tau03(f2,
			     opt & Reduce_Containment_Checks_Stronger);
	      f2->destroy();
	      f2 = f1;
	    }
	  f = f2;
	}
      prev->destroy();
      return const_cast<formula*>(f);
    }

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
  }
}
