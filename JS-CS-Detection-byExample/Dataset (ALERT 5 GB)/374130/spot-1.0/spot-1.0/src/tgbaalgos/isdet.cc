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

#include "tgbaalgos/isdet.hh"
#include <set>
#include <deque>

namespace spot
{
  namespace
  {
    static
    unsigned
    count_nondet_states_aux(const tgba* aut, bool count = true)
    {
      unsigned res = 0;
      typedef std::set<const state*, state_ptr_less_than> seen_set;
      typedef std::deque<const state*> todo_list;

      seen_set seen;
      todo_list todo;
      {
	const state* s = aut->get_init_state();
	seen.insert(s);
	todo.push_back(s);
      }
      while (!todo.empty())
	{
	  const state* src = todo.front();
	  todo.pop_front();

	  tgba_succ_iterator* i = aut->succ_iter(src);
	  tgba_succ_iterator* j = aut->succ_iter(src);
	  unsigned in = 0;
	  bool nondeterministic = false;
	  for (i->first(); !i->done(); i->next())
	    {
	      // If we know the state is nondeterministic, just skip the
	      // test for the remaining transitions.  But don't break
	      // the loop, because we still have to record the
	      // destination states.
	      if (!nondeterministic)
		{
		  ++in;
		  // Move j to the transition that follows i.
		  j->first();
		  for (unsigned jn = 0; jn < in; ++jn)
		    j->next();
		  // Make sure transitions after i are not conflicting.
		  while (!j->done())
		    {
		      if ((i->current_condition() & j->current_condition())
			  != bddfalse)
			{
			  nondeterministic = true;
			  break;
			}
		      j->next();
		    }
		}
	      const state* dst = i->current_state();
	      if (seen.insert(dst).second)
		todo.push_back(dst);
	      else
		dst->destroy();
	    }
	  delete j;
	  delete i;
	  res += nondeterministic;
	  if (!count && nondeterministic)
	    break;
	}
      for (seen_set::const_iterator i = seen.begin(); i != seen.end();)
	{
	  const state* s = *i++;
	  s->destroy();
	}
      return res;
    }
  }

  unsigned
  count_nondet_states(const tgba* aut)
  {
    return count_nondet_states_aux(aut);
  }

  bool
  is_deterministic(const tgba* aut)
  {
    return !count_nondet_states_aux(aut, false);
  }
}
