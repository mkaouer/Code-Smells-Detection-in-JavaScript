// Copyright (C) 2009 Laboratoire de Recherche et Développement
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

#include "saba.hh"

namespace spot
{
  saba::saba()
    : num_acc_(-1)
  {
  }

  saba::~saba()
  {
  }

  unsigned int
  saba::number_of_acceptance_conditions() const
  {
    if (num_acc_ < 0)
      {
	bdd all = all_acceptance_conditions();
	unsigned int n = 0;
	while (all != bddfalse)
	  {
	    ++n;
	    all -= bdd_satone(all);
	  }
	num_acc_ = n;
      }
    return num_acc_;
  }

} // end namespace spot.
