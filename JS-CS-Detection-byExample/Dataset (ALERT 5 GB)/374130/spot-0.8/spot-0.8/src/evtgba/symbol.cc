// Copyright (C) 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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
#include "symbol.hh"
#include <ostream>

namespace spot
{
  symbol::map symbol::instances_;

  symbol::symbol(const std::string* name)
    : name_(name), refs_(1)
  {
  }

  symbol::~symbol()
  {
    map::iterator i = instances_.find(name());
    assert (i != instances_.end());
    instances_.erase(i);
  }

  const symbol*
  symbol::instance(const std::string& name)
  {
    map::iterator i = instances_.find(name);
    if (i != instances_.end())
      {
	const symbol* s = i->second;
	s->ref();
	return s;
      }
    // Convoluted insertion because we want the NAME member of the
    // value to point to the key.
    i = instances_.insert(map::value_type(name, 0)).first;
    i->second = new symbol(&i->first);
    return i->second;
  }

  const std::string&
  symbol::name() const
  {
    return *name_;
  }

  void
  symbol::ref() const
  {
    ++refs_;
  }

  void
  symbol::unref() const
  {
    assert(refs_ > 0);
    --refs_;
    if (!refs_)
      delete this;
  }

  int
  symbol::ref_count_() const
  {
    return refs_;
  }

  unsigned
  symbol::instance_count()
  {
    return instances_.size();
  }

  std::ostream&
  symbol::dump_instances(std::ostream& os)
  {
    for (map::iterator i = instances_.begin(); i != instances_.end(); ++i)
      {
	os << i->second << " = " << i->second->ref_count_()
	   << " * symbol(" << i->second->name() << ")" << std::endl;
      }
    return os;
  }







}
