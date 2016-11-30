// -*- coding: utf-8 -*-
// Copyright (C) 2011, 2013 Laboratoire de Recherche et Developpement de
// l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
// et Marie Curie.
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

#ifndef SPOT_TGBA_SUCCITER_HH
# define SPOT_TGBA_SUCCITER_HH

#include "state.hh"

namespace spot
{
  /// \ingroup tgba_essentials
  /// \brief Iterate over the successors of a state.
  ///
  /// This class provides the basic functionalities required to
  /// iterate over the successors of a state, as well as querying
  /// transition labels.  Because transitions are never explicitely
  /// encoded, labels (conditions and acceptance conditions) can only
  /// be queried while iterating over the successors.
  class SPOT_API tgba_succ_iterator
  {
  public:
    virtual
    ~tgba_succ_iterator()
    {
    }

    /// \name Iteration
    //@{

    /// \brief Position the iterator on the first successor (if any).
    ///
    /// This method can be called several times to make multiple
    /// passes over successors.
    ///
    /// \warning One should always call \c done() to
    /// ensure there is a successor, even after \c first().  A
    /// common trap is to assume that there is at least one
    /// successor: this is wrong.
    virtual void first() = 0;

    /// \brief Jump to the next successor (if any).
    ///
    /// \warning Again, one should always call \c done() to ensure
    /// there is a successor.
    virtual void next() = 0;

    /// \brief Check whether the iteration is finished.
    ///
    /// This function should be called after any call to \c first()
    /// or \c next() and before any enquiry about the current state.
    ///
    /// The usual way to do this is with a \c for loop.
    ///
    ///     for (s->first(); !s->done(); s->next())
    ///       ...
    virtual bool done() const = 0;

    //@}

    /// \name Inspection
    //@{

    /// \brief Get the state of the current successor.
    ///
    /// Note that the same state may occur at different points
    /// in the iteration.  These actually correspond to the same
    /// destination.  It just means there were several transitions,
    /// with different conditions, leading to the same state.
    ///
    /// The returned state should be destroyed (see state::destroy)
    /// by the caller after it is no longer used.
    virtual state* current_state() const = 0;
    /// \brief Get the condition on the transition leading to this successor.
    ///
    /// This is a boolean function of atomic propositions.
    virtual bdd current_condition() const = 0;
    /// \brief Get the acceptance conditions on the transition leading
    /// to this successor.
    virtual bdd current_acceptance_conditions() const = 0;

    //@}
  };
}


#endif // SPOT_TGBA_SUCCITER_HH
