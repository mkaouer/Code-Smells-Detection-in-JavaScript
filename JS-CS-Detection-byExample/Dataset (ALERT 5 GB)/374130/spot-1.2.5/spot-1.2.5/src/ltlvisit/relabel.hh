// -*- coding: utf-8 -*-
// Copyright (C) 2012, 2013 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
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

#ifndef SPOT_LTLVISIT_RELABEL_HH
# define SPOT_LTLVISIT_RELABEL_HH

#include "ltlast/formula.hh"
#include "misc/hash.hh"

namespace spot
{
  namespace ltl
  {
    enum relabeling_style { Abc, Pnn };


    struct relabeling_map: public Sgi::hash_map<const formula*,
						const formula*,
						ptr_hash<formula> >
    {
      ~relabeling_map()
      {
	for (iterator i = begin(); i != end(); ++i)
	  i->second->destroy();
      }
    };

    /// \ingroup ltl_rewriting
    /// \brief Relabel the atomic propositions in a formula.
    ///
    /// If \a m is non-null, it is filled with correspondence
    /// between the new names (keys) and the old names (values).
    SPOT_API
    const formula* relabel(const formula* f, relabeling_style style,
			   relabeling_map* m = 0);


    /// \ingroup ltl_rewriting
    /// \brief Relabel Boolean subexpressions in a formula using
    /// atomic propositions.
    ///
    /// If \a m is non-null, it is filled with correspondence
    /// between the new names (keys) and the old names (values).
    SPOT_API
    const formula* relabel_bse(const formula* f, relabeling_style style,
			       relabeling_map* m = 0);

  }
}



#endif // SPOT_LTLVISIT_RELABEL_HH
