// Copyright (C) 2003  Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
// et Marie Curie.
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

#include "dump.hh"
#include "ltlast/visitor.hh"
#include "ltlast/allnodes.hh"


namespace spot
{
  namespace ltl
  {

    class dump_visitor : public const_visitor
    {
    public:
      dump_visitor(std::ostream& os = std::cout)
	: os_(os)
      {
      }

      virtual
      ~dump_visitor()
      {
      }

      void
      visit(const atomic_prop* ap)
      {
	os_ << "AP(" << ap->name() << ")";
      }

      void
      visit(const constant* c)
      {
	os_ << "constant(" << c->val_name() << ")";
      }

      void
      visit(const binop* bo)
      {
	os_ << "binop(" << bo->op_name() << ", ";
	bo->first()->accept(*this);
	os_ << ", ";
	bo->second()->accept(*this);
	os_ << ")";
      }

      void
      visit(const unop* uo)
      {
	os_ << "unop(" << uo->op_name() << ", ";
	uo->child()->accept(*this);
	os_ << ")";
      }

      void
      visit(const multop* mo)
      {
	os_ << "multop(" << mo->op_name() << ", ";
	unsigned max = mo->size();
	mo->nth(0)->accept(*this);
	for (unsigned n = 1; n < max; ++n)
	  {
	    std::cout << ", ";
	    mo->nth(n)->accept(*this);
	  }
	os_ << ")";
      }
    private:
      std::ostream& os_;
    };

    std::ostream&
    dump(std::ostream& os, const formula* f)
    {
      dump_visitor v(os);
      f->accept(v);
      return os;
    }

  }
}
