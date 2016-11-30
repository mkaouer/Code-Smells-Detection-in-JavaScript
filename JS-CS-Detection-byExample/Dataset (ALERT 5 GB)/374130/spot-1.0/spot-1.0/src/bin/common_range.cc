// -*- coding: utf-8 -*-
// Copyright (C) 2012 Laboratoire de Recherche et Développement de
// l'Epita (LRDE).
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

#include "common_sys.hh"
#include "error.h"

#include "common_range.hh"
#include <iostream>
#include <cstdlib>

range
parse_range(const char* str)
{
  range res;

  // The range should have the form INT..INT or INT:INT, with
  // "42" standing for "42..42", and "..42" meaning "1..42".
  char* end;
  res.min = strtol(str, &end, 10);
  if (end == str)
    {
      // No leading number.  It's OK as long as the string is not
      // empty.
      if (!*end)
	error(1, 0, "invalid empty range");
      res.min = 1;
    }
  if (!*end)
    {
      // Only one number.
      res.max = res.min;
    }
  else
    {
      // Skip : or ..
      if (end[0] == ':')
	++end;
      else if (end[0] == '.' && end[1] == '.')
	end += 2;

      // Parse the next integer.
      char* end2;
      res.max = strtol(end, &end2, 10);
      if (end == end2)
	error(1, 0, "invalid range '%s' (missing end?)", str);
      if (*end2)
	error(1, 0, "invalid range '%s' (trailing garbage?)", str);
    }

  if (res.min < 0 || res.max < 0)
    error(1, 0, "invalid range '%s': values must be positive", str);

  return res;
}
