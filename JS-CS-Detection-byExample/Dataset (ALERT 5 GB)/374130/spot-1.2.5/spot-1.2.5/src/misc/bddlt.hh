// Copyright (C) 2011 Laboratoire de Recherche et Developpement de
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

#ifndef SPOT_MISC_BDDLT_HH
# define SPOT_MISC_BDDLT_HH

# include <bdd.h>
# include <functional>

namespace spot
{
  /// \ingroup misc_tools
  /// \brief Comparison functor for BDDs.
  struct bdd_less_than :
    public std::binary_function<const bdd&, const bdd&, bool>
  {
    bool
    operator()(const bdd& left, const bdd& right) const
    {
      return left.id() < right.id();
    }
  };

  /// \ingroup misc_tools
  /// \brief Hash functor for BDDs.
  struct bdd_hash :
    public std::unary_function<const bdd&, size_t>
  {
    size_t
    operator()(const bdd& b) const
    {
      return b.id();
    }
  };

}

#endif // SPOT_MISC_BDDLT_HH
