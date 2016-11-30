// Copyright (C) 2008 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
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
#include "nfa.hh"
#include "formula_tree.hh"

namespace spot
{
  namespace ltl
  {
    nfa::nfa()
      : is_(), si_(), arity_(0), name_(), init_(0), finals_()
    {
    }

    nfa::~nfa()
    {
      is_map::iterator i;
      for (i = is_.begin(); i != is_.end(); ++i)
      {
	state::iterator i2;
	for (i2 = i->second->begin(); i2 != i->second->end(); ++i2)
	  delete *i2;
	delete i->second;
      }
    }

    nfa::state*
    nfa::add_state(int name)
    {
      is_map::iterator i = is_.find(name);
      if (i == is_.end())
      {
	state* s = new nfa::state;
	is_[name] = s;
	si_[s] = name;

	if (!init_)
	  init_ = s;
	return s;
      }
      return i->second;
    }

    void
    nfa::add_transition(int src, int dst, const label lbl)
    {
      state* s = add_state(src);
      nfa::transition* t = new transition;
      t->dst = add_state(dst);
      t->lbl = lbl;
      s->push_back(t);
      size_t arity = formula_tree::arity(formula_tree::node_ptr(lbl));
      if (arity >= arity_)
	arity_ = arity;
    }

    void
    nfa::set_init_state(int name)
    {
      init_ = add_state(name);
    }

    void
    nfa::set_final(int name)
    {
      finals_.insert(name);
    }

    bool
    nfa::is_final(const state* s)
    {
      return finals_.find(format_state(s)) != finals_.end();
    }

    bool
    nfa::is_loop()
    {
      return finals_.empty();
    }

    unsigned
    nfa::arity()
    {
      return arity_;
    }

    const nfa::state*
    nfa::get_init_state()
    {
      if (!init_)
	assert(0);
      return init_;
    }

    nfa::iterator
    nfa::begin(const state* s) const
    {
      return nfa::iterator(s->begin());
    }

    nfa::iterator
    nfa::end(const state* s) const
    {
      return nfa::iterator(s->end());
    }

    int
    nfa::format_state(const state* s) const
    {
      si_map::const_iterator i = si_.find(s);
      assert(i != si_.end());
      return i->second;
    }

    const std::string&
    nfa::get_name() const
    {
      return name_;
    }

    void
    nfa::set_name(const std::string& name)
    {
      name_ = name;
    }
  }
}
