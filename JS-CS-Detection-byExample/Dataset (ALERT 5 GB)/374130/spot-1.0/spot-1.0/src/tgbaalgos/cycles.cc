// Copyright (C) 2012 Laboratoire de Recherche et Developpement de
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

#include <iostream>
#include "cycles.hh"

namespace spot
{
  enumerate_cycles::enumerate_cycles(const scc_map& map)
    : aut_(map.get_aut()), sm_(map)
  {
  }

  void
  enumerate_cycles::nocycle(tagged_state x, tagged_state y)
  {
    // insert x in B(y)
    y->second.b.insert(x->first);
    // remove y from A(x)
    x->second.del.insert(y->first);
  }

  void
  enumerate_cycles::unmark(tagged_state y)
  {
    std::deque<tagged_state> q;
    q.push_back(y);

    while (!q.empty())
      {
	y = q.back();
	q.pop_back();

	y->second.mark = false;
	for (set_type::iterator i = y->second.b.begin();
	     i != y->second.b.end(); ++i)
	  {
	    tagged_state x = tags_.find(*i);
	    assert(x != tags_.end());
	    // insert y in A(x)
	    x->second.del.erase(y->first);

	    // unmark x recursively if marked
	    if (x->second.mark)
	      q.push_back(x);
	  }
	// empty B(y)
	y->second.b.clear();
      }
  }

  enumerate_cycles::tagged_state
  enumerate_cycles::tag_state(const state* s)
  {
    std::pair<tagged_state, bool> p =
      tags_.insert(std::make_pair(s, state_info()));
    if (!p.second)
      s->destroy();
    return p.first;
  }

  void
  enumerate_cycles::push_state(tagged_state ts)
  {
    ts->second.mark = true;

    dfs_entry e;
    e.ts = ts;
    e.succ = 0;
    e.f = false;
    dfs_.push_back(e);
  }

  void
  enumerate_cycles::run(unsigned scc)
  {
    bool keep_going = true;

    push_state(tag_state(sm_.one_state_of(scc)->clone()));

    while (keep_going && !dfs_.empty())
      {
	dfs_entry& cur = dfs_.back();
	if (cur.succ == 0)
	  {
	    cur.succ = aut_->succ_iter(cur.ts->first);
	    cur.succ->first();
	  }
	else
	  cur.succ->next();
	if (!cur.succ->done())
	  {
	    // Explore one successor.

	    // Ignore those that are not on the SCC, or destination
	    // that have been "virtually" deleted from A(v).
	    state* s = cur.succ->current_state();
	    if ((sm_.scc_of_state(s) != scc)
		|| (cur.ts->second.del.find(s) != cur.ts->second.del.end()))
	      {
		s->destroy();
		continue;
	      }

	    tagged_state w = tag_state(s);
	    if (!w->second.mark)
	      {
		push_state(w);
	      }
	    else if (!w->second.reach)
	      {
		keep_going = cycle_found(w->first);
		cur.f = true;
	      }
	    else
	      {
		nocycle(cur.ts, w);
	      }
	  }
	else
	  {
	    // No more successors.
	    bool f = cur.f;
	    tagged_state v = cur.ts;
	    delete cur.succ;

	    dfs_.pop_back();
	    if (f)
	      unmark(v);
	    v->second.reach = true;

	    // Update the predecessor in the stack if there is one.
	    if (!dfs_.empty())
	      {
		if (f)
		  dfs_.back().f = true;
		else
		  nocycle(dfs_.back().ts, v);
	      }
	  }
      }

    // Purge the dfs_ stack, in case we aborted because cycle_found()
    // returned false.
    while (!dfs_.empty())
      {
	delete dfs_.back().succ;
	dfs_.pop_back();
      }

    hash_type::iterator i = tags_.begin();
    while (i != tags_.end())
      {
	hash_type::iterator old = i;
	++i;
	old->first->destroy();
      }
    tags_.clear();
    dfs_.clear();
  }

  bool
  enumerate_cycles::cycle_found(const state* start)
  {
    dfs_stack::const_iterator i = dfs_.begin();
    while (i->ts->first != start)
      ++i;
    do
      {
	std::cout << aut_->format_state(i->ts->first) << " ";
	++i;
      }
    while (i != dfs_.end());
    std::cout << "\n";
    return true;
  }


}
