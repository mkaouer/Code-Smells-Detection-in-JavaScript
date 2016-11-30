/* A Bison parser, made by GNU Bison 2.4.3.  */

/* Skeleton implementation for Bison LALR(1) parsers in C++
   
      Copyright (C) 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010 Free
   Software Foundation, Inc.
   
   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.
   
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   
   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.
   
   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

// Take the name prefix into account.
#define yylex   eltlyylex

/* First part of user declarations.  */


/* Line 311 of lalr1.cc  */
#line 42 "eltlparse.cc"


#include "eltlparse.hh"

/* User implementation prologue.  */


/* Line 317 of lalr1.cc  */
#line 51 "eltlparse.cc"
/* Unqualified %code blocks.  */

/* Line 318 of lalr1.cc  */
#line 84 "eltlparse.yy"

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




/* Line 318 of lalr1.cc  */
#line 172 "eltlparse.cc"

#ifndef YY_
# if defined YYENABLE_NLS && YYENABLE_NLS
#  if ENABLE_NLS
#   include <libintl.h> /* FIXME: INFRINGES ON USER NAME SPACE */
#   define YY_(msgid) dgettext ("bison-runtime", msgid)
#  endif
# endif
# ifndef YY_
#  define YY_(msgid) msgid
# endif
#endif

/* Suppress unused-variable warnings by "using" E.  */
#define YYUSE(e) ((void) (e))

/* Enable debugging if requested.  */
#if YYDEBUG

/* A pseudo ostream that takes yydebug_ into account.  */
# define YYCDEBUG if (yydebug_) (*yycdebug_)

# define YY_SYMBOL_PRINT(Title, Type, Value, Location)	\
do {							\
  if (yydebug_)						\
    {							\
      *yycdebug_ << Title << ' ';			\
      yy_symbol_print_ ((Type), (Value), (Location));	\
      *yycdebug_ << std::endl;				\
    }							\
} while (false)

# define YY_REDUCE_PRINT(Rule)		\
do {					\
  if (yydebug_)				\
    yy_reduce_print_ (Rule);		\
} while (false)

# define YY_STACK_PRINT()		\
do {					\
  if (yydebug_)				\
    yystack_print_ ();			\
} while (false)

#else /* !YYDEBUG */

# define YYCDEBUG if (false) std::cerr
# define YY_SYMBOL_PRINT(Title, Type, Value, Location)
# define YY_REDUCE_PRINT(Rule)
# define YY_STACK_PRINT()

#endif /* !YYDEBUG */

#define yyerrok		(yyerrstatus_ = 0)
#define yyclearin	(yychar = yyempty_)

#define YYACCEPT	goto yyacceptlab
#define YYABORT		goto yyabortlab
#define YYERROR		goto yyerrorlab
#define YYRECOVERING()  (!!yyerrstatus_)


namespace eltlyy {

/* Line 380 of lalr1.cc  */
#line 238 "eltlparse.cc"
#if YYERROR_VERBOSE

  /* Return YYSTR after stripping away unnecessary quotes and
     backslashes, so that it's suitable for yyerror.  The heuristic is
     that double-quoting is unnecessary unless the string contains an
     apostrophe, a comma, or backslash (other than backslash-backslash).
     YYSTR is taken from yytname.  */
  std::string
  parser::yytnamerr_ (const char *yystr)
  {
    if (*yystr == '"')
      {
        std::string yyr = "";
        char const *yyp = yystr;

        for (;;)
          switch (*++yyp)
            {
            case '\'':
            case ',':
              goto do_not_strip_quotes;

            case '\\':
              if (*++yyp != '\\')
                goto do_not_strip_quotes;
              /* Fall through.  */
            default:
              yyr += *yyp;
              break;

            case '"':
              return yyr;
            }
      do_not_strip_quotes: ;
      }

    return yystr;
  }

#endif

  /// Build a parser object.
  parser::parser (spot::eltl::nfamap& nmap_yyarg, spot::eltl::aliasmap& amap_yyarg, spot::eltl::parse_error_list_t &pe_yyarg, spot::ltl::environment &parse_environment_yyarg, spot::ltl::formula* &result_yyarg)
    :
#if YYDEBUG
      yydebug_ (false),
      yycdebug_ (&std::cerr),
#endif
      nmap (nmap_yyarg),
      amap (amap_yyarg),
      pe (pe_yyarg),
      parse_environment (parse_environment_yyarg),
      result (result_yyarg)
  {
  }

  parser::~parser ()
  {
  }

#if YYDEBUG
  /*--------------------------------.
  | Print this symbol on YYOUTPUT.  |
  `--------------------------------*/

  inline void
  parser::yy_symbol_value_print_ (int yytype,
			   const semantic_type* yyvaluep, const location_type* yylocationp)
  {
    YYUSE (yylocationp);
    YYUSE (yyvaluep);
    switch (yytype)
      {
        case 3: /* "\"atomic proposition\"" */

/* Line 449 of lalr1.cc  */
#line 242 "eltlparse.yy"
	{ debug_stream() << *(yyvaluep->sval); };

/* Line 449 of lalr1.cc  */
#line 319 "eltlparse.cc"
	break;
      case 4: /* "\"identifier\"" */

/* Line 449 of lalr1.cc  */
#line 242 "eltlparse.yy"
	{ debug_stream() << *(yyvaluep->sval); };

/* Line 449 of lalr1.cc  */
#line 328 "eltlparse.cc"
	break;
       default:
	  break;
      }
  }


  void
  parser::yy_symbol_print_ (int yytype,
			   const semantic_type* yyvaluep, const location_type* yylocationp)
  {
    *yycdebug_ << (yytype < yyntokens_ ? "token" : "nterm")
	       << ' ' << yytname_[yytype] << " ("
	       << *yylocationp << ": ";
    yy_symbol_value_print_ (yytype, yyvaluep, yylocationp);
    *yycdebug_ << ')';
  }
#endif

  void
  parser::yydestruct_ (const char* yymsg,
			   int yytype, semantic_type* yyvaluep, location_type* yylocationp)
  {
    YYUSE (yylocationp);
    YYUSE (yymsg);
    YYUSE (yyvaluep);

    YY_SYMBOL_PRINT (yymsg, yytype, yyvaluep, yylocationp);

    switch (yytype)
      {
        case 3: /* "\"atomic proposition\"" */

/* Line 480 of lalr1.cc  */
#line 239 "eltlparse.yy"
	{ delete (yyvaluep->sval); };

/* Line 480 of lalr1.cc  */
#line 367 "eltlparse.cc"
	break;
      case 4: /* "\"identifier\"" */

/* Line 480 of lalr1.cc  */
#line 239 "eltlparse.yy"
	{ delete (yyvaluep->sval); };

/* Line 480 of lalr1.cc  */
#line 376 "eltlparse.cc"
	break;
      case 29: /* "subformula" */

/* Line 480 of lalr1.cc  */
#line 240 "eltlparse.yy"
	{ (yyvaluep->fval)->destroy(); };

/* Line 480 of lalr1.cc  */
#line 385 "eltlparse.cc"
	break;

	default:
	  break;
      }
  }

  void
  parser::yypop_ (unsigned int n)
  {
    yystate_stack_.pop (n);
    yysemantic_stack_.pop (n);
    yylocation_stack_.pop (n);
  }

#if YYDEBUG
  std::ostream&
  parser::debug_stream () const
  {
    return *yycdebug_;
  }

  void
  parser::set_debug_stream (std::ostream& o)
  {
    yycdebug_ = &o;
  }


  parser::debug_level_type
  parser::debug_level () const
  {
    return yydebug_;
  }

  void
  parser::set_debug_level (debug_level_type l)
  {
    yydebug_ = l;
  }
#endif

  int
  parser::parse ()
  {
    /// Lookahead and lookahead in internal form.
    int yychar = yyempty_;
    int yytoken = 0;

    /* State.  */
    int yyn;
    int yylen = 0;
    int yystate = 0;

    /* Error handling.  */
    int yynerrs_ = 0;
    int yyerrstatus_ = 0;

    /// Semantic value of the lookahead.
    semantic_type yylval;
    /// Location of the lookahead.
    location_type yylloc;
    /// The locations where the error started and ended.
    location_type yyerror_range[3];

    /// $$.
    semantic_type yyval;
    /// @$.
    location_type yyloc;

    int yyresult;

    YYCDEBUG << "Starting parse" << std::endl;


    /* Initialize the stacks.  The initial state will be pushed in
       yynewstate, since the latter expects the semantical and the
       location values to have been already stored, initialize these
       stacks with a primary value.  */
    yystate_stack_ = state_stack_type (0);
    yysemantic_stack_ = semantic_stack_type (0);
    yylocation_stack_ = location_stack_type (0);
    yysemantic_stack_.push (yylval);
    yylocation_stack_.push (yylloc);

    /* New state.  */
  yynewstate:
    yystate_stack_.push (yystate);
    YYCDEBUG << "Entering state " << yystate << std::endl;

    /* Accept?  */
    if (yystate == yyfinal_)
      goto yyacceptlab;

    goto yybackup;

    /* Backup.  */
  yybackup:

    /* Try to take a decision without lookahead.  */
    yyn = yypact_[yystate];
    if (yyn == yypact_ninf_)
      goto yydefault;

    /* Read a lookahead token.  */
    if (yychar == yyempty_)
      {
	YYCDEBUG << "Reading a token: ";
	yychar = yylex (&yylval, &yylloc, pe);
      }


    /* Convert token to internal form.  */
    if (yychar <= yyeof_)
      {
	yychar = yytoken = yyeof_;
	YYCDEBUG << "Now at end of input." << std::endl;
      }
    else
      {
	yytoken = yytranslate_ (yychar);
	YY_SYMBOL_PRINT ("Next token is", yytoken, &yylval, &yylloc);
      }

    /* If the proper action on seeing token YYTOKEN is to reduce or to
       detect an error, take that action.  */
    yyn += yytoken;
    if (yyn < 0 || yylast_ < yyn || yycheck_[yyn] != yytoken)
      goto yydefault;

    /* Reduce or error.  */
    yyn = yytable_[yyn];
    if (yyn <= 0)
      {
	if (yyn == 0 || yyn == yytable_ninf_)
	goto yyerrlab;
	yyn = -yyn;
	goto yyreduce;
      }

    /* Shift the lookahead token.  */
    YY_SYMBOL_PRINT ("Shifting", yytoken, &yylval, &yylloc);

    /* Discard the token being shifted.  */
    yychar = yyempty_;

    yysemantic_stack_.push (yylval);
    yylocation_stack_.push (yylloc);

    /* Count tokens shifted since error; after three, turn off error
       status.  */
    if (yyerrstatus_)
      --yyerrstatus_;

    yystate = yyn;
    goto yynewstate;

  /*-----------------------------------------------------------.
  | yydefault -- do the default action for the current state.  |
  `-----------------------------------------------------------*/
  yydefault:
    yyn = yydefact_[yystate];
    if (yyn == 0)
      goto yyerrlab;
    goto yyreduce;

  /*-----------------------------.
  | yyreduce -- Do a reduction.  |
  `-----------------------------*/
  yyreduce:
    yylen = yyr2_[yyn];
    /* If YYLEN is nonzero, implement the default value of the action:
       `$$ = $1'.  Otherwise, use the top of the stack.

       Otherwise, the following line sets YYVAL to garbage.
       This behavior is undocumented and Bison
       users should not rely upon it.  */
    if (yylen)
      yyval = yysemantic_stack_[yylen - 1];
    else
      yyval = yysemantic_stack_[0];

    {
      slice<location_type, location_stack_type> slice (yylocation_stack_, yylen);
      YYLLOC_DEFAULT (yyloc, slice, yylen);
    }
    YY_REDUCE_PRINT (yyn);
    switch (yyn)
      {
	  case 2:

/* Line 678 of lalr1.cc  */
#line 247 "eltlparse.yy"
    {
	  result = (yysemantic_stack_[(2) - (2)].fval);
	  YYACCEPT;
	}
    break;

  case 5:

/* Line 678 of lalr1.cc  */
#line 260 "eltlparse.yy"
    {
	  (yysemantic_stack_[(5) - (4)].nval)->set_name(*(yysemantic_stack_[(5) - (1)].sval));
          nmap[*(yysemantic_stack_[(5) - (1)].sval)] = nfa::ptr((yysemantic_stack_[(5) - (4)].nval));
	  delete (yysemantic_stack_[(5) - (1)].sval);
        }
    break;

  case 6:

/* Line 678 of lalr1.cc  */
#line 266 "eltlparse.yy"
    {
	  /// Recursivity issues of aliases are handled by a parse error.
	  aliasmap::iterator i = amap.find(*(yysemantic_stack_[(3) - (1)].sval));
	  if (i != amap.end())
	  {
	    std::string s = "`";
	    s += *(yysemantic_stack_[(3) - (1)].sval);
	    s += "' is already aliased";
	    PARSE_ERROR((yylocation_stack_[(3) - (1)]), s);
	    delete (yysemantic_stack_[(3) - (1)].sval);
	    YYERROR;
	  }
	  amap.insert(make_pair(*(yysemantic_stack_[(3) - (1)].sval), formula_tree::node_ptr((yysemantic_stack_[(3) - (3)].pval))));
   	  delete (yysemantic_stack_[(3) - (1)].sval);
   	}
    break;

  case 7:

/* Line 678 of lalr1.cc  */
#line 284 "eltlparse.yy"
    {
	  (yyval.nval) = new nfa;
        }
    break;

  case 8:

/* Line 678 of lalr1.cc  */
#line 288 "eltlparse.yy"
    {
	  (yysemantic_stack_[(4) - (1)].nval)->add_transition((yysemantic_stack_[(4) - (2)].ival), (yysemantic_stack_[(4) - (3)].ival), formula_tree::node_ptr((yysemantic_stack_[(4) - (4)].pval)));
	  (yyval.nval) = (yysemantic_stack_[(4) - (1)].nval);
        }
    break;

  case 9:

/* Line 678 of lalr1.cc  */
#line 293 "eltlparse.yy"
    {
 	  (yysemantic_stack_[(3) - (1)].nval)->set_final((yysemantic_stack_[(3) - (3)].ival));
	  (yyval.nval) = (yysemantic_stack_[(3) - (1)].nval);
        }
    break;

  case 10:

/* Line 678 of lalr1.cc  */
#line 300 "eltlparse.yy"
    {
	  (yyval.bval) = new formula_tree::node_nfa;
	  (yyval.bval)->children.push_back(formula_tree::node_ptr((yysemantic_stack_[(1) - (1)].pval)));
	}
    break;

  case 11:

/* Line 678 of lalr1.cc  */
#line 305 "eltlparse.yy"
    {
	  (yysemantic_stack_[(3) - (1)].bval)->children.push_back(formula_tree::node_ptr((yysemantic_stack_[(3) - (3)].pval)));
	  (yyval.bval) = (yysemantic_stack_[(3) - (1)].bval);
	}
    break;

  case 12:

/* Line 678 of lalr1.cc  */
#line 312 "eltlparse.yy"
    {
	  if ((yysemantic_stack_[(1) - (1)].ival) == -1)
	  {
	    std::string s = "out of range integer";
	    PARSE_ERROR((yylocation_stack_[(1) - (1)]), s);
	    YYERROR;
	  }
	  formula_tree::node_atomic* res = new formula_tree::node_atomic;
	  res->i = (yysemantic_stack_[(1) - (1)].ival);
	  (yyval.pval) = res;
	}
    break;

  case 13:

/* Line 678 of lalr1.cc  */
#line 324 "eltlparse.yy"
    {
	  formula_tree::node_atomic* res = new formula_tree::node_atomic;
	  res->i = formula_tree::True;
	  (yyval.pval) = res;
	}
    break;

  case 14:

/* Line 678 of lalr1.cc  */
#line 330 "eltlparse.yy"
    {
	  formula_tree::node_atomic* res = new formula_tree::node_atomic;
	  res->i = formula_tree::False;
	  (yyval.pval) = res;
	}
    break;

  case 15:

/* Line 678 of lalr1.cc  */
#line 336 "eltlparse.yy"
    {
	  formula_tree::node_unop* res = new formula_tree::node_unop;
	  res->op = unop::Not;
	  res->child = formula_tree::node_ptr((yysemantic_stack_[(2) - (2)].pval));
	  (yyval.pval) = res;
	}
    break;

  case 16:

/* Line 678 of lalr1.cc  */
#line 343 "eltlparse.yy"
    {
	  formula_tree::node_unop* res = new formula_tree::node_unop;
	  res->op = unop::Finish;
	  res->child = formula_tree::node_ptr((yysemantic_stack_[(4) - (3)].pval));
	  (yyval.pval) = res;
	}
    break;

  case 17:

/* Line 678 of lalr1.cc  */
#line 350 "eltlparse.yy"
    {
	  INSTANCIATE_OP((yyval.pval), formula_tree::node_multop, multop::And, (yysemantic_stack_[(3) - (1)].pval), (yysemantic_stack_[(3) - (3)].pval));
	}
    break;

  case 18:

/* Line 678 of lalr1.cc  */
#line 354 "eltlparse.yy"
    {
	  INSTANCIATE_OP((yyval.pval), formula_tree::node_multop, multop::Or, (yysemantic_stack_[(3) - (1)].pval), (yysemantic_stack_[(3) - (3)].pval));
	}
    break;

  case 19:

/* Line 678 of lalr1.cc  */
#line 358 "eltlparse.yy"
    {
	  INSTANCIATE_OP((yyval.pval), formula_tree::node_binop, binop::Xor, (yysemantic_stack_[(3) - (1)].pval), (yysemantic_stack_[(3) - (3)].pval));
	}
    break;

  case 20:

/* Line 678 of lalr1.cc  */
#line 362 "eltlparse.yy"
    {
	  INSTANCIATE_OP((yyval.pval), formula_tree::node_binop, binop::Implies, (yysemantic_stack_[(3) - (1)].pval), (yysemantic_stack_[(3) - (3)].pval));
	}
    break;

  case 21:

/* Line 678 of lalr1.cc  */
#line 366 "eltlparse.yy"
    {
	  INSTANCIATE_OP((yyval.pval), formula_tree::node_binop, binop::Equiv, (yysemantic_stack_[(3) - (1)].pval), (yysemantic_stack_[(3) - (3)].pval));
	}
    break;

  case 22:

/* Line 678 of lalr1.cc  */
#line 370 "eltlparse.yy"
    {
	  aliasmap::const_iterator i = amap.find(*(yysemantic_stack_[(4) - (1)].sval));
	  if (i != amap.end())
	  {
            unsigned arity = formula_tree::arity(i->second);
	    CHECK_ARITY((yylocation_stack_[(4) - (1)]), (yysemantic_stack_[(4) - (1)].sval), (yysemantic_stack_[(4) - (3)].bval)->children.size(), arity);

	    // Hack to return the right type without screwing with the
	    // boost::shared_ptr memory handling by using get for
	    // example. FIXME: Wait for the next version of boost and
	    // modify the %union to handle formula_tree::node_ptr.
	    formula_tree::node_unop* tmp1 = new formula_tree::node_unop;
	    tmp1->op = unop::Not;
	    tmp1->child = realias(i->second, (yysemantic_stack_[(4) - (3)].bval)->children);
	    formula_tree::node_unop* tmp2 = new formula_tree::node_unop;
	    tmp2->op = unop::Not;
	    tmp2->child = formula_tree::node_ptr(tmp1);
	    (yyval.pval) = tmp2;
	    delete (yysemantic_stack_[(4) - (3)].bval);
	  }
	  else
	  {
	    CHECK_EXISTING_NMAP((yylocation_stack_[(4) - (1)]), (yysemantic_stack_[(4) - (1)].sval));
	    nfa::ptr np = nmap[*(yysemantic_stack_[(4) - (1)].sval)];

	    CHECK_ARITY((yylocation_stack_[(4) - (1)]), (yysemantic_stack_[(4) - (1)].sval), (yysemantic_stack_[(4) - (3)].bval)->children.size(), np->arity());
	    (yysemantic_stack_[(4) - (3)].bval)->nfa = np;
	    (yyval.pval) = (yysemantic_stack_[(4) - (3)].bval);
	  }
	  delete (yysemantic_stack_[(4) - (1)].sval);
	}
    break;

  case 23:

/* Line 678 of lalr1.cc  */
#line 405 "eltlparse.yy"
    {
	  (yyval.fval) = parse_environment.require(*(yysemantic_stack_[(1) - (1)].sval));
	  if (!(yyval.fval))
	  {
	    std::string s = "unknown atomic proposition `";
	    s += *(yysemantic_stack_[(1) - (1)].sval);
	    s += "' in environment `";
	    s += parse_environment.name();
	    s += "'";
	    PARSE_ERROR((yylocation_stack_[(1) - (1)]), s);
	    delete (yysemantic_stack_[(1) - (1)].sval);
	    YYERROR;
	  }
	  else
	    delete (yysemantic_stack_[(1) - (1)].sval);
	}
    break;

  case 24:

/* Line 678 of lalr1.cc  */
#line 422 "eltlparse.yy"
    {
	  aliasmap::iterator i = amap.find(*(yysemantic_stack_[(3) - (2)].sval));
	  if (i != amap.end())
	  {
	    CHECK_ARITY((yylocation_stack_[(3) - (1)]), (yysemantic_stack_[(3) - (2)].sval), 2, formula_tree::arity(i->second));
	    automatop::vec v;
	    v.push_back((yysemantic_stack_[(3) - (1)].fval));
	    v.push_back((yysemantic_stack_[(3) - (3)].fval));
	    (yyval.fval) = instanciate(i->second, v);
	    (yysemantic_stack_[(3) - (1)].fval)->destroy();
	    (yysemantic_stack_[(3) - (3)].fval)->destroy();
	  }
	  else
	  {
	    CHECK_EXISTING_NMAP((yylocation_stack_[(3) - (1)]), (yysemantic_stack_[(3) - (2)].sval));
	    nfa::ptr np = nmap[*(yysemantic_stack_[(3) - (2)].sval)];
	    CHECK_ARITY((yylocation_stack_[(3) - (1)]), (yysemantic_stack_[(3) - (2)].sval), 2, np->arity());
	    automatop::vec* v = new automatop::vec;
	    v->push_back((yysemantic_stack_[(3) - (1)].fval));
	    v->push_back((yysemantic_stack_[(3) - (3)].fval));
	    (yyval.fval) = automatop::instance(np, v, false);
	  }
	  delete (yysemantic_stack_[(3) - (2)].sval);
	}
    break;

  case 25:

/* Line 678 of lalr1.cc  */
#line 447 "eltlparse.yy"
    {
	  aliasmap::iterator i = amap.find(*(yysemantic_stack_[(4) - (1)].sval));
	  if (i != amap.end())
	  {
	    CHECK_ARITY((yylocation_stack_[(4) - (1)]), (yysemantic_stack_[(4) - (1)].sval), (yysemantic_stack_[(4) - (3)].aval)->size(), formula_tree::arity(i->second));
	    (yyval.fval) = instanciate(i->second, *(yysemantic_stack_[(4) - (3)].aval));
	    automatop::vec::iterator it = (yysemantic_stack_[(4) - (3)].aval)->begin();
	    while (it != (yysemantic_stack_[(4) - (3)].aval)->end())
	      (*it++)->destroy();
	    delete (yysemantic_stack_[(4) - (3)].aval);
	  }
	  else
	  {
	    CHECK_EXISTING_NMAP((yylocation_stack_[(4) - (1)]), (yysemantic_stack_[(4) - (1)].sval));
	    nfa::ptr np = nmap[*(yysemantic_stack_[(4) - (1)].sval)];

	    /// Easily handle deletion of $3 when CHECK_ARITY fails.
	    unsigned i = (yysemantic_stack_[(4) - (3)].aval)->size();
	    if ((yysemantic_stack_[(4) - (3)].aval)->size() != np->arity())
	    {
	      automatop::vec::iterator it = (yysemantic_stack_[(4) - (3)].aval)->begin();
	      while (it != (yysemantic_stack_[(4) - (3)].aval)->end())
		(*it++)->destroy();
	      delete (yysemantic_stack_[(4) - (3)].aval);
	    }

	    CHECK_ARITY((yylocation_stack_[(4) - (1)]), (yysemantic_stack_[(4) - (1)].sval), i, np->arity());
	    (yyval.fval) = automatop::instance(np, (yysemantic_stack_[(4) - (3)].aval), false);
	  }
	  delete (yysemantic_stack_[(4) - (1)].sval);
	}
    break;

  case 26:

/* Line 678 of lalr1.cc  */
#line 479 "eltlparse.yy"
    { (yyval.fval) = constant::true_instance(); }
    break;

  case 27:

/* Line 678 of lalr1.cc  */
#line 481 "eltlparse.yy"
    { (yyval.fval) = constant::false_instance(); }
    break;

  case 28:

/* Line 678 of lalr1.cc  */
#line 483 "eltlparse.yy"
    { (yyval.fval) = (yysemantic_stack_[(3) - (2)].fval); }
    break;

  case 29:

/* Line 678 of lalr1.cc  */
#line 485 "eltlparse.yy"
    { (yyval.fval) = multop::instance(multop::And, (yysemantic_stack_[(3) - (1)].fval), (yysemantic_stack_[(3) - (3)].fval)); }
    break;

  case 30:

/* Line 678 of lalr1.cc  */
#line 487 "eltlparse.yy"
    { (yyval.fval) = multop::instance(multop::Or, (yysemantic_stack_[(3) - (1)].fval), (yysemantic_stack_[(3) - (3)].fval)); }
    break;

  case 31:

/* Line 678 of lalr1.cc  */
#line 489 "eltlparse.yy"
    { (yyval.fval) = binop::instance(binop::Xor, (yysemantic_stack_[(3) - (1)].fval), (yysemantic_stack_[(3) - (3)].fval)); }
    break;

  case 32:

/* Line 678 of lalr1.cc  */
#line 491 "eltlparse.yy"
    { (yyval.fval) = binop::instance(binop::Implies, (yysemantic_stack_[(3) - (1)].fval), (yysemantic_stack_[(3) - (3)].fval)); }
    break;

  case 33:

/* Line 678 of lalr1.cc  */
#line 493 "eltlparse.yy"
    { (yyval.fval) = binop::instance(binop::Equiv, (yysemantic_stack_[(3) - (1)].fval), (yysemantic_stack_[(3) - (3)].fval)); }
    break;

  case 34:

/* Line 678 of lalr1.cc  */
#line 495 "eltlparse.yy"
    { (yyval.fval) = unop::instance(unop::Not, (yysemantic_stack_[(2) - (2)].fval)); }
    break;

  case 35:

/* Line 678 of lalr1.cc  */
#line 499 "eltlparse.yy"
    {
	  (yyval.aval) = new automatop::vec;
	  (yyval.aval)->push_back((yysemantic_stack_[(1) - (1)].fval));
	}
    break;

  case 36:

/* Line 678 of lalr1.cc  */
#line 504 "eltlparse.yy"
    {
	  (yysemantic_stack_[(3) - (1)].aval)->push_back((yysemantic_stack_[(3) - (3)].fval));
	  (yyval.aval) = (yysemantic_stack_[(3) - (1)].aval);
	}
    break;



/* Line 678 of lalr1.cc  */
#line 986 "eltlparse.cc"
	default:
          break;
      }
    YY_SYMBOL_PRINT ("-> $$ =", yyr1_[yyn], &yyval, &yyloc);

    yypop_ (yylen);
    yylen = 0;
    YY_STACK_PRINT ();

    yysemantic_stack_.push (yyval);
    yylocation_stack_.push (yyloc);

    /* Shift the result of the reduction.  */
    yyn = yyr1_[yyn];
    yystate = yypgoto_[yyn - yyntokens_] + yystate_stack_[0];
    if (0 <= yystate && yystate <= yylast_
	&& yycheck_[yystate] == yystate_stack_[0])
      yystate = yytable_[yystate];
    else
      yystate = yydefgoto_[yyn - yyntokens_];
    goto yynewstate;

  /*------------------------------------.
  | yyerrlab -- here on detecting error |
  `------------------------------------*/
  yyerrlab:
    /* If not already recovering from an error, report this error.  */
    if (!yyerrstatus_)
      {
	++yynerrs_;
	error (yylloc, yysyntax_error_ (yystate, yytoken));
      }

    yyerror_range[1] = yylloc;
    if (yyerrstatus_ == 3)
      {
	/* If just tried and failed to reuse lookahead token after an
	 error, discard it.  */

	if (yychar <= yyeof_)
	  {
	  /* Return failure if at end of input.  */
	  if (yychar == yyeof_)
	    YYABORT;
	  }
	else
	  {
	    yydestruct_ ("Error: discarding", yytoken, &yylval, &yylloc);
	    yychar = yyempty_;
	  }
      }

    /* Else will try to reuse lookahead token after shifting the error
       token.  */
    goto yyerrlab1;


  /*---------------------------------------------------.
  | yyerrorlab -- error raised explicitly by YYERROR.  |
  `---------------------------------------------------*/
  yyerrorlab:

    /* Pacify compilers like GCC when the user code never invokes
       YYERROR and the label yyerrorlab therefore never appears in user
       code.  */
    if (false)
      goto yyerrorlab;

    yyerror_range[1] = yylocation_stack_[yylen - 1];
    /* Do not reclaim the symbols of the rule which action triggered
       this YYERROR.  */
    yypop_ (yylen);
    yylen = 0;
    yystate = yystate_stack_[0];
    goto yyerrlab1;

  /*-------------------------------------------------------------.
  | yyerrlab1 -- common code for both syntax error and YYERROR.  |
  `-------------------------------------------------------------*/
  yyerrlab1:
    yyerrstatus_ = 3;	/* Each real token shifted decrements this.  */

    for (;;)
      {
	yyn = yypact_[yystate];
	if (yyn != yypact_ninf_)
	{
	  yyn += yyterror_;
	  if (0 <= yyn && yyn <= yylast_ && yycheck_[yyn] == yyterror_)
	    {
	      yyn = yytable_[yyn];
	      if (0 < yyn)
		break;
	    }
	}

	/* Pop the current state because it cannot handle the error token.  */
	if (yystate_stack_.height () == 1)
	YYABORT;

	yyerror_range[1] = yylocation_stack_[0];
	yydestruct_ ("Error: popping",
		     yystos_[yystate],
		     &yysemantic_stack_[0], &yylocation_stack_[0]);
	yypop_ ();
	yystate = yystate_stack_[0];
	YY_STACK_PRINT ();
      }

    yyerror_range[2] = yylloc;
    // Using YYLLOC is tempting, but would change the location of
    // the lookahead.  YYLOC is available though.
    YYLLOC_DEFAULT (yyloc, yyerror_range, 2);
    yysemantic_stack_.push (yylval);
    yylocation_stack_.push (yyloc);

    /* Shift the error token.  */
    YY_SYMBOL_PRINT ("Shifting", yystos_[yyn],
		     &yysemantic_stack_[0], &yylocation_stack_[0]);

    yystate = yyn;
    goto yynewstate;

    /* Accept.  */
  yyacceptlab:
    yyresult = 0;
    goto yyreturn;

    /* Abort.  */
  yyabortlab:
    yyresult = 1;
    goto yyreturn;

  yyreturn:
    if (yychar != yyempty_)
      yydestruct_ ("Cleanup: discarding lookahead", yytoken, &yylval, &yylloc);

    /* Do not reclaim the symbols of the rule which action triggered
       this YYABORT or YYACCEPT.  */
    yypop_ (yylen);
    while (yystate_stack_.height () != 1)
      {
	yydestruct_ ("Cleanup: popping",
		   yystos_[yystate_stack_[0]],
		   &yysemantic_stack_[0],
		   &yylocation_stack_[0]);
	yypop_ ();
      }

    return yyresult;
  }

  // Generate an error message.
  std::string
  parser::yysyntax_error_ (int yystate, int tok)
  {
    std::string res;
    YYUSE (yystate);
#if YYERROR_VERBOSE
    int yyn = yypact_[yystate];
    if (yypact_ninf_ < yyn && yyn <= yylast_)
      {
	/* Start YYX at -YYN if negative to avoid negative indexes in
	   YYCHECK.  */
	int yyxbegin = yyn < 0 ? -yyn : 0;

	/* Stay within bounds of both yycheck and yytname.  */
	int yychecklim = yylast_ - yyn + 1;
	int yyxend = yychecklim < yyntokens_ ? yychecklim : yyntokens_;
	int count = 0;
	for (int x = yyxbegin; x < yyxend; ++x)
	  if (yycheck_[x + yyn] == x && x != yyterror_)
	    ++count;

	// FIXME: This method of building the message is not compatible
	// with internationalization.  It should work like yacc.c does it.
	// That is, first build a string that looks like this:
	// "syntax error, unexpected %s or %s or %s"
	// Then, invoke YY_ on this string.
	// Finally, use the string as a format to output
	// yytname_[tok], etc.
	// Until this gets fixed, this message appears in English only.
	res = "syntax error, unexpected ";
	res += yytnamerr_ (yytname_[tok]);
	if (count < 5)
	  {
	    count = 0;
	    for (int x = yyxbegin; x < yyxend; ++x)
	      if (yycheck_[x + yyn] == x && x != yyterror_)
		{
		  res += (!count++) ? ", expecting " : " or ";
		  res += yytnamerr_ (yytname_[x]);
		}
	  }
      }
    else
#endif
      res = YY_("syntax error");
    return res;
  }


  /* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
     STATE-NUM.  */
  const signed char parser::yypact_ninf_ = -27;
  const signed char
  parser::yypact_[] =
  {
       -27,     5,    45,   -27,    -9,    10,    13,    13,   -27,   -27,
     -27,    80,    13,    47,   -27,     1,    13,    13,    13,    13,
      13,    13,    80,     9,    12,   -27,    65,    16,   -27,   -27,
     -27,    97,   -27,   -27,    89,    92,     3,    32,    32,   -27,
      13,    65,   -27,    65,    41,    65,    65,    65,    65,    65,
      80,    38,    97,    64,    40,    44,   -27,   101,    -8,    68,
     -27,   -27,   -27,    65,   -27,    65,   -27,    97,    97
  };

  /* YYDEFACT[S] -- default rule to reduce with in state S when YYTABLE
     doesn't specify something else to do.  Zero means the default is an
     error.  */
  const unsigned char
  parser::yydefact_[] =
  {
         3,     0,     0,     1,    23,     0,     0,     0,    26,    27,
       4,     2,     0,     0,    34,     0,     0,     0,     0,     0,
       0,     0,    35,     0,     0,    12,     0,     0,     7,    13,
      14,     6,    28,    24,    30,    31,    29,    32,    33,    25,
       0,     0,    15,     0,     0,     0,     0,     0,     0,     0,
      36,     0,    10,     0,     0,     0,     5,    18,    19,    17,
      20,    21,    22,     0,    16,     0,     9,    11,     8
  };

  /* YYPGOTO[NTERM-NUM].  */
  const signed char
  parser::yypgoto_[] =
  {
       -27,   -27,   -27,   -27,   -27,   -27,   -26,    24,   -27
  };

  /* YYDEFGOTO[NTERM-NUM].  */
  const signed char
  parser::yydefgoto_[] =
  {
        -1,     1,     2,    10,    44,    51,    31,    11,    23
  };

  /* YYTABLE[YYPACT[STATE-NUM]].  What to do in state STATE-NUM.  If
     positive, shift that token.  If negative, reduce the rule which
     number is the opposite.  If zero, do what YYDEFACT says.  */
  const signed char parser::yytable_ninf_ = -1;
  const unsigned char
  parser::yytable_[] =
  {
        42,    47,    48,    49,    16,     3,    16,    12,    17,    18,
      19,    20,    21,    20,    21,    52,     4,    53,    32,    57,
      58,    59,    60,    61,    13,     6,    39,    40,    41,     7,
      14,    15,    43,     8,     9,    16,    22,    67,     0,    68,
      33,    34,    35,    36,    37,    38,    65,    54,     4,     5,
      66,    24,    25,     0,    55,    62,    63,     6,    56,    26,
       0,     7,    27,    28,    50,     8,     9,    29,    30,    24,
      25,    45,    46,    47,    48,    49,     0,    26,    48,    49,
      27,    64,     0,    16,     0,    29,    30,    17,    18,    19,
      20,    21,    16,     0,     0,    16,     0,    18,    19,    20,
      21,    19,    20,    21,    45,    46,    47,    48,    49,    46,
      47,    48,    49
  };

  /* YYCHECK.  */
  const signed char
  parser::yycheck_[] =
  {
        26,     9,    10,    11,     3,     0,     3,    16,     7,     8,
       9,    10,    11,    10,    11,    41,     3,    43,    17,    45,
      46,    47,    48,    49,    14,    12,    17,    18,    16,    16,
       6,     7,    16,    20,    21,     3,    12,    63,    -1,    65,
      16,    17,    18,    19,    20,    21,     6,     6,     3,     4,
       6,     4,     5,    -1,    13,    17,    18,    12,    17,    12,
      -1,    16,    15,    16,    40,    20,    21,    20,    21,     4,
       5,     7,     8,     9,    10,    11,    -1,    12,    10,    11,
      15,    17,    -1,     3,    -1,    20,    21,     7,     8,     9,
      10,    11,     3,    -1,    -1,     3,    -1,     8,     9,    10,
      11,     9,    10,    11,     7,     8,     9,    10,    11,     8,
       9,    10,    11
  };

  /* STOS_[STATE-NUM] -- The (internal number of the) accessing
     symbol of state STATE-NUM.  */
  const unsigned char
  parser::yystos_[] =
  {
         0,    23,    24,     0,     3,     4,    12,    16,    20,    21,
      25,    29,    16,    14,    29,    29,     3,     7,     8,     9,
      10,    11,    29,    30,     4,     5,    12,    15,    16,    20,
      21,    28,    17,    29,    29,    29,    29,    29,    29,    17,
      18,    16,    28,    16,    26,     7,     8,     9,    10,    11,
      29,    27,    28,    28,     6,    13,    17,    28,    28,    28,
      28,    28,    17,    18,    17,     6,     6,    28,    28
  };

#if YYDEBUG
  /* TOKEN_NUMBER_[YYLEX-NUM] -- Internal symbol number corresponding
     to YYLEX-NUM.  */
  const unsigned short int
  parser::yytoken_number_[] =
  {
         0,   256,   257,   258,   259,   260,   261,   262,   263,   264,
     265,   266,   267,   268,   269,   270,   271,   272,   273,   274,
     275,   276
  };
#endif

  /* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
  const unsigned char
  parser::yyr1_[] =
  {
         0,    22,    23,    24,    24,    25,    25,    26,    26,    26,
      27,    27,    28,    28,    28,    28,    28,    28,    28,    28,
      28,    28,    28,    29,    29,    29,    29,    29,    29,    29,
      29,    29,    29,    29,    29,    30,    30
  };

  /* YYR2[YYN] -- Number of symbols composing right hand side of rule YYN.  */
  const unsigned char
  parser::yyr2_[] =
  {
         0,     2,     2,     0,     2,     5,     3,     0,     4,     3,
       1,     3,     1,     1,     1,     2,     4,     3,     3,     3,
       3,     3,     4,     1,     3,     4,     1,     1,     3,     3,
       3,     3,     3,     3,     2,     1,     3
  };

#if YYDEBUG || YYERROR_VERBOSE || YYTOKEN_TABLE
  /* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
     First, the terminals, then, starting at \a yyntokens_, nonterminals.  */
  const char*
  const parser::yytname_[] =
  {
    "$end", "error", "$undefined", "\"atomic proposition\"",
  "\"identifier\"", "\"argument\"", "\"state\"", "\"or operator\"",
  "\"xor operator\"", "\"and operator\"", "\"implication operator\"",
  "\"equivalent operator\"", "\"not operator\"", "\"accept\"", "\"=\"",
  "\"finish\"", "\"(\"", "\")\"", "\",\"", "\"end of file\"",
  "\"constant true\"", "\"constant false\"", "$accept", "result",
  "nfa_list", "nfa", "nfa_def", "nfa_arg_list", "nfa_arg", "subformula",
  "arg_list", 0
  };
#endif

#if YYDEBUG
  /* YYRHS -- A `-1'-separated list of the rules' RHS.  */
  const parser::rhs_number_type
  parser::yyrhs_[] =
  {
        23,     0,    -1,    24,    29,    -1,    -1,    24,    25,    -1,
       4,    14,    16,    26,    17,    -1,     4,    14,    28,    -1,
      -1,    26,     6,     6,    28,    -1,    26,    13,     6,    -1,
      28,    -1,    27,    18,    28,    -1,     5,    -1,    20,    -1,
      21,    -1,    12,    28,    -1,    15,    16,    28,    17,    -1,
      28,     9,    28,    -1,    28,     7,    28,    -1,    28,     8,
      28,    -1,    28,    10,    28,    -1,    28,    11,    28,    -1,
       4,    16,    27,    17,    -1,     3,    -1,    29,     3,    29,
      -1,     3,    16,    30,    17,    -1,    20,    -1,    21,    -1,
      16,    29,    17,    -1,    29,     9,    29,    -1,    29,     7,
      29,    -1,    29,     8,    29,    -1,    29,    10,    29,    -1,
      29,    11,    29,    -1,    12,    29,    -1,    29,    -1,    30,
      18,    29,    -1
  };

  /* YYPRHS[YYN] -- Index of the first RHS symbol of rule number YYN in
     YYRHS.  */
  const unsigned char
  parser::yyprhs_[] =
  {
         0,     0,     3,     6,     7,    10,    16,    20,    21,    26,
      30,    32,    36,    38,    40,    42,    45,    50,    54,    58,
      62,    66,    70,    75,    77,    81,    86,    88,    90,    94,
      98,   102,   106,   110,   114,   117,   119
  };

  /* YYRLINE[YYN] -- Source line where rule number YYN was defined.  */
  const unsigned short int
  parser::yyrline_[] =
  {
         0,   246,   246,   255,   256,   259,   265,   284,   287,   292,
     299,   304,   311,   323,   329,   335,   342,   349,   353,   357,
     361,   365,   369,   404,   421,   446,   478,   480,   482,   484,
     486,   488,   490,   492,   494,   498,   503
  };

  // Print the state stack on the debug stream.
  void
  parser::yystack_print_ ()
  {
    *yycdebug_ << "Stack now";
    for (state_stack_type::const_iterator i = yystate_stack_.begin ();
	 i != yystate_stack_.end (); ++i)
      *yycdebug_ << ' ' << *i;
    *yycdebug_ << std::endl;
  }

  // Report on the debug stream that the rule \a yyrule is going to be reduced.
  void
  parser::yy_reduce_print_ (int yyrule)
  {
    unsigned int yylno = yyrline_[yyrule];
    int yynrhs = yyr2_[yyrule];
    /* Print the symbols being reduced, and their result.  */
    *yycdebug_ << "Reducing stack by rule " << yyrule - 1
	       << " (line " << yylno << "):" << std::endl;
    /* The symbols being reduced.  */
    for (int yyi = 0; yyi < yynrhs; yyi++)
      YY_SYMBOL_PRINT ("   $" << yyi + 1 << " =",
		       yyrhs_[yyprhs_[yyrule] + yyi],
		       &(yysemantic_stack_[(yynrhs) - (yyi + 1)]),
		       &(yylocation_stack_[(yynrhs) - (yyi + 1)]));
  }
#endif // YYDEBUG

  /* YYTRANSLATE(YYLEX) -- Bison symbol number corresponding to YYLEX.  */
  parser::token_number_type
  parser::yytranslate_ (int t)
  {
    static
    const token_number_type
    translate_table[] =
    {
           0,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     1,     2,     3,     4,
       5,     6,     7,     8,     9,    10,    11,    12,    13,    14,
      15,    16,    17,    18,    19,    20,    21
    };
    if ((unsigned int) t <= yyuser_token_number_max_)
      return translate_table[t];
    else
      return yyundef_token_;
  }

  const int parser::yyeof_ = 0;
  const int parser::yylast_ = 112;
  const int parser::yynnts_ = 9;
  const int parser::yyempty_ = -2;
  const int parser::yyfinal_ = 3;
  const int parser::yyterror_ = 1;
  const int parser::yyerrcode_ = 256;
  const int parser::yyntokens_ = 22;

  const unsigned int parser::yyuser_token_number_max_ = 276;
  const parser::token_number_type parser::yyundef_token_ = 2;


} // eltlyy

/* Line 1054 of lalr1.cc  */
#line 1464 "eltlparse.cc"


/* Line 1056 of lalr1.cc  */
#line 510 "eltlparse.yy"


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

