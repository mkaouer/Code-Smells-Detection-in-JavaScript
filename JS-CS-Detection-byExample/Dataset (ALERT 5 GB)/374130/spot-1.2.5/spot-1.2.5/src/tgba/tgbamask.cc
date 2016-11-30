// -*- coding: utf-8 -*-
// Copyright (C) 2013 Laboratoire de Recherche et Développement
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

#include "tgbamask.hh"
#include <vector>

namespace spot
{
  namespace
  {
    struct transition
    {
      const state* dest;
      bdd cond;
      bdd acc;
    };
    typedef std::vector<transition> transitions;

    struct succ_iter_filtered: public tgba_succ_iterator
    {
      ~succ_iter_filtered()
      {
	for (first(); !done(); next())
	  it_->dest->destroy();
      }

      void first()
      {
	it_ = trans_.begin();
      }

      virtual void next()
      {
	++it_;
      }

      bool done() const
      {
	return it_ == trans_.end();
      }

      state* current_state() const
      {
	return it_->dest->clone();
      }

      bdd current_condition() const
      {
	return it_->cond;
      }

      bdd current_acceptance_conditions() const
      {
	return it_->acc;
      }

      transitions trans_;
      transitions::const_iterator it_;
    };


    class tgba_mask_keep: public tgba_mask
    {
      const state_set& mask_;
    public:
      tgba_mask_keep(const tgba* masked,
		     const state_set& mask,
		     const state* init)
	: tgba_mask(masked, init),
	  mask_(mask)
      {
      }

      bool wanted(const state* s) const
      {
	state_set::const_iterator i = mask_.find(s);
	return i != mask_.end();
      }
    };

    class tgba_mask_ignore: public tgba_mask
    {
      const state_set& mask_;
    public:
      tgba_mask_ignore(const tgba* masked,
		       const state_set& mask,
		       const state* init)
	: tgba_mask(masked, init),
	  mask_(mask)
      {
      }

      bool wanted(const state* s) const
      {
	state_set::const_iterator i = mask_.find(s);
	return i == mask_.end();
      }
    };


  }


  tgba_mask::tgba_mask(const tgba* masked,
		       const state* init)
    : tgba_proxy(masked),
      init_(init)
  {
    if (!init)
      init_ = masked->get_init_state();
  }

  tgba_mask::~tgba_mask()
  {
    init_->destroy();
  }

  state* tgba_mask::get_init_state() const
  {
    return init_->clone();
  }

  tgba_succ_iterator*
  tgba_mask::succ_iter(const state* local_state,
		       const state* global_state,
		       const tgba* global_automaton) const
  {
    tgba_succ_iterator* it = original_->succ_iter(local_state,
						  global_state,
						  global_automaton);

    succ_iter_filtered* res = new succ_iter_filtered;
    for (it->first(); !it->done(); it->next())
      {
	const state* s = it->current_state();
	if (!wanted(s))
	  {
	    s->destroy();
	    continue;
	  }
	transition t = { s,
			 it->current_condition(),
			 it->current_acceptance_conditions() };
	res->trans_.push_back(t);
      }
    delete it;
    return res;
  }


  const tgba*
  build_tgba_mask_keep(const tgba* to_mask,
		       const state_set& to_keep,
		       const state* init)
  {
    return new tgba_mask_keep(to_mask, to_keep, init);
  }

  const tgba*
  build_tgba_mask_ignore(const tgba* to_mask,
			 const state_set& to_ignore,
			 const state* init)
  {
    return new tgba_mask_ignore(to_mask, to_ignore, init);
  }

}
