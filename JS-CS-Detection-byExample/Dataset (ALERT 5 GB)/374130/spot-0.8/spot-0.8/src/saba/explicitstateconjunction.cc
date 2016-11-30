// Copyright (C) 2009 Laboratoire de Recherche et Développement
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

#include "explicitstateconjunction.hh"

namespace spot
{

  /// explicit_state_conjunction
  ////////////////////////////////////////

  explicit_state_conjunction::explicit_state_conjunction()
  {
  }

  explicit_state_conjunction::
  explicit_state_conjunction(const explicit_state_conjunction* other)
    : set_(other->set_)
  {
  }

  explicit_state_conjunction*
  explicit_state_conjunction::operator=(const explicit_state_conjunction& o)
  {
    if (this != &o)
    {
      this->~explicit_state_conjunction();
      new (this) explicit_state_conjunction(&o);
    }
    return this;
  }

  explicit_state_conjunction::~explicit_state_conjunction()
  {
  }

  void
  explicit_state_conjunction::first()
  {
    it_ = set_.begin();
  }

  void
  explicit_state_conjunction::next()
  {
    ++it_;
  }

  bool
  explicit_state_conjunction::done() const
  {
    return it_ == set_.end();
  }

  explicit_state_conjunction*
  explicit_state_conjunction::clone() const
  {
    return new explicit_state_conjunction(this);
  }

  saba_state*
  explicit_state_conjunction::current_state() const
  {
    return (*it_)->clone();
  }

  void
  explicit_state_conjunction::add(saba_state* state)
  {
    set_.insert(shared_saba_state(state));
  }

} // end namespace spot.
