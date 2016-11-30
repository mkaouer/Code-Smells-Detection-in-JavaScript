// Copyright (C) 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include "apcollect.hh"
#include "ltlvisit/postfix.hh"

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

	virtual void doit(spot::ltl::atomic_prop* ap)
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
      const_cast<formula*>(f)->accept(v);
      return s;
    }

  }

}
