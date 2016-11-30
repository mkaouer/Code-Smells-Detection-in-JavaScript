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
#define yylex   neverclaimyylex

/* First part of user declarations.  */


/* Line 293 of lalr1.cc  */
#line 41 "neverclaimparse.cc"


#include "neverclaimparse.hh"

/* User implementation prologue.  */


/* Line 299 of lalr1.cc  */
#line 50 "neverclaimparse.cc"
/* Unqualified %code blocks.  */

/* Line 300 of lalr1.cc  */
#line 50 "neverclaimparse.yy"

#include "ltlast/constant.hh"
  /* Unfortunately Bison 2.3 uses the same guards in all parsers :( */
#undef BISON_POSITION_HH
#undef BISON_LOCATION_HH
#include "ltlparse/public.hh"

/* neverclaimparse.hh and parsedecl.hh include each other recursively.
   We must ensure that YYSTYPE is declared (by the above %union)
   before parsedecl.hh uses it. */
#include "parsedecl.hh"
using namespace spot::ltl;



/* Line 300 of lalr1.cc  */
#line 71 "neverclaimparse.cc"

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


namespace neverclaimyy {

/* Line 382 of lalr1.cc  */
#line 157 "neverclaimparse.cc"

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
  parser::parser (spot::neverclaim_parse_error_list& error_list_yyarg, spot::ltl::environment& parse_environment_yyarg, spot::tgba_explicit_string*& result_yyarg)
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
        case 10: /* "\"boolean formula\"" */

/* Line 449 of lalr1.cc  */
#line 90 "neverclaimparse.yy"
	{
    if ((yyvaluep->str))
      debug_stream() << *(yyvaluep->str);
    else
      debug_stream() << "\"\""; };

/* Line 449 of lalr1.cc  */
#line 238 "neverclaimparse.cc"
	break;
      case 11: /* "\"identifier\"" */

/* Line 449 of lalr1.cc  */
#line 90 "neverclaimparse.yy"
	{
    if ((yyvaluep->str))
      debug_stream() << *(yyvaluep->str);
    else
      debug_stream() << "\"\""; };

/* Line 449 of lalr1.cc  */
#line 251 "neverclaimparse.cc"
	break;
      case 19: /* "ident_list" */

/* Line 449 of lalr1.cc  */
#line 90 "neverclaimparse.yy"
	{
    if ((yyvaluep->str))
      debug_stream() << *(yyvaluep->str);
    else
      debug_stream() << "\"\""; };

/* Line 449 of lalr1.cc  */
#line 264 "neverclaimparse.cc"
	break;
      case 22: /* "formula" */

/* Line 449 of lalr1.cc  */
#line 90 "neverclaimparse.yy"
	{
    if ((yyvaluep->str))
      debug_stream() << *(yyvaluep->str);
    else
      debug_stream() << "\"\""; };

/* Line 449 of lalr1.cc  */
#line 277 "neverclaimparse.cc"
	break;
      case 23: /* "opt_dest" */

/* Line 449 of lalr1.cc  */
#line 90 "neverclaimparse.yy"
	{
    if ((yyvaluep->str))
      debug_stream() << *(yyvaluep->str);
    else
      debug_stream() << "\"\""; };

/* Line 449 of lalr1.cc  */
#line 290 "neverclaimparse.cc"
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
        case 10: /* "\"boolean formula\"" */

/* Line 480 of lalr1.cc  */
#line 79 "neverclaimparse.yy"
	{ delete (yyvaluep->str); };

/* Line 480 of lalr1.cc  */
#line 329 "neverclaimparse.cc"
	break;
      case 11: /* "\"identifier\"" */

/* Line 480 of lalr1.cc  */
#line 79 "neverclaimparse.yy"
	{ delete (yyvaluep->str); };

/* Line 480 of lalr1.cc  */
#line 338 "neverclaimparse.cc"
	break;
      case 19: /* "ident_list" */

/* Line 480 of lalr1.cc  */
#line 79 "neverclaimparse.yy"
	{ delete (yyvaluep->str); };

/* Line 480 of lalr1.cc  */
#line 347 "neverclaimparse.cc"
	break;
      case 21: /* "transitions" */

/* Line 480 of lalr1.cc  */
#line 81 "neverclaimparse.yy"
	{
  for (std::list<pair>::iterator i = (yyvaluep->list)->begin();
       i != (yyvaluep->list)->end(); ++i)
  {
    i->first->destroy();
    delete i->second;
  }
  delete (yyvaluep->list);
  };

/* Line 480 of lalr1.cc  */
#line 364 "neverclaimparse.cc"
	break;
      case 22: /* "formula" */

/* Line 480 of lalr1.cc  */
#line 79 "neverclaimparse.yy"
	{ delete (yyvaluep->str); };

/* Line 480 of lalr1.cc  */
#line 373 "neverclaimparse.cc"
	break;
      case 23: /* "opt_dest" */

/* Line 480 of lalr1.cc  */
#line 79 "neverclaimparse.yy"
	{ delete (yyvaluep->str); };

/* Line 480 of lalr1.cc  */
#line 382 "neverclaimparse.cc"
	break;
      case 24: /* "transition" */

/* Line 480 of lalr1.cc  */
#line 80 "neverclaimparse.yy"
	{ (yyvaluep->p)->first->destroy(); delete (yyvaluep->p)->second; delete (yyvaluep->p); };

/* Line 480 of lalr1.cc  */
#line 391 "neverclaimparse.cc"
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
	  case 7:

/* Line 690 of lalr1.cc  */
#line 109 "neverclaimparse.yy"
    {
      (yyval.str) = (yysemantic_stack_[(2) - (1)].str);
    }
    break;

  case 8:

/* Line 690 of lalr1.cc  */
#line 113 "neverclaimparse.yy"
    {
      result->add_state_alias(*(yysemantic_stack_[(3) - (2)].str), *(yysemantic_stack_[(3) - (1)].str));
      // Keep any identifier that start with accept.
      if (strncmp("accept", (yysemantic_stack_[(3) - (1)].str)->c_str(), 6))
        {
          delete (yysemantic_stack_[(3) - (1)].str);
          (yyval.str) = (yysemantic_stack_[(3) - (2)].str);
        }
      else
        {
	  delete (yysemantic_stack_[(3) - (2)].str);
	  (yyval.str) = (yysemantic_stack_[(3) - (1)].str);
        }
    }
    break;

  case 9:

/* Line 690 of lalr1.cc  */
#line 130 "neverclaimparse.yy"
    {
      spot::state_explicit_string::transition* t = result->create_transition(*(yysemantic_stack_[(2) - (1)].str), *(yysemantic_stack_[(2) - (1)].str));
      bool acc = !strncmp("accept", (yysemantic_stack_[(2) - (1)].str)->c_str(), 6);
      if (acc)
	result->add_acceptance_condition(t,
					 spot::ltl::constant::true_instance());
      delete (yysemantic_stack_[(2) - (1)].str);
    }
    break;

  case 10:

/* Line 690 of lalr1.cc  */
#line 138 "neverclaimparse.yy"
    { delete (yysemantic_stack_[(1) - (1)].str); }
    break;

  case 11:

/* Line 690 of lalr1.cc  */
#line 139 "neverclaimparse.yy"
    { delete (yysemantic_stack_[(2) - (1)].str); }
    break;

  case 12:

/* Line 690 of lalr1.cc  */
#line 141 "neverclaimparse.yy"
    {
      std::list<pair>::iterator it;
      bool acc = !strncmp("accept", (yysemantic_stack_[(4) - (1)].str)->c_str(), 6);
      for (it = (yysemantic_stack_[(4) - (3)].list)->begin(); it != (yysemantic_stack_[(4) - (3)].list)->end(); ++it)
      {
	spot::state_explicit_string::transition* t =
	  result->create_transition(*(yysemantic_stack_[(4) - (1)].str), *it->second);

	result->add_condition(t, it->first);
	if (acc)
	  result
	    ->add_acceptance_condition(t, spot::ltl::constant::true_instance());
      }
      // Free the list
      delete (yysemantic_stack_[(4) - (1)].str);
      for (std::list<pair>::iterator it = (yysemantic_stack_[(4) - (3)].list)->begin();
	   it != (yysemantic_stack_[(4) - (3)].list)->end(); ++it)
	delete it->second;
      delete (yysemantic_stack_[(4) - (3)].list);
    }
    break;

  case 13:

/* Line 690 of lalr1.cc  */
#line 164 "neverclaimparse.yy"
    { (yyval.list) = new std::list<pair>; }
    break;

  case 14:

/* Line 690 of lalr1.cc  */
#line 166 "neverclaimparse.yy"
    {
      if ((yysemantic_stack_[(2) - (2)].p))
	{
	  (yysemantic_stack_[(2) - (1)].list)->push_back(*(yysemantic_stack_[(2) - (2)].p));
	  delete (yysemantic_stack_[(2) - (2)].p);
	}
      (yyval.list) = (yysemantic_stack_[(2) - (1)].list);
    }
    break;

  case 16:

/* Line 690 of lalr1.cc  */
#line 176 "neverclaimparse.yy"
    { (yyval.str) = new std::string("0"); }
    break;

  case 17:

/* Line 690 of lalr1.cc  */
#line 180 "neverclaimparse.yy"
    {
      (yyval.str) = 0;
    }
    break;

  case 18:

/* Line 690 of lalr1.cc  */
#line 184 "neverclaimparse.yy"
    {
      (yyval.str) = (yysemantic_stack_[(3) - (3)].str);
    }
    break;

  case 19:

/* Line 690 of lalr1.cc  */
#line 190 "neverclaimparse.yy"
    {
      // If there is no destination, do ignore the transition.
      // This happens for instance with
      //   if
      //   :: false
      //   fi
      if (!(yysemantic_stack_[(4) - (4)].str))
	{
	  delete (yysemantic_stack_[(4) - (3)].str);
	  (yyval.p) = 0;
	}
      else
	{
	  spot::ltl::parse_error_list pel;
	  const spot::ltl::formula* f =
	    spot::ltl::parse_boolean(*(yysemantic_stack_[(4) - (3)].str), pel, parse_environment,
				     debug_level(), true);
	  delete (yysemantic_stack_[(4) - (3)].str);
	  for(spot::ltl::parse_error_list::const_iterator i = pel.begin();
	  i != pel.end(); ++i)
	    {
	      // Adjust the diagnostic to the current position.
	      location here = (yylocation_stack_[(4) - (3)]);
	      here.end.line = here.begin.line + i->first.end.line - 1;
	      here.end.column = here.begin.column + i->first.end.column -1;
	      here.begin.line += i->first.begin.line - 1;
	      here.begin.column += i->first.begin.column - 1;
	      error(here, i->second);
	    }
	  (yyval.p) = new pair(f, (yysemantic_stack_[(4) - (4)].str));
	}
    }
    break;



/* Line 690 of lalr1.cc  */
#line 763 "neverclaimparse.cc"
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
  const signed char parser::yypact_ninf_ = -11;
  const signed char
  parser::yypact_[] =
  {
         2,    -5,    12,     3,   -11,    -2,   -10,    -3,   -11,   -11,
     -11,     3,   -11,   -11,   -11,     0,   -11,    -6,   -11,   -11,
       4,   -11,     1,   -11,   -11,     9,    10,   -11,     6,   -11
  };

  /* YYDEFACT[S] -- default reduction number in state S.  Performed when
     YYTABLE doesn't specify something else to do.  Zero means the
     default is an error.  */
  const unsigned char
  parser::yydefact_[] =
  {
         0,     0,     0,     3,     1,     0,     0,    10,     4,     7,
       2,     6,     9,    13,    11,     0,     5,     0,     8,    12,
       0,    14,     0,    16,    15,    17,     0,    19,     0,    18
  };

  /* YYPGOTO[NTERM-NUM].  */
  const signed char
  parser::yypgoto_[] =
  {
       -11,   -11,   -11,   -11,    11,   -11,   -11,   -11,   -11
  };

  /* YYDEFGOTO[NTERM-NUM].  */
  const signed char
  parser::yydefgoto_[] =
  {
        -1,     2,     6,     7,     8,    17,    25,    27,    21
  };

  /* YYTABLE[YYPACT[STATE-NUM]].  What to do in state STATE-NUM.  If
     positive, shift that token.  If negative, reduce the rule which
     number is the opposite.  If YYTABLE_NINF_, syntax error.  */
  const signed char parser::yytable_ninf_ = -1;
  const unsigned char
  parser::yytable_[] =
  {
        19,    12,    13,    10,    11,     1,    14,     3,    15,    20,
      23,    24,     4,     9,     5,    18,    26,    29,    28,    22,
       0,     0,    16
  };

  /* YYCHECK.  */
  const signed char
  parser::yycheck_[] =
  {
         6,     4,     5,    13,    14,     3,     9,    12,    11,    15,
       9,    10,     0,    15,    11,    15,     7,    11,     8,    15,
      -1,    -1,    11
  };

  /* STOS_[STATE-NUM] -- The (internal number of the) accessing
     symbol of state STATE-NUM.  */
  const unsigned char
  parser::yystos_[] =
  {
         0,     3,    17,    12,     0,    11,    18,    19,    20,    15,
      13,    14,     4,     5,     9,    11,    20,    21,    15,     6,
      15,    24,    15,     9,    10,    22,     7,    23,     8,    11
  };

#if YYDEBUG
  /* TOKEN_NUMBER_[YYLEX-NUM] -- Internal symbol number corresponding
     to YYLEX-NUM.  */
  const unsigned short int
  parser::yytoken_number_[] =
  {
         0,   256,   257,   258,   259,   260,   261,   262,   263,   264,
     265,   266,   123,   125,    59,    58
  };
#endif

  /* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
  const unsigned char
  parser::yyr1_[] =
  {
         0,    16,    17,    18,    18,    18,    18,    19,    19,    20,
      20,    20,    20,    21,    21,    22,    22,    23,    23,    24
  };

  /* YYR2[YYN] -- Number of symbols composing right hand side of rule YYN.  */
  const unsigned char
  parser::yyr2_[] =
  {
         0,     2,     4,     0,     1,     3,     2,     2,     3,     2,
       1,     2,     4,     0,     2,     1,     1,     0,     3,     4
  };

#if YYDEBUG || YYERROR_VERBOSE || YYTOKEN_TABLE
  /* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
     First, the terminals, then, starting at \a yyntokens_, nonterminals.  */
  const char*
  const parser::yytname_[] =
  {
    "$end", "error", "$undefined", "\"never\"", "\"skip\"", "\"if\"",
  "\"fi\"", "\"->\"", "\"goto\"", "\"false\"", "\"boolean formula\"",
  "\"identifier\"", "'{'", "'}'", "';'", "':'", "$accept", "neverclaim",
  "states", "ident_list", "state", "transitions", "formula", "opt_dest",
  "transition", 0
  };
#endif

#if YYDEBUG
  /* YYRHS -- A `-1'-separated list of the rules' RHS.  */
  const parser::rhs_number_type
  parser::yyrhs_[] =
  {
        17,     0,    -1,     3,    12,    18,    13,    -1,    -1,    20,
      -1,    18,    14,    20,    -1,    18,    14,    -1,    11,    15,
      -1,    19,    11,    15,    -1,    19,     4,    -1,    19,    -1,
      19,     9,    -1,    19,     5,    21,     6,    -1,    -1,    21,
      24,    -1,    10,    -1,     9,    -1,    -1,     7,     8,    11,
      -1,    15,    15,    22,    23,    -1
  };

  /* YYPRHS[YYN] -- Index of the first RHS symbol of rule number YYN in
     YYRHS.  */
  const unsigned char
  parser::yyprhs_[] =
  {
         0,     0,     3,     8,     9,    11,    15,    18,    21,    25,
      28,    30,    33,    38,    39,    42,    44,    46,    47,    51
  };

  /* YYRLINE[YYN] -- Source line where rule number YYN was defined.  */
  const unsigned char
  parser::yyrline_[] =
  {
         0,    98,    98,   101,   103,   104,   105,   108,   112,   129,
     138,   139,   140,   164,   165,   176,   176,   180,   183,   189
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
       2,     2,     2,     2,     2,     2,     2,     2,    15,    14,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,    12,     2,    13,     2,     2,     2,     2,
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
       5,     6,     7,     8,     9,    10,    11
    };
    if ((unsigned int) t <= yyuser_token_number_max_)
      return translate_table[t];
    else
      return yyundef_token_;
  }

  const int parser::yyeof_ = 0;
  const int parser::yylast_ = 22;
  const int parser::yynnts_ = 9;
  const int parser::yyempty_ = -2;
  const int parser::yyfinal_ = 4;
  const int parser::yyterror_ = 1;
  const int parser::yyerrcode_ = 256;
  const int parser::yyntokens_ = 16;

  const unsigned int parser::yyuser_token_number_max_ = 266;
  const parser::token_number_type parser::yyundef_token_ = 2;


} // neverclaimyy

/* Line 1136 of lalr1.cc  */
#line 1260 "neverclaimparse.cc"


/* Line 1138 of lalr1.cc  */
#line 222 "neverclaimparse.yy"


void
neverclaimyy::parser::error(const location_type& location,
			    const std::string& message)
{
  error_list.push_back(spot::neverclaim_parse_error(location, message));
}

namespace spot
{
  tgba_explicit_string*
  neverclaim_parse(const std::string& name,
		   neverclaim_parse_error_list& error_list,
		   bdd_dict* dict,
		   environment& env,
		   bool debug)
  {
    if (neverclaimyyopen(name))
      {
	error_list.push_back
	  (neverclaim_parse_error(neverclaimyy::location(),
				  std::string("Cannot open file ") + name));
	return 0;
      }
    tgba_explicit_string* result = new tgba_explicit_string(dict);
    result->declare_acceptance_condition(spot::ltl::constant::true_instance());
    neverclaimyy::parser parser(error_list, env, result);
    parser.set_debug_level(debug);
    parser.parse();
    neverclaimyyclose();
    return result;
  }
}

// Local Variables:
// mode: c++
// End:

