// Copyright (C) 2011 Laboratoire de Recherche et Développement de
// l'Epita.
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

#ifndef SPOT_TGBA_WDBACOMP_HH
# define SPOT_TGBA_WDBACOMP_HH

#include "tgba.hh"

namespace spot
{
  /// \brief Complement a weak deterministic Büchi automaton
  /// \ingroup tgba_on_the_fly_algorithms
  /// \param aut a weak deterministic Büchi automaton to complement
  /// \return a new automaton that recognizes the complement language
  tgba* wdba_complement(const tgba* aut);
}

#endif
