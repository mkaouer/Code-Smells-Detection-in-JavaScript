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

#include <ostream>
#include "save.hh"
#include "reachiter.hh"
#include "misc/bareword.hh"

namespace spot
{
  namespace
  {
    class save_bfs: public evtgba_reachable_iterator_breadth_first
    {
    public:
      save_bfs(const evtgba* a, std::ostream& os)
	: evtgba_reachable_iterator_breadth_first(a), os_(os)
      {
      }

      void
      start(int)
      {
	os_ << "acc =";
	output_acc_set(automata_->all_acceptance_conditions());
	os_ << ";" << std::endl;
	os_ << "init =";
	evtgba_iterator* i = automata_->init_iter();
	for (i->first(); !i->done(); i->next())
	  {
	    const state* s = i->current_state();
	    os_ << " " << quote_unless_bare_word(automata_->format_state(s));
	    delete s;
	  }
	os_ << ";" << std::endl;
	delete i;
      }

      void
      process_state(const state* s, int, evtgba_iterator* si)
      {
	std::string cur = quote_unless_bare_word(automata_->format_state(s));
	for (si->first(); !si->done(); si->next())
	  {
	    const state* dest = si->current_state();
	    os_ << cur << ", "
		<< quote_unless_bare_word(automata_->format_state(dest))
		<< ", "
		<< quote_unless_bare_word(automata_
					  ->format_label(si->current_label()))
		<< ",";
	    output_acc_set(si->current_acceptance_conditions());
	    os_ << ";" << std::endl;
	    delete dest;
	  }
      }

    private:
      std::ostream& os_;

      void
      output_acc_set(const symbol_set& ss) const
      {
	// Store all formated acceptance condition in a set to sort
	// them in the output.
	typedef std::set<std::string> acc_set;
	acc_set acc;
	for (symbol_set::const_iterator i = ss.begin(); i != ss.end(); ++i)
	  acc.insert(automata_->format_acceptance_condition(*i));
	for (acc_set::const_iterator i = acc.begin(); i != acc.end(); ++i)
	  os_ << " " << quote_unless_bare_word(*i);
      }

    };
  }

  std::ostream&
  evtgba_save_reachable(std::ostream& os, const evtgba* g)
  {
    save_bfs b(g, os);
    b.run();
    return os;
  }
}
