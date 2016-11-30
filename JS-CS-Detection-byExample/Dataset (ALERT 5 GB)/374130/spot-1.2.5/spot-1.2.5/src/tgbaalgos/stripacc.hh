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

#ifndef SPOT_TGBAALGOS_STRIPACC_HH
# define SPOT_TGBAALGOS_STRIPACC_HH

# include "tgba/tgbaexplicit.hh"

namespace spot
{
  /// \ingroup tgba_misc
  /// \brief Duplicate automaton \a a, removing all acceptance sets.
  ///
  /// This is equivalent to marking all states/transitions as accepting.
  SPOT_API sba_explicit_number*
  strip_acceptance(const tgba* a);
}

#endif // SPOT_TGBAALGOS_STRIPACC_HH
