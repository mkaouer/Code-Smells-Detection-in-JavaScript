// Copyright (C) 2003, 2004, 2005  Laboratoire d'Informatique de Paris 6 (LIP6),
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
#include "tgba/bddprint.hh"
#include "ltlvisit/tostring.hh"
#include "ltlast/atomic_prop.hh"
#include "reachiter.hh"
#include "misc/escape.hh"

namespace spot
{
  namespace
  {
    class save_bfs: public tgba_reachable_iterator_breadth_first
    {
    public:
      save_bfs(const tgba* a, std::ostream& os)
	: tgba_reachable_iterator_breadth_first(a), os_(os)
      {
      }

      void
      start()
      {
	os_ << "acc =";
	print_acc(automata_->all_acceptance_conditions()) << ";" << std::endl;
      }

      void
      process_state(const state* s, int, tgba_succ_iterator* si)
      {
	const bdd_dict* d = automata_->get_dict();
	std::string cur = automata_->format_state(s);
	for (si->first(); !si->done(); si->next())
	  {
	    state* dest = si->current_state();
	    os_ << "\"" << cur << "\", \""
		<< automata_->format_state(dest) << "\", \"";
	    escape_str(os_, bdd_format_formula(d, si->current_condition()));
	    os_ << "\",";
	    print_acc(si->current_acceptance_conditions()) << ";" << std::endl;
	    delete dest;
	  }
      }

    private:
      std::ostream& os_;

      std::ostream&
      print_acc(bdd acc)
      {
	const bdd_dict* d = automata_->get_dict();
	while (acc != bddfalse)
	  {
	    bdd cube = bdd_satone(acc);
	    acc -= cube;
	    while (cube != bddtrue)
	      {
		assert(cube != bddfalse);
		// Display the first variable that is positive.
		// There should be only one per satisfaction.
		if (bdd_high(cube) != bddfalse)
		  {
		    int v = bdd_var(cube);
		    bdd_dict::vf_map::const_iterator vi =
		      d->acc_formula_map.find(v);
		    assert(vi != d->acc_formula_map.end());
		    std::string s = ltl::to_string(vi->second);
		    if (dynamic_cast<const ltl::atomic_prop*>(vi->second)
			&& s[0] == '"')
		      {
			// Unquote atomic propositions.
			s.erase(s.begin());
			s.resize(s.size() - 1);
		      }
		    os_ << " \"";
		    escape_str(os_, s) << "\"";
		    break;
		  }
		cube = bdd_low(cube);
	      }
	  }
	return os_;
      }
    };
  }

  std::ostream&
  tgba_save_reachable(std::ostream& os, const tgba* g)
  {
    save_bfs b(g, os);
    b.run();
    return os;
  }
}
