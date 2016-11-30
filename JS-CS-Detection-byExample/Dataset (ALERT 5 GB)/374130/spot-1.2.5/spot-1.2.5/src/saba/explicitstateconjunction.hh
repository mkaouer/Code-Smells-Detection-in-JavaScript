// -*- coding: utf-8 -*-
// Copyright (C) 2009, 2012, 2013 Laboratoire de Recherche et
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

#ifndef SPOT_SABA_EXPLICITSTATECONJUNCTION_HH
# define SPOT_SABA_EXPLICITSTATECONJUNCTION_HH

#include "misc/common.hh"
#include "misc/hash.hh"
#include "sabasucciter.hh"

namespace spot
{
  /// \ingroup saba_essentials
  /// \brief Basic implementation of saba_state_conjunction.
  ///
  /// This class provides a basic implementation to
  /// iterate over a conjunction of states of a saba.
  class SPOT_API explicit_state_conjunction : public saba_state_conjunction
  {
  public:

    explicit_state_conjunction();
    explicit_state_conjunction(const explicit_state_conjunction* other);
    virtual ~explicit_state_conjunction();

    explicit_state_conjunction& operator=(const explicit_state_conjunction& o);

    /// \name Iteration
    //@{

    virtual void first();
    virtual void next();
    virtual bool done() const;

    //@}

    /// \name Inspection
    //@{

    /// Duplicate a this conjunction.
    explicit_state_conjunction* clone() const;

    /// Return the a new instance on the current state.
    /// This is the caller responsibility to delete the returned state.
    virtual saba_state* current_state() const;

    //@}

    /// Add a new state in the conjunction.
    /// The class becomes owner of \a state.
    void add(saba_state* state);
  private:
    typedef Sgi::hash_set<shared_saba_state,
                          spot::saba_state_shared_ptr_hash,
                          spot::saba_state_shared_ptr_equal> saba_state_set_t;
    saba_state_set_t set_;
    saba_state_set_t::iterator it_;
  };
} // end namespace spot.


#endif  // SPOT_SABA_EXPLICITSTATECONJUNCTION_HH
