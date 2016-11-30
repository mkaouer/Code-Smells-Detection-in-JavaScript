/* -*- coding: utf-8 -*-
** Copyright (C) 2009, 2010, 2011, 2012, 2013, 2014 Laboratoire de
** Recherche et Développement de l'Epita (LRDE).
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
%name-prefix "ltlyy"
%debug
%error-verbose
%expect 0
%lex-param { spot::ltl::parse_error_list& error_list }
%define api.location.type "spot::location"

%code requires
{
#include <string>
#include "public.hh"
#include "ltlast/allnodes.hh"
#include "ltlvisit/tostring.hh"

  struct minmax_t { unsigned min, max; };
}

%parse-param {spot::ltl::parse_error_list &error_list}
%parse-param {spot::ltl::environment &parse_environment}
%parse-param {const spot::ltl::formula* &result}
%union
{
  std::string* str;
  const spot::ltl::formula* ltl;
  unsigned num;
  minmax_t minmax;
}

%code {
/* ltlparse.hh and parsedecl.hh include each other recursively.
   We mut ensure that YYSTYPE is declared (by the above %union)
   before parsedecl.hh uses it. */
#include "parsedecl.hh"
using namespace spot::ltl;

#define missing_right_op_msg(op, str)		\
  error_list.push_back(parse_error(op,		\
    "missing right operand for \"" str "\""));

#define missing_right_op(res, op, str)		\
  do						\
    {						\
      missing_right_op_msg(op, str);		\
      res = constant::false_instance();		\
    }						\
  while (0);

// right is missing, so complain and use left.
#define missing_right_binop(res, left, op, str)	\
  do						\
    {						\
      missing_right_op_msg(op, str);		\
      res = left;				\
    }						\
  while (0);

// right is missing, so complain and use false.
#define missing_right_binop_hard(res, left, op, str)	\
  do							\
    {							\
      left->destroy();					\
      missing_right_op(res, op, str);			\
    }							\
  while (0);

  enum parser_type { parser_ltl, parser_bool, parser_sere };

  const formula*
  try_recursive_parse(const std::string& str,
		      const spot::location& location,
		      spot::ltl::environment& env,
		      bool debug,
		      parser_type type,
		      spot::ltl::parse_error_list& error_list)
    {
      // We want to parse a U (b U c) as two until operators applied
      // to the atomic propositions a, b, and c.  We also want to
      // parse a U (b == c) as one until operator applied to the
      // atomic propositions "a" and "b == c".  The only problem is
      // that we do not know anything about "==" or in general about
      // the syntax of atomic proposition of our users.
      //
      // To support that, the lexer will return "b U c" and "b == c"
      // as PAR_BLOCK tokens.  We then try to parse such tokens
      // recursively.  If, as in the case of "b U c", the block is
      // successfully parsed as a formula, we return this formula.
      // Otherwise, we convert the string into an atomic proposition
      // (it's up to the environment to check the syntax of this
      // proposition, and maybe reject it).

      if (str.empty())
	{
	  error_list.push_back(parse_error(location,
					   "unexpected empty block"));
	  return 0;
	}

      spot::ltl::parse_error_list suberror;
      const spot::ltl::formula* f = 0;
      switch (type)
	{
	case parser_sere:
	  f = spot::ltl::parse_sere(str, suberror, env, debug, true);
	  break;
	case parser_bool:
	  f = spot::ltl::parse_boolean(str, suberror, env, debug, true);
	  break;
	case parser_ltl:
	  f = spot::ltl::parse(str, suberror, env, debug, true);
	  break;
	}

      if (suberror.empty())
	return f;

      if (f)
	f->destroy();

      f = env.require(str);
      if (!f)
	{
	  std::string s = "atomic proposition `";
	  s += str;
	  s += "' rejected by environment `";
	  s += env.name();
	  s += "'";
	  error_list.push_back(parse_error(location, s));
	}
      return f;
    }
}


/* All tokens.  */

%token START_LTL "LTL start marker"
%token START_LBT "LBT start marker"
%token START_SERE "SERE start marker"
%token START_BOOL "BOOLEAN start marker"
%token PAR_OPEN "opening parenthesis" PAR_CLOSE "closing parenthesis"
%token <str> PAR_BLOCK "(...) block"
%token <str> BRA_BLOCK "{...} block"
%token <str> BRA_BANG_BLOCK "{...}! block"
%token BRACE_OPEN "opening brace" BRACE_CLOSE "closing brace"
%token BRACE_BANG_CLOSE "closing brace-bang"
%token OP_OR "or operator" OP_XOR "xor operator"
%token OP_AND "and operator" OP_SHORT_AND "short and operator"
%token OP_IMPLIES "implication operator" OP_EQUIV "equivalent operator"
%token OP_U "until operator" OP_R "release operator"
%token OP_W "weak until operator" OP_M "strong release operator"
%token OP_F "sometimes operator" OP_G "always operator"
%token OP_X "next operator" OP_NOT "not operator"
%token OP_STAR "star operator" OP_BSTAR "bracket star operator"
%token OP_PLUS "plus operator"
%token OP_STAR_OPEN "opening bracket for star operator"
%token OP_EQUAL_OPEN "opening bracket for equal operator"
%token OP_GOTO_OPEN "opening bracket for goto operator"
%token OP_SQBKT_CLOSE "closing bracket"
%token <num> OP_SQBKT_NUM "number for square bracket operator"
%token OP_UNBOUNDED "unbounded mark"
%token OP_SQBKT_SEP "separator for square bracket operator"
%token OP_UCONCAT "universal concat operator"
%token OP_ECONCAT "existential concat operator"
%token OP_UCONCAT_NONO "universal non-overlapping concat operator"
%token OP_ECONCAT_NONO "existential non-overlapping concat operator"
%token <str> ATOMIC_PROP "atomic proposition"
%token OP_CONCAT "concat operator" OP_FUSION "fusion operator"
%token CONST_TRUE "constant true" CONST_FALSE "constant false"
%token END_OF_INPUT "end of formula"
%token OP_POST_NEG "negative suffix" OP_POST_POS "positive suffix"

/* Priorities.  */

/* Low priority SERE-LTL binding operator. */
%nonassoc OP_UCONCAT OP_ECONCAT OP_UCONCAT_NONO OP_ECONCAT_NONO

%left OP_CONCAT
%left OP_FUSION

/* Logical operators.  */
%right OP_IMPLIES OP_EQUIV
%left OP_OR
%left OP_XOR
%left OP_AND OP_SHORT_AND

/* OP_STAR can be used as an AND when occurring in some LTL formula in
   Wring's syntax (so it has to be close to OP_AND), and as a Kleen
   Star in SERE (so it has to be close to OP_BSTAR -- luckily
   U/R/M/W/F/G/X are not used in SERE). */
%left OP_STAR

/* LTL operators.  */
%right OP_U OP_R OP_M OP_W
%nonassoc OP_F OP_G
%nonassoc OP_X

/* High priority regex operator. */
%nonassoc OP_BSTAR OP_STAR_OPEN OP_PLUS OP_EQUAL_OPEN OP_GOTO_OPEN

/* Not has the most important priority (after Wring's `=0' and `=1',
   but as those can only attach to atomic proposition, they do not
   need any precedence).  */
%nonassoc OP_NOT

%type <ltl> subformula booleanatom sere lbtformula boolformula
%type <ltl> bracedsere parenthesedsubformula
%type <minmax> starargs equalargs sqbracketargs gotoargs

%destructor { delete $$; } <str>
%destructor { $$->destroy(); } <ltl>

%printer { debug_stream() << *$$; } <str>
%printer { spot::ltl::to_string($$, debug_stream()); } <ltl>
%printer { spot::ltl::to_string($$, debug_stream(), false, true); } sere bracedsere
%printer { debug_stream() << $$; } <num>
%printer { debug_stream() << $$.min << ".." << $$.max; } <minmax>

%%
result:       START_LTL subformula END_OF_INPUT
	      { result = $2;
		YYACCEPT;
	      }
	    | START_LTL enderror
	      {
		result = 0;
		YYABORT;
	      }
	    | START_LTL subformula enderror
	      {
		result = $2;
		YYACCEPT;
	      }
	    | START_LTL emptyinput
              { YYABORT; }
            | START_BOOL boolformula END_OF_INPUT
	      { result = $2;
		YYACCEPT;
	      }
	    | START_BOOL enderror
	      {
		result = 0;
		YYABORT;
	      }
	    | START_BOOL boolformula enderror
	      {
		result = $2;
		YYACCEPT;
	      }
	    | START_BOOL emptyinput
              { YYABORT; }
            | START_SERE sere END_OF_INPUT
	      { result = $2;
		YYACCEPT;
	      }
	    | START_SERE enderror
	      {
		result = 0;
		YYABORT;
	      }
	    | START_SERE sere enderror
	      {
		result = $2;
		YYACCEPT;
	      }
	    | START_SERE emptyinput
              { YYABORT; }
            | START_LBT lbtformula END_OF_INPUT
	      { result = $2;
		YYACCEPT;
	      }
	    | START_LBT enderror
	      {
		result = 0;
		YYABORT;
	      }
	    | START_LBT lbtformula enderror
	      {
		result = $2;
		YYACCEPT;
	      }
	    | START_LBT emptyinput
              { YYABORT; }

emptyinput: END_OF_INPUT
              {
		error_list.push_back(parse_error(@$, "empty input"));
		result = 0;
	      }

enderror: error END_OF_INPUT
              {
		error_list.push_back(parse_error(@1,
						 "ignoring trailing garbage"));
	      }


OP_SQBKT_SEP_unbounded: OP_SQBKT_SEP | OP_SQBKT_SEP OP_UNBOUNDED
OP_SQBKT_SEP_opt: | OP_SQBKT_SEP_unbounded
error_opt: | error

/* for [*i..j] and [=i..j] */
sqbracketargs: OP_SQBKT_NUM OP_SQBKT_SEP OP_SQBKT_NUM OP_SQBKT_CLOSE
              { $$.min = $1; $$.max = $3; }
	     | OP_SQBKT_NUM OP_SQBKT_SEP_unbounded OP_SQBKT_CLOSE
              { $$.min = $1; $$.max = bunop::unbounded; }
	     | OP_SQBKT_SEP OP_SQBKT_NUM OP_SQBKT_CLOSE
              { $$.min = 0U; $$.max = $2; }
	     | OP_SQBKT_SEP_opt OP_SQBKT_CLOSE
              { $$.min = 0U; $$.max = bunop::unbounded; }
	     | OP_SQBKT_NUM OP_SQBKT_CLOSE
              { $$.min = $$.max = $1; }

/* [->i..j] has default values that are different than [*] and [=]. */
gotoargs: OP_GOTO_OPEN OP_SQBKT_NUM OP_SQBKT_SEP OP_SQBKT_NUM OP_SQBKT_CLOSE
           { $$.min = $2; $$.max = $4; }
	  | OP_GOTO_OPEN OP_SQBKT_NUM OP_SQBKT_SEP_unbounded OP_SQBKT_CLOSE
           { $$.min = $2; $$.max = bunop::unbounded; }
	  | OP_GOTO_OPEN OP_SQBKT_SEP OP_SQBKT_NUM OP_SQBKT_CLOSE
           { $$.min = 1U; $$.max = $3; }
	  | OP_GOTO_OPEN OP_SQBKT_SEP_unbounded OP_SQBKT_CLOSE
           { $$.min = 1U; $$.max = bunop::unbounded; }
	  | OP_GOTO_OPEN OP_SQBKT_CLOSE
           { $$.min = $$.max = 1U; }
	  | OP_GOTO_OPEN OP_SQBKT_NUM OP_SQBKT_CLOSE
           { $$.min = $$.max = $2; }
	  | OP_GOTO_OPEN error OP_SQBKT_CLOSE
           { error_list.push_back(parse_error(@$,
	       "treating this goto block as [->]"));
             $$.min = $$.max = 1U; }
          | OP_GOTO_OPEN error_opt END_OF_INPUT
	   { error_list.push_back(parse_error(@$,
               "missing closing bracket for goto operator"));
	     $$.min = $$.max = 0U; }

kleen_star: OP_STAR | OP_BSTAR

starargs: kleen_star
            { $$.min = 0U; $$.max = bunop::unbounded; }
        | OP_PLUS
	    { $$.min = 1U; $$.max = bunop::unbounded; }
	| OP_STAR_OPEN sqbracketargs
	    { $$ = $2; }
	| OP_STAR_OPEN error OP_SQBKT_CLOSE
            { error_list.push_back(parse_error(@$,
		"treating this star block as [*]"));
              $$.min = 0U; $$.max = bunop::unbounded; }
        | OP_STAR_OPEN error_opt END_OF_INPUT
	    { error_list.push_back(parse_error(@$,
                "missing closing bracket for star"));
	      $$.min = $$.max = 0U; }

equalargs: OP_EQUAL_OPEN sqbracketargs
	    { $$ = $2; }
	| OP_EQUAL_OPEN error OP_SQBKT_CLOSE
            { error_list.push_back(parse_error(@$,
		"treating this equal block as [*]"));
              $$.min = 0U; $$.max = bunop::unbounded; }
        | OP_EQUAL_OPEN error_opt END_OF_INPUT
	    { error_list.push_back(parse_error(@$,
                "missing closing bracket for equal operator"));
	      $$.min = $$.max = 0U; }


/* The reason we use `constant::false_instance()' for error recovery
   is that it isn't reference counted.  (Hence it can't leak references.)  */

booleanatom: ATOMIC_PROP
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
	    | ATOMIC_PROP OP_POST_POS
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
	    | ATOMIC_PROP OP_POST_NEG
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
		$$ = unop::instance(unop::Not, $$);
	      }
	    | CONST_TRUE
	      { $$ = constant::true_instance(); }
	    | CONST_FALSE
	      { $$ = constant::false_instance(); }

sere: booleanatom
            | OP_NOT sere
	      {
		if ($2->is_boolean())
		  {
		    $$ = unop::instance(unop::Not, $2);
		  }
		else
		  {
		    error_list.push_back(parse_error(@2,
                       "not a boolean expression: inside a SERE `!' can only "
                       "be applied to a Boolean expression"));
		    error_list.push_back(parse_error(@$,
				"treating this block as false"));
		    $2->destroy();
		    $$ = constant::false_instance();
		  }
	      }
            | bracedsere
	    | PAR_BLOCK
              {
		$$ = try_recursive_parse(*$1, @1, parse_environment,
					 debug_level(), parser_sere, error_list);
		delete $1;
		if (!$$)
		  YYERROR;
	      }
	    | PAR_OPEN sere PAR_CLOSE
	      { $$ = $2; }
	    | PAR_OPEN error PAR_CLOSE
	      { error_list.push_back(parse_error(@$,
		 "treating this parenthetical block as false"));
		$$ = constant::false_instance();
	      }
	    | PAR_OPEN sere END_OF_INPUT
	      { error_list.push_back(parse_error(@1 + @2,
				      "missing closing parenthesis"));
		$$ = $2;
	      }
	    | PAR_OPEN error END_OF_INPUT
	      { error_list.push_back(parse_error(@$,
                    "missing closing parenthesis, "
		    "treating this parenthetical block as false"));
		$$ = constant::false_instance();
	      }
	    | sere OP_AND sere
	      { $$ = multop::instance(multop::AndRat, $1, $3); }
	    | sere OP_AND error
	      { missing_right_binop($$, $1, @2,
				    "length-matching and operator"); }
	    | sere OP_SHORT_AND sere
	      { $$ = multop::instance(multop::AndNLM, $1, $3); }
	    | sere OP_SHORT_AND error
              { missing_right_binop($$, $1, @2,
                                    "non-length-matching and operator"); }
	    | sere OP_OR sere
	      { $$ = multop::instance(multop::OrRat, $1, $3); }
	    | sere OP_OR error
              { missing_right_binop($$, $1, @2, "or operator"); }
	    | sere OP_CONCAT sere
	      { $$ = multop::instance(multop::Concat, $1, $3); }
	    | sere OP_CONCAT error
              { missing_right_binop($$, $1, @2, "concat operator"); }
	    | sere OP_FUSION sere
	      { $$ = multop::instance(multop::Fusion, $1, $3); }
	    | sere OP_FUSION error
              { missing_right_binop($$, $1, @2, "fusion operator"); }
	    | sere starargs
	      {
		if ($2.max < $2.min)
		  {
		    error_list.push_back(parse_error(@2, "reversed range"));
		    std::swap($2.max, $2.min);
		  }
		$$ = bunop::instance(bunop::Star, $1, $2.min, $2.max);
	      }
	    | starargs
	      {
		if ($1.max < $1.min)
		  {
		    error_list.push_back(parse_error(@1, "reversed range"));
		    std::swap($1.max, $1.min);
		  }
		$$ = bunop::instance(bunop::Star, constant::true_instance(),
				     $1.min, $1.max);
	      }
	    | sere equalargs
	      {
		if ($2.max < $2.min)
		  {
		    error_list.push_back(parse_error(@2, "reversed range"));
		    std::swap($2.max, $2.min);
		  }
		if ($1->is_boolean())
		  {
		    $$ = bunop::sugar_equal($1, $2.min, $2.max);
		  }
		else
		  {
		    error_list.push_back(parse_error(@1,
				"not a boolean expression: [=...] can only "
				"be applied to a Boolean expression"));
		    error_list.push_back(parse_error(@$,
				"treating this block as false"));
		    $1->destroy();
		    $$ = constant::false_instance();
		  }
	      }
	    | sere gotoargs
	      {
		if ($2.max < $2.min)
		  {
		    error_list.push_back(parse_error(@2, "reversed range"));
		    std::swap($2.max, $2.min);
		  }
		if ($1->is_boolean())
		  {
		    $$ = bunop::sugar_goto($1, $2.min, $2.max);
		  }
		else
		  {
		    error_list.push_back(parse_error(@1,
				"not a boolean expression: [->...] can only "
				"be applied to a Boolean expression"));
		    error_list.push_back(parse_error(@$,
				"treating this block as false"));
		    $1->destroy();
		    $$ = constant::false_instance();
		  }
	      }
	    | sere OP_XOR sere
	      {
		if ($1->is_boolean() && $3->is_boolean())
		  {
		    $$ = binop::instance(binop::Xor, $1, $3);
		  }
		else
		  {
		    if (!$1->is_boolean())
		      {
			error_list.push_back(parse_error(@1,
                         "not a boolean expression: inside SERE `<->' can only "
                         "be applied to Boolean expressions"));
                      }
		    if (!$3->is_boolean())
		      {
			error_list.push_back(parse_error(@3,
                         "not a boolean expression: inside SERE `<->' can only "
                         "be applied to Boolean expressions"));
                      }
		    error_list.push_back(parse_error(@$,
				"treating this block as false"));
		    $1->destroy();
		    $3->destroy();
		    $$ = constant::false_instance();
		  }
	      }
	    | sere OP_XOR error
	      { missing_right_binop($$, $1, @2, "xor operator"); }
	    | sere OP_IMPLIES sere
	      {
		if ($1->is_boolean())
		  {
		    $$ = binop::instance(binop::Implies, $1, $3);
		  }
		else
		  {
		    if (!$1->is_boolean())
		      {
			error_list.push_back(parse_error(@1,
                         "not a boolean expression: inside SERE `->' can only "
                         "be applied to a Boolean expression"));
                      }
		    error_list.push_back(parse_error(@$,
				"treating this block as false"));
		    $1->destroy();
		    $3->destroy();
		    $$ = constant::false_instance();
		  }
	      }
	    | sere OP_IMPLIES error
	      { missing_right_binop($$, $1, @2, "implication operator"); }
	    | sere OP_EQUIV sere
	      {
		if ($1->is_boolean() && $3->is_boolean())
		  {
		    $$ = binop::instance(binop::Equiv, $1, $3);
		  }
		else
		  {
		    if (!$1->is_boolean())
		      {
			error_list.push_back(parse_error(@1,
                         "not a boolean expression: inside SERE `<->' can only "
                         "be applied to Boolean expressions"));
                      }
		    if (!$3->is_boolean())
		      {
			error_list.push_back(parse_error(@3,
                         "not a boolean expression: inside SERE `<->' can only "
                         "be applied to Boolean expressions"));
                      }
		    error_list.push_back(parse_error(@$,
				"treating this block as false"));
		    $1->destroy();
		    $3->destroy();
		    $$ = constant::false_instance();
		  }
	      }
	    | sere OP_EQUIV error
	      { missing_right_binop($$, $1, @2, "equivalent operator"); }

bracedsere: BRACE_OPEN sere BRACE_CLOSE
              { $$ = $2; }
            | BRACE_OPEN sere error BRACE_CLOSE
	      { error_list.push_back(parse_error(@3, "ignoring this"));
		$$ = $2;
	      }
            | BRACE_OPEN error BRACE_CLOSE
	      { error_list.push_back(parse_error(@$,
		 "treating this brace block as false"));
		$$ = constant::false_instance();
	      }
            | BRACE_OPEN sere END_OF_INPUT
	      { error_list.push_back(parse_error(@1 + @2,
				      "missing closing brace"));
		$$ = $2;
	      }
	    | BRACE_OPEN sere error END_OF_INPUT
	      { error_list.push_back(parse_error(@3,
                "ignoring trailing garbage and missing closing brace"));
		$$ = $2;
	      }
	    | BRACE_OPEN error END_OF_INPUT
	      { error_list.push_back(parse_error(@$,
                    "missing closing brace, "
		    "treating this brace block as false"));
		$$ = constant::false_instance();
	      }
            | BRA_BLOCK
              {
		$$ = try_recursive_parse(*$1, @1, parse_environment,
					 debug_level(),
					 parser_sere, error_list);
		delete $1;
		if (!$$)
		  YYERROR;
	      }

parenthesedsubformula: PAR_BLOCK
              {
		$$ = try_recursive_parse(*$1, @1, parse_environment,
					 debug_level(), parser_ltl, error_list);
		delete $1;
		if (!$$)
		  YYERROR;
	      }
            | PAR_OPEN subformula PAR_CLOSE
	      { $$ = $2; }
	    | PAR_OPEN subformula error PAR_CLOSE
	      { error_list.push_back(parse_error(@3, "ignoring this"));
		$$ = $2;
	      }
	    | PAR_OPEN error PAR_CLOSE
	      { error_list.push_back(parse_error(@$,
		 "treating this parenthetical block as false"));
		$$ = constant::false_instance();
	      }
	    | PAR_OPEN subformula END_OF_INPUT
	      { error_list.push_back(parse_error(@1 + @2,
				      "missing closing parenthesis"));
		$$ = $2;
	      }
	    | PAR_OPEN subformula error END_OF_INPUT
	      { error_list.push_back(parse_error(@3,
                "ignoring trailing garbage and missing closing parenthesis"));
		$$ = $2;
	      }
	    | PAR_OPEN error END_OF_INPUT
	      { error_list.push_back(parse_error(@$,
                    "missing closing parenthesis, "
		    "treating this parenthetical block as false"));
		$$ = constant::false_instance();
	      }


boolformula: booleanatom
            | PAR_BLOCK
              {
		$$ = try_recursive_parse(*$1, @1, parse_environment,
					 debug_level(), parser_bool, error_list);
		delete $1;
		if (!$$)
		  YYERROR;
	      }
            | PAR_OPEN boolformula PAR_CLOSE
	      { $$ = $2; }
	    | PAR_OPEN boolformula error PAR_CLOSE
	      { error_list.push_back(parse_error(@3, "ignoring this"));
		$$ = $2;
	      }
	    | PAR_OPEN error PAR_CLOSE
	      { error_list.push_back(parse_error(@$,
		 "treating this parenthetical block as false"));
		$$ = constant::false_instance();
	      }
	    | PAR_OPEN boolformula END_OF_INPUT
	      { error_list.push_back(parse_error(@1 + @2,
				      "missing closing parenthesis"));
		$$ = $2;
	      }
	    | PAR_OPEN boolformula error END_OF_INPUT
	      { error_list.push_back(parse_error(@3,
                "ignoring trailing garbage and missing closing parenthesis"));
		$$ = $2;
	      }
	    | PAR_OPEN error END_OF_INPUT
	      { error_list.push_back(parse_error(@$,
                    "missing closing parenthesis, "
		    "treating this parenthetical block as false"));
		$$ = constant::false_instance();
	      }
	    | boolformula OP_AND boolformula
	      { $$ = multop::instance(multop::And, $1, $3); }
	    | boolformula OP_AND error
              { missing_right_binop($$, $1, @2, "and operator"); }
	    | boolformula OP_SHORT_AND boolformula
	      { $$ = multop::instance(multop::And, $1, $3); }
	    | boolformula OP_SHORT_AND error
              { missing_right_binop($$, $1, @2, "and operator"); }
	    | boolformula OP_STAR boolformula
	      { $$ = multop::instance(multop::And, $1, $3); }
	    | boolformula OP_STAR error
              { missing_right_binop($$, $1, @2, "and operator"); }
	    | boolformula OP_OR boolformula
	      { $$ = multop::instance(multop::Or, $1, $3); }
	    | boolformula OP_OR error
              { missing_right_binop($$, $1, @2, "or operator"); }
	    | boolformula OP_XOR boolformula
	      { $$ = binop::instance(binop::Xor, $1, $3); }
	    | boolformula OP_XOR error
	      { missing_right_binop($$, $1, @2, "xor operator"); }
	    | boolformula OP_IMPLIES boolformula
	      { $$ = binop::instance(binop::Implies, $1, $3); }
	    | boolformula OP_IMPLIES error
	      { missing_right_binop($$, $1, @2, "implication operator"); }
	    | boolformula OP_EQUIV boolformula
	      { $$ = binop::instance(binop::Equiv, $1, $3); }
	    | boolformula OP_EQUIV error
	      { missing_right_binop($$, $1, @2, "equivalent operator"); }
	    | OP_NOT boolformula
	      { $$ = unop::instance(unop::Not, $2); }
	    | OP_NOT error
	      { missing_right_op($$, @1, "not operator"); }

subformula: booleanatom
            | parenthesedsubformula
	    | subformula OP_AND subformula
	      { $$ = multop::instance(multop::And, $1, $3); }
	    | subformula OP_AND error
              { missing_right_binop($$, $1, @2, "and operator"); }
	    | subformula OP_SHORT_AND subformula
	      { $$ = multop::instance(multop::And, $1, $3); }
	    | subformula OP_SHORT_AND error
              { missing_right_binop($$, $1, @2, "and operator"); }
	    | subformula OP_STAR subformula
	      { $$ = multop::instance(multop::And, $1, $3); }
	    | subformula OP_STAR error
              { missing_right_binop($$, $1, @2, "and operator"); }
	    | subformula OP_OR subformula
	      { $$ = multop::instance(multop::Or, $1, $3); }
	    | subformula OP_OR error
              { missing_right_binop($$, $1, @2, "or operator"); }
	    | subformula OP_XOR subformula
	      { $$ = binop::instance(binop::Xor, $1, $3); }
	    | subformula OP_XOR error
	      { missing_right_binop($$, $1, @2, "xor operator"); }
	    | subformula OP_IMPLIES subformula
	      { $$ = binop::instance(binop::Implies, $1, $3); }
	    | subformula OP_IMPLIES error
	      { missing_right_binop($$, $1, @2, "implication operator"); }
	    | subformula OP_EQUIV subformula
	      { $$ = binop::instance(binop::Equiv, $1, $3); }
	    | subformula OP_EQUIV error
	      { missing_right_binop($$, $1, @2, "equivalent operator"); }
	    | subformula OP_U subformula
	      { $$ = binop::instance(binop::U, $1, $3); }
	    | subformula OP_U error
	      { missing_right_binop($$, $1, @2, "until operator"); }
	    | subformula OP_R subformula
	      { $$ = binop::instance(binop::R, $1, $3); }
	    | subformula OP_R error
	      { missing_right_binop($$, $1, @2, "release operator"); }
	    | subformula OP_W subformula
	      { $$ = binop::instance(binop::W, $1, $3); }
	    | subformula OP_W error
	      { missing_right_binop($$, $1, @2, "weak until operator"); }
	    | subformula OP_M subformula
	      { $$ = binop::instance(binop::M, $1, $3); }
	    | subformula OP_M error
	      { missing_right_binop($$, $1, @2, "strong release operator"); }
	    | OP_F subformula
	      { $$ = unop::instance(unop::F, $2); }
	    | OP_F error
	      { missing_right_op($$, @1, "sometimes operator"); }
	    | OP_G subformula
	      { $$ = unop::instance(unop::G, $2); }
	    | OP_G error
	      { missing_right_op($$, @1, "always operator"); }
	    | OP_X subformula
	      { $$ = unop::instance(unop::X, $2); }
	    | OP_X error
	      { missing_right_op($$, @1, "next operator"); }
	    | OP_NOT subformula
	      { $$ = unop::instance(unop::Not, $2); }
	    | OP_NOT error
	      { missing_right_op($$, @1, "not operator"); }
            | bracedsere
	      { $$ = unop::instance(unop::Closure, $1); }
            | bracedsere OP_UCONCAT subformula
	      { $$ = binop::instance(binop::UConcat, $1, $3); }
            | bracedsere parenthesedsubformula
	      { $$ = binop::instance(binop::UConcat, $1, $2); }
            | bracedsere OP_UCONCAT error
	      { missing_right_binop_hard($$, $1, @2,
				    "universal overlapping concat operator"); }
            | bracedsere OP_ECONCAT subformula
	      { $$ = binop::instance(binop::EConcat, $1, $3); }
            | bracedsere OP_ECONCAT error
	      { missing_right_binop_hard($$, $1, @2,
				    "existential overlapping concat operator");
	      }
            | bracedsere OP_UCONCAT_NONO subformula
	      /* {SERE}[]=>EXP = {SERE;1}[]->EXP */
	      { $$ = binop::instance(binop::UConcat,
		       multop::instance(multop::Concat, $1,
					constant::true_instance()), $3);
	      }
            | bracedsere OP_UCONCAT_NONO error
	      { missing_right_binop_hard($$, $1, @2,
				  "universal non-overlapping concat operator");
	      }
            | bracedsere OP_ECONCAT_NONO subformula
	      /* {SERE}<>=>EXP = {SERE;1}<>->EXP */
	      { $$ = binop::instance(binop::EConcat,
		       multop::instance(multop::Concat, $1,
					constant::true_instance()), $3);
	      }
            | bracedsere OP_ECONCAT_NONO error
	      { missing_right_binop_hard($$, $1, @2,
				"existential non-overlapping concat operator");
	      }
            | BRACE_OPEN sere BRACE_BANG_CLOSE
	      /* {SERE}! = {SERE} <>-> 1 */
	      { $$ = binop::instance(binop::EConcat, $2,
				     constant::true_instance()); }
            | BRA_BANG_BLOCK
              {
		$$ = try_recursive_parse(*$1, @1, parse_environment,
					 debug_level(), parser_sere, error_list);
		delete $1;
		if (!$$)
		  YYERROR;
		$$ = binop::instance(binop::EConcat, $$,
				     constant::true_instance());
	      }

lbtformula: ATOMIC_PROP
	      {
		$$ = parse_environment.require(*$1);
		if (! $$)
		  {
		    std::string s = "atomic proposition `";
		    s += *$1;
		    s += "' rejected by environment `";
		    s += parse_environment.name();
		    s += "'";
		    error_list.push_back(parse_error(@1, s));
		    delete $1;
		    YYERROR;
		  }
		else
		  delete $1;
	      }
            | '!' lbtformula
	      { $$ = unop::instance(unop::Not, $2); }
            | '&' lbtformula lbtformula
	      { $$ = multop::instance(multop::And, $2, $3); }
            | '|' lbtformula lbtformula
	      { $$ = multop::instance(multop::Or, $2, $3); }
            | '^' lbtformula lbtformula
	      { $$ = binop::instance(binop::Xor, $2, $3); }
            | 'i' lbtformula lbtformula
	      { $$ = binop::instance(binop::Implies, $2, $3); }
            | 'e' lbtformula lbtformula
	      { $$ = binop::instance(binop::Equiv, $2, $3); }
            | 'X' lbtformula
	      { $$ = unop::instance(unop::X, $2); }
            | 'F' lbtformula
	      { $$ = unop::instance(unop::F, $2); }
            | 'G' lbtformula
	      { $$ = unop::instance(unop::G, $2); }
            | 'U' lbtformula lbtformula
	      { $$ = binop::instance(binop::U, $2, $3); }
            | 'V' lbtformula lbtformula
	      { $$ = binop::instance(binop::R, $2, $3); }
            | 'R' lbtformula lbtformula
	      { $$ = binop::instance(binop::R, $2, $3); }
            | 'W' lbtformula lbtformula
	      { $$ = binop::instance(binop::W, $2, $3); }
            | 'M' lbtformula lbtformula
	      { $$ = binop::instance(binop::M, $2, $3); }
            | 't'
	      { $$ = constant::true_instance(); }
            | 'f'
	      { $$ = constant::false_instance(); }
            ;

%%

void
ltlyy::parser::error(const location_type& location, const std::string& message)
{
  error_list.push_back(parse_error(location, message));
}

namespace spot
{
  namespace ltl
  {
    const formula*
    parse(const std::string& ltl_string,
	  parse_error_list& error_list,
	  environment& env,
	  bool debug, bool lenient)
    {
      const formula* result = 0;
      flex_set_buffer(ltl_string,
		      ltlyy::parser::token::START_LTL,
		      lenient);
      ltlyy::parser parser(error_list, env, result);
      parser.set_debug_level(debug);
      parser.parse();
      flex_unset_buffer();
      return result;
    }

    const formula*
    parse_boolean(const std::string& ltl_string,
		  parse_error_list& error_list,
		  environment& env,
		  bool debug, bool lenient)
    {
      const formula* result = 0;
      flex_set_buffer(ltl_string,
		      ltlyy::parser::token::START_BOOL,
		      lenient);
      ltlyy::parser parser(error_list, env, result);
      parser.set_debug_level(debug);
      parser.parse();
      flex_unset_buffer();
      return result;
    }

    const formula*
    parse_lbt(const std::string& ltl_string,
	  parse_error_list& error_list,
	  environment& env,
	  bool debug)
    {
      const formula* result = 0;
      flex_set_buffer(ltl_string,
		      ltlyy::parser::token::START_LBT,
		      false);
      ltlyy::parser parser(error_list, env, result);
      parser.set_debug_level(debug);
      parser.parse();
      flex_unset_buffer();
      return result;
    }

    const formula*
    parse_sere(const std::string& sere_string,
	       parse_error_list& error_list,
	       environment& env,
	       bool debug,
	       bool lenient)
    {
      const formula* result = 0;
      flex_set_buffer(sere_string,
		      ltlyy::parser::token::START_SERE,
		      lenient);
      ltlyy::parser parser(error_list, env, result);
      parser.set_debug_level(debug);
      parser.parse();
      flex_unset_buffer();
      return result;
    }

  }
}

// Local Variables:
// mode: c++
// End:
