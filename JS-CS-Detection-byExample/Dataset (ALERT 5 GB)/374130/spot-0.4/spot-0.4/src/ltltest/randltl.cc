// Copyright (C) 2003, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
// et Marie Curie.
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
#include <iostream>
#include <set>
#include <string>
#include "ltlast/atomic_prop.hh"
#include "ltlvisit/randomltl.hh"
#include "ltlvisit/tostring.hh"
#include "ltlvisit/destroy.hh"
#include "ltlvisit/length.hh"
#include "ltlvisit/reduce.hh"
#include "ltlenv/defaultenv.hh"
#include "misc/random.hh"

void
syntax(char* prog)
{
  std::cerr << "Usage: "<< prog << " [OPTIONS...] PROPS..." << std::endl
	    << std::endl
	    << "Options:" << std::endl
	    << "  -d      dump priorities, do not generate any formula"
	    << std::endl
	    << "  -f N    the size of the formula [15]" << std::endl
	    << "  -F N    number of formulae to generate [1]" << std::endl
	    << "  -p S    priorities to use" << std::endl
	    << "  -r N    simplify formulae using all available reductions"
	    << " and reject those" << std::endl
	    << "            strictly smaller than N" << std::endl
	    << "  -u      generate unique formulae"
	    << std::endl
	    << "  -s N    seed for the random number generator" << std::endl
	    << std::endl
	    << "Where:" << std::endl
	    << "  F      are floating values" << std::endl
	    << "  S      are `KEY=F, KEY=F, ...' strings" << std::endl
	    << "  N      are positive integers" << std::endl
	    << "  PROPS  are the atomic properties to use on transitions"
	    << std::endl
	    << "Use -d to see the list of KEYs." << std::endl;
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
main(int argc, char** argv)
{
  bool opt_d = false;
  int opt_s = 0;
  int opt_f = 15;
  int opt_F = 1;
  char* opt_p = 0;
  int opt_r = 0;
  bool opt_u = false;

  spot::ltl::environment& env(spot::ltl::default_environment::instance());
  spot::ltl::atomic_prop_set* ap = new spot::ltl::atomic_prop_set;

  int argn = 0;

  if (argc <= 1)
    syntax(argv[0]);

  while (++argn < argc)
    {
      if (!strcmp(argv[argn], "-d"))
	{
	  opt_d = true;
	}
      else if (!strcmp(argv[argn], "-f"))
	{
	  if (argc < argn + 2)
	    syntax(argv[0]);
	  opt_f = to_int(argv[++argn]);
	}
      else if (!strcmp(argv[argn], "-F"))
	{
	  if (argc < argn + 2)
	    syntax(argv[0]);
	  opt_F = to_int(argv[++argn]);
	}
      else if (!strcmp(argv[argn], "-p"))
	{
	  if (argc < argn + 2)
	    syntax(argv[0]);
	  opt_p = argv[++argn];
	}
      else if (!strcmp(argv[argn], "-r"))
	{
	  if (argc < argn + 2)
	    syntax(argv[0]);
	  opt_r = to_int(argv[++argn]);
	}
      else if (!strcmp(argv[argn], "-s"))
	{
	  if (argc < argn + 2)
	    syntax(argv[0]);
	  opt_s = to_int(argv[++argn]);
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

  if (opt_r > opt_f)
    {
      std::cerr << "-r's argument (" << opt_r << ") should not be larger than "
		<< "-f's (" << opt_f << ")" << std::endl;
      exit(2);
    }

  if (opt_d)
    {
      rl.dump_priorities(std::cout);
    }
  else
    {
      std::set<std::string> unique;

      while (opt_F--)
	{
	  int max_tries_u = 1000;
	  while (max_tries_u--)
	    {
	      spot::srand(opt_s++);
	      spot::ltl::formula* f = 0;
	      int max_tries_r = 1000;
	      while (max_tries_r--)
		{
		  f = rl.generate(opt_f);
		  if (opt_r)
		    {
		      spot::ltl::formula* g = reduce(f);
		      spot::ltl::destroy(f);
		      if (spot::ltl::length(g) < opt_r)
			{
			  spot::ltl::destroy(g);
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
	      if (max_tries_r < 0)
		{
		  assert(opt_r);
		  std::cerr << "Failed to generate non-reducible formula "
			    << "of size " << opt_r << " or more." << std::endl;
		  exit(2);
		}
	      std::string txt = spot::ltl::to_string(f);
	      spot::ltl::destroy(f);
	      if (!opt_u || unique.insert(txt).second)
		{
		  std::cout << txt << std::endl;
		  break;
		}
	    }
	  if (max_tries_u < 0)
	    {
	      assert(opt_u);
	      std::cerr << "Failed to generate another unique formula."
			<< std::endl;
	      exit(2);
	    }
	}
    }
  delete ap;
}
