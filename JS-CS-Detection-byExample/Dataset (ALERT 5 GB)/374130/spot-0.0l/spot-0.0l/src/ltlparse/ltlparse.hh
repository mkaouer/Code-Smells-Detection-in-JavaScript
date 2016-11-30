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
/* FIXME: This is wrong, we want computed header guards.
   I don't know why the macros are missing now. :( */
#ifndef PARSER_HEADER_H
# define PARSER_HEADER_H

#include "stack.hh"
#include "location.hh"

#include <string>
#include <iostream>

/* Using locations.  */
#define YYLSP_NEEDED 1

/* Tokens.  */
#ifndef YYTOKENTYPE
# define YYTOKENTYPE
   /* Put the tokens into the symbol table, so that GDB and other debuggers
      know about them.  */
   enum yytokentype {
     OP_OR = 258,
     OP_XOR = 259,
     OP_AND = 260,
     OP_EQUIV = 261,
     OP_IMPLIES = 262,
     OP_R = 263,
     OP_U = 264,
     OP_G = 265,
     OP_F = 266,
     OP_X = 267,
     OP_NOT = 268,
     PAR_OPEN = 269,
     PAR_CLOSE = 270,
     ATOMIC_PROP = 271,
     CONST_TRUE = 272,
     CONST_FALSE = 273,
     END_OF_INPUT = 274
   };
#endif
#define OP_OR 258
#define OP_XOR 259
#define OP_AND 260
#define OP_EQUIV 261
#define OP_IMPLIES 262
#define OP_R 263
#define OP_U 264
#define OP_G 265
#define OP_F 266
#define OP_X 267
#define OP_NOT 268
#define PAR_OPEN 269
#define PAR_CLOSE 270
#define ATOMIC_PROP 271
#define CONST_TRUE 272
#define CONST_FALSE 273
#define END_OF_INPUT 274




/* Copy the first part of user declarations.  */
#line 22 "ltlparse.yy"

#include <string>
#include "public.hh"
#include "ltlast/allnodes.hh"
#include "ltlvisit/destroy.hh"



/* Line 317 of lalr1.cc.  */
#line 92 "/home/adl/proj/spot/src/ltlparse/ltlparse.hh"

/* Enabling traces.  */
#ifndef YYDEBUG
# define YYDEBUG 1
#endif

/* Enabling verbose error message.  */
#ifndef YYERROR_VERBOSE
# define YYERROR_VERBOSE 1
#endif

#ifndef YYSTYPE
#line 36 "ltlparse.yy"
typedef union {
  int token;
  std::string* str;
  spot::ltl::formula* ltl;
} yystype;
/* Line 317 of lalr1.cc.  */
#line 112 "/home/adl/proj/spot/src/ltlparse/ltlparse.hh"
# define YYSTYPE yystype
#endif

/* Copy the second part of user declarations.  */
#line 42 "ltlparse.yy"

/* ltlparse.hh and parsedecl.hh include each other recursively.
   We mut ensure that YYSTYPE is declared (by the above %union)
   before parsedecl.hh uses it. */
#include "parsedecl.hh"
using namespace spot::ltl;

/* Ugly hack so that Bison uses ltlyylex, not yylex.
   (%name-prefix doesn't work for the lalr1.cc skeleton
   at the time of writing.)  */
#define yylex ltlyylex


/* Line 317 of lalr1.cc.  */
#line 132 "/home/adl/proj/spot/src/ltlparse/ltlparse.hh"
#ifndef YYLLOC_DEFAULT
# define YYLLOC_DEFAULT(Current, Rhs, N) \
   ((Current).end = Rhs[N].end)
#endif

namespace yy
{
  class Parser;

  template < typename P >
  struct Traits
  {
  };

  template < >
  struct Traits< Parser >
  {
    typedef unsigned char TokenNumberType;
    typedef signed char       RhsNumberType;
    typedef int      StateType;
    typedef yystype  SemanticType;
    typedef Location LocationType;
  };
}

namespace yy
{
  class Parser 
  {
  public:

    typedef Traits< Parser >::TokenNumberType TokenNumberType;
    typedef Traits< Parser >::RhsNumberType   RhsNumberType;
    typedef Traits< Parser >::StateType       StateType;
    typedef Traits< Parser >::SemanticType    SemanticType;
    typedef Traits< Parser >::LocationType    LocationType;

    typedef Stack< StateType >    StateStack;
    typedef Stack< SemanticType > SemanticStack;
    typedef Stack< LocationType > LocationStack;

#if YYLSP_NEEDED
    Parser (bool debug,
	    LocationType initlocation, spot::ltl::parse_error_list &error_list, spot::ltl::environment &parse_environment, spot::ltl::formula* &result) :
      debug_ (debug),
      cdebug_ (std::cerr),
      initlocation_ (initlocation),
      error_list(error_list),
      parse_environment(parse_environment),
      result(result)
#else
    Parser (bool debug, spot::ltl::parse_error_list &error_list, spot::ltl::environment &parse_environment, spot::ltl::formula* &result) :
      debug_ (debug),
      cdebug_ (std::cerr),
      error_list(error_list),
      parse_environment(parse_environment),
      result(result)
#endif
    {
    }

    virtual ~Parser ()
    {
    }

    virtual int parse ();

  private:

    virtual void lex_ ();
    virtual void error_ ();
    virtual void print_ ();

    /* Stacks.  */
    StateStack    state_stack_;
    SemanticStack semantic_stack_;
    LocationStack location_stack_;

    /* Tables.  */
    static const short pact_[];
    static const signed char pact_ninf_;
    static const unsigned char defact_[];
    static const short pgoto_[];
    static const signed char defgoto_[];
    static const signed char table_[];
    static const signed char table_ninf_;
    static const signed char check_[];
    static const unsigned char r1_[];
    static const unsigned char r2_[];

#if YYDEBUG || YYERROR_VERBOSE
    static const char* const name_[];
#endif

    /* More tables, for debugging.  */
#if YYDEBUG
    static const RhsNumberType rhs_[];
    static const unsigned char prhs_[];
    static const unsigned char rline_[];
    static const unsigned char stos_[];
    static const unsigned short token_number_[];
    virtual void reduce_print_ (int yyrule);
    virtual void stack_print_ ();
#endif

    /* Even more tables.  */
    static inline TokenNumberType translate_ (int token);

    /* Constants.  */
    static const int eof_;
    /* LAST_ -- Last index in TABLE_.  */
    static const int last_;
    static const int nnts_;
    static const int empty_;
    static const int final_;
    static const int terror_;
    static const int errcode_;
    static const int ntokens_;
    static const unsigned user_token_number_max_;
    static const TokenNumberType undef_token_;

    /* State.  */
    int n_;
    int len_;
    int state_;

    /* Debugging.  */
    int debug_;
    std::ostream &cdebug_;

    /* Lookahead and lookahead in internal form.  */
    int looka_;
    int ilooka_;

    /* Message.  */
    std::string message;

    /* Semantic value and location of lookahead token.  */
    SemanticType value;
    LocationType location;

    /* @$ and $$.  */
    SemanticType yyval;
    LocationType yyloc;

    /* Initial location.  */
    LocationType initlocation_;

    /* User arguments.  */
    spot::ltl::parse_error_list &error_list;
    spot::ltl::environment &parse_environment;
    spot::ltl::formula* &result;
  };
}

#endif /* ! defined PARSER_HEADER_H */
