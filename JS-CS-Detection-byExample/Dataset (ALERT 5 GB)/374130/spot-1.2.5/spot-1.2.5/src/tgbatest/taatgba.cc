// Copyright (C) 2009 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
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
#include "misc/hash.hh"
#include "ltlenv/defaultenv.hh"
#include "ltlast/allnodes.hh"
#include "tgbaalgos/dotty.hh"
#include "tgba/taatgba.hh"

int
main()
{
  spot::bdd_dict* dict = new spot::bdd_dict();

  spot::ltl::default_environment& e =
    spot::ltl::default_environment::instance();
  spot::taa_tgba_string* a = new spot::taa_tgba_string(dict);

  typedef spot::taa_tgba::transition trans;

  std::string ss1_values[] = { "state 2", "state 3" };
  std::vector<std::string> ss1_vector(ss1_values, ss1_values + 2);
  trans* t1 = a->create_transition("state 1", ss1_vector);
  trans* t2 = a->create_transition("state 2", "state 3");
  trans* t3 = a->create_transition("state 2", "state 4");

  a->add_condition(t1, e.require("a"));
  a->add_condition(t2, e.require("b"));
  a->add_condition(t3, e.require("c"));

  a->set_init_state("state 1");
  spot::dotty_reachable(std::cout, a);

  delete a;
  delete dict;
  assert(spot::ltl::atomic_prop::instance_count() == 0);
  assert(spot::ltl::unop::instance_count() == 0);
  assert(spot::ltl::binop::instance_count() == 0);
  assert(spot::ltl::multop::instance_count() == 0);
}
