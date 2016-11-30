// -*- coding: utf-8 -*-
// Copyright (C) 2008, 2009, 2010, 2011, 2012 Laboratoire de Recherche
// et Développement de l'Epita (LRDE)
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

#include <iostream>
#include "automatop.hh"
#include "nfa.hh"
#include "visitor.hh"

namespace spot
{
  namespace ltl
  {
    automatop::automatop(const nfa::ptr nfa, vec* v, bool negated)
      : ref_formula(AutomatOp), nfa_(nfa), children_(v), negated_(negated)
    {
      is.boolean = false;
      is.sugar_free_boolean = true;
      is.in_nenoform = true;
      is.X_free = true;
      is.sugar_free_ltl = true;
      is.ltl_formula = false;
      is.eltl_formula = true;
      is.psl_formula = false;
      is.sere_formula = false;
      is.finite = false;
      is.eventual = false;
      is.universal = false;
      is.syntactic_safety = false;
      is.syntactic_guarantee = false;
      is.syntactic_obligation = false;
      is.syntactic_recurrence = false;
      is.syntactic_persistence = false;
      is.not_marked = true;
      is.accepting_eword = false;

      unsigned s = v->size();
      for (unsigned i = 0; i < s; ++i)
	props &= (*v)[i]->get_props();
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
    automatop::accept(visitor& v) const
    {
      v.visit(this);
    }

    automatop::map automatop::instances;

    const automatop*
    automatop::instance(const nfa::ptr nfa, vec* v, bool negated)
    {
      assert(nfa != 0);
      triplet p(std::make_pair(nfa, negated), v);
      map::iterator i = instances.find(p);
      const automatop* res;
      if (i != instances.end())
	{
	  // The instance already exists.
	  for (vec::iterator vi = v->begin(); vi != v->end(); ++vi)
	    (*vi)->destroy();
	  delete v;
	  res = i->second;
	}
      else
	{
	  res = instances[p] = new automatop(nfa, v, negated);
	}
      res->clone();
      return res;
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
