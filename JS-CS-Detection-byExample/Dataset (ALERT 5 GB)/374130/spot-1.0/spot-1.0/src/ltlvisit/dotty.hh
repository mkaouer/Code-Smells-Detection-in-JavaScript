// Copyright (C) 2003, 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_LTLVISIT_DOTTY_HH
# define SPOT_LTLVISIT_DOTTY_HH

#include <ltlast/formula.hh>
#include <iosfwd>

namespace spot
{
  namespace ltl
  {
    /// \brief Write a formula tree using dot's syntax.
    /// \ingroup ltl_io
    /// \param os The stream where it should be output.
    /// \param f The formula to translate.
    ///
    /// \c dot is part of the GraphViz package
    /// http://www.research.att.com/sw/tools/graphviz/
    std::ostream& dotty(std::ostream& os, const formula* f);
  }
}

#endif // SPOT_LTLVISIT_DOTTY_HH
