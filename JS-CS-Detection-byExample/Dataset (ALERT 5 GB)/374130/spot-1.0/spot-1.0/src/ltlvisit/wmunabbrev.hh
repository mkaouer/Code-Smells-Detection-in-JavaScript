// -*- coding: utf-8 -*-
// Copyright (C) 2012 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
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

#ifndef SPOT_LTLVISIT_WMUNABBREV_HH
# define SPOT_LTLVISIT_WMUNABBREV_HH

namespace spot
{
  namespace ltl
  {
    class formula;

    /// \brief Rewrite a formula to remove the W and M operators.
    ///
    /// This is necessary if you want to use the formula with a tool
    /// that do not support these operators.
    ///
    /// <code>a W b</code> is replaced by <code>b R (b | a)</code>,
    /// and <code>a M b</code> is replaced by <code>b U (b & a)</code>.
    ///
    /// \ingroup ltl_rewriting
    const formula* unabbreviate_wm(const formula* f);
  }
}

#endif // SPOT_LTLVISIT_WMUNABBREV_HH
