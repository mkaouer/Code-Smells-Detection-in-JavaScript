/* Copyright (C) 2013 Laboratoire de Recherche et Développement
** de l'Epita (LRDE).
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
%option prefix="dstaryy"
%option outfile="lex.yy.c"
/* %option debug */

%{
#include <string>
#include "dstarparse/parsedecl.hh"

#define YY_USER_ACTION yylloc->columns(yyleng);
#define YY_NEVER_INTERACTIVE 1

typedef dstaryy::parser::token token;
%}

eol      \n|\r|\n\r|\r\n
%x in_COMMENT in_STRING

%%

%{
  std::string s;
  yylloc->step();
%}

{eol}			yylloc->lines(yyleng); return token::EOL;

                        /* skip blanks and comments */
[ \t]+			yylloc->step();
"/*"			BEGIN(in_COMMENT);
"//".*			continue;
"Comment:".*		yylloc->step();

"DRA"			return token::DRA;
"DSA"			return token::DSA;
"v2"			return token::V2;
"explicit"		return token::EXPLICIT;

"Acceptance-Pairs:"	return token::ACCPAIRS;
"AP:"			return token::AP;
"Start:"		return token::START;
"States:"		return token::STATES;
"State:"		return token::STATE;
"Acc-Sig:"		return token::ACCSIG;
"---"			return token::ENDOFHEADER;

[0-9]+			{
			  errno = 0;
			  unsigned long n = strtoul(yytext, 0, 10);
                          yylval->num = n;
			  if (errno || yylval->num != n)
			    {
                              error_list.push_back(
			        spot::dstar_parse_error(*yylloc,
				  "value too large"));
			      yylval->num = 0;
                            }
                          return token::NUMBER;
			}

"\""			BEGIN(in_STRING);

<in_COMMENT>{
  [^*\n]*		continue;
  "*"+[^*/\n]*		continue;
  "\n"+			yylloc->end.column = 1;	yylloc->lines(yyleng);
  "*"+"/"		BEGIN(INITIAL);
  <<EOF>>		{
                           error_list.push_back(
			     spot::dstar_parse_error(*yylloc,
			       "unclosed comment"));
			   return 0;
                        }
}

<in_STRING>{
  \"	                {
                           BEGIN(INITIAL);
			   yylval->str = new std::string(s);
			   return token::STRING;
 			}
  \\\"			s += '"';
  \\.			s += yytext[1];
  [^\\\"]+		s.append(yytext, yyleng);
  <<EOF>>		{
                           error_list.push_back(
			     spot::dstar_parse_error(*yylloc,
			       "unclosed string"));
			   return 0;
                        }
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
  dstaryyopen(const std::string &name)
  {
    // yy_flex_debug = 1;
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
    YY_NEW_FILE;
    BEGIN(INITIAL);
    return 0;
  }

  void
  dstaryyclose()
  {
    fclose(yyin);
  }
}
