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
#include "clone.hh"

namespace spot
{
  namespace ltl
  {
    clone_visitor::clone_visitor()
    {
    }

    clone_visitor::~clone_visitor()
    {
    }

    formula*
    clone_visitor::result() const
    {
      return result_;
    }

    void
    clone_visitor::visit(atomic_prop* ap)
    {
      result_ = ap->ref();
    }

    void
    clone_visitor::visit(constant* c)
    {
      result_ = c->ref();
    }

    void
    clone_visitor::visit(unop* uo)
    {
      result_ = unop::instance(uo->op(), recurse(uo->child()));
    }

    void
    clone_visitor::visit(binop* bo)
    {
      result_ = binop::instance(bo->op(),
				recurse(bo->first()), recurse(bo->second()));
    }

    void
    clone_visitor::visit(multop* mo)
    {
      multop::vec* res = new multop::vec;
      unsigned mos = mo->size();
      for (unsigned i = 0; i < mos; ++i)
	res->push_back(recurse(mo->nth(i)));
      result_ = multop::instance(mo->op(), res);
    }

    formula*
    clone_visitor::recurse(formula* f)
    {
      return clone(f);
    }

    formula*
    clone(const formula* f)
    {
      clone_visitor v;
      const_cast<formula*>(f)->accept(v);
      return v.result();
    }
  }
}
