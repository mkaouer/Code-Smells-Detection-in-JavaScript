// Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_TGBAALGOS_LBTT_HH
# define SPOT_TGBAALGOS_LBTT_HH

#include "tgba/tgba.hh"
#include <iostream>

namespace spot
{
  /// \brief Print reachable states in LBTT format.
  ///
  /// Note that LBTT expects an automaton with transition
  /// labeled by propositional formulae, and generalized
  /// Büchi acceptance conditions on \b states.  This
  /// is unlike our spot::tgba automata which put
  /// both generalized acceptance conditions and propositional
  /// formulae) on \b transitions.
  ///
  /// This algorithm will therefore produce an automata where
  /// acceptance conditions have been moved from each transition to
  /// previous state.  In the worst case, doing so will multiply the
  /// number of states and transitions of the automata by
  /// <code>2^|Acc|</code>.  where <code>|Acc|</code> is the number of
  /// acceptance conditions used by the automata.  (It can be a bit
  /// more because LBTT allows only for one initial state:
  /// lbtt_reachable() may also have to create an additional state in
  /// case the source initial state had to be split.)  You have been
  /// warned.
  ///
  /// \param g The automata to print.
  /// \param os Where to print.
  std::ostream& lbtt_reachable(std::ostream& os, const tgba* g);
}

#endif // SPOT_TGBAALGOS_LBTT_HH
