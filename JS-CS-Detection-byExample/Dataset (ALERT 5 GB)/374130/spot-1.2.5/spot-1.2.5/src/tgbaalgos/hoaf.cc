// -*- coding: utf-8 -*-
// Copyright (C) 2011, 2012, 2014 Laboratoire de Recherche et
// Developpement de l'Epita (LRDE).
// Copyright (C) 2003, 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include <ostream>
#include <sstream>
#include <map>
#include "tgba/tgba.hh"
#include "tgba/sba.hh"
#include "hoaf.hh"
#include "reachiter.hh"
#include "misc/escape.hh"
#include "misc/bddlt.hh"
#include "misc/minato.hh"
#include "tgba/formula2bdd.hh"
#include "ltlvisit/tostring.hh"

namespace spot
{
  namespace
  {
    struct metadata
    {
      // Assign a number to each atomic proposition.
      typedef std::map<int, unsigned> ap_map;
      ap_map ap;
      typedef std::vector<int> vap_t;
      vap_t vap;

      // Assign a number to each acceptance condition.
      typedef std::map<bdd, unsigned, bdd_less_than> acc_map;
      acc_map acc;

      // Map each state to a number.
      typedef Sgi::hash_map<const state*, unsigned,
			    state_ptr_hash, state_ptr_equal> state_map;
      state_map sm;
      // Map each number to its states.
      typedef std::vector<const state*> number_map;
      number_map nm;

      std::vector<bool> common_acc;
      bool state_acc;
      bool is_complete;
      bool is_deterministic;

      // Label support: the set of all conditions occurring in the
      // automaton.
      typedef std::map<bdd, std::string, bdd_less_than> sup_map;
      sup_map sup;

      metadata(const tgba* aut)
	: state_acc(true), is_complete(true), is_deterministic(true)
      {
	number_all_acc(aut);
	number_all_states_and_fill_sup(aut);
	number_all_ap();
      }

      void number_all_acc(const tgba* aut)
      {
	bdd all_acc = aut->all_acceptance_conditions();
	unsigned count = 0;
	while (all_acc != bddfalse)
	  {
	    bdd one = bdd_satone(all_acc);
	    all_acc -= one;
	    acc[one] = count++;
	  }
      }

      std::ostream&
      emit_acc(std::ostream& os, bdd b)
      {
	// FIXME: We could use a cache for this.
	if (b == bddfalse)
	  return os;
	os << " {";
	bool notfirst = false;
	while (b != bddfalse)
	  {
	    bdd one = bdd_satone(b);
	    b -= one;
	    if (notfirst)
	      os << ' ';
	    else
	      notfirst = true;
	    os << acc[one];
	  }
	os << '}';
	return os;
      }

      void number_all_states_and_fill_sup(const tgba* aut)
      {
	std::string empty;

	// Scan the whole automaton to number states.
	std::deque<const state*> todo;

	const state* init = aut->get_init_state();
	sm[init] = 0;
	nm.push_back(init);
	todo.push_back(init);

	while (!todo.empty())
	  {
	    tgba_succ_iterator* i = aut->succ_iter(todo.front());
	    todo.pop_front();
	    bdd prev = bddtrue;
	    bool st_acc = true;
	    bdd sum = bddfalse;
	    bdd available = bddtrue;
	    for (i->first(); !i->done(); i->next())
	      {
		const state* dst = i->current_state();
		bdd cond = i->current_condition();
		if (is_complete)
		  sum |= cond;
		if (is_deterministic)
		  {
		    if (!bdd_implies(cond, available))
		      is_deterministic = false;
		    else
		      available -= cond;
		  }
		sup.insert(std::make_pair(cond, empty));
		if (sm.insert(std::make_pair(dst, nm.size())).second)
		  {
		    nm.push_back(dst);
		    todo.push_back(dst);
		  }
		else
		  {
		    dst->destroy();
		  }
		if (st_acc)
		  {
		    bdd acc = i->current_acceptance_conditions();
		    if (prev != bddtrue && prev != acc)
		      st_acc = false;
		    else
		      prev = acc;
		  }
	      }
	    if (is_complete)
	      is_complete &= sum == bddtrue;

	    common_acc.push_back(st_acc);
	    state_acc &= st_acc;
	    delete i;
	  }
      }

      void number_all_ap()
      {
	sup_map::iterator i;
	bdd all = bddtrue;
	for (i = sup.begin(); i != sup.end(); ++i)
	  all &= bdd_support(i->first);

	while (all != bddtrue)
	  {
	    int v = bdd_var(all);
	    all = bdd_high(all);
	    ap.insert(std::make_pair(v, vap.size()));
	    vap.push_back(v);
	  }

	for (i = sup.begin(); i != sup.end(); ++i)
	  {
	    bdd cond = i->first;
	    if (cond == bddtrue)
	      {
		i->second = "t";
		continue;
	      }
	    if (cond == bddfalse)
	      {
		i->second = "f";
		continue;
	      }
	    std::ostringstream s;
	    bool notfirstor = false;

	    minato_isop isop(cond);
	    bdd cube;
	    while ((cube = isop.next()) != bddfalse)
	      {
		if (notfirstor)
		  s << " | ";
		bool notfirstand = false;
		while (cube != bddtrue)
		  {
		    if (notfirstand)
		      s << '&';
		    else
		      notfirstand = true;
		    bdd h = bdd_high(cube);
		    if (h == bddfalse)
		      {
			s << '!' << ap[bdd_var(cube)];
			cube = bdd_low(cube);
		      }
		    else
		      {
			s << ap[bdd_var(cube)];
			cube = h;
		      }
		  }
		notfirstor = true;
	      }
	    i->second = s.str();
	  }
      }

    };

  }


  std::ostream&
  hoaf_reachable(std::ostream& os,
		 const tgba* aut,
		 const ltl::formula* f,
		 hoaf_acceptance acceptance,
		 hoaf_alias alias,
		 bool newline)
  {
    (void) alias;

    metadata md(aut);

    if (acceptance == Hoaf_Acceptance_States
	&& !md.state_acc)
      acceptance = Hoaf_Acceptance_Transitions;

    unsigned num_states = md.nm.size();

    const char nl = newline ? '\n' : ' ';
    os << "HOA: v1" << nl;
    if (f)
      escape_str(os << "name: \"", to_string(f)) << '"' << nl;
    os << "States: " << num_states << nl
       << "Start: 0" << nl
       << "AP: " << md.vap.size();
    bdd_dict* d = aut->get_dict();
    for (metadata::vap_t::const_iterator i = md.vap.begin();
	 i != md.vap.end(); ++i)
      escape_str(os << " \"", to_string(d->bdd_map[*i].f)) << '"';
    os << nl;
    unsigned num_acc = md.acc.size();
    if (num_acc == 0)
      os << "acc-name: all";
    else if (num_acc == 1)
      os << "acc-name: Buchi";
    else
      os << "acc-name: generalized-Buchi " << num_acc;
    os << nl;
    os << "Acceptance: " << num_acc;
    if (num_acc > 0)
      {
	os << " I(0";
	for (unsigned i = 1; i < num_acc; ++i)
	  os << ")&I(" << i;
	os << ')';
      }
    else
      {
	os  << " t";
      }
    os << nl;
    os << "properties: trans-labels explicit-labels";
    if (acceptance == Hoaf_Acceptance_States)
      os << " state-acc";
    else if (acceptance == Hoaf_Acceptance_Transitions)
      os << " trans-acc";
    if (md.is_complete)
      os << " complete";
    if (md.is_deterministic)
      os << " deterministic";
    os << nl;
    os << "--BODY--" << nl;
    for (unsigned i = 0; i < num_states; ++i)
      {
	hoaf_acceptance this_acc = acceptance;
	if (this_acc == Hoaf_Acceptance_Mixed)
	  this_acc = (md.common_acc[i] ?
		      Hoaf_Acceptance_States : Hoaf_Acceptance_Transitions);

	tgba_succ_iterator* j = aut->succ_iter(md.nm[i]);
	j->first();

	os << "State: " << i;
	if (this_acc == Hoaf_Acceptance_States && !j->done())
	  md.emit_acc(os, j->current_acceptance_conditions());
	os << nl;

	for (; !j->done(); j->next())
	  {
	    const state* dst = j->current_state();
	    os << '[' << md.sup[j->current_condition()] << "] " << md.sm[dst];
	    if (this_acc == Hoaf_Acceptance_Transitions)
	      md.emit_acc(os, j->current_acceptance_conditions());
	    os << nl;
	    dst->destroy();
	  }
	delete j;
      }
    os << "--END--";		// No newline.  Let the caller decide.
    for (unsigned i = 0; i < num_states; ++i)
      md.nm[i]->destroy();
    return os;
  }

  std::ostream&
  hoaf_reachable(std::ostream& os,
		 const tgba* aut,
		 const char* opt,
		 const ltl::formula* f)
  {
    bool newline = true;
    hoaf_acceptance acceptance = Hoaf_Acceptance_States;
    hoaf_alias alias = Hoaf_Alias_None;

    if (opt)
      while (*opt)
	{
	  switch (*opt++)
	    {
	    case 'l':
	      newline = false;
	      break;
	    case 'm':
	      acceptance = Hoaf_Acceptance_Mixed;
	      break;
	    case 's':
	      acceptance = Hoaf_Acceptance_States;
	      break;
	    case 't':
	      acceptance = Hoaf_Acceptance_Transitions;
	      break;
	    }
	}
    return hoaf_reachable(os, aut, f, acceptance, alias, newline);
  }

}
