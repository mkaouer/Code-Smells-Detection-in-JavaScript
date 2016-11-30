// -*- coding: utf-8 -*-
// Copyright (C) 2010, 2012, 2013 Laboratoire de Recherche de
// Developpement de l'EPITA (LRDE).
// Copyright (C) 2003, 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include "config.h"
#include "refformula.hh"
#include <cassert>

namespace spot
{
  namespace ltl
  {
    ref_formula::ref_formula(opkind k)
      : formula(k), ref_counter_(0)
    {
    }

    ref_formula::~ref_formula()
    {
    }

    void
    ref_formula::ref_() const
    {
      ++ref_counter_;
    }

    bool
    ref_formula::unref_() const
    {
      assert(ref_counter_ > 0);
      return !--ref_counter_;
    }

    unsigned
    ref_formula::ref_count_() const
    {
      return ref_counter_;
    }

  }
}
