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

/// \file ltlast/binop.hh
/// \brief LTL binary operators
///
/// This does not include \c AND and \c OR operators.  These are
/// considered to be multi-operand operators (see spot::ltl::multop).
#ifndef SPOT_LTLAST_BINOP_HH
# define SPOT_LTLAST_BINOP_HH

#include <map>
#include <iosfwd>
#include "refformula.hh"

namespace spot
{
  namespace ltl
  {

    /// \brief Binary operator.
    /// \ingroup ltl_ast
    class binop : public ref_formula
    {
    public:
      /// Different kinds of binary opertaors
      ///
      /// And and Or are not here.  Because they
      /// are often nested we represent them as multops.
      enum type { Xor,
		  Implies,
		  Equiv,
		  U, //< until
		  R, //< release (dual of until)
		  W, //< weak until
		  M,  //< strong release (dual of weak until)
		  EConcat, // Existential Concatenation
		  EConcatMarked, // Existential Concatenation, Marked
		  UConcat // Universal Concatenation
      };

      /// \brief Build a unary operator with operation \a op and
      /// children \a first and \a second.
      ///
      /// Some reordering will be performed on arguments of commutative
      /// operators (Xor and Equiv) to ensure that for instance (a <=> b)
      /// is the same formula as (b <=> a).
      ///
      /// Furthermore, the following trivial simplifications are
      /// performed (the left formula is rewritten as the right
      /// formula):
      ///   - (1 => Exp) = Exp
      ///   - (0 => Exp) = 1
      ///   - (Exp => 1) = 1
      ///   - (Exp => 0) = !Exp
      ///   - (Exp => Exp) = 1
      ///   - (1 ^ Exp) = !Exp
      ///   - (0 ^ Exp) = Exp
      ///   - (Exp ^ Exp) = 0
      ///   - (0 <=> Exp) = !Exp
      ///   - (1 <=> Exp) = Exp
      ///   - (Exp <=> Exp) = Exp
      ///   - (Exp U 1) = 1
      ///   - (Exp U 0) = 0
      ///   - (0 U Exp) = Exp
      ///   - (Exp U Exp) = Exp
      ///   - (Exp W 1) = 1
      ///   - (0 W Exp) = Exp
      ///   - (1 W Exp) = 1
      ///   - (Exp W Exp) = Exp
      ///   - (Exp R 1) = 1
      ///   - (Exp R 0) = 0
      ///   - (1 R Exp) = Exp
      ///   - (Exp R Exp) = Exp
      ///   - (Exp M 0) = 0
      ///   - (1 M Exp) = Exp
      ///   - (0 M Exp) = 0
      ///   - (Exp M Exp) = Exp
      ///   - 0 <>-> Exp = 0
      ///   - 1 <>-> Exp = Exp
      ///   - [*0] <>-> Exp = 0
      ///   - Exp <>-> 0 = 0
      ///   - boolExp <>-> Exp = boolExp & Exp
      ///   - 0 []-> Exp = 1
      ///   - 1 []-> Exp = Exp
      ///   - [*0] []-> Exp = 1
      ///   - Exp []-> 1 = 1
      ///   - boolExp <>-> Exp = !boolExp | Exp
      static const formula* instance(type op,
				     const formula* first,
				     const formula* second);

      virtual void accept(visitor& v) const;

      /// Get the first operand.
      const formula* first() const;
      /// Get the second operand.
      const formula* second() const;

      /// Get the type of this operator.
      type op() const;
      /// Get the type of this operator, as a string.
      const char* op_name() const;

      /// Return a canonic representation of the atomic proposition
      virtual std::string dump() const;

      /// Number of instantiated binary operators.  For debugging.
      static unsigned instance_count();

      /// Dump all instances.  For debugging.
      static std::ostream& dump_instances(std::ostream& os);

    protected:
      typedef std::pair<const formula*, const formula*> pairf;
      typedef std::pair<type, pairf> pair;
      typedef std::map<pair, const binop*> map;
      static map instances;

      binop(type op, const formula* first, const formula* second);
      virtual ~binop();

    private:
      type op_;
      const formula* first_;
      const formula* second_;
    };

    /// \brief Cast \a f into a binop
    ///
    /// Cast \a f into a binop iff it is a binop instance.  Return 0
    /// otherwise.  This is faster than \c dynamic_cast.
    inline
    const binop*
    is_binop(const formula* f)
    {
      if (f->kind() != formula::BinOp)
	return 0;
      return static_cast<const binop*>(f);
    }

    /// \brief Cast \a f into a binop if it has type \a op.
    ///
    /// Cast \a f into a binop iff it is a unop instance with operator \a op.
    /// Returns 0 otherwise.
    inline
    const binop*
    is_binop(const formula* f, binop::type op)
    {
      if (const binop* bo = is_binop(f))
	if (bo->op() == op)
	  return bo;
      return 0;
    }

    /// \brief Cast \a f into a binop if it has type \a op1 or \a op2.
    ///
    /// Cast \a f into a binop iff it is a unop instance with operator \a op1 or
    /// \a op2.  Returns 0 otherwise.
    inline
    const binop*
    is_binop(const formula* f, binop::type op1, binop::type op2)
    {
      if (const binop* bo = is_binop(f))
	if (bo->op() == op1 || bo->op() == op2)
	  return bo;
      return 0;
    }

    /// \brief Cast \a f into a binop if it is a U.
    ///
    /// Return 0 otherwise.
    inline
    const binop*
    is_U(const formula* f)
    {
      return is_binop(f, binop::U);
    }

    /// \brief Cast \a f into a binop if it is a M.
    ///
    /// Return 0 otherwise.
    inline
    const binop*
    is_M(const formula* f)
    {
      return is_binop(f, binop::M);
    }

    /// \brief Cast \a f into a binop if it is a R.
    ///
    /// Return 0 otherwise.
    inline
    const binop*
    is_R(const formula* f)
    {
      return is_binop(f, binop::R);
    }

    /// \brief Cast \a f into a binop if it is a W.
    ///
    /// Return 0 otherwise.
    inline
    const binop*
    is_W(const formula* f)
    {
      return is_binop(f, binop::W);
    }
  }
}

#endif // SPOT_LTLAST_BINOP_HH
