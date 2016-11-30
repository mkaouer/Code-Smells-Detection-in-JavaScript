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
#include "ltlast/allnodes.hh"
#include "ltlvisit/destroy.hh"

%}

%parse-param {spot::ltl::parse_error_list &error_list}
%parse-param {spot::ltl::environment &parse_environment}
%parse-param {spot::ltl::formula* &result}
%debug
%error-verbose
%union
{
  int token;
  std::string* str;
  spot::ltl::formula* ltl;
}

%{
/* ltlparse.hh and parsedecl.hh include each other recursively.
   We mut ensure that YYSTYPE is declared (by the above %union)
   before parsedecl.hh uses it. */
#include "parsedecl.hh"
using namespace spot::ltl;

/* Ugly hack so that Bison uses ltlyylex, not yylex.
   (%name-prefix doesn't work for the lalr1.cc skeleton
   at the time of writing.)  */
#define yylex ltlyylex
%}

/* Logical operators.  */
%left <token> OP_OR
%left <token> OP_XOR
%left <token> OP_AND
%left <token> OP_IMPLIES OP_EQUIV

/* LTL operators.  */
%left <token> OP_U OP_R
%nonassoc <token> OP_F OP_G
%nonassoc <token> OP_X

/* Not has the most important priority.  */
%nonassoc <token> OP_NOT

/* Grouping (parentheses).  */
%token <token> PAR_OPEN PAR_CLOSE

/* Atomic proposition.  */
%token <str> ATOMIC_PROP

/* Constants */
%token CONST_TRUE
%token CONST_FALSE
%token END_OF_INPUT

%type <ltl> result ltl_formula subformula

%%
result:       ltl_formula END_OF_INPUT
	      { result = $$ = $1;
		YYACCEPT;
	      }
	    | many_errors END_OF_INPUT
	      { error_list.push_back(parse_error(@1,
				      "couldn't parse anything sensible"));
		result = $$ = 0;
		YYABORT;
	      }
	    | END_OF_INPUT
	      { error_list.push_back(parse_error(@1, "empty input"));
		result = $$ = 0;
		YYABORT;
	      }

many_errors_diagnosed : many_errors
	      { error_list.push_back(parse_error(@1,
				     "unexpected input ignored")); }

ltl_formula: subformula
	      { $$ = $1; }
	    | many_errors_diagnosed subformula
	      { $$ = $2; }
	    | ltl_formula many_errors_diagnosed
	      { $$ = $1; }

many_errors: error
	    | many_errors error

/* The reason we use `constant::false_instance()' for error recovery
   is that it isn't reference counted.  (Hence it can't leak references.)  */

subformula: ATOMIC_PROP
	      {
		$$ = parse_environment.require(*$1);
		if (! $$)
		  {
		    std::string s = "unknown atomic proposition `";
		    s += *$1;
		    s += "' in environment `";
		    s += parse_environment.name();
		    s += "'";
		    error_list.push_back(parse_error(@1, s));
		    delete $1;
		    YYERROR;
		  }
		else
		  delete $1;
	      }
	    | CONST_TRUE
	      { $$ = constant::true_instance(); }
	    | CONST_FALSE
	      { $$ = constant::false_instance(); }
	    | PAR_OPEN subformula PAR_CLOSE
	      { $$ = $2; }
	    | PAR_OPEN error PAR_CLOSE
	      { error_list.push_back(parse_error(@$,
		 "treating this parenthetical block as false"));
		$$ = constant::false_instance();
	      }
	    | PAR_OPEN subformula many_errors_diagnosed PAR_CLOSE
              { $$ = $2; }
	    | PAR_OPEN subformula many_errors_diagnosed END_OF_INPUT
	      { error_list.push_back(parse_error(@1 + @2,
				      "missing closing parenthesis"));
		$$ = $2;
	      }
	    | PAR_OPEN many_errors_diagnosed END_OF_INPUT
	      { error_list.push_back(parse_error(@$,
                    "missing closing parenthesis, "
		    "treating this parenthetical block as false"));
		$$ = constant::false_instance();
	      }
	    | OP_NOT subformula
	      { $$ = unop::instance(unop::Not, $2); }
	    | subformula OP_AND subformula
	      { $$ = multop::instance(multop::And, $1, $3); }
	    | subformula OP_AND error
	      {
		destroy($1);
		error_list.push_back(parse_error(@2,
				     "missing right operand for OP_AND"));
		$$ = constant::false_instance();
	      }
	    | subformula OP_OR subformula
	      { $$ = multop::instance(multop::Or, $1, $3); }
	    | subformula OP_OR error
	      {
		destroy($1);
		error_list.push_back(parse_error(@2,
				     "missing right operand for OP_OR"));
		$$ = constant::false_instance();
	      }
	    | subformula OP_XOR subformula
	      { $$ = binop::instance(binop::Xor, $1, $3); }
	    | subformula OP_XOR error
	      {
		destroy($1);
		error_list.push_back(parse_error(@2,
				     "missing right operand for OP_XOR"));
		$$ = constant::false_instance();
	      }
	    | subformula OP_IMPLIES subformula
	      { $$ = binop::instance(binop::Implies, $1, $3); }
	    | subformula OP_IMPLIES error
	      {
		destroy($1);
		error_list.push_back(parse_error(@2,
				     "missing right operand for OP_IMPLIES"));
		$$ = constant::false_instance();
	      }
	    | subformula OP_EQUIV subformula
	      { $$ = binop::instance(binop::Equiv, $1, $3); }
	    | subformula OP_EQUIV error
	      {
		destroy($1);
		error_list.push_back(parse_error(@2,
				     "missing right operand for OP_EQUIV"));
		$$ = constant::false_instance();
	      }
	    | subformula OP_U subformula
	      { $$ = binop::instance(binop::U, $1, $3); }
	    | subformula OP_U error
	      {
		destroy($1);
		error_list.push_back(parse_error(@2,
				     "missing right operand for OP_U"));
		$$ = constant::false_instance();
	      }
	    | subformula OP_R subformula
	      { $$ = binop::instance(binop::R, $1, $3); }
	    | subformula OP_R error
	      {
		destroy($1);
		error_list.push_back(parse_error(@2,
				     "missing right operand for OP_R"));
		$$ = constant::false_instance();
	      }
	    | OP_F subformula
	      { $$ = unop::instance(unop::F, $2); }
	    | OP_G subformula
	      { $$ = unop::instance(unop::G, $2); }
	    | OP_X subformula
	      { $$ = unop::instance(unop::X, $2); }
//	    | subformula many_errors
//              { error_list->push_back(parse_error(@2,
//		  "ignoring these unexpected trailing tokens"));
//	        $$ = $1;
//	      }

;

%%

void
yy::Parser::print_()
{
  if (looka_ == ATOMIC_PROP)
    YYCDEBUG << " '" << *value.str << "'";
}

void
yy::Parser::error_()
{
  error_list.push_back(parse_error(location, message));
}

namespace spot
{
  namespace ltl
  {
    formula*
    parse(const std::string& ltl_string,
	  parse_error_list& error_list,
	  environment& env,
	  bool debug)
    {
      formula* result = 0;
      flex_set_buffer(ltl_string.c_str());
      yy::Parser parser(debug, yy::Location(), error_list, env, result);
      parser.parse();
      return result;
    }
  }
}

// Local Variables:
// mode: c++
// End:
