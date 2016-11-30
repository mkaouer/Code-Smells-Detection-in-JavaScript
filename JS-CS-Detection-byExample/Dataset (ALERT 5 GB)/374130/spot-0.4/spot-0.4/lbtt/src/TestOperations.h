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

#ifndef TESTOPERATIONS_H
#define TESTOPERATIONS_H

#include <config.h>
#include <fstream>
#include <iostream>
#include <string>
#include <vector>
#include "LbttAlloc.h"
#include "Configuration.h"
#include "Exception.h"
#include "StateSpace.h"
#include "TestStatistics.h"

using namespace std;

extern Configuration configuration;



/******************************************************************************
 *
 * Functions for various test operations.
 *
 *****************************************************************************/

namespace TestOperations
{

void openFile
  (const char* filename, ifstream& stream,          /* Opens a file for */
   ios::openmode mode, int indent = 0);             /* reading.         */

void openFile                                       /* Opens a file for */
  (const char* filename, ofstream& stream,          /* writing.         */
   ios::openmode mode, int indent = 0);

void openFile                                       /* Opens a file for   */
  (const char* filename, int& fd, int flags,        /* input/output using */
   int indent = 0);                                 /* file descriptors.  */

void truncateFile                                   /* Truncates a file. */
  (const char* filename, int indent = 0);

void printFileContents                              /* Displays the contents */
  (ostream& stream, const char* message,            /* of a file.            */
   const char* filename, int indent,
   const char* line_prefix = "");

void writeToTranscript                              /* Writes a message     */
  (const string& message,                           /* into the transcript  */
   bool show_formula_in_header = true);             /* file. 	            */

void generateStateSpace();                          /* Generates a state space.
						     */

void generateFormulae                               /* Generates a random   */
  (istream* formula_input_stream = 0);              /* LTL formula and
                                                     * stores the formula,
                                                     * its negation and the
						     * NNFs of the two
						     * formulae into a
						     * given array.
						     */

void verifyFormulaOnPath();                         /* Evaluates the LTL
						     * formula (accessed
						     * through `round_info')
						     * on a path directly
						     * (if using paths as
						     * state spaces).
						     */

void writeFormulaeToFiles();                        /* Writes LTL formulas */
                                                    /* into a file.        */

void generateBuchiAutomaton                         /* Generates a Büchi     */
  (int f,                                           /* automaton from a LTL  */
   vector<Configuration::AlgorithmInformation>      /* formula stored in a   */
     ::size_type                                    /* given file, using a   */
     algorithm_id);                                 /* given LTL-to-Büchi
                                                     * translation algorithm
                  				     * for the conversion.
						     */

void performEmptinessCheck                          /* Performs an emptiness */
  (int f,                                           /* check on a product    */
   vector<Configuration::AlgorithmInformation>      /* automaton.            */
     ::size_type
     algorithm_id);

void performConsistencyCheck                        /* Performs a            */
  (vector<Configuration::AlgorithmInformation>      /* consistency check on  */
     ::size_type                                    /* the test results      */
     algorithm_id);                                 /* for a formula and its
                                                     * negation.
						     */

void compareResults();                              /* Compares the model
                                                     * checking results
                                                     * obtained using some
                                                     * LTL-to-Büchi conversion
                                                     * algorithm with the
                                                     * results given by the
                                                     * other algorithms.
						     */

void performBuchiIntersectionCheck();               /* Performs pairwise
                                                     * emptiness checks on the
                                                     * Büchi automata
                   			             * constructed by the
                                                     * different algorithms
                                                     * from a formula and its
                        			     * negation.
						     */



/******************************************************************************
 *
 * A class for reporting state space generation errors.
 *
 *****************************************************************************/

class StateSpaceGenerationException : public Exception
{
public:
  StateSpaceGenerationException();                  /* Constructor. */

  /* default copy constructor */

  ~StateSpaceGenerationException() throw();         /* Destructor. */

  StateSpaceGenerationException& operator=          /* Assignment operator. */
    (const StateSpaceGenerationException& e);
};



/******************************************************************************
 *
 * A class for reporting LTL formula generation errors.
 *
 *****************************************************************************/

class FormulaGenerationException : public Exception
{
public:
  FormulaGenerationException();                     /* Constructor. */

  /* default copy constructor */

  ~FormulaGenerationException() throw();            /* Destructor. */

  FormulaGenerationException& operator=             /* Assignment operator. */
    (const FormulaGenerationException& e);
};



/******************************************************************************
 *
 * A class for reporting Büchi automaton generation errors.
 *
 *****************************************************************************/

class BuchiAutomatonGenerationException : public Exception
{
public:
  BuchiAutomatonGenerationException();              /* Constructor. */

  /* default copy constructor */

  ~BuchiAutomatonGenerationException() throw();     /* Destructor. */

  BuchiAutomatonGenerationException& operator=      /* Assignment operator. */
    (const BuchiAutomatonGenerationException& e);
};



/******************************************************************************
 *
 * A class for reporting product automaton generation errors.
 *
 *****************************************************************************/

class ProductAutomatonGenerationException : public Exception
{
public:
  ProductAutomatonGenerationException();            /* Constructor. */

  /* default copy constructor */

  ~ProductAutomatonGenerationException() throw();   /* Destructor. */

  ProductAutomatonGenerationException& operator=    /* Assignment operator. */
    (const ProductAutomatonGenerationException& e);
};



/******************************************************************************
 *
 * A class for reporting errors during the emptiness check.
 *
 *****************************************************************************/

class EmptinessCheckFailedException : public Exception
{
public:
  EmptinessCheckFailedException();                  /* Constructor. */

  /* default copy constructor */

  ~EmptinessCheckFailedException() throw();         /* Destructor. */

  EmptinessCheckFailedException& operator=          /* Assignment operator. */
    (const EmptinessCheckFailedException& e);
};



/******************************************************************************
 *
 * Inline function definitions for class StateSpaceGenerationException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline StateSpaceGenerationException::StateSpaceGenerationException() :
  Exception("state space generation failed")
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class StateSpaceGenerationException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline StateSpaceGenerationException::~StateSpaceGenerationException() throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class StateSpaceGenerationException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline StateSpaceGenerationException&
StateSpaceGenerationException::operator=
  (const StateSpaceGenerationException& e)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class StateSpaceGenerationException.
 *                Assigns the value of another StateSpaceGenerationException to
 *                `this' one.
 *
 * Arguments:     e  --  A reference to a constant StateSpaceException.
 *
 * Returns:       A reference to the object whose values was changed.
 *
 * ------------------------------------------------------------------------- */
{
  Exception::operator=(e);
  return *this;
}



/******************************************************************************
 *
 * Inline function definitions for class FormulaGenerationException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline FormulaGenerationException::FormulaGenerationException() :
  Exception("LTL formula generation failed")
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class FormulaGenerationException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline FormulaGenerationException::~FormulaGenerationException() throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class FormulaGenerationException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline FormulaGenerationException&
FormulaGenerationException::operator=(const FormulaGenerationException& e)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class FormulaGenerationException.
 *                Assigns the value of another FormulaGenerationException to
 *                `this' one.
 *
 * Arguments:     e  --  A reference to a constant FormulaException.
 *
 * Returns:       A reference to the object whose values was changed.
 *
 * ------------------------------------------------------------------------- */
{
  Exception::operator=(e);
  return *this;
}



/******************************************************************************
 *
 * Inline function definitions for class BuchiAutomatonGenerationException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline BuchiAutomatonGenerationException::BuchiAutomatonGenerationException() :
  Exception("Büchi automaton generation failed")
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class BuchiAutomatonGenerationException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline BuchiAutomatonGenerationException::~BuchiAutomatonGenerationException()
  throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class BuchiAutomatonGenerationException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline BuchiAutomatonGenerationException&
BuchiAutomatonGenerationException::operator=
  (const BuchiAutomatonGenerationException& e)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class
 *                BuchiAutomatonGenerationException. Assigns the value of
 *                another BuchiAutomatonGenerationException to `this' one.
 *
 * Arguments:     e  --  A reference to a constant
 *                       BuchiAutomatonGenerationException.
 *
 * Returns:       A reference to the object whose value was changed.
 *
 * ------------------------------------------------------------------------- */
{
  Exception::operator=(e);
  return *this;
}



/******************************************************************************
 *
 * Inline function definitions for class ProductAutomatonGenerationException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline
ProductAutomatonGenerationException::ProductAutomatonGenerationException() :
  Exception("product automaton generation failed")
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class ProductAutomatonGenerationException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline
ProductAutomatonGenerationException::~ProductAutomatonGenerationException()
  throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class ProductAutomatonGenerationException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline ProductAutomatonGenerationException&
ProductAutomatonGenerationException::operator=
  (const ProductAutomatonGenerationException& e)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class
 *                ProductAutomatonGenerationException.
 *
 * Arguments:     e  --  A reference to a constant
 *                       ProductAutomatonGenerationException.
 *
 * Returns:       A reference to the object whose value was changed.
 *
 * ------------------------------------------------------------------------- */
{
  Exception::operator=(e);
  return *this;
}



/******************************************************************************
 *
 * Inline function definitions for class EmptinessCheckFailedException.
 *
 *****************************************************************************/

/* ========================================================================= */
inline
EmptinessCheckFailedException::EmptinessCheckFailedException() :
  Exception("emptiness check failed")
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class EmptinessCheckFailedException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline EmptinessCheckFailedException::~EmptinessCheckFailedException() throw()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class EmptinessCheckFailedException.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline EmptinessCheckFailedException& EmptinessCheckFailedException::operator=
  (const EmptinessCheckFailedException& e)
/* ----------------------------------------------------------------------------
 *
 * Description:   Assignment operator for class EmptinessCheckFailedException.
 *                Assigns the value of another EmptinessCheckFailedException to
 *                `this' one.
 *
 * Arguments:     e  --  A reference to a constant
 *                       EmptinessCheckFailedException.
 *
 * Returns:       A reference to the object whose value was changed.
 *
 * ------------------------------------------------------------------------- */
{
  Exception::operator=(e);
  return *this;
}

}

#endif /* !TESTOPERATIONS_H */
