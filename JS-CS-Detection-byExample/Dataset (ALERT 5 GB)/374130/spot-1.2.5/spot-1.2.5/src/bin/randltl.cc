// -*- coding: utf-8 -*-
// Copyright (C) 2012, 2013, 2014 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
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

#include <iostream>
#include <fstream>
#include <argp.h>
#include <cstdlib>
#include <iterator>
#include "error.h"

#include "common_setup.hh"
#include "common_output.hh"
#include "common_range.hh"
#include "common_r.hh"

#include <sstream>
#include "ltlast/atomic_prop.hh"
#include "ltlast/multop.hh"
#include "ltlast/unop.hh"
#include "ltlvisit/randomltl.hh"
#include "ltlvisit/tostring.hh"
#include "ltlvisit/length.hh"
#include "ltlvisit/simplify.hh"
#include "ltlenv/defaultenv.hh"
#include "misc/random.hh"
#include "misc/hash.hh"

const char argp_program_doc[] ="\
Generate random temporal logic formulas.\n\n\
The formulas are built over the atomic propositions named by PROPS...\n\
or, if N is a nonnegative number, using N arbitrary names.\v\
Examples:\n\
\n\
The following generates 10 random LTL formulas over the propositions a, b,\n\
and c, with the default tree-size, and all available operators.\n\
  % randltl -n10 a b c\n\
\n\
If you do not mind about the name of the atomic propositions, just give\n\
a number instead:\n\
  % ./randltl -n10 3\n\
\n\
You can disable or favor certain operators by changing their priority.\n\
The following disables xor, implies, and equiv, and multiply the probability\n\
of X to occur by 10.\n\
  % ./randltl --ltl-priorities='xor=0, implies=0, equiv=0, X=10' -n10 a b c\n\
";

#define OPT_DUMP_PRIORITIES 1
#define OPT_LTL_PRIORITIES 2
#define OPT_SERE_PRIORITIES 3
#define OPT_PSL_PRIORITIES 4
#define OPT_BOOLEAN_PRIORITIES 5
#define OPT_SEED 6
#define OPT_TREE_SIZE 7
#define OPT_WF 8
#define OPT_DUPS 9

static const argp_option options[] =
  {
    // Keep this alphabetically sorted (expect for aliases).
    /**************************************************/
    { 0, 0, 0, 0, "Type of formula to generate:", 1 },
    { "boolean", 'B', 0, 0, "generate Boolean formulas", 0 },
    { "ltl", 'L', 0, 0, "generate LTL formulas (default)", 0 },
    { "sere", 'S', 0, 0, "generate SERE", 0 },
    { "psl", 'P', 0, 0, "generate PSL formulas", 0 },
    /**************************************************/
    { 0, 0, 0, 0, "Generation:", 2 },
    { "weak-fairness", OPT_WF, 0, 0,
      "append some weak-fairness conditions", 0 },
    { "formulas", 'n', "INT", 0, "number of formulas to output (1)\n"\
      "use a negative value for unbounded generation", 0 },
    { "seed", OPT_SEED, "INT", 0,
      "seed for the random number generator (0)", 0 },
    { "tree-size", OPT_TREE_SIZE, "RANGE", 0,
      "tree size of the formulas generated, before mandatory "\
      "trivial simplifications (15)", 0 },
    { "allow-dups", OPT_DUPS, 0, 0,
      "allow duplicate formulas to be output", 0 },
    DECLARE_OPT_R,
    RANGE_DOC,
    LEVEL_DOC(3),
    /**************************************************/
    { 0, 0, 0, 0, "Adjusting probabilities:", 4 },
    { "dump-priorities", OPT_DUMP_PRIORITIES, 0, 0,
      "show current priorities, do not generate any formula", 0 },
    { "ltl-priorities", OPT_LTL_PRIORITIES, "STRING", 0,
      "set priorities for LTL formulas", 0 },
    { "sere-priorities", OPT_SERE_PRIORITIES, "STRING", 0,
      "set priorities for SERE formulas", 0 },
    { "boolean-priorities", OPT_BOOLEAN_PRIORITIES, "STRING", 0,
      "set priorities for Boolean formulas", 0 },
    { 0, 0, 0, 0, "STRING should be a comma-separated list of "
      "assignments, assigning integer priorities to the tokens "
      "listed by --dump-priorities.", 0 },
    /**************************************************/
    { 0, 0, 0, 0, "Output options:", -20 },
    { 0, 0, 0, 0, "The FORMAT string passed to --format may use "\
      "the following interpreted sequences:", -19 },
    { "%f", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "the formula (in the selected syntax)", 0 },
    { "%L", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "the (serial) number of the formula", 0 },
    { "%%", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "a single %", 0 },
    { 0, 0, 0, 0, "Miscellaneous options:", -1 },
    { 0, 0, 0, 0, 0, 0 }
  };


const struct argp_child children[] =
  {
    { &output_argp, 0, 0, -20 },
    { &misc_argp, 0, 0, -1 },
    { 0, 0, 0, 0 }
  };

static enum { OutputBool, OutputLTL, OutputSERE, OutputPSL }
  output = OutputLTL;
spot::ltl::atomic_prop_set aprops;
static char* opt_pL = 0;
static char* opt_pS = 0;
static char* opt_pB = 0;
static bool opt_dump_priorities = false;
static int opt_formulas = 1;
static int opt_seed = 0;
static range opt_tree_size = { 15, 15 };
static bool opt_unique = true;
static bool opt_wf = false;
static bool ap_count_given = false;

void
remove_some_props(spot::ltl::atomic_prop_set& s)
{
  // How many propositions to remove from s?
  // (We keep at least one.)
  size_t n = spot::mrand(s.size());

  while (n--)
    {
      spot::ltl::atomic_prop_set::iterator i = s.begin();
      std::advance(i, spot::mrand(s.size()));
      s.erase(i);
    }
}

// GF(p_1) & GF(p_2) & ... & GF(p_n)
const spot::ltl::formula*
GF_n(spot::ltl::atomic_prop_set& ap)
{
  const spot::ltl::formula* res = 0;
  spot::ltl::atomic_prop_set::const_iterator i;
  for (i = ap.begin(); i != ap.end(); ++i)
    {
      const spot::ltl::formula* f =
	spot::ltl::unop::instance(spot::ltl::unop::F, (*i)->clone());
      f = spot::ltl::unop::instance(spot::ltl::unop::G, f);
      if (res)
        res = spot::ltl::multop::instance(spot::ltl::multop::And, f, res);
      else
        res = f;
    }
  return res;
}

static int
to_int(const char* s)
{
  char* endptr;
  int res = strtol(s, &endptr, 10);
  if (*endptr)
    error(2, 0, "failed to parse '%s' as an integer.", s);
  return res;
}

static int
parse_opt(int key, char* arg, struct argp_state* as)
{
  // This switch is alphabetically-ordered.
  switch (key)
    {
    case 'B':
      output = OutputBool;
      break;
    case 'L':
      output = OutputLTL;
      break;
    case 'n':
      opt_formulas = to_int(arg);
      break;
    case 'P':
      output = OutputPSL;
      break;
    case OPT_R:
      parse_r(arg);
      break;
    case 'S':
      output = OutputSERE;
      break;
    case OPT_BOOLEAN_PRIORITIES:
      opt_pB = arg;
      break;
    case OPT_DUPS:
      opt_unique = false;
      break;
    case OPT_LTL_PRIORITIES:
      opt_pL = arg;
      break;
    case OPT_DUMP_PRIORITIES:
      opt_dump_priorities = true;
      break;
      // case OPT_PSL_PRIORITIES: break;
    case OPT_SERE_PRIORITIES:
      opt_pS = arg;
      break;
    case OPT_SEED:
      opt_seed = to_int(arg);
      break;
    case OPT_TREE_SIZE:
      opt_tree_size = parse_range(arg);
      if (opt_tree_size.min > opt_tree_size.max)
	std::swap(opt_tree_size.min, opt_tree_size.max);
      break;
    case OPT_WF:
      opt_wf = true;
      break;
    case ARGP_KEY_ARG:
      // If this is the unique non-option argument, it can
      // be a number of atomic propositions to build.
      //
      // argp reorganizes argv[] so that options always come before
      // non-options.  So if as->argc == as->next we know this is the
      // last non-option argument, and if aprops.empty() we know this
      // is the also the first one.
      if (aprops.empty() && as->argc == as->next)
	{
	  char* endptr;
	  int res = strtol(arg, &endptr, 10);
	  if (!*endptr && res >= 0) // arg is a number
	    {
	      ap_count_given = true;
	      spot::ltl::default_environment& e =
		spot::ltl::default_environment::instance();
	      for (int i = 0; i < res; ++i)
		{
		  std::ostringstream p;
		  p << 'p' << i;
		  aprops.insert(static_cast<const spot::ltl::atomic_prop*>
				(e.require(p.str())));
		}
	      break;
	    }
	}
      aprops.insert(static_cast<const spot::ltl::atomic_prop*>
		    (spot::ltl::default_environment::instance().require(arg)));
      break;
    default:
      return ARGP_ERR_UNKNOWN;
    }
  return 0;
}

int
main(int argc, char** argv)
{
  setup(argv);

  const argp ap = { options, parse_opt, "N|PROP...", argp_program_doc,
		    children, 0, 0 };

  if (int err = argp_parse(&ap, argc, argv, ARGP_NO_HELP, 0, 0))
    exit(err);

  spot::ltl::random_formula* rf = 0;
  spot::ltl::random_psl* rp = 0;
  spot::ltl::random_sere* rs = 0;
  const char* tok_pL = 0;
  const char* tok_pS = 0;
  const char* tok_pB = 0;

  switch (output)
    {
    case OutputLTL:
      rf = new spot::ltl::random_ltl(&aprops);
      tok_pL = rf->parse_options(opt_pL);
      if (opt_pS)
	error(2, 0, "option --sere-priorities unsupported for LTL output");
      if (opt_pB)
	error(2, 0, "option --boolean-priorities unsupported for LTL output");
      break;
    case OutputBool:
      rf = new spot::ltl::random_boolean(&aprops);
      tok_pB = rf->parse_options(opt_pB);
      if (opt_pL)
	error(2, 0, "option --ltl-priorities unsupported for Boolean output");
      if (opt_pS)
	error(2, 0, "option --sere-priorities unsupported for Boolean output");
      break;
    case OutputSERE:
      rf = rs = new spot::ltl::random_sere(&aprops);
      tok_pS = rs->parse_options(opt_pS);
      tok_pB = rs->rb.parse_options(opt_pB);
      if (opt_pL)
	error(2, 0, "option --ltl-priorities unsupported for SERE output");
      break;
    case OutputPSL:
      rf = rp = new spot::ltl::random_psl(&aprops);
      rs = &rp->rs;
      tok_pL = rp->parse_options(opt_pL);
      tok_pS = rs->parse_options(opt_pS);
      tok_pB = rs->rb.parse_options(opt_pB);
      break;
    }

  if (tok_pL)
    error(2, 0, "failed to parse LTL priorities near '%s'", tok_pL);
  if (tok_pS)
    error(2, 0, "failed to parse SERE priorities near '%s'", tok_pS);
  if (tok_pB)
    error(2, 0, "failed to parse Boolean priorities near '%s'", tok_pB);

  if (opt_dump_priorities)
    {
      switch (output)
	{
	case OutputLTL:
	  std::cout
	    << "Use --ltl-priorities to set the following LTL priorities:\n";
	  rf->dump_priorities(std::cout);
	  break;
	case OutputBool:
	  std::cout
	    << ("Use --boolean-priorities to set the following Boolean "
		"formula priorities:\n");
	  rf->dump_priorities(std::cout);
	  break;
	case OutputPSL:
	  std::cout
	    << "Use --ltl-priorities to set the following LTL priorities:\n";
	  rp->dump_priorities(std::cout);
	  // Fall through.
	case OutputSERE:
	  std::cout
	    << "Use --sere-priorities to set the following SERE priorities:\n";
	  rs->dump_priorities(std::cout);
	  std::cout
	    << ("Use --boolean-priorities to set the following Boolean "
		"formula priorities:\n");
	  rs->rb.dump_priorities(std::cout);
	  break;
	default:
	  error(2, 0, "internal error: unknown type of output");
	}
      destroy_atomic_prop_set(aprops);
      exit(0);
    }

  // running 'randltl 0' is one way to generate formulas using no
  // atomic propositions so do not complain in that case.
  if (aprops.empty() && !ap_count_given)
    error(2, 0, "No atomic proposition supplied?   Run '%s --help' for usage.",
	  program_name);

  spot::srand(opt_seed);

  typedef Sgi::hash_set<const spot::ltl::formula*,
			const spot::ptr_hash<const spot::ltl::formula> > fset_t;
  fset_t unique_set;

  spot::ltl::ltl_simplifier simpl(simplifier_options());

  while (opt_formulas < 0 || opt_formulas--)
    {
#define MAX_TRIALS 100000
      unsigned trials = MAX_TRIALS;
      bool ignore;
      const spot::ltl::formula* f = 0;
      do
	{
	  ignore = false;
	  int size = opt_tree_size.min;
	  if (size != opt_tree_size.max)
	    size = spot::rrand(size, opt_tree_size.max);
	  f = rf->generate(size);

	  if (opt_wf)
	    {
	      spot::ltl::atomic_prop_set s = aprops;
	      remove_some_props(s);
	      f = spot::ltl::multop::instance(spot::ltl::multop::And,
					      f, GF_n(s));
	    }

	  if (simplification_level)
	    {
	      const spot::ltl::formula* tmp = simpl.simplify(f);
	      f->destroy();
	      f = tmp;
	    }
	  if (opt_unique)
	    {
	      if (unique_set.insert(f).second)
		{
		  f->clone();
		}
	      else
		{
		  ignore = true;
		  f->destroy();
		}
	    }
	}
      while (ignore && --trials);
      if (trials == 0)
	error(2, 0, "failed to generate a new unique formula after %d trials",
	      MAX_TRIALS);
      static int count = 0;
      output_formula_checked(f, 0, ++count);
      f->destroy();
    };


  delete rf;
  // Cleanup the unicity table.
  {
    fset_t::const_iterator i;
    for (i = unique_set.begin(); i != unique_set.end(); ++i)
      (*i)->destroy();
  }
  // Cleanup the atomic_prop set.
  destroy_atomic_prop_set(aprops);
  return 0;
}
