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

#ifndef SPOT_TGBAALGOS_REPLAYRUN_HH
# define SPOT_TGBAALGOS_REPLAYRUN_HH

# include "misc/common.hh"
# include <iosfwd>

namespace spot
{
  struct tgba_run;
  class tgba;

  /// \ingroup tgba_run
  /// \brief Replay a tgba_run on a tgba.
  ///
  /// This is similar to print_tgba_run(), except that the run is
  /// actually replayed on the automaton while it is printed.  Doing
  /// so makes it possible to display transition annotations (returned
  /// by spot::tgba::transition_annotation()).  The output will stop
  /// if the run cannot be completed.
  ///
  /// \param run the run to replay
  /// \param a the automata on which to replay that run
  /// \param os the stream on which the replay should be traced
  /// \param debug if set the output will be more verbose and extra
  ///              debugging informations will be output on failure
  /// \return true iff the run could be completed
  SPOT_API bool
  replay_tgba_run(std::ostream& os, const tgba* a, const tgba_run* run,
		  bool debug = false);
}

#endif // SPOT_TGBAALGOS_REPLAYRUN_HH
