// -*- coding: utf-8 -*-
// Copyright (C) 2012, 2013 Laboratoire de Recherche et Développement
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

#include "common_post.hh"
#include "common_r.hh"
#include "error.h"

spot::postprocessor::output_type type = spot::postprocessor::TGBA;
spot::postprocessor::output_pref pref = spot::postprocessor::Small;
spot::postprocessor::output_pref comp = spot::postprocessor::Any;
spot::postprocessor::optimization_level level = spot::postprocessor::High;

#define OPT_SMALL 1
#define OPT_LOW 2
#define OPT_MEDIUM 3
#define OPT_HIGH 4

static const argp_option options[] =
  {
    /**************************************************/
    { 0, 0, 0, 0, "Translation intent:", 20 },
    { "small", OPT_SMALL, 0, 0, "prefer small automata (default)", 0 },
    { "deterministic", 'D', 0, 0, "prefer deterministic automata", 0 },
    { "any", 'a', 0, 0, "no preference", 0 },
    { "complete", 'C', 0, 0, "output a complete automaton (combine "
      "with other intents)", 0 },
    /**************************************************/
    { 0, 0, 0, 0, "Optimization level:", 21 },
    { "low", OPT_LOW, 0, 0, "minimal optimizations (fast)", 0 },
    { "medium", OPT_MEDIUM, 0, 0, "moderate optimizations", 0 },
    { "high", OPT_HIGH, 0, 0,
      "all available optimizations (slow, default)", 0 },
    { 0, 0, 0, 0, 0, 0 }
  };

static int
parse_opt_post(int key, char*, struct argp_state*)
{
  // This switch is alphabetically-ordered.
  switch (key)
    {
    case 'a':
      pref = spot::postprocessor::Any;
      break;
    case 'C':
      comp = spot::postprocessor::Complete;
      break;
    case 'D':
      pref = spot::postprocessor::Deterministic;
      break;
    case OPT_HIGH:
      level = spot::postprocessor::High;
      simplification_level = 3;
      break;
    case OPT_LOW:
      level = spot::postprocessor::Low;
      simplification_level = 1;
      break;
    case OPT_MEDIUM:
      level = spot::postprocessor::Medium;
      simplification_level = 2;
      break;
    case OPT_SMALL:
      pref = spot::postprocessor::Small;
      break;
    default:
      return ARGP_ERR_UNKNOWN;
    }
  return 0;
}

const struct argp post_argp = { options, parse_opt_post, 0, 0, 0, 0, 0 };

