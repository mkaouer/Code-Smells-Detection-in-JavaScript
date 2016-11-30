// Copyright (C) 2009, 2010 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
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
#include "lunabbrev.hh"
#include <cassert>

namespace spot
{
  namespace ltl
  {

    unabbreviate_logic_visitor::unabbreviate_logic_visitor()
    {
    }

    unabbreviate_logic_visitor::~unabbreviate_logic_visitor()
    {
    }

    void
    unabbreviate_logic_visitor::visit(binop* bo)
    {
      formula* f1 = recurse(bo->first());
      formula* f2 = recurse(bo->second());

      binop::type op = bo->op();
      switch (op)
	{
	  /* f1 ^ f2  ==  (f1 & !f2) | (f2 & !f1) */
	case binop::Xor:
	  result_ = multop::instance(multop::Or,
				     multop::instance(multop::And, f1->clone(),
						      unop::instance(unop::Not,
								     f2)),
				     multop::instance(multop::And, f2->clone(),
						      unop::instance(unop::Not,
								     f1)));
	  return;
	  /* f1 => f2  ==  !f1 | f2 */
	case binop::Implies:
	  result_ = multop::instance(multop::Or,
				     unop::instance(unop::Not, f1), f2);
	  return;
	  /* f1 <=> f2  ==  (f1 & f2) | (!f1 & !f2) */
	case binop::Equiv:
	  result_ = multop::instance(multop::Or,
				     multop::instance(multop::And,
						      f1->clone(), f2->clone()),
				     multop::instance(multop::And,
						      unop::instance(unop::Not,
								     f1),
						      unop::instance(unop::Not,
								     f2)));
	  return;
          /* f1 U f2 == f1 U f2 */
	  /* f1 R f2 == f1 R f2 */
          /* f1 W f2 == f1 W f2 */
	  /* f1 M f2 == f1 M f2 */
   	case binop::U:
	case binop::R:
	case binop::W:
	case binop::M:
	  result_ = binop::instance(op, f1, f2);
	  return;
	}
      /* Unreachable code. */
      assert(0);
    }

    formula*
    unabbreviate_logic_visitor::recurse(formula* f)
    {
      return unabbreviate_logic(f);
    }

    formula*
    unabbreviate_logic(const formula* f)
    {
      unabbreviate_logic_visitor v;
      const_cast<formula*>(f)->accept(v);
      return v.result();
    }
  }
}
