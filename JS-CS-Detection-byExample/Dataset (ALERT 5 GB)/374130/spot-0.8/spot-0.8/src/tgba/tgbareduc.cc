// Copyright (C) 2008, 2009, 2011 Laboratoire de Recherche et
// Developpement de l'Epita (LRDE).
// Copyright (C) 2004, 2005 Laboratoire d'Informatique de Paris
// 6 (LIP6), département Systèmes Répartis Coopératifs (SRC),
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

#include "tgbareduc.hh"
#include <sstream>

namespace spot
{
  namespace
  {
    typedef std::pair<const spot::state*, tgba_succ_iterator*> pair_state_iter;
  }

  tgba_reduc::tgba_reduc(const tgba* a)
    : tgba_explicit_string(a->get_dict()),
      tgba_reachable_iterator_breadth_first(a)
  {
    dict_->register_all_variables_of(a, this);

    run();
    all_acceptance_conditions_ = a->all_acceptance_conditions();
    all_acceptance_conditions_computed_ = true;
  }

  tgba_reduc::~tgba_reduc()
  {
    sp_map::iterator i;
    for (i = state_predecessor_map_.begin();
	 i!= state_predecessor_map_.end(); ++i)
      {
	delete i->second;
      }
  }

  void
  tgba_reduc::quotient_state(direct_simulation_relation* rel)
  {
    // Remember that for each state couple
    // (*i)->second simulate (*i)->first.

    for (direct_simulation_relation::iterator i = rel->begin();
	 i != rel->end(); ++i)
      {

	// All state simulate himself.
	if (((*i)->first)->compare((*i)->second) == 0)
	  continue;

	// We check if the two state are co-simulate.
	bool recip = false;
	for (direct_simulation_relation::iterator j = i;
	     j != rel->end(); ++j)
	  if ((((*i)->first)->compare((*j)->second) == 0) &&
	      (((*j)->first)->compare((*i)->second) == 0))
	    recip = true;

	if (recip)
	  //this->redirect_transition((*i)->first, (*i)->second);
	  this->merge_state((*i)->first, (*i)->second);
      }

    this->merge_transitions();
  }

  void
  tgba_reduc::quotient_state(delayed_simulation_relation* rel)
  {
    if (nb_set_acc_cond() > 1)
      return;

    //this->quotient_state(rel);

    for (delayed_simulation_relation::iterator i = rel->begin();
	 i != rel->end(); ++i)
      {

	// All state simulate himself.
	if (((*i)->first)->compare((*i)->second) == 0)
	  continue;

	// We check if the two state are co-simulate.
	bool recip = false;
	for (delayed_simulation_relation::iterator j = i;
	     j != rel->end(); ++j)
	  if ((((*i)->first)->compare((*j)->second) == 0) &&
	      (((*j)->first)->compare((*i)->second) == 0))
	    recip = true;

	if (recip)
	  this->merge_state((*i)->first, (*i)->second);
      }

    this->merge_transitions();
  }

  void
  tgba_reduc::delete_transitions(simulation_relation* rel)
  {
    for (simulation_relation::iterator i = rel->begin();
	 i != rel->end(); ++i)
      {
	if (((*i)->first)->compare((*i)->second) == 0)
	  continue;
	this->redirect_transition((*i)->first, (*i)->second);
      }
    this->merge_transitions();
  }

  ////////////////////////////////////////////
  // for build tgba_reduc

  void
  tgba_reduc::process_state(const spot::state* s, int, tgba_succ_iterator* si)
  {
    spot::state* init = automata_->get_init_state();
    if (init->compare(s) == 0)
      this->set_init_state(automata_->format_state(s));
    init->destroy();

    transition* t;
    for (si->first(); !si->done(); si->next())
      {
	init = si->current_state();
	t = this->create_transition(s, init);
	this->add_conditions(t, si->current_condition());
	this->add_acceptance_conditions(t, si->current_acceptance_conditions());
	init->destroy();
      }
  }

  tgba_explicit::transition*
  tgba_reduc::create_transition(const spot::state* source,
				const spot::state* dest)
  {
    const std::string ss = automata_->format_state(source);
    const std::string sd = automata_->format_state(dest);

    tgba_explicit::state* s = tgba_explicit_string::add_state(ss);
    tgba_explicit::state* d = tgba_explicit_string::add_state(sd);

    transition t;
    t.dest = d;

    sp_map::iterator i = state_predecessor_map_.find(d);
    if (i == state_predecessor_map_.end())
      {
	std::list<state*>* pred = new std::list<state*>;
	pred->push_back(s);
	state_predecessor_map_[d] = pred;
      }
    else
      {
	(i->second)->push_back(s);
      }

    t.condition = bddtrue;
    t.acceptance_conditions = bddfalse;
    state_explicit::transitions_t::iterator is
      = s->successors.insert(s->successors.end(), t);
    return &*is;
  }

  ///////////////////////////////////////////////////

  void
  tgba_reduc::redirect_transition(const spot::state* s,
				  const spot::state* simul)
  {
    bool belong = false;
    bdd cond_simul;
    bdd acc_simul;
    std::list<state*> ltmp;
    const tgba_explicit::state* s1 =
      name_state_map_[tgba_explicit_string::format_state(s)];
    const tgba_explicit::state* s2 =
      name_state_map_[tgba_explicit_string::format_state(simul)];

    sp_map::iterator i = state_predecessor_map_.find(s1);
    if (i == state_predecessor_map_.end()) // 0 predecessor
	return;

    // for all predecessor of s.
    for (std::list<state*>::iterator p = (i->second)->begin();
	 p != (i->second)->end(); ++p)
      {
	// We check if simul belong to the successor of p,
	// as s belong too.
	for (state_explicit::transitions_t::iterator
	       j = (*p)->successors.begin();
	     j != (*p)->successors.end(); ++j)
	  if (j->dest == s2) // simul belong to the successor of p.
	    {
	      belong = true;
	      cond_simul = j->condition;
	      acc_simul = j->acceptance_conditions;
	      break;
	    }

	// If not, we check for another predecessor of s.
	if (!belong)
	  continue;

	// for all successor of p, a predecessor of s and simul.
	for (state_explicit::transitions_t::iterator
	       j = (*p)->successors.begin();
	     j != (*p)->successors.end(); ++j)
	  {
	    // if the transition lead to s.
	    if ((j->dest == s1) &&
		// if the label of the transition whose lead to s implies
		// this leading to simul.
		(((!j->condition | cond_simul) == bddtrue) &&
		 ((!j->acceptance_conditions) | acc_simul) == bddtrue))
	      {
		// We can redirect transition leading to s on simul.
		j->dest = const_cast<tgba_explicit::state*>(s2);

		// We memorize that we have to remove p
		// of the predecessor of s.
		ltmp.push_back(*p);
	      }
	  }
	belong = false;
      }

    // We remove all the predecessor of s than we have memorized.
    std::list<state*>::iterator k;
    for (k = ltmp.begin();
	 k != ltmp.end(); ++k)
      this->remove_predecessor_state(i->first, *k);
  }

  void
  tgba_reduc::remove_predecessor_state(const state* s, const state* p)
  {
    sp_map::iterator i = state_predecessor_map_.find(s);
    if (i == state_predecessor_map_.end()) // 0 predecessor
      return;

    // for all predecessor of s we remove p.
    for (std::list<state*>::iterator j = (i->second)->begin();
	 j != (i->second)->end();)
      if (p == *j)
	j = (i->second)->erase(j);
      else
	++j;
  }

  void
  tgba_reduc::remove_state(const spot::state* s)
  {
    // We suppose that the state is not reachable when called by
    // merge_state => NO PREDECESSOR.  But it can be have some
    // predecessor in state_predecessor_map_.

    ns_map::iterator k =
      name_state_map_.find(tgba_explicit_string::format_state(s));
    if (k == name_state_map_.end()) // 0 predecessor
	return;

    tgba_explicit::state* st =
      name_state_map_[tgba_explicit_string::format_state(s)];

    // for all successor q of s, we remove s of the predecessor of q.
    // Note that the initial node can't be removed.
    for (state_explicit::transitions_t::iterator j =
	   st->successors.begin(); j != st->successors.end(); ++j)
      this->remove_predecessor_state(j->dest, st);


    sp_map::iterator i = state_predecessor_map_.find(st);
    if (i == state_predecessor_map_.end()) // 0 predecessor
	return;

    // for all predecessor of s (none when called by merge_state)
    for (std::list<state*>::iterator p = (i->second)->begin();
	 p != (i->second)->end(); ++p)
      {
	// for all transition of p, a predecessor of s.
	for (state_explicit::transitions_t::iterator
	       j = (*p)->successors.begin();
	     j != (*p)->successors.end();)
	  {
	    if (j->dest == st)
	      {
		// Remove the transition
		j = (*p)->successors.erase(j);
		++j;
	      }
	    else
	      ++j;
	  }
      }

    // DESTROY THE STATE !? USELESS
    // it will be destroyed when the automaton is deleted
    // name_state_map_::iterator = name_state_map_[st];
    // const tgba_explicit::state* st = name_state_map_[this->format_state(s)];
  }

  void
  tgba_reduc::merge_state(const spot::state* sim1, const spot::state* sim2)
  {
    const tgba_explicit::state* s1 =
      name_state_map_[tgba_explicit_string::format_state(sim1)];
    const tgba_explicit::state* s2 =
      name_state_map_[tgba_explicit_string::format_state(sim2)];
    const tgba_explicit::state* stmp = s1;
    const spot::state* simtmp = sim1;

    // if sim1 is the init state, we remove sim2.
    spot::state* init = this->get_init_state();
    if (sim1->compare(init) == 0)
      {
	s1 = s2;
	s2 = stmp;
	sim1 = sim2;
	sim2 = simtmp;
      }
    init->destroy();

    sp_map::iterator i = state_predecessor_map_.find(s1);
    if (i == state_predecessor_map_.end()) // 0 predecessor
      {
	// We can remove s1 safely, without changing the language
	// of the automaton.
	this->remove_state(sim1);
	return;
      }

    // for all predecessor of s1, not the initial state,
    // we redirect to s2 the transitions that lead to s1.
    for (std::list<state*>::iterator p = (i->second)->begin();
	 p != (i->second)->end(); ++p)
      {
	// for all successor of p, a predecessor of s1.
	for (state_explicit::transitions_t::iterator
	       j = (*p)->successors.begin();
	     j != (*p)->successors.end(); ++j)
	  {
	    // if the successor was s1...
	    if (j->dest == s1)
	      {
		// ... make it s2.
		j->dest = const_cast<tgba_explicit::state*>(s2);
	      }
	  }
      }

    // FIXME: The following justification sounds really dubious.
    //
    // We have to stock on s2 the acceptance condition of the arc
    // leaving s1 (possible when the simulation is delayed). Since s2
    // simulates s1, s2 has some labels that imply these of s1, so we
    // can put the acceptance conditions on its arcs.
    for (state_explicit::transitions_t::const_iterator
	   j = s1->successors.begin();
	 j != s1->successors.end(); ++j)
      {
	transition t;
	t.dest = j->dest;
	t.condition = j->condition;
	t.acceptance_conditions = j->acceptance_conditions;
	const_cast<state_explicit*>(s2)->successors.push_back(t);
      }

    // We remove all the predecessor of s1.
    (i->second)->clear();

    // then we can remove s1 safely, without changing the language
    // of the automaton.
    this->remove_state(sim1);
  }

  void
  tgba_reduc::merge_state_delayed(const spot::state*,
				  const spot::state*)
  {
    // TO DO
  }

  int
  tgba_reduc::nb_set_acc_cond() const
  {
    bdd acc, all;
    acc = all = this->all_acceptance_conditions();
    int count = 0;
    while (all != bddfalse)
      {
	all -= bdd_satone(all);
	++count;
      }
    return count;
  }

  //////// JUST FOR DEBUG //////////

  void
  tgba_reduc::display_rel_sim(simulation_relation* rel,
			      std::ostream& os)
  {
    int n = 0;
    simulation_relation::iterator i;
    for (i = rel->begin(); i != rel->end(); ++i)
      {
	if (((*i)->first)->compare((*i)->second) == 0)
	  continue;

	  ++n;
	  os << "couple " << n
	     << std::endl
	     << "  " << " [label=\""
	     << this->format_state((*i)->first) << "\"]"
	     << std::endl
	     << "  " << " [label=\""
	     << this->format_state((*i)->second) << "\"]"
	     << std::endl
	     << std::endl;
      }
  }

}
