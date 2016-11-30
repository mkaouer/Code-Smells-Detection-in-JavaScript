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

#ifndef SPOT_TGBA_STATEBDD_HH
# define SPOT_TGBA_STATEBDD_HH

#include <bdd.h>
#include "state.hh"

namespace spot
{
  /// A state whose representation is a BDD.
  class state_bdd: public state
  {
  public:
    state_bdd(bdd s)
      : state_(s)
    {
    }

    /// Return the BDD part of the state.
    virtual bdd
    as_bdd() const
    {
      return state_;
    }

    virtual int compare(const state* other) const;
    virtual size_t hash() const;
    virtual state_bdd* clone() const;

  protected:
    bdd state_;			///< BDD representation of the state.
  };
}

#endif // SPOT_TGBA_STATEBDD_HH
