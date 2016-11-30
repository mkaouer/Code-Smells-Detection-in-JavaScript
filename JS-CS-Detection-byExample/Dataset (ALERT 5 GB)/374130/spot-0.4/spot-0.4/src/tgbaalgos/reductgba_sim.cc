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

#include "reductgba_sim.hh"
#include "tgba/bddprint.hh"

namespace spot
{
  ///////////////////////////////////////////////////////////////////////
  // spoiler_node

  spoiler_node::spoiler_node(const state* d_node,
			     const state* s_node,
			     int num)
  {
    num_ = num;
    sc_ = new state_couple(d_node, s_node);
    lnode_succ = new sn_v;
    lnode_pred = new sn_v;
    not_win = false;
  }

  spoiler_node::~spoiler_node()
  {
    lnode_succ->clear();
    lnode_pred->clear();
    delete lnode_succ;
    delete lnode_pred;
    delete sc_;
  }

  bool
  spoiler_node::add_succ(spoiler_node* n)
  {
    bool exist = false;
    for (sn_v::iterator i = lnode_succ->begin();
	 i != lnode_succ->end(); ++i)
      if ((*i == n) ||
	  ((*i)->compare(n) == true))
	exist = true;
    if (exist)
      return false;

    lnode_succ->push_back(n);
    n->add_pred(this);
    return true;
  }

  void
  spoiler_node::del_succ(spoiler_node* n)
  {
    for (sn_v::iterator i = lnode_succ->begin();
	 i != lnode_succ->end();)
      {
	if (*i == n)
	  {
	    i = lnode_succ->erase(i);
	  }
	else
	  ++i;
      }
  }

  void
  spoiler_node::add_pred(spoiler_node* n)
  {
    lnode_pred->push_back(n);
  }

  void
  spoiler_node::del_pred()
  {
    for (sn_v::iterator i = lnode_pred->begin();
	 i != lnode_pred->end(); ++i)
      (*i)->del_succ(this);
  }

  bool
  spoiler_node::set_win()
  {
    bool change = not_win;
    for (std::vector<spoiler_node*>::iterator i = lnode_succ->begin();
	 i != lnode_succ->end(); ++i)
      {
	not_win |= (*i)->not_win;
      }
    if (change != not_win)
      for (std::vector<spoiler_node*>::iterator i = lnode_pred->begin();
	   i != lnode_pred->end(); ++i)
	(*i)->set_win();

    return change != not_win;
  }

  std::string
  spoiler_node::to_string(const tgba* a)
  {
    std::ostringstream os;
    os << num_
       << " [shape=box, label=\"("
       << a->format_state(sc_->first)
       << ", "
       << a->format_state(sc_->second)
       << ")\"]"
       << std::endl;
    return os.str();
  }

  std::string
  spoiler_node::succ_to_string()
  {
    std::ostringstream os;
    sn_v::iterator i;
    for (i = lnode_succ->begin(); i != lnode_succ->end(); ++i)
      {
	os << num_ << " -> " << (*i)->num_ << std::endl;
      }
    return os.str();
  }

  bool
  spoiler_node::compare(spoiler_node* n)
  {
    return (((sc_->first)->compare((n->get_pair())->first) == 0) &&
	    ((sc_->second)->compare((n->get_pair())->second) == 0));
  }

  int
  spoiler_node::get_nb_succ()
  {
    return lnode_succ->size();
  }

  const state*
  spoiler_node::get_spoiler_node()
  {
    return sc_->first;
  }

  const state*
  spoiler_node::get_duplicator_node()
  {
    return sc_->second;
  }

  state_couple*
  spoiler_node::get_pair()
  {
    return sc_;
  }

  ///////////////////////////////////////////////////////////////////////
  // duplicator_node

  duplicator_node::duplicator_node(const state* d_node,
				   const state* s_node,
				   bdd l,
				   bdd a,
				   int num)
    : spoiler_node(d_node, s_node, num),
      label_(l),
      acc_(a)
  {
  }

  duplicator_node::~duplicator_node()
  {
  }

  bool
  duplicator_node::set_win()
  {
    bool change = not_win;

    if (!this->get_nb_succ())
      not_win = true;
    else
      {
	not_win = true;
	for (std::vector<spoiler_node*>::iterator i = lnode_succ->begin();
	     i != lnode_succ->end(); ++i)
	  {
	    not_win &= (*i)->not_win;
	  }
      }
    if (change != not_win)
      for (std::vector<spoiler_node*>::iterator i = lnode_pred->begin();
	   i != lnode_pred->end(); ++i)
	(*i)->set_win();

    return change != not_win;
  }

  std::string
  duplicator_node::to_string(const tgba* a)
  {
    std::ostringstream os;
    os << num_
       << " [shape=box, label=\"("
       << a->format_state(sc_->first)
       << ", "
       << a->format_state(sc_->second)
       << ", ";
    bdd_print_acc(os, a->get_dict(), label_);
    os << ", ";
    bdd_print_acc(os, a->get_dict(), acc_);
    os << ")\"]"
       << std::endl;
    return os.str();
  }

  bool
  duplicator_node::compare(spoiler_node* n)
  {
    return (this->spoiler_node::compare(n) &&
	    (label_ == dynamic_cast<duplicator_node*>(n)->get_label()) &&
	    (acc_ == dynamic_cast<duplicator_node*>(n)->get_acc()));
  }

  bdd
  duplicator_node::get_label() const
  {
    return label_;
  }

  bdd
  duplicator_node::get_acc() const
  {
    return acc_;
  }

  bool
  duplicator_node::match(bdd l, bdd a)
  {
    return ((l == label_) && (a == acc_));
  }

  bool
  duplicator_node::implies(bdd l, bdd a)
  {
    // if (a | !b) == true then (a => b).
    return (((l | !label_) == bddtrue) &&
	    ((a | !acc_) == bddtrue));
  }

  ///////////////////////////////////////////////////////////////////////
  // parity_game_graph

  void
  parity_game_graph::start()
  {
  }

  void
  parity_game_graph::end()
  {
  }

  void
  parity_game_graph::process_state(const state* s,
				    int ,
				    tgba_succ_iterator*)
  {
    tgba_state_.push_back(s);
  }

  void
  parity_game_graph::process_link(int ,
				   int ,
				   const tgba_succ_iterator*)
  {
  }

  void
  parity_game_graph::print(std::ostream& os)
  {
    std::vector<spoiler_node*>::iterator i1;
    std::vector<duplicator_node*>::iterator i2;

    int n = 0;

    os << "digraph G {" << std::endl;

    os << "{" << std::endl
       << "rank = same;" << std::endl
       << "node [color=red];" << std::endl;
    for (i1 = spoiler_vertice_.begin();
	 i1 != spoiler_vertice_.end(); ++i1)
      {
	os << (*i1)->to_string(automata_);
	++n;
	if (n > 20)
	  {
	    n = 0;
	    os << "}" << std::endl << std::endl
	       << "{" << std::endl
	       << "rank = same" << std::endl
	       << "node [color=red];" << std::endl;
	  }
      }
    os << "}" << std::endl;

    n = 0;
    os << "{" << std::endl
       << "rank = same;" << std::endl
       << "node [color=green];" << std::endl;
    for (i2 = duplicator_vertice_.begin();
	 i2 != duplicator_vertice_.end(); ++i2)
      {
	os << (*i2)->to_string(automata_);
	++n;
	if (n > 20)
	  {
	    n = 0;
	    os << "}" << std::endl << std::endl
	       << "{" << std::endl
	       << "rank = same" << std::endl
	       << "node [color=green];" << std::endl;
	  }
      }
    os << "}" << std::endl << std::endl;

    os << "edge [color=red];" << std::endl;
    for (i1 = spoiler_vertice_.begin();
	 i1 != spoiler_vertice_.end(); ++i1)
      {
	os << (*i1)->succ_to_string();
      }

    os << std::endl
       << "edge [color=green];" << std::endl;
    for (i2 = duplicator_vertice_.begin();
	 i2 != duplicator_vertice_.end(); ++i2)
      {
	os << (*i2)->succ_to_string();
      }

    os << "}" << std::endl;

  }

  parity_game_graph::~parity_game_graph()
  {
    std::vector<spoiler_node*>::iterator i1;
    std::vector<duplicator_node*>::iterator i2;

    for (i1 = spoiler_vertice_.begin();
	 i1 != spoiler_vertice_.end(); ++i1)
      {
	delete *i1;
      }

    for (i2 = duplicator_vertice_.begin();
	 i2 != duplicator_vertice_.end(); ++i2)
      {
	delete *i2;
      }

    spoiler_vertice_.clear();
    duplicator_vertice_.clear();
  }

  parity_game_graph::parity_game_graph(const tgba* a)
    : tgba_reachable_iterator_breadth_first(a)
  {
    this->run();
    nb_node_parity_game = 0;
  }

  ///////////////////////////////////////////////////////////////////////
  // parity_game_graph_direct

  void
  parity_game_graph_direct::build_graph()
  {
    tgba_succ_iterator* si = 0;
    typedef std::pair<bdd, bdd> couple_bdd;
    couple_bdd *p = 0;
    std::vector<couple_bdd*>* trans = 0;
    bool exist = false;
    spot::state* s = 0;

    for (std::vector<const state*>::iterator i = tgba_state_.begin();
	 i != tgba_state_.end(); ++i)
      {

	// spoiler node are all state couple (i,j)
	for (std::vector<const state*>::iterator j = tgba_state_.begin();
	     j != tgba_state_.end(); ++j)
	  {
	    spoiler_node* n1 = new spoiler_node(*i,
						*j,
						nb_node_parity_game++);
	    spoiler_vertice_.push_back(n1);
	  }

	// duplicator node are all state couple where
	// the first state i are reachable.
	trans = new std::vector<couple_bdd*>;
        for (std::vector<const state*>::iterator j = tgba_state_.begin();
	     j != tgba_state_.end(); ++j)
	  {
	    si = automata_->succ_iter(*j);
	    for (si->first(); !si->done(); si->next())
	      {

		// if there exist a predecessor of i named j
		s = si->current_state();
		if (s->compare(*i) == 0)
		  {

		    // p is the label of the transition j->i
		    p = new couple_bdd(si->current_condition(),
				       si->current_acceptance_conditions());

		    // If an other predecessor of i has the same label p
		    // to reach i, then we don't compute the duplicator node.
		    exist = false;
		    for (std::vector<couple_bdd*>::iterator v
			   = trans->begin();
			 v != trans->end(); ++v)
		      {
			if ((si->current_condition() == (*v)->first) &&
			    (si->current_acceptance_conditions()
			     == (*v)->second))
			  exist = true;
		      }

		    if (!exist)
		      {
			// We build all the state couple with the label p.
			trans->push_back(p);
			for (std::vector<const state*>::iterator s
			       = tgba_state_.begin();
			     s != tgba_state_.end(); ++s)
			  {
			    duplicator_node* n2
			      = new
			      duplicator_node(*i,
					      *s,
					      si->current_condition(),
					      si
					      ->current_acceptance_conditions(),
					      nb_node_parity_game++);
			    duplicator_vertice_.push_back(n2);
			  }
		      }
		    else
		      delete p;
		  }
		delete s;
	      }
	    delete si;
	  }
	std::vector<couple_bdd*>::iterator i2;
	for (i2 = trans->begin(); i2 != trans->end(); ++i2)
	  {
	    delete *i2;
	  }
	delete trans;
      }
  }

  void
  parity_game_graph_direct::build_link()
  {
    int nb_ds = 0;
    int nb_sd = 0;
    spot::state* s = 0;

    // for each couple of (spoiler, duplicator)
    for (std::vector<spoiler_node*>::iterator i
	   = spoiler_vertice_.begin(); i != spoiler_vertice_.end(); ++i)
      {
	for (std::vector<duplicator_node*>::iterator j
	       = duplicator_vertice_.begin();
	     j != duplicator_vertice_.end(); ++j)
	  {
	    // We add a link between a duplicator and a spoiler.
	    if ((*j)->get_spoiler_node()
		->compare((*i)->get_spoiler_node()) == 0)
	      {
		tgba_succ_iterator* si
		  = automata_->succ_iter((*j)->get_duplicator_node());
		for (si->first(); !si->done(); si->next())
		  {
		    s = si->current_state();
		    if ((s->compare((*i)->get_duplicator_node()) == 0) &&
			(*j)->implies(si->current_condition(),
				      si->current_acceptance_conditions()))
		      {
			(*j)->add_succ(*i);
			++nb_ds;
		      }
		    delete s;
		  }
		delete si;
	      }

	    // We add a link between a spoiler and a duplicator.
	    if ((*j)->get_duplicator_node()
		->compare((*i)->get_duplicator_node()) == 0)
	      {
		tgba_succ_iterator* si
		  = automata_->succ_iter((*i)->get_spoiler_node());
		for (si->first(); !si->done(); si->next())
		  {
		    s = si->current_state();
		    if ((s->compare((*j)->get_spoiler_node()) == 0) &&
			(*j)->match(si->current_condition(),
				    si->current_acceptance_conditions()))
		      {
			(*i)->add_succ(*j);
			++nb_sd;
		      }
		    delete s;
		  }
		delete si;
	      }
	  }
      }
  }

  void
  parity_game_graph_direct::lift()
  {
    bool change = true;

    while (change)
      {
	change = false;
	for (std::vector<duplicator_node*>::iterator i
	       = duplicator_vertice_.begin();
	     i != duplicator_vertice_.end(); ++i)
	  {
	    change |= (*i)->set_win();
	  }
	for (std::vector<spoiler_node*>::iterator i
	       = spoiler_vertice_.begin();
	     i != spoiler_vertice_.end(); ++i)
	  {
	    change |= (*i)->set_win();
	  }
      }
  }

  direct_simulation_relation*
  parity_game_graph_direct::get_relation()
  {
    direct_simulation_relation* rel = new direct_simulation_relation;
    state_couple* p = 0;
    seen_map::iterator j;

    for (std::vector<spoiler_node*>::iterator i
	   = spoiler_vertice_.begin();
	 i != spoiler_vertice_.end(); ++i)
      {
	if (!(*i)->not_win)
	  {
	    p = new state_couple((*i)->get_spoiler_node(),
				 (*i)->get_duplicator_node());
	    rel->push_back(p);


	    // We remove the state in rel from seen
	    // because the destructor of
	    // tgba_reachable_iterator_breadth_first
	    // delete all instance of state.


   	    if ((j = seen.find(p->first)) != seen.end())
	      seen.erase(j);
	    if ((j = seen.find(p->second)) != seen.end())
	      seen.erase(j);

	  }
      }

    return rel;
  }

  parity_game_graph_direct::~parity_game_graph_direct()
  {
  }

  parity_game_graph_direct::parity_game_graph_direct(const tgba* a)
    : parity_game_graph(a)
  {
    this->build_graph();
    this->build_link();
    this->lift();
  }

  ///////////////////////////////////////////////////////////////////////

  direct_simulation_relation*
  get_direct_relation_simulation(const tgba* f,
				 std::ostream& os,
				 int opt)
  {
    parity_game_graph_direct* G = new parity_game_graph_direct(f);
    direct_simulation_relation* rel = G->get_relation();
    if (opt == 1)
      G->print(os);
    delete G;
    return rel;
  }

  void
  free_relation_simulation(direct_simulation_relation* rel)
  {
    if (rel == 0)
      return;

    Sgi::hash_map<const spot::state*, int,
      state_ptr_hash, state_ptr_equal> seen;
    Sgi::hash_map<const spot::state*, int,
      state_ptr_hash, state_ptr_equal>::iterator j;

    simulation_relation::iterator i;
    for (i = rel->begin(); i != rel->end(); ++i)
      {
	if ((j = seen.find((*i)->first)) == seen.end())
	    seen[(*i)->first] = 0;

	if ((j = seen.find((*i)->second)) == seen.end())
	    seen[(*i)->second] = 0;

	delete *i;
      }
    delete rel;
    rel = 0;

    for (j = seen.begin(); j != seen.end();)
      {
	const state* ptr = j->first;
	++j;
	delete ptr;
      }
  }

  bool
  is_include(const tgba*, const tgba*)
  {
    return false;
  }

  tgba*
  reduc_tgba_sim(const tgba* f, int opt)
  {
    spot::tgba_reduc* automatareduc = new  spot::tgba_reduc(f);

    if (opt & (Reduce_quotient_Dir_Sim | Reduce_transition_Dir_Sim))
      {
	direct_simulation_relation* rel
	  = get_direct_relation_simulation(automatareduc, std::cout);

	automatareduc->display_rel_sim(rel, std::cout);
	automatareduc->quotient_state(rel);
	automatareduc->delete_transitions(rel);

	free_relation_simulation(rel);
      }
    else
      if (opt & (Reduce_quotient_Del_Sim | Reduce_transition_Del_Sim))
	{
	  delayed_simulation_relation* rel
	    = get_delayed_relation_simulation(automatareduc, std::cout);

	  automatareduc->display_rel_sim(rel, std::cout);
	  automatareduc->quotient_state(rel);
	  automatareduc->delete_transitions(rel);

	  free_relation_simulation(rel);
	}

    if (opt & Reduce_Scc)
      {
	automatareduc->prune_scc();
      }

    return automatareduc;
  }

}
