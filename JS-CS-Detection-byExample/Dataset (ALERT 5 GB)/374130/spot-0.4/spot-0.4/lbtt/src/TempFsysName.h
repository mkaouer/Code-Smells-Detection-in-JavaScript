/*
 *  Copyright (C) 2004, 2005
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

#ifndef TEMPFSYSNAME_H
#define TEMPFSYSNAME_H

#include <config.h>
#include <cstdio>
#ifdef HAVE_UNISTD_H
#include <unistd.h>
#endif /* HAVE_UNISTD_H */

/******************************************************************************
 *
 * A class for temporary file system names.
 *
 *****************************************************************************/

class TempFsysName
{
public:
  TempFsysName();                                   /* Constructor. */

  ~TempFsysName();                                  /* Destructor. */

  enum NameType { FILE, DIRECTORY };                /* Types of temporary
						     * file system names.
						     */

  const char* allocate                              /* Allocates a name    */
    (const char* prefix = "",                       /* in the file system. */
     const NameType t = FILE,
     const bool literal = false);

  const char* get() const;                          /* Tells the name. */

private:
  TempFsysName(const TempFsysName&);                /* Prevent copying and */
  TempFsysName& operator=(const TempFsysName&);     /* assignment of
						     * TempFsysName objects.
						     */

  void releaseName();                               /* Frees a name in the file
						     * system.
						     */

  char* name;                                       /* Temporary name. */

  NameType type;                                    /* Tells whether the name
						     * refers to a file or a
						     * directory.
						     */

  static unsigned long int                          /* Counter for the     */
    number_of_allocated_names;                      /* number of generated
						     * temporary names.
						     */
};



/******************************************************************************
 *
 * Inline function definitions for class TempFsysName.
 *
 *****************************************************************************/

/* ========================================================================= */
inline TempFsysName::TempFsysName() : name(static_cast<char*>(0))
/* ----------------------------------------------------------------------------
 *
 * Description:   Constructor for class TempFsysName.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
}

/* ========================================================================= */
inline TempFsysName::~TempFsysName()
/* ----------------------------------------------------------------------------
 *
 * Description:   Destructor for class TempFsysName.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  releaseName();
}

/* ========================================================================= */
inline const char* TempFsysName::get() const
/* ----------------------------------------------------------------------------
 *
 * Description:   Tells the name associated with the TempFsysName object.
 *
 * Arguments:     None.
 *
 * Returns:       A pointer to a constant C-style string containing the name
 *                associated with the TempFsysName object.  If the TempFsysName
 *                object has not yet been (successfully) associated with a file
 *                using TempFsysName::allocate, this pointer has the value 0.
 *
 * ------------------------------------------------------------------------- */
{
  return name;
}

/* ========================================================================= */
inline void TempFsysName::releaseName()
/* ----------------------------------------------------------------------------
 *
 * Description:   Deallocates the memory reserved for `this->name' and frees
 *                the name also in the file system by removing the file or
 *                directory associated with the object.  If the name
 *                is associated with a directory, the directory is assumed to
 *                be empty.
 *
 * Arguments:     None.
 *
 * Returns:       Nothing.
 *
 * ------------------------------------------------------------------------- */
{
  if (name == static_cast<char*>(0))
    return;
  if (type == FILE)
    remove(name);
  else
    rmdir(name);
  delete[] name;
  name = 0;
}

#endif /* !TEMPFSYSNAME_H */
