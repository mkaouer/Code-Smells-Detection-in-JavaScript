/* A Bison parser, made by GNU Bison 1.875c.  */

/* Skeleton parser for Yacc-like parsing with Bison,
   Copyright (C) 1984, 1989, 1990, 2000, 2001, 2002, 2003 Free Software Foundation, Inc.

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
     CFG_VERBOSITY = 270,
     CFG_STATESPACEOPTIONS = 271,
     CFG_EDGEPROBABILITY = 272,
     CFG_PROPOSITIONS = 273,
     CFG_SIZE = 274,
     CFG_TRUTHPROBABILITY = 275,
     CFG_CHANGEINTERVAL = 276,
     CFG_RANDOMSEED = 277,
     CFG_FORMULAOPTIONS = 278,
     CFG_ABBREVIATEDOPERATORS = 279,
     CFG_ANDPRIORITY = 280,
     CFG_BEFOREPRIORITY = 281,
     CFG_DEFAULTOPERATORPRIORITY = 282,
     CFG_EQUIVALENCEPRIORITY = 283,
     CFG_FALSEPRIORITY = 284,
     CFG_FINALLYPRIORITY = 285,
     CFG_GENERATEMODE = 286,
     CFG_GLOBALLYPRIORITY = 287,
     CFG_IMPLICATIONPRIORITY = 288,
     CFG_NEXTPRIORITY = 289,
     CFG_NOTPRIORITY = 290,
     CFG_ORPRIORITY = 291,
     CFG_OUTPUTMODE = 292,
     CFG_PROPOSITIONPRIORITY = 293,
     CFG_RELEASEPRIORITY = 294,
     CFG_STRONGRELEASEPRIORITY = 295,
     CFG_TRUEPRIORITY = 296,
     CFG_UNTILPRIORITY = 297,
     CFG_WEAKUNTILPRIORITY = 298,
     CFG_XORPRIORITY = 299,
     CFG_TRUTH_VALUE = 300,
     CFG_INTERACTIVITY_VALUE = 301,
     CFG_FORMULA_MODE_VALUE = 302,
     CFG_STATESPACE_MODE_VALUE = 303,
     CFG_PRODUCT_TYPE_VALUE = 304,
     CFG_INTEGER = 305,
     CFG_REAL = 306,
     CFG_INTEGER_INTERVAL = 307,
     CFG_STRING_CONSTANT = 308,
     CFG_LBRACE = 309,
     CFG_RBRACE = 310,
     CFG_EQUALS = 311,
     CFG_BLOCK_ID = 312,
     CFG_OPTION_ID = 313,
     CFG_UNKNOWN = 314
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
#define CFG_VERBOSITY 270
#define CFG_STATESPACEOPTIONS 271
#define CFG_EDGEPROBABILITY 272
#define CFG_PROPOSITIONS 273
#define CFG_SIZE 274
#define CFG_TRUTHPROBABILITY 275
#define CFG_CHANGEINTERVAL 276
#define CFG_RANDOMSEED 277
#define CFG_FORMULAOPTIONS 278
#define CFG_ABBREVIATEDOPERATORS 279
#define CFG_ANDPRIORITY 280
#define CFG_BEFOREPRIORITY 281
#define CFG_DEFAULTOPERATORPRIORITY 282
#define CFG_EQUIVALENCEPRIORITY 283
#define CFG_FALSEPRIORITY 284
#define CFG_FINALLYPRIORITY 285
#define CFG_GENERATEMODE 286
#define CFG_GLOBALLYPRIORITY 287
#define CFG_IMPLICATIONPRIORITY 288
#define CFG_NEXTPRIORITY 289
#define CFG_NOTPRIORITY 290
#define CFG_ORPRIORITY 291
#define CFG_OUTPUTMODE 292
#define CFG_PROPOSITIONPRIORITY 293
#define CFG_RELEASEPRIORITY 294
#define CFG_STRONGRELEASEPRIORITY 295
#define CFG_TRUEPRIORITY 296
#define CFG_UNTILPRIORITY 297
#define CFG_WEAKUNTILPRIORITY 298
#define CFG_XORPRIORITY 299
#define CFG_TRUTH_VALUE 300
#define CFG_INTERACTIVITY_VALUE 301
#define CFG_FORMULA_MODE_VALUE 302
#define CFG_STATESPACE_MODE_VALUE 303
#define CFG_PRODUCT_TYPE_VALUE 304
#define CFG_INTEGER 305
#define CFG_REAL 306
#define CFG_INTEGER_INTERVAL 307
#define CFG_STRING_CONSTANT 308
#define CFG_LBRACE 309
#define CFG_RBRACE 310
#define CFG_EQUALS 311
#define CFG_BLOCK_ID 312
#define CFG_OPTION_ID 313
#define CFG_UNKNOWN 314




/* Copy the first part of user declarations.  */
#line 20 "Config-parse.yy"


#include <config.h>
#include <cstdio>
#include <string>
#include "Configuration.h"
#include "StringUtil.h"



/******************************************************************************
 *
 * Variables for parsing the configuration file.
 *
 *****************************************************************************/

static Configuration::AlgorithmInformation          /* Stores all the      */
  algorithm_information;                            /* information in a
						     * single `Algorithm'
						     * block in the
						     * configuration file.
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

static int current_block_type;                      /* Type of the current
						     * configuration block.
						     */

static int current_option_type;                     /* Type of the current
						     * option name.
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

extern int getCharacter();                          /* Returns the next
                                                     * character in the lexer
                                                     * input stream.
                                                     */



/******************************************************************************
 *
 * Function for reporting parse errors.
 *
 *****************************************************************************/

/* ========================================================================= */
void yyerror(char* error_message)
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
  string unknown_token(yytext);
  int c;

  do
  {
    c = getCharacter();
    if (c != EOF && c != ' ' && c != '\t' && c != '\n')
      unknown_token += static_cast<char>(c);
  }
  while (c != EOF && c != ' ' && c != '\t' && c != '\n');

  string msg;

  switch (expected_token)
  {
    case CFG_BLOCK_ID :
      msg = string("unrecognized block identifier (`") + unknown_token + "')";
      break;
    case CFG_OPTION_ID :
      msg = string("unrecognized option identifier (`") + unknown_token + "')";
      break;
    case CFG_LBRACE :
      msg = "`{' expected after block identifier";
      break;
    case CFG_EQUALS :
      msg = "`=' expected after option identifier";
      break;
    case CFG_TRUTH_VALUE :
      msg = "truth value expected as option argument";
      break;
    case CFG_INTERACTIVITY_VALUE :
      msg = "interactivity mode expected as option argument";
      break;
    case CFG_FORMULA_MODE_VALUE :
      msg = "formula generation mode expected as option argument";
      break;
    case CFG_STATESPACE_MODE_VALUE :
      msg = "state space generation mode expected as option argument";
      break;
    case CFG_PRODUCT_TYPE_VALUE :
      msg = "model checking mode expected as option argument";
      break;
    case CFG_INTEGER :
      msg = "nonnegative integer expected as option argument";
      break;
    case CFG_INTEGER_INTERVAL :
      msg = "nonnegative integer or an integer interval expected as option"
	    " argument";
      break;
    case CFG_REAL :
      msg = "nonnegative real number expected as option argument";
      break;
    case CFG_STRING_CONSTANT :
      msg = "string constant expected as option argument";
      break;
    default :
      msg = error_message;
      break;
  }

  throw Configuration::ConfigurationException(config_file_line_number, msg);
}



/******************************************************************************
 *
 * Functions for performing various bound checks for the values of different
 * options in the configuration file.
 *
 *****************************************************************************/

/* ========================================================================= */
void checkIntegerRange
  (long int value, const struct Configuration::IntegerRange& range,
   bool show_line_number_if_error = true)
/* ----------------------------------------------------------------------------
 *
 * Description:   Checks that a value given to a configuration is within the
 *                acceptable range.
 *
 * Arguments:     value                      --  Integer value for a
 *                                               configuration option.
 *                range                      --  A reference to a constant
 *                                               struct
 *                                               Configuration::IntegerRange.
 *                show_line_number_if_error  --  If the value is not within the
 *                                               specified range, this
 *                                               parameter determines whether a
 *                                               configuration file line number
 *                                               is shown together with the
 *                                               error message.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (value < range.min || value > range.max)
    throw Configuration::ConfigurationException
            ((show_line_number_if_error ? config_file_line_number : -1),
	     range.error_message);
}

/* ========================================================================= */
void checkProbability(double value, bool show_line_number_if_error = true)
/* ----------------------------------------------------------------------------
 *
 * Description:   Checks whether a probability value specified in the program
 *                configuration is between 0.0 and 1.0 inclusive.
 *
 * Argument:      value                      --  A value supposed to denote a
 *                                               probability.
 *                show_line_number_if_error  --  If the value is not within the
 *                                               specified range, this
 *                                               parameter determines whether a
 *                                               configuration file line number
 *                                               is shown together with the
 *                                               error message.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (value < 0.0 || value > 1.0)
    throw Configuration::ConfigurationException
            ((show_line_number_if_error ? config_file_line_number : -1),
	     "probability must be between 0.0 and 1.0 inclusive");
}

/* ========================================================================= */
static inline bool isLocked(int option)
/* ----------------------------------------------------------------------------
 *
 * Description:   Checks whether the value of a configuration option can be
 *                initialized from the configuration file.  (This should not be
 *                done if the option was present on the program command line.)
 *
 * Argument:      option  --  The command line option.
 *
 * Returns:       `true' if the value of the option has been overridden in the
 *                command line.
 *
 * ------------------------------------------------------------------------- */
{
  return (parser_cfg->locked_options.find(make_pair(current_block_type,
                                                    option))
	    != parser_cfg->locked_options.end());
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
#line 292 "Config-parse.yy"
typedef union YYSTYPE {
  bool truth_value;

  long int integer;

  Configuration::InteractionMode interactivity_value;

  Configuration::FormulaMode formula_mode_value;

  Configuration::StateSpaceMode statespace_mode_value;

  Configuration::ProductMode product_type_value;

  double real;

  string *str;

  struct
  {
    long int min;
    long int max;
  } integer_interval;
} YYSTYPE;
/* Line 191 of yacc.c.  */
#line 479 "Config-parse.cc"
# define yystype YYSTYPE /* obsolescent; will be withdrawn */
# define YYSTYPE_IS_DECLARED 1
# define YYSTYPE_IS_TRIVIAL 1
#endif



/* Copy the second part of user declarations.  */


/* Line 214 of yacc.c.  */
#line 491 "Config-parse.cc"

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
#   define YYSTACK_ALLOC alloca
#  endif
# else
#  if defined (alloca) || defined (_ALLOCA_H)
#   define YYSTACK_ALLOC alloca
#  else
#   ifdef __GNUC__
#    define YYSTACK_ALLOC __builtin_alloca
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
  short yyss;
  YYSTYPE yyvs;
  };

/* The size of the maximum gap between one aligned stack and the next.  */
# define YYSTACK_GAP_MAXIMUM (sizeof (union yyalloc) - 1)

/* The size of an array large to enough to hold all stacks, each with
   N elements.  */
# define YYSTACK_BYTES(N) \
     ((N) * (sizeof (short) + sizeof (YYSTYPE))				\
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
   typedef short yysigned_char;
#endif

/* YYFINAL -- State number of the termination state. */
#define YYFINAL  3
/* YYLAST -- Last index in YYTABLE.  */
#define YYLAST   158

/* YYNTOKENS -- Number of terminals. */
#define YYNTOKENS  60
/* YYNNTS -- Number of nonterminals. */
#define YYNNTS  113
/* YYNRULES -- Number of rules. */
#define YYNRULES  162
/* YYNRULES -- Number of states. */
#define YYNSTATES  257

/* YYTRANSLATE(YYLEX) -- Bison symbol number corresponding to YYLEX.  */
#define YYUNDEFTOK  2
#define YYMAXUTOK   314

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
      45,    46,    47,    48,    49,    50,    51,    52,    53,    54,
      55,    56,    57,    58,    59
};

#if YYDEBUG
/* YYPRHS[YYN] -- Index of the first RHS symbol of rule number YYN in
   YYRHS.  */
static const unsigned short yyprhs[] =
{
       0,     0,     3,     5,     6,     7,    11,    13,    15,    17,
      19,    20,    26,    27,    28,    32,    33,    34,    40,    41,
      42,    48,    49,    50,    56,    57,    58,    64,    65,    71,
      72,    73,    77,    78,    79,    85,    86,    87,    93,    94,
      95,   101,   102,   103,   109,   110,   111,   117,   118,   119,
     125,   126,   127,   133,   134,   140,   141,   142,   146,   147,
     148,   154,   155,   156,   162,   163,   164,   170,   171,   172,
     178,   179,   180,   186,   187,   188,   194,   195,   196,   202,
     203,   209,   210,   211,   215,   216,   217,   223,   224,   225,
     231,   232,   233,   239,   240,   241,   247,   248,   249,   255,
     256,   257,   263,   264,   265,   271,   272,   273,   279,   280,
     281,   287,   288,   289,   295,   296,   297,   303,   304,   305,
     311,   312,   313,   319,   320,   321,   327,   328,   329,   335,
     336,   337,   343,   344,   345,   351,   352,   353,   359,   360,
     361,   367,   368,   369,   375,   376,   377,   383,   384,   385,
     391,   392,   393,   399,   400,   401,   407,   408,   409,   415,
     417,   419,   421
};

/* YYRHS -- A `-1'-separated list of the rules' RHS. */
static const short yyrhs[] =
{
      61,     0,    -1,    62,    -1,    -1,    -1,    62,    63,    64,
      -1,    65,    -1,    78,    -1,    97,    -1,   116,    -1,    -1,
       3,    66,    54,    67,    55,    -1,    -1,    -1,    67,    68,
      69,    -1,    -1,    -1,     4,    70,    56,    71,    45,    -1,
      -1,    -1,     5,    72,    56,    73,    53,    -1,    -1,    -1,
       6,    74,    56,    75,    53,    -1,    -1,    -1,     7,    76,
      56,    77,    53,    -1,    -1,     8,    79,    54,    80,    55,
      -1,    -1,    -1,    80,    81,    82,    -1,    -1,    -1,     9,
      83,    56,    84,    45,    -1,    -1,    -1,    10,    85,    56,
      86,    45,    -1,    -1,    -1,    11,    87,    56,    88,    46,
      -1,    -1,    -1,    12,    89,    56,    90,    45,    -1,    -1,
      -1,    13,    91,    56,    92,    49,    -1,    -1,    -1,    14,
      93,    56,    94,    50,    -1,    -1,    -1,    15,    95,    56,
      96,    50,    -1,    -1,    16,    98,    54,    99,    55,    -1,
      -1,    -1,    99,   100,   101,    -1,    -1,    -1,    21,   102,
      56,   103,    50,    -1,    -1,    -1,    17,   104,    56,   105,
      51,    -1,    -1,    -1,    31,   106,    56,   107,    48,    -1,
      -1,    -1,    18,   108,    56,   109,    50,    -1,    -1,    -1,
      22,   110,    56,   111,    50,    -1,    -1,    -1,    19,   112,
      56,   113,   172,    -1,    -1,    -1,    20,   114,    56,   115,
      51,    -1,    -1,    23,   117,    54,   118,    55,    -1,    -1,
      -1,   118,   119,   120,    -1,    -1,    -1,    24,   121,    56,
     122,    45,    -1,    -1,    -1,    25,   123,    56,   124,    50,
      -1,    -1,    -1,    26,   125,    56,   126,    50,    -1,    -1,
      -1,    21,   127,    56,   128,    50,    -1,    -1,    -1,    27,
     129,    56,   130,    50,    -1,    -1,    -1,    28,   131,    56,
     132,    50,    -1,    -1,    -1,    29,   133,    56,   134,    50,
      -1,    -1,    -1,    30,   135,    56,   136,    50,    -1,    -1,
      -1,    31,   137,    56,   138,    47,    -1,    -1,    -1,    32,
     139,    56,   140,    50,    -1,    -1,    -1,    33,   141,    56,
     142,    50,    -1,    -1,    -1,    34,   143,    56,   144,    50,
      -1,    -1,    -1,    35,   145,    56,   146,    50,    -1,    -1,
      -1,    36,   147,    56,   148,    50,    -1,    -1,    -1,    37,
     149,    56,   150,    47,    -1,    -1,    -1,    38,   151,    56,
     152,    50,    -1,    -1,    -1,    18,   153,    56,   154,    50,
      -1,    -1,    -1,    22,   155,    56,   156,    50,    -1,    -1,
      -1,    39,   157,    56,   158,    50,    -1,    -1,    -1,    19,
     159,    56,   160,   171,    -1,    -1,    -1,    40,   161,    56,
     162,    50,    -1,    -1,    -1,    41,   163,    56,   164,    50,
      -1,    -1,    -1,    42,   165,    56,   166,    50,    -1,    -1,
      -1,    43,   167,    56,   168,    50,    -1,    -1,    -1,    44,
     169,    56,   170,    50,    -1,    50,    -1,    52,    -1,    50,
      -1,    52,    -1
};

/* YYRLINE[YYN] -- source line where rule number YYN was defined.  */
static const unsigned short yyrline[] =
{
       0,   387,   387,   390,   392,   391,   398,   399,   400,   401,
     405,   404,   444,   446,   445,   453,   458,   452,   467,   472,
     466,   481,   486,   480,   495,   500,   494,   510,   509,   517,
     519,   518,   526,   531,   525,   541,   546,   540,   556,   561,
     555,   571,   576,   570,   586,   591,   585,   601,   606,   600,
     622,   627,   621,   642,   641,   649,   651,   650,   658,   663,
     657,   678,   683,   677,   697,   702,   696,   713,   718,   712,
     734,   739,   733,   750,   755,   749,   761,   766,   760,   781,
     780,   788,   790,   789,   797,   802,   796,   813,   818,   812,
     833,   838,   832,   853,   858,   852,   873,   879,   872,   894,
     899,   893,   914,   919,   913,   934,   939,   933,   954,   959,
     953,   970,   975,   969,   990,   995,   989,  1010,  1015,  1009,
    1030,  1035,  1029,  1050,  1055,  1049,  1070,  1075,  1069,  1085,
    1090,  1084,  1105,  1110,  1104,  1127,  1132,  1126,  1143,  1148,
    1142,  1163,  1168,  1162,  1174,  1179,  1173,  1194,  1199,  1193,
    1214,  1219,  1213,  1234,  1239,  1233,  1254,  1259,  1253,  1274,
    1288,  1312,  1326
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
  "CFG_INTERSECTIONTEST", "CFG_MODELCHECK", "CFG_ROUNDS", "CFG_VERBOSITY",
  "CFG_STATESPACEOPTIONS", "CFG_EDGEPROBABILITY", "CFG_PROPOSITIONS",
  "CFG_SIZE", "CFG_TRUTHPROBABILITY", "CFG_CHANGEINTERVAL",
  "CFG_RANDOMSEED", "CFG_FORMULAOPTIONS", "CFG_ABBREVIATEDOPERATORS",
  "CFG_ANDPRIORITY", "CFG_BEFOREPRIORITY", "CFG_DEFAULTOPERATORPRIORITY",
  "CFG_EQUIVALENCEPRIORITY", "CFG_FALSEPRIORITY", "CFG_FINALLYPRIORITY",
  "CFG_GENERATEMODE", "CFG_GLOBALLYPRIORITY", "CFG_IMPLICATIONPRIORITY",
  "CFG_NEXTPRIORITY", "CFG_NOTPRIORITY", "CFG_ORPRIORITY",
  "CFG_OUTPUTMODE", "CFG_PROPOSITIONPRIORITY", "CFG_RELEASEPRIORITY",
  "CFG_STRONGRELEASEPRIORITY", "CFG_TRUEPRIORITY", "CFG_UNTILPRIORITY",
  "CFG_WEAKUNTILPRIORITY", "CFG_XORPRIORITY", "CFG_TRUTH_VALUE",
  "CFG_INTERACTIVITY_VALUE", "CFG_FORMULA_MODE_VALUE",
  "CFG_STATESPACE_MODE_VALUE", "CFG_PRODUCT_TYPE_VALUE", "CFG_INTEGER",
  "CFG_REAL", "CFG_INTEGER_INTERVAL", "CFG_STRING_CONSTANT", "CFG_LBRACE",
  "CFG_RBRACE", "CFG_EQUALS", "CFG_BLOCK_ID", "CFG_OPTION_ID",
  "CFG_UNKNOWN", "$accept", "configuration_file", "configuration_blocks",
  "@1", "configuration_block", "algorithm_option_block", "@2",
  "algorithm_options", "@3", "algorithm_option", "@4", "@5", "@6", "@7",
  "@8", "@9", "@10", "@11", "global_option_block", "@12", "global_options",
  "@13", "global_option", "@14", "@15", "@16", "@17", "@18", "@19", "@20",
  "@21", "@22", "@23", "@24", "@25", "@26", "@27",
  "statespace_option_block", "@28", "statespace_options", "@29",
  "statespace_option", "@30", "@31", "@32", "@33", "@34", "@35", "@36",
  "@37", "@38", "@39", "@40", "@41", "@42", "@43", "formula_option_block",
  "@44", "formula_options", "@45", "formula_option", "@46", "@47", "@48",
  "@49", "@50", "@51", "@52", "@53", "@54", "@55", "@56", "@57", "@58",
  "@59", "@60", "@61", "@62", "@63", "@64", "@65", "@66", "@67", "@68",
  "@69", "@70", "@71", "@72", "@73", "@74", "@75", "@76", "@77", "@78",
  "@79", "@80", "@81", "@82", "@83", "@84", "@85", "@86", "@87", "@88",
  "@89", "@90", "@91", "@92", "@93", "@94", "@95", "formula_size",
  "statespace_size", 0
};
#endif

# ifdef YYPRINT
/* YYTOKNUM[YYLEX-NUM] -- Internal token number corresponding to
   token YYLEX-NUM.  */
static const unsigned short yytoknum[] =
{
       0,   256,   257,   258,   259,   260,   261,   262,   263,   264,
     265,   266,   267,   268,   269,   270,   271,   272,   273,   274,
     275,   276,   277,   278,   279,   280,   281,   282,   283,   284,
     285,   286,   287,   288,   289,   290,   291,   292,   293,   294,
     295,   296,   297,   298,   299,   300,   301,   302,   303,   304,
     305,   306,   307,   308,   309,   310,   311,   312,   313,   314
};
# endif

/* YYR1[YYN] -- Symbol number of symbol that rule YYN derives.  */
static const unsigned char yyr1[] =
{
       0,    60,    61,    62,    63,    62,    64,    64,    64,    64,
      66,    65,    67,    68,    67,    70,    71,    69,    72,    73,
      69,    74,    75,    69,    76,    77,    69,    79,    78,    80,
      81,    80,    83,    84,    82,    85,    86,    82,    87,    88,
      82,    89,    90,    82,    91,    92,    82,    93,    94,    82,
      95,    96,    82,    98,    97,    99,   100,    99,   102,   103,
     101,   104,   105,   101,   106,   107,   101,   108,   109,   101,
     110,   111,   101,   112,   113,   101,   114,   115,   101,   117,
     116,   118,   119,   118,   121,   122,   120,   123,   124,   120,
     125,   126,   120,   127,   128,   120,   129,   130,   120,   131,
     132,   120,   133,   134,   120,   135,   136,   120,   137,   138,
     120,   139,   140,   120,   141,   142,   120,   143,   144,   120,
     145,   146,   120,   147,   148,   120,   149,   150,   120,   151,
     152,   120,   153,   154,   120,   155,   156,   120,   157,   158,
     120,   159,   160,   120,   161,   162,   120,   163,   164,   120,
     165,   166,   120,   167,   168,   120,   169,   170,   120,   171,
     171,   172,   172
};

/* YYR2[YYN] -- Number of symbols composing right hand side of rule YYN.  */
static const unsigned char yyr2[] =
{
       0,     2,     1,     0,     0,     3,     1,     1,     1,     1,
       0,     5,     0,     0,     3,     0,     0,     5,     0,     0,
       5,     0,     0,     5,     0,     0,     5,     0,     5,     0,
       0,     3,     0,     0,     5,     0,     0,     5,     0,     0,
       5,     0,     0,     5,     0,     0,     5,     0,     0,     5,
       0,     0,     5,     0,     5,     0,     0,     3,     0,     0,
       5,     0,     0,     5,     0,     0,     5,     0,     0,     5,
       0,     0,     5,     0,     0,     5,     0,     0,     5,     0,
       5,     0,     0,     3,     0,     0,     5,     0,     0,     5,
       0,     0,     5,     0,     0,     5,     0,     0,     5,     0,
       0,     5,     0,     0,     5,     0,     0,     5,     0,     0,
       5,     0,     0,     5,     0,     0,     5,     0,     0,     5,
       0,     0,     5,     0,     0,     5,     0,     0,     5,     0,
       0,     5,     0,     0,     5,     0,     0,     5,     0,     0,
       5,     0,     0,     5,     0,     0,     5,     0,     0,     5,
       0,     0,     5,     0,     0,     5,     0,     0,     5,     1,
       1,     1,     1
};

/* YYDEFACT[STATE-NAME] -- Default rule to reduce with in state
   STATE-NUM when YYTABLE doesn't specify something else to do.  Zero
   means the default is an error.  */
static const unsigned char yydefact[] =
{
       3,     0,     4,     1,     0,    10,    27,    53,    79,     5,
       6,     7,     8,     9,     0,     0,     0,     0,    12,    29,
      55,    81,    13,    30,    56,    82,    11,     0,    28,     0,
      54,     0,    80,     0,    15,    18,    21,    24,    14,    32,
      35,    38,    41,    44,    47,    50,    31,    61,    67,    73,
      76,    58,    70,    64,    57,   132,   141,    93,   135,    84,
      87,    90,    96,    99,   102,   105,   108,   111,   114,   117,
     120,   123,   126,   129,   138,   144,   147,   150,   153,   156,
      83,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,    16,    19,    22,    25,    33,    36,
      39,    42,    45,    48,    51,    62,    68,    74,    77,    59,
      71,    65,   133,   142,    94,   136,    85,    88,    91,    97,
     100,   103,   106,   109,   112,   115,   118,   121,   124,   127,
     130,   139,   145,   148,   151,   154,   157,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
       0,     0,     0,     0,     0,     0,     0,     0,     0,     0,
      17,    20,    23,    26,    34,    37,    40,    43,    46,    49,
      52,    63,    69,   161,   162,    75,    78,    60,    72,    66,
     134,   159,   160,   143,    95,   137,    86,    89,    92,    98,
     101,   104,   107,   110,   113,   116,   119,   122,   125,   128,
     131,   140,   146,   149,   152,   155,   158
};

/* YYDEFGOTO[NTERM-NUM]. */
static const short yydefgoto[] =
{
      -1,     1,     2,     4,     9,    10,    14,    22,    27,    38,
      81,   167,    82,   168,    83,   169,    84,   170,    11,    15,
      23,    29,    46,    85,   171,    86,   172,    87,   173,    88,
     174,    89,   175,    90,   176,    91,   177,    12,    16,    24,
      31,    54,    96,   182,    92,   178,    98,   184,    93,   179,
      97,   183,    94,   180,    95,   181,    13,    17,    25,    33,
      80,   103,   189,   104,   190,   105,   191,   101,   187,   106,
     192,   107,   193,   108,   194,   109,   195,   110,   196,   111,
     197,   112,   198,   113,   199,   114,   200,   115,   201,   116,
     202,   117,   203,    99,   185,   102,   188,   118,   204,   100,
     186,   119,   205,   120,   206,   121,   207,   122,   208,   123,
     209,   233,   225
};

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
#define YYPACT_NINF -24
static const yysigned_char yypact[] =
{
     -24,     2,     5,   -24,    24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,    -9,    -8,     3,     4,   -24,   -24,
     -24,   -24,     1,     6,     7,     8,   -24,    37,   -24,    40,
     -24,    17,   -24,   -18,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -23,     9,    10,    11,    12,    13,    14,    15,    16,
      18,    19,    20,    21,    22,    23,    25,    26,    27,    28,
      29,    30,    31,    32,    33,    34,    35,    36,    38,    39,
      41,    42,    43,    44,    45,    46,    47,    48,    49,    50,
      51,    52,    53,    54,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,    66,    59,    60,
      61,    70,    71,    72,    74,    68,    73,    75,    69,    76,
     -22,    77,    79,    80,    83,    82,   -21,    84,    85,    88,
      86,    87,    89,    90,    91,    92,    96,    94,    95,    97,
      98,    99,   103,   101,   102,   104,   105,   106,   107,   108,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24
};

/* YYPGOTO[NTERM-NUM].  */
static const yysigned_char yypgoto[] =
{
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,   -24,
     -24,   -24,   -24
};

/* YYTABLE[YYPACT[STATE-NUM]].  What to do in state STATE-NUM.  If
   positive, shift that token.  If negative, reduce the rule which
   number is the opposite.  If zero, do what YYDEFACT says.
   If YYTABLE_NINF, syntax error.  */
#define YYTABLE_NINF -3
static const short yytable[] =
{
      55,    56,     3,    57,    58,    -2,    59,    60,    61,    62,
      63,    64,    65,    66,    67,    68,    69,    70,    71,    72,
      73,    74,    75,    76,    77,    78,    79,     5,   223,   231,
     224,   232,     6,   124,    47,    48,    49,    50,    51,    52,
       7,    34,    35,    36,    37,    18,    19,     8,    53,    39,
      40,    41,    42,    43,    44,    45,    26,    20,    21,     0,
       0,    28,    30,    32,     0,   125,   126,   127,   128,   129,
     130,   131,   132,     0,   133,   134,   135,   136,   137,   138,
       0,   139,   140,   141,   142,   143,   144,   145,   146,   147,
     148,   149,   150,     0,   151,   152,     0,   153,   154,   155,
     156,   157,   158,   159,   160,   161,   162,   163,   164,   165,
     166,   210,   211,   212,   213,   214,   215,   218,   216,   217,
     221,     0,     0,   219,     0,   220,   222,     0,   226,   227,
     228,   229,   230,   236,   234,   235,   237,   238,     0,   239,
     240,   241,   242,   243,   244,   245,     0,   246,   247,   248,
     249,   250,   251,     0,   252,   253,   254,   255,   256
};

static const yysigned_char yycheck[] =
{
      18,    19,     0,    21,    22,     0,    24,    25,    26,    27,
      28,    29,    30,    31,    32,    33,    34,    35,    36,    37,
      38,    39,    40,    41,    42,    43,    44,     3,    50,    50,
      52,    52,     8,    56,    17,    18,    19,    20,    21,    22,
      16,     4,     5,     6,     7,    54,    54,    23,    31,     9,
      10,    11,    12,    13,    14,    15,    55,    54,    54,    -1,
      -1,    55,    55,    55,    -1,    56,    56,    56,    56,    56,
      56,    56,    56,    -1,    56,    56,    56,    56,    56,    56,
      -1,    56,    56,    56,    56,    56,    56,    56,    56,    56,
      56,    56,    56,    -1,    56,    56,    -1,    56,    56,    56,
      56,    56,    56,    56,    56,    56,    56,    56,    56,    56,
      56,    45,    53,    53,    53,    45,    45,    49,    46,    45,
      51,    -1,    -1,    50,    -1,    50,    50,    -1,    51,    50,
      50,    48,    50,    45,    50,    50,    50,    50,    -1,    50,
      50,    50,    50,    47,    50,    50,    -1,    50,    50,    50,
      47,    50,    50,    -1,    50,    50,    50,    50,    50
};

/* YYSTOS[STATE-NUM] -- The (internal number of the) accessing
   symbol of state STATE-NUM.  */
static const unsigned char yystos[] =
{
       0,    61,    62,     0,    63,     3,     8,    16,    23,    64,
      65,    78,    97,   116,    66,    79,    98,   117,    54,    54,
      54,    54,    67,    80,    99,   118,    55,    68,    55,    81,
      55,   100,    55,   119,     4,     5,     6,     7,    69,     9,
      10,    11,    12,    13,    14,    15,    82,    17,    18,    19,
      20,    21,    22,    31,   101,    18,    19,    21,    22,    24,
      25,    26,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    39,    40,    41,    42,    43,    44,
     120,    70,    72,    74,    76,    83,    85,    87,    89,    91,
      93,    95,   104,   108,   112,   114,   102,   110,   106,   153,
     159,   127,   155,   121,   123,   125,   129,   131,   133,   135,
     137,   139,   141,   143,   145,   147,   149,   151,   157,   161,
     163,   165,   167,   169,    56,    56,    56,    56,    56,    56,
      56,    56,    56,    56,    56,    56,    56,    56,    56,    56,
      56,    56,    56,    56,    56,    56,    56,    56,    56,    56,
      56,    56,    56,    56,    56,    56,    56,    56,    56,    56,
      56,    56,    56,    56,    56,    56,    56,    71,    73,    75,
      77,    84,    86,    88,    90,    92,    94,    96,   105,   109,
     113,   115,   103,   111,   107,   154,   160,   128,   156,   122,
     124,   126,   130,   132,   134,   136,   138,   140,   142,   144,
     146,   148,   150,   152,   158,   162,   164,   166,   168,   170,
      45,    53,    53,    53,    45,    45,    46,    45,    49,    50,
      50,    51,    50,    50,    52,   172,    51,    50,    50,    48,
      50,    50,    52,   171,    50,    50,    45,    50,    50,    50,
      50,    50,    50,    47,    50,    50,    50,    50,    50,    47,
      50,    50,    50,    50,    50,    50,    50
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

/* YYLLOC_DEFAULT -- Compute the default location (before the actions
   are run).  */

#ifndef YYLLOC_DEFAULT
# define YYLLOC_DEFAULT(Current, Rhs, N)		\
   ((Current).first_line   = (Rhs)[1].first_line,	\
    (Current).first_column = (Rhs)[1].first_column,	\
    (Current).last_line    = (Rhs)[N].last_line,	\
    (Current).last_column  = (Rhs)[N].last_column)
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

# define YYDSYMPRINT(Args)			\
do {						\
  if (yydebug)					\
    yysymprint Args;				\
} while (0)

# define YYDSYMPRINTF(Title, Token, Value, Location)		\
do {								\
  if (yydebug)							\
    {								\
      YYFPRINTF (stderr, "%s ", Title);				\
      yysymprint (stderr, 					\
                  Token, Value);	\
      YYFPRINTF (stderr, "\n");					\
    }								\
} while (0)

/*------------------------------------------------------------------.
| yy_stack_print -- Print the state stack from its BOTTOM up to its |
| TOP (included).                                                   |
`------------------------------------------------------------------*/

#if defined (__STDC__) || defined (__cplusplus)
static void
yy_stack_print (short *bottom, short *top)
#else
static void
yy_stack_print (bottom, top)
    short *bottom;
    short *top;
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
# define YYDSYMPRINT(Args)
# define YYDSYMPRINTF(Title, Token, Value, Location)
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

#if defined (YYMAXDEPTH) && YYMAXDEPTH == 0
# undef YYMAXDEPTH
#endif

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
    {
      YYFPRINTF (yyoutput, "token %s (", yytname[yytype]);
# ifdef YYPRINT
      YYPRINT (yyoutput, yytoknum[yytype], *yyvaluep);
# endif
    }
  else
    YYFPRINTF (yyoutput, "nterm %s (", yytname[yytype]);

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
yydestruct (int yytype, YYSTYPE *yyvaluep)
#else
static void
yydestruct (yytype, yyvaluep)
    int yytype;
    YYSTYPE *yyvaluep;
#endif
{
  /* Pacify ``unused variable'' warnings.  */
  (void) yyvaluep;

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



/* The lookahead symbol.  */
int yychar;

/* The semantic value of the lookahead symbol.  */
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
  /* Lookahead token as an internal (translated) token number.  */
  int yytoken = 0;

  /* Three stacks and their tools:
     `yyss': related to states,
     `yyvs': related to semantic values,
     `yyls': related to locations.

     Refer to the stacks thru separate pointers, to allow yyoverflow
     to reallocate them elsewhere.  */

  /* The state stack.  */
  short	yyssa[YYINITDEPTH];
  short *yyss = yyssa;
  register short *yyssp;

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
	short *yyss1 = yyss;


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
	short *yyss1 = yyss;
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
/* Read a lookahead token if we need one and don't already have one.  */
/* yyresume: */

  /* First try to decide what to do without reference to lookahead token.  */

  yyn = yypact[yystate];
  if (yyn == YYPACT_NINF)
    goto yydefault;

  /* Not known => get a lookahead token if don't already have one.  */

  /* YYCHAR is either YYEMPTY or YYEOF or a valid lookahead symbol.  */
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
      YYDSYMPRINTF ("Next token is", yytoken, &yylval, &yylloc);
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

  /* Shift the lookahead token.  */
  YYDPRINTF ((stderr, "Shifting token %s, ", yytname[yytoken]));

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
        case 4:
#line 392 "Config-parse.yy"
    {
                               expected_token = CFG_BLOCK_ID;
                             }
    break;

  case 10:
#line 405 "Config-parse.yy"
    {
			       current_block_type = CFG_ALGORITHM;

                               algorithm_begin_line = config_file_line_number;

                               algorithm_information.name = 0;
                               algorithm_information.path_to_program = 0;
                               algorithm_information.extra_parameters = 0;
			       algorithm_information.enabled = true;

                               expected_token = CFG_LBRACE;
                             }
    break;

  case 11:
#line 418 "Config-parse.yy"
    {
                               if (algorithm_information.name == 0)
                               {
                                 algorithm_information.name =
                                   new string("Algorithm ");
				 algorithm_information.name->append
				   (::StringUtil::toString
				    (parser_cfg->algorithms.size()));
                               }
                               parser_cfg->algorithms.push_back
                                 (algorithm_information);
                               if (algorithm_information.path_to_program == 0)
                               {
				 throw Configuration::ConfigurationException
				         (::StringUtil::toString
					    (algorithm_begin_line)
					    + "--"
					    + ::StringUtil::toString
					        (config_file_line_number),
					  "missing path to executable (`"
					  + *(algorithm_information.name)
                                          + "')");
                               }
                             }
    break;

  case 13:
#line 446 "Config-parse.yy"
    {
                               expected_token = CFG_OPTION_ID;
                             }
    break;

  case 15:
#line 453 "Config-parse.yy"
    {
			       current_option_type = CFG_ENABLED;
			       expected_token = CFG_EQUALS;
			     }
    break;

  case 16:
#line 458 "Config-parse.yy"
    {
			       expected_token = CFG_TRUTH_VALUE;
			     }
    break;

  case 17:
#line 462 "Config-parse.yy"
    {
			       algorithm_information.enabled = yyvsp[0].truth_value;
			     }
    break;

  case 18:
#line 467 "Config-parse.yy"
    {
			       current_option_type = CFG_NAME;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 19:
#line 472 "Config-parse.yy"
    {
                               expected_token = CFG_STRING_CONSTANT;
                             }
    break;

  case 20:
#line 476 "Config-parse.yy"
    {
                               algorithm_information.name = yyvsp[0].str;
                             }
    break;

  case 21:
#line 481 "Config-parse.yy"
    {
			       current_option_type = CFG_PARAMETERS;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 22:
#line 486 "Config-parse.yy"
    {
                               expected_token = CFG_STRING_CONSTANT;
                             }
    break;

  case 23:
#line 490 "Config-parse.yy"
    {
                               algorithm_information.extra_parameters = yyvsp[0].str;
                             }
    break;

  case 24:
#line 495 "Config-parse.yy"
    {
			       current_option_type = CFG_PROGRAMPATH;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 25:
#line 500 "Config-parse.yy"
    {
                               expected_token = CFG_STRING_CONSTANT;
                             }
    break;

  case 26:
#line 504 "Config-parse.yy"
    {
                               algorithm_information.path_to_program = yyvsp[0].str;
                             }
    break;

  case 27:
#line 510 "Config-parse.yy"
    {
			       current_block_type = CFG_GLOBALOPTIONS;
                               expected_token = CFG_LBRACE;
                             }
    break;

  case 30:
#line 519 "Config-parse.yy"
    {
                               expected_token = CFG_OPTION_ID;
                             }
    break;

  case 32:
#line 526 "Config-parse.yy"
    {
			       current_option_type = CFG_COMPARISONTEST;
			       expected_token = CFG_EQUALS;
			     }
    break;

  case 33:
#line 531 "Config-parse.yy"
    {
			       expected_token = CFG_TRUTH_VALUE;
			     }
    break;

  case 34:
#line 535 "Config-parse.yy"
    {
			       if (!isLocked(CFG_COMPARISONTEST))
				 parser_cfg->global_options.do_comp_test = yyvsp[0].truth_value;
			     }
    break;

  case 35:
#line 541 "Config-parse.yy"
    {
			       current_option_type = CFG_CONSISTENCYTEST;
			       expected_token = CFG_EQUALS;
			     }
    break;

  case 36:
#line 546 "Config-parse.yy"
    {
			       expected_token = CFG_TRUTH_VALUE;
			     }
    break;

  case 37:
#line 550 "Config-parse.yy"
    {
			       if (!isLocked(CFG_CONSISTENCYTEST))
				 parser_cfg->global_options.do_cons_test = yyvsp[0].truth_value;
			     }
    break;

  case 38:
#line 556 "Config-parse.yy"
    {
			       current_option_type = CFG_INTERACTIVE;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 39:
#line 561 "Config-parse.yy"
    {
                               expected_token = CFG_INTERACTIVITY_VALUE;
                             }
    break;

  case 40:
#line 565 "Config-parse.yy"
    {
			       if (!isLocked(CFG_INTERACTIVE))
				 parser_cfg->global_options.interactive = yyvsp[0].interactivity_value;
                             }
    break;

  case 41:
#line 571 "Config-parse.yy"
    {
			       current_option_type = CFG_INTERSECTIONTEST;
			       expected_token = CFG_EQUALS;
			     }
    break;

  case 42:
#line 576 "Config-parse.yy"
    {
			       expected_token = CFG_TRUTH_VALUE;
			     }
    break;

  case 43:
#line 580 "Config-parse.yy"
    {
			       if (!isLocked(CFG_INTERSECTIONTEST))
				 parser_cfg->global_options.do_intr_test = yyvsp[0].truth_value;
			     }
    break;

  case 44:
#line 586 "Config-parse.yy"
    {
			       current_option_type = CFG_MODELCHECK;
			       expected_token = CFG_EQUALS;
			     }
    break;

  case 45:
#line 591 "Config-parse.yy"
    {
			       expected_token = CFG_PRODUCT_TYPE_VALUE;
			     }
    break;

  case 46:
#line 595 "Config-parse.yy"
    {
			       if (!isLocked(CFG_MODELCHECK))
				 parser_cfg->global_options.product_mode = yyvsp[0].product_type_value;
			     }
    break;

  case 47:
#line 601 "Config-parse.yy"
    {
			       current_option_type = CFG_ROUNDS;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 48:
#line 606 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 49:
#line 610 "Config-parse.yy"
    {
			       if (!isLocked(CFG_ROUNDS))
			       {
				 checkIntegerRange
	                           (yyvsp[0].integer, Configuration::ROUND_COUNT_RANGE,
                                    true);
				 parser_cfg->global_options.number_of_rounds
				   = yyvsp[0].integer;
			       }
                             }
    break;

  case 50:
#line 622 "Config-parse.yy"
    {
			       current_option_type = CFG_VERBOSITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 51:
#line 627 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 52:
#line 631 "Config-parse.yy"
    {
			       if (!isLocked(CFG_VERBOSITY))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::VERBOSITY_RANGE, true);
				 parser_cfg->global_options.verbosity = yyvsp[0].integer;
			       }
                             }
    break;

  case 53:
#line 642 "Config-parse.yy"
    {
			       current_block_type = CFG_STATESPACEOPTIONS;
                               expected_token = CFG_LBRACE;
                             }
    break;

  case 56:
#line 651 "Config-parse.yy"
    {
                               expected_token = CFG_OPTION_ID;
                             }
    break;

  case 58:
#line 658 "Config-parse.yy"
    {
			       current_option_type = CFG_CHANGEINTERVAL;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 59:
#line 663 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 60:
#line 667 "Config-parse.yy"
    {
			       if (!isLocked(CFG_CHANGEINTERVAL))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::GENERATION_RANGE, true);
				 parser_cfg->global_options.
				   statespace_change_interval = yyvsp[0].integer;
			       }
                             }
    break;

  case 61:
#line 678 "Config-parse.yy"
    {
			       current_option_type = CFG_EDGEPROBABILITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 62:
#line 683 "Config-parse.yy"
    {
                               expected_token = CFG_REAL;
                             }
    break;

  case 63:
#line 687 "Config-parse.yy"
    {
			       if (!isLocked(CFG_EDGEPROBABILITY))
			       {
				 checkProbability(yyvsp[0].real);
				 parser_cfg->statespace_generator.
	                           edge_probability = yyvsp[0].real;
			       }
                             }
    break;

  case 64:
#line 697 "Config-parse.yy"
    {
			       current_option_type = CFG_GENERATEMODE;
			       expected_token = CFG_EQUALS;
			     }
    break;

  case 65:
#line 702 "Config-parse.yy"
    {
			       expected_token = CFG_STATESPACE_MODE_VALUE;
			     }
    break;

  case 66:
#line 706 "Config-parse.yy"
    {
			       if (!isLocked(CFG_GENERATEMODE))
				 parser_cfg->global_options.
	                           statespace_generation_mode = yyvsp[0].statespace_mode_value;
			     }
    break;

  case 67:
#line 713 "Config-parse.yy"
    {
			       current_option_type = CFG_PROPOSITIONS;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 68:
#line 718 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 69:
#line 722 "Config-parse.yy"
    {
			       if (!isLocked(CFG_PROPOSITIONS))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::PROPOSITION_COUNT_RANGE,
                                    true);
				 parser_cfg->statespace_generator.
	                           atoms_per_state = yyvsp[0].integer;
			       }
                             }
    break;

  case 70:
#line 734 "Config-parse.yy"
    {
			       current_option_type = CFG_RANDOMSEED;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 71:
#line 739 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 72:
#line 743 "Config-parse.yy"
    {
			       if (!isLocked(CFG_RANDOMSEED))
				 parser_cfg->global_options.
                                   statespace_random_seed = yyvsp[0].integer;
                             }
    break;

  case 73:
#line 750 "Config-parse.yy"
    {
			       current_option_type = CFG_SIZE;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 74:
#line 755 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER_INTERVAL;
                             }
    break;

  case 76:
#line 761 "Config-parse.yy"
    {
			       current_option_type = CFG_TRUTHPROBABILITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 77:
#line 766 "Config-parse.yy"
    {
                               expected_token = CFG_REAL;
                             }
    break;

  case 78:
#line 770 "Config-parse.yy"
    {
			       if (!isLocked(CFG_TRUTHPROBABILITY))
			       {
				 checkProbability(yyvsp[0].real);
				 parser_cfg->statespace_generator.
                                   truth_probability = yyvsp[0].real;
			       }
                             }
    break;

  case 79:
#line 781 "Config-parse.yy"
    {
			       current_block_type = CFG_FORMULAOPTIONS;
                               expected_token = CFG_LBRACE;
                             }
    break;

  case 82:
#line 790 "Config-parse.yy"
    {
                               expected_token = CFG_OPTION_ID;
                             }
    break;

  case 84:
#line 797 "Config-parse.yy"
    {
			       current_option_type = CFG_ABBREVIATEDOPERATORS;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 85:
#line 802 "Config-parse.yy"
    {
                               expected_token = CFG_TRUTH_VALUE;
                             }
    break;

  case 86:
#line 806 "Config-parse.yy"
    {
			       if (!isLocked(CFG_ABBREVIATEDOPERATORS))
				 parser_cfg->formula_options.
				   allow_abbreviated_operators = yyvsp[0].truth_value;
                             }
    break;

  case 87:
#line 813 "Config-parse.yy"
    {
			       current_option_type = CFG_ANDPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 88:
#line 818 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 89:
#line 822 "Config-parse.yy"
    {
			       if (!isLocked(CFG_ANDPRIORITY))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
	                           [::Ltl::LTL_CONJUNCTION] = yyvsp[0].integer;
			       }
                             }
    break;

  case 90:
#line 833 "Config-parse.yy"
    {
			       current_option_type = CFG_BEFOREPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 91:
#line 838 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 92:
#line 842 "Config-parse.yy"
    {
			       if (!isLocked(CFG_BEFOREPRIORITY))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
	                           [::Ltl::LTL_BEFORE] = yyvsp[0].integer;
			       }
                             }
    break;

  case 93:
#line 853 "Config-parse.yy"
    {
			       current_option_type = CFG_CHANGEINTERVAL;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 94:
#line 858 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 95:
#line 862 "Config-parse.yy"
    {
			       if (!isLocked(CFG_CHANGEINTERVAL))
			       {
				 checkIntegerRange
	                           (yyvsp[0].integer, Configuration::GENERATION_RANGE, true);
				 parser_cfg->global_options.
                                   formula_change_interval = yyvsp[0].integer;
			       }
                             }
    break;

  case 96:
#line 873 "Config-parse.yy"
    {
			       current_option_type
	                         = CFG_DEFAULTOPERATORPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 97:
#line 879 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 98:
#line 883 "Config-parse.yy"
    {
			       if (!isLocked(CFG_DEFAULTOPERATORPRIORITY))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.
	                           default_operator_priority = yyvsp[0].integer;
			       }
                             }
    break;

  case 99:
#line 894 "Config-parse.yy"
    {
			       current_option_type = CFG_EQUIVALENCEPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 100:
#line 899 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 101:
#line 903 "Config-parse.yy"
    {
			       if (!isLocked(CFG_EQUIVALENCEPRIORITY))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
	                           [::Ltl::LTL_EQUIVALENCE] = yyvsp[0].integer;
			       }
                             }
    break;

  case 102:
#line 914 "Config-parse.yy"
    {
			       current_option_type = CFG_FALSEPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 103:
#line 919 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 104:
#line 923 "Config-parse.yy"
    {
			       if (!isLocked(CFG_FALSEPRIORITY))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_FALSE] = yyvsp[0].integer;
			       }
                             }
    break;

  case 105:
#line 934 "Config-parse.yy"
    {
			       current_option_type = CFG_FINALLYPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 106:
#line 939 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 107:
#line 943 "Config-parse.yy"
    {
			       if (!isLocked(CFG_FINALLYPRIORITY))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_FINALLY] = yyvsp[0].integer;
			       }
                             }
    break;

  case 108:
#line 954 "Config-parse.yy"
    {
			       current_option_type = CFG_GENERATEMODE;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 109:
#line 959 "Config-parse.yy"
    {
                               expected_token = CFG_FORMULA_MODE_VALUE;
                             }
    break;

  case 110:
#line 963 "Config-parse.yy"
    {
			       if (!isLocked(CFG_GENERATEMODE))
				 parser_cfg->formula_options.generate_mode
				   = yyvsp[0].formula_mode_value;
                             }
    break;

  case 111:
#line 970 "Config-parse.yy"
    {
			       current_option_type = CFG_GLOBALLYPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 112:
#line 975 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 113:
#line 979 "Config-parse.yy"
    {
			       if (!isLocked(CFG_GLOBALLYPRIORITY))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_GLOBALLY] = yyvsp[0].integer;
			       }
                             }
    break;

  case 114:
#line 990 "Config-parse.yy"
    {
			       current_option_type = CFG_IMPLICATIONPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 115:
#line 995 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 116:
#line 999 "Config-parse.yy"
    {
			       if (!isLocked(CFG_IMPLICATIONPRIORITY))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_IMPLICATION] = yyvsp[0].integer;
			       }
                             }
    break;

  case 117:
#line 1010 "Config-parse.yy"
    {
			       current_option_type = CFG_NEXTPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 118:
#line 1015 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 119:
#line 1019 "Config-parse.yy"
    {
			       if (!isLocked(CFG_NEXTPRIORITY))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_NEXT] = yyvsp[0].integer;
			       }
                             }
    break;

  case 120:
#line 1030 "Config-parse.yy"
    {
			       current_option_type = CFG_NOTPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 121:
#line 1035 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 122:
#line 1039 "Config-parse.yy"
    {
			       if (!isLocked(CFG_NOTPRIORITY))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_NEGATION] = yyvsp[0].integer;
			       }
                             }
    break;

  case 123:
#line 1050 "Config-parse.yy"
    {
			       current_option_type = CFG_ORPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 124:
#line 1055 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 125:
#line 1059 "Config-parse.yy"
    {
			       if (!isLocked(CFG_ORPRIORITY))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_DISJUNCTION] = yyvsp[0].integer;
			       }
                             }
    break;

  case 126:
#line 1070 "Config-parse.yy"
    {
			       current_option_type = CFG_OUTPUTMODE;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 127:
#line 1075 "Config-parse.yy"
    {
                               expected_token = CFG_FORMULA_MODE_VALUE;
                             }
    break;

  case 128:
#line 1079 "Config-parse.yy"
    {
			       if (!isLocked(CFG_OUTPUTMODE))
				 parser_cfg->formula_options.output_mode = yyvsp[0].formula_mode_value;
                             }
    break;

  case 129:
#line 1085 "Config-parse.yy"
    {
			       current_option_type = CFG_PROPOSITIONPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 130:
#line 1090 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 131:
#line 1094 "Config-parse.yy"
    {
			       if (!isLocked(CFG_PROPOSITIONPRIORITY))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_ATOM] = yyvsp[0].integer;
			       }
                             }
    break;

  case 132:
#line 1105 "Config-parse.yy"
    {
			       current_option_type = CFG_PROPOSITIONS;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 133:
#line 1110 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 134:
#line 1114 "Config-parse.yy"
    {
			       if (!isLocked(CFG_PROPOSITIONS))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::PROPOSITION_COUNT_RANGE,
                                    true);
				 parser_cfg->formula_options.
                                   formula_generator.
				   number_of_available_variables = yyvsp[0].integer;
			       }
                             }
    break;

  case 135:
#line 1127 "Config-parse.yy"
    {
			       current_option_type = CFG_RANDOMSEED;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 136:
#line 1132 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 137:
#line 1136 "Config-parse.yy"
    {
			       if (!isLocked(CFG_RANDOMSEED))
				 parser_cfg->global_options.formula_random_seed
                                   = yyvsp[0].integer;
                             }
    break;

  case 138:
#line 1143 "Config-parse.yy"
    {
			       current_option_type = CFG_RELEASEPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 139:
#line 1148 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 140:
#line 1152 "Config-parse.yy"
    {
			       if (!isLocked(CFG_RELEASEPRIORITY))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_V] = yyvsp[0].integer;
			       }
                             }
    break;

  case 141:
#line 1163 "Config-parse.yy"
    {
			       current_option_type = CFG_SIZE;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 142:
#line 1168 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER_INTERVAL;
                             }
    break;

  case 144:
#line 1174 "Config-parse.yy"
    {
			       current_option_type = CFG_STRONGRELEASEPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 145:
#line 1179 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 146:
#line 1183 "Config-parse.yy"
    {
			       if (!isLocked(CFG_STRONGRELEASEPRIORITY))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_STRONG_RELEASE] = yyvsp[0].integer;
			       }
                             }
    break;

  case 147:
#line 1194 "Config-parse.yy"
    {
			       current_option_type = CFG_TRUEPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 148:
#line 1199 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 149:
#line 1203 "Config-parse.yy"
    {
			       if (!isLocked(CFG_TRUEPRIORITY))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_TRUE] = yyvsp[0].integer;
			       }
                             }
    break;

  case 150:
#line 1214 "Config-parse.yy"
    {
			       current_option_type = CFG_UNTILPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 151:
#line 1219 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 152:
#line 1223 "Config-parse.yy"
    {
			       if (!isLocked(CFG_UNTILPRIORITY))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_UNTIL] = yyvsp[0].integer;
			       }
                             }
    break;

  case 153:
#line 1234 "Config-parse.yy"
    {
			       current_option_type = CFG_WEAKUNTILPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 154:
#line 1239 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 155:
#line 1243 "Config-parse.yy"
    {
			       if (!isLocked(CFG_WEAKUNTILPRIORITY))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_WEAK_UNTIL] = yyvsp[0].integer;
			       }
                             }
    break;

  case 156:
#line 1254 "Config-parse.yy"
    {
			       current_option_type = CFG_XORPRIORITY;
                               expected_token = CFG_EQUALS;
                             }
    break;

  case 157:
#line 1259 "Config-parse.yy"
    {
                               expected_token = CFG_INTEGER;
                             }
    break;

  case 158:
#line 1263 "Config-parse.yy"
    {
			       if (!isLocked(CFG_XORPRIORITY))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::PRIORITY_RANGE, true);
				 parser_cfg->formula_options.symbol_priority
                                   [::Ltl::LTL_XOR] = yyvsp[0].integer;
			       }
                             }
    break;

  case 159:
#line 1275 "Config-parse.yy"
    {
			       if (!isLocked(CFG_SIZE))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::FORMULA_SIZE_RANGE,
                                    true);
				 parser_cfg->formula_options.formula_generator.
                                   size = yyvsp[0].integer;
				 parser_cfg->formula_options.formula_generator.
				   max_size = yyvsp[0].integer;
			       }
                             }
    break;

  case 160:
#line 1289 "Config-parse.yy"
    {
			       if (!isLocked(CFG_SIZE))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer_interval.min, Configuration::FORMULA_SIZE_RANGE,
				    true);

				 Configuration::IntegerRange max_len_range
	                           (Configuration::FORMULA_MAX_SIZE_RANGE);

				 max_len_range.min = yyvsp[0].integer_interval.min;

				 checkIntegerRange(yyvsp[0].integer_interval.max, max_len_range,
						   true);

				 parser_cfg->formula_options.formula_generator.
				   size = yyvsp[0].integer_interval.min;
				 parser_cfg->formula_options.formula_generator.
				   max_size = yyvsp[0].integer_interval.max;
			       }
			     }
    break;

  case 161:
#line 1313 "Config-parse.yy"
    {
			       if (!isLocked(CFG_SIZE))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer, Configuration::STATESPACE_SIZE_RANGE,
				    true);
				 parser_cfg->statespace_generator.min_size
                                   = yyvsp[0].integer;
				 parser_cfg->statespace_generator.max_size
                                   = yyvsp[0].integer;
			       }
                             }
    break;

  case 162:
#line 1327 "Config-parse.yy"
    {
			       if (!isLocked(CFG_SIZE))
			       {
				 checkIntegerRange
                                   (yyvsp[0].integer_interval.min,
                                    Configuration::STATESPACE_SIZE_RANGE,
                                    true);

				 Configuration::IntegerRange max_size_range
                                   (Configuration::STATESPACE_MAX_SIZE_RANGE);

				 checkIntegerRange(yyvsp[0].integer_interval.max, max_size_range,
						   true);

				 parser_cfg->statespace_generator.min_size
                                   = yyvsp[0].integer_interval.min;
				 parser_cfg->statespace_generator.max_size
                                   = yyvsp[0].integer_interval.max;
			       }
			     }
    break;


    }

/* Line 1000 of yacc.c.  */
#line 2925 "Config-parse.cc"

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
      /* If just tried and failed to reuse lookahead token after an
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
		 YYDSYMPRINTF ("Error: popping", yystos[*yyssp], yyvsp, yylsp);
		 yydestruct (yystos[*yyssp], yyvsp);
	       }
        }
      else
	{
	  YYDSYMPRINTF ("Error: discarding", yytoken, &yylval, &yylloc);
	  yydestruct (yytoken, &yylval);
	  yychar = YYEMPTY;

	}
    }

  /* Else will try to reuse lookahead token after shifting the error
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

      YYDSYMPRINTF ("Error: popping", yystos[*yyssp], yyvsp, yylsp);
      yydestruct (yystos[yystate], yyvsp);
      YYPOPSTACK;
      yystate = *yyssp;
      YY_STACK_PRINT (yyss, yyssp);
    }

  if (yyn == YYFINAL)
    YYACCEPT;

  YYDPRINTF ((stderr, "Shifting error token, "));

  *++yyvsp = yylval;


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


#line 1349 "Config-parse.yy"




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
 * Returns:       
 *
 * ------------------------------------------------------------------------- */
{
  yyrestart(stream);
  parser_cfg = &configuration;
  config_file_line_number = 1;
  return yyparse();
}

