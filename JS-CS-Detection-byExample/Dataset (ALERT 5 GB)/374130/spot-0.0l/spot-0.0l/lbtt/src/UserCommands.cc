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
#include "DispUtil.h"
#include "Exception.h"
#include "PathEvaluator.h"
#include "SharedTestData.h"
#include "StatDisplay.h"
#include "StringUtil.h"
#include "TestRoundInfo.h"
#include "TestStatistics.h"
#include "UserCommandReader.h"
#include "UserCommands.h"

/******************************************************************************
 *
 * Implementations for the user commands.
 *
 *****************************************************************************/

namespace UserCommands
{

using namespace ::DispUtil;
using namespace ::SharedTestData;
using namespace ::StatDisplay;
using namespace ::StringUtil;
using namespace ::UserCommandInterface;

/* ========================================================================= */
void computeProductAutomaton
  (ProductAutomaton*& product_automaton,
   const BuchiAutomaton& buchi_automaton,
   pair<unsigned long int, bool>& last_automaton,
   const pair<unsigned long int, bool>& new_automaton)
/* ----------------------------------------------------------------------------
 *
 * Description:   Computes a product automaton.
 *
 * Arguments:     product_automaton  --  A reference to a pointer giving the
 *                                       storage location of the generated
 *                                       automaton.
 *                buchi_automaton    --  The Büchi automaton to be used for
 *                                       computing the product automaton.
 *                last_automaton     --  A pair telling the algorithm and the
 *                                       formula last used for computing an
 *                                       automaton (for testing whether the
 *                                       result is already available).
 *                new_automaton      --  A pair telling the algorithm and the
 *                                       formula which are to be used for
 *                                       computing the automaton.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (product_automaton == 0 || last_automaton != new_automaton)
  {
    if (product_automaton != 0)
    {
      printText("<deallocating memory>", 4, 2);

      delete product_automaton;
      product_automaton = 0;

      printText(" ok\n", 4);
    }

    printText("<computing product automaton>", 0, 2);

    try
    {
      product_automaton = new ProductAutomaton();
      product_automaton->computeProduct
	(buchi_automaton, *(round_info.statespace),
	 configuration.global_options.product_mode == Configuration::GLOBAL);
    }
    catch (...)
    {
      if (product_automaton != 0)
      {
	delete product_automaton;
	product_automaton = 0;
      }

      printText(" error\n", 0);

      try
      {
	throw;
      }
      catch (const ::Graph::ProductAutomaton::ProductSizeException&)
      {
	throw CommandErrorException("Product may be too large");
      }
      catch (const UserBreakException&)
      {
	throw CommandErrorException("User break");
      }
      catch (const bad_alloc&)
      {
	throw CommandErrorException("Out of memory");
      }
    }

    printText(" ok\n", 0);

    last_automaton = new_automaton;
  }
}

/* ========================================================================= */
void printAlgorithmList(ostream& stream, int indent)
/* ----------------------------------------------------------------------------
 *
 * Description:   Implements the user command `algorithms', i.e., writes a list
 *                of algorithms used in the tests to a stream.
 *
 * Arguments:     stream  --  A reference to an output stream.
 *                indent  --  Number of spaces to leave on the left of the
 *                            output.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::badbit | ios::failbit);

  estream << string(indent, ' ') + "List of implementations:\n";

  for (unsigned long int algorithm_id = 0;
       algorithm_id < round_info.number_of_translators;
       ++algorithm_id)
  {
    estream << string(indent + 2, ' ')
               + configuration.algorithmString(algorithm_id)
               + " ("
               + (configuration.algorithms[algorithm_id].enabled
		  ? "en"
		  : "dis")
               + "abled)\n";
  }

  estream << '\n';
  estream.flush();
}

/* ========================================================================= */
void synchronizePrefixAndCycle
  (deque<Graph::Graph<GraphEdgeContainer>::size_type,
         ALLOC(Graph::Graph<GraphEdgeContainer>::size_type) >& prefix,
   deque<Graph::Graph<GraphEdgeContainer>::size_type,
         ALLOC(Graph::Graph<GraphEdgeContainer>::size_type) >& cycle)
/* ----------------------------------------------------------------------------
 *
 * Description:   Function for `synchronizing' a sequence of states consisting
 *                of a prefix and a repeating cycle. This means removing from
 *                the end of the prefix the longest sequence of states that
 *                forms a postfix of the cycle. The states in the cycle will be
 *                `rotated' accordingly.
 *
 * Arguments:     prefix  --  A reference to a deque of state identifiers
 *                            forming the prefix of the state sequence.
 *                cycle   --  A reference to a deque of state identifiers
 *                            representing the states in the repeating cycle.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (cycle.empty())
    throw CommandErrorException("internal error");

  while (!prefix.empty() && prefix.back() == cycle.back())
  {
    prefix.pop_back();
    cycle.push_front(cycle.back());
    cycle.pop_back();
  }

  Graph::Graph<GraphEdgeContainer>::size_type state_id = cycle.front();
  deque<Graph::Graph<GraphEdgeContainer>::size_type,
        ALLOC(Graph::Graph<GraphEdgeContainer>::size_type) >::const_iterator
    s;

  for (s = cycle.begin() + 1; s != cycle.end() && *s == state_id; ++s)
    ;

  if (s == cycle.end())
  {
    cycle.clear();
    cycle.push_front(state_id);
  }
}

/* ========================================================================= */
void printCrossComparisonAnalysisResults
  (ostream& stream, int indent, bool formula_type,
   const vector<string, ALLOC(string) >& input_tokens,
   ProductAutomaton*& product_automaton,
   pair<unsigned long int, bool>& last_product_automaton)
/* ----------------------------------------------------------------------------
 *
 * Description:   Implements the user command `resultanalysis', i.e., analyzes
 *                a discrepancy between the results of two algorithms (or one
 *                algorithm against the path algorithm) by searching for a
 *                system execution producting contradicting results and then
 *                displaying the execution.
 *
 * Arguments:     stream                  --  A reference to an output stream.
 *                indent                  --  Number of spaces to leave on the
 *                                            left of the output.
 *                formula_type            --  Tells the LTL formula for which
 *                                            the analysis is to be performed.
 *                input_tokens            --  A reference to a vector
 *                                            containing the arguments of the
 *                                            command.
 *                product_automaton       --  A reference to a pointer telling
 *                                            the storage location of the
 *                                            generated product automaton.
 *                last_product_automaton  --  A pair telling the algorithm and
 *                                            the formula last used for
 *                                            computing a product automaton
 *                                            (for testing whether the result
 *                                            is already available).
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::badbit | ios::failbit);

  unsigned long int algorithm1, algorithm2;
  bool path_compare = false;

  if (!configuration.global_options.do_comp_test)
    throw CommandErrorException("This command is available only when the "
				"model checking result cross-comparison test "
				"is enabled.");

  if (input_tokens[1] == "p")
  {
    path_compare = true;
    algorithm1 = parseNumber(input_tokens[2]);
    algorithm2 = round_info.number_of_translators;
  }
  else
  {
    algorithm1 = parseNumber(input_tokens[1]);
    if (input_tokens[2] == "p")
    {
      path_compare = true;
      algorithm2 = round_info.number_of_translators;
    }
    else
      algorithm2 = parseNumber(input_tokens[2]);
  }

  if (path_compare
      && !(configuration.global_options.statespace_generation_mode
	     & Configuration::PATH))
    throw CommandErrorException("This feature is available only when using "
				"paths as state spaces.");

  verifyNumber(algorithm1, round_info.number_of_translators,
	       "No such implementation");

  int formula = (formula_type ? 0 : 1);
  int generator_formula = formula;

  if (configuration.formula_options.output_mode != Configuration::NNF)
    generator_formula += 2;

  const AutomatonStats* stats1;
  const AutomatonStats* stats2;

  stats1 = &test_results[algorithm1].automaton_stats[formula];

  if (!path_compare)
  {
    verifyNumber(algorithm2, round_info.number_of_translators,
		 "No such implementation");

    if (algorithm1 == algorithm2)
      throw CommandErrorException("Implementation identifiers must be "
				  "different.");
  }

  stats2 = &test_results[algorithm2].automaton_stats[formula];

  if (!stats1->crossComparisonPerformed(algorithm2))
  {
    printTextBlock(stream, indent,
		   "Model checking result cross-comparison was not "
		   "performed between "
                   + configuration.algorithmString(algorithm1)
		   + " and " + configuration.algorithmString(algorithm2)
                   + ".\n",
		   78);
    return;
  }

  if (stats1->cross_comparison_stats[algorithm2].second == 0)
  {
    printTextBlock(stream, indent,
		   "No inconsistencies detected in the cross-comparison of "
		   "results given by "
                   + configuration.algorithmString(algorithm1)
		   + " and "
                   + configuration.algorithmString(algorithm2) + ".\n",
		   78);
    return;
  }

  estream << string(indent, ' ')
             + "Model checking result cross-comparison analysis:\n"
             + string(indent + 2, ' ') + "Formula: "
	  << *round_info.formulae[generator_formula]
	  << "\n\n";

  StateSpace::size_type state;

  if (input_tokens.size() == 3)
  {
    /*
     *  If no state identifier was given as a command argument, search for a
     *  system state in which the results of the two algorithms (or the result
     *  of an algorithm and the path checking algorithm) differ.
     */

    estream << string(indent + 2, ' ') + "The cross-comparison check failed "
                                         "in "
               + toString(stats1->cross_comparison_stats[algorithm2].second)
	       + " (";

    changeStreamFormatting(stream, 0, 2, ios::fixed);
    estream << static_cast<double>
	         (stats1->cross_comparison_stats[algorithm2].second)
               / round_info.real_emptiness_check_size
               * 100.0;
    restoreStreamFormatting(stream);

    estream << "%) of " + toString(round_info.real_emptiness_check_size)
	       + " test cases.\n\n";

    for (state = 0; 
	 stats1->emptiness_check_result[state] 
	   == stats2->emptiness_check_result[state];
	 ++state)
      ;
  }
  else
  {
    /*
     *  Otherwise use the state given as the command argument.
     */

    state = parseNumber(input_tokens[3]);

    verifyNumber(state, round_info.statespace->size(), "No such state");

    if (state >= round_info.real_emptiness_check_size)
    {
      printTextBlock(stream, indent,
	             "Model checking result cross-comparison test was not "
		     "performed between "
		     + configuration.algorithmString(algorithm1)
		     + " and "
		     + configuration.algorithmString(algorithm2)
		     + " in state "
                     + toString(state)
		     + " of the state space.\n",
		     78);
      return;
    }

    if (stats1->emptiness_check_result[state]
	  == stats2->emptiness_check_result[state])
    {
      printTextBlock(stream, indent,
		     "No inconsistency detected between the results given by "
                     + configuration.algorithmString(algorithm1)
                     + " and "
                     + configuration.algorithmString(algorithm2)
                     + " in state "
                     + toString(state)
                     + " of the state space.\n",
		     78);
      return;
    }
  }

  /*
   *  Translate the formula to be analyzed into a Büchi automaton using the
   *  algorithm that claims the existence of an accepting cycle beginning in
   *  the state.
   */

  unsigned long int accepting_algorithm
    = (stats1->emptiness_check_result[state] ? algorithm1 : algorithm2);

  unsigned long int rejecting_algorithm = (accepting_algorithm == algorithm1
					   ? algorithm2
					   : algorithm1);

  deque<StateSpace::size_type, ALLOC(StateSpace::size_type) > system_prefix;
  deque<StateSpace::size_type, ALLOC(StateSpace::size_type) > system_cycle;
  deque<BuchiAutomaton::size_type, ALLOC(BuchiAutomaton::size_type) >
    automaton_prefix;
  deque<BuchiAutomaton::size_type, ALLOC(BuchiAutomaton::size_type) >
    automaton_cycle;

  if (!path_compare || accepting_algorithm == algorithm1)
  {
    /*
     *  Compute the synchronous product of the automaton and the state space.
     */

    computeProductAutomaton(product_automaton,
			    *(test_results[accepting_algorithm].
			       automaton_stats[formula].buchi_automaton),
			    last_product_automaton,
			    make_pair(accepting_algorithm, formula_type));

    /*
     *  Search the product automaton for an accepting cycle.
     */

    pair<deque<ProductAutomaton::StateIdPair,
               ALLOC(ProductAutomaton::StateIdPair) >,
         deque<ProductAutomaton::StateIdPair,
               ALLOC(ProductAutomaton::StateIdPair) > >
      execution;

    try
    {
      printText("<searching for a system execution producing contradictory "
		"results>", 0, 2);
      product_automaton->findAcceptingExecution(state, execution);
    }
    catch (...)
    {
      printText(" error\n", 0);
      throw;
    }

    printText(" ok\n\n", 0);

    /*
     *  Separate the parallel executions of the system and the automaton into
     *  prefixes and cycles.
     */

    while (!execution.first.empty())
    {
      automaton_prefix.push_back(execution.first.front().first);
      system_prefix.push_back(execution.first.front().second);
      execution.first.pop_front();
    }

    while (!execution.second.empty())
    {
      automaton_cycle.push_back(execution.second.front().first);
      system_cycle.push_back(execution.second.front().second);
      execution.second.pop_front();
    }

    synchronizePrefixAndCycle(automaton_prefix, automaton_cycle);
    synchronizePrefixAndCycle(system_prefix, system_cycle);
  }
  else
  {
    StateSpace::size_type loop_state
      = (*((*round_info.statespace)[round_info.statespace->size() - 1].
	    edges().begin()))->targetNode();

    if (state < loop_state)
    {
      for (StateSpace::size_type path_state = state; path_state < loop_state;
	   path_state++)
	system_prefix.push_back(path_state);

      for (StateSpace::size_type path_state = loop_state;
	   path_state < round_info.statespace->size();
	   path_state++)
	system_cycle.push_back(path_state);
    }
    else
    {
      for (StateSpace::size_type path_state = state;
	   path_state < round_info.statespace->size();
	   path_state++)
	system_cycle.push_back(path_state);

      for (StateSpace::size_type path_state = loop_state;
	   path_state < state;
	   path_state++)
	system_cycle.push_back(path_state);
    }
  }

  state = system_prefix.size();

  /*
   *  Construct a path in the state space from the system prefix and the cycle.
   */

  vector<StateSpace::size_type, ALLOC(StateSpace::size_type) > path;

  path.insert(path.end(), system_prefix.begin(), system_prefix.end());
  path.insert(path.end(), system_cycle.begin(), system_cycle.end());

  /*
   *  Write information about the execution to the output stream. That is,
   *  display the states in the infinite execution (a prefix and a cycle)
   *  and tell which one of the algorithms would accept and which one
   *  would reject the execution (or show whether the algorithm compared
   *  against the path checking algorithm accepted or rejected the
   *  execution).
   */

  estream << string(indent + 2, ' ') + "Execution M:\n";

  printPath(stream, indent + 4, system_prefix, system_cycle,
	    *(round_info.statespace));

  estream << string(indent + 4, ' ')
             + "accepted by: "
             + configuration.algorithmString(accepting_algorithm) + '\n'
             + string(indent + 4, ' ') + "rejected by: "
             + configuration.algorithmString(rejecting_algorithm)
             + "\n\n"
             + string(indent + 2, ' ')
             + "Analysis of the formula in the execution:\n";

  /*
   *  Model check the formula separately in the obtained execution to find out
   *  which one of the algorithms was in error.
   */

  Ltl::PathEvaluator path_evaluator;
  bool result = path_evaluator.evaluate
    (*round_info.formulae[generator_formula], *(round_info.statespace), path,
     state);

  path_evaluator.print(stream, indent + 4);

  printTextBlock(stream, indent + 2,
		 string(" \n The formula is ") + (result ? "" : "not ")
                 + "satisfied in the execution, which should therefore be "
                 + (result ? "accep" : "rejec")
                 + "ted by all Büchi automata that represent the formula "
                   "correctly. This suggests an error in implementation "
		 + (path_compare
		    ? configuration.algorithmString(algorithm1)
		    : configuration.algorithmString(result
						    ? rejecting_algorithm
						    : accepting_algorithm))
                 + ".\n",
		 78);

  if (!result)
    printAcceptingCycle(stream, indent + 2, accepting_algorithm,
			automaton_prefix, automaton_cycle,
			*(test_results[accepting_algorithm].
			    automaton_stats[formula].buchi_automaton));
}

/* ========================================================================= */
void printConsistencyAnalysisResults
  (ostream& stream, int indent,
   const vector<string, ALLOC(string) >& input_tokens)
/* ----------------------------------------------------------------------------
 *
 * Description:   Implements the user command `consistencyanalysis', i.e.,
 *                analyzes a discrepancy detected in the model checking result
 *                consistency check for an implementation.
 *
 * Arguments:     stream        --  A reference to an output stream.
 *                indent        --  Number of spaces to leave on the left of
 *                                  the output.
 *                input_tokens  --  A reference to a vector containing the
 *                                  arguments of the command.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::badbit | ios::failbit);

  if (!configuration.global_options.do_cons_test)
    throw CommandErrorException("This command is available only when the "
				"model checking result consistency check is "
				"enabled.");

  unsigned long int algorithm_id = parseNumber(input_tokens[1]);

  verifyNumber(algorithm_id, round_info.number_of_translators,
	       "No such implementation");

  if (test_results[algorithm_id].consistency_check_result == -1)
  {
    printTextBlock(stream, indent,
		   "Model checking result consistency check was not performed "
		   "on implementation "
		   + configuration.algorithmString(algorithm_id)
		   + ".\n",
		   78);
    return;
  }

  if (test_results[algorithm_id].consistency_check_result == 1)
  {
    printTextBlock(stream, indent,
		   "Implementation "
		   + configuration.algorithmString(algorithm_id)
		   + " passed the model checking result consistency check.\n",
		   78);
    return;
  }

  StateSpace::size_type state;
  int formula = (configuration.formula_options.output_mode
		   == Configuration::NNF
		 ? 0
		 : 2);

  estream << string(indent, ' ') + "Consistency check result analysis:\n"
             + string(indent + 2, ' ') + "Implementation:   "
             + configuration.algorithmString(algorithm_id) + '\n'
             + string(indent + 2, ' ') + "Positive formula: "
	  << *round_info.formulae[formula]
	  << "\n\n";

  if (input_tokens.size() == 2)
  {
    /*
     *  If no state identifier was given as a parameter, search for a state in
     *  which the consistency check failed.
     */

    for (state = 0;
	 test_results[algorithm_id].automaton_stats[0].
	   emptiness_check_result.test(state)
	   || test_results[algorithm_id].automaton_stats[1].
	        emptiness_check_result.test(state);
	 ++state)
      ;
  }
  else
  {
    /*
     *  Otherwise use the state given as a parameter for the command.
     */

    state = parseNumber(input_tokens[2]);
    verifyNumber(state, round_info.statespace->size(), "No such state");

    if (state >= round_info.real_emptiness_check_size)
    {
      printTextBlock(stream, indent,
		     "Model checking result consistency check was not "
		     "performed on implementation "
		     + configuration.algorithmString(algorithm_id)
		     + " in state " + toString(state) + " of the state "
		       "space.\n",
		     78);
      return;
    }
    if (test_results[algorithm_id].automaton_stats[0].
	  emptiness_check_result.test(state)
	|| test_results[algorithm_id].automaton_stats[1].
	     emptiness_check_result.test(state))
    {
      printTextBlock(stream, indent,
		     "No discrepancy detected in the model checking result "
		     "consistency check on implementation "
		     + configuration.algorithmString(algorithm_id)
		     + " in state " + toString(state) + " of the state "
		       "space.\n",
		     78);
      return;
    }
  }

  vector<StateSpace::size_type, ALLOC(StateSpace::size_type) > path;
  deque<StateSpace::size_type, ALLOC(StateSpace::size_type) > prefix, cycle;
  map<StateSpace::size_type, StateSpace::size_type,
      less<StateSpace::size_type>, ALLOC(StateSpace::size_type) >
    ordering;
  StateSpace::size_type state_count = 0;
  StateSpace::size_type loop_state;

  /*
   *  Construct a vector of state identifiers representing a path by
   *  traversing the state space until some state is encountered twice.
   */

  while (1)
  {
    path.push_back(state);
    ordering[state] = state_count;
    state_count++;

    state = (*((*round_info.statespace)[state].edges().begin()))->targetNode();

    if (ordering.find(state) != ordering.end())
      break;
  }

  loop_state = ordering[state];

  for (StateSpace::size_type s = 0; s < loop_state; s++)
    prefix.push_back(path[s]);

  for (StateSpace::size_type s = loop_state; s < path.size(); s++)
    cycle.push_back(path[s]);

  estream << string(indent + 2, ' ') + "Execution M:\n";
  printPath(stream, indent + 4, prefix, cycle, *(round_info.statespace));

  estream << '\n' + string(indent + 2, ' ')
             + "Analysis of the formula in the execution:\n";

  Ltl::PathEvaluator path_evaluator;
  bool result = path_evaluator.evaluate
    (*round_info.formulae[formula], *(round_info.statespace), path,
     loop_state);

  path_evaluator.print(stream, indent + 4);

  printTextBlock(stream, indent + 2,
		 string(" \n The formula is ") + (result ? "" : "not ")
                 + "satisfied in the execution. It seems that the automaton "
		   "constructed for the "
		 + (result ? "posi" : "nega")
                 + "tive formula rejects the execution incorrectly.\n",
		 78);
}
 
/* ========================================================================= */
void printAutomatonAnalysisResults
  (ostream& stream, int indent, unsigned long int algorithm1,
   unsigned long int algorithm2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Implements the user command `buchianalysis', i.e., analyzes
 *                an inconsistency detected in the intersection emptiness check
 *                for two Büchi automata constructed for the formula and its
 *                negation.
 *
 * Arguments:     stream       --  A reference to an output stream.
 *                indent       --  Number of spaces to leave on the left of the
 *                                 output.
 *                algorithm1,  --  Identifiers of the algorithms for which the
 *                algorithm2       should be performed.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::badbit | ios::failbit);

  if (!configuration.global_options.do_intr_test)
    throw CommandErrorException("This command is available only when the "
				"Büchi automata intersection emptiness check "
				"is enabled.");

  verifyNumber(algorithm1, round_info.number_of_translators,
	       "No such implementation");
  verifyNumber(algorithm2, round_info.number_of_translators,
	       "No such implementation");

  int test_result
    = test_results[algorithm1].automaton_stats[0].
        buchi_intersection_check_stats[algorithm2];

  if (test_result != 0)
  {
    printTextBlock(stream, indent,
		   string("The automata intersection check was ")
		   + (test_result == -1 ? "not performed" : "successful") + ' '
	           + (algorithm1 == algorithm2 ? "o" : "betwee")
                   + "n implementation"
                   + (algorithm1 == algorithm2 ? "" : "s") + ' '
                   + configuration.algorithmString(algorithm1)
                   + (algorithm1 == algorithm2
		      ? ""
		      : " (positive formula) and "
		        + configuration.algorithmString(algorithm2)
		        + " (negative formula)")
		   + ".\n",
		   78);

    return;
  }

  int formula
    = (configuration.formula_options.output_mode == Configuration::NNF
       ? 0
       : 2);

  estream << string(indent, ' ')
             + "Automata intersection emptiness check analysis:\n"
             + string(indent + 2, ' ') + "Positive formula: ";

  round_info.formulae[formula]->print(estream);

  estream << '\n' + string(indent + 2, ' ') + "Negative formula: ";
  round_info.formulae[formula + 1]->print(estream);

  estream << '\n' + string(indent + 2, ' ') + "Implementation"
             + (algorithm1 != algorithm2 ? "s:" : ": ") + "  "
             + configuration.algorithmString(algorithm1);

  if (algorithm1 != algorithm2)
    estream << " (positive formula)\n" + string(indent + 20, ' ')
               + configuration.algorithmString(algorithm2)
               + " (negative formula)";

  estream << "\n\n";
  estream.flush();

  /*
   *  Compute the intersection of the two automata.
   */
  
  BuchiAutomaton* a = 0;

  try
  {
    map<BuchiAutomaton::size_type, BuchiAutomaton::StateIdPair,
        less<BuchiAutomaton::size_type>, ALLOC(BuchiAutomaton::StateIdPair) >
      intersection_state_mapping;

    try
    {
      printText("<computing the intersection of two automata>", 0, 2);

      a = BuchiAutomaton::intersect
	    (*(test_results[algorithm1].automaton_stats[0].buchi_automaton),
	     *(test_results[algorithm2].automaton_stats[1].buchi_automaton),
	     &intersection_state_mapping);
    }
    catch (...)
    {
      printText(" error\n", 0);
      throw;
    }

    printText(" ok\n", 0);

    /*
     *  Search the intersection automaton for an accepting execution. This is
     *  done `indirectly' as follows:
     *
     *      1.  Convert the intersection automaton into a StateSpace, i.e.,
     *          construct a StateSpace which shares a similar transition
     *          relation with the automaton.
     *      2.  Construct a `dummy' single-state Büchi automaton which
     *          accepts all inputs.
     *      3.  Compute the synchronous product of the state space and the
     *          automaton. The obtained product automaton again has the same
     *          transition relation as the original intersection automaton
     *          (state identifiers may have been permuted; however, the
     *          product automaton contains information about the
     *          correspondence between the state identifiers of the product 
     *          automaton and the original intersection automaton).
     *      4.  Switch the roles of the state space and the Büchi automaton in
     *          the product to effectively transfer state acceptance
     *          information to the product automaton from the automaton
     *          intersection.
     *      5.  Search an accepting cycle in the product automaton. This then
     *          corresponds to an accepting execution of the intersection
     *          automaton.
     */
    
    StateSpace automaton_as_statespace
      (configuration.formula_options.formula_generator.
         number_of_available_variables,
       a->size());

    /*
     *  1.
     */

    for (BuchiAutomaton::size_type state = 0; state < a->size(); state++)
    {
      for (GraphEdgeContainer::const_iterator transition
	     = (*a)[state].edges().begin();
	   transition != (*a)[state].edges().end();
	   ++transition)
	automaton_as_statespace.connect(state, (*transition)->targetNode());
    }

    /*
     *  2.
     */

    BuchiAutomaton dummy_automaton(1, 0, 0);
    dummy_automaton.connect(0, 0, &Ltl::True::construct());

    /*
     *  3.
     */

    ProductAutomaton p;
    p.computeProduct(dummy_automaton, automaton_as_statespace, false);

    /*
     *  4.
     */

    p.buchi_automaton = a;
    p.statespace_size = 1;

    /*
     *  5.
     */

    pair<deque<BuchiAutomaton::StateIdPair,
               ALLOC(BuchiAutomaton::StateIdPair) >,
         deque<BuchiAutomaton::StateIdPair,
               ALLOC(BuchiAutomaton::StateIdPair) > >
      execution;

    try
    {
      printText("<searching for accepting execution in automata intersection>",
		0, 2);

      p.findAcceptingExecution(0, execution);
    }
    catch (...)
    {
      printText(" error\n", 0);
      throw;
    }

    printText(" ok\n\n", 0);

    /*
     *  Extract the state identifiers belonging to the execution of the
     *  intersection automaton from the result.
     */

    vector<StateSpace::size_type, ALLOC(StateSpace::size_type) > path;

    for (deque<BuchiAutomaton::StateIdPair,
	       ALLOC(BuchiAutomaton::StateIdPair) >::const_iterator
	   state = execution.first.begin();
	 state != execution.first.end();
	 ++state)
      path.push_back(state->first);

    const vector<StateSpace::size_type, ALLOC(StateSpace::size_type) >
      ::size_type loop_pos = path.size();

    for (deque<BuchiAutomaton::StateIdPair,
	       ALLOC(BuchiAutomaton::StateIdPair) >::const_iterator
	   state = execution.second.begin();
	 state != execution.second.end();
	 ++state)
      path.push_back(state->first);

    /*
     *  Construct an execution accepted by both of the automata. This is done
     *  by giving suitable truth assignments for the atomic propositions in
     *  'path.size()' states, where `path' corresponds to an accepting run of
     *  the intersection automaton. (The state space representing the
     *  intersection automaton is reused for this purpose, since it is not
     *  needed any longer.) In addition, `prefix' and `cycle' (required for
     *  displaying the execution) are built to refer to the reused states.
     */

    deque<StateSpace::size_type, ALLOC(StateSpace::size_type) > prefix, cycle;

    /*
     *  Ensure that the state space is large enough to contain the execution
     *  (the execution may pass several times through a state in the
     *  intersection automaton).
     */

    if (automaton_as_statespace.size() < path.size())
      automaton_as_statespace.expand
	(path.size() - automaton_as_statespace.size());

    path.push_back(path[loop_pos]); /* use the first state of the cycle as a
				     * temporary sentinel element
				     */

    for (vector<StateSpace::size_type, ALLOC(StateSpace::size_type) >
	   ::size_type state = 0;
	 state + 1 < path.size();
	 ++state)
    {
      GraphEdgeContainer::const_iterator transition;

      for (transition = (*a)[path[state]].edges().begin();
	   (*transition)->targetNode() != path[state + 1];
	   ++transition)
	;

      automaton_as_statespace[state].positiveAtoms()
	= static_cast<BuchiAutomaton::BuchiTransition*>(*transition)
            ->guard().findPropositionalModel
	        (configuration.formula_options.formula_generator.
		   number_of_available_variables);

      (state < loop_pos ? prefix : cycle).push_back(state);
    }

    path.pop_back(); /* remove the sentinel element */

    delete a;
    a = 0;

    /*
     *  Display the input sequence accepted by both automata.
     */

    estream << string(indent + 2, ' ')
               + "Execution M accepted by both automata:\n";

    printPath(stream, indent + 4, prefix, cycle, automaton_as_statespace);

    estream << '\n';

    /*
     *  For each of the original automata, display the accepting runs that
     *  these automata have on the input sequence.
     */

    deque<BuchiAutomaton::size_type, ALLOC(BuchiAutomaton::size_type) >
      aut_prefix;

    deque<BuchiAutomaton::size_type, ALLOC(BuchiAutomaton::size_type) >
      aut_cycle;

    for (int i = 0; i < 2; i++)
    {
      aut_prefix.clear();
      aut_cycle.clear();

      deque<BuchiAutomaton::size_type, ALLOC(BuchiAutomaton::size_type) >*
	new_execution_states = &aut_prefix;

      for (vector<StateSpace::size_type, ALLOC(StateSpace::size_type) >
	     ::size_type state_id = 0;
	   state_id < path.size();
	   ++state_id)
      {
	if (state_id == loop_pos)
	  new_execution_states = &aut_cycle;
	new_execution_states->push_back
	  (i == 0
	   ? intersection_state_mapping[path[state_id]].first
	   : intersection_state_mapping[path[state_id]].second);
      }

      synchronizePrefixAndCycle(aut_prefix, aut_cycle);

      printAcceptingCycle(stream, indent + 2,
			  (i == 0 ? algorithm1 : algorithm2),
			  aut_prefix, aut_cycle,
			  *(test_results[(i == 0 ? algorithm1 : algorithm2)].
			      automaton_stats[i].buchi_automaton));
    }

    /*
     *  Normalize the state identifiers in `path' to refer to the states that
     *  give the valuations for atomic propositions along the execution.
     */

    for (vector<StateSpace::size_type, ALLOC(StateSpace::size_type) >
	   ::size_type state = 0;
	 state < path.size();
	 ++state)
      path[state] = state;

    /*
     *  Display a proof or a refutation for the formula in the execution.
     */

    estream << string(indent + 2, ' ')
	       + "Analysis of the positive formula in the execution M:\n";

    Ltl::PathEvaluator path_evaluator;
    bool result = path_evaluator.evaluate
      (*round_info.formulae[formula], automaton_as_statespace, path, loop_pos);

    path_evaluator.print(stream, indent + 4);

    printTextBlock(stream, indent + 2,
		   " \n The positive formula is "
		   + string(result ? "" : "not ")
		   + "satisfied in the execution. This suggests that the "
                     "Büchi automaton constructed for the "
		   + (result ? "nega" : "posi") + "tive formula "
		   + (algorithm1 == algorithm2
		      ? ""
		      : "(the automaton constructed by implementation "
			+ configuration.algorithmString(result ? algorithm2
							       : algorithm1)
		        + ") ")
		   + "is incorrect.\n",
		   78);
  }
  catch (...)
  {
    if (a != 0)
      delete a;
  }
}

/* ========================================================================= */
void printPath
  (ostream& stream, int indent,
   const deque<StateSpace::size_type, ALLOC(StateSpace::size_type) >& prefix,
   const deque<StateSpace::size_type, ALLOC(StateSpace::size_type) >& cycle,
   const StateSpace& path)
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes information about a single execution path to a stream.
 *
 * Arguments:     stream  --  A reference to an output stream.
 *                indent  --  Number of spaces to leave to the left of output.
 *                prefix  --  A reference to a constant deque of state
 *                            identifiers forming the prefix of the execution.
 *                cycle   --  A reference to a constant deque of state
 *                            identifiers forming the infinitely repeating
 *                            cycle in the execution.
 *                path    --  A reference to the constant state space from
 *                            which the state identifiers in the deques are
 *                            taken.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::badbit | ios::failbit);

  const deque<StateSpace::size_type, ALLOC(StateSpace::size_type) >*
    execution_states;

  for (int counter = 0; counter < 2; counter++)
  {
    estream << string(indent, ' ');

    if (counter == 0)
    {
      execution_states = &prefix;
      estream << "prefix:";
    }
    else
    {
      execution_states = &cycle;
      estream << "cycle: ";
    }

    estream << string(6, ' ') + "< ";

    if (!execution_states->empty())
    {
      bool first_printed;

      for (deque<StateSpace::size_type, ALLOC(StateSpace::size_type) >
	     ::const_iterator execution_state = execution_states->begin();
	   execution_state != execution_states->end();
	   ++execution_state)
      {
	if (execution_state != execution_states->begin())
	  estream << ",\n" + string(indent + 15, ' ');

	estream << 's' + toString(*execution_state) + "  {";

	first_printed = false;

	for (unsigned long int proposition = 0;
	     proposition < path.numberOfPropositions();
	     proposition++)
	{
	  if (path[*execution_state].positiveAtoms().test(proposition))
	  {
	    if (first_printed)
	      estream << ", ";
	    else
	      first_printed = true;

	    estream << 'p' + toString(proposition);
	  }
	}

	estream << '}';
      }
    }
    else
      estream << "empty";
    
    estream << " >\n";
  }
}

/* ========================================================================= */
void printAcceptingCycle
  (ostream& stream, int indent,
   vector<Configuration::AlgorithmInformation,
          ALLOC(Configuration::AlgorithmInformation) >::size_type
     algorithm_id,		
   const deque<BuchiAutomaton::size_type, ALLOC(BuchiAutomaton::size_type) >&
     prefix,
   const deque<BuchiAutomaton::size_type, ALLOC(BuchiAutomaton::size_type) >&
     cycle,
   const BuchiAutomaton& automaton)
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes information about a single automaton execution to a
 *                stream.
 *
 * Arguments:     stream        --  A reference to an output stream.
 *                indent        --  Number of spaces to leave to the left of
 *                                  output.
 *                algorithm_id  --  Identifier for an algorithm.
 *                prefix        --  A reference to a constant deque of state
 *                                  identifiers forming the prefix of the
 *                                  execution.
 *                cycle         --  A reference to a constant deque of state
 *                                  identifiers forming the infinitely
 *                                  repeating cycle in the execution.
 *                automaton     --  A reference to a constant BuchiAutomaton
 *                                  from which the state identifiers in the
 *                                  deques are taken.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::badbit | ios::failbit);

  const deque<BuchiAutomaton::size_type, ALLOC(BuchiAutomaton::size_type) >*
    execution_states;
  bool first_printed;

  printTextBlock(stream, indent,
		 "On input M, the automaton constructed by implementation "
                 + configuration.algorithmString(algorithm_id) + " (with "
                 + toString(automaton.numberOfAcceptanceSets())
                 + " acceptance set"
                 + (automaton.numberOfAcceptanceSets() == 1 ? "" : "s")
                 + ") has the following accepting execution:",
		 78);

  for (int counter = 0; counter < 2; counter++)
  {
    estream << string(indent + 2, ' ');

    if (counter == 0)
    {
      estream << "prefix";
      execution_states = &prefix;
    }
    else
    {
      estream << "cycle";
      execution_states = &cycle;
    }

    estream << ":\n";

    string execution_string = "<";

    if (!execution_states->empty())
    {
      first_printed = false;
	  
      for (deque<BuchiAutomaton::size_type,
	         ALLOC(BuchiAutomaton::size_type) >::const_iterator
	     execution_state = execution_states->begin();
	   execution_state != execution_states->end();
	   ++execution_state)
      {
	if (first_printed)
	  execution_string += ", ";
	else
	  first_printed = true;
	execution_string += toString(*execution_state);

	if (counter == 1)
	{
	  bool first_acceptance_set_printed = false;

	  for (unsigned long int accept_set = 0;
	       accept_set < automaton.numberOfAcceptanceSets();
	       accept_set++)
	  {
	    if (automaton[*execution_state].acceptanceSets().test(accept_set))
	    {
	      if (first_acceptance_set_printed)
		execution_string += ", ";
	      else
	      {
		first_acceptance_set_printed = true;
		execution_string += " [acceptance sets: {";
	      }
	      execution_string += toString(accept_set);
	    }
	  }

	  if (first_acceptance_set_printed)
	    execution_string += "}]";
	}
      }
    }
    else
      execution_string += "empty";

    execution_string += ">";

    printTextBlock(stream, indent + 4, execution_string, 78);
  }

  estream << '\n';
  estream.flush();
}
 
/* ========================================================================= */
void printBuchiAutomaton
  (ostream& stream, int indent, bool formula_type,
   vector<string, ALLOC(string) >& input_tokens,
   Graph::GraphOutputFormat fmt)
/* ----------------------------------------------------------------------------
 *
 * Description:   Implements the user command `buchi', i.e., writes information
 *                about a set of states of a Büchi automaton computed using a
 *                given algorithm to a stream.
 *
 * Arguments:     stream        --  A reference to an output stream.
 *                indent        --  Number of spaces to leave on the left of
 *                                  the output.
 *                formula_type  --  Determines the formula to be translated
 *                                  into a Büchi automaton.
 *                input_tokens  --  A reference to a vector containing the
 *                                  arguments of the user command (the
 *                                  algorithm and automaton state numbers).
 *                fmt           --  Determines the format of output.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::badbit | ios::failbit);

  unsigned long int algorithm = parseNumber(input_tokens[1]);

  verifyNumber(algorithm, round_info.number_of_translators,
	       "No such implementation");

  int formula
    = (configuration.formula_options.output_mode == Configuration::NNF
       ? 0 : 2)
      + (formula_type ? 0 : 1);

  if (!test_results[algorithm].automaton_stats[formula_type ? 0 : 1].
         buchiAutomatonComputed())
  {
    printTextBlock(stream, indent,
		   "No automaton was generated using implementation "
		   + configuration.algorithmString(algorithm)
		   + " for the formula `"
		   + toString(*round_info.formulae[formula])
		   + "'.\n",
		   78);
    return;
  }

  const BuchiAutomaton* automaton
    = test_results[algorithm].automaton_stats[formula_type ? 0 : 1].
	buchi_automaton;

  if (fmt == Graph::NORMAL)
  {
    /*
     *  Display information about the states of the automaton. If no state
     *  list was given, display the whole automaton.
     */

    estream << string(indent, ' ') + "Büchi automaton information:\n"
               + string(indent + 2, ' ') + "Formula: ";

    round_info.formulae[formula]->print(estream);

    estream << '\n' + string(indent + 2, ' ') + "Implementation: "
               + configuration.algorithmString(algorithm) + '\n'
	       + string(indent + 2, ' ') + "Number of acceptance sets: "
               + toString(automaton->numberOfAcceptanceSets()) + '\n';

    if (automaton->empty())
      estream << string(indent + 2, ' ') + "The automaton is empty.\n";
    else
    {
      set<unsigned long int, less<unsigned long int>,
                                  ALLOC(unsigned long int) > states;

      if (input_tokens.size() == 2)
	input_tokens.push_back("*");

      parseInterval(input_tokens[2], states, 0, automaton->size() - 1);

      for (set<unsigned long int, less<unsigned long int>,
               ALLOC(unsigned long int) >::const_iterator
	     state = states.begin();
	   state != states.end(); ++state)
      {
	verifyNumber(*state, automaton->size(),
		     "State identifier out of range");

	estream << string(indent + 2, ' ') + "State " + toString(*state)
                   + (*state == automaton->initialState()
		      ? " (initial state)" : "") + ":\n";
	automaton->node(*state).print(stream, indent + 4, Graph::NORMAL,
				      automaton->numberOfAcceptanceSets());
      }
    }
  }
  else if (fmt == Graph::DOT)
    automaton->print(stream, indent, Graph::DOT);

  estream << '\n';
  estream.flush();
}

/* ========================================================================= */
void evaluateFormula
  (ostream& stream, int indent, bool formula_type, 
   vector<string, ALLOC(string) >& input_tokens)
/* ----------------------------------------------------------------------------
 *
 * Description:   Implements the user command `evaluate', i.e., tells whether
 *                there exists an accepting execution beginning at some system
 *                state according to the product automaton constructed from a
 *                Büchi automaton computed using a given algorithm (or
 *                algorithms).
 *
 * Arguments:     stream        --  A reference to an output stream.
 *                indent        --  Number of spaces to leave on the left of
 *                                  the output.
 *                formula_type  --  Determines the LTL formula to be evaluated.
 *                input_tokens  --  A reference to a vector of strings
 *                                  containing the arguments of the user
 *                                  command.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);

  set<unsigned long int, less<unsigned long int>, ALLOC(unsigned long int) >
    algorithms;
  set<unsigned long int, less<unsigned long int>, ALLOC(unsigned long int) >
    system_states;
  string algorithm_name;
  int formula = (formula_type ? 0 : 1);
  bool show_path_eval_results = false;

  if (!configuration.global_options.do_comp_test
      && !configuration.global_options.do_cons_test)
    throw CommandErrorException("This command is available only when one of "
				"the model checking tests is enabled.");

  if (round_info.statespace == 0)
    throw CommandErrorException("No state space was generated in this test "
                                "round.");
  string algorithm_list;

  if (input_tokens.size() < 2)
  {
    /*
     *  If no list of algorithms was given as an argument, show the results of
     *  all algorithms.
     */

    algorithm_list = "*";
    input_tokens.push_back("*");
    show_path_eval_results
      = ((configuration.global_options.statespace_generation_mode
	    & Configuration::PATH) != 0);
  }
  else
  {
    /*
     *  Otherwise parse the list of algorithms. Test also whether the internal
     *  model checking algorithm was included in the list.
     */

    vector<string, ALLOC(string) > algorithm_list_elements;
    sliceString(input_tokens[1], ",", algorithm_list_elements);

    for (vector<string, ALLOC(string) >::const_iterator alg
	   = algorithm_list_elements.begin();
	 alg != algorithm_list_elements.end();
	 ++alg)
    {
      if (algorithm_list.length() > 0)
	algorithm_list += ',';

      if (*alg == "p")
      {
	if ((configuration.global_options.statespace_generation_mode
	      & Configuration::PATH) == 0)
	  throw CommandErrorException("This feature is available only when "
				      "using paths as state spaces.");
	else
	  show_path_eval_results = true;
      }
      else
	algorithm_list += *alg;
    }
  }

  parseInterval(algorithm_list, algorithms, 0,
		round_info.number_of_translators - 1);

  /*
   *  If no list of states was given, show information about all states.
   *  (If only a local product was computed, show information only about the
   *  initial state.)
   */

  if (input_tokens.size() < 3)
    input_tokens.push_back("*");

  parseInterval(input_tokens[2], system_states, 0,
		round_info.real_emptiness_check_size - 1);

  estream << string(indent, ' ') + "Acceptance information:\n"
             + string(indent + 2, ' ') + "CTL* formula: E ";

  round_info.formulae[configuration.formula_options.output_mode
		        == Configuration::NNF
		      ? formula
		      : 2 + formula]->print(estream);

  estream << '\n';

  for (set<unsigned long int, less<unsigned long int>,
           ALLOC(unsigned long int) >::const_iterator
	 state = system_states.begin();
       state != system_states.end();
       ++state)
  {
    verifyNumber(*state, round_info.real_emptiness_check_size,
		 "State identifier out of range");

    estream << string(indent + 2, ' ') + "State " + toString(*state) + ":\n";

    for (set<unsigned long int, less<unsigned long int>,
             ALLOC(unsigned long int) >::const_iterator
	   algorithm = algorithms.begin();
         algorithm != algorithms.end();
         ++algorithm)
    {
      verifyNumber(*algorithm, round_info.number_of_translators,
		   "No such implementation");

      algorithm_name = configuration.algorithms[*algorithm].name
	                 ->substr(0, 20);
      estream << string(indent + 4, ' ') + toString(*algorithm) + ": "
                 + algorithm_name + ':'
                 + string(21 - algorithm_name.length(), ' ');

      if (!test_results[*algorithm].automaton_stats[formula].
             emptiness_check_performed)
        estream << "emptiness check not performed\n";
      else
        estream << (test_results[*algorithm].automaton_stats[formula].
                      emptiness_check_result[*state]
                    ? "true"
                    : "false")
                << '\n';
    }

    if (show_path_eval_results)
    {
      estream << string(indent + 4, ' ') + "lbtt:                    ";
      if (!test_results[round_info.number_of_translators].
	    automaton_stats[formula].emptiness_check_performed)
	estream << "no model checking result available\n";
      else
	estream << (test_results[round_info.number_of_translators].
	              automaton_stats[formula].emptiness_check_result[*state]
		    ? "true"
		    : "false")
	        << '\n';
    }
  }

  estream << '\n';
  estream.flush();
}    

/* ========================================================================= */
void printFormula(ostream& stream, int indent, bool formula_type)
/* ----------------------------------------------------------------------------
 *
 * Description:   Implements the user command `formula', i.e., displays the
 *                random LTL formula (or its negation) used for generating the
 *                Büchi automata. The formula is shown in its original form and
 *                possibly also in negation normal form if the user has
 *                requested the conversion.
 *
 * Arguments:     stream        --  A reference to an output stream.
 *                indent        --  Number of spaces to leave on the left of
 *                                  the output.
 *                formula_type  --  Identifies the formula to be displayed.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (round_info.formulae[0] == 0)
    throw CommandErrorException("No formulas were generated in this test "
				"round.");

  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);

  estream << string(indent, ' ') + "Formula:" + string(17, ' ');
  round_info.formulae[formula_type ? 2 : 3]->print(estream);

  if (configuration.formula_options.output_mode == Configuration::NNF)
  {
    estream << '\n' + string(indent, ' ') + "In negation normal form: ";
    round_info.formulae[formula_type ? 0 : 1]->print(estream);
  }

  estream << "\n\n";
  estream.flush();
}
  
/* ========================================================================= */
void printCommandHelp
  (ostream& stream, int indent,
   const vector<string, ALLOC(string) >& input_tokens)
/* ----------------------------------------------------------------------------
 *
 * Description:   Implements the user command `help', i.e., gives instructions
 *                on the use of different commands.
 *
 * Arguments:     stream        --  A reference to an output stream.
 *                indent        --  Number of spaces to leave on the left of 
 *                                  the output.
 *                input_tokens  --  A reference to a constant vector of strings
 *                                  containing the arguments of the user
 *                                  command.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);

  TokenType command = _NO_INPUT;

  if (input_tokens.size() > 1)
    command = parseCommand(input_tokens[1]);

  if (command == UNKNOWN)
    estream << string(indent, ' ') + "Unknown command (`" + input_tokens[1]
               + "').\n\n";

  switch (command)
  {
    case ALGORITHMS :
      estream << string(indent, ' ') + "algorithms\n";

      printTextBlock(stream, indent + 4,
                     "List all implementations currently available for "
		     "testing.",
                     78);
      break;

    case BUCHI :
      estream << string(indent, ' ') + "buchi [\"+\"|\"-\"] <implementation> "
                                       "[states|\"dot\"]\n";

      printTextBlock(stream, indent + 4,
                     "Display information about a (list of) state(s) of a "
                     "Büchi automaton constructed either from the positive or "
                     "the negated formula using a given implementation (\"+\" "
                     "denotes the positive formula, \"-\" the negated "
                     "formula). If no formula is specified, use the positive "
                     "formula. If no state list is given, display all the "
                     "states of the automaton. The description of the entire "
                     "automaton can be alternatively obtained in the input "
                     "format of the `dot' tool that can be used to produce a "
                     "graphical representation of the automaton. To do this, "
                     "replace the state list with the string \"dot\".",
                     78);
      break;

    case BUCHIANALYZE :
      estream << string(indent, ' ') + "buchianalysis <implementation 1> "
                                       "<implementation 2>\n";

      printTextBlock(stream, indent + 4,
		     "Analyze a contradiction in the Büchi automata "
                     "intersection emptiness check between the automaton "
                     "constructed from the positive formula by "
		     "`implementation 1' "
                     "and the automaton constructed from the negated formula "
                     "by `implementation 2'.",
		     78);
      break;

    case CONSISTENCYANALYSIS :
      estream << string(indent, ' ')
                 + "consistencyanalysis <implementation> [state]\n";

      printTextBlock(stream, indent + 4,
		     "Analyze a contradiction in the model checking result "
		     "consistency check for a given implementation. The "
		     "optional argument `state' can be used to specify a "
		     "state in which to do the analysis.",
		     78);
      break;

    case CONTINUE :
      estream << string(indent, ' ') + "continue [number of rounds]\n";

      printTextBlock(stream, indent + 4,
                     "Continue testing for a given number of rounds. If the "
		     "number of rounds is omitted, testing will continue "
		     "following the current interactivity mode (for example, "
		     "if the option `--pauseonerror' was given in the command "
		     "line when invoking `lbtt', testing will continue until "
		     "the next test failure or until all tests have "
		     "finished).\n\n"
		     "Note: The output of this command cannot be redirected "
		     "to a file.",
                     78);
      break;

    case DISABLE :
      estream << string(indent, ' ') + "disable [implementations]\n";

      printTextBlock(stream, indent + 4,
		     "Disable testing of a given set of implementations. "
		     "If no implementations are specified, disables all "
		     "implementations.\n\n"
		     "Note: The output of this command cannot be redirected "
		     "to a file.",
		     78);
      break;

    case ENABLE :
      estream << string(indent, ' ') + "enable [implementations]\n";

      printTextBlock(stream, indent + 4,
		     "Enable testing of a given set of implementations. "
		     "If no implementations are specified, enables all "
		     "implementations.\n\n"
		     "Note: The output of this command cannot be redirected "
		     "to a file.",
		     78);
      break;

    case EVALUATE :
      estream << string(indent, ' ') + "evaluate [\"+\"|\"-\"] "
                                       "[implementations] [states]\n";

      printTextBlock(stream, indent + 4,
                     "Tell whether the Büchi automaton constructed from "
                     "a formula (\"+\" denotes the positive formula, \"-\" "
                     "the negated formula) using a given implementation "
		     "accepts some system execution starting from a given "
		     "system state. If no formula is specified, use the "
		     "positive formula. Leaving the list of implementations "
		     "or the list of states unspecified will display the "
		     "results for all implementations or all system states, "
		     "respectively. If using paths as state spaces, the "
		     "special symbol \"p\" in the implementation list will "
		     "show also the results obtained with the internal "
		     "model checking algorithm.",
                     78);
      break;

    case FORMULA :
      estream << string(indent, ' ') + "formula [\"+\"|\"-\"]\n";

      printTextBlock(stream, indent + 4,
                     "Display the LTL formula used in this test round for "
                     "generating Büchi automata (\"+\" denotes the positive "
                     "formula, \"-\" the negated formula). If no formula is "
                     "specified, show the positive formula.",
                     78);
      break;

    case HELP :
      estream << string(indent, ' ') + "help [command]\n";

      printTextBlock(stream, indent + 4,
                     "Display help about a specific command. If no command "
                     "name is given, give a list of all available commands."
                     "\n\nIn command-specific help, arguments in angle "
                     "brackets < > are obligatory, while square bracketed [ ] "
                     "arguments are optional. A vertical bar | denotes "
                     "selection between alternatives. Arguments "
                     "in quotes must be entered literally (without the "
                     "quotes themselves).",
                     78);
      break;

    case INCONSISTENCIES :
      estream << string(indent, ' ') + "inconsistencies [implementations]\n";

      printTextBlock(stream, indent + 4,
                     "List the system states where the model checking result "
                     "consistency check failed for a given (set of) "
                     "implementation(s).",
                     78);
      break;

    case QUIT :
      estream << string(indent, ' ') + "quit\n";

      printTextBlock(stream, indent + 4, "Abort testing.", 78);
      break;

    case RESULTANALYZE :
      estream << string(indent, ' ') + "resultanalysis [\"+\"|\"-\"] "
                                       "<implementation> <implementation> "
                                       "[state]\n";

      printTextBlock(stream, indent + 4,
                     "Analyze a contradiction in the model checking results "
                     "of two implementations on a formula (\"+\" denotes the "
                     "positive formula, \"-\" the negated formula). If no "
                     "formula is specified, use the positive formula. When "
                     "using paths as state spaces, one of implementation "
                     "identifiers can be replaced by the symbol \"p\" to "
                     "analyze the result given by some implementation against "
		     "that given by the internal model checking algorithm. "
		     "The optional argument `state' can be used to specify "
		     "the state of the state space in which to do the "
		     "analysis.",
                     78);
      break;

    case RESULTS :
      estream << string(indent, ' ') + "results [implementations]\n";

      printTextBlock(stream, indent + 4,
                     "Display this round's test results for a given (set of) "
                     "implementation(s). If no implementations are specified, "
		     "show the results for all implementations.",
                     78);
      break;

    case SKIP :
      estream << string(indent, ' ') + "skip [number of rounds]\n";

      printTextBlock(stream, indent + 4,
                     "Skip a given number of rounds (defaults to 1 if no "
                     "number is specified).\n\nNote: The output of this "
                     "command cannot be redirected to a file.",
                     78);
      break;

    case STATESPACE :
      estream << string(indent, ' ') + "statespace [states|\"dot\"]\n";

      printTextBlock(stream, indent + 4,
                     "Display information about a given (list of) system "
                     "state(s). Display the whole state space if no "
                     "states are specified. Alternatively, the state "
                     "space description can be obtained in the input format "
                     "of the `dot' tool that can be used to produce a "
                     "graphical representation of the state space. To do "
                     "this, replace the state list with the string \"dot\".",
                     78);
      break;

    case STATISTICS :
      estream << string(indent, ' ') + "statistics\n";

      printTextBlock(stream, indent + 4,
                     "Display test statistics over all tests performed.",
                     78);
      break;
    
    case VERBOSITY :
      estream << string(indent, ' ') + "verbosity [0-5]\n";

      printTextBlock(stream, indent + 4,
                     "Change the verbosity of the output produced by the "
                     "program. If no value is given, display the current "
                     "setting.\n\nNote: The output of this command cannot "
                     "be redirected to a file.",
                     78);
      break;
    
    default :
      printTextBlock(stream, indent,
                     "List of available commands (use `help "
                     "[command]' for command specific help):\n",
                     78);

      printTextBlock(stream, indent + 2,
                     "algorithms\n"
                     "buchi\n"
                     "buchianalysis\n"
                     "continue\n"
		     "consistencyanalysis\n"
		     "disable\n"
		     "enable\n"
                     "evaluate\n"
		     "formula\n"
                     "help\n"
                     "inconsistencies\n"
                     "quit\n"
                     "resultanalysis\n"
                     "results\n"
                     "skip\n"
                     "statespace\n"
                     "statistics\n"
                     "verbosity\n",
                     78);

      printTextBlock(stream, indent,
                     "Command names can be abbreviated to the shortest prefix "
		     "that identifies the command unambiguously. "
                     "For example, the prefix `h' can be used instead of the "
		     "`help' command.\n\n"
                     "Lists of implementation or state identifiers may be "
                     "specified as comma-separated intervals (with no spaces "
		     "in between), e.g., the command `statespace "
		     "-5,8,14-18,22-' would display the list of all states "
		     "with an identifier less than or equal to 5, state 8 and "
		     "states 14--18, together with the states whose "
		     "identifiers are greater than or equal to 22.\n\n"
                     "The output of most commands can be redirected or "
                     "appended to a file by ending the command line with "
                     "`>filename' or `>>filename', respectively. Optionally, "
                     "the output can be written to a pipe instead by ending "
                     "the command line with `| <command>'.",
                     78);
      break;
  }

  estream << '\n';
  estream.flush();
}

/* ========================================================================= */
void printInconsistencies
  (ostream& stream, int indent, vector<string, ALLOC(string) >& input_tokens)
/* ----------------------------------------------------------------------------
 *
 * Description:   Implements the user command `inconsistencies', i.e., lists
 *                the states where the consistency check failed for a set of
 *                algorithms.
 *
 * Arguments:     stream        --  A reference to an output stream.
 *                indent        --  Number of spaces to leave on the left of
 *                                  the output.
 *                input_tokens  --  A reference to a vector of strings
 *                                  containing the parameters of the user
 *                                  command.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);

  set<unsigned long int, less<unsigned long int>, ALLOC(unsigned long int) >
    number_set;

  if (!configuration.global_options.do_cons_test)
    throw CommandErrorException("This command is available only when the "
				"model checking result consistency check is "
				"enabled.");

  estream << string(indent, ' ') + "Model checking result consistency check "
                                   "results for round "
             + toString(round_info.current_round) + ":\n";

  indent += 2;

  estream << string(indent, ' ') + "Positive formula: "
	  << *round_info.formulae[configuration.formula_options.output_mode
		 		   == Configuration::NNF
				  ? 0
				  : 2]
	  << '\n';

  if (input_tokens.size() == 1)
    input_tokens.push_back("*");

  parseInterval(input_tokens[1], number_set, 0,
                round_info.number_of_translators - 1);

  for (set<unsigned long int, less<unsigned long int>,
           ALLOC(unsigned long int) >::const_iterator
	 algorithm = number_set.begin();
       algorithm != number_set.end(); ++algorithm)
  {
    estream << '\n';

    verifyNumber(*algorithm, round_info.number_of_translators,
		 "No such implementation");

    estream << string(indent, ' ') + configuration.algorithmString(*algorithm)
               + '\n';

    if (test_results[*algorithm].consistency_check_result == -1)
      printTextBlock(stream, indent + 2,
		     "Model checking result consistency check was not "
		     "performed on this implementation.",
		     78);
    else
    {
      if (test_results[*algorithm].consistency_check_result > 0)
	printTextBlock(stream, indent + 2,
		       "The implementation passed the model checking result "
		       "consistency check.",
		       78);
      else
      {
	bool first_printed = false;

	estream << string(indent + 2, ' ') + "Check failed in states\n";

	string resultstring = "{";

        for (unsigned long int state = 0;
             state < round_info.real_emptiness_check_size;
             state++)
        {
          if (!test_results[*algorithm].automaton_stats[0].
                 emptiness_check_result[state]
              && !test_results[*algorithm].automaton_stats[1].
                    emptiness_check_result[state])
	  {
	    if (first_printed)
	      resultstring += ", ";
	    else
	      first_printed = true;

	    resultstring += toString(state);
	  }
        }

        resultstring += "}";

	printTextBlock(stream, indent + 4, resultstring, 78);
      }
    }
  }

  estream << '\n';
  estream.flush();
}

/* ========================================================================= */
void printTestResults
  (ostream& stream, int indent, vector<string, ALLOC(string) >& input_tokens)
/* ----------------------------------------------------------------------------
 *
 * Description:   Implements the user command `results', i.e., displays the
 *                current round's test results for a given (set of)
 *                algorithm(s).
 *
 * Arguments:     stream        --  A reference to an output stream.
 *                indent        --  Number of spaces to leave on the left of
 *                                  the output.
 *                input_tokens  --  A reference to a vector of strings
 *                                  containing the arguments of the user
 *                                  command.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);
  set<unsigned long int, less<unsigned long int>, ALLOC(unsigned long int) >
    number_set;

  estream << string(indent, ' ') + "Test results for round "
             + toString(round_info.current_round) << ":\n";

  if (input_tokens.size() == 1)
    input_tokens.push_back("*");

  parseInterval(input_tokens[1], number_set, 0,
                round_info.number_of_translators - 1);

  for (set<unsigned long int, less<unsigned long int>,
           ALLOC(unsigned long int) >::const_iterator
	 algorithm = number_set.begin();
       algorithm != number_set.end(); ++algorithm)
  {
    verifyNumber(*algorithm, round_info.number_of_translators,
		 "No such implementation");

    printAllStats(stream, indent, *algorithm);

    if (configuration.global_options.do_comp_test)
    {
      estream << string(indent, ' ')
                 + "Model checking result cross-comparison:\n";
      printCrossComparisonStats(stream, indent + 2, *algorithm);
    }

    if (configuration.global_options.do_intr_test)
    {
      estream << string(indent, ' ')
                 + "Büchi automata intersection emptiness check:\n";
      printBuchiIntersectionCheckStats(stream, indent + 2, *algorithm);
    }
    estream.flush();
  }
}

/* ========================================================================= */
void printStateSpace
  (ostream& stream, int indent, vector<string, ALLOC(string) >& input_tokens,
   Graph::GraphOutputFormat fmt)
/* ----------------------------------------------------------------------------
 *
 * Description:   Implements the user command `statespace', i.e., displays
 *                information about a given set of states of the state space.
 *
 * Arguments:     stream        --  A reference to an output stream.
 *                indent        --  Number of spaces to leave on the left of
 *                                  the output.
 *                input_tokens  --  A reference to a vector of strings
 *                                  containing the arguments of the user
 *                                  command.
 *                fmt           --  Determines the format of output.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);
  set<unsigned long int, less<unsigned long int>, ALLOC(unsigned long int) >
    number_set;

  if (!configuration.global_options.do_comp_test
      && !configuration.global_options.do_cons_test)
    throw CommandErrorException("This command is available only when one of "
				"the model checking tests is enabled.");

  if (round_info.statespace == 0)
    throw CommandErrorException("No state space was generated in this test "
                                "round.");

  if (fmt == Graph::NORMAL)
  {
    estream << string(indent, ' ') + "State space information:\n";

    if (input_tokens.size() == 1)
      input_tokens.push_back("*");

    parseInterval(input_tokens[1], number_set, 0,
		  round_info.statespace->size() - 1);

    for (set<unsigned long int, less<unsigned long int>,
             ALLOC(unsigned long int) >::const_iterator
	   state = number_set.begin();
         state != number_set.end(); ++state)
    {
      verifyNumber(*state, round_info.statespace->size(),
		   "State identifier out of range");

      estream << string(indent, ' ') + "State " + toString(*state)
	         + (*state == round_info.statespace->initialState()
		    ? " (initial state)"
		    : "")
	         + ":\n";

      (*(round_info.statespace))[*state].print
	(stream, indent + 2, fmt,
	 round_info.statespace->numberOfPropositions());
    }
  }
  else if (fmt == Graph::DOT)
    round_info.statespace->print(stream, indent, Graph::DOT);

  estream << '\n';
  estream.flush();
}

/* ========================================================================= */
void changeVerbosity(const vector<string, ALLOC(string) >& input_tokens)
/* ----------------------------------------------------------------------------
 *
 * Description:   Implements the user command `verbosity', i.e., displays or
 *                changes the output verbosity level.
 *
 * Argument:      input_tokens  --  A reference to a constant vector of strings
 *                                  containing the argument of the command.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (input_tokens.size() == 1)
    printText("Output verbosity is currently set to "
	      + toString(configuration.global_options.verbosity)
	      + ".\n\n",
	      0,
	      2);
  else
  {
    unsigned long int new_verbosity = parseNumber(input_tokens[1]);
    if (new_verbosity > 5)
      throw CommandErrorException("Verbosity level must be between 0 and 5 "
                                  "inclusive.");

    configuration.global_options.verbosity = new_verbosity;

    printText("Output verbosity level set to "
	      + toString(new_verbosity)
	      + ".\n\n",
	      0,
	      2);
  }
}

/* ========================================================================= */
void changeAlgorithmState
  (vector<string, ALLOC(string) >& input_tokens, bool enable)
/* ----------------------------------------------------------------------------
 *
 * Description:   Changes the enabledness of a set of algorithms used in the
 *                tests.
 *
 * Argument:      input_tokens  --  A reference to a constant vector of strings
 *                                  containing the argument of the command.
 *                enable        --  Determines whether the algorithms are to be
 *                                  enabled or disabled.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  set<unsigned long int, less<unsigned long int>, ALLOC(unsigned long int) >
    algorithms;

  if (input_tokens.size() < 2)
    input_tokens.push_back("*");

  parseInterval(input_tokens[1], algorithms, 0,
                round_info.number_of_translators - 1);

  for (set<unsigned long int, less<unsigned long int>,
           ALLOC(unsigned long int) >::const_iterator
	 alg = algorithms.begin();
       alg != algorithms.end(); alg++)
  {
    verifyNumber(*alg, round_info.number_of_translators,
		 "No such implementation");

    printText(string(enable ? "En" : "Dis")
	      + "abling implementation "
	      + configuration.algorithmString(*alg)
	      + ".\n",
	      0,
	      2);
    
    configuration.algorithms[*alg].enabled = enable;
  }

  round_info.cout << '\n';
  round_info.cout.flush();
}

}
