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

#include "ltlvisit/postfix.hh"
#include "ltlast/allnodes.hh"

namespace spot
{
  namespace ltl
  {
    postfix_visitor::postfix_visitor()
    {
    }

    postfix_visitor::~postfix_visitor()
    {
    }

    void
    postfix_visitor::visit(atomic_prop* ap)
    {
      doit(ap);
    }

    void
    postfix_visitor::visit(unop* uo)
    {
      uo->child()->accept(*this);
      doit(uo);
    }

    void
    postfix_visitor::visit(binop* bo)
    {
      bo->first()->accept(*this);
      bo->second()->accept(*this);
      doit(bo);
    }

    void
    postfix_visitor::visit(automatop* ao)
    {
      unsigned s = ao->size();
      for (unsigned i = 0; i < s; ++i)
	ao->nth(i)->accept(*this);
      doit(ao);
    }

    void
    postfix_visitor::visit(multop* mo)
    {
      unsigned s = mo->size();
      for (unsigned i = 0; i < s; ++i)
	mo->nth(i)->accept(*this);
      doit(mo);
    }

    void
    postfix_visitor::visit(constant* c)
    {
      doit(c);
    }

    void
    postfix_visitor::doit(atomic_prop* ap)
    {
      doit_default(ap);
    }

    void
    postfix_visitor::doit(unop* uo)
    {
      doit_default(uo);
    }

    void
    postfix_visitor::doit(binop* bo)
    {
      doit_default(bo);
    }

    void
    postfix_visitor::doit(multop* mo)
    {
      doit_default(mo);
    }

    void
    postfix_visitor::doit(automatop* ao)
    {
      doit_default(ao);
    }

    void
    postfix_visitor::doit(constant* c)
    {
      doit_default(c);
    }

    void
    postfix_visitor::doit_default(formula* f)
    {
      (void)f;
      // Dummy implementation that does nothing.
    }
  }
}
