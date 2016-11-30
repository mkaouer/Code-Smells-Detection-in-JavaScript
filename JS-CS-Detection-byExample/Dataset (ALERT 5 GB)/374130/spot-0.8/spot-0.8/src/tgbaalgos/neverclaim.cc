// Copyright (C) 2009, 2011 Laboratoire de Recherche et Développement
// de l'Epita (LRDE).
// Copyright (C) 2004 Laboratoire d'Informatique de Paris 6 (LIP6),
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

#include <ostream>
#include <sstream>
#include "bdd.h"
#include "tgba/tgbatba.hh"
#include "neverclaim.hh"
#include "tgba/bddprint.hh"
#include "reachiter.hh"
#include "ltlvisit/tostring.hh"
#include "tgba/formula2bdd.hh"

namespace spot
{
  namespace
  {
    class never_claim_bfs : public tgba_reachable_iterator_breadth_first
    {
    public:
      never_claim_bfs(const tgba* a, std::ostream& os,
		      const ltl::formula* f, bool comments)
	: tgba_reachable_iterator_breadth_first(a),
	  os_(os), f_(f), accept_all_(-1), fi_needed_(false),
	  comments_(comments), all_acc_conds_(a->all_acceptance_conditions()),
	  degen_(dynamic_cast<const tgba_sba_proxy*>(a))
      {
      }

      void
      start()
      {
	os_ << "never {";
	if (f_)
	  {
	    os_ << " /* ";
	    to_string(f_, os_);
	    os_ << " */";
	  }
	os_ << '\n';
	init_ = automata_->get_init_state();
      }

      void
      end()
      {
	if (fi_needed_)
	  os_ << "  fi;\n";
	if (accept_all_ != -1)
	  os_ << "accept_all:\n  skip\n";
	os_ << "}" << std::endl;
	init_->destroy();
      }

      bool
      state_is_accepting(const state *s)
      {
	// If the automaton is degeneralized on-the-fly,
	// it's easier to just query the state_is_accepting() method.
	if (degen_)
	  return degen_->state_is_accepting(s);

	// Otherwise, since we are dealing with a degeneralized
	// automaton nonetheless, the transitions leaving an accepting
	// state are either all accepting, or all non-accepting.  So
	// we just check the acceptance of the first transition.  This
	// is not terribly efficient since we have to create the
	// iterator.
	tgba_succ_iterator* it = automata_->succ_iter(s);
	it->first();
	bool accepting =
	  !it->done() && it->current_acceptance_conditions() == all_acc_conds_;
	delete it;
	return accepting;
      }

      std::string
      get_state_label(const state* s, int n)
      {
	std::string label;
	if (s->compare(init_) == 0)
	  if (state_is_accepting(s))
	    label = "accept_init";
	  else
	    label = "T0_init";
	else
	  {
	    std::ostringstream ost;
	    ost << n;
	    std::string ns(ost.str());

	    if (state_is_accepting(s))
	      {
		tgba_succ_iterator* it = automata_->succ_iter(s);
		it->first();
		if (it->done())
		  label = "accept_S" + ns;
		else
		  {
		    state* current = it->current_state();
		    if (it->current_condition() != bddtrue
			|| s->compare(current) != 0)
		      label = "accept_S" + ns;
		    else
		      label = "accept_all";
		    current->destroy();
		  }
		delete it;
	      }
	    else
	      label = "T0_S" + ns;
	  }
	  return label;
      }

      void
      process_state(const state* s, int n, tgba_succ_iterator*)
      {
	tgba_succ_iterator* it = automata_->succ_iter(s);
	it->first();
	if (it->done())
	  {
	    if (fi_needed_ != 0)
	      os_ << "  fi;\n";
	    os_ << get_state_label(s, n) << ":";
	    if (comments_)
	      os_ << " /* " << automata_->format_state(s) << " */";
	    os_ << "\n  if\n  :: (0) -> goto "
		<< get_state_label(s, n) << '\n';
	    fi_needed_ = true;
	  }
	else
	  {
	    state* current =it->current_state();
	    if (state_is_accepting(s)
		&& it->current_condition() == bddtrue
		&& s->compare(init_) != 0
		&& s->compare(current) == 0)
	      accept_all_ = n;
	    else
	      {
		if (fi_needed_)
		  os_ << "  fi;\n";
		os_ << get_state_label(s, n) << ":";
		if (comments_)
		  os_ << " /* " << automata_->format_state(s) << " */";
		os_ << "\n  if\n";
		fi_needed_ = true;
	      }
	    current->destroy();
	  }
	  delete it;
       }

      void
      process_link(const state*, int in, const state*, int out,
		   const tgba_succ_iterator* si)
      {
	if (in != accept_all_)
	  {
	    os_ << "  :: (";
	    const ltl::formula* f = bdd_to_formula(si->current_condition(),
						   automata_->get_dict());
	    to_spin_string(f, os_, true);
	    f->destroy();
	    state* current = si->current_state();
	    os_ << ") -> goto " << get_state_label(current, out) << '\n';
	    current->destroy();
	  }
      }

    private:
      std::ostream& os_;
      const ltl::formula* f_;
      int accept_all_;
      bool fi_needed_;
      state* init_;
      bool comments_;
      bdd all_acc_conds_;
      const tgba_sba_proxy* degen_;
    };
  } // anonymous

  std::ostream&
  never_claim_reachable(std::ostream& os, const tgba* g,
			const ltl::formula* f, bool comments)
  {
    assert(g->number_of_acceptance_conditions() <= 1);
    never_claim_bfs n(g, os, f, comments);
    n.run();
    return os;
  }
}
