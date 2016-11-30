/* A Bison parser, made by GNU Bison 2.5.  */

/* Skeleton implementation for Bison LALR(1) parsers in C++
   
      Copyright (C) 2002-2011 Free Software Foundation, Inc.
   
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
#define yylex   ltlyylex

/* First part of user declarations.  */


/* Line 293 of lalr1.cc  */
#line 41 "ltlparse.cc"


#include "ltlparse.hh"

/* User implementation prologue.  */


/* Line 299 of lalr1.cc  */
#line 50 "ltlparse.cc"
/* Unqualified %code blocks.  */

/* Line 300 of lalr1.cc  */
#line 52 "ltlparse.yy"

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
		      const ltlyy::location& location,
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



/* Line 300 of lalr1.cc  */
#line 161 "ltlparse.cc"

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

/* YYLLOC_DEFAULT -- Set CURRENT to span from RHS[1] to RHS[N].
   If N is 0, then set CURRENT to the empty location which ends
   the previous symbol: RHS[0] (always defined).  */

#define YYRHSLOC(Rhs, K) ((Rhs)[K])
#ifndef YYLLOC_DEFAULT
# define YYLLOC_DEFAULT(Current, Rhs, N)                               \
 do                                                                    \
   if (N)                                                              \
     {                                                                 \
       (Current).begin = YYRHSLOC (Rhs, 1).begin;                      \
       (Current).end   = YYRHSLOC (Rhs, N).end;                        \
     }                                                                 \
   else                                                                \
     {                                                                 \
       (Current).begin = (Current).end = YYRHSLOC (Rhs, 0).end;        \
     }                                                                 \
 while (false)
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


namespace ltlyy {

/* Line 382 of lalr1.cc  */
#line 247 "ltlparse.cc"

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


  /// Build a parser object.
  parser::parser (spot::ltl::parse_error_list &error_list_yyarg, spot::ltl::environment &parse_environment_yyarg, const spot::ltl::formula* &result_yyarg)
    :
#if YYDEBUG
      yydebug_ (false),
      yycdebug_ (&std::cerr),
#endif
      error_list (error_list_yyarg),
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
        case 9: /* "\"(...) block\"" */

/* Line 449 of lalr1.cc  */
#line 235 "ltlparse.yy"
	{ debug_stream() << *(yyvaluep->str); };

/* Line 449 of lalr1.cc  */
#line 324 "ltlparse.cc"
	break;
      case 10: /* "\"{...} block\"" */

/* Line 449 of lalr1.cc  */
#line 235 "ltlparse.yy"
	{ debug_stream() << *(yyvaluep->str); };

/* Line 449 of lalr1.cc  */
#line 333 "ltlparse.cc"
	break;
      case 11: /* "\"{...}! block\"" */

/* Line 449 of lalr1.cc  */
#line 235 "ltlparse.yy"
	{ debug_stream() << *(yyvaluep->str); };

/* Line 449 of lalr1.cc  */
#line 342 "ltlparse.cc"
	break;
      case 36: /* "\"number for square bracket operator\"" */

/* Line 449 of lalr1.cc  */
#line 238 "ltlparse.yy"
	{ debug_stream() << (yyvaluep->num); };

/* Line 449 of lalr1.cc  */
#line 351 "ltlparse.cc"
	break;
      case 43: /* "\"atomic proposition\"" */

/* Line 449 of lalr1.cc  */
#line 235 "ltlparse.yy"
	{ debug_stream() << *(yyvaluep->str); };

/* Line 449 of lalr1.cc  */
#line 360 "ltlparse.cc"
	break;
      case 74: /* "sqbracketargs" */

/* Line 449 of lalr1.cc  */
#line 239 "ltlparse.yy"
	{ debug_stream() << (yyvaluep->minmax).min << ".." << (yyvaluep->minmax).max; };

/* Line 449 of lalr1.cc  */
#line 369 "ltlparse.cc"
	break;
      case 75: /* "gotoargs" */

/* Line 449 of lalr1.cc  */
#line 239 "ltlparse.yy"
	{ debug_stream() << (yyvaluep->minmax).min << ".." << (yyvaluep->minmax).max; };

/* Line 449 of lalr1.cc  */
#line 378 "ltlparse.cc"
	break;
      case 77: /* "starargs" */

/* Line 449 of lalr1.cc  */
#line 239 "ltlparse.yy"
	{ debug_stream() << (yyvaluep->minmax).min << ".." << (yyvaluep->minmax).max; };

/* Line 449 of lalr1.cc  */
#line 387 "ltlparse.cc"
	break;
      case 78: /* "equalargs" */

/* Line 449 of lalr1.cc  */
#line 239 "ltlparse.yy"
	{ debug_stream() << (yyvaluep->minmax).min << ".." << (yyvaluep->minmax).max; };

/* Line 449 of lalr1.cc  */
#line 396 "ltlparse.cc"
	break;
      case 79: /* "booleanatom" */

/* Line 449 of lalr1.cc  */
#line 236 "ltlparse.yy"
	{ spot::ltl::to_string((yyvaluep->ltl), debug_stream()); };

/* Line 449 of lalr1.cc  */
#line 405 "ltlparse.cc"
	break;
      case 80: /* "sere" */

/* Line 449 of lalr1.cc  */
#line 237 "ltlparse.yy"
	{ spot::ltl::to_string((yyvaluep->ltl), debug_stream(), false, true); };

/* Line 449 of lalr1.cc  */
#line 414 "ltlparse.cc"
	break;
      case 81: /* "bracedsere" */

/* Line 449 of lalr1.cc  */
#line 237 "ltlparse.yy"
	{ spot::ltl::to_string((yyvaluep->ltl), debug_stream(), false, true); };

/* Line 449 of lalr1.cc  */
#line 423 "ltlparse.cc"
	break;
      case 82: /* "parenthesedsubformula" */

/* Line 449 of lalr1.cc  */
#line 236 "ltlparse.yy"
	{ spot::ltl::to_string((yyvaluep->ltl), debug_stream()); };

/* Line 449 of lalr1.cc  */
#line 432 "ltlparse.cc"
	break;
      case 83: /* "boolformula" */

/* Line 449 of lalr1.cc  */
#line 236 "ltlparse.yy"
	{ spot::ltl::to_string((yyvaluep->ltl), debug_stream()); };

/* Line 449 of lalr1.cc  */
#line 441 "ltlparse.cc"
	break;
      case 84: /* "subformula" */

/* Line 449 of lalr1.cc  */
#line 236 "ltlparse.yy"
	{ spot::ltl::to_string((yyvaluep->ltl), debug_stream()); };

/* Line 449 of lalr1.cc  */
#line 450 "ltlparse.cc"
	break;
      case 85: /* "lbtformula" */

/* Line 449 of lalr1.cc  */
#line 236 "ltlparse.yy"
	{ spot::ltl::to_string((yyvaluep->ltl), debug_stream()); };

/* Line 449 of lalr1.cc  */
#line 459 "ltlparse.cc"
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
        case 9: /* "\"(...) block\"" */

/* Line 480 of lalr1.cc  */
#line 232 "ltlparse.yy"
	{ delete (yyvaluep->str); };

/* Line 480 of lalr1.cc  */
#line 498 "ltlparse.cc"
	break;
      case 10: /* "\"{...} block\"" */

/* Line 480 of lalr1.cc  */
#line 232 "ltlparse.yy"
	{ delete (yyvaluep->str); };

/* Line 480 of lalr1.cc  */
#line 507 "ltlparse.cc"
	break;
      case 11: /* "\"{...}! block\"" */

/* Line 480 of lalr1.cc  */
#line 232 "ltlparse.yy"
	{ delete (yyvaluep->str); };

/* Line 480 of lalr1.cc  */
#line 516 "ltlparse.cc"
	break;
      case 43: /* "\"atomic proposition\"" */

/* Line 480 of lalr1.cc  */
#line 232 "ltlparse.yy"
	{ delete (yyvaluep->str); };

/* Line 480 of lalr1.cc  */
#line 525 "ltlparse.cc"
	break;
      case 79: /* "booleanatom" */

/* Line 480 of lalr1.cc  */
#line 233 "ltlparse.yy"
	{ (yyvaluep->ltl)->destroy(); };

/* Line 480 of lalr1.cc  */
#line 534 "ltlparse.cc"
	break;
      case 80: /* "sere" */

/* Line 480 of lalr1.cc  */
#line 233 "ltlparse.yy"
	{ (yyvaluep->ltl)->destroy(); };

/* Line 480 of lalr1.cc  */
#line 543 "ltlparse.cc"
	break;
      case 81: /* "bracedsere" */

/* Line 480 of lalr1.cc  */
#line 233 "ltlparse.yy"
	{ (yyvaluep->ltl)->destroy(); };

/* Line 480 of lalr1.cc  */
#line 552 "ltlparse.cc"
	break;
      case 82: /* "parenthesedsubformula" */

/* Line 480 of lalr1.cc  */
#line 233 "ltlparse.yy"
	{ (yyvaluep->ltl)->destroy(); };

/* Line 480 of lalr1.cc  */
#line 561 "ltlparse.cc"
	break;
      case 83: /* "boolformula" */

/* Line 480 of lalr1.cc  */
#line 233 "ltlparse.yy"
	{ (yyvaluep->ltl)->destroy(); };

/* Line 480 of lalr1.cc  */
#line 570 "ltlparse.cc"
	break;
      case 84: /* "subformula" */

/* Line 480 of lalr1.cc  */
#line 233 "ltlparse.yy"
	{ (yyvaluep->ltl)->destroy(); };

/* Line 480 of lalr1.cc  */
#line 579 "ltlparse.cc"
	break;
      case 85: /* "lbtformula" */

/* Line 480 of lalr1.cc  */
#line 233 "ltlparse.yy"
	{ (yyvaluep->ltl)->destroy(); };

/* Line 480 of lalr1.cc  */
#line 588 "ltlparse.cc"
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

  inline bool
  parser::yy_pact_value_is_default_ (int yyvalue)
  {
    return yyvalue == yypact_ninf_;
  }

  inline bool
  parser::yy_table_value_is_error_ (int yyvalue)
  {
    return yyvalue == yytable_ninf_;
  }

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
    if (yy_pact_value_is_default_ (yyn))
      goto yydefault;

    /* Read a lookahead token.  */
    if (yychar == yyempty_)
      {
	YYCDEBUG << "Reading a token: ";
	yychar = yylex (&yylval, &yylloc, error_list);
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
	if (yy_table_value_is_error_ (yyn))
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

/* Line 690 of lalr1.cc  */
#line 243 "ltlparse.yy"
    { result = (yysemantic_stack_[(3) - (2)].ltl);
		YYACCEPT;
	      }
    break;

  case 3:

/* Line 690 of lalr1.cc  */
#line 247 "ltlparse.yy"
    {
		result = 0;
		YYABORT;
	      }
    break;

  case 4:

/* Line 690 of lalr1.cc  */
#line 252 "ltlparse.yy"
    {
		result = (yysemantic_stack_[(3) - (2)].ltl);
		YYACCEPT;
	      }
    break;

  case 5:

/* Line 690 of lalr1.cc  */
#line 257 "ltlparse.yy"
    { YYABORT; }
    break;

  case 6:

/* Line 690 of lalr1.cc  */
#line 259 "ltlparse.yy"
    { result = (yysemantic_stack_[(3) - (2)].ltl);
		YYACCEPT;
	      }
    break;

  case 7:

/* Line 690 of lalr1.cc  */
#line 263 "ltlparse.yy"
    {
		result = 0;
		YYABORT;
	      }
    break;

  case 8:

/* Line 690 of lalr1.cc  */
#line 268 "ltlparse.yy"
    {
		result = (yysemantic_stack_[(3) - (2)].ltl);
		YYACCEPT;
	      }
    break;

  case 9:

/* Line 690 of lalr1.cc  */
#line 273 "ltlparse.yy"
    { YYABORT; }
    break;

  case 10:

/* Line 690 of lalr1.cc  */
#line 275 "ltlparse.yy"
    { result = (yysemantic_stack_[(3) - (2)].ltl);
		YYACCEPT;
	      }
    break;

  case 11:

/* Line 690 of lalr1.cc  */
#line 279 "ltlparse.yy"
    {
		result = 0;
		YYABORT;
	      }
    break;

  case 12:

/* Line 690 of lalr1.cc  */
#line 284 "ltlparse.yy"
    {
		result = (yysemantic_stack_[(3) - (2)].ltl);
		YYACCEPT;
	      }
    break;

  case 13:

/* Line 690 of lalr1.cc  */
#line 289 "ltlparse.yy"
    { YYABORT; }
    break;

  case 14:

/* Line 690 of lalr1.cc  */
#line 291 "ltlparse.yy"
    { result = (yysemantic_stack_[(3) - (2)].ltl);
		YYACCEPT;
	      }
    break;

  case 15:

/* Line 690 of lalr1.cc  */
#line 295 "ltlparse.yy"
    {
		result = 0;
		YYABORT;
	      }
    break;

  case 16:

/* Line 690 of lalr1.cc  */
#line 300 "ltlparse.yy"
    {
		result = (yysemantic_stack_[(3) - (2)].ltl);
		YYACCEPT;
	      }
    break;

  case 17:

/* Line 690 of lalr1.cc  */
#line 305 "ltlparse.yy"
    { YYABORT; }
    break;

  case 18:

/* Line 690 of lalr1.cc  */
#line 308 "ltlparse.yy"
    {
		error_list.push_back(parse_error((yyloc), "empty input"));
		result = 0;
	      }
    break;

  case 19:

/* Line 690 of lalr1.cc  */
#line 314 "ltlparse.yy"
    {
		error_list.push_back(parse_error((yylocation_stack_[(2) - (1)]),
						 "ignoring trailing garbage"));
	      }
    break;

  case 26:

/* Line 690 of lalr1.cc  */
#line 326 "ltlparse.yy"
    { (yyval.minmax).min = (yysemantic_stack_[(4) - (1)].num); (yyval.minmax).max = (yysemantic_stack_[(4) - (3)].num); }
    break;

  case 27:

/* Line 690 of lalr1.cc  */
#line 328 "ltlparse.yy"
    { (yyval.minmax).min = (yysemantic_stack_[(3) - (1)].num); (yyval.minmax).max = bunop::unbounded; }
    break;

  case 28:

/* Line 690 of lalr1.cc  */
#line 330 "ltlparse.yy"
    { (yyval.minmax).min = 0U; (yyval.minmax).max = (yysemantic_stack_[(3) - (2)].num); }
    break;

  case 29:

/* Line 690 of lalr1.cc  */
#line 332 "ltlparse.yy"
    { (yyval.minmax).min = 0U; (yyval.minmax).max = bunop::unbounded; }
    break;

  case 30:

/* Line 690 of lalr1.cc  */
#line 334 "ltlparse.yy"
    { (yyval.minmax).min = (yyval.minmax).max = (yysemantic_stack_[(2) - (1)].num); }
    break;

  case 31:

/* Line 690 of lalr1.cc  */
#line 338 "ltlparse.yy"
    { (yyval.minmax).min = (yysemantic_stack_[(5) - (2)].num); (yyval.minmax).max = (yysemantic_stack_[(5) - (4)].num); }
    break;

  case 32:

/* Line 690 of lalr1.cc  */
#line 340 "ltlparse.yy"
    { (yyval.minmax).min = (yysemantic_stack_[(4) - (2)].num); (yyval.minmax).max = bunop::unbounded; }
    break;

  case 33:

/* Line 690 of lalr1.cc  */
#line 342 "ltlparse.yy"
    { (yyval.minmax).min = 1U; (yyval.minmax).max = (yysemantic_stack_[(4) - (3)].num); }
    break;

  case 34:

/* Line 690 of lalr1.cc  */
#line 344 "ltlparse.yy"
    { (yyval.minmax).min = 1U; (yyval.minmax).max = bunop::unbounded; }
    break;

  case 35:

/* Line 690 of lalr1.cc  */
#line 346 "ltlparse.yy"
    { (yyval.minmax).min = (yyval.minmax).max = 1U; }
    break;

  case 36:

/* Line 690 of lalr1.cc  */
#line 348 "ltlparse.yy"
    { (yyval.minmax).min = (yyval.minmax).max = (yysemantic_stack_[(3) - (2)].num); }
    break;

  case 37:

/* Line 690 of lalr1.cc  */
#line 350 "ltlparse.yy"
    { error_list.push_back(parse_error((yyloc),
	       "treating this goto block as [->]"));
             (yyval.minmax).min = (yyval.minmax).max = 1U; }
    break;

  case 38:

/* Line 690 of lalr1.cc  */
#line 354 "ltlparse.yy"
    { error_list.push_back(parse_error((yyloc),
               "missing closing bracket for goto operator"));
	     (yyval.minmax).min = (yyval.minmax).max = 0U; }
    break;

  case 41:

/* Line 690 of lalr1.cc  */
#line 361 "ltlparse.yy"
    { (yyval.minmax).min = 0U; (yyval.minmax).max = bunop::unbounded; }
    break;

  case 42:

/* Line 690 of lalr1.cc  */
#line 363 "ltlparse.yy"
    { (yyval.minmax).min = 1U; (yyval.minmax).max = bunop::unbounded; }
    break;

  case 43:

/* Line 690 of lalr1.cc  */
#line 365 "ltlparse.yy"
    { (yyval.minmax) = (yysemantic_stack_[(2) - (2)].minmax); }
    break;

  case 44:

/* Line 690 of lalr1.cc  */
#line 367 "ltlparse.yy"
    { error_list.push_back(parse_error((yyloc),
		"treating this star block as [*]"));
              (yyval.minmax).min = 0U; (yyval.minmax).max = bunop::unbounded; }
    break;

  case 45:

/* Line 690 of lalr1.cc  */
#line 371 "ltlparse.yy"
    { error_list.push_back(parse_error((yyloc),
                "missing closing bracket for star"));
	      (yyval.minmax).min = (yyval.minmax).max = 0U; }
    break;

  case 46:

/* Line 690 of lalr1.cc  */
#line 376 "ltlparse.yy"
    { (yyval.minmax) = (yysemantic_stack_[(2) - (2)].minmax); }
    break;

  case 47:

/* Line 690 of lalr1.cc  */
#line 378 "ltlparse.yy"
    { error_list.push_back(parse_error((yyloc),
		"treating this equal block as [*]"));
              (yyval.minmax).min = 0U; (yyval.minmax).max = bunop::unbounded; }
    break;

  case 48:

/* Line 690 of lalr1.cc  */
#line 382 "ltlparse.yy"
    { error_list.push_back(parse_error((yyloc),
                "missing closing bracket for equal operator"));
	      (yyval.minmax).min = (yyval.minmax).max = 0U; }
    break;

  case 49:

/* Line 690 of lalr1.cc  */
#line 391 "ltlparse.yy"
    {
		(yyval.ltl) = parse_environment.require(*(yysemantic_stack_[(1) - (1)].str));
		if (! (yyval.ltl))
		  {
		    std::string s = "unknown atomic proposition `";
		    s += *(yysemantic_stack_[(1) - (1)].str);
		    s += "' in environment `";
		    s += parse_environment.name();
		    s += "'";
		    error_list.push_back(parse_error((yylocation_stack_[(1) - (1)]), s));
		    delete (yysemantic_stack_[(1) - (1)].str);
		    YYERROR;
		  }
		else
		  delete (yysemantic_stack_[(1) - (1)].str);
	      }
    break;

  case 50:

/* Line 690 of lalr1.cc  */
#line 408 "ltlparse.yy"
    {
		(yyval.ltl) = parse_environment.require(*(yysemantic_stack_[(2) - (1)].str));
		if (! (yyval.ltl))
		  {
		    std::string s = "unknown atomic proposition `";
		    s += *(yysemantic_stack_[(2) - (1)].str);
		    s += "' in environment `";
		    s += parse_environment.name();
		    s += "'";
		    error_list.push_back(parse_error((yylocation_stack_[(2) - (1)]), s));
		    delete (yysemantic_stack_[(2) - (1)].str);
		    YYERROR;
		  }
		else
		  delete (yysemantic_stack_[(2) - (1)].str);
	      }
    break;

  case 51:

/* Line 690 of lalr1.cc  */
#line 425 "ltlparse.yy"
    {
		(yyval.ltl) = parse_environment.require(*(yysemantic_stack_[(2) - (1)].str));
		if (! (yyval.ltl))
		  {
		    std::string s = "unknown atomic proposition `";
		    s += *(yysemantic_stack_[(2) - (1)].str);
		    s += "' in environment `";
		    s += parse_environment.name();
		    s += "'";
		    error_list.push_back(parse_error((yylocation_stack_[(2) - (1)]), s));
		    delete (yysemantic_stack_[(2) - (1)].str);
		    YYERROR;
		  }
		else
		  delete (yysemantic_stack_[(2) - (1)].str);
		(yyval.ltl) = unop::instance(unop::Not, (yyval.ltl));
	      }
    break;

  case 52:

/* Line 690 of lalr1.cc  */
#line 443 "ltlparse.yy"
    { (yyval.ltl) = constant::true_instance(); }
    break;

  case 53:

/* Line 690 of lalr1.cc  */
#line 445 "ltlparse.yy"
    { (yyval.ltl) = constant::false_instance(); }
    break;

  case 55:

/* Line 690 of lalr1.cc  */
#line 449 "ltlparse.yy"
    {
		if ((yysemantic_stack_[(2) - (2)].ltl)->is_boolean())
		  {
		    (yyval.ltl) = unop::instance(unop::Not, (yysemantic_stack_[(2) - (2)].ltl));
		  }
		else
		  {
		    error_list.push_back(parse_error((yylocation_stack_[(2) - (2)]),
                       "not a boolean expression: inside a SERE `!' can only "
                       "be applied to a Boolean expression"));
		    error_list.push_back(parse_error((yyloc),
				"treating this block as false"));
		    (yysemantic_stack_[(2) - (2)].ltl)->destroy();
		    (yyval.ltl) = constant::false_instance();
		  }
	      }
    break;

  case 57:

/* Line 690 of lalr1.cc  */
#line 467 "ltlparse.yy"
    {
		(yyval.ltl) = try_recursive_parse(*(yysemantic_stack_[(1) - (1)].str), (yylocation_stack_[(1) - (1)]), parse_environment,
					 debug_level(), parser_sere, error_list);
		delete (yysemantic_stack_[(1) - (1)].str);
		if (!(yyval.ltl))
		  YYERROR;
	      }
    break;

  case 58:

/* Line 690 of lalr1.cc  */
#line 475 "ltlparse.yy"
    { (yyval.ltl) = (yysemantic_stack_[(3) - (2)].ltl); }
    break;

  case 59:

/* Line 690 of lalr1.cc  */
#line 477 "ltlparse.yy"
    { error_list.push_back(parse_error((yyloc),
		 "treating this parenthetical block as false"));
		(yyval.ltl) = constant::false_instance();
	      }
    break;

  case 60:

/* Line 690 of lalr1.cc  */
#line 482 "ltlparse.yy"
    { error_list.push_back(parse_error((yylocation_stack_[(3) - (1)]) + (yylocation_stack_[(3) - (2)]),
				      "missing closing parenthesis"));
		(yyval.ltl) = (yysemantic_stack_[(3) - (2)].ltl);
	      }
    break;

  case 61:

/* Line 690 of lalr1.cc  */
#line 487 "ltlparse.yy"
    { error_list.push_back(parse_error((yyloc),
                    "missing closing parenthesis, "
		    "treating this parenthetical block as false"));
		(yyval.ltl) = constant::false_instance();
	      }
    break;

  case 62:

/* Line 690 of lalr1.cc  */
#line 493 "ltlparse.yy"
    { (yyval.ltl) = multop::instance(multop::AndRat, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 63:

/* Line 690 of lalr1.cc  */
#line 495 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]),
				    "length-matching and operator"); }
    break;

  case 64:

/* Line 690 of lalr1.cc  */
#line 498 "ltlparse.yy"
    { (yyval.ltl) = multop::instance(multop::AndNLM, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 65:

/* Line 690 of lalr1.cc  */
#line 500 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]),
                                    "non-length-matching and operator"); }
    break;

  case 66:

/* Line 690 of lalr1.cc  */
#line 503 "ltlparse.yy"
    { (yyval.ltl) = multop::instance(multop::OrRat, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 67:

/* Line 690 of lalr1.cc  */
#line 505 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "or operator"); }
    break;

  case 68:

/* Line 690 of lalr1.cc  */
#line 507 "ltlparse.yy"
    { (yyval.ltl) = multop::instance(multop::Concat, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 69:

/* Line 690 of lalr1.cc  */
#line 509 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "concat operator"); }
    break;

  case 70:

/* Line 690 of lalr1.cc  */
#line 511 "ltlparse.yy"
    { (yyval.ltl) = multop::instance(multop::Fusion, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 71:

/* Line 690 of lalr1.cc  */
#line 513 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "fusion operator"); }
    break;

  case 72:

/* Line 690 of lalr1.cc  */
#line 515 "ltlparse.yy"
    {
		if ((yysemantic_stack_[(2) - (2)].minmax).max < (yysemantic_stack_[(2) - (2)].minmax).min)
		  {
		    error_list.push_back(parse_error((yylocation_stack_[(2) - (2)]), "reversed range"));
		    std::swap((yysemantic_stack_[(2) - (2)].minmax).max, (yysemantic_stack_[(2) - (2)].minmax).min);
		  }
		(yyval.ltl) = bunop::instance(bunop::Star, (yysemantic_stack_[(2) - (1)].ltl), (yysemantic_stack_[(2) - (2)].minmax).min, (yysemantic_stack_[(2) - (2)].minmax).max);
	      }
    break;

  case 73:

/* Line 690 of lalr1.cc  */
#line 524 "ltlparse.yy"
    {
		if ((yysemantic_stack_[(1) - (1)].minmax).max < (yysemantic_stack_[(1) - (1)].minmax).min)
		  {
		    error_list.push_back(parse_error((yylocation_stack_[(1) - (1)]), "reversed range"));
		    std::swap((yysemantic_stack_[(1) - (1)].minmax).max, (yysemantic_stack_[(1) - (1)].minmax).min);
		  }
		(yyval.ltl) = bunop::instance(bunop::Star, constant::true_instance(),
				     (yysemantic_stack_[(1) - (1)].minmax).min, (yysemantic_stack_[(1) - (1)].minmax).max);
	      }
    break;

  case 74:

/* Line 690 of lalr1.cc  */
#line 534 "ltlparse.yy"
    {
		if ((yysemantic_stack_[(2) - (2)].minmax).max < (yysemantic_stack_[(2) - (2)].minmax).min)
		  {
		    error_list.push_back(parse_error((yylocation_stack_[(2) - (2)]), "reversed range"));
		    std::swap((yysemantic_stack_[(2) - (2)].minmax).max, (yysemantic_stack_[(2) - (2)].minmax).min);
		  }
		if ((yysemantic_stack_[(2) - (1)].ltl)->is_boolean())
		  {
		    (yyval.ltl) = bunop::sugar_equal((yysemantic_stack_[(2) - (1)].ltl), (yysemantic_stack_[(2) - (2)].minmax).min, (yysemantic_stack_[(2) - (2)].minmax).max);
		  }
		else
		  {
		    error_list.push_back(parse_error((yylocation_stack_[(2) - (1)]),
				"not a boolean expression: [=...] can only "
				"be applied to a Boolean expression"));
		    error_list.push_back(parse_error((yyloc),
				"treating this block as false"));
		    (yysemantic_stack_[(2) - (1)].ltl)->destroy();
		    (yyval.ltl) = constant::false_instance();
		  }
	      }
    break;

  case 75:

/* Line 690 of lalr1.cc  */
#line 556 "ltlparse.yy"
    {
		if ((yysemantic_stack_[(2) - (2)].minmax).max < (yysemantic_stack_[(2) - (2)].minmax).min)
		  {
		    error_list.push_back(parse_error((yylocation_stack_[(2) - (2)]), "reversed range"));
		    std::swap((yysemantic_stack_[(2) - (2)].minmax).max, (yysemantic_stack_[(2) - (2)].minmax).min);
		  }
		if ((yysemantic_stack_[(2) - (1)].ltl)->is_boolean())
		  {
		    (yyval.ltl) = bunop::sugar_goto((yysemantic_stack_[(2) - (1)].ltl), (yysemantic_stack_[(2) - (2)].minmax).min, (yysemantic_stack_[(2) - (2)].minmax).max);
		  }
		else
		  {
		    error_list.push_back(parse_error((yylocation_stack_[(2) - (1)]),
				"not a boolean expression: [->...] can only "
				"be applied to a Boolean expression"));
		    error_list.push_back(parse_error((yyloc),
				"treating this block as false"));
		    (yysemantic_stack_[(2) - (1)].ltl)->destroy();
		    (yyval.ltl) = constant::false_instance();
		  }
	      }
    break;

  case 76:

/* Line 690 of lalr1.cc  */
#line 578 "ltlparse.yy"
    {
		if ((yysemantic_stack_[(3) - (1)].ltl)->is_boolean() && (yysemantic_stack_[(3) - (3)].ltl)->is_boolean())
		  {
		    (yyval.ltl) = binop::instance(binop::Xor, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl));
		  }
		else
		  {
		    if (!(yysemantic_stack_[(3) - (1)].ltl)->is_boolean())
		      {
			error_list.push_back(parse_error((yylocation_stack_[(3) - (1)]),
                         "not a boolean expression: inside SERE `<->' can only "
                         "be applied to Boolean expressions"));
                      }
		    if (!(yysemantic_stack_[(3) - (3)].ltl)->is_boolean())
		      {
			error_list.push_back(parse_error((yylocation_stack_[(3) - (3)]),
                         "not a boolean expression: inside SERE `<->' can only "
                         "be applied to Boolean expressions"));
                      }
		    error_list.push_back(parse_error((yyloc),
				"treating this block as false"));
		    (yysemantic_stack_[(3) - (1)].ltl)->destroy();
		    (yysemantic_stack_[(3) - (3)].ltl)->destroy();
		    (yyval.ltl) = constant::false_instance();
		  }
	      }
    break;

  case 77:

/* Line 690 of lalr1.cc  */
#line 605 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "xor operator"); }
    break;

  case 78:

/* Line 690 of lalr1.cc  */
#line 607 "ltlparse.yy"
    {
		if ((yysemantic_stack_[(3) - (1)].ltl)->is_boolean())
		  {
		    (yyval.ltl) = binop::instance(binop::Implies, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl));
		  }
		else
		  {
		    if (!(yysemantic_stack_[(3) - (1)].ltl)->is_boolean())
		      {
			error_list.push_back(parse_error((yylocation_stack_[(3) - (1)]),
                         "not a boolean expression: inside SERE `->' can only "
                         "be applied to a Boolean expression"));
                      }
		    error_list.push_back(parse_error((yyloc),
				"treating this block as false"));
		    (yysemantic_stack_[(3) - (1)].ltl)->destroy();
		    (yysemantic_stack_[(3) - (3)].ltl)->destroy();
		    (yyval.ltl) = constant::false_instance();
		  }
	      }
    break;

  case 79:

/* Line 690 of lalr1.cc  */
#line 628 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "implication operator"); }
    break;

  case 80:

/* Line 690 of lalr1.cc  */
#line 630 "ltlparse.yy"
    {
		if ((yysemantic_stack_[(3) - (1)].ltl)->is_boolean() && (yysemantic_stack_[(3) - (3)].ltl)->is_boolean())
		  {
		    (yyval.ltl) = binop::instance(binop::Equiv, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl));
		  }
		else
		  {
		    if (!(yysemantic_stack_[(3) - (1)].ltl)->is_boolean())
		      {
			error_list.push_back(parse_error((yylocation_stack_[(3) - (1)]),
                         "not a boolean expression: inside SERE `<->' can only "
                         "be applied to Boolean expressions"));
                      }
		    if (!(yysemantic_stack_[(3) - (3)].ltl)->is_boolean())
		      {
			error_list.push_back(parse_error((yylocation_stack_[(3) - (3)]),
                         "not a boolean expression: inside SERE `<->' can only "
                         "be applied to Boolean expressions"));
                      }
		    error_list.push_back(parse_error((yyloc),
				"treating this block as false"));
		    (yysemantic_stack_[(3) - (1)].ltl)->destroy();
		    (yysemantic_stack_[(3) - (3)].ltl)->destroy();
		    (yyval.ltl) = constant::false_instance();
		  }
	      }
    break;

  case 81:

/* Line 690 of lalr1.cc  */
#line 657 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "equivalent operator"); }
    break;

  case 82:

/* Line 690 of lalr1.cc  */
#line 660 "ltlparse.yy"
    { (yyval.ltl) = (yysemantic_stack_[(3) - (2)].ltl); }
    break;

  case 83:

/* Line 690 of lalr1.cc  */
#line 662 "ltlparse.yy"
    { error_list.push_back(parse_error((yylocation_stack_[(4) - (3)]), "ignoring this"));
		(yyval.ltl) = (yysemantic_stack_[(4) - (2)].ltl);
	      }
    break;

  case 84:

/* Line 690 of lalr1.cc  */
#line 666 "ltlparse.yy"
    { error_list.push_back(parse_error((yyloc),
		 "treating this brace block as false"));
		(yyval.ltl) = constant::false_instance();
	      }
    break;

  case 85:

/* Line 690 of lalr1.cc  */
#line 671 "ltlparse.yy"
    { error_list.push_back(parse_error((yylocation_stack_[(3) - (1)]) + (yylocation_stack_[(3) - (2)]),
				      "missing closing brace"));
		(yyval.ltl) = (yysemantic_stack_[(3) - (2)].ltl);
	      }
    break;

  case 86:

/* Line 690 of lalr1.cc  */
#line 676 "ltlparse.yy"
    { error_list.push_back(parse_error((yylocation_stack_[(4) - (3)]),
                "ignoring trailing garbage and missing closing brace"));
		(yyval.ltl) = (yysemantic_stack_[(4) - (2)].ltl);
	      }
    break;

  case 87:

/* Line 690 of lalr1.cc  */
#line 681 "ltlparse.yy"
    { error_list.push_back(parse_error((yyloc),
                    "missing closing brace, "
		    "treating this brace block as false"));
		(yyval.ltl) = constant::false_instance();
	      }
    break;

  case 88:

/* Line 690 of lalr1.cc  */
#line 687 "ltlparse.yy"
    {
		(yyval.ltl) = try_recursive_parse(*(yysemantic_stack_[(1) - (1)].str), (yylocation_stack_[(1) - (1)]), parse_environment,
					 debug_level(),
					 parser_sere, error_list);
		delete (yysemantic_stack_[(1) - (1)].str);
		if (!(yyval.ltl))
		  YYERROR;
	      }
    break;

  case 89:

/* Line 690 of lalr1.cc  */
#line 697 "ltlparse.yy"
    {
		(yyval.ltl) = try_recursive_parse(*(yysemantic_stack_[(1) - (1)].str), (yylocation_stack_[(1) - (1)]), parse_environment,
					 debug_level(), parser_ltl, error_list);
		delete (yysemantic_stack_[(1) - (1)].str);
		if (!(yyval.ltl))
		  YYERROR;
	      }
    break;

  case 90:

/* Line 690 of lalr1.cc  */
#line 705 "ltlparse.yy"
    { (yyval.ltl) = (yysemantic_stack_[(3) - (2)].ltl); }
    break;

  case 91:

/* Line 690 of lalr1.cc  */
#line 707 "ltlparse.yy"
    { error_list.push_back(parse_error((yylocation_stack_[(4) - (3)]), "ignoring this"));
		(yyval.ltl) = (yysemantic_stack_[(4) - (2)].ltl);
	      }
    break;

  case 92:

/* Line 690 of lalr1.cc  */
#line 711 "ltlparse.yy"
    { error_list.push_back(parse_error((yyloc),
		 "treating this parenthetical block as false"));
		(yyval.ltl) = constant::false_instance();
	      }
    break;

  case 93:

/* Line 690 of lalr1.cc  */
#line 716 "ltlparse.yy"
    { error_list.push_back(parse_error((yylocation_stack_[(3) - (1)]) + (yylocation_stack_[(3) - (2)]),
				      "missing closing parenthesis"));
		(yyval.ltl) = (yysemantic_stack_[(3) - (2)].ltl);
	      }
    break;

  case 94:

/* Line 690 of lalr1.cc  */
#line 721 "ltlparse.yy"
    { error_list.push_back(parse_error((yylocation_stack_[(4) - (3)]),
                "ignoring trailing garbage and missing closing parenthesis"));
		(yyval.ltl) = (yysemantic_stack_[(4) - (2)].ltl);
	      }
    break;

  case 95:

/* Line 690 of lalr1.cc  */
#line 726 "ltlparse.yy"
    { error_list.push_back(parse_error((yyloc),
                    "missing closing parenthesis, "
		    "treating this parenthetical block as false"));
		(yyval.ltl) = constant::false_instance();
	      }
    break;

  case 97:

/* Line 690 of lalr1.cc  */
#line 735 "ltlparse.yy"
    {
		(yyval.ltl) = try_recursive_parse(*(yysemantic_stack_[(1) - (1)].str), (yylocation_stack_[(1) - (1)]), parse_environment,
					 debug_level(), parser_bool, error_list);
		delete (yysemantic_stack_[(1) - (1)].str);
		if (!(yyval.ltl))
		  YYERROR;
	      }
    break;

  case 98:

/* Line 690 of lalr1.cc  */
#line 743 "ltlparse.yy"
    { (yyval.ltl) = (yysemantic_stack_[(3) - (2)].ltl); }
    break;

  case 99:

/* Line 690 of lalr1.cc  */
#line 745 "ltlparse.yy"
    { error_list.push_back(parse_error((yylocation_stack_[(4) - (3)]), "ignoring this"));
		(yyval.ltl) = (yysemantic_stack_[(4) - (2)].ltl);
	      }
    break;

  case 100:

/* Line 690 of lalr1.cc  */
#line 749 "ltlparse.yy"
    { error_list.push_back(parse_error((yyloc),
		 "treating this parenthetical block as false"));
		(yyval.ltl) = constant::false_instance();
	      }
    break;

  case 101:

/* Line 690 of lalr1.cc  */
#line 754 "ltlparse.yy"
    { error_list.push_back(parse_error((yylocation_stack_[(3) - (1)]) + (yylocation_stack_[(3) - (2)]),
				      "missing closing parenthesis"));
		(yyval.ltl) = (yysemantic_stack_[(3) - (2)].ltl);
	      }
    break;

  case 102:

/* Line 690 of lalr1.cc  */
#line 759 "ltlparse.yy"
    { error_list.push_back(parse_error((yylocation_stack_[(4) - (3)]),
                "ignoring trailing garbage and missing closing parenthesis"));
		(yyval.ltl) = (yysemantic_stack_[(4) - (2)].ltl);
	      }
    break;

  case 103:

/* Line 690 of lalr1.cc  */
#line 764 "ltlparse.yy"
    { error_list.push_back(parse_error((yyloc),
                    "missing closing parenthesis, "
		    "treating this parenthetical block as false"));
		(yyval.ltl) = constant::false_instance();
	      }
    break;

  case 104:

/* Line 690 of lalr1.cc  */
#line 770 "ltlparse.yy"
    { (yyval.ltl) = multop::instance(multop::And, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 105:

/* Line 690 of lalr1.cc  */
#line 772 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "and operator"); }
    break;

  case 106:

/* Line 690 of lalr1.cc  */
#line 774 "ltlparse.yy"
    { (yyval.ltl) = multop::instance(multop::And, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 107:

/* Line 690 of lalr1.cc  */
#line 776 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "and operator"); }
    break;

  case 108:

/* Line 690 of lalr1.cc  */
#line 778 "ltlparse.yy"
    { (yyval.ltl) = multop::instance(multop::And, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 109:

/* Line 690 of lalr1.cc  */
#line 780 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "and operator"); }
    break;

  case 110:

/* Line 690 of lalr1.cc  */
#line 782 "ltlparse.yy"
    { (yyval.ltl) = multop::instance(multop::Or, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 111:

/* Line 690 of lalr1.cc  */
#line 784 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "or operator"); }
    break;

  case 112:

/* Line 690 of lalr1.cc  */
#line 786 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::Xor, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 113:

/* Line 690 of lalr1.cc  */
#line 788 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "xor operator"); }
    break;

  case 114:

/* Line 690 of lalr1.cc  */
#line 790 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::Implies, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 115:

/* Line 690 of lalr1.cc  */
#line 792 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "implication operator"); }
    break;

  case 116:

/* Line 690 of lalr1.cc  */
#line 794 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::Equiv, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 117:

/* Line 690 of lalr1.cc  */
#line 796 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "equivalent operator"); }
    break;

  case 118:

/* Line 690 of lalr1.cc  */
#line 798 "ltlparse.yy"
    { (yyval.ltl) = unop::instance(unop::Not, (yysemantic_stack_[(2) - (2)].ltl)); }
    break;

  case 119:

/* Line 690 of lalr1.cc  */
#line 800 "ltlparse.yy"
    { missing_right_op((yyval.ltl), (yylocation_stack_[(2) - (1)]), "not operator"); }
    break;

  case 122:

/* Line 690 of lalr1.cc  */
#line 805 "ltlparse.yy"
    { (yyval.ltl) = multop::instance(multop::And, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 123:

/* Line 690 of lalr1.cc  */
#line 807 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "and operator"); }
    break;

  case 124:

/* Line 690 of lalr1.cc  */
#line 809 "ltlparse.yy"
    { (yyval.ltl) = multop::instance(multop::And, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 125:

/* Line 690 of lalr1.cc  */
#line 811 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "and operator"); }
    break;

  case 126:

/* Line 690 of lalr1.cc  */
#line 813 "ltlparse.yy"
    { (yyval.ltl) = multop::instance(multop::And, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 127:

/* Line 690 of lalr1.cc  */
#line 815 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "and operator"); }
    break;

  case 128:

/* Line 690 of lalr1.cc  */
#line 817 "ltlparse.yy"
    { (yyval.ltl) = multop::instance(multop::Or, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 129:

/* Line 690 of lalr1.cc  */
#line 819 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "or operator"); }
    break;

  case 130:

/* Line 690 of lalr1.cc  */
#line 821 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::Xor, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 131:

/* Line 690 of lalr1.cc  */
#line 823 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "xor operator"); }
    break;

  case 132:

/* Line 690 of lalr1.cc  */
#line 825 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::Implies, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 133:

/* Line 690 of lalr1.cc  */
#line 827 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "implication operator"); }
    break;

  case 134:

/* Line 690 of lalr1.cc  */
#line 829 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::Equiv, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 135:

/* Line 690 of lalr1.cc  */
#line 831 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "equivalent operator"); }
    break;

  case 136:

/* Line 690 of lalr1.cc  */
#line 833 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::U, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 137:

/* Line 690 of lalr1.cc  */
#line 835 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "until operator"); }
    break;

  case 138:

/* Line 690 of lalr1.cc  */
#line 837 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::R, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 139:

/* Line 690 of lalr1.cc  */
#line 839 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "release operator"); }
    break;

  case 140:

/* Line 690 of lalr1.cc  */
#line 841 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::W, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 141:

/* Line 690 of lalr1.cc  */
#line 843 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "weak until operator"); }
    break;

  case 142:

/* Line 690 of lalr1.cc  */
#line 845 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::M, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 143:

/* Line 690 of lalr1.cc  */
#line 847 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "strong release operator"); }
    break;

  case 144:

/* Line 690 of lalr1.cc  */
#line 849 "ltlparse.yy"
    { (yyval.ltl) = unop::instance(unop::F, (yysemantic_stack_[(2) - (2)].ltl)); }
    break;

  case 145:

/* Line 690 of lalr1.cc  */
#line 851 "ltlparse.yy"
    { missing_right_op((yyval.ltl), (yylocation_stack_[(2) - (1)]), "sometimes operator"); }
    break;

  case 146:

/* Line 690 of lalr1.cc  */
#line 853 "ltlparse.yy"
    { (yyval.ltl) = unop::instance(unop::G, (yysemantic_stack_[(2) - (2)].ltl)); }
    break;

  case 147:

/* Line 690 of lalr1.cc  */
#line 855 "ltlparse.yy"
    { missing_right_op((yyval.ltl), (yylocation_stack_[(2) - (1)]), "always operator"); }
    break;

  case 148:

/* Line 690 of lalr1.cc  */
#line 857 "ltlparse.yy"
    { (yyval.ltl) = unop::instance(unop::X, (yysemantic_stack_[(2) - (2)].ltl)); }
    break;

  case 149:

/* Line 690 of lalr1.cc  */
#line 859 "ltlparse.yy"
    { missing_right_op((yyval.ltl), (yylocation_stack_[(2) - (1)]), "next operator"); }
    break;

  case 150:

/* Line 690 of lalr1.cc  */
#line 861 "ltlparse.yy"
    { (yyval.ltl) = unop::instance(unop::Not, (yysemantic_stack_[(2) - (2)].ltl)); }
    break;

  case 151:

/* Line 690 of lalr1.cc  */
#line 863 "ltlparse.yy"
    { missing_right_op((yyval.ltl), (yylocation_stack_[(2) - (1)]), "not operator"); }
    break;

  case 152:

/* Line 690 of lalr1.cc  */
#line 865 "ltlparse.yy"
    { (yyval.ltl) = unop::instance(unop::Closure, (yysemantic_stack_[(1) - (1)].ltl)); }
    break;

  case 153:

/* Line 690 of lalr1.cc  */
#line 867 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::UConcat, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 154:

/* Line 690 of lalr1.cc  */
#line 869 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::UConcat, (yysemantic_stack_[(2) - (1)].ltl), (yysemantic_stack_[(2) - (2)].ltl)); }
    break;

  case 155:

/* Line 690 of lalr1.cc  */
#line 871 "ltlparse.yy"
    { missing_right_binop_hard((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]),
				    "universal overlapping concat operator"); }
    break;

  case 156:

/* Line 690 of lalr1.cc  */
#line 874 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::EConcat, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 157:

/* Line 690 of lalr1.cc  */
#line 876 "ltlparse.yy"
    { missing_right_binop_hard((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]),
				    "existential overlapping concat operator");
	      }
    break;

  case 158:

/* Line 690 of lalr1.cc  */
#line 881 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::UConcat,
		       multop::instance(multop::Concat, (yysemantic_stack_[(3) - (1)].ltl),
					constant::true_instance()), (yysemantic_stack_[(3) - (3)].ltl));
	      }
    break;

  case 159:

/* Line 690 of lalr1.cc  */
#line 886 "ltlparse.yy"
    { missing_right_binop_hard((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]),
				  "universal non-overlapping concat operator");
	      }
    break;

  case 160:

/* Line 690 of lalr1.cc  */
#line 891 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::EConcat,
		       multop::instance(multop::Concat, (yysemantic_stack_[(3) - (1)].ltl),
					constant::true_instance()), (yysemantic_stack_[(3) - (3)].ltl));
	      }
    break;

  case 161:

/* Line 690 of lalr1.cc  */
#line 896 "ltlparse.yy"
    { missing_right_binop_hard((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]),
				"existential non-overlapping concat operator");
	      }
    break;

  case 162:

/* Line 690 of lalr1.cc  */
#line 901 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::EConcat, (yysemantic_stack_[(3) - (2)].ltl),
				     constant::true_instance()); }
    break;

  case 163:

/* Line 690 of lalr1.cc  */
#line 904 "ltlparse.yy"
    {
		(yyval.ltl) = try_recursive_parse(*(yysemantic_stack_[(1) - (1)].str), (yylocation_stack_[(1) - (1)]), parse_environment,
					 debug_level(), parser_sere, error_list);
		delete (yysemantic_stack_[(1) - (1)].str);
		if (!(yyval.ltl))
		  YYERROR;
		(yyval.ltl) = binop::instance(binop::EConcat, (yyval.ltl),
				     constant::true_instance());
	      }
    break;

  case 164:

/* Line 690 of lalr1.cc  */
#line 915 "ltlparse.yy"
    {
		(yyval.ltl) = parse_environment.require(*(yysemantic_stack_[(1) - (1)].str));
		if (! (yyval.ltl))
		  {
		    std::string s = "atomic proposition `";
		    s += *(yysemantic_stack_[(1) - (1)].str);
		    s += "' rejected by environment `";
		    s += parse_environment.name();
		    s += "'";
		    error_list.push_back(parse_error((yylocation_stack_[(1) - (1)]), s));
		    delete (yysemantic_stack_[(1) - (1)].str);
		    YYERROR;
		  }
		else
		  delete (yysemantic_stack_[(1) - (1)].str);
	      }
    break;

  case 165:

/* Line 690 of lalr1.cc  */
#line 932 "ltlparse.yy"
    { (yyval.ltl) = unop::instance(unop::Not, (yysemantic_stack_[(2) - (2)].ltl)); }
    break;

  case 166:

/* Line 690 of lalr1.cc  */
#line 934 "ltlparse.yy"
    { (yyval.ltl) = multop::instance(multop::And, (yysemantic_stack_[(3) - (2)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 167:

/* Line 690 of lalr1.cc  */
#line 936 "ltlparse.yy"
    { (yyval.ltl) = multop::instance(multop::Or, (yysemantic_stack_[(3) - (2)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 168:

/* Line 690 of lalr1.cc  */
#line 938 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::Xor, (yysemantic_stack_[(3) - (2)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 169:

/* Line 690 of lalr1.cc  */
#line 940 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::Implies, (yysemantic_stack_[(3) - (2)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 170:

/* Line 690 of lalr1.cc  */
#line 942 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::Equiv, (yysemantic_stack_[(3) - (2)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 171:

/* Line 690 of lalr1.cc  */
#line 944 "ltlparse.yy"
    { (yyval.ltl) = unop::instance(unop::X, (yysemantic_stack_[(2) - (2)].ltl)); }
    break;

  case 172:

/* Line 690 of lalr1.cc  */
#line 946 "ltlparse.yy"
    { (yyval.ltl) = unop::instance(unop::F, (yysemantic_stack_[(2) - (2)].ltl)); }
    break;

  case 173:

/* Line 690 of lalr1.cc  */
#line 948 "ltlparse.yy"
    { (yyval.ltl) = unop::instance(unop::G, (yysemantic_stack_[(2) - (2)].ltl)); }
    break;

  case 174:

/* Line 690 of lalr1.cc  */
#line 950 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::U, (yysemantic_stack_[(3) - (2)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 175:

/* Line 690 of lalr1.cc  */
#line 952 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::R, (yysemantic_stack_[(3) - (2)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 176:

/* Line 690 of lalr1.cc  */
#line 954 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::R, (yysemantic_stack_[(3) - (2)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 177:

/* Line 690 of lalr1.cc  */
#line 956 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::W, (yysemantic_stack_[(3) - (2)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 178:

/* Line 690 of lalr1.cc  */
#line 958 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::M, (yysemantic_stack_[(3) - (2)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); }
    break;

  case 179:

/* Line 690 of lalr1.cc  */
#line 960 "ltlparse.yy"
    { (yyval.ltl) = constant::true_instance(); }
    break;

  case 180:

/* Line 690 of lalr1.cc  */
#line 962 "ltlparse.yy"
    { (yyval.ltl) = constant::false_instance(); }
    break;



/* Line 690 of lalr1.cc  */
#line 2309 "ltlparse.cc"
	default:
          break;
      }
    /* User semantic actions sometimes alter yychar, and that requires
       that yytoken be updated with the new translation.  We take the
       approach of translating immediately before every use of yytoken.
       One alternative is translating here after every semantic action,
       but that translation would be missed if the semantic action
       invokes YYABORT, YYACCEPT, or YYERROR immediately after altering
       yychar.  In the case of YYABORT or YYACCEPT, an incorrect
       destructor might then be invoked immediately.  In the case of
       YYERROR, subsequent parser actions might lead to an incorrect
       destructor call or verbose syntax error message before the
       lookahead is translated.  */
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
    /* Make sure we have latest lookahead translation.  See comments at
       user semantic actions for why this is necessary.  */
    yytoken = yytranslate_ (yychar);

    /* If not already recovering from an error, report this error.  */
    if (!yyerrstatus_)
      {
	++yynerrs_;
	if (yychar == yyempty_)
	  yytoken = yyempty_;
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
	if (!yy_pact_value_is_default_ (yyn))
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
      {
        /* Make sure we have latest lookahead translation.  See comments
           at user semantic actions for why this is necessary.  */
        yytoken = yytranslate_ (yychar);
        yydestruct_ ("Cleanup: discarding lookahead", yytoken, &yylval,
                     &yylloc);
      }

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
  parser::yysyntax_error_ (int yystate, int yytoken)
  {
    std::string yyres;
    // Number of reported tokens (one for the "unexpected", one per
    // "expected").
    size_t yycount = 0;
    // Its maximum.
    enum { YYERROR_VERBOSE_ARGS_MAXIMUM = 5 };
    // Arguments of yyformat.
    char const *yyarg[YYERROR_VERBOSE_ARGS_MAXIMUM];

    /* There are many possibilities here to consider:
       - If this state is a consistent state with a default action, then
         the only way this function was invoked is if the default action
         is an error action.  In that case, don't check for expected
         tokens because there are none.
       - The only way there can be no lookahead present (in yytoken) is
         if this state is a consistent state with a default action.
         Thus, detecting the absence of a lookahead is sufficient to
         determine that there is no unexpected or expected token to
         report.  In that case, just report a simple "syntax error".
       - Don't assume there isn't a lookahead just because this state is
         a consistent state with a default action.  There might have
         been a previous inconsistent state, consistent state with a
         non-default action, or user semantic action that manipulated
         yychar.
       - Of course, the expected token list depends on states to have
         correct lookahead information, and it depends on the parser not
         to perform extra reductions after fetching a lookahead from the
         scanner and before detecting a syntax error.  Thus, state
         merging (from LALR or IELR) and default reductions corrupt the
         expected token list.  However, the list is correct for
         canonical LR with one exception: it will still contain any
         token that will not be accepted due to an error action in a
         later state.
    */
    if (yytoken != yyempty_)
      {
        yyarg[yycount++] = yytname_[yytoken];
        int yyn = yypact_[yystate];
        if (!yy_pact_value_is_default_ (yyn))
          {
            /* Start YYX at -YYN if negative to avoid negative indexes in
               YYCHECK.  In other words, skip the first -YYN actions for
               this state because they are default actions.  */
            int yyxbegin = yyn < 0 ? -yyn : 0;
            /* Stay within bounds of both yycheck and yytname.  */
            int yychecklim = yylast_ - yyn + 1;
            int yyxend = yychecklim < yyntokens_ ? yychecklim : yyntokens_;
            for (int yyx = yyxbegin; yyx < yyxend; ++yyx)
              if (yycheck_[yyx + yyn] == yyx && yyx != yyterror_
                  && !yy_table_value_is_error_ (yytable_[yyx + yyn]))
                {
                  if (yycount == YYERROR_VERBOSE_ARGS_MAXIMUM)
                    {
                      yycount = 1;
                      break;
                    }
                  else
                    yyarg[yycount++] = yytname_[yyx];
                }
          }
      }

    char const* yyformat = 0;
    switch (yycount)
      {
#define YYCASE_(N, S)                         \
        case N:                               \
          yyformat = S;                       \
        break
        YYCASE_(0, YY_("syntax error"));
        YYCASE_(1, YY_("syntax error, unexpected %s"));
        YYCASE_(2, YY_("syntax error, unexpected %s, expecting %s"));
        YYCASE_(3, YY_("syntax error, unexpected %s, expecting %s or %s"));
        YYCASE_(4, YY_("syntax error, unexpected %s, expecting %s or %s or %s"));
        YYCASE_(5, YY_("syntax error, unexpected %s, expecting %s or %s or %s or %s"));
#undef YYCASE_
      }

    // Argument number.
    size_t yyi = 0;
    for (char const* yyp = yyformat; *yyp; ++yyp)
      if (yyp[0] == '%' && yyp[1] == 's' && yyi < yycount)
        {
          yyres += yytnamerr_ (yyarg[yyi++]);
          ++yyp;
        }
      else
        yyres += *yyp;
    return yyres;
  }


  /* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
     STATE-NUM.  */
  const signed char parser::yypact_ninf_ = -122;
  const short int
  parser::yypact_[] =
  {
        83,   348,    16,   370,   360,     8,   -32,   452,  -122,  -122,
    -122,   459,   500,   507,   530,   558,    -9,  -122,  -122,  -122,
    -122,  -122,  -122,   193,  -122,   404,  -122,  1229,  1229,  1229,
    1229,  1229,  1229,  1229,  1229,  1229,  1229,  1229,  1229,  1229,
    1229,  -122,  -122,  -122,  -122,     9,   580,  -122,   459,  1228,
    -122,  -122,  -122,    18,  -122,  -122,  -122,  -122,  -122,   308,
    -122,  1074,  -122,  1090,  -122,  -122,  -122,   314,  -122,  -122,
      -5,   229,    -2,   266,  -122,  -122,  -122,  -122,  -122,  -122,
    -122,  -122,  -122,  -122,   606,   629,   636,   659,  -122,   687,
     710,   717,   740,   768,   791,   798,   821,   849,   872,   879,
    -122,  -122,  -122,  1229,  1229,  1229,  1229,  1229,  -122,  -122,
    -122,  1229,  1229,  1229,  1229,  1229,  -122,  -122,    -4,  1200,
     274,  -122,    48,    -3,    68,  -122,    56,   -15,  -122,   901,
     927,   953,   979,  1005,  1031,   349,   403,  1057,  1083,  -122,
    -122,  -122,  -122,  -122,     4,   428,  -122,  -122,  1100,  1131,
    1141,  1148,  1153,  1164,  1196,  -122,  -122,  -122,  -122,    10,
    -122,  -122,  -122,  -122,    36,  -122,  -122,  -122,  -122,     5,
    -122,     5,  -122,     5,  -122,     5,  -122,  1324,  -122,  1333,
    -122,    88,  -122,    88,  -122,     5,  -122,     5,  -122,   136,
    -122,   136,  -122,   136,  -122,   136,  -122,   136,  -122,  -122,
    -122,  -122,  -122,  -122,  -122,  -122,  -122,  -122,  -122,  -122,
    -122,  -122,  -122,  -122,    89,    78,    95,  -122,  -122,  -122,
    -122,  1287,  -122,  1305,  -122,   209,  -122,   209,  -122,  1281,
    -122,  1281,   103,   -11,  -122,   106,  -122,    80,   143,   126,
     117,  -122,  1234,  -122,  1281,  -122,  -122,    37,  -122,  -122,
    -122,    85,  -122,    13,  -122,   138,  -122,   138,  -122,   189,
    -122,   189,  -122,  -122,  -122,  -122,  -122,  -122,   134,  -122,
    -122,  -122,  -122,  -122,  -122,   174,   149,   166,  -122,  -122,
    -122,  -122,  -122,   168,  -122,  -122,  -122
  };

  /* YYDEFACT[S] -- default reduction number in state S.  Performed when
     YYTABLE doesn't specify something else to do.  Zero means the
     default is an error.  */
  const unsigned char
  parser::yydefact_[] =
  {
         0,     0,     0,     0,     0,     0,     0,     0,    89,    88,
     163,     0,     0,     0,     0,     0,    49,    52,    53,    18,
       5,     3,   120,   152,   121,     0,   164,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,   179,   180,    17,    15,     0,     0,    57,     0,     0,
      39,    40,    42,     0,    13,    11,    41,    73,    54,     0,
      56,     0,    97,     0,     9,     7,    96,     0,     1,    19,
       0,     0,     0,     0,   145,   144,   147,   146,   149,   148,
     151,   150,    51,    50,     0,     0,     0,     0,   154,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       2,     4,   165,     0,     0,     0,     0,     0,   171,   172,
     173,     0,     0,     0,     0,     0,    14,    16,     0,     0,
       0,    55,    25,     0,    20,    23,     0,     0,    43,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,    10,
      12,    75,    72,    74,     0,     0,   119,   118,     0,     0,
       0,     0,     0,     0,     0,     6,     8,    92,    95,     0,
      90,    93,    84,    87,     0,    82,   162,    85,   155,   153,
     157,   156,   159,   158,   161,   160,   129,   128,   131,   130,
     123,   122,   125,   124,   133,   132,   135,   134,   137,   136,
     139,   138,   141,   140,   143,   142,   127,   126,   166,   167,
     168,   169,   170,   174,   175,   176,   177,   178,    59,    61,
      58,    60,    44,    30,    20,     0,     0,    21,    29,    45,
      67,    66,    77,    76,    63,    62,    65,    64,    79,    78,
      81,    80,    25,     0,    46,    25,    35,     0,    20,     0,
       0,    69,    68,    71,    70,   100,   103,     0,    98,   101,
     111,   110,   113,   112,   105,   104,   107,   106,   115,   114,
     117,   116,   109,   108,    91,    94,    83,    86,     0,    27,
      28,    47,    48,    37,    36,    20,     0,     0,    34,    38,
      99,   102,    26,     0,    32,    33,    31
  };

  /* YYPGOTO[NTERM-NUM].  */
  const short int
  parser::yypgoto_[] =
  {
      -122,  -122,   144,   104,  -121,  -122,    77,    96,  -122,  -122,
     -59,  -122,     2,   -10,    44,   213,   -54,   130,   159
  };

  /* YYDEFGOTO[NTERM-NUM].  */
  const short int
  parser::yydefgoto_[] =
  {
        -1,     5,    20,    21,   125,   126,   127,   128,   141,    56,
      57,   143,    22,    59,    23,    24,    67,    25,    45
  };

  /* YYTABLE[YYPACT[STATE-NUM]].  What to do in state STATE-NUM.  If
     positive, shift that token.  If negative, reduce the rule which
     number is the opposite.  If YYTABLE_NINF_, syntax error.  */
  const signed char parser::yytable_ninf_ = -25;
  const short int
  parser::yytable_[] =
  {
       142,    73,   215,   157,   208,    58,    66,   145,    68,   147,
       6,   162,   245,    58,   142,   239,    69,     6,   264,   122,
      89,    90,    91,    92,    93,    94,    95,    96,    97,    98,
     150,   151,   213,   219,    99,   214,   119,   272,   120,   121,
      82,    83,   154,   158,   209,   280,   163,    60,    58,   266,
      58,    58,   246,   -22,   123,    60,   124,   116,   265,    26,
     142,   142,   142,    66,    19,    66,   -24,    27,    28,    29,
      30,    31,    32,    33,    34,    35,    36,    37,    38,    39,
      40,    41,    42,   212,   267,   281,     1,     2,     3,     4,
      60,   218,    60,    60,   251,   253,   255,   257,   259,   261,
     263,   149,   150,   151,   216,   217,    44,    55,    65,    95,
      96,    97,    98,   269,   154,   274,   276,    99,   275,   221,
     223,   225,   227,   229,   231,   268,   217,   242,   244,   101,
     270,    58,    58,    58,    58,    58,    58,    71,   271,    58,
      58,   273,    75,    77,    79,    81,    43,    54,    64,   117,
      66,    66,    66,    66,    66,    66,    66,    95,    96,    97,
      98,   278,   142,   140,   142,   279,   142,   154,   142,   282,
     142,   156,   142,    60,    60,    60,    60,    60,    60,   277,
     217,    60,    60,   142,   284,   142,   102,   103,   104,   105,
     106,   107,   108,   109,   110,   111,   112,   113,   114,   115,
       7,   285,     8,   286,   148,   149,   150,   151,   152,   153,
     283,   217,   233,   240,   169,   171,   173,   175,   154,   177,
     179,   181,   183,   185,   187,   189,   191,   193,   195,   197,
     159,   234,    84,    85,    86,    87,    88,   160,    50,    51,
      52,    53,   135,   136,    89,    90,    91,    92,    93,    94,
      95,    96,    97,    98,     0,     0,     0,     0,    99,     0,
       0,     0,   198,   199,   200,   201,   202,   164,     0,     0,
     203,   204,   205,   206,   207,   164,     0,   161,     0,   165,
     166,   129,   130,   131,   132,   133,   134,   165,     0,   129,
     130,   131,   132,   133,   134,    50,    51,    52,    53,   135,
     136,     0,     0,    50,    51,    52,    53,   135,   136,     6,
     137,   138,     0,     0,   167,     6,     0,     0,   137,   138,
       0,     0,   167,   129,   130,   131,   132,   133,   134,   148,
     149,   150,   151,   152,   153,     0,     0,    50,    51,    52,
      53,   135,   136,   154,     0,     0,     0,     0,     0,     6,
     232,     0,   137,   138,     0,     7,   139,     8,     9,    10,
      11,     6,   155,     0,     0,     0,     0,    61,     0,    62,
       0,     6,     0,    12,    13,    14,    15,    46,     0,    47,
       9,     0,    48,     0,   -22,   123,     0,   124,    63,     0,
       0,    16,     0,     0,    17,    18,    19,   -24,    49,    50,
      51,    52,    53,    16,   235,     6,    17,    18,    19,     0,
       0,     0,     0,    16,     0,     0,    17,    18,    19,    89,
      90,    91,    92,    93,    94,    95,    96,    97,    98,   247,
       0,     0,     0,    99,     0,     0,   248,     0,   236,   237,
       0,   238,     0,   148,   149,   150,   151,   152,   153,     0,
       0,   -24,   100,    70,     0,     0,     0,   154,     0,     7,
      72,     8,     9,    10,    11,     0,    46,     0,    47,     9,
       0,    48,     0,     0,     0,     0,   249,    12,    13,    14,
      15,     0,     0,     0,     0,     0,     0,    49,    50,    51,
      52,    53,     0,     0,     0,    16,     0,     0,    17,    18,
       0,    74,    16,     0,     0,    17,    18,     7,    76,     8,
       9,    10,    11,     0,     7,     0,     8,     9,    10,    11,
       0,     0,     0,     0,     0,    12,    13,    14,    15,     0,
       0,    78,    12,    13,    14,    15,     0,     7,     0,     8,
       9,    10,    11,    16,     0,     0,    17,    18,     0,     0,
      16,     0,     0,    17,    18,    12,    13,    14,    15,    80,
       0,     0,     0,     0,     0,     7,     0,     8,     9,    10,
      11,     0,     0,    16,     0,     0,    17,    18,     0,     0,
       0,   118,     0,    12,    13,    14,    15,    46,     0,    47,
       9,     0,    48,     0,     0,     0,     0,     0,     0,     0,
       0,    16,     0,     0,    17,    18,     0,   168,    49,    50,
      51,    52,    53,     7,     0,     8,     9,    10,    11,     0,
       0,     0,     0,    16,     0,     0,    17,    18,     0,     0,
     170,    12,    13,    14,    15,     0,     7,   172,     8,     9,
      10,    11,     0,     7,     0,     8,     9,    10,    11,    16,
       0,     0,    17,    18,    12,    13,    14,    15,     0,     0,
     174,    12,    13,    14,    15,     0,     7,     0,     8,     9,
      10,    11,    16,     0,     0,    17,    18,     0,     0,    16,
       0,     0,    17,    18,    12,    13,    14,    15,   176,     0,
       0,     0,     0,     0,     7,     0,     8,     9,    10,    11,
       0,     0,    16,     0,     0,    17,    18,     0,     0,     0,
       0,   178,    12,    13,    14,    15,     0,     7,   180,     8,
       9,    10,    11,     0,     7,     0,     8,     9,    10,    11,
      16,     0,     0,    17,    18,    12,    13,    14,    15,     0,
       0,   182,    12,    13,    14,    15,     0,     7,     0,     8,
       9,    10,    11,    16,     0,     0,    17,    18,     0,     0,
      16,     0,     0,    17,    18,    12,    13,    14,    15,   184,
       0,     0,     0,     0,     0,     7,     0,     8,     9,    10,
      11,     0,     0,    16,     0,     0,    17,    18,     0,     0,
       0,     0,   186,    12,    13,    14,    15,     0,     7,   188,
       8,     9,    10,    11,     0,     7,     0,     8,     9,    10,
      11,    16,     0,     0,    17,    18,    12,    13,    14,    15,
       0,     0,   190,    12,    13,    14,    15,     0,     7,     0,
       8,     9,    10,    11,    16,     0,     0,    17,    18,     0,
       0,    16,     0,     0,    17,    18,    12,    13,    14,    15,
     192,     0,     0,     0,     0,     0,     7,     0,     8,     9,
      10,    11,     0,     0,    16,     0,     0,    17,    18,     0,
       0,     0,     0,   194,    12,    13,    14,    15,     0,     7,
     196,     8,     9,    10,    11,     0,     7,     0,     8,     9,
      10,    11,    16,     0,     0,    17,    18,    12,    13,    14,
      15,     0,   220,     0,    12,    13,    14,    15,    46,     0,
      47,     9,     0,    48,     0,    16,     0,     0,    17,    18,
       0,     0,    16,     0,     0,    17,    18,     0,   222,    49,
      50,    51,    52,    53,    46,     0,    47,     9,     0,    48,
       0,     0,     0,     0,    16,     0,     0,    17,    18,     0,
       0,     0,     0,     0,   224,    49,    50,    51,    52,    53,
      46,     0,    47,     9,     0,    48,     0,     0,     0,     0,
      16,     0,     0,    17,    18,     0,     0,     0,     0,     0,
     226,    49,    50,    51,    52,    53,    46,     0,    47,     9,
       0,    48,     0,     0,     0,     0,    16,     0,     0,    17,
      18,     0,     0,     0,     0,     0,   228,    49,    50,    51,
      52,    53,    46,     0,    47,     9,     0,    48,     0,     0,
       0,     0,    16,     0,     0,    17,    18,     0,     0,     0,
       0,     0,   230,    49,    50,    51,    52,    53,    46,     0,
      47,     9,     0,    48,     0,     0,     0,     0,    16,     0,
       0,    17,    18,     0,     0,     0,     0,     0,   241,    49,
      50,    51,    52,    53,    46,     0,    47,     9,     0,    48,
       0,     0,     0,     0,    16,   144,     0,    17,    18,     0,
       0,    61,     0,    62,   243,    49,    50,    51,    52,    53,
      46,   146,    47,     9,     0,    48,     0,    61,     0,    62,
      16,   250,    63,    17,    18,     0,     0,    61,     0,    62,
       0,    49,    50,    51,    52,    53,     0,    16,    63,     0,
      17,    18,     0,     0,     0,     0,    16,     0,    63,    17,
      18,     0,   252,    16,     0,     0,    17,    18,    61,     0,
      62,     0,   254,    16,     0,     0,    17,    18,    61,   256,
      62,     0,     0,     0,   258,    61,     0,    62,     0,    63,
      61,     0,    62,     0,     0,   260,     0,     0,     0,    63,
       0,    61,     0,    62,    16,     0,    63,    17,    18,     0,
       0,    63,     0,     0,    16,     0,     0,    17,    18,     0,
       0,    16,    63,     0,    17,    18,    16,   262,     0,    17,
      18,     0,     0,    61,     0,    62,     0,    16,   210,     0,
      17,    18,     0,     0,     0,   129,   130,   131,   132,   133,
     134,     0,     0,     0,    63,     0,     0,     0,     0,    50,
      51,    52,    53,   135,   136,    46,     0,    47,     9,    16,
      48,     0,    17,    18,   137,   138,     0,     0,   211,   129,
     130,   131,   132,   133,   134,     0,    49,    50,    51,    52,
      53,     0,     0,    50,    51,    52,    53,   135,   136,     0,
       0,    16,    26,     0,    17,    18,     0,     0,     0,   138,
      27,    28,    29,    30,    31,    32,    33,    34,    35,    36,
      37,    38,    39,    40,    41,    42,   129,   130,   131,   132,
     133,   134,     0,   130,   131,   132,     0,     0,     0,     0,
      50,    51,    52,    53,   135,   136,    50,    51,    52,    53,
     135,   136,   131,   132,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,    50,    51,    52,    53,   135,   136,
      90,    91,    92,     0,     0,    95,    96,    97,    98,     0,
      91,    92,     0,    99,    95,    96,    97,    98,     0,     0,
       0,     0,    99
  };

  /* YYCHECK.  */
  const short int
  parser::yycheck_[] =
  {
        59,    11,   123,     8,     8,     3,     4,    61,     0,    63,
       1,    13,     8,    11,    73,   136,    48,     1,     8,     1,
      15,    16,    17,    18,    19,    20,    21,    22,    23,    24,
      17,    18,    35,    48,    29,    38,    46,    48,    48,    49,
      49,    50,    29,    48,    48,     8,    48,     3,    46,    13,
      48,    49,    48,    35,    36,    11,    38,    48,    48,    43,
     119,   120,   121,    61,    48,    63,    48,    51,    52,    53,
      54,    55,    56,    57,    58,    59,    60,    61,    62,    63,
      64,    65,    66,    35,    48,    48,     3,     4,     5,     6,
      46,    35,    48,    49,   148,   149,   150,   151,   152,   153,
     154,    16,    17,    18,    36,    37,     2,     3,     4,    21,
      22,    23,    24,    35,    29,    35,   237,    29,    38,   129,
     130,   131,   132,   133,   134,    36,    37,   137,   138,    25,
      35,   129,   130,   131,   132,   133,   134,     7,    35,   137,
     138,    35,    12,    13,    14,    15,     2,     3,     4,    45,
     148,   149,   150,   151,   152,   153,   154,    21,    22,    23,
      24,    35,   221,    59,   223,    48,   225,    29,   227,    35,
     229,    67,   231,   129,   130,   131,   132,   133,   134,    36,
      37,   137,   138,   242,    35,   244,    27,    28,    29,    30,
      31,    32,    33,    34,    35,    36,    37,    38,    39,    40,
       7,    35,     9,    35,    15,    16,    17,    18,    19,    20,
      36,    37,   135,   136,    84,    85,    86,    87,    29,    89,
      90,    91,    92,    93,    94,    95,    96,    97,    98,    99,
       1,   135,    39,    40,    41,    42,    23,     8,    29,    30,
      31,    32,    33,    34,    15,    16,    17,    18,    19,    20,
      21,    22,    23,    24,    -1,    -1,    -1,    -1,    29,    -1,
      -1,    -1,   103,   104,   105,   106,   107,     1,    -1,    -1,
     111,   112,   113,   114,   115,     1,    -1,    48,    -1,    13,
      14,    15,    16,    17,    18,    19,    20,    13,    -1,    15,
      16,    17,    18,    19,    20,    29,    30,    31,    32,    33,
      34,    -1,    -1,    29,    30,    31,    32,    33,    34,     1,
      44,    45,    -1,    -1,    48,     1,    -1,    -1,    44,    45,
      -1,    -1,    48,    15,    16,    17,    18,    19,    20,    15,
      16,    17,    18,    19,    20,    -1,    -1,    29,    30,    31,
      32,    33,    34,    29,    -1,    -1,    -1,    -1,    -1,     1,
       1,    -1,    44,    45,    -1,     7,    48,     9,    10,    11,
      12,     1,    48,    -1,    -1,    -1,    -1,     7,    -1,     9,
      -1,     1,    -1,    25,    26,    27,    28,     7,    -1,     9,
      10,    -1,    12,    -1,    35,    36,    -1,    38,    28,    -1,
      -1,    43,    -1,    -1,    46,    47,    48,    48,    28,    29,
      30,    31,    32,    43,     1,     1,    46,    47,    48,    -1,
      -1,    -1,    -1,    43,    -1,    -1,    46,    47,    48,    15,
      16,    17,    18,    19,    20,    21,    22,    23,    24,     1,
      -1,    -1,    -1,    29,    -1,    -1,     8,    -1,    35,    36,
      -1,    38,    -1,    15,    16,    17,    18,    19,    20,    -1,
      -1,    48,    48,     1,    -1,    -1,    -1,    29,    -1,     7,
       1,     9,    10,    11,    12,    -1,     7,    -1,     9,    10,
      -1,    12,    -1,    -1,    -1,    -1,    48,    25,    26,    27,
      28,    -1,    -1,    -1,    -1,    -1,    -1,    28,    29,    30,
      31,    32,    -1,    -1,    -1,    43,    -1,    -1,    46,    47,
      -1,     1,    43,    -1,    -1,    46,    47,     7,     1,     9,
      10,    11,    12,    -1,     7,    -1,     9,    10,    11,    12,
      -1,    -1,    -1,    -1,    -1,    25,    26,    27,    28,    -1,
      -1,     1,    25,    26,    27,    28,    -1,     7,    -1,     9,
      10,    11,    12,    43,    -1,    -1,    46,    47,    -1,    -1,
      43,    -1,    -1,    46,    47,    25,    26,    27,    28,     1,
      -1,    -1,    -1,    -1,    -1,     7,    -1,     9,    10,    11,
      12,    -1,    -1,    43,    -1,    -1,    46,    47,    -1,    -1,
      -1,     1,    -1,    25,    26,    27,    28,     7,    -1,     9,
      10,    -1,    12,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    43,    -1,    -1,    46,    47,    -1,     1,    28,    29,
      30,    31,    32,     7,    -1,     9,    10,    11,    12,    -1,
      -1,    -1,    -1,    43,    -1,    -1,    46,    47,    -1,    -1,
       1,    25,    26,    27,    28,    -1,     7,     1,     9,    10,
      11,    12,    -1,     7,    -1,     9,    10,    11,    12,    43,
      -1,    -1,    46,    47,    25,    26,    27,    28,    -1,    -1,
       1,    25,    26,    27,    28,    -1,     7,    -1,     9,    10,
      11,    12,    43,    -1,    -1,    46,    47,    -1,    -1,    43,
      -1,    -1,    46,    47,    25,    26,    27,    28,     1,    -1,
      -1,    -1,    -1,    -1,     7,    -1,     9,    10,    11,    12,
      -1,    -1,    43,    -1,    -1,    46,    47,    -1,    -1,    -1,
      -1,     1,    25,    26,    27,    28,    -1,     7,     1,     9,
      10,    11,    12,    -1,     7,    -1,     9,    10,    11,    12,
      43,    -1,    -1,    46,    47,    25,    26,    27,    28,    -1,
      -1,     1,    25,    26,    27,    28,    -1,     7,    -1,     9,
      10,    11,    12,    43,    -1,    -1,    46,    47,    -1,    -1,
      43,    -1,    -1,    46,    47,    25,    26,    27,    28,     1,
      -1,    -1,    -1,    -1,    -1,     7,    -1,     9,    10,    11,
      12,    -1,    -1,    43,    -1,    -1,    46,    47,    -1,    -1,
      -1,    -1,     1,    25,    26,    27,    28,    -1,     7,     1,
       9,    10,    11,    12,    -1,     7,    -1,     9,    10,    11,
      12,    43,    -1,    -1,    46,    47,    25,    26,    27,    28,
      -1,    -1,     1,    25,    26,    27,    28,    -1,     7,    -1,
       9,    10,    11,    12,    43,    -1,    -1,    46,    47,    -1,
      -1,    43,    -1,    -1,    46,    47,    25,    26,    27,    28,
       1,    -1,    -1,    -1,    -1,    -1,     7,    -1,     9,    10,
      11,    12,    -1,    -1,    43,    -1,    -1,    46,    47,    -1,
      -1,    -1,    -1,     1,    25,    26,    27,    28,    -1,     7,
       1,     9,    10,    11,    12,    -1,     7,    -1,     9,    10,
      11,    12,    43,    -1,    -1,    46,    47,    25,    26,    27,
      28,    -1,     1,    -1,    25,    26,    27,    28,     7,    -1,
       9,    10,    -1,    12,    -1,    43,    -1,    -1,    46,    47,
      -1,    -1,    43,    -1,    -1,    46,    47,    -1,     1,    28,
      29,    30,    31,    32,     7,    -1,     9,    10,    -1,    12,
      -1,    -1,    -1,    -1,    43,    -1,    -1,    46,    47,    -1,
      -1,    -1,    -1,    -1,     1,    28,    29,    30,    31,    32,
       7,    -1,     9,    10,    -1,    12,    -1,    -1,    -1,    -1,
      43,    -1,    -1,    46,    47,    -1,    -1,    -1,    -1,    -1,
       1,    28,    29,    30,    31,    32,     7,    -1,     9,    10,
      -1,    12,    -1,    -1,    -1,    -1,    43,    -1,    -1,    46,
      47,    -1,    -1,    -1,    -1,    -1,     1,    28,    29,    30,
      31,    32,     7,    -1,     9,    10,    -1,    12,    -1,    -1,
      -1,    -1,    43,    -1,    -1,    46,    47,    -1,    -1,    -1,
      -1,    -1,     1,    28,    29,    30,    31,    32,     7,    -1,
       9,    10,    -1,    12,    -1,    -1,    -1,    -1,    43,    -1,
      -1,    46,    47,    -1,    -1,    -1,    -1,    -1,     1,    28,
      29,    30,    31,    32,     7,    -1,     9,    10,    -1,    12,
      -1,    -1,    -1,    -1,    43,     1,    -1,    46,    47,    -1,
      -1,     7,    -1,     9,     1,    28,    29,    30,    31,    32,
       7,     1,     9,    10,    -1,    12,    -1,     7,    -1,     9,
      43,     1,    28,    46,    47,    -1,    -1,     7,    -1,     9,
      -1,    28,    29,    30,    31,    32,    -1,    43,    28,    -1,
      46,    47,    -1,    -1,    -1,    -1,    43,    -1,    28,    46,
      47,    -1,     1,    43,    -1,    -1,    46,    47,     7,    -1,
       9,    -1,     1,    43,    -1,    -1,    46,    47,     7,     1,
       9,    -1,    -1,    -1,     1,     7,    -1,     9,    -1,    28,
       7,    -1,     9,    -1,    -1,     1,    -1,    -1,    -1,    28,
      -1,     7,    -1,     9,    43,    -1,    28,    46,    47,    -1,
      -1,    28,    -1,    -1,    43,    -1,    -1,    46,    47,    -1,
      -1,    43,    28,    -1,    46,    47,    43,     1,    -1,    46,
      47,    -1,    -1,     7,    -1,     9,    -1,    43,     8,    -1,
      46,    47,    -1,    -1,    -1,    15,    16,    17,    18,    19,
      20,    -1,    -1,    -1,    28,    -1,    -1,    -1,    -1,    29,
      30,    31,    32,    33,    34,     7,    -1,     9,    10,    43,
      12,    -1,    46,    47,    44,    45,    -1,    -1,    48,    15,
      16,    17,    18,    19,    20,    -1,    28,    29,    30,    31,
      32,    -1,    -1,    29,    30,    31,    32,    33,    34,    -1,
      -1,    43,    43,    -1,    46,    47,    -1,    -1,    -1,    45,
      51,    52,    53,    54,    55,    56,    57,    58,    59,    60,
      61,    62,    63,    64,    65,    66,    15,    16,    17,    18,
      19,    20,    -1,    16,    17,    18,    -1,    -1,    -1,    -1,
      29,    30,    31,    32,    33,    34,    29,    30,    31,    32,
      33,    34,    17,    18,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    -1,    -1,    29,    30,    31,    32,    33,    34,
      16,    17,    18,    -1,    -1,    21,    22,    23,    24,    -1,
      17,    18,    -1,    29,    21,    22,    23,    24,    -1,    -1,
      -1,    -1,    29
  };

  /* STOS_[STATE-NUM] -- The (internal number of the) accessing
     symbol of state STATE-NUM.  */
  const unsigned char
  parser::yystos_[] =
  {
         0,     3,     4,     5,     6,    68,     1,     7,     9,    10,
      11,    12,    25,    26,    27,    28,    43,    46,    47,    48,
      69,    70,    79,    81,    82,    84,    43,    51,    52,    53,
      54,    55,    56,    57,    58,    59,    60,    61,    62,    63,
      64,    65,    66,    69,    70,    85,     7,     9,    12,    28,
      29,    30,    31,    32,    69,    70,    76,    77,    79,    80,
      81,     7,     9,    28,    69,    70,    79,    83,     0,    48,
       1,    84,     1,    80,     1,    84,     1,    84,     1,    84,
       1,    84,    49,    50,    39,    40,    41,    42,    82,    15,
      16,    17,    18,    19,    20,    21,    22,    23,    24,    29,
      48,    70,    85,    85,    85,    85,    85,    85,    85,    85,
      85,    85,    85,    85,    85,    85,    48,    70,     1,    80,
      80,    80,     1,    36,    38,    71,    72,    73,    74,    15,
      16,    17,    18,    19,    20,    33,    34,    44,    45,    48,
      70,    75,    77,    78,     1,    83,     1,    83,    15,    16,
      17,    18,    19,    20,    29,    48,    70,     8,    48,     1,
       8,    48,    13,    48,     1,    13,    14,    48,     1,    84,
       1,    84,     1,    84,     1,    84,     1,    84,     1,    84,
       1,    84,     1,    84,     1,    84,     1,    84,     1,    84,
       1,    84,     1,    84,     1,    84,     1,    84,    85,    85,
      85,    85,    85,    85,    85,    85,    85,    85,     8,    48,
       8,    48,    35,    35,    38,    71,    36,    37,    35,    48,
       1,    80,     1,    80,     1,    80,     1,    80,     1,    80,
       1,    80,     1,    73,    74,     1,    35,    36,    38,    71,
      73,     1,    80,     1,    80,     8,    48,     1,     8,    48,
       1,    83,     1,    83,     1,    83,     1,    83,     1,    83,
       1,    83,     1,    83,     8,    48,    13,    48,    36,    35,
      35,    35,    48,    35,    35,    38,    71,    36,    35,    48,
       8,    48,    35,    36,    35,    35,    35
  };

#if YYDEBUG
  /* TOKEN_NUMBER_[YYLEX-NUM] -- Internal symbol number corresponding
     to YYLEX-NUM.  */
  const unsigned short int
  parser::yytoken_number_[] =
  {
         0,   256,   257,   258,   259,   260,   261,   262,   263,   264,
     265,   266,   267,   268,   269,   270,   271,   272,   273,   274,
     275,   276,   277,   278,   279,   280,   281,   282,   283,   284,
     285,   286,   287,   288,   289,   290,   291,   292,   293,   294,
     295,   296,   297,   298,   299,   300,   301,   302,   303,   304,
     305,    33,    38,   124,    94,   105,   101,    88,    70,    71,
      85,    86,    82,    87,    77,   116,   102
  };
#endif

  /* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
  const unsigned char
  parser::yyr1_[] =
  {
         0,    67,    68,    68,    68,    68,    68,    68,    68,    68,
      68,    68,    68,    68,    68,    68,    68,    68,    69,    70,
      71,    71,    72,    72,    73,    73,    74,    74,    74,    74,
      74,    75,    75,    75,    75,    75,    75,    75,    75,    76,
      76,    77,    77,    77,    77,    77,    78,    78,    78,    79,
      79,    79,    79,    79,    80,    80,    80,    80,    80,    80,
      80,    80,    80,    80,    80,    80,    80,    80,    80,    80,
      80,    80,    80,    80,    80,    80,    80,    80,    80,    80,
      80,    80,    81,    81,    81,    81,    81,    81,    81,    82,
      82,    82,    82,    82,    82,    82,    83,    83,    83,    83,
      83,    83,    83,    83,    83,    83,    83,    83,    83,    83,
      83,    83,    83,    83,    83,    83,    83,    83,    83,    83,
      84,    84,    84,    84,    84,    84,    84,    84,    84,    84,
      84,    84,    84,    84,    84,    84,    84,    84,    84,    84,
      84,    84,    84,    84,    84,    84,    84,    84,    84,    84,
      84,    84,    84,    84,    84,    84,    84,    84,    84,    84,
      84,    84,    84,    84,    85,    85,    85,    85,    85,    85,
      85,    85,    85,    85,    85,    85,    85,    85,    85,    85,
      85
  };

  /* YYR2[YYN] -- Number of symbols composing right hand side of rule YYN.  */
  const unsigned char
  parser::yyr2_[] =
  {
         0,     2,     3,     2,     3,     2,     3,     2,     3,     2,
       3,     2,     3,     2,     3,     2,     3,     2,     1,     2,
       1,     2,     0,     1,     0,     1,     4,     3,     3,     2,
       2,     5,     4,     4,     3,     2,     3,     3,     3,     1,
       1,     1,     1,     2,     3,     3,     2,     3,     3,     1,
       2,     2,     1,     1,     1,     2,     1,     1,     3,     3,
       3,     3,     3,     3,     3,     3,     3,     3,     3,     3,
       3,     3,     2,     1,     2,     2,     3,     3,     3,     3,
       3,     3,     3,     4,     3,     3,     4,     3,     1,     1,
       3,     4,     3,     3,     4,     3,     1,     1,     3,     4,
       3,     3,     4,     3,     3,     3,     3,     3,     3,     3,
       3,     3,     3,     3,     3,     3,     3,     3,     2,     2,
       1,     1,     3,     3,     3,     3,     3,     3,     3,     3,
       3,     3,     3,     3,     3,     3,     3,     3,     3,     3,
       3,     3,     3,     3,     2,     2,     2,     2,     2,     2,
       2,     2,     1,     3,     2,     3,     3,     3,     3,     3,
       3,     3,     3,     1,     1,     2,     3,     3,     3,     3,
       3,     2,     2,     2,     3,     3,     3,     3,     3,     1,
       1
  };

#if YYDEBUG || YYERROR_VERBOSE || YYTOKEN_TABLE
  /* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
     First, the terminals, then, starting at \a yyntokens_, nonterminals.  */
  const char*
  const parser::yytname_[] =
  {
    "$end", "error", "$undefined", "\"LTL start marker\"",
  "\"LBT start marker\"", "\"SERE start marker\"",
  "\"BOOLEAN start marker\"", "\"opening parenthesis\"",
  "\"closing parenthesis\"", "\"(...) block\"", "\"{...} block\"",
  "\"{...}! block\"", "\"opening brace\"", "\"closing brace\"",
  "\"closing brace-bang\"", "\"or operator\"", "\"xor operator\"",
  "\"and operator\"", "\"short and operator\"", "\"implication operator\"",
  "\"equivalent operator\"", "\"until operator\"", "\"release operator\"",
  "\"weak until operator\"", "\"strong release operator\"",
  "\"sometimes operator\"", "\"always operator\"", "\"next operator\"",
  "\"not operator\"", "\"star operator\"", "\"bracket star operator\"",
  "\"plus operator\"", "\"opening bracket for star operator\"",
  "\"opening bracket for equal operator\"",
  "\"opening bracket for goto operator\"", "\"closing bracket\"",
  "\"number for square bracket operator\"", "\"unbounded mark\"",
  "\"separator for square bracket operator\"",
  "\"universal concat operator\"", "\"existential concat operator\"",
  "\"universal non-overlapping concat operator\"",
  "\"existential non-overlapping concat operator\"",
  "\"atomic proposition\"", "\"concat operator\"", "\"fusion operator\"",
  "\"constant true\"", "\"constant false\"", "\"end of formula\"",
  "\"negative suffix\"", "\"positive suffix\"", "'!'", "'&'", "'|'", "'^'",
  "'i'", "'e'", "'X'", "'F'", "'G'", "'U'", "'V'", "'R'", "'W'", "'M'",
  "'t'", "'f'", "$accept", "result", "emptyinput", "enderror",
  "OP_SQBKT_SEP_unbounded", "OP_SQBKT_SEP_opt", "error_opt",
  "sqbracketargs", "gotoargs", "kleen_star", "starargs", "equalargs",
  "booleanatom", "sere", "bracedsere", "parenthesedsubformula",
  "boolformula", "subformula", "lbtformula", 0
  };
#endif

#if YYDEBUG
  /* YYRHS -- A `-1'-separated list of the rules' RHS.  */
  const parser::rhs_number_type
  parser::yyrhs_[] =
  {
        68,     0,    -1,     3,    84,    48,    -1,     3,    70,    -1,
       3,    84,    70,    -1,     3,    69,    -1,     6,    83,    48,
      -1,     6,    70,    -1,     6,    83,    70,    -1,     6,    69,
      -1,     5,    80,    48,    -1,     5,    70,    -1,     5,    80,
      70,    -1,     5,    69,    -1,     4,    85,    48,    -1,     4,
      70,    -1,     4,    85,    70,    -1,     4,    69,    -1,    48,
      -1,     1,    48,    -1,    38,    -1,    38,    37,    -1,    -1,
      71,    -1,    -1,     1,    -1,    36,    38,    36,    35,    -1,
      36,    71,    35,    -1,    38,    36,    35,    -1,    72,    35,
      -1,    36,    35,    -1,    34,    36,    38,    36,    35,    -1,
      34,    36,    71,    35,    -1,    34,    38,    36,    35,    -1,
      34,    71,    35,    -1,    34,    35,    -1,    34,    36,    35,
      -1,    34,     1,    35,    -1,    34,    73,    48,    -1,    29,
      -1,    30,    -1,    76,    -1,    31,    -1,    32,    74,    -1,
      32,     1,    35,    -1,    32,    73,    48,    -1,    33,    74,
      -1,    33,     1,    35,    -1,    33,    73,    48,    -1,    43,
      -1,    43,    50,    -1,    43,    49,    -1,    46,    -1,    47,
      -1,    79,    -1,    28,    80,    -1,    81,    -1,     9,    -1,
       7,    80,     8,    -1,     7,     1,     8,    -1,     7,    80,
      48,    -1,     7,     1,    48,    -1,    80,    17,    80,    -1,
      80,    17,     1,    -1,    80,    18,    80,    -1,    80,    18,
       1,    -1,    80,    15,    80,    -1,    80,    15,     1,    -1,
      80,    44,    80,    -1,    80,    44,     1,    -1,    80,    45,
      80,    -1,    80,    45,     1,    -1,    80,    77,    -1,    77,
      -1,    80,    78,    -1,    80,    75,    -1,    80,    16,    80,
      -1,    80,    16,     1,    -1,    80,    19,    80,    -1,    80,
      19,     1,    -1,    80,    20,    80,    -1,    80,    20,     1,
      -1,    12,    80,    13,    -1,    12,    80,     1,    13,    -1,
      12,     1,    13,    -1,    12,    80,    48,    -1,    12,    80,
       1,    48,    -1,    12,     1,    48,    -1,    10,    -1,     9,
      -1,     7,    84,     8,    -1,     7,    84,     1,     8,    -1,
       7,     1,     8,    -1,     7,    84,    48,    -1,     7,    84,
       1,    48,    -1,     7,     1,    48,    -1,    79,    -1,     9,
      -1,     7,    83,     8,    -1,     7,    83,     1,     8,    -1,
       7,     1,     8,    -1,     7,    83,    48,    -1,     7,    83,
       1,    48,    -1,     7,     1,    48,    -1,    83,    17,    83,
      -1,    83,    17,     1,    -1,    83,    18,    83,    -1,    83,
      18,     1,    -1,    83,    29,    83,    -1,    83,    29,     1,
      -1,    83,    15,    83,    -1,    83,    15,     1,    -1,    83,
      16,    83,    -1,    83,    16,     1,    -1,    83,    19,    83,
      -1,    83,    19,     1,    -1,    83,    20,    83,    -1,    83,
      20,     1,    -1,    28,    83,    -1,    28,     1,    -1,    79,
      -1,    82,    -1,    84,    17,    84,    -1,    84,    17,     1,
      -1,    84,    18,    84,    -1,    84,    18,     1,    -1,    84,
      29,    84,    -1,    84,    29,     1,    -1,    84,    15,    84,
      -1,    84,    15,     1,    -1,    84,    16,    84,    -1,    84,
      16,     1,    -1,    84,    19,    84,    -1,    84,    19,     1,
      -1,    84,    20,    84,    -1,    84,    20,     1,    -1,    84,
      21,    84,    -1,    84,    21,     1,    -1,    84,    22,    84,
      -1,    84,    22,     1,    -1,    84,    23,    84,    -1,    84,
      23,     1,    -1,    84,    24,    84,    -1,    84,    24,     1,
      -1,    25,    84,    -1,    25,     1,    -1,    26,    84,    -1,
      26,     1,    -1,    27,    84,    -1,    27,     1,    -1,    28,
      84,    -1,    28,     1,    -1,    81,    -1,    81,    39,    84,
      -1,    81,    82,    -1,    81,    39,     1,    -1,    81,    40,
      84,    -1,    81,    40,     1,    -1,    81,    41,    84,    -1,
      81,    41,     1,    -1,    81,    42,    84,    -1,    81,    42,
       1,    -1,    12,    80,    14,    -1,    11,    -1,    43,    -1,
      51,    85,    -1,    52,    85,    85,    -1,    53,    85,    85,
      -1,    54,    85,    85,    -1,    55,    85,    85,    -1,    56,
      85,    85,    -1,    57,    85,    -1,    58,    85,    -1,    59,
      85,    -1,    60,    85,    85,    -1,    61,    85,    85,    -1,
      62,    85,    85,    -1,    63,    85,    85,    -1,    64,    85,
      85,    -1,    65,    -1,    66,    -1
  };

  /* YYPRHS[YYN] -- Index of the first RHS symbol of rule number YYN in
     YYRHS.  */
  const unsigned short int
  parser::yyprhs_[] =
  {
         0,     0,     3,     7,    10,    14,    17,    21,    24,    28,
      31,    35,    38,    42,    45,    49,    52,    56,    59,    61,
      64,    66,    69,    70,    72,    73,    75,    80,    84,    88,
      91,    94,   100,   105,   110,   114,   117,   121,   125,   129,
     131,   133,   135,   137,   140,   144,   148,   151,   155,   159,
     161,   164,   167,   169,   171,   173,   176,   178,   180,   184,
     188,   192,   196,   200,   204,   208,   212,   216,   220,   224,
     228,   232,   236,   239,   241,   244,   247,   251,   255,   259,
     263,   267,   271,   275,   280,   284,   288,   293,   297,   299,
     301,   305,   310,   314,   318,   323,   327,   329,   331,   335,
     340,   344,   348,   353,   357,   361,   365,   369,   373,   377,
     381,   385,   389,   393,   397,   401,   405,   409,   413,   416,
     419,   421,   423,   427,   431,   435,   439,   443,   447,   451,
     455,   459,   463,   467,   471,   475,   479,   483,   487,   491,
     495,   499,   503,   507,   511,   514,   517,   520,   523,   526,
     529,   532,   535,   537,   541,   544,   548,   552,   556,   560,
     564,   568,   572,   576,   578,   580,   583,   587,   591,   595,
     599,   603,   606,   609,   612,   616,   620,   624,   628,   632,
     634
  };

  /* YYRLINE[YYN] -- Source line where rule number YYN was defined.  */
  const unsigned short int
  parser::yyrline_[] =
  {
         0,   242,   242,   246,   251,   256,   258,   262,   267,   272,
     274,   278,   283,   288,   290,   294,   299,   304,   307,   313,
     320,   320,   321,   321,   322,   322,   325,   327,   329,   331,
     333,   337,   339,   341,   343,   345,   347,   349,   353,   358,
     358,   360,   362,   364,   366,   370,   375,   377,   381,   390,
     407,   424,   442,   444,   447,   448,   465,   466,   474,   476,
     481,   486,   492,   494,   497,   499,   502,   504,   506,   508,
     510,   512,   514,   523,   533,   555,   577,   604,   606,   627,
     629,   656,   659,   661,   665,   670,   675,   680,   686,   696,
     704,   706,   710,   715,   720,   725,   733,   734,   742,   744,
     748,   753,   758,   763,   769,   771,   773,   775,   777,   779,
     781,   783,   785,   787,   789,   791,   793,   795,   797,   799,
     802,   803,   804,   806,   808,   810,   812,   814,   816,   818,
     820,   822,   824,   826,   828,   830,   832,   834,   836,   838,
     840,   842,   844,   846,   848,   850,   852,   854,   856,   858,
     860,   862,   864,   866,   868,   870,   873,   875,   879,   885,
     889,   895,   899,   903,   914,   931,   933,   935,   937,   939,
     941,   943,   945,   947,   949,   951,   953,   955,   957,   959,
     961
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
       2,     2,     2,    51,     2,     2,     2,     2,    52,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
      58,    59,     2,     2,     2,     2,     2,    64,     2,     2,
       2,     2,    62,     2,     2,    60,    61,    63,    57,     2,
       2,     2,     2,     2,    54,     2,     2,     2,     2,     2,
       2,    56,    66,     2,     2,    55,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,    65,     2,     2,     2,
       2,     2,     2,     2,    53,     2,     2,     2,     2,     2,
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
      15,    16,    17,    18,    19,    20,    21,    22,    23,    24,
      25,    26,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    39,    40,    41,    42,    43,    44,
      45,    46,    47,    48,    49,    50
    };
    if ((unsigned int) t <= yyuser_token_number_max_)
      return translate_table[t];
    else
      return yyundef_token_;
  }

  const int parser::yyeof_ = 0;
  const int parser::yylast_ = 1362;
  const int parser::yynnts_ = 19;
  const int parser::yyempty_ = -2;
  const int parser::yyfinal_ = 68;
  const int parser::yyterror_ = 1;
  const int parser::yyerrcode_ = 256;
  const int parser::yyntokens_ = 67;

  const unsigned int parser::yyuser_token_number_max_ = 305;
  const parser::token_number_type parser::yyundef_token_ = 2;


} // ltlyy

/* Line 1136 of lalr1.cc  */
#line 3312 "ltlparse.cc"


/* Line 1138 of lalr1.cc  */
#line 965 "ltlparse.yy"


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
      flex_set_buffer(ltl_string.c_str(),
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
      flex_set_buffer(ltl_string.c_str(),
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
      flex_set_buffer(ltl_string.c_str(),
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
      flex_set_buffer(sere_string.c_str(),
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

