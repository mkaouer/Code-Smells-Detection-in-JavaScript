// -*- coding: utf-8 -*-
// Copyright (C) 2011, 2012, 2014 Laboratoire de Recherche et
// Developpement de l'Epita (LRDE)
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


#include "kripkeexplicit.hh"
#include "tgba/bddprint.hh"
#include "tgba/formula2bdd.hh"
#include <iostream>

namespace spot
{

  state_kripke::state_kripke()
    : bdd_ (bddtrue)
  {
  }

  void state_kripke::add_conditions(bdd f)
  {
    bdd_ &= f;
  }

  void state_kripke::add_succ(state_kripke* add_me)
  {
    // This method must add only state_kripke for now.
    state_kripke* to_add = down_cast<state_kripke*>(add_me);
    assert(to_add);
    succ_.push_back(to_add);
  }

  int state_kripke::compare(const state* other) const
  {
    // This method should not be called to compare states from different
    // automata, and all states from the same automaton will use the same
    // state class.
    const state_kripke* s = down_cast<const state_kripke*>(other);
    assert(s);
    return s - this;
  }

  size_t
  state_kripke::hash() const
  {
    return
    reinterpret_cast<const char*>(this) - static_cast<const char*>(0);
  }

  state_kripke*
  state_kripke::clone() const
  {
    return const_cast<state_kripke*>(this);
  }

  ////////////////////////////
  // Support for succ_iterator

  const std::list<state_kripke*>&
  state_kripke::get_succ() const
  {
    return succ_;
  }









  /////////////////////////////////////
  // kripke_explicit_succ_iterator

  kripke_explicit_succ_iterator::kripke_explicit_succ_iterator
  (const state_kripke* s, bdd cond)
    : kripke_succ_iterator(cond),
      s_(s)
  {
  }


  kripke_explicit_succ_iterator::~kripke_explicit_succ_iterator()
  {
  }

  void kripke_explicit_succ_iterator::first()
  {
    it_ = s_->get_succ().begin();
  }

  void kripke_explicit_succ_iterator::next()
  {
    ++it_;
  }

  bool kripke_explicit_succ_iterator::done() const
  {
    return it_ == s_->get_succ().end();
  }

  state_kripke* kripke_explicit_succ_iterator::current_state() const
  {
    assert(!done());
    return *it_;
  }

  /////////////////////////////////////
  // kripke_explicit


  kripke_explicit::kripke_explicit(bdd_dict* dict)
    : dict_(dict),
      init_(0)
  {
  }

  kripke_explicit::kripke_explicit(bdd_dict* dict,
                                   state_kripke*  init)
    : dict_(dict),
      init_ (init)
  {
  }


  std::string
  kripke_explicit::format_state(const spot::state* s) const
  {
    assert(s);
    const state_kripke* se = down_cast<const state_kripke*>(s);
    assert(se);
    std::map<const state_kripke*, std::string>::const_iterator it =
      sn_nodes_.find (se);
    assert(it != sn_nodes_.end());
    return it->second;
  }

  kripke_explicit::~kripke_explicit()
  {
    dict_->unregister_all_my_variables(this);
    std::map<const std::string, state_kripke*>::iterator it;
    for (it = ns_nodes_.begin(); it != ns_nodes_.end(); ++it)
    {
      state_kripke* del_me = it->second;
      delete del_me;
    }
  }

  state_kripke*
  kripke_explicit::get_init_state() const
  {
    return init_;
  }

  bdd_dict*
  kripke_explicit::get_dict() const
  {
    return dict_;
  }

  // FIXME : Change the bddtrue.
  kripke_explicit_succ_iterator*
  kripke_explicit::succ_iter(const spot::state* local_state,
                            const spot::state* global_state,
                            const tgba* global_automaton) const
  {
    const state_kripke* s = down_cast<const state_kripke*>(local_state);
    assert(s);
    (void) global_state;
    (void) global_automaton;
    state_kripke* it = const_cast<state_kripke*>(s);
    return new kripke_explicit_succ_iterator(it, bddtrue);
  }

  bdd
  kripke_explicit::state_condition(const state* s) const
  {
    const state_kripke* f = down_cast<const state_kripke*>(s);
    assert(f);
    return (f->as_bdd());
  }

  bdd
  kripke_explicit::state_condition(const std::string& name) const
  {
    std::map<const std::string, state_kripke*>::const_iterator it;
    it = ns_nodes_.find(name);
    assert(it != ns_nodes_.end());
    return state_condition(it->second);
  }

  const std::map<const state_kripke*, std::string>&
  kripke_explicit::sn_get() const
  {
    return sn_nodes_;
  }


  void kripke_explicit::add_state(std::string name,
                                 state_kripke* state)
  {
    if (ns_nodes_.find(name) == ns_nodes_.end())
    {
      ns_nodes_[name] = state;
      sn_nodes_[state] = name;
      if (!init_)
        init_ = state;
    }
  }

  void kripke_explicit::add_state(std::string name)
  {
    if (ns_nodes_.find(name) == ns_nodes_.end())
    {
      state_kripke* state = new state_kripke;
      add_state(name, state);
    }
  }

  void kripke_explicit::add_transition(state_kripke* source,
                                       const state_kripke* dest)
  {
    state_kripke* Dest = const_cast<state_kripke*>(dest);
    source->add_succ(Dest);
  }

  void kripke_explicit::add_transition(std::string source,
                                       const state_kripke* dest)
  {
    add_transition(ns_nodes_[source], dest);
  }

  void kripke_explicit::add_transition(std::string source,
                                       std::string dest)
  {
    std::map<const std::string, state_kripke*>::iterator destination
      = ns_nodes_.find(dest);

    if (ns_nodes_.find(dest) == ns_nodes_.end())
    {
      state_kripke* neo = new state_kripke;
      add_state(dest, neo);
      add_transition(source, neo);
    }
    else
    {
      add_transition(source, destination->second);
    }
  }

  void kripke_explicit::add_conditions(bdd add,
                                       state_kripke* on_me)
  {
    on_me->add_conditions(add);
  }

  void kripke_explicit::add_conditions(bdd add,
                                       std::string on_me)
  {
    add_conditions(add, ns_nodes_[on_me]);
  }


  void kripke_explicit::add_condition(const ltl::formula* f,
                                      std::string on_me)
  {
    add_conditions(formula_to_bdd(f, dict_, this), on_me);
    f->destroy();
  }


} // End namespace spot.
