// Copyright (C) 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_TGBAALGOS_GTEC_CE_HH
# define SPOT_TGBAALGOS_GTEC_CE_HH

#include "status.hh"
#include "tgbaalgos/emptiness.hh"
#include "tgbaalgos/emptiness_stats.hh"

namespace spot
{
  /// Compute a counter example from a spot::couvreur99_check_status
  class couvreur99_check_result:
    public emptiness_check_result,
    public acss_statistics
  {
  public:
    couvreur99_check_result(const couvreur99_check_status* ecs,
			    option_map o = option_map());

    virtual tgba_run* accepting_run();

    void print_stats(std::ostream& os) const;

    virtual unsigned acss_states() const;

  protected:
    /// Called by accepting_run() to find a cycle which traverses all
    /// acceptance conditions in the accepted SCC.
    void accepting_cycle();

  private:
    const couvreur99_check_status* ecs_;
    tgba_run* run_;
  };
}

#endif // SPOT_TGBAALGOS_GTEC_CE_HH
