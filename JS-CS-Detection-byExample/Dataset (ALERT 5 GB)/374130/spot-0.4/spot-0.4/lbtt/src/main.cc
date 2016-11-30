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
#include <csignal>
#include <cstdlib>
#include <ctime>
#ifdef HAVE_SYS_TYPES_H
#include <sys/types.h>
#endif /* HAVE_SYS_TYPES_H */
#include <iostream>
#include <vector>
#ifdef HAVE_READLINE
#include <cstdio>
#include <readline/readline.h>
#include <readline/history.h>
#endif  /* HAVE_READLINE */
#ifdef HAVE_ISATTY
#ifdef HAVE_UNISTD_H
#include <unistd.h>
#endif /* HAVE_UNISTD_H */
#endif /* HAVE_ISATTY */
#include "LbttAlloc.h"
#include "Configuration.h"
#include "DispUtil.h"
#include "Exception.h"
#include "LtlFormula.h"
#include "Random.h"
#include "SharedTestData.h"
#include "StatDisplay.h"
#include "TempFsysName.h"
#include "TestOperations.h"
#include "TestRoundInfo.h"
#include "TestStatistics.h"
#include "UserCommandReader.h"

using namespace std;



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

vector<AlgorithmTestResults> test_results;          /* Test results for each
						     * individual algorithm.
						     */

vector<TestStatistics> final_statistics;            /* Overall test
                                                     * statistics for each
						     * algorithm.
						     */

}



/******************************************************************************
 *
 * Functions for allocating and deallocating temporary file names.
 *
 *****************************************************************************/

static void allocateTempFilenames()
{
  using SharedTestData::round_info;
  round_info.formula_file_name[0] = new TempFsysName;
  round_info.formula_file_name[0]->allocate("lbtt");
  round_info.formula_file_name[1] = new TempFsysName;
  round_info.formula_file_name[1]->allocate("lbtt");
  round_info.automaton_file_name = new TempFsysName;
  round_info.automaton_file_name->allocate("lbtt");
  round_info.cout_capture_file = new TempFsysName;
  round_info.cout_capture_file->allocate("lbtt");
  round_info.cerr_capture_file = new TempFsysName;
  round_info.cerr_capture_file->allocate("lbtt");
}

static void deallocateTempFilenames()
{
  using SharedTestData::round_info;
  if (round_info.formula_file_name[0] != 0)
  {
    delete round_info.formula_file_name[0];
    round_info.formula_file_name[0] = 0;
  }
  if (round_info.formula_file_name[1] != 0)
  {
    delete round_info.formula_file_name[1];
    round_info.formula_file_name[1] = 0;
  }
  if (round_info.automaton_file_name != 0)
  {
    delete round_info.automaton_file_name;
    round_info.automaton_file_name = 0;
  }
  if (round_info.cout_capture_file != 0)
  {
    delete round_info.cout_capture_file;
    round_info.cout_capture_file = 0;
  }
  if (round_info.cerr_capture_file != 0)
  {
    delete round_info.cerr_capture_file;
    round_info.cerr_capture_file = 0;
  }
}



/******************************************************************************
 *
 * Handler for the SIGINT signal.
 *
 *****************************************************************************/

static void breakHandler(int)
{
  user_break = true;
}



/******************************************************************************
 *
 * Default handler for signals that terminate the process.
 *
 *****************************************************************************/

pid_t translator_process = 0; /* Process group for translator process */

static void abortHandler(int signum)
{
  deallocateTempFilenames();
  if (translator_process != 0 && kill(translator_process, 0) == 0)
    kill(-translator_process, SIGTERM);
  struct sigaction s;
  s.sa_handler = SIG_DFL;
  sigemptyset(&s.sa_mask);
  s.sa_flags = 0;
  sigaction(signum, &s, static_cast<struct sigaction*>(0));
  raise(signum);
}



/******************************************************************************
 *
 * Function for installing signal handlers.
 *
 *****************************************************************************/

static void installSignalHandler(int signum, void (*handler)(int))
{
  struct sigaction s;
  sigaction(signum, static_cast<struct sigaction*>(0), &s);

  if (s.sa_handler != SIG_IGN)
  {
    s.sa_handler = handler;
    sigemptyset(&s.sa_mask);
    s.sa_flags = 0;
    sigaction(signum, &s, static_cast<struct sigaction*>(0));
  }
}



/******************************************************************************
 *
 * Test loop.
 *
 *****************************************************************************/

bool testLoop()
{
  using namespace DispUtil;
  using namespace SharedTestData;
  using namespace StatDisplay;
  using namespace StringUtil;
  using namespace TestOperations;

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
   *  reading.  The special filename "-" refers to the standard input.
   */

  try
  {
    if (!global_options.formula_input_filename.empty())
    {
      if (global_options.formula_input_filename == "-")
	round_info.formula_input_stream = &cin;
      else
      {
	openFile(global_options.formula_input_filename.c_str(),
		 round_info.formula_input_file,
		 ios::in,
		 0);
	round_info.formula_input_stream = &round_info.formula_input_file;
      }
    }
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
      printText(string("Round ") + toString(round_info.current_round)
		+ " of " + toString(global_options.number_of_rounds) + "\n\n",
		2);

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

	for (vector<AlgorithmTestResults>::iterator it = test_results.begin();
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

	for (vector<AlgorithmTestResults>::iterator it = test_results.begin();
	     it != test_results.end();
	     ++it)
	  it->fullReset();

	if ((!round_info.error && !user_break && !round_info.skip)
	    || (round_info.next_round_to_run
		  < round_info.next_round_to_change_formula))
	{
	  try
	  {
	    generateFormulae(!global_options.formula_input_filename.empty()
			     ? round_info.formula_input_stream
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
	printText("[User break]\n\n", 1, 4);
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
	    && (!test_results[round_info.number_of_translators - 1].
		  automaton_stats[0].emptiness_check_performed)
	    && configuration.algorithms[round_info.number_of_translators - 1].
	         enabled)
	  verifyFormulaOnPath();

	if (!round_info.error)
        {
	  if (global_options.verbosity == 2)
	    ::StatDisplay::printStatTableHeader(round_info.cout, 4);

	  unsigned long int num_enabled_implementations = 0;

	  for (unsigned long int algorithm_id = 0;
	       algorithm_id < round_info.number_of_translators;
	       ++algorithm_id)
	  {
	    if (!configuration.algorithms[algorithm_id].enabled)
	      continue;

	    num_enabled_implementations++;

	    if (configuration.isInternalAlgorithm(algorithm_id))
	      continue;

	    printText(configuration.algorithmString(algorithm_id) + '\n',
		      3, 4);

	    for (int counter = 0; counter < 2; counter++)
	    {
	      if (user_break)
	      {
		printText("[User break]\n\n", 1, 4);
		throw UserBreakException();
	      }

	      if (global_options.verbosity == 1
		  || global_options.verbosity == 2)
	      {
		if (counter == 1)
		  round_info.cout << '\n';
		if (global_options.verbosity == 1)
		  round_info.cout << round_info.current_round << ' ';
		else
		  round_info.cout << string(4, ' ');
		changeStreamFormatting(cout, 2, 0, ios::right);
		round_info.cout << algorithm_id << ' ';
		restoreStreamFormatting(cout);
		round_info.cout << (counter == 0 ? '+' : '-') << ' ';
		round_info.cout.flush();
	      }
	      else
		printText(string(counter == 1 ? "Negated" : "Positive")
			  + " formula:\n",
			  3,
			  6);

	      try
	      {
		/*
		 *  Generate a Büchi automaton using the current algorithm.
		 *  `counter' determines the formula which is to be
		 *  translated into an automaton; 0 denotes the positive and
		 *  1 the negated formula.
		 */

		generateBuchiAutomaton(counter, algorithm_id);

		if (global_options.do_cons_test || global_options.do_comp_test)
		{
		  /*
		   *  Find the system states from which an accepting
		   *  execution cycle can be reached by checking the product
		   *  automaton for emptiness.
		   */

		  performEmptinessCheck(counter, algorithm_id);
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

	    printText("\n", 1);
	  }

	  if (global_options.verbosity == 2)
	  {
	    round_info.cout << '\n';
	    round_info.cout.flush();
	  }

	  if (num_enabled_implementations > 0)
	  {
	    if (global_options.do_comp_test)
	    {
	      /*
	       *  Perform the pairwise comparisons of the emptiness check
	       *  results obtained using the different algorithms.
	       */

	      if (num_enabled_implementations >= 2)
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

    /*
     *  Determine from the program configuration and the error status whether
     *  the testing should be paused to wait for user commands.
     */

    if (round_info.error)
      round_info.all_tests_successful = false;

    if (round_info.error
	&& global_options.interactive == Configuration::ONERROR)
      round_info.next_round_to_stop = round_info.current_round;

    if (round_info.next_round_to_stop == round_info.current_round)
      ::UserCommandInterface::executeUserCommands();
  }

  if (round_info.path_iterator != 0)
    delete round_info.path_iterator;
  else if (round_info.statespace != 0)
    delete round_info.statespace;

  for (int f = 0; f < 4; f++)
  {
    if (round_info.formulae[f] != 0)
      ::Ltl::LtlFormula::destruct(round_info.formulae[f]);
  }

  for (vector<AlgorithmTestResults>::iterator it = test_results.begin();
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

  if (global_options.verbosity >= 2)
    printCollectiveStats(cout, 0);

  if (round_info.formula_input_file.is_open())
    round_info.formula_input_file.close();

  return round_info.all_tests_successful;
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
    exit(2);
  }

#ifdef HAVE_ISATTY
  if (configuration.global_options.formula_input_filename == "-"
      && !isatty(STDIN_FILENO))
  {
    configuration.global_options.interactive = Configuration::NEVER;
    configuration.global_options.handle_breaks = false;
  }
#endif /* HAVE_ISATTY */

  if (configuration.global_options.verbosity >= 3)
    configuration.print(cout);

  user_break = false;

  installSignalHandler(SIGHUP, abortHandler);
  installSignalHandler(SIGINT,
		       configuration.global_options.handle_breaks
		       ? breakHandler
		       : abortHandler);
  installSignalHandler(SIGQUIT, abortHandler);
  installSignalHandler(SIGABRT, abortHandler);
  installSignalHandler(SIGPIPE, abortHandler);
  installSignalHandler(SIGALRM, abortHandler);
  installSignalHandler(SIGTERM, abortHandler);
  installSignalHandler(SIGUSR1, abortHandler);
  installSignalHandler(SIGUSR2, abortHandler);

#ifdef HAVE_OBSTACK_H
  obstack_alloc_failed_handler = &ObstackAllocator::failure;
#endif  /* HAVE_OBSTACK_H */

#ifdef HAVE_READLINE
  using_history();
#endif  /* HAVE_READLINE */

  try 
  {
    allocateTempFilenames();
    if (!testLoop())
    {
      deallocateTempFilenames();
      return 1;
    }
  }
  catch (const Exception& e)
  {
    deallocateTempFilenames();
    cerr << endl << argv[0] << ": " << e.what() << endl;
    exit(3);
  }
  catch (const bad_alloc&)
  {
    deallocateTempFilenames();
    cerr << endl << argv[0] << ": out of memory" << endl;
    exit(3);
  }

  deallocateTempFilenames();
  return 0;
}
