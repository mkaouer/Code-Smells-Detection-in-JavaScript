// Copyright (C) 2003, 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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
      /// Entry point for vspot::ltl::visitor instances.
      virtual void accept(visitor& v) = 0;
      /// Entry point for vspot::ltl::const_visitor instances.
      virtual void accept(const_visitor& v) const = 0;

      /// \brief clone this node
      ///
      /// This increments the reference counter of this node (if one is
      /// used).  You should almost never use this method directly as
      /// it doesn't touch the children.  If you want to clone a
      /// whole formula, use spot::ltl::clone() instead.
      formula* ref();
      /// \brief release this node
      ///
      /// This decrements the reference counter of this node (if one is
      /// used) and can free the object.  You should almost never use
      /// this method directly as it doesn't touch the children.  If you
      /// want to release a whole formula, use spot::ltl::destroy() instead.
      static void unref(formula* f);

      /// Return a canonic representation of the formula
      const std::string& dump() const;

      /// Return a hash_key for the formula.
      const size_t
      hash() const
      {
	return hash_key_;
      }
    protected:
      virtual ~formula();

      /// \brief increment reference counter if any
      virtual void ref_();
      /// \brief decrement reference counter if any, return true when
      /// the instance must be deleted (usually when the counter hits 0).
      virtual bool unref_();

      /// \brief Compute key_ from dump_.
      ///
      /// Should be called once in each object, after dump_ has been set.
      void set_key_();
      /// The canonic representation of the formula
      std::string dump_;
      /// \brief The hash key of this formula.
      ///
      /// Initialized by set_key_().
      size_t hash_key_;
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
	size_t l = left->hash();
	size_t r = right->hash();
	if (1 != r)
	  return l < r;
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
