// Copyright (C) 2011 Laboratoire de Recherche et Développement de
// l'Epita.
// Copyright (C) 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_TGBAALGOS_POWERSET_HH
# define SPOT_TGBAALGOS_POWERSET_HH

# include <list>
# include <map>
# include "tgba/tgbaexplicit.hh"

namespace spot
{

  struct power_map
  {
    typedef std::set<const state*, state_ptr_less_than> power_state;
    typedef std::map<int, power_state> power_map_data;
    typedef Sgi::hash_set<const state*, state_ptr_hash,
			  state_ptr_equal> state_set;

    ~power_map()
    {
      // Release all states.
      state_set::const_iterator i = states.begin();
      while (i != states.end())
	{
	  // Advance the iterator before deleting the key.
	  const state* s = *i;
	  ++i;
	  s->destroy();
	}
    }

    const power_state&
    states_of(int s) const
    {
      return map_.find(s)->second;
    }

    const state*
    canonicalize(const state* s)
    {
      state_set::const_iterator i = states.find(s);
      if (i != states.end())
	{
	  s->destroy();
	  s = *i;
	}
      else
	{
	  states.insert(s);
	}
      return s;
    }

    power_map_data map_;
    state_set states;
  };


  /// \brief Build a deterministic automaton, ignoring acceptance conditions.
  /// \ingroup tgba_misc
  ///
  /// This create a deterministic automaton that recognizes the
  /// same language as \a aut would if its acceptance conditions
  /// were ignored.  This is the classical powerset algorithm.
  ///
  /// If \a pm is supplied it will be filled with the set of original states
  /// associated to each state of the deterministic automaton.
  //@{
  tgba_explicit_number* tgba_powerset(const tgba* aut, power_map& pm);
  tgba_explicit_number* tgba_powerset(const tgba* aut);
  //@}
}

#endif // SPOT_TGBAALGOS_POWERSET_HH
