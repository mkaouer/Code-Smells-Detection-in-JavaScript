// Copyright (C) 2008, 2009, 2010 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2004, 2005 Laboratoire d'Informatique de Paris
// 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
//
// This file is part of Spot, a model checking library.
//
// Spot is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// Spot is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Spot; see the file COPYING.  If not, write to the Free
// Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
// 02111-1307, USA.

#include <cassert>
#include <cstdlib>
#include <iostream>
#include <iomanip>
#include <sstream>
#include <fstream>
#include <string>
#include <utility>
#include <set>
#include <vector>
#include "ltlparse/public.hh"
#include "ltlvisit/apcollect.hh"
#include "ltlvisit/randomltl.hh"
#include "ltlvisit/tostring.hh"
#include "ltlvisit/length.hh"
#include "ltlvisit/reduce.hh"
#include "tgbaalgos/randomgraph.hh"
#include "tgbaalgos/save.hh"
#include "tgbaalgos/stats.hh"
#include "ltlenv/defaultenv.hh"
#include "ltlast/atomic_prop.hh"
#include "tgbaalgos/dotty.hh"
#include "tgbaparse/public.hh"
#include "misc/random.hh"
#include "misc/optionmap.hh"
#include "tgba/tgbatba.hh"
#include "tgba/tgbaproduct.hh"
#include "misc/timer.hh"

#include "tgbaalgos/ltl2tgba_fm.hh"

#include "tgbaalgos/emptiness.hh"
#include "tgbaalgos/emptiness_stats.hh"
#include "tgbaalgos/reducerun.hh"
#include "tgbaalgos/replayrun.hh"

struct ec_algo
{
  std::string name;
  spot::emptiness_check_instantiator* inst;
};

const char* default_algos[] = {
  "Cou99(!poprem)",
  "Cou99(!poprem shy !group)",
  "Cou99(!poprem shy group)",
  "Cou99(poprem)",
  "Cou99(poprem shy !group)",
  "Cou99(poprem shy group)",
  "CVWY90",
  "CVWY90(bsh=4K)",
  "GV04",
  "SE05",
  "SE05(bsh=4K)",
  "Tau03",
  "Tau03_opt",
  "Tau03_opt(condstack)",
  "Tau03_opt(condstack ordering)",
  "Tau03_opt(condstack ordering !weights)",
  0
};

std::vector<ec_algo> ec_algos;

spot::emptiness_check*
cons_emptiness_check(int num, const spot::tgba* a,
		     const spot::tgba* degen, unsigned int n_acc)
{
  spot::emptiness_check_instantiator* inst = ec_algos[num].inst;
  if (n_acc < inst->min_acceptance_conditions()
      || n_acc > inst->max_acceptance_conditions())
    a = degen;
  if (a)
    return inst->instantiate(a);
  return 0;
}

void
syntax(char* prog)
{
  std::cerr << "Usage: "<< prog << " [OPTIONS...] PROPS..." << std::endl
	    << std::endl
	    << "General Options:" << std::endl
	    << "  -0      suppress default output, just generate the graph"
	    << " in memory" << std::endl
	    << "  -1      produce minimal output (for our paper)" << std::endl
	    << "  -g      output graph in dot format" << std::endl
	    << "  -s N    seed for the random number generator" << std::endl
	    << "  -z      display statistics about emptiness-check algorithms"
	    << std::endl
	    << "  -Z      like -z, but print extra statistics after the run"
	    << " of each algorithm" << std::endl
	    << std::endl
	    << "Graph Generation Options:" << std::endl
	    << "  -a N F  number of acceptance conditions and probability that"
	    << " one is true" << std::endl
	    << "            [0 0.0]" << std::endl
	    << "  -d F    density of the graph [0.2]" << std::endl
	    << "  -n N    number of nodes of the graph [20]" << std::endl
	    << "  -t F    probability of the atomic propositions to be true"
	    << " [0.5]" << std::endl
	    << std::endl
	    << "LTL Formula Generation Options:" << std::endl
	    << "  -dp     dump priorities, do not generate any formula"
	    << std::endl
	    << "  -f N    size of the formula [15]" << std::endl
	    << "  -F N    number of formulae to generate [0]" << std::endl
	    << "  -l N    simplify formulae using all available reductions"
	    << " and reject those" << std::endl
	    << "            strictly smaller than N" << std::endl
	    << "  -i FILE do not generate formulae, read them from FILE"
	    << std::endl
	    << "  -p S    priorities to use" << std::endl
	    << "  -S N    skip N formulae before starting to use them"
	    << std::endl
	    << "            (useful to replay a specific seed when -u is used)"
	    << std::endl
	    << "  -u      generate unique formulae" << std::endl
	    << std::endl
	    << "Emptiness-Check Options:" << std::endl
	    << "  -A FILE use all algorithms listed in FILE" << std::endl
	    << "  -D      degeneralize TGBA for emptiness-check algorithms that"
	    << " would" << std::endl
	    << "            otherwise be skipped (implies -e)" << std::endl
	    << "  -e N    compare result of all "
	    << "emptiness checks on N randomly generated graphs" << std::endl
	    << "  -H      halt on the first statistic difference in algorithms"
	    << std::endl
	    << "  -m      try to reduce runs, in a second pass (implies -r)"
            << std::endl
	    << "  -R N    repeat each emptiness-check and accepting run "
	    << "computation N times" << std::endl
	    << "  -r      compute and replay accepting runs (implies -e)"
	    << std::endl
	    << "  ar:MODE select the mode MODE for accepting runs computation "
            << "(implies -r)" << std::endl
	    << std::endl
	    << "Where:" << std::endl
	    << "  F      are floats between 0.0 and 1.0 inclusive" << std::endl
	    << "  E      are floating values" << std::endl
	    << "  S      are `KEY=E, KEY=E, ...' strings" << std::endl
	    << "  N      are positive integers" << std::endl
	    << "  PROPS  are the atomic properties to use on transitions"
	    << std::endl
	    << "Use -dp to see the list of KEYs." << std::endl
	    << std::endl
	    << "When -F or -i is used, a random graph a synchronized with"
	    << " each formula." << std::endl << "If -e N is additionally used"
	    << " N random graphs are generated for each formula." << std::endl;
  exit(2);
}


int
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
to_int_pos(const char* s, const char* arg)
{
  int res = to_int(s);
  if (res <= 0)
    {
      std::cerr << "argument of " << arg
		<< " (" << res << ") must be positive" << std::endl;
      exit(1);
    }
  return res;
}

int
to_int_nonneg(const char* s, const char* arg)
{
  int res = to_int(s);
  if (res < 0)
    {
      std::cerr << "argument of " << arg
		<< " (" << res << ") must be nonnegative" << std::endl;
      exit(1);
    }
  return res;
}

float
to_float(const char* s)
{
  char* endptr;
  // Do not use strtof(), it does not exist on Solaris 9.
  float res = strtod(s, &endptr);
  if (*endptr)
    {
      std::cerr << "Failed to parse `" << s << "' as a float." << std::endl;
      exit(1);
    }
  return res;
}

float
to_float_nonneg(const char* s, const char* arg)
{
  float res = to_float(s);
  if (res < 0)
    {
      std::cerr << "argument of " << arg
		<< " (" << res << ") must be nonnegative" << std::endl;
      exit(1);
    }
  return res;
}

// Convertors using for statistics:

template <typename T>
T
id(const char*, unsigned x)
{
  return static_cast<T>(x);
}

spot::tgba_statistics prod_stats;

float
prod_conv(const char* name, unsigned x)
{
  float y = static_cast<float>(x);
  if (!strcmp(name, "transitions"))
    return y / prod_stats.transitions * 100.0;
  return y / prod_stats.states * 100.0;
}

template <typename T, T (*convertor)(const char*, unsigned) = id<T> >
struct stat_collector
{
  struct one_stat
  {
    T min;
    T max;
    T tot;
    unsigned n;

    one_stat()
      : n(0)
    {
    }

    void
    count(T val)
    {
      if (n++)
	{
	  min = std::min(min, val);
	  max = std::max(max, val);
	  tot += val;
	}
      else
	{
	  max = min = tot = val;
	}
    }
  };

  typedef std::map<std::string, one_stat> alg_1stat_map;
  typedef std::map<std::string, alg_1stat_map> stats_alg_map;
  stats_alg_map stats;

  bool
  empty()
  {
    return stats.empty();
  }

  void
  count(const std::string& algorithm, const spot::unsigned_statistics* s)
  {
    if (!s)
      return;
    spot::unsigned_statistics::stats_map::const_iterator i;
    for (i = s->stats.begin(); i != s->stats.end(); ++i)
      stats[i->first][algorithm].count(convertor(i->first, (s->*i->second)()));
  }

  std::ostream&
  display(std::ostream& os,
	  const alg_1stat_map& m, const std::string title,
	  bool total = true) const
  {
    std::ios::fmtflags old = os.flags();
    os << std::setw(25) << "" << " | "
       << std::setw(30) << std::left << title << std::right << "|" << std::endl
       << std::setw(25) << "algorithm"
       << " |   min   < mean  < max | total |  n"
       << std::endl
       << std::setw(64) << std::setfill('-') << "" << std::setfill(' ')
       << std::endl;
    os << std::right << std::fixed << std::setprecision(1);
    for (typename alg_1stat_map::const_iterator i = m.begin();
	 i != m.end(); ++i)
      {
	os << std::setw(25) << i->first << " |"
	   << std::setw(6) << i->second.min
	   << " "
	   << std::setw(8)
	   << static_cast<float>(i->second.tot) / i->second.n
	   << " "
	   << std::setw(6) << i->second.max
	   << " |";
	if (total)
	  os << std::setw(6) << i->second.tot;
	else
	  os << "      ";
	os << " |"
	   << std::setw(4) << i->second.n
	   << std::endl;
      }
    os << std::setw(64) << std::setfill('-') << "" << std::setfill(' ')
       << std::endl;
    os << std::setiosflags(old);
    return os;
  }

  std::ostream&
  display(std::ostream& os, bool total = true) const
  {
    typename stats_alg_map::const_iterator i;
    for (i = stats.begin(); i != stats.end(); ++i)
      display(os, i->second, i->first, total);
    return os;
  }


};

struct ar_stat
{
  int min_prefix;
  int max_prefix;
  int tot_prefix;
  int min_cycle;
  int max_cycle;
  int tot_cycle;
  int min_run;
  int max_run;
  int n;

  ar_stat()
    : n(0)
  {
  }

  void
  count(const spot::tgba_run* run)
  {
    int p = run->prefix.size();
    int c = run->cycle.size();
    if (n++)
      {
	min_prefix = std::min(min_prefix, p);
	max_prefix = std::max(max_prefix, p);
	tot_prefix += p;
	min_cycle = std::min(min_cycle, c);
	max_cycle = std::max(max_cycle, c);
	tot_cycle += c;
	min_run = std::min(min_run, c + p);
	max_run = std::max(max_run, c + p);
      }
    else
      {
	min_prefix = max_prefix = tot_prefix = p;
	min_cycle = max_cycle = tot_cycle = c;
	min_run = max_run = c + p;
      }
  }
};

stat_collector<unsigned> sc_ec;
stat_collector<unsigned> sc_arc;

typedef stat_collector<float, prod_conv> ec_ratio_stat_type;
ec_ratio_stat_type glob_ec_ratio_stats;
typedef std::map<int, ec_ratio_stat_type > ec_ratio_stats_type;
ec_ratio_stats_type ec_ratio_stats;

ec_ratio_stat_type arc_ratio_stats;

typedef std::map<std::string, ar_stat> ar_stats_type;
ar_stats_type ar_stats;		// Statistics about accepting runs.
ar_stats_type mar_stats;        // ... about minimized accepting runs.


void
print_ar_stats(ar_stats_type& ar_stats, const std::string s)
{
  std::ios::fmtflags old = std::cout.flags();
  std::cout << std::endl << s << std::endl;
  std::cout << std::right << std::fixed << std::setprecision(1);

  std::cout << std::setw(25) << ""
            << " |         prefix        |         cycle         |"
            << std::endl
            << std::setw(25) << "algorithm"
            << " |   min   < mean  < max |   min   < mean  < max |   n"
            << std::endl
            << std::setw(79) << std::setfill('-') << "" << std::setfill(' ')
            << std::endl;
  for (ar_stats_type::const_iterator i = ar_stats.begin();
	   i != ar_stats.end(); ++i)
    std::cout << std::setw(25) << i->first << " |"
              << std::setw(6) << i->second.min_prefix
              << " "
              << std::setw(8)
              << static_cast<float>(i->second.tot_prefix) / i->second.n
              << " "
              << std::setw(6) << i->second.max_prefix
              << " |"
              << std::setw(6) << i->second.min_cycle
              << " "
              << std::setw(8)
              << static_cast<float>(i->second.tot_cycle) / i->second.n
              << " "
              << std::setw(6) << i->second.max_cycle
              << " |"
              << std::setw(4) << i->second.n
              << std::endl;
  std::cout << std::setw(79) << std::setfill('-') << "" << std::setfill(' ')
            << std::endl
            << std::setw(25) << ""
            << " |          runs         |         total         |"
            << std::endl <<
	std::setw(25) << "algorithm"
            << " |   min   < mean  < max |  pre.   cyc.     runs |   n"
            << std::endl
            << std::setw(79) << std::setfill('-') << "" << std::setfill(' ')
            << std::endl;
  for (ar_stats_type::const_iterator i = ar_stats.begin();
	   i != ar_stats.end(); ++i)
    std::cout << std::setw(25) << i->first << " |"
              << std::setw(6)
              << i->second.min_run
              << " "
              << std::setw(8)
              << static_cast<float>(i->second.tot_prefix
                                    + i->second.tot_cycle) / i->second.n
              << " "
              << std::setw(6)
              << i->second.max_run
              << " |"
              << std::setw(6) << i->second.tot_prefix
              << " "
              << std::setw(6) << i->second.tot_cycle
              << " "
              << std::setw(8) << i->second.tot_prefix + i->second.tot_cycle
              << " |"
              << std::setw(4) << i->second.n
              << std::endl;
  std::cout << std::setiosflags(old);
}

spot::ltl::formula*
generate_formula(const spot::ltl::random_ltl& rl, int opt_f, int opt_s,
                 int opt_l = 0, bool opt_u = false)
{
  static std::set<std::string> unique;

  int max_tries_u = 1000;
  while (max_tries_u--)
    {
      spot::srand(opt_s++);
      spot::ltl::formula* f;
      int max_tries_l = 1000;
      while (max_tries_l--)
        {
          f = rl.generate(opt_f);
          if (opt_l)
            {
              spot::ltl::formula* g = reduce(f);
              f->destroy();
              if (spot::ltl::length(g) < opt_l)
                {
                  g->destroy();
                  continue;
                }
              f = g;
            }
          else
            {
              assert(spot::ltl::length(f) <= opt_f);
            }
          break;
        }
      if (max_tries_l < 0)
        {
          assert(opt_l);
          std::cerr << "Failed to generate non-reducible formula "
                    << "of size " << opt_l << " or more." << std::endl;
          return 0;
        }
      std::string txt = spot::ltl::to_string(f);
      if (!opt_u || unique.insert(txt).second)
        {
          return f;
        }
      f->destroy();
    }
  assert(opt_u);
  std::cerr << "Failed to generate another unique formula."
            << std::endl;
  return 0;
}

int
main(int argc, char** argv)
{
  bool opt_paper = false;
  bool opt_dp = false;
  int opt_f = 15;
  int opt_F = 0;
  char* opt_p = 0;
  char* opt_i = 0;
  std::istream *formula_file = 0;
  int opt_l = 0;
  bool opt_u = false;
  int opt_S = 0;

  int opt_n_acc = 0;
  float opt_a = 0.0;
  float opt_d = 0.2;
  int opt_n = 20;
  float opt_t = 0.5;

  bool opt_0 = false;
  bool opt_z = false;
  bool opt_Z = false;

  int opt_R = 0;

  bool opt_dot = false;
  int opt_ec = 0;
  int opt_ec_seed = 0;
  bool opt_reduce = false;
  bool opt_replay = false;
  bool opt_degen = false;
  int argn = 0;

  int exit_code = 0;

  bool stop_on_first_difference = false;

  spot::tgba* formula = 0;
  spot::tgba* product = 0;

  spot::option_map options;

  spot::ltl::environment& env(spot::ltl::default_environment::instance());
  spot::ltl::atomic_prop_set* ap = new spot::ltl::atomic_prop_set;
  spot::bdd_dict* dict = new spot::bdd_dict();

  if (argc <= 1)
    syntax(argv[0]);

  while (++argn < argc)
    {
      if (!strcmp(argv[argn], "-0"))
	{
	  opt_0 = true;
	}
      else if (!strcmp(argv[argn], "-1"))
	{
	  opt_paper = true;
	  opt_z = true;
	}
      else if (!strcmp(argv[argn], "-a"))
	{
	  if (argc < argn + 3)
	    syntax(argv[0]);
	  opt_n_acc = to_int_nonneg(argv[++argn], "-a");
	  opt_a = to_float_nonneg(argv[++argn], "-a");
	}
      else if (!strcmp(argv[argn], "-A"))
        {
          if (argc < argn + 2)
            syntax(argv[0]);
	  if (!opt_ec)
	    opt_ec = 1;
	  std::istream* in;
          if (strcmp(argv[++argn], "-"))
            {
              in = new std::ifstream(argv[argn]);
              if (!*in)
                {
                  delete in;
                  std::cerr << "Failed to open " << argv[argn] << std::endl;
                  exit(2);
                }
            }
          else
	    {
	      in = &std::cin;
	    }

          while (in->good())
            {
              std::string input;
              if (std::getline(*in, input, '\n').fail())
		break;
	      else if (input == "")
		break;
	      ec_algo a = { input, 0 };
	      ec_algos.push_back(a);
	    }

	  if (in != &std::cin)
	    delete in;
        }
      else if (!strncmp(argv[argn], "ar:", 3))
        {
          if (options.parse_options(argv[argn]))
            {
              std::cerr << "Failed to parse " << argv[argn] << std::endl;
              exit(2);
            }
        }
      else if (!strcmp(argv[argn], "-d"))
	{
	  if (argc < argn + 2)
	    syntax(argv[0]);
	  opt_d = to_float_nonneg(argv[++argn], "-d");
	}
      else if (!strcmp(argv[argn], "-D"))
	{
	  opt_degen = true;
	  if (!opt_ec)
	    opt_ec = 1;
	}
      else if (!strcmp(argv[argn], "-e"))
	{
	  if (argc < argn + 2)
	    syntax(argv[0]);
	  opt_ec = to_int_nonneg(argv[++argn], "-e");
	}
      else if (!strcmp(argv[argn], "-g"))
	{
	  opt_dot = true;
	}
      else if (!strcmp(argv[argn], "-H"))
	{
	  if (argc < argn + 1)
	    syntax(argv[0]);
	  stop_on_first_difference = true;
	}
      else if (!strcmp(argv[argn], "-i"))
        {
          if (argc < argn + 2)
            syntax(argv[0]);
          opt_i = argv[++argn];
          if (strcmp(opt_i, "-"))
            {
              formula_file = new std::ifstream(opt_i);
              if (!*formula_file)
                {
                  delete formula_file;
                  std::cerr << "Failed to open " << opt_i << std::endl;
                  exit(2);
                }
            }
          else
            formula_file = &std::cin;
        }
      else if (!strcmp(argv[argn], "-m"))
	{
	  opt_reduce = true;
	  opt_replay = true;
	  if (!opt_ec)
	    opt_ec = 1;
	}
      else if (!strcmp(argv[argn], "-n"))
	{
	  if (argc < argn + 2)
	    syntax(argv[0]);
	  opt_n = to_int_pos(argv[++argn], "-n");
	}
      else if (!strcmp(argv[argn], "-r"))
	{
	  opt_replay = true;
	  if (!opt_ec)
	    opt_ec = 1;
	}
      else if (!strcmp(argv[argn], "-R"))
	{
	  if (argc < argn + 2)
	    syntax(argv[0]);
	  opt_R = to_int_pos(argv[++argn], "-R");
	}
      else if (!strcmp(argv[argn], "-s"))
	{
	  if (argc < argn + 2)
	    syntax(argv[0]);
	  opt_ec_seed = to_int_nonneg(argv[++argn], "-s");
	  spot::srand(opt_ec_seed);
	}
      else if (!strcmp(argv[argn], "-S"))
	{
	  if (argc < argn + 2)
	    syntax(argv[0]);
	  opt_S = to_int_pos(argv[++argn], "-S");
	}
      else if (!strcmp(argv[argn], "-t"))
	{
	  if (argc < argn + 2)
	    syntax(argv[0]);
	  opt_t = to_float_nonneg(argv[++argn], "-t");
	}
      else if (!strcmp(argv[argn], "-z"))
	{
	  opt_z = true;
	}
      else if (!strcmp(argv[argn], "-Z"))
	{
	  opt_Z = opt_z = true;
	}
      else if (!strcmp(argv[argn], "-dp"))
	{
	  opt_dp = true;
	}
      else if (!strcmp(argv[argn], "-f"))
	{
	  if (argc < argn + 2)
	    syntax(argv[0]);
	  opt_f = to_int_pos(argv[++argn], "-f");
	}
      else if (!strcmp(argv[argn], "-F"))
	{
	  if (argc < argn + 2)
	    syntax(argv[0]);
	  opt_F = to_int_nonneg(argv[++argn], "-F");
	}
      else if (!strcmp(argv[argn], "-p"))
	{
	  if (argc < argn + 2)
	    syntax(argv[0]);
	  opt_p = argv[++argn];
	}
      else if (!strcmp(argv[argn], "-l"))
	{
	  if (argc < argn + 2)
	    syntax(argv[0]);
	  opt_l = to_int_nonneg(argv[++argn], "-l");
	}
      else if (!strcmp(argv[argn], "-u"))
	{
	  opt_u = true;
	}
      else
	{
	  ap->insert(static_cast<spot::ltl::atomic_prop*>
		     (env.require(argv[argn])));
	}
    }

  spot::ltl::random_ltl rl(ap);
  const char* tok = rl.parse_options(opt_p);
  if (tok)
    {
      std::cerr << "failed to parse probabilities near `"
		<< tok << "'" << std::endl;
      exit(2);
    }

  if (opt_l > opt_f)
    {
      std::cerr << "-l's argument (" << opt_l << ") should not be larger than "
		<< "-f's (" << opt_f << ")" << std::endl;
      exit(2);
    }

  if (opt_dp)
    {
      rl.dump_priorities(std::cout);
      exit(0);
    }

  if (ec_algos.empty())
    {
      const char** i = default_algos;
      while (*i)
	{
	  ec_algo a = { *i++, 0 };
	  ec_algos.push_back(a);
	}
    }

  spot::timer_map tm_ec;
  spot::timer_map tm_ar;
  std::set<int> failed_seeds;
  int init_opt_ec = opt_ec;
  spot::ltl::atomic_prop_set* apf = new spot::ltl::atomic_prop_set;

  if (opt_ec)
    {
      for (unsigned i = 0; i < ec_algos.size(); ++i)
	{
	  const char* err;
	  ec_algos[i].inst =
	    spot::emptiness_check_instantiator::construct(ec_algos[i]
							  .name.c_str(),
							  &err);
	  if (ec_algos[i].inst == 0)
	    {
	      std::cerr << "Parse error after `" << err << "'" << std::endl;
	      exit(1);
	    }
	  ec_algos[i].inst->options().set(options);
	}
    }

  do
    {
      if (opt_F)
        {
          spot::ltl::formula* f = generate_formula(rl, opt_f, opt_ec_seed,
						   opt_l, opt_u);
          if (!f)
            exit(1);
          formula = spot::ltl_to_tgba_fm(f, dict, true);
          f->destroy();
        }
      else if (opt_i)
        {
          if (formula_file->good())
            {
              std::string input;
              if (std::getline(*formula_file, input, '\n').fail())
		break;
	      else if (input == "")
		break;
              spot::ltl::parse_error_list pel;
              spot::ltl::formula* f = spot::ltl::parse(input, pel, env);
              if (spot::ltl::format_parse_errors(std::cerr, input, pel))
		{
		  exit_code = 1;
		  break;
		}
              formula = spot::ltl_to_tgba_fm(f, dict, true);
              spot::ltl::atomic_prop_set* tmp =
		spot::ltl::atomic_prop_collect(f);
              for (spot::ltl::atomic_prop_set::iterator i = tmp->begin();
		   i != tmp->end(); ++i)
		apf->insert(dynamic_cast<spot::ltl::atomic_prop*>
			    ((*i)->clone()));
              f->destroy();
              delete tmp;
            }
          else
            {
              if (formula_file->bad())
                std::cerr << "Failed to read " << opt_i << std::endl;
              break;
            }
        }

      for (spot::ltl::atomic_prop_set::iterator i = ap->begin();
	   i != ap->end(); ++i)
	apf->insert(dynamic_cast<spot::ltl::atomic_prop*>((*i)->clone()));

      if (!opt_S)
	{
	  do
	    {
	      if (opt_ec && !opt_paper)
		std::cout << "seed: " << opt_ec_seed << std::endl;
	      spot::srand(opt_ec_seed);

	      spot::tgba* a;
	      spot::tgba* r = a = spot::random_graph(opt_n, opt_d, apf, dict,
						     opt_n_acc, opt_a, opt_t,
						     &env);

	      if (formula)
		a = product = new spot::tgba_product(formula, a);

	      int real_n_acc = a->number_of_acceptance_conditions();

	      if (opt_dot)
		{
		  dotty_reachable(std::cout, a);
		}
	      if (!opt_ec)
		{
		  if (!opt_0 && !opt_dot)
		    tgba_save_reachable(std::cout, a);
		}
	      else
		{
		  spot::tgba* degen = 0;
		  if (opt_degen && real_n_acc > 1)
		    degen = new spot::tgba_tba_proxy(a);

		  int n_alg = ec_algos.size();
		  int n_ec = 0;
		  int n_empty = 0;
		  int n_non_empty = 0;
		  int n_maybe_empty = 0;
		  spot::unsigned_statistics_copy ostats_ec, ostats_arc;

		  for (int i = 0; i < n_alg; ++i)
		    {
		      spot::emptiness_check* ec;
		      spot::emptiness_check_result* res;
		      ec = cons_emptiness_check(i, a, degen, real_n_acc);
		      if (!ec)
			continue;
		      ++n_ec;
		      const std::string algo = ec_algos[i].name;
		      if (!opt_paper)
			{
			  std::cout.width(32);
			  std::cout << algo << ": ";
			}
		      tm_ec.start(algo);
		      for (int count = opt_R;;)
			{
			  res = ec->check();
			  if (count-- <= 0)
			    break;
			  delete res;
			  delete ec;
			  ec = cons_emptiness_check(i, a, degen, real_n_acc);
			}
		      tm_ec.stop(algo);
		      const spot::unsigned_statistics* ecs = ec->statistics();
		      if (opt_z && res)
			{
			  // Notice that ratios are computed w.r.t. the
			  // generalized automaton a.
			  prod_stats = spot::stats_reachable(a);
			}
		      else
			{
			  // To trigger a division by 0 if used erroneously.
			  prod_stats.states = 0;
			  prod_stats.transitions = 0;
			}

		      if (opt_z && ecs)
			{
			  sc_ec.count(algo, ecs);
			  if (res)
			    {
			      ec_ratio_stats[real_n_acc].count(algo, ecs);
			      glob_ec_ratio_stats.count(algo, ecs);
			    }
			}

		      if (stop_on_first_difference && ecs)
			if (!ostats_ec.seteq(*ecs))
			  {
			    std::cout << "DIFFERING STATS for emptiness check,"
				      << " halting... ";
			    opt_ec = n_alg = opt_F = 0;
			  }

		      if (res)
			{
			  if (!opt_paper)
			    std::cout << "acc. run";
			  ++n_non_empty;
			  if (opt_replay)
			    {
			      spot::tgba_run* run;
			      bool done = false;
			      tm_ar.start(algo);
			      for (int count = opt_R;;)
				{
				  run = res->accepting_run();
				  const spot::unsigned_statistics* s
				    = res->statistics();
				  if (opt_z && !done)
				    {
				      // Count only the first run (the
				      // other way would be to divide
				      // the stats by opt_R).
				      done = true;
				      sc_arc.count(algo, s);
				      arc_ratio_stats.count(algo, s);
				    }
				  if (stop_on_first_difference && s)
				    if (!ostats_arc.seteq(*s))
				      {
					std::cout << "DIFFERING STATS for "
						  << "accepting runs,"
						  << " halting... ";
					opt_ec = n_alg = opt_F = 0;
					break;
				      }

				  if (count-- <= 0 || !run)
				    break;
				  delete run;
				}
			      if (!run)
				{
				  tm_ar.cancel(algo);
				  if (!opt_paper)
				    std::cout << " exists, not computed";
				}
			      else
				{
				  tm_ar.stop(algo);
				  std::ostringstream s;
				  if (!spot::replay_tgba_run(s,
							     res->automaton(),
							     run))
				    {
				      if (!opt_paper)
					std::cout << ", but could not replay "
						  << "it (ERROR!)";
				      failed_seeds.insert(opt_ec_seed);
				    }
				  else
				    {
				      if (!opt_paper)
					std::cout << ", computed";
				      if (opt_z)
					ar_stats[algo].count(run);
				    }
				  if (opt_z && !opt_paper)
				    std::cout << " [" << run->prefix.size()
					      << "+" << run->cycle.size()
					      << "]";

				  if (opt_reduce)
				    {
				      spot::tgba_run* redrun =
					spot::reduce_run(res->automaton(), run);
				      if (!spot::replay_tgba_run(s,
								 res
								 ->automaton(),
								 redrun))
					{
					  if (!opt_paper)
					    std::cout
					      << ", but could not replay "
					      << "its minimization (ERROR!)";
					  failed_seeds.insert(opt_ec_seed);
					}
				      else
					{
					  if (!opt_paper)
					    std::cout << ", reduced";
					  if (opt_z)
					    mar_stats[algo].count(redrun);
					}
				      if (opt_z && !opt_paper)
					{
					  std::cout << " ["
						    << redrun->prefix.size()
						    << "+"
						    << redrun->cycle.size()
						    << "]";
					}
				      delete redrun;
				    }
				  delete run;
				}
			    }
			  if (!opt_paper)
			    std::cout << std::endl;
			  delete res;
			}
		      else
			{
			  if (ec->safe())
			    {
			      if (!opt_paper)
				std::cout << "empty language" << std::endl;
			      ++n_empty;
			    }
			  else
			    {
			      if (!opt_paper)
				std::cout << "maybe empty language"
					  << std::endl;
			      ++n_maybe_empty;
			    }

			}

		      if (opt_Z && !opt_paper)
			ec->print_stats(std::cout);
		      delete ec;
		    }

		  assert(n_empty + n_non_empty + n_maybe_empty == n_ec);

		  if ((n_empty == 0 && (n_non_empty + n_maybe_empty) != n_ec)
		      || (n_empty != 0 && n_non_empty != 0))
		    {
		      std::cout << "ERROR: not all algorithms agree"
				<< std::endl;
		      failed_seeds.insert(opt_ec_seed);
		    }

		  delete degen;
		}

	      delete product;
	      delete r;

	      if (opt_ec)
		{
		  --opt_ec;
		  ++opt_ec_seed;
		}
	    }
	  while (opt_ec);
	}
      else
	{
	  --opt_S;
	  opt_ec_seed += init_opt_ec;
	}

      delete formula;
      if (opt_F)
        --opt_F;
      opt_ec = init_opt_ec;
      for (spot::ltl::atomic_prop_set::iterator i = apf->begin();
	   i != apf->end(); ++i)
        (*i)->destroy();
      apf->clear();
    }
  while (opt_F || opt_i);

  if (!opt_paper && opt_z)
    {
      if (!sc_ec.empty())
	{
	  std::cout << std::endl
		    << "Statistics about emptiness checks:"
		    << std::endl;
	  sc_ec.display(std::cout);
	}
      if (!sc_arc.empty())
	{
	  std::cout << std::endl
		    << "Statistics about accepting run computations:"
		    << std::endl;
	  sc_arc.display(std::cout);
	}
      if (!glob_ec_ratio_stats.empty())
	{
	  std::cout << std::endl
		    << "Emptiness check ratios for non-empty automata:"
		    << std::endl << "all tests"
		    << std::endl;
	  glob_ec_ratio_stats.display(std::cout, false);
	  if (ec_ratio_stats.size() > 1)
	    for (ec_ratio_stats_type::const_iterator i = ec_ratio_stats.begin();
		 i != ec_ratio_stats.end(); ++i)
	      {
		std::cout << "tests with " << i->first
			  << " acceptance conditions"
			  << std::endl;
		i->second.display(std::cout, false);
	      }
	}
      if (!ar_stats.empty())
	print_ar_stats(ar_stats, "Statistics about accepting runs:");
      if (!mar_stats.empty())
	print_ar_stats(mar_stats, "Statistics about reduced accepting runs:");
      if (!arc_ratio_stats.empty())
	{
	  std::cout << std::endl
		    << "Accepting run ratios:" << std::endl;
	  arc_ratio_stats.display(std::cout, false);
	}
      if (!tm_ec.empty())
	{
	  std::cout << std::endl
		    << "emptiness checks cumulated timings:" << std::endl;
	  tm_ec.print(std::cout);
	}
      if (!tm_ar.empty())
	{
	  std::cout << std::endl
		    << "accepting runs cumulated timings:" << std::endl;
	  tm_ar.print(std::cout);
	}
    }
  else if (opt_paper)
    {
      std::cout << "Emptiness check ratios" << std::endl;
      std::cout << std::right << std::fixed << std::setprecision(1);
      ec_ratio_stat_type::stats_alg_map& stats = glob_ec_ratio_stats.stats;
      typedef ec_ratio_stat_type::alg_1stat_map::const_iterator ec_iter;

      for (unsigned ai = 0; ai < ec_algos.size(); ++ai)
	{
	  const std::string algo = ec_algos[ai].name;

	  int n = -1;

	  std::cout << std::setw(25)  << algo << " " << std::setw(8);

	  ec_iter i = stats["states"].find(algo);
	  if (i != stats["states"].end())
	    {
	      std::cout << i->second.tot / i->second.n;
	      n = i->second.n;
	    }
	  else
	    std::cout << "";
	  std::cout << " " << std::setw(8);

	  i = stats["transitions"].find(algo);
	  if (i != stats["transitions"].end())
	    {
	      std::cout << i->second.tot / i->second.n;
	      n = i->second.n;
	    }
	  else
	    std::cout << "";
	  std::cout << " " << std::setw(8);

	  i = stats["max. depth"].find(algo);
	  if (i != stats["max. depth"].end())
	    {
	      std::cout << i->second.tot / i->second.n;
	      n = i->second.n;
	    }
	  else
	    std::cout << "";
	  if (n >= 0)
	    std::cout << " " << std::setw(8) << n;
	  std::cout << std::endl;
	}

      std::cout << std::endl << "Accepting run ratios" << std::endl;
      std::cout << std::right << std::fixed << std::setprecision(1);
      ec_ratio_stat_type::stats_alg_map& stats2 = arc_ratio_stats.stats;

      for (unsigned ai = 0; ai < ec_algos.size(); ++ai)
	{
	  const std::string algo = ec_algos[ai].name;

	  std::cout << std::setw(25)  << algo << " " << std::setw(8);

	  ec_iter i = stats2["search space states"].find(algo);
	  if (i != stats2["search space states"].end())
	    std::cout << i->second.tot / i->second.n;
	  else
	    std::cout << "";
	  std::cout << " " << std::setw(8);

	  i = stats2["(non unique) states for cycle"].find(algo);
	  if (i != stats2["(non unique) states for cycle"].end())
	    std::cout << i->second.tot / i->second.n;
	  else
	    std::cout << "";
	  std::cout << std::endl;
      }
    }

  if (!failed_seeds.empty())
    {
      exit_code = 1;
      std::cout << "The check failed for the following seeds:";
      for (std::set<int>::const_iterator i = failed_seeds.begin();
	   i != failed_seeds.end(); ++i)
	std::cout << " " << *i;
      std::cout << std::endl;
    }

  for (spot::ltl::atomic_prop_set::iterator i = ap->begin();
       i != ap->end(); ++i)
    (*i)->destroy();

  if (opt_i && strcmp(opt_i, "-"))
    {
      dynamic_cast<std::ifstream*>(formula_file)->close();
      delete formula_file;
    }

  if (opt_ec)
    for (unsigned i = 0; i < ec_algos.size(); ++i)
      delete ec_algos[i].inst;

  delete ap;
  delete apf;
  delete dict;
  return exit_code;
}
