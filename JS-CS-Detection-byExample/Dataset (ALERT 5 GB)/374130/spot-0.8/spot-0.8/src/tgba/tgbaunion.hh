// Copyright (C) 2009, 2011 Laboratoire de Recherche et Développement
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

#ifndef SPOT_TGBA_TGBAUNION_HH
# define SPOT_TGBA_TGBAUNION_HH

#include "tgba.hh"

namespace spot
{
  /// \brief A state for spot::tgba_union.
  /// \ingroup tgba_on_the_fly_algorithms
  ///
  /// This state is in fact a pair.
  /// If the first member equals 0 and the second is different from 0,
  /// the state belongs to the left automaton.
  /// If the first member is different from 0 and the second is 0,
  /// the state belongs to the right automaton.
  /// If both members are 0, the state is the initial state.
  class state_union : public state
  {
  public:
    /// \brief Constructor
    /// \param left The state from the left automaton.
    /// \param right The state from the right automaton.
    /// These states are acquired by spot::state_union, and will
    /// be destroyed on destruction.
    state_union(state* left, state* right)
      :	left_(left),
	right_(right)
    {
    }

    /// Copy constructor
    state_union(const state_union& o);

    virtual ~state_union();

    state*
    left() const
    {
      return left_;
    }

    state*
    right() const
    {
      return right_;
    }

    virtual int compare(const state* other) const;
    virtual size_t hash() const;
    virtual state_union* clone() const;

  private:
    state* left_;		///< Does the state belongs
                                /// to the left automaton ?
    state* right_;		///< Does the state belongs
                                /// to the right automaton ?
  };

   /// \brief Iterate over the successors of an union computed on the fly.
  class tgba_succ_iterator_union: public tgba_succ_iterator
  {
  public:
    tgba_succ_iterator_union(tgba_succ_iterator* left,
			     tgba_succ_iterator* right,
			     bdd left_missing,
			     bdd right_missing, bdd left_var, bdd right_var);

    virtual ~tgba_succ_iterator_union();

    // iteration
    void first();
    void next();
    bool done() const;

    // inspection
    state_union* current_state() const;
    bdd current_condition() const;
    bdd current_acceptance_conditions() const;

  protected:
    tgba_succ_iterator* left_;
    tgba_succ_iterator* right_;
    bdd current_cond_;
    bdd left_missing_;
    bdd right_missing_;
    bdd left_neg_;
    bdd right_neg_;
    friend class tgba_union;
  };

  /// \brief A lazy union.  (States are computed on the fly.)
  class tgba_union: public tgba
  {
  public:
    /// \brief Constructor.
    /// \param left The left automata in the union.
    /// \param right The right automata in the union.
    tgba_union(const tgba* left, const tgba* right);

    virtual ~tgba_union();

    virtual state* get_init_state() const;

    virtual tgba_succ_iterator_union*
    succ_iter(const state* local_state,
	      const state* global_state = 0,
	      const tgba* global_automaton = 0) const;

    virtual bdd_dict* get_dict() const;

    virtual std::string format_state(const state* state) const;

    virtual state* project_state(const state* s, const tgba* t) const;

    virtual bdd all_acceptance_conditions() const;
    virtual bdd neg_acceptance_conditions() const;

  protected:
    virtual bdd compute_support_conditions(const state* state) const;
    virtual bdd compute_support_variables(const state* state) const;

  private:
    bdd_dict* dict_;
    const tgba* left_;
    const tgba* right_;
    bdd left_acc_missing_;
    bdd right_acc_missing_;
    bdd left_acc_complement_;
    bdd right_acc_complement_;
    bdd left_var_missing_;
    bdd right_var_missing_;
    bdd all_acceptance_conditions_;
    bdd neg_acceptance_conditions_;
    bddPair* right_common_acc_;
    // Disallow copy.
    tgba_union(const tgba_union&);
    tgba_union& operator=(const tgba_union&);
  };


}

#endif // SPOT_TGBA_TGBAUNION_HH
