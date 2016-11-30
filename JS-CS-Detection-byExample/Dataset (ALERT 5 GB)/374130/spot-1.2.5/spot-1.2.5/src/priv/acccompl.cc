// -*- coding: utf-8 -*-
// Copyright (C) 2012 Laboratoire de Recherche et Developpement de
// l'Epita (LRDE)
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

#include "acccompl.hh"

namespace spot
{
  // If ALL = a!b!c + !ab!c + !a!bc", the negation of ACC = a!b!c is
  // RES = bc.   We do that by computing ALL-ACC and enumerating
  // all positive variables in the remaining products.
  bdd acc_compl::complement(const bdd& acc)
  {
    bdd_cache_t::const_iterator it = cache_.find(acc);
    if (it != cache_.end())
      return it->second;

    bdd res = bddtrue;
    bdd n = all_ - acc;

    while (n != bddfalse)
    {
      bdd cond = bdd_satone(n);
      bdd oldcond = cond;
      n -= cond;

      while (bdd_high(cond) == bddfalse)
        cond = bdd_low(cond);

      // Here we want to keep only the current one.
      // tmp is only useful to keep the value and cache it.
      bdd tmp = bdd_ithvar(bdd_var(cond));
      res &= tmp;
    }

    cache_[acc] = res;

    return res;
  }

  bdd acc_compl::reverse_complement(const bdd& acc)
  {
    // We are sure that if we have no acceptance condition the result
    // is all_.
    if (acc == bddtrue)
      return all_;

    // Since we never cache a unique positive bdd, we can reuse the
    // same cache.  In fact the only kind of acc we will receive in
    // this method, are a conjunction of positive acceptance
    // conditions.  (i.e., "ab" and not "a!b + !ab")
    bdd_cache_t::const_iterator it = cache_.find(acc);
    if (it != cache_.end())
      return it->second;

    bdd res = all_;
    bdd cond = acc;
    while (cond != bddtrue)
    {
      res &= bdd_nithvar(bdd_var(cond));
      cond = bdd_high(cond);
    }

    cache_[acc] = res;

    return res;
  }

} // End namespace spot.
