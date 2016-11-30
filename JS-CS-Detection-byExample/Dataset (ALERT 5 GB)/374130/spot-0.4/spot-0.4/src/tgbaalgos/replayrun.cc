// Copyright (C) 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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
#include "replayrun.hh"
#include "tgba/tgba.hh"
#include "emptiness.hh"
#include "tgba/bddprint.hh"
#include <sstream>

namespace spot
{
  namespace
  {
    void
    print_annotation(std::ostream& os, const tgba* a,
		     const tgba_succ_iterator* i)
    {
      std::string s = a->transition_annotation(i);
      if (s == "")
	return;
      os << " " << s;
    }
  }

  bool
  replay_tgba_run(std::ostream& os, const tgba* a, const tgba_run* run,
		  bool debug)
  {
    const state* s = a->get_init_state();
    int serial = 1;
    const tgba_run::steps* l;
    std::string in;
    bdd all_acc = bddfalse;
    bdd expected_all_acc = a->all_acceptance_conditions();
    bool all_acc_seen = false;
    typedef Sgi::hash_map<const state*, std::set<int>,
                          state_ptr_hash, state_ptr_equal> state_map;
    state_map seen;

    if (run->prefix.empty())
      {
	l = &run->cycle;
	in = "cycle";
	if (!debug)
	  os << "No prefix.\nCycle:" << std::endl;
      }
    else
      {
	l = &run->prefix;
	in = "prefix";
	if (!debug)
	  os << "Prefix:" << std::endl;
      }

    tgba_run::steps::const_iterator i = l->begin();

    if (s->compare(i->s))
      {
	if (debug)
	  os << "ERROR: First state of run (in " << in << "): "
	     << a->format_state(i->s) << std::endl
	     << "does not match initial state of automata: "
	     << a->format_state(s) << std::endl;
	delete s;
	return false;
      }

    for (; i != l->end(); ++serial)
      {
	if (debug)
	  {
	    // Keep track of the serial associated to each state so we
	    // can note duplicate states and make the replay easier to read.
	    state_map::iterator o = seen.find(s);
	    std::ostringstream msg;
	    if (o != seen.end())
	      {
		std::set<int>::const_iterator d;
		for (d = o->second.begin(); d != o->second.end(); ++d)
		  msg << " == " << *d;
		o->second.insert(serial);
		delete s;
		s = o->first;
	      }
	    else
	      {
		seen[s].insert(serial);
	      }
	    os << "state " << serial << " in " << in << msg.str() << ": ";
	  }
	else
	  {
	    os << "  ";
	  }
	os << a->format_state(s) << std::endl;

	// expected outgoing transition
	bdd label = i->label;
	bdd acc = i->acc;

	// compute the next expected state
	const state* next;
	++i;
	if (i != l->end())
	  {
	    next = i->s;
	  }
	else
	  {
	    if (l == &run->prefix)
	      {
		l = &run->cycle;
		in = "cycle";
		i = l->begin();
		if (!debug)
		  os << "Cycle:" << std::endl;
	      }
	    next = l->begin()->s;
	  }

	// browse the actual outgoing transitions
	tgba_succ_iterator* j = a->succ_iter(s);
	// When not debugging, S is not used as key in SEEN, so we can
	// delete it right now.
	if (!debug)
	  delete s;
	for (j->first(); !j->done(); j->next())
	  {
	    if (j->current_condition() != label
		|| j->current_acceptance_conditions() != acc)
	      continue;

	    const state* s2 = j->current_state();
	    if (s2->compare(next))
	      {
		delete s2;
		continue;
	      }
	    else
	      {
		s = s2;
		break;
	      }
	  }
	if (j->done())
	  {
	    if (debug)
	      {
		os << "ERROR: no transition with label="
		   << bdd_format_formula(a->get_dict(), label)
		   << " and acc=" << bdd_format_accset(a->get_dict(), acc)
		   << " leaving state " << serial
		   << " for state " << a->format_state(next)
		   << std::endl
		   << "The following transitions leave state " << serial
		   << ":" << std::endl;
		for (j->first(); !j->done(); j->next())
		  {
		    const state* s2 = j->current_state();
		    os << "  *";
		    print_annotation(os, a, j);
		    os << " label="
		       << bdd_format_formula(a->get_dict(),
					     j->current_condition())
		       << " and acc="
		       << bdd_format_accset(a->get_dict(),
					    j->current_acceptance_conditions())
		       << " going to " << a->format_state(s2) << std::endl;
		    delete s2;
		  }
	      }
	    delete j;
	    delete s;
	    return false;
	  }
	if (debug)
	  {
	    os << "transition";
	    print_annotation(os, a, j);
	    os << " with label="
	       << bdd_format_formula(a->get_dict(), label)
	       << " and acc=" << bdd_format_accset(a->get_dict(), acc)
	       << std::endl;
	  }
	else
	  {
	    os << "  |  ";
	    print_annotation(os, a, j);
	    bdd_print_formula(os, a->get_dict(), label);
	    os << "\t";
	    bdd_print_accset(os, a->get_dict(), acc);
	    os << std::endl;
	  }
	delete j;

	// Sum acceptance conditions.
	//
	// (Beware l and i designate the next step to consider.
	// Therefore if i is at the beginning of the cycle, `acc'
	// contains the acceptance conditions of the last transition
	// in the prefix; we should not account it.)
	if (l == &run->cycle && i != l->begin())
	  {
	    all_acc |= acc;
	    if (!all_acc_seen && all_acc == expected_all_acc)
	      {
		all_acc_seen = true;
		if (debug)
		  os << "all acceptance conditions ("
		     << bdd_format_accset(a->get_dict(), all_acc)
		     << ") have been seen"
		     << std::endl;
	      }
	  }
      }
    delete s;
    if (all_acc != expected_all_acc)
      {
	if (debug)
	  os << "ERROR: The cycle's acceptance conditions ("
	     << bdd_format_accset(a->get_dict(), all_acc) << ") do not"
	     << std::endl
	     << "match those of the automata ("
	     << bdd_format_accset(a->get_dict(), expected_all_acc)
	     << std::endl;
	return false;
      }

    state_map::const_iterator o = seen.begin();
    while (o != seen.end())
      {
	// Advance the iterator before deleting the "key" pointer.
	const state* ptr = o->first;
	++o;
	delete ptr;
      }

    return true;
  }
}
