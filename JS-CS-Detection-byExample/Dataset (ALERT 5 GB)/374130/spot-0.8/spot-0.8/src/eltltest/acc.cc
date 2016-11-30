// Copyright (C) 2008, 2009 Laboratoire de Recherche et Developpement
// de l'Epita (LRDE)
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
#include <cassert>
#include "eltlparse/public.hh"
#include "ltlvisit/lunabbrev.hh"
#include "ltlvisit/nenoform.hh"

int
main(int argc, char** argv)
{
  spot::eltl::parse_error_list p;
  const spot::ltl::formula* f = spot::eltl::parse_file(
    argv[1], p, spot::ltl::default_environment::instance(), argc > 2);

  if (spot::eltl::format_parse_errors(std::cerr, p))
  {
    if (f != 0)
    {
      std::cout << f->dump() << std::endl;
      f->destroy();
    }
    return 1;
  }

  const spot::ltl::formula* f1 = spot::ltl::unabbreviate_logic(f);
  const spot::ltl::formula* f2 = spot::ltl::negative_normal_form(f1);
  f1->destroy();

  assert(f != 0);
  std::cout << f->dump() << std::endl;
  f->destroy();

  assert(f2 != 0);
  std::cout << f2->dump() << std::endl;
  f2->destroy();
}
