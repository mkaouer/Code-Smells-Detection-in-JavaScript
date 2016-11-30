// Copyright (C) 2003, 2004, 2005, 2006, 2007 Laboratoire d'Informatique de
// Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
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

#include <iostream>
#include <iomanip>
#include <cassert>
#include <fstream>
#include <string>
#include "ltlvisit/destroy.hh"
#include "ltlvisit/contain.hh"
#include "ltlvisit/tostring.hh"
#include "ltlvisit/apcollect.hh"
#include "ltlast/allnodes.hh"
#include "ltlparse/public.hh"
#include "tgbaalgos/ltl2tgba_lacim.hh"
#include "tgbaalgos/ltl2tgba_fm.hh"
#include "tgba/bddprint.hh"
#include "tgbaalgos/save.hh"
#include "tgbaalgos/dotty.hh"
#include "tgbaalgos/lbtt.hh"
#include "tgba/tgbatba.hh"
#include "tgba/tgbaproduct.hh"
#include "tgbaalgos/reducerun.hh"
#include "tgbaparse/public.hh"
#include "tgbaalgos/dupexp.hh"
#include "tgbaalgos/neverclaim.hh"
#include "tgbaalgos/reductgba_sim.hh"
#include "tgbaalgos/replayrun.hh"
#include "tgbaalgos/rundotdec.hh"

#include "tgbaalgos/stats.hh"
#include "tgbaalgos/emptiness_stats.hh"

void
syntax(char* prog)
{
  std::cerr << "Usage: "<< prog << " [OPTIONS...] formula" << std::endl
            << "       "<< prog << " -F [OPTIONS...] file" << std::endl
            << "       "<< prog << " -X [OPTIONS...] file" << std::endl
	    << std::endl
	    << "Options:" << std::endl
	    << "  -0    produce minimal output dedicated to the paper"
	    << std::endl
	    << "  -a    display the acceptance_conditions BDD, not the "
	    << "reachability graph"
	    << std::endl
	    << "  -A    same as -a, but as a set" << std::endl
	    << "  -b    display the automaton in the format of spot"
            << std::endl
	    << "  -c    enable language containment checks (implies -f)"
	    << std::endl
	    << "  -d    turn on traces during parsing" << std::endl
	    << "  -D    degeneralize the automaton as a TBA" << std::endl
	    << "  -DS   degeneralize the automaton as an SBA" << std::endl
	    << "  -e[ALGO]  emptiness-check, expect and compute an "
	    << "accepting run" << std::endl
	    << "  -E[ALGO]  emptiness-check, expect no accepting run"
	    << std::endl
            << "  -f    use Couvreur's FM algorithm for translation"
	    << std::endl
	    << "  -fr1  use -r1 (see below) at each step of FM" << std::endl
	    << "  -fr2  use -r2 (see below) at each step of FM" << std::endl
	    << "  -fr3  use -r3 (see below) at each step of FM" << std::endl
	    << "  -fr4  use -r4 (see below) at each step of FM" << std::endl
	    << "  -fr5  use -r5 (see below) at each step of FM" << std::endl
	    << "  -fr6  use -r6 (see below) at each step of FM" << std::endl
	    << "  -fr7  use -r7 (see below) at each step of FM" << std::endl
	    << "  -fr8  use -r8 (see below) at each step of FM" << std::endl
            << "  -F    read the formula from the file" << std::endl
	    << "  -g    graph the accepting run on the automaton (requires -e)"
	    << std::endl
	    << "  -G    graph the accepting run seen as an automaton "
	    << " (requires -e)" << std::endl
            << "  -L    fair-loop approximation (implies -f)" << std::endl
	    << "  -m    try to reduce accepting runs, in a second pass"
	    << std::endl
	    << "  -N    display the never clain for Spin "
	    << "(implies -D)" << std::endl
            << "  -p    branching postponement (implies -f)" << std::endl
	    << "  -Pfile  multiply the formula with the automaton from `file'."
	    << std::endl
	    << "  -r    display the relation BDD, not the reachability graph"
	    << std::endl
	    << "  -r1   reduce formula using basic rewriting" << std::endl
	    << "  -r2   reduce formula using class of eventuality and "
	    << "and universality" << std::endl
	    << "  -r3   reduce formula using implication between "
	    << "sub-formulae" << std::endl
	    << "  -r4   reduce formula using all above rules" << std::endl
	    << "  -r5   reduce formula using tau03" << std::endl
	    << "  -r6   reduce formula using tau03+" << std::endl
	    << "  -r7   reduce formula using tau03+ and -r1" << std::endl
	    << "  -r8   reduce formula using tau03+ and -r4" << std::endl
	    << "  -rd   display the reduce formula" << std::endl
	    << "  -R    same as -r, but as a set" << std::endl
	    << "  -R1q  merge states using direct simulation "
	    << "(use -L for more reduction)"
	    << std::endl
	    << "  -R1t  remove transitions using direct simulation "
	    << "(use -L for more reduction)"
	    << std::endl
	    << "  -R2q  merge states using delayed simulation" << std::endl
	    << "  -R2t  remove transitions using delayed simulation"
	    << std::endl
	    << "  -R3   use SCC to reduce the automata" << std::endl
	    << "  -Rd   display the simulation relation" << std::endl
	    << "  -RD   display the parity game (dot format)" << std::endl
	    << "  -s    convert to explicit automata, and number states "
	    << "in DFS order" << std::endl
	    << "  -S    convert to explicit automata, and number states "
	    << "in BFS order" << std::endl
	    << "  -t    display reachable states in LBTT's format" << std::endl
            << "  -U[PROPS]  consider atomic properties PROPS as exclusive "
	    << "events (implies -f)" << std::endl
	    << "  -v    display the BDD variables used by the automaton"
	    << std::endl
            << "  -x    try to produce a more deterministic automata "
	    << "(implies -f)" << std::endl
	    << "  -X    do not compute an automaton, read it from a file"
	    << std::endl
	    << "  -y    do not merge states with same symbolic representation "
	    << "(implies -f)" << std::endl
	    << std::endl
	    << "Where ALGO should be one of:" << std::endl
	    << "  Cou99(OPTIONS) (the default)" << std::endl
	    << "  CVWY90(OPTIONS)" << std::endl
	    << "  GV04(OPTIONS)" << std::endl
	    << "  SE05(OPTIONS)" << std::endl
	    << "  Tau03(OPTIONS)" << std::endl
	    << "  Tau03_opt(OPTIONS)" << std::endl;
  exit(2);
}

int
main(int argc, char** argv)
{
  int exit_code = 0;

  bool debug_opt = false;
  bool paper_opt = false;
  enum { NoDegen, DegenTBA, DegenSBA } degeneralize_opt = NoDegen;
  bool fm_opt = false;
  int fm_red = spot::ltl::Reduce_None;
  bool fm_exprop_opt = false;
  bool fm_symb_merge_opt = true;
  bool file_opt = false;
  int output = 0;
  int formula_index = 0;
  const char* echeck_algo = 0;
  spot::emptiness_check_instantiator* echeck_inst = 0;
  enum { NoneDup, BFS, DFS } dupexp = NoneDup;
  bool expect_counter_example = false;
  bool from_file = false;
  int reduc_aut = spot::Reduce_None;
  int redopt = spot::ltl::Reduce_None;
  bool display_reduce_form = false;
  bool display_rel_sim = false;
  bool display_parity_game = false;
  bool post_branching = false;
  bool fair_loop_approx = false;
  bool graph_run_opt = false;
  bool graph_run_tgba_opt = false;
  bool opt_reduce = false;
  bool containment = false;
  spot::ltl::environment& env(spot::ltl::default_environment::instance());
  spot::ltl::atomic_prop_set* unobservables = 0;
  spot::tgba_explicit* system = 0;
  spot::tgba* product = 0;
  spot::tgba* product_to_free = 0;
  spot::bdd_dict* dict = new spot::bdd_dict();

  for (;;)
    {
      if (argc < formula_index + 2)
	syntax(argv[0]);

      ++formula_index;

      if (!strcmp(argv[formula_index], "-0"))
	{
	  paper_opt = true;
	}
      else if (!strcmp(argv[formula_index], "-a"))
	{
	  output = 2;
	}
      else if (!strcmp(argv[formula_index], "-A"))
	{
	  output = 4;
	}
      else if (!strcmp(argv[formula_index], "-b"))
	{
	  output = 7;
	}
      else if (!strcmp(argv[formula_index], "-c"))
	{
	  containment = true;
	  fm_opt = true;
	}
      else if (!strcmp(argv[formula_index], "-d"))
	{
	  debug_opt = true;
	}
      else if (!strcmp(argv[formula_index], "-D"))
	{
	  degeneralize_opt = DegenTBA;
	}
      else if (!strcmp(argv[formula_index], "-DS"))
	{
	  degeneralize_opt = DegenSBA;
	}
      else if (!strncmp(argv[formula_index], "-e", 2))
        {
	  echeck_algo = 2 + argv[formula_index];
	  if (!*echeck_algo)
	    echeck_algo = "Cou99";

	  const char* err;
	  echeck_inst =
	    spot::emptiness_check_instantiator::construct(echeck_algo, &err);
	  if (!echeck_inst)
	    {
	      std::cerr << "Failed to parse argument of -e near `"
			<< err <<  "'" << std::endl;
	      exit(2);
	    }
          expect_counter_example = true;
          output = -1;
        }
      else if (!strncmp(argv[formula_index], "-E", 2))
        {
	  const char* echeck_algo = 2 + argv[formula_index];
	  if (!*echeck_algo)
	    echeck_algo = "Cou99";

	  const char* err;
	  echeck_inst =
	    spot::emptiness_check_instantiator::construct(echeck_algo, &err);
	  if (!echeck_inst)
	    {
	      std::cerr << "Failed to parse argument of -e near `"
			<< err <<  "'" << std::endl;
	      exit(2);
	    }
          expect_counter_example = false;
          output = -1;
        }
      else if (!strcmp(argv[formula_index], "-f"))
	{
	  fm_opt = true;
	}
      else if (!strcmp(argv[formula_index], "-fr1"))
	{
	  fm_opt = true;
	  fm_red |= spot::ltl::Reduce_Basics;
	}
      else if (!strcmp(argv[formula_index], "-fr2"))
	{
	  fm_opt = true;
	  fm_red |= spot::ltl::Reduce_Eventuality_And_Universality;
	}
      else if (!strcmp(argv[formula_index], "-fr3"))
	{
	  fm_opt = true;
	  fm_red |= spot::ltl::Reduce_Syntactic_Implications;
	}
      else if (!strcmp(argv[formula_index], "-fr4"))
	{
	  fm_opt = true;
 	  fm_red |= spot::ltl::Reduce_Basics
	    | spot::ltl::Reduce_Eventuality_And_Universality
	    | spot::ltl::Reduce_Syntactic_Implications;
	}
      else if (!strcmp(argv[formula_index], "-fr5"))
	{
	  fm_opt = true;
	  fm_red |= spot::ltl::Reduce_Containment_Checks;
	}
      else if (!strcmp(argv[formula_index], "-fr6"))
	{
	  fm_opt = true;
	  fm_red |= spot::ltl::Reduce_Containment_Checks_Stronger;
	}
      else if (!strcmp(argv[formula_index], "-fr7"))
	{
	  fm_opt = true;
	  fm_red |= spot::ltl::Reduce_All;
	}
      else if (!strcmp(argv[formula_index], "-F"))
	{
	  file_opt = true;
	}
      else if (!strcmp(argv[formula_index], "-g"))
	{
	  graph_run_opt = true;
	}
      else if (!strcmp(argv[formula_index], "-G"))
	{
	  graph_run_tgba_opt = true;
	}
      else if (!strcmp(argv[formula_index], "-L"))
	{
	  fair_loop_approx = true;
	  fm_opt = true;
	}
      else if (!strcmp(argv[formula_index], "-m"))
	{
	  opt_reduce = true;
	}
      else if (!strcmp(argv[formula_index], "-N"))
	{
	  degeneralize_opt = DegenSBA;
	  output = 8;
	}
      else if (!strcmp(argv[formula_index], "-p"))
	{
	  post_branching = true;
	  fm_opt = true;
	}
      else if (!strncmp(argv[formula_index], "-P", 2))
	{
	  spot::tgba_parse_error_list pel;
	  system = spot::tgba_parse(argv[formula_index] + 2,
				    pel, dict, env, env, debug_opt);
	  if (spot::format_tgba_parse_errors(std::cerr,
					     argv[formula_index] + 2, pel))
	    return 2;
	  system->merge_transitions();

	  if (!paper_opt)
	    std::clog << argv[formula_index] + 2 << " read" << std::endl;
	}
      else if (!strcmp(argv[formula_index], "-r"))
	{
	  output = 1;
	}
      else if (!strcmp(argv[formula_index], "-r1"))
	{
	  redopt |= spot::ltl::Reduce_Basics;
	}
      else if (!strcmp(argv[formula_index], "-r2"))
	{
	  redopt |= spot::ltl::Reduce_Eventuality_And_Universality;
	}
      else if (!strcmp(argv[formula_index], "-r3"))
	{
	  redopt |= spot::ltl::Reduce_Syntactic_Implications;
	}
      else if (!strcmp(argv[formula_index], "-r4"))
	{
 	  redopt |= spot::ltl::Reduce_Basics
	    | spot::ltl::Reduce_Eventuality_And_Universality
	    | spot::ltl::Reduce_Syntactic_Implications;
	}
      else if (!strcmp(argv[formula_index], "-r5"))
	{
	  redopt |= spot::ltl::Reduce_Containment_Checks;
	}
      else if (!strcmp(argv[formula_index], "-r6"))
	{
	  redopt |= spot::ltl::Reduce_Containment_Checks_Stronger;
	}
      else if (!strcmp(argv[formula_index], "-r7"))
	{
	  redopt |= spot::ltl::Reduce_All;
	}
      else if (!strcmp(argv[formula_index], "-R"))
	{
	  output = 3;
	}
      else if (!strcmp(argv[formula_index], "-R1q"))
	{
	  reduc_aut |= spot::Reduce_quotient_Dir_Sim;
	}
      else if (!strcmp(argv[formula_index], "-R1t"))
	{
	  reduc_aut |= spot::Reduce_transition_Dir_Sim;
	}
      else if (!strcmp(argv[formula_index], "-R2q"))
	{
	  reduc_aut |= spot::Reduce_quotient_Del_Sim;
	}
      else if (!strcmp(argv[formula_index], "-R2t"))
	{
	  reduc_aut |= spot::Reduce_transition_Del_Sim;
	}
      else if (!strcmp(argv[formula_index], "-R3"))
	{
	  reduc_aut |= spot::Reduce_Scc;
	}
      else if (!strcmp(argv[formula_index], "-rd"))
	{
	  display_reduce_form = true;
	}
      else if (!strcmp(argv[formula_index], "-Rd"))
	{
	  display_rel_sim = true;
	}
      else if (!strcmp(argv[formula_index], "-RD"))
	{
	  display_parity_game = true;
	}
      else if (!strcmp(argv[formula_index], "-s"))
	{
	  dupexp = DFS;
	}
      else if (!strcmp(argv[formula_index], "-S"))
	{
	  dupexp = BFS;
	}
      else if (!strcmp(argv[formula_index], "-t"))
	{
	  output = 6;
	}
      else if (!strncmp(argv[formula_index], "-U", 2))
	{
	  unobservables = new spot::ltl::atomic_prop_set;
	  fm_opt = true;
	  // Parse -U's argument.
	  const char* tok = strtok(argv[formula_index] + 2, ", \t;");
	  while (tok)
	    {
	      unobservables->insert
		(static_cast<spot::ltl::atomic_prop*>(env.require(tok)));
	      tok = strtok(0, ", \t;");
	    }
	}
      else if (!strcmp(argv[formula_index], "-v"))
	{
	  output = 5;
	}
      else if (!strcmp(argv[formula_index], "-x"))
	{
	  fm_opt = true;
	  fm_exprop_opt = true;
	}
      else if (!strcmp(argv[formula_index], "-X"))
	{
	  from_file = true;
	}
      else if (!strcmp(argv[formula_index], "-y"))
	{
	  fm_opt = true;
	  fm_symb_merge_opt = false;
	}
      else
	{
	  break;
	}
    }

  if ((graph_run_opt || graph_run_tgba_opt)
      && (!echeck_inst || !expect_counter_example))
    {
      std::cerr << argv[0] << ": error: -g and -G require -e." << std::endl;
      exit(1);
    }

  std::string input;

  if (file_opt)
    {
      if (strcmp(argv[formula_index], "-"))
	{
	  std::ifstream fin(argv[formula_index]);
	  if (!fin)
	    {
	      std::cerr << "Cannot open " << argv[formula_index] << std::endl;
	      exit(2);
	    }

	  if (!std::getline(fin, input, '\0'))
	    {
	      std::cerr << "Cannot read " << argv[formula_index] << std::endl;
	      exit(2);
	    }
	}
      else
	{
	  std::getline(std::cin, input, '\0');
	}
    }
  else
    {
      input = argv[formula_index];
    }

  spot::ltl::formula* f = 0;
  if (!from_file)
    {
      spot::ltl::parse_error_list pel;
      f = spot::ltl::parse(input, pel, env, debug_opt);
      exit_code = spot::ltl::format_parse_errors(std::cerr, input, pel);
    }
  if (f || from_file)
    {
      spot::tgba_bdd_concrete* concrete = 0;
      spot::tgba* to_free = 0;
      spot::tgba* a = 0;

      if (from_file)
	{
	  spot::tgba_parse_error_list pel;
	  spot::tgba_explicit* e;
	  to_free = a = e = spot::tgba_parse(input, pel, dict,
					     env, env, debug_opt);
	  if (spot::format_tgba_parse_errors(std::cerr, input, pel))
	    {
	      delete to_free;
	      delete dict;
	      return 2;
	    }
	  e->merge_transitions();
	}
      else
	{
	  if (redopt != spot::ltl::Reduce_None)
	    {
	      spot::ltl::formula* t = spot::ltl::reduce(f, redopt);
	      spot::ltl::destroy(f);
	      f = t;
	      if (display_reduce_form)
		std::cout << spot::ltl::to_string(f) << std::endl;
	    }

	  if (fm_opt)
	    to_free = a = spot::ltl_to_tgba_fm(f, dict, fm_exprop_opt,
					       fm_symb_merge_opt,
					       post_branching,
					       fair_loop_approx, unobservables,
					       fm_red, containment);
	  else
	    to_free = a = concrete = spot::ltl_to_tgba_lacim(f, dict);
	}

      spot::tgba_tba_proxy* degeneralized = 0;

      unsigned int n_acc = a->number_of_acceptance_conditions();
      if (echeck_inst
	  && degeneralize_opt == NoDegen
	  && n_acc > 1
	  && echeck_inst->max_acceptance_conditions() < n_acc)
	degeneralize_opt = DegenTBA;
      if (degeneralize_opt == DegenTBA)
	a = degeneralized = new spot::tgba_tba_proxy(a);
      else if (degeneralize_opt == DegenSBA)
	a = degeneralized = new spot::tgba_sba_proxy(a);

      spot::tgba_reduc* aut_red = 0;
      if (reduc_aut != spot::Reduce_None)
	{
	  a = aut_red = new spot::tgba_reduc(a);

	  if (reduc_aut & spot::Reduce_Scc)
	    aut_red->prune_scc();

	  if (reduc_aut & (spot::Reduce_quotient_Dir_Sim |
			   spot::Reduce_transition_Dir_Sim |
			   spot::Reduce_quotient_Del_Sim |
			   spot::Reduce_transition_Del_Sim))
	    {
	      spot::direct_simulation_relation* rel_dir = 0;
	      spot::delayed_simulation_relation* rel_del = 0;

	      if (reduc_aut & (spot::Reduce_quotient_Dir_Sim |
			       spot::Reduce_transition_Dir_Sim))
		{
		  rel_dir =
		    spot::get_direct_relation_simulation(a,
							 std::cout,
							 display_parity_game);
		  assert(rel_dir);
		}
	      if (reduc_aut & (spot::Reduce_quotient_Del_Sim |
				    spot::Reduce_transition_Del_Sim))
		{
		  rel_del =
		    spot::get_delayed_relation_simulation(a,
							  std::cout,
							  display_parity_game);
		  assert(rel_del);
		}

	      if (display_rel_sim)
		{
		  if (rel_dir)
		    aut_red->display_rel_sim(rel_dir, std::cout);
		  if (rel_del)
		    aut_red->display_rel_sim(rel_del, std::cout);
		}

	      if (reduc_aut & spot::Reduce_quotient_Dir_Sim)
		aut_red->quotient_state(rel_dir);
	      if (reduc_aut & spot::Reduce_transition_Dir_Sim)
		aut_red->delete_transitions(rel_dir);
	      if (reduc_aut & spot::Reduce_quotient_Del_Sim)
		aut_red->quotient_state(rel_del);
	      if (reduc_aut & spot::Reduce_transition_Del_Sim)
		aut_red->delete_transitions(rel_del);

	      if (rel_dir)
		spot::free_relation_simulation(rel_dir);
	      if (rel_del)
		spot::free_relation_simulation(rel_del);
	    }
	}

      spot::tgba_explicit* expl = 0;
      switch (dupexp)
	{
	case NoneDup:
	  break;
	case BFS:
	  a = expl = tgba_dupexp_bfs(a);
	  break;
	case DFS:
	  a = expl = tgba_dupexp_dfs(a);
	  break;
	}

      spot::tgba* product_degeneralized = 0;

      if (system)
        {
          a = product = product_to_free = new spot::tgba_product(system, a);

	  unsigned int n_acc = a->number_of_acceptance_conditions();
	  if (echeck_inst
	      && degeneralize_opt == NoDegen
	      && n_acc > 1
	      && echeck_inst->max_acceptance_conditions() < n_acc)
            degeneralize_opt = DegenTBA;
          if (degeneralize_opt == DegenTBA)
            a = product = product_degeneralized =
                                            new spot::tgba_tba_proxy(product);
          else if (degeneralize_opt == DegenSBA)
            a = product = product_degeneralized =
                                            new spot::tgba_sba_proxy(product);
        }

      if (echeck_inst
	  && (a->number_of_acceptance_conditions()
	      < echeck_inst->min_acceptance_conditions()))
	{
	  if (!paper_opt)
	    {
	      std::cerr << echeck_algo << " requires at least "
			<< echeck_inst->min_acceptance_conditions()
			<< " acceptance conditions." << std::endl;
	      exit(1);
	    }
	  else
	    {
	      std::cout << std::endl;
	      exit(0);
	    }
	}

      switch (output)
	{
	case -1:
	  /* No output.  */
	  break;
	case 0:
	  spot::dotty_reachable(std::cout, a);
	  break;
	case 1:
	  if (concrete)
	    spot::bdd_print_dot(std::cout, concrete->get_dict(),
				concrete->get_core_data().relation);
	  break;
	case 2:
	  if (concrete)
	    spot::bdd_print_dot(std::cout, concrete->get_dict(),
				concrete->
				get_core_data().acceptance_conditions);
	  break;
	case 3:
	  if (concrete)
	    spot::bdd_print_set(std::cout, concrete->get_dict(),
				concrete->get_core_data().relation);
	  break;
	case 4:
	  if (concrete)
	    spot::bdd_print_set(std::cout, concrete->get_dict(),
				concrete->
				get_core_data().acceptance_conditions);
	  break;
	case 5:
	  a->get_dict()->dump(std::cout);
	  break;
	case 6:
	  spot::lbtt_reachable(std::cout, a);
	  break;
	case 7:
	  spot::tgba_save_reachable(std::cout, a);
	  break;
	case 8:
	  {
	    assert(degeneralize_opt == DegenSBA);
	    const spot::tgba_sba_proxy* s =
	      static_cast<const spot::tgba_sba_proxy*>(degeneralized);
	    spot::never_claim_reachable(std::cout, s, f);
	    break;
	  }
	default:
	  assert(!"unknown output option");
	}

      if (echeck_inst)
	{
	  spot::emptiness_check* ec = echeck_inst->instantiate(a);
	  bool search_many = echeck_inst->options().get("repeated");
	  assert(ec);
	  do
	    {
	      spot::emptiness_check_result* res = ec->check();
              if (paper_opt)
                {
                  std::ios::fmtflags old = std::cout.flags();
                  std::cout << std::left << std::setw(25)
                            << echeck_algo << ", ";
                  spot::tgba_statistics a_size =
                                        spot::stats_reachable(ec->automaton());
                  std::cout << std::right << std::setw(10)
                            << a_size.states << ", "
                            << std::right << std::setw(10)
                            << a_size.transitions << ", ";
                  std::cout <<
                            ec->automaton()->number_of_acceptance_conditions()
                            << ", ";
                  const spot::ec_statistics* ecs =
                        dynamic_cast<const spot::ec_statistics*>(ec);
                  if (ecs)
                    std::cout << std::right << std::setw(10)
                              << ecs->states() << ", "
                              << std::right << std::setw(10)
                              << ecs->transitions() << ", "
                              << std::right << std::setw(10)
                              << ecs->max_depth();
                  else
                    std::cout << "no stats, , ";
                  if (res)
                    std::cout << ", accepting run found";
                  else
                    std::cout << ", no accepting run found";
                  std::cout << std::endl;
                  std::cout << std::setiosflags(old);
                }
              else
                {
                  if (!graph_run_opt && !graph_run_tgba_opt)
                    ec->print_stats(std::cout);
                  if (expect_counter_example != !!res &&
		      (!expect_counter_example || ec->safe()))
                    exit_code = 1;

                  if (!res)
                    {
                      std::cout << "no accepting run found";
                      if (!ec->safe() && expect_counter_example)
                        {
                          std::cout << " even if expected" << std::endl;
                          std::cout << "this may be due to the use of the bit"
                                    << " state hashing technique" << std::endl;
                          std::cout << "you can try to increase the heap size "
                                    << "or use an explicit storage"
                                    << std::endl;
                        }
                      std::cout << std::endl;
                      break;
                    }
                  else
                    {

                      spot::tgba_run* run = res->accepting_run();
                      if (!run)
                        {
                          std::cout << "an accepting run exists" << std::endl;
                        }
                      else
                        {
                          if (opt_reduce)
                            {
                              spot::tgba_run* redrun =
                                spot::reduce_run(res->automaton(), run);
                              delete run;
                              run = redrun;
                            }
                          if (graph_run_opt)
                            {
                              spot::tgba_run_dotty_decorator deco(run);
                              spot::dotty_reachable(std::cout, a, &deco);
                            }
                          else if (graph_run_tgba_opt)
                            {
                              spot::tgba* ar = spot::tgba_run_to_tgba(a, run);
                              spot::dotty_reachable(std::cout, ar);
                              delete ar;
                            }
                          else
                            {
                              spot::print_tgba_run(std::cout, a, run);
                              if (!spot::replay_tgba_run(std::cout, a, run,
                                                                        true))
                                exit_code = 1;
                            }
                          delete run;
                        }
                    }
                }
	      delete res;
	    }
	  while (search_many);
	  delete ec;
	}

      if (f)
        spot::ltl::destroy(f);
      delete product_degeneralized;
      delete product_to_free;
      delete system;
      delete expl;
      delete aut_red;
      delete degeneralized;
      delete to_free;
      delete echeck_inst;
    }
  else
    {
      exit_code = 1;
    }

  assert(spot::ltl::atomic_prop::instance_count() == 0);
  assert(spot::ltl::unop::instance_count() == 0);
  assert(spot::ltl::binop::instance_count() == 0);
  assert(spot::ltl::multop::instance_count() == 0);
  delete dict;
  return exit_code;
}
