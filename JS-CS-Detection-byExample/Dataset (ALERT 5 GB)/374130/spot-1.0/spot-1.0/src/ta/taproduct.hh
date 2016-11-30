// Copyright (C) 2010 Laboratoire de Recherche et Developpement
// de l Epita (LRDE).
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

#ifndef SPOT_TA_TAPRODUCT_HH
# define SPOT_TA_TAPRODUCT_HH

#include "ta.hh"
#include "kripke/kripke.hh"

namespace spot
{

  /// \brief A state for spot::ta_product.
  /// \ingroup ta_emptiness_check
  ///
  /// This state is in fact a pair of state: the state from the TA
  /// automaton and that of Kripke structure.
  class state_ta_product : public state
  {
  public:
    /// \brief Constructor
    /// \param ta_state The state from the ta automaton.
    /// \param kripke_state_ The state from Kripke structure.
    state_ta_product(state* ta_state, state* kripke_state) :
      ta_state_(ta_state), kripke_state_(kripke_state)
    {
    }

    /// Copy constructor
    state_ta_product(const state_ta_product& o);

    virtual
    ~state_ta_product();

    state*
    get_ta_state() const
    {
      return ta_state_;
    }

    state*
    get_kripke_state() const
    {
      return kripke_state_;
    }

    virtual int
    compare(const state* other) const;
    virtual size_t
    hash() const;
    virtual state_ta_product*
    clone() const;

  private:
    state* ta_state_; ///< State from the ta automaton.
    state* kripke_state_; ///< State from the kripke structure.
  };

  /// \brief Iterate over the successors of a product computed on the fly.
  class ta_succ_iterator_product : public ta_succ_iterator
  {
  public:
    ta_succ_iterator_product(const state_ta_product* s, const ta* t,
        const kripke* k);

    virtual
    ~ta_succ_iterator_product();

    // iteration
    void
    first();
    void
    next();
    bool
    done() const;

    // inspection
    state_ta_product*
    current_state() const;
    bdd
    current_condition() const;

    bdd
    current_acceptance_conditions() const;

    /// \brief Return true if the changeset of the current transition is empty
    bool
    is_stuttering_transition() const;

  protected:
    //@{
    /// Internal routines to advance to the next successor.
    void
    step_();
    void
    next_non_stuttering_();

    /// \brief Move to the next successor in the kripke structure
    void
    next_kripke_dest();

    //@}

  protected:
    const state_ta_product* source_;
    const ta* ta_;
    const kripke* kripke_;
    ta_succ_iterator* ta_succ_it_;
    tgba_succ_iterator* kripke_succ_it_;
    state_ta_product* current_state_;
    bdd current_condition_;
    bdd current_acceptance_conditions_;
    bool is_stuttering_transition_;
    bdd kripke_source_condition;
    state * kripke_current_dest_state;

  };

  /// \brief A lazy product between a Testing automaton and a Kripke structure.
  /// (States are computed on the fly.)
  /// \ingroup ta_emptiness_check
  class ta_product : public ta
  {
  public:
    /// \brief Constructor.
    /// \param testing_automaton The TA component in the product.
    /// \param kripke_structure The Kripke component in the product.
    ta_product(const ta* testing_automaton, const kripke* kripke_structure);

    virtual
    ~ta_product();

    virtual const std::set<state*, state_ptr_less_than>
    get_initial_states_set() const;

    virtual ta_succ_iterator_product*
    succ_iter(const spot::state* s) const;

    virtual ta_succ_iterator_product*
    succ_iter(const spot::state* s, bdd changeset) const;

    virtual bdd_dict*
    get_dict() const;

    virtual std::string
    format_state(const spot::state* s) const;

    virtual bool
    is_accepting_state(const spot::state* s) const;

    virtual bool
    is_livelock_accepting_state(const spot::state* s) const;

    virtual bool
    is_initial_state(const spot::state* s) const;

    /// \brief Return true if the state \a s has no succeseurs
    /// in the TA automaton (the TA component of the product automaton)
    virtual bool
    is_hole_state_in_ta_component(const spot::state* s) const;

    virtual bdd
    get_state_condition(const spot::state* s) const;

    virtual bdd
    all_acceptance_conditions() const;

    virtual void
    free_state(const spot::state* s) const;

    const ta*
    get_ta() const
    {
      return ta_;
    }

    const kripke*
    get_kripke() const
    {
      return kripke_;
    }

  private:
    bdd_dict* dict_;
    const ta* ta_;
    const kripke* kripke_;

    // Disallow copy.
    ta_product(const ta_product&);
    ta_product&
    operator=(const ta_product&);
  };


   class ta_succ_iterator_product_by_changeset : public ta_succ_iterator_product
  {
  public:
    ta_succ_iterator_product_by_changeset(const state_ta_product* s,
        const ta* t, const kripke* k, bdd changeset);



    /// \brief Move to the next successor in the kripke structure
    void
    next_kripke_dest();


   };


}

#endif // SPOT_TA_TAPRODUCT_HH
