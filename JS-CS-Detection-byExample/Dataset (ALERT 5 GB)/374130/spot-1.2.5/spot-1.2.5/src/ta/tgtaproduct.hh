// -*- coding: utf-8 -*-
// Copyright (C) 2011, 2012, 2013 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
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

#ifndef SPOT_TA_TGTAPRODUCT_HH
# define SPOT_TA_TGTAPRODUCT_HH

#include "tgba/tgba.hh"
#include "tgba/tgbaproduct.hh"
#include "misc/fixpool.hh"
#include "kripke/kripke.hh"
#include "tgta.hh"

namespace spot
{

  /// \brief A lazy product.  (States are computed on the fly.)
  class SPOT_API tgta_product : public tgba_product
  {
  public:
    tgta_product(const kripke* left, const tgta* right);

    virtual state*
    get_init_state() const;

    virtual tgba_succ_iterator*
    succ_iter(const state* local_state, const state* global_state = 0,
        const tgba* global_automaton = 0) const;
  };

  /// \brief Iterate over the successors of a product computed on the fly.
  class SPOT_API tgta_succ_iterator_product : public tgba_succ_iterator
  {
  public:
    tgta_succ_iterator_product(const state_product* s, const kripke* k,
			       const tgta* tgta, fixed_size_pool* pool);

    virtual
    ~tgta_succ_iterator_product();

    // iteration
    void
    first();
    void
    next();
    bool
    done() const;

    // inspection
    state_product*
    current_state() const;
    bdd
    current_condition() const;

    bdd
    current_acceptance_conditions() const;

  private:
    //@{
    /// Internal routines to advance to the next successor.
    void
    step_();
    void
    find_next_succ_();

    void
    next_kripke_dest();

    //@}

  protected:
    const state_product* source_;
    const tgta* tgta_;
    const kripke* kripke_;
    fixed_size_pool* pool_;
    tgba_succ_iterator* tgta_succ_it_;
    tgba_succ_iterator* kripke_succ_it_;
    state_product* current_state_;
    bdd current_condition_;
    bdd current_acceptance_conditions_;
    bdd kripke_source_condition;
    state* kripke_current_dest_state;
  };

}

#endif // SPOT_TA_TGTAPRODUCT_HH
