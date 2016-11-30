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

#include "ltlast/allnodes.hh"
#include "tunabbrev.hh"

namespace spot
{
  namespace ltl
  {
    unabbreviate_ltl_visitor::unabbreviate_ltl_visitor()
    {
    }

    unabbreviate_ltl_visitor::~unabbreviate_ltl_visitor()
    {
    }

    void
    unabbreviate_ltl_visitor::visit(unop* uo)
    {
      switch (uo->op())
	{
	case unop::Finish:
	case unop::X:
	case unop::Not:
	  this->super::visit(uo);
	  return;
	case unop::F:
	  result_ = binop::instance(binop::U,
				    constant::true_instance(),
				    recurse(uo->child()));
	  return;
	case unop::G:
	  result_ = binop::instance(binop::R,
				    constant::false_instance(),
				    recurse(uo->child()));
	  return;
	}
    }

    formula*
    unabbreviate_ltl_visitor::recurse(formula* f)
    {
      return unabbreviate_ltl(f);
    }

    formula*
    unabbreviate_ltl(const formula* f)
    {
      unabbreviate_ltl_visitor v;
      const_cast<formula*>(f)->accept(v);
      return v.result();
    }

  }
}
