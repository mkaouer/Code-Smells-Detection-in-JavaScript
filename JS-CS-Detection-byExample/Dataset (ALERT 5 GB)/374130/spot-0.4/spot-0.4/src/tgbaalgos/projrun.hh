// Copyright (C) 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
// et Marie Curie.
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

#ifndef SPOT_TGBAALGOS_PROJRUN_HH
# define SPOT_TGBAALGOS_PROJRUN_HH

#include <iosfwd>

namespace spot
{
  struct tgba_run;
  class tgba;

  /// \brief Project a tgba_run on a tgba.
  /// \ingroup tgba_run
  ///
  /// If a tgba_run has been generated on a product, or any other
  /// on-the-fly algorithm with tgba operands,
  ///
  /// \param run the run to replay
  /// \param a_run the automata on which the run was generated
  /// \param a_proj the automata on which to project the run
  /// \return true iff the run could be completed
  tgba_run* project_tgba_run(const tgba* a_run,
			     const tgba* a_proj,
			     const tgba_run* run);
}

#endif // SPOT_TGBAALGOS_PROJRUN_HH
