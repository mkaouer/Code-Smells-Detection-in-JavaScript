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
#ifdef HAVE_SSTREAM
#include <sstream>
#else
#include <strstream>
#endif /* HAVE_SSTREAM */
#include "Exception.h"
#include "FormulaWriter.h"
#include "NeverClaimAutomaton.h"
#include "SpinWrapper.h"

/******************************************************************************
 *
 * Definitions for operator symbols specific to Spin.
 *
 *****************************************************************************/

const char SpinWrapper::SPIN_AND[]     = "&&";
const char SpinWrapper::SPIN_OR[]      = "||";



/******************************************************************************
 *
 * Function definitions for class SpinWrapper.
 *
 *****************************************************************************/

/* ========================================================================= */
void SpinWrapper::translateFormula
  (const ::Ltl::LtlFormula& formula, string& translated_formula)
/* ----------------------------------------------------------------------------
 *
 * Description:   Translates an LtlFormula into a string which contains the
 *                formula in the input syntax of Spin.
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
                BinaryOperatorInfixWriter<SPIN_AND>,
                BinaryOperatorInfixWriter<SPIN_OR>,
                BinaryOperatorInfixWriter<LtlImplication::infix_symbol>,
                BinaryOperatorInfixWriter<LtlEquivalence::infix_symbol>,
                WriterErrorReporter,
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

/* ========================================================================= */
void SpinWrapper::parseAutomaton
  (const string& input_filename, const string& output_filename)
/* ----------------------------------------------------------------------------
 *
 * Description:   Parses the never claim in the output returned by Spin and
 *                converts it into lbtt format.
 *
 * Arguments:     input_filename   --  Name of the input file.
 *                output_filename  --  Name of the output file.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  NeverClaimAutomaton automaton;
  automaton.read(input_filename.c_str());
  automaton.write(output_filename.c_str());
}
