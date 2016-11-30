#include <iostream>
#include <cassert>
#include <fstream>
#include <string>
#include "ltlvisit/destroy.hh"
#include "ltlast/allnodes.hh"
#include "ltlparse/public.hh"
#include "tgbaalgos/ltl2tgba_lacim.hh"
#include "tgbaalgos/ltl2tgba_fm.hh"
#include "tgba/bddprint.hh"
#include "tgbaalgos/dotty.hh"
#include "tgbaalgos/lbtt.hh"
#include "tgba/tgbatba.hh"
#include "tgbaalgos/magic.hh"
#include "tgbaalgos/emptinesscheck.hh"
#include "tgbaparse/public.hh"
#include "tgbaalgos/dupexp.hh"

void
syntax(char* prog)
{
  std::cerr << "Usage: "<< prog << " [OPTIONS...] formula" << std::endl
            << "       "<< prog << " -F [OPTIONS...] file" << std::endl
            << "       "<< prog << " -X [OPTIONS...] file" << std::endl
	    << std::endl
	    << "Options:" << std::endl
	    << "  -a   display the acceptance_conditions BDD, not the "
	    << "reachability graph"
	    << std::endl
	    << "  -A   same as -a, but as a set" << std::endl
	    << "  -d   turn on traces during parsing" << std::endl
	    << "  -D   degeneralize the automaton" << std::endl
	    << "  -e   emptiness-check (Couvreur), expect and compute "
	    << "a counter-example" << std::endl
	    << "  -E   emptiness-check (Couvreur), expect no counter-example "
	    << std::endl
            << "  -f   use Couvreur's FM algorithm for translation"
	    << std::endl
            << "  -F   read the formula from the file" << std::endl
	    << "  -m   magic-search (implies -D), expect a counter-example"
	    << std::endl
	    << "  -M   magic-search (implies -D), expect no counter-example"
	    << std::endl
	    << "  -n   same as -m, but display more counter-examples"
	    << std::endl
	    << "  -r   display the relation BDD, not the reachability graph"
	    << std::endl
	    << "  -R   same as -r, but as a set" << std::endl
	    << "  -s   convert to explicit automata, and number states "
	    << "in DFS order" << std::endl
	    << "  -S   convert to explicit automata, and number states "
	    << "in BFS order" << std::endl
	    << "  -t   display reachable states in LBTT's format" << std::endl
	    << "  -v   display the BDD variables used by the automaton"
	    << std::endl
	    << "  -X   do compute an automaton, read it from a file"
	    << std::endl;
  exit(2);
}

int
main(int argc, char** argv)
{
  int exit_code = 0;

  bool debug_opt = false;
  bool degeneralize_opt = false;
  bool fm_opt = false;
  bool file_opt = false;
  int output = 0;
  int formula_index = 0;
  enum { None, Couvreur, MagicSearch } echeck = None;
  enum { NoneDup, BFS, DFS } dupexp = NoneDup;
  bool magic_many = false;
  bool expect_counter_example = false;
  bool from_file = false;

  for (;;)
    {
      if (argc < formula_index + 2)
	syntax(argv[0]);

      ++formula_index;

      if (!strcmp(argv[formula_index], "-a"))
	{
	  output = 2;
	}
      else if (!strcmp(argv[formula_index], "-A"))
	{
	  output = 4;
	}
      else if (!strcmp(argv[formula_index], "-d"))
	{
	  debug_opt = true;
	}
      else if (!strcmp(argv[formula_index], "-D"))
	{
	  degeneralize_opt = true;
	}
      else if (!strcmp(argv[formula_index], "-e"))
	{
	  echeck = Couvreur;
	  expect_counter_example = true;
	  output = -1;
	}
      else if (!strcmp(argv[formula_index], "-E"))
	{
	  echeck = Couvreur;
	  expect_counter_example = false;
	  output = -1;
	}
      else if (!strcmp(argv[formula_index], "-f"))
	{
	  fm_opt = true;
	}
      else if (!strcmp(argv[formula_index], "-F"))
	{
	  file_opt = true;
	}
      else if (!strcmp(argv[formula_index], "-m"))
	{
	  echeck = MagicSearch;
	  degeneralize_opt = true;
	  expect_counter_example = true;
	  output = -1;
	}
      else if (!strcmp(argv[formula_index], "-M"))
	{
	  echeck = MagicSearch;
	  degeneralize_opt = true;
	  expect_counter_example = false;
	  output = -1;
	}
      else if (!strcmp(argv[formula_index], "-n"))
	{
	  echeck = MagicSearch;
	  degeneralize_opt = true;
	  expect_counter_example = true;
	  output = -1;
	  magic_many = true;
	}
      else if (!strcmp(argv[formula_index], "-r"))
	{
	  output = 1;
	}
      else if (!strcmp(argv[formula_index], "-R"))
	{
	  output = 3;
	}
      else if (!strcmp(argv[formula_index], "-s"))
	{
	  dupexp = DFS;
	}
      else if (!strcmp(argv[formula_index], "-S"))
	{
	  dupexp = BFS;
	}
      else if (!strcmp(argv[formula_index], "-t"))
	{
	  output = 6;
	}
      else if (!strcmp(argv[formula_index], "-v"))
	{
	  output = 5;
	}
      else if (!strcmp(argv[formula_index], "-X"))
	{
	  from_file = true;
	}
      else
	{
	  break;
	}
    }

  std::string input;

  if (file_opt)
    {
      std::ifstream fin(argv[formula_index]);
      if (! fin)
	{
	  std::cerr << "Cannot open " << argv[formula_index] << std::endl;
	  exit(2);
	}

      if (! std::getline(fin, input, '\0'))
	{
	  std::cerr << "Cannot read " << argv[formula_index] << std::endl;
	  exit(2);
	}
    }
  else
    {
      input = argv[formula_index];
    }

  spot::ltl::environment& env(spot::ltl::default_environment::instance());
  spot::bdd_dict* dict = new spot::bdd_dict();

  spot::ltl::formula* f = 0;
  if (!from_file)
    {
      spot::ltl::parse_error_list pel;
      f = spot::ltl::parse(input, pel, env, debug_opt);
      exit_code = spot::ltl::format_parse_errors(std::cerr, input, pel);
    }
  if (f || from_file)
    {
      spot::tgba_bdd_concrete* concrete = 0;
      spot::tgba* to_free = 0;
      spot::tgba* a = 0;

      if (from_file)
	{
	  spot::tgba_parse_error_list pel;
	  to_free = a = spot::tgba_parse(input, pel, dict, env, debug_opt);
	  if (spot::format_tgba_parse_errors(std::cerr, pel))
	    return 2;
	}
      else
	{
	  if (fm_opt)
	    to_free = a = spot::ltl_to_tgba_fm(f, dict);
	  else
	    to_free = a = concrete = spot::ltl_to_tgba_lacim(f, dict);
	  spot::ltl::destroy(f);
	}

      spot::tgba_tba_proxy* degeneralized = 0;
      if (degeneralize_opt)
	a = degeneralized = new spot::tgba_tba_proxy(a);

      spot::tgba_explicit* expl = 0;
      switch (dupexp)
	{
	case NoneDup:
	  break;
	case BFS:
	  a = expl = tgba_dupexp_bfs(a);
	  break;
	case DFS:
	  a = expl = tgba_dupexp_dfs(a);
	  break;
	}

      switch (output)
	{
	case -1:
	  /* No output.  */
	  break;
	case 0:
	  spot::dotty_reachable(std::cout, a);
	  break;
	case 1:
	  if (concrete)
	    spot::bdd_print_dot(std::cout, concrete->get_dict(),
				concrete->get_core_data().relation);
	  break;
	case 2:
	  if (concrete)
	    spot::bdd_print_dot(std::cout, concrete->get_dict(),
				concrete->
				get_core_data().acceptance_conditions);
	  break;
	case 3:
	  if (concrete)
	    spot::bdd_print_set(std::cout, concrete->get_dict(),
				concrete->get_core_data().relation);
	  break;
	case 4:
	  if (concrete)
	    spot::bdd_print_set(std::cout, concrete->get_dict(),
				concrete->
				get_core_data().acceptance_conditions);
	  break;
	case 5:
	  a->get_dict()->dump(std::cout);
	  break;
	case 6:
	  spot::lbtt_reachable(std::cout, a);
	  break;
	default:
	  assert(!"unknown output option");
	}

      switch (echeck)
	{
	case None:
	  break;
	case Couvreur:
	  {
	    spot::emptiness_check ec = spot::emptiness_check(a);
	    bool res = ec.check();
	    if (expect_counter_example)
	      {
		if (res)
		  {
		    exit_code = 1;
		    break;
		  }
		ec.counter_example();
		ec.print_result(std::cout);
	      }
	    else
	      {
		exit_code = !res;
	      }
	  }
	  break;
	case MagicSearch:
	  {
	    spot::magic_search ms(degeneralized);
	    bool res = ms.check();
	    if (expect_counter_example)
	      {
		if (!res)
		  {
		    exit_code = 1;
		    break;
		  }
		do
		  ms.print_result(std::cout);
		while (magic_many && ms.check());
	      }
	    else
	      {
		exit_code = res;
	      }
	  }
	  break;
	}

      if (expl)
	delete expl;
      if (degeneralize_opt)
	delete degeneralized;

      delete to_free;
    }
  else
    {
      exit_code = 1;
    }

  assert(spot::ltl::atomic_prop::instance_count() == 0);
  assert(spot::ltl::unop::instance_count() == 0);
  assert(spot::ltl::binop::instance_count() == 0);
  assert(spot::ltl::multop::instance_count() == 0);
  delete dict;
  return exit_code;
}
