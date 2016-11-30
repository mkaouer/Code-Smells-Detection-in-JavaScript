/* -*- coding: utf-8 -*-
** Copyright (C) 2009, 2010, 2012, 2013 Laboratoire de Recherche et
** Développement de l'Epita (LRDE).
** Copyright (C) 2003, 2004, 2005, 2006 Laboratoire d'Informatique de
** Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
** Université Pierre et Marie Curie.
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
%language "C++"
%locations
%defines
%name-prefix "tgbayy"
%debug
%error-verbose
%define api.location.type "spot::location"

%code requires
{
#include <string>
#include "public.hh"

/* Cache parsed formulae.  Labels on arcs are frequently identical and
   it would be a waste of time to parse them to formula* over and
   over, and to register all their atomic_propositions in the
   bdd_dict.  Keep the bdd result around so we can reuse it.  */
typedef std::map<std::string, bdd> formula_cache;
}

%parse-param {spot::tgba_parse_error_list& error_list}
%parse-param {spot::ltl::environment& parse_environment}
%parse-param {spot::ltl::environment& parse_envacc}
%parse-param {spot::tgba_explicit_string*& result}
%parse-param {formula_cache& fcache}
%union
{
  int token;
  std::string* str;
  const spot::ltl::formula* f;
  std::list<const spot::ltl::formula*>* list;
}

%code
{
#include "ltlast/constant.hh"
#include "ltlparse/public.hh"
#include <map>

/* tgbaparse.hh and parsedecl.hh include each other recursively.
   We must ensure that YYSTYPE is declared (by the above %union)
   before parsedecl.hh uses it. */
#include "parsedecl.hh"
using namespace spot::ltl;

typedef std::pair<bool, spot::ltl::formula*> pair;
}

%token <str> STRING UNTERMINATED_STRING
%token <str> IDENT
%type <str> strident string
%type <str> condition
%type <list> acc_list
%token ACC_DEF

%destructor { delete $$; } <str>
%destructor {
  for (std::list<const spot::ltl::formula*>::iterator i = $$->begin();
       i != $$->end(); ++i)
    (*i)->destroy();
  delete $$;
  } <list>

%printer { debug_stream() << *$$; } <str>

%%
tgba: acceptance_decl lines
      | acceptance_decl
      { result->add_state("0"); }
      | lines
      |
      { result->add_state("0"); };

acceptance_decl: ACC_DEF acc_decl ';'

/* At least one line.  */
lines: line
       | lines line
       ;

line: strident ',' strident ',' condition ',' acc_list ';'
       {
	 spot::state_explicit_string::transition* t
	   = result->create_transition(*$1, *$3);
	 if ($5)
	   {
	     formula_cache::const_iterator i = fcache.find(*$5);
	     if (i == fcache.end())
	       {
		 parse_error_list pel;
		 const formula* f =
		   spot::ltl::parse_boolean(*$5, pel, parse_environment,
					    debug_level());
		 for (parse_error_list::iterator i = pel.begin();
		      i != pel.end(); ++i)
		   {
		     // Adjust the diagnostic to the current position.
		     spot::location here = @5;
		     here.end.line = here.begin.line + i->first.end.line - 1;
		     here.end.column =
		       here.begin.column + i->first.end.column;
		     here.begin.line += i->first.begin.line - 1;
		     here.begin.column += i->first.begin.column;
		     error_list.push_back(spot::tgba_parse_error(here,
								 i->second));
		   }
		 if (f)
		   result->add_condition(t, f);
		 else
		   result->add_conditions(t, bddfalse);
		 fcache[*$5] = t->condition;
	       }
	     else
	       {
		 t->condition = i->second;
	       }
	     delete $5;
	   }
	 std::list<const formula*>::iterator i;
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
	 $$ = $1;
	 error_list.push_back(spot::tgba_parse_error(@1,
						     "unterminated string"));
       }

strident: string | IDENT

condition:
       {
	 $$ = 0;
       }
       | string
       {
	 $$ = $1;
       }
       ;

acc_list:
       {
	 $$ = new std::list<const formula*>;
       }
       | acc_list strident
       {
	 if (*$2 == "true")
	   {
	     $1->push_back(constant::true_instance());
	   }
	 else if (*$2 != "" && *$2 != "false")
	   {
	     const formula* f = parse_envacc.require(*$2);
	     if (! result->has_acceptance_condition(f))
	       {
		 error_list.push_back(spot::tgba_parse_error(@2,
			 "undeclared acceptance condition `" + *$2 + "'"));
		 f->destroy();
		 // $2 will be destroyed on error recovery.
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
	 const formula* f = parse_envacc.require(*$2);
	 if (! f)
	   {
	     std::string s = "acceptance condition `";
	     s += *$2;
	     s += "' unknown in environment `";
	     s += parse_envacc.name();
	     s += "'";
	     error_list.push_back(spot::tgba_parse_error(@2, s));
	     YYERROR;
	   }
	 result->declare_acceptance_condition(f);
	 delete $2;
       }
       ;

%%

void
tgbayy::parser::error(const location_type& location,
		      const std::string& message)
{
  error_list.push_back(spot::tgba_parse_error(location, message));
}

namespace spot
{
  tgba_explicit_string*
  tgba_parse(const std::string& name,
	     tgba_parse_error_list& error_list,
	     bdd_dict* dict,
	     environment& env,
	     environment& envacc,
	     bool debug)
  {
    if (tgbayyopen(name))
      {
	error_list.push_back
	  (tgba_parse_error(spot::location(),
			    std::string("Cannot open file ") + name));
	return 0;
      }
    formula_cache fcache;
    tgba_explicit_string* result = new tgba_explicit_string(dict);
    tgbayy::parser parser(error_list, env, envacc, result, fcache);
    parser.set_debug_level(debug);
    parser.parse();
    tgbayyclose();
    return result;
  }
}

// Local Variables:
// mode: c++
// End:
