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
  } // anonymous

  tgba_statistics
  stats_reachable(const tgba* g)
  {
    tgba_statistics s = {0, 0};
    stats_bfs d(g, s);
    d.run();
    return s;
  }
}
