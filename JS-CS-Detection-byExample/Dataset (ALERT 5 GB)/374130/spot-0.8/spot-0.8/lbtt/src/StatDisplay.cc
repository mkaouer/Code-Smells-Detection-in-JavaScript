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
#include <string>
#include "DispUtil.h"
#include "Exception.h"
#include "IntervalList.h"
#include "SharedTestData.h"
#include "StatDisplay.h"
#include "StringUtil.h"
#include "TestRoundInfo.h"

namespace StatDisplay
{

using namespace ::DispUtil;
using namespace ::SharedTestData;
using namespace ::StringUtil;

/* ========================================================================= */
void printStatTableHeader(ostream& stream, int indent)
/* ----------------------------------------------------------------------------
 *
 * Description:   Displays a table header for test statistics (used in
 *                verbosity mode 2).
 *
 * Arguments:     stream  --  A reference to the output stream to which the
 *                            header should be written.
 *                indent  --  Number of spaces to leave on the left of the
 *                            output.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);

  int num_dashes = 39;
  estream << string(indent, ' ') + " # F   Elapsed     Büchi     Büchi Acc.";
  if (configuration.global_options.do_cons_test
      || configuration.global_options.do_comp_test)
  {
    num_dashes += 31;
    estream << "     Product     Product   Acc.";
    if (configuration.global_options.do_cons_test)
    {
      num_dashes += 3;
      estream << " CC";
    }
  }
  estream << '\n' + string(indent + 10, ' ')
             + "time    states    trans. sets";
  if (configuration.global_options.do_cons_test
      || configuration.global_options.do_comp_test)
    estream << "      states      trans. cycles";
  estream << "\n" + string(indent, ' ') + string(num_dashes, '-') + '\n';
  estream.flush();
}


/* ========================================================================= */
void printBuchiAutomatonStats
  (ostream& stream, int indent,
   vector<AlgorithmTestResults>::size_type algorithm,
   int result_id)
/* ----------------------------------------------------------------------------
 *
 * Description:   Displays information about a Büchi automaton, extracting the
 *                information from a TestStatistics structure stored in the
 *                UserInterface object.
 *
 * Arguments:     stream     --  A reference to the output stream to which the
 *                               information should be written.
 *                indent     --  Number of spaces to leave on the left of the
 *                               output in verbosity modes >= 3.
 *                algorithm  --  Identifier of the algorithm used for
 *                               generating the automaton.
 *                result_id  --  Selects between the automata constructed from
 *                               a formula and its negation.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);

  const AutomatonStats& automaton_stats = 
    test_results[algorithm].automaton_stats[result_id];

  if (configuration.global_options.verbosity <= 2)
  {
    if (!automaton_stats.buchiAutomatonComputed())
      estream << "      N/A       N/A       N/A  N/A";
    else
    {
      if (automaton_stats.buchi_generation_time >= 0.0)
      {
	changeStreamFormatting(stream, 9, 2, ios::fixed | ios::right);
	estream << automaton_stats.buchi_generation_time;
	restoreStreamFormatting(stream);
      }
      else
	estream << "      N/A";
      estream << ' ';
      changeStreamFormatting(stream, 9, 0, ios::right);
      estream << automaton_stats.number_of_buchi_states;
      restoreStreamFormatting(stream);
      estream << ' ';
      changeStreamFormatting(stream, 9, 0, ios::right);
      estream << automaton_stats.number_of_buchi_transitions;
      restoreStreamFormatting(stream);
      estream << ' ';
      changeStreamFormatting(stream, 4, 0, ios::right);
      estream << automaton_stats.number_of_acceptance_sets;
      restoreStreamFormatting(stream);
    }
    estream << ' ';
  }
  else
  {
    estream << string(indent, ' ');

    if (!automaton_stats.buchiAutomatonComputed())
      estream << "not computed";
    else
    {
      estream << "number of states:" + string(6, ' ')
                 + toString(automaton_stats.number_of_buchi_states)
	         + '\n' + string(indent, ' ') + "number of transitions: "
                 + toString(automaton_stats.number_of_buchi_transitions)
                 + '\n' + string(indent, ' ') + "acceptance sets:"
                 + string(7, ' ')
                 + toString(automaton_stats.number_of_acceptance_sets)
                 + '\n' + string(indent, ' ') + "computation time:"
                 + string(6, ' ');
    
      if (automaton_stats.buchi_generation_time != -1.0)
      {
	changeStreamFormatting(stream, 9, 2, ios::fixed | ios::left);
	estream << automaton_stats.buchi_generation_time;
	restoreStreamFormatting(stream);

	estream << " seconds (user time)";
      }
      else
	estream << "N/A";
    }

    estream << '\n';
  }

  estream.flush();
}

/* ========================================================================= */
void printProductAutomatonStats
  (ostream& stream, int indent,
   vector<AlgorithmTestResults>::size_type algorithm,
   int result_id)
/* ----------------------------------------------------------------------------
 *
 * Description:   Displays information about a product automaton, extracting
 *                the information from a TestStatistics structure stored in the
 *                UserInterface object.
 *
 * Arguments:     stream     --  A reference to the output stream to which the
 *                               information should be written.
 *                indent     --  Number of spaces to leave on the left of the
 *                               output in verbosity modes >= 3.
 *                algorithm  --  Identifier of the algorithm used for
 *                               generating the product automaton.
 *                result_id  --  Selects between the automata constructed from
 *                               a formula and its negation.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);

  const AutomatonStats& automaton_stats =
    test_results[algorithm].automaton_stats[result_id];

  if (configuration.global_options.verbosity <= 2)
  {
    if (!automaton_stats.productAutomatonComputed())
      estream << "        N/A         N/A";
    else
    {
      changeStreamFormatting(stream, 11, 0, ios::right);
      estream << automaton_stats.number_of_product_states;
      restoreStreamFormatting(stream);
      estream << ' ';
      changeStreamFormatting(stream, 11, 0, ios::right);
      estream << automaton_stats.number_of_product_transitions;
      restoreStreamFormatting(stream);
    }
    estream << ' ';
  }
  else
  {
    estream << string(indent, ' ');

    if (!automaton_stats.productAutomatonComputed())
      estream << "not computed";
    else
    {
      estream << "number of states:" + string(6, ' ');

      changeStreamFormatting(stream, 9, 0, ios::left);
      estream << automaton_stats.number_of_product_states;
      restoreStreamFormatting(stream);

      estream << " [";

      if (automaton_stats.number_of_product_states != 0)
      {
	changeStreamFormatting(stream, 0, 2, ios::fixed);
	estream << static_cast<double>
                     (automaton_stats.number_of_product_states)
                   / static_cast<double>
                       (automaton_stats.number_of_buchi_states)
                   / static_cast<double>(round_info.statespace->size())
	           * 100.0;
	restoreStreamFormatting(stream);

	estream << "% of worst case ("
		<< automaton_stats.number_of_buchi_states
	             * round_info.statespace->size()
		<< ')';
      }
      else
	estream << "empty automaton";

      estream << "]\n" + string(indent, ' ') + "number of transitions: "
                 + toString(automaton_stats.number_of_product_transitions);
    }

    estream << '\n';
  }

  estream.flush();
}

/* ========================================================================= */
void printAcceptanceCycleStats
  (ostream& stream, int indent,
   vector<AlgorithmTestResults>::size_type algorithm,
   int result_id)
/* ----------------------------------------------------------------------------
 *
 * Description:   Displays information about the number of system states from
 *                which begins an execution accepted by a Büchi automaton. The
 *                information is extracted from a TestStatistics structure
 *                stored in the UserInterface object.
 *
 * Arguments:     stream     --  A reference to the output stream to which the
 *                               information should be written.
 *                indent     --  Number of spaces to leave on the left of the
 *                               output in verbosity mode >= 3.
 *                algorithm  --  Identifier of the algorithm used for
 *                               computing the Büchi automaton whose accepting
 *                               cycles are to be considered.
 *                result_id  --  Selects between the result computed for a
 *                               formula and its negation.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);

  const AutomatonStats& automaton_stats =
    test_results[algorithm].automaton_stats[result_id];

  if (configuration.global_options.verbosity <= 2)
  {
    if (!automaton_stats.emptiness_check_performed)
      estream << "   N/A";
    else
    {
      changeStreamFormatting(stream, 6, 0, ios::right);
      estream << automaton_stats.emptiness_check_result.count();
      restoreStreamFormatting(stream);
    }
    estream << ' ';
  }
  else
  {
    estream << string(indent, ' ');

    if (!automaton_stats.emptiness_check_performed)
      estream << "not computed";
    else if (configuration.global_options.product_mode == Configuration::LOCAL)
    {
      estream << string("cycle ");

      if (automaton_stats.emptiness_check_result[0])
	estream << "reachable    ";
      else
	estream << "not reachable";

      estream << "    (from the initial state)";
    }
    else
    {
      estream << "cycle reachable from   ";

      changeStreamFormatting(stream, 9, 0, ios::left);
      estream << automaton_stats.emptiness_check_result.count();
      restoreStreamFormatting(stream);

      estream << " states\n" + string(indent, ' ')
                 + "not reachable from     ";

      changeStreamFormatting(stream, 9, 0, ios::left);
      estream << (round_info.real_emptiness_check_size
		    - automaton_stats.emptiness_check_result.count());
      restoreStreamFormatting(stream);

      estream << " states";
    }

    estream << '\n';
  }

  estream.flush();
}

/* ========================================================================= */
void printConsistencyCheckStats
  (ostream& stream, int indent,
   vector<AlgorithmTestResults>::size_type algorithm)
/* ----------------------------------------------------------------------------
 *
 * Description:   Displays information about the consistency check result for
 *                a given algorithm, extracting the information from a
 *                TestStatistics structure stored in the UserInterface object.
 *
 * Arguments:     stream     --  A reference to an output stream to which the
 *                               information should be written.
 *                indent     --  Number of spaces to leave on the left of the
 *                               output in verbosity mode >= 3.
 *                algorithm  --  Identifier of the algorithm whose consistency
 *                               check result should be displayed.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);
  const AlgorithmTestResults& test_result = test_results[algorithm];

  if (configuration.global_options.verbosity <= 2)
  {
    switch (test_result.consistency_check_result)
    {
      case -1 :
	estream << "NA";
	break;

      case 0 :
	estream << " F";
	break;

      default:
	estream << " P";
	break;
    }
  }
  else
  {
    estream << string(indent, ' ');

    if (test_result.consistency_check_result == -1)
      estream << "not performed";
    else
    {
      estream << "result:" + string(18, ' ');

      if (test_result.consistency_check_result == 0)
      {
	estream << "failed    ["
	           + toString(test_result.failed_consistency_check_comparisons)
                   + " (";

	changeStreamFormatting(stream, 0, 2, ios::fixed);
	estream << ((test_result.consistency_check_comparisons == 0)
		    ? 0.0
		    : static_cast<double>
                        (test_result.failed_consistency_check_comparisons)
                      / test_result.consistency_check_comparisons * 100.0);
	restoreStreamFormatting(stream);

	estream << "%) of "
	           + toString(test_result.consistency_check_comparisons)
                   + " test cases]";
      }
      else
	estream << "passed";
    }

    estream << '\n';
  }

  estream.flush();
}

/* ========================================================================= */
void printCrossComparisonStats
  (ostream& stream, int indent, const IntervalList& algorithms)
/* ----------------------------------------------------------------------------
 *
 * Description:   Displays information about the model checking result cross-
 *                comparison check, extracting the information from a vector of
 *                TestStatistics structures stored in the UserInterface object.
 *
 * Arguments:     stream      --  A reference to an output stream to which the
 *                                information should be written.
 *                indent      --  Number of spaces to leave on the left of the
 *                                output.
 *                algorithms  --  A reference to a constant IntervalList
 *                                storing the numeric identifiers of the
 *                                algorithms for which the statistics should
 *                                be shown.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);
  bool no_errors_to_report = true, nothing_to_report = true;

  const AutomatonStats* alg_1_pos_results;
  const AutomatonStats* alg_1_neg_results;

  for (IntervalList::const_iterator alg_1 = algorithms.begin();
       alg_1 != algorithms.end();
       ++alg_1)
  {
    alg_1_pos_results = &test_results[*alg_1].automaton_stats[0];
    alg_1_neg_results = &test_results[*alg_1].automaton_stats[1];

    for (vector<AlgorithmTestResults>::size_type alg_2 = 0;
	 alg_2 < round_info.number_of_translators;
	 alg_2++)
    {
      if (*alg_1 != alg_2
	  && (alg_2 > *alg_1 || !algorithms.covers(alg_2))
	  && configuration.algorithms[*alg_1].enabled
	  && configuration.algorithms[alg_2].enabled)
      {
	bool pos_test, neg_test;

	if (nothing_to_report)
        {
	  nothing_to_report = false;
	  estream << string(indent, ' ') + "result:";
	}

	for (int counter = 0; counter < 2; counter++)
	{
	  if (counter == 0)
	  {
	    pos_test = !alg_1_pos_results->crossComparisonPerformed(alg_2);
	    neg_test = !alg_1_neg_results->crossComparisonPerformed(alg_2);
	  }
	  else
	  {
	    pos_test = (alg_1_pos_results->cross_comparison_stats[alg_2].
			  second > 0);
	    neg_test = (alg_1_neg_results->cross_comparison_stats[alg_2].
			  second > 0);
	  }

	  if (pos_test || neg_test)
	  {
	    estream << '\n' + string(indent + 2, ' ');
	    no_errors_to_report = false;

	    estream << string(counter == 0 ? "N/A   " : "failed") + " (";

	    if (pos_test)
	      estream << string("+") + (neg_test ? "-)" : ") ");
	    else
	      estream << "-) ";

	    estream << " " + configuration.algorithmString(*alg_1) + ", "
                       + configuration.algorithmString(alg_2);
	  }
	}
      }
    }
  }

  if (nothing_to_report)
    estream << string(indent, ' ') + "not performed";
  else if (no_errors_to_report)
    estream << string(20, ' ') + "no failures detected";
  estream << "\n\n";
  estream.flush();
}

/* ========================================================================= */
void printBuchiIntersectionCheckStats
  (ostream& stream, int indent, const IntervalList& algorithms)
/* ----------------------------------------------------------------------------
 *
 * Description:   Displays information about the Büchi automaton intersection
 *                emptiness check results, extracting the information from a
 *                TestStatistics structure stored in the UserInterface object.
 *
 * Arguments:     stream      --  A reference to an output stream to which the
 *                                information should be written.
 *                indent      --  Number of spaces to leave on the left of the
 *                                output.
 *                algorithms  --  A reference to a constant IntervalList
 *                                storing the numeric identifiers of the
 *                                algorithms for which the statistics should
 *                                be shown.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);
  bool no_errors_to_report = true, nothing_to_report = true;

  const AutomatonStats* alg_1_pos_results;
  const AutomatonStats* alg_1_neg_results;

  for (IntervalList::const_iterator alg_1 = algorithms.begin();
       alg_1 != algorithms.end();
       ++alg_1)
  {
    alg_1_pos_results = &test_results[*alg_1].automaton_stats[0];
    alg_1_neg_results = &test_results[*alg_1].automaton_stats[1];

    for (vector<AlgorithmTestResults>::size_type alg_2 = 0;
	 alg_2 < round_info.number_of_translators;
	 alg_2++)
    {
      if (configuration.algorithms[*alg_1].enabled
	  && configuration.algorithms[alg_2].enabled
	  && (alg_2 >= *alg_1 || !algorithms.covers(alg_2))
	  && !configuration.isInternalAlgorithm(*alg_1)
	  && !configuration.isInternalAlgorithm(alg_2))
      {
	bool pos_test, neg_test;

	if (nothing_to_report)
        {
	  nothing_to_report = false;
	  estream << string(indent, ' ') + "result:";
	}

	for (int counter = -1; counter < 1; counter++)
	{
	  pos_test = (alg_1_pos_results->buchi_intersection_check_stats[alg_2]
		        == counter);
	  neg_test = (alg_1_neg_results->buchi_intersection_check_stats[alg_2]
		        == counter);

	  if (pos_test || neg_test)
	  {
	    estream << '\n' + string(indent + 2, ' ');
	    no_errors_to_report = false;

	    estream << string(counter == -1 ? "N/A   " : "failed") + ' ';

	    if (*alg_1 != alg_2)
	    {
	      estream << '(';
	      if (pos_test)
		estream << string("+") + (neg_test ? "-)" : ") ");
	      else
		estream << "-) ";
	    }
	    else
	      estream << "    ";
	      
	    estream << ' ' + configuration.algorithmString(*alg_1);

	    if (*alg_1 != alg_2)
	    {
	      estream << ", (";
	      if (pos_test)
		estream << string("-") + (neg_test ? "+" : "");
	      else
		estream << '+';

	      estream << ") " + configuration.algorithmString(alg_2);
	    }
	  }
	}
      }
    }
  }

  if (nothing_to_report)
    estream << string(indent, ' ') + "not performed";
  else if (no_errors_to_report)
    estream << string(20, ' ') + "no failures detected";
  estream << "\n";
  estream.flush();
}
 
/* ========================================================================= */
void printAllStats
  (ostream& stream, int indent,
   vector<AlgorithmTestResults>::size_type algorithm)
/* ----------------------------------------------------------------------------
 *
 * Description:   Displays all test information (Büchi automaton and product
 *                automaton statistics, acceptance cycle information,
 *                consistency check result) for an algorithm.
 *
 * Arguments:     stream     --  A reference to an output stream to which the
 *                               information should be written.
 *                indent     --  Number of spaces to leave on the left of the
 *                               output.
 *                algorithm  --  Identifier of an algorithm.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);

  if (configuration.global_options.verbosity >= 3)
    estream << string(indent, ' ') + configuration.algorithmString(algorithm)
               + '\n';

  for (int counter = 0; counter < 2; counter++)
  {
    if (configuration.global_options.verbosity <= 2)
    {
      if (counter == 1)
	estream << '\n';
      estream << string(indent, ' ');
      changeStreamFormatting(stream, 2, 0, ios::right);
      estream << algorithm << ' ';
      restoreStreamFormatting(stream);
      estream << (counter == 0 ? '+' : '-') << ' ';
    }
    else
    {
      estream << string(indent + 2, ' ')
	         + (counter == 0 ? "Positive" : "Negated") + " formula:\n"
	         + string(indent + 4, ' ') + "Büchi automaton:\n";
    }
    printBuchiAutomatonStats(stream, indent + 6, algorithm, counter);

    if (configuration.global_options.do_comp_test
	|| configuration.global_options.do_cons_test)
    {
      if (configuration.global_options.verbosity >= 3)
	estream << string(indent + 4, ' ') + "Product automaton:\n";
      printProductAutomatonStats(stream, indent + 6, algorithm, counter);
      if (configuration.global_options.verbosity >= 3)
	estream << string(indent + 4, ' ') + "Accepting cycles:\n";
      printAcceptanceCycleStats(stream, indent + 6, algorithm, counter);
    }
  }

  if (configuration.global_options.do_cons_test)
  {
    if (configuration.global_options.verbosity >= 3)
      estream << string(indent + 2, ' ') + "Result consistency check:\n";
    printConsistencyCheckStats(stream, indent + 4, algorithm);
  }

  estream << '\n';
  estream.flush();
}

/* ========================================================================= */
void printCollectiveCrossComparisonStats
  (ostream& stream,
   vector<TestStatistics>::size_type algorithm_y,
   vector<TestStatistics>::size_type algorithm_x,
   int data_type)
/* ----------------------------------------------------------------------------
 *
 * Description:   Called by printCollectiveStats in order to fill a single cell
 *                of the result cross-comparison table.
 *
 * Arguments:     stream                    --  A reference to an output
 *                                              stream.
 *                algorithm_x, algorithm_y  --  Identifiers of the algorithms
 *                                              whose cross-comparison results
 *                                              should be displayed.
 *                data_type                 --  Determines the type of data to
 *                                              be displayed in the cell:
 *                                                 0  --  Model checking result
 *                                                        cross-comparison
 *                                                        statistics.
 *                                                 1  --  Model checking result
 *                                                        cross-comparison
 *                                                        statistics (initial
 *                                                        state only).
 *                                                 2  --  Büchi automaton
 *                                                        intersection check
 *                                                        statistics.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{        
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);

  estream << ' ';

  if (algorithm_x == algorithm_y && data_type != 2)
    estream << string(21, ' ');
  else
  {
    unsigned long int num_comparisons, num_mismatches;
    const TestStatistics& stats = final_statistics[algorithm_y];

    switch (data_type)
    {
      case 0 :
	num_comparisons = stats.cross_comparisons_performed[algorithm_x];
	num_mismatches = stats.cross_comparison_mismatches[algorithm_x];
	break;

      case 1 :
	num_comparisons = stats.cross_comparisons_performed[algorithm_x];
	num_mismatches
	  = stats.initial_cross_comparison_mismatches[algorithm_x];
	break;

      default :
	if (configuration.isInternalAlgorithm(algorithm_x)
	    || configuration.isInternalAlgorithm(algorithm_y))
	{
	  estream << string(21, ' ');
	  return;
	}

	num_comparisons
	  = stats.buchi_intersection_checks_performed[algorithm_x];
	num_mismatches = stats.buchi_intersection_check_failures[algorithm_x];
	break;
    }

    if (num_comparisons > 0)
    {
      changeStreamFormatting(stream, 5, 0, ios::right);
      estream << num_mismatches;
      restoreStreamFormatting(stream);

      estream << '/';

      changeStreamFormatting(stream, 5, 0, ios::left);
      estream << num_comparisons;
      restoreStreamFormatting(stream);

      estream << " (";

      double percentage = static_cast<double>(num_mismatches)
                            / static_cast<double>(num_comparisons)
	                    * 100.0;

      changeStreamFormatting(stream, 0, 2, ios::fixed);
      estream << percentage;
      restoreStreamFormatting(stream);
      
      estream << "%)";

      if (percentage < 100.0)
	estream << ' ';    
      if (percentage < 10.0)
	estream << ' ';
    }
    else
      estream << "    N/A" + string(14, ' ');
  }
}

/* ========================================================================= */
void printCollectiveStats(ostream& stream, int indent)
/* ----------------------------------------------------------------------------
 *
 * Description:   Displays average information about a series of tests for each
 *                tested algorithm:
 *                  - average sizes of the Büchi and product automata
 *                  - number of failed attempts to generate a Büchi automaton
 *                  - number of failed consistency checks
 *                  - number of failed path checks
 *                  - cross-comparison mismatches
 *                  - Büchi automata intersection emptiness check failures
 *
 * Arguments:     stream  --  A reference to an output stream.
 *                indent  --  Number of spaces to leave on the left of the
 *                            output.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);
  const string ind(indent, ' ');

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
   *  Display state space statistics.
   */

  estream << '\n' + ind + "Statistics after round "
	     + toString(round_info.current_round) + '\n'
             + ind + string(toString(round_info.current_round).length() + 23,
			    '*')
             + "\n\n\n";

  if (configuration.global_options.do_comp_test
      || configuration.global_options.do_cons_test)
  {
    estream << ind + "  State space statistics\n"
               + ind + "  " + string(22, '=') + "\n\n"
               + ind + "    " + toString(round_info.num_generated_statespaces)
               + " state spaces generated\n"
               + ind + "    " + toString(round_info.total_statespace_states)
	       + " states generated";

    if (round_info.num_generated_statespaces > 0)
    {
      estream << " ("
                 + toString(static_cast<double>
			      (round_info.total_statespace_states)
			    / static_cast<double>
			        (round_info.num_generated_statespaces),
			    2)
                 + " states per state space)";
    }

    estream << '\n' + ind + "    "
               + toString(round_info.total_statespace_transitions)
	       + " transitions generated";

    if (round_info.num_generated_statespaces > 0)
    {
      estream << " ("
                 + toString(static_cast<double>
	  		      (round_info.total_statespace_transitions)
			    / static_cast<double>
			      (round_info.num_generated_statespaces),
			    2)
	         + " transitions per state space)";
    }

    estream << "\n\n\n";
  }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
   *  Display LTL formula statistics.
   */

  estream << ind + "  LTL formula statistics\n"
             + ind + "  " + string(22, '=') + "\n\n"
             + ind + "    " + toString(round_info.num_processed_formulae)
             + " LTL formulas "
             + (!configuration.global_options.formula_input_filename.empty()
		? "process"
		: "generat")
             + "ed\n";

  if (round_info.num_processed_formulae > 0
      && configuration.global_options.formula_input_filename.empty())
  {
    const map<unsigned long int, unsigned long int>&
      proposition_statistics
        = configuration.formula_options.formula_generator.
            propositionStatistics();

    const map<int, unsigned long int>
      symbol_statistics
        = configuration.formula_options.formula_generator.symbolStatistics();

    estream << '\n' + ind + "    Atomic symbol distribution:\n";
    string symbol_name_string;
    string symbol_number_string;
    string symbol_distribution_string;
    int number_of_symbols_printed = 0;

    if (symbol_statistics.find(::Ltl::LTL_TRUE) != symbol_statistics.end())
    {
      const unsigned long int num
	= symbol_statistics.find(::Ltl::LTL_TRUE)->second;
      symbol_name_string += "true        ";
      const string number_string = toString(num);
      symbol_number_string += number_string
                              + string(12 - number_string.length(), ' ');
      const string distribution_string
	= toString(static_cast<double>(num)
		     / static_cast<double>(round_info.num_processed_formulae),
		   3);
      symbol_distribution_string += distribution_string
	                            + string(12 - distribution_string.length(),
					     ' ');
      number_of_symbols_printed++;
    }

    if (symbol_statistics.find(::Ltl::LTL_FALSE) != symbol_statistics.end())
    {
      const unsigned long int num
	= symbol_statistics.find(::Ltl::LTL_FALSE)->second;
      symbol_name_string += "false       ";
      const string number_string = toString(num);
      symbol_number_string += number_string
	                      + string(12 - number_string.length(), ' ');
      const string distribution_string
	= toString(static_cast<double>(num)
		     / static_cast<double>(round_info.num_processed_formulae),
		   3);
      symbol_distribution_string += distribution_string
	                            + string(12 - distribution_string.length(),
					     ' ');
      number_of_symbols_printed++;
    }

    for (map<unsigned long int, unsigned long int>::const_iterator
	   proposition = proposition_statistics.begin();
	 proposition != proposition_statistics.end();
	 ++proposition)
    {
      const string name_string = "p" + toString(proposition->first);
      symbol_name_string += name_string;
      const string number_string = toString(proposition->second);
      symbol_number_string += number_string;
      const string distribution_string
	= toString(static_cast<double>(proposition->second)
		     / static_cast<double>(round_info.num_processed_formulae),
		   3);
      symbol_distribution_string += distribution_string;

      number_of_symbols_printed++;

      if (number_of_symbols_printed % 5 == 0)
      {
	estream << ind + "         symbol  " + symbol_name_string + '\n'
                   + ind + "              #  " + symbol_number_string + '\n'
	           + ind + "      #/formula  " + symbol_distribution_string
                   + "\n\n";
	symbol_name_string = symbol_number_string = symbol_distribution_string
	  = "";
      }
      else
      {
	symbol_name_string += string(12 - name_string.length(), ' ');
	symbol_number_string += string(12 - number_string.length(), ' ');
	symbol_distribution_string += string(12 - distribution_string.length(),
					     ' ');
      }
    }

    if (number_of_symbols_printed % 5 != 0)
    {
      estream << ind + "         symbol  " + symbol_name_string + '\n'
                 + ind + "              #  " + symbol_number_string + '\n'
	         + ind + "      #/formula  " + symbol_distribution_string
                 + "\n\n";
    }

    estream << ind + "    Operator distribution:\n";
    symbol_name_string = symbol_number_string = symbol_distribution_string
      = "";
    number_of_symbols_printed = 0;

    for (map<int, unsigned long int>::const_iterator
	   op = symbol_statistics.begin();
	 op != symbol_statistics.end();
	 ++op)
    {
      if (op->first == ::Ltl::LTL_ATOM || op->first == ::Ltl::LTL_TRUE
	  || op->first == ::Ltl::LTL_FALSE)
	continue;

      const string name_string = ::Ltl::infixSymbol(op->first);
      symbol_name_string += name_string;
      const string number_string = toString(op->second);
      symbol_number_string += number_string;
      const string distribution_string
	= toString(static_cast<double>(op->second)
		     / static_cast<double>(round_info.num_processed_formulae),
		   3);
      symbol_distribution_string += distribution_string;

      number_of_symbols_printed++;

      if (number_of_symbols_printed % 5 == 0)
      {
	if (number_of_symbols_printed > 5)
	  estream << '\n';
	estream << ind + "       operator  " + symbol_name_string + '\n'
                   + ind + "              #  " + symbol_number_string + '\n'
	           + ind + "      #/formula  " + symbol_distribution_string
                   + '\n';
	symbol_name_string = symbol_number_string = symbol_distribution_string
	  = "";
      }	
      else
      {
	symbol_name_string += string(12 - name_string.length(), ' ');
	symbol_number_string += string(12 - number_string.length(), ' ');
	symbol_distribution_string += string(12 - distribution_string.length(),
					     ' ');
      }
    }

    if (number_of_symbols_printed % 5 != 0)
    {
      if (number_of_symbols_printed > 5)
	estream << '\n';
      estream << ind + "       operator  " + symbol_name_string + '\n'
                 + ind + "              #  " + symbol_number_string + '\n'
	         + ind + "      #/formula  " + symbol_distribution_string
                 + '\n';
    }
  }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
   *  Display the following information for each algorithm:
   *     0.  Automata statistics.
   *     1.  Number of failures to compute Büchi automata.
   *     2.  Number of model checking result consistency check failures.
   */

  string algorithm_name;

  for (int i = 0; i <= 2; i++)
  {
    estream << '\n' + string(2 + indent, ' ');

    switch (i)
    {
      case 1 :
        estream << "Failures to compute Büchi automaton\n"
                   + string(2 + indent, ' ') + string(35, '=') + '\n';
        break;
      case 2 :
	if (!configuration.global_options.do_cons_test)
	  continue;

        estream << "Model checking result consistency check failures\n"
                   + string(2 + indent, ' ') + string(48, '=') + '\n';
        break;
      default :
        break;
    }

    for (unsigned long int algorithm = 0;
         algorithm < round_info.number_of_translators;
         ++algorithm)
    {
      if (configuration.isInternalAlgorithm(algorithm))
	continue;

      estream << '\n' + string((i > 0 ? 4 : 2) + indent, ' ')
                 + configuration.algorithms[algorithm].name + '\n';

      switch (i)
      {
	/*
	 *  Display a table of automaton statistics.
	 */

        case 0 :
        {
          unsigned long int failures_to_compute_automaton;
          unsigned long int automaton_count;
          unsigned long int number_of_successful_instances;
          BIGUINT total_number_of_states;
          BIGUINT total_number_of_transitions;

          const TestStatistics& stats = final_statistics[algorithm];

          estream << string(2 + indent,  ' ')
	             + string(configuration.algorithms[algorithm].name.
                                length(), '=');

	  for (int k = 0; k < 2; k++)
          {
	    if (k == 1 && !configuration.global_options.do_comp_test
		&& !configuration.global_options.do_cons_test)
	      continue;

	    estream << "\n\n" + string(8 + indent, ' ')
	               + (k == 0 ? "BÜCHI  " : "PRODUCT") + string(7, ' ')
	               + "|    Number of    |    Number of    |"
	                 "    Number of    |\n"
	               + string(8 + indent, ' ')
	               + "AUTOMATA" + string(6, ' ')
	               + "|    automata     |     states      |"
	                 "   transitions   |\n"
	               + string(7 + indent, ' ') + string(15, '-') + '+';

            for (int j = 0; j < 3; j++)
              estream << string(17, '-') + '+';

            estream << '\n';

            for (int j = 0; j < 3; j++)
            {
	      estream << string(8 + indent, ' ');
	      switch (j)
              {
                case 0  : estream << "Pos. formulae"; break;
                case 1  : estream << "Neg. formulae"; break;
                default : estream << "All formulae "; break;
              }

              estream << " | ";

              if (j < 2)
              {
		if (k == 0)
		{
		  failures_to_compute_automaton
		    = stats.failures_to_compute_buchi_automaton[j];
		  automaton_count = stats.buchi_automaton_count[j];
		  total_number_of_states
		    = stats.total_number_of_buchi_states[j];
		  total_number_of_transitions
		    = stats.total_number_of_buchi_transitions[j];
		}
		else
		{
		  failures_to_compute_automaton
		    = stats.failures_to_compute_product_automaton[j];
		  automaton_count = stats.product_automaton_count[j];
		  total_number_of_states
		    = stats.total_number_of_product_states[j];
		  total_number_of_transitions
		    = stats.total_number_of_product_transitions[j];
		}
              }
              else
              {
		if (k == 0)
		{
		  failures_to_compute_automaton =
		    stats.failures_to_compute_buchi_automaton[0]
		    + stats.failures_to_compute_buchi_automaton[1];
		  automaton_count = stats.buchi_automaton_count[0]
		                    + stats.buchi_automaton_count[1];
		  total_number_of_states
		    = stats.total_number_of_buchi_states[0]
		      + stats.total_number_of_buchi_states[1];
		  total_number_of_transitions
		    = stats.total_number_of_buchi_transitions[0]
		      + stats.total_number_of_buchi_transitions[1];
		}
		else
		{
		  failures_to_compute_automaton =
		    stats.failures_to_compute_product_automaton[0]
		    + stats.failures_to_compute_product_automaton[1];
		  automaton_count = stats.product_automaton_count[0]
		                    + stats.product_automaton_count[1];
		  total_number_of_states
		    = stats.total_number_of_product_states[0]
		      + stats.total_number_of_product_states[1];
		  total_number_of_transitions
		    = stats.total_number_of_product_transitions[0]
		      + stats.total_number_of_product_transitions[1];
		}
              }

              number_of_successful_instances =
                automaton_count - failures_to_compute_automaton;

	      for (int z = 0; z < 2; z++)
	      {
		if (z == 0)
		{
		  changeStreamFormatting(stream, 15, 2, ios::right);
		  estream << number_of_successful_instances;
		  restoreStreamFormatting(stream);
		}
		else
		  estream << string(indent + 15, ' ') + "(avg.) |"
		             + string(16, ' ');

		estream << " | ";

		if (number_of_successful_instances == 0)
		  estream << string(15, ' ');
		else
                {
		  changeStreamFormatting(stream, 15, 2,
					 ios::fixed | ios::right);
		  if (z == 0)
		    estream << total_number_of_states;
		  else
		    estream << total_number_of_states
                                 / static_cast<double>
                                     (number_of_successful_instances);
		  restoreStreamFormatting(stream);
		}

		estream << " | ";

		if (number_of_successful_instances == 0)
		  estream << string(15, ' ');
		else
                {
		  changeStreamFormatting(stream, 15, 2,
					 ios::fixed | ios::right);
		  if (z == 0)
		    estream << total_number_of_transitions;
		  else
		    estream << total_number_of_transitions
                                 / static_cast<double>
                                     (number_of_successful_instances);
		  restoreStreamFormatting(stream);
		}

		estream << " |\n";
	      }
            }

	    if (k == 0)
	    {
	      BIGUINT total_number_of_acceptance_sets;
	      double buchi_generation_time;

	      estream << '\n' + string(22 + indent, ' ')
                         + "|    Number of    |  Time consumed  |\n"
		         + string(22 + indent, ' ')
                         + "| acceptance sets |    (seconds)    |\n"
		         + string(7 + indent, ' ') + string(15, '-') + '+';

	      for (int j = 0; j < 2; j++)
		estream << string(17, '-') + '+';

	      for (int j = 0; j < 3; j++)
	      {
		estream << '\n' + string(8 + indent, ' ');
		switch (j)
                {
                  case 0  : estream << "Pos. formulae"; break;
                  case 1  : estream << "Neg. formulae"; break;
                  default : estream << "All formulae "; break;
		}

		estream << " | ";

		if (j < 2)
		{
		  failures_to_compute_automaton =
		    stats.failures_to_compute_buchi_automaton[j];

		  automaton_count = stats.buchi_automaton_count[j];

		  buchi_generation_time = stats.total_buchi_generation_time[j];
		  total_number_of_acceptance_sets
		    = stats.total_number_of_acceptance_sets[j];
		}
		else
		{
		  failures_to_compute_automaton =
		    stats.failures_to_compute_buchi_automaton[0]
                    + stats.failures_to_compute_buchi_automaton[1];

		  automaton_count = stats.buchi_automaton_count[0]
                                    + stats.buchi_automaton_count[1];

		  buchi_generation_time =
		    (stats.total_buchi_generation_time[0] >= 0.0
		     && stats.total_buchi_generation_time[1] >= 0.0
		     ? stats.total_buchi_generation_time[0]
                       + stats.total_buchi_generation_time[1]
		     : -1.0);
		  total_number_of_acceptance_sets
		    = stats.total_number_of_acceptance_sets[0]
		      + stats.total_number_of_acceptance_sets[1];
		}

		number_of_successful_instances =
		  automaton_count - failures_to_compute_automaton;

		for (int z = 0; z < 2; z++)
		{
		  if (number_of_successful_instances == 0)
		    estream << string(15, ' ');
		  else
		  {
		    changeStreamFormatting(stream, 15, 2,
					   ios::fixed | ios::right);
		    if (z == 0)
		      estream << total_number_of_acceptance_sets;
		    else
		      estream << total_number_of_acceptance_sets
                                   / static_cast<double>
		                       (number_of_successful_instances);
		    restoreStreamFormatting(stream);
		  }

		  estream << " | ";

		  if (number_of_successful_instances == 0
		      || buchi_generation_time < 0.0)
		    estream << string(15, ' ');
		  else
                  {
		    changeStreamFormatting(stream, 15, 2,
					   ios::fixed | ios::right);
		    if (z == 0)
		      estream << buchi_generation_time;
		    else
		      estream << buchi_generation_time
		                   / number_of_successful_instances;
		    restoreStreamFormatting(stream);
		  }

		  estream << " |";
		  if (z == 0)
		    estream << '\n' + string(indent + 15, ' ') + "(avg.) | ";
		}
	      }

	      estream << '\n';
	    }
          }

	  if (algorithm + 1 < round_info.number_of_translators)
	    estream << '\n';
        
          break;
        }

	/*
	 *  Display the number of automaton computation failures.
	 */

        case 1 :
        {
          unsigned long int number_of_failures;
          unsigned long int total_count;

          const TestStatistics& stats = final_statistics[algorithm];

          for (int j = 0; j < 3; j++)
          {
            estream << string(8 + indent, ' ');
            switch (j)
            {
              case 0  : estream << "Positive formulae: "; break;
              case 1  : estream << "Negative formulae: "; break;
              default : estream << "Total:" + string(13, ' '); break;
            }

            if (j < 2)
            {
	      number_of_failures
		= stats.failures_to_compute_buchi_automaton[j];
	      total_count = stats.buchi_automaton_count[j];
            }
            else
            {
	      number_of_failures
		= stats.failures_to_compute_buchi_automaton[0]
		  + stats.failures_to_compute_buchi_automaton[1];
	      total_count = stats.buchi_automaton_count[0]
                            + stats.buchi_automaton_count[1];
            }

            changeStreamFormatting(stream, 5, 0, ios::left);
            estream << number_of_failures;
            restoreStreamFormatting(stream);

            estream << " [";

	    if (total_count > 0)
	    {
	      changeStreamFormatting(stream, 0, 2, ios::fixed);
	      estream << static_cast<double>(number_of_failures) / total_count
		         * 100.0;
	      restoreStreamFormatting(stream);

	      estream << "% of " << total_count;
	    }
	    else
	      estream << "no";

	    estream << " attempts]\n";
          }

          break;
        }

	/*
	 *  Display the number of consistency check failures.
	 */

        case 2 :
	{
	  const TestStatistics& stats = final_statistics[algorithm];

	  estream << string(8 + indent, ' ');

	  changeStreamFormatting(stream, 5, 0, ios::left);
	  estream << stats.consistency_check_failures;
	  restoreStreamFormatting(stream);

	  estream << " [";

	  if (stats.consistency_checks_performed > 0)
	  {
	    changeStreamFormatting(stream, 0, 2, ios::fixed);
	    estream << static_cast<double>(stats.consistency_check_failures)
                       / stats.consistency_checks_performed
	               * 100.0;
	    restoreStreamFormatting(stream);

	    estream << "% of " << stats.consistency_checks_performed;
	  }
	  else
	    estream << "no";

	  estream << " checks performed]\n";

	  break;
	}
      }
    }
    estream << '\n';
  }

  estream << '\n';

  if ((configuration.algorithms.size() > 1
       && configuration.global_options.do_comp_test)
      || configuration.global_options.do_intr_test)
  {
    vector<Configuration::AlgorithmInformation>::size_type
      number_of_algorithms = configuration.algorithms.size();

    int legend;

    estream << ind + "  Result inconsistency statistics\n"
               + ind + "  " + string(31, '=') + '\n';

    vector<TestStatistics>::size_type algorithm_x, algorithm_y;

    for (algorithm_x = 0; algorithm_x < number_of_algorithms;
	 algorithm_x += 2)
    {
      estream << '\n' + string(30 + indent, ' ');

      for (int i = 0; i < 2; ++i)
      {
	if (algorithm_x + i < number_of_algorithms)
        {
	  algorithm_name =
	    configuration.algorithms[algorithm_x + i].name.substr(0, 20);
	  estream << "| " + algorithm_name
	             + string(21 - algorithm_name.length(), ' ');
	}
      }
      estream << '|';

      for (algorithm_y = 0; algorithm_y < number_of_algorithms;
           algorithm_y++)
      {
	if (configuration.isInternalAlgorithm(algorithm_y))
	  continue;

	estream << "\n    " + ind + string(26, '-');

	for (int i = 0; i < 2; ++i)
        {
	  if (algorithm_x + i < number_of_algorithms)
	    estream << '+' + string(22, '-');
	}
	estream << '+';

	algorithm_name
	  = configuration.algorithms[algorithm_y].name.substr(0, 20);

	bool algorithm_name_printed = false;
	legend = 1;

	for (int data_type = 0; data_type < 3; ++data_type)
        {
	  if ((data_type < 2
	       && configuration.global_options.do_comp_test
	       && number_of_algorithms > 1
	       && (data_type == 0
		   || configuration.global_options.product_mode
		        == Configuration::GLOBAL))
	      || (data_type == 2
		  && configuration.global_options.do_intr_test))
	  {
	    estream << "\n    " + ind
	               + (!algorithm_name_printed
			  ? algorithm_name
                            + string(21 - algorithm_name.length(), ' ')
			  : string(21, ' '))
	               + " ["
	               + toString(legend)
		       + "] ";

	    algorithm_name_printed = true;
	    legend++;

	    for (int i = 0; i < 2; ++i)
            {
	      if (algorithm_x + i < number_of_algorithms)
	      {
		estream << '|';
		printCollectiveCrossComparisonStats(stream, algorithm_y,
						    algorithm_x + i,
						    data_type);
	      }
	    }
	    estream << '|';
	  }
	}
      }

      estream << '\n';
    }

    legend = 1;
    if (number_of_algorithms > 1 && configuration.global_options.do_comp_test)
    {
      if (configuration.global_options.product_mode == Configuration::GLOBAL)
      {
	estream << '\n' + string(indent + 4, ' ')
                   + "[1]  Model checking result cross-comparison failures\n"
	           + string(indent + 9, ' ')
                   + "(number of failures / number of global "
	             "cross-comparisons)\n";
	legend++;
      }

      estream << '\n' + string(indent + 4, ' ') + '[' + toString(legend)
                 + "]  Model checking result cross-comparison failures "
                   "(initial state only)\n"
		 + string(indent + 9, ' ')
	         + "(number of failures / number of cross-comparisons)\n";
      legend++;
    }

    if (configuration.global_options.do_intr_test)
      estream << '\n' + string(indent + 4, ' ') + '[' + toString(legend)
	         + "]  Büchi automata intersection emptiness check "
	           "failures\n"
                 + string(indent + 9, ' ')
	         + "(number of failures / number of checks performed)";

    estream << "\n\n";
  }

  estream.flush();
}

}
