// Copyright (C) 2003, 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
// d�partement Syst�mes R�partis Coop�ratifs (SRC), Universit� Pierre
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

#ifndef SPOT_TGBAALGOS_LTL2TGBA_LACIM_HH
# define SPOT_TGBAALGOS_LTL2TGBA_LACIM_HH

#include "ltlast/formula.hh"
#include "tgba/tgbabddconcrete.hh"

namespace spot
{
  /// \brief Build a spot::tgba_bdd_concrete from an LTL formula.
  /// \ingroup tgba_ltl
  ///
  /// This is based on the following paper.
  /// \verbatim
  /// @InProceedings{   couvreur.00.lacim,
  ///   author        = {Jean-Michel Couvreur},
  ///   title         = {Un point de vue symbolique sur la logique temporelle
  ///                   lin{\'e}aire},
  ///   booktitle     = {Actes du Colloque LaCIM 2000},
  ///   month         = {August},
  ///   year          = {2000},
  ///   pages         = {131--140},
  ///   volume        = {27},
  ///   series        = {Publications du LaCIM},
  ///   publisher     = {Universit{\'e} du Qu{\'e}bec {\`a} Montr{\'e}al},
  ///   editor        = {Pierre Leroux}
  /// }
  /// \endverbatim
  /// \param f The formula to translate into an automaton.
  /// \param dict The spot::bdd_dict the constructed automata should use.
  /// \return A spot::tgba_bdd_concrete that recognizes the language of \a f.
  tgba_bdd_concrete* ltl_to_tgba_lacim(const ltl::formula* f, bdd_dict* dict);
}

#endif // SPOT_TGBAALGOS_LTL2TGBA_LACIM_HH
