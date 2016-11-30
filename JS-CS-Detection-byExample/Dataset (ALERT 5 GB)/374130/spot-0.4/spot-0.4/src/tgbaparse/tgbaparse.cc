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
#define yylex   tgbayylex

#include "tgbaparse.hh"

/* User implementation prologue.  */
#line 49 "tgbaparse.yy"

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


/* Line 317 of lalr1.cc.  */
#line 67 "tgbaparse.cc"

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

namespace tgbayy
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
  parser::parser (spot::tgba_parse_error_list& error_list_yyarg, spot::ltl::environment& parse_environment_yyarg, spot::ltl::environment& parse_envacc_yyarg, spot::tgba_explicit*& result_yyarg, formula_cache& fcache_yyarg)
    : yydebug_ (false),
      yycdebug_ (&std::cerr),
      error_list (error_list_yyarg),
      parse_environment (parse_environment_yyarg),
      parse_envacc (parse_envacc_yyarg),
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
#line 88 "tgbaparse.yy"
	{ debug_stream() << *(yyvaluep->str); };
#line 201 "tgbaparse.cc"
	break;
      case 4: /* "UNTERMINATED_STRING" */
#line 88 "tgbaparse.yy"
	{ debug_stream() << *(yyvaluep->str); };
#line 206 "tgbaparse.cc"
	break;
      case 5: /* "IDENT" */
#line 88 "tgbaparse.yy"
	{ debug_stream() << *(yyvaluep->str); };
#line 211 "tgbaparse.cc"
	break;
      case 14: /* "string" */
#line 88 "tgbaparse.yy"
	{ debug_stream() << *(yyvaluep->str); };
#line 216 "tgbaparse.cc"
	break;
      case 15: /* "strident" */
#line 88 "tgbaparse.yy"
	{ debug_stream() << *(yyvaluep->str); };
#line 221 "tgbaparse.cc"
	break;
      case 16: /* "condition" */
#line 88 "tgbaparse.yy"
	{ debug_stream() << *(yyvaluep->str); };
#line 226 "tgbaparse.cc"
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
        case 3: /* "STRING" */
#line 79 "tgbaparse.yy"
	{ delete (yyvaluep->str); };
#line 261 "tgbaparse.cc"
	break;
      case 4: /* "UNTERMINATED_STRING" */
#line 79 "tgbaparse.yy"
	{ delete (yyvaluep->str); };
#line 266 "tgbaparse.cc"
	break;
      case 5: /* "IDENT" */
#line 79 "tgbaparse.yy"
	{ delete (yyvaluep->str); };
#line 271 "tgbaparse.cc"
	break;
      case 14: /* "string" */
#line 79 "tgbaparse.yy"
	{ delete (yyvaluep->str); };
#line 276 "tgbaparse.cc"
	break;
      case 15: /* "strident" */
#line 79 "tgbaparse.yy"
	{ delete (yyvaluep->str); };
#line 281 "tgbaparse.cc"
	break;
      case 16: /* "condition" */
#line 79 "tgbaparse.yy"
	{ delete (yyvaluep->str); };
#line 286 "tgbaparse.cc"
	break;
      case 17: /* "acc_list" */
#line 81 "tgbaparse.yy"
	{
  for (std::list<spot::ltl::formula*>::iterator i = (yyvaluep->list)->begin();
       i != (yyvaluep->list)->end(); ++i)
    spot::ltl::destroy(*i);
  delete (yyvaluep->list);
  };
#line 296 "tgbaparse.cc"
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
	  case 3:
#line 94 "tgbaparse.yy"
    { result->add_state("0"); ;}
    break;

  case 5:
#line 97 "tgbaparse.yy"
    { result->add_state("0"); ;}
    break;

  case 9:
#line 107 "tgbaparse.yy"
    {
	 spot::tgba_explicit::transition* t
	   = result->create_transition(*(yysemantic_stack_[(8) - (1)].str), *(yysemantic_stack_[(8) - (3)].str));
	 if ((yysemantic_stack_[(8) - (5)].str))
	   {
	     formula_cache::const_iterator i = fcache.find(*(yysemantic_stack_[(8) - (5)].str));
	     if (i == fcache.end())
	       {
		 parse_error_list pel;
		 formula* f = spot::ltl::parse(*(yysemantic_stack_[(8) - (5)].str), pel, parse_environment);
		 for (parse_error_list::iterator i = pel.begin();
		      i != pel.end(); ++i)
		   {
		     // Adjust the diagnostic to the current position.
		     location here = (yylocation_stack_[(8) - (1)]);
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
		 fcache[*(yysemantic_stack_[(8) - (5)].str)] = t->condition;
	       }
	     else
	       {
		 t->condition = i->second;
	       }
	     delete (yysemantic_stack_[(8) - (5)].str);
	   }
	 std::list<formula*>::iterator i;
	 for (i = (yysemantic_stack_[(8) - (7)].list)->begin(); i != (yysemantic_stack_[(8) - (7)].list)->end(); ++i)
	   result->add_acceptance_condition(t, *i);
	 delete (yysemantic_stack_[(8) - (1)].str);
	 delete (yysemantic_stack_[(8) - (3)].str);
	 delete (yysemantic_stack_[(8) - (7)].list);
       ;}
    break;

  case 11:
#line 154 "tgbaparse.yy"
    {
	 (yyval.str) = (yysemantic_stack_[(1) - (1)].str);
	 error_list.push_back(spot::tgba_parse_error((yylocation_stack_[(1) - (1)]),
						     "unterminated string"));
       ;}
    break;

  case 14:
#line 163 "tgbaparse.yy"
    {
	 (yyval.str) = 0
       ;}
    break;

  case 15:
#line 167 "tgbaparse.yy"
    {
	 (yyval.str) = (yysemantic_stack_[(1) - (1)].str);
       ;}
    break;

  case 16:
#line 173 "tgbaparse.yy"
    {
	 (yyval.list) = new std::list<formula*>;
       ;}
    break;

  case 17:
#line 177 "tgbaparse.yy"
    {
	 if (*(yysemantic_stack_[(2) - (2)].str) == "true")
	   {
	     (yysemantic_stack_[(2) - (1)].list)->push_back(constant::true_instance());
	   }
	 else if (*(yysemantic_stack_[(2) - (2)].str) != "" && *(yysemantic_stack_[(2) - (2)].str) != "false")
	   {
	     formula* f = parse_envacc.require(*(yysemantic_stack_[(2) - (2)].str));
	     if (! result->has_acceptance_condition(f))
	       {
		 error_list.push_back(spot::tgba_parse_error((yylocation_stack_[(2) - (2)]),
			 "undeclared acceptance condition `" + *(yysemantic_stack_[(2) - (2)].str) + "'"));
		 destroy(f);
		 // $2 will be destroyed on error recovery.
		 YYERROR;
	       }
	     (yysemantic_stack_[(2) - (1)].list)->push_back(f);
	   }
	 delete (yysemantic_stack_[(2) - (2)].str);
	 (yyval.list) = (yysemantic_stack_[(2) - (1)].list);
       ;}
    break;

  case 19:
#line 203 "tgbaparse.yy"
    {
	 formula* f = parse_envacc.require(*(yysemantic_stack_[(2) - (2)].str));
	 if (! f)
	   {
	     std::string s = "acceptance condition `";
	     s += *(yysemantic_stack_[(2) - (2)].str);
	     s += "' unknown in environment `";
	     s += parse_envacc.name();
	     s += "'";
	     error_list.push_back(spot::tgba_parse_error((yylocation_stack_[(2) - (2)]), s));
	     YYERROR;
	   }
	 result->declare_acceptance_condition(f);
	 delete (yysemantic_stack_[(2) - (2)].str);
       ;}
    break;


    /* Line 675 of lalr1.cc.  */
#line 618 "tgbaparse.cc"
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
  const signed char parser::yypact_ninf_ = -12;
  const signed char
  parser::yypact_[] =
  {
        16,   -12,   -12,   -12,   -12,     5,    20,    20,   -12,   -12,
      -2,     6,   -12,    20,   -12,    20,   -12,   -12,     0,    -1,
     -12,     9,   -12,    11,   -12,   -12
  };

  /* YYDEFACT[S] -- default rule to reduce with in state S when YYTABLE
     doesn't specify something else to do.  Zero means the default is an
     error.  */
  const unsigned char
  parser::yydefact_[] =
  {
         5,    10,    11,    13,    18,     0,     3,     4,     7,    12,
       0,     0,     1,     2,     8,     0,     6,    19,     0,    14,
      15,     0,    16,     0,     9,    17
  };

  /* YYPGOTO[NTERM-NUM].  */
  const signed char
  parser::yypgoto_[] =
  {
       -12,   -12,   -12,    21,    -6,     7,   -11,   -12,   -12,   -12
  };

  /* YYDEFGOTO[NTERM-NUM].  */
  const signed char
  parser::yydefgoto_[] =
  {
        -1,     5,     6,     7,     8,     9,    10,    21,    23,    11
  };

  /* YYTABLE[YYPACT[STATE-NUM]].  What to do in state STATE-NUM.  If
     positive, shift that token.  If negative, reduce the rule which
     number is the opposite.  If zero, do what YYDEFACT says.  */
  const signed char parser::yytable_ninf_ = -1;
  const unsigned char
  parser::yytable_[] =
  {
        17,    14,     1,     2,    18,    12,    15,    14,    19,     1,
       2,     3,    25,    16,     1,     2,     3,    22,    24,     1,
       2,     3,     4,     1,     2,     3,    20,    13
  };

  /* YYCHECK.  */
  const unsigned char
  parser::yycheck_[] =
  {
        11,     7,     3,     4,    15,     0,     8,    13,     8,     3,
       4,     5,    23,     7,     3,     4,     5,     8,     7,     3,
       4,     5,     6,     3,     4,     5,    19,     6
  };

  /* STOS_[STATE-NUM] -- The (internal number of the) accessing
     symbol of state STATE-NUM.  */
  const unsigned char
  parser::yystos_[] =
  {
         0,     3,     4,     5,     6,    10,    11,    12,    13,    14,
      15,    18,     0,    12,    13,     8,     7,    15,    15,     8,
      14,    16,     8,    17,     7,    15
  };

#if YYDEBUG
  /* TOKEN_NUMBER_[YYLEX-NUM] -- Internal symbol number corresponding
     to YYLEX-NUM.  */
  const unsigned short int
  parser::yytoken_number_[] =
  {
         0,   256,   257,   258,   259,   260,   261,    59,    44
  };
#endif

  /* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
  const unsigned char
  parser::yyr1_[] =
  {
         0,     9,    10,    10,    10,    10,    11,    12,    12,    13,
      14,    14,    15,    15,    16,    16,    17,    17,    18,    18
  };

  /* YYR2[YYN] -- Number of symbols composing right hand side of rule YYN.  */
  const unsigned char
  parser::yyr2_[] =
  {
         0,     2,     2,     1,     1,     0,     3,     1,     2,     8,
       1,     1,     1,     1,     0,     1,     0,     2,     0,     2
  };

#if YYDEBUG || YYERROR_VERBOSE || YYTOKEN_TABLE
  /* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
     First, the terminals, then, starting at \a yyntokens_, nonterminals.  */
  const char*
  const parser::yytname_[] =
  {
    "$end", "error", "$undefined", "STRING", "UNTERMINATED_STRING", "IDENT",
  "ACC_DEF", "';'", "','", "$accept", "tgba", "acceptance_decl", "lines",
  "line", "string", "strident", "condition", "acc_list", "acc_decl", 0
  };
#endif

#if YYDEBUG
  /* YYRHS -- A `-1'-separated list of the rules' RHS.  */
  const parser::rhs_number_type
  parser::yyrhs_[] =
  {
        10,     0,    -1,    11,    12,    -1,    11,    -1,    12,    -1,
      -1,     6,    18,     7,    -1,    13,    -1,    12,    13,    -1,
      15,     8,    15,     8,    16,     8,    17,     7,    -1,     3,
      -1,     4,    -1,    14,    -1,     5,    -1,    -1,    14,    -1,
      -1,    17,    15,    -1,    -1,    18,    15,    -1
  };

  /* YYPRHS[YYN] -- Index of the first RHS symbol of rule number YYN in
     YYRHS.  */
  const unsigned char
  parser::yyprhs_[] =
  {
         0,     0,     3,     6,     8,    10,    11,    15,    17,    20,
      29,    31,    33,    35,    37,    38,    40,    41,    44,    45
  };

  /* YYRLINE[YYN] -- Source line where rule number YYN was defined.  */
  const unsigned char
  parser::yyrline_[] =
  {
         0,    92,    92,    93,    95,    97,    99,   102,   103,   106,
     152,   153,   160,   160,   163,   166,   173,   176,   201,   202
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
       2,     2,     2,     2,     8,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     7,
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
       5,     6
    };
    if ((unsigned int) t <= yyuser_token_number_max_)
      return translate_table[t];
    else
      return yyundef_token_;
  }

  const int parser::yyeof_ = 0;
  const int parser::yylast_ = 27;
  const int parser::yynnts_ = 10;
  const int parser::yyempty_ = -2;
  const int parser::yyfinal_ = 12;
  const int parser::yyterror_ = 1;
  const int parser::yyerrcode_ = 256;
  const int parser::yyntokens_ = 9;

  const unsigned int parser::yyuser_token_number_max_ = 261;
  const parser::token_number_type parser::yyundef_token_ = 2;

} // namespace tgbayy

#line 220 "tgbaparse.yy"


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

