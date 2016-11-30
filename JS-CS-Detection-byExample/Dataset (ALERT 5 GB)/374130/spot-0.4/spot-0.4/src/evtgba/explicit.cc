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

#include "explicit.hh"
#include "misc/bareword.hh"
#include "misc/escape.hh"

namespace spot
{
  const evtgba_explicit::state*
  state_evtgba_explicit::get_state() const
  {
    return state_;
  }

  int
  state_evtgba_explicit::compare(const spot::state* other) const
  {
    const state_evtgba_explicit* o =
      dynamic_cast<const state_evtgba_explicit*>(other);
    assert(o);
    return o->get_state() - get_state();
  }

  size_t
  state_evtgba_explicit::hash() const
  {
    return
      reinterpret_cast<const char*>(get_state()) - static_cast<const char*>(0);
  }

  state_evtgba_explicit*
  state_evtgba_explicit::clone() const
  {
    return new state_evtgba_explicit(*this);
  }

  namespace
  {
    class evtgba_explicit_iterator: public evtgba_iterator
    {
    public:
      evtgba_explicit_iterator(const evtgba_explicit::transition_list* s)
	: s_(s), i_(s_->end())
      {
      }

      virtual
      ~evtgba_explicit_iterator()
      {
      }

      virtual void first()
      {
	i_ = s_->begin();
      }

      virtual void
      next()
      {
	++i_;
      }

      virtual bool
      done() const
      {
	return i_ == s_->end();
      }

      virtual const symbol*
      current_label() const
      {
	return (*i_)->label;
      }

      virtual symbol_set
      current_acceptance_conditions() const
      {
	return (*i_)->acceptance_conditions;
      }
    protected:
      const evtgba_explicit::transition_list* s_;
      evtgba_explicit::transition_list::const_iterator i_;
    };



    class evtgba_explicit_iterator_fw: public evtgba_explicit_iterator
    {
    public:
      evtgba_explicit_iterator_fw(const evtgba_explicit::transition_list* s)
	: evtgba_explicit_iterator(s)
      {
      }

      virtual
      ~evtgba_explicit_iterator_fw()
      {
      }

      const state*
      current_state() const
      {
	return new state_evtgba_explicit((*i_)->out);
      }
    };

    class evtgba_explicit_iterator_bw: public evtgba_explicit_iterator
    {
    public:
      evtgba_explicit_iterator_bw(const evtgba_explicit::transition_list* s)
	: evtgba_explicit_iterator(s)
      {
      }

      virtual
      ~evtgba_explicit_iterator_bw()
      {
      }

      const state*
      current_state() const
      {
	return new state_evtgba_explicit((*i_)->in);
      }
    };
  }

  evtgba_explicit::evtgba_explicit()
  {
  }

  evtgba_explicit::~evtgba_explicit()
  {
    for (transition_list::const_iterator i = init_states_.begin();
	 i != init_states_.end(); ++i)
      delete *i;
    for (ns_map::const_iterator j = name_state_map_.begin();
	 j != name_state_map_.end(); ++j)
      {
	for (transition_list::const_iterator i = j->second->out.begin();
	     i != j->second->out.end(); ++i)
	  delete *i;
	delete j->second;
      }
    for (symbol_set::const_iterator i = alphabet_.begin();
	 i != alphabet_.end(); ++i)
      (*i)->unref();
    for (symbol_set::const_iterator i = acc_set_.begin();
	 i != acc_set_.end(); ++i)
      (*i)->unref();
  }

  evtgba_iterator*
  evtgba_explicit::init_iter() const
  {
    return new evtgba_explicit_iterator_fw(&init_states_);
  }

  evtgba_iterator*
  evtgba_explicit::succ_iter(const spot::state* s) const
  {
    const state_evtgba_explicit* u =
      dynamic_cast<const state_evtgba_explicit*>(s);
    assert(u);
    return new evtgba_explicit_iterator_fw(&u->get_state()->out);
  }

  evtgba_iterator*
  evtgba_explicit::pred_iter(const spot::state* s) const
  {
    const state_evtgba_explicit* u =
      dynamic_cast<const state_evtgba_explicit*>(s);
    assert(u);
    return new evtgba_explicit_iterator_fw(&u->get_state()->in);
  }

  std::string
  evtgba_explicit::format_state(const spot::state* s) const
  {
    const state_evtgba_explicit* u =
      dynamic_cast<const state_evtgba_explicit*>(s);
    assert(u);
    sn_map::const_iterator i = state_name_map_.find(u->get_state());
    assert(i != state_name_map_.end());
    return i->second;
  }

  const symbol_set&
  evtgba_explicit::all_acceptance_conditions() const
  {
    return acc_set_;
  }

  const symbol_set&
  evtgba_explicit::alphabet() const
  {
    return alphabet_;
  }

  evtgba_explicit::state*
  evtgba_explicit::declare_state(const std::string& name)
  {
    ns_map::const_iterator i = name_state_map_.find(name);
    if (i != name_state_map_.end())
      return i->second;
    state* s = new state;
    name_state_map_[name] = s;
    state_name_map_[s] = name;
    return s;
  }

  evtgba_explicit::transition*
  evtgba_explicit::add_transition(const std::string& source,
				  const rsymbol& label,
				  rsymbol_set acc,
				  const std::string& dest)
  {
    state* in = declare_state(source);
    state* out = declare_state(dest);
    transition* t = new transition;
    t->in = in;
    t->label = label;
    t->out = out;
    in->out.push_back(t);
    out->in.push_back(t);

    for (rsymbol_set::const_iterator i = acc.begin(); i != acc.end(); ++i)
      {
	const symbol* s = *i;
	t->acceptance_conditions.insert(s);
	declare_acceptance_condition(*i);
      }

    if (alphabet_.find(t->label) == alphabet_.end())
      {
	alphabet_.insert(t->label);
	t->label->ref();
      }

    return t;
  }

  void
  evtgba_explicit::declare_acceptance_condition(const rsymbol& acc)
  {
    const symbol* s = acc;
    if (acc_set_.find(s) == acc_set_.end())
      {
	acc_set_.insert(s);
	s->ref();
      }
  }


  void
  evtgba_explicit::set_init_state(const std::string& name)
  {
    transition* t = new transition;
    t->in = 0;
    t->out = declare_state(name);
    t->label = 0;
    init_states_.push_back(t);
  }

}
