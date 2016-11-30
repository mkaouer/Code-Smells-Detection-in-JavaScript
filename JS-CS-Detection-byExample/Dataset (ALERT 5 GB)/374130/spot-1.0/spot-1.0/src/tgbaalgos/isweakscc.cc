// Copyright (C) 2012 Laboratoire de Recherche et Developpement de
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

#ifndef SPOT_TGBAALGOS_ISWEAKSCC_CC
# define SPOT_TGBAALGOS_ISWEAKSCC_CC

#include "cycles.hh"

namespace spot
{
  namespace
  {
    // Look for a non-accepting cycle.
    class weak_checker: public enumerate_cycles
    {
    public:
      bool result;

      weak_checker(const scc_map& map)
	: enumerate_cycles(map), result(true)
      {
      }

      virtual bool
      cycle_found(const state* start)
      {
	dfs_stack::const_reverse_iterator i = dfs_.rbegin();
	bdd acc = bddfalse;
	for (;;)
	  {
	    acc |= i->succ->current_acceptance_conditions();
	    if (i->ts->first == start)
	      break;
	    ++i;
	    // The const cast is here to please old g++ versions.
	    // At least version 4.0 needs it.
	    assert(i != const_cast<const dfs_stack&>(dfs_).rend());
	  }
	if (acc != aut_->all_acceptance_conditions())
	  {
	    // We have found an non-accepting cycle, so the SCC is not
	    // weak.
	    result = false;
	    return false;
	  }
	return true;
      }

    };

  }

  bool
  is_weak_scc(scc_map& map, unsigned scc)
  {
    // If no cycle is accepting, the SCC is weak.
    if (!map.accepting(scc))
      return true;
    // If all transitions use all acceptance conditions, the SCC is weak.
    if (map.useful_acc_of(scc) ==
	bdd_support(map.get_aut()->neg_acceptance_conditions()))
      return true;
    // If the SCC is accepting, but one cycle is not, the SCC is not
    // weak.
    weak_checker w(map);
    w.run(scc);
    return w.result;
  }


}

#endif // SPOT_TGBAALGOS_ISWEAKSCC_CC
