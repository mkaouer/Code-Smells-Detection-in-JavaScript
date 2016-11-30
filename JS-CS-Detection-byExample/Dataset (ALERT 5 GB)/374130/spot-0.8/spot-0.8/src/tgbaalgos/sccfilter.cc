// Copyright (C) 2009, 2010, 2011 Laboratoire de Recherche et Developpement
// de l'Epita (LRDE).
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

#include "sccfilter.hh"
#include "tgba/tgbaexplicit.hh"
#include "reachiter.hh"
#include "tgbaalgos/scc.hh"
#include "misc/bddop.hh"
#include <sstream>

namespace spot
{
  namespace
  {
    static
    tgba_explicit::transition*
    create_transition(const tgba* aut, tgba_explicit_string* out_aut,
		      const state* in_s, int in,
		      const state* out_s, int out)
    {
      std::ostringstream in_name;
      in_name << "(#" << in << ") " << aut->format_state(in_s);
      std::ostringstream out_name;
      out_name << "(#" << out << ") " << aut->format_state(out_s);
      return out_aut->create_transition(in_name.str(), out_name.str());
    }

    static
    tgba_explicit::transition*
    create_transition(const tgba* aut, tgba_explicit_formula* out_aut,
		      const state* in_s, int, const state* out_s, int)
    {
      const tgba_explicit_formula* a =
	static_cast<const tgba_explicit_formula*>(aut);
      const ltl::formula* in_f = a->get_label(in_s);
      const ltl::formula* out_f = a->get_label(out_s);
      if (!out_aut->has_state(in_f))
	in_f->clone();
      if ((in_f != out_f) && !out_aut->has_state(out_f))
	out_f->clone();
      return out_aut->create_transition(in_f, out_f);
    }

    template<class T>
    class filter_iter: public tgba_reachable_iterator_depth_first
    {
    public:
      filter_iter(const tgba* a,
		  const scc_map& sm,
		  const std::vector<bool>& useless,
		  bdd useful, bdd strip, bool remove_all_useless)
	: tgba_reachable_iterator_depth_first(a),
	  out_(new T(a->get_dict())),
	  sm_(sm),
	  useless_(useless),
	  useful_(useful),
	  strip_(strip),
	  all_(remove_all_useless)
      {
	out_->set_acceptance_conditions(useful);
      }

      T*
      result()
      {
	return out_;
      }

      bool
      want_state(const state* s) const
      {
	return !useless_[sm_.scc_of_state(s)];
      }

      void
      process_link(const state* in_s, int in,
		   const state* out_s, int out,
		   const tgba_succ_iterator* si)
      {
	tgba_explicit::transition* t =
	  create_transition(this->automata_, out_, in_s, in, out_s, out);
	out_->add_conditions(t, si->current_condition());

	// Regardless of all_, do not output any acceptance condition
	// if the destination is not in an accepting SCC.
	//
	// If all_ is set, do not output any acceptance condition if the
	// source is not in the same SCC as dest.
	//
	// (See the documentation of scc_filter() for a rational.)
	unsigned u = sm_.scc_of_state(out_s);
	if (sm_.accepting(u)
	    && (!all_ || u == sm_.scc_of_state(in_s)))
	  out_->add_acceptance_conditions
	    (t, (bdd_exist(si->current_acceptance_conditions(), strip_)
		 & useful_));
      }

    private:
      T* out_;
      const scc_map& sm_;
      const std::vector<bool>& useless_;
      bdd useful_;
      bdd strip_;
      bool all_;
    };

  } // anonymous


  tgba* scc_filter(const tgba* aut, bool remove_all_useless)
  {
    scc_map sm(aut);
    sm.build_map();
    scc_stats ss = build_scc_stats(sm);

    bdd useful = ss.useful_acc;
    bdd negall = aut->neg_acceptance_conditions();
    // Compute a set of useless acceptance conditions.
    // If the acceptance combinations occurring in
    // the automata are  { a, ab, abc, bd }, then
    // USEFUL contains (a&!b&!c&!d)|(a&b&!c&!d)|(a&b&c&!d)|(!a&b&!c&d)
    // and we want to find that 'a' and 'b' are useless because
    // they always occur with 'c'.
    // The way we check if 'a' is useless that is to look whether
    // USEFUL & (x -> a) == USEFUL for some other acceptance
    // condition x.
    bdd allconds = bdd_support(negall);
    bdd allcondscopy = allconds;
    bdd useless = bddtrue;
    while (allconds != bddtrue)
      {
	bdd a = bdd_ithvar(bdd_var(allconds));
	bdd others = allcondscopy;

	while (others != bddtrue)
	  {
	    bdd x = bdd_ithvar(bdd_var(others));
	    if (x != a)
	      {
		if ((useful & (x >> a)) == useful)
		  {
		    // a is useless
		    useful = bdd_exist(useful, a);
		    useless &= a;
		    allcondscopy = bdd_exist(allcondscopy, a);
		    break;
		  }
	      }
	    others = bdd_high(others);
	  }
	allconds = bdd_high(allconds);
      }

    // We never remove ALL acceptance conditions.
    assert(negall == bddtrue || useless != bdd_support(negall));

    useful = compute_all_acceptance_conditions(bdd_exist(negall, useless));

    // In most cases we will create a tgba_explicit_string copy of the
    // initial tgba, but this is not very space efficient as the
    // labels are built using the "format_state()" string output of
    // the original automaton.  In the case where the source automaton is
    // a tgba_explicit_formula (typically after calling ltl2tgba_fm())
    // we can create another tgba_explicit_formula instead.
    const tgba_explicit_formula* af =
      dynamic_cast<const tgba_explicit_formula*>(aut);
    if (af)
      {
	filter_iter<tgba_explicit_formula> fi(af, sm, ss.useless_scc_map,
					      useful, useless,
					      remove_all_useless);
	fi.run();
	tgba_explicit_formula* res = fi.result();
	res->merge_transitions();
	return res;
      }
    else
      {
	filter_iter<tgba_explicit_string> fi(aut, sm, ss.useless_scc_map,
					     useful, useless,
					     remove_all_useless);
	fi.run();
	tgba_explicit_string* res = fi.result();
	res->merge_transitions();
	return res;
      }
  }

}
