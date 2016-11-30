// Copyright (C) 2013 Laboratoire de Recherche et
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
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

#include "tgbaproxy.hh"

namespace spot
{
  tgba_proxy::tgba_proxy(const tgba* original)
    : original_(original)
  {
    get_dict()->register_all_variables_of(original, this);
  }

  tgba_proxy::~tgba_proxy()
  {
    get_dict()->unregister_all_my_variables(this);
  }

  state* tgba_proxy::get_init_state() const
  {
    return original_->get_init_state();
  }

  tgba_succ_iterator*
  tgba_proxy::succ_iter(const state* local_state,
			const state* global_state,
			const tgba* global_automaton) const
  {
    return original_->succ_iter(local_state, global_state, global_automaton);
  }

  bdd_dict*
  tgba_proxy::get_dict() const
  {
    return original_->get_dict();
  }

  std::string
  tgba_proxy::format_state(const state* state) const
  {
    return original_->format_state(state);
  }

  std::string
  tgba_proxy::transition_annotation(const tgba_succ_iterator* t) const
  {
    return original_->transition_annotation(t);
  }

  state*
  tgba_proxy::project_state(const state* s, const tgba* t) const
  {
    return original_->project_state(s, t);
  }

  bdd
  tgba_proxy::all_acceptance_conditions() const
  {
    return original_->all_acceptance_conditions();
  }

  bdd
  tgba_proxy::neg_acceptance_conditions() const
  {
    return original_->neg_acceptance_conditions();
  }

  bdd
  tgba_proxy::compute_support_conditions(const state* state) const
  {
    return original_->support_conditions(state);
  }

  bdd
  tgba_proxy::compute_support_variables(const state* state) const
  {
    return original_->support_variables(state);
  }
}

