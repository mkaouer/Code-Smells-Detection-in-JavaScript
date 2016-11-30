// -*- coding: utf-8 -*-
// Copyright (C) 2009, 2010, 2011, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2005 Laboratoire d'Informatique de Paris
// 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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

#include "constant.hh"
#include "visitor.hh"
#include <cassert>

namespace spot
{
  namespace ltl
  {
    constant constant::true_instance_(constant::True);
    constant constant::false_instance_(constant::False);
    constant constant::empty_word_instance_(constant::EmptyWord);

    constant::constant(type val)
      : formula(Constant), val_(val)
    {
      switch (val)
	{
	case constant::True:
	case constant::False:
	  is.boolean = true;
	  is.sugar_free_boolean = true;
	  is.in_nenoform = true;
	  is.X_free = true;
	  is.sugar_free_ltl = true;
	  is.ltl_formula = true;
	  is.eltl_formula = true;
	  is.psl_formula = true;
	  is.sere_formula = true;
	  is.finite = true;
	  is.eventual = true;
	  is.universal = true;
	  is.syntactic_safety = true;
	  is.syntactic_guarantee = true;
	  is.syntactic_obligation = true;
	  is.syntactic_recurrence = true;
	  is.syntactic_persistence = true;
	  is.not_marked = true;
	  is.accepting_eword = false;
	  break;
	case constant::EmptyWord:
	  is.boolean = false;
	  is.sugar_free_boolean = false;
	  is.in_nenoform = true;
	  is.X_free = true;
	  is.sugar_free_ltl = true;
	  is.ltl_formula = false;
	  is.eltl_formula = false;
	  is.psl_formula = false;
	  is.sere_formula = true;
	  is.finite = true;
	  is.eventual = false;
	  is.syntactic_safety = false;
	  is.syntactic_guarantee = false;
	  is.syntactic_obligation = false;
	  is.syntactic_recurrence = false;
	  is.syntactic_persistence = false;
	  is.universal = false;
	  is.not_marked = true;
	  is.accepting_eword = true;
	  break;
	}
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
	case EmptyWord:
	  return "constant(e)";
	}
      // Unreachable code.
      assert(0);
      return "BUG";
    }

    void
    constant::accept(visitor& v) const
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
	case EmptyWord:
	  return "[*0]";
	}
      // Unreachable code.
      assert(0);
      return 0;
    }
  }
}
