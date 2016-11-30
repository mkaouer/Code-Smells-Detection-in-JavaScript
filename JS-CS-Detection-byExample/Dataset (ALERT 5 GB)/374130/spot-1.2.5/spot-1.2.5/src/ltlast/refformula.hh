// -*- coding: utf-8 -*-
// Copyright (C) 2010, 2012, 2013 Laboratoire de Recherche de
// Développement de l'EPITA (LRDE).
// Copyright (C) 2003, 2004, 2005 Laboratoire d'Informatique de Paris
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

/// \file ltlast/refformula.hh
/// \brief Reference-counted LTL formulae
#ifndef SPOT_LTLAST_REFFORMULA_HH
# define SPOT_LTLAST_REFFORMULA_HH

#include "formula.hh"

namespace spot
{
  namespace ltl
  {

    /// \ingroup ltl_ast
    /// \brief A reference-counted LTL formula.
    class SPOT_API ref_formula : public formula
    {
    protected:
      virtual ~ref_formula();
      ref_formula(opkind k);
      void ref_() const;
      bool unref_() const;
      /// Number of references to this formula.
      unsigned ref_count_() const;
    private:
      mutable unsigned ref_counter_;
    };

  }
}

#endif // SPOT_LTLAST_REFFORMULA_HH
