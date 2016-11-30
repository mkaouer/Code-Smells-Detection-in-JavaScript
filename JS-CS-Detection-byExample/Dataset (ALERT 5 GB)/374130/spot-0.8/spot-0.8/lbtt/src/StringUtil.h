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

#ifndef STRINGUTIL_H
#define STRINGUTIL_H

#include <config.h>
#include <set>
#include <string>
#ifdef HAVE_SSTREAM
#include <sstream>
#else
#include <strstream>
#endif /* HAVE_SSTREAM */
#include <vector>
#include "LbttAlloc.h"
#include "Exception.h"
#include "IntervalList.h"

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
   vector<string>& slices);                         /* given set of
						     * characters as
                                                     * separators.
						     */

string toLowerCase(const string& s);                /* Converts a string to
						     * lower case.
						     */

string unquoteString(const string& s);              /* Removes unescaped quotes
                                                     * from a string and
                                                     * interprets escaped
						     * characters.
						     */

enum QuoteMode {GLOBAL, INSIDE_QUOTES,              /* Enumeration type used */
                OUTSIDE_QUOTES};                    /* for controlling the
						     * behavior of the
						     * substitute function.
						     */

string::size_type findInQuotedString                /* Finds a character in */
  (const string& s, const string& chars,            /* a string (respecting */
   QuoteMode type = GLOBAL);                        /* quotes).             */

string substituteInQuotedString                     /* Replaces characters */
  (const string& s, const string& chars,            /* in a string with    */
   const string& substitutions,                     /* other characters.   */
   QuoteMode type = GLOBAL);

unsigned long int parseNumber                       /* Converts a string to  */
  (const string& number_string);                    /* an unsigned long
                                                     * integer.
						     */

 enum IntervalStringType {UNBOUNDED = 0,            /* Type for an interval */
			  LEFT_BOUNDED = 1,         /* string (see the      */
			  RIGHT_BOUNDED = 2};       /* documentation of
						     * the parseInterval
						     * function in
						     * StringUtil.cc).
						     */

int parseInterval                                   /* Reads the lower and   */
  (const string& token,                             /* upper bounds from a   */
   unsigned long int& min,                          /* "number interval      */
   unsigned long int& max);                         /* string" into two
                                                     * unsigned long integer
						     * variables.
						     */

void parseIntervalList                              /* Converts a list of   */
  (const string& token,                             /* number intervals to  */
   IntervalList& intervals,                         /* the set of unsigned  */
   unsigned long int min,                           /* long integers        */
   unsigned long int max,                           /* corresponding to the */
   vector<string>* extra_tokens = 0);               /* union of the
                                                     * intervals.
						     */

void parseTime                                      /* Parses a time string. */
  (const string& time_string,
   unsigned long int& hours,
   unsigned long int& minutes,
   unsigned long int& seconds);



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
 * A class for reporting "out of range" errors for numbers when parsing
 * intervals.
 *
 *****************************************************************************/

class IntervalRangeException : public Exception
{
public:
  IntervalRangeException                            /* Constructor. */
    (const unsigned long int number,
     const string& message = "number out of range");

  /* default copy constructor */

  ~IntervalRangeException() throw();                /* Destructor. */

  IntervalRangeException& operator=                 /* Assignment operator. */
    (const IntervalRangeException& e);

  unsigned long int getNumber() const;              /* Returns the number
						     * associated with the
						     * exception object.
						     */

private:
  const unsigned long int invalid_number;
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



/******************************************************************************
 *
 * Inline function definitions for class IntervalRangeException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline IntervalRangeException::IntervalRangeException  
  (const unsigned long int number, const string& message) :
  Exception(message), invalid_number(number)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class IntervalRangeException. Creates an  
 *                exception object.
 *
 * Arguments:     number   --  A constant unsigned long integer specifying a
 *                             number that does not fit in an interval.
 *                message  --  A reference to a constant string containing an
 *                             error message.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline IntervalRangeException::~IntervalRangeException() throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class IntervalRangeException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline unsigned long int IntervalRangeException::getNumber() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the number associated with the IntervalRangeException
 *                object.
 *
 * Arguments:     None.
 *
 * Returns:       The number associated with the object.
 *
 * ------------------------------------------------------------------------- */
{
  return invalid_number;
}

/* ========================================================================= */
inline IntervalRangeException& IntervalRangeException::operator=
  (const IntervalRangeException& e)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class IntervalRangeException.
 *                Copies the contents of an exception object to another.
 *
 * Arguments:     e  --  A reference to a constant IntervalRangeException.
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
