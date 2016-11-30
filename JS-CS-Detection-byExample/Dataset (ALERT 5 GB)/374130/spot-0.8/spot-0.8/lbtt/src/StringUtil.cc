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
#include <cctype>
#include <climits>
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
  (const string& s, const char* slice_chars, vector<string>& slices)
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
string toLowerCase(const string& s)
/* ----------------------------------------------------------------------------
 *
 * Description:   Converts a string to lower case.
 *
 * Argument:      s  --  String to process.
 *
 * Returns:       The string in lower case.
 *
 * ------------------------------------------------------------------------- */
{
  string result;
  for (string::size_type pos = 0; pos < s.length(); ++pos)
    result += tolower(s[pos]);
  return result;
}

/* ========================================================================= */
bool interpretSpecialCharacters(const char c, bool& escape, char& quotechar)
/* ----------------------------------------------------------------------------
 *
 * Description:   Updates the values of `escape' and `quotechar' based on their
 *                original values and the value of `c'.  Used for scanning
 *                through a string possibly containing quotes and escaped
 *                characters.
 *
 * Arguments:     c          --  A character.
 *                escape     --  A truth value telling whether `c' was escaped.
 *                quotechar  --   0  ==  `c' was read outside of quotes.
 *                               `'' ==  `c' was read inside single quotes.
 *                               `"' ==  `c' was read inside double quotes.
 *
 * Returns:       True if `c' had a special meaning (for example, if it was a
 *                begin/end quote character) in the state determined by the
 *                original values of `escape' and `quotechar'.
 *
 * ------------------------------------------------------------------------- */
{
  if (escape)
  {
    escape = false;
    return false;
  }

  switch (c)
  {
    case '\\' :
      if (quotechar != '\'')
      {
	escape = true;
	return true;
      }
      break;

    case '\'' : case '"' :
      if (quotechar == 0)
      {
	quotechar = c;
	return true;
      }
      else if (c == quotechar)
      {
	quotechar = 0;
	return true;
      }
      break;

    default :
      break;
  }

  return false;
}

/* ========================================================================= */
string unquoteString(const string& s)
/* ----------------------------------------------------------------------------
 *
 * Description:   Removes (unescaped) single and double quotes and escape
 *                characters from a string.
 *
 * Argument:      s  --  String to process.
 *
 * Returns:       A string with the quotes and escape characters removed.
 *
 * ------------------------------------------------------------------------- */
{
  string result;
  char quotechar = 0;
  bool escape = false;

  for (string::size_type pos = 0; pos < s.size(); ++pos)
  {
    if (!interpretSpecialCharacters(s[pos], escape, quotechar))
      result += s[pos];
  }

  return result;
}

/* ========================================================================= */
string::size_type findInQuotedString
  (const string& s, const string& chars, QuoteMode type)
/* ----------------------------------------------------------------------------
 *
 * Description:   Finds a character in a string (respecting quotes).
 *
 * Arguments:     s      --  String to process.
 *                chars  --  A sting of characters to be searched in `s'.
 *                type   --  The extent of the search.
 *                             GLOBAL         - Apply the search to the entire
 *                                              string.
 *                             INSIDE_QUOTES  - Restrict the search to
 *                                              unescaped characters between
 *                                              quotes.
 *                             OUTSIDE_QUOTES - Restrict the search to
 *                                              unescaped characters outside
 *                                              quotes.
 *
 * Returns:       If `s' contains one of the characters in `chars' in a part
 *                of the string that matches `type', the position of the
 *                character in `s', and string::npos otherwise.
 *                
 * ------------------------------------------------------------------------- */
{
  char quotechar = 0;
  bool escape = false;

  for (string::size_type pos = 0; pos < s.size(); ++pos)
  {
    if ((type == GLOBAL || (!escape &&
			    ((type == INSIDE_QUOTES && quotechar != 0)
			     || (type == OUTSIDE_QUOTES && quotechar == 0))))
	&& chars.find_first_of(s[pos]) != string::npos)
      return pos;

    interpretSpecialCharacters(s[pos], escape, quotechar);
  }

  return string::npos;
}

/* ========================================================================= */
string substituteInQuotedString
  (const string& s, const string& chars, const string& substitutions,
   QuoteMode type)
/* ----------------------------------------------------------------------------
 *
 * Description:   Substitutes characters in a string with other characters.
 *
 * Arguments:     s              --  String to process.
 *                chars          --  A string of characters, each of which
 *                                   should be substituted in `s' with the
 *                                   character at the corresponding
 *                                   position of the string `substitutions'.
 *                substitutions  --  Characters to substitute. The length of
 *                                   this string should equal the length of
 *                                   `chars'.
 *                type           --  The extent of substitution.
 *                                     GLOBAL         - Apply the substitutions
 *                                                      globally (the default).
 *                                     INSIDE_QUOTES  - Apply the substitutions
 *                                                      to unescaped characters
 *                                                      only inside quotes that
 *                                                      have not been escaped
 *                                                      with a backslash.
 *                                     OUTSIDE_QUOTES - Apply the substitutions
 *                                                      to unescaped characters
 *                                                      only outside quotes
 *                                                      that have not been
 *                                                      escaped with a
 *                                                      backslash.
 *                                   It is not recommended to substitute the
 *                                   special characters ', " and \ with other
 *                                   characters if they have special meaning in
 *                                   `s'.
 *
 * Returns:       A string with the substitutions.
 *
 * ------------------------------------------------------------------------- */
{
  string result;
  char quotechar = 0;
  bool escape = false;

  for (string::size_type pos = 0; pos < s.size(); ++pos)
  {
    char c = s[pos];
    if (type == GLOBAL || (!escape &&
			   ((type == INSIDE_QUOTES && quotechar != 0)
			    || (type == OUTSIDE_QUOTES && quotechar == 0))))
    {
      string::size_type subst_pos = chars.find_first_of(c);
      if (subst_pos != string::npos)
	c = substitutions[subst_pos];
    }
    result += c;

    interpretSpecialCharacters(s[pos], escape, quotechar);
  }

  return result;
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
                
  if (*endptr != '\0' || number_string.empty()
      || number_string.find_first_of("-") != string::npos)
    throw NotANumberException("expected a nonnegative integer, got `"
                              + number_string + "'");

  return number;
}

/* ========================================================================= */
int parseInterval
  (const string& token, unsigned long int& min, unsigned long int& max)
/* ----------------------------------------------------------------------------
 *
 * Description:   Reads the lower and upper bound from an "interval string"
 *                into two unsigned long integer variables.
 *
 * Arguments:     token     --  A reference to a constant "interval string" of
 *                              the format
 *                                  <interval string>
 *                                    ::=  "*"                  // 0
 *                                      |  <ulong>              // 1
 *                                      |  <sep><ulong>         // 2
 *                                      |  <ulong><sep>         // 3
 *                                      |  <ulong><sep><ulong>  // 4
 *                              where <ulong> is an unsigned long integer (not
 *                              containing a minus sign), and <sep> is either
 *                              "-" or "...". The meaning of the various cases
 *                              is as follows:
 *                                  0  All integers between 0 and ULONG_MAX.
 *                                  1  A point interval consisting of a single
 *                                     value.
 *                                  2  An interval from 0 to a given upper
 *                                     bound.
 *                                  3  An interval from a given lower bound to
 *                                     ULONG_MAX.
 *                                  4  A bounded interval.
 *                min, max  --  References to two unsigned long integers for
 *                              storing the lower and upper bound of the
 *                              interval.
 *
 * Returns:       A value telling the type of the specified interval, which is
 *                a bitwise or of the values LEFT_BOUNDED and RIGHT_BOUNDED
 *                depending on which bounds were given explicitly for the
 *                interval. (The lower and upper bounds of the interval itself
 *                are stored in the variables `min' and `max', respectively.)
 *                The function will throw a NotANumberException if the
 *                interval string is of an invalid format.
 *
 * ------------------------------------------------------------------------- */
{
  unsigned long int tmp_min = 0;
  unsigned long int tmp_max = ULONG_MAX;
  int interval_type = UNBOUNDED;

  if (token != "*")
  {
    string::size_type pos(token.find_first_of("-"));
    if (pos == string::npos)
      pos = token.find("...");
    string value(token.substr(0, pos));

    if (!value.empty())
    {
      tmp_min = parseNumber(value);
      if (pos == string::npos)
	tmp_max = tmp_min;
      interval_type |= LEFT_BOUNDED;
    }

    if (pos != string::npos)
      value = token.substr(pos + (token[pos] == '-' ? 1 : 3));

    if (!value.empty())
    {
      tmp_max = parseNumber(value);
      interval_type |= RIGHT_BOUNDED;
    }
    else if (!(interval_type & LEFT_BOUNDED))
      throw NotANumberException("invalid format for interval");
  }

  min = tmp_min;
  max = tmp_max;

  return interval_type;
}

/* ========================================================================= */
void parseIntervalList
  (const string& token, IntervalList& intervals, unsigned long int min,
   unsigned long int max, vector<string>* extra_tokens)
/* ----------------------------------------------------------------------------
 *
 * Description:   Parses a string of number intervals into an IntervalList.
 *
 * Arguments:     token         --  A reference to a constant comma-separated
 *                                  list of interval strings (see documentation
 *                                  for the parseInterval function).
 *                intervals     --  A reference to an IntervalList to be used
 *                                  for storing the result.
 *                min           --  Absolute lower bound for the numbers.
 *                                  Numbers lower than this bound will not be
 *                                  stored in the result set.
 *                max           --  Absolute upper bound for the numbers.
 *                                  Numbers greater than this bound will not be
 *                                  stored in the result set.
 *                extra_tokens  --  If not 0, all tokens that cannot be
 *                                  recognized as valid interval strings will
 *                                  be stored in the vector of strings to which
 *                                  this variable points. Otherwise the
 *                                  function will throw a NotANumberException.
 *
 * Returns:       Nothing. Throws an IntervalRangeException if any of the
 *                intervals in the list does not fit in the closed range
 *                [min,max].
 *
 * ------------------------------------------------------------------------- */
{
  vector<string> interval_strings;
  int interval_type;

  intervals.clear();
  sliceString(token, ",", interval_strings);

  for (vector<string>::const_iterator i = interval_strings.begin();
       i != interval_strings.end();
       ++i)
  {
    unsigned long int i_start, i_end;

    try
    {
      interval_type = parseInterval(*i, i_start, i_end);
    }
    catch (const NotANumberException&)
    {
      if (extra_tokens != 0)
      {
	extra_tokens->push_back(*i);
	continue;
      }
      else
	throw;
    }

    if (interval_type & LEFT_BOUNDED)
    {
      if (i_start < min || i_start > max)
	throw IntervalRangeException(i_start);
    }
    else if (i_start < min)
      i_start = min;
      
    if (interval_type & RIGHT_BOUNDED)
    {
      if (i_end < min || i_end > max)
	throw IntervalRangeException(i_end);
    }
    else if (i_end > max)
      i_end = max;

    intervals.merge(i_start, i_end);
  }
}

/* ========================================================================= */
void parseTime
  (const string& time_string, unsigned long int& hours,
   unsigned long int& minutes, unsigned long int& seconds)
/* ----------------------------------------------------------------------------
 *
 * Description:   Parses a "time string", i.e., a string of the form
 *                    ([0-9]+"h")([0-9]+"min")?([0-9]+"s")?
 *                  | ([0-9]+"min")([0-9]+"s")?
 *                  | ([0-9]+"s")
 *                (where 'h', 'min' and 's' correspond to hours, minutes and
 *                seconds, respectively) and stores the numbers into three
 *                unsigned long integers.  The case of the unit symbols is not
 *                relevant.
 *
 * Arguments:     time_string  --  String to process.
 *                hours        --  A reference to an unsigned long integer for
 *                                 storing the number of hours.
 *                minutes      --  A reference to an unsigned long integer for
 *                                 storing the number of minutes.
 *                seconds      --  A reference to an unsigned long integer for
 *                                 storing the number of seconds.
 *
 *                Time components left unspecified in `time_string' will get
 *                the value 0.
 *
 * Returns:       Nothing. Throws an Exception if the given string is not of
 *                the correct format.
 *
 * ------------------------------------------------------------------------- */
{
  bool hours_present = false, minutes_present = false, seconds_present = false;
  hours = minutes = seconds = 0;

  if (time_string.empty())
    throw Exception("invalid time format");

  string::size_type pos1 = 0;
  string s;

  while (pos1 < time_string.length())
  {
    string::size_type pos2 = time_string.find_first_not_of("0123456789", pos1);
    if (pos2 >= time_string.length())
      throw Exception("invalid time format");

    unsigned long int val;

    try
    {
      val = parseNumber(time_string.substr(pos1, pos2 - pos1));
    }
    catch (const NotANumberException&)
    {
      throw Exception("invalid time format");
    }
    
    switch (tolower(time_string[pos2]))
    {
      case 'h' :
	if (hours_present || minutes_present || seconds_present)
	  throw Exception("invalid time format");
	hours_present = true;
	hours = val;
	break;

      case 'm' :
	if (minutes_present
	    || seconds_present
	    || pos2 + 2 >= time_string.length()
	    || tolower(time_string[pos2 + 1]) != 'i'
	    || tolower(time_string[pos2 + 2]) != 'n')
	  throw Exception("invalid time format");
	minutes_present = true;
        minutes = val;
	pos2 += 2;
	break;

      case 's' :
	if (seconds_present)
	  throw Exception("invalid time format");
	seconds_present = true;
	seconds = val;
	break;

      default : /* 's' */
	throw Exception("invalid time format");
	break;
    }

    pos1 = pos2 + 1;
  }
}

}
