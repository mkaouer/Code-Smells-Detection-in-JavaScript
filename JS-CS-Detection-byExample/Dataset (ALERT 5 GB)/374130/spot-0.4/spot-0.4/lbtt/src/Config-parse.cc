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




/* Copy the first part of user declarations.  */
#line 20 "Config-parse.yy"


#include <config.h>
#include <string>
#include "Configuration.h"
#include "StringUtil.h"

using namespace ::StringUtil;




/******************************************************************************
 *
 * Variables for parsing the configuration file.
 *
 *****************************************************************************/

static string algorithm_name, algorithm_path,       /* Implementation       */
              algorithm_parameters;                 /* attributes read from */
static bool algorithm_enabled;                      /* an `Algorithm' block
                                                     * in the configuration
                                                     * file.
                                                     */

static int algorithm_begin_line;                    /* Input file line number
						     * denoting the beginning
						     * of the most recently
						     * encountered `Algorithm'
						     * block.
						     */

static int expected_token;                          /* Type of a token to be
						     * encountered next when
						     * reading the
						     * configuration file.
						     */

static Configuration* parser_cfg;                   /* Pointer to a
                                                     * Configuration data
                                                     * structure in which
                                                     * the configuration will
                                                     * be stored.
                                                     */

int config_file_line_number;                        /* Number of the current
                                                     * line in the
                                                     * configuration
                                                     * file.
                                                     */



/******************************************************************************
 *
 * Declarations for external functions and variables (provided by the lexer)
 * used when parsing the configuration file.
 *
 *****************************************************************************/

#ifdef YYTEXT_POINTER
extern char* yytext;                                /* Current token in the  */
#else	                                            /* input (provided by    */
extern char yytext[];                               /* the lexer).           */
#endif /* YYTEXT_POINTER */

extern void yyrestart(FILE*);                       /* Changes the input stream
                                                     * for the lexer (provided
                                                     * by the lexer).
                                                     */

extern int yylex();                                 /* Reads the next token
						     * from the input (this
						     * function is provided by
						     * the lexer).
						     */



/******************************************************************************
 *
 * Function for reporting parse errors.
 *
 *****************************************************************************/

/* ========================================================================= */
void yyerror(const char* error_message)
/* ----------------------------------------------------------------------------
 *
 * Description:   Function for reporting parse errors.
 *
 * Arguments:     error_message  --  An error message.
 *
 * Returns:       Nothing. Instead, throws a
 *                Configuration::ConfigurationException initialized with the
 *                given error message.
 *
 * ------------------------------------------------------------------------- */
{
  const string unknown_token(yytext);
  string msg;

  switch (expected_token)
  {
    case CFG_BLOCK_ID :
      msg = "`" + unknown_token + "' is not a valid block identifier";
      break;
    case CFG_OPTION_ID :
      if (!unknown_token.empty())
	msg = "`" + unknown_token + "' is not a valid option identifier";
      else
	msg = "'}' expected at the end of block";
      break;
    case CFG_LBRACE :
      msg = "`{' expected after block identifier";
      break;
    case CFG_EQUALS :
      msg = "`=' expected after option identifier";
      break;
    case CFG_VALUE :
      msg = "value for option expected";
      break;
    default :
      msg = error_message;
      break;
  }

  throw Configuration::ConfigurationException(config_file_line_number, msg);
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
#line 163 "Config-parse.yy"
typedef union YYSTYPE {
  const char* value;
} YYSTYPE;
/* Line 190 of yacc.c.  */
#line 316 "Config-parse.cc"
# define yystype YYSTYPE /* obsolescent; will be withdrawn */
# define YYSTYPE_IS_DECLARED 1
# define YYSTYPE_IS_TRIVIAL 1
#endif



/* Copy the second part of user declarations.  */


/* Line 213 of yacc.c.  */
#line 328 "Config-parse.cc"

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
#define YYFINAL  3
/* YYLAST -- Last index in YYTABLE.  */
#define YYLAST   102

/* YYNTOKENS -- Number of terminals. */
#define YYNTOKENS  53
/* YYNNTS -- Number of nonterminals. */
#define YYNNTS  28
/* YYNRULES -- Number of rules. */
#define YYNRULES  76
/* YYNRULES -- Number of states. */
#define YYNSTATES  130

/* YYTRANSLATE(YYLEX) -- Bison symbol number corresponding to YYLEX.  */
#define YYUNDEFTOK  2
#define YYMAXUTOK   307

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
      15,    16,    17,    18,    19,    20,    21,    22,    23,    24,
      25,    26,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    39,    40,    41,    42,    43,    44,
      45,    46,    47,    48,    49,    50,    51,    52
};

#if YYDEBUG
/* YYPRHS[YYN] -- Index of the first RHS symbol of rule number YYN in
   YYRHS.  */
static const unsigned char yyprhs[] =
{
       0,     0,     3,     5,     6,     7,    12,    13,    14,    18,
      20,    22,    24,    26,    27,    33,    34,    35,    39,    42,
      45,    48,    51,    52,    58,    59,    60,    64,    67,    70,
      73,    76,    79,    82,    85,    88,    89,    95,    96,    97,
     101,   104,   107,   110,   113,   116,   119,   122,   123,   129,
     130,   131,   135,   138,   141,   144,   147,   150,   153,   156,
     159,   162,   165,   168,   171,   174,   177,   180,   183,   186,
     189,   192,   195,   198,   201,   204,   207
};

/* YYRHS -- A `-1'-separated list of the rules' RHS. */
static const yysigned_char yyrhs[] =
{
      54,     0,    -1,    58,    -1,    -1,    -1,    56,    48,    57,
      51,    -1,    -1,    -1,    58,    59,    60,    -1,    61,    -1,
      66,    -1,    71,    -1,    76,    -1,    -1,     3,    62,    46,
      63,    47,    -1,    -1,    -1,    63,    64,    65,    -1,     4,
      55,    -1,     5,    55,    -1,     6,    55,    -1,     7,    55,
      -1,    -1,     8,    67,    46,    68,    47,    -1,    -1,    -1,
      68,    69,    70,    -1,     9,    55,    -1,    10,    55,    -1,
      11,    55,    -1,    12,    55,    -1,    13,    55,    -1,    14,
      55,    -1,    15,    55,    -1,    16,    55,    -1,    -1,    17,
      72,    46,    73,    47,    -1,    -1,    -1,    73,    74,    75,
      -1,    22,    55,    -1,    18,    55,    -1,    32,    55,    -1,
      19,    55,    -1,    23,    55,    -1,    20,    55,    -1,    21,
      55,    -1,    -1,    24,    77,    46,    78,    47,    -1,    -1,
      -1,    78,    79,    80,    -1,    25,    55,    -1,    26,    55,
      -1,    27,    55,    -1,    22,    55,    -1,    28,    55,    -1,
      29,    55,    -1,    30,    55,    -1,    31,    55,    -1,    32,
      55,    -1,    33,    55,    -1,    34,    55,    -1,    35,    55,
      -1,    36,    55,    -1,    37,    55,    -1,    38,    55,    -1,
      39,    55,    -1,    19,    55,    -1,    23,    55,    -1,    40,
      55,    -1,    20,    55,    -1,    41,    55,    -1,    42,    55,
      -1,    43,    55,    -1,    44,    55,    -1,    45,    55,    -1
};

/* YYRLINE[YYN] -- source line where rule number YYN was defined.  */
static const unsigned short int yyrline[] =
{
       0,   213,   213,   216,   218,   216,   223,   225,   224,   229,
     230,   231,   232,   236,   235,   253,   255,   254,   259,   266,
     269,   274,   279,   278,   283,   285,   284,   289,   297,   305,
     308,   316,   319,   327,   330,   340,   339,   344,   346,   345,
     350,   358,   366,   369,   377,   386,   393,   403,   402,   407,
     409,   408,   413,   421,   430,   439,   447,   456,   465,   474,
     483,   490,   499,   508,   517,   526,   535,   542,   551,   560,
     569,   578,   585,   594,   603,   612,   621
};
#endif

#if YYDEBUG || YYERROR_VERBOSE
/* YYTNME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
   First, the terminals, then, starting at YYNTOKENS, nonterminals. */
static const char *const yytname[] =
{
  "$end", "error", "$undefined", "CFG_ALGORITHM", "CFG_ENABLED",
  "CFG_NAME", "CFG_PARAMETERS", "CFG_PROGRAMPATH", "CFG_GLOBALOPTIONS",
  "CFG_COMPARISONTEST", "CFG_CONSISTENCYTEST", "CFG_INTERACTIVE",
  "CFG_INTERSECTIONTEST", "CFG_MODELCHECK", "CFG_ROUNDS",
  "CFG_TRANSLATORTIMEOUT", "CFG_VERBOSITY", "CFG_STATESPACEOPTIONS",
  "CFG_EDGEPROBABILITY", "CFG_PROPOSITIONS", "CFG_SIZE",
  "CFG_TRUTHPROBABILITY", "CFG_CHANGEINTERVAL", "CFG_RANDOMSEED",
  "CFG_FORMULAOPTIONS", "CFG_ABBREVIATEDOPERATORS", "CFG_ANDPRIORITY",
  "CFG_BEFOREPRIORITY", "CFG_DEFAULTOPERATORPRIORITY",
  "CFG_EQUIVALENCEPRIORITY", "CFG_FALSEPRIORITY", "CFG_FINALLYPRIORITY",
  "CFG_GENERATEMODE", "CFG_GLOBALLYPRIORITY", "CFG_IMPLICATIONPRIORITY",
  "CFG_NEXTPRIORITY", "CFG_NOTPRIORITY", "CFG_ORPRIORITY",
  "CFG_OUTPUTMODE", "CFG_PROPOSITIONPRIORITY", "CFG_RELEASEPRIORITY",
  "CFG_STRONGRELEASEPRIORITY", "CFG_TRUEPRIORITY", "CFG_UNTILPRIORITY",
  "CFG_WEAKUNTILPRIORITY", "CFG_XORPRIORITY", "CFG_LBRACE", "CFG_RBRACE",
  "CFG_EQUALS", "CFG_BLOCK_ID", "CFG_OPTION_ID", "CFG_VALUE",
  "CFG_UNKNOWN", "$accept", "configuration_file", "equals_value", "@1",
  "@2", "configuration_blocks", "@3", "configuration_block",
  "algorithm_option_block", "@4", "algorithm_options", "@5",
  "algorithm_option", "global_option_block", "@6", "global_options", "@7",
  "global_option", "statespace_option_block", "@8", "statespace_options",
  "@9", "statespace_option", "formula_option_block", "@10",
  "formula_options", "@11", "formula_option", 0
};
#endif

# ifdef YYPRINT
/* YYTOKNUM[YYLEX-NUM] -- Internal token number corresponding to
   token YYLEX-NUM.  */
static const unsigned short int yytoknum[] =
{
       0,   256,   257,   258,   259,   260,   261,   262,   263,   264,
     265,   266,   267,   268,   269,   270,   271,   272,   273,   274,
     275,   276,   277,   278,   279,   280,   281,   282,   283,   284,
     285,   286,   287,   288,   289,   290,   291,   292,   293,   294,
     295,   296,   297,   298,   299,   300,   301,   302,   303,   304,
     305,   306,   307
};
# endif

/* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
static const unsigned char yyr1[] =
{
       0,    53,    54,    56,    57,    55,    58,    59,    58,    60,
      60,    60,    60,    62,    61,    63,    64,    63,    65,    65,
      65,    65,    67,    66,    68,    69,    68,    70,    70,    70,
      70,    70,    70,    70,    70,    72,    71,    73,    74,    73,
      75,    75,    75,    75,    75,    75,    75,    77,    76,    78,
      79,    78,    80,    80,    80,    80,    80,    80,    80,    80,
      80,    80,    80,    80,    80,    80,    80,    80,    80,    80,
      80,    80,    80,    80,    80,    80,    80
};

/* YYR2[YYN] -- Number of symbols composing right hand side of rule YYN.  */
static const unsigned char yyr2[] =
{
       0,     2,     1,     0,     0,     4,     0,     0,     3,     1,
       1,     1,     1,     0,     5,     0,     0,     3,     2,     2,
       2,     2,     0,     5,     0,     0,     3,     2,     2,     2,
       2,     2,     2,     2,     2,     0,     5,     0,     0,     3,
       2,     2,     2,     2,     2,     2,     2,     0,     5,     0,
       0,     3,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2
};

/* YYDEFACT[STATE-NAME] -- Default rule to reduce with in state
   STATE-NUM when YYTABLE doesn't specify something else to do.  Zero
   means the default is an error.  */
static const unsigned char yydefact[] =
{
       6,     0,     7,     1,     0,    13,    22,    35,    47,     8,
       9,    10,    11,    12,     0,     0,     0,     0,    15,    24,
      37,    49,    16,    25,    38,    50,    14,     0,    23,     0,
      36,     0,    48,     0,     3,     3,     3,     3,    17,     3,
       3,     3,     3,     3,     3,     3,     3,    26,     3,     3,
       3,     3,     3,     3,     3,    39,     3,     3,     3,     3,
       3,     3,     3,     3,     3,     3,     3,     3,     3,     3,
       3,     3,     3,     3,     3,     3,     3,     3,     3,     3,
       3,    51,    18,     0,    19,    20,    21,    27,    28,    29,
      30,    31,    32,    33,    34,    41,    43,    45,    46,    40,
      44,    42,    68,    71,    55,    69,    52,    53,    54,    56,
      57,    58,    59,    60,    61,    62,    63,    64,    65,    66,
      67,    70,    72,    73,    74,    75,    76,     4,     0,     5
};

/* YYDEFGOTO[NTERM-NUM]. */
static const short int yydefgoto[] =
{
      -1,     1,    82,    83,   128,     2,     4,     9,    10,    14,
      22,    27,    38,    11,    15,    23,    29,    47,    12,    16,
      24,    31,    55,    13,    17,    25,    33,    81
};

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
#define YYPACT_NINF -36
static const yysigned_char yypact[] =
{
     -36,     3,    12,   -36,    70,   -36,   -36,   -36,   -36,   -36,
     -36,   -36,   -36,   -36,   -26,     2,     5,    28,   -36,   -36,
     -36,   -36,    29,    30,    38,    39,   -36,    84,   -36,    86,
     -36,    61,   -36,    27,   -36,   -36,   -36,   -36,   -36,   -36,
     -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,
     -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,
     -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,
     -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,
     -36,   -36,   -36,    44,   -36,   -36,   -36,   -36,   -36,   -36,
     -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,
     -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,
     -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,
     -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,    24,   -36
};

/* YYPGOTO[NTERM-NUM].  */
static const yysigned_char yypgoto[] =
{
     -36,   -36,   -35,   -36,   -36,   -36,   -36,   -36,   -36,   -36,
     -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36,
     -36,   -36,   -36,   -36,   -36,   -36,   -36,   -36
};

/* YYTABLE[YYPACT[STATE-NUM]].  What to do in state STATE-NUM.  If
   positive, shift that token.  If negative, reduce the rule which
   number is the opposite.  If zero, do what YYDEFACT says.
   If YYTABLE_NINF, syntax error.  */
#define YYTABLE_NINF -3
static const short int yytable[] =
{
      84,    85,    86,     3,    87,    88,    89,    90,    91,    92,
      93,    94,    -2,    95,    96,    97,    98,    99,   100,   101,
      18,   102,   103,   104,   105,   106,   107,   108,   109,   110,
     111,   112,   113,   114,   115,   116,   117,   118,   119,   120,
     121,   122,   123,   124,   125,   126,    56,    57,    19,    58,
      59,    20,    60,    61,    62,    63,    64,    65,    66,    67,
      68,    69,    70,    71,    72,    73,    74,    75,    76,    77,
      78,    79,    80,     5,    21,   129,    26,    28,     6,    48,
      49,    50,    51,    52,    53,    30,    32,     7,    34,    35,
      36,    37,   127,    54,     8,    39,    40,    41,    42,    43,
      44,    45,    46
};

static const unsigned char yycheck[] =
{
      35,    36,    37,     0,    39,    40,    41,    42,    43,    44,
      45,    46,     0,    48,    49,    50,    51,    52,    53,    54,
      46,    56,    57,    58,    59,    60,    61,    62,    63,    64,
      65,    66,    67,    68,    69,    70,    71,    72,    73,    74,
      75,    76,    77,    78,    79,    80,    19,    20,    46,    22,
      23,    46,    25,    26,    27,    28,    29,    30,    31,    32,
      33,    34,    35,    36,    37,    38,    39,    40,    41,    42,
      43,    44,    45,     3,    46,    51,    47,    47,     8,    18,
      19,    20,    21,    22,    23,    47,    47,    17,     4,     5,
       6,     7,    48,    32,    24,     9,    10,    11,    12,    13,
      14,    15,    16
};

/* YYSTOS[STATE-NUM] -- The (internal number of the) accessing
   symbol of state STATE-NUM.  */
static const unsigned char yystos[] =
{
       0,    54,    58,     0,    59,     3,     8,    17,    24,    60,
      61,    66,    71,    76,    62,    67,    72,    77,    46,    46,
      46,    46,    63,    68,    73,    78,    47,    64,    47,    69,
      47,    74,    47,    79,     4,     5,     6,     7,    65,     9,
      10,    11,    12,    13,    14,    15,    16,    70,    18,    19,
      20,    21,    22,    23,    32,    75,    19,    20,    22,    23,
      25,    26,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    39,    40,    41,    42,    43,    44,
      45,    80,    55,    56,    55,    55,    55,    55,    55,    55,
      55,    55,    55,    55,    55,    55,    55,    55,    55,    55,
      55,    55,    55,    55,    55,    55,    55,    55,    55,    55,
      55,    55,    55,    55,    55,    55,    55,    55,    55,    55,
      55,    55,    55,    55,    55,    55,    55,    48,    57,    51
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
        case 3:
#line 216 "Config-parse.yy"
    { expected_token = CFG_EQUALS; }
    break;

  case 4:
#line 218 "Config-parse.yy"
    { expected_token = CFG_VALUE; }
    break;

  case 5:
#line 220 "Config-parse.yy"
    { (yyval.value) = (yyvsp[0].value); }
    break;

  case 7:
#line 225 "Config-parse.yy"
    { expected_token = CFG_BLOCK_ID; }
    break;

  case 13:
#line 236 "Config-parse.yy"
    {
                               algorithm_name = "";
                               algorithm_path = "";
                               algorithm_parameters = "";
                               algorithm_enabled = true;
                               algorithm_begin_line = config_file_line_number;
                               expected_token = CFG_LBRACE;
                             }
    break;

  case 14:
#line 245 "Config-parse.yy"
    {
                               parser_cfg->registerAlgorithm
                                 (algorithm_name, algorithm_path,
                                  algorithm_parameters, algorithm_enabled,
                                  algorithm_begin_line);
                             }
    break;

  case 16:
#line 255 "Config-parse.yy"
    { expected_token = CFG_OPTION_ID; }
    break;

  case 18:
#line 260 "Config-parse.yy"
    {
                               parser_cfg->readTruthValue
                                             (algorithm_enabled,
                                              (yyvsp[0].value));
                             }
    break;

  case 19:
#line 267 "Config-parse.yy"
    { algorithm_name = unquoteString((yyvsp[0].value)); }
    break;

  case 20:
#line 270 "Config-parse.yy"
    {
                               algorithm_parameters = unquoteString((yyvsp[0].value));
                             }
    break;

  case 21:
#line 275 "Config-parse.yy"
    { algorithm_path = unquoteString((yyvsp[0].value)); }
    break;

  case 22:
#line 279 "Config-parse.yy"
    { expected_token = CFG_LBRACE; }
    break;

  case 25:
#line 285 "Config-parse.yy"
    { expected_token = CFG_OPTION_ID; }
    break;

  case 27:
#line 290 "Config-parse.yy"
    {
                               parser_cfg->readTruthValue
                                             (parser_cfg->global_options.
                                                do_comp_test,
                                              (yyvsp[0].value));
                             }
    break;

  case 28:
#line 298 "Config-parse.yy"
    {
                               parser_cfg->readTruthValue
                                             (parser_cfg->global_options.
                                                do_cons_test,
                                              (yyvsp[0].value));
			     }
    break;

  case 29:
#line 306 "Config-parse.yy"
    { parser_cfg->readInteractivity((yyvsp[0].value)); }
    break;

  case 30:
#line 309 "Config-parse.yy"
    {
                               parser_cfg->readTruthValue
                                             (parser_cfg->global_options.
                                                do_intr_test,
                                              (yyvsp[0].value));
                             }
    break;

  case 31:
#line 317 "Config-parse.yy"
    { parser_cfg->readProductType((yyvsp[0].value)); }
    break;

  case 32:
#line 320 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->global_options.number_of_rounds,
                                  (yyvsp[0].value),
                                  Configuration::ROUND_COUNT_RANGE);
                             }
    break;

  case 33:
#line 328 "Config-parse.yy"
    { parser_cfg->readTranslatorTimeout((yyvsp[0].value)); }
    break;

  case 34:
#line 331 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->global_options.verbosity,
                                  (yyvsp[0].value),
                                  Configuration::VERBOSITY_RANGE);
                             }
    break;

  case 35:
#line 340 "Config-parse.yy"
    { expected_token = CFG_LBRACE; }
    break;

  case 38:
#line 346 "Config-parse.yy"
    { expected_token = CFG_OPTION_ID; }
    break;

  case 40:
#line 351 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                             (parser_cfg->global_options.
                                                statespace_change_interval,
                                              (yyvsp[0].value));
                             }
    break;

  case 41:
#line 359 "Config-parse.yy"
    {
                               parser_cfg->readProbability
                                             (parser_cfg->statespace_generator.
                                                edge_probability,
                                              (yyvsp[0].value));
                             }
    break;

  case 42:
#line 367 "Config-parse.yy"
    { parser_cfg->readStateSpaceMode((yyvsp[0].value)); }
    break;

  case 43:
#line 370 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                             (parser_cfg->statespace_generator.
                                                atoms_per_state,
                                              (yyvsp[0].value));
                             }
    break;

  case 44:
#line 378 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->global_options.
                                                statespace_random_seed,
                                  (yyvsp[0].value),
                                  Configuration::RANDOM_SEED_RANGE);
                             }
    break;

  case 45:
#line 387 "Config-parse.yy"
    {
                               parser_cfg->readSize
                                 (Configuration::OPT_STATESPACESIZE,
                                  (yyvsp[0].value));
                             }
    break;

  case 46:
#line 394 "Config-parse.yy"
    {
                               parser_cfg->readProbability
                                             (parser_cfg->statespace_generator.
                                                truth_probability,
                                              (yyvsp[0].value));
                             }
    break;

  case 47:
#line 403 "Config-parse.yy"
    { expected_token = CFG_LBRACE; }
    break;

  case 50:
#line 409 "Config-parse.yy"
    { expected_token = CFG_OPTION_ID; }
    break;

  case 52:
#line 414 "Config-parse.yy"
    {
                               parser_cfg->readTruthValue
                                             (parser_cfg->formula_options.
                                                allow_abbreviated_operators,
                                              (yyvsp[0].value));
                             }
    break;

  case 53:
#line 422 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_CONJUNCTION],
                                  (yyvsp[0].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 54:
#line 431 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_BEFORE],
                                  (yyvsp[0].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 55:
#line 440 "Config-parse.yy"
    {
                               parser_cfg->readInteger
				             (parser_cfg->global_options.
                                                formula_change_interval,
                                              (yyvsp[0].value));
                             }
    break;

  case 56:
#line 448 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.
                                    default_operator_priority,
                                  (yyvsp[0].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 57:
#line 457 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_EQUIVALENCE],
                                  (yyvsp[0].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 58:
#line 466 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_FALSE],
                                  (yyvsp[0].value),
                                  Configuration::ATOMIC_PRIORITY_RANGE);
                             }
    break;

  case 59:
#line 475 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_FINALLY],
                                  (yyvsp[0].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 60:
#line 484 "Config-parse.yy"
    {
                               parser_cfg->readFormulaMode
                                 (parser_cfg->formula_options.generate_mode,
                                  (yyvsp[0].value));
                             }
    break;

  case 61:
#line 491 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_GLOBALLY],
                                  (yyvsp[0].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 62:
#line 500 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_IMPLICATION],
                                  (yyvsp[0].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 63:
#line 509 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_NEXT],
                                  (yyvsp[0].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 64:
#line 518 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_NEGATION],
                                  (yyvsp[0].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 65:
#line 527 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_DISJUNCTION],
                                  (yyvsp[0].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 66:
#line 536 "Config-parse.yy"
    {
                               parser_cfg->readFormulaMode
                                 (parser_cfg->formula_options.output_mode,
                                  (yyvsp[0].value));
                             }
    break;

  case 67:
#line 543 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_ATOM],
                                  (yyvsp[0].value),
                                  Configuration::ATOMIC_PRIORITY_RANGE);
                             }
    break;

  case 68:
#line 552 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                             (parser_cfg->formula_options.
                                                formula_generator.
                                                number_of_available_variables,
                                              (yyvsp[0].value));
                             }
    break;

  case 69:
#line 561 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->global_options.
                                                formula_random_seed,
                                  (yyvsp[0].value),
                                  Configuration::RANDOM_SEED_RANGE);
                             }
    break;

  case 70:
#line 570 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_V],
                                  (yyvsp[0].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 71:
#line 579 "Config-parse.yy"
    {
                               parser_cfg->readSize
                                             (Configuration::OPT_FORMULASIZE,
                                              (yyvsp[0].value));
                             }
    break;

  case 72:
#line 586 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_STRONG_RELEASE],
                                  (yyvsp[0].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 73:
#line 595 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_TRUE],
                                  (yyvsp[0].value),
                                  Configuration::ATOMIC_PRIORITY_RANGE);
                             }
    break;

  case 74:
#line 604 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_UNTIL],
                                  (yyvsp[0].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 75:
#line 613 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_WEAK_UNTIL],
                                  (yyvsp[0].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 76:
#line 622 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_XOR],
                                  (yyvsp[0].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;


    }

/* Line 1037 of yacc.c.  */
#line 1861 "Config-parse.cc"

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


#line 631 "Config-parse.yy"




/******************************************************************************
 *
 * Main interface to the parser.
 *
 *****************************************************************************/

/* ========================================================================= */
int parseConfiguration(FILE* stream, Configuration& configuration)
/* ----------------------------------------------------------------------------
 *
 * Description:   Main interface to the configuration file parser. Parses the
 *                configuration file and stores the results into the given
 *                Configuration object.
 *
 * Arguments:     stream         --  A pointer to a file from which the
 *                                   configuration should be read.  The file is
 *                                   assumed to be open for reading.
 *                configuration  --  A reference to a Configuration object in
 *                                   which the configuration should be stored.
 *
 * Returns:       The result of yyparse() on the file.
 *
 * ------------------------------------------------------------------------- */
{
  yyrestart(stream);
  parser_cfg = &configuration;
  config_file_line_number = 1;
  return yyparse();
}

