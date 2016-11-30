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

#ifndef SPOT_TGBAALGOS_MAGIC_HH
# define SPOT_TGBAALGOS_MAGIC_HH

#include "misc/hash.hh"
#include <list>
#include <utility>
#include <ostream>
#include "tgba/tgbatba.hh"

namespace spot
{
  /// \brief Emptiness check on spot::tgba_tba_proxy automata using
  /// the Magic Search algorithm.
  ///
  /// This algorithm comes from
  /// \verbatim
  /// @InProceedings{   godefroid.93.pstv,
  ///   author        = {Patrice Godefroid and Gerard .J. Holzmann},
  ///   title         = {On the verification of temporal properties},
  ///   booktitle     = {Proceedings of the 13th IFIP TC6/WG6.1 International
  ///                   Symposium on Protocol Specification, Testing, and
  ///                   Verification (PSTV'93)},
  ///   month         = {May},
  ///   editor        = {Andr{\'e} A. S. Danthine and Guy Leduc
  ///                    and Pierre Wolper},
  ///   address       = {Liege, Belgium},
  ///   pages         = {109--124},
  ///   publisher     = {North-Holland},
  ///   year          = {1993},
  ///   series        = {IFIP Transactions},
  ///   volume        = {C-16},
  ///   isbn          = {0-444-81648-8}
  /// }
  /// \endverbatim
  struct magic_search
  {
    /// Initialize the Magic Search algorithm on the automaton \a a.
    magic_search(const tgba_tba_proxy *a);
    ~magic_search();

    /// \brief Perform a Magic Search.
    ///
    /// \return true iff the algorithm has found a new accepting
    ///    path.
    ///
    /// check() can be called several times until it return false,
    /// to enumerate all accepting paths.
    bool check();

    /// \brief Print the last accepting path found.
    ///
    /// Restrict printed states to \a the state space of restrict if
    /// supplied.
    std::ostream& print_result(std::ostream& os,
			       const tgba* restrict = 0) const;

  private:

    // The names "stack", "h", and "x", are those used in the paper.

    /// \brief  Records whether a state has be seen with the magic bit
    /// on or off.
    struct magic
    {
      bool seen_without : 1;
      bool seen_with    : 1;
    };

    /// \brief A state for the spot::magic_search algorithm.
    struct magic_state
    {
      const state* s;
      bool m;			///< The state of the magic demon.
    };

    typedef std::pair<magic_state, tgba_succ_iterator*> state_iter_pair;
    typedef std::list<state_iter_pair> stack_type;
    stack_type stack;		///< Stack of visited states on the path.

    typedef std::list<bdd> tstack_type;
    /// \brief Stack of transitions.
    ///
    /// This is an addition to the data from the paper.
    tstack_type tstack;

    typedef Sgi::hash_map<const state*, magic,
			  state_ptr_hash, state_ptr_equal> hash_type;
    hash_type h;		///< Map of visited states.

    /// Append a new state to the current path.
    void push(const state* s, bool m);
    /// Check whether we already visited \a s with the Magic bit set to \a m.
    bool has(const state* s, bool m) const;

    const tgba_tba_proxy* a;	///< The automata to check.
    /// The state for which we are currently seeking an SCC.
    const state* x;
  };


}

#endif // SPOT_TGBAALGOS_MAGIC_HH
