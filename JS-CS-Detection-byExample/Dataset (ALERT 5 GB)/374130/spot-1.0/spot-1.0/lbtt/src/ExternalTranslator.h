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

#ifndef EXTERNALTRANSLATOR_H
#define EXTERNALTRANSLATOR_H

#include <config.h>
#include <deque>
#include <stack>
#include <string>
#ifdef HAVE_SSTREAM
#include <sstream>
#else
#include <strstream>
#endif /* HAVE_SSTREAM */
#include "LbttAlloc.h"
#include "Exception.h"
#include "LtlFormula.h"
#include "translate.h"
#include "TempFsysName.h"
#include "TranslatorInterface.h"

/******************************************************************************
 *
 * Interface class for an LTL-to-Büchi translation algorithm operating as an
 * external program.  An actual implementation of this interface (a class
 * derived from this base class) must provide definitions for the following
 * functions:
 *
 *    string commandLine
 *      (const string& input_filename, const string& output_filename)
 *           Formats the command line of the external translator such that the
 *           external program will read its input (i.e., an LTL formula) from
 *           `input_filename' and store its output into `output_filename'.
 *
 *           The returned string should include _only_ the argument part of the
 *           command line (the returned string will be appended to the name of
 *           the program).  Use input/output redirection in the string if the
 *           program does not have an option to explicitly specify an
 *           input/output file.
 *
 *    void parseAutomaton
 *      (const string& input_filename, const string& output_filename)
 *           Reads the automaton constructed by the external program from
 *           `input_filename' and stores the automaton in lbtt syntax into
 *           `output_filename'.
 *
 * In addition, the implementation can override the default definitions for the
 * following functions:
 *
 *    void translateFormula
 *      (const LtlFormula& formula, string& translated_formula)
 *           Translates a given LtlFormula into the input syntax of the
 *           external translator.  The result must be stored in
 *           `translated_formula'. The default implementation simply returns
 *           the formula in lbtt's prefix notation.
 *
 *    void formatInput(Exceptional_ostream& stream, const string& formula)
 *           Prepares an input file for the external translator program.
 *           The default implementation simply writes `formula' (followed by a
 *           newline) into `stream'.
 *
 *    bool execSuccess(int exitcode)
 *           Tests whether the execution of the external program was performed
 *           successfully by examining the exit status of the program (as
 *           returned by a call to `system').  The default implementation
 *           returns true if and only if `exitcode' has the value 0.
 *
 * When performing the translation, the above functions will be called in the
 * following order:
 *    1. translateFormula   (translate the input formula into the external
 *                           program syntax)
 *    2. formatInput        (write the translated formula into a file)
 *    3. commandLine        (construct the command line required for executing
 *                           the external translator)
 *    4. execSuccess        (test whether the execution succeeded)
 *    5. parseAutomaton     (translate the constructed automaton into lbtt
 *                           format)
 *
 * The interface class additionally provides a function to help deleting any
 * temporary files or directories that might be created during the execution of
 * the external program, provided that their names are static or can be derived
 * from the names of the input/output files.  Each of these files should be
 * "registered" before calling the external program with the function
 *
 *    const char* registerTempFileObject
 *      (const string& filename, const TempFsysName::NameType t,
 *       const bool literal)
 *
 * where `filename' is the prefix of a temporary file name, `t' is a type
 * of the object (TempFsysName::FILE or TempFsysName::DIRECTORY), and
 * `literal' specifies whether `filename' should be interpreted literally or
 * not (if not, `filename' will be treated as a suggestion for the name
 * of the temporary file).  If the name is to be interpreted literally,
 * `filename' should contain the full path name of the temporary file to be
 * created.  In all cases, the function returns the full path name of the
 * temporary file or directory, or it throws an IOException (defined in
 * Exception.h) if the creation fails.
 *
 * All files or directories registered using this function will be
 * automatically deleted after the translation is finished or aborted.
 * The files or directories will be deleted in the reverse order of
 * registration, i.e., the most recently registered file/directory will be
 * deleted first.
 *
 *****************************************************************************/

class ExternalTranslator : public TranslatorInterface
{
public:
  ExternalTranslator();                             /* Constructor. */

  ~ExternalTranslator();                            /* Destructor. */

  const char* registerTempFileObject                /* Registers a temporary */
    (const string& filename = "",                   /* file or directory     */
     const TempFsysName::NameType                   /* such that it will be  */
       t = TempFsysName::FILE,                      /* automatically deleted */
     const bool literal = false);                   /* when the translation
						     * is complete.
						     */

  void translate                                    /* Main translation */
    (const ::Ltl::LtlFormula& formula,              /* function.        */
     const char* filename);

  virtual void translateFormula                     /* Translates an      */
    (const ::Ltl::LtlFormula& formula,              /* LtlFormula into    */
     string& translated_formula);                   /* the syntax of some
						     * external program.
						     */

  virtual void formatInput                          /* Prepares an input    */
    (Exceptional_ostream& stream,                   /* file for an external */
     const string& formula);                        /* translator.          */

  virtual string commandLine                        /* Constructs the       */
    (const string& input_filename,                  /* argument part of the */
     const string& output_filename) = 0;            /* command line for
						     * an external
						     * translator.
						     */

  virtual bool execSuccess(int exitcode);           /* Tests whether the
						     * execution of the
						     * external translator was
						     * successful.
						     */

  virtual void parseAutomaton                       /* Translates the        */
    (const string& input_filename,                  /* automaton description */
     const string& output_filename) = 0;            /* into lbtt format.     */

private:
  ExternalTranslator(const ExternalTranslator&);    /* Prevent copying and */
  ExternalTranslator& operator=                     /* assignment of       */
    (const ExternalTranslator&);                    /* ExternalTranslator
						     * objects.
						     */

  stack<TempFsysName*, deque<TempFsysName*> >       /* Stack for storing */
    temporary_file_objects;                         /* temporary file
                                                     * information.
						     */

  friend class SpinWrapper;                         /* Friend declarations. */
  friend class SpotWrapper;

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  class WriterErrorReporter                         /* Class for reporting */
  {                                                 /* about unsupported   */
  public:                                           /* input formula       */
    static void write                               /* operators.          */
      (Exceptional_ostream&, int);
  };
};



/******************************************************************************
 *
 * Inline function definitions for class ExternalTranslator.
 *
 *****************************************************************************/

/* ========================================================================= */
inline ExternalTranslator::ExternalTranslator()
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class ExternalTranslator.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline void ExternalTranslator::translateFormula
  (const ::Ltl::LtlFormula& formula, string& translated_formula)
/* ----------------------------------------------------------------------------
 *
 * Description:   Converts an LtlFormula object into a string.
 *
 * Arguments:     formula             --  An LtlFormula.
 *                translated_formula  --  A reference to a string for storing
 *                                        the result.
 *
 * Returns:       Nothing.  The result can be found in the string
 *                `translated_formula'.
 *
 * ------------------------------------------------------------------------- */
{
#ifdef HAVE_SSTREAM
  ostringstream formula_stream;
  formula.print(formula_stream, ::Ltl::LTL_PREFIX);
  translated_formula = formula_stream.str();
#else
  ostrstream formula_stream;
  formula.print(formula_stream, ::Ltl::LTL_PREFIX);
  formula_stream << ends;
  translated_formula = formula_stream.str();
  formula_stream.freeze(0);
#endif /* HAVE_SSTREAM */
}

/* ========================================================================= */
inline void ExternalTranslator::formatInput
  (Exceptional_ostream& stream, const string& formula)
/* ----------------------------------------------------------------------------
 *
 * Description:   Writes a string (assumed to contain an LTL formula in the
 *                input format of some external translator program) followed by
 *                a newline into a stream.
 *
 * Arguments:     stream   --  A reference to an Exceptional_ostream.
 *                formula  --  A reference to a constant string assumed to
 *                             contain an LTL formula.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  stream << formula << '\n';
}

/* ========================================================================= */
inline bool ExternalTranslator::execSuccess(int exitcode)
/* ----------------------------------------------------------------------------
 *
 * Description:   Tests whether the exit status of an external program is 0.
 *
 * Arguments:     exitcode  --  Exit status of an external program as reported
 *                              by a call to `system'.
 *
 * Returns:       `true' if the exit status is equal to 0.
 *
 * ------------------------------------------------------------------------- */
{
  return (exitcode == 0);
}



/******************************************************************************
 *
 * Inline function definitions for class
 * ExternalTranslator::WriterErrorReporter.
 *
 *****************************************************************************/

/* ========================================================================= */
inline void ExternalTranslator::WriterErrorReporter::write
  (Exceptional_ostream&, int)
/* ----------------------------------------------------------------------------
 *
 * Description:   Aborts the formula translation if the formula contains an
 *                unsupported operator.
 *
 * Arguments:     The arguments are required only for supporting the function
 *                call interface.
 *
 * Returns:       Nothing. Instead, throws an Exception with an error message.
 *
 * ------------------------------------------------------------------------- */
{
  throw Exception("unsupported operators in formula");
}

#endif /* !EXTERNALTRANSLATOR_H */
