// Copyright (C) 2009, 2010 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
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

#ifndef SPOT_TGBAALGOS_LTL2TAA_HH
# define SPOT_TGBAALGOS_LTL2TAA_HH

#include "ltlast/formula.hh"
#include "tgba/taatgba.hh"

namespace spot
{
  /// \brief Build a spot::taa* from an LTL formula.
  /// \ingroup tgba_ltl
  ///
  /// This is based on the following.
  /// \verbatim
  /// @techreport{HUT-TCS-A104,
  ///     address = {Espoo, Finland},
  ///     author  = {Heikki Tauriainen},
  ///     month   = {September},
  ///     note    = {Doctoral dissertation},
  ///     number  = {A104},
  ///     pages   = {xii+229},
  ///     title   = {Automata and Linear Temporal Logic: Translations
  ///                with Transition-Based Acceptance},
  ///     type    = {Research Report},
  ///     year    = {2006}
  /// }
  /// \endverbatim
  ///
  /// \param f The formula to translate into an automaton.
  /// \param dict The spot::bdd_dict the constructed automata should use.
  /// \param refined_rules If this parameter is set, refined rules are used.
  /// \return A spot::taa that recognizes the language of \a f.
  taa_tgba* ltl_to_taa(const ltl::formula* f, bdd_dict* dict,
                       bool refined_rules = false);
}

#endif // SPOT_TGBAALGOS_LTL2TAA_HH
