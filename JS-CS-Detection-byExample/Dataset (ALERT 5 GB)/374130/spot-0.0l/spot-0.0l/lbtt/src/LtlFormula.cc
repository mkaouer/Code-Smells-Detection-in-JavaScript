/*
 *  Copyright (C) 1999, 2000, 2001, 2002, 2003
 *  Heikki Tauriainen <Heikki.Tauriainen@hut.fi>
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

#ifdef __GNUC__
#pragma implementation
#endif /* __GNUC__ */

#include <config.h>
#include "FormulaWriter.h"
#include "LtlFormula.h"

namespace Ltl
{

map<LtlFormula*, unsigned long int,                 /* Shared storage for */
    LtlFormula::ptr_less,                           /* LTL formulae.      */
    ALLOC(unsigned long int) >
  LtlFormula::formula_storage;

unsigned long int                                   /* Upper limit for the */
  LtlFormula::eval_proposition_id_limit;            /* atomic proposition
						     * identifiers (used
						     * when evaluating the
						     * truth value of the
						     * formula in a given
						     * truth assignment).
						     */



/******************************************************************************
 *
 * Function for obtaining the infix symbol associated with a given
 * FormulaType constant.
 *
 *****************************************************************************/

/* ========================================================================= */
const char* infixSymbol(const int symbol)
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the infix symbol corresponding to a given FormulaType
 *                constant.
 *
 * Argument:      symbol  --  A symbol identifier.
 *
 * Returns:       A pointer to a C-style string containing the symbol (or
 *                "UNKNOWN" if the argument does not correspond to a valid
 *                FormulaType symbol).
 *
 * ------------------------------------------------------------------------- */
{
  switch (symbol)
  {
    case LTL_TRUE :
      return static_cast<const char*>(LtlTrue::infix_symbol);

    case LTL_FALSE :
      return static_cast<const char*>(LtlFalse::infix_symbol);

    case LTL_ATOM :
      return "p";

    case LTL_NEGATION :
      return static_cast<const char*>(LtlNegation::infix_symbol);

    case LTL_CONJUNCTION :
      return static_cast<const char*>(LtlConjunction::infix_symbol);

    case LTL_DISJUNCTION :	  
      return static_cast<const char*>(LtlDisjunction::infix_symbol);

    case LTL_IMPLICATION :
      return static_cast<const char*>(LtlImplication::infix_symbol);

    case LTL_EQUIVALENCE :
      return static_cast<const char*>(LtlEquivalence::infix_symbol);

    case LTL_XOR :
      return static_cast<const char*>(LtlXor::infix_symbol);

    case LTL_NEXT :
      return static_cast<const char*>(LtlNext::infix_symbol);

    case LTL_UNTIL :
      return static_cast<const char*>(LtlUntil::infix_symbol);

    case LTL_WEAK_UNTIL :
      return static_cast<const char*>(LtlWeakUntil::infix_symbol);

    case LTL_FINALLY :
      return static_cast<const char*>(LtlFinally::infix_symbol);

    case LTL_V :
      return static_cast<const char*>(LtlV::infix_symbol);

    case LTL_STRONG_RELEASE :
      return static_cast<const char*>(LtlStrongRelease::infix_symbol);

    case LTL_GLOBALLY :
      return static_cast<const char*>(LtlGlobally::infix_symbol);

    case LTL_BEFORE :
      return static_cast<const char*>(LtlBefore::infix_symbol);

    default :
      return "UNKNOWN";
  }
}



/******************************************************************************
 *
 * A function class for convering an LtlFormula into negation normal form.
 *
 *****************************************************************************/

class NnfConverter
{
public:
  NnfConverter();                                   /* Constructor. */

  ~NnfConverter();                                  /* Destructor. */

  void operator()                                   /* Implements the        */
    (const LtlFormula* f, int operand);             /* conversion operation. */

  LtlFormula* getResult() const;                    /* Returns the result of
						     * the conversion.
						     */

private:
  stack<LtlFormula*,
        deque<LtlFormula*, ALLOC(LtlFormula*) > >
    formula_stack;

  stack<bool, deque<bool, ALLOC(bool) > >
    negation_stack;

  NnfConverter(const NnfConverter&);                /* Prevent copying and   */
  NnfConverter& operator=(const NnfConverter&);     /* assignment of
						     * NnfConverter objects.
						     */
};



/******************************************************************************
 *
 * A function class for computing the size of the parse tree of a formula.
 *
 *****************************************************************************/

class FormulaSizeCounter
{
public:
  FormulaSizeCounter();                             /* Constructor. */

  ~FormulaSizeCounter();                            /* Destructor. */

  void operator()(const LtlFormula*, int);          /* Implements the node
						     * counting operation.
						     */

  unsigned long int size;                           /* Node count. */

private:
  FormulaSizeCounter(const FormulaSizeCounter&);    /* Prevent copying and */
  FormulaSizeCounter& operator=                     /* assignment of       */
    (const FormulaSizeCounter&);                    /* FormulaSizeCounter
						     * objects.
						     */
};



/******************************************************************************
 *
 * A function class for collecting the subformulae of an LtlFormula into a
 * stack.
 *
 *****************************************************************************/

class SubformulaCollector
{
public:
  SubformulaCollector                               /* Constructor. */
    (stack<const LtlFormula*,
           deque<const LtlFormula*,
                 ALLOC(const LtlFormula*) > >&
       result_stack);

  ~SubformulaCollector();                           /* Destructor. */

  void operator()(const LtlFormula* f, int);        /* Implements the
						     * subformula collection
						     * operation.
						     */

private:
  stack<const LtlFormula*,                          /* Stack of subformulae. */
        deque<const LtlFormula*,
              ALLOC(const LtlFormula*) > >&
    subformula_stack;

  SubformulaCollector(const SubformulaCollector&);  /* Prevent copying and */
  SubformulaCollector& operator=                    /* assignment of       */
    (const SubformulaCollector&);                   /* SubformulaCollector
						     * objects.
						     */
};



/******************************************************************************
 *
 * A function class for finding the largest atom identifier in a LtlFormula.
 *
 *****************************************************************************/

class MaxAtomFinder
{
public:
  MaxAtomFinder();                                  /* Constuctor. */

  ~MaxAtomFinder();                                 /* Destructor. */

  void operator()(const LtlFormula* f, int);        /* Implements the atom
						     * identifier search
						     * operation.
						     */

  long int getResult() const;                       /* Returns the result of
						     * the operation.
						     */

private:
  long int max_atom_id;                             /* Largest identifier for
						     * an atom in an
						     * LtlFormula.
						     */

  MaxAtomFinder(const SubformulaCollector&);        /* Prevent copying and */
  MaxAtomFinder& operator=                          /* assignment of       */
    (const MaxAtomFinder&);                         /* MaxAtomFinder
						     * objects.
						     */
};



/******************************************************************************
 *
 * Inline function definitions for class NnfConverter.
 *
 *****************************************************************************/

/* ========================================================================= */
inline NnfConverter::NnfConverter()
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class NnfConverter.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  negation_stack.push(true);
}

/* ========================================================================= */
inline NnfConverter::~NnfConverter()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class NnfConverter.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  while (!formula_stack.empty())
  {
    LtlFormula::destruct(formula_stack.top());
    formula_stack.pop();
  }
}

/* ========================================================================= */
inline LtlFormula* NnfConverter::getResult() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the result of negation normal form conversion or
 *                `static_cast<LtlFormula*>(0)' if there is no result
 *                available.
 *
 * Arguments:     None.
 *
 * Returns:       A pointer to the result of the conversion.
 *
 * ------------------------------------------------------------------------- */
{
  if (!formula_stack.empty())
    return formula_stack.top()->clone();
  else
    return static_cast<LtlFormula*>(0);
}



/******************************************************************************
 *
 * Inline function definitions for class FormulaSizeCounter.
 *
 *****************************************************************************/

/* ========================================================================= */
inline FormulaSizeCounter::FormulaSizeCounter() :
  size(0)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class FormulaSizeCounter.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline FormulaSizeCounter::~FormulaSizeCounter()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class FormulaSizeCounter.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline void FormulaSizeCounter::operator()(const LtlFormula*, int)
/* ----------------------------------------------------------------------------
 *
 * Description:   Implementation for the parse tree node counting operation.
 *
 * Arguments:     The arguments are needed only for supporting the function
 *                interface.
 *
 * Returns:       Nothing; increments `this->size' by 1.
 *
 * ------------------------------------------------------------------------- */
{
  ++size;
}



/******************************************************************************
 *
 * Inline function definitions for class SubformulaCollector.
 *
 *****************************************************************************/

/* ========================================================================= */
inline SubformulaCollector::SubformulaCollector
  (stack<const LtlFormula*, deque<const LtlFormula*,
                                  ALLOC(const LtlFormula*) > >&
     result_stack) :
  subformula_stack(result_stack)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class SubformulaCollector.
 *
 * Arguments:     result_stack  --  A stack of constant LtlFormulae for
 *                                  collecting the results.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline SubformulaCollector::~SubformulaCollector()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class SubformulaCollector.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline void SubformulaCollector::operator()(const LtlFormula* f, int)
/* ----------------------------------------------------------------------------
 *
 * Description:   Implementation for the subformula collection operation.
 *
 * Arguments:     f  --  A pointer to a constant LtlFormula.
 *                The other argument is needed only for supporting the function
 *                interface.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  subformula_stack.push(f);
}



/******************************************************************************
 *
 * Inline function definitions for class MaxAtomFinder.
 *
 *****************************************************************************/

/* ========================================================================= */
inline MaxAtomFinder::MaxAtomFinder() :
  max_atom_id(-1)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class MaxAtomFinder.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline MaxAtomFinder::~MaxAtomFinder()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class MaxAtomFinder.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline void MaxAtomFinder::operator()(const LtlFormula* f, int)
/* ----------------------------------------------------------------------------
 *
 * Description:   Implements the search for the largest atomic proposition in
 *                an LtlFormula.
 *
 * Arguments:     f  --  A pointer to a constant LtlFormula.
 *                The other argument is needed only for supporting the function
 *                interface.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (f->what() == LTL_ATOM
      && static_cast<const Atom*>(f)->getId() > max_atom_id)
    max_atom_id = static_cast<const Atom*>(f)->getId();
}

/* ========================================================================= */
inline long int MaxAtomFinder::getResult() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the largest identifier of an atomic proposition
 *                found by the MaxAtomFinder object.
 *
 * Arguments:     None.
 *
 * Returns:       Largest identifier for an atomic proposition in an
 *                LtlFormula or -1 if the formula does not contain any
 *                atomic propositions.
 *
 * ------------------------------------------------------------------------- */
{
  return max_atom_id;
}



/******************************************************************************
 *
 * Function definitions for class NnfConverter.
 *
 *****************************************************************************/

/* ========================================================================= */
void NnfConverter::operator()(const LtlFormula* f, int operand)
/* ----------------------------------------------------------------------------
 *
 * Description:   Implements the negation normal form conversion for an
 *                LtlFormula.
 *
 * Arguments:     f        --  A pointer to a constant LtlFormula to be
 *                             converted into negation normal form.
 *                operand  --  
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (operand == 0)
  {
    switch (f->what())
    {
      case LTL_ATOM :
	{
	  Atom* a = &Atom::construct(static_cast<const Atom*>(f)->getId());
	  if (negation_stack.top())
	    formula_stack.push(a);
	  else
	    formula_stack.push(&Not::construct(a));
	  negation_stack.pop();
	  break;
	}

      case LTL_TRUE : case LTL_FALSE :
	if ((f->what() == LTL_TRUE) == negation_stack.top())
	  formula_stack.push(&True::construct());
	else
	  formula_stack.push(&False::construct());
	negation_stack.pop();
	break;

      default :
	negation_stack.push(f->what() != LTL_NEGATION
			      && f->what() != LTL_BEFORE
			    ? negation_stack.top()
			    : !negation_stack.top());

	switch (f->what())
        {
          case LTL_CONJUNCTION : case LTL_DISJUNCTION : case LTL_UNTIL :
	  case LTL_V : case LTL_WEAK_UNTIL : case LTL_STRONG_RELEASE :
	    negation_stack.push(negation_stack.top());
	    break;

	  case LTL_IMPLICATION :
	  case LTL_BEFORE :
	    negation_stack.push(!negation_stack.top());
	    break;

	  case LTL_EQUIVALENCE :
	    negation_stack.push(true);
	    break;

	  case LTL_XOR :
	    negation_stack.push(false);
	    break;

          default :
	    break;
	}
    }

    return;
  }

  switch (f->what())
  {
    case LTL_NEGATION :
      break;

    case LTL_NEXT :
      formula_stack.top() = &Next::construct(formula_stack.top());
      break;

    case LTL_FINALLY : case LTL_GLOBALLY :
      if ((f->what() == LTL_FINALLY) == negation_stack.top())
	formula_stack.top()
	  = &Until::construct(&True::construct(), formula_stack.top());
      else
	formula_stack.top()
	  = &V::construct(&False::construct(), formula_stack.top());
      break;

    default :  /* binary operator */
      {
	LtlFormula* rgt = formula_stack.top();
	formula_stack.pop();

        switch (f->what())
	{
	  case LTL_CONJUNCTION : case LTL_DISJUNCTION : case LTL_IMPLICATION :
	    if ((f->what() == LTL_CONJUNCTION) == negation_stack.top())
	      formula_stack.top()
		= &And::construct(formula_stack.top(), rgt);
	    else
	      formula_stack.top()
		= &Or::construct(formula_stack.top(), rgt);
	    break;

	  case LTL_EQUIVALENCE : case LTL_XOR :
	    {
	      LtlFormula* g = &Not::construct(*formula_stack.top());
	      LtlFormula* lft_neg = g->nnfClone();
	      LtlFormula::destruct(g);
	      g = &Not::construct(*rgt);
	      LtlFormula* rgt_neg = g->nnfClone();
	      LtlFormula::destruct(g);

	      formula_stack.top()
		= &And::construct(&Or::construct(formula_stack.top(), rgt_neg),
				  &Or::construct(lft_neg, rgt));
	      break;
	    }

	  case LTL_UNTIL : case LTL_V : case LTL_BEFORE :
	    if ((f->what() == LTL_UNTIL) == negation_stack.top())
	      formula_stack.top()
		= &Until::construct(formula_stack.top(), rgt);
	    else
	      formula_stack.top()
		= &V::construct(formula_stack.top(), rgt);
	    break;

	  default :  /* LTL_WEAK_UNTIL || LTL_STRONG_RELEASE */
	    if ((f->what() == LTL_WEAK_UNTIL) == negation_stack.top())
	      formula_stack.top()
		= &Or::construct(&Until::construct(*formula_stack.top(), rgt),
				 &V::construct(&False::construct(),
					       formula_stack.top()));
	    else
	      formula_stack.top()
		= &And::construct(&V::construct(*formula_stack.top(), rgt),
				  &Until::construct(&True::construct(),
						    formula_stack.top()));
	    break;
	}

	break;
      }
  }

  negation_stack.pop();
}



/******************************************************************************
 *
 * Definitions for symbols used for printing the formula.
 *
 *****************************************************************************/

const char LtlTrue::prefix_symbol[] = "t";
const char LtlTrue::infix_symbol[] = "true";

const char LtlFalse::prefix_symbol[] = "f";
const char LtlFalse::infix_symbol[] = "false";

const char LtlNegation::prefix_symbol[] = "!";
const char LtlNegation::infix_symbol[] = "!";

const char LtlNext::prefix_symbol[] = "X";
const char LtlNext::infix_symbol[] = "X";

const char LtlFinally::prefix_symbol[] = "F";
const char LtlFinally::infix_symbol[] = "<>";

const char LtlGlobally::prefix_symbol[] = "G";
const char LtlGlobally::infix_symbol[] = "[]";

const char LtlConjunction::prefix_symbol[] = "&";
const char LtlConjunction::infix_symbol[] = "/\\";

const char LtlDisjunction::prefix_symbol[] = "|";
const char LtlDisjunction::infix_symbol[] = "\\/";

const char LtlImplication::prefix_symbol[] = "i";
const char LtlImplication::infix_symbol[] = "->";

const char LtlEquivalence::prefix_symbol[] = "e";
const char LtlEquivalence::infix_symbol[] = "<->";

const char LtlXor::prefix_symbol[] = "^";
const char LtlXor::infix_symbol[] = "xor";

const char LtlUntil::prefix_symbol[] = "U";
const char LtlUntil::infix_symbol[] = "U";

const char LtlV::prefix_symbol[] = "V";
const char LtlV::infix_symbol[] = "V";

const char LtlWeakUntil::prefix_symbol[] = "W";
const char LtlWeakUntil::infix_symbol[] = "W";

const char LtlStrongRelease::prefix_symbol[] = "M";
const char LtlStrongRelease::infix_symbol[] = "M";

const char LtlBefore::prefix_symbol[] = "B";
const char LtlBefore::infix_symbol[] = "B";



/******************************************************************************
 *
 * Function definitions for class LtlFormula.
 *
 *****************************************************************************/

/* ========================================================================= */
LtlFormula* LtlFormula::nnfClone()
/* ----------------------------------------------------------------------------
 *
 * Description:   Creates a copy of an LtlFormula in negation normal form.
 *
 * Arguments:     None.
 *
 * Returns:       A pointer to a newly allocated LtlFormula, which is
 *                equivalent to `this' formula in negation normal form.
 *
 * ------------------------------------------------------------------------- */
{
  NnfConverter nc;
  traverse(nc, LTL_PREORDER | LTL_POSTORDER);
  return nc.getResult();
}

/* ========================================================================= */
unsigned long int LtlFormula::size() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Computes the number of nodes in the parse tree of an
 *                LtlFormula.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  FormulaSizeCounter fsc;
  traverse(fsc, LTL_PREORDER);
  return fsc.size;
}

/* ========================================================================= */
void LtlFormula::collectSubformulae
  (stack<const LtlFormula*, deque<const LtlFormula*,
                                  ALLOC(const LtlFormula*) > >&
     result_stack) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Collects the subformulae of a LtlFormula into a stack. After
 *                the operation, the result stack contains the subformulae of
 *                the formula in post-depth-first-search order, i.e., each
 *                subformula of the formula can be accessed in the stack only
 *                after its subformulae have been accessed.
 *
 * Argument:      result_stack  --  A reference to a stack for collecting the
 *                                  results.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  SubformulaCollector sc(result_stack);
  traverse(sc, LTL_PREORDER);
}

/* ========================================================================= */
long int LtlFormula::maxAtom() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Returns the identifier of the "greatest" atomic proposition
 *                in the formula.
 *
 * Arguments:     None.
 *
 * Returns:       The identifier of the "greatest" atomic proposition in the
 *                formula or -1 if the formula is a constant formula.
 *
 * ------------------------------------------------------------------------- */
{
  if (info_flags.is_constant)
    return -1;

  MaxAtomFinder maf;
  traverse(maf, LTL_PREORDER);
  return maf.getResult();
}

/* ========================================================================= */
bool LtlFormula::satisfiable(long int max_atom) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells whether the formula is satisfiable. (This check is
 *                allowed only on fully propositional formulae.)
 *
 * Arguments:     max_atom  --  Identifier of the "greatest" atomic proposition
 *                              in the formula.
 *
 * Returns:       A truth value according to the result of the test.
 *
 * ------------------------------------------------------------------------- */
{
  if (!info_flags.is_propositional)
    throw Exception("satisfiability check not allowed on a temporal formula");

  if (max_atom < 0)
    max_atom = maxAtom();

  TableauStack tableau_stack;
  return sat_eval(tableau_stack, max_atom);
}

/* ========================================================================= */
Bitset LtlFormula::findPropositionalModel(long int max_atom) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Finds a model for a fully propositional formula (if the
 *                formula is satisfiable).
 *
 * Arguments:     max_atom  --  Identifier of the "greatest" atomic proposition
 *                              in the formula.
 *
 * Returns:       A Bitset defining a truth assignment for the propositional
 *                variables in the formula such that the formula is satisfied.
 *
 * ------------------------------------------------------------------------- */
{
  if (!info_flags.is_propositional)
    throw Exception("satisfiability check not allowed on a temporal formula");

  if (max_atom < 0)
    max_atom = maxAtom();

  TableauStack tableau_stack;

  if (!sat_eval(tableau_stack, max_atom))
    throw Exception("formula is not satisfiable");

  Bitset model(max_atom + 1);
  model.clear();

  for (long int i = 0; i <= max_atom; i++)
  {
    if (tableau_stack.top().second.test(i))
      model.setBit(i);
  }

  return model;
}

/* ========================================================================= */
LtlFormula* LtlFormula::read(Exceptional_istream& stream)
/* ----------------------------------------------------------------------------
 *
 * Description:   Recursively constructs an LtlFormula by parsing input from an
 *                exception-aware input stream.
 *
 * Argument:      stream  --  A reference to an exception-aware input stream.
 *
 * Returns:       The constructed LtlFormula.
 *
 * ------------------------------------------------------------------------- */
{
  string token;
  LtlFormula* formula;

  try
  {
    stream >> token;
  }
  catch (const IOException&)
  {
    if (static_cast<istream&>(stream).eof())
      throw ParseErrorException("error parsing LTL formula (unexpected end of "
				"input)");
    else
      throw ParseErrorException("error parsing LTL formula (I/O error)");
  }

  if (token[0] == 'p')
  {
    if (token.length() == 1)
      throw ParseErrorException("error parsing LTL formula (unrecognized "
				"token: `" + token + "')");

    long int id;
    char* endptr;

    id = strtol(token.c_str() + 1, &endptr, 10);

    if (*endptr != '\0' || id < 0 || id == LONG_MIN || id == LONG_MAX)
      throw ParseErrorException("error parsing LTL formula (unrecognized "
				"token: `" + token + "')");

    formula = &Atom::construct(id);
  }
  else
  {
    if (token.length() > 1)
      throw ParseErrorException("error parsing LTL formula (unrecognized "
				"token: `" + token + "')");

    switch (token[0])
    {
      case LTL_TRUE :
	formula = &True::construct();
	break;

      case LTL_FALSE :
	formula = &False::construct();
	break;

      case LTL_NEGATION :
      case LTL_NEXT :
      case LTL_FINALLY :
      case LTL_GLOBALLY :
	{
	  LtlFormula* g = read(stream);

	  try
	  {
	    switch (token[0])
	    {
	      case LTL_NEGATION :
		formula = &Not::construct(g);
		break;

	      case LTL_NEXT :
		formula = &Next::construct(g);
		break;

	      case LTL_FINALLY :
		formula = &Finally::construct(g);
		break;

	      default : /* LTL_GLOBALLY */
		formula = &Globally::construct(g);
		break;
	    }
	  }
	  catch (...)
	  {
	    LtlFormula::destruct(g);
	    throw;
	  }

	  break;
	}

      case LTL_CONJUNCTION :
      case LTL_DISJUNCTION :
      case LTL_IMPLICATION :
      case LTL_EQUIVALENCE :
      case LTL_XOR :
      case LTL_UNTIL :
      case LTL_V :
      case LTL_WEAK_UNTIL :
      case LTL_STRONG_RELEASE :
      case LTL_BEFORE :
	{
	  LtlFormula* g = read(stream);
	  LtlFormula* h;

	  try
	  {
	    h = read(stream);
	  }
	  catch (...)
	  {
	    LtlFormula::destruct(g);
	    throw;
	  }

	  try
	  {
	    switch (token[0])
	    {
	      case LTL_CONJUNCTION :
		formula = &And::construct(g, h);
		break;

	      case LTL_DISJUNCTION :
		formula = &Or::construct(g, h);
		break;

	      case LTL_IMPLICATION :
		formula = &Imply::construct(g, h);
		break;

	      case LTL_EQUIVALENCE :
		formula = &Equiv::construct(g, h);
		break;

	      case LTL_XOR :
		formula = &Xor::construct(g, h);
		break;
		
	      case LTL_UNTIL :
		formula = &Until::construct(g, h);
		break;

	      case LTL_V :
		formula = &V::construct(g, h);
		break;

	      case LTL_WEAK_UNTIL :
		formula = &WeakUntil::construct(g, h);
		break;

	      case LTL_STRONG_RELEASE :
		formula = &StrongRelease::construct(g, h);
		break;

	      default : /* LTL_BEFORE */
		formula = &Before::construct(g, h);
		break;
	    }
	  }
	  catch (...)
	  {
	    LtlFormula::destruct(g);
	    LtlFormula::destruct(h);
	    throw;
	  }

	  break;
	}

      default :
	throw ParseErrorException("error parsing LTL formula (unrecognized "
				  "token: `" + token + "')");
    }
  }

  return formula;
}

/* ========================================================================= */
void LtlFormula::print(Exceptional_ostream& estream, OutputMode mode) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes the formula to a stream.
 *
 * Arguments:     estream  --  A reference to an exception-aware output stream.
 *                mode     --  Chooses between prefix and infix notation.
 * 
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (mode == LTL_PREFIX)
  {
    FormulaWriter<ConstantWriter<LtlTrue::prefix_symbol>,
                  ConstantWriter<LtlFalse::prefix_symbol>,
                  AtomWriter,
                  UnaryOperatorWriter<LtlNegation::prefix_symbol>,
                  UnaryOperatorWriter<LtlNext::prefix_symbol>,
                  UnaryOperatorWriter<LtlFinally::prefix_symbol>,
                  UnaryOperatorWriter<LtlGlobally::prefix_symbol>,
                  BinaryOperatorPrefixWriter<LtlConjunction::prefix_symbol>,
                  BinaryOperatorPrefixWriter<LtlDisjunction::prefix_symbol>,
                  BinaryOperatorPrefixWriter<LtlImplication::prefix_symbol>,
                  BinaryOperatorPrefixWriter<LtlEquivalence::prefix_symbol>,
                  BinaryOperatorPrefixWriter<LtlXor::prefix_symbol>,
                  BinaryOperatorPrefixWriter<LtlUntil::prefix_symbol>,
                  BinaryOperatorPrefixWriter<LtlV::prefix_symbol>,
                  BinaryOperatorPrefixWriter<LtlWeakUntil::prefix_symbol>,
                  BinaryOperatorPrefixWriter<LtlStrongRelease::prefix_symbol>,
                  BinaryOperatorPrefixWriter<LtlBefore::prefix_symbol>
                 >
      fw(estream);

    traverse(fw, LTL_PREORDER | LTL_INORDER | LTL_POSTORDER);
  }
  else
  {
    FormulaWriter<ConstantWriter<LtlTrue::infix_symbol>,
                  ConstantWriter<LtlFalse::infix_symbol>,
                  AtomWriter,
                  UnaryOperatorWriter<LtlNegation::infix_symbol>,
                  UnaryOperatorWriter<LtlNext::infix_symbol>,
                  UnaryOperatorWriter<LtlFinally::infix_symbol>,
                  UnaryOperatorWriter<LtlGlobally::infix_symbol>,
                  BinaryOperatorInfixWriter<LtlConjunction::infix_symbol>,
                  BinaryOperatorInfixWriter<LtlDisjunction::infix_symbol>,
                  BinaryOperatorInfixWriter<LtlImplication::infix_symbol>,
                  BinaryOperatorInfixWriter<LtlEquivalence::infix_symbol>,
                  BinaryOperatorInfixWriter<LtlXor::infix_symbol>,
                  BinaryOperatorInfixWriter<LtlUntil::infix_symbol>,
                  BinaryOperatorInfixWriter<LtlV::infix_symbol>,
                  BinaryOperatorInfixWriter<LtlWeakUntil::infix_symbol>,
                  BinaryOperatorInfixWriter<LtlStrongRelease::infix_symbol>,
                  BinaryOperatorInfixWriter<LtlBefore::infix_symbol>
                 >
      fw(estream);

    traverse(fw, LTL_PREORDER | LTL_INORDER | LTL_POSTORDER);
  }
}

/* ========================================================================= */
bool LtlFormula::sat_eval
  (TableauStack& tableau_stack, const long int max_atom) const
/* ----------------------------------------------------------------------------
 *
 * Description:   Determines whether the formula is satisfiable. If yes, a
 *                model of the formula will be stored in the map given as a
 *                parameter. (This check is allowed only on fully propositional
 *                formulae.)
 *
 * Arguments:     tableau_stack  --  Nodes of the tableau used for
 *                                   satisfiability testing.
 *                max_atom       --  Identifier of the "greatest" atomic
 *                                   proposition in the formula.
 *
 * Returns:       A truth value telling whether the formula is satisfiable or
 *                not.
 *
 * ------------------------------------------------------------------------- */
{
  bool prove_true;
  const LtlFormula* current_formula;

  tableau_stack.push
    (make_pair(FormulaStack(),
	       Bitset((static_cast<unsigned long int>(max_atom) + 1) * 2)));
  tableau_stack.top().second.clear();

  FormulaStack* formula_stack = &tableau_stack.top().first;

  formula_stack->push(make_pair(true, this));

  while (!formula_stack->empty())
  {
    prove_true = formula_stack->top().first;
    current_formula = formula_stack->top().second;
    formula_stack->pop();

    switch (current_formula->what())
    {
      case LTL_ATOM : 
      {
	long int atom_id = static_cast<const Atom*>(current_formula)->getId();

	if (prove_true)
	{
	  if (tableau_stack.top().second.test(atom_id + max_atom + 1))
	  {
	    tableau_stack.pop();
	    if (tableau_stack.empty())
	      return false;
	    formula_stack = &tableau_stack.top().first;
	  }
	  else
	    tableau_stack.top().second.setBit(atom_id);
	}
	else
	{
	  if (tableau_stack.top().second.test(atom_id))
	  {
	    tableau_stack.pop();
	    if (tableau_stack.empty())
	      return false;
	    formula_stack = &tableau_stack.top().first;
	  }
	  else
	    tableau_stack.top().second.setBit(atom_id + max_atom + 1);
	}

	break;
      }

      case LTL_TRUE :
	if (!prove_true)
	{
	  tableau_stack.pop();
	  if (tableau_stack.empty())
	    return false;
	  formula_stack = &tableau_stack.top().first;
	}

	break;

      case LTL_FALSE :
	if (prove_true)
	{
	  tableau_stack.pop();
	  if (tableau_stack.empty())
	    return false;
	  formula_stack = &tableau_stack.top().first;
	}

	break;

      case LTL_NEGATION :
	{
	  const Not* f = static_cast<const Not*>(current_formula);
	  formula_stack->push(make_pair(!prove_true, f->subformula));
	  break;
	}

      case LTL_DISJUNCTION :
	{
	  const Or* f = static_cast<const Or*>(current_formula);
	  
	  if (prove_true)
	  {
	    tableau_stack.push(tableau_stack.top());
	    formula_stack->push(make_pair(true, f->subformula2));
	    formula_stack = &tableau_stack.top().first;
	    formula_stack->push(make_pair(true, f->subformula1));
	  }
	  else
	  {
	    formula_stack->push(make_pair(false, f->subformula2));
	    formula_stack->push(make_pair(false, f->subformula1));
	  }

	  break;
	}

      case LTL_CONJUNCTION :
        {
	  const And* f = static_cast<const And*>(current_formula);

	  if (prove_true)
	  {
	    formula_stack->push(make_pair(true, f->subformula2));
	    formula_stack->push(make_pair(true, f->subformula1));
	  }
	  else
	  {
	    tableau_stack.push(tableau_stack.top());
	    formula_stack->push(make_pair(false, f->subformula2));
	    formula_stack = &tableau_stack.top().first;
	    formula_stack->push(make_pair(false, f->subformula1));
	  }
      
	  break;
	}

      case LTL_IMPLICATION :
        {
	  const Imply* f = static_cast<const Imply*>(current_formula);
	  
	  if (prove_true)
	  {
	    tableau_stack.push(tableau_stack.top());
	    formula_stack->push(make_pair(true, f->subformula2));
	    formula_stack = &tableau_stack.top().first;
	    formula_stack->push(make_pair(false, f->subformula1));
	  }
	  else
	  {
	    formula_stack->push(make_pair(false, f->subformula2));
	    formula_stack->push(make_pair(true, f->subformula1));
	  }
	
	  break;
	}

      case LTL_EQUIVALENCE :
        {
	  const Equiv* f = static_cast<const Equiv*>(current_formula);

	  tableau_stack.push(tableau_stack.top());
	  formula_stack->push(make_pair(!prove_true, f->subformula2));
	  formula_stack->push(make_pair(false, f->subformula1));
	  formula_stack = &tableau_stack.top().first;
	  formula_stack->push(make_pair(prove_true, f->subformula2));
	  formula_stack->push(make_pair(true, f->subformula1));
	  
	  break;
	}

      case LTL_XOR :
	{
	  const Xor* f = static_cast<const Xor*>(current_formula);

	  tableau_stack.push(tableau_stack.top());
	  formula_stack->push(make_pair(prove_true, f->subformula2));
	  formula_stack->push(make_pair(false, f->subformula1));
	  formula_stack = &tableau_stack.top().first;
	  formula_stack->push(make_pair(!prove_true, f->subformula2));
	  formula_stack->push(make_pair(true, f->subformula1));

	  break;
	}

      default :
	throw Exception("satisfiable(): unknown formula type");
    }
  }

  return true;
}

}
