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

#ifndef USERCOMMANDREADER_H
#define USERCOMMANDREADER_H

#ifdef __GNUC__
#pragma interface
#endif /* __GNUC__ */

#include <config.h>
#include <string>
#include <utility>
#include <vector>
#include "Alloc.h"
#include "Configuration.h"
#include "Exception.h"

using namespace std;

extern Configuration configuration;

extern bool user_break;



/******************************************************************************
 *
 * Interactive user command interface.
 *
 *****************************************************************************/

namespace UserCommandInterface
{                                                    

void executeUserCommands();                         /* Function for reading and
                                                     * executing user commands
                                                     * at the end of a test
                                                     * round.
						     */

enum TokenType                                      /* User commands. */
  {ALGORITHMS, BUCHI, BUCHIANALYZE,
   CONSISTENCYANALYSIS, CONTINUE, DISABLE, ENABLE,
   EVALUATE, FORMULA, HELP, INCONSISTENCIES, QUIT,
   RESULTANALYZE, RESULTS, SKIP, STATESPACE,
   STATISTICS, VERBOSITY, UNKNOWN, _NO_INPUT};

TokenType parseCommand(const string& token);        /* Translates a command
                                                     * name into its
                                                     * corresponding
                                                     * TokenType identifier.
						     */

void verifyArgumentCount                            /* Checks that the      */
  (const vector<string, ALLOC(string) >&            /* number of arguments  */
     arguments,                                     /* for a command is     */
   vector<string, ALLOC(string) >::size_type        /* within given limits. */
     min_arg_count,       
   vector<string, ALLOC(string) >::size_type
     max_arg_count);

pair<string, bool> parseRedirection                 /* Checks whether an  */
  (vector<string, ALLOC(string) >& input_tokens);   /* user command given
                                                     * will require
                                                     * redirecting its
						     * output to a file.
						     */

bool parseFormulaType                               /* Checks whether an     */
  (vector<string, ALLOC(string) >& input_tokens);   /* user command
						     * specified a positive
						     * or a negative
						     * formula.
						     */

void verifyNumber                                   /* Checks that a given   */
  (unsigned long int number,                        /* number is less than a */
   unsigned long int max,                           /* given maximum value.  */
   const char* error_message);



/******************************************************************************
 *
 * An exception class for reporting errors in user commands.
 *
 *****************************************************************************/

class CommandErrorException : public Exception
{
public:
  CommandErrorException                             /* Constructor. */
    (const string& message     
       = "Syntax error in command.");          

  /* default copy constructor */

  ~CommandErrorException() throw();                 /* Destructor. */

  CommandErrorException& operator=                  /* Assignment operator. */
    (const CommandErrorException& e);
};



/******************************************************************************
 *
 * Inline function definitions for class CommandErrorException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline CommandErrorException::CommandErrorException
  (const string& message) :
  Exception(message)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class CommandErrorException. Creates an
 *                exception object and initializes it with an error message.
 *
 * Arguments:     message  --  A reference to a constant string containing the
 *                             error message.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline CommandErrorException::~CommandErrorException() throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class CommandErrorException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline CommandErrorException&
CommandErrorException::operator=(const CommandErrorException& e)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class CommandErrorException. Copies
 *                the contents of an exception object to another.
 *
 * Arguments:     e  --  A reference to a constant CommandErrorException.
 *
 * Returns:       A reference to the object assigned to.
 *
 * ------------------------------------------------------------------------- */
{
  Exception::operator=(e);
  return *this;
}

}

#endif /* !USERCOMMANDREADER_H */
