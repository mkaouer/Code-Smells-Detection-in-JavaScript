// Copyright (C) 2008, 2011 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2004, 2005 Laboratoire d'Informatique de Paris 6 (LIP6),
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
  /// Number of spoiler node with a one priority (see icalp2001).
  /// The one priority is represent by a \a acceptance_condition_visited_
  /// which differ of bddfalse.
  /// This spoiler node are looser for the duplicator.
  /// FIXME: get rid of these ugly globals
  static int nb_spoiler_loose_;

  static int nb_spoiler;
  static int nb_duplicator;

  static bdd all_acc_cond = bddfalse;

  static std::vector<bool*> bool_v;

  //static int nb_node = 0;

  //seen_map_node seen_node_;

  ///////////////////////////////////////////////////////////////////////
  // spoiler_node_delayed

  spoiler_node_delayed::spoiler_node_delayed(const state* d_node,
					     const state* s_node,
					     bdd a,
					     int num)
    : spoiler_node(d_node, s_node, num),
      acceptance_condition_visited_(a)
  {
    ++nb_spoiler;
    progress_measure_ = 0;
    if (acceptance_condition_visited_ != bddfalse)
      ++nb_spoiler_loose_;
    lead_2_acc_all_ = false;

    seen_ = false;
  }

  spoiler_node_delayed::~spoiler_node_delayed()
  {
    if (acceptance_condition_visited_ != bddfalse)
      --nb_spoiler_loose_;
  }

  bool
  spoiler_node_delayed::set_win()
  {
    // We take the max of the progress measure of the successor node
    // because we are on a spoiler.

    if (lnode_succ->empty())
      progress_measure_ = nb_spoiler_loose_ + 1;

    if (progress_measure_ >= nb_spoiler_loose_ + 1)
      return false;

    bool change;
    int tmpmax = 0;
    int tmp = 0;
    int tmpmaxwin = -1;
    sn_v::iterator i = lnode_succ->begin();
    if (i != lnode_succ->end())
      {
	tmpmax =
	  static_cast<duplicator_node_delayed*>(*i)->get_progress_measure();
	if (static_cast<duplicator_node_delayed*>(*i)->get_lead_2_acc_all())
	  tmpmaxwin = tmpmax;
	++i;
      }
    for (; i != lnode_succ->end(); ++i)
      {
	tmp =
	  static_cast<duplicator_node_delayed*>(*i)->get_progress_measure();
	if (tmp > tmpmax)
	  tmpmax = tmp;
	if (static_cast<duplicator_node_delayed*>(*i)->get_lead_2_acc_all() &&
	    (tmp > tmpmaxwin))
	  tmpmaxwin = tmp;
      }

    if (tmpmaxwin != -1)
      tmpmax = tmpmaxwin;

    // If the priority of the node is 1
    // acceptance_condition_visited_ != bddfalse
    // then we increment the progress measure of 1.
    if ((acceptance_condition_visited_ != bddfalse) &&
	(tmpmax < (nb_spoiler_loose_ + 1)))
      ++tmpmax;

    change = (progress_measure_ < tmpmax);

    progress_measure_ = tmpmax;
    return change;
  }

  bool
  spoiler_node_delayed::compare(spoiler_node* n)
  {
    return (this->spoiler_node::compare(n) &&
	    (acceptance_condition_visited_ ==
	     static_cast<spoiler_node_delayed*>(n)->
	     get_acceptance_condition_visited()));
  }

  std::string
  spoiler_node_delayed::to_string(const tgba* a)
  {
    std::ostringstream os;

    // print the node.
    os << num_
       << " [shape=box, label=\"("
       << a->format_state(sc_->first)
       << ", "
       << a->format_state(sc_->second)
       << ", ";
    //bdd_print_acc(os, a->get_dict(), acceptance_condition_visited_);
    if (acceptance_condition_visited_ == bddfalse)
      {
	os << "false";
      }
    else
      {
	os << "ACC";
      }
    os << ")"
       << " pm = " << progress_measure_;
    if (lead_2_acc_all_)
      os << ", 1\"]";
    else
      os << ", 0\"]";
    os << std::endl;

    return os.str();
  }

  bdd
  spoiler_node_delayed::get_acceptance_condition_visited() const
  {
    return acceptance_condition_visited_;
  }

  int
  spoiler_node_delayed::get_progress_measure() const
  {
    if ((acceptance_condition_visited_ == bddfalse) &&
	(progress_measure_ != (nb_spoiler_loose_ + 1)))
      return 0;
    else
      return progress_measure_;
  }

  bool
  spoiler_node_delayed::get_lead_2_acc_all()
  {
    return lead_2_acc_all_;
  }


  bool
  spoiler_node_delayed::set_lead_2_acc_all(bdd acc)
  {
    if (!seen_)
      {
	seen_ = true;
	for (sn_v::iterator i = lnode_succ->begin();
	     i != lnode_succ->end(); ++i)
	  static_cast<duplicator_node_delayed*>(*i)->set_lead_2_acc_all(acc);
      }
    else
      {
	if (acc == all_acc_cond)
	  lead_2_acc_all_ = true;
      }
    return lead_2_acc_all_;
  }


  ///////////////////////////////////////////////////////////////////////
  // duplicator_node_delayed

  duplicator_node_delayed::duplicator_node_delayed(const state* d_node,
						   const state* s_node,
						   bdd l,
						   bdd a,
						   int num)
    : duplicator_node(d_node, s_node, l, a, num)
  {
    ++nb_duplicator;
    progress_measure_ = 0;
    all_acc_cond |= a;
    lead_2_acc_all_ = false;

    seen_ = false;
  }

  duplicator_node_delayed::~duplicator_node_delayed()
  {
  }

  bool
  duplicator_node_delayed::set_win()
  {
    // We take the min of the progress measure of the successor node
    // because we are on a duplicator.

    if (lnode_succ->empty())
      progress_measure_ = nb_spoiler_loose_ + 1;

    if (progress_measure_ >= nb_spoiler_loose_ + 1)
      return false;

    bool change;
    int tmpmin = 0;
    int tmp = 0;
    int tmpminwin = -1;
    sn_v::iterator i = lnode_succ->begin();
    if (i != lnode_succ->end())
      {
	tmpmin =
	  static_cast<spoiler_node_delayed*>(*i)->get_progress_measure();
	if (static_cast<spoiler_node_delayed*>(*i)->get_lead_2_acc_all())
	  tmpminwin = tmpmin;
	++i;
      }
    for (; i != lnode_succ->end(); ++i)
      {
	tmp = static_cast<spoiler_node_delayed*>(*i)->get_progress_measure();
	if (tmp < tmpmin)
	  tmpmin = tmp;
	if (static_cast<spoiler_node_delayed*>(*i)->get_lead_2_acc_all() &&
	    (tmp > tmpminwin))
	  tmpminwin = tmp;
      }
    if (tmpminwin != -1)
      tmpmin = tmpminwin;

    change = (progress_measure_ < tmpmin);
    progress_measure_ = tmpmin;
    return change;
  }

  std::string
  duplicator_node_delayed::to_string(const tgba* a)
  {
    std::ostringstream os;

    // print the node.
    os << num_
       << " [shape=box, label=\"("
       << a->format_state(sc_->first)
       << ", "
       << a->format_state(sc_->second)
       << ", ";
    if (label_ == bddfalse)
      os << "0";
    else if (label_ == bddtrue)
      os << "1";
    else
      bdd_print_acc(os, a->get_dict(), label_);
    //<< ", ";
    //bdd_print_acc(os, a->get_dict(), acc_);
    os << ")"
       << " pm = " << progress_measure_;
    if (lead_2_acc_all_)
      os << ", 1\"]";
    else
      os << ", 0\"]";
    os << std::endl;

    return os.str();
  }

  bool
  duplicator_node_delayed::implies_label(bdd l)
  {
    return ((l | !label_) == bddtrue);
  }

  bool
  duplicator_node_delayed::implies_acc(bdd a)
  {
    return ((a | !acc_) == bddtrue);
  }

  int
  duplicator_node_delayed::get_progress_measure()
  {
    return progress_measure_;
  }

  bool
  duplicator_node_delayed::get_lead_2_acc_all()
  {
    return lead_2_acc_all_;
  }

  bool
  duplicator_node_delayed::set_lead_2_acc_all(bdd acc)
  {
    acc |= acc_;
    if (!seen_)
      {
	seen_ = true;
	for (sn_v::iterator i = lnode_succ->begin();
	     i != lnode_succ->end(); ++i)
	  lead_2_acc_all_
	    |= static_cast<spoiler_node_delayed*>(*i)->set_lead_2_acc_all(acc);
      }
    return lead_2_acc_all_;
  }

  ///////////////////////////////////////////////////////////////////////
  // parity_game_graph_delayed

  int
  parity_game_graph_delayed::nb_set_acc_cond()
  {
    return automata_->number_of_acceptance_conditions();
  }

  // We build only node which are reachable
  void
  parity_game_graph_delayed::build_graph()
  {
    // We build only some "basic" spoiler node.
    sn_v tab_temp;
    s_v::iterator i1;
    for (i1 = tgba_state_.begin(); i1 != tgba_state_.end(); ++i1)
      {

	// spoiler node are all state couple (i,j)
	s_v::iterator i2;
	for (i2 = tgba_state_.begin();
	     i2 != tgba_state_.end(); ++i2)
	  {
	    //std::cout << "add spoiler node" << std::endl;
	    ++nb_spoiler;
	    spoiler_node_delayed* n1
	      = new spoiler_node_delayed(*i1, *i2,
					 bddfalse,
					 nb_node_parity_game++);
	    spoiler_vertice_.push_back(n1);
	    tab_temp.push_back(n1);
	  }
      }

    sn_v::iterator j;
    std::ostringstream os;
    for (j = tab_temp.begin(); j != tab_temp.end(); ++j)
      {
	// We add a link between a spoiler and a (new) duplicator.
	// The acc of the duplicator must contains the
	// acceptance_condition_visited_ of the spoiler.
	//std::cout << "build_link : iter " << ++n << std::endl;
	build_recurse_successor_spoiler(*j, os);

      }
  }

  void
  parity_game_graph_delayed::
  build_recurse_successor_spoiler(spoiler_node* sn,
				  std::ostringstream& os)
  {
    assert(sn);

    tgba_succ_iterator* si = automata_->succ_iter(sn->get_spoiler_node());

    for (si->first(); !si->done(); si->next())
      {
	bdd btmp = si->current_acceptance_conditions() |
	  static_cast<spoiler_node_delayed*>(sn)->
	  get_acceptance_condition_visited();

	s_v::iterator i1;
	state* s;
	for (i1 = tgba_state_.begin();
	     i1 != tgba_state_.end(); ++i1)
	  {

	    s  = si->current_state();
	    if (s->compare(*i1) == 0)
	      {
		s->destroy();
		duplicator_node_delayed* dn
		  = add_duplicator_node_delayed(*i1,
						sn->get_duplicator_node(),
						si->current_condition(),
						btmp,
						nb_node_parity_game++);

		if (!(sn->add_succ(dn)))
		    continue;

		std::ostringstream os2;
		os2 << os.str() << " ";
		build_recurse_successor_duplicator(dn, sn, os2);
	      }
	    else
	      s->destroy();
	  }
      }

    delete si;
  }

  void
  parity_game_graph_delayed::
  build_recurse_successor_duplicator(duplicator_node* dn,
				     spoiler_node* ,
				     std::ostringstream& os)
  {
    tgba_succ_iterator* si = automata_->succ_iter(dn->get_duplicator_node());

    for (si->first(); !si->done(); si->next())
      {

	// if si->current_condition() doesn't implies dn->get_label()
	// then duplicator can't play.
	if ((si->current_condition() | !dn->get_label()) != bddtrue)
	  {
	    continue;
	  }

	bdd btmp = dn->get_acc() -
	  (dn->get_acc() & si->current_acceptance_conditions());

	s_v::iterator i1;
	state* s;
	for (i1 = tgba_state_.begin();
	     i1 != tgba_state_.end(); ++i1)
	  {
	    s  = si->current_state();

	    if (s->compare(*i1) == 0)
	      {
		s->destroy();
		spoiler_node_delayed* sn_n
		  = add_spoiler_node_delayed(dn->get_spoiler_node(),
					     *i1,
					     btmp,
					     nb_node_parity_game++);

		if (!(dn->add_succ(sn_n)))
		  continue;

		std::ostringstream os2;
		os2 << os.str() << " ";
		build_recurse_successor_spoiler(sn_n, os2);
	      }
	    else
	      s->destroy();
	  }
      }

    delete si;
  }

  duplicator_node_delayed*
  parity_game_graph_delayed::add_duplicator_node_delayed(const spot::state* sn,
							 const spot::state* dn,
							 bdd acc,
							 bdd label,
							 int nb)
  {
    bool exist = false;

    duplicator_node_delayed* dn_n
      = new duplicator_node_delayed(sn, dn, acc, label, nb);

    for (std::vector<duplicator_node*>::iterator i
	       = duplicator_vertice_.begin();
	     i != duplicator_vertice_.end(); ++i)
      {
	if (dn_n->compare(*i))
	  {
	    exist = true;
	    delete dn_n;
	    dn_n = static_cast<duplicator_node_delayed*>(*i);
	    break;
	  }
      }

    if (!exist)
      duplicator_vertice_.push_back(dn_n);

    return dn_n;
  }

  spoiler_node_delayed*
  parity_game_graph_delayed::add_spoiler_node_delayed(const spot::state* sn,
						      const spot::state* dn,
						      bdd acc,
						      int nb)
  {
    bool exist = false;

    //bool l2a = (acc != automata_->all_acceptance_conditions());
    spoiler_node_delayed* sn_n
      = new spoiler_node_delayed(sn, dn, acc, nb);

    for (std::vector<spoiler_node*>::iterator i
	   = spoiler_vertice_.begin();
	 i != spoiler_vertice_.end(); ++i)
      {
	if (sn_n->compare(*i))
	  {
	    exist = true;
	    delete sn_n;
	    sn_n = static_cast<spoiler_node_delayed*>(*i);
	    break;
	  }
      }

    if (!exist)
	spoiler_vertice_.push_back(sn_n);

    return sn_n;
  }

  void
  parity_game_graph_delayed::lift()
  {
    // Jurdzinski's algorithm
    bool change = true;

    while (change)
      {
	//std::cout << "lift::change = true" << std::endl;
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
    //std::cout << "lift::change = false" << std::endl;
  }

  delayed_simulation_relation*
  parity_game_graph_delayed::get_relation()
  {
    delayed_simulation_relation* rel = new delayed_simulation_relation;
    state_couple* p = 0;
    seen_map::iterator j;

    // This does not work for generalized automata.  A tarjan-like
    // algorithm is required to tell whether a state can lead to an
    // acceptance cycle.
    if (this->nb_set_acc_cond() > 1)
      return rel;

    for (std::vector<spoiler_node*>::iterator i
	   = spoiler_vertice_.begin();
	 i != spoiler_vertice_.end(); ++i)
      {
	if ((static_cast<spoiler_node_delayed*>(*i)->get_progress_measure()
	     < nb_spoiler_loose_ + 1) &&
	    (static_cast<spoiler_node_delayed*>(*i)
	     ->get_acceptance_condition_visited() == bddfalse))
	  {
	    p = new state_couple((*i)->get_spoiler_node(),
				 (*i)->get_duplicator_node());
	    rel->push_back(p);

	    // We remove the state in rel from seen
	    // because the destructor of
	    // tgba_reachable_iterator_breadth_first
	    // delete all the state.

	    if ((j = seen.find(p->first)) != seen.end())
	      seen.erase(j);
	    if ((j = seen.find(p->second)) != seen.end())
	      seen.erase(j);
	  }

      }

    return rel;
  }

  parity_game_graph_delayed::~parity_game_graph_delayed()
  {
  }

  parity_game_graph_delayed::parity_game_graph_delayed(const tgba* a)
    : parity_game_graph(a)
  {
    nb_spoiler_loose_ = 0;
    this->build_graph();
    this->lift();
  }

  ///////////////////////////////////////////
  delayed_simulation_relation*
  get_delayed_relation_simulation(const tgba* f, std::ostream& os, int opt)
  {
    parity_game_graph_delayed* G = new parity_game_graph_delayed(f);
    delayed_simulation_relation* rel = G->get_relation();
    if (opt == 1)
      G->print(os);
    delete G;
    return rel;
  }

  void
  free_relation_simulation(delayed_simulation_relation* rel)
  {
    if (rel == 0)
      return;

    Sgi::hash_map<const spot::state*, int,
      state_ptr_hash, state_ptr_equal> seen;
    Sgi::hash_map<const spot::state*, int,
      state_ptr_hash, state_ptr_equal>::iterator j;

    delayed_simulation_relation::iterator i;
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
	ptr->destroy();
      }
  }

}
