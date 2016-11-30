// Copyright (C) 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_TGBAALGOS_GV04_HH
# define SPOT_TGBAALGOS_GV04_HH

#include "misc/optionmap.hh"

namespace spot
{
  class tgba;
  class emptiness_check;

  /// \brief Emptiness check based on Geldenhuys and Valmari's
  /// TACAS'04 paper.
  /// \ingroup emptiness_check_algorithms
  /// \pre The automaton \a a must have at most one acceptance condition.
  ///
  /// The original algorithm, coming from the following paper, has only
  /// been slightly modified to work on transition-based automata.
  /// \verbatim
  /// @InProceedings{geldenhuys.04.tacas,
  ///   author  = {Jaco Geldenhuys and Antti Valmari},
  ///   title   = {Tarjan's Algorithm Makes On-the-Fly {LTL} Verification
  ///             More Efficient},
  ///   booktitle = {Proceedings of the 10th International Conference on Tools
  ///             and Algorithms for the Construction and Analysis of Systems
  ///             (TACAS'04)},
  ///   editor  = {Kurt Jensen and Andreas Podelski},
  ///   pages   = {205--219},
  ///   year    = {2004},
  ///   publisher = {Springer-Verlag},
  ///   series  = {Lecture Notes in Computer Science},
  ///   volume  = {2988},
  ///   isbn    = {3-540-21299-X}
  /// }
  /// \endverbatim
  emptiness_check* explicit_gv04_check(const tgba* a,
				       option_map o = option_map());
}

#endif // SPOT_TGBAALGOS_GV04_HH
