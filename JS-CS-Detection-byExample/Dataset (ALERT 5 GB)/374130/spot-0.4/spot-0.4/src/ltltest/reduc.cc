// Copyright (C) 2004, 2006, 2007 Laboratoire d'Informatique de Paris
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

#include <iostream>
#include <cassert>
#include "ltlparse/public.hh"
#include "ltlvisit/lunabbrev.hh"
#include "ltlvisit/tunabbrev.hh"
#include "ltlvisit/dump.hh"
#include "ltlvisit/nenoform.hh"
#include "ltlvisit/destroy.hh"
#include "ltlvisit/tostring.hh"
#include "ltlvisit/reduce.hh"
#include "ltlvisit/length.hh"
#include "ltlvisit/contain.hh"
#include "ltlast/allnodes.hh"

void
syntax(char* prog)
{
  std::cerr << prog << " option formula1 (formula2)?" << std::endl;
  exit(2);
}

int
main(int argc, char** argv)
{
  if (argc < 3)
    syntax(argv[0]);

  int o = spot::ltl::Reduce_None;
  switch (atoi(argv[1]))
    {
    case 0:
      o = spot::ltl::Reduce_Basics;
      break;
    case 1:
      o = spot::ltl::Reduce_Syntactic_Implications;
      break;
    case 2:
      o = spot::ltl::Reduce_Eventuality_And_Universality;
      break;
    case 3:
      o = spot::ltl::Reduce_Basics
	| spot::ltl::Reduce_Syntactic_Implications
	| spot::ltl::Reduce_Eventuality_And_Universality;
      break;
    case 4:
      o = spot::ltl::Reduce_Basics | spot::ltl::Reduce_Syntactic_Implications;
      break;
    case 5:
      o = (spot::ltl::Reduce_Basics
	   | spot::ltl::Reduce_Eventuality_And_Universality);
      break;
    case 6:
      o = (spot::ltl::Reduce_Syntactic_Implications
	   | spot::ltl::Reduce_Eventuality_And_Universality);
      break;
    case 7:
      o = spot::ltl::Reduce_Containment_Checks;
      break;
    case 8:
      o = spot::ltl::Reduce_Containment_Checks_Stronger;
      break;
    case 9:
      o = spot::ltl::Reduce_All;
      break;
    case 10:
      o = (spot::ltl::Reduce_Basics
	   | spot::ltl::Reduce_Containment_Checks_Stronger);
      break;
    case 11:
      o = (spot::ltl::Reduce_Syntactic_Implications
	   | spot::ltl::Reduce_Containment_Checks_Stronger);
      break;
    case 12:
      o = (spot::ltl::Reduce_Basics
	   | spot::ltl::Reduce_Syntactic_Implications
	   | spot::ltl::Reduce_Containment_Checks_Stronger);
      break;
    case 13:
      o = (spot::ltl::Reduce_Eventuality_And_Universality
	   | spot::ltl::Reduce_Containment_Checks_Stronger);
      break;
    case 14:
      o = (spot::ltl::Reduce_Basics
	   | spot::ltl::Reduce_Eventuality_And_Universality
	   | spot::ltl::Reduce_Containment_Checks_Stronger);
      break;
    default:
      return 2;
  }

  spot::ltl::parse_error_list p1;
  spot::ltl::formula* f1 = spot::ltl::parse(argv[2], p1);
  spot::ltl::formula* f2 = 0;

  if (spot::ltl::format_parse_errors(std::cerr, argv[2], p1))
    return 2;


  if (argc == 4)
    {
      spot::ltl::parse_error_list p2;
      f2 = spot::ltl::parse(argv[3], p2);
      if (spot::ltl::format_parse_errors(std::cerr, argv[3], p2))
	return 2;
    }

  int exit_code = 0;

  spot::ltl::formula* ftmp1;
  spot::ltl::formula* ftmp2;

  ftmp1 = f1;
  f1 = unabbreviate_logic(f1);
  ftmp2 = f1;
  f1 = negative_normal_form(f1);
  spot::ltl::destroy(ftmp1);
  spot::ltl::destroy(ftmp2);


  int length_f1_before = spot::ltl::length(f1);
  std::string f1s_before = spot::ltl::to_string(f1);

  ftmp1 = f1;
  f1 = spot::ltl::reduce(f1, o);
  ftmp2 = f1;
  f1 = spot::ltl::unabbreviate_logic(f1);
  spot::ltl::destroy(ftmp1);
  spot::ltl::destroy(ftmp2);

  int length_f1_after = spot::ltl::length(f1);
  std::string f1s_after = spot::ltl::to_string(f1);

  bool red = (length_f1_after < length_f1_before);
  std::string f2s = "";
  if (f2)
    {
      ftmp1 = f2;
      f2 = unabbreviate_logic(f2);
      ftmp2 = f2;
      f2 = negative_normal_form(f2);
      spot::ltl::destroy(ftmp1);
      spot::ltl::destroy(ftmp2);
      ftmp1 = f2;
      f2 = unabbreviate_logic(f2);
      spot::ltl::destroy(ftmp1);
      f2s = spot::ltl::to_string(f2);
    }

  if ((red | !red) && !f2)
  {
      std::cout << length_f1_before << " " << length_f1_after
		<< " '" << f1s_before << "' reduce to '" << f1s_after << "'"
		<< std::endl;
  }

  if (f2)
    {
      if (f1 != f2)
	{
	  if (length_f1_after < length_f1_before)
	    std::cout << f1s_before << " ** " << f2s << " ** " << f1s_after
		      << " KOREDUC " << std::endl;
	  else
	    std::cout << f1s_before << " ** " << f2s << " ** " << f1s_after
		      << " KOIDEM " << std::endl;
	  exit_code = 1;
	}
      else
	{
	  if (f1s_before != f1s_after)
	    std::cout << f1s_before << " ** " << f2s << " ** " << f1s_after
		      << " OKREDUC " << std::endl;
	  else
	    std::cout << f1s_before << " ** " << f2s << " ** " << f1s_after
		      << " OKIDEM" << std::endl;
	  exit_code = 0;
	}
    }
  else
    {
      if (length_f1_after < length_f1_before)
	exit_code = 0;
    }

  spot::ltl::destroy(f1);
  if (f2)
    spot::ltl::destroy(f2);


  assert(spot::ltl::atomic_prop::instance_count() == 0);
  assert(spot::ltl::unop::instance_count() == 0);
  assert(spot::ltl::binop::instance_count() == 0);
  assert(spot::ltl::multop::instance_count() == 0);

  return exit_code;
}
