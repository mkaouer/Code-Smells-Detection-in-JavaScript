#include <iostream>
#include <cassert>
#include "tgbaparse/public.hh"
#include "tgba/tgbaexplicit.hh"
#include "tgbaalgos/dotty.hh"
#include "ltlast/allnodes.hh"

void
syntax(char* prog)
{
  std::cerr << prog << " [-d] filename" << std::endl;
  exit(2);
}

int
main(int argc, char** argv)
{
  if (argc < 2)
    syntax(argv[0]);

  bool debug = false;
  int filename_index = 1;

  if (!strcmp(argv[1], "-d"))
    {
      debug = true;
      if (argc < 3)
	syntax(argv[0]);
      filename_index = 2;
    }

  spot::bdd_dict* dict = new spot::bdd_dict();

  spot::ltl::environment& env(spot::ltl::default_environment::instance());
  spot::tgba_parse_error_list pel;
  spot::tgba_explicit* a = spot::tgba_parse(argv[filename_index],
					    pel, dict, env, debug);

  if (spot::format_tgba_parse_errors(std::cerr, pel))
    return 2;

  if (a)
    {
      spot::dotty_reachable(std::cout, a);
      delete a;
    }
  else
    {
      return 1;
    }

  delete dict;
  assert(spot::ltl::atomic_prop::instance_count() == 0);
  assert(spot::ltl::unop::instance_count() == 0);
  assert(spot::ltl::binop::instance_count() == 0);
  assert(spot::ltl::multop::instance_count() == 0);

  return 0;
}
