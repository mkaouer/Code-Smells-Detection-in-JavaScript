// Copyright (C) 2003, 2004, 2006  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include "gspn.hh"
#include "tgbaalgos/dotty.hh"

int
main(int argc, char **argv)
  try
    {
      spot::ltl::declarative_environment env;

      if (argc <= 2)
	{
	  std::cerr << "usage: " << argv[0] << " model props..." << std::endl;
	  exit(1);
	}

      while (argc > 2)
	{
	  env.declare(argv[argc - 1]);
	  --argc;
	}

      spot::bdd_dict* dict = new spot::bdd_dict();
      spot::gspn_interface gspn(2, argv, dict, env);
      spot::tgba* a = gspn.automaton();

      spot::dotty_reachable(std::cout, a);

      delete a;
      delete dict;
    }
  catch (spot::gspn_exception e)
    {
      std::cerr << e << std::endl;
      throw;
    }
