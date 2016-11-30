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

#ifndef SPOT_LTLVISIT_APCOLLECT_HH
# define SPOT_LTLVISIT_APCOLLECT_HH

#include <set>
#include "ltlast/atomic_prop.hh"

namespace spot
{
  namespace ltl
  {
    /// \addtogroup ltl_misc
    /// @{

    /// Set of atomic propositions.
    typedef std::set<atomic_prop*, formula_ptr_less_than> atomic_prop_set;

    /// \brief Return the set of atomic propositions occurring in a formula.
    ///
    /// \param f the formula to inspect
    /// \param s an existing set to fill with atomic_propositions discovered,
    ///        or 0 if the set should be allocated by the function.
    /// \return A pointer to the supplied set, \c s, augmented with
    ///        atomic propositions occurring in \c f; or a newly allocated
    ///        set containing all these atomic propositions if \c s is 0.
    atomic_prop_set*
    atomic_prop_collect(const formula* f, atomic_prop_set* s = 0);

    /// @}
  }
}
#endif
