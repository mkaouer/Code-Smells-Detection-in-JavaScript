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

#ifndef PATHITERATOR_H
#define PATHITERATOR_H

#include <config.h>
#include "StateSpace.h"

using namespace std;

namespace Graph
{

/******************************************************************************
 *
 * An iterator class for systematically enumerating all state spaces consisting
 * of a single infinite path (a prefix and a loop) with a given number of
 * states and a given number of atomic propositions in each state.
 *
 *****************************************************************************/

class PathIterator
{
public:
  PathIterator                                      /* Constructor. */
    (unsigned long int propositions_per_state,
     StateSpace::size_type number_of_states);

  /* default copy constructor */

  ~PathIterator();                                  /* Destructor. */

  /* default assignment operator */

  bool operator==(const PathIterator& it)           /* Equivalence operator. */
    const;

  bool operator!=(const PathIterator& it)           /* Inequivalence operator.
						     */
    const;

  const StateSpace& operator*() const;              /* Dereferencing */
  const StateSpace* operator->() const;             /* operators.    */

  const StateSpace& operator++();                   /* Increment operators. */
  const StateSpace operator++(int);

  bool atEnd() const;                               /* Tells whether the
						     * iterator has enumerated
						     * all the state spaces in
						     * the range determined by
						     * the parameters with
						     * which the iterator was
						     * initialized.
						     */
						     
private:
  void computeNextPath();                           /* Updates the state space
						     * currently pointed to by
						     * the iterator.
						     */

  StateSpace path;                                  /* The state space (the
						     * path) currently pointed
						     * to by the iterator.
						     */

  StateSpace::size_type loop_target_state;          /* Identifier of the target
						     * state of the last state
						     * in the path.
						     */
};



/******************************************************************************
 *
 * Inline function definitions for class PathIterator.
 *
 *****************************************************************************/

/* ========================================================================= */
inline PathIterator::~PathIterator()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class PathIterator.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline const StateSpace& PathIterator::operator*() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Dereferencing operator for class PathIterator. Gives access
 *                to the state space currently pointed to by the iterator.
 *
 * Arguments:     None.
 *
 * Returns:       A constant reference to the state space currently pointed to
 *                by the iterator.
 *
 * ------------------------------------------------------------------------- */
{
  return path;
}

/* ========================================================================= */
inline const StateSpace* PathIterator::operator->() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Dereferencing operator for class PathIterator. Gives access
 *                to the state space currently pointed to by the iterator.
 *
 * Arguments:     None.
 *
 * Returns:       A pointer to a constant state space currently pointed to by
 *                the iterator.
 *
 * ------------------------------------------------------------------------- */
{
  return &path;
}

/* ========================================================================= */
inline const StateSpace& PathIterator::operator++()
/* ----------------------------------------------------------------------------
 *
 * Description:   Prefix increment operator for class PathIterator. Computes
 *                the next path in the graph sequence and returns a constant
 *                reference to it.
 *
 * Arguments:     None.
 *
 * Returns:       A constant reference to the updated state space pointer to by
 *                the iterator.
 *
 * ------------------------------------------------------------------------- */
{
  computeNextPath();
  return path;
}

/* ========================================================================= */
inline const StateSpace PathIterator::operator++(int)
/* ----------------------------------------------------------------------------
 *
 * Description:   Postfix increment operator for class PathIterator. Computes
 *                the next path in the graph sequence and returns the graph
 *                pointed to by the iterator after this operation.
 *
 * Arguments:     None.
 *
 * Returns:       A constant reference to the state space pointed to by the
 *                iterator before computing the next path in the sequence.
 *
 * ------------------------------------------------------------------------- */
{
  StateSpace old_path(path);
  computeNextPath();
  return old_path;
}

/* ========================================================================= */
inline bool PathIterator::atEnd() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells whether all possible paths have been enumerated by the
 *                iterator.
 *
 * Arguments:     None.
 *
 * Returns:       A truth value according to the result of the test.
 *
 * ------------------------------------------------------------------------- */
{
  return (loop_target_state == path.size());
}
 
}

#endif  /* !PATHITERATOR_H */
