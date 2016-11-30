// -*- coding: utf-8 -*-
// Copyright (C) 2013 Laboratoire de Recherche et Développement
// de l'Epita.
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

#ifndef SPOT_TGBAALGOS_DTBASAT_HH
# define SPOT_TGBAALGOS_DTBASAT_HH

#include "tgba/tgbaexplicit.hh"

namespace spot
{
  /// \brief Attempt to synthetize an equivalent deterministic TBA
  /// with a SAT solver.
  ///
  /// \param a the input TGBA.  It should have only one acceptance
  /// set and be deterministic.  I.e., it should be a deterministic TBA.
  ///
  /// \param target_state_number the desired number of states wanted
  /// in the resulting automaton.  The result may have less than \a
  /// target_state_number reachable states.
  ///
  /// \param state_based set to true to force all outgoing transitions
  /// of a state to share the same acceptance condition, effectively
  /// turning the TBA into a BA.
  ///
  /// If no equivalent deterministic TBA with \a target_state_number
  /// states is found, a null pointer
  SPOT_API tgba_explicit_number*
  dtba_sat_synthetize(const tgba* a, int target_state_number,
		      bool state_based = false);

  /// \brief Attempt to minimize a deterministic TBA with a SAT solver.
  ///
  /// This calls dtba_sat_synthetize() in a loop, with a decreasing
  /// number of states, and returns the last successfully built TBA.
  ///
  /// If no smaller TBA exist, this returns a null pointer.
  SPOT_API tgba_explicit_number*
  dtba_sat_minimize(const tgba* a, bool state_based = false);

  /// \brief Attempt to minimize a deterministic TBA with a SAT solver.
  ///
  /// This calls dtba_sat_synthetize() in a loop, but attempting to
  /// find the minimum number of states using a binary search.
  //
  /// If no smaller TBA exist, this returns a null pointer.
  SPOT_API tgba_explicit_number*
  dtba_sat_minimize_dichotomy(const tgba* a, bool state_based = false);
}

#endif // SPOT_TGBAALGOS_DTBASAT_HH
