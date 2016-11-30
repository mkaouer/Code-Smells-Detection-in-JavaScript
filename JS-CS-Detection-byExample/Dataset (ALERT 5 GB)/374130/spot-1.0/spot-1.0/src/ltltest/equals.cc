// -*- coding: utf-8 -*-
// Copyright (C) 2008, 2009, 2010, 2011, 2012 Laboratoire de Recherche
// et Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2006 Laboratoire d'Informatique de
// Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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
#include <cstring>
#include "ltlparse/public.hh"
#include "ltlvisit/lunabbrev.hh"
#include "ltlvisit/tunabbrev.hh"
#include "ltlvisit/dump.hh"
#include "ltlvisit/wmunabbrev.hh"
#include "ltlvisit/nenoform.hh"
#include "ltlast/allnodes.hh"
#include "ltlvisit/simplify.hh"
#include "ltlvisit/tostring.hh"

void
syntax(char* prog)
{
  std::cerr << prog << " [-E] formula1 formula2" << std::endl;
  exit(2);
}

int
main(int argc, char** argv)
{
  bool check_first = true;

  if (argc > 1 && !strcmp(argv[1], "-E"))
    {
      check_first = false;
      argv[1] = argv[0];
      ++argv;
      --argc;
    }
  if (argc != 3)
    syntax(argv[0]);

  spot::ltl::parse_error_list p1;
  const spot::ltl::formula* f1 = spot::ltl::parse(argv[1], p1);

  if (check_first && spot::ltl::format_parse_errors(std::cerr, argv[1], p1))
    return 2;

  spot::ltl::parse_error_list p2;
  const spot::ltl::formula* f2 = spot::ltl::parse(argv[2], p2);

  if (spot::ltl::format_parse_errors(std::cerr, argv[2], p2))
    return 2;

  int exit_code;

  {
#if defined LUNABBREV || defined TUNABBREV || defined NENOFORM || defined WM
    const spot::ltl::formula* tmp;
#endif
#ifdef LUNABBREV
    tmp = f1;
    f1 = spot::ltl::unabbreviate_logic(f1);
    tmp->destroy();
    spot::ltl::dump(std::cout, f1);
    std::cout << std::endl;
#endif
#ifdef TUNABBREV
    tmp = f1;
    f1 = spot::ltl::unabbreviate_ltl(f1);
    tmp->destroy();
    spot::ltl::dump(std::cout, f1);
    std::cout << std::endl;
#endif
#ifdef WM
    tmp = f1;
    f1 = spot::ltl::unabbreviate_wm(f1);
    tmp->destroy();
    spot::ltl::dump(std::cout, f1);
    std::cout << std::endl;
#endif
#ifdef NENOFORM
    tmp = f1;
    f1 = spot::ltl::negative_normal_form(f1);
    tmp->destroy();
    spot::ltl::dump(std::cout, f1);
    std::cout << std::endl;
#endif
#ifdef REDUC
    spot::ltl::ltl_simplifier_options opt(true, true, true, false, false);
    spot::ltl::ltl_simplifier simp(opt);
    {
      const spot::ltl::formula* tmp;
      tmp = f1;
      f1 = simp.simplify(f1);
      tmp->destroy();
    }
    spot::ltl::dump(std::cout, f1);
    std::cout << std::endl;
#endif
#ifdef REDUC_TAU
    spot::ltl::ltl_simplifier_options opt(false, false, false, true, false);
    spot::ltl::ltl_simplifier simp(opt);
    {
      const spot::ltl::formula* tmp;
      tmp = f1;
      f1 = simp.simplify(f1);
      tmp->destroy();
    }
    spot::ltl::dump(std::cout, f1);
    std::cout << std::endl;
#endif
#ifdef REDUC_TAUSTR
    spot::ltl::ltl_simplifier_options opt(false, false, false, true, true);
    spot::ltl::ltl_simplifier simp(opt);
    {
      const spot::ltl::formula* tmp;
      tmp = f1;
      f1 = simp.simplify(f1);
      tmp->destroy();
    }
    spot::ltl::dump(std::cout, f1);
    std::cout << std::endl;
#endif

    exit_code = f1 != f2;

#if (!defined(REDUC) && !defined(REDUC_TAU) && !defined(REDUC_TAUSTR))
    spot::ltl::ltl_simplifier simp;
#endif

    if (!simp.are_equivalent(f1, f2))
      {
	std::cerr << "Source and destination formulae are not equivalent!"
		  << std::endl;
	exit_code = 1;
      }

    if (exit_code)
      {
	spot::ltl::dump(std::cerr, f1) << std::endl;
	spot::ltl::dump(std::cerr, f2) << std::endl;
      }

    f1->destroy();
    f2->destroy();
  }
  spot::ltl::atomic_prop::dump_instances(std::cerr);
  spot::ltl::unop::dump_instances(std::cerr);
  spot::ltl::binop::dump_instances(std::cerr);
  spot::ltl::multop::dump_instances(std::cerr);
  spot::ltl::automatop::dump_instances(std::cerr);
  assert(spot::ltl::atomic_prop::instance_count() == 0);
  assert(spot::ltl::unop::instance_count() == 0);
  assert(spot::ltl::binop::instance_count() == 0);
  assert(spot::ltl::multop::instance_count() == 0);
  assert(spot::ltl::automatop::instance_count() == 0);
  return exit_code;
}
