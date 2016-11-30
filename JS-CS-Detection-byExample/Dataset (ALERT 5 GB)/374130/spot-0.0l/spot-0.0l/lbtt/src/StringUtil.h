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

#ifndef STRINGUTIL_H
#define STRINGUTIL_H

#ifdef __GNUC__
#pragma interface
#endif /* __GNUC__ */

#include <config.h>
#include <set>
#include <string>
#ifdef HAVE_SSTREAM
#include <sstream>
#else
#include <strstream>
#endif /* HAVE_SSTREAM */
#include <vector>
#include "Alloc.h"
#include "Exception.h"

using namespace std;

/******************************************************************************
 *
 * Miscellaneous routines extracting data from strings or converting data types
 * to strings.
 *
 *****************************************************************************/

namespace StringUtil
{

string toString                                     /* Function for        */
  (const double d,                                  /* converting a double */
   const int precision = 2,                         /* to a string.        */
   const ios::fmtflags flags = ios::fixed);

template<typename T> string toString(const T& t);   /* Template function for
						     * converting data types
						     * supporting stream output
						     * operations into strings.
						     */

void sliceString                                    /* Breaks a string into */
  (const string& s, const char* slice_chars,        /* `slices', using a    */
   vector<string, ALLOC(string) >& slices);         /* given set of
						     * characters as
                                                     * separators.
						     */

unsigned long int parseNumber                       /* Converts a string to  */
  (const string& number_string);                    /* an unsigned long
                                                     * integer.
						     */

void parseInterval                                  /* Converts a string of  */
  (const string& token,                             /* number intervals to   */
   set<unsigned long int, less<unsigned long int>,  /* the corresponding set */
       ALLOC(unsigned long int) >& number_set,      /* of unsigned long      */
   unsigned long int min,                           /* integers.             */
   unsigned long int max);



/******************************************************************************
 *
 * Template function definitions in namespace StringUtil.
 *
 *****************************************************************************/

/* ========================================================================= */
template<typename T>
string toString(const T& t)
/* ----------------------------------------------------------------------------
 *
 * Description:   Converts any data type supporting stream output (the <<
 *                operator) into a string.
 *
 * Arguments:     t  --  The object to be converted into a string.
 *
 * Returns:       The object as a string.
 *
 * ------------------------------------------------------------------------- */
{
#ifdef HAVE_SSTREAM
  ostringstream stream;
  stream << t;
  return stream.str();
#else
  ostrstream stream;
  stream << t << ends;
  string result(stream.str());
  stream.freeze(0);
  return result;
#endif /* HAVE_SSTREAM */
}



/******************************************************************************
 *
 * A class for reporting number conversion errors.
 *
 *****************************************************************************/

class NotANumberException : public Exception
{
public:
  NotANumberException                               /* Constructor. */
    (const string& message = "not a number");

  /* default copy constructor */

  ~NotANumberException() throw();                   /* Destructor. */

  NotANumberException& operator=                    /* Assignment operator. */
    (const NotANumberException& e);
};



/******************************************************************************
 *
 * Inline function definitions for class NotANumberException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline NotANumberException::NotANumberException  
  (const string& message) :
  Exception(message)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class NotANumberException. Creates an  
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
inline NotANumberException::~NotANumberException() throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class NotANumberException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline NotANumberException& NotANumberException::operator=
  (const NotANumberException& e)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class NotANumberException. Copies
 *                the contents of an exception object to another.
 *
 * Arguments:     e  --  A reference to a constant NotANumberException.
 *
 * Returns:       A reference to the object assigned to.
 *
 * ------------------------------------------------------------------------- */
{
  Exception::operator=(e);
  return *this;
}

}

#endif /* !STRINGUTIL_H */
