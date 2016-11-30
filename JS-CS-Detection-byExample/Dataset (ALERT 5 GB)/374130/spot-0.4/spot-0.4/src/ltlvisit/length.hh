// Copyright (C) 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_LTLVISIT_LENGTH_HH
# define SPOT_LTLVISIT_LENGTH_HH

#include "ltlast/formula.hh"

namespace spot
{
  namespace ltl
  {
    /// \brief Compute the length of a formula.
    /// \ingroup ltl_misc
    ///
    /// The length of a formula is the number of atomic properties,
    /// constants, and operators (logical and temporal) occurring in
    /// the formula.  spot::ltl::multops count only for 1, even if
    /// they have more than two operands (e.g. <code>a | b | c</code>
    /// has length 4, because <code>|</code> is represented once
    /// internally).
    int length(const formula* f);
  }
}

#endif // SPOT_LTLVISIT_LENGTH_HH
