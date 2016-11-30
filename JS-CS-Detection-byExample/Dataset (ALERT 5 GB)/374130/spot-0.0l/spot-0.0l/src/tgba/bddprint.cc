// Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include <sstream>
#include <cassert>
#include "bddprint.hh"
#include "ltlvisit/tostring.hh"
#include "formula2bdd.hh"
#include "ltlvisit/destroy.hh"

namespace spot
{
  /// Global dictionary used by print_handler() to lookup variables.
  static const bdd_dict* dict;

  /// Global flag to enable Acc[x] output (instead of `x').
  static bool want_acc;

  /// Stream handler used by Buddy to display BDD variables.
  static void
  print_handler(std::ostream& o, int var)
  {
    bdd_dict::vf_map::const_iterator isi =
      dict->var_formula_map.find(var);
    if (isi != dict->var_formula_map.end())
      to_string(isi->second, o);
    else
      {
	isi = dict->acc_formula_map.find(var);
	if (isi != dict->acc_formula_map.end())
	  {
	    if (want_acc)
	      {
		o << "Acc[";
		to_string(isi->second, o) << "]";
	      }
	    else
	      {
		o << "\"";
		to_string(isi->second, o) << "\"";
	      }
	  }
	else
	  {
	    isi = dict->now_formula_map.find(var);
	    if (isi != dict->now_formula_map.end())
	      {
		o << "Now["; to_string(isi->second, o) << "]";
	      }
	    else
	      {
		isi = dict->now_formula_map.find(var - 1);
		if (isi != dict->now_formula_map.end())
		  {
		    o << "Next["; to_string(isi->second, o) << "]";
		  }
		else
		  {
		    o << "?" << var;
		  }
	      }
	  }
      }
  }


  static std::ostream* where;
  static void
  print_sat_handler(char* varset, int size)
  {
    bool not_first = false;
    for (int v = 0; v < size; ++v)
      {
	if (varset[v] < 0)
	  continue;
	if (not_first)
	  *where << " ";
	else
	  not_first = true;
	if (varset[v] == 0)
	  // The space is important for LBTT.
	  *where << "! ";
	print_handler(*where, v);
      }
  }

  std::ostream&
  bdd_print_sat(std::ostream& os, const bdd_dict* d, bdd b)
  {
    dict = d;
    where = &os;
    want_acc = false;
    assert(bdd_satone(b) == b);
    bdd_allsat(b, print_sat_handler);
    return os;
  }

  static void
  print_acc_handler(char* varset, int size)
  {
    for (int v = 0; v < size; ++v)
      if (varset[v] > 0)
	{
	  *where << " ";
	  print_handler(*where, v);
	}
  }

  std::ostream&
  bdd_print_acc(std::ostream& os, const bdd_dict* d, bdd b)
  {
    dict = d;
    where = &os;
    want_acc = false;
    bdd_allsat(b, print_acc_handler);
    return os;
  }

  static bool first_done = false;
  static void
  print_accset_handler(char* varset, int size)
  {
    for (int v = 0; v < size; ++v)
      if (varset[v] > 0)
	{
	  *where << (first_done ? ", " : "{");
	  print_handler(*where, v);
	  first_done = true;
	}
  }

  std::ostream&
  bdd_print_accset(std::ostream& os, const bdd_dict* d, bdd b)
  {
    dict = d;
    where = &os;
    want_acc = true;
    first_done = false;
    bdd_allsat(b, print_accset_handler);
    if (first_done)
      *where << "}";
    return os;
  }

  std::string
  bdd_format_sat(const bdd_dict* d, bdd b)
  {
    std::ostringstream os;
    bdd_print_sat(os, d, b);
    return os.str();
  }

  std::ostream&
  bdd_print_set(std::ostream& os, const bdd_dict* d, bdd b)
  {
    dict = d;
    want_acc = true;
    bdd_strm_hook(print_handler);
    os << bddset << b;
    bdd_strm_hook(0);
    return os;
  }

  std::string
  bdd_format_set(const bdd_dict* d, bdd b)
  {
    std::ostringstream os;
    bdd_print_set(os, d, b);
    return os.str();
  }

  std::ostream&
  bdd_print_formula(std::ostream& os, const bdd_dict* d, bdd b)
  {
    const ltl::formula* f = bdd_to_formula(b, d);
    to_string(f, os);
    destroy(f);
    return os;
  }

  std::string
  bdd_format_formula(const bdd_dict* d, bdd b)
  {
    std::ostringstream os;
    bdd_print_formula(os, d, b);
    return os.str();
  }

  std::ostream&
  bdd_print_dot(std::ostream& os, const bdd_dict* d, bdd b)
  {
    dict = d;
    want_acc = true;
    bdd_strm_hook(print_handler);
    os << bdddot << b;
    bdd_strm_hook(0);
    return os;
  }

  std::ostream&
  bdd_print_table(std::ostream& os, const bdd_dict* d, bdd b)
  {
    dict = d;
    want_acc = true;
    bdd_strm_hook(print_handler);
    os << bddtable << b;
    bdd_strm_hook(0);
    return os;
  }

}
