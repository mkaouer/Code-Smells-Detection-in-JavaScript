// Copyright (C) 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_MISC_BAREWORD_HH
# define SPOT_MISC_BAREWORD_HH

#include <string>

namespace spot
{
  /// \addtogroup misc_tools Miscellaneous helper algorithms
  /// @{
  /// \brief Whether a word is bare.
  ///
  /// Bare words should start with a letter or an underscore, and
  /// consist solely of alphanumeric characters and underscores.
  bool is_bare_word(const char* str);

  /// \brief Double-quote words that are not bare.
  /// \see is_bare_word
  std::string quote_unless_bare_word(const std::string& str);
  /// @}
}

#endif // SPOT_MISC_BAREWORD_HH
