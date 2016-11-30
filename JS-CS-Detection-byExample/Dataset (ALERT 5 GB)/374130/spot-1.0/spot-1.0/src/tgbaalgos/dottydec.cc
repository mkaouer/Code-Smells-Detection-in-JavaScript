// Copyright (C) 2011 Laboratoire de Recherche et Developpement de
// l'Epita (LRDE).
// Copyright (C) 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
// d�partement Syst�mes R�partis Coop�ratifs (SRC), Universit� Pierre
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

#include "dottydec.hh"
#include "tgba/tgba.hh"

namespace spot
{
  dotty_decorator::dotty_decorator()
  {
  }

  dotty_decorator::~dotty_decorator()
  {
  }

  std::string
  dotty_decorator::state_decl(const tgba*, const state*, int,
			      tgba_succ_iterator*, const std::string& label,
			      bool accepting)
  {
    if (accepting)
      return "[label=\"" + label + "\", peripheries=2]";
    else
      return "[label=\"" + label + "\"]";
  }

  std::string
  dotty_decorator::link_decl(const tgba*, const state*, int, const state*, int,
			     const tgba_succ_iterator*,
			     const std::string& label)
  {
    return "[label=\"" + label + "\"]";
  }

  dotty_decorator*
  dotty_decorator::instance()
  {
    static dotty_decorator d;
    return &d;
  }
}
