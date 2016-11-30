// Copyright (C) 2010, 2012, 2013 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2005 Laboratoire d'Informatique de Paris
// 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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

#include "public.hh"
#include <ostream>
#include <iterator>
#include <vector>
#include "utf8/utf8.h"

namespace spot
{
  namespace ltl
  {

    void
    fix_utf8_locations(const std::string& ltl_string,
		       parse_error_list& error_list)
    {
      // LUT to convert byte positions to utf8 positions.
      // (The +2 is to account for position 0, not used,
      // and position ltl_string.size()+1 denoting EOS.)
      std::vector<unsigned> b2u(ltl_string.size() + 2);

      // i will iterate over all utf8 characters between b and e
      std::string::const_iterator b = ltl_string.begin();
      std::string::const_iterator i = b;
      std::string::const_iterator e = ltl_string.end();

      unsigned n = 0;		// number of current utf8 character
      unsigned prev = 0;	// last byte of previous utf8 character
      while (i != e)
	{
	  utf8::next(i, e);
	  ++n;
	  unsigned d = std::distance(b, i);
	  while (prev < d)
	    b2u[++prev] = n;
	}
      b2u[++prev] = ++n;

      // use b2u to update error_list
      parse_error_list::iterator it;
      for (it = error_list.begin(); it != error_list.end(); ++it)
	{
	  location& l = it->first;
	  l.begin.column = b2u[l.begin.column];
	  l.end.column = b2u[l.end.column];
	}
    }

    namespace
    {
      bool
      format_parse_errors_aux(std::ostream& os,
			      const std::string& ltl_string,
			      const parse_error_list& error_list)
      {
	bool printed = false;
	parse_error_list::const_iterator it;
	for (it = error_list.begin(); it != error_list.end(); ++it)
	  {
	    os << ">>> " << ltl_string << std::endl;
	    const location& l = it->first;

	    unsigned n = 1;
	    for (; n < 4 + l.begin.column; ++n)
	      os << ' ';
	    // Write at least one '^', even if begin==end.
	    os << '^';
	    ++n;
	    for (; n < 4 + l.end.column; ++n)
	      os << '^';
	    os << std::endl << it->second << std::endl << std::endl;
	    printed = true;
	  }
	return printed;
      }
    }

    bool
    format_parse_errors(std::ostream& os,
			const std::string& ltl_string,
			const parse_error_list& error_list)
    {
      if (utf8::is_valid(ltl_string.begin(), ltl_string.end()))
	{
	  parse_error_list fixed = error_list;
	  fix_utf8_locations(ltl_string, fixed);
	  return format_parse_errors_aux(os, ltl_string, fixed);
	}
      else
	{
	  return format_parse_errors_aux(os, ltl_string, error_list);
	}
    }

  }
}
