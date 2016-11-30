// Copyright (C) 2010, 2012 Laboratoire de Recherche et Développement de
// l'Epita (LRDE).
// Copyright (C) 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
// et Marie Curie.
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

#include "length.hh"
#include "ltlvisit/postfix.hh"
#include "ltlast/multop.hh"
#include "ltlast/unop.hh"

namespace spot
{
  namespace ltl
  {
    namespace
    {
      class length_visitor: public postfix_visitor
      {
      public:
	length_visitor()
	  : result_(0)
	{
	}

	int
	result() const
	{
	  return result_;
	}

	virtual void
	visit(const multop* mo)
	{
	  unsigned s = mo->size();
	  for (unsigned i = 0; i < s; ++i)
	    mo->nth(i)->accept(*this);
	  // "a & b & c" should count for 5, even though it is
	  // stored as And(a,b,c).
	  result_ += s - 1;
	}

	virtual void
	doit_default(const formula*)
	{
	  ++result_;
	}

      protected:
	int result_; // size of the formula
      };

      class length_boolone_visitor: public length_visitor
      {

	virtual void
	visit(const unop* uo)
	{
	  ++result_;
	  // Boolean formula have length one.
	  if (!uo->is_boolean())
	    uo->child()->accept(*this);
	}

	virtual void
	visit(const multop* mo)
	{
	  // Boolean formula have length one.
	  if (mo->is_boolean())
	    {
	      ++result_;
	      return;
	    }

	  unsigned s = mo->size();
	  for (unsigned i = 0; i < s; ++i)
	    mo->nth(i)->accept(*this);
	  // "a & b & c" should count for 5, even though it is
	  // stored as And(a,b,c).
	  result_ += s - 1;
	}

      };
    }

    int
    length(const formula* f)
    {
      length_visitor v;
      f->accept(v);
      return v.result();
    }

    int
    length_boolone(const formula* f)
    {
      length_boolone_visitor v;
      f->accept(v);
      return v.result();
    }

  }
}
