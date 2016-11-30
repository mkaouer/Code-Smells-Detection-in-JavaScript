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

#include <iterator>
#include <cassert>
#include "magic.hh"
#include "tgba/bddprint.hh"

namespace spot
{
  magic_search::magic_search(const tgba_tba_proxy* a)
    : a(a), x(0)
  {
  }

  magic_search::~magic_search()
  {
    hash_type::const_iterator s = h.begin();
    while (s != h.end())
      {
	// Advance the iterator before deleting the "key" pointer.
	const state* ptr = s->first;
	++s;
	delete ptr;
      }
    if (x)
      delete x;
  }

  void
  magic_search::push(const state* s, bool m)
  {
    tgba_succ_iterator* i = a->succ_iter(s);
    i->first();

    hash_type::iterator hi = h.find(s);
    if (hi == h.end())
      {
	magic d = { !m, m };
	h[s] = d;
      }
    else
      {
	hi->second.seen_without |= !m;
	hi->second.seen_with |= m;
	if (hi->first != s)
	  delete s;
	s = hi->first;
      }

    magic_state ms = { s, m };
    stack.push_front(state_iter_pair(ms, i));
  }

  bool
  magic_search::has(const state* s, bool m) const
  {
    hash_type::const_iterator i = h.find(s);
    if (i == h.end())
      return false;
    if (!m && i->second.seen_without)
      return true;
    if (m && i->second.seen_with)
      return true;
    return false;
  }

  bool
  magic_search::check()
  {
    if (stack.empty())
      // It's a new search.
      push(a->get_init_state(), false);
    else
      // Remove the transition to the cycle root.
      tstack.pop_front();

    assert(stack.size() == 1 + tstack.size());

    while (! stack.empty())
      {
      recurse:
	magic_search::state_iter_pair& p = stack.front();
	tgba_succ_iterator* i = p.second;
	const bool magic = p.first.m;

	while (! i->done())
	  {
	    const state* s_prime = i->current_state();
	    bdd c = i->current_condition();
	    i->next();
	    if (magic && 0 == s_prime->compare(x))
	      {
		delete s_prime;
		tstack.push_front(c);
		assert(stack.size() == tstack.size());
		return true;
	      }
	    if (! has(s_prime, magic))
	      {
		push(s_prime, magic);
		tstack.push_front(c);
		goto recurse;
	      }
	    delete s_prime;
	  }

	const state* s = p.first.s;
	stack.pop_front();

	if (! magic && a->state_is_accepting(s))
	  {
	    if (! has(s, true))
	      {
		if (x)
		  delete x;
		x = s->clone();
		push(s, true);
		continue;
	      }
	  }
	if (! stack.empty())
	  tstack.pop_front();
      }

    assert(tstack.empty());
    return false;
  }

  std::ostream&
  magic_search::print_result(std::ostream& os, const tgba* restrict) const
  {
    stack_type::const_reverse_iterator i;
    tstack_type::const_reverse_iterator ti;
    os << "Prefix:" << std::endl;
    const bdd_dict* d = a->get_dict();
    for (i = stack.rbegin(), ti = tstack.rbegin();
	 i != stack.rend(); ++i, ++ti)
      {
	if (i->first.s->compare(x) == 0)
	  os <<"Cycle:" <<std::endl;

	const state* s = i->first.s;
	if (restrict)
	  {
	    s = a->project_state(s, restrict);
	    assert(s);
	    os << "  " << restrict->format_state(s) << std::endl;
	    delete s;
	  }
	else
	  {
	    os << "  " << a->format_state(s) << std::endl;
	  }
	os << "    | " << bdd_format_set(d, *ti) << std::endl;
      }

    if (restrict)
      {
	const state* s = a->project_state(x, restrict);
	assert(s);
	os << "  " << restrict->format_state(s) << std::endl;
	delete s;
      }
    else
      {
	os << "  " << a->format_state(x) << std::endl;
      }
    return os;
  }

}
