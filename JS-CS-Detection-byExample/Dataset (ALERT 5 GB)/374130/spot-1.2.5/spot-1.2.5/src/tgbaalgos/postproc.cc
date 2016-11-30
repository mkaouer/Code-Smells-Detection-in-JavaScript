// -*- coding: utf-8 -*-
// Copyright (C) 2012, 2013, 2014 Laboratoire de Recherche et
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

#include "postproc.hh"
#include "minimize.hh"
#include "simulation.hh"
#include "sccfilter.hh"
#include "degen.hh"
#include "stripacc.hh"
#include <cstdlib>
#include "misc/optionmap.hh"
#include "priv/countstates.hh"
#include "powerset.hh"
#include "isdet.hh"
#include "tgba/tgbatba.hh"
#include "dtbasat.hh"
#include "dtgbasat.hh"
#include "complete.hh"

namespace spot
{

  postprocessor::postprocessor(const option_map* opt)
    : type_(TGBA), pref_(Small), level_(High),
      degen_reset_(true), degen_order_(false), degen_cache_(true),
      degen_lskip_(true), simul_(-1), scc_filter_(-1), ba_simul_(-1),
      tba_determinisation_(false), sat_minimize_(0), sat_acc_(0),
      sat_states_(0), state_based_(false), wdba_minimize_(true)
  {
    if (opt)
      {
	degen_order_ = opt->get("degen-order", 0);
	degen_reset_ = opt->get("degen-reset", 1);
	degen_cache_ = opt->get("degen-lcache", 1);
	degen_lskip_ = opt->get("degen-lskip", 1);
	simul_ = opt->get("simul", -1);
	simul_limit_ = opt->get("simul-limit", -1);
	scc_filter_ = opt->get("scc-filter", -1);
	ba_simul_ = opt->get("ba-simul", -1);
	tba_determinisation_ = opt->get("tba-det", 0);
	sat_minimize_ = opt->get("sat-minimize", 0);
	sat_acc_ = opt->get("sat-acc", 0);
	sat_states_ = opt->get("sat-states", 0);
	state_based_ = opt->get("state-based", 0);
	wdba_minimize_ = opt->get("wdba-minimize", 1);

	if (sat_acc_ && sat_minimize_ == 0)
	  sat_minimize_ = 1;	// 2?
	if (sat_states_ && sat_minimize_ == 0)
	  sat_minimize_ = 1;
	if (sat_minimize_)
	  {
	    tba_determinisation_ = 1;
	    if (sat_acc_ <= 0)
	      sat_acc_ = -1;
	    if (sat_states_ <= 0)
	      sat_states_ = -1;
	  }
      }
  }

  const tgba* postprocessor::do_simul(const tgba* a, int opt)
  {
    switch (opt)
      {
      case 0:
	return a;
      case 1:
	return simulation(a);
      case 2:
	return cosimulation(a);
      case 3:
      default:
	return iterated_simulations(a);
      case 4:
	return dont_care_simulation(a, simul_limit_);
      case 5:
	return dont_care_iterated_simulations(a, simul_limit_);
      }
  }

  const tgba* postprocessor::do_ba_simul(const tgba* a, int opt)
  {
    switch (opt)
      {
      case 0:
	return a;
      case 1:
	return simulation_sba(a);
      case 2:
	return cosimulation_sba(a);
      case 3:
      default:
	return iterated_simulations_sba(a);
      }
  }


  const tgba* postprocessor::do_degen(const tgba* a)
  {
    const tgba* d = degeneralize(a,
				 degen_reset_,
				 degen_order_,
				 degen_cache_,
				 degen_lskip_);
    delete a;
    if (ba_simul_ <= 0)
      return d;

    const tgba* s = do_ba_simul(d, ba_simul_);
    if (s != d)
      delete d;

    return s;
  }

#define PREF_ (pref_ & (Small | Deterministic))
#define COMP_ (pref_ & Complete)

  const tgba* postprocessor::run(const tgba* a, const ltl::formula* f)
  {
    if (type_ == TGBA && PREF_ == Any && level_ == Low)
      return a;

    if (simul_ < 0)
      simul_ = (level_ == Low) ? 1 : 3;
    if (ba_simul_ < 0)
      ba_simul_ = (level_ == High) ? 3 : 0;
    if (scc_filter_ < 0)
      scc_filter_ = 1;
    if (type_ == BA)
      state_based_ = true;

    int original_acc = a->number_of_acceptance_conditions();

    // Remove useless SCCs.
    if (type_ == Monitor)
      {
	// Do not bother about acceptance conditions, they will be
	// ignored.
	const tgba* s = scc_filter_states(a);
	delete a;
	a = s;
      }
    else if (scc_filter_ > 0)
      {
	const tgba* s = scc_filter(a, scc_filter_ > 1);
	delete a;
	a = s;
      }

    if (type_ == Monitor)
      {
	if (PREF_ == Deterministic)
	  {
	    const tgba* m = minimize_monitor(a);
	    delete a;
	    return m;
	  }
	else
	  {
	    const tgba* m = strip_acceptance(a);
	    delete a;
	    a = m;
	  }
	if (PREF_ == Any)
	  return a;

	const tgba* sim = do_simul(a, simul_);
	if (a == sim)
	  // simulation was disabled.
	  return a;
	if (level_ != High)
	  {
	    delete a;
	    return sim;
	  }
	// For Small,High we return the smallest between the output of
	// the simulation, and that of the deterministic minimization.
	const tgba* m = minimize_monitor(a);
	delete a;
	if (count_states(m) > count_states(sim))
	  {
	    delete m;
	  }
	else
	  {
	    delete sim;
	    sim = m;
	  }
	if (COMP_ == Complete)
	  {
	    const tgba* s = tgba_complete(sim);
	    delete sim;
	    sim = s;
	  }
	return sim;
      }

    if (PREF_ == Any)
      {
	if (type_ == BA)
	  a = do_degen(a);
	return a;
      }

    bool dba_is_wdba = false;
    bool dba_is_minimal = false;
    const tgba* dba = 0;
    const tgba* sim = 0;

    // (Small,Low) is the only configuration where we do not run
    // WDBA-minimization.
    if ((PREF_ != Small || level_ != Low) && wdba_minimize_)
      {
	bool reject_bigger = (PREF_ == Small) && (level_ == Medium);
	dba = minimize_obligation(a, f, 0, reject_bigger);
	if (dba == a)	// Minimization failed.
	  dba = 0;
	else
	  dba_is_minimal = dba_is_wdba = true;
	// The WDBA is a BA, so no degeneralization is required.
      }

    // Run a simulation when wdba failed (or was not run), or
    // at hard levels if we want a small output.
    if (!dba || (level_ == High && PREF_ == Small))
      {
	sim = do_simul(a, simul_);

	if (sim != a)
	  delete a;

	// Degeneralize the result of the simulation if needed.
	// No need to do that if tba_determinisation_ will be used.
	if (type_ == BA && !tba_determinisation_)
	  sim = do_degen(sim);
      }
    else
      {
	delete a;
      }

    // If WDBA failed, but the simulation returned a deterministic
    // automaton, use it as dba.
    assert(dba || sim);
    if (!dba && is_deterministic(sim))
      {
	std::swap(sim, dba);
	// We postponed degeneralization above i case we would need
	// to perform TBA-determinisation, but now it is clear
	// that we won't perform it.  So do degeneralize.
	if (tba_determinisation_ && type_ == BA)
	  {
	    dba = do_degen(dba);
	    assert(is_deterministic(dba));
	  }
      }

    // If we don't have a DBA, attempt tba-determinization if requested.
    if (tba_determinisation_ && !dba)
      {
	const tgba* tmpd = 0;
	if (PREF_ == Deterministic
	    && f
	    && f->is_syntactic_recurrence()
	    && sim->number_of_acceptance_conditions() > 1)
	  tmpd = new tgba_tba_proxy(sim);

	const tgba* in = tmpd ? tmpd : sim;

	// These thresholds are arbitrary.
	//
	// For producing Small automata, we assume that a
	// deterministic automaton that is twice the size of the
	// original will never get reduced to a smaller one.  We also
	// do not want more than 2^13 cycles in an SCC.
	//
	// For Deterministic automata, we accept automata that
	// are 8 times bigger, with no more that 2^15 cycle per SCC.
	// The cycle threshold is the most important limit here.  You
	// may up it if you want to try producing larger automata.
	const tgba* tmp =
	  tba_determinize_check(in,
				(PREF_ == Small) ? 2 : 8,
				1 << ((PREF_ == Small) ? 13 : 15),
				f);
	if (tmp != 0 && tmp != in)
	  {
	    // There is no point in running the reverse simulation on
	    // a deterministic automaton, since all prefixes are
	    // unique.
	    dba = simulation(tmp);
	    delete tmp;
	  }
	delete tmpd;
	if (dba && PREF_ == Deterministic)
	  {
	    // disregard the result of the simulation.
	    delete sim;
	    sim = 0;
	  }
	else
	  {
	    // degeneralize sim, because we did not do it earlier
	    if (type_ == BA)
	      sim = do_degen(sim);
	  }
      }

    // Now dba contains either the result of WDBA-minimization (in
    // that case dba_is_wdba=true), or some deterministic automaton
    // that is either the result of the simulation or of the
    // TBA-determinization (dba_is_wdba=false in both cases).  If the
    // dba is a WDBA, we do not have to run SAT-minimization.  A
    // negative value in sat_minimize_ can for its use for debugging.
    if (sat_minimize_ && dba && (!dba_is_wdba || sat_minimize_ < 0))
      {
	unsigned target_acc;
	if (type_ == BA)
	  target_acc = 1;
	else if (sat_acc_ != -1)
	  target_acc = sat_acc_;
	else
	  // Take the number of acceptance conditions from the input
	  // automaton, not from dba, because dba often has been
	  // degeneralized to beform tba_determinize_check().  MAke
	  // sure it is at least 1.
	  target_acc = original_acc > 0 ? original_acc : 1;

	const tgba* in = 0;
	const tgba* to_free = 0;
	if (target_acc == 1)
	  {
	    // If we are seeking a minimal DBA with unknown number of
	    // states, then we should start from the degeneralized,
	    // because the input TBA might be smaller.
	    if (state_based_)
	      to_free = in = degeneralize(dba);
	    else if (dba->number_of_acceptance_conditions() != 1)
	      to_free = in = new tgba_tba_proxy(dba);
	    else
	      in = dba;
	  }
	else
	  {
	    in = dba;
	  }

	const tgba* cmp = tgba_complete(in);
	const tgba* res = 0;
	if (target_acc == 1)
	  {
	    if (sat_states_ != -1)
	      res = dtba_sat_synthetize(cmp, sat_states_, state_based_);
	    else if (sat_minimize_ == 1 || sat_minimize_ == -1)
	      res = dtba_sat_minimize(cmp, state_based_);
	    else  // sat_minimize_ == 2
	      res = dtba_sat_minimize_dichotomy(cmp, state_based_);
	  }
	else
	  {
	    if (sat_states_ != -1)
	      res = dtgba_sat_synthetize(cmp, target_acc, sat_states_,
					 state_based_);
	    else if (sat_minimize_ == 1 || sat_minimize_ == -1)
	      res = dtgba_sat_minimize(cmp, target_acc, state_based_);
	    else  // sat_minimize_ == 2
	      res = dtgba_sat_minimize_dichotomy(cmp, target_acc, state_based_);
	  }
	delete cmp;
	delete to_free;
	if (res != 0)
	  {
	    delete dba;
	    if (state_based_)
	      // FIXME: This does not simplify generalized acceptance
	      // conditions, but calling scc_filter() would break the
	      // BA-typeness of res by removing acceptance marks from
	      // out-of-SCC transitions.
	      dba = scc_filter_states(res);
	    else
	      dba = scc_filter(res, true);
	    delete res;
	    dba_is_minimal = true;
	  }
      }

    // Degeneralize the dba resulting from tba-determinization or
    // sat-minimization (which is a TBA) if requested and needed.
    if (dba && !dba_is_wdba && type_ == BA
	&& !(dba_is_minimal && state_based_
	     && dba->number_of_acceptance_conditions() == 1))
      {
	const tgba* d = degeneralize(dba);
	delete dba;
	dba = d;
      }

    if (dba && sim)
      {
	if (count_states(dba) > count_states(sim))
	  {
	    delete dba;
	    dba = 0;
	  }
	else
	  {
	    delete sim;
	    sim = 0;
	  }
      }


    if (type_ == TGBA && level_ == High && scc_filter_ != 0)
      {
	if (dba && !dba_is_minimal) // WDBA is already clean.
	  {
	    const tgba* s = scc_filter(dba, true);
	    delete dba;
	    assert(!sim);
	    dba = s;
	  }
	else if (sim)
	  {
	    const tgba* s = scc_filter(sim, true);
	    delete sim;
	    assert(!dba);
	    sim = s;
	  }
      }

    sim = dba ? dba : sim;

    if (COMP_ == Complete)
      {
	const tgba* s = tgba_complete(sim);
	delete sim;
	sim = s;
      }
    return sim;
  }
}
