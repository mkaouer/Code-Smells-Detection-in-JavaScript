/*
 *  Copyright (C) 1999, 2000, 2001, 2002, 2003, 2004, 2005
 *  Heikki Tauriainen <Heikki.Tauriainen@tkk.fi>
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

#include "BuchiProduct.h"

namespace Graph
{

/******************************************************************************
 *
 * Static member definitions for class BuchiProduct.
 *
 *****************************************************************************/

map< ::Ltl::LtlFormula*, BuchiProduct::SatisfiabilityMapping>
  BuchiProduct::sat_cache;



/******************************************************************************
 *
 * Function definitions for class BuchiProduct.
 *
 *****************************************************************************/

/* ========================================================================= */
bool BuchiProduct::synchronizable
  (const Graph<GraphEdgeContainer>::Edge& transition_1,
   const Graph<GraphEdgeContainer>::Edge& transition_2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Tests whether two transitions of two Büchi automata are
 *                synchronizable by checking whether the conjunction of their
 *                guard formulas is satisfiable.
 *
 * Arguments:     transition_1,  --  Constant references to the transitions.
 *                transition_2
 *
 * Returns:       true iff the transitions are synchronizable.  The result is
 *                also stored into `this->sat_cache' for later reference.
 *
 * ------------------------------------------------------------------------- */
{
  using ::Ltl::LtlFormula;
  using ::Ltl::And;

  LtlFormula* guard_1 = &static_cast<const BuchiAutomaton::BuchiTransition&>
                           (transition_1).guard();
  LtlFormula* guard_2 = &static_cast<const BuchiAutomaton::BuchiTransition&>
                           (transition_2).guard();

  if (guard_2 > guard_1)
  {
    LtlFormula* swap_guard = guard_2;
    guard_2 = guard_1;
    guard_1 = swap_guard;
  }

  map<LtlFormula*, SatisfiabilityMapping>::iterator
    sat_cache_element = sat_cache.find(guard_1);

  if (sat_cache_element == sat_cache.end())
    sat_cache_element = sat_cache.insert
                          (make_pair(guard_1, SatisfiabilityMapping())).first;
  else
  {
    SatisfiabilityMapping::const_iterator sat_result
      = sat_cache_element->second.find(guard_2);
    if (sat_result != sat_cache_element->second.end())
      return sat_result->second;
  }

  LtlFormula* f = &And::construct(*guard_1, *guard_2);
  const bool result = f->satisfiable();
  LtlFormula::destruct(f);
  sat_cache_element->second.insert(make_pair(guard_2, result));
  return result;
}

}
