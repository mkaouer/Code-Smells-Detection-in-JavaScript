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

#ifndef SPOT_TGBA_TGBASGBA_HH
# define SPOT_TGBA_TGBASGBA_HH

#include "tgba.hh"
#include "misc/bddlt.hh"

namespace spot
{

  /// \brief Change the labeling-mode of spot::tgba on the fly, producing a
  /// state-based generalized Büchi automaton.
  /// \ingroup tgba_on_the_fly_algorithms
  ///
  /// This class acts as a proxy in front of a spot::tgba, that should
  /// label on states on-the-fly.  The result is still a spot::tgba,
  /// but acceptances conditions are also on states.
  class tgba_sgba_proxy : public tgba
  {
  public:
    tgba_sgba_proxy(const tgba* a, bool no_zero_acc = true);

    virtual ~tgba_sgba_proxy();

    virtual state* get_init_state() const;

    virtual tgba_succ_iterator*
    succ_iter(const state* local_state,
	      const state* global_state = 0,
	      const tgba* global_automaton = 0) const;

    virtual bdd_dict* get_dict() const;

    virtual std::string format_state(const state* state) const;

    virtual bdd all_acceptance_conditions() const;
    virtual bdd neg_acceptance_conditions() const;

    /// \brief Retrieve the acceptance condition of a state.
    bdd state_acceptance_conditions(const state* state) const;
  protected:
    virtual bdd compute_support_conditions(const state* state) const;
    virtual bdd compute_support_variables(const state* state) const;

  private:
    const tgba* a_;
    // If the automaton has no acceptance condition,
    // every state is accepting.
    bool emulate_acc_cond_;
    bdd acceptance_condition_;
    // Disallow copy.
    tgba_sgba_proxy(const tgba_sgba_proxy&);
    tgba_sgba_proxy& operator=(const tgba_sgba_proxy&);
  };

}
#endif // SPOT_TGBA_TGBASGBA_HH
