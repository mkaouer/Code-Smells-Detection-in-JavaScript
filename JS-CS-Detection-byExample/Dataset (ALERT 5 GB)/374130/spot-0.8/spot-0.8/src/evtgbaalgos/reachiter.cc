// Copyright (C) 2011 Laboratoire de Recherche et Developpement de
// l'Epita (LRDE).
// Copyright (C) 2003, 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include <cassert>
#include "reachiter.hh"

namespace spot
{
  // evtgba_reachable_iterator
  //////////////////////////////////////////////////////////////////////

  evtgba_reachable_iterator::evtgba_reachable_iterator(const evtgba* a)
    : automata_(a)
  {
  }

  evtgba_reachable_iterator::~evtgba_reachable_iterator()
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
  evtgba_reachable_iterator::run()
  {
    int n = 0;

    evtgba_iterator* i =  automata_->init_iter();
    for (i->first(); !i->done(); i->next())
      {
	const state* dest = i->current_state();
	add_state(dest);
	seen[dest] = ++n;
      }
    delete i;
    start(n);

    const state* t;
    while ((t = next_state()))
      {
	assert(seen.find(t) != seen.end());
	int tn = seen[t];
	evtgba_iterator* si = automata_->succ_iter(t);
	process_state(t, tn, si);
	for (si->first(); !si->done(); si->next())
	  {
	    const state* current = si->current_state();
	    seen_map::const_iterator s = seen.find(current);
	    if (s == seen.end())
	      {
		seen[current] = ++n;
		add_state(current);
		process_link(tn, n, si);
	      }
	    else
	      {
		process_link(tn, s->second, si);
		current->destroy();
	      }
	  }
	delete si;
      }
    end();
  }

  void
  evtgba_reachable_iterator::start(int)
  {
  }

  void
  evtgba_reachable_iterator::end()
  {
  }

  void
  evtgba_reachable_iterator::process_state(const state*, int, evtgba_iterator*)
  {
  }

  void
  evtgba_reachable_iterator::process_link(int, int, const evtgba_iterator*)
  {
  }

  // evtgba_reachable_iterator_depth_first
  //////////////////////////////////////////////////////////////////////

  evtgba_reachable_iterator_depth_first::
    evtgba_reachable_iterator_depth_first(const evtgba* a)
      : evtgba_reachable_iterator(a)
  {
  }

  void
  evtgba_reachable_iterator_depth_first::add_state(const state* s)
  {
    todo.push(s);
  }

  const state*
  evtgba_reachable_iterator_depth_first::next_state()
  {
    if (todo.empty())
      return 0;
    const state* s = todo.top();
    todo.pop();
    return s;
  }

  // evtgba_reachable_iterator_breadth_first
  //////////////////////////////////////////////////////////////////////

  evtgba_reachable_iterator_breadth_first::
    evtgba_reachable_iterator_breadth_first(const evtgba* a)
      : evtgba_reachable_iterator(a)
  {
  }

  void
  evtgba_reachable_iterator_breadth_first::add_state(const state* s)
  {
    todo.push_back(s);
  }

  const state*
  evtgba_reachable_iterator_breadth_first::next_state()
  {
    if (todo.empty())
      return 0;
    const state* s = todo.front();
    todo.pop_front();
    return s;
  }

}
