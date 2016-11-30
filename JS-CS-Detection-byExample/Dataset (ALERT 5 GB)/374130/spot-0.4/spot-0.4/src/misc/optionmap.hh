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

#ifndef SPOT_MISC_OPTIONMAP_HH
# define SPOT_MISC_OPTIONMAP_HH

#include <string>
#include <map>
#include <iosfwd>

namespace spot
{
  /// \brief Manage a map of options.
  /// \ingroup misc_tools
  /// Each option is defined by a string and is associated to an integer value.
  class option_map
  {
  public:
    /// \brief Add the parsed options to the map.
    ///
    /// \a options are separated by a space, comma, semicolon or tabulation and
    /// can be optionnaly followed by an integer value (preceded by an equal
    /// sign). If not specified, the default value is 1.
    ///
    /// The following three lines are equivalent.
    /// \verbatim
    /// optA !optB optC=4194304
    /// optA=1, optB=0, optC=4096K
    /// optC = 4M; optA !optB
    /// \endverbatim
    ///
    /// \return A non-null pointer to the option for which an expected integer
    /// value cannot be parsed.
    const char* parse_options(const char* options);

    /// \brief Get the value of \a option.
    ///
    /// \return The value associated to \a option if it exists,
    /// \a def otherwise.
    /// \see operator[]()
    int get(const char* option, int def = 0) const;

    /// \brief Get the value of \a option.
    ///
    /// \return The value associated to \a option if it exists, 0 otherwise.
    /// \see get()
    int operator[](const char* option) const;

    /// \brief Set the value of \a option to \a val.
    ///
    /// \return The previous value associated to \a option if declared,
    /// or \a def otherwise.
    int set(const char* option, int val, int def = 0);

    /// Acquire all the settings of \a o.
    void set(const option_map& o);

    /// \brief Get a reference to the current value of \a option.
    int& operator[](const char* option);

    /// \brief Print the option_map \a m.
    friend std::ostream& operator<<(std::ostream& os, const option_map& m);

  private:
    std::map<std::string, int> options_;
  };
};

#endif // SPOT_MISC_OPTIONMAP_HH
