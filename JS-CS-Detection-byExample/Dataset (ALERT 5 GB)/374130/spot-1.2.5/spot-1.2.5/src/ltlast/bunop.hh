// -*- coding: utf-8 -*-
// Copyright (C) 2010, 2011, 2012, 2013, 2014 Laboratoire de Recherche
// et Développement de l'Epita (LRDE).
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

/// \file ltlast/bunop.hh
/// \brief Bounded Unary operators
#ifndef SPOT_LTLAST_BUNOP_HH
# define SPOT_LTLAST_BUNOP_HH

#include "refformula.hh"
#include <map>
#include <iosfwd>
#include "constant.hh"

namespace spot
{
  namespace ltl
  {

    /// \ingroup ltl_ast
    /// \brief Bounded unary operator.
    class SPOT_API bunop : public ref_formula
    {
    public:
      enum type { Star };

      static const unsigned unbounded = -1U;

      /// \brief Build a bunop with bounds \a min and \a max.
      ///
      /// The following trivial simplifications are performed
      /// automatically (the left expression is rewritten as the right
      /// expression):
      ///   - 0[*0..max] = [*0]
      ///   - 0[*min..max] = 0 if min > 0
      ///   - [*0][*min..max] = [*0]
      ///   - Exp[*0] = [*0]
      ///   - Exp[*i..j][*k..l] = Exp[*ik..jl] if i*(k+1)<=jk+1.
      ///   - Exp[*1] = Exp
      ///
      /// These rewriting rules imply that it is not possible to build
      /// an LTL formula object that is SYNTACTICALLY equal to one of
      /// these left expressions.
      static const formula* instance(type op,
				     const formula* child,
				     unsigned min = 0,
				     unsigned max = unbounded);

      /// \brief Implement <code>b[->i..j]</code> using the Kleen star.
      ///
      /// <code>b[->i..j]</code> is implemented as
      /// <code>((!b)[*];b)[*i..j]</code>.
      ///
      /// Note that \a min defaults to 1, not 0, because [->] means
      /// [->1..].
      ///
      /// \pre \a child must be a Boolean formula.
      static const formula* sugar_goto(const formula* child,
				       unsigned min = 1,
				       unsigned max = unbounded);

      /// \brief Implement b[=i..j] using the Kleen star.
      ///
      /// <code>b[=i..j]</code> is implemented as
      /// <code>((!b)[*];b)[*i..j];(!b)[*]</code>.
      ///
      /// \pre \a child must be a Boolean formula.
      static const formula* sugar_equal(const formula* child,
					unsigned min = 0,
					unsigned max = unbounded);

      virtual void accept(visitor& v) const;

      /// Get the sole operand of this operator.
      const formula* child() const
      {
	return child_;
      }

      /// Minimum number of repetition.
      unsigned min() const
      {
	return min_;
      }

      /// Minimum number of repetition.
      unsigned max() const
      {
	return max_;
      }

      /// \brief A string representation of the operator.
      ///
      /// For instance "[*2..]".
      std::string format() const;

      /// Get the type of this operator.
      type op() const
      {
	return op_;
      }

      /// Get the type of this operator, as a string.
      const char* op_name() const;

      /// Return a canonic representation of operation.
      virtual std::string dump() const;

      /// Number of instantiated unary operators.  For debugging.
      static unsigned instance_count();

      /// Dump all instances.  For debugging.
      static std::ostream& dump_instances(std::ostream& os);

      /// \brief Return a formula for <code>1[*]</code>.
      ///
      /// A global instance is returned, and it should not be
      /// destroyed.  Remember to clone it if you use it to build a
      /// formula.
      static const formula* one_star()
      {
	if (!one_star_)
	  one_star_ = instance(Star, constant::true_instance());
	return one_star_;
      }

    protected:
      typedef std::pair<unsigned, unsigned> pairu;
      typedef std::pair<type, const formula*> pairo;
      typedef std::pair<pairo, pairu> pair;
      typedef std::map<pair, const bunop*> map;
      static map instances;

      bunop(type op, const formula* child, unsigned min, unsigned max);
      virtual ~bunop();

    private:
      type op_;
      const formula* child_;
      unsigned min_;
      unsigned max_;
      static const formula* one_star_;
    };

    /// \brief Cast \a f into a bunop.
    ///
    /// Cast \a f into a bunop iff it is a bunop instance.  Return 0
    /// otherwise.  This is faster than \c dynamic_cast.
    inline
    const bunop*
    is_bunop(const formula* f)
    {
      if (f->kind() != formula::BUnOp)
	return 0;
      return static_cast<const bunop*>(f);
    }

    /// \brief Cast \a f into a bunop if it has type \a op.
    ///
    /// Cast \a f into a bunop iff it is a bunop instance with operator \a op.
    /// Returns 0 otherwise.
    inline
    const bunop*
    is_bunop(const formula* f, bunop::type op)
    {
      if (const bunop* bo = is_bunop(f))
	if (bo->op() == op)
	  return bo;
      return 0;
    }

    /// \brief Cast \a f into a bunop if it is a Star.
    ///
    /// Return 0 otherwise.
    inline
    const bunop*
    is_Star(const formula* f)
    {
      return is_bunop(f, bunop::Star);
    }

    /// \brief Cast \a f into a bunop if it is a Star[0..].
    ///
    /// Return 0 otherwise.
    inline
    const bunop*
    is_KleenStar(const formula* f)
    {
      if (const bunop* b = is_Star(f))
	if (b->min() == 0 && b->max() == bunop::unbounded)
	  return b;
      return 0;
    }

  }
}
#endif // SPOT_LTLAST_BUNOP_HH
