// Copyright (C) 2008, 2009 Laboratoire de Recherche et DÃ©veloppement
// de l'Epita (LRDE).
// Copyright (C) 2003 Laboratoire d'Informatique de Paris 6
// (LIP6), département Systèmes Répartis Coopératifs (SRC), Université
// Pierre et Marie Curie.
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
#include "ltlparse/public.hh"
#include "ltlvisit/dump.hh"
#include "ltlvisit/dotty.hh"
#include "ltlast/allnodes.hh"

void
syntax(char* prog)
{
  std::cerr << prog << " [-d] formula" << std::endl;
  exit(2);
}

void
dump_instances(const std::string& label)
{
  std::cerr << "=== " << label << " ===" << std::endl;
  spot::ltl::atomic_prop::dump_instances(std::cerr);
  spot::ltl::unop::dump_instances(std::cerr);
  spot::ltl::binop::dump_instances(std::cerr);
  spot::ltl::multop::dump_instances(std::cerr);
}

int
main(int argc, char** argv)
{
  int exit_code = 0;

  if (argc < 2)
    syntax(argv[0]);

  bool debug = false;
  bool debug_ref = false;
  int formula_index = 1;

  if (!strcmp(argv[1], "-d"))
    {
      debug = true;
      if (argc < 3)
	syntax(argv[0]);
      formula_index = 2;
    }
  else if (!strcmp(argv[1], "-r"))
    {
      debug_ref = true;
      if (argc < 3)
	syntax(argv[0]);
      formula_index = 2;
    }

  spot::ltl::environment& env(spot::ltl::default_environment::instance());
  spot::ltl::parse_error_list pel;
  spot::ltl::formula* f = spot::ltl::parse(argv[formula_index],
					   pel, env, debug);

  exit_code =
    spot::ltl::format_parse_errors(std::cerr, argv[formula_index], pel);


  if (f)
    {
      if (debug_ref)
	dump_instances("before");

#ifdef DOTTY
      spot::ltl::dotty(std::cout, f);
#else
      spot::ltl::dump(std::cout, f);
      std::cout << std::endl;
#endif
      f->destroy();

      if (debug_ref)
	dump_instances("after");
    }
  else
    {
      exit_code = 1;
    }

  assert(spot::ltl::atomic_prop::instance_count() == 0);
  assert(spot::ltl::unop::instance_count() == 0);
  assert(spot::ltl::binop::instance_count() == 0);
  assert(spot::ltl::multop::instance_count() == 0);
  return exit_code;
}
