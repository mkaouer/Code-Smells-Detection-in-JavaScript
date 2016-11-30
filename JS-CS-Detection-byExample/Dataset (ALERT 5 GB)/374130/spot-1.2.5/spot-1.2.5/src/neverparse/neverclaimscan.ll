/* Copyright (C) 2010, 2011, 2013 Laboratoire de Recherche et
** Développement de l'Epita (LRDE).
**
** This file is part of Spot, a model checking library.
**
** Spot is free software; you can redistribute it and/or modify it
** under the terms of the GNU General Public License as published by
** the Free Software Foundation; either version 3 of the License, or
** (at your option) any later version.
**
** Spot is distributed in the hope that it will be useful, but WITHOUT
** ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
** or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
** License for more details.
**
** You should have received a copy of the GNU General Public License
** along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
%option noyywrap
%option prefix="neverclaimyy"
%option outfile="lex.yy.c"

%{
#include <string>
#include "neverparse/parsedecl.hh"
#include "misc/escape.hh"

#define YY_USER_ACTION \
  yylloc->columns(yyleng);

#define YY_NEVER_INTERACTIVE 1

typedef neverclaimyy::parser::token token;
static int parent_level = 0;
static bool missing_parent = false;

%}

%x in_par

eol      \n|\r|\n\r|\r\n

%%

%{
  yylloc->step();
%}

                        /* skip blanks */
{eol}			yylloc->lines(yyleng); yylloc->step();
[ \t]+			yylloc->step();
"/*".*"*/"		yylloc->step();

"never"			return token::NEVER;
"skip"			return token::SKIP;
"if"			return token::IF;
"fi"			return token::FI;
"do"			return token::DO;
"od"			return token::OD;
"->"			return token::ARROW;
"goto"			return token::GOTO;
"false"|"0"		return token::FALSE;
"atomic"		return token::ATOMIC;
"assert"		return token::ASSERT;

("!"[ \t]*)?"("		{
			  parent_level = 1;
			  BEGIN(in_par);
			  yylval->str = new std::string(yytext,yyleng);
			}

<in_par>{
	 "("		{
			  ++parent_level;
			  yylval->str->append(yytext, yyleng);
			}
         /* if we match ")&&(" or ")||(", stay in <in_par> mode */
         ")"[ \t]*("&&"|"||")[ \t!]*"(" {
	                  yylval->str->append(yytext, yyleng);
			}
	 ")"		{
	                  yylval->str->append(yytext, yyleng);
			  if (!--parent_level)
			    {
                              BEGIN(0);
			      spot::trim(*yylval->str);
			      return token::FORMULA;
			    }
			}
         [^()]+		yylval->str->append(yytext, yyleng);
	 <<EOF>>	{
			  unput(')');
			  if (!missing_parent)
                             error_list.push_back(
			       spot::neverclaim_parse_error(*yylloc,
 				"missing closing parenthese"));
				  missing_parent = true;
			}
}

"true"|"1"		{
                          yylval->str = new std::string(yytext, yyleng);
			  return token::FORMULA;
                        }

[a-zA-Z][a-zA-Z0-9_]*   {
			  yylval->str = new std::string(yytext, yyleng);
	                  return token::IDENT;
		        }

.			return *yytext;

%{
  /* Dummy use of yyunput to shut up a gcc warning.  */
  (void) &yyunput;
%}

%%

namespace spot
{
  int
  neverclaimyyopen(const std::string &name)
  {
    if (name == "-")
      {
        yyin = stdin;
      }
    else
      {
        yyin = fopen(name.c_str(), "r");
        if (!yyin)
	  return 1;
      }
    // Reset the lexer in case a previous parse
    // ended badly.
    BEGIN(0);
    YY_NEW_FILE;
    return 0;
  }

  void
  neverclaimyyclose()
  {
    fclose(yyin);
    missing_parent = false;
  }
}
