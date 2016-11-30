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

#include "common.hh"
#include "ltlvisit/destroy.hh"

namespace spot
{

  std::ostream&
  operator<<(std::ostream& os, const gspn_exeption& e)
  {
    os << e.get_where() << " exited with " << e.get_err();
    return os;
  }

  // gspn_environment
  //////////////////////////////////////////////////////////////////////

  gspn_environment::gspn_environment()
  {
  }

  gspn_environment::~gspn_environment()
  {
    for (prop_map::iterator i = props_.begin(); i != props_.end(); ++i)
      ltl::destroy(i->second);
  }

  bool
  gspn_environment::declare(const std::string& prop_str)
  {
    if (props_.find(prop_str) != props_.end())
      return false;
    props_[prop_str] = ltl::atomic_prop::instance(prop_str, *this);
    return true;
  }

  ltl::formula*
  gspn_environment::require(const std::string& prop_str)
  {
    prop_map::iterator i = props_.find(prop_str);
    if (i == props_.end())
      return 0;
    // It's an atomic_prop, so we do not have to use the clone() visitor.
    return i->second->ref();
  }

  /// Get the name of the environment.
  const std::string&
  gspn_environment::name()
  {
    static std::string name("gspn environment");
    return name;
  }

  const gspn_environment::prop_map&
  gspn_environment::get_prop_map() const
  {
    return props_;
  }

}
