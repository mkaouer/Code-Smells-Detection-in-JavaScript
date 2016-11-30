// Copyright (C) 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
// et Marie Curie.
//
// This file is part of Spot, a model checking library.
//
// Spot is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// Spot is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Spot; see the file COPYING.  If not, write to the Free
// Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
// 02111-1307, USA.

#include <iostream>
#include <cassert>
#include "evtgbaparse/public.hh"
#include "evtgbaalgos/save.hh"

void
syntax(char* prog)
{
  std::cerr << prog << " [-d] filename" << std::endl;
  exit(2);
}

int
main(int argc, char** argv)
{
  int exit_code = 0;

  if (argc < 2)
    syntax(argv[0]);

  bool debug = false;
  int filename_index = 1;

  if (!strcmp(argv[1], "-d"))
    {
      debug = true;
      if (argc < 3)
	syntax(argv[0]);
      filename_index = 2;
    }

  spot::evtgba_parse_error_list pel;
  spot::evtgba_explicit* a = spot::evtgba_parse(argv[filename_index],
						pel, debug);

  exit_code = spot::format_evtgba_parse_errors(std::cerr, argv[filename_index],
					       pel);

  if (a)
    {
      spot::evtgba_save_reachable(std::cout, a);
      delete a;
    }
  else
    {
      exit_code = 1;
    }

  return exit_code;
}
