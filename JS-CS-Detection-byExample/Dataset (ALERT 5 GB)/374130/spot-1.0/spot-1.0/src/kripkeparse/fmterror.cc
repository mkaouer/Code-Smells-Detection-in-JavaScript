// Copyright (C) 2011 Laboratoire de Recherche et Developpement
// de l'Epita (LRDE)
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

#include <ostream>
#include "public.hh"

namespace spot
{
  bool
  format_kripke_parse_errors(std::ostream& os,
			   const std::string& filename,
			   kripke_parse_error_list& error_list)
  {
    bool printed = false;
    spot::kripke_parse_error_list::iterator it;
    for (it = error_list.begin(); it != error_list.end(); ++it)
      {
	if (filename != "-")
	  os << filename << ":";
	os << it->first << ": ";
	os << it->second << std::endl;
	printed = true;
      }
    return printed;
  }
}
