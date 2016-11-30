// Copyright (C) 2012 Laboratoire de Recherche et
// Développement de l'Epita.
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

#ifndef SPOT_TGBAALGOS_DEGEN_HH
# define SPOT_TGBAALGOS_DEGEN_HH

namespace spot
{
  class sba;
  class tgba;

  /// \brief Degeneralize a spot::tgba into an equivalent sba with
  /// only one acceptance condition.
  ///
  /// This algorithms will build a new explicit automaton that has
  /// at most (N+1) times the number of states of the original automaton.
  ///
  /// If you want to build a degeneralized automaton on-the-fly, see
  /// spot::tgba_sba_proxy or spot::tgba_tba_proxy.
  ///
  /// \see tgba_sba_proxy, tgba_tba_proxy
  /// \ingroup tgba_misc
  sba* degeneralize(const tgba* a);
}


#endif
