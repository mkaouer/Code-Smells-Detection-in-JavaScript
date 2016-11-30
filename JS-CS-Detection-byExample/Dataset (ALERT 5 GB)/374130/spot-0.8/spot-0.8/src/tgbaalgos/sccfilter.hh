// Copyright (C) 2009, 2010 Laboratoire de Recherche et Developpement
// de l'Epita (LRDE).
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

#ifndef SPOT_TGBAALGOS_SCCFILTER_HH
# define SPOT_TGBAALGOS_SCCFILTER_HH

#include "tgba/tgba.hh"

namespace spot
{

  /// \brief Prune unaccepting SCCs and remove superfluous acceptance
  /// conditions.
  ///
  /// This function will explore the SCCs of the automaton and remove
  /// dead SCCs (i.e. SCC that are not accepting, and those with no
  /// exit path leading to an accepting SCC).
  ///
  /// Additionally, this will try to remove useless acceptance
  /// conditions.  This operation may diminish the number of
  /// acceptance condition of the automaton (for instance when two
  /// acceptance conditions are always used together we only keep one)
  /// but it will never remove all acceptance conditions, even if it
  /// would be OK to have zero.
  ///
  /// Acceptance conditions on transitions going to non-accepting SCC
  /// are all removed.  Acceptance conditions going to an accepting
  /// SCC and coming from another SCC are only removed if \a
  /// remove_all_useless is set.  The default value of \a
  /// remove_all_useless is \c false because some algorithms (like the
  /// degeneralization) will work better if transitions going to an
  /// accepting SCC are accepting.
  tgba* scc_filter(const tgba* aut, bool remove_all_useless = false);

}

#endif // SPOT_TGBAALGOS_SCC_HH
