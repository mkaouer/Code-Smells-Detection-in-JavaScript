// Copyright (C) 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_EVTGBA_EVTGBAITER_HH
# define SPOT_EVTGBA_EVTGBAITER_HH

#include "tgba/state.hh"
#include "symbol.hh"
#include "evtgbaiter.hh"

namespace spot
{
  // FIXME: doc me
  class evtgba_iterator
  {
  public:
    virtual
    ~evtgba_iterator()
    {
    }

    virtual void first() = 0;
    virtual void next() = 0;
    virtual bool done() const = 0;

    virtual const state* current_state() const = 0;
    virtual const symbol* current_label() const = 0;
    virtual symbol_set current_acceptance_conditions() const = 0;
  };
}

#endif // SPOT_EVTGBA_EVTGBAITER_HH
