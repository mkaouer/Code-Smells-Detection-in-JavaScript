// -*- coding: utf-8 -*-
// Copyright (C) 2008, 2009, 2010, 2011, 2012 Laboratoire de Recherche
// et Développement de l'Epita.
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

#ifndef SPOT_TGBAALGOS_SCC_HH
# define SPOT_TGBAALGOS_SCC_HH

#include <map>
#include <stack>
#include <vector>
#include "tgba/tgba.hh"
#include <iosfwd>
#include "misc/hash.hh"
#include "misc/bddlt.hh"

namespace spot
{

  struct scc_stats
  {
    /// Total number of SCCs.
    unsigned scc_total;
    /// Total number of accepting SCC.
    unsigned acc_scc;
    /// Total number of dead SCC.
    ///
    /// An SCC is dead if no accepting SCC is reachable from it.
    /// Note that an SCC can be neither dead nor accepting.
    unsigned dead_scc;

    /// Number of maximal accepting paths.
    ///
    /// A path is maximal and accepting if it ends in an accepting
    /// SCC that has only dead (i.e. non accepting) successors, or no
    /// successors at all.
    unsigned acc_paths;
    /// Number of paths to a terminal dead SCC.
    ///
    /// A terminal dead SCC is a dead SCC without successors.
    unsigned dead_paths;
    unsigned self_loops;

    /// A map of the useless SCCs.
    std::vector<bool> useless_scc_map;

    /// The set of useful acceptance conditions (i.e. acceptance
    /// conditions that are not always implied by other acceptance
    /// conditions).
    bdd useful_acc;

    std::ostream& dump(std::ostream& out) const;
  };

  /// Build a map of Strongly Connected components in in a TGBA.
  class scc_map
  {
  public:
    typedef std::map<unsigned, bdd> succ_type;
    typedef std::set<bdd, bdd_less_than> cond_set;

    /// \brief Constructor.
    ///
    /// This will note compute the map initially.  You should call
    /// build_map() to do so.
    scc_map(const tgba* aut);

    ~scc_map();

    /// Actually compute the graph of strongly connected components.
    void build_map();

    /// Get the automaton for which the map has been constructed.
    const tgba* get_aut() const;

    /// \brief Get the number of SCC in the automaton.
    ///
    /// SCCs are labelled from 0 to scc_count()-1.
    ///
    /// \pre This should only be called once build_map() has run.
    unsigned scc_count() const;

    /// \brief Get number of the SCC containing the initial state.
    ///
    /// \pre This should only be called once build_map() has run.
    unsigned initial() const;

    /// \brief Successor SCCs of a SCC.
    ///
    /// \pre This should only be called once build_map() has run.
    const succ_type& succ(unsigned n) const;

    /// \brief Return whether an SCC is trivial.
    ///
    /// Trivial SCCs have one state and no self-loop.
    ///
    /// \pre This should only be called once build_map() has run.
    bool trivial(unsigned n) const;

    /// \brief Return whether an SCC is accepting.
    ///
    /// \pre This should only be called once build_map() has run.
    bool accepting(unsigned n) const;

    /// \brief Return the set of conditions occurring in an SCC.
    ///
    /// \pre This should only be called once build_map() has run.
    const cond_set& cond_set_of(unsigned n) const;

    /// \brief Return the set of atomic properties occurring on the
    /// transitions leaving states from SCC \a n.
    ///
    /// The transitions considered are all transitions inside SCC
    /// \a n, as well as the transitions leaving SCC \a n.
    ///
    /// \return a BDD that is a conjuction of all atomic properties
    /// occurring on the transitions leaving the states of SCC \a n.
    ///
    /// \pre This should only be called once build_map() has run.
    bdd ap_set_of(unsigned n) const;

    /// \brief Return the set of atomic properties reachable from this SCC.
    ///
    /// \return a BDD that is a conjuction of all atomic properties
    /// occurring on the transitions reachable from this SCC n.
    ///
    /// \pre This should only be called once build_map() has run.
    bdd aprec_set_of(unsigned n) const;

    /// \brief Return the set of acceptance conditions occurring in an SCC.
    ///
    /// \pre This should only be called once build_map() has run.
    bdd acc_set_of(unsigned n) const;

    /// \brief Return the set of useful acceptance conditions of SCC \a n.
    ///
    /// Useless acceptances conditions are always implied by other acceptances
    /// conditions.  This returns all the other acceptance conditions.
    bdd useful_acc_of(unsigned n) const;

    /// \brief Return the set of states of an SCC.
    ///
    /// The states in the returned list are still owned by the scc_map
    /// instance.  They should NOT be destroyed by the client code.
    ///
    /// \pre This should only be called once build_map() has run.
    const std::list<const state*>& states_of(unsigned n) const;

    /// \brief Return one state of an SCC.
    ///
    /// The state in the returned list is still owned by the scc_map
    /// instance.  It should NOT be destroyed by the client code.
    ///
    /// \pre This should only be called once build_map() has run.
    const state* one_state_of(unsigned n) const;

    /// \brief Return the number of the SCC a state belongs too.
    ///
    /// \pre This should only be called once build_map() has run.
    unsigned scc_of_state(const state* s) const;

    /// \brief Return the number of self loops in the automaton.
    unsigned self_loops() const;

  protected:
    bdd update_supp_rec(unsigned state);
    int relabel_component();

    struct scc
    {
    public:
      scc(int index) : index(index), acc(bddfalse),
		       supp(bddtrue), supp_rec(bddfalse),
		       trivial(true), useful_acc(bddfalse) {};
      /// Index of the SCC.
      int index;
      /// The union of all acceptance conditions of transitions which
      /// connect the states of the connected component.
      bdd acc;
      /// States of the component.
      std::list<const state*> states;
      /// Set of conditions used in the SCC.
      cond_set conds;
      /// Conjunction of atomic propositions used in the SCC.
      bdd supp;
      /// Conjunction of atomic propositions used in the SCC.
      bdd supp_rec;
      /// Successor SCC.
      succ_type succ;
      /// Trivial SCC have one state and no self-loops.
      bool trivial;
      /// \brief Set of acceptance combinations used in the SCC.
      ///
      /// Note that the encoding used here differs from the
      /// encoding used in automata.
      /// If some transitions of the automaton are labeled by
      ///      Acc[a]&!Acc[b]&!Acc[c]  |  !Acc[a]&Acc[b]&!Acc[c]
      /// an other transitions are labeled by
      ///      !Acc[a]&Acc[b]&!Acc[c]  |  !Acc[a]&!Acc[b]&Acc[c]
      /// then useful_acc will contain
      ///      Acc[a]&Acc[b]&!Acc[c] | !Acc[a]&Acc[b]&Acc[c]
      bdd useful_acc;
    };

    const tgba* aut_;		// Automata to decompose.
    typedef std::list<scc> stack_type;
    stack_type root_;		// Stack of SCC roots.
    std::stack<bdd> arc_acc_;	// A stack of acceptance conditions
				// between each of these SCC.
    std::stack<bdd> arc_cond_;	// A stack of conditions
				// between each of these SCC.
    typedef Sgi::hash_map<const state*, int,
			  state_ptr_hash, state_ptr_equal> hash_type;
    hash_type h_;		// Map of visited states.  Values >= 0
                                // designate maximal SCC.  Values < 0
                                // number states that are part of
                                // incomplete SCCs being completed.
    int num_;			// Number of visited nodes, negated.
    typedef std::pair<const spot::state*, tgba_succ_iterator*> pair_state_iter;
    std::stack<pair_state_iter> todo_; // DFS stack.  Holds (STATE,
				       // ITERATOR) pairs where
				       // ITERATOR is an iterator over
				       // the successors of STATE.
				       // ITERATOR should always be
				       // freed when TODO is popped,
				       // but STATE should not because
				       // it is used as a key in H.

    typedef std::vector<scc> scc_map_type;
    scc_map_type scc_map_; // Map of constructed maximal SCC.
			   // SCC number "n" in H_ corresponds to entry
                           // "n" in SCC_MAP_.
    unsigned self_loops_; // Self loops count.
 };

  scc_stats build_scc_stats(const tgba* a);
  scc_stats build_scc_stats(const scc_map& m);

  std::ostream& dump_scc_dot(const tgba* a, std::ostream& out,
			     bool verbose = false);
  std::ostream& dump_scc_dot(const scc_map& m, std::ostream& out,
			     bool verbose = false);
}

#endif // SPOT_TGBAALGOS_SCC_HH
