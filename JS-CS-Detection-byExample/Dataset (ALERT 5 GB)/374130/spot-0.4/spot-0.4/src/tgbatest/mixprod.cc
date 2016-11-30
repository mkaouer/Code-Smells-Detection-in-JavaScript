// Copyright (C) 2003, 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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
#include "tgbaalgos/ltl2tgba_lacim.hh"
#include "tgba/tgbaproduct.hh"
#include "tgba/tgbabddconcreteproduct.hh"
#include "tgbaparse/public.hh"
#include "tgbaalgos/save.hh"

void
syntax(char* prog)
{
  std::cerr << prog << " formula file" << std::endl;
  exit(2);
}

int
main(int argc, char** argv)
{
  int exit_code = 0;

  if (argc != 3)
    syntax(argv[0]);

  spot::bdd_dict* dict = new spot::bdd_dict();

  spot::ltl::environment& env(spot::ltl::default_environment::instance());

  spot::ltl::parse_error_list pel1;
  spot::ltl::formula* f1 = spot::ltl::parse(argv[1], pel1, env);
  if (spot::ltl::format_parse_errors(std::cerr, argv[1], pel1))
    return 2;

  spot::tgba_parse_error_list pel2;
  spot::tgba_explicit* a2 = spot::tgba_parse(argv[2], pel2, dict, env);
  if (spot::format_tgba_parse_errors(std::cerr, argv[2], pel2))
    return 2;

  {
    spot::tgba_bdd_concrete* a1 = spot::ltl_to_tgba_lacim(f1, dict);
    spot::ltl::destroy(f1);
    spot::tgba_product p(a1, a2);
    spot::tgba_save_reachable(std::cout, &p);
    delete a1;
  }

  assert(spot::ltl::unop::instance_count() == 0);
  assert(spot::ltl::binop::instance_count() == 0);
  assert(spot::ltl::multop::instance_count() == 0);
  delete a2;
  assert(spot::ltl::atomic_prop::instance_count() == 0);
  delete dict;
  return exit_code;
}
