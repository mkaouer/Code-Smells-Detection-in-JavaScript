// -*- coding: utf-8 -*-
// Copyright (C) 2009, 2011, 2012 Laboratoire de Recherche et
// Développement de l'Epita (LRDE).
// Copyright (C) 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include "tgba2evtgba.hh"
#include "tgba/tgba.hh"
#include "evtgba/explicit.hh"
#include "tgbaalgos/reachiter.hh"
#include "ltlvisit/tostring.hh"

namespace spot
{
  namespace
  {
    class tgba_to_evtgba_iter:
      public tgba_reachable_iterator_depth_first
    {
    public:
      tgba_to_evtgba_iter(const tgba* a)
	: tgba_reachable_iterator_depth_first(a)
      {
	res = new evtgba_explicit;
      }

      ~tgba_to_evtgba_iter()
      {
      }

      virtual void
      start()
      {
	const rsymbol_set ss =
	  acc_to_symbol_set(aut_->all_acceptance_conditions());
	for (rsymbol_set::const_iterator i = ss.begin(); i != ss.end(); ++i)
	  res->declare_acceptance_condition(*i);
      }

      virtual void
      process_state(const state* s, int n, tgba_succ_iterator*)
      {
	std::string str = this->aut_->format_state(s);
	name_[n] = str;
	if (n == 1)
	  res->set_init_state(str);
      }

      virtual void
      process_link(const state*, int in,
		   const state*, int out,
		   const tgba_succ_iterator* si)
      {
	// We might need to format out before process_state is called.
	name_map_::const_iterator i = name_.find(out);
	if (i == name_.end())
	  {
	    const state* s = si->current_state();
	    process_state(s, out, 0);
	    s->destroy();
	  }

	rsymbol_set ss = acc_to_symbol_set(si->current_acceptance_conditions());

	bdd all = si->current_condition();
	while (all != bddfalse)
	  {
	    bdd one = bdd_satone(all);
	    all -= one;
	    while (one != bddfalse)
	      {
		bdd low = bdd_low(one);
		if (low == bddfalse)
		  {
		    const ltl::formula* v =
		      aut_->get_dict()->bdd_map[bdd_var(one)].f;
		    res->add_transition(name_[in],
					to_string(v),
					ss,
					name_[out]);
		    break;
		  }
		else
		  {
		    one = low;
		  }
	      }
	    assert(one != bddfalse);
	  }
      }

      evtgba_explicit* res;
    protected:
      typedef std::map<int, std::string> name_map_;
      name_map_ name_;

      rsymbol_set
      acc_to_symbol_set(bdd acc) const
      {
	rsymbol_set ss;
	while (acc != bddfalse)
	  {
	    bdd one = bdd_satone(acc);
	    acc -= one;
	    while (one != bddfalse)
	      {
		bdd low = bdd_low(one);
		if (low == bddfalse)
		  {
		    const ltl::formula* v =
		      aut_->get_dict()->bdd_map[bdd_var(one)].f;
		    ss.insert(rsymbol(to_string(v)));
		    break;
		  }
		else
		  {
		    one = low;
		  }
	      }
	    assert(one != bddfalse);
	  }
	return ss;
      }

    };

  } // anonymous

  evtgba_explicit*
  tgba_to_evtgba(const tgba* a)
  {
    tgba_to_evtgba_iter i(a);
    i.run();
    return i.res;
  }
} // spot
