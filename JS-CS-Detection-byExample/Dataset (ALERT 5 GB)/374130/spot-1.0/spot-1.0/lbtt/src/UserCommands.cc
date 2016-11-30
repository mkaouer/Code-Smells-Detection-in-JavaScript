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
#include "BuchiProduct.h"
#include "DispUtil.h"
#include "Exception.h"
#include "PathEvaluator.h"
#include "Product.h"
#include "SharedTestData.h"
#include "StatDisplay.h"
#include "StateSpaceProduct.h"
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
unsigned long int parseAlgorithmId(const string& id)
/* ----------------------------------------------------------------------------
 *
 * Description:   Parses an algorithm identifier (either a symbolic or a
 *                numeric one).
 *
 * Argument:      id  --  String containing the identifier.
 *
 * Returns:       The numeric identifier of the algorithm. Throws a
 *                CommandErrorException if the identifier is not recognizable
 *                as a proper algorithm identifier.
 *
 * ------------------------------------------------------------------------- */
{
  unsigned long int result;
  string unquoted_id = unquoteString(id);

  try
  {
    result = parseNumber(unquoted_id);
    verifyNumber(result, round_info.number_of_translators,
		 "Implementation identifier out of range");
  }
  catch (const NotANumberException&)
  {
    map<string, unsigned long int>::const_iterator id_finder
      = configuration.algorithm_names.find(unquoted_id);
    if (id_finder == configuration.algorithm_names.end())
      throw CommandErrorException
	      ("Unknown implementation identifier (`" + unquoted_id + "').");
    result = id_finder->second;
  }

  return result;
}

/* ========================================================================= */
void parseAlgorithmIdList(const string& ids, IntervalList& algorithms)
/* ----------------------------------------------------------------------------
 *
 * Description:   Parses a list of algorithm identifiers specified either as
 *                comma-separated intervals or algorithm identifiers.
 *
 * Arguments:     ids         --  A constant reference to a string containing
 *                                the list of algorithm identifiers.
 *                algorithms  --  A reference to an IntervalList for storing
 *                                the numeric identifiers of the algorithms.
 *
 * Returns:       Nothing. Throws a CommandErrorException if the identifier
 *                list includes a string not recognizable as an algorithm
 *                identifier.
 *
 * ------------------------------------------------------------------------- */
{
  /*
   *  Make a copy of `ids' in which each comma (',') within double quotes is
   *  substituted with a newline. This is necessary to handle symbolic
   *  algorithm identifiers with commas correctly.
   */

  string id_string = substituteInQuotedString(ids, ",", "\n", INSIDE_QUOTES);

  try
  {
    vector<string> nonnumeric_algorithm_ids;

    parseIntervalList(id_string, algorithms, 0,
		      round_info.number_of_translators - 1,
		      &nonnumeric_algorithm_ids);

    for (vector<string>::iterator id = nonnumeric_algorithm_ids.begin();
	 id != nonnumeric_algorithm_ids.end();
	 ++id)
    {
      *id = unquoteString(substituteInQuotedString(*id, "\n", ","));
      map<string, unsigned long int>::const_iterator
	id_finder = configuration.algorithm_names.find(*id);
      if (id_finder == configuration.algorithm_names.end())
	throw CommandErrorException
	        ("Unknown implementation identifier (`" + *id + "').");
      algorithms.merge(id_finder->second);
    }
  }
  catch (const IntervalRangeException& e)
  {
    throw CommandErrorException
      (string("Implementation identifier out of range (")
       + toString(e.getNumber())
       + ").");
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

  estream.flush();
}

/* ========================================================================= */
void printCrossComparisonAnalysisResults
  (ostream& stream, int indent, bool formula_type,
   const vector<string>& input_tokens)
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

  algorithm1 = parseAlgorithmId(input_tokens[1]);
  algorithm2 = parseAlgorithmId(input_tokens[2]);

  if (algorithm1 == algorithm2)
    throw CommandErrorException("Implementation identifiers must be "
				"different.");

  /* 
   *  Arrange the algorithm identifiers such that `algorithm1' never refers to
   *  the internal model checking algorithm (swap `algorithm1' and `algorithm2'
   *  if necessary).
   */

  if (configuration.isInternalAlgorithm(algorithm1))
  {
    path_compare = true;
    algorithm1 ^= algorithm2;
    algorithm2 ^= algorithm1;
    algorithm1 ^= algorithm2;
  }

  if (configuration.isInternalAlgorithm(algorithm2))
    path_compare = true;

  if (path_compare
      && !(configuration.global_options.statespace_generation_mode
	     & Configuration::PATH))
    throw CommandErrorException("This feature is available only when using "
				"paths as state spaces.");

  int formula = (formula_type ? 0 : 1);
  int generator_formula = formula;

  if (configuration.formula_options.output_mode != Configuration::NNF)
    generator_formula += 2;

  const AutomatonStats* stats1;
  const AutomatonStats* stats2;

  stats1 = &test_results[algorithm1].automaton_stats[formula];
  stats2 = &test_results[algorithm2].automaton_stats[formula];

  if (!stats1->crossComparisonPerformed(algorithm2))
  {
    printTextBlock(stream, indent,
		   "Model checking result cross-comparison was not "
		   "performed between "
                   + configuration.algorithmString(algorithm1)
		   + " and " + configuration.algorithmString(algorithm2)
                   + ".",
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
                   + configuration.algorithmString(algorithm2) + ".",
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

    verifyNumber(state, round_info.statespace->size(),
		 "State identifier out of range");

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
		     + " of the state space.",
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
                     + " of the state space.",
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

  ::Graph::Product<Graph::StateSpaceProduct>::Witness witness;

  if (!path_compare || accepting_algorithm == algorithm1)
  {
    /*
     *  Search the product automaton for an accepting cycle.
     */

    const BuchiAutomaton& automaton
      =  *test_results[accepting_algorithm].automaton_stats[formula]
            .buchi_automaton;

    ::Graph::Product<Graph::StateSpaceProduct> product
	(automaton, *round_info.statespace);

    try
    {
      printText("<searching for a system execution producing contradictory "
		"results>", 0, 2);

      product.findWitness(automaton.initialState(), state, witness);

      if (witness.cycle.first.empty())
	throw Exception
	  ("UserCommands::printCrossComparisonAnalysisResults(...): internal "
	   "error [witness construction failed]");
    }
    catch (const UserBreakException&)
    {
      printText(" user break\n", 0);
      throw;
    }
    catch (...)
    {
      printText(" error\n", 0);
      throw;
    }

    printText(" ok\n\n", 0);
  }
  else
  {
    const StateSpace::size_type loop_state
      = (*((*round_info.statespace)[round_info.statespace->size() - 1].
	    edges().begin()))->targetNode();
    const StateSpace::size_type loop_length
      = round_info.statespace->size() - loop_state;

    for ( ; state < loop_state; ++state)
      witness.prefix.second.push_back(StateSpace::PathElement(state, 0));

    state -= loop_state;
    for (StateSpace::size_type s = 0; s < loop_length; ++s)
    {
      witness.cycle.second.push_back
	(StateSpace::PathElement(state + loop_state, 0));
      state = (state + 1) % loop_length;
    }
  }

  /*
   *  Write information about the execution to the output stream. That is,
   *  display the states in the infinite execution (a prefix and a cycle)
   *  and tell which one of the algorithms would accept and which one
   *  would reject the execution (or show whether the algorithm compared
   *  against the path checking algorithm accepted or rejected the
   *  execution).
   */

  estream << string(indent + 2, ' ') + "Execution M:\n";

  printPath(stream, indent + 4, witness.prefix.second, witness.cycle.second,
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
    (*round_info.formulae[generator_formula], witness.prefix.second,
     witness.cycle.second, *round_info.statespace);

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
                 + ".",
		 78);

  if (!result)
  {
    estream << '\n';
    printAcceptingCycle(stream, indent + 2, accepting_algorithm,
			witness.prefix.first, witness.cycle.first,
			*(test_results[accepting_algorithm].
			    automaton_stats[formula].buchi_automaton),
			witness.prefix.second, witness.cycle.second,
			*round_info.statespace);
  }
}

/* ========================================================================= */
void printConsistencyAnalysisResults
  (ostream& stream, int indent, const vector<string>& input_tokens)
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

  unsigned long int algorithm_id = parseAlgorithmId(input_tokens[1]);

  if (test_results[algorithm_id].consistency_check_result == -1)
  {
    printTextBlock(stream, indent,
		   "Model checking result consistency check was not performed "
		   "on implementation "
		   + configuration.algorithmString(algorithm_id)
		   + ".",
		   78);
    return;
  }

  if (test_results[algorithm_id].consistency_check_result == 1)
  {
    printTextBlock(stream, indent,
		   "Implementation "
		   + configuration.algorithmString(algorithm_id)
		   + " passed the model checking result consistency check.",
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
    verifyNumber(state, round_info.statespace->size(),
		 "State identifier out of range");

    if (state >= round_info.real_emptiness_check_size)
    {
      printTextBlock(stream, indent,
		     "Model checking result consistency check was not "
		     "performed on implementation "
		     + configuration.algorithmString(algorithm_id)
		     + " in state " + toString(state) + " of the state "
		       "space.",
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
		       "space.",
		     78);
      return;
    }
  }

  vector<StateSpace::size_type> path;
  StateSpace::Path prefix, cycle;
  map<StateSpace::size_type, StateSpace::size_type> ordering;
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
    prefix.push_back(StateSpace::PathElement(path[s], 0));

  for (StateSpace::size_type s = loop_state; s < path.size(); s++)
    cycle.push_back(StateSpace::PathElement(path[s], 0));

  estream << string(indent + 2, ' ') + "Execution M:\n";
  printPath(stream, indent + 4, prefix, cycle, *(round_info.statespace));

  estream << '\n' + string(indent + 2, ' ')
             + "Analysis of the formula in the execution:\n";

  Ltl::PathEvaluator path_evaluator;
  bool result = path_evaluator.evaluate
    (*round_info.formulae[formula], prefix, cycle, *round_info.statespace);

  path_evaluator.print(stream, indent + 4);

  printTextBlock(stream, indent + 2,
		 string(" \n The formula is ") + (result ? "" : "not ")
                 + "satisfied in the execution. It seems that the automaton "
		   "constructed for the "
		 + (result ? "posi" : "nega")
                 + "tive formula rejects the execution incorrectly.",
		 78);
}
 
/* ========================================================================= */
void printAutomatonAnalysisResults
  (ostream& stream, int indent, const vector<string>& input_tokens)
/* ----------------------------------------------------------------------------
 *
 * Description:   Implements the user command `buchianalysis', i.e., analyzes
 *                an inconsistency detected in the intersection emptiness check
 *                for two Büchi automata constructed for the formula and its
 *                negation.
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

  if (!configuration.global_options.do_intr_test)
    throw CommandErrorException("This command is available only when the "
				"Büchi automata intersection emptiness check "
				"is enabled.");

  unsigned long int algorithm1 = parseAlgorithmId(input_tokens[1]);
  unsigned long int algorithm2 = parseAlgorithmId(input_tokens[2]);

  if (configuration.isInternalAlgorithm(algorithm1)
      || configuration.isInternalAlgorithm(algorithm2))
    throw CommandErrorException
            ("This feature is not available for lbtt's internal "
	     "model checking algorithm.");

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
		   + ".",
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

  ::Graph::Product<Graph::BuchiProduct>::Witness witness;

  try
  {
    printText("<searching for an accepting execution>", 0, 2);

    const BuchiAutomaton& automaton_1
      = *(test_results[algorithm1].automaton_stats[0].buchi_automaton);
    const BuchiAutomaton& automaton_2
      = *(test_results[algorithm2].automaton_stats[1].buchi_automaton);

    ::Graph::Product<Graph::BuchiProduct> product(automaton_1, automaton_2);
    product.findWitness(automaton_1.initialState(), automaton_2.initialState(),
			witness);

    if (witness.cycle.first.empty())
      throw Exception
	("UserCommands::printAutomatonAnalysisResults(...): internal error "
	 "[witness construction failed]");
  }
  catch (const UserBreakException&)
  {
    printText(" user break\n", 0);
    throw;
  }
  catch (...)
  {
    printText(" error\n", 0);
    throw;
  }

  printText(" ok\n", 0);

  const unsigned long int valuation_size
    = configuration.formula_options.formula_generator
        .number_of_available_variables;
  const StateSpace::size_type path_length
    = witness.prefix.first.size() + witness.cycle.first.size();
  StateSpace path(valuation_size, path_length);

  StateSpace::size_type state = 0;
  StateSpace::Path::const_iterator p1, p2;
  StateSpace::Path path_prefix, path_cycle;

  for (int i = 0; i < 2; ++i)
  {
    const pair<StateSpace::Path, StateSpace::Path>* witness_segment;
    StateSpace::Path* path_segment;

    if (i == 0)
    {
      witness_segment = &witness.prefix;
      path_segment = &path_prefix;
    }
    else
    {
      witness_segment = &witness.cycle;
      path_segment = &path_cycle;
    }

    for (p1 = witness_segment->first.begin(), 
	   p2 = witness_segment->second.begin();
	 p1 != witness_segment->first.end();
	 ++p1, ++p2, ++state)
    {
      ::Ltl::LtlFormula* f
	= &::Ltl::And::construct
	     (static_cast<const BuchiAutomaton::BuchiTransition&>(p1->edge())
	        .guard(),
	      static_cast<const BuchiAutomaton::BuchiTransition&>(p2->edge())
	        .guard());

      path[state].positiveAtoms().copy
	(f->findPropositionalModel(valuation_size - 1), valuation_size);

      path_segment->push_back(StateSpace::PathElement(state, 0));

      ::Ltl::LtlFormula::destruct(f);
    }
  }

  /*
   *  Display the input sequence accepted by both automata.
   */

  estream << string(indent + 2, ' ')
             + "Execution M accepted by both automata:\n";

  printPath(stream, indent + 4, path_prefix, path_cycle, path);

  estream << '\n';

  /*
   *  For each of the original automata, display the accepting runs that
   *  these automata have on the input sequence.
   */

  for (int i = 0; i < 2; ++i)
  {
    const BuchiAutomaton::Path* prefix;
    const BuchiAutomaton::Path* cycle;

    if (i == 0)
    {
      prefix = &witness.prefix.first;
      cycle = &witness.cycle.first;
    }
    else
    {
      prefix = &witness.prefix.second;
      cycle = &witness.cycle.second;
    }

    printAcceptingCycle(stream, indent + 2, (i == 0 ? algorithm1 : algorithm2),
			*prefix, *cycle,
			*(test_results[i == 0 ? algorithm1 : algorithm2]
			    .automaton_stats[i].buchi_automaton),
			path_prefix, path_cycle, path);

    estream << '\n';
  }

  /*
   *  Display a proof or a refutation for the formula in the execution.
   */

  estream << string(indent + 2, ' ')
	     + "Analysis of the positive formula in the execution M:\n";

  Ltl::PathEvaluator path_evaluator;
  bool result = path_evaluator.evaluate(*round_info.formulae[formula],
					path_prefix, path_cycle, path);

  path_evaluator.print(stream, indent + 4);

  printTextBlock(stream, indent + 2,
		 " \n The positive formula is " + string(result ? "" : "not ")
		 + "satisfied in the execution. This suggests that the "
                   "Büchi automaton constructed for the "
		 + (result ? "nega" : "posi") + "tive formula "
		 + (algorithm1 == algorithm2
		    ? ""
		    : "(the automaton constructed by implementation "
		      + configuration.algorithmString(result ? algorithm2
						             : algorithm1)
		      + ") ")
		 + "is incorrect.",
		 78);
}

/* ========================================================================= */
void printPath
  (ostream& stream, int indent, const StateSpace::Path& prefix,
   const StateSpace::Path& cycle, const StateSpace& path)
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes information about a path in a state space to a stream.
 *
 * Arguments:     stream  --  A reference to an output stream.
 *                indent  --  Number of spaces to leave to the left of output.
 *                prefix  --  The prefix of the path.
 *                cycle   --  The cycle of the path.
 *                path    --  The state space to which the state identifiers in
 *                            the prefix and the cycle refer.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::badbit | ios::failbit);

  const StateSpace::Path* path_segment;

  for (int i = 0; i < 2; ++i)
  {
    if (i == 0 && prefix.empty())
      continue;

    estream << string(indent, ' ');
    if (i == 0)
    {
      path_segment = &prefix;
      estream << "prefix";
    }
    else
    {
      path_segment = &cycle;
      estream << "cycle";
    }
    estream << ":\n";

    bool first_printed;

    for (StateSpace::Path::const_iterator path_element = path_segment->begin();
	 path_element != path_segment->end();
	 ++path_element)
    {
      if (path_element != path_segment->begin())
	estream << toString(path_element->node()) + "\n";
      estream << string(indent + 2, ' ') + 's' + toString(path_element->node())
                 + " {";

      first_printed = false;
      for (unsigned long int proposition = 0;
	   proposition < path.numberOfPropositions();
	   ++proposition)
      {
	if (path[path_element->node()].positiveAtoms().test(proposition))
	{
	  if (first_printed)
	    estream << ',';
	  else
	    first_printed = true;

	  estream << 'p' + toString(proposition);
	}
      }

      estream << "} --> s";
    }
    estream << toString(cycle.begin()->node()) + "\n";
  }

  estream.flush();
}

/* ========================================================================= */
void printAcceptingCycle
  (ostream& stream, int indent,
   vector<Configuration::AlgorithmInformation>::size_type algorithm_id,
   const BuchiAutomaton::Path& aut_prefix,
   const BuchiAutomaton::Path& aut_cycle,
   const BuchiAutomaton& automaton, const StateSpace::Path& path_prefix,
   const StateSpace::Path& path_cycle, const StateSpace& path)
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes information about an execution of a Büchi automaton to
 *                a stream.
 *
 * Arguments:     stream        --  A reference to an output stream.
 *                indent        --  Number of spaces to leave to the left of
 *                                  output.
 *                algorithm_id  --  Identifier for an algorithm.
 *                aut_prefix    --  The prefix of a path in a Büchi automaton.
 *                aut_cycle     --  The cycle of a path in a Büchi automaton.
 *                automaton     --  The Büchi automaton to which the state
 *                                  identifiers in `aut_prefix' and `aut_cycle'
 *                                  refer.
 *                path_prefix   --  The prefix of a path in a state space.
 *                                  (This path is interpreted as the input for
 *                                  the Büchi automaton.)  It is assumed that
 *                                  `path_prefix.size() == aut_prefix.size()'.
 *                path_cycle    --  The cycle of a path in a state space.  It
 *                                  is assumed that `path_cycle.size() ==
 *                                  aut_cycle.size()'.
 *                path          --  The state space to which the state
 *                                  identifiers in `path_prefix' and
 *                                  `path_cycle' refer.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::badbit | ios::failbit);

  printTextBlock(stream, indent,
		 "On input M, the automaton constructed by implementation "
                 + configuration.algorithmString(algorithm_id) + " (with "
                 + toString(automaton.numberOfAcceptanceSets())
                 + " acceptance set"
                 + (automaton.numberOfAcceptanceSets() == 1 ? "" : "s")
                 + ") has the following accepting execution:",
		 78);

  const BuchiAutomaton::Path* aut_segment;
  const StateSpace::Path* path_segment;
  BuchiAutomaton::Path::const_iterator aut_state;
  StateSpace::Path::const_iterator path_state;

  for (int i = 0; i < 2; ++i)
  {
    if (i == 0 && aut_prefix.empty())
      continue;

    estream << string(indent + 2, ' ');
    if (i == 0)
    {
      estream << "prefix";
      aut_segment = &aut_prefix;
      path_segment = &path_prefix;
    }
    else
    {
      estream << "cycle";
      aut_segment = &aut_cycle;
      path_segment = &path_cycle;
    }
    estream << ":\n";

    bool first_printed = false;
    for (aut_state = aut_segment->begin(), path_state = path_segment->begin();
	 aut_state != aut_segment->end();
	 ++aut_state, ++path_state)
    {
      estream << string(indent + 4, ' ') + toString(aut_state->node()) + ' ';
	
      const BitArray* bits;
      for (int j = 0; j < 2; ++j)
      {
	bits = (j == 0
		? &automaton[aut_state->node()].acceptanceSets()
		: &static_cast<const BuchiAutomaton::BuchiTransition&>
                     (aut_state->edge()).acceptanceSets());
	first_printed = false;

	for (unsigned long int accept_set = 0;
	     accept_set < automaton.numberOfAcceptanceSets();
	     ++accept_set)
        {
	  if (bits->test(accept_set))
	  {
	    if (first_printed)
	      estream << ',';
	    else
	    {
	      first_printed = true;
	      if (j == 1)
		estream << "--";
	      estream << '{';
	    }
	    estream << accept_set;
	  }
	}

	if (first_printed)
	{
	  estream << "}";
	  if (j == 0)
	    estream << ' ';
	}
      }

      estream << "--> " + toString(aut_state->edge().targetNode()) + "  [ {"; 
      bits = &path[path_state->node()].positiveAtoms();
      first_printed = false;
      for (unsigned long int proposition = 0;
	   proposition < path.numberOfPropositions();
	   ++proposition)
      {
	if (bits->test(proposition))
	{
	  if (first_printed)
	    estream << ',';
	  else
	    first_printed = true;
	  estream << 'p' + toString(proposition);
	}
      }
      estream << "} |== ";
      static_cast<const BuchiAutomaton::BuchiTransition&>(aut_state->edge())
	.guard().print(estream);
      estream << " ]\n";
    }
  }

  estream.flush();
}
 
/* ========================================================================= */
void printBuchiAutomaton
  (ostream& stream, int indent, bool formula_type,
   vector<string>& input_tokens, Graph::GraphOutputFormat fmt)
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

  unsigned long int algorithm = parseAlgorithmId(input_tokens[1]);
  int formula
    = (configuration.formula_options.output_mode == Configuration::NNF
       ? 0 : 2)
      + (formula_type ? 0 : 1);

  if (configuration.isInternalAlgorithm(algorithm))
    throw CommandErrorException
            ("This feature is not available for lbtt's internal "
	     "model checking algorithm.");

  if (!test_results[algorithm].automaton_stats[formula_type ? 0 : 1].
         buchiAutomatonComputed())
  {
    printTextBlock(stream, indent,
		   "No automaton was generated using implementation "
		   + configuration.algorithmString(algorithm)
		   + " for the formula `"
		   + toString(*round_info.formulae[formula])
		   + "'.",
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
      IntervalList states;

      if (input_tokens.size() == 2)
	input_tokens.push_back("*");

      try
      {
	parseIntervalList(input_tokens[2], states, 0, automaton->size() - 1);
      }
      catch (const IntervalRangeException& e)
      {
	throw CommandErrorException
	  (string("State identifier out of range (")
	   + toString(e.getNumber())
	   + ").");
      }

      for (IntervalList::const_iterator state = states.begin();
	   state != states.end();
	   ++state)
      {
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

  estream.flush();
}

/* ========================================================================= */
void evaluateFormula
  (ostream& stream, int indent, bool formula_type,
   vector<string>& input_tokens)
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

  IntervalList algorithms;
  IntervalList system_states;
  string algorithm_name;
  int formula = (formula_type ? 0 : 1);

  if (!configuration.global_options.do_comp_test
      && !configuration.global_options.do_cons_test)
    throw CommandErrorException("This command is available only when one of "
				"the model checking tests is enabled.");

  if (round_info.statespace == 0)
    throw CommandErrorException("No state space was generated in this test "
                                "round.");

  /*
   *  If no list of algorithms was given as an argument, show the results of
   *  all algorithms.
   */

  if (input_tokens.size() < 2)
    input_tokens.push_back("*");

  parseAlgorithmIdList(input_tokens[1], algorithms);

  /*
   *  If no list of states was given, show information about all states.
   *  (If only a local product was computed, show information only about the
   *  initial state.)
   */

  if (input_tokens.size() < 3)
    input_tokens.push_back("*");

  try
  {
    parseIntervalList(input_tokens[2], system_states, 0,
		      round_info.real_emptiness_check_size - 1);
  }
  catch (const IntervalRangeException& e)
  {
    throw CommandErrorException
      (string("State identifier out of range (")
       + toString(e.getNumber())
       + ").");
  }

  estream << string(indent, ' ') + "Acceptance information:\n"
             + string(indent + 2, ' ') + "CTL* formula: E ";

  round_info.formulae[configuration.formula_options.output_mode
		        == Configuration::NNF
		      ? formula
		      : 2 + formula]->print(estream);

  estream << '\n';

  for (IntervalList::const_iterator state = system_states.begin();
       state != system_states.end();
       ++state)
  {
    estream << string(indent + 2, ' ') + "State " + toString(*state) + ":\n";

    for (IntervalList::const_iterator algorithm = algorithms.begin();
	 algorithm != algorithms.end();
	 ++algorithm)
    {
      algorithm_name = configuration.algorithms[*algorithm].name.substr(0, 20);
      estream << string(indent + 4, ' ') + toString(*algorithm) + ": "
                 + algorithm_name + ':'
                 + string(21 - algorithm_name.length(), ' ');

      if (!test_results[*algorithm].automaton_stats[formula].
             emptiness_check_performed)
        estream << (configuration.isInternalAlgorithm(*algorithm)
		    ? "model checking result not available\n" 
		    : "emptiness check not performed\n");
      else
        estream << (test_results[*algorithm].automaton_stats[formula].
                      emptiness_check_result[*state]
                    ? "true"
                    : "false")
                << '\n';
    }
  }

  estream.flush();
}    

/* ========================================================================= */
void printFormula
  (ostream& stream, int indent, bool formula_type,
   const vector<string>& input_tokens)
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
 *                input_tokens  --  A reference to a vector of strings
 *                                  containing the arguments of the user
 *                                  command.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (round_info.formulae[0] == 0)
    throw CommandErrorException("No formulas were generated in this test "
				"round.");

  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);

  if (input_tokens.size() <= 1 || input_tokens[1] == "normal")
  {
    estream << string(indent, ' ');
    round_info.formulae[formula_type ? 2 : 3]->print(estream);
  }
  else if (input_tokens[1] == "nnf")
  {
    estream << string(indent, ' ');
    if (configuration.formula_options.output_mode != Configuration::NNF)
    {
      ::Ltl::LtlFormula* f
	  = round_info.formulae[formula_type ? 2 : 3]->nnfClone();
      f->print(estream);
      ::Ltl::LtlFormula::destruct(f);
    }
    else
      round_info.formulae[formula_type ? 0 : 1]->print(estream);
  }
  else
    throw CommandErrorException
	    ("`" + input_tokens[1] + "' is not a valid formula mode.");

  estream << '\n';
  estream.flush();
}
  
/* ========================================================================= */
void printCommandHelp
  (ostream& stream, int indent, const vector<string>& input_tokens)
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
  {
    command = parseCommand(input_tokens[1]);
    if (command == UNKNOWN)
      estream << string(indent, ' ') + "Unknown command (`" + input_tokens[1]
                 + "').\n\n";
  }

  switch (command)
  {
    case ALGORITHMS :
      estream << string(indent, ' ') + "algorithms\n"
	         + string(indent, ' ') + "implementations\n"
	         + string(indent, ' ') + "translators\n";

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
		     "respectively.",
                     78);
      break;

    case FORMULA :
      estream << string(indent, ' ')
                 + "formula [\"+\"|\"-\"] [\"normal\"|\"nnf\"]\n";

      printTextBlock(stream, indent + 4,
                     "Display the LTL formula used in this test round for "
                     "generating Büchi automata (\"+\" denotes the positive "
                     "formula, \"-\" the negated formula; \"normal\" and "
                     "\"nnf\" select between the display mode). If no formula "
                     "(display mode) is specified, show the positive formula "
                     "(the formula as generated).",
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
                     "formula is specified, use the positive formula. The "
		     "optional argument `state' can be used to specify the "
		     "state of the state space in which to do the analysis.",
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
                     "implementations\n"
                     "inconsistencies\n"
                     "quit\n"
                     "resultanalysis\n"
                     "results\n"
                     "skip\n"
                     "statespace\n"
                     "statistics\n"
                     "translators\n"
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

  estream.flush();
}

/* ========================================================================= */
void printInconsistencies
  (ostream& stream, int indent, vector<string>& input_tokens)
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

  IntervalList algorithms;

  if (!configuration.global_options.do_cons_test)
    throw CommandErrorException("This command is available only when the "
				"model checking result consistency check is "
				"enabled.");

  if (input_tokens.size() == 1)
    input_tokens.push_back("*");

  parseAlgorithmIdList(input_tokens[1], algorithms);

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

  for (IntervalList::const_iterator algorithm = algorithms.begin();
       algorithm != algorithms.end();
       ++algorithm)
  {
    estream << '\n'
               + string(indent, ' ')
               + configuration.algorithmString(*algorithm)
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

  estream.flush();
}

/* ========================================================================= */
void printTestResults
  (ostream& stream, int indent, vector<string>& input_tokens)
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
  IntervalList algorithms;

  if (input_tokens.size() == 1)
    input_tokens.push_back("*");

  parseAlgorithmIdList(input_tokens[1], algorithms);

  estream << string(indent, ' ') + "Test results for round "
             + toString(round_info.current_round) << ":\n\n";

  if (configuration.global_options.verbosity <= 2)
    printStatTableHeader(stream, indent);

  for (IntervalList::const_iterator algorithm = algorithms.begin();
       algorithm != algorithms.end();
       ++algorithm)
    printAllStats(stream, indent, *algorithm);

  if (configuration.global_options.verbosity <= 2)
    estream << '\n';

  if (configuration.global_options.do_comp_test)
  {
    estream << string(indent, ' ')
               + "Model checking result cross-comparison:\n";
    printCrossComparisonStats(stream, indent + 2, algorithms);
  }

  if (configuration.global_options.do_intr_test)
  {
    estream << string(indent, ' ')
               + "Büchi automata intersection emptiness check:\n";
    printBuchiIntersectionCheckStats(stream, indent + 2, algorithms);
  }

  estream.flush();
}

/* ========================================================================= */
void printStateSpace
  (ostream& stream, int indent, vector<string>& input_tokens,
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
  IntervalList states;

  if (!configuration.global_options.do_comp_test
      && !configuration.global_options.do_cons_test)
    throw CommandErrorException("This command is available only when one of "
				"the model checking tests is enabled.");

  if (round_info.statespace == 0)
    throw CommandErrorException("No state space was generated in this test "
                                "round.");

  if (fmt == Graph::NORMAL)
  {
    if (input_tokens.size() == 1)
      input_tokens.push_back("*");

    try
    {
      parseIntervalList(input_tokens[1], states, 0,
			round_info.statespace->size() - 1);
    }
    catch (const IntervalRangeException& e)
    {
      throw CommandErrorException
	(string("State identifier out of range (")
	 + toString(e.getNumber())
	 + ").");
    }

    estream << string(indent, ' ') + "State space information:\n";

    for (IntervalList::const_iterator state = states.begin();
	 state != states.end();
	 ++state)
    {
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

  estream.flush();
}

/* ========================================================================= */
void changeVerbosity(const vector<string>& input_tokens)
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
void changeAlgorithmState(vector<string>& input_tokens, bool enable)
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
  IntervalList algorithms;

  if (input_tokens.size() < 2)
    input_tokens.push_back("*");

  parseAlgorithmIdList(input_tokens[1], algorithms);

  for (IntervalList::const_iterator algorithm = algorithms.begin();
       algorithm != algorithms.end();
       ++algorithm)
  {
    printText(string(enable ? "En" : "Dis")
	      + "abling implementation "
	      + configuration.algorithmString(*algorithm)
	      + ".\n",
	      0,
	      2);
    
    configuration.algorithms[*algorithm].enabled = enable;
  }

  round_info.cout << '\n';
  round_info.cout.flush();
}

}
