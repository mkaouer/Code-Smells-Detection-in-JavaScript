// Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#ifndef SPOT_TGBA_FORMULA2BDD_HH
# define SPOT_TGBA_FORMULA2BDD_HH

# include "bdddict.hh"
# include "ltlast/formula.hh"

namespace spot
{
  // \brief Convert a formula into a BDD.
  //
  // Convert formula \a f into a Bdd, using existing variables from \a
  // d, and registering new one as necessary.  \a for_me is the
  // address to use as owner of the variables used in the BDD.
  bdd formula_to_bdd(const ltl::formula* f, bdd_dict* d, void* for_me);

  // Convert a BDD into a formula.
  const ltl::formula* bdd_to_formula(bdd f, const bdd_dict* d);
}

#endif // SPOT_TGBA_FORMULA2BDD_HH
