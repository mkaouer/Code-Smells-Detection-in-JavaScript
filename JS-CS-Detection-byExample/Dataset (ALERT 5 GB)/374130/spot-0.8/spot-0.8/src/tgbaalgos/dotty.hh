// Copyright (C) 2003, 2004, 2011  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_TGBAALGOS_DOTTY_HH
# define SPOT_TGBAALGOS_DOTTY_HH

#include "dottydec.hh"
#include <iosfwd>

namespace spot
{
  class tgba;

  /// \brief Print reachable states in dot format.
  /// \ingroup tgba_io
  ///
  /// If assume_sba is set, this assumes that the automaton
  /// is an SBA and use double elipse to mark accepting states.
  ///
  /// The \a dd argument allows to customize the output in various
  /// ways.  See \ref tgba_dotty "this page" for a list of available
  /// decorators.
  std::ostream&
  dotty_reachable(std::ostream& os,
		  const tgba* g,
		  bool assume_sba = false,
		  dotty_decorator* dd = dotty_decorator::instance());
}

#endif // SPOT_TGBAALGOS_DOTTY_HH
