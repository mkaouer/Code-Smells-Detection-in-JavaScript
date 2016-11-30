/* Copyright (C) 2008, 2009, 2010, 2011 Laboratoire de Recherche et
** Développement de l'Epita (LRDE).
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
%name-prefix "eltlyy"
%debug
%error-verbose

%code requires
{
#include <string>
#include <sstream>
#include <limits>
#include <cerrno>
#include <algorithm>
#include <boost/shared_ptr.hpp>
#include "public.hh"
#include "ltlast/allnodes.hh"
#include "ltlast/formula_tree.hh"

namespace spot
{
  namespace eltl
  {
    typedef std::map<std::string, nfa::ptr> nfamap;

    /// The following parser allows one to define aliases of automaton
    /// operators such as: F=U(true,$0). Internally it's handled by
    /// creating a small AST associated with each alias in order to
    /// instanciate the right automatop after: U(constant(1), AP(f))
    /// for the formula F(f).
    typedef std::map<std::string, formula_tree::node_ptr> aliasmap;

    /// Implementation details for error handling.
    struct parse_error_list_t
    {
      parse_error_list list_;
      std::string file_;
    };
  }
}
}

%parse-param {spot::eltl::nfamap& nmap}
%parse-param {spot::eltl::aliasmap& amap}
%parse-param {spot::eltl::parse_error_list_t &pe}
%parse-param {spot::ltl::environment &parse_environment}
%parse-param {spot::ltl::formula* &result}
%lex-param {spot::eltl::parse_error_list_t &pe}
%expect 0
%pure-parser
%union
{
  int ival;
  std::string* sval;
  spot::ltl::nfa* nval;
  spot::ltl::automatop::vec* aval;
  spot::ltl::formula* fval;

  /// To handle aliases.
  spot::ltl::formula_tree::node* pval;
  spot::ltl::formula_tree::node_nfa* bval;
}

%code {
/* ltlparse.hh and parsedecl.hh include each other recursively.
   We mut ensure that YYSTYPE is declared (by the above %union)
   before parsedecl.hh uses it. */
#include "parsedecl.hh"
using namespace spot::eltl;
using namespace spot::ltl;

namespace spot
{
  namespace eltl
  {
    using namespace spot::ltl::formula_tree;

    /// Alias an existing alias, as in Strong=G(F($0))->G(F($1)),
    /// where F is an alias.
    ///
    /// \param ap The original alias.
    /// \param v The arguments of the new alias.
    static node_ptr
    realias(const node_ptr ap, std::vector<node_ptr> v)
    {
      if (node_atomic* a = dynamic_cast<node_atomic*>(ap.get())) // Do it.
    	return a->i < 0 ? ap : v.at(a->i);

      // Traverse the tree.
      if (node_unop* a = dynamic_cast<node_unop*>(ap.get()))
      {
    	node_unop* res = new node_unop;
	res->op = a->op;
    	res->child = realias(a->child, v);
    	return node_ptr(res);
      }
      if (node_nfa* a = dynamic_cast<node_nfa*>(ap.get()))
      {
    	node_nfa* res = new node_nfa;
    	std::vector<node_ptr>::const_iterator i = a->children.begin();
    	while (i != a->children.end())
    	  res->children.push_back(realias(*i++, v));
    	res->nfa = a->nfa;
    	return node_ptr(res);
      }
      if (node_binop* a = dynamic_cast<node_binop*>(ap.get()))
      {
    	node_binop* res = new node_binop;
    	res->op = a->op;
    	res->lhs = realias(a->lhs, v);
    	res->rhs = realias(a->rhs, v);
    	return node_ptr(res);
      }
      if (node_multop* a = dynamic_cast<node_multop*>(ap.get()))
      {
    	node_multop* res = new node_multop;
    	res->op = a->op;
    	res->lhs = realias(a->lhs, v);
    	res->rhs = realias(a->rhs, v);
    	return node_ptr(res);
      }

      /* Unreachable code.  */
      assert(0);
    }
  }
}

#define PARSE_ERROR(Loc, Msg)				\
  pe.list_.push_back					\
    (parse_error(Loc, spair(pe.file_, Msg)))

#define CHECK_EXISTING_NMAP(Loc, Ident)			\
  {							\
    nfamap::const_iterator i = nmap.find(*Ident);	\
    if (i == nmap.end())				\
    {							\
      std::string s = "unknown automaton operator `";	\
      s += *Ident;					\
      s += "'";						\
      PARSE_ERROR(Loc, s);				\
      delete Ident;					\
      YYERROR;						\
    }							\
  }

#define CHECK_ARITY(Loc, Ident, A1, A2)			\
  {							\
    if (A1 != A2)					\
    {							\
      std::ostringstream oss1;				\
      oss1 << A1;					\
      std::ostringstream oss2;				\
      oss2 << A2;					\
							\
      std::string s(*Ident);				\
      s += " is used with ";				\
      s += oss1.str();					\
      s += " arguments, but has an arity of ";		\
      s += oss2.str();					\
      PARSE_ERROR(Loc, s);				\
      delete Ident;					\
      YYERROR;						\
    }							\
  }

#define INSTANCIATE_OP(Name, TypeNode, TypeOp, L, R)	\
  {							\
    TypeNode* res = new TypeNode;			\
    res->op = TypeOp;					\
    res->lhs = formula_tree::node_ptr(L);		\
    res->rhs = formula_tree::node_ptr(R);		\
    Name = res;						\
  }

}

/* All tokens.  */

%token <sval>	ATOMIC_PROP "atomic proposition"
		IDENT "identifier"

%token <ival>	ARG "argument"
		STATE "state"
		OP_OR "or operator"
		OP_XOR "xor operator"
		OP_AND "and operator"
		OP_IMPLIES "implication operator"
		OP_EQUIV "equivalent operator"
		OP_NOT "not operator"

%token		ACC "accept"
		EQ "="
		FIN "finish"
		LPAREN "("
		RPAREN ")"
		COMMA ","
		END_OF_FILE "end of file"
		CONST_TRUE "constant true"
		CONST_FALSE "constant false"

/* Priorities.  */

%left OP_OR
%left OP_XOR
%left OP_AND
%left OP_IMPLIES OP_EQUIV

%left ATOMIC_PROP

%nonassoc OP_NOT

%type <nval> nfa_def
%type <fval> subformula
%type <aval> arg_list
%type <pval> nfa_arg
%type <bval> nfa_arg_list

%destructor { delete $$; } <sval>
%destructor { $$->destroy(); } <fval>

%printer { debug_stream() << *$$; } <sval>

%%

result: nfa_list subformula
	{
	  result = $2;
	  YYACCEPT;
	}
;

/* NFA definitions. */

nfa_list: /* empty */
        | nfa_list nfa
;

nfa: IDENT "=" "(" nfa_def ")"
	{
	  $4->set_name(*$1);
          nmap[*$1] = nfa::ptr($4);
	  delete $1;
        }
   | IDENT "=" nfa_arg
        {
	  /// Recursivity issues of aliases are handled by a parse error.
	  aliasmap::iterator i = amap.find(*$1);
	  if (i != amap.end())
	  {
	    std::string s = "`";
	    s += *$1;
	    s += "' is already aliased";
	    PARSE_ERROR(@1, s);
	    delete $1;
	    YYERROR;
	  }
	  amap.insert(make_pair(*$1, formula_tree::node_ptr($3)));
   	  delete $1;
   	}
;

nfa_def: /* empty */
        {
	  $$ = new nfa;
        }
        | nfa_def STATE STATE nfa_arg
        {
	  $1->add_transition($2, $3, formula_tree::node_ptr($4));
	  $$ = $1;
        }
        | nfa_def ACC STATE
        {
 	  $1->set_final($3);
	  $$ = $1;
        }
;

nfa_arg_list: nfa_arg
	{
	  $$ = new formula_tree::node_nfa;
	  $$->children.push_back(formula_tree::node_ptr($1));
	}
	| nfa_arg_list "," nfa_arg
	{
	  $1->children.push_back(formula_tree::node_ptr($3));
	  $$ = $1;
	}
;

nfa_arg: ARG
	{
	  if ($1 == -1)
	  {
	    std::string s = "out of range integer";
	    PARSE_ERROR(@1, s);
	    YYERROR;
	  }
	  formula_tree::node_atomic* res = new formula_tree::node_atomic;
	  res->i = $1;
	  $$ = res;
	}
	| CONST_TRUE
	{
	  formula_tree::node_atomic* res = new formula_tree::node_atomic;
	  res->i = formula_tree::True;
	  $$ = res;
	}
        | CONST_FALSE
	{
	  formula_tree::node_atomic* res = new formula_tree::node_atomic;
	  res->i = formula_tree::False;
	  $$ = res;
	}
	| OP_NOT nfa_arg
	{
	  formula_tree::node_unop* res = new formula_tree::node_unop;
	  res->op = unop::Not;
	  res->child = formula_tree::node_ptr($2);
	  $$ = res;
	}
	| FIN "(" nfa_arg ")"
	{
	  formula_tree::node_unop* res = new formula_tree::node_unop;
	  res->op = unop::Finish;
	  res->child = formula_tree::node_ptr($3);
	  $$ = res;
	}
	| nfa_arg OP_AND nfa_arg
	{
	  INSTANCIATE_OP($$, formula_tree::node_multop, multop::And, $1, $3);
	}
	| nfa_arg OP_OR nfa_arg
	{
	  INSTANCIATE_OP($$, formula_tree::node_multop, multop::Or, $1, $3);
	}
	| nfa_arg OP_XOR nfa_arg
	{
	  INSTANCIATE_OP($$, formula_tree::node_binop, binop::Xor, $1, $3);
	}
	| nfa_arg OP_IMPLIES nfa_arg
	{
	  INSTANCIATE_OP($$, formula_tree::node_binop, binop::Implies, $1, $3);
	}
	| nfa_arg OP_EQUIV nfa_arg
	{
	  INSTANCIATE_OP($$, formula_tree::node_binop, binop::Equiv, $1, $3);
	}
        | IDENT "(" nfa_arg_list ")"
	{
	  aliasmap::const_iterator i = amap.find(*$1);
	  if (i != amap.end())
	  {
            unsigned arity = formula_tree::arity(i->second);
	    CHECK_ARITY(@1, $1, $3->children.size(), arity);

	    // Hack to return the right type without screwing with the
	    // boost::shared_ptr memory handling by using get for
	    // example. FIXME: Wait for the next version of boost and
	    // modify the %union to handle formula_tree::node_ptr.
	    formula_tree::node_unop* tmp1 = new formula_tree::node_unop;
	    tmp1->op = unop::Not;
	    tmp1->child = realias(i->second, $3->children);
	    formula_tree::node_unop* tmp2 = new formula_tree::node_unop;
	    tmp2->op = unop::Not;
	    tmp2->child = formula_tree::node_ptr(tmp1);
	    $$ = tmp2;
	    delete $3;
	  }
	  else
	  {
	    CHECK_EXISTING_NMAP(@1, $1);
	    nfa::ptr np = nmap[*$1];

	    CHECK_ARITY(@1, $1, $3->children.size(), np->arity());
	    $3->nfa = np;
	    $$ = $3;
	  }
	  delete $1;
	}

/* Formulae. */

subformula: ATOMIC_PROP
	{
	  $$ = parse_environment.require(*$1);
	  if (!$$)
	  {
	    std::string s = "unknown atomic proposition `";
	    s += *$1;
	    s += "' in environment `";
	    s += parse_environment.name();
	    s += "'";
	    PARSE_ERROR(@1, s);
	    delete $1;
	    YYERROR;
	  }
	  else
	    delete $1;
	}
	  | subformula ATOMIC_PROP subformula
	{
	  aliasmap::iterator i = amap.find(*$2);
	  if (i != amap.end())
	  {
	    CHECK_ARITY(@1, $2, 2, formula_tree::arity(i->second));
	    automatop::vec v;
	    v.push_back($1);
	    v.push_back($3);
	    $$ = instanciate(i->second, v);
	    $1->destroy();
	    $3->destroy();
	  }
	  else
	  {
	    CHECK_EXISTING_NMAP(@1, $2);
	    nfa::ptr np = nmap[*$2];
	    CHECK_ARITY(@1, $2, 2, np->arity());
	    automatop::vec* v = new automatop::vec;
	    v->push_back($1);
	    v->push_back($3);
	    $$ = automatop::instance(np, v, false);
	  }
	  delete $2;
	}
	  | ATOMIC_PROP "(" arg_list ")"
	{
	  aliasmap::iterator i = amap.find(*$1);
	  if (i != amap.end())
	  {
	    CHECK_ARITY(@1, $1, $3->size(), formula_tree::arity(i->second));
	    $$ = instanciate(i->second, *$3);
	    automatop::vec::iterator it = $3->begin();
	    while (it != $3->end())
	      (*it++)->destroy();
	    delete $3;
	  }
	  else
	  {
	    CHECK_EXISTING_NMAP(@1, $1);
	    nfa::ptr np = nmap[*$1];

	    /// Easily handle deletion of $3 when CHECK_ARITY fails.
	    unsigned i = $3->size();
	    if ($3->size() != np->arity())
	    {
	      automatop::vec::iterator it = $3->begin();
	      while (it != $3->end())
		(*it++)->destroy();
	      delete $3;
	    }

	    CHECK_ARITY(@1, $1, i, np->arity());
	    $$ = automatop::instance(np, $3, false);
	  }
	  delete $1;
	}
	  | CONST_TRUE
	{ $$ = constant::true_instance(); }
	  | CONST_FALSE
	{ $$ = constant::false_instance(); }
          | "(" subformula ")"
	{ $$ = $2; }
          | subformula OP_AND subformula
	{ $$ = multop::instance(multop::And, $1, $3); }
          | subformula OP_OR subformula
	{ $$ = multop::instance(multop::Or, $1, $3); }
	  | subformula OP_XOR subformula
	{ $$ = binop::instance(binop::Xor, $1, $3); }
	  | subformula OP_IMPLIES subformula
        { $$ = binop::instance(binop::Implies, $1, $3); }
	  | subformula OP_EQUIV subformula
	{ $$ = binop::instance(binop::Equiv, $1, $3); }
          | OP_NOT subformula
	{ $$ = unop::instance(unop::Not, $2); }
;

arg_list: subformula
	{
	  $$ = new automatop::vec;
	  $$->push_back($1);
	}
	| arg_list "," subformula
	{
	  $1->push_back($3);
	  $$ = $1;
	}
;

%%

void
eltlyy::parser::error(const location_type& loc, const std::string& s)
{
  PARSE_ERROR(loc, s);
}

namespace spot
{
  namespace eltl
  {
    formula*
    parse_file(const std::string& name,
	       parse_error_list& error_list,
	       environment& env,
	       bool debug)
    {
      if (flex_open(name))
      {
	error_list.push_back
	  (parse_error(eltlyy::location(),
		       spair("-", std::string("Cannot open file ") + name)));
	return 0;
      }
      formula* result = 0;
      nfamap nmap;
      aliasmap amap;
      parse_error_list_t pe;
      pe.file_ = name;
      eltlyy::parser parser(nmap, amap, pe, env, result);
      parser.set_debug_level(debug);
      parser.parse();
      error_list = pe.list_;
      flex_close();
      return result;
    }

    formula*
    parse_string(const std::string& eltl_string,
		 parse_error_list& error_list,
		 environment& env,
		 bool debug)
    {
      flex_scan_string(eltl_string.c_str());
      formula* result = 0;
      nfamap nmap;
      aliasmap amap;
      parse_error_list_t pe;
      eltlyy::parser parser(nmap, amap, pe, env, result);
      parser.set_debug_level(debug);
      parser.parse();
      error_list = pe.list_;
      return result;
    }
  }
}

// Local Variables:
// mode: c++
// End:
