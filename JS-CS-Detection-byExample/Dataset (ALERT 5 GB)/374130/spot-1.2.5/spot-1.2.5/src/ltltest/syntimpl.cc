// -*- coding: utf-8 -*-
// Copyright (C) 2008, 2009, 2010, 2011, 2012 Laboratoire de Recherche
// et Développement de l'Epita (LRDE).
// Copyright (C) 2004 Laboratoire d'Informatique de Paris 6
// (LIP6), département Systèmes Répartis Coopératifs (SRC), Université
// Pierre et Marie Curie.
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
#include "ltlvisit/lunabbrev.hh"
#include "ltlvisit/tunabbrev.hh"
#include "ltlvisit/dump.hh"
#include "ltlvisit/tostring.hh"
#include "ltlvisit/simplify.hh"
#include "ltlast/allnodes.hh"
#include "ltlvisit/nenoform.hh"

void
syntax(char* prog)
{
  std::cerr << prog << " formula1 formula2?" << std::endl;
  exit(2);
}

int
main(int argc, char** argv)
{
  if (argc < 4)
    syntax(argv[0]);

  int opt = atoi(argv[1]);

  spot::ltl::parse_error_list p1;
  const spot::ltl::formula* ftmp1 = spot::ltl::parse(argv[2], p1);

  if (spot::ltl::format_parse_errors(std::cerr, argv[2], p1))
    return 2;

  spot::ltl::parse_error_list p2;
  const spot::ltl::formula* ftmp2 = spot::ltl::parse(argv[3], p2);

  if (spot::ltl::format_parse_errors(std::cerr, argv[3], p2))
    return 2;

  const spot::ltl::formula* f1 = spot::ltl::negative_normal_form(ftmp1);
  const spot::ltl::formula* f2 = spot::ltl::negative_normal_form(ftmp2);

  std::string f1s = spot::ltl::to_string(f1);
  std::string f2s = spot::ltl::to_string(f2);

  int exit_return = 0;
  spot::ltl::ltl_simplifier* c = new spot::ltl::ltl_simplifier;

  switch (opt)
    {
    case 0:
      std::cout << "Test f1 < f2" << std::endl;
      if (c->syntactic_implication(f1, f2))
	{
	  std::cout << f1s << " < " << f2s << std::endl;
	  exit_return = 1;
	}
      break;

    case 1:
      std::cout << "Test !f1 < f2" << std::endl;
      if (c->syntactic_implication_neg(f1, f2, false))
	{
	  std::cout << "!(" << f1s << ") < " << f2s << std::endl;
	  exit_return = 1;
	}
      break;

    case 2:
      std::cout << "Test f1 < !f2" << std::endl;
      if (c->syntactic_implication_neg(f1, f2, true))
	{
	  std::cout << f1s << " < !(" << f2s << ")" << std::endl;
	  exit_return = 1;
	}
      break;
    default:
      break;
    }

  spot::ltl::dump(std::cout, f1) << std::endl;
  spot::ltl::dump(std::cout, f2) << std::endl;

  f1->destroy();
  f2->destroy();
  ftmp1->destroy();
  ftmp2->destroy();

  delete c;

  assert(spot::ltl::atomic_prop::instance_count() == 0);
  assert(spot::ltl::unop::instance_count() == 0);
  assert(spot::ltl::binop::instance_count() == 0);
  assert(spot::ltl::multop::instance_count() == 0);

  return exit_return;
}
