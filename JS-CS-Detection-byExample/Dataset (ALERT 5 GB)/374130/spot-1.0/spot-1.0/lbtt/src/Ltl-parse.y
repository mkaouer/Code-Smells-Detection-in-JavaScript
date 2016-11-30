/*
 *  Copyright (C) 2004, 2005, 2008
 *  Heikki Tauriainen <Heikki.Tauriainen@tkk.fi>
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

%{
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

%}



%name-prefix="ltl_"



/******************************************************************************
 *
 * Declarations for terminal and nonterminal symbols used in the grammar rules
 * below.
 *
 *****************************************************************************/

%union {
  class LtlFormula* formula;
}

/* Uninterpreted symbols. */

%token LTLPARSE_LPAR LTLPARSE_RPAR LTLPARSE_FALSE LTLPARSE_TRUE
       LTLPARSE_UNKNOWN

/* Atomic propositions. */

%token <formula> LTLPARSE_ATOM

/* Operators. */

%nonassoc LTLPARSE_UNTIL LTLPARSE_RELEASE LTLPARSE_WEAK_UNTIL
          LTLPARSE_STRONG_RELEASE LTLPARSE_BEFORE
%left LTLPARSE_IMPLY LTLPARSE_EQUIV LTLPARSE_XOR
%left LTLPARSE_OR
%left LTLPARSE_AND
%nonassoc LTLPARSE_NOT LTLPARSE_NEXT LTLPARSE_FINALLY LTLPARSE_GLOBALLY
%nonassoc LTLPARSE_EQUALS

/* Compound formulas. */

%type <formula> formula atomic_formula unary_formula prefix_op_formula
                binary_formula prefix_b_formula infix_b_formula



/******************************************************************************
 *
 * Grammar rule definitions.
 *
 *****************************************************************************/

%%

ltl_formula:         formula
                       { result = $1; }
                   ;

formula:             atomic_formula
                       { $$ = $1; }

                   | unary_formula
                       { $$ = $1; }

                   | binary_formula
                       { $$ = $1; }

                   | LTLPARSE_LPAR formula LTLPARSE_RPAR
                       { $$ = $2; }
                   ;

atomic_formula:      LTLPARSE_ATOM
                       { $$ = $1; }

                   | LTLPARSE_ATOM LTLPARSE_EQUALS LTLPARSE_FALSE
                       {
                         intermediate_results.erase($1);
                         $$ = newFormula(Not::construct($1));
                       }

                   | LTLPARSE_ATOM LTLPARSE_EQUALS LTLPARSE_TRUE
                       { $$ = $1; }

                   | LTLPARSE_FALSE
                       { $$ = newFormula(False::construct()); }

                   | LTLPARSE_TRUE
                       { $$ = newFormula(True::construct()); }
                   ;

unary_formula:       LTLPARSE_NOT formula
                       {
                         intermediate_results.erase($2);
                         $$ = newFormula(Not::construct($2));
                       }

                   | LTLPARSE_NEXT formula
                       {
                         intermediate_results.erase($2);
                         $$ = newFormula(Next::construct($2));
                       }

                   | LTLPARSE_FINALLY formula
                       {
                         intermediate_results.erase($2);
                         $$ = newFormula(Finally::construct($2));
                       }

                   | LTLPARSE_GLOBALLY formula
                       {
                         intermediate_results.erase($2);
                         $$ = newFormula(Globally::construct($2));
                       }
                   ;

prefix_op_formula:   atomic_formula
                       { $$ = $1; }

                   | unary_formula
                       { $$ = $1; }

                   | prefix_b_formula
                       { $$ = $1; }

                   | LTLPARSE_LPAR formula LTLPARSE_RPAR
                       { $$ = $2; }
                   ;

binary_formula:      prefix_b_formula
                       { $$ = $1; }
                   | infix_b_formula
                       { $$ = $1; }
                   ;

prefix_b_formula:    LTLPARSE_AND prefix_op_formula prefix_op_formula
                       {
                         intermediate_results.erase($2);
			 intermediate_results.erase($3);
                         $$ = newFormula(And::construct($2, $3));
                       }

                   | LTLPARSE_OR prefix_op_formula prefix_op_formula
                       {
                         intermediate_results.erase($2);
			 intermediate_results.erase($3);
                         $$ = newFormula(Or::construct($2, $3));
                       }

                   | LTLPARSE_IMPLY prefix_op_formula prefix_op_formula
                       {
                         intermediate_results.erase($2);
			 intermediate_results.erase($3);
                         $$ = newFormula(Imply::construct($2, $3));
                       }

                   | LTLPARSE_EQUIV prefix_op_formula prefix_op_formula
                       {
                         intermediate_results.erase($2);
			 intermediate_results.erase($3);
                         $$ = newFormula(Equiv::construct($2, $3));
                       }

                   | LTLPARSE_XOR prefix_op_formula prefix_op_formula
                       {
                         intermediate_results.erase($2);
			 intermediate_results.erase($3);
                         $$ = newFormula(Xor::construct($2, $3));
                       }

                   | LTLPARSE_UNTIL prefix_op_formula prefix_op_formula
                       {
                         intermediate_results.erase($2);
			 intermediate_results.erase($3);
                         $$ = newFormula(Until::construct($2, $3));
                       }

                   | LTLPARSE_RELEASE prefix_op_formula prefix_op_formula
                       {
                         intermediate_results.erase($2);
			 intermediate_results.erase($3);
                         $$ = newFormula(V::construct($2, $3));
                       }

                   | LTLPARSE_WEAK_UNTIL prefix_op_formula prefix_op_formula
                       {
                         intermediate_results.erase($2);
			 intermediate_results.erase($3);
                         $$ = newFormula(WeakUntil::construct($2, $3));
                       }

                   | LTLPARSE_STRONG_RELEASE prefix_op_formula
                     prefix_op_formula
                       {
                         intermediate_results.erase($2);
			 intermediate_results.erase($3);
                         $$ = newFormula(StrongRelease::construct($2, $3));
                       }

                   | LTLPARSE_BEFORE prefix_op_formula prefix_op_formula
                       {
                         intermediate_results.erase($2);
			 intermediate_results.erase($3);
                         $$ = newFormula(Before::construct($2, $3));
                       }
                   ;

infix_b_formula:     formula LTLPARSE_AND formula
                       {
                         intermediate_results.erase($1);
			 intermediate_results.erase($3);
                         $$ = newFormula(And::construct($1, $3));
                       }

                   | formula LTLPARSE_OR formula
                       {
                         intermediate_results.erase($1);
			 intermediate_results.erase($3);
                         $$ = newFormula(Or::construct($1, $3));
                       }

                   | formula LTLPARSE_IMPLY formula
                       {
                         intermediate_results.erase($1);
			 intermediate_results.erase($3);
                         $$ = newFormula(Imply::construct($1, $3));
                       }

                   | formula LTLPARSE_EQUIV formula
                       {
                         intermediate_results.erase($1);
			 intermediate_results.erase($3);
                         $$ = newFormula(Equiv::construct($1, $3));
                       }

                   | formula LTLPARSE_XOR formula
                       {
                         intermediate_results.erase($1);
			 intermediate_results.erase($3);
                         $$ = newFormula(Xor::construct($1, $3));
                       }

                   | formula LTLPARSE_UNTIL formula
                       {
                         intermediate_results.erase($1);
			 intermediate_results.erase($3);
                         $$ = newFormula(Until::construct($1, $3));
                       }

                   | formula LTLPARSE_RELEASE formula
                       {
                         intermediate_results.erase($1);
			 intermediate_results.erase($3);
                         $$ = newFormula(V::construct($1, $3));
                       }

                   | formula LTLPARSE_WEAK_UNTIL formula
                       {
                         intermediate_results.erase($1);
			 intermediate_results.erase($3);
                         $$ = newFormula(WeakUntil::construct($1, $3));
                       }

                   | formula LTLPARSE_STRONG_RELEASE formula
                       {
                         intermediate_results.erase($1);
			 intermediate_results.erase($3);
                         $$ = newFormula(StrongRelease::construct($1, $3));
                       }

                   | formula LTLPARSE_BEFORE formula
                       {
                         intermediate_results.erase($1);
			 intermediate_results.erase($3);
                         $$ = newFormula(Before::construct($1, $3));
                       }
                   ;

%%



/******************************************************************************
 *
 * Helper function for reading lexical tokens from a stream.
 *
 *****************************************************************************/

static inline size_t matchCharactersFromStream
  (istream& stream, const char* chars)
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
