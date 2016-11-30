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

#ifndef TRANSLATORINTERFACE_H
#define TRANSLATORINTERFACE_H

#include <config.h>
#include "LtlFormula.h"

/******************************************************************************
 *
 * General interface class for an LTL-to-Büchi translator.  The interface
 * provides an abstract member function `translate' taking an LtlFormula
 * and an output file name as arguments.  The purpose of an actual
 * implementation of the function is to translate the given LtlFormula into
 * an automaton and store the results in the given file.
 *
 *****************************************************************************/

class TranslatorInterface
{
public:
  TranslatorInterface();                            /* Constructor. */

  virtual ~TranslatorInterface() = 0;               /* Destructor. */

  virtual void translate                            /* Interface for a */
    (const ::Ltl::LtlFormula& formula,              /* translation     */
     const char* filename) = 0;                     /* algorithm.      */

private:
  TranslatorInterface(const TranslatorInterface&);  /* Prevent copying and */
  TranslatorInterface& operator=                    /* assignment of       */
    (const TranslatorInterface&);                   /* TranslatorInterface
						     * objects.
						     */
};



/******************************************************************************
 *
 * Inline function definitions for class TranslatorInterface.
 *
 *****************************************************************************/

/* ========================================================================= */
inline TranslatorInterface::TranslatorInterface()
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class TranslatorInterface.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline TranslatorInterface::~TranslatorInterface()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class TranslatorInterface.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

#endif /* !TRANSLATORINTERFACE_H */
