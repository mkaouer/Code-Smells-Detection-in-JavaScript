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

#ifndef LBTWRAPPER_H
#define LBTWRAPPER_H

#include <config.h>
#include <string>
#include "ExternalTranslator.h"
#include "translate.h"

/******************************************************************************
 *
 * Interface class for lbt.
 *
 *****************************************************************************/

class LbtWrapper : public ExternalTranslator
{
public:
  LbtWrapper();                                     /* Constructor. */

  ~LbtWrapper();                                    /* Destructor. */

  /* `translateFormula' inherited from ExternalTranslator */

  /* `formatInput' inherited from ExternalTranslator */

  string commandLine                                /* Prepares the command */
    (const string& input_filename,                  /* line for executing   */
     const string&);                                /* lbt.                 */

  /* `execSuccess' inherited from ExternalTranslator */

  void parseAutomaton                               /* Dummy function,       */
    (const string&, const string&);                 /* needed to support the
                                                     * ExternalTranslator
						     * interface.
						     */

private:
  LbtWrapper(const LbtWrapper&);                    /* Prevent copying and */
  LbtWrapper& operator=(const LbtWrapper&);         /* assignment of
						     * LbtWrapper objects.
						     */
};



/******************************************************************************
 *
 * Inline function definitions for class LbtWrapper.
 *
 *****************************************************************************/

/* ========================================================================= */
inline LbtWrapper::LbtWrapper()
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class LbtWrapper.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline LbtWrapper::~LbtWrapper()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class LbtWrapper.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline string LbtWrapper::commandLine
  (const string& input_filename, const string&)
/* ----------------------------------------------------------------------------
 *
 * Description:   Prepares the command line for lbt.
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
  return string(" <") + input_filename + " >"
         + string(command_line_arguments[4]);
}

/* ========================================================================= */
inline void LbtWrapper::parseAutomaton(const string&, const string&)
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

#endif /* !LBTWRAPPER_H */
