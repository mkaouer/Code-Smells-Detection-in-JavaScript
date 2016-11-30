// Copyright (C) 2011 Laboratoire de Recherche et Développement de
// l'Epita (LRDE).
// Copyright (C) 2003, 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include "misc/hash.hh"
#include "dotty.hh"
#include "ltlast/visitor.hh"
#include "ltlast/allnodes.hh"
#include <ostream>
#include <sstream>

namespace spot
{
  namespace ltl
  {
    namespace
    {
      class dotty_visitor: public const_visitor
      {
      public:
	typedef Sgi::hash_map<const formula*, int, ptr_hash<formula> > map;
	dotty_visitor(std::ostream& os, map& m)
	  : os_(os), father_(-1), node_(m), sinks_(new std::ostringstream)
	{
	}

	virtual
	~dotty_visitor()
	{
	}

	void
	visit(const atomic_prop* ap)
	{
	  draw_node_(ap, ap->name(), true);
	}

	void
	visit(const constant* c)
	{
	  draw_node_(c, c->val_name(), true);
	}

	void
	visit(const binop* bo)
	{
	  if (draw_node_(bo, bo->op_name()))
	    {
	      dir = left;
	      dotty_visitor v(*this);
	      bo->first()->accept(v);
	      dir = right;
	      bo->second()->accept(*this);
	    }
	}

	void
	visit(const unop* uo)
	{
	  if (draw_node_(uo, uo->op_name()))
	    {
	      dir = none;
	      uo->child()->accept(*this);
	    }
	}

	void
	visit(const automatop*)
	{
	  assert(0);
	}

	void
	visit(const multop* mo)
	{
	  if (!draw_node_(mo, mo->op_name()))
	    return;
	  dir = none;
	  unsigned max = mo->size();
	  for (unsigned n = 0; n < max; ++n)
	    {
	      dotty_visitor v(*this);
	      mo->nth(n)->accept(v);
	    }
	}

	void finish()
	{
	  os_ << "  subgraph atoms {" << std::endl
	      << "    rank=sink;" << std::endl
	      << sinks_->str() << "  }" << std::endl;
	  delete sinks_;
	}

	enum { left, right, none } dir;

      private:
	std::ostream& os_;
	int father_;
	map& node_;
	std::ostringstream* sinks_;

	bool
	draw_node_(const formula* f, const std::string& str, bool sink = false)
	{
	  map::iterator i = node_.find(f);
	  int node;
	  bool node_exists = false;
	  if (i != node_.end())
	    {
	      node = i->second;
	      node_exists = true;
	    }
	  else
	    {
	      node = node_.size();
	      node_[f] = node;
	    }
	  // the link
	  if (father_ >= 0)
	    {
	      os_ << "  " << father_ << " -> " << node;
	      if (dir == left)
		os_ << " [taillabel=\"L\"]";
	      else if (dir == right)
		os_ << " [taillabel=\"R\"]";
	      os_ << ";" << std::endl;
	    }
	  father_ = node;

	  // the node
	  if (node_exists)
	    return false;

	  if (!sink)
	    {
	      os_ << "  " << node << " [label=\"" << str << "\"];";
	    }
	  else
	    {
	      *sinks_ << "    " << node
		      << " [label=\"" << str << "\", shape=box];\n";
	    }
	  return true;
	}
      };
    }

    std::ostream&
    dotty(std::ostream& os, const formula* f)
    {
      dotty_visitor::map m;
      dotty_visitor v(os, m);
      os << "digraph G {" << std::endl;
      f->accept(v);
      v.finish();
      os << "}" << std::endl;
      return os;
    }

  }
}
