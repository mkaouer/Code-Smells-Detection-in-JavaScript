// -*- coding: utf-8 -*-
// Copyright (C) 2009, 2011, 2012, 2013 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2004, 2005, 2007 Laboratoire d'Informatique de
// Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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

#define SKIP_DEPRECATED_WARNING
#include "reductgba_sim.hh"
#include "sccfilter.hh"
#include "simulation.hh"
#include "dupexp.hh"

namespace spot
{
  const tgba*
  reduc_tgba_sim(const tgba* f, int opt)
  {
    if (opt & Reduce_Scc)
      {
	f = scc_filter(f);

	// No more reduction requested? Return the automaton as-is.
	if (opt == Reduce_Scc)
	  return f;
      }

    if (opt & (Reduce_quotient_Dir_Sim | Reduce_transition_Dir_Sim
	       | Reduce_quotient_Del_Sim | Reduce_transition_Del_Sim))
      {
	tgba* res = simulation(f);

	if (opt & Reduce_Scc)
	  delete f;

	return res;
      }

    return tgba_dupexp_dfs(f);
  }

}
