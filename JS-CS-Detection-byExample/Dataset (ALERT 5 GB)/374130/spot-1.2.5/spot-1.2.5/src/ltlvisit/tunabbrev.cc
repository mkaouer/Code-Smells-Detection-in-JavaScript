// Copyright (C) 2009, 2010, 2012 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
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
    unabbreviate_ltl_visitor::visit(const unop* uo)
    {
      switch (uo->op())
	{
	case unop::Finish:
	case unop::X:
	case unop::Not:
	case unop::Closure:
	case unop::NegClosure:
	case unop::NegClosureMarked:
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

    const formula*
    unabbreviate_ltl_visitor::recurse(const formula* f)
    {
      return unabbreviate_ltl(f);
    }

    const formula*
    unabbreviate_ltl(const formula* f)
    {
      if (f->is_sugar_free_boolean() && f->is_sugar_free_ltl())
	return f->clone();
      unabbreviate_ltl_visitor v;
      f->accept(v);
      return v.result();
    }

  }
}
