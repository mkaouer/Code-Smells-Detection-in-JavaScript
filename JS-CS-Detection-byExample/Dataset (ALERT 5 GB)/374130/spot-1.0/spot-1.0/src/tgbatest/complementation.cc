// Copyright (C) 2008, 2009, 2010, 2011, 2012 Laboratoire de Recherche et
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

#include <iomanip>
#include <iostream>
#include "tgbaalgos/dotty.hh"
#include "tgbaalgos/save.hh"
#include "tgbaparse/public.hh"
#include "tgba/tgbaproduct.hh"
#include "tgbaalgos/gtec/gtec.hh"
#include "tgbaalgos/ltl2tgba_fm.hh"
#include "ltlparse/public.hh"
#include "tgbaalgos/stats.hh"
#include "tgbaalgos/emptiness.hh"
#include "ltlast/unop.hh"
#include "tgbaalgos/stats.hh"
#include "tgbaalgos/emptiness_stats.hh"
#include "tgba/tgbatba.hh"

#include "tgba/tgbasafracomplement.hh"
#include "tgba/tgbakvcomplement.hh"

void usage(const char* prog)
{
  std::cout << "usage: " << prog << " [options]" << std::endl;
  std::cout << "with options" << std::endl
            << "-b                      Output in spot's format" << std::endl
            << "-S                      Use Safra's complementation "
	    << "instead of Kupferman&Vardi's" << std::endl
            << "-s     buchi_automaton  display the safra automaton"
            << std::endl
            << "-a     buchi_automaton  display the complemented automaton"
            << std::endl
            << "-astat buchi_automaton  statistics for !a" << std::endl
            << "-fstat formula          statistics for !A_f" << std::endl
            << "-f     formula          test !A_f and !A_!f" << std::endl
            << "-p     formula          print the automaton for f" << std::endl;
}

int main(int argc, char* argv[])
{
  char *file = 0;
  bool print_safra = false;
  bool print_automaton = false;
  //bool check = false;
  int return_value = 0;
  bool stats = false;
  bool formula = false;
  bool safra = false;
  bool print_formula = false;
  bool save_spot = false;

  if (argc < 3)
  {
    usage(argv[0]);
    return 1;
  }

  for (int i = 1; i < argc; ++i)
  {
    if (argv[i][0] == '-')
    {
      if (strcmp(argv[i] + 1, "b") == 0)
      {
	save_spot = true;
	continue;
      }

      if (strcmp(argv[i] + 1, "astat") == 0)
      {
        stats = true;
        formula = false;
        continue;
      }

      if (strcmp(argv[i] + 1, "fstat") == 0)
      {
        stats = true;
        formula = true;
        continue;
      }

      switch (argv[i][1])
      {
        case 'S':
          safra = true; break;
        case 's':
          safra = true; print_safra = true; break;
        case 'a':
          print_automaton = true; break;
        case 'f':
          //check = true;
	  break;
        case 'p':
          print_formula = true; break;
        default:
          std::cerr << "unrecognized option `-" << argv[i][1]
                    << "'" << std::endl;
          return 2;
      }
    }
    else
      file = argv[i];
  }

  if (file == 0)
  {
    usage(argv[0]);
    return 1;
  }

  spot::bdd_dict* dict = new spot::bdd_dict();
  if (print_automaton || print_safra)
  {
    spot::ltl::environment& env(spot::ltl::default_environment::instance());
    spot::tgba_parse_error_list pel;
    spot::tgba_explicit_string* a = spot::tgba_parse(file, pel, dict, env);
    if (spot::format_tgba_parse_errors(std::cerr, file, pel))
      return 2;

    spot::tgba* complement = 0;

    if (safra)
      complement = new spot::tgba_safra_complement(a);
    else
      complement = new spot::tgba_kv_complement(a);

    if (print_automaton)
      {
	if (save_spot)
	  spot::tgba_save_reachable(std::cout, complement);
	else
	  spot::dotty_reachable(std::cout, complement);
      }

    if (print_safra)
    {
      spot::tgba_safra_complement* safra_complement =
        dynamic_cast<spot::tgba_safra_complement*>(complement);
      spot::display_safra(safra_complement);
    }
    delete complement;
    delete a;
  }
  else if (print_formula)
  {
    spot::tgba* a;
    const spot::ltl::formula* f1 = 0;

    spot::ltl::parse_error_list p1;
    f1 = spot::ltl::parse(file, p1);

    if (spot::ltl::format_parse_errors(std::cerr, file, p1))
      return 2;

    a = spot::ltl_to_tgba_fm(f1, dict);

    spot::tgba* complement = 0;
    if (safra)
      complement = new spot::tgba_safra_complement(a);
    else
      complement = new spot::tgba_kv_complement(a);

    spot::dotty_reachable(std::cout, complement);
    f1->destroy();
    delete complement;
    delete a;
  }
  else if (stats)
  {
    spot::tgba* a;
    const spot::ltl::formula* f1 = 0;

    if (formula)
    {
      spot::ltl::parse_error_list p1;
      f1 = spot::ltl::parse(file, p1);

      if (spot::ltl::format_parse_errors(std::cerr, file, p1))
        return 2;

      a = spot::ltl_to_tgba_fm(f1, dict);
    }
    else
    {
      spot::tgba_parse_error_list pel;
      spot::ltl::environment& env(spot::ltl::default_environment::instance());
      a = spot::tgba_parse(file, pel, dict, env);
      if (spot::format_tgba_parse_errors(std::cerr, file, pel))
        return 2;
    }

    spot::tgba_safra_complement* safra_complement =
      new spot::tgba_safra_complement(a);

    spot::tgba_statistics a_size =  spot::stats_reachable(a);
    std::cout << "Original: "
              << a_size.states << ", "
              << a_size.transitions << ", "
              << a->number_of_acceptance_conditions()
              << std::endl;

    spot::tgba *buchi = new spot::tgba_sba_proxy(a);
    a_size =  spot::stats_reachable(buchi);
    std::cout << "Buchi: "
              << a_size.states << ", "
              << a_size.transitions << ", "
              << buchi->number_of_acceptance_conditions()
              << std::endl;
    delete buchi;

    spot::tgba_statistics b_size =  spot::stats_reachable(safra_complement);
    std::cout << "Safra Complement: "
              << b_size.states << ", "
              << b_size.transitions << ", "
              << safra_complement->number_of_acceptance_conditions()
              << std::endl;

    spot::tgba_kv_complement* complement =
      new spot::tgba_kv_complement(a);

    b_size =  spot::stats_reachable(complement);
    std::cout << "GBA Complement: "
              << b_size.states << ", "
              << b_size.transitions << ", "
              << complement->number_of_acceptance_conditions()
              << std::endl;

    delete complement;
    delete a;
    if (formula)
    {
      const spot::ltl::formula* nf1 =
        spot::ltl::unop::instance(spot::ltl::unop::Not,
                                  f1->clone());
      spot::tgba* a2 = spot::ltl_to_tgba_fm(nf1, dict);
      spot::tgba_statistics a_size =  spot::stats_reachable(a2);
      std::cout << "Not Formula: "
                << a_size.states << ", "
                << a_size.transitions << ", "
                << a2->number_of_acceptance_conditions()
                << std::endl;

      delete a2;
      f1->destroy();
      nf1->destroy();
    }
  }
  else
  {
    spot::ltl::parse_error_list p1;
    const spot::ltl::formula* f1 = spot::ltl::parse(file, p1);

    if (spot::ltl::format_parse_errors(std::cerr, file, p1))
      return 2;

    spot::tgba* Af = spot::ltl_to_tgba_fm(f1, dict);
    const spot::ltl::formula* nf1 =
      spot::ltl::unop::instance(spot::ltl::unop::Not, f1->clone());
    spot::tgba* Anf = spot::ltl_to_tgba_fm(nf1, dict);

    spot::tgba* nAf;
    spot::tgba* nAnf;
    if (safra)
    {
      nAf = new spot::tgba_safra_complement(Af);
      nAnf = new spot::tgba_safra_complement(Anf);
    }
    else
    {
      nAf = new spot::tgba_kv_complement(Af);
      nAnf = new spot::tgba_kv_complement(Anf);
    }
    spot::tgba* prod = new spot::tgba_product(nAf, nAnf);
    spot::emptiness_check* ec = spot::couvreur99(prod);
    spot::emptiness_check_result* res = ec->check();
    spot::tgba_statistics a_size =  spot::stats_reachable(ec->automaton());
    std::cout << "States: "
              << a_size.states << std::endl
              << "Transitions: "
              << a_size.transitions << std::endl
              << "Acc Cond: "
              << ec->automaton()->number_of_acceptance_conditions()
              << std::endl;
    if (res)
    {
      std::cout << "FAIL";
      return_value = 1;
    }
    else
      std::cout << "OK";
    std::cout << std::endl;

    delete res;
    delete ec;
    delete prod;
    delete nAf;
    delete Af;
    delete nAnf;
    delete Anf;

    nf1->destroy();
    f1->destroy();

  }

  delete dict;

  return return_value;
}
