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
#include <deque>
#include <stack>
#include <utility>
#include <vector>
#include "BuchiAutomaton.h"
#include "StringUtil.h"

namespace Graph
{

/******************************************************************************
 *
 * Function definitions for class BuchiAutomaton.
 *
 *****************************************************************************/

/* ========================================================================= */
BuchiAutomaton::BuchiAutomaton
  (const size_type initial_number_of_states, const size_type initstate,
   const unsigned long int number_of_accept_sets) :
  initial_state(initstate), number_of_acceptance_sets(number_of_accept_sets)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class BuchiAutomaton. Initializes an
 *                automaton with a given number of states and a given
 *                initial state.
 *
 * Arguments:     initial_number_of_states  --  Initial size of the automaton
 *                                              (can be grown later).
 *                initstate                 --  Index of the automaton's
 *                                              initial state.
 *                number_of_accept_sets     --  Number of acceptance sets in
 *                                              the automaton.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (initstate >= initial_number_of_states
      && !(initial_number_of_states == 0 && initstate == 0))
    throw NodeIndexException();

  nodes.reserve(initial_number_of_states);

  for (size_type i = 0; i < initial_number_of_states; ++i)
    nodes.push_back(new BuchiState(number_of_accept_sets));
}

/* ========================================================================= */
BuchiAutomaton::BuchiAutomaton(const BuchiAutomaton& automaton) :
  Graph<GraphEdgeContainer>(),
  initial_state(automaton.initial_state),
  number_of_acceptance_sets(automaton.number_of_acceptance_sets)
/* ----------------------------------------------------------------------------
 *
 * Description:   Copy constructor for class BuchiAutomaton. Creates a copy of
 *                a BuchiAutomaton object.
 *
 * Argument:      automaton  --  Automaton to be copied.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  expand(automaton.size());

  for (size_type state = 0; state < size(); ++state)
  {
    for (GraphEdgeContainer::const_iterator transition
	   = automaton[state].edges().begin();
	 transition != automaton[state].edges().end();
	 ++transition)
      connect(state,
	      static_cast<const BuchiTransition*>(*transition)->targetNode(),
	      static_cast<const BuchiTransition*>(*transition)->guard(),
	      static_cast<const BuchiTransition*>(*transition)
	        ->acceptanceSets());

    operator[](state).acceptanceSets().copy(automaton[state].acceptanceSets(),
					    number_of_acceptance_sets);
  }
}

/* ========================================================================= */
BuchiAutomaton& BuchiAutomaton::operator=(const BuchiAutomaton& automaton)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class BuchiAutomaton. Assigns the
 *                contents of a BuchiAutomaton to another one.
 *
 * Argument:      automaton  --  A constant reference to a BuchiAutomaton whose
 *                               contents are to be copied.
 *
 * Returns:       A reference to the BuchiAutomaton assigned to.
 *
 * ------------------------------------------------------------------------- */
{
  if (&automaton != this)
  {
    initial_state = automaton.initial_state;
    number_of_acceptance_sets = automaton.number_of_acceptance_sets;

    clear();
    expand(automaton.size());

    for (size_type state = 0; state < size(); ++state)
    {
      for (GraphEdgeContainer::const_iterator transition
	     = automaton[state].edges().begin();
	   transition != automaton[state].edges().end();
	   ++transition)
	connect(state,
		static_cast<const BuchiTransition*>(*transition)->targetNode(),
		static_cast<const BuchiTransition*>(*transition)->guard(),
		static_cast<const BuchiTransition*>(*transition)
		  ->acceptanceSets());

      operator[](state).acceptanceSets().copy
	(automaton[state].acceptanceSets(), number_of_acceptance_sets);
    }
  }

  return *this;
}

/* ========================================================================= */
void BuchiAutomaton::clear()
/* ----------------------------------------------------------------------------
 *
 * Description:   Makes the automaton empty.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Graph<GraphEdgeContainer>::clear();
  initial_state = 0;
  number_of_acceptance_sets = 0;
}

/* ========================================================================= */
BuchiAutomaton::size_type BuchiAutomaton::expand(size_type node_count)
/* ----------------------------------------------------------------------------
 *
 * Description:   Inserts a given number of states to a BuchiAutomaton.
 *
 * Argument:      node_count  --  Number of states to be inserted.
 *
 * Returns:       The index of the last inserted state.
 *
 * ------------------------------------------------------------------------- */
{
  nodes.reserve(nodes.size() + node_count);

  BuchiState* new_buchi_state;

  while (node_count > 0)
  {
    new_buchi_state = new BuchiState(number_of_acceptance_sets);
    try
    {
      nodes.push_back(new_buchi_state);
    }
    catch (...)
    {
      delete new_buchi_state;
      throw;
    }
    node_count--;
  }

  return nodes.size() - 1;
}

/* ========================================================================= */
void BuchiAutomaton::read(istream& input_stream)
/* ----------------------------------------------------------------------------
 *
 * Description:   Reads an automaton description (which may represent a
 *                generalized Büchi automaton) from a stream and stores it
 *                into the automaton object.
 *
 * Argument:      input_stream  --  A reference to an input stream.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  size_type number_of_states, current_state, neighbor_state;
  unsigned long int acceptance_set;
  Exceptional_istream einput_stream(&input_stream, ios::failbit | ios::badbit);

  clear();

  try 
  {
    /* Read the number of states in the generalized Büchi automaton. */

    einput_stream >> number_of_states;

    /* If the automaton is empty, do nothing. */

    if (number_of_states == 0)
      return;

    /*
     *  Determine the number and placement of acceptance sets.
     *  (Acceptance sets are described using strings described by the regular
     *  expression [0-9]+(s|S|t|T)*, where the [0-9]+ part corresponds to the
     *  number of the sets, and the (s|S|t|T)* part corresponds to the
     *  placement of the sets -- s or S for states, t or T for transitions.
     *  To retain compatibility with lbtt 1.0.x, the acceptance set placement
     *  defaults to acceptance sets on states if is not given explicitly.)
     */

    bool acceptance_sets_on_states = false;
    bool acceptance_sets_on_transitions = false;

    string tok;
    einput_stream >> tok;

    string::size_type s_pos = string::npos;
    string::size_type t_pos = string::npos;
    string::size_type pos = tok.find_first_not_of("0123456789");
    if (pos == 0)
      throw AutomatonParseException
	("invalid specification for acceptance sets");

    number_of_acceptance_sets = strtoul(tok.substr(0, pos).c_str(), 0, 10);

    for ( ; pos < tok.length(); ++pos)
    {
      if (tok[pos] == 's' || tok[pos] == 'S')
      {
	s_pos = pos;
	acceptance_sets_on_states = true;
      }
      else if (tok[pos] == 't' || tok[pos] == 'T')
      {
	t_pos = pos;
	acceptance_sets_on_transitions = true;
      }
      else
	throw AutomatonParseException
	  ("invalid specification for acceptance sets");
    }
    if (s_pos == string::npos && t_pos == string::npos)
    {
      acceptance_sets_on_states = true;
      acceptance_sets_on_transitions = false;
    }
      
    BitArray acc_sets(number_of_acceptance_sets);

    /*
     *  Allocate space for the regular Büchi automaton that will be constructed
     *  from the generalized Büchi automaton.
     */

    nodes.reserve(number_of_states);
    for (size_type i = 0; i < number_of_states; ++i)
      nodes.push_back(new BuchiState(number_of_acceptance_sets));

    /*
     *  The automaton state numbers will be mapped from input file identifiers
     *  to the interval [0...(number of states - 1)].
     */

    map<long int, size_type> state_number_map;
    pair<long int, size_type> state_mapping(0, 0);

    pair<map<long int, size_type>::const_iterator, bool> state_finder;

    /*
     *  Also the acceptance set numbers will be mapped to the interval
     *  [0...(number of acceptance sets - 1)].
     */

    map<long int, unsigned long int> acceptance_set_map;
    pair<long int, unsigned long int> acceptance_set_mapping(0, 0);

    pair<map<long int, unsigned long int>::const_iterator, bool>
      acceptance_set_finder;

    /*
     *  A bit array is used to keep information about states which have been
     *  processed. (This is used to verify that each state is defined exactly
     *  once in the input.)
     */

    BitArray processed_states(number_of_states);
    processed_states.clear(number_of_states);
    unsigned long int number_of_processed_states = 0;

    bool initial_state_fixed = false;       /* will be set to true after    */
                                            /* processing the initial state */
                                            /* of the automaton             */

    int is_initial;

    ::Ltl::LtlFormula* guard;

    for (size_type i = 0; i < number_of_states; ++i)
    {
      /*
       *  Begin processing a new state by reading a state id from the stream.
       */

      einput_stream >> state_mapping.first;

      /*
       *  Try to insert a new state mapping into the state identifier map.
       *  If an insertion actually occurs (there is no element in the map with
       *  key equal to `state_mapping.first'), increment the state counter. In
       *  any case, set the value of `current_state' to the map value.
       *  (Note: It is an error to redefine a state that has already been
       *  processed.)
       */

      state_finder = state_number_map.insert(state_mapping);

      if (!state_finder.second)  /* no insertion occurred */
      {
        current_state = (state_finder.first)->second;
        if (processed_states[current_state])
          throw AutomatonParseException("state redefinition encountered");
      }
      else
      {
        if (state_mapping.second >= number_of_states)
          throw AutomatonParseException("number of different state "
                                        "identifiers does not match the size"
                                        " of the Büchi automaton");

        current_state = state_mapping.second;
        state_mapping.second++;
      }

      /* 
       *  Check whether the current state is an initial state. (There must be
       *  exactly one initial state.)
       */

      einput_stream >> is_initial;

      if (is_initial != 0)
      {
        if (!initial_state_fixed)
        {
          initial_state = current_state;
          initial_state_fixed = true;
        }
        else
          throw AutomatonParseException("multiple initial state definitions");
      }

      /*
       *  Determine which acceptance sets the current state belongs to.
       *  The numbers of the acceptance sets are mapped to the proper
       *  interval, again by introducing mappings for acceptance set numbers
       *  whenever necessary.
       */

      operator[](current_state).acceptanceSets().clear
	(number_of_acceptance_sets);

      if (acceptance_sets_on_states)
      {
	while (1)
        {
	  einput_stream >> acceptance_set_mapping.first;

	  if (acceptance_set_mapping.first == -1)
	    break;

	  acceptance_set_finder =
	    acceptance_set_map.insert(acceptance_set_mapping);

	  if (!acceptance_set_finder.second)
	    acceptance_set = (acceptance_set_finder.first)->second;
	  else
          {
	    if (acceptance_set_mapping.second >= number_of_acceptance_sets)
	      throw AutomatonParseException("number of acceptance sets "
					    "does not match automaton state "
					    "definitions");

	    acceptance_set = acceptance_set_mapping.second;
	    ++acceptance_set_mapping.second;
	  }

	  operator[](current_state).acceptanceSets().setBit(acceptance_set);
	}
      }

      /*
       *  Process the transitions from the state to other states. Read a
       *  target state id and add a mapping for it in the translation table if
       *  necessary. If the automaton is allowed to have acceptance sets
       *  associated with transitions, read an additional list of acceptance
       *  sets. Finally, read the propositional formula guarding the
       *  transition and connect the current state to the target using the
       *  guard formula.
       */

      while (1)
      {
        einput_stream >> state_mapping.first;

        if (state_mapping.first == -1)
          break;

        state_finder = state_number_map.insert(state_mapping);
        if (!state_finder.second)
          neighbor_state = (state_finder.first)->second;
        else
        {
          if (state_mapping.second >= number_of_states)
            throw AutomatonParseException("number of different state "
                                          "identifiers does not match the size"
                                          " of the Büchi automaton");

          neighbor_state = state_mapping.second;
          state_mapping.second++;
        }

	acc_sets.clear(number_of_acceptance_sets);

	/*
	 *  If automata with acceptance sets on transitions are accepted, read
	 *  the acceptance sets associated with the transition.
	 */

	if (acceptance_sets_on_transitions)
	{
	  while (1)
          {
	    einput_stream >> acceptance_set_mapping.first;

	    if (acceptance_set_mapping.first == -1)
	      break;

	    acceptance_set_finder =
	      acceptance_set_map.insert(acceptance_set_mapping);

	    if (!acceptance_set_finder.second)
	      acceptance_set = (acceptance_set_finder.first)->second;
	    else
            {
	      if (acceptance_set_mapping.second >= number_of_acceptance_sets)
		throw AutomatonParseException("number of acceptance sets "
					      "does not match automaton state "
					      "definitions");

	      acceptance_set = acceptance_set_mapping.second;
	      ++acceptance_set_mapping.second;
	    }

	    acc_sets.setBit(acceptance_set);
	  }
	}

        try
        {
          guard = ::Ltl::LtlFormula::read(input_stream);
        }
        catch (const ::Ltl::LtlFormula::ParseErrorException& e)
        {
          throw AutomatonParseException(e.what());
        }

	if (!guard->propositional())
        {
	  ::Ltl::LtlFormula::destruct(guard);
	  throw AutomatonParseException("illegal operators in guard formula");
	}

	connect(current_state, neighbor_state, guard, acc_sets);
      }

      processed_states.setBit(current_state);
      ++number_of_processed_states;
    }

    if (!initial_state_fixed)
      throw AutomatonParseException("no initial state specified");
    
    if (number_of_processed_states != number_of_states)
      throw AutomatonParseException("incomplete automaton definition");
  }
  catch (const IOException& e)
  {
    clear();
    if (input_stream.eof())
      throw AutomatonParseException("unexpected end of input");
    else
      throw;
  }
  catch (...)
  {
    clear();
    throw;
  }
}

/* ========================================================================= */
void BuchiAutomaton::print
  (ostream& stream, const int indent, const GraphOutputFormat fmt) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes information about a BuchiAutomaton to a stream.
 *
 * Arguments:     stream  --  A reference to an output stream.
 *                indent  --  Number of spaces to leave to the left of output.
 *                fmt     --  Determines the format of the output.
 * 
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);

  if (fmt == DOT)
    estream << string(indent, ' ') + "digraph G {\n";

  if (nodes.empty())
  {
    if (fmt == NORMAL)
      estream << string(indent, ' ') + "The Büchi automaton is empty.\n";
  }
  else
  {
    if (fmt == NORMAL)
    {
      pair<size_type, unsigned long int> statistics = stats();
      pair<size_type, unsigned long int> reachable_part_statistics
        = subgraphStats(initial_state);

      estream << string(indent, ' ') + "The Büchi automaton consists of\n"
                 + string(indent + 4, ' ')
              << statistics.first
              << " states and\n" + string(indent + 4, ' ')
              << statistics.second
              << " transitions.\n" + string(indent, ' ')
                 + "The automaton has "
	      << number_of_acceptance_sets
	      << " acceptance sets.\n" + string(indent, ' ')
                 + "The reachable part of the automaton contains\n"
                 + string(indent + 4, ' ')
              << reachable_part_statistics.first
              << " states and\n" + string(indent + 4, ' ')
              << reachable_part_statistics.second
              << " transitions.\n" + string(indent, ' ') + "Initial state: "
              << initial_state
              << '\n';
    }
    else if (fmt == DOT)
    {
      estream << string(indent, ' ') + "  init [style=invis];\n"
                 + string(indent, ' ') + "  init->n"
	      << initial_state
	      << ";\n";
    }

    size_type s = nodes.size();
    for (size_type state = 0; state < s; ++state)
    {
      estream << string(indent, ' ');
      if (fmt == NORMAL)
      {
        estream << "State " << state << ":\n";
        operator[](state).print(stream, indent + 4, NORMAL,
				number_of_acceptance_sets);
      }
      else if (fmt == DOT)
      {
        GraphEdgeContainer::const_iterator transition;
	bool first_printed = false;

        estream << "  n"
		<< state
		<< string(" [shape=circle,label=\"")
                << state
                << "\\n{";

	for (unsigned long int accept_set = 0;
	     accept_set < number_of_acceptance_sets;
	     accept_set++)
	{
	  if (operator[](state).acceptanceSets().test(accept_set))
	  {
	    if (first_printed)
	      estream << ',';
	    else
	      first_printed = true;

	    estream << accept_set;
	  }
	}

	estream << "}\",fontsize=12];\n";

        for (transition = nodes[state]->edges().begin();
             transition != nodes[state]->edges().end();
             ++transition)
        {
          estream << string(indent + 2, ' ') + 'n' << state;
          static_cast<const BuchiTransition*>(*transition)
	    ->print(stream, indent, fmt, number_of_acceptance_sets);
          estream << ";\n";
        }
      }
    }
  }

  if (fmt == DOT)
    estream << string(indent, ' ') + "}\n";

  estream.flush();
}



/******************************************************************************
 *
 * Function definitions for class BuchiAutomaton::BuchiTransition.
 *
 *****************************************************************************/

/* ========================================================================= */
void BuchiAutomaton::BuchiTransition::print
  (ostream& stream, const int indent, const GraphOutputFormat fmt,
   const unsigned long int number_of_acceptance_sets) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes information about a transition between two states of
 *                a Büchi automaton.
 *
 * Arguments:     stream                     --  A reference to an output
 *                                               stream.
 *                indent                     --  Number of spaces to leave to
 *                                               the left of output.
 *                fmt                        --  Determines the format of
 *                                               output.
 *                number_of_acceptance_sets  --  Number of acceptance sets in
 *                                               the automaton to which the
 *                                               transition belongs.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);

  if (fmt == NORMAL)
    estream << string(indent, ' ') + "Transition to state "
            << targetNode()
	    << " [ acc.: ";
  else if (fmt == DOT)
  {
    string formula(StringUtil::toString(*guard_formula));

    estream << " -> n" << targetNode() << " [label=\"";
    for (unsigned long int i = 0; i < formula.length(); ++i)
    {
      if (formula[i] == '/')
      {
        estream << "&&";
        i++;
      }
      else if (formula[i] == '\\')
      {
        estream << "||";
        i++;
      }
      else
        estream << formula[i];
    }

    estream << "\\n";
  }

  estream << '{';
  bool first_printed = false;
  for (unsigned long int accept_set = 0;
       accept_set < number_of_acceptance_sets;
       ++accept_set)
  {
    if (acceptance_sets[accept_set])
    {
      if (first_printed)
	estream << ", ";
      else
	first_printed = true;

      estream << accept_set;
    }
  }
  estream << '}';

  if (fmt == NORMAL)
    estream << ", guard: " << *guard_formula << " ]\n";
  else if (fmt == DOT)
    estream << "\",fontsize=10,fontname=\"Courier-Bold\"]";

  estream.flush();
}



/******************************************************************************
 *
 * Function definitions for class BuchiAutomaton::BuchiState.
 *
 *****************************************************************************/

/* ========================================================================= */
void BuchiAutomaton::BuchiState::print
  (ostream& stream, const int indent, const GraphOutputFormat fmt,
   const unsigned long int number_of_acceptance_sets) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes information about a state of a Büchi automaton.
 *
 * Arguments:     stream                     --  A reference to an output
 *                                               stream.
 *                indent                     --  Number of spaces to leave to
 *                                               the left of output.
 *                fmt                        --  Determines the output format
 *                                               of the state.
 *                number_of_acceptance_sets  --  Number of acceptance sets in
 *                                               the automaton in which the
 *                                               state belongs.
 * 
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------ */
{
  if (fmt == DOT)
    return;

  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);

  estream << string(indent, ' ') + "Member of acceptance sets {";

  bool first_printed = false;

  for (unsigned long int accept_set = 0;
       accept_set < number_of_acceptance_sets;
       ++accept_set)
  {
    if (acceptance_sets[accept_set])
    {
      if (first_printed)
	estream << ", ";
      else
	first_printed = true;

      estream << accept_set;
    }
  }

  estream << "}\n";

  if (!edges().empty())
  {
    GraphEdgeContainer::const_iterator edge;

    for (edge = edges().begin(); edge != edges().end(); ++edge)
      static_cast<const BuchiAutomaton::BuchiTransition*>(*edge)
	->print(stream, indent, fmt, number_of_acceptance_sets);

  } else
    estream << string(indent, ' ') + "No transitions to other states.\n";

  estream.flush();
}

}
