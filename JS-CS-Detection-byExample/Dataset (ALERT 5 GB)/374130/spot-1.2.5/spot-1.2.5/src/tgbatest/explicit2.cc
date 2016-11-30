// Copyright (C) 2012 Laboratoire de Recherche et Développement
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
#include "ltlast/allnodes.hh"
#include "ltlvisit/tostring.hh"

#include "tgba/tgbaexplicit.hh"

using namespace spot;

void
create_tgba_explicit_string(bdd_dict* d)
{
  tgba_explicit<state_explicit_string>* tgba =
    new tgba_explicit<state_explicit_string>(d);
  state_explicit_string* s1 = tgba->add_state("toto");
  state_explicit_string* s2 = tgba->add_state("tata");
  state_explicit_string::transition* t =
   tgba->create_transition(s1, s2);
  (void) t;

  tgba_explicit_succ_iterator<state_explicit_string>* it
    = tgba->succ_iter(tgba->get_init_state());
  for (it->first(); !it->done(); it->next())
    {
      state_explicit_string* se = it->current_state();
      std::cout << se->label() << std::endl;
      se->destroy();
    }
  delete it;

  delete tgba;
}

void
create_tgba_explicit_number(bdd_dict* d)
{
  tgba_explicit<state_explicit_number>* tgba =
    new tgba_explicit<state_explicit_number>(d);

  state_explicit_number* s1 = tgba->add_state(51);
  state_explicit_number* s2 = tgba->add_state(69);
  state_explicit_number::transition* t =
    tgba->create_transition(s1, s2);
  (void) t;

  tgba_explicit_succ_iterator<state_explicit_number>* it =
    tgba->succ_iter(tgba->get_init_state());
  for (it->first(); !it->done(); it->next())
    {
      state_explicit_number* s = it->current_state();
      std::cout << s->label() << std::endl;
      s->destroy();
    }

  delete it;
  delete tgba;
}

void
create_tgba_explicit_formula(bdd_dict* d, spot::ltl::default_environment& e)
{
  tgba_explicit<state_explicit_formula>* tgba =
    new tgba_explicit<state_explicit_formula>(d);

  state_explicit_formula* s1 = tgba->add_state(e.require("a"));
  state_explicit_formula* s2 = tgba->add_state(e.require("b"));

  state_explicit_formula::transition* t =
    tgba->create_transition(s1, s2);
  (void) t;

  tgba_explicit_succ_iterator<state_explicit_formula>* it =
    tgba->succ_iter(tgba->get_init_state());
  for (it->first(); !it->done(); it->next())
    {
      state_explicit_formula* s = it->current_state();
      to_string(s->label(), std::cout) << std::endl;
      s->destroy();
    }

  delete it;
  delete tgba;
}

void create_sba_explicit_string(bdd_dict* d)
{
  sba_explicit<state_explicit_string>* sba =
    new sba_explicit<state_explicit_string>(d);

  ltl::formula* acc = ltl::constant::true_instance();
  sba->declare_acceptance_condition(acc);

  state_explicit_string* s1 = sba->add_state("STATE1");
  state_explicit_string* s2 = sba->add_state("STATE2");
  state_explicit_string* s3 = sba->add_state("STATE3");

  state_explicit_string::transition* t =
    sba->create_transition(s1, s2);
  sba->add_acceptance_condition(t, acc);

  t = sba->create_transition(s1, s3);
  sba->add_acceptance_condition(t, acc);

  std::cout << "S1 ACCEPTING? " << sba->state_is_accepting(s1) << std::endl;
  std::cout << "S2 ACCEPTING? " << sba->state_is_accepting(s2) << std::endl;
  std::cout << "S3 ACCEPTING? " << sba->state_is_accepting(s3) << std::endl;

  delete sba;
}

void create_sba_explicit_number(bdd_dict* d)
{
  sba_explicit<state_explicit_number>* sba =
    new sba_explicit<state_explicit_number>(d);

  ltl::formula* acc = ltl::constant::true_instance();
  sba->declare_acceptance_condition(acc);

  state_explicit_number* s1 = sba->add_state(1);
  state_explicit_number* s2 = sba->add_state(2);

  state_explicit_number::transition* t = sba->create_transition(s1, s2);
  sba->add_acceptance_condition(t, acc);

  std::cout << "S1 ACCEPTING? " << sba->state_is_accepting(s1) << std::endl;
  std::cout << "S2 ACCEPTING? " << sba->state_is_accepting(s2) << std::endl;

  delete sba;
}

void
create_sba_explicit_formula(bdd_dict* d, spot::ltl::default_environment& e)
{
  sba_explicit<state_explicit_formula>* sba =
    new sba_explicit<state_explicit_formula>(d);

  ltl::formula* acc = ltl::constant::true_instance();
  sba->declare_acceptance_condition(acc);

  state_explicit_formula* s1 = sba->add_state(e.require("a"));
  state_explicit_formula* s2 = sba->add_state(e.require("b"));
  state_explicit_formula* s3 = sba->add_state(e.require("c"));

  state_explicit_formula::transition* t = sba->create_transition(s1, s2);
  sba->add_acceptance_condition(t, acc);

  t = sba->create_transition(s1, s3);
  sba->add_acceptance_condition(t, acc);

  std::cout << "S1 ACCEPTING? " << sba->state_is_accepting(s1) << std::endl;
  std::cout << "S2 ACCEPTING? " << sba->state_is_accepting(s2) << std::endl;
  std::cout << "S3 ACCEPTING? " << sba->state_is_accepting(s3) << std::endl;

  delete sba;
}

int
main(int argc, char** argv)
{
  (void) argc;
  (void) argv;

  bdd_dict* d = new spot::bdd_dict();
  spot::ltl::default_environment& e =
    spot::ltl::default_environment::instance();

  //check tgba creation
  std::cout << "* TGBA explicit string" << std::endl;
  create_tgba_explicit_string(d);
  std::cout << "* TGBA explicit number" << std::endl;
  create_tgba_explicit_number(d);
  std::cout << "* TGBA explicit formula" << std::endl;
  create_tgba_explicit_formula(d, e);

  //check sba creation
  std::cout << "* SBA explicit string, 1 accepting state" << std::endl;
  create_sba_explicit_string(d);
  std::cout << "* SBA explicit number, 1 accepting state" << std::endl;
  create_sba_explicit_number(d);
  std::cout << "* SBA explicit formula, 1 accepting state" << std::endl;
  create_sba_explicit_formula(d, e);

  delete d;
  assert(spot::ltl::atomic_prop::instance_count() == 0);
  assert(spot::ltl::unop::instance_count() == 0);
  assert(spot::ltl::binop::instance_count() == 0);
  assert(spot::ltl::multop::instance_count() == 0);
}
