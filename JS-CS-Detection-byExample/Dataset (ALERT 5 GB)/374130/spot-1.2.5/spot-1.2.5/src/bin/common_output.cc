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

#include "common_sys.hh"
#include "common_output.hh"
#include <iostream>
#include <sstream>
#include "ltlvisit/tostring.hh"
#include "ltlvisit/lbt.hh"
#include "misc/formater.hh"
#include "misc/escape.hh"
#include "common_cout.hh"
#include "error.h"

#define OPT_SPOT 1
#define OPT_WRING 2
#define OPT_LATEX 3
#define OPT_FORMAT 4
#define OPT_CSV 5

output_format_t output_format = spot_output;
bool full_parenth = false;
bool escape_csv = false;

static const argp_option options[] =
  {
    { "full-parentheses", 'p', 0, 0,
      "output fully-parenthesized formulas", -20 },
    { "spin", 's', 0, 0, "output in Spin's syntax", -20 },
    { "spot", OPT_SPOT, 0, 0, "output in Spot's syntax (default)", -20 },
    { "lbt", 'l', 0, 0, "output in LBT's syntax", -20 },
    { "wring", OPT_WRING, 0, 0, "output in Wring's syntax", -20 },
    { "utf8", '8', 0, 0, "output using UTF-8 characters", -20 },
    { "latex", OPT_LATEX, 0, 0, "output using LaTeX macros", -20 },
    { "csv-escape", OPT_CSV, 0, 0, "quote the formula for use in a CSV file",
      -20 },
    { "format", OPT_FORMAT, "FORMAT", 0,
      "specify how each line should be output (default: \"%f\")", -20 },
    { 0, 0, 0, 0, 0, 0 }
  };

const struct argp output_argp = { options, parse_opt_output, 0, 0, 0, 0, 0 };

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

static void
stream_formula(std::ostream& out,
	       const spot::ltl::formula* f, const char* filename, int linenum)
{
  switch (output_format)
    {
    case lbt_output:
      if (f->is_ltl_formula())
	spot::ltl::to_lbt_string(f, out);
      else
	report_not_ltl(f, filename, linenum, "LBT");
      break;
    case spot_output:
      spot::ltl::to_string(f, out, full_parenth);
      break;
    case spin_output:
      if (f->is_ltl_formula())
	spot::ltl::to_spin_string(f, out, full_parenth);
      else
	report_not_ltl(f, filename, linenum, "Spin");
      break;
    case wring_output:
      if (f->is_ltl_formula())
	spot::ltl::to_wring_string(f, out);
      else
	report_not_ltl(f, filename, linenum, "Wring");
      break;
    case utf8_output:
      spot::ltl::to_utf8_string(f, out, full_parenth);
      break;
    case latex_output:
      spot::ltl::to_latex_string(f, out, full_parenth);
      break;
    }
}

static void
stream_escapable_formula(std::ostream& os,
			 const spot::ltl::formula* f,
			 const char* filename, int linenum)
{
  if (escape_csv)
    {
      std::ostringstream out;
      stream_formula(out, f, filename, linenum);
      os << '"';
      spot::escape_rfc4180(os, out.str());
      os << '"';
    }
  else
    {
      stream_formula(os, f, filename, linenum);
    }
}


namespace
{
  struct formula_with_location
  {
    const spot::ltl::formula* f;
    const char* filename;
    int line;
    const char* prefix;
    const char* suffix;
  };

  class printable_formula:
    public spot::printable_value<const formula_with_location*>
  {
  public:
    printable_formula&
    operator=(const formula_with_location* new_val)
    {
      val_ = new_val;
      return *this;
    }

    virtual void
    print(std::ostream& os, const char*) const
    {
      stream_escapable_formula(os, val_->f, val_->filename, val_->line);
    }
  };

  class formula_printer: protected spot::formater
  {
  public:
    formula_printer(std::ostream& os, const char* format)
      : format_(format)
    {
      declare('f', &fl_);
      declare('F', &filename_);
      declare('L', &line_);
      declare('<', &prefix_);
      declare('>', &suffix_);
      set_output(os);
    }

    std::ostream&
    print(const formula_with_location& fl)
    {
      fl_ = &fl;
      filename_ = fl.filename ? fl.filename : "";
      line_ = fl.line;
      prefix_ = fl.prefix ? fl.prefix : "";
      suffix_ = fl.suffix ? fl.suffix : "";
      return format(format_);
    }

  private:
    const char* format_;
    printable_formula fl_;
    spot::printable_value<const char*> filename_;
    spot::printable_value<int> line_;
    spot::printable_value<const char*> prefix_;
    spot::printable_value<const char*> suffix_;
  };
}

static formula_printer* format = 0;

int
parse_opt_output(int key, char* arg, struct argp_state*)
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
    case OPT_CSV:
      escape_csv = true;
      break;
    case OPT_LATEX:
      output_format = latex_output;
      break;
    case OPT_SPOT:
      output_format = spot_output;
      break;
    case OPT_WRING:
      output_format = wring_output;
      break;
    case OPT_FORMAT:
      delete format;
      format = new formula_printer(std::cout, arg);
      break;
    default:
      return ARGP_ERR_UNKNOWN;
    }
  return 0;
}


void
output_formula(std::ostream& out,
	       const spot::ltl::formula* f, const char* filename, int linenum,
	       const char* prefix, const char* suffix)
{
  if (!format)
    {
      if (prefix)
	out << prefix << ",";
      stream_escapable_formula(out, f, filename, linenum);
      if (suffix)
	out << "," << suffix;
    }
  else
    {
      formula_with_location fl = { f, filename, linenum, prefix, suffix };
      format->print(fl);
    }
}

void
output_formula_checked(const spot::ltl::formula* f,
		       const char* filename, int linenum,
		       const char* prefix, const char* suffix)
{
  output_formula(std::cout, f, filename, linenum, prefix, suffix);
  std::cout << std::endl;
  // Make sure we abort if we can't write to std::cout anymore
  // (like disk full or broken pipe with SIGPIPE ignored).
  check_cout();
}
