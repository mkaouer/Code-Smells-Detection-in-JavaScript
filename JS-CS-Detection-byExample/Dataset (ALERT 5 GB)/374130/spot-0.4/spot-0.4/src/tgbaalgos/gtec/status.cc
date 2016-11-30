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

#include <ostream>
#include "status.hh"

namespace spot
{
  couvreur99_check_status::couvreur99_check_status
    (const tgba* aut,
     const numbered_state_heap_factory* nshf)
      : aut(aut),
	h(nshf->build())
  {
  }

  couvreur99_check_status::~couvreur99_check_status()
  {
    delete h;
  }

  void
  couvreur99_check_status::print_stats(std::ostream& os) const
  {
    os << h->size() << " unique states visited" << std::endl;
    os << root.size()
       << " strongly connected components in search stack"
       << std::endl;
  }

  int
  couvreur99_check_status::states() const
  {
    return h->size();
  }
}
