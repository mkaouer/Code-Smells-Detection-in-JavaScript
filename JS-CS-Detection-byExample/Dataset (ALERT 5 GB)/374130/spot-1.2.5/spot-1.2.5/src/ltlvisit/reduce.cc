// Copyright (C) 2008, 2009, 2010, 2011, 2012 Laboratoire de Recherche
// et Développement de l'Epita (LRDE).
// Copyright (C) 2004, 2006, 2007 Laboratoire d'Informatique de
// Paris 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
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

#include <cassert>
#include "simplify.hh"
#define SKIP_DEPRECATED_WARNING
#include "reduce.hh"

namespace spot
{
  namespace ltl
  {
    const formula*
    reduce(const formula* f, int opt)
    {
      ltl_simplifier_options o;
      o.reduce_basics = opt & Reduce_Basics;
      o.synt_impl = opt & Reduce_Syntactic_Implications;
      o.event_univ = opt & Reduce_Eventuality_And_Universality;
      o.containment_checks = opt & Reduce_Containment_Checks;
      o.containment_checks_stronger = opt & Reduce_Containment_Checks_Stronger;
      ltl_simplifier simplifier(o);
      return simplifier.simplify(f);
    }

    bool
    is_eventual(const formula* f)
    {
      return f->is_eventual();
    }

    bool
    is_universal(const formula* f)
    {
      return f->is_universal();
    }
  }
}
