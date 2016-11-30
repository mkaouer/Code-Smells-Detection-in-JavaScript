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

/// FIXME:
/// * Test some heuristics on the order of visit of the successors in the blue
///   dfs:
///   - favorize the arcs conducting to the blue stack (the states of color
///     cyan)
///   - in this category, favorize the labelled arcs
///   - for the remaining ones, favorize the arcs labelled by the greatest
///     number of new acceptance conditions (notice that this number may evolve
///     after the visit of previous successors).
///
/// * Add a bit-state hashing version.

//#define TRACE

#include <iostream>
#ifdef TRACE
#define trace std::cerr
#else
#define trace while (0) std::cerr
#endif

#include <cassert>
#include <vector>
#include <stack>
#include "misc/hash.hh"
#include "tgba/tgba.hh"
#include "emptiness.hh"
#include "emptiness_stats.hh"
#include "tau03opt.hh"
#include "weight.hh"
#include "ndfs_result.hxx"

namespace spot
{
  namespace
  {
    enum color {WHITE, CYAN, BLUE};

    /// \brief Emptiness checker on spot::tgba automata having a finite number
    /// of acceptance conditions (i.e. a TGBA).
    template <typename heap>
    class tau03_opt_search : public emptiness_check, public ec_statistics
    {
    public:
      /// \brief Initialize the search algorithm on the automaton \a a
      tau03_opt_search(const tgba *a, size_t size, option_map o)
        : emptiness_check(a, o),
          current_weight(a->neg_acceptance_conditions()),
          h(size),
          all_acc(a->all_acceptance_conditions()),
	  use_condition_stack(o.get("condstack")),
	  use_ordering(use_condition_stack && o.get("ordering")),
	  use_weights(o.get("weights", 1)),
	  use_red_weights(use_weights && o.get("redweights", 1))
      {
	if (use_ordering)
	  {
	    bdd all_conds = all_acc;
	    while (all_conds != bddfalse)
	      {
		bdd acc = bdd_satone(all_conds);
		cond.push_back(acc);
		all_conds -= acc;
	      }
	  }
      }

      virtual ~tau03_opt_search()
      {
        // Release all iterators on the stacks.
        while (!st_blue.empty())
          {
            h.pop_notify(st_blue.front().s);
            delete st_blue.front().it;
            st_blue.pop_front();
          }
        while (!st_red.empty())
          {
            h.pop_notify(st_red.front().s);
            delete st_red.front().it;
            st_red.pop_front();
          }
      }

      /// \brief Perform an emptiness check.
      ///
      /// \return non null pointer iff the algorithm has found an
      /// accepting path.
      virtual emptiness_check_result* check()
      {
        if (!st_blue.empty())
            return 0;
        assert(st_red.empty());
        const state* s0 = a_->get_init_state();
        inc_states();
        h.add_new_state(s0, CYAN, current_weight);
        push(st_blue, s0, bddfalse, bddfalse);
        if (dfs_blue())
          return new ndfs_result<tau03_opt_search<heap>, heap>(*this);
        return 0;
      }

      virtual std::ostream& print_stats(std::ostream &os) const
      {
        os << states() << " distinct nodes visited" << std::endl;
        os << transitions() << " transitions explored" << std::endl;
        os << max_depth() << " nodes for the maximal stack depth" << std::endl;
        return os;
      }

      const heap& get_heap() const
        {
          return h;
        }

      const stack_type& get_st_blue() const
        {
          return st_blue;
        }

      const stack_type& get_st_red() const
        {
          return st_red;
        }

    private:
      void push(stack_type& st, const state* s,
                        const bdd& label, const bdd& acc)
      {
        inc_depth();
        tgba_succ_iterator* i = a_->succ_iter(s);
        i->first();
        st.push_front(stack_item(s, i, label, acc));
      }

      void pop(stack_type& st)
      {
        dec_depth();
        delete st.front().it;
        st.pop_front();
      }

      bdd project_acc(bdd acc) const
      {
	bdd result = bddfalse;
	for (std::vector<bdd>::const_iterator i = cond.begin();
	     i != cond.end() && (acc & *i) != bddfalse;
	     ++i)
	  result |= *i;
	return result;
      }

      /// \brief weight of the state on top of the blue stack.
      weight current_weight;

      /// \brief Stack of the blue dfs.
      stack_type st_blue;

      /// \brief Stack of the red dfs.
      stack_type st_red;

      /// \brief Map where each visited state is colored
      /// by the last dfs visiting it.
      heap h;

      /// The unique acceptance condition of the automaton \a a.
      bdd all_acc;

      /// Whether to use the "condition stack".
      bool use_condition_stack;
      /// Whether to use an ordering between the acceptance conditions.
      /// Effective only if using the condition stack.
      bool use_ordering;
      /// Whether to use weights to abort earlier.
      bool use_weights;
      /// Whether to use weights in the red dfs.
      bool use_red_weights;

      /// Ordering of the acceptance conditions.
      std::vector<bdd> cond;

      bool dfs_blue()
      {
        while (!st_blue.empty())
          {
            stack_item& f = st_blue.front();
            trace << "DFS_BLUE treats: " << a_->format_state(f.s) << std::endl;
            if (!f.it->done())
              {
                const state *s_prime = f.it->current_state();
                trace << "  Visit the successor: "
                      << a_->format_state(s_prime) << std::endl;
                bdd label = f.it->current_condition();
                bdd acc = f.it->current_acceptance_conditions();
                // Go down the edge (f.s, <label, acc>, s_prime)
                f.it->next();
                inc_transitions();
                typename heap::color_ref c_prime = h.get_color_ref(s_prime);
                if (c_prime.is_white())
                  {
                    trace << "  It is white, go down" << std::endl;
		    if (use_weights)
		      current_weight += acc;
                    inc_states();
                    h.add_new_state(s_prime, CYAN, current_weight);
                    push(st_blue, s_prime, label, acc);
                  }
                else
                  {
                    typename heap::color_ref c = h.get_color_ref(f.s);
                    assert(!c.is_white());
                    if (c_prime.get_color() == CYAN
			&& all_acc == ((current_weight - c_prime.get_weight())
				       | c.get_acc()
				       | acc
				       | c_prime.get_acc()))
                      {
                        trace << "  It is cyan and acceptance condition "
                              << "is reached, report cycle" << std::endl;
                        c_prime.cumulate_acc(all_acc);
                        push(st_red, s_prime, label, acc);
                        return true;
                      }
                    else
                      {
                        trace << "  It is cyan or blue and";
                        bdd acu = acc | c.get_acc();
                        bdd acp = (use_ordering ? project_acc(acu) : acu);
                        if ((c_prime.get_acc() & acp) != acp)
                          {
                            trace << "  a propagation is needed, "
                                  << "start a red dfs" << std::endl;
                            c_prime.cumulate_acc(acp);
                            push(st_red, s_prime, label, acc);
                            if (dfs_red(acu))
                              return true;
                          }
                        else
                          {
                            trace << " no propagation is needed, pop it."
                                  << std::endl;
                            h.pop_notify(s_prime);
                          }
                      }
                  }
              }
            else
            // Backtrack the edge
            //        (predecessor of f.s in st_blue, <f.label, f.acc>, f.s)
              {
                trace << "  All the successors have been visited" << std::endl;
                stack_item f_dest(f);
                pop(st_blue);
		if (use_weights)
		  current_weight -= f_dest.acc;
                typename heap::color_ref c_prime = h.get_color_ref(f_dest.s);
                assert(!c_prime.is_white());
                c_prime.set_color(BLUE);
                if (!st_blue.empty())
                  {
                    typename heap::color_ref c =
                                          h.get_color_ref(st_blue.front().s);
                    assert(!c.is_white());
                    bdd acu = f_dest.acc | c.get_acc();
                    bdd acp = (use_ordering ? project_acc(acu) : acu);
                    if ((c_prime.get_acc() & acp) != acp)
                      {
                        trace << "  The arc from "
                              << a_->format_state(st_blue.front().s)
                              << " to the current state implies to "
                              << " start a red dfs" << std::endl;
                        c_prime.cumulate_acc(acp);
                        push(st_red, f_dest.s, f_dest.label, f_dest.acc);
                        if (dfs_red(acu))
                            return true;
                      }
                    else
                      {
                        trace << "  Pop it" << std::endl;
                        h.pop_notify(f_dest.s);
                      }
                  }
                else
                  {
                    trace << "  Pop it" << std::endl;
                    h.pop_notify(f_dest.s);
                  }
              }
          }
        return false;
      }

      bool
      dfs_red(bdd acu)
      {
        assert(!st_red.empty());

	// These are useful only when USE_CONDITION_STACK is set.
	typedef std::pair<bdd, unsigned> cond_level;
	std::stack<cond_level> condition_stack;
	unsigned depth = 1;
	condition_stack.push(cond_level(bddfalse, 0));

        while (!st_red.empty())
          {
            stack_item& f = st_red.front();
            trace << "DFS_RED treats: " << a_->format_state(f.s) << std::endl;
            if (!f.it->done())
              {
                const state *s_prime = f.it->current_state();
                trace << "  Visit the successor: "
                      << a_->format_state(s_prime) << std::endl;
                bdd label = f.it->current_condition();
                bdd acc = f.it->current_acceptance_conditions();
                // Go down the edge (f.s, <label, acc>, s_prime)
                f.it->next();
                inc_transitions();
                typename heap::color_ref c_prime = h.get_color_ref(s_prime);
                if (c_prime.is_white())
                  {
                    trace << "  It is white, pop it" << std::endl;
                    delete s_prime;
                    continue;
                  }
		else if (c_prime.get_color() == CYAN &&
			 (all_acc == ((use_red_weights ?
				       (current_weight - c_prime.get_weight())
				       : bddfalse)
				      | c_prime.get_acc()
				      | acc
				      | acu)))
		  {
		    trace << "  It is cyan and acceptance condition "
			  << "is reached, report cycle" << std::endl;
		    c_prime.cumulate_acc(all_acc);
		    push(st_red, s_prime, label, acc);
		    return true;
		  }
		bdd acp;
		if (use_ordering)
		  acp = project_acc(c_prime.get_acc() | acu | acc);
		else if (use_condition_stack)
		  acp = acu | acc;
		else
		  acp = acu;
		if ((c_prime.get_acc() & acp) != acp)
		  {
		    trace << "  It is cyan or blue and propagation "
			  << "is needed, go down"
			  << std::endl;
		    c_prime.cumulate_acc(acp);
		    push(st_red, s_prime, label, acc);
		    if (use_condition_stack)
		      {
			bdd old = acu;
			acu |= acc;
			condition_stack.push(cond_level(acu - old, depth));
		      }
		    ++depth;
		  }
		else
		  {
		    trace << "  It is cyan or blue and no propagation "
			  << "is needed , pop it" << std::endl;
		    h.pop_notify(s_prime);
		  }
              }
            else // Backtrack
              {
                trace << "  All the successors have been visited, pop it"
                      << std::endl;
                h.pop_notify(f.s);
                pop(st_red);
		--depth;
		if (condition_stack.top().second == depth)
		  {
		    acu -= condition_stack.top().first;
		    condition_stack.pop();
		  }
              }
          }
	assert(depth == 0);
	assert(condition_stack.empty());
        return false;
      }

    };

    class explicit_tau03_opt_search_heap
    {
      typedef Sgi::hash_map<const state*, std::pair<weight, bdd>,
                state_ptr_hash, state_ptr_equal> hcyan_type;
      typedef Sgi::hash_map<const state*, std::pair<color, bdd>,
                state_ptr_hash, state_ptr_equal> hash_type;
    public:
      class color_ref
      {
      public:
        color_ref(hash_type* h, hcyan_type* hc, const state* s,
            const weight* w, bdd* a)
          : is_cyan(true), w(w), ph(h), phc(hc), ps(s), acc(a)
          {
          }
        color_ref(color* c, bdd* a)
          : is_cyan(false), pc(c), acc(a)
          {
          }
        color get_color() const
          {
            if (is_cyan)
              return CYAN;
            return *pc;
          }
        const weight& get_weight() const
          {
            assert(is_cyan);
            return *w;
          }
        void set_color(color c)
          {
            assert(!is_white());
            if (is_cyan)
              {
                assert(c != CYAN);
                std::pair<hash_type::iterator, bool> p;
                p = ph->insert(std::make_pair(ps, std::make_pair(c, *acc)));
                assert(p.second);
                acc = &(p.first->second.second);
                int i = phc->erase(ps);
                assert(i==1);
                (void)i;
              }
            else
              {
                *pc=c;
              }
          }
        const bdd& get_acc() const
          {
            assert(!is_white());
            return *acc;
          }
        void cumulate_acc(const bdd& a)
          {
            assert(!is_white());
            *acc |= a;
          }
        bool is_white() const
          {
            return !is_cyan && pc == 0;
          }
      private:
        bool is_cyan;
        const weight* w; // point to the weight of a state in hcyan
        hash_type* ph; //point to the main hash table
        hcyan_type* phc; // point to the hash table hcyan
        const state* ps; // point to the state in hcyan
        color *pc; // point to the color of a state stored in main hash table
        bdd* acc; // point to the acc set of a state stored in main hash table
                  // or hcyan
      };

      explicit_tau03_opt_search_heap(size_t)
        {
        }

      ~explicit_tau03_opt_search_heap()
        {
          hcyan_type::const_iterator sc = hc.begin();
          while (sc != hc.end())
            {
              const state* ptr = sc->first;
              ++sc;
              delete ptr;
            }
          hash_type::const_iterator s = h.begin();
          while (s != h.end())
            {
              const state* ptr = s->first;
              ++s;
              delete ptr;
            }
        }

      color_ref get_color_ref(const state*& s)
        {
          hcyan_type::iterator ic = hc.find(s);
          if (ic==hc.end())
            {
              hash_type::iterator it = h.find(s);
              if (it==h.end())
                // white state
                return color_ref(0, 0);
              if (s!=it->first)
                {
                  delete s;
                  s = it->first;
                }
              // blue or red state
              return color_ref(&(it->second.first), &(it->second.second));
            }
          if (s!=ic->first)
            {
              delete s;
              s = ic->first;
            }
          // cyan state
          return color_ref(&h, &hc, ic->first,
                              &(ic->second.first), &(ic->second.second));
        }

      void add_new_state(const state* s, color c)
        {
          assert(hc.find(s)==hc.end() && h.find(s)==h.end());
          assert(c != CYAN);
          h.insert(std::make_pair(s, std::make_pair(c, bddfalse)));
        }

      void add_new_state(const state* s, color c, const weight& w)
        {
          assert(hc.find(s)==hc.end() && h.find(s)==h.end());
          assert(c == CYAN);
          (void)c;
          hc.insert(std::make_pair(s, std::make_pair(w, bddfalse)));
        }

      void pop_notify(const state*) const
        {
        }

      bool has_been_visited(const state* s) const
        {
          hcyan_type::const_iterator ic = hc.find(s);
          if (ic == hc.end())
            {
              hash_type::const_iterator it = h.find(s);
              return (it != h.end());
            }
          return true;
        }

      enum { Has_Size = 1 };
      int size() const
        {
          return h.size() + hc.size();
        }

    private:

      // associate to each blue and red state its color and its acceptance set
      hash_type h;
      // associate to each cyan state its weight and its acceptance set
      hcyan_type hc;
    };

  } // anonymous

  emptiness_check* explicit_tau03_opt_search(const tgba *a, option_map o)
  {
    return new tau03_opt_search<explicit_tau03_opt_search_heap>(a, 0, o);
  }

}
