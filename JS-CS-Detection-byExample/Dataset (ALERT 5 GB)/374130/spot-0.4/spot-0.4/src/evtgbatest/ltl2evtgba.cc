// Copyright (C) 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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
#include "ltlvisit/destroy.hh"
#include "ltlast/allnodes.hh"
#include "ltlparse/public.hh"
#include "evtgbaparse/public.hh"
#include "evtgbaalgos/save.hh"
#include "evtgbaalgos/dotty.hh"
#include "evtgbaalgos/tgba2evtgba.hh"
#include "tgbaalgos/ltl2tgba_fm.hh"

void
syntax(char* prog)
{
  std::cerr << prog << " [-d] [-D] formula" << std::endl;
  exit(2);
}

int
main(int argc, char** argv)
{
  int exit_code = 0;

  if (argc < 2)
    syntax(argv[0]);

  bool debug_opt = false;
  bool dotty_opt = false;

  bool post_branching = false;
  bool fair_loop_approx = false;

  int formula_index = 1;

  spot::ltl::environment& env(spot::ltl::default_environment::instance());
  spot::ltl::atomic_prop_set* unobservables = new spot::ltl::atomic_prop_set;

  while (argv[formula_index][0] == '-' && formula_index < argc)
    {
      if (!strcmp(argv[formula_index], "-d"))
	{
	  debug_opt = true;
	}
      else if (!strcmp(argv[formula_index], "-D"))
	{
	  dotty_opt = true;
	}
      else if (!strcmp(argv[formula_index], "-L"))
	{
	  fair_loop_approx = true;
	}
      else if (!strcmp(argv[formula_index], "-p"))
	{
	  post_branching = true;
	}
      else if (!strncmp(argv[formula_index], "-U", 2))
	{
	  // Parse -U's argument.
	  const char* tok = strtok(argv[formula_index] + 2, ", \t;");
	  while (tok)
	    {
	      unobservables->insert
		(static_cast<spot::ltl::atomic_prop*>(env.require(tok)));
	      tok = strtok(0, ", \t;");
	    }
	}
      else
	{
	  syntax(argv[0]);
	}
      ++formula_index;
    }

  if (formula_index == argc)
    syntax(argv[0]);

  spot::bdd_dict* dict = new spot::bdd_dict();

  spot::ltl::formula* f = 0;

  {
    spot::ltl::parse_error_list pel;
    f = spot::ltl::parse(argv[formula_index], pel, env, debug_opt);
    exit_code = spot::ltl::format_parse_errors(std::cerr, argv[formula_index],
					       pel);
  }

  if (f)
    {
      spot::tgba* a = spot::ltl_to_tgba_fm(f, dict, false, true,
					   post_branching,
					   fair_loop_approx, unobservables);

      spot::ltl::destroy(f);
      spot::evtgba* e = spot::tgba_to_evtgba(a);

      if (dotty_opt)
	spot::dotty_reachable(std::cout, e);
      else
	spot::evtgba_save_reachable(std::cout, e);

      delete e;
      delete a;
    }

  for (spot::ltl::atomic_prop_set::iterator i = unobservables->begin();
       i != unobservables->end(); ++i)
    spot::ltl::destroy(*i);
  delete unobservables;

  delete dict;

  return exit_code;
}
