// Copyright (C) 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_MISC_LTSTR_HH
#  define SPOT_MISC_LTSTR_HH

#  include <cstring>
#  include <functional>

namespace spot
{

  /// \brief Strict Weak Ordering for \c char*.
  /// \ingroup misc_tools
  ///
  /// This is meant to be used as a comparison functor for
  /// STL \c map whose key are of type <code>const char*</code>.
  ///
  /// For instance here is how one could declare
  /// a map of <code>const state*</code>.
  /// \code
  ///   std::map<const char*, int, spot::state_ptr_less_than> seen;
  /// \endcode
  struct char_ptr_less_than:
    public std::binary_function<const char*, const char*, bool>
  {
    bool
    operator()(const char* left, const char* right) const
    {
      return strcmp(left, right) < 0;
    }
  };
}

#endif // SPOT_MISC_LTSTR_HH
