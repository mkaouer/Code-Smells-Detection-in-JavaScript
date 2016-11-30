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
#define yylex   kripkeyylex

/* First part of user declarations.  */


/* Line 311 of lalr1.cc  */
#line 42 "kripkeparse.cc"


#include "kripkeparse.hh"

/* User implementation prologue.  */


/* Line 317 of lalr1.cc  */
#line 51 "kripkeparse.cc"
/* Unqualified %code blocks.  */

/* Line 318 of lalr1.cc  */
#line 56 "kripkeparse.yy"

#include "kripke/kripkeexplicit.hh"
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
#include <iostream>
  //typedef std::pair<bool, spot::ltl::formula*> pair;



/* Line 318 of lalr1.cc  */
#line 76 "kripkeparse.cc"

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


namespace kripkeyy {

/* Line 380 of lalr1.cc  */
#line 142 "kripkeparse.cc"
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
  parser::parser (spot::kripke_parse_error_list& error_list_yyarg, spot::ltl::environment& parse_environment_yyarg, spot::kripke_explicit*& result_yyarg, formula_cache& fcache_yyarg)
    :
#if YYDEBUG
      yydebug_ (false),
      yycdebug_ (&std::cerr),
#endif
      error_list (error_list_yyarg),
      parse_environment (parse_environment_yyarg),
      result (result_yyarg),
      fcache (fcache_yyarg)
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
        case 3: /* "STRING" */

/* Line 449 of lalr1.cc  */
#line 90 "kripkeparse.yy"
	{ debug_stream() << *(yyvaluep->str); };

/* Line 449 of lalr1.cc  */
#line 222 "kripkeparse.cc"
	break;
      case 4: /* "UNTERMINATED_STRING" */

/* Line 449 of lalr1.cc  */
#line 90 "kripkeparse.yy"
	{ debug_stream() << *(yyvaluep->str); };

/* Line 449 of lalr1.cc  */
#line 231 "kripkeparse.cc"
	break;
      case 5: /* "IDENT" */

/* Line 449 of lalr1.cc  */
#line 90 "kripkeparse.yy"
	{ debug_stream() << *(yyvaluep->str); };

/* Line 449 of lalr1.cc  */
#line 240 "kripkeparse.cc"
	break;
      case 12: /* "string" */

/* Line 449 of lalr1.cc  */
#line 90 "kripkeparse.yy"
	{ debug_stream() << *(yyvaluep->str); };

/* Line 449 of lalr1.cc  */
#line 249 "kripkeparse.cc"
	break;
      case 13: /* "strident" */

/* Line 449 of lalr1.cc  */
#line 90 "kripkeparse.yy"
	{ debug_stream() << *(yyvaluep->str); };

/* Line 449 of lalr1.cc  */
#line 258 "kripkeparse.cc"
	break;
      case 15: /* "condition" */

/* Line 449 of lalr1.cc  */
#line 90 "kripkeparse.yy"
	{ debug_stream() << *(yyvaluep->str); };

/* Line 449 of lalr1.cc  */
#line 267 "kripkeparse.cc"
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
        case 3: /* "STRING" */

/* Line 480 of lalr1.cc  */
#line 81 "kripkeparse.yy"
	{ delete (yyvaluep->str); };

/* Line 480 of lalr1.cc  */
#line 306 "kripkeparse.cc"
	break;
      case 4: /* "UNTERMINATED_STRING" */

/* Line 480 of lalr1.cc  */
#line 81 "kripkeparse.yy"
	{ delete (yyvaluep->str); };

/* Line 480 of lalr1.cc  */
#line 315 "kripkeparse.cc"
	break;
      case 5: /* "IDENT" */

/* Line 480 of lalr1.cc  */
#line 81 "kripkeparse.yy"
	{ delete (yyvaluep->str); };

/* Line 480 of lalr1.cc  */
#line 324 "kripkeparse.cc"
	break;
      case 12: /* "string" */

/* Line 480 of lalr1.cc  */
#line 81 "kripkeparse.yy"
	{ delete (yyvaluep->str); };

/* Line 480 of lalr1.cc  */
#line 333 "kripkeparse.cc"
	break;
      case 13: /* "strident" */

/* Line 480 of lalr1.cc  */
#line 81 "kripkeparse.yy"
	{ delete (yyvaluep->str); };

/* Line 480 of lalr1.cc  */
#line 342 "kripkeparse.cc"
	break;
      case 14: /* "follow_list" */

/* Line 480 of lalr1.cc  */
#line 82 "kripkeparse.yy"
	{
  std::cout << (yyvaluep->list)->size() << std::endl;
  for (std::list<std::string*>::iterator i = (yyvaluep->list)->begin();
       i != (yyvaluep->list)->end(); ++i)
    delete (*i);
  delete (yyvaluep->list);
  };

/* Line 480 of lalr1.cc  */
#line 357 "kripkeparse.cc"
	break;
      case 15: /* "condition" */

/* Line 480 of lalr1.cc  */
#line 81 "kripkeparse.yy"
	{ delete (yyvaluep->str); };

/* Line 480 of lalr1.cc  */
#line 366 "kripkeparse.cc"
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
#line 95 "kripkeparse.yy"
    {
 }
    break;

  case 3:

/* Line 678 of lalr1.cc  */
#line 97 "kripkeparse.yy"
    {
}
    break;

  case 4:

/* Line 678 of lalr1.cc  */
#line 102 "kripkeparse.yy"
    { }
    break;

  case 5:

/* Line 678 of lalr1.cc  */
#line 103 "kripkeparse.yy"
    { }
    break;

  case 6:

/* Line 678 of lalr1.cc  */
#line 108 "kripkeparse.yy"
    {
  result->add_state(*(yysemantic_stack_[(6) - (1)].str));
  if ((yysemantic_stack_[(6) - (3)].str))
  {
    formula_cache::const_iterator i = fcache.find(*(yysemantic_stack_[(6) - (3)].str));
    if (i == fcache.end())
    {
      parse_error_list pel;
      formula* f = spot::ltl::parse(*(yysemantic_stack_[(6) - (3)].str), pel, parse_environment);
      for (parse_error_list::iterator i = pel.begin();
           i != pel.end(); ++i)
      {
        //Adjust the diagnostic to the current position.
        location here = (yylocation_stack_[(6) - (3)]);
        here.end.line = here.begin.line + i->first.end.line - 1;
        here.end.column =
          here.begin.column + i->first.end.column;
        here.begin.line += i->first.begin.line - 1;
        here.begin.column += i->first.begin.column;
        error_list.push_back(spot::kripke_parse_error(here,
                                                    i->second));
      }
      if (f)
        result->add_condition(f, *(yysemantic_stack_[(6) - (1)].str));
      else
        result->add_conditions(bddfalse, *(yysemantic_stack_[(6) - (1)].str));
      fcache[*(yysemantic_stack_[(6) - (3)].str)] = result->state_condition(*(yysemantic_stack_[(6) - (1)].str));
    }
    else
    {
      result->add_conditions(i->second, *(yysemantic_stack_[(6) - (1)].str));
    }
    delete (yysemantic_stack_[(6) - (3)].str);
  }
  std::list<std::string*>::iterator i;
  for (i = (yysemantic_stack_[(6) - (5)].list)->begin(); i != (yysemantic_stack_[(6) - (5)].list)->end(); ++i)
  {
    result->add_transition(*(yysemantic_stack_[(6) - (1)].str), **i);
    delete *i;
  }

  delete (yysemantic_stack_[(6) - (1)].str);
  delete (yysemantic_stack_[(6) - (5)].list);
}
    break;

  case 7:

/* Line 678 of lalr1.cc  */
#line 156 "kripkeparse.yy"
    { (yyval.str) = (yysemantic_stack_[(1) - (1)].str); }
    break;

  case 8:

/* Line 678 of lalr1.cc  */
#line 158 "kripkeparse.yy"
    {
	 (yyval.str) = (yysemantic_stack_[(1) - (1)].str);
         error_list.push_back(spot::kripke_parse_error((yylocation_stack_[(1) - (1)]),
         					     "unterminated string"));
       }
    break;

  case 9:

/* Line 678 of lalr1.cc  */
#line 166 "kripkeparse.yy"
    { (yyval.str) = (yysemantic_stack_[(1) - (1)].str); }
    break;

  case 10:

/* Line 678 of lalr1.cc  */
#line 168 "kripkeparse.yy"
    { (yyval.str) = (yysemantic_stack_[(1) - (1)].str); }
    break;

  case 11:

/* Line 678 of lalr1.cc  */
#line 173 "kripkeparse.yy"
    {
  (yyval.list) = (yysemantic_stack_[(2) - (1)].list);
  (yyval.list)->push_back((yysemantic_stack_[(2) - (2)].str));
}
    break;

  case 12:

/* Line 678 of lalr1.cc  */
#line 177 "kripkeparse.yy"
    {
    (yyval.list) = new std::list<std::string*>;
  }
    break;

  case 13:

/* Line 678 of lalr1.cc  */
#line 183 "kripkeparse.yy"
    {
	 (yyval.str) = 0;
       }
    break;

  case 14:

/* Line 678 of lalr1.cc  */
#line 187 "kripkeparse.yy"
    {
	 (yyval.str) = (yysemantic_stack_[(1) - (1)].str);
       }
    break;



/* Line 678 of lalr1.cc  */
#line 708 "kripkeparse.cc"
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
  const signed char parser::yypact_ninf_ = -4;
  const signed char
  parser::yypact_[] =
  {
         2,    -4,    -4,    -4,     3,     2,    -4,    -4,     4,    -4,
      -4,     5,    -4,     6,    -4,    -3,    -4,    -4
  };

  /* YYDEFACT[S] -- default rule to reduce with in state S when YYTABLE
     doesn't specify something else to do.  Zero means the default is an
     error.  */
  const unsigned char
  parser::yydefact_[] =
  {
         3,     7,     8,    10,     0,     2,     4,     9,     0,     1,
       5,    13,    14,     0,    12,     0,     6,    11
  };

  /* YYPGOTO[NTERM-NUM].  */
  const signed char
  parser::yypgoto_[] =
  {
        -4,    -4,    -4,     8,     0,    -1,    -4,    -4
  };

  /* YYDEFGOTO[NTERM-NUM].  */
  const signed char
  parser::yydefgoto_[] =
  {
        -1,     4,     5,     6,     7,     8,    15,    13
  };

  /* YYTABLE[YYPACT[STATE-NUM]].  What to do in state STATE-NUM.  If
     positive, shift that token.  If negative, reduce the rule which
     number is the opposite.  If zero, do what YYDEFACT says.  */
  const signed char parser::yytable_ninf_ = -1;
  const unsigned char
  parser::yytable_[] =
  {
         1,     2,     3,     9,    16,     1,     2,     3,     1,     2,
      11,    12,    14,    10,    17
  };

  /* YYCHECK.  */
  const unsigned char
  parser::yycheck_[] =
  {
         3,     4,     5,     0,     7,     3,     4,     5,     3,     4,
       6,    11,     6,     5,    15
  };

  /* STOS_[STATE-NUM] -- The (internal number of the) accessing
     symbol of state STATE-NUM.  */
  const unsigned char
  parser::yystos_[] =
  {
         0,     3,     4,     5,     9,    10,    11,    12,    13,     0,
      11,     6,    12,    15,     6,    14,     7,    13
  };

#if YYDEBUG
  /* TOKEN_NUMBER_[YYLEX-NUM] -- Internal symbol number corresponding
     to YYLEX-NUM.  */
  const unsigned short int
  parser::yytoken_number_[] =
  {
         0,   256,   257,   258,   259,   260,   261,   262
  };
#endif

  /* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
  const unsigned char
  parser::yyr1_[] =
  {
         0,     8,     9,     9,    10,    10,    11,    12,    12,    13,
      13,    14,    14,    15,    15
  };

  /* YYR2[YYN] -- Number of symbols composing right hand side of rule YYN.  */
  const unsigned char
  parser::yyr2_[] =
  {
         0,     2,     1,     0,     1,     2,     6,     1,     1,     1,
       1,     2,     0,     0,     1
  };

#if YYDEBUG || YYERROR_VERBOSE || YYTOKEN_TABLE
  /* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
     First, the terminals, then, starting at \a yyntokens_, nonterminals.  */
  const char*
  const parser::yytname_[] =
  {
    "$end", "error", "$undefined", "STRING", "UNTERMINATED_STRING", "IDENT",
  "\",\"", "\";\"", "$accept", "kripke", "lines", "line", "string",
  "strident", "follow_list", "condition", 0
  };
#endif

#if YYDEBUG
  /* YYRHS -- A `-1'-separated list of the rules' RHS.  */
  const parser::rhs_number_type
  parser::yyrhs_[] =
  {
         9,     0,    -1,    10,    -1,    -1,    11,    -1,    10,    11,
      -1,    13,     6,    15,     6,    14,     7,    -1,     3,    -1,
       4,    -1,    12,    -1,     5,    -1,    14,    13,    -1,    -1,
      -1,    12,    -1
  };

  /* YYPRHS[YYN] -- Index of the first RHS symbol of rule number YYN in
     YYRHS.  */
  const unsigned char
  parser::yyprhs_[] =
  {
         0,     0,     3,     5,     6,     8,    11,    18,    20,    22,
      24,    26,    29,    30,    31
  };

  /* YYRLINE[YYN] -- Source line where rule number YYN was defined.  */
  const unsigned char
  parser::yyrline_[] =
  {
         0,    95,    95,    97,   102,   103,   107,   155,   157,   165,
     167,   172,   177,   183,   186
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
       5,     6,     7
    };
    if ((unsigned int) t <= yyuser_token_number_max_)
      return translate_table[t];
    else
      return yyundef_token_;
  }

  const int parser::yyeof_ = 0;
  const int parser::yylast_ = 14;
  const int parser::yynnts_ = 8;
  const int parser::yyempty_ = -2;
  const int parser::yyfinal_ = 9;
  const int parser::yyterror_ = 1;
  const int parser::yyerrcode_ = 256;
  const int parser::yyntokens_ = 8;

  const unsigned int parser::yyuser_token_number_max_ = 262;
  const parser::token_number_type parser::yyundef_token_ = 2;


} // kripkeyy

/* Line 1054 of lalr1.cc  */
#line 1126 "kripkeparse.cc"


/* Line 1056 of lalr1.cc  */
#line 192 "kripkeparse.yy"


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
        (kripke_parse_error(kripkeyy::location(),
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

