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

#ifndef SPINWRAPPER_H
#define SPINWRAPPER_H

#include <config.h>
#include <string>
#include "ExternalTranslator.h"
#include "LtlFormula.h"

/******************************************************************************
 *
 * Interface class for Spin.
 *
 *****************************************************************************/

class SpinWrapper : public ExternalTranslator
{
public:
  SpinWrapper();                                    /* Constructor. */

  ~SpinWrapper();                                   /* Destructor. */

  void translateFormula                             /* Translates a formula */
    (const ::Ltl::LtlFormula& formula,              /* into a Büchi         */
     string& translated_formula);                   /* automaton.           */

  /* `formatInput' inherited from ExternalTranslator */

  string commandLine                                /* Prepares the command */
    (const string& input_filename,                  /* line for executing   */
     const string& output_filename);                /* Spin.                */

  /* `execSuccess' inherited from ExternalTranslator */

  void parseAutomaton                               /* Translates the output */
    (const string& input_filename,                  /* of the translation    */
     const string& output_filename);                /* algorithm into lbtt
						     * format.
						     */

private:
  SpinWrapper(const SpinWrapper&);                  /* Prevent copying and */
  SpinWrapper& operator=(const SpinWrapper&);       /* assignment of
						     * SpinWrapper objects.
						     */

  static const char SPIN_AND[];                     /* Symbols for */
  static const char SPIN_OR[];                      /* operators.  */
};



/******************************************************************************
 *
 * Inline function definitions for class SpinWrapper.
 *
 *****************************************************************************/

/* ========================================================================= */
inline SpinWrapper::SpinWrapper()
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class SpinWrapper.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline SpinWrapper::~SpinWrapper()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class SpinWrapper.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline string SpinWrapper::commandLine
  (const string& input_filename, const string& output_filename)
/* ----------------------------------------------------------------------------
 *
 * Description:   Prepares the command line for Spin.
 *
 * Arguments:     input_filename   --  Name of the input file.
 *                output_filename  --  Name of the output file.
 *
 * Returns:       The command line string.
 *
 * ------------------------------------------------------------------------- */
{
  return string(" -F ") + input_filename + " >" + output_filename;
}

#endif /* !SPINWRAPPER_H */
