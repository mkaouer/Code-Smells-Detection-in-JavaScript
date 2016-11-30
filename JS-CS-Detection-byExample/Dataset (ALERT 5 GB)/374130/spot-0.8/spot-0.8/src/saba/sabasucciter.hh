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

#ifndef SPOT_SABA_SABASUCCITER_HH
# define SPOT_SABA_SABASUCCITER_HH

#include "sabastate.hh"

namespace spot
{
  /// \brief Iterate over a conjunction of saba_state.
  /// \ingroup saba_essentials
  ///
  /// This class provides the basic functionalities required to
  /// iterate over a conjunction of states of a saba.
  class saba_state_conjunction
  {
  public:
    virtual
    ~saba_state_conjunction()
    {
    }

    /// \name Iteration
    //@{

    /// \brief Position the iterator on the first successor of the conjunction
    /// (if any).
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

    /// \brief Check whether the iteration over a conjunction of states
    /// is finished.
    ///
    /// This function should be called after any call to \c first()
    /// or \c next() and before any enquiry about the current state.
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
    /// \warning the state is allocated with new, its deletion is
    /// the responsibility of the caller.
    virtual saba_state* current_state() const = 0;

    //@}

    /// Duplicate a saba_state conjunction.
    virtual saba_state_conjunction* clone() const = 0;
  };


  /// \brief Iterate over the successors of a saba_state.
  /// \ingroup saba_essentials
  ///
  /// This class provides the basic functionalities required to
  /// iterate over the successors of a state of a saba. Since
  /// transitions of an alternating automaton are defined as a
  /// boolean function with conjunctions (universal) and
  /// disjunctions (non-deterministic),
  class saba_succ_iterator
  {
  public:
    virtual
    ~saba_succ_iterator()
    {
    }

    /// \name Iteration
    //@{

    /// \brief Position the iterator on the first conjunction
    /// of successors (if any).
    ///
    /// This method can be called several times to make multiple
    /// passes over successors.
    ///
    /// \warning One should always call \c done() to
    /// ensure there is a successor, even after \c first().  A
    /// common trap is to assume that there is at least one
    /// successor: this is wrong.
    virtual void first() = 0;

    /// \brief Jump to the next conjunction of successors (if any).
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
    /// \code
    ///    for (s->first(); !s->done(); s->next())
    ///      ...
    /// \endcode
    virtual bool done() const = 0;

    //@}

    /// \name Inspection
    //@{

    /// \brief Get current conjunction of successor states.
    virtual saba_state_conjunction* current_conjunction() const = 0;

    /// \brief Get the condition on the transition leading to this successor.
    ///
    /// This is a boolean function of atomic propositions.
    virtual bdd current_condition() const = 0;

    //@}
  };
}


#endif // SPOT_TGBA_SUCCITER_HH
