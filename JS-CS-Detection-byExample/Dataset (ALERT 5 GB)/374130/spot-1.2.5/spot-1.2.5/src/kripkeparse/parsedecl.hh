// -*- coding: utf-8 -*-
// Copyright (C) 2011, 2013 Laboratoire de Recherche et Developpement
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


#ifndef SPOT_KRIPKEPARSE_PARSEDECL_HH
# define SPOT_KRIPKEPARSE_PARSEDECL_HH

# include <string>
# include "kripkeparse.hh"
# include "misc/location.hh"

# define YY_DECL \
  int kripkeyylex (kripkeyy::parser::semantic_type *yylval, \
                   spot::location *yylloc)

YY_DECL;

namespace spot
{
  int kripkeyyopen(const std::string& name);
  void kripkeyyclose();
}

#endif /* !PARSEDECL_HH_ */
