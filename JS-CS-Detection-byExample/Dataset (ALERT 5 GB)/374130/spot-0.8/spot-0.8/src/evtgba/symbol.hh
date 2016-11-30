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

#ifndef SPOT_EVTGBA_SYMBOL_HH
# define SPOT_EVTGBA_SYMBOL_HH

#include <string>
#include <iosfwd>
#include <map>
#include <set>

namespace spot
{
  class symbol
  {
  public:
    static const symbol* instance(const std::string& name);
    const std::string& name() const;

    /// Number of instantiated atomic propositions.  For debugging.
    static unsigned instance_count();
    /// List all instances of atomic propositions.  For debugging.
    static std::ostream& dump_instances(std::ostream& os);

    void ref() const;
    void unref() const;

  protected:
    int ref_count_() const;
    symbol(const std::string* name);
    ~symbol();
    typedef std::map<const std::string, const symbol*> map;
    static map instances_;
  private:
    symbol(const symbol&); /// Undefined.
    const std::string* name_;
    mutable int refs_;
  };

  class rsymbol
  {
  public:
    rsymbol(const symbol* s): s_(s)
    {
    }

    rsymbol(const std::string& s): s_(symbol::instance(s))
    {
    }

    rsymbol(const char* s): s_(symbol::instance(s))
    {
    }

    rsymbol(const rsymbol& rs): s_(rs.s_)
    {
      s_->ref();
    }

    ~rsymbol()
    {
      s_->unref();
    }

    operator const symbol*() const
    {
      return s_;
    }

    const rsymbol&
    operator=(const rsymbol& rs)
    {
      if (this != &rs)
	{
	  this->~rsymbol();
	  new (this) rsymbol(rs);
	}
      return *this;
    }

    bool
    operator==(const rsymbol& rs) const
    {
      return s_ == rs.s_;
    }

    bool
    operator!=(const rsymbol& rs) const
    {
      return s_ != rs.s_;
    }

    bool
    operator<(const rsymbol& rs) const
    {
      return s_ < rs.s_;
    }

  private:
    const symbol* s_;
  };

  typedef std::set<const symbol*> symbol_set;
  typedef std::set<rsymbol> rsymbol_set;

}

#endif // SPOT_EVTGBA_SYMBOL_HH
