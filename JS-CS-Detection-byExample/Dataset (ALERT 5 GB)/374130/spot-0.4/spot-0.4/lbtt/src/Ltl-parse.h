/* A Bison parser, made by GNU Bison 2.0.  */

/* Skeleton parser for Yacc-like parsing with Bison,
   Copyright (C) 1984, 1989, 1990, 2000, 2001, 2002, 2003, 2004 Free Software Foundation, Inc.

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

/* As a special exception, when this file is copied by Bison into a
   Bison output file, you may use that output file without restriction.
   This special exception was added by the Free Software Foundation
   in version 1.24 of Bison.  */

/* Tokens.  */
#ifndef YYTOKENTYPE
# define YYTOKENTYPE
   /* Put the tokens into the symbol table, so that GDB and other debuggers
      know about them.  */
   enum yytokentype {
     LTLPARSE_LPAR = 258,
     LTLPARSE_RPAR = 259,
     LTLPARSE_FALSE = 260,
     LTLPARSE_TRUE = 261,
     LTLPARSE_UNKNOWN = 262,
     LTLPARSE_ATOM = 263,
     LTLPARSE_BEFORE = 264,
     LTLPARSE_STRONG_RELEASE = 265,
     LTLPARSE_WEAK_UNTIL = 266,
     LTLPARSE_RELEASE = 267,
     LTLPARSE_UNTIL = 268,
     LTLPARSE_XOR = 269,
     LTLPARSE_EQUIV = 270,
     LTLPARSE_IMPLY = 271,
     LTLPARSE_OR = 272,
     LTLPARSE_AND = 273,
     LTLPARSE_GLOBALLY = 274,
     LTLPARSE_FINALLY = 275,
     LTLPARSE_NEXT = 276,
     LTLPARSE_NOT = 277,
     LTLPARSE_EQUALS = 278
   };
#endif
#define LTLPARSE_LPAR 258
#define LTLPARSE_RPAR 259
#define LTLPARSE_FALSE 260
#define LTLPARSE_TRUE 261
#define LTLPARSE_UNKNOWN 262
#define LTLPARSE_ATOM 263
#define LTLPARSE_BEFORE 264
#define LTLPARSE_STRONG_RELEASE 265
#define LTLPARSE_WEAK_UNTIL 266
#define LTLPARSE_RELEASE 267
#define LTLPARSE_UNTIL 268
#define LTLPARSE_XOR 269
#define LTLPARSE_EQUIV 270
#define LTLPARSE_IMPLY 271
#define LTLPARSE_OR 272
#define LTLPARSE_AND 273
#define LTLPARSE_GLOBALLY 274
#define LTLPARSE_FINALLY 275
#define LTLPARSE_NEXT 276
#define LTLPARSE_NOT 277
#define LTLPARSE_EQUALS 278




#if ! defined (YYSTYPE) && ! defined (YYSTYPE_IS_DECLARED)
#line 103 "Ltl-parse.yy"
typedef union YYSTYPE {
  class LtlFormula* formula;
} YYSTYPE;
/* Line 1318 of yacc.c.  */
#line 87 "Ltl-parse.h"
# define yystype YYSTYPE /* obsolescent; will be withdrawn */
# define YYSTYPE_IS_DECLARED 1
# define YYSTYPE_IS_TRIVIAL 1
#endif

extern YYSTYPE ltl_lval;



