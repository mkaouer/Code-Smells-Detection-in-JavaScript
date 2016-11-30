// -*- coding: utf-8 -*-
// Copyright (C) 2013 Laboratoire de Recherche et Développement de
// l'Epita (LRDE).
// Copyright (C) 2003, 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_LTLVISIT_DUMP_HH
# define SPOT_LTLVISIT_DUMP_HH

#include "ltlast/formula.hh"
#include <iosfwd>

namespace spot
{
  namespace ltl
  {
    /// \ingroup ltl_io
    /// \brief Dump a formula tree.
    /// \param os The stream where it should be output.
    /// \param f The formula to dump.
    ///
    /// This is useful to display a formula when debugging.
    SPOT_API
    std::ostream& dump(std::ostream& os, const formula* f);
  }
}

#endif // SPOT_LTLVISIT_DUMP_HH
