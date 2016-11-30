// -*- coding: utf-8 -*-
// Copyright (C) 2012, 2013, 2014 Laboratoire de Recherche et
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
#include <algorithm>
#include "tgbaalgos/scc.hh"
#include "tgba/bddprint.hh"

//#define DEGEN_DEBUG

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

      size_t
      size()
      {
        return m.size();
      }
    };

    // Acceptance set common to all outgoing transitions (of the same
    // SCC -- we do not care about the other) of some state.
    class outgoing_acc
    {
      const tgba* a_;
      typedef std::pair<bdd, bdd> cache_entry;
      typedef Sgi::hash_map<const state*, cache_entry,
                            state_ptr_hash, state_ptr_equal> cache_t;
      cache_t cache_;
      const scc_map* sm_;

    public:
      outgoing_acc(const tgba* a, const scc_map* sm): a_(a), sm_(sm)
      {
      }

      cache_t::const_iterator fill_cache(const state* s)
      {
	unsigned s1 = sm_ ? sm_->scc_of_state(s) : 0;
        bdd common = a_->all_acceptance_conditions();
        bdd union_ = bddfalse;
        tgba_succ_iterator* it = a_->succ_iter(s);
        for (it->first(); !it->done(); it->next())
          {
	    // Ignore transitions that leave the SCC of s.
	    const state* d = it->current_state();
	    unsigned s2 = sm_ ? sm_->scc_of_state(d) : 0;
	    d->destroy();
	    if (s2 != s1)
	      continue;

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


    // Check whether a state has an accepting self-loop, with a catch.
    class has_acc_loop
    {
      const tgba* a_;
      typedef Sgi::hash_map<const state*, bool,
                            state_ptr_hash, state_ptr_equal> cache_t;
      cache_t cache_;
      unicity_table& uniq_;

    public:
      has_acc_loop(const tgba* a, unicity_table& uniq):
	a_(a),
	uniq_(uniq)
      {
      }

      bool check(const state* s)
      {
	std::pair<cache_t::iterator, bool> p =
	  cache_.insert(std::make_pair(s, false));
	if (p.second)
	  {
	    bdd all = a_->all_acceptance_conditions();
	    tgba_succ_iterator* it = a_->succ_iter(s);
	    for (it->first(); !it->done(); it->next())
	      {
		// Look only for transitions that are accepting.
		if (all != it->current_acceptance_conditions())
		  continue;
		// Look only for self-loops.
		const state* dest = uniq_(it->current_state());
		if (dest == s)
		  {
		    p.first->second = true;
		    break;
		  }
	      }
	    delete it;
	  }
        return p.first->second;
      }
    };

    // Order of accepting sets (for one SCC)
    class acc_order
    {
      std::vector<bdd> order_;
      bdd found_;

    public:
      unsigned
      next_level(bdd all, int slevel, bdd acc, bool skip_levels)
      {
        bdd temp = acc;
        if (all != found_)
          {
            // Check for new conditions in acc
            if ((acc & found_) != acc)
              {
                bdd acc_t = acc;
                while (acc_t != bddfalse)
                {
                  bdd next = bdd_satone(acc_t);
                  acc_t -= next;
                  // Add new condition
                  if ((next & found_) != next)
                    {
                      order_.push_back(next);
                      found_ |= next;
                    }
                }
              }
          }

        acc = temp;
        unsigned next = slevel;
        while (next < order_.size() && bdd_implies(order_[next], acc))
	  {
	    ++next;
	    if (!skip_levels)
	      break;
	  }
        return next;
      }

      void
      print(int scc, const bdd_dict* dict)
      {
        std::vector<bdd>::iterator i;
        std::cout << "Order_" << scc << ":\t";
        for (i = order_.begin(); i != order_.end(); i++)
          {
            bdd_print_acc(std::cout, dict, *i);
            std::cout << ", ";
          }
        std::cout << std::endl;
      }
    };

    // Accepting order for each SCC
    class scc_orders
    {
      bdd all_;
      std::map<int, acc_order> orders_;
      bool skip_levels_;

    public:
      scc_orders(bdd all, bool skip_levels):
	all_(all), skip_levels_(skip_levels)
      {
      }

      unsigned
      next_level(int scc, int slevel, bdd acc)
      {
        return orders_[scc].next_level(all_, slevel, acc, skip_levels_);
      }

      void
      print(const bdd_dict* dict)
      {
        std::map<int, acc_order>::iterator i;
        for (i = orders_.begin(); i != orders_.end(); i++)
          i->second.print(i->first, dict);
      }
    };
  }

  sba*
  degeneralize(const tgba* a, bool use_z_lvl, bool use_cust_acc_orders,
               int use_lvl_cache, bool skip_levels)
  {
    bool use_scc = use_lvl_cache || use_cust_acc_orders || use_z_lvl;

    bdd_dict* dict = a->get_dict();

    // The result (degeneralized) automaton uses numbered states.
    sba_explicit_number* res = new sba_explicit_number(dict);

    // We use the same BDD variables as the input, except for the
    // acceptance.
    dict->register_all_variables_of(a, res);
    dict->unregister_all_typed_variables(bdd_dict::acc, res);

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

    // Initialize scc_orders
    scc_orders orders(a->all_acceptance_conditions(), skip_levels);

    // Make sure we always use the same pointer for identical states
    // from the input automaton.
    unicity_table uniq;

    // Accepting loop checker, for some heuristics.
    has_acc_loop acc_loop(a, uniq);

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

    // State level cache
    typedef std::map<const state*, unsigned> lvl_cache_t;
    lvl_cache_t lvl_cache;

    // Compute SCCs in order to use any optimization.
    scc_map m(a);
    if (use_scc)
      m.build_map();

    // Cache for common outgoing acceptances.
    outgoing_acc outgoing(a, use_scc ? &m : 0);

    queue_t todo;

    const state* s0 = uniq(a->get_init_state());
    degen_state s(s0, 0);

    // As an heuristic, if the initial state at least one accepting
    // self-loop, start the degeneralization on the accepting level.
    if (acc_loop.check(s0))
      s.second = order.size();
    // Otherwise, check for acceptance conditions common to all
    // outgoing transitions, and assume we have already seen these and
    // start on the associated level.
    if (s.second == 0)
      {
	bdd acc = outgoing.common_acc(s.first);
	if (use_cust_acc_orders)
	  s.second = orders.next_level(m.initial(), s.second, acc);
	else
	  while (s.second < order.size() && bdd_implies(order[s.second], acc))
	    {
	      ++s.second;
	      if (!skip_levels)
		break;
	    }
      }

#ifdef DEGEN_DEBUG
    std::map<const state*, int>names;
    names[s.first] = 1;

    ds2num[s] =
      10000 * names[s.first] + 100 * s.second + m.scc_of_state(s.first);
#else
    ds2num[s] = 0;
#endif

    todo.push_back(s);

    // If use_lvl_cache is on insert initial state to level cache
    // Level cache stores first encountered level for each state.
    // When entering an SCC first the lvl_cache is checked.
    // If such state exists level from chache is used.
    // If not, a new level (starting with 0) is computed.
    if (use_lvl_cache)
      lvl_cache[s.first] = s.second;

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

        // Check SCC for state s
        int s_scc = -1;
        if (use_scc)
          s_scc = m.scc_of_state(s.first);

        tgba_succ_iterator* i = a->succ_iter(s.first);
        for (i->first(); !i->done(); i->next())
          {
            degen_state d(uniq(i->current_state()), 0);

#ifdef DEGEN_DEBUG
            if (names.find(d.first) == names.end())
              names[d.first] = uniq.size();
#endif
            // Check whether the target SCC is accepting
            bool is_scc_acc;
            int scc;
	    if (use_scc)
	      {
		scc = m.scc_of_state(d.first);
		is_scc_acc = m.accepting(scc);
	      }
	    else
	      {
		// If we have no SCC information, treat all SCCs as
		// accepting.
		scc = -1;
		is_scc_acc = true;
	      }

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
                //    two copies of state 2, depending on whether we
                //    reach it using 1=>2 or from 2->2.  If this
                //    example was not clear, uncomment the following
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
            if (is_scc_acc)
              {
                // If lvl_cache is used and switching SCCs, use level
                // from cache
                if (use_lvl_cache && s_scc != scc
		    && lvl_cache.find(d.first) != lvl_cache.end())
                  {
                    d.second = lvl_cache[d.first];
                  }
                else
                  {
		    // Complete (or replace) the acceptance sets of
		    // this link with the acceptance sets common to
		    // all transitions leaving the destination state.
		    if (s_scc == scc)
		      acc |= otheracc;
		    else
		      acc = otheracc;

		    // If use_z_lvl is on, start with level zero 0 when
		    // swhitching SCCs
		    unsigned next = (!use_z_lvl || s_scc == scc) ? slevel : 0;

		    // If using custom acc orders, get next level for this scc
                    if (use_cust_acc_orders)
		      {
			d.second = orders.next_level(scc, next, acc);
		      }
                    // Else compute level according the global acc order
                    else
                      {
			// As a heuristic, if we enter the SCC on a
			// state that has at least one accepting
			// self-loop, start the degeneralization on
			// the accepting level.
			if (s_scc != scc && acc_loop.check(d.first))
			  {
			    d.second = order.size();
			  }
			else
			  {
			    // Consider both the current acceptance
			    // sets, and the acceptance sets common to
			    // the outgoing transitions of the
			    // destination state.  But don't do
			    // that if the state is accepting and we
			    // are not skipping levels.
			    if (skip_levels || !is_acc)
			      while (next < order.size()
				     && bdd_implies(order[next], acc))
				{
				  ++next;
				  if (!skip_levels)
				    break;
				}
			    d.second = next;
			  }
                      }
                  }
              }

            // Have we already seen this destination?
            int dest;
            ds2num_map::const_iterator di = ds2num.find(d);
            if (di != ds2num.end())
              {
                dest = di->second;
              }
            else
              {
#ifdef DEGEN_DEBUG
                dest = 10000 * names[d.first] + 100 * d.second + scc;
#else
                dest = ds2num.size();
#endif
                ds2num[d] = dest;
                todo.push_back(d);
                // Insert new state to cache

		if (use_lvl_cache)
		  {
		    std::pair<lvl_cache_t::iterator, bool> res =
		      lvl_cache.insert(lvl_cache_t::value_type(d.first,
							       d.second));

		    if (!res.second)
		      {
			if (use_lvl_cache == 3)
			  res.first->second =
			    std::max(res.first->second, d.second);
			else if (use_lvl_cache == 2)
			  res.first->second =
			    std::min(res.first->second, d.second);
		      }
		  }
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
		if (is_acc)
		  t->acceptance_conditions = degen_acc;
              }
            else
              {
                t->condition |= i->current_condition();
              }
          }
        delete i;
        tr_cache.clear();
      }

#ifdef DEGEN_DEBUG
        std::vector<bdd>::iterator i;
        std::cout << "Orig. order:  \t";
        for (i = order.begin(); i != order.end(); i++)
          {
            bdd_print_acc(std::cout, dict, *i);
            std::cout << ", ";
          }
        std::cout << std::endl;
        orders.print(dict);
#endif

    res->merge_transitions();
    return res;
  }
}
