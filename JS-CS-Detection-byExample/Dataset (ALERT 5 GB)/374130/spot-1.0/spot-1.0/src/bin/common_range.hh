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

#ifndef SPOT_BIN_COMMON_RANGE_HH
#define SPOT_BIN_COMMON_RANGE_HH


#define RANGE_DOC \
    { 0, 0, 0, 0, "RANGE may have one of the following forms: 'INT', " \
      "'INT..INT', or '..INT'.\nIn the latter case, the missing number " \
      "is assumed to be 1.", 0 }

struct range
{
  int min;
  int max;
};

range parse_range(const char* str);

#endif // SPOT_BIN_COMMON_RANGE_HH
