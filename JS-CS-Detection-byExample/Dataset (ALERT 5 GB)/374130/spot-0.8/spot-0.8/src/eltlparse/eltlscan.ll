/* Copyright (C) 2008 Laboratoire de Recherche et Développement
** de l'Epita (LRDE).
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
%option prefix="eltlyy"
%option outfile="lex.yy.c"

%{
#include <string>
#include <stack>
#include "eltlparse/parsedecl.hh"

static int _atoi (char* yytext, int base);

#define YY_USER_ACTION \
  yylloc->columns(yyleng);

// Flex uses `0' for end of file.  0 is not a token_type.
#define yyterminate() return token::END_OF_FILE

// Stack for handling include files.
typedef std::pair<YY_BUFFER_STATE, std::string> state;
std::stack<state> include;

#define ERROR(Msg) \
  pe.list_.push_back \
    (spot::eltl::parse_error(*yylloc, spot::eltl::spair(pe.file_, Msg)))

typedef eltlyy::parser::token token;

%}

eol      \n|\r|\n\r|\r\n
%s	formula
%x	incl

%%

%{
  yylloc->step();
%}

  /* Rules for the include part. */

<incl>[ \t]*
<incl>[^ \t\n]+		{
			  FILE* tmp = fopen(yytext, "r");
			  if (!tmp)
			    ERROR(std::string("cannot open file ") + yytext);
			  else
			  {
			    include.push(make_pair(YY_CURRENT_BUFFER, pe.file_));
                            pe.file_ = std::string(yytext);
			    yy_switch_to_buffer(yy_create_buffer(tmp, YY_BUF_SIZE));
                          }
			  BEGIN(INITIAL);
			}

  /* Global rules (1). */

"("			return token::LPAREN;
","			return token::COMMA;
")"			return token::RPAREN;
"!"			return token::OP_NOT;

  /* & and | come from Spin.  && and || from LTL2BA.
     /\, \/, and xor are from LBTT.
  */
"||"|"|"|"+"|"\\/" {
			  return token::OP_OR;
			}
"&&"|"&"|"."|"*"|"/\\"	{
			  return token::OP_AND;
			}
"^"|"xor"		return token::OP_XOR;
"=>"|"->"		return token::OP_IMPLIES;
"<=>"|"<->"		return token::OP_EQUIV;

  /* Rules for the automaton definitions part. */

<INITIAL>"include"	BEGIN(incl);
<INITIAL>"%"		BEGIN(formula);

<INITIAL>"="		return token::EQ;
<INITIAL>"accept"	return token::ACC;
<INITIAL>"finish"	return token::FIN;

<INITIAL>[tT][rR][uU][eE] {
			  return token::CONST_TRUE;
			}
<INITIAL>[fF][aA][lL][sS][eE] {
			  return token::CONST_FALSE;
			}

<INITIAL>[a-zA-Z][a-zA-Z0-9_]* {
			  yylval->sval = new std::string(yytext, yyleng);
	                  return token::IDENT;
		        }

<INITIAL>[0-9]+		{
			  // Out of range is checked in the parser.
	                  yylval->ival = _atoi(yytext, 10);
	                  return token::STATE;
			}

<INITIAL>$[0-9]+	{
			  // Out of range is checked in the parser.
	                  yylval->ival = _atoi(++yytext, 10);
	                  return token::ARG;
			}

<INITIAL><<EOF>>	{
			  if (include.empty())
			    yyterminate();

			  state s = include.top();
			  include.pop();
			  pe.file_ = s.second;
			  yy_delete_buffer(YY_CURRENT_BUFFER);
			  yy_switch_to_buffer(s.first);
			}

  /* Rules for the formula part. */

<formula>"1"|[tT][rR][uU][eE] {
			  return token::CONST_TRUE;
			}
<formula>"0"|[fF][aA][lL][sS][eE] {
			  return token::CONST_FALSE;
			}

<formula>[a-zA-Z][a-zA-Z0-9_]* {
			  yylval->sval = new std::string(yytext, yyleng);
			  return token::ATOMIC_PROP;
			}

  /* Global rules (2). */

			/* discard whitespace */
{eol}			yylloc->lines(yyleng); yylloc->step();
[ \t]+			yylloc->step();

.			return *yytext;


%{
  /* Dummy use of yyunput to shut up a gcc warning.  */
  (void) &yyunput;
%}

%%

namespace spot
{
  namespace eltl
  {
    int
    flex_open(const std::string &name)
    {
      if (name == "-")
	yyin = stdin;
      else
      {
	yyin = fopen(name.c_str(), "r");
	if (!yyin)
	  return 1;
      }
      return 0;
    }

    void
    flex_close()
    {
      fclose(yyin);
    }

    void
    flex_scan_string(const char* s)
    {
      yy_scan_string(s);
    }
  }
}

static int
_atoi(char* yytext, int base)
{
  errno = 0;
  long i = strtol(yytext, 0, base);
  if (i > std::numeric_limits<long>::max() ||
      i < std::numeric_limits<long>::min() || errno == ERANGE)
    return -1;
  return i;
}
