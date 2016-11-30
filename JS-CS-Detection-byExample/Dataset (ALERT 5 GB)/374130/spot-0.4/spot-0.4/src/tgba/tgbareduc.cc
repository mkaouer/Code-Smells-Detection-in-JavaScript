// Copyright (C) 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include "tgbareduc.hh"
#include <sstream>

namespace spot
{
  namespace
  {
    typedef std::pair<const spot::state*, tgba_succ_iterator*> pair_state_iter;
  }

  tgba_reduc::tgba_reduc(const tgba* a,
			 const numbered_state_heap_factory* nshf)
    : tgba_explicit(a->get_dict()),
      tgba_reachable_iterator_breadth_first(a),
      h_(nshf->build())
  {
    dict_->register_all_variables_of(a, this);

    run();
    all_acceptance_conditions_ = a->all_acceptance_conditions();
    all_acceptance_conditions_computed_ = true;
    seen_ = 0;
    scc_computed_ = false;
  }

  tgba_reduc::~tgba_reduc()
  {
    sp_map::iterator i;
    for (i = state_predecessor_map_.begin();
	 i!= state_predecessor_map_.end(); ++i)
      {
	delete i->second;
      }

    delete h_;
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

  void
  tgba_reduc::prune_scc()
  {
    if (!scc_computed_)
      this->compute_scc();
    this->prune_acc();
    this->delete_scc();

    this->merge_transitions();
  }

  std::string
  tgba_reduc::format_state(const spot::state* s) const
  {
    std::ostringstream os;
    const state_explicit* se = dynamic_cast<const state_explicit*>(s);
    assert(se);
    sn_map::const_iterator i = state_name_map_.find(se->get_state());
    seen_map::const_iterator j = si_.find(s);
    assert(i != state_name_map_.end());
    if (j != si_.end()) // SCC have been computed
      {
	os << ", SCC " << j->second;
	return i->second + std::string(os.str());
      }
    else
      return i->second;
  }

  ////////////////////////////////////////////
  // for build tgba_reduc

  void
  tgba_reduc::start()
  {
  }

  void
  tgba_reduc::end()
  {
  }

  void
  tgba_reduc::process_state(const spot::state* s, int, tgba_succ_iterator* si)
  {
    spot::state* init = automata_->get_init_state();
    if (init->compare(s) == 0)
      this->set_init_state(automata_->format_state(s));
    delete init;

    transition* t;
    for (si->first(); !si->done(); si->next())
      {
	init = si->current_state();
	t = this->create_transition(s, init);
	this->add_conditions(t, si->current_condition());
	this->add_acceptance_conditions(t, si->current_acceptance_conditions());
	delete init;
      }
  }

  void
  tgba_reduc::process_link(int, int, const tgba_succ_iterator*)
  {
  }

  tgba_explicit::transition*
  tgba_reduc::create_transition(const spot::state* source,
				const spot::state* dest)
  {
    const std::string ss = automata_->format_state(source);
    const std::string sd = automata_->format_state(dest);

    tgba_explicit::state* s
      = tgba_explicit::add_state(ss);
    tgba_explicit::state* d
      = tgba_explicit::add_state(sd);

    transition* t = new transition();
    t->dest = d;

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

    t->condition = bddtrue;
    t->acceptance_conditions = bddfalse;
    s->push_back(t);

    return t;
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
      name_state_map_[tgba_explicit::format_state(s)];
    const tgba_explicit::state* s2 =
      name_state_map_[tgba_explicit::format_state(simul)];

    sp_map::iterator i = state_predecessor_map_.find(s1);
    if (i == state_predecessor_map_.end()) // 0 predecessor
	return;

    // for all predecessor of s.
    for (std::list<state*>::iterator p = (i->second)->begin();
	 p != (i->second)->end(); ++p)
      {

	// We check if simul belong to the successor of p,
	// as s belong too.
	for (tgba_explicit::state::iterator j = (*p)->begin();
	     j != (*p)->end(); ++j)
	  if ((*j)->dest == s2) // simul belong to the successor of p.
	    {
	      belong = true;
	      cond_simul = (*j)->condition;
	      acc_simul = (*j)->acceptance_conditions;
	      break;
	    }

	// If not, we check for another predecessor of s.
	if (!belong)
	  continue;

	// for all successor of p, a predecessor of s and simul.
	for (tgba_explicit::state::iterator j = (*p)->begin();
	     j != (*p)->end(); ++j)
	  {
	    // if the transition lead to s.
	    if (((*j)->dest == s1) &&
		// if the label of the transition whose lead to s implies
		// this leading to simul.
		(((!(*j)->condition | cond_simul) == bddtrue) &&
		 ((!(*j)->acceptance_conditions) | acc_simul) == bddtrue))
	      {
		// We can redirect transition leading to s on simul.
		(*j)->dest = const_cast<tgba_explicit::state*>(s2);

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
    // We suppose than the state is not reachable when call by
    // merge_state => NO PREDECESSOR !!
    // But it can be have some predecessor in state_predecessor_map_ !!
    // So, we remove from it.

    ns_map::iterator k =
      name_state_map_.find(tgba_explicit::format_state(s));
    if (k == name_state_map_.end()) // 0 predecessor
	return;

    tgba_explicit::state* st =
      name_state_map_[tgba_explicit::format_state(s)];

    // for all successor q of s, we remove s of the predecessor of q.
    // Note that the initial node can't be removed.
    for (state::iterator j = st->begin(); j != st->end(); ++j)
      this->remove_predecessor_state((*j)->dest, st);


    sp_map::iterator i = state_predecessor_map_.find(st);
    if (i == state_predecessor_map_.end()) // 0 predecessor
	return;

    // for all predecessor of s. Zero if call by merge_state.
    for (std::list<state*>::iterator p = (i->second)->begin();
	 p != (i->second)->end(); ++p)
      {
	// for all transition of p, a predecessor of s.
	for (state::iterator j = (*p)->begin();
	     j != (*p)->end();)
	  {
	    if ((*j)->dest == st)
	      {
		// Remove the transition
		delete *j;
		j = (*p)->erase(j);
		++j;
	      }
	    else
	      ++j;
	  }
      }

    // DESTROY THE STATE !? USELESS
    // it will be destroy when the automaton will be delete
    // name_state_map_::iterator = name_state_map_[st];
    // const tgba_explicit::state* st = name_state_map_[this->format_state(s)];
  }

  void
  tgba_reduc::merge_state(const spot::state* sim1, const spot::state* sim2)
  {
    const tgba_explicit::state* s1 =
      name_state_map_[tgba_explicit::format_state(sim1)];
    const tgba_explicit::state* s2 =
      name_state_map_[tgba_explicit::format_state(sim2)];
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
    delete init;

    sp_map::iterator i = state_predecessor_map_.find(s1);
    if (i == state_predecessor_map_.end()) // 0 predecessor
      {
	// We can remove s1 safely, without change the language
	// of the automaton.
	this->remove_state(sim1);
	return;
      }

    // for all predecessor of s1, not the initial state,
    // we redirect transition whose lead to s1 to s2.
    for (std::list<state*>::iterator p = (i->second)->begin();
	 p != (i->second)->end(); ++p)
      {
	// for all successor of p, a predecessor of s1.
	for (tgba_explicit::state::iterator j = (*p)->begin();
	     j != (*p)->end(); ++j)
	  {
	    // if the successor if s1.
	    if ((*j)->dest == s1)
	      {
		// We can redirect transition to s2.
		(*j)->dest = const_cast<tgba_explicit::state*>(s2);
	      }
	  }
      }

    // FIXME:
    // Be careful, we have to stock on s2 the acceptance condition on the arc
    // leaving s1 (possible when the simulation is delayed). Since s2 simulate
    // s1, s2 has some label whose implies these of s1, so we can put the
    // acceptance conditions on this arcs.
    for (tgba_explicit::state::const_iterator j = s1->begin();
	 j != s1->end(); ++j)
      {
	// FIXME
	transition* t = new transition();
	t->dest = (*j)->dest;
	t->condition = (*j)->condition;
	t->acceptance_conditions = (*j)->acceptance_conditions;
	const_cast<tgba_explicit::state*>(s2)->push_back(t);
      }

    // We remove all the predecessor of s1.
    (i->second)->clear();

    // then we can remove s1 safely, without change the language
    // of the automaton.
    // useless because the state is not reachable.
    this->remove_state(sim1);

  }

  void
  tgba_reduc::merge_state_delayed(const spot::state*,
				  const spot::state*)
  {
    // TO DO
  }

  /////////////////////////////////////////
  /////////////////////////////////////////
  // Compute SCC

  // From gtec.cc
  void
  tgba_reduc::remove_component(const spot::state* from)
  {
    std::stack<tgba_succ_iterator*> to_remove;

    numbered_state_heap::state_index_p spi = h_->index(from);
    assert(spi.first);
    assert(*spi.second != -1);
    *spi.second = -1;
    tgba_succ_iterator* i = this->succ_iter(from);

    for (;;)
      {
	for (i->first(); !i->done(); i->next())
	  {
	    spot::state* s = i->current_state();
	    numbered_state_heap::state_index_p spi = h_->index(s);

	    if (!spi.first)
	      continue;

	    if (*spi.second != -1)
	      {
		*spi.second = -1;
		to_remove.push(this->succ_iter(spi.first));
	      }
	  }
	delete i;
	if (to_remove.empty())
	  break;
	i = to_remove.top();
	to_remove.pop();
      }
  }

  // From gtec.cc
  void
  tgba_reduc::compute_scc()
  {
    std::stack<bdd> arc;
    int num = 1;
    std::stack<pair_state_iter> todo;


    {
      spot::state* init = this->get_init_state();
      h_->insert(init, 1);
      si_[init] = 1;
      state_scc_.push(init);
      root_.push(1);
      arc.push(bddfalse);
      tgba_succ_iterator* iter = this->succ_iter(init);
      iter->first();
      todo.push(pair_state_iter(init, iter));
    }

    while (!todo.empty())
      {
	assert(root_.size() == arc.size());

	tgba_succ_iterator* succ = todo.top().second;

	if (succ->done())
	  {
	    const spot::state* curr = todo.top().first;

	    todo.pop();

	    numbered_state_heap::state_index_p spi = h_->index(curr);
	    assert(spi.first);
	    assert(!root_.empty());
	    if (root_.top().index == *spi.second)
	      {
		assert(!arc.empty());
		arc.pop();
		root_.pop();
		remove_component(curr);
	      }

	    delete succ;

	    continue;
	  }

	const spot::state* dest = succ->current_state();
	bdd acc = succ->current_acceptance_conditions();
	succ->next();

	numbered_state_heap::state_index_p spi = h_->find(dest);
	if (!spi.first)
	  {
	    h_->insert(dest, ++num);
	    si_[dest] = num;
	    state_scc_.push(dest);
	    root_.push(num);
	    arc.push(acc);
	    tgba_succ_iterator* iter = this->succ_iter(dest);
	    iter->first();
	    todo.push(pair_state_iter(dest, iter));
	    continue;
	  }

	if (*spi.second == -1)
	  continue;

	int threshold = *spi.second;
	while (threshold < root_.top().index)
	  {
	    assert(!root_.empty());
	    assert(!arc.empty());
	    acc |= root_.top().condition;
	    acc |= arc.top();
	    root_.pop();
	    arc.pop();

	    si_[state_scc_.top()] = threshold;
	    state_scc_.pop();
	  }

	root_.top().condition |= acc;

      }

    seen_map::iterator i;
    for (i = si_.begin(); i != si_.end(); ++i)
      {
	state_scc_v_[i->second] = i->first;
      }

    scc_computed_ = true;
  }

  ///////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////

  void
  tgba_reduc::prune_acc()
  {
    if (!scc_computed_)
      this->compute_scc();

    Sgi::hash_map<int, const spot::state*>::iterator i;
    for (i = state_scc_v_.begin(); i != state_scc_v_.end(); ++i)
      {
	if (is_not_accepting(i->second))
	    remove_acc(i->second);
      }
  }

  void
  tgba_reduc::remove_acc(const spot::state* s)
  {
    tgba_explicit::state* s1;
    seen_map::iterator sm = si_.find(s);
    sm = si_.find(s);
    int n = sm->second;

    for (sm == si_.begin(); sm != si_.end(); ++sm)
      {

	if (sm->second == n)
	  {
	    s1 = name_state_map_[tgba_explicit::format_state(s)];
	    s1 = const_cast<tgba_explicit::state*>(s1);
	    for (state::iterator i = s1->begin();
		 i != s1->end(); ++i)
	      (*i)->acceptance_conditions = bddfalse;
	  }
	/*
	  else
	  {
	  // FIXME
	  tgba_succ_iterator* si = this->succ_iter(sm->first);
	  spot::state* s2 = si->current_state();
	  seen_map::iterator sm2 = si_.find(s2);
	  if (sm2->second == n)
	  {
	  s1 = name_state_map_[tgba_explicit::format_state(sm2->first)];
	  for (state::iterator i = s1->begin();
	  i != s1->end(); ++i)
	  (*i)->acceptance_conditions = bddfalse;
	  }
	  delete s2;
	  delete si;
	  }
	*/

      }
  }

  bool
  tgba_reduc::is_not_accepting(const spot::state* s,
			       int n)
  {
    bool b = false;

    // First call of is_terminal //
    seen_map::const_iterator i;
    if (n == -1)
      {
	acc_ = bddfalse;
	b = true;
	assert(seen_ == 0);
	seen_ = new seen_map();
	i = si_.find(s);
	assert(i->first != 0);
	n = i->second;
      }
    ///////////////////////////////

    seen_map::const_iterator sm = seen_->find(s);
    if (sm == seen_->end())
      {
	// this state is visited for the first time.
	seen_->insert(std::pair<const spot::state*, int>(s, 1));
	i = si_.find(s);
	assert(i->first != 0);
	if (n != i->second)
	    return true;
      }
    else
      // This state is already visited.
      {
	delete s;
	s = 0;
	return true;
      }

    spot::state* s2;
    tgba_succ_iterator* j = this->succ_iter(s);
    for (j->first(); !j->done(); j->next())
      {
	s2 = j->current_state();
	i = si_.find(s2);
	assert(i->first != 0);
	if (n == i->second)
	  {
	    acc_ |= j->current_acceptance_conditions();
	    this->is_not_accepting(s2, n);
	  }
      }
    delete j;

    // First call of is_terminal //
    if (b)
      {
	for (seen_map::iterator i = seen_->begin();
	     i != seen_->end(); ++i)
	  {
	    s2 = const_cast<spot::state*>(i->first);
	    assert(s2 != 0);
	    delete dynamic_cast<tgba_explicit*>(s2);
	  }
	seen_->clear();
	delete seen_;
	seen_ = 0;
	return acc_ != this->all_acceptance_conditions();
      }
    ///////////////////////////////

    return true;
  }

  void
  tgba_reduc::delete_scc()
  {
    bool change = true;
    Sgi::hash_map<int, const spot::state*>::iterator i;
    spot::state* s;

    // we check if there is a terminal SCC we can be remove while
    // they have been one removed, because a terminal SCC removed
    // can generate a new terminal SCC
    while (change)
      {
	change = false;
	for (i = state_scc_v_.begin(); i != state_scc_v_.end(); ++i)
	  {
	    s = (i->second)->clone();

	    if (is_terminal(s))
	      {
		change = true;
		remove_scc(const_cast<spot::state*>(i->second));
		state_scc_v_.erase(i);
		break;
	      }
	    delete s;
	  }
      }
  }

  bool
  tgba_reduc::is_terminal(const spot::state* s, int n)
  {
    // a SCC is terminal if there are no transition
    // leaving the SCC AND she doesn't contain all
    // the acceptance condition.
    // So we can remove it safely without change the
    // automaton language.

    bool b = false;

    // First call of is_terminal //
    seen_map::const_iterator i;
    if (n == -1)
      {
	acc_ = bddfalse;
	b = true;
	assert(seen_ == 0);
	seen_ = new seen_map();
	i = si_.find(s);
	assert(i->first != 0);
	n = i->second;
      }
    ///////////////////////////////

    seen_map::const_iterator sm = seen_->find(s);
    if (sm == seen_->end())
      {
	// this state is visited for the first time.
	seen_->insert(std::pair<const spot::state*, int>(s, 1));
	i = si_.find(s);
	assert(i->first != 0);
	if (n != i->second)
	    return false;
      }
    else
      {
	// This state is already visited.
	delete s;
	s = 0;
	return true;
      }

    bool ret = true;
    spot::state* s2;
    tgba_succ_iterator* j = this->succ_iter(s);
    for (j->first(); !j->done(); j->next())
      {
	s2 = j->current_state();
	acc_ |= j->current_acceptance_conditions();
	ret &= this->is_terminal(s2, n);
	if (!ret)
	  break;
      }
    delete j;

    // First call of is_terminal //
    if (b)
      {
	for (seen_map::iterator i = seen_->begin();
	     i != seen_->end(); ++i)
	  {
	    s2 = const_cast<spot::state*>(i->first);
	    assert(s2 != 0);
	    delete dynamic_cast<tgba_explicit*>(s2);
	  }
	seen_->clear();
	delete seen_;
	seen_ = 0;
	if (acc_ == this->all_acceptance_conditions())
	  ret = false;
      }
    ///////////////////////////////

    return ret;
  }

  void
  tgba_reduc::remove_scc(spot::state* s)
  {
    // To remove a scc, we remove all his state.

    seen_map::iterator sm = si_.find(s);
    sm = si_.find(s);
    int n = sm->second;

    for (sm == si_.begin(); sm != si_.end(); ++sm)
      {
	if (sm->second == n)
	  {
	    this->remove_state(const_cast<spot::state*>(sm->first));
	    sm->second = -1;
	  }
      }

  }

  /*
  void
  tgba_reduc::remove_scc_depth_first(spot::state* s, int n)
  {
    if (n == -1)
      {
	assert(seen_ == 0);
	seen_ = new seen_map();
      }

    seen_map::const_iterator sm = seen_->find(s);
    if (sm == seen_->end())
	seen_->insert(std::pair<const spot::state*, int>(s, 1));
    else
	return;

    tgba_succ_iterator* j = this->succ_iter(s);
    for (j->first(); !j->done(); j->next())
      {
	this->remove_scc_depth_first(j->current_state(), 1);
      }
    this->remove_state(s);

    if (n == -1)
      {
	delete seen_;
	seen_ = 0;
      }
  }
  */

  /*
  bool
  tgba_reduc::is_alpha_ball(const spot::state* s, bdd label, int n)
  {
    /// FIXME
    // a SCC is alpha ball if she's terminal but with some acceptance
    // condition, and all transition have the same label.
    // So we replace this SCC by a single state.

    bool b = false;

    seen_map::const_iterator i;
    if ((n == -1) &&
	(label == bddfalse))
      {
	acc_ == bddfalse;
	b = true;
	assert(seen_ == 0);
	seen_ = new seen_map();
	i = si_.find(s);
	assert(i->first != 0);
	n = i->second;
      }

    seen_map::const_iterator sm = seen_->find(s);
    if (sm == seen_->end())
      {
	seen_->insert(std::pair<const spot::state*, int>(s, 1));
	i = si_.find(s);
	assert(i->first != 0);
	if (n != i->second)
	    return false;
      }
    else
      {
	return true;
      }

    bool ret = true;
    tgba_succ_iterator* j = this->succ_iter(s);
    for (j->first(); !j->done(); j->next())
      {
	acc_ |= j->current_acceptance_conditions();
	ret &= this->is_terminal(j->current_state(), n);
      }

    if (b)
      {
	delete seen_;
	seen_ = 0;
	if (acc_ == this->all_acceptance_conditions())
	  ret = false;
      }

    return ret;
  }
  */

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

  void
  tgba_reduc::display_scc(std::ostream& os)
  {


    while (!root_.empty())
      {
	os << "index : " << root_.top().index << std::endl;
	root_.pop();
      }

    seen_map::iterator i;
    for (i = si_.begin(); i != si_.end(); ++i)
      {
	os << " [label=\""
	   << this->format_state(i->first)
	   << "\"]"
	   << " scc : "
	   << i->second
	   << std::endl;
      }

    os << " Root of each SCC :"
       << std::endl;

    Sgi::hash_map<int, const spot::state*>::iterator j;
    for (j = state_scc_v_.begin(); j != state_scc_v_.end(); ++j)
      {
	os << " [label=\""
	   << this->format_state(j->second)
	   << "\"]"
	   << std::endl;
	state_scc_.pop();
      }

  }

}
