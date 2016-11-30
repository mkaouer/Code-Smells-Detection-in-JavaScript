// Copyright (C) 2011, 2012, 2013 Laboratoire de Recherche et
// Developpement de l'Epita (LRDE).
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

#ifndef SPOT_LTLVISIT_SIMPLIFY_HH
# define SPOT_LTLVISIT_SIMPLIFY_HH

#include "ltlast/formula.hh"
#include "bdd.h"
#include "tgba/bdddict.hh"
#include <iosfwd>

namespace spot
{
  namespace ltl
  {
    class ltl_simplifier_options
    {
    public:
      ltl_simplifier_options(bool basics = true,
			     bool synt_impl = true,
			     bool event_univ = true,
			     bool containment_checks = false,
			     bool containment_checks_stronger = false,
			     bool nenoform_stop_on_boolean = false,
			     bool reduce_size_strictly = false,
			     bool boolean_to_isop = false,
			     bool favor_event_univ = false)
	: reduce_basics(basics),
	  synt_impl(synt_impl),
	  event_univ(event_univ),
	  containment_checks(containment_checks),
	  containment_checks_stronger(containment_checks_stronger),
	  nenoform_stop_on_boolean(nenoform_stop_on_boolean),
	  reduce_size_strictly(reduce_size_strictly),
	  boolean_to_isop(boolean_to_isop),
	  favor_event_univ(favor_event_univ)
      {
      }

      bool reduce_basics;
      bool synt_impl;
      bool event_univ;
      bool containment_checks;
      bool containment_checks_stronger;
      // If true, Boolean subformulae will not be put into
      // negative normal form.
      bool nenoform_stop_on_boolean;
      // If true, some rules that produce slightly larger formulae
      // will be disabled.  Those larger formulae are normally easier
      // to translate, so we recommend to set this to false.
      bool reduce_size_strictly;
      // If true, Boolean subformulae will be rewritten in ISOP form.
      bool boolean_to_isop;
      // Try to isolate subformulae that are eventual and universal.
      bool favor_event_univ;
    };

    // fwd declaration to hide technical details.
    class ltl_simplifier_cache;

    /// \ingroup ltl_rewriting
    /// \brief Rewrite or simplify \a f in various ways.
    class SPOT_API ltl_simplifier
    {
    public:
      ltl_simplifier(bdd_dict* dict = 0);
      ltl_simplifier(const ltl_simplifier_options& opt, bdd_dict* dict = 0);
      ~ltl_simplifier();

      /// Simplify the formula \a f (using options supplied to the
      /// constructor).
      const formula* simplify(const formula* f);

      /// Build the negative normal form of formula \a f.
      /// All negations of the formula are pushed in front of the
      /// atomic propositions.  Operators <=>, =>, xor are all removed
      /// (calling spot::ltl::unabbreviate_ltl is not needed).
      ///
      /// \param f The formula to normalize.
      /// \param negated If \c true, return the negative normal form of
      ///        \c !f
      const formula*
      negative_normal_form(const formula* f, bool negated = false);

      /// \brief Syntactic implication.
      ///
      /// Returns whether \a f syntactically implies \a g.
      ///
      /// This is adapted from
      /** \verbatim
          @InProceedings{          somenzi.00.cav,
          author         = {Fabio Somenzi and Roderick Bloem},
          title          = {Efficient {B\"u}chi Automata for {LTL} Formulae},
          booktitle      = {Proceedings of the 12th International Conference on
                           Computer Aided Verification (CAV'00)},
          pages          = {247--263},
          year           = {2000},
          volume         = {1855},
          series         = {Lecture Notes in Computer Science},
          publisher      = {Springer-Verlag}
          }
          \endverbatim */
      ///
      bool syntactic_implication(const formula* f, const formula* g);
      /// \brief Syntactic implication with one negated argument.
      ///
      /// If \a right is true, this method returns whether
      /// \a f implies !\a g.  If \a right is false, this returns
      /// whether !\a f implies \a g.
      bool syntactic_implication_neg(const formula* f, const formula* g,
				     bool right);

      /// \brief check whether two formulae are equivalent.
      ///
      /// This costly check performs up to four translations,
      /// two products, and two emptiness checks.
      bool are_equivalent(const formula* f, const formula* g);


      /// \brief Check whether \a f implies \a g.
      ///
      /// This operation is costlier than syntactic_implication()
      /// because it requires two translation, one product and one
      /// emptiness check.
      bool implication(const formula* f, const formula* g);

      /// \brief Convert a Boolean formula as a BDD.
      ///
      /// If you plan to use this method, be sure to pass a bdd_dict
      /// to the constructor.
      bdd as_bdd(const formula* f);

      /// \brief Clear the as_bdd() cache.
      ///
      /// Calling this function is recommended before running other
      /// algorithms that create BDD variables in a more natural
      /// order.  For instance ltl_to_tgba_fm() will usually be more
      /// efficient if the BDD variables for atomic propositions have
      /// not been ordered before hand.
      ///
      /// This also clears the language containment cache.
      void clear_as_bdd_cache();

      /// Return the bdd_dict used.
      bdd_dict* get_dict() const;

      /// Cached version of spot::ltl::star_normal_form().
      const formula* star_normal_form(const formula* f);

      /// \brief Rewrite a Boolean formula \a f into as an irredundant
      /// sum of product.
      ///
      /// This uses a cache, so it is OK to call this with identical
      /// arguments.
      const formula* boolean_to_isop(const formula* f);

      /// Dump statistics about the caches.
      void print_stats(std::ostream& os) const;

    private:
      ltl_simplifier_cache* cache_;
      // Copy disallowed.
      ltl_simplifier(const ltl_simplifier&);
      bool owndict;
    };
  }

}

#endif // SPOT_LTLVISIT_SIMPLIFY_HH
