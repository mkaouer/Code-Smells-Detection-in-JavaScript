// Copyright (C) 2009 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
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

#ifndef SPOT_SABA_SABASTATE_HH
# define SPOT_SABA_SABASTATE_HH

#include <bdd.h>
#include <functional>
#include <boost/shared_ptr.hpp>

namespace spot
{

  /// \brief Abstract class for saba states.
  /// \ingroup saba_essentials
  class saba_state
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
    /// \sa spot::saba_state_ptr_less_than
    virtual int compare(const saba_state* other) const = 0;

    /// \brief Hash a state.
    ///
    /// This method returns an integer that can be used as a
    /// hash value for this state.
    ///
    /// Note that the hash value is guaranteed to be unique for all
    /// equal states (in compare()'s sense) for only has long has one
    /// of these states exists.  So it's OK to use a spot::saba_state as a
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
    virtual saba_state* clone() const = 0;

    /// \brief Get the acceptance condition.
    ///
    /// saba are state-labeled automata, then their acceptance conditions
    /// are labeled on states.
    virtual bdd acceptance_conditions() const = 0;

    virtual ~saba_state()
    {
    }
  };

  /// \brief Strict Weak Ordering for \c saba_state*.
  /// \ingroup saba_essentials
  ///
  /// This is meant to be used as a comparison functor for
  /// STL \c map whose key are of type \c saba_state*.
  ///
  /// For instance here is how one could declare
  /// a map of \c saba_state*.
  /// \code
  ///   // Remember how many times each state has been visited.
  ///   std::map<spot::saba_state*, int, spot::saba_state_ptr_less_than> seen;
  /// \endcode
  struct saba_state_ptr_less_than:
    public std::binary_function<const saba_state*, const saba_state*, bool>
  {
    bool
    operator()(const saba_state* left, const saba_state* right) const
    {
      assert(left);
      return left->compare(right) < 0;
    }
  };

  /// \brief An Equivalence Relation for \c saba_state*.
  /// \ingroup saba_essentials
  ///
  /// This is meant to be used as a comparison functor for
  /// Sgi \c hash_map whose key are of type \c saba_state*.
  ///
  /// For instance here is how one could declare
  /// a map of \c saba_state*.
  /// \code
  ///   // Remember how many times each state has been visited.
  ///   Sgi::hash_map<spot::saba_state*, int, spot::saba_state_ptr_hash,
  ///                                    spot::saba_state_ptr_equal> seen;
  /// \endcode
  struct saba_state_ptr_equal:
    public std::binary_function<const saba_state*, const saba_state*, bool>
  {
    bool
    operator()(const saba_state* left, const saba_state* right) const
    {
      assert(left);
      return 0 == left->compare(right);
    }
  };

  /// \brief Hash Function for \c saba_state*.
  /// \ingroup saba_essentials
  /// \ingroup hash_funcs
  ///
  /// This is meant to be used as a hash functor for
  /// Sgi's \c hash_map whose key are of type \c saba_state*.
  ///
  /// For instance here is how one could declare
  /// a map of \c saba_state*.
  /// \code
  ///   // Remember how many times each state has been visited.
  ///   Sgi::hash_map<spot::saba_state*, int, spot::saba_state_ptr_hash,
  ///                                    spot::saba_state_ptr_equal> seen;
  /// \endcode
  struct saba_state_ptr_hash:
    public std::unary_function<const saba_state*, size_t>
  {
    size_t
    operator()(const saba_state* that) const
    {
      assert(that);
      return that->hash();
    }
  };

  // Functions related to shared_ptr.
  //////////////////////////////////////////////////

  typedef boost::shared_ptr<const saba_state> shared_saba_state;

  /// \brief Strict Weak Ordering for \c shared_saba_state
  /// (shared_ptr<const saba_state*>).
  /// \ingroup saba_essentials
  ///
  /// This is meant to be used as a comparison functor for
  /// STL \c map whose key are of type \c shared_saba_state.
  ///
  /// For instance here is how one could declare
  /// a map of \c shared_saba_state.
  /// \code
  ///   // Remember how many times each state has been visited.
  ///   std::map<shared_saba_state, int, spot::saba_state_shared_ptr_less_than>
  ///      seen;
  /// \endcode
  struct saba_state_shared_ptr_less_than:
    public std::binary_function<shared_saba_state,
                                shared_saba_state, bool>
  {
    bool
    operator()(shared_saba_state left,
               shared_saba_state right) const
    {
      assert(left);
      return left->compare(right.get()) < 0;
    }
  };

  /// \brief An Equivalence Relation for \c shared_saba_state
  /// (shared_ptr<const saba_state*>).
  /// \ingroup saba_essentials
  ///
  /// This is meant to be used as a comparison functor for
  /// Sgi \c hash_map whose key are of type \c shared_saba_state.
  ///
  /// For instance here is how one could declare
  /// a map of \c shared_saba_state
  /// \code
  ///   // Remember how many times each state has been visited.
  ///   Sgi::hash_map<shared_saba_state, int,
  ///                 spot::saba_state_shared_ptr_hash,
  ///                 spot::saba_state_shared_ptr_equal> seen;
  /// \endcode
  struct saba_state_shared_ptr_equal:
    public std::binary_function<shared_saba_state,
                                shared_saba_state, bool>
  {
    bool
    operator()(shared_saba_state left,
               shared_saba_state right) const
    {
      assert(left);
      return 0 == left->compare(right.get());
    }
  };

  /// \brief Hash Function for \c shared_saba_state
  ///  (shared_ptr<const saba_state*>).
  /// \ingroup saba_essentials
  /// \ingroup hash_funcs
  ///
  /// This is meant to be used as a hash functor for
  /// Sgi's \c hash_map whose key are of type
  /// \c shared_saba_state.
  ///
  /// For instance here is how one could declare
  /// a map of \c shared_saba_state.
  /// \code
  ///   // Remember how many times each state has been visited.
  ///   Sgi::hash_map<shared_saba_state, int,
  ///                 spot::saba_state_shared_ptr_hash,
  ///                 spot::saba_state_shared_ptr_equal> seen;
  /// \endcode
  struct saba_state_shared_ptr_hash:
    public std::unary_function<shared_saba_state, size_t>
  {
    size_t
    operator()(shared_saba_state that) const
    {
      assert(that);
      return that->hash();
    }
  };

}

#endif // SPOT_SABA_SABASTATE_HH
