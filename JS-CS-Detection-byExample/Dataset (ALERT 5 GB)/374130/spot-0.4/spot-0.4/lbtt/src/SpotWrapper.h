/*
 *  Copyright (C) 2003, 2004
 *  Heikki Tauriainen <Heikki.Tauriainen@tkk.fi>
 *
 *  Derived from SpinWrapper.h by Alexandre Duret-Lutz <adl@src.lip6.fr>.
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

#ifndef SPOTWRAPPER_H
#define SPOTWRAPPER_H

#include <config.h>
#include <string>
#include "ExternalTranslator.h"
#include "LtlFormula.h"

/******************************************************************************
 *
 * Interface class for Spot.
 *
 *****************************************************************************/

class SpotWrapper : public ExternalTranslator
{
public:
  SpotWrapper();                                    /* Constructor. */

  ~SpotWrapper();                                   /* Destructor. */

  void translateFormula                             /* Translates a formula */
    (const ::Ltl::LtlFormula& formula,              /* into a Büchi         */
     string& translated_formula);                   /* automaton.           */

  /* `formatInput' inherited from ExternalTranslator */

  string commandLine                                /* Prepares the command */
    (const string& input_filename,                  /* line for executing   */
     const string& output_filename);                /* Spot.                */

  /* `execSuccess' inherited from ExternalTranslator */

  void parseAutomaton                               /* Translates the output */
    (const string& input_filename,                  /* of the translation    */
     const string& output_filename);                /* algorithm into lbtt
						     * format.
						     */

private:
  SpotWrapper(const SpotWrapper&);                  /* Prevent copying and */
  SpotWrapper& operator=(const SpotWrapper&);       /* assignment of
						     * SpotWrapper objects.
						     */

  static const char SPOT_AND[];                     /* Symbols for */
  static const char SPOT_OR[];                      /* operators.  */
  static const char SPOT_XOR[];
};



/******************************************************************************
 *
 * Inline function definitions for class SpotWrapper.
 *
 *****************************************************************************/

/* ========================================================================= */
inline SpotWrapper::SpotWrapper()
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class SpotWrapper.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline SpotWrapper::~SpotWrapper()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class SpotWrapper.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline string SpotWrapper::commandLine
  (const string& input_filename, const string&)
/* ----------------------------------------------------------------------------
 *
 * Description:   Prepares the command line for Spot.
 *
 * Arguments:     input_filename   --  Name of the input file.
 *                The other argument is only needed for supporting the
 *                ExternalTranslator interface; the output will be written to
 *                the filename stored in `command_line_arguments[4]'.
 *
 * Returns:       The command line string.
 *
 * ------------------------------------------------------------------------- */
{
  return (string(" ") + input_filename
	  + " >" + string(command_line_arguments[4]));
}

/* ========================================================================= */
inline void SpotWrapper::parseAutomaton(const string&, const string&)
/* ----------------------------------------------------------------------------
 *
 * Description:   Dummy function which is needed to support the
 *                ExternalTranslator interface.
 *
 * Arguments:     References to two constant strings.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

#endif /* !SPOTWRAPPER_H */
