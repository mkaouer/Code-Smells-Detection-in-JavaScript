// Copyright (C) 2009, 2012 Laboratoire de Recherche et D�veloppement
// de l'Epita (LRDE).
// Copyright (C) 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
// d�partement Syst�mes R�partis Coop�ratifs (SRC), Universit� Pierre
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

#include "declenv.hh"

namespace spot
{
  namespace ltl
  {

    declarative_environment::declarative_environment()
    {
    }

    declarative_environment::~declarative_environment()
    {
      for (prop_map::iterator i = props_.begin(); i != props_.end(); ++i)
	i->second->destroy();
    }

    bool
    declarative_environment::declare(const std::string& prop_str)
    {
      if (props_.find(prop_str) != props_.end())
	return false;
      props_[prop_str] = ltl::atomic_prop::instance(prop_str, *this);
      return true;
    }

    const formula*
    declarative_environment::require(const std::string& prop_str)
    {
      prop_map::iterator i = props_.find(prop_str);
      if (i == props_.end())
	return 0;
      return i->second->clone();
    }

    const std::string&
    declarative_environment::name()
    {
      static std::string name("declarative environment");
      return name;
    }

    const declarative_environment::prop_map&
    declarative_environment::get_prop_map() const
    {
      return props_;
    }
  }
}
