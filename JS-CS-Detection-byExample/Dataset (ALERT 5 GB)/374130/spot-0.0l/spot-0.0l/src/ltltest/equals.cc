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

#include <iostream>
#include <cassert>
#include "ltlparse/public.hh"
#include "ltlvisit/lunabbrev.hh"
#include "ltlvisit/tunabbrev.hh"
#include "ltlvisit/dump.hh"
#include "ltlvisit/nenoform.hh"
#include "ltlvisit/destroy.hh"
#include "ltlast/allnodes.hh"

void
syntax(char* prog)
{
  std::cerr << prog << " formula1 formula2" << std::endl;
  exit(2);
}

int
main(int argc, char** argv)
{
  if (argc != 3)
    syntax(argv[0]);


  spot::ltl::parse_error_list p1;
  spot::ltl::formula* f1 = spot::ltl::parse(argv[1], p1);

  if (spot::ltl::format_parse_errors(std::cerr, argv[1], p1))
    return 2;

  spot::ltl::parse_error_list p2;
  spot::ltl::formula* f2 = spot::ltl::parse(argv[2], p2);

  if (spot::ltl::format_parse_errors(std::cerr, argv[2], p2))
    return 2;

#if (defined LUNABBREV) || (defined TUNABBREV) || (defined NENOFORM)
  spot::ltl::formula* tmp;
#endif
#ifdef LUNABBREV
  tmp = f1;
  f1 = spot::ltl::unabbreviate_logic(f1);
  spot::ltl::destroy(tmp);
  spot::ltl::dump(std::cout, f1);
  std::cout << std::endl;
#endif
#ifdef TUNABBREV
  tmp = f1;
  f1 = spot::ltl::unabbreviate_ltl(f1);
  spot::ltl::destroy(tmp);
  spot::ltl::dump(std::cout, f1);
  std::cout << std::endl;
#endif
#ifdef NENOFORM
  tmp = f1;
  f1 = spot::ltl::negative_normal_form(f1);
  spot::ltl::destroy(tmp);
  spot::ltl::dump(std::cout, f1);
  std::cout << std::endl;
#endif

  int exit_code = f1 != f2;

  spot::ltl::destroy(f1);
  spot::ltl::destroy(f2);
  assert(spot::ltl::atomic_prop::instance_count() == 0);
  assert(spot::ltl::unop::instance_count() == 0);
  assert(spot::ltl::binop::instance_count() == 0);
  assert(spot::ltl::multop::instance_count() == 0);

  return exit_code;
}
