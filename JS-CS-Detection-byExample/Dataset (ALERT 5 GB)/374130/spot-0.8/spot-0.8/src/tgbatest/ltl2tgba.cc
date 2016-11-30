// Copyright (C) 2007, 2008, 2009, 2010, 2011 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2005, 2006, 2007 Laboratoire d'Informatique de
// Paris 6 (LIP6), département Systèmes Répartis
// Coopératifs (SRC), Université Pierre et Marie Curie.
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
#include <cstdlib>
#include "ltlvisit/contain.hh"
#include "ltlvisit/tostring.hh"
#include "ltlvisit/apcollect.hh"
#include "ltlast/allnodes.hh"
#include "ltlparse/public.hh"
#include "tgbaalgos/ltl2tgba_lacim.hh"
#include "tgbaalgos/ltl2tgba_fm.hh"
#include "tgbaalgos/ltl2taa.hh"
#include "tgba/bddprint.hh"
#include "tgbaalgos/save.hh"
#include "tgbaalgos/dotty.hh"
#include "tgbaalgos/lbtt.hh"
#include "tgba/tgbatba.hh"
#include "tgba/tgbasgba.hh"
#include "tgba/tgbaproduct.hh"
#include "tgba/futurecondcol.hh"
#include "tgbaalgos/reducerun.hh"
#include "tgbaparse/public.hh"
#include "neverparse/public.hh"
#include "tgbaalgos/dupexp.hh"
#include "tgbaalgos/minimize.hh"
#include "tgbaalgos/neverclaim.hh"
#include "tgbaalgos/reductgba_sim.hh"
#include "tgbaalgos/replayrun.hh"
#include "tgbaalgos/rundotdec.hh"
#include "tgbaalgos/sccfilter.hh"
#include "tgbaalgos/safety.hh"
#include "tgbaalgos/eltl2tgba_lacim.hh"
#include "tgbaalgos/gtec/gtec.hh"
#include "eltlparse/public.hh"
#include "misc/timer.hh"
#include "tgbaalgos/stats.hh"
#include "tgbaalgos/scc.hh"
#include "tgbaalgos/emptiness_stats.hh"
#include "tgbaalgos/scc.hh"
#include "kripkeparse/public.hh"

std::string
ltl_defs()
{
  std::string s = "\
X=(0 1 true	   \
   1 2 $0	   \
   accept 2)	   \
U=(0 0 $0	   \
   0 1 $1	   \
   accept 1)	   \
G=(0 0 $0)	   \
F=U(true, $0)	   \
W=G($0)|U($0, $1)  \
R=!U(!$0, !$1)     \
M=F($0)&R($0, $1)";
  return s;
}

void
syntax(char* prog)
{
  // Display the supplied name unless it appears to be a libtool wrapper.
  char* slash = strrchr(prog, '/');
  if (slash && (strncmp(slash + 1, "lt-", 3) == 0))
    prog = slash + 4;

  std::cerr << "Usage: "<< prog << " [-f|-l|-le|-taa] [OPTIONS...] formula"
	    << std::endl
            << "       "<< prog << " [-f|-l|-le|-taa] -F [OPTIONS...] file"
	    << std::endl
            << "       "<< prog << " -X [OPTIONS...] file" << std::endl
	    << std::endl

            << "Translate an LTL formula into an automaton, or read the "
	    << "automaton from a file." << std::endl
	    << "Optionally multiply this automaton by another"
	    << " automaton read from a file." << std::endl
            << "Output the result in various formats, or perform an emptiness "
            << "check." << std::endl
	    << std::endl

            << "Input options:" << std::endl
            << "  -F    read the formula from a file, not from the command line"
	    << std::endl
	    << "  -X    do not compute an automaton, read it from a file"
	    << std::endl
	    << "  -XN   do not compute an automaton, read it from a"
	    << " neverclaim file" << std::endl
	    << "  -Pfile  multiply the formula automaton with the TGBA read"
	    << " from `file'\n"
	    << "  -KPfile multiply the formula automaton with the Kripke"
	    << " structure from `file'\n"
	    << std::endl

	    << "Translation algorithm:" << std::endl
            << "  -f    use Couvreur's FM algorithm for LTL"
	    << "(default)"
	    << std::endl
            << "  -l    use Couvreur's LaCIM algorithm for LTL "
	    << std::endl
	    << "  -le   use Couvreur's LaCIM algorithm for ELTL"
	    << std::endl
            << "  -taa  use Tauriainen's TAA-based algorithm for LTL"
	    << std::endl
	    << std::endl

	    << "Options for Couvreur's FM algorithm (-f):" << std::endl
	    << "  -fr1  use -r1 (see below) at each step of FM" << std::endl
	    << "  -fr2  use -r2 (see below) at each step of FM" << std::endl
	    << "  -fr3  use -r3 (see below) at each step of FM" << std::endl
	    << "  -fr4  use -r4 (see below) at each step of FM" << std::endl
	    << "  -fr5  use -r5 (see below) at each step of FM" << std::endl
	    << "  -fr6  use -r6 (see below) at each step of FM" << std::endl
	    << "  -fr7  use -r7 (see below) at each step of FM" << std::endl
            << "  -L    fair-loop approximation (implies -f)" << std::endl
            << "  -p    branching postponement (implies -f)" << std::endl
            << "  -U[PROPS]  consider atomic properties of the formula as "
	    << "exclusive events, and" << std::endl
	    << "        PROPS as unobservables events (implies -f)"
	    << std::endl
            << "  -x    try to produce a more deterministic automaton "
	    << "(implies -f)" << std::endl
	    << "  -y    do not merge states with same symbolic representation "
	    << "(implies -f)" << std::endl
	    << std::endl

	    << "Options for Couvreur's LaCIM algorithm (-l or -le):"
	    << std::endl
	    << "  -a    display the acceptance_conditions BDD, not the "
	    << "reachability graph"
	    << std::endl
	    << "  -A    same as -a, but as a set" << std::endl
	    << "  -lo   pre-define standard LTL operators as automata "
	    << "(implies -le)" << std::endl
	    << "  -r    display the relation BDD, not the reachability graph"
	    << std::endl
	    << "  -R    same as -r, but as a set" << std::endl
	    << "  -R3b  symbolically prune unaccepting SCC from BDD relation"
	    << std::endl
	    << std::endl

	    << "Options for Tauriainen's TAA-based algorithm (-taa):"
	    << std::endl
	    << "  -c    enable language containment checks (implies -taa)"
	    << std::endl
	    << std::endl

	    << "Formula simplification (before translation):"
	    << std::endl
	    << "  -r1   reduce formula using basic rewriting" << std::endl
	    << "  -r2   reduce formula using class of eventuality and "
	    << "universality" << std::endl
	    << "  -r3   reduce formula using implication between "
	    << "sub-formulae" << std::endl
	    << "  -r4   reduce formula using all above rules" << std::endl
	    << "  -r5   reduce formula using tau03" << std::endl
	    << "  -r6   reduce formula using tau03+" << std::endl
	    << "  -r7   reduce formula using tau03+ and -r4" << std::endl
	    << "  -rd   display the reduced formula" << std::endl
	    << std::endl

	    << "Automaton degeneralization (after translation):"
	    << std::endl
            << "  -lS   move generalized acceptance conditions to states "
	    << "(SGBA)" << std::endl
	    << "  -D    degeneralize the automaton as a TBA" << std::endl
	    << "  -DS   degeneralize the automaton as an SBA" << std::endl
	    << std::endl

	    << "Automaton simplifications (after translation):"
	    << std::endl
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
	    << "  -R3f  clean more acceptance conditions than -R3" << std::endl
	    << "          "
	    << "(prefer -R3 over -R3f if you degeneralize with -D, -DS, or -N)"
	    << std::endl
	    << "  -Rd   display the simulation relation" << std::endl
	    << "  -RD   display the parity game (dot format)" << std::endl
            << "  -Rm   attempt to minimize the automata" << std::endl
	    << std::endl

            << "Automaton conversion:" << std::endl
            << "  -M    convert into a deterministic minimal monitor "
	    << "(implies -R3 or R3b)" << std::endl
	    << "  -s    convert to explicit automata, and number states "
	    << "in DFS order" << std::endl
	    << "  -S    convert to explicit automata, and number states "
	    << "in BFS order" << std::endl
	    << std::endl

	    << "Options for performing emptiness checks:" << std::endl
	    << "  -e[ALGO]  run emptiness check, expect and compute an "
	    << "accepting run" << std::endl
	    << "  -E[ALGO]  run emptiness check, expect no accepting run"
	    << std::endl
	    << "  -C    compute an accepting run (Counterexample) if it exists"
	    << std::endl
	    << "  -CR   compute and replay an accepting run (implies -C)"
	    << std::endl
	    << "  -g    graph the accepting run on the automaton (requires -e)"
	    << std::endl
	    << "  -G    graph the accepting run seen as an automaton "
	    << " (requires -e)" << std::endl
	    << "  -m    try to reduce accepting runs, in a second pass"
	    << std::endl
	    << "Where ALGO should be one of:" << std::endl
	    << "  Cou99(OPTIONS) (the default)" << std::endl
	    << "  CVWY90(OPTIONS)" << std::endl
	    << "  GV04(OPTIONS)" << std::endl
	    << "  SE05(OPTIONS)" << std::endl
	    << "  Tau03(OPTIONS)" << std::endl
	    << "  Tau03_opt(OPTIONS)" << std::endl
	    << std::endl

	    << "If no emptiness check is run, the automaton will be output "
	    << "in dot format" << std::endl << "by default.  This can be "
	    << "changed with the following options." << std::endl
	    << std::endl

	    << "Output options (if no emptiness check):" << std::endl
	    << "  -b    output the automaton in the format of spot"
            << std::endl
            << "  -FC   dump the automaton showing future conditions on states"
	    << std::endl
	    << "  -k    display statistics on the automaton (size and SCCs)"
	    << std::endl
	    << "  -ks   display statistics on the automaton (size only)"
	    << std::endl
	    << "  -kt   display statistics on the automaton (size + "
	    << "subtransitions)"
	    << std::endl
	    << "  -K    dump the graph of SCCs in dot format" << std::endl
	    << "  -KV   verbosely dump the graph of SCCs in dot format"
	    << std::endl
	    << "  -N    output the never clain for Spin (implies -DS)"
	    << std::endl
	    << "  -NN   output the never clain for Spin, with commented states"
	    << " (implies -DS)" << std::endl
	    << "  -O    tell if a formula represents a safety, guarantee, "
	    << "or obligation property"
	    << std::endl
	    << "  -t    output automaton in LBTT's format" << std::endl
	    << std::endl

	    << "Miscellaneous options:" << std::endl
	    << "  -0    produce minimal output dedicated to the paper"
	    << std::endl
	    << "  -d    turn on traces during parsing" << std::endl
            << "  -T    time the different phases of the translation"
	    << std::endl
	    << "  -v    display the BDD variables used by the automaton"
	    << std::endl;
  exit(2);
}

int
main(int argc, char** argv)
{
  int exit_code = 0;

  bool debug_opt = false;
  bool paper_opt = false;
  enum { NoDegen, DegenTBA, DegenSBA } degeneralize_opt = NoDegen;
  enum { TransitionLabeled, StateLabeled } labeling_opt = TransitionLabeled;
  enum { TransFM, TransLaCIM, TransLaCIM_ELTL, TransLaCIM_ELTL_ops, TransTAA }
    translation = TransFM;
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
  bool accepting_run = false;
  bool accepting_run_replay = false;
  bool from_file = false;
  bool read_neverclaim = false;
  int reduc_aut = spot::Reduce_None;
  int redopt = spot::ltl::Reduce_None;
  bool scc_filter_all = false;
  bool symbolic_scc_pruning = false;
  bool display_reduce_form = false;
  bool display_rel_sim = false;
  bool display_parity_game = false;
  bool post_branching = false;
  bool fair_loop_approx = false;
  bool graph_run_opt = false;
  bool graph_run_tgba_opt = false;
  bool opt_reduce = false;
  bool opt_minimize = false;
  bool opt_monitor = false;
  bool containment = false;
  bool show_fc = false;
  bool spin_comments = false;
  spot::ltl::environment& env(spot::ltl::default_environment::instance());
  spot::ltl::atomic_prop_set* unobservables = 0;
  spot::tgba* system = 0;
  const spot::tgba* product = 0;
  const spot::tgba* product_to_free = 0;
  spot::bdd_dict* dict = new spot::bdd_dict();
  spot::timer_map tm;
  bool use_timer = false;
  bool assume_sba = false;

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
	  translation = TransTAA;
	}
      else if (!strcmp(argv[formula_index], "-C"))
	{
	  accepting_run = true;
	}
      else if (!strcmp(argv[formula_index], "-CR"))
	{
	  accepting_run = true;
	  accepting_run_replay = true;
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
	  translation = TransFM;
	}
      else if (!strcmp(argv[formula_index], "-fr1"))
	{
	  translation = TransFM;
	  fm_red |= spot::ltl::Reduce_Basics;
	}
      else if (!strcmp(argv[formula_index], "-fr2"))
	{
	  translation = TransFM;
	  fm_red |= spot::ltl::Reduce_Eventuality_And_Universality;
	}
      else if (!strcmp(argv[formula_index], "-fr3"))
	{
	  translation = TransFM;
	  fm_red |= spot::ltl::Reduce_Syntactic_Implications;
	}
      else if (!strcmp(argv[formula_index], "-fr4"))
	{
	  translation = TransFM;
 	  fm_red |= spot::ltl::Reduce_Basics
	    | spot::ltl::Reduce_Eventuality_And_Universality
	    | spot::ltl::Reduce_Syntactic_Implications;
	}
      else if (!strcmp(argv[formula_index], "-fr5"))
	{
	  translation = TransFM;
	  fm_red |= spot::ltl::Reduce_Containment_Checks;
	}
      else if (!strcmp(argv[formula_index], "-fr6"))
	{
	  translation = TransFM;
	  fm_red |= spot::ltl::Reduce_Containment_Checks_Stronger;
	}
      else if (!strcmp(argv[formula_index], "-fr7"))
	{
	  translation = TransFM;
	  fm_red |= spot::ltl::Reduce_All;
	}
      else if (!strcmp(argv[formula_index], "-F"))
	{
	  file_opt = true;
	}
      else if (!strcmp(argv[formula_index], "-FC"))
	{
	  show_fc = true;
	}
      else if (!strcmp(argv[formula_index], "-g"))
	{
	  accepting_run = true;
	  graph_run_opt = true;
	}
      else if (!strcmp(argv[formula_index], "-G"))
	{
	  accepting_run = true;
	  graph_run_tgba_opt = true;
	}
      else if (!strcmp(argv[formula_index], "-k"))
	{
	  output = 9;
	}
      else if (!strcmp(argv[formula_index], "-ks"))
	{
	  output = 12;
	}
      else if (!strcmp(argv[formula_index], "-kt"))
	{
	  output = 13;
	}
      else if (!strcmp(argv[formula_index], "-K"))
	{
	  output = 10;
	}
      else if (!strncmp(argv[formula_index], "-KP", 3))
	{
	  tm.start("reading -KP's argument");

	  spot::kripke_parse_error_list pel;
	  system = spot::kripke_parse(argv[formula_index] + 3,
				      pel, dict, env, debug_opt);
	  if (spot::format_kripke_parse_errors(std::cerr,
					       argv[formula_index] + 2, pel))
	    return 2;
	  tm.stop("reading -KP's argument");
	}
      else if (!strcmp(argv[formula_index], "-KV"))
	{
	  output = 11;
	}
      else if (!strcmp(argv[formula_index], "-l"))
	{
	  translation = TransLaCIM;
	}
      else if (!strcmp(argv[formula_index], "-le"))
	{
	  /* -lo is documented to imply -le, so do not overwrite it. */
	  if (translation != TransLaCIM_ELTL_ops)
	    translation = TransLaCIM_ELTL;
	}
      else if (!strcmp(argv[formula_index], "-lo"))
	{
	  translation = TransLaCIM_ELTL_ops;
	}
      else if (!strcmp(argv[formula_index], "-L"))
	{
	  fair_loop_approx = true;
	  translation = TransFM;
	}
      else if (!strcmp(argv[formula_index], "-lS"))
	{
	  labeling_opt = StateLabeled;
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
      else if (!strcmp(argv[formula_index], "-NN"))
	{
	  degeneralize_opt = DegenSBA;
	  output = 8;
	  spin_comments = true;
	}
      else if (!strcmp(argv[formula_index], "-O"))
	{
	  output = 14;
          opt_minimize = true;
	}
      else if (!strcmp(argv[formula_index], "-p"))
	{
	  post_branching = true;
	  translation = TransFM;
	}
      else if (!strncmp(argv[formula_index], "-P", 2))
	{
	  tm.start("reading -P's argument");

	  spot::tgba_parse_error_list pel;
	  spot::tgba_explicit_string* s;
	  s = spot::tgba_parse(argv[formula_index] + 2,
			       pel, dict, env, env, debug_opt);
	  if (spot::format_tgba_parse_errors(std::cerr,
					     argv[formula_index] + 2, pel))
	    return 2;
	  s->merge_transitions();
	  tm.stop("reading -P's argument");
	  system = s;
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
      else if (!strcmp(argv[formula_index], "-R3f"))
	{
	  reduc_aut |= spot::Reduce_Scc;
	  scc_filter_all = true;
	}
      else if (!strcmp(argv[formula_index], "-R3b"))
	{
	  symbolic_scc_pruning = true;
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
      else if (!strcmp(argv[formula_index], "-Rm"))
        {
          opt_minimize = true;
        }
      else if (!strcmp(argv[formula_index], "-M"))
        {
          opt_monitor = true;
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
      else if (!strcmp(argv[formula_index], "-T"))
	{
	  use_timer = true;
	}
      else if (!strcmp(argv[formula_index], "-taa"))
	{
	  translation = TransTAA;
	}
      else if (!strncmp(argv[formula_index], "-U", 2))
	{
	  unobservables = new spot::ltl::atomic_prop_set;
	  translation = TransFM;
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
	  translation = TransFM;
	  fm_exprop_opt = true;
	}
      else if (!strcmp(argv[formula_index], "-X"))
	{
	  from_file = true;
	}
      else if (!strcmp(argv[formula_index], "-XN"))
	{
	  from_file = true;
	  read_neverclaim = true;
	}
      else if (!strcmp(argv[formula_index], "-y"))
	{
	  translation = TransFM;
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
      tm.start("reading formula");
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
      tm.stop("reading formula");
    }
  else
    {
      input = argv[formula_index];
    }

  spot::ltl::formula* f = 0;
  if (!from_file) // Reading a formula, not reading an automaton from a file.
    {
      switch (translation)
	{
	case TransFM:
	case TransLaCIM:
	case TransTAA:
	  {
	    spot::ltl::parse_error_list pel;
	    tm.start("parsing formula");
	    f = spot::ltl::parse(input, pel, env, debug_opt);
	    tm.stop("parsing formula");
	    exit_code = spot::ltl::format_parse_errors(std::cerr, input, pel);
	  }
	  break;
	case TransLaCIM_ELTL:
	  {
	    spot::eltl::parse_error_list p;
	    tm.start("parsing formula");
	    f = spot::eltl::parse_string(input, p, env, false);
	    tm.stop("parsing formula");
	    exit_code = spot::eltl::format_parse_errors(std::cerr, p);
	  }
	  break;
	case TransLaCIM_ELTL_ops:
	  {
	    // Call the LTL parser first to handle operators such as
	    // [] or <> and rewrite the output as a string with G or F.
	    // Then prepend definitions of usual LTL operators, and parse
	    // the result again as an ELTL formula.
	    spot::ltl::parse_error_list pel;
	    tm.start("parsing formula");
	    f = spot::ltl::parse(input, pel, env, debug_opt);
	    input = ltl_defs();
	    input += "%";
	    input += spot::ltl::to_string(f, true);
	    f->destroy();
	    spot::eltl::parse_error_list p;
	    f = spot::eltl::parse_string(input, p, env, debug_opt);
	    tm.stop("parsing formula");
	    exit_code = spot::eltl::format_parse_errors(std::cerr, p);
	  }
	  break;
	}
    }
  if (f || from_file)
    {
      const spot::tgba_bdd_concrete* concrete = 0;
      const spot::tgba* to_free = 0;
      const spot::tgba* a = 0;

      if (from_file)
	{
	  spot::tgba_explicit_string* e;
	  if (!read_neverclaim)
	    {
	      spot::tgba_parse_error_list pel;
	      tm.start("parsing automaton");
	      to_free = a = e = spot::tgba_parse(input, pel, dict,
						 env, env, debug_opt);
	      tm.stop("parsing automaton");
	      if (spot::format_tgba_parse_errors(std::cerr, input, pel))
		{
		  delete to_free;
		  delete dict;
		  return 2;
		}
	    }
	  else
	    {
	      spot::neverclaim_parse_error_list pel;
	      tm.start("parsing neverclaim");
	      to_free = a = e = spot::neverclaim_parse(input, pel, dict,
						       env, debug_opt);
	      tm.stop("parsing neverclaim");
	      if (spot::format_neverclaim_parse_errors(std::cerr, input, pel))
		{
		  delete to_free;
		  delete dict;
		  return 2;
		}
	    }
	  e->merge_transitions();
	}
      else
	{
	  if (redopt != spot::ltl::Reduce_None)
	    {
	      tm.start("reducing formula");
	      spot::ltl::formula* t = spot::ltl::reduce(f, redopt);
	      f->destroy();
	      tm.stop("reducing formula");
	      f = t;
	      if (display_reduce_form)
		std::cout << spot::ltl::to_string(f) << std::endl;
	    }

	  tm.start("translating formula");
	  switch (translation)
	    {
	    case TransFM:
	      a = spot::ltl_to_tgba_fm(f, dict, fm_exprop_opt,
				       fm_symb_merge_opt,
				       post_branching,
				       fair_loop_approx,
				       unobservables,
				       fm_red);
	      break;
	    case TransTAA:
	      a = spot::ltl_to_taa(f, dict, containment);
	      break;
	    case TransLaCIM:
	      a = concrete = spot::ltl_to_tgba_lacim(f, dict);
	      break;
	    case TransLaCIM_ELTL:
	    case TransLaCIM_ELTL_ops:
	      a = concrete = spot::eltl_to_tgba_lacim(f, dict);
	      break;
	    }
	  tm.stop("translating formula");
	  to_free = a;
	}

      if (opt_monitor && ((reduc_aut & spot::Reduce_Scc) == 0))
	{
	  if (dynamic_cast<const spot::tgba_bdd_concrete*>(a))
	    symbolic_scc_pruning = true;
	  else
	    reduc_aut |= spot::Reduce_Scc;
	}

      if (symbolic_scc_pruning)
        {
	  const spot::tgba_bdd_concrete* bc =
	    dynamic_cast<const spot::tgba_bdd_concrete*>(a);
	  if (!bc)
	    {
	      std::cerr << ("Error: Automaton is not symbolic: cannot "
			    "prune SCCs symbolically.\n"
			    "       Try -R3 instead of -R3b, or use "
			    "another translation.")
			<< std::endl;
	      exit(2);
	    }
	  else
	    {
	      tm.start("reducing A_f w/ symbolic SCC pruning");
	      if (bc)
		const_cast<spot::tgba_bdd_concrete*>(bc)
		  ->delete_unaccepting_scc();
	      tm.stop("reducing A_f w/ symbolic SCC pruning");
	    }
	}

      // Remove dead SCCs and useless acceptance conditions before
      // degeneralization.
      const spot::tgba* aut_scc = 0;
      if (reduc_aut & spot::Reduce_Scc)
	{
	  tm.start("reducing A_f w/ SCC");
	  a = aut_scc = spot::scc_filter(a, scc_filter_all);
	  tm.stop("reducing A_f w/ SCC");
	}

      const spot::tgba_tba_proxy* degeneralized = 0;
      const spot::tgba_sgba_proxy* state_labeled = 0;

      unsigned int n_acc = a->number_of_acceptance_conditions();
      if (echeck_inst
	  && degeneralize_opt == NoDegen
	  && n_acc > 1
	  && echeck_inst->max_acceptance_conditions() < n_acc)
	degeneralize_opt = DegenTBA;

      if (degeneralize_opt == DegenTBA)
	{
	  a = degeneralized = new spot::tgba_tba_proxy(a);
	}
      else if (degeneralize_opt == DegenSBA)
	{
	  a = degeneralized = new spot::tgba_sba_proxy(a);
	  assume_sba = true;
	}
      else if (labeling_opt == StateLabeled)
	{
	  a = state_labeled = new spot::tgba_sgba_proxy(a);
	}

      const spot::tgba* minimized = 0;
      if (opt_minimize)
	{
	  tm.start("obligation minimization");
	  minimized = minimize_obligation(a, f);
	  tm.stop("obligation minimization");

	  if (minimized == 0)
	    {
	      // if (!f)
		{
		  std::cerr << "Error: Without a formula I cannot make "
			    << "sure that the automaton built with -Rm\n"
			    << "       is correct." << std::endl;
		  exit(2);
		}
	    }
	  else if (minimized == a)
	    {
	      minimized = 0;
	    }
	  else
	    {
	      a = minimized;
	      assume_sba = true;
	    }
	}

      if (opt_monitor)
	{
	  tm.start("Monitor minimization");
	  a = minimized = minimize_monitor(a);
	  tm.stop("Monitor minimization");
	  assume_sba = false; 	// All states are accepting, so double
				// circles in the dot output are
				// pointless.
	}

      spot::tgba_reduc* aut_red = 0;
      if (reduc_aut != spot::Reduce_None)
	{
	  if (reduc_aut & ~spot::Reduce_Scc)
	    {
	      tm.start("reducing A_f w/ sim.");
	      a = aut_red = new spot::tgba_reduc(a);

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
			spot::get_direct_relation_simulation
			  (a, std::cout, display_parity_game);
		      assert(rel_dir);
		    }
		  if (reduc_aut & (spot::Reduce_quotient_Del_Sim |
					spot::Reduce_transition_Del_Sim))
		    {
		      rel_del =
			spot::get_delayed_relation_simulation
			  (a, std::cout, display_parity_game);
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
	      tm.stop("reducing A_f w/ sim.");
	    }
	}

      const spot::tgba_explicit* expl = 0;
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

      const spot::tgba* product_degeneralized = 0;

      if (system)
        {
          a = product = product_to_free = new spot::tgba_product(system, a);

	  assume_sba = false;

	  unsigned int n_acc = a->number_of_acceptance_conditions();
	  if (echeck_inst
	      && degeneralize_opt == NoDegen
	      && n_acc > 1
	      && echeck_inst->max_acceptance_conditions() < n_acc)
            degeneralize_opt = DegenTBA;
          if (degeneralize_opt == DegenTBA)
	    {
	      a = product = product_degeneralized =
		new spot::tgba_tba_proxy(product);
	    }
          else if (degeneralize_opt == DegenSBA)
	    {
	      a = product = product_degeneralized =
		new spot::tgba_sba_proxy(product);
	      assume_sba = true;
	    }
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

      if (show_fc)
	{
	  a = new spot::future_conditions_collector(a, true);
	}

      if (output != -1)
	{
	  tm.start("producing output");
	  switch (output)
	    {
	    case 0:
	      spot::dotty_reachable(std::cout, a, assume_sba);
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
		if (assume_sba || dynamic_cast<const spot::tgba_sba_proxy*>(a))
		  spot::never_claim_reachable(std::cout, a, f, spin_comments);
		else
		  {
		    // It is possible that we have applied other
		    // operations to the automaton since its initial
		    // degeneralization.  Let's degeneralize again!
		    spot::tgba_sba_proxy* s = new spot::tgba_sba_proxy(a);
		    spot::never_claim_reachable(std::cout, s, f, spin_comments);
		    delete s;
		  }
		break;
	      }
	    case 9:
	      stats_reachable(a).dump(std::cout);
	      build_scc_stats(a).dump(std::cout);
	      break;
	    case 10:
	      dump_scc_dot(a, std::cout, false);
	      break;
	    case 11:
	      dump_scc_dot(a, std::cout, true);
	      break;
	    case 12:
	      stats_reachable(a).dump(std::cout);
	      break;
	    case 13:
	      sub_stats_reachable(a).dump(std::cout);
	      break;
	    case 14:
	      if (minimized == 0)
		{
		  std::cout << "this is not an obligation property";
		}
	      else
		{
		  bool g = is_guarantee_automaton(minimized);
		  bool s = is_safety_mwdba(minimized);
		  if (g && !s)
		    {
		      std::cout << "this is a guarantee property (hence, "
				<< "an obligation property)";
		    }
		  else if (s && !g)
		    {
		      std::cout << "this is a safety property (hence, "
				<< "an obligation property)";
		    }
		  else if (s && g)
		    {
		      std::cout << "this is a guarantee and a safety property"
				<< " (and of course an obligation property)";
		    }
		  else
		    {
		      std::cout << "this is an obligation property that is "
				<< "neither a safety nor a guarantee";
		    }
		}
	      std::cout << std::endl;

	      break;
	    default:
	      assert(!"unknown output option");
	    }
	  tm.stop("producing output");
	}

      if (echeck_inst)
	{
	  spot::emptiness_check* ec = echeck_inst->instantiate(a);
	  bool search_many = echeck_inst->options().get("repeated");
	  assert(ec);
	  do
	    {
	      tm.start("running emptiness check");
	      spot::emptiness_check_result* res = ec->check();
	      tm.stop("running emptiness check");

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
                  else if (accepting_run)
                    {

		      tm.start("computing accepting run");
                      spot::tgba_run* run = res->accepting_run();
		      tm.stop("computing accepting run");

                      if (!run)
                        {
                          std::cout << "an accepting run exists" << std::endl;
                        }
                      else
                        {
                          if (opt_reduce)
                            {
			      tm.start("reducing accepting run");
                              spot::tgba_run* redrun =
                                spot::reduce_run(res->automaton(), run);
			      tm.stop("reducing accepting run");
                              delete run;
                              run = redrun;
                            }
			  if (accepting_run_replay)
			    {
			      tm.start("replaying acc. run");
			      if (!spot::replay_tgba_run(std::cout, a,
							 run, true))
				exit_code = 1;
			      tm.stop("replaying acc. run");
			    }
			  else
			    {
			      tm.start("printing accepting run");
			      if (graph_run_opt)
				{
				  spot::tgba_run_dotty_decorator deco(run);
				  spot::dotty_reachable(std::cout, a,
							assume_sba, &deco);
				}
			      else if (graph_run_tgba_opt)
				{
				  spot::tgba* ar =
				    spot::tgba_run_to_tgba(a, run);
				  spot::dotty_reachable(std::cout, ar);
				  delete ar;
				}
			      else
				{
				  spot::print_tgba_run(std::cout, a, run);
				}
			      tm.stop("printing accepting run");
			    }
			  delete run;
                        }
                    }
		  else
		    {
		      std::cout << "an accepting run exists "
				<< "(use -C to print it)" << std::endl;
		    }
                }
	      delete res;
	    }
	  while (search_many);
	  delete ec;
	}

      if (show_fc)
	delete a;
      if (f)
        f->destroy();
      delete product_degeneralized;
      delete product_to_free;
      delete system;
      delete expl;
      delete aut_red;
      delete minimized;
      delete degeneralized;
      delete aut_scc;
      delete state_labeled;
      delete to_free;
      delete echeck_inst;
    }
  else
    {
      exit_code = 1;
    }

  if (use_timer)
    tm.print(std::cout);

  if (unobservables)
    {
      for (spot::ltl::atomic_prop_set::iterator i =
	     unobservables->begin(); i != unobservables->end(); ++i)
	(*i)->destroy();
      delete unobservables;
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
  delete dict;
  return exit_code;
}
