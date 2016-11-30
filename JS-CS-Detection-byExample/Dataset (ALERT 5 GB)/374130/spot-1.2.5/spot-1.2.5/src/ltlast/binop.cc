// -*- coding: utf-8 -*-
// Copyright (C) 2009, 2010, 2011, 2012, 2013, 2014 Laboratoire de
// Recherche et Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2005 Laboratoire d'Informatique de Paris
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

#include "config.h"
#include <cassert>
#include <utility>
#include "binop.hh"
#include "unop.hh"
#include "multop.hh"
#include "constant.hh"
#include "visitor.hh"
#include <iostream>

namespace spot
{
  namespace ltl
  {
    binop::binop(type op, const formula* first, const formula* second)
      : ref_formula(BinOp), op_(op), first_(first), second_(second)
    {
      // Beware: (f U g) is a pure eventuality if both operands
      // are pure eventualities, unlike in the proceedings of
      // Concur'00.  (The revision of the paper available at
      // http://www.bell-labs.com/project/TMP/ is fixed.)  See
      // also http://arxiv.org/abs/1011.4214v2 for a discussion
      // about this problem.  (Which we fixed in 2005 thanks
      // to LBTT.)
      // This means that we can use the following line to handle
      // all cases of (f U g), (f R g), (f W g), (f M g) for
      // universality and eventuality.
      props = first->get_props() & second->get_props();
      // The matter can be further refined because:
      //  (f U g) is a pure eventuality if
      //                g is a pure eventuality (regardless of f),
      //             or f == 1
      //  (g M f) is a pure eventuality if f and g are,
      //                                or f == 1
      //  (g R f) is purely universal if
      //                f is purely universal (regardless of g)
      //               or g == 0
      //  (f W g) is purely universal if f and g are
      //                              or g == 0
      switch (op)
	{
	case Xor:
	case Equiv:
	  is.eventual = false;
	  is.universal = false;
	  is.sere_formula = is.boolean;
	  is.sugar_free_boolean = false;
	  is.in_nenoform = false;
	  // is.syntactic_obligation inherited;
	  is.accepting_eword = false;
	  if (is.syntactic_obligation)
	    {
	      // Only formula that are in the intersection of
	      // guarantee and safety are closed by Xor and <=>.
	      bool sg = is.syntactic_safety && is.syntactic_guarantee;
	      is.syntactic_safety = sg;
	      is.syntactic_guarantee = sg;
	      assert(is.syntactic_recurrence == true);
	      assert(is.syntactic_persistence == true);
	    }
	  else
	    {
	      is.syntactic_safety = false;
	      is.syntactic_guarantee = false;
	      is.syntactic_recurrence = false;
	      is.syntactic_persistence = false;
	    }
	  break;
	case Implies:
	  is.eventual = false;
	  is.universal = false;
	  is.sere_formula = is.boolean;
	  is.sugar_free_boolean = false;
	  is.in_nenoform = false;
	  is.syntactic_safety =
	    first->is_syntactic_guarantee() && second->is_syntactic_safety();
	  is.syntactic_guarantee =
	    first->is_syntactic_safety() && second->is_syntactic_guarantee();
	  // is.syntactic_obligation inherited
	  is.syntactic_persistence = first->is_syntactic_recurrence()
	    && second->is_syntactic_persistence();
	  is.syntactic_recurrence = first->is_syntactic_persistence()
	    && second->is_syntactic_recurrence();
	  is.accepting_eword = false;
	  break;
	case EConcatMarked:
	case EConcat:
	  is.not_marked = (op != EConcatMarked);
	  is.ltl_formula = false;
	  is.boolean = false;
	  is.eltl_formula = false;
	  is.sere_formula = false;
	  is.accepting_eword = false;
	  is.psl_formula = true;

	  is.syntactic_guarantee = second->is_syntactic_guarantee();
	  is.syntactic_persistence = second->is_syntactic_persistence();
	  if (first->is_finite())
	    {
	      is.syntactic_safety = second->is_syntactic_safety();
	      is.syntactic_obligation = second->is_syntactic_obligation();
	      is.syntactic_recurrence = second->is_syntactic_recurrence();
	    }
	  else
	    {
	      is.syntactic_safety = false;
	      is.syntactic_obligation = second->is_syntactic_guarantee();
	      is.syntactic_recurrence = second->is_syntactic_guarantee();
	    }
	  assert(first->is_sere_formula());
	  assert(second->is_psl_formula());
	  break;
	case UConcat:
	  is.not_marked = true;
	  is.ltl_formula = false;
	  is.boolean = false;
	  is.eltl_formula = false;
	  is.sere_formula = false;
	  is.accepting_eword = false;
	  is.psl_formula = true;

	  is.syntactic_safety = second->is_syntactic_safety();
	  is.syntactic_recurrence = second->is_syntactic_recurrence();
	  if (first->is_finite())
	    {
	      is.syntactic_guarantee = second->is_syntactic_guarantee();
	      is.syntactic_obligation = second->is_syntactic_obligation();
	      is.syntactic_persistence = second->is_syntactic_persistence();
	    }
	  else
	    {
	      is.syntactic_guarantee = false;
	      is.syntactic_obligation = second->is_syntactic_safety();
	      is.syntactic_persistence = second->is_syntactic_safety();
	    }
	  assert(first->is_sere_formula());
	  assert(second->is_psl_formula());
	  break;
	case U:
	  is.not_marked = true;
	  // f U g is universal if g is eventual, or if f == 1.
	  is.eventual = second->is_eventual();
	  is.eventual |= (first == constant::true_instance());
	  is.boolean = false;
	  is.eltl_formula = false;
	  is.sere_formula = false;
	  is.finite = false;
	  is.accepting_eword = false;

	  is.syntactic_safety = false;
	  // is.syntactic_guarantee = Guarantee U Guarantee
	  is.syntactic_obligation = // Obligation U Guarantee
	    first->is_syntactic_obligation()
	    && second->is_syntactic_guarantee();
	  is.syntactic_recurrence = // Recurrence U Guarantee
	    first->is_syntactic_recurrence()
	    && second->is_syntactic_guarantee();
	  // is.syntactic_persistence = Persistence U Persistance
	  break;
	case W:
	  is.not_marked = true;
	  // f W g is universal if f and g are, or if g == 0.
	  is.universal |= (second == constant::false_instance());
	  is.boolean = false;
	  is.eltl_formula = false;
	  is.sere_formula = false;
	  is.finite = false;
	  is.accepting_eword = false;

	  // is.syntactic_safety = Safety W Safety;
	  is.syntactic_guarantee = false;
	  is.syntactic_obligation = // Safety W Obligation
	    first->is_syntactic_safety() && second->is_syntactic_obligation();
	  // is.syntactic_recurrence = Recurrence W Recurrence
	  is.syntactic_persistence = // Safety W Persistance
	    first->is_syntactic_safety()
	    && second->is_syntactic_persistence();

	  break;
	case R:
	  is.not_marked = true;
	  // g R f is universal if f is universal, or if g == 0.
	  is.universal = second->is_universal();
	  is.universal |= (first == constant::false_instance());
	  is.boolean = false;
	  is.eltl_formula = false;
	  is.sere_formula = false;
	  is.finite = false;
	  is.accepting_eword = false;

	  // is.syntactic_safety = Safety R Safety;
	  is.syntactic_guarantee = false;
	  is.syntactic_obligation = // Obligation R Safety
	    first->is_syntactic_obligation() && second->is_syntactic_safety();
	  //is.syntactic_recurrence = Recurrence R Recurrence
	  is.syntactic_persistence = // Persistence R Safety
	    first->is_syntactic_persistence()
	    && second->is_syntactic_safety();

	  break;
	case M:
	  is.not_marked = true;
	  // g M f is eventual if both g and f are eventual, or if f == 1.
	  is.eventual |= (second == constant::true_instance());
	  is.boolean = false;
	  is.eltl_formula = false;
	  is.sere_formula = false;
	  is.finite = false;
	  is.accepting_eword = false;

	  is.syntactic_safety = false;
	  // is.syntactic_guarantee = Guarantee M Guarantee
	  is.syntactic_obligation = // Guarantee M Obligation
	    first->is_syntactic_guarantee()
	    && second->is_syntactic_obligation();
	  is.syntactic_recurrence = // Guarantee M Recurrence
	    first->is_syntactic_guarantee()
	    && second->is_syntactic_recurrence();
	  // is.syntactic_persistence = Persistence M Persistance

	  break;
	}

      assert((!is.syntactic_obligation) ||
	     (is.syntactic_persistence && is.syntactic_recurrence));
    }

    binop::~binop()
    {
      // Get this instance out of the instance map.
      pairf pf(first(), second());
      pair p(op(), pf);
      map::iterator i = instances.find(p);
      assert (i != instances.end());
      instances.erase(i);

      // Dereference children.
      first()->destroy();
      second()->destroy();
    }

    std::string
    binop::dump() const
    {
      return (std::string("binop(") + op_name()
	      + ", " + first()->dump()
	      + ", " + second()->dump() + ")");
    }

    void
    binop::accept(visitor& v) const
    {
      v.visit(this);
    }

    const char*
    binop::op_name() const
    {
      switch (op_)
	{
	case Xor:
	  return "Xor";
	case Implies:
	  return "Implies";
	case Equiv:
	  return "Equiv";
	case U:
	  return "U";
	case R:
	  return "R";
	case W:
	  return "W";
	case M:
	  return "M";
	case EConcat:
	  return "EConcat";
	case EConcatMarked:
	  return "EConcatMarked";
	case UConcat:
	  return "UConcat";
	}
      // Unreachable code.
      assert(0);
      return 0;
    }

    binop::map binop::instances;

    const formula*
    binop::instance(type op, const formula* first, const formula* second)
    {
      // Sort the operands of commutative operators, so that for
      // example the formula instance for 'a xor b' is the same as
      // that for 'b xor a'.

      // Trivial identities:
      switch (op)
	{
	case Xor:
	  {
	    // Xor is commutative: sort operands.
	    formula_ptr_less_than_bool_first cmp;
	    if (cmp(second, first))
	      std::swap(second, first);
	  }
	  //   - (1 ^ Exp) = !Exp
	  //   - (0 ^ Exp) = Exp
	  if (first == constant::true_instance())
	    return unop::instance(unop::Not, second);
	  if (first == constant::false_instance())
	    return second;
	  if (first == second)
	    {
	      first->destroy();
	      second->destroy();
	      return constant::false_instance();
	    }
	  // We expect constants to appear first, because they are
	  // instantiated first.
	  assert(second != constant::false_instance());
	  assert(second != constant::true_instance());
	  break;
	case Equiv:
	  {
	    // Equiv is commutative: sort operands.
	    formula_ptr_less_than_bool_first cmp;
	    if (cmp(second, first))
	      std::swap(second, first);
	  }
	  //   - (0 <=> Exp) = !Exp
	  //   - (1 <=> Exp) = Exp
	  //   - (Exp <=> Exp) = 1
	  if (first == constant::false_instance())
	    return unop::instance(unop::Not, second);
	  if (first == constant::true_instance())
	    return second;
	  if (first == second)
	    {
	      first->destroy();
	      second->destroy();
	      return constant::true_instance();
	    }
	  // We expect constants to appear first, because they are
	  // instantiated first.
	  assert(second != constant::false_instance());
	  assert(second != constant::true_instance());
	  break;
	case Implies:
	  //   - (1 => Exp) = Exp
	  //   - (0 => Exp) = 1
	  //   - (Exp => 1) = 1
	  //   - (Exp => 0) = !Exp
	  //   - (Exp => Exp) = 1
	  if (first == constant::true_instance())
	    return second;
	  if (first == constant::false_instance())
	    {
	      second->destroy();
	      return constant::true_instance();
	    }
	  if (second == constant::true_instance())
	    {
	      first->destroy();
	      return second;
	    }
	  if (second == constant::false_instance())
	    return unop::instance(unop::Not, first);
	  if (first == second)
	    {
	      first->destroy();
	      second->destroy();
	      return constant::true_instance();
	    }
	  break;
	case U:
	  //   - (Exp U 1) = 1
	  //   - (Exp U 0) = 0
	  //   - (0 U Exp) = Exp
	  //   - (Exp U Exp) = Exp
	  if (second == constant::true_instance()
	      || second == constant::false_instance()
	      || first == constant::false_instance()
	      || first == second)
	    {
	      first->destroy();
	      return second;
	    }
	  break;
	case W:
	  //   - (Exp W 1) = 1
	  //   - (0 W Exp) = Exp
	  //   - (1 W Exp) = 1
	  //   - (Exp W Exp) = Exp
	  if (second == constant::true_instance()
	      || first == constant::false_instance()
	      || first == second)
	    {
	      first->destroy();
	      return second;
	    }
	  if (first == constant::true_instance())
	    {
	      second->destroy();
	      return first;
	    }
	  break;
	case R:
	  //   - (Exp R 1) = 1
	  //   - (Exp R 0) = 0
	  //   - (1 R Exp) = Exp
	  //   - (Exp R Exp) = Exp
	  if (second == constant::true_instance()
	      || second == constant::false_instance()
	      || first == constant::true_instance()
	      || first == second)
	    {
	      first->destroy();
	      return second;
	    }
	  break;
	case M:
	  //   - (Exp M 0) = 0
	  //   - (1 M Exp) = Exp
	  //   - (0 M Exp) = 0
	  //   - (Exp M Exp) = Exp
	  if (second == constant::false_instance()
	      || first == constant::true_instance()
	      || first == second)
	    {
	      first->destroy();
	      return second;
	    }
	  if (first == constant::false_instance())
	    {
	      second->destroy();
	      return first;
	    }
	  break;
	case EConcat:
	case EConcatMarked:
	  //   - 0 <>-> Exp = 0
	  //   - 1 <>-> Exp = Exp
	  //   - [*0] <>-> Exp = 0
	  //   - Exp <>-> 0 = 0
	  //   - boolExp <>-> Exp = boolExp & Exp
	  if (first == constant::true_instance())
	    return second;
	  if (first == constant::false_instance()
	      || first == constant::empty_word_instance())
	    {
	      second->destroy();
	      return constant::false_instance();
	    }
	  if (second == constant::false_instance())
	    {
	      first->destroy();
	      return second;
	    }
	  if (first->is_boolean())
	    return multop::instance(multop::And, first, second);
	  break;
	case UConcat:
	  //   - 0 []-> Exp = 1
	  //   - 1 []-> Exp = Exp
	  //   - [*0] []-> Exp = 1
	  //   - Exp []-> 1 = 1
	  //   - boolExp []-> Exp = !boolExp | Exp
	  if (first == constant::true_instance())
	    return second;
	  if (first == constant::false_instance()
	      || first == constant::empty_word_instance())
	    {
	      second->destroy();
	      return constant::true_instance();
	    }
	  if (second == constant::true_instance())
	    {
	      first->destroy();
	      return second;
	    }
	  if (first->is_boolean())
	    return multop::instance(multop::Or,
				    unop::instance(unop::Not, first), second);
	  break;
	}

      pairf pf(first, second);
      pair p(op, pf);

      const binop* res;
      std::pair<map::iterator, bool> ires =
	instances.insert(map::value_type(p, 0));
      if (!ires.second)
	{
	  // This instance already exists.
	  first->destroy();
	  second->destroy();
	  res = ires.first->second;
	}
      else
	{
	  res = ires.first->second = new binop(op, first, second);
	}
      res->clone();
      return res;
    }

    unsigned
    binop::instance_count()
    {
      return instances.size();
    }

    std::ostream&
    binop::dump_instances(std::ostream& os)
    {
      for (map::iterator i = instances.begin(); i != instances.end(); ++i)
	{
	  os << i->second << " = "
	     << i->second->ref_count_() << " * "
	     << i->second->dump()
	     << std::endl;
	}
      return os;

    }


  }
}
