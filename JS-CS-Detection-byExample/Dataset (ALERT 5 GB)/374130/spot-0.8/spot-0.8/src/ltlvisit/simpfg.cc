// Copyright (C) 2010 Laboratoire de Recherche et Développement de
// l'Epita (LRDE).
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
// et Marie Curie.
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

#include "ltlast/allnodes.hh"
#include "ltlvisit/clone.hh"
#include "simpfg.hh"
#include <cassert>

namespace spot
{
  namespace ltl
  {

    simplify_f_g_visitor::simplify_f_g_visitor()
    {
    }

    simplify_f_g_visitor::~simplify_f_g_visitor()
    {
    }

    void
    simplify_f_g_visitor::visit(binop* bo)
    {
      formula* f1 = recurse(bo->first());
      formula* f2 = recurse(bo->second());
      binop::type op = bo->op();

      switch (op)
	{
	case binop::Xor:
	case binop::Implies:
	case binop::Equiv:
	  result_ = binop::instance(op, f1, f2);
	  return;
	  /* true U f2 == F(f2) */
	case binop::U:
	  if (f1 == constant::true_instance())
	    result_ = unop::instance(unop::F, f2);
	  else
	    result_ = binop::instance(binop::U, f1, f2);
	  return;
	  /* false R f2 == G(f2) */
	case binop::R:
	  if (f1 == constant::false_instance())
	    result_ = unop::instance(unop::G, f2);
	  else
	    result_ = binop::instance(binop::R, f1, f2);
	  return;
	  /* f1 W false == G(f1) */
	case binop::W:
	  if (f2 == constant::false_instance())
	    result_ = unop::instance(unop::G, f1);
	  else
	    result_ = binop::instance(binop::W, f1, f2);
	  return;
	  /* f1 M true == F(f1) */
	case binop::M:
	  if (f2 == constant::true_instance())
	    result_ = unop::instance(unop::F, f1);
	  else
	    result_ = binop::instance(binop::M, f1, f2);
	  return;
	}
      /* Unreachable code. */
      assert(0);
    }

    formula*
    simplify_f_g_visitor::recurse(formula* f)
    {
      return simplify_f_g(f);
    }

    formula*
    simplify_f_g(const formula* f)
    {
      simplify_f_g_visitor v;
      const_cast<formula*>(f)->accept(v);
      return v.result();
    }
  }
}
