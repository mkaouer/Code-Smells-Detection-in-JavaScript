// Copyright (C) 2010, 2011 Laboratoire de Recherche et Développement de
// l'Epita (LRDE)
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

#ifndef SPOT_TGBAALGOS_SAFETY_HH
# define SPOT_TGBAALGOS_SAFETY_HH

#include "scc.hh"

namespace spot
{
  /// \brief Whether an automaton represents a guarantee property.
  ///
  /// A weak deterministic TGBA represents a guarantee property if any
  /// accepting path ends on an accepting state with only one
  /// transition that is a self-loop labelled by true.
  ///
  /// Note that in the general case, this is only a sufficient
  /// condition : some guarantee automata might not be recognized with
  /// this check e.g. because of some non-determinism in the
  /// automaton.  In that case, you should interpret a \c false return
  /// value as "I don't know".
  ///
  /// If you apply this function on a weak deterministic TGBA
  /// (e.g. after a successful minimization with
  /// minimize_obligation()), then the result leaves no doubt: false
  /// really means that the automaton is not a guarantee property.
  ///
  /// \param aut the automaton to check
  ///
  /// \param sm an scc_map of the automaton if available (it will be
  /// built otherwise.  If you supply an scc_map you should call
  /// build_map() before passing it to this function.
  bool is_guarantee_automaton(const tgba* aut, const scc_map* sm = 0);

  /// \brief Whether a minimized WDBA represents a safety property.
  ///
  /// A minimized WDBA (as returned by a successful run of
  /// minimize_obligation()) represent safety property if it contains
  /// only accepting transitions.
  ///
  /// \param aut the automaton to check
  bool is_safety_mwdba(const tgba* aut);


}

#endif // SPOT_TGBAALGOS_SAFETY_HH
