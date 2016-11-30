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

#ifndef SPOT_KRIPKE_KRIPKE_HH
# define SPOT_KRIPKE_KRIPKE_HH

#include "fairkripke.hh"

namespace spot
{

  /// \brief Iterator code for Kripke structure
  /// \ingroup kripke
  ///
  /// This iterator can be used to simplify the writing
  /// of an iterator on a Kripke structure (or lookalike).
  ///
  /// If you inherit from this iterator, you should only
  /// redefine
  ///
  ///   - kripke_succ_iterator::first()
  ///   - kripke_succ_iterator::next()
  ///   - kripke_succ_iterator::done()
  ///   - kripke_succ_iterator::current_state()
  ///
  /// This class implements kripke_succ_iterator::current_condition(),
  /// and kripke_succ_iterator::current_acceptance_conditions().
  class kripke_succ_iterator : public tgba_succ_iterator
  {
  public:
    /// \brief Constructor
    ///
    /// The \a cond argument will be the one returned
    /// by kripke_succ_iterator::current_condition().
    kripke_succ_iterator(const bdd& cond);
    virtual ~kripke_succ_iterator();

    virtual bdd current_condition() const;
    virtual bdd current_acceptance_conditions() const;
  protected:
    bdd cond_;
  };

  /// \brief Interface for a Kripke structure
  /// \ingroup kripke
  ///
  /// A Kripke structure is a graph in which each node (=state) is
  /// labeled by a conjunction of atomic proposition.
  ///
  /// Such a structure can be seen as spot::tgba without
  /// any acceptance condition.
  ///
  /// A programmer that develops an instance of Kripke structure needs
  /// just provide an implementation for the following methods:
  ///
  ///   - kripke::get_init_state()
  ///   - kripke::succ_iter()
  ///   - kripke::state_condition()
  ///   - kripke::format_state()
  ///   - and optionally kripke::transition_annotation()
  ///
  /// The other methods of the tgba interface (like those dealing with
  /// acceptance conditions) are supplied by this kripke class and
  /// need not be defined.
  ///
  /// See also spot::kripke_succ_iterator.
  class kripke: public fair_kripke
  {
  public:
    virtual ~kripke();

    virtual bdd state_acceptance_conditions(const state*) const;
    virtual bdd neg_acceptance_conditions() const;
    virtual bdd all_acceptance_conditions() const;
  };
}

#endif // SPOT_KRIPKE_KRIPKE_HH
