// Copyright (C) 2011 Laboratoire de Recherche et Developpement de
// l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2006  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include <list>
#include <set>
#include <map>
#include <iosfwd>
#include <bdd.h>
#include "ltlast/formula.hh"
#include "misc/bddalloc.hh"

namespace spot
{

  /// \ingroup tgba_essentials
  /// \brief Map BDD variables to formulae.
  ///
  /// The BDD library uses integers to designate Boolean variables in
  /// its decision diagrams.  This class is used to map such integers
  /// to objects actually used in Spot.  These objects are usually
  /// atomic propositions, but they can also be acceptance conditions,
  /// or "Now/Next" variables (although the latter should be
  /// eventually removed).
  class bdd_dict: public bdd_allocator
  {
  public:

    bdd_dict();
    ~bdd_dict();

    /// Formula-to-BDD-variable maps.
    typedef std::map<const ltl::formula*, int> fv_map;
    /// BDD-variable-to-formula maps.
    typedef std::map<int, const ltl::formula*> vf_map;

    fv_map now_map;		///< Maps formulae to "Now" BDD variables
    vf_map now_formula_map;	///< Maps "Now" BDD variables to formulae
    fv_map var_map;		///< Maps atomic propositions to BDD variables
    vf_map var_formula_map;     ///< Maps BDD variables to atomic propositions
    fv_map acc_map;		///< Maps acceptance conditions to BDD variables
    vf_map acc_formula_map;	///< Maps BDD variables to acceptance conditions

    /// Clone counts.
    typedef std::map<int, int> cc_map;
    cc_map clone_counts;

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

    /// \brief Clone an acceptance variable VAR for FOR_ME.
    ///
    /// This is used in products TGBAs when both operands share the
    /// same acceptance variables but they need to be distinguished in
    /// the result.
    int register_clone_acc(int var, const void* for_me);

    /// \brief Register BDD variables as acceptance variables.
    ///
    /// Register all variables occurring in \a f as acceptance variables
    /// used by \a for_me.  This assumes that these acceptance variables
    /// are already known from the dictionary (i.e., they have already
    /// been registered by register_acceptance_variable() for another
    /// automaton).
    void register_acceptance_variables(bdd f, const void* for_me);

    /// \brief Register anonymous BDD variables.
    ///
    /// Return (and maybe allocate) \a n consecutive BDD variables which
    /// will be used only by \a for_me.
    ///
    /// \return The variable number.  Use bdd_ithvar() or bdd_nithvar()
    ///   to convert this to a BDD.
    int register_anonymous_variables(int n, const void* for_me);

    /// \brief Duplicate the variable usage of another object.
    ///
    /// This tells this dictionary that the \a for_me object
    /// will be using the same BDD variables as the \a from_other objects.
    /// This ensure that the variables won't be freed when \a from_other
    /// is deleted if \a from_other is still alive.
    void register_all_variables_of(const void* from_other, const void* for_me);

    /// \brief Release all variables used by an object.
    ///
    /// Usually called in the destructor if \a me.
    void unregister_all_my_variables(const void* me);

    /// \brief Release a variable used by \a me.
    void unregister_variable(int var, const void* me);

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
    typedef std::set<const void*> ref_set;
    typedef std::map<int, ref_set> vr_map;
    vr_map var_refs;

    void unregister_variable(vr_map::iterator& cur, const void* me);

    // SWIG does not grok the following definition, no idea why.
    // It's not important for the Python interface anyway.
#ifndef SWIG
    class anon_free_list : public spot::free_list
    {
    public:
      // WARNING: We need a default constructor so this can be used in
      // a hash; but we should ensure that no object in the hash is
      // constructed with d==0.
      anon_free_list(bdd_dict* d = 0);
      virtual int extend(int n);
    private:
      bdd_dict* dict_;
    };
#endif

    /// List of unused anonymous variable number for each automaton.
    typedef std::map<const void*, anon_free_list> free_anonymous_list_of_type;
    free_anonymous_list_of_type free_anonymous_list_of;

  private:
    // Disallow copy.
    bdd_dict(const bdd_dict& other);
    bdd_dict& operator=(const bdd_dict& other);
  };


}

#endif // SPOT_TGBA_BDDDICT_HH
