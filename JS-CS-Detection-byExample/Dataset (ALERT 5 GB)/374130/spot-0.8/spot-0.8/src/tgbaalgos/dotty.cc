// Copyright (C) 2011 Laboratoire de Recherche et Developpement de
// l'Epita (LRDE).
// Copyright (C) 2003, 2004  Laboratoire d'Informatique de Paris 6 (LIP6),
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
#include "tgba/tgba.hh"
#include "dotty.hh"
#include "tgba/bddprint.hh"
#include "reachiter.hh"
#include "misc/escape.hh"
#include "tgba/tgbatba.hh"

namespace spot
{
  namespace
  {
    class dotty_bfs : public tgba_reachable_iterator_breadth_first
    {
    public:
      dotty_bfs(std::ostream& os, const tgba* a, bool mark_accepting_states,
		dotty_decorator* dd)
	: tgba_reachable_iterator_breadth_first(a), os_(os),
	  mark_accepting_states_(mark_accepting_states), dd_(dd),
	  sba_(dynamic_cast<const tgba_sba_proxy*>(a))
      {
      }

      void
      start()
      {
	os_ << ("digraph G {\n"
		"  0 [label=\"\", style=invis, height=0]\n"
		"  0 -> 1\n");
      }

      void
      end()
      {
	os_ << "}" << std::endl;
      }

      void
      process_state(const state* s, int n, tgba_succ_iterator* si)
      {
	bool accepting;

	if (mark_accepting_states_)
	  {
	    if (sba_)
	      {
		accepting = sba_->state_is_accepting(s);
	      }
	    else
	      {
		si->first();
		accepting = ((!si->done())
			     && (si->current_acceptance_conditions() ==
				 automata_->all_acceptance_conditions()));
	      }
	  }
	else
	  {
	    accepting = false;
	  }

	os_ << "  " << n << " "
	    << dd_->state_decl(automata_, s, n, si,
			       escape_str(automata_->format_state(s)),
			       accepting)
	    << '\n';
      }

      void
      process_link(const state* in_s, int in,
		   const state* out_s, int out, const tgba_succ_iterator* si)
      {
	std::string label =
	  bdd_format_formula(automata_->get_dict(),
			     si->current_condition())
	  + "\n"
	  + bdd_format_accset(automata_->get_dict(),
			      si->current_acceptance_conditions());

	std::string s = automata_->transition_annotation(si);
	if (!s.empty())
	  {
	    if (*label.rbegin() != '\n')
	      label += '\n';
	    label += s;
	  }

	os_ << "  " << in << " -> " << out << " "
	    << dd_->link_decl(automata_, in_s, in, out_s, out, si,
			      escape_str(label))
	    << '\n';
      }

    private:
      std::ostream& os_;
      bool mark_accepting_states_;
      dotty_decorator* dd_;
      const tgba_sba_proxy* sba_;
    };
  }

  std::ostream&
  dotty_reachable(std::ostream& os, const tgba* g,
		  bool assume_sba, dotty_decorator* dd)
  {
    dotty_bfs d(os, g, assume_sba, dd);
    d.run();
    return os;
  }


}
