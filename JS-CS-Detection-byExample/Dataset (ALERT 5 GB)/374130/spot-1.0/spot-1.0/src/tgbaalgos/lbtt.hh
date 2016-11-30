// -*- coding: utf-8 -*-
// Copyright (C) 2012 Laboratoire de Recherche et Développement de
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

#ifndef SPOT_TGBAALGOS_LBTT_HH
# define SPOT_TGBAALGOS_LBTT_HH

#include "tgba/tgba.hh"
#include <iosfwd>
#include "ltlenv/defaultenv.hh"

namespace spot
{
  /// \brief Print reachable states in LBTT format.
  /// \ingroup tgba_io
  ///
  /// \param g The automata to print.
  /// \param os Where to print.
  std::ostream& lbtt_reachable(std::ostream& os, const tgba* g);


  /// \brief Read an automaton in LBTT's format
  /// \ingroup tgba_io
  ///
  /// \param is The stream on which the automaton should be input.
  /// \param error A string in which to write any error message.
  /// \param env The environment of atomic proposition into which parsing
  ///        should take place.
  /// \param envacc The environment of acceptance conditions into which parsing
  ///        should take place.
  /// \return the read tgba or 0 on error.
  const tgba* lbtt_parse(std::istream& is, std::string& error,
			 bdd_dict* dict,
			 ltl::environment& env
			 = ltl::default_environment::instance(),
			 ltl::environment& envacc
			 = ltl::default_environment::instance());
}

#endif // SPOT_TGBAALGOS_LBTT_HH
