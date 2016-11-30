// -*- coding: utf-
// Copyright (C) 2011, 2012 Laboratoire de Recherche et Developpement de
// l'Epita (LRDE).
// Copyright (C) 2003, 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_TGBAALGOS_DOTTY_HH
# define SPOT_TGBAALGOS_DOTTY_HH

#include <iosfwd>

namespace spot
{
  class tgba;
  class dotty_decorator;

  /// \brief Print reachable states in dot format.
  /// \ingroup tgba_io
  ///
  /// If \a assume_sba is set, this assumes that the automaton
  /// is an SBA and use double elipse to mark accepting states.
  ///
  /// The \a dd argument allows to customize the output in various
  /// ways.  See \ref tgba_dotty "this page" for a list of available
  /// decorators.  If no decorator is specified, the dotty_decorator
  /// is used.
  /// labels the transitions are encoded in UTF-8.
  std::ostream&
  dotty_reachable(std::ostream& os,
		  const tgba* g,
		  bool assume_sba = false,
		  dotty_decorator* dd = 0);
}

#endif // SPOT_TGBAALGOS_DOTTY_HH
