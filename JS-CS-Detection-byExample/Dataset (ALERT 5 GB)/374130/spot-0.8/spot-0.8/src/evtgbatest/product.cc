// Copyright (C) 2008 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2004, 2005 Laboratoire d'Informatique de Paris
// 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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
#include <cstdlib>
#include <cstring>
#include "evtgbaparse/public.hh"
#include "evtgbaalgos/save.hh"
#include "evtgbaalgos/dotty.hh"
#include "evtgba/product.hh"

void
syntax(char* prog)
{
  std::cerr << prog << " [-d] [-D] filenames..." << std::endl;
  exit(2);
}

int
main(int argc, char** argv)
{
  int exit_code = 0;

  if (argc < 2)
    syntax(argv[0]);

  bool debug = false;
  bool dotty = false;
  int filename_index = 1;

  while (argv[filename_index][0] == '-' && filename_index < argc)
    {
      if (!strcmp(argv[filename_index], "-d"))
	debug = true;
      else if (!strcmp(argv[filename_index], "-D"))
	dotty = true;
      else
	syntax(argv[0]);
      ++filename_index;
    }

  if (filename_index == argc)
    syntax(argv[0]);

  spot::evtgba_product::evtgba_product_operands op;

  while (filename_index < argc)
    {
      spot::evtgba_parse_error_list pel;
      spot::evtgba_explicit* a = spot::evtgba_parse(argv[filename_index],
						    pel, debug);

      exit_code = spot::format_evtgba_parse_errors(std::cerr,
						   argv[filename_index],
						   pel);

      if (a)
	{
	  op.push_back(a);
	}
      else
	{
	  exit_code = 1;
	}

      ++filename_index;
    }

  if (op.empty())
    return exit_code;

  spot::evtgba_product p(op);

  if (dotty)
    spot::dotty_reachable(std::cout, &p);
  else
    spot::evtgba_save_reachable(std::cout, &p);

  for (spot::evtgba_product::evtgba_product_operands::iterator i = op.begin();
       i != op.end(); ++i)
    delete *i;

  return exit_code;
}
