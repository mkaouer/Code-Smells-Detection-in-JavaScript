// Copyright (C) 2009, 2010, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
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

/// \file ltlast/predecl.hh
/// \brief Predeclare all LTL node types.
///
/// This file is usually used when \b declaring methods and functions
/// over LTL nodes.
/// Use ltlast/allnodes.hh or an individual header when the definition of
/// the node is actually needed.
#ifndef SPOT_LTLAST_PREDECL_HH
# define SPOT_LTLAST_PREDECL_HH

namespace spot
{
  namespace ltl
  {
    struct visitor;

    class atomic_prop;
    class automatop;
    class binop;
    class bunop;
    class constant;
    class formula;
    class multop;
    class unop;
  }
}

#endif // SPOT_LTLAST_PREDECL_HH
