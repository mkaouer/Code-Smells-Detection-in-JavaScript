// Copyright (C) 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include <set>
#include <map>
#include <deque>
#include <sstream>
#include "misc/hash.hh"
#include "tgbaalgos/powerset.hh"
#include "bdd.h"

namespace spot
{
  tgba_explicit*
  tgba_powerset(const tgba* aut)
  {
    typedef Sgi::hash_set<const state*, state_ptr_hash,
                          state_ptr_equal> state_set;
    typedef std::set<const state*, state_ptr_less_than> power_state;
    typedef std::map<power_state, std::string> power_set;
    typedef std::deque<power_state> todo_list;

    power_set seen;
    todo_list todo;
    tgba_explicit* res = new tgba_explicit(aut->get_dict());

    state_set states;

    {
      power_state ps;
      state* s = aut->get_init_state();
      states.insert(s);
      ps.insert(s);
      todo.push_back(ps);
      seen[ps] = "1";
    }

    unsigned state_num = 1;

    while (!todo.empty())
      {
	power_state src = todo.front();
	todo.pop_front();
	// Compute all variables occurring on outgoing arcs.
	bdd all_vars = bddtrue;
	power_state::const_iterator i;

	for (i = src.begin(); i != src.end(); ++i)
	  all_vars &= aut->support_variables(*i);

	// Compute all possible combinations of these variables.
	bdd all_conds = bddtrue;
	while (all_conds != bddfalse)
	  {
	    bdd cond = bdd_satoneset(all_conds, all_vars, bddtrue);
	    all_conds -= cond;

	    // Construct the set of all states reachable via COND.
	    power_state dest;
	    for (i = src.begin(); i != src.end(); ++i)
	      {
		tgba_succ_iterator *si = aut->succ_iter(*i);
		for (si->first(); !si->done(); si->next())
		  if ((cond >> si->current_condition()) == bddtrue)
		    {
		      const state* s = si->current_state();
		      state_set::const_iterator i = states.find(s);
		      if (i != states.end())
			{
			  delete s;
			  s = *i;
			}
		      else
			{
			  states.insert(s);
			}
		      dest.insert(s);
		    }
		delete si;
	      }
	    if (dest.empty())
	      continue;

	    // Add that transition.
	    power_set::const_iterator i = seen.find(dest);
	    std::string dest_name;
	    if (i != seen.end())
	      {
		dest_name = i->second;
	      }
	    else
	      {
		std::ostringstream str;
		str << ++state_num;
		dest_name = str.str();
		seen[dest] = dest_name;
		todo.push_back(dest);
	      }
	    tgba_explicit::transition* t =
	      res->create_transition(seen[src], dest_name);
	    res->add_conditions(t, cond);
	  }
      }
    res->merge_transitions();

    // Release all states.
    state_set::const_iterator i = states.begin();
    while (i != states.end())
      {
	// Advance the iterator before deleting the key.
	const state* s = *i;
	++i;
	delete s;
      }

    return res;
  }
}
