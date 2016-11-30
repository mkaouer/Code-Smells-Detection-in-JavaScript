// Copyright (C) 2012 Laboratoire de Recherche et Developement de
// l'Epita (LRDE).
// Copyright (C) 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
// d�partement Syst�mes R�partis Coop�ratifs (SRC), Universit� Pierre
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
    /// The length of a formula is the number of atomic propositions,
    /// constants, and operators (logical and temporal) occurring in
    /// the formula.  spot::ltl::multop instances with n arguments
    /// count for n-1; for instance <code>a | b | c</code> has length
    /// 5, even if there is only as single <code>|</code> node
    /// internally.
    ///
    /// If squash_boolean is set, all Boolean formulae are assumed
    /// to have length one.
    int length(const formula* f);

    /// \brief Compute the length of a formula, squashing Boolean formulae
    /// \ingroup ltl_misc
    ///
    /// This is similar to spot::ltl::length(), except all Boolean
    /// formulae are assumed to have length one.
    int length_boolone(const formula* f);
  }
}

#endif // SPOT_LTLVISIT_LENGTH_HH
