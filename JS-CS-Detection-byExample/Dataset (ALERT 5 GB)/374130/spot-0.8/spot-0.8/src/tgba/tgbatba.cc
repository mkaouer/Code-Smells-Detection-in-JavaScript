// Copyright (C) 2010, 2011 Laboratoire de Recherche et Développement de
// l'Epita.
// Copyright (C) 2003, 2004, 2005 Laboratoire d'Informatique de Paris
// 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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

#include <cassert>
#include "tgbatba.hh"
#include "bddprint.hh"
#include "ltlast/constant.hh"
#include "misc/hashfunc.hh"

namespace spot
{
  namespace
  {
    /// \brief A state for spot::tgba_tba_proxy.
    ///
    /// This state is in fact a pair of states: the state from the tgba
    /// automaton, and a state of the "counter" (we use a pointer
    /// to the position in the cycle_acc_ list).
    class state_tba_proxy: public state
    {
      typedef tgba_tba_proxy::cycle_list::const_iterator iterator;
    public:
      state_tba_proxy(state* s, iterator acc)
	:	s_(s), acc_(acc)
      {
      }

      /// Copy constructor
      state_tba_proxy(const state_tba_proxy& o)
	: state(),
	  s_(o.real_state()->clone()),
	  acc_(o.acceptance_iterator())
      {
      }

      virtual
      ~state_tba_proxy()
      {
	s_->destroy();
      }

      state*
      real_state() const
      {
	return s_;
      }

      bdd
      acceptance_cond() const
      {
	return *acc_;
      }

      iterator
      acceptance_iterator() const
      {
	return acc_;
      }

      virtual int
      compare(const state* other) const
      {
	const state_tba_proxy* o = down_cast<const state_tba_proxy*>(other);
	assert(o);
	int res = s_->compare(o->real_state());
	if (res != 0)
	  return res;
	return acc_->id() - o->acceptance_cond().id();
      }

      virtual size_t
      hash() const
      {
	return wang32_hash(s_->hash()) ^ wang32_hash(acc_->id());
      }

      virtual
      state_tba_proxy* clone() const
      {
	return new state_tba_proxy(*this);
      }

    private:
      state* s_;
      iterator acc_;
    };


    typedef std::pair<const state_tba_proxy*, bool> state_ptr_bool_t;

    struct state_ptr_bool_hash:
      public std::unary_function<const state_ptr_bool_t&,
				 size_t>
    {
      size_t
      operator()(const state_ptr_bool_t& s) const
      {
	if (s.second)
	  return s.first->hash() ^ 12421;
	else
	  return s.first->hash();
      }
    };

    struct state_ptr_bool_equal:
      public std::binary_function<const state_ptr_bool_t&,
				  const state_ptr_bool_t&, bool>
    {
      bool
      operator()(const state_ptr_bool_t& left,
		 const state_ptr_bool_t& right) const
      {
	if (left.second != right.second)
	  return false;
	return left.first->compare(right.first) == 0;
      }
    };

    /// \brief Iterate over the successors of tgba_tba_proxy computed
    /// on the fly.
    class tgba_tba_proxy_succ_iterator: public tgba_succ_iterator
    {
      typedef tgba_tba_proxy::cycle_list list;
      typedef tgba_tba_proxy::cycle_list::const_iterator iterator;
    public:
      tgba_tba_proxy_succ_iterator(const state* rs,
				   tgba_succ_iterator* it,
				   iterator expected,
				   const list& cycle,
				   bdd the_acceptance_cond,
				   const tgba_tba_proxy* aut)
	: the_acceptance_cond_(the_acceptance_cond)
      {
	for (it->first(); !it->done(); it->next())
	  {
	    bool accepting;
	    bdd acc = it->current_acceptance_conditions();
	    // As an extra optimization step, gather the acceptance
	    // conditions common to all outgoing transitions of the
	    // destination state.  We will later add these to "acc" to
	    // pretend they are already present on this transition.
	    state* odest = it->current_state();
	    bdd otheracc =
	      aut->common_acceptance_conditions_of_original_state(odest);

	    iterator next;
	    // bddtrue is a special condition used for tgba_sba_proxy
	    // to denote the (N+1)th copy of the state, after all
	    // acceptance conditions have been traversed.  Such state
	    // is always accepting, so do not check acc for this.
	    // bddtrue is also used by tgba_tba_proxy if the automaton
	    // does not use acceptance conditions.  In that case, all
	    // states are accepting.
	    if (*expected == bddtrue)
	      {
		// When degeneralizing to SBA, ignore the last
		// expected acceptance set (the value of *prev below)
		// if it is common to all other outgoing transitions (of the
		// current state) AND if it is not used by any outgoing
		// transition of the destination state.
		//
		// 1) It's correct to do that, because this acceptance
		//    set is common to other outgoing transitions.
		//    Therefore if we make a cycle to this state we
		//    will eventually see that acceptance set thanks
		//    to the "pulling" of the common acceptance sets
		//    of the destination state (cf. "odest").
		//
		// 2) It's also desirable because it makes the
		//    degeneralization idempotent (up to a renaming of
		//    states).  Consider the following automaton where
		//    1 is initial and => marks accepting transitions:
		//    1=>1, 1=>2, 2->2, 2->1 This is already an SBA,
		//    with 1 as accepting state.  However if you try
		//    degeralize it without ignoring *prev, we'll get
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
		if (expected != cycle.begin())
		  {
		    iterator prev = expected;
		    --prev;
		    bdd common = aut->
		      common_acceptance_conditions_of_original_state(rs);
		    if ((common & *prev) == *prev)
		      {
			bdd u = aut->
			  union_acceptance_conditions_of_original_state(odest);
			if ((u & *prev) != *prev)
			  acc -= *prev;
		      }
		  }
		// Use the acceptance sets common to all outgoing
		// transition of the destination state.  In case of a
		// self-loop, we will be adding back the acceptance
		// set we removed.  This is what we want.
		acc |= otheracc;
	      }
	    else
	      {
		// A transition in the *EXPECTED acceptance set should
		// be directed to the next acceptance set.  If the
		// current transition is also in the next acceptance
		// set, then go to the one after, etc.
		//
		// See Denis Oddoux's PhD thesis for a nice
		// explanation (in French).
		// @PhDThesis{	  oddoux.03.phd,
		//   author	= {Denis Oddoux},
		//   title	= {Utilisation des automates alternants pour un
		// 		  model-checking efficace des logiques
		//		  temporelles lin{\'e}aires.},
		//   school	= {Universit{\'e}e Paris 7},
		//   year	= {2003},
		//   address= {Paris, France},
		//   month	= {December}
		// }
		next = expected;
		// Consider both the current acceptance sets, and the
		// acceptance sets common to the outgoing transition of
		// the destination state.
		acc |= otheracc;
		while (next != cycle.end() && (acc & *next) == *next)
		  ++next;
		if (next != cycle.end())
		  {
		    accepting = false;
		    goto next_is_set;
		  }
	      }
	    // The transition is accepting.
	    accepting = true;
	    // Skip as much acceptance conditions as we can on our cycle.
	    next = cycle.begin();
	    while (next != expected && (acc & *next) == *next)
	      ++next;
	  next_is_set:
	    state_tba_proxy* dest = new state_tba_proxy(odest, next);
	    // Is DEST already reachable with the same value of ACCEPTING?
	    state_ptr_bool_t key(dest, accepting);
	    transmap_t::iterator id = transmap_.find(key);
	    if (id == transmap_.end()) // No
	      {
		mapit_t pos = transmap_
		  .insert(std::make_pair(key, it->current_condition())).first;
		// Keep the order of the transitions in the
		// degeneralized automaton related to the order of the
		// transitions in the input automaton: in the past we
		// used to simply iterate over transmap_ in whatever
		// order the transitions were stored, but the output
		// would change between different runs depending on
		// the memory address of the states.  Now we keep the
		// order using translist_.  We only arrange it
		// slightly so that accepting transitions come first:
		// this way they are processed early during
		// emptiness-check.
		if (accepting)
		  translist_.push_front(pos);
		else
		  translist_.push_back(pos);
	      }
	    else // Yes, combine labels.
	      {
		id->second |= it->current_condition();
		dest->destroy();
	      }
	  }
	delete it;
      }

      virtual
      ~tgba_tba_proxy_succ_iterator()
      {
	for (transmap_t::const_iterator i = transmap_.begin();
	     i != transmap_.end();)
	  {
	    const state* d = i->first.first;
	    // Advance i before deleting d.
	    ++i;
	    d->destroy();
	  }
      }

      // iteration

      void
      first()
      {
	it_ = translist_.begin();
      }

      void
      next()
      {
	++it_;
      }

      bool
      done() const
      {
	return it_ == translist_.end();
      }

      // inspection

      state_tba_proxy*
      current_state() const
      {
	return (*it_)->first.first->clone();
      }

      bdd
      current_condition() const
      {
	return (*it_)->second;
      }

      bdd
      current_acceptance_conditions() const
      {
	return (*it_)->first.second ? the_acceptance_cond_ : bddfalse;
      }

    protected:
      const bdd the_acceptance_cond_;

      typedef Sgi::hash_map<state_ptr_bool_t, bdd,
			    state_ptr_bool_hash,
			    state_ptr_bool_equal> transmap_t;
      transmap_t transmap_;
      typedef transmap_t::const_iterator mapit_t;
      typedef std::list<mapit_t> translist_t;
      translist_t translist_;
      translist_t::const_iterator it_;
    };

  } // anonymous

  tgba_tba_proxy::tgba_tba_proxy(const tgba* a)
    : a_(a)
  {
    // We will use one acceptance condition for this automata.
    // Let's call it Acc[True].
    int v = get_dict()
      ->register_acceptance_variable(ltl::constant::true_instance(), this);
    the_acceptance_cond_ = bdd_ithvar(v);

    if (a->number_of_acceptance_conditions() == 0)
      {
	acc_cycle_.push_front(bddtrue);
      }
    else
      {
	// Build a cycle of expected acceptance conditions.
	//
	// The order is arbitrary, but it turns out that using
	// push_back instead of push_front often gives better results
	// because acceptance conditions at the beginning if the
	// cycle are more often used in the automaton.  (This
	// surprising fact is probably related to the order in which we
	// declare the BDD variables during the translation.)
	bdd all = a_->all_acceptance_conditions();
	while (all != bddfalse)
	  {
	    bdd next = bdd_satone(all);
	    all -= next;
	    acc_cycle_.push_back(next);
	  }
      }
  }

  tgba_tba_proxy::~tgba_tba_proxy()
  {
    get_dict()->unregister_all_my_variables(this);

    accmap_t::const_iterator i = accmap_.begin();
    while (i != accmap_.end())
      {
	// Advance the iterator before deleting the key.
	const state* s = i->first;
	++i;
	s->destroy();
      }
    i = accmapu_.begin();
    while (i != accmapu_.end())
      {
	// Advance the iterator before deleting the key.
	const state* s = i->first;
	++i;
	s->destroy();
      }
  }

  state*
  tgba_tba_proxy::get_init_state() const
  {
    return new state_tba_proxy(a_->get_init_state(), acc_cycle_.begin());
  }

  tgba_succ_iterator*
  tgba_tba_proxy::succ_iter(const state* local_state,
			    const state* global_state,
			    const tgba* global_automaton) const
  {
    const state_tba_proxy* s =
      down_cast<const state_tba_proxy*>(local_state);
    assert(s);

    const state* rs = s->real_state();
    tgba_succ_iterator* it = a_->succ_iter(rs, global_state, global_automaton);

    return new tgba_tba_proxy_succ_iterator(rs, it,
					    s->acceptance_iterator(),
					    acc_cycle_, the_acceptance_cond_,
					    this);
  }

  bdd
  tgba_tba_proxy::common_acceptance_conditions_of_original_state(const state* s)
    const
  {
    // Lookup cache
    accmap_t::const_iterator i = accmap_.find(s);
    if (i != accmap_.end())
      return i->second;

    bdd common = a_->all_acceptance_conditions();
    tgba_succ_iterator* it = a_->succ_iter(s);
    for (it->first(); !it->done() && common != bddfalse; it->next())
      common &= it->current_acceptance_conditions();
    delete it;

    // Populate cache
    accmap_[s->clone()] = common;
    return common;
  }

  bdd
  tgba_tba_proxy::union_acceptance_conditions_of_original_state(const state* s)
    const
  {
    // Lookup cache
    accmap_t::const_iterator i = accmapu_.find(s);
    if (i != accmapu_.end())
      return i->second;

    bdd common = bddfalse;
    tgba_succ_iterator* it = a_->succ_iter(s);
    for (it->first(); !it->done(); it->next())
      common |= it->current_acceptance_conditions();
    delete it;

    // Populate cache
    accmapu_[s->clone()] = common;
    return common;
  }

  bdd_dict*
  tgba_tba_proxy::get_dict() const
  {
    return a_->get_dict();
  }

  std::string
  tgba_tba_proxy::format_state(const state* state) const
  {
    const state_tba_proxy* s = down_cast<const state_tba_proxy*>(state);
    assert(s);
    std::string a = bdd_format_accset(get_dict(), s->acceptance_cond());
    if (a != "")
      a = " " + a;
    return a_->format_state(s->real_state()) + a;
  }

  state*
  tgba_tba_proxy::project_state(const state* s, const tgba* t) const
  {
    const state_tba_proxy* s2 = down_cast<const state_tba_proxy*>(s);
    assert(s2);
    if (t == this)
      return s2->clone();
    return a_->project_state(s2->real_state(), t);
  }


  bdd
  tgba_tba_proxy::all_acceptance_conditions() const
  {
    return the_acceptance_cond_;
  }

  bdd
  tgba_tba_proxy::neg_acceptance_conditions() const
  {
    return !the_acceptance_cond_;
  }

  bdd
  tgba_tba_proxy::compute_support_conditions(const state* state) const
  {
    const state_tba_proxy* s =
      down_cast<const state_tba_proxy*>(state);
    assert(s);
    return a_->support_conditions(s->real_state());
  }

  bdd
  tgba_tba_proxy::compute_support_variables(const state* state) const
  {
    const state_tba_proxy* s =
      down_cast<const state_tba_proxy*>(state);
    assert(s);
    return a_->support_variables(s->real_state());
  }

  ////////////////////////////////////////////////////////////////////////
  // tgba_sba_proxy

  tgba_sba_proxy::tgba_sba_proxy(const tgba* a)
    : tgba_tba_proxy(a)
  {
    if (a->number_of_acceptance_conditions() > 0)
      {
	cycle_start_ = acc_cycle_.insert(acc_cycle_.end(), bddtrue);

	bdd all = a->all_acceptance_conditions();

	state* init = a->get_init_state();
	tgba_succ_iterator* it = a->succ_iter(init);
	for (it->first(); !it->done(); it->next())
	  {
	    // Look only for transitions that are accepting.
	    if (all != it->current_acceptance_conditions())
	      continue;
	    // Look only for self-loops.
	    state* dest = it->current_state();
	    if (dest->compare(init) == 0)
	      {
		// The initial state has an accepting self-loop.
		// In that case it is better to start the accepting
		// cycle on a "acceptance" state.  This will avoid
		// duplication of the initial state.
		// The cycle_start_ points to the right starting
		// point already, so just return.
		dest->destroy();
		delete it;
		init->destroy();
		return;
	      }
	    dest->destroy();
	  }
	delete it;
	init->destroy();
      }

    // If we arrive here either because the number of acceptance
    // condition is 0, or because the initial state has no accepting
    // self-loop, start the acceptance cycle on the first condition
    // (that is a non-accepting state if the number of conditions is
    // not 0).
    cycle_start_ = acc_cycle_.begin();
  }

  state*
  tgba_sba_proxy::get_init_state() const
  {
    return new state_tba_proxy(a_->get_init_state(), cycle_start_);
  }

  bool
  tgba_sba_proxy::state_is_accepting(const state* state) const
  {
    const state_tba_proxy* s =
      down_cast<const state_tba_proxy*>(state);
    assert(s);
    return bddtrue == s->acceptance_cond();
  }

}
