// -*- coding: utf-8 -*-
// Copyright (C) 2009, 2010, 2011, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2003, 2004 Laboratoire d'Informatique de Paris 6
// (LIP6), département Systèmes Répartis Coopératifs (SRC), Université
// Pierre et Marie Curie.
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
      class dotty_visitor: public visitor
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
	visit(const bunop* so)
	{
	  if (draw_node_(so, so->format()))
	    {
	      childnum = 0;
	      so->child()->accept(*this);
	    }
	}

	void
	visit(const binop* bo)
	{
	  if (draw_node_(bo, bo->op_name()))
	    {
	      childnum = -1;
	      dotty_visitor v(*this);
	      bo->first()->accept(v);
	      childnum = -2;
	      bo->second()->accept(*this);
	    }
	}

	void
	visit(const unop* uo)
	{
	  if (draw_node_(uo, uo->op_name()))
	    {
	      childnum = 0;
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
	  childnum = 0;
	  unsigned max = mo->size();
	  multop::type op = mo->op();
	  bool update_childnum = (op == multop::Fusion ||
				  op == multop::Concat);

	  for (unsigned n = 0; n < max; ++n)
	    {
	      if (update_childnum)
		++childnum;
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

	int childnum;

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
	      if (childnum > 0)
		os_ << " [taillabel=\"" << childnum << "\"]";
	      if (childnum == -1)
		os_ << " [taillabel=\"L\"]";
	      else if (childnum == -2)
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
