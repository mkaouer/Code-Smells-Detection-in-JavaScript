// -*- coding: utf-8 -*-
// Copyright (C) 2012 Laboratoire de Recherche et
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

#include <queue>
#include <map>
#include <utility>
#include "tgba/tgbaexplicit.hh"
#include "simulation.hh"
#include "misc/acccompl.hh"
#include "misc/minato.hh"
#include "misc/unique_ptr.hh"
#include "tgba/bddprint.hh"
#include "tgbaalgos/reachiter.hh"
#include "tgbaalgos/sccfilter.hh"

// The way we developed this algorithm is the following: We take an
// automaton, and reverse all these acceptance conditions.  We reverse
// them to go make the meaning of the signature easier. We are using
// bdd, and we want to let it make all the simplification. Because of
// the format of the acceptance condition, it doesn't allow easy
// simplification. Instead of encoding them as: "a!b!c + !ab!c", we
// use them as: "ab". We complement them because we want a
// simplification if the condition of the transition A implies the
// transition of B, and if the acceptance condition of A is included
// in the acceptance condition of B. To let the bdd makes the job, we
// revert them.

// Then, to check if a transition i-dominates another, we'll use the bdd:
// "sig(transA) = cond(trans) & acc(trans) & implied(class(trans->state))"
// Idem for sig(transB). The 'implied'
// (represented by a hash table 'relation_' in the implementation) is
// a conjunction of all the class dominated by the class of the
// destination. This is how the relation is included in the
// signature. It makes the simplifications alone, and the work is
// done.  The algorithm is cut into several step:
//
// 1. Run through the tgba and switch the acceptance condition to their
//    negation, and initializing relation_ by the 'init_ -> init_' where
//    init_ is the bdd which represents the class. This function is the
//    constructor of Simulation.
// 2. Enter in the loop (run).
//    - Rename the class.
//    - run through the automaton and computing the signature of each
//      state. This function is `update_sig'.
//    - Enter in a double loop to adapt the partial order, and set
//      'relation_' accordingly. This function is `update_po'.
// 3. Rename the class (to actualize the name in the previous_class and
//    in relation_).
// 4. Building an automaton with the result, with the condition:
// "a transition in the original automaton appears in the simulated one
// iff this transition is included in the set of i-maximal neighbour."
// This function is `build_output'.
// The automaton simulated is recomplemented to come back to its initial
// state when the object Simulation is destroyed.
//
// Obviously these functions are possibly cut into several little one.
// This is just the general development idea.

// How to use isop:
// I need all variable non_acceptance & non_class.
// bdd_support(sig(X)): All var
// bdd_support(sig(X)) - allacc - allclassvar


// We have had the Cosimulation by changing the acc_compl_automaton by
// adding a template parameter. If this parameter is set to true, we
// record the transition in the opposite direction (we just swap
// sources and destination). In the build result we are making the
// same thing to rebuild the automaton.
// In the signature,

namespace spot
{
  namespace
  {
    // Some useful typedef:

    // Used to get the signature of the state.
    typedef Sgi::hash_map<const state*, bdd,
                          state_ptr_hash,
                          state_ptr_equal> map_state_bdd;

    typedef Sgi::hash_map<const state*, unsigned,
                          state_ptr_hash,
                          state_ptr_equal> map_state_unsigned;

    // Get the list of state for each class.
    typedef std::map<bdd, std::list<const state*>,
                     bdd_less_than> map_bdd_lstate;

    struct automaton_size
    {
      automaton_size()
        : transitions(0),
          states(0)
      {
      }

      inline bool operator!= (const automaton_size& r)
      {
        return transitions != r.transitions || states != r.states;
      }

      int transitions;
      int states;
    };

    // This class takes an automaton and creates a copy with all
    // acceptance conditions complemented.
    template <bool Cosimulation>
    class acc_compl_automaton:
      public tgba_reachable_iterator_depth_first
    {
    public:
      acc_compl_automaton(const tgba* a)
      : tgba_reachable_iterator_depth_first(a),
	size(0),
	out_(new tgba_explicit_number(a->get_dict())),
	ea_(a),
	ac_(ea_->all_acceptance_conditions(),
	    ea_->neg_acceptance_conditions()),
	current_max(0)
      {
        init_ = ea_->get_init_state();
        out_->set_init_state(get_state(init_));
      }

      inline unsigned
      get_state(const state* s)
      {
	map_state_unsigned::const_iterator i = state2int.find(s);
        if (i == state2int.end())
	  {
	    i = state2int.insert(std::make_pair(s, ++current_max)).first;
	    previous_class_[out_->add_state(current_max)] = bddfalse;
	  }
        return i->second;
      }

      void process_link(const state* in_s,
                        int,
                        const state* out_s,
                        int,
                        const tgba_succ_iterator* si)
      {
        int src = get_state(in_s);
        int dst = get_state(out_s);

        // In the case of the cosimulation, we want to have all the
        // ingoing transition, and to keep the rest of the code
        // similar, we just create equivalent transition in the other
        // direction. Since we do not have to run through the
        // automaton to get the signature, this is correct.
        if (Cosimulation)
          std::swap(src, dst);

        bdd acc = ac_.complement(si->current_acceptance_conditions());

        tgba_explicit_number::transition* t
          = out_->create_transition(src, dst);
        out_->add_acceptance_conditions(t, acc);
        out_->add_conditions(t, si->current_condition());
      }

      void process_state(const state*, int, tgba_succ_iterator*)
      {
        ++size;
      }

      ~acc_compl_automaton()
      {
        init_->destroy();
      }

    public:
      size_t size;
      tgba_explicit_number* out_;
      map_state_bdd previous_class_;

    private:
      const tgba* ea_;
      acc_compl ac_;
      map_state_unsigned state2int;
      unsigned current_max;
      state* init_;
    };

    template <bool Cosimulation>
    class direct_simulation
    {
      // Shortcut used in update_po and go_to_next_it.
      typedef std::map<bdd, bdd, bdd_less_than> map_bdd_bdd;
    public:
      direct_simulation(const tgba* t)
      : a_(0),
	po_size_(0),
	all_class_var_(bddtrue)
      {
	acc_compl_automaton<Cosimulation>
	  acc_compl(t);

	// We'll start our work by replacing all the acceptance
	// conditions by their complement.
	acc_compl.run();

	a_ = acc_compl.out_;

        initial_state = a_->get_init_state();

	// We use the previous run to know the size of the
	// automaton, and to class all the reachable states in the
	// map previous_class_.
	size_a_ = acc_compl.size;

	// Now, we have to get the bdd which will represent the
	// class. We register one bdd by state, because in the worst
	// case, |Class| == |State|.
	unsigned set_num = a_->get_dict()
	  ->register_anonymous_variables(size_a_ + 1, a_);

        bdd_initial = bdd_ithvar(set_num++);
	bdd init = bdd_ithvar(set_num++);

	used_var_.push_back(init);

	// We fetch the result the run of acc_compl_automaton which
	// has recorded all the state in a hash table, and we set all
	// to init.
	for (map_state_bdd::iterator it
	       = acc_compl.previous_class_.begin();
	     it != acc_compl.previous_class_.end();
	     ++it)
          {
            previous_class_[it->first] = init;
          }

	// Put all the anonymous variable in a queue, and record all
	// of these in a variable all_class_var_ which will be used
	// to understand the destination part in the signature when
	// building the resulting automaton.
	all_class_var_ = init;
	for (unsigned i = set_num; i < set_num + size_a_ - 1; ++i)
          {
            free_var_.push(i);
            all_class_var_ &= bdd_ithvar(i);
          }

	relation_[init] = init;
      }


      // Reverse all the acceptance condition at the destruction of
      // this object, because it occurs after the return of the
      // function simulation.
      ~direct_simulation()
      {
	delete a_;
      }


      // We update the name of the class.
      void update_previous_class()
      {
	std::list<bdd>::iterator it_bdd = used_var_.begin();

	// We run through the map bdd/list<state>, and we update
	// the previous_class_ with the new data.
	it_bdd = used_var_.begin();
	for (map_bdd_lstate::iterator it = bdd_lstate_.begin();
	     it != bdd_lstate_.end();
	     ++it)
          {
            for (std::list<const state*>::iterator it_s = it->second.begin();
                 it_s != it->second.end();
                 ++it_s)
	      {
		// If the signature of a state is bddfalse (no
		// transitions) the class of this state is bddfalse
		// instead of an anonymous variable. It allows
		// simplifications in the signature by removing a
		// transition which has as a destination a state with
		// no outgoing transition.
		if (it->first == bddfalse)
                  previous_class_[*it_s] = bddfalse;
		else
		  previous_class_[*it_s] = *it_bdd;
	      }
            if (it->first != bddfalse)
              ++it_bdd;
          }
      }

      // The core loop of the algorithm.
      tgba* run()
      {
	unsigned int nb_partition_before = 0;
	unsigned int nb_po_before = po_size_ - 1;
	while (nb_partition_before != bdd_lstate_.size()
	       || nb_po_before != po_size_)
          {
            update_previous_class();
            nb_partition_before = bdd_lstate_.size();
            bdd_lstate_.clear();
            nb_po_before = po_size_;
            po_size_ = 0;
            update_sig();
            go_to_next_it();
          }

	update_previous_class();
	return build_result();
      }

      // Take a state and compute its signature.
      bdd compute_sig(const state* src)
      {
        tgba_succ_iterator* sit = a_->succ_iter(src);
        bdd res = bddfalse;

        for (sit->first(); !sit->done(); sit->next())
          {
            const state* dst = sit->current_state();
            bdd acc = sit->current_acceptance_conditions();

            // to_add is a conjunction of the acceptance condition,
            // the label of the transition and the class of the
            // destination and all the class it implies.
            bdd to_add = acc & sit->current_condition()
              & relation_[previous_class_[dst]];

            res |= to_add;
            dst->destroy();
          }

        // When we Cosimulate, we add a special flag to differentiate
        // initial state.
        if (Cosimulation && initial_state == src)
          res |= bdd_initial;

        delete sit;
        return res;
      }


      void update_sig()
      {
	// At this time, current_class_ must be empty.  It implies
	// that the "previous_class_ = current_class_" must be
	// done before.
	assert(current_class_.empty());

	// Here we suppose that previous_class_ always contains
	// all the reachable states of this automaton. We do not
	// have to make (again) a traversal. We just have to run
	// through this map.
	for (map_state_bdd::iterator it = previous_class_.begin();
	     it != previous_class_.end();
	     ++it)
          {
            const state* src = it->first;

            bdd_lstate_[compute_sig(src)].push_back(src);
          }
      }


      // This method rename the color set, update the partial order.
      void go_to_next_it()
      {
	int nb_new_color = bdd_lstate_.size() - used_var_.size();

	for (int i = 0; i < nb_new_color; ++i)
          {
            assert(!free_var_.empty());
            used_var_.push_back(bdd_ithvar(free_var_.front()));
            free_var_.pop();
          }

	for (int i = 0; i > nb_new_color; --i)
          {
            assert(!used_var_.empty());
            free_var_.push(bdd_var(used_var_.front()));
            used_var_.pop_front();
          }


	assert((bdd_lstate_.size() == used_var_.size())
               || (bdd_lstate_.find(bddfalse) != bdd_lstate_.end()
                   && bdd_lstate_.size() == used_var_.size() + 1));

	// Now we make a temporary hash_table which links the tuple
	// "C^(i-1), N^(i-1)" to the new class coloring.  If we
	// rename the class before updating the partial order, we
	// loose the information, and if we make it after, I can't
	// figure out how to apply this renaming on rel_.
	// It adds a data structure but it solves our problem.
	map_bdd_bdd now_to_next;

	std::list<bdd>::iterator it_bdd = used_var_.begin();

	for (map_bdd_lstate::iterator it = bdd_lstate_.begin();
	     it != bdd_lstate_.end();
	     ++it)
          {
            // If the signature of a state is bddfalse (which is
            // roughly equivalent to no transition) the class of
            // this state is bddfalse instead of an anonymous
            // variable. It allows simplifications in the signature
            // by removing a transition which has as a destination a
            // state with no outgoing transition.
            if (it->first == bddfalse)
              {
                now_to_next[it->first] = bddfalse;
                free_var_.push(bdd_var(*it_bdd));
                it_bdd = used_var_.erase(it_bdd);
              }
            else
              {
                now_to_next[it->first] = *it_bdd;
                ++it_bdd;
              }
          }

	update_po(now_to_next);
      }

      // This function computes the new po with previous_class_
      // and the argument. `now_to_next' contains the relation
      // between the signature and the future name of the class.
      void update_po(const map_bdd_bdd& now_to_next)
      {
	// This loop follows the pattern given by the paper.
	// foreach class do
	// |  foreach class do
	// |  | update po if needed
	// |  od
	// od

	for (map_bdd_bdd::const_iterator it1 = now_to_next.begin();
	     it1 != now_to_next.end();
	     ++it1)
          {
            bdd accu = it1->second;
            for (map_bdd_bdd::const_iterator it2 = now_to_next.begin();
                 it2 != now_to_next.end();
                 ++it2)
              {
                // Skip the case managed by the initialization of accu.
                if (it1 == it2)
                  continue;

                if (bdd_implies(it1->first, it2->first))
                  {
                    accu &= it2->second;
                    ++po_size_;
                  }
              }
            relation_[it1->second] = accu;
          }
      }

      automaton_size get_stat() const
      {
        assert(stat.states != 0);

        return stat;
      }


      // Build the minimal resulting automaton.
      tgba* build_result()
      {
	// Now we need to create a state per partition. But the
	// problem is that we don't know exactly the class. We know
	// that it is a combination of the acceptance condition
	// contained in all_class_var_. So we need to make a little
	// workaround. We will create a map which will associate bdd
	// and unsigned.
	std::map<bdd, unsigned, bdd_less_than> bdd2state;
	unsigned int current_max = 0;

	bdd all_acceptance_conditions
	  = a_->all_acceptance_conditions();

	// We have all the a_'s acceptances conditions
	// complemented.  So we need to complement it when adding a
	// transition.  We *must* keep the complemented because it
	// is easy to know if an acceptance condition is maximal or
	// not.
	acc_compl reverser(all_acceptance_conditions,
			   a_->neg_acceptance_conditions());

	tgba_explicit_number* res
	  = new tgba_explicit_number(a_->get_dict());
	res->set_acceptance_conditions
	  (all_acceptance_conditions);

	bdd sup_all_acc = bdd_support(all_acceptance_conditions);
	// Non atomic propositions variables (= acc and class)
	bdd nonapvars = sup_all_acc & bdd_support(all_class_var_);

	// Create one state per partition.
	for (map_bdd_lstate::iterator it = bdd_lstate_.begin();
	     it != bdd_lstate_.end(); ++it)
          {
            res->add_state(++current_max);
            bdd part = previous_class_[*it->second.begin()];

            // The difference between the two next lines is:
            // the first says "if you see A", the second "if you
            // see A and all the class implied by it".
            bdd2state[part] = current_max;
            bdd2state[relation_[part]] = current_max;
          }

        stat.states = bdd_lstate_.size();
        stat.transitions = 0;

	// For each partition, we will create
	// all the transitions between the states.
	for (map_bdd_lstate::iterator it = bdd_lstate_.begin();
	     it != bdd_lstate_.end();
	     ++it)
          {
            // Get the signature.
            bdd sig = compute_sig(*(it->second.begin()));

            if (Cosimulation)
              sig = bdd_compose(sig, bddfalse, bdd_var(bdd_initial));

            // Get all the variable in the signature.
            bdd sup_sig = bdd_support(sig);

            // Get the variable in the signature which represents the
            // conditions.
            bdd sup_all_atomic_prop = bdd_exist(sup_sig, nonapvars);

            // Get the part of the signature composed only with the atomic
            // proposition.
            bdd all_atomic_prop = bdd_exist(sig, nonapvars);

	    // First loop over all possible valuations atomic properties.
            while (all_atomic_prop != bddfalse)
	      {
		bdd one = bdd_satoneset(all_atomic_prop,
					sup_all_atomic_prop,
					bddtrue);
		all_atomic_prop -= one;

		// For each possible valuation, iterator over all possible
		// destination classes.   We use minato_isop here, because
		// if the same valuation of atomic properties can go
		// to two different classes C1 and C2, iterating on
		// C1 + C2 with the above bdd_satoneset loop will see
		// C1 then (!C1)C2, instead of C1 then C2.
		// With minatop_isop, we ensure that the no negative
		// class variable will be seen (likewise for promises).
		minato_isop isop(sig & one);

		bdd cond_acc_dest;
		while ((cond_acc_dest = isop.next()) != bddfalse)
		  {
                    ++stat.transitions;

		    // Take the transition, and keep only the variable which
		    // are used to represent the class.
		    bdd dest = bdd_existcomp(cond_acc_dest,
					     all_class_var_);

		    // Keep only ones who are acceptance condition.
		    bdd acc = bdd_existcomp(cond_acc_dest, sup_all_acc);

		    // Keep the other !
		    bdd cond = bdd_existcomp(cond_acc_dest,
					     sup_all_atomic_prop);

		    // Because we have complemented all the acceptance
		    // condition on the input automaton, we must re
		    // invert them to create a new transition.
		    acc = reverser.reverse_complement(acc);

		    // Take the id of the source and destination.  To
		    // know the source, we must take a random state in
		    // the list which is in the class we currently
		    // work on.
		    int src = bdd2state[previous_class_[*it->second.begin()]];
		    int dst = bdd2state[dest];

                    if (Cosimulation)
                      std::swap(src, dst);

		    // src or dst == 0 means "dest" or "prev..." isn't
		    // in the map.  so it is a bug.
		    assert(src != 0);
		    assert(dst != 0);

		    // Create the transition, add the condition and the
		    // acceptance condition.
		    tgba_explicit_number::transition* t
		      = res->create_transition(src , dst);
		    res->add_conditions(t, cond);
		    res->add_acceptance_conditions(t, acc);
		  }
	      }
          }

	res->set_init_state(bdd2state[previous_class_
				      [a_->get_init_state()]]);

	res->merge_transitions();

	return res;
      }


      // Debug:
      // In a first time, print the signature, and the print a list
      // of each state in this partition.
      // In a second time, print foreach state, who is where,
      // where is the new class name.
      void print_partition()
      {
	for (map_bdd_lstate::iterator it = bdd_lstate_.begin();
	     it != bdd_lstate_.end();
	     ++it)
          {
            std::cerr << "partition: "
                      << bdd_format_set(a_->get_dict(), it->first) << std::endl;

            for (std::list<const state*>::iterator it_s = it->second.begin();
                 it_s != it->second.end();
                 ++it_s)
	      {
		std::cerr << "  - "
			  << a_->format_state(*it_s) << std::endl;
	      }
          }

	std::cerr << "\nPrevious iteration\n" << std::endl;

	for (map_state_bdd::const_iterator it = previous_class_.begin();
	     it != previous_class_.end();
	     ++it)
          {
            std::cerr << a_->format_state(it->first)
                      << " was in "
                      << bdd_format_set(a_->get_dict(), it->second)
                      << std::endl;
          }
      }

    private:
      // The automaton which is simulated.
      tgba_explicit_number* a_;

      // Relation is aimed to represent the same thing than
      // rel_. The difference is in the way it does.
      // If A => A /\ A => B, rel will be (!A U B), but relation_
      // will have A /\ B at the key A. This trick is due to a problem
      // with the computation of the resulting automaton with the signature.
      // rel_ will pollute the meaning of the signature.
      map_bdd_bdd relation_;

      // Represent the class of each state at the previous iteration.
      map_state_bdd previous_class_;

      // The class at the current iteration.
      map_state_bdd current_class_;

      // The list of state for each class at the current_iteration.
      // Computed in `update_sig'.
      map_bdd_lstate bdd_lstate_;

      // The queue of free bdd. They will be used as the identifier
      // for the class.
      std::queue<int> free_var_;

      // The list of used bdd. They are in used as identifier for class.
      std::list<bdd> used_var_;

      // Size of the automaton.
      unsigned int size_a_;

      // Used to know when there is no evolution in the po. Updated
      // in the `update_po' method.
      unsigned int po_size_;

      // All the class variable:
      bdd all_class_var_;

      // The flag to say if the outgoing state is initial or not
      bdd bdd_initial;

      // Initial state of the automaton we are working on
      state* initial_state;

      automaton_size stat;
    };

  } // End namespace anonymous.

  tgba*
  simulation(const tgba* t)
  {
    direct_simulation<false> simul(t);

    return simul.run();
  }

  tgba*
  cosimulation(const tgba* t)
  {
    direct_simulation<true> simul(t);

    return simul.run();
  }

  tgba*
  iterated_simulations(const tgba* t)
  {
    tgba* res = const_cast<tgba*> (t);
    automaton_size prev;
    automaton_size next;

    do
      {
        prev = next;
        direct_simulation<false> simul(res);

        unique_ptr<tgba> after_simulation(simul.run());

        if (res != t)
          delete res;

        direct_simulation<true> cosimul(after_simulation);

        unique_ptr<tgba> after_cosimulation(cosimul.run());

        next = cosimul.get_stat();

        res = scc_filter(after_cosimulation, false);
      }
    while (prev != next);

    return res;
  }


} // End namespace spot.
