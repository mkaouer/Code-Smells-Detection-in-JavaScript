// -*- coding: utf-8 -*-
// Copyright (C) 2010, 2013 Laboratoire de Recherche et Développement de
// l'Epita.
// Copyright (C) 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_TGBAALGOS_REDUCERUN_HH
# define SPOT_TGBAALGOS_REDUCERUN_HH

# include "misc/common.hh"

namespace spot
{
  class tgba;
  struct tgba_run;

  /// \ingroup tgba_run
  /// \brief Reduce an accepting run.
  ///
  /// Return a run which is accepting for \a a and that is no longer
  /// than \a org.
  SPOT_API tgba_run*
  reduce_run(const tgba* a, const tgba_run* org);
}

#endif // SPOT_TGBAALGOS_REDUCERUN_HH
