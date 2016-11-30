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

#include <config.h>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <sys/stat.h>
#include <sys/types.h>
#ifdef HAVE_FCNTL_H
#include <fcntl.h>
#endif /* HAVE_FCNTL_H */
#include <string>
#include "Exception.h"
#include "StringUtil.h"
#include "TempFsysName.h"

/******************************************************************************
 *
 * Number of generated temporary names.
 *
 *****************************************************************************/

unsigned long int TempFsysName::number_of_allocated_names = 0;



/******************************************************************************
 *
 * Function definitions for class TempFsysName.
 *
 *****************************************************************************/

/* ========================================================================= */
const char* TempFsysName::allocate
  (const char* prefix, const NameType t, const bool literal)
/* ----------------------------------------------------------------------------
 *
 * Description:   Associates a TempFsysName object with a temporary name.  (As
 *                a side effect, the function actually creates an empty
 *                temporary file or a directory to reserve the name in the file
 *                system.  The file or directory should never be removed
 *                explicitly; it is removed automatically when the TempFsysName
 *                object is destroyed or another call is made to
 *                `this->allocate'.)
 *
 * Arguments:     prefix   --  Pointer to a C-style string containing a prefix
 *                             for the temporary name (empty by default).  If
 *                             `literal == true', `prefix' (if nonempty) is
 *                             assumed to contain the full path for the
 *                             temporary file or directory; otherwise the
 *                             function will reserve a temporary name in the
 *                             `P_tmpdir' directory.  This name will consist
 *                             of the value of `prefix' (if nonempty), followed
 *                             by the current value of
 *                             `TempFsysName::number_of_allocated_names' and
 *                             the current process id, separated by dots.
 *                t        --  Determines the type of the name (file or a
 *                             directory).
 *                literal  --  See above.
 *
 * Returns:       A pointer to a constant C-style string containing the
 *                temporary name.  The function throws an IOException if the
 *                name allocation or the file or directory creation fails.
 *
 * ------------------------------------------------------------------------- */
{
  releaseName();

  using ::StringUtil::toString;
  string tempname;

  try
  {
    if (!literal || strlen(prefix) == 0)
    {
      tempname = toString(P_tmpdir) + "/";
      if (strlen(prefix))
	tempname += string(prefix) + ".";
      tempname += toString(number_of_allocated_names) + "."
	          + toString(getpid());
      ++number_of_allocated_names;
    }
    else
      tempname = prefix;

    name = new char[tempname.length() + 1];
    strcpy(name, tempname.c_str());
  }
  catch (const bad_alloc&)
  {
    name = 0;
    throw IOException
      ("unable to allocate a temporary name in the file system");
  }

  type = t;
  if (t == FILE)
  {
    int fd = open(name, O_RDWR | O_CREAT | O_EXCL, S_IREAD | S_IWRITE);
    if (fd == -1)
      throw IOException("unable to create a temporary file");
    close(fd);
  }
  else if (mkdir(name, S_IRWXU) == -1)
    throw IOException("unable to create a temporary directory");

  return name;
}
