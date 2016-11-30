// Copyright (C) 2003, 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

/// \file ltlast/visitor.hh
/// \brief LTL visitor interface
#ifndef SPOT_LTLAST_VISITOR_HH
# define SPOT_LTLAST_VISITOR_HH

#include "predecl.hh"

namespace spot
{
  namespace ltl
  {
    /// \brief Formula visitor that can modify the formula.
    /// \ingroup ltl_essential
    ///
    /// Writing visitors is the prefered way
    /// to traverse a formula, since it doesn't
    /// involve any cast.
    ///
    /// If you do not need to modify the visited formula, inherit from
    /// spot::ltl:const_visitor instead.
    struct visitor
    {
      virtual ~visitor() {}
      virtual void visit(atomic_prop* node) = 0;
      virtual void visit(constant* node) = 0;
      virtual void visit(binop* node) = 0;
      virtual void visit(unop* node) = 0;
      virtual void visit(multop* node) = 0;
      virtual void visit(automatop* node) = 0;
    };

    /// \brief Formula visitor that cannot modify the formula.
    ///
    /// Writing visitors is the prefered way
    /// to traverse a formula, since it doesn't
    /// involve any cast.
    ///
    /// If you want to modify the visited formula, inherit from
    /// spot::ltl:visitor instead.
    struct const_visitor
    {
      virtual ~const_visitor() {}
      virtual void visit(const atomic_prop* node) = 0;
      virtual void visit(const constant* node) = 0;
      virtual void visit(const binop* node) = 0;
      virtual void visit(const unop* node) = 0;
      virtual void visit(const multop* node) = 0;
      virtual void visit(const automatop* node) = 0;
    };


  }
}

#endif // SPOT_LTLAST_VISITOR_HH
