// -*- coding: utf-8 -*-
// Copyright (C) 2009, 2010, 2011, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
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

#include "unop.hh"
#include "visitor.hh"
#include <cassert>
#include <iostream>
#include "constant.hh"
#include "atomic_prop.hh"

namespace spot
{
  namespace ltl
  {
    unop::unop(type op, const formula* child)
      : ref_formula(UnOp), op_(op), child_(child)
    {
      props = child->get_props();
      switch (op)
	{
	case Not:
	  is.not_marked = true;
	  is.eventual = child->is_universal();
	  is.universal = child->is_eventual();
	  is.in_nenoform = (child->kind() == AtomicProp);
	  is.sere_formula = is.boolean;

	  is.syntactic_safety = child->is_syntactic_guarantee();
	  is.syntactic_guarantee = child->is_syntactic_safety();
	  // is.syntactic_obligation inherited from child
	  is.syntactic_recurrence = child->is_syntactic_persistence();
	  is.syntactic_persistence = child->is_syntactic_recurrence();

	  is.accepting_eword = false;
	  break;
	case X:
	  is.not_marked = true;
	  is.boolean = false;
	  is.X_free = false;
	  is.eltl_formula = false;
	  is.sere_formula = false;
	  // is.syntactic_safety inherited
	  // is.syntactic_guarantee inherited
	  // is.syntactic_obligation inherited
	  // is.syntactic_recurrence inherited
	  // is.syntactic_persistence inherited
	  is.accepting_eword = false;
	  break;
	case F:
	  is.not_marked = true;
	  is.boolean = false;
	  is.eltl_formula = false;
	  is.sere_formula = false;
	  is.finite = false;
	  is.sugar_free_ltl = false;
	  is.eventual = true;
	  is.syntactic_safety = false;
	  // is.syntactic_guarantee inherited
	  is.syntactic_obligation = is.syntactic_guarantee;
	  is.syntactic_recurrence = is.syntactic_guarantee;
	  // is.syntactic_persistence inherited
	  is.accepting_eword = false;
	  break;
	case G:
	  is.not_marked = true;
	  is.boolean = false;
	  is.eltl_formula = false;
	  is.sere_formula = false;
	  is.finite = false;
	  is.sugar_free_ltl = false;
	  is.universal = true;
	  // is.syntactic_safety inherited
	  is.syntactic_guarantee = false;
	  is.syntactic_obligation = is.syntactic_safety;
	  // is.syntactic_recurrence inherited
	  is.syntactic_persistence = is.syntactic_safety;
	  is.accepting_eword = false;
	  break;
	case Finish:
	  is.not_marked = true;
	  is.boolean = false;
	  is.ltl_formula = false;
	  is.psl_formula = false;
	  is.sere_formula = false;
	  is.finite = false;
	  is.syntactic_safety = false;
	  is.syntactic_guarantee = false;
	  is.syntactic_obligation = false;
	  is.syntactic_recurrence = false;
	  is.syntactic_persistence = false;
	  is.accepting_eword = false;
	  break;
	case NegClosure:
	case NegClosureMarked:
	  is.not_marked = (op == NegClosure);
	  is.boolean = false;
	  is.ltl_formula = false;
	  is.eltl_formula = false;
	  is.psl_formula = true;
	  is.sere_formula = false;
	  is.syntactic_safety = is.finite;
	  is.syntactic_guarantee = true;
	  is.syntactic_obligation = true;
	  is.syntactic_recurrence = true;
	  is.syntactic_persistence = true;
	  is.accepting_eword = false;
	  break;
	case Closure:
	  is.not_marked = true;
	  is.boolean = false;
	  is.ltl_formula = false;
	  is.eltl_formula = false;
	  is.psl_formula = true;
	  is.sere_formula = false;
	  is.syntactic_safety = true;
	  is.syntactic_guarantee = is.finite;
	  is.syntactic_obligation = true;
	  is.syntactic_recurrence = true;
	  is.syntactic_persistence = true;
	  is.accepting_eword = false;
	  assert(child->is_sere_formula());
	  break;
	}
    }

    unop::~unop()
    {
      // Get this instance out of the instance map.
      pair p(op(), child());
      map::iterator i = instances.find(p);
      assert (i != instances.end());
      instances.erase(i);

      // Dereference child.
      child()->destroy();
    }

    std::string
    unop::dump() const
    {
      return std::string("unop(") + op_name() + ", " + child()->dump() + ")";
    }

    void
    unop::accept(visitor& v) const
    {
      v.visit(this);
    }

    const formula*
    unop::child() const
    {
      return child_;
    }

    unop::type
    unop::op() const
    {
      return op_;
    }

    const char*
    unop::op_name() const
    {
      switch (op_)
	{
	case Not:
	  return "Not";
	case X:
	  return "X";
	case F:
	  return "F";
	case G:
	  return "G";
	case Finish:
	  return "Finish";
	case Closure:
	  return "Closure";
	case NegClosure:
	  return "NegClosure";
	case NegClosureMarked:
	  return "NegClosureMarked";
	}
      // Unreachable code.
      assert(0);
      return 0;
    }

    unop::map unop::instances;

    const formula*
    unop::instance(type op, const formula* child)
    {

      // Some trivial simplifications.
      switch (op)
	{
	case F:
	case G:
	  {
	    if (const unop* u = is_unop(child))
	      {
		// F and G are idempotent.
		if (u->op() == op)
		  return u;
	      }

	    // F(0) = G(0) = 0
	    // F(1) = G(1) = 1
	    if (child == constant::false_instance()
		|| child == constant::true_instance())
	      return child;

	    assert(child != constant::empty_word_instance());
	  }
	  break;

	case Not:
	  {
	    // !1 = 0
	    if (child == constant::true_instance())
	      return constant::false_instance();
	    // !0 = 1
	    if (child == constant::false_instance())
	      return constant::true_instance();
	    // ![*0] = 1[+]
	    if (child == constant::empty_word_instance())
	      return bunop::instance(bunop::Star,
				     constant::true_instance(), 1);

	    if (const unop* u = is_unop(child))
	      {
		// "Not" is an involution.
		if (u->op() == op)
		  {
		    const formula* c = u->child()->clone();
		    u->destroy();
		    return c;
		  }
		// !Closure(Exp) = NegClosure(Exp)
		if (u->op() == Closure)
		  {
		    const formula* c = unop::instance(NegClosure,
						      u->child()->clone());
		    u->destroy();
		    return c;
		  }
		// !NegClosure(Exp) = Closure(Exp)
		if (u->op() == NegClosure)
		  {
		    const formula* c = unop::instance(Closure,
						      u->child()->clone());
		    u->destroy();
		    return c;
		  }
	      }
	    break;
	  }

	case X:
	  // X(1) = 1,  X(0) = 0
	  if (child == constant::true_instance()
	      || child == constant::false_instance())
	    return child;
	  assert(child != constant::empty_word_instance());
	  break;

	case Finish:
	  // No simplifications for Finish.
	  break;

	case Closure:
	  // {0} = 0, {1} = 1,  {b} = b
	  if (child->is_boolean())
	    return child;
	  // {[*0]} = 1
	  if (child == constant::empty_word_instance())
	    return constant::true_instance();
	  break;

	case NegClosure:
	case NegClosureMarked:
	  // {1} = 0,  {[*0]} = 0
	  if (child == constant::true_instance()
	      || child == constant::empty_word_instance())
	    return constant::false_instance();
	  // {0} = 1
	  if (child == constant::false_instance())
	    return constant::true_instance();
	  // {b} = !b
	  if (child->is_boolean())
	    return unop::instance(Not, child);
	  break;
	}

      const unop* res;
      pair p(op, child);
      map::iterator i = instances.find(p);
      if (i != instances.end())
	{
	  // This instance already exists.
	  child->destroy();
	  res = i->second;
	}
      else
	{
	  res = instances[p] = new unop(op, child);
	}
      res->clone();
      return res;
    }

    unsigned
    unop::instance_count()
    {
      return instances.size();
    }

    std::ostream&
    unop::dump_instances(std::ostream& os)
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
