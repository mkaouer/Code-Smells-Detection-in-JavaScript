// -*- coding: utf-8 -*-
// Copyright (C) 2008, 2009, 2011, 2012 Laboratoire de Recherche et
// Développement de l'Epita.
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

#include <queue>
#include <set>
#include <iostream>
#include <sstream>
#include "scc.hh"
#include "tgba/bddprint.hh"
#include "misc/escape.hh"
#include "misc/accconv.hh"

namespace spot
{
  std::ostream&
  scc_stats::dump(std::ostream& out) const
  {
    out << "total SCCs: " << scc_total << std::endl;
    out << "accepting SCCs: " << acc_scc << std::endl;
    out << "dead SCCs: " << dead_scc << std::endl;
    out << "accepting paths: " << acc_paths << std::endl;
    out << "dead paths: " << dead_paths << std::endl;
    return out;
  }


  scc_map::scc_map(const tgba* aut)
    : aut_(aut)
  {
  }

  scc_map::~scc_map()
  {
    hash_type::iterator i = h_.begin();

    while (i != h_.end())
      {
	// Advance the iterator before deleting the key.
	const state* s = i->first;
	++i;
	s->destroy();
      }
  }

  unsigned
  scc_map::initial() const
  {
    state* in = aut_->get_init_state();
    int val = scc_of_state(in);
    in->destroy();
    return val;
  }

  const scc_map::succ_type&
  scc_map::succ(unsigned n) const
  {
    assert(scc_map_.size() > n);
    return scc_map_[n].succ;
  }

  bool
  scc_map::trivial(unsigned n) const
  {
    assert(scc_map_.size() > n);
    return scc_map_[n].trivial;
  }

  bool
  scc_map::accepting(unsigned n) const
  {
    if (scc_map_[n].trivial)
      return false;
    return acc_set_of(n) == aut_->all_acceptance_conditions();
  }

  const tgba*
  scc_map::get_aut() const
  {
    return aut_;
  }


  int
  scc_map::relabel_component()
  {
    assert(!root_.front().states.empty());
    std::list<const state*>::iterator i;
    int n = scc_map_.size();
    for (i = root_.front().states.begin(); i != root_.front().states.end(); ++i)
      {
	hash_type::iterator spi = h_.find(*i);
	assert(spi != h_.end());
	assert(spi->first == *i);
	assert(spi->second < 0);
	spi->second = n;
      }
    scc_map_.push_back(root_.front());
    return n;
  }

  // recursively update supp rec
  bdd
  scc_map::update_supp_rec(unsigned state)
  {
    assert(scc_map_.size() > state);

    bdd& res = scc_map_[state].supp_rec;

    if (res == bddfalse)
      {
	const succ_type& s = succ(state);
	succ_type::const_iterator it;

	res = scc_map_[state].supp;

	for (it = s.begin(); it != s.end(); ++it)
	  res &= update_supp_rec(it->first) & bdd_support(it->second);
      }

    return res;
  }

  void
  scc_map::build_map()
  {
    acceptance_convertor conv(aut_->neg_acceptance_conditions());

    // Setup depth-first search from the initial state.
    {
      self_loops_ = 0;
      state* init = aut_->get_init_state();
      num_ = -1;
      h_.insert(std::make_pair(init, num_));
      root_.push_front(scc(num_));
      arc_acc_.push(bddfalse);
      arc_cond_.push(bddfalse);
      tgba_succ_iterator* iter = aut_->succ_iter(init);
      iter->first();
      todo_.push(pair_state_iter(init, iter));
    }

    while (!todo_.empty())
      {
	assert(root_.size() == arc_acc_.size());
	assert(root_.size() == arc_cond_.size());

	// We are looking at the next successor in SUCC.
	tgba_succ_iterator* succ = todo_.top().second;

	// If there is no more successor, backtrack.
	if (succ->done())
	  {
	    // We have explored all successors of state CURR.
	    const state* curr = todo_.top().first;

	    // Backtrack TODO_.
	    todo_.pop();

	    // Fill rem with any component removed, so that
	    // remove_component() does not have to traverse the SCC
	    // again.
	    hash_type::const_iterator spi = h_.find(curr);
	    assert(spi != h_.end());
	    root_.front().states.push_front(spi->first);

	    // When backtracking the root of an SCC, we must also
	    // remove that SCC from the ARC/ROOT stacks.  We must
	    // discard from H all reachable states from this SCC.
	    assert(!root_.empty());
	    if (root_.front().index == spi->second)
	      {
		assert(!arc_acc_.empty());
		assert(arc_cond_.size() == arc_acc_.size());
		bdd cond = arc_cond_.top();
		arc_cond_.pop();
		arc_acc_.pop();
		int num = relabel_component();
		root_.pop_front();

		// Record the transition between the SCC being popped
		// and the previous SCC.
		if (!root_.empty())
		  root_.front().succ.insert(std::make_pair(num, cond));
	      }

	    delete succ;
	    // Do not destroy CURR: it is a key in H.
	    continue;
	  }

	// We have a successor to look at.
	// Fetch the values we are interested in...
	const state* dest = succ->current_state();
	if (!dest->compare(todo_.top().first))
	  ++self_loops_;
	bdd acc = succ->current_acceptance_conditions();
	bdd cond = succ->current_condition();
	root_.front().supp &= bdd_support(cond);
	// ... and point the iterator to the next successor, for
	// the next iteration.
	succ->next();
	// We do not need SUCC from now on.

	// Are we going to a new state?
	hash_type::const_iterator spi = h_.find(dest);
	if (spi == h_.end())
	  {
	    // Yes.  Number it, stack it, and register its successors
	    // for later processing.
	    h_.insert(std::make_pair(dest, --num_));
	    root_.push_front(scc(num_));
	    arc_acc_.push(acc);
	    arc_cond_.push(cond);
	    tgba_succ_iterator* iter = aut_->succ_iter(dest);
	    iter->first();
	    todo_.push(pair_state_iter(dest, iter));
	    continue;
	  }

	// If we know the state, reuse the previous object.
	dest->destroy();
	dest = spi->first;

	// Have we reached a maximal SCC?
	if (spi->second >= 0)
	  {
	    int dest = spi->second;
	    // Record that there is a transition from this SCC to the
	    // dest SCC labelled with cond.
	    succ_type::iterator i = root_.front().succ.find(dest);
	    if (i == root_.front().succ.end())
	      root_.front().succ.insert(std::make_pair(dest, cond));
	    else
	      i->second |= cond;

	    continue;
	  }

	// Now this is the most interesting case.  We have reached a
	// state S1 which is already part of a non-dead SCC.  Any such
	// non-dead SCC has necessarily been crossed by our path to
	// this state: there is a state S2 in our path which belongs
	// to this SCC too.  We are going to merge all states between
	// this S1 and S2 into this SCC.
	//
	// This merge is easy to do because the order of the SCC in
	// ROOT is descending: we just have to merge all SCCs from the
	// top of ROOT that have an index lesser than the one of
	// the SCC of S2 (called the "threshold").
	int threshold = spi->second;
	std::list<const state*> states;
	succ_type succs;
	cond_set conds;
	conds.insert(cond);
	bdd supp = bddtrue;
	bdd all = aut_->all_acceptance_conditions();
	bdd useful = conv.as_full_product(acc);
	while (threshold > root_.front().index)
	  {
	    assert(!root_.empty());
	    assert(!arc_acc_.empty());
	    assert(arc_acc_.size() == arc_cond_.size());
	    acc |= root_.front().acc;
	    bdd lacc = arc_acc_.top();
	    acc |= lacc;
	    useful |= conv.as_full_product(lacc) | root_.front().useful_acc;
	    states.splice(states.end(), root_.front().states);
	    succs.insert(root_.front().succ.begin(),
			 root_.front().succ.end());
	    conds.insert(arc_cond_.top());
	    conds.insert(root_.front().conds.begin(),
			 root_.front().conds.end());
	    supp &= root_.front().supp;
	    root_.pop_front();
	    arc_acc_.pop();
	    arc_cond_.pop();
	  }

	// Note that we do not always have
	//  threshold == root_.front().index
	// after this loop, the SCC whose index is threshold might have
	// been merged with a higher SCC.

	// Accumulate all acceptance conditions, states, SCC
	// successors, and conditions into the merged SCC.
	root_.front().acc |= acc;
	root_.front().states.splice(root_.front().states.end(), states);
	root_.front().succ.insert(succs.begin(), succs.end());
	root_.front().conds.insert(conds.begin(), conds.end());
	root_.front().supp &= supp;
	// This SCC is no longer trivial.
	root_.front().trivial = false;
	root_.front().useful_acc |= useful;
      }

    // recursively update supp_rec
    (void) update_supp_rec(initial());
  }

  unsigned scc_map::scc_of_state(const state* s) const
  {
    hash_type::const_iterator i = h_.find(s);
    assert(i != h_.end());
    return i->second;
  }

  const scc_map::cond_set& scc_map::cond_set_of(unsigned n) const
  {
    assert(scc_map_.size() > n);
    return scc_map_[n].conds;
  }

  bdd scc_map::ap_set_of(unsigned n) const
  {
    assert(scc_map_.size() > n);
    return scc_map_[n].supp;
  }


  bdd scc_map::aprec_set_of(unsigned n) const
  {
    assert(scc_map_.size() > n);
    return scc_map_[n].supp_rec;
  }


  bdd scc_map::acc_set_of(unsigned n) const
  {
    assert(scc_map_.size() > n);
    return scc_map_[n].acc;
  }

  unsigned scc_map::self_loops() const
  {
    return self_loops_;
  }

  const std::list<const state*>& scc_map::states_of(unsigned n) const
  {
    assert(scc_map_.size() > n);
    return scc_map_[n].states;
  }

  const state* scc_map::one_state_of(unsigned n) const
  {
    assert(scc_map_.size() > n);
    return scc_map_[n].states.front();
  }

  unsigned scc_map::scc_count() const
  {
    return scc_map_.size();
  }

  bdd
  scc_map::useful_acc_of(unsigned n) const
  {
    assert(scc_map_.size() > n);
    return scc_map_[n].useful_acc;
  }

  namespace
  {
    struct scc_recurse_data
    {
      scc_recurse_data() : acc_scc(0), dead_scc(0) {};
      typedef std::map<unsigned, unsigned> graph_counter;
      graph_counter acc_paths;
      graph_counter dead_paths;
      unsigned acc_scc;
      unsigned dead_scc;
    };

    bool scc_recurse(const scc_map& m, unsigned state, scc_recurse_data& data)
    {
      // Don't recurse on previously visited states.
      scc_recurse_data::graph_counter::const_iterator i =
	data.acc_paths.find(state);
      if (i != data.acc_paths.end())
	return i->second > 0;

      const scc_map::succ_type& succ = m.succ(state);

      bool accepting = m.accepting(state);
      scc_map::succ_type::const_iterator it;
      unsigned acc_paths = 0;
      unsigned dead_paths = 0;

      bool paths_accepting = false;
      for (it = succ.begin(); it != succ.end(); ++it)
	{
	  unsigned dest = it->first;
	  bool path_accepting = scc_recurse(m, dest, data);
	  paths_accepting |= path_accepting;

	  acc_paths += data.acc_paths[dest];
	  dead_paths += data.dead_paths[dest];
	}

      if (accepting)
	{
	  ++data.acc_scc;
	  if (acc_paths == 0)
	    acc_paths = 1;
	}
      else if (!paths_accepting)
	{
	  ++data.dead_scc;
	  if (dead_paths == 0)
	    dead_paths = 1;
	}

      data.acc_paths[state] = acc_paths;
      data.dead_paths[state] = dead_paths;

      return accepting | paths_accepting;
    }

  }

  scc_stats build_scc_stats(const scc_map& m)
  {
    scc_stats res;
    res.self_loops = m.self_loops();
    res.scc_total = m.scc_count();

    scc_recurse_data d;
    unsigned init = m.initial();
    scc_recurse(m, init, d);

    res.acc_scc = d.acc_scc;
    res.dead_scc = d.dead_scc;
    res.acc_paths = d.acc_paths[init];
    res.dead_paths = d.dead_paths[init];

    res.useless_scc_map.reserve(res.scc_total);
    res.useful_acc = bddfalse;
    for (unsigned n = 0; n < res.scc_total; ++n)
      {
	res.useless_scc_map[n] = !d.acc_paths[n];
	if (m.accepting(n))
	  res.useful_acc |= m.useful_acc_of(n);
      }
    return res;
  }

  scc_stats
  build_scc_stats(const tgba* a)
  {
    scc_map m(a);
    m.build_map();
    return build_scc_stats(m);
  }

  std::ostream&
  dump_scc_dot(const scc_map& m, std::ostream& out, bool verbose)
  {
    out << "digraph G {\n  i [label=\"\", style=invis, height=0]" << std::endl;
    int start = m.initial();
    out << "  i -> " << start << std::endl;

    std::vector<bool> seen(m.scc_count());
    seen[start] = true;

    std::queue<int> q;
    q.push(start);
    while (!q.empty())
      {
	int state = q.front();
	q.pop();

	const scc_map::cond_set& cs = m.cond_set_of(state);

	std::ostringstream ostr;
	ostr << state;
	if (verbose)
	  {
	    size_t n = m.states_of(state).size();
	    ostr << " (" << n << " state";
	    if (n > 1)
	      ostr << "s";
	    ostr << ")\\naccs=";
	    escape_str(ostr, bdd_format_accset(m.get_aut()->get_dict(),
					       m.acc_set_of(state)));
	    ostr << "\\nconds=[";
	    for (scc_map::cond_set::const_iterator i = cs.begin();
		 i != cs.end(); ++i)
	      {
		if (i != cs.begin())
		  ostr << ", ";
		escape_str(ostr,
			   bdd_format_formula(m.get_aut()->get_dict(), *i));
	      }
	    ostr << "]\\n AP=[";
	    escape_str(ostr,
		       bdd_format_sat(m.get_aut()->get_dict(),
				      m.ap_set_of(state)));
	    ostr << "]\\n APrec=[";
	    escape_str(ostr, bdd_format_sat(m.get_aut()->get_dict(),
					    m.aprec_set_of(state)));
	    ostr << "]\\n useful=[";
	    escape_str(ostr, bdd_format_accset(m.get_aut()->get_dict(),
					       m.useful_acc_of(state))) << "]";
	  }

	out << "  " << state << " [shape=box,"
            << (m.accepting(state) ? "style=bold," : "")
            << "label=\"" << ostr.str() << "\"]" << std::endl;

	const scc_map::succ_type& succ = m.succ(state);

	scc_map::succ_type::const_iterator it;
	for (it = succ.begin(); it != succ.end(); ++it)
	  {
	    int dest = it->first;
	    bdd label = it->second;

	    out << "  " << state << " -> " << dest
		<< " [label=\"";
	    escape_str(out, bdd_format_formula(m.get_aut()->get_dict(), label));
	    out << "\"]" << std::endl;

	    if (seen[dest])
	      continue;

	    seen[dest] = true;
	    q.push(dest);
	  }
      }

    out << "}" << std::endl;

    return out;
  }

  std::ostream&
  dump_scc_dot(const tgba* a, std::ostream& out, bool verbose)
  {
    scc_map m(a);
    m.build_map();
    return dump_scc_dot(m, out, verbose);
  }

}
