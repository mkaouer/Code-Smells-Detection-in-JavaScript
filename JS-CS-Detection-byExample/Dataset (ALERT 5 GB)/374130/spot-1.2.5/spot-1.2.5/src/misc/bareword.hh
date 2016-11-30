// -*- coding: utf-8 -*-
// Copyright (C) 2013 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_MISC_BAREWORD_HH
# define SPOT_MISC_BAREWORD_HH

# include "common.hh"
# include <string>

namespace spot
{
  /// \addtogroup misc_tools Miscellaneous helper algorithms
  /// @{
  /// \brief Whether a word is bare.
  ///
  /// Bare words should start with a letter, an underscore, or a dot,
  /// and consist solely of alphanumeric characters, underscores, and
  /// dots.
  SPOT_API bool is_bare_word(const char* str);

  /// \brief Double-quote words that are not bare.
  /// \see is_bare_word
  SPOT_API std::string quote_unless_bare_word(const std::string& str);
  /// @}
}

#endif // SPOT_MISC_BAREWORD_HH
