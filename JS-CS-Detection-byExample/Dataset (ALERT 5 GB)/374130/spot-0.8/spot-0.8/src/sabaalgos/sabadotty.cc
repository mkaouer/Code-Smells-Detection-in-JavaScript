// Copyright (C) 2009 Laboratoire de Recherche et Développement
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

#include <ostream>
#include "saba/saba.hh"
#include "sabadotty.hh"
#include "tgba/bddprint.hh"
#include "sabareachiter.hh"
#include "misc/escape.hh"

namespace spot
{
  namespace
  {
    class saba_dotty_bfs : public saba_reachable_iterator_breadth_first
    {
    public:
      saba_dotty_bfs(std::ostream& os, const saba* a)
	: saba_reachable_iterator_breadth_first(a), os_(os)
      {
      }

      void
      start()
      {
	os_ << "digraph G {" << std::endl;
	os_ << "  0 [label=\"\", style=invis, height=0]" << std::endl;
	os_ << "  0 -> 1;" << std::endl;
      }

      void
      end()
      {
	os_ << "}" << std::endl;
      }

      void
      process_state(const saba_state* s, int n)
      {
        std::string label =
          escape_str(automata_->format_state(s))
          + "\\n"
          + bdd_format_accset(automata_->get_dict(),
			      s->acceptance_conditions());
	os_ << "  " << n << " "
	    << "[label=\"" << label << "\"];"
	    << std::endl;
      }

      void
      process_state_conjunction(const saba_state*, int in,
                                const saba_state_conjunction*,
                                int sc_id,
                                const saba_succ_iterator* si)
      {
        os_ << "  s" << in << "sc" << sc_id
            << " [label=\"\", style=invis, height=0];" << std::endl;

	std::string label =
	  bdd_format_formula(automata_->get_dict(),
			     si->current_condition());

	os_ << "  " << in << " -> s" << in << "sc" << sc_id
	    << " [label=\"" << label << "\", arrowhead=\"none\"];"
	    << std::endl;
      }

      void
      process_link(const saba_state*, int in,
		   const saba_state*, int out,
                   const saba_state_conjunction*,
                   int sc_id,
                   const saba_succ_iterator*)
      {
        os_ << "  s" << in << "sc" << sc_id << " -> " << out
	    << " [label=\"\"];"
	    << std::endl;
      }

    private:
      std::ostream& os_;
    };
  }

  std::ostream&
  saba_dotty_reachable(std::ostream& os, const saba* g)
  {
    saba_dotty_bfs d(os, g);
    d.run();
    return os;
  }

}
