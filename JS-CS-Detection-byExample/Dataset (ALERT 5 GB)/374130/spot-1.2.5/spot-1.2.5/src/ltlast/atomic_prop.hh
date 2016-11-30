// -*- coding: utf-8 -*-
// Copyright (C) 2009, 2012, 2013, 2014 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
// et Marie Curie.
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

/// \file ltlast/atomic_prop.hh
/// \brief LTL atomic propositions
#ifndef SPOT_LTLAST_ATOMIC_PROP_HH
# define SPOT_LTLAST_ATOMIC_PROP_HH

#include "refformula.hh"
#include <string>
#include <iosfwd>
#include <map>
#include "ltlenv/environment.hh"

namespace spot
{
  namespace ltl
  {

    /// \ingroup ltl_ast
    /// \brief Atomic propositions.
    class SPOT_API atomic_prop : public ref_formula
    {
    public:
      /// Build an atomic proposition with name \a name in
      /// environment \a env.
      static const atomic_prop*
      instance(const std::string& name, environment& env);

      virtual void accept(visitor& visitor) const;

      /// Get the name of the atomic proposition.
      const std::string& name() const
      {
	return name_;
      }

      /// Get the environment of the atomic proposition.
      environment& env() const
      {
	return *env_;
      }

      /// Return a canonic representation of the atomic proposition
      virtual std::string dump() const;

      /// Number of instantiated atomic propositions.  For debugging.
      static unsigned instance_count();
      /// List all instances of atomic propositions.  For debugging.
      static std::ostream& dump_instances(std::ostream& os);

    protected:
      atomic_prop(const std::string& name, environment& env);
      virtual ~atomic_prop();

      typedef std::pair<std::string, environment*> pair;
      typedef std::map<pair, const atomic_prop*> map;
      static map instances;

    private:
      std::string name_;
      environment* env_;
    };

    inline
    const atomic_prop*
    is_atomic_prop(const formula* f)
    {
      if (f->kind() != formula::AtomicProp)
	return 0;
      return static_cast<const atomic_prop*>(f);
    }
  }
}

#endif // SPOT_LTLAST_ATOMICPROP_HH
