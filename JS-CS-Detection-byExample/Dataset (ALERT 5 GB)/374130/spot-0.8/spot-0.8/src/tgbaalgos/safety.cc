// Copyright (C) 2010, 2011 Laboratoire de Recherche et Développement de
// l'Epita (LRDE)
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

#include "safety.hh"
#include "misc/hash.hh"
#include <deque>

namespace spot
{
  bool
  is_guarantee_automaton(const tgba* aut, const scc_map* sm)
  {
    // Create an scc_map of the user did not give one to us.
    bool need_sm = !sm;
    if (need_sm)
      {
	scc_map* x = new scc_map(aut);
	x->build_map();
	sm = x;
      }

    bool result = true;

    unsigned scc_count = sm->scc_count();
    for (unsigned scc = 0; (scc < scc_count) && result; ++scc)
      {
	if (!sm->accepting(scc))
	  continue;
	// Accepting SCCs should have only one state.
	const std::list<const state*>& st = sm->states_of(scc);
	if (st.size() != 1)
	  {
	    result = false;
	    break;
	  }
	// The state should have only one transition that is a
	// self-loop labelled by true.
	const state* s = *st.begin();
	tgba_succ_iterator* it = aut->succ_iter(s);
	it->first();
	assert(!it->done());
	state* dest = it->current_state();
	bdd cond = it->current_condition();
	it->next();
	result = (!dest->compare(s)) && it->done() && (cond == bddtrue);
	dest->destroy();
	delete it;
      }

    // Free the scc_map if we created it.
    if (need_sm)
      delete sm;

    return result;
  }

  bool is_safety_mwdba(const tgba* aut)
  {
    typedef Sgi::hash_set<const state*,
			  state_ptr_hash, state_ptr_equal> seen_map;
    seen_map seen;		   // States already seen.
    std::deque<const state*> todo; // A queue of states yet to explore.

    {
      state* s = aut->get_init_state();
      todo.push_back(s);
      seen.insert(s);
    }

    bdd all_acc = aut->all_acceptance_conditions();

    bool all_accepting = true;
    while (all_accepting && !todo.empty())
      {
	const state* s = todo.front();
	todo.pop_front();

	tgba_succ_iterator* it = aut->succ_iter(s);
	for (it->first(); !it->done(); it->next())
	  {
	    bdd acc = it->current_acceptance_conditions();
	    if (acc != all_acc)
	      {
		all_accepting = false;
		break;
	      }
	    state* d = it->current_state();
	    if (seen.find(d) != seen.end())
	      {
		d->destroy();
	      }
	    else
	      {
		seen.insert(d);
		todo.push_back(d);
	      }
	  }
	delete it;
      }

    seen_map::const_iterator it = seen.begin();
    while (it != seen.end())
      {
	seen_map::const_iterator old = it;
	++it;
	(*old)->destroy();
      }

    return all_accepting;
  }



}
