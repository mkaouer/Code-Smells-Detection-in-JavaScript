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

#include "/home/adl/proj/spot/src/ltlparse/ltlparse.hh"

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
        case 2:
#line 84 "ltlparse.yy"
    { result = yyval.ltl = semantic_stack_[1].ltl;
		YYACCEPT;
	      ;}
    break;

  case 3:
#line 88 "ltlparse.yy"
    { error_list.push_back(parse_error(location_stack_[1],
				      "couldn't parse anything sensible"));
		result = yyval.ltl = 0;
		YYABORT;
	      ;}
    break;

  case 4:
#line 94 "ltlparse.yy"
    { error_list.push_back(parse_error(location_stack_[0], "empty input"));
		result = yyval.ltl = 0;
		YYABORT;
	      ;}
    break;

  case 5:
#line 100 "ltlparse.yy"
    { error_list.push_back(parse_error(location_stack_[0],
				     "unexpected input ignored")); ;}
    break;

  case 6:
#line 104 "ltlparse.yy"
    { yyval.ltl = semantic_stack_[0].ltl; ;}
    break;

  case 7:
#line 106 "ltlparse.yy"
    { yyval.ltl = semantic_stack_[0].ltl; ;}
    break;

  case 8:
#line 108 "ltlparse.yy"
    { yyval.ltl = semantic_stack_[1].ltl; ;}
    break;

  case 11:
#line 117 "ltlparse.yy"
    {
		yyval.ltl = parse_environment.require(*semantic_stack_[0].str);
		if (! yyval.ltl)
		  {
		    std::string s = "unknown atomic proposition `";
		    s += *semantic_stack_[0].str;
		    s += "' in environment `";
		    s += parse_environment.name();
		    s += "'";
		    error_list.push_back(parse_error(location_stack_[0], s));
		    delete semantic_stack_[0].str;
		    YYERROR;
		  }
		else
		  delete semantic_stack_[0].str;
	      ;}
    break;

  case 12:
#line 134 "ltlparse.yy"
    { yyval.ltl = constant::true_instance(); ;}
    break;

  case 13:
#line 136 "ltlparse.yy"
    { yyval.ltl = constant::false_instance(); ;}
    break;

  case 14:
#line 138 "ltlparse.yy"
    { yyval.ltl = semantic_stack_[1].ltl; ;}
    break;

  case 15:
#line 140 "ltlparse.yy"
    { error_list.push_back(parse_error(yyloc,
		 "treating this parenthetical block as false"));
		yyval.ltl = constant::false_instance();
	      ;}
    break;

  case 16:
#line 145 "ltlparse.yy"
    { yyval.ltl = semantic_stack_[2].ltl; ;}
    break;

  case 17:
#line 147 "ltlparse.yy"
    { error_list.push_back(parse_error(location_stack_[3] + location_stack_[2],
				      "missing closing parenthesis"));
		yyval.ltl = semantic_stack_[2].ltl;
	      ;}
    break;

  case 18:
#line 152 "ltlparse.yy"
    { error_list.push_back(parse_error(yyloc,
                    "missing closing parenthesis, "
		    "treating this parenthetical block as false"));
		yyval.ltl = constant::false_instance();
	      ;}
    break;

  case 19:
#line 158 "ltlparse.yy"
    { yyval.ltl = unop::instance(unop::Not, semantic_stack_[0].ltl); ;}
    break;

  case 20:
#line 160 "ltlparse.yy"
    { yyval.ltl = multop::instance(multop::And, semantic_stack_[2].ltl, semantic_stack_[0].ltl); ;}
    break;

  case 21:
#line 162 "ltlparse.yy"
    {
		destroy(semantic_stack_[2].ltl);
		error_list.push_back(parse_error(location_stack_[1],
				     "missing right operand for OP_AND"));
		yyval.ltl = constant::false_instance();
	      ;}
    break;

  case 22:
#line 169 "ltlparse.yy"
    { yyval.ltl = multop::instance(multop::Or, semantic_stack_[2].ltl, semantic_stack_[0].ltl); ;}
    break;

  case 23:
#line 171 "ltlparse.yy"
    {
		destroy(semantic_stack_[2].ltl);
		error_list.push_back(parse_error(location_stack_[1],
				     "missing right operand for OP_OR"));
		yyval.ltl = constant::false_instance();
	      ;}
    break;

  case 24:
#line 178 "ltlparse.yy"
    { yyval.ltl = binop::instance(binop::Xor, semantic_stack_[2].ltl, semantic_stack_[0].ltl); ;}
    break;

  case 25:
#line 180 "ltlparse.yy"
    {
		destroy(semantic_stack_[2].ltl);
		error_list.push_back(parse_error(location_stack_[1],
				     "missing right operand for OP_XOR"));
		yyval.ltl = constant::false_instance();
	      ;}
    break;

  case 26:
#line 187 "ltlparse.yy"
    { yyval.ltl = binop::instance(binop::Implies, semantic_stack_[2].ltl, semantic_stack_[0].ltl); ;}
    break;

  case 27:
#line 189 "ltlparse.yy"
    {
		destroy(semantic_stack_[2].ltl);
		error_list.push_back(parse_error(location_stack_[1],
				     "missing right operand for OP_IMPLIES"));
		yyval.ltl = constant::false_instance();
	      ;}
    break;

  case 28:
#line 196 "ltlparse.yy"
    { yyval.ltl = binop::instance(binop::Equiv, semantic_stack_[2].ltl, semantic_stack_[0].ltl); ;}
    break;

  case 29:
#line 198 "ltlparse.yy"
    {
		destroy(semantic_stack_[2].ltl);
		error_list.push_back(parse_error(location_stack_[1],
				     "missing right operand for OP_EQUIV"));
		yyval.ltl = constant::false_instance();
	      ;}
    break;

  case 30:
#line 205 "ltlparse.yy"
    { yyval.ltl = binop::instance(binop::U, semantic_stack_[2].ltl, semantic_stack_[0].ltl); ;}
    break;

  case 31:
#line 207 "ltlparse.yy"
    {
		destroy(semantic_stack_[2].ltl);
		error_list.push_back(parse_error(location_stack_[1],
				     "missing right operand for OP_U"));
		yyval.ltl = constant::false_instance();
	      ;}
    break;

  case 32:
#line 214 "ltlparse.yy"
    { yyval.ltl = binop::instance(binop::R, semantic_stack_[2].ltl, semantic_stack_[0].ltl); ;}
    break;

  case 33:
#line 216 "ltlparse.yy"
    {
		destroy(semantic_stack_[2].ltl);
		error_list.push_back(parse_error(location_stack_[1],
				     "missing right operand for OP_R"));
		yyval.ltl = constant::false_instance();
	      ;}
    break;

  case 34:
#line 223 "ltlparse.yy"
    { yyval.ltl = unop::instance(unop::F, semantic_stack_[0].ltl); ;}
    break;

  case 35:
#line 225 "ltlparse.yy"
    { yyval.ltl = unop::instance(unop::G, semantic_stack_[0].ltl); ;}
    break;

  case 36:
#line 227 "ltlparse.yy"
    { yyval.ltl = unop::instance(unop::X, semantic_stack_[0].ltl); ;}
    break;


    }

/* Line 500 of lalr1.cc.  */
#line 436 "/home/adl/proj/spot/src/ltlparse/ltlparse.cc"

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
const signed char yy::Parser::pact_ninf_ = -9;
const short
yy::Parser::pact_[] =
{
       4,    -9,    35,    35,    35,    35,    49,    -9,    -9,    -9,
      -9,    12,    35,     5,    25,   183,    -9,    -9,    -9,    -9,
      10,    -6,    39,   170,    -9,   183,    -9,    -9,    -9,    -9,
      63,    77,    91,   105,   119,   133,   147,    -9,    -9,    -9,
      -8,    -9,   189,    -9,   161,    -9,    62,    -9,     0,    -9,
       0,    -9,    -9,    -9,    -9,    -9,    -9
};

/* YYDEFACT[S] -- default rule to reduce with in state S when YYTABLE
   doesn't specify something else to do.  Zero means the default is an
   error.  */
const unsigned char
yy::Parser::defact_[] =
{
       0,     9,     0,     0,     0,     0,     0,    11,    12,    13,
       4,     0,     0,     0,     0,     6,    35,    34,    36,    19,
       9,     0,     0,     0,     1,     7,     2,     8,    10,     3,
       0,     0,     0,     0,     0,     0,     0,    15,    18,    14,
       0,    23,    22,    25,    24,    21,    20,    29,    28,    27,
      26,    33,    32,    31,    30,    16,    17
};

/* YYPGOTO[NTERM-NUM].  */
const short
yy::Parser::pgoto_[] =
{
      -9,    -9,   149,    -9,    19,    -2
};

/* YYDEFGOTO[NTERM-NUM].  */
const signed char
yy::Parser::defgoto_[] =
{
      -1,    11,    12,    13,    22,    15
};

/* YYTABLE[YYPACT[STATE-NUM]].  What to do in state STATE-NUM.  If
   positive, shift that token.  If negative, reduce the rule which
   number is the opposite.  If zero, do what YYDEFACT says.  */
const signed char yy::Parser::table_ninf_ = -6;
const signed char
yy::Parser::table_[] =
{
      16,    17,    18,    19,    23,     1,     1,    55,    35,    36,
      25,    56,    24,    38,     2,     3,     4,     5,     6,    14,
       7,     8,     9,    10,    26,    37,    28,     0,    42,    44,
      46,    48,    50,    52,    54,    -5,    -5,    -5,    -5,    -5,
      28,    -5,    -5,    -5,    29,     2,     3,     4,     5,     6,
      20,     7,     8,     9,    -5,     0,     0,     0,    -5,     2,
       3,     4,     5,     6,    41,     7,     8,     9,    33,    34,
      35,    36,     0,     2,     3,     4,     5,     6,    43,     7,
       8,     9,     0,     0,     0,     0,     0,     2,     3,     4,
       5,     6,    45,     7,     8,     9,     0,     0,     0,     0,
       0,     2,     3,     4,     5,     6,    47,     7,     8,     9,
       0,     0,     0,     0,     0,     2,     3,     4,     5,     6,
      49,     7,     8,     9,     0,     0,     0,     0,     0,     2,
       3,     4,     5,     6,    51,     7,     8,     9,     0,     0,
       0,     0,     0,     2,     3,     4,     5,     6,    53,     7,
       8,     9,     0,     0,     0,    21,     0,     2,     3,     4,
       5,     6,    27,     7,     8,     9,    32,    33,    34,    35,
      36,     1,    40,    30,    31,    32,    33,    34,    35,    36,
       0,     0,     0,     0,     0,    39,    30,    31,    32,    33,
      34,    35,    36,    31,    32,    33,    34,    35,    36
};

/* YYCHECK.  */
const signed char
yy::Parser::check_[] =
{
       2,     3,     4,     5,     6,     1,     1,    15,     8,     9,
      12,    19,     0,    19,    10,    11,    12,    13,    14,     0,
      16,    17,    18,    19,    19,    15,     1,    -1,    30,    31,
      32,    33,    34,    35,    36,    10,    11,    12,    13,    14,
       1,    16,    17,    18,    19,    10,    11,    12,    13,    14,
       1,    16,    17,    18,    15,    -1,    -1,    -1,    19,    10,
      11,    12,    13,    14,     1,    16,    17,    18,     6,     7,
       8,     9,    -1,    10,    11,    12,    13,    14,     1,    16,
      17,    18,    -1,    -1,    -1,    -1,    -1,    10,    11,    12,
      13,    14,     1,    16,    17,    18,    -1,    -1,    -1,    -1,
      -1,    10,    11,    12,    13,    14,     1,    16,    17,    18,
      -1,    -1,    -1,    -1,    -1,    10,    11,    12,    13,    14,
       1,    16,    17,    18,    -1,    -1,    -1,    -1,    -1,    10,
      11,    12,    13,    14,     1,    16,    17,    18,    -1,    -1,
      -1,    -1,    -1,    10,    11,    12,    13,    14,     1,    16,
      17,    18,    -1,    -1,    -1,     6,    -1,    10,    11,    12,
      13,    14,    13,    16,    17,    18,     5,     6,     7,     8,
       9,     1,    23,     3,     4,     5,     6,     7,     8,     9,
      -1,    -1,    -1,    -1,    -1,    15,     3,     4,     5,     6,
       7,     8,     9,     4,     5,     6,     7,     8,     9
};

#if YYDEBUG
/* STOS_[STATE-NUM] -- The (internal number of the) accessing
   symbol of state STATE-NUM.  */
const unsigned char
yy::Parser::stos_[] =
{
       0,     1,    10,    11,    12,    13,    14,    16,    17,    18,
      19,    21,    22,    23,    24,    25,    25,    25,    25,    25,
       1,    22,    24,    25,     0,    25,    19,    22,     1,    19,
       3,     4,     5,     6,     7,     8,     9,    15,    19,    15,
      22,     1,    25,     1,    25,     1,    25,     1,    25,     1,
      25,     1,    25,     1,    25,    15,    19
};

/* TOKEN_NUMBER_[YYLEX-NUM] -- Internal token number corresponding
   to YYLEX-NUM.  */
const unsigned short
yy::Parser::token_number_[] =
{
       0,   256,   257,   258,   259,   260,   261,   262,   263,   264,
     265,   266,   267,   268,   269,   270,   271,   272,   273,   274
};
#endif

/* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
const unsigned char
yy::Parser::r1_[] =
{
       0,    20,    21,    21,    21,    22,    23,    23,    23,    24,
      24,    25,    25,    25,    25,    25,    25,    25,    25,    25,
      25,    25,    25,    25,    25,    25,    25,    25,    25,    25,
      25,    25,    25,    25,    25,    25,    25
};

/* YYR2[YYN] -- Number of symbols composing right hand side of rule YYN.  */
const unsigned char
yy::Parser::r2_[] =
{
       0,     2,     2,     2,     1,     1,     1,     2,     2,     1,
       2,     1,     1,     1,     3,     3,     4,     4,     3,     2,
       3,     3,     3,     3,     3,     3,     3,     3,     3,     3,
       3,     3,     3,     3,     2,     2,     2
};

#if YYDEBUG || YYERROR_VERBOSE
/* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
   First, the terminals, then, starting at YYNTOKENS, nonterminals. */
const char*
const yy::Parser::name_[] =
{
  "$end", "error", "$undefined", "OP_OR", "OP_XOR", "OP_AND", "OP_EQUIV",
  "OP_IMPLIES", "OP_R", "OP_U", "OP_G", "OP_F", "OP_X", "OP_NOT",
  "PAR_OPEN", "PAR_CLOSE", "ATOMIC_PROP", "CONST_TRUE", "CONST_FALSE",
  "END_OF_INPUT", "$accept", "result", "many_errors_diagnosed",
  "ltl_formula", "many_errors", "subformula", 0
};
#endif

#if YYDEBUG
/* YYRHS -- A `-1'-separated list of the rules' RHS. */
const yy::Parser::RhsNumberType
yy::Parser::rhs_[] =
{
      21,     0,    -1,    23,    19,    -1,    24,    19,    -1,    19,
      -1,    24,    -1,    25,    -1,    22,    25,    -1,    23,    22,
      -1,     1,    -1,    24,     1,    -1,    16,    -1,    17,    -1,
      18,    -1,    14,    25,    15,    -1,    14,     1,    15,    -1,
      14,    25,    22,    15,    -1,    14,    25,    22,    19,    -1,
      14,    22,    19,    -1,    13,    25,    -1,    25,     5,    25,
      -1,    25,     5,     1,    -1,    25,     3,    25,    -1,    25,
       3,     1,    -1,    25,     4,    25,    -1,    25,     4,     1,
      -1,    25,     7,    25,    -1,    25,     7,     1,    -1,    25,
       6,    25,    -1,    25,     6,     1,    -1,    25,     9,    25,
      -1,    25,     9,     1,    -1,    25,     8,    25,    -1,    25,
       8,     1,    -1,    11,    25,    -1,    10,    25,    -1,    12,
      25,    -1
};

/* YYPRHS[YYN] -- Index of the first RHS symbol of rule number YYN in
   YYRHS.  */
const unsigned char
yy::Parser::prhs_[] =
{
       0,     0,     3,     6,     9,    11,    13,    15,    18,    21,
      23,    26,    28,    30,    32,    36,    40,    45,    50,    54,
      57,    61,    65,    69,    73,    77,    81,    85,    89,    93,
      97,   101,   105,   109,   113,   116,   119
};

/* YYRLINE[YYN] -- source line where rule number YYN was defined.  */
const unsigned char
yy::Parser::rline_[] =
{
       0,    83,    83,    87,    93,    99,   103,   105,   107,   110,
     111,   116,   133,   135,   137,   139,   144,   146,   151,   157,
     159,   161,   168,   170,   177,   179,   186,   188,   195,   197,
     204,   206,   213,   215,   222,   224,   226
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
      15,    16,    17,    18,    19
  };
  if ((unsigned) token <= user_token_number_max_)
    return translate_table[token];
  else
    return undef_token_;
}

const int yy::Parser::eof_ = 0;
const int yy::Parser::last_ = 198;
const int yy::Parser::nnts_ = 6;
const int yy::Parser::empty_ = -2;
const int yy::Parser::final_ = 24;
const int yy::Parser::terror_ = 1;
const int yy::Parser::errcode_ = 256;
const int yy::Parser::ntokens_ = 20;

const unsigned yy::Parser::user_token_number_max_ = 274;
const yy::Parser::TokenNumberType yy::Parser::undef_token_ = 2;

#line 236 "ltlparse.yy"


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

