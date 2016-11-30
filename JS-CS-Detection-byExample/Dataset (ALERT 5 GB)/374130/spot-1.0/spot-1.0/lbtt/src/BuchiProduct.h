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

#ifndef BUCHIPRODUCT_H
#define BUCHIPRODUCT_H

#include <config.h>
#include <deque>
#include <iostream>
#include <map>
#include <string>
#include <vector>
#include "BitArray.h"
#include "BuchiAutomaton.h"
#include "EdgeContainer.h"
#include "Graph.h"
#include "LtlFormula.h"

using namespace std;

namespace Graph 
{

/******************************************************************************
 *
 * A class with operations for checking the intersection of two Büchi automata
 * (represented as two BuchiAutomaton objects) for emptiness.
 *
 *****************************************************************************/

class BuchiProduct
{
public:
  BuchiProduct                                      /* Constructor. */
    (const Graph<GraphEdgeContainer>& a1,
     const Graph<GraphEdgeContainer>& a2);

  /* default copy constructor */

  ~BuchiProduct();                                  /* Destructor. */

  /* default assignment operator */

  bool empty() const;                               /* Tells whether the
						     * intersection of the
						     * Büchi automata
						     * associated with the
						     * product object is
						     * (trivially) empty.
						     */

  unsigned long int numberOfAcceptanceSets() const; /* Tells the number of
                                                     * acceptance sets in the
						     * intersection of the
						     * automata associated with
						     * the object.
						     */

  const BuchiAutomaton::BuchiState& firstComponent  /* Mappings between an   */
    (const Graph<GraphEdgeContainer>::size_type     /* intersection state    */
       state_id) const;                             /* identifier and states */
  const BuchiAutomaton::BuchiState& secondComponent /* of the underlying     */
    (const Graph<GraphEdgeContainer>::size_type     /* automata.             */
       state_id) const;

  void mergeAcceptanceInformation                   /* Merges the acceptance */
    (const Graph<GraphEdgeContainer>::Node& state1, /* sets associated with  */
     const Graph<GraphEdgeContainer>::Node& state2, /* a pair of states into */
     BitArray& acceptance_sets) const;              /* a collection of sets. */

  void mergeAcceptanceInformation                   /* Merges the acceptance */
    (const Graph<GraphEdgeContainer>::Edge&         /* sets associated with  */
       transition1,                                 /* a pair of             */
     const Graph<GraphEdgeContainer>::Edge&         /* transitions into a    */
       transition2,                                 /* collection of sets.   */
     BitArray& acceptance_sets) const;

  void validateEdgeIterators                        /* Ensures that a pair   */
    (const Graph<GraphEdgeContainer>::Node&         /* of transition         */
       state_1,                                     /* iterators points to a */
     const Graph<GraphEdgeContainer>::Node&         /* transition beginning  */
       state_2,                                     /* from a given state in */
     GraphEdgeContainer::const_iterator&            /* the intersection of   */
       transition_1,                                /* two Büchi automata.   */
     GraphEdgeContainer::const_iterator&
       transition_2);

  void incrementEdgeIterators                       /* Updates a pair of     */
    (const Graph<GraphEdgeContainer>::Node&         /* transition iterators  */
       state_1,                                     /* to make them point to */
     const Graph<GraphEdgeContainer>::Node&         /* the "next" transition */
       state_2,                                     /* starting from a given */
     GraphEdgeContainer::const_iterator&            /* state in the          */
       transition_1,                                /* intersection of two   */
     GraphEdgeContainer::const_iterator&            /* Büchi automata.       */
       transition_2);

  static void clearSatisfiabilityCache();           /* Clears information about
						     * the satisfiability of
						     * the guards of product
						     * transitions.
						     */
private:
  void mergeAcceptanceInformation                   /* Bitwise or between   */
    (const BitArray& sets1, const BitArray& sets2,  /* two "component"      */
     BitArray& result) const;                       /* acceptance set
						     * vectors and a result
						     * vector.
						     */

  

  bool synchronizable                               /* Tests whether a pair  */
    (const Graph<GraphEdgeContainer>::Edge&         /* of transitions of two */
       transition_1,                                /* Büchi automata is     */
     const Graph<GraphEdgeContainer>::Edge&         /* synchronizable.       */
       transition_2);

  const BuchiAutomaton& automaton_1;                /* Automata associated   */
  const BuchiAutomaton& automaton_2;                /* with the BuchiProduct */
                                                    /* object.               */

  const unsigned long int                           /* Number of acceptance  */
    number_of_acceptance_sets;                      /* sets in the
						     * intersection of the
						     * automata.
						     */

  typedef map< ::Ltl::LtlFormula*, bool>            /* Type definition for   */
    SatisfiabilityMapping;                          /* storing information
                                                     * about the
                                                     * satisfiability of the
						     * guards of product
						     * transitions.
						     */

  static map< ::Ltl::LtlFormula*,                   /* Result cache for      */
             SatisfiabilityMapping>                 /* satisfiability tests. */
    sat_cache;
};



/******************************************************************************
 *
 * Inline function definitions for class BuchiProduct.
 *
 *****************************************************************************/

/* ========================================================================= */
inline BuchiProduct::BuchiProduct
  (const Graph<GraphEdgeContainer>& a1, const Graph<GraphEdgeContainer>& a2) :
  automaton_1(static_cast<const BuchiAutomaton&>(a1)),
  automaton_2(static_cast<const BuchiAutomaton&>(a2)),
  number_of_acceptance_sets(static_cast<const BuchiAutomaton&>(a1)
			      .numberOfAcceptanceSets()
			    + static_cast<const BuchiAutomaton&>(a2)
			        .numberOfAcceptanceSets())
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class BuchiProduct.  Initializes a new object
 *                with operations for checking the emptiness of two Büchi
 *                automata.
 *
 * Arguments:     a1, a2  --  Constant references to two
 *                            Graph<GraphEdgeContainer> objects, assumed to be
 *                            BüchiAutomaton objects to which to apply the
 *                            operations.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline BuchiProduct::~BuchiProduct()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class BuchiProduct.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline bool BuchiProduct::empty() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells whether the intersection of the Büchi automata
 *                associated with a BuchiProduct object is (trivially) empty.
 *
 * Arguments:     None.
 *
 * Returns:       true iff either of the automata associated with the
 *                BuchiProduct object is (trivially) empty (i.e., whether
 *                either of the automata has no states).
 *
 * ------------------------------------------------------------------------- */
{
  return (automaton_1.empty() || automaton_2.empty());
}


/* ========================================================================= */
inline unsigned long int BuchiProduct::numberOfAcceptanceSets() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the number of acceptance sets in the intersection of
 *                the two Büchi automata associated with a BuchiProduct object.
 *
 * Arguments:     None.
 *
 * Returns:       The number of acceptance sets in the intersection.
 *
 * ------------------------------------------------------------------------- */
{
  return number_of_acceptance_sets;
}

/* ========================================================================= */
inline const BuchiAutomaton::BuchiState& BuchiProduct::firstComponent
  (const Graph<GraphEdgeContainer>::size_type state_id) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Function for accessing states of the "first" component
 *                automaton in the intersection of two Büchi automata.
 *
 * Argument:      state_id  --  Identifier of a state in the component
 *                              automaton.
 *
 * Returns:       A constant reference to a state in the component automaton.
 *
 * ------------------------------------------------------------------------- */
{
  return automaton_1[state_id];
}

/* ========================================================================= */
inline const BuchiAutomaton::BuchiState& BuchiProduct::secondComponent
  (const Graph<GraphEdgeContainer>::size_type state_id) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Function for accessing states of the "second" component
 *                automaton in the intersection of two Büchi automata.
 *
 * Argument:      state_id  --  Identifier of a state in the component
 *                              automaton.
 *
 * Returns:       A constant reference to a state in the component automaton.
 *
 * ------------------------------------------------------------------------- */
{
  return automaton_2[state_id];
}

/* ========================================================================= */
inline void BuchiProduct::mergeAcceptanceInformation
  (const Graph<GraphEdgeContainer>::Node& state1,
   const Graph<GraphEdgeContainer>::Node& state2,
   BitArray& acceptance_sets) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Merges the acceptance sets associated with a pair of states
 *                of two Büchi automata into a collection of sets.
 *
 * Arguments:     state1, state2   --  Constant references to the states of the
 *                                     automata.
 *                acceptance_sets  --  A reference to a BitArray for storing
 *                                     the result.  The BitArray is assumed to
 *                                     have capacity for
 *                                     `this->number_of_acceptance_sets' bits.
 *
 * Returns:       Nothing.  Let n=`this->automaton_1.numberOfAcceptanceSets()';
 *                after the operation, `acceptance_sets[i] = true' holds if
 *                either
 *                    0 <= i < n and
 *                    `state1.acceptanceSets().test(i) == true'
 *                or  0 <= i - n < `this->automaton_2.numberOfAcceptanceSets()'
 *                    and `state2.acceptanceSets().test(i - n) == true'.
 *
 * ------------------------------------------------------------------------- */
{
  mergeAcceptanceInformation
    (static_cast<const BuchiAutomaton::BuchiState&>(state1).acceptanceSets(),
     static_cast<const BuchiAutomaton::BuchiState&>(state2).acceptanceSets(),
     acceptance_sets);
}

/* ========================================================================= */
inline void BuchiProduct::mergeAcceptanceInformation
  (const Graph<GraphEdgeContainer>::Edge& transition1,
   const Graph<GraphEdgeContainer>::Edge& transition2,
   BitArray& acceptance_sets) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Merges the acceptance sets associated with a pair of
 *                transitions of two Büchi automata into a collection of
 *                acceptance sets.
 *
 * Arguments:     transition1,     --  Constant references to the transitions
 *                transition2          of the automata.
 *                acceptance_sets  --  A reference to a BitArray for storing
 *                                     the result.  The BitArray is assumed to
 *                                     have capacity for
 *                                     `this->number_of_acceptance_sets' bits.
 *
 * Returns:       Nothing.  Let n=`this->automaton_1.numberOfAcceptanceSets()';
 *                after the operation, `acceptance_sets[i] = true' holds if
 *                either
 *                    0 <= i < n and
 *                    `transition1.acceptanceSets().test(i) == true'
 *                or  0 <= i - n < `this->automaton_2.numberOfAcceptanceSets()'
 *                    and `transition2.acceptanceSets().test(i - n) == true'.
 *
 * ------------------------------------------------------------------------- */
{
  mergeAcceptanceInformation
    (static_cast<const BuchiAutomaton::BuchiTransition&>(transition1)
       .acceptanceSets(),
     static_cast<const BuchiAutomaton::BuchiTransition&>(transition2)
       .acceptanceSets(),
     acceptance_sets);
}

/* ========================================================================= */
inline void BuchiProduct::mergeAcceptanceInformation
  (const BitArray& sets1, const BitArray& sets2, BitArray& result) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Bitwise or between two acceptance set vectors and a result
 *                vector.
 *
 * Arguments:     sets1,  --  Constant references to two BitArrays having (at
 *                sets2       least) capacities
 *                            `automaton_1.numberOfAcceptanceSets()' and
 *                            `automaton_2.numberOfAcceptanceSets()',
 *                            respectively.
 *                result  --  A BitArray for storing the result, assumed to
 *                            have room for at least
 *                            `this->number_of_acceptance_sets' bits.
 *
 * Returns:       Nothing.  Let n=`this->automaton_1.numberOfAcceptanceSets()';
 *                after the operation, `result[i] = true' holds if
 *                either
 *                    0 <= i < n and `sets1[i] == true'
 *                or  0 <= i - n < `this->automaton_2.numberOfAcceptanceSets()'
 *                    and `sets2[i - n] == true'.
 *
 * ------------------------------------------------------------------------- */
{
  const unsigned long int shift
    = automaton_1.numberOfAcceptanceSets();
  unsigned long int acceptance_set;
  for (acceptance_set = 0; acceptance_set < shift; ++acceptance_set)
  {
    if (sets1[acceptance_set])
      result.setBit(acceptance_set);
  }
  for ( ; acceptance_set < number_of_acceptance_sets; ++acceptance_set)
  {
    if (sets2[acceptance_set - shift])
      result.setBit(acceptance_set);
  }
}

/* ========================================================================= */
inline void BuchiProduct::validateEdgeIterators
  (const Graph<GraphEdgeContainer>::Node& state_1,
   const Graph<GraphEdgeContainer>::Node& state_2,
   GraphEdgeContainer::const_iterator& transition_1,
   GraphEdgeContainer::const_iterator& transition_2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Checks whether a pair of transition iterators corresponds to
 *                a transition beginning from a state in the intersection of
 *                two Büchi automata; if this is not the case, increments the
 *                iterators to make them point to a valid transition beginning
 *                from the state in the intersection (or to the "end" of the
 *                collection of transitions beginning from the state if no
 *                valid transition can be found by incrementing the iterators).
 *
 * Arguments:     state_1,       --  These variables determine the state in
 *                state_2            the intersection automaton; `state_1' and
 *                                   `state_2' should both be references to
 *                                   BuchiAutomaton::BuchiState objects.
 *                transition_1,  --  References to the transition iterators.
 *                transition_2       Initially, `transition_1' and
 *                                   `transition_2' should point to two
 *                                   transitions starting from `state_1' and
 *                                   `state_2', respectively.
 *
 * Returns:       Nothing.  Upon return, `transition_1' and `transition_2' will
 *                either equal `state_1.edges().end()' and
 *                `state_2.edges().end()', respectively, or they will point to
 *                a pair of transitions beginning from `state_1' and `state_2'
 *                such that this pair of transitions corresponds to a
 *                transition starting from the intersection state determined by
 *                `state_1' and `state_2'.
 *
 * ------------------------------------------------------------------------- */
{
  const GraphEdgeContainer& transitions_1 = state_1.edges();
  const GraphEdgeContainer& transitions_2 = state_2.edges();

  if (transition_1 == transitions_1.end())
  {
    transition_2 = transitions_2.end();
    return;
  }
  if (transition_2 == transitions_2.end())
  {
    transition_1 = transitions_1.end();
    return;
  }
 
  if (!synchronizable(**transition_1, **transition_2))
    incrementEdgeIterators(state_1, state_2, transition_1, transition_2);
}

/* ========================================================================= */
inline void BuchiProduct::incrementEdgeIterators
  (const Graph<GraphEdgeContainer>::Node& state_1,
   const Graph<GraphEdgeContainer>::Node& state_2,
   GraphEdgeContainer::const_iterator& transition_1,
   GraphEdgeContainer::const_iterator& transition_2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Increments a pair of transition iterators to point to the   
 *                "next" transition beginning from a state in the intersection
 *                of two Büchi automata.  If no "next" transition exists, makes
 *                the iterators point to the "end" of the collection of
 *                transitions beginning from the state.
 *
 * Arguments:     state_1,       --  These variables determine the state in
 *                state_2            the intersection automaton; `state_1' and
 *                                   `state_2' should both be references to
 *                                   BuchiAutomaton::BuchiState objects.
 *                transition_1,  --  References to the transition iterators.
 *                transition_2       Initially, `transition_1' and
 *                                   `transition_2' should point to two
 *                                   transitions starting from `state_1' and
 *                                   `state_2', respectively.
 *
 * Returns:       Nothing.  Upon return, `transition_1' and `transition_2' will
 *                either equal `state_1.edges().end()' and
 *                `state_2.edges().end()', respectively, or they will point to
 *                a pair of transitions beginning from `state_1' and `state_2'
 *                such that this pair of transitions corresponds to a
 *                transition starting from the intersection state determined by
 *                `state_1' and `state_2'.
 *
 * ------------------------------------------------------------------------- */
{
  const GraphEdgeContainer& transitions_1 = state_1.edges();
  const GraphEdgeContainer& transitions_2 = state_2.edges();

  do
  {
    ++transition_2;
    if (transition_2 == transitions_2.end())
    {
      ++transition_1;
      if (transition_1 == transitions_1.end())
	return;
      transition_2 = transitions_2.begin();
    }
  }
  while (!synchronizable(**transition_1, **transition_2));
}

/* ========================================================================= */
inline void BuchiProduct::clearSatisfiabilityCache()
/* ----------------------------------------------------------------------------
 *
 * Description:   Clears information about the satisfiability of the guard
 *                formulas of product transitions.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  sat_cache.clear();
}

}

#endif /* !BUCHIPRODUCT_H */
