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

#ifndef DISPUTIL_H
#define DISPUTIL_H

#include <config.h>
#include <iostream>
#include <string>
#include "Configuration.h"
#include "SharedTestData.h"
#include "TestRoundInfo.h"

using namespace std;

extern Configuration configuration;

/******************************************************************************
 *
 * Prototypes for miscellaneous routines for controlling output stream
 * formatting and writing text into a stream.
 *
 *****************************************************************************/

namespace DispUtil
{

void changeStreamFormatting                         /* Changes the state of */
  (ostream& stream, int width, int precision,       /* an output stream and */
   ios::fmtflags flags);                            /* saves its previous  
                                                     * state.
						     */

void restoreStreamFormatting(ostream& stream);      /* Restores a previously
                                                     * saved state of an
                                                     * output stream.
						     */

void printTextBlock                                 /* Writes an indented */
  (ostream& stream, int indent, const string& text, /* and word-wrapped   */
   int max_line_len);                               /* block of text into
						     * a stream.
						     */

 bool printText                                     /* "Verbosity-aware"     */
   (const char* text,                               /* functions for writing */
    const int verbosity_threshold,                  /* text to standard      */
    const int indent);                              /* output.               */

 bool printText
   (const string& text,
    const int verbosity_threshold,
    const int indent);



/******************************************************************************
 *
 * A data stucture for storing information about the state of an output stream.
 *
 *****************************************************************************/

struct StreamFormatting
{
  int width;                                      /* Field width. */

  int precision;                                  /* Floating-point
						   * precision.
						   */

  ios::fmtflags flags;                            /* Various flags affecting
						   * e.g. the justification
						   * of output.
						   */
};



/******************************************************************************
 *
 * Functions for printing text on standard output.
 *
 *****************************************************************************/

/* ========================================================================= */
inline bool printText
  (const char* text, const int verbosity_threshold, const int indent = 0)
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes text on the standard output if the current output
 *                verbosity level is greater or equal to a given threshold.
 *
 * Arguments:     text                 --  Text to write.
 *                verbosity_threshold  --  Verbosity level threshold.
 *                indent               --  Number of spaces to print on the
 *                                         left of the text.
 *
 * ------------------------------------------------------------------------- */
{
  if (configuration.global_options.verbosity >= verbosity_threshold)
  {
    if (indent > 0)
      SharedTestData::round_info.cout << string(indent, ' ');
    SharedTestData::round_info.cout << text;
    SharedTestData::round_info.cout.flush();
    return true;
  }

  return false;
}   

/* ========================================================================= */
inline bool printText
  (const string& text, const int verbosity_threshold, const int indent = 0)
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes text on the standard output if the current output
 *                verbosity level is greater or equal to a given threshold.
 *
 * Arguments:     text                 --  Text to write.
 *                verbosity_threshold  --  Verbosity level threshold.
 *                indent               --  Number of spaces to print on the
 *                                         left of the text.
 *
 * Returns:       `true' if anything was written to standard output.
 *
 * ------------------------------------------------------------------------- */
{
  if (configuration.global_options.verbosity >= verbosity_threshold)
  {
    if (indent > 0)
      SharedTestData::round_info.cout << string(indent, ' ');
    SharedTestData::round_info.cout << text;
    SharedTestData::round_info.cout.flush();
    return true;
  }

  return false;
}   

}

#endif /* !DISPUTIL_H */
