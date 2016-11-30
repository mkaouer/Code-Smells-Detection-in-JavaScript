// Copyright (C) 2010, 2012 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
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

#include "ltlfile.hh"
#include "public.hh"

namespace spot
{
  namespace ltl
  {

    ltl_file::ltl_file(const std::string& filename)
      : in(filename.c_str())
    {
      if (!in)
	{
	  std::cerr << "Cannot open " << filename << std::endl;
	  exit(2);
	}
    }

    ltl_file::ltl_file(const char* filename)
      : in(filename)
    {
      if (!in)
	{
	  std::cerr << "Cannot open " << filename << std::endl;
	  exit(2);
	}
    }

    const formula* ltl_file::next()
    {
      if (!in.good())
	return 0;

      std::string input;
      do
	{
	  if (!std::getline(in, input))
	    return 0;
	}
      while (input == "");

      spot::ltl::parse_error_list pel;
      const formula* f = parse(input, pel);
      int ret = spot::ltl::format_parse_errors(std::cerr, input, pel);
      if (ret)
	exit(ret);
      return f;
    }

  }
}
