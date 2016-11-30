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

#ifndef TRANSLATE_H
#define TRANSLATE_H

#ifdef __GNUC__
#pragma interface
#endif /* __GNUC__ */

#include <config.h>
#include <string>

/******************************************************************************
 *
 * Declarations of external variables and functions defined in translate.cc.
 *
 *****************************************************************************/

extern char** command_line_arguments;               /* Command line arguments
						     * passed to the main
						     * translator program.
						     */

void printWarning(const string& msg);               /* Displays a warning. */

#endif /* !TRANSLATE_H */
