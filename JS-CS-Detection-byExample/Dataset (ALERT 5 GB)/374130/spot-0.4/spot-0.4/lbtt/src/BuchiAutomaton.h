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

#ifndef BUCHIAUTOMATON_H
#define BUCHIAUTOMATON_H

#include <config.h>
#include <cstdlib>
#include <iostream>
#include <map>
#include <string>
#include "LbttAlloc.h"
#include "BitArray.h"
#include "EdgeContainer.h"
#include "Exception.h"
#include "Graph.h"
#include "LtlFormula.h"

using namespace std;

namespace Graph
{

/******************************************************************************
 *
 * A class for representing generalized Büchi automata.
 *
 *****************************************************************************/

class BuchiAutomaton : public Graph<GraphEdgeContainer>
{
public:
  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  class BuchiTransition;                            /* A class for representing
						     * the transitions between
						     * the states of the
						     * automaton.
						     */

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  class BuchiState :                                /* A class for      */
    public Graph<GraphEdgeContainer>::Node          /* representing the */
  {                                                 /* states of the
                                                     * automaton.
						     */
  public:
    explicit BuchiState                             /* Constructor. */
      (const unsigned long int
         num_of_acceptance_sets);

    ~BuchiState();                                  /* Destructor. */

    /* `edges' inherited from Graph<GraphEdgeContainer>::Node */
    
    BitArray& acceptanceSets();                     /* Tell the acceptance  */
    const BitArray& acceptanceSets() const;         /* status of the state. */
    
    void print                                      /* Writes information   */
      (ostream& stream,                             /* about the state to a */
       const int indent,                            /* stream.              */
       const GraphOutputFormat fmt) const;

    void print                                      /* Writes information   */
      (ostream& stream,                             /* about the state to a */
       const int indent,                            /* stream.              */
       const GraphOutputFormat fmt,
       const unsigned long int
         number_of_acceptance_sets)
      const;     

  private:
    BuchiState(const BuchiState&);                  /* Prevent copying and */
    BuchiState& operator=(const BuchiState&);       /* assignment of
						     * BuchiState objects.
						     */

    BitArray acceptance_sets;                       /* Acceptance status of the
						     * state.
						     */
  };

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  explicit BuchiAutomaton                           /* Constructor. */
    (const size_type initial_number_of_states = 0,
     const size_type initstate = 0,
     const unsigned long int
       number_of_accept_sets = 0);

  BuchiAutomaton(const BuchiAutomaton& automaton);  /* Copy constructor. */

  ~BuchiAutomaton();                                /* Destructor. */

  BuchiAutomaton&                                   /* Assignment operator. */
    operator=(const BuchiAutomaton& automaton);

  BuchiState& operator[]                            /* Indexing operator. No */
    (const size_type index) const;                  /* range check is
						     * performed on the
						     * argument.
						     */

  BuchiState& node(const size_type index) const;    /* Synonym for the indexing
                                                     * operator. This function
						     * also checks the range of
						     * the argument.
                                                     */

  /* `size' inherited from Graph<GraphEdgeContainer> */

  /* `empty' inherited from Graph<GraphEdgeContainer> */

  void clear();                                     /* Makes the automaton
						     * empty.
						     */

  size_type expand(size_type node_count = 1);       /* Inserts states to the
                                                     * automaton.
                                                     */

  void connect                                      /* Connects two states   */
    (const size_type father,                        /* of the automaton with */
     const size_type child);                        /* an unguarded
						     * transition with no
						     * associated acceptance
						     * sets.
						     */

  void connect                                      /* Connects two states   */
    (const size_type father, const size_type child, /* of the automaton with */
     ::Ltl::LtlFormula& guard,                      /* a transition guarded  */
     const BitArray& acc_sets);                     /* by a propositional    */
  void connect                                      /* formula.              */
    (const size_type father, const size_type child,
     ::Ltl::LtlFormula* guard,
     const BitArray& acc_sets);

  /* `disconnect' inherited from Graph<GraphEdgeContainer> */

  /* `connected' inherited from Graph<GraphEdgeContainer> */

  /* `stats' inherited from Graph<GraphEdgeContainer> */

  /* `subgraphStats' inherited from Graph<GraphEdgeContainer> */

  size_type initialState() const;                   /* Get or set the       */
  size_type& initialState();                        /* initial state of the *
                                                     * automaton.
                                                     */

  unsigned long int numberOfAcceptanceSets() const; /* Returns the number of
						     * acceptance sets in the
						     * automaton.
						     */

  void read(istream& input_stream);                 /* Reads the automaton
                                                     * from a stream.
                                                     */

  void print                                        /* Writes information  */
    (ostream& stream = cout,                        /* about the automaton */
     const int indent = 0,                          /* to a stream in      */
     const GraphOutputFormat fmt = NORMAL) const;   /* various formats
                                                     * (determined by the
                                                     * `fmt' argument).
						     */

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  class AutomatonParseException;                    /* Class for reporting
						     * parse errors when
						     * reading an automaton
						     * description from a
						     * stream.
						     */

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

private:
  size_type initial_state;                          /* Identifier of the
                                                     * initial state of the
                                                     * automaton.
                                                     */

  unsigned long int number_of_acceptance_sets;      /* Number of acceptance
						     * sets in the automaton.
						     */
};



/******************************************************************************
 *
 * A class for representing the transitions of a Büchi automaton.
 *
 *****************************************************************************/

class BuchiAutomaton::BuchiTransition : public Graph<GraphEdgeContainer>::Edge
{
public:
  BuchiTransition                                   /* Constructor. */
    (const size_type target,
     ::Ltl::LtlFormula* formula,
     const BitArray& acc_sets,
     unsigned long int num_acc_sets);

  ~BuchiTransition();                               /* Destructor. */

  /* `targetNode' inherited from Graph<GraphEdgeContainer>::Edge */
    
  bool enabled                                      /* These functions test */
    (const BitArray& truth_assignment,              /* whether the          */
     const unsigned long int assignment_size)       /* transition is        */
    const;                                          /* enabled in a given   */
                                                    /* truth assignment for */
  bool enabled                                      /* the atomic           */
    (const Bitset& truth_assignment) const;         /* propositions.        */

  ::Ltl::LtlFormula& guard() const;                 /* Returns the
                                                     * propositional formula
                                                     * guarding the transition.
                                                     */

  BitArray& acceptanceSets();                       /* Returns the         */
  const BitArray& acceptanceSets() const;           /* acceptance sets
						     * associated with the
						     * the transition.
						     */

  void print                                        /* Writes information   */
    (ostream& stream,                               /* about the transition */
     const int indent,                              /* (as a plain graph    */
     const GraphOutputFormat fmt) const;            /* edge) to a stream.   */
    
  void print                                        /* Writes information   */
    (ostream& stream,                               /* about the transition */
     const int indent,                              /* to a stream in       */
     const GraphOutputFormat fmt,                   /* various formats      */
     const unsigned long int                        /* (determined by the   */
       number_of_acceptance_sets) const;	    /* `fmt' argument).     */

private:
  BuchiTransition(const BuchiTransition&);          /* Prevent copying and */
  BuchiTransition& operator=                        /* assignment of       */
    (const BuchiTransition&);                       /* BuchiTransition
						     * objects.
						     */

  bool operator==                                   /* Equality test. Used   */
    (const Graph<GraphEdgeContainer>::Edge&         /* for sorting           */
       transition) const;                           /* transitions in an STL
						     * container.
						     */

  bool operator<                                    /* `Less than' relation. */
    (const Graph<GraphEdgeContainer>::Edge&         /* Used for sorting      */
       transition) const;                           /* transitions in an STL
						     * container.
						     */

  ::Ltl::LtlFormula* guard_formula;                 /* The propositional
                                                     * formula guarding the
                                                     * transition.
                                                     */

  BitArray acceptance_sets;                         /* Acceptance sets
						     * associated with the
						     * transition.
						     */
};



/******************************************************************************
 *
 * A class for reporting parse errors when reading an automaton description
 * from a stream.
 *
 *****************************************************************************/

class BuchiAutomaton::AutomatonParseException : public Exception
{
public:
  AutomatonParseException                           /* Constructor. */
    (const string& msg = "parse error");

  /* default copy constructor */

  ~AutomatonParseException() throw();               /* Destructor. */

  AutomatonParseException&                          /* Assignment operator. */
    operator=(const AutomatonParseException& e);

  /* `what' inherited from class Exception */
};



/******************************************************************************
 *
 * Inline function definitions for class BuchiAutomaton.
 *
 *****************************************************************************/

/* ========================================================================= */
inline BuchiAutomaton::~BuchiAutomaton()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class BuchiAutomaton.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline BuchiAutomaton::BuchiState& BuchiAutomaton::operator[]
  (const size_type index) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Indexing operator for class BuchiAutomaton. This function can
 *                be used to refer to the individual states of the automaton.
 *                No range check will be performed on the argument.
 *
 * Argument:      index  --  Index of a state.
 *
 * Returns:       A reference to a state of the automaton.
 *
 * ------------------------------------------------------------------------- */
{
  return static_cast<BuchiState&>(*nodes[index]);
}

/* ========================================================================= */
inline BuchiAutomaton::BuchiState& BuchiAutomaton::node
  (const size_type index) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Function for referring to a single state of a BuchiAutomaton.
 *                This function will perform a range check on the argument.
 *
 * Argument:      index  --  Index of a state.
 *
 * Returns:       A reference to a state of the automaton.
 *
 * ------------------------------------------------------------------------- */
{
  return static_cast<BuchiState&>(Graph<GraphEdgeContainer>::node(index));
}

/* ========================================================================= */
inline void BuchiAutomaton::connect
  (const size_type father, const size_type child)
/* ----------------------------------------------------------------------------
 *
 * Description:   Connects two states of a BuchiAutomaton to each other with an
 *                unguarded transition (actually, a transition with a guard
 *                that is always true) with an empty set of acceptance
 *                conditions.
 *
 * Arguments:     father  --  Source state identifier.
 *                child   --  Target state identifier.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  BitArray acc_sets(number_of_acceptance_sets);
  acc_sets.clear(number_of_acceptance_sets);
  connect(father, child, &(::Ltl::True::construct()), acc_sets);
}

/* ========================================================================= */
inline void BuchiAutomaton::connect
  (const size_type father, const size_type child, ::Ltl::LtlFormula& guard,
   const BitArray& acc_sets)
/* ----------------------------------------------------------------------------
 *
 * Description:   Connects two states of a BuchiAutomaton to each other, using
 *                a LtlFormula (which is actually a propositional formula) to
 *                guard the transition between the states.
 *
 * Arguments:     father    --  Source state.
 *                child     --  Target state.
 *                guard     --  A reference to an LtlFormula (a propositional
 *                              formula) guarding the transition.
 *                acc_sets  --  A reference to a BitArray giving the
 *                              acceptance sets associated with the transition.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  connect(father, child, guard.clone(), acc_sets);
}

/* ========================================================================= */
inline void BuchiAutomaton::connect
  (const size_type father, const size_type child, ::Ltl::LtlFormula* guard,
   const BitArray& acc_sets)
/* ----------------------------------------------------------------------------
 *
 * Description:   Connects two states of a BuchiAutomaton to each other, using
 *                a LtlFormula (which is actually a propositional formula) to
 *                guard the transition between the states.
 *
 * Arguments:     father    --  Source state.
 *                child     --  Target state.
 *                guard     --  A pointer to an LtlFormula (a propositional
 *                              formula) guarding the transition. The
 *                              transition will "own" the guard formula.
 *                acc_sets  --  A reference to a BitArray giving the acceptance
 *                              sets associated with the transition.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  BuchiTransition* new_buchi_transition
    = new BuchiTransition(child, guard, acc_sets, number_of_acceptance_sets);

  try
  {
    nodes[father]->outgoing_edges.insert(new_buchi_transition);
  }
  catch (...)
  {
    delete new_buchi_transition;
    throw;
  }
}

/* ========================================================================= */
inline BuchiAutomaton::size_type BuchiAutomaton::initialState() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the initial state of the BuchiAutomaton by value.
 *
 * Arguments:     None.
 *
 * Returns:       Index of the initial state of the automaton.
 *
 * ------------------------------------------------------------------------- */
{
  return initial_state;
}

/* ========================================================================= */
inline BuchiAutomaton::size_type& BuchiAutomaton::initialState()
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the initial state of the BuchiAutomaton by reference.
 *                This function can therefore be used to change the initial
 *                state.
 *
 * Arguments:     None.
 *
 * Returns:       A reference to the value of the initial state.
 *
 * ------------------------------------------------------------------------- */
{
  return initial_state;
}

/* ========================================================================= */
inline unsigned long int BuchiAutomaton::numberOfAcceptanceSets() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the number of acceptance sets in the automaton.
 *
 * Arguments:     None.
 *
 * Returns:       The number of acceptance sets in the automaton.
 *
 * ------------------------------------------------------------------------- */
{
  return number_of_acceptance_sets;
}

/* ========================================================================= */
inline istream& operator>>(istream& stream, BuchiAutomaton& automaton)
/* ----------------------------------------------------------------------------
 *
 * Description:   Defines an alternative method for reading an automaton from
 *                a stream by using the >> operator.
 *
 * Arguments:     stream     --  A reference to an input stream.
 *                automaton  --  A reference to the BuchiAutomaton.
 *
 * Returns:       A reference to the input stream.
 *
 * ------------------------------------------------------------------------- */
{
  automaton.read(stream);
  return stream;
}



/******************************************************************************
 *
 * Inline function definitions for class BuchiAutomaton::BuchiTransition.
 *
 *****************************************************************************/

/* ========================================================================= */
inline BuchiAutomaton::BuchiTransition::BuchiTransition
  (const size_type target, ::Ltl::LtlFormula* formula,
   const BitArray& acc_sets, unsigned long int num_acc_sets) :
  Edge(target), guard_formula(formula), acceptance_sets(num_acc_sets)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class BuchiAutomaton::BuchiTransition.
 *                Initializes a new transition to a BuchiState, guarded by an
 *                LtlFormula (which is actually a propositional formula).
 *                
 * Arguments:     target    --  Identifier of the target state of the
 *                              automaton.
 *                formula   --  A pointer to a propositional formula guarding
 *                              the transition.
 *                acc_sets  --  A reference to a constant BitArray containing
 *                              the acceptance sets associated with the
 *                              transition.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  acceptance_sets.copy(acc_sets, num_acc_sets);
}

/* ========================================================================= */
inline BuchiAutomaton::BuchiTransition::~BuchiTransition()
/* ----------------------------------------------------------------------------
 * 
 * Description:   Destructor for class BuchiAutomaton::BuchiTransition.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  ::Ltl::LtlFormula::destruct(guard_formula);
}

/* ========================================================================= */
inline bool BuchiAutomaton::BuchiTransition::operator==
  (const Graph<GraphEdgeContainer>::Edge& transition) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Equality relation for comparing two BuchiTransitions. Two
 *                transitions are `equal' if and only if their target nodes
 *                have the same identifier and if their guard formulae are
 *                identical according to the `less<class ::Ltl::LtlFormula>'
 *                relation.
 *
 * Argument:      transition  --  A reference to a constant Edge.
 *
 * Returns:       Truth value according to the relationship between the two
 *                transitions.
 *
 * ------------------------------------------------------------------------- */
{
  /*
   * This function is called only when comparing two edges stored in the
   * `outgoing_edges' GraphEdgeContainer of some state in a BuchiAutomaton.
   * Since (pointers to) BuchiTransitions are never mixed with other types in
   * that container, it is always safe to static_cast `transition' to a
   * reference to a BuchiTransition.
   */

  return (Edge::operator==(transition)
	  && !(guard_formula < static_cast<const BuchiTransition&>(transition)
	                         .guard_formula)
	  && !(static_cast<const BuchiTransition&>(transition).guard_formula
	         < guard_formula));
}

/* ========================================================================= */
inline bool BuchiAutomaton::BuchiTransition::operator<
  (const Graph<GraphEdgeContainer>::Edge& transition) const
/* ----------------------------------------------------------------------------
 *
 * Description:   `Less than' relation for comparing two BuchiTransitions. A
 *                BuchiTransition is `less than' another if and only if the
 *                identifier of its target node is less than that of the other
 *                or the target nodes agree but the guard formula of the
 *                first transition is `less than' the other according to the
 *                `less<class ::Ltl::LtlFormula>' relation.
 *
 * Argument:      transition  --  A reference to a constant Edge.
 *
 * Returns:       Truth value according to the relationship between the two
 *                transitions.
 *
 * ------------------------------------------------------------------------- */
{
  /*
   * This function is called only when comparing two edges stored in the
   * `outgoing_edges' GraphEdgeContainer of some state in a BuchiAutomaton.
   * Since (pointers to) BuchiTransitions are never mixed with other types in
   * that container, it is always safe to static_cast `transition' to a
   * reference to a BuchiTransition.
   */

  return (Edge::operator<(transition)
	  || (Edge::operator==(transition)
	      && guard_formula
	           < static_cast<const BuchiTransition&>(transition)
	               .guard_formula));
}

/* ========================================================================= */
inline bool BuchiAutomaton::BuchiTransition::enabled
  (const BitArray& truth_assignment, const unsigned long int assignment_size)
  const
/* ----------------------------------------------------------------------------
 *
 * Description:   Determines whether the transition is enabled in a given
 *                truth assignment for propositional variables.
 *
 * Arguments:     truth_assignment  --  A reference to a constant BitArray
 *                                      representing an assignment of truth
 *                                      values to propositional variables.
 *                assignment_size   --  Number of propositions in the
 *                                      truth assignment.
 *
 * Returns:       A truth value telling whether the transition is enabled in
 *                the truth assignment.
 *
 * ------------------------------------------------------------------------- */
{ 
  return guard_formula->evaluate(truth_assignment, assignment_size);
}

/* ========================================================================= */
inline bool BuchiAutomaton::BuchiTransition::enabled
  (const Bitset& truth_assignment) const
/* ----------------------------------------------------------------------------
 *
 * Description:   See above.
 *
 * Arguments:     truth_assignment  --  A reference to a constant Bitset
 *                                      representing an assignment of truth
 *                                      values to propositional variables.
 *
 * Returns:       A truth value telling whether the transition is enabled in
 *                the truth assignment.
 *
 * ------------------------------------------------------------------------- */
{ 
  return enabled(truth_assignment, truth_assignment.capacity());
}

/* ========================================================================= */
inline ::Ltl::LtlFormula& BuchiAutomaton::BuchiTransition::guard() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the propositional formula guarding the transition
 *                (a LtlFormula object).
 *
 * Arguments:     None.
 *
 * Returns:       A reference to the constant propositional formula guarding
 *                the transition.
 *
 * ------------------------------------------------------------------------- */
{
  return *guard_formula;
}

/* ========================================================================= */
inline BitArray& BuchiAutomaton::BuchiTransition::acceptanceSets()
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the acceptance sets associated with a
 *                BuchiTransition.
 *
 * Arguments:     None.
 *
 * Returns:       A reference to the BitArray storing the acceptance sets
 *                associated with the transition.
 *
 * ------------------------------------------------------------------------- */
{
  return acceptance_sets;
}

/* ========================================================================= */
inline const BitArray& BuchiAutomaton::BuchiTransition::acceptanceSets() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the acceptance sets associated with a
 *                BuchiTransition.
 *
 * Arguments:     None.
 *
 * Returns:       A constant reference to the BitArray storing the acceptance
 *                sets associated with the transition.
 *
 * ------------------------------------------------------------------------- */
{
  return acceptance_sets;
}

/* ========================================================================= */
inline void BuchiAutomaton::BuchiTransition::print
  (ostream& stream, const int indent, const GraphOutputFormat fmt) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes information about a transition (as a plain graph edge
 *                without any associated information) to a stream.
 *
 * Arguments:     stream  --  A reference to an output stream.
 *                indent  --  Number of spaces to leave to the left of output.
 *                fmt     --  Determines the output format of the transition.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Graph<GraphEdgeContainer>::Edge::print(stream, indent, fmt);
}



/******************************************************************************
 *
 * Inline function definitions for class BuchiAutomaton::BuchiState.
 *
 *****************************************************************************/

/* ========================================================================= */
inline BuchiAutomaton::BuchiState::BuchiState
  (const unsigned long int num_of_acceptance_sets) : 
  Node(), acceptance_sets(num_of_acceptance_sets)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class BuchiAutomaton::BuchiState. Initializes
 *                a new state for the automaton.
 *
 * Argument:      num_of_acceptance_sets  --  Number of acceptance sets in the
 *                                            automaton to which the state
 *                                            belongs.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  acceptance_sets.clear(num_of_acceptance_sets);
}

/* ========================================================================= */
inline BuchiAutomaton::BuchiState::~BuchiState()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class BuchiAutomaton::BuchiState.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline BitArray& BuchiAutomaton::BuchiState::acceptanceSets()
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the acceptance status of the BuchiState, i.e. the
 *                BitArray indicating the acceptance sets to which the state
 *                belongs.
 *
 * Arguments:     None.
 *
 * Returns:       A reference to a BitArray object telling the acceptance sets
 *                to which the state belongs.
 *
 * ------------------------------------------------------------------------- */
{
  return acceptance_sets;
}

/* ========================================================================= */
inline const BitArray& BuchiAutomaton::BuchiState::acceptanceSets() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the acceptance status of the BuchiState, i.e. the
 *                BitArray indicating the acceptance sets to which the state
 *                belongs.
 *
 * Arguments:     None.
 *
 * Returns:       A reference to a constant BitArray object telling the
 *                acceptance sets to which the state belongs.
 *
 * ------------------------------------------------------------------------- */
{
  return acceptance_sets;
}

/* ========================================================================= */
inline void BuchiAutomaton::BuchiState::print
  (ostream& stream, const int indent, const GraphOutputFormat fmt) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes information about a state of a Büchi automaton,
 *                assuming that the (unspecified) maximum number of acceptance
 *                sets in the automaton is 0. [Note: This function is used
 *                to override the `print' function defined in the base class
 *                `Graph::Node'.]
 *
 * Arguments:     stream  --  A reference to an output stream.
 *                indent  --  Number of spaces to leave to the left of output.
 *                fmt     --  Determines the output format of the state.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------ */
{
  this->print(stream, indent, fmt, 0);
}



/******************************************************************************
 *
 * Inline function definitions for class
 * BuchiAutomaton::AutomatonParseException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline BuchiAutomaton::AutomatonParseException::AutomatonParseException
  (const string& msg) :
  Exception(msg)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class
 *                BuchiAutomaton::AutomatonParseException. Initializes a new
 *                exception object with an error message.
 *
 * Argument:      msg  --  A reference to a constant string containing the
 *                         error message.
 *
 * Returns:       Nothing.
 * 
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline BuchiAutomaton::AutomatonParseException::~AutomatonParseException()
  throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class BuchiAutomaton::AutomatonParseException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline BuchiAutomaton::AutomatonParseException&
BuchiAutomaton::AutomatonParseException::operator=
  (const AutomatonParseException& e)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class
 *                BuchiAutomaton::AutomatonParseException. Assigns the contents
 *                of another object of the same type to the exception object.
 *
 * Argument:      e  --  A reference to a constant exception object of the
 *                       same type.
 *
 * Returns:       A reference to the exception object assigned to.
 *
 * ------------------------------------------------------------------------- */
{
  Exception::operator=(e);
  return *this;
}

}

#endif /* !BUCHIAUTOMATON_H */
