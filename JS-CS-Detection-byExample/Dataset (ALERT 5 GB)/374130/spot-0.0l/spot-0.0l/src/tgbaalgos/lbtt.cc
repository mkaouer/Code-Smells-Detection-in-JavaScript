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

#include "misc/hash.hh"
#include <map>
#include <set>
#include <string>
#include <sstream>
#include <functional>
#include "tgba/tgba.hh"
#include "save.hh"
#include "tgba/bddprint.hh"
#include "ltlvisit/tostring.hh"
#include "tgba/bddprint.hh"
#include "misc/bddlt.hh"

namespace spot
{
  // At some point we'll need to print an acceptance set into LBTT's
  // format.  LBTT expects numbered acceptance sets, so first we'll
  // number each acceptance condition, and latter when we have to print
  // them we'll just have to look up each of them.
  class acceptance_cond_splitter
  {
  public:
    acceptance_cond_splitter(bdd all_acc)
    {
      unsigned count = 0;
      while (all_acc != bddfalse)
	{
	  bdd acc = bdd_satone(all_acc);
	  all_acc -= acc;
	  sm[acc] = count++;
	}
    }

    std::ostream&
    split(std::ostream& os, bdd b)
    {
      while (b != bddfalse)
	{
	  bdd acc = bdd_satone(b);
	  b -= acc;
	  os << sm[acc] << " ";
	}
      return os;
    }

    unsigned
    count() const
    {
      return sm.size();
    }

  private:
    typedef std::map<bdd, unsigned, bdd_less_than> split_map;
    split_map sm;
  };

  // Convert a BDD formula to the syntax used by LBTT's transition guards.
  // Conjunctions are printed by bdd_format_sat, so we just have
  // to handle the other cases.
  static std::string
  bdd_to_lbtt(bdd b, const bdd_dict* d)
  {
    if (b == bddfalse)
      return "f";
    else if (b == bddtrue)
      return "t";
    else
      {
	bdd cube = bdd_satone(b);
	b -= cube;
	if (b != bddfalse)
	  {
	    return "| " + bdd_to_lbtt(b, d) + " " + bdd_to_lbtt(cube, d);
	  }
	else
	  {
	    std::string res = "";
	    for (int count = bdd_nodecount(cube); count > 1; --count)
	      res += "& ";
	    return res + bdd_format_sat(d, cube);
	  }
      }

  }

  // Each state in the produced automata corresponds to
  // a (state, acceptance set) pair for the source automata.

  typedef std::pair<state*, bdd> state_acc_pair;

  struct state_acc_pair_equal :
    public std::binary_function<const state_acc_pair&,
                                const state_acc_pair&, bool>
  {
    bool
    operator()(const state_acc_pair& left, const state_acc_pair& right) const
    {
      if (left.first->compare(right.first))
	return false;
      return left.second.id() == right.second.id();
    }
  };

  struct state_acc_pair_hash :
    public std::unary_function<const state_acc_pair&, size_t>
  {
    bool
    operator()(const state_acc_pair& that) const
    {
      // We assume there will be far more states than acceptance conditions.
      // Hence we keep only 8 bits for the latter.
      return (that.first->hash() << 8) + (that.second.id() & 0xFF);
    }
  };

  // Each state of the produced automata is numbered.  Map of state seen.
  typedef Sgi::hash_map<state_acc_pair, unsigned, state_acc_pair_hash,
			state_acc_pair_equal> acp_seen_map;

  // Set of states yet to produce.
  typedef Sgi::hash_set<state_acc_pair, state_acc_pair_hash,
			state_acc_pair_equal> todo_set;

  // Each *source* state corresponds to several states in the produced
  // automata.  A minmax_pair specifies the range of such associated states.
  typedef std::pair<unsigned, unsigned> minmax_pair;
  typedef Sgi::hash_map<state*, minmax_pair,
			state_ptr_hash, state_ptr_equal> seen_map;

  // Take a STATE from the source automaton, and fill TODO with
  // the list of associated states to output.  Return the correponding
  // range in MMP.  Update SEEN, ACP_SEEN, and STATE_NUMBER.
  //
  // INIT must be set to true when registering the initial state.
  // This allows us to create an additional state if required.  (LBTT
  // supports only one initial state, so whenever the initial state
  // of the source automaton has to be split, we need to create
  // a supplementary state, to act as initial state for LBTT.)
  void
  fill_todo(todo_set& todo, seen_map& seen, acp_seen_map& acp_seen,
	    state* state, const tgba* g,
	    minmax_pair& mmp, unsigned& state_number,
	    bool init)
  {
    typedef std::set<bdd, bdd_less_than> bdd_set;

    seen_map::iterator i = seen.find(state);
    if (i != seen.end())
      {
	mmp = i->second;
	delete state;
	return;
      }

    // Browse the successors of STATE to gather acceptance
    // conditions of outgoing transitions.
    bdd_set acc_seen;
    tgba_succ_iterator* si = g->succ_iter(state);
    for (si->first(); !si->done(); si->next())
      {
	acc_seen.insert(si->current_acceptance_conditions());
      }

    // Order the creation of the supplementary initial state if needed.
    // Use bddtrue as acceptance condition because it cannot conflict
    // with other (state, acceptance cond) pairs in the maps.
    if (init && acc_seen.size() > 1)
      {
	state_acc_pair p(state, bddtrue);
	todo.insert(p);
	acp_seen[p] = state_number++;
      }

    // Order the creation of normal states.
    mmp.first = state_number;
    for (bdd_set::iterator i = acc_seen.begin(); i != acc_seen.end(); ++i)
      {
	state_acc_pair p(state, *i);
	todo.insert(p);
	acp_seen[p] = state_number++;
      }
    mmp.second = state_number;
    seen[state] = mmp;
  }

  std::ostream&
  lbtt_reachable(std::ostream& os, const tgba* g)
  {
    const bdd_dict* d = g->get_dict();
    std::ostringstream body;

    seen_map seen;
    acp_seen_map acp_seen;
    todo_set todo;
    unsigned state_number = 0;

    minmax_pair mmp;

    fill_todo(todo, seen, acp_seen,
	      g->get_init_state(), g, mmp, state_number, true);
    acceptance_cond_splitter acs(g->all_acceptance_conditions());

    while(! todo.empty())
      {
	state_acc_pair sap = *todo.begin();
	todo.erase(todo.begin());
	unsigned number = acp_seen[sap];

	// number == 0 is the initial state.  bddtrue as an acceptance
	// conditions indicates a "fake" initial state introduced
	// because the original initial state was split into many
	// states (with different acceptance conditions).
	// As this "fake" state has no input transitions, there is
	// no point in computing any acceptance conditions.
	body << number << (number ? " 0 " : " 1 ");
	if (sap.second != bddtrue)
	  acs.split(body, sap.second);
	body << "-1" << std::endl;

	tgba_succ_iterator* si = g->succ_iter(sap.first);
	for (si->first(); !si->done(); si->next())
	  {
	    // We have put the acceptance conditions on the state,
	    // so draw only outgoing transition with these acceptance
	    // conditions.

	    if (sap.second != bddtrue
		&& si->current_acceptance_conditions() != sap.second)
	      continue;

	    minmax_pair destrange;
	    fill_todo(todo, seen, acp_seen,
		      si->current_state(), g, destrange, state_number, false);

	    // Link to all instances of the successor.
	    std::string s = bdd_to_lbtt(si->current_condition(), d);
	    for (unsigned i = destrange.first; i < destrange.second; ++i)
	      {
		body << i << " " << s << std::endl;
	      }
	  }
	body << "-1" << std::endl;
	delete si;
      }

    os << state_number << " " << acs.count() << std::endl;
    os << body.str();
    // Finally delete all states used as keys in m.
    seen_map::const_iterator s = seen.begin();
    while (s != seen.end())
      {
	// Advance the iterator before deleting the "key" pointer.
	const state* ptr = s->first;
	++s;
	delete ptr;
      }
    return os;
  }
}
