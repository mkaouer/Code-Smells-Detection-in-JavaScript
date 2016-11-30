// -*- coding: utf-8 -*-
// Copyright (C) 2009, 2010, 2011, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
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
    postfix_visitor::visit(const atomic_prop* ap)
    {
      doit(ap);
    }

    void
    postfix_visitor::visit(const unop* uo)
    {
      uo->child()->accept(*this);
      doit(uo);
    }

    void
    postfix_visitor::visit(const binop* bo)
    {
      bo->first()->accept(*this);
      bo->second()->accept(*this);
      doit(bo);
    }

    void
    postfix_visitor::visit(const automatop* ao)
    {
      unsigned s = ao->size();
      for (unsigned i = 0; i < s; ++i)
	ao->nth(i)->accept(*this);
      doit(ao);
    }

    void
    postfix_visitor::visit(const multop* mo)
    {
      unsigned s = mo->size();
      for (unsigned i = 0; i < s; ++i)
	mo->nth(i)->accept(*this);
      doit(mo);
    }

    void
    postfix_visitor::visit(const bunop* so)
    {
      so->child()->accept(*this);
      doit(so);
    }

    void
    postfix_visitor::visit(const constant* c)
    {
      doit(c);
    }

    void
    postfix_visitor::doit(const atomic_prop* ap)
    {
      doit_default(ap);
    }

    void
    postfix_visitor::doit(const unop* uo)
    {
      doit_default(uo);
    }

    void
    postfix_visitor::doit(const binop* bo)
    {
      doit_default(bo);
    }

    void
    postfix_visitor::doit(const multop* mo)
    {
      doit_default(mo);
    }

    void
    postfix_visitor::doit(const automatop* ao)
    {
      doit_default(ao);
    }

    void
    postfix_visitor::doit(const bunop* so)
    {
      doit_default(so);
    }

    void
    postfix_visitor::doit(const constant* c)
    {
      doit_default(c);
    }

    void
    postfix_visitor::doit_default(const formula* f)
    {
      (void)f;
      // Dummy implementation that does nothing.
    }
  }
}
