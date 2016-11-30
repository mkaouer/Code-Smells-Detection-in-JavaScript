// -*- coding: utf-8 -*-
// Copyright (C) 2013, 2014 Laboratoire de Recherche et Développement de
// l'Epita (LRDE).
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

#include "public.hh"
#include "tgbaalgos/reachiter.hh"
#include "tgbaalgos/sccfilter.hh"
#include "ltlast/constant.hh"

namespace spot
{
  namespace
  {
    // Christof Löding's Diploma Thesis: Methods for the
    // Transformation of ω-Automata: Complexity and Connection to
    // Second Order Logic.  Section 3.4.3: Rabin to Büchi.
    //
    // However beware that the {...,(Ei,Fi),...} pairs used by Löding
    // are reversed compared to the {...,(Li,Ui),...} pairs used by
    // several other people.  We have Ei=Ui and Fi=Li.
    class nra_to_nba_worker: public tgba_reachable_iterator_depth_first
    {
    public:
      // AUT is the automate we iterate on, while A is the automaton
      // we read the acceptance conditions from.  Separating the two
      // makes its possible to mask AUT, as needed in dra_to_ba().
      nra_to_nba_worker(const dstar_aut* a, const tgba* aut):
	tgba_reachable_iterator_depth_first(aut),
	out_(new tgba_explicit_number(aut->get_dict())),
	d_(a),
	num_states_(a->aut->num_states())
      {
	bdd_dict* bd = out_->get_dict();
	bd->register_all_variables_of(aut, out_);

	// Invent a new acceptance set for the degeneralized automaton.
	int accvar =
	  bd->register_acceptance_variable(ltl::constant::true_instance(),
					   out_);
	acc_ = bdd_ithvar(accvar);
	out_->set_acceptance_conditions(acc_);
      }

      tgba_explicit_number*
      result()
      {
	return out_;
      }

      void
      process_link(const state* sin, int,
		   const state* sout, int,
		   const tgba_succ_iterator* si)
      {
	int in = d_->aut->get_label(sin);
	int out = d_->aut->get_label(sout);

	typedef state_explicit_number::transition trans;
	trans* t = out_->create_transition(in, out);
	bdd cond = t->condition = si->current_condition();

	// Create one clone of the automaton per accepting pair,
	// removing states from the Ui part of the (Li, Ui) pairs.
	// (Or the Ei part of Löding's (Ei, Fi) pairs.)
	bitvect& l = d_->accsets->at(2 * in);
	bitvect& u = d_->accsets->at(2 * in + 1);
	for (size_t i = 0; i < d_->accpair_count; ++i)
	  {
	    int shift = num_states_ * (i + 1);
	    // In the Ui set. (Löding's Ei set.)
	    if (!u.get(i))
	      {
		// Transition t1 is a non-deterministic jump
		// from the original automaton to the i-th clone.
		//
		// Transition t2 constructs the clone.
		//
		// Löding creates transition t1 regardless of the
		// acceptance set.  We restrict it to the non-Li
		// states.  Both his definition and this
		// implementation create more transitions than needed:
		// we do not need more than one transition per
		// accepting cycle.
		trans* t1 = out_->create_transition(in, out + shift);
		t1->condition = cond;

		trans* t2 = out_->create_transition(in + shift, out + shift);
		t2->condition = cond;
		// In the Li set. (Löding's Fi set.)
		if (l.get(i))
		  t2->acceptance_conditions = acc_;
	      }
	  }
      }

    protected:
      tgba_explicit_number* out_;
      const dstar_aut* d_;
      size_t num_states_;
      bdd acc_;
    };

  }

  // In dra_to_dba() we call this function with a second argument
  // that is a masked version of nra->aut.
  SPOT_LOCAL
  tgba* nra_to_nba(const dstar_aut* nra, const tgba* aut)
  {
    assert(nra->type == Rabin);
    nra_to_nba_worker w(nra, aut);
    w.run();
    tgba_explicit_number* res1 = w.result();
    tgba* res2 = scc_filter_states(res1);
    delete res1;
    return res2;
  }

  SPOT_API
  tgba* nra_to_nba(const dstar_aut* nra)
  {
    return nra_to_nba(nra, nra->aut);
  }

}
