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

#ifndef PATHEVALUATOR_H
#define PATHEVALUATOR_H

#include <iostream>
#include <map>
#include "LbttAlloc.h"
#include "BitArray.h"
#include "Exception.h"
#include "LtlFormula.h"
#include "StateSpace.h"

namespace Ltl
{

using namespace Graph;

/******************************************************************************
 *
 * A class for testing whether an LtlFormula holds in a StateSpace that
 * consists of states connected into a non-branching sequence, which ends in a
 * loop.
 *
 *****************************************************************************/

class PathEvaluator
{
public:
  PathEvaluator();                                  /* Constructor. */

  ~PathEvaluator();                                 /* Destructor. */

  void reset();                                     /* Prepares the object for
						     * a new evaluation run.
						     */

  bool evaluate                                     /* Tests whether an      */
    (const LtlFormula& formula,                     /* LtlFormula holds in a */
     const StateSpace::Path& prefix,                /* path described by a   */
     const StateSpace::Path& cycle,                 /* prefix and a cycle of */
     const StateSpace& statespace);                 /* states from a state
						     * space.
						     */

  bool evaluate                                     /* Same as above. */
    (const LtlFormula& formula,
     const StateSpace& statespace);

  bool getResult(StateSpace::size_type state)       /* Returns the result of */
    const;                                          /* the evaluation in a
						     * given state of the
						     * current path after a
						     * call to `evaluate'.
						     */

  void print                                        /* Displays the results */
    (ostream& stream = cout,                        /* after a call to      */
     const int indent = 0) const;                   /* `evaluate'.          */

private:
  PathEvaluator(const PathEvaluator&);              /* Prevent copying and */
  PathEvaluator& operator=(const PathEvaluator&);   /* assignment of
						     * PathEvaluator
						     * objects.
						     */

  bool eval();                                      /* Evaluates a formula
						     * on a path.
						     */

  void recPrint                                     /* Recursively prints */
    (Exceptional_ostream& estream,                  /* a proof or a       */
     const int indent,                              /* refutation for a   */
     const LtlFormula* f,                           /* formula.           */
     StateSpace::size_type state) const;

  const LtlFormula* current_formula;                /* LTL formula associated
						     * with the path evaluator.
						     */

  const StateSpace* current_path;                   /* State space associated
						     * with the path evaluator.
						     */

  StateSpace::size_type current_loop_state;         /* Number of the target
						     * state of the loop on
						     * the current path.
						     */

  vector<StateSpace::size_type> path_states;        /* Correspondence
                                                     * between states of the
                                                     * path and the states
						     * of the current state
						     * space.
						     */

  map<const LtlFormula*, BitArray*,                 /* Information about the */
      LtlFormula::ptr_less>                         /* truth values of the   */
    eval_info;                                      /* subformulae of the
						     * formula to be
						     * evaluated.
						     */
};



/******************************************************************************
 *
 * Inline function definitions for class PathEvaluator.
 *
 *****************************************************************************/

/* ========================================================================= */
inline PathEvaluator::PathEvaluator() :
  current_formula(static_cast<const LtlFormula*>(0)),
  current_path(static_cast<const StateSpace*>(0)),
  current_loop_state(0)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class PathEvaluator.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline PathEvaluator::~PathEvaluator()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class PathEvaluator.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  reset();
}

}

#endif /* !PATHEVALUATOR_H */
