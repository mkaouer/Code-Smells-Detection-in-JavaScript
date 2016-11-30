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

#ifndef SPOT_LTLVISIT_NENOFORM_HH
# define SPOT_LTLVISIT_NENOFORM_HH

#include "ltlast/formula.hh"
#include "ltlast/visitor.hh"

namespace spot
{
  namespace ltl
  {
    /// \brief Build the negative normal form of \a f.
    /// \ingroup ltl_rewriting
    ///
    /// All negations of the formula are pushed in front of the
    /// atomic propositions.
    ///
    /// \param f The formula to normalize.
    /// \param negated If \c true, return the negative normal form of
    ///        \c !f
    ///
    /// Note that this will not remove abbreviated operators.  If you
    /// want to remove abbreviations, call spot::ltl::unabbreviate_logic
    /// or spot::ltl::unabbreviate_ltl first.  (Calling these functions
    /// after spot::ltl::negative_normal_form would likely produce a
    /// formula which is not in negative normal form.)

    formula* negative_normal_form(const formula* f, bool negated = false);
  }
}

#endif //  SPOT_LTLVISIT_NENOFORM_HH
