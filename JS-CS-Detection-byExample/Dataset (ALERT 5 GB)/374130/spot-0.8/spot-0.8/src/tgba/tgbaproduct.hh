// Copyright (C) 2011 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2006 Laboratoire d'Informatique de Paris
// 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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
#include "misc/fixpool.hh"

namespace spot
{

  /// \brief A state for spot::tgba_product.
  /// \ingroup tgba_on_the_fly_algorithms
  ///
  /// This state is in fact a pair of state: the state from the left
  /// automaton and that of the right.
  class state_product : public state
  {
  public:
    /// \brief Constructor
    /// \param left The state from the left automaton.
    /// \param right The state from the right automaton.
    /// \param pool The pool from which the state was allocated.
    /// These states are acquired by spot::state_product, and will
    /// be destroyed on destruction.
    state_product(state* left, state* right, fixed_size_pool* pool)
      :	left_(left), right_(right), count_(1), pool_(pool)
    {
    }

    virtual void destroy() const;

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
    mutable unsigned count_;
    fixed_size_pool* pool_;

    virtual ~state_product();
    state_product(const state_product& o); // No implementation.
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

    virtual tgba_succ_iterator*
    succ_iter(const state* local_state,
	      const state* global_state = 0,
	      const tgba* global_automaton = 0) const;

    virtual bdd_dict* get_dict() const;

    virtual std::string format_state(const state* state) const;

    virtual std::string
    transition_annotation(const tgba_succ_iterator* t) const;

    virtual state* project_state(const state* s, const tgba* t) const;

    virtual bdd all_acceptance_conditions() const;
    virtual bdd neg_acceptance_conditions() const;

  protected:
    virtual bdd compute_support_conditions(const state* state) const;
    virtual bdd compute_support_variables(const state* state) const;

  protected:
    bdd_dict* dict_;
    const tgba* left_;
    const tgba* right_;
    bool left_kripke_;
    bdd left_acc_complement_;
    bdd right_acc_complement_;
    bdd all_acceptance_conditions_;
    bdd neg_acceptance_conditions_;
    bddPair* right_common_acc_;
    fixed_size_pool pool_;

  private:
    // Disallow copy.
    tgba_product(const tgba_product&);
    tgba_product& operator=(const tgba_product&);
  };

  /// \brief A lazy product with different initial states.
  class tgba_product_init: public tgba_product
  {
  public:
    tgba_product_init(const tgba* left, const tgba* right,
		      const state* left_init, const state* right_init);
    virtual state* get_init_state() const;
  protected:
    const state* left_init_;
    const state* right_init_;
  };

}

#endif // SPOT_TGBA_TGBAPRODUCT_HH
