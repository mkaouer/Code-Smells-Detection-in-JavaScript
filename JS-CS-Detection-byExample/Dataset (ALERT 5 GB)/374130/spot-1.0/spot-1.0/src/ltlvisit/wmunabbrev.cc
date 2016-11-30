// -*- coding: utf-8 -*-
// Copyright (C) 2012 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
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
#include "clone.hh"
#include "wmunabbrev.hh"
#include <cassert>

namespace spot
{
  namespace ltl
  {
    namespace
    {
      class unabbreviate_wm_visitor : public clone_visitor
      {
	typedef clone_visitor super;
      public:
	unabbreviate_wm_visitor()
	{
	}

	virtual
	~unabbreviate_wm_visitor()
	{
	}

	using super::visit;
	void visit(const binop* bo)
	{
	  const formula* f1 = recurse(bo->first());
	  const formula* f2 = recurse(bo->second());
	  binop::type op = bo->op();
	  switch (op)
	    {
	    case binop::Xor:
	    case binop::Implies:
	    case binop::Equiv:
	    case binop::U:
	    case binop::R:
	    case binop::UConcat:
	    case binop::EConcat:
	    case binop::EConcatMarked:
	      result_ = binop::instance(op, f1, f2);
	      break;
	    case binop::W:
	      // f1 W f2 = f2 R (f2 | f1)
	      result_ =
		binop::instance(binop::R, f2,
				multop::instance(multop::Or,
						 f2->clone(), f1));
	      break;
	    case binop::M:
	      // f1 M f2 = f2 U (g2 & f1)
	      result_ =
		binop::instance(binop::U, f2,
				multop::instance(multop::And,
						 f2->clone(), f1));
	      break;
	    }
	}

	virtual const formula* recurse(const formula* f)
	{
	  if (f->is_boolean())
	    return f->clone();
	  f->accept(*this);
	  return this->result();
	}
      };
    }

    const formula*
    unabbreviate_wm(const formula* f)
    {
      unabbreviate_wm_visitor v;
      return v.recurse(f);
    }
  }
}
