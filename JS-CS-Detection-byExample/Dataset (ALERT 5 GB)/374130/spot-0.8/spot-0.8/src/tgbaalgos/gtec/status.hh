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

#ifndef SPOT_TGBAALGOS_GTEC_STATUS_HH
# define SPOT_TGBAALGOS_GTEC_STATUS_HH

#include "sccstack.hh"
#include "nsheap.hh"
#include "tgba/tgba.hh"
#include <iosfwd>

namespace spot
{
  /// \brief The status of the emptiness-check on success.
  ///
  /// This contains everything needed to construct a counter-example:
  /// the automata, the stack of SCCs traversed by the counter-example,
  /// and the heap of visited states with their indexes.
  class couvreur99_check_status
  {
  public:
    couvreur99_check_status(const tgba* aut,
			   const numbered_state_heap_factory* nshf);
    ~couvreur99_check_status();

    const tgba* aut;
    scc_stack root;
    numbered_state_heap* h;	///< Heap of visited states.
    const state* cycle_seed;

    /// Output statistics about this object.
    void print_stats(std::ostream& os) const;

    /// Return the number of states visited by the search
    int states() const;
  };

}

#endif // SPOT_TGBAALGOS_GTEC_STATUS_HH
