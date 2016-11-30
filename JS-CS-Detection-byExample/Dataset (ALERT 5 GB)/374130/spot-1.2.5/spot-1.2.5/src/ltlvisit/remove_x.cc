// Copyright (C) 2013 Laboratoire de Recherche et Developpement de
// l'Epita (LRDE).
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

#include "ltlast/allnodes.hh"
#include "ltlvisit/simplify.hh"
#include "ltlvisit/clone.hh"
#include "ltlvisit/apcollect.hh"
#include "ltlvisit/remove_x.hh"

namespace spot
{
  namespace ltl
  {
    namespace
    {

#define AND(x, y) multop::instance(multop::And, (x), (y))
#define OR(x, y)  multop::instance(multop::Or, (x), (y))
#define NOT(x)    unop::instance(unop::Not, (x))
#define G(x)      unop::instance(unop::G, (x))
#define U(x, y)   binop::instance(binop::U, (x), (y))

      class remove_x_visitor : public clone_visitor
      {
	typedef clone_visitor super;
	atomic_prop_set aps;

      public:
	remove_x_visitor(const formula* f)
	{
	  atomic_prop_collect(f, &aps);
	}

	virtual
	~remove_x_visitor()
	{
	}

	using super::visit;
	void visit(const unop* uo)
	{
	  const formula* c = recurse(uo->child());

	  unop::type op = uo->op();
	  if (op != unop::X)
	    {
	      result_ = unop::instance(op, c);
	      return;
	    }
	  multop::vec* vo = new multop::vec;
	  for (atomic_prop_set::const_iterator i = aps.begin();
	       i != aps.end(); ++i)
	    {
	      // First line
	      multop::vec* va1 = new multop::vec;
	      const formula* npi = NOT((*i)->clone());
	      va1->push_back((*i)->clone());
	      va1->push_back(U((*i)->clone(), AND(npi, c->clone())));
	      for (atomic_prop_set::const_iterator j = aps.begin();
		   j != aps.end(); ++j)
		if (*j != *i)
		  va1->push_back(OR(U((*j)->clone(), npi->clone()),
				    U(NOT((*j)->clone()), npi->clone())));
	      vo->push_back(multop::instance(multop::And, va1));
	      // Second line
	      multop::vec* va2 = new multop::vec;
	      va2->push_back(npi->clone());
	      va2->push_back(U(npi->clone(), AND((*i)->clone(), c->clone())));
	      for (atomic_prop_set::const_iterator j = aps.begin();
		   j != aps.end(); ++j)
		if (*j != *i)
		  va2->push_back(OR(U((*j)->clone(), (*i)->clone()),
				    U(NOT((*j)->clone()), (*i)->clone())));
	      vo->push_back(multop::instance(multop::And, va2));
	    }
	  const formula* l12 = multop::instance(multop::Or, vo);
	  // Third line
	  multop::vec* va3 = new multop::vec;
	  for (atomic_prop_set::const_iterator i = aps.begin();
	       i != aps.end(); ++i)
	    {
	      va3->push_back(OR(G((*i)->clone()),
				G(NOT((*i)->clone()))));
	    }
	  result_ = OR(l12, AND(multop::instance(multop::And, va3), c));
	  return;
	}

	virtual const formula* recurse(const formula* f)
	{
	  if (f->is_X_free())
	    return f->clone();
	  f->accept(*this);
	  return this->result();
	}
      };

    }

    const formula* remove_x(const formula* f)
    {
      remove_x_visitor v(f);
      return v.recurse(f);
    }

    bool is_stutter_insensitive(const formula* f)
    {
      assert(f->is_ltl_formula());
      if (f->is_X_free())
	return true;
      const formula* g = remove_x(f);
      ltl_simplifier ls;
      bool res = ls.are_equivalent(f, g);
      g->destroy();
      return res;
    }

  }
}
