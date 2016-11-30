// -*- coding: utf-8 -*-
// Copyright (C) 2012 Laboratoire de Recherche et Développement de
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

#include "common_sys.hh"

#include <string>
#include <iostream>

#include <argp.h>
#include "error.h"

#include "common_setup.hh"
#include "common_r.hh"
#include "common_cout.hh"
#include "common_finput.hh"
#include "common_post.hh"

#include "ltlvisit/simplify.hh"
#include "tgbaalgos/dotty.hh"
#include "tgbaalgos/lbtt.hh"
#include "tgbaalgos/ltl2tgba_fm.hh"
#include "tgbaalgos/neverclaim.hh"
#include "tgbaalgos/save.hh"
#include "tgbaalgos/stats.hh"
#include "tgba/bddprint.hh"

const char argp_program_doc[] ="\
Translate linear-time formulas (LTL/PSL) into Büchi automata.\n\n\
By default it will apply all available optimizations to output \
the smallest Transition-based Generalized Büchi Automata, \
in GraphViz's format.\n\
If multiple formulas are supplied, several automata will be output.";

#define OPT_TGBA 1
#define OPT_DOT 2
#define OPT_LBTT 3
#define OPT_SPOT 4
#define OPT_STATS 5

static const argp_option options[] =
  {
    /**************************************************/
    { 0, 0, 0, 0, "Output automaton type:", 2 },
    { "tgba", OPT_TGBA, 0, 0,
      "Transition-based Generalized Büchi Automaton (default)", 0 },
    { "ba", 'B', 0, 0, "Büchi Automaton", 0 },
    { "monitor", 'M', 0, 0, "Monitor (accepts all finite prefixes "
      "of the given formula)", 0 },
    /**************************************************/
    { 0, 0, 0, 0, "Output format:", 3 },
    { "dot", OPT_DOT, 0, 0, "GraphViz's format (default)", 0 },
    { "lbtt", OPT_LBTT, 0, 0, "LBTT's format", 0 },
    { "spin", 's', 0, 0, "Spin neverclaim (implies --ba)", 0 },
    { "spot", OPT_SPOT, 0, 0, "SPOT's format", 0 },
    { "utf8", '8', 0, 0, "enable UTF-8 characters in output "
      "(ignored with --lbtt or --spin)", 0 },
    { "stats", OPT_STATS, "FORMAT", 0,
      "output statistics about the automaton", 0 },
    /**************************************************/
    { 0, 0, 0, 0, "The FORMAT string passed to --stats may use "\
      "the following interpreted sequences:", 4 },
    { "%f", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "the formula, in Spot's syntax", 0 },
    { "%s", 0, 0, OPTION_DOC | OPTION_NO_USAGE, "number of states", 0 },
    { "%e", 0, 0, OPTION_DOC | OPTION_NO_USAGE,	"number of edges", 0 },
    { "%t", 0, 0, OPTION_DOC | OPTION_NO_USAGE,	"number of transitions", 0 },
    { "%a", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "number of acceptance sets", 0 },
    { "%S", 0, 0, OPTION_DOC | OPTION_NO_USAGE,	"number of SCCs", 0 },
    { "%n", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "number of nondeterministic states", 0 },
    { "%d", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "1 if the automaton is deterministic, 0 otherwise", 0 },
    { "%%", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "a single %", 0 },
    /**************************************************/
    { 0, 0, 0, 0, "Miscellaneous options:", -1 },
    { 0, 0, 0, 0, 0, 0 }
  };

const struct argp_child children[] =
  {
    { &finput_argp, 0, 0, 1 },
    { &post_argp, 0, 0, 20 },
    { &misc_argp, 0, 0, -1 },
    { 0, 0, 0, 0 }
  };

enum output_format { Dot, Lbtt, Spin, Spot, Stats } format = Dot;
bool utf8 = false;
const char* stats = "";

static int
parse_opt(int key, char* arg, struct argp_state*)
{
  // This switch is alphabetically-ordered.
  switch (key)
    {
    case '8':
      spot::enable_utf8();
      break;
    case 'B':
      type = spot::postprocessor::BA;
      break;
    case 'M':
      type = spot::postprocessor::Monitor;
      break;
    case 's':
      format = Spin;
      if (type != spot::postprocessor::Monitor)
	type = spot::postprocessor::BA;
      break;
    case OPT_DOT:
      format = Dot;
      break;
    case OPT_LBTT:
      format = Lbtt;
      break;
    case OPT_SPOT:
      format = Spot;
      break;
    case OPT_STATS:
      if (!*arg)
	error(2, 0, "empty format string for --stats");
      stats = arg;
      format = Stats;
      break;
    case OPT_TGBA:
      if (format == Spin)
	error(2, 0, "--spin and --tgba are incompatible");
      type = spot::postprocessor::TGBA;
      break;
    case ARGP_KEY_ARG:
      // FIXME: use stat() to distinguish filename from string?
      jobs.push_back(job(arg, false));
      break;

    default:
      return ARGP_ERR_UNKNOWN;
    }
  return 0;
}


namespace
{
  class trans_processor: public job_processor
  {
  public:
    spot::ltl::ltl_simplifier& simpl;
    spot::postprocessor& post;
    spot::stat_printer statistics;

    trans_processor(spot::ltl::ltl_simplifier& simpl,
		    spot::postprocessor& post)
      : simpl(simpl), post(post), statistics(std::cout, stats)
    {
    }

    int
    process_formula(const spot::ltl::formula* f,
		    const char* filename = 0, int linenum = 0)
    {
      const spot::ltl::formula* res = simpl.simplify(f);
      f->destroy();
      f = res;
      // This helps ltl_to_tgba_fm() to order BDD variables in a more
      // natural way (improving the degeneralization).
      simpl.clear_as_bdd_cache();

      // This should not happen, because the parser we use can only
      // read PSL/LTL formula, but since our ltl::formula* type can
      // represent more than PSL formula, let's make this
      // future-proof.
      if (!f->is_psl_formula())
	{
	  std::string s = spot::ltl::to_string(f);
	  error_at_line(2, 0, filename, linenum,
			"formula '%s' is not an LTL or PSL formula",
			s.c_str());
	}

      bool exprop = level == spot::postprocessor::High;
      const spot::tgba* aut = ltl_to_tgba_fm(f, simpl.get_dict(), exprop);
      aut = post.run(aut, f);

      if (utf8)
	{
	  spot::tgba* a = const_cast<spot::tgba*>(aut);
	  if (spot::tgba_explicit_formula* tef =
	      dynamic_cast<spot::tgba_explicit_formula*>(a))
	    tef->enable_utf8();
	  else if (spot::sba_explicit_formula* sef =
		   dynamic_cast<spot::sba_explicit_formula*>(a))
	    sef->enable_utf8();
	}

      switch (format)
	{
	case Dot:
	  spot::dotty_reachable(std::cout, aut,
				(type == spot::postprocessor::BA)
				|| (type == spot::postprocessor::Monitor));
	  break;
	case Lbtt:
	  spot::lbtt_reachable(std::cout, aut);
	  break;
	case Spot:
	  spot::tgba_save_reachable(std::cout, aut);
	  break;
	case Spin:
	  spot::never_claim_reachable(std::cout, aut, f);
	  break;
	case Stats:
	  statistics.print(aut, f) << "\n";
	  break;
	}
      delete aut;
      f->destroy();
      flush_cout();
      return 0;
    }
  };
}

int
main(int argc, char** argv)
{
  setup(argv);

  const argp ap = { options, parse_opt, "[FORMULA...]",
		    argp_program_doc, children, 0, 0 };

  if (int err = argp_parse(&ap, argc, argv, ARGP_NO_HELP, 0, 0))
    exit(err);

  if (jobs.empty())
    error(2, 0, "No formula to translate?  Run '%s --help' for usage.",
	  program_name);

  spot::ltl::ltl_simplifier simpl(simplifier_options());

  spot::postprocessor postproc;
  postproc.set_pref(pref);
  postproc.set_type(type);
  postproc.set_level(level);

  trans_processor processor(simpl, postproc);
  if (processor.run())
    return 2;
  return 0;
}
