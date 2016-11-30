// -*- coding: utf-8 -*-
// Copyright (C) 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014
// Laboratoire de Recherche et Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2005, 2006, 2007 Laboratoire d'Informatique de
// Paris 6 (LIP6), département Systèmes Répartis
// Coopératifs (SRC), Université Pierre et Marie Curie.
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
#include <iomanip>
#include <cassert>
#include <fstream>
#include <string>
#include <cstdlib>
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
#include "tgbaalgos/hoaf.hh"
#include "tgba/tgbatba.hh"
#include "tgba/tgbasgba.hh"
#include "tgbaalgos/degen.hh"
#include "tgba/tgbaproduct.hh"
#include "tgba/futurecondcol.hh"
#include "tgbaalgos/reducerun.hh"
#include "tgbaparse/public.hh"
#include "neverparse/public.hh"
#include "dstarparse/public.hh"
#include "tgbaalgos/dupexp.hh"
#include "tgbaalgos/minimize.hh"
#include "taalgos/minimize.hh"
#include "tgbaalgos/neverclaim.hh"
#include "tgbaalgos/replayrun.hh"
#include "tgbaalgos/rundotdec.hh"
#include "tgbaalgos/sccfilter.hh"
#include "tgbaalgos/safety.hh"
#include "tgbaalgos/eltl2tgba_lacim.hh"
#include "tgbaalgos/gtec/gtec.hh"
#include "eltlparse/public.hh"
#include "misc/timer.hh"
#include "misc/unique_ptr.hh"
#include "tgbaalgos/stats.hh"
#include "tgbaalgos/scc.hh"
#include "tgbaalgos/emptiness_stats.hh"
#include "tgbaalgos/scc.hh"
#include "tgbaalgos/isdet.hh"
#include "tgbaalgos/cycles.hh"
#include "tgbaalgos/isweakscc.hh"
#include "kripkeparse/public.hh"
#include "tgbaalgos/simulation.hh"
#include "tgbaalgos/compsusp.hh"
#include "tgbaalgos/powerset.hh"
#include "tgbaalgos/dtgbacomp.hh"
#include "tgbaalgos/complete.hh"
#include "tgbaalgos/dtbasat.hh"
#include "tgbaalgos/dtgbasat.hh"

#include "taalgos/tgba2ta.hh"
#include "taalgos/dotty.hh"
#include "taalgos/stats.hh"

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
	    << "  -XD   do not compute an automaton, read it from an"
	    << " ltl2dstar file" << std::endl
	    << "  -XDB  read the from an ltl2dstar file and convert it to "
	    << "TGBA" << std::endl
	    << "  -XDD  read the from an ltl2dstar file and convert it to "
	    << "TGBA,\n       keeping it deterministic when possible\n"
	    << "  -XL   do not compute an automaton, read it from an"
	    << " LBTT file" << std::endl
	    << "  -XN   do not compute an automaton, read it from a"
	    << " neverclaim file" << std::endl
	    << "  -Pfile  multiply the formula automaton with the TGBA read"
	    << " from `file'\n"
	    << "  -KPfile multiply the formula automaton with the Kripke"
	    << " structure from `file'\n"
	    << std::endl

	    << "Translation algorithm:" << std::endl
            << "  -f    use Couvreur's FM algorithm for LTL"
	    << " (default)" << std::endl
            << "  -l    use Couvreur's LaCIM algorithm for LTL "
	    << std::endl
	    << "  -le   use Couvreur's LaCIM algorithm for ELTL"
	    << std::endl
            << "  -taa  use Tauriainen's TAA-based algorithm for LTL"
	    << std::endl
	    << "  -u    use Compositional translation"
	    << std::endl
	    << std::endl

	    << "Options for Couvreur's FM algorithm (-f):" << std::endl
	    << "  -fr   reduce formula at each step of FM" << std::endl
	    << "          as specified with the -r{1..7} options" << std::endl
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
	    << "  -rD   dump statistics about the simplifier cache" << std::endl
	    << "  -rL   disable basic rewritings producing larger formulas"
	    << std::endl
	    << "  -ru   lift formulae that are eventual and universal"
	    << std::endl << std::endl

	    << "Automaton degeneralization (after translation):"
	    << std::endl
            << "  -lS   move generalized acceptance conditions to states "
	    << "(SGBA)" << std::endl
	    << "  -D    degeneralize the automaton as a TBA" << std::endl
	    << "  -DS   degeneralize the automaton as an SBA" << std::endl
	    << "          (append z/Z, o/O, l/L: to turn on/off options "
            << "(default: zol)\n "
	    << "          z: level resetting, o: adaptive order, "
	    << "l: level cache)\n"
	    << std::endl

	    << "Automaton simplifications (after translation):"
	    << std::endl
	    << "  -R3   use SCC to reduce the automaton" << std::endl
	    << "  -R3f  clean more acceptance conditions than -R3" << std::endl
	    << "          "
	    << "(prefer -R3 over -R3f if you degeneralize with -D, -DS, or -N)"
	    << std::endl
	    << "  -RDS  reduce the automaton with direct simulation"
	    << std::endl
	    << "  -RRS  reduce the automaton with reverse simulation"
	    << std::endl
	    << "  -RIS  iterate both direct and reverse simulations"
	    << std::endl
            << "  -RDCS reduce the automaton with direct simulation"
            << std::endl
            << "  -Rm   attempt to WDBA-minimize the automaton" << std::endl
	    << std::endl
            << "  -RM   attempt to WDBA-minimize the automaton unless the "
	    << "result is bigger" << std::endl
	    << "  -RQ   determinize a TGBA (assuming it's legal!)" << std::endl
	    << std::endl

            << "Automaton conversion:" << std::endl
            << "  -M    convert into a deterministic minimal monitor "
	    << "(implies -R3 or R3b)" << std::endl
	    << "  -s    convert to explicit automaton, and number states "
	    << "in DFS order" << std::endl
	    << "  -S    convert to explicit automaton, and number states "
	    << "in BFS order" << std::endl
	    << std::endl

	    << "Conversion to Testing Automaton:" << std::endl
 	    << "  -TA   output a Generalized Testing Automaton (GTA),\n"
	    << "          or a Testing Automaton (TA) with -DS\n"
 	    << "  -lv   add an artificial livelock state to obtain a "
	    << "Single-pass (G)TA\n"
            << "  -sp   convert into a single-pass (G)TA without artificial "
	    << "livelock state\n"
	    << "  -in   do not use an artificial initial state\n"
            << "  -TGTA output a Transition-based Generalized TA"
            << std::endl
	    << "  -RT   reduce the (G)TA/TGTA using bisimulation.\n"
            << std::endl

	    << "Options for performing emptiness checks (on TGBA):" << std::endl
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
	    << "  -KC   list cycles in automaton" << std::endl
	    << "  -KW   list weak SCCs" << std::endl
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
	    << "  -8    output UTF-8 formulae" << std::endl
	    << "  -d    turn on traces during parsing" << std::endl
            << "  -T    time the different phases of the translation"
	    << std::endl
	    << "  -v    display the BDD variables used by the automaton"
	    << std::endl
	    << std::endl;

  exit(2);
}

static int
to_int(const char* s)
{
  char* endptr;
  int res = strtol(s, &endptr, 10);
  if (*endptr)
    {
      std::cerr << "Failed to parse `" << s << "' as an integer." << std::endl;
      exit(1);
    }
  return res;
}

int
main(int argc, char** argv)
{
  int exit_code = 0;

  bool debug_opt = false;
  bool paper_opt = false;
  bool utf8_opt = false;
  enum { NoDegen, DegenTBA, DegenSBA } degeneralize_opt = NoDegen;
  enum { TransitionLabeled, StateLabeled } labeling_opt = TransitionLabeled;
  enum { TransFM, TransLaCIM, TransLaCIM_ELTL, TransLaCIM_ELTL_ops, TransTAA,
	 TransCompo }
    translation = TransFM;
  bool fm_red = false;
  bool fm_exprop_opt = false;
  bool fm_symb_merge_opt = true;
  bool file_opt = false;
  bool degen_reset = true;
  bool degen_order = false;
  bool degen_cache = true;
  int output = 0;
  int formula_index = 0;
  const char* echeck_algo = 0;
  spot::emptiness_check_instantiator* echeck_inst = 0;
  enum { NoneDup, BFS, DFS } dupexp = NoneDup;
  bool expect_counter_example = false;
  bool accepting_run = false;
  bool accepting_run_replay = false;
  bool from_file = false;
  enum { ReadSpot, ReadLbtt, ReadNeverclaim, ReadDstar } readformat = ReadSpot;
  bool nra2nba = false;
  bool dra2dba = false;
  bool scc_filter = false;
  bool simpltl = false;
  spot::ltl::ltl_simplifier_options redopt(false, false, false, false,
					   false, false, false);
  bool simpcache_stats = false;
  bool scc_filter_all = false;
  bool symbolic_scc_pruning = false;
  bool display_reduced_form = false;
  bool post_branching = false;
  bool fair_loop_approx = false;
  bool graph_run_opt = false;
  bool graph_run_tgba_opt = false;
  bool opt_reduce = false;
  bool opt_minimize = false;
  bool opt_determinize = false;
  unsigned opt_determinize_threshold = 0;
  unsigned opt_o_threshold = 0;
  bool opt_dtgbacomp = false;
  bool reject_bigger = false;
  bool opt_bisim_ta = false;
  bool opt_monitor = false;
  bool containment = false;
  bool show_fc = false;
  bool spin_comments = false;
  const char* hoaf_opt = 0;
  spot::ltl::environment& env(spot::ltl::default_environment::instance());
  spot::ltl::atomic_prop_set* unobservables = 0;
  spot::tgba* system_aut = 0;
  const spot::tgba* product_to_free = 0;
  spot::bdd_dict* dict = new spot::bdd_dict();
  spot::timer_map tm;
  bool use_timer = false;
  bool reduction_dir_sim = false;
  bool reduction_rev_sim = false;
  bool reduction_iterated_sim = false;
  bool reduction_dont_care_sim = false;
  int limit_dont_care_sim = 0;
  bool reduction_iterated_dont_care_sim = false;
  spot::tgba* temp_dir_sim = 0;
  bool ta_opt = false;
  bool tgta_opt = false;
  bool opt_with_artificial_initial_state = true;
  bool opt_single_pass_emptiness_check = false;
  bool opt_with_artificial_livelock = false;
  spot::tgba* temp_rev_sim = 0;
  spot::tgba* temp_iterated_sim = 0;
  spot::tgba* temp_dont_care_sim = 0;
  spot::tgba* temp_dont_care_iterated_sim = 0;
  bool cs_nowdba = true;
  bool cs_wdba_smaller = false;
  bool cs_nosimul = true;
  bool cs_early_start = false;
  bool cs_oblig = false;
  bool opt_complete = false;
  int opt_dtbasat = -1;
  int opt_dtgbasat = -1;

  for (;;)
    {
      if (argc < formula_index + 2)
	syntax(argv[0]);

      ++formula_index;

      if (!strcmp(argv[formula_index], "-0"))
	{
	  paper_opt = true;
	}
      else if (!strcmp(argv[formula_index], "-8"))
	{
	  utf8_opt = true;
	  spot::enable_utf8();
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
      else if (!strcmp(argv[formula_index], "-DC"))
	{
	  opt_dtgbacomp = true;
	}
      else if (!strncmp(argv[formula_index], "-DS", 3))
	{
	  degeneralize_opt = DegenSBA;
	  const char* p = argv[formula_index] + 3;
	  while (*p)
	    {
	      switch (*p++)
		{
		case 'o':
		  degen_order = true;
		  break;
		case 'O':
		  degen_order = false;
		  break;
		case 'z':
		  degen_reset = true;
		  break;
		case 'Z':
		  degen_reset = false;
		  break;
		case 'l':
		  degen_cache = true;
		  break;
		case 'L':
		  degen_cache = false;
		  break;
		}
	    }
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
      else if (!strcmp(argv[formula_index], "-fr"))
	{
	  fm_red = true;
	  translation = TransFM;
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
      else if (!strncmp(argv[formula_index], "-H", 2))
	{
	  output = 17;
	  hoaf_opt = argv[formula_index] + 2;
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
	  system_aut = spot::kripke_parse(argv[formula_index] + 3,
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
      else if (!strcmp(argv[formula_index], "-KC"))
	{
	  output = 15;
	}
      else if (!strcmp(argv[formula_index], "-KW"))
	{
	  output = 16;
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
      else if (!strncmp(argv[formula_index], "-O", 2))
	{
	  output = 14;
          opt_minimize = true;
	  if (argv[formula_index][2] != 0)
	    opt_o_threshold = to_int(argv[formula_index] + 2);
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
	  system_aut = s;
	}
      else if (!strcmp(argv[formula_index], "-r"))
	{
	  output = 1;
	}
      else if (!strcmp(argv[formula_index], "-r1"))
	{
	  simpltl = true;
	  redopt.reduce_basics = true;
	}
      else if (!strcmp(argv[formula_index], "-r2"))
	{
	  simpltl = true;
	  redopt.event_univ = true;
	}
      else if (!strcmp(argv[formula_index], "-r3"))
	{
	  simpltl = true;
	  redopt.synt_impl = true;
	}
      else if (!strcmp(argv[formula_index], "-r4"))
	{
	  simpltl = true;
	  redopt.reduce_basics = true;
	  redopt.event_univ = true;
	  redopt.synt_impl = true;
	}
      else if (!strcmp(argv[formula_index], "-r5"))
	{
	  simpltl = true;
	  redopt.containment_checks = true;
	}
      else if (!strcmp(argv[formula_index], "-r6"))
	{
	  simpltl = true;
	  redopt.containment_checks = true;
	  redopt.containment_checks_stronger = true;
	}
      else if (!strcmp(argv[formula_index], "-r7"))
	{
	  simpltl = true;
	  redopt.reduce_basics = true;
	  redopt.event_univ = true;
	  redopt.synt_impl = true;
	  redopt.containment_checks = true;
	  redopt.containment_checks_stronger = true;
	}
      else if (!strcmp(argv[formula_index], "-R"))
	{
	  output = 3;
	}
      else if (!strcmp(argv[formula_index], "-R1q")
	       || !strcmp(argv[formula_index], "-R1t")
	       || !strcmp(argv[formula_index], "-R2q")
	       || !strcmp(argv[formula_index], "-R2t"))
	{
	  // For backward compatibility, make all these options
	  // equal to -RDS.
	  reduction_dir_sim = true;
	}
      else if (!strcmp(argv[formula_index], "-RRS"))
        {
          reduction_rev_sim = true;
        }
      else if (!strcmp(argv[formula_index], "-R3"))
	{
	  scc_filter = true;
	}
      else if (!strcmp(argv[formula_index], "-R3f"))
	{
	  scc_filter = true;
	  scc_filter_all = true;
	}
      else if (!strcmp(argv[formula_index], "-R3b"))
	{
	  symbolic_scc_pruning = true;
	}
      else if (!strcmp(argv[formula_index], "-rd"))
	{
	  display_reduced_form = true;
	}
      else if (!strcmp(argv[formula_index], "-rD"))
	{
	  simpcache_stats = true;
	}
      else if (!strcmp(argv[formula_index], "-RC"))
	{
	  opt_complete = true;
	}
      else if (!strcmp(argv[formula_index], "-RDS"))
        {
          reduction_dir_sim = true;
        }
      else if (!strcmp(argv[formula_index], "-RIS"))
        {
          reduction_iterated_sim = true;
        }
      else if (!strncmp(argv[formula_index], "-RDCS", 5))
        {
          reduction_dont_care_sim = true;
          if (argv[formula_index][5] == '=')
            limit_dont_care_sim = to_int(argv[formula_index] + 6);
        }
      else if (!strncmp(argv[formula_index], "-RDCIS", 6))
        {
          reduction_iterated_dont_care_sim = true;
          if (argv[formula_index][6] == '=')
            limit_dont_care_sim = to_int(argv[formula_index] + 7);
        }
      else if (!strcmp(argv[formula_index], "-rL"))
        {
	  simpltl = true;
	  redopt.reduce_basics = true;
	  redopt.reduce_size_strictly = true;
        }
      else if (!strncmp(argv[formula_index], "-RG", 3))
        {
	  if (argv[formula_index][3] != 0)
	    opt_dtgbasat = to_int(argv[formula_index] + 3);
	  else
	    opt_dtgbasat = 0;
          //output = -1;
        }
      else if (!strcmp(argv[formula_index], "-Rm"))
        {
          opt_minimize = true;
        }
      else if (!strcmp(argv[formula_index], "-RM"))
        {
          opt_minimize = true;
	  reject_bigger = true;
        }
      else if (!strncmp(argv[formula_index], "-RQ", 3))
        {
          opt_determinize = true;
	  if (argv[formula_index][3] != 0)
	    opt_determinize_threshold = to_int(argv[formula_index] + 3);
        }
      else if (!strncmp(argv[formula_index], "-RS", 3))
        {
	  if (argv[formula_index][3] != 0)
	    opt_dtbasat = to_int(argv[formula_index] + 3);
	  else
	    opt_dtbasat = 0;
          //output = -1;
        }
      else if (!strcmp(argv[formula_index], "-RT"))
        {
          opt_bisim_ta = true;
	}
      else if (!strcmp(argv[formula_index], "-ru"))
        {
	  simpltl = true;
	  redopt.event_univ = true;
	  redopt.favor_event_univ = true;
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
      else if (!strcmp(argv[formula_index], "-TA"))
        {
          ta_opt = true;
        }
      else if (!strcmp(argv[formula_index], "-TGTA"))
        {
          tgta_opt = true;
        }
      else if (!strcmp(argv[formula_index], "-lv"))
        {
          opt_with_artificial_livelock = true;
        }
      else if (!strcmp(argv[formula_index], "-sp"))
        {
          opt_single_pass_emptiness_check = true;
        }
      else if (!strcmp(argv[formula_index], "-in"))
        {
          opt_with_artificial_initial_state = false;
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
		(static_cast<const spot::ltl::atomic_prop*>(env.require(tok)));
	      tok = strtok(0, ", \t;");
	    }
	}
      else if (!strncmp(argv[formula_index], "-u", 2))
	{
	  translation = TransCompo;
	  const char* c = argv[formula_index] + 2;
	  while (*c != 0)
	    {
	      switch (*c)
		{
		case '2':
		  cs_nowdba = false;
		  cs_wdba_smaller = true;
		  break;
		case 'w':
		  cs_nowdba = false;
		  cs_wdba_smaller = false;
		  break;
		case 's':
		  cs_nosimul = false;
		  break;
		case 'e':
		  cs_early_start = true;
		  break;
		case 'W':
		  cs_nowdba = true;
		  break;
		case 'S':
		  cs_nosimul = true;
		  break;
		case 'E':
		  cs_early_start = false;
		  break;
		case 'o':
		  cs_oblig = true;
		  break;
		case 'O':
		  cs_oblig = false;
		  break;
		default:
		  std::cerr << "Unknown suboption `" << *c
			    << "' for option -u" << std::endl;
		}
	      ++c;
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
	  readformat = ReadSpot;
	}
      else if (!strcmp(argv[formula_index], "-XD"))
	{
	  from_file = true;
	  readformat = ReadDstar;
	}
      else if (!strcmp(argv[formula_index], "-XDB"))
	{
	  from_file = true;
	  readformat = ReadDstar;
	  nra2nba = true;
	}
      else if (!strcmp(argv[formula_index], "-XDD"))
	{
	  from_file = true;
	  readformat = ReadDstar;
	  nra2nba = true;
	  dra2dba = true;
	}
      else if (!strcmp(argv[formula_index], "-XL"))
	{
	  from_file = true;
	  readformat = ReadLbtt;
	}
      else if (!strcmp(argv[formula_index], "-XN"))
	{
	  from_file = true;
	  readformat = ReadNeverclaim;
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

  const spot::ltl::formula* f = 0;
  if (!from_file) // Reading a formula, not reading an automaton from a file.
    {
      switch (translation)
	{
	case TransFM:
	case TransLaCIM:
	case TransTAA:
	case TransCompo:
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
      spot::tgba_bdd_concrete* concrete = 0;
      const spot::tgba* to_free = 0;
      const spot::tgba* to_free2 = 0;
      spot::tgba* a = 0;
      bool assume_sba = false;

      if (from_file)
	{
	  spot::tgba_explicit_string* e = 0;
	  switch (readformat)
	    {
	    case ReadSpot:
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
	      break;
	    case ReadNeverclaim:
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
		assume_sba = true;
	      }
	      break;
	    case ReadLbtt:
	      {
		std::string error;
		std::istream* in = &std::cin;
		std::fstream* f = 0;
		if (input != "-")
		  {
		    in = f = new std::fstream(input.c_str());
		    if (!*f)
		      {
			std::cerr << "cannot open " << input << std::endl;
			delete dict;
			return 2;
		      }
		  }
		tm.start("parsing lbtt");
		to_free = a =
		  const_cast<spot::tgba*>(spot::lbtt_parse(*in, error, dict,
							   env, env));
		tm.stop("parsing lbtt");
		delete f;
		if (!to_free)
		  {
		    std::cerr << error << std::endl;
		    delete dict;
		    return 2;
		  }
	      }
	      break;
	    case ReadDstar:
	      {
		spot::dstar_parse_error_list pel;
		tm.start("parsing dstar");
		spot::dstar_aut* daut;
		daut = spot::dstar_parse(input, pel, dict,
					 env, debug_opt);
		tm.stop("parsing dstar");
		if (spot::format_dstar_parse_errors(std::cerr, input, pel))
		  {
		    delete to_free;
		    delete dict;
		    return 2;
		  }
		tm.start("dstar2tgba");
		if (nra2nba)
		  {
		    if (daut->type == spot::Rabin)
		      {
			if (dra2dba)
			  to_free = a = spot::dstar_to_tgba(daut);
			else
			  to_free = a = spot::nra_to_nba(daut);
			assume_sba = true;
		      }
		    else
		      {
			to_free = a = spot::nsa_to_tgba(daut);
			assume_sba = false;
		      }
		  }
		else
		  {
		    to_free = a = daut->aut;
		    daut->aut = 0;
		    assume_sba = false;
		  }
		tm.stop("dstar2tgba");
		delete daut;
	      }
	      break;
	    }
	  if (e)
	    e->merge_transitions();

	}
      else
	{
	  spot::ltl::ltl_simplifier* simp = 0;
	  if (simpltl)
	    simp = new spot::ltl::ltl_simplifier(redopt, dict);

	  if (simp)
	    {
	      tm.start("reducing formula");
	      const spot::ltl::formula* t = simp->simplify(f);
	      f->destroy();
	      tm.stop("reducing formula");
	      f = t;
	      if (display_reduced_form)
		{
		  if (utf8_opt)
		    std::cout << spot::ltl::to_utf8_string(f) << std::endl;
		  else
		    std::cout << spot::ltl::to_string(f) << std::endl;
		}
	      // This helps ltl_to_tgba_fm() to order BDD variables in
	      // a more natural way.
	      simp->clear_as_bdd_cache();
	    }

	  if (f->is_psl_formula()
	      && !f->is_ltl_formula()
	      && (translation != TransFM && translation != TransCompo))
	    {
	      std::cerr << "Only the FM algorithm can translate PSL formulae;"
			<< " I'm using it for this formula." << std::endl;
	      translation = TransFM;
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
				       fm_red ? simp : 0);
	      break;
	    case TransCompo:
	      {
		a = spot::compsusp(f, dict,
				   cs_nowdba, cs_nosimul, cs_early_start,
				   false, cs_wdba_smaller, cs_oblig);
		break;
	      }
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

	  if (simp && simpcache_stats)
	    {
	      simp->print_stats(std::cerr);
	      bddStat s;
	      bdd_stats(&s);
	      std::cerr << "BDD produced: " << s.produced
			<< "\n    nodenum: " << s.nodenum
			<< "\n    maxnodenum: " << s.maxnodenum
			<< "\n    freenodes: " <<  s.freenodes
			<< "\n    minfreenodes: " << s.minfreenodes
			<< "\n    varnum: " <<  s.varnum
			<< "\n    cachesize: " << s.cachesize
			<< "\n    gbcnum: " << s.gbcnum
			<< std::endl;
	      bdd_fprintstat(stderr);
	      dict->dump(std::cerr);
	    }
	  delete simp;
	}

      if (opt_monitor && !scc_filter)
	{
	  if (dynamic_cast<const spot::tgba_bdd_concrete*>(a))
	    symbolic_scc_pruning = true;
	  else
	    scc_filter = true;
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
      if (scc_filter)
	{
	  tm.start("SCC-filter");
	  aut_scc = a = spot::scc_filter(a, scc_filter_all);
	  tm.stop("SCC-filter");
	  assume_sba = false;
	}

      const spot::tgba* degeneralized = 0;

      spot::tgba* minimized = 0;
      if (opt_minimize)
	{
	  tm.start("obligation minimization");
	  minimized = minimize_obligation(a, f, 0, reject_bigger);
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
              // When the minimization succeed, simulation is useless.
              reduction_dir_sim = false;
              reduction_rev_sim = false;
              reduction_iterated_dont_care_sim = false;
              reduction_dont_care_sim = false;
              reduction_iterated_sim = false;
	      assume_sba = true;
	    }
	}

      if (reduction_dir_sim && !reduction_iterated_sim)
        {
          tm.start("direct simulation");
          temp_dir_sim = spot::simulation(a);
          a = temp_dir_sim;
          tm.stop("direct simulation");
	  assume_sba = false;
        }

      if (reduction_rev_sim && !reduction_iterated_sim)
        {
          tm.start("reverse simulation");
          temp_rev_sim = spot::cosimulation(a);
          a = temp_rev_sim;
          tm.stop("reverse simulation");
	  assume_sba = false;
        }


      if (reduction_iterated_dont_care_sim)
        {
          tm.start("don't care iterated simulation");
          temp_dont_care_iterated_sim
            = spot::dont_care_iterated_simulations(a, limit_dont_care_sim);
          a = temp_dont_care_iterated_sim;
          tm.stop("don't care iterated simulation");
	  assume_sba = false;
        }

      if (reduction_iterated_sim)
        {
          tm.start("Reduction w/ iterated simulations");
          temp_iterated_sim = spot::iterated_simulations(a);
          a = temp_iterated_sim;
          tm.stop("Reduction w/ iterated simulations");
	  assume_sba = false;
        }

      if (scc_filter && (reduction_dir_sim || reduction_rev_sim))
	{
	  tm.start("SCC-filter post-sim");
	  delete aut_scc;
	  aut_scc = a = spot::scc_filter(a, scc_filter_all);
	  tm.stop("SCC-filter post-sim");
	}

      if (reduction_dont_care_sim)
        {
          tm.start("don't care simulation");
          temp_dont_care_sim
            = spot::dont_care_simulation(a, limit_dont_care_sim);
          a = temp_dont_care_sim;
          tm.stop("don't care simulation");

	  if (scc_filter)
	    {
	      tm.start("SCC-filter on don't care");
	      a = spot::scc_filter(a, true);
	      delete temp_dont_care_sim;
	      temp_dont_care_sim = a;
	      tm.stop("SCC-filter on don't care");
	    }
	  assume_sba = false;
        }

      unsigned int n_acc = a->number_of_acceptance_conditions();
      if (echeck_inst
	  && degeneralize_opt == NoDegen
	  && n_acc > 1
	  && echeck_inst->max_acceptance_conditions() < n_acc)
	{
	  degeneralize_opt = DegenTBA;
	  assume_sba = false;
	}

      if (!assume_sba && !opt_monitor)
	{
	  if (degeneralize_opt == DegenTBA)
	    {
	      degeneralized = a = new spot::tgba_tba_proxy(a);
	    }
	  else if (degeneralize_opt == DegenSBA)
	    {
	      tm.start("degeneralization");
	      degeneralized = a = spot::degeneralize(a,
						     degen_reset,
						     degen_order,
						     degen_cache);
	      tm.stop("degeneralization");
	      assume_sba = true;
	    }
	  else if (labeling_opt == StateLabeled)
	    {
	      degeneralized = a = new spot::tgba_sgba_proxy(a);
	    }
	}

      spot::tgba* determinized = 0;
      if (opt_determinize && a->number_of_acceptance_conditions() <= 1
	  && (!f || f->is_syntactic_recurrence()))
	{
	  tm.start("determinization 2");
	  determinized = tba_determinize(a, 0, opt_determinize_threshold);
	  tm.stop("determinization 2");
	  if (determinized)
	    a = determinized;
	}

      const spot::tgba* monitor = 0;
      if (opt_monitor)
	{
	  tm.start("Monitor minimization");
	  monitor = a = minimize_monitor(a);
	  tm.stop("Monitor minimization");
	  assume_sba = false; 	// All states are accepting, so double
				// circles in the dot output are
				// pointless.
	}

      if (degeneralized || determinized)
	{
	  if (reduction_dir_sim && !reduction_iterated_sim)
	    {
	      tm.start("direct simulation 2");
	      spot::tgba* tmp = spot::simulation(a);
	      delete temp_dir_sim;
	      a = temp_dir_sim = tmp;
	      tm.stop("direct simulation 2");
	      assume_sba = false;
	    }

	  if (reduction_rev_sim && !reduction_iterated_sim)
	    {
	      tm.start("reverse simulation 2");
	      spot::tgba* tmp = spot::cosimulation(a);
	      delete temp_rev_sim;
	      a = temp_rev_sim = tmp;
	      tm.stop("reverse simulation 2");
	      assume_sba = false;
	    }

	  if (reduction_iterated_sim)
	    {
	      tm.start("Reduction w/ iterated simulations");
	      spot::tgba* tmp = spot::iterated_simulations(a);
	      delete temp_iterated_sim;
	      a = temp_iterated_sim = tmp;
	      tm.stop("Reduction w/ iterated simulations");
	      assume_sba = false;
	    }
	}

      spot::tgba* completed = 0;
      if (opt_complete)
	{
	  tm.start("determinization");
	  a = completed = tgba_complete(a);
	  tm.stop("determinization");
	}

      spot::tgba* satminimized = 0;
      if (opt_dtbasat >= 0)
	{
	  tm.start("dtbasat");
	  satminimized = dtba_sat_synthetize(a, opt_dtbasat);
	  tm.stop("dtbasat");
	  if (satminimized)
	    a = satminimized;
	}
      else if (opt_dtgbasat >= 0)
	{
	  tm.start("dtgbasat");
	  satminimized = dtgba_sat_minimize(a, opt_dtgbasat);
	  tm.stop("dtgbasat");
	  if (satminimized)
	    a = satminimized;
	}

      spot::tgba* complemented = 0;
      if (opt_dtgbacomp)
	{
	  tm.start("DTGBA complement");
	  a = complemented = dtgba_complement(a);
	  tm.stop("DTGBA complement");
	}

      if (complemented || satminimized || determinized)
	{
	  if (scc_filter && (reduction_dir_sim || reduction_rev_sim))
	    {
	      tm.start("SCC-filter post-sim");
	      delete aut_scc;
	      // Do not filter_all for SBA
	      aut_scc = a = spot::scc_filter(a, assume_sba ?
					     false : scc_filter_all);
	      tm.stop("SCC-filter post-sim");
	    }
	}

      if (opt_monitor)
	{
	  tm.start("Monitor minimization");
	  minimized = a = minimize_monitor(a);
	  tm.stop("Monitor minimization");
	  assume_sba = false; 	// All states are accepting, so double
				// circles in the dot output are
				// pointless.
	}

      const spot::tgba* expl = 0;
      switch (dupexp)
	{
	case NoneDup:
	  break;
	case BFS:
	  expl = a = tgba_dupexp_bfs(a);
	  break;
	case DFS:
	  expl = a = tgba_dupexp_dfs(a);
	  break;
	}

      //TA, STA, GTA, SGTA and TGTA
      if (ta_opt || tgta_opt)
        {
	  bdd atomic_props_set_bdd = atomic_prop_collect_as_bdd(f, a);

          if (ta_opt)
            {
	      tm.start("conversion to TA");
              spot::ta* testing_automaton
                  = tgba_to_ta(a, atomic_props_set_bdd, degeneralize_opt
                      == DegenSBA, opt_with_artificial_initial_state,
                      opt_single_pass_emptiness_check,
                      opt_with_artificial_livelock);
	      tm.stop("conversion to TA");

              spot::ta* testing_automaton_nm = 0;
              if (opt_bisim_ta)
                {
		  tm.start("TA bisimulation");
                  testing_automaton_nm = testing_automaton;
                  testing_automaton = minimize_ta(testing_automaton);
		  tm.stop("TA bisimulation");
                }

              if (output != -1)
                {
                  tm.start("producing output");
                  switch (output)
                    {
		    case 0:
		      spot::dotty_reachable(std::cout, testing_automaton);
		      break;
		    case 12:
		      stats_reachable(testing_automaton).dump(std::cout);
		      break;
		    default:
		      std::cerr << "unsupported output option" << std::endl;
		      exit(1);
                    }
                  tm.stop("producing output");
                }

              delete testing_automaton_nm;
              delete testing_automaton;
	      delete a;
              a = 0;
              degeneralized = 0;
              if (degeneralize_opt != DegenSBA)
                to_free = 0;
              output = -1;
            }
          if (tgta_opt)
            {
              spot::tgta_explicit* tgta = tgba_to_tgta(a, atomic_props_set_bdd);
              if (opt_bisim_ta)
                {
		  tm.start("TA bisimulation");
                  a = minimize_tgta(tgta);
		  tm.stop("TA bisimulation");
		  delete tgta;
                }
              else
                {
                  a = tgta;
                }
	      to_free2 = a;

              if (output != -1)
                {
                  tm.start("producing output");
                  switch (output)
                    {
		    case 0:
		      spot::dotty_reachable(std::cout,
			    dynamic_cast<spot::tgta_explicit*>(a)->get_ta());
		      break;
		    case 12:
		      stats_reachable(a).dump(std::cout);
		      break;
		    default:
		      std::cerr << "unsupported output option" << std::endl;
		      exit(1);
                    }
                  tm.stop("producing output");
                }
	      output = -1;
            }
        }

      spot::tgba* product_degeneralized = 0;

      if (system_aut)
        {
	  product_to_free = a = new spot::tgba_product(system_aut, a);

	  assume_sba = false;

	  unsigned int n_acc = a->number_of_acceptance_conditions();
	  if (echeck_inst
	      && degeneralize_opt == NoDegen
	      && n_acc > 1
	      && echeck_inst->max_acceptance_conditions() < n_acc)
            degeneralize_opt = DegenTBA;
          if (degeneralize_opt == DegenTBA)
	    {
	      product_degeneralized = a = new spot::tgba_tba_proxy(a);
	    }
          else if (degeneralize_opt == DegenSBA)
	    {
	      tm.start("degeneralize product");
	      product_degeneralized = a =
		spot::degeneralize(a,
				   degen_reset,
				   degen_order,
				   degen_cache);
	      tm.stop("degeneralize product");
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

      if (utf8_opt)
	{
	  if (spot::tgba_explicit_formula* tef =
	      dynamic_cast<spot::tgba_explicit_formula*>(a))
	    tef->enable_utf8();
	  else if (spot::sba_explicit_formula* sef =
		   dynamic_cast<spot::sba_explicit_formula*>(a))
	    sef->enable_utf8();
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
                    spot::unique_ptr<spot::tgba>
		      s(spot::degeneralize(a,
					   degen_reset,
					   degen_order,
					   degen_cache));
		    spot::never_claim_reachable(std::cout, s, f, spin_comments);
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
	      std::cout << "nondeterministic states: "
			<< count_nondet_states(a) << std::endl;
	      break;
	    case 14:
	      if (minimized == 0)
		{
		  std::cout << "this is not an obligation property";
		  const spot::tgba* tmp =
		    tba_determinize_check(a, 0, opt_o_threshold, f, 0);
		  if (tmp != 0 && tmp != a)
		    {
		      std::cout << ", but it is a recurrence property";
		      delete tmp;
		    }
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
	    case 15:
	      {
		spot::scc_map m(a);
		m.build_map();
		spot::enumerate_cycles c(m);
		unsigned max = m.scc_count();
		for (unsigned n = 0; n < max; ++n)
		  {
		    std::cout << "Cycles in SCC #" << n << std::endl;
		    c.run(n);
		  }
		break;
	      }
	    case 16:
	      {
		spot::scc_map m(a);
		m.build_map();
		unsigned max = m.scc_count();
		for (unsigned n = 0; n < max; ++n)
		  {
		    bool w = spot::is_weak_scc(m, n);
		    std::cout << "SCC #" << n
			      << (w ? " is weak" : " is not weak")
			      << std::endl;
		  }
		break;
	      }
	    case 17:
	      {
		hoaf_reachable(std::cout, a, hoaf_opt, f) << '\n';
		break;
	      }
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
                                  spot::unique_ptr<spot::tgba>
                                    ar(spot::tgba_run_to_tgba(a, run));
				  spot::dotty_reachable(std::cout, ar, false);
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

      delete to_free2;
      if (show_fc)
	delete a;
      if (f)
        f->destroy();
      delete product_degeneralized;
      delete product_to_free;
      delete system_aut;
      delete expl;
      delete monitor;
      delete minimized;
      delete satminimized;
      delete degeneralized;
      delete determinized;
      delete completed;
      delete complemented;
      delete aut_scc;
      delete to_free;
      delete echeck_inst;
      delete temp_dir_sim;
      delete temp_rev_sim;
      delete temp_iterated_sim;
      delete temp_dont_care_sim;
      delete temp_dont_care_iterated_sim;
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
