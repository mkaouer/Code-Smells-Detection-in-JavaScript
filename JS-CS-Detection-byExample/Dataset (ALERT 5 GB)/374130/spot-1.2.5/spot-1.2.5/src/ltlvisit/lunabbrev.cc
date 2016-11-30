// -*- coding: utf-8 -*-
// Copyright (C) 2009, 2010, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
// et Marie Curie.
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
    unabbreviate_logic_visitor::visit(const binop* bo)
    {
      const formula* f1 = recurse(bo->first());
      const formula* f2 = recurse(bo->second());

      binop::type op = bo->op();
      switch (op)
	{
	  /* f1 ^ f2  ==  (f1 & !f2) | (f2 & !f1) */
	case binop::Xor:
	  {
	    const formula* a =
	      multop::instance(multop::And, f1->clone(),
			       unop::instance(unop::Not, f2->clone()));
	    const formula* b =
	      multop::instance(multop::And, f2,
			       unop::instance(unop::Not, f1));
	    result_ = multop::instance(multop::Or, a, b);
	    return;
	  }
	  /* f1 => f2  ==  !f1 | f2 */
	case binop::Implies:
	  result_ = multop::instance(multop::Or,
				     unop::instance(unop::Not, f1), f2);
	  return;
	  /* f1 <=> f2  ==  (f1 & f2) | (!f1 & !f2) */
	case binop::Equiv:
	  {
	    const formula* f1c = f1->clone();
	    const formula* f2c = f2->clone();

	    result_ =
	      multop::instance(multop::Or,
			       multop::instance(multop::And, f1c, f2c),
			       multop::instance(multop::And,
						unop::instance(unop::Not, f1),
						unop::instance(unop::Not, f2)));
	    return;
	  }
          /* f1 U f2 == f1 U f2 */
	  /* f1 R f2 == f1 R f2 */
          /* f1 W f2 == f1 W f2 */
	  /* f1 M f2 == f1 M f2 */
	  /* f1 UConcat f2 == f1 UConcat f2 */
	  /* f1 EConcat f2 == f1 EConcat f2 */
   	case binop::U:
	case binop::R:
	case binop::W:
	case binop::M:
	case binop::UConcat:
	case binop::EConcat:
	case binop::EConcatMarked:
	  result_ = binop::instance(op, f1, f2);
	  return;
	}
      /* Unreachable code. */
      assert(0);
    }

    const formula*
    unabbreviate_logic_visitor::recurse(const formula* f)
    {
      return unabbreviate_logic(f);
    }

    const formula*
    unabbreviate_logic(const formula* f)
    {
      if (f->is_sugar_free_boolean())
	return f->clone();
      unabbreviate_logic_visitor v;
      f->accept(v);
      return v.result();
    }
  }
}
