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
#include <cstring>
#include <string>
#include "LtlFormula.h"
#include "NeverClaim-parse.h"

extern string current_neverclaim_line;
extern int current_neverclaim_line_number;
%}

%option never-interactive
%option noyywrap
%option nounput

%%

[ \t]                      {
                             current_neverclaim_line += yytext;
                             /* Skip whitespace. */
                           }
\n                         {
                             current_neverclaim_line = "";
			     current_neverclaim_line_number++;
                             /* Skip newlines. */
                           }
"/*"([^\*]*(\*[^/])?)*"*/" { 
                             char* s = yytext, *t;

			     do
			     {
			       t = strchr(s, '\n');
			       if (t != static_cast<char*>(0))
			       {
				 current_neverclaim_line = "";
				 current_neverclaim_line_number++;
				 s = t + 1;
			       }
			     }
			     while (t != static_cast<char*>(0));

			     current_neverclaim_line += s;

                             /* Skip comments. */
                           }

never                      { 
                             current_neverclaim_line += yytext;
                             return NC_NEVER;
                           }

if                         {
                             current_neverclaim_line += yytext;
                             return NC_IF;
                           }

fi                         {
                             current_neverclaim_line += yytext;
                             return NC_FI;
                           }

goto                       {
                             current_neverclaim_line += yytext;
                             return NC_GOTO;
                           }

skip                       {
                             current_neverclaim_line += yytext;
                             return NC_SKIP;
                           }

"::"                       {
                             current_neverclaim_line += yytext;
                             return NC_DOUBLE_COLON;
                           }

":"                        {
                             current_neverclaim_line += yytext;
                             return NC_COLON;
                           }

";"                        {
                             current_neverclaim_line += yytext;
                             return NC_SEMICOLON;
                           }

"{"                        {
                             current_neverclaim_line += yytext;
                             return NC_LBRACE;
                           }

"}"                        {
                             current_neverclaim_line += yytext;
                             return NC_RBRACE;
                           }

"("                        {
                             current_neverclaim_line += yytext;
                             return NC_LPAREN;
                           }

")"                        {
                             current_neverclaim_line += yytext;
                             return NC_RPAREN;
                           }

"->"                       {
                             current_neverclaim_line += yytext;
                             return NC_RIGHT_ARROW;
                           }

"p"[0-9]+                  {
                             current_neverclaim_line += yytext;
                             yylval.pf = new string(yytext);
                             return NC_PROPOSITION;
                           }

"1"|true                   {
                             current_neverclaim_line += yytext;
                             yylval.pf = new string;
                             *yylval.pf += ::Ltl::LtlTrue::prefix_symbol;
                             return NC_TRUE;
			   }

"0"|false                  {
                             current_neverclaim_line += yytext;
                             yylval.pf = new string;
			     *yylval.pf += ::Ltl::LtlFalse::prefix_symbol;
                             return NC_FALSE;
                           }

"||"                       {
                             current_neverclaim_line += yytext;
                             return NC_OR;
                           }

"&&"                       {
                             current_neverclaim_line += yytext;
                             return NC_AND;
                           }

"!"                        {
                             current_neverclaim_line += yytext;
                             return NC_NOT;
                           }

[A-Za-z_][A-Za-z0-9_]*     {
                             current_neverclaim_line += yytext;
                             yylval.str = new string(yytext);
                             return NC_LABEL;
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
