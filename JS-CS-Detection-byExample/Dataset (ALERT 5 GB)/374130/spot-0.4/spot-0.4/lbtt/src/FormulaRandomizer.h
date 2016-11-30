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

#ifndef FORMULARANDOMIZER_H
#define FORMULARANDOMIZER_H

#include <config.h>
#include <map>
#include <vector>
#include <utility>
#include "LbttAlloc.h"
#include "LtlFormula.h"

namespace Ltl
{

/******************************************************************************
 *
 * Class for generating random LTL formulae.
 *
 *****************************************************************************/

class FormulaRandomizer
{
public:
  FormulaRandomizer();                              /* Constructor. */

  /* default copy constructor */

  ~FormulaRandomizer();                             /* Destructor. */

  /* default assignment operator */

  void reset();                                     /* Resets the formula
                                                     * generation parameters.
                                                     */

  void useSymbol                                    /* Inserts an atomic */
    (const int symbol, const int priority);         /* symbol (an atomic
                                                     * proposition or a
						     * Boolean constant)
                                                     * into the set of
                                                     * operands used for
                                                     * generating random
                                                     * formulae.
						     */

  void useShortOperator                             /* Inserts an element */
    (const int symbol, const int priority);         /* into the set of
                                                     * operators used for
                                                     * generating random
                                                     * formulae of size
                                                     * two.
                                                     */

  void useLongOperator                              /* Inserts an element */
    (const int symbol, const int priority);         /* into the set of
                                                     * operators used for
                                                     * generating random
                                                     * formulae of size
                                                     * greater than two.
                                                     */

  LtlFormula* generate();                           /* Generates a random LTL
						     * formula.
						     */

  unsigned long int numberOfFormulas() const;       /* Get the number of
						     * generated formulas since
						     * the last call to
						     * `reset'.
						     */

  const map<unsigned long int, unsigned long int>&  /* Get the numbers of    */
    propositionStatistics() const;                  /* different atomic
                                                     * propositions
                                                     * generated since the
						     * last call to `reset'.
						     */

  const map<int, unsigned long int>&                /* Get the numbers of    */
    symbolStatistics() const;                       /* different symbols
                                                     * generated since the
						     * last call to `reset'.
						     */

  unsigned long int number_of_available_variables;  /* Size of the set of
                                                     * atomic propositions used
                                                     * for generating random
                                                     * LTL formulae.
                                                     */

  unsigned long int size;                           /* Minimum size of the
                                                     * generated formulae
						     * (number of nodes in the
						     * formula parse tree).
                                                     */

  unsigned long int max_size;                       /* Maximum size of the
                                                     * generated formulae.
                                                     */

private:
  LtlFormula* recGenerate                           /* Implementation of     */
    (unsigned long int target_size);                /* the random formula
						     * generation algorithm.
						     */

  typedef pair<int, int> IntegerPair;

  vector<IntegerPair>                               /* Operand symbols and */
    propositional_symbol_priorities;                /* their priorities in
                                                     * random formulae.
                                                     */

  vector<IntegerPair> short_formula_operators;      /* Operators and their
                                                     * priorities in random
                                                     * formulae of size two.
                                                     */

  vector<IntegerPair> long_formula_operators;       /* Operators and their
                                                     * priorities in random
                                                     * formulae of size greater
                                                     * than two.
                                                     */

  unsigned long int number_of_generated_formulas;   /* Number of generated
						     * formulas since the
						     * last call to `reset'.
						     */

  map<unsigned long int, unsigned long int>         /* Number of different  */
    proposition_statistics;                         /* atomic propositions
                                                     * generated since the
                                                     * last call to `reset'
						     */

  map<int, unsigned long int> symbol_statistics;    /* Number of different
                                                     * formula symbols
                                                     * generated since the
						     * last call to `reset'.
						     */
};



/******************************************************************************
 *
 * Inline function definitions for class FormulaRandomizer.
 *
 *****************************************************************************/

/* ========================================================================= */
inline FormulaRandomizer::FormulaRandomizer()
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class FormulaRandomizer. Creates a new
 *                formula parameter information object and initializes the
 *                generation parameters with their default values.
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
inline FormulaRandomizer::~FormulaRandomizer()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class FormulaRandomizer.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline void FormulaRandomizer::useSymbol(const int symbol, const int priority)
/* ----------------------------------------------------------------------------
 *
 * Description:   Inserts an atomic symbol (an atomic proposition or a Boolean
 *                constant) into the set of symbols used for generating random
 *                LTL formulae. The symbol will then be chosen into a formula
 *                by the random formula generation algorithm with a given
 *                priority.
 *
 * Arguments:     symbol    --  A symbol type identifier (one of the LTL_
 *                              constants defined in LtlFormula.h).
 *                priority  --  Priority for the symbol.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (priority > 0)
  {
    propositional_symbol_priorities.push_back(make_pair(symbol, priority));
    propositional_symbol_priorities[0].second += priority;
  }
}

/* ========================================================================= */
inline void FormulaRandomizer::useShortOperator
  (const int symbol, const int priority)
/* ----------------------------------------------------------------------------
 *
 * Description:   Inserts a symbol into the set of operators considered when
 *                generating random LTL formulae with two nodes in their parse
 *                tree. The symbol will then be chosen into a formula by the
 *                random formula generation algorithm with a given priority.
 *
 * Arguments:     symbol    --  A symbol type identifier (one of the LTL_
 *                              constants defined in LtlFormula.h).
 *                priority  --  Priority for the symbol.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (priority > 0)
  {
    short_formula_operators.push_back(make_pair(symbol, priority));
    short_formula_operators[0].second += priority;
  }
}

/* ========================================================================= */
inline void FormulaRandomizer::useLongOperator
  (const int symbol, const int priority)
/* ----------------------------------------------------------------------------
 *
 * Description:   Inserts a symbol into the set of operators considered when
 *                generating random LTL formulae with two nodes in their parse
 *                tree. The symbol will then be chosen into a formula by the
 *                random formula generation algorithm with a given priority.
 *
 * Arguments:     symbol    --  A symbol type identifier (one of the LTL_
 *                              constants defined in LtlFormula.h).
 *                priority  --  Priority for the symbol.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (priority > 0)
  {
    long_formula_operators.push_back(make_pair(symbol, priority));
    long_formula_operators[0].second += priority;
  }
}

/* ========================================================================= */
inline unsigned long int FormulaRandomizer::numberOfFormulas() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the number of formulae generated since the last call to
 *                `reset'.
 *
 * Arguments:     None.
 *
 * Returns:       Number of formulae generated since the last call to `reset'.
 *
 * ------------------------------------------------------------------------- */
{
  return number_of_generated_formulas;
}

/* ========================================================================= */
inline const map<unsigned long int, unsigned long int>&
FormulaRandomizer::propositionStatistics() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the number of different atomic propositions generated
 *                since the last call to `reset'.
 *
 * Arguments:     None.
 *
 * Returns:       A reference to a constant mapping between proposition
 *                identifiers and their numbers.
 *
 * ------------------------------------------------------------------------- */
{
  return proposition_statistics;
}

/* ========================================================================= */
inline const map<int, unsigned long int>&
FormulaRandomizer::symbolStatistics() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the number of different formula symbols generated since
 *                the last call to `reset'.
 *
 * Arguments:     None.
 *
 * Returns:       A reference to a constant mapping between LTL formula symbols
 *                and their numbers.
 *
 * ------------------------------------------------------------------------- */
{
  return symbol_statistics;
}

}

#endif /* !FORMULARANDOMIZER_H */
