// -*- coding: utf-8 -*-
// Copyright (C) 2013 Laboratoire de Recherche et Developement de
// l'Epita (LRDE).
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
#include "ltlvisit/relabel.hh"
#include "ltlast/allnodes.hh"
#include "ltlvisit/tostring.hh"

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
  const spot::ltl::formula* f1 = spot::ltl::parse(argv[1], p1);

  if (spot::ltl::format_parse_errors(std::cerr, argv[1], p1))
    return 2;

  spot::ltl::relabeling_map* m = new spot::ltl::relabeling_map;
  const spot::ltl::formula* f2 = spot::ltl::relabel_bse(f1, spot::ltl::Pnn, m);
  f1->destroy();
  spot::ltl::to_string(f2, std::cout) << "\n";


  typedef std::map<std::string, std::string> map_t;
  map_t sorted_map;
  for (spot::ltl::relabeling_map::const_iterator i = m->begin();
       i != m->end(); ++i)
    sorted_map[spot::ltl::to_string(i->first)] =
      spot::ltl::to_string(i->second);
  for (map_t::const_iterator i = sorted_map.begin();
       i != sorted_map.end(); ++i)
    std::cout << "  " << i->first << "   ->   "
	      << i->second << "\n";
  f2->destroy();
  delete m;

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
  return 0;
}
