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

#ifndef SPOT_TGBA_TGBABDDCONCRETEFACTORY_HH
# define SPOT_TGBA_TGBABDDCONCRETEFACTORY_HH

#include "misc/hash.hh"
#include "ltlast/formula.hh"
#include "tgbabddfactory.hh"

namespace spot
{
  /// Helper class to build a spot::tgba_bdd_concrete object.
  class tgba_bdd_concrete_factory: public tgba_bdd_factory
  {
  public:
    tgba_bdd_concrete_factory(bdd_dict* dict);

    virtual ~tgba_bdd_concrete_factory();

    /// Create a state variable for formula \a f.
    ///
    /// \param f The formula to create a state for.
    /// \return The variable number for this state.
    ///
    /// The state is not created if it already exists.  Instead its
    /// existing variable number is returned.  Variable numbers
    /// can be turned into BDD using ithvar().
    int create_state(const ltl::formula* f);

    /// Create an atomic proposition variable for formula \a f.
    ///
    /// \param f The formula to create an aotmic proposition for.
    /// \return The variable number for this state.
    ///
    /// The atomic proposition is not created if it already exists.
    /// Instead its existing variable number is returned.  Variable numbers
    /// can be turned into BDD using ithvar().
    int create_atomic_prop(const ltl::formula* f);

    /// Declare an acceptance condition.
    ///
    /// Formula such as 'f U g' or 'F g' make the promise
    /// that 'g' will be fulfilled eventually.  So once
    /// one of this formula has been translated into a BDD,
    /// we use declare_acceptance_condition() to associate
    /// all other states to the acceptance set of 'g'.
    ///
    /// \param b a BDD indicating which variables are in the
    ///          acceptance set
    /// \param a the formula associated
    void declare_acceptance_condition(bdd b, const ltl::formula* a);

    const tgba_bdd_core_data& get_core_data() const;
    bdd_dict* get_dict() const;

    /// Add a new constraint to the relation.
    void constrain_relation(bdd new_rel);

    /// \brief Perfom final computations before the relation can be used.
    ///
    /// This function should be called after all propositions, state,
    /// acceptance conditions, and constraints have been declared, and
    /// before calling get_code_data() or get_dict().
    void finish();

  private:
    tgba_bdd_core_data data_;	///< Core data for the new automata.

    typedef Sgi::hash_map<const ltl::formula*, bdd,
			  ptr_hash<ltl::formula> > acc_map_;
    acc_map_ acc_;		///< BDD associated to each acceptance condition
  };

}
#endif // SPOT_TGBA_TGBABDDCONCRETEFACTORY_HH
