// Copyright (C) 2003, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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
#include "binop.hh"
#include "visitor.hh"

namespace spot
{
  namespace ltl
  {
    binop::binop(type op, formula* first, formula* second)
      : op_(op), first_(first), second_(second)
    {
      dump_ = "binop(";
      dump_ += op_name();
      dump_ += ", " + first->dump() + ", " + second->dump() + ")";
      set_key_();
    }

    binop::~binop()
    {
      // Get this instance out of the instance map.
      pairf pf(first(), second());
      pair p(op(), pf);
      map::iterator i = instances.find(p);
      assert (i != instances.end());
      instances.erase(i);
    }

    void
    binop::accept(visitor& v)
    {
      v.visit(this);
    }

    void
    binop::accept(const_visitor& v) const
    {
      v.visit(this);
    }

    const formula*
    binop::first() const
    {
      return first_;
    }

    formula*
    binop::first()
    {
      return first_;
    }

    const formula*
    binop::second() const
    {
      return second_;
    }

    formula*
    binop::second()
    {
      return second_;
    }

    binop::type
    binop::op() const
    {
      return op_;
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
	}
      // Unreachable code.
      assert(0);
      return 0;
    }

    binop::map binop::instances;

    binop*
    binop::instance(type op, formula* first, formula* second)
    {
      // Sort the operands of associative operators, so that for
      // example the formula instance for 'a xor b' is the same as
      // that for 'b xor a'.
      switch (op)
	{
	case Xor:
	case Equiv:
	  if (second < first)
	    std::swap(first, second);
	  break;
	case Implies:
	case U:
	case R:
	  // Non associative operators.
	  break;
	}

      pairf pf(first, second);
      pair p(op, pf);
      map::iterator i = instances.find(p);
      if (i != instances.end())
	{
	  return static_cast<binop*>(i->second->ref());
	}
      binop* ap = new binop(op, first, second);
      instances[p] = ap;
      return static_cast<binop*>(ap->ref());
    }

    unsigned
    binop::instance_count()
    {
      return instances.size();
    }
  }
}
