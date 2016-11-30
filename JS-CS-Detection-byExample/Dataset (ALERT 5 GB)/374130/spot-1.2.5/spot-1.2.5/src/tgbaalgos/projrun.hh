// -*- coding: utf-8 -*-
// Copyright (C) 2013 Laboratoire de Recherche et Developpement
// de l'Epita (LRDE).
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

#ifndef SPOT_TGBAALGOS_PROJRUN_HH
# define SPOT_TGBAALGOS_PROJRUN_HH

# include "misc/common.hh"
# include <iosfwd>

namespace spot
{
  struct tgba_run;
  class tgba;

  /// \ingroup tgba_run
  /// \brief Project a tgba_run on a tgba.
  ///
  /// If a tgba_run has been generated on a product, or any other
  /// on-the-fly algorithm with tgba operands,
  ///
  /// \param run the run to replay
  /// \param a_run the automata on which the run was generated
  /// \param a_proj the automata on which to project the run
  /// \return true iff the run could be completed
  SPOT_API tgba_run*
  project_tgba_run(const tgba* a_run, const tgba* a_proj,

const tgba_run* run);
}

#endif // SPOT_TGBAALGOS_PROJRUN_HH
