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
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include "Configuration.h"
#ifdef HAVE_GETOPT_LONG
#include <getopt.h>
#define OPTIONSTRUCT struct option
#else
#include "gnu-getopt.h"
#define optarg gnu_optarg
#define optind gnu_optind
#define opterr gnu_opterr
#define OPTIONSTRUCT struct gnu_option
#define getopt_long gnu_getopt_long
#endif  /* HAVE_GETOPT_LONG */

/******************************************************************************
 *
 * Declarations for functions and variables provided by the parser.
 *
 *****************************************************************************/

#include "Config-parse.h"                           /* Include declarations for
						     * the tokens that may be
						     * present in the
						     * configuration file.
						     */

extern int parseConfiguration                       /* Parser interface. */
  (FILE*, Configuration&);

extern void checkIntegerRange                       /* Range checking */
  (long int,                                        /* functions.     */
   const struct Configuration::IntegerRange&,
   bool);

extern void checkProbability(double, bool);



/******************************************************************************
 *
 * Definitions for ranges of certain integer-valued configuration options.
 *
 *****************************************************************************/

const struct Configuration::IntegerRange Configuration::VERBOSITY_RANGE
  = {0, 5, "verbosity must be between 0 and 5 (inclusive)"};

const struct Configuration::IntegerRange Configuration::ROUND_COUNT_RANGE
  = {1, LONG_MAX, "number of rounds must be positive"};

const struct Configuration::IntegerRange Configuration::GENERATION_RANGE
  = {0, LONG_MAX, "length of interval must be nonnegative"};

const struct Configuration::IntegerRange Configuration::PRIORITY_RANGE
  = {0, INT_MAX / 14, "priority value out of range"};

const struct Configuration::IntegerRange
  Configuration::PROPOSITION_COUNT_RANGE
    = {0, LONG_MAX, "number of propositions must be nonnegative"};

const struct Configuration::IntegerRange Configuration::FORMULA_SIZE_RANGE
  = {1, LONG_MAX, "formula size must be always positive"};

const struct Configuration::IntegerRange
  Configuration::FORMULA_MAX_SIZE_RANGE
    = {1, LONG_MAX, "minimum formula size exceeds the maximum formula size"};

const struct Configuration::IntegerRange Configuration::STATESPACE_SIZE_RANGE
  = {1, LONG_MAX, "state space size must be always positive"};

const struct Configuration::IntegerRange
  Configuration::STATESPACE_MAX_SIZE_RANGE
    = {1, LONG_MAX, "minimum state space size exceeds the maximum state space "
		    "size"};



/******************************************************************************
 *
 * Function definitions for class Configuration.
 *
 *****************************************************************************/

/* ========================================================================= */
Configuration::Configuration()
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class Configuration. Creates a new data
 *                structure for storing program configuration and initializes
 *                the configuration parameters to their default values.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  reset();
}

/* ========================================================================= */
Configuration::~Configuration()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class Configuration.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  for (vector<AlgorithmInformation, ALLOC(AlgorithmInformation) >
	 ::const_iterator it = algorithms.begin();
       it != algorithms.end(); ++it)
  {
    delete it->name;
    delete it->path_to_program;
    delete it->extra_parameters;
  }
}

/* ========================================================================= */
void Configuration::read(int argc, char* argv[])
/* ----------------------------------------------------------------------------
 *
 * Description:   Parses the command line parameters passed to the program and
 *                reads the configuration file.
 *
 * Arguments:     argc  --  (Number of command line parameters) + 1.
 *                argv  --  Array of C-style strings storing the parameters.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  using namespace ::StringUtil;

  reset();

  /*
   *  Command line option declarations.
   */

  static OPTIONSTRUCT command_line_options[] =
  {
    {"comparisontest",        no_argument,       0, OPT_COMPARISONTEST},
    {"comparisoncheck",       no_argument,       0, OPT_COMPARISONTEST},
    {"configfile",            required_argument, 0, OPT_CONFIGFILE},
    {"consistencytest",       no_argument,       0, OPT_CONSISTENCYTEST},
    {"consistencycheck",      no_argument,       0, OPT_CONSISTENCYTEST},
    {"disable",               required_argument, 0, OPT_DISABLE},
    {"enable",                required_argument, 0, OPT_ENABLE},
    {"formulachangeinterval", required_argument, 0, OPT_FORMULACHANGEINTERVAL},
    {"formulafile",           required_argument, 0, OPT_FORMULAFILE},
    {"formularandomseed",     required_argument, 0, OPT_FORMULARANDOMSEED},
    {"globalmodelcheck",      no_argument,       0, OPT_GLOBALPRODUCT},
    {"help",                  no_argument,       0, OPT_HELP},
    {"interactive",           required_argument, 0, OPT_INTERACTIVE},
    {"intersectiontest",      no_argument,       0, OPT_INTERSECTIONTEST},
    {"intersectioncheck",     no_argument,       0, OPT_INTERSECTIONTEST},
    {"localmodelcheck",       no_argument,       0, OPT_LOCALPRODUCT},
    {"logfile",               required_argument, 0, OPT_LOGFILE},
    {"modelcheck",            required_argument, 0, OPT_MODELCHECK},
    {"nocomparisontest",      no_argument,       0, OPT_NOCOMPARISONTEST},
    {"nocomparisoncheck",     no_argument,       0, OPT_NOCOMPARISONTEST},
    {"noconsistencytest",     no_argument,       0, OPT_NOCONSISTENCYTEST},
    {"noconsistencycheck",    no_argument,       0, OPT_NOCONSISTENCYTEST},
    {"nointersectiontest",    no_argument,       0, OPT_NOINTERSECTIONTEST},
    {"nointersectioncheck",   no_argument,       0, OPT_NOINTERSECTIONTEST},
    {"nopause",               no_argument,       0, OPT_NOPAUSE},
    {"pause",                 no_argument,       0, OPT_PAUSE},
    {"pauseonerror",          no_argument,       0, OPT_PAUSEONERROR},
    {"profile",               no_argument,       0, OPT_PROFILE},
    {"quiet",                 no_argument,       0, OPT_QUIET},
    {"rounds",                required_argument, 0, OPT_ROUNDS},
    {"showconfig",            no_argument,       0, OPT_SHOWCONFIG},
    {"showoperatordistribution", no_argument, 0, OPT_SHOWOPERATORDISTRIBUTION},
    {"silent",                no_argument,       0, OPT_QUIET},
    {"skip",                  required_argument, 0, OPT_SKIP},
    {"statespacechangeinterval", required_argument, 0,
     OPT_STATESPACECHANGEINTERVAL},
    {"statespacerandomseed",  required_argument, 0, OPT_STATESPACERANDOMSEED},
    {"verbosity",             required_argument, 0, OPT_VERBOSITY},
    {"version",               no_argument,       0, OPT_VERSION},

    {"abbreviatedoperators",  no_argument,       0, OPT_ABBREVIATEDOPERATORS},
    {"andpriority",           required_argument, 0, OPT_ANDPRIORITY},
    {"beforepriority",        required_argument, 0, OPT_BEFOREPRIORITY},
    {"defaultoperatorpriority", required_argument, 0,
     OPT_DEFAULTOPERATORPRIORITY},
    {"equivalencepriority",   required_argument, 0, OPT_EQUIVALENCEPRIORITY},
    {"falsepriority",         required_argument, 0, OPT_FALSEPRIORITY},
    {"finallypriority",       required_argument, 0, OPT_FINALLYPRIORITY},
    {"formulageneratemode",   required_argument, 0, OPT_FORMULAGENERATEMODE},
    {"formulaoutputmode",     required_argument, 0, OPT_FORMULAOUTPUTMODE},
    {"formulapropositions",   required_argument, 0, OPT_FORMULAPROPOSITIONS},
    {"formulasize",           required_argument, 0, OPT_FORMULASIZE},
    {"generatennf",           no_argument,       0, OPT_GENERATENNF},
    {"globallypriority",      required_argument, 0, OPT_GLOBALLYPRIORITY},
    {"implicationpriority",   required_argument, 0, OPT_IMPLICATIONPRIORITY},
    {"nextpriority",          required_argument, 0, OPT_NEXTPRIORITY},
    {"noabbreviatedoperators", no_argument, 0, OPT_NOABBREVIATEDOPERATORS},
    {"nogeneratennf",         no_argument,       0, OPT_NOGENERATENNF},
    {"nooutputnnf",           no_argument,       0, OPT_NOOUTPUTNNF},
    {"notpriority",           required_argument, 0, OPT_NOTPRIORITY},
    {"orpriority",            required_argument, 0, OPT_ORPRIORITY},
    {"outputnnf",             no_argument,       0, OPT_OUTPUTNNF},
    {"propositionpriority",   required_argument, 0, OPT_PROPOSITIONPRIORITY},
    {"releasepriority",       required_argument, 0, OPT_RELEASEPRIORITY},
    {"strongreleasepriority", required_argument, 0, OPT_STRONGRELEASEPRIORITY},
    {"truepriority",          required_argument, 0, OPT_TRUEPRIORITY},
    {"untilpriority",         required_argument, 0, OPT_UNTILPRIORITY},
    {"weakuntilpriority",     required_argument, 0, OPT_WEAKUNTILPRIORITY},
    {"xorpriority",           required_argument, 0, OPT_XORPRIORITY},

    {"edgeprobability",       required_argument, 0, OPT_EDGEPROBABILITY},
    {"enumeratedpath",        no_argument,       0, OPT_ENUMERATEDPATH},
    {"randomconnectedgraph",  no_argument,       0, OPT_RANDOMCONNECTEDGRAPH},
    {"randomgraph",           no_argument,       0, OPT_RANDOMGRAPH},
    {"randompath",            no_argument,       0, OPT_RANDOMPATH},
    {"statespacegeneratemode", required_argument, 0,
     OPT_STATESPACEGENERATEMODE},
    {"statespacepropositions", required_argument, 0,
     OPT_STATESPACEPROPOSITIONS},
    {"statespacesize",        required_argument, 0, OPT_STATESPACESIZE},
    {"truthprobability",      required_argument, 0, OPT_TRUTHPROBABILITY},

    {0,                       0,                 0, 0}
  };

  opterr = 1;

  int opttype;
  int option_index;
  bool error = false, print_config = false,
       print_operator_distribution = false;

  string enabled_or_disabled_algorithms[2];

  locked_options.clear();

  /*
   *  Read the command line parameters.
   */

  do
  {
    option_index = 0;
    opttype = getopt_long(argc, argv, "h", command_line_options,
			  &option_index);

    switch (opttype)
    {
      case OPT_COMPARISONTEST :
      case OPT_NOCOMPARISONTEST :
	global_options.do_comp_test = (opttype == OPT_COMPARISONTEST);
	locked_options.insert(make_pair(CFG_GLOBALOPTIONS,
					CFG_COMPARISONTEST));
	break;

      case OPT_CONFIGFILE :
	global_options.cfg_filename = optarg;
	break;

      case OPT_CONSISTENCYTEST :
      case OPT_NOCONSISTENCYTEST :
	global_options.do_cons_test = (opttype == OPT_CONSISTENCYTEST);
	locked_options.insert(make_pair(CFG_GLOBALOPTIONS,
					CFG_CONSISTENCYTEST));
	break;

      case OPT_DISABLE :
      case OPT_ENABLE :
	{
	  int i = (opttype == OPT_DISABLE ? 1 : 0);

	  if (!enabled_or_disabled_algorithms[i].empty())
	    enabled_or_disabled_algorithms[i] += ',';
	  enabled_or_disabled_algorithms[i] += optarg;
	}
	break;

      case OPT_FORMULACHANGEINTERVAL :
      case OPT_STATESPACECHANGEINTERVAL :
	{
	  long int interval_length
	    = parseCommandLineInteger
		(command_line_options[option_index].name, optarg);
	  checkIntegerRange(interval_length, GENERATION_RANGE, false);

	  if (opttype == OPT_FORMULACHANGEINTERVAL)
	  {
	    global_options.formula_change_interval = interval_length;
	    locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					    CFG_CHANGEINTERVAL));
	  }
	  else
	  {
	    global_options.statespace_change_interval = interval_length;
	    locked_options.insert(make_pair(CFG_STATESPACEOPTIONS,
					    CFG_CHANGEINTERVAL));
	  }
	}

	break;

      case OPT_FORMULAFILE :
	global_options.formula_input_filename = optarg;
	break;

      case OPT_FORMULARANDOMSEED :
	global_options.formula_random_seed
	  = parseCommandLineInteger
	      (command_line_options[option_index].name, optarg);
	locked_options.insert(make_pair(CFG_FORMULAOPTIONS, CFG_RANDOMSEED));
	break;

      case OPT_GLOBALPRODUCT :
	global_options.product_mode = GLOBAL;
	locked_options.insert(make_pair(CFG_GLOBALOPTIONS, CFG_MODELCHECK));
	break;

      case OPT_HELP :
	showCommandLineHelp(argv[0]);
	exit(0);

      case OPT_INTERACTIVE :
	if (strcmp(optarg, "always") == 0)
	  global_options.interactive = ALWAYS;
	else if (strcmp(optarg, "onerror") == 0)
	  global_options.interactive = ONERROR;
	else if (strcmp(optarg, "never") == 0)
	  global_options.interactive = NEVER;
	else
	  error = true;

	locked_options.insert(make_pair(CFG_GLOBALOPTIONS, CFG_INTERACTIVE));
	break;

      case OPT_INTERSECTIONTEST :
      case OPT_NOINTERSECTIONTEST :
	global_options.do_intr_test = (opttype == OPT_INTERSECTIONTEST);
	locked_options.insert(make_pair(CFG_GLOBALOPTIONS,
					CFG_INTERSECTIONTEST));
	break;

      case OPT_LOCALPRODUCT :
	global_options.product_mode = LOCAL;
	locked_options.insert(make_pair(CFG_GLOBALOPTIONS, CFG_MODELCHECK));
	break;

      case OPT_LOGFILE :
	global_options.transcript_filename = optarg;
	break;

      case OPT_MODELCHECK :
	if (strcmp(optarg, "global") == 0)
	  global_options.product_mode = GLOBAL;
	else if (strcmp(optarg, "local") == 0)
	  global_options.product_mode = LOCAL;
	else
	  error = true;

	locked_options.insert(make_pair(CFG_GLOBALOPTIONS, CFG_MODELCHECK));
	break;

      case OPT_PAUSE :
      case OPT_NOPAUSE :
	global_options.interactive = (opttype == OPT_PAUSE ? ALWAYS : NEVER);
	locked_options.insert(make_pair(CFG_GLOBALOPTIONS, CFG_INTERACTIVE));
	break;

      case OPT_PAUSEONERROR :
	global_options.interactive = ONERROR;
	locked_options.insert(make_pair(CFG_GLOBALOPTIONS, CFG_INTERACTIVE));
	break;

      case OPT_PROFILE :
	global_options.do_comp_test
	  = global_options.do_cons_test
	  = global_options.do_intr_test
	  = false;
	locked_options.insert(make_pair(CFG_GLOBALOPTIONS,
					CFG_COMPARISONTEST));
	locked_options.insert(make_pair(CFG_GLOBALOPTIONS,
					CFG_CONSISTENCYTEST));
	locked_options.insert(make_pair(CFG_GLOBALOPTIONS,
					CFG_INTERSECTIONTEST));
	break;

      case OPT_QUIET :
	global_options.verbosity = 0;
	locked_options.insert(make_pair(CFG_GLOBALOPTIONS, CFG_VERBOSITY));
	global_options.interactive = NEVER;
	locked_options.insert(make_pair(CFG_GLOBALOPTIONS, CFG_INTERACTIVE));
	break;

      case OPT_ROUNDS :
	{
	  long int number_of_rounds
	    = parseCommandLineInteger
		(command_line_options[option_index].name, optarg);
	  checkIntegerRange(number_of_rounds, ROUND_COUNT_RANGE, false);
	  global_options.number_of_rounds = number_of_rounds;
	  locked_options.insert(make_pair(CFG_GLOBALOPTIONS, CFG_ROUNDS));
	}

	break;

      case OPT_SHOWCONFIG :
	print_config = true;
	break;

      case OPT_SHOWOPERATORDISTRIBUTION :
	print_operator_distribution = true;
	break;

      case OPT_SKIP :
	{
	  long int rounds_to_skip
	    = parseCommandLineInteger
		(command_line_options[option_index].name, optarg);
	  checkIntegerRange(rounds_to_skip, ROUND_COUNT_RANGE, false);
	  global_options.init_skip = rounds_to_skip;
	}

	break;

      case OPT_STATESPACERANDOMSEED :
	global_options.statespace_random_seed
	  = parseCommandLineInteger
	      (command_line_options[option_index].name, optarg);
	locked_options.insert(make_pair(CFG_STATESPACEOPTIONS,
					CFG_RANDOMSEED));
	break;

      case OPT_VERBOSITY :
	{
	  long int verbosity
	    = parseCommandLineInteger
		(command_line_options[option_index].name, optarg);
	  checkIntegerRange(verbosity, VERBOSITY_RANGE, false);
	  global_options.verbosity = verbosity;
	  locked_options.insert(make_pair(CFG_GLOBALOPTIONS, CFG_VERBOSITY));
	}

	break;

      case OPT_VERSION :
	cout << "lbtt " PACKAGE_VERSION "\n"
		"lbtt is free software; you may change and "
		"redistribute it under the terms of\n"
		"the GNU General Public License. lbtt comes with "
		"NO WARRANTY. See the file\n"
		"COPYING for details.\n";
	exit(0);
	break;

      case OPT_ABBREVIATEDOPERATORS :
      case OPT_NOABBREVIATEDOPERATORS :
	formula_options.allow_abbreviated_operators
	  = (opttype == OPT_ABBREVIATEDOPERATORS);
	locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					CFG_ABBREVIATEDOPERATORS));
	break;

      case OPT_ANDPRIORITY :
      case OPT_BEFOREPRIORITY :
      case OPT_DEFAULTOPERATORPRIORITY :
      case OPT_EQUIVALENCEPRIORITY :
      case OPT_FALSEPRIORITY :
      case OPT_FINALLYPRIORITY :
      case OPT_GLOBALLYPRIORITY :
      case OPT_IMPLICATIONPRIORITY :
      case OPT_NEXTPRIORITY :
      case OPT_NOTPRIORITY :
      case OPT_ORPRIORITY :
      case OPT_PROPOSITIONPRIORITY :
      case OPT_RELEASEPRIORITY :
      case OPT_STRONGRELEASEPRIORITY :
      case OPT_TRUEPRIORITY :
      case OPT_UNTILPRIORITY :
      case OPT_WEAKUNTILPRIORITY :
      case OPT_XORPRIORITY :
	{
	  long int priority
	    = parseCommandLineInteger
		(command_line_options[option_index].name, optarg);

	  checkIntegerRange(priority, PRIORITY_RANGE, false);

	  int symbol = -1;

	  switch (opttype)
	  {
	    case OPT_ANDPRIORITY :
	      symbol = ::Ltl::LTL_CONJUNCTION;
	      locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					      CFG_ANDPRIORITY));
	      break;

	    case OPT_BEFOREPRIORITY :
	      symbol = ::Ltl::LTL_BEFORE;
	      locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					      CFG_BEFOREPRIORITY));
	      break;

	    case OPT_DEFAULTOPERATORPRIORITY :
	      formula_options.default_operator_priority = priority;
	      locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					      CFG_DEFAULTOPERATORPRIORITY));
	      break;

	    case OPT_EQUIVALENCEPRIORITY :
	      symbol = ::Ltl::LTL_EQUIVALENCE;
	      locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					      CFG_EQUIVALENCEPRIORITY));
	      break;

	    case OPT_FALSEPRIORITY :
	      symbol = ::Ltl::LTL_FALSE;
	      locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					      CFG_FALSEPRIORITY));
	      break;

	    case OPT_FINALLYPRIORITY :
	      symbol = ::Ltl::LTL_FINALLY;
	      locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					      CFG_FINALLYPRIORITY));
	      break;

	    case OPT_GLOBALLYPRIORITY :
	      symbol = ::Ltl::LTL_GLOBALLY;
	      locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					      CFG_GLOBALLYPRIORITY));
	      break;

	    case OPT_IMPLICATIONPRIORITY :
	      symbol = ::Ltl::LTL_IMPLICATION;
	      locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					      CFG_IMPLICATIONPRIORITY));
	      break;

	    case OPT_NEXTPRIORITY :
	      symbol = ::Ltl::LTL_NEXT;
	      locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					      CFG_NEXTPRIORITY));
	      break;

	    case OPT_NOTPRIORITY :
	      symbol = ::Ltl::LTL_NEGATION;
	      locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					      CFG_NOTPRIORITY));
	      break;

	    case OPT_ORPRIORITY :
	      symbol = ::Ltl::LTL_DISJUNCTION;
	      locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					      CFG_ORPRIORITY));
	      break;

	    case OPT_PROPOSITIONPRIORITY :
	      symbol = ::Ltl::LTL_ATOM;
	      locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					      CFG_PROPOSITIONPRIORITY));
	      break;

	    case OPT_RELEASEPRIORITY :
	      symbol = ::Ltl::LTL_V;
	      locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					      CFG_RELEASEPRIORITY));
	      break;

	    case OPT_STRONGRELEASEPRIORITY :
	      symbol = ::Ltl::LTL_STRONG_RELEASE;
	      locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					      CFG_STRONGRELEASEPRIORITY));
	      break;

	    case OPT_TRUEPRIORITY :
	      symbol = ::Ltl::LTL_TRUE;
	      locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					      CFG_TRUEPRIORITY));
	      break;

	    case OPT_UNTILPRIORITY :
	      symbol = ::Ltl::LTL_UNTIL;
	      locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					      CFG_UNTILPRIORITY));
	      break;

	    case OPT_WEAKUNTILPRIORITY :
	      symbol = ::Ltl::LTL_WEAK_UNTIL;
	      locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					      CFG_WEAKUNTILPRIORITY));
	      break;

	    case OPT_XORPRIORITY :
	      symbol = ::Ltl::LTL_XOR;
	      locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					      CFG_XORPRIORITY));

	    default :
	      break;
	  }

	  if (symbol != -1)
	    formula_options.symbol_priority[symbol] = priority;
	}

	break;

      case OPT_FORMULAGENERATEMODE :
	if (strcmp(optarg, "nnf") == 0)
	  formula_options.generate_mode = NNF;
	else if (strcmp(optarg, "normal") == 0)
	  formula_options.generate_mode = NORMAL;
	else
	  error = true;

	locked_options.insert(make_pair(CFG_FORMULAOPTIONS, CFG_GENERATEMODE));
	break;

      case OPT_FORMULAOUTPUTMODE :
	if (strcmp(optarg, "nnf") == 0)
	  formula_options.output_mode = NNF;
	else if (strcmp(optarg, "normal") == 0)
	  formula_options.output_mode = NORMAL;
	else
	  error = true;

	locked_options.insert(make_pair(CFG_FORMULAOPTIONS, CFG_OUTPUTMODE));
	break;

      case OPT_FORMULAPROPOSITIONS :
      case OPT_STATESPACEPROPOSITIONS :
	{
	  long int num_propositions
	    = parseCommandLineInteger
		(command_line_options[option_index].name, optarg);

	  checkIntegerRange(num_propositions, PROPOSITION_COUNT_RANGE, false);

	  if (opttype == OPT_STATESPACEPROPOSITIONS)
	  {
	    locked_options.insert(make_pair(CFG_STATESPACEOPTIONS,
					    CFG_PROPOSITIONS));
	    statespace_generator.atoms_per_state = num_propositions;
	  }
	  else
	  {
	    locked_options.insert(make_pair(CFG_FORMULAOPTIONS,
					    CFG_PROPOSITIONS));
	    formula_options.formula_generator.number_of_available_variables
	      = num_propositions;
	  }
	}

	break;

      case OPT_FORMULASIZE :
      case OPT_STATESPACESIZE :
	{
	  IntegerRange min_size_range, max_size_range;

	  if (opttype == OPT_FORMULASIZE)
	  {
	    min_size_range = FORMULA_SIZE_RANGE;
	    max_size_range = FORMULA_MAX_SIZE_RANGE;
	    locked_options.insert(make_pair(CFG_FORMULAOPTIONS, CFG_SIZE));
	  }
	  else
	  {
	    min_size_range = STATESPACE_SIZE_RANGE;
	    max_size_range = STATESPACE_MAX_SIZE_RANGE;
	    locked_options.insert(make_pair(CFG_STATESPACEOPTIONS, CFG_SIZE));
	  }

	  string value(optarg);
	  string::size_type index = value.find("...");

	  if (index == string::npos)
	  {
	    long int size
	      = parseCommandLineInteger
		  (command_line_options[option_index].name, value);

	    checkIntegerRange(size, min_size_range, false);

	    if (opttype == OPT_FORMULASIZE)
	      formula_options.formula_generator.size
		= formula_options.formula_generator.max_size
		= size;
	    else
	      statespace_generator.min_size
		= statespace_generator.max_size
		= size;
	  }
	  else
	  {
	    string option_name(command_line_options[option_index].name);

	    long int min = parseCommandLineInteger(option_name + " (min)",
						   value.substr(0, index));
	    long int max = parseCommandLineInteger(option_name + " (max)",
						   value.substr(index + 3));

	    checkIntegerRange(min, min_size_range, false);
	    max_size_range.min = min;
	    checkIntegerRange(max, max_size_range, false);

	    if (opttype == OPT_FORMULASIZE)
	    {
	      formula_options.formula_generator.size = min;
	      formula_options.formula_generator.max_size = max;
	    }
	    else
	    {
	      statespace_generator.min_size = min;
	      statespace_generator.max_size = max;
	    }
	  }
	}

	break;

      case OPT_GENERATENNF :
      case OPT_NOGENERATENNF :
	formula_options.generate_mode
	  = (opttype == OPT_GENERATENNF ? NNF : NORMAL);
	locked_options.insert(make_pair(CFG_FORMULAOPTIONS, CFG_GENERATEMODE));
	break;

      case OPT_OUTPUTNNF :
      case OPT_NOOUTPUTNNF :
	formula_options.output_mode
	  = (opttype == OPT_OUTPUTNNF ? NNF : NORMAL);
	locked_options.insert(make_pair(CFG_FORMULAOPTIONS, CFG_OUTPUTMODE));
	break;

      case OPT_EDGEPROBABILITY :
      case OPT_TRUTHPROBABILITY :
	{
	  char* endptr;
	  double probability = strtod(optarg, &endptr);

	  if (*endptr != '\0')
	    probability = -1.0;

	  checkProbability(probability, false);

	  if (opttype == OPT_EDGEPROBABILITY)
	  {
	    statespace_generator.edge_probability = probability;
	    locked_options.insert(make_pair(CFG_STATESPACEOPTIONS,
					    CFG_EDGEPROBABILITY));
	  }
	  else
	  {
	    statespace_generator.truth_probability = probability;
	    locked_options.insert(make_pair(CFG_STATESPACEOPTIONS,
					    CFG_TRUTHPROBABILITY));
	  }
	}

	break;

      case OPT_ENUMERATEDPATH :
	global_options.statespace_generation_mode = ENUMERATEDPATH;
	locked_options.insert(make_pair(CFG_STATESPACEOPTIONS,
					CFG_GENERATEMODE));
	break;

      case OPT_RANDOMCONNECTEDGRAPH :
	global_options.statespace_generation_mode = RANDOMCONNECTEDGRAPH;
	locked_options.insert(make_pair(CFG_STATESPACEOPTIONS,
					CFG_GENERATEMODE));
	break;

      case OPT_RANDOMGRAPH :
	global_options.statespace_generation_mode = RANDOMGRAPH;
	locked_options.insert(make_pair(CFG_STATESPACEOPTIONS,
					CFG_GENERATEMODE));
	break;

      case OPT_RANDOMPATH :
	global_options.statespace_generation_mode = RANDOMPATH;
	locked_options.insert(make_pair(CFG_STATESPACEOPTIONS,
					CFG_GENERATEMODE));
	break;

      case OPT_STATESPACEGENERATEMODE :
	if (strcmp(optarg, "randomconnectedgraph") == 0)
	  global_options.statespace_generation_mode = RANDOMCONNECTEDGRAPH;
	else if (strcmp(optarg, "randomgraph") == 0)
	  global_options.statespace_generation_mode = RANDOMGRAPH;
	else if (strcmp(optarg, "randompath") == 0)
	  global_options.statespace_generation_mode = RANDOMPATH;
	else if (strcmp(optarg, "enumeratedpath") == 0)
	  global_options.statespace_generation_mode = ENUMERATEDPATH;
	else
	  error = true;

	locked_options.insert(make_pair(CFG_STATESPACEOPTIONS,
					CFG_GENERATEMODE));
	break;

      case '?' :
      case ':' :
	exit(-1);
    }

    if (error)
      throw ConfigurationException
	("", string("unrecognized argument (`") + optarg
	     + "') for option `--"
	     + command_line_options[option_index].name + "'");
  }
  while (opttype != -1);

  if (optind != argc)
    throw ConfigurationException
      ("", string("unrecognized command line option `")
	   + argv[optind] + "'");

  /*
   *  Read the configuration file.
   */

  FILE* configuration_file = fopen(global_options.cfg_filename.c_str(), "r");
  if (configuration_file == NULL)
    throw ConfigurationException
	    ("", "error opening configuration file `"
		 + global_options.cfg_filename + "'");

  try
  {
    parseConfiguration(configuration_file, *this);
    fclose(configuration_file);
  }
  catch (const ConfigurationException&)
  {
    fclose(configuration_file);
    throw;
  }

  /*
   *  Use the information gathered from command line options to enable or
   *  disable some of the implementations.
   */

  set<unsigned long int, less<unsigned long int>, ALLOC(unsigned long int) >
    algorithm_id_set;

  for (int i = 0; i < 2; i++)
  {
    if (enabled_or_disabled_algorithms[i].empty())
      continue;

    try
    {
      parseInterval(enabled_or_disabled_algorithms[i], algorithm_id_set, 0,
		    algorithms.size() - 1);

      if (algorithm_id_set.empty())
	throw Exception();
    }
    catch (const Exception&)
    {
      throw ConfigurationException("",
				   string("invalid argument (`")
				   + enabled_or_disabled_algorithms[i]
				   + "') for option `--"
				   + (i == 0 ? "en" : "dis")
				   + "able'");
    }

    for (set<unsigned long int, less<unsigned long int>,
	     ALLOC(unsigned long int) >::const_iterator
	   id = algorithm_id_set.begin();
	 id != algorithm_id_set.end();
	 ++id)
    {
      if (*id >= algorithms.size())
	throw ConfigurationException
	  ("",
	   string("invalid implementation identifier (") + toString(*id)
	   + ") in the argument for `--"
	   + (i == 0 ? "en" : "dis")
	   + "able'");

      algorithms[*id].enabled = (i == 0);
    }
  }

  /*
   *  Check that the values for configuration options are within acceptable
   *  limits. Initialize also the values of unspecified options to their
   *  default values.
   */

  /*
   *  Check that the number of rounds to run is greater than the number of
   *  rounds to skip.
   */

  if (global_options.number_of_rounds <= global_options.init_skip)
    throw ConfigurationException
	    ("", "the argument for `--skip' must be less than the total "
		 "number of test rounds");

  /*
   *  Check that there is at least one algorithm available for use.
   */

  if (algorithms.empty())
    throw ConfigurationException
	    ("", "no implementations defined in the configuration file");

  /*
   *  The case where the number of available variables for propositional
   *  formulae is zero is equivalent to the case where propositional atoms are
   *  disallowed in the formulae altogether.
   */

  if (formula_options.formula_generator.number_of_available_variables == 0)
    formula_options.symbol_priority[::Ltl::LTL_ATOM] = 0;

  /*
   *  Verify that at least one propositional symbol class (a Boolean constant
   *  or a propositional atom) has a nonzero priority.
   */

  if (formula_options.symbol_priority[::Ltl::LTL_ATOM] == 0
      && formula_options.symbol_priority[::Ltl::LTL_TRUE] == 0
      && formula_options.symbol_priority[::Ltl::LTL_FALSE] == 0)
    throw ConfigurationException("", "at least one atomic symbol must have "
				     "nonzero priority");

  /*
   *  If the operators ->, <->, xor, <>, [], W and M are disallowed, set their
   *  priorities to zero.
   */

  if (!formula_options.allow_abbreviated_operators)
  {
    formula_options.symbol_priority[::Ltl::LTL_IMPLICATION] = 0;
    formula_options.symbol_priority[::Ltl::LTL_EQUIVALENCE] = 0;
    formula_options.symbol_priority[::Ltl::LTL_XOR] = 0;
    formula_options.symbol_priority[::Ltl::LTL_FINALLY] = 0;
    formula_options.symbol_priority[::Ltl::LTL_GLOBALLY] = 0;
    formula_options.symbol_priority[::Ltl::LTL_WEAK_UNTIL] = 0;
    formula_options.symbol_priority[::Ltl::LTL_STRONG_RELEASE] = 0;
    formula_options.symbol_priority[::Ltl::LTL_BEFORE] = 0;
  }

  /*
   *  Check that at least one unary operator has a nonzero priority.
   *  Initialize the priority of the operators whose priority has not yet been
   *  specified to the default priority.
   */

  bool unary_operator_allowed = false;

  for (map<int, int, less<int>, ALLOC(int) >::iterator
	 it = formula_options.symbol_priority.begin();
       it != formula_options.symbol_priority.end(); ++it)
  {
    if (it->second == -1)
      it->second = formula_options.default_operator_priority;

    if (it->second > 0 && !unary_operator_allowed)
      unary_operator_allowed =
	(it->first == ::Ltl::LTL_NEGATION || it->first == ::Ltl::LTL_NEXT
	 || it->first == ::Ltl::LTL_FINALLY
	 || it->first == ::Ltl::LTL_GLOBALLY);
  }

  if (!unary_operator_allowed)
    throw ConfigurationException("", "at least one unary operator must have "
				     "a nonzero priority");

  /*
   *  Initialize the random formula generator with priorities for the LTL
   *  formula symbols.
   */

  int total_short_unary_priority = 0;
  int total_long_unary_priority = 0;
  int total_binary_priority = 0;

  for (map<int, int, less<int>, ALLOC(int) >::const_iterator
	 it = formula_options.symbol_priority.begin();
       it != formula_options.symbol_priority.end(); ++it)
  {
    if (it->second != 0)
    {
      switch (it->first)
      {
	case ::Ltl::LTL_ATOM :
	case ::Ltl::LTL_TRUE :
	case ::Ltl::LTL_FALSE :
	  formula_options.formula_generator.useSymbol(it->first, it->second);
	  break;

	case ::Ltl::LTL_NEGATION :
	  formula_options.formula_generator.useShortOperator
	    (it->first, it->second);
	  total_short_unary_priority += it->second;
	  if (formula_options.generate_mode != NNF)
	  {
	    formula_options.formula_generator.useLongOperator
	      (it->first, it->second);
	    total_long_unary_priority += it->second;
	  }
	  break;

	case ::Ltl::LTL_NEXT :
	case ::Ltl::LTL_FINALLY :
	case ::Ltl::LTL_GLOBALLY :
	  formula_options.formula_generator.useShortOperator
	    (it->first, it->second);
	  total_short_unary_priority += it->second;
	  formula_options.formula_generator.useLongOperator
	    (it->first, it->second);
	  total_long_unary_priority += it->second;
	  break;

	case ::Ltl::LTL_CONJUNCTION :
	case ::Ltl::LTL_DISJUNCTION :
	case ::Ltl::LTL_IMPLICATION :
	case ::Ltl::LTL_EQUIVALENCE :
	case ::Ltl::LTL_XOR :
	case ::Ltl::LTL_UNTIL :
	case ::Ltl::LTL_V :
	case ::Ltl::LTL_WEAK_UNTIL :
	case ::Ltl::LTL_STRONG_RELEASE :
	case ::Ltl::LTL_BEFORE :
	  formula_options.formula_generator.useLongOperator
	    (it->first, it->second);
	  total_binary_priority += it->second;
	  break;
      }
    }
  }

  if (print_operator_distribution
      && global_options.formula_input_filename.empty())
  {
    /*
     *  Compute the probability distribution for the operators used in random
     *  LTL formula generation.
     */

    ProbabilityMap result_cache;

    for (unsigned long int k = 1;
	 k <= formula_options.formula_generator.max_size;
	 k++)
    {
      for (map<int, int, less<int>, ALLOC(int) >::const_iterator
	     op = formula_options.symbol_priority.begin();
	   op != formula_options.symbol_priority.end();
	   ++op)
      {
	if (op->second > 0)
	{
	  switch (op->first)
	  {
	    case ::Ltl::LTL_ATOM :
	    case ::Ltl::LTL_TRUE :
	    case ::Ltl::LTL_FALSE :
	      break;

	    default :
	      {
		double probability = 0.0;
		for (unsigned long int s
		       = formula_options.formula_generator.size;
		     s <= formula_options.formula_generator.max_size;
		     s++)
		{
		  if (k >= s)
		    continue;
		  probability += operatorProbability
				   (op->first, k, s,
				    total_short_unary_priority,
				    total_long_unary_priority,
				    total_binary_priority,
				    result_cache);
		}
		probability /= static_cast<double>
				 (formula_options.formula_generator.max_size
				  - formula_options.formula_generator.size
				  + 1);

		formula_options.symbol_distribution[op->first]
		  += k * probability;

		break;
	      }
	  }
	}
      }
    }
  }

  if (print_config)
  {
    print(cout);
    exit(0);
  }
}

/* ========================================================================= */
void Configuration::print(ostream& stream, int indent) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes information about the program configuration into a
 *                stream.
 *
 * Argument:      stream  --  A reference to an output stream.
 *                indent  --  Number of spaces to leave to the left of output.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  using namespace ::StringUtil;

  Exceptional_ostream estream(&stream, ios::badbit | ios::failbit);

  estream << string(indent, ' ') + "Program configuration:\n"
	     + string(indent, ' ') + string(22, '-') + "\n\n"
	     + string(indent + 2, ' ')
	     + toString(global_options.number_of_rounds)
	     + " test round"
	     + (global_options.number_of_rounds > 1 ? "s" : "");

  if (global_options.init_skip > 0)
    estream << " (of which the first "
	       + (global_options.init_skip > 1
		  ? toString(global_options.init_skip) + " rounds "
		  : string(""))
	       + "will be skipped)";

  estream << ".\n" + string(indent + 2, ' ');

  if (global_options.interactive == ALWAYS)
    estream << "Pausing after every test round.\n";
  else if (global_options.interactive == ONERROR)
    estream << "Testing will be interrupted in case of an error.\n";
  else
    estream << "Running in batch mode.\n";

  estream << string(indent + 2, ' ')
	     + "Using "
	     + (global_options.product_mode == GLOBAL
		? "global" : "local")
	     + " model checking for tests.\n";

  if (!global_options.transcript_filename.empty())
    estream << string(indent + 2, ' ') + "Writing error log to `"
	       + global_options.transcript_filename + "'.\n";

  estream << '\n' + string(indent + 2, ' ') + "Implementations:\n";

  vector<AlgorithmInformation, ALLOC(AlgorithmInformation) >::size_type
    algorithm_number = 0;

  for (vector<AlgorithmInformation, ALLOC(AlgorithmInformation) >
	 ::const_iterator a = algorithms.begin();
       a != algorithms.end();
       ++a)
  {
    estream << string(indent + 4, ' ') + algorithmString(algorithm_number);

    if (!a->enabled)
      estream << " (disabled)";

    estream << '\n';

    algorithm_number++;
  }

  estream << '\n' + string(indent + 2, ' ');

  if (global_options.do_comp_test || global_options.do_cons_test
      || global_options.do_intr_test)
  {
    estream << "Enabled tests:\n";
    if (global_options.do_comp_test)
      estream << string(indent + 4, ' ') +
		 "Model checking result cross-comparison test\n";
    if (global_options.do_cons_test)
      estream << string(indent + 4, ' ') +
		 "Model checking result consistency check\n";
    if (global_options.do_intr_test)
      estream << string(indent + 4, ' ') +
		 "Büchi automata intersection emptiness check\n";
  }
  else
    estream << "All automata correctness tests disabled.\n";

  estream << '\n' + string(indent + 2, ' ') + "Random state spaces:\n"
	     + string(indent + 4, ' ');

  switch (global_options.statespace_generation_mode)
  {
    case RANDOMGRAPH :
      estream << "Random graphs ";
      break;
    case RANDOMCONNECTEDGRAPH :
      estream << "Random connected graphs ";
      break;
    case RANDOMPATH :
      estream << "Random paths ";
      break;
    case ENUMERATEDPATH :
      estream << "Enumerated paths ";
      break;
    default :
      break;
  }

  estream << '(' + toString(statespace_generator.min_size);

  if (statespace_generator.max_size != statespace_generator.min_size
      && global_options.statespace_generation_mode != ENUMERATEDPATH)
    estream << "..." + toString(statespace_generator.max_size);

  estream << string(" state")
	     + (statespace_generator.max_size == 1 ? "" : "s") + ", "
	     + toString(statespace_generator.atoms_per_state)
	     + " atomic proposition"
	     + (statespace_generator.atoms_per_state == 1 ? "" : "s")
	     + ")\n" + string(indent + 4, ' ');

  if (global_options.statespace_change_interval == 0
      || global_options.statespace_change_interval
	   >= global_options.number_of_rounds)
    estream << "Using a fixed state space.\n" + string(indent + 4, ' ');
  else
  {
    estream << "New state space will be generated after every ";
    if (global_options.statespace_change_interval > 1)
    {
      estream << global_options.statespace_change_interval;

      if (global_options.statespace_change_interval % 100 < 10
	  || global_options.statespace_change_interval % 100 >= 20)
      {
	switch (global_options.statespace_change_interval % 10)
	{
	  case 1 : estream << "st"; break;
	  case 2 : estream << "nd"; break;
	  case 3 : estream << "rd"; break;
	  default : estream << "th"; break;
	}
      }
      else
	estream << "th";

      estream << ' ';
    }

    estream << "round.\n" + string(indent + 4, ' ');
  }

  if (global_options.statespace_generation_mode != ENUMERATEDPATH)
  {
    estream << "Random seed: "
	       + toString(global_options.statespace_random_seed)
	       + '\n' + string(indent + 4, ' ');

    if (global_options.statespace_generation_mode & GRAPH)
      estream << "Random edge probability: "
		 + toString(statespace_generator.edge_probability)
		 + '\n' + string(indent + 4, ' ');

    estream << "Propositional truth probability: "
	       + toString(statespace_generator.truth_probability)
	       + "\n";
  }

  estream << "\n" + string(indent + 2, ' ');

  if (global_options.formula_input_filename.empty())
  {
    estream << "Random LTL formulas:\n" + string(indent + 4, ' ')
	       + toString(formula_options.formula_generator.size);

    if (formula_options.formula_generator.max_size
	  != formula_options.formula_generator.size)
      estream << "..."
		 + toString(formula_options.formula_generator.max_size);

    estream << string(" parse tree node")
	       + (formula_options.formula_generator.max_size == 1 ? "" : "s")
	       + ", "
	       + toString(formula_options.formula_generator.
			    number_of_available_variables)
	       + " atomic proposition"
	       + (formula_options.formula_generator.
		    number_of_available_variables == 1 ? "" : "s");
  }
  else
    estream << "Reading LTL formulas from `"
	       + global_options.formula_input_filename
	       + "'.";

  estream << '\n' + string(indent + 4, ' ');

  if (global_options.formula_change_interval == 0
      || global_options.formula_change_interval
	   >= global_options.number_of_rounds)
    estream << "Using a fixed LTL formula.";
  else
  {
    estream << "New LTL formula will be "
	    << (global_options.formula_input_filename.empty()
		? "generate"
		: "rea")
	    << "d after every ";
    if (global_options.formula_change_interval > 1)
    {
      estream << global_options.formula_change_interval;

      if (global_options.formula_change_interval % 100 < 10
	  || global_options.formula_change_interval % 100 >= 20)
      {
	switch (global_options.formula_change_interval % 10)
	{
	  case 1 : estream << "st"; break;
	  case 2 : estream << "nd"; break;
	  case 3 : estream << "rd"; break;
	  default : estream << "th"; break;
	}
      }
      else
	estream << "th";

      estream << ' ';
    }

    estream << "round.";
  }

  estream << '\n';

  if (global_options.formula_input_filename.empty()
      && formula_options.generate_mode == NNF)
    estream << string(indent + 4, ' ')
	       + "Formulas will be generated into negation normal form.\n";
  else if (formula_options.output_mode == NNF)
    estream << string(indent + 4, ' ')
	       + "Formulas will be converted into negation normal form.\n";

  if (global_options.formula_input_filename.empty())
  {
    estream << string(indent + 4, ' ') + "Random seed: "
	       + toString(global_options.formula_random_seed)
	       + '\n' + string(indent + 4, ' ')
	       + "Atomic symbols in use (priority):\n"
	       + string(indent + 6, ' ');

    bool first_printed = false;

    for (map<int, int, less<int>, ALLOC(int) >::const_iterator
	   op = formula_options.symbol_priority.begin();
	 op != formula_options.symbol_priority.end();
	 ++op)
    {
      if ((op->first != ::Ltl::LTL_ATOM && op->first != ::Ltl::LTL_TRUE
	   && op->first != ::Ltl::LTL_FALSE)
	  || op->second == 0)
	continue;

      if (first_printed)
	estream << "; ";

      first_printed = true;

      switch (op->first)
      {
	case ::Ltl::LTL_ATOM :
	  estream << "propositions";
	  break;
	case ::Ltl::LTL_TRUE : case ::Ltl::LTL_FALSE :
	  estream << ::Ltl::infixSymbol(op->first);
	  break;
	default :
	  break;
      }

      estream << " (" << op->second << ')';
    }

    estream << '\n'
	    << string(indent + 4, ' ')
	       + "Operators used for random LTL formula generation:";

    string operator_name_string;
    string operator_priority_string;
    string operator_distribution_string;
    int number_of_operators_printed = 0;
    int max_operators_per_line
      = (formula_options.symbol_distribution.empty() ? 7 : 6);

    for (map<int, int, less<int>, ALLOC(int) >::const_iterator op
	   = formula_options.symbol_priority.begin();
	 op != formula_options.symbol_priority.end();
	 ++op)
    {
      if (op->first == ::Ltl::LTL_ATOM || op->first == ::Ltl::LTL_TRUE
	  || op->first == ::Ltl::LTL_FALSE || op->second == 0)
	continue;

      if (number_of_operators_printed % max_operators_per_line == 0)
      {
	operator_name_string = string(indent + 6, ' ') + "operator  ";
	operator_priority_string = string(indent + 6, ' ') + "priority  ";

	if (!formula_options.symbol_distribution.empty())
	{
	  operator_name_string = string(11, ' ') + operator_name_string;
	  operator_priority_string = string(11, ' ')
				     + operator_priority_string;
	  operator_distribution_string
	    = string(indent + 6, ' ') + "occurrences/formula  ";
	}
      }

      string symbol_string = ::Ltl::infixSymbol(op->first);
      operator_name_string += symbol_string;

      string priority_string = ::StringUtil::toString(op->second);
      operator_priority_string += priority_string;

      string distribution_string;
      if (!formula_options.symbol_distribution.empty())
      {
	distribution_string
	  = ::StringUtil::toString(formula_options.symbol_distribution.
				     find(op->first)->second, 3);

	operator_distribution_string += distribution_string;
      }

      if (number_of_operators_printed % max_operators_per_line
	    == max_operators_per_line - 1)
      {
	estream << '\n' + operator_name_string + '\n'
		   + operator_priority_string + '\n';

	if (!formula_options.symbol_distribution.empty())
	  estream << operator_distribution_string + '\n';
      }
      else
      {
	operator_name_string += string(9 - symbol_string.length(), ' ');
	operator_priority_string += string(9 - priority_string.length(), ' ');
	if (!formula_options.symbol_distribution.empty())
	  operator_distribution_string
	    += string(9 - distribution_string.length(), ' ');
      }

      number_of_operators_printed++;
    }

    if (number_of_operators_printed % max_operators_per_line != 0)
    {
      estream << '\n' + operator_name_string + '\n' + operator_priority_string
		 + '\n';

      if (!formula_options.symbol_distribution.empty())
	estream << operator_distribution_string + '\n';
    }
  }

  estream << '\n';
  estream.flush();
}

/* ========================================================================= */
string Configuration::algorithmString
  (vector<Configuration::AlgorithmInformation,
	  ALLOC(Configuration::AlgorithmInformation) >::size_type
     algorithm_id) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructs a string with an algorithm identifer and the name
 *                of the algorithm in the form "<id>: `<name>'".
 *
 * Arguments:     algorithm_id  --  Numeric identifier for the algorithm.
 *
 * Returns:       A string with the algorithm's id and name.
 *
 * ------------------------------------------------------------------------- */
{
  using namespace ::StringUtil;

  return toString(algorithm_id) + ": `" + *(algorithms[algorithm_id].name)
	 + '\'';
}

/* ========================================================================= */
void Configuration::showCommandLineHelp(const char* program_name)
/* ----------------------------------------------------------------------------
 *
 * Description:   Prints the list of command line options.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  cout << string("Usage: ") + program_name
	  + " [OPTION]...\n\nGeneral options:\n"
	    "  --[no]comparisontest        Enable or disable the model "
					   "checking result\n"
	    "                              cross-comparison test\n"
	    "  --configfile=FILE           Read configuration from FILE\n"
	    "  --[no]consistencytest       Enable or disable the model "
					   "checking result\n"
	    "                              consistency test\n"
	    "  --disable=IMPLEMENTATION-ID[,IMPLEMENTATION-ID...]\n"
	    "                              Exclude implementation(s) from "
					   "tests\n"
	    "  --enable=IMPLEMENTATION-ID[,IMPLEMENTATION-ID,...]\n"
	    "                              Include implementation(s) into "
					   "tests\n"
	    "  --formulafile=FILE          Read LTL formulas from FILE\n"
	    "  --globalmodelcheck          Use global model checking in "
					   "tests\n"
	    "                              (equivalent to "
					   "`--modelcheck=global')\n"
	    "  -h, --help                  Show this help and exit\n"
	    "  --interactive=MODE          Set the interactivity mode "
					   "(`always', `onerror', \n"
	    "                              `never')\n"
	    "  --[no]intersectiontest      Enable or disable the Büchi "
					   "automata\n"
	    "                              intersection emptiness test\n"
	    "  --localmodelcheck           Use local model checking in tests"
					   "\n"
	    "                              (equivalent to "
					   "`--modelcheck=local')\n"
	    "  --logfile=FILE              Write error log to FILE\n"
	    "  --modelcheck=MODE           Set model checking mode "
					   "(`global' or `local')\n"
	    "  --nopause                   Do not pause between test rounds "
					   "(equivalent to\n"
	    "                              `--interactive=never')\n"
	    "  --pause                     Pause unconditionally after every "
					   "test round\n"
	    "                              (equivalent to "
					   "`--interactive=always')\n"
	    "  --pauseonerror              Pause between test rounds only in "
					   "case of an\n"
	    "                              error (equivalent to "
					   "`--interactive=onerror')\n"
	    "  --profile                   Disable all automata correctness "
					   "tests\n"
	    "  --quiet, --silent           Run all tests silently without "
					   "interruption\n"
	    "  --rounds=NUMBER-OF-ROUNDS   Set number of test rounds (1-)\n"
	    "  --showconfig                Display current configuration and "
					   "exit\n"
	    "  --showoperatordistribution  Display probability distribution "
					   "for LTL formula\n"
	    "                              operators\n"
	    "  --skip=NUMBER-OF-ROUNDS     Set number of test rounds to skip "
					   "before\n"
	    "                              starting tests\n"
	    "  --verbosity=INTEGER         Set the verbosity of output (0-5)\n"
	    "  --version                   Display program version and exit"
					   "\n\n"
	    "LTL formula generation options:\n"
	    "  --[no]abbreviatedoperators  Allow or disallow operators ->, "
					   "<->, xor, <>,\n"
	    "                              [], u, w in the generated "
					   "formulas\n"
	    "  --andpriority=INTEGER       Set priority for the /\\ operator\n"
	    "  --beforepriority=INTEGER    Set priority for the Before "
					   "operator\n"
	    "  --defaultoperatorpriority=INTEGER\n"
	    "                              Set default priority for operators"
					   "\n"
	    "  --equivalencepriority=INTEGER\n"
	    "                              Set priority for the <-> operator\n"
	    "  --falsepriority=INTEGER     Set priority for the constant "
					   "`false'\n"
	    "  --finallypriority=INTEGER   Set priority for the <> operator\n"
	    "  --formulachangeinterval=NUMBER-OF-ROUNDS\n"
	    "                              Set formula generation interval in "
					   "test rounds\n"
	    "                              (0-)\n"
	    "  --formulageneratemode=MODE  Set formula generation mode "
					   "(`normal', `nnf')\n"
	    "  --formulaoutputmode=MODE    Set formula output mode (`normal', "
					   "`nnf')\n"
	    "  --formulapropositions=NUMBER-OF-PROPOSITIONS\n"
	    "                              Set maximum number of atomic "
					   "propositions in\n"
	    "                              generated formulas (0-)\n"
	    "  --formularandomseed=INTEGER Set random seed for the formula "
					   "generation\n"
	    "                              algorithm\n"
	    "  --formulasize=SIZE,\n"
	    "  --formulasize=MIN-SIZE...MAX-SIZE\n"
	    "                              Set size of random LTL formulas "
					   "(1-)\n"
	    "  --[no]generatennf           Force or prevent generating LTL "
					   "formulas in\n"
	    "                              negation normal form\n"
	    "  --globallypriority=INTEGER  Set priority for the [] operator\n"
	    "  --implicationpriority=INTEGER\n"
	    "                              Set priority for the -> operator\n"
	    "  --nextpriority=INTEGER      Set priority for the Next operator"
					   "\n"
	    "  --notpriority=INTEGER       Set priority for the negation "
					   "operator\n"
	    "  --orpriority=INTEGER        Set priority for the \\/ operator\n"
	    "  --[no]outputnnf             Enable or disable formula "
					   "translation to\n"
	    "                              negation normal form before "
					   "invoking the\n"
	    "                              translators\n"
	    "  --propositionpriority=INTEGER\n"
	    "                              Set priority for atomic "
					   "propositions\n"
	    "  --releasepriority=INTEGER   Set priority for the (Weak) Release"
					   " operator\n"
	    "  --strongreleasepriority=INTEGER\n"
	    "                              Set priority for the Strong "
					   "Release operator\n"
	    "  --truepriority=INTEGER      Set priority for the constant "
					   "`true'\n"
	    "  --untilpriority=INTEGER     Set priority for the (Strong) Until"
					   " operator\n"
	    "  --weakuntilpriority=INTEGER\n"
	    "                              Set priority for the Weak Until "
					   "operator\n"
	    "  --xorpriority=INTEGER       Set priority for the xor "
					   "operator\n\n"
	    "State space generation options:\n"
	    "  --edgeprobability=PROBABILITY\n"
	    "                              Set random edge probability for "
					   "state spaces\n"
	    "                              (0.0--1.0)\n"
	    "  --enumeratedpath            Enumerate all paths of a given "
					   "size and a given\n"
	    "                              number of propositions per state "
					   "(equivalent to\n"
	    "                              `--statespacegeneratemode="
					   "enumeratedpath')\n"
	    "  --randomconnectedgraph      Generate connected graphs as state "
					   "spaces\n"
	    "                              (equivalent to\n"
	    "                              `--statespacegeneratemode="
					   "randomconnectedgraph')\n"
	    "  --randomgraph               Generate random graphs as state "
					   "spaces\n"
	    "                              (equivalent to\n"
	    "                              `--statespacegeneratemode="
					   "randomgraph')\n"
	    "  --randompath                Generate random paths as state "
					   "spaces\n"
	    "                              (equivalent to\n"
	    "                              `--statespacegeneratemode="
					   "randompath')\n"
	    "  --statespacechangeinterval=NUMBER-OF-ROUNDS\n"
	    "                              Set state space generation "
					   "interval in test\n"
	    "                              rounds (0-)\n"
	    "  --statespacegeneratemode=MODE\n"
	    "                              Set state space generation mode\n"
	    "                              (`randomconnectedgraph', "
					   "`randomgraph',\n"
	    "                              `randompath', `enumeratedpath')\n"
	    "  --statespacepropositions=NUMBER-OF-PROPOSITIONS\n"
	    "                              Set number of propositions per "
					   "state (0-)\n"
	    "  --statespacerandomseed=INTEGER\n"
	    "                              Set random seed for the state "
					   "space generation\n"
	    "                              algorithm\n"
	    "  --statespacesize=SIZE,\n"
	    "  --statespacesize=MIN-SIZE...MAX-SIZE\n"
	    "                              Set size of generated state spaces "
					   "(1-)\n"
	    "  --truthprobability=PROBABILITY\n"
	    "                              Set truth probability of "
					   "propositions (0.0--1.0)\n\n"
	    "Report bugs to <heikki.tauriainen@hut.fi>.\n";
}

/* ========================================================================= */
void Configuration::reset()
/* ----------------------------------------------------------------------------
 *
 * Description:   Resets the program configuration to the default
 *                configuration.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  global_options.verbosity = 3;
  global_options.interactive = ALWAYS;
  global_options.number_of_rounds = 10;
  global_options.init_skip = 0;
  global_options.statespace_change_interval = 1;
  global_options.statespace_generation_mode = RANDOMCONNECTEDGRAPH;
  global_options.formula_change_interval = 1;
  global_options.product_mode = GLOBAL;
  global_options.cfg_filename = "config";
  global_options.transcript_filename = "";
  global_options.formula_input_filename = "";
  global_options.do_comp_test = true;
  global_options.do_cons_test = true;
  global_options.do_intr_test = true;
  global_options.statespace_random_seed = 1;
  global_options.formula_random_seed = 1;

  formula_options.default_operator_priority = 0;
  formula_options.symbol_priority.clear();
  formula_options.symbol_priority[::Ltl::LTL_ATOM] = 90;
  formula_options.symbol_priority[::Ltl::LTL_TRUE] = 5;
  formula_options.symbol_priority[::Ltl::LTL_FALSE] = 5;
  formula_options.symbol_priority[::Ltl::LTL_CONJUNCTION] = -1;
  formula_options.symbol_priority[::Ltl::LTL_DISJUNCTION] = -1;
  formula_options.symbol_priority[::Ltl::LTL_UNTIL] = -1;
  formula_options.symbol_priority[::Ltl::LTL_V] = -1;
  formula_options.symbol_priority[::Ltl::LTL_WEAK_UNTIL] = -1;
  formula_options.symbol_priority[::Ltl::LTL_STRONG_RELEASE] = -1;
  formula_options.symbol_priority[::Ltl::LTL_BEFORE] = -1;
  formula_options.symbol_priority[::Ltl::LTL_IMPLICATION] = -1;
  formula_options.symbol_priority[::Ltl::LTL_EQUIVALENCE] = -1;
  formula_options.symbol_priority[::Ltl::LTL_XOR] = -1;
  formula_options.symbol_priority[::Ltl::LTL_NEGATION] = -1;
  formula_options.symbol_priority[::Ltl::LTL_NEXT] = -1;
  formula_options.symbol_priority[::Ltl::LTL_FINALLY] = -1;
  formula_options.symbol_priority[::Ltl::LTL_GLOBALLY] = -1;

  formula_options.symbol_distribution.clear();

  formula_options.allow_abbreviated_operators = true;
  formula_options.output_mode = NORMAL;
  formula_options.generate_mode = NORMAL;

  formula_options.formula_generator.reset();

  statespace_generator.reset();

  locked_options.clear();
}

/* ========================================================================= */
long int Configuration::parseCommandLineInteger
  (const string& option, const string& value) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Converts an integer (given as a string) into a long int. Used
 *                when processing command line parameters.
 *
 * Arguments:     option  --  A reference to a constant string giving a name of
 *                            a command line option.
 *                value   --  A reference to a string which is supposed to
 *                            contain an integer.
 *
 * Returns:       The value of the integer.
 *
 * ------------------------------------------------------------------------- */
{
  char* endptr;
  long int val = strtol(value.c_str(), &endptr, 10);

  if (*endptr != '\0' || value.empty())
    throw ConfigurationException
	    ("", "the argument for `--" + option + "' must be a nonnegative "
		 "integer");

  if (val == LONG_MIN || val == LONG_MAX)
    throw ConfigurationException
	    ("", "the argument for `--" + option + "' is out of range");

  return val;
}

/* ========================================================================= */
double Configuration::operatorProbability
  (const int op, const int k, const int n,
   const int total_short_unary_priority, const int total_long_unary_priority,
   const int total_binary_priority,
   ProbabilityMap& result_cache) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Computes the probability with which a randomly generated
 *                formula of size `n' will contain exactly `k' occurrences of
 *                the operator `op'.
 *
 * Arguments:     op                          --  Operator type identifier.
 *                k                           --  Number of occurrences of `op'
 *                                                in a formula.
 *                n                           --  Formula size.
 *                total_short_unary_priority  --  Combined priority of all
 *                                                unary operators allowed in
 *                                                formulae of size 2.
 *                total_long_unary_priority   --  Combined priority of all
 *                                                unary operators allowed in
 *                                                formulae of size greater than
 *                                                2.
 *                total_binary_priority       --  Combined priority of all
 *                                                binary operators (allowed in
 *                                                formulae of size greater than
 *                                                2).
 *                result_cache                --  Data structure for storing
 *                                                intermediate results.
 *
 * Returns:       The probability with which a randomly generated formula of
 *                size `n' will contain exactly `k' occurrences of the operator
 *                `op'.
 *
 * ------------------------------------------------------------------------- */
{
  double result;

  int arity;
  int priority = formula_options.symbol_priority.find(op)->second;

  ProbabilityMap::const_iterator check_op(result_cache.find(op));
  if (check_op != result_cache.end())
  {
    ProbabilityMapElement::const_iterator check_p
      (check_op->second.find(make_pair(k, n)));
    if (check_p != check_op->second.end())
      return check_p->second;
  }

  switch (op)
  {
    case ::Ltl::LTL_NEGATION :
    case ::Ltl::LTL_NEXT :
    case ::Ltl::LTL_FINALLY :
    case ::Ltl::LTL_GLOBALLY :
      arity = 1;
      break;

    default :
      arity = 2;
      break;
  }

  if (k == 0)
  {
    result = 1.0;
    for (int kp = 1; kp < n; kp++)
      result -= operatorProbability(op, kp, n,
				    total_short_unary_priority,
				    total_long_unary_priority,
				    total_binary_priority,
				    result_cache);
  }
  else if (n == 1 || k >= n)
    result = 0.0;
  else if (k == 1 && n == 2)
  {
    if (arity == 1 && total_short_unary_priority > 0)
      result = static_cast<double>(priority)
		 / static_cast<double>(total_short_unary_priority);
    else
      result = 0.0;
  }
  else
  {
    int p1, p2;

    if (arity == 1)
    {
      p1 = total_long_unary_priority;
      if (op != ::Ltl::LTL_NEGATION || formula_options.generate_mode != NNF)
	p1 -=  priority;
      p2 = total_binary_priority;
    }
    else
    {
      p1 = total_long_unary_priority;
      p2 = total_binary_priority - priority;
    }

    result = 0.0;
    for (int m = 1; m <= n - 2; m++)
      for (int i = 0; i <= k; i++)
      {
	if (i >= m || k - i >= n - m - 1)
	  continue;
	result += operatorProbability(op, i, m,
				      total_short_unary_priority,
				      total_long_unary_priority,
				      total_binary_priority,
				      result_cache)
		    * operatorProbability(op, k - i, n - m - 1,
					  total_short_unary_priority,
					  total_long_unary_priority,
					  total_binary_priority,
					  result_cache);
      }

    result *= static_cast<double>(p2);

    if (arity == 1)
    {
      result /= static_cast<double>(n - 2);

      if (op != ::Ltl::LTL_NEGATION || formula_options.generate_mode != NNF)
	result += static_cast<double>(priority)
		    * operatorProbability(op, k - 1, n - 1,
					  total_short_unary_priority,
					  total_long_unary_priority,
					  total_binary_priority,
					  result_cache);
    }
    else
    {
      double r = 0.0;
      for (int m = 1; m <= n - 2; m++)
	for (int i = 0; i <= k - 1; i++)
	{
	  if (i >= m || k - 1 - i >= n - m - 1)
	    continue;
	  r += operatorProbability(op, i, m,
				   total_short_unary_priority,
				   total_long_unary_priority,
				   total_binary_priority,
				   result_cache)
		 * operatorProbability(op, k - 1 - i, n - m - 1,
				       total_short_unary_priority,
				       total_long_unary_priority,
				       total_binary_priority,
				       result_cache);
	}
      result += static_cast<double>(priority) * r;
      result /= static_cast<double>(n - 2);
    }

    result += static_cast<double>(p1)
		* operatorProbability(op, k, n - 1,
				      total_short_unary_priority,
				      total_long_unary_priority,
				      total_binary_priority,
				      result_cache);

    result /= static_cast<double>(total_long_unary_priority
				    + total_binary_priority);
  }

  result_cache[op][make_pair(k, n)] = result;
  return result;
}
