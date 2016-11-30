/* Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
** département Systèmes Répartis Coopératifs (SRC), Université Pierre
** et Marie Curie.
**
** This file is part of Spot, a model checking library.
**
** Spot is free software; you can redistribute it and/or modify it
** under the terms of the GNU General Public License as published by
** the Free Software Foundation; either version 2 of the License, or
** (at your option) any later version.
**
** Spot is distributed in the hope that it will be useful, but WITHOUT
** ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
** or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
** License for more details.
**
** You should have received a copy of the GNU General Public License
** along with Spot; see the file COPYING.  If not, write to the Free
** Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
** 02111-1307, USA.
*/
%option noyywrap
%option prefix="ltlyy"
%option outfile="lex.yy.c"

%{
#include <string>
#include "ltlparse/parsedecl.hh"

/* Hack Flex so we read from a string instead of reading from a file.  */
#define YY_INPUT(buf, result, max_size)					\
  do {									\
    result = (max_size < to_parse_size) ? max_size : to_parse_size;	\
    memcpy(buf, to_parse, result);					\
    to_parse_size -= result;						\
    to_parse += result;							\
  } while (0);

#define YY_USER_ACTION \
  yylloc->columns(yyleng);

static const char* to_parse = 0;
static size_t to_parse_size = 0;

void
flex_set_buffer(const char* buf)
{
  to_parse = buf;
  to_parse_size = strlen(to_parse);
}

%}

%%

%{
  yylloc->step();
%}

"("			return PAR_OPEN;
")"			return PAR_CLOSE;

"!"			return OP_NOT;
  /* & and | come from Spin.  && and || from LTL2BA.
     /\, \/, and xor are from LBTT.
  */
"||"|"|"|"+"|"\\/"	return OP_OR;
"&&"|"&"|"."|"*"|"/\\"	return OP_AND;
"^"|"xor"		return OP_XOR;
"=>"|"->"		return OP_IMPLIES;
"<=>"|"<->"		return OP_EQUIV;

  /* <>, [], and () are used in Spin.  */
"F"|"<>"		return OP_F;
"G"|"[]"		return OP_G;
"U"			return OP_U;
"R"|"V"			return OP_R;
"X"|"()"		return OP_X;

"1"|"true"		return CONST_TRUE;
"0"|"false"		return CONST_FALSE;

[ \t\n]+		/* discard whitespace */ yylloc->step ();

  /* An Atomic proposition cannot start with the letter
     used by a unary operator (F,G,X), unless this
     letter is followed by a digit in which case we assume
     it's an ATOMIC_PROP (even though F0 could be seen as Ffalse, we
     don't).  */
[a-zA-EH-WYZ_][a-zA-Z0-9_]* |
[FGX][0-9_][a-zA-Z0-9_]* {
		  yylval->str = new std::string(yytext);
	          return ATOMIC_PROP;
		}

  /* Atomic propositions can also be enclosed in double quotes.  */
\"[^\"]*\"	{
		  yylval->str = new std::string(yytext + 1, yyleng - 2);
	          return ATOMIC_PROP;
		}

.		return *yytext;

<<EOF>>		return END_OF_INPUT;

%{
  /* Dummy use of yyunput to shut up a gcc warning.  */
  (void) &yyunput;
%}
