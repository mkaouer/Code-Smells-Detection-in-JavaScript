// Copyright (C) 2004, 2005, 2006  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_TGBAALGOS_NDFS_RESULT_HXX
# define SPOT_TGBAALGOS_NDFS_RESULT_HXX

//#define NDFSR_TRACE

#include <iostream>
#ifdef NDFSR_TRACE
#define ndfsr_trace std::cerr
#else
#define ndfsr_trace while (0) std::cerr
#endif

#include <cassert>
#include <list>
#include "misc/hash.hh"
#include "tgba/tgba.hh"
#include "emptiness.hh"
#include "emptiness_stats.hh"
#include "bfssteps.hh"
#include "misc/hash.hh"


namespace spot
{
  struct stack_item
  {
    stack_item(const state* n, tgba_succ_iterator* i, bdd l, bdd a)
      : s(n), it(i), label(l), acc(a) {};
    /// The visited state.
    const state* s;
    /// Design the next successor of \a s which has to be visited.
    tgba_succ_iterator* it;
    /// The label of the transition traversed to reach \a s
    /// (false for the first one).
    bdd label;
    /// The acceptance set of the transition traversed to reach \a s
    /// (false for the first one).
    bdd acc;
  };

  typedef std::list<stack_item> stack_type;

  namespace
  {
    // The acss_statistics is available only when the heap has a
    // size() method (which we indicate using n==1).

    template <typename T, int n>
    struct stats_interface
      : public ars_statistics
    {
    };

    template <typename T>
    struct stats_interface<T, 1>
      : public acss_statistics
    {
      unsigned
      acss_states() const
      {
	// all visited states are in the state space search
	return dynamic_cast<const T*>(this)->h_.size();
      }
    };

  }


  template <typename ndfs_search, typename heap>
  class ndfs_result:
    public emptiness_check_result,
    // Conditionally inherit from acss_statistics or ars_statistics.
    public stats_interface<ndfs_result<ndfs_search, heap>, heap::Has_Size>
  {
  public:
    ndfs_result(const ndfs_search& ms)
      : emptiness_check_result(ms.automaton()), ms_(ms),
        h_(ms_.get_heap())
    {
    }

    virtual ~ndfs_result()
    {
    }

    virtual tgba_run* accepting_run()
    {
      const stack_type& stb = ms_.get_st_blue();
      const stack_type& str = ms_.get_st_red();

      assert(!stb.empty());

      bdd covered_acc = bddfalse;
      accepting_transitions_list acc_trans;

      const state* start;

      start = stb.front().s->clone();
      if (!str.empty())
        {
          if (a_->number_of_acceptance_conditions() == 0)
            {
              // take arbitrarily the last transition on the red stack
              stack_type::const_iterator i, j;
              i = j = str.begin(); ++i;
              if (i == str.end())
                i = stb.begin();
              transition t = { i->s->clone(), j->label, j->acc,
			       j->s->clone() };
              assert(h_.has_been_visited(t.source));
              assert(h_.has_been_visited(t.dest));
              acc_trans.push_back(t);
            }
          else
            {
              // ignore the prefix
              stack_type::const_reverse_iterator i, j;

              i = j = stb.rbegin(); ++j;
              while (i->s->compare(start) != 0)
		++i, ++j;

              stack_type::const_reverse_iterator end = stb.rend();
              for (; j != end; ++i, ++j)
                {
                  if ((covered_acc & j->acc) != j->acc)
                    {
                      transition t = { i->s->clone(), j->label, j->acc,
				       j->s->clone() };
                      assert(h_.has_been_visited(t.source));
                      assert(h_.has_been_visited(t.dest));
                      acc_trans.push_back(t);
                      covered_acc |= j->acc;
                    }
                }

              j = str.rbegin();
              if ((covered_acc & j->acc) != j->acc)
                {
                  transition t = { i->s->clone(), j->label, j->acc,
				   j->s->clone() };
                  assert(h_.has_been_visited(t.source));
                  assert(h_.has_been_visited(t.dest));
                  acc_trans.push_back(t);
                  covered_acc |= j->acc;
                }

              i = j; ++j;
              end = str.rend();
              for (; j != end; ++i, ++j)
                {
                  if ((covered_acc & j->acc) != j->acc)
                    {
                      transition t = { i->s->clone(), j->label, j->acc,
				       j->s->clone() };
                      assert(h_.has_been_visited(t.source));
                      assert(h_.has_been_visited(t.dest));
                      acc_trans.push_back(t);
                      covered_acc |= j->acc;
                    }
                }
            }
        }

      if (a_->all_acceptance_conditions() != covered_acc)
        {
          bool b = dfs(start, acc_trans, covered_acc);
          assert(b);
          (void) b;
        }

      delete start;

      assert(!acc_trans.empty());

      tgba_run* run = new tgba_run;
      // construct run->cycle from acc_trans.
      construct_cycle(run, acc_trans);
      // construct run->prefix (a minimal path from the initial state to any
      // state of run->cycle) and adjust the cycle to the state reached by the
      // prefix.
      construct_prefix(run);

      for (typename accepting_transitions_list::const_iterator i =
	     acc_trans.begin(); i != acc_trans.end(); ++i)
        {
          delete i->source;
          delete i->dest;
        }

      return run;
    }

  private:
    const ndfs_search& ms_;
    const heap& h_;
    template <typename T, int n>
    friend struct stats_interface;

    struct transition {
      const state* source;
      bdd label;
      bdd acc;
      const state* dest;
    };
    typedef std::list<transition> accepting_transitions_list;

    typedef Sgi::hash_set<const state*,
                          state_ptr_hash, state_ptr_equal> state_set;

    void clean(stack_type& st1, state_set& seen, state_set& dead)
    {
      while (!st1.empty())
	{
	  delete st1.front().it;
	  st1.pop_front();
	}
      for (state_set::iterator i = seen.begin(); i != seen.end();)
	{
	  const state* s = *i;
	  ++i;
	  delete s;
	}
      for (state_set::iterator i = dead.begin(); i != dead.end();)
	{
	  const state* s = *i;
	  ++i;
	  delete s;
	}
    }

    bool dfs(const state* target, accepting_transitions_list& acc_trans,
	     bdd& covered_acc)
    {
      assert(h_.has_been_visited(target));
      stack_type st1;

      state_set seen, dead;
      const state* start = target->clone();

      seen.insert(start);
      tgba_succ_iterator* i = a_->succ_iter(start);
      i->first();
      st1.push_front(stack_item(start, i, bddfalse, bddfalse));

      while (!st1.empty())
        {
          stack_item& f = st1.front();
          ndfsr_trace << "DFS1 treats: " << a_->format_state(f.s)
                      << std::endl;
          if (!f.it->done())
            {
              const state *s_prime = f.it->current_state();
              ndfsr_trace << "  Visit the successor: "
                          << a_->format_state(s_prime) << std::endl;
              bdd label = f.it->current_condition();
              bdd acc = f.it->current_acceptance_conditions();
              f.it->next();
              if (h_.has_been_visited(s_prime))
                {
                  if (dead.find(s_prime) != dead.end())
                    {
                      ndfsr_trace << "  it is dead, pop it" << std::endl;
                      delete s_prime;
                    }
                  else if (seen.find(s_prime) == seen.end())
                    {
		      this->inc_ars_cycle_states();
                      ndfsr_trace << "  it is not seen, go down" << std::endl;
                      seen.insert(s_prime);
                      tgba_succ_iterator* i = a_->succ_iter(s_prime);
                      i->first();
                      st1.push_front(stack_item(s_prime, i, label, acc));
                    }
                  else if ((acc & covered_acc) != acc)
                    {
		      this->inc_ars_cycle_states();
                      ndfsr_trace << "  a propagation is needed, "
                                  << "start a search" << std::endl;
                      if (search(s_prime, target, dead))
                        {
                          transition t = { f.s->clone(), label, acc,
					   s_prime->clone() };
                          assert(h_.has_been_visited(t.source));
                          assert(h_.has_been_visited(t.dest));
                          acc_trans.push_back(t);
                          covered_acc |= acc;
                          if (covered_acc == a_->all_acceptance_conditions())
                            {
                              clean(st1, seen, dead);
                              delete s_prime;
                              return true;
                            }
                        }
                      delete s_prime;
                    }
                  else
                    {
                      ndfsr_trace << "  already seen, pop it" << std::endl;
                      delete s_prime;
                    }
                }
              else
                {
                  ndfsr_trace << "  not seen during the search, pop it"
                              << std::endl;
                  delete s_prime;
                }
            }
          else
            {
              ndfsr_trace << "  all the successors have been visited"
                          << std::endl;
              stack_item f_dest(f);
              delete st1.front().it;
              st1.pop_front();
              if (!st1.empty() && (f_dest.acc & covered_acc) != f_dest.acc)
                {
                  ndfsr_trace << "  a propagation is needed, start a search"
                              << std::endl;
                  if (search(f_dest.s, target, dead))
                    {
		      transition t = { st1.front().s->clone(),
				       f_dest.label, f_dest.acc,
				       f_dest.s->clone() };
                      assert(h_.has_been_visited(t.source));
                      assert(h_.has_been_visited(t.dest));
                      acc_trans.push_back(t);
                      covered_acc |= f_dest.acc;
                      if (covered_acc == a_->all_acceptance_conditions())
                        {
                          clean(st1, seen, dead);
                          return true;
                        }
                    }
                }
              else
                {
                  ndfsr_trace << "  no propagation needed, pop it"
                              << std::endl;
                }
            }
        }

      clean(st1, seen, dead);
      return false;
    }

    class test_path: public bfs_steps
    {
    public:
      test_path(ars_statistics* ars,
		const tgba* a, const state* t,
		const state_set& d, const heap& h)
        : bfs_steps(a), ars(ars), target(t), dead(d), h(h)
      {
      }

      ~test_path()
      {
        state_set::const_iterator i = seen.begin();
        while (i != seen.end())
          {
            const state* ptr = *i;
            ++i;
            delete ptr;
          }
      }

      const state* search(const state* start, tgba_run::steps& l)
      {
        const state* s = filter(start);
        if (s)
          return this->bfs_steps::search(s, l);
        else
          return 0;
      }

      const state* filter(const state* s)
      {
        if (!h.has_been_visited(s)
	    || seen.find(s) != seen.end()
	    || dead.find(s) != dead.end())
          {
            delete s;
            return 0;
          }
	ars->inc_ars_cycle_states();
        seen.insert(s);
        return s;
      }

      void finalize(const std::map<const state*, tgba_run::step,
		                   state_ptr_less_than>&,
		    const tgba_run::step&, const state*, tgba_run::steps&)
      {
      }

      const state_set& get_seen() const
      {
        return seen;
      }

      bool match(tgba_run::step&, const state* dest)
      {
        return target->compare(dest) == 0;
      }

    private:
      ars_statistics* ars;
      state_set seen;
      const state* target;
      const state_set& dead;
      const heap& h;
    };

    bool search(const state* start, const state* target, state_set& dead)
    {
      tgba_run::steps path;
      if (start->compare(target) == 0)
	return true;

      test_path s(this, a_, target, dead, h_);
      const state* res = s.search(start->clone(), path);
      if (res)
	{
	  assert(res->compare(target) == 0);
	  return true;
	}
      else
	{
	  state_set::const_iterator it;
	  for (it = s.get_seen().begin(); it != s.get_seen().end(); ++it)
	    dead.insert((*it)->clone());
	  return false;
	}
    }

    typedef Sgi::hash_multimap<const state*, transition,
			       state_ptr_hash, state_ptr_equal> m_source_trans;

    template<bool cycle>
    class min_path: public bfs_steps
    {
    public:
      min_path(ars_statistics* ars,
	       const tgba* a, const m_source_trans& target, const heap& h)
        : bfs_steps(a), ars(ars), target(target), h(h)
      {
      }

      ~min_path()
      {
        state_set::const_iterator i = seen.begin();
        while (i != seen.end())
          {
            const state* ptr = *i;
            ++i;
            delete ptr;
          }
      }

      const state* search(const state* start, tgba_run::steps& l)
      {
        const state* s = filter(start);
        if (s)
          return this->bfs_steps::search(s, l);
        else
          return 0;
      }

      const state* filter(const state* s)
      {
        ndfsr_trace << "filter: " << a_->format_state(s);
        if (!h.has_been_visited(s) || seen.find(s) != seen.end())
          {
            if (!h.has_been_visited(s))
              ndfsr_trace << " not visited" << std::endl;
            else
              ndfsr_trace << " already seen" << std::endl;
            delete s;
            return 0;
          }
        ndfsr_trace << " OK" << std::endl;
	if (cycle)
	  ars->inc_ars_cycle_states();
	else
	  ars->inc_ars_prefix_states();
        seen.insert(s);
        return s;
      }

      bool match(tgba_run::step&, const state* dest)
      {
        ndfsr_trace << "match: " << a_->format_state(dest)
                    << std::endl;
        return target.find(dest) != target.end();
      }

    private:
      ars_statistics* ars;
      state_set seen;
      const m_source_trans& target;
      const heap& h;
    };

    void construct_cycle(tgba_run* run,
			 const accepting_transitions_list& acc_trans)
    {
      assert(!acc_trans.empty());
      transition current = acc_trans.front();
      // insert the first accepting transition in the cycle
      ndfsr_trace << "the initial accepting transition is from "
		  << a_->format_state(current.source) << " to "
		  << a_->format_state(current.dest) << std::endl;
      const state* begin = current.source;

      m_source_trans target;
      typename accepting_transitions_list::const_iterator i =
	acc_trans.begin();
      ndfsr_trace << "targets are the source states: ";
      for (++i; i != acc_trans.end(); ++i)
	{
	  if (i->source->compare(begin) == 0 &&
	      i->source->compare(i->dest) == 0)
	    {
	      ndfsr_trace << "(self loop " << a_->format_state(i->source)
			  << " -> " << a_->format_state(i->dest)
			  << " ignored) ";
	      tgba_run::step st = { i->source->clone(), i->label, i->acc };
	      run->cycle.push_back(st);
	    }
	  else
	    {
	      ndfsr_trace << a_->format_state(i->source) << " (-> "
			  << a_->format_state(i->dest) << ") ";
	      target.insert(std::make_pair(i->source, *i));
	    }
	}
      ndfsr_trace << std::endl;

      tgba_run::step st = { current.source->clone(), current.label,
			    current.acc };
      run->cycle.push_back(st);

      while (!target.empty())
	{
	  // find a minimal path from current.dest to any source state in
	  // target.
	  ndfsr_trace << "looking for a path from "
		      << a_->format_state(current.dest) << std::endl;
	  typename m_source_trans::iterator i = target.find(current.dest);
	  if (i == target.end())
	    {
	      min_path<true> s(this, a_, target, h_);
	      const state* res = s.search(current.dest->clone(), run->cycle);
	      // init current to the corresponding transition.
	      assert(res);
	      ndfsr_trace << a_->format_state(res) << " reached" << std::endl;
	      i = target.find(res);
	      assert(i != target.end());
	    }
	  else
	    {
	      ndfsr_trace << "this is a target" << std::endl;
	    }
	  current = i->second;
	  // complete the path with the corresponding transition
	  tgba_run::step st = { current.source->clone(), current.label,
				current.acc };
	  run->cycle.push_back(st);
	  // remove this source state of target
	  target.erase(i);
	}

      if (current.dest->compare(begin) != 0)
	{
	  // close the cycle by adding a path from the destination of the
	  // last inserted transition to the source of the first one
	  ndfsr_trace << std::endl << "looking for a path from "
		      << a_->format_state(current.dest) << " to "
		      << a_->format_state(begin) << std::endl;
	  transition tmp;
	  tmp.source = tmp.dest = 0; // Initialize to please GCC 4.0.1 (Darwin).
	  target.insert(std::make_pair(begin, tmp));
	  min_path<true> s(this, a_, target, h_);
	  const state* res = s.search(current.dest->clone(), run->cycle);
	  assert(res);
	  assert(res->compare(begin) == 0);
	  (void)res;
	}
    }

    void construct_prefix(tgba_run* run)
    {
      m_source_trans target;
      transition tmp;
      tmp.source = tmp.dest = 0; // Initialize to please GCC 4.0.

      // Register all states from the cycle as target of the BFS.
      for (tgba_run::steps::const_iterator i = run->cycle.begin();
	   i != run->cycle.end(); ++i)
        target.insert(std::make_pair(i->s, tmp));

      const state* prefix_start = a_->get_init_state();
      // There are two cases: either the initial state is already on
      // the cycle, or it is not.  If it is, we will have to rotate
      // the cycle so it begins on this position.  Otherwise we will shift
      // the cycle so it begins on the state that follows the prefix.
      // cycle_entry_point is that state.
      const state* cycle_entry_point;
      typename m_source_trans::const_iterator ps = target.find(prefix_start);
      if (ps != target.end())
        {
          // The initial state is on the cycle.
          delete prefix_start;
          cycle_entry_point = ps->first->clone();
        }
      else
        {
          // This initial state is outside the cycle.  Compute the prefix.
          min_path<false> s(this, a_, target, h_);
          cycle_entry_point = s.search(prefix_start, run->prefix);
          assert(cycle_entry_point);
          cycle_entry_point = cycle_entry_point->clone();
        }

      // Locate cycle_entry_point on the cycle.
      tgba_run::steps::iterator cycle_ep_it;
      for (cycle_ep_it = run->cycle.begin();
	   cycle_ep_it != run->cycle.end()
	     && cycle_entry_point->compare(cycle_ep_it->s); ++cycle_ep_it)
        continue;
      assert(cycle_ep_it != run->cycle.end());
      delete cycle_entry_point;

      // Now shift the cycle so it starts on cycle_entry_point.
      run->cycle.splice(run->cycle.end(), run->cycle,
                        run->cycle.begin(), cycle_ep_it);
    }
  };

}

#undef ndfsr_trace

#endif // SPOT_TGBAALGOS_NDFS_RESULT_HXX
