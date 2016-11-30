/* Copyright (C) 2003, 2004, 2005, 2006 Laboratoire d'Informatique de
** Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
** Université Pierre et Marie Curie.
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

/* Cache parsed formulae.  Labels on arcs are frequently identical and
   it would be a waste of time to parse them to formula* over and
   over, and to register all their atomic_propositions in the
   bdd_dict.  Keep the bdd result around so we can reuse it.  */
typedef std::map<std::string, bdd> formula_cache;
%}

%name-prefix="tgbayy"
%parse-param {spot::tgba_parse_error_list& error_list}
%parse-param {spot::ltl::environment& parse_environment}
%parse-param {spot::ltl::environment& parse_envacc}
%parse-param {spot::tgba_explicit*& result}
%parse-param {formula_cache& fcache}
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
  /* Unfortunately Bison 2.3 uses the same guards in all parsers :( */
#undef BISON_POSITION_HH
#undef BISON_LOCATION_HH
#include "ltlparse/public.hh"
#include <map>

/* tgbaparse.hh and parsedecl.hh include each other recursively.
   We must ensure that YYSTYPE is declared (by the above %union)
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
%type <str> condition
%type <list> acc_list
%token ACC_DEF

%destructor { delete $$; } STRING UNTERMINATED_STRING IDENT
                           strident string condition
%destructor {
  for (std::list<spot::ltl::formula*>::iterator i = $$->begin();
       i != $$->end(); ++i)
    spot::ltl::destroy(*i);
  delete $$;
  } acc_list

%printer { debug_stream() << *$$; } STRING UNTERMINATED_STRING IDENT
                                    strident string condition

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
	 spot::tgba_explicit::transition* t
	   = result->create_transition(*$1, *$3);
	 if ($5)
	   {
	     formula_cache::const_iterator i = fcache.find(*$5);
	     if (i == fcache.end())
	       {
		 parse_error_list pel;
		 formula* f = spot::ltl::parse(*$5, pel, parse_environment);
		 for (parse_error_list::iterator i = pel.begin();
		      i != pel.end(); ++i)
		   {
		     // Adjust the diagnostic to the current position.
		     location here = @1;
		     here.begin.line += i->first.begin.line;
		     here.begin.column += i->first.begin.column;
		     here.end.line =
		       here.begin.line + i->first.begin.line;
		     here.end.column =
		       here.begin.column + i->first.begin.column;
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
	 $$ = $1;
	 error_list.push_back(spot::tgba_parse_error(@1,
						     "unterminated string"));
       }

strident: string | IDENT

condition:
       {
	 $$ = 0
       }
       | string
       {
	 $$ = $1;
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
	     formula* f = parse_envacc.require(*$2);
	     if (! result->has_acceptance_condition(f))
	       {
		 error_list.push_back(spot::tgba_parse_error(@2,
			 "undeclared acceptance condition `" + *$2 + "'"));
		 destroy(f);
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
	 formula* f = parse_envacc.require(*$2);
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
  tgba_explicit*
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
	  (tgba_parse_error(tgbayy::location(),
			    std::string("Cannot open file ") + name));
	return 0;
      }
    formula_cache fcache;
    tgba_explicit* result = new tgba_explicit(dict);
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
