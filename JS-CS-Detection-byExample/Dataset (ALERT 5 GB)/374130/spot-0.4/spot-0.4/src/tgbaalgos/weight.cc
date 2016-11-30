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

#include <cassert>
#include <ostream>
#include "weight.hh"

namespace spot
{
  weight::weight_vector* weight::pm = 0;

  weight::weight(const bdd& neg_all_cond) : neg_all_acc(neg_all_cond)
  {
  }

  void weight::inc_weight_handler(char* varset, int size)
  {
    bool ok = true;
    for (int v = 0; v < size; ++v)
      if (varset[v] > 0)
        {
          assert(ok);
          ok=false;
          weight::weight_vector::iterator it = pm->find(v);
          if (it == pm->end())
            pm->insert(std::make_pair(v, 1));
          else
            ++(it->second);
        }
  }

  weight& weight::operator+=(const bdd& acc)
  {
    pm = &m;
    bdd_allsat(acc, inc_weight_handler);
    return *this;
  }

  void weight::dec_weight_handler(char* varset, int size)
  {
    bool ok = true;
    for (int v = 0; v < size; ++v)
      if (varset[v] > 0)
        {
          assert(ok);
          ok=false;
          weight::weight_vector::iterator it = pm->find(v);
          assert(it != pm->end() && it->second > 0);
          if (it->second > 1)
            --(it->second);
          else
            pm->erase(it);
        }
  }

  weight& weight::operator-=(const bdd& acc)
  {
    pm = &m;
    bdd_allsat(acc, dec_weight_handler);
    return *this;
  }

  bdd weight::operator-(const weight& w) const
  {
    weight_vector::const_iterator itw1 = m.begin(), itw2 = w.m.begin();
    bdd res = bddfalse;

    while (itw1 != m.end() && itw2 != w.m.end())
      {
        assert(itw1->first <= itw2->first);
        if (itw1->first < itw2->first)
          {
            res |= bdd_exist(neg_all_acc, bdd_ithvar(itw1->first)) &
                                                      bdd_ithvar(itw1->first);
            ++itw1;
          }
        else
          {
            assert(itw1->second >= itw2->second);
            if (itw1->second > itw2->second)
              {
                res |= bdd_exist(neg_all_acc, bdd_ithvar(itw1->first)) &
                                                      bdd_ithvar(itw1->first);
              }
            ++itw1;
            ++itw2;
          }
      }
    assert(itw2 == w.m.end());
    while (itw1 != m.end())
      {
        res |= bdd_exist(neg_all_acc, bdd_ithvar(itw1->first)) &
                                                      bdd_ithvar(itw1->first);
        ++itw1;
      }
    return res;
  }

  std::ostream& operator<<(std::ostream& os, const weight& w)
  {
    weight::weight_vector::const_iterator it;
    for (it = w.m.begin(); it != w.m.end(); ++it)
      os << "(" << it->first << "," << it->second << ")";
    return os;
  }

};
