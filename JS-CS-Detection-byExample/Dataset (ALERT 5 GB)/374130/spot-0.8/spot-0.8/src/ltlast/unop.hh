// Copyright (C) 2009 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2003, 2004 Laboratoire d'Informatique de Paris
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

/// \file ltlast/unop.hh
/// \brief LTL unary operators
#ifndef SPOT_LTLAST_UNOP_HH
# define SPOT_LTLAST_UNOP_HH

#include <map>
#include <iosfwd>
#include "refformula.hh"

namespace spot
{
  namespace ltl
  {

    /// \brief Unary operators.
    /// \ingroup ltl_ast
    class unop : public ref_formula
    {
    public:
      enum type { Not, X, F, G, Finish }; // Finish is used in ELTL formulae.

      /// Build an unary operator with operation \a op and
      /// child \a child.
      static unop* instance(type op, formula* child);

      virtual void accept(visitor& v);
      virtual void accept(const_visitor& v) const;

      /// Get the sole operand of this operator.
      const formula* child() const;
      /// Get the sole operand of this operator.
      formula* child();

      /// Get the type of this operator.
      type op() const;
      /// Get the type of this operator, as a string.
      const char* op_name() const;

      /// Return a canonic representation of the atomic proposition
      virtual std::string dump() const;

      /// Number of instantiated unary operators.  For debugging.
      static unsigned instance_count();

      /// Dump all instances.  For debugging.
      static std::ostream& dump_instances(std::ostream& os);

    protected:
      typedef std::pair<type, formula*> pair;
      typedef std::map<pair, unop*> map;
      static map instances;

      unop(type op, formula* child);
      virtual ~unop();

    private:
      type op_;
      formula* child_;
    };

  }
}

#endif // SPOT_LTLAST_UNOP_HH
