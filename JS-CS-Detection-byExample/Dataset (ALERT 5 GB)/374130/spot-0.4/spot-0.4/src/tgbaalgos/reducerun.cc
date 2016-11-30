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

#include "misc/hash.hh"
#include "emptiness.hh"
#include "tgba/tgba.hh"
#include "bfssteps.hh"
#include "reducerun.hh"

namespace spot
{
  namespace
  {
    typedef Sgi::hash_set<const state*,
			  state_ptr_hash, state_ptr_equal> state_set;
    class shortest_path: public bfs_steps
    {
    public:
      shortest_path(const tgba* a)
        : bfs_steps(a), target(0)
      {
      }

      ~shortest_path()
      {
        state_set::const_iterator i = seen.begin();
        while (i != seen.end())
          {
            const state* ptr = *i;
            ++i;
            delete ptr;
          }
      }

      void
      set_target(const state_set* t)
      {
        target = t;
      }

      const state*
      search(const state* start, tgba_run::steps& l)
      {
	return this->bfs_steps::search(filter(start), l);
      }

      const state*
      filter(const state* s)
      {
        state_set::const_iterator i = seen.find(s);
        if (i==seen.end())
          seen.insert(s);
        else
          {
            delete s;
            s = *i;
          }
        return s;
      }

      bool
      match(tgba_run::step&, const state* dest)
      {
        return target->find(dest) != target->end();
      }

    private:
      state_set seen;
      const state_set* target;
    };
  }

  tgba_run*
  reduce_run(const tgba* a, const tgba_run* org)
  {
    tgba_run* res = new tgba_run;
    state_set ss;
    shortest_path shpath(a);
    shpath.set_target(&ss);

    // We want to find a short segment of the original cycle that
    // contains all acceptance conditions.

    const state* segment_start; // The initial state of the segment.
    const state* segment_next; // The state immediately after the segment.

    // Start from the end of the original cycle, and rewind until all
    // acceptance conditions have been seen.
    bdd seen_acc = bddfalse;
    bdd all_acc = a->all_acceptance_conditions();
    tgba_run::steps::const_iterator seg = org->cycle.end();
    do
      {
        assert(seg != org->cycle.begin());
	--seg;
        seen_acc |= seg->acc;
      }
    while (seen_acc != all_acc);
    segment_start = seg->s;

    // Now go forward and ends the segment as soon as we have seen all
    // acceptance conditions, cloning it in our result along the way.
    seen_acc = bddfalse;
    do
      {
        assert(seg != org->cycle.end());
        seen_acc |= seg->acc;

	tgba_run::step st = { seg->s->clone(), seg->label, seg->acc };
	res->cycle.push_back(st);

	++seg;
      }
    while (seen_acc != all_acc);
    segment_next = seg == org->cycle.end() ? org->cycle.front().s : seg->s;

    // Close this cycle if needed, that is, compute a cycle going
    // from the state following the segment to its starting state.
    if (segment_start != segment_next)
      {
        ss.insert(segment_start);
        const state* s = shpath.search(segment_next->clone(), res->cycle);
        ss.clear();
        assert(s->compare(segment_start) == 0);
	(void)s;
      }

    // Compute the prefix: it's the shortest path from the initial
    // state of the automata to any state of the cycle.

    // Register all states from the cycle as target of the BFS.
    for (tgba_run::steps::const_iterator i = res->cycle.begin();
	 i != res->cycle.end(); ++i)
      ss.insert(i->s);

    const state* prefix_start = a->get_init_state();
    // There are two cases: either the initial state is already on
    // the cycle, or it is not.  If it is, we will have to rotate
    // the cycle so it begins on this position.  Otherwise we will shift
    // the cycle so it begins on the state that follows the prefix.
    // cycle_entry_point is that state.
    const state* cycle_entry_point;
    state_set::const_iterator ps = ss.find(prefix_start);
    if (ps != ss.end())
      {
	// The initial state is on the cycle.
	delete prefix_start;
	cycle_entry_point = *ps;
      }
    else
      {
	// This initial state is outside the cycle.  Compute the prefix.
        cycle_entry_point = shpath.search(prefix_start, res->prefix);
      }

    // Locate cycle_entry_point on the cycle.
    tgba_run::steps::iterator cycle_ep_it;
    for (cycle_ep_it = res->cycle.begin();
	 cycle_ep_it != res->cycle.end()
	   && cycle_entry_point->compare(cycle_ep_it->s); ++cycle_ep_it)
      continue;
    assert(cycle_ep_it != res->cycle.end());

    // Now shift the cycle so it starts on cycle_entry_point.
    res->cycle.splice(res->cycle.end(), res->cycle,
		      res->cycle.begin(), cycle_ep_it);

    return res;
  }
}
