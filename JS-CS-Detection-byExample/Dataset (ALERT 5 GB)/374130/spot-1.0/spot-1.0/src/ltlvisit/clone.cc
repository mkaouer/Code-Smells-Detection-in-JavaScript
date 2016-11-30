// Copyright (C) 2009, 2010, 2012 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2003 Laboratoire d'Informatique de Paris 6 (LIP6),
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

    const formula*
    clone_visitor::result() const
    {
      return result_;
    }

    void
    clone_visitor::visit(const atomic_prop* ap)
    {
      result_ = ap->clone();
    }

    void
    clone_visitor::visit(const constant* c)
    {
      result_ = c->clone();
    }

    void
    clone_visitor::visit(const bunop* bo)
    {
      result_ = bunop::instance(bo->op(), recurse(bo->child()),
				bo->min(), bo->max());
    }

    void
    clone_visitor::visit(const unop* uo)
    {
      result_ = unop::instance(uo->op(), recurse(uo->child()));
    }

    void
    clone_visitor::visit(const binop* bo)
    {
      const formula* first = recurse(bo->first());
      result_ = binop::instance(bo->op(),
				first, recurse(bo->second()));
    }

    void
    clone_visitor::visit(const automatop* ao)
    {
      automatop::vec* res = new automatop::vec;
      for (unsigned i = 0; i < ao->size(); ++i)
        res->push_back(recurse(ao->nth(i)));
      result_ = automatop::instance(ao->get_nfa(), res, ao->is_negated());
    }

    void
    clone_visitor::visit(const multop* mo)
    {
      multop::vec* res = new multop::vec;
      unsigned mos = mo->size();
      for (unsigned i = 0; i < mos; ++i)
	res->push_back(recurse(mo->nth(i)));
      result_ = multop::instance(mo->op(), res);
    }

    const formula*
    clone_visitor::recurse(const formula* f)
    {
      f->accept(*this);
      return result_;
    }

    const formula*
    clone(const formula* f)
    {
      return f->clone();
    }
  }
}
