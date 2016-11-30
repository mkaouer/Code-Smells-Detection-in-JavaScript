// Copyright (C) 2003, 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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
      last_support_variables_input_(0),
      num_acc_(-1)
  {
  }

  tgba::~tgba()
  {
    delete last_support_conditions_input_;
    delete last_support_variables_input_;
  }

  bdd
  tgba::support_conditions(const state* state) const
  {
    if (!last_support_conditions_input_
	|| last_support_conditions_input_->compare(state) != 0)
      {
	last_support_conditions_output_ = compute_support_conditions(state);
	delete last_support_conditions_input_;
	last_support_conditions_input_ = state->clone();
      }
    return last_support_conditions_output_;
  }

  bdd
  tgba::support_variables(const state* state) const
  {
    if (!last_support_variables_input_
	|| last_support_variables_input_->compare(state) != 0)
      {
	last_support_variables_output_ = compute_support_variables(state);
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

  std::string
  tgba::transition_annotation(const tgba_succ_iterator*) const
  {
    return "";
  }

  unsigned int
  tgba::number_of_acceptance_conditions() const
  {
    if (num_acc_ < 0)
      {
	bdd all = all_acceptance_conditions();
	unsigned int n = 0;
	while (all != bddfalse)
	  {
	    ++n;
	    all -= bdd_satone(all);
	  }
	num_acc_ = n;
      }
    return num_acc_;
  }

}
