// Copyright (C) 2003, 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_LTLVISIT_LUNABBREV_HH
# define SPOT_LTLVISIT_LUNABBREV_HH

#include "clone.hh"

namespace spot
{
  namespace ltl
  {
    /// \brief Clone and rewrite a formula to remove most of the
    /// abbreviated logical operators.
    /// \ingroup ltl_visitor
    ///
    /// This will rewrite binary operators such as binop::Implies,
    /// binop::Equals, and binop::Xor, using only unop::Not, multop::Or,
    /// and multop::And.
    ///
    /// This visitor is public, because it's convenient
    /// to derive from it and override some of its methods.
    /// But if you just want the functionality, consider using
    /// spot::ltl::unabbreviate_logic instead.
    class unabbreviate_logic_visitor : public clone_visitor
    {
      typedef clone_visitor super;
    public:
      unabbreviate_logic_visitor();
      virtual ~unabbreviate_logic_visitor();

      using super::visit;
      void visit(binop* bo);

      virtual formula* recurse(formula* f);
    };

    /// \brief Clone and rewrite a formula to remove most of the abbreviated
    /// logical operators.
    /// \ingroup ltl_rewriting
    ///
    /// This will rewrite binary operators such as binop::Implies,
    /// binop::Equals, and binop::Xor, using only unop::Not, multop::Or,
    /// and multop::And.
    formula* unabbreviate_logic(const formula* f);

  }
}

#endif // SPOT_LTLVISIT_LUNABBREV_HH
