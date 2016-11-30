// Copyright (C) 2011 Laboratoire de Recherche et Developpement de
// l'Epita (LRDE).
// Copyright (C) 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include "explscc.hh"

namespace spot
{
  const state*
  connected_component_hash_set::has_state(const state* s) const
  {
    set_type::const_iterator it = states.find(s);
    if (it != states.end())
      {
	if (s != *it)
	  s->destroy();
	return *it;
      }
    else
      return 0;
  }

  void
  connected_component_hash_set::insert(const state* s)
  {
    states.insert(s);
  }

  //////////////////////////////////////////////////////////////////////

  connected_component_hash_set_factory::connected_component_hash_set_factory()
    : explicit_connected_component_factory()
  {
  }

  connected_component_hash_set*
  connected_component_hash_set_factory::build() const
  {
    return new connected_component_hash_set();
  }

  const connected_component_hash_set_factory*
  connected_component_hash_set_factory::instance()
  {
    static connected_component_hash_set_factory f;
    return &f;
  }
}
