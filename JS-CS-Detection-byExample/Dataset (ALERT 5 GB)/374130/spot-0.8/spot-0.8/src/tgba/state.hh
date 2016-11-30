// Copyright (C) 2009, 2011 Laboratoire de Recherche et DÃ©veloppement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2004 Laboratoire d'Informatique de Paris 6
// (LIP6), département Systèmes Répartis Coopératifs (SRC), Université
// Pierre et Marie Curie.
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
#include <boost/shared_ptr.hpp>
#include "misc/casts.hh"

namespace spot
{

  /// \brief Abstract class for states.
  /// \ingroup tgba_essentials
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

    /// \brief Release a state.
    ///
    /// Methods from the tgba or tgba_succ_iterator always return a
    /// new state that you should deallocate with this function.
    /// Before Spot 0.7, you had to "delete" your state directly.
    /// Starting with Spot 0.7, you update your code to this function
    /// instead (which simply calls "delete").  In a future version,
    /// some subclasses will redefine destroy() to allow better memory
    /// management (e.g. no memory allocation for explicit automata).
    virtual void destroy() const
    {
      delete this;
    }

  protected:
    /// \brief Destructor.
    ///
    /// \deprecated Client code should now call
    /// <code>s->destroy();</code> instead of <code>delete s;</code>.
    virtual ~state()
    {
    }
  };

  /// \brief Strict Weak Ordering for \c state*.
  /// \ingroup tgba_essentials
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
  struct state_ptr_less_than:
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
  /// \ingroup tgba_essentials
  ///
  /// This is meant to be used as a comparison functor for
  /// Sgi \c hash_map whose key are of type \c state*.
  ///
  /// For instance here is how one could declare
  /// a map of \c state*.
  /// \code
  ///   // Remember how many times each state has been visited.
  ///   Sgi::hash_map<spot::state*, int, spot::state_ptr_hash,
  ///                                    spot::state_ptr_equal> seen;
  /// \endcode
  struct state_ptr_equal:
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
  /// \ingroup tgba_essentials
  /// \ingroup hash_funcs
  ///
  /// This is meant to be used as a hash functor for
  /// Sgi's \c hash_map whose key are of type \c state*.
  ///
  /// For instance here is how one could declare
  /// a map of \c state*.
  /// \code
  ///   // Remember how many times each state has been visited.
  ///   Sgi::hash_map<spot::state*, int, spot::state_ptr_hash,
  ///                                    spot::state_ptr_equal> seen;
  /// \endcode
  struct state_ptr_hash:
    public std::unary_function<const state*, size_t>
  {
    size_t
    operator()(const state* that) const
    {
      assert(that);
      return that->hash();
    }
  };

  // Functions related to shared_ptr.
  //////////////////////////////////////////////////

  typedef boost::shared_ptr<const state> shared_state;

  inline void shared_state_deleter(state* s) { s->destroy(); }

  /// \brief Strict Weak Ordering for \c shared_state
  /// (shared_ptr<const state*>).
  /// \ingroup tgba_essentials
  ///
  /// This is meant to be used as a comparison functor for
  /// STL \c map whose key are of type \c shared_state.
  ///
  /// For instance here is how one could declare
  /// a map of \c shared_state.
  /// \code
  ///   // Remember how many times each state has been visited.
  ///   std::map<shared_state, int, spot::state_shared_ptr_less_than> seen;
  /// \endcode
  struct state_shared_ptr_less_than:
    public std::binary_function<shared_state,
                                shared_state, bool>
  {
    bool
    operator()(shared_state left,
               shared_state right) const
    {
      assert(left);
      return left->compare(right.get()) < 0;
    }
  };

  /// \brief An Equivalence Relation for \c shared_state
  /// (shared_ptr<const state*>).
  /// \ingroup tgba_essentials
  ///
  /// This is meant to be used as a comparison functor for
  /// Sgi \c hash_map whose key are of type \c shared_state.
  ///
  /// For instance here is how one could declare
  /// a map of \c shared_state
  /// \code
  ///   // Remember how many times each state has been visited.
  ///   Sgi::hash_map<shared_state, int,
  ///                 spot::state_shared_ptr_hash,
  ///                 spot::state_shared_ptr_equal> seen;
  /// \endcode
  struct state_shared_ptr_equal:
    public std::binary_function<shared_state,
                                shared_state, bool>
  {
    bool
    operator()(shared_state left,
               shared_state right) const
    {
      assert(left);
      return 0 == left->compare(right.get());
    }
  };

  /// \brief Hash Function for \c shared_state (shared_ptr<const state*>).
  /// \ingroup tgba_essentials
  /// \ingroup hash_funcs
  ///
  /// This is meant to be used as a hash functor for
  /// Sgi's \c hash_map whose key are of type
  /// \c shared_state.
  ///
  /// For instance here is how one could declare
  /// a map of \c shared_state.
  /// \code
  ///   // Remember how many times each state has been visited.
  ///   Sgi::hash_map<shared_state, int,
  ///                 spot::state_shared_ptr_hash,
  ///                 spot::state_shared_ptr_equal> seen;
  /// \endcode
  struct state_shared_ptr_hash:
    public std::unary_function<shared_state, size_t>
  {
    size_t
    operator()(shared_state that) const
    {
      assert(that);
      return that->hash();
    }
  };

}

#endif // SPOT_TGBA_STATE_HH
