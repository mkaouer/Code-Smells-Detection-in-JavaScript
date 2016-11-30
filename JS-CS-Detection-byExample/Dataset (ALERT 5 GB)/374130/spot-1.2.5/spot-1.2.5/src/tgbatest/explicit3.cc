// Copyright (C) 2013 Laboratoire de Recherche et Développement
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
#include "ltlenv/defaultenv.hh"
#include "tgba/tgbaexplicit.hh"
#include "tgbaalgos/dotty.hh"
#include "ltlast/allnodes.hh"
#include "tgba/tgbamask.hh"

int
main()
{
  spot::ltl::default_environment& e =
    spot::ltl::default_environment::instance();

  const spot::ltl::formula* a = e.require("a");
  const spot::ltl::formula* b = e.require("b");
  const spot::ltl::formula* c = e.require("c");

  spot::bdd_dict* dict = new spot::bdd_dict();
  spot::tgba_explicit_number* aut = new spot::tgba_explicit_number(dict);

  typedef spot::state_explicit_number::transition trans;

  {
    trans* t = aut->create_transition(0, 1);
    aut->add_condition(t, a->clone());
  }
  {
    trans* t = aut->create_transition(1, 2);
    aut->add_condition(t, a->clone());
  }
  {
    trans* t = aut->create_transition(2, 0);
    aut->add_condition(t, a->clone());
  }
  {
    trans* t = aut->create_transition(1, 3);
    aut->add_condition(t, b->clone());
  }
  {
    trans* t = aut->create_transition(3, 4);
    aut->add_condition(t, c->clone());
  }
  {
    trans* t = aut->create_transition(4, 3);
    aut->add_condition(t, c->clone());
    aut->declare_acceptance_condition(b->clone());
    aut->add_acceptance_condition(t, b->clone());
  }

  a->destroy();
  b->destroy();
  c->destroy();

  spot::dotty_reachable(std::cout, aut);

  spot::state_set s;
  s.insert(aut->get_state(0));
  s.insert(aut->get_state(1));
  s.insert(aut->get_state(2));

  const spot::tgba* mk = build_tgba_mask_keep(aut, s);
  spot::dotty_reachable(std::cout, mk);
  delete mk;

  const spot::tgba* mi = build_tgba_mask_ignore(aut, s);
  spot::dotty_reachable(std::cout, mi);
  delete mi;

  const spot::tgba* mi2 = build_tgba_mask_ignore(aut, s, aut->get_state(1));
  spot::dotty_reachable(std::cout, mi2);
  delete mi2;

  delete aut;
  delete dict;
  assert(spot::ltl::atomic_prop::instance_count() == 0);
  assert(spot::ltl::unop::instance_count() == 0);
  assert(spot::ltl::binop::instance_count() == 0);
  assert(spot::ltl::multop::instance_count() == 0);
}
