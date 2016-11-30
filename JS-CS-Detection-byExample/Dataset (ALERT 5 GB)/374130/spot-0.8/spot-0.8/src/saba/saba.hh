// Copyright (C) 2009, 2010 Laboratoire de Recherche et Développement
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

#ifndef SPOT_SABA_SABA_HH
# define SPOT_SABA_SABA_HH

#include "sabastate.hh"
#include "sabasucciter.hh"
#include <tgba/bdddict.hh>

namespace spot
{
  /// \defgroup saba SABA (State-based Alternating Büchi Automata)
  ///
  /// Spot was centered around non-deterministic \ref tgba.
  /// Alternating automata are an extension to non-deterministic
  /// automata, and are presented with spot::saba.
  /// This type and its cousins are listed \ref saba_essentials "here".
  /// This is an abstract interface.

  /// \addtogroup saba_essentials Essential SABA types
  /// \ingroup saba

  /// \brief A State-based Alternating (Generalized) Büchi Automaton.
  /// \ingroup saba_essentials
  ///
  /// Browsing such automaton can be achieved using two functions:
  /// \c get_init_state, and \c succ_iter.  The former returns
  /// the initial state while the latter lists the
  /// successor states of any state.
  ///
  /// Note that although this is a transition-based automata,
  /// we never represent transitions!  Transition informations are
  /// obtained by querying the iterator over the successors of
  /// a state.
  class saba
  {
  protected:
    saba();

  public:
    virtual ~saba();

    /// \brief Get the initial state of the automaton.
    ///
    /// The state has been allocated with \c new.  It is the
    /// responsability of the caller to \c delete it when no
    /// longer needed.
    virtual saba_state* get_init_state() const = 0;

    /// \brief Get an iterator over the successors of \a local_state.
    ///
    /// The iterator has been allocated with \c new.  It is the
    /// responsability of the caller to \c delete it when no
    /// longer needed.
    ///
    /// \param local_state The state whose successors are to be explored.
    /// This pointer is not adopted in any way by \c succ_iter, and
    /// it is still the caller's responsability to delete it when
    /// appropriate (this can be done during the lifetime of
    /// the iterator).
    virtual saba_succ_iterator*
    succ_iter(const saba_state* local_state) const = 0;

    /// \brief Get the dictionary associated to the automaton.
    ///
    /// State are represented as BDDs.  The dictionary allows
    /// to map BDD variables back to formulae, and vice versa.
    /// This is useful when dealing with several automata (which
    /// may use the same BDD variable for different formula),
    /// or simply when printing.
    virtual bdd_dict* get_dict() const = 0;

    /// \brief Format the state as a string for printing.
    ///
    /// This formating is the responsability of the automata
    /// that owns the state.
    virtual std::string format_state(const saba_state* state) const = 0;

    /// \brief Return the set of all acceptance conditions used
    /// by this automaton.
    ///
    /// The goal of the emptiness check is to ensure that
    /// a strongly connected component walks through each
    /// of these acceptiong conditions.  I.e., the union
    /// of the acceptiong conditions of all transition in
    /// the SCC should be equal to the result of this function.
    virtual bdd all_acceptance_conditions() const = 0;

    /// The number of acceptance conditions.
    virtual unsigned int number_of_acceptance_conditions() const;
  private:
    mutable int num_acc_;
  };

} // end namespace spot.


#endif // SPOT_SABA_SABA_HH
