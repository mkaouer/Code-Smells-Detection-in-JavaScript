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
#include "bddprint.hh"
#include "state.hh"
#include "tgbakvcomplement.hh"
#include "misc/hash.hh"
#include "tgbaalgos/bfssteps.hh"
#include "misc/hashfunc.hh"
#include "ltlast/formula.hh"
#include "ltlast/constant.hh"
#include "tgbaalgos/stats.hh"

namespace spot
{
  namespace
  {
    ////////////////////////////////////////
    // rank

    /// \brief A rank structure, one of the main structure of the algorithm.
    ///
    /// A rank has a number (\a rank) that refers to the depth in the DAG of
    /// the current word. When the rank is odd, a \a condition is associated
    /// to this rank.
    struct rank_t
    {
      mutable unsigned rank;
      mutable bdd_ordered condition;

      bool operator<(const rank_t& other) const
      {
        return rank < other.rank ||
          condition.order() < other.condition.order();
      }

      unsigned get_rank() const
      {
        return rank;
      }

      bdd_ordered get_condition() const
      {
        return condition;
      }

      size_t hash() const
      {
        size_t hash = wang32_hash(rank);
        if (rank & 1)
          hash ^= wang32_hash(condition.order());
        return hash;
      }

      std::string format(const tgba* a) const
      {
        std::ostringstream ss;
        ss << "{rank: " << rank;
        if (rank & 1)
        {
          ss << ", bdd: {" << condition.order() << ", "
             << bdd_format_accset(a->get_dict(), condition.get_bdd())
             << "} ";
        }
        ss << "}";
        return ss.str();
      }
    };

    // typedefs.
    typedef Sgi::hash_map<shared_state, rank_t,
                          state_shared_ptr_hash,
                          state_shared_ptr_equal> state_rank_map;
    typedef Sgi::hash_set<shared_state,
                          state_shared_ptr_hash,
                          state_shared_ptr_equal> state_set;

    ////////////////////////////////////////
    // state_kv_complement

    /// States used by spot::tgba_kv_complement.
    /// A state has a map of states associated to ranks, and a set
    /// of filtered states.
    /// \ingroup tgba_representation
    class state_kv_complement : public state
    {
    public:
      state_kv_complement();
      state_kv_complement(state_rank_map state_map, state_set state_filter);
      virtual ~state_kv_complement() {}

      virtual int compare(const state* other) const;
      virtual size_t hash() const;
      virtual state_kv_complement* clone() const;

      void add(shared_state state, const rank_t& rank);
      const state_rank_map& get_state_map() const;
      const state_set& get_filter_set() const;
      bool accepting() const;
    private:
      state_rank_map state_map_;
      state_set state_filter_;
    };

    state_kv_complement::state_kv_complement()
    {
    }

    state_kv_complement::state_kv_complement(state_rank_map state_map,
                                       state_set state_filter)
      : state_map_(state_map), state_filter_(state_filter)
    {
    }

    int
    state_kv_complement::compare(const state* o) const
    {
      const state_kv_complement* other =
	down_cast<const state_kv_complement*>(o);

      if (other == 0)
        return 1;

      if (state_map_.size() < other->state_map_.size())
        return -1;
      else if (state_map_.size() > other->state_map_.size())
        return 1;

      if (state_filter_.size() < other->state_filter_.size())
        return -1;
      else if (state_filter_.size() > other->state_filter_.size())
        return 1;

      {
        state_rank_map::const_iterator i = state_map_.begin();
        state_rank_map::const_iterator j = other->state_map_.begin();
        while (i != state_map_.end() && j != other->state_map_.end())
        {
          int result = i->first->compare(j->first.get());
          if (result != 0)
            return result;
          if (i->second < j->second)
            return -1;
          if (j->second < i->second)
            return 1;
          ++i;
          ++j;
        }
      }

      {
        state_set::const_iterator i = state_filter_.begin();
        state_set::const_iterator j = other->state_filter_.begin();
        while (i != state_filter_.end() && j != other->state_filter_.end())
        {
          int result = (*i)->compare(j->get());
          if (result != 0)
            return result;
          ++i;
          ++j;
        }
      }

      return 0;
    }

    size_t
    state_kv_complement::hash() const
    {
      size_t hash = 0;

      {
        state_rank_map::const_iterator i = state_map_.begin();
        while (i != state_map_.end())
        {
          hash ^= i->first->hash();
          hash ^= i->second.hash();
          ++i;
        }
      }

      {
        state_set::const_iterator i = state_filter_.begin();
        while (i != state_filter_.end())
        {
          hash ^= (*i)->hash();
          ++i;
        }
      }

      return hash;
    }

    state_kv_complement*
    state_kv_complement::clone() const
    {
      return new state_kv_complement(*this);
    }

    void
    state_kv_complement::add(shared_state state,
                          const rank_t& rank)
    {
      state_map_[state] = rank;
    }

    const state_rank_map&
    state_kv_complement::get_state_map() const
    {
      return state_map_;
    }

    const state_set&
    state_kv_complement::get_filter_set() const
    {
      return state_filter_;
    }

    bool
    state_kv_complement::accepting() const
    {
      return state_filter_.empty();
    }

    /// Successor iterators used by spot::tgba_kv_complement.
    /// \ingroup tgba_representation
    ///
    /// Since the algorithm works on-the-fly, the key components of the
    /// algorithm are implemented in this class.
    ///
    ///
    class tgba_kv_complement_succ_iterator: public tgba_succ_iterator
    {
    public:
      typedef std::list<bdd> bdd_list_t;

      tgba_kv_complement_succ_iterator(const tgba_sgba_proxy* automaton,
				       bdd the_acceptance_cond,
				       const acc_list_t& acc_list,
				       const state_kv_complement* origin);
      virtual ~tgba_kv_complement_succ_iterator() {};

      virtual void first();
      virtual void next();
      virtual bool done() const;
      virtual state_kv_complement* current_state() const;
      virtual bdd current_condition() const;
      virtual bdd current_acceptance_conditions() const;
    private:
      /// \brief Create the highest rank of \a origin_ as origin and
      /// \a condition as successor condition.
      void successor_highest_rank(bdd condition);
      void get_atomics(std::set<int>& list, bdd c);
      void get_conj_list();
      bool is_valid_rank() const;
      bool next_valid_rank();

      const tgba_sgba_proxy* automaton_;
      bdd the_acceptance_cond_;
      const acc_list_t& acc_list_;
      const state_kv_complement* origin_;
      const state_kv_complement* current_state_;
      bdd_list_t condition_list_;
      bdd_list_t::const_iterator current_condition_;
      state_rank_map highest_current_ranks_;
      state_rank_map current_ranks_;
      state_set highest_state_set_;
    };

    tgba_kv_complement_succ_iterator::
    tgba_kv_complement_succ_iterator(const tgba_sgba_proxy* automaton,
				     bdd the_acceptance_cond,
				     const acc_list_t& acc_list,
				     const state_kv_complement* origin)
      : automaton_(automaton), the_acceptance_cond_(the_acceptance_cond),
        acc_list_(acc_list), origin_(origin)
    {
      get_conj_list();
    }

    /// Insert in \a list atomic properties of the formula \a c.
    void
    tgba_kv_complement_succ_iterator::get_atomics(std::set<int>& list, bdd c)
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

    /// Create the conjunction of all the atomic properties from
    /// the successors of the current state.
    void
    tgba_kv_complement_succ_iterator::get_conj_list()
    {
      std::set<int> atomics;
      condition_list_.clear();
      state_rank_map sr_map = origin_->get_state_map();

      // Retrieve all the atomics in acceptance conditions.
      for (state_rank_map::const_iterator i = sr_map.begin();
           i != sr_map.end();
           ++i)
      {
        tgba_succ_iterator* iterator = automaton_->succ_iter(i->first.get());
        for (iterator->first(); !iterator->done(); iterator->next())
        {
          bdd c = iterator->current_condition();
          get_atomics(atomics, c);
        }
        delete iterator;
      }

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
        condition_list_.push_back(result);
      }
    }

    /// Check whether \a current_ranks_ is a valid rank.
    /// For each odd rank, its condition associated must not
    /// be present in its tracked state.
    bool
    tgba_kv_complement_succ_iterator::is_valid_rank() const
    {
      for (state_rank_map::const_iterator i = current_ranks_.begin();
           i != current_ranks_.end();
           ++i)
      {
        if (i->second.rank & 1)
	  {
          if ((automaton_->state_acceptance_conditions(i->first.get()) &
               i->second.condition.get_bdd()) != bddfalse)
            return false;
	  }
      }

      return true;
    }

    /// \brief Decrease \a current_ranks_ and produces a valid rank.
    /// \a current_ranks_ is a map of states to a rank.
    /// A rank for a state is valid if it is inferior than the rank of its
    /// predecessor.
    /// When the rank is odd, its has an acceptance condition associated that
    /// must not be in its associated state.
    /// \return false if there is not valid rank as successor.
    bool tgba_kv_complement_succ_iterator::next_valid_rank()
    {
      state_rank_map::const_iterator i;
      do
	{
	  for (i = current_ranks_.begin(); i != current_ranks_.end(); ++i)
	    {
	      if (i->second.rank != 0)
		{
		  if (i->second.rank & 1)
		    {
		      if (i->second.condition.order() == 0)
			--i->second.rank;
		      else
			i->second.condition =
			  acc_list_[i->second.condition.order() - 1];
		    }
		  else
		    {
		      --i->second.rank;
		      i->second.condition = acc_list_[acc_list_.size() - 1];
		    }
		  break;
		}
	      else
		{
		  current_ranks_[i->first] = highest_current_ranks_[i->first];
		}
	    }
	}
      while ((i != current_ranks_.end()) && !is_valid_rank());

      return i != current_ranks_.end();
    }

    /// \brief Create the highest rank of \a origin_ as origin and
    /// \a condition as successor condition.
    void
    tgba_kv_complement_succ_iterator::successor_highest_rank(bdd condition)
    {
      // Highest rank for bdd.
      state_rank_map sr_map = origin_->get_state_map();
      highest_current_ranks_.clear();

      for (state_rank_map::const_iterator i = sr_map.begin();
           i != sr_map.end();
           ++i)
      {
        tgba_succ_iterator* iterator = automaton_->succ_iter(i->first.get());
        for (iterator->first(); !iterator->done(); iterator->next())
        {
          bdd c = iterator->current_condition();
          if ((c & condition) != bddfalse)
          {
            shared_state s(iterator->current_state(), shared_state_deleter);
            if (highest_current_ranks_.find(s) != highest_current_ranks_.end())
            {
              if (i->second < highest_current_ranks_[s])
                highest_current_ranks_[s] = i->second;
            }
            else
              highest_current_ranks_[s] = i->second;
          }
        }
        delete iterator;
      }

      // Highest $O$ set of the algorithm.
      state_set s_set = origin_->get_filter_set();
      highest_state_set_.clear();

      for (state_set::const_iterator i = s_set.begin();
           i != s_set.end();
           ++i)
      {
        tgba_succ_iterator* iterator = automaton_->succ_iter(i->get());
        for (iterator->first(); !iterator->done(); iterator->next())
        {
          bdd c = iterator->current_condition();
          if ((c & condition) != bddfalse)
          {
            shared_state s(iterator->current_state(), shared_state_deleter);
            highest_state_set_.insert(s);
          }
        }
        delete iterator;
      }

      current_ranks_ = highest_current_ranks_;
    }

    void
    tgba_kv_complement_succ_iterator::first()
    {
      current_condition_ = condition_list_.begin();
      if (done())
        return;

      successor_highest_rank(*current_condition_);

      if (!is_valid_rank())
        next_valid_rank();
    }

    void
    tgba_kv_complement_succ_iterator::next()
    {
      if (done())
        return;

      if (!next_valid_rank())
      {
        ++current_condition_;
        if (!done())
        {
          successor_highest_rank(*current_condition_);
          if (!is_valid_rank())
            next_valid_rank();
        }
      }
    }

    bool
    tgba_kv_complement_succ_iterator::done() const
    {
      return (current_condition_ == condition_list_.end());
    }

    state_kv_complement*
    tgba_kv_complement_succ_iterator::current_state() const
    {
      if (done())
        return 0;

      // If the filter set is empty, all the states of the map
      // that are associated to an even rank create the new filter set.
      state_set filter;
      if (origin_->get_filter_set().empty())
      {
        for (state_rank_map::const_iterator i = current_ranks_.begin();
             i != current_ranks_.end();
             ++i)
          if (!(i->second.rank & 1))
            filter.insert(i->first);
      }
      else
      {
        // It the filter set is non-empty, we delete from this set states
        // that are now associated to an odd rank.
        for (state_set::const_iterator i = highest_state_set_.begin();
             i != highest_state_set_.end();
             ++i)
        {
          state_rank_map::const_iterator s(current_ranks_.find(*i));
          assert(s != current_ranks_.end());

          if (!(s->second.get_rank() & 1))
            filter.insert(*i);
        }
      }

      return new state_kv_complement(current_ranks_, filter);
    }

    bdd
    tgba_kv_complement_succ_iterator::current_condition() const
    {
      if (done())
        return bddfalse;
      return *current_condition_;
    }

    bdd
    tgba_kv_complement_succ_iterator::current_acceptance_conditions() const
    {
      if (done())
        return bddfalse;

      // This algorithm doesn't generalized acceptance conditions.
      if (origin_->accepting())
        return the_acceptance_cond_;
      else
        return bddfalse;
    }

  } // end namespace anonymous.

  /// Retrieve all the atomic acceptance conditions of the automaton.
  /// They are inserted into \a acc_list_.
  void
  tgba_kv_complement::get_acc_list()
  {
    bdd c = automaton_->all_acceptance_conditions();
    bdd current = bdd_satone(c);
    unsigned i = 0;
    while (current != bddtrue && current != bddfalse)
    {
      acc_list_.push_back(bdd_ordered(bdd_var(current), i));
      ++i;
      bdd high = bdd_high(current);
      if (high == bddfalse)
        current = bdd_low(current);
      else
        current = high;
    }
  }

  tgba_kv_complement::tgba_kv_complement(const tgba* a)
    : automaton_(new tgba_sgba_proxy(a))
  {
    get_dict()->register_all_variables_of(automaton_, this);
    int v = get_dict()
      ->register_acceptance_variable(ltl::constant::true_instance(), this);
    the_acceptance_cond_ = bdd_ithvar(v);
    {
      spot::tgba_statistics a_size =  spot::stats_reachable(automaton_);
      nb_states_ = a_size.states;
    }
    get_acc_list();
  }

  tgba_kv_complement::~tgba_kv_complement()
  {
    get_dict()->unregister_all_my_variables(this);
    delete automaton_;
  }

  state*
  tgba_kv_complement::get_init_state() const
  {
    state_kv_complement* init = new state_kv_complement();
    rank_t r = {2 * nb_states_, bdd_ordered()};
    init->add(shared_state(automaton_->get_init_state(), shared_state_deleter),
	      r);
    return init;
  }

  tgba_succ_iterator*
  tgba_kv_complement::succ_iter(const state* local_state,
                             const state*,
                             const tgba*) const
  {
    const state_kv_complement* state =
      down_cast<const state_kv_complement*>(local_state);
    assert(state);

    return new tgba_kv_complement_succ_iterator(automaton_,
                                             the_acceptance_cond_,
                                             acc_list_, state);
  }

  bdd_dict*
  tgba_kv_complement::get_dict() const
  {
    return automaton_->get_dict();
  }

  std::string
  tgba_kv_complement::format_state(const state* state) const
  {
    const state_kv_complement* s =
      down_cast<const state_kv_complement*>(state);
    assert(s);
    std::ostringstream ss;
    ss << "{ set: {" << std::endl;

    const state_rank_map& state_map = s->get_state_map();
    const state_set& state_filter = s->get_filter_set();

    for (state_rank_map::const_iterator i = state_map.begin();
         i != state_map.end();
         ++i)
    {
      ss << "  {" << automaton_->format_state(i->first.get())
         << ", " << i->second.format(this) << "}" << std::endl;
    }
    ss << "} odd-less: {";

    for (state_set::const_iterator i = state_filter.begin();
         i != state_filter.end();
         ++i)
      ss << "  " << automaton_->format_state(i->get()) << std::endl;
    ss << "} }";
    return ss.str();
  }

  bdd
  tgba_kv_complement::all_acceptance_conditions() const
  {
    return the_acceptance_cond_;
  }

  bdd
  tgba_kv_complement::neg_acceptance_conditions() const
  {
    return !the_acceptance_cond_;
  }

  bdd
  tgba_kv_complement::compute_support_conditions(const state* state) const
  {
    tgba_succ_iterator* i = succ_iter(state);
    bdd result = bddtrue;
    for (i->first(); !i->done(); i->next())
      result |= i->current_condition();
    delete i;
    return result;
  }

  bdd
  tgba_kv_complement::compute_support_variables(const state* state) const
  {
    tgba_succ_iterator* i = succ_iter(state);
    bdd result = bddtrue;
    for (i->first(); !i->done(); i->next())
      result &= bdd_support(i->current_condition());
    delete i;
    return result;
  }

} // end namespace spot.
