// Copyright (C) 2010, 2011, 2012 Laboratoire de Recherche et
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

#ifndef SPOT_LTLVISIT_MARK_HH
# define SPOT_LTLVISIT_MARK_HH

#include "ltlast/formula.hh"
#include "ltlast/visitor.hh"
#include "misc/hash.hh"

namespace spot
{
  namespace ltl
  {
    class mark_tools
    {
    public:
      /// \brief Mark operators NegClosure and EConcat.
      /// \ingroup ltl_rewriting
      ///
      /// \param f The formula to rewrite.
      const formula* mark_concat_ops(const formula* f);

      const formula* simplify_mark(const formula* f);

      mark_tools();
      ~mark_tools();

    private:
      typedef Sgi::hash_map<const formula*, const formula*,
			    ptr_hash<formula> > f2f_map;
      f2f_map simpmark_;
      f2f_map markops_;
      visitor* simpvisitor_;
      visitor* markvisitor_;
    };

  }
}

#endif //  SPOT_LTLVISIT_MARK_HH
