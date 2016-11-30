// Copyright (C) 2011 Laboratoire de Recherche et Developpement de
// l'Epita (LRDE)
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
#include "accconv.hh"

namespace spot
{
  bdd acceptance_convertor::as_positive_product(bdd acc)
  {
    // Lookup in cache.
    bdd_cache_t::const_iterator it = pos_prod_cache_.find(acc);
    if (it != pos_prod_cache_.end())
      return it->second;

    // Split the sum
    bdd res = bddtrue;
    bdd all = acc;
    while (all != bddfalse)
      {
	bdd one = bdd_satone(all);
	all -= one;
	// Lookup the subproduct in the cache.
	it = pos_prod_cache_.find(one);
	if (it != pos_prod_cache_.end())
	  {
	    res &= it->second;
	    continue;
	  }
	// Otherwise, strip negative variables.
	bdd pos = bddfalse;
	bdd cur = one;
	while (cur != bddfalse)
	  {
	    bdd low = bdd_low(cur);
	    if (low == bddfalse)
	      {
		pos = bdd_ithvar(bdd_var(cur));
		break;
	      }
	    cur = low;
	  }
	assert(pos != bddfalse);
	// Cache result for subproduct.
	pos_prod_cache_[one] = pos;
	// Augment final result.
	res &= pos;
      }

    // Cache final result.
    pos_prod_cache_[acc] = res;
    return res;
  };

  bdd acceptance_convertor::as_full_product(bdd acc)
  {
    // Lookup in cache.
    bdd_cache_t::const_iterator it = full_prod_cache_.find(acc);
    if (it != full_prod_cache_.end())
      return it->second;

    bdd pos = as_positive_product(acc);
    bdd res = bdd_exist(allneg_, pos) & pos;

    // Cache final result.
    full_prod_cache_[acc] = res;
    return res;
  }

}
