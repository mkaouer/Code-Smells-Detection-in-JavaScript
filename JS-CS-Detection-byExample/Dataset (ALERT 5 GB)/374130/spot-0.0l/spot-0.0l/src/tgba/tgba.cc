// Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
// et Marie Curie.
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

#include "tgba.hh"

namespace spot
{
  tgba::tgba()
    : last_support_conditions_input_(0),
      last_support_variables_input_(0)
  {
  }

  tgba::~tgba()
  {
    if (last_support_conditions_input_)
      delete last_support_conditions_input_;
    if (last_support_variables_input_)
      delete last_support_variables_input_;
  }

  bdd
  tgba::support_conditions(const state* state) const
  {
    if (! last_support_conditions_input_
	|| last_support_conditions_input_->compare(state) != 0)
      {
	last_support_conditions_output_ =
	  compute_support_conditions(state);
	if (last_support_conditions_input_)
	  delete last_support_conditions_input_;
	last_support_conditions_input_ = state->clone();
      }
    return last_support_conditions_output_;
  }

  bdd
  tgba::support_variables(const state* state) const
  {
    if (! last_support_variables_input_
	|| last_support_variables_input_->compare(state) != 0)
      {
	last_support_variables_output_ =
	  compute_support_variables(state);
	if (last_support_variables_input_)
	  delete last_support_variables_input_;
	last_support_variables_input_ = state->clone();
      }
    return last_support_variables_output_;
  }

  state*
  tgba::project_state(const state* s, const tgba* t) const
  {
    if (t == this)
      return s->clone();
    return 0;
  }

}
