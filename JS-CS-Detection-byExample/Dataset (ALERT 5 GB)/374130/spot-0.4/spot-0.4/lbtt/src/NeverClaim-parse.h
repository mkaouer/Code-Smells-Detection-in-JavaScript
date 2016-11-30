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
     NC_NEVER = 258,
     NC_IF = 259,
     NC_FI = 260,
     NC_GOTO = 261,
     NC_SKIP = 262,
     NC_LABEL = 263,
     NC_COLON = 264,
     NC_SEMICOLON = 265,
     NC_DOUBLE_COLON = 266,
     NC_LBRACE = 267,
     NC_RBRACE = 268,
     NC_LPAREN = 269,
     NC_RPAREN = 270,
     NC_RIGHT_ARROW = 271,
     NC_PROPOSITION = 272,
     NC_TRUE = 273,
     NC_FALSE = 274,
     NC_OR = 275,
     NC_AND = 276,
     NC_NOT = 277
   };
#endif
#define NC_NEVER 258
#define NC_IF 259
#define NC_FI 260
#define NC_GOTO 261
#define NC_SKIP 262
#define NC_LABEL 263
#define NC_COLON 264
#define NC_SEMICOLON 265
#define NC_DOUBLE_COLON 266
#define NC_LBRACE 267
#define NC_RBRACE 268
#define NC_LPAREN 269
#define NC_RPAREN 270
#define NC_RIGHT_ARROW 271
#define NC_PROPOSITION 272
#define NC_TRUE 273
#define NC_FALSE 274
#define NC_OR 275
#define NC_AND 276
#define NC_NOT 277




#if ! defined (YYSTYPE) && ! defined (YYSTYPE_IS_DECLARED)
#line 132 "NeverClaim-parse.yy"
typedef union YYSTYPE {
  string* pf;
  string* str;
} YYSTYPE;
/* Line 1318 of yacc.c.  */
#line 86 "NeverClaim-parse.h"
# define yystype YYSTYPE /* obsolescent; will be withdrawn */
# define YYSTYPE_IS_DECLARED 1
# define YYSTYPE_IS_TRIVIAL 1
#endif

extern YYSTYPE yylval;



