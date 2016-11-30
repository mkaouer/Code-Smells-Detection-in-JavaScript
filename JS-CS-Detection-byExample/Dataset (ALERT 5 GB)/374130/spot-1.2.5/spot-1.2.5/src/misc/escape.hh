// -*- coding: utf-8 -*-
// Copyright (C) 2011, 2012, 2013 Laboratoire de Recherche et
// Developpement de l'Epita (LRDE).
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

#ifndef SPOT_MISC_ESCAPE_HH
# define SPOT_MISC_ESCAPE_HH

# include "common.hh"
# include <iosfwd>
# include <string>

namespace spot
{
  /// \addtogroup misc_tools
  /// @{

  /// \brief Double characters <code>"</code> in strings.
  ///
  /// In CSV files, as defined by RFC4180, double-quoted string that
  /// contain double-quotes should simply duplicate those quotes.
  SPOT_API std::ostream&
  escape_rfc4180(std::ostream& os, const std::string& str);

  /// \brief Escape characters <code>"</code>, <code>\\</code>, and
  /// <code>\\n</code> in \a str.
  SPOT_API std::ostream&
  escape_str(std::ostream& os, const std::string& str);

  /// \brief Escape characters <code>"</code>, <code>\\</code>, and
  /// <code>\\n</code> in \a str.
  SPOT_API std::string
  escape_str(const std::string& str);

  /// \brief Remove spaces at the front and back of \a str.
  SPOT_API void
  trim(std::string& str);
  /// @}
}

#endif // SPOT_MISC_ESCAPE_HH
