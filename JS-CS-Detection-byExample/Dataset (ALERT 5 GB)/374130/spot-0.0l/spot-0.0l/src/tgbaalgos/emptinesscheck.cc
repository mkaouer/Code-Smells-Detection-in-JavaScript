// Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include "emptinesscheck.hh"
#include "tgba/bddprint.hh"
#include <queue>
#include <stdio.h>
#include <vector>
#include <map>

namespace spot
{
  typedef std::pair<const spot::state*, tgba_succ_iterator*> pair_state_iter;

  emptiness_check::connected_component::connected_component(int i)
  {
    index = i;
    condition = bddfalse;
  }

  bool
  emptiness_check::connected_component_set::has_state(const state* s) const
  {
    return states.find(s) != states.end();
  }


  emptiness_check::emptiness_check(const tgba* a)
    : aut_(a)
  {
  }

  emptiness_check::~emptiness_check()
  {
    // Free keys in H.
    hash_type::iterator i = h.begin();
    while (i != h.end())
      {
	// Advance the iterator before deleting the key.
	const state* s = i->first;
	++i;
	delete s;
      }
  }

  const state*
  emptiness_check::h_filt(const state* s) const
  {
    hash_type::const_iterator i = h.find(s);
    assert(i != h.end());
    if (s != i->first)
      delete s;
    return i->first;
  }

  void
  emptiness_check::remove_component(const state* from)
  {
    // Remove from H all states which are reachable from state FROM.

    // Stack of iterators towards states to remove.
    std::stack<tgba_succ_iterator*> to_remove;

    // Remove FROM itself, and prepare to remove its successors.
    // (FROM should be in H, otherwise it means all reachable
    // states from FROM have already been removed and there is no
    // point in calling remove_component.)
    hash_type::iterator hi = h.find(from);
    assert(hi->second != -1);
    hi->second = -1;
    tgba_succ_iterator* i = aut_->succ_iter(from);

    for (;;)
      {
	// Remove each destination of this iterator.
	for (i->first(); !i->done(); i->next())
	  {
	    state* s = i->current_state();
	    hash_type::iterator hi = h.find(s);
	    assert(hi != h.end());

	    if (hi->second != -1)
	      {
		hi->second = -1;
		to_remove.push(aut_->succ_iter(s));
	      }
	    delete s;
	  }
	delete i;
	if (to_remove.empty())
	  break;
	i = to_remove.top();
	to_remove.pop();
      }
  }

  bool
  emptiness_check::check()
  {
    // We use five main data in this algorithm:
    // * emptiness_check::root, a stack of strongly connected components (SCC),
    // * emptiness_check::h, a hash of all visited node, with their order,
    //   (it is called "Hash" in Couvreur's paper)
    // * arc, a stack of acceptance conditions between each of these SCC,
    std::stack<bdd> arc;
    // * num, the number of visited nodes.  Used to set the order of each
    //   visited node,
    int num = 1;
    // * todo, the depth-first search stack.  This holds pairs of the
    //   form (STATE, ITERATOR) where ITERATOR is a tgba_succ_iterator
    //   over the successors of STATE.  In our use, ITERATOR should
    //   always be freed when TODO is popped, but STATE should not because
    //   it is also used as a key in H.
    std::stack<pair_state_iter> todo;

    // Setup depth-first search from the initial state.
    {
      state* init = aut_->get_init_state();
      h[init] = 1;
      root.push(connected_component(1));
      arc.push(bddfalse);
      tgba_succ_iterator* iter = aut_->succ_iter(init);
      iter->first();
      todo.push(pair_state_iter(init, iter));
    }

    while (!todo.empty())
      {
	assert(root.size() == arc.size());

	// We are looking at the next successor in SUCC.
	tgba_succ_iterator* succ = todo.top().second;

	// If there is no more successor, backtrack.
	if (succ->done())
	  {
	    // We have explored all successors of state CURR.
	    const state* curr = todo.top().first;

	    // Backtrack TODO.
	    todo.pop();

	    // When backtracking the root of an SCC, we must also
	    // remove that SCC from the ARC/ROOT stacks.  We must
	    // discard from H all reachable states from this SCC.
	    hash_type::iterator i = h.find(curr);
	    assert(i != h.end());
	    assert(!root.empty());
	    if (root.top().index == i->second)
	      {
		assert(!arc.empty());
		arc.pop();
		root.pop();
		remove_component(curr);
	      }

	    delete succ;
	    // Do not delete CURR: it is a key in H.
	    continue;
	  }

	// We have a successor to look at.  Fetch the values
	// (destination state, acceptance conditions of the arc)
	// we are interested in...
	const state* dest = succ->current_state();
	bdd acc = succ->current_acceptance_conditions();
	// ... and point the iterator to the next successor, for
	// the next iteration.
	succ->next();
	// We do not need SUCC from now on.

	// Are we going to a new state?
	hash_type::iterator i = h.find(dest);
	if (i == h.end())
	  {
	    // Yes.  Number it, stack it, and register its successors
	    // for later processing.
	    h[dest] = ++num;
	    root.push(connected_component(num));
	    arc.push(acc);
	    tgba_succ_iterator* iter = aut_->succ_iter(dest);
	    iter->first();
	    todo.push(pair_state_iter(dest, iter));
	    continue;
	  }

	// We know the state exist.  Since a state can have several
	// representations (i.e., objects), make sure we delete
	// anything but the first one seen (the one used as key in
	// H).
	if (dest != i->first)
	  delete dest;

	// If we have reached a dead component.  Ignore it.
	if (i->second == -1)
	  continue;

	// Now this is the most interesting case.  We have reached a
	// state S1 which is already part of a non-dead SCC.  Any such
	// non-dead SCC has necessarily been crossed by our path to
	// this state: there is a state S2 in our path which belongs
	// to this SCC too.  We are going to merge all states between
	// this S1 and S2 into this SCC.
	//
	// This merge is easy to do because the order of the SCC in
	// ROOT is ascending: we just have to merge all SCCs from the
	// top of ROOT that have an index greater to the one of
	// the SCC of S2 (called the "threshold").
	int threshold = i->second;
	while (threshold < root.top().index)
	  {
	    assert(!root.empty());
	    assert(!arc.empty());
	    acc |= root.top().condition;
	    acc |= arc.top();
	    root.pop();
	    arc.pop();
	  }
	// Note that we do not always have
	//  threshold == root.top().index
	// after this loop, the SCC whose index is threshold might have
	// been merged with a lower SCC.

	// Accumulate all acceptance conditions into the merged SCC.
	root.top().condition |= acc;

	if (root.top().condition == aut_->all_acceptance_conditions())
	  {
	    // We have found an accepting SCC.
	    // Release all iterators in TODO.
	    while (!todo.empty())
	      {
		delete todo.top().second;
		todo.pop();
	      }
	    return false;
	  }
      }
    // This automaton recognize no word.
    return true;
  }


  std::ostream&
  emptiness_check::print_result(std::ostream& os, const tgba* restrict) const
  {
    os << "Prefix:" << std::endl;
    const bdd_dict* d = aut_->get_dict();
    for (state_sequence::const_iterator i_se = suffix.begin();
	 i_se != suffix.end(); ++i_se)
      {
	os << "  ";
	if (restrict)
	  {
	    const state* s = aut_->project_state(*i_se, restrict);
	    assert(s);
	    os << restrict->format_state(s) << std::endl;
	    delete s;
	  }
	else
	  {
	    os << aut_->format_state(*i_se) << std::endl;
	  }
      }
    os << "Cycle:" <<std::endl;
    for (cycle_path::const_iterator it = period.begin();
	 it != period.end(); ++it)
      {
	os << "    | " << bdd_format_set(d, it->second) << std::endl;
	os << "  ";
	if (restrict)
	  {
	    const state* s = aut_->project_state(it->first, restrict);
	    assert(s);
	    os << restrict->format_state(s) << std::endl;
	    delete s;
	  }
	else
	  {
	    os << aut_->format_state(it->first) << std::endl;
	  }
      }
    return os;
  }

  void
  emptiness_check::counter_example()
  {
    assert(!root.empty());
    assert(suffix.empty());

    int comp_size = root.size();
    // Transform the stack of connected component into an array.
    connected_component_set* scc = new connected_component_set[comp_size];
    for (int j = comp_size - 1; 0 <= j; --j)
      {
	scc[j].index = root.top().index;
	scc[j].condition = root.top().condition;
	root.pop();
      }
    assert(root.empty());

    // Build the set of states for all SCCs.
    for (hash_type::iterator i = h.begin(); i != h.end(); ++i)
      {
	int index = i->second;
	// Skip states from dead SCCs.
	if (index < 0)
	  continue;
	assert(index != 0);

	// Find the SCC this state belongs to.
	int j;
	for (j = 1; j < comp_size; ++j)
	  if (index < scc[j].index)
	    break;
	scc[j - 1].states.insert(i->first);
      }

    suffix.push_front(h_filt(aut_->get_init_state()));

    // We build a path trough each SCC in the stack.  For the
    // first SCC, the starting state is the initial state of the
    // automaton.  The destination state is the closest state
    // from the next SCC.  This destination state becomes the
    // starting state when building a path though the next SCC.
    for (int k = 0; k < comp_size - 1; ++k)
      {
	// FIFO for the breadth-first search.
	// (we are looking for the closest state in the next SCC.)
	std::deque<pair_state_iter> todo;

	// Record the father of each state, while performing the BFS.
	typedef std::map<const state*, const state*,
	                 state_ptr_less_than> father_map;
	father_map father;

	// Initial state of the BFS.
	const state* start = suffix.back();
	{
	  tgba_succ_iterator* i = aut_->succ_iter(start);
	  todo.push_back(pair_state_iter(start, i));
	}

	while (!todo.empty())
	  {
	    const state* src = todo.front().first;
	    tgba_succ_iterator* i = todo.front().second;
	    todo.pop_front();

	    for (i->first(); !i->done(); i->next())
	      {
		const state* dest = i->current_state();

		// Are we leaving this SCC?
		if (!scc[k].has_state(dest))
		  {
		    // If we have found a state in the next SCC.
		    // Unwind the path and populate SUFFIX.
		    if (scc[k+1].has_state(dest))
		      {
			state_sequence seq;

			seq.push_front(h_filt(dest));
			while (src->compare(start))
			  {
			    seq.push_front(src);
			    src = father[src];
			  }
			// Append SEQ to SUFFIX.
			suffix.splice(suffix.end(), seq);
			// Exit this BFS for this SCC.
			while (!todo.empty())
			  {
			    delete todo.front().second;
			    todo.pop_front();
			  }
			break;
		      }
		    // Restrict the BFS to state inside the SCC.
		    delete dest;
		    continue;
		  }

		dest = h_filt(dest);
		if (father.find(dest) == father.end())
		  {
		    todo.push_back(pair_state_iter(dest,
						   aut_->succ_iter(dest)));
		    father[dest] = src;
		  }
	      }
	    delete i;
	  }
      }

    accepting_path(scc[comp_size - 1], suffix.back(),
		   scc[comp_size - 1].condition);

    delete[] scc;
  }

  void
  emptiness_check::complete_cycle(const connected_component_set& scc,
				  const state* from,
				  const state* to)
  {
    if (from == to)
      return;

    // Records backlinks to parent state during the BFS.
    // (This also stores the propositions of this link.)
    std::map<const state*, state_proposition, state_ptr_less_than> father;

    // BFS queue.
    std::deque<pair_state_iter> todo;

    // Initial state.
    {
      tgba_succ_iterator* i = aut_->succ_iter(from);
      todo.push_back(pair_state_iter(from, i));
    }

    while (!todo.empty())
      {
	const state* src = todo.front().first;
	tgba_succ_iterator* i = todo.front().second;
	todo.pop_front();
	for (i->first(); !i->done(); i->next())
	  {
	    const state* dest = i->current_state();

	    // Do not escape this SCC.
	    if (!scc.has_state(dest))
	      {
		delete dest;
		continue;
	      }
	    dest = h_filt(dest);

	    bdd cond = i->current_condition();

	    // If we have reached our destination, unwind the path
	    // and populate PERIOD.
	    if (dest == to)
	      {
		cycle_path p;
		p.push_front(state_proposition(dest, cond));
		while (src != from)
		  {
		    const state_proposition& psi = father[src];
		    p.push_front(state_proposition(src, psi.second));
		    src = psi.first;
		  }
		period.splice(period.end(), p);

		// Exit the BFS, but release all iterators first.
		while (!todo.empty())
		  {
		    delete todo.front().second;
		    todo.pop_front();
		  }
		break;
	      }

	    // Common case: record backlinks and continue BFS.
	    todo.push_back(pair_state_iter(dest, aut_->succ_iter(dest)));
	    father[dest] = state_proposition(src, cond);
	  }
	delete i;
      }
  }


  namespace
  {
    struct triplet
    {
      const state* s;		// Current state.
      tgba_succ_iterator* iter;	// Iterator to successor of the current state.
      bdd acc;			// All acceptance conditions traversed by
				// the path so far.

      triplet (const state* s, tgba_succ_iterator* iter, bdd acc)
	: s(s), iter(iter), acc(acc)
      {
      }
    };

  }

  void
  emptiness_check::accepting_path(const connected_component_set& scc,
				  const state* start, bdd acc_to_traverse)
  {
    // State seen during the DFS.
    connected_component_set::set_type seen;
    // DFS stack.
    std::stack<triplet> todo;

    while (acc_to_traverse != bddfalse)
      {
	// Initial state.
	{
	  tgba_succ_iterator* i = aut_->succ_iter(start);
	  i->first();
	  todo.push(triplet(start, i, bddfalse));
	  seen.insert(start);
	}

	// The path being explored currently.
	cycle_path path;
	// The best path seen so far.
	cycle_path best_path;
	// The acceptance conditions traversed by BEST_PATH.
	bdd best_acc = bddfalse;

	while (!todo.empty())
	  {
	    tgba_succ_iterator* iter = todo.top().iter;
	    const state* s = todo.top().s;

	    // Nothing more to explore, backtrack.
	    if (iter->done())
	      {
		todo.pop();
		delete iter;
		seen.erase(s);
		path.pop_back();
		continue;
	      }

	    const state* dest = iter->current_state();

	    // We must not escape the current SCC.
	    if (!scc.has_state(dest))
	      {
		delete dest;
		iter->next();
		continue;
	      }

	    dest = h_filt(dest);
	    bdd acc = iter->current_acceptance_conditions() | todo.top().acc;
	    path.push_back(state_proposition(dest, iter->current_condition()));

	    // Advance iterator for next step.
	    iter->next();

	    if (seen.find(dest) == seen.end())
	      {
		// A new state: continue the DFS.
		tgba_succ_iterator* di = aut_->succ_iter(dest);
		di->first();
		todo.push(triplet(dest, di, acc));
		seen.insert(dest);
		continue;
	      }

	    // We have completed a full cycle.

	    // If we already have a best path, let see if the current
	    // one is better.
	    if (best_path.size())
	      {
		// When comparing the merits of two paths, only the
		// acceptance conditions we are trying the traverse
		// are important.
		bdd acc_restrict = acc & acc_to_traverse;
		bdd best_acc_restrict = best_acc & acc_to_traverse;

		// If the best path and the current one traverse the
		// same acceptance conditions, we keep the shorter
		// path.  Otherwise, we keep the path which has the
		// more acceptance conditions.
		if (best_acc_restrict == acc_restrict)
		  {
		    if (best_path.size() <= path.size())
		      continue;
		  }
		else
		  {
		    // `best_acc_restrict >> acc_restrict' is true
		    // when the set of acceptance conditions of
		    // best_acc_restrict is included in the set of
		    // acceptance conditions of acc_restrict.
		    //
		    // FIXME: It would be better to count the number
		    // of acceptance conditions.
		    if (bddtrue != (best_acc_restrict >> acc_restrict))
		      continue;
		  }
	      }

	    // The current path the best one.
	    best_path = path;
	    best_acc = acc;
	  }

	// Append our best path to the period.
	for (cycle_path::iterator it = best_path.begin();
	     it != best_path.end(); ++it)
	  period.push_back(*it);

	// Prepare to find another path for the remaining acceptance
	// conditions.
	acc_to_traverse -= best_acc;
	start = period.back().first;
      }

    // Complete the path so that it goes back to its beginning,
    // forming a cycle.
    complete_cycle(scc, start, suffix.back());
  }
}
