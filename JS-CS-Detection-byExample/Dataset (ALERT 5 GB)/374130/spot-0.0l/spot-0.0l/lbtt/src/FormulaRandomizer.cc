/*
 *  Copyright (C) 1999, 2000, 2001, 2002, 2003
 *  Heikki Tauriainen <Heikki.Tauriainen@hut.fi>
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

#ifdef __GNUC__
#pragma implementation
#endif /* __GNUC__ */

#include <config.h>
#include "FormulaRandomizer.h"
#include "Random.h"

namespace Ltl
{

/******************************************************************************
 *
 * Function definitions for class FormulaRandomizer.
 *
 *****************************************************************************/

/* ========================================================================= */
void FormulaRandomizer::reset()
/* ----------------------------------------------------------------------------
 *
 * Description:   Sets the random formula generation parameters to their
 *                default values.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  number_of_available_variables = 5;
  size = 5;
  max_size = 5;
  propositional_symbol_priorities.clear();
  short_formula_operators.clear();
  long_formula_operators.clear();
  propositional_symbol_priorities.push_back(make_pair(-1, 0));
  short_formula_operators.push_back(make_pair(-1, 0));
  long_formula_operators.push_back(make_pair(-1, 0));
  number_of_generated_formulas = 0;
  proposition_statistics.clear();
  symbol_statistics.clear();
}

/* ========================================================================= */
LtlFormula* FormulaRandomizer::generate()
/* ----------------------------------------------------------------------------
 *
 * Description:   Generates a random (newly allocated) LtlFormula using the
 *                parameters stored in `this' object.
 *
 * Arguments:     None.
 *
 * Returns:       A pointer to the generated formula.
 *
 * ------------------------------------------------------------------------- */
{
  unsigned long int target_size(size);

  if (max_size > size)
    target_size += LRAND(0, max_size - size + 1);

  number_of_generated_formulas++;
  return recGenerate(target_size);
}

/* ========================================================================= */
LtlFormula* FormulaRandomizer::recGenerate(unsigned long int target_size)
/* ----------------------------------------------------------------------------
 *
 * Description:   Implementation of the recursive random formula generation
 *                algorithm.
 *
 * Arguments:     target_size  --  Size of the formula to be generated.
 *
 * Returns:       A pointer to the generated formula.
 *
 * ------------------------------------------------------------------------- */
{
  vector<IntegerPair, ALLOC(IntegerPair) >::const_iterator symbol_priority;
  LtlFormula* formula;
  long int x;

  /*
   *  Select a list of allowable symbols according to the target size. If the
   *  size is 1, only atomic propositions and Boolean constants are allowed
   *  If the size is 2, only unary operators are allowed. Otherwise select the
   *  symbols from the set of unary and binary operators.
   */

  switch (target_size)
  {
    case 1 :
      x = LRAND(0, propositional_symbol_priorities[0].second);
      symbol_priority = propositional_symbol_priorities.begin();
      break;

    case 2 :
      x = LRAND(0, short_formula_operators[0].second);
      symbol_priority = short_formula_operators.begin();
      break;

    default :
      x = LRAND(0, long_formula_operators[0].second);
      symbol_priority = long_formula_operators.begin();
      break;
  }

  /*
   *  Using the selected list, choose a random symbol in the list using the
   *  priority distribution of the different symbols in the list. The list
   *  consists of <symbol, priority> pairs. The first element of the list does
   *  not correspond to any symbol, however; instead, it gives the sum of the
   *  priorities of the symbols in the list.
   */

  ++symbol_priority;
  while (x >= symbol_priority->second)
  {
    x -= symbol_priority->second;
    ++symbol_priority;
  }

  symbol_statistics[symbol_priority->first]++;

  switch (symbol_priority->first)
  {
    case LTL_ATOM :
      {
	unsigned long int atom = LRAND(0, number_of_available_variables);
	proposition_statistics[atom]++;
	formula = &Atom::construct(atom);
	break;
      }

    case LTL_TRUE :
      formula = &True::construct();
      break;

    case LTL_FALSE :
      formula = &False::construct();
      break;

    case LTL_NEGATION :
      formula = &Not::construct(recGenerate(target_size - 1));
      break;

    case LTL_NEXT :
      formula = &Next::construct(recGenerate(target_size - 1));
      break;

    case LTL_FINALLY :
      formula = &Finally::construct(recGenerate(target_size - 1));
      break;

    case LTL_GLOBALLY :
      formula = &Globally::construct(recGenerate(target_size - 1));
      break;

    default :
      {
	unsigned long int s = LRAND(1, target_size - 1);
	LtlFormula* g = recGenerate(s);
	LtlFormula* h = recGenerate(target_size - s - 1);

	switch (symbol_priority->first)
	{
	  case LTL_CONJUNCTION :
	    formula = &And::construct(g, h);
	    break;

	  case LTL_DISJUNCTION :
	    formula = &Or::construct(g, h);
	    break;

	  case LTL_IMPLICATION :
	    formula = &Imply::construct(g, h);
	    break;

	  case LTL_EQUIVALENCE :
	    formula = &Equiv::construct(g, h);
	    break;

	  case LTL_XOR :
	    formula = &Xor::construct(g, h);
	    break;

	  case LTL_UNTIL :
	    formula = &Until::construct(g, h);
	    break;

	  case LTL_V :
	    formula = &V::construct(g, h);
	    break;

	  case LTL_WEAK_UNTIL :
	    formula = &WeakUntil::construct(g, h);
	    break;

	  case LTL_STRONG_RELEASE :
	    formula = &StrongRelease::construct(g, h);
	    break;

	  default : /* LTL_BEFORE */
	    formula = &Before::construct(g, h);
	    break;
	}
      }
  }

  return formula;
}

}
