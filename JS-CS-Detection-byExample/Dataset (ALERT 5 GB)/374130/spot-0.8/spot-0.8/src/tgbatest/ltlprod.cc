// Copyright (C) 2008, 2009 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2004 Laboratoire d'Informatique de
// Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
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
#include "ltlast/allnodes.hh"
#include "ltlparse/public.hh"
#include "tgbaalgos/ltl2tgba_lacim.hh"
#include "tgba/tgbaproduct.hh"
#include "tgba/tgbabddconcreteproduct.hh"
#include "tgbaalgos/dotty.hh"

void
syntax(char* prog)
{
  std::cerr << prog << " formula1 formula2" << std::endl;
  exit(2);
}

int
main(int argc, char** argv)
{
  int exit_code = 0;

  if (argc != 3)
    syntax(argv[0]);

  spot::ltl::environment& env(spot::ltl::default_environment::instance());

  spot::ltl::parse_error_list pel1;
  spot::ltl::formula* f1 = spot::ltl::parse(argv[1], pel1, env);

  if (spot::ltl::format_parse_errors(std::cerr, argv[1], pel1))
    return 2;

  spot::ltl::parse_error_list pel2;
  spot::ltl::formula* f2 = spot::ltl::parse(argv[2], pel2, env);

  if (spot::ltl::format_parse_errors(std::cerr, argv[2], pel2))
    return 2;

  spot::bdd_dict* dict = new spot::bdd_dict();
  {
    spot::tgba_bdd_concrete* a1 = spot::ltl_to_tgba_lacim(f1, dict);
    spot::tgba_bdd_concrete* a2 = spot::ltl_to_tgba_lacim(f2, dict);
    f1->destroy();
    f2->destroy();

#ifdef BDD_CONCRETE_PRODUCT
    spot::tgba_bdd_concrete* p = spot::product(a1, a2);
#else
    spot::tgba_product* p = new spot::tgba_product(a1, a2);
#endif
    spot::dotty_reachable(std::cout, p);
    delete p;
    delete a1;
    delete a2;
  }

  assert(spot::ltl::atomic_prop::instance_count() == 0);
  assert(spot::ltl::unop::instance_count() == 0);
  assert(spot::ltl::binop::instance_count() == 0);
  assert(spot::ltl::multop::instance_count() == 0);
  delete dict;
  return exit_code;
}
