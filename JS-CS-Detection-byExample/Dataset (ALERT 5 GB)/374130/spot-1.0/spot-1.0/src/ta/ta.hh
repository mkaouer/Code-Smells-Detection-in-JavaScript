// Copyright (C) 2010, 2012 Laboratoire de Recherche et Developpement
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

#ifndef SPOT_TA_TA_HH
# define SPOT_TA_TA_HH

#include <set>

#include <cassert>
#include "misc/bddlt.hh"
#include "tgba/state.hh"
#include "tgba/succiter.hh"
#include "tgba/bdddict.hh"

namespace spot
{

  // Forward declarations.  See below.
  class ta_succ_iterator;

  /// \defgroup ta TA (Testing Automata)
  ///
  /// This type and its cousins are listed \ref ta_essentials "here".
  /// This is an abstract interface.  Its implementations are \ref
  /// ta_representation "concrete representations".  The
  /// algorithms that work on spot::ta are \ref tgba_algorithms
  /// "listed separately".

  /// \addtogroup ta_essentials Essential TA types
  /// \ingroup ta

  /// \brief A Testing Automaton.
  /// \ingroup ta_essentials
  ///
  /// The Testing Automata (TA) were introduced by
  /// Henri Hansen, Wojciech Penczek and Antti Valmari
  /// in "Stuttering-insensitive automata for on-the-fly detection of livelock
  /// properties" In Proc. of FMICSÕ02, vol. 66(2) of Electronic Notes in
  /// Theoretical Computer Science.Elsevier.
  ///
  /// While a TGBA automaton observes the value of the atomic propositions, the
  /// basic idea of TA is to detect the changes in these values; if a valuation
  ///  does not change between two consecutive valuations of an execution,
  /// the TA stay in the same state. A TA transition \c (s,k,d) is labeled by a
  /// "changeset" \c k: i.e. the set of atomic propositions that change between
  /// states \c s and \c d, if the changeset is empty then the transition is
  /// called stuttering transition.
  /// To detect execution that ends by stuttering in the same TA state, a
  /// new kind of acceptance states is introduced: "livelock-acceptance states"
  /// (in addition to the standard Buchi-acceptance states).
  ///
  /// Browsing such automaton can be achieved using two functions:
  /// \c get_initial_states_set or \c get_artificial_initial_state, and \c
  /// succ_iter. The former returns the initial state(s) while the latter lists
  /// the successor states of any state (filtred by "changeset").
  ///
  /// Note that although this is a transition-based automata,
  /// we never represent transitions!  Transition informations are
  /// obtained by querying the iterator over the successors of
  /// a state.

  class ta
  {

  public:
    virtual
    ~ta()
    {
    }

    typedef std::set<state*, state_ptr_less_than> states_set_t;

    /// \brief Get the initial states set of the automaton.
    virtual const states_set_t
    get_initial_states_set() const = 0;

    /// \brief Get the artificial initial state set of the automaton.
    /// Return 0 if this artificial state is not implemented
    /// (in this case, use \c get_initial_states_set)
    /// The aim of adding this state is to have an unique initial state. This
    /// artificial initial state have one transition to each real initial state,
    /// and this transition is labeled by the corresponding initial condition.
    /// (For more details, see the paper cited above)
    virtual spot::state*
    get_artificial_initial_state() const
    {
      return 0;
    }

    /// \brief Get an iterator over the successors of \a state.
    ///
    /// The iterator has been allocated with \c new.  It is the
    /// responsability of the caller to \c delete it when no
    /// longer needed.
    ///
    virtual ta_succ_iterator*
    succ_iter(const spot::state* state) const = 0;

    /// \brief Get an iterator over the successors of \a state
    /// filtred by the changeset on transitions
    ///
    /// The iterator has been allocated with \c new.  It is the
    /// responsability of the caller to \c delete it when no
    /// longer needed.
    ///
    virtual ta_succ_iterator*
    succ_iter(const spot::state* state, bdd changeset) const = 0;

    /// \brief Get the dictionary associated to the automaton.
    ///
    /// State are represented as BDDs.  The dictionary allows
    /// to map BDD variables back to formulae, and vice versa.
    /// This is useful when dealing with several automata (which
    /// may use the same BDD variable for different formula),
    /// or simply when printing.
    virtual bdd_dict*
    get_dict() const = 0;

    /// \brief Format the state as a string for printing.
    ///
    /// This formating is the responsability of the automata
    /// that owns the state.
    virtual std::string
    format_state(const spot::state* s) const = 0;

    /// \brief Return true if \a s is a Buchi-accepting state, otherwise false
    virtual bool
    is_accepting_state(const spot::state* s) const = 0;

    /// \brief Return true if \a s is a livelock-accepting state
    /// , otherwise false
    virtual bool
    is_livelock_accepting_state(const spot::state* s) const = 0;

    /// \brief Return true if \a s is an initial state, otherwise false
    virtual bool
    is_initial_state(const spot::state* s) const = 0;

    /// \brief Return a BDD condition that represents the valuation
    /// of atomic propositions in the state \a s
    virtual bdd
    get_state_condition(const spot::state* s) const = 0;

    /// \brief Release a state \a s
    virtual void
    free_state(const spot::state* s) const = 0;

    /// \brief Return the set of all acceptance conditions used
    /// by this automaton
    /// (for Generalized form: Transition-based Generalized Testing Automata).
    ///
    /// The goal of the emptiness check is to ensure that
    /// a strongly connected component walks through each
    /// of these acceptiong conditions.  I.e., the union
    /// of the acceptiong conditions of all transition in
    /// the SCC should be equal to the result of this function.
    virtual bdd
    all_acceptance_conditions() const = 0;

  };

  /// \brief Iterate over the successors of a state.
  /// \ingroup ta_essentials
  ///
  /// This class provides the basic functionalities required to
  /// iterate over the successors of a state, as well as querying
  /// transition labels.  Because transitions are never explicitely
  /// encoded, labels (conditions and acceptance conditions) can only
  /// be queried while iterating over the successors.
  class ta_succ_iterator : public tgba_succ_iterator
  {
  public:
    virtual
    ~ta_succ_iterator()
    {
    }

    virtual void
    first() = 0;
    virtual void
    next() = 0;
    virtual bool
    done() const = 0;

    virtual state*
    current_state() const = 0;

    /// \brief Get the changeset on the transition leading to current successor.
    ///
    /// This is a boolean function of atomic propositions.
    virtual bdd
    current_condition() const = 0;

    bdd
    current_acceptance_conditions() const = 0;

  };

#ifndef SWIG
  // A stack of Strongly-Connected Components
  class scc_stack_ta
  {
  public:
    struct connected_component
    {
    public:
      connected_component(int index = -1);

      /// Index of the SCC.
      int index;

      bool is_accepting;

      /// The bdd condition is the union of all acceptance conditions of
      /// transitions which connect the states of the connected component.
      bdd condition;

      std::list<state*> rem;
    };

    /// Stack a new SCC with index \a index.
    void
    push(int index);

    /// Access the top SCC.
    connected_component&
    top();

    /// Access the top SCC.
    const connected_component&
    top() const;

    /// Pop the top SCC.
    void
    pop();

    /// How many SCC are in stack.
    size_t
    size() const;

    /// The \c rem member of the top SCC.
    std::list<state*>&
    rem();

    /// Is the stack empty?
    bool
    empty() const;

    typedef std::list<connected_component> stack_type;
    stack_type s;
  };
#endif // !SWIG

/// \addtogroup ta_representation TA representations
/// \ingroup ta

/// \addtogroup ta_algorithms TA algorithms
/// \ingroup ta

/// \addtogroup ta_io Input/Output of TA
/// \ingroup ta_algorithms

/// \addtogroup tgba_ta Transforming TGBA into TA
/// \ingroup ta_algorithms


/// \addtogroup ta_generic Algorithm patterns
/// \ingroup ta_algorithms

/// \addtogroup ta_reduction TA simplifications
/// \ingroup ta_algorithms

/// \addtogroup ta_misc Miscellaneous algorithms on TA
/// \ingroup ta_algorithms


}

#endif // SPOT_TA_TA_HH
