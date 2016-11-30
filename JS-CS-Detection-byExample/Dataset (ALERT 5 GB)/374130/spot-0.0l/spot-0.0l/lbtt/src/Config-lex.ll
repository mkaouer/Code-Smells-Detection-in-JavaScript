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
#include <cmath>
#include <climits>
#include <cstdlib>
#include <cstring>
#include <string>
#include "Configuration.h"
#include "Config-parse.h"

extern int config_file_line_number;

%}

%option case-insensitive
%option never-interactive
%option noyywrap

%%

[ \t]*                      { /* Skip whitespace. */ }
"#"[^\n]*                   { /* Skip comments. */ }

"\n"                        { /* Skip newlines, but update the line number. */
                               config_file_line_number++;
                            }

"{"                         { return CFG_LBRACE; }
"}"                         { return CFG_RBRACE; }
"="                         { return CFG_EQUALS; }

algorithm                   { return CFG_ALGORITHM; }
enabled                     { return CFG_ENABLED; }
name                        { return CFG_NAME; }
parameters                  { return CFG_PARAMETERS; }
path                        { return CFG_PROGRAMPATH; }

globaloptions               { return CFG_GLOBALOPTIONS; }
comparisoncheck             { return CFG_COMPARISONTEST; }
comparisontest              { return CFG_COMPARISONTEST; }
consistencycheck            { return CFG_CONSISTENCYTEST; }
consistencytest             { return CFG_CONSISTENCYTEST; }
interactive                 { return CFG_INTERACTIVE; }
intersectioncheck           { return CFG_INTERSECTIONTEST; }
intersectiontest            { return CFG_INTERSECTIONTEST; }
modelcheck                  { return CFG_MODELCHECK; }
rounds                      { return CFG_ROUNDS; }
verbosity                   { return CFG_VERBOSITY; }

statespaceoptions           { return CFG_STATESPACEOPTIONS; }
edgeprobability             { return CFG_EDGEPROBABILITY; }
propositions                { return CFG_PROPOSITIONS; }
size                        { return CFG_SIZE; }
truthprobability            { return CFG_TRUTHPROBABILITY; }
changeinterval              { return CFG_CHANGEINTERVAL; }
randomseed                  { return CFG_RANDOMSEED; }

formulaoptions              { return CFG_FORMULAOPTIONS; }
abbreviatedoperators        { return CFG_ABBREVIATEDOPERATORS; }
andpriority                 { return CFG_ANDPRIORITY; }
beforepriority              { return CFG_BEFOREPRIORITY; }
defaultoperatorpriority     { return CFG_DEFAULTOPERATORPRIORITY; }
equivalencepriority         { return CFG_EQUIVALENCEPRIORITY; }
falsepriority               { return CFG_FALSEPRIORITY; }
finallypriority             { return CFG_FINALLYPRIORITY; }
generatemode                { return CFG_GENERATEMODE; }
globallypriority            { return CFG_GLOBALLYPRIORITY; }
implicationpriority         { return CFG_IMPLICATIONPRIORITY; }
nextpriority                { return CFG_NEXTPRIORITY; }
notpriority                 { return CFG_NOTPRIORITY; }
orpriority                  { return CFG_ORPRIORITY; }
outputmode                  { return CFG_OUTPUTMODE; }
propositionpriority         { return CFG_PROPOSITIONPRIORITY; }
releasepriority             { return CFG_RELEASEPRIORITY; }
strongreleasepriority       { return CFG_STRONGRELEASEPRIORITY; }
truepriority                { return CFG_TRUEPRIORITY; }
untilpriority               { return CFG_UNTILPRIORITY; }
weakuntilpriority           { return CFG_WEAKUNTILPRIORITY; }
xorpriority                 { return CFG_XORPRIORITY; }

true|yes                    {
                              yylval.truth_value = true;
                              return CFG_TRUTH_VALUE;
                            }

false|no                    {
                              yylval.truth_value = false;
                              return CFG_TRUTH_VALUE;
                            }

always                      {
                              yylval.interactivity_value =
                                Configuration::ALWAYS;
                              return CFG_INTERACTIVITY_VALUE;
                            }

never                       {
                              yylval.interactivity_value =
                                Configuration::NEVER;
                              return CFG_INTERACTIVITY_VALUE;
                            }

onerror                     {
                              yylval.interactivity_value =
                                Configuration::ONERROR;
                              return CFG_INTERACTIVITY_VALUE;
                            }

normal                      {
			      yylval.formula_mode_value = 
				Configuration::NORMAL;
			      return CFG_FORMULA_MODE_VALUE;
                            }

nnf                         {
                              yylval.formula_mode_value = Configuration::NNF;
			      return CFG_FORMULA_MODE_VALUE;
                            }

local                       {
                              yylval.product_type_value = Configuration::LOCAL;
			      return CFG_PRODUCT_TYPE_VALUE;
                            }

global                      {
                              yylval.product_type_value =
	                        Configuration::GLOBAL;
			      return CFG_PRODUCT_TYPE_VALUE;
                            }

randomgraph                 {
                              yylval.statespace_mode_value
				= Configuration::RANDOMGRAPH;
			      return CFG_STATESPACE_MODE_VALUE;
                            }

randomconnectedgraph        {
                              yylval.statespace_mode_value
				= Configuration::RANDOMCONNECTEDGRAPH;
			      return CFG_STATESPACE_MODE_VALUE;
                            }

randompath                  {
                              yylval.statespace_mode_value
				= Configuration::RANDOMPATH;
			      return CFG_STATESPACE_MODE_VALUE;
                            }

enumeratedpath              {
                              yylval.statespace_mode_value
				= Configuration::ENUMERATEDPATH;
			      return CFG_STATESPACE_MODE_VALUE;
                            }


"-"?[0-9]+"...""-"?[0-9]+   {
                              char* dot_ptr;
                              yylval.integer_interval.min
				= strtol(yytext, &dot_ptr, 10);
			      
			      if (yylval.integer_interval.min == LONG_MIN
				  || yylval.integer_interval.min == LONG_MAX)
                                throw Configuration::ConfigurationException
                                  (config_file_line_number,
				   "integer out of range");

			      dot_ptr += 3;
			      yylval.integer_interval.max
				= strtol(dot_ptr, 0, 10);

			      if (yylval.integer_interval.max == LONG_MIN
				  || yylval.integer_interval.max == LONG_MAX)
                                throw Configuration::ConfigurationException
                                  (config_file_line_number,
				   "integer out of range");

			      return CFG_INTEGER_INTERVAL;
                            }

"-"?[0-9]+                  {
                              yylval.integer = strtol(yytext, 0, 10);
                              if (yylval.integer == LONG_MIN
                                  || yylval.integer == LONG_MAX)
                                throw Configuration::ConfigurationException
                                  (config_file_line_number,
				   "integer out of range");
                              return CFG_INTEGER;
                            }

"-"?[0-9]*"."[0-9]+         {
                              yylval.real = strtod(yytext, 0);

                              if (yylval.real == HUGE_VAL
				  || yylval.real == -HUGE_VAL)
                                throw Configuration::ConfigurationException
                                  (config_file_line_number,
				   "real number out of range");
                              return CFG_REAL;
                            }

\"([^\n\"\\]*(\\[^\n])?)*\" {
                              unsigned long int len = strlen(yytext);
                              bool escape = false;
                              yylval.str = new string;
                              for (unsigned long int i = 1; i < len - 1; i++)
			      {
                                if (!escape && yytext[i] == '\\')
                                  escape = true;
                                else
				{
                                  escape = false;
                                  (*yylval.str) += yytext[i];
                                }
                              }
                              return CFG_STRING_CONSTANT;
                            }

.                           {
                              return CFG_UNKNOWN;
                            }


%%

/* ========================================================================= */
int getCharacter()
/* ----------------------------------------------------------------------------
 *
 * Description:   Reads the next character from the lexer input stream.
 *
 * Arguments:     None.
 *
 * Returns:       The next character in the lexer input stream or EOF if there
 *                are no more characters to read.
 *
 * ------------------------------------------------------------------------- */
{
  return yyinput();
}
