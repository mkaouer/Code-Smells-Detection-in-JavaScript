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

#include <cassert>
#include "bddop.hh"

namespace spot
{
  bdd
  compute_all_acceptance_conditions(bdd neg_acceptance_conditions)
  {
    bdd all = bddfalse;

    // Build all_acceptance_conditions_ from neg_acceptance_conditions_
    // I.e., transform !A & !B & !C into
    //        A & !B & !C
    //     + !A &  B & !C
    //     + !A & !B &  C
    bdd cur = neg_acceptance_conditions;
    while (cur != bddtrue)
    {
      assert(cur != bddfalse);

      bdd v = bdd_ithvar(bdd_var(cur));
      all |= v & bdd_exist(neg_acceptance_conditions, v);

      assert(bdd_high(cur) != bddtrue);
      cur = bdd_low(cur);
    }

    return all;
  }

  bdd
  compute_neg_acceptance_conditions(bdd all_acceptance_conditions)
  {
    bdd cur = bdd_support(all_acceptance_conditions);
    bdd neg = bddtrue;
    while (cur != bddtrue)
      {
	neg &= bdd_nithvar(bdd_var(cur));
	assert(bdd_low(cur) != bddtrue);
	cur = bdd_high(cur);
      }
    return neg;
  }

}
