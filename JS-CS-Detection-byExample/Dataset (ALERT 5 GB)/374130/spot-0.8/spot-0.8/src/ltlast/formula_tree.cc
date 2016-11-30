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

#include <cassert>
#include "formula_tree.hh"
#include "allnodes.hh"

namespace spot
{
  namespace ltl
  {
    namespace formula_tree
    {
      formula*
      instanciate(const node_ptr np, const std::vector<formula*>& v)
      {
	if (node_atomic* n = dynamic_cast<node_atomic*>(np.get()))
	  return n->i == True ? constant::true_instance() :
	    n->i == False ? constant::false_instance() : v.at(n->i)->clone();

	if (node_unop* n = dynamic_cast<node_unop*>(np.get()))
	  return unop::instance(n->op, instanciate(n->child, v));
	if (node_nfa* n = dynamic_cast<node_nfa*>(np.get()))
	{
	  automatop::vec* va = new automatop::vec;
	  std::vector<node_ptr>::const_iterator i = n->children.begin();
	  while (i != n->children.end())
	    va->push_back(instanciate(*i++, v));
	  return automatop::instance(n->nfa, va, false);
	}
	if (node_binop* n = dynamic_cast<node_binop*>(np.get()))
	  return binop::instance(n->op,
				 instanciate(n->lhs, v),
				 instanciate(n->rhs, v));
	if (node_multop* n = dynamic_cast<node_multop*>(np.get()))
	  return multop::instance(n->op,
				  instanciate(n->lhs, v),
				  instanciate(n->rhs, v));

	/* Unreachable code.  */
	assert(0);
	return 0;
      }

      size_t
      arity(const node_ptr np)
      {
	if (node_atomic* n = dynamic_cast<node_atomic*>(np.get()))
	  return std::max(n->i + 1, 0);
	if (node_unop* n = dynamic_cast<node_unop*>(np.get()))
	  return arity(n->child);
	if (node_nfa* n = dynamic_cast<node_nfa*>(np.get()))
	{
	  size_t res = 0;
	  std::vector<node_ptr>::const_iterator i = n->children.begin();
	  while (i != n->children.end())
	    res = std::max(arity(*i++), res);
	  return res;
	}
	if (node_binop* n = dynamic_cast<node_binop*>(np.get()))
	  return std::max(arity(n->lhs), arity(n->rhs));
	if (node_multop* n = dynamic_cast<node_multop*>(np.get()))
	  return std::max(arity(n->lhs), arity(n->rhs));

	/* Unreachable code.  */
	assert(0);
	return 0;
      }
    }
  }
}
