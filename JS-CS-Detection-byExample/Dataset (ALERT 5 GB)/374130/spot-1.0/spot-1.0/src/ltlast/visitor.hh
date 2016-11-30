// Copyright (C) 2009, 2010, 2012 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

/// \file ltlast/visitor.hh
/// \brief LTL visitor interface
#ifndef SPOT_LTLAST_VISITOR_HH
# define SPOT_LTLAST_VISITOR_HH

#include "predecl.hh"

namespace spot
{
  namespace ltl
  {
    /// \brief Formula visitor
    /// \ingroup ltl_essential
    ///
    /// Implementing visitors is the prefered way
    /// to traverse a formula, since it does not
    /// involve any cast.
    struct visitor
    {
      virtual ~visitor() {}
      virtual void visit(const atomic_prop* node) = 0;
      virtual void visit(const constant* node) = 0;
      virtual void visit(const binop* node) = 0;
      virtual void visit(const unop* node) = 0;
      virtual void visit(const multop* node) = 0;
      virtual void visit(const automatop* node) = 0;
      virtual void visit(const bunop* node) = 0;
    };
  }
}

#endif // SPOT_LTLAST_VISITOR_HH
