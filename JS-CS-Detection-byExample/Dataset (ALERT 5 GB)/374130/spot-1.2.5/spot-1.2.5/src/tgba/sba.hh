// Copyright (C) 2012 Laboratoire de Recherche et Développement
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

#ifndef SPOT_TGBA_SBA_HH
# define SPOT_TGBA_SBA_HH

#include "tgba.hh"

namespace spot
{
  /// \ingroup tgba_essentials
  /// \brief A State-based Generalized Büchi Automaton.
  ///
  /// An SBA is a TGBA in which the outgoing transitions of
  /// a state are either all accepting (in which case the
  /// source state is said "accepting"(, or all non-accepting.
  class sba: public tgba
  {
  public:
    /// \brief is \a s an accepting state?
    ///
    /// If a state is accepting all its outgoing transitions are
    /// accepting.
    virtual bool state_is_accepting(const spot::state* s) const = 0;
  };
}

#endif // SPOT_TGBA_SBA_HH
