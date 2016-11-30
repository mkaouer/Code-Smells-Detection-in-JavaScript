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

#include "gspn.hh"
#include "ltlparse/public.hh"
#include "ltlvisit/destroy.hh"
#include "tgba/tgbatba.hh"
#include "tgba/tgbaproduct.hh"
#include "tgbaalgos/ltl2tgba_lacim.hh"
#include "tgbaalgos/ltl2tgba_fm.hh"
#include "tgbaalgos/magic.hh"
#include "tgbaalgos/emptinesscheck.hh"

void
syntax(char* prog)
{
  std::cerr << "Usage: "<< prog
	    << " [OPTIONS...] model formula props..."   << std::endl
	    << std::endl
	    << "  -c  compute a counter example" << std::endl
	    << "      (instead of just checking for emptiness)" << std::endl
	    << std::endl
	    << "  -e  use Couvreur's emptiness-check (default)" << std::endl
	    << "  -m  degeneralize and perform a magic-search" << std::endl
	    << std::endl
            << "  -l  use Couvreur's LaCIM algorithm for translation (default)"
	    << std::endl
            << "  -f  use Couvreur's FM algorithm for translation"
	    << std::endl;
  exit(2);
}

int
main(int argc, char **argv)
  try
    {
      int formula_index = 1;
      enum { Couvreur, Magic } check = Couvreur;
      enum { Lacim, Fm } trans = Lacim;
      bool compute_counter_example = false;

      spot::gspn_environment env;

      while (formula_index < argc && *argv[formula_index] == '-')
	{
	  if (!strcmp(argv[formula_index], "-c"))
	    {
	      compute_counter_example = true;
	    }
	  else if (!strcmp(argv[formula_index], "-e"))
	    {
	      check = Couvreur;
	    }
	  else if (!strcmp(argv[formula_index], "-m"))
	    {
	      check = Magic;
	    }
	  else if (!strcmp(argv[formula_index], "-l"))
	    {
	      trans = Lacim;
	    }
	  else if (!strcmp(argv[formula_index], "-f"))
	    {
	      trans = Fm;
	    }
	  else
	    {
	      syntax(argv[0]);
	    }
	  ++formula_index;
	}
      if (argc < formula_index + 3)
	syntax(argv[0]);


      while (argc > formula_index + 2)
	{
	  env.declare(argv[argc - 1]);
	  --argc;
	}

      spot::ltl::parse_error_list pel;
      spot::ltl::formula* f = spot::ltl::parse(argv[formula_index + 1],
					       pel, env);

      if (spot::ltl::format_parse_errors(std::cerr,
					 argv[formula_index + 1], pel))
	exit(2);

      argv[1] = argv[formula_index];
      spot::gspn_interface gspn(2, argv);
      spot::bdd_dict* dict = new spot::bdd_dict();

      spot::tgba* a_f = 0;
      switch (trans)
	{
	case Fm:
	  a_f = spot::ltl_to_tgba_fm(f, dict);
	  break;
	case Lacim:
	  a_f = spot::ltl_to_tgba_lacim(f, dict);
	  break;
	}
      spot::ltl::destroy(f);

      spot::tgba* model        = new spot::tgba_gspn(dict, env);
      spot::tgba_product* prod = new spot::tgba_product(model, a_f);

      switch (check)
	{
	case Couvreur:
	  {
	    spot::emptiness_check ec(prod);
	    bool res = ec.check();
	    if (!res)
	      {
		if (compute_counter_example)
		  {
		    ec.counter_example();
		    ec.print_result(std::cout, model);
		  }
		else
		  {
		    std::cout << "non empty" << std::endl;
		  }
		exit(1);
	      }
	    else
	      {
		std::cout << "empty" << std::endl;
	      }
	  }
	  break;
	case Magic:
	  {
	    spot::tgba_tba_proxy* d  = new spot::tgba_tba_proxy(prod);
	    spot::magic_search ms(d);

	    if (ms.check())
	      {
		if (compute_counter_example)
		  ms.print_result (std::cout, model);
		else
		  std::cout << "non-empty" << std::endl;
		exit(1);
	      }
	    else
	      {
		std::cout << "empty" << std::endl;
	      }
	    delete d;
	  }
	}
      delete prod;
      delete model;
      delete a_f;
      delete dict;
    }
  catch (spot::gspn_exeption e)
    {
      std::cerr << e << std::endl;
      throw;
    }
