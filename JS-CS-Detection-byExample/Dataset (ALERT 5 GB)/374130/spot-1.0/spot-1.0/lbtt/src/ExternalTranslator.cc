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

#include <config.h>
#include <cstdio>
#include <cstdlib>
#include <cstring>
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
const char* ExternalTranslator::registerTempFileObject
  (const string& filename, const TempFsysName::NameType type,
   const bool literal)
/* ----------------------------------------------------------------------------
 *
 * Description:   Registers a temporary file or directory such that it will be
 *                automatically deleted when the ExternalTranslator object is
 *                destroyed.
 *
 * Arguments:     filename  --  Name of the temporary file or directory.  If
 *                              empty, a new name will be created.
 *                type      --  Type of the object (TempFileObject::FILE or
 *                              TempFileObject::DIRECTORY).
 *                literal   --  Tells whether the file name should be
 *                              interpreted literally.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  TempFsysName* name = new TempFsysName;
  name->allocate(filename.c_str(), type, literal);
  temporary_file_objects.push(name);
  return name->get();
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
  const char* external_program_input_file
    = registerTempFileObject("lbtt-translate");

  const char* external_program_output_file
    = registerTempFileObject("lbtt-translate");

  string translated_formula;
  translateFormula(formula, translated_formula);

  ofstream input_file;
  input_file.open(external_program_input_file, ios::out | ios::trunc);
  if (!input_file.good())
    throw FileCreationException(string("`") + external_program_input_file
				+ "'");

  Exceptional_ostream einput_file(&input_file, ios::failbit | ios::badbit);

  formatInput(einput_file, translated_formula);

  input_file.close();

  string command_line = string(command_line_arguments[2])
                        + commandLine(external_program_input_file,
				      external_program_output_file);

  if (!execSuccess(system(command_line.c_str())))
    throw ExecFailedException(command_line_arguments[2]);

  parseAutomaton(external_program_output_file, filename);
}
