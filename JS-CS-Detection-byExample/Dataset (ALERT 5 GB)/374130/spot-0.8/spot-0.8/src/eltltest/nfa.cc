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

#include <string>
#include <set>
#include <iostream>
#include "ltlast/formula_tree.hh"
#include "ltlast/nfa.hh"

using namespace spot::ltl;

typedef std::set<const nfa::state*> mset;

void
dfs(nfa& a, const nfa::state* s, mset& m)
{
  if (m.find(s) != m.end())
    return;
  m.insert(s);

  for (nfa::iterator i = a.begin(s); i != a.end(s); ++i)
  {
    std::cout << (*i)->lbl << std::endl;
    dfs(a, (*i)->dst, m);
  }
}

int
main()
{
  nfa a;

  formula_tree::node_atomic* n1 = new formula_tree::node_atomic;
  formula_tree::node_atomic* n2 = new formula_tree::node_atomic;
  n1->i = 1;
  n2->i = 2;

  a.add_transition(0, 1, formula_tree::node_ptr(n1));
  a.add_transition(1, 2, formula_tree::node_ptr(n2));

  std::cout << "init: " << a.format_state(a.get_init_state()) << std::endl;

  mset m;
  dfs(a, a.get_init_state(), m);
}
