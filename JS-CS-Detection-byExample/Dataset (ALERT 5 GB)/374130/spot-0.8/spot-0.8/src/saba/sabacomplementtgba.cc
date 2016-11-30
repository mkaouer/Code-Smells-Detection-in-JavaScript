// Copyright (C) 2009, 2010, 2011 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
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

#include <vector>
#include <cassert>
#include <sstream>
#include "bdd.h"
#include <tgba/bddprint.hh>
#include <tgba/state.hh>
#include "misc/hash.hh"
#include "tgbaalgos/bfssteps.hh"
#include "misc/hashfunc.hh"
#include "ltlast/formula.hh"
#include "ltlast/constant.hh"
#include "tgbaalgos/stats.hh"

#include "sabacomplementtgba.hh"
#include "explicitstateconjunction.hh"

namespace spot
{
  namespace
  {

    // typedefs.
    typedef int rank_t;

    ////////////////////////////////////////
    // saba_state_complement_tgba

    /// States used by spot::saba_complement_tgba.
    /// A state gather a spot::state* and a rank.
    /// \ingroup saba_representation
    class saba_state_complement_tgba : public saba_state
    {
    public:
      saba_state_complement_tgba();
      saba_state_complement_tgba(const saba_state_complement_tgba* state_taa);
      saba_state_complement_tgba(shared_state state, rank_t rank,
                                 bdd condition);
      virtual ~saba_state_complement_tgba() {}

      virtual int compare(const saba_state* other) const;
      virtual size_t hash() const;
      virtual saba_state_complement_tgba* clone() const;

      virtual bdd acceptance_conditions() const;
      const state* get_state() const;
      rank_t get_rank() const;
    private:
      shared_state state_;
      rank_t rank_;
      bdd condition_;
    };

    saba_state_complement_tgba::saba_state_complement_tgba()
    {
    }

    saba_state_complement_tgba::saba_state_complement_tgba(shared_state state,
                                                           rank_t rank,
                                                           bdd condition)
      : state_(state), rank_(rank), condition_(condition)
    {
    }

    saba_state_complement_tgba::
    saba_state_complement_tgba(const saba_state_complement_tgba* state_taa)
      : state_(state_taa->state_), rank_(state_taa->rank_),
        condition_(state_taa->condition_)
    {
    }

    int
    saba_state_complement_tgba::compare(const saba_state* o) const
    {
      const saba_state_complement_tgba* other =
        dynamic_cast<const saba_state_complement_tgba*>(o);

      int compare_value = get_state()->compare(other->get_state());
      if (compare_value != 0)
        return compare_value;

      if (get_rank() != other->get_rank())
        return get_rank() - other->get_rank();

      return acceptance_conditions().id() -
        other->acceptance_conditions().id();
    }

    size_t
    saba_state_complement_tgba::hash() const
    {
      size_t hash = get_state()->hash() ^ wang32_hash(get_rank());
      hash ^= wang32_hash(acceptance_conditions().id());
      return hash;
    }

    saba_state_complement_tgba*
    saba_state_complement_tgba::clone() const
    {
      return new saba_state_complement_tgba(*this);
    }

    bdd
    saba_state_complement_tgba::acceptance_conditions() const
    {
      if ((get_rank() & 1) == 1)
        return condition_;
      else
        return bddfalse;
    }

    const state*
    saba_state_complement_tgba::get_state() const
    {
      return state_.get();
    }

    rank_t
    saba_state_complement_tgba::get_rank() const
    {
      return rank_;
    }


    /// Successor iterators used by spot::saba_complement_tgba.
    /// \ingroup saba_representation
    ///
    /// Since the algorithm works on-the-fly, the key components of the
    /// algorithm are implemented in this class.
    ///
    class saba_complement_tgba_succ_iterator: public saba_succ_iterator
    {
    public:
      typedef std::list<saba_state_conjunction*> state_conjunction_list_t;
      typedef std::map<bdd, state_conjunction_list_t, bdd_less_than>
      bdd_list_t;

      saba_complement_tgba_succ_iterator(const tgba_sba_proxy* automaton,
                                         bdd the_acceptance_cond,
                                         const saba_state_complement_tgba*
                                         origin);
      virtual ~saba_complement_tgba_succ_iterator();

      virtual void first();
      virtual void next();
      virtual bool done() const;
      virtual saba_state_conjunction* current_conjunction() const;
      virtual bdd current_condition() const;
    private:
      void get_atomics(std::set<int>& list, bdd c);
      void get_conj_list();
      void state_conjunction();
      void delete_condition_list();

      const tgba_sba_proxy* automaton_;
      bdd the_acceptance_cond_;
      const saba_state_complement_tgba* origin_;
      bdd_list_t condition_list_;
      bdd_list_t::const_iterator current_condition_;
      state_conjunction_list_t::const_iterator current_conjunction_;
      state_conjunction_list_t::const_iterator current_end_conjunction_;
    };

    saba_complement_tgba_succ_iterator::
    saba_complement_tgba_succ_iterator(const tgba_sba_proxy* automaton,
                                      bdd the_acceptance_cond,
                                      const saba_state_complement_tgba* origin)
      : automaton_(automaton), the_acceptance_cond_(the_acceptance_cond),
        origin_(origin)
    {
      // If state not accepting or rank is even
      if (((origin_->get_rank() & 1) == 0) ||
          !automaton_->state_is_accepting(origin_->get_state()))
      {
        get_conj_list();
        state_conjunction();
      }
    }

    saba_complement_tgba_succ_iterator::
    ~saba_complement_tgba_succ_iterator()
    {
      delete_condition_list();
    }

    void
    saba_complement_tgba_succ_iterator::state_conjunction()
    {
      int max_rank = origin_->get_rank();

      for (bdd_list_t::iterator it = condition_list_.begin();
           it != condition_list_.end();
           ++it)
      {
        // Get successors states.
        bdd condition = it->first;
        tgba_succ_iterator* iterator =
          automaton_->succ_iter(origin_->get_state());
        std::vector<shared_state> state_list;
        for (iterator->first(); !iterator->done(); iterator->next())
        {
          bdd c = iterator->current_condition();
          if ((c & condition) != bddfalse)
            state_list.push_back(shared_state(iterator->current_state(),
					      shared_state_deleter));
        }
        delete iterator;

        // Make the conjunction with ranks.
        std::vector<int> current_ranks(state_list.size(), max_rank);

        if (state_list.empty())
          return;

        do
        {
          explicit_state_conjunction* conj = new explicit_state_conjunction();
          for (unsigned i = 0; i < state_list.size(); ++i)
          {
            conj->add(new saba_state_complement_tgba(state_list[i],
                                                     current_ranks[i],
                                                     the_acceptance_cond_));
          }
          it->second.push_back(conj);

          if (current_ranks[0] <= 0)
            break;

          unsigned order = state_list.size() - 1;
          while (current_ranks[order] == 0)
          {
            current_ranks[order] = max_rank;
            --order;
          }
          --current_ranks[order];
        }
        while (1);
      }
    }

    /// Insert in \a list atomic properties of the formula \a c.
    void
    saba_complement_tgba_succ_iterator::get_atomics(std::set<int>& list, bdd c)
    {
      bdd current = bdd_satone(c);
      while (current != bddtrue && current != bddfalse)
      {
        list.insert(bdd_var(current));
        bdd high = bdd_high(current);
        if (high == bddfalse)
          current = bdd_low(current);
        else
          current = high;
      }
    }

    /// Free the conjunctions in the condition map.
    void
    saba_complement_tgba_succ_iterator::delete_condition_list()
    {
      for (bdd_list_t::iterator i = condition_list_.begin();
           i != condition_list_.end();
           ++i)
      {
        for (state_conjunction_list_t::iterator j = i->second.begin();
             j != i->second.end();
             ++j)
          delete *j;
      }
      condition_list_.clear();
    }

    /// Create the conjunction of all the atomic properties from
    /// the successors of the current state.
    void
    saba_complement_tgba_succ_iterator::get_conj_list()
    {
      std::set<int> atomics;
      delete_condition_list();

      tgba_succ_iterator* iterator =
        automaton_->succ_iter(origin_->get_state());
      for (iterator->first(); !iterator->done(); iterator->next())
      {
        bdd c = iterator->current_condition();
        get_atomics(atomics, c);
      }
      delete iterator;

      // Compute the conjunction of all those atomic properties.
      unsigned atomics_size = atomics.size();

      assert(atomics_size < 32);
      for (unsigned i = 1; i <= static_cast<unsigned>(1 << atomics_size); ++i)
      {
        bdd result = bddtrue;
        unsigned position = 1;
        for (std::set<int>::const_iterator a_it = atomics.begin();
             a_it != atomics.end();
             ++a_it, position <<= 1)
        {
          bdd this_atomic;
          if (position & i)
            this_atomic = bdd_ithvar(*a_it);
          else
            this_atomic = bdd_nithvar(*a_it);
          result = bdd_apply(result, this_atomic, bddop_and);
        }
        condition_list_[result] = state_conjunction_list_t();
      }
    }

    void
    saba_complement_tgba_succ_iterator::first()
    {
      current_condition_ = condition_list_.begin();
      if (current_condition_ != condition_list_.end())
      {
        current_conjunction_ = current_condition_->second.begin();
        current_end_conjunction_ = current_condition_->second.end();
      }
    }

    void
    saba_complement_tgba_succ_iterator::next()
    {
      if (++current_conjunction_ == current_end_conjunction_)
      {
        ++current_condition_;
        if (current_condition_ != condition_list_.end())
        {
          current_conjunction_ = current_condition_->second.begin();
          current_end_conjunction_ = current_condition_->second.end();
        }
      }
    }

    bool
    saba_complement_tgba_succ_iterator::done() const
    {
      return (current_condition_ == condition_list_.end());
    }

    saba_state_conjunction*
    saba_complement_tgba_succ_iterator::current_conjunction() const
    {
      return (*current_conjunction_)->clone();
    }

    bdd
    saba_complement_tgba_succ_iterator::current_condition() const
    {
      return current_condition_->first;
    }

  } // end namespace anonymous.

  saba_complement_tgba::saba_complement_tgba(const tgba* a)
    : automaton_(new tgba_sba_proxy(a))
  {
    get_dict()->register_all_variables_of(automaton_, this);
    int v = get_dict()
      ->register_acceptance_variable(ltl::constant::true_instance(), this);
    the_acceptance_cond_ = bdd_ithvar(v);
    {
      spot::tgba_statistics a_size =  spot::stats_reachable(automaton_);
      nb_states_ = a_size.states;
    }
  }

  saba_complement_tgba::~saba_complement_tgba()
  {
    get_dict()->unregister_all_my_variables(this);
    delete automaton_;
  }

  saba_state*
  saba_complement_tgba::get_init_state() const
  {
    state* original_init_state = automaton_->get_init_state();
    saba_state_complement_tgba* new_init;
    if (automaton_->state_is_accepting(original_init_state))
      new_init =
        new saba_state_complement_tgba(shared_state(original_init_state,
						    shared_state_deleter),
                                       2 * nb_states_,
                                       the_acceptance_cond_);
    else
      new_init =
        new saba_state_complement_tgba(shared_state(original_init_state,
						    shared_state_deleter),
                                       2 * nb_states_,
                                       bddfalse);

    return new_init;
  }

  saba_succ_iterator*
  saba_complement_tgba::succ_iter(const saba_state* local_state) const
  {
    const saba_state_complement_tgba* state =
      dynamic_cast<const saba_state_complement_tgba*>(local_state);
    assert(state);

    return new saba_complement_tgba_succ_iterator(automaton_,
                                                  the_acceptance_cond_,
                                                  state);
  }

  bdd_dict*
  saba_complement_tgba::get_dict() const
  {
    return automaton_->get_dict();
  }

  std::string
  saba_complement_tgba::format_state(const saba_state* state) const
  {
    const saba_state_complement_tgba* s =
      dynamic_cast<const saba_state_complement_tgba*>(state);
    assert(s);
    std::ostringstream ss;
    ss << "{"
       << "original: " << automaton_->format_state(s->get_state()) << std::endl
       << "rank: " << s->get_rank() << "}" << std::endl
       << "acc: " << bdd_format_accset(get_dict(), s->acceptance_conditions())
       << "}";
    return ss.str();
  }

  bdd
  saba_complement_tgba::all_acceptance_conditions() const
  {
    return the_acceptance_cond_;
  }

} // end namespace spot.
