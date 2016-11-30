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

#ifndef STATESPACERANDOMIZER_H
#define STATESPACERANDOMIZER_H

#include <config.h>
#include "Random.h"
#include "StateSpace.h"

namespace Graph
{

/******************************************************************************
 *
 * A class for generating random state spaces.
 *
 *****************************************************************************/

class StateSpaceRandomizer
{
public:
  StateSpaceRandomizer();                           /* Constructor. */

  ~StateSpaceRandomizer();                          /* Destructor. */

  StateSpace* generateGraph() const;                /* Generates a random
						     * state space.
						     */

  StateSpace* generateConnectedGraph() const;       /* Generates a random
						     * state space whose all
						     * states are reachable
						     * from its initial state.
						     */

  StateSpace* generatePath() const;                 /* Generates a random path.
						     */

  void reset();                                     /* (Re)initializes the
						     * object with default
						     * state space generation
						     * parameters.
						     */

  StateSpace::size_type min_size;                   /* Minimum size for the
						     * generated state spaces.
						     */

  StateSpace::size_type max_size;                   /* Maximum size for the
						     * generated state spaces.
						     */

  unsigned long int atoms_per_state;                /* Number of atomic
						     * propositions associated
						     * with each state of the
						     * generated state spaces.
						     */

  double edge_probability;                          /* Probability for adding
						     * random edges between
						     * state space states.
						     */

  double truth_probability;                         /* Probability of assigning
						     * the value `true' for a
						     * proposition in a state.
						     */

private:
  StateSpace::size_type chooseSize() const;         /* Chooses a size for a
						     * state space to be
						     * generated.
						     */

  StateSpaceRandomizer                              /* Prevent copying and  */
    (const StateSpaceRandomizer&);                  /* assignment of        */
  StateSpaceRandomizer& operator=                   /* StateSpaceRandomizer */
    (const StateSpaceRandomizer&);                  /* objects.             */
};



/******************************************************************************
 *
 * Inline function definitions for class StateSpaceRandomizer.
 *
 *****************************************************************************/

/* ========================================================================= */
inline StateSpaceRandomizer::StateSpaceRandomizer()
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class StateSpaceRandomizer. Initializes a new
 *                StateSpaceRandomizer object with default parameters.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  reset();
}

/* ========================================================================= */
inline StateSpaceRandomizer::~StateSpaceRandomizer()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class StateSpaceRandomizer.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline StateSpace::size_type StateSpaceRandomizer::chooseSize() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Chooses a random size for a state space to be generated.
 *
 * Arguments:     None.
 *
 * Returns:       A random integer in the interval
 *                [this->min_size, this->max_size].
 *
 * ------------------------------------------------------------------------- */
{
  return (min_size + LRAND(0, max_size - min_size + 1));
}

}

#endif /* STATESPACERANDOMIZER_H */
