// -*- coding: utf-8 -*-
// Copyright (C) 2012 Laboratoire de Recherche et Développement de
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

#include "postproc.hh"
#include "minimize.hh"
#include "simulation.hh"
#include "sccfilter.hh"
#include "degen.hh"
#include "stats.hh"
#include "stripacc.hh"

namespace spot
{
  unsigned count_states(const tgba* a)
  {
    // FIXME: the number of states can be found more
    // efficiently in explicit automata.
    tgba_statistics st = stats_reachable(a);
    return st.states;
  }

  const tgba* postprocessor::run(const tgba* a, const ltl::formula* f)
  {
    if (type_ == TGBA && pref_ == Any && level_ == Low)
      return a;

    // Remove useless SCCs.
    {
      const tgba* s = scc_filter(a, false);
      delete a;
      a = s;
    }

    if (type_ == Monitor)
      {
	if (pref_ == Deterministic)
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
	if (pref_ == Any)
	  return a;

	const tgba* sim;
	if (level_ == Low)
	  sim = simulation(a);
	else
	  sim = iterated_simulations(a);
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
	    return sim;
	  }
	else
	  {
	    delete sim;
	    return m;
	  }
      }

    if (pref_ == Any)
      {
	if (type_ == BA)
	  {
	    const tgba* d = degeneralize(a);
	    delete a;
	    a = d;
	  }
	return a;
      }

    const tgba* wdba = 0;
    const tgba* sim = 0;

    // (Small,Low) is the only configuration where we do not run
    // WDBA-minimization.
    if (pref_ != Small || level_ != Low)
      {
	bool reject_bigger = (pref_ == Small) && (level_ == Medium);
	wdba = minimize_obligation(a, f, 0, reject_bigger);
	if (wdba == a)	// Minimization failed.
	  wdba = 0;
	// The WDBA is a BA, so no degeneralization required.
      }

    // Run a simulation when wdba failed (or was not run), or
    // at hard levels if we want a small output.
    if (!wdba || (level_ == High && pref_ == Small))
      {
	if (level_ == Low)
	  sim = simulation(a);
	else
	  sim = iterated_simulations(a);

	// Degeneralize the result of the simulation if needed.
	if (type_ == BA)
	  {
	    const tgba* d = degeneralize(sim);
	    delete sim;
	    sim = d;
	  }
      }

    delete a;

    if (wdba && sim)
      {
	if (count_states(wdba) > count_states(sim))
	  {
	    delete wdba;
	    wdba = 0;
	  }
	else
	  {
	    delete sim;
	    sim = 0;
	  }
      }

    if (sim && type_ == TGBA && level_ == High)
      {
	const tgba* s = scc_filter(sim, true);
	delete sim;
	return s;
      }

    return wdba ? wdba : sim;
  }
}
