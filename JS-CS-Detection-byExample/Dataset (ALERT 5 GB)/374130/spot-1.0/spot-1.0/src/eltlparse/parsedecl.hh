// Copyright (C) 2008 Laboratoire de Recherche et Développement
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

#ifndef SPOT_ELTLPARSE_PARSEDECL_HH
# define SPOT_ELTLPARSE_PARSEDECL_HH

#include "eltlparse.hh"
#include "location.hh"

# define YY_DECL \
  int eltlyylex (eltlyy::parser::semantic_type *yylval, \
		 eltlyy::location *yylloc, \
		 spot::eltl::parse_error_list_t &pe)
YY_DECL;

namespace spot
{
  namespace eltl
  {
    int  flex_open(const std::string& name);
    void flex_close();
    void flex_scan_string(const char* s);
  }
}

#endif // SPOT_ELTLPARSE_PARSEDECL_HH
