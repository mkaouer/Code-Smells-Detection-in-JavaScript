/* A Bison parser, made by GNU Bison 1.875c.  */

/* C++ Skeleton parser for LALR(1) parsing with Bison,
   Copyright (C) 2002, 2003 Free Software Foundation, Inc.

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
   Foundation, Inc., 59 Temple Place - Suite 330,
   Boston, MA 02111-1307, USA.  */

#include "/home/adl/proj/spot/src/tgbaparse/tgbaparse.hh"

/* Enable debugging if requested.  */
#if YYDEBUG
# define YYCDEBUG    if (debug_) cdebug_
# define YY_REDUCE_PRINT(Rule)		\
do {					\
  if (debug_)				\
    reduce_print_ (Rule);		\
} while (0)
# define YY_STACK_PRINT()		\
do {					\
  if (debug_)				\
    stack_print_ ();			\
} while (0)
#else /* !YYDEBUG */
# define YYCDEBUG    if (0) cdebug_
# define YY_REDUCE_PRINT(Rule)
# define YY_STACK_PRINT()
#endif /* !YYDEBUG */

#define YYACCEPT	goto yyacceptlab
#define YYABORT		goto yyabortlab
#define YYERROR		goto yyerrorlab


int
yy::Parser::parse ()
{
  int nerrs = 0;
  int errstatus = 0;

  /* Initialize the stacks.  The initial state will be pushed in
     yynewstate, since the latter expects the semantical and the
     location values to have been already stored, initialize these
     stacks with a primary value.  */
  state_stack_ = StateStack (0);
  semantic_stack_ = SemanticStack (1);
  location_stack_ = LocationStack (1);

  /* Start.  */
  state_ = 0;
  looka_ = empty_;
#if YYLSP_NEEDED
  location = initlocation_;
#endif
  YYCDEBUG << "Starting parse" << std::endl;

  /* New state.  */
 yynewstate:
  state_stack_.push (state_);
  YYCDEBUG << "Entering state " << state_ << std::endl;
  goto yybackup;

  /* Backup.  */
 yybackup:

  /* Try to take a decision without lookahead.  */
  n_ = pact_[state_];
  if (n_ == pact_ninf_)
    goto yydefault;

  /* Read a lookahead token.  */
  if (looka_ == empty_)
    {
      YYCDEBUG << "Reading a token: ";
      lex_ ();
    }

  /* Convert token to internal form.  */
  if (looka_ <= 0)
    {
      looka_ = eof_;
      ilooka_ = 0;
      YYCDEBUG << "Now at end of input." << std::endl;
    }
  else
    {
      ilooka_ = translate_ (looka_);
#if YYDEBUG
      if (debug_)
	{
	  YYCDEBUG << "Next token is " << looka_
		 << " (" << name_[ilooka_];
	  print_ ();
	  YYCDEBUG << ')' << std::endl;
	}
#endif
    }

  /* If the proper action on seeing token YYTOKEN is to reduce or to
     detect an error, take that action.  */
  n_ += ilooka_;
  if (n_ < 0 || last_ < n_ || check_[n_] != ilooka_)
    goto yydefault;

  /* Reduce or error.  */
  n_ = table_[n_];
  if (n_ < 0)
    {
      if (n_ == table_ninf_)
	goto yyerrlab;
      else
	{
	  n_ = -n_;
	  goto yyreduce;
	}
    }
  else if (n_ == 0)
    goto yyerrlab;

  /* Accept?  */
  if (n_ == final_)
    goto yyacceptlab;

  /* Shift the lookahead token.  */
#if YYDEBUG
  YYCDEBUG << "Shifting token " << looka_
           << " (" << name_[ilooka_] << "), ";
#endif

  /* Discard the token being shifted unless it is eof.  */
  if (looka_ != eof_)
    looka_ = empty_;

  semantic_stack_.push (value);
  location_stack_.push (location);

  /* Count tokens shifted since error; after three, turn off error
     status.  */
  if (errstatus)
    --errstatus;

  state_ = n_;
  goto yynewstate;

/*-----------------------------------------------------------.
| yydefault -- do the default action for the current state.  |
`-----------------------------------------------------------*/
 yydefault:
  n_ = defact_[state_];
  if (n_ == 0)
    goto yyerrlab;
  goto yyreduce;

/*-----------------------------.
| yyreduce -- Do a reduction.  |
`-----------------------------*/
 yyreduce:
  len_ = r2_[n_];
  /* If LEN_ is nonzero, implement the default value of the action:
     `$$ = $1'.  Otherwise, use the top of the stack.

     Otherwise, the following line sets YYVAL to garbage.
     This behavior is undocumented and Bison
     users should not rely upon it.  */
  if (len_)
    {
      yyval = semantic_stack_[len_ - 1];
      yyloc = location_stack_[len_ - 1];
    }
  else
    {
      yyval = semantic_stack_[0];
      yyloc = location_stack_[0];
    }

  if (len_)
    {
      Slice< LocationType, LocationStack > slice (location_stack_, len_);
      YYLLOC_DEFAULT (yyloc, slice, len_);
    }
  YY_REDUCE_PRINT (n_);
  switch (n_)
    {
        case 7:
#line 77 "tgbaparse.yy"
    {
	 spot::tgba_explicit::transition* t
	   = result->create_transition(*semantic_stack_[7].str, *semantic_stack_[5].str);
	 result->add_condition(t, semantic_stack_[3].f);
	 std::list<formula*>::iterator i;
	 for (i = semantic_stack_[1].list->begin(); i != semantic_stack_[1].list->end(); ++i)
	   result->add_acceptance_condition(t, *i);
	 delete semantic_stack_[7].str;
	 delete semantic_stack_[5].str;
	 delete semantic_stack_[1].list;
       ;}
    break;

  case 9:
#line 92 "tgbaparse.yy"
    {
	 error_list.push_back(spot::tgba_parse_error(location_stack_[0],
						     "unterminated string"));
       ;}
    break;

  case 12:
#line 100 "tgbaparse.yy"
    {
	 yyval.f = constant::true_instance();
       ;}
    break;

  case 13:
#line 104 "tgbaparse.yy"
    {
	 parse_error_list pel;
	 formula* f = spot::ltl::parse(*semantic_stack_[0].str, pel, parse_environment);
	 for (parse_error_list::iterator i = pel.begin();
	      i != pel.end(); ++i)
	   {
	     // Adjust the diagnostic to the current position.
	     Location here = location_stack_[0];
	     here.begin.line += i->first.begin.line;
	     here.begin.column += i->first.begin.column;
	     here.end.line = here.begin.line + i->first.begin.line;
	     here.end.column = here.begin.column + i->first.begin.column;
	     error_list.push_back(spot::tgba_parse_error(here, i->second));
	   }
	 delete semantic_stack_[0].str;
	 yyval.f = f ? f : constant::false_instance();
       ;}
    break;

  case 14:
#line 124 "tgbaparse.yy"
    {
	 yyval.list = new std::list<formula*>;
       ;}
    break;

  case 15:
#line 128 "tgbaparse.yy"
    {
	 if (*semantic_stack_[0].str == "true")
	   {
	     semantic_stack_[1].list->push_back(constant::true_instance());
	   }
	 else if (*semantic_stack_[0].str != "" && *semantic_stack_[0].str != "false")
	   {
	     formula* f = parse_environment.require(*semantic_stack_[0].str);
	     if (! result->has_acceptance_condition(f))
	       {
		 error_list.push_back(spot::tgba_parse_error(location_stack_[0],
			 "undeclared acceptance condition"));
		 destroy(f);
		 delete semantic_stack_[0].str;
		 YYERROR;
	       }
	     semantic_stack_[1].list->push_back(f);
	   }
	 delete semantic_stack_[0].str;
	 yyval.list = semantic_stack_[1].list;
       ;}
    break;

  case 17:
#line 154 "tgbaparse.yy"
    {
	 formula* f = parse_environment.require(*semantic_stack_[0].str);
	 result->declare_acceptance_condition(f);
	 delete semantic_stack_[0].str;
       ;}
    break;


    }

/* Line 500 of lalr1.cc.  */
#line 293 "/home/adl/proj/spot/src/tgbaparse/tgbaparse.cc"

  state_stack_.pop (len_);
  semantic_stack_.pop (len_);
  location_stack_.pop (len_);

  YY_STACK_PRINT ();

  semantic_stack_.push (yyval);
  location_stack_.push (yyloc);

  /* Shift the result of the reduction.  */
  n_ = r1_[n_];
  state_ = pgoto_[n_ - ntokens_] + state_stack_[0];
  if (0 <= state_ && state_ <= last_ && check_[state_] == state_stack_[0])
    state_ = table_[state_];
  else
    state_ = defgoto_[n_ - ntokens_];
  goto yynewstate;

/*------------------------------------.
| yyerrlab -- here on detecting error |
`------------------------------------*/
 yyerrlab:
  /* If not already recovering from an error, report this error.  */
  if (!errstatus)
    {
      ++nerrs;

#if YYERROR_VERBOSE
      n_ = pact_[state_];
      if (pact_ninf_ < n_ && n_ < last_)
	{
	  message = "syntax error, unexpected ";
	  message += name_[ilooka_];
	  {
	    int count = 0;
            /* Start YYX at -YYN if negative to avoid negative indexes in
               YYCHECK.  */
	    int xbegin = n_ < 0 ? -n_ : 0;
 	    /* Stay within bounds of both yycheck and yytname.  */
	    int checklim = last_ - n_;
	    int xend = checklim < ntokens_ ? checklim : ntokens_;
	    for (int x = xbegin; x < xend; ++x)
	      if (check_[x + n_] == x && x != terror_)
		++count;
	    if (count < 5)
	      {
		count = 0;
		for (int x1 = xbegin; x1 < xend; ++x1)
		  if (check_[x1 + n_] == x1 && x1 != terror_)
		    {
		      message += (!count++) ? ", expecting " : " or ";
		      message += name_[x1];
		    }
	      }
	  }
	}
      else
#endif
	message = "syntax error";
      error_ ();
    }

  if (errstatus == 3)
    {
      /* If just tried and failed to reuse lookahead token after an
	 error, discard it.  */

      /* Return failure if at end of input.  */
      if (looka_ <= eof_)
        {
          /* If at end of input, pop the error token,
	     then the rest of the stack, then return failure.  */
	  if (looka_ == eof_)
	     for (;;)
	       {
                 state_stack_.pop ();
                 semantic_stack_.pop ();
                 location_stack_.pop ();
		 if (state_stack_.height () == 1)
		   YYABORT;
//		 YYDSYMPRINTF ("Error: popping", yystos[*yyssp], yyvsp, yylsp);
// FIXME: yydestruct (yystos[*yyssp], yyvsp, yylsp);
	       }
        }
      else
        {
#if YYDEBUG
           YYCDEBUG << "Discarding token " << looka_
  	            << " (" << name_[ilooka_] << ")." << std::endl;
//	  yydestruct (yytoken, &yylval, &yylloc);
#endif
           looka_ = empty_;
        }
    }

  /* Else will try to reuse lookahead token after shifting the error
     token.  */
  goto yyerrlab1;


/*---------------------------------------------------.
| yyerrorlab -- error raised explicitly by YYERROR.  |
`---------------------------------------------------*/
yyerrorlab:

  state_stack_.pop (len_);
  semantic_stack_.pop (len_);
  location_stack_.pop (len_);
  state_ = state_stack_[0];
  goto yyerrlab1;

/*-------------------------------------------------------------.
| yyerrlab1 -- common code for both syntax error and YYERROR.  |
`-------------------------------------------------------------*/
yyerrlab1:
  errstatus = 3;	/* Each real token shifted decrements this.  */

  for (;;)
    {
      n_ = pact_[state_];
      if (n_ != pact_ninf_)
	{
	  n_ += terror_;
	  if (0 <= n_ && n_ <= last_ && check_[n_] == terror_)
	    {
	      n_ = table_[n_];
	      if (0 < n_)
		break;
	    }
	}

      /* Pop the current state because it cannot handle the error token.  */
      if (state_stack_.height () == 1)
	YYABORT;

#if YYDEBUG
      if (debug_)
	{
	  if (stos_[state_] < ntokens_)
	    {
	      YYCDEBUG << "Error: popping token "
		     << token_number_[stos_[state_]]
		     << " (" << name_[stos_[state_]];
# ifdef YYPRINT
	      YYPRINT (stderr, token_number_[stos_[state_]],
		       semantic_stack_.top ());
# endif
	      YYCDEBUG << ')' << std::endl;
	    }
	  else
	    {
	      YYCDEBUG << "Error: popping nonterminal ("
		     << name_[stos_[state_]] << ')' << std::endl;
	    }
	}
#endif

      state_stack_.pop ();
      semantic_stack_.pop ();
      location_stack_.pop ();
      state_ = state_stack_[0];
      YY_STACK_PRINT ();
    }

  if (n_ == final_)
    goto yyacceptlab;

  YYCDEBUG << "Shifting error token, ";

  semantic_stack_.push (value);
  location_stack_.push (location);

  state_ = n_;
  goto yynewstate;

  /* Accept.  */
 yyacceptlab:
  return 0;

  /* Abort.  */
 yyabortlab:
  return 1;
}

void
yy::Parser::lex_ ()
{
#if YYLSP_NEEDED
  looka_ = yylex (&value, &location);
#else
  looka_ = yylex (&value);
#endif
}


/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
const signed char yy::Parser::pact_ninf_ = -12;
const signed char
yy::Parser::pact_[] =
{
      16,   -12,   -12,   -12,   -12,     5,    20,    20,   -12,   -12,
      -2,     6,   -12,    20,   -12,    20,   -12,   -12,     0,    -1,
     -12,     9,   -12,    11,   -12,   -12
};

/* YYDEFACT[S] -- default rule to reduce with in state S when YYTABLE
   doesn't specify something else to do.  Zero means the default is an
   error.  */
const unsigned char
yy::Parser::defact_[] =
{
       0,     8,     9,    11,    16,     0,     0,     3,     5,    10,
       0,     0,     1,     2,     6,     0,     4,    17,     0,    12,
      13,     0,    14,     0,     7,    15
};

/* YYPGOTO[NTERM-NUM].  */
const signed char
yy::Parser::pgoto_[] =
{
     -12,   -12,   -12,    21,    -6,     7,   -11,   -12,   -12,   -12
};

/* YYDEFGOTO[NTERM-NUM].  */
const signed char
yy::Parser::defgoto_[] =
{
      -1,     5,     6,     7,     8,     9,    10,    21,    23,    11
};

/* YYTABLE[YYPACT[STATE-NUM]].  What to do in state STATE-NUM.  If
   positive, shift that token.  If negative, reduce the rule which
   number is the opposite.  If zero, do what YYDEFACT says.  */
const signed char yy::Parser::table_ninf_ = -1;
const unsigned char
yy::Parser::table_[] =
{
      17,    14,     1,     2,    18,    12,    15,    14,    19,     1,
       2,     3,    25,    16,     1,     2,     3,    22,    24,     1,
       2,     3,     4,     1,     2,     3,    20,    13
};

/* YYCHECK.  */
const unsigned char
yy::Parser::check_[] =
{
      11,     7,     3,     4,    15,     0,     8,    13,     8,     3,
       4,     5,    23,     7,     3,     4,     5,     8,     7,     3,
       4,     5,     6,     3,     4,     5,    19,     6
};

#if YYDEBUG
/* STOS_[STATE-NUM] -- The (internal number of the) accessing
   symbol of state STATE-NUM.  */
const unsigned char
yy::Parser::stos_[] =
{
       0,     3,     4,     5,     6,    10,    11,    12,    13,    14,
      15,    18,     0,    12,    13,     8,     7,    15,    15,     8,
      14,    16,     8,    17,     7,    15
};

/* TOKEN_NUMBER_[YYLEX-NUM] -- Internal token number corresponding
   to YYLEX-NUM.  */
const unsigned short
yy::Parser::token_number_[] =
{
       0,   256,   257,   258,   259,   260,   261,    59,    44
};
#endif

/* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
const unsigned char
yy::Parser::r1_[] =
{
       0,     9,    10,    10,    11,    12,    12,    13,    14,    14,
      15,    15,    16,    16,    17,    17,    18,    18
};

/* YYR2[YYN] -- Number of symbols composing right hand side of rule YYN.  */
const unsigned char
yy::Parser::r2_[] =
{
       0,     2,     2,     1,     3,     1,     2,     8,     1,     1,
       1,     1,     0,     1,     0,     2,     0,     2
};

#if YYDEBUG || YYERROR_VERBOSE
/* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
   First, the terminals, then, starting at YYNTOKENS, nonterminals. */
const char*
const yy::Parser::name_[] =
{
  "$end", "error", "$undefined", "STRING", "UNTERMINATED_STRING", "IDENT",
  "ACC_DEF", "';'", "','", "$accept", "tgba", "acceptance_decl", "lines",
  "line", "string", "strident", "condition", "acc_list", "acc_decl", 0
};
#endif

#if YYDEBUG
/* YYRHS -- A `-1'-separated list of the rules' RHS. */
const yy::Parser::RhsNumberType
yy::Parser::rhs_[] =
{
      10,     0,    -1,    11,    12,    -1,    12,    -1,     6,    18,
       7,    -1,    13,    -1,    12,    13,    -1,    15,     8,    15,
       8,    16,     8,    17,     7,    -1,     3,    -1,     4,    -1,
      14,    -1,     5,    -1,    -1,    14,    -1,    -1,    17,    15,
      -1,    -1,    18,    15,    -1
};

/* YYPRHS[YYN] -- Index of the first RHS symbol of rule number YYN in
   YYRHS.  */
const unsigned char
yy::Parser::prhs_[] =
{
       0,     0,     3,     6,     8,    12,    14,    17,    26,    28,
      30,    32,    34,    35,    37,    38,    41,    42
};

/* YYRLINE[YYN] -- source line where rule number YYN was defined.  */
const unsigned char
yy::Parser::rline_[] =
{
       0,    67,    67,    67,    69,    72,    73,    76,    90,    91,
      97,    97,   100,   103,   124,   127,   152,   153
};

/** Print the state stack from its BOTTOM up to its TOP (included).  */

void
yy::Parser::stack_print_ ()
{
  cdebug_ << "state stack now";
  for (StateStack::ConstIterator i = state_stack_.begin ();
       i != state_stack_.end (); ++i)
    cdebug_ << ' ' << *i;
  cdebug_ << std::endl;
}

/** Report that the YYRULE is going to be reduced.  */

void
yy::Parser::reduce_print_ (int yyrule)
{
  unsigned int yylno = rline_[yyrule];
  /* Print the symbols being reduced, and their result.  */
  cdebug_ << "Reducing via rule " << n_ - 1 << " (line " << yylno << "), ";
  for (unsigned char i = prhs_[n_];
       0 <= rhs_[i]; ++i)
    cdebug_ << name_[rhs_[i]] << ' ';
  cdebug_ << "-> " << name_[r1_[n_]] << std::endl;
}
#endif // YYDEBUG

/* YYTRANSLATE(YYLEX) -- Bison symbol number corresponding to YYLEX.  */
yy::Parser::TokenNumberType
yy::Parser::translate_ (int token)
{
  static
  const TokenNumberType
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
  if ((unsigned) token <= user_token_number_max_)
    return translate_table[token];
  else
    return undef_token_;
}

const int yy::Parser::eof_ = 0;
const int yy::Parser::last_ = 27;
const int yy::Parser::nnts_ = 10;
const int yy::Parser::empty_ = -2;
const int yy::Parser::final_ = 12;
const int yy::Parser::terror_ = 1;
const int yy::Parser::errcode_ = 256;
const int yy::Parser::ntokens_ = 9;

const unsigned yy::Parser::user_token_number_max_ = 261;
const yy::Parser::TokenNumberType yy::Parser::undef_token_ = 2;

#line 161 "tgbaparse.yy"


void
yy::Parser::print_()
{
  if (looka_ == STRING || looka_ == IDENT)
    YYCDEBUG << " '" << *value.str << "'";
}

void
yy::Parser::error_()
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
	     bool debug)
  {
    if (tgbayyopen(name))
      {
	error_list.push_back
	  (tgba_parse_error(yy::Location(),
			    std::string("Cannot open file ") + name));
	return 0;
      }
    tgba_explicit* result = new tgba_explicit(dict);
    tgbayy::Parser parser(debug, yy::Location(), error_list, env, result);
    parser.parse();
    tgbayyclose();
    return result;
  }
}

// Local Variables:
// mode: c++
// End:

