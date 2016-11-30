// -*- coding: utf-8 -*-
// Copyright (C) 2013, 2014 Laboratoire de Recherche et Développement
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

#include "common_sys.hh"

#include <string>
#include <iostream>

#include <argp.h>
#include "error.h"
#include "gethrxtime.h"

#include "common_setup.hh"
#include "common_finput.hh"
#include "common_cout.hh"
#include "common_post.hh"

#include "ltlast/formula.hh"
#include "tgba/tgbaexplicit.hh"
#include "tgbaalgos/dotty.hh"
#include "tgbaalgos/lbtt.hh"
#include "tgbaalgos/hoaf.hh"
#include "tgbaalgos/neverclaim.hh"
#include "tgbaalgos/save.hh"
#include "tgbaalgos/stats.hh"
#include "tgba/bddprint.hh"
#include "misc/optionmap.hh"
#include "dstarparse/public.hh"
#include "tgbaalgos/scc.hh"

const char argp_program_doc[] ="\
Convert Rabin and Streett automata into Büchi automata.\n\n\
This reads the output format of ltl2dstar and will output a \n\
Transition-based Generalized Büchi Automata in GraphViz's format by default.\n\
If multiple files are supplied (one automaton per file), several automata\n\
will be output.";

#define OPT_TGBA 1
#define OPT_DOT 2
#define OPT_LBTT 3
#define OPT_SPOT 4
#define OPT_STATS 5

static const argp_option options[] =
  {
    /**************************************************/
    { 0, 0, 0, 0, "Input:", 1 },
    { "file", 'F', "FILENAME", 0,
      "process the automaton in FILENAME", 0 },
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
    { "hoaf", 'H', "s|t|m|l", OPTION_ARG_OPTIONAL,
      "Output the automaton in HOA format.  Add letters to select "
      "(s) state-based acceptance, (t) transition-based acceptance, "
      "(m) mixed acceptance, (l) single-line output", 0 },
    { "lbtt", OPT_LBTT, "t", OPTION_ARG_OPTIONAL,
      "LBTT's format (add =t to force transition-based acceptance even"
      " on Büchi automata)", 0 },
    { "spin", 's', 0, 0, "Spin neverclaim (implies --ba)", 0 },
    { "spot", OPT_SPOT, 0, 0, "SPOT's format", 0 },
    { "utf8", '8', 0, 0, "enable UTF-8 characters in output "
      "(ignored with --lbtt or --spin)", 0 },
    { "stats", OPT_STATS, "FORMAT", 0,
      "output statistics about the automaton", 0 },
    /**************************************************/
    { 0, 0, 0, 0, "The FORMAT string passed to --stats may use "\
      "the following interpreted sequences (capitals for input,"
      " minuscules for output):", 4 },
    { "%F", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "name of the input file", 0 },
    { "%S, %s", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "number of states", 0 },
    { "%E, %e", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "number of edges", 0 },
    { "%T, %t", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "number of transitions", 0 },
    { "%A, %a", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "number of acceptance pairs or sets", 0 },
    { "%C, %c", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "number of SCCs", 0 },
    { "%n", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "number of nondeterministic states in output", 0 },
    { "%d", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "1 if the output is deterministic, 0 otherwise", 0 },
    { "%p", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "1 if the output is complete, 0 otherwise", 0 },
    { "%r", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "conversion time (including post-processings, but not parsing)"
      " in seconds", 0 },
    { "%%", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "a single %", 0 },
    /**************************************************/
    { 0, 0, 0, 0, "Miscellaneous options:", -1 },
    { "extra-options", 'x', "OPTS", 0,
      "fine-tuning options (see spot-x (7))", 0 },
    { 0, 0, 0, 0, 0, 0 }
  };

const struct argp_child children[] =
  {
    { &post_argp, 0, 0, 20 },
    { &misc_argp, 0, 0, -1 },
    { 0, 0, 0, 0 }
  };

enum output_format { Dot, Lbtt, Lbtt_t, Spin, Spot, Stats, Hoa } format = Dot;
bool utf8 = false;
const char* stats = "";
const char* hoaf_opt = 0;
spot::option_map extra_options;

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
    case 'F':
      jobs.push_back(job(arg, true));
      break;
    case 'H':
      format = Hoa;
      hoaf_opt = arg;
      break;
    case 'M':
      type = spot::postprocessor::Monitor;
      break;
    case 's':
      format = Spin;
      if (type != spot::postprocessor::Monitor)
	type = spot::postprocessor::BA;
      break;
    case 'x':
      {
	const char* opt = extra_options.parse_options(arg);
	if (opt)
	  error(2, 0, "failed to parse --options near '%s'", opt);
      }
      break;
    case OPT_DOT:
      format = Dot;
      break;
    case OPT_LBTT:
      if (arg)
	{
	  if (arg[0] == 't' && arg[1] == 0)
	    format = Lbtt_t;
	  else
	    error(2, 0, "unknown argument for --lbtt: '%s'", arg);
	}
      else
	{
	  format = Lbtt;
	}
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
      jobs.push_back(job(arg, true));
      break;

    default:
      return ARGP_ERR_UNKNOWN;
    }
  return 0;
}


namespace
{
  /// \brief prints various statistics about a TGBA
  ///
  /// This object can be configured to display various statistics
  /// about a TGBA.  Some %-sequence of characters are interpreted in
  /// the format string, and replaced by the corresponding statistics.
  class dstar_stat_printer: protected spot::stat_printer
  {
  public:
    dstar_stat_printer(std::ostream& os, const char* format)
      : spot::stat_printer(os, format)
    {
      declare('A', &daut_acc_);
      declare('C', &daut_scc_);
      declare('E', &daut_edges_);
      declare('F', &filename_);
      declare('f', &filename_);	// Override the formula printer.
      declare('S', &daut_states_);
      declare('T', &daut_trans_);
    }

    /// \brief print the configured statistics.
    ///
    /// The \a f argument is not needed if the Formula does not need
    /// to be output.
    std::ostream&
    print(const spot::dstar_aut* daut, const spot::tgba* aut,
	  const char* filename, double run_time)
    {
      filename_ = filename;

      if (has('T'))
	{
	  spot::tgba_sub_statistics s = sub_stats_reachable(daut->aut);
	  daut_states_ = s.states;
	  daut_edges_ = s.transitions;
	  daut_trans_ = s.sub_transitions;
	}
      else if (has('E'))
	{
	  spot::tgba_sub_statistics s = sub_stats_reachable(daut->aut);
	  daut_states_ = s.states;
	  daut_edges_ = s.transitions;
	}
      else if (has('S'))
	{
	  daut_states_ = daut->aut->num_states();
	}

      if (has('A'))
	daut_acc_ = daut->accpair_count;

      if (has('C'))
	{
	  spot::scc_map m(daut->aut);
	  m.build_map();
	  daut_scc_ = m.scc_count();
	}

      return this->spot::stat_printer::print(aut, 0, run_time);
    }

  private:
    spot::printable_value<const char*> filename_;
    spot::printable_value<unsigned> daut_states_;
    spot::printable_value<unsigned> daut_edges_;
    spot::printable_value<unsigned> daut_trans_;
    spot::printable_value<unsigned> daut_acc_;
    spot::printable_value<unsigned> daut_scc_;
  };


  class dstar_processor: public job_processor
  {
  public:
    spot::postprocessor& post;
    dstar_stat_printer statistics;

    dstar_processor(spot::postprocessor& post)
      : post(post), statistics(std::cout, stats)
    {
    }

    int
    process_formula(const spot::ltl::formula*, const char*, int)
    {
      assert(!"should not happen");
      return 0;
    }


    int
    process_file(const char* filename)
    {
      spot::dstar_parse_error_list pel;
      spot::dstar_aut* daut;
      spot::bdd_dict dict;
      daut = spot::dstar_parse(filename, pel, &dict);
      if (spot::format_dstar_parse_errors(std::cerr, filename, pel))
	{
	  delete daut;
	  return 2;
	}
      if (!daut)
	{
	  error(2, 0, "failed to read automaton from %s", filename);
	}

      const xtime_t before = gethrxtime();

      spot::tgba* nba = spot::dstar_to_tgba(daut);
      const spot::tgba* aut = post.run(nba, 0);

      const xtime_t after = gethrxtime();
      const double prec = XTIME_PRECISION;
      const double conversion_time = (after - before) / prec;

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
	  spot::lbtt_reachable(std::cout, aut, type == spot::postprocessor::BA);
	  break;
	case Lbtt_t:
	  spot::lbtt_reachable(std::cout, aut, false);
	  break;
	case Hoa:
	  spot::hoaf_reachable(std::cout, aut, hoaf_opt) << '\n';
	  break;
	case Spot:
	  spot::tgba_save_reachable(std::cout, aut);
	  break;
	case Spin:
	  spot::never_claim_reachable(std::cout, aut);
	  break;
	case Stats:
	  statistics.print(daut, aut, filename, conversion_time) << "\n";
	  break;
	}
      delete aut;
      delete daut;
      flush_cout();
      return 0;
    }
  };
}

int
main(int argc, char** argv)
{
  setup(argv);

  const argp ap = { options, parse_opt, "[FILENAMES...]",
		    argp_program_doc, children, 0, 0 };

  if (int err = argp_parse(&ap, argc, argv, ARGP_NO_HELP, 0, 0))
    exit(err);

  if (jobs.empty())
    jobs.push_back(job("-", true));

  spot::postprocessor post(&extra_options);
  post.set_pref(pref | comp);
  post.set_type(type);
  post.set_level(level);

  dstar_processor processor(post);
  if (processor.run())
    return 2;
  return 0;
}
