// Copyright (C) 2008, 2011 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
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

#include <iostream>
#include "tgba/tgba.hh"
#include "stats.hh"
#include "reachiter.hh"

namespace spot
{
  namespace
  {
    class stats_bfs: public tgba_reachable_iterator_breadth_first
    {
    public:
      stats_bfs(const tgba* a, tgba_statistics& s)
	: tgba_reachable_iterator_breadth_first(a), s_(s)
      {
      }

      void
      process_state(const state*, int, tgba_succ_iterator*)
      {
	++s_.states;
      }

      void
      process_link(const state*, int, const state*, int,
		   const tgba_succ_iterator*)
      {
	++s_.transitions;
      }

    private:
      tgba_statistics& s_;
    };

    class sub_stats_bfs: public stats_bfs
    {
    public:
      sub_stats_bfs(const tgba* a, tgba_sub_statistics& s)
	: stats_bfs(a, s), s_(s), seen_(bddtrue)
      {
      }

      void
      process_link(const state*, int, const state*, int,
		   const tgba_succ_iterator* it)
      {
	++s_.transitions;

	bdd cond = it->current_condition();
	bdd newvars = bdd_exist(bdd_support(cond), seen_);
	if (newvars != bddtrue)
	  {
	    seen_ &= newvars;
	    int count = 0;
	    while (newvars != bddtrue)
	      {
		++count;
		newvars = bdd_high(newvars);
	      }
	    // If we discover one new variable, that means that all
	    // transitions we counted so far are actually double
	    // subtransitions.  If we have two new variables, they where
	    // quadruple transitions, etc.
	    s_.sub_transitions <<= count;
	  }
	while (cond != bddfalse)
	  {
	    cond -= bdd_satoneset(cond, seen_, bddtrue);
	    ++s_.sub_transitions;
	  }
      }

    private:
      tgba_sub_statistics& s_;
      bdd seen_;
    };
  } // anonymous


  std::ostream& tgba_statistics::dump(std::ostream& out) const
  {
    out << "transitions: " << transitions << std::endl;
    out << "states: " << states << std::endl;
    return out;
  }

  std::ostream& tgba_sub_statistics::dump(std::ostream& out) const
  {
    out << "sub trans.: " << sub_transitions << std::endl;
    this->tgba_statistics::dump(out);
    return out;
  }

  tgba_statistics
  stats_reachable(const tgba* g)
  {
    tgba_statistics s;
    stats_bfs d(g, s);
    d.run();
    return s;
  }

  tgba_sub_statistics
  sub_stats_reachable(const tgba* g)
  {
    tgba_sub_statistics s;
    sub_stats_bfs d(g, s);
    d.run();
    return s;
  }
}
