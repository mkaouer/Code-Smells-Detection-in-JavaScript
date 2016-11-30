// Copyright (C) 2009, 2010 Laboratoire de Recherche et Developpement de l'Epita
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

#ifndef SPOT_KRIPKE_FAIRKRIPKE_HH
# define SPOT_KRIPKE_FAIRKRIPKE_HH

#include "tgba/tgba.hh"
#include "tgba/succiter.hh"

/// \addtogroup kripke Kripke Structures
/// \ingroup tgba

namespace spot
{
  class fair_kripke;

  /// \brief Iterator code for a Fair Kripke structure.
  /// \ingroup kripke
  ///
  /// This iterator can be used to simplify the writing
  /// of an iterator on a Fair Kripke structure (or lookalike).
  ///
  /// If you inherit from this iterator, you should only
  /// redefine
  ///
  ///   - fair_kripke_succ_iterator::first()
  ///   - fair_kripke_succ_iterator::next()
  ///   - fair_kripke_succ_iterator::done()
  ///   - fair_kripke_succ_iterator::current_state()
  ///
  /// This class implements fair_kripke_succ_iterator::current_condition(),
  /// and fair_kripke_succ_iterator::current_acceptance_conditions().
  class fair_kripke_succ_iterator : public tgba_succ_iterator
  {
  public:
    /// \brief Constructor
    ///
    /// The \a cond and \a acc_cond arguments will be those returned
    /// by fair_kripke_succ_iterator::current_condition(),
    /// and fair_kripke_succ_iterator::current_acceptance_conditions().
    fair_kripke_succ_iterator(const bdd& cond, const bdd& acc_cond);
    virtual ~fair_kripke_succ_iterator();

    virtual bdd current_condition() const;
    virtual bdd current_acceptance_conditions() const;
  protected:
    bdd cond_;
    bdd acc_cond_;
  };

  /// \brief Interface for a Fair Kripke structure.
  /// \ingroup kripke
  ///
  /// A Kripke structure is a graph in which each node (=state) is
  /// labeled by a conjunction of atomic proposition, and a set of
  /// acceptance conditions.
  ///
  /// Such a structure can be seen as spot::tgba by pushing all labels
  /// to the outgoing transitions.
  ///
  /// A programmer that develops an instance of Fair Kripke structure
  /// needs just provide an implementation for the following methods:
  ///
  ///   - kripke::get_init_state()
  ///   - kripke::succ_iter()
  ///   - kripke::state_condition()
  ///   - kripke::state_acceptance_conditions()
  ///   - kripke::format_state()
  ///   - and optionally kripke::transition_annotation()
  ///
  /// The other methods of the tgba interface are supplied by this
  /// class and need not be defined.
  ///
  /// See also spot::fair_kripke_succ_iterator.
  class fair_kripke : public tgba
  {
  public:
    /// \brief The condition that label the state \a s.
    ///
    /// This should be a conjunction of atomic propositions.
    virtual bdd state_condition(const state* s) const = 0;

    /// \brief The set of acceptance conditions that label the state \a s.
    virtual bdd state_acceptance_conditions(const state* s) const = 0;

  protected:
    virtual bdd compute_support_conditions(const state* s) const;
    virtual bdd compute_support_variables(const state* s) const;
  };

}


#endif // SPOT_KRIPKE_FAIRKRIPKE_HH
