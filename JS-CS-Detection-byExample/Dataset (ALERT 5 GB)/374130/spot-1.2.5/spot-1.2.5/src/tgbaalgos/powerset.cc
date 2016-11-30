// Copyright (C) 2009, 2010, 2011, 2013 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
// département Systèmes Répartis Coopératifs (SRC), Université Pierre
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

#include <set>
#include <deque>
#include <iterator>
#include <vector>
#include "powerset.hh"
#include "misc/hash.hh"
#include "tgbaalgos/powerset.hh"
#include "bdd.h"
#include "tgbaalgos/scc.hh"
#include "tgbaalgos/cycles.hh"
#include "tgbaalgos/gtec/gtec.hh"
#include "tgba/tgbaproduct.hh"
#include "tgba/bddprint.hh"
#include "tgbaalgos/dotty.hh"
#include "tgbaalgos/gtec/gtec.hh"
#include "tgbaalgos/sccfilter.hh"
#include "tgbaalgos/ltl2tgba_fm.hh"
#include "tgbaalgos/dtgbacomp.hh"
#include "ltlast/unop.hh"

namespace spot
{
  tgba_explicit_number*
  tgba_powerset(const tgba* aut, power_map& pm, bool merge)
  {
    typedef power_map::power_state power_state;
    typedef std::map<power_map::power_state, int> power_set;
    typedef std::deque<power_map::power_state> todo_list;

    power_set seen;
    todo_list todo;
    tgba_explicit_number* res = new tgba_explicit_number(aut->get_dict());

    {
      power_state ps;
      state* s = aut->get_init_state();
      pm.states.insert(s);
      ps.insert(s);
      todo.push_back(ps);
      seen[ps] = 1;
      pm.map_[1] = ps;
    }

    unsigned state_num = 1;

    while (!todo.empty())
      {
	power_state src = todo.front();
	todo.pop_front();
	// Compute all variables occurring on outgoing arcs.
	bdd all_vars = bddtrue;
	power_state::const_iterator i;

	for (i = src.begin(); i != src.end(); ++i)
	  all_vars &= aut->support_variables(*i);

	// Compute all possible combinations of these variables.
	bdd all_conds = bddtrue;
	while (all_conds != bddfalse)
	  {
	    bdd cond = bdd_satoneset(all_conds, all_vars, bddtrue);
	    all_conds -= cond;

	    // Construct the set of all states reachable via COND.
	    power_state dest;
	    for (i = src.begin(); i != src.end(); ++i)
	      {
		tgba_succ_iterator *si = aut->succ_iter(*i);
		for (si->first(); !si->done(); si->next())
		  if ((cond >> si->current_condition()) == bddtrue)
		    {
		      const state* s = pm.canonicalize(si->current_state());
		      dest.insert(s);
		    }
		delete si;
	      }
	    if (dest.empty())
	      continue;
	    // Add that transition.
	    power_set::const_iterator i = seen.find(dest);
	    int dest_num;
	    state_explicit_number::transition* t;
	    if (i != seen.end())
	      {
		dest_num = i->second;
                t = res->create_transition(seen[src], dest_num);
	      }
	    else
	      {
		dest_num = ++state_num;
		seen[dest] = dest_num;
		pm.map_[dest_num] = dest;
		todo.push_back(dest);
                t = res->create_transition(seen[src], dest_num);
	      }
	    res->add_conditions(t, cond);
	  }
      }
    if (merge)
      res->merge_transitions();
    return res;
  }

  tgba_explicit_number*
  tgba_powerset(const tgba* aut)
  {
    power_map pm;
    return tgba_powerset(aut, pm);
  }


  namespace
  {

    class fix_scc_acceptance: protected enumerate_cycles
    {
    public:
      typedef dfs_stack::const_iterator cycle_iter;
      typedef state_explicit_number::transition trans;
      typedef std::set<trans*> trans_set;
      typedef std::vector<trans_set> set_set;
    protected:
      const tgba* ref_;
      power_map& refmap_;
      trans_set reject_;	// set of rejecting transitions
      set_set accept_;		// set of cycles that are accepting
      trans_set all_;		// all non rejecting transitions
      unsigned threshold_;	// maximum count of enumerated cycles
      unsigned cycles_left_; 	// count of cycles left to explore

    public:
      fix_scc_acceptance(const scc_map& sm, const tgba* ref, power_map& refmap,
			 unsigned threshold)
	: enumerate_cycles(sm), ref_(ref), refmap_(refmap),
	  threshold_(threshold)
      {
      }

      bool fix_scc(const int m)
      {
	reject_.clear();
	accept_.clear();
	cycles_left_ = threshold_;
	run(m);

//	std::cerr << "SCC #" << m << "\n";
//	std::cerr << "REJECT: ";
//	print_set(std::cerr, reject_) << "\n";
//	std::cerr << "ALL: ";
//	print_set(std::cerr, all_) << "\n";
//	for (set_set::const_iterator j = accept_.begin();
//	     j != accept_.end(); ++j)
//	  {
//	    std::cerr << "ACCEPT: ";
//	    print_set(std::cerr, *j) << "\n";
//	  }

	bdd acc = aut_->all_acceptance_conditions();
	for (trans_set::iterator i = all_.begin(); i != all_.end(); ++i)
	  {
	    (*i)->acceptance_conditions = acc;
	  }
	return threshold_ != 0 && cycles_left_ == 0;
      }

      bool is_cycle_accepting(cycle_iter begin, trans_set& ts) const
      {
	tgba_explicit_number* a =
	  down_cast<tgba_explicit_number*>(const_cast<tgba*>(aut_));
	// Build an automaton representing this loop.
	tgba_explicit_number loop_a(aut_->get_dict());
	int loop_size = std::distance(begin, dfs_.end());
	int n;
	cycle_iter i;
	for (n = 1, i = begin; n <= loop_size; ++n, ++i)
	  {
	    trans* t = a->get_transition(i->succ);
	    loop_a.create_transition(n - 1, n % loop_size)->condition =
	      t->condition;
	    if (reject_.find(t) == reject_.end())
	      ts.insert(t);
	  }
	assert(i == dfs_.end());

	const state* loop_a_init = loop_a.get_init_state();
	assert(loop_a.get_label(loop_a_init) == 0);

	// Check if the loop is accepting in the original automaton.
	bool accepting = false;

	// Iterate on each original state corresponding to the
	// start of the loop in the determinized automaton.
	const power_map::power_state& ps =
	  refmap_.states_of(a->get_label(begin->ts->first));
	for (power_map::power_state::const_iterator it = ps.begin();
	     it != ps.end() && !accepting; ++it)
	  {
	    // Construct a product between
	    // LOOP_A, and ORIG_A starting in *IT.

	    tgba* p = new tgba_product_init(&loop_a, ref_,
					    loop_a_init, *it);

	    //spot::dotty_reachable(std::cout, p);

	    couvreur99_check* ec = down_cast<couvreur99_check*>(couvreur99(p));
	    assert(ec);
	    emptiness_check_result* res = ec->check();
	    if (res)
	      accepting = true;
	    delete res;
	    delete ec;
	    delete p;
	  }

	loop_a_init->destroy();
	return accepting;
      }

      std::ostream&
      print_set(std::ostream& o, const trans_set& s) const
      {
	o << "{ ";
	for (trans_set::const_iterator i = s.begin(); i != s.end(); ++i)
	  o << *i << " ";
	o << "}";
	return o;
      }

      virtual bool
      cycle_found(const state* start)
      {
	cycle_iter i = dfs_.begin();
	while (i->ts->first != start)
	  ++i;
	trans_set ts;
	bool is_acc = is_cycle_accepting(i, ts);
	do
	  {
	    //	    std::cerr << aut_->format_state(i->ts->first) << " ";
	    ++i;
	  }
	while (i != dfs_.end());
	//	std::cerr << "  acc=" << is_acc << "  (";
	//	bdd_print_accset(std::cerr, aut_->get_dict(), s) << ") ";
	//	print_set(std::cerr, ts) << "\n";
	if (is_acc)
	  {
	    accept_.push_back(ts);
	    all_.insert(ts.begin(), ts.end());
	  }
	else
	  {
	    for (trans_set::const_iterator i = ts.begin(); i != ts.end(); ++i)
	      {
		trans* t = *i;
		reject_.insert(t);
		for (set_set::iterator j = accept_.begin();
		     j != accept_.end(); ++j)
		  {
		    j->erase(t);
		  }
		all_.erase(t);
	      }
	  }

	// Abort this algorithm if we have seen too much cycles, i.e.,
	// when cycle_left_ *reaches* 0.  (If cycle_left_ == 0, that
	// means we had no limit.)
	return (cycles_left_ == 0) || --cycles_left_;
      }
    };

    static bool
    fix_dba_acceptance(tgba_explicit_number* det,
		       const tgba* ref, power_map& refmap,
		       unsigned threshold)
    {
      det->copy_acceptance_conditions_of(ref);

      scc_map sm(det);
      sm.build_map();

      unsigned scc_count = sm.scc_count();

      fix_scc_acceptance fsa(sm, ref, refmap, threshold);

      for (unsigned m = 0; m < scc_count; ++m)
	if (!sm.trivial(m))
	  if (fsa.fix_scc(m))
	    return true;
      return false;
    }
  }

  tgba_explicit_number*
  tba_determinize(const tgba* aut,
		  unsigned threshold_states, unsigned threshold_cycles)
  {
    power_map pm;
    // Do not merge transitions in the deterministic automaton.  If we
    // add two self-loops labeled by "a" and "!a", we do not want
    // these to be merged as "1" before the acceptance has been fixed.
    tgba_explicit_number* det = tgba_powerset(aut, pm, false);

    if ((threshold_states > 0)
	&& (pm.map_.size() > pm.states.size() * threshold_states))
      {
	delete det;
	return 0;
      }
    if (fix_dba_acceptance(det, aut, pm, threshold_cycles))
      {
	delete det;
	return 0;
      }
    det->merge_transitions();
    return det;
  }

  tgba*
  tba_determinize_check(const tgba* aut,
			unsigned threshold_states,
			unsigned threshold_cycles,
			const ltl::formula* f,
			const tgba* neg_aut)
  {
    const tgba* built = 0;
    if (f == 0 && neg_aut == 0)
      return 0;
    if (aut->number_of_acceptance_conditions() > 1)
      return 0;

    tgba_explicit_number* det =
      tba_determinize(aut, threshold_states, threshold_cycles);

    if (!det)
      return 0;

    if (neg_aut == 0)
      {
	const ltl::formula* neg_f =
	  ltl::unop::instance(ltl::unop::Not, f->clone());
	neg_aut = ltl_to_tgba_fm(neg_f, aut->get_dict());
	neg_f->destroy();

	// Remove useless SCCs.
	const tgba* tmp = scc_filter(neg_aut, true);
	delete neg_aut;
	built = neg_aut = tmp;
      }

    bool ok = false;

    tgba* p = new tgba_product(det, neg_aut);
    emptiness_check* ec = couvreur99(p);
    emptiness_check_result* res = ec->check();
    if (!res)
      {
	delete ec;
	delete p;

	// Complement the DBA.
	tgba* neg_det = dtgba_complement(det);

	tgba* p = new tgba_product(aut, neg_det);
	emptiness_check* ec = couvreur99(p);
	res = ec->check();

	if (!res)
	  {
	    // Finally, we are now sure that it was safe
	    // to determinize the automaton.
	    ok = true;
	  }

	delete res;
	delete ec;
	delete p;
	delete neg_det;
      }
    else
      {
	delete res;
	delete ec;
	delete p;
      }
    delete built;

    if (ok)
      return det;
    delete det;
    return const_cast<tgba*>(aut);
  }
}
