// Copyright (C) 2010, 2011, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
//
// This file is part of Spot, a model checking library.
//
// Spot is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or
// (at your option) any later version.
//
// Spot is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANta_explicitBILITY
// or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more deta_explicitils.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

#ifndef SPOT_TA_TGTAEXPLICIT_HH
# define SPOT_TA_TGTAEXPLICIT_HH

#include "misc/hash.hh"
#include <list>
#include "tgba/tgba.hh"
#include <set>
#include "ltlast/formula.hh"
#include <cassert>
#include "misc/bddlt.hh"
#include "taexplicit.hh"
#include "tgta.hh"

namespace spot
{

  /// Explicit representation of a spot::tgta.
  /// \ingroup ta_representation
  class tgta_explicit : public tgta
  {
  public:
    tgta_explicit(const tgba* tgba, bdd all_acceptance_conditions,
		  state_ta_explicit* artificial_initial_state,
		  bool own_tgba = false);

    // tgba interface
    virtual spot::state* get_init_state() const;

    virtual tgba_succ_iterator*
    succ_iter(const spot::state* local_state, const spot::state* global_state =
        0, const tgba* global_automaton = 0) const;

    virtual bdd_dict*
    get_dict() const;

    const ta_explicit* get_ta() const { return &ta_; }
    ta_explicit* get_ta() { return &ta_; }

    virtual bdd all_acceptance_conditions() const;
    virtual bdd neg_acceptance_conditions() const;

    virtual std::string format_state(const spot::state* s) const;

    virtual tgba_succ_iterator*
    succ_iter_by_changeset(const spot::state* s, bdd change_set) const;
  protected:
    virtual bdd compute_support_conditions(const spot::state* state) const;
    virtual bdd compute_support_variables(const spot::state* state) const;

    ta_explicit ta_;
  };

}

#endif // SPOT_TA_TGTAEXPLICIT_HH
