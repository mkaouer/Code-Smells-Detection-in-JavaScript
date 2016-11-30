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
     STRING = 258,
     UNTERMINATED_STRING = 259,
     IDENT = 260,
     ACC_DEF = 261
   };
#endif
#define STRING 258
#define UNTERMINATED_STRING 259
#define IDENT 260
#define ACC_DEF 261




/* Copy the first part of user declarations.  */
#line 22 "tgbaparse.yy"

#include <string>
#include "public.hh"


/* Line 317 of lalr1.cc.  */
#line 63 "/home/adl/proj/spot/src/tgbaparse/tgbaparse.hh"

/* Enabling traces.  */
#ifndef YYDEBUG
# define YYDEBUG 1
#endif

/* Enabling verbose error message.  */
#ifndef YYERROR_VERBOSE
# define YYERROR_VERBOSE 1
#endif

#ifndef YYSTYPE
#line 33 "tgbaparse.yy"
typedef union {
  int token;
  std::string* str;
  spot::ltl::formula* f;
  std::list<spot::ltl::formula*>* list;
} yystype;
/* Line 317 of lalr1.cc.  */
#line 84 "/home/adl/proj/spot/src/tgbaparse/tgbaparse.hh"
# define YYSTYPE yystype
#endif

/* Copy the second part of user declarations.  */
#line 40 "tgbaparse.yy"

#include "ltlast/constant.hh"
#include "ltlvisit/destroy.hh"
#include "ltlparse/public.hh"

/* tgbaparse.hh and parsedecl.hh include each other recursively.
   We mut ensure that YYSTYPE is declared (by the above %union)
   before parsedecl.hh uses it. */
#include "parsedecl.hh"
using namespace spot::ltl;

/* Ugly hack so that Bison use tgbayylex, not yylex.
   (%name-prefix doesn't work for the lalr1.cc skeleton
   at the time of writing.)  */
#define yylex tgbayylex

typedef std::pair<bool, spot::ltl::formula*> pair;


/* Line 317 of lalr1.cc.  */
#line 110 "/home/adl/proj/spot/src/tgbaparse/tgbaparse.hh"
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
	    LocationType initlocation, spot::tgba_parse_error_list &error_list, spot::ltl::environment &parse_environment, spot::tgba_explicit* &result) :
      debug_ (debug),
      cdebug_ (std::cerr),
      initlocation_ (initlocation),
      error_list(error_list),
      parse_environment(parse_environment),
      result(result)
#else
    Parser (bool debug, spot::tgba_parse_error_list &error_list, spot::ltl::environment &parse_environment, spot::tgba_explicit* &result) :
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
    static const signed char pact_[];
    static const signed char pact_ninf_;
    static const unsigned char defact_[];
    static const signed char pgoto_[];
    static const signed char defgoto_[];
    static const unsigned char table_[];
    static const signed char table_ninf_;
    static const unsigned char check_[];
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
    spot::tgba_parse_error_list &error_list;
    spot::ltl::environment &parse_environment;
    spot::tgba_explicit* &result;
  };
}

#endif /* ! defined PARSER_HEADER_H */
