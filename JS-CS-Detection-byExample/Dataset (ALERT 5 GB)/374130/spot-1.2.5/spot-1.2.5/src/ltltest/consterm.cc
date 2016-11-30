// -*- coding: utf-8 -*-
// Copyright (C) 2010, 2011, 2012 Laboratoire de Recherche et
// Dévelopement de l'Epita (LRDE).
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
#include "ltlparse/public.hh"
#include "ltlast/allnodes.hh"

void
syntax(char *prog)
{
  std::cerr << prog << " formula" << std::endl;
  exit(2);
}

int
main(int argc, char **argv)
{
  if (argc != 2)
    syntax(argv[0]);

  spot::ltl::parse_error_list p1;
  const spot::ltl::formula* f1 = spot::ltl::parse_sere(argv[1], p1);

  if (spot::ltl::format_parse_errors(std::cerr, argv[1], p1))
    return 2;

  bool b = f1->accepts_eword();

  std::cout << b << std::endl;

  f1->destroy();
  assert(spot::ltl::atomic_prop::instance_count() == 0);
  assert(spot::ltl::unop::instance_count() == 0);
  assert(spot::ltl::binop::instance_count() == 0);
  assert(spot::ltl::bunop::instance_count() == 0);
  assert(spot::ltl::multop::instance_count() == 0);
  return b;
}
