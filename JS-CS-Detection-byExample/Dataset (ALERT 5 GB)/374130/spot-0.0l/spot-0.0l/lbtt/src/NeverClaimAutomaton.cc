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
#include <cstdio>
#include <fstream>
#include "LtlFormula.h"
#include "NeverClaimAutomaton.h"
#include "StringUtil.h"

/******************************************************************************
 *
 * Declarations for functions and variables provided by the parser.
 *
 *****************************************************************************/

#include "NeverClaim-parse.h"                       /* Include declarations for
						     * the tokens that may be
						     * present in a never claim
						     * file.
						     */

extern int parseNeverClaim                          /* Parser interface. */
  (FILE*, NeverClaimAutomaton&);

extern int current_neverclaim_line_number;          /* Number of the current
						     * line in the never claim
						     * file.
						     */

/******************************************************************************
 *
 * Function definitions for class NeverClaimAutomaton.
 *
 *****************************************************************************/

/* ========================================================================= */
void NeverClaimAutomaton::clear()
/* ----------------------------------------------------------------------------
 *
 * Description:   Makes a NeverClaimAutomaton empty.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  for (vector<StateInfo*, ALLOC(StateInfo*) >::iterator
	 state = state_list.begin();
       state != state_list.end();
       ++state)
    delete (*state);

  state_list.clear();
  label_mapping.clear();
  current_state = 0;
}

/* ========================================================================= */
void NeverClaimAutomaton::read(const char* input_filename)
/* ----------------------------------------------------------------------------
 *
 * Description:   Initializes a NeverClaimAutomaton by parsing a "never claim"
 *                stored in a file.
 *
 * Arguments:     input_filename  --  A C-style string containing the name of
 *                                    the input file.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  clear();

  /*
   *  Parse the never claim in the given input file.
   */

  FILE* input_file = fopen(input_filename, "r");
  if (input_file == 0)
    throw FileOpenException(string("`") + input_filename + "'");

  try
  {
    parseNeverClaim(input_file, *this);
    fclose(input_file);
  }
  catch (const ParseErrorException&)
  {
    fclose(input_file);
    clear();
    throw;
  }

  if (state_list.size() == 1)
    state_list[0]->initial() = true;
}

/* ========================================================================= */
void NeverClaimAutomaton::write(const char* output_filename)
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes a NeverClaimAutomaton to a file in `lbtt' format.
 *
 * Arguments:     output_filename  --  A C-style string containing the name of
 *                                     the output file.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  ofstream automaton_file;
  automaton_file.open(output_filename, ios::out | ios::trunc);
  if (!automaton_file.good())
    throw FileCreationException(string("`") + output_filename + "'");

  try
  {
    Exceptional_ostream eautomaton_file(&automaton_file,
                                        ios::failbit | ios::badbit);

    eautomaton_file << ::StringUtil::toString(state_list.size()) + " 1\n";

    /*
     *  Go through the vector of states. For each state, write the following
     *  information to the output file:
     *
     *       - The number of the state.
     *
     *       - Information about whether the state is the initial state of
     *         the automaton.
     *
     *       - Information about whether the state is an accepting state.
     *
     *       - Transitions from the state to other states (in the form
     *         [state number] [guard formula]; end the transition list with
     *         `-1'.
     */

    for (vector<StateInfo*, ALLOC(StateInfo*) >::const_iterator
	   state = state_list.begin();
         state != state_list.end();
	 ++state)
    {
      eautomaton_file << ::StringUtil::toString((*state)->number()) + ' '
                         + ((*state)->initial() ? "1" : "0") + ' '
                         + ((*state)->accepting() ? "0 " : "") + "-1\n";

      for (multimap<Cstr, Cstr*, less<Cstr>, ALLOC(Cstr*) >::const_iterator
             transition = (*state)->transitions().begin();
           transition != (*state)->transitions().end(); ++transition)
      {
	if (label_mapping.find(transition->first) == label_mapping.end())
	{
	  remove(output_filename);
	  automaton_file.close();
	  throw Exception("error in never claim: jump to undefined label `"
			  + transition->first + "'");
	}

        eautomaton_file << ::StringUtil::toString
	                     ((*(label_mapping.find(transition->first))).
			      second->number())
                           + ' '
	                   + ::StringUtil::toString(*(transition->second))
			   + '\n';
      }

      eautomaton_file << "-1\n";
    }

    automaton_file.close();
  }
  catch (const IOException&)
  {
    remove(output_filename);
    automaton_file.close();
    throw;
  }
}

/* ========================================================================= */
void NeverClaimAutomaton::addNewState()
/* ----------------------------------------------------------------------------
 *
 * Description:   Adds a state to a NeverClaimAutomaton and makes it the
 *                `current' state of the automaton.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  current_state = new StateInfo(state_list.size());
  state_list.push_back(current_state);
}

/* ========================================================================= */
void NeverClaimAutomaton::addNewLabel(Cstr& label)
/* ----------------------------------------------------------------------------
 *
 * Description:   Gives a new label for the `current' state of the automaton.
 *                The following substrings, if present in the label, have the
 *                following side effects (the side effects are cumulative):
 *                   - If the label contains the substring "init", the current
 *                     state is made an initial state.
 *                   - If the label contains the substring "accept", the
 *                     current state is made an accepting state.
 *                   - If the state is labeled "accept_all", a transition is
 *                     added from the state to itself.
 *
 * Argument:      label  --  A string giving the label for the state.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  label_mapping[label] = current_state;

  if (label.find("init") != string::npos)
    current_state->initial() = true;
  if (label.find("accept") != string::npos)
    current_state->accepting() = true;
  if (label == "accept_all")
  {
    string* true_str = new string;
    *true_str += ::Ltl::LtlTrue::prefix_symbol;
    current_state->addTransition(label, true_str);
  }
}



/******************************************************************************
 *
 * Function definitions for class NeverClaimAutomaton::StateInfo.
 *
 *****************************************************************************/

/* ========================================================================= */
NeverClaimAutomaton::StateInfo::~StateInfo()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class NeverClaimAutomaton::StateInfo.
 *                Releases the memory allocated by the state object.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  for (multimap<Cstr, Cstr*, less<Cstr>, ALLOC(Cstr*) >::const_iterator
         transition = state_transitions.begin();
       transition != state_transitions.end(); 
       ++transition)
    delete transition->second;
}



/******************************************************************************
 *
 * Function definitions for class ParseErrorException.
 *
 *****************************************************************************/

/* ========================================================================= */
ParseErrorException::ParseErrorException
  (const string& msg, int line_number, string::size_type error_pos) :
  Exception()
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class ParseErrorException. Initializes an
 *                exception object with an error message.
 *
 * Arguments:     msg         -- A string containing the error message.
 *                line_number -- If nonzero, `msg' is interpreted as the line
 *                               containing the parse error. In this case, the
 *                               error message indicates the position of the
 *                               error on the line.
 *                error_pos   -- Position of the error on the line.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (line_number == 0)
    changeMessage(msg);
  else
  {
    string space_string(msg.substr(0, error_pos));
    for (string::size_type c = 0; c < space_string.length(); c++)
      if (space_string[c] != ' ' && space_string[c] != '\t')
	space_string[c] = ' ';

    changeMessage(string("never claim parse error on line ")
                  + StringUtil::toString(line_number) + '\n' + msg + '\n'
		  + space_string + "^\n");
  }
}
