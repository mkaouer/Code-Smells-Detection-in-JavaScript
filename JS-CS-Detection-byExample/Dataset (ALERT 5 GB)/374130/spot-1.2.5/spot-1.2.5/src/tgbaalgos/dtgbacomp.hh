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

#ifndef SPOT_TGBAALGOS_DTGBACOMP_HH
# define SPOT_TGBAALGOS_DTGBACOMP_HH

#include "tgba/tgbaexplicit.hh"

namespace spot
{
  /// \brief Complement a deterministic TGBA
  ///
  /// The automaton \a aut should be deterministic.  It does no need
  /// to be complete.  Acceptance can be transition-based, or
  /// state-based.  The resulting automaton is very unlikely to be
  /// deterministic.
  SPOT_API tgba_explicit_number*
  dtgba_complement(const tgba* aut);
}

#endif // SPOT_TGBAALGOS_DTGBACOMP_HH
