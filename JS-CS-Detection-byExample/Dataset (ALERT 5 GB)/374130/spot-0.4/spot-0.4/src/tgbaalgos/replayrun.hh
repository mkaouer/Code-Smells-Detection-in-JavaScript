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

#ifndef SPOT_TGBAALGOS_REPLAYRUN_HH
# define SPOT_TGBAALGOS_REPLAYRUN_HH

#include <iosfwd>

namespace spot
{
  struct tgba_run;
  class tgba;

  /// \brief Replay a tgba_run on a tgba.
  /// \ingroup tgba_run
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
  bool replay_tgba_run(std::ostream& os, const tgba* a, const tgba_run* run,
		       bool debug = false);
}

#endif // SPOT_TGBAALGOS_REPLAYRUN_HH
