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

#ifndef SPOT_TGBA_STATE_HH
# define SPOT_TGBA_STATE_HH

#include <cstddef>
#include <bdd.h>
#include <cassert>
#include <functional>

namespace spot
{

  /// \brief Abstract class for states.
  class state
  {
  public:
    /// \brief Compares two states (that come from the same automaton).
    ///
    /// This method returns an integer less than, equal to, or greater
    /// than zero if \a this is found, respectively, to be less than, equal
    /// to, or greater than \a other according to some implicit total order.
    ///
    /// This method should not be called to compare states from
    /// different automata.
    ///
    /// \sa spot::state_ptr_less_than
    virtual int compare(const state* other) const = 0;

    /// \brief Hash a state.
    ///
    /// This method returns an integer that can be used as a
    /// hash value for this state.
    ///
    /// Note that the hash value is guaranteed to be unique for all
    /// equal states (in compare()'s sense) for only has long has one
    /// of these states exists.  So it's OK to use a spot::state as a
    /// key in a \c hash_map because the mere use of the state as a
    /// key in the hash will ensure the state continues to exist.
    ///
    /// However if you create the state, get its hash key, delete the
    /// state, recreate the same state, and get its hash key, you may
    /// obtain two different hash keys if the same state were not
    /// already used elsewhere.  In practice this weird situation can
    /// occur only when the state is BDD-encoded, because BDD numbers
    /// (used to build the hash value) can be reused for other
    /// formulas.  That probably doesn't matter, since the hash value
    /// is meant to be used in a \c hash_map, but it had to be noted.
    virtual size_t hash() const = 0;

    /// Duplicate a state.
    virtual state* clone() const = 0;

    virtual ~state()
    {
    }
  };

  /// \brief Strict Weak Ordering for \c state*.
  ///
  /// This is meant to be used as a comparison functor for
  /// STL \c map whose key are of type \c state*.
  ///
  /// For instance here is how one could declare
  /// a map of \c state*.
  /// \code
  ///   // Remember how many times each state has been visited.
  ///   std::map<spot::state*, int, spot::state_ptr_less_than> seen;
  /// \endcode
  struct state_ptr_less_than :
    public std::binary_function<const state*, const state*, bool>
  {
    bool
    operator()(const state* left, const state* right) const
    {
      assert(left);
      return left->compare(right) < 0;
    }
  };

  /// \brief An Equivalence Relation for \c state*.
  ///
  /// This is meant to be used as a comparison functor for
  /// Sgi \c hash_map whose key are of type \c state*.
  ///
  /// For instance here is how one could declare
  /// a map of \c state*.
  /// \code
  ///   // Remember how many times each state has been visited.
  ///   Sgi::hash_map<spot::state*, int, spot::state_ptr_less_than,
  ///                                    spot::state_ptr_equal> seen;
  /// \endcode
  struct state_ptr_equal :
    public std::binary_function<const state*, const state*, bool>
  {
    bool
    operator()(const state* left, const state* right) const
    {
      assert(left);
      return 0 == left->compare(right);
    }
  };

  /// \brief Hash Function for \c state*.
  ///
  /// This is meant to be used as a hash functor for
  /// Sgi's \c hash_map whose key are of type \c state*.
  ///
  /// For instance here is how one could declare
  /// a map of \c state*.
  /// \code
  ///   // Remember how many times each state has been visited.
  ///   Sgi::hash_map<spot::state*, int, spot::state_ptr_less_than,
  ///                                    spot::state_ptr_equal> seen;
  /// \endcode
  struct state_ptr_hash :
    public std::unary_function<const state*, size_t>
  {
    size_t
    operator()(const state* that) const
    {
      assert(that);
      return that->hash();
    }
  };

}

#endif // SPOT_TGBA_STATE_HH
