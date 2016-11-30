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

%{

#include <config.h>
#include <cstdio>
#include <string>
#include "LtlFormula.h"
#include "NeverClaimAutomaton.h"

/******************************************************************************
 *
 * Variables used when parsing a never claim.
 *
 *****************************************************************************/

static NeverClaimAutomaton* automaton;              /* Automaton in which the
                                                     * results are stored.
                                                     */

string current_neverclaim_line;                     /* Current input line. */

int current_neverclaim_line_number;                 /* Number of the current
                                                     * line in the never claim
                                                     * file.
                                                     */



/******************************************************************************
 *
 * Declarations for external functions and variables (provided by the lexer)
 * used when parsing a never claim.
 *
 *****************************************************************************/

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
                                                     * input stream (defined
                                                     * in NeverClaim-lex.ll).
                                                     */

extern int yyleng;                                  /* Length of the last
                                                     * token parsed from the
                                                     * input (provided by the
                                                     * lexer).
                                                     */



/******************************************************************************
 *
 * Function to be called in case of a parse error.
 *
 *****************************************************************************/

/* ========================================================================= */
void yyerror(char*)
/* ----------------------------------------------------------------------------
 *
 * Description:   Function for reporting never claim parse errors.
 *
 * Arguments:     A pointer to a char (required to satisfy the function
 *                interface).
 *
 * Returns:       Nothing. Instead, throws an exception with information about
 *                the location of the error.
 *
 * ------------------------------------------------------------------------- */
{
  int c;

  string::size_type error_pos = current_neverclaim_line.length();
  if (error_pos > static_cast<unsigned long int>(yyleng))
    error_pos -= yyleng;

  do
  {
    c = getCharacter();
    if (c != EOF && c != '\n')
      current_neverclaim_line += static_cast<char>(c);
  }
  while (c != EOF && c != '\n');

  throw ParseErrorException
    (current_neverclaim_line, current_neverclaim_line_number, error_pos);
}



%}



/******************************************************************************
 *
 * Declarations for terminal and nonterminal symbols used in the grammar rules
 * below.
 *
 *****************************************************************************/

/* Data types. */

%union {
  string* pf;
  string* str;
}

/* Keywords. */

%token NC_NEVER NC_IF NC_FI NC_GOTO NC_SKIP

/* State labels. */

%token <str> NC_LABEL

/* Punctuation symbols. */

%token NC_COLON NC_SEMICOLON NC_DOUBLE_COLON NC_LBRACE NC_RBRACE NC_LPAREN
       NC_RPAREN NC_RIGHT_ARROW

/* Propositional formulae. */

%token <pf> NC_PROPOSITION NC_TRUE NC_FALSE
%right NC_OR
%right NC_AND
%nonassoc NC_NOT

%type <pf> formula

%%



/******************************************************************************
 *
 * Grammar rule definitions.
 *
 *****************************************************************************/

never_claim:         NC_NEVER NC_LBRACE states optional_semicolon NC_RBRACE
                   ;

optional_semicolon:  NC_SEMICOLON
                   | /* empty */
                   ;

states:              state
                   | states NC_SEMICOLON state
                   ;

state:                 {
                         automaton->addNewState();
                       }
                     state_labels state_body
                   ;

state_labels:        NC_LABEL NC_COLON
                       {
                         automaton->addNewLabel(*$1);
                         delete $1;
                       }
                   | state_labels NC_LABEL NC_COLON
                       {
                         automaton->addNewLabel(*$2);
                         delete $2;
                       }
                   ;

state_body:          NC_IF transitions NC_FI
                   | NC_SKIP
                   | NC_FALSE
                       {
                         automaton->currentState()->accepting() = false;
                       }
                   | NC_IF NC_DOUBLE_COLON NC_FALSE NC_FI
                       {
			 automaton->currentState()->accepting() = false;
		       }
		   ;

transitions:         transition
                   | transitions transition
                   ;

transition:          NC_DOUBLE_COLON formula NC_RIGHT_ARROW NC_GOTO NC_LABEL
                       {
                         automaton->currentState()->addTransition(*$5, $2);
                         delete $5;
                       }
                   ;

formula:             NC_PROPOSITION
                       { 
                         $$ = $1; 
                       }
                   | NC_TRUE
                       { 
                         $$ = $1; 
                       }
                   | NC_FALSE
                       {
                         $$ = $1;
		       }
                   | NC_NOT formula
                       {
                         $$ = new string;
                         *$$ += ::Ltl::LtlNegation::prefix_symbol;
                         *$$ += ' ';
                         *$$ += *$2;
                         delete $2;
                       }
                   | formula NC_AND formula
                       {
                         $$ = new string;
                         *$$ += ::Ltl::LtlConjunction::prefix_symbol;
                         *$$ += ' ';
                         *$$ += *$1;
                         *$$ += ' ';
                         *$$ += *$3;
                         delete $1;
                         delete $3;
                       }
                   | formula NC_OR formula
                       {
                         $$ = new string;
                         *$$ += ::Ltl::LtlDisjunction::prefix_symbol;
                         *$$ += ' ';
                         *$$ += *$1;
                         *$$ += ' ';
                         *$$ += *$3;
                         delete $1;
                         delete $3;
                       }
                   | NC_LPAREN formula NC_RPAREN
                       {
                         $$ = $2;
                       }
                   ;

%%



/******************************************************************************
 *
 * Main interface to the parser.
 *
 *****************************************************************************/

/* ========================================================================= */
int parseNeverClaim(FILE* stream, NeverClaimAutomaton& a)
/* ----------------------------------------------------------------------------
 *
 * Description:   Main interface to the never claim file parser. Parses a
 *                never claim and stores the results into the given
 *                NeverClaimAutomaton object.
 *
 * Arguments:     stream  --  A pointer to a file from which the never claim
 *                            should be read.  The file is assumed to be open
 *                            for reading.
 *                a       --  A reference to a NeverClaimAutomaton object in
 *                            which the results should be stored.
 *
 * Returns:       
 *
 * ------------------------------------------------------------------------- */
{
  yyrestart(stream);
  automaton = &a;
  current_neverclaim_line_number = 1;
  return yyparse();
}
