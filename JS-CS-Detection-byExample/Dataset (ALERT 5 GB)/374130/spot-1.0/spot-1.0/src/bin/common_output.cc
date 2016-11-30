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
#include "common_output.hh"
#include <iostream>
#include "ltlvisit/tostring.hh"
#include "ltlvisit/lbt.hh"
#include "common_cout.hh"
#include "error.h"

#define OPT_SPOT 1
#define OPT_WRING 2

output_format_t output_format = spot_output;
bool full_parenth = false;

static const argp_option options[] =
  {
    { "full-parentheses", 'p', 0, 0,
      "output fully-parenthesized formulas", -20 },
    { "spin", 's', 0, 0, "output in Spin's syntax", -20 },
    { "spot", OPT_SPOT, 0, 0, "output in Spot's syntax (default)", -20 },
    { "lbt", 'l', 0, 0, "output in LBT's syntax", -20 },
    { "wring", OPT_WRING, 0, 0, "output in Wring's syntax", -20 },
    { "utf8", '8', 0, 0, "output using UTF-8 characters", -20 },
    { 0, 0, 0, 0, 0, 0 }
  };

const struct argp output_argp = { options, parse_opt_output, 0, 0, 0, 0, 0 };

int
parse_opt_output(int key, char*, struct argp_state*)
{
  // This switch is alphabetically-ordered.
  switch (key)
    {
    case '8':
      output_format = utf8_output;
      break;
    case 'l':
      output_format = lbt_output;
      break;
    case 'p':
      full_parenth = true;
      break;
    case 's':
      output_format = spin_output;
      break;
    case OPT_SPOT:
      output_format = spot_output;
      break;
    case OPT_WRING:
      output_format = wring_output;
      break;
    default:
      return ARGP_ERR_UNKNOWN;
    }
  return 0;
}

static
void
report_not_ltl(const spot::ltl::formula* f,
	       const char* filename, int linenum, const char* syn)
{
  std::string s = spot::ltl::to_string(f);
  static const char msg[] =
    "formula '%s' cannot be written %s's syntax because it is not LTL";
  if (filename)
    error_at_line(2, 0, filename, linenum, msg, s.c_str(), syn);
  else
    error(2, 0, msg, s.c_str(), syn);
}


void
output_formula(const spot::ltl::formula* f, const char* filename, int linenum)
{
  switch (output_format)
    {
    case lbt_output:
      if (f->is_ltl_formula())
	spot::ltl::to_lbt_string(f, std::cout);
      else
	report_not_ltl(f, filename, linenum, "LBT");
      break;
    case spot_output:
      spot::ltl::to_string(f, std::cout, full_parenth);
      break;
    case spin_output:
      if (f->is_ltl_formula())
	spot::ltl::to_spin_string(f, std::cout, full_parenth);
      else
	report_not_ltl(f, filename, linenum, "Spin");
      break;
    case wring_output:
      if (f->is_ltl_formula())
	spot::ltl::to_wring_string(f, std::cout);
      else
	report_not_ltl(f, filename, linenum, "Wring");
      break;
    case utf8_output:
      spot::ltl::to_utf8_string(f, std::cout, full_parenth);
      break;
    }
  // Make sure we abort if we can't write to std::cout anymore
  // (like disk full or broken pipe with SIGPIPE ignored).
  std::cout << std::endl;
  check_cout();
}
