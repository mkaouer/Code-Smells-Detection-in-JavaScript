// Copyright (C) 2003, 2004, 2006, 2007 Laboratoire d'Informatique de
// Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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

#include <bdd.h>
#include <cassert>
#include "bddalloc.hh"

namespace spot
{
  bool bdd_allocator::initialized = false;

  bdd_allocator::bdd_allocator()
  {
    initialize();
    lvarnum = bdd_varnum();
    fl.push_front(pos_lenght_pair(0, lvarnum));
  }

  void
  bdd_allocator::initialize()
  {
    if (initialized)
      return;
    initialized = true;
    // Buddy might have been initialized by a third-party library.
    if (bdd_isrunning())
      return;
    // The values passed to bdd_init should depends on the problem
    // the library is solving.  It would be nice to allow users
    // to tune this.  By the meantime, we take the typical values
    // for large examples advocated by the BuDDy manual.
    bdd_init(1000000, 10000);
    bdd_setvarnum(2);
  }

  void
  bdd_allocator::extvarnum(int more)
  {
    int varnum = bdd_varnum();
    // If varnum has been extended from another allocator (or
    // externally), use the new variables.
    if (lvarnum < varnum)
      {
	more -= varnum - lvarnum;
	lvarnum = varnum;
      }
    // If we still need more variable, do allocate them.
    if (more > 0)
      {
	bdd_extvarnum(more);
	varnum += more;
	lvarnum = varnum;
      }
  }

  int
  bdd_allocator::allocate_variables(int n)
  {
    return register_n(n);
  }

  void
  bdd_allocator::release_variables(int base, int n)
  {
    release_n(base, n);
  }

  int
  bdd_allocator::extend(int n)
  {
    // If we already have some free variable at the end
    // of the variable space, allocate just the difference.
    if (!fl.empty() && fl.back().first + fl.back().second == lvarnum)
      {
	int res = fl.back().first;
	int endvar = fl.back().second;
	assert(n > endvar);
	extvarnum(n - endvar);
	fl.pop_back();
	return res;
      }
    else
      {
	// Otherwise, allocate as much variables as we need.
	int res = lvarnum;
	extvarnum(n);
	return res;
      }
  }
}
