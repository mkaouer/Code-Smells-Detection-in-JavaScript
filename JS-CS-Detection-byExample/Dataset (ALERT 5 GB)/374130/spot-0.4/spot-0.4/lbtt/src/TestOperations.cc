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
#include <cerrno>
#include <csignal>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#ifdef HAVE_SYS_STAT_H
#include <sys/stat.h>
#endif /* HAVE_SYS_STAT_H */
#ifdef HAVE_SYS_TIMES_H
#include <sys/times.h>
#endif /* HAVE_SYS_TIMES_H */
#ifdef HAVE_SYS_TYPES_H
#include <sys/types.h>
#endif /* HAVE_SYS_TYPES_H */
#ifdef HAVE_SYS_WAIT_H
#include <sys/wait.h>
#endif /* HAVE_SYS_WAIT_H */
#ifdef HAVE_UNISTD_H
#include <unistd.h>
#endif /* HAVE_UNISTD_H */
#ifdef HAVE_FCNTL_H
#include <fcntl.h>
#endif /* HAVE_FCNTL_H */
#include "BitArray.h"
#include "BuchiAutomaton.h"
#include "BuchiProduct.h"
#include "DispUtil.h"
#include "Product.h"
#include "IntervalList.h"
#include "LtlFormula.h"
#include "PathEvaluator.h"
#include "Random.h"
#include "SccCollection.h"
#include "SharedTestData.h"
#include "PathIterator.h"
#include "StateSpace.h"
#include "StateSpaceProduct.h"
#include "StatDisplay.h"
#include "StringUtil.h"
#include "TempFsysName.h"
#include "TestOperations.h"
#include "TestRoundInfo.h"



extern pid_t translator_process;

/******************************************************************************
 *
 * Implementations for the operations used in the main test loop.
 *
 *****************************************************************************/

namespace TestOperations
{

using namespace ::SharedTestData;
using namespace ::StatDisplay;
using namespace ::StringUtil;
using namespace ::DispUtil;

/******************************************************************************
 *
 * Timeout handler.
 *
 *****************************************************************************/

bool timeout = false;

void timeoutHandler(int)
{
  timeout = true;
}

/* ========================================================================= */
void openFile
  (const char* filename, ifstream& stream, ios::openmode mode, int indent)
/* ----------------------------------------------------------------------------
 *
 * Description:   Function for opening a file for input.
 *
 * Arguments:     filename  --  A pointer to a constant C-style string
 *                              containing the name of the file to be opened.
 *                stream    --  A reference to the input stream that should be
 *                              associated with the file.
 *                mode      --  A constant of type `ios::openmode' determining
 *                              the open mode.
 *                indent    --  Number of spaces to leave to the left of
 *                              messages given to the user.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  printText(string("<opening `") + filename + "'>", 5, indent);

  stream.open(filename, mode);

  if (!stream.good())
  {
    printText(" error\n", 5);
    throw FileOpenException(string("`") + filename + "'");
  }
  else
    printText(" ok\n", 5);
}

/* ========================================================================= */
void openFile
  (const char* filename, ofstream& stream, ios::openmode mode, int indent)
/* ----------------------------------------------------------------------------
 *
 * Description:   Function for opening a file for output.
 *
 * Arguments:     filename  --  A pointer to a constant C-style string with
 *                              the name of the file to be opened.
 *                stream    --  A reference to the output stream that should be
 *                              associated with the file.
 *                mode      --  A constant of type `ios::openmode' determining
 *                              the open mode.
 *                indent    --  Number of spaces to leave to the left of
 *                              messages given to the user.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  printText(string("<") + (mode & ios::trunc ? "creat" : "open") + "ing `"
	    + filename + "'>",
	    5,
	    indent);

  stream.open(filename, mode);

  if (!stream.good())
  {
    printText(" error\n", 5);

    if (mode & ios::trunc)
      throw FileCreationException(string("`") + filename + "'");
    else
      throw FileOpenException(string("`") + filename + "'");
  }
  else
    printText(" ok\n", 5);
}

/* ========================================================================= */
void openFile(const char* filename, int& fd, int flags, int indent)
/* ----------------------------------------------------------------------------
 *
 * Description:   Function for opening a file for input/output using file
 *                descriptors.
 *
 * Arguments:     filename  --  A pointer to a constant C-style string with
 *                              the name of the file to be opened.
 *                fd        --  A reference to an int that should be associated
 *                              with the file descriptor of the file.  This
 *                              variable will have the value -1 if the
 *                              operation fails.
 *                flags     --  An integer specifying the open mode.
 *                indent    --  Number of spaces to leave to the left of
 *                              messages given to the user.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  printText(string("<") + (flags & O_CREAT ? "creat" : "open") + "ing `"
	    + filename + "'>",
	    5,
	    indent);

  if (flags & O_CREAT)
    fd = open(filename, flags, S_IRUSR | S_IWUSR);
  else
    fd = open(filename, flags);

  if (fd == -1)
  {
    printText(" error\n", 5);

    if (flags & O_CREAT)
      throw FileCreationException(string("`") + filename + "'");
    else
      throw FileOpenException(string("`") + filename + "'");
  }
  else
    printText(" ok\n", 5);
}

/* ========================================================================= */
void truncateFile(const char* filename, int indent)
/* ----------------------------------------------------------------------------
 *
 * Description:   Truncates a file.
 *
 * Arguments:     filename  --  A pointer to a constant C-style string with
 *                              the name of the file to be truncated.
 *                indent    --  Number of spaces to leave to the left of
 *                              messages given to the user.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  printText(string("<truncating `") + filename + "'>", 5, indent);

  int fd =  open(filename, O_RDWR | O_CREAT, S_IRUSR | S_IWUSR);
  if (fd != -1)
  {
    close(fd);
    printText(" ok\n", 5);
  }
  else
    printText(" error\n", 5);
}

/* ========================================================================= */
void printFileContents
  (ostream& stream, const char* message, const char* filename, int indent,
   const char* line_prefix)
/* ----------------------------------------------------------------------------
 *
 * Description:   Outputs the contents of a file into a stream.
 *
 * Arguments:     stream       --  A reference to the output stream into which
 *                                 the file contents should be outputted.
 *                message      --  A pointer to a constant C-style string
 *                                 containing a message to be outputted to the
 *                                 stream before the contents of the file. (To
 *                                 display only the file contents, use a null
 *                                 pointer for the message; an empty string
 *                                 results in an empty line to be outputted
 *                                 before the file contents.)
 *                filename     --  A pointer to a constant C-style string
 *                                 containing the name of the input file.
 *                indent       --  Number of spaces to leave to the left of
 *                                 output.
 *                line_prefix  --  A pointer to a constant C-style string
 *                                 that will be prepended to each outputted
 *                                 line.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  ifstream file;
  Exceptional_ostream estream(&stream, ios::failbit | ios::badbit);

  openFile(filename, file, ios::in, indent);

  bool first_line_printed = false;
  string message_line;

  printText(string("<reading from `") + filename + "'>\n", 5, indent);

  while (file.good())
  {
    message_line = "";

    getline(file, message_line, '\n');

    if (!file.eof())
    {
      if (!first_line_printed && message != 0)
      {
	first_line_printed = true;
	estream << string(indent, ' ') + message + '\n';
      }
                    
      estream << string(indent, ' ') + line_prefix + message_line + '\n';
    }
  }

  estream.flush();

  file.close();
}

/* ========================================================================= */
void writeToTranscript(const string& message, bool show_formula_in_header)
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes a message into the transcript file.  (If this is the
 *                first message to be written to the file in the current test
 *                round, the message is preceded with a header showing the
 *                round number and the LTL formulae used in the round.)
 *
 * Argument:      message                 --  A message to be written to the
 *                                            file.
 *                show_formula_in_header  --  If false, prevents displaying
 *                                            an LTL formula in the error
 *                                            report header (e.g., when it is
 *                                            the formula generation that
 *                                            failed).
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (round_info.error_report_round != round_info.current_round)
  {
    round_info.error_report_round = round_info.current_round;

    const string roundstring = "Round " + toString(round_info.current_round);

    round_info.transcript_file << roundstring + '\n'
                                  + string(roundstring.length(), '-')
                                  + "\n\n";

    if (show_formula_in_header)
    {
      const int formula
	= (configuration.formula_options.output_mode == Configuration::NNF
	   ? 0
	   : 2);

      try
      {
	round_info.transcript_file << "    Formula (+): ";
	round_info.formulae[formula]->print(round_info.transcript_file);
	round_info.transcript_file << "\n    Negated (-): ";
	round_info.formulae[formula + 1]->print(round_info.transcript_file);
	round_info.transcript_file << endl << endl;
      }
      catch (const IOException&)
      {
      }
    }
  }

  round_info.transcript_file << "    " + message + '\n';
  round_info.transcript_file.flush();
}

/* ========================================================================= */
void generateStateSpace()
/* ----------------------------------------------------------------------------
 *
 * Description:   Generates a random state space. The global variable
 *                `configuration.global_options.statespace_generation_mode'
 *                determines the type of state space to be generated.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing. The result can be accessed through
 *                `round_info.statespace'.
 *
 * ------------------------------------------------------------------------- */
{
  using ::Graph::StateSpace;

  if (configuration.global_options.statespace_generation_mode
        == Configuration::ENUMERATEDPATH)
  {
    StateSpace::size_type current_size
      = configuration.statespace_generator.min_size;

    /*
     *  If the state spaces are enumerated paths, generate the next path.
     */

    printText("Generating next state space\n", 2, 4);

    if (round_info.path_iterator != 0)
    {
      current_size = (*round_info.path_iterator)->size();
      ++(*round_info.path_iterator);
      if (round_info.path_iterator->atEnd())
      {
	delete round_info.path_iterator;
	round_info.path_iterator = 0;

	if (current_size < configuration.statespace_generator.max_size)
	{
	  current_size++;
	  printText("[Increasing state space size to " + toString(current_size)
		    + "]\n",
		    2,
		    6);
	}
	else 
	{
	  current_size = configuration.statespace_generator.min_size;
	  printText("[All state spaces have been enumerated. Staring over]\n",
		    2,
		    6);
	}
      }
    }

    if (round_info.path_iterator == 0)
    {
      round_info.path_iterator
	= new Graph::PathIterator
  	        (configuration.statespace_generator.atoms_per_state,
		 current_size);
    }

    printText("\n", 2);

    round_info.statespace = &(**round_info.path_iterator);
  }
  else
  {
    /*
     *  Otherwise generate a random state space.
     */

    if (round_info.statespace != 0)
    {
      delete round_info.statespace;
      round_info.statespace = 0;
    }

    if (printText("Generating random state space\n", 2, 4))
      printText("<generating>", 4, 6);

    /*
     *  Determine the type of the state space to be generated according to the
     *  configuration options, then generate it.
     */

    StateSpace* statespace;

    try
    {
      switch (configuration.global_options.statespace_generation_mode)
      {
        case Configuration::RANDOMGRAPH :
	  statespace = configuration.statespace_generator.generateGraph();
	  break;

        case Configuration::RANDOMCONNECTEDGRAPH :
	  statespace = configuration.statespace_generator.
	                 generateConnectedGraph();
	  break;

        default : /* Configuration::RANDOMPATH */
	  statespace = configuration.statespace_generator.generatePath();
	  break;
      }
    }
    catch (const UserBreakException&)
    {
      printText(" user break\n\n", 4);

      if (round_info.transcript_file.is_open())
	writeToTranscript("User break while generating state space. No tests "
			  "performed.\n", false);

      throw;
    }
    catch (const bad_alloc&)
    {
      if (!printText(" out of memory\n\n", 4))
	printText("[Out of memory]\n\n", 2, 6);

      if (round_info.transcript_file.is_open())
	writeToTranscript("Out of memory while generating state space. No "
			  "tests performed.\n", false);

      throw StateSpaceGenerationException();
    }

    round_info.statespace = statespace;

    printText(" ok\n", 4);

    if (configuration.statespace_generator.max_size 
 	  > configuration.statespace_generator.min_size)
      printText("number of states: "
		+ toString(round_info.statespace->size())
		+ '\n',
		3,
		6);

    printText("\n", 2);
  }

  round_info.num_generated_statespaces++;

  pair<StateSpace::size_type, unsigned long int>
    statespace_stats(round_info.statespace->stats());

  round_info.total_statespace_states += statespace_stats.first;
  round_info.total_statespace_transitions += statespace_stats.second;

  round_info.real_emptiness_check_size
    = (configuration.global_options.product_mode == Configuration::GLOBAL
       ? round_info.statespace->size()
       : 1);
}

/* ========================================================================= */
void generateFormulae(istream* formula_input_stream)
/* ----------------------------------------------------------------------------
 *
 * Description:   Generates a random LTL formula according to the current
 *                configuration and stores the formula, its negation and their
 *                negated normal forms into `round_info.formulae'.
 *
 * Argument:      formula_input_stream  --  A pointer to an input stream. If
 *                                          the pointer is nonzero, no random
 *                                          formula will be generated; instead,
 *                                          a formula will be read from the
 *                                          stream.
 *
 * Returns:       Nothing. The generated formulae can be found in the global
 *                array `round_info.formulae'.
 *
 * ------------------------------------------------------------------------- */
{
  for (int f = 0; f < 4; f++)
  {
    if (round_info.formulae[f] != 0)
    {
      ::Ltl::LtlFormula::destruct(round_info.formulae[f]);
      round_info.formulae[f] = static_cast<class ::Ltl::LtlFormula*>(0);
    }
  }

  if (printText(string(formula_input_stream == 0 ? "Random " : "")
		+ "LTL formula:\n",
		3,
		4))
    printText(string("<") + (formula_input_stream == 0 ? "generat" : "read")
	      + "ing>",
	      4,
	      6);
  else
    printText(string(formula_input_stream == 0
		     ? "Generating random"
		     : "Reading")
	      + " LTL formula\n",
	      2,
	      4);

  if (formula_input_stream != 0)
  {
    /*
     *  If a valid pointer to a stream was given as a parameter, try to read a
     *  formula from the stream.
     */

    try
    {
      try
      {
	round_info.formulae[2]
	  = ::Ltl::LtlFormula::read(*formula_input_stream);
      }
      catch (...)
      {
	printText(" error\n", 4);
	throw;
      }
    }
    catch (const ::Ltl::LtlFormula::ParseErrorException&)
    {
      printText(string("[Error parsing the ")
		+ (formula_input_stream == &round_info.formula_input_file
		   ? "formula file"
		   : "input formula")
		+ ". Aborting]\n",
		2,
		6);

      if (round_info.transcript_file.is_open())
	writeToTranscript("Error parsing input formula. Testing aborted.\n",
			  false);

      throw FormulaGenerationException();
    }
    catch (const IOException&)
    {
      bool fatal_io_error
	= (configuration.global_options.formula_input_filename.empty()
	   || !round_info.formula_input_stream->eof());

      printText(string("[") + (fatal_io_error
			       ? "Error reading formula"
			       : "No more input formulae")
		+ ". Aborting]\n",
		2,
		6);

      if (round_info.transcript_file.is_open())
	writeToTranscript(fatal_io_error
			  ? "Error reading input formula. Testing "
			    "aborted.\n"
			  : "No more input formulae. Testing aborted.\n",
			  false);

      throw FormulaGenerationException();
    }

    printText(" ok\n", 4);
  }
  else
  {
    /*
     *  Otherwise generate a random formula.
     */

    try
    {
      round_info.formulae[2]
	= configuration.formula_options.formula_generator.generate();
    }
    catch (...)
    {
      printText(" error\n", 4);
      throw;
    }

    printText(" ok\n", 4);

    if (configuration.formula_options.formula_generator.max_size
	  > configuration.formula_options.formula_generator.size)
      printText("parse tree size:" + string(11, ' ')
		+ toString(round_info.formulae[2]->size()) + '\n',
		3,
		6);
  }

  ++round_info.num_processed_formulae;

  printText("<converting to negation normal form>", 4, 6);

  round_info.formulae[0] = round_info.formulae[2]->nnfClone();

  if (printText(" ok\n", 4))
    printText("<negating formula>", 4, 6);
  
  round_info.formulae[3] = &(::Ltl::Not::construct(*round_info.formulae[2]));

  if (printText(" ok\n", 4))
    printText("<converting negated formula to negation normal form>", 4, 6);

  round_info.formulae[1] = round_info.formulae[3]->nnfClone();

  if (configuration.global_options.verbosity >= 3)
  {
    printText(" ok\n", 4);

    for (int f = 0; f <= 1; f++)
    {
      round_info.cout << string(6, ' ') + (f == 0 ? "" : "negated ")
	                 + "formula:" + string(19 - 8 * f, ' ');
      round_info.formulae[f + 2]->print(round_info.cout);
      round_info.cout << '\n';

      if (configuration.formula_options.output_mode == Configuration::NNF)
      {
	round_info.cout << string(8, ' ') + "in negation normal form: ";
	round_info.formulae[f]->print(round_info.cout);
	round_info.cout << '\n';
      }
    }
  }

  printText("\n", 2);
}

/* ========================================================================= */
void verifyFormulaOnPath()
/* ----------------------------------------------------------------------------
 *
 * Description:   Model checks an LTL formula (accessed through the global
 *                data structure `round_info' on a path directly and stores the
 *                result into the test result data structure.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing. The model checking results are stored into the test
 *                result data structure.
 *
 * ------------------------------------------------------------------------- */
{
  if (printText("Model checking formula using internal algorithm\n", 2, 4))
    printText("<model checking>", 4, 6);

  test_results[round_info.number_of_translators - 1].automaton_stats[0].
    emptiness_check_result.clear();
  test_results[round_info.number_of_translators - 1].automaton_stats[1].
    emptiness_check_result.clear();

  try
  {
    int formula = (configuration.formula_options.output_mode
		     == Configuration::NNF
		   ? 0
		   : 2);

    Ltl::PathEvaluator path_evaluator;
    path_evaluator.evaluate(*round_info.formulae[formula],
			    *(round_info.statespace));

    for (unsigned long int s = 0; s < round_info.real_emptiness_check_size;
	 s++)
    {
      if (path_evaluator.getResult(s))
	test_results[round_info.number_of_translators - 1].automaton_stats[0].
	  emptiness_check_result.setBit(s);
      else
	test_results[round_info.number_of_translators - 1].automaton_stats[1].
	  emptiness_check_result.setBit(s);
    }
  }
  catch (const UserBreakException&)
  {
    if (!printText(" aborted (user break)", 4))
      printText("[User break]", 2, 6);
    printText("\n\n", 2);

    if (round_info.transcript_file.is_open())
      writeToTranscript("User break while model checking formulas. No tests "
			"performed.\n");

    throw;
  }
  catch (const bad_alloc&)
  {
    if (!printText(" aborted (out of memory)", 4))
      printText("[Out of memory]", 2, 6);
    printText("\n\n", 2);

    if (round_info.transcript_file.is_open())
      writeToTranscript("Out of memory while model checking formulas. No "
			"tests performed.\n");

    round_info.error = true;
    return;
  }

  printText(" ok\n", 4);
  printText("\n", 2);

  test_results[round_info.number_of_translators - 1].automaton_stats[0].
    emptiness_check_performed = true;
  test_results[round_info.number_of_translators - 1].automaton_stats[1].
    emptiness_check_performed = true;
}

/* ========================================================================= */
void writeFormulaeToFiles()
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes the LTL formulae used in the tests into files in the
 *                output mode specified in the program configuration.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  ofstream formula_file;

  for (int f = 0; f < 2; f++)
  {
    Exceptional_ostream eformula_file(&formula_file,
				      ios::failbit | ios::badbit);

    if (!round_info.formula_in_file[f])
    {
      try
      {
	openFile(round_info.formula_file_name[f]->get(), formula_file,
		 ios::out | ios::trunc, 6);

	printText(string("<writing ") + (f == 0 ? "posi" : "nega")
		  + "tive formula to `"
		  + round_info.formula_file_name[f]->get()
		  + "'>\n",
		  5,
		  6);

	round_info.formulae[configuration.formula_options.
			      output_mode == Configuration::NNF
			    ? f
			    : f + 2]->print(eformula_file, ::Ltl::LTL_PREFIX);

	eformula_file << '\n';

	formula_file.close();
	round_info.formula_in_file[f] = true;
      }
      catch (const IOException& e)
      {
	if (formula_file.is_open())
	  formula_file.close();

	printText(string("Error: ") + e.what() + "\n\n", 2, 6);

	round_info.error = true;
	return;
      }
    }
  }
}

/* ========================================================================= */
void generateBuchiAutomaton
  (int f,
   vector<Configuration::AlgorithmInformation>::size_type algorithm_id)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructs a BuchiAutomaton by invoking an external program
 *                that will perform the conversion of a LTL formula (stored
 *                into a file) into a Büchi automaton.
 *
 * Arguments:     f             --  Indicates the formula to be converted into
 *                                  an automaton. 0 corresponds to the positive
 *                                  and 1 to the negated formula.
 *                algorithm_id  --  Identifier of the LTL-to-Büchi translator
 *                                  to use.
 *
 * Returns:       Nothing. The result is stored in
 *                    `test_results[algorithm_id].automaton_stats[f].
 *                       buchi_automaton'.
 *
 * ------------------------------------------------------------------------- */
{
  using ::Graph::BuchiAutomaton;

  AutomatonStats& automaton_stats
    = test_results[algorithm_id].automaton_stats[f];

  if (automaton_stats.buchiAutomatonComputed())
    printText("Büchi automaton (cached):\n", 3, 8);
  else
  {
    printText("Büchi automaton:\n", 3, 8);

    const Configuration::AlgorithmInformation& algorithm
      = configuration.algorithms[algorithm_id];

    final_statistics[algorithm_id].buchi_automaton_count[f]++;

    BuchiAutomaton* buchi_automaton = new BuchiAutomaton();

    struct tms timing_information_begin, timing_information_end;

    string failure_reason;
    int stdout_capture_fileno = -1, stderr_capture_fileno = -1;
    int exitcode;

    struct sigaction timeout_sa;
    timeout_sa.sa_handler = timeoutHandler;
    sigemptyset(&timeout_sa.sa_mask);
    timeout_sa.sa_flags = 0;

    truncateFile(round_info.automaton_file_name->get(), 10);
    truncateFile(round_info.cout_capture_file->get(), 10);
    truncateFile(round_info.cerr_capture_file->get(), 10);

    try
    {
      automaton_stats.buchi_generation_time = -1.0;

      /*
       *  Redirect standard output and standard error to files.
       */

      try
      {
	openFile(round_info.cout_capture_file->get(), stdout_capture_fileno,
		 O_CREAT | O_WRONLY | O_TRUNC, 10);

	openFile(round_info.cerr_capture_file->get(), stderr_capture_fileno,
		 O_CREAT | O_WRONLY | O_TRUNC, 10);
      }
      catch (const FileCreationException&)
      {
	if (stdout_capture_fileno != -1)
	{
	  close(stdout_capture_fileno);
	  stdout_capture_fileno = -1;
	}
	printText(string(9, ' '), 4);
	throw Exception(string("redirection of standard ")
			+ (stdout_capture_fileno == -1 ? "output" : "error")
			+ " failed ("
                        + string(strerror(errno)) + ")");
      }

      /* Execute the external program. */

      if (!printText("<executing translator>", 5, 10))
	printText("<computing Büchi automaton>", 4, 10);

      int error_number;
      int error_pipe[2]; /* used for communicating errors in exec() */

      double elapsed_time = -1.0;

      if (pipe(error_pipe) == -1)
	error_number = errno;
      else
      {
	algorithm.parameters[algorithm.num_parameters + 1]
	  = const_cast<char*>(round_info.formula_file_name[f]->get());
	algorithm.parameters[algorithm.num_parameters + 2]
	  = const_cast<char*>(round_info.automaton_file_name->get());

	times(&timing_information_begin);
	translator_process = fork();
	switch (translator_process)
	{
	  case 0 : /* child */
	    close(error_pipe[0]);

	    if (setpgid(0, 0) != -1
		&& dup2(stdout_capture_fileno, STDOUT_FILENO) != -1
		&& dup2(stderr_capture_fileno, STDERR_FILENO) != -1)
	      execvp(algorithm.parameters[0], algorithm.parameters);

	    /* setsid, dup2 or exec failed: write the value of errno to
	     * error_pipe */

	    write(error_pipe[1], static_cast<const void*>(&errno),
		  sizeof(int));
	    close(error_pipe[1]);
	    exit(0);

	  case -1 : /* fork failed */
	    translator_process = 0;
	    error_number = errno;
	    close(error_pipe[0]);
	    close(error_pipe[1]);
	    break;

	  default : /* parent */
	    setpgid(translator_process, translator_process);

	    if (configuration.global_options.handle_breaks)
	    {
	      /* If lbtt is currently in the foreground (and has a controlling
		 terminal), transfer the controlling terminal to the translator
		 process. */

	      const pid_t foreground_pgrp = tcgetpgrp(STDIN_FILENO);
	      if (foreground_pgrp != -1 && foreground_pgrp == getpgrp())
	      {
		sigset_t mask;
		sigemptyset(&mask);
		sigaddset(&mask, SIGTTOU);
		sigprocmask(SIG_BLOCK, &mask, 0);
		tcsetpgrp(STDIN_FILENO, translator_process);
		sigprocmask(SIG_UNBLOCK, &mask, 0);
	      }
	    }

	    /* Install handler for timeouts if necessary. */

	    if (configuration.global_options.translator_timeout > 0)
	    {
	      sigaction(SIGALRM, &timeout_sa,
			static_cast<struct sigaction*>(0));
	      timeout = false;
	      alarm(configuration.global_options.translator_timeout);
	    }

	    close(error_pipe[1]);

	    if (waitpid(translator_process, &exitcode, 0) == -1)
	      /* waitpid failed */
	    {
	      error_number = errno;
	      if (kill(translator_process, 0) == 0) /* child still running */
	      {
		/*
		 *  Try to terminate the child process three times with SIGTERM
		 *  (sleeping for one second between the tries). If the child
		 *  fails to respond, try to terminate the child one more time
		 *  with SIGKILL.
		 */

		int sig = SIGTERM;
		unsigned int delay = 1;
		for (int attempts_to_terminate = 0; attempts_to_terminate < 4;
		     ++attempts_to_terminate)
		{
		  kill(-translator_process, sig);
		  sleep(delay);
		  if (waitpid(translator_process, &exitcode, WNOHANG) != 0)
		  {
		    times(&timing_information_end);
		    translator_process = 0;
		    break;
		  }
		  if (attempts_to_terminate == 2)
		  {
		    sig = SIGKILL;
		    delay = 5;
		  }
		}
	      }
	      else if (errno != EPERM)
		translator_process = 0;
	    }
	    else /* child exited successfully */
	    {
	      times(&timing_information_end);
	      translator_process = 0;

	      /*  
	       *  If there is something to be read from error_pipe, then there
	       *  was an error in replacing the child process with the external
	       *  program (and the pipe contains the value of errno in this
	       *  case).
	       */

	      if (read(error_pipe[0], static_cast<void*>(&error_number),
		       sizeof(int)) == 0)
		error_number = 0;
	    }

	    close(error_pipe[0]);

	    /* Restore signal handlers and remove any pending alarms. */

	    if (configuration.global_options.translator_timeout > 0)
	    {
	      timeout_sa.sa_handler = SIG_DFL;
	      sigaction(SIGALRM, &timeout_sa,
			static_cast<struct sigaction*>(0));
	      alarm(0);
	    }

	    if (configuration.global_options.handle_breaks)
	    {
	      /* Put lbtt again in the foreground. */

	      if (tcgetpgrp(STDIN_FILENO) != -1)
	      {
		sigset_t mask;
		sigemptyset(&mask);
		sigaddset(&mask, SIGTTOU);
		sigprocmask(SIG_BLOCK, &mask, 0);
		tcsetpgrp(STDIN_FILENO, getpgrp());
		sigprocmask(SIG_UNBLOCK, &mask, 0);
	      }
	    }

	    if (translator_process == 0
		&& timing_information_begin.tms_utime
		     != static_cast<clock_t>(-1)
		&& timing_information_begin.tms_cutime
		     != static_cast<clock_t>(-1)
		&& timing_information_end.tms_utime != static_cast<clock_t>(-1)
		&& timing_information_end.tms_cutime
		     != static_cast<clock_t>(-1))
	      elapsed_time = static_cast<double>
		               (timing_information_end.tms_utime
				+ timing_information_end.tms_cutime
				- timing_information_begin.tms_utime
				- timing_information_begin.tms_cutime)
		             / sysconf(_SC_CLK_TCK);

	    break;
	}
      }

      close(stdout_capture_fileno);
      close(stderr_capture_fileno);

      /*
       *  If translator_process != 0 at this point, then a timeout occurred,
       *  but lbtt was unable to terminate the child process. The exception
       *  handler will in this case throw an unexpected exception (see below)
       *  so that lbtt will terminate (for example, it is not safe to use the
       *  temporary file names any longer if the (still running) child process
       *  happens to write to them).
       */

      if (translator_process != 0)
      {
	stdout_capture_fileno = stderr_capture_fileno = -1;
	throw Exception("could not terminate child process");
      }

      if (error_number != 0) /* pipe, fork, setsid, dup2, execvp or waitpid
			      * failed */
      {
	stdout_capture_fileno = stderr_capture_fileno = -1;
	ExecFailedException e;

	if (configuration.global_options.translator_timeout > 0 && timeout)
	  e.changeMessage("Automaton generation aborted due to timeout.");
	else
	  e.changeMessage("Execution of `" + string(algorithm.parameters[0])
			  + "' failed (" + string(strerror(error_number))
			  + ")");
	throw e;
      }

      automaton_stats.buchi_generation_time = elapsed_time;

      /*
       *  Nonzero exit codes from the external program are interpreted as
       *  errors. The same holds if the program was aborted by a signal. In
       *  these cases, throw an exception indicating that the program
       *  execution failed.
       */

      if (WIFSIGNALED(exitcode)
	  || (WIFEXITED(exitcode) && WEXITSTATUS(exitcode) != 0))
      {
	ExecFailedException e;
	failure_reason = "`" + string(algorithm.parameters[0]) + "' ";

	if (WIFSIGNALED(exitcode))
	{
          failure_reason += "aborted by signal "
	                    + toString(WTERMSIG(exitcode));

#ifdef HAVE_STRSIGNAL
	  const char* signame = strsignal(WTERMSIG(exitcode));
	  if (signame != 0)
	    failure_reason += " (" + string(signame) + ")";
#endif /* HAVE_STRSIGNAL */

	  if (WTERMSIG(exitcode) == SIGINT || WTERMSIG(exitcode) == SIGQUIT)
	    raise(WTERMSIG(exitcode));
	}
	else
	  failure_reason += "exited with exit status "
	                    + toString(WEXITSTATUS(exitcode));

	e.changeMessage(failure_reason
			+ (automaton_stats.buchi_generation_time >= 0.0
			   ? " after "
			     + toString(automaton_stats.buchi_generation_time,
					2)
			     + " seconds"
			   : string("")));

	throw e;
      }

      printText(" ok\n", 4);

      /*
       *  Read the automaton description into memory from the result file.
       */

      ifstream automaton_file;
      openFile(round_info.automaton_file_name->get(), automaton_file, ios::in,
	       10);

      printText("<reading automaton description>", 4, 10);

      try
      {
	automaton_file >> *buchi_automaton;
      }
      catch (const bad_alloc&)
      {
	throw Exception("out of memory");
      }

      automaton_file.close();

      printText(" ok\n", 4);

      automaton_stats.buchi_automaton = buchi_automaton;
    }
    catch (...)
    {
      delete buchi_automaton;

      if (user_break)
      {
	if (!printText(" aborted (user break)", 4))
	  printText("[User break]", 1,
		    configuration.global_options.verbosity <= 2 ? 0 : 10);
	printText("\n\n", 1);
       
	if (round_info.transcript_file.is_open())
	  writeToTranscript("User break while generating Büchi automaton ("
			    + configuration.algorithmString(algorithm_id)
			    + ", "
			    + (f == 0 ? "posi" : "nega") + "tive formula)\n");

	throw UserBreakException();
      }

      if (round_info.transcript_file.is_open())
	writeToTranscript("Büchi automaton generation failed ("
                          + configuration.algorithmString(algorithm_id)
	                  + ", "
		  	  + (f == 0 ? "posi" : "nega")
			  + "tive formula)"
	                  + (automaton_stats.buchi_generation_time >= 0.0
			     ? "\n" + string(8, ' ') + "Elapsed time: "
			       + toString(automaton_stats.
					    buchi_generation_time,
					  2)
			       + " seconds (user time)"
			     : string("")));

      try
      {
	throw;
      }
      catch (const ExecFailedException& e)
      {
	if (configuration.global_options.translator_timeout > 0 && timeout)
	{
	  if (!printText(" aborted (timeout)", 4))
	    printText("[Timeout]", 1,
		      configuration.global_options.verbosity <= 2 ? 0 : 10);
	}
	else
	{
	  if (!printText(string(" error: ") + e.what(), 4))
	    printText("[Failed to execute translator]", 1,
		      configuration.global_options.verbosity <= 2 ? 0 : 10);
	}
	printText("\n", 3);
	if (round_info.transcript_file.is_open())
	  round_info.transcript_file << string(8, ' ') + e.what() + "\n";
      }
      catch (const BuchiAutomaton::AutomatonParseException& e)
      {
	if (!printText(string(" error parsing input: ") + e.what(), 4))
	  printText("[Error parsing automaton]", 1,
		    configuration.global_options.verbosity <= 2 ? 0 : 10);
	printText("\n", 3);
	if (round_info.transcript_file.is_open())
	  round_info.transcript_file << string(8, ' ')
	                                + "Error reading automaton: "
	                                + e.what()
                                        + "\n";
      }
      catch (const Exception& e)
      {
	if (!printText(string(" lbtt internal error: ") + e.what(), 4))
	  printText("[lbtt internal error]", 1,
		    configuration.global_options.verbosity <= 2 ? 0 : 10);
	printText("\n", 3);
	if (round_info.transcript_file.is_open())
	  round_info.transcript_file << string(8, ' ')
                                        + "lbtt internal error: "
	                                + e.what()
	                                + "\n";
      }

      try
      {
	if (stdout_capture_fileno != -1)
        {
	  const char* msg = "Contents of stdout";

	  if (configuration.global_options.verbosity >= 3)
	    printFileContents(cout, msg, round_info.cout_capture_file->get(),
			      10, "> ");
	  if (round_info.transcript_file.is_open())
	    printFileContents(round_info.transcript_file, msg,
			      round_info.cout_capture_file->get(), 10, "> ");
	}

	if (stderr_capture_fileno != -1)
        {
	  const char* msg = "Contents of stderr:";

	  if (configuration.global_options.verbosity >= 3)
	    printFileContents(cout, msg, round_info.cerr_capture_file->get(),
			      10, "> ");
	  if (round_info.transcript_file.is_open())
	    printFileContents(round_info.transcript_file, msg,
			      round_info.cerr_capture_file->get(), 10, "> ");
	}
      }
      catch (const IOException&)
      {
      }

      if (round_info.transcript_file.is_open())
      {
	round_info.transcript_file << '\n';
	round_info.transcript_file.flush();
      }

      if (translator_process != 0) /* fatal error, lbtt should be terminated */
	throw Exception
	        ("fatal internal error while generating Büchi automaton");

      throw BuchiAutomatonGenerationException();
    }

    if (configuration.global_options.verbosity >= 3)
    {
      try
      {
	printFileContents(cout, "Contents of stdout:",
			  round_info.cout_capture_file->get(), 10, "> ");
	printFileContents(cout, "Contents of stderr:",
			  round_info.cerr_capture_file->get(), 10, "> ");
      }
      catch (const IOException&)
      {
      }
    }

    printText("<computing statistics>", 4, 10);

    pair<BuchiAutomaton::size_type, unsigned long int> buchi_stats
      = automaton_stats.buchi_automaton->stats();

    automaton_stats.number_of_buchi_states = buchi_stats.first;
    automaton_stats.number_of_buchi_transitions = buchi_stats.second;
    automaton_stats.number_of_acceptance_sets
      = automaton_stats.buchi_automaton->numberOfAcceptanceSets();

    /*
     *  Update Büchi automaton statistics for the given algorithm.
     */

    final_statistics[algorithm_id].total_number_of_buchi_states[f]
      += automaton_stats.number_of_buchi_states;
    final_statistics[algorithm_id].total_number_of_buchi_transitions[f]
      += automaton_stats.number_of_buchi_transitions;
    final_statistics[algorithm_id].total_number_of_acceptance_sets[f]
      += automaton_stats.number_of_acceptance_sets;
		  
    if (final_statistics[algorithm_id].total_buchi_generation_time[f] < 0.0
	|| automaton_stats.buchi_generation_time < 0.0)
      final_statistics[algorithm_id].total_buchi_generation_time[f] = -1.0;
    else
      final_statistics[algorithm_id].total_buchi_generation_time[f]
	+= automaton_stats.buchi_generation_time;

    printText(" ok\n", 4);
  }

  if (configuration.global_options.verbosity >= 1)
    printBuchiAutomatonStats(cout, 10, algorithm_id, f);
}

/* ========================================================================= */
void performEmptinessCheck
  (int f,
   vector<Configuration::AlgorithmInformation>::size_type algorithm_id)
/* ----------------------------------------------------------------------------
 *
 * Description:   Performs the emptiness check on a ProductAutomaton, i.e.,
 *                finds the states of the original state space from which an
 *                accepting cycle of the Büchi automaton can be reached.
 *
 * Arguments:     f             --  Indicates the formula originally used for
 *                                  constructing the given product automaton.
 *                                  0 corresponds to the automaton obtained
 *                                  from the positive, 1 to the one obtained
 *                                  from the negated formula.
 *                algorithm_id  --  Identifier of the LTL-to-Büchi translator
 *                                  originally used for generating the given
 *                                  product automaton.
 *
 * Returns:       Nothing. The test state variables are updated according to
 *                the results.
 *
 * ------------------------------------------------------------------------- */
{
  AutomatonStats& automaton_stats
    = test_results[algorithm_id].automaton_stats[f];

  const bool result_cached = automaton_stats.emptiness_check_performed;

  printText("Product automaton"
	    + string(result_cached ? " (cached)" : "")
	    + ":\n",
	    3,
	    8);

  if (!result_cached)
  {
    printText("<checking product automaton for emptiness>", 4, 10);

    final_statistics[algorithm_id].product_automaton_count[f]++;

    using ::Graph::StateSpaceProduct;
    using ::Graph::Product;

    try
    {
      Product<StateSpaceProduct>
	product(*automaton_stats.buchi_automaton, *round_info.statespace);

      const pair<Product<StateSpaceProduct>::size_type, unsigned long int>
	product_stats =  product.globalEmptinessCheck
	                   (automaton_stats.buchi_automaton->initialState(),
			    automaton_stats.emptiness_check_result,
			    round_info.real_emptiness_check_size);	 

      printText(" ok\n", 4);
    
      automaton_stats.number_of_product_states = product_stats.first;
      automaton_stats.number_of_product_transitions = product_stats.second;

      final_statistics[algorithm_id].total_number_of_product_states[f]
	+= automaton_stats.number_of_product_states;
      final_statistics[algorithm_id].total_number_of_product_transitions[f]
	+= automaton_stats.number_of_product_transitions;
      automaton_stats.emptiness_check_performed = true;
    }
    catch (const Product<StateSpaceProduct>::SizeException&)
    {
      if (!printText(" aborted (product may be too large)", 4))
	printText("[Product may be too large]", 1,
		  configuration.global_options.verbosity <= 2 ? 0 : 10);
      printText("\n", 3);

      if (round_info.transcript_file.is_open())
	writeToTranscript("Product automaton generation aborted ("
			  + configuration.algorithmString(algorithm_id)
			  + ", "
			  + (f == 0 ? "posi" : "nega") + "tive formula)"
			  + ". Product may be too large.\n");

      throw ProductAutomatonGenerationException();
    }
    catch (const UserBreakException&)
    {
      if (!printText(" aborted (user break)", 4))
	printText("[User break]", 1,
		  configuration.global_options.verbosity <= 2 ? 0 : 10);
      printText("\n\n", 1);
       
      if (round_info.transcript_file.is_open())
	writeToTranscript("User break while generating product automaton ("
			  + configuration.algorithmString(algorithm_id)
			  + ", "
			  + (f == 0 ? "posi" : "nega") + "tive formula)\n");

      throw;
    }
    catch (const bad_alloc&)
    {
      if (!printText(" aborted (out of memory)", 4))
	printText("[Out of memory]", 1,
		configuration.global_options.verbosity <= 2 ? 0 : 10);
      printText("\n", 3);

      if (round_info.transcript_file.is_open())
	writeToTranscript("Out of memory while generating product "
			  "automaton ("
			  + configuration.algorithmString(algorithm_id)
			  + ", "
			  + (f == 0 ? "posi" : "nega") + "tive formula)\n");

      throw ProductAutomatonGenerationException();
    }
  }

  if (configuration.global_options.verbosity >= 1)
    printProductAutomatonStats(cout, 10, algorithm_id, f);

  printText("Accepting cycles" + string(result_cached ? " (cached)" : "")
	    + ":\n",
	    3,
	    8);
  if (configuration.global_options.verbosity >= 1)
    printAcceptanceCycleStats(cout, 10, algorithm_id, f);

}

/* ========================================================================= */
void performConsistencyCheck
  (vector<Configuration::AlgorithmInformation>::size_type algorithm_id)
/* ----------------------------------------------------------------------------
 *
 * Description:   Checks the model checking results for consistency for a
 *                particular LTL-to-Büchi conversion algorithm implementation,
 *                i.e., verifies that the model checking results for a formula
 *                and its negation are not contradictory.
 *
 * Arguments:     algorithm_id  --  Identifier of an algorithm for which the
 *                                  model checking result consistency check
 *                                  should be performed.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  bool result = true;

  if (test_results[algorithm_id].consistency_check_result == -1)
  {
    StateSpace::size_type state;
    const Bitset& acceptance_vector_for_formula
      = test_results[algorithm_id].automaton_stats[0].emptiness_check_result;
    const Bitset& acceptance_vector_for_negation
      = test_results[algorithm_id].automaton_stats[1].emptiness_check_result;

    if (printText("Result consistency check:\n", 3, 6))
      printText("<comparing results>", 4, 8);

    /*
     *  The consistency check will succeed if `result' is still true at the end
     *  of the following loop.
     *
     *  The consistency check fails if there is a state in which both the
     *  formula and its negation are claimed to be false.
     */

    final_statistics[algorithm_id].consistency_checks_performed++;

    for (state = 0; state < round_info.real_emptiness_check_size; ++state)
    {
      ++test_results[algorithm_id].consistency_check_comparisons;
      if (!acceptance_vector_for_formula[state]
	  && !acceptance_vector_for_negation[state])
      {
	++test_results[algorithm_id].failed_consistency_check_comparisons;
	result = false;
      }
    }

    test_results[algorithm_id].consistency_check_result = (result ? 1 : 0);
  }
  else
    result = (test_results[algorithm_id].consistency_check_result == 1);

  if (!result)
  {
    round_info.error = true;

    if (round_info.transcript_file.is_open())
      writeToTranscript("Model checking result consistency check failed ("
			+ configuration.algorithmString(algorithm_id) + ")\n");

    final_statistics[algorithm_id].consistency_check_failures++;
  }

  printText((result ? " ok\n" : " failed\n"), 4);
  
  if (configuration.global_options.verbosity >= 2)
    printConsistencyCheckStats(cout, 8, algorithm_id);
}      

/* ========================================================================= */
void compareResults()
/* ----------------------------------------------------------------------------
 *
 * Description:   Compares the model checking results obtained using different
 *                LTL->Büchi conversion algorithm implementations with each
 *                other.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (printText("Model checking result cross-comparison:\n", 3, 4))
    printText("<comparing results>", 4, 6);
  else
    printText("Comparing model checking results", 2, 4);

  bool result = true;
  AutomatonStats* alg_1_stats;
  AutomatonStats* alg_2_stats;

  for (vector<AlgorithmTestResults>::size_type alg_1 = 0;
       alg_1 < test_results.size();
       ++alg_1)
  {
    for (int counter = 0; counter < 2; counter++)
    {
      alg_1_stats = &test_results[alg_1].automaton_stats[counter];

      for (vector<AlgorithmTestResults>::size_type alg_2 = alg_1 + 1;
	   alg_2 < test_results.size();
	   ++alg_2)
      {
	alg_2_stats = &test_results[alg_2].automaton_stats[counter];

	if (configuration.algorithms[alg_1].enabled
	    && configuration.algorithms[alg_2].enabled
	    && alg_1_stats->emptiness_check_performed
	    && alg_2_stats->emptiness_check_performed)
	{
	  if (!alg_1_stats->cross_comparison_stats[alg_2].first)
	  {
	    (final_statistics[alg_1].cross_comparisons_performed[alg_2])++;
	    (final_statistics[alg_2].cross_comparisons_performed[alg_1])++;

	    unsigned long int dist
	      = alg_1_stats->emptiness_check_result.hammingDistance
	          (alg_2_stats->emptiness_check_result);

	    alg_1_stats->cross_comparison_stats[alg_2].first
	      = alg_2_stats->cross_comparison_stats[alg_1].first
	      = true;

	    alg_1_stats->cross_comparison_stats[alg_2].second
	      = alg_2_stats->cross_comparison_stats[alg_1].second
	      = dist;

	    if (dist > 0)
	    {
	      (final_statistics[alg_1].cross_comparison_mismatches[alg_2])++;
	      (final_statistics[alg_2].cross_comparison_mismatches[alg_1])++;

	      if (alg_1_stats->emptiness_check_result[0]
		  != alg_2_stats->emptiness_check_result[0])
	      {
		(final_statistics[alg_1].
	           initial_cross_comparison_mismatches[alg_2])++;
		(final_statistics[alg_2].
	           initial_cross_comparison_mismatches[alg_1])++;
	      }

	      result = false;
	    }
	  }
	  else if (alg_1_stats->cross_comparison_stats[alg_2].second != 0)
	    result = false;
	}
      }
    }
  }

  IntervalList algorithms;
  algorithms.merge(0, round_info.number_of_translators - 1);

  if (!result)
  {
    round_info.error = true;

    if (round_info.transcript_file.is_open())
    {
      writeToTranscript("Model checking result cross-comparison check failed");
      printCrossComparisonStats(round_info.transcript_file, 8,
				algorithms);
    }
  }

  printText((result ? " ok\n" : " failed\n"), 4);
  if (configuration.global_options.verbosity == 2)
  {
    if (!result)
      round_info.cout << " [failed]";
    round_info.cout << '\n';
    round_info.cout.flush();
  }
  else if (configuration.global_options.verbosity >= 3)
    printCrossComparisonStats(cout, 6, algorithms);
}

/* ========================================================================= */
void performBuchiIntersectionCheck()
/* ----------------------------------------------------------------------------
 *
 * Description:   Tests the intersection of the Büchi automata constructed for
 *                the formula and its negation for emptiness.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (printText("Büchi automata intersection emptiness check:\n", 3, 4))
    printText("<checking Büchi automata intersections for emptiness>\n", 4, 6);
  else
    printText("Checking Büchi automata intersections for emptiness", 2, 4);

  bool result = true;

  ::Graph::BuchiProduct::clearSatisfiabilityCache();

  for (vector<AlgorithmTestResults>::size_type alg_1 = 0;
       alg_1 < round_info.number_of_translators;
       ++alg_1)
  {
    for (vector<AlgorithmTestResults>::size_type alg_2 = 0;
	 alg_2 < round_info.number_of_translators;
	 ++alg_2)
    {
      if (configuration.isInternalAlgorithm(alg_1)
	  || configuration.isInternalAlgorithm(alg_2))
	continue;

      try
      {
	if (test_results[alg_1].automaton_stats[0].
	      buchi_intersection_check_stats[alg_2] == -1)
	{
	  printText("(+)  " + configuration.algorithmString(alg_1) + ", (-)  "
		    + configuration.algorithmString(alg_2),
		    4,
		    8);

	  /*
	   *  Compute the intersection of two Büchi automata constructed for
	   *  the positive and the negative formula, respectively.
	   */

	  if (test_results[alg_1].automaton_stats[0].buchiAutomatonComputed()
	      && test_results[alg_2].automaton_stats[1].
	           buchiAutomatonComputed())
	  {
	    using ::Graph::BuchiAutomaton;
	    using ::Graph::BuchiProduct;
	    using ::Graph::Product;

	    /*
	     *  Scan the nontrivial maximal strongly connected components of
	     *  the intersection automaton to check whether the automaton has
	     *  any accepting executions. If an MSCC with a state from every
	     *  acceptance set is found, the intersection emptiness check
	     *  fails.
	     */

	    const BuchiAutomaton& a1
	      = *(test_results[alg_1].automaton_stats[0].buchi_automaton);
	    const BuchiAutomaton& a2
	      = *(test_results[alg_2].automaton_stats[1].buchi_automaton);

	    Product<BuchiProduct> product(a1, a2);

	    if (!product.localEmptinessCheck(a1.initialState(),
					     a2.initialState()))
	    {
	      test_results[alg_1].automaton_stats[0].
		buchi_intersection_check_stats[alg_2] = 1;
	      test_results[alg_2].automaton_stats[1].
		buchi_intersection_check_stats[alg_1] = 1;

	      printText(": ok\n", 4);
	    }
	    else
	    {
	      test_results[alg_1].automaton_stats[0].
		buchi_intersection_check_stats[alg_2] = 0;
	      test_results[alg_2].automaton_stats[1].
		buchi_intersection_check_stats[alg_1] = 0;

	      final_statistics[alg_1]
		.buchi_intersection_check_failures[alg_2]++;
	      if (alg_1 != alg_2)
		final_statistics[alg_2]
		  .buchi_intersection_check_failures[alg_1]++;

	      result = false;

	      printText(": failed\n", 4);
	    }

	    final_statistics[alg_1].
	      buchi_intersection_checks_performed[alg_2]++;
	    if (alg_1 != alg_2)
	      final_statistics[alg_2].
		buchi_intersection_checks_performed[alg_1]++;
	  }
	  else
	    printText(": not performed\n", 4);
	}
	else if (test_results[alg_1].automaton_stats[0].
		   buchi_intersection_check_stats[alg_2] == 0)
	  result = false;
      }
      catch (const UserBreakException&)
      {
	if (!printText(": aborted (user break)", 4))
	  printText(" [User break]", 2, 6);
	printText("\n\n", 2);

	if (round_info.transcript_file.is_open())
	{
	  writeToTranscript("User break during Büchi automata intersection "
			    "emptiness check");
	  round_info.transcript_file << string(8, ' ') + "(+) "
	                                + configuration.algorithmString(alg_1)
	                                + ", (-) "
	                                + configuration.algorithmString(alg_2)
	                                + "\n\n";
	}

	throw;
      }
      catch (const ::Graph::Product< ::Graph::BuchiProduct>::SizeException&)
      {
	if (!printText(": aborted (product may be too large)", 4))
	  printText(" [Product may be too large: (+)  "
		    + configuration.algorithmString(alg_1)
		    + ", (-)  "
		    + configuration.algorithmString(alg_2)
		    + "]",
		    2,
		    6);
	printText("\n", 2);

	if (round_info.transcript_file.is_open())
	{
	  writeToTranscript("Automata intersection emptiness check aborted "
			    "(product may be too large)");
	  round_info.transcript_file << string(8, ' ') + "(+) "
	                                + configuration.algorithmString(alg_1)
	                                + ", (-) "
	                                + configuration.algorithmString(alg_2)
	                                + "\n\n";
	}
      }
      catch (const bad_alloc&)
      {
	if (!printText(": aborted (out of memory)", 4))
	  printText(" [Out of memory: (+)  "
		    + configuration.algorithmString(alg_1)
		    + ", (-)  "
		    + configuration.algorithmString(alg_2)
		    + "]",
		    2,
		    6);
	printText("\n", 2);

	if (round_info.transcript_file.is_open())
	{
	  writeToTranscript("Out of memory during Büchi automata "
			    "intersection emptiness check");
	  round_info.transcript_file << string(8, ' ') + "(+) "
	                                + configuration.algorithmString(alg_1)
	                                + ", (-) "
	                                + configuration.algorithmString(alg_2)
	                                + "\n\n";
	}
      }
    }
  }

  IntervalList algorithms;
  algorithms.merge(0, round_info.number_of_translators - 1);

  if (!result)
  {
    round_info.error = true;

    if (round_info.transcript_file.is_open())
    {
      writeToTranscript("Büchi automata intersection emptiness check failed");
      printBuchiIntersectionCheckStats
	(round_info.transcript_file, 8, algorithms);
      round_info.transcript_file << '\n';
    }

    if (configuration.global_options.verbosity == 2)
      round_info.cout << " [failed]";
  }

  if (configuration.global_options.verbosity == 2)
  {
    round_info.cout << '\n';
    round_info.cout.flush();
  }
  else if (configuration.global_options.verbosity >= 3)
  {
    printBuchiIntersectionCheckStats(cout, 6, algorithms);
    round_info.cout << '\n';
    round_info.cout.flush();
  }
}

}



/******************************************************************************
 *
 * Definitions for static members for specializations of the Product template.
 *
 *****************************************************************************/

namespace Graph
{

template <>
Product<BuchiProduct>* Product<BuchiProduct>::product = 0;

template <>
Product<StateSpaceProduct>* Product<StateSpaceProduct>::product = 0;

}
