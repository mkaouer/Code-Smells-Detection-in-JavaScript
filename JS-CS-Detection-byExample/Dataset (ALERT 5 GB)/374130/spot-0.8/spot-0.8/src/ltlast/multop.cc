// Copyright (C) 2009 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2005 Laboratoire d'Informatique de
// Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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
#include <iostream>
#include "multop.hh"
#include "constant.hh"
#include "visitor.hh"

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

      // Dereference children.
      for (unsigned n = 0; n < size(); ++n)
	nth(n)->destroy();

      delete children_;
    }

    std::string
    multop::dump() const
    {
      std::string r = "multop(";
      r += op_name();
      unsigned max = size();
      for (unsigned n = 0; n < max; ++n)
	r += ", " + nth(n)->dump();
      r += ")";
      return r;
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
      // Inline children of same kind.
      //
      // When we construct a formula such as Multop(Op,X,Multop(Op,Y,Z))
      // we will want to inline it as Multop(Op,X,Y,Z).
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
		  inlined.push_back(p->nth(n)->clone());
		(*i)->destroy();
		i = v->erase(i);
	      }
	    else
	      {
		++i;
	      }
	  }
	v->insert(v->end(), inlined.begin(), inlined.end());
      }

      std::sort(v->begin(), v->end(), formula_ptr_less_than());

      // Remove duplicates.  We can't use std::unique(), because we
      // must destroy() any formula we drop.
      {
	formula* last = 0;
	vec::iterator i = v->begin();
	while (i != v->end())
	  {
	    if (*i == last)
	      {
		(*i)->destroy();
		i = v->erase(i);
	      }
	    else
	      {
		last = *i++;
	      }
	  }
      }

      vec::size_type s = v->size();
      if (s == 0)
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
      else if (s == 1)
	{
	  // Simply replace Multop(Op,X) by X.
	  formula* res = (*v)[0];
	  delete v;
	  return res;
	}

      // The hash key.
      pair p(op, v);

      map::iterator i = instances.find(p);
      if (i != instances.end())
	{
	  // The instance already exists.
	  for (vec::iterator vi = v->begin(); vi != v->end(); ++vi)
	    (*vi)->destroy();
	  delete v;
	  return static_cast<multop*>(i->second->clone());
	}

      // This is the first instance of this formula.

      // Record the instance in the map,
      multop* ap = new multop(op, v);
      instances[p] = ap;
      return ap->clone();
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

    std::ostream&
    multop::dump_instances(std::ostream& os)
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
