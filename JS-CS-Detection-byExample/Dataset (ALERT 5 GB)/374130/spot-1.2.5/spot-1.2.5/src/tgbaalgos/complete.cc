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

#include "complete.hh"
#include "reachiter.hh"
#include "ltlast/constant.hh"

namespace spot
{

  namespace
  {
    class tgbacomplete_iter:
      public tgba_reachable_iterator_depth_first_stack
    {
      bdd_dict* dict_;
      tgba_explicit_number* out_;
      bdd addacc_;

      typedef state_explicit_number::transition trans;
    public:
      tgbacomplete_iter(const tgba* a)
	: tgba_reachable_iterator_depth_first_stack(a),
	  dict_(a->get_dict()),
	  out_(new tgba_explicit_number(dict_)),
	  addacc_(bddfalse)
      {
	dict_->register_all_variables_of(a, out_);

	if (a->number_of_acceptance_conditions() == 0)
	  {
	    const ltl::formula* t = ltl::constant::true_instance();
	    int accvar =
	      dict_->register_acceptance_variable(t, out_);
	    addacc_ = bdd_ithvar(accvar);
	    out_->set_acceptance_conditions(addacc_);
	  }
	else
	  {
	    out_->set_acceptance_conditions(a->all_acceptance_conditions());
	  }
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
	  }
      }

      void process_state(const state*, int n,
			 tgba_succ_iterator* i)
      {
	// add a transition to a sink state if the state is not complete.
	bdd all = bddtrue;
	bdd acc = bddfalse;
	i->first();
	// In case the automaton use state-based acceptance, propagate
	// the acceptance of the first transition to the one we add.
	if (!i->done())
	  acc = i->current_acceptance_conditions();
	for (; !i->done(); i->next())
	  all -= i->current_condition();
	if (all != bddfalse)
	  {
	    trans* t = out_->create_transition(n, 0);
	    t->condition = all;
	    t->acceptance_conditions = acc | addacc_;
	  }
      }

      void
      process_link(const state*, int in,
		   const state*, int out,
		   const tgba_succ_iterator* si)
      {
	assert(in > 0);
	assert(out > 0);
	trans* t1 = out_->create_transition(in, out);

	t1->condition = si->current_condition();
	t1->acceptance_conditions =
	  si->current_acceptance_conditions() | addacc_;
      }

    };

  } // anonymous

  tgba_explicit_number* tgba_complete(const tgba* aut)
  {
    tgbacomplete_iter ci(aut);
    ci.run();
    return ci.result();
  }
}


