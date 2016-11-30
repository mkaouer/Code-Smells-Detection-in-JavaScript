// Copyright (C) 2010, 2012 Laboratoire de Recherche et Developpement
// de l Epita (LRDE).
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

#include <ostream>
#include "dotty.hh"
#include "tgba/bddprint.hh"
#include "reachiter.hh"
#include "misc/escape.hh"
#include "misc/bareword.hh"

namespace spot
{
  namespace
  {
    class dotty_bfs : public ta_reachable_iterator_breadth_first
    {
    public:
      dotty_bfs(std::ostream& os, const ta* a) :
        ta_reachable_iterator_breadth_first(a), os_(os)
      {
      }

      void
      start()
      {
        os_ << "digraph G {" << std::endl;

        int n = 0;

        artificial_initial_state_ = t_automata_->get_artificial_initial_state();

        ta::states_set_t init_states_set;

        ta::states_set_t::const_iterator it;

        if (artificial_initial_state_)
	  {
	    init_states_set.insert(artificial_initial_state_);
	    os_ << "  0 [label=\"\", style=invis, height=0]\n  0 -> 1\n";
	  }
        else
	  {
	    init_states_set = t_automata_->get_initial_states_set();

	    for (it = init_states_set.begin();
		 it != init_states_set.end(); it++)
	      {
		bdd init_condition = t_automata_->get_state_condition(*it);
		std::string label = bdd_format_formula(t_automata_->get_dict(),
						       init_condition);
		++n;
		os_ << "  " << -n << "  [label=\"\", style=invis, height=0]\n  "
		    << -n << " -> " << n << " [label=\"" << label << "\"]\n";
	      }
	  }
      }

      void
      end()
      {
        os_ << "}" << std::endl;
      }

      void
      process_state(const state* s, int n)
      {

        std::string style;
        if (t_automata_->is_accepting_state(s))
	  style = ",peripheries=2";

	if (t_automata_->is_livelock_accepting_state(s))
	  style += ",shape=box";

	os_ << "  " << n << " [label=";
	if (s == artificial_initial_state_)
	  os_ << "init";
	else
	  os_ << quote_unless_bare_word(t_automata_->format_state(s));
	os_ << style << "]\n";
      }

      void
      process_link(int in, int out, const ta_succ_iterator* si)
      {
	bdd_dict* d = t_automata_->get_dict();
	std::string label =
	  ((in == 1 && artificial_initial_state_)
	   ? bdd_format_formula(d, si->current_condition())
	   : bdd_format_accset(d, si->current_condition()));

	if (label.empty())
	  label = "{}";

	label += ("\n" +
		  bdd_format_accset(d, si->current_acceptance_conditions()));


        os_ << "  " << in << " -> " << out << " [label=\"";
	escape_str(os_, label);
	os_ << "\"]\n";
      }

    private:
      std::ostream& os_;
      spot::state* artificial_initial_state_;
    };

  }

  std::ostream&
  dotty_reachable(std::ostream& os, const ta* a)
  {
    dotty_bfs d(os, a);
    d.run();
    return os;
  }

}
