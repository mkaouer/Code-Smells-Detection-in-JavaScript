// Copyright (C) 2009  Laboratoire de recherche et développement de l'Epita.
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

#include <sstream>
#include "futurecondcol.hh"
#include "bddprint.hh"

namespace spot
{

  void
  future_conditions_collector::map_builder_(unsigned src)
  {
    // The future conditions of a SCC are the conditions of the SCC
    // augmented with the future conditions of any descendent SCC.
    cond_set res(scc_map_.cond_set_of(src));
    const scc_map::succ_type& next = scc_map_.succ(src);
    for (scc_map::succ_type::const_iterator i = next.begin();
	 i != next.end(); ++i)
      {
	unsigned dest = i->first;
	map_builder_(dest);
	res.insert(i->second);
	res.insert(future_conds_[dest].begin(), future_conds_[dest].end());
      }
    future_conds_[src] = res;
  }

  future_conditions_collector::future_conditions_collector(const tgba* aut,
							   bool show)
    : tgba_scc(aut, show),
      // Initialize future_conds_ with as much empty
      // "cond_set"s as there are SCCs.
      future_conds_(scc_map_.scc_count())
  {
    // Fill future_conds by recursively visiting the (acyclic) graph
    // of SCCs.
    map_builder_(scc_map_.initial());
  }

  future_conditions_collector::~future_conditions_collector()
  {
  }

  const future_conditions_collector::cond_set&
  future_conditions_collector::future_conditions(const spot::state* st) const
  {
    // The future conditions of a state are the future conditions of
    // the SCC the state belongs to.
    unsigned s = scc_map_.scc_of_state(st);
    return future_conds_[s];
  }

  std::string
  future_conditions_collector::format_state(const state* state) const
  {
    if (!show_)
      return aut_->format_state(state);

    std::ostringstream str;
    str << this->tgba_scc::format_state(state);
    str << "\\n[";
    const cond_set& c = future_conditions(state);
    for (cond_set::const_iterator i = c.begin(); i != c.end(); ++i)
      {
	if (i != c.begin())
	  str << ", ";
	bdd_print_formula(str, get_dict(), *i);
      }
    str << "]";
    return str.str();
  }
}
