// Copyright (C) 2010 Laboratoire de Recherche et Développement
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

#ifndef SPOT_TAALGOS_STATESSETBUILDER_HH
# define SPOT_TAALGOS_STATESSETBUILDER_HH

#include "ta/ta.hh"

namespace spot
{

  /// \brief Compute states set for an automaton.
  std::set<const state*> get_states_set(const ta* t);

  /// @}
}

#endif // SPOT_TAALGOS_STATESSETBUILDER_HH
