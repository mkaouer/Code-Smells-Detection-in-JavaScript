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

#include <climits>
#include <config.h>
#include <map>
#include "LbttAlloc.h"
#include "BitArray.h"
#include "Exception.h"
#include "StateSpaceRandomizer.h"

namespace Graph
{

/******************************************************************************
 *
 * Function definitions for class StateSpaceRandomizer.
 *
 *****************************************************************************/

/* ========================================================================= */
void StateSpaceRandomizer::reset()
/* ----------------------------------------------------------------------------
 *
 * Description:   (Re)initializes the StateSpaceRandomizer object with default
 *                state space generation parameters.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  min_size = max_size = 20;
  atoms_per_state = 5;
  edge_probability = 0.2;
  truth_probability = 0.5;
}

/* ========================================================================= */
StateSpace* StateSpaceRandomizer::generateGraph() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Generates a random state space with the current graph
 *                generation parameters.
 *
 * Arguments:     None.
 *
 * Returns:       A pointer to a newly allocated StateSpace object.
 *
 * ------------------------------------------------------------------------- */
{
  StateSpace::size_type size = chooseSize();

  StateSpace* statespace = new StateSpace(atoms_per_state, size);

  statespace->initialState() = 0;

  if (size > 0)
  {
    try
    {
      bool has_successor;

      for (StateSpace::size_type i = 0; i < size; i++)
      {
	if (::user_break)
	  throw UserBreakException();

	has_successor = false;

	for (StateSpace::size_type j = 0; j < size; j++)
        {
	  if (DRAND() < edge_probability)
          {
	    statespace->connect(i, j);
	    has_successor = true;
	  }
	}

	if (!has_successor)
	  statespace->connect(i, LRAND(0, size));

	for (unsigned long int j = 0; j < atoms_per_state; j++)
        {
	  if (DRAND() < truth_probability)
	    (*statespace)[i].positiveAtoms().setBit(j);
	}
      }
    }
    catch (...)
    {
      delete statespace;
      throw;
    }
  }

  return statespace;
}

/* ========================================================================= */
StateSpace* StateSpaceRandomizer::generateConnectedGraph() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Initializes a random connected state space whose each state
 *                is reachable from its initial state 0.
 *
 * Arguments:     None.
 *
 * Returns:       A pointer to a newly allocated StateSpace object.
 *
 * ------------------------------------------------------------------------- */
{
  StateSpace::size_type size = chooseSize();

  StateSpace* statespace = new StateSpace(atoms_per_state, size);

  statespace->initialState() = 0;

  if (size > 0)
  {
    try
    {
      /* Random graph generation algorithm:
	 ----------------------------------

	 allocate number_of_states nodes;

	 insert node 0 to the set of `reachable but unprocessed' nodes (nodes
	 that are reachable from the root node but whose children have not yet
	 been selected)

	 while the set of `reachable but unprocessed' nodes in nonempty 
         {
           select a (random) node X from the set;

           if there exists a node Y that is not yet reachable from root node 
           {
             connect X to Y by making X the parent node of Y;
             insert Y to the set of `reachable but unprocessed' nodes;
           }

           for each node Z (excluding node Y) in the graph 
           {
             randomly connect X to Z (making X the parent of Z);

             if Z was not previously reachable from the root node
               insert Z to the set of `reachable but unprocessed' nodes;
            }

            remove X from the set of `reachable but unprocessed' nodes;
          }

          In the following implementation, the random truth value allocation
	  for propositions that hold in a state is interleaved with the
	  previous algorithm.
      */

      StateSpace::size_type first_unreachable_state = 1, random_node;

      multimap<long int, StateSpace::size_type>	reachable_but_unprocessed;

      reachable_but_unprocessed.insert(make_pair(0, 0));

      while (!reachable_but_unprocessed.empty()) 
      {
	if (::user_break)
	  throw UserBreakException();

	random_node = (*(reachable_but_unprocessed.begin())).second;
	reachable_but_unprocessed.erase(reachable_but_unprocessed.begin());

	for (StateSpace::size_type n = 0; n < first_unreachable_state; ++n)
        {
	  if (DRAND() < edge_probability)
	    statespace->connect(random_node, n);
	}

	if (first_unreachable_state < size)
        {
	  statespace->connect(random_node, first_unreachable_state);
	  reachable_but_unprocessed.insert(make_pair(LRAND(0, LONG_MAX),
						     first_unreachable_state));
	  ++first_unreachable_state;

	  for (StateSpace::size_type i = first_unreachable_state; i < size;
	       ++i) 
          {
	    if (DRAND() < edge_probability)
            {
	      statespace->connect(random_node, first_unreachable_state);
	      reachable_but_unprocessed.insert
		(make_pair(LRAND(0, LONG_MAX), first_unreachable_state));
	      ++first_unreachable_state;
	    }
	  }
	}

	if ((*statespace)[random_node].edges().empty())
	  statespace->connect(random_node, random_node);

	BitArray& atoms = (*statespace)[random_node].positiveAtoms();
	for (unsigned long int i = 0; i < atoms_per_state; ++i) 
        {
	  if (DRAND() < truth_probability)
	    atoms.setBit(i);
	}
      }
    }
    catch (...)
    {
      delete statespace;
      throw;
    }
  }

  return statespace;
}

/* ========================================================================= */
StateSpace* StateSpaceRandomizer::generatePath() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Generates a random Kripke structure that consists of a finite
 *                prefix of states that ends in a loop. The initial state of
 *                the generated structure will be at index 0.
 *
 * Arguments:     None.
 *
 * Returns:       A pointer to a newly allocated StateSpace object.
 *
 * ------------------------------------------------------------------------- */
{
  StateSpace::size_type size = chooseSize();

  StateSpace* statespace = new StateSpace(atoms_per_state, size);

  statespace->initialState() = 0;

  if (size > 0)
  {
    try
    {
      for (StateSpace::size_type i = 0; i + 1 < size; i++)
      {
	if (::user_break)
	  throw UserBreakException();

	statespace->connect(i, i + 1);
	for (unsigned long int j = 0; j < atoms_per_state; j++)
	{
	  if (DRAND() < truth_probability)
	    (*statespace)[i].positiveAtoms().setBit(j);
	}
      }

      statespace->connect(size - 1, LRAND(0, size));
    }
    catch (...)
    {
      delete statespace;
      throw;
    }
  }

  return statespace;
}

}
