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
#include <fstream>
#include <iostream>
#include "Exception.h"
#include "LbtWrapper.h"
#include "LtlFormula.h"
#include "SpinWrapper.h"
#include "SpotWrapper.h"
#ifdef HAVE_GETOPT_LONG
#include <getopt.h>
#define OPTIONSTRUCT struct option
#else
#include "gnu-getopt.h"
#define opterr gnu_opterr
#define OPTIONSTRUCT struct gnu_option
#define getopt_long gnu_getopt_long
#endif  /* HAVE_GETOPT_LONG */

/******************************************************************************
 *
 * Pointer to the command line arguments of the program.
 *
 *****************************************************************************/

char** command_line_arguments;



/******************************************************************************
 *
 * Pointer to an object providing operations for translating a formula into an
 * automaton.
 *
 *****************************************************************************/

static TranslatorInterface* translator = 0;



/******************************************************************************
 *
 * A function for showing warnings to the user.
 *
 *****************************************************************************/

void printWarning(const string& msg)
{
  cerr << string(command_line_arguments[0]) + ": warning: " + msg << endl;
}



/******************************************************************************
 *
 * Handler for SIGINT, SIGQUIT, SIGABRT and SIGTERM.
 *
 *****************************************************************************/

static void signalHandler(int signal_number)
{
  if (translator != 0)
    delete translator;
  struct sigaction s;
  s.sa_handler = SIG_DFL;
  sigemptyset(&s.sa_mask);
  s.sa_flags = 0;
  sigaction(signal_number, &s, static_cast<struct sigaction*>(0));
  raise(signal_number);
}



/******************************************************************************
 *
 * Function for installing signal handlers.
 *
 *****************************************************************************/

static void installSignalHandler(int signum)
{
  struct sigaction s;
  sigaction(signum, static_cast<struct sigaction*>(0), &s);

  if (s.sa_handler != SIG_IGN)
  {
    s.sa_handler = signalHandler;
    sigemptyset(&s.sa_mask);
    s.sa_flags = 0;
    sigaction(signum, &s, static_cast<struct sigaction*>(0));
  }
}



/******************************************************************************
 *
 * Main function.
 *
 *****************************************************************************/

int main(int argc, char** argv)
{
  typedef enum {OPT_HELP = 'h', OPT_LBT, OPT_SPIN, OPT_SPOT, OPT_VERSION = 'V'}
    OptionType;

  static OPTIONSTRUCT command_line_options[] =
  {
    {"help",    no_argument, 0, OPT_HELP},
    {"lbt",     no_argument, 0, OPT_LBT},
    {"spin",    no_argument, 0, OPT_SPIN},
    {"spot",    no_argument, 0, OPT_SPOT},
    {"version", no_argument, 0, OPT_VERSION},
    {0,         0,           0, 0}
  };

  command_line_arguments = argv;

  opterr = 1;
  int opttype, option_index;

  do
  {
    option_index = 0;
    opttype = getopt_long(argc, argv, "hV", command_line_options,
			  &option_index);

    switch (opttype)
    {
      case OPT_HELP :
	cout << string("Usage: ") << command_line_arguments[0]
	     << " [translator] [command line for translator] [formula "
	        "file] [automaton file]\n"
	        "General options:\n"
                "  -h, --help               Show this help\n"
                "  -V, --version            Show version and exit\n\n"
                "Translator options:\n"
                "       --lbt               lbt\n"
                "       --spin              Spin\n"
                "       --spot              Spot\n"
                "The command line for these translators must be given as a "
	        "single argument\n"
                "including the name (and location) of an external program to "
                "execute, together\n"
                "with any optional parameters to be passed to the "
	        "program.\n\n";
	exit(0);
	break;

      case OPT_LBT :
	translator = new LbtWrapper();
	break;

      case OPT_SPIN :
	translator = new SpinWrapper();
	break;

      case OPT_SPOT :
	translator = new SpotWrapper();
	break;

      case OPT_VERSION :
	cout << "lbtt-translate " PACKAGE_VERSION "\n"
	        "lbtt-translate is free software; you may change and "
	        "redistribute it under the\n"
	        "terms of the GNU General Public License. lbtt-translate "
	        "comes with NO WARRANTY.\n"
	        "See the file COPYING for details.\n";
	exit(0);
	break;

      case '?' :
      case ':' :
	exit(-1);
    }
  }
  while (opttype != -1);

  if (argc < 5)
  {
    cerr << argv[0] << ": too few command line arguments" << endl;
    exit(-1);
  }

  if (argc > 5)
  {
    cerr << argv[0] << ": too many command line arguments" << endl;
    exit(-1);
  }

  int exitstatus = 0;

  installSignalHandler(SIGHUP);
  installSignalHandler(SIGINT);
  installSignalHandler(SIGQUIT);
  installSignalHandler(SIGABRT);
  installSignalHandler(SIGPIPE);
  installSignalHandler(SIGALRM);
  installSignalHandler(SIGTERM);
  installSignalHandler(SIGUSR1);
  installSignalHandler(SIGUSR2);

  ::Ltl::LtlFormula* formula(0);

  try
  {
    ifstream input_file;
    input_file.open(command_line_arguments[argc - 2], ios::in);
    if (!input_file.good())
      throw FileOpenException(command_line_arguments[argc - 2]);

    formula = ::Ltl::LtlFormula::read(input_file);

    translator->translate(*formula, command_line_arguments[argc - 1]);

    ::Ltl::LtlFormula::destruct(formula);
    delete translator;
  }
  catch (...)
  {
    if (formula != 0)
      ::Ltl::LtlFormula::destruct(formula);

    cerr << string(command_line_arguments[0]) + ": ";
    exitstatus = -1;

    if (translator != 0)
      delete translator;

    try
    {
      throw;
    }
    catch (const Exception& e)
    {
      cerr << e.what();
    }
    catch (...)
    {
      cerr << "fatal error, aborting";
    }

    cerr << endl;
  }

  return exitstatus;
}
