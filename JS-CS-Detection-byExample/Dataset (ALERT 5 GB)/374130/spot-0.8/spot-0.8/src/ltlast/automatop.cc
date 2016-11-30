// Copyright (C) 2008, 2009 Laboratoire de Recherche et Developpement
// de l'Epita (LRDE)
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

#include <iostream>
#include "automatop.hh"
#include "nfa.hh"
#include "visitor.hh"

namespace spot
{
  namespace ltl
  {
    automatop::automatop(const nfa::ptr nfa, vec* v, bool negated)
      : nfa_(nfa), children_(v), negated_(negated)
    {
    }

    automatop::~automatop()
    {
      // Get this instance out of the instance map.
      triplet p(std::make_pair(get_nfa(), negated_), children_);
      map::iterator i = instances.find(p);
      assert (i != instances.end());
      instances.erase(i);

      // Dereference children.
      for (unsigned n = 0; n < size(); ++n)
	nth(n)->destroy();

      delete children_;
    }

    std::string
    automatop::dump() const
    {
      std::string r = is_negated() ? "!" : "";
      r += get_nfa()->get_name();
      r += "(";
      r += nth(0)->dump();
      for (unsigned n = 1; n < size(); ++n)
	r += ", " + nth(n)->dump();
      r += ")";
      return r;
    }

    void
    automatop::accept(visitor& v)
    {
      v.visit(this);
    }

    void
    automatop::accept(const_visitor& v) const
    {
      v.visit(this);
    }

    automatop::map automatop::instances;

    automatop*
    automatop::instance(const nfa::ptr nfa, vec* v, bool negated)
    {
      assert(nfa != 0);
      triplet p(std::make_pair(nfa, negated), v);
      map::iterator i = instances.find(p);
      if (i != instances.end())
	{
	  // The instance already exists.
	  for (vec::iterator vi = v->begin(); vi != v->end(); ++vi)
	    (*vi)->destroy();
	  delete v;
	  return static_cast<automatop*>(i->second->clone());
	}
      automatop* res = new automatop(nfa, v, negated);
      instances[p] = res;
      return static_cast<automatop*>(res->clone());
    }

    unsigned
    automatop::size() const
    {
      return children_->size();
    }

    const formula*
    automatop::nth(unsigned n) const
    {
      return (*children_)[n];
    }

    formula*
    automatop::nth(unsigned n)
    {
      return (*children_)[n];
    }

    const spot::ltl::nfa::ptr
    automatop::get_nfa() const
    {
      assert(nfa_ != 0);
      return nfa_;
    }

    bool
    automatop::is_negated() const
    {
      return negated_;
    }

    unsigned
    automatop::instance_count()
    {
      return instances.size();
    }

    std::ostream&
    automatop::dump_instances(std::ostream& os)
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
