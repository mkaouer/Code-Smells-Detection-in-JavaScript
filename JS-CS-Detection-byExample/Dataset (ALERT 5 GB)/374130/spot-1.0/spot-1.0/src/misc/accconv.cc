// -*- coding: utf-8 -*-
// Copyright (C) 2011, 2012 Laboratoire de Recherche et Développement
// de l'Epita (LRDE)
//
// This file is part of Spot, a model checking library.
//
// Spot is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or
// (at your option) any later version.
//
// Spot is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
// or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
// License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

#include <cassert>
#include "accconv.hh"

namespace spot
{
  bdd acceptance_convertor::as_positive_product(bdd acc)
  {
    // Cache lookup.
    bdd_cache_t::const_iterator it = pos_prod_cache_.find(acc);
    if (it != pos_prod_cache_.end())
      return it->second;

    // This would be one way to construct the positive product,
    // if we did not want to populate a cache.
    //
    // bdd res = bddtrue;
    // BDD a = acc.id();
    // while (a)
    //   {
    //	   if (bdd_high(a))
    //	     res &= bdd_ithvar(bdd_var(a));
    //	   a = bdd_low(a);
    //   }

    // Skip all negative variables.
    bdd a = acc;
    while (a != bddfalse && bdd_high(a) == bddfalse)
      a = bdd_low(a);

    bdd res = bddtrue;
    if (a != bddfalse)
      {
	// Make a recursive call right below each positive variable,
	// in order to populate the cache.
	res = bdd_ithvar(bdd_var(a));
	bdd low = bdd_low(a);
	if (low != bddfalse)
	  res &= as_positive_product(low);
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
