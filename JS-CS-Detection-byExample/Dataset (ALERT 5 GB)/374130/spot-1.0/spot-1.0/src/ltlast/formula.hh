// -*- coding: utf-8 -*-
// Copyright (C) 2008, 2009, 2010, 2011, 2012 Laboratoire de Recherche
// et Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2005 Laboratoire d'Informatique de
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

/// \file ltlast/formula.hh
/// \brief LTL formula interface
#ifndef SPOT_LTLAST_FORMULA_HH
# define SPOT_LTLAST_FORMULA_HH

#include <string>
#include <cassert>
#include "predecl.hh"
#include <list>

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
      /// Kind of a sub-formula
      enum opkind { Constant,
		    AtomicProp,
		    UnOp,
		    BinOp,
		    MultOp,
		    BUnOp,
		    AutomatOp };

    protected:
      formula(opkind k) : count_(max_count++), kind_(k)
      {
	// If the counter of formulae ever loops, we want to skip the
	// first three values, because they are permanently associated
	// to constants, and it is convenient to have constants smaller
	// than all other formulae.
	if (max_count == 0)
	  max_count = 3;
      }

    public:
      /// Entry point for spot::ltl::visitor instances.
      virtual void accept(visitor& v) const = 0;

      /// \brief clone this node
      ///
      /// This increments the reference counter of this node (if one is
      /// used).
      const formula* clone() const;
      /// \brief release this node
      ///
      /// This decrements the reference counter of this node (if one is
      /// used) and can free the object.
      void destroy() const;

      /// Return a canonic representation of the formula
      virtual std::string dump() const = 0;

      /// Return the kind of the top-level operator.
      opkind kind() const
      {
	return kind_;
      }

      ////////////////
      // Properties //
      ////////////////

      /// Whether the formula use only boolean operators.
      bool is_boolean() const
      {
	return is.boolean;
      }

      /// Whether the formula use only AND, OR, and NOT operators.
      bool is_sugar_free_boolean() const
      {
	return is.sugar_free_boolean;
      }

      /// \brief Whether the formula is in negative normal form.
      ///
      /// A formula is in negative normal form if the not operators
      /// occur only in front of atomic propositions.
      bool is_in_nenoform() const
      {
	return is.in_nenoform;
      }

      /// Whether the formula avoids the X operator.
      bool is_X_free() const
      {
	return is.X_free;
      }

      /// Whether the formula avoids the F and G operators.
      bool is_sugar_free_ltl() const
      {
	return is.sugar_free_ltl;
      }

      /// Whether the formula uses only LTL operators.
      bool is_ltl_formula() const
      {
	return is.ltl_formula;
      }

      /// Whether the formula uses only ELTL operators.
      bool is_eltl_formula() const
      {
	return is.eltl_formula;
      }

      /// Whether the formula uses only PSL operators.
      bool is_psl_formula() const
      {
	return is.psl_formula;
      }

      /// Whether the formula uses only SERE operators.
      bool is_sere_formula() const
      {
	return is.sere_formula;
      }

      /// Whether a SERE describes a finite language, or an LTL
      /// formula uses no temporal operator but X.
      bool is_finite() const
      {
	return is.finite;
      }

      /// \brief Whether the formula is purely eventual.
      ///
      /// Pure eventuality formulae are defined in
      /// \verbatim
      /// @InProceedings{	  etessami.00.concur,
      /// author  	= {Kousha Etessami and Gerard J. Holzmann},
      /// title		= {Optimizing {B\"u}chi Automata},
      /// booktitle	= {Proceedings of the 11th International Conference on
      /// 		  Concurrency Theory (Concur'2000)},
      /// pages		= {153--167},
      /// year		= {2000},
      /// editor  	= {C. Palamidessi},
      /// volume  	= {1877},
      /// series  	= {Lecture Notes in Computer Science},
      /// publisher	= {Springer-Verlag}
      /// }
      /// \endverbatim
      ///
      /// A word that satisfies a pure eventuality can be prefixed by
      /// anything and still satisfies the formula.
      bool is_eventual() const
      {
	return is.eventual;
      }

      /// \brief Whether a formula is purely universal.
      ///
      /// Purely universal formulae are defined in
      /// \verbatim
      /// @InProceedings{	  etessami.00.concur,
      /// author  	= {Kousha Etessami and Gerard J. Holzmann},
      /// title		= {Optimizing {B\"u}chi Automata},
      /// booktitle	= {Proceedings of the 11th International Conference on
      /// 		  Concurrency Theory (Concur'2000)},
      /// pages		= {153--167},
      /// year		= {2000},
      /// editor  	= {C. Palamidessi},
      /// volume  	= {1877},
      /// series  	= {Lecture Notes in Computer Science},
      /// publisher	= {Springer-Verlag}
      /// }
      /// \endverbatim
      ///
      /// Any (non-empty) suffix of a word that satisfies a purely
      /// universal formula also satisfies the formula.
      bool is_universal() const
      {
	return is.universal;
      }

      /// Whether a PSL/LTL formula is syntactic safety property.
      bool is_syntactic_safety() const
      {
	return is.syntactic_safety;
      }

      /// Whether a PSL/LTL formula is syntactic guarantee property.
      bool is_syntactic_guarantee() const
      {
	return is.syntactic_guarantee;
      }

      /// Whether a PSL/LTL formula is syntactic obligation property.
      bool is_syntactic_obligation() const
      {
	return is.syntactic_obligation;
      }

      /// Whether a PSL/LTL formula is syntactic recurrence property.
      bool is_syntactic_recurrence() const
      {
	return is.syntactic_recurrence;
      }

      /// Whether a PSL/LTL formula is syntactic persistence property.
      bool is_syntactic_persistence() const
      {
	return is.syntactic_persistence;
      }

      /// Whether the formula has an occurrence of EConcatMarked.
      bool is_marked() const
      {
	return !is.not_marked;
      }

      /// Whether the formula accepts [*0].
      bool accepts_eword() const
      {
	return is.accepting_eword;
      }

      bool has_lbt_atomic_props() const
      {
	return is.lbt_atomic_props;
      }

      /// The properties as a field of bits.  For internal use.
      unsigned get_props() const
      {
	return props;
      }

      /// Return a hash key for the formula.
      size_t
      hash() const
      {
	return count_;
      }
    protected:
      virtual ~formula();

      /// \brief increment reference counter if any
      virtual void ref_() const;
      /// \brief decrement reference counter if any, return true when
      /// the instance must be deleted (usually when the counter hits 0).
      virtual bool unref_() const;

      /// \brief The hash key of this formula.
      size_t count_;

      struct ltl_prop
      {
	// All properties here should be expressed in such a a way
	// that property(f && g) is just property(f)&property(g).
	// This allows us to compute all properties of a compound
	// formula in one operation.
	//
	// For instance we do not use a property that says "has
	// temporal operator", because it would require an OR between
	// the two arguments.  Instead we have a property that
	// says "no temporal operator", and that one is computed
	// with an AND between the arguments.
	//
	// Also choose a name that makes sense when prefixed with
	// "the formula is".
	bool boolean:1;		   // No temporal operators.
	bool sugar_free_boolean:1; // Only AND, OR, and NOT operators.
	bool in_nenoform:1;	   // Negative Normal Form.
	bool X_free:1;		   // No X operators.
	bool sugar_free_ltl:1;	   // No F and G operators.
	bool ltl_formula:1;	   // Only LTL operators.
	bool eltl_formula:1;	   // Only ELTL operators.
	bool psl_formula:1;	   // Only PSL operators.
	bool sere_formula:1;	   // Only SERE operators.
	bool finite:1;		   // Finite SERE formulae, or Bool+X forms.
	bool eventual:1;	   // Purely eventual formula.
	bool universal:1;	   // Purely universal formula.
	bool syntactic_safety:1;   // Syntactic Safety Property.
	bool syntactic_guarantee:1;   // Syntactic Guarantee Property.
	bool syntactic_obligation:1;  // Syntactic Obligation Property.
	bool syntactic_recurrence:1;  // Syntactic Recurrence Property.
	bool syntactic_persistence:1; // Syntactic Persistence Property.
	bool not_marked:1;	   // No occurrence of EConcatMarked.
	bool accepting_eword:1;	   // Accepts the empty word.
	bool lbt_atomic_props:1;   // Use only atomic propositions like p42.
      };
      union
      {
	// Use an unsigned for fast computation of all properties.
	unsigned props;
	ltl_prop is;
      };

    private:
      /// \brief Number of formulae created so far.
      static size_t max_count;
      opkind kind_;
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

    /// Print the properties of formula \a f on stream \a out.
    std::ostream& print_formula_props(std::ostream& out,
				      const formula* f,
				      bool abbreviated = false);

    /// List the properties of formula \a f.
    std::list<std::string> list_formula_props(const formula* f);
  }
}

#endif // SPOT_LTLAST_FORMULA_HH
