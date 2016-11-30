/*
 *  Copyright (C) 1999, 2000, 2001, 2002, 2003, 2004, 2005, 2009
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

%{

#include <config.h>
#include <string>
#include "Configuration.h"
#include "StringUtil.h"

using namespace ::StringUtil;




/******************************************************************************
 *
 * Variables for parsing the configuration file.
 *
 *****************************************************************************/

static string algorithm_name, algorithm_path,       /* Implementation       */
              algorithm_parameters;                 /* attributes read from */
static bool algorithm_enabled;                      /* an `Algorithm' block
                                                     * in the configuration
                                                     * file.
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

void yyerror(const char* error_message);    /* Fwd. definition.  See below. */

%}


/******************************************************************************
 *
 * Declarations for terminal and nonterminal symbols used in the grammar rules
 * below.
 *
 *****************************************************************************/

/* Data type for configuration file option values. */

%union
{
  const char* value;
}

/* Keywords. */

%token CFG_ALGORITHM CFG_ENABLED CFG_NAME CFG_PARAMETERS CFG_PROGRAMPATH

%token CFG_GLOBALOPTIONS CFG_COMPARISONTEST CFG_CONSISTENCYTEST CFG_INTERACTIVE
       CFG_INTERSECTIONTEST CFG_MODELCHECK CFG_ROUNDS CFG_TRANSLATORTIMEOUT
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

/* Punctuation symbols. */

%token CFG_LBRACE CFG_RBRACE CFG_EQUALS

/* Block and option identifiers. */

%token CFG_BLOCK_ID CFG_OPTION_ID

/* Uninterpreted option values. */

%token <value> CFG_VALUE
%type <value> equals_value

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

equals_value:                { expected_token = CFG_EQUALS; }
                           CFG_EQUALS
                             { expected_token = CFG_VALUE; }
                           CFG_VALUE
                             { $$ = $4; }
                         ;

configuration_blocks:      /* empty */
                         | configuration_blocks
                             { expected_token = CFG_BLOCK_ID; }
                           configuration_block
                         ;

configuration_block:       algorithm_option_block
                         | global_option_block
                         | statespace_option_block
                         | formula_option_block
                         ;

algorithm_option_block:    CFG_ALGORITHM
                             {
                               algorithm_name = "";
                               algorithm_path = "";
                               algorithm_parameters = "";
                               algorithm_enabled = true;
                               algorithm_begin_line = config_file_line_number;
                               expected_token = CFG_LBRACE;
                             }
                           CFG_LBRACE algorithm_options CFG_RBRACE
                             {
                               parser_cfg->registerAlgorithm
                                 (algorithm_name, algorithm_path,
                                  algorithm_parameters, algorithm_enabled,
                                  algorithm_begin_line);
                             }
                         ;

algorithm_options:         /* empty */
                         | algorithm_options
                             { expected_token = CFG_OPTION_ID; }
                           algorithm_option
                         ;

algorithm_option:          CFG_ENABLED equals_value
                             {
                               parser_cfg->readTruthValue
                                             (algorithm_enabled,
                                              $2);
                             }

                         | CFG_NAME equals_value
                             { algorithm_name = unquoteString($2); }

                         | CFG_PARAMETERS equals_value
                             {
                               algorithm_parameters = unquoteString($2);
                             }

                         | CFG_PROGRAMPATH equals_value
                             { algorithm_path = unquoteString($2); }
                         ;

global_option_block:       CFG_GLOBALOPTIONS
                             { expected_token = CFG_LBRACE; }
                           CFG_LBRACE global_options CFG_RBRACE
                         ;

global_options:            /* empty */
                         | global_options
                             { expected_token = CFG_OPTION_ID; }
                           global_option
                         ;

global_option:             CFG_COMPARISONTEST equals_value
                             {
                               parser_cfg->readTruthValue
                                             (parser_cfg->global_options.
                                                do_comp_test,
                                              $2);
                             }

                         | CFG_CONSISTENCYTEST equals_value
                             {
                               parser_cfg->readTruthValue
                                             (parser_cfg->global_options.
                                                do_cons_test,
                                              $2);
			     }

                         | CFG_INTERACTIVE equals_value
                             { parser_cfg->readInteractivity($2); }

                         | CFG_INTERSECTIONTEST equals_value
                             {
                               parser_cfg->readTruthValue
                                             (parser_cfg->global_options.
                                                do_intr_test,
                                              $2);
                             }

                         | CFG_MODELCHECK equals_value
                             { parser_cfg->readProductType($2); }

                         | CFG_ROUNDS equals_value
                             {
                               parser_cfg->readInteger
                                 (parser_cfg->global_options.number_of_rounds,
                                  $2,
                                  Configuration::ROUND_COUNT_RANGE);
                             }

                         | CFG_TRANSLATORTIMEOUT equals_value
                             { parser_cfg->readTranslatorTimeout($2); }

                         | CFG_VERBOSITY equals_value
                             {
                               parser_cfg->readInteger
                                 (parser_cfg->global_options.verbosity,
                                  $2,
                                  Configuration::VERBOSITY_RANGE);
                             }
                         ;

statespace_option_block:   CFG_STATESPACEOPTIONS
                             { expected_token = CFG_LBRACE; }
                           CFG_LBRACE statespace_options CFG_RBRACE
                         ;

statespace_options:        /* empty */
                         | statespace_options
                             { expected_token = CFG_OPTION_ID; }
                           statespace_option
                         ;

statespace_option:         CFG_CHANGEINTERVAL equals_value
                             {
                               parser_cfg->readInteger
                                             (parser_cfg->global_options.
                                                statespace_change_interval,
                                              $2);
                             }

                         | CFG_EDGEPROBABILITY equals_value
                             {
                               parser_cfg->readProbability
                                             (parser_cfg->statespace_generator.
                                                edge_probability,
                                              $2);
                             }

                         | CFG_GENERATEMODE equals_value
                             { parser_cfg->readStateSpaceMode($2); }

                         | CFG_PROPOSITIONS equals_value
                             {
                               parser_cfg->readInteger
                                             (parser_cfg->statespace_generator.
                                                atoms_per_state,
                                              $2);
                             }

                         | CFG_RANDOMSEED equals_value
                             {
                               parser_cfg->readInteger
                                 (parser_cfg->global_options.
                                                statespace_random_seed,
                                  $2,
                                  Configuration::RANDOM_SEED_RANGE);
                             }

                         | CFG_SIZE equals_value
                             {
                               parser_cfg->readSize
                                 (Configuration::OPT_STATESPACESIZE,
                                  $2);
                             }

                         | CFG_TRUTHPROBABILITY equals_value
                             {
                               parser_cfg->readProbability
                                             (parser_cfg->statespace_generator.
                                                truth_probability,
                                              $2);
                             }
                         ;

formula_option_block:      CFG_FORMULAOPTIONS
                             { expected_token = CFG_LBRACE; }
                           CFG_LBRACE formula_options CFG_RBRACE
                         ;

formula_options:           /* empty */
                         | formula_options
                             { expected_token = CFG_OPTION_ID; }
                           formula_option
                         ;

formula_option:            CFG_ABBREVIATEDOPERATORS equals_value
                             {
                               parser_cfg->readTruthValue
                                             (parser_cfg->formula_options.
                                                allow_abbreviated_operators,
                                              $2);
                             }

                         | CFG_ANDPRIORITY equals_value
			     {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_CONJUNCTION],
                                  $2,
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }

                         | CFG_BEFOREPRIORITY equals_value
			     {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_BEFORE],
                                  $2,
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }

                         | CFG_CHANGEINTERVAL equals_value
                             {
                               parser_cfg->readInteger
				             (parser_cfg->global_options.
                                                formula_change_interval,
                                              $2);
                             }

                         | CFG_DEFAULTOPERATORPRIORITY equals_value
			     {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.
                                    default_operator_priority,
                                  $2,
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }

                         | CFG_EQUIVALENCEPRIORITY equals_value
			     {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_EQUIVALENCE],
                                  $2,
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }

                         | CFG_FALSEPRIORITY equals_value
			     {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_FALSE],
                                  $2,
                                  Configuration::ATOMIC_PRIORITY_RANGE);
                             }

                         | CFG_FINALLYPRIORITY equals_value
			     {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_FINALLY],
                                  $2,
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }

                         | CFG_GENERATEMODE equals_value
                             {
                               parser_cfg->readFormulaMode
                                 (parser_cfg->formula_options.generate_mode,
                                  $2);
                             }

                         | CFG_GLOBALLYPRIORITY equals_value
			     {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_GLOBALLY],
                                  $2,
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }

                         | CFG_IMPLICATIONPRIORITY equals_value
			     {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_IMPLICATION],
                                  $2,
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }

                         | CFG_NEXTPRIORITY equals_value
			     {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_NEXT],
                                  $2,
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }

                         | CFG_NOTPRIORITY equals_value
			     {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_NEGATION],
                                  $2,
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }

                         | CFG_ORPRIORITY equals_value
			     {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_DISJUNCTION],
                                  $2,
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }

                         | CFG_OUTPUTMODE equals_value
                             {
                               parser_cfg->readFormulaMode
                                 (parser_cfg->formula_options.output_mode,
                                  $2);
                             }

                         | CFG_PROPOSITIONPRIORITY equals_value
			     {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_ATOM],
                                  $2,
                                  Configuration::ATOMIC_PRIORITY_RANGE);
                             }

                         | CFG_PROPOSITIONS equals_value
                             {
                               parser_cfg->readInteger
                                             (parser_cfg->formula_options.
                                                formula_generator.
                                                number_of_available_variables,
                                              $2);
                             }

                         | CFG_RANDOMSEED equals_value
                             {
                               parser_cfg->readInteger
                                 (parser_cfg->global_options.
                                                formula_random_seed,
                                  $2,
                                  Configuration::RANDOM_SEED_RANGE);
                             }

                         | CFG_RELEASEPRIORITY equals_value
			     {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_V],
                                  $2,
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }

                         | CFG_SIZE equals_value
                             {
                               parser_cfg->readSize
                                             (Configuration::OPT_FORMULASIZE,
                                              $2);
                             }

                         | CFG_STRONGRELEASEPRIORITY equals_value
			     {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_STRONG_RELEASE],
                                  $2,
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }

                         | CFG_TRUEPRIORITY equals_value
			     {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_TRUE],
                                  $2,
                                  Configuration::ATOMIC_PRIORITY_RANGE);
                             }

                         | CFG_UNTILPRIORITY equals_value
			     {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_UNTIL],
                                  $2,
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }

	                 | CFG_WEAKUNTILPRIORITY equals_value
			     {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_WEAK_UNTIL],
                                  $2,
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }

	                 | CFG_XORPRIORITY equals_value
			     {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_XOR],
                                  $2,
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
                         ;

%%



/******************************************************************************
 *
 * Function for reporting parse errors.
 *
 *****************************************************************************/

/* ========================================================================= */
void yyerror(const char* error_message)
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
  const string unknown_token(yytext);
  string msg;

  switch (expected_token)
  {
    case CFG_BLOCK_ID :
      msg = "`" + unknown_token + "' is not a valid block identifier";
      break;
    case CFG_OPTION_ID :
      if (!unknown_token.empty())
	msg = "`" + unknown_token + "' is not a valid option identifier";
      else
	msg = "'}' expected at the end of block";
      break;
    case CFG_LBRACE :
      msg = "`{' expected after block identifier";
      break;
    case CFG_EQUALS :
      msg = "`=' expected after option identifier";
      break;
    case CFG_VALUE :
      msg = "value for option expected";
      break;
    default :
      msg = error_message;
      break;
  }

  throw Configuration::ConfigurationException(config_file_line_number, msg);
}

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
 * Returns:       The result of yyparse() on the file.
 *
 * ------------------------------------------------------------------------- */
{
  yyrestart(stream);
  parser_cfg = &configuration;
  config_file_line_number = 1;
  return yyparse();
}
