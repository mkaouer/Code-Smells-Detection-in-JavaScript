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

#ifndef TESTSTATISTICS_H
#define TESTSTATISTICS_H

#include <config.h>
#include <utility>
#include <vector>
#include "EdgeContainer.h"
#include "Graph.h"
#include "LbttAlloc.h"
#include "BuchiAutomaton.h"
#include "Configuration.h"
#include "StateSpace.h"

using namespace std;
using Graph::BuchiAutomaton;
using Graph::StateSpace;

/******************************************************************************
 *
 * A data structure for storing test data for a single formula.
 *
 *****************************************************************************/

struct AutomatonStats
{
  explicit AutomatonStats                           /* Constructor. */
    (vector<Configuration::AlgorithmInformation>
       ::size_type number_of_algorithms,
     StateSpace::size_type max_statespace_size);

  /* default copy constructor */

  ~AutomatonStats();                                /* Destructor. */

  /* default assignment operator */

  bool buchiAutomatonComputed() const;              /* Tests whether a Büchi
						     * automaton has been
                                                     * computed.
						     */

  bool productAutomatonComputed() const;            /* Tests whether a product
                                                     * automaton has been
                                                     * computed.
						     */

  bool crossComparisonPerformed                     /* Tests whether the     */
    (unsigned long int algorithm) const;            /* result of the
						     * emptiness check has
						     * been compared against
						     * the result computed
						     * using some other
						     * implementation.
						     */

  bool buchiIntersectionCheckPerformed              /* Tests whether the  */
    (unsigned long int algorithm) const;            /* Büchi automata
						     * intersection check
						     * has been performed
						     * against a given
						     * implementation.
						     */

  BuchiAutomaton* buchi_automaton;                  /* A pointer to a Büchi
						     * automaton.
						     */

  BuchiAutomaton::size_type number_of_buchi_states; /* Number of states in the
						     * automaton.
						     */

  unsigned long int number_of_buchi_transitions;    /* Number of transitions in
						     * the automaton.
						     */

  unsigned long int number_of_acceptance_sets;      /* Number of acceptance
						     * sets in the automaton.
						     */

  unsigned long int number_of_msccs;                /* Number of maximal
                                                     * strongly connected
                                                     * components in the
                                                     * automaton.
                                                     */

  double buchi_generation_time;                     /* Time used to generate a
                                                     * Büchi automaton.
						     */

  ::Graph::Graph<GraphEdgeContainer>::size_type     /* Number of stats in a */
    number_of_product_states;                       /* product automaton.   */

  unsigned long int number_of_product_transitions;  /* Number of transitions in
                                                     * a product automaton.
						     */

  Bitset emptiness_check_result;                    /* Result of the emptiness
						     * check for the product
                                                     * automaton.
                                                     */

  bool emptiness_check_performed;                   /* Tells whether the
						     * contents of the previous
						     * Bitset are valid.
						     */

  typedef pair<bool, unsigned long int>
    CrossComparisonStats;

  vector<CrossComparisonStats>                      /* Emptiness check       */
    cross_comparison_stats;                         /* cross-comparison
                                                     * results. The `first'
                                                     * element of the pair
                                                     * tells whether a cross-
                                                     * comparison with a given
                                                     * algorithm has been
                                                     * performed, and the
                                                     * `second' element of the
                                                     * pair gives the number
                                                     * of system states in
                                                     * which the results
                                                     * differ.
						     */

  vector<int> buchi_intersection_check_stats;       /* Büchi automaton
                                                     * intersection
                                                     * emptiness check
						     * results. The elements
						     * of the vector tell
						     * whether the check has
						     * been performed
						     * against the automata
						     * constructed from the
						     * negated formula using
						     * the other algorithms,
						     * and if yes, the result
						     * of the check
						     * (-1 = check not
						     * performed, 0 = check
						     * failed, 1 = check
						     * was successful).
						     */
};



/******************************************************************************
 *
 * A data structure for storing test data for a single algorithm.
 *
 *****************************************************************************/

struct AlgorithmTestResults
{
  explicit AlgorithmTestResults                     /* Constructor. */
    (vector<Configuration::AlgorithmInformation>
       ::size_type
       number_of_algorithms,
     StateSpace::size_type max_statespace_size);

  /* default copy constructor */

  ~AlgorithmTestResults();                          /* Destructor. */

  /* default assignment operator */

  void emptinessReset();                            /* Resets the emptiness
						     * checking information.
						     */

  void fullReset();                                 /* Resets the test results
						     * completely.
                                                     */

  int consistency_check_result;                     /* Tells the consistency
                                                     * check status for an
                                                     * algorithm. The value
                                                     * -1 means the check has
                                                     * not been performed, a 0
                                                     * stands for a failed
                                                     * check, and the value 1
                                                     * denotes that the check
                                                     * was successful.
						     */

  StateSpace::size_type                             /* Number of test cases */
    consistency_check_comparisons;                  /* in the consistency
                                                     * check.
						     */

  StateSpace::size_type                             /* Number of failed test */
    failed_consistency_check_comparisons;           /* cases in the consistency
                                                     * check.
						     */

  vector<AutomatonStats> automaton_stats;           /* A two-element vector
                                                     * storing test results
                                                     * for an algorithm.
						     */
};



/******************************************************************************
 *
 * A data structure for storing test statistics for a single algorithm over the
 * whole test session.
 *
 *****************************************************************************/

struct TestStatistics
{
  explicit TestStatistics                           /* Constructor. */
    (vector<TestStatistics>::size_type
       number_of_algorithms);

  /* default copy constructor */

  ~TestStatistics();

  /* default assignment operator */

  unsigned long int                                 /* Number of failed      */
    failures_to_compute_buchi_automaton[2];         /* attempts to generate
                                                     * a Büchi automaton.
						     */

  unsigned long int buchi_automaton_count[2];       /* Number of attempts to
                                                     * generate a Büchi
                                                     * automaton.
						     */

  unsigned long int                                 /* Number of failed     */
    failures_to_compute_product_automaton[2];       /* attempts to generate
						     * a product automaton.
						     */

  unsigned long int product_automaton_count[2];     /* Number of attempts to
						     * generate a product
						     * automaton.
						     */

  unsigned long int consistency_check_failures;     /* Number of failed
                                                     * consistency checks.
						     */

  unsigned long int consistency_checks_performed;   /* Number of consistency
                                                     * checks performed.
						     */

  BIGUINT total_number_of_buchi_states[2];          /* Total number of states
                                                     * in all the generated
                                                     * Büchi automata.
						     */

  BIGUINT total_number_of_buchi_transitions[2];     /* Total number of
                                                     * transitions in all
                                                     * the generated Büchi
                                                     * automata.
						     */

  BIGUINT total_number_of_acceptance_sets[2];       /* Total number of sets of
                                                     * accepting states in all
						     * the generated Büchi
						     * automata.
						     */

  double total_buchi_generation_time[2];            /* Total time used when
                                                     * generating Büchi
                                                     * automata.
						     */

  BIGUINT total_number_of_product_states[2];        /* Total number of states
                                                     * in all the generated
                                                     * product automata.
						     */

  BIGUINT total_number_of_product_transitions[2];   /* Total number of
                                                     * transitions in all the
                                                     * generated product
                                                     * automata.
						     */

  vector<unsigned long int>                         /* Number of failed */
    cross_comparison_mismatches;                    /* result cross-
                                                     * comparisons.
						     */

  vector<unsigned long int>                         /* Number of failed     */
    initial_cross_comparison_mismatches;            /* result cross-
                                                     * comparisons in the
						     * initial state of the
						     * state space.
						     */

  vector<unsigned long int>                         /* Number of result  */
    cross_comparisons_performed;                    /* cross-comparisons
                                                     * performed.
						     */

  vector<unsigned long int>                         /* Number of failed     */
    buchi_intersection_check_failures;              /* Büchi automaton
                                                     * emptiness checks
						     * against the automata
						     * constructed from the
						     * negated formula
						     * using the other
						     * algorithms.
						     */

  vector<unsigned long int>                         /* Number of Büchi       */
    buchi_intersection_checks_performed;            /* automaton emptiness
                                                     * checks performed
						     * against the automata
						     * constructed from the
						     * negated formula using
						     * the other algorithms.
						     */
};



/******************************************************************************
 *
 * Inline function definitions for struct AutomatonStats.
 *
 *****************************************************************************/

/* ========================================================================= */
inline AutomatonStats::AutomatonStats
  (vector<Configuration::AlgorithmInformation>::size_type number_of_algorithms,
   StateSpace::size_type max_statespace_size) :
  buchi_automaton(0), number_of_buchi_states(0),
  number_of_buchi_transitions(0), number_of_acceptance_sets(0),
  number_of_msccs(0), buchi_generation_time(0.0), number_of_product_states(0),
  number_of_product_transitions(1),
  emptiness_check_result(max_statespace_size),
  emptiness_check_performed(false),
  cross_comparison_stats(number_of_algorithms, make_pair(false, 0)),
  buchi_intersection_check_stats(number_of_algorithms, -1)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for struct AutomatonStats.
 *
 * Arguments:     number_of_algorithms  --  Number of implementations taking
 *                                          part in the tests.
 *                max_statespace_size   --  Maximum size of the state spaces
 *                                          used in testing.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  emptiness_check_result.clear();
}

/* ========================================================================= */
inline AutomatonStats::~AutomatonStats()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for struct AutomatonStats.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline bool AutomatonStats::buchiAutomatonComputed() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Test whether a Büchi automaton has been computed (i.e.,
 *                whether information about the automaton has been stored in
 *                the AutomatonStats structure).
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  return (buchi_automaton != 0);
}

/* ========================================================================= */
inline bool AutomatonStats::productAutomatonComputed() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Test whether a product automaton has been computed (i.e.,
 *                whether information about the automaton has been stored in
 *                the AutomatonStats structure).
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  return (number_of_product_states != 0
          || number_of_product_transitions <= number_of_product_states);
}

/* ========================================================================= */
inline bool
AutomatonStats::crossComparisonPerformed(unsigned long int algorithm) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Test whether the emptiness check result for a product
 *                automaton has been compared with another result computed
 *                using a different implementation.
 *
 * Arguments:     algorithm  --  Implementation identifier.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  return cross_comparison_stats[algorithm].first;
}

/* ========================================================================= */
inline bool
AutomatonStats::buchiIntersectionCheckPerformed(unsigned long int algorithm)
  const
/* ----------------------------------------------------------------------------
 *
 * Description:    Test whether the Büchi automata intersection check result
 *                 (against some other implementation) is available in the data
 *                 structure.
 *
 * Arguments:      algorithm  --  Implementation identifier.
 *
 * Returns:        Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  return (buchi_intersection_check_stats[algorithm] != -1);
}



/******************************************************************************
 *
 * Inline function definitions for struct AlgorithmTestResults.
 *
 *****************************************************************************/

/* ========================================================================= */
inline AlgorithmTestResults::AlgorithmTestResults
  (vector<Configuration::AlgorithmInformation>::size_type number_of_algorithms,
   StateSpace::size_type max_statespace_size) :
  consistency_check_result(-1), consistency_check_comparisons(0),
  failed_consistency_check_comparisons(0),
  automaton_stats(2, AutomatonStats(number_of_algorithms,
				    max_statespace_size))
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for struct AlgorithmTestResults.
 *
 * Arguments:     number_of_algorithms  --  Number of implementations taking
 *                                          part in the tests.
 *                max_statespace_size   --  Maximum size of the state spaces
 *                                          used in testing.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline AlgorithmTestResults::~AlgorithmTestResults()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for struct AlgorithmTestResults.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}



/******************************************************************************
 *
 * Inline function definitions for struct TestStatistics.
 *
 *****************************************************************************/

/* ========================================================================= */
inline TestStatistics::TestStatistics
  (vector<TestStatistics>::size_type number_of_algorithms) :
  consistency_check_failures(0), consistency_checks_performed(0),
  cross_comparison_mismatches(number_of_algorithms, 0),
  initial_cross_comparison_mismatches(number_of_algorithms, 0),
  cross_comparisons_performed(number_of_algorithms, 0),
  buchi_intersection_check_failures(number_of_algorithms, 0),
  buchi_intersection_checks_performed(number_of_algorithms, 0)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for struct TestStatistics.
 *
 * Arguments:     number_of_algorithms  --  Number of implementations taking
 *                                          part in the tests.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  for (int i = 0; i < 2; i++)
  {
    failures_to_compute_buchi_automaton[i] = 0;
    buchi_automaton_count[i] = 0;
    failures_to_compute_product_automaton[i] = 0;
    product_automaton_count[i] = 0;
    total_number_of_buchi_states[i] = 0;
    total_number_of_buchi_transitions[i] = 0;
    total_number_of_acceptance_sets[i] = 0;
    total_number_of_product_states[i] = 0;
    total_number_of_product_transitions[i] = 0;
    total_buchi_generation_time[i] = 0.0;
  }
}

/* ========================================================================= */
inline TestStatistics::~TestStatistics()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for struct TestStatistics.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

#endif /* !TESTSTATISTICS_H */
