// Copyright (C) 2010, 2011  Laboratoire de Recherche et Développement de
// l'Epita (LRDE).
// Copyright (C) 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include "ce.hh"
#include "tgbaalgos/bfssteps.hh"
#include "misc/hash.hh"

namespace spot
{
  namespace
  {
    typedef Sgi::hash_set<const state*,
			  state_ptr_hash, state_ptr_equal> state_set;
    class shortest_path: public bfs_steps
    {
    public:
      shortest_path(const state_set* t,
		    const couvreur99_check_status* ecs,
		    couvreur99_check_result* r)
        : bfs_steps(ecs->aut), target(t), ecs(ecs), r(r)
      {
      }

      const state*
      search(const state* start, tgba_run::steps& l)
      {
	return this->bfs_steps::search(filter(start), l);
      }

      const state*
      filter(const state* s)
      {
	r->inc_ars_prefix_states();
	numbered_state_heap::state_index_p sip = ecs->h->find(s);
	// Ignore unknown states ...
	if (!sip.first)
	  {
	    s->destroy();
	    return 0;
	  }
	// ... as well as dead states.
	if (*sip.second == -1)
	  return 0;
	return sip.first;
      }

      bool
      match(tgba_run::step&, const state* dest)
      {
        return target->find(dest) != target->end();
      }

    private:
      state_set seen;
      const state_set* target;
      const couvreur99_check_status* ecs;
      couvreur99_check_result* r;
    };
  }

  couvreur99_check_result::couvreur99_check_result
  (const couvreur99_check_status* ecs, option_map o)
    : emptiness_check_result(ecs->aut, o), ecs_(ecs)
  {
  }

  unsigned
  couvreur99_check_result::acss_states() const
  {
    unsigned count = 0;
    int scc_root = ecs_->root.top().index;

    numbered_state_heap_const_iterator* i = ecs_->h->iterator();
    for (i->first(); !i->done(); i->next())
      if (i->get_index() >= scc_root)
	++count;
    delete i;
    return count;
  }

  tgba_run*
  couvreur99_check_result::accepting_run()
  {
    run_ = new tgba_run;

    assert(!ecs_->root.empty());

    // Compute an accepting cycle.
    accepting_cycle();

    // Compute the prefix: it's the shortest path from the initial
    // state of the automata to any state of the cycle.

    // Register all states from the cycle as target of the BFS.
    state_set ss;
    for (tgba_run::steps::const_iterator i = run_->cycle.begin();
	 i != run_->cycle.end(); ++i)
      ss.insert(i->s);
    shortest_path shpath(&ss, ecs_, this);

    const state* prefix_start = ecs_->aut->get_init_state();
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
	prefix_start->destroy();
	cycle_entry_point = *ps;
      }
    else
      {
	// This initial state is outside the cycle.  Compute the prefix.
        cycle_entry_point = shpath.search(prefix_start, run_->prefix);
      }

    // Locate cycle_entry_point on the cycle.
    tgba_run::steps::iterator cycle_ep_it;
    for (cycle_ep_it = run_->cycle.begin();
	 cycle_ep_it != run_->cycle.end()
	   && cycle_entry_point->compare(cycle_ep_it->s); ++cycle_ep_it)
      continue;
    assert(cycle_ep_it != run_->cycle.end());

    // Now shift the cycle so it starts on cycle_entry_point.
    run_->cycle.splice(run_->cycle.end(), run_->cycle,
		       run_->cycle.begin(), cycle_ep_it);

    return run_;
  }

  void
  couvreur99_check_result::accepting_cycle()
  {
    bdd acc_to_traverse = ecs_->aut->all_acceptance_conditions();
    // Compute an accepting cycle using successive BFS that are
    // restarted from the point reached after we have discovered a
    // transition with a new acceptance conditions.
    //
    // This idea is taken from Product<T>::findWitness in LBTT 1.1.2,
    // which in turn is probably inspired from
    // @Article{	  latvala.00.fi,
    //   author	= {Timo Latvala and Keijo Heljanko},
    //   title		= {Coping With Strong Fairness},
    //   journal	= {Fundamenta Informaticae},
    //   year		= {2000},
    //   volume	= {43},
    //   number	= {1--4},
    //   pages		= {1--19},
    //   publisher	= {IOS Press}
    // }
    const state* substart = ecs_->cycle_seed;
    do
      {
	struct scc_bfs: bfs_steps
	{
	  const couvreur99_check_status* ecs;
	  couvreur99_check_result* r;
	  bdd& acc_to_traverse;
	  int scc_root;

	  scc_bfs(const couvreur99_check_status* ecs,
		  couvreur99_check_result* r, bdd& acc_to_traverse)
	    : bfs_steps(ecs->aut), ecs(ecs), r(r),
	      acc_to_traverse(acc_to_traverse),
	      scc_root(ecs->root.top().index)
	  {
	  }

	  virtual const state*
	  filter(const state* s)
	  {
	    numbered_state_heap::state_index_p sip = ecs->h->find(s);
	    // Ignore unknown states.
	    if (!sip.first)
	      {
		s->destroy();
		return 0;
	      }
	    // Stay in the final SCC.
	    if (*sip.second < scc_root)
	      return 0;
	    r->inc_ars_cycle_states();
	    return sip.first;
	  }

	  virtual bool
	  match(tgba_run::step& st, const state* s)
	  {
	    bdd less_acc = acc_to_traverse - st.acc;
	    if (less_acc != acc_to_traverse
		|| (acc_to_traverse == bddfalse
		    && s == ecs->cycle_seed))
	      {
		acc_to_traverse = less_acc;
		return true;
	      }
	    return false;
	  }

	} b(ecs_, this, acc_to_traverse);

	substart = b.search(substart, run_->cycle);
	assert(substart);
      }
    while (acc_to_traverse != bddfalse || substart != ecs_->cycle_seed);
  }

  void
  couvreur99_check_result::print_stats(std::ostream& os) const
  {
    ecs_->print_stats(os);
    // FIXME: This is bogusly assuming run_ exists.  (Even if we
    // created it, the user might have deleted it.)
    os << run_->prefix.size() << " states in run_->prefix" << std::endl;
    os << run_->cycle.size() << " states in run_->cycle" << std::endl;
  }

}
