// Copyright (C) 2009, 2011, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_TGBAALGOS_NEVERCLAIM_HH
# define SPOT_TGBAALGOS_NEVERCLAIM_HH

#include <iosfwd>
#include "ltlast/formula.hh"
#include "tgba/tgbatba.hh"

namespace spot
{
  /// \brief Print reachable states in Spin never claim format.
  /// \ingroup tgba_io
  ///
  /// \param os The output stream to print on.
  /// \param g The (state-based degeneralized) automaton to output.
  ///          There should be only one acceptance condition, and
  ///          all the transitions of a state should be either all accepting
  ///          or all unaccepting.  If your automaton does not satisfies
  ///          these requirements, call degeneralize() first.
  /// \param f The (optional) formula associated to the automaton.  If given
  ///          it will be output as a comment.
  /// \param comments Whether to comment each state of the never clause
  ///          with the label of the \a g automaton.
  std::ostream& never_claim_reachable(std::ostream& os,
				      const tgba* g,
				      const ltl::formula* f = 0,
				      bool comments = false);
}

#endif // SPOT_TGBAALGOS_NEVERCLAIM_HH
