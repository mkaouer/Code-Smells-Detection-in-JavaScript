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
#include <climits>
#include <cstdlib>
#include <cstring>
#include "Configuration.h"
#include "IntervalList.h"
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
 * Definitions for ranges of certain integer-valued configuration options.
 *
 *****************************************************************************/
 
const struct Configuration::IntegerRange Configuration::DEFAULT_RANGE
  = {0, ULONG_MAX, "value out of range"};

const struct Configuration::IntegerRange Configuration::VERBOSITY_RANGE
  = {0, 5, "verbosity must be between 0 and 5 (inclusive)"};
 
const struct Configuration::IntegerRange Configuration::ROUND_COUNT_RANGE
  = {1, ULONG_MAX, "number of rounds must be positive"};

const struct Configuration::IntegerRange Configuration::RANDOM_SEED_RANGE
  = {0, UINT_MAX, "random seed out of range"};
 
const struct Configuration::IntegerRange Configuration::ATOMIC_PRIORITY_RANGE
  = {0, INT_MAX / 3, "priority out of range"};

const struct Configuration::IntegerRange Configuration::OPERATOR_PRIORITY_RANGE
  = {0, INT_MAX / 14, "priority out of range"};
 


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
  for (vector<AlgorithmInformation>::const_iterator it = algorithms.begin();
       it != algorithms.end(); ++it)
  {
    for (vector<string>::size_type p = 0; p <= it->num_parameters; ++p)
      delete[] it->parameters[p];
    delete[] it->parameters;
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

  /* Command line option declarations. */

  static OPTIONSTRUCT command_line_options[] =
  {
    {"comparisontest",        optional_argument, 0, OPT_COMPARISONTEST},
    {"comparisoncheck",       optional_argument, 0, OPT_COMPARISONTEST},
    {"configfile",            required_argument, 0, OPT_CONFIGFILE},
    {"consistencytest",       optional_argument, 0, OPT_CONSISTENCYTEST},
    {"consistencycheck",      optional_argument, 0, OPT_CONSISTENCYTEST},
    {"disable",               required_argument, 0, OPT_DISABLE},
    {"enable",                required_argument, 0, OPT_ENABLE},
    {"formulachangeinterval", required_argument, 0, OPT_FORMULACHANGEINTERVAL},
    {"formulafile",           required_argument, 0, OPT_FORMULAFILE},
    {"formularandomseed",     required_argument, 0, OPT_FORMULARANDOMSEED},
    {"globalmodelcheck",      no_argument,       0, OPT_GLOBALPRODUCT},
    {"help",                  no_argument,       0, OPT_HELP},
    {"interactive",           optional_argument, 0, OPT_INTERACTIVE},
    {"intersectiontest",      optional_argument, 0, OPT_INTERSECTIONTEST},
    {"intersectioncheck",     optional_argument, 0, OPT_INTERSECTIONTEST},
    {"localmodelcheck",       no_argument,       0, OPT_LOCALPRODUCT},
    {"logfile",               required_argument, 0, OPT_LOGFILE},
    {"modelcheck",            required_argument, 0, OPT_MODELCHECK},
    {"nocomparisontest",      no_argument,       0, OPT_COMPARISONTEST},
    {"nocomparisoncheck",     no_argument,       0, OPT_COMPARISONTEST},
    {"noconsistencytest",     no_argument,       0, OPT_CONSISTENCYTEST},
    {"noconsistencycheck",    no_argument,       0, OPT_CONSISTENCYTEST},
    {"nointersectiontest",    no_argument,       0, OPT_INTERSECTIONTEST},
    {"nointersectioncheck",   no_argument,       0, OPT_INTERSECTIONTEST},
    {"pause",                 optional_argument, 0, OPT_INTERACTIVE},
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
    {"translatortimeout",     required_argument, 0 ,OPT_TRANSLATORTIMEOUT},
    {"verbosity",             required_argument, 0, OPT_VERBOSITY},
    {"version",               no_argument,       0, OPT_VERSION},

    {"abbreviatedoperators",  optional_argument, 0, OPT_ABBREVIATEDOPERATORS},
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
    {"noabbreviatedoperators", no_argument,      0, OPT_ABBREVIATEDOPERATORS},
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

  opterr = 1; /* enable error messages from getopt_long */

  const char* false_value = "false", *true_value = "true",
              *always_value = "always";

  int opttype;
  int option_index;
  bool print_config = false, print_operator_distribution = false;
  config_file_line_number = -1;

  typedef pair<const OPTIONSTRUCT*, const char*> Parameter;
  vector<Parameter> parameters;

  /*
   *  Preprocess the command line parameters.  At this point only those special
   *  options that do not override settings in the configuration file are
   *  processed completely; all other options are stored in the vector
   *  `parameters' to be handled only after reading the configuration file.
   *  The arguments of all parameters taking optional parameters are
   *  adjusted here.
   */

  do
  {
    option_index = 0;
    opttype = getopt_long(argc, argv, "hV", command_line_options,
			  &option_index);

    switch (opttype)
    {
      case OPT_CONFIGFILE :
	global_options.cfg_filename = optarg;
	break;

      case OPT_FORMULAFILE :
	global_options.formula_input_filename = optarg;
	break;

      case OPT_HELP :
	showCommandLineHelp(argv[0]);
	exit(0);

      case OPT_LOGFILE :
	global_options.transcript_filename = optarg;
	break;

      case OPT_SHOWCONFIG :
	print_config = true;
	break;

      case OPT_SHOWOPERATORDISTRIBUTION :
	print_operator_distribution = true;
	break;

      case OPT_SKIP :
	readInteger(global_options.init_skip, optarg);
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

      case '?' :
      case ':' :
	exit(2);

      case -1 :
	break;

      case OPT_CONSISTENCYTEST :
      case OPT_COMPARISONTEST :
      case OPT_INTERSECTIONTEST :
      case OPT_ABBREVIATEDOPERATORS :
	{
	  const char* val;
	  if (command_line_options[option_index].name[0] == 'n')
	    val = false_value;
	  else if (optarg == 0)
	    val = true_value;
	  else
	    val = optarg;
	  parameters.push_back(make_pair(&command_line_options[option_index],
					 val));
	  break;
	}

      case OPT_INTERACTIVE :
	{
	  const char* val;
	  if (optarg == 0)
	    val = always_value;
	  else
	    val = optarg;
	  parameters.push_back(make_pair(&command_line_options[option_index],
					 val));
	  break;
	}

      default :
	parameters.push_back(make_pair(&command_line_options[option_index],
				       optarg));
	break;
    }
  }
  while (opttype != -1);

  /* Read the configuration file. */

  FILE* configuration_file = fopen(global_options.cfg_filename.c_str(), "r");
  if (configuration_file == NULL)
    throw ConfigurationException
            (-1, "error opening configuration file `"
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

  config_file_line_number = -1; /* Suppress configuration file line number in
				 * any future error messages */

  /*
   *  Process the command line parameters that override settings made in the
   *  configuration file.
   */

  vector<Parameter>::const_iterator parameter;

  try
  {
    for (parameter = parameters.begin(); parameter != parameters.end();
	 ++parameter)
    {
      switch (parameter->first->val)
      {
	/* Remaining special options (excluding "--enable" and "--disable"). */

        case OPT_ENABLE : case OPT_DISABLE :  /* These options can be    */
	  break;                              /* processed only after
					       * determining whether the
					       * internal model checking
					       * algorithm might be
					       * included in the tests.
					       */

        case OPT_PROFILE :
	  global_options.do_comp_test
	    = global_options.do_cons_test
	    = global_options.do_intr_test
	    = false;
	  break;

        case OPT_QUIET :
	  global_options.verbosity = 0;
	  global_options.interactive = NEVER;
	  break;

	/* 
	 *  Options corresponding to the GlobalOptions section in the
	 *  configuration file.
	 */

        case OPT_COMPARISONTEST :
	  readTruthValue(global_options.do_comp_test, parameter->second);
	  break;

        case OPT_CONSISTENCYTEST :
	  readTruthValue(global_options.do_cons_test, parameter->second);
	  break;

        case OPT_GLOBALPRODUCT :
	  readProductType("global");
	  break;

        case OPT_INTERACTIVE :
	  readInteractivity(parameter->second);
	  break;

        case OPT_INTERSECTIONTEST :
	  readTruthValue(global_options.do_intr_test, parameter->second);
	  break;

        case OPT_LOCALPRODUCT :
	  readProductType("local");
	  break;

        case OPT_MODELCHECK :
	  readProductType(parameter->second);
	  break;

        case OPT_ROUNDS :
	  readInteger(global_options.number_of_rounds, parameter->second,
		      ROUND_COUNT_RANGE);
	  break;

        case OPT_TRANSLATORTIMEOUT :
	  readTranslatorTimeout(parameter->second);
	  break;

        case OPT_VERBOSITY :
	  readInteger(global_options.verbosity, parameter->second,
		      VERBOSITY_RANGE);
	  break;

	/* 
	 *  Options corresponding to the StatespaceOptions section in the
	 *  configuration file.
	 */

        case OPT_EDGEPROBABILITY :
	  readProbability(statespace_generator.edge_probability,
			  parameter->second);
	  break;

        case OPT_ENUMERATEDPATH :
	  readStateSpaceMode("enumeratedpath");
	  break;
	
        case OPT_RANDOMCONNECTEDGRAPH :
	  readStateSpaceMode("randomconnectedgraph");
	  break;

        case OPT_RANDOMGRAPH :
	  readStateSpaceMode("randomgraph");
	  break;

        case OPT_RANDOMPATH :
	  readStateSpaceMode("randompath");
	  break;

        case OPT_STATESPACECHANGEINTERVAL :
	  readInteger(global_options.statespace_change_interval,
		      parameter->second);
	  break;

        case OPT_STATESPACEGENERATEMODE :
	  readStateSpaceMode(parameter->second);
	  break;

        case OPT_STATESPACEPROPOSITIONS :
	  readInteger(statespace_generator.atoms_per_state, parameter->second);
	  break;

        case OPT_STATESPACERANDOMSEED :
	  readInteger(global_options.statespace_random_seed,
		      parameter->second, RANDOM_SEED_RANGE);
	  break;

        case OPT_STATESPACESIZE :
	  readSize(parameter->first->val, parameter->second);
	  break;

        case OPT_TRUTHPROBABILITY :
	  readProbability(statespace_generator.truth_probability,
			  parameter->second);
	  break;

	/* 
	 *  Options corresponding to the FormulaOptions section in the
	 *  configuration file.
	 */

        case OPT_ABBREVIATEDOPERATORS :
	  readTruthValue(formula_options.allow_abbreviated_operators,
			 parameter->second);
	  break;

        case OPT_ANDPRIORITY :
	  readInteger(formula_options.symbol_priority[::Ltl::LTL_CONJUNCTION],
		      parameter->second, OPERATOR_PRIORITY_RANGE);
	  break;

        case OPT_BEFOREPRIORITY :
	  readInteger(formula_options.symbol_priority[::Ltl::LTL_BEFORE],
		      parameter->second, OPERATOR_PRIORITY_RANGE);
	  break;

        case OPT_DEFAULTOPERATORPRIORITY :
	  readInteger(formula_options.default_operator_priority,
		      parameter->second, OPERATOR_PRIORITY_RANGE);
	  break;

        case OPT_EQUIVALENCEPRIORITY :
	  readInteger(formula_options.symbol_priority[::Ltl::LTL_EQUIVALENCE],
		      parameter->second, OPERATOR_PRIORITY_RANGE);
	  break;

        case OPT_FALSEPRIORITY :
	  readInteger(formula_options.symbol_priority[::Ltl::LTL_FALSE],
		      parameter->second, ATOMIC_PRIORITY_RANGE);
	  break;

        case OPT_FINALLYPRIORITY :
	  readInteger(formula_options.symbol_priority[::Ltl::LTL_FINALLY],
		      parameter->second, OPERATOR_PRIORITY_RANGE);
	  break;

        case OPT_FORMULACHANGEINTERVAL :
	  readInteger(global_options.formula_change_interval,
		      parameter->second);
	  break;

        case OPT_FORMULAGENERATEMODE :
	  readFormulaMode(formula_options.generate_mode, parameter->second);
	  break;

        case OPT_FORMULAOUTPUTMODE :
	  readFormulaMode(formula_options.output_mode, parameter->second);
	  break;

        case OPT_FORMULAPROPOSITIONS :
	  readInteger(formula_options.formula_generator.
		        number_of_available_variables,
		      parameter->second);
	  break;

        case OPT_FORMULARANDOMSEED :
	  readInteger(global_options.formula_random_seed, parameter->second,
		      RANDOM_SEED_RANGE);
	  break;

        case OPT_FORMULASIZE :
	  readSize(parameter->first->val, parameter->second);
	  break;

        case OPT_GENERATENNF :
	  readFormulaMode(formula_options.generate_mode, "nnf");
	  break;

        case OPT_GLOBALLYPRIORITY :
	  readInteger(formula_options.symbol_priority[::Ltl::LTL_GLOBALLY],
		      parameter->second, OPERATOR_PRIORITY_RANGE);
	  break;

        case OPT_IMPLICATIONPRIORITY :
	  readInteger(formula_options.symbol_priority[::Ltl::LTL_IMPLICATION],
		      parameter->second, OPERATOR_PRIORITY_RANGE);
	  break;

        case OPT_NEXTPRIORITY :
	  readInteger(formula_options.symbol_priority[::Ltl::LTL_NEXT],
		      parameter->second, OPERATOR_PRIORITY_RANGE);
	  break;

        case OPT_NOGENERATENNF :
	  readFormulaMode(formula_options.generate_mode, "normal");
	  break;

        case OPT_NOOUTPUTNNF :
	  readFormulaMode(formula_options.output_mode, "normal");
	  break;

        case OPT_NOTPRIORITY :
	  readInteger(formula_options.symbol_priority[::Ltl::LTL_NEGATION],
		      parameter->second, OPERATOR_PRIORITY_RANGE);
	  break;

        case OPT_ORPRIORITY :
	  readInteger(formula_options.symbol_priority[::Ltl::LTL_DISJUNCTION],
		      parameter->second, OPERATOR_PRIORITY_RANGE);
	  break;

        case OPT_OUTPUTNNF :
	  readFormulaMode(formula_options.output_mode, "nnf");
	  break;

        case OPT_PROPOSITIONPRIORITY :
	  readInteger(formula_options.symbol_priority[::Ltl::LTL_ATOM],
		      parameter->second, ATOMIC_PRIORITY_RANGE);
	  break;

        case OPT_RELEASEPRIORITY :
	  readInteger(formula_options.symbol_priority[::Ltl::LTL_V],
		      parameter->second, OPERATOR_PRIORITY_RANGE);
	  break;

        case OPT_STRONGRELEASEPRIORITY :
	  readInteger(formula_options.symbol_priority
		        [::Ltl::LTL_STRONG_RELEASE],
		      parameter->second, OPERATOR_PRIORITY_RANGE);
	  break;

        case OPT_TRUEPRIORITY :
	  readInteger(formula_options.symbol_priority[::Ltl::LTL_TRUE],
		      parameter->second, ATOMIC_PRIORITY_RANGE);
	  break;

        case OPT_UNTILPRIORITY :
	  readInteger(formula_options.symbol_priority[::Ltl::LTL_UNTIL],
		      parameter->second, OPERATOR_PRIORITY_RANGE);
	  break;

        case OPT_WEAKUNTILPRIORITY :
	  readInteger(formula_options.symbol_priority[::Ltl::LTL_WEAK_UNTIL],
		      parameter->second, OPERATOR_PRIORITY_RANGE);
	  break;

        case OPT_XORPRIORITY :
	  readInteger(formula_options.symbol_priority[::Ltl::LTL_XOR],
		      parameter->second, OPERATOR_PRIORITY_RANGE);
	  break;
      }
    }

    /*
     *  If using paths as state spaces, include the internal model checking
     *  algorithm in the set of algorithms.
     */

    if (global_options.statespace_generation_mode & Configuration::PATH)
    {
      AlgorithmInformation lbtt_info = {"lbtt", new char*[1], 0, true};
      lbtt_info.parameters[0] = new char[1];

      algorithm_names["lbtt"] = algorithms.size();
      algorithms.push_back(lbtt_info);
    }

    /* Process "--enable" and "--disable" options. */

    for (parameter = parameters.begin(); parameter != parameters.end();
	 ++parameter)
    {
      switch (parameter->first->val)
      {
        case OPT_DISABLE :
        case OPT_ENABLE :
	  try
	  {
	    IntervalList algorithm_ids;
	    vector<string> nonnumeric_algorithm_ids;
	    string id_string
	      = substituteInQuotedString(parameter->second, ",", "\n",
					 INSIDE_QUOTES);

	    parseIntervalList(id_string, algorithm_ids, 0,
			      algorithms.size() - 1,
			      &nonnumeric_algorithm_ids);

	    for (vector<string>::iterator
		   id = nonnumeric_algorithm_ids.begin();
		 id != nonnumeric_algorithm_ids.end();
		 ++id)
	    {
	      *id = unquoteString(substituteInQuotedString(*id, "\n", ","));
	      map<string, unsigned long int>::const_iterator id_finder
		= algorithm_names.find(*id);
	      if (id_finder == algorithm_names.end())
		throw ConfigurationException
		        (-1,
			 string("unknown implementation identifier (`")
			 + *id
			 + "')");
	      algorithm_ids.merge(id_finder->second);
	    }

	    for (IntervalList::const_iterator id = algorithm_ids.begin();
		 id != algorithm_ids.end();
		 ++id)
	      algorithms[*id].enabled = (parameter->first->val == OPT_ENABLE);
	  }
	  catch (const IntervalRangeException& e)
          {
	    throw ConfigurationException
	            (-1,
		     string("invalid implementation identifier (")
		     + toString(e.getNumber())
		     + ")");
	  }

	  break;

        default :
	  break;
      }
    }
  }
  catch (ConfigurationException& e)
  {
    e.changeMessage(string("[--") + parameter->first->name + "]: " + e.what());
    throw e;
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
            (-1, "[--skip]: number of rounds is less than skip count");

  /*
   *  Check that there is at least one algorithm available for use.
   */

  if (algorithms.empty())
    throw ConfigurationException
            (-1, "no implementations defined in the configuration file");

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
    throw ConfigurationException(-1, "at least one atomic symbol should have "
                                     "a nonzero priority");

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

  for (map<int, int>::iterator it = formula_options.symbol_priority.begin();
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
    throw ConfigurationException(-1, "at least one unary operator should have "
                                     "a nonzero priority");

  /*
   *  Initialize the random formula generator with priorities for the LTL
   *  formula symbols.
   */

  int total_short_unary_priority = 0;
  int total_long_unary_priority = 0;
  int total_binary_priority = 0;

  for (map<int, int>::const_iterator
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
      for (map<int, int>::const_iterator
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
             + "Signalling a break will "
             + (global_options.handle_breaks ? "interrupt" : "abort")
             + " testing.\n";

  estream << string(indent + 2, ' ')
             + "Using "
             + (global_options.product_mode == GLOBAL
		? "global" : "local")
             + " model checking for tests.\n";

  if (!global_options.transcript_filename.empty())
    estream << string(indent + 2, ' ') + "Writing error log to `"
               + global_options.transcript_filename + "'.\n";

  estream << '\n' + string(indent + 2, ' ') + "Implementations:\n";

  vector<AlgorithmInformation>::size_type algorithm_number = 0;
  
  for (vector<AlgorithmInformation>::const_iterator a = algorithms.begin();
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
  if (global_options.translator_timeout > 0)
  {
    estream << "Timeout for translators is set to "
               + toString(global_options.translator_timeout)
               + " seconds";
    if (global_options.translator_timeout >= 60)
    {
      bool first_printed = false;
      estream << " (";
      if (global_options.translator_timeout >= 3600)
      {
	first_printed = true;
	estream << toString(global_options.translator_timeout / 3600) + " h";
      }
      if (global_options.translator_timeout % 3600 > 60)
      {
	if (first_printed)
	  estream << ' ';
	else
	  first_printed = true;
	estream << toString((global_options.translator_timeout % 3600) / 60)
	           + " min";
      }
      if (global_options.translator_timeout % 60 != 0)
      {
	if (first_printed)
	  estream << ' ';
	estream << toString(global_options.translator_timeout % 60) + " s";
      }
      estream << ')';
    }
    estream << ".\n";
  }
  else
    estream << "Translators are allowed to run until their termination.\n";

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
  {
    estream << "Reading LTL formulas from ";
    if (global_options.formula_input_filename == "-")
      estream << "standard input.";
    else
      estream << "`" + global_options.formula_input_filename + "'.";
  }

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

    for (map<int, int>::const_iterator
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

    for (map<int, int>::const_iterator op
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
  (vector<Configuration::AlgorithmInformation>::size_type algorithm_id) const
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
  using ::StringUtil::toString;
  return toString(algorithm_id) + ": `" + algorithms[algorithm_id].name + '\'';
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
            "  --comparisontest[=VALUE], --nocomparisontest\n"
            "                              Enable or disable the model "
                                           "checking result\n"
            "                              cross-comparison test\n"
            "  --configfile=FILE           Read configuration from FILE\n"
            "  --consistencytest[=VALUE], --noconsistencytest\n"
            "                              Enable or disable the model "
                                           "checking result\n"
            "                              consistency test\n"
            "  --disable=IMPLEMENTATION-ID[,IMPLEMENTATION-ID...]\n"
            "                              Exclude implementation(s) from "
                                           "tests\n"
            "  --enable=IMPLEMENTATION-ID[,IMPLEMENTATION-ID,...]\n"
            "                              Include implementation(s) into "
                                           "tests\n"
            "  --formulafile=FILE          Read LTL formulas from FILE "
            "(- = standard input)\n"
            "  --globalmodelcheck          Use global model checking in "
                                           "tests\n"
            "                              (equivalent to "
                                           "`--modelcheck=global')\n"
            "  -h, --help                  Show this help and exit\n"
            "  --interactive[=MODE[,MODE]], --pause[=MODE[,MODE]]\n"
            "                              Set the interactivity mode "
                                           "(`always', `onerror', \n"
            "                              `never', `onbreak')\n"
            "  --intersectiontest[=VALUE], --nointersectiontest\n"
            "                              Enable or disable the Büchi "
                                           "automata\n"
            "                              intersection emptiness test\n"
            "  --localmodelcheck           Use local model checking in tests"
                                           "\n"
            "                              (equivalent to "
                                           "`--modelcheck=local')\n"
            "  --logfile=FILE              Write error log to FILE\n"
            "  --modelcheck=MODE           Set model checking mode "
                                           "(`global' or `local')\n"
            "  --profile                   Disable all automata correctness "
                                           "tests\n"
            "  --quiet, --silent           Run all tests silently without "
                                           "pausing\n"
            "  --rounds=NUMBER-OF-ROUNDS   Set number of test rounds (1-)\n"
            "  --showconfig                Display current configuration and "
                                           "exit\n"
            "  --showoperatordistribution  Display probability distribution "
                                           "for LTL formula\n"
            "                              operators\n"
            "  --skip=NUMBER-OF-ROUNDS     Set number of test rounds to skip "
                                           "before\n"
            "                              starting tests\n"
            "  --translatortimeout=TIME    Set timeout for translators\n"
            "  --verbosity=INTEGER         Set the verbosity of output (0-5)\n"
            "  -V,--version                Display program version and exit"
                                           "\n\n"
            "LTL formula generation options:\n"
            "  --abbreviatedoperators[=VALUE], --noabbreviatedoperators\n"
            "                              Allow or disallow operators ->, "
                                           "<->, xor, <>, [],\n"
            "                              W, M, and B in the generated "
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
            "Report bugs to <" PACKAGE_BUGREPORT ">.\n";
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
  global_options.handle_breaks = false;
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
  global_options.translator_timeout = 0;

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
}

/* ========================================================================= */
void Configuration::registerAlgorithm
  (const string& name, const string& path, const string& parameters,
   bool enabled, const int block_begin_line)
/* ----------------------------------------------------------------------------
 *
 * Description:   Adds a new implementation to the configuration.
 *
 * Arguments:     name              --  Name of the implementation.  If empty,
 *                                      the implementation will be given the
 *                                      name `Algorithm n', where n is the
 *                                      number of previously registered
 *                                      algorithms.  The name "lbtt" is
 *                                      reserved and cannot be used as a name
 *                                      for an implementation.  In addition,
 *                                      `name' should be unique among the set
 *                                      of the names of previously registered
 *                                      implementations.
 *                path              --  Path to the executable file used for
 *                                      invoking the implementation.  This
 *                                      string should not be empty.
 *                parameters        --  Parameters for the implementation.
 *                                      Parameters containing white space
 *                                      should be quoted.
 *                enabled           --  Whether the implementation is initially
 *                                      enabled.
 *                block_begin_line  --  Number of the first line of the most
 *                                      recently encountered Algorithm block in
 *                                      the configuration file.
 *
 * Returns:       Nothing.  The function throws a ConfigurationException if
 *                `name' or `path' fails to satisfy one of the above
 *                requirements.
 *
 * ------------------------------------------------------------------------- */
{
  using namespace ::StringUtil;
  string error;

  AlgorithmInformation algorithm_information;

  if (!name.empty())
    algorithm_information.name = name;
  else
    algorithm_information.name = "Algorithm " + toString(algorithms.size());

  if (algorithm_information.name == "lbtt")
    error = "`lbtt' is a reserved name for an implementation";
  else if (algorithm_names.find(algorithm_information.name)
	     != algorithm_names.end())
    error = "multiple definitions for implementation `"
            + algorithm_information.name + "'";
  else if (path.empty())
    error = "missing path to executable for implementation `"
            + algorithm_information.name + "'";

  if (!error.empty())
    throw ConfigurationException
            (toString(block_begin_line)
	     + (config_file_line_number > block_begin_line
		? "-" + toString(config_file_line_number)
		: string("")),
	     error);

  vector<string> params;
  sliceString(unquoteString(substituteInQuotedString(parameters, " \t", "\n\n",
						     OUTSIDE_QUOTES)),
	      "\n",
	      params);

  /*
   *  Initialize the parameter array for the implementation.  This array is
   *  arranged into a standard argv-style array of C-style strings (ready to be
   *  used as a parameter for one of the exec functions) and has the following
   *  structure:
   *    Index                      Description
   *    0                      --  Path to the executable for invoking the
   *                               implementation (obtained from `path').
   *    1...params.size()      --  Optional parameters (obtained from the
   *                               `params' vector).
   *    params.size() + 1,     --  Reserved for storing the input and output
   *    params.size() + 2          file names given as the last two parameters
   *                               for the implementation.
   *    params.size() + 3      --  A 0 pointer terminating the parameter list.
   */

  algorithm_information.parameters = new char*[params.size() + 4];
  algorithm_information.num_parameters = params.size();

  algorithm_information.parameters[0] = new char[path.size() + 1];
  memcpy(static_cast<void*>(algorithm_information.parameters[0]),
	 static_cast<const void*>(path.c_str()), path.size() + 1);

  for (vector<string>::size_type p = 0;
       p < algorithm_information.num_parameters;
       ++p)
  {
    algorithm_information.parameters[p + 1] = new char[params[p].size() + 1];
    memcpy(static_cast<void*>(algorithm_information.parameters[p + 1]),
	   static_cast<const void*>(params[p].c_str()), params[p].size() + 1);
  }

  algorithm_information.parameters
    [algorithm_information.num_parameters + 3] = 0;

  algorithm_information.enabled = enabled;

  algorithm_names[algorithm_information.name] = algorithms.size();
  algorithms.push_back(algorithm_information);
}

/* ========================================================================= */
void Configuration::readProbability(double& target, const string& value)
/* ----------------------------------------------------------------------------
 *
 * Description:   Reads a probability and stores it into `target'.
 *
 * Arguments:     target  --  A reference to a double for storing the result.
 *                value   --  The probability as a string.
 *
 * Returns:       Nothing; the result is stored into `target'.  The function
 *                throws a ConfigurationException if `value' is not a valid
 *                probability (a number between 0.0 and 1.0).
 *
 * ------------------------------------------------------------------------- */
{
  char* endptr;
  string error;

  target = strtod(value.c_str(), &endptr);
  if (*endptr != '\0')
    error = "`" + value + "' is not a valid real number";
  else if (target < 0.0 || target > 1.0)
    error = "probability must be between 0.0 and 1.0 (inclusive)";

  if (!error.empty())
    throw ConfigurationException(config_file_line_number, error);
}

/* ========================================================================= */
void Configuration::readSize(int valtype, const string& value)
/* ----------------------------------------------------------------------------
 *
 * Description:   Initializes formula or state space size ranges from `value'.
 *
 * Arguments:     valtype  --  If == OPT_STATESPACESIZE, store the result in
 *                             `this->statespace_generator.min_size' and
 *                             `this->statespace_generator.max_size'; otherwise
 *                             store the result in
 *                             `this->formula_options.formula_generator.size'
 *                             and
 *                             `this->formula_options.formula_generator.
 *                                      max_size'.
 *                value    --  Size range as a string (a single integer or a
 *                             closed integer interval).
 *
 * Returns:       Nothing; the result is stored into the Configuration object.
 *                The function throws a ConfigurationException if `value' is
 *                not a valid positive integer or a closed nonempty integer
 *                interval.
 *
 * ------------------------------------------------------------------------- */
{
  string error;
  unsigned long int min, max;

  try
  {
    int interval_type = ::StringUtil::parseInterval(value, min, max);
    if (!(interval_type & ::StringUtil::LEFT_BOUNDED) ||
	!(interval_type & ::StringUtil::RIGHT_BOUNDED))
      throw Exception();

    if (min < 1)
    {
      if (valtype == OPT_STATESPACESIZE)
	error = "state space size must be positive";
      else
	error = "formula size must be positive";
    }
    else if (min > max)
    {
      if (valtype == OPT_STATESPACESIZE)
	error = "minimum state space size exceeds maximum state space size";
      else
	error = "minimum formula size exceeds maximum formula size";
    }
  }
  catch (const Exception&)
  {
    error = "`" + value + "' is neither a valid positive integer nor a closed "
            "integer interval";
  }

  if (!error.empty())
    throw ConfigurationException(config_file_line_number, error);

  if (valtype == OPT_STATESPACESIZE)
  {
    statespace_generator.min_size = min;
    statespace_generator.max_size = max;
  }
  else
  {
    formula_options.formula_generator.size = min;
    formula_options.formula_generator.max_size = max;
  }
}

/* ========================================================================= */
void Configuration::readTruthValue(bool& target, const string& value)
/* ----------------------------------------------------------------------------
 *
 * Description:   Interprets a symbolic truth value and stores it into
 *                `target'.
 *
 * Arguments:     target  --  A reference to a Boolean variable whose value
 *                            should be set according to the given value.
 *                value   --  The symbolic truth value.
 *
 * Returns:       Nothing; the interpreted value is stored in `target'.  If
 *                `value' is not a valid truth value, the function throws a
 *                ConfigurationException.
 *
 * ------------------------------------------------------------------------- */
{
  const string value_in_lower_case = ::StringUtil::toLowerCase(value);

  if (value_in_lower_case == "yes" || value_in_lower_case == "true")
    target = true;
  else if (value_in_lower_case == "no" || value_in_lower_case == "false")
    target = false;
  else
    throw ConfigurationException
            (config_file_line_number,
	     "`" + value + "' is not a valid truth value");
}

/* ========================================================================= */
void Configuration::readInteractivity(const string& value)
/* ----------------------------------------------------------------------------
 *
 * Description:   Interprets a symbolic list of interactivity modes and updates
 *                `this->global_options.interactive' and
 *                `this->global_options.handle_breaks' accordingly.
 *
 * Argument:      value  --  The symbolic mode (a comma-separated list of
 *                           "always", "onerror", "never" or "onbreak"; the
 *                           case is not relevant).
 *
 * Returns:       Nothing; the result is stored in
 *                `this->global_options.interactive' and/or
 *                `this->global_options.handle_breaks'.  The function throws a
 *                ConfigurationException is `value' is not a valid
 *                interactivity mode.
 *
 * ------------------------------------------------------------------------- */
{
  /*
   *  Reset the interactivity mode to NEVER and disable break handling to allow
   *  the interactivity specification to be interpreted correctly.
   */

  global_options.interactive = NEVER;
  global_options.handle_breaks = false;

  vector<string> modes;
  ::StringUtil::sliceString(value, ",", modes);
  for (vector<string>::const_iterator mode = modes.begin();
       mode != modes.end();
       ++mode)
  {
    string mode_in_lower_case = ::StringUtil::toLowerCase(*mode);

    if (mode_in_lower_case == "always")
      global_options.interactive = ALWAYS;
    else if (mode_in_lower_case == "onerror")
      global_options.interactive = ONERROR;
    else if (mode_in_lower_case == "never")
      global_options.interactive = NEVER;
    else if (mode_in_lower_case == "onbreak")
      global_options.handle_breaks = true;
    else
      throw ConfigurationException
	     (config_file_line_number,
	      "`" + *mode + "' is not a valid interactivity mode");
  }
}

/* ========================================================================= */
void Configuration::readProductType(const string& value)
/* ----------------------------------------------------------------------------
 *
 * Description:   Interprets a symbolic model checking mode and updates
 *                `this->global_options.product_mode' accordingly.
 *
 * Argument:      value  --  The symbolic mode (one of "local" or "global"; the
 *                           case of characters is not relevant).
 *
 * Returns:       Nothing; the result is stored in
 *                `this->global_options.product_mode'.  The function throws a
 *                ConfigurationException is `value' is not a valid model
 *                checking mode.
 *
 * ------------------------------------------------------------------------- */
{
  const string value_in_lower_case = ::StringUtil::toLowerCase(value);

  if (value_in_lower_case == "global")
    global_options.product_mode = GLOBAL;
  else if (value_in_lower_case == "local")
    global_options.product_mode = LOCAL;
  else
    throw ConfigurationException
	    (config_file_line_number,
	     "`" + value + "' is not a valid model checking mode");
}

/* ========================================================================= */
void Configuration::readFormulaMode(FormulaMode& target, const string& mode)
/* ----------------------------------------------------------------------------
 *
 * Description:   Interprets a symbolic formula mode and updates `target'
 *                accordingly.
 *
 * Argument:      mode  --  Symbolic formula mode (one of "normal" or "nnf";
 *                          the case of characters is not relevant).
 *
 * Returns:       Nothing; the result is stored into `target'.  The function
 *                throws a ConfigurationException if `mode' is not a valid mode
 *                string.
 *
 * ------------------------------------------------------------------------- */
{
  const string mode_in_lower_case = ::StringUtil::toLowerCase(mode);

  if (mode_in_lower_case == "nnf")
    target = NNF;
  else if (mode_in_lower_case == "normal")
    target = NORMAL;
  else
    throw ConfigurationException
	    (config_file_line_number,
	     "`" + mode + "' is not a valid formula mode");
}

/* ========================================================================= */
void Configuration::readStateSpaceMode(const string& mode)
/* ----------------------------------------------------------------------------
 *
 * Description:   Interprets a symbolic state space generation mode and updates
 *                `global_options.statespace_generation_mode' accordingly.
 *
 * Argument:      mode  --  Symbolic state space generation mode (one of
 *                          "randomconnectedgraph", "randomgraph", "randompath"
 *                          and "enumeratedpath"; the case of characters is
 *                          not relevant).
 *
 * Returns:       Nothing; the result is stored into
 *                `global_options.statespace_generation_mode'.  The function
 *                throws a ConfigurationException if `mode' is not one of the
 *                above keywords.
 *
 * ------------------------------------------------------------------------- */
{
  const string mode_in_lower_case = ::StringUtil::toLowerCase(mode);

  if (mode_in_lower_case == "randomconnectedgraph")
    global_options.statespace_generation_mode = RANDOMCONNECTEDGRAPH;
  else if (mode_in_lower_case == "randomgraph")
    global_options.statespace_generation_mode = RANDOMGRAPH;
  else if (mode_in_lower_case == "randompath")
    global_options.statespace_generation_mode = RANDOMPATH;
  else if (mode_in_lower_case == "enumeratedpath")
    global_options.statespace_generation_mode = ENUMERATEDPATH;
  else
    throw ConfigurationException
	    (config_file_line_number,
	     "`" + mode + "' is not a valid state space generation mode");
}

/* ========================================================================= */
void Configuration::readTranslatorTimeout(const string& value)
/* ----------------------------------------------------------------------------
 *
 * Description:   Reads a time specification from a string into
 *                `this->global_options.translator_timeout'.
 *
 * Argument:      value  --  A time specification in the format expected by
 *                           ::StringUtil::parseTime.
 *
 * Returns:       Nothing; the result is stored into
 *                `this->global_options.translator_timeout'.  The function
 *                throws a ConfigurationException if `value' is not a valid
 *                time specification.
 *
 * ------------------------------------------------------------------------- */
{ 
  unsigned long int hours, minutes, seconds;
  try
  {
    ::StringUtil::parseTime(value, hours, minutes, seconds);
  }
  catch (const Exception&)
  {
    throw ConfigurationException
            (config_file_line_number,
	     "`" + value + "' is not a valid time specification");
  }
  global_options.translator_timeout = (hours * 60 + minutes) * 60 + seconds;
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
