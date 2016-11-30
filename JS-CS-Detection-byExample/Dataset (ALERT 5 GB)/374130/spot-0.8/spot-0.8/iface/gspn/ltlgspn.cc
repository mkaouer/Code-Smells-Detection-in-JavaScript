// Copyright (C) 2003, 2004, 2006, 2007, 2008, 2009 Laboratoire
// d'Informatique de Paris 6 (LIP6), département Systèmes Répartis
// Coopératifs (SRC), Université Pierre et Marie Curie.
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

#ifndef SSP
#include "gspn.hh"
#define MIN_ARG 3
#else
#include "ssp.hh"
#define MIN_ARG 4
#include "tgba/tgbaexplicit.hh"
#include "tgbaparse/public.hh"
#endif
#include "ltlparse/public.hh"
#include "tgba/tgbatba.hh"
#include "tgba/tgbaproduct.hh"
#include "tgbaalgos/ltl2tgba_lacim.hh"
#include "tgbaalgos/ltl2tgba_fm.hh"
#include "tgbaalgos/magic.hh"
#include "tgbaalgos/gtec/gtec.hh"
#include "tgbaalgos/gtec/ce.hh"
#include "tgbaalgos/projrun.hh"


void
syntax(char* prog)
{
  std::cerr << "Usage: "<< prog
#ifndef SSP
	    << " [OPTIONS...] model formula props..."   << std::endl
#else
	    << " [OPTIONS...] model formula automata props..."   << std::endl
#endif
	    << std::endl
#ifdef SSP
	    << "  -1  do not use a double hash (for inclusion check)"
	    << std::endl
	    << "  -L  use LIFO ordering for inclusion check"
	    << std::endl
#endif
	    << "  -c  compute an example" << std::endl
	    << "      (instead of just checking for emptiness)" << std::endl
	    << std::endl
#ifndef SSP
            << "  -d DEAD" << std::endl
            << "      use DEAD as property for marking dead states"
	    << " (by default DEAD=true)" << std::endl
#endif

	    << "  -e  use Couvreur's emptiness-check (default)" << std::endl
	    << "  -e2 use Couvreur's emptiness-check's shy variant" << std::endl
#ifdef SSP
	    << "  -e3 use semi-d. incl. Couvreur's emptiness-check"
	    << std::endl
	    << "  -e4 use semi-d. incl. Couvreur's emptiness-check's "
	    << "shy variant"
	    << std::endl
	    << "  -e45 mix of -e4 and -e5 (semi.d. incl. before d.incl.)"
	    << std::endl
	    << "  -e54 mix of -e5 and -e4 (the other way around)" << std::endl
	    << "  -e5 use d. incl. Couvreur's emptiness-check's shy variant"
	    << std::endl
	    << "  -e6 like -e5, but without inclusion checks in the "
	    << "search stack" << std::endl
#endif
	    << "  -m  degeneralize and perform a magic-search" << std::endl
	    << std::endl
#ifdef SSP
            << "  -n  do not perform any decomposition" << std::endl
#endif
            << "  -l  use Couvreur's LaCIM algorithm for translation (default)"
	    << std::endl
            << "  -f  use Couvreur's FM algorithm for translation" << std::endl
	    << "  -P  do not project example on model" << std::endl;
  exit(2);
}


void
display_stats(const spot::unsigned_statistics* s)
{
  assert(s);
  spot::unsigned_statistics::stats_map::const_iterator i;
  for (i = s->stats.begin(); i != s->stats.end(); ++i)
    std::cout << i->first << " = " << (s->*i->second)() << std::endl;
}

int
main(int argc, char **argv)
  try
    {
      int formula_index = 1;
      enum { Couvreur, Couvreur2, Couvreur3,
	     Couvreur4, Couvreur5, Magic } check = Couvreur;
      enum { Lacim, Fm } trans = Lacim;
      bool compute_counter_example = false;
      bool proj = true;
#ifdef SSP
      bool doublehash = true;
      bool stack_inclusion = true;
      bool pushfront = false;
      bool double_inclusion = false;
      bool reversed_double_inclusion = false;
      bool no_decomp = false;
#endif
      std::string dead = "true";

      spot::ltl::declarative_environment env;

      while (formula_index < argc && *argv[formula_index] == '-')
	{
#ifdef SSP
	  if (!strcmp(argv[formula_index], "-1"))
	    {
	      doublehash = false;
	    }
	  else if (!strcmp(argv[formula_index], "-L"))
	    {
	      pushfront = true;
	    }
	  else
#endif
	  if (!strcmp(argv[formula_index], "-c"))
	    {
	      compute_counter_example = true;
	    }
#ifndef SSP
	  else if (!strcmp(argv[formula_index], "-d"))
	    {
	      if (formula_index + 1 >= argc)
		syntax(argv[0]);
	      dead = argv[++formula_index];
	      if (strcasecmp(dead.c_str(), "true")
		  && strcasecmp(dead.c_str(), "false"))
		env.declare(dead);
	    }
#endif
	  else if (!strcmp(argv[formula_index], "-e"))
	    {
	      check = Couvreur;
	    }
	  else if (!strcmp(argv[formula_index], "-e2"))
	    {
	      check = Couvreur2;
	    }
#ifdef SSP
	  else if (!strcmp(argv[formula_index], "-e3"))
	    {
	      check = Couvreur3;
	    }
	  else if (!strcmp(argv[formula_index], "-e4"))
	    {
	      check = Couvreur4;
	    }
	  else if (!strcmp(argv[formula_index], "-e45"))
	    {
	      check = Couvreur5;
	      double_inclusion = true;
	    }
	  else if (!strcmp(argv[formula_index], "-e54"))
	    {
	      check = Couvreur5;
	      double_inclusion = true;
	      reversed_double_inclusion = true;
	    }
	  else if (!strcmp(argv[formula_index], "-e5"))
	    {
	      check = Couvreur5;
	    }
	  else if (!strcmp(argv[formula_index], "-e6"))
	    {
	      check = Couvreur5;
	      stack_inclusion = false;
	    }
#endif
	  else if (!strcmp(argv[formula_index], "-m"))
	    {
	      check = Magic;
	    }
#ifdef SSP
	  else if (!strcmp(argv[formula_index], "-n"))
	    {
	      no_decomp = true;
	    }
#endif
	  else if (!strcmp(argv[formula_index], "-l"))
	    {
	      trans = Lacim;
	    }
	  else if (!strcmp(argv[formula_index], "-f"))
	    {
	      trans = Fm;
	    }
	  else if (!strcmp(argv[formula_index], "-P"))
	    {
	      proj = 0;
	    }
	  else
	    {
	      syntax(argv[0]);
	    }
	  ++formula_index;
	}
      if (argc < formula_index + MIN_ARG)
	syntax(argv[0]);


      while (argc >= formula_index + MIN_ARG)
	{
	  env.declare(argv[argc - 1]);
	  --argc;
	}

      spot::ltl::parse_error_list pel;
      spot::ltl::formula* f = spot::ltl::parse(argv[formula_index + 1],
					       pel, env);

      if (spot::ltl::format_parse_errors(std::cerr,
					 argv[formula_index + 1], pel))
	exit(2);

      argv[1] = argv[formula_index];
      spot::bdd_dict* dict = new spot::bdd_dict();

#if SSP
      bool inclusion = (check != Couvreur && check != Couvreur2);
      spot::gspn_ssp_interface gspn(2, argv, dict, env, inclusion,
				    doublehash, pushfront);

      spot::tgba_parse_error_list pel1;
      spot::tgba_explicit* control = spot::tgba_parse(argv[formula_index + 2],
						      pel1, dict, env);
      if (spot::format_tgba_parse_errors(std::cerr, argv[formula_index + 2],
					 pel1))
	return 2;
#else
      spot::gspn_interface gspn(2, argv, dict, env, dead);
#endif

      spot::tgba* a_f = 0;
      switch (trans)
	{
	case Fm:
	  a_f = spot::ltl_to_tgba_fm(f, dict);
	  break;
	case Lacim:
	  a_f = spot::ltl_to_tgba_lacim(f, dict);
	  break;
	}
      f->destroy();

#ifndef SSP
      spot::tgba* model        = gspn.automaton();
      spot::tgba_product* prod = new spot::tgba_product(model, a_f);
#else
      spot::tgba_product* ca = new spot::tgba_product(control, a_f);
      spot::tgba* model      = gspn.automaton(ca);
      spot::tgba* prod = model;
#endif

      switch (check)
	{
	case Couvreur:
	case Couvreur2:
	case Couvreur3:
	case Couvreur4:
	case Couvreur5:
	  {
	    spot::couvreur99_check* ec;

	    switch (check)
	      {
	      case Couvreur:
		ec = new spot::couvreur99_check(prod);
		break;
	      case Couvreur2:
		ec = new spot::couvreur99_check_shy(prod);
		break;
#ifdef SSP
	      case Couvreur3:
		ec = spot::couvreur99_check_ssp_semi(prod);
		break;
	      case Couvreur4:
		ec = spot::couvreur99_check_ssp_shy_semi(prod);
		break;
	      case Couvreur5:
		ec = spot::couvreur99_check_ssp_shy(prod, stack_inclusion,
						    double_inclusion,
						    reversed_double_inclusion,
						    no_decomp);
		break;
#endif
	      default:
		assert(0);
		// Assign something so that GCC does not complains
		// EC might be used uninitialized if assert is disabled.
		ec = 0;
	      }

	    spot::emptiness_check_result* res = ec->check();
	    const spot::couvreur99_check_status* ecs = ec->result();
	    if (res)
	      {
		if (compute_counter_example)
		  {
		    spot::couvreur99_check_result* ce;
#ifndef SSP
		    ce = new spot::couvreur99_check_result(ecs);
#else
		    switch (check)
		      {
		      case Couvreur:
		      case Couvreur2:
		      case Couvreur5:
			ce = new spot::couvreur99_check_result(ecs);
			break;
		      default:
			// ce = spot::counter_example_ssp(ecs);
			std::cerr
			  << "counter_example_ssp() is no longer supported"
			  << std::endl;
			exit(1);
		      }
#endif
		    spot::tgba_run* run = ce->accepting_run();
		    if (proj)
		      {
			spot::tgba_run* p = project_tgba_run(prod, model, run);
			spot::print_tgba_run(std::cout, model, p);
			delete p;
		      }
		    else
		      {
			spot::print_tgba_run(std::cout, prod, run);
		      }
		    ce->print_stats(std::cout);
		    display_stats(ec);
		    delete run;
		    delete ce;
		  }
		else
		  {
		    std::cout << "non empty" << std::endl;
		    ecs->print_stats(std::cout);
		    display_stats(ec);
		  }
		delete res;
	      }
	    else
	      {
		std::cout << "empty" << std::endl;
		ecs->print_stats(std::cout);
		display_stats(ec);
	      }
	    std::cout << std::endl;
	    delete ec;
	    if (res)
	      exit(1);
	  }
	  break;
	case Magic:
	  {
	    spot::tgba_tba_proxy* d  = new spot::tgba_tba_proxy(prod);
	    spot::emptiness_check* ec = spot::explicit_magic_search(d);

	    spot::emptiness_check_result* res = ec->check();
	    if (res)
	      {
		if (compute_counter_example)
		  {
		    spot::tgba_run* run = res->accepting_run();
		    if (proj)
		      {
			spot::tgba_run* p = project_tgba_run(prod, model, run);
			spot::print_tgba_run(std::cout, model, p);
			delete p;
		      }
		    else
		      {
			spot::print_tgba_run(std::cout, prod, run);
		      }
		    delete run;
		  }
		else
		  std::cout << "non-empty" << std::endl;
		delete res;
		exit(1);
	      }
	    else
	      {
		std::cout << "empty" << std::endl;
	      }
	    delete ec;
	    delete d;
	  }
	}
#ifndef SSP
      delete prod;
      delete model;
#else
      delete model;
      delete control;
      delete ca;
#endif
      delete a_f;
      delete dict;
    }
  catch (spot::gspn_exception e)
    {
      std::cerr << e << std::endl;
      throw;
    }
