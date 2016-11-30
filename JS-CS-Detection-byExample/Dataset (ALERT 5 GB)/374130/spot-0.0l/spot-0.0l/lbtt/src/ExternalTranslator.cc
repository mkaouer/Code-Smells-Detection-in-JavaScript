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
#include <csignal>
#include <cstdio>
#include <cstdlib>
#include <sys/stat.h>
#include <sys/types.h>
#ifdef HAVE_FCNTL_H
#include <fcntl.h>
#endif /* HAVE_FCNTL_H */
#ifdef HAVE_UNISTD_H
#include <unistd.h>
#endif /* HAVE_UNISTD_H */
#include <fstream>
#include "ExternalTranslator.h"

/******************************************************************************
 *
 * Function definitions for class ExternalTranslator.
 *
 *****************************************************************************/

/* ========================================================================= */
ExternalTranslator::~ExternalTranslator()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class ExternalTranslator.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  /*
   * Delete all registered temporary files or directories in the reverse order
   * of registration.
   */

  while (!temporary_file_objects.empty())
  {
    delete temporary_file_objects.top();
    temporary_file_objects.pop();
  }
}

/* ========================================================================= */
ExternalTranslator::TempFileObject&
ExternalTranslator::registerTempFileObject
  (const string& filename, TempFileObject::Type type)
/* ----------------------------------------------------------------------------
 *
 * Description:   Registers a temporary file or directory such that it will be
 *                automatically deleted when the ExternalTranslator object is
 *                destroyed.
 *
 * Arguments:     filename  --  Name of the temporary file or directory.
 *                type      --  Type of the object (TempFileObject::FILE or
 *                              TempFileObject::DIRECTORY).
 *
 * Returns:       A reference to the file object.
 *
 * ------------------------------------------------------------------------- */
{
  temporary_file_objects.push(new TempFileObject(filename, type));
  return *temporary_file_objects.top();
}

/* ========================================================================= */
void ExternalTranslator::translate
  (const ::Ltl::LtlFormula& formula, const char* filename)
/* ----------------------------------------------------------------------------
 *
 * Description:   Executes an external program which translates an LTL formula
 *                into a Büchi automaton and stores the automaton description
 *                in lbtt format into a file.
 *
 * Arguments:     formula   --  LTL formula to be translated.
 *                filename  --  Name of the output file.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  TempFileObject& external_program_input_file = registerTempFileObject();

  TempFileObject& external_program_output_file = registerTempFileObject();

  string translated_formula;
  translateFormula(formula, translated_formula);

  ofstream input_file;
  input_file.open(external_program_input_file.getName().c_str(),
		  ios::out | ios::trunc);
  if (!input_file.good())
    throw FileCreationException(string("`")
				+ external_program_input_file.getName()
				+ "'");

  Exceptional_ostream einput_file(&input_file, ios::failbit | ios::badbit);

  formatInput(einput_file, translated_formula);

  input_file.close();

  string command_line = string(command_line_arguments[2])
			+ commandLine(external_program_input_file.getName(),
				      external_program_output_file.getName());

  int exitcode = system(command_line.c_str());

  /*
   * system() blocks SIGINT and SIGQUIT.  If the child was killed
   * by such a signal, forward the signal to the current process.
   */
  if (WIFSIGNALED(exitcode) &&
      (WTERMSIG(exitcode) == SIGINT || WTERMSIG(exitcode) == SIGQUIT))
    raise(WTERMSIG(exitcode));

  if (!execSuccess(exitcode))
    throw ExecFailedException(command_line_arguments[2]);

  parseAutomaton(external_program_output_file.getName(), filename);
}



/******************************************************************************
 *
 * Function definitions for class ExternalTranslator::TempFileObject.
 *
 *****************************************************************************/

/* ========================================================================= */
ExternalTranslator::TempFileObject::TempFileObject
  (const string& filename, Type t)
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class TempFileObject. Creates a temporary
 *                file or a directory and tests whether it can really be
 *                written to (if not, a FileCreationException is thrown).
 *
 * Arguments:     filename  --  Name of the temporary file or directory.
 *                              If the filename is an empty string, the
 *                              filename is obtained by a call to tmpnam(3).
 *                t         --  Type of the object (TempFileObject::FILE or
 *                              TempFileObject::DIRECTORY).
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (filename.empty())
  {
    char tempname[L_tmpnam + 1];

    if (tmpnam(tempname) == 0)
      throw FileCreationException("a temporary file");

    name = tempname;
  }
  else
    name = filename;

  if (t == FILE)
  {
    ofstream tempfile;
    tempfile.open(name.c_str(), ios::out | ios::trunc);
    if (!tempfile.good())
      throw FileCreationException("a temporary file");
    tempfile.close();
  }
  else
  {
    if (mkdir(name.c_str(), 0700) != 0)
      throw FileCreationException("a temporary directory");
  }

  type = t;
}

/* ========================================================================= */
ExternalTranslator::TempFileObject::~TempFileObject()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class TempFileObject. Deletes the file or
 *                the directory associated with the object (displays a warning
 *                if this fails).
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (remove(name.c_str()) != 0)
  {
    string msg("error removing temporary ");

    if (type == TempFileObject::FILE)
      msg += "file";
    else
      msg += "directory";

    msg += " `" + name + "'";

    printWarning(msg);
  }
}
