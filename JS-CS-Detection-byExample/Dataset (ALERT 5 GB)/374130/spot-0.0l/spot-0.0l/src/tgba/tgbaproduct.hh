// Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_TGBA_TGBAPRODUCT_HH
# define SPOT_TGBA_TGBAPRODUCT_HH

#include "tgba.hh"
#include "statebdd.hh"

namespace spot
{

  /// \brief A state for spot::tgba_product.
  ///
  /// This state is in fact a pair of state: the state from the left
  /// automaton and that of the right.
  class state_product : public state
  {
  public:
    /// \brief Constructor
    /// \param left The state from the left automaton.
    /// \param right The state from the right automaton.
    /// These states are acquired by spot::state_product, and will
    /// be deleted on destruction.
    state_product(state* left, state* right)
      :	left_(left),
	right_(right)
    {
    }

    /// Copy constructor
    state_product(const state_product& o);

    virtual ~state_product();

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
    virtual state_product* clone() const;

  private:
    state* left_;		///< State from the left automaton.
    state* right_;		///< State from the right automaton.
  };


  /// \brief Iterate over the successors of a product computed on the fly.
  class tgba_succ_iterator_product: public tgba_succ_iterator
  {
  public:
    tgba_succ_iterator_product(tgba_succ_iterator* left,
			       tgba_succ_iterator* right,
			       bdd left_neg, bdd right_neg);

    virtual ~tgba_succ_iterator_product();

    // iteration
    void first();
    void next();
    bool done() const;

    // inspection
    state_product* current_state() const;
    bdd current_condition() const;
    bdd current_acceptance_conditions() const;

  private:
    //@{
    /// Internal routines to advance to the next successor.
    void step_();
    void next_non_false_();
    //@}

  protected:
    tgba_succ_iterator* left_;
    tgba_succ_iterator* right_;
    bdd current_cond_;
    bdd left_neg_;
    bdd right_neg_;
  };

  /// \brief A lazy product.  (States are computed on the fly.)
  class tgba_product: public tgba
  {
  public:
    /// \brief Constructor.
    /// \param left The left automata in the product.
    /// \param right The right automata in the product.
    /// Do not be fooled by these arguments: a product is commutative.
    tgba_product(const tgba* left, const tgba* right);

    virtual ~tgba_product();

    virtual state* get_init_state() const;

    virtual tgba_succ_iterator_product*
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
    bdd left_acc_complement_;
    bdd right_acc_complement_;
    bdd all_acceptance_conditions_;
    bdd neg_acceptance_conditions_;
    // Disallow copy.
    tgba_product(const tgba_product&);
    tgba_product& tgba_product::operator=(const tgba_product&);
  };

}

#endif // SPOT_TGBA_TGBAPRODUCT_HH
