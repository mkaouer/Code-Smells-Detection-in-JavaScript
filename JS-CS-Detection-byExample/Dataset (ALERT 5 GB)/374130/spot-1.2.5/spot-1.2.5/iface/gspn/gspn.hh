// Copyright (C) 2003, 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_IFACE_GSPN_GSPN_HH
# define SPOT_IFACE_GSPN_GSPN_HH

// Do not include gspnlib.h here, or it will polute the user's
// namespace with internal C symbols.

# include <string>
# include "tgba/tgba.hh"
# include "common.hh"
# include "ltlenv/declenv.hh"

namespace spot
{

  class gspn_interface
  {
  public:
    gspn_interface(int argc, char **argv,
		   bdd_dict* dict, ltl::declarative_environment& env,
		   const std::string& dead = "true");
    ~gspn_interface();
    tgba* automaton() const;
  private:
    bdd_dict* dict_;
    ltl::declarative_environment& env_;
    const std::string dead_;
  };
}

#endif // SPOT_IFACE_GSPN_GSPN_HH
