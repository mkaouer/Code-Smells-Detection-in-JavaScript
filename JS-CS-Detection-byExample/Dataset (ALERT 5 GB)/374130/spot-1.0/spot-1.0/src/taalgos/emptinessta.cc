// Copyright (C) 2010, 2011, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
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

//#define TRACE

#include <iostream>
#ifdef TRACE
#define trace std::clog
#else
#define trace while (0) std::clog
#endif

#include "emptinessta.hh"
#include "misc/memusage.hh"
#include <math.h>
#include "tgba/bddprint.hh"

namespace spot
{

  ta_check::ta_check(const ta_product* a, option_map o) :
    a_(a), o_(o)
  {
    is_full_2_pass_ = o.get("is_full_2_pass", 0);
  }

  ta_check::~ta_check()
  {
  }

  bool
  ta_check::check(bool disable_second_pass,
      bool disable_heuristic_for_livelock_detection)
  {

    // We use five main data in this algorithm:

    // * scc: (attribute) a stack of strongly connected components (SCC)

    // * arc, a stack of acceptance conditions between each of these SCC,
    std::stack<bdd> arc;

    // * h: a hash of all visited nodes, with their order,
    //   (it is called "Hash" in Couvreur's paper)
    numbered_state_heap* h =
        numbered_state_heap_hash_map_factory::instance()->build();

    // * num: the number of visited nodes.  Used to set the order of each
    //   visited node,
    int num = 1;

    // * todo: the depth-first search stack.  This holds pairs of the
    //   form (STATE, ITERATOR) where ITERATOR is a ta_succ_iterator_product
    //   over the successors of STATE.  In our use, ITERATOR should
    //   always be freed when TODO is popped, but STATE should not because
    //   it is also used as a key in H.
    std::stack<pair_state_iter> todo;

    Sgi::hash_map<const state*, std::string, state_ptr_hash, state_ptr_equal>
        colour;

    trace
      << "PASS 1" << std::endl;

    Sgi::hash_map<const state*, std::set<const state*, state_ptr_less_than>,
        state_ptr_hash, state_ptr_equal> liveset;

    std::stack<spot::state*> livelock_roots;

    bool livelock_acceptance_states_not_found = true;

    bool activate_heuristic = !disable_heuristic_for_livelock_detection
        && (is_full_2_pass_ == disable_second_pass);

    // Setup depth-first search from initial states.
    const ta* ta_ = a_->get_ta();
    const kripke* kripke_ = a_->get_kripke();
    state* kripke_init_state = kripke_->get_init_state();
    bdd kripke_init_state_condition = kripke_->state_condition(
        kripke_init_state);

    spot::state* artificial_initial_state = ta_->get_artificial_initial_state();

    ta_succ_iterator* ta_init_it_ = ta_->succ_iter(artificial_initial_state,
        kripke_init_state_condition);
    kripke_init_state->destroy();
    for (ta_init_it_->first(); !ta_init_it_->done(); ta_init_it_->next())
      {

        state_ta_product* init = new state_ta_product(
            (ta_init_it_->current_state()), kripke_init_state->clone());

        numbered_state_heap::state_index_p h_init = h->find(init);

        if (h_init.first)
          continue;

        h->insert(init, ++num);
        scc.push(num);
        arc.push(bddfalse);

        ta_succ_iterator_product* iter = a_->succ_iter(init);
        iter->first();
        todo.push(pair_state_iter(init, iter));

        inc_depth();

        //push potential root of live-lock accepting cycle
        if (activate_heuristic && a_->is_livelock_accepting_state(init))
          livelock_roots.push(init);

        while (!todo.empty())
          {

            state* curr = todo.top().first;

            // We are looking at the next successor in SUCC.
            ta_succ_iterator_product* succ = todo.top().second;

            // If there is no more successor, backtrack.
            if (succ->done())
              {
                // We have explored all successors of state CURR.


                // Backtrack TODO.
                todo.pop();
                dec_depth();
                trace
                  << "PASS 1 : backtrack" << std::endl;

                if (a_->is_livelock_accepting_state(curr)
                    && !a_->is_accepting_state(curr))
                  {
                    livelock_acceptance_states_not_found = false;
                    trace
                      << "PASS 1 : livelock accepting state found" << std::endl;

                  }

                // fill rem with any component removed,
                numbered_state_heap::state_index_p spi =
                    h->index(curr->clone());
                assert(spi.first);

                scc.rem().push_front(curr);
                inc_depth();

                // set the h value of the Backtracked state to negative value.
                // colour[curr] = BLUE;
                *spi.second = -std::abs(*spi.second);

                // Backtrack livelock_roots.
                if (activate_heuristic && !livelock_roots.empty()
                    && !livelock_roots.top()->compare(curr))
                  livelock_roots.pop();

                // When backtracking the root of an SSCC, we must also
                // remove that SSCC from the ROOT stacks.  We must
                // discard from H all reachable states from this SSCC.
                assert(!scc.empty());
                if (scc.top().index == std::abs(*spi.second))
                  {
                    // removing states
                    std::list<state*>::iterator i;

                    for (i = scc.rem().begin(); i != scc.rem().end(); ++i)
                      {
                        numbered_state_heap::state_index_p spi = h->index(
                            (*i)->clone());
                        assert(spi.first->compare(*i) == 0);
                        assert(*spi.second != -1);
                        *spi.second = -1;
                        //colour[*i] = BLACK;

                      }
                    dec_depth(scc.rem().size());
                    scc.pop();
                    assert(!arc.empty());
                    arc.pop();

                  }

                delete succ;
                // Do not delete CURR: it is a key in H.
                continue;
              }

            // We have a successor to look at.
            inc_transitions();
            trace
              << "PASS 1: transition" << std::endl;
            // Fetch the values destination state we are interested in...
            state* dest = succ->current_state();

            bdd acc_cond = succ->current_acceptance_conditions();

            bool curr_is_livelock_hole_state_in_ta_component =
                (a_->is_hole_state_in_ta_component(curr))
                    && a_->is_livelock_accepting_state(curr);

            // May be Buchi accepting scc or livelock accepting scc
            // (contains a livelock accepting state that have no
            // successors in TA).
            scc.top().is_accepting = (a_->is_accepting_state(curr)
                && (!succ->is_stuttering_transition()
                    || a_->is_livelock_accepting_state(curr)))
                || curr_is_livelock_hole_state_in_ta_component;

            bool is_stuttering_transition = succ->is_stuttering_transition();

            // ... and point the iterator to the next successor, for
            // the next iteration.
            succ->next();
            // We do not need SUCC from now on.

            // Are we going to a new state?
            numbered_state_heap::state_index_p spi = h->find(dest);

            // Is this a new state?
            if (!spi.first)
              {
                // Number it, stack it, and register its successors
                // for later processing.
                h->insert(dest, ++num);
                scc.push(num);
                arc.push(acc_cond);

                ta_succ_iterator_product* iter = a_->succ_iter(dest);
                iter->first();
                todo.push(pair_state_iter(dest, iter));
                //colour[dest] = GREY;
                inc_depth();

                //push potential root of live-lock accepting cycle
                if (activate_heuristic && a_->is_livelock_accepting_state(dest)
                    && !is_stuttering_transition)
                  livelock_roots.push(dest);

                continue;
              }

            // If we have reached a dead component, ignore it.
            if (*spi.second == -1)
              continue;

            // Now this is the most interesting case.  We have reached a
            // state S1 which is already part of a non-dead SSCC.  Any such
            // non-dead SSCC has necessarily been crossed by our path to
            // this state: there is a state S2 in our path which belongs
            // to this SSCC too.  We are going to merge all states between
            // this S1 and S2 into this SSCC.
            //
            // This merge is easy to do because the order of the SSCC in
            // ROOT is ascending: we just have to merge all SSCCs from the
            // top of ROOT that have an index greater to the one of
            // the SSCC of S2 (called the "threshold").
            int threshold = std::abs(*spi.second);
            std::list<state*> rem;
            bool acc = false;

            trace
              << "***PASS 1: CYCLE***" << std::endl;

            while (threshold < scc.top().index)
              {
                assert(!scc.empty());
                assert(!arc.empty());

                acc |= scc.top().is_accepting;
                acc_cond |= scc.top().condition;
                acc_cond |= arc.top();

                rem.splice(rem.end(), scc.rem());
                scc.pop();
                arc.pop();

              }

            // Note that we do not always have
            //  threshold == scc.top().index
            // after this loop, the SSCC whose index is threshold might have
            // been merged with a lower SSCC.

            // Accumulate all acceptance conditions into the merged SSCC.
            scc.top().is_accepting |= acc;
            scc.top().condition |= acc_cond;

            scc.rem().splice(scc.rem().end(), rem);
            bool is_accepting_sscc = (scc.top().is_accepting)
                || (scc.top().condition == a_->all_acceptance_conditions());

            if (is_accepting_sscc)
              {
                trace
		  << "PASS 1: SUCCESS : a_->is_livelock_accepting_state(curr): "
		  << a_->is_livelock_accepting_state(curr) << std::endl;
                trace
                  << "PASS 1: scc.top().condition : "
		  << bdd_format_accset(a_->get_dict(), scc.top().condition)
		  << std::endl;
                trace
                  << "PASS 1: a_->all_acceptance_conditions() : "
		  << (a_->all_acceptance_conditions()) << std::endl;
                trace
		  << ("PASS 1 CYCLE and (scc.top().condition == "
		      "a_->all_acceptance_conditions()) : ")
		  << (scc.top().condition
		      == a_->all_acceptance_conditions()) << std::endl;

                trace
                  << "PASS 1: bddtrue : " << (a_->all_acceptance_conditions()
					      == bddtrue) << std::endl;

                trace
                  << "PASS 1: bddfalse : " << (a_->all_acceptance_conditions()
					       == bddfalse) << std::endl;

                clear(h, todo, ta_init_it_);
                return true;
              }

            //ADDLINKS
            if (activate_heuristic && a_->is_livelock_accepting_state(curr)
                && is_stuttering_transition)
              {
                trace
                  << "PASS 1: heuristic livelock detection " << std::endl;
                const state* dest = spi.first;
                std::set<const state*, state_ptr_less_than> liveset_dest =
                    liveset[dest];

                std::set<const state*, state_ptr_less_than> liveset_curr =
                    liveset[curr];

                int h_livelock_root = 0;
                if (!livelock_roots.empty())
                  h_livelock_root = *(h->find((livelock_roots.top()))).second;

                if (heuristic_livelock_detection(dest, h, h_livelock_root,
                    liveset_curr))
                  {
                    clear(h, todo, ta_init_it_);
                    return true;
                  }

                std::set<const state*, state_ptr_less_than>::const_iterator it;
                for (it = liveset_dest.begin(); it != liveset_dest.end(); it++)
                  {
                    const state* succ = (*it);
                    if (heuristic_livelock_detection(succ, h, h_livelock_root,
                        liveset_curr))
                      {
                        clear(h, todo, ta_init_it_);
                        return true;
                      }

                  }

              }
          }

      }

    clear(h, todo, ta_init_it_);

    if (disable_second_pass || livelock_acceptance_states_not_found)
      return false;

    return livelock_detection(a_);
  }

  bool
  ta_check::heuristic_livelock_detection(const state * u,
      numbered_state_heap* h, int h_livelock_root, std::set<const state*,
          state_ptr_less_than> liveset_curr)
  {
    numbered_state_heap::state_index_p hu = h->find(u);

    if (*hu.second > 0) // colour[u] == GREY
      {

        if (*hu.second >= h_livelock_root)
          {
            trace
              << "PASS 1: heuristic livelock detection SUCCESS" << std::endl;
            return true;
          }

        liveset_curr.insert(u);
      }

    return false;

  }

  bool
  ta_check::livelock_detection(const ta_product* t)
  {
    // We use five main data in this algorithm:

    // * sscc: a stack of strongly stuttering-connected components (SSCC)


    // * h: a hash of all visited nodes, with their order,
    //   (it is called "Hash" in Couvreur's paper)
    numbered_state_heap* h =
        numbered_state_heap_hash_map_factory::instance()->build();

    // * num: the number of visited nodes.  Used to set the order of each
    //   visited node,

    trace
      << "PASS 2" << std::endl;

    int num = 0;

    // * todo: the depth-first search stack.  This holds pairs of the
    //   form (STATE, ITERATOR) where ITERATOR is a tgba_succ_iterator
    //   over the successors of STATE.  In our use, ITERATOR should
    //   always be freed when TODO is popped, but STATE should not because
    //   it is also used as a key in H.
    std::stack<pair_state_iter> todo;

    // * init: the set of the depth-first search initial states
    std::queue<spot::state*> ta_init_it_;

    const ta::states_set_t init_states_set = a_->get_initial_states_set();
    ta::states_set_t::const_iterator it;
    for (it = init_states_set.begin(); it != init_states_set.end(); it++)
      {
        state* init_state = (*it);
        ta_init_it_.push(init_state);

      }

    while (!ta_init_it_.empty())
      {
        // Setup depth-first search from initial states.
          {
            state* init = ta_init_it_.front();
            ta_init_it_.pop();
            numbered_state_heap::state_index_p h_init = h->find(init);

            if (h_init.first)
              continue;

            h->insert(init, ++num);
            sscc.push(num);
            sscc.top().is_accepting = t->is_livelock_accepting_state(init);
            ta_succ_iterator_product* iter = t->succ_iter(init);
            iter->first();
            todo.push(pair_state_iter(init, iter));
            inc_depth();

          }

        while (!todo.empty())
          {

            state* curr = todo.top().first;

            // We are looking at the next successor in SUCC.
            ta_succ_iterator_product* succ = todo.top().second;

            // If there is no more successor, backtrack.
            if (succ->done())
              {
                // We have explored all successors of state CURR.

                // Backtrack TODO.
                todo.pop();
                dec_depth();
                trace
                  << "PASS 2 : backtrack" << std::endl;

                // fill rem with any component removed,
                numbered_state_heap::state_index_p spi =
                    h->index(curr->clone());
                assert(spi.first);

                sscc.rem().push_front(curr);
                inc_depth();

                // When backtracking the root of an SSCC, we must also
                // remove that SSCC from the ROOT stacks.  We must
                // discard from H all reachable states from this SSCC.
                assert(!sscc.empty());
                if (sscc.top().index == *spi.second)
                  {
                    // removing states
                    std::list<state*>::iterator i;

                    for (i = sscc.rem().begin(); i != sscc.rem().end(); ++i)
                      {
                        numbered_state_heap::state_index_p spi = h->index(
                            (*i)->clone());
                        assert(spi.first->compare(*i) == 0);
                        assert(*spi.second != -1);
                        *spi.second = -1;
                      }
                    dec_depth(sscc.rem().size());
                    sscc.pop();
                  }

                delete succ;
                // Do not delete CURR: it is a key in H.

                continue;
              }

            // We have a successor to look at.
            inc_transitions();
            trace
              << "PASS 2 : transition" << std::endl;
            // Fetch the values destination state we are interested in...
            state* dest = succ->current_state();

            bool is_stuttering_transition = succ->is_stuttering_transition();
            // ... and point the iterator to the next successor, for
            // the next iteration.
            succ->next();
            // We do not need SUCC from now on.

            numbered_state_heap::state_index_p spi = h->find(dest);

            // Is this a new state?
            if (!spi.first)
              {

                // Are we going to a new state through a stuttering transition?

                if (!is_stuttering_transition)
                  {
                    ta_init_it_.push(dest);
                    continue;
                  }

                // Number it, stack it, and register its successors
                // for later processing.
                h->insert(dest, ++num);
                sscc.push(num);
                sscc.top().is_accepting = t->is_livelock_accepting_state(dest);

                ta_succ_iterator_product* iter = t->succ_iter(dest);
                iter->first();
                todo.push(pair_state_iter(dest, iter));
                inc_depth();
                continue;
              }

            // If we have reached a dead component, ignore it.
            if (*spi.second == -1)
              continue;

            //self loop state
            if (!curr->compare(spi.first))
              {
                state * self_loop_state = (curr);

                if (t->is_livelock_accepting_state(self_loop_state))
                  {
                    clear(h, todo, ta_init_it_);
                    trace
                      << "PASS 2: SUCCESS" << std::endl;
                    return true;
                  }

              }

            // Now this is the most interesting case.  We have reached a
            // state S1 which is already part of a non-dead SSCC.  Any such
            // non-dead SSCC has necessarily been crossed by our path to
            // this state: there is a state S2 in our path which belongs
            // to this SSCC too.  We are going to merge all states between
            // this S1 and S2 into this SSCC.
            //
            // This merge is easy to do because the order of the SSCC in
            // ROOT is ascending: we just have to merge all SSCCs from the
            // top of ROOT that have an index greater to the one of
            // the SSCC of S2 (called the "threshold").
            int threshold = *spi.second;
            std::list<state*> rem;
            bool acc = false;

            while (threshold < sscc.top().index)
              {
                assert(!sscc.empty());

                acc |= sscc.top().is_accepting;

                rem.splice(rem.end(), sscc.rem());
                sscc.pop();

              }
            // Note that we do not always have
            //  threshold == sscc.top().index
            // after this loop, the SSCC whose index is threshold might have
            // been merged with a lower SSCC.

            // Accumulate all acceptance conditions into the merged SSCC.
            sscc.top().is_accepting |= acc;

            sscc.rem().splice(sscc.rem().end(), rem);
            if (sscc.top().is_accepting)
              {
                clear(h, todo, ta_init_it_);
                trace
                  << "PASS 2: SUCCESS" << std::endl;
                return true;
              }
          }

      }
    clear(h, todo, ta_init_it_);
    return false;
  }

  void
  ta_check::clear(numbered_state_heap* h, std::stack<pair_state_iter> todo,
      std::queue<spot::state*> init_states)
  {

    set_states(states() + h->size());

    while (!init_states.empty())
      {
        a_->free_state(init_states.front());
        init_states.pop();
      }

    // Release all iterators in TODO.
    while (!todo.empty())
      {
        delete todo.top().second;
        todo.pop();
        dec_depth();
      }
    delete h;
  }

  void
  ta_check::clear(numbered_state_heap* h, std::stack<pair_state_iter> todo,
      spot::ta_succ_iterator* init_states_it)
  {

    set_states(states() + h->size());

    delete init_states_it;

    // Release all iterators in TODO.
    while (!todo.empty())
      {
        delete todo.top().second;
        todo.pop();
        dec_depth();
      }
    delete h;
  }

  std::ostream&
  ta_check::print_stats(std::ostream& os) const
  {
    //    ecs_->print_stats(os);
    os << states() << " unique states visited" << std::endl;

    //TODO  sscc;
    os << scc.size() << " strongly connected components in search stack"
        << std::endl;
    os << transitions() << " transitions explored" << std::endl;
    os << max_depth() << " items max in DFS search stack" << std::endl;
    return os;
  }

//////////////////////////////////////////////////////////////////////


}
