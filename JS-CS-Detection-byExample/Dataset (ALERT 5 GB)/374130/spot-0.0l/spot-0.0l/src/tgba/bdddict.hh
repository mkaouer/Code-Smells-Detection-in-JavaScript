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

#ifndef SPOT_TGBA_BDDDICT_HH
# define SPOT_TGBA_BDDDICT_HH

#include "misc/hash.hh"
#include <list>
#include <set>
#include <iostream>
#include <bdd.h>
#include "ltlast/formula.hh"
#include "misc/bddalloc.hh"

namespace spot
{

  /// Map BDD variables to formulae.
  class bdd_dict: public bdd_allocator
  {
  public:

    bdd_dict();
    ~bdd_dict();

    /// Formula-to-BDD-variable maps.
    typedef Sgi::hash_map<const ltl::formula*, int,
			  ptr_hash<ltl::formula> > fv_map;
    /// BDD-variable-to-formula maps.
    typedef Sgi::hash_map<int, const ltl::formula*> vf_map;

    fv_map now_map;		///< Maps formulae to "Now" BDD variables
    vf_map now_formula_map;	///< Maps "Now" BDD variables to formulae
    fv_map var_map;		///< Maps atomic propositions to BDD variables
    vf_map var_formula_map;     ///< Maps BDD variables to atomic propositions
    fv_map acc_map;		///< Maps acceptance conditions to BDD variables
    vf_map acc_formula_map;	///< Maps BDD variables to acceptance conditions

    /// \brief Map Next variables to Now variables.
    ///
    /// Use with BuDDy's bdd_replace() function.
    bddPair* next_to_now;
    /// \brief Map Now variables to Next variables.
    ///
    /// Use with BuDDy's bdd_replace() function.
    bddPair* now_to_next;

    /// \brief Register an atomic proposition.
    ///
    /// Return (and maybe allocate) a BDD variable designating formula
    /// \a f.  The \a for_me argument should point to the object using
    /// this BDD variable, this is used for reference counting.  It is
    /// perfectly safe to call this function several time with the same
    /// arguments.
    ///
    /// \return The variable number.  Use bdd_ithvar() or bdd_nithvar()
    ///   to convert this to a BDD.
    int register_proposition(const ltl::formula* f, const void* for_me);

    /// \brief Register BDD variables as atomic propositions.
    ///
    /// Register all variables occurring in \a f as atomic propositions
    /// used by \a for_me.  This assumes that these atomic propositions
    /// are already known from the dictionary (i.e., they have already
    /// been registered by register_proposition() for another
    /// automaton).
    void register_propositions(bdd f, const void* for_me);

    /// \brief Register a couple of Now/Next variables
    ///
    /// Return (and maybe allocate) two BDD variables for a state
    /// associated to formula \a f.  The \a for_me argument should point
    /// to the object using this BDD variable, this is used for
    /// reference counting.  It is perfectly safe to call this
    /// function several time with the same arguments.
    ///
    /// \return The first variable number.  Add one to get the second
    /// variable.  Use bdd_ithvar() or bdd_nithvar() to convert this
    /// to a BDD.
    int register_state(const ltl::formula* f, const void* for_me);

    /// \brief Register an atomic proposition.
    ///
    /// Return (and maybe allocate) a BDD variable designating an
    /// acceptance set associated to formula \a f.  The \a for_me
    /// argument should point to the object using this BDD variable,
    /// this is used for reference counting.  It is perfectly safe to
    /// call this function several time with the same arguments.
    ///
    /// \return The variable number.  Use bdd_ithvar() or bdd_nithvar()
    ///   to convert this to a BDD.
    int register_acceptance_variable(const ltl::formula* f, const void* for_me);

    /// \brief Register BDD variables as acceptance variables.
    ///
    /// Register all variables occurring in \a f as acceptance variables
    /// used by \a for_me.  This assumes that these acceptance variables
    /// are already known from the dictionary (i.e., they have already
    /// been registered by register_acceptance_variable() for another
    /// automaton).
    void register_acceptance_variables(bdd f, const void* for_me);

    /// \brief Duplicate the variable usage of another object.
    ///
    /// This tells this dictionary that the \a for_me object
    /// will be using the same BDD variables as the \a from_other objects.
    /// This ensure that the variables won't be freed when \a from_other
    /// is deleted if \a from_other is still alive.
    void register_all_variables_of(const void* from_other, const void* for_me);

    /// \brief Release the variables used by object.
    ///
    /// Usually called in the destructor if \a me.
    void unregister_all_my_variables(const void* me);

    /// @{
    /// Check whether formula \a f has already been registered by \a by_me.
    bool is_registered_proposition(const ltl::formula* f, const void* by_me);
    bool is_registered_state(const ltl::formula* f, const void* by_me);
    bool is_registered_acceptance_variable(const ltl::formula* f,
					  const void* by_me);
    /// @}

    /// \brief Dump all variables for debugging.
    /// \param os The output stream.
    std::ostream& dump(std::ostream& os) const;

    /// \brief Make sure the dictionary is empty.
    ///
    /// This will print diagnostics and abort if the dictionary
    /// is not empty.  Use for debugging.
    void assert_emptiness() const;

  protected:
    /// BDD-variable reference counts.
    typedef Sgi::hash_set<const void*, ptr_hash<void> > ref_set;
    typedef Sgi::hash_map<int, ref_set> vr_map;
    vr_map var_refs;

  private:
    // Disallow copy.
    bdd_dict(const bdd_dict& other);
    bdd_dict& operator=(const bdd_dict& other);
  };


}

#endif // SPOT_TGBA_BDDDICT_HH
