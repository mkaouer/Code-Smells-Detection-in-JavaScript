/* Copyright (C) 2004, 2005, 2006  Laboratoire d'Informatique de Paris 6 (LIP6),
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
%{
#include <string>
#include "public.hh"
#include "evtgba/symbol.hh"
%}

%name-prefix="evtgbayy"
%parse-param {spot::evtgba_parse_error_list &error_list}
%parse-param {spot::evtgba_explicit* &result}
%debug
%error-verbose
%union
{
  int token;
  std::string* str;
  spot::rsymbol_set* symset;
}

%{
/* evtgbaparse.hh and parsedecl.hh include each other recursively.
   We mut ensure that YYSTYPE is declared (by the above %union)
   before parsedecl.hh uses it. */
#include "parsedecl.hh"

/* Ugly hack so that Bison use tgbayylex, not yylex.
   (%name-prefix doesn't work for the lalr1.cc skeleton
   at the time of writing.)  */
#define yylex evtgbayylex
%}

%token <str> STRING UNTERMINATED_STRING
%token <str> IDENT
%type <str> strident string
%type <symset> acc_list
%token ACC_DEF
%token INIT_DEF

%destructor { delete $$; } STRING UNTERMINATED_STRING IDENT
                           strident string acc_list

%printer { debug_stream() << *$$; } STRING UNTERMINATED_STRING IDENT
                                    strident string

%%
evtgba: lines

/* At least one line.  */
lines: line
       | lines line
       ;

line: strident ',' strident ',' strident ',' acc_list ';'
       {
	 result->add_transition(*$1, *$5, *$7, *$3);
	 delete $1;
	 delete $5;
	 delete $3;
	 delete $7;
       }
      | ACC_DEF acc_decl ';'
      | INIT_DEF init_decl ';'
       ;

string: STRING
       | UNTERMINATED_STRING
       {
	 $$ = $1;
	 error_list.push_back(spot::evtgba_parse_error(@1,
						     "unterminated string"));
       }

strident: string | IDENT

acc_list:
       {
	 $$ = new spot::rsymbol_set;
       }
       | acc_list strident
       {
	 $$ = $1;
	 $$->insert(spot::rsymbol(*$2));
	 delete $2;
       }
       ;

acc_decl:
       | acc_decl strident
       {
	 result->declare_acceptance_condition(*$2);
	 delete $2;
       }
       ;

init_decl:
       | init_decl strident
       {
	 result->set_init_state(*$2);
	 delete $2;
       }
       ;

%%

void
evtgbayy::parser::error(const location_type& location,
			const std::string& message)
{
  error_list.push_back(spot::evtgba_parse_error(location, message));
}

namespace spot
{
  evtgba_explicit*
  evtgba_parse(const std::string& name,
	       evtgba_parse_error_list& error_list,
	       bool debug)
  {
    if (evtgbayyopen(name))
      {
	error_list.push_back
	  (evtgba_parse_error(evtgbayy::location(),
			      std::string("Cannot open file ") + name));
	return 0;
      }
    evtgba_explicit* result = new evtgba_explicit();
    evtgbayy::parser parser(error_list, result);
    parser.set_debug_level(debug);
    parser.parse();
    evtgbayyclose();
    return result;
  }
}

// Local Variables:
// mode: c++
// End:
