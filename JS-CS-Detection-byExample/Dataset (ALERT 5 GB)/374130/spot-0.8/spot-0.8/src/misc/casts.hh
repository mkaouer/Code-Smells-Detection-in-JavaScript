// Copyright (C) 2011 Laboratoire de Recherche et DÃ©veloppement
// de l'Epita (LRDE).
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

#ifndef SPOT_MISC_CASTS_HH
#  define SPOT_MISC_CASTS_HH


// We usually write code like
//   subclass* i = down_cast<subclass*>(m);
//   assert(i);
//   ... use i ...
// When NDEBUG is set, the down_cast is a fast static_cast
// and the assert has no effect.
// Otherwise, the down_cast is a dynamic_cast and may return 0
// on error, which the assert catches.

#if NDEBUG
# define down_cast static_cast
#else
#  define down_cast dynamic_cast
#endif

#endif // SPOT_MISC_CASTS_HH
