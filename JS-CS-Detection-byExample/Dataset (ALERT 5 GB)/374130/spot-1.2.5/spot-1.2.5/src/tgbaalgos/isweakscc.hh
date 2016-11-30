// -*- coding: utf-8 -*-
// Copyright (C) 2012, 2013 Laboratoire de Recherche et Développement de
// l'Epita (LRDE).
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

#ifndef SPOT_TGBAALGOS_ISWEAKSCC_HH
# define SPOT_TGBAALGOS_ISWEAKSCC_HH

#include "scc.hh"

namespace spot
{
  /// \addtogroup tgba_misc
  /// @{

  /// \brief Whether the SCC number \a scc in \a map is inherently
  /// weak.
  ///
  /// An SCC is inherently weak if either its cycles are all
  /// accepting, or they are all non-accepting.
  ///
  /// Note the terminal SCCs are also inherently weak with that
  /// definition.
  ///
  /// The scc_map \a map should have been built already.  The absence
  /// of accepting cycle is easy to check (the scc_map can tell
  /// whether the SCC is non-accepting already).  Similarly, an SCC in
  /// which all transitions belong to all acceptance sets is
  /// necessarily weak.
  /// For other accepting SCCs, this function enumerates all cycles in
  /// the given SCC (it stops if it find a non-accepting cycle).
  SPOT_API bool
  is_inherently_weak_scc(scc_map& map, unsigned scc);

  /// \brief Whether the SCC number \a scc in \a map is weak.
  ///
  /// An SCC is weak if its non-accepting, or if all its transition
  /// are fully accepting (i.e., the belong to all acceptance sets).
  ///
  /// Note that terminal SCCs are also weak with that definition.
  ///
  /// The scc_map \a map should have been built already.
  SPOT_API bool
  is_weak_scc(scc_map& map, unsigned scc);

  /// \brief Whether the SCC number \a scc in \a map is complete.
  ///
  /// An SCC is complete iff for all states and all label there exists
  /// a transition that stays into this SCC.
  ///
  /// The scc_map \a map should have been built already.
  SPOT_API bool
  is_complete_scc(scc_map& map, unsigned scc);

  /// \brief Whether the SCC number \a scc in \a map is syntactically
  /// weak.
  ///
  /// This works only on tgba whose labels are formulas.  An SCC is
  /// syntactically weak if one of its states is labeled by a
  /// syntactic-persistence formula.
  ///
  /// The scc_map \a map should have been built already.
  SPOT_API bool
  is_syntactic_weak_scc(scc_map& map, unsigned scc);

  /// \brief Whether the SCC number \a scc in \a map is syntactically
  /// terminal.
  ///
  /// This works only on tgba whose labels are formulas.  An SCC is
  /// syntactically terminal if one of its states is labeled by a
  /// syntactic-guarantee formula.
  ///
  /// The scc_map \a map should have been built already.
  SPOT_API bool
  is_syntactic_terminal_scc(scc_map& map, unsigned scc);

  /// \brief Whether the SCC number \a scc in \a map is terminal.
  ///
  /// An SCC is terminal if it is weak, complete, and accepting.
  ///
  /// The scc_map \a map should have been built already.
  SPOT_API bool
  is_terminal_scc(scc_map& map, unsigned scc);

  /// @}
}


#endif // SPOT_TGBAALGOS_ISWEAKSCC_HH
