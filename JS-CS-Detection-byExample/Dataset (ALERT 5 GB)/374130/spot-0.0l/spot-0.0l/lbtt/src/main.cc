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

#include <config.h>
#include <csignal>
#include <cstdlib>
#include <ctime>
#include <iostream>
#include <vector>
#ifdef HAVE_READLINE
#include <cstdio>
#include <readline/readline.h>
#include <readline/history.h>
#endif  /* HAVE_READLINE */
#include "Alloc.h"
#include "Configuration.h"
#include "DispUtil.h"
#include "Exception.h"
#include "LtlFormula.h"
#include "Random.h"
#include "SharedTestData.h"
#include "StatDisplay.h"
#include "TestOperations.h"
#include "TestRoundInfo.h"
#include "TestStatistics.h"
#include "UserCommandReader.h"

using namespace std;

/******************************************************************************
 *
 * Handler for the SIGINT signal.
 *
 *****************************************************************************/

RETSIGTYPE breakHandler(int)
{
  user_break = true;
}



/******************************************************************************
 *
 * This variable will be used for testing whether the testing has been aborted
 * with a SIGINT signal.
 *
 *****************************************************************************/

bool user_break;



/******************************************************************************
 *
 * Program configuration.
 *
 *****************************************************************************/

Configuration configuration;



/******************************************************************************
 *
 * Variables for storing test results and maintaining test state information.
 *
 *****************************************************************************/

namespace SharedTestData
{

TestRoundInfo round_info;                           /* Data structure for
						     * storing information
						     * about the current test
						     * round.
						     */

vector<AlgorithmTestResults,                        /* Test results for each */
       ALLOC(AlgorithmTestResults) >                /* individual algorithm. */
  test_results;

vector<TestStatistics, ALLOC(TestStatistics) >      /* Overall test        */
  final_statistics;                                 /* statistics for each
						     * algorithm.
						     */

}



/******************************************************************************
 *
 * Test loop.
 *
 *****************************************************************************/

int testLoop()
{
  using namespace DispUtil;
  using namespace SharedTestData;
  using namespace StatDisplay;
  using namespace StringUtil;
  using namespace TestOperations;

  /* Return code.  Will be set to 1 if any of the test fails.  */
  int exit_status = 0;

  const Configuration::GlobalConfiguration& global_options
    = configuration.global_options;

  /*
   *  Initialize the test state information data structure with program
   *  configuration information.
   */

  round_info.number_of_translators = configuration.algorithms.size();

  round_info.next_round_to_run += global_options.init_skip;

  round_info.next_round_to_stop
    = (global_options.interactive == Configuration::ALWAYS
       ? round_info.next_round_to_run
       : global_options.number_of_rounds + 1);

  if (tmpnam(round_info.formula_file_name[0]) == 0
      || tmpnam(round_info.formula_file_name[1]) == 0
      || tmpnam(round_info.automaton_file_name) == 0
      || tmpnam(round_info.cout_capture_file) == 0
      || tmpnam(round_info.cerr_capture_file) == 0)
    throw Exception("unable to allocate names for temporary files");

  /*
   *  If a name for the error log file was given in the configuration, create
   *  the file.
   */

  if (!global_options.transcript_filename.empty())
  {
    time_t current_time;

    time(&current_time);

    try
    {
      openFile(global_options.transcript_filename.c_str(),
	       round_info.transcript_file,
	       ios::out | ios::trunc,
	       0);
    }
    catch (const IOException&)
    {
      throw Exception("error creating log file `"
		      + global_options.transcript_filename + '\'');
    }

    try
    {
      round_info.transcript_file << "lbtt " PACKAGE_VERSION
				    " error log file, created on "
				    + string(ctime(&current_time))
				    + '\n';

      configuration.print(round_info.transcript_file);
    }
    catch (const IOException&)
    {
      round_info.transcript_file.close();
    }
  }

  /*
   *  If a formula file name was given in the configuration, open the file for
   *  reading.
   */

  try
  {
    if (!global_options.formula_input_filename.empty())
      openFile(global_options.formula_input_filename.c_str(),
	       round_info.formula_input_file,
	       ios::in,
	       0);
  }
  catch (const FileOpenException& e)
  {
    if (round_info.transcript_file.is_open())
      writeToTranscript("Testing aborted: " + string(e.what()), false);

    throw;
  }

  /*
   *  If using the rand48() function family for generating random numbers,
   *  initialize the random number generators.
   */

#ifdef HAVE_RAND48
  unsigned short int statespace_random_state[3];
  unsigned short int formula_random_state[3];

  SRAND(configuration.global_options.statespace_random_seed);
  for (int i = 0; i < 3; i++)
    statespace_random_state[i] = static_cast<short int>(LRAND(0, LONG_MAX));

  SRAND(configuration.global_options.formula_random_seed);
  for (int i = 0; i < 3; i++)
    formula_random_state[i] = static_cast<short int>(LRAND(0, LONG_MAX));
#endif /* HAVE_RAND48 */

  /*
   *  If using paths as state spaces, include the internal model checking
   *  algorithm in the set of algorithms.
   */

  if (global_options.statespace_generation_mode & Configuration::PATH)
  {
    Configuration::AlgorithmInformation lbtt_info
      = {new string("lbtt"), new string(), new string(), true};

    configuration.algorithms.push_back(lbtt_info);
  }

  /*
   *  Intialize the vector for storing the test results for each
   *  implementation and the vector for collecting overall test statistics for
   *  each implementation.
   */

  StateSpace::size_type max_emptiness_checking_size
    = (global_options.product_mode == Configuration::GLOBAL
       ? configuration.statespace_generator.max_size
       : 1);

  test_results.clear();
  final_statistics.clear();
  for (unsigned long int i = 0; i < configuration.algorithms.size(); ++i)
  {
    test_results.push_back
      (AlgorithmTestResults(configuration.algorithms.size(),
			    max_emptiness_checking_size));
    final_statistics.push_back
      (TestStatistics(configuration.algorithms.size()));
  }

  /*
   *  Test loop.
   */

  for (round_info.current_round = 1;
       !round_info.abort
	 && round_info.current_round <= global_options.number_of_rounds;
       ++round_info.current_round)
  {
    user_break = false;
    round_info.error = false;
    round_info.skip
      = (round_info.current_round < round_info.next_round_to_run);

    if (!round_info.skip)
    {
      if (!printText(string("Round ") + toString(round_info.current_round)
		     + " of " + toString(global_options.number_of_rounds)
		     + "\n\n",
		     2))
      {
	if (global_options.verbosity == 1)
	{
	  if (round_info.current_round > 1)
	    round_info.cout << ' ';
	  round_info.cout << round_info.current_round;
	  round_info.cout.flush();
	}
      }
    }

    try
    {
      /*
       *  Generate a new state space if necessary.
       */

      round_info.fresh_statespace
	= ((global_options.do_comp_test || global_options.do_cons_test)
	   && round_info.next_round_to_change_statespace
		== round_info.current_round);

      if (round_info.fresh_statespace)
      {
#ifdef HAVE_RAND48
	seed48(statespace_random_state);
	for (int i = 0; i < 3; i++)
	  statespace_random_state[i] = static_cast<short int>
					 (LRAND(0, LONG_MAX));
#else
	SRAND(global_options.statespace_random_seed);
	configuration.global_options.statespace_random_seed
	  = LRAND(0, RAND_MAX);
#endif /* HAVE_RAND48 */

	if (global_options.statespace_change_interval == 0)
	  round_info.next_round_to_change_statespace
	    = global_options.number_of_rounds + 1;
	else
	  round_info.next_round_to_change_statespace
	    += global_options.statespace_change_interval;

	for (vector<AlgorithmTestResults, ALLOC(AlgorithmTestResults) >
	       ::iterator it = test_results.begin();
	     it != test_results.end();
	     ++it)
	  it->emptinessReset();

	if ((!user_break && !round_info.skip)
	    || (global_options.statespace_generation_mode
		  == Configuration::ENUMERATEDPATH)
	    || (round_info.next_round_to_run
		  < round_info.next_round_to_change_statespace))
	{
	  try
	  {
	    generateStateSpace();
	  }
	  catch (const UserBreakException&)
	  {
	  }
	  catch (const StateSpaceGenerationException&)
	  {
	    round_info.error = true;
	  }
	}
      }

      /*
       *  Test whether it is necessary to generate (or read) a new LTL formula.
       */

      round_info.fresh_formula
	= (round_info.next_round_to_change_formula
	     == round_info.current_round);

      if (round_info.fresh_formula)
      {
#ifdef HAVE_RAND48
	seed48(formula_random_state);
	for (int i = 0; i < 3; i++)
	  formula_random_state[i] = static_cast<short int>(LRAND(0, LONG_MAX));
#else
	SRAND(global_options.formula_random_seed);
	configuration.global_options.formula_random_seed = LRAND(0, RAND_MAX);
#endif /* HAVE_RAND48 */

	if (global_options.formula_change_interval == 0)
	  round_info.next_round_to_change_formula
	    = global_options.number_of_rounds + 1;
	else
	  round_info.next_round_to_change_formula
	    += global_options.formula_change_interval;

	round_info.formula_in_file[0] = round_info.formula_in_file[1] = false;

	for (vector<AlgorithmTestResults, ALLOC(AlgorithmTestResults) >
	       ::iterator it = test_results.begin();
	     it != test_results.end();
	     ++it)
	  it->fullReset();

	if ((!round_info.error && !user_break && !round_info.skip)
	    || (round_info.next_round_to_run
		  < round_info.next_round_to_change_formula))
	{
	  try
	  {
	    generateFormulae(round_info.formula_input_file.is_open()
			     ? &round_info.formula_input_file
			     : 0);
	  }
	  catch (const FormulaGenerationException&)
	  {
	    round_info.error = true;
	    round_info.abort = true;
	    continue;
	  }
	}
      }

      if (user_break)
      {
	printText("[User break]\n\n", 2, 4);
	throw UserBreakException();
      }

      if (!round_info.error && !round_info.skip)
      {
	writeFormulaeToFiles();

	/*
	 *  If the generated state spaces paths, model check the formula
	 *  separately in the path.
	 */

	if (global_options.statespace_generation_mode & Configuration::PATH
	    && (global_options.do_cons_test || global_options.do_comp_test)
	    && (!test_results[round_info.number_of_translators].
		  automaton_stats[0].emptiness_check_performed))
	  verifyFormulaOnPath();

	if (!round_info.error)
	{
	  unsigned long int num_enabled_implementations = 0;

	  for (unsigned long int algorithm_id = 0;
	       algorithm_id < round_info.number_of_translators;
	       ++algorithm_id)
	  {
	    if (!configuration.algorithms[algorithm_id].enabled)
	      continue;

	    num_enabled_implementations++;

	    printText(configuration.algorithmString(algorithm_id) + '\n',
		      2, 4);

	    for (int counter = 0; counter < 2; counter++)
	    {
	      if (user_break)
	      {
		printText("[User break]\n\n", 2, 4);
		throw UserBreakException();
	      }

	      printText(string(counter == 1 ? "Negated" : "Positive")
			+ " formula:\n",
			2,
			6);

	      try
	      {
		try
		{
		  round_info.product_automaton = 0;

		  /*
		   *  Generate a Büchi automaton using the current algorithm.
		   *  `counter' determines the formula which is to be
		   *  translated into an automaton; 0 denotes the positive and
		   *  1 the negated formula.
		   */

		  generateBuchiAutomaton(counter, algorithm_id);

		  if (global_options.do_cons_test
		      || global_options.do_comp_test)
		  {
		    /*
		     *  Compute the product of the Büchi automaton with the
		     *  state space.
		     */

		    generateProductAutomaton(counter, algorithm_id);

		    /*
		     *  Find the system states from which an accepting
		     *  execution cycle can be reached by checking the product
		     *  automaton for emptiness.
		     */

		    performEmptinessCheck(counter, algorithm_id);

		    /*
		     *  If a product automaton was computed in this test round
		     *  (it might have not if the emptiness checking result was
		     *  already available), release the memory allocated for
		     *  the product automaton.
		     */

		    if (round_info.product_automaton != 0)
		    {
		      printText("<deallocating memory>", 4, 8);

		      delete round_info.product_automaton;
		      round_info.product_automaton = 0;

		      printText(" ok\n", 4);
		    }
		  }
		}
		catch (...)
		{
		  if (round_info.product_automaton != 0)
		  {
		    delete round_info.product_automaton;
		    round_info.product_automaton = 0;
		  }
		  throw;
		}
	      }
	      catch (const BuchiAutomatonGenerationException&)
	      {
		round_info.error = true;
		final_statistics[algorithm_id].
		  failures_to_compute_buchi_automaton[counter]++;
	      }
	      catch (const ProductAutomatonGenerationException&)
	      {
		round_info.error = true;
		final_statistics[algorithm_id].
		  failures_to_compute_product_automaton[counter]++;
	      }
	      catch (const EmptinessCheckFailedException&)
	      {
		round_info.error = true;
	      }
	    }

	    /*
	     *  If the emptiness check was performed successfully for the
	     *  product automata constructed from both the positive and
	     *  negated formulae, test whether the emptiness check results
	     *  are consistent with each other. (It should not be possible
	     *  for both the formula and its negation to be true in any
	     *  state.)
	     */
	    if (global_options.do_cons_test
		&& test_results[algorithm_id].automaton_stats[0].
		     emptiness_check_performed
		&& test_results[algorithm_id].automaton_stats[1].
		     emptiness_check_performed)
	      performConsistencyCheck(algorithm_id);

	    printText("\n", 2);
	  }

	  if (num_enabled_implementations > 0)
	  {
	    if (global_options.do_comp_test)
	    {
	      /*
	       *  Perform the pairwise comparisons of the emptiness check
	       *  results obtained using the different algorithms.
	       */

	      if (num_enabled_implementations >= 2
		  || (num_enabled_implementations == 1
		      && global_options.statespace_generation_mode
			   & Configuration::PATH))
		compareResults();
	    }

	    if (global_options.do_intr_test)
	    {
	      /*
	       *  Perform the pairwise intersection emptiness checks on the
	       *  Büchi automata computed during this test round using the
	       *  different algorithms.
	       */

	      performBuchiIntersectionCheck();
	    }

	    if ((global_options.do_comp_test || global_options.do_intr_test)
		&& global_options.verbosity == 2)
	    {
	      round_info.cout << '\n';
	      round_info.cout.flush();
	    }
	  }
	}
      }
    }
    catch (const UserBreakException&)
    {
      user_break = false;
      round_info.next_round_to_stop = round_info.current_round;
    }

    if (round_info.error)
      exit_status = 1;

    /*
     *  Determine from the program configuration and the error status whether
     *  the testing should be paused to wait for user commands.
     */

    if (round_info.error
	&& global_options.interactive == Configuration::ONERROR)
      round_info.next_round_to_stop = round_info.current_round;

    if (round_info.next_round_to_stop == round_info.current_round)
    {
      if (global_options.verbosity == 1)
      {
	round_info.cout << '\n';
	round_info.cout.flush();
      }

      ::UserCommandInterface::executeUserCommands();
    }
  }

  for (int i = 0; i < 2; i++)
    removeFile(round_info.formula_file_name[i], 2);

  if (round_info.path_iterator != 0)
    delete round_info.path_iterator;
  else if (round_info.statespace != 0)
    delete round_info.statespace;

  for (int f = 0; f < 4; f++)
  {
    if (round_info.formulae[f] != 0)
      ::Ltl::LtlFormula::destruct(round_info.formulae[f]);
  }

  for (vector<AlgorithmTestResults, ALLOC(AlgorithmTestResults) >
	 ::iterator it = test_results.begin();
       it != test_results.end();
       ++it)
    it->fullReset();

  round_info.current_round--;

  if (round_info.transcript_file.is_open())
  {
    round_info.transcript_file << endl;

    if (round_info.abort)
      round_info.transcript_file << "Testing aborted in round "
				    + toString(round_info.current_round)
				    + ".\n"
				 << endl;

    try
    {
      printCollectiveStats(round_info.transcript_file, 0);
    }
    catch (const IOException&)
    {
    }

    round_info.transcript_file << endl;

    time_t current_time;

    time(&current_time);

    round_info.transcript_file << "lbtt error log closed on "
				  + string(ctime(&current_time))
			       << endl;

    round_info.transcript_file.close();
  }

  if (global_options.verbosity >= 1)
    printCollectiveStats(cout, 0);

  if (round_info.formula_input_file.is_open())
    round_info.formula_input_file.close();

  return exit_status;
}



/******************************************************************************
 *
 * Main function.
 *
 *****************************************************************************/

int main(int argc, char* argv[])
{
  try
  {
    configuration.read(argc, argv);
  }
  catch (const Configuration::ConfigurationException& e)
  {
    cerr << argv[0];
    if (!e.line_info.empty())
      cerr << ":" << configuration.global_options.cfg_filename << ":"
	   << e.line_info;
    cerr << ": " << e.what() << endl;
    exit(-1);
  }

  if (configuration.global_options.verbosity >= 3)
    configuration.print(cout);

  user_break = false;
  if (configuration.global_options.interactive != Configuration::NEVER)
    signal(SIGINT, breakHandler);

#ifdef HAVE_OBSTACK_H
  obstack_alloc_failed_handler = &ObstackAllocator::failure;
#endif  /* HAVE_OBSTACK_H */

#ifdef HAVE_READLINE
  using_history();
#endif  /* HAVE_READLINE */

  try
  {
    return testLoop();
  }
  catch (const Exception& e)
  {
    cerr << argv[0] << ": " << e.what() << endl;
    exit(-1);
  }
  catch (const bad_alloc&)
  {
    cerr << argv[0] << ": out of memory" << endl;
    exit(-1);
  }
}
