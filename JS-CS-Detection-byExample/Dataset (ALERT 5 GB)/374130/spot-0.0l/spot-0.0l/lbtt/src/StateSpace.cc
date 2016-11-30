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
#include <map>
#include "DispUtil.h"
#include "Exception.h"
#include "StateSpace.h"
#include "StringUtil.h"

namespace Graph 
{

/******************************************************************************
 *
 * Function definitions for class StateSpace.
 *
 *****************************************************************************/

/* ========================================================================= */
StateSpace::StateSpace
  (const unsigned long int propositions_per_state,
   const size_type initial_number_of_states) :
  atoms_per_state(propositions_per_state), initial_state(0)
#ifdef HAVE_OBSTACK_H
, store()
#endif /* HAVE_OBSTACK_H */
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class StateSpace. Initializes a state space
 *                with a given number of states and with a given number of
 *                atomic propositions per state.
 *
 * Arguments:     propositions_per_state    --  Atomic propositions per state.
 *                initial_number_of_states  --  Initial size of the state space
 *                                              (can be grown later).
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  expand(initial_number_of_states);
}

/* ========================================================================= */
StateSpace::StateSpace(const StateSpace& statespace) :
  Graph<GraphEdgeContainer>(), atoms_per_state(statespace.atoms_per_state),
  initial_state(statespace.initial_state)
#ifdef HAVE_OBSTACK_H
, store()
#endif /* HAVE_OBSTACK_H */
/* ----------------------------------------------------------------------------
 *
 * Description:   Copy constructor for class StateSpace. Creates a copy of a
 *                StateSpace object.
 *
 * Argument:      statespace  --  StateSpace to be copied.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  expand(statespace.size());

  for (size_type state = 0; state < size(); ++state)
  {
    for (GraphEdgeContainer::const_iterator transition
	   = statespace[state].edges().begin();
	 transition != statespace[state].edges().end();
	 ++transition)
      connect(state, (*transition)->targetNode());

    operator[](state).positiveAtoms().copy(statespace[state].positiveAtoms(),
					   atoms_per_state);
  }
}  

/* ========================================================================= */
StateSpace& StateSpace::operator=(const StateSpace& statespace)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class StateSpace. Assigns the
 *                contents of a state space to another one.
 *
 * Argument:      statespace  --  A reference to the constant StateSpace whose
 *                                contents are to be copied.
 *
 * Returns:       A reference to the StateSpace assigned to.
 *
 * ------------------------------------------------------------------------- */
{
  if (&statespace != this) 
  {
    clear();
    expand(statespace.size());
    atoms_per_state = statespace.atoms_per_state;
    initial_state = statespace.initial_state;

    for (size_type state = 0; state < size(); ++state)
    {
      for (GraphEdgeContainer::const_iterator transition
	     = statespace[state].edges().begin();
	   transition != statespace[state].edges().end();
	   ++transition)
	connect(state, (*transition)->targetNode());

      operator[](state).positiveAtoms().copy(statespace[state].positiveAtoms(),
					     atoms_per_state);
    }
  }

  return *this;
}

/* ========================================================================= */
void StateSpace::clear()
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
  atoms_per_state = 0;
  initial_state = 0;

#ifdef HAVE_OBSTACK_H
  for (vector<Node*, ALLOC(Node*) >::iterator state = nodes.begin();
       state != nodes.end();
       ++state)
    static_cast<State*>(*state)->~State();

  if (!nodes.empty())
  {
    store.free(*nodes.begin());
    nodes.clear();
    nodes.reserve(0);
  }
#endif /* HAVE_OBSTACK_H */

  Graph<GraphEdgeContainer>::clear();
}

/* ========================================================================= */
StateSpace::size_type StateSpace::expand(size_type node_count)
/* ----------------------------------------------------------------------------
 *
 * Description:   Inserts a given number of states to a StateSpace.
 *
 * Argument:      node_count  --  Number of states to be inserted.
 *
 * Returns:       The index of the last inserted state.
 *
 * ------------------------------------------------------------------------- */
{
  nodes.reserve(nodes.size() + node_count);

  while (node_count > 0) 
  {
#ifdef HAVE_OBSTACK_H
    void* state_storage = store.alloc(sizeof(State));
    State* new_state = new(state_storage) State(atoms_per_state);
#else
    State* new_state = new State(atoms_per_state);
#endif /* HAVE_OBSTACK_H */

    try
    {
      nodes.push_back(new_state);
    }
    catch (...)
    {
#ifdef HAVE_OBSTACK_H
      new_state->~State();
      store.free(state_storage);
#else
      delete new_state;
#endif /* HAVE_OBSTACK_H */
      throw;
    }
    node_count--;
  }

  return size() - 1;
}

/* ========================================================================= */
void StateSpace::connect(const size_type father, const size_type child)
/* ----------------------------------------------------------------------------
 *
 * Description:   Connects two states of the state space.
 *
 * Arguments:     father, child  --  Identifiers of two states.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Edge* edge = operator[](child).incoming_edge;

  if (edge != 0)
  {
    nodes[father]->outgoing_edges.insert(edge);
    return;
  }

#ifdef HAVE_OBSTACK_H
  void* edge_storage = store.alloc(sizeof(Edge));
  edge = new(edge_storage) Edge(child);
#else
  edge = new Edge(child);
#endif /* HAVE_OBSTACK_H */

  try
  {
    nodes[father]->outgoing_edges.insert(edge);
  }
  catch (...)
  {
#ifdef HAVE_OBSTACK_H
    edge->~Edge();
    store.free(edge_storage);
#else
    delete edge;
#endif /* HAVE_OBSTACK_H */
    throw;
  }

  operator[](child).incoming_edge = edge;
}

/* ========================================================================= */
void StateSpace::disconnect(const size_type father, const size_type child)
/* ----------------------------------------------------------------------------
 *
 * Description:   Disconnects two states of the state space.
 *
 * Arguments:     father, child  --  Identifiers for two states.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Edge e(child);

  /*
   *  Scan the set of `father''s outgoing transitions for a transition to the
   *  given target state and remove it if such a transition exists.
   */

  GraphEdgeContainer::iterator search_edge
    = nodes[father]->outgoing_edges.find(&e);

  if (search_edge != nodes[father]->outgoing_edges.end())
    nodes[father]->outgoing_edges.erase(search_edge);
}

/* ========================================================================= */
void StateSpace::print
  (ostream& stream, const int indent, const GraphOutputFormat fmt) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes information about a StateSpace to a stream.
 *
 * Arguments:     stream  --  A reference to an output stream.
 *                indent  --  Number of spaces to leave to the left of output.
 *                fmt     --  Determines the output format.
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
      estream << string(indent, ' ') + "The state space is empty.\n";
  }
  else 
  {
    if (fmt == NORMAL)
    {
      pair<size_type, unsigned long int> statistics = stats();
      pair<size_type, unsigned long int> reachable_part_statistics =
        subgraphStats(initial_state);

      estream << string(indent, ' ') + "The state space consists of\n"
	         + string(indent + 4, ' ')
              << statistics.first
              << " states and\n" + string(indent + 4, ' ')
              << statistics.second
              << " transitions.\n" + string(indent, ' ')
                 + "The reachable part of the state space contains\n"
                 + string(indent + 4, ' ')
              << reachable_part_statistics.first
              << " states and\n" + string(indent + 4, ' ')
              << reachable_part_statistics.second 
              << " transitions.\n" + string(indent, ' ') + "Initial state: "
              << initial_state << '\n';
    }

    size_type s = nodes.size();
    for (size_type state = 0; state < s; ++state) 
    {
      estream << string(indent, ' ');
      if (fmt == NORMAL)
      {
        estream << "State " << state << ":\n";
        operator[](state).print(stream, indent + 4, NORMAL, atoms_per_state);
      }
      else if (fmt == DOT)
      {
        GraphEdgeContainer::const_iterator transition;

        estream << "  n" << state << " [";
        if (state == 0)
          estream << "style=filled,";
        estream << "shape=ellipse,label=\"" << state << ": ";
        operator[](state).print(stream, 0, DOT, atoms_per_state);
        estream << "\",fontsize=12];\n";

        for (transition = nodes[state]->edges().begin();
             transition != nodes[state]->edges().end();
             ++transition)
        {
          estream << string(indent + 2, ' ') + 'n' << state;
          (*transition)->print(stream, indent, fmt);
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
 * Function definitions for class StateSpace::State.
 *
 *****************************************************************************/

/* ========================================================================= */
void StateSpace::State::print
  (ostream& stream, const int indent, const GraphOutputFormat fmt,
   const unsigned long int number_of_atoms) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes information about a state of a state space.
 *
 * Arguments:     stream           --  A reference to an output stream.
 *                indent           --  Number of spaces to leave to the left of
 *                                     output.
 *                fmt              --  Determines the format of output.
 *                number_of_atoms  --  Number of atoms associated with the
 *                                     state.
 * 
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);

  if (positive_atoms.count(number_of_atoms) == 0)
  {
    if (fmt == NORMAL)
      estream << string(indent, ' ') + "No true propositions in this state.\n";
    else if (fmt == DOT)
      estream << "{}";
  }
  else 
  {
    bool first_printed = false;

    if (fmt == NORMAL)
      estream << string(indent, ' ') + "True propositions:\n";

    string text = "{";

    for (unsigned long int atom = 0; atom < number_of_atoms; ++atom) 
    {
      if (positive_atoms[atom])
      {
        if (first_printed)
          text += ", ";
        else
          first_printed = true;
        text += 'p' + ::StringUtil::toString(atom);
      }
    }
    text += '}';
    if (fmt == NORMAL)
      ::DispUtil::printTextBlock(stream, indent + 2, text, 78);
    else
      estream << text;
  }

  if (fmt == NORMAL)
  {
    if (edges().empty())
      estream << string(indent, ' ') + "No transitions to other states.\n";
    else 
    {
      bool first_printed = false;
      estream << string(indent, ' ') + "Transitions to states\n";

      string text = "{";
    
      for (GraphEdgeContainer::const_iterator edge = edges().begin();
           edge != edges().end();
           ++edge) 
      {
        if (first_printed)
          text += ", ";
        else
          first_printed = true;
        text += ::StringUtil::toString((*edge)->targetNode());
      }
      text += '}';
      ::DispUtil::printTextBlock(stream, indent + 2, text, 78);
    }
  }

  estream.flush();
}

}
