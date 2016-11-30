/* Copyright (C) 2011, Laboratoire de Recherche et D�veloppement de
** l'Epita (LRDE).
** Copyright (C) 2003, 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
** d�partement Syst�mes R�partis Coop�ratifs (SRC), Universit� Pierre
** et Marie Curie.
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
%option prefix="tgbayy"
%option outfile="lex.yy.c"
%x STATE_STRING

%{
#include <string>
#include "tgbaparse/parsedecl.hh"

#define YY_USER_ACTION \
  yylloc->columns(yyleng);

#define YY_NEVER_INTERACTIVE 1

typedef tgbayy::parser::token token;

%}

eol      \n|\r|\n\r|\r\n

%%

%{
  yylloc->step ();
%}

acc[ \t]*=		return token::ACC_DEF;

[a-zA-Z_.][a-zA-Z0-9_.]*   {
			  yylval->str = new std::string(yytext, yyleng);
			  return token::IDENT;
			}

			/* discard whitespace */
{eol}			yylloc->lines(yyleng); yylloc->step();
[ \t]+			yylloc->step();

\"			{
			  yylval->str = new std::string;
			  BEGIN(STATE_STRING);
			}

.			return *yytext;

  /* Handle \" and \\ in strings.  */
<STATE_STRING>{
  \"                    {
                          BEGIN(INITIAL);
			  return token::STRING;
                        }
  \\["\\]               yylval->str->append(1, yytext[1]);
  [^"\\]+               yylval->str->append(yytext, yyleng);
  <<EOF>>		{
  			  BEGIN(INITIAL);
			  return token::UNTERMINATED_STRING;
			}
}

%{
  /* Dummy use of yyunput to shut up a gcc warning.  */
  (void) &yyunput;
%}

%%

namespace spot
{
  int
  tgbayyopen(const std::string &name)
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
    return 0;
  }

  void
  tgbayyclose()
  {
    fclose(yyin);
  }
}
