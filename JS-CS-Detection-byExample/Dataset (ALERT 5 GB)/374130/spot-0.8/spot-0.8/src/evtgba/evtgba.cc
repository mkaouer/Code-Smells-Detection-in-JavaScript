// Copyright (C) 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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
#include "evtgba.hh"
#include "misc/escape.hh"
#include "misc/bareword.hh"

namespace spot
{
  evtgba::evtgba()
  {
  }

  evtgba::~evtgba()
  {
  }

  std::string
  evtgba::format_label(const symbol* symbol) const
  {
    return symbol->name();
  }

  std::string
  evtgba::format_acceptance_condition(const symbol* symbol) const
  {
    return symbol->name();
  }

  std::string
  evtgba::format_acceptance_conditions(const symbol_set& symset) const
  {
    std::ostringstream o;
    symbol_set::const_iterator i = symset.begin();
    if (i != symset.end())
      {
	o << '{';
	for (;;)
	  {
	    o << quote_unless_bare_word(format_acceptance_condition(*i));
	    if (++i == symset.end())
	      break;
	    o << ", ";
	  }
	o << '}';
      }
    return o.str();
  }

}
