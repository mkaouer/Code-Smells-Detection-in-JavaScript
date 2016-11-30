// Copyright (C) 2010, 2011, 2012 Laboratoire de Recherche et
// Developpement de l'Epita (LRDE).
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

#include "ltlast/atomic_prop.hh"
#include "ltlast/constant.hh"
#include "tgtaexplicit.hh"
#include "tgba/formula2bdd.hh"
#include "misc/bddop.hh"
#include "ltlvisit/tostring.hh"

#include "tgba/bddprint.hh"

namespace spot
{

  tgta_explicit::tgta_explicit(const tgba* tgba, bdd all_acceptance_conditions,
			       state_ta_explicit* artificial_initial_state,
			       bool own_tgba) :
    ta_(tgba, all_acceptance_conditions, artificial_initial_state, own_tgba)
  {
  }

  state*
  tgta_explicit::get_init_state() const
  {
    return ta_.get_artificial_initial_state();
  }

  tgba_succ_iterator*
  tgta_explicit::succ_iter(const spot::state* state, const spot::state*,
			   const tgba*) const
  {
    return ta_.succ_iter(state);
  }

  bdd
  tgta_explicit::compute_support_conditions(const spot::state* in) const
  {
    const state_ta_explicit* s = down_cast<const state_ta_explicit*>(in);
    assert(s);
    return ta_.get_tgba()->support_conditions(s->get_tgba_state());
  }

  bdd
  tgta_explicit::compute_support_variables(const spot::state* in) const
  {
    const state_ta_explicit* s = down_cast<const state_ta_explicit*>(in);
    assert(s);
    return ta_.get_tgba()->support_variables(s->get_tgba_state());
  }

  bdd_dict*
  tgta_explicit::get_dict() const
  {
    return ta_.get_dict();
  }

  bdd
  tgta_explicit::all_acceptance_conditions() const
  {
    return ta_.all_acceptance_conditions();
  }

  bdd
  tgta_explicit::neg_acceptance_conditions() const
  {
    return ta_.get_tgba()->neg_acceptance_conditions();
  }

  std::string
  tgta_explicit::format_state(const spot::state* s) const
  {
    return ta_.format_state(s);
  }

  spot::tgba_succ_iterator*
  tgta_explicit::succ_iter_by_changeset(const spot::state* s, bdd chngset) const
  {
    return ta_.succ_iter(s, chngset);
  }

}
