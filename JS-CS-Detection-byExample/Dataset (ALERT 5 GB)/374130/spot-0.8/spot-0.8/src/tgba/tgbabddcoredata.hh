// Copyright (C) 2009, 2011 Laboratoire de Recherche et Développement de
// l'Epita (LRDE).
// Copyright (C) 2003, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_TGBA_TGBABDDCOREDATA_HH
# define SPOT_TGBA_TGBABDDCOREDATA_HH

#include <bdd.h>
#include "bdddict.hh"

namespace spot
{
  /// Core data for a TGBA encoded using BDDs.
  struct tgba_bdd_core_data
  {
    /// \brief encodes the transition relation of the TGBA.
    ///
    /// \c relation uses three kinds of variables:
    /// \li "Now" variables, that encode the current state
    /// \li "Next" variables, that encode the destination state
    /// \li atomic propositions, which are things to verify before going on
    ///     to the next state
    bdd relation;

    /// \brief encodes the acceptance conditions
    ///
    /// <tt>a U b</tt>, or <tt>F b</tt>, both imply that \c b should
    /// be verified eventually.  We encode this with generalized Büchi
    /// acceptating conditions.  An acceptance set, called
    /// <tt>Acc[b]</tt>, hold all the state that do not promise to
    /// verify \c b eventually.  (I.e., all the states that contain \c
    /// b, or do not contain <tt>a U b</tt>, or <tt>F b</tt>.)
    ///
    /// The spot::succ_iter::current_acceptance_conditions() method
    /// will return the \c Acc[x] variables of the acceptance sets
    /// in which a transition is.  Actually we never return \c Acc[x]
    /// alone, but \c Acc[x] and all other acceptance variables negated.
    ///
    /// So if there is three acceptance set \c a, \c b, and \c c, and a
    /// transition is in set \c a, we'll return <tt>
    /// Acc[a]&!Acc[b]&!Acc[c]</tt>. If the transition is in both \c
    /// a and \c b, we'll return <tt>(Acc[a]\&!Acc[b]\&!Acc[c]) \c | \c
    /// (!Acc[a]\&Acc[b]\&!Acc[c])</tt>.
    ///
    /// Acceptance conditions are attributed to transitions and are
    /// only concerned by atomic propositions (which label the
    /// transitions) and Next variables (the destination).  Typically,
    /// a transition should bear the variable \c Acc[b] if it doesn't
    /// check for `b' and have a destination of the form <tt>a U b</tt>,
    /// or <tt>F b</tt>.
    ///
    /// To summarize, \c acceptance_conditions contains three kinds of
    /// variables:
    /// \li "Next" variables, that encode the destination state,
    /// \li atomic propositions, which are things to verify before going on
    ///     to the next state,
    /// \li "Acc" variables.
    bdd acceptance_conditions;

    /// The value of \c bdd_support(acceptance_conditions)
    bdd acceptance_conditions_support;

    /// \brief The set of all acceptance conditions used by the Automaton.
    ///
    /// The goal of the emptiness check is to ensure that
    /// a strongly connected component walks through each
    /// of these acceptiong conditions.  I.e., the union
    /// of the acceptiong conditions of all transition in
    /// the SCC should be equal to the result of this function.
    bdd all_acceptance_conditions;

    /// The conjunction of all Now variables, in their positive form.
    bdd now_set;
    /// The conjunction of all Next variables, in their positive form.
    bdd next_set;
    /// The conjunction of all Now and Next variables, in their positive form.
    bdd nownext_set;
    /// \brief The (positive) conjunction of all variables which are
    /// not Now variables.
    bdd notnow_set;
    /// \brief The (positive) conjunction of all variables which are
    /// not Next variables.
    bdd notnext_set;
    /// \brief The (positive) conjunction of all variables which are
    /// atomic propositions.
    bdd var_set;
    /// \brief The (positive) conjunction of all variables which are
    /// not atomic propositions.
    bdd notvar_set;
    /// \brief The (positive) conjunction of all Next variables
    /// and atomic propositions.
    bdd varandnext_set;
    /// \brief The (positive) conjunction of all variables which are
    /// acceptance conditions.
    bdd acc_set;
    /// \brief The (positive) conjunction of all variables which are not
    /// acceptance conditions.
    bdd notacc_set;
    /// \brief The negative conjunction of all variables which are acceptance
    /// conditions.
    bdd negacc_set;

    /// The dictionary used by the automata.
    bdd_dict* dict;

    /// \brief Default constructor.
    ///
    /// Initially all variable set are empty and the \c relation is true.
    tgba_bdd_core_data(bdd_dict* dict);

    /// Copy constructor.
    tgba_bdd_core_data(const tgba_bdd_core_data& copy);

    /// \brief Merge two tgba_bdd_core_data.
    ///
    /// This is used when building a product of two automata.
    tgba_bdd_core_data(const tgba_bdd_core_data& left,
		       const tgba_bdd_core_data& right);

    const tgba_bdd_core_data& operator= (const tgba_bdd_core_data& copy);

    /// \brief Update the variable sets to take a new pair of variables into
    /// account.
    void declare_now_next(bdd now, bdd next);
    /// \brief Update the variable sets to take a new automic proposition into
    /// account.
    void declare_atomic_prop(bdd var);
    /// \brief Update the variable sets to take a new acceptance condition
    /// into account.
    void declare_acceptance_condition(bdd prom);

    /// \brief Delete SCCs (Strongly Connected Components) from the
    /// relation which cannot be accepting.
    void delete_unaccepting_scc(bdd init);

  private:
    bdd infinitely_often(bdd s, bdd acc, bdd er);
  };
}

#endif // SPOT_TGBA_TGBABDDCOREDATA_HH
