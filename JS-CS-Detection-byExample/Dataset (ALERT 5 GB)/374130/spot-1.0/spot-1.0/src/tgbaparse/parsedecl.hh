// Copyright (C) 2003, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
// et Marie Curie.
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

#ifndef SPOT_TGBAPARSE_PARSEDECL_HH
# define SPOT_TGBAPARSE_PARSEDECL_HH

#include <string>
#include "tgbaparse.hh"
#include "location.hh"

# define YY_DECL \
  int tgbayylex (tgbayy::parser::semantic_type *yylval, \
		 tgbayy::location *yylloc)
YY_DECL;

namespace spot
{
  int tgbayyopen(const std::string& name);
  void tgbayyclose();
}


// Gross kludge to compile yy::Parser in another namespace (tgbayy::)
// but still use yy::Location.  The reason is that Bison's C++
// skeleton does not support anything close to %name-prefix at the
// moment.  All parser are named yy::Parser which makes it somewhat
// difficult to define multiple parsers.
// namespace tgbayy
// {
//   using namespace yy;
// }
// #define yy tgbayy



#endif // SPOT_TGBAPARSE_PARSEDECL_HH
