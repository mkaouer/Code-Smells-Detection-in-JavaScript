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

/// \file ltlast/multop.hh
/// \brief LTL multi-operand operators
#ifndef SPOT_LTLAST_MULTOP_HH
# define SPOT_LTLAST_MULTOP_HH

#include <vector>
#include <map>
#include <iosfwd>
#include "refformula.hh"

namespace spot
{
  namespace ltl
  {

    /// \brief Multi-operand operators.
    /// \ingroup ltl_ast
    class multop : public ref_formula
    {
    public:
      enum type { Or, OrRat, And, AndRat, AndNLM, Concat, Fusion };

      /// List of formulae.
      typedef std::vector<const formula*> vec;

      /// \brief Build a spot::ltl::multop with two children.
      ///
      /// If one of the children itself is a spot::ltl::multop
      /// with the same type, it will be inlined.  I.e., children
      /// of that child will be added, and that child itself will
      /// be destroyed.  This allows incremental building of
      /// n-ary ltl::multop.
      ///
      /// This functions can perform slight optimizations and
      /// may not return an ltl::multop object. See the other
      /// instance function for the list of rewritings.
      static const formula*
      instance(type op, const formula* first, const formula* second);

      /// \brief Build a spot::ltl::multop with many children.
      ///
      /// Same as the other instance() function, but take a vector of
      /// formulae as argument.  This vector is acquired by the
      /// spot::ltl::multop class, the caller should allocate it with
      /// \c new, but not use it (especially not destroy it) after it
      /// has been passed to spot::ltl::multop.  Inside the vector,
      /// null pointers are ignored.
      ///
      /// Most operators (Or, OrRat, And, AndRat, Concat) are
      /// associative, and are automatically inlined.  Or, OrRat, And,
      /// and AndRat are commutative, so their arguments are also
      /// sorted, to ensure that "a & b" is equal to "b & a", also
      /// duplicate arguments are removed.
      ///
      /// Furthermore this function can perform slight optimizations
      /// and may not return an ltl::multop object.  For instance if
      /// the vector contains only one unique element, this this
      /// formula will be returned as-is.  Neutral and absorbent element
      /// are also taken care of.  The following rewritings are performed
      /// (the left patterns are rewritten as shown on the right):
      ///
      /// - And(Exps1...,1,Exps2...) = And(Exps1...,Exps2...)
      /// - And(Exps1...,0,Exps2...) = 0
      /// - And(Exp) = Exp
      /// - Or(Exps1...,1,Exps2...) = 1
      /// - Or(Exps1...,0,Exps2...) = Or(Exps1...,Exps2...)
      /// - Or(Exp) = Exp
      /// - AndNLM(FExps1...,1,Exps2...) = AndNLM(Exps2...)
      ///     if Fexps1... accept [*0], and Exps2... don't.
      /// - AndNLM(FExps1...,1,FExps2...) = 1
      ///     if Fexps1...,FExps2... all accept[*0].
      /// - AndNLM(Exps1...,0,Exps2...) = 0
      /// - AndNLM(Exps1...,[*0],Exps2...) = AndNLM(Exps1...,Exps2...)
      /// - AndNLM(Exp) = Exp
      /// - AndNLM(Exps1...,BoolExp1,Exps2...,BoolExp2,Exps3...) =
      ///    AndNLM(Exps1...,Exps2...,Exps3...,And(BoolExp1,BoolExp2))
      /// - AndRat(Exps1...,0,Exps2...) = 0
      /// - AndRat(Exps1...,BoolExp1,Exps2...,BoolExps2...) =
      ///    AndRat(Exps1...,Exps2...,And(BoolExp1,BoolExps2...))
      /// - AndRat(Exps1...,[*0],Exps2...) = [*0] if all Expi accept [*0]
      /// - AndRat(Exps1...,[*0],Exps2...) = 0 if some Expi reject [*0]
      /// - AndRat(Exps1...,1[*],Exps2...) = AndRat(Exps1...,Exps2...)
      /// - OrRat(Exps1...,0,Exps2...) = OrRat(Exps1...,Exps2...)
      /// - OrRat(Exps1...,BoolExp1,Exps2...,BoolExps2...) =
      ///    OrRat(Exps1...,Exps2...,Or(BoolExp1,BoolExps2...))
      /// - OrRat(Exps1...,1[*],Exps2...) = 1[*]
      /// - Concat(Exps1...,0,Exps2...) = 0
      /// - Concat(Exps1...,[*0],Exps2...) = Concat(Exps1...,Exps2...)
      /// - Concat(Exps1...,FExps2...,1[*],FExps3...,Exps4) =
      ///     Concat(Exps1...,1[*],Exps4) if FExps2...FExps3... all accept [*0]
      /// - Concat(Exp) = Exp
      /// - Concat(Exps1...,E,E[*i..j],E[*k..l],Exps2...) =
      ///     Concat(Exps1...,E[*1+i+k..j+l],Exps2...)  and similar forms
      /// - Fusion(Exps1...1,Exps2...) = Fusion(Exps1...,Exps2...)
      ///     if at least one exp reject [*0]
      /// - Fusion(Exps1...,0,Exps2...) = 0
      /// - Fusion(Exps1...,[*0],Exps2...) = 0
      /// - Fusion(Exp) = Exp
      /// - Fusion(Exps1...,BoolExp1...BoolExpN,Exps2,Exps3...) =
      ///     Fusion(Exps1...,And(BoolExp1...BoolExpN),Exps2,Exps3...)
      static const formula* instance(type op, vec* v);

      virtual void accept(visitor& v) const;

      /// Get the number of children.
      unsigned size() const;
      /// \brief Get the nth child.
      ///
      /// Starting with \a n = 0.
      const formula* nth(unsigned n) const;

      /// \brief construct a formula without the nth child.
      ///
      /// If the formula \c f is <code>a|b|c|d</code> and <code>d</code>
      /// is child number 2, then calling <code>f->all_but(2)</code> will
      /// return a new formula <code>a|b|d</code>.
      const formula* all_but(unsigned n) const;

      /// Get the type of this operator.
      type op() const;
      /// Get the type of this operator, as a string.
      const char* op_name() const;

      /// Return a canonic representation of the atomic proposition
      virtual std::string dump() const;

      /// Number of instantiated multi-operand operators.  For debugging.
      static unsigned instance_count();

      /// Dump all instances.  For debugging.
      static std::ostream& dump_instances(std::ostream& os);

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
      typedef std::map<pair, const multop*, paircmp> map;
      static map instances;

      multop(type op, vec* v);
      virtual ~multop();

    private:
      type op_;
      vec* children_;
    };


    /// \brief Cast \a f into a multop.
    ///
    /// Cast \a f into a multop iff it is a multop instance.  Return 0
    /// otherwise.  This is faster than \c dynamic_cast.
    inline
    const multop*
    is_multop(const formula* f)
    {
      if (f->kind() != formula::MultOp)
	return 0;
      return static_cast<const multop*>(f);
    }

    /// \brief Cast \a f into a multop if it has type \a op.
    ///
    /// Cast \a f into a multop iff it is a multop instance with operator \a op.
    /// Returns 0 otherwise.
    inline
    const multop*
    is_multop(const formula* f, multop::type op)
    {
      if (const multop* mo = is_multop(f))
	if (mo->op() == op)
	  return mo;
      return 0;
    }

    /// \brief Cast \a f into a multop if it has type \a op1 or \a op2.
    ///
    /// Cast \a f into a multop iff it is a multop instance with
    /// operator \a op1 or \a op2.  Returns 0 otherwise.
    inline
    const multop*
    is_multop(const formula* f, multop::type op1, multop::type op2)
    {
      if (const multop* mo = is_multop(f))
	if (mo->op() == op1 || mo->op() == op2)
	  return mo;
      return 0;
    }

    /// \brief Cast \a f into a multop if it is an And.
    ///
    /// Return 0 otherwise.
    inline
    const multop*
    is_And(const formula* f)
    {
      return is_multop(f, multop::And);
    }

    /// \brief Cast \a f into a multop if it is an AndRat.
    ///
    /// Return 0 otherwise.
    inline
    const multop*
    is_AndRat(const formula* f)
    {
      return is_multop(f, multop::AndRat);
    }

    /// \brief Cast \a f into a multop if it is an AndNLM.
    ///
    /// Return 0 otherwise.
    inline
    const multop*
    is_AndNLM(const formula* f)
    {
      return is_multop(f, multop::AndNLM);
    }

    /// \brief Cast \a f into a multop if it is an Or.
    ///
    /// Return 0 otherwise.
    inline
    const multop*
    is_Or(const formula* f)
    {
      return is_multop(f, multop::Or);
    }

    /// \brief Cast \a f into a multop if it is an OrRat.
    ///
    /// Return 0 otherwise.
    inline
    const multop*
    is_OrRat(const formula* f)
    {
      return is_multop(f, multop::OrRat);
    }

    /// \brief Cast \a f into a multop if it is a Concat.
    ///
    /// Return 0 otherwise.
    inline
    const multop*
    is_Concat(const formula* f)
    {
      return is_multop(f, multop::Concat);
    }

    /// \brief Cast \a f into a multop if it is a Fusion.
    ///
    /// Return 0 otherwise.
    inline
    const multop*
    is_Fusion(const formula* f)
    {
      return is_multop(f, multop::Fusion);
    }
  }
}

#endif // SPOT_LTLAST_MULTOP_HH
