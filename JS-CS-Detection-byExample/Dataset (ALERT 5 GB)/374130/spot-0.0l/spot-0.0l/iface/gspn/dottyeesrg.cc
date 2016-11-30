// Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include "eesrg.hh"
#include "tgbaalgos/dotty.hh"
#include "tgba/tgbaexplicit.hh"
#include "tgbaparse/public.hh"

int
main(int argc, char **argv)
  try
    {
      spot::gspn_environment env;

      if (argc <= 3)
	{
	  std::cerr << "usage: " << argv[0] << " model automata props..."
		    << std::endl;
	  exit(1);
	}

      while (argc > 3)
	env.declare(argv[--argc]);

      spot::gspn_eesrg_interface gspn(2, argv);
      spot::bdd_dict* dict = new spot::bdd_dict();

      spot::tgba_parse_error_list pel1;
      spot::tgba_explicit* control = spot::tgba_parse(argv[--argc], pel1,
						      dict, env);
      if (spot::format_tgba_parse_errors(std::cerr, pel1))
	return 2;

      {
	spot::tgba_gspn_eesrg a(dict, env, control);

	spot::dotty_reachable(std::cout, &a);
      }

      delete control;
      delete dict;
    }
  catch (spot::gspn_exeption e)
    {
      std::cerr << e << std::endl;
      throw;
    }
