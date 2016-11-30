// Copyright (C) 2009 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2005 Laboratoire d'Informatique de Paris
// 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
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

#include "unop.hh"
#include "visitor.hh"
#include <cassert>
#include <iostream>

namespace spot
{
  namespace ltl
  {
    unop::unop(type op, formula* child)
      : op_(op), child_(child)
    {
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
    unop::accept(visitor& v)
    {
      v.visit(this);
    }

    void
    unop::accept(const_visitor& v) const
    {
      v.visit(this);
    }

    const formula*
    unop::child() const
    {
      return child_;
    }

    formula*
    unop::child()
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
	}
      // Unreachable code.
      assert(0);
      return 0;
    }

    unop::map unop::instances;

    unop*
    unop::instance(type op, formula* child)
    {
      pair p(op, child);
      map::iterator i = instances.find(p);
      if (i != instances.end())
	{
	  // This instance already exists.
	  child->destroy();
	  return static_cast<unop*>(i->second->clone());
	}
      unop* ap = new unop(op, child);
      instances[p] = ap;
      return static_cast<unop*>(ap->clone());
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
