// -*- coding: utf-8 -*-
// Copyright (C) 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
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

#include <relabel.hh>
#include <sstream>
#include "clone.hh"
#include "misc/hash.hh"
#include "ltlenv/defaultenv.hh"

namespace spot
{
  namespace ltl
  {
    namespace
    {
      class relabeler: public clone_visitor
      {
      public:
	typedef Sgi::hash_map<const atomic_prop*, const formula*,
			      ptr_hash<atomic_prop> > map;
	map newname;

	void
	visit(const atomic_prop* ap)
	{
	  map::const_iterator it = newname.find(ap);
	  if (it != newname.end())
	    result_ = it->second->clone();
	  else
	    newname[ap] = result_ = next();
	}

	virtual const formula* next() = 0;
      };

      class relabeler_pnn: public relabeler
      {
      public:
	relabeler_pnn()
	  : nn(0)
	{
	}

	unsigned nn;

	const formula* next()
	{
	  std::ostringstream s;
	  s << "p" << nn++;
	  return default_environment::instance().require(s.str());
	}
      };

      class relabeler_abc: public relabeler
      {
      public:
	relabeler_abc()
	  : nn(0)
	{
	}

	unsigned nn;

	const formula* next()
	{
	  std::string s;
	  unsigned n = nn++;
	  do
	    {
	      s.push_back('a' + (n % 26));
	      n /= 26;
	    }
	  while (n);

	  return default_environment::instance().require(s);
	}
      };
    }


    const formula*
    relabel(const formula* f, relabeling_style style)
    {
      relabeler* rel = 0;
      switch (style)
	{
	case Pnn:
	  rel = new relabeler_pnn;
	  break;
	case Abc:
	  rel = new relabeler_abc;
	  break;
	}
      f->accept(*rel);
      const formula* res = rel->result();
      delete rel;
      return res;
    }
  }
}
