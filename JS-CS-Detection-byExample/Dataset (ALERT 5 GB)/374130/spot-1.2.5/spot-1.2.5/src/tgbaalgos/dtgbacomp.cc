// -*- coding: utf-8 -*-
// Copyright (C) 2013 Laboratoire de Recherche et Développement
// de l'Epita.
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

#include "dtgbacomp.hh"
#include "ltlast/constant.hh"
#include "reachiter.hh"

namespace spot
{

  namespace
  {
    class dtgbacomp_iter: public tgba_reachable_iterator_depth_first_stack
    {
      bdd orig_acc_;
      bdd all_neg_;
      bdd acc_;
      bdd_dict* dict_;
      tgba_explicit_number* out_;
      int num_acc_;

      typedef state_explicit_number::transition trans;
    public:
      dtgbacomp_iter(const tgba* a)
	: tgba_reachable_iterator_depth_first_stack(a),
	  dict_(a->get_dict()),
	  out_(new tgba_explicit_number(dict_))
      {
	dict_->register_all_variables_of(a, out_);
	orig_acc_ = a->all_acceptance_conditions();
	all_neg_ = a->neg_acceptance_conditions();
	num_acc_ =  a->number_of_acceptance_conditions();

	// Register one acceptance condition for the result.
	int accvar = dict_->register_acceptance_variable
	  (ltl::constant::true_instance(), out_);
	acc_ = bdd_ithvar(accvar);
	out_->set_acceptance_conditions(acc_);
      }

      tgba_explicit_number*
      result()
      {
	return out_;
      }

      void
      end()
      {
	out_->merge_transitions();
	// create a sink state if needed.
	if (out_->has_state(0))
	  {
	    trans* t = out_->create_transition(0, 0);
	    t->condition = bddtrue;
	    t->acceptance_conditions = acc_;
	  }
      }

      void process_state(const state*, int n,
			 tgba_succ_iterator* i)
      {
	// add a transition to a sink state if the state is not complete.
	bdd all = bddtrue;
	for (i->first(); !i->done(); i->next())
	  all -= i->current_condition();
	if (all != bddfalse)
	  {
	    trans* t = out_->create_transition(n, 0);
	    t->condition = all;
	  }
      }

      void
      process_link(const state*, int in,
		   const state*, int out,
		   const tgba_succ_iterator* si)
      {
	assert(in > 0);
	assert(out > 0);
	bdd a = si->current_acceptance_conditions();

	// Positive states represent a non-accepting copy of the
	// original automaton.
	trans* t1 = out_->create_transition(in, out);
	t1->condition = si->current_condition();

	// Negative states encode NUM_ACC_ copies of the automaton.
	// In each copy transitions labeled by one of the acceptance
	// set have been removed, and all the remaining transitions
	// are now accepting.
	// For each state S, we have NUM_ACC_ additional copies
	// labeled S*-NUM_ACC, S*-NUM_ACC+1, ... S*-NUM_ACC+(NUM_ACC-1),
	if (a != orig_acc_)
	  {
	    bool backlink = on_stack(out);
	    int add = 0;
	    if (a == bddfalse)
	      a = all_neg_;
	    // Iterate over all the acceptance conditions in 'a'.
	    bdd ac = a;
	    while (ac != bddtrue)
	      {
		bdd h = bdd_high(ac);
		if (h == bddfalse)
		  {
		    trans* t2 = out_->create_transition(in * -num_acc_ + add,
							out * -num_acc_ + add);
		    t2->condition = si->current_condition();
		    t2->acceptance_conditions = acc_;

		    if (backlink)
		      {
			// Since we are closing a cycle, add
			// a non-deterministic transition from
			// the original automaton to this copy.
			trans* t3 =
			  out_->create_transition(in, out * -num_acc_ + add);
			t3->condition = si->current_condition();
		      }
		    ac = bdd_low(ac);
		  }
		else
		  {
		    // We know that only one variable can be positive
		    // on any branch, so since we have just seen such
		    // a variable, we want to go to explore its LOW
		    // branch for more positive variables.  The only
		    // case where we will not do that is if the LOW
		    // branch is false.  In that case we take the HIGH
		    // branch to enumerate all the remaining negated
		    // variables.
		    bdd tmp = bdd_low(ac);
		    if (tmp != bddfalse)
		      ac = tmp;
		    else
		      ac = h;
		  }
		++add;
	      }
	    assert(add == num_acc_);
	  }
      }

    };

  } // anonymous

  tgba_explicit_number* dtgba_complement(const tgba* aut)
  {
    dtgbacomp_iter dci(aut);
    dci.run();
    return dci.result();
  }
}
