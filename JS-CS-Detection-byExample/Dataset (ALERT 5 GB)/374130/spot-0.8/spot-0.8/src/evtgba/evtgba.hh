// Copyright (C) 2009 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_EVTGBA_EVTGBA_HH
# define SPOT_EVTGBA_EVTGBA_HH

#include "tgba/state.hh"
#include "evtgbaiter.hh"

namespace spot
{
  // FIXME: doc me
  class evtgba
  {
  protected:
    evtgba();

  public:
    virtual ~evtgba();

    virtual evtgba_iterator* init_iter() const = 0;
    virtual evtgba_iterator* succ_iter(const state* s) const = 0;
    virtual evtgba_iterator* pred_iter(const state* s) const = 0;

    /// \brief Format the state as a string for printing.
    ///
    /// This formating is the responsability of the automata
    /// that owns the state.
    virtual std::string format_state(const state* state) const = 0;

    virtual std::string format_label(const symbol* symbol) const;
    virtual std::string format_acceptance_condition(const symbol* symbol) const;
    virtual std::string
    format_acceptance_conditions(const symbol_set& symset) const;

    /// \brief Return the set of all acceptance conditions used
    /// by this automaton.
    ///
    /// The goal of the emptiness check is to ensure that
    /// a strongly connected component walks through each
    /// of these acceptiong conditions.  I.e., the union
    /// of the acceptiong conditions of all transition in
    /// the SCC should be equal to the result of this function.
    virtual const symbol_set& all_acceptance_conditions() const = 0;

    virtual const symbol_set& alphabet() const = 0;
  };

}

#endif // SPOT_EVTGBA_EVTGBA_HH
