// Copyright (C) 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_LTLVISIT_BASICREDUCE_HH
# define SPOT_LTLVISIT_BASICREDUCE_HH

#include "ltlast/formula.hh"

namespace spot
{
  namespace ltl
  {
    /// \brief Basic rewritings.
    /// \ingroup ltl_rewriting
    formula* basic_reduce(const formula* f);

    /// \brief Whether a formula starts with GF.
    /// \ingroup ltl_misc
    bool is_GF(const formula* f);
    /// \brief Whether a formula starts with FG.
    /// \ingroup ltl_misc
    bool is_FG(const formula* f);
  }
}

#endif //  SPOT_LTLVISIT_BASICREDUCE_HH
