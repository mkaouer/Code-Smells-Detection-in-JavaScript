// Copyright (C) 2009 Laboratoire de Recherche et Développement
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

#include <cassert>
#include "sabareachiter.hh"

namespace spot
{
  // saba_reachable_iterator
  //////////////////////////////////////////////////////////////////////

  saba_reachable_iterator::saba_reachable_iterator(const saba* a)
    : automata_(a)
  {
  }

  saba_reachable_iterator::~saba_reachable_iterator()
  {
    seen_map::const_iterator s = seen.begin();
    while (s != seen.end())
    {
      // Advance the iterator before deleting the "key" pointer.
      const saba_state* ptr = s->first;
      ++s;
      delete ptr;
    }
  }

  void
  saba_reachable_iterator::run()
  {
    int n = 0;
    int conjn = 0;

    start();
    saba_state* i = automata_->get_init_state();
    if (want_state(i))
      add_state(i);
    seen[i] = ++n;
    const saba_state* t;
    while ((t = next_state()))
      {
	assert(seen.find(t) != seen.end());
	int tn = seen[t];
	saba_succ_iterator* si = automata_->succ_iter(t);
	process_state(t, tn);
	for (si->first(); !si->done(); si->next())
	  {
            saba_state_conjunction* conj = si->current_conjunction();
            ++conjn;
            process_state_conjunction(t, tn, conj, conjn, si);

            for (conj->first(); !conj->done(); conj->next())
            {
              const saba_state* current = conj->current_state();
              seen_map::const_iterator s = seen.find(current);
              bool ws = want_state(current);

              if (s == seen.end())
	      {
		seen[current] = ++n;
		if (ws)
                {
                  add_state(current);
                  process_link(t, tn, current, n, conj, conjn, si);
                }
	      }
              else
	      {
		if (ws)
		  process_link(t, tn, s->first, s->second, conj, conjn, si);
		delete current;
	      }
            }
            delete conj;
	  }
	delete si;
      }
    end();
  }

  bool
  saba_reachable_iterator::want_state(const saba_state*) const
  {
    return true;
  }

  void
  saba_reachable_iterator::start()
  {
  }

  void
  saba_reachable_iterator::end()
  {
  }

  void
  saba_reachable_iterator::process_state(const saba_state*, int)
  {
  }

  void
  saba_reachable_iterator::
  process_state_conjunction(const saba_state*, int,
                            const saba_state_conjunction*,
                            int,
                            const saba_succ_iterator*)
  {
  }

  void
  saba_reachable_iterator::process_link(const saba_state*, int,
                                        const saba_state*, int,
                                        const saba_state_conjunction*,
                                        int,
                                        const saba_succ_iterator*)
  {
  }

  // saba_reachable_iterator_depth_first
  //////////////////////////////////////////////////////////////////////

  saba_reachable_iterator_depth_first::
    saba_reachable_iterator_depth_first(const saba* a)
      : saba_reachable_iterator(a)
  {
  }

  void
  saba_reachable_iterator_depth_first::add_state(const saba_state* s)
  {
    todo.push(s);
  }

  const saba_state*
  saba_reachable_iterator_depth_first::next_state()
  {
    if (todo.empty())
      return 0;
    const saba_state* s = todo.top();
    todo.pop();
    return s;
  }

  // saba_reachable_iterator_breadth_first
  //////////////////////////////////////////////////////////////////////

  saba_reachable_iterator_breadth_first::
    saba_reachable_iterator_breadth_first(const saba* a)
      : saba_reachable_iterator(a)
  {
  }

  void
  saba_reachable_iterator_breadth_first::add_state(const saba_state* s)
  {
    todo.push_back(s);
  }

  const saba_state*
  saba_reachable_iterator_breadth_first::next_state()
  {
    if (todo.empty())
      return 0;
    const saba_state* s = todo.front();
    todo.pop_front();
    return s;
  }

}
