// Copyright (C) 2003, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_TGBA_TGBABDDFACTORY_HH
# define SPOT_TGBA_TGBABDDFACTORY_HH

#include "tgbabddcoredata.hh"

namespace spot
{
  /// \brief Abstract class for spot::tgba_bdd_concrete factories.
  ///
  /// A spot::tgba_bdd_concrete can be constructed from anything that
  /// supplies core data and their associated dictionary.
  class tgba_bdd_factory
  {
  public:
    virtual ~tgba_bdd_factory() {}
    /// Get the core data for the new automata.
    virtual const tgba_bdd_core_data& get_core_data() const = 0;
  };
}

#endif // SPOT_TGBA_TGBABDDFACTORY_HH
