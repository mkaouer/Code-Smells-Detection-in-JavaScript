// -*- coding: utf-8 -*-
// Copyright (C) 2010, 2012, 2013 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
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

#ifndef SPOT_TAALGOS_TGBA2TA_HH
# define SPOT_TAALGOS_TGBA2TA_HH

#include "tgba/tgba.hh"
#include "ta/taexplicit.hh"
#include "ta/tgtaexplicit.hh"

namespace spot
{
  /// \ingroup tgba_ta
  /// \brief Build a spot::ta_explicit* (TA) from an LTL formula.
  ///
  /// This is based on the following paper.
  /** \verbatim
       @InProceedings{        geldenhuys.06.spin,
       author        = {Jaco Geldenhuys and Henri Hansen},
       title         = {Larger Automata and Less Work for {LTL} Model Checking},
       booktitle     = {Proceedings of the 13th International SPIN Workshop
                       (SPIN'06)},
       year          = {2006},
       pages         = {53--70},
       series        = {Lecture Notes in Computer Science},
       volume        = {3925},
       publisher     = {Springer}
      }
      \endverbatim */
  ///
  /// \param tgba_to_convert The TGBA automaton to convert into a TA automaton
  ///
  /// \param atomic_propositions_set The set of atomic propositions used in the
  /// input TGBA \a tgba_to_convert
  ///
  /// \param degeneralized When false, the returned automaton is a generalized
  /// form of TA, called GTA (Generalized Testing Automaton).
  /// Like TGBA, GTA use Generalized Büchi acceptance
  /// conditions intead of Buchi-accepting states: there are several acceptance
  /// sets (of transitions), and a path is accepted if it traverses
  /// at least one transition of each set infinitely often or if it contains a
  /// livelock-accepting cycle (like a TA). The spot emptiness check algorithm
  /// for TA (spot::ta_check::check) can also be used to check GTA.
  ///
  /// \param artificial_initial_state_mode When set, the algorithm will build
  /// a TA automaton with an unique initial state.  This
  /// artificial initial state have one transition to each real initial state,
  /// and this transition is labeled by the corresponding initial condition.
  /// (see spot::ta::get_artificial_initial_state())
  ///
  /// \param single_pass_emptiness_check When set, the product between the
  /// returned automaton and a kripke structure requires only the fist pass of
  /// the emptiness check algorithm (see the parameter \c disable_second_pass
  /// of the method spot::ta_check::check)
  ///
  ///
  /// \param artificial_livelock_state_mode When set, the returned TA automaton
  ///  is a STA (Single-pass Testing Automata): a STA automaton is a TA
  /// where: for every livelock-accepting state s, if s is not also a
  /// Buchi-accepting state, then s has no successors. A STA product requires
  /// only one-pass emptiness check algorithm (see spot::ta_check::check)
  ///
  ///
  /// \return A spot::ta_explicit that recognizes the same language as the
  /// TGBA \a tgba_to_convert.
  SPOT_API ta_explicit*
  tgba_to_ta(const tgba* tgba_to_convert, bdd atomic_propositions_set,
      bool degeneralized = true, bool artificial_initial_state_mode = true,
      bool single_pass_emptiness_check = false,
      bool artificial_livelock_state_mode = false);

  /// \ingroup tgba_ta
  /// \brief Build a spot::tgta_explicit* (TGTA) from an LTL formula.
  ///
  /// \param tgba_to_convert The TGBA automaton to convert into a TGTA automaton
  /// \param atomic_propositions_set The set of atomic propositions used in the
  /// input TGBA \a tgba_to_convert
  ///
  /// \return A spot::tgta_explicit (spot::tgta) that recognizes the same
  ///  language as the TGBA \a tgba_to_convert.
  SPOT_API tgta_explicit*
  tgba_to_tgta(const tgba* tgba_to_convert, bdd atomic_propositions_set);

}

#endif // SPOT_TAALGOS_TGBA2TA_HH
