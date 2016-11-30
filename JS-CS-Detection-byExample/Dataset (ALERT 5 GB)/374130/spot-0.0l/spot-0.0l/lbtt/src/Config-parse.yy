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

%{

#include <config.h>
#include <cstdio>
#include <string>
#include "Configuration.h"
#include "StringUtil.h"



/******************************************************************************
 *
 * Variables for parsing the configuration file.
 *
 *****************************************************************************/

static Configuration::AlgorithmInformation          /* Stores all the      */
  algorithm_information;                            /* information in a
						     * single `Algorithm'
						     * block in the
						     * configuration file.
						     */

static int algorithm_begin_line;                    /* Input file line number
						     * denoting the beginning
						     * of the most recently
						     * encountered `Algorithm'
						     * block.
						     */

static int expected_token;                          /* Type of a token to be
						     * encountered next when
						     * reading the
						     * configuration file.
						     */

static int current_block_type;                      /* Type of the current
						     * configuration block.
						     */

static int current_option_type;                     /* Type of the current
						     * option name.
						     */

static Configuration* parser_cfg;                   /* Pointer to a
                                                     * Configuration data
                                                     * structure in which
                                                     * the configuration will
                                                     * be stored.
                                                     */

int config_file_line_number;                        /* Number of the current
                                                     * line in the
                                                     * configuration
                                                     * file.
                                                     */



/******************************************************************************
 *
 * Declarations for external functions and variables (provided by the lexer)
 * used when parsing the configuration file.
 *
 *****************************************************************************/

#ifdef YYTEXT_POINTER
extern char* yytext;                                /* Current token in the  */
#else	                                            /* input (provided by    */
extern char yytext[];                               /* the lexer).           */
#endif /* YYTEXT_POINTER */

extern void yyrestart(FILE*);                       /* Changes the input stream
                                                     * for the lexer (provided
                                                     * by the lexer).
                                                     */

extern int yylex();                                 /* Reads the next token
						     * from the input (this
						     * function is provided by
						     * the lexer).
						     */

extern int getCharacter();                          /* Returns the next
                                                     * character in the lexer
                                                     * input stream.
                                                     */



/******************************************************************************
 *
 * Function for reporting parse errors.
 *
 *****************************************************************************/

/* ========================================================================= */
void yyerror(char* error_message)
/* ----------------------------------------------------------------------------
 *
 * Description:   Function for reporting parse errors.
 *
 * Arguments:     error_message  --  An error message.
 *
 * Returns:       Nothing. Instead, throws a
 *                Configuration::ConfigurationException initialized with the
 *                given error message.
 *
 * ------------------------------------------------------------------------- */
{
  string unknown_token(yytext);
  int c;

  do
  {
    c = getCharacter();
    if (c != EOF && c != ' ' && c != '\t' && c != '\n')
      unknown_token += static_cast<char>(c);
  }
  while (c != EOF && c != ' ' && c != '\t' && c != '\n');

  string msg;

  switch (expected_token)
  {
    case CFG_BLOCK_ID :
      msg = string("unrecognized block identifier (`") + unknown_token + "')";
      break;
    case CFG_OPTION_ID :
      msg = string("unrecognized option identifier (`") + unknown_token + "')";
      break;
    case CFG_LBRACE :
      msg = "`{' expected after block identifier";
      break;
    case CFG_EQUALS :
      msg = "`=' expected after option identifier";
      break;
    case CFG_TRUTH_VALUE :
      msg = "truth value expected as option argument";
      break;
    case CFG_INTERACTIVITY_VALUE :
      msg = "interactivity mode expected as option argument";
      break;
    case CFG_FORMULA_MODE_VALUE :
      msg = "formula generation mode expected as option argument";
      break;
    case CFG_STATESPACE_MODE_VALUE :
      msg = "state space generation mode expected as option argument";
      break;
    case CFG_PRODUCT_TYPE_VALUE :
      msg = "model checking mode expected as option argument";
      break;
    case CFG_INTEGER :
      msg = "nonnegative integer expected as option argument";
      break;
    case CFG_INTEGER_INTERVAL :
      msg = "nonnegative integer or an integer interval expected as option"
	    " argument";
      break;
    case CFG_REAL :
      msg = "nonnegative real number expected as option argument";
      break;
    case CFG_STRING_CONSTANT :
      msg = "string constant expected as option argument";
      break;
    default :
      msg = error_message;
      break;
  }

  throw Configuration::ConfigurationException(config_file_line_number, msg);
}



/******************************************************************************
 *
 * Functions for performing various bound checks for the values of different
 * options in the configuration file.
 *
 *****************************************************************************/

/* ========================================================================= */
void checkIntegerRange
  (long int value, const struct Configuration::IntegerRange& range,
   bool show_line_number_if_error = true)
/* ----------------------------------------------------------------------------
 *
 * Description:   Checks that a value given to a configuration is within the
 *                acceptable range.
 *
 * Arguments:     value                      --  Integer value for a
 *                                               configuration option.
 *                range                      --  A reference to a constant
 *                                               struct
 *                                               Configuration::IntegerRange.
 *                show_line_number_if_error  --  If the value is not within the
 *                                               specified range, this
 *                                               parameter determines whether a
 *                                               configuration file line number
 *                                               is shown together with the
 *                                               error message.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (value < range.min || value > range.max)
    throw Configuration::ConfigurationException
            ((show_line_number_if_error ? config_file_line_number : -1),
	     range.error_message);
}

/* ========================================================================= */
void checkProbability(double value, bool show_line_number_if_error = true)
/* ----------------------------------------------------------------------------
 *
 * Description:   Checks whether a probability value specified in the program
 *                configuration is between 0.0 and 1.0 inclusive.
 *
 * Argument:      value                      --  A value supposed to denote a
 *                                               probability.
 *                show_line_number_if_error  --  If the value is not within the
 *                                               specified range, this
 *                                               parameter determines whether a
 *                                               configuration file line number
 *                                               is shown together with the
 *                                               error message.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (value < 0.0 || value > 1.0)
    throw Configuration::ConfigurationException
            ((show_line_number_if_error ? config_file_line_number : -1),
	     "probability must be between 0.0 and 1.0 inclusive");
}

/* ========================================================================= */
static inline bool isLocked(int option)
/* ----------------------------------------------------------------------------
 *
 * Description:   Checks whether the value of a configuration option can be
 *                initialized from the configuration file.  (This should not be
 *                done if the option was present on the program command line.)
 *
 * Argument:      option  --  The command line option.
 *
 * Returns:       `true' if the value of the option has been overridden in the
 *                command line.
 *
 * ------------------------------------------------------------------------- */
{
  return (parser_cfg->locked_options.find(make_pair(current_block_type,
                                                    option))
	    != parser_cfg->locked_options.end());
}

%}


/******************************************************************************
 *
 * Declarations for terminal and nonterminal symbols used in the grammar rules
 * below.
 *
 *****************************************************************************/

/* Data types for configuration file option values. */

%union
{
  bool truth_value;

  long int integer;

  Configuration::InteractionMode interactivity_value;

  Configuration::FormulaMode formula_mode_value;

  Configuration::StateSpaceMode statespace_mode_value;

  Configuration::ProductMode product_type_value;

  double real;

  string *str;

  struct
  {
    long int min;
    long int max;
  } integer_interval;
}

/* Keywords. */

%token CFG_ALGORITHM CFG_ENABLED CFG_NAME CFG_PARAMETERS CFG_PROGRAMPATH

%token CFG_GLOBALOPTIONS CFG_COMPARISONTEST CFG_CONSISTENCYTEST
       CFG_INTERACTIVE CFG_INTERSECTIONTEST CFG_MODELCHECK CFG_ROUNDS
       CFG_VERBOSITY

%token CFG_STATESPACEOPTIONS CFG_EDGEPROBABILITY CFG_PROPOSITIONS CFG_SIZE
       CFG_TRUTHPROBABILITY CFG_CHANGEINTERVAL CFG_RANDOMSEED

%token CFG_FORMULAOPTIONS CFG_ABBREVIATEDOPERATORS CFG_ANDPRIORITY
       CFG_BEFOREPRIORITY CFG_DEFAULTOPERATORPRIORITY CFG_EQUIVALENCEPRIORITY
       CFG_FALSEPRIORITY CFG_FINALLYPRIORITY CFG_GENERATEMODE
       CFG_GLOBALLYPRIORITY CFG_IMPLICATIONPRIORITY CFG_NEXTPRIORITY
       CFG_NOTPRIORITY CFG_ORPRIORITY CFG_OUTPUTMODE CFG_PROPOSITIONPRIORITY
       CFG_RELEASEPRIORITY CFG_STRONGRELEASEPRIORITY CFG_TRUEPRIORITY
       CFG_UNTILPRIORITY CFG_WEAKUNTILPRIORITY CFG_XORPRIORITY

/* Boolean constants. */

%token <truth_value> CFG_TRUTH_VALUE

/* Interactivity mode constants. */

%token <interactivity_value> CFG_INTERACTIVITY_VALUE

/* Formula output/generation mode constants. */

%token <formula_mode_value> CFG_FORMULA_MODE_VALUE

/* Statespace generation mode constants. */

%token <statespace_mode_value> CFG_STATESPACE_MODE_VALUE

/* Constants controlling the product space computation. */

%token <product_type_value> CFG_PRODUCT_TYPE_VALUE

/* Numbers. */

%token <integer> CFG_INTEGER
%token <real> CFG_REAL

/* Intervals of integers. */

%token <integer_interval> CFG_INTEGER_INTERVAL

/* String constants. */

%token <str> CFG_STRING_CONSTANT

/* Punctuation symbols. */

%token CFG_LBRACE CFG_RBRACE CFG_EQUALS
%token CFG_BLOCK_ID CFG_OPTION_ID

/* The `unknown' token. */

%token CFG_UNKNOWN



/******************************************************************************
 *
 * Grammar rule definitions.
 *
 *****************************************************************************/

%%

configuration_file:      configuration_blocks
                         ;

configuration_blocks:      /* empty */
                         | configuration_blocks
                             {
                               expected_token = CFG_BLOCK_ID;
                             }
                           configuration_block
                         ;

configuration_block:       algorithm_option_block
                         | global_option_block
                         | statespace_option_block
                         | formula_option_block
                         ;

algorithm_option_block:    CFG_ALGORITHM
                             {
			       current_block_type = CFG_ALGORITHM;

                               algorithm_begin_line = config_file_line_number;

                               algorithm_information.name = 0;
                               algorithm_information.path_to_program = 0;
                               algorithm_information.extra_parameters = 0;
			       algorithm_information.enabled = true;

                               expected_token = CFG_LBRACE;
                             }
                           CFG_LBRACE algorithm_options CFG_RBRACE
                             {
                               if (algorithm_information.name == 0)
                               {
                                 algorithm_information.name =
                                   new string("Algorithm ");
				 algorithm_information.name->append
				   (::StringUtil::toString
				    (parser_cfg->algorithms.size()));
                               }
                               parser_cfg->algorithms.push_back
                                 (algorithm_information);
                               if (algorithm_information.path_to_program == 0)
                               {
				 throw Configuration::ConfigurationException
				         (::StringUtil::toString
					    (algorithm_begin_line)
					    + "--"
					    + ::StringUtil::toString
					        (config_file_line_number),
					  "missing path to executable (`"
					  + *(algorithm_information.name)
                                          + "')");
                               }
                             }
                         ;

algorithm_options:         /* empty */
                         | algorithm_options
                             {
                               expected_token = CFG_OPTION_ID;
                             }
                           algorithm_option
                         ;

algorithm_option:          CFG_ENABLED
                             {
			       current_option_type = CFG_ENABLED;
			       expected_token = CFG_EQUALS;
			     }
                           CFG_EQUALS
                             {
			       expected_token = CFG_TRUTH_VALUE;
			     }
                           CFG_TRUTH_VALUE
                             {
			       algorithm_information.enabled = $5;
			     }

                         | CFG_NAME
                             {
			       current_option_type = CFG_NAME;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_STRING_CONSTANT;
                             }
                           CFG_STRING_CONSTANT
                             {
                               algorithm_information.name = $5;
                             }

                         | CFG_PARAMETERS
                             {
			       current_option_type = CFG_PARAMETERS;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_STRING_CONSTANT;
                             }
                           CFG_STRING_CONSTANT
                             {
                               algorithm_information.extra_parameters = $5;
                             }

                         | CFG_PROGRAMPATH
                             {
			       current_option_type = CFG_PROGRAMPATH;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_STRING_CONSTANT;
                             }
                           CFG_STRING_CONSTANT
                             {
                               algorithm_information.path_to_program = $5;
                             }
                         ;

global_option_block:       CFG_GLOBALOPTIONS
                             {
			       current_block_type = CFG_GLOBALOPTIONS;
                               expected_token = CFG_LBRACE;
                             }
                           CFG_LBRACE global_options CFG_RBRACE
                         ;

global_options:            /* empty */
                         | global_options
                             {
                               expected_token = CFG_OPTION_ID;
                             }
                           global_option
                         ;

global_option:             CFG_COMPARISONTEST
                             {
			       current_option_type = CFG_COMPARISONTEST;
			       expected_token = CFG_EQUALS;
			     }
                           CFG_EQUALS
                             {
			       expected_token = CFG_TRUTH_VALUE;
			     }
                           CFG_TRUTH_VALUE
                             {
			       if (!isLocked(CFG_COMPARISONTEST))
				 parser_cfg->global_options.do_comp_test = $5;
			     }

                         | CFG_CONSISTENCYTEST
                             {
			       current_option_type = CFG_CONSISTENCYTEST;
			       expected_token = CFG_EQUALS;
			     }
                           CFG_EQUALS
                             {
			       expected_token = CFG_TRUTH_VALUE;
			     }
                           CFG_TRUTH_VALUE
                             {
			       if (!isLocked(CFG_CONSISTENCYTEST))
				 parser_cfg->global_options.do_cons_test = $5;
			     }

                         | CFG_INTERACTIVE
                             {
			       current_option_type = CFG_INTERACTIVE;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTERACTIVITY_VALUE;
                             }
                           CFG_INTERACTIVITY_VALUE
                             {
			       if (!isLocked(CFG_INTERACTIVE))
				 parser_cfg->global_options.interactive = $5;
                             }

                         | CFG_INTERSECTIONTEST
                             {
			       current_option_type = CFG_INTERSECTIONTEST;
			       expected_token = CFG_EQUALS;
			     }
                           CFG_EQUALS
                             {
			       expected_token = CFG_TRUTH_VALUE;
			     }
                           CFG_TRUTH_VALUE
                             {
			       if (!isLocked(CFG_INTERSECTIONTEST))
				 parser_cfg->global_options.do_intr_test = $5;
			     }

                         | CFG_MODELCHECK
                             {
			       current_option_type = CFG_MODELCHECK;
			       expected_token = CFG_EQUALS;
			     }
                           CFG_EQUALS
                             {
			       expected_token = CFG_PRODUCT_TYPE_VALUE;
			     }
                           CFG_PRODUCT_TYPE_VALUE
                             {
			       if (!isLocked(CFG_MODELCHECK))
				 parser_cfg->global_options.product_mode = $5;
			     }

                         | CFG_ROUNDS
                             {
			       current_option_type = CFG_ROUNDS;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_ROUNDS))
			       {
				 checkIntegerRange
	                           ($5, Configuration::ROUND_COUNT_RANGE,
                                    true);
				 parser_cfg->global_options.number_of_rounds
				   = $5;
			       }
                             }

                         | CFG_VERBOSITY
                             {
			       current_option_type = CFG_VERBOSITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_VERBOSITY))
			       {
				 checkIntegerRange
                                   ($5, Configuration::VERBOSITY_RANGE, true);
				 parser_cfg->global_options.verbosity = $5;
			       }
                             }
                         ;

statespace_option_block:   CFG_STATESPACEOPTIONS
                             {
			       current_block_type = CFG_STATESPACEOPTIONS;
                               expected_token = CFG_LBRACE;
                             }
                           CFG_LBRACE statespace_options CFG_RBRACE
                         ;

statespace_options:        /* empty */
                         | statespace_options
                             {
                               expected_token = CFG_OPTION_ID;
                             }
                           statespace_option
                         ;

statespace_option:         CFG_CHANGEINTERVAL
                             {
			       current_option_type = CFG_CHANGEINTERVAL;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_CHANGEINTERVAL))
			       {
				 checkIntegerRange
                                   ($5, Configuration::GENERATION_RANGE, true);
				 parser_cfg->global_options.
				   statespace_change_interval = $5;
			       }
                             }

                         | CFG_EDGEPROBABILITY
                             {
			       current_option_type = CFG_EDGEPROBABILITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_REAL;
                             }
                           CFG_REAL
                             {
			       if (!isLocked(CFG_EDGEPROBABILITY))
			       {
				 checkProbability($5);
				 parser_cfg->statespace_generator.
	                           edge_probability = $5;
			       }
                             }

                         | CFG_GENERATEMODE
                             {
			       current_option_type = CFG_GENERATEMODE;
			       expected_token = CFG_EQUALS;
			     }
                           CFG_EQUALS
                             {
			       expected_token = CFG_STATESPACE_MODE_VALUE;
			     }
                           CFG_STATESPACE_MODE_VALUE
                             {
			       if (!isLocked(CFG_GENERATEMODE))
				 parser_cfg->global_options.
	                           statespace_generation_mode = $5;
			     }

                         | CFG_PROPOSITIONS
                             {
			       current_option_type = CFG_PROPOSITIONS;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_PROPOSITIONS))
			       {
				 checkIntegerRange
                                   ($5, Configuration::PROPOSITION_COUNT_RANGE,
                                    true);
				 parser_cfg->statespace_generator.
	                           atoms_per_state = $5;
			       }
                             }

                         | CFG_RANDOMSEED
                             {
			       current_option_type = CFG_RANDOMSEED;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_RANDOMSEED))
				 parser_cfg->global_options.
                                   statespace_random_seed = $5;
                             }

                         | CFG_SIZE
                             {
			       current_option_type = CFG_SIZE;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER_INTERVAL;
                             }
                           statespace_size

                         | CFG_TRUTHPROBABILITY
                             {
			       current_option_type = CFG_TRUTHPROBABILITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_REAL;
                             }
                           CFG_REAL
                             {
			       if (!isLocked(CFG_TRUTHPROBABILITY))
			       {
				 checkProbability($5);
				 parser_cfg->statespace_generator.
                                   truth_probability = $5;
			       }
                             }
                         ;

formula_option_block:      CFG_FORMULAOPTIONS
                             {
			       current_block_type = CFG_FORMULAOPTIONS;
                               expected_token = CFG_LBRACE;
                             }
                           CFG_LBRACE formula_options CFG_RBRACE
                         ;

formula_options:           /* empty */
                         | formula_options
                             {
                               expected_token = CFG_OPTION_ID;
                             }
                           formula_option
                         ;

formula_option:            CFG_ABBREVIATEDOPERATORS
                             {
			       current_option_type = CFG_ABBREVIATEDOPERATORS;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_TRUTH_VALUE;
                             }
                           CFG_TRUTH_VALUE
                             {
			       if (!isLocked(CFG_ABBREVIATEDOPERATORS))
				 parser_cfg->formula_options.
				   allow_abbreviated_operators = $5;
                             }

                         | CFG_ANDPRIORITY
                             {
			       current_option_type = CFG_ANDPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_ANDPRIORITY))
			       {
				 checkIntegerRange
                                   ($5, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
	                           [::Ltl::LTL_CONJUNCTION] = $5;
			       }
                             }

                         | CFG_BEFOREPRIORITY
                             {
			       current_option_type = CFG_BEFOREPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_BEFOREPRIORITY))
			       {
				 checkIntegerRange
                                   ($5, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
	                           [::Ltl::LTL_BEFORE] = $5;
			       }
                             }

                         | CFG_CHANGEINTERVAL
                             {
			       current_option_type = CFG_CHANGEINTERVAL;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_CHANGEINTERVAL))
			       {
				 checkIntegerRange
	                           ($5, Configuration::GENERATION_RANGE, true);
				 parser_cfg->global_options.
                                   formula_change_interval = $5;
			       }
                             }

                         | CFG_DEFAULTOPERATORPRIORITY
                             {
			       current_option_type
	                         = CFG_DEFAULTOPERATORPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_DEFAULTOPERATORPRIORITY))
			       {
				 checkIntegerRange
                                   ($5, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.
	                           default_operator_priority = $5;
			       }
                             }

                         | CFG_EQUIVALENCEPRIORITY
                             {
			       current_option_type = CFG_EQUIVALENCEPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_EQUIVALENCEPRIORITY))
			       {
				 checkIntegerRange
                                   ($5, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
	                           [::Ltl::LTL_EQUIVALENCE] = $5;
			       }
                             }

                         | CFG_FALSEPRIORITY
                             {
			       current_option_type = CFG_FALSEPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_FALSEPRIORITY))
			       {
				 checkIntegerRange
                                   ($5, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_FALSE] = $5;
			       }
                             }

                         | CFG_FINALLYPRIORITY
                             {
			       current_option_type = CFG_FINALLYPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_FINALLYPRIORITY))
			       {
				 checkIntegerRange
                                   ($5, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_FINALLY] = $5;
			       }
                             }

                         | CFG_GENERATEMODE
                             {
			       current_option_type = CFG_GENERATEMODE;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_FORMULA_MODE_VALUE;
                             }
                           CFG_FORMULA_MODE_VALUE
                             {
			       if (!isLocked(CFG_GENERATEMODE))
				 parser_cfg->formula_options.generate_mode
				   = $5;
                             }

                         | CFG_GLOBALLYPRIORITY
                             {
			       current_option_type = CFG_GLOBALLYPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_GLOBALLYPRIORITY))
			       {
				 checkIntegerRange
                                   ($5, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_GLOBALLY] = $5;
			       }
                             }

                         | CFG_IMPLICATIONPRIORITY
                             {
			       current_option_type = CFG_IMPLICATIONPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_IMPLICATIONPRIORITY))
			       {
				 checkIntegerRange
                                   ($5, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_IMPLICATION] = $5;
			       }
                             }

                         | CFG_NEXTPRIORITY
                             {
			       current_option_type = CFG_NEXTPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_NEXTPRIORITY))
			       {
				 checkIntegerRange
                                   ($5, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_NEXT] = $5;
			       }
                             }

                         | CFG_NOTPRIORITY
                             {
			       current_option_type = CFG_NOTPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_NOTPRIORITY))
			       {
				 checkIntegerRange
                                   ($5, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_NEGATION] = $5;
			       }
                             }

                         | CFG_ORPRIORITY
                             {
			       current_option_type = CFG_ORPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_ORPRIORITY))
			       {
				 checkIntegerRange
                                   ($5, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_DISJUNCTION] = $5;
			       }
                             }

                         | CFG_OUTPUTMODE
                             {
			       current_option_type = CFG_OUTPUTMODE;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_FORMULA_MODE_VALUE;
                             }
                           CFG_FORMULA_MODE_VALUE
                             {
			       if (!isLocked(CFG_OUTPUTMODE))
				 parser_cfg->formula_options.output_mode = $5;
                             }

                         | CFG_PROPOSITIONPRIORITY
                             {
			       current_option_type = CFG_PROPOSITIONPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_PROPOSITIONPRIORITY))
			       {
				 checkIntegerRange
                                   ($5, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_ATOM] = $5;
			       }
                             }

                         | CFG_PROPOSITIONS
                             {
			       current_option_type = CFG_PROPOSITIONS;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_PROPOSITIONS))
			       {
				 checkIntegerRange
                                   ($5, Configuration::PROPOSITION_COUNT_RANGE,
                                    true);
				 parser_cfg->formula_options.
                                   formula_generator.
				   number_of_available_variables = $5;
			       }
                             }

                         | CFG_RANDOMSEED
                             {
			       current_option_type = CFG_RANDOMSEED;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_RANDOMSEED))
				 parser_cfg->global_options.formula_random_seed
                                   = $5;
                             }

                         | CFG_RELEASEPRIORITY
                             {
			       current_option_type = CFG_RELEASEPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_RELEASEPRIORITY))
			       {
				 checkIntegerRange
                                   ($5, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_V] = $5;
			       }
                             }

                         | CFG_SIZE
                             {
			       current_option_type = CFG_SIZE;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER_INTERVAL;
                             }
                           formula_size

                         | CFG_STRONGRELEASEPRIORITY
                             {
			       current_option_type = CFG_STRONGRELEASEPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_STRONGRELEASEPRIORITY))
			       {
				 checkIntegerRange
                                   ($5, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_STRONG_RELEASE] = $5;
			       }
                             }

                         | CFG_TRUEPRIORITY
                             {
			       current_option_type = CFG_TRUEPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_TRUEPRIORITY))
			       {
				 checkIntegerRange
                                   ($5, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_TRUE] = $5;
			       }
                             }

                         | CFG_UNTILPRIORITY
                             {
			       current_option_type = CFG_UNTILPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_UNTILPRIORITY))
			       {
				 checkIntegerRange
                                   ($5, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_UNTIL] = $5;
			       }
                             }

	                 | CFG_WEAKUNTILPRIORITY
                             {
			       current_option_type = CFG_WEAKUNTILPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_WEAKUNTILPRIORITY))
			       {
				 checkIntegerRange
                                   ($5, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_WEAK_UNTIL] = $5;
			       }
                             }

	                 | CFG_XORPRIORITY
                             {
			       current_option_type = CFG_XORPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
                           CFG_EQUALS
                             {
                               expected_token = CFG_INTEGER;
                             }
                           CFG_INTEGER
                             {
			       if (!isLocked(CFG_XORPRIORITY))
			       {
				 checkIntegerRange
                                   ($5, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_XOR] = $5;
			       }
                             }
                         ;

formula_size:              CFG_INTEGER
                             {
			       if (!isLocked(CFG_SIZE))
			       {
				 checkIntegerRange
                                   ($1, Configuration::FORMULA_SIZE_RANGE,
                                    true);
				 parser_cfg->formula_options.formula_generator.
                                   size = $1;
				 parser_cfg->formula_options.formula_generator.
				   max_size = $1;
			       }
                             }

                         | CFG_INTEGER_INTERVAL
                             {
			       if (!isLocked(CFG_SIZE))
			       {
				 checkIntegerRange
                                   ($1.min, Configuration::FORMULA_SIZE_RANGE,
				    true);

				 Configuration::IntegerRange max_len_range
	                           (Configuration::FORMULA_MAX_SIZE_RANGE);

				 max_len_range.min = $1.min;

				 checkIntegerRange($1.max, max_len_range,
						   true);

				 parser_cfg->formula_options.formula_generator.
				   size = $1.min;
				 parser_cfg->formula_options.formula_generator.
				   max_size = $1.max;
			       }
			     }
                         ;

statespace_size:           CFG_INTEGER
                             {
			       if (!isLocked(CFG_SIZE))
			       {
				 checkIntegerRange
                                   ($1, Configuration::STATESPACE_SIZE_RANGE,
				    true);
				 parser_cfg->statespace_generator.min_size
                                   = $1;
				 parser_cfg->statespace_generator.max_size
                                   = $1;
			       }
                             }

                         | CFG_INTEGER_INTERVAL
                             {
			       if (!isLocked(CFG_SIZE))
			       {
				 checkIntegerRange
                                   ($1.min,
                                    Configuration::STATESPACE_SIZE_RANGE,
                                    true);

				 Configuration::IntegerRange max_size_range
                                   (Configuration::STATESPACE_MAX_SIZE_RANGE);

				 checkIntegerRange($1.max, max_size_range,
						   true);

				 parser_cfg->statespace_generator.min_size
                                   = $1.min;
				 parser_cfg->statespace_generator.max_size
                                   = $1.max;
			       }
			     }
                         ;

%%



/******************************************************************************
 *
 * Main interface to the parser.
 *
 *****************************************************************************/

/* ========================================================================= */
int parseConfiguration(FILE* stream, Configuration& configuration)
/* ----------------------------------------------------------------------------
 *
 * Description:   Main interface to the configuration file parser. Parses the
 *                configuration file and stores the results into the given
 *                Configuration object.
 *
 * Arguments:     stream         --  A pointer to a file from which the
 *                                   configuration should be read.  The file is
 *                                   assumed to be open for reading.
 *                configuration  --  A reference to a Configuration object in
 *                                   which the configuration should be stored.
 *
 * Returns:       
 *
 * ------------------------------------------------------------------------- */
{
  yyrestart(stream);
  parser_cfg = &configuration;
  config_file_line_number = 1;
  return yyparse();
}
