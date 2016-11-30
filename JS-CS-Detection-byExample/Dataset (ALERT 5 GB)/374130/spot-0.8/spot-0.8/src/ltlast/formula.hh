// Copyright (C) 2008, 2009 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2005 Laboratoire d'Informatique de
// Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
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

/// \file ltlast/formula.hh
/// \brief LTL formula interface
#ifndef SPOT_LTLAST_FORMULA_HH
# define SPOT_LTLAST_FORMULA_HH

#include <string>
#include <cassert>
#include "predecl.hh"

namespace spot
{
  namespace ltl
  {
    /// \defgroup ltl LTL formulae
    ///
    /// This module gathers types and definitions related to LTL formulae.

    /// \addtogroup ltl_essential Essential LTL types
    /// \ingroup ltl

    /// \addtogroup ltl_ast LTL Abstract Syntax Tree
    /// \ingroup ltl

    /// \addtogroup ltl_environment LTL environments
    /// \ingroup ltl
    /// LTL environment implementations.

    /// \addtogroup ltl_algorithm Algorithms for LTL formulae
    /// \ingroup ltl

    /// \addtogroup ltl_io Input/Output of LTL formulae
    /// \ingroup ltl_algorithm

    /// \addtogroup ltl_visitor Derivable visitors
    /// \ingroup ltl_algorithm

    /// \addtogroup ltl_rewriting Rewriting LTL formulae
    /// \ingroup ltl_algorithm

    /// \addtogroup ltl_misc Miscellaneous algorithms for LTL formulae
    /// \ingroup ltl_algorithm


    /// \brief An LTL formula.
    /// \ingroup ltl_essential
    /// \ingroup ltl_ast
    ///
    /// The only way you can work with a formula is to
    /// build a spot::ltl::visitor or spot::ltl::const_visitor.
    class formula
    {
    public:
      formula() : count_(++max_count) {}

      /// Entry point for vspot::ltl::visitor instances.
      virtual void accept(visitor& v) = 0;
      /// Entry point for vspot::ltl::const_visitor instances.
      virtual void accept(const_visitor& v) const = 0;

      /// \brief clone this node
      ///
      /// This increments the reference counter of this node (if one is
      /// used).
      formula* clone() const;
      /// \brief release this node
      ///
      /// This decrements the reference counter of this node (if one is
      /// used) and can free the object.
      void destroy() const;

      /// Return a canonic representation of the formula
      virtual std::string dump() const = 0;

      /// Return a hash key for the formula.
      size_t
      hash() const
      {
	return count_;
      }
    protected:
      virtual ~formula();

      /// \brief increment reference counter if any
      virtual void ref_();
      /// \brief decrement reference counter if any, return true when
      /// the instance must be deleted (usually when the counter hits 0).
      virtual bool unref_();

      /// \brief The hash key of this formula.
      size_t count_;

    private:
      /// \brief Number of formulae created so far.
      static size_t max_count;
    };

    /// \brief Strict Weak Ordering for <code>const formula*</code>.
    /// \ingroup ltl_essentials
    ///
    /// This is meant to be used as a comparison functor for
    /// STL \c map whose key are of type <code>const formula*</code>.
    ///
    /// For instance here is how one could declare
    /// a map of \c const::formula*.
    /// \code
    ///   // Remember how many times each formula has been seen.
    ///   std::map<const spot::ltl::formula*, int,
    ///            spot::formula_ptr_less_than> seen;
    /// \endcode
    struct formula_ptr_less_than:
      public std::binary_function<const formula*, const formula*, bool>
    {
      bool
      operator()(const formula* left, const formula* right) const
      {
	assert(left);
	assert(right);
	if (left == right)
	  return false;
	size_t l = left->hash();
	size_t r = right->hash();
	if (l != r)
	  return l < r;
	// Because the hash code assigned to each formula is the
	// number of formulae constructed so far, it is very unlikely
	// that we will ever reach a case were two different formulae
	// have the same hash.  This will happen only ever with have
	// produced 256**sizeof(size_t) formulae (i.e. max_count has
	// looped back to 0 and started over).  In that case we can
	// order two formulae by looking at their text representation.
	// We could be more efficient and look at their AST, but it's
	// not worth the burden.  (Also ordering pointers is ruled out
	// because it breaks the determinism of the implementation.)
	return left->dump() < right->dump();
      }
    };

    /// \brief Hash Function for <code>const formula*</code>.
    /// \ingroup ltl_essentials
    /// \ingroup hash_funcs
    ///
    /// This is meant to be used as a hash functor for
    /// Sgi's \c hash_map whose key are of type <code>const formula*</code>.
    ///
    /// For instance here is how one could declare
    /// a map of \c const::formula*.
    /// \code
    ///   // Remember how many times each formula has been seen.
    ///   Sgi::hash_map<const spot::ltl::formula*, int,
    ///                 const spot::ltl::formula_ptr_hash> seen;
    /// \endcode
    struct formula_ptr_hash:
      public std::unary_function<const formula*, size_t>
    {
      size_t
      operator()(const formula* that) const
      {
	assert(that);
	return that->hash();
      }
    };


  }
}

#endif // SPOT_LTLAST_FORMULA_HH
