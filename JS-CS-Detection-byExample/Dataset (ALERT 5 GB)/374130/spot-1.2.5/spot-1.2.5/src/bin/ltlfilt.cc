// -*- coding: utf-8 -*-
// Copyright (C) 2012, 2013 Laboratoire de Recherche et Développement de
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

#include <cstdlib>
#include <string>
#include <iostream>
#include <fstream>
#include <argp.h>
#include <cstring>
#include "error.h"

#include "common_setup.hh"
#include "common_finput.hh"
#include "common_output.hh"
#include "common_cout.hh"
#include "common_r.hh"

#include "misc/hash.hh"
#include "ltlvisit/simplify.hh"
#include "ltlvisit/length.hh"
#include "ltlvisit/relabel.hh"
#include "ltlvisit/wmunabbrev.hh"
#include "ltlvisit/remove_x.hh"
#include "ltlast/unop.hh"
#include "ltlast/multop.hh"
#include "tgbaalgos/ltl2tgba_fm.hh"
#include "tgbaalgos/minimize.hh"
#include "tgbaalgos/safety.hh"

const char argp_program_doc[] ="\
Read a list of formulas and output them back after some optional processing.\v\
Exit status:\n\
  0  if some formulas were output (skipped syntax errors do not count)\n\
  1  if no formulas were output (no match)\n\
  2  if any error has been reported";

#define OPT_SKIP_ERRORS 1
#define OPT_DROP_ERRORS 2
#define OPT_NNF 3
#define OPT_LTL 4
#define OPT_NOX 5
#define OPT_BOOLEAN 6
#define OPT_EVENTUAL 7
#define OPT_UNIVERSAL 8
#define OPT_SYNTACTIC_SAFETY 9
#define OPT_SYNTACTIC_GUARANTEE 10
#define OPT_SYNTACTIC_OBLIGATION 11
#define OPT_SYNTACTIC_RECURRENCE 12
#define OPT_SYNTACTIC_PERSISTENCE 13
#define OPT_SAFETY 14
#define OPT_GUARANTEE 15
#define OPT_OBLIGATION 16
#define OPT_SIZE_MIN 17
#define OPT_SIZE_MAX 18
#define OPT_BSIZE_MIN 19
#define OPT_BSIZE_MAX 20
#define OPT_IMPLIED_BY 21
#define OPT_IMPLY 22
#define OPT_EQUIVALENT_TO 23
#define OPT_RELABEL 24
#define OPT_RELABEL_BOOL 25
#define OPT_REMOVE_WM 26
#define OPT_BOOLEAN_TO_ISOP 27
#define OPT_REMOVE_X 28
#define OPT_STUTTER_INSENSITIVE 29

static const argp_option options[] =
  {
    /**************************************************/
    { 0, 0, 0, 0, "Error handling:", 2 },
    { "skip-errors", OPT_SKIP_ERRORS, 0, 0,
      "output erroneous lines as-is without processing", 0 },
    { "drop-errors", OPT_DROP_ERRORS, 0, 0,
      "discard erroneous lines (default)", 0 },
    { "quiet", 'q', 0, 0, "do not report syntax errors", 0 },
    /**************************************************/
    { 0, 0, 0, 0, "Transformation options:", 3 },
    { "negate", 'n', 0, 0, "negate each formula", 0 },
    { "nnf", OPT_NNF, 0, 0, "rewrite formulas in negative normal form", 0 },
    { "relabel", OPT_RELABEL, "abc|pnn", OPTION_ARG_OPTIONAL,
      "relabel all atomic propositions, alphabetically unless " \
      "specified otherwise", 0 },
    { "relabel-bool", OPT_RELABEL_BOOL, "abc|pnn", OPTION_ARG_OPTIONAL,
      "relabel Boolean subexpressions, alphabetically unless " \
      "specified otherwise", 0 },
    { "remove-wm", OPT_REMOVE_WM, 0, 0,
      "rewrite operators W and M using U and R", 0 },
    { "boolean-to-isop", OPT_BOOLEAN_TO_ISOP, 0, 0,
      "rewrite Boolean subformulas as irredundant sum of products "
      "(implies at least -r1)", 0 },
    { "remove-x", OPT_REMOVE_X, 0, 0,
      "remove X operators (valid only for stutter-insensitive properties)",
      0 },
    DECLARE_OPT_R,
    LEVEL_DOC(4),
    /**************************************************/
    { 0, 0, 0, 0,
      "Filtering options (matching is done after transformation):", 5 },
    { "ltl", OPT_LTL, 0, 0, "match only LTL formulas (no PSL operator)", 0 },
    { "nox", OPT_NOX, 0, 0, "match X-free formulas", 0 },
    { "boolean", OPT_BOOLEAN, 0, 0, "match Boolean formulas", 0 },
    { "eventual", OPT_EVENTUAL, 0, 0, "match pure eventualities", 0 },
    { "universal", OPT_UNIVERSAL, 0, 0, "match purely universal formulas", 0 },
    { "syntactic-safety", OPT_SYNTACTIC_SAFETY, 0, 0,
      "match syntactic-safety formulas", 0 },
    { "syntactic-guarantee", OPT_SYNTACTIC_GUARANTEE, 0, 0,
      "match syntactic-guarantee formulas", 0 },
    { "syntactic-obligation", OPT_SYNTACTIC_OBLIGATION, 0, 0,
      "match syntactic-obligation formulas", 0 },
    { "syntactic-recurrence", OPT_SYNTACTIC_RECURRENCE, 0, 0,
      "match syntactic-recurrence formulas", 0 },
    { "syntactic-persistence", OPT_SYNTACTIC_PERSISTENCE, 0, 0,
      "match syntactic-persistence formulas", 0 },
    { "safety", OPT_SAFETY, 0, 0,
      "match safety formulas (even pathological)", 0 },
    { "guarantee", OPT_GUARANTEE, 0, 0,
      "match guarantee formulas (even pathological)", 0 },
    { "obligation", OPT_OBLIGATION, 0, 0,
      "match obligation formulas (even pathological)", 0 },
    { "size-max", OPT_SIZE_MAX, "INT", 0,
      "match formulas with size <= INT", 0 },
    { "size-min", OPT_SIZE_MIN, "INT", 0,
      "match formulas with size >= INT", 0 },
    { "bsize-max", OPT_BSIZE_MAX, "INT", 0,
      "match formulas with Boolean size <= INT", 0 },
    { "bsize-min", OPT_BSIZE_MIN, "INT", 0,
      "match formulas with Boolean size >= INT", 0 },
    { "implied-by", OPT_IMPLIED_BY, "FORMULA", 0,
      "match formulas implied by FORMULA", 0 },
    { "imply", OPT_IMPLY, "FORMULA", 0,
      "match formulas implying FORMULA", 0 },
    { "equivalent-to", OPT_EQUIVALENT_TO, "FORMULA", 0,
      "match formulas equivalent to FORMULA", 0 },
    { "stutter-insensitive", OPT_STUTTER_INSENSITIVE, 0, 0,
      "match stutter-insensitive LTL formulas", 0 },
    { "stutter-invariant", 0, 0, OPTION_ALIAS, 0, 0 },
    { "invert-match", 'v', 0, 0, "select non-matching formulas", 0},
    { "unique", 'u', 0, 0,
      "drop formulas that have already been output (not affected by -v)", 0 },
    /**************************************************/
    { 0, 0, 0, 0, "Output options:", -20 },
    { 0, 0, 0, 0, "The FORMAT string passed to --format may use "\
      "the following interpreted sequences:", -19 },
    { "%f", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "the formula (in the selected syntax)", 0 },
    { "%F", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "the name of the input file", 0 },
    { "%L", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "the original line number in the input file", 0 },
    { "%<", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "the part of the line before the formula if it "
      "comes from a column extracted from a CSV file", 0 },
    { "%>", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "the part of the line after the formula if it "
      "comes from a column extracted from a CSV file", 0 },
    { "%%", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "a single %", 0 },
    { 0, 0, 0, 0, "Miscellaneous options:", -1 },
    { 0, 0, 0, 0, 0, 0 }
  };

const struct argp_child children[] =
  {
    { &finput_argp, 0, 0, 1 },
    { &output_argp, 0, 0, -20 },
    { &misc_argp, 0, 0, -1 },
    { 0, 0, 0, 0 }
  };

static bool one_match = false;

enum error_style_t { drop_errors, skip_errors };
static error_style_t error_style = drop_errors;
static bool quiet = false;
static bool nnf = false;
static bool negate = false;
static bool boolean_to_isop = false;
static bool unique = false;
static bool psl = false;
static bool ltl = false;
static bool nox = false;
static bool invert = false;
static bool boolean = false;
static bool universal = false;
static bool eventual = false;
static bool syntactic_safety = false;
static bool syntactic_guarantee = false;
static bool syntactic_obligation = false;
static bool syntactic_recurrence = false;
static bool syntactic_persistence = false;
static bool safety = false;
static bool guarantee = false;
static bool obligation = false;
static int size_min = -1;
static int size_max = -1;
static int bsize_min = -1;
static int bsize_max = -1;
enum relabeling_mode { NoRelabeling = 0, ApRelabeling, BseRelabeling };
static relabeling_mode relabeling = NoRelabeling;
static spot::ltl::relabeling_style style = spot::ltl::Abc;
static bool remove_wm = false;
static bool remove_x = false;
static bool stutter_insensitive = false;

static const spot::ltl::formula* implied_by = 0;
static const spot::ltl::formula* imply = 0;
static const spot::ltl::formula* equivalent_to = 0;

static int
to_int(const char* s)
{
  char* endptr;
  int res = strtol(s, &endptr, 10);
  if (*endptr)
    error(2, 0, "failed to parse '%s' as an integer.", s);
  return res;
}


static const spot::ltl::formula*
parse_formula_arg(const std::string& input)
{
  spot::ltl::parse_error_list pel;
  const spot::ltl::formula* f = parse_formula(input, pel);
  if (spot::ltl::format_parse_errors(std::cerr, input, pel))
    error(2, 0, "parse error when parsing an argument");
  return f;
}



static int
parse_opt(int key, char* arg, struct argp_state*)
{
  // This switch is alphabetically-ordered.
  switch (key)
    {
    case 'n':
      negate = true;
      break;
    case 'q':
      quiet = true;
      break;
    case OPT_R:
      parse_r(arg);
      break;
    case 'u':
      unique = true;
      break;
    case 'v':
      invert = true;
      break;
    case ARGP_KEY_ARG:
      // FIXME: use stat() to distinguish filename from string?
      jobs.push_back(job(arg, true));
      break;
    case OPT_BOOLEAN:
      boolean = true;
      break;
    case OPT_BOOLEAN_TO_ISOP:
      boolean_to_isop = true;
      break;
    case OPT_BSIZE_MIN:
      bsize_min = to_int(arg);
      break;
    case OPT_BSIZE_MAX:
      bsize_max = to_int(arg);
      break;
    case OPT_DROP_ERRORS:
      error_style = drop_errors;
      break;
    case OPT_EQUIVALENT_TO:
      {
	if (equivalent_to)
	  error(2, 0, "only one --equivalent-to option can be given");
	equivalent_to = parse_formula_arg(arg);
	break;
      }
    case OPT_EVENTUAL:
      eventual = true;
      break;
    case OPT_GUARANTEE:
      guarantee = obligation = true;
      break;
    case OPT_IMPLIED_BY:
      {
	const spot::ltl::formula* i = parse_formula_arg(arg);
	// a→c∧b→c ≡ (a∨b)→c
	implied_by =
	  spot::ltl::multop::instance(spot::ltl::multop::Or, implied_by, i);
	break;
      }
    case OPT_IMPLY:
      {
	// a→b∧a→c ≡ a→(b∧c)
	const spot::ltl::formula* i = parse_formula_arg(arg);
	imply =
	  spot::ltl::multop::instance(spot::ltl::multop::And, imply, i);
	break;
      }
    case OPT_LTL:
      ltl = true;
      break;
    case OPT_NNF:
      nnf = true;
      break;
    case OPT_NOX:
      nox = true;
      break;
    case OPT_OBLIGATION:
      obligation = true;
      break;
    case OPT_RELABEL:
    case OPT_RELABEL_BOOL:
      relabeling = (key == OPT_RELABEL_BOOL ? BseRelabeling : ApRelabeling);
      if (!arg || !strncasecmp(arg, "abc", 6))
	style = spot::ltl::Abc;
      else if (!strncasecmp(arg, "pnn", 4))
	style = spot::ltl::Pnn;
      else
	error(2, 0, "invalid argument for --relabel%s: '%s'",
	      (key == OPT_RELABEL_BOOL ? "-bool" : ""),
	      arg);
      break;
    case OPT_REMOVE_WM:
      remove_wm = true;
      break;
    case OPT_REMOVE_X:
      remove_x = true;
      break;
    case OPT_SAFETY:
      safety = obligation = true;
      break;
    case OPT_SIZE_MIN:
      size_min = to_int(arg);
      break;
    case OPT_SIZE_MAX:
      size_max = to_int(arg);
      break;
    case OPT_SKIP_ERRORS:
      error_style = skip_errors;
      break;
    case OPT_STUTTER_INSENSITIVE:
      stutter_insensitive = true;
      break;
    case OPT_SYNTACTIC_SAFETY:
      syntactic_safety = true;
      break;
    case OPT_SYNTACTIC_GUARANTEE:
      syntactic_guarantee = true;
      break;
    case OPT_SYNTACTIC_OBLIGATION:
      syntactic_obligation = true;
      break;
    case OPT_SYNTACTIC_RECURRENCE:
      syntactic_recurrence = true;
      break;
    case OPT_SYNTACTIC_PERSISTENCE:
      syntactic_persistence = true;
      break;
    case OPT_UNIVERSAL:
      universal = true;
      break;
    default:
      return ARGP_ERR_UNKNOWN;
    }
  return 0;
}

typedef Sgi::hash_set<const spot::ltl::formula*,
		      const spot::ptr_hash<const spot::ltl::formula> > fset_t;

namespace
{
  class ltl_processor: public job_processor
  {
  public:
    spot::ltl::ltl_simplifier& simpl;
    fset_t unique_set;

    ~ltl_processor()
    {
      fset_t::iterator i = unique_set.begin();
      while (i != unique_set.end())
	(*i++)->destroy();

      if (equivalent_to)
	equivalent_to->destroy();
      if (implied_by)
	implied_by->destroy();
      if (imply)
	imply->destroy();
    }

    ltl_processor(spot::ltl::ltl_simplifier& simpl)
    : simpl(simpl)
    {
    }

    int
    process_string(const std::string& input,
		    const char* filename = 0, int linenum = 0)
    {
      spot::ltl::parse_error_list pel;
      const spot::ltl::formula* f = parse_formula(input, pel);

      if (!f || pel.size() > 0)
	  {
	    if (!quiet)
	      {
		if (filename)
		  error_at_line(0, 0, filename, linenum, "parse error:");
		spot::ltl::format_parse_errors(std::cerr, input, pel);
	      }

	    if (f)
	      f->destroy();

	    if (error_style == skip_errors)
	      std::cout << input << std::endl;
	    else
	      assert(error_style == drop_errors);
	    check_cout();
	    return !quiet;
	  }
      return process_formula(f, filename, linenum);
    }

    int
    process_formula(const spot::ltl::formula* f,
		    const char* filename = 0, int linenum = 0)
    {
      if (negate)
	f = spot::ltl::unop::instance(spot::ltl::unop::Not, f);

      if (remove_x)
	{
	  // If simplification are enabled, we do them before and after.
	  if (simplification_level)
	    {
	      const spot::ltl::formula* res = simpl.simplify(f);
	      f->destroy();
	      f = res;
	    }

	  const spot::ltl::formula* res = spot::ltl::remove_x(f);
	  f->destroy();
	  f = res;
	}

      if (simplification_level || boolean_to_isop)
	{
	  const spot::ltl::formula* res = simpl.simplify(f);
	  f->destroy();
	  f = res;
	}

      if (nnf)
	{
	  const spot::ltl::formula* res = simpl.negative_normal_form(f);
	  f->destroy();
	  f = res;
	}

      switch (relabeling)
	{
	case ApRelabeling:
	  {
	    const spot::ltl::formula* res = spot::ltl::relabel(f, style);
	    f->destroy();
	    f = res;
	    break;
	  }
	case BseRelabeling:
	  {
	    const spot::ltl::formula* res = spot::ltl::relabel_bse(f, style);
	    f->destroy();
	    f = res;
	    break;
	  }
	case NoRelabeling:
	  break;
	}

      if (remove_wm)
	{
	  const spot::ltl::formula* res = spot::ltl::unabbreviate_wm(f);
	  f->destroy();
	  f = res;
	}

      bool matched = true;

      matched &= !ltl || f->is_ltl_formula();
      matched &= !psl || f->is_psl_formula();
      matched &= !nox || f->is_X_free();
      matched &= !boolean || f->is_boolean();
      matched &= !universal || f->is_universal();
      matched &= !eventual || f->is_eventual();
      matched &= !syntactic_safety || f->is_syntactic_safety();
      matched &= !syntactic_guarantee || f->is_syntactic_guarantee();
      matched &= !syntactic_obligation || f->is_syntactic_obligation();
      matched &= !syntactic_recurrence || f->is_syntactic_recurrence();
      matched &= !syntactic_persistence || f->is_syntactic_persistence();

      if (matched && (size_min > 0 || size_max >= 0))
	{
	  int l = spot::ltl::length(f);
	  matched &= (size_min <= 0) || (l >= size_min);
	  matched &= (size_max < 0) || (l <= size_max);
	}

      if (matched && (bsize_min > 0 || bsize_max >= 0))
	{
	  int l = spot::ltl::length_boolone(f);
	  matched &= (bsize_min <= 0) || (l >= bsize_min);
	  matched &= (bsize_max < 0) || (l <= bsize_max);
	}

      matched &= !implied_by || simpl.implication(implied_by, f);
      matched &= !imply || simpl.implication(f, imply);
      matched &= !equivalent_to || simpl.are_equivalent(f, equivalent_to);
      matched &= !stutter_insensitive || (f->is_ltl_formula()
					  && is_stutter_insensitive(f));

      // Match obligations and subclasses using WDBA minimization.
      // Because this is costly, we compute it later, so that we don't
      // have to compute it on formulas that have been discarded for
      // other reasons.
      if (matched && obligation)
	{
	  spot::tgba* aut = ltl_to_tgba_fm(f, simpl.get_dict());
	  spot::tgba* min = minimize_obligation(aut, f);
	  assert(min);
	  if (aut == min)
	    {
	      // Not an obligation
	      matched = false;
	    }
	  else
	    {
	      matched &= !guarantee || is_guarantee_automaton(min);
	      matched &= !safety || is_safety_mwdba(min);
	      delete min;
	    }
	  delete aut;
	}

      matched ^= invert;

      if (unique)
	{
	  if (unique_set.insert(f).second)
	    f->clone();
	  else
	    matched = false;
	}

      if (matched)
	{
	  one_match = true;
	  output_formula_checked(f, filename, linenum, prefix, suffix);
	}
      f->destroy();
      return 0;
    }
  };
}

int
main(int argc, char** argv)
{
  setup(argv);

  const argp ap = { options, parse_opt, "[FILENAME[/COL]...]",
		    argp_program_doc, children, 0, 0 };

  if (int err = argp_parse(&ap, argc, argv, ARGP_NO_HELP, 0, 0))
    exit(err);

  if (jobs.empty())
    jobs.push_back(job("-", 1));

  // --stutter-insensitive implies --ltl
  ltl |= stutter_insensitive;
  if (boolean_to_isop && simplification_level == 0)
    simplification_level = 1;
  spot::ltl::ltl_simplifier_options opt = simplifier_options();
  opt.boolean_to_isop = boolean_to_isop;
  spot::ltl::ltl_simplifier simpl(opt);
  ltl_processor processor(simpl);
  if (processor.run())
    return 2;
  return one_match ? 0 : 1;
}
