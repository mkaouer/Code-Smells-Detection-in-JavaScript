// Copyright (C) 2003, 2004, 2006 Laboratoire d'Informatique de Paris 6
// (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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

#include "tgbaalgos/ltl2tgba_fm.hh"
#include "tgbaalgos/reductgba_sim.hh"
#include "tgba/tgbareduc.hh"

#include "ltlvisit/destroy.hh"
#include "ltlvisit/reduce.hh"
#include "ltlast/allnodes.hh"
#include "ltlparse/public.hh"
#include "tgbaalgos/ltl2tgba_lacim.hh"
#include "tgbaalgos/ltl2tgba_fm.hh"
#include "tgba/bddprint.hh"
#include "tgbaalgos/dotty.hh"
#include "tgbaalgos/lbtt.hh"
#include "tgba/tgbatba.hh"
#include "tgbaalgos/magic.hh"
#include "tgbaalgos/gtec/gtec.hh"
#include "tgbaalgos/gtec/ce.hh"
#include "tgbaparse/public.hh"
#include "tgbaalgos/dupexp.hh"
#include "tgbaalgos/neverclaim.hh"

#include "misc/escape.hh"

void
syntax(char* prog)
{
#ifdef REDUCCMP
  std::cerr << prog << " option file" << std::endl;
#else
  std::cerr << prog << " option formula" << std::endl;
#endif
  exit(2);
}

int
main(int argc, char** argv)
{
  if (argc < 3)
    syntax(argv[0]);

  int o = spot::ltl::Reduce_None;
  switch (atoi(argv[1]))
    {
    case 0:
      o = spot::Reduce_Scc;
      break;
    case 1:
      o = spot::Reduce_quotient_Dir_Sim;
      break;
    case 2:
      o = spot::Reduce_transition_Dir_Sim;
      break;
    case 3:
      o = spot::Reduce_quotient_Del_Sim;
      break;
    case 4:
      o = spot::Reduce_transition_Del_Sim;
      break;
    case 5:
      o = spot::Reduce_quotient_Dir_Sim |
	spot::Reduce_transition_Dir_Sim |
	spot::Reduce_Scc;
      break;
    case 6:
      o = spot::Reduce_quotient_Del_Sim |
	spot::Reduce_transition_Del_Sim |
	spot::Reduce_Scc;
      break;
    case 7:
      // No Reduction
      break;
    default:
      return 2;
  }

  int exit_code = 0;
  spot::direct_simulation_relation* rel_dir = 0;
  spot::delayed_simulation_relation* rel_del = 0;
  spot::tgba* automata = 0;
  spot::tgba_reduc* automatareduc = 0;

  spot::ltl::environment& env(spot::ltl::default_environment::instance());
  spot::bdd_dict* dict = new spot::bdd_dict();

#ifdef REDUCCMP
  spot::tgba_parse_error_list pel;
  automata = spot::tgba_parse(argv[2], pel, dict, env, env, false);
  if (spot::format_tgba_parse_errors(std::cerr, argv[2], pel))
    return 2;
#else
  spot::ltl::parse_error_list p1;
  spot::ltl::formula* f = spot::ltl::parse(argv[2], p1, env);
  if (spot::ltl::format_parse_errors(std::cerr, argv[2], p1))
    return 2;
  automata = spot::ltl_to_tgba_fm(f, dict,
				  false, true,
				  false, true);
#endif

  spot::dotty_reachable(std::cout, automata);
  automatareduc = new spot::tgba_reduc(automata);

  if (o & spot::Reduce_quotient_Dir_Sim)
    {
      rel_dir = spot::get_direct_relation_simulation(automatareduc, std::cout);
      automatareduc->quotient_state(rel_dir);
    }
  else if (o & spot::Reduce_quotient_Del_Sim)
    {
      std::cout << "get delayed" << std::endl;
      rel_del = spot::get_delayed_relation_simulation(automatareduc, std::cout);
      std::cout << "quotient state" << std::endl;
      automatareduc->quotient_state(rel_del);
      std::cout << "end" << std::endl;
    }

  if (rel_dir != 0)
    {
      automatareduc->display_rel_sim(rel_dir, std::cout);
      spot::free_relation_simulation(rel_dir);
    }

  if (rel_del != 0)
    {
      automatareduc->display_rel_sim(rel_del, std::cout);
      spot::free_relation_simulation(rel_del);
    }

  if (o & spot::Reduce_Scc)
    {
      automatareduc->prune_scc();
      //automatareduc->display_scc(std::cout);
    }

  if (automatareduc != 0)
    {
      spot::dotty_reachable(std::cout, automatareduc);
    }

  if (automata != 0)
    delete automata;
  if (automatareduc != 0)
    delete automatareduc;
#ifndef REDUCCMP
  if (f != 0)
    spot::ltl::destroy(f);
#endif

  assert(spot::ltl::atomic_prop::instance_count() == 0);
  assert(spot::ltl::unop::instance_count() == 0);
  assert(spot::ltl::binop::instance_count() == 0);
  assert(spot::ltl::multop::instance_count() == 0);

  if (dict != 0)
    delete dict;

  return exit_code;
}
