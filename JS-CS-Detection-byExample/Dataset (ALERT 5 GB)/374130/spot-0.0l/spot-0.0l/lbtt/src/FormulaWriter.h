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

#ifndef FORMULAWRITER_H
#define FORMULAWRITER_H

#include "Exception.h"

namespace Ltl
{

class LtlFormula;
class Atom;

/******************************************************************************
 *
 * A function template class for writing the formula to a stream.
 *
 *****************************************************************************/

template<class TrueWriter, class FalseWriter, class AtomWriter,
         class NotWriter, class NextWriter, class FinallyWriter,
         class GloballyWriter, class AndWriter, class OrWriter,
         class ImplyWriter, class EquivWriter, class XorWriter,
         class UntilWriter, class ReleaseWriter, class WeakUntilWriter,
         class StrongReleaseWriter, class BeforeWriter>
class FormulaWriter
{
public:
  FormulaWriter(Exceptional_ostream& stream);       /* Constructor. */

  ~FormulaWriter();                                 /* Destructor. */

  void operator()                                   /* Implements the write */
    (const LtlFormula* f, int operand);             /* operation.           */

private:
  Exceptional_ostream& estream;                     /* Output stream. */

  FormulaWriter(const FormulaWriter&);              /* Prevent copying and */
  FormulaWriter& operator=(const FormulaWriter&);   /* assignment of
						     * FormulaWriter
						     * objects.
						     */
};



/******************************************************************************
 *
 * Class for printing atomic propositions.
 *
 *****************************************************************************/

class AtomWriter
{
public:
  static void write                                 /* Implements the write */
    (Exceptional_ostream& estream,                  /* operation.           */
     long int atom_id);
};



/******************************************************************************
 *
 * Template class for printing Boolean constants.
 *
 *****************************************************************************/

template<const char* symbol>
class ConstantWriter
{
public:
  static void write(Exceptional_ostream& estream);  /* Implements the write */
};                                                  /* operation.           */



/******************************************************************************
 *
 * Template class for printing unary operators.
 *
 *****************************************************************************/

template<const char* symbol>
class UnaryOperatorWriter
{
public:
  static void write                                 /* Implements the write */
    (Exceptional_ostream& estream, int operand);    /* operation.           */
};



/******************************************************************************
 *
 * Template class for printing binary operators in prefix notation.
 *
 *****************************************************************************/

template<const char* symbol>
class BinaryOperatorPrefixWriter
{
public:
  static void write                                 /* Implements the write */
    (Exceptional_ostream& estream, int operand);    /* operation.           */
};



/******************************************************************************
 *
 * Template class for printing binary operators in infix notation.
 *
 *****************************************************************************/

template<const char* symbol>
class BinaryOperatorInfixWriter
{
public:
  static void write                                 /* Implements the write */
    (Exceptional_ostream& estream, int operand);    /* operation.           */
};



/******************************************************************************
 *
 * Inline function definitions for template class FormulaWriter.
 *
 *****************************************************************************/

/* ========================================================================= */
template<class TrueWriter, class FalseWriter, class AtomWriter,
         class NotWriter, class NextWriter, class FinallyWriter,
         class GloballyWriter, class AndWriter, class OrWriter,
         class ImplyWriter, class EquivWriter, class XorWriter,
         class UntilWriter, class ReleaseWriter, class WeakUntilWriter,
         class StrongReleaseWriter, class BeforeWriter>
inline FormulaWriter<TrueWriter, FalseWriter, AtomWriter, NotWriter,
                     NextWriter, FinallyWriter, GloballyWriter, AndWriter,
                     OrWriter, ImplyWriter, EquivWriter, XorWriter,
                     UntilWriter, ReleaseWriter, WeakUntilWriter,
                     StrongReleaseWriter, BeforeWriter>::
FormulaWriter(Exceptional_ostream& stream) :
  estream(stream)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class FormulaWriter.
 *
 * Arguments:     stream        --  A reference to an exception-aware output
 *                                  stream.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
template<class TrueWriter, class FalseWriter, class AtomWriter,
         class NotWriter, class NextWriter, class FinallyWriter,
         class GloballyWriter, class AndWriter, class OrWriter,
         class ImplyWriter, class EquivWriter, class XorWriter,
         class UntilWriter, class ReleaseWriter, class WeakUntilWriter,
         class StrongReleaseWriter, class BeforeWriter>
inline FormulaWriter<TrueWriter, FalseWriter, AtomWriter, NotWriter,
                     NextWriter, FinallyWriter, GloballyWriter, AndWriter,
                     OrWriter, ImplyWriter, EquivWriter, XorWriter,
                     UntilWriter, ReleaseWriter, WeakUntilWriter,
                     StrongReleaseWriter, BeforeWriter>::
~FormulaWriter()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class FormulaWriter.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}



/******************************************************************************
 *
 * Inline function definitions for class AtomWriter.
 *
 *****************************************************************************/

/* ========================================================================= */
inline void AtomWriter::write
  (Exceptional_ostream& estream, long int atom_id)
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes an atomic proposition into a stream.
 *
 * Arguments:     estream  --  A reference to an exception-aware output stream.
 *                atom_id  --  Numeric identifier of the proposition.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  estream << 'p' << atom_id;
}



/******************************************************************************
 *
 * Inline function definitions for template class ConstantWriter.
 *
 *****************************************************************************/

/* ========================================================================= */
template<const char* symbol>
inline void ConstantWriter<symbol>::write(Exceptional_ostream& estream)
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes a Boolean constant into a stream.
 *
 * Arguments:     estream  --  A reference to an exception-aware output stream.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  estream << symbol;
}



/******************************************************************************
 *
 * Inline function definitions for template class UnaryOperatorWriter.
 *
 *****************************************************************************/

/* ========================================================================= */
template<const char* symbol>
inline void UnaryOperatorWriter<symbol>::write
  (Exceptional_ostream& estream, int operand)
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes an unary operator symbol into a stream.
 *
 * Arguments:     estream  --  A reference to an exception-aware output stream.
 *                operand  --  Identifies the state of the depth-first search
 *                             that manages the writing. The operator will be
 *                             written to the stream if and only if `operand'
 *                             has the value 0.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (operand == 0)
    estream << symbol << ' ';
}



/******************************************************************************
 *
 * Inline function definitions for template class BinaryOperatorPrefixWriter.
 *
 *****************************************************************************/

/* ========================================================================= */
template<const char* symbol>
inline void BinaryOperatorPrefixWriter<symbol>::write
  (Exceptional_ostream& estream, int operand)
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes a binary operator symbol into a stream.
 *
 * Arguments:     estream  --  A reference to an exception-aware output stream.
 *                operand  --  Identifies the state of the depth-first search
 *                             that manages thel writing. This value is used to
 *                             decide what to write to the stream.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (operand == 0)
    estream << symbol << ' ';
  else if (operand == 1)
    estream << ' ';
}



/******************************************************************************
 *
 * Inline function definitions for template class BinaryOperatorInfixWriter.
 *
 *****************************************************************************/

/* ========================================================================= */
template<const char* symbol>
inline void BinaryOperatorInfixWriter<symbol>::write
  (Exceptional_ostream& estream, int operand)
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes a binary operator symbol into a stream.
 *
 * Arguments:     estream  --  A reference to an exception-aware output stream.
 *                operand  --  Identifies the state of the depth-first search
 *                             that manages the writing. This value is
 *                             used to decide what to write to the stream.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (operand == 0)
    estream << '(';
  else if (operand == 1)
    estream << ' ' << symbol << ' ';
  else
    estream << ')';
}



/******************************************************************************
 *
 * Function definitions for template class FormulaWriter.
 *
 *****************************************************************************/

/* ========================================================================= */
template<class TrueWriter, class FalseWriter, class AtomWriter,
         class NotWriter, class NextWriter, class FinallyWriter,
         class GloballyWriter, class AndWriter, class OrWriter,
         class ImplyWriter, class EquivWriter, class XorWriter,
         class UntilWriter, class ReleaseWriter, class WeakUntilWriter,
         class StrongReleaseWriter, class BeforeWriter>
void FormulaWriter<TrueWriter, FalseWriter, AtomWriter, NotWriter, NextWriter,
                   FinallyWriter, GloballyWriter, AndWriter, OrWriter,
                   ImplyWriter, EquivWriter, XorWriter, UntilWriter,
                   ReleaseWriter, WeakUntilWriter, StrongReleaseWriter,
                   BeforeWriter>::
operator()(const LtlFormula* f, int operand)
/* ----------------------------------------------------------------------------
 *
 * Description:   Implements the formula writing operation.
 *
 * Arguments:     f        --  A pointer to a constant LtlFormula.
 *                operand  --  Used for checking when to write certain `extra'
 *                             symbols (parentheses or spaces) to the stream. 
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  switch (f->what())
  {
    case LTL_ATOM :
      AtomWriter::write(estream, static_cast<const Atom*>(f)->getId());
      break;

    case LTL_TRUE :
      TrueWriter::write(estream);
      break;

    case LTL_FALSE :
      FalseWriter::write(estream);
      break;

    case LTL_NEGATION :
      NotWriter::write(estream, operand);
      break;

    case LTL_NEXT :
      NextWriter::write(estream, operand);
      break;

    case LTL_FINALLY :
      FinallyWriter::write(estream, operand);
      break;

    case LTL_GLOBALLY :
      GloballyWriter::write(estream, operand);
      break;

    case LTL_CONJUNCTION :
      AndWriter::write(estream, operand);
      break;

    case LTL_DISJUNCTION :
      OrWriter::write(estream, operand);
      break;

    case LTL_IMPLICATION :
      ImplyWriter::write(estream, operand);
      break;

    case LTL_EQUIVALENCE :
      EquivWriter::write(estream, operand);
      break;

    case LTL_XOR :
      XorWriter::write(estream, operand);
      break;

    case LTL_UNTIL :
      UntilWriter::write(estream, operand);
      break;

    case LTL_V :
      ReleaseWriter::write(estream, operand);
      break;

    case LTL_WEAK_UNTIL :
      WeakUntilWriter::write(estream, operand);
      break;

    case LTL_STRONG_RELEASE :
      StrongReleaseWriter::write(estream, operand);
      break;

    default : /* LTL_BEFORE */
      BeforeWriter::write(estream, operand);
      break;
  }
}

}

#endif /* !FORMULAWRITER_H */
