// -*- coding: utf-8 -*-
// Copyright (C) 2012, 2013 Laboratoire de Recherche et Developpement de
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


#ifndef SPOT_PRIV_ACCCOMPL_HH
# define SPOT_PRIV_ACCCOMPL_HH

#include <map>
#include <bdd.h>
#include "misc/hash.hh"
#include "misc/bddlt.hh"

namespace spot
{
  /// \brief Helper class to convert acceptance conditions into promises
  ///
  /// A set of acceptance conditions represented by the sum "à la Spot",
  /// is converted into a product of promises.
  class acc_compl
  {
    public:
      acc_compl(const bdd& all, const bdd& neg)
        : all_(all),
          neg_(neg)
      {
      }


      bdd complement(const bdd& acc);
      bdd reverse_complement(const bdd& acc);

    protected:
      const bdd all_;
      const bdd neg_;
      typedef Sgi::hash_map<bdd, bdd, bdd_hash> bdd_cache_t;
      bdd_cache_t cache_;
  };
} // End namespace Spot

#endif // SPOT_PRIV_ACCCOMPL_HH
