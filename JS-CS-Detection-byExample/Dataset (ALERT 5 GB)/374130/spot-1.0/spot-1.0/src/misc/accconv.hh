// Copyright (C) 2011 Laboratoire de Recherche et Developpement de
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

#ifndef SPOT_MISC_ACCCONV_HH
# define SPOT_MISC_ACCCONV_HH

#include <bdd.h>
#include "misc/hash.hh"
#include "misc/bddlt.hh"

namespace spot
{
  /// \brief Help class to convert between acceptance conditions to
  /// other BDD formats.
  class acceptance_convertor
  {
  public:
    acceptance_convertor(bdd allneg)
      : allneg_(allneg)
    {
    }

    bdd as_positive_product(bdd acc);

    bdd as_full_product(bdd acc);

  protected:
    bdd allneg_;
    typedef Sgi::hash_map<bdd, bdd, bdd_hash> bdd_cache_t;
    bdd_cache_t pos_prod_cache_;
    bdd_cache_t full_prod_cache_;
  };

}

#endif // SPOT_MISC_ACCCONV_HH
