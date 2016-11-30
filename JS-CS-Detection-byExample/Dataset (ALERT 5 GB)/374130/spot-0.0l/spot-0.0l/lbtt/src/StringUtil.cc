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
#include <cstdlib>
#include "StringUtil.h"

namespace StringUtil
{

/* ========================================================================= */
string toString(const double d, const int precision, const ios::fmtflags flags)
/* ----------------------------------------------------------------------------
 *
 * Description:   Converts a double to a string with a given precision and
 *                format. The function defaults to fixed-point format with a
 *                precision of two decimals.
 *
 * Arguments:     d          --  The double to be converted.
 *                precision  --  Precision.
 *                flags      --  Formatting flags.
 *
 * Returns:       The double as a string.
 *
 * ------------------------------------------------------------------------- */
{
#ifdef HAVE_SSTREAM
  ostringstream stream;
  stream.precision(precision);
  stream.flags(flags);
  stream << d;
  return stream.str();
#else
  ostrstream stream;
  stream.precision(precision);
  stream.flags(flags);
  stream << d << ends;
  string result(stream.str());
  stream.freeze(0);
  return result;
#endif /* HAVE_SSTREAM */
}

/* ========================================================================= */
void sliceString
  (const string& s, const char* slice_chars,
   vector<string, ALLOC(string) >& slices)
/* ----------------------------------------------------------------------------
 *
 * Description:   Slices a string into a vector of strings, using a given set
 *                of characters as separators.
 *
 * Arguments:     s            --  A reference to the constant original string.
 *                slice_chars  --  A C-style string containing the characters
 *                                 to be used as separators.
 *                slices       --  A reference to a vector for storing the
 *                                 string components.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  string::size_type last_non_slicechar_pos = 0;
  string::size_type last_slicechar_pos = 0;

  slices.clear();

  do
  {
    last_non_slicechar_pos = 
      s.find_first_not_of(slice_chars, last_slicechar_pos);
    if (last_non_slicechar_pos != s.npos)
    {
      last_slicechar_pos = s.find_first_of(slice_chars,
                                           last_non_slicechar_pos);
      if (last_slicechar_pos == s.npos)
        slices.push_back(s.substr(last_non_slicechar_pos));
      else
        slices.push_back(s.substr(last_non_slicechar_pos,
                                  last_slicechar_pos 
                                  - last_non_slicechar_pos));
    }
  }
  while (last_non_slicechar_pos != s.npos && last_slicechar_pos != s.npos);
}

/* ========================================================================= */
unsigned long int parseNumber(const string& number_string)
/* ----------------------------------------------------------------------------
 *
 * Description:   Converts a string to an unsigned long integer.
 *
 * Argument:      number_string  --  A reference to a constant string.
 *
 * Returns:       The number contained in the string, unless the string could
 *                not be converted to a number, in which case an exception is
 *                thrown.
 *
 * ------------------------------------------------------------------------- */
{
  char* endptr;
  unsigned long int number = strtoul(number_string.c_str(), &endptr, 10);
                
  if (*endptr != '\0')
    throw NotANumberException("expected a nonnegative integer, got `"
                              + number_string + "'");

  return number;
}

/* ========================================================================= */
void parseInterval
  (const string& token,
   set<unsigned long int, less<unsigned long int>, ALLOC(unsigned long int) >& 
     number_set,
   unsigned long int min, unsigned long int max)
/* ----------------------------------------------------------------------------
 *
 * Description:   Parses a string for a list of number intervals, storing all
 *                the numbers into a set.
 *
 * Arguments:     token       --  A reference to a constant string containing
 *                                the list of intervals. A legal list of 
 *                                intervals has the following syntax:
 *
 *                                  <interval_list>
 *                                     ::=   <number>
 *                                         | '*'          // all numbers in the
 *                                                        //   interval
 *                                                        //   [min, max]
 *                                         | '-'<number>  // all numbers in the
 *                                                        //   interval
 *                                                        //   [min, number]
 *                                         | <number>'-'  // all numbers in the
 *                                                        //   interval
 *                                                        //   [number, max]
 *                                         | <number>'-'<number>
 *                                         | <interval_list>','<interval_list>
 *                            
 *                number_set  --  A reference to a set of unsigned long
 *                                integers for storing the result.
 *                min         --  Minimum bound for the numbers.
 *                max         --  Maximum bound for the numbers.
 *
 *                Note: `min' and `max' are only used to determine the limits
 *                      for only partially defined intervals. The check that
 *                      the explicitly specified values in the interval list
 *                      are within these bounds is left to the caller when
 *                      examining the result set.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  vector<string, ALLOC(string) > intervals;

  sliceString(token, ",", intervals);
  string::size_type dash_pos;

  number_set.clear();

  for (vector<string, ALLOC(string) >::const_iterator
	 interval = intervals.begin();
       interval != intervals.end();
       ++interval)
  {
    if (*interval == "*")
    {
      for (unsigned long int i = min; i <= max; i++)
        number_set.insert(i);
      break;
    }

    dash_pos = (*interval).find_first_of("-");

    if (dash_pos == (*interval).npos)
      number_set.insert(parseNumber(*interval));
    else
    {
      if (dash_pos == 0)
      {
        unsigned long int number = parseNumber((*interval).substr(1));
        for (unsigned long int i = min; i <= number; i++)
          number_set.insert(i);
      }
      else if (dash_pos == (*interval).length() - 1)
      {
        unsigned long int number =
          parseNumber((*interval).substr(0, (*interval).length() - 1));
        for (unsigned long int i = number; i <= max; i++)
          number_set.insert(i);
      }
      else
      {
        unsigned long int min_bound =
          parseNumber((*interval).substr(0, dash_pos));
        unsigned long int max_bound =
          parseNumber((*interval).substr(dash_pos + 1));

        for (unsigned long int i = min_bound; i <= max_bound; i++)
          number_set.insert(i);
      }
    }
  }
}

}
