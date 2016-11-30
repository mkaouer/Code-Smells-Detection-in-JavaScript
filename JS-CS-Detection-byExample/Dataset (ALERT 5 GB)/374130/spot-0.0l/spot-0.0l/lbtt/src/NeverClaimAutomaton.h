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

#ifndef NEVERCLAIMAUTOMATON_H
#define NEVERCLAIMAUTOMATON_H

#ifdef __GNUC__
#pragma interface
#endif /* __GNUC__ */

#include <config.h>
#include <map>
#include <string>
#include <utility>
#include <vector>
#include "Alloc.h"
#include "Exception.h"

using namespace std;

/******************************************************************************
 *
 * A class for representing the Büchi automaton obtained by parsing a "never
 * claim" (model checker Spin's representation for Büchi automata). This class
 * provides only a very limited set of operations that suffice for parsing a
 * never claim and outputting the parsed automaton in the format used by
 * `lbtt'.
 *
 *****************************************************************************/

class NeverClaimAutomaton
{
private:
  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  class StateInfo;                                  /* A class for storing the
                                                     * states of the automaton.
                                                     */

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  typedef const string Cstr;

public:
  NeverClaimAutomaton();                            /* Constructor. */

  ~NeverClaimAutomaton();                           /* Destructor. */

  void clear();                                     /* Makes the automaton
                                                     * empty.
						     */

  void read(const char* input_filename);            /* Initializes the
                                                     * automaton by parsing a
                                                     * never claim stored in a
                                                     * file.
						     */

  void write(const char* output_filename);          /* Outputs the automaton in
                                                     * `lbtt' format.
						     */

  StateInfo* currentState();                        /* Returns a pointer to the
                                                     * `current' state of the
                                                     * automaton (corresponding
                                                     * to the most recently
                                                     * introduced state in the
                                                     * never claim that is
                                                     * currently being parsed).
                                                     */

  void addNewState();                               /* Adds a new state to the
                                                     * automaton.
						     */

  void addNewLabel(Cstr& label);                    /* Adds a new label for the
                                                     * `current' state of the
                                                     * automaton (a single
                                                     * state can have several
                                                     * different labels).
						     */

private:
  vector<StateInfo*, ALLOC(StateInfo*) >            /* States of the */
    state_list;                                     /* automaton.    */

  map<string, StateInfo*, less<string>,             /* Mapping from state   */
      ALLOC(StateInfo*) >                           /* labels to the states */
    label_mapping;                                  /* itself.              */

  StateInfo* current_state;                         /* Pointer to the state
                                                     * introduced most recently
                                                     * in the input file.
						     */

  NeverClaimAutomaton                               /* Prevent copying and  */
    (const NeverClaimAutomaton& automaton);         /* assignment of        */
  NeverClaimAutomaton& operator=                    /* NeverClaimAutomaton  */
    (const NeverClaimAutomaton& automaton);         /* objects.             */ 
};



/******************************************************************************
 *
 * A class for storing the states of a Büchi automaton that is being generated
 * by parsing a never claim.
 *
 *****************************************************************************/

class NeverClaimAutomaton::StateInfo
{
public:
  explicit StateInfo(unsigned long int num);        /* Constructor. */

  ~StateInfo();                                     /* Destructor. */

  unsigned long int number() const;                 /* Returns the unique
                                                     * identifier of the
                                                     * state.
						     */

  bool initial() const;                             /* Returns or changes */
  bool& initial();                                  /* the `initialness'  */
                                                    /* of the state.
						     */

  bool accepting() const;                           /* Returns or changes    */
  bool& accepting();                                /* the acceptance status */
                                                    /* of the state.
						     */

  const multimap<Cstr, Cstr*, less<Cstr>,           /* Returns the labels of */
                 ALLOC(Cstr*) >&                    /* the state's successor */
    transitions() const;                            /* states, including the
                                                     * conditions controlling
                                                     * the enabledness of the
                                                     * transition.
						     */

  void addTransition                                /* Connects the state to */
    (Cstr& target_label, Cstr* guard);              /* another state, given
                                                     * a state label and a
                                                     * propositional formula
                                                     * guarding the transition.
                                                     */

private:
  StateInfo(const StateInfo&);                      /* Prevent copying and */
  StateInfo& operator=(const StateInfo&);           /* assignment of
						     * StateInfo objects.
						     */

  unsigned long int state_number;                   /* Unique state identifier.
                                                     */

  bool is_initial;                                  /* Is the state an initial
                                                     * state?
						     */

  bool accept;                                      /* Is the state an
                                                     * accepting state?
						     */

  multimap<Cstr, Cstr*, less<Cstr>, ALLOC(Cstr*) >  /* Labels of the state's */
    state_transitions;                              /* successors, including
                                                     * the guard formulae
                                                     * controlling the
                                                     * enabledness of the
                                                     * transitions between
                                                     * states.
						     */
};



/******************************************************************************
 *
 * A class for reporting errors when parsing a never claim.
 *
 *****************************************************************************/

class ParseErrorException : public Exception
{
public:
  ParseErrorException                               /* Constructor. */
    (const string& msg
       = "error parsing never claim",
     int line_number = 0,
     string::size_type error_pos = 0);

  /* default copy constructor */
  
  ~ParseErrorException() throw();                   /* Destructor. */
  
  ParseErrorException& operator=                    /* Assignment operator. */
    (const ParseErrorException& e);
};



/******************************************************************************
 *
 * Inline function definitions for class NeverClaimAutomaton.
 *
 *****************************************************************************/

/* ========================================================================= */
inline NeverClaimAutomaton::NeverClaimAutomaton() : 
  current_state(static_cast<StateInfo*>(0))
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class NeverClaimAutomaton. Creates an empty
 *                automaton.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline NeverClaimAutomaton::~NeverClaimAutomaton()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class NeverClaimAutomaton.
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
inline NeverClaimAutomaton::StateInfo* NeverClaimAutomaton::currentState()
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns a pointer to the "current" state of the automaton,
 *                i.e., the state corresponding to the most recently introduced
 *                state parsed from the never claim.
 *
 * Arguments:     None.
 *
 * Returns:       A pointer to the state.
 *
 * ------------------------------------------------------------------------- */
{
  return current_state;
}



/******************************************************************************
 *
 * Inline function definitions for class NeverClaimAutomaton::StateInfo.
 *
 *****************************************************************************/

/* ========================================================================= */
inline NeverClaimAutomaton::StateInfo::StateInfo(unsigned long int num) :
  state_number(num), is_initial(false), accept(false)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class NeverClaimAutomaton::StateInfo.
 *                Creates a new state with a given identifier. By default, the
 *                created state will be noninitial and nonaccepting.
 *
 * Arguments:     num  --  Numeric identifier for the state.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline unsigned long int NeverClaimAutomaton::StateInfo::number() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the identifier of a state.
 *
 * Arguments:     None.
 *
 * Returns:       The identifier of the state.
 *
 * ------------------------------------------------------------------------- */
{
  return state_number;
}

/* ========================================================================= */
inline bool NeverClaimAutomaton::StateInfo::initial() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells whether the state is an initial state. This function
 *                can be used only for querying the value.
 *
 * Arguments:     None.
 *
 * Returns:       Truth value telling whether the state is an initial state.
 *
 * ------------------------------------------------------------------------- */
{
  return is_initial;
}

/* ========================================================================= */
inline bool& NeverClaimAutomaton::StateInfo::initial()
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells whether the state is an initial state. This function
 *                can be also used to change the `initialness' of the state.
 *
 * Arguments:     None.
 * 
 * Returns:       A reference to a truth value telling whether the state is an
 *                initial state.
 *
 * ------------------------------------------------------------------------- */
{
  return is_initial;
}

/* ========================================================================= */
inline bool NeverClaimAutomaton::StateInfo::accepting() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells whether the state is an accepting state. This function
 *                can be used only for querying the acceptance status.
 *
 * Arguments:     None.
 *
 * Returns:       A truth value telling whether the state is an accepting
 *                state.
 *
 * ------------------------------------------------------------------------- */
{
  return accept;
}

/* ========================================================================= */
inline bool& NeverClaimAutomaton::StateInfo::accepting()
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells whether the state is an accepting state. This function
 *                can also be used to change the acceptance status.
 *
 * Arguments:     None.
 *
 * Returns:       A reference to a truth value telling whether the state is an
 *                accepting state.
 *
 * ------------------------------------------------------------------------- */
{
  return accept;
}

/* ========================================================================= */
inline const multimap<NeverClaimAutomaton::Cstr, NeverClaimAutomaton::Cstr*,
                      less<NeverClaimAutomaton::Cstr>,
                      ALLOC(NeverClaimAutomaton::Cstr*) >&
NeverClaimAutomaton::StateInfo::transitions() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the set of labels of the state's successor states,
 *                including the propositional formulae containing the
 *                conditions (propositional formulae) controlling the
 *                enabledness of the transitions the state and its successors.
 *
 * Arguments:     None.
 *
 * Returns:       A constant reference to a constant multimap object containing
 *                the successor information.
 *
 * ------------------------------------------------------------------------- */
{ 
  return state_transitions; 
}

/* ========================================================================= */
inline void NeverClaimAutomaton::StateInfo::addTransition
  (Cstr& target_label, Cstr* guard)
/* ----------------------------------------------------------------------------
 *
 * Description:   Connects the state to another state.
 *
 * Arguments:     target_label  --  Label of the target state.
 *                guard         --  A pointer to a constant string containing
 *                                  the guard (a propositional formula) for
 *                                  the transition.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{ 
  state_transitions.insert(make_pair(target_label, guard));
}



/******************************************************************************
 *
 * Inline function definitions for class ParseErrorException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline ParseErrorException::~ParseErrorException() throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class ParseErrorException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline ParseErrorException&
ParseErrorException::operator=(const ParseErrorException& e)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class ParseErrorException.
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

#endif /* !NEVERCLAIMAUTOMATON_H */
