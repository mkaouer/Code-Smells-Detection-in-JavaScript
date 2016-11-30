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
     CFG_ALGORITHM = 258,
     CFG_ENABLED = 259,
     CFG_NAME = 260,
     CFG_PARAMETERS = 261,
     CFG_PROGRAMPATH = 262,
     CFG_GLOBALOPTIONS = 263,
     CFG_COMPARISONTEST = 264,
     CFG_CONSISTENCYTEST = 265,
     CFG_INTERACTIVE = 266,
     CFG_INTERSECTIONTEST = 267,
     CFG_MODELCHECK = 268,
     CFG_ROUNDS = 269,
     CFG_TRANSLATORTIMEOUT = 270,
     CFG_VERBOSITY = 271,
     CFG_STATESPACEOPTIONS = 272,
     CFG_EDGEPROBABILITY = 273,
     CFG_PROPOSITIONS = 274,
     CFG_SIZE = 275,
     CFG_TRUTHPROBABILITY = 276,
     CFG_CHANGEINTERVAL = 277,
     CFG_RANDOMSEED = 278,
     CFG_FORMULAOPTIONS = 279,
     CFG_ABBREVIATEDOPERATORS = 280,
     CFG_ANDPRIORITY = 281,
     CFG_BEFOREPRIORITY = 282,
     CFG_DEFAULTOPERATORPRIORITY = 283,
     CFG_EQUIVALENCEPRIORITY = 284,
     CFG_FALSEPRIORITY = 285,
     CFG_FINALLYPRIORITY = 286,
     CFG_GENERATEMODE = 287,
     CFG_GLOBALLYPRIORITY = 288,
     CFG_IMPLICATIONPRIORITY = 289,
     CFG_NEXTPRIORITY = 290,
     CFG_NOTPRIORITY = 291,
     CFG_ORPRIORITY = 292,
     CFG_OUTPUTMODE = 293,
     CFG_PROPOSITIONPRIORITY = 294,
     CFG_RELEASEPRIORITY = 295,
     CFG_STRONGRELEASEPRIORITY = 296,
     CFG_TRUEPRIORITY = 297,
     CFG_UNTILPRIORITY = 298,
     CFG_WEAKUNTILPRIORITY = 299,
     CFG_XORPRIORITY = 300,
     CFG_LBRACE = 301,
     CFG_RBRACE = 302,
     CFG_EQUALS = 303,
     CFG_BLOCK_ID = 304,
     CFG_OPTION_ID = 305,
     CFG_VALUE = 306,
     CFG_UNKNOWN = 307
   };
#endif
#define CFG_ALGORITHM 258
#define CFG_ENABLED 259
#define CFG_NAME 260
#define CFG_PARAMETERS 261
#define CFG_PROGRAMPATH 262
#define CFG_GLOBALOPTIONS 263
#define CFG_COMPARISONTEST 264
#define CFG_CONSISTENCYTEST 265
#define CFG_INTERACTIVE 266
#define CFG_INTERSECTIONTEST 267
#define CFG_MODELCHECK 268
#define CFG_ROUNDS 269
#define CFG_TRANSLATORTIMEOUT 270
#define CFG_VERBOSITY 271
#define CFG_STATESPACEOPTIONS 272
#define CFG_EDGEPROBABILITY 273
#define CFG_PROPOSITIONS 274
#define CFG_SIZE 275
#define CFG_TRUTHPROBABILITY 276
#define CFG_CHANGEINTERVAL 277
#define CFG_RANDOMSEED 278
#define CFG_FORMULAOPTIONS 279
#define CFG_ABBREVIATEDOPERATORS 280
#define CFG_ANDPRIORITY 281
#define CFG_BEFOREPRIORITY 282
#define CFG_DEFAULTOPERATORPRIORITY 283
#define CFG_EQUIVALENCEPRIORITY 284
#define CFG_FALSEPRIORITY 285
#define CFG_FINALLYPRIORITY 286
#define CFG_GENERATEMODE 287
#define CFG_GLOBALLYPRIORITY 288
#define CFG_IMPLICATIONPRIORITY 289
#define CFG_NEXTPRIORITY 290
#define CFG_NOTPRIORITY 291
#define CFG_ORPRIORITY 292
#define CFG_OUTPUTMODE 293
#define CFG_PROPOSITIONPRIORITY 294
#define CFG_RELEASEPRIORITY 295
#define CFG_STRONGRELEASEPRIORITY 296
#define CFG_TRUEPRIORITY 297
#define CFG_UNTILPRIORITY 298
#define CFG_WEAKUNTILPRIORITY 299
#define CFG_XORPRIORITY 300
#define CFG_LBRACE 301
#define CFG_RBRACE 302
#define CFG_EQUALS 303
#define CFG_BLOCK_ID 304
#define CFG_OPTION_ID 305
#define CFG_VALUE 306
#define CFG_UNKNOWN 307




#if ! defined (YYSTYPE) && ! defined (YYSTYPE_IS_DECLARED)
#line 163 "Config-parse.yy"
typedef union YYSTYPE {
  const char* value;
} YYSTYPE;
/* Line 1318 of yacc.c.  */
#line 145 "Config-parse.h"
# define yystype YYSTYPE /* obsolescent; will be withdrawn */
# define YYSTYPE_IS_DECLARED 1
# define YYSTYPE_IS_TRIVIAL 1
#endif

extern YYSTYPE yylval;



