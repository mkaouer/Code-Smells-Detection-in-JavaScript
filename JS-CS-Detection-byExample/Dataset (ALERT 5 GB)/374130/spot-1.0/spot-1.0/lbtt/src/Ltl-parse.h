/* A Bison parser, made by GNU Bison 2.5.  */

/* Bison interface for Yacc-like parsers in C
   
      Copyright (C) 1984, 1989-1990, 2000-2011 Free Software Foundation, Inc.
   
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
/* Tokens.  */
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




#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
typedef union YYSTYPE
{

/* Line 2068 of yacc.c  */
#line 103 "Ltl-parse.y"

  class LtlFormula* formula;



/* Line 2068 of yacc.c  */
#line 102 "Ltl-parse.h"
} YYSTYPE;
# define YYSTYPE_IS_TRIVIAL 1
# define yystype YYSTYPE /* obsolescent; will be withdrawn */
# define YYSTYPE_IS_DECLARED 1
#endif

extern YYSTYPE ltl_lval;


