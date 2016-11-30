// -*- coding: utf-8 -*-
// Copyright (C) 2012, 2013 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
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
#include <cmath>
#include <limits>
#include "tgba/tgbaexplicit.hh"
#include "simulation.hh"
#include "priv/acccompl.hh"
#include "misc/minato.hh"
#include "misc/unique_ptr.hh"
#include "tgba/bddprint.hh"
#include "tgbaalgos/reachiter.hh"
#include "tgbaalgos/sccfilter.hh"
#include "tgbaalgos/scc.hh"
#include "tgbaalgos/dupexp.hh"
#include "tgbaalgos/dotty.hh"

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

// TODO LIST: Play on the order of the selection in the
// dont_care_simulation. The good place to work is in add_to_map_imply.


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

    typedef std::map<const state*, const state*,
                     state_ptr_less_than> map_state_state;


    // Get the list of state for each class.
    typedef std::map<bdd, std::list<const state*>,
                     bdd_less_than> map_bdd_lstate;

    typedef std::map<bdd, const state*,
                     bdd_less_than> map_bdd_state;

    // Our constraint: (state_src, state_dst) = to_add.
    // We define the couple of state as the key of the constraint.
    typedef std::pair<const state*, const state*> constraint_key;

    // But we need a comparator for that key.
    struct constraint_key_comparator
    {
      bool operator()(const constraint_key& l,
                      const constraint_key& r) const
      {
        if (l.first->compare(r.first) < 0)
          return true;
        else
          if (l.first->compare(r.first) > 0)
            return false;

        if (l.second->compare(r.second) < 0)
          return true;
        else
          if (l.second->compare(r.second) > 0)
            return false;

        return false;
      }
    };

    // The full definition of the constraint.
    typedef std::map<constraint_key, bdd,
                     constraint_key_comparator> map_constraint;

    typedef std::pair<constraint_key, bdd> constraint;

    // Helper to create the map of constraints to give to the
    // simulation.
    void add_to_map(const std::list<constraint>& list,
                    map_constraint& feed_me)
    {
      for (std::list<constraint>::const_iterator it = list.begin();
           it != list.end();
           ++it)
        {
          if (feed_me.find(it->first) == feed_me.end())
            feed_me[it->first] = it->second;
        }
    }


    // This class helps to compare two automata in term of
    // size.
    struct automaton_size
    {
      automaton_size()
        : transitions(0),
          states(0)
      {
      }

      inline bool operator!=(const automaton_size& r)
      {
        return transitions != r.transitions || states != r.states;
      }

      inline bool operator<(const automaton_size& r)
      {
        if (states < r.states)
          return true;
        if (states > r.states)
          return false;

        if (transitions < r.transitions)
          return true;
        if (transitions > r.transitions)
          return false;

        return false;
      }

      inline bool operator>(const automaton_size& r)
      {
        if (states < r.states)
          return false;
        if (states > r.states)
          return true;

        if (transitions < r.transitions)
          return false;
        if (transitions > r.transitions)
          return true;

        return false;
      }

      int transitions;
      int states;
    };

    // This class takes an automaton, and return a (maybe new)
    // automaton. If Cosimulation is equal to true, we create a new
    // automaton. Otherwise, we create a new one.  The returned
    // automaton is similar to the old one, except that the acceptance
    // condition on the transitions are complemented.
    // There is a specialization below.
    template <bool Cosimulation, bool Sba>
    class acc_compl_automaton:
      public tgba_reachable_iterator_depth_first
    {
    public:
       acc_compl_automaton(const tgba* a)
       : tgba_reachable_iterator_depth_first(a),
 	size(0),
 	ea_(down_cast<tgba_explicit_number*>(const_cast<tgba*>(a))),
 	ac_(ea_->all_acceptance_conditions(),
 	    ea_->neg_acceptance_conditions())
       {
         assert(ea_);
         out_ = ea_;
       }

       void process_link(const state*, int,
                         const state*, int,
                         const tgba_succ_iterator* si)
       {
         bdd acc = ac_.complement(si->current_acceptance_conditions());

         typename tgba_explicit_number::transition* t =
           ea_->get_transition(si);

         t->acceptance_conditions = acc;
       }

       void process_state(const state* s, int, tgba_succ_iterator*)
       {
         ++size;
         previous_class_[s] = bddfalse;
         old_name_[s] = s;
         order_.push_back(s);
       }

       ~acc_compl_automaton()
       {
       }

    public:
      size_t size;
      tgba_explicit_number* out_;
      map_state_bdd previous_class_;
      std::list<const state*> order_;
      map_state_state old_name_;

    private:
      tgba_explicit_number* ea_;
      acc_compl ac_;
    };

    // The specialization for Cosimulation equals to true: We copy the
    // automaton and transpose it at the same time.
    template <bool Sba>
    class acc_compl_automaton<true, Sba>:
      public tgba_reachable_iterator_depth_first
    {
    public:
      acc_compl_automaton(const tgba* a)
      : tgba_reachable_iterator_depth_first(a),
	size(0),
        out_(new tgba_explicit_number(a->get_dict())),
	ac_(a->all_acceptance_conditions(),
	    a->neg_acceptance_conditions()),
	current_max(0)
      {
	a->get_dict()->register_all_variables_of(a, out_);
	out_->set_acceptance_conditions(a->all_acceptance_conditions());

        const state* init_ = a->get_init_state();
        out_->set_init_state(get_state(init_));
	init_->destroy();
      }

      inline unsigned
      get_state(const state* s)
      {
	map_state_unsigned::const_iterator i = state2int.find(s);
        if (i == state2int.end())
	  {
	    i = state2int.insert(std::make_pair(s, ++current_max)).first;
            state* in_new_aut = out_->add_state(current_max);
	    previous_class_[in_new_aut] = bddfalse;
            old_name_[in_new_aut] = s;
            order_.push_back(in_new_aut);
	  }
        return i->second;
      }

      void process_link(const state* in_s,
                        int,
                        const state* out_s,
                        int,
                        const tgba_succ_iterator* si)
      {
        unsigned src = get_state(in_s);
        unsigned dst = get_state(out_s);

	// Note the order of src and dst: the transition is reversed.
        tgba_explicit_number::transition* t
           = out_->create_transition(dst, src);

        t->condition = si->current_condition();
	if (!Sba)
	  {
	    bdd acc = ac_.complement(si->current_acceptance_conditions());
	    t->acceptance_conditions = acc;
	  }
	else
	  {
	    // If the acceptance is interpreted as state-based, to
	    // apply the reverse simulation on a SBA, we should pull
	    // the acceptance of the destination state on its incoming
	    // arcs (which now become outgoing args after
	    // transposition).
	    tgba_succ_iterator* it = out_->succ_iter(out_s);
	    it->first();
	    if (!it->done())
	      {
		bdd acc = ac_.complement(it->current_acceptance_conditions());
		t->acceptance_conditions = acc;
	      }
	    delete it;
	  }
      }

      void process_state(const state*, int, tgba_succ_iterator*)
      {
        ++size;
      }

      ~acc_compl_automaton()
      {
      }

    public:
      size_t size;
      tgba_explicit_number* out_;
      map_state_bdd previous_class_;
      std::list<const state*> order_;
      map_state_state old_name_;

    private:
      acc_compl ac_;
      map_state_unsigned state2int;
      unsigned current_max;
    };

    // The direct_simulation. If Cosimulation is true, we are doing a
    // cosimulation.
    template <bool Cosimulation, bool Sba>
    class direct_simulation
    {
    protected:
      // Shortcut used in update_po and go_to_next_it.
      typedef std::map<bdd, bdd, bdd_less_than> map_bdd_bdd;
    public:
      direct_simulation(const tgba* t, const map_constraint* map_cst = 0)
        : a_(0),
          po_size_(0),
          all_class_var_(bddtrue),
          map_cst_(map_cst),
          original_(t),
          dont_delete_old_(false)
      {
        // We need to do a dupexp for being able to run scc_map later.
        // new_original_ is the map that contains the relation between
        // the names (addresses) of the states in the automaton
        // returned by dupexp, and in automaton given in argument to
        // the constructor.
        a_ = tgba_dupexp_dfs(t, new_original_);
        scc_map_ = new scc_map(a_);
        scc_map_->build_map();
        old_a_ = a_;

	acc_compl_automaton<Cosimulation, Sba> acc_compl(a_);

	// We'll start our work by replacing all the acceptance
	// conditions by their complement.
	acc_compl.run();

        // Contains the relation between the names of the states in
        // the automaton returned by the complementation and the one
        // passed to the constructor of acc_compl.
	std::swap(old_name_, acc_compl.old_name_);

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
	  ->register_anonymous_variables(size_a_ + 1, this);

        all_acceptance_conditions_ = a_->all_acceptance_conditions();
        all_proms_ = bdd_support(all_acceptance_conditions_);

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

	std::swap(order_, acc_compl.order_);
      }


      // Reverse all the acceptance condition at the destruction of
      // this object, because it occurs after the return of the
      // function simulation.
      virtual ~direct_simulation()
      {
	a_->get_dict()->unregister_all_my_variables(this);
        delete scc_map_;

        if (!dont_delete_old_)
	  delete old_a_;
        // a_ is a new automaton only if we are doing a cosimulation.
        if (Cosimulation)
          delete a_;
      }

      // Update the name of the classes.
      void update_previous_class()
      {
	std::list<bdd>::iterator it_bdd = used_var_.begin();

	// We run through the map bdd/list<state>, and we update
	// the previous_class_ with the new data.
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
              ++it_bdd;
          }
      }

      void main_loop()
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
      }

      // The core loop of the algorithm.
      tgba* run()
      {
        main_loop();
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
            bdd acc = bddtrue;

	    map_constraint::const_iterator it;
	    // We are using new_original_[old_name_[...]] because
	    // we have the constraints in the original automaton
	    // which has been duplicated twice to get the current
	    // automaton.
	    if (map_cst_
		&& ((it = map_cst_
		     ->find(std::make_pair(new_original_[old_name_[src]],
					   new_original_[old_name_[dst]])))
		    != map_cst_->end()))
	      {
		acc = it->second;
	      }
	    else
	      {
		acc = sit->current_acceptance_conditions();
	      }

	    // to_add is a conjunction of the acceptance condition,
	    // the label of the transition and the class of the
	    // destination and all the class it implies.
	    bdd to_add = acc & sit->current_condition()
	      & relation_[previous_class_[dst]];

	    res |= to_add;
	    dst->destroy();
	  }

        // When we Cosimulate, we add a special flag to differentiate
        // the initial state from the other.
        if (Cosimulation && initial_state == src)
          res |= bdd_initial;

        delete sit;
        return res;
      }


      void update_sig()
      {
	// Here we suppose that previous_class_ always contains
	// all the reachable states of this automaton. We do not
	// have to make (again) a traversal. We just have to run
	// through this map.
        for (std::list<const state*>::const_iterator it = order_.begin();
             it != order_.end();
             ++it)
          {
            const state* src = previous_class_.find(*it)->first;

            bdd_lstate_[compute_sig(src)].push_back(src);
          }
      }


      // This method rename the color set, update the partial order.
      void go_to_next_it()
      {
	int nb_new_color = bdd_lstate_.size() - used_var_.size();


        // If we have created more partitions, we need to use more
        // variables.
	for (int i = 0; i < nb_new_color; ++i)
          {
            assert(!free_var_.empty());
            used_var_.push_back(bdd_ithvar(free_var_.front()));
            free_var_.pop();
          }


        // If we have reduced the number of partition, we 'free' them
        // in the free_var_ list.
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
                now_to_next[it->first] = bddfalse;
            else
              now_to_next[it->first] = *it_bdd;

            ++it_bdd;
          }

	update_po(now_to_next, relation_);
      }

      // This function computes the new po with previous_class_ and
      // the argument. `now_to_next' contains the relation between the
      // signature and the future name of the class.  We need a
      // template parameter because we use this function with a
      // map_bdd_bdd, but later, we need a list_bdd_bdd. So to
      // factorize some code we use a template.
      template <typename container_bdd_bdd>
      void update_po(const container_bdd_bdd& now_to_next,
                     map_bdd_bdd& relation)
      {
	// This loop follows the pattern given by the paper.
	// foreach class do
	// |  foreach class do
	// |  | update po if needed
	// |  od
	// od

	for (typename container_bdd_bdd::const_iterator it1
               = now_to_next.begin();
	     it1 != now_to_next.end();
	     ++it1)
          {
            bdd accu = it1->second;
            for (typename container_bdd_bdd::const_iterator it2
                   = now_to_next.begin();
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
            relation[it1->second] = accu;
          }
      }

      automaton_size get_stat() const
      {
        assert(stat.states != 0);

        return stat;
      }

      bool result_is_deterministic() const
      {
        assert(stat.states != 0);

        return res_is_deterministic;
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

	// We have all the a_'s acceptances conditions
	// complemented.  So we need to complement it when adding a
	// transition.  We *must* keep the complemented because it
	// is easy to know if an acceptance condition is maximal or
	// not.
	acc_compl reverser(all_acceptance_conditions_,
			   a_->neg_acceptance_conditions());

	bdd_dict* d = a_->get_dict();
	tgba_explicit_number* res = new tgba_explicit_number(d);
	d->register_all_variables_of(a_, res);
	res->set_acceptance_conditions(all_acceptance_conditions_);

	bdd sup_all_acc = bdd_support(all_acceptance_conditions_);
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
            // see A and all the classes implied by it".
            bdd2state[part] = current_max;
            bdd2state[relation_[part]] = current_max;
          }

	// Acceptance of states.  Only used if Sba && Cosimulation.
	std::vector<bdd> accst;
	if (Sba && Cosimulation)
	  accst.resize(current_max + 1, bddfalse);

        stat.states = bdd_lstate_.size();
        stat.transitions = 0;

        unsigned nb_satoneset = 0;
        unsigned nb_minato = 0;

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

		// For each possible valuation, iterate over all possible
		// destination classes.   We use minato_isop here, because
		// if the same valuation of atomic properties can go
		// to two different classes C1 and C2, iterating on
		// C1 + C2 with the above bdd_satoneset loop will see
		// C1 then (!C1)C2, instead of C1 then C2.
		// With minatop_isop, we ensure that the no negative
		// class variable will be seen (likewise for promises).
		minato_isop isop(sig & one);

                ++nb_satoneset;

		bdd cond_acc_dest;
		while ((cond_acc_dest = isop.next()) != bddfalse)
		  {
                    ++stat.transitions;

                    ++nb_minato;

		    // Take the transition, and keep only the variable which
		    // are used to represent the class.
		    bdd dest = bdd_existcomp(cond_acc_dest,
					     all_class_var_);

		    // Keep only ones who are acceptance condition.
		    bdd acc = bdd_existcomp(cond_acc_dest, sup_all_acc);

		    // Keep the other!
		    bdd cond = bdd_existcomp(cond_acc_dest,
					     sup_all_atomic_prop);

		    // Because we have complemented all the acceptance
		    // conditions on the input automaton, we must
		    // revert them to create a new transition.
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
		      = res->create_transition(src, dst);
		    t->condition = cond;
		    if (Sba && Cosimulation)
		      accst[dst] = acc;
		    else
		      t->acceptance_conditions = acc;
		  }
	      }
          }

	res->set_init_state(bdd2state[previous_class_
				      [a_->get_init_state()]]);

	res->merge_transitions();

	// Mark all accepting state in a second pass, when
	// dealing with SBA in cosimulation.
	if (Sba && Cosimulation)
	  for (unsigned snum = current_max; snum > 0; --snum)
	    {
	      const state* s = res->get_state(snum);
	      tgba_succ_iterator* it = res->succ_iter(s);
	      bdd acc = accst[snum];
	      for (it->first(); !it->done(); it->next())
		{
		  tgba_explicit_number::transition* t =
		    res->get_transition(it);
		  t->acceptance_conditions = acc;
		}
	      delete it;
	    }

        res_is_deterministic = nb_minato == nb_satoneset;

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
                      << bdd_format_isop(a_->get_dict(), it->first)
                      << std::endl;

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

    protected:
      // The automaton which is simulated.
      tgba_explicit_number* a_;
      tgba_explicit_number* old_a_;

      // Relation is aimed to represent the same thing than
      // rel_. The difference is in the way it does.
      // If A => A /\ A => B, rel will be (!A U B), but relation_
      // will have A /\ B at the key A. This trick is due to a problem
      // with the computation of the resulting automaton with the signature.
      // rel_ will pollute the meaning of the signature.
      map_bdd_bdd relation_;

      // Represent the class of each state at the previous iteration.
      map_state_bdd previous_class_;

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

      bdd all_proms_;

      automaton_size stat;

      // The order of the state.
      std::list<const state*> order_;
      scc_map* scc_map_;
      map_state_state old_name_;
      map_state_state new_original_;

      // This table link a state in the current automaton with a state
      // in the original one.
      map_state_state old_old_name_;

      const map_constraint* map_cst_;

      const tgba* original_;

      bdd all_acceptance_conditions_;

      // This variable is used when we return the copy, so we avoid
      // deleting what we return. It is better!
      bool dont_delete_old_;

      bool res_is_deterministic;
    };

    // For now, we don't try to handle cosimulation.
    class direct_simulation_dont_care: public direct_simulation<false, false>
    {
      typedef std::vector<std::list<constraint> > constraints;
      typedef std::map<bdd,                  // Source Class.
                       std::map<bdd,         // Destination (implied) Class.
                                std::list<constraint>, // Constraints list.
                                bdd_less_than>,
                       bdd_less_than> constraint_list;
      typedef std::list<std::pair<bdd, bdd> > list_bdd_bdd;


    public:
      direct_simulation_dont_care(const tgba* t)
      : direct_simulation<false, false>(t)
      {
        // This variable is used in the new signature.
        on_cycle_ =
          bdd_ithvar(a_->get_dict()->register_anonymous_variables(1, this));

        // This one is used for the iteration on all the
        // possibilities. Avoid computing two times "no constraints".
        empty_seen_ = false;


        // If this variable is set to true, we have a number limit of
        // simulation to run.
        has_limit_ = false;

	notap = (bdd_support(all_acceptance_conditions_)
                 & all_class_var_ & on_cycle_);
      }

      // This function computes the don't care signature of the state
      // src. This signature is similar to the classic one, excepts
      // that if the transition is on a SCC, we add a on_cycle_ on it,
      // otherwise we add !on_cycle_. This allows us to split the
      // signature later.
      bdd dont_care_compute_sig(const state* src)
      {
        tgba_succ_iterator* sit = a_->succ_iter(src);
        bdd res = bddfalse;

        unsigned scc = scc_map_->scc_of_state(old_name_[src]);
        bool sccacc = scc_map_->accepting(scc);

        for (sit->first(); !sit->done(); sit->next())
          {
            const state* dst = sit->current_state();
            bdd cl = previous_class_[dst];
            bdd acc;

            if (scc != scc_map_->scc_of_state(old_name_[dst]))
              acc = !on_cycle_;
            else if (sccacc)
              acc = on_cycle_ & sit->current_acceptance_conditions();
            else
              acc = on_cycle_ & all_proms_;

            bdd to_add = acc & sit->current_condition() & relation_[cl];
            res |= to_add;
          }

        delete sit;
        return res;
      }

      // We used to have
      //   sig(s1) = (f1 | g1)
      //   sig(s2) = (f2 | g2)
      // and we say that s2 simulates s1 if sig(s1)=>sig(s2).
      // This amount to testing whether (f1|g1)=>(f2|g2),
      // which is equivalent to testing both
      //    f1=>(f2|g2)  and g1=>(f2|g2)
      // separately.
      //
      // Now we have a slightly improved version of this rule.
      // g1 and g2 are not on cycle, so they can make as many
      // promises as we wish, if that helps.  Adding promises
      // to g2 will not help, but adding promises to g1 can.
      //
      // So we test whether
      //    f1=>(f2|g2)
      //    g1=>noprom(f2|g2)
      // Where noprom(f2|g2) removes all promises from f2|g2.
      // (g1 do not have promises, and neither do g2).

      bool could_imply_aux(bdd f1, bdd g1, bdd left_class,
			   bdd right, bdd right_class)
      {
        (void) left_class;
        (void) right_class;

        bdd f2g2 = bdd_exist(right, on_cycle_);
        bdd f2g2n = bdd_exist(f2g2, all_proms_);

	bdd both = left_class & right_class;
	int lc = bdd_var(left_class);
        f1 = bdd_compose(f1, both, lc);
        g1 = bdd_compose(g1, both, lc);
        f2g2 = bdd_compose(f2g2, both, lc);
        f2g2n = bdd_compose(f2g2n, both, lc);

        return bdd_implies(f1, f2g2) && bdd_implies(g1, f2g2n);
      }

      bool could_imply(bdd left, bdd left_class,
		       bdd right, bdd right_class)
      {
	bdd f1 = bdd_relprod(left, on_cycle_, on_cycle_);
	bdd g1 = bdd_relprod(left, !on_cycle_, on_cycle_);

        //bdd f1 = bdd_restrict(left, on_cycle_);
        //bdd g1 = bdd_restrict(left, !on_cycle_);
	return could_imply_aux(f1, g1, left_class,
			       right, right_class);
      }

      void dont_care_update_po(const list_bdd_bdd& now_to_next,
                               map_bdd_bdd& relation)
      {
        // This loop follows the pattern given by the paper.
        // foreach class do
        // |  foreach class do
        // |  | update po if needed
        // |  od
        // od

        for (list_bdd_bdd::const_iterator it1 = now_to_next.begin();
             it1 != now_to_next.end();
             ++it1)
          {
            bdd accu = it1->second;

	    bdd f1 = bdd_relprod(it1->first, on_cycle_, on_cycle_);
	    bdd g1 = bdd_relprod(it1->first, !on_cycle_, on_cycle_);

            // bdd f1 = bdd_restrict(it1->first_, on_cycle_);
            // bdd g1 = bdd_restrict(it1->first_, !on_cycle_);

            for (list_bdd_bdd::const_iterator it2 = now_to_next.begin();
                 it2 != now_to_next.end();
                 ++it2)
              {
                // Skip the case managed by the initialization of accu.
                if (it1 == it2)
                  continue;

		if (could_imply_aux(f1, g1, it1->second,
				    it2->first, it2->second))
                  {
                    accu &= it2->second;
                    ++po_size_;
                  }
              }
            relation[it1->second] = accu;
          }
      }

#define ISOP(bdd) #bdd" - " << bdd_format_isop(a_->get_dict(), bdd)

      inline bool is_out_scc(bdd b)
      {
	return bddfalse !=  bdd_relprod(b, !on_cycle_, on_cycle_);
        // return bddfalse != bdd_restrict(b, !on_cycle_);
      }

#define create_cstr(src, dst, constraint) \
      std::make_pair(std::make_pair(src, dst), constraint)

      // This method solves three kind of problems, where we have two
      // conjunctions of variable (that corresponds to a particular
      // transition), and where left could imply right.
      // Three cases:
      //   - αP₁ ⇒ xβP₁ where x is unknown.
      //   - xβP₁ ⇒ αP₁ where x is unknown.
      //   - xαP₁ ⇒ yβP₁ where x, y are unknown.
      void create_simple_constraint(bdd left, bdd right,
                                    const state* src_left,
                                    const state* src_right,
                                    std::list<constraint>& constraint)
      {
	assert(src_left != src_right);
        // Determine which is the current case.
        bool out_scc_left = is_out_scc(left);
        bool out_scc_right = is_out_scc(right);
        bdd dest_class = bdd_existcomp(left, all_class_var_);
        assert(revert_relation_.find(dest_class) != revert_relation_.end());
        const state* dst_left = revert_relation_[dest_class];
        dest_class = bdd_existcomp(right, all_class_var_);
        const state* dst_right = revert_relation_[dest_class];

	assert(src_left != dst_left || src_right != dst_right);

        left = bdd_exist(left, all_class_var_ & on_cycle_);
        right = bdd_exist(right, all_class_var_ & on_cycle_);


        if (!out_scc_left && out_scc_right)
          {
            bdd b = bdd_exist(right, notap);
            bdd add = bdd_exist(left & b, bdd_support(b));

            if (add != bddfalse
                && bdd_exist(add, all_acceptance_conditions_) == bddtrue)
              {
		assert(src_right != dst_right);

                constraint
                  .push_back(create_cstr(new_original_[old_name_[src_right]],
                                         new_original_[old_name_[dst_right]],
                                         add));
              }
          }
        else if (out_scc_left && !out_scc_right)
          {
            bdd b = bdd_exist(left, notap);
            bdd add = bdd_exist(right & b, bdd_support(b));

            if (add != bddfalse
                && bdd_exist(add, all_acceptance_conditions_) == bddtrue)
              {
		assert(src_left != dst_left);

                constraint
                  .push_back(create_cstr(new_original_[old_name_[src_left]],
                                         new_original_[old_name_[dst_left]],
                                         add));
              }
          }
        else if (out_scc_left && out_scc_right)
          {
            bdd b = bdd_exist(left, notap);
            bdd add = bdd_exist(right & b, bdd_support(b));

            if (add != bddfalse
                && bdd_exist(add, all_acceptance_conditions_) == bddtrue)
              {
		assert(src_left != dst_left && src_right != dst_right);
		// FIXME: cas pas compris.
                constraint
                  .push_back(create_cstr(new_original_[old_name_[src_left]],
                                         new_original_[old_name_[dst_left]],
                                         add));
		constraint
		  .push_back(create_cstr(new_original_[old_name_[src_right]],
					 new_original_[old_name_[dst_right]],
                                         add));
              }

          }
        else
          assert(0);
      }


      // This function run over the signatures, and select the
      // transitions that are out of a SCC and call the function
      // create_simple_constraint to solve the problem.

      // NOTE: Currently, this may not be the most accurate method,
      // because we check for equality in the destination part of the
      // signature. We may just check the destination that can be
      // implied instead.
      std::list<constraint> create_new_constraint(const state* left,
                                                  const state* right,
                                                  map_state_bdd& state2sig)
      {
	bdd pcl = previous_class_[left];
	bdd pcr = previous_class_[right];

        bdd sigl = state2sig[left];
        bdd sigr = state2sig[right];

        std::list<constraint> res;

	bdd ex = all_class_var_ & on_cycle_;

	bdd both = pcl & pcr;
	int lc = bdd_var(pcl);
#define DEST(x) bdd_compose(bdd_existcomp(x, ex), both, lc)

        // Key is destination class, value is the signature part that
        // led to this destination class.
	map_bdd_bdd sigl_map;
	{
	  minato_isop isop(sigl & on_cycle_);
	  bdd cond_acc_dest;
	  while ((cond_acc_dest = isop.next()) != bddfalse)
	    sigl_map[DEST(cond_acc_dest)]
	      |= cond_acc_dest;
	}
	{
	  minato_isop isop(sigl & !on_cycle_);
	  bdd cond_acc_dest;
	  while ((cond_acc_dest = isop.next()) != bddfalse)
	    sigl_map[DEST(cond_acc_dest)]
	      |= cond_acc_dest;
	}
        map_bdd_bdd sigr_map;
	{
	  minato_isop isop2(sigr & on_cycle_);
	  bdd cond_acc_dest2;
	  while ((cond_acc_dest2 = isop2.next()) != bddfalse)
	    sigr_map[DEST(cond_acc_dest2)]
	      |= cond_acc_dest2;
	}
	{
	  minato_isop isop2(sigr & !on_cycle_);
	  bdd cond_acc_dest2;
	  while ((cond_acc_dest2 = isop2.next()) != bddfalse)
	    sigr_map[DEST(cond_acc_dest2)]
	      |= cond_acc_dest2;
	}

        // Iterate over the transitions of both states.
        for (map_bdd_bdd::const_iterator lit = sigl_map.begin();
             lit != sigl_map.end(); ++lit)
	    for (map_bdd_bdd::iterator rit = sigr_map.begin();
		 rit != sigr_map.end(); ++rit)
	      {
	      // And create constraints if any of the transitions
	      // is out of the SCC and the left could imply the right.
	      if ((is_out_scc(lit->second) || is_out_scc(rit->second))
		  && (bdd_exist(lit->first, on_cycle_) ==
		      bdd_exist(rit->first, on_cycle_)))
                {
                  create_simple_constraint(lit->second, rit->second,
                                           left, right, res);
                }
	      }

        return res;
      }

      inline automaton_size get_stat() const
      {
        return min_size_;
      }

      tgba* run()
      {
        // Iterate the simulation until the end. We just don't return
        // an automaton. This allows us to get all the information
        // about the states and their signature.
        main_loop();

        // Compute the don't care signatures,
        map_bdd_lstate dont_care_bdd_lstate;
        // Useful to keep track of who is who.
        map_state_bdd dont_care_state2sig;
        map_state_bdd state2sig;

        list_bdd_bdd dont_care_now_to_now;
        map_bdd_state class2state;
        list_bdd_bdd now_to_now;
        bdd_lstate_.clear();

        // Compute the don't care signature for all the states.
        for (std::list<const state*>::const_iterator my_it = order_.begin();
             my_it != order_.end();
             ++my_it)
          {
            map_state_bdd::iterator it = previous_class_.find(*my_it);
            const state* src = it->first;

            bdd sig = dont_care_compute_sig(src);
            dont_care_bdd_lstate[sig].push_back(src);
            dont_care_state2sig[src] = sig;
            dont_care_now_to_now.push_back(std::make_pair(sig, it->second));
            class2state[it->second] = it->first;

            sig = compute_sig(src);
            bdd_lstate_[sig].push_back(src);
            state2sig[src] = sig;
            now_to_now.push_back(std::make_pair(sig, it->second));
          }

        map_bdd_bdd dont_care_relation;
        map_bdd_bdd relation;
        update_po(now_to_now, relation);
        dont_care_update_po(dont_care_now_to_now, dont_care_relation);

        constraint_list cc;

        for (map_bdd_bdd::iterator it = relation.begin();
             it != relation.end();
             ++it)
          {
            revert_relation_[it->second] = class2state[it->first];
          }

        int number_constraints = 0;
        relation_ = relation;


        // order_ is here for the determinism.  Here we make the diff
        // between the two tables: imply and could_imply.
        for (std::list<const state*>::const_iterator my_it = order_.begin();
             my_it != order_.end();
             ++my_it)
          {
            map_state_bdd::iterator it = previous_class_.find(*my_it);
            assert(relation.find(it->second) != relation.end());
            assert(dont_care_relation.find(it->second)
                   != dont_care_relation.end());

            bdd care_rel = relation[it->second];
            bdd dont_care_rel = dont_care_relation[it->second];

            if (care_rel == dont_care_rel)
              continue;

            // If they are different we necessarily have
	    // dont_care_rel == care_rel & diff
            bdd diff = bdd_exist(dont_care_rel, care_rel);
	    assert(dont_care_rel == (care_rel & diff));
	    assert(diff != bddtrue);

	    do
              {
                bdd cur_diff = bdd_ithvar(bdd_var(diff));
                cc[it->second][cur_diff]
                  = create_new_constraint(it->first,
                                          class2state[cur_diff],
                                          dont_care_state2sig);
                ++number_constraints;
                diff = bdd_high(diff);
              }
	    while (diff != bddtrue);
          }
#ifndef NDEBUG
	for (map_bdd_state::const_iterator i = class2state.begin();
	     i != class2state.end(); ++i)
	  assert(previous_class_[i->second] == i->first);
#endif

        tgba* min = 0;

        map_constraint cstr;

        if (has_limit_)
          rec(cc, cstr, &min, limit_);
        else
          rec(cc, cstr, &min);

        return min;
      }

#define ERASE(inner_map, bigger_map, it)        \
      inner_map.erase(it);                      \
      if (inner_map.empty())                    \
        bigger_map.erase(bigger_map.begin())

      // Add and erase.
      void add_to_map_imply(constraint_list& constraints,
                            map_constraint& cstr)
      {
        constraint_list::iterator it = constraints.begin();
        std::map<bdd,
                 std::list<constraint>,
                 bdd_less_than>::iterator it2 = it->second.begin();

        add_to_map(it2->second, cstr);

        bdd implied_list = relation_[it2->first]; // it2->first:
                                                  // destination class.

        ERASE(it->second, constraints, it2);
        if (constraints.empty())
          return;
        it = constraints.begin();
        // At worst, implied_list is equal to it2->first.
        while (implied_list != bddtrue)
          {
            bdd cur_implied = bdd_ithvar(bdd_var(implied_list));

            std::map<bdd,
                     std::list<constraint>,
                     bdd_less_than>::iterator tmp
              = it->second.find(cur_implied);
            if (tmp != it->second.end())
              {
                add_to_map(tmp->second, cstr);
                ERASE(it->second, constraints, tmp);
                if (constraints.empty())
                  return;
              }

            implied_list = bdd_high(implied_list);
          }
      }

      // Compute recursively all the combinations.
      void rec(constraint_list constraints,
               map_constraint cstr,
               tgba** min,
               int max_depth = std::numeric_limits<int>::max())
      {
        assert(max_depth > 0);
        while (!constraints.empty())
          {
            if (!--max_depth)
                break;
            add_to_map_imply(constraints, cstr);
            rec(constraints, cstr, min, max_depth);
          }

        if (empty_seen_ && cstr.empty())
          return;
        else if (cstr.empty())
          empty_seen_ = true;

        direct_simulation<false, false> dir_sim(original_, &cstr);
        tgba* tmp = dir_sim.run();
        automaton_size cur_size = dir_sim.get_stat();
        if (*min == 0 || min_size_ > cur_size)
          {
            delete *min;
            *min = tmp;
            min_size_ = cur_size;
            res_is_deterministic = dir_sim.result_is_deterministic();
          }
        else
          {
            delete tmp;
          }
      }

      void set_limit(int n)
      {
        has_limit_ = true;
        limit_ = n;
      }

    private:
      // This bdd is used to differentiate parts of the signature that
      // are in a SCC and those that are not.
      bdd on_cycle_;

      map_bdd_bdd dont_care_relation_;

      map_bdd_state revert_relation_;

      automaton_size min_size_;

      bool empty_seen_;

      bool has_limit_;
      int limit_;

      bdd notap;
    };


  } // End namespace anonymous.


  tgba*
  simulation(const tgba* t)
  {
    direct_simulation<false, false> simul(t);
    return simul.run();
  }

  tgba*
  simulation_sba(const tgba* t)
  {
    direct_simulation<false, true> simul(t);
    return simul.run();
  }

  tgba*
  cosimulation(const tgba* t)
  {
    direct_simulation<true, false> simul(t);
    return simul.run();
  }

  tgba*
  cosimulation_sba(const tgba* t)
  {
    direct_simulation<true, true> simul(t);
    return simul.run();
  }


  template<bool Sba>
  tgba*
  iterated_simulations_(const tgba* t)
  {
    tgba* res = const_cast<tgba*> (t);
    automaton_size prev;
    automaton_size next;

    do
      {
        prev = next;
        direct_simulation<false, Sba> simul(res);
        tgba* maybe_res = simul.run();

        if (res != t)
          delete res;

        if (simul.result_is_deterministic())
          {
            res = maybe_res;
            break;
          }

        unique_ptr<tgba> after_simulation(maybe_res);
        direct_simulation<true, Sba> cosimul(after_simulation);
        unique_ptr<tgba> after_cosimulation(cosimul.run());
        next = cosimul.get_stat();
	if (Sba)
	  res = scc_filter_states(after_cosimulation);
	else
	  res = scc_filter(after_cosimulation, false);
      }
    while (prev != next);
    return res;
  }

  tgba*
  iterated_simulations(const tgba* t)
  {
    return iterated_simulations_<false>(t);
  }

  tgba*
  iterated_simulations_sba(const tgba* t)
  {
    return iterated_simulations_<true>(t);
  }

  tgba*
  dont_care_simulation(const tgba* t, int limit)
  {
    direct_simulation<false, false> sim(t);
    tgba* tmp = sim.run();

    direct_simulation_dont_care s(tmp);
    if (limit > 0)
      s.set_limit(limit);

    tgba* res = s.run();
    delete tmp;

    return res;
  }


  tgba*
  dont_care_iterated_simulations(const tgba* t, int limit)
  {
    tgba* res = const_cast<tgba*> (t);
    automaton_size prev;
    automaton_size next;

    do
      {
        prev = next;

        unique_ptr<tgba> after_simulation(dont_care_simulation(res, limit));

        if (res != t)
          delete res;

        direct_simulation<true, false> cosimul(after_simulation);
        unique_ptr<tgba> after_cosimulation(cosimul.run());
        next = cosimul.get_stat();
        res = scc_filter(after_cosimulation, true);
      }
    while (prev != next);

    return res;
  }

} // End namespace spot.
