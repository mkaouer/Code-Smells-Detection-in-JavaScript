// Copyright (C) 2009, 2010, 2011 Laboratoire de Recherche et DÃ©veloppement
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

#ifndef SPOT_TGBA_TGBASAFRACOMPLEMENT_HH
# define SPOT_TGBA_TGBASAFRACOMPLEMENT_HH

# include <vector>
# include "tgba/tgba.hh"

#ifndef TRANSFORM_TO_TBA
# define TRANSFORM_TO_TBA 0
#endif
#define TRANSFORM_TO_TGBA (!TRANSFORM_TO_TBA)

namespace spot
{
  /// \brief Build a complemented automaton.
  /// \ingroup tgba_on_the_fly_algorithms
  ///
  /// It creates an automaton that recognizes the
  /// negated language of \a aut.
  ///
  /// 1. First Safra construction algorithm produces a
  ///    deterministic Rabin automaton.
  /// 2. Interpreting this deterministic Rabin automaton as a
  ///    deterministic Streett will produce a complemented automaton.
  /// 3. Then we use a transformation from deterministic Streett
  ///    automaton to nondeterministic Büchi automaton.
  ///
  ///  Safra construction is done in \a tgba_complement, the transformation
  ///  is done on-the-fly when successors are called.
  ///
  /// \sa safra_determinisation, tgba_safra_complement::succ_iter.
  class tgba_safra_complement : public tgba
  {
  public:
    tgba_safra_complement(const tgba* a);
    virtual ~tgba_safra_complement();

    // tgba interface.
    virtual state* get_init_state() const;
    virtual tgba_succ_iterator*
    succ_iter(const state* local_state,
	      const state* global_state = 0,
	      const tgba* global_automaton = 0) const;

    virtual bdd_dict* get_dict() const;
    virtual std::string format_state(const state* state) const;
    virtual bdd all_acceptance_conditions() const;
    virtual bdd neg_acceptance_conditions() const;

    void* get_safra() const
    {
      return safra_;
    }

  protected:
    virtual bdd compute_support_conditions(const state* state) const;
    virtual bdd compute_support_variables(const state* state) const;
  private:
    const tgba* automaton_;
    void* safra_;
#if TRANSFORM_TO_TBA
    bdd the_acceptance_cond_;
#endif
#if TRANSFORM_TO_TGBA
    bdd all_acceptance_cond_;
    bdd neg_acceptance_cond_;
    // Map to i the i-th acceptance condition of the final automaton.
    std::vector<bdd> acceptance_cond_vec_;
#endif
  };

  /// \brief Produce a dot output of the Safra automaton associated
  /// to \a a.
  ///
  /// @param a The \c tgba_safra_complement with an intermediate Safra
  /// automaton to display
  ///
  void display_safra(const tgba_safra_complement* a);
}

#endif  // SPOT_TGBA_TGBASAFRACOMPLEMENT_HH
