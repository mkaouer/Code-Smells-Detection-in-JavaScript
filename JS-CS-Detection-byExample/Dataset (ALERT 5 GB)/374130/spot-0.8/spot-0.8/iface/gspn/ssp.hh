// Copyright (C) 2008 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2006, 2007 Laboratoire d'Informatique de
// Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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

#ifndef SPOT_IFACE_GSPN_SSP_HH
# define SPOT_IFACE_GSPN_SSP_HH

// Do not include gspnlib.h here, or it will polute the user's
// namespace with internal C symbols.

# include <string>
# include "tgba/tgba.hh"
# include "common.hh"
# include "tgbaalgos/gtec/gtec.hh"
# include "tgbaalgos/gtec/ce.hh"
# include "ltlenv/declenv.hh"

namespace spot
{

  class gspn_ssp_interface
  {
  public:
    gspn_ssp_interface(int argc, char **argv,
		       bdd_dict* dict, const ltl::declarative_environment& env,
		       bool inclusion = false,
		       bool doublehash = true,
		       bool pushfront = false);
    ~gspn_ssp_interface();
    tgba* automaton(const tgba* operand) const;
  private:
    bdd_dict* dict_;
    const ltl::declarative_environment& env_;
  };

  /// \defgroup emptiness_check_ssp Emptiness-check algorithms for SSP
  /// \ingroup emptiness_check
  /// @{
  couvreur99_check* couvreur99_check_ssp_semi(const tgba* ssp_automata);
  couvreur99_check* couvreur99_check_ssp_shy_semi(const tgba* ssp_automata);
  couvreur99_check* couvreur99_check_ssp_shy(const tgba* ssp_automata,
					     bool stack_inclusion = true,
					     bool double_inclusion = false,
					     bool reversed_double_inclusion
					     = false,
					     bool no_decomp = false);

  /// @}

  // I rewrote couvreur99_check_result today, and it no longer use
  // connected_component_ssp_factory.  So this cannot work anymore.
  // -- adl 2004-12-10.
  // couvreur99_check_result*
  // counter_example_ssp(const couvreur99_check_status* status);
}

#endif // SPOT_IFACE_GSPN_SSP_GSPN_SSP_HH
