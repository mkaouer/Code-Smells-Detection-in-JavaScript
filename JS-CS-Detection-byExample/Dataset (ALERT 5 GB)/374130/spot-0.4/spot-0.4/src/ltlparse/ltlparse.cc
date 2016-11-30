/* A Bison parser, made by GNU Bison 2.3.  */

/* Skeleton implementation for Bison LALR(1) parsers in C++

   Copyright (C) 2002, 2003, 2004, 2005, 2006 Free Software Foundation, Inc.

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2, or (at your option)
   any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor,
   Boston, MA 02110-1301, USA.  */

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

#include "ltlparse.hh"

/* User implementation prologue.  */
#line 43 "ltlparse.yy"

/* ltlparse.hh and parsedecl.hh include each other recursively.
   We mut ensure that YYSTYPE is declared (by the above %union)
   before parsedecl.hh uses it. */
#include "parsedecl.hh"
using namespace spot::ltl;

/* Ugly hack so that Bison uses ltlyylex, not yylex.
   (%name-prefix doesn't work for the lalr1.cc skeleton
   at the time of writing.)  */
#define yylex ltlyylex

#define missing_right_op(res, op, str)			\
  do							\
    {							\
      error_list.push_back(parse_error(op,		\
       "missing right operand for \"" str "\""));	\
      res = constant::false_instance();			\
    }							\
  while (0);

#define missing_right_binop(res, left, op, str)	\
  do						\
    {						\
      destroy(left);				\
      missing_right_op(res, op, str);		\
    }						\
  while (0);



/* Line 317 of lalr1.cc.  */
#line 75 "ltlparse.cc"

#ifndef YY_
# if YYENABLE_NLS
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

/* A pseudo ostream that takes yydebug_ into account.  */
# define YYCDEBUG							\
  for (bool yydebugcond_ = yydebug_; yydebugcond_; yydebugcond_ = false)	\
    (*yycdebug_)

/* Enable debugging if requested.  */
#if YYDEBUG

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

# define YY_SYMBOL_PRINT(Title, Type, Value, Location)
# define YY_REDUCE_PRINT(Rule)
# define YY_STACK_PRINT()

#endif /* !YYDEBUG */

#define YYACCEPT	goto yyacceptlab
#define YYABORT		goto yyabortlab
#define YYERROR		goto yyerrorlab

namespace ltlyy
{
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
  parser::parser (spot::ltl::parse_error_list &error_list_yyarg, spot::ltl::environment &parse_environment_yyarg, spot::ltl::formula* &result_yyarg)
    : yydebug_ (false),
      yycdebug_ (&std::cerr),
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
        case 15: /* "\"atomic proposition\"" */
#line 115 "ltlparse.yy"
	{ debug_stream() << *(yyvaluep->str); };
#line 207 "ltlparse.cc"
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
#endif /* ! YYDEBUG */

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
        case 15: /* "\"atomic proposition\"" */
#line 112 "ltlparse.yy"
	{ delete (yyvaluep->str); };
#line 242 "ltlparse.cc"
	break;
      case 24: /* "subformula" */
#line 113 "ltlparse.yy"
	{ spot::ltl::destroy((yyvaluep->ltl)); };
#line 247 "ltlparse.cc"
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


  int
  parser::parse ()
  {
    /// Look-ahead and look-ahead in internal form.
    int yychar = yyempty_;
    int yytoken = 0;

    /* State.  */
    int yyn;
    int yylen = 0;
    int yystate = 0;

    /* Error handling.  */
    int yynerrs_ = 0;
    int yyerrstatus_ = 0;

    /// Semantic value of the look-ahead.
    semantic_type yylval;
    /// Location of the look-ahead.
    location_type yylloc;
    /// The locations where the error started and ended.
    location yyerror_range[2];

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
    goto yybackup;

    /* Backup.  */
  yybackup:

    /* Try to take a decision without look-ahead.  */
    yyn = yypact_[yystate];
    if (yyn == yypact_ninf_)
      goto yydefault;

    /* Read a look-ahead token.  */
    if (yychar == yyempty_)
      {
	YYCDEBUG << "Reading a token: ";
	yychar = yylex (&yylval, &yylloc);
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

    /* Accept?  */
    if (yyn == yyfinal_)
      goto yyacceptlab;

    /* Shift the look-ahead token.  */
    YY_SYMBOL_PRINT ("Shifting", yytoken, &yylval, &yylloc);

    /* Discard the token being shifted unless it is eof.  */
    if (yychar != yyeof_)
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
#line 119 "ltlparse.yy"
    { result = (yysemantic_stack_[(2) - (1)].ltl);
		YYACCEPT;
	      ;}
    break;

  case 3:
#line 123 "ltlparse.yy"
    { error_list.push_back(parse_error((yylocation_stack_[(2) - (1)]),
				      "could not parse anything sensible"));
		result = 0;
		YYABORT;
	      ;}
    break;

  case 4:
#line 129 "ltlparse.yy"
    { error_list.push_back(parse_error((yylocation_stack_[(3) - (2)]),
				      "ignoring trailing garbage"));
		result = (yysemantic_stack_[(3) - (1)].ltl);
		YYACCEPT;
	      ;}
    break;

  case 5:
#line 135 "ltlparse.yy"
    { error_list.push_back(parse_error((yyloc), "empty input"));
		result = 0;
		YYABORT;
	      ;}
    break;

  case 6:
#line 144 "ltlparse.yy"
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
	      ;}
    break;

  case 7:
#line 161 "ltlparse.yy"
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
	      ;}
    break;

  case 8:
#line 178 "ltlparse.yy"
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
	      ;}
    break;

  case 9:
#line 196 "ltlparse.yy"
    { (yyval.ltl) = constant::true_instance(); ;}
    break;

  case 10:
#line 198 "ltlparse.yy"
    { (yyval.ltl) = constant::false_instance(); ;}
    break;

  case 11:
#line 200 "ltlparse.yy"
    { (yyval.ltl) = (yysemantic_stack_[(3) - (2)].ltl); ;}
    break;

  case 12:
#line 202 "ltlparse.yy"
    { error_list.push_back(parse_error((yyloc),
		 "treating this parenthetical block as false"));
		(yyval.ltl) = constant::false_instance();
	      ;}
    break;

  case 13:
#line 207 "ltlparse.yy"
    { error_list.push_back(parse_error((yylocation_stack_[(3) - (1)]) + (yylocation_stack_[(3) - (2)]),
				      "missing closing parenthesis"));
		(yyval.ltl) = (yysemantic_stack_[(3) - (2)].ltl);
	      ;}
    break;

  case 14:
#line 212 "ltlparse.yy"
    { error_list.push_back(parse_error((yyloc),
                    "missing closing parenthesis, "
		    "treating this parenthetical block as false"));
		(yyval.ltl) = constant::false_instance();
	      ;}
    break;

  case 15:
#line 218 "ltlparse.yy"
    { (yyval.ltl) = multop::instance(multop::And, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); ;}
    break;

  case 16:
#line 220 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "and operator"); ;}
    break;

  case 17:
#line 222 "ltlparse.yy"
    { (yyval.ltl) = multop::instance(multop::Or, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); ;}
    break;

  case 18:
#line 224 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "or operator"); ;}
    break;

  case 19:
#line 226 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::Xor, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); ;}
    break;

  case 20:
#line 228 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "xor operator"); ;}
    break;

  case 21:
#line 230 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::Implies, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); ;}
    break;

  case 22:
#line 232 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "implication operator"); ;}
    break;

  case 23:
#line 234 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::Equiv, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); ;}
    break;

  case 24:
#line 236 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "equivalent operator"); ;}
    break;

  case 25:
#line 238 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::U, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); ;}
    break;

  case 26:
#line 240 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "until operator"); ;}
    break;

  case 27:
#line 242 "ltlparse.yy"
    { (yyval.ltl) = binop::instance(binop::R, (yysemantic_stack_[(3) - (1)].ltl), (yysemantic_stack_[(3) - (3)].ltl)); ;}
    break;

  case 28:
#line 244 "ltlparse.yy"
    { missing_right_binop((yyval.ltl), (yysemantic_stack_[(3) - (1)].ltl), (yylocation_stack_[(3) - (2)]), "release operator"); ;}
    break;

  case 29:
#line 246 "ltlparse.yy"
    { (yyval.ltl) = unop::instance(unop::F, (yysemantic_stack_[(2) - (2)].ltl)); ;}
    break;

  case 30:
#line 248 "ltlparse.yy"
    { missing_right_op((yyval.ltl), (yylocation_stack_[(2) - (1)]), "sometimes operator"); ;}
    break;

  case 31:
#line 250 "ltlparse.yy"
    { (yyval.ltl) = unop::instance(unop::G, (yysemantic_stack_[(2) - (2)].ltl)); ;}
    break;

  case 32:
#line 252 "ltlparse.yy"
    { missing_right_op((yyval.ltl), (yylocation_stack_[(2) - (1)]), "always operator"); ;}
    break;

  case 33:
#line 254 "ltlparse.yy"
    { (yyval.ltl) = unop::instance(unop::X, (yysemantic_stack_[(2) - (2)].ltl)); ;}
    break;

  case 34:
#line 256 "ltlparse.yy"
    { missing_right_op((yyval.ltl), (yylocation_stack_[(2) - (1)]), "next operator"); ;}
    break;

  case 35:
#line 258 "ltlparse.yy"
    { (yyval.ltl) = unop::instance(unop::Not, (yysemantic_stack_[(2) - (2)].ltl)); ;}
    break;

  case 36:
#line 260 "ltlparse.yy"
    { missing_right_op((yyval.ltl), (yylocation_stack_[(2) - (1)]), "not operator"); ;}
    break;


    /* Line 675 of lalr1.cc.  */
#line 682 "ltlparse.cc"
	default: break;
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

    yyerror_range[0] = yylloc;
    if (yyerrstatus_ == 3)
      {
	/* If just tried and failed to reuse look-ahead token after an
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

    /* Else will try to reuse look-ahead token after shifting the error
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

    yyerror_range[0] = yylocation_stack_[yylen - 1];
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

	yyerror_range[0] = yylocation_stack_[0];
	yydestruct_ ("Error: popping",
		     yystos_[yystate],
		     &yysemantic_stack_[0], &yylocation_stack_[0]);
	yypop_ ();
	yystate = yystate_stack_[0];
	YY_STACK_PRINT ();
      }

    if (yyn == yyfinal_)
      goto yyacceptlab;

    yyerror_range[1] = yylloc;
    // Using YYLLOC is tempting, but would change the location of
    // the look-ahead.  YYLOC is available though.
    YYLLOC_DEFAULT (yyloc, (yyerror_range - 1), 2);
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
    if (yychar != yyeof_ && yychar != yyempty_)
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
  const signed char parser::yypact_ninf_ = -13;
  const short int
  parser::yypact_[] =
  {
         3,   -12,    43,    51,    69,    76,    14,    94,   -13,   -13,
     -13,    24,    32,   -13,     4,   191,   -13,   -13,   -13,   -13,
     -13,   -13,   -13,   -13,   -13,   -13,   -13,    -5,   101,   119,
     126,   144,   151,   169,   176,   -13,   -13,   -13,   -13,   -13,
     -13,   -13,   197,   -13,     2,   -13,    39,   -13,    63,   -13,
      63,   -13,   -13,   -13,   -13
  };

  /* YYDEFACT[S] -- default rule to reduce with in state S when YYTABLE
     doesn't specify something else to do.  Zero means the default is an
     error.  */
  const unsigned char
  parser::yydefact_[] =
  {
         0,     0,     0,     0,     0,     0,     6,     0,     9,    10,
       5,     0,     0,     3,     0,     0,    30,    29,    32,    31,
      34,    33,     8,     7,    36,    35,     1,     0,     0,     0,
       0,     0,     0,     0,     0,     2,    12,    14,    11,    13,
       4,    18,    17,    20,    19,    16,    15,    22,    21,    24,
      23,    26,    25,    28,    27
  };

  /* YYPGOTO[NTERM-NUM].  */
  const signed char
  parser::yypgoto_[] =
  {
       -13,   -13,    -2
  };

  /* YYDEFGOTO[NTERM-NUM].  */
  const signed char
  parser::yydefgoto_[] =
  {
        -1,    11,    12
  };

  /* YYTABLE[YYPACT[STATE-NUM]].  What to do in state STATE-NUM.  If
     positive, shift that token.  If negative, reduce the rule which
     number is the opposite.  If zero, do what YYDEFACT says.  */
  const signed char parser::yytable_ninf_ = -1;
  const unsigned char
  parser::yytable_[] =
  {
        15,    17,    19,    21,     1,    25,     2,    13,    36,    30,
      31,    32,    33,    34,    40,     3,     4,     5,     6,     7,
       8,     9,    10,    37,    26,     0,    42,    44,    46,    48,
      50,    52,    54,    27,    22,    23,     0,    28,    29,    30,
      31,    32,    33,    34,    14,     0,     2,    31,    32,    33,
      34,    35,    16,     0,     2,     3,     4,     5,     6,     7,
       8,     9,     0,     3,     4,     5,     6,     7,     8,     9,
      18,     0,     2,    33,    34,     0,     0,    20,     0,     2,
       0,     3,     4,     5,     6,     7,     8,     9,     3,     4,
       5,     6,     7,     8,     9,    24,     0,     2,     0,     0,
       0,     0,    41,     0,     2,     0,     3,     4,     5,     6,
       7,     8,     9,     3,     4,     5,     6,     7,     8,     9,
      43,     0,     2,     0,     0,     0,     0,    45,     0,     2,
       0,     3,     4,     5,     6,     7,     8,     9,     3,     4,
       5,     6,     7,     8,     9,    47,     0,     2,     0,     0,
       0,     0,    49,     0,     2,     0,     3,     4,     5,     6,
       7,     8,     9,     3,     4,     5,     6,     7,     8,     9,
      51,     0,     2,     0,     0,     0,     0,    53,     0,     2,
       0,     3,     4,     5,     6,     7,     8,     9,     3,     4,
       5,     6,     7,     8,     9,    38,    28,    29,    30,    31,
      32,    33,    34,    29,    30,    31,    32,    33,    34,     0,
      39
  };

  /* YYCHECK.  */
  const signed char
  parser::yycheck_[] =
  {
         2,     3,     4,     5,     1,     7,     3,    19,     4,     7,
       8,     9,    10,    11,    19,    12,    13,    14,    15,    16,
      17,    18,    19,    19,     0,    -1,    28,    29,    30,    31,
      32,    33,    34,     1,    20,    21,    -1,     5,     6,     7,
       8,     9,    10,    11,     1,    -1,     3,     8,     9,    10,
      11,    19,     1,    -1,     3,    12,    13,    14,    15,    16,
      17,    18,    -1,    12,    13,    14,    15,    16,    17,    18,
       1,    -1,     3,    10,    11,    -1,    -1,     1,    -1,     3,
      -1,    12,    13,    14,    15,    16,    17,    18,    12,    13,
      14,    15,    16,    17,    18,     1,    -1,     3,    -1,    -1,
      -1,    -1,     1,    -1,     3,    -1,    12,    13,    14,    15,
      16,    17,    18,    12,    13,    14,    15,    16,    17,    18,
       1,    -1,     3,    -1,    -1,    -1,    -1,     1,    -1,     3,
      -1,    12,    13,    14,    15,    16,    17,    18,    12,    13,
      14,    15,    16,    17,    18,     1,    -1,     3,    -1,    -1,
      -1,    -1,     1,    -1,     3,    -1,    12,    13,    14,    15,
      16,    17,    18,    12,    13,    14,    15,    16,    17,    18,
       1,    -1,     3,    -1,    -1,    -1,    -1,     1,    -1,     3,
      -1,    12,    13,    14,    15,    16,    17,    18,    12,    13,
      14,    15,    16,    17,    18,     4,     5,     6,     7,     8,
       9,    10,    11,     6,     7,     8,     9,    10,    11,    -1,
      19
  };

  /* STOS_[STATE-NUM] -- The (internal number of the) accessing
     symbol of state STATE-NUM.  */
  const unsigned char
  parser::yystos_[] =
  {
         0,     1,     3,    12,    13,    14,    15,    16,    17,    18,
      19,    23,    24,    19,     1,    24,     1,    24,     1,    24,
       1,    24,    20,    21,     1,    24,     0,     1,     5,     6,
       7,     8,     9,    10,    11,    19,     4,    19,     4,    19,
      19,     1,    24,     1,    24,     1,    24,     1,    24,     1,
      24,     1,    24,     1,    24
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
         0,    22,    23,    23,    23,    23,    24,    24,    24,    24,
      24,    24,    24,    24,    24,    24,    24,    24,    24,    24,
      24,    24,    24,    24,    24,    24,    24,    24,    24,    24,
      24,    24,    24,    24,    24,    24,    24
  };

  /* YYR2[YYN] -- Number of symbols composing right hand side of rule YYN.  */
  const unsigned char
  parser::yyr2_[] =
  {
         0,     2,     2,     2,     3,     1,     1,     2,     2,     1,
       1,     3,     3,     3,     3,     3,     3,     3,     3,     3,
       3,     3,     3,     3,     3,     3,     3,     3,     3,     2,
       2,     2,     2,     2,     2,     2,     2
  };

#if YYDEBUG || YYERROR_VERBOSE || YYTOKEN_TABLE
  /* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
     First, the terminals, then, starting at \a yyntokens_, nonterminals.  */
  const char*
  const parser::yytname_[] =
  {
    "$end", "error", "$undefined", "\"opening parenthesis\"",
  "\"closing parenthesis\"", "\"or operator\"", "\"xor operator\"",
  "\"and operator\"", "\"implication operator\"",
  "\"equivalent operator\"", "\"until operator\"", "\"release operator\"",
  "\"sometimes operator\"", "\"always operator\"", "\"next operator\"",
  "\"atomic proposition\"", "\"not operator\"", "\"constant true\"",
  "\"constant false\"", "\"end of formula\"", "\"negative suffix\"",
  "\"positive suffix\"", "$accept", "result", "subformula", 0
  };
#endif

#if YYDEBUG
  /* YYRHS -- A `-1'-separated list of the rules' RHS.  */
  const parser::rhs_number_type
  parser::yyrhs_[] =
  {
        23,     0,    -1,    24,    19,    -1,     1,    19,    -1,    24,
       1,    19,    -1,    19,    -1,    15,    -1,    15,    21,    -1,
      15,    20,    -1,    17,    -1,    18,    -1,     3,    24,     4,
      -1,     3,     1,     4,    -1,     3,    24,    19,    -1,     3,
       1,    19,    -1,    24,     7,    24,    -1,    24,     7,     1,
      -1,    24,     5,    24,    -1,    24,     5,     1,    -1,    24,
       6,    24,    -1,    24,     6,     1,    -1,    24,     8,    24,
      -1,    24,     8,     1,    -1,    24,     9,    24,    -1,    24,
       9,     1,    -1,    24,    10,    24,    -1,    24,    10,     1,
      -1,    24,    11,    24,    -1,    24,    11,     1,    -1,    12,
      24,    -1,    12,     1,    -1,    13,    24,    -1,    13,     1,
      -1,    14,    24,    -1,    14,     1,    -1,    16,    24,    -1,
      16,     1,    -1
  };

  /* YYPRHS[YYN] -- Index of the first RHS symbol of rule number YYN in
     YYRHS.  */
  const unsigned char
  parser::yyprhs_[] =
  {
         0,     0,     3,     6,     9,    13,    15,    17,    20,    23,
      25,    27,    31,    35,    39,    43,    47,    51,    55,    59,
      63,    67,    71,    75,    79,    83,    87,    91,    95,    99,
     102,   105,   108,   111,   114,   117,   120
  };

  /* YYRLINE[YYN] -- Source line where rule number YYN was defined.  */
  const unsigned short int
  parser::yyrline_[] =
  {
         0,   118,   118,   122,   128,   134,   143,   160,   177,   195,
     197,   199,   201,   206,   211,   217,   219,   221,   223,   225,
     227,   229,   231,   233,   235,   237,   239,   241,   243,   245,
     247,   249,   251,   253,   255,   257,   259
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
	       << " (line " << yylno << "), ";
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
  const int parser::yylast_ = 210;
  const int parser::yynnts_ = 3;
  const int parser::yyempty_ = -2;
  const int parser::yyfinal_ = 26;
  const int parser::yyterror_ = 1;
  const int parser::yyerrcode_ = 256;
  const int parser::yyntokens_ = 22;

  const unsigned int parser::yyuser_token_number_max_ = 276;
  const parser::token_number_type parser::yyundef_token_ = 2;

} // namespace ltlyy

#line 263 "ltlparse.yy"


void
ltlyy::parser::error(const location_type& location, const std::string& message)
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
      ltlyy::parser parser(error_list, env, result);
      parser.set_debug_level(debug);
      parser.parse();
      return result;
    }
  }
}

// Local Variables:
// mode: c++
// End:

