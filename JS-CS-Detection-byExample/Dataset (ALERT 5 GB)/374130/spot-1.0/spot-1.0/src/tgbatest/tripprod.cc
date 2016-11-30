// Copyright (C) 2008 Laboratoire de Recherche et D�veloppement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2004 Laboratoire d'Informatique de Paris
// 6 (LIP6), d�partement Syst�mes R�partis Coop�ratifs (SRC),
// Universit� Pierre et Marie Curie.
//
// This file is part of Spot, a model checking library.
//
// Spot is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or
// (at your option) any later version.
//
// Spot is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

#include <iostream>
#include <cassert>
#include <cstdlib>
#include "tgba/tgbaexplicit.hh"
#include "tgba/tgbaproduct.hh"
#include "tgbaparse/public.hh"
#include "tgbaalgos/save.hh"
#include "ltlast/allnodes.hh"

void
syntax(char* prog)
{
  std::cerr << prog << " file1 file2 file3" << std::endl;
  exit(2);
}

int
main(int argc, char** argv)
{
  int exit_code = 0;

  if (argc != 4)
    syntax(argv[0]);

  spot::bdd_dict* dict = new spot::bdd_dict();

  spot::ltl::environment& env(spot::ltl::default_environment::instance());
  spot::tgba_parse_error_list pel1;
  spot::tgba_explicit_string* a1 = spot::tgba_parse(argv[1], pel1, dict, env);
  if (spot::format_tgba_parse_errors(std::cerr, argv[1], pel1))
    return 2;
  spot::tgba_parse_error_list pel2;
  spot::tgba_explicit_string* a2 = spot::tgba_parse(argv[2], pel2, dict, env);
  if (spot::format_tgba_parse_errors(std::cerr, argv[2], pel2))
    return 2;
  spot::tgba_parse_error_list pel3;
  spot::tgba_explicit_string* a3 = spot::tgba_parse(argv[3], pel3, dict, env);
  if (spot::format_tgba_parse_errors(std::cerr, argv[3], pel3))
    return 2;

  {
    spot::tgba_product p(a1, a2);
    spot::tgba_product p2(&p, a3);
    spot::tgba_save_reachable(std::cout, &p2);
  }

  assert(spot::ltl::unop::instance_count() == 0);
  assert(spot::ltl::binop::instance_count() == 0);
  assert(spot::ltl::multop::instance_count() == 0);
  assert(spot::ltl::atomic_prop::instance_count() != 0);
  delete a1;
  delete a2;
  delete a3;
  assert(spot::ltl::atomic_prop::instance_count() == 0);
  delete dict;
  return exit_code;
}
