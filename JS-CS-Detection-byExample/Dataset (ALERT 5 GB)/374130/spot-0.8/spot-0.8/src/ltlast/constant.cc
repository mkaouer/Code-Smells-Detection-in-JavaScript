// Copyright (C) 2009 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2005 Laboratoire d'Informatique de Paris
// 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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

#include "constant.hh"
#include "visitor.hh"
#include <cassert>

namespace spot
{
  namespace ltl
  {
    constant::constant(type val)
      : val_(val)
    {
    }

    constant::~constant()
    {
    }

    std::string
    constant::dump() const
    {
      switch (val())
	{
	case True:
	  return "constant(1)";
	case False:
	  return "constant(0)";
	}
      // Unreachable code.
      assert(0);
      return "BUG";
    }

    void
    constant::accept(visitor& v)
    {
      v.visit(this);
    }

    void
    constant::accept(const_visitor& v) const
    {
      v.visit(this);
    }

    constant::type
    constant::val() const
    {
      return val_;
    }

    const char*
    constant::val_name() const
    {
      switch (val_)
	{
	case True:
	  return "1";
	case False:
	  return "0";
	}
      // Unreachable code.
      assert(0);
      return 0;
    }

    constant*
    constant::false_instance()
    {
      static constant f(constant::False);
      return &f;
    }

    constant*
    constant::true_instance()
    {
      static constant t(constant::True);
      return &t;
    }
  }
}
