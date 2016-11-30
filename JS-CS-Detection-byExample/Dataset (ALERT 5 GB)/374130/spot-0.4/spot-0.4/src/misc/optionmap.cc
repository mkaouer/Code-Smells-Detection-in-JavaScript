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

#include <cstring>
#include <iostream>
#include "optionmap.hh"

namespace spot
{
  const char*
  option_map::parse_options(const char* options)
  {
    while (*options)
      {
	// Skip leading separators.
	while (*options && strchr(" \t\n,;", *options))
	  ++options;

	// `!foo' is a shorthand for `foo=0'.
	const char* negated = 0;
	if (*options == '!')
	  {
	    // Skip spaces.
	    while (*options && strchr(" \t\n", *options))
	      ++options;
	    negated = options++;
	  }

	if (!*options)
	  {
	    if (negated)
	      return negated;
	    else
	      break;
	  }

	const char* name_start = options;

	// Find the end of the name.
	while (*options && !strchr(", \t\n;=", *options))
	  ++options;

	std::string name(name_start, options);

	// Skip spaces.
	while (*options && strchr(" \t\n", *options))
	  ++options;

	if (*options != '=')
	  {
	    options_[name] = (negated ? 0 : 1);
	  }
	else if (negated)
	  {
	    return negated;
	  }
	else
	  {
	    ++options;
	    // Skip spaces.
	    while (*options && strchr(" \t\n", *options))
	      ++options;
	    if (!*options)
	      return name_start;

	    char* val_end;
	    int val = strtol(options, &val_end, 10);
	    if (val_end == options)
	      return name_start;

	    if (*val_end == 'K')
	      {
		val *= 1024;
		++val_end;
	      }
	    else if (*val_end == 'M')
	      {
		val *= 1024 * 1024;
		++val_end;
	      }
	    else if (*val_end && !strchr(" \t\n,;", *val_end))
	      {
		return options;
	      }

	    options = val_end;

	    options_[name] = val;
	  }
      }
    return 0;
  }

  int
  option_map::get(const char* option, int def) const
  {
    std::map<std::string, int>::const_iterator it = options_.find(option);
    if (it == options_.end())
      // default value if not declared
      return def;
    else
      return it->second;
  }

  int
  option_map::operator[](const char* option) const
  {
    return get(option);
  }

  int
  option_map::set(const char* option, int val, int def)
  {
    int old = get(option, def);
    options_[option] = val;
    return old;
  }

  void
  option_map::set(const option_map& o)
  {
    for (std::map<std::string, int>::const_iterator it = o.options_.begin();
	 it != o.options_.end(); ++it)
      options_[it->first] = it->second;
  }

  int&
  option_map::operator[](const char* option)
  {
    return options_[option];
  }

  std::ostream&
  operator<<(std::ostream& os, const option_map& m)
  {
    for (std::map<std::string, int>::const_iterator it = m.options_.begin();
	 it != m.options_.end(); ++it)
      os << "\"" << it->first << "\" = " << it->second << std::endl;
    return os;
  }
};
