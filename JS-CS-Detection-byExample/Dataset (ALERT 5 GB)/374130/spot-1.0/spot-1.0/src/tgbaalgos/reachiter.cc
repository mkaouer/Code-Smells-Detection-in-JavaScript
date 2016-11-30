// Copyright (C) 2009, 2011 Laboratoire de Recherche et D�veloppement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
// d�partement Syst�mes R�partis Coop�ratifs (SRC), Universit� Pierre
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

#include <cassert>
#include "reachiter.hh"

namespace spot
{
  // tgba_reachable_iterator
  //////////////////////////////////////////////////////////////////////

  tgba_reachable_iterator::tgba_reachable_iterator(const tgba* a)
    : aut_(a)
  {
  }

  tgba_reachable_iterator::~tgba_reachable_iterator()
  {
    seen_map::const_iterator s = seen.begin();
    while (s != seen.end())
      {
	// Advance the iterator before deleting the "key" pointer.
	const state* ptr = s->first;
	++s;
	ptr->destroy();
      }
  }

  void
  tgba_reachable_iterator::run()
  {
    int n = 0;
    start();
    state* i = aut_->get_init_state();
    if (want_state(i))
      add_state(i);
    seen[i] = ++n;
    const state* t;
    while ((t = next_state()))
      {
	assert(seen.find(t) != seen.end());
	int tn = seen[t];
	tgba_succ_iterator* si = aut_->succ_iter(t);
	process_state(t, tn, si);
	for (si->first(); !si->done(); si->next())
	  {
	    const state* current = si->current_state();
	    seen_map::const_iterator s = seen.find(current);
	    bool ws = want_state(current);
	    if (s == seen.end())
	      {
		seen[current] = ++n;
		if (ws)
		  {
		    add_state(current);
		    process_link(t, tn, current, n, si);
		  }
	      }
	    else
	      {
		if (ws)
		  process_link(t, tn, s->first, s->second, si);
		current->destroy();
	      }
	  }
	delete si;
      }
    end();
  }

  bool
  tgba_reachable_iterator::want_state(const state*) const
  {
    return true;
  }

  void
  tgba_reachable_iterator::start()
  {
  }

  void
  tgba_reachable_iterator::end()
  {
  }

  void
  tgba_reachable_iterator::process_state(const state*, int,
					 tgba_succ_iterator*)
  {
  }

  void
  tgba_reachable_iterator::process_link(const state*, int,
					const state*, int,
					const tgba_succ_iterator*)
  {
  }

  // tgba_reachable_iterator_depth_first
  //////////////////////////////////////////////////////////////////////

  tgba_reachable_iterator_depth_first::
    tgba_reachable_iterator_depth_first(const tgba* a)
      : tgba_reachable_iterator(a)
  {
  }

  void
  tgba_reachable_iterator_depth_first::add_state(const state* s)
  {
    todo.push(s);
  }

  const state*
  tgba_reachable_iterator_depth_first::next_state()
  {
    if (todo.empty())
      return 0;
    const state* s = todo.top();
    todo.pop();
    return s;
  }

  // tgba_reachable_iterator_breadth_first
  //////////////////////////////////////////////////////////////////////

  tgba_reachable_iterator_breadth_first::
    tgba_reachable_iterator_breadth_first(const tgba* a)
      : tgba_reachable_iterator(a)
  {
  }

  void
  tgba_reachable_iterator_breadth_first::add_state(const state* s)
  {
    todo.push_back(s);
  }

  const state*
  tgba_reachable_iterator_breadth_first::next_state()
  {
    if (todo.empty())
      return 0;
    const state* s = todo.front();
    todo.pop_front();
    return s;
  }

}
