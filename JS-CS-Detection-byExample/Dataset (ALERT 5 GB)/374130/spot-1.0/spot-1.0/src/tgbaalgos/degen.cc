// Copyright (C) 2012 Laboratoire de Recherche et
// Développement de l'Epita.
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


#include "degen.hh"
#include "tgba/tgbaexplicit.hh"
#include "misc/hash.hh"
#include "misc/hashfunc.hh"
#include "ltlast/constant.hh"
#include <deque>
#include <vector>

namespace spot
{
  namespace
  {
    // A state in the degenalized automaton corresponds to a state in
    // the TGBA associated to a level.  The level is just an index in
    // the list of acceptance sets.
    typedef std::pair<const state*, unsigned> degen_state;

    struct degen_state_hash
    {
      size_t
      operator()(const degen_state& s) const
      {
	return s.first->hash() & wang32_hash(s.second);
      }
    };

    struct degen_state_equal
    {
      bool
      operator()(const degen_state& left,
		 const degen_state& right) const
      {
	if (left.second != right.second)
	  return false;
	return left.first->compare(right.first) == 0;
      }
    };

    // Associate the degeneralized state to its number.
    typedef Sgi::hash_map<degen_state, int,
			  degen_state_hash, degen_state_equal> ds2num_map;

    // Queue of state to be processed.
    typedef std::deque<degen_state> queue_t;

    // Memory management for the input states.
    class unicity_table
    {
      typedef Sgi::hash_set<const state*,
			    state_ptr_hash, state_ptr_equal> uniq_set;
      uniq_set m;
    public:
      const state* operator()(const state* s)
      {
	uniq_set::const_iterator i = m.find(s);
	if (i == m.end())
	  {
	    m.insert(s);
	    return s;
	  }
	else
	  {
	    s->destroy();
	    return *i;
	  }
      }

      ~unicity_table()
      {
	for (uniq_set::iterator i = m.begin(); i != m.end();)
	  {
	    // Advance the iterator before destroying its key.  This
	    // avoid issues with old g++ implementations.
	    uniq_set::iterator old = i++;
	    (*old)->destroy();
	  }
      }
    };

    // Acceptance set common to all outgoing transitions of some state.
    class outgoing_acc
    {
      const tgba* a_;
      typedef std::pair<bdd, bdd> cache_entry;
      typedef Sgi::hash_map<const state*, cache_entry,
			    state_ptr_hash, state_ptr_equal> cache_t;
      cache_t cache_;

    public:
      outgoing_acc(const tgba* a): a_(a)
      {
      }

      cache_t::const_iterator fill_cache(const state* s)
      {
	bdd common = a_->all_acceptance_conditions();
	bdd union_ = bddfalse;
	tgba_succ_iterator* it = a_->succ_iter(s);
	for (it->first(); !it->done(); it->next())
	  {
	    bdd set = it->current_acceptance_conditions();
	    common &= set;
	    union_ |= set;
	  }
	delete it;
	cache_entry e(common, union_);
	return cache_.insert(std::make_pair(s, e)).first;
      }

      // Intersection of all outgoing acceptance sets
      bdd common_acc(const state* s)
      {
	cache_t::const_iterator i = cache_.find(s);
	if (i == cache_.end())
	  i = fill_cache(s);
	return i->second.first;
      }

      // Union of all outgoing acceptance sets
      bdd union_acc(const state* s)
      {
	cache_t::const_iterator i = cache_.find(s);
	if (i == cache_.end())
	  i = fill_cache(s);
	return i->second.second;
      }
    };
  }

  sba*
  degeneralize(const tgba* a)
  {
    bdd_dict* dict = a->get_dict();

    // The result (degeneralized) automaton uses numbered state.
    sba_explicit_number* res = new sba_explicit_number(dict);
    dict->register_all_variables_of(a, res);
    // FIXME: unregister acceptance conditions.

    // Invent a new acceptance set for the degeneralized automaton.
    int accvar =
      dict->register_acceptance_variable(ltl::constant::true_instance(), res);
    bdd degen_acc = bdd_ithvar(accvar);
    res->set_acceptance_conditions(degen_acc);

    // Create an order of acceptance conditions.  Each entry in this
    // vector correspond to an acceptance set.  Each index can
    // be used as a level in degen_state to indicate the next expected
    // acceptance set.  Level order.size() is a special level used to
    // denote accepting states.
    std::vector<bdd> order;
    {
      // The order is arbitrary, but it turns out that using push_back
      // instead of push_front often gives better results because
      // acceptance sets at the beginning if the cycle are more often
      // used in the automaton.  (This surprising fact is probably
      // related to the order in which we declare the BDD variables
      // during the translation.)
      bdd all = a->all_acceptance_conditions();
      while (all != bddfalse)
	{
	  bdd next = bdd_satone(all);
	  all -= next;
	  order.push_back(next);
	}
    }

    outgoing_acc outgoing(a);

    // Make sure we always use the same pointer for identical states
    // from the input automaton.
    unicity_table uniq;

    // These maps make it possible to convert degen_state to number
    // and vice-versa.
    ds2num_map ds2num;

    // This map is used to find transitions that go to the same
    // destination with the same acceptance.  The integer key is
    // (dest*2+acc) where dest is the destination state number, and
    // acc is 1 iff the transition is accepting.  The source
    // is always that of the current iteration.
    typedef std::map<int, state_explicit_number::transition*> tr_cache_t;
    tr_cache_t tr_cache;

    queue_t todo;

    const state* s0 = uniq(a->get_init_state());
    degen_state s(s0, 0);

    // As an heuristic, if the initial state has accepting self-loops,
    // start the degeneralization on the accepting level.
    {
      bdd all = a->all_acceptance_conditions();
      tgba_succ_iterator* it = a->succ_iter(s0);
      for (it->first(); !it->done(); it->next())
	{
	  // Look only for transitions that are accepting.
	  if (all != it->current_acceptance_conditions())
	    continue;
	  // Look only for self-loops.
	  const state* dest = uniq(it->current_state());
	  if (dest == s0)
	    {
	      // The initial state has an accepting self-loop.
	      s.second = order.size();
	      break;
	    }
	}
      delete it;
    }

    ds2num[s] = 0;
    todo.push_back(s);

    while (!todo.empty())
      {
	s = todo.front();
	todo.pop_front();
	int src = ds2num[s];
	unsigned slevel = s.second;

	// If we have a state on the last level, it should be accepting.
	bool is_acc = slevel == order.size();
	// On the accepting level, start again from level 0.
	if (is_acc)
	  slevel = 0;

	tgba_succ_iterator* i = a->succ_iter(s.first);
	for (i->first(); !i->done(); i->next())
	  {
	    degen_state d(uniq(i->current_state()), 0);

	    // The old level is slevel.  What should be the new one?
	    bdd acc = i->current_acceptance_conditions();
	    bdd otheracc = outgoing.common_acc(d.first);

	    if (is_acc)
	      {
		// Ignore the last expected acceptance set (the value of
		// *prev below) if it is common to all other outgoing
		// transitions (of the current state) AND if it is not
		// used by any outgoing transition of the destination
		// state.
		//
		// 1) It's correct to do that, because this acceptance
		//    set is common to other outgoing transitions.
		//    Therefore if we make a cycle to this state we
		//    will eventually see that acceptance set thanks
		//    to the "pulling" of the common acceptance sets
		//    of the destination state (d.first).
		//
		// 2) It's also desirable because it makes the
		//    degeneralization idempotent (up to a renaming of
		//    states).  Consider the following automaton where
		//    1 is initial and => marks accepting transitions:
		//    1=>1, 1=>2, 2->2, 2->1 This is already an SBA,
		//    with 1 as accepting state.  However if you try
		//    degeralize it without ignoring *prev, you'll get
		//    two copies of states 2, depending on whether we
		//    reach it using 1=>2 or from 2->2.  If this
		//    example was not clear, uncomment this following
		//    "if" block, and play with the "degenid.test"
		//    test case.
		//
		// 3) Ignoring all common acceptance sets would also
		//    be correct, but it would make the
		//    degeneralization produce larger automata in some
		//    cases.  The current condition to ignore only one
		//    acceptance set if is this not used by the next
		//    state is a heuristic that is compatible with
		//    point 2) above while not causing more states to
		//    be generated in our benchmark of 188 formulae
		//    from the literature.
                if (!order.empty())
                  {
                    unsigned prev = order.size() - 1;
                    bdd common = outgoing.common_acc(s.first);
                    if (bdd_implies(order[prev], common))
                      {
                        bdd u = outgoing.union_acc(d.first);
                        if (!bdd_implies(order[prev], u))
                          acc -= order[prev];
                      }
                  }
	      }
	    // A transition in the SLEVEL acceptance set should
	    // be directed to the next acceptance set.  If the
	    // current transition is also in the next acceptance
	    // set, then go to the one after, etc.
	    //
	    // See Denis Oddoux's PhD thesis for a nice
	    // explanation (in French).
	    // @PhDThesis{    oddoux.03.phd,
	    //   author     = {Denis Oddoux},
	    //   title      = {Utilisation des automates alternants pour un
	    //                model-checking efficace des logiques
	    //                temporelles lin{\'e}aires.},
	    //   school     = {Universit{\'e}e Paris 7},
	    //   year       = {2003},
	    //   address= {Paris, France},
	    //   month      = {December}
	    // }
	    unsigned next = slevel;
	    // Consider both the current acceptance sets, and the
	    // acceptance sets common to the outgoing transitions of
	    // the destination state.
	    acc |= otheracc;
	    while (next < order.size() && bdd_implies(order[next], acc))
	      ++next;

	    d.second = next;

	    // Have we already seen this destination?
	    int dest;
	    ds2num_map::const_iterator di = ds2num.find(d);
	    if (di != ds2num.end())
	      {
		dest = di->second;
	      }
	    else
	      {
		dest = ds2num.size();
		ds2num[d] = dest;
		todo.push_back(d);
	      }

	    state_explicit_number::transition*& t =
	      tr_cache[dest * 2 + is_acc];

	    if (t == 0)
	      {
		// Actually create the transition.
		t = res->create_transition(src, dest);
		t->condition = i->current_condition();
		// If the source state is accepting, we have to put
		// degen_acc on all outgoing transitions.  (We are still
		// building a TGBA; we only assure that it can be used as
		// an SBA.)
		t->acceptance_conditions = is_acc ? degen_acc : bddfalse;
	      }
	    else
	      {
		t->condition |= i->current_condition();
	      }
	  }
	delete i;
	tr_cache.clear();
      }
    return res;
  }
}
