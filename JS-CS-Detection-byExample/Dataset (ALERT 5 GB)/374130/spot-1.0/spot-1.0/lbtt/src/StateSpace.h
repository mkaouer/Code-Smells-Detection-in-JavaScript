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

#ifndef STATESPACE_H
#define STATESPACE_H

#include <config.h>
#include "LbttAlloc.h"
#include "BitArray.h"
#include "EdgeContainer.h"
#include "Graph.h"

using namespace std;

extern bool user_break;

namespace Graph
{

/******************************************************************************
 *
 * A class for representing state spaces.
 *
 *****************************************************************************/

class StateSpace : public Graph<GraphEdgeContainer>
{
private:
  unsigned long int atoms_per_state;                /* Number of propositional
                                                     * variables per state in
                                                     * the state space.
                                                     */

  size_type initial_state;                          /* Index of the initial
                                                     * state of the state
                                                     * space.
                                                     */

#ifdef HAVE_OBSTACK_H                               /* Storage for states */
  ObstackAllocator store;                           /* and transitions.   */
#endif /* HAVE_OBSTACK_H */

public:
  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  class State :                                     /* A class for         */
    public Graph<GraphEdgeContainer>::Node          /* representing the    */
  {                                                 /* states of the state
						     * space.
                                                     */
  public:
    explicit State                                  /* Constructs a state   */
      (const unsigned long int                      /* with a false truth   */
         number_of_atoms = 0);                      /* assignment for the
                                                     * atomic propositions.
                                                     */

    State                                           /* Constructs a state  */
      (const BitArray& atoms,                       /* from a given truth  */
       const unsigned long int number_of_atoms);    /* assignment.         */

    ~State();                                       /* Destructor. */

    /* `edges' inherited from Graph<GraphEdgeContainer>::Node */

    bool holds                                      /* Test whether a given */
      (const unsigned long int atom,                /* atomic proposition   */
       const unsigned long int number_of_atoms)     /* holds in the state.  */
      const;
    
    const BitArray& positiveAtoms() const;          /* Get or set the truth  */
    BitArray& positiveAtoms();                      /* assignment for the
                                                     * propositional atoms in
                                                     * a state.
                                                     */
    
    void print                                      /* Writes information   */
      (ostream& stream,                             /* about the state to a */
       const int indent,                            /* stream.              */
       const GraphOutputFormat fmt) const;

    void print                                      /* Writes information   */
      (ostream& stream,                             /* about the state to a */
       const int indent,                            /* stream.              */
       const GraphOutputFormat fmt,
       const unsigned long int number_of_atoms)
      const;    
                                                     
  private:
    friend class StateSpace;

    State(const State&);                            /* Prevent copying and */
    State& operator=(const State&);                 /* assignment of State
						     * objects.
						     */

    BitArray positive_atoms;                        /* The set of propositions
                                                     * holding in the state.
                                                     */

    Edge* incoming_edge;                            /* The unique edge pointing
						     * to `this' state.
						     */
  };

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  explicit StateSpace                               /* Constructor. */
    (const unsigned long int
       propositions_per_state = 0,
     const size_type initial_number_of_states = 0);

  StateSpace(const StateSpace& statespace);         /* Copy constructor. */

  ~StateSpace();                                    /* Destructor. */

  StateSpace& operator=                             /* Assignment operator. */
    (const StateSpace& statespace);

  State& operator[](const size_type index) const;   /* Indexing operator. No
						     * range checks are
						     * performed on the
						     * argument.
						     */

  State& node(const size_type index) const;         /* Synonym for the indexing
                                                     * operator. This function
						     * will also check the
						     * range of its argument.
                                                     */

  /* `size' inherited from Graph<GraphEdgeContainer> */

  /* `empty' inherited from Graph<GraphEdgeContainer> */

  void clear();                                     /* Makes the state space
						     * empty.
						     */

  size_type expand(size_type node_count = 1);       /* Inserts states to the
                                                     * state space.
                                                     */

  void connect                                      /* Connects two states */
    (const size_type father,                        /* of the state space. */
     const size_type child);

  void disconnect                                   /* Disconnects two     */
    (const size_type father,                        /* states of the state */
     const size_type child);                        /* space.              */

  /* `connected' inherited from Graph<GraphEdgeContainer> */

  /* `stats' inherited from Graph<GraphEdgeContainer> */

  /* `subgraphStats' inherited from Graph<GraphEdgeContainer> */

  unsigned long int numberOfPropositions() const;   /* Get the number of atomic
						     * propositions associated
						     * with each state of the
						     * state space.
						     */

  size_type initialState() const;                   /* Get or set the       */
  size_type& initialState();                        /* initial state of the
                                                     * state space.
                                                     */

  void print                                        /* Writes information    */
    (ostream& stream = cout,                        /* about the state space */
     const int indent = 0,                          /* to a stream.          */
     const GraphOutputFormat fmt = NORMAL) const;
};



/******************************************************************************
 *
 * Inline function definitions for class StateSpace.
 *
 *************************************************************************** */

/* ========================================================================= */
inline StateSpace::~StateSpace()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class StateSpace.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  clear();
}

/* ========================================================================= */
inline StateSpace::State& StateSpace::operator[](const size_type index) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Indexing operator for class StateSpace. This function can
 *                be used to refer to the individual states of the state space
 *                This function does not perform any range checks on its
 *                argument.
 *
 * Argument:      index  --  Index of a state.
 *
 * Returns:       A reference to a state of the state space.
 *
 * ------------------------------------------------------------------------- */
{
  return static_cast<State&>(*nodes[index]);
}

/* ========================================================================= */
inline StateSpace::State& StateSpace::node(const size_type index) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Function for referring to a single state of a StateSpace.
 *                This function also performs a range check on its argument.
 *
 * Argument:      index  --  Index of a state.
 *
 * Returns:       A reference to a state of the state space.
 *
 * ------------------------------------------------------------------------- */
{
  return static_cast<State&>(Graph<GraphEdgeContainer>::node(index));
}

/* ========================================================================= */
inline unsigned long int StateSpace::numberOfPropositions() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the number of atomic propositions associated with each
 *                state of the state space.
 *
 * Arguments:     None.
 *
 * Returns:       Number of propositions associated with each state of the
 *                state space.
 *
 * ------------------------------------------------------------------------- */
{
  return atoms_per_state;
}

/* ========================================================================= */
inline StateSpace::size_type StateSpace::initialState() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the number of the initial state of the StateSpace by
 *                value.
 *
 * Arguments:     None.
 *
 * Returns:       Index of the initial state of the state space.
 *
 * ------------------------------------------------------------------------- */
{
  return initial_state;
}

/* ========================================================================= */
inline StateSpace::size_type& StateSpace::initialState()
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the number of the initial state of the StateSpace by 
 *                reference. This function can therefore be used to change the
 *                initial state.
 *
 * Arguments:     None.
 *
 * Returns:       A reference to the index of the initial state.
 *
 * ------------------------------------------------------------------------- */
{
  return initial_state;
}



/******************************************************************************
 *
 * Inline function definitions for class StateSpace::State.
 *
 *****************************************************************************/

/* ========================================================================= */
inline StateSpace::State::State(const unsigned long int number_of_atoms) :
  Graph<GraphEdgeContainer>::Node(), positive_atoms(number_of_atoms),
  incoming_edge(0)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class StateSpace::State. Creates a new state
 *                with a given number of atomic propositions, all of which are
 *                initially false.
 *
 * Argument:      number_of_atoms  --  Number of atomic propositions in the
 *                                     state.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  positive_atoms.clear(number_of_atoms);
}

/* ========================================================================= */
inline StateSpace::State::State
  (const BitArray& atoms, const unsigned long int number_of_atoms) :
  Graph<GraphEdgeContainer>::Node()
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class StateSpace::State. Creates a new state,
 *                using a given truth assignment for propositional variables  
 *                for initialization.
 *
 * Argument:      atoms            --  A truth assignment for atomic
 *                                     propositions.
 *                number_of_atoms  --  Number of atomic propositions in the
 *                                     truth assignment.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  positive_atoms.copy(atoms, number_of_atoms);
  incoming_edge = 0;
}

/* ========================================================================= */
inline StateSpace::State::~State()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class StateSpace::State.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (incoming_edge != 0)
  {
#ifdef HAVE_OBSTACK_H
    incoming_edge->~Edge();
#else
    delete incoming_edge;
#endif /* HAVE_OBSTACK_H */
  }
  outgoing_edges.clear();
}

/* ========================================================================= */
inline bool StateSpace::State::holds
  (const unsigned long int atom, const unsigned long int number_of_atoms) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tests whether a given proposition holds in a state.
 *
 * Arguments:     atom             --  Identifier of the proposition.
 *                number_of_atoms  --  Number of atomic propositions associated
 *                                     with the state.
 *
 * Returns:       Truth value of the proposition in the state.
 *
 * ------------------------------------------------------------------------- */
{
  if (atom >= number_of_atoms)
    return false;

  return positive_atoms[atom];
}

/* ========================================================================= */
inline const BitArray& StateSpace::State::positiveAtoms() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the truth assignment for the propositions in a state.
 *
 * Arguments:     None.
 *
 * Returns:       A reference to the truth assignment, represented as a
 *                constant BitArray.
 *
 * ------------------------------------------------------------------------- */
{
  return positive_atoms;
}

/* ========================================================================= */
inline BitArray& StateSpace::State::positiveAtoms()
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the truth assignment for the propositions in a state.
 *                This function can be also used to change the truth
 *                assignment.
 *
 * Arguments:     None.
 *
 * Returns:       A reference to the truth assignment, represented as a
 *                BitArray.
 *
 * ------------------------------------------------------------------------- */
{
  return positive_atoms;
}

/* ========================================================================= */
inline void StateSpace::State::print
  (ostream& stream, const int indent, const GraphOutputFormat fmt) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes information about a state of a state space, assuming
 *                that there are no propositions associated with the state.
 *                [Note: This function is used to override the `print' function
 *                defined in the base class `Graph::node'.]
 *
 * Arguments:     stream  --  A reference to an output stream.
 *                indent  --  Number of spaces to leave to the left of output.
 *                fmt     --  Determines the format of output.
 * 
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  this->print(stream, indent, fmt, 0);
}

}

#endif /* !STATESPACE_H */
