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
#include <cstdio>
#include <iostream>
#include <fstream>
#ifdef HAVE_SSTREAM
#include <sstream>
#else
#include <strstream>
#endif /* HAVE_SSTREAM */
#include "DispUtil.h"
#include "SharedTestData.h"
#include "StatDisplay.h"
#include "StringUtil.h"
#include "TestRoundInfo.h"
#include "TestStatistics.h"
#include "TestOperations.h"
#include "UserCommandReader.h"
#include "UserCommands.h"

#ifdef HAVE_READLINE
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <readline/readline.h>
#include <readline/history.h>
#endif /* HAVE_READLINE */

#ifdef HAVE_ISATTY
#ifdef HAVE_UNISTD_H
#include <unistd.h>
#endif /* HAVE_UNISTD_H */
#endif /* HAVE_ISATTY */



/******************************************************************************
 *
 * Functions for reading and parsing user commands.
 *
 *****************************************************************************/

namespace UserCommandInterface
{

using namespace ::SharedTestData;
using namespace ::StatDisplay;
using namespace ::StringUtil;
using namespace ::UserCommands;

/* ========================================================================= */
void executeUserCommands()
/* ----------------------------------------------------------------------------
 *
 * Description:   Loop for reading user commands and executing them after a
 *                test round.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing. However, changes `round_info.current_round',
 *                `round_info.next_round_to_stop',
 *                `round_info.next_round_to_run' and `round_info.abort' as a
 *                side effect, depending on the user's wish to abort testing,
 *                skip some number of test rounds or continue testing for a
 *                number of rounds.
 *
 * ------------------------------------------------------------------------ */
{
  string input_line;
  vector<string> input_tokens;
  TokenType token;

  bool formula_type = true;

  pair<string, bool> redirection_info;
  string external_command;
  ofstream* output_file = 0;
#ifdef HAVE_SSTREAM
  ostringstream* output_string = 0;
#else
  ostrstream* output_string = 0;
#endif  /* HAVE_SSTREAM */
  ostream* output_stream = 0;

  const string prompt = "  ** [Round " + toString(round_info.current_round)
                        + " of " + toString(configuration.global_options.
					    number_of_rounds)
                        + "] >> ";
  int indent;

#ifdef HAVE_READLINE
  char* prompt_c_str = new char[prompt.length() + 1];
  strcpy(prompt_c_str, prompt.c_str());

  char* line;

  try
  {
#endif  /* HAVE_READLINE */

  signal(SIGPIPE, SIG_IGN);

  while (1)
  {
    try
    {
      input_line = "";
#ifdef HAVE_READLINE
      line = readline(prompt_c_str);
      if (line != static_cast<char*>(0))
	input_line = line;
      else
      {
#else
      round_info.cout << prompt;
      round_info.cout.flush();
      getline(cin, input_line, '\n');
      if (cin.eof())
      {
	cin.clear();
#endif /* HAVE_READLINE */
#ifdef HAVE_ISATTY
	/*
	 *  If standard input is not bound to a terminal, act on EOF as if the
	 *  `continue' command had been issued. Otherwise act as if an empty
	 *  line was given as input.
	 */
	if (!isatty(STDIN_FILENO))
	  input_line = "continue";
	else
	{
	  round_info.cout << '\n';
	  round_info.cout.flush();
	}
#else
	input_line = "continue";
	round_info.cout << input_line << '\n';
	round_info.cout.flush();
#endif /* HAVE_ISATTY */
      }

#ifdef HAVE_ISATTY
      if (!isatty(STDIN_FILENO))
      {
	round_info.cout << input_line << '\n';
	round_info.cout.flush();
      }
#endif /* HAVE_ISATTY */

#ifdef HAVE_READLINE
      if (line != static_cast<char*>(0)    /* line may be 0 on EOF */
	  && input_line.find_first_not_of(" \t") != string::npos)
      {
	add_history(line);
	free(line);
      }
#endif  /* HAVE_READLINE */

      /*
       *  If the input line contains an unescaped pipe symbol ('|') outside
       *  quotes, extract an external command from the line.
       */

      external_command = "";
      string::size_type pos
	= findInQuotedString(input_line, "|", OUTSIDE_QUOTES);

      if (pos != string::npos)
      {
	string::size_type nonspace_pos
	  = input_line.find_first_not_of(" \t", pos + 1);
	if (nonspace_pos != string::npos)
	{
	  external_command = input_line.substr(nonspace_pos);
	  input_line = input_line.substr(0, pos);
	}
      }

      sliceString(substituteInQuotedString(input_line, " \t", "\n\n",
					   OUTSIDE_QUOTES),
		  "\n",
		  input_tokens);

      user_break = false;

      if (!input_tokens.empty() || !external_command.empty())
      {
	round_info.cout << '\n';
	round_info.cout.flush();
      }

      if (!input_tokens.empty())
      {
        token = parseCommand(input_tokens[0]);

        if (token == CONTINUE || token == SKIP)
        {
          verifyArgumentCount(input_tokens, 0, 1);

          unsigned long int rounds_to_continue;

          rounds_to_continue = (input_tokens.size() > 1
                                ? parseNumber(input_tokens[1])
                                : 1);

          if (rounds_to_continue == 0)
            throw CommandErrorException("Argument of the command must be "
                                        "positive.");

          if (token == CONTINUE)
	  {
	    bool all_algorithms_disabled = true;

	    for (vector<Configuration::AlgorithmInformation>::const_iterator
		   algorithm = configuration.algorithms.begin();
		 algorithm != configuration.algorithms.end();
		 ++algorithm)
	    {
	      if (algorithm->enabled)
	      {
		all_algorithms_disabled = false;
		break;
	      }
	    }

	    /*
	     *  Show a warning if the `continue' command would result in no
	     *  further tests in the case that none of the implementations is
	     *  enabled for testing.
	     */

	    if (configuration.global_options.interactive
		  != Configuration::ALWAYS
		&& all_algorithms_disabled)
	    {
	      round_info.cout << "  Warning! All algorithms are currently "
		                 "disabled.\n  Are you sure you wish to "
		                 "continue? [y/n] ";
	      round_info.cout.flush();

	      input_line = "";
	      getline(cin, input_line, '\n');
	      sliceString(input_line, " \t", input_tokens);

	      if (!input_tokens.empty()
		  && (input_tokens[0][0] == 'y' || input_tokens[0][0] == 'Y'))
		round_info.next_round_to_stop
		  = configuration.global_options.number_of_rounds + 1;
	      else
	      {
		round_info.cout << '\n';
		round_info.cout.flush();
		continue;
	      }
	    }

	    if (round_info.next_round_to_run == round_info.current_round)
	      round_info.next_round_to_run++;

	    if (configuration.global_options.interactive
		  == Configuration::ALWAYS
		|| input_tokens.size() > 1)
		round_info.next_round_to_stop
		  = round_info.current_round + rounds_to_continue;
	  }
          else  /* token == SKIP */
	  {
	    round_info.next_round_to_stop
	      = round_info.current_round + rounds_to_continue;

            round_info.next_round_to_run = round_info.next_round_to_stop + 1;
	  }

          break;
        }
	else if (token == ENABLE || token == DISABLE)
	{
	  verifyArgumentCount(input_tokens, 0, 1);
	  changeAlgorithmState(input_tokens, token == ENABLE);
	  continue;
	}
        else if (token == QUIT)
        {
          verifyArgumentCount(input_tokens, 0, 0);
          round_info.abort = true;
          break;
        }
        else if (token == VERBOSITY)
        {
          verifyArgumentCount(input_tokens, 0, 1);
          changeVerbosity(input_tokens);
          continue;
        }

	if (round_info.skip
	    && (token == BUCHI || token == BUCHIANALYZE
		|| token == CONSISTENCYANALYSIS || token == EVALUATE
		|| token == FORMULA || token == INCONSISTENCIES
		|| token == RESULTANALYZE || token == STATESPACE))
	  throw CommandErrorException("This command is not available because "
				      "the current test round was skipped.");

        /*
         *  If the command expects a formula identifier as a parameter,
         *  determine the type of the formula to which the command refers.
         */

        if (token == BUCHI || token == EVALUATE
            || token == FORMULA || token == RESULTANALYZE)
          formula_type = parseFormulaType(input_tokens);

	if (!external_command.empty())
	{
	  /*
	   *  If the command output should be piped to an external program,
	   *  prepare to collect the output into a string. In this case no
	   *  output redirection (> or >>) is allowed.
	   */

#ifdef HAVE_SSTREAM
	  output_string = new ostringstream();
#else
	  output_string = new ostrstream();
#endif  /* HAVE_SSTREAM */
	  output_stream = output_string;
	  indent = 0;
	}
	else
	{
	  /*
	   *  Determine whether the output of the command should be saved or
	   *  appended to a file, instead of displaying it on the console. If
	   *  output redirection is required, open a file for output.
	   */

	  redirection_info = parseRedirection(input_tokens);

	  if (redirection_info.first.empty())
          {
	    output_stream = &cout;
	    indent = 2;
	  }
	  else
          {
	    output_file = new ofstream();

	    try
	    {
	      TestOperations::openFile(redirection_info.first.c_str(),
				       *output_file,
				       ios::out | (redirection_info.second
						   ? ios::app
						   : ios::trunc),
				       2);
	    }
	    catch (const IOException& e)
	    {
	      delete output_file;
	      output_file = 0;
	      throw CommandErrorException(e.what());
	    }

	    output_stream = output_file;
	    indent = 0;
	  }
	}

        switch (token)
        {
          case ALGORITHMS :
            verifyArgumentCount(input_tokens, 0, 0);
            printAlgorithmList(*output_stream, indent);
            if (output_file != 0)
              round_info.cout << "  List of algorithms";
            break;

          case BUCHI :
	    {
	      bool use_dot = (input_tokens.size() == 3
			      && input_tokens[2] == "dot");

	      verifyArgumentCount(input_tokens, 1, 2);
	      printBuchiAutomaton(*output_stream, indent,
				  formula_type,
				  input_tokens,
				  (use_dot ? Graph::DOT : Graph::NORMAL));
	      if (output_file != 0)
              {
		round_info.cout << "  Büchi automaton information";
		if (use_dot)
		  round_info.cout << " (in dot format)";
	      }
	    }
	    break;

          case BUCHIANALYZE :
            verifyArgumentCount(input_tokens, 2, 2);
            printAutomatonAnalysisResults(*output_stream, indent,
					  input_tokens);
            if (output_file != 0)
              round_info.cout << "  Büchi automaton intersection emptiness "
		                 "check analysis";
            break;

	  case CONSISTENCYANALYSIS :
	    verifyArgumentCount(input_tokens, 1, 2);
	    printConsistencyAnalysisResults(*output_stream, indent,
					    input_tokens);
	    if (output_file != 0)
	      round_info.cout << "  Consistency check result analysis";
	    break;

          case EVALUATE :
            verifyArgumentCount(input_tokens, 0, 2);
            evaluateFormula(*output_stream, indent, formula_type,
                            input_tokens);
            if (output_file != 0)
              round_info.cout << "  Formula acceptance information";
            break;

          case FORMULA :
            verifyArgumentCount(input_tokens, 0, 1);
            printFormula(*output_stream, indent, formula_type, input_tokens);
            if (output_file != 0)
              round_info.cout << string("  ") + (formula_type
						 ? "Formula"
						 : "Negated formula");
            break;

          case HELP :
            verifyArgumentCount(input_tokens, 0, 1);
            printCommandHelp(*output_stream, indent, input_tokens);
            if (output_file != 0)
              round_info.cout << "  Command help";
            break;

          case INCONSISTENCIES :
            verifyArgumentCount(input_tokens, 0, 1);
            printInconsistencies(*output_stream, indent, input_tokens);
            if (output_file != 0)
              round_info.cout << "  Model checking result consistency check "
		                 "results for round "
                                 + toString(round_info.current_round)
 		                 + "\n  ";
            break;

          case RESULTANALYZE :
            verifyArgumentCount(input_tokens, 2, 3);
            printCrossComparisonAnalysisResults
	      (*output_stream, indent, formula_type, input_tokens);
            if (output_file != 0)
              round_info.cout << "  Model checking result cross-comparison "
                                 "analysis";
            break;

          case RESULTS :
            verifyArgumentCount(input_tokens, 0, 1);
            printTestResults(*output_stream, indent, input_tokens);
            if (output_file != 0)
              round_info.cout << "  Test results for round "
			         + toString(round_info.current_round);
            break;

          case STATESPACE :
	    {
	      bool use_dot = (input_tokens.size() == 2
			      && input_tokens[1] == "dot");

	      verifyArgumentCount(input_tokens, 0, 1);
	      printStateSpace(*output_stream, indent, input_tokens,
			      (use_dot ? Graph::DOT : Graph::NORMAL));

	      if (output_file != 0)
              {
		round_info.cout << "  State space information";
		if (use_dot)
		  round_info.cout << " (in dot format)";
	      }
	    }
	    break;

          case STATISTICS :
            verifyArgumentCount(input_tokens, 0, 0);
            printCollectiveStats(*output_stream, indent);

            if (output_file != 0)
              round_info.cout << "  Test statistics after round "
			         + toString(round_info.current_round);
            break;

          default :
            throw CommandErrorException("Unknown command (`" + input_tokens[0]
					+ "').");
        }

	if (output_string != 0)
	{
	  *output_stream << ends;
	  string outstring(output_string->str());

	  FILE* output_pipe = popen(external_command.c_str(), "w");
	  if (output_pipe == NULL)
	    throw ExecFailedException(external_command);
	  fputs(outstring.c_str(), output_pipe);
	  pclose(output_pipe);
	}
	else
	{
	  if (output_file != 0)
	    round_info.cout << string(redirection_info.second
				      ? " appended"
				      : " written")
	                       + " to `" + redirection_info.first + "'.\n";
	  round_info.cout << '\n';
	  round_info.cout.flush();
	}
      }
      else if (!external_command.empty())
      {
	(void) system(external_command.c_str());
	round_info.cout << '\n';
	round_info.cout.flush();
      }
    }
    catch (const Exception& e)
    {
      ::DispUtil::printTextBlock(cout, 2, string("Error: ") + e.what() + '\n',
				 78);
    }

    if (output_string != 0)
    {
#ifndef HAVE_SSTREAM
      output_string->freeze(0);
#endif  /* HAVE_SSTREAM */
      delete output_string;
      output_string = 0;
    }
    else if (output_file != 0)
    {
      output_file->close();
      delete output_file;
      output_file = 0;
    }
  }

#ifdef HAVE_READLINE
  }
  catch (...)
  {
    delete[] prompt_c_str;
    throw;
  }

  delete[] prompt_c_str;
#endif  /* HAVE_READLINE */

  signal(SIGPIPE, SIG_DFL);
}

/* ========================================================================= */
TokenType parseCommand(const string& token)
/* ----------------------------------------------------------------------------
 *
 * Description:   Parses a user command by translating a command name into
 *                its corresponding TokenType identifier.
 *
 * Argument:      token  --  A reference to a string containing the command.
 *
 * Returns:       A command identifier of the enumerated type TokenType.
 *
 * ------------------------------------------------------------------------- */
{
  TokenType token_type = UNKNOWN;
  string::size_type len = token.length();
  bool ambiguous = false;

  if (token.empty())
    return token_type;

  switch (token[0])
  {
    case 'a' :
      if (token.compare(1, len - 1, "lgorithms", len - 1) == 0)
	token_type = ALGORITHMS;
      break;

    case 'b' :
      if (len < 2)
	ambiguous = true;
      else if (token[1] == 'u')
      {
	if (len < 3)
	  ambiguous = true;
	else if (token[2] == 'c')
	{
	  if (len < 4)
	    ambiguous = true;
	  else if (token[3] == 'h')
	  {
	    if (len < 5)
	      ambiguous = true;
	    else if (token[4] == 'i')
	    {
	      if (len < 6)
		token_type = BUCHI;
	      else if (token[5] == 'a'
		       && token.compare(6, len - 6, "nalysis", len - 6) == 0)
		token_type = BUCHIANALYZE;
	    }
	  }
	}
      }
      break;

    case 'c' :
      if (len < 2)
	ambiguous = true;
      else if (token[1] == 'o')
      {
	if (len < 3)
	  ambiguous = true;
	else if (token[2] == 'n')
	{
	  if (len < 4)
	    ambiguous = true;
	  else if (token[3] == 's'
		   && token.compare(4, len - 4, "istencyanalysis", len - 4)
		        == 0)
	    token_type = CONSISTENCYANALYSIS;
	  else if (token[3] == 't'
		   && token.compare(4, len - 4, "inue", len - 4) == 0)
	    token_type = CONTINUE;
	}
      }
      break;

    case 'd' :
      if (token.compare(1, len - 1, "isable", len - 1) == 0)
	token_type = DISABLE;
      break;

    case 'e' :
      if (len < 2)
	ambiguous = true;
      else if (token[1] == 'n')
      {
	if (token.compare(2, len - 2, "able", len - 2) == 0)
	  token_type = ENABLE;
      }
      else if (token[1] == 'v')
      {
	if (token.compare(2, len - 2, "aluate", len - 2) == 0)
	  token_type = EVALUATE;
      }

      break;

    case 'f' :
      if (token.compare(1, len - 1, "ormula", len - 1) == 0)
        token_type = FORMULA;
      break;

    case 'h' :
      if (token.compare(1, len - 1, "elp", len - 1) == 0)
        token_type = HELP;
      break;

    case 'i' :
      if (len < 2)
	ambiguous = true;
      else if (token[1] == 'm')
      {
	if (token.compare(2, len - 2, "plementations", len - 2) == 0)
	  token_type = ALGORITHMS;
      }
      else if (token[1] == 'n')
      {
	if (token.compare(2, len - 2, "consistencies", len - 2) == 0)
	  token_type = INCONSISTENCIES;
      }

      break;

    case 'q' :
      if (token.compare(1, len - 1, "uit", len - 1) == 0)
        token_type = QUIT;
      break;

    case 'r' :
      if (len < 2)
	ambiguous = true;
      else if (token[1] == 'e')
      {
	if (len < 3)
	  ambiguous = true;
	else if (token[2] == 's')
	{
	  if (len < 4)
	    ambiguous = true;
	  else if (token[3] == 'u')
	  {
	    if (len < 5)
	      ambiguous = true;
	    else if (token[4] == 'l')
	    {
	      if (len < 6)
		ambiguous = true;
	      else if (token[5] == 't')
	      {
		if (len < 7)
		  ambiguous = true;
		else if (token[6] == 's' && len == 7)
		  token_type = RESULTS;
		else if (token[6] == 'a')
		{
		  if (token.compare(7, len - 7, "nalysis", len - 7) == 0)
		    token_type = RESULTANALYZE;
		}
	      }
	    }
	  }
	}
      }
      break;

    case 's' :
      if (len < 2)
	ambiguous = true;
      else if (token[1] == 'k')
      {
	if (token.compare(2, len - 2, "ip", len - 2) == 0)
          token_type = SKIP;
      }
      else if (token[1] == 't')
      {
	if (len < 3)
	  ambiguous = true;
	else if (token[2] == 'a')
	{
	  if (len < 4)
	    ambiguous = true;
	  else if (token[3] == 't')
	  {
	    if (len < 5)
	      ambiguous = true;
	    else if (token[4] == 'e')
	    {
	      if (token.compare(5, len - 5, "space", len - 5) == 0)
		token_type = STATESPACE;
	    }
	    else if (token[4] == 'i')
	    {
	      if (token.compare(5, len - 5, "stics", len - 5) == 0)
		token_type = STATISTICS;
	    }
	  }
	}
      }
      break;

    case 't' :
      if (token.compare(1, len - 1, "ranslators", len - 1) == 0)
	token_type = ALGORITHMS;
      break;

    case 'v' :
      if (token.compare(1, len - 1, "erbosity", len - 1) == 0)
        token_type = VERBOSITY;
      break;
  }

  if (ambiguous)
    throw CommandErrorException("Ambiguous command.");

  return token_type;
}

/* ========================================================================= */
void verifyArgumentCount
  (const vector<string>& command, vector<string>::size_type min_arg_count,
   vector<string>::size_type max_arg_count)
/* ----------------------------------------------------------------------------
 *
 * Description:   Verifies that the number of arguments given for a user
 *                command is between a given interval.
 *
 * Arguments:     command        --  A reference to a constant vector of
 *                                   strings (the user command and its
 *                                   arguments).
 *                min_arg_count  --  Smallest allowed number of arguments.
 *                max_arg_count  --  Largest allowed number of arguments.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (command.size() < min_arg_count + 1)
    throw CommandErrorException("Too few arguments for command.");
  else if (command.size() > max_arg_count + 1)
    throw CommandErrorException("Too many arguments for command.");
}

/* ========================================================================= */
pair<string, bool> parseRedirection(vector<string>& input_tokens)
/* ----------------------------------------------------------------------------
 *
 * Description:   Tests whether the last argument to a user command specifies
 *                output redirection. If redirection is requested, the
 *                "argument" specifying the redirection is removed from the
 *                vector of strings forming the command.
 *
 * Argument:      input_tokens  --  A reference to a vector of strings giving
 *                                  the user command and its arguments.
 *
 * Returns:       A pair whose first component is the name of the output file
 *                (or the empty string if no redirection was specified) and
 *                whose second component determines whether the output should
 *                be appended to the file instead of creating a new file.
 *
 * ------------------------------------------------------------------------- */
{
  string filename;
  bool append = false;

  if (!input_tokens.empty())
  {
    string& token = input_tokens.back();

    if (token[0] == '>')
    {
      if (token.length() > 1)
      {
        if (token[1] == '>')
	{
	  if (token.length() > 2)
          {
	    append = true;
	    filename = unquoteString(token.substr(2));
	    input_tokens.pop_back();
	  }
	}
        else
	{
          filename = unquoteString(token.substr(1));
	  input_tokens.pop_back();
	}
      }
    }
    else if (input_tokens.size() >= 2)
    {
      string& token = *(input_tokens.rbegin() + 1);

      if (token == ">" || token == ">>")
      {
        filename = unquoteString(input_tokens.back());
        append = (token.length() == 2);
        input_tokens.pop_back();
        input_tokens.pop_back();
      }
    }
  }

  return make_pair(filename, append);
}

/* ========================================================================= */
bool parseFormulaType(vector<string>& input_tokens)
/* ----------------------------------------------------------------------------
 *
 * Description:   Tests whether the first argument of a command specifies a
 *                formula (i.e., whether the first argument of the command is
 *                either a `+' or a `-'). If it is, the argument is removed
 *                from the vector of strings forming the command.
 *
 * Argument:      input_tokens  --  A reference to a vector of strings giving
 *                                  the user command.
 *
 * Returns:       A truth value according to whether a formula or its negation
 *                was specified; the effect of specifying no formula type is
 *                the same as giving a `+' as an argument (i.e. the formula
 *                type defaults to the positive formula).
 *
 * ------------------------------------------------------------------------- */
{
  bool formula_type = true;

  if (input_tokens.size() >= 2)
  {
    formula_type = (input_tokens[1] != "-");

    if (input_tokens[1] == "+" || input_tokens[1] == "-")
      input_tokens.erase(input_tokens.begin());
  }

  return formula_type;
}

/* ========================================================================= */
void verifyNumber
  (unsigned long int number, unsigned long int max, const char* error_message)
/* ----------------------------------------------------------------------------
 *
 * Description:   Checks that a given unsigned long integer is less than a
 *                given maximum value. Throws an exception with an error
 *                message if this is not the case.
 *
 * Argument:      number         --  Number to be tested.
 *                max            --  Value the number is to be tested against.
 *                error_message  --  Error message.
 *
 * Returns:       Nothing. Throws an exception if the check fails.
 *
 * ------------------------------------------------------------------------- */
{
  if (number >= max)
    throw CommandErrorException(string(error_message) + " ("
				+ toString(number) + ").");
}

}
