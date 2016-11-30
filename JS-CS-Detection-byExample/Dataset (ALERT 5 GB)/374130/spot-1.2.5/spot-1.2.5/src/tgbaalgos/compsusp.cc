// -*- coding: utf-8 -*-
// Copyright (C) 2012, 2013 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
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

#include "compsusp.hh"
#include "sccfilter.hh"
#include "scc.hh"
#include "tgba/tgbaexplicit.hh"
#include "ltl2tgba_fm.hh"
#include "minimize.hh"
#include "simulation.hh"
#include "safety.hh"
#include "ltlast/allnodes.hh"
#include "ltlvisit/tostring.hh"
#include "ltlvisit/clone.hh"
#include <queue>
#include <sstream>
#include "ltlenv/environment.hh"

namespace spot
{
  namespace
  {
    typedef std::map<const ltl::formula*, bdd> formula_bdd_map;

    // An environment to store atomic proposition associated to
    // suspended variable.  (We don't use the default environment to
    // avoid conflicts with user-defined atomic propositions that
    // would share the same name.)
    class suspended_environment: public ltl::environment
    {
    public:
      const ltl::formula*
      require(const std::string& s)
      {
	return ltl::atomic_prop::instance(s, *this);
      }

      const std::string&
      name()
      {
	static std::string name("suspended environment");
	return name;
      }
    };
    static suspended_environment suspenv;

    // Rewrite the suspendable subformulae "s" of an LTL formula in
    // the form Gg where "g" is an atomic proposition representing
    // "s".  At the same time, populate maps that associate "s" to "g"
    // and vice-versa.
    class ltl_suspender_visitor: public ltl::clone_visitor
    {
    public:
      typedef std::map<const ltl::formula*, const ltl::formula*> fmap_t;
      ltl_suspender_visitor(fmap_t& g2s, fmap_t& a2o, bool oblig)
	: g2s_(g2s), a2o_(a2o), oblig_(oblig)
      {
      }

      void
      visit(const ltl::multop* mo)
      {
	ltl::multop::type op = mo->op();
	switch (op)
	  {
	  case ltl::multop::Or:
	  case ltl::multop::And:
	    {
	      ltl::multop::vec* res = new ltl::multop::vec;
	      ltl::multop::vec* oblig = oblig_ ? new ltl::multop::vec : 0;
	      ltl::multop::vec* susp = new ltl::multop::vec;
	      unsigned mos = mo->size();
	      for (unsigned i = 0; i < mos; ++i)
		{
		  const ltl::formula* c = mo->nth(i);
		  if (c->is_boolean())
		    res->push_back(c->clone());
		  else if (oblig_ && c->is_syntactic_obligation())
		    oblig->push_back(c->clone());
		  else if (c->is_eventual() && c->is_universal())
		    susp->push_back(c->clone());
		  else
		    res->push_back(recurse(c));
		}
	      if (!oblig_ || oblig->empty())
		{
		  delete oblig;
		}
	      else
		{
		  const ltl::formula* o = ltl::multop::instance(op, oblig);
		  res->push_back(recurse(o));
		  o->destroy();
		}
	      if (susp->empty())
		{
		  delete susp;
		}
	      else
		{
		  const ltl::formula* o = ltl::multop::instance(op, susp);
		  // Rewrite 'o' as 'G"o"'
		  const ltl::formula* g = recurse(o);
		  o->destroy();
		  if (op == ltl::multop::And)
		    {
		      res->push_back(g);
		    }
		  else
		    {
		      // res || susp -> (res && G![susp]) || G[susp])
		      const ltl::formula* r = ltl::multop::instance(op, res);
		      const ltl::unop* u =
			down_cast<const ltl::unop*>(g);
		      const ltl::formula* gn =
			ltl::unop::instance
			(ltl::unop::G, ltl::unop::instance
			 (ltl::unop::Not, u->child()->clone()));
		      result_ = ltl::multop::instance
			(ltl::multop::Or, ltl::multop::instance
			 (ltl::multop::And, r, gn),
			 g);
		      return;
		    }
		}
	      result_ = ltl::multop::instance(op, res);
	    }
	    break;
	  case ltl::multop::OrRat:
	  case ltl::multop::AndRat:
	  case ltl::multop::AndNLM:
	  case ltl::multop::Concat:
	  case ltl::multop::Fusion:
	    this->ltl::clone_visitor::visit(mo);
	    break;
	  }
      }


      const ltl::formula*
      recurse(const ltl::formula* f)
      {
	const ltl::formula* res;
	if (f->is_boolean())
	  return f->clone();
	if (oblig_ && f->is_syntactic_obligation())
	  {
	    fmap_t::const_iterator i = assoc_.find(f);
	    if (i != assoc_.end())
	      return i->second->clone();

	    std::ostringstream s;
	    s << "〈";
	    to_string(f, s);
	    s << "〉";
	    res = suspenv.require(s.str());
	    // We have to clone f, because it is not always a sub-tree
	    // of the original formula.  (Think n-ary operators.)
	    a2o_[res] = f->clone();
	    assoc_[f] = res;
	    return res;
	  }
	if (f->is_eventual() && f->is_universal())
	  {
	    fmap_t::const_iterator i = assoc_.find(f);
	    if (i != assoc_.end())
	      return ltl::unop::instance(ltl::unop::G, i->second->clone());

	    std::ostringstream s;
	    s << "[";
	    to_string(f, s);
	    s << "]";
	    res = suspenv.require(s.str());
	    // We have to clone f, because it is not always a sub-tree
	    // of the original formula.  (Think n-ary operators.)
	    g2s_[res] = f->clone();
	    assoc_[f] = res;
	    return ltl::unop::instance(ltl::unop::G, res);
	  }
	f->accept(*this);
	return result_;
      }

    private:
      fmap_t& g2s_;
      fmap_t assoc_;		// This one is only needed by the visitor.
      fmap_t& a2o_;
      bool oblig_;
    };


    typedef std::pair<const state*, const state*> state_pair;

    typedef std::map<state_pair, int> pair_map;
    typedef std::deque<state_pair> pair_queue;

    static
    tgba*
    susp_prod(tgba* left, const ltl::formula* f, bdd v)
    {
      bdd_dict* dict = left->get_dict();
      const tgba* a1 = ltl_to_tgba_fm(f, dict, true, true);

      const tgba* a2 = scc_filter(a1, false);
      delete a1;
      const tgba* right = iterated_simulations(a2);
      delete a2;

      tgba_explicit_number* res = new tgba_explicit_number(dict);
      dict->register_all_variables_of(left, res);
      dict->register_all_variables_of(right, res);
      dict->unregister_variable(bdd_var(v), res);

      // The left and right automata might have acceptance marker in
      // common.
      // For the example, assume left has acceptance conditions A,B,C,D
      // while right has acceptance condition C,D,E,F.

      // Negative acceptance variables...
      // !A&!B&!C&!D
      bdd lna = left->neg_acceptance_conditions();
      // !C&!D&!E&!F
      bdd rna = right->neg_acceptance_conditions();

      // Missing acceptance variables...
      // !E&!F
      bdd lma = bdd_exist(rna, bdd_support(lna));
      // !A&!B
      bdd rma = bdd_exist(lna, bdd_support(rna));

      // (A&!B&!C&!D + ... + !A&!B&!C&D) & !E&!F
      bdd lac = left->all_acceptance_conditions() & lma;
      // (C&!D&!E&!F + ... + !C&!D&!E&F) & !A&!B
      bdd rac = right->all_acceptance_conditions() & rma;
      bdd allacc = lac | rac;
      res->set_acceptance_conditions(allacc);

      // Acceptance condition to add to all transitions
      // of the left automaton.
      // !A&!B&!C&!D&E&!F | !A&!B&!C&!D&!E&F
      bdd ladd = rac - lma;
      bdd radd = lac - rma;

      pair_map seen;
      pair_queue todo;

      state_pair p(left->get_init_state(), 0);
      state* ris = right->get_init_state();
      p.second = ris;
      seen[p] = 0;
      todo.push_back(p);
      res->set_init_state(0);

      typedef state_explicit_number::transition trans;

      while (!todo.empty())
	{
	  p = todo.front();
	  todo.pop_front();
	  const state* ls = p.first;
	  const state* rs = p.second;
	  int src = seen[p];

	  tgba_succ_iterator* li = left->succ_iter(ls);
	  for (li->first(); !li->done(); li->next())
	    {
	      state_pair d(li->current_state(), ris);
	      bdd lc = li->current_condition();

	      tgba_succ_iterator* ri = 0;
	      // Should we reset the right automaton?
	      if ((lc & v) == lc)
		{
		  // No.
		  ri = right->succ_iter(rs);
		  ri->first();
		}
	      // Yes.  Reset the right automaton.
	      else
		{
		  p.second = ris;
		}

	      // This loops over all the right transitions
	      // if RI is defined.  Otherwise this just makes
	      // one iteration as if the right automaton was
	      // looping in state 0 with "true".
	      while (!ri || !ri->done())
		{
		  bdd cond = lc;
		  bdd ra = allacc;
		  if (ri)
		    {
		      cond = lc & ri->current_condition();
		      // Skip incompatible transitions.
		      if (cond == bddfalse)
			{
			  ri->next();
			  continue;
			}
		      d.second = ri->current_state();
		      ra = (ri->current_acceptance_conditions() & rma) | radd;
		    }

		  int dest = seen.size();
		  pair_map::const_iterator i = seen.find(d);
		  if (i != seen.end())
		    {
		      dest = i->second;
		    }
		  else
		    {
		      seen[d] = dest;
		      todo.push_back(d);
		    }

		  trans* t = res->create_transition(src, dest);
		  t->condition = bdd_exist(cond, v);
		  bdd la = (li->current_acceptance_conditions() & lma) | ladd;
		  t->acceptance_conditions = ra & la;

		  if (ri)
		    ri->next();
		  else
		    break;
		}
	      delete ri;
	    }
	}
      delete left;
      delete right;

      return res;
    }
  }

  tgba*
  compsusp(const ltl::formula* f, bdd_dict* dict,
	   bool no_wdba, bool no_simulation,
	   bool early_susp, bool no_susp_product, bool wdba_smaller,
	   bool oblig)
  {
    ltl_suspender_visitor::fmap_t g2s;
    ltl_suspender_visitor::fmap_t a2o;
    ltl_suspender_visitor v(g2s, a2o, oblig);
    const ltl::formula* g = v.recurse(f);

    tgba* res;
    {
      // Translate the patched formula, and remove useless SCCs.
      tgba* aut = spot::ltl_to_tgba_fm(g, dict, true, true, false, false, 0, 0);
      res = scc_filter(aut, false);
      delete aut;
    }

    if (!no_wdba)
      {
	tgba* min = minimize_obligation(res, g, 0, wdba_smaller);
	if (min != res)
	  {
	    delete res;
	    res = min;
	    no_simulation = true;
	  }
      }

    if (!no_simulation)
      {
	tgba* sim = spot::iterated_simulations(res);
	delete res;
	res = sim;
      }

    spot::formula_bdd_map susp;
    ltl_suspender_visitor::fmap_t::const_iterator it;
    for (it = g2s.begin(); it != g2s.end(); ++it)
      {
	bdd_dict::fv_map::const_iterator j = dict->var_map.find(it->first);
	assert(j != dict->var_map.end());
	susp[it->second] = bdd_ithvar(j->second);
      }

    // Remove suspendable formulae from non-accepting SCCs.
    bdd suspvars = bddtrue;
    for (formula_bdd_map::const_iterator i = susp.begin();
	 i != susp.end(); ++i)
      suspvars &= i->second;

    bdd allaccap = bddtrue; // set of atomic prop used in accepting SCCs.
    tgba* aut = res;
    {
      scc_map sm(aut);
      sm.build_map();

      // Restrict suspvars to the set of suspension labels that occur
      // in accepting SCC.
      unsigned sn = sm.scc_count();
      for (unsigned n = 0; n < sn; n++)
	if (sm.accepting(n))
	  allaccap &= sm.ap_set_of(n);

      bdd ignored = bdd_exist(suspvars, allaccap);
      suspvars = bdd_existcomp(suspvars, allaccap);
      res = scc_filter(aut, false, &sm, suspvars, early_susp, ignored);
    }
    delete aut;

    // Do we need to synchronize any suspended formula?
    if (!susp.empty() && !no_susp_product)
      for (formula_bdd_map::const_iterator i = susp.begin();
	   i != susp.end(); ++i)
	if ((allaccap & i->second) == allaccap)
	  res = susp_prod(res, i->first, i->second);

    g->destroy();

    for (ltl_suspender_visitor::fmap_t::iterator i = g2s.begin();
	 i != g2s.end(); ++i)
      i->second->destroy();
    for (ltl_suspender_visitor::fmap_t::iterator i = a2o.begin();
	 i != a2o.end(); ++i)
      i->second->destroy();
    return res;
  }
}
