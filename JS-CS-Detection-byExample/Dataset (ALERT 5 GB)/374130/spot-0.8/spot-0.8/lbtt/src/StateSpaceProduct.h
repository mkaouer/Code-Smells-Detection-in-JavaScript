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

#ifndef STATESPACEPRODUCT_H
#define STATESPACEPRODUCT_H

#include <config.h>
#include "BitArray.h"
#include "BuchiAutomaton.h"
#include "EdgeContainer.h"
#include "Graph.h"
#include "StateSpace.h"

using namespace std;

namespace Graph 
{

/******************************************************************************
 *
 * A class with operations for checking the product of a Büchi automaton and
 * a state space for emptiness.
 *
 *****************************************************************************/

class StateSpaceProduct
{
public:
  StateSpaceProduct                                 /* Constructor. */
    (const Graph<GraphEdgeContainer>& a,
     const Graph<GraphEdgeContainer>& s);

  /* default copy constructor */

  ~StateSpaceProduct();                             /* Destructor. */

  /* default assignment operator */

  bool empty() const;                               /* Tells whether the
						     * product of a Büchi
						     * automaton and a state
						     * space is (trivially)
						     * empty.
						     */

  unsigned long int numberOfAcceptanceSets() const; /* Tells the number of
                                                     * acceptance sets in a
						     * Büchi automaton in its
						     * product with a state
						     * space.
						     */

  const BuchiAutomaton::BuchiState& firstComponent  /* Mappings between a  */
    (const Graph<GraphEdgeContainer>::size_type     /* product state       */
       state_id) const;                             /* identifier and      */ 
  const StateSpace::State& secondComponent          /* states of the Büchi */
    (const Graph<GraphEdgeContainer>::size_type     /* automaton and the   */
       state_id) const;                             /* state space forming
						     * the product.
						     */

  void mergeAcceptanceInformation                   /* Merges the acceptance */
    (const Graph<GraphEdgeContainer>::Node& state,  /* sets associated with  */
     const Graph<GraphEdgeContainer>::Node&,        /* a state in the Büchi  */
     BitArray& acceptance_sets) const;              /* automaton into a
						     * collection of sets.
						     */

  void mergeAcceptanceInformation                   /* Merges the acceptance */
    (const Graph<GraphEdgeContainer>::Edge&         /* sets associated with  */
       buchi_transition,                            /* a transition in the   */
     const Graph<GraphEdgeContainer>::Edge&,        /* Büchi automaton into  */
     BitArray& acceptance_sets) const;              /* a collection of sets. */

  void validateEdgeIterators                        /* Ensures that a pair   */
    (const Graph<GraphEdgeContainer>::Node&         /* of transition         */
       buchi_state,                                 /* iterators points to a */
     const Graph<GraphEdgeContainer>::Node&         /* transition beginning  */
       system_state,                                /* from a given state in */
     GraphEdgeContainer::const_iterator&            /* the product of a      */
       buchi_transition,                            /* Büchi automaton and   */
     GraphEdgeContainer::const_iterator&            /* a state space.        */
       system_transition) const;

  void incrementEdgeIterators                       /* Updates a pair of     */
    (const Graph<GraphEdgeContainer>::Node&         /* transition iterators  */
       buchi_state,                                 /* to make them point to */
     const Graph<GraphEdgeContainer>::Node&         /* the "next" transition */
       system_state,                                /* starting from a given */
     GraphEdgeContainer::const_iterator&            /* state in the product  */
       buchi_transition,                            /* of a Büchi automaton  */
     GraphEdgeContainer::const_iterator&            /* and a state space.    */
       system_transition) const;

private:
  const BuchiAutomaton& buchi_automaton;            /* Büchi automaton
						     * associated with the
						     * product.
						     */

  const StateSpace& statespace;                     /* State space associated
						     * with the product.
						     */
};



/******************************************************************************
 *
 * Inline function definitions for class StateSpaceProduct.
 *
 *****************************************************************************/

/* ========================================================================= */
inline StateSpaceProduct::StateSpaceProduct
  (const Graph<GraphEdgeContainer>& a, const Graph<GraphEdgeContainer>& s) :
  buchi_automaton(static_cast<const BuchiAutomaton&>(a)),
  statespace(static_cast<const StateSpace&>(s))
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class StateSpaceProduct.  Initializes a new
 *                object with operations for checking the emptiness of the
 *                product of a Büchi automaton and a state space.
 *
 * Arguments:     a  --  A constant reference to a Graph<GraphEdgeContainer>
 *                       object, assumed to be a BuchiAutomaton.
 *                s  --  A constant reference to a Graph<GraphEdgeContainer>
 *                       object, assumed to be a StateSpace.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline StateSpaceProduct::~StateSpaceProduct()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class StateSpaceProduct.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline bool StateSpaceProduct::empty() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells whether the product of `this->buchi_automaton' and
 *                `this->statespace' is (trivially) empty.
 *
 * Arguments:     None.
 *
 * Returns:       true iff either the Büchi automaton or the state space has
 *                no states.
 *
 * ------------------------------------------------------------------------- */
{
  return (buchi_automaton.empty() || statespace.empty());
}

/* ========================================================================= */
inline unsigned long int StateSpaceProduct::numberOfAcceptanceSets() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the number of acceptance sets in the Büchi automaton
 *                associated with a StateSpaceProduct object.
 *
 * Arguments:     None.
 *
 * Returns:       The number of acceptance sets in the automaton.
 *
 * ------------------------------------------------------------------------- */
{
  return buchi_automaton.numberOfAcceptanceSets();
}

/* ========================================================================= */
inline const BuchiAutomaton::BuchiState& StateSpaceProduct::firstComponent
  (const Graph<GraphEdgeContainer>::size_type state_id) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Function for accessing states in the Büchi automaton
 *                associated with a StateSpaceProduct object.
 *
 * Argument:      state_id  --  Identifier of a state in the automaton.
 *
 * Returns:       A constant reference to a state in the automaton.
 *
 * ------------------------------------------------------------------------- */
{
  return buchi_automaton[state_id];
}

/* ========================================================================= */
inline const StateSpace::State& StateSpaceProduct::secondComponent
  (const Graph<GraphEdgeContainer>::size_type state_id) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Function for accessing states in the state space associated
 *                with a StateSpaceProduct object.
 *
 * Argument:      state_id  --  Identifier of a state in the state space.
 *
 * Returns:       A constant reference to a state in the state space.
 *
 * ------------------------------------------------------------------------- */
{
  return statespace[state_id];
}

/* ========================================================================= */
inline void StateSpaceProduct::mergeAcceptanceInformation
  (const Graph<GraphEdgeContainer>::Node& state,
   const Graph<GraphEdgeContainer>::Node&, BitArray& acceptance_sets) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Merges the acceptance sets associated with a state of a Büchi
 *                automaton into a collection of sets.
 *
 * Arguments:     state            --  A constant reference to a state in the
 *                                     automaton.
 *                acceptance_sets  --  A reference to a BitArray for storing
 *                                     the result.  The BitArray should have
 *                                     capacity for (at least)
 *                                     `this->buchi_automaton
 *                                        .numberOfAcceptanceSets()' bits.
 *                (The second argument is needed to allow the class
 *                StateSpaceProduct to be used for instantiating the Product
 *                template; see file Product.h.)
 *
 * Returns:       Nothing.  After the operation, `acceptance_sets[i] == true'
 *                holds if `state.acceptanceSets().test(i) == true' for all
 *                0 <= i < `this->buchi_automaton.numberOfAcceptanceSets()'.
 *
 * ------------------------------------------------------------------------- */
{
  acceptance_sets.bitwiseOr
    (static_cast<const BuchiAutomaton::BuchiState&>(state).acceptanceSets(),
     numberOfAcceptanceSets());
}

/* ========================================================================= */
inline void StateSpaceProduct::mergeAcceptanceInformation
  (const Graph<GraphEdgeContainer>::Edge& buchi_transition,
   const Graph<GraphEdgeContainer>::Edge&, BitArray& acceptance_sets) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Merges the acceptance sets associated with a transition of a
 *                Büchi automaton into a collection of sets.
 *
 * Arguments:     transition       --  A constant reference to a transition in
 *                                     the automaton.
 *                acceptance_sets  --  A reference to a BitArray for storing
 *                                     the result.  The BitArray should have
 *                                     capacity for (at least)
 *                                     `this->buchi_automaton
 *                                        .numberOfAcceptanceSets()' bits.
 *                (The second argument is needed to allow the class
 *                StateSpaceProduct to be used for instantiating the Product
 *                template; see file Product.h.)
 *
 * Returns:       Nothing.  After the operation, `acceptance_sets[i] == true'
 *                holds if `transition.acceptanceSets().test(i) == true' for
 *                all
 *                0 <= i < `this->buchi_automaton.numberOfAcceptanceSets()'.
 *
 * ------------------------------------------------------------------------- */
{
  acceptance_sets.bitwiseOr
    (static_cast<const BuchiAutomaton::BuchiTransition&>(buchi_transition)
       .acceptanceSets(),
     numberOfAcceptanceSets());
}

/* ========================================================================= */
inline void StateSpaceProduct::validateEdgeIterators
  (const Graph<GraphEdgeContainer>::Node& buchi_state,
   const Graph<GraphEdgeContainer>::Node& system_state,
   GraphEdgeContainer::const_iterator& buchi_transition,
   GraphEdgeContainer::const_iterator& system_transition) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Checks whether a pair of transition iterators corresponds to
 *                a transition beginning from a state in the product of a Büchi
 *                automaton and a state space; if this is not the case, makes
 *                the iterators point to a valid transition beginning from the
 *                product state (or to the "end" of the collection of
 *                transitions beginning from the product state if no valid
 *                transition can be found by incrementing the iterators).
 *
 * Arguments:     buchi_state,       --  These variables determine the state
 *                system_state,          in the product; `buchi_state' and
 *                                       `system_state' should be references to
 *                                       states in `this->buchi_automaton' and
 *                                       `this->statespace', respectively.
 *                buchi_transition,  --  References to the transition
 *                system_transition      iterators.  It is assumed that
 *                                       `buchi_transition' and
 *                                       `system_transition' initially point to
 *                                       two transitions starting from
 *                                       `buchi_state' and `system_state',
 *                                       respectively.
 *
 * Returns:       Nothing.  Upon return, `buchi_transition' and
 *                `system_transition' will either equal
 *                `buchi_state.edges().end()' and `system_state.edges().end()',
 *                respectively, or they will point to a pair of transitions
 *                beginning from `buchi_state' and `system_state' such that
 *                this pair of transitions corresponds to a transition starting
 *                from the product state determined by `buchi_state' and
 *                `system_state'.
 *
 * ------------------------------------------------------------------------- */
{
  const GraphEdgeContainer& buchi_transitions = buchi_state.edges();
  const GraphEdgeContainer& system_transitions = system_state.edges();

  if (buchi_transition == buchi_transitions.end())
  {
    system_transition = system_transitions.end();
    return;
  }
  if (system_transition == system_transitions.end())
  {
    buchi_transition = buchi_transitions.end();
    return;
  }

  while (!static_cast<const BuchiAutomaton::BuchiTransition*>
            (*buchi_transition)->enabled
	      (static_cast<const StateSpace::State&>(system_state)
	         .positiveAtoms(),
	       statespace.numberOfPropositions()))
  {
    ++buchi_transition;
    if (buchi_transition == buchi_transitions.end())
    {
      system_transition = system_transitions.end();
      return;
    }
  }

  system_transition = system_transitions.begin();
}

/* ========================================================================= */
inline void StateSpaceProduct::incrementEdgeIterators
  (const Graph<GraphEdgeContainer>::Node& buchi_state,
   const Graph<GraphEdgeContainer>::Node& system_state,
   GraphEdgeContainer::const_iterator& buchi_transition,
   GraphEdgeContainer::const_iterator& system_transition) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Increments a pair of transition iterators to point to the   
 *                "next" transition beginning from a state in the product of a
 *                Büchi automaton and a state space.  If no "next" transition
 *                exists, makes the iterators point to the "end" of the
 *                collection of transitions beginning from the product state.
 *
 * Arguments:     buchi_state,       --  These variables determine the state
 *                system_state,          in the product; `buchi_state' and
 *                                       `system_state' should be references to
 *                                       states in `this->buchi_automaton' and
 *                                       `this->statespace', respectively.
 *                buchi_transition,  --  References to the transition
 *                system_transition      iterators.  It is assumed that
 *                                       `buchi_transition' and
 *                                       `system_transition' initially point to
 *                                       two transitions starting from
 *                                       `buchi_state' and `system_state',
 *                                       respectively.
 *
 * Returns:       Nothing.  Upon return, `buchi_transition' and
 *                `system_transition' will either equal
 *                `buchi_state.edges().end()' and `system_state.edges().end()',
 *                respectively, or they will point to a pair of transitions
 *                beginning from `buchi_state' and `system_state' such that
 *                this pair of transitions corresponds to a transition starting
 *                from the product state determined by `buchi_state' and
 *                `system_state'.
 *
 * ------------------------------------------------------------------------- */
{
  const GraphEdgeContainer& buchi_transitions = buchi_state.edges();
  const GraphEdgeContainer& system_transitions = system_state.edges();

  ++system_transition;
  if (system_transition == system_transitions.end())
  {
    do
    {
      ++buchi_transition;
      if (buchi_transition == buchi_transitions.end())
	return;
    }
    while (!static_cast<const BuchiAutomaton::BuchiTransition*>
             (*buchi_transition)->enabled
	       (static_cast<const StateSpace::State&>(system_state)
		  .positiveAtoms(),
		statespace.numberOfPropositions()));

    system_transition = system_transitions.begin();
  }
}

}

#endif /* !STATESPACEPRODUCT_H */
