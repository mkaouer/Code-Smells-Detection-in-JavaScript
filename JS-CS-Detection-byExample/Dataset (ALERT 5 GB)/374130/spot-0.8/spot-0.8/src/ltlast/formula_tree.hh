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

/// \file ltlast/formula_tree.hh
/// \brief Trees representing formulae where atomic propositions are unknown
#ifndef SPOT_LTLAST_FORMULA_TREE_HH
# define SPOT_LTLAST_FORMULA_TREE_HH

# include <vector>
# include <boost/shared_ptr.hpp>
# include "formula.hh"
# include "multop.hh"
# include "binop.hh"
# include "unop.hh"
# include "nfa.hh"

namespace spot
{
  namespace ltl
  {
    /// Trees representing formulae where atomic propositions are unknown.
    namespace formula_tree
    {
      struct node
      {
	virtual ~node() {};
      };
      /// We use boost::shared_ptr to easily handle deletion.
      typedef boost::shared_ptr<node> node_ptr;

      struct node_unop : node
      {
	unop::type op;
	node_ptr child;
      };
      struct node_binop : node
      {
	binop::type op;
	node_ptr lhs;
	node_ptr rhs;
      };
      struct node_multop : node
      {
	multop::type op;
	node_ptr lhs;
	node_ptr rhs;
      };
      struct node_nfa : node
      {
	std::vector<node_ptr> children;
	spot::ltl::nfa::ptr nfa;
      };
      /// Integer values for True and False used in node_atomic.
      enum { True = -1, False = -2 };
      struct node_atomic : node
      {
	int i;
      };

      /// Instanciate the formula corresponding to the node with
      /// atomic propositions taken from \a v.
      formula* instanciate(const node_ptr np, const std::vector<formula*>& v);

      /// Get the arity.
      size_t arity(const node_ptr np);
    }
  }
}

#endif // SPOT_LTLAST_FORMULA_TREE_HH_
