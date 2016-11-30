#include <iostream>
#include <cassert>
#include "tgba/tgbaexplicit.hh"
#include "tgba/tgbaproduct.hh"
#include "tgbaparse/public.hh"
#include "tgbaalgos/save.hh"
#include "ltlast/allnodes.hh"

void
syntax(char* prog)
{
  std::cerr << prog << " file1 file2" << std::endl;
  exit(2);
}

int
main(int argc, char** argv)
{
  int exit_code = 0;

  if (argc != 3)
    syntax(argv[0]);

  spot::bdd_dict* dict = new spot::bdd_dict();

  spot::ltl::environment& env(spot::ltl::default_environment::instance());
  spot::tgba_parse_error_list pel1;
  spot::tgba_explicit* a1 = spot::tgba_parse(argv[1], pel1, dict, env);
  if (spot::format_tgba_parse_errors(std::cerr, pel1))
    return 2;
  spot::tgba_parse_error_list pel2;
  spot::tgba_explicit* a2 = spot::tgba_parse(argv[2], pel2, dict, env);
  if (spot::format_tgba_parse_errors(std::cerr, pel2))
    return 2;

  {
    spot::tgba_product p(a1, a2);
    spot::tgba_save_reachable(std::cout, &p);
  }

  assert(spot::ltl::unop::instance_count() == 0);
  assert(spot::ltl::binop::instance_count() == 0);
  assert(spot::ltl::multop::instance_count() == 0);
  assert(spot::ltl::atomic_prop::instance_count() != 0);
  delete a1;
  delete a2;
  assert(spot::ltl::atomic_prop::instance_count() == 0);
  delete dict;
  return exit_code;
}
