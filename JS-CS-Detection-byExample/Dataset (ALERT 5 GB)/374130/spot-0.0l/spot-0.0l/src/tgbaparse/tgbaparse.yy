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
%{
#include <string>
#include "public.hh"
%}

%parse-param {spot::tgba_parse_error_list &error_list}
%parse-param {spot::ltl::environment &parse_environment}
%parse-param {spot::tgba_explicit* &result}
%debug
%error-verbose
%union
{
  int token;
  std::string* str;
  spot::ltl::formula* f;
  std::list<spot::ltl::formula*>* list;
}

%{
#include "ltlast/constant.hh"
#include "ltlvisit/destroy.hh"
#include "ltlparse/public.hh"

/* tgbaparse.hh and parsedecl.hh include each other recursively.
   We mut ensure that YYSTYPE is declared (by the above %union)
   before parsedecl.hh uses it. */
#include "parsedecl.hh"
using namespace spot::ltl;

/* Ugly hack so that Bison use tgbayylex, not yylex.
   (%name-prefix doesn't work for the lalr1.cc skeleton
   at the time of writing.)  */
#define yylex tgbayylex

typedef std::pair<bool, spot::ltl::formula*> pair;
%}

%token <str> STRING UNTERMINATED_STRING
%token <str> IDENT
%type <str> strident string
%type <f> condition
%type <list> acc_list
%token ACC_DEF

%%
tgba: acceptance_decl lines | lines;

acceptance_decl: ACC_DEF acc_decl ';'

/* At least one line.  */
lines: line
       | lines line
       ;

line: strident ',' strident ',' condition ',' acc_list ';'
       {
	 spot::tgba_explicit::transition* t
	   = result->create_transition(*$1, *$3);
	 result->add_condition(t, $5);
	 std::list<formula*>::iterator i;
	 for (i = $7->begin(); i != $7->end(); ++i)
	   result->add_acceptance_condition(t, *i);
	 delete $1;
	 delete $3;
	 delete $7;
       }
       ;

string: STRING
       | UNTERMINATED_STRING
       {
	 error_list.push_back(spot::tgba_parse_error(@1,
						     "unterminated string"));
       }

strident: string | IDENT

condition:
       {
	 $$ = constant::true_instance();
       }
       | string
       {
	 parse_error_list pel;
	 formula* f = spot::ltl::parse(*$1, pel, parse_environment);
	 for (parse_error_list::iterator i = pel.begin();
	      i != pel.end(); ++i)
	   {
	     // Adjust the diagnostic to the current position.
	     Location here = @1;
	     here.begin.line += i->first.begin.line;
	     here.begin.column += i->first.begin.column;
	     here.end.line = here.begin.line + i->first.begin.line;
	     here.end.column = here.begin.column + i->first.begin.column;
	     error_list.push_back(spot::tgba_parse_error(here, i->second));
	   }
	 delete $1;
	 $$ = f ? f : constant::false_instance();
       }
       ;

acc_list:
       {
	 $$ = new std::list<formula*>;
       }
       | acc_list strident
       {
	 if (*$2 == "true")
	   {
	     $1->push_back(constant::true_instance());
	   }
	 else if (*$2 != "" && *$2 != "false")
	   {
	     formula* f = parse_environment.require(*$2);
	     if (! result->has_acceptance_condition(f))
	       {
		 error_list.push_back(spot::tgba_parse_error(@2,
			 "undeclared acceptance condition"));
		 destroy(f);
		 delete $2;
		 YYERROR;
	       }
	     $1->push_back(f);
	   }
	 delete $2;
	 $$ = $1;
       }
       ;


acc_decl:
       | acc_decl strident
       {
	 formula* f = parse_environment.require(*$2);
	 result->declare_acceptance_condition(f);
	 delete $2;
       }
       ;

%%

void
yy::Parser::print_()
{
  if (looka_ == STRING || looka_ == IDENT)
    YYCDEBUG << " '" << *value.str << "'";
}

void
yy::Parser::error_()
{
  error_list.push_back(spot::tgba_parse_error(location, message));
}

namespace spot
{
  tgba_explicit*
  tgba_parse(const std::string& name,
	     tgba_parse_error_list& error_list,
	     bdd_dict* dict,
	     environment& env,
	     bool debug)
  {
    if (tgbayyopen(name))
      {
	error_list.push_back
	  (tgba_parse_error(yy::Location(),
			    std::string("Cannot open file ") + name));
	return 0;
      }
    tgba_explicit* result = new tgba_explicit(dict);
    tgbayy::Parser parser(debug, yy::Location(), error_list, env, result);
    parser.parse();
    tgbayyclose();
    return result;
  }
}

// Local Variables:
// mode: c++
// End:
