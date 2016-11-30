// Copyright (C) 2004, 2005, 2007 Laboratoire d'Informatique de Paris 6
// (LIP6), département Systèmes Répartis Coopératifs (SRC),
// Université Pierre et Marie Curie.
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

#include "randomgraph.hh"
#include "tgba/tgbaexplicit.hh"
#include "misc/random.hh"
#include "ltlast/atomic_prop.hh"
#include "ltlvisit/destroy.hh"
#include <sstream>
#include <list>
#include <set>
#include <iterator>

namespace spot
{

  namespace
  {
    std::string
    st(int n)
    {
      std::stringstream s;
      s << n;
      return "S" + s.str();
    }

    std::string
    acc(int n)
    {
      std::stringstream s;
      s << n;
      return "a" + s.str();
    }

    void
    random_labels(tgba_explicit* aut,
		  tgba_explicit::state* src, const tgba_explicit::state* dest,
		  int* props, int props_n, float t,
		  const std::list<bdd>& accs, float a)
    {
      int val = 0;
      int size = 0;
      bdd p = bddtrue;
      while (props_n)
	{
	  if (size == 8 * sizeof(int))
	    {
	      p &= bdd_ibuildcube(val, size, props);
	      props += size;
	      val = 0;
	      size = 0;
	    }
	  val <<= 1;
	  val |= (drand() < t);
	  ++size;
	  --props_n;
	}
      if (size > 0)
	p &= bdd_ibuildcube(val, size, props);

      bdd ac = bddfalse;
      for (std::list<bdd>::const_iterator i = accs.begin();
	   i != accs.end(); ++i)
	if (drand() < a)
	  ac |= *i;

      tgba_explicit::transition* u = aut->create_transition(src, dest);
      aut->add_conditions(u, p);
      aut->add_acceptance_conditions(u, ac);
    }
  }

  tgba*
  random_graph(int n, float d,
	       const ltl::atomic_prop_set* ap, bdd_dict* dict,
	       int n_acc, float a, float t,
	       ltl::environment* env)
  {
    assert(n > 0);
    tgba_explicit* res = new tgba_explicit(dict);

    int props_n = ap->size();
    int* props = new int[props_n];

    int pi = 0;
    for (ltl::atomic_prop_set::const_iterator i = ap->begin();
	 i != ap->end(); ++i)
      props[pi++] = dict->register_proposition(*i, res);

    std::vector<tgba_explicit::state*> states(n);

    std::list<bdd> accs;
    bdd allneg = bddtrue;
    for (int i = 0; i < n_acc; ++i)
      {
	ltl::formula* f = env->require(acc(i));
	int v = dict->register_acceptance_variable(f, res);
	res->declare_acceptance_condition(f);
	allneg &= bdd_nithvar(v);
	bdd b = bdd_ithvar(v);
	accs.push_back(b);
      }
    for (std::list<bdd>::iterator i = accs.begin(); i != accs.end(); ++i)
      *i &= bdd_exist(allneg, *i);


    // Using Sgi::hash_set instead of std::set for these sets is 3
    // times slower (tested on a 50000 nodes example).  Use an int
    // (the index into states[]), not the tgba_explicit::state*
    // directly, because the later would yield different graph
    // depending on the memory layout.
    typedef std::set<tgba_explicit::state*> node_set;
    node_set nodes_to_process;
    node_set unreachable_nodes;

    {
      tgba_explicit::state* init = res->add_state(st(0));
      states[0] = init;
      nodes_to_process.insert(init);
    }

    for (int i = 1; i < n; ++i)
      {
	tgba_explicit::state* s = res->add_state(st(i));
	states[i] = s;
	unreachable_nodes.insert(s);
      }

    // We want to connect each node to a number of successors between
    // 1 and n (with probability d).  This follow
    barand<nrand> bin(n - 1, d);

    while (!nodes_to_process.empty())
      {
	tgba_explicit::state* src = *nodes_to_process.begin();
	nodes_to_process.erase(nodes_to_process.begin());

	// Choose a random number of successors (at least one), using
	// a binomial distribution.
	int nsucc = 1 + bin.rand();

	// Connect to NSUCC randomly chosen successors.  We want at
	// least one unreachable successors among these if there are
	// some.
	bool saw_unreachable = false;
	int possibilities = n;
	while (nsucc--)
	  {
	    if (nsucc == 0
		&& !saw_unreachable
		&& !unreachable_nodes.empty())
	      {
		// Pick a random unreachable node.
		int index = mrand(unreachable_nodes.size());
		node_set::const_iterator i = unreachable_nodes.begin();
		std::advance(i, index);

		// Link it from src.
		random_labels(res, src, *i, props, props_n, t, accs, a);
		nodes_to_process.insert(*i);
		unreachable_nodes.erase(i);
		break;
	      }
	    else
	      {
		// Pick a random node.
		int index = mrand(possibilities--);
		tgba_explicit::state* dest = states[index];

		// Permute the state with states[possibilities], so we
		// cannot pick it again.
		states[index] = states[possibilities];
		states[possibilities] = dest;

		random_labels(res, src, dest, props, props_n, t, accs, a);

		node_set::iterator j = unreachable_nodes.find(dest);
		if (j != unreachable_nodes.end())
		  {
		    nodes_to_process.insert(dest);
		    unreachable_nodes.erase(j);
		    saw_unreachable = true;
		  }
	      }
	  }

	// The node must have at least one successor.
	assert(!src->empty());
      }
    // All nodes must be reachable.
    assert(unreachable_nodes.empty());
    delete[] props;
    return res;
  }

}
