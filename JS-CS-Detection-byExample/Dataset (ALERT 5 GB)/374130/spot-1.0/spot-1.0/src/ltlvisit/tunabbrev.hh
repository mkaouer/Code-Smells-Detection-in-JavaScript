// -*- coding: utf-8 -*-
// Copyright (C) 2011, 2012 Laboratoire de Recherche et Développement de
// l'Epita (LRDE).
// Copyright (C) 2003, 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_LTLVISIT_TUNABBREV_HH
# define SPOT_LTLVISIT_TUNABBREV_HH

#include "ltlast/formula.hh"
#include "ltlvisit/lunabbrev.hh"

namespace spot
{
  namespace ltl
  {
    /// \brief Clone and rewrite a formula to remove most of the
    /// abbreviated LTL and logical operators.
    /// \ingroup ltl_visitor
    ///
    /// The rewriting performed on logical operator is
    /// the same as the one done by spot::ltl::unabbreviate_logic_visitor.
    ///
    /// This will also rewrite unary operators such as unop::F,
    /// and unop::G, using only binop::U, and binop::R.
    ///
    /// This visitor is public, because it's convenient
    /// to derive from it and override some of its methods.
    /// But if you just want the functionality, consider using
    /// spot::ltl::unabbreviate_ltl instead.
    class unabbreviate_ltl_visitor : public unabbreviate_logic_visitor
    {
      typedef unabbreviate_logic_visitor super;
    public:
      unabbreviate_ltl_visitor();
      virtual ~unabbreviate_ltl_visitor();

      using super::visit;
      void visit(const unop* uo);

      const formula* recurse(const formula* f);
    };

    /// \brief Clone and rewrite a formula to remove most of the
    /// abbreviated LTL and logical operators.
    ///
    /// The rewriting performed on logical operator is
    /// the same as the one done by spot::ltl::unabbreviate_logic.
    ///
    /// This will also rewrite unary operators such as unop::F,
    /// and unop::G, using only binop::U, and binop::R.
    const formula* unabbreviate_ltl(const formula* f);
  }
}

#endif // SPOT_LTLVISIT_TUNABBREV_HH
