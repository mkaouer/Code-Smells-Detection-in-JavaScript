/* Copyright (C) 2010, 2011 Laboratoire de Recherche et Développement
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
%language "C++"
%locations
%defines
%expect 0 // No shift/reduce
%expect-rr 0 // No reduce/reduce
%name-prefix "neverclaimyy"
%debug
%error-verbose

%code requires
{
#include <string>
#include <cstring>
#include "ltlast/constant.hh"
#include "public.hh"

  typedef std::pair<spot::ltl::formula*, std::string*> pair;
}

%parse-param {spot::neverclaim_parse_error_list& error_list}
%parse-param {spot::ltl::environment& parse_environment}
%parse-param {spot::tgba_explicit_string*& result}
%union
{
  std::string* str;
  pair* p;
  std::list<pair>* list;
}

%code
{
#include "ltlast/constant.hh"
  /* Unfortunately Bison 2.3 uses the same guards in all parsers :( */
#undef BISON_POSITION_HH
#undef BISON_LOCATION_HH
#include "ltlparse/public.hh"

/* neverclaimparse.hh and parsedecl.hh include each other recursively.
   We must ensure that YYSTYPE is declared (by the above %union)
   before parsedecl.hh uses it. */
#include "parsedecl.hh"
using namespace spot::ltl;
}

%token NEVER "never"
%token SKIP "skip"
%token IF "if"
%token FI "fi"
%token ARROW "->"
%token GOTO "goto"
%token FALSE "false"
%token <str> FORMULA "boolean formula"
%token <str> IDENT "identifier"
%type <str> formula opt_dest
%type <p> transition
%type <list> transitions
%type <str> ident_list


%destructor { delete $$; } <str>
%destructor { $$->first->destroy(); delete $$->second; delete $$; } <p>
%destructor {
  for (std::list<pair>::iterator i = $$->begin();
       i != $$->end(); ++i)
  {
    i->first->destroy();
    delete i->second;
  }
  delete $$;
  } <list>
  %printer {
    if ($$)
      debug_stream() << *$$;
    else
      debug_stream() << "\"\""; } <str>

%%
neverclaim:
  "never" '{' states '}'


states:
  /* empty */
  | state
  | states ';' state
  | states ';'

ident_list:
    IDENT ':'
    {
      $$ = $1;
    }
  | ident_list IDENT ':'
    {
      result->add_state_alias(*$2, *$1);
      delete $1;
      $$ = $2;
    }

state:
  ident_list "skip"
    {
      spot::tgba_explicit::transition* t = result->create_transition(*$1, *$1);
      bool acc = !strncmp("accept", $1->c_str(), 6);
      if (acc)
	result->add_acceptance_condition(t,
					 spot::ltl::constant::true_instance());
      delete $1;
    }
  | ident_list { delete $1; }
  | ident_list "false" { delete $1; }
  | ident_list "if" transitions "fi"
    {
      std::list<pair>::iterator it;
      bool acc = !strncmp("accept", $1->c_str(), 6);
      for (it = $3->begin(); it != $3->end(); ++it)
      {
	spot::tgba_explicit::transition* t =
	  result->create_transition(*$1,*it->second);

	result->add_condition(t, it->first);
	if (acc)
	  result
	    ->add_acceptance_condition(t, spot::ltl::constant::true_instance());
      }
      // Free the list
      delete $1;
      for (std::list<pair>::iterator it = $3->begin();
	   it != $3->end(); ++it)
	delete it->second;
      delete $3;
    }


transitions:
  /* empty */ { $$ = new std::list<pair>; }
  | transitions transition
    {
      if ($2)
	{
	  $1->push_back(*$2);
	  delete $2;
	}
      $$ = $1;
    }


formula: FORMULA | "false" { $$ = new std::string("0"); }

opt_dest:
  /* empty */
    {
      $$ = 0;
    }
  | "->" "goto" IDENT
    {
      $$ = $3;
    }

transition:
  ':' ':' formula opt_dest
    {
      // If there is no destination, do ignore the transition.
      // This happens for instance with
      //   if
      //   :: false
      //   fi
      if (!$4)
	{
	  delete $3;
	  $$ = 0;
	}
      else
	{
	  spot::ltl::parse_error_list pel;
	  spot::ltl::formula* f = spot::ltl::parse(*$3, pel);
	  delete $3;
	  for(spot::ltl::parse_error_list::const_iterator i = pel.begin();
	  i != pel.end(); ++i)
	    {
	      // Adjust the diagnostic to the current position.
	      location here = @3;
	      here.end.line = here.begin.line + i->first.end.line - 1;
	      here.end.column = here.begin.column + i->first.end.column -1;
	      here.begin.line += i->first.begin.line - 1;
	      here.begin.column += i->first.begin.column - 1;
	      error(here, i->second);
	    }
	  $$ = new pair(f, $4);
	}
    }
%%

void
neverclaimyy::parser::error(const location_type& location,
			    const std::string& message)
{
  error_list.push_back(spot::neverclaim_parse_error(location, message));
}

namespace spot
{
  tgba_explicit_string*
  neverclaim_parse(const std::string& name,
		   neverclaim_parse_error_list& error_list,
		   bdd_dict* dict,
		   environment& env,
		   bool debug)
  {
    if (neverclaimyyopen(name))
      {
	error_list.push_back
	  (neverclaim_parse_error(neverclaimyy::location(),
				  std::string("Cannot open file ") + name));
	return 0;
      }
    tgba_explicit_string* result = new tgba_explicit_string(dict);
    result->declare_acceptance_condition(spot::ltl::constant::true_instance());
    neverclaimyy::parser parser(error_list, env, result);
    parser.set_debug_level(debug);
    parser.parse();
    neverclaimyyclose();
    return result;
  }
}

// Local Variables:
// mode: c++
// End:
