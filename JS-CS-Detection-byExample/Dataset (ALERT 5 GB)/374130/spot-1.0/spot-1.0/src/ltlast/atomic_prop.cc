// -*- coding: utf-8 -*-
// Copyright (C) 2009, 2010, 2011, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2004, 2005 Laboratoire d'Informatique de
// Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
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

#include "atomic_prop.hh"
#include "visitor.hh"
#include <cassert>
#include <ostream>

namespace spot
{
  namespace ltl
  {

    atomic_prop::atomic_prop(const std::string& name, environment& env)
      : ref_formula(AtomicProp), name_(name), env_(&env)
    {
      is.boolean = true;
      is.sugar_free_boolean = true;
      is.in_nenoform = true;
      is.X_free = true;
      is.sugar_free_ltl = true;
      is.ltl_formula = true;
      is.eltl_formula = true;
      is.psl_formula = true;
      is.sere_formula = true;
      is.finite = true;
      is.eventual = false;
      is.universal = false;
      is.syntactic_safety = true;
      is.syntactic_guarantee = true;
      is.syntactic_obligation = true;
      is.syntactic_recurrence = true;
      is.syntactic_persistence = true;
      is.not_marked = true;
      is.accepting_eword = false;
      // is.lbt_atomic_props should be true if the name has the form
      // pNN where NN is any number of digit.
      std::string::const_iterator pos = name.begin();
      is.lbt_atomic_props = (pos != name.end() && *pos++ == 'p');
      while (is.lbt_atomic_props && pos != name.end())
	{
	  char l = *pos++;
	  is.lbt_atomic_props = (l >= '0' && l <= '9');
	}
    }

    atomic_prop::~atomic_prop()
    {
      // Get this instance out of the instance map.
      pair p(name(), &env());
      map::iterator i = instances.find(p);
      assert (i != instances.end());
      instances.erase(i);
    }

    std::string
    atomic_prop::dump() const
    {
      return "AP(" + name() + ")";
    }

    void
    atomic_prop::accept(visitor& v) const
    {
      v.visit(this);
    }

    const std::string&
    atomic_prop::name() const
    {
      return name_;
    }

    environment&
    atomic_prop::env() const
    {
      return *env_;
    }

    atomic_prop::map atomic_prop::instances;

    const atomic_prop*
    atomic_prop::instance(const std::string& name, environment& env)
    {
      pair p(name, &env);
      // FIXME: Use lower_bound, or a hash_map.
      map::iterator i = instances.find(p);
      const atomic_prop* ap;
      if (i != instances.end())
	ap = i->second;
      else
	ap = instances[p] = new atomic_prop(name, env);
      ap->clone();
      return ap;
    }

    unsigned
    atomic_prop::instance_count()
    {
      return instances.size();
    }

    std::ostream&
    atomic_prop::dump_instances(std::ostream& os)
    {

      for (map::iterator i = instances.begin(); i != instances.end(); ++i)
	{
	  os << i->second << " = " << i->second->ref_count_()
	     << " * atomic_prop(" << i->first.first << ", "
	     << i->first.second->name() << ")" << std::endl;
	}
      return os;
    }

  }
}
