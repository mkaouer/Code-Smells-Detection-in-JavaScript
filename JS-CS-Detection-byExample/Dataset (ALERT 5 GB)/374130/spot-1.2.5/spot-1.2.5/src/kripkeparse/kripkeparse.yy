// -*- coding: utf-8 -*-
// Copyright (C) 2011, 2012, 2013 Laboratoire de Recherche et
// Développement de l'Epita (LRDE)
//
// This file is part of Spot, a model checking library.
//
// Spot is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or
// (at your option) any later version.
//
// Spot is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

%language "C++"
%locations
%defines
%expect 0
%name-prefix "kripkeyy"
%debug
%error-verbose
%define api.location.type "spot::location"

%code requires
{
#include <string>
#include <map>
#include "public.hh"

/* Cache parsed formulae.  Labels on arcs are frequently identical and
   it would be a waste of time to parse them to formula* over and
   over, and to register all their atomic_propositions in the
   bdd_dict.  Keep the bdd result around so we can reuse it.  */
typedef std::map<std::string, bdd> formula_cache;
}

%parse-param {spot::kripke_parse_error_list& error_list}
%parse-param {spot::ltl::environment& parse_environment}
%parse-param {spot::kripke_explicit*& result}
%parse-param {formula_cache& fcache}

%union
{
  int token;
  std::string* str;
  const spot::ltl::formula* f;
  std::list<std::string*>* list;
}

%code
{
#include "kripke/kripkeexplicit.hh"
#include "ltlparse/public.hh"
#include <map>

/* tgbaparse.hh and parsedecl.hh include each other recursively.
   We must ensure that YYSTYPE is declared (by the above %union)
   before parsedecl.hh uses it. */
#include "parsedecl.hh"

using namespace spot::ltl;
#include <iostream>
  //typedef std::pair<bool, spot::ltl::formula*> pair;
}

%token <str> STRING UNTERMINATED_STRING IDENT
%token COMA ","
%token SEMICOL ";"
%type <str> strident string
%type <str> condition
%type <list> follow_list

%destructor { delete $$; } <str>
%destructor {
  std::cout << $$->size() << std::endl;
  for (std::list<std::string*>::iterator i = $$->begin();
       i != $$->end(); ++i)
    delete (*i);
  delete $$;
  } <list>

%printer { debug_stream() << *$$; } <str>

%%

kripke:
lines {
 }
| {
}
;

/* At least one line.  */
lines: line { }
| lines line { }
       ;

line:
strident "," condition "," follow_list ";"
{
  result->add_state(*$1);
  if ($3)
  {
    formula_cache::const_iterator i = fcache.find(*$3);
    if (i == fcache.end())
    {
      parse_error_list pel;
      const formula* f = spot::ltl::parse(*$3, pel, parse_environment);
      for (parse_error_list::iterator i = pel.begin();
           i != pel.end(); ++i)
      {
        //Adjust the diagnostic to the current position.
	spot::location here = @3;
        here.end.line = here.begin.line + i->first.end.line - 1;
        here.end.column =
          here.begin.column + i->first.end.column;
        here.begin.line += i->first.begin.line - 1;
        here.begin.column += i->first.begin.column;
        error_list.push_back(spot::kripke_parse_error(here,
                                                    i->second));
      }
      if (f)
        result->add_condition(f, *$1);
      else
        result->add_conditions(bddfalse, *$1);
      fcache[*$3] = result->state_condition(*$1);
    }
    else
    {
      result->add_conditions(i->second, *$1);
    }
    delete $3;
  }
  std::list<std::string*>::iterator i;
  for (i = $5->begin(); i != $5->end(); ++i)
  {
    result->add_transition(*$1, **i);
    delete *i;
  }

  delete $1;
  delete $5;
}
;


string: STRING
          { $$ = $1; }
       | UNTERMINATED_STRING
       {
	 $$ = $1;
         error_list.push_back(spot::kripke_parse_error(@1,
	 					     "unterminated string"));
       }
;

strident: string
{ $$ = $1; }
| IDENT
{ $$ = $1; }
;

follow_list:
follow_list strident
{
  $$ = $1;
  $$->push_back($2);
}
| {
    $$ = new std::list<std::string*>;
  }
;

condition:
       {
	 $$ = 0;
       }
       | string
       {
	 $$ = $1;
       }
       ;

%%

void
kripkeyy::parser::error(const location_type& location,
                        const std::string& message)
{
  error_list.push_back(spot::kripke_parse_error(location, message));
}

namespace spot
{
  kripke_explicit*
  kripke_parse(const std::string& name,
               kripke_parse_error_list& error_list,
               bdd_dict* dict,
               environment& env,
               bool debug)
  {
    if (kripkeyyopen(name))
    {
      error_list.push_back
        (kripke_parse_error(spot::location(),
			    std::string("Cannot open file ") + name));
      return 0;
    }
    formula_cache fcache;
    kripke_explicit* result = new kripke_explicit(dict);
    kripkeyy::parser parser(error_list, env, result, fcache);
    parser.set_debug_level(debug);
    parser.parse();
    kripkeyyclose();

    return result;
  }
}

// Local Variables:
// mode: c++
// End:
