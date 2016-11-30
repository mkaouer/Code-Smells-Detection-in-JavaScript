// Copyright (C) 2009 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
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

#include <iostream>
#include <cstring>

#include <saba/saba.hh>
#include <saba/sabacomplementtgba.hh>
#include <tgba/tgba.hh>
#include <tgbaparse/public.hh>
#include <tgba/tgbatba.hh>
#include <sabaalgos/sabadotty.hh>
#include <tgbaalgos/ltl2tgba_fm.hh>
#include <ltlparse/public.hh>

void usage(const std::string& argv0)
{
  std::cerr
    << "usage " << argv0 << " [options]" << std::endl
    << "options:" << std::endl
    << "-f formula          display the saba of !forumula"
    << std::endl;
}

int main(int argc, char* argv[])
{
  std::string formula;
  for (int i = 1; i < argc; ++i)
  {
    if (!strcmp(argv[i], "-f"))
    {
      if (i + 1 >= argc)
      {
        usage(argv[0]);
        return 1;
      }
      formula = argv[++i];
    }
    else
    {
      usage(argv[0]);
      return 1;
    }
  }

  if (formula.empty())
  {
    usage(argv[0]);
    return 1;
  }

  spot::bdd_dict* dict = new spot::bdd_dict();
  spot::tgba* a;
  spot::ltl::formula* f1 = 0;

  spot::ltl::parse_error_list p1;
  f1 = spot::ltl::parse(formula, p1);
  if (spot::ltl::format_parse_errors(std::cerr, formula, p1))
    return 2;

  a = spot::ltl_to_tgba_fm(f1, dict);

  spot::saba_complement_tgba* complement =
    new spot::saba_complement_tgba(a);

  spot::saba_dotty_reachable(std::cout, complement);

  delete complement;
  delete a;
  f1->destroy();
  delete dict;
}
