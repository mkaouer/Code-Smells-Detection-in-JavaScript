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

#include <cassert>
#include <sstream>
#include "tostring.hh"
#include "ltlast/visitor.hh"
#include "ltlast/allnodes.hh"


namespace spot
{
  namespace ltl
  {

    class to_string_visitor : public const_visitor
    {
    public:
      to_string_visitor(std::ostream& os = std::cout)
	: os_(os)
      {
      }

      virtual
      ~to_string_visitor()
      {
      }

      void
      visit(const atomic_prop* ap)
      {
	os_ << ap->name();
      }

      void
      visit(const constant* c)
      {
	os_ << c->val_name();
      }

      void
      visit(const binop* bo)
      {
	os_ << "(";
	bo->first()->accept(*this);

	switch(bo->op())
	  {
	  case binop::Xor:
	    os_ << " ^ ";
	    break;
	  case binop::Implies:
	    os_ << " => ";
	    break;
	  case binop::Equiv:
	    os_ << " <=> ";
	    break;
	  case binop::U:
	    os_ << " U ";
	    break;
	  case binop::R:
	    os_ << " R ";
	    break;
	  }

	bo->second()->accept(*this);
	os_ << ")";
      }

      void
      visit(const unop* uo)
      {
	switch(uo->op())
	  {
	  case unop::Not:
	    os_ << "!";
	    break;
	  case unop::X:
	    os_ << "X";
	    break;
	  case unop::F:
	    os_ << "F";
	    break;
	  case unop::G:
	    os_ << "G";
	    break;
	  }

	uo->child()->accept(*this);
      }

      void
      visit(const multop* mo)
      {
	os_ << "(";
	unsigned max = mo->size();
	mo->nth(0)->accept(*this);
	const char* ch = " ";
	switch (mo->op())
	  {
	  case multop::Or:
	    ch = " | ";
	    break;
	  case multop::And:
	    ch = " & ";
	    break;
	  }

	for (unsigned n = 1; n < max; ++n)
	  {
	    os_ << ch;
	    mo->nth(n)->accept(*this);
	  }
	os_ << ")";
      }
    private:
      std::ostream& os_;
    };

    std::ostream&
    to_string(const formula* f, std::ostream& os)
    {
      to_string_visitor v(os);
      f->accept(v);
      return os;
    }

    std::string
    to_string(const formula* f)
    {
      std::ostringstream os;
      to_string(f, os);
      return os.str();
    }
  }
}
