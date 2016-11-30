// Copyright (C) 2009  Laboratoire de recherche et développement de l'Epita.
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

#include <sstream>
#include "tgbascc.hh"

namespace spot
{

  tgba_scc::tgba_scc(const tgba* aut, bool show)
    : aut_(aut), scc_map_(aut), show_(show)
  {
    scc_map_.build_map();
  }

  tgba_scc::~tgba_scc()
  {
  }

  unsigned
  tgba_scc::scc_of_state(const spot::state* st) const
  {
    return scc_map_.scc_of_state(st);
  }

  state*
  tgba_scc::get_init_state() const
  {
    return aut_->get_init_state();
  }

  tgba_succ_iterator*
  tgba_scc::succ_iter(const state* local_state,
					 const state* global_state,
					 const tgba* global_automaton) const
  {
    return aut_->succ_iter(local_state, global_state, global_automaton);
  }

  bdd_dict*
  tgba_scc::get_dict() const
  {
    return aut_->get_dict();
  }

  std::string
  tgba_scc::format_state(const state* state) const
  {
    if (!show_)
      return aut_->format_state(state);

    std::ostringstream str;
    str << aut_->format_state(state);
    str << "\\nSCC #" << scc_of_state(state);
    return str.str();
  }

  std::string
  tgba_scc::transition_annotation
    (const tgba_succ_iterator* t) const
  {
    return aut_->transition_annotation(t);
  }

  state*
  tgba_scc::project_state(const state* s,
					     const tgba* t) const
  {
    return aut_->project_state(s, t);
  }

  bdd
  tgba_scc::all_acceptance_conditions() const
  {
    return aut_->all_acceptance_conditions();
  }

  bdd
  tgba_scc::neg_acceptance_conditions() const
  {
    return aut_->neg_acceptance_conditions();
  }

  bdd
  tgba_scc::compute_support_conditions
    (const state* state) const
  {
    return aut_->support_conditions(state);
  }

  bdd
  tgba_scc::compute_support_variables
    (const state* state) const
  {
    return aut_->support_variables(state);
  }
}
