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

#include "common_setup.hh"
#include "common_cout.hh"
#include "common_finput.hh"
#include "neverparse/public.hh"
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
#include "misc/formater.hh"
#include "tgbaalgos/stats.hh"
#include "tgbaalgos/isdet.hh"
#include "misc/escape.hh"
#include "misc/hash.hh"

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

static const argp_option options[] =
  {
    /**************************************************/
    { 0, 0, 0, 0, "Specifying translator to call:", 2 },
    { "translator", 't', "COMMANDFMT", 0,
      "register one translators to call", 0 },
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
    { "%N,%T", 0, 0, OPTION_DOC | OPTION_NO_USAGE,
      "the output automaton as a Never claim, or in LBTT's format", 0 },
    { 0, 0, 0, 0,
      "If either %l, %L, or %T are used, any input formula that does "
      "not use LBT-style atomic propositions (i.e. p0, p1, ...) will be "
      "relabeled automatically.", 0 },
    /**************************************************/
    { 0, 0, 0, 0, "ltlcross behavior:", 4 },
    { "allow-dups", OPT_DUPS, 0, 0,
      "translate duplicate formulas in input", 0 },
    { "no-checks", OPT_NOCHECKS, 0, 0,
      "do not perform any sanity checks (negated formulas "
      "will not be translated)", 0 },
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
    /**************************************************/
    { 0, 0, 0, 0, "Statistics output:", 6 },
    { "json", OPT_JSON, "FILENAME", OPTION_ARG_OPTIONAL,
      "output statistics as JSON in FILENAME or on standard output", 0 },
    { "csv", OPT_CSV, "FILENAME", OPTION_ARG_OPTIONAL,
      "output statistics as CSV in FILENAME or on standard output", 0 },
    /**************************************************/
    { 0, 0, 0, 0, "Miscellaneous options:", -1 },
    { 0, 0, 0, 0, 0, 0 }
  };

const struct argp_child children[] =
  {
    { &finput_argp, 0, 0, 1 },
    { &misc_argp, 0, 0, -1 },
    { 0, 0, 0, 0 }
  };

unsigned states = 200;
float density = 0.1;
unsigned timeout = 0;
const char* json_output = 0;
const char* csv_output = 0;
bool want_stats = false;
bool allow_dups = false;
bool no_checks = false;
bool stop_on_error = false;

std::vector<char*> translators;
bool global_error_flag = false;

static std::ostream&
global_error()
{
  global_error_flag = true;
  return std::cerr;
}

struct statistics
{
  bool ok;
  unsigned states;
  unsigned edges;
  unsigned transitions;
  unsigned acc;
  unsigned scc;
  unsigned nondetstates;
  bool nondeterministic;
  double time;
  unsigned product_states;
  unsigned product_transitions;
  unsigned product_scc;

  static void
  fields(std::ostream& os)
  {
    os << (" \"states\","
	   " \"edges\","
	   " \"transitions\","
	   " \"acc\","
	   " \"scc\","
	   " \"nondetstates\","
	   " \"nondeterministic\","
	   " \"time\","
	   " \"product_states\","
	   " \"product_transitions\","
	   " \"product_scc\"");
  }

  void
  to_csv(std::ostream& os)
  {
    os << states << ", "
       << edges << ", "
       << transitions << ", "
       << acc << ", "
       << scc << ", "
       << nondetstates << ", "
       << nondeterministic << ", "
       << time << ", "
       << product_states << ", "
       << product_transitions << ", "
       << product_scc;
  }
};

typedef std::vector<statistics> statistics_formula;
typedef std::vector<statistics_formula> statistics_vector;
statistics_vector vstats;
std::vector<std::string> formulas;

// Cleanup temporary files.
std::list<std::string> toclean;
void
cleanup()
{
  for (std::list<std::string>::const_iterator i = toclean.begin();
       i != toclean.end(); ++i)
    unlink(i->c_str());
  toclean.clear();
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
    case OPT_NOCHECKS:
      no_checks = true;
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

static int
create_tmpfile(char c, unsigned int n, std::string& name)
{
  char tmpname[30];
  snprintf(tmpname, sizeof tmpname, "lck-%c%u-XXXXXX", c, n);
  int fd = mkstemp(tmpname);
  if (fd == -1)
    error(2, errno, "failed to create a temporary file");
  name = tmpname;
  return fd;
}


static volatile bool timed_out = false;

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
      cleanup();
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

  struct printable_result_filename: public spot::printable_value<std::string>
  {
    unsigned translator_num;
    enum output_format { None, Spin, Lbtt };
    mutable output_format format;

    void reset(unsigned n)
    {
      val_.clear();
      translator_num = n;
      format = None;
    }

    void
    print(std::ostream& os, const char* pos) const
    {
      if (*pos == 'N')
	format = Spin;
      else
	format = Lbtt;
      if (!val_.empty())
	error(2, 0, "you may have only one %%N or %%T specifier: %s",
	      translators[translator_num]);
      close(create_tmpfile('o', translator_num,
			   const_cast<std::string&>(val_)));
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
      declare('N', &output);
      declare('T', &output);

      size_t s = translators.size();
      assert(s);
      for (size_t n = 0; n < s; ++n)
	prime(translators[n]);

    }

    void
    string_to_tmp(std::string& str, unsigned n, std::string& tmpname)
    {
      int fd = create_tmpfile('i', n, tmpname);
      write(fd, str.c_str(), str.size());
      write(fd, "\n", 1);
      close(fd);
      toclean.push_back(tmpname);
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
      error(2, 0, "None of the translators need the input formula?");
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
    translate(unsigned int translator_num, char l, statistics_formula* fstats)
    {
      output.reset(translator_num);

      std::ostringstream command;
      format(command, translators[translator_num]);
      toclean.push_back(output.val());

      if (output.format == printable_result_filename::None)
	error(2, 0, "no output sequence used in %s",
	      translators[translator_num]);

      std::string cmd = command.str();
      std::cerr << "Running [" << l << translator_num << "]: "
		<< cmd << std::endl;
      xtime_t before = gethrxtime();
      int es = exec_with_timeout(cmd.c_str());
      xtime_t after = gethrxtime();

      const spot::tgba* res = 0;
      if (timed_out)
	{
	  // This is not considered to be a global error.
	  std::cerr << "warning: timeout during execution of command\n";
	}
      else if (WIFSIGNALED(es))
	{
	  global_error() << "error: execution terminated by signal "
			 << WTERMSIG(es) << ".\n";
	}
      else if (WIFEXITED(es) && WEXITSTATUS(es) != 0)
	{
	  global_error() << "error: execution returned exit code "
			 << WEXITSTATUS(es) << ".\n";
	}
      else
	{
	  switch (output.format)
	    {
	    case printable_result_filename::Spin:
	      {
		spot::neverclaim_parse_error_list pel;
		res = spot::neverclaim_parse(output, pel, &dict);
		if (!pel.empty())
		  {
		    std::ostream& err = global_error();
		    err << "error: failed to parse the produced neverclaim.\n";
		    spot::format_neverclaim_parse_errors(err, output, pel);
		    delete res;
		    res = 0;
		  }
		break;
	      }
	    case printable_result_filename::Lbtt:
	      {
		std::string error;
		std::ifstream f(output.val().c_str());
		if (!f)
		  {
		    global_error() << "Cannot open " << output.val()
				   << std::endl;
		    global_error_flag = true;
		  }
		else
		  {
		    res = spot::lbtt_parse(f, error, &dict);
		    if (!res)
		      global_error() << ("error: failed to parse output in "
					 "LBTT format: ")
				     << error << std::endl;
		  }
		break;
	      }
	    case printable_result_filename::None:
	      assert(!"unreachable code");
	    }
	}
      // Compute statistics.
      if (res && want_stats)
	{
	  statistics* st = &(*fstats)[translator_num];
	  st->ok = true;
	  spot::tgba_sub_statistics s = sub_stats_reachable(res);
	  st->states = s.states;
	  st->edges = s.transitions;
	  st->transitions = s.sub_transitions;
	  st->acc = res->number_of_acceptance_conditions();
	  spot::scc_map m(res);
	  m.build_map();
	  st->scc = m.scc_count();
	  st->nondetstates = spot::count_nondet_states(res);
	  st->nondeterministic = st->nondetstates != 0;
          double prec = XTIME_PRECISION;
	  st->time = (after - before) / prec;
	}
      return res;
    }
  };

  static bool
  is_empty(const spot::tgba* aut)
  {
    spot::emptiness_check* ec = spot::couvreur99(aut);
    spot::emptiness_check_result* res = ec->check();
    delete res;
    delete ec;
    return !res;
  }

  static void
  cross_check(const std::vector<spot::scc_map*>& maps, char l)
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
		err << ",";
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
		err << ",";
	      err << l << i;
	    }
	err << "} when evaluating the state-space\n";
      }
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
    for (it = s.begin(); it != s.end(); it++)
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
    process_formula(const spot::ltl::formula* f,
		    const char* filename = 0, int linenum = 0)
    {
      (void) filename;
      (void) linenum;
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
      std::cerr << fstr << "\n";

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

      size_t m = translators.size();
      std::vector<const spot::tgba*> pos(m);
      std::vector<const spot::tgba*> neg(m);


      unsigned n = vstats.size();
      vstats.resize(n + (no_checks ? 1 : 2));
      statistics_formula* pstats = &vstats[n];
      statistics_formula* nstats = 0;
      pstats->resize(m);
      formulas.push_back(fstr);

      for (size_t n = 0; n < m; ++n)
	pos[n] = runner.translate(n, 'P', pstats);

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
	    neg[n] = runner.translate(n, 'N', nstats);
	  nf->destroy();
	}

      f->destroy();
      cleanup();
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
		    spot::tgba_product* prod =
		      new spot::tgba_product(pos[i], neg[j]);
		    if (!is_empty(prod))
		      global_error() << "error: P" << i << "*N" << j
				     << " is nonempty\n";
		    delete prod;
		  }
	}
      else
	{
	  std::cerr << "Gathering statistics..." << std::endl;
	}

      // build products with a random state-space.
      spot::ltl::atomic_prop_set* ap = spot::ltl::atomic_prop_collect(f);
      spot::tgba* statespace = spot::random_graph(states, density, ap, &dict);
      delete ap;

      std::vector<spot::tgba*> pos_prod(m);
      std::vector<spot::tgba*> neg_prod(m);
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
		(*pstats)[i].product_scc = sm->scc_count();
		spot::tgba_statistics s = spot::stats_reachable(p);
		(*pstats)[i].product_states = s.states;
		(*pstats)[i].product_transitions = s.transitions;
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
		  (*nstats)[i].product_scc = sm->scc_count();
		  spot::tgba_statistics s = spot::stats_reachable(p);
		  (*nstats)[i].product_states = s.states;
		  (*nstats)[i].product_transitions = s.transitions;
		}
	    }

      if (!no_checks)
	{
	  // cross-comparison test
	  cross_check(pos_map, 'P');
	  cross_check(neg_map, 'N');

	  // consistency check
	  for (size_t i = 0; i < m; ++i)
	    if (pos_map[i] && neg_map[i] &&
		!(consistency_check(pos_map[i], neg_map[i], statespace)))
	      global_error() << "error: inconsistency between P" << i
			     << " and N" << i << "\n";
	}

      // Cleanup.

      if (!no_checks)
	for (size_t n = 0; n < m; ++n)
	{
	  delete neg_map[n];
	  delete neg_prod[n];
	  delete neg[n];
	}
      for (size_t n = 0; n < m; ++n)
	{
	  delete pos_map[n];
	  delete pos_prod[n];
	  delete pos[n];
	}
      delete statespace;
      std::cerr << std::endl;

      // Shall we stop processing formulas now?
      abort_run = global_error_flag && stop_on_error;
      return 0;
    }
  };
}

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
      if (!outfile)
	error(2, errno, "cannot open '%s'", filename);
    }

  unsigned ntrans = translators.size();
  unsigned rounds = vstats.size();
  assert(rounds = formulas.size());

  *out << "\"formula\", \"tool\", ";
  statistics::fields(*out);
  *out << "\n";
  for (unsigned r = 0; r < rounds; ++r)
    for (unsigned t = 0; t < ntrans; ++t)
      if (vstats[r][t].ok)
	{
	  *out << "\"";
	  spot::escape_str(*out, formulas[r]);
	  *out << "\", \"";
	  spot::escape_str(*out, translators[t]);
	  *out << "\", ";
	  vstats[r][t].to_csv(*out);
	  *out << "\n";
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
      if (!outfile)
	error(2, errno, "cannot open '%s'", filename);
    }

  unsigned ntrans = translators.size();
  unsigned rounds = vstats.size();
  assert(rounds = formulas.size());

  *out << "{\n  \"tools\": [\n    \"";
  spot::escape_str(*out, translators[0]);
  for (unsigned t = 1; t < ntrans; ++t)
    {
      *out << "\",\n    \"";
      spot::escape_str(*out, translators[t]);
    }
  *out << "\"\n  ],\n  \"inputs\": [\n    \"";
  spot::escape_str(*out, formulas[0]);
  for (unsigned r = 1; r < rounds; ++r)
    {
      *out << "\",\n    \"";
      spot::escape_str(*out, formulas[r]);
    }
  *out << ("\"\n  ],\n  \"fields\": [\n    \"input\", \"tool\",");
  statistics::fields(*out);
  *out << "\n  ],\n  \"results\": [";
  bool notfirst = false;
  for (unsigned r = 0; r < rounds; ++r)
    for (unsigned t = 0; t < ntrans; ++t)
      if (vstats[r][t].ok)
	{
	  if (notfirst)
	    *out << ",";
	  notfirst = true;
	  *out << "\n    [ " << r << ", " << t << ", ";
	  vstats[r][t].to_csv(*out);
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
	std::cerr
	  << ("error: some error was detected during the above runs,\n"
	      "       please search for 'error:' messages in the above trace.")
	  << std::endl;
	else
	  std::cerr << "no problem detected" << std::endl;
    }

  if (json_output)
    print_stats_json(json_output);
  if (csv_output)
    print_stats_csv(csv_output);

  return global_error_flag;
}
