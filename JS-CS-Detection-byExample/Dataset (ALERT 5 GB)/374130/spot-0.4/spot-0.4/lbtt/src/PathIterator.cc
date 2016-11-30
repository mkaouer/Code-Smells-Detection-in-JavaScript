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

#include <config.h>
#include "BitArray.h"
#include "PathIterator.h"

namespace Graph
{

/******************************************************************************
 *
 * Function definitions for class PathIterator.
 *
 *****************************************************************************/

/* ========================================================================= */
PathIterator::PathIterator
  (unsigned long int propositions_per_state,
   StateSpace::size_type number_of_states) :
  path(propositions_per_state, number_of_states), loop_target_state(0)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class PathIterator. Creates a new object for
 *                enumerating all paths with a given number of states and
 *                atomic propositions.
 *
 * Arguments:     propositions_per_state  --  Number of atomic propositions
 *                                            per a state in the path.
 *                number_of_states        --  Number of states in the path.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  StateSpace::size_type state;

  for (state = 0; (state + 1) < number_of_states; state++)
    path.connect(state, state + 1);

  path.connect(state, 0);
}

/* ========================================================================= */
bool PathIterator::operator==(const PathIterator& it) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Equivalence operator for class PathIterator. Two
 *                PathIterators are equivalent if and only if all of the
 *                following conditions hold:
 *                   (1)  Both iterators point to paths of equal size with the
 *                        same number of atomic propositions in each state.
 *                   (2)  Starting from the initial states of the paths pointed
 *                        to by the individual iterators, the paths agree on
 *                        the truth values of the atomic propositions in the
 *                        corresponding states of the paths.
 *                   (3)  Both paths contain a cycle of an equal length.
 *
 * Argument:      it  --  A constant reference to a PathIterator.
 *
 * Returns:       A truth value according to the result of the test.
 *
 * ------------------------------------------------------------------------- */
{
  if (loop_target_state != it.loop_target_state
      || path.size() != it.path.size()
      || path.numberOfPropositions() != it.path.numberOfPropositions())
    return false;

  StateSpace::size_type state;

  for (state = 0;
       state < path.size()
	 && path[state].positiveAtoms().equal(it.path[state].positiveAtoms(),
					      path.numberOfPropositions());
       state++)
    ;

  return (state == path.size());
}

/* ========================================================================= */
bool PathIterator::operator!=(const PathIterator& it) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Inequivalence operator for class PathIterator. See above for
 *                the definition of equivalence between two PathIterators; two
 *                PathIterators are inequal if and only if they are not equal.
 *
 * Argument:      it  --  A constant reference to a PathIterator.
 *
 * Returns:       A truth value according to the result of the test.
 *
 * ------------------------------------------------------------------------- */
{
  if (loop_target_state != it.loop_target_state
      || path.size() != it.path.size()
      || path.numberOfPropositions() != it.path.numberOfPropositions())
    return true;

  StateSpace::size_type state;

  for (state = 0;
       state < path.size()
	 && path[state].positiveAtoms().equal(it.path[state].positiveAtoms(),
					      path.numberOfPropositions());
       state++)
    ;

  return (state != path.size());
}

/* ========================================================================= */
void PathIterator::computeNextPath()
/* ----------------------------------------------------------------------------
 *
 * Description:   Updates the path pointed to by the PathIterator to the
 *                `next' path in the sequence. The sequence is constructed by
 *                identifying the sequence as a binary integer (obtained by
 *                concatenating the truth valuations for the atomic
 *                propositions in each state) which is then incremented. If
 *                there is an overflow, the `loop state' is changed until it
 *                exceeds the length of the path.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (atEnd())
    return;

  StateSpace::size_type state;
  const unsigned long int number_of_propositions(path.numberOfPropositions());

  /*
   *  Find the first state in the current path where some proposition has the
   *  value `false'. Change the truth value of all propositions to `false'
   *  in all states preceding this state.
   */

  for (state = 0;
       state < path.size()
	 && path[state].positiveAtoms().count(number_of_propositions)
	      == number_of_propositions;
       state++)
    path[state].positiveAtoms().clear(number_of_propositions);

  if (state == path.size())
  {
    /*
     *  If the path did not contain a state in which some proposition had the
     *  value `false', update the `loop state' in the path.
     */

    path.disconnect(path.size() - 1, loop_target_state);

    loop_target_state++;

    if (loop_target_state < path.size())
      path.connect(path.size() - 1, loop_target_state);
  }
  else
  {
    /*
     *  In other case, change the truth value of the proposition to `true' in
     *  the state and reset the truth values of all propositions with smaller
     *  identifiers to `false'.
     */

    BitArray& truth_assignment = path[state].positiveAtoms();
    unsigned long int proposition;

    for (proposition = 0; truth_assignment[proposition]; proposition++)
      truth_assignment.clearBit(proposition);

    truth_assignment.setBit(proposition);
  }
}

}
