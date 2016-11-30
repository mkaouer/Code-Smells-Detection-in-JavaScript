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

#ifndef CONFIGURATION_H
#define CONFIGURATION_H

#ifdef __GNUC__
#pragma interface
#endif /* __GNUC__ */

#include <config.h>
#include <iostream>
#include <map>
#include <set>
#include <string>
#include <utility>
#include <vector>
#include "Alloc.h"
#include "Exception.h"
#include "FormulaRandomizer.h"
#include "StateSpaceRandomizer.h"
#include "StringUtil.h"

using namespace std;



/******************************************************************************
 *
 * A class for storing program configuration information.
 *
 *****************************************************************************/

class Configuration
{
public:
  Configuration();                                  /* Constructor. */

  ~Configuration();                                 /* Destructor. */

  void read(int argc, char* argv[]);                /* Reads the program
						     * configuration.
						     */

  void print                                        /* Writes the current    */
    (ostream& stream = cout, int indent = 0) const; /* configuration (in a
						     * textual form) to a
						     * stream.
						     */

  struct AlgorithmInformation;                      /* See below. */

  string algorithmString                            /* Formats the the id  */
    (vector<AlgorithmInformation,                   /* of an algorithm and */
	    ALLOC(AlgorithmInformation) >::size_type/* the name of the     */
     algorithm_id) const;                           /* algorithm into a
						     * string.
						     */

  static void showCommandLineHelp                   /* Prints the list of    */
    (const char* program_name);                     /* command line options. */

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  enum InteractionMode {NEVER, ALWAYS, ONERROR};    /* Enumeration constants
						     * affecting the behaviour
						     * of the program as
						     * regards user control.
						     */

  enum FormulaMode {NORMAL, NNF};                   /* Enumeration constants
						     * affecting the generation
						     * and output of random
						     * formulae.
						     */

  enum StateSpaceMode {RANDOMGRAPH = 1,             /* Enumeration constants */
		       RANDOMCONNECTEDGRAPH = 2,    /* affecting the         */
		       GRAPH = 3,                   /* generation of random  */
		       RANDOMPATH = 4,              /* state spaces.         */
		       ENUMERATEDPATH = 8,
		       PATH = 12};

  enum ProductMode {LOCAL, GLOBAL};                 /* Enumeration constants
						     * for controlling the
						     * scope of synchronous
						     * products.
						     */

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  struct AlgorithmInformation                       /* A structure for storing
						     * information about a
						     * particular algorithm
						     * (name, path to
						     * executable, command-line
						     * parameters).
						     */
  {
    string* name;                                   /* Name of the algorithm.
						     */

    string* path_to_program;                        /* Path to the executable
						     * required for running
						     * the algorithm.
						     */

    string* extra_parameters;                       /* Additional command-line
						     * parameters required for
						     * running the executable.
						     */

    bool enabled;                                   /* Determines whether the
						     * algorithm is enabled
						     * (whether it will be used
						     * in the tests or not).
						     */
  };

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  struct GlobalConfiguration                        /* A structure for storing
						     * all the information
						     * affecting the general
						     * behaviour of the
						     * program.
						     */
  {
    int verbosity;                                  /* Determines the verbosity
						     * of program output (0-5,
						     * the bigger the value,
						     * the more information
						     * will be shown).
						     */

    InteractionMode interactive;                    /* Controls the behaviour
						     * of the program as
						     * regards the ability of
						     * the user to enter
						     * commands between test
						     * rounds. Possible values
						     * and their meanings are:
						     *
						     * NEVER:
						     *   Run all tests without
						     *   interruption.
						     * ALWAYS:
						     *   Pause after each test
						     *   round to wait for
						     *   user commands.
						     * ONERROR:
						     *   Try to run the tests
						     *   without interruption.
						     *   However, in case of
						     *   an error, pause and
						     *   wait for user
						     *   commands.
						     */

    unsigned long int number_of_rounds;             /* Number of test rounds.
						     */

    unsigned long int init_skip;                    /* Number of rounds to skip
						     * before starting testing.
						     */

    unsigned long int statespace_change_interval;   /* Determines the frequency
						     * (in rounds) of how often
						     * a new state space is
						     * generated.
						     */

    StateSpaceMode statespace_generation_mode;      /* Random state space
						     * generation mode.
						     * Available options are:
						     *
						     * RANDOMGRAPH:
						     *   Generate random
						     *   connected graphs as
						     *   state spaces.
						     * RANDOMPATH:
						     *   Generate paths as
						     *   state spaces, choose
						     *   the loop and the
						     *   truth assignments for
						     *   atomic propositions
						     *   randomly.
						     * ENUMERATEDPATH:
						     *   Generate paths as
						     *   state spaces by
						     *   enumerating all
						     *   possible paths of a
						     *   given length.
						     */

    unsigned long int formula_change_interval;      /* Determines the frequency
						     * (in rounds) of how often
						     * a new formula is
						     * generated.
						     */

    ProductMode product_mode;                       /* Determines the scope of
						     * the synchronous products
						     * computed by the program.
						     * Possible values and
						     * their meanings are:
						     *
						     * LOCAL:
						     *   The synchronous
						     *   products are computed
						     *   only with respect to
						     *   the initial state of
						     *   the system. This will
						     *   save memory but makes
						     *   the algorithm cross-
						     *   comparisons less
						     *   powerful, possibly
						     *   at the cost of
						     *   chances for finding
						     *   inconsistencies in the
						     *   results.
						     * GLOBAL:
						     *   The synchronous
						     *   products are computed
						     *   with respect to each
						     *   system state (i.e.
						     *   the formula is model
						     *   checked in each system
						     *   state separately).
						     *   This will usually
						     *   require more memory
						     *   than the other
						     *   alternative.
						     */

    string cfg_filename;                            /* Name for the
						     * configuration file.
						     */

    string transcript_filename;                     /* Name for the error log
						     * file.
						     */

    string formula_input_filename;                  /* Name for the file from
						     * which to read LTL
						     * formulae.
						     */

    bool do_comp_test;                              /* Is the model checking
						     * result cross-comparison
						     * test enabled?
						     */

    bool do_cons_test;                              /* Is the model checking
						     * result consistency check
						     * enabled?
						     */

    bool do_intr_test;                              /* Is the automata
						     * intersection emptiness
						     * check enabled?
						     */

    unsigned int statespace_random_seed;            /* Random seeds for the */
    unsigned int formula_random_seed;               /* state space and
						     * formula generation
						     * algorithms.
						     */
  };

  struct FormulaConfiguration                       /* A structure for storing
						     * specific information
						     * affecting the generation
						     * of random formulae.
						     */
  {
    int default_operator_priority;                  /* Default priority for all
						     * LTL formula symbols.
						     */

    map<int, int, less<int>, ALLOC(int) >           /* Priorities for LTL */
      symbol_priority;                              /* formula symbols.   */

    map<int, double, less<int>, ALLOC(double) >     /* Expected numbers of */
      symbol_distribution;                          /* occurrence for the
						     * different formula
						     * operators.
						     */

    bool allow_abbreviated_operators;               /* Determines whether the
						     * operators ->, <->, xor,
						     * <>, [], W and M should
						     * be allowed when
						     * generating random
						     * formulae (these are
						     * `abbreviated' operators
						     * since they could be
						     * written in an equivalent
						     * form by using another
						     * operators).
						     */

    Configuration::FormulaMode output_mode;         /* Determines whether the
						     * generated formulae are
						     * to be converted to
						     * negation normal form
						     * before passing them to
						     * the different
						     * algorithms. Possible
						     * values are:
						     *
						     * NORMAL:
						     *   No conversion.
						     * NNF:
						     *   Do the conversion
						     *   (this may affect the
						     *   size of the formulae!)
						     */

    Configuration::FormulaMode generate_mode;       /* Determines whether the
						     * formulae are to be
						     * generated in negation
						     * normal form (strict
						     * size requirement for
						     * formulae). Possible
						     * values are:
						     *
						     * NORMAL:
						     *   Allow more flexibility
						     *   in the generation of
						     *   formulae.
						     * NNF:
						     *   Force generation into
						     *   negation normal form.
						     */

    ::Ltl::FormulaRandomizer formula_generator;     /* Interface to the random
						     * LTL formula generation
						     * algorithm.
						     */
  };

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  vector<AlgorithmInformation,                      /* A vector containing   */
	 ALLOC(AlgorithmInformation) > algorithms;  /* information about the
						     * algorithms used in
						     * the tests.
						     */

  GlobalConfiguration global_options;               /* General configuration
						     * information.
						     */

  FormulaConfiguration formula_options;             /* Configuration
						     * information for
						     * generating random
						     * formulae.
						     */

  Graph::StateSpaceRandomizer                       /* Interface to the   */
    statespace_generator;                           /* random state space
						     * generation
						     * algorithms.
						     */

  typedef pair<int, int> IntPair;

  set<IntPair, less<IntPair>, ALLOC(IntPair) >      /* Configuration options */
    locked_options;                                 /* the values of which
						     * should not be
						     * initialized from the
						     * configuration file.
						     */

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  class ConfigurationException : public Exception   /* A class for reporting
						     * errors when reading
						     * the configuration file.
						     */
  {
  public:
    ConfigurationException                          /* Constructors. */
      (const string& info = "",
       const string& msg = "");

    ConfigurationException
      (int line_number = -1,
       const string& msg = "");

    ~ConfigurationException() throw();              /* Destructor. */

    /* default copy constructor */

    ConfigurationException& operator=               /* Assignment operator. */
      (const ConfigurationException& e);

    /* `what' inherited from class Exception */

    /* `changeMessage' inherited from class Exception */

    string line_info;                               /* Error context
						     * information.
						     */
  };

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  struct IntegerRange                               /* Data structure for
						     * representing integer-
						     * valued ranges of certain
						     * program configuration
						     * options.
						     */
  {
    long int min;                                   /* Lower bound. */

    long int max;                                   /* Upper bound. */

    char* error_message;                            /* Error message to be
						     * displayed if the value
						     * is not within the
						     * specified range.
						     */
  };

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  /*
   *  Ranges for certain integer-valued configuration options.
   */

  static const struct IntegerRange
    VERBOSITY_RANGE, ROUND_COUNT_RANGE, GENERATION_RANGE, PRIORITY_RANGE,
    PROPOSITION_COUNT_RANGE, FORMULA_SIZE_RANGE, FORMULA_MAX_SIZE_RANGE,
    STATESPACE_SIZE_RANGE, STATESPACE_MAX_SIZE_RANGE;

private:
  enum CommandLineOptionType                        /* Command line options. */
    {OPT_COMPARISONTEST = 10000, OPT_CONFIGFILE,
     OPT_CONSISTENCYTEST, OPT_DISABLE, OPT_ENABLE,
     OPT_FORMULACHANGEINTERVAL, OPT_FORMULAFILE,
     OPT_FORMULARANDOMSEED, OPT_HELP = 'h',
     OPT_GLOBALPRODUCT = 20000, OPT_INTERACTIVE,
     OPT_INTERSECTIONTEST, OPT_LOGFILE,
     OPT_MODELCHECK, OPT_NOCOMPARISONTEST,
     OPT_NOCONSISTENCYTEST, OPT_NOINTERSECTIONTEST,
     OPT_NOPAUSE, OPT_PAUSE, OPT_PAUSEONERROR,
     OPT_PROFILE, OPT_QUIET, OPT_ROUNDS,
     OPT_SHOWCONFIG, OPT_SHOWOPERATORDISTRIBUTION,
     OPT_SKIP, OPT_STATESPACECHANGEINTERVAL,
     OPT_STATESPACERANDOMSEED, OPT_VERBOSITY,
     OPT_VERSION,

     OPT_LOCALPRODUCT,

     OPT_ABBREVIATEDOPERATORS, OPT_ANDPRIORITY,
     OPT_BEFOREPRIORITY,
     OPT_DEFAULTOPERATORPRIORITY,
     OPT_EQUIVALENCEPRIORITY, OPT_FALSEPRIORITY,
     OPT_FINALLYPRIORITY,OPT_FORMULAGENERATEMODE,
     OPT_FORMULAOUTPUTMODE,
     OPT_FORMULAPROPOSITIONS, OPT_FORMULASIZE,
     OPT_GENERATENNF, OPT_GLOBALLYPRIORITY,
     OPT_IMPLICATIONPRIORITY, OPT_NEXTPRIORITY,
     OPT_NOABBREVIATEDOPERATORS,
     OPT_NOGENERATENNF, OPT_NOOUTPUTNNF,
     OPT_NOTPRIORITY, OPT_ORPRIORITY,
     OPT_OUTPUTNNF, OPT_PROPOSITIONPRIORITY,
     OPT_RELEASEPRIORITY,
     OPT_STRONGRELEASEPRIORITY, OPT_TRUEPRIORITY,
     OPT_UNTILPRIORITY, OPT_WEAKUNTILPRIORITY,
     OPT_XORPRIORITY,

     OPT_EDGEPROBABILITY,
     OPT_ENUMERATEDPATH, OPT_RANDOMCONNECTEDGRAPH,
     OPT_RANDOMGRAPH, OPT_RANDOMPATH,
     OPT_STATESPACEGENERATEMODE,
     OPT_STATESPACEPROPOSITIONS,
     OPT_STATESPACESIZE, OPT_TRUTHPROBABILITY};

  typedef map<pair<int, int>, double,               /* Type definitions for  */
	      less<pair<int, int> >,                /* the result cache used */
		   ALLOC(double) >                  /* for computing the    */
    ProbabilityMapElement;                          /* probability           */
  typedef map<int, ProbabilityMapElement,           /* distribution of LTL   */
	      less<int>,                            /* formula operators.    */
	      ALLOC(ProbabilityMapElement) >
    ProbabilityMap;

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  Configuration(const Configuration& cfg);          /* Prevent copying and */
  Configuration& operator=                          /* assignment of       */
    (const Configuration& cfg);                     /* Configuration
						     * objects.
						     */

  void reset();                                     /* Initializes the
						     * configuration data
						     * to default values.
						     */

  long int parseCommandLineInteger                  /* Converts an integer */
    (const string& option, const string& value)     /* to a string with    */
    const;                                          /* some additional
						     * validity checks.
						     */

  double operatorProbability                        /* Computes the         */
    (const int op, const int k, const int n,        /* probability with     */
     const int total_short_unary_priority,          /* which the operator   */
     const int total_long_unary_priority,           /* `op' will occur      */
     const int total_binary_priority,               /* exactly `k' times in */
     ProbabilityMap& result_cache) const;           /* a randomly generated
						     * formula of size `n'.
						     */
};



/******************************************************************************
 *
 * Inline function definitions for class Configuration::ConfigurationException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline Configuration::ConfigurationException::ConfigurationException
  (const string& info, const string& msg) :
  Exception(msg), line_info(info)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class Configuration::ConfigurationException.
 *                Creates a new exception object, initializing it with an
 *                error message and optional context information.
 *
 * Arguments:     info  --  Configuration file line number information (for
 *                          telling about the context of the error).
 *                msg   --  A reference to a constant string containing the
 *                          error message.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline Configuration::ConfigurationException::ConfigurationException
  (int line_number, const string& msg) :
  Exception(msg), line_info(line_number == -1
			    ? string("")
			    : ::StringUtil::toString(line_number))
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class Configuration::ConfigurationException.
 *                Creates a new exception object, initializing it with an
 *                error message and configuration file line number.
 *
 * Arguments:     line_number  --  Number of the line with an error (if -1,
 *                                 no context info is assumed to be present).
 *                msg          --  A reference to a constant string containing
 *                                 the error message.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline Configuration::ConfigurationException::~ConfigurationException() throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class Configuration::ConfigurationException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline Configuration::ConfigurationException&
Configuration::ConfigurationException::operator=
  (const ConfigurationException& e)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class
 *                Configuration::ConfigurationException. Assigns the value of
 *                another Configuration::ConfigurationException to `this' one.
 *
 * Arguments:     e  --  A reference to a constant
 *                       Configuration::ConfigurationException.
 *
 * Returns:       A reference to the object whose value was changed.
 *
 * ------------------------------------------------------------------------- */
{
  Exception::operator=(e);
  line_info = e.line_info;
  return *this;
}



#endif /* !CONFIGURATION_H */
