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
#include <deque>
#include <stack>
#include "Alloc.h"
#include "DispUtil.h"

/******************************************************************************
 *
 * Functions for displaying various statistics during testing.
 *
 *****************************************************************************/

namespace DispUtil
{

stack<StreamFormatting,                             /* Stack for storing the */
      deque<StreamFormatting,                       /* previous states of an */
            ALLOC(StreamFormatting) > >             /* output stream.        */
  stream_formatting_stack;

/* ========================================================================= */
void changeStreamFormatting
  (ostream& stream, int width, int precision, ios::fmtflags flags)
/* ----------------------------------------------------------------------------
 *
 * Description:   Changes the formatting state of an output stream, storing its
 *                previous state so that it can be restored later.
 *
 * Arguments:     stream     --  A reference to an output stream.
 *                width      --  Field width.
 *                precision  --  Floating-point precision.
 *                flags      --  Flags affecting e.g. output justification.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  StreamFormatting formatting;

  formatting.width = stream.width(width);
  formatting.precision = stream.precision(precision);
  formatting.flags = stream.flags(flags);

  stream_formatting_stack.push(formatting);
}

/* ========================================================================= */
void restoreStreamFormatting(ostream& stream)
/* ----------------------------------------------------------------------------
 *
 * Description:   Restores the formatting state of an output stream whose
 *                previous state was saved by a call to
 *                changeStreamFormatting.
 *
 * Arguments:     stream  --  A reference to an output stream.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (!stream_formatting_stack.empty())
  {
    StreamFormatting formatting = stream_formatting_stack.top();
    stream_formatting_stack.pop();
    stream.width(formatting.width);
    stream.precision(formatting.precision);
    stream.flags(formatting.flags);
  }
}

/* ========================================================================= */
void printTextBlock
  (ostream& stream, int indent, const string& text, int max_line_len)
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes a string of text to a stream, breaking the text to
 *                multiple indented lines of a given maximum length if
 *                required.
 *
 * Arguments:     stream        --  A reference to an output stream.
 *                indent        --  Number of spaces to leave on the left of
 *                                  each printed line.
 *                text          --  Text to be written to the stream. '\n':s
 *                                  can be used in the text to insert
 *                                  additional line breaks to the output.
 *                max_line_len  --  Maximum allowed line length (including the
 *                                  amount of indentation).
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);
  string::size_type max_len, current_pos = 1;
  const string ind = string(indent, ' ');

  if (indent > max_line_len)
    max_len = 0;
  else
    max_len = max_line_len - indent;

  string word;
  string::const_iterator c = text.begin();

  while (c != text.end() || !word.empty())
  {
    if (c == text.end() || *c == ' ' || *c == '\t' || *c == '\n')
    {
      while (!word.empty())
      {
	if (current_pos == 1)
	  estream << ind;

	string::size_type new_pos = current_pos + word.length();
	if (current_pos > 1)
	  ++new_pos;

	if (new_pos > max_len)
	{
	  if (current_pos == 1)
	  {
	    estream << word;
	    word = "";
	  }

	  estream << '\n';
	  current_pos = 1;
	}
	else
        {
	  if (current_pos > 1)
	    estream << ' ';
	  estream << word;
	  word = "";
	  current_pos = new_pos;
	}
      }

      if (c != text.end())
      {
	if (*c == '\n')
	{
	  current_pos = 1;
	  estream << '\n';
	}
	++c;
      }
    }
    else if (c != text.end())
    {
      word += *c;
      ++c;
    }
  }

  estream << '\n';
  estream.flush();
}

}
