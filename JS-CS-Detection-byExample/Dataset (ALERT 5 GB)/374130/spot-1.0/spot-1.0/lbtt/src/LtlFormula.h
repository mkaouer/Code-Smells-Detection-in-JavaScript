/*
 *  Copyright (C) 1999, 2000, 2001, 2002, 2003, 2004, 2005
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

#ifndef LTLFORMULA_H
#define LTLFORMULA_H

#include <config.h>
#include <deque>
#include <iostream>
#include <set>
#include <stack>
#include <string>
#include "LbttAlloc.h"
#include "BitArray.h"
#include "Exception.h"

using namespace std;

extern bool user_break;

namespace Ltl
{

/******************************************************************************
 *
 * Constants for identifying LTL formulae of various types.
 *
 *****************************************************************************/

enum FormulaType
  {LTL_TRUE = 't', LTL_FALSE = 'f', LTL_ATOM = 'p', LTL_NEGATION = '!',
   LTL_NEXT = 'X', LTL_FINALLY = 'F', LTL_GLOBALLY = 'G',
   LTL_CONJUNCTION = '&', LTL_DISJUNCTION = '|', LTL_IMPLICATION = 'i',
   LTL_EQUIVALENCE = 'e', LTL_XOR = '^', LTL_UNTIL = 'U', LTL_V = 'V',
   LTL_WEAK_UNTIL = 'W', LTL_STRONG_RELEASE = 'M', LTL_BEFORE = 'B'};

const char* infixSymbol(const int symbol);          /* Returns the infix
						     * symbol corresponding to
						     * a given FormulaType
						     * constant.
						     */



/******************************************************************************
 *
 * Constants for selecting between the traversal modes in the parse tree of an
 * LTL formula.
 *
 *****************************************************************************/

enum TraversalMode {LTL_PREORDER = 1, LTL_INORDER = 2, LTL_POSTORDER = 4};



/******************************************************************************
 *
 * Constants for selecting the notation in which to print an LtlFormula.
 *
 *****************************************************************************/

enum OutputMode {LTL_PREFIX, LTL_INFIX};



/******************************************************************************
 *
 * A base class for LTL formulae.
 *
 *****************************************************************************/

class LtlFormula
{
public:
  LtlFormula();                                     /* Creates a new LTL
                                                     * formula.
                                                     */

  virtual ~LtlFormula();                            /* Destructor. */

private:
  LtlFormula(const LtlFormula& f);                  /* Prevent copying and */
  LtlFormula& operator=(const LtlFormula& f);       /* assignment of
						     * LtlFormula objects.
						     */

public:
  static void destruct(LtlFormula* f);              /* Removes an LTL formula
						     * from `formula_storage'.
						     */

  virtual bool operator<(const LtlFormula& f)       /* ``Less than'' */
    const = 0;         			            /* comparison.   */

  LtlFormula* clone();                              /* Creates a copy of the
                                                     * formula object.
                                                     */

  LtlFormula* nnfClone();                           /* Creates a copy of the
						     * formula object in
						     * negation normal form.
						     */

  bool evaluate                                     /* These functions       */
    (const BitArray& valuation,                     /* evaluate the formula  */
     const unsigned long int valuation_size) const; /* in a given truth      */
                                                    /* assignment for the    */
  bool evaluate                                     /* atomic propositions.  */
    (const Bitset& valuation) const;                /* (The functions are
                                                     * intended to be used
                                                     * mainly with pure
						     * propositional
                                                     * formulae. If the
						     * formula contains
						     * temporal operators,
						     * calling one of these
						     * functions is
                                                     * interpreted as a
                                                     * request to evaluate
                                                     * the formula in a
						     * state space
						     * consisting of a
                                                     * single state having a
						     * transition to itself,
						     * with the given truth
						     * assignment for the
						     * atomic propositions.)
                                                     */

  bool propositional() const;                       /* Tells whether the
						     * formula contains any
						     * temporal operators.
						     */

  bool constant() const;                            /* Tells whether the
						     * formula is a constant,
						     * i.e., whether all its
						     * atoms are Boolean
						     * constants.
						     */

  unsigned long int size() const;                   /* Returns the number of
						     * nodes in the parse tree
						     * of the formula.
						     */

  long int maxAtom() const;                         /* Finds the largest
						     * identifier of the
						     * atomic propositions in
						     * the formula.
						     */

  bool satisfiable(long int max_atom = -1) const;   /* Tells whether the
						     * formula is satisfiable.
						     * (This test is only
						     * allowed if the formula
						     * is propositional.)
						     */

  Bitset findPropositionalModel                     /* Finds a model for a  */
    (long int max_atom = -1) const;                 /* purely propositional
						     * formula (if it is
						     * satisfiable).
						     */

  virtual FormulaType what() const = 0;             /* Tells the type of the
						     * formula.
						     */

  static LtlFormula* read(istream& stream);         /* Constructs an LtlFormula
						     * by parsing input from an
						     * input stream.
						     */

  void print                                        /* Writes the formula to */
    (ostream& stream = cout,                        /* an output stream.     */
     OutputMode mode = LTL_INFIX) const;

  void print                                        /* Writes the formula to */
    (Exceptional_ostream& stream,                   /* an exception-aware    */
     OutputMode mode) const;                        /* output stream.        */

  template<class F>                                 /* Performs a depth-   */
  void traverse(F& f, int mode) const;              /* first traversal on  */
                                                    /* the formula parse
    						     * tree, calling the
    						     * function object `f'
    						     * at each node.
    						     */

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  class ParseErrorException : public Exception      /* A class for reporting */
                                                    /* errors when           */
                                                    /* constructing an       */
                                                    /* LtlFormula by reading */
  {                                                 /* it from a stream.     */
  public:
    ParseErrorException(const string& msg);         /* Constructor. */

    /* default copy constructor */

    ~ParseErrorException() throw();                 /* Destructor. */

    ParseErrorException& operator=                  /* Assignment operator. */
      (const ParseErrorException& e);
  };

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  class ptr_less                                    /* Class for mapping    */
  {                                                 /* the `less than'      */
  public:                                           /* relation between two */
    bool operator()                                 /* pointers to          */
      (const LtlFormula* f1,                        /* LtlFormulae to the   */
       const LtlFormula* f2) const;                 /* corresponding        */
  };						    /* relation between the
						     * formulae itself.
						     */

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

protected:
  struct                                            /* These flags tell      */
  {                                                 /* whether the formula   */
    unsigned int is_propositional : 1;              /* contains any temporal */
    unsigned int is_constant      : 1;              /* operators and atomic  */
  } info_flags;                                     /* propositions.         */

  unsigned long int refcount;                       /* Number of references to
						     * `this' LtlFormula.
						     */

  static LtlFormula&                                /* Updates the shared   */
    insertToStorage(LtlFormula* f);                 /* formula storage with
						     * a new formula.
						     */

private:
  virtual bool eval                                 /* Helper function for   */
    (const BitArray& valuation) const = 0;          /* evaluating the
                                                     * formula in a given
						     * truth assignment.
                                                     */

  void collectSubformulae                           /* Builds a stack of the */
    (stack<const LtlFormula*,                       /* subformulae of the    */
           deque<const LtlFormula*> >&              /* formula.              */
       result_stack) const;

  typedef pair<bool, const LtlFormula*>             /* Shorthand type      */
    FormulaStackElement;                            /* definitions for the */
  typedef stack<FormulaStackElement,                /* propositional       */
                deque<FormulaStackElement> >        /* satisfiability      */
    FormulaStack;                                   /* checking algorithm. */
  typedef pair<FormulaStack, Bitset>
    TableauStackElement;
  typedef stack<TableauStackElement,
                deque<TableauStackElement> >
    TableauStack;

  bool sat_eval                                     /* Helper function for */
    (TableauStack& tableau_stack,                   /* testing the         */
     const long int max_atom)                       /* satisfiability of a */
    const;                                          /* propositional
                                                     * formula.
						     */

  static set<LtlFormula*, ptr_less>                 /* Shared storage for */
    formula_storage;                                /* LTL formulae.      */

  static unsigned long int                          /* Upper limit for the */
    eval_proposition_id_limit;                      /* atomic proposition
						     * identifiers (used
						     * when evaluating the
						     * truth value of the
						     * formula in a given
						     * truth assignment).
						     */

  friend class Atom;                                /* Friend declarations. */

  friend class LtlNegation;
  friend class LtlNext;
  friend class LtlFinally;
  friend class LtlGlobally;

  friend class LtlDisjunction;
  friend class LtlConjunction;
  friend class LtlImplication;
  friend class LtlEquivalence;
  friend class LtlXor;
  friend class LtlUntil;
  friend class LtlV;
  friend class LtlWeakUntil;
  friend class LtlStrongRelease;
  friend class LtlBefore;

  friend class PathEvaluator;

  template<class Operator>
  friend class UnaryFormula;

  template<class Operator>
  friend class BinaryFormula;
};



/******************************************************************************
 *
 * Interface to the formula parser.
 *
 *****************************************************************************/

extern LtlFormula* parseFormula(istream& stream);



/******************************************************************************
 *
 * A class for atomic propositions.
 *
 *****************************************************************************/

class Atom : public LtlFormula
{
public:
  static Atom& construct(long int a);               /* Creates a new atomic
						     * proposition.
						     */

private:
  explicit Atom(long int a);                        /* Constructor for an
                                                     * atomic proposition.
                                                     */

  ~Atom();                                          /* Destructor. */

  Atom(const Atom&);                                /* Prevent copying and */
  Atom& operator=(const Atom&);                     /* assignment of Atom
						     * objects.
						     */

public:
  long int getId() const;                           /* Returns the identifier
						     * of the atomic
						     * proposition.
						     */

  bool operator<(const LtlFormula& f) const;        /* ``Less than''
						     * comparison.
						     */

  FormulaType what() const;                         /* Returns the type of
						     * the formula, i.e. the
						     * constant LTL_ATOM.
						     */

  friend class LtlNegation;                         /* Friend declarations. */
  friend class LtlNext;
  friend class LtlFinally;
  friend class LtlGlobally;

  friend class LtlDisjunction;
  friend class LtlConjunction;
  friend class LtlImplication;
  friend class LtlEquivalence;
  friend class LtlXor;
  friend class LtlUntil;
  friend class LtlV;
  friend class LtlWeakUntil;
  friend class LtlStrongRelease;
  friend class LtlBefore;

  friend class PathEvaluator;

private:
  bool eval(const BitArray& valuation) const;       /* Returns the truth value
                                                     * of the atom in a given
                                                     * truth assignment.
                                                     */

  long int atom;                                    /* Identifier of a
                                                     * propositional variable.
                                                     */
};



/******************************************************************************
 *
 * A template class for Boolean constants. The class (`Symbol') used to
 * instantiate the template must satisfy the following requirements:
 *
 *    (1) The class contains two static public constant arrays of characters
 *        `prefix_symbol' and `infix_symbol' representing the textual symbols
 *        that should be used when printing the constant in prefix or infix
 *        notation, respectively.
 *
 *    (2) The class contains a static public constant attribute `type' of type
 *        FormulaType which can be used to identify the constant.
 *
 *    (3) The class contains a static public member function `eval' which can
 *        be called without any parameters. The function returns a truth value
 *        of the constant.
 *
 *****************************************************************************/

template<class Symbol>
class Constant : public LtlFormula
{
public:
  static Constant<Symbol>& construct();             /* Creates a Boolean
						     * constant.
						     */

private:
  Constant();                                       /* Constructor for a
                                                     * Boolean constant. 
                                                     */

  ~Constant();                                      /* Destructor. */

  Constant(const Constant<Symbol>&);                /* Prevent copying and */
  Constant<Symbol>& operator=                       /* assignment of       */
    (const Constant<Symbol>&);                      /* Constants.          */

public:
  bool operator<(const LtlFormula& f) const;        /* ``Less than''
						     * comparison.
						     */

  FormulaType what() const;                         /* Returns the type of
						     * the constant, i.e.
						     * Symbol::type.
						     */

  friend class LtlNegation;                         /* Friend declarations. */
  friend class LtlNext;
  friend class LtlFinally;
  friend class LtlGlobally;

  friend class LtlDisjunction;
  friend class LtlConjunction;
  friend class LtlImplication;
  friend class LtlEquivalence;
  friend class LtlXor;
  friend class LtlUntil;
  friend class LtlV;
  friend class LtlWeakUntil;
  friend class LtlStrongRelease;
  friend class LtlBefore;

  friend class PathEvaluator;

private:
  bool eval(const BitArray&) const;                 /* This function only tells
                                                     * the truth value of the
                                                     * constant. It is needed
                                                     * to satisfy the
                                                     * LtlFormula member
                                                     * function interface.
						     */
};



/******************************************************************************
 *
 * A class for the Boolean constant `true'.
 *
 *****************************************************************************/

class LtlTrue
{
public:
  static const char prefix_symbol[];                /* Symbols for printing */
  static const char infix_symbol[];                 /* the constant.        */

  static const FormulaType type = LTL_TRUE;         /* Type of the constant. */

  static bool eval();                               /* Returns the truth value
                                                     * of the constant.
						     */
};



/******************************************************************************
 *
 * A class for the Boolean constant `false'.
 *
 *****************************************************************************/

class LtlFalse
{
public:
  static const char prefix_symbol[];                /* Symbols for printing */
  static const char infix_symbol[];                 /* the constant.        */

  static const FormulaType type = LTL_FALSE;        /* Type of the constant. */

  static bool eval();                               /* Returns the truth value
                                                     * of the constant.
						     */
};



/******************************************************************************
 *
 * Type definitions for propositional constants to make them accessible
 * without using the template instantiation syntax.
 *
 *****************************************************************************/

typedef Constant<LtlTrue> True;
typedef Constant<LtlFalse> False;



/******************************************************************************
 *
 * A template class for LTL formulae consisting of a unary operator (e.g.
 * negation) and a subformula. The class `Operator' used to instantiate the
 * template must satisfy the following requirements:
 *
 *    (1) The class contains two static public constant arrays of characters
 *        `prefix_symbol' and `infix_symbol' representing the textual symbols
 *        that should be used when printing the operator in prefix or infix
 *        notation, respectively.
 *
 *    (2) The class contains a static public constant attribute `type' of type
 *        FormulaType which can be used to identify the exact type of the
 *        formula.
 *
 *    (3) The class contains a static public member function `eval' which can
 *        be called with two parameters:
 *          - a BitArray object assigning truth values to atomic propositions
 *          - a pointer to an LtlFormula object (the subformula associated with
 *            the unary operator).
 *        The function must return a truth value (assumed to be the truth value
 *        of the formula in the truth assignment given by the BitArray object).
 *
 *****************************************************************************/

template<class Operator>
class UnaryFormula : public LtlFormula
{
public:
  static UnaryFormula<Operator>& construct          /* Create a new unary */
    (LtlFormula* f);                                /* LTL formula.       */
  static UnaryFormula<Operator>& construct
    (LtlFormula& f);

private:
  UnaryFormula(LtlFormula* f);                      /* Constructor for unary
                                                     * LTL formulae.
                                                     */

  ~UnaryFormula();                                  /* Destructor. */

  UnaryFormula                                      /* Prevent copying and   */
    (const UnaryFormula<Operator>& f);              /* assignment of         */
  UnaryFormula<Operator>& operator=                 /* UnaryFormula objects. */
    (const UnaryFormula<Operator>& f);

public:
  bool operator<(const LtlFormula& f) const;        /* ``Less than''
                                                     * comparison.
						     */

  FormulaType what() const;                         /* Tells the type of the
						     * formula.
						     */

  friend class LtlFormula;                          /* Friend declarations. */

  friend class LtlNegation;
  friend class LtlNext;
  friend class LtlFinally;
  friend class LtlGlobally;

  friend class LtlDisjunction;
  friend class LtlConjunction;
  friend class LtlImplication;
  friend class LtlEquivalence;
  friend class LtlXor;
  friend class LtlUntil;
  friend class LtlV;
  friend class LtlWeakUntil;
  friend class LtlStrongRelease;
  friend class LtlBefore;

  friend class PathEvaluator;

private:
  bool eval(const BitArray& valuation) const;       /* Returns the truth value
                                                     * of the formula in a
                                                     * given truth assignment.
                                                     */

  LtlFormula* subformula;                           /* Pointer to the operand
                                                     * formula of the unary
                                                     * operator.
                                                     */
};



/******************************************************************************
 *
 * A class for negation operator.
 *
 *****************************************************************************/

class LtlNegation
{
public:
  static const char prefix_symbol[];                /* Symbols for printing */
  static const char infix_symbol[];                 /* the formula.         */

  static const FormulaType type = LTL_NEGATION;     /* Type of the formula. */

  static bool eval                                  /* Evaluates a negated  */
    (const BitArray& valuation,                     /* formula in a given   */
     const LtlFormula* f);                          /* truth assignment (a
                                                     * single-state state
                                                     * space).
                                                     */
};



/******************************************************************************
 *
 * A class for "Next" operator.
 *
 *****************************************************************************/

class LtlNext
{
public:
  static const char prefix_symbol[];                /* Symbols for printing */
  static const char infix_symbol[];                 /* the formula.         */

  static const FormulaType type = LTL_NEXT;         /* Type of the formula. */

  static bool eval                                  /* Evaluates a "Next"   */
    (const BitArray& valuation,                     /* formula in a given   */
     const LtlFormula* f);                          /* truth assignment (a
                                                     * single-state state
                                                     * space).
                                                     */
};



/******************************************************************************
 *
 * A class for "Finally" operator.
 *
 *****************************************************************************/

class LtlFinally
{
public:
  static const char prefix_symbol[];                /* Symbols for printing */
  static const char infix_symbol[];                 /* the formula.         */

  static const FormulaType type = LTL_FINALLY;      /* Type of the formula. */

  static bool eval                                  /* Evaluates a "Finally" */
    (const BitArray& valuation,                     /* formula in a given    */
     const LtlFormula* f);                          /* truth assignment (a
                                                     * single-state state
                                                     * space).
                                                     */
};



/******************************************************************************
 *
 * A class for "Globally" operator.
 *
 *****************************************************************************/

class LtlGlobally
{
public:
  static const char prefix_symbol[];                /* Symbols for printing */
  static const char infix_symbol[];                 /* the formula.         */

  static const FormulaType type = LTL_GLOBALLY;     /* Type of the formula. */

  static bool eval                                  /* Evaluates a           */
    (const BitArray& valuation,                     /* "Globally" formula in */
     const LtlFormula* f);                          /* a given truth
                                                     * assignment (a single-
                                                     * state state space).
                                                     */
};



/******************************************************************************
 *
 * Type definitions for unary LTL formulae to make them accessible without
 * using the template instantiation syntax.
 *
 *****************************************************************************/
 
typedef UnaryFormula<LtlNegation> Not;
typedef UnaryFormula<LtlNext> Next;
typedef UnaryFormula<LtlFinally> Finally;
typedef UnaryFormula<LtlGlobally> Globally;



/******************************************************************************
 *
 * A template class for LTL formulae consisting of a binary operator
 * (e.g. conjunction) with two subformulae. The class `Operator' used for
 * instantiating the template must satisfy the following requirements:
 *
 *    (1) The class contains two static public constant arrays of characters
 *        `prefix_symbol' and `infix_symbol' representing the textual symbols
 *        that should be used when printing the operator in prefix or infix
 *        notation, respectively.
 *
 *    (2) The class contains a static public constant attribute `type' of type
 *        FormulaType which can be used to identify the exact type of the
 *        formula.
 *
 *    (3) The class contains a static public member function `eval' which can
 *        be called with three parameters:
 *          - a BitArray object assigning truth values to atomic propositions
 *          - two pointers to LtlFormula objects (the subformulae associated
 *            with the binary operator)
 *        The function must return a truth value (assumed to be the truth value
 *        of the formula in the truth assignment given by the BitArray object).
 *
 *****************************************************************************/

template<class Operator>
class BinaryFormula : public LtlFormula
{
public:
  static BinaryFormula<Operator>& construct         /* Create a binary LTL */
    (LtlFormula* f1, LtlFormula* f2);               /* formula.            */
  static BinaryFormula<Operator>& construct
    (LtlFormula& f1, LtlFormula& f2);
  static BinaryFormula<Operator>& construct
    (LtlFormula* f1, LtlFormula& f2);
  static BinaryFormula<Operator>& construct
    (LtlFormula& f1, LtlFormula* f2);

private:
  BinaryFormula(LtlFormula* f1, LtlFormula* f2);    /* Constructor for a   */
                                                    /* binary LTL formula. */

  ~BinaryFormula();                                 /* Destructor. */

  BinaryFormula                                     /* Prevent copying and */
    (const BinaryFormula<Operator>& f);             /* assignment of       */
  BinaryFormula<Operator>& operator=                /* BinaryFormula       */
    (const BinaryFormula<Operator>& f);             /* objects.            */

public:
  bool operator<(const LtlFormula& f) const;        /* ``Less than''
                                                     * comparison.
						     */

  FormulaType what() const;                         /* Tells the type of the
						     * formula.
						     */

  friend class LtlFormula;                          /* Friend declarations. */

  friend class LtlNegation;
  friend class LtlNext;
  friend class LtlFinally;
  friend class LtlGlobally;

  friend class LtlDisjunction;
  friend class LtlConjunction;
  friend class LtlImplication;
  friend class LtlEquivalence;
  friend class LtlXor;
  friend class LtlUntil;
  friend class LtlV;
  friend class LtlWeakUntil;
  friend class LtlStrongRelease;
  friend class LtlBefore;

  friend class PathEvaluator;

private:
  bool eval(const BitArray& valuation) const;       /* Returns the truth value
						     * of the formula in a
						     * given truth assignment.
						     */

  LtlFormula* subformula1;                          /* Pointer to the first
                                                     * operand of the binary
                                                     * operator.
						     */

  LtlFormula* subformula2;                          /* Pointer to the second
                                                     * operand.
						     */
};



/******************************************************************************
 *
 * A class for disjunction operator.
 *
 *****************************************************************************/

class LtlDisjunction
{
public:
  static const char prefix_symbol[];                /* Symbols for printing */
  static const char infix_symbol[];                 /* the formula.         */

  static const FormulaType type = LTL_DISJUNCTION;  /* Type of the formula. */

  static bool eval                                  /* Evaluates the       */
    (const BitArray& valuation,                     /* disjunctive formula */
     const LtlFormula* f1,                          /* in a given truth    */
     const LtlFormula* f2);                         /* assignment.         */
};



/******************************************************************************
 *
 * A class for conjunction operator.
 *
 *****************************************************************************/

class LtlConjunction
{
public:
  static const char prefix_symbol[];                /* Symbols for printing */
  static const char infix_symbol[];                 /* the formula.         */

  static const FormulaType type = LTL_CONJUNCTION;  /* Type of the formula. */

  static bool eval                                  /* Evaluates the       */
    (const BitArray& valuation,                     /* conjunctive formula */
     const LtlFormula* f1,                          /* in a given truth    */
     const LtlFormula* f2);                         /* assignment.         */
};



/******************************************************************************
 *
 * A class for implication operator.
 *
 *****************************************************************************/

class LtlImplication
{
public:
  static const char prefix_symbol[];                /* Symbols for printing */
  static const char infix_symbol[];                 /* the formula.         */

  static const FormulaType type = LTL_IMPLICATION;  /* Type of the formula. */

  static bool eval                                  /* Evaluates the       */
    (const BitArray& valuation,                     /* implication formula */
     const LtlFormula* f1,                          /* in a given truth    */
     const LtlFormula* f2);                         /* assignment.         */
};



/******************************************************************************
 *
 * A class for equivalence operator.
 *
 *****************************************************************************/

class LtlEquivalence
{
public:
  static const char prefix_symbol[];                /* Symbols for printing */
  static const char infix_symbol[];                 /* the formula.         */

  static const FormulaType type = LTL_EQUIVALENCE;  /* Type of the formula. */

  static bool eval                                  /* Evaluates the       */
    (const BitArray& valuation,                     /* equivalence formula */
     const LtlFormula* f1,                          /* in a given truth    */
     const LtlFormula* f2);                         /* assignment.         */
};



/******************************************************************************
 *
 * A class for the ``exclusive or'' operator.
 *
 *****************************************************************************/

class LtlXor
{
public:
  static const char prefix_symbol[];                /* Symbols for printing */
  static const char infix_symbol[];                 /* the formula.         */

  static const FormulaType type = LTL_XOR;          /* Type of the formula. */

  static bool eval                                  /* Evaluates the ``xor'' */
    (const BitArray& valuation,                     /* formula in a given    */
     const LtlFormula* f1,                          /* truth assignment.     */
     const LtlFormula* f2);
};



/******************************************************************************
 *
 * A class for `Until' operator.
 *
 *****************************************************************************/

class LtlUntil
{
public:
  static const char prefix_symbol[];                /* Symbols for printing */
  static const char infix_symbol[];                 /* the formula.         */

  static const FormulaType type = LTL_UNTIL;        /* Type of the formula. */

  static bool eval                                  /* Evaluates the        */
    (const BitArray& valuation,                     /* "Until" formula in a */
     const LtlFormula*,                             /* given truth          */
     const LtlFormula* f2);                         /* assignment.          */
};



/******************************************************************************
 *
 * A class for `V' (the dual of `Until') operator.
 *
 *****************************************************************************/

class LtlV
{
public:
  static const char prefix_symbol[];                /* Symbols for printing */
  static const char infix_symbol[];                 /* the formula.         */

  static const FormulaType type = LTL_V;            /* Type of the formula. */

  static bool eval                                  /* Evaluates the "V"  */
    (const BitArray& valuation,                     /* formula in a given */
     const LtlFormula*,                             /* truth assignment.  */
     const LtlFormula* f2);
};



/******************************************************************************
 *
 * A class for `weak until' operator.
 *
 *****************************************************************************/

class LtlWeakUntil
{
public:
  static const char prefix_symbol[];                /* Symbols for printing */
  static const char infix_symbol[];                 /* the formula.         */

  static const FormulaType type = LTL_WEAK_UNTIL;   /* Type of the formula. */

  static bool eval                                  /* Evaluates the "weak */
    (const BitArray& valuation,                     /* until" formula in a */
     const LtlFormula* f1,                          /* given truth         */
     const LtlFormula* f2);                         /* assignment.         */
};



/******************************************************************************
 *
 * A class for `strong release' operator.
 *
 *****************************************************************************/

class LtlStrongRelease
{
public:
  static const char prefix_symbol[];                /* Symbols for printing */
  static const char infix_symbol[];                 /* the formula.         */

  static const FormulaType type                     /* Type of the formula. */
    = LTL_STRONG_RELEASE;

  static bool eval                                  /* Evaluates the "strong */
    (const BitArray& valuation,                     /* release" formula in a */
     const LtlFormula* f1,                          /* given truth           */
     const LtlFormula* f2);                         /* assignment.           */
};



/******************************************************************************
 *
 * A class for `before' operator.
 *
 *****************************************************************************/

class LtlBefore
{
public:
  static const char prefix_symbol[];                /* Symbols for printing */
  static const char infix_symbol[];                 /* the formula.         */

  static const FormulaType type = LTL_BEFORE;       /* Type of the formula. */

  static bool eval                                  /* Evaluates the         */
    (const BitArray& valuation,                     /* "before" formula in a */
     const LtlFormula*,                             /* given truth           */
     const LtlFormula* f2);                         /* assignment.           */
};



/******************************************************************************
 *
 * Type definitions for the different binary LTL formulae to make them
 * accessible without using the template instantiation syntax.
 *
 *****************************************************************************/

typedef BinaryFormula<LtlDisjunction> Or;
typedef BinaryFormula<LtlConjunction> And;
typedef BinaryFormula<LtlImplication> Imply;
typedef BinaryFormula<LtlEquivalence> Equiv;
typedef BinaryFormula<LtlXor> Xor;
typedef BinaryFormula<LtlUntil> Until;
typedef BinaryFormula<LtlV> V;
typedef BinaryFormula<LtlWeakUntil> WeakUntil;
typedef BinaryFormula<LtlStrongRelease> StrongRelease;
typedef BinaryFormula<LtlBefore> Before;



/******************************************************************************
 *
 * Inline function definitions for class LtlFormula.
 *
 *****************************************************************************/

/* ========================================================================= */
inline LtlFormula::LtlFormula() : refcount(1)
/* ----------------------------------------------------------------------------
 *
 * Description:    Constructor for class LtlFormula. Initializes the attributes
 *                 of the base class LtlFormula.
 *
 * Arguments:      None.
 *
 * Returns:        Nothing.
 *
 * --------------------------------------------------------------------------*/
{
}

/* ========================================================================= */
inline LtlFormula::~LtlFormula()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class LtlFormula. Deallocates the memory
 *                reserved for the formula.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline void LtlFormula::destruct(LtlFormula* f)
/* ----------------------------------------------------------------------------
 *
 * Description:   Updates the shared formula storage to remove one reference to
 *                the formula.  The formula object is deleted if the reference
 *                count becomes zero.
 *
 * Argument:      f  --  A pointer to the formula to be removed from the shared
 *                       storage.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (--f->refcount == 0)
  {
    formula_storage.erase(f);
    delete f;
  }
}

/* ========================================================================= */
inline LtlFormula* LtlFormula::clone()
/* ----------------------------------------------------------------------------
 *
 * Description:   Updates the shared formula storage to add a new reference to
 *                `this' LtlFormula.
 *
 * Arguments:     None.
 *
 * Returns:       A pointer to the same formula.
 *
 * ------------------------------------------------------------------------- */
{
  ++refcount;
  return this;
}

/* ========================================================================= */
inline bool LtlFormula::evaluate
  (const BitArray& valuation, const unsigned long int valuation_size) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Computes the truth value of the formula in a given truth
 *                assignment for the atomic propositions in the formula. This
 *                function can be used to evaluate purely propositional
 *                formulae without the requirement to evaluate the formula with
 *                respect to some state space. However, if the formula
 *                contains temporal operators, calling this function for the
 *                formula is equivalent to evaluating the formula in a state
 *                space consisting of a single state with a self-loop (with the
 *                given assignment for the propositions in the state).
 *
 * Arguments:     valuation       --  A reference to a constant BitArray
 *                                    representing the truth assignment.
 *                valuation_size  --  Number of propositions in the valuation
 *                                    (if the formula contains propositions
 *                                    with an identifier greater or equal to
 *                                    the limit, they are assumed to be false
 *                                    in the valuation).
 *
 * Returns:       The truth value of the formula.
 *
 * ------------------------------------------------------------------------- */
{
  eval_proposition_id_limit = valuation_size;
  return eval(valuation);
}

/* ========================================================================= */
inline bool LtlFormula::evaluate(const Bitset& valuation) const
/* ----------------------------------------------------------------------------
 *
 * Description:   See above.
 *
 * Argument:      valuation  --  A reference to a constant Bitset representing
 *                               the truth assignment.
 *
 * Returns:       The truth value of the formula.
 *
 * ------------------------------------------------------------------------- */
{
  return evaluate(valuation, valuation.capacity());
}

/* ========================================================================= */
inline bool LtlFormula::propositional() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells whether the formula contains any temporal operators.
 *
 * Arguments:     None.
 *
 * Returns:       true or false according to the test whether the formula is
 *                propositional or not.
 *
 * ------------------------------------------------------------------------- */
{
  return info_flags.is_propositional;
}

/* ========================================================================= */
inline bool LtlFormula::constant() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells whether the formula contains any non-constant atoms.
 *
 * Arguments:     None.
 *
 * Returns:       A truth value according to the test for the property.
 *
 * ------------------------------------------------------------------------- */
{
  return info_flags.is_constant;
}

/* ========================================================================= */
inline LtlFormula* LtlFormula::read(istream& stream)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructs an LtlFormula by parsing input from a stream.
 *
 * Arguments:     stream  --  A reference to an input stream.
 *
 * Returns:       The constructed LtlFormula.
 *
 * ------------------------------------------------------------------------- */
{
  return parseFormula(stream);
}

/* ========================================================================= */
inline void LtlFormula::print(ostream& stream, OutputMode mode) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes the formula to a stream.
 *
 * Arguments:     stream  --  A reference to an output stream.
 *                mode    --  Chooses between prefix and infix notation.
 * 
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  Exceptional_ostream estream(&stream, ios::badbit | ios::failbit);
  print(estream, mode);
}

/* ========================================================================= */
inline ostream& operator<<(ostream& stream, const LtlFormula& f)
/* ----------------------------------------------------------------------------
 *
 * Description:   Alternative method for printing an LTL formula by using the
 *                << operator.
 *
 * Arguments:     stream  --  A reference to the output stream to which the
 *                            formula should be printed.
 *                f       --  A reference to the constant LTL formula.
 *
 * Returns:       A reference to the output stream (to support chaining of the
 *                << operators).
 *
 * --------------------------------------------------------------------------*/
{
  f.print(stream);
  return stream;
}

/* ========================================================================= */
inline Exceptional_ostream& operator<<
  (Exceptional_ostream& stream, const LtlFormula& f)
/* ----------------------------------------------------------------------------
 *
 * Description:   Alternative method for printing an LTL formula by using the
 *                << operator.
 *
 * Arguments:     stream  --  A reference to the exception-aware output stream
 *                            to which the formula should be printed.
 *                f       --  A reference to the constant LTL formula.
 *
 * Returns:       A reference to the output stream (to support chaining of the
 *                << operators).
 *
 * --------------------------------------------------------------------------*/
{
  f.print(stream, LTL_INFIX);
  return stream;
}

/* ========================================================================= */
inline LtlFormula& LtlFormula::insertToStorage(LtlFormula* f)
/* ----------------------------------------------------------------------------
 *
 * Description:   Inserts a formula to the formula storage.
 *
 * Arguments:     f  --  The formula to be inserted.
 *
 * Returns:       A pointer to the formula.
 *
 * ------------------------------------------------------------------------- */
{
  set<LtlFormula*, ptr_less>::iterator inserter = formula_storage.find(f);
  if (inserter != formula_storage.end())
  {
    delete f;
    ++(*inserter)->refcount;
    return **inserter;
  }

  formula_storage.insert(f);
  return *f;
}



/******************************************************************************
 *
 * Template function definitions for class LtlFormula.
 *
 *****************************************************************************/

/* ========================================================================= */
template<class F>
void LtlFormula::traverse(F& f, int mode) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Implements a depth-first traversal algorithm in the parse
 *                tree of an LtlFormula.
 *
 * Arguments:     f     --  A reference to a function object implementing an
 *                          operation that will be performed in each node of
 *                          the parse tree of the formula. The class F must
 *                          define the member function
 *                           void operator()(const LtlFormula* f, int operand).
 *                          The function will then be called by the depth-first
 *                          traversal function such that
 *                           `f'
 *                             points to the subformula rooted at the node, and
 *
 *                           `operand' 
 *                             is an integer that can be used to determine the
 *                             state of the depth-first search. The possible
 *                             values are:
 *                               0  --  The depth-first has just entered the
 *                                      node from its parent node.
 *                               1  --  The depth-first search has traversed
 *                                      the left-hand subtree of the node and
 *                                      will proceed next to the right-hand
 *                                      subtree of the node. (`operand' may
 *                                      have this value only if the node has
 *                                      two children, i.e., if the node is
 *                                      associated with a binary operator.)
 *                               2  --  The depth-first search has traversed
 *                                      all subtrees of the node and will
 *                                      backtrack to the parent node.
 *                mode  --  A bitwise combination of the constants
 *                          `LTL_PREORDER', `LTL_INORDER' and `LTL_POSTORDER'.
 *                          The value of `mode' controls when the node
 *                          operation `f' should be performed. If
 *                          `mode & LTL_PREORDER != 0', the node operation will
 *                          be performed when the depth-first search has just
 *                          entered the node. If `mode & LTL_INORDER != 0' and
 *                          the node is associated with a binary operator, the
 *                          node operation will be performed when the depth-
 *                          first search is about to enter the right-hand
 *                          subtree of the node. If `mode & LTL_POSTORDER != 0'
 *                          the operation will be performed just before the
 *                          depth-first search backtracks from the node. (If
 *                          the current parse tree node is a leaf node, the
 *                          node operation is always performed once using the
 *                          value 0 for the `operand' parameter.)
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (what() == LTL_ATOM || what() == LTL_TRUE || what() == LTL_FALSE)
  {
    f(this, 0);
    return;
  }

  if (mode & LTL_PREORDER)
    f(this, 0);

  switch (what())
  {
    case LTL_NEGATION :
      static_cast<const Not*>(this)->subformula->traverse(f, mode);
      goto postorder_visit;

    case LTL_NEXT :
      static_cast<const Next*>(this)->subformula->traverse(f, mode);
      goto postorder_visit;

    case LTL_FINALLY :
      static_cast<const Finally*>(this)->subformula->traverse(f, mode);
      goto postorder_visit;

    case LTL_GLOBALLY :
      static_cast<const Globally*>(this)->subformula->traverse(f, mode);
      goto postorder_visit;

    case LTL_CONJUNCTION :
      static_cast<const And*>(this)->subformula1->traverse(f, mode);
      break;

    case LTL_DISJUNCTION :
      static_cast<const Or*>(this)->subformula1->traverse(f, mode);
      break;

    case LTL_IMPLICATION :
      static_cast<const Imply*>(this)->subformula1->traverse(f, mode);
      break;

    case LTL_EQUIVALENCE :
      static_cast<const Equiv*>(this)->subformula1->traverse(f, mode);
      break;

    case LTL_XOR :
      static_cast<const Xor*>(this)->subformula1->traverse(f, mode);
      break;

    case LTL_UNTIL :
      static_cast<const Equiv*>(this)->subformula1->traverse(f, mode);
      break;

    case LTL_V :
      static_cast<const V*>(this)->subformula1->traverse(f, mode);
      break;

    case LTL_WEAK_UNTIL :
      static_cast<const WeakUntil*>(this)->subformula1->traverse(f, mode);
      break;

    case LTL_STRONG_RELEASE :
      static_cast<const StrongRelease*>(this)->subformula1->traverse(f, mode);
      break;

    default : /* LTL_BEFORE */
      static_cast<const Before*>(this)->subformula1->traverse(f, mode);
      break;
  }

  if (mode & LTL_INORDER)
    f(this, 1);

  switch (what())
  {
    case LTL_CONJUNCTION :
      static_cast<const And*>(this)->subformula2->traverse(f, mode);
      break;

    case LTL_DISJUNCTION :
      static_cast<const Or*>(this)->subformula2->traverse(f, mode);
      break;

    case LTL_IMPLICATION :
      static_cast<const Imply*>(this)->subformula2->traverse(f, mode);
      break;

    case LTL_EQUIVALENCE :
      static_cast<const Equiv*>(this)->subformula2->traverse(f, mode);
      break;

    case LTL_XOR :
      static_cast<const Xor*>(this)->subformula2->traverse(f, mode);
      break;

    case LTL_UNTIL :
      static_cast<const Until*>(this)->subformula2->traverse(f, mode);
      break;

    case LTL_V :
      static_cast<const V*>(this)->subformula2->traverse(f, mode);
      break;

    case LTL_WEAK_UNTIL :
      static_cast<const WeakUntil*>(this)->subformula2->traverse(f, mode);
      break;

    case LTL_STRONG_RELEASE :
      static_cast<const StrongRelease*>(this)->subformula2->traverse(f, mode);
      break;

    default : /* LTL_BEFORE */
      static_cast<const Before*>(this)->subformula2->traverse(f, mode);
      break;
  }

postorder_visit:    
  if (mode & LTL_POSTORDER)
    f(this, 2);
}



/******************************************************************************
 *
 * Inline function definitions for class LtlFormula::ptr_less.
 *
 *****************************************************************************/

/* ========================================================================= */
inline bool LtlFormula::ptr_less::operator()
  (const LtlFormula* f1, const LtlFormula* f2) const
/* ----------------------------------------------------------------------------
 *
 * Description:   This function maps the `less than' relation between two
 *                pointers to LtlFormula objects to the corresponding relation
 *                between the formulae. Used when storing pointers to
 *                LtlFormulae into STL containers.
 *
 * Arguments:     f1, f2  --  Two pointers to LtlFormulae.
 *
 * Returns:       A truth value according to the relation between the formulae.
 *
 * ------------------------------------------------------------------------- */
{
  return f1->operator<(*f2);
}



/******************************************************************************
 *
 * Inline function definitions for class Atom.
 *
 *****************************************************************************/

/* ========================================================================= */
inline Atom& Atom::construct(long int a)
/* ----------------------------------------------------------------------------
 *
 * Description:   Inserts a new propositional atom into the shared formula
 *                storage.
 *
 * Arguments:     a  --  Identifier of the atom (>=0).
 *
 * Returns:       A reference to the atom.
 *
 * ------------------------------------------------------------------------- */
{
  if (a < 0)
    throw Exception("cannot construct an atomic proposition with a negative "
		    "id");

  return static_cast<Atom&>(LtlFormula::insertToStorage(new Atom(a)));
}

/* ========================================================================= */
inline Atom::Atom(long int a) : atom(a)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class Atom. Creates a new propositional atom.
 *
 * Argument:      a  --  Identifier of a propositional variable.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  info_flags.is_propositional = 1;
  info_flags.is_constant = 0;
}

/* ========================================================================= */
inline Atom::~Atom()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class Atom.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline long int Atom::getId() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the identifier of an atomic proposition.
 *
 * Arguments:     None.
 *
 * Returns:       Identifier of the atomic proposition.
 *
 * ------------------------------------------------------------------------- */
{
  return atom;
}

/* ========================================================================= */
inline bool Atom::operator<(const LtlFormula& f) const
/* ----------------------------------------------------------------------------
 *
 * Description:   ``Less than'' comparison between an Atom and an LtlFormula.
 *                An Atom is ``less than'' another LtlFormula if and only if
 *                the corresponding relation holds between the integer type
 *                identifiers between the formulae or if the other formula is
 *                also an Atom, but it refers to a propositional variable
 *                with a larger identifier.
 *
 * Arguments:     f  --  A reference to an LtlFormula.
 *
 * Returns:       A truth value according to the result of the comparison.
 *
 * ------------------------------------------------------------------------- */
{
  if (what() < f.what())
    return true;

  if (f.what() < what())
    return false;

  return atom < static_cast<const Atom&>(f).atom;
}

/* ========================================================================= */
inline FormulaType Atom::what() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the `formula type' of the atom, i.e. the constant
 *                LTL_ATOM.
 *
 * Arguments:     None.
 *
 * Returns:       The constant LTL_ATOM.
 *
 * ------------------------------------------------------------------------- */
{
  return LTL_ATOM;
}

/* ========================================================================= */
inline bool Atom::eval(const BitArray& valuation) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the truth value of the atom in a given truth
 *                assignment.
 *
 * Argument:      valuation  --  A reference to a constant BitArray
 *                               representing the truth assignment.
 *
 * Returns:       The truth value of the atom in the assignment. (If the
 *                atom identifier exceeds
 *                `LtlFormula::eval_proposition_id_limit' representing the
 *                assignment, the truth value of the atom is assumed to be
 *                false.)
 *
 * ------------------------------------------------------------------------- */
{
  return (static_cast<unsigned long int>(atom)
	    < LtlFormula::eval_proposition_id_limit
	  && valuation.test(atom));
}



/******************************************************************************
 *
 * Inline function definitions for template class Constant<Symbol>.
 *
 *****************************************************************************/

/* ========================================================================= */
template<class Symbol>
inline Constant<Symbol>& Constant<Symbol>::construct()
/* ----------------------------------------------------------------------------
 *
 * Description:   Inserts a new propositional constant into the shared formula
 *                storage.
 *
 * Arguments:     None.
 *
 * Returns:       A reference to the constant.
 *
 * ------------------------------------------------------------------------- */
{
  return static_cast<Constant<Symbol>&>
           (LtlFormula::insertToStorage(new Constant<Symbol>()));
}

/* ========================================================================= */
template<class Symbol>
inline Constant<Symbol>::Constant()
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for a LTL constant.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  info_flags.is_propositional = 1;
  info_flags.is_constant = 1;
}

/* ========================================================================= */
template<class Symbol>
inline Constant<Symbol>::~Constant()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for a LTL constant.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template<class Symbol>
inline bool Constant<Symbol>::operator<(const LtlFormula& f) const
/* ----------------------------------------------------------------------------
 *
 * Description:   ``Less than'' comparison between a constant and an
 *                LtlFormula. A constant is ``less than'' another LtlFormula if
 *                and only if the corresponding relation holds between the
 *                integer type identifiers between the formulae.
 *
 * Arguments:     f  --  A reference to an LtlFormula.
 *
 * Returns:       A truth value according to the result of the comparison.
 *
 * ------------------------------------------------------------------------- */
{
  return (what() < f.what());
}

/* ========================================================================= */
template<class Symbol>
inline FormulaType Constant<Symbol>::what() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the type of the constant.
 *
 * Arguments:     None.
 *
 * Returns:       Type of the constant as a value of type FormulaType.
 *
 * ------------------------------------------------------------------------- */
{
  return Symbol::type;
}

/* ========================================================================= */
template<class Symbol>
inline bool Constant<Symbol>::eval(const BitArray&) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Evaluates the constant in a truth assignment, i.e., simply
 *                returns the constant's truth value.
 *
 * Argument:      A reference to a constant BitArray (required in order to
 *                satisfy the class interface restrictions).
 *
 * Returns:       Truth value of the constant.
 *
 * ------------------------------------------------------------------------- */
{
  return Symbol::eval();
}



/******************************************************************************
 *
 * Inline function definitions for class True.
 *
 *****************************************************************************/

/* ========================================================================= */
inline bool LtlTrue::eval()
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the truth value of a `true' constant.
 *
 * Arguments:     None.
 *
 * Returns:       true
 *
 * ------------------------------------------------------------------------- */
{
  return true;
}



/******************************************************************************
 *
 * Inline function definitions for class False.
 *
 *****************************************************************************/

/* ========================================================================= */
inline bool LtlFalse::eval()
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the truth value of a `false' constant.
 *
 * Arguments:     None.
 *
 * Returns:       false
 *
 * ------------------------------------------------------------------------- */
{
  return false;
}



/******************************************************************************
 *
 * Inline function definitions for template class UnaryFormula<Operator>.
 *
 *****************************************************************************/

/* ========================================================================= */
template<class Operator>
inline UnaryFormula<Operator>& UnaryFormula<Operator>::construct(LtlFormula* f)
/* ----------------------------------------------------------------------------
 *
 * Description:   Inserts a new unary formula into the shared formula storage.
 *
 * Arguments:     f  --  A pointer to the subformula to be associated with the
 *                       unary formula. After the initialization, the new
 *                       object will ``own'' its subformula.
 *
 * Returns:       A reference to the formula.
 *
 * ------------------------------------------------------------------------- */
{
  return static_cast<UnaryFormula<Operator>&>
           (LtlFormula::insertToStorage(new UnaryFormula<Operator>(f)));
}

/* ========================================================================= */
template<class Operator>
inline UnaryFormula<Operator>& UnaryFormula<Operator>::construct(LtlFormula& f)
/* ----------------------------------------------------------------------------
 *
 * Description:   Inserts a new unary formula into the shared formula storage.
 *
 * Arguments:     f  --  A reference to a LtlFormula to be associated with the
 *                       unary formula.
 *
 * Returns:       A reference to the formula.
 *
 * ------------------------------------------------------------------------- */
{
  return static_cast<UnaryFormula<Operator>&>
           (LtlFormula::insertToStorage(new UnaryFormula<Operator>
					      (f.clone())));
}

/* ========================================================================= */
template<class Operator>
inline UnaryFormula<Operator>::UnaryFormula(LtlFormula* f) : subformula(f)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructs an LTL formula with a unary operator.
 *
 * Argument:      f  --  Pointer to the subformula of the operator.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  info_flags.is_propositional = (f->propositional()
				 && Operator::type == LTL_NEGATION);
  info_flags.is_constant = f->constant();
}

/* ========================================================================= */
template<class Operator>
inline UnaryFormula<Operator>::~UnaryFormula()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for unary LTL formulae. Releases the memory
 *                allocated for the operand of the unary operator.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  LtlFormula::destruct(subformula);
}

/* ========================================================================= */
template<class Operator>
inline bool UnaryFormula<Operator>::operator<(const LtlFormula& f) const
/* ----------------------------------------------------------------------------
 *
 * Description:   ``Less than'' comparison between a unary formula and an
 *                LtlFormula. A unary formula is ``less than'' another
 *                LtlFormula if and only if the corresponding relation holds
 *                between the integer type identifiers between the formulae,
 *                or if both formulae are of the same type and the relation
 *                holds between their subformulae.
 *
 * Arguments:     f  --  A reference to an LtlFormula.
 *
 * Returns:       A truth value according to the result of the comparison.
 *
 * ------------------------------------------------------------------------- */
{
  if (what() < f.what())
    return true;

  if (f.what() < what())
    return false;

  return subformula->operator<
           (*(static_cast<const UnaryFormula<Operator>&>(f).subformula));
}

/* ========================================================================= */
template<class Operator>
inline FormulaType UnaryFormula<Operator>::what() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the exact type of the unary formula.
 *
 * Arguments:     None.
 *
 * Returns:       Type of the formula (a FormulaType constant).
 *
 * ------------------------------------------------------------------------- */
{
  return Operator::type;
}

/* ========================================================================= */
template<class Operator>
inline bool UnaryFormula<Operator>::eval(const BitArray& valuation) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Evaluates the unary formula in a given truth assignment (in
 *                a state space consisting of a single state with a transition
 *                to itself).
 *
 * Argument:      A reference to a constant BitArray, defining the truth
 *                assignment.
 *
 * Returns:       Truth value of the formula in the assignment.
 *
 * ------------------------------------------------------------------------- */
{
  return Operator::eval(valuation, subformula);
}



/******************************************************************************
 *
 * Inline function definitions for class LtlNegation.
 *
 *****************************************************************************/

/* ========================================================================= */
inline bool LtlNegation::eval(const BitArray& valuation, const LtlFormula* f)
/* ----------------------------------------------------------------------------
 *
 * Description:   Evaluates the negated formula in a given assignment for the
 *                propositional variables.
 *
 * Arguments:     valuation  --  A reference to a constant BitArray
 *                               representing the truth assignment.
 *                f          --  A pointer to the subformula of the negation
 *                               operator.
 *
 * Returns:       The truth value of the formula in the truth assignment.
 *
 * ------------------------------------------------------------------------- */
{
  return (!f->eval(valuation));
}



/******************************************************************************
 *
 * Inline function definitions for class LtlNext.
 *
 *****************************************************************************/

/* ========================================================================= */
inline bool LtlNext::eval(const BitArray& valuation, const LtlFormula* f)
/* ----------------------------------------------------------------------------
 *
 * Description:   Evaluates the `Next' formula in a given truth assignment for
 *                the propositional variables (in a state space consisting of a
 *                single state with a transition to itself).
 *
 * Arguments:     valuation  --  A reference to a constant BitArray
 *                               representing the truth assignment.
 *                f          --  A pointer to the subformula of the `Next'
 *                               operator.
 *
 * Returns:       The truth value of the formula in the assignment.
 *
 * ------------------------------------------------------------------------- */
{
  return f->eval(valuation);
}



/******************************************************************************
 *
 * Inline function definitions for class LtlFinally.
 *
 *****************************************************************************/

/* ========================================================================= */
inline bool LtlFinally::eval(const BitArray& valuation, const LtlFormula* f)
/* ----------------------------------------------------------------------------
 *
 * Description:   Evaluates the `Finally' formula in a given truth assignment
 *                for the propositional variables (in a state space consisting
 *                of a single state with a transition to itself).
 *
 * Arguments:     valuation  --  A reference to a constant BitArray
 *                               representing the truth assignment.
 *                f          --  A pointer to the subformula of the `Finally'
 *                               operator.
 *
 * Returns:       The truth value of the formula in the assignment.
 *
 * ------------------------------------------------------------------------- */
{
  return f->eval(valuation);
}



/******************************************************************************
 *
 * Inline function definitions for class LtlGlobally.
 *
 *****************************************************************************/

/* ========================================================================= */
inline bool LtlGlobally::eval(const BitArray& valuation, const LtlFormula* f)
/* ----------------------------------------------------------------------------
 *
 * Description:   Evaluates the `Globally' formula in a given truth assignment
 *                for the propositional variables (in a state space consisting
 *                of a single state with a transition to itself).
 *
 * Arguments:     valuation  --  A reference to a constant BitArray
 *                               representing the truth assignment.
 *                f          --  A pointer to the subformula of the `Globally'
 *                               operator.
 *
 * Returns:       The truth value of the formula in the assignment.
 *
 * ------------------------------------------------------------------------- */
{
  return f->eval(valuation);
}



/******************************************************************************
 *
 * Inline function definitions for template class BinaryFormula<Operator>.
 *
 *****************************************************************************/

/* ========================================================================= */
template<class Operator>
inline BinaryFormula<Operator>&
BinaryFormula<Operator>::construct(LtlFormula* f1, LtlFormula* f2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Inserts a new binary formula into the shared formula storage.
 *
 * Arguments:     f1, f2  --  Pointers to the subformulae to be associated with
 *                            the binary formula. The new returned object will
 *                            ``own'' its both subformulae.
 *
 * Returns:       A reference to the formula.
 *
 * ------------------------------------------------------------------------- */
{
  return static_cast<BinaryFormula<Operator>&>
           (LtlFormula::insertToStorage(new BinaryFormula<Operator>(f1, f2)));
}

/* ========================================================================= */
template<class Operator>
inline BinaryFormula<Operator>&
BinaryFormula<Operator>::construct(LtlFormula& f1, LtlFormula& f2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Inserts a new binary formula into the shared formula storage.
 *
 * Arguments:     f1, f2  --  References to two LtlFormulae to be associated
 *                            with the binary formula.
 *
 * Returns:       A reference to the formula.
 *
 * ------------------------------------------------------------------------- */
{
  return static_cast<BinaryFormula<Operator>&>
           (LtlFormula::insertToStorage(new BinaryFormula<Operator>
					      (f1.clone(), f2.clone())));
}

/* ========================================================================= */
template<class Operator>
inline BinaryFormula<Operator>&
BinaryFormula<Operator>::construct(LtlFormula* f1, LtlFormula& f2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Inserts a new binary formula into the shared formula storage.
 *
 * Arguments:     f1  --  A pointer to a LtlFormula to be associated with the
 *                        binary formula (the ``left-hand'' subformula).
 *                f2  --  A reference to a LtlFormula to be associated with the
 *                        binary formula (the ``right-hand'' subformula).
 *
 * Returns:       A reference to the formula.
 *
 * ------------------------------------------------------------------------- */
{
  return static_cast<BinaryFormula<Operator>&>
           (LtlFormula::insertToStorage(new BinaryFormula<Operator>
					      (f1, f2.clone())));
}

/* ========================================================================= */
template<class Operator>
inline BinaryFormula<Operator>&
BinaryFormula<Operator>::construct(LtlFormula& f1, LtlFormula* f2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Inserts a new binary formula into the shared formula storage.
 *
 * Arguments:     f1  --  A reference to a LtlFormula to be associated with the
 *                        binary formula (the ``left-hand'' subformula).
 *                f2  --  A pointer to a LtlFormula to be associated with the
 *                        binary formula (the ``right-hand'' subformula).
 *
 * Returns:       A reference to the formula.
 *
 * ------------------------------------------------------------------------- */
{
  return static_cast<BinaryFormula<Operator>&>
           (LtlFormula::insertToStorage(new BinaryFormula<Operator>
					      (f1.clone(), f2)));
}

/* ========================================================================= */
template<class Operator>
inline BinaryFormula<Operator>::BinaryFormula(LtlFormula* f1, LtlFormula* f2) :
  subformula1(f1), subformula2(f2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructs a binary LTL formula.
 *
 * Arguments:     f1  --  Pointer to an LTL formula to be associated with the
 *                        new formula as its left operand.
 *                f2  --  Pointer to an LTL formula to be associated with the
 *                        new formula as its right operand.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  info_flags.is_propositional = (f1->propositional()
				 && f2->propositional()
				 && Operator::type != LTL_UNTIL
				 && Operator::type != LTL_V
				 && Operator::type != LTL_WEAK_UNTIL
				 && Operator::type != LTL_STRONG_RELEASE
				 && Operator::type != LTL_BEFORE);
  info_flags.is_constant = (f1->constant() && f2->constant());
}

/* ========================================================================= */
template<class Operator>
inline BinaryFormula<Operator>::~BinaryFormula()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for binary LTL formulae. Releases the memory
 *                allocated for the operands of the binary operator.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  LtlFormula::destruct(subformula1);
  LtlFormula::destruct(subformula2);
}

/* ========================================================================= */
template<class Operator>
inline bool BinaryFormula<Operator>::operator<(const LtlFormula& f) const
/* ----------------------------------------------------------------------------
 *
 * Description:   ``Less than'' comparison between a binary formula and an
 *                LtlFormula. A binary formula is ``less than'' another
 *                LtlFormula if and only if
 *                    (1)  the corresponding relation holds between the integer
 *                         type identifiers between the formulae;
 *                 OR (2)  both formulae are of the same type and the relation
 *                         holds between their ``left-hand'' subformulae;
 *                 OR (3)  both formulae are of the same type, their
 *                         ``left-hand'' subformulae are of the same type, and
 *                         the relation holds between the ``right-hand''
 *                         subformulae
 *
 * Arguments:     f  --  A reference to an LtlFormula.
 *
 * Returns:       A truth value according to the result of the comparison.
 *
 * ------------------------------------------------------------------------- */
{
  if (what() < f.what())
    return true;

  if (f.what() < what())
    return false;

  const BinaryFormula<Operator>& ff
    = static_cast<const BinaryFormula<Operator>&>(f);

  if (subformula1->operator<(*ff.subformula1))
    return true;

  if (ff.subformula1->operator<(*subformula1))
    return false;

  return (subformula2->operator<(*ff.subformula2));
}

/* ========================================================================= */
template<class Operator>
inline FormulaType BinaryFormula<Operator>::what() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the exact type of the binary formula.
 *
 * Arguments:     None.
 *
 * Returns:       Type of the formula (a FormulaType constant).
 *
 * ------------------------------------------------------------------------- */
{
  return Operator::type;
}

/* ========================================================================= */
template<class Operator>
inline bool BinaryFormula<Operator>::eval(const BitArray& valuation) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Evaluates the binary formula in a given truth assignment (in
 *                a state space consisting of a single state with a transition
 *                to itself).
 *
 * Argument:      A reference to a constant BitArray, defining the truth
 *                assignment.
 *
 * Returns:       Truth value of the formula in the assignment.
 *
 * ------------------------------------------------------------------------- */
{
  return Operator::eval(valuation, subformula1, subformula2);
}



/******************************************************************************
 *
 * Inline function definitions for class LtlDisjunction.
 *
 *****************************************************************************/

/* ========================================================================= */
inline bool LtlDisjunction::eval
  (const BitArray& valuation, const LtlFormula* f1, const LtlFormula* f2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Evaluates the disjunctive formula in a given assignment for
 *                the propositional variables.
 *
 * Arguments:     valuation  --  A reference to a constant BitArray
 *                               representing the truth assignment.
 *                f1, f2     --  Pointers to the subformulae of the disjunction
 *                               operator.
 *
 * Returns:       The truth value of the formula in the truth assignment.
 *
 * ------------------------------------------------------------------------- */
{
  return (f1->eval(valuation) || f2->eval(valuation));
}



/******************************************************************************
 *
 * Inline function definitions for class LtlConjunction.
 *
 *****************************************************************************/

/* ========================================================================= */
inline bool LtlConjunction::eval
  (const BitArray& valuation, const LtlFormula* f1, const LtlFormula* f2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Evaluates the conjunctive formula in a given assignment for
 *                the propositional variables.
 *
 * Arguments:     valuation  --  A reference to a constant BitArray
 *                               representing the truth assignment.
 *                f1, f2     --  Pointers to the subformulae of the conjunction
 *                               operator.
 *
 * Returns:       The truth value of the formula in the truth assignment.
 *
 * ------------------------------------------------------------------------- */
{
  return (f1->eval(valuation) && f2->eval(valuation));
}



/******************************************************************************
 *
 * Inline function definitions for class LtlImplication.
 *
 *****************************************************************************/

/* ========================================================================= */
inline bool LtlImplication::eval
  (const BitArray& valuation, const LtlFormula* f1, const LtlFormula* f2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Evaluates the implication in a given assignment for the
 *                propositional variables.
 *
 * Arguments:     valuation  --  A reference to a constant BitArray
 *                               representing the truth assignment.
 *                f1, f2     --  Pointers to the subformulae of the implication
 *                               operator.
 *
 * Returns:       The truth value of the formula in the truth assignment.
 *
 * ------------------------------------------------------------------------- */
{
  return (!f1->eval(valuation) || f2->eval(valuation));
}



/******************************************************************************
 *
 * Inline function definitions for class LtlEquivalence.
 *
 *****************************************************************************/

/* ========================================================================= */
inline bool LtlEquivalence::eval
  (const BitArray& valuation, const LtlFormula* f1, const LtlFormula* f2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Evaluates the equivalence in a given assignment for the
 *                propositional variables.
 *
 * Arguments:     valuation  --  A reference to a constant BitArray
 *                               representing the truth assignment.
 *                f1, f2     --  Pointers to the subformulae of the equivalence
 *                               operator.
 *
 * Returns:       The truth value of the formula in the truth assignment.
 *
 * ------------------------------------------------------------------------- */
{
  return (f1->eval(valuation) == f2->eval(valuation));
}



/******************************************************************************
 *
 * Inline function definitions for class LtlXor.
 *
 *****************************************************************************/

/* ========================================================================= */
inline bool LtlXor::eval
  (const BitArray& valuation, const LtlFormula* f1, const LtlFormula* f2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Evaluates the exclusive disjunction in a given assignment for
 *                the atomic propositions.
 *
 * Arguments:     valuation  --  A reference to a constant BitArray
 *                               representing the truth assignment.
 *                f1, f2     --  Pointers to the subformulae of the ``exclusive
 *                               or'' operator.
 *
 * Returns:       The truth value of the formula in the truth assignment.
 *
 * ------------------------------------------------------------------------- */
{
  return (f1->eval(valuation) != f2->eval(valuation));
}



/******************************************************************************
 *
 * Inline function definitions for class LtlUntil.
 *
 *****************************************************************************/

/* ========================================================================= */
inline bool LtlUntil::eval
  (const BitArray& valuation, const LtlFormula*, const LtlFormula* f2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Evaluates the `Until' formula in a given truth assignment
 *                for the propositional variables (in a state space consisting
 *                of a single state with a transition to itself).
 *
 * Arguments:     valuation  --  A reference to a constant BitArray
 *                               representing the truth assignment.
 *                f1, f2     --  Pointers to the subformulae of the `Until'
 *                               operator.
 *
 * Returns:       The truth value of the formula in the assignment.
 *
 * ------------------------------------------------------------------------- */
{
  return f2->eval(valuation);
}



/******************************************************************************
 *
 * Inline function definitions for class LtlV.
 *
 *****************************************************************************/

/* ========================================================================= */
inline bool LtlV::eval
  (const BitArray& valuation, const LtlFormula*, const LtlFormula* f2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Evaluates the `Release' formula in a given truth assignment
 *                for the propositional variables (in a state space consisting
 *                of a single state with a transition to itself).
 *
 * Arguments:     valuation  --  A reference to a constant BitArray
 *                               representing the truth assignment.
 *                f1, f2     --  A pointer to the subformula of the `V'
 *                               operator.
 *
 * Returns:       The truth value of the formula in the assignment.
 *
 * ------------------------------------------------------------------------- */
{
  return f2->eval(valuation);
}



/******************************************************************************
 *
 * Inline function definitions for class LtlWeakUntil.
 *
 *****************************************************************************/

/* ========================================================================= */
inline bool LtlWeakUntil::eval
  (const BitArray& valuation, const LtlFormula* f1, const LtlFormula* f2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Evaluates the `Weak until' formula in a given truth
 *                assignment for the propositional variables (in a state space
 *                consisting of a single state with a transition to itself).
 *
 * Arguments:     valuation  --  A reference to a constant BitArray
 *                               representing the truth assignment.
 *                f1, f2     --  Pointers to the subformulae of the `Weak
 *                               until' operator.
 *
 * Returns:       The truth value of the formula in the assignment.
 *
 * ------------------------------------------------------------------------- */
{
  return (f1->eval(valuation) || f2->eval(valuation));
}



/******************************************************************************
 *
 * Inline function definitions for class LtlStrongRelease.
 *
 *****************************************************************************/

/* ========================================================================= */
inline bool LtlStrongRelease::eval
  (const BitArray& valuation, const LtlFormula* f1, const LtlFormula* f2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Evaluates the `Strong release' formula in a given truth
 *                assignment for the propositional variables (in a state space
 *                consisting of a single state with a transition to itself).
 *
 * Arguments:     valuation  --  A reference to a constant BitArray
 *                               representing the truth assignment.
 *                f1, f2     --  Pointers to the subformulae of the `String
 *                               release' operator.
 *
 * Returns:       The truth value of the formula in the assignment.
 *
 * ------------------------------------------------------------------------- */
{
  return (f1->eval(valuation) && f2->eval(valuation));
}



/******************************************************************************
 *
 * Inline function definitions for class LtlBefore.
 *
 *****************************************************************************/

/* ========================================================================= */
inline bool LtlBefore::eval
  (const BitArray& valuation, const LtlFormula*, const LtlFormula* f2)
/* ----------------------------------------------------------------------------
 *
 * Description:   Evaluates the `Before' formula in a given truth assignment
 *                for the propositional variables (in a state space consisting
 *                of a single state with a transition to itself).
 *
 * Arguments:     valuation  --  A reference to a constant BitArray
 *                               representing the truth assignment.
 *                f1, f2     --  Pointers to the subformulae of the `String
 *                               release' operator.
 *
 * Returns:       The truth value of the formula in the assignment.
 *
 * ------------------------------------------------------------------------- */
{
  return !f2->eval(valuation);
}



/******************************************************************************
 *
 * Inline function definitions for class LtlFormula::ParseErrorException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline LtlFormula::ParseErrorException::ParseErrorException
  (const string& msg) :
  Exception(msg)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class LtlFormula::ParseErrorException.
 *                Initializes an exception object with an error message.
 *
 * Argument:      msg  --  Error message.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline LtlFormula::ParseErrorException::~ParseErrorException() throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class LtlFormula::ParseErrorException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline LtlFormula::ParseErrorException&
LtlFormula::ParseErrorException::operator=(const ParseErrorException& e)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class
 *                LtlFormula::ParseErrorException.
 *
 * Argument:      e  --  A reference to a constant exception object of the
 *                       same type.
 *
 * Returns:       A reference to the exception object assigned to.
 *
 * ------------------------------------------------------------------------- */
{
  Exception::operator=(e);
  return *this;
}

}

#endif /* !LTLFORMULA_H */
