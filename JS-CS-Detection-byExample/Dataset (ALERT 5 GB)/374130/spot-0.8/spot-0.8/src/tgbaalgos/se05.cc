// Copyright (C) 2011 Laboratoire de Recherche et Developpement de
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

//#define TRACE

#include <iostream>
#ifdef TRACE
#define trace std::cerr
#else
#define trace while (0) std::cerr
#endif

#include <cassert>
#include <list>
#include "misc/hash.hh"
#include "tgba/tgba.hh"
#include "emptiness.hh"
#include "emptiness_stats.hh"
#include "se05.hh"
#include "ndfs_result.hxx"

namespace spot
{
  namespace
  {
    enum color {WHITE, CYAN, BLUE, RED};

    /// \brief Emptiness checker on spot::tgba automata having at most one
    /// acceptance condition (i.e. a TBA).
    template <typename heap>
    class se05_search : public emptiness_check, public ec_statistics
    {
    public:
      /// \brief Initialize the Magic Search algorithm on the automaton \a a
      ///
      /// \pre The automaton \a a must have at most one acceptance
      /// condition (i.e. it is a TBA).
      se05_search(const tgba *a, size_t size, option_map o = option_map())
        : emptiness_check(a, o),
          h(size),
          all_cond(a->all_acceptance_conditions())
      {
        assert(a->number_of_acceptance_conditions() <= 1);
      }

      virtual ~se05_search()
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

      /// \brief Perform a Magic Search.
      ///
      /// \return non null pointer iff the algorithm has found a
      /// new accepting path.
      ///
      /// check() can be called several times (until it returns a null
      /// pointer) to enumerate all the visited accepting paths. The method
      /// visits only a finite set of accepting paths.
      virtual emptiness_check_result* check()
      {
        if (st_red.empty())
          {
            assert(st_blue.empty());
            const state* s0 = a_->get_init_state();
            inc_states();
            h.add_new_state(s0, CYAN);
            push(st_blue, s0, bddfalse, bddfalse);
            if (dfs_blue())
              return new se05_result(*this, options());
          }
        else
          {
            h.pop_notify(st_red.front().s);
            pop(st_red);
            if (!st_red.empty() && dfs_red())
              return new se05_result(*this, options());
            else
              if (dfs_blue())
                return new se05_result(*this, options());
          }
        return 0;
      }

      virtual std::ostream& print_stats(std::ostream &os) const
      {
        os << states() << " distinct nodes visited" << std::endl;
        os << transitions() << " transitions explored" << std::endl;
        os << max_depth() << " nodes for the maximal stack depth" << std::endl;
        if (!st_red.empty())
          {
            assert(!st_blue.empty());
            os << st_blue.size() + st_red.size() - 1
               << " nodes for the counter example" << std::endl;
          }
        return os;
      }

      virtual bool safe() const
      {
	return heap::Safe;
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

      /// \brief Stack of the blue dfs.
      stack_type st_blue;

      /// \brief Stack of the red dfs.
      stack_type st_red;

      /// \brief Map where each visited state is colored
      /// by the last dfs visiting it.
      heap h;

      /// The unique acceptance condition of the automaton \a a.
      bdd all_cond;

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
                typename heap::color_ref c = h.get_color_ref(s_prime);
                if (c.is_white())
                  {
                    trace << "  It is white, go down" << std::endl;
                    inc_states();
                    h.add_new_state(s_prime, CYAN);
                    push(st_blue, s_prime, label, acc);
                  }
                else if (c.get_color() == CYAN && (acc == all_cond ||
                             (f.s->compare(s_prime) != 0 && f.acc == all_cond)))
                  {
                    trace << "  It is cyan and acceptance condition "
                          << "is reached, report cycle" << std::endl;
                    c.set_color(RED);
                    push(st_red, s_prime, label, acc);
                    return true;
                  }
                else if (acc == all_cond && c.get_color() != RED)
                  {
                    // the test 'c.get_color() != RED' is added to limit
                    // the number of runs reported by successive
                    // calls to the check method. Without this
                    // functionnality, the test can be ommited.
                    trace << "  It is cyan or blue and the arc is "
                          << "accepting, start a red dfs" << std::endl;
                    c.set_color(RED);
                    push(st_red, s_prime, label, acc);
                    if (dfs_red())
                      return true;
                  }
                else
                  {
                    trace << "  It is cyan, blue or red, pop it" << std::endl;
                    h.pop_notify(s_prime);
                  }
              }
            else
            // Backtrack the edge
            //        (predecessor of f.s in st_blue, <f.label, f.acc>, f.s)
              {
                trace << "  All the successors have been visited" << std::endl;
                stack_item f_dest(f);
                pop(st_blue);
                typename heap::color_ref c = h.get_color_ref(f_dest.s);
                assert(!c.is_white());
                if (!st_blue.empty() &&
                          f_dest.acc == all_cond && c.get_color() != RED)
                  {
                    // the test 'c.get_color() != RED' is added to limit
                    // the number of runs reported by successive
                    // calls to the check method. Without this
                    // functionnality, the test can be ommited.
                    trace << "  The arc from "
                          << a_->format_state(st_blue.front().s)
                          << " to the current state is accepting, start a "
                          << "red dfs" << std::endl;
                    c.set_color(RED);
                    push(st_red, f_dest.s, f_dest.label, f_dest.acc);
                    if (dfs_red())
                      return true;
                  }
                else
                  {
                    trace << "  Pop it" << std::endl;
                    c.set_color(BLUE);
                    h.pop_notify(f_dest.s);
                  }
              }
          }
        return false;
      }

      bool dfs_red()
      {
        assert(!st_red.empty());

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
                typename heap::color_ref c = h.get_color_ref(s_prime);
                if (c.is_white())
                  {
                    // For an explicit search, we can pose assert(!c.is_white())
                    // because to reach a white state, the red dfs must
                    // have crossed a cyan one (a state in the blue stack)
                    // implying the report of a cycle.
                    // However, with a bit-state hashing search and due to
                    // collision, this property does not hold.
                    trace << "  It is white (due to collision), pop it"
                          << std::endl;
                    s_prime->destroy();
                  }
                else if (c.get_color() == RED)
                  {
                    trace << "  It is red, pop it" << std::endl;
                    h.pop_notify(s_prime);
                  }
                else if (c.get_color() == CYAN)
                  {
                    trace << "  It is cyan, report a cycle" << std::endl;
                    c.set_color(RED);
                    push(st_red, s_prime, label, acc);
                    return true;
                  }
                else
                  {
                    trace << "  It is blue, go down" << std::endl;
                    c.set_color(RED);
                    push(st_red, s_prime, label, acc);
                  }
              }
            else // Backtrack
              {
                trace << "  All the successors have been visited, pop it"
                      << std::endl;
                h.pop_notify(f.s);
                pop(st_red);
              }
          }
        return false;
      }

      class result_from_stack: public emptiness_check_result,
        public acss_statistics
      {
      public:
        result_from_stack(se05_search& ms)
          : emptiness_check_result(ms.automaton()), ms_(ms)
        {
        }

        virtual tgba_run* accepting_run()
        {
          assert(!ms_.st_blue.empty());
          assert(!ms_.st_red.empty());

          tgba_run* run = new tgba_run;

          typename stack_type::const_reverse_iterator i, j, end;
          tgba_run::steps* l;

          const state* target = ms_.st_red.front().s;

          l = &run->prefix;

          i = ms_.st_blue.rbegin();
          end = ms_.st_blue.rend(); --end;
          j = i; ++j;
          for (; i != end; ++i, ++j)
            {
              if (l == &run->prefix && i->s->compare(target) == 0)
                l = &run->cycle;
              tgba_run::step s = { i->s->clone(), j->label, j->acc };
              l->push_back(s);
            }

          if (l == &run->prefix && i->s->compare(target) == 0)
            l = &run->cycle;
          assert(l == &run->cycle);

          j = ms_.st_red.rbegin();
          tgba_run::step s = { i->s->clone(), j->label, j->acc };
          l->push_back(s);

          i = j; ++j;
          end = ms_.st_red.rend(); --end;
          for (; i != end; ++i, ++j)
            {
              tgba_run::step s = { i->s->clone(), j->label, j->acc };
              l->push_back(s);
            }

          return run;
        }

        unsigned acss_states() const
        {
          return 0;
        }
      private:
        se05_search& ms_;
      };

#     define FROM_STACK "ar:from_stack"

      class se05_result: public emptiness_check_result
      {
      public:
        se05_result(se05_search& m, option_map o = option_map())
          : emptiness_check_result(m.automaton(), o), ms(m)
        {
          if (options()[FROM_STACK])
            computer = new result_from_stack(ms);
          else
            computer = new ndfs_result<se05_search<heap>, heap>(ms);
        }

        virtual void options_updated(const option_map& old)
        {
          if (old[FROM_STACK] && !options()[FROM_STACK])
            {
              delete computer;
              computer = new ndfs_result<se05_search<heap>, heap>(ms);
            }
          else if (!old[FROM_STACK] && options()[FROM_STACK])
            {
              delete computer;
              computer = new result_from_stack(ms);
            }
        }

        virtual ~se05_result()
        {
          delete computer;
        }

        virtual tgba_run* accepting_run()
        {
          return computer->accepting_run();
        }

        virtual const unsigned_statistics* statistics() const
        {
          return computer->statistics();
        }

      private:
        emptiness_check_result* computer;
        se05_search& ms;
      };
    };

    class explicit_se05_search_heap
    {
      typedef Sgi::hash_set<const state*,
                state_ptr_hash, state_ptr_equal> hcyan_type;
      typedef Sgi::hash_map<const state*, color,
                state_ptr_hash, state_ptr_equal> hash_type;
    public:
      enum { Safe = 1 };

      class color_ref
      {
      public:
        color_ref(hash_type* h, hcyan_type* hc, const state* s)
          : is_cyan(true), ph(h), phc(hc), ps(s), pc(0)
          {
          }
        color_ref(color* c)
          : is_cyan(false), ph(0), phc(0), ps(0), pc(c)
          {
          }
        color get_color() const
          {
            if (is_cyan)
              return CYAN;
            return *pc;
          }
        void set_color(color c)
          {
            assert(!is_white());
            if (is_cyan)
              {
                assert(c != CYAN);
                int i = phc->erase(ps);
                assert(i == 1);
                (void)i;
                ph->insert(std::make_pair(ps, c));
              }
            else
              {
                *pc=c;
              }
          }
        bool is_white() const
          {
            return !is_cyan && pc == 0;
          }
      private:
        bool is_cyan;
        hash_type* ph; //point to the main hash table
        hcyan_type* phc; // point to the hash table hcyan
        const state* ps; // point to the state in hcyan
        color *pc; // point to the color of a state stored in main hash table
      };

      explicit_se05_search_heap(size_t)
        {
        }

      ~explicit_se05_search_heap()
        {
          hcyan_type::const_iterator sc = hc.begin();
          while (sc != hc.end())
            {
              const state* ptr = *sc;
              ++sc;
              ptr->destroy();
            }
          hash_type::const_iterator s = h.begin();
          while (s != h.end())
            {
              const state* ptr = s->first;
              ++s;
              ptr->destroy();
            }
        }

      color_ref get_color_ref(const state*& s)
        {
          hcyan_type::iterator ic = hc.find(s);
          if (ic == hc.end())
            {
              hash_type::iterator it = h.find(s);
              if (it == h.end())
                return color_ref(0); // white state
              if (s != it->first)
                {
                  s->destroy();
                  s = it->first;
                }
              return color_ref(&it->second); // blue or red state
            }
          if (s != *ic)
            {
              s->destroy();
              s = *ic;
            }
          return color_ref(&h, &hc, *ic); // cyan state
        }

      void add_new_state(const state* s, color c)
        {
          assert(hc.find(s) == hc.end() && h.find(s) == h.end());
          if (c == CYAN)
            hc.insert(s);
          else
            h.insert(std::make_pair(s, c));
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

      hash_type h; // associate to each blue and red state its color
      hcyan_type hc; // associate to each cyan state its weight
    };

    class bsh_se05_search_heap
    {
    private:
      typedef Sgi::hash_set<const state*,
                state_ptr_hash, state_ptr_equal> hcyan_type;
    public:
      enum { Safe = 0 };

      class color_ref
      {
      public:
        color_ref(hcyan_type* h, const state* st,
                    unsigned char *base, unsigned char offset)
          : is_cyan(true), phc(h), ps(st), b(base), o(offset*2)
          {
          }
        color_ref(unsigned char *base, unsigned char offset)
          : is_cyan(false), phc(0), ps(0), b(base), o(offset*2)
          {
          }
        color get_color() const
          {
            if (is_cyan)
              return CYAN;
            return color(((*b) >> o) & 3U);
          }
        void set_color(color c)
          {
            if (is_cyan && c != CYAN)
              {
                int i = phc->erase(ps);
                assert(i == 1);
                (void)i;
              }
            *b =  (*b & ~(3U << o)) | (c << o);
          }
        bool is_white() const
          {
            return get_color() == WHITE;
          }
      private:
        bool is_cyan;
        hcyan_type* phc;
        const state* ps;
        unsigned char *b;
        unsigned char o;
      };

      bsh_se05_search_heap(size_t s) : size_(s)
        {
          h = new unsigned char[size_];
          memset(h, WHITE, size_);
        }

      ~bsh_se05_search_heap()
        {
          delete[] h;
        }

      color_ref get_color_ref(const state*& s)
        {
          size_t ha = s->hash();
          hcyan_type::iterator ic = hc.find(s);
          if (ic != hc.end())
            return color_ref(&hc, *ic, &h[ha%size_], ha%4);
          return color_ref(&h[ha%size_], ha%4);
        }

      void add_new_state(const state* s, color c)
        {
          assert(get_color_ref(s).is_white());
          if (c == CYAN)
            hc.insert(s);
          else
            {
              color_ref cr(get_color_ref(s));
              cr.set_color(c);
            }
        }

      void pop_notify(const state* s) const
        {
          s->destroy();
        }

      bool has_been_visited(const state* s) const
        {
          hcyan_type::const_iterator ic = hc.find(s);
          if (ic != hc.end())
            return true;
          size_t ha = s->hash();
          return color((h[ha%size_] >> ((ha%4)*2)) & 3U) != WHITE;
        }

      enum { Has_Size = 0 };

    private:
      size_t size_;
      unsigned char* h;
      hcyan_type hc;
    };

  } // anonymous

  emptiness_check* explicit_se05_search(const tgba *a, option_map o)
  {
    return new se05_search<explicit_se05_search_heap>(a, 0, o);
  }

  emptiness_check* bit_state_hashing_se05_search(const tgba *a, size_t size,
                                                option_map o)
  {
    return new se05_search<bsh_se05_search_heap>(a, size, o);
  }

  emptiness_check*
  se05(const tgba *a, option_map o)
  {
    size_t size = o.get("bsh");
    if (size)
      return bit_state_hashing_se05_search(a, size, o);
    return explicit_se05_search(a, o);
  }

}
