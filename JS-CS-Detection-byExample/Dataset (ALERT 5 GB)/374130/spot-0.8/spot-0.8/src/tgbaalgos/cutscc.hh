// Copyright (C) 2009 Laboratoire de Recherche et Developpement de
// l'Epita (LRDE).
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


#ifndef SPOT_TGBAALGOS_CUTSCC_HH
# define SPOT_TGBAALGOS_CUTSCC_HH

#include <iosfwd>
#include <set>
#include <vector>
#include "tgba/public.hh"
#include "tgbaalgos/scc.hh"

namespace spot
{
  struct sccs_set
  {
    std::set<unsigned> sccs;
    unsigned size;
  };

  std::vector<std::vector<sccs_set* > >* find_paths(tgba* a, const scc_map& m);
  unsigned max_spanning_paths(std::vector<sccs_set* >* paths, scc_map& m);
  std::list<tgba*> split_tgba(tgba* a, const scc_map& m,
			      unsigned split_number);

}

#endif // SPOT_TGBAALGOS_CUTSCC_HH
