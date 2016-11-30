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

#ifndef SPOT_LTLAST_MULTOP_HH
# define SPOT_LTLAST_MULTOP_HH

#include <vector>
#include <map>
#include "refformula.hh"

namespace spot
{
  namespace ltl
  {

    /// \brief Multi-operand operators.
    ///
    /// These operators are considered commutative and associative.
    class multop : public ref_formula
    {
    public:
      enum type { Or, And };

      /// List of formulae.
      typedef std::vector<formula*> vec;

      /// \brief Build a spot::ltl::multop with two children.
      ///
      /// If one of the children itself is a spot::ltl::multop
      /// with the same type, it will be merged.  I.e., children
      /// if that child will be added, and that child itself will
      /// be destroyed.  This allows incremental building of
      /// n-ary ltl::multop.
      ///
      /// This functions can perform slight optimizations and
      /// may not return an ltl::multop objects.  For instance
      /// if \c first and \c second are equal, that formula is
      /// returned as-is.
      static formula* instance(type op, formula* first, formula* second);

      /// \brief Build a spot::ltl::multop with many children.
      ///
      /// Same as the other instance() function, but take a vector of
      /// formula in argument.  This vector is acquired by the
      /// spot::ltl::multop class, the caller should allocate it with
      /// \c new, but not use it (especially not destroy it) after it
      /// has been passed to spot::ltl::multop.
      ///
      /// This functions can perform slight optimizations and
      /// may not return an ltl::multop objects.  For instance
      /// if the vector contain only one unique element, this
      /// this formula will be returned as-is.
      static formula* instance(type op, vec* v);

      virtual void accept(visitor& v);
      virtual void accept(const_visitor& v) const;

      /// Get the number of children.
      unsigned size() const;
      /// \brief Get the nth children.
      ///
      /// Starting with \a n = 0.
      const formula* nth(unsigned n) const;
      /// \brief Get the nth children.
      ///
      /// Starting with \a n = 0.
      formula* nth(unsigned n);

      /// Get the type of this operator.
      type op() const;
      /// Get the type of this operator, as a string.
      const char* op_name() const;

      /// Number of instantiated multi-operand operators.  For debugging.
      static unsigned instance_count();

    protected:
      typedef std::pair<type, vec*> pair;
      /// Comparison functor used internally by ltl::multop.
      struct paircmp
      {
	bool
	operator () (const pair& p1, const pair& p2) const
	{
	  if (p1.first != p2.first)
	    return p1.first < p2.first;
	  return *p1.second < *p2.second;
	}
      };
      typedef std::map<pair, formula*, paircmp> map;
      static map instances;

      multop(type op, vec* v);
      virtual ~multop();

    private:
      type op_;
      vec* children_;
    };

  }
}

#endif // SPOT_LTLAST_MULTOP_HH
