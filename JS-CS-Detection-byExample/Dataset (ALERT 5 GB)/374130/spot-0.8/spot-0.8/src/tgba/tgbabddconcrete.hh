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

#ifndef SPOT_TGBA_TGBABDDCONCRETE_HH
# define SPOT_TGBA_TGBABDDCONCRETE_HH

#include "tgba.hh"
#include "statebdd.hh"
#include "tgbabddfactory.hh"
#include "succiterconcrete.hh"

namespace spot
{
  /// \brief A concrete spot::tgba implemented using BDDs.
  /// \ingroup tgba_representation
  class tgba_bdd_concrete: public tgba
  {
  public:
    /// \brief Construct a tgba_bdd_concrete with unknown initial state.
    ///
    /// set_init_state() should be called later.
    tgba_bdd_concrete(const tgba_bdd_factory& fact);

    /// \brief Construct a tgba_bdd_concrete with known initial state.
    tgba_bdd_concrete(const tgba_bdd_factory& fact, bdd init);

    virtual ~tgba_bdd_concrete();

    /// \brief Set the initial state.
    virtual void set_init_state(bdd s);

    virtual state_bdd* get_init_state() const;

    /// \brief Get the initial state directly as a BDD.
    ///
    /// The sole point of this method is to prevent writing
    /// horrors such as
    /// \code
    ///   state_bdd* s = automata.get_init_state();
    ///   some_class some_instance(s->as_bdd());
    ///   s->destroy();
    /// \endcode
    bdd get_init_bdd() const;

    virtual tgba_succ_iterator_concrete*
    succ_iter(const state* local_state,
	      const state* global_state = 0,
	      const tgba* global_automaton = 0) const;

    virtual std::string format_state(const state* state) const;

    virtual bdd_dict* get_dict() const;

    /// \brief Get the core data associated to this automaton.
    ///
    /// These data includes the various BDD used to represent
    /// the relation, encode variable sets, Next-to-Now rewrite
    /// rules, etc.
    const tgba_bdd_core_data& get_core_data() const;

    virtual bdd all_acceptance_conditions() const;
    virtual bdd neg_acceptance_conditions() const;

    /// \brief Delete SCCs (Strongly Connected Components) from the
    /// TGBA which cannot be accepting.
    void delete_unaccepting_scc();

  protected:
    virtual bdd compute_support_conditions(const state* state) const;
    virtual bdd compute_support_variables(const state* state) const;

    tgba_bdd_core_data data_;	///< Core data associated to the automaton.
    bdd init_;			///< Initial state.
  private:
    // Disallow copy.
    tgba_bdd_concrete(const tgba_bdd_concrete&);
    tgba_bdd_concrete& operator=(const tgba_bdd_concrete&);
  };
}

#endif // SPOT_TGBA_TGBABDDCONCRETE_HH
