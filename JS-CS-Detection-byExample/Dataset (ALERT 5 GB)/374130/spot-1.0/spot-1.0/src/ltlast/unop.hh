// Copyright (C) 2009, 2010, 2011, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2004 Laboratoire d'Informatique de Paris
// 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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

/// \file ltlast/unop.hh
/// \brief LTL unary operators
#ifndef SPOT_LTLAST_UNOP_HH
# define SPOT_LTLAST_UNOP_HH

#include <map>
#include <iosfwd>
#include "refformula.hh"
#include "bunop.hh"

namespace spot
{
  namespace ltl
  {

    /// \brief Unary operators.
    /// \ingroup ltl_ast
    class unop : public ref_formula
    {
    public:
      enum type {
	// LTL
	Not, X, F, G,
	// ELTL
	Finish,
	// Closure
	Closure, NegClosure, NegClosureMarked
	};

      /// \brief Build an unary operator with operation \a op and
      /// child \a child.
      ///
      /// The following trivial simplifications are performed
      /// automatically (the left expression is rewritten as the right
      /// expression):
      ///   - FF(Exp) = F(Exp)
      ///   - GG(Exp) = G(Exp)
      ///   - F(0) = 0
      ///   - G(0) = 0
      ///   - X(0) = 0
      ///   - F(1) = 1
      ///   - G(1) = 1
      ///   - X(1) = 1
      ///   - !1 = 0
      ///   - !0 = 1
      ///   - ![*0] = 1[+]     (read below)
      ///   - !!Exp = Exp
      ///   - !Closure(Exp) = NegClosure(Exp)
      ///   - !NegClosure(Exp) = Closure(Exp)
      ///   - Closure([*0]) = 1
      ///   - Closure(1) = 1
      ///   - Closure(0) = 0
      ///   - Closure(b) = b
      ///   - NegClosure([*0]) = 0
      ///   - NegClosure(1) = 0
      ///   - NegClosure(0) = 1
      ///   - NegClosure(b) = !b
      ///
      /// This rewriting implies that it is not possible to build an
      /// LTL formula object that is SYNTACTICALLY equal to one of
      /// these left expressions.
      ///
      /// Note that the "![*0]" form cannot be read using the PSL
      /// grammar.  Spot cannot read it either.  However some
      /// BDD-based algorithm may need to negate any constant, so we
      /// handle this one as well.
      static const formula* instance(type op, const formula* child);

      virtual void accept(visitor& v) const;

      /// Get the sole operand of this operator.
      const formula* child() const;

      /// Get the type of this operator.
      type op() const;
      /// Get the type of this operator, as a string.
      const char* op_name() const;

      /// Return a canonic representation of the atomic proposition
      virtual std::string dump() const;

      /// Number of instantiated unary operators.  For debugging.
      static unsigned instance_count();

      /// Dump all instances.  For debugging.
      static std::ostream& dump_instances(std::ostream& os);

    protected:
      typedef std::pair<type, const formula*> pair;
      typedef std::map<pair, const unop*> map;
      static map instances;

      unop(type op, const formula* child);
      virtual ~unop();

    private:
      type op_;
      const formula* child_;
    };


    /// \brief Cast \a f into a unop
    ///
    /// Cast \a f into a unop iff it is a unop instance.  Return 0
    /// otherwise.  This is faster than \c dynamic_cast.
    inline
    const unop*
    is_unop(const formula* f)
    {
      if (f->kind() != formula::UnOp)
	return 0;
      return static_cast<const unop*>(f);
    }

    /// \brief Cast \a f into a unop if it has type \a op.
    ///
    /// Cast \a f into a unop iff it is a unop instance with operator \a op.
    /// Returns 0 otherwise.
    inline
    const unop*
    is_unop(const formula* f, unop::type op)
    {
      if (const unop* uo = is_unop(f))
	if (uo->op() == op)
	  return uo;
      return 0;
    }

    /// \brief Cast \a f into a unop if it is a Not.
    ///
    /// Return 0 otherwise.
    inline
    const unop*
    is_Not(const formula* f)
    {
      return is_unop(f, unop::Not);
    }

    /// \brief Cast \a f into a unop if it is a X.
    ///
    /// Return 0 otherwise.
    inline
    const unop*
    is_X(const formula* f)
    {
      return is_unop(f, unop::X);
    }

    /// \brief Cast \a f into a unop if it is a F.
    ///
    /// Return 0 otherwise.
    inline
    const unop*
    is_F(const formula* f)
    {
      return is_unop(f, unop::F);
    }

    /// \brief Cast \a f into a unop if it is a G.
    ///
    /// Return 0 otherwise.
    inline
    const unop*
    is_G(const formula* f)
    {
      return is_unop(f, unop::G);
    }

    /// \brief Cast \a f into a unop if has the form GF(...).
    ///
    /// Return 0 otherwise.
    inline
    const unop*
    is_GF(const formula* f)
    {
      if (const unop* op = is_G(f))
	return is_F(op->child());
      return 0;
    }

    /// \brief Cast \a f into a unop if has the form FG(...).
    ///
    /// Return 0 otherwise.
    inline
    const unop*
    is_FG(const formula* f)
    {
      if (const unop* op = is_F(f))
	return is_G(op->child());
      return 0;
    }
  }
}

#endif // SPOT_LTLAST_UNOP_HH
