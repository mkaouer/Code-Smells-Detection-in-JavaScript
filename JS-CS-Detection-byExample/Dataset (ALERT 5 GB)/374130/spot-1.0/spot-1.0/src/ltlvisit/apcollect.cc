// -*- coding: utf-8 -*-
// Copyright (C) 2012 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include "apcollect.hh"
#include "ltlvisit/postfix.hh"
#include "tgba/tgba.hh"
#include "tgba/bdddict.hh"

namespace spot
{
  namespace ltl
  {
    namespace
    {
      class atomic_prop_collector : public spot::ltl::postfix_visitor
      {
      public:
	atomic_prop_collector(atomic_prop_set* s)
	  : postfix_visitor(), sap(s)
	{
	}

	virtual ~atomic_prop_collector()
	{
	}

	virtual void doit(const spot::ltl::atomic_prop* ap)
	{
	  sap->insert(ap);
	}

      private:
	atomic_prop_set* sap;
      };
    }

    atomic_prop_set*
    atomic_prop_collect(const formula* f, atomic_prop_set* s)
    {
      if (!s)
	s = new atomic_prop_set;
      atomic_prop_collector v(s);
      f->accept(v);
      return s;
    }

    bdd
    atomic_prop_collect_as_bdd(const formula* f, const tgba* a)
    {
      spot::ltl::atomic_prop_set aps;
      atomic_prop_collect(f, &aps);
      bdd_dict* d = a->get_dict();
      bdd res = bddtrue;
      for (atomic_prop_set::const_iterator i = aps.begin();
	   i != aps.end(); ++i)
	res &= bdd_ithvar(d->register_proposition(*i, a));
      return res;
    }

  }

}
