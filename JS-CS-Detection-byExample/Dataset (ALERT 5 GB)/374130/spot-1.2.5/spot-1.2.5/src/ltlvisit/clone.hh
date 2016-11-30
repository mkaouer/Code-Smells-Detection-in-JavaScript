// -*- coding: utf-8 -*-
// Copyright (C) 2009, 2010, 2012, 2013 Laboratoire de Recherche et
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

#ifndef SPOT_LTLVISIT_CLONE_HH
# define SPOT_LTLVISIT_CLONE_HH

#include "ltlast/formula.hh"
#include "ltlast/visitor.hh"

namespace spot
{
  namespace ltl
  {
    /// \ingroup ltl_visitor
    /// \brief Clone a formula.
    ///
    /// This visitor is public, because it's convenient
    /// to derive from it and override part of its methods.
    /// But if you just want the functionality, consider using
    /// spot::ltl::formula::clone instead, it is way faster.
    class SPOT_API clone_visitor : public visitor
    {
    public:
      clone_visitor();
      virtual ~clone_visitor();

      const formula* result() const;

      void visit(const atomic_prop* ap);
      void visit(const unop* uo);
      void visit(const binop* bo);
      void visit(const automatop* mo);
      void visit(const multop* mo);
      void visit(const constant* c);
      void visit(const bunop* c);

      virtual const formula* recurse(const formula* f);

    protected:
      const formula* result_;
    };

    /// \ingroup ltl_essential
    /// \brief Clone a formula.
    /// \deprecated Use f->clone() instead.
    SPOT_API SPOT_DEPRECATED
    const formula* clone(const formula* f) __attribute__ ((deprecated));
  }
}

#endif // SPOT_LTLVISIT_LUNABBREV_HH
