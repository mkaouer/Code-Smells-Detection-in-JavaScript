/* A Bison parser, made by GNU Bison 2.4.3.  */

/* Skeleton implementation for Bison's Yacc-like parsers in C
   
      Copyright (C) 1984, 1989, 1990, 2000, 2001, 2002, 2003, 2004, 2005, 2006,
   2009, 2010 Free Software Foundation, Inc.
   
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

/* C LALR(1) parser skeleton written by Richard Stallman, by
   simplifying the original so-called "semantic" parser.  */

/* All symbols defined below should begin with yy or YY, to avoid
   infringing on user name space.  This should be done even for local
   variables, as they might otherwise be expanded by user macros.
   There are some unavoidable exceptions within include files to
   define necessary library symbols; they are noted "INFRINGES ON
   USER NAME SPACE" below.  */

/* Identify Bison output.  */
#define YYBISON 1

/* Bison version.  */
#define YYBISON_VERSION "2.4.3"

/* Skeleton name.  */
#define YYSKELETON_NAME "yacc.c"

/* Pure parsers.  */
#define YYPURE 0

/* Push parsers.  */
#define YYPUSH 0

/* Pull parsers.  */
#define YYPULL 1

/* Using locations.  */
#define YYLSP_NEEDED 0



/* Copy the first part of user declarations.  */

/* Line 189 of yacc.c  */
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

void yyerror(const char* error_message);    /* Fwd. definition.  See below. */



/* Line 189 of yacc.c  */
#line 154 "Config-parse.cc"

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

/* Enabling the token table.  */
#ifndef YYTOKEN_TABLE
# define YYTOKEN_TABLE 0
#endif


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
/* Tokens.  */
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




#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
typedef union YYSTYPE
{

/* Line 214 of yacc.c  */
#line 112 "Config-parse.yy"

  const char* value;



/* Line 214 of yacc.c  */
#line 300 "Config-parse.cc"
} YYSTYPE;
# define YYSTYPE_IS_TRIVIAL 1
# define yystype YYSTYPE /* obsolescent; will be withdrawn */
# define YYSTYPE_IS_DECLARED 1
#endif


/* Copy the second part of user declarations.  */


/* Line 264 of yacc.c  */
#line 312 "Config-parse.cc"

#ifdef short
# undef short
#endif

#ifdef YYTYPE_UINT8
typedef YYTYPE_UINT8 yytype_uint8;
#else
typedef unsigned char yytype_uint8;
#endif

#ifdef YYTYPE_INT8
typedef YYTYPE_INT8 yytype_int8;
#elif (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
typedef signed char yytype_int8;
#else
typedef short int yytype_int8;
#endif

#ifdef YYTYPE_UINT16
typedef YYTYPE_UINT16 yytype_uint16;
#else
typedef unsigned short int yytype_uint16;
#endif

#ifdef YYTYPE_INT16
typedef YYTYPE_INT16 yytype_int16;
#else
typedef short int yytype_int16;
#endif

#ifndef YYSIZE_T
# ifdef __SIZE_TYPE__
#  define YYSIZE_T __SIZE_TYPE__
# elif defined size_t
#  define YYSIZE_T size_t
# elif ! defined YYSIZE_T && (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
#  include <stddef.h> /* INFRINGES ON USER NAME SPACE */
#  define YYSIZE_T size_t
# else
#  define YYSIZE_T unsigned int
# endif
#endif

#define YYSIZE_MAXIMUM ((YYSIZE_T) -1)

#ifndef YY_
# if defined YYENABLE_NLS && YYENABLE_NLS
#  if ENABLE_NLS
#   include <libintl.h> /* INFRINGES ON USER NAME SPACE */
#   define YY_(msgid) dgettext ("bison-runtime", msgid)
#  endif
# endif
# ifndef YY_
#  define YY_(msgid) msgid
# endif
#endif

/* Suppress unused-variable warnings by "using" E.  */
#if ! defined lint || defined __GNUC__
# define YYUSE(e) ((void) (e))
#else
# define YYUSE(e) /* empty */
#endif

/* Identity function, used to suppress warnings about constant conditions.  */
#ifndef lint
# define YYID(n) (n)
#else
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static int
YYID (int yyi)
#else
static int
YYID (yyi)
    int yyi;
#endif
{
  return yyi;
}
#endif

#if ! defined yyoverflow || YYERROR_VERBOSE

/* The parser invokes alloca or malloc; define the necessary symbols.  */

# ifdef YYSTACK_USE_ALLOCA
#  if YYSTACK_USE_ALLOCA
#   ifdef __GNUC__
#    define YYSTACK_ALLOC __builtin_alloca
#   elif defined __BUILTIN_VA_ARG_INCR
#    include <alloca.h> /* INFRINGES ON USER NAME SPACE */
#   elif defined _AIX
#    define YYSTACK_ALLOC __alloca
#   elif defined _MSC_VER
#    include <malloc.h> /* INFRINGES ON USER NAME SPACE */
#    define alloca _alloca
#   else
#    define YYSTACK_ALLOC alloca
#    if ! defined _ALLOCA_H && ! defined _STDLIB_H && (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
#     include <stdlib.h> /* INFRINGES ON USER NAME SPACE */
#     ifndef _STDLIB_H
#      define _STDLIB_H 1
#     endif
#    endif
#   endif
#  endif
# endif

# ifdef YYSTACK_ALLOC
   /* Pacify GCC's `empty if-body' warning.  */
#  define YYSTACK_FREE(Ptr) do { /* empty */; } while (YYID (0))
#  ifndef YYSTACK_ALLOC_MAXIMUM
    /* The OS might guarantee only one guard page at the bottom of the stack,
       and a page size can be as small as 4096 bytes.  So we cannot safely
       invoke alloca (N) if N exceeds 4096.  Use a slightly smaller number
       to allow for a few compiler-allocated temporary stack slots.  */
#   define YYSTACK_ALLOC_MAXIMUM 4032 /* reasonable circa 2006 */
#  endif
# else
#  define YYSTACK_ALLOC YYMALLOC
#  define YYSTACK_FREE YYFREE
#  ifndef YYSTACK_ALLOC_MAXIMUM
#   define YYSTACK_ALLOC_MAXIMUM YYSIZE_MAXIMUM
#  endif
#  if (defined __cplusplus && ! defined _STDLIB_H \
       && ! ((defined YYMALLOC || defined malloc) \
	     && (defined YYFREE || defined free)))
#   include <stdlib.h> /* INFRINGES ON USER NAME SPACE */
#   ifndef _STDLIB_H
#    define _STDLIB_H 1
#   endif
#  endif
#  ifndef YYMALLOC
#   define YYMALLOC malloc
#   if ! defined malloc && ! defined _STDLIB_H && (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
void *malloc (YYSIZE_T); /* INFRINGES ON USER NAME SPACE */
#   endif
#  endif
#  ifndef YYFREE
#   define YYFREE free
#   if ! defined free && ! defined _STDLIB_H && (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
void free (void *); /* INFRINGES ON USER NAME SPACE */
#   endif
#  endif
# endif
#endif /* ! defined yyoverflow || YYERROR_VERBOSE */


#if (! defined yyoverflow \
     && (! defined __cplusplus \
	 || (defined YYSTYPE_IS_TRIVIAL && YYSTYPE_IS_TRIVIAL)))

/* A type that is properly aligned for any stack member.  */
union yyalloc
{
  yytype_int16 yyss_alloc;
  YYSTYPE yyvs_alloc;
};

/* The size of the maximum gap between one aligned stack and the next.  */
# define YYSTACK_GAP_MAXIMUM (sizeof (union yyalloc) - 1)

/* The size of an array large to enough to hold all stacks, each with
   N elements.  */
# define YYSTACK_BYTES(N) \
     ((N) * (sizeof (yytype_int16) + sizeof (YYSTYPE)) \
      + YYSTACK_GAP_MAXIMUM)

/* Copy COUNT objects from FROM to TO.  The source and destination do
   not overlap.  */
# ifndef YYCOPY
#  if defined __GNUC__ && 1 < __GNUC__
#   define YYCOPY(To, From, Count) \
      __builtin_memcpy (To, From, (Count) * sizeof (*(From)))
#  else
#   define YYCOPY(To, From, Count)		\
      do					\
	{					\
	  YYSIZE_T yyi;				\
	  for (yyi = 0; yyi < (Count); yyi++)	\
	    (To)[yyi] = (From)[yyi];		\
	}					\
      while (YYID (0))
#  endif
# endif

/* Relocate STACK from its old location to the new one.  The
   local variables YYSIZE and YYSTACKSIZE give the old and new number of
   elements in the stack, and YYPTR gives the new location of the
   stack.  Advance YYPTR to a properly aligned location for the next
   stack.  */
# define YYSTACK_RELOCATE(Stack_alloc, Stack)				\
    do									\
      {									\
	YYSIZE_T yynewbytes;						\
	YYCOPY (&yyptr->Stack_alloc, Stack, yysize);			\
	Stack = &yyptr->Stack_alloc;					\
	yynewbytes = yystacksize * sizeof (*Stack) + YYSTACK_GAP_MAXIMUM; \
	yyptr += yynewbytes / sizeof (*yyptr);				\
      }									\
    while (YYID (0))

#endif

/* YYFINAL -- State number of the termination state.  */
#define YYFINAL  3
/* YYLAST -- Last index in YYTABLE.  */
#define YYLAST   102

/* YYNTOKENS -- Number of terminals.  */
#define YYNTOKENS  53
/* YYNNTS -- Number of nonterminals.  */
#define YYNNTS  28
/* YYNRULES -- Number of rules.  */
#define YYNRULES  76
/* YYNRULES -- Number of states.  */
#define YYNSTATES  130

/* YYTRANSLATE(YYLEX) -- Bison symbol number corresponding to YYLEX.  */
#define YYUNDEFTOK  2
#define YYMAXUTOK   307

#define YYTRANSLATE(YYX)						\
  ((unsigned int) (YYX) <= YYMAXUTOK ? yytranslate[YYX] : YYUNDEFTOK)

/* YYTRANSLATE[YYLEX] -- Bison symbol number corresponding to YYLEX.  */
static const yytype_uint8 yytranslate[] =
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
static const yytype_uint8 yyprhs[] =
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

/* YYRHS -- A `-1'-separated list of the rules' RHS.  */
static const yytype_int8 yyrhs[] =
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
static const yytype_uint16 yyrline[] =
{
       0,   162,   162,   165,   167,   165,   172,   174,   173,   178,
     179,   180,   181,   185,   184,   202,   204,   203,   208,   215,
     218,   223,   228,   227,   232,   234,   233,   238,   246,   254,
     257,   265,   268,   276,   279,   289,   288,   293,   295,   294,
     299,   307,   315,   318,   326,   335,   342,   352,   351,   356,
     358,   357,   362,   370,   379,   388,   396,   405,   414,   423,
     432,   439,   448,   457,   466,   475,   484,   491,   500,   509,
     518,   527,   534,   543,   552,   561,   570
};
#endif

#if YYDEBUG || YYERROR_VERBOSE || YYTOKEN_TABLE
/* YYTNAME[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
   First, the terminals, then, starting at YYNTOKENS, nonterminals.  */
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
  "CFG_UNKNOWN", "$accept", "configuration_file", "equals_value", "$@1",
  "$@2", "configuration_blocks", "$@3", "configuration_block",
  "algorithm_option_block", "$@4", "algorithm_options", "$@5",
  "algorithm_option", "global_option_block", "$@6", "global_options",
  "$@7", "global_option", "statespace_option_block", "$@8",
  "statespace_options", "$@9", "statespace_option", "formula_option_block",
  "$@10", "formula_options", "$@11", "formula_option", 0
};
#endif

# ifdef YYPRINT
/* YYTOKNUM[YYLEX-NUM] -- Internal token number corresponding to
   token YYLEX-NUM.  */
static const yytype_uint16 yytoknum[] =
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
static const yytype_uint8 yyr1[] =
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
static const yytype_uint8 yyr2[] =
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
static const yytype_uint8 yydefact[] =
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

/* YYDEFGOTO[NTERM-NUM].  */
static const yytype_int16 yydefgoto[] =
{
      -1,     1,    82,    83,   128,     2,     4,     9,    10,    14,
      22,    27,    38,    11,    15,    23,    29,    47,    12,    16,
      24,    31,    55,    13,    17,    25,    33,    81
};

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
#define YYPACT_NINF -36
static const yytype_int8 yypact[] =
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
static const yytype_int8 yypgoto[] =
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
static const yytype_int16 yytable[] =
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

static const yytype_uint8 yycheck[] =
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
static const yytype_uint8 yystos[] =
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

#define yyerrok		(yyerrstatus = 0)
#define yyclearin	(yychar = YYEMPTY)
#define YYEMPTY		(-2)
#define YYEOF		0

#define YYACCEPT	goto yyacceptlab
#define YYABORT		goto yyabortlab
#define YYERROR		goto yyerrorlab


/* Like YYERROR except do call yyerror.  This remains here temporarily
   to ease the transition to the new meaning of YYERROR, for GCC.
   Once GCC version 2 has supplanted version 1, this can go.  However,
   YYFAIL appears to be in use.  Nevertheless, it is formally deprecated
   in Bison 2.4.2's NEWS entry, where a plan to phase it out is
   discussed.  */

#define YYFAIL		goto yyerrlab
#if defined YYFAIL
  /* This is here to suppress warnings from the GCC cpp's
     -Wunused-macros.  Normally we don't worry about that warning, but
     some users do, and we want to make it easy for users to remove
     YYFAIL uses, which will produce warnings from Bison 2.5.  */
#endif

#define YYRECOVERING()  (!!yyerrstatus)

#define YYBACKUP(Token, Value)					\
do								\
  if (yychar == YYEMPTY && yylen == 1)				\
    {								\
      yychar = (Token);						\
      yylval = (Value);						\
      yytoken = YYTRANSLATE (yychar);				\
      YYPOPSTACK (1);						\
      goto yybackup;						\
    }								\
  else								\
    {								\
      yyerror (YY_("syntax error: cannot back up")); \
      YYERROR;							\
    }								\
while (YYID (0))


#define YYTERROR	1
#define YYERRCODE	256


/* YYLLOC_DEFAULT -- Set CURRENT to span from RHS[1] to RHS[N].
   If N is 0, then set CURRENT to the empty location which ends
   the previous symbol: RHS[0] (always defined).  */

#define YYRHSLOC(Rhs, K) ((Rhs)[K])
#ifndef YYLLOC_DEFAULT
# define YYLLOC_DEFAULT(Current, Rhs, N)				\
    do									\
      if (YYID (N))                                                    \
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
    while (YYID (0))
#endif


/* YY_LOCATION_PRINT -- Print the location on the stream.
   This macro was not mandated originally: define only if we know
   we won't break user code: when these are the locations we know.  */

#ifndef YY_LOCATION_PRINT
# if defined YYLTYPE_IS_TRIVIAL && YYLTYPE_IS_TRIVIAL
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
} while (YYID (0))

# define YY_SYMBOL_PRINT(Title, Type, Value, Location)			  \
do {									  \
  if (yydebug)								  \
    {									  \
      YYFPRINTF (stderr, "%s ", Title);					  \
      yy_symbol_print (stderr,						  \
		  Type, Value); \
      YYFPRINTF (stderr, "\n");						  \
    }									  \
} while (YYID (0))


/*--------------------------------.
| Print this symbol on YYOUTPUT.  |
`--------------------------------*/

/*ARGSUSED*/
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static void
yy_symbol_value_print (FILE *yyoutput, int yytype, YYSTYPE const * const yyvaluep)
#else
static void
yy_symbol_value_print (yyoutput, yytype, yyvaluep)
    FILE *yyoutput;
    int yytype;
    YYSTYPE const * const yyvaluep;
#endif
{
  if (!yyvaluep)
    return;
# ifdef YYPRINT
  if (yytype < YYNTOKENS)
    YYPRINT (yyoutput, yytoknum[yytype], *yyvaluep);
# else
  YYUSE (yyoutput);
# endif
  switch (yytype)
    {
      default:
	break;
    }
}


/*--------------------------------.
| Print this symbol on YYOUTPUT.  |
`--------------------------------*/

#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static void
yy_symbol_print (FILE *yyoutput, int yytype, YYSTYPE const * const yyvaluep)
#else
static void
yy_symbol_print (yyoutput, yytype, yyvaluep)
    FILE *yyoutput;
    int yytype;
    YYSTYPE const * const yyvaluep;
#endif
{
  if (yytype < YYNTOKENS)
    YYFPRINTF (yyoutput, "token %s (", yytname[yytype]);
  else
    YYFPRINTF (yyoutput, "nterm %s (", yytname[yytype]);

  yy_symbol_value_print (yyoutput, yytype, yyvaluep);
  YYFPRINTF (yyoutput, ")");
}

/*------------------------------------------------------------------.
| yy_stack_print -- Print the state stack from its BOTTOM up to its |
| TOP (included).                                                   |
`------------------------------------------------------------------*/

#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static void
yy_stack_print (yytype_int16 *yybottom, yytype_int16 *yytop)
#else
static void
yy_stack_print (yybottom, yytop)
    yytype_int16 *yybottom;
    yytype_int16 *yytop;
#endif
{
  YYFPRINTF (stderr, "Stack now");
  for (; yybottom <= yytop; yybottom++)
    {
      int yybot = *yybottom;
      YYFPRINTF (stderr, " %d", yybot);
    }
  YYFPRINTF (stderr, "\n");
}

# define YY_STACK_PRINT(Bottom, Top)				\
do {								\
  if (yydebug)							\
    yy_stack_print ((Bottom), (Top));				\
} while (YYID (0))


/*------------------------------------------------.
| Report that the YYRULE is going to be reduced.  |
`------------------------------------------------*/

#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static void
yy_reduce_print (YYSTYPE *yyvsp, int yyrule)
#else
static void
yy_reduce_print (yyvsp, yyrule)
    YYSTYPE *yyvsp;
    int yyrule;
#endif
{
  int yynrhs = yyr2[yyrule];
  int yyi;
  unsigned long int yylno = yyrline[yyrule];
  YYFPRINTF (stderr, "Reducing stack by rule %d (line %lu):\n",
	     yyrule - 1, yylno);
  /* The symbols being reduced.  */
  for (yyi = 0; yyi < yynrhs; yyi++)
    {
      YYFPRINTF (stderr, "   $%d = ", yyi + 1);
      yy_symbol_print (stderr, yyrhs[yyprhs[yyrule] + yyi],
		       &(yyvsp[(yyi + 1) - (yynrhs)])
		       		       );
      YYFPRINTF (stderr, "\n");
    }
}

# define YY_REDUCE_PRINT(Rule)		\
do {					\
  if (yydebug)				\
    yy_reduce_print (yyvsp, Rule); \
} while (YYID (0))

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
   YYSTACK_ALLOC_MAXIMUM < YYSTACK_BYTES (YYMAXDEPTH)
   evaluated with infinite-precision integer arithmetic.  */

#ifndef YYMAXDEPTH
# define YYMAXDEPTH 10000
#endif



#if YYERROR_VERBOSE

# ifndef yystrlen
#  if defined __GLIBC__ && defined _STRING_H
#   define yystrlen strlen
#  else
/* Return the length of YYSTR.  */
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static YYSIZE_T
yystrlen (const char *yystr)
#else
static YYSIZE_T
yystrlen (yystr)
    const char *yystr;
#endif
{
  YYSIZE_T yylen;
  for (yylen = 0; yystr[yylen]; yylen++)
    continue;
  return yylen;
}
#  endif
# endif

# ifndef yystpcpy
#  if defined __GLIBC__ && defined _STRING_H && defined _GNU_SOURCE
#   define yystpcpy stpcpy
#  else
/* Copy YYSRC to YYDEST, returning the address of the terminating '\0' in
   YYDEST.  */
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
static char *
yystpcpy (char *yydest, const char *yysrc)
#else
static char *
yystpcpy (yydest, yysrc)
    char *yydest;
    const char *yysrc;
#endif
{
  char *yyd = yydest;
  const char *yys = yysrc;

  while ((*yyd++ = *yys++) != '\0')
    continue;

  return yyd - 1;
}
#  endif
# endif

# ifndef yytnamerr
/* Copy to YYRES the contents of YYSTR after stripping away unnecessary
   quotes and backslashes, so that it's suitable for yyerror.  The
   heuristic is that double-quoting is unnecessary unless the string
   contains an apostrophe, a comma, or backslash (other than
   backslash-backslash).  YYSTR is taken from yytname.  If YYRES is
   null, do not copy; instead, return the length of what the result
   would have been.  */
static YYSIZE_T
yytnamerr (char *yyres, const char *yystr)
{
  if (*yystr == '"')
    {
      YYSIZE_T yyn = 0;
      char const *yyp = yystr;

      for (;;)
	switch (*++yyp)
	  {
	  case '\'':
	  case ',':
	    goto do_not_strip_quotes;

	  case '\\':
	    if (*++yyp != '\\')
	      goto do_not_strip_quotes;
	    /* Fall through.  */
	  default:
	    if (yyres)
	      yyres[yyn] = *yyp;
	    yyn++;
	    break;

	  case '"':
	    if (yyres)
	      yyres[yyn] = '\0';
	    return yyn;
	  }
    do_not_strip_quotes: ;
    }

  if (! yyres)
    return yystrlen (yystr);

  return yystpcpy (yyres, yystr) - yyres;
}
# endif

/* Copy into YYRESULT an error message about the unexpected token
   YYCHAR while in state YYSTATE.  Return the number of bytes copied,
   including the terminating null byte.  If YYRESULT is null, do not
   copy anything; just return the number of bytes that would be
   copied.  As a special case, return 0 if an ordinary "syntax error"
   message will do.  Return YYSIZE_MAXIMUM if overflow occurs during
   size calculation.  */
static YYSIZE_T
yysyntax_error (char *yyresult, int yystate, int yychar)
{
  int yyn = yypact[yystate];

  if (! (YYPACT_NINF < yyn && yyn <= YYLAST))
    return 0;
  else
    {
      int yytype = YYTRANSLATE (yychar);
      YYSIZE_T yysize0 = yytnamerr (0, yytname[yytype]);
      YYSIZE_T yysize = yysize0;
      YYSIZE_T yysize1;
      int yysize_overflow = 0;
      enum { YYERROR_VERBOSE_ARGS_MAXIMUM = 5 };
      char const *yyarg[YYERROR_VERBOSE_ARGS_MAXIMUM];
      int yyx;

# if 0
      /* This is so xgettext sees the translatable formats that are
	 constructed on the fly.  */
      YY_("syntax error, unexpected %s");
      YY_("syntax error, unexpected %s, expecting %s");
      YY_("syntax error, unexpected %s, expecting %s or %s");
      YY_("syntax error, unexpected %s, expecting %s or %s or %s");
      YY_("syntax error, unexpected %s, expecting %s or %s or %s or %s");
# endif
      char *yyfmt;
      char const *yyf;
      static char const yyunexpected[] = "syntax error, unexpected %s";
      static char const yyexpecting[] = ", expecting %s";
      static char const yyor[] = " or %s";
      char yyformat[sizeof yyunexpected
		    + sizeof yyexpecting - 1
		    + ((YYERROR_VERBOSE_ARGS_MAXIMUM - 2)
		       * (sizeof yyor - 1))];
      char const *yyprefix = yyexpecting;

      /* Start YYX at -YYN if negative to avoid negative indexes in
	 YYCHECK.  */
      int yyxbegin = yyn < 0 ? -yyn : 0;

      /* Stay within bounds of both yycheck and yytname.  */
      int yychecklim = YYLAST - yyn + 1;
      int yyxend = yychecklim < YYNTOKENS ? yychecklim : YYNTOKENS;
      int yycount = 1;

      yyarg[0] = yytname[yytype];
      yyfmt = yystpcpy (yyformat, yyunexpected);

      for (yyx = yyxbegin; yyx < yyxend; ++yyx)
	if (yycheck[yyx + yyn] == yyx && yyx != YYTERROR)
	  {
	    if (yycount == YYERROR_VERBOSE_ARGS_MAXIMUM)
	      {
		yycount = 1;
		yysize = yysize0;
		yyformat[sizeof yyunexpected - 1] = '\0';
		break;
	      }
	    yyarg[yycount++] = yytname[yyx];
	    yysize1 = yysize + yytnamerr (0, yytname[yyx]);
	    yysize_overflow |= (yysize1 < yysize);
	    yysize = yysize1;
	    yyfmt = yystpcpy (yyfmt, yyprefix);
	    yyprefix = yyor;
	  }

      yyf = YY_(yyformat);
      yysize1 = yysize + yystrlen (yyf);
      yysize_overflow |= (yysize1 < yysize);
      yysize = yysize1;

      if (yysize_overflow)
	return YYSIZE_MAXIMUM;

      if (yyresult)
	{
	  /* Avoid sprintf, as that infringes on the user's name space.
	     Don't have undefined behavior even if the translation
	     produced a string with the wrong number of "%s"s.  */
	  char *yyp = yyresult;
	  int yyi = 0;
	  while ((*yyp = *yyf) != '\0')
	    {
	      if (*yyp == '%' && yyf[1] == 's' && yyi < yycount)
		{
		  yyp += yytnamerr (yyp, yyarg[yyi++]);
		  yyf += 2;
		}
	      else
		{
		  yyp++;
		  yyf++;
		}
	    }
	}
      return yysize;
    }
}
#endif /* YYERROR_VERBOSE */


/*-----------------------------------------------.
| Release the memory associated to this symbol.  |
`-----------------------------------------------*/

/*ARGSUSED*/
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
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
  YYUSE (yyvaluep);

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
#if defined __STDC__ || defined __cplusplus
int yyparse (void *YYPARSE_PARAM);
#else
int yyparse ();
#endif
#else /* ! YYPARSE_PARAM */
#if defined __STDC__ || defined __cplusplus
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



/*-------------------------.
| yyparse or yypush_parse.  |
`-------------------------*/

#ifdef YYPARSE_PARAM
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
int
yyparse (void *YYPARSE_PARAM)
#else
int
yyparse (YYPARSE_PARAM)
    void *YYPARSE_PARAM;
#endif
#else /* ! YYPARSE_PARAM */
#if (defined __STDC__ || defined __C99__FUNC__ \
     || defined __cplusplus || defined _MSC_VER)
int
yyparse (void)
#else
int
yyparse ()

#endif
#endif
{


    int yystate;
    /* Number of tokens to shift before error messages enabled.  */
    int yyerrstatus;

    /* The stacks and their tools:
       `yyss': related to states.
       `yyvs': related to semantic values.

       Refer to the stacks thru separate pointers, to allow yyoverflow
       to reallocate them elsewhere.  */

    /* The state stack.  */
    yytype_int16 yyssa[YYINITDEPTH];
    yytype_int16 *yyss;
    yytype_int16 *yyssp;

    /* The semantic value stack.  */
    YYSTYPE yyvsa[YYINITDEPTH];
    YYSTYPE *yyvs;
    YYSTYPE *yyvsp;

    YYSIZE_T yystacksize;

  int yyn;
  int yyresult;
  /* Lookahead token as an internal (translated) token number.  */
  int yytoken;
  /* The variables used to return semantic value and location from the
     action routines.  */
  YYSTYPE yyval;

#if YYERROR_VERBOSE
  /* Buffer for error messages, and its allocated size.  */
  char yymsgbuf[128];
  char *yymsg = yymsgbuf;
  YYSIZE_T yymsg_alloc = sizeof yymsgbuf;
#endif

#define YYPOPSTACK(N)   (yyvsp -= (N), yyssp -= (N))

  /* The number of symbols on the RHS of the reduced rule.
     Keep to zero when no symbol should be popped.  */
  int yylen = 0;

  yytoken = 0;
  yyss = yyssa;
  yyvs = yyvsa;
  yystacksize = YYINITDEPTH;

  YYDPRINTF ((stderr, "Starting parse\n"));

  yystate = 0;
  yyerrstatus = 0;
  yynerrs = 0;
  yychar = YYEMPTY; /* Cause a token to be read.  */

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
     have just been pushed.  So pushing a state here evens the stacks.  */
  yyssp++;

 yysetstate:
  *yyssp = yystate;

  if (yyss + yystacksize - 1 <= yyssp)
    {
      /* Get the current used size of the three stacks, in elements.  */
      YYSIZE_T yysize = yyssp - yyss + 1;

#ifdef yyoverflow
      {
	/* Give user a chance to reallocate the stack.  Use copies of
	   these so that the &'s don't force the real ones into
	   memory.  */
	YYSTYPE *yyvs1 = yyvs;
	yytype_int16 *yyss1 = yyss;

	/* Each stack pointer address is followed by the size of the
	   data in use in that stack, in bytes.  This used to be a
	   conditional around just the two extra args, but that might
	   be undefined if yyoverflow is a macro.  */
	yyoverflow (YY_("memory exhausted"),
		    &yyss1, yysize * sizeof (*yyssp),
		    &yyvs1, yysize * sizeof (*yyvsp),
		    &yystacksize);

	yyss = yyss1;
	yyvs = yyvs1;
      }
#else /* no yyoverflow */
# ifndef YYSTACK_RELOCATE
      goto yyexhaustedlab;
# else
      /* Extend the stack our own way.  */
      if (YYMAXDEPTH <= yystacksize)
	goto yyexhaustedlab;
      yystacksize *= 2;
      if (YYMAXDEPTH < yystacksize)
	yystacksize = YYMAXDEPTH;

      {
	yytype_int16 *yyss1 = yyss;
	union yyalloc *yyptr =
	  (union yyalloc *) YYSTACK_ALLOC (YYSTACK_BYTES (yystacksize));
	if (! yyptr)
	  goto yyexhaustedlab;
	YYSTACK_RELOCATE (yyss_alloc, yyss);
	YYSTACK_RELOCATE (yyvs_alloc, yyvs);
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

  if (yystate == YYFINAL)
    YYACCEPT;

  goto yybackup;

/*-----------.
| yybackup.  |
`-----------*/
yybackup:

  /* Do appropriate processing given the current state.  Read a
     lookahead token if we need one and don't already have one.  */

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

  /* Count tokens shifted since error; after three, turn off error
     status.  */
  if (yyerrstatus)
    yyerrstatus--;

  /* Shift the lookahead token.  */
  YY_SYMBOL_PRINT ("Shifting", yytoken, &yylval, &yylloc);

  /* Discard the shifted token.  */
  yychar = YYEMPTY;

  yystate = yyn;
  *++yyvsp = yylval;

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

/* Line 1464 of yacc.c  */
#line 165 "Config-parse.yy"
    { expected_token = CFG_EQUALS; }
    break;

  case 4:

/* Line 1464 of yacc.c  */
#line 167 "Config-parse.yy"
    { expected_token = CFG_VALUE; }
    break;

  case 5:

/* Line 1464 of yacc.c  */
#line 169 "Config-parse.yy"
    { (yyval.value) = (yyvsp[(4) - (4)].value); }
    break;

  case 7:

/* Line 1464 of yacc.c  */
#line 174 "Config-parse.yy"
    { expected_token = CFG_BLOCK_ID; }
    break;

  case 13:

/* Line 1464 of yacc.c  */
#line 185 "Config-parse.yy"
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

/* Line 1464 of yacc.c  */
#line 194 "Config-parse.yy"
    {
                               parser_cfg->registerAlgorithm
                                 (algorithm_name, algorithm_path,
                                  algorithm_parameters, algorithm_enabled,
                                  algorithm_begin_line);
                             }
    break;

  case 16:

/* Line 1464 of yacc.c  */
#line 204 "Config-parse.yy"
    { expected_token = CFG_OPTION_ID; }
    break;

  case 18:

/* Line 1464 of yacc.c  */
#line 209 "Config-parse.yy"
    {
                               parser_cfg->readTruthValue
                                             (algorithm_enabled,
                                              (yyvsp[(2) - (2)].value));
                             }
    break;

  case 19:

/* Line 1464 of yacc.c  */
#line 216 "Config-parse.yy"
    { algorithm_name = unquoteString((yyvsp[(2) - (2)].value)); }
    break;

  case 20:

/* Line 1464 of yacc.c  */
#line 219 "Config-parse.yy"
    {
                               algorithm_parameters = unquoteString((yyvsp[(2) - (2)].value));
                             }
    break;

  case 21:

/* Line 1464 of yacc.c  */
#line 224 "Config-parse.yy"
    { algorithm_path = unquoteString((yyvsp[(2) - (2)].value)); }
    break;

  case 22:

/* Line 1464 of yacc.c  */
#line 228 "Config-parse.yy"
    { expected_token = CFG_LBRACE; }
    break;

  case 25:

/* Line 1464 of yacc.c  */
#line 234 "Config-parse.yy"
    { expected_token = CFG_OPTION_ID; }
    break;

  case 27:

/* Line 1464 of yacc.c  */
#line 239 "Config-parse.yy"
    {
                               parser_cfg->readTruthValue
                                             (parser_cfg->global_options.
                                                do_comp_test,
                                              (yyvsp[(2) - (2)].value));
                             }
    break;

  case 28:

/* Line 1464 of yacc.c  */
#line 247 "Config-parse.yy"
    {
                               parser_cfg->readTruthValue
                                             (parser_cfg->global_options.
                                                do_cons_test,
                                              (yyvsp[(2) - (2)].value));
			     }
    break;

  case 29:

/* Line 1464 of yacc.c  */
#line 255 "Config-parse.yy"
    { parser_cfg->readInteractivity((yyvsp[(2) - (2)].value)); }
    break;

  case 30:

/* Line 1464 of yacc.c  */
#line 258 "Config-parse.yy"
    {
                               parser_cfg->readTruthValue
                                             (parser_cfg->global_options.
                                                do_intr_test,
                                              (yyvsp[(2) - (2)].value));
                             }
    break;

  case 31:

/* Line 1464 of yacc.c  */
#line 266 "Config-parse.yy"
    { parser_cfg->readProductType((yyvsp[(2) - (2)].value)); }
    break;

  case 32:

/* Line 1464 of yacc.c  */
#line 269 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->global_options.number_of_rounds,
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::ROUND_COUNT_RANGE);
                             }
    break;

  case 33:

/* Line 1464 of yacc.c  */
#line 277 "Config-parse.yy"
    { parser_cfg->readTranslatorTimeout((yyvsp[(2) - (2)].value)); }
    break;

  case 34:

/* Line 1464 of yacc.c  */
#line 280 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->global_options.verbosity,
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::VERBOSITY_RANGE);
                             }
    break;

  case 35:

/* Line 1464 of yacc.c  */
#line 289 "Config-parse.yy"
    { expected_token = CFG_LBRACE; }
    break;

  case 38:

/* Line 1464 of yacc.c  */
#line 295 "Config-parse.yy"
    { expected_token = CFG_OPTION_ID; }
    break;

  case 40:

/* Line 1464 of yacc.c  */
#line 300 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                             (parser_cfg->global_options.
                                                statespace_change_interval,
                                              (yyvsp[(2) - (2)].value));
                             }
    break;

  case 41:

/* Line 1464 of yacc.c  */
#line 308 "Config-parse.yy"
    {
                               parser_cfg->readProbability
                                             (parser_cfg->statespace_generator.
                                                edge_probability,
                                              (yyvsp[(2) - (2)].value));
                             }
    break;

  case 42:

/* Line 1464 of yacc.c  */
#line 316 "Config-parse.yy"
    { parser_cfg->readStateSpaceMode((yyvsp[(2) - (2)].value)); }
    break;

  case 43:

/* Line 1464 of yacc.c  */
#line 319 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                             (parser_cfg->statespace_generator.
                                                atoms_per_state,
                                              (yyvsp[(2) - (2)].value));
                             }
    break;

  case 44:

/* Line 1464 of yacc.c  */
#line 327 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->global_options.
                                                statespace_random_seed,
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::RANDOM_SEED_RANGE);
                             }
    break;

  case 45:

/* Line 1464 of yacc.c  */
#line 336 "Config-parse.yy"
    {
                               parser_cfg->readSize
                                 (Configuration::OPT_STATESPACESIZE,
                                  (yyvsp[(2) - (2)].value));
                             }
    break;

  case 46:

/* Line 1464 of yacc.c  */
#line 343 "Config-parse.yy"
    {
                               parser_cfg->readProbability
                                             (parser_cfg->statespace_generator.
                                                truth_probability,
                                              (yyvsp[(2) - (2)].value));
                             }
    break;

  case 47:

/* Line 1464 of yacc.c  */
#line 352 "Config-parse.yy"
    { expected_token = CFG_LBRACE; }
    break;

  case 50:

/* Line 1464 of yacc.c  */
#line 358 "Config-parse.yy"
    { expected_token = CFG_OPTION_ID; }
    break;

  case 52:

/* Line 1464 of yacc.c  */
#line 363 "Config-parse.yy"
    {
                               parser_cfg->readTruthValue
                                             (parser_cfg->formula_options.
                                                allow_abbreviated_operators,
                                              (yyvsp[(2) - (2)].value));
                             }
    break;

  case 53:

/* Line 1464 of yacc.c  */
#line 371 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_CONJUNCTION],
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 54:

/* Line 1464 of yacc.c  */
#line 380 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_BEFORE],
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 55:

/* Line 1464 of yacc.c  */
#line 389 "Config-parse.yy"
    {
                               parser_cfg->readInteger
				             (parser_cfg->global_options.
                                                formula_change_interval,
                                              (yyvsp[(2) - (2)].value));
                             }
    break;

  case 56:

/* Line 1464 of yacc.c  */
#line 397 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.
                                    default_operator_priority,
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 57:

/* Line 1464 of yacc.c  */
#line 406 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_EQUIVALENCE],
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 58:

/* Line 1464 of yacc.c  */
#line 415 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_FALSE],
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::ATOMIC_PRIORITY_RANGE);
                             }
    break;

  case 59:

/* Line 1464 of yacc.c  */
#line 424 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_FINALLY],
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 60:

/* Line 1464 of yacc.c  */
#line 433 "Config-parse.yy"
    {
                               parser_cfg->readFormulaMode
                                 (parser_cfg->formula_options.generate_mode,
                                  (yyvsp[(2) - (2)].value));
                             }
    break;

  case 61:

/* Line 1464 of yacc.c  */
#line 440 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_GLOBALLY],
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 62:

/* Line 1464 of yacc.c  */
#line 449 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_IMPLICATION],
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 63:

/* Line 1464 of yacc.c  */
#line 458 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_NEXT],
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 64:

/* Line 1464 of yacc.c  */
#line 467 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_NEGATION],
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 65:

/* Line 1464 of yacc.c  */
#line 476 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_DISJUNCTION],
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 66:

/* Line 1464 of yacc.c  */
#line 485 "Config-parse.yy"
    {
                               parser_cfg->readFormulaMode
                                 (parser_cfg->formula_options.output_mode,
                                  (yyvsp[(2) - (2)].value));
                             }
    break;

  case 67:

/* Line 1464 of yacc.c  */
#line 492 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_ATOM],
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::ATOMIC_PRIORITY_RANGE);
                             }
    break;

  case 68:

/* Line 1464 of yacc.c  */
#line 501 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                             (parser_cfg->formula_options.
                                                formula_generator.
                                                number_of_available_variables,
                                              (yyvsp[(2) - (2)].value));
                             }
    break;

  case 69:

/* Line 1464 of yacc.c  */
#line 510 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->global_options.
                                                formula_random_seed,
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::RANDOM_SEED_RANGE);
                             }
    break;

  case 70:

/* Line 1464 of yacc.c  */
#line 519 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_V],
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 71:

/* Line 1464 of yacc.c  */
#line 528 "Config-parse.yy"
    {
                               parser_cfg->readSize
                                             (Configuration::OPT_FORMULASIZE,
                                              (yyvsp[(2) - (2)].value));
                             }
    break;

  case 72:

/* Line 1464 of yacc.c  */
#line 535 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_STRONG_RELEASE],
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 73:

/* Line 1464 of yacc.c  */
#line 544 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_TRUE],
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::ATOMIC_PRIORITY_RANGE);
                             }
    break;

  case 74:

/* Line 1464 of yacc.c  */
#line 553 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_UNTIL],
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 75:

/* Line 1464 of yacc.c  */
#line 562 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_WEAK_UNTIL],
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;

  case 76:

/* Line 1464 of yacc.c  */
#line 571 "Config-parse.yy"
    {
                               parser_cfg->readInteger
                                 (parser_cfg->formula_options.symbol_priority
                                    [::Ltl::LTL_XOR],
                                  (yyvsp[(2) - (2)].value),
                                  Configuration::OPERATOR_PRIORITY_RANGE);
                             }
    break;



/* Line 1464 of yacc.c  */
#line 2251 "Config-parse.cc"
      default: break;
    }
  YY_SYMBOL_PRINT ("-> $$ =", yyr1[yyn], &yyval, &yyloc);

  YYPOPSTACK (yylen);
  yylen = 0;
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
#if ! YYERROR_VERBOSE
      yyerror (YY_("syntax error"));
#else
      {
	YYSIZE_T yysize = yysyntax_error (0, yystate, yychar);
	if (yymsg_alloc < yysize && yymsg_alloc < YYSTACK_ALLOC_MAXIMUM)
	  {
	    YYSIZE_T yyalloc = 2 * yysize;
	    if (! (yysize <= yyalloc && yyalloc <= YYSTACK_ALLOC_MAXIMUM))
	      yyalloc = YYSTACK_ALLOC_MAXIMUM;
	    if (yymsg != yymsgbuf)
	      YYSTACK_FREE (yymsg);
	    yymsg = (char *) YYSTACK_ALLOC (yyalloc);
	    if (yymsg)
	      yymsg_alloc = yyalloc;
	    else
	      {
		yymsg = yymsgbuf;
		yymsg_alloc = sizeof yymsgbuf;
	      }
	  }

	if (0 < yysize && yysize <= yymsg_alloc)
	  {
	    (void) yysyntax_error (yymsg, yystate, yychar);
	    yyerror (yymsg);
	  }
	else
	  {
	    yyerror (YY_("syntax error"));
	    if (yysize != 0)
	      goto yyexhaustedlab;
	  }
      }
#endif
    }



  if (yyerrstatus == 3)
    {
      /* If just tried and failed to reuse lookahead token after an
	 error, discard it.  */

      if (yychar <= YYEOF)
	{
	  /* Return failure if at end of input.  */
	  if (yychar == YYEOF)
	    YYABORT;
	}
      else
	{
	  yydestruct ("Error: discarding",
		      yytoken, &yylval);
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

  /* Pacify compilers like GCC when the user code never invokes
     YYERROR and the label yyerrorlab therefore never appears in user
     code.  */
  if (/*CONSTCOND*/ 0)
     goto yyerrorlab;

  /* Do not reclaim the symbols of the rule which action triggered
     this YYERROR.  */
  YYPOPSTACK (yylen);
  yylen = 0;
  YY_STACK_PRINT (yyss, yyssp);
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


      yydestruct ("Error: popping",
		  yystos[yystate], yyvsp);
      YYPOPSTACK (1);
      yystate = *yyssp;
      YY_STACK_PRINT (yyss, yyssp);
    }

  *++yyvsp = yylval;


  /* Shift the error token.  */
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
  yyresult = 1;
  goto yyreturn;

#if !defined(yyoverflow) || YYERROR_VERBOSE
/*-------------------------------------------------.
| yyexhaustedlab -- memory exhaustion comes here.  |
`-------------------------------------------------*/
yyexhaustedlab:
  yyerror (YY_("memory exhausted"));
  yyresult = 2;
  /* Fall through.  */
#endif

yyreturn:
  if (yychar != YYEMPTY)
     yydestruct ("Cleanup: discarding lookahead",
		 yytoken, &yylval);
  /* Do not reclaim the symbols of the rule which action triggered
     this YYABORT or YYACCEPT.  */
  YYPOPSTACK (yylen);
  YY_STACK_PRINT (yyss, yyssp);
  while (yyssp != yyss)
    {
      yydestruct ("Cleanup: popping",
		  yystos[*yyssp], yyvsp);
      YYPOPSTACK (1);
    }
#ifndef yyoverflow
  if (yyss != yyssa)
    YYSTACK_FREE (yyss);
#endif
#if YYERROR_VERBOSE
  if (yymsg != yymsgbuf)
    YYSTACK_FREE (yymsg);
#endif
  /* Make sure YYID is used.  */
  return YYID (yyresult);
}



/* Line 1684 of yacc.c  */
#line 580 "Config-parse.yy"




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

