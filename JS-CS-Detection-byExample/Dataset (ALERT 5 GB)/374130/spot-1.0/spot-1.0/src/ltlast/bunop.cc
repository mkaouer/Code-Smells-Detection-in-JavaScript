// Copyright (C) 2009, 2010, 2011, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
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

#include "bunop.hh"
#include "visitor.hh"
#include <cassert>
#include <iostream>
#include <sstream>
#include "constant.hh"
#include "unop.hh"
#include "multop.hh"

namespace spot
{
  namespace ltl
  {
    // Can't build it on startup, because it uses
    // constant::true_instance that may not have been built yet...
    const formula* bunop::one_star_ = 0;

    bunop::bunop(type op, const formula* child, unsigned min, unsigned max)
      : ref_formula(BUnOp), op_(op), child_(child), min_(min), max_(max)
    {
      props = child->get_props();

      assert(is.sere_formula);
      is.boolean = false;
      is.ltl_formula = false;
      is.eltl_formula = false;
      is.psl_formula = false;
      is.eventual = false;
      is.universal = false;
      is.syntactic_safety = false;
      is.syntactic_guarantee = false;
      is.syntactic_obligation = false;
      is.syntactic_recurrence = false;
      is.syntactic_persistence = false;

      switch (op_)
	{
	case Star:
	  if (max_ == unbounded)
	    is.finite = false;
	  if (min_ == 0)
	    is.accepting_eword = true;
	  break;
	}
    }

    bunop::~bunop()
    {
      // one_star_ should never get delete.  Otherwise, that means
      // is has been destroyed too much, or not cloned enough.
      assert(this != one_star_);

      // Get this instance out of the instance map.
      pair p(pairo(op(), child()), pairu(min_, max_));
      map::iterator i = instances.find(p);
      assert (i != instances.end());
      instances.erase(i);

      // Dereference child.
      child()->destroy();
    }

    std::string
    bunop::dump() const
    {
      std::ostringstream out;
      out << "bunop(" << op_name() << ", "
	  << child()->dump() << ", " << min_ << ", ";
      if (max_ == unbounded)
	out << "unbounded";
      else
	out << max_;
      out << ")";
      return out.str();
    }

    void
    bunop::accept(visitor& v) const
    {
      v.visit(this);
    }

    const formula*
    bunop::child() const
    {
      return child_;
    }

    unsigned
    bunop::min() const
    {
      return min_;
    }

    unsigned
    bunop::max() const
    {
      return max_;
    }

    bunop::type
    bunop::op() const
    {
      return op_;
    }

    const char*
    bunop::op_name() const
    {
      switch (op_)
	{
	case Star:
	  return "Star";
	}
      // Unreachable code.
      assert(0);
      return 0;
    }

    std::string
    bunop::format() const
    {
      std::ostringstream out;

      switch (op_)
	{
	case Star:
	  // Syntactic sugaring
	  if (min_ == 1 && max_ == unbounded)
	    {
	      out << "[+]";
	      return out.str();
	    }
	  out << "[*";
	  break;
	}

      if (min_ != 0 || max_ != unbounded)
	{
	  // Always print the min_, even when it is equal to
	  // default_min, this way we avoid ambiguities (like
	  // when reading a[*..3];b[->..2] which actually
	  // means a[*0..3];b[->1..2].
	  out << min_;
	  if (min_ != max_)
	    {
	      out << "..";
	      if (max_ != unbounded)
		out << max_;
	    }
	}
      out << "]";
      return out.str();
    }

    bunop::map bunop::instances;

    const formula*
    bunop::instance(type op, const formula* child,
		    unsigned min, unsigned max)
    {
      assert(min <= max);

      // Some trivial simplifications.

      switch (op)
	{
	case Star:
	  {
	    //   - [*0][*min..max] = [*0]
	    if (child == constant::empty_word_instance())
	      return child;

	    //   - 0[*0..max] = [*0]
	    //   - 0[*min..max] = 0 if min > 0
	    if (child == constant::false_instance())
	      {
		if (min == 0)
		  return constant::empty_word_instance();
		else
		  return child;
	      }

	    //   - Exp[*0] = [*0]
	    if (max == 0)
	      {
		child->destroy();
		return constant::empty_word_instance();
	      }

	    // - Exp[*1] = Exp
	    if (min == 1 && max == 1)
	      return child;

	    // - Exp[*i..j][*min..max] = Exp[*i(min)..j(max)]
	    //                                       if i*(min+1)<=j(min)+1.
	    if (const bunop* s = is_bunop(child))
	      {
		unsigned i = s->min();
		unsigned j = s->max();

		// Exp has to be true between i*min and j*min
		//               then between i*(min+1) and j*(min+1)
		//               ...
		//            finally between i*max and j*max
		//
		// We can merge these intervals into [i*min..j*max] iff the
		// first are adjacent or overlap, i.e. iff
		//   i*(min+1) <= j*min+1.
		// (Because i<=j, this entails that the other intervals also
		// overlap).

		const formula* exp = s->child();
		if (j == unbounded)
		  {
		    min *= i;
		    max = unbounded;

		    // Exp[*min..max]
		    exp->clone();
		    child->destroy();
		    child = exp;
		  }
		else
		  {
		    if (i * (min + 1) <= (j * min) + 1)
		      {
			min *= i;
			if (max != unbounded)
			  {
			    if (j == unbounded)
			      max = unbounded;
			    else
			      max *= j;
			  }
			exp->clone();
			child->destroy();
			child = exp;
		      }
		  }
	      }
	    break;
	  }
	}

      pair p(pairo(op, child), pairu(min, max));
      map::iterator i = instances.find(p);
      if (i != instances.end())
	{
	  // This instance already exists.
	  child->destroy();
	  return i->second->clone();
	}
      const bunop* res = instances[p] = new bunop(op, child, min, max);
      res->clone();
      return res;
    }

    const formula*
    bunop::sugar_goto(const formula* b, unsigned min, unsigned max)
    {
      assert(b->is_boolean());
      // b[->min..max] is implemented as ((!b)[*];b)[*min..max]
      const formula* s =
	bunop::instance(bunop::Star,
			unop::instance(unop::Not, b->clone()));
      return bunop::instance(bunop::Star,
			     multop::instance(multop::Concat, s, b),
			     min, max);
    }

    const formula*
    bunop::sugar_equal(const formula* b, unsigned min, unsigned max)
    {
      assert(b->is_boolean());
      // b[=0..] = 1[*]
      if (min == 0 && max == unbounded)
	{
	  b->destroy();
	  return instance(Star, constant::true_instance());
	}

      // b[=min..max] is implemented as ((!b)[*];b)[*min..max];(!b)[*]
      const formula* s =
	bunop::instance(bunop::Star,
			unop::instance(unop::Not, b->clone()));
      const formula* t =
	bunop::instance(bunop::Star,
			multop::instance(multop::Concat,
					 s->clone(), b), min, max);
      return multop::instance(multop::Concat, t, s);
    }

    unsigned
    bunop::instance_count()
    {
      // Don't count one_star_ since it should not be destroyed.
      return instances.size() - !!one_star_;
    }

    std::ostream&
    bunop::dump_instances(std::ostream& os)
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
