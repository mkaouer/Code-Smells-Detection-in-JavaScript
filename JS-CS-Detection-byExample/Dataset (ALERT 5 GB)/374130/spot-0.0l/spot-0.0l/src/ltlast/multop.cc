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

#include <cassert>
#include <utility>
#include <algorithm>
#include "multop.hh"
#include "constant.hh"
#include "visitor.hh"
#include "ltlvisit/destroy.hh"

namespace spot
{
  namespace ltl
  {
    multop::multop(type op, vec* v)
      : op_(op), children_(v)
    {
    }

    multop::~multop()
    {
      // Get this instance out of the instance map.
      pair p(op(), children_);
      map::iterator i = instances.find(p);
      assert (i != instances.end());
      instances.erase(i);

      delete children_;
    }

    void
    multop::accept(visitor& v)
    {
      v.visit(this);
    }

    void
    multop::accept(const_visitor& v) const
    {
      v.visit(this);
    }

    unsigned
    multop::size() const
    {
      return children_->size();
    }

    const formula*
    multop::nth(unsigned n) const
    {
      return (*children_)[n];
    }

    formula*
    multop::nth(unsigned n)
    {
      return (*children_)[n];
    }

    multop::type
    multop::op() const
    {
      return op_;
    }

    const char*
    multop::op_name() const
    {
      switch (op_)
	{
	case And:
	  return "And";
	case Or:
	  return "Or";
	}
      // Unreachable code.
      assert(0);
      return 0;
    }

    multop::map multop::instances;

    formula*
    multop::instance(type op, vec* v)
    {
      pair p(op, v);

      // Inline children of same kind.
      {
	vec inlined;
	vec::iterator i = v->begin();
	while (i != v->end())
	  {
	    multop* p = dynamic_cast<multop*>(*i);
	    if (p && p->op() == op)
	      {
		unsigned ps = p->size();
		for (unsigned n = 0; n < ps; ++n)
		  inlined.push_back(p->nth(n));
		// That sub-formula is now useless, drop it.
		// Note that we use unref(), not destroy(), because we've
		// adopted its children and don't want to destroy these.
		formula::unref(*i);
		i = v->erase(i);
	      }
	    else
	      {
		++i;
	      }
	  }
	v->insert(v->end(), inlined.begin(), inlined.end());
      }

      std::sort(v->begin(), v->end());

      // Remove duplicates.  We can't use std::unique(), because we
      // must destroy() any formula we drop.
      {
	formula* last = 0;
	vec::iterator i = v->begin();
	while (i != v->end())
	  {
	    if (*i == last)
	      {
		destroy(*i);
		i = v->erase(i);
	      }
	    else
	      {
		last = *i++;
	      }
	  }
	}

      if (v->size() == 0)
	{
	  delete v;
	  switch (op)
	    {
	    case And:
	      return constant::true_instance();
	    case Or:
	      return constant::false_instance();
	    }
	  /* Unreachable code.  */
	  assert(0);
	}
      if (v->size() == 1)
	{
	  formula* res = (*v)[0];
	  delete v;
	  return res;
	}

      map::iterator i = instances.find(p);
      if (i != instances.end())
	{
	  delete v;
	  return static_cast<multop*>(i->second->ref());
	}
      multop* ap = new multop(op, v);
      instances[p] = ap;
      return static_cast<multop*>(ap->ref());

    }

    formula*
    multop::instance(type op, formula* first, formula* second)
    {
      vec* v = new vec;
      v->push_back(first);
      v->push_back(second);
      return instance(op, v);
    }

    unsigned
    multop::instance_count()
    {
      return instances.size();
    }
  }
}
