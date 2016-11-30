// -*- coding: utf-8 -*-
// Copyright (C) 2013 Laboratoire de Recherche et Développement de
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

#include "word.hh"
#include "tgba/bddprint.hh"
#include "tgba/bdddict.hh"

namespace spot
{
  tgba_word::tgba_word(const tgba_run* run)
  {
    for (tgba_run::steps::const_iterator i = run->prefix.begin();
	 i != run->prefix.end(); ++i)
      prefix.push_back(i->label);
    for (tgba_run::steps::const_iterator i = run->cycle.begin();
	 i != run->cycle.end(); ++i)
      cycle.push_back(i->label);
  }

  void
  tgba_word::simplify()
  {
    // If all the formulas on the cycle are compatible, reduce the
    // cycle to a simple conjunction.
    //
    // For instance
    //   !a|!b; b; a&b; cycle{a; b; a&b}
    // can be reduced to
    //   !a|!b; b; a&b; cycle{a&b}
    {
      bdd all = bddtrue;
      for (seq_t::const_iterator i = cycle.begin(); i != cycle.end(); ++i)
	all &= *i;
      if (all != bddfalse)
	{
	  cycle.clear();
	  cycle.push_back(all);
	}
    }
    // If the last formula of the prefix is compatible with the
    // last formula of the cycle, we can shift the cycle and
    // reduce the prefix.
    //
    // For instance
    //   !a|!b; b; a&b; cycle{a&b}
    // can be reduced to
    //   !a|!b; cycle{a&b}
    while (!prefix.empty())
      {
	bdd a = prefix.back() & cycle.back();
	if (a == bddfalse)
	  break;
	prefix.pop_back();
	cycle.pop_back();
	cycle.push_front(a);
      }
    // Get rid of any disjunction.
    //
    // For instance
    //   !a|!b; cycle{a&b}
    // can be reduced to
    //   !a&!b; cycle{a&b}
    for (seq_t::iterator i = prefix.begin(); i != prefix.end(); ++i)
      *i = bdd_satone(*i);
    for (seq_t::iterator i = cycle.begin(); i != cycle.end(); ++i)
      *i = bdd_satone(*i);

  }

  std::ostream&
  tgba_word::print(std::ostream& os, bdd_dict* d) const
  {
    if (!prefix.empty())
      for (seq_t::const_iterator i = prefix.begin(); i != prefix.end(); ++i)
	{
	  bdd_print_formula(os, d, *i);
	  os << "; ";
	}
    assert(!cycle.empty());
    bool notfirst = false;
    os << "cycle{";
    for (seq_t::const_iterator i = cycle.begin(); i != cycle.end(); ++i)
      {
	if (notfirst)
	  os << "; ";
	notfirst = true;
	bdd_print_formula(os, d, *i);
      }
    os << "}";
    return os;
  }


}
