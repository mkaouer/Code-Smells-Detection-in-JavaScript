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

/* Written by Richard Stallman by simplifying the original so called
   ``semantic'' parser.  */

/* All symbols defined below should begin with yy or YY, to avoid
   infringing on user name space.  This should be done even for local
   variables, as they might otherwise be expanded by user macros.
   There are some unavoidable exceptions within include files to
   define necessary library symbols; they are noted "INFRINGES ON
   USER NAME SPACE" below.  */

/* Identify Bison output.  */
#define YYBISON 1

/* Skeleton name.  */
#define YYSKELETON_NAME "yacc.c"

/* Pure parsers.  */
#define YYPURE 0

/* Using locations.  */
#define YYLSP_NEEDED 0

/* Substitute the variable and function names.  */
#define yyparse ltl_parse
#define yylex   ltl_lex
#define yyerror ltl_error
#define yylval  ltl_lval
#define yychar  ltl_char
#define yydebug ltl_debug
#define yynerrs ltl_nerrs


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




/* Copy the first part of user declarations.  */
#line 20 "Ltl-parse.yy"

#include <config.h>
#include <cctype>
#include <climits>
#include <cstring>
#include <istream>
#include <set>
#include "Exception.h"
#include "LbttAlloc.h"
#include "LtlFormula.h"

using namespace Ltl;

/******************************************************************************
 *
 * Variables and functions used for parsing an LTL formula.
 *
 *****************************************************************************/

static Exceptional_istream* estream;                /* Pointer to input stream.
                                                     */

static LtlFormula* result;                          /* This variable stores the
                                                     * result after a call to
                                                     * ltl_parse.
                                                     */

static std::set<LtlFormula*> intermediate_results;  /* Intermediate results.
                                                     * (This set is used
                                                     * for keeping track of
						     * the subformulas of a
						     * partially constructed
						     * formula in case the
						     * memory allocated for
						     * the subformulas needs
						     * to be freed because
						     * of a parse error.)
						     */

static int ltl_lex();                               /* The lexical scanner. */



/******************************************************************************
 *
 * Function for reporting parse errors.
 *
 *****************************************************************************/

static void ltl_error(const char*)
{
  throw LtlFormula::ParseErrorException("error parsing LTL formula");
}



/******************************************************************************
 *
 * Function for updating the set of intermediate results.
 *
 *****************************************************************************/

inline LtlFormula* newFormula(LtlFormula& f)
{
  intermediate_results.insert(&f);
  return &f;
}



/* Enabling traces.  */
#ifndef YYDEBUG
# define YYDEBUG 0
#endif

/* Enabling verbose error messages.  */
#ifdef YYERROR_VERBOSE
# undef YYERROR_VERBOSE
# define YYERROR_VERBOSE 1
#else
# define YYERROR_VERBOSE 0
#endif

#if ! defined (YYSTYPE) && ! defined (YYSTYPE_IS_DECLARED)
#line 103 "Ltl-parse.yy"
typedef union YYSTYPE {
  class LtlFormula* formula;
} YYSTYPE;
/* Line 190 of yacc.c.  */
#line 204 "Ltl-parse.cc"
# define yystype YYSTYPE /* obsolescent; will be withdrawn */
# define YYSTYPE_IS_DECLARED 1
# define YYSTYPE_IS_TRIVIAL 1
#endif



/* Copy the second part of user declarations.  */


/* Line 213 of yacc.c.  */
#line 216 "Ltl-parse.cc"

#if ! defined (yyoverflow) || YYERROR_VERBOSE

# ifndef YYFREE
#  define YYFREE free
# endif
# ifndef YYMALLOC
#  define YYMALLOC malloc
# endif

/* The parser invokes alloca or malloc; define the necessary symbols.  */

# ifdef YYSTACK_USE_ALLOCA
#  if YYSTACK_USE_ALLOCA
#   ifdef __GNUC__
#    define YYSTACK_ALLOC __builtin_alloca
#   else
#    define YYSTACK_ALLOC alloca
#   endif
#  endif
# endif

# ifdef YYSTACK_ALLOC
   /* Pacify GCC's `empty if-body' warning. */
#  define YYSTACK_FREE(Ptr) do { /* empty */; } while (0)
# else
#  if defined (__STDC__) || defined (__cplusplus)
#   include <stdlib.h> /* INFRINGES ON USER NAME SPACE */
#   define YYSIZE_T size_t
#  endif
#  define YYSTACK_ALLOC YYMALLOC
#  define YYSTACK_FREE YYFREE
# endif
#endif /* ! defined (yyoverflow) || YYERROR_VERBOSE */


#if (! defined (yyoverflow) \
     && (! defined (__cplusplus) \
	 || (defined (YYSTYPE_IS_TRIVIAL) && YYSTYPE_IS_TRIVIAL)))

/* A type that is properly aligned for any stack member.  */
union yyalloc
{
  short int yyss;
  YYSTYPE yyvs;
  };

/* The size of the maximum gap between one aligned stack and the next.  */
# define YYSTACK_GAP_MAXIMUM (sizeof (union yyalloc) - 1)

/* The size of an array large to enough to hold all stacks, each with
   N elements.  */
# define YYSTACK_BYTES(N) \
     ((N) * (sizeof (short int) + sizeof (YYSTYPE))			\
      + YYSTACK_GAP_MAXIMUM)

/* Copy COUNT objects from FROM to TO.  The source and destination do
   not overlap.  */
# ifndef YYCOPY
#  if defined (__GNUC__) && 1 < __GNUC__
#   define YYCOPY(To, From, Count) \
      __builtin_memcpy (To, From, (Count) * sizeof (*(From)))
#  else
#   define YYCOPY(To, From, Count)		\
      do					\
	{					\
	  register YYSIZE_T yyi;		\
	  for (yyi = 0; yyi < (Count); yyi++)	\
	    (To)[yyi] = (From)[yyi];		\
	}					\
      while (0)
#  endif
# endif

/* Relocate STACK from its old location to the new one.  The
   local variables YYSIZE and YYSTACKSIZE give the old and new number of
   elements in the stack, and YYPTR gives the new location of the
   stack.  Advance YYPTR to a properly aligned location for the next
   stack.  */
# define YYSTACK_RELOCATE(Stack)					\
    do									\
      {									\
	YYSIZE_T yynewbytes;						\
	YYCOPY (&yyptr->Stack, Stack, yysize);				\
	Stack = &yyptr->Stack;						\
	yynewbytes = yystacksize * sizeof (*Stack) + YYSTACK_GAP_MAXIMUM; \
	yyptr += yynewbytes / sizeof (*yyptr);				\
      }									\
    while (0)

#endif

#if defined (__STDC__) || defined (__cplusplus)
   typedef signed char yysigned_char;
#else
   typedef short int yysigned_char;
#endif

/* YYFINAL -- State number of the termination state. */
#define YYFINAL  46
/* YYLAST -- Last index in YYTABLE.  */
#define YYLAST   233

/* YYNTOKENS -- Number of terminals. */
#define YYNTOKENS  24
/* YYNNTS -- Number of nonterminals. */
#define YYNNTS  9
/* YYNRULES -- Number of rules. */
#define YYNRULES  41
/* YYNRULES -- Number of states. */
#define YYNSTATES  82

/* YYTRANSLATE(YYLEX) -- Bison symbol number corresponding to YYLEX.  */
#define YYUNDEFTOK  2
#define YYMAXUTOK   278

#define YYTRANSLATE(YYX) 						\
  ((unsigned int) (YYX) <= YYMAXUTOK ? yytranslate[YYX] : YYUNDEFTOK)

/* YYTRANSLATE[YYLEX] -- Bison symbol number corresponding to YYLEX.  */
static const unsigned char yytranslate[] =
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
      15,    16,    17,    18,    19,    20,    21,    22,    23
};

#if YYDEBUG
/* YYPRHS[YYN] -- Index of the first RHS symbol of rule number YYN in
   YYRHS.  */
static const unsigned char yyprhs[] =
{
       0,     0,     3,     5,     7,     9,    11,    15,    17,    21,
      25,    27,    29,    32,    35,    38,    41,    43,    45,    47,
      51,    53,    55,    59,    63,    67,    71,    75,    79,    83,
      87,    91,    95,    99,   103,   107,   111,   115,   119,   123,
     127,   131
};

/* YYRHS -- A `-1'-separated list of the rules' RHS. */
static const yysigned_char yyrhs[] =
{
      25,     0,    -1,    26,    -1,    27,    -1,    28,    -1,    30,
      -1,     3,    26,     4,    -1,     8,    -1,     8,    23,     5,
      -1,     8,    23,     6,    -1,     5,    -1,     6,    -1,    22,
      26,    -1,    21,    26,    -1,    20,    26,    -1,    19,    26,
      -1,    27,    -1,    28,    -1,    31,    -1,     3,    26,     4,
      -1,    31,    -1,    32,    -1,    18,    29,    29,    -1,    17,
      29,    29,    -1,    16,    29,    29,    -1,    15,    29,    29,
      -1,    14,    29,    29,    -1,    13,    29,    29,    -1,    12,
      29,    29,    -1,    11,    29,    29,    -1,    10,    29,    29,
      -1,     9,    29,    29,    -1,    26,    18,    26,    -1,    26,
      17,    26,    -1,    26,    16,    26,    -1,    26,    15,    26,
      -1,    26,    14,    26,    -1,    26,    13,    26,    -1,    26,
      12,    26,    -1,    26,    11,    26,    -1,    26,    10,    26,
      -1,    26,     9,    26,    -1
};

/* YYRLINE[YYN] -- source line where rule number YYN was defined.  */
static const unsigned short int yyrline[] =
{
       0,   141,   141,   145,   148,   151,   154,   158,   161,   167,
     170,   173,   177,   183,   189,   195,   202,   205,   208,   211,
     215,   217,   221,   228,   235,   242,   249,   256,   263,   270,
     277,   285,   293,   300,   307,   314,   321,   328,   335,   342,
     349,   356
};
#endif

#if YYDEBUG || YYERROR_VERBOSE
/* YYTNME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
   First, the terminals, then, starting at YYNTOKENS, nonterminals. */
static const char *const yytname[] =
{
  "$end", "error", "$undefined", "LTLPARSE_LPAR", "LTLPARSE_RPAR",
  "LTLPARSE_FALSE", "LTLPARSE_TRUE", "LTLPARSE_UNKNOWN", "LTLPARSE_ATOM",
  "LTLPARSE_BEFORE", "LTLPARSE_STRONG_RELEASE", "LTLPARSE_WEAK_UNTIL",
  "LTLPARSE_RELEASE", "LTLPARSE_UNTIL", "LTLPARSE_XOR", "LTLPARSE_EQUIV",
  "LTLPARSE_IMPLY", "LTLPARSE_OR", "LTLPARSE_AND", "LTLPARSE_GLOBALLY",
  "LTLPARSE_FINALLY", "LTLPARSE_NEXT", "LTLPARSE_NOT", "LTLPARSE_EQUALS",
  "$accept", "ltl_formula", "formula", "atomic_formula", "unary_formula",
  "prefix_op_formula", "binary_formula", "prefix_b_formula",
  "infix_b_formula", 0
};
#endif

# ifdef YYPRINT
/* YYTOKNUM[YYLEX-NUM] -- Internal token number corresponding to
   token YYLEX-NUM.  */
static const unsigned short int yytoknum[] =
{
       0,   256,   257,   258,   259,   260,   261,   262,   263,   264,
     265,   266,   267,   268,   269,   270,   271,   272,   273,   274,
     275,   276,   277,   278
};
# endif

/* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
static const unsigned char yyr1[] =
{
       0,    24,    25,    26,    26,    26,    26,    27,    27,    27,
      27,    27,    28,    28,    28,    28,    29,    29,    29,    29,
      30,    30,    31,    31,    31,    31,    31,    31,    31,    31,
      31,    31,    32,    32,    32,    32,    32,    32,    32,    32,
      32,    32
};

/* YYR2[YYN] -- Number of symbols composing right hand side of rule YYN.  */
static const unsigned char yyr2[] =
{
       0,     2,     1,     1,     1,     1,     3,     1,     3,     3,
       1,     1,     2,     2,     2,     2,     1,     1,     1,     3,
       1,     1,     3,     3,     3,     3,     3,     3,     3,     3,
       3,     3,     3,     3,     3,     3,     3,     3,     3,     3,
       3,     3
};

/* YYDEFACT[STATE-NAME] -- Default rule to reduce with in state
   STATE-NUM when YYTABLE doesn't specify something else to do.  Zero
   means the default is an error.  */
static const unsigned char yydefact[] =
{
       0,     0,    10,    11,     7,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       2,     3,     4,     5,    20,    21,     0,     0,     0,    16,
      17,     0,    18,     0,     0,     0,     0,     0,     0,     0,
       0,     0,    15,    14,    13,    12,     1,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     6,     8,     9,
       0,    31,    30,    29,    28,    27,    26,    25,    24,    23,
      22,    41,    40,    39,    38,    37,    36,    35,    34,    33,
      32,    19
};

/* YYDEFGOTO[NTERM-NUM]. */
static const yysigned_char yydefgoto[] =
{
      -1,    19,    20,    29,    30,    31,    23,    32,    25
};

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
#define YYPACT_NINF -11
static const short int yypact[] =
{
     156,   156,   -11,   -11,   -10,   176,   176,   176,   176,   176,
     176,   176,   176,   176,   176,   156,   156,   156,   156,    14,
     205,   -11,   -11,   -11,   -11,   -11,    62,     6,   156,   -11,
     -11,   176,   -11,   176,   176,   176,   176,   176,   176,   176,
     176,   176,   -11,   -11,   -11,   -11,   -11,   156,   156,   156,
     156,   156,   156,   156,   156,   156,   156,   -11,   -11,   -11,
     195,   -11,   -11,   -11,   -11,   -11,   -11,   -11,   -11,   -11,
     -11,   215,   215,   215,   215,   215,     2,     2,     2,     3,
     -11,   -11
};

/* YYPGOTO[NTERM-NUM].  */
static const yysigned_char yypgoto[] =
{
     -11,   -11,   102,     0,    42,    -4,   -11,    84,   -11
};

/* YYTABLE[YYPACT[STATE-NUM]].  What to do in state STATE-NUM.  If
   positive, shift that token.  If negative, reduce the rule which
   number is the opposite.  If zero, do what YYDEFACT says.
   If YYTABLE_NINF, syntax error.  */
#define YYTABLE_NINF -1
static const yysigned_char yytable[] =
{
      21,    21,    33,    34,    35,    36,    37,    38,    39,    40,
      41,    58,    59,    27,    46,    21,    21,    21,    21,    55,
      56,    56,     0,     0,     0,     0,     0,    61,    21,    62,
      63,    64,    65,    66,    67,    68,    69,    70,     0,     0,
       0,     0,    22,    22,     0,     0,     0,    21,    21,    21,
      21,    21,    21,    21,    21,    21,    21,    22,    22,    22,
      22,     0,     0,     0,     0,     0,    57,     0,     0,     0,
      22,    47,    48,    49,    50,    51,    52,    53,    54,    55,
      56,     0,     0,     0,    24,    24,     0,     0,     0,    22,
      22,    22,    22,    22,    22,    22,    22,    22,    22,    24,
      24,    24,    24,    26,     0,     0,     0,     0,     0,     0,
       0,     0,    24,     0,     0,     0,     0,    42,    43,    44,
      45,     0,     0,     0,     0,     0,     0,     0,     0,     0,
      60,    24,    24,    24,    24,    24,    24,    24,    24,    24,
      24,     0,     0,     0,     0,     0,     0,     0,     0,    71,
      72,    73,    74,    75,    76,    77,    78,    79,    80,     1,
       0,     2,     3,     0,     4,     5,     6,     7,     8,     9,
      10,    11,    12,    13,    14,    15,    16,    17,    18,    28,
       0,     2,     3,     0,     4,     5,     6,     7,     8,     9,
      10,    11,    12,    13,    14,    15,    16,    17,    18,    81,
       0,     0,     0,     0,    47,    48,    49,    50,    51,    52,
      53,    54,    55,    56,    47,    48,    49,    50,    51,    52,
      53,    54,    55,    56,    -1,    -1,    -1,    -1,    -1,    52,
      53,    54,    55,    56
};

static const yysigned_char yycheck[] =
{
       0,     1,     6,     7,     8,     9,    10,    11,    12,    13,
      14,     5,     6,    23,     0,    15,    16,    17,    18,    17,
      18,    18,    -1,    -1,    -1,    -1,    -1,    31,    28,    33,
      34,    35,    36,    37,    38,    39,    40,    41,    -1,    -1,
      -1,    -1,     0,     1,    -1,    -1,    -1,    47,    48,    49,
      50,    51,    52,    53,    54,    55,    56,    15,    16,    17,
      18,    -1,    -1,    -1,    -1,    -1,     4,    -1,    -1,    -1,
      28,     9,    10,    11,    12,    13,    14,    15,    16,    17,
      18,    -1,    -1,    -1,     0,     1,    -1,    -1,    -1,    47,
      48,    49,    50,    51,    52,    53,    54,    55,    56,    15,
      16,    17,    18,     1,    -1,    -1,    -1,    -1,    -1,    -1,
      -1,    -1,    28,    -1,    -1,    -1,    -1,    15,    16,    17,
      18,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,
      28,    47,    48,    49,    50,    51,    52,    53,    54,    55,
      56,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    -1,    47,
      48,    49,    50,    51,    52,    53,    54,    55,    56,     3,
      -1,     5,     6,    -1,     8,     9,    10,    11,    12,    13,
      14,    15,    16,    17,    18,    19,    20,    21,    22,     3,
      -1,     5,     6,    -1,     8,     9,    10,    11,    12,    13,
      14,    15,    16,    17,    18,    19,    20,    21,    22,     4,
      -1,    -1,    -1,    -1,     9,    10,    11,    12,    13,    14,
      15,    16,    17,    18,     9,    10,    11,    12,    13,    14,
      15,    16,    17,    18,     9,    10,    11,    12,    13,    14,
      15,    16,    17,    18
};

/* YYSTOS[STATE-NUM] -- The (internal number of the) accessing
   symbol of state STATE-NUM.  */
static const unsigned char yystos[] =
{
       0,     3,     5,     6,     8,     9,    10,    11,    12,    13,
      14,    15,    16,    17,    18,    19,    20,    21,    22,    25,
      26,    27,    28,    30,    31,    32,    26,    23,     3,    27,
      28,    29,    31,    29,    29,    29,    29,    29,    29,    29,
      29,    29,    26,    26,    26,    26,     0,     9,    10,    11,
      12,    13,    14,    15,    16,    17,    18,     4,     5,     6,
      26,    29,    29,    29,    29,    29,    29,    29,    29,    29,
      29,    26,    26,    26,    26,    26,    26,    26,    26,    26,
      26,     4
};

#if ! defined (YYSIZE_T) && defined (__SIZE_TYPE__)
# define YYSIZE_T __SIZE_TYPE__
#endif
#if ! defined (YYSIZE_T) && defined (size_t)
# define YYSIZE_T size_t
#endif
#if ! defined (YYSIZE_T)
# if defined (__STDC__) || defined (__cplusplus)
#  include <stddef.h> /* INFRINGES ON USER NAME SPACE */
#  define YYSIZE_T size_t
# endif
#endif
#if ! defined (YYSIZE_T)
# define YYSIZE_T unsigned int
#endif

#define yyerrok		(yyerrstatus = 0)
#define yyclearin	(yychar = YYEMPTY)
#define YYEMPTY		(-2)
#define YYEOF		0

#define YYACCEPT	goto yyacceptlab
#define YYABORT		goto yyabortlab
#define YYERROR		goto yyerrorlab


/* Like YYERROR except do call yyerror.  This remains here temporarily
   to ease the transition to the new meaning of YYERROR, for GCC.
   Once GCC version 2 has supplanted version 1, this can go.  */

#define YYFAIL		goto yyerrlab

#define YYRECOVERING()  (!!yyerrstatus)

#define YYBACKUP(Token, Value)					\
do								\
  if (yychar == YYEMPTY && yylen == 1)				\
    {								\
      yychar = (Token);						\
      yylval = (Value);						\
      yytoken = YYTRANSLATE (yychar);				\
      YYPOPSTACK;						\
      goto yybackup;						\
    }								\
  else								\
    { 								\
      yyerror ("syntax error: cannot back up");\
      YYERROR;							\
    }								\
while (0)


#define YYTERROR	1
#define YYERRCODE	256


/* YYLLOC_DEFAULT -- Set CURRENT to span from RHS[1] to RHS[N].
   If N is 0, then set CURRENT to the empty location which ends
   the previous symbol: RHS[0] (always defined).  */

#define YYRHSLOC(Rhs, K) ((Rhs)[K])
#ifndef YYLLOC_DEFAULT
# define YYLLOC_DEFAULT(Current, Rhs, N)				\
    do									\
      if (N)								\
	{								\
	  (Current).first_line   = YYRHSLOC (Rhs, 1).first_line;	\
	  (Current).first_column = YYRHSLOC (Rhs, 1).first_column;	\
	  (Current).last_line    = YYRHSLOC (Rhs, N).last_line;		\
	  (Current).last_column  = YYRHSLOC (Rhs, N).last_column;	\
	}								\
      else								\
	{								\
	  (Current).first_line   = (Current).last_line   =		\
	    YYRHSLOC (Rhs, 0).last_line;				\
	  (Current).first_column = (Current).last_column =		\
	    YYRHSLOC (Rhs, 0).last_column;				\
	}								\
    while (0)
#endif


/* YY_LOCATION_PRINT -- Print the location on the stream.
   This macro was not mandated originally: define only if we know
   we won't break user code: when these are the locations we know.  */

#ifndef YY_LOCATION_PRINT
# if YYLTYPE_IS_TRIVIAL
#  define YY_LOCATION_PRINT(File, Loc)			\
     fprintf (File, "%d.%d-%d.%d",			\
              (Loc).first_line, (Loc).first_column,	\
              (Loc).last_line,  (Loc).last_column)
# else
#  define YY_LOCATION_PRINT(File, Loc) ((void) 0)
# endif
#endif


/* YYLEX -- calling `yylex' with the right arguments.  */

#ifdef YYLEX_PARAM
# define YYLEX yylex (YYLEX_PARAM)
#else
# define YYLEX yylex ()
#endif

/* Enable debugging if requested.  */
#if YYDEBUG

# ifndef YYFPRINTF
#  include <stdio.h> /* INFRINGES ON USER NAME SPACE */
#  define YYFPRINTF fprintf
# endif

# define YYDPRINTF(Args)			\
do {						\
  if (yydebug)					\
    YYFPRINTF Args;				\
} while (0)

# define YY_SYMBOL_PRINT(Title, Type, Value, Location)		\
do {								\
  if (yydebug)							\
    {								\
      YYFPRINTF (stderr, "%s ", Title);				\
      yysymprint (stderr, 					\
                  Type, Value);	\
      YYFPRINTF (stderr, "\n");					\
    }								\
} while (0)

/*------------------------------------------------------------------.
| yy_stack_print -- Print the state stack from its BOTTOM up to its |
| TOP (included).                                                   |
`------------------------------------------------------------------*/

#if defined (__STDC__) || defined (__cplusplus)
static void
yy_stack_print (short int *bottom, short int *top)
#else
static void
yy_stack_print (bottom, top)
    short int *bottom;
    short int *top;
#endif
{
  YYFPRINTF (stderr, "Stack now");
  for (/* Nothing. */; bottom <= top; ++bottom)
    YYFPRINTF (stderr, " %d", *bottom);
  YYFPRINTF (stderr, "\n");
}

# define YY_STACK_PRINT(Bottom, Top)				\
do {								\
  if (yydebug)							\
    yy_stack_print ((Bottom), (Top));				\
} while (0)


/*------------------------------------------------.
| Report that the YYRULE is going to be reduced.  |
`------------------------------------------------*/

#if defined (__STDC__) || defined (__cplusplus)
static void
yy_reduce_print (int yyrule)
#else
static void
yy_reduce_print (yyrule)
    int yyrule;
#endif
{
  int yyi;
  unsigned int yylno = yyrline[yyrule];
  YYFPRINTF (stderr, "Reducing stack by rule %d (line %u), ",
             yyrule - 1, yylno);
  /* Print the symbols being reduced, and their result.  */
  for (yyi = yyprhs[yyrule]; 0 <= yyrhs[yyi]; yyi++)
    YYFPRINTF (stderr, "%s ", yytname [yyrhs[yyi]]);
  YYFPRINTF (stderr, "-> %s\n", yytname [yyr1[yyrule]]);
}

# define YY_REDUCE_PRINT(Rule)		\
do {					\
  if (yydebug)				\
    yy_reduce_print (Rule);		\
} while (0)

/* Nonzero means print parse trace.  It is left uninitialized so that
   multiple parsers can coexist.  */
int yydebug;
#else /* !YYDEBUG */
# define YYDPRINTF(Args)
# define YY_SYMBOL_PRINT(Title, Type, Value, Location)
# define YY_STACK_PRINT(Bottom, Top)
# define YY_REDUCE_PRINT(Rule)
#endif /* !YYDEBUG */


/* YYINITDEPTH -- initial size of the parser's stacks.  */
#ifndef	YYINITDEPTH
# define YYINITDEPTH 200
#endif

/* YYMAXDEPTH -- maximum size the stacks can grow to (effective only
   if the built-in stack extension method is used).

   Do not make this value too large; the results are undefined if
   SIZE_MAX < YYSTACK_BYTES (YYMAXDEPTH)
   evaluated with infinite-precision integer arithmetic.  */

#ifndef YYMAXDEPTH
# define YYMAXDEPTH 10000
#endif



#if YYERROR_VERBOSE

# ifndef yystrlen
#  if defined (__GLIBC__) && defined (_STRING_H)
#   define yystrlen strlen
#  else
/* Return the length of YYSTR.  */
static YYSIZE_T
#   if defined (__STDC__) || defined (__cplusplus)
yystrlen (const char *yystr)
#   else
yystrlen (yystr)
     const char *yystr;
#   endif
{
  register const char *yys = yystr;

  while (*yys++ != '\0')
    continue;

  return yys - yystr - 1;
}
#  endif
# endif

# ifndef yystpcpy
#  if defined (__GLIBC__) && defined (_STRING_H) && defined (_GNU_SOURCE)
#   define yystpcpy stpcpy
#  else
/* Copy YYSRC to YYDEST, returning the address of the terminating '\0' in
   YYDEST.  */
static char *
#   if defined (__STDC__) || defined (__cplusplus)
yystpcpy (char *yydest, const char *yysrc)
#   else
yystpcpy (yydest, yysrc)
     char *yydest;
     const char *yysrc;
#   endif
{
  register char *yyd = yydest;
  register const char *yys = yysrc;

  while ((*yyd++ = *yys++) != '\0')
    continue;

  return yyd - 1;
}
#  endif
# endif

#endif /* !YYERROR_VERBOSE */



#if YYDEBUG
/*--------------------------------.
| Print this symbol on YYOUTPUT.  |
`--------------------------------*/

#if defined (__STDC__) || defined (__cplusplus)
static void
yysymprint (FILE *yyoutput, int yytype, YYSTYPE *yyvaluep)
#else
static void
yysymprint (yyoutput, yytype, yyvaluep)
    FILE *yyoutput;
    int yytype;
    YYSTYPE *yyvaluep;
#endif
{
  /* Pacify ``unused variable'' warnings.  */
  (void) yyvaluep;

  if (yytype < YYNTOKENS)
    YYFPRINTF (yyoutput, "token %s (", yytname[yytype]);
  else
    YYFPRINTF (yyoutput, "nterm %s (", yytname[yytype]);


# ifdef YYPRINT
  if (yytype < YYNTOKENS)
    YYPRINT (yyoutput, yytoknum[yytype], *yyvaluep);
# endif
  switch (yytype)
    {
      default:
        break;
    }
  YYFPRINTF (yyoutput, ")");
}

#endif /* ! YYDEBUG */
/*-----------------------------------------------.
| Release the memory associated to this symbol.  |
`-----------------------------------------------*/

#if defined (__STDC__) || defined (__cplusplus)
static void
yydestruct (const char *yymsg, int yytype, YYSTYPE *yyvaluep)
#else
static void
yydestruct (yymsg, yytype, yyvaluep)
    const char *yymsg;
    int yytype;
    YYSTYPE *yyvaluep;
#endif
{
  /* Pacify ``unused variable'' warnings.  */
  (void) yyvaluep;

  if (!yymsg)
    yymsg = "Deleting";
  YY_SYMBOL_PRINT (yymsg, yytype, yyvaluep, yylocationp);

  switch (yytype)
    {

      default:
        break;
    }
}


/* Prevent warnings from -Wmissing-prototypes.  */

#ifdef YYPARSE_PARAM
# if defined (__STDC__) || defined (__cplusplus)
int yyparse (void *YYPARSE_PARAM);
# else
int yyparse ();
# endif
#else /* ! YYPARSE_PARAM */
#if defined (__STDC__) || defined (__cplusplus)
int yyparse (void);
#else
int yyparse ();
#endif
#endif /* ! YYPARSE_PARAM */



/* The look-ahead symbol.  */
int yychar;

/* The semantic value of the look-ahead symbol.  */
YYSTYPE yylval;

/* Number of syntax errors so far.  */
int yynerrs;



/*----------.
| yyparse.  |
`----------*/

#ifdef YYPARSE_PARAM
# if defined (__STDC__) || defined (__cplusplus)
int yyparse (void *YYPARSE_PARAM)
# else
int yyparse (YYPARSE_PARAM)
  void *YYPARSE_PARAM;
# endif
#else /* ! YYPARSE_PARAM */
#if defined (__STDC__) || defined (__cplusplus)
int
yyparse (void)
#else
int
yyparse ()

#endif
#endif
{
  
  register int yystate;
  register int yyn;
  int yyresult;
  /* Number of tokens to shift before error messages enabled.  */
  int yyerrstatus;
  /* Look-ahead token as an internal (translated) token number.  */
  int yytoken = 0;

  /* Three stacks and their tools:
     `yyss': related to states,
     `yyvs': related to semantic values,
     `yyls': related to locations.

     Refer to the stacks thru separate pointers, to allow yyoverflow
     to reallocate them elsewhere.  */

  /* The state stack.  */
  short int yyssa[YYINITDEPTH];
  short int *yyss = yyssa;
  register short int *yyssp;

  /* The semantic value stack.  */
  YYSTYPE yyvsa[YYINITDEPTH];
  YYSTYPE *yyvs = yyvsa;
  register YYSTYPE *yyvsp;



#define YYPOPSTACK   (yyvsp--, yyssp--)

  YYSIZE_T yystacksize = YYINITDEPTH;

  /* The variables used to return semantic value and location from the
     action routines.  */
  YYSTYPE yyval;


  /* When reducing, the number of symbols on the RHS of the reduced
     rule.  */
  int yylen;

  YYDPRINTF ((stderr, "Starting parse\n"));

  yystate = 0;
  yyerrstatus = 0;
  yynerrs = 0;
  yychar = YYEMPTY;		/* Cause a token to be read.  */

  /* Initialize stack pointers.
     Waste one element of value and location stack
     so that they stay on the same level as the state stack.
     The wasted elements are never initialized.  */

  yyssp = yyss;
  yyvsp = yyvs;


  yyvsp[0] = yylval;

  goto yysetstate;

/*------------------------------------------------------------.
| yynewstate -- Push a new state, which is found in yystate.  |
`------------------------------------------------------------*/
 yynewstate:
  /* In all cases, when you get here, the value and location stacks
     have just been pushed. so pushing a state here evens the stacks.
     */
  yyssp++;

 yysetstate:
  *yyssp = yystate;

  if (yyss + yystacksize - 1 <= yyssp)
    {
      /* Get the current used size of the three stacks, in elements.  */
      YYSIZE_T yysize = yyssp - yyss + 1;

#ifdef yyoverflow
      {
	/* Give user a chance to reallocate the stack. Use copies of
	   these so that the &'s don't force the real ones into
	   memory.  */
	YYSTYPE *yyvs1 = yyvs;
	short int *yyss1 = yyss;


	/* Each stack pointer address is followed by the size of the
	   data in use in that stack, in bytes.  This used to be a
	   conditional around just the two extra args, but that might
	   be undefined if yyoverflow is a macro.  */
	yyoverflow ("parser stack overflow",
		    &yyss1, yysize * sizeof (*yyssp),
		    &yyvs1, yysize * sizeof (*yyvsp),

		    &yystacksize);

	yyss = yyss1;
	yyvs = yyvs1;
      }
#else /* no yyoverflow */
# ifndef YYSTACK_RELOCATE
      goto yyoverflowlab;
# else
      /* Extend the stack our own way.  */
      if (YYMAXDEPTH <= yystacksize)
	goto yyoverflowlab;
      yystacksize *= 2;
      if (YYMAXDEPTH < yystacksize)
	yystacksize = YYMAXDEPTH;

      {
	short int *yyss1 = yyss;
	union yyalloc *yyptr =
	  (union yyalloc *) YYSTACK_ALLOC (YYSTACK_BYTES (yystacksize));
	if (! yyptr)
	  goto yyoverflowlab;
	YYSTACK_RELOCATE (yyss);
	YYSTACK_RELOCATE (yyvs);

#  undef YYSTACK_RELOCATE
	if (yyss1 != yyssa)
	  YYSTACK_FREE (yyss1);
      }
# endif
#endif /* no yyoverflow */

      yyssp = yyss + yysize - 1;
      yyvsp = yyvs + yysize - 1;


      YYDPRINTF ((stderr, "Stack size increased to %lu\n",
		  (unsigned long int) yystacksize));

      if (yyss + yystacksize - 1 <= yyssp)
	YYABORT;
    }

  YYDPRINTF ((stderr, "Entering state %d\n", yystate));

  goto yybackup;

/*-----------.
| yybackup.  |
`-----------*/
yybackup:

/* Do appropriate processing given the current state.  */
/* Read a look-ahead token if we need one and don't already have one.  */
/* yyresume: */

  /* First try to decide what to do without reference to look-ahead token.  */

  yyn = yypact[yystate];
  if (yyn == YYPACT_NINF)
    goto yydefault;

  /* Not known => get a look-ahead token if don't already have one.  */

  /* YYCHAR is either YYEMPTY or YYEOF or a valid look-ahead symbol.  */
  if (yychar == YYEMPTY)
    {
      YYDPRINTF ((stderr, "Reading a token: "));
      yychar = YYLEX;
    }

  if (yychar <= YYEOF)
    {
      yychar = yytoken = YYEOF;
      YYDPRINTF ((stderr, "Now at end of input.\n"));
    }
  else
    {
      yytoken = YYTRANSLATE (yychar);
      YY_SYMBOL_PRINT ("Next token is", yytoken, &yylval, &yylloc);
    }

  /* If the proper action on seeing token YYTOKEN is to reduce or to
     detect an error, take that action.  */
  yyn += yytoken;
  if (yyn < 0 || YYLAST < yyn || yycheck[yyn] != yytoken)
    goto yydefault;
  yyn = yytable[yyn];
  if (yyn <= 0)
    {
      if (yyn == 0 || yyn == YYTABLE_NINF)
	goto yyerrlab;
      yyn = -yyn;
      goto yyreduce;
    }

  if (yyn == YYFINAL)
    YYACCEPT;

  /* Shift the look-ahead token.  */
  YY_SYMBOL_PRINT ("Shifting", yytoken, &yylval, &yylloc);

  /* Discard the token being shifted unless it is eof.  */
  if (yychar != YYEOF)
    yychar = YYEMPTY;

  *++yyvsp = yylval;


  /* Count tokens shifted since error; after three, turn off error
     status.  */
  if (yyerrstatus)
    yyerrstatus--;

  yystate = yyn;
  goto yynewstate;


/*-----------------------------------------------------------.
| yydefault -- do the default action for the current state.  |
`-----------------------------------------------------------*/
yydefault:
  yyn = yydefact[yystate];
  if (yyn == 0)
    goto yyerrlab;
  goto yyreduce;


/*-----------------------------.
| yyreduce -- Do a reduction.  |
`-----------------------------*/
yyreduce:
  /* yyn is the number of a rule to reduce with.  */
  yylen = yyr2[yyn];

  /* If YYLEN is nonzero, implement the default value of the action:
     `$$ = $1'.

     Otherwise, the following line sets YYVAL to garbage.
     This behavior is undocumented and Bison
     users should not rely upon it.  Assigning to YYVAL
     unconditionally makes the parser a bit smaller, and it avoids a
     GCC warning that YYVAL may be used uninitialized.  */
  yyval = yyvsp[1-yylen];


  YY_REDUCE_PRINT (yyn);
  switch (yyn)
    {
        case 2:
#line 142 "Ltl-parse.yy"
    { result = (yyvsp[0].formula); }
    break;

  case 3:
#line 146 "Ltl-parse.yy"
    { (yyval.formula) = (yyvsp[0].formula); }
    break;

  case 4:
#line 149 "Ltl-parse.yy"
    { (yyval.formula) = (yyvsp[0].formula); }
    break;

  case 5:
#line 152 "Ltl-parse.yy"
    { (yyval.formula) = (yyvsp[0].formula); }
    break;

  case 6:
#line 155 "Ltl-parse.yy"
    { (yyval.formula) = (yyvsp[-1].formula); }
    break;

  case 7:
#line 159 "Ltl-parse.yy"
    { (yyval.formula) = (yyvsp[0].formula); }
    break;

  case 8:
#line 162 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-2].formula));
                         (yyval.formula) = newFormula(Not::construct((yyvsp[-2].formula)));
                       }
    break;

  case 9:
#line 168 "Ltl-parse.yy"
    { (yyval.formula) = (yyvsp[-2].formula); }
    break;

  case 10:
#line 171 "Ltl-parse.yy"
    { (yyval.formula) = newFormula(False::construct()); }
    break;

  case 11:
#line 174 "Ltl-parse.yy"
    { (yyval.formula) = newFormula(True::construct()); }
    break;

  case 12:
#line 178 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(Not::construct((yyvsp[0].formula)));
                       }
    break;

  case 13:
#line 184 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(Next::construct((yyvsp[0].formula)));
                       }
    break;

  case 14:
#line 190 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(Finally::construct((yyvsp[0].formula)));
                       }
    break;

  case 15:
#line 196 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(Globally::construct((yyvsp[0].formula)));
                       }
    break;

  case 16:
#line 203 "Ltl-parse.yy"
    { (yyval.formula) = (yyvsp[0].formula); }
    break;

  case 17:
#line 206 "Ltl-parse.yy"
    { (yyval.formula) = (yyvsp[0].formula); }
    break;

  case 18:
#line 209 "Ltl-parse.yy"
    { (yyval.formula) = (yyvsp[0].formula); }
    break;

  case 19:
#line 212 "Ltl-parse.yy"
    { (yyval.formula) = (yyvsp[-1].formula); }
    break;

  case 20:
#line 216 "Ltl-parse.yy"
    { (yyval.formula) = (yyvsp[0].formula); }
    break;

  case 21:
#line 218 "Ltl-parse.yy"
    { (yyval.formula) = (yyvsp[0].formula); }
    break;

  case 22:
#line 222 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-1].formula));
			 intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(And::construct((yyvsp[-1].formula), (yyvsp[0].formula)));
                       }
    break;

  case 23:
#line 229 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-1].formula));
			 intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(Or::construct((yyvsp[-1].formula), (yyvsp[0].formula)));
                       }
    break;

  case 24:
#line 236 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-1].formula));
			 intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(Imply::construct((yyvsp[-1].formula), (yyvsp[0].formula)));
                       }
    break;

  case 25:
#line 243 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-1].formula));
			 intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(Equiv::construct((yyvsp[-1].formula), (yyvsp[0].formula)));
                       }
    break;

  case 26:
#line 250 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-1].formula));
			 intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(Xor::construct((yyvsp[-1].formula), (yyvsp[0].formula)));
                       }
    break;

  case 27:
#line 257 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-1].formula));
			 intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(Until::construct((yyvsp[-1].formula), (yyvsp[0].formula)));
                       }
    break;

  case 28:
#line 264 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-1].formula));
			 intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(V::construct((yyvsp[-1].formula), (yyvsp[0].formula)));
                       }
    break;

  case 29:
#line 271 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-1].formula));
			 intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(WeakUntil::construct((yyvsp[-1].formula), (yyvsp[0].formula)));
                       }
    break;

  case 30:
#line 279 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-1].formula));
			 intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(StrongRelease::construct((yyvsp[-1].formula), (yyvsp[0].formula)));
                       }
    break;

  case 31:
#line 286 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-1].formula));
			 intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(Before::construct((yyvsp[-1].formula), (yyvsp[0].formula)));
                       }
    break;

  case 32:
#line 294 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-2].formula));
			 intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(And::construct((yyvsp[-2].formula), (yyvsp[0].formula)));
                       }
    break;

  case 33:
#line 301 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-2].formula));
			 intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(Or::construct((yyvsp[-2].formula), (yyvsp[0].formula)));
                       }
    break;

  case 34:
#line 308 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-2].formula));
			 intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(Imply::construct((yyvsp[-2].formula), (yyvsp[0].formula)));
                       }
    break;

  case 35:
#line 315 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-2].formula));
			 intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(Equiv::construct((yyvsp[-2].formula), (yyvsp[0].formula)));
                       }
    break;

  case 36:
#line 322 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-2].formula));
			 intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(Xor::construct((yyvsp[-2].formula), (yyvsp[0].formula)));
                       }
    break;

  case 37:
#line 329 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-2].formula));
			 intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(Until::construct((yyvsp[-2].formula), (yyvsp[0].formula)));
                       }
    break;

  case 38:
#line 336 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-2].formula));
			 intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(V::construct((yyvsp[-2].formula), (yyvsp[0].formula)));
                       }
    break;

  case 39:
#line 343 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-2].formula));
			 intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(WeakUntil::construct((yyvsp[-2].formula), (yyvsp[0].formula)));
                       }
    break;

  case 40:
#line 350 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-2].formula));
			 intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(StrongRelease::construct((yyvsp[-2].formula), (yyvsp[0].formula)));
                       }
    break;

  case 41:
#line 357 "Ltl-parse.yy"
    {
                         intermediate_results.erase((yyvsp[-2].formula));
			 intermediate_results.erase((yyvsp[0].formula));
                         (yyval.formula) = newFormula(Before::construct((yyvsp[-2].formula), (yyvsp[0].formula)));
                       }
    break;


    }

/* Line 1037 of yacc.c.  */
#line 1515 "Ltl-parse.cc"

  yyvsp -= yylen;
  yyssp -= yylen;


  YY_STACK_PRINT (yyss, yyssp);

  *++yyvsp = yyval;


  /* Now `shift' the result of the reduction.  Determine what state
     that goes to, based on the state we popped back to and the rule
     number reduced by.  */

  yyn = yyr1[yyn];

  yystate = yypgoto[yyn - YYNTOKENS] + *yyssp;
  if (0 <= yystate && yystate <= YYLAST && yycheck[yystate] == *yyssp)
    yystate = yytable[yystate];
  else
    yystate = yydefgoto[yyn - YYNTOKENS];

  goto yynewstate;


/*------------------------------------.
| yyerrlab -- here on detecting error |
`------------------------------------*/
yyerrlab:
  /* If not already recovering from an error, report this error.  */
  if (!yyerrstatus)
    {
      ++yynerrs;
#if YYERROR_VERBOSE
      yyn = yypact[yystate];

      if (YYPACT_NINF < yyn && yyn < YYLAST)
	{
	  YYSIZE_T yysize = 0;
	  int yytype = YYTRANSLATE (yychar);
	  const char* yyprefix;
	  char *yymsg;
	  int yyx;

	  /* Start YYX at -YYN if negative to avoid negative indexes in
	     YYCHECK.  */
	  int yyxbegin = yyn < 0 ? -yyn : 0;

	  /* Stay within bounds of both yycheck and yytname.  */
	  int yychecklim = YYLAST - yyn;
	  int yyxend = yychecklim < YYNTOKENS ? yychecklim : YYNTOKENS;
	  int yycount = 0;

	  yyprefix = ", expecting ";
	  for (yyx = yyxbegin; yyx < yyxend; ++yyx)
	    if (yycheck[yyx + yyn] == yyx && yyx != YYTERROR)
	      {
		yysize += yystrlen (yyprefix) + yystrlen (yytname [yyx]);
		yycount += 1;
		if (yycount == 5)
		  {
		    yysize = 0;
		    break;
		  }
	      }
	  yysize += (sizeof ("syntax error, unexpected ")
		     + yystrlen (yytname[yytype]));
	  yymsg = (char *) YYSTACK_ALLOC (yysize);
	  if (yymsg != 0)
	    {
	      char *yyp = yystpcpy (yymsg, "syntax error, unexpected ");
	      yyp = yystpcpy (yyp, yytname[yytype]);

	      if (yycount < 5)
		{
		  yyprefix = ", expecting ";
		  for (yyx = yyxbegin; yyx < yyxend; ++yyx)
		    if (yycheck[yyx + yyn] == yyx && yyx != YYTERROR)
		      {
			yyp = yystpcpy (yyp, yyprefix);
			yyp = yystpcpy (yyp, yytname[yyx]);
			yyprefix = " or ";
		      }
		}
	      yyerror (yymsg);
	      YYSTACK_FREE (yymsg);
	    }
	  else
	    yyerror ("syntax error; also virtual memory exhausted");
	}
      else
#endif /* YYERROR_VERBOSE */
	yyerror ("syntax error");
    }



  if (yyerrstatus == 3)
    {
      /* If just tried and failed to reuse look-ahead token after an
	 error, discard it.  */

      if (yychar <= YYEOF)
        {
          /* If at end of input, pop the error token,
	     then the rest of the stack, then return failure.  */
	  if (yychar == YYEOF)
	     for (;;)
	       {

		 YYPOPSTACK;
		 if (yyssp == yyss)
		   YYABORT;
		 yydestruct ("Error: popping",
                             yystos[*yyssp], yyvsp);
	       }
        }
      else
	{
	  yydestruct ("Error: discarding", yytoken, &yylval);
	  yychar = YYEMPTY;
	}
    }

  /* Else will try to reuse look-ahead token after shifting the error
     token.  */
  goto yyerrlab1;


/*---------------------------------------------------.
| yyerrorlab -- error raised explicitly by YYERROR.  |
`---------------------------------------------------*/
yyerrorlab:

#ifdef __GNUC__
  /* Pacify GCC when the user code never invokes YYERROR and the label
     yyerrorlab therefore never appears in user code.  */
  if (0)
     goto yyerrorlab;
#endif

yyvsp -= yylen;
  yyssp -= yylen;
  yystate = *yyssp;
  goto yyerrlab1;


/*-------------------------------------------------------------.
| yyerrlab1 -- common code for both syntax error and YYERROR.  |
`-------------------------------------------------------------*/
yyerrlab1:
  yyerrstatus = 3;	/* Each real token shifted decrements this.  */

  for (;;)
    {
      yyn = yypact[yystate];
      if (yyn != YYPACT_NINF)
	{
	  yyn += YYTERROR;
	  if (0 <= yyn && yyn <= YYLAST && yycheck[yyn] == YYTERROR)
	    {
	      yyn = yytable[yyn];
	      if (0 < yyn)
		break;
	    }
	}

      /* Pop the current state because it cannot handle the error token.  */
      if (yyssp == yyss)
	YYABORT;


      yydestruct ("Error: popping", yystos[yystate], yyvsp);
      YYPOPSTACK;
      yystate = *yyssp;
      YY_STACK_PRINT (yyss, yyssp);
    }

  if (yyn == YYFINAL)
    YYACCEPT;

  *++yyvsp = yylval;


  /* Shift the error token. */
  YY_SYMBOL_PRINT ("Shifting", yystos[yyn], yyvsp, yylsp);

  yystate = yyn;
  goto yynewstate;


/*-------------------------------------.
| yyacceptlab -- YYACCEPT comes here.  |
`-------------------------------------*/
yyacceptlab:
  yyresult = 0;
  goto yyreturn;

/*-----------------------------------.
| yyabortlab -- YYABORT comes here.  |
`-----------------------------------*/
yyabortlab:
  yydestruct ("Error: discarding lookahead",
              yytoken, &yylval);
  yychar = YYEMPTY;
  yyresult = 1;
  goto yyreturn;

#ifndef yyoverflow
/*----------------------------------------------.
| yyoverflowlab -- parser overflow comes here.  |
`----------------------------------------------*/
yyoverflowlab:
  yyerror ("parser stack overflow");
  yyresult = 2;
  /* Fall through.  */
#endif

yyreturn:
#ifndef yyoverflow
  if (yyss != yyssa)
    YYSTACK_FREE (yyss);
#endif
  return yyresult;
}


#line 364 "Ltl-parse.yy"




/******************************************************************************
 *
 * Helper function for reading lexical tokens from a stream.
 *
 *****************************************************************************/

static inline size_t matchCharactersFromStream
  (istream& stream, char* chars)
{
  size_t num_matched;
  for (num_matched = 0; *chars != '\0' && stream.peek() == *chars; ++chars)
  {
    stream.ignore(1);
    ++num_matched;
  }
  return num_matched;
}


/******************************************************************************
 *
 * Main interface to the parser.
 *
 *****************************************************************************/

namespace Ltl
{

/* ========================================================================= */
LtlFormula* parseFormula(istream& stream)
/* ----------------------------------------------------------------------------
 *
 * Description:   Parses an LTL formula from a stream.  The formula should be
 *                in one of the formats used by the tools lbtt 1.0.x (both
 *                prefix and infix form), Spin/Temporal Massage Parlor/LTL2BA,
 *                LTL2AUT or Wring 1.1.0 (actually, the grammar is basically
 *                a combination of the grammars of the above tools with the
 *                exception that propositions should always be written in the
 *                form `pN' for some integer N; in principle, it is possible to
 *                use a mixed syntax for the formula).  The input should be
 *                terminated with a newline.
 *
 * Argument:      stream  --  A reference to the input stream.
 *
 * Returns:       A pointer to the formula.  The function throws an
 *                LtlFormula::ParseErrorException if the syntax is incorrect,
 *                or an IOException in case of an end-of-file or another I/O
 *                error.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_istream es(&stream, ios::badbit | ios::failbit | ios::eofbit);
  estream = &es;
  intermediate_results.clear();

  try
  {
    ltl_parse();
  }
  catch (...)
  {
    for (std::set<LtlFormula*>::const_iterator
	   f = intermediate_results.begin();
	 f != intermediate_results.end();
	 ++f)
      LtlFormula::destruct(*f);
    throw;
  }
  return result;
}

}



/******************************************************************************
 *
 * The lexical scanner.
 *
 *****************************************************************************/

static int ltl_lex()
{
  char c;
  std::istream& stream = static_cast<istream&>(*estream);

  do
  {
    estream->get(c);
  }
  while (isspace(c) && c != '\n');

  switch (c)
  {
    case '\n' : return 0;

    case '(' :
      return (matchCharactersFromStream(stream, ")") == 1
	      ? LTLPARSE_NEXT
	      : LTLPARSE_LPAR);

    case ')' : return LTLPARSE_RPAR;

    case 'f' :
      switch (matchCharactersFromStream(stream, "alse"))
      {
        case 0 : case 4 :
	  return LTLPARSE_FALSE;
        default:
	  break;
      }
      return LTLPARSE_UNKNOWN;

    case '0' : return LTLPARSE_FALSE;

    case 't' :
      switch (matchCharactersFromStream(stream, "rue"))
      {
        case 0 : case 3 :
	  return LTLPARSE_TRUE;
        default :
	  return LTLPARSE_UNKNOWN;
      }

    case 'T' :
      return (matchCharactersFromStream(stream, "RUE") == 3
	      ? LTLPARSE_TRUE
	      : LTLPARSE_UNKNOWN);

    case '1' : return LTLPARSE_TRUE;

    case '!' : case '~' : return LTLPARSE_NOT;

    case '&' :
      matchCharactersFromStream(stream, "&");
      return LTLPARSE_AND;

    case '/' :
      return (matchCharactersFromStream(stream, "\\") == 1
	      ? LTLPARSE_AND
	      : LTLPARSE_UNKNOWN);

    case '*' : return LTLPARSE_AND;

    case '|' :
      matchCharactersFromStream(stream, "|");
      return LTLPARSE_OR;

    case '\\' :
      return (matchCharactersFromStream(stream, "/") == 1
	      ? LTLPARSE_OR
	      : LTLPARSE_UNKNOWN);

    case '+' : return LTLPARSE_OR;

    case '=' :
      return (matchCharactersFromStream(stream, ">") == 1
	      ? LTLPARSE_IMPLY
	      : LTLPARSE_EQUALS);

    case '-' :
      return (matchCharactersFromStream(stream, ">") == 1
	      ? LTLPARSE_IMPLY
	      : LTLPARSE_UNKNOWN);

    case 'i' : return LTLPARSE_IMPLY;

    case '<' :
      if (matchCharactersFromStream(stream, ">") == 1)
	return LTLPARSE_FINALLY;
      return (matchCharactersFromStream(stream, "->") == 2
	      || matchCharactersFromStream(stream, "=>") == 2
	      ? LTLPARSE_EQUIV
	      : LTLPARSE_UNKNOWN);

    case 'e' : return LTLPARSE_EQUIV;

    case 'x' :
      return (matchCharactersFromStream(stream, "or") == 2
	      ? LTLPARSE_XOR
	      : LTLPARSE_UNKNOWN);

    case '^' : return LTLPARSE_XOR;

    case 'X' : return LTLPARSE_NEXT;

    case 'U' : return LTLPARSE_UNTIL;

    case 'V' : case 'R' : return LTLPARSE_RELEASE;

    case 'W' : return LTLPARSE_WEAK_UNTIL;

    case 'M' : return LTLPARSE_STRONG_RELEASE;

    case 'B' : return LTLPARSE_BEFORE;

    case 'F' :
      switch (matchCharactersFromStream(stream, "ALSE"))
      {
	case 0 :
	  return LTLPARSE_FINALLY;
        case 4 :
	  return LTLPARSE_FALSE;
        default :
	  return LTLPARSE_UNKNOWN;
      }

    case '[' :
      return (matchCharactersFromStream(stream, "]") == 1
	      ? LTLPARSE_GLOBALLY
	      : LTLPARSE_UNKNOWN);

    case 'G' : return LTLPARSE_GLOBALLY;

    case 'p' :
    {
      long int id = 0;
      bool id_ok = false;
      int ch = stream.peek();
      while (ch >= '0' && ch <= '9')
      {
	id_ok = true;
	estream->get(c);
	if (LONG_MAX / 10 < id)
	  throw LtlFormula::ParseErrorException
	          ("error parsing LTL formula (proposition identifier out of "
		   "range)");
	id *= 10;
	id += (c - '0');
	ch = stream.peek();
      }

      if (id_ok)
      {
	ltl_lval.formula = newFormula(Atom::construct(id));
	return LTLPARSE_ATOM;
      }
      return LTLPARSE_UNKNOWN;
    }

    default : return LTLPARSE_UNKNOWN;
  }
}

