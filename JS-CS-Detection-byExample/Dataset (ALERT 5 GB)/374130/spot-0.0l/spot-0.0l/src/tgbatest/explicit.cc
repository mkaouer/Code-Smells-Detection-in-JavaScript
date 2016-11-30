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
