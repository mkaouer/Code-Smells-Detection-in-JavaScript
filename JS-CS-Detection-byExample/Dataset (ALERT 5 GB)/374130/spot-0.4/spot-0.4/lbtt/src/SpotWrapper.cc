/*
 *  Copyright (C) 2003, 2004
 *  Heikki Tauriainen <Heikki.Tauriainen@tkk.fi>
 *
 *  Derived from SpinWrapper.cc by Alexandre Duret-Lutz <adl@src.lip6.fr>.
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
#ifdef HAVE_SSTREAM
#include <sstream>
#else
#include <strstream>
#endif /* HAVE_SSTREAM */
#include "Exception.h"
#include "FormulaWriter.h"
#include "NeverClaimAutomaton.h"
#include "SpotWrapper.h"

/******************************************************************************
 *
 * Definitions for operator symbols specific to Spot.
 *
 *****************************************************************************/

const char SpotWrapper::SPOT_AND[]     = "&&";
const char SpotWrapper::SPOT_OR[]      = "||";
const char SpotWrapper::SPOT_XOR[]     = "^";



/******************************************************************************
 *
 * Function definitions for class SpotWrapper.
 *
 *****************************************************************************/

/* ========================================================================= */
void SpotWrapper::translateFormula
  (const ::Ltl::LtlFormula& formula, string& translated_formula)
/* ----------------------------------------------------------------------------
 *
 * Description:   Translates an LtlFormula into a string which contains the
 *                formula in the input syntax of Spot.
 *
 * Arguments:     formula             --  The LtlFormula to be translated.
 *                translated_formula  --  A reference to a string for storing
 *                                        the results.
 *
 * Returns:       Nothing.  The result of the translation can be found in
 *                the string `translated_formula'.
 *
 * ------------------------------------------------------------------------- */
{
  using namespace Ltl;

#ifdef HAVE_SSTREAM
  ostringstream translated_formula_stream;
#else
  ostrstream translated_formula_stream;
#endif /* HAVE_SSTREAM */
  Exceptional_ostream estream(&translated_formula_stream, ios::goodbit);

  FormulaWriter<ConstantWriter<LtlTrue::infix_symbol>,
		ConstantWriter<LtlFalse::infix_symbol>,
		AtomWriter,
		UnaryOperatorWriter<LtlNegation::infix_symbol>,
		UnaryOperatorWriter<LtlNext::infix_symbol>,
		UnaryOperatorWriter<LtlFinally::infix_symbol>,
		UnaryOperatorWriter<LtlGlobally::infix_symbol>,
		BinaryOperatorInfixWriter<SPOT_AND>,
		BinaryOperatorInfixWriter<SPOT_OR>,
		BinaryOperatorInfixWriter<LtlImplication::infix_symbol>,
		BinaryOperatorInfixWriter<LtlEquivalence::infix_symbol>,
		BinaryOperatorInfixWriter<SPOT_XOR>,
		BinaryOperatorInfixWriter<LtlUntil::infix_symbol>,
		BinaryOperatorInfixWriter<LtlV::infix_symbol>,
		WriterErrorReporter,
		WriterErrorReporter,
		WriterErrorReporter>
    fw(estream);

  formula.traverse(fw, LTL_PREORDER | LTL_INORDER | LTL_POSTORDER);

  translated_formula = translated_formula_stream.str();
#ifndef HAVE_SSTREAM
  translated_formula_stream.freeze(0);
#endif /* HAVE_SSTREAM */
}
