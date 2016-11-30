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

#include <string>
#include <iostream>
#include <sstream>
#include <fstream>
#include <cstdlib>
#include <cstdio>
#include <argp.h>
#include <signal.h>
#include <unistd.h>
#include <sys/wait.h>
#include "error.h"
#include "gethrxtime.h"
#include "argmatch.h"

#include "common_setup.hh"
#include "common_cout.hh"
#include "common_finput.hh"
#include "neverparse/public.hh"
#include "dstarparse/public.hh"
#include "ltlast/unop.hh"
#include "ltlvisit/tostring.hh"
#include "ltlvisit/apcollect.hh"
#include "ltlvisit/lbt.hh"
#include "ltlvisit/relabel.hh"
#include "tgbaalgos/lbtt.hh"
#include "tgba/tgbaproduct.hh"
#include "tgbaalgos/gtec/gtec.hh"
#include "tgbaalgos/randomgraph.hh"
#include "tgbaalgos/scc.hh"
#include "tgbaalgos/dotty.hh"
#include "tgbaalgos/isweakscc.hh"
#include "tgbaalgos/reducerun.hh"
#include "tgbaalgos/word.hh"
#include "tgbaalgos/dtgbacomp.hh"
#include "misc/formater.hh"
#include "tgbaalgos/stats.hh"
#include "tgbaalgos/isdet.hh"
#include "misc/escape.hh"
#include "misc/hash.hh"
#include "misc/random.hh"
#include "misc/tmpfile.hh"

// Disable handling of timeout on systems that miss kill() or alarm().
// For instance MinGW.
#if HAVE_KILL && HAVE_ALARM
# define ENABLE_TIMEOUT 1
#else
# define ENABLE_TIMEOUT 0
#endif

const char argp_program_doc[] ="\
Call several LTL/PSL translators and cross-compare their output to detect \
bugs, or to gather statistics.  The list of formulas to use should be \
supplied on standard input, or using the -f or -F options.\v\
Exit status:\n\
  0  everything went fine (timeouts are OK too)\n\
  1  some translator failed to output something we understand, or failed\n\
     sanity checks (statistics were output nonetheless)\n\
  2  ltlcross aborted on error\n\
";


#define OPT_STATES 1
#define OPT_DENSITY 2
#define OPT_JSON 3
#define OPT_CSV 4
#define OPT_DUPS 5
#define OPT_NOCHECKS 6
#define OPT_STOP_ERR 7
#define OPT_SEED 8
#define OPT_PRODUCTS 9
#define OPT_COLOR 10
#define OPT_NOCOMP 11
#define OPT_OMIT 12
#define OPT_BOGUS 13

static const argp_option options[] =
  {
    /**************************************************/
    { 0, 0, 0, 0, "Specifying translators to call:", 2 },
    { "translator", 't', "COMMANDFMT", 0,
      "register one translator to call", 0 },
    { "timeout", 'T', "NUMBER", 0, "kill translators after NUMBER seconds", 0 },
    /**************************************************/
    { 0, 0, 0, 0,
      "COMMANDFMT should specify input and output arguments using the "
      "following character sequences:", 3 },
    { "%f,%s,%l,%w", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "the formula as a (quoted) string in Spot, Spin, LBT, or Wring's syntax",
      0 },
    { "%F,%S,%L,%W", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "the formula as a file in Spot, Spin, LBT, or Wring's syntax", 0 },
    { "%N,%T,%D", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "the output automaton as a Never claim, in LBTT's or in LTL2DSTAR's "
      "format", 0 },
    { 0, 0, 0, 0,
      "If either %l, %L, or %T are used, any input formula that does "
      "not use LBT-style atomic propositions (i.e. p0, p1, ...) will be "
      "relabeled automatically.\n"
      "Furthermore, if COMMANDFMT has the form \"{NAME}CMD\", then only CMD "
      "will be passed to the shell, and NAME will be used to name the tool "
      "in the CSV or JSON outputs.", 0 },
    /**************************************************/
    { 0, 0, 0, 0, "ltlcross behavior:", 4 },
    { "allow-dups", OPT_DUPS, 0, 0,
      "translate duplicate formulas in input", 0 },
    { "no-checks", OPT_NOCHECKS, 0, 0,
      "do not perform any sanity checks (negated formulas "
      "will not be translated)", 0 },
    { "no-complement", OPT_NOCOMP, 0, 0,
      "do not complement deterministic automata to perform extra checks", 0 },
    { "stop-on-error", OPT_STOP_ERR, 0, 0,
      "stop on first execution error or failure to pass"
      " sanity checks (timeouts are OK)", 0 },
    /**************************************************/
    { 0, 0, 0, 0, "State-space generation:", 5 },
    { "states", OPT_STATES, "INT", 0,
      "number of the states in the state-spaces (200 by default)", 0 },
    { "density", OPT_DENSITY, "FLOAT", 0,
      "probability, between 0.0 and 1.0, to add a transition between "
      "two states (0.1 by default)", 0 },
    { "seed", OPT_SEED, "INT", 0,
      "seed for the random number generator (0 by default)", 0 },
    { "products", OPT_PRODUCTS, "[+]INT", 0,
      "number of products to perform (1 by default), statistics will be "
      "averaged unless the number is prefixed with '+'", 0 },
    /**************************************************/
    { 0, 0, 0, 0, "Statistics output:", 6 },
    { "json", OPT_JSON, "FILENAME", OPTION_ARG_OPTIONAL,
      "output statistics as JSON in FILENAME or on standard output", 0 },
    { "csv", OPT_CSV, "FILENAME", OPTION_ARG_OPTIONAL,
      "output statistics as CSV in FILENAME or on standard output", 0 },
    { "omit-missing", OPT_OMIT, 0, 0,
      "do not output statistics for timeouts or failed translations", 0 },
    /**************************************************/
    { 0, 0, 0, 0, "Miscellaneous options:", -1 },
    { "color", OPT_COLOR, "WHEN", OPTION_ARG_OPTIONAL,
      "colorize output; WHEN can be 'never', 'always' (the default if "
      "--color is used without argument), or "
      "'auto' (the default if --color is not used)", 0 },
    { "save-bogus", OPT_BOGUS, "FILENAME", 0,
      "save formulas for which problems were detected in FILENAME", 0 },
    { 0, 0, 0, 0, 0, 0 }
  };

const struct argp_child children[] =
  {
    { &finput_argp, 0, 0, 1 },
    { &misc_argp, 0, 0, -1 },
    { 0, 0, 0, 0 }
  };

enum color_type { color_never, color_always, color_if_tty };

static char const *const color_args[] =
{
  "always", "yes", "force",
  "never", "no", "none",
  "auto", "tty", "if-tty", 0
};
static color_type const color_types[] =
{
  color_always, color_always, color_always,
  color_never, color_never, color_never,
  color_if_tty, color_if_tty, color_if_tty
};
ARGMATCH_VERIFY(color_args, color_types);

color_type color_opt = color_if_tty;
const char* bright_red = "\033[01;31m";
const char* bright_blue = "\033[01;34m";
const char* bright_yellow = "\033[01;33m";
const char* reset_color = "\033[m";

unsigned states = 200;
float density = 0.1;
unsigned timeout = 0;
const char* json_output = 0;
const char* csv_output = 0;
bool want_stats = false;
bool allow_dups = false;
bool no_checks = false;
bool no_complement = false;
bool stop_on_error = false;
int seed = 0;
unsigned products = 1;
bool products_avg = true;
bool opt_omit = false;
bool has_sr = false; // Has Streett or Rabin automata to process.
const char* bogus_output_filename = 0;
std::ofstream* bogus_output = 0;

struct translator_spec
{
  // The translator command, as specified on the command-line.
  // If this has the form of
  //    {name}cmd
  // then it is split in two components.
  // Otherwise, spec=cmd=name.
  const char* spec;
  // actual shell command (or spec)
  const char* cmd;
  // name of the translator (or spec)
  const char* name;

  translator_spec(const char* spec)
    : spec(spec), cmd(spec), name(spec)
  {
    if (*cmd != '{')
      return;

    // Match the closing '}'
    const char* pos = cmd;
    unsigned count = 1;
    while (*++pos)
      {
	if (*pos == '{')
	  ++count;
	else if (*pos == '}')
	  if (!--count)
	    {
	      name = strndup(cmd + 1, pos - cmd - 1);
	      cmd = pos + 1;
	      while (*cmd == ' ' || *cmd == '\t')
		++cmd;
	      break;
	    }
      }
  }

  translator_spec(const translator_spec& other)
    : spec(other.spec), cmd(other.cmd), name(other.name)
  {
    if (name != spec)
      name = strdup(name);
  }

  ~translator_spec()
  {
    if (name != spec)
      free(const_cast<char*>(name));
  }
};

std::vector<translator_spec> translators;

bool global_error_flag = false;

static std::ostream&
global_error()
{
  global_error_flag = true;
  if (color_opt)
    std::cerr << bright_red;
  return std::cerr;
}

static std::ostream&
example()
{
  if (color_opt)
    std::cerr << bright_yellow;
  return std::cerr;
}


static void
end_error()
{
  if (color_opt)
    std::cerr << reset_color;
}


struct statistics
{
  statistics()
    : ok(false),
      has_in(false),
      status_str(0),
      status_code(0),
      time(0),
      in_type(0),
      in_states(0),
      in_edges(0),
      in_transitions(0),
      in_acc(0),
      in_scc(0),
      states(0),
      edges(0),
      transitions(0),
      acc(0),
      scc(0),
      nonacc_scc(0),
      terminal_scc(0),
      weak_scc(0),
      strong_scc(0),
      nondetstates(0),
      nondeterministic(false),
      terminal_aut(false),
      weak_aut(false),
      strong_aut(false)
  {
  }

  // If OK is false, only the status_str, status_code, and time fields
  // should be valid.
  bool ok;
  // has in_* data to display.
  bool has_in;
  const char* status_str;
  int status_code;
  double time;
  const char* in_type;
  unsigned in_states;
  unsigned in_edges;
  unsigned in_transitions;
  unsigned in_acc;
  unsigned in_scc;
  unsigned states;
  unsigned edges;
  unsigned transitions;
  unsigned acc;
  unsigned scc;
  unsigned nonacc_scc;
  unsigned terminal_scc;
  unsigned weak_scc;
  unsigned strong_scc;
  unsigned nondetstates;
  bool nondeterministic;
  bool terminal_aut;
  bool weak_aut;
  bool strong_aut;
  std::vector<double> product_states;
  std::vector<double> product_transitions;
  std::vector<double> product_scc;

  static void
  fields(std::ostream& os, bool show_exit, bool show_sr)
  {
    if (show_exit)
      os << "\"exit_status\",\"exit_code\",";
    os << "\"time\",";
    if (show_sr)
      os << ("\"in_type\",\"in_states\",\"in_edges\",\"in_transitions\","
	     "\"in_acc\",\"in_scc\",");
    os << ("\"states\","
	   "\"edges\","
	   "\"transitions\","
	   "\"acc\","
	   "\"scc\","
	   "\"nonacc_scc\","
	   "\"terminal_scc\","
	   "\"weak_scc\","
	   "\"strong_scc\","
	   "\"nondet_states\","
	   "\"nondet_aut\","
	   "\"terminal_aut\","
	   "\"weak_aut\","
	   "\"strong_aut\"");
    size_t m = products_avg ? 1U : products;
    for (size_t i = 0; i < m; ++i)
      os << ",\"product_states\",\"product_transitions\",\"product_scc\"";
  }

  void
  to_csv(std::ostream& os, bool show_exit, bool show_sr, const char* na = "")
  {
    if (show_exit)
      os << '"' << status_str << "\"," << status_code << ',';
    os << time << ',';
    if (ok)
      {
	if (has_in)
	  os << '"' << in_type << "\","
	     << in_states << ','
	     << in_edges << ','
	     << in_transitions << ','
	     << in_acc << ','
	     << in_scc << ',';
	else if (show_sr)
	  os << na << ',' << na << ',' << na << ','
	     << na << ',' << na << ',' << na << ',';
	os << states << ','
	   << edges << ','
	   << transitions << ','
	   << acc << ','
	   << scc << ','
	   << nonacc_scc << ','
	   << terminal_scc << ','
	   << weak_scc << ','
	   << strong_scc << ','
	   << nondetstates << ','
	   << nondeterministic << ','
	   << terminal_aut << ','
	   << weak_aut << ','
	   << strong_aut;
	if (!products_avg)
	  {
	    for (size_t i = 0; i < products; ++i)
	      os << ',' << product_states[i]
		 << ',' << product_transitions[i]
		 << ',' << product_scc[i];
	  }
	else
	  {
	    double st = 0.0;
	    double tr = 0.0;
	    double sc = 0.0;
	    for (size_t i = 0; i < products; ++i)
	      {
		st += product_states[i];
		tr += product_transitions[i];
		sc += product_scc[i];
	      }
	    os << ',' << (st / products)
	       << ',' << (tr / products)
	       << ',' << (sc / products);
	  }
      }
    else
      {
	size_t m = products_avg ? 1U : products;
	m *= 3;
	m += 13 + show_sr * 6;
	os << na;
	for (size_t i = 0; i < m; ++i)
	  os << ',' << na;
      }
  }
};

typedef std::vector<statistics> statistics_formula;
typedef std::vector<statistics_formula> statistics_vector;
statistics_vector vstats;
std::vector<std::string> formulas;

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
to_pos_int(const char* s)
{
  int res = to_int(s);
  if (res < 0)
    error(2, 0, "%d is not positive", res);
  return res;
}

static float
to_float(const char* s)
{
  char* endptr;
  // Do not use strtof(), it does not exist on Solaris 9.
  float res = strtod(s, &endptr);
  if (*endptr)
    error(2, 0, "failed to parse '%s' as a float.", s);
  return res;
}

static float
to_probability(const char* s)
{
  float res = to_float(s);
  if (res < 0.0 || res > 1.0)
    error(2, 0, "%f is not between 0 and 1.", res);
  return res;
}


static int
parse_opt(int key, char* arg, struct argp_state*)
{
  // This switch is alphabetically-ordered.
  switch (key)
    {
    case 't':
    case ARGP_KEY_ARG:
      translators.push_back(arg);
      break;
    case 'T':
      timeout = to_pos_int(arg);
#if !ENABLE_TIMEOUT
      std::cerr << "warning: setting a timeout is not supported "
		<< "on your platform" << std::endl;
#endif
      break;
    case OPT_BOGUS:
      {
	bogus_output = new std::ofstream(arg);
	if (!*bogus_output)
	  error(2, errno, "cannot open '%s'", arg);
	bogus_output_filename = arg;
	break;
      }
    case OPT_COLOR:
      {
	if (arg)
	  color_opt = XARGMATCH("--color", arg, color_args, color_types);
	else
	  color_opt = color_always;
	break;
      }
    case OPT_CSV:
      want_stats = true;
      csv_output = arg ? arg : "-";
      break;
    case OPT_DENSITY:
      density = to_probability(arg);
      break;
    case OPT_DUPS:
      allow_dups = true;
      break;
    case OPT_JSON:
      want_stats = true;
      json_output = arg ? arg : "-";
      break;
    case OPT_PRODUCTS:
      if (*arg == '+')
	{
	  products_avg = false;
	  ++arg;
	}
      products = to_pos_int(arg);
      break;
    case OPT_NOCHECKS:
      no_checks = true;
      no_complement = true;
      break;
    case OPT_NOCOMP:
      no_complement = true;
      break;
    case OPT_OMIT:
      opt_omit = true;
      break;
    case OPT_SEED:
      seed = to_pos_int(arg);
      break;
    case OPT_STATES:
      states = to_pos_int(arg);
      break;
    case OPT_STOP_ERR:
      stop_on_error = true;
      break;
    default:
      return ARGP_ERR_UNKNOWN;
    }
  return 0;
}

static volatile bool timed_out = false;
unsigned timeout_count = 0;

#if ENABLE_TIMEOUT
static volatile int alarm_on = 0;
static int child_pid = -1;

static void
sig_handler(int sig)
{
  if (child_pid == 0)
    error(2, 0, "child received signal %d before starting", sig);

  if (sig == SIGALRM && alarm_on)
    {
      timed_out = true;
      if (--alarm_on)
	{
	  // Send SIGTERM to children.
	  kill(-child_pid, SIGTERM);
	  // Try again later if it didn't work.  (alarm() will be reset
	  // if it did work and the call to wait() returns)
	  alarm(2);
	}
      else
	{
	  // After a few gentle tries, really kill that child.
	  kill(-child_pid, SIGKILL);
	}
    }
  else
    {
      // forward signal
      kill(-child_pid, sig);
      // cleanup files
      spot::cleanup_tmpfiles();
      // and die verbosely
      error(2, 0, "received signal %d", sig);
    }
}

static void
setup_sig_handler()
{
  struct sigaction sa;
  sa.sa_handler = sig_handler;
  sigemptyset(&sa.sa_mask);
  sa.sa_flags = SA_RESTART; // So that wait() doesn't get aborted by SIGALRM.
  sigaction(SIGALRM, &sa, 0);
  // Catch termination signals, so we can kill the subprocess.
  sigaction(SIGHUP, &sa, 0);
  sigaction(SIGINT, &sa, 0);
  sigaction(SIGQUIT, &sa, 0);
  sigaction(SIGTERM, &sa, 0);
}

static int
exec_with_timeout(const char* cmd)
{
  int status;

  timed_out = false;

  child_pid = fork();
  if (child_pid == -1)
    error(2, errno, "failed to fork()");

  if (child_pid == 0)
    {
      setpgid(0, 0);
      execlp("sh", "sh", "-c", cmd, (char*)0);
      error(2, errno, "failed to run 'sh'");
      // never reached
      return -1;
    }
  else
    {
      alarm(timeout);
      // Upon SIGALRM, the child will receive up to 3
      // signals: SIGTERM, SIGTERM, SIGKILL.
      alarm_on = 3;
      int w = waitpid(child_pid, &status, 0);
      alarm_on = 0;

      if (w == -1)
	error(2, errno, "error during wait()");

      alarm(0);
    }
  return status;
}
#else // !ENABLE_TIMEOUT
#define exec_with_timeout(cmd) system(cmd)
#define setup_sig_handler() while (0);
#endif // !ENABLE_TIMEOUT

namespace
{
  struct quoted_string: public spot::printable_value<std::string>
  {
    using spot::printable_value<std::string>::operator=;

    void
    print(std::ostream& os, const char* pos) const
    {
      os << '\'';
      this->spot::printable_value<std::string>::print(os, pos);
      os << '\'';
    }
  };

  struct printable_result_filename:
    public spot::printable_value<spot::temporary_file*>
  {
    unsigned translator_num;
    enum output_format { None, Spin, Lbtt, Dstar };
    mutable output_format format;

    printable_result_filename()
    {
      val_ = 0;
    }

    ~printable_result_filename()
    {
      delete val_;
    }

    void reset(unsigned n)
    {
      translator_num = n;
      format = None;
    }

    void cleanup()
    {
      delete val_;
      val_ = 0;
    }

    void
    print(std::ostream& os, const char* pos) const
    {
      output_format old_format = format;
      if (*pos == 'N')
	format = Spin;
      else if (*pos == 'T')
	format = Lbtt;
      else if (*pos == 'D')
	format = Dstar;
      else
	assert(!"BUG");

      if (val_)
	{
	  // It's OK to use a specified multiple time, but it's not OK
	  // to mix the formats.
	  if (format != old_format)
	    error(2, 0, "you may not mix %%D, %%N, and %%T specifiers: %s",
		  translators[translator_num].spec);
	}
      else
	{
	  char prefix[30];
	  snprintf(prefix, sizeof prefix, "lcr-o%u-", translator_num);
	  const_cast<printable_result_filename*>(this)->val_
	    = spot::create_tmpfile(prefix);
	}
      os << '\'' << val_ << '\'';
    }
  };

  class translator_runner: protected spot::formater
  {
  private:
    spot::bdd_dict& dict;
    // Round-specific variables
    quoted_string string_ltl_spot;
    quoted_string string_ltl_spin;
    quoted_string string_ltl_lbt;
    quoted_string string_ltl_wring;
    quoted_string filename_ltl_spot;
    quoted_string filename_ltl_spin;
    quoted_string filename_ltl_lbt;
    quoted_string filename_ltl_wring;
    // Run-specific variables
    printable_result_filename output;
  public:
    using spot::formater::has;

    translator_runner(spot::bdd_dict& dict)
      : dict(dict)
    {
      declare('f', &string_ltl_spot);
      declare('s', &string_ltl_spin);
      declare('l', &string_ltl_lbt);
      declare('w', &string_ltl_wring);
      declare('F', &filename_ltl_spot);
      declare('S', &filename_ltl_spin);
      declare('L', &filename_ltl_lbt);
      declare('W', &filename_ltl_wring);
      declare('D', &output);
      declare('N', &output);
      declare('T', &output);

      size_t s = translators.size();
      assert(s);
      for (size_t n = 0; n < s; ++n)
	{
	  // Check that each translator uses at least one input and
	  // one output.
	  std::vector<bool> has(256);
	  const translator_spec& t = translators[n];
	  scan(t.cmd, has);
	  if (!(has['f'] || has['s'] || has['l'] || has['w']
		|| has['F'] || has['S'] || has['L'] || has['W']))
	    error(2, 0, "no input %%-sequence in '%s'.\n       Use "
		  "one of %%f,%%s,%%l,%%w,%%F,%%S,%%L,%%W to indicate how "
		  "to pass the formula.", t.spec);
	  bool has_d = has['D'];
	  if (!(has_d || has['N'] || has['T']))
	    error(2, 0, "no output %%-sequence in '%s'.\n      Use one of "
		  "%%D,%%N,%%T to indicate where the automaton is saved.",
		  t.spec);
	  has_sr |= has_d;

	  // Remember the %-sequences used by all translators.
	  prime(t.cmd);
	}

    }

    void
    string_to_tmp(std::string& str, unsigned n, std::string& tmpname)
    {
      char prefix[30];
      snprintf(prefix, sizeof prefix, "lcr-i%u-", n);
      spot::open_temporary_file* tmpfile = spot::create_open_tmpfile(prefix);
      tmpname = tmpfile->name();
      int fd = tmpfile->fd();
      ssize_t s = str.size();
      if (write(fd, str.c_str(), s) != s
	  || write(fd, "\n", 1) != 1)
	error(2, errno, "failed to write into %s", tmpname.c_str());
      tmpfile->close();
    }

    const std::string&
    formula() const
    {
      // Pick the most readable format we have...
      if (!string_ltl_spot.val().empty())
	return string_ltl_spot;
      if (!string_ltl_spin.val().empty())
	return string_ltl_spin;
      if (!string_ltl_wring.val().empty())
	return string_ltl_wring;
      if (!string_ltl_lbt.val().empty())
	return string_ltl_lbt;
      assert(!"None of the translators need the input formula?");
      return string_ltl_spot;
    }

    void
    round_formula(const spot::ltl::formula* f, unsigned serial)
    {
      if (has('f') || has('F'))
	string_ltl_spot = spot::ltl::to_string(f, true);
      if (has('s') || has('S'))
	string_ltl_spin = spot::ltl::to_spin_string(f, true);
      if (has('l') || has('L'))
	string_ltl_lbt = spot::ltl::to_lbt_string(f);
      if (has('w') || has('W'))
	string_ltl_wring = spot::ltl::to_wring_string(f);
      if (has('F'))
	string_to_tmp(string_ltl_spot, serial, filename_ltl_spot);
      if (has('S'))
	string_to_tmp(string_ltl_spin, serial, filename_ltl_spin);
      if (has('L'))
	string_to_tmp(string_ltl_lbt, serial, filename_ltl_lbt);
      if (has('W'))
	string_to_tmp(string_ltl_wring, serial, filename_ltl_wring);
    }

    const spot::tgba*
    translate(unsigned int translator_num, char l, statistics_formula* fstats,
	      bool& problem)
    {
      output.reset(translator_num);

      std::ostringstream command;
      format(command, translators[translator_num].cmd);

      assert(output.format != printable_result_filename::None);

      std::string cmd = command.str();
      std::cerr << "Running [" << l << translator_num << "]: "
		<< cmd << std::endl;
      xtime_t before = gethrxtime();
      int es = exec_with_timeout(cmd.c_str());
      xtime_t after = gethrxtime();

      const char* status_str = 0;

      const spot::tgba* res = 0;
      if (timed_out)
	{
	  // This is not considered to be a global error.
	  std::cerr << "warning: timeout during execution of command\n";
	  ++timeout_count;
	  status_str = "timeout";
	  problem = false;	// A timeout is not a sign of a bug
	  es = -1;
	}
      else if (WIFSIGNALED(es))
	{
	  status_str = "signal";
	  problem = true;
	  es = WTERMSIG(es);
	  global_error() << "error: execution terminated by signal "
			 << es << ".\n";
	  end_error();
	}
      else if (WIFEXITED(es) && WEXITSTATUS(es) != 0)
	{
	  es = WEXITSTATUS(es);
	  status_str = "exit code";
	  problem = true;
	  global_error() << "error: execution returned exit code "
			 << es << ".\n";
	  end_error();
	}
      else
	{
	  status_str = "ok";
	  problem = false;
	  es = 0;
	  switch (output.format)
	    {
	    case printable_result_filename::Spin:
	      {
		spot::neverclaim_parse_error_list pel;
		std::string filename = output.val()->name();
		res = spot::neverclaim_parse(filename, pel, &dict);
		if (!pel.empty())
		  {
		    status_str = "parse error";
		    problem = true;
		    es = -1;
		    std::ostream& err = global_error();
		    err << "error: failed to parse the produced neverclaim.\n";
		    spot::format_neverclaim_parse_errors(err, filename, pel);
		    end_error();
		    delete res;
		    res = 0;
		  }
		break;
	      }
	    case printable_result_filename::Lbtt:
	      {
		std::string error;
		std::ifstream f(output.val()->name());
		if (!f)
		  {
		    status_str = "no output";
		    problem = true;
		    es = -1;
		    global_error() << "Cannot open " << output.val()
				   << std::endl;
		    end_error();
		  }
		else
		  {
		    res = spot::lbtt_parse(f, error, &dict);
		    if (!res)
		      {
			status_str = "parse error";
			problem = true;
			es = -1;
			global_error() << ("error: failed to parse output in "
					   "LBTT format: ")
				       << error << std::endl;
			end_error();
		      }
		  }
		break;
	      }
	    case printable_result_filename::Dstar:
	      {
		spot::dstar_parse_error_list pel;
		std::string filename = output.val()->name();
		spot::dstar_aut* aut;
		aut = spot::dstar_parse(filename, pel, &dict);
		if (!pel.empty())
		  {
		    status_str = "parse error";
		    problem = true;
		    es = -1;
		    std::ostream& err = global_error();
		    err << "error: failed to parse the produced DSTAR"
		      " output.\n";
		    spot::format_dstar_parse_errors(err, filename, pel);
		    end_error();
		    delete aut;
		    res = 0;
		  }
		else
		  {
		    // Gather statistics about the input automaton
		    if (want_stats)
		      {
			statistics* st = &(*fstats)[translator_num];
			st->has_in = true;

			switch (aut->type)
			  {
			  case spot::Rabin:
			    st->in_type = "DRA";
			    break;
			  case spot::Streett:
			    st->in_type = "DSA";
			    break;
			  }

			spot::tgba_sub_statistics s =
			  sub_stats_reachable(aut->aut);
			st->in_states= s.states;
			st->in_edges = s.transitions;
			st->in_transitions = s.sub_transitions;
			st->in_acc = aut->accpair_count;

			spot::scc_map m(aut->aut);
			m.build_map();
			st->in_scc = m.scc_count();
		      }
		    // convert it into TGBA for further processing
		    res = dstar_to_tgba(aut);
		    delete aut;
		  }
		break;
	      }
	    case printable_result_filename::None:
	      assert(!"unreachable code");
	    }
	}

      if (want_stats)
	{
	  statistics* st = &(*fstats)[translator_num];
	  st->status_str = status_str;
	  st->status_code = es;
	  double prec = XTIME_PRECISION;
	  st->time = (after - before) / prec;

	  // Compute statistics.
	  if (res)
	    {
	      st->ok = true;
	      spot::tgba_sub_statistics s = sub_stats_reachable(res);
	      st->states = s.states;
	      st->edges = s.transitions;
	      st->transitions = s.sub_transitions;
	      st->acc = res->number_of_acceptance_conditions();
	      spot::scc_map m(res);
	      m.build_map();
	      unsigned c = m.scc_count();
	      st->scc = c;
	      st->nondetstates = spot::count_nondet_states(res);
	      st->nondeterministic = st->nondetstates != 0;
	      for (unsigned n = 0; n < c; ++n)
		{
		  if (!m.accepting(n))
		    ++st->nonacc_scc;
		  else if (is_terminal_scc(m, n))
		    ++st->terminal_scc;
		  else if (is_weak_scc(m, n))
		    ++st->weak_scc;
		  else
		    ++st->strong_scc;
		}
	      if (st->strong_scc)
		st->strong_aut = true;
	      else if (st->weak_scc)
		st->weak_aut = true;
	      else
		st->terminal_aut = true;
	    }
	}
      output.cleanup();
      return res;
    }
  };

  static bool
  check_empty_prod(const spot::tgba* aut_i, const spot::tgba* aut_j,
		   size_t i, size_t j, bool icomp, bool jcomp)
  {
    spot::tgba_product* prod = new spot::tgba_product(aut_i, aut_j);
    spot::emptiness_check* ec = spot::couvreur99(prod);
    spot::emptiness_check_result* res = ec->check();

    if (res)
      {
	std::ostream& err = global_error();
	err << "error: ";
	if (icomp)
	  err << "Comp(N" << i << ")";
	else
	  err << "P" << i;
	if (jcomp)
	  err << "*Comp(P" << j << ")";
	else
	  err << "*N" << j;
	err << " is nonempty";

	spot::tgba_run* run = res->accepting_run();
	if (run)
	  {
	    const spot::tgba_run* runmin = reduce_run(prod, run);
	    delete run;
	    std::cerr << "; both automata accept the infinite word\n"
		      << "       ";
	    spot::tgba_word w(runmin);
	    w.simplify();
	    w.print(example(), prod->get_dict()) << "\n";
	    delete runmin;
	  }
	else
	  {
	    std::cerr << "\n";
	  }
	end_error();
      }
    delete res;
    delete ec;
    delete prod;
    return res;
  }

  static bool
  cross_check(const std::vector<spot::scc_map*>& maps, char l, unsigned p)
  {
    size_t m = maps.size();

    std::vector<bool> res(m);
    unsigned verified = 0;
    unsigned violated = 0;
    for (size_t i = 0; i < m; ++i)
      if (spot::scc_map* m = maps[i])
	{
	  // r == true iff the automaton i is accepting.
	  bool r = false;
	  unsigned c = m->scc_count();
	  for (unsigned j = 0; (j < c) && !r; ++j)
	    r |= m->accepting(j);
	  res[i] = r;
	  if (r)
	    ++verified;
	  else
	    ++violated;
	}
    if (verified != 0 && violated != 0)
      {
	std::ostream& err = global_error();
	err << "error: {";
	bool first = true;
	for (size_t i = 0; i < m; ++i)
	  if (maps[i] && res[i])
	    {
	      if (first)
		first = false;
	      else
		err << ',';
	      err << l << i;
	    }
	err << "} disagree with {";
	first = true;
	for (size_t i = 0; i < m; ++i)
	  if (maps[i] && !res[i])
	    {
	      if (first)
		first = false;
	      else
		err << ',';
	      err << l << i;
	    }
	err << "} when evaluating ";
	if (products > 1)
	  err << "state-space #" << p << "/" << products << "\n";
	else
	  err << "the state-space\n";
	end_error();
	return true;
      }
    return false;
  }

  typedef std::set<spot::state*, spot::state_ptr_less_than> state_set;

  // Collect all the states of SSPACE that appear in the accepting SCCs
  // of PROD.
  static void
  states_in_acc(const spot::scc_map* m, const spot::tgba* sspace,
		state_set& s)
  {
    const spot::tgba* aut = m->get_aut();
    unsigned c = m->scc_count();
    for (unsigned n = 0; n < c; ++n)
      if (m->accepting(n))
	{
	  const std::list<const spot::state*>& l = m->states_of(n);
	  for (std::list<const spot::state*>::const_iterator i = l.begin();
	       i != l.end(); ++i)
	    {
	      spot::state* x = aut->project_state(*i, sspace);
	      if (!s.insert(x).second)
		x->destroy();
	    }
	}
  }

  static bool
  consistency_check(const spot::scc_map* pos, const spot::scc_map* neg,
		    const spot::tgba* sspace)
  {
    // the states of SSPACE should appear in the accepting SCC of at
    // least one of POS or NEG.  Maybe both.
    state_set s;
    states_in_acc(pos, sspace, s);
    states_in_acc(neg, sspace, s);
    bool res = s.size() == states;
    state_set::iterator it;
    for (it = s.begin(); it != s.end(); ++it)
      (*it)->destroy();
    return res;
  }

  typedef
  Sgi::hash_set<const spot::ltl::formula*,
		const spot::ptr_hash<const spot::ltl::formula> > fset_t;


  class processor: public job_processor
  {
    spot::bdd_dict dict;
    translator_runner runner;
    fset_t unique_set;
  public:
    processor()
      : runner(dict)
    {
    }

    ~processor()
    {
      fset_t::iterator i = unique_set.begin();
      while (i != unique_set.end())
	(*i++)->destroy();
    }

    int
    process_string(const std::string& input,
		   const char* filename,
		   int linenum)
    {
      spot::ltl::parse_error_list pel;
      const spot::ltl::formula* f = parse_formula(input, pel);

      if (!f || !pel.empty())
	{
	  if (filename)
	    error_at_line(0, 0, filename, linenum, "parse error:");
	  spot::ltl::format_parse_errors(std::cerr, input, pel);
	  if (f)
	    f->destroy();
	  return 1;
	}
      int res = process_formula(f, filename, linenum);

      if (res && bogus_output)
	*bogus_output << input << std::endl;
      return 0;
    }


    int
    process_formula(const spot::ltl::formula* f,
		    const char* filename = 0, int linenum = 0)
    {
      static unsigned round = 0;

      // If we need LBT atomic proposition in any of the input or
      // output, relabel the formula.
      if (!f->has_lbt_atomic_props() &&
	  (runner.has('l') || runner.has('L') || runner.has('T')))
	{
	  const spot::ltl::formula* g = spot::ltl::relabel(f, spot::ltl::Pnn);
	  f->destroy();
	  f = g;
	}

      // ---------- Positive Formula ----------

      runner.round_formula(f, round);

      // Call formula() before printing anything else, in case it
      // complains.
      std::string fstr = runner.formula();
      if (filename)
	std::cerr << filename << ":";
      if (linenum)
	std::cerr << linenum << ":";
      if (filename || linenum)
	std::cerr << " ";
      if (color_opt)
	std::cerr << bright_blue;
      std::cerr << fstr << "\n";
      if (color_opt)
	std::cerr << reset_color;

      // Make sure we do not translate the same formula twice.
      if (!allow_dups)
	{
	  if (unique_set.insert(f).second)
	    {
	      f->clone();
	    }
	  else
	    {
	      std::cerr
		<< ("warning: This formula or its negation has already"
		    " been checked.\n         Use --allow-dups if it "
		    "should not be ignored.\n")
		<< std::endl;
	      f->destroy();
	      return 0;
	    }
	}


      int problems = 0;

      // These store the result of the translation of the positive and
      // negative formulas.
      size_t m = translators.size();
      std::vector<const spot::tgba*> pos(m);
      std::vector<const spot::tgba*> neg(m);
      // These store the complement of the above results, when we can
      // compute it easily.
      std::vector<const spot::tgba*> comp_pos(m);
      std::vector<const spot::tgba*> comp_neg(m);


      unsigned n = vstats.size();
      vstats.resize(n + (no_checks ? 1 : 2));
      statistics_formula* pstats = &vstats[n];
      statistics_formula* nstats = 0;
      pstats->resize(m);
      formulas.push_back(fstr);

      for (size_t n = 0; n < m; ++n)
	{
	  bool prob;
	  pos[n] = runner.translate(n, 'P', pstats, prob);
	  problems += prob;

	  // If the automaton is deterministic, compute its complement
	  // as well.  Note that if we have computed statistics
	  // already, there is no need to call is_deterministic()
	  // again.
	  if (!no_complement && pos[n]
	      && ((want_stats && !(*pstats)[n].nondeterministic)
		  || (!want_stats && is_deterministic(pos[n]))))
	    comp_pos[n] = dtgba_complement(pos[n]);
	}

      // ---------- Negative Formula ----------

      // The negative formula is only needed when checks are
      // activated.
      if (!no_checks)
	{
	  nstats = &vstats[n + 1];
	  nstats->resize(m);

	  const spot::ltl::formula* nf =
	    spot::ltl::unop::instance(spot::ltl::unop::Not, f->clone());

	  if (!allow_dups)
	    {
	      bool res = unique_set.insert(nf->clone()).second;
	      // It is not possible to discover that nf has already been
	      // translated, otherwise that would mean that f had been
	      // translated too and we would have caught it before.
	      assert(res);
	      (void) res;
	    }

	  runner.round_formula(nf, round);
	  formulas.push_back(runner.formula());

	  for (size_t n = 0; n < m; ++n)
	    {
	      bool prob;
	      neg[n] = runner.translate(n, 'N', nstats, prob);
	      problems += prob;

	      // If the automaton is deterministic, compute its
	      // complement as well.  Note that if we have computed
	      // statistics already, there is no need to call
	      // is_deterministic() again.
	      if (!no_complement && neg[n]
		  && ((want_stats && !(*nstats)[n].nondeterministic)
		      || (!want_stats && is_deterministic(neg[n]))))
		comp_neg[n] = dtgba_complement(neg[n]);
	    }
	  nf->destroy();
	}

      spot::cleanup_tmpfiles();
      ++round;

      if (!no_checks)
	{
	  std::cerr << "Performing sanity checks and gathering statistics..."
		    << std::endl;

	  // intersection test
	  for (size_t i = 0; i < m; ++i)
	    if (pos[i])
	      for (size_t j = 0; j < m; ++j)
		if (neg[j])
		  {
		    problems +=
		      check_empty_prod(pos[i], neg[j], i, j, false, false);

		    // Deal with the extra complemented automata if we
		    // have some.

		    // If comp_pos[j] and comp_neg[j] exist for the
		    // same j, it means pos[j] and neg[j] were both
		    // deterministic.  In that case, we will want to
		    // make sure that comp_pos[j]*comp_neg[j] is empty
		    // to assert the complementary of pos[j] and
		    // neg[j].  However using comp_pos[j] and
		    // comp_neg[j] against other translator will not
		    // give us any more insight than pos[j] and
		    // neg[j].  So we only do intersection checks with
		    // a complement automata when one of the two
		    // translation was not deterministic.

		    if (i != j && comp_pos[j] && !comp_neg[j])
		      problems +=
			check_empty_prod(pos[i], comp_pos[j],
					 i, j, false, true);
		    if (i != j && comp_neg[i] && !comp_neg[i])
		      problems +=
			check_empty_prod(comp_neg[i], neg[j],
					 i, j, true, false);
		    if (comp_pos[i] && comp_neg[j] &&
			(i == j || (!comp_neg[i] && !comp_pos[j])))
		      problems +=
			check_empty_prod(comp_pos[i], comp_neg[j],
					 i, j, true, true);
		  }
	}
      else
	{
	  std::cerr << "Gathering statistics..." << std::endl;
	}

      spot::ltl::atomic_prop_set* ap = spot::ltl::atomic_prop_collect(f);
      f->destroy();

      if (want_stats)
	for (size_t i = 0; i < m; ++i)
	  {
	    (*pstats)[i].product_states.reserve(products);
	    (*pstats)[i].product_transitions.reserve(products);
	    (*pstats)[i].product_scc.reserve(products);
	    if (neg[i])
	      {
		(*nstats)[i].product_states.reserve(products);
		(*nstats)[i].product_transitions.reserve(products);
		(*nstats)[i].product_scc.reserve(products);
	      }
	  }
      for (unsigned p = 0; p < products; ++p)
	{
	  // build a random state-space.
	  spot::srand(seed);
	  spot::tgba* statespace = spot::random_graph(states, density,
						      ap, &dict);

	  // Products of the state space with the positive automata.
	  std::vector<spot::tgba*> pos_prod(m);
	  // Products of the state space with the negative automata.
	  std::vector<spot::tgba*> neg_prod(m);
	  // Associated SCC maps.
	  std::vector<spot::scc_map*> pos_map(m);
	  std::vector<spot::scc_map*> neg_map(m);
	  for (size_t i = 0; i < m; ++i)
	    if (pos[i])
	      {
		spot::tgba* p = new spot::tgba_product(pos[i], statespace);
		pos_prod[i] = p;
		spot::scc_map* sm = new spot::scc_map(p);
		sm->build_map();
		pos_map[i] = sm;

		// Statistics
		if (want_stats)
		  {
		    (*pstats)[i].product_scc.push_back(sm->scc_count());
		    spot::tgba_statistics s = spot::stats_reachable(p);
		    (*pstats)[i].product_states.push_back(s.states);
		    (*pstats)[i].product_transitions.push_back(s.transitions);
		  }
	      }

	  if (!no_checks)
	    for (size_t i = 0; i < m; ++i)
	      if (neg[i])
		{
		  spot::tgba* p = new spot::tgba_product(neg[i], statespace);
		  neg_prod[i] = p;
		  spot::scc_map* sm = new spot::scc_map(p);
		  sm->build_map();
		  neg_map[i] = sm;

		  // Statistics
		  if (want_stats)
		    {
		      (*nstats)[i].product_scc.push_back(sm->scc_count());
		      spot::tgba_statistics s = spot::stats_reachable(p);
		      (*nstats)[i].product_states.push_back(s.states);
		      (*nstats)[i].product_transitions.push_back(s.transitions);
		    }
		}

	  if (!no_checks)
	    {
	      // cross-comparison test
	      problems += cross_check(pos_map, 'P', p);
	      problems += cross_check(neg_map, 'N', p);

	      // consistency check
	      for (size_t i = 0; i < m; ++i)
		if (pos_map[i] && neg_map[i] &&
		    !(consistency_check(pos_map[i], neg_map[i], statespace)))
		  {
		    ++problems;

		    std::ostream& err = global_error();
		    err << "error: inconsistency between P" << i
			<< " and N" << i;
		    if (products > 1)
		      err << " for state-space #" << p
			  << "/" << products << "\n";
		    else
		      err << "\n";
		    end_error();
		  }
	    }

	  // Cleanup.
	  if (!no_checks)
	    for (size_t i = 0; i < m; ++i)
	      {
		delete neg_map[i];
		delete neg_prod[i];
	      }
	  for (size_t i = 0; i < m; ++i)
	    {
	      delete pos_map[i];
	      delete pos_prod[i];
	    }
	  delete statespace;
	  ++seed;
	}
      std::cerr << std::endl;
      delete ap;

      if (!no_checks)
	for (size_t i = 0; i < m; ++i)
	  {
	    delete neg[i];
	    delete comp_neg[i];
	    delete comp_pos[i];
	  }
      for (size_t i = 0; i < m; ++i)
	delete pos[i];

      // Shall we stop processing formulas now?
      abort_run = global_error_flag && stop_on_error;
      return problems;
    }
  };
}

// Output an RFC4180-compatible CSV file.
static void
print_stats_csv(const char* filename)
{
  std::ofstream* outfile = 0;
  std::ostream* out;
  if (!strncmp(filename, "-", 2))
    {
      out = &std::cout;
    }
  else
    {
      out = outfile = new std::ofstream(filename);
      if (!*outfile)
	error(2, errno, "cannot open '%s'", filename);
    }

  unsigned ntrans = translators.size();
  unsigned rounds = vstats.size();
  assert(rounds == formulas.size());

  *out << "\"formula\",\"tool\",";
  statistics::fields(*out, !opt_omit, has_sr);
  *out << '\n';
  for (unsigned r = 0; r < rounds; ++r)
    for (unsigned t = 0; t < ntrans; ++t)
      if (!opt_omit || vstats[r][t].ok)
	{
	  *out << '"';
	  spot::escape_rfc4180(*out, formulas[r]);
	  *out << "\",\"";
	  spot::escape_rfc4180(*out, translators[t].name);
	  *out << "\",";
	  vstats[r][t].to_csv(*out, !opt_omit, has_sr);
	  *out << '\n';
	}
  delete outfile;
}

static void
print_stats_json(const char* filename)
{
  std::ofstream* outfile = 0;
  std::ostream* out;
  if (!strncmp(filename, "-", 2))
    {
      out = &std::cout;
    }
  else
    {
      out = outfile = new std::ofstream(filename);
      if (!*outfile)
	error(2, errno, "cannot open '%s'", filename);
    }

  unsigned ntrans = translators.size();
  unsigned rounds = vstats.size();
  assert(rounds == formulas.size());

  *out << "{\n  \"tool\": [\n    \"";
  spot::escape_str(*out, translators[0].name);
  for (unsigned t = 1; t < ntrans; ++t)
    {
      *out << "\",\n    \"";
      spot::escape_str(*out, translators[t].name);
    }
  *out << "\"\n  ],\n  \"formula\": [\n    \"";
  spot::escape_str(*out, formulas[0]);
  for (unsigned r = 1; r < rounds; ++r)
    {
      *out << "\",\n    \"";
      spot::escape_str(*out, formulas[r]);
    }
  *out << ("\"\n  ],\n  \"fields\":  [\n  \"formula\",\"tool\",");
  statistics::fields(*out, !opt_omit, has_sr);
  *out << "\n  ],\n  \"inputs\":  [ 0, 1 ],";
  *out << "\n  \"results\": [";
  bool notfirst = false;
  for (unsigned r = 0; r < rounds; ++r)
    for (unsigned t = 0; t < ntrans; ++t)
      if (!opt_omit || vstats[r][t].ok)
	{
	  if (notfirst)
	    *out << ',';
	  notfirst = true;
	  *out << "\n    [ " << r << ',' << t << ',';
	  vstats[r][t].to_csv(*out, !opt_omit, has_sr, "null");
	  *out << " ]";
	}
  *out << "\n  ]\n}\n";

  delete outfile;
}

int
main(int argc, char** argv)
{
  setup(argv);

  const argp ap = { options, parse_opt, "[COMMANDFMT...]",
		    argp_program_doc, children, 0, 0 };

  if (int err = argp_parse(&ap, argc, argv, ARGP_NO_HELP, 0, 0))
    exit(err);

  if (jobs.empty())
    jobs.push_back(job("-", 1));

  if (translators.empty())
    error(2, 0, "No translator to run?  Run '%s --help' for usage.",
	  program_name);

  if (color_opt == color_if_tty)
    color_opt = isatty(STDERR_FILENO) ? color_always : color_never;

  setup_sig_handler();

  processor p;
  if (p.run())
    return 2;

  if (formulas.empty())
    {
      error(2, 0, "no formula to translate");
    }
  else
    {
      if (global_error_flag)
	{
	  std::ostream& err = global_error();
	  if (bogus_output)
	    err << ("error: some error was detected during the above runs.\n"
		    "       Check file ")
		<< bogus_output_filename
		<< " for problematic formulas.";
	  else
	    err << ("error: some error was detected during the above runs,\n"
		    "       please search for 'error:' messages in the above"
		    " trace.");
	  err << std::endl;
	  if (timeout_count == 1)
	    err << "Additionally, 1 timeout occurred." << std::endl;
	  else if (timeout_count > 1)
	    err << "Additionally, "
		<< timeout_count << " timeouts occurred." << std::endl;
	  end_error();
	}
      else if (timeout_count == 0)
	std::cerr << "No problem detected." << std::endl;
      else if (timeout_count == 1)
	std::cerr << "1 timeout, but no other problem detected." << std::endl;
      else
	std::cerr << timeout_count
		  << " timeouts, but no other problem detected." << std::endl;
    }

  delete bogus_output;

  if (json_output)
    print_stats_json(json_output);
  if (csv_output)
    print_stats_csv(csv_output);

  return global_error_flag;
}
