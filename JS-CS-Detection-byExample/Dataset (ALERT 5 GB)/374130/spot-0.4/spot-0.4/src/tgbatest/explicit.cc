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
#include "ltlenv/defaultenv.hh"
#include "tgba/tgbaexplicit.hh"
#include "tgbaalgos/dotty.hh"
#include "ltlast/allnodes.hh"

int
main()
{
  spot::bdd_dict* dict = new spot::bdd_dict();

  spot::ltl::default_environment& e =
    spot::ltl::default_environment::instance();
  spot::tgba_explicit* a = new spot::tgba_explicit(dict);

  typedef spot::tgba_explicit::transition trans;

  trans* t1 = a->create_transition("state 0", "state 1");
  trans* t2 = a->create_transition("state 1", "state 2");
  trans* t3 = a->create_transition("state 2", "state 0");
  a->add_condition(t2, e.require("a"));
  a->add_condition(t3, e.require("b"));
  a->add_condition(t3, e.require("c"));
  a->declare_acceptance_condition(e.require("p"));
  a->declare_acceptance_condition(e.require("q"));
  a->declare_acceptance_condition(e.require("r"));
  a->add_acceptance_condition(t1, e.require("p"));
  a->add_acceptance_condition(t1, e.require("q"));
  a->add_acceptance_condition(t2, e.require("r"));

  spot::dotty_reachable(std::cout, a);

  delete a;
  delete dict;
  assert(spot::ltl::atomic_prop::instance_count() == 0);
  assert(spot::ltl::unop::instance_count() == 0);
  assert(spot::ltl::binop::instance_count() == 0);
  assert(spot::ltl::multop::instance_count() == 0);
}
